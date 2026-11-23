package org.spark_project.jetty.client;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.component.Destroyable;

public class GZIPContentDecoder implements ContentDecoder, Destroyable {
   private final Inflater inflater;
   private final byte[] bytes;
   private byte[] output;
   private State state;
   private int size;
   private int value;
   private byte flags;

   public GZIPContentDecoder() {
      this(2048);
   }

   public GZIPContentDecoder(int bufferSize) {
      this.inflater = new Inflater(true);
      this.bytes = new byte[bufferSize];
      this.reset();
   }

   public ByteBuffer decode(ByteBuffer buffer) {
      try {
         label125:
         while(buffer.hasRemaining()) {
            byte currByte = buffer.get();
            switch (this.state) {
               case INITIAL:
                  buffer.position(buffer.position() - 1);
                  this.state = GZIPContentDecoder.State.ID;
                  break;
               case ID:
                  this.value += (currByte & 255) << 8 * this.size;
                  ++this.size;
                  if (this.size != 2) {
                     break;
                  }

                  if (this.value != 35615) {
                     throw new ZipException("Invalid gzip bytes");
                  }

                  this.state = GZIPContentDecoder.State.CM;
                  break;
               case CM:
                  if ((currByte & 255) != 8) {
                     throw new ZipException("Invalid gzip compression method");
                  }

                  this.state = GZIPContentDecoder.State.FLG;
                  break;
               case FLG:
                  this.flags = currByte;
                  this.state = GZIPContentDecoder.State.MTIME;
                  this.size = 0;
                  this.value = 0;
                  break;
               case MTIME:
                  ++this.size;
                  if (this.size == 4) {
                     this.state = GZIPContentDecoder.State.XFL;
                  }
                  break;
               case XFL:
                  this.state = GZIPContentDecoder.State.OS;
                  break;
               case OS:
                  this.state = GZIPContentDecoder.State.FLAGS;
                  break;
               case FLAGS:
                  buffer.position(buffer.position() - 1);
                  if ((this.flags & 4) == 4) {
                     this.state = GZIPContentDecoder.State.EXTRA_LENGTH;
                     this.size = 0;
                     this.value = 0;
                  } else if ((this.flags & 8) == 8) {
                     this.state = GZIPContentDecoder.State.NAME;
                  } else if ((this.flags & 16) == 16) {
                     this.state = GZIPContentDecoder.State.COMMENT;
                  } else {
                     if ((this.flags & 2) == 2) {
                        this.state = GZIPContentDecoder.State.HCRC;
                        this.size = 0;
                        this.value = 0;
                        continue;
                     }

                     this.state = GZIPContentDecoder.State.DATA;
                  }
                  break;
               case EXTRA_LENGTH:
                  this.value += (currByte & 255) << 8 * this.size;
                  ++this.size;
                  if (this.size == 2) {
                     this.state = GZIPContentDecoder.State.EXTRA;
                  }
                  break;
               case EXTRA:
                  --this.value;
                  if (this.value == 0) {
                     this.flags &= -5;
                     this.state = GZIPContentDecoder.State.FLAGS;
                  }
                  break;
               case NAME:
                  if (currByte == 0) {
                     this.flags &= -9;
                     this.state = GZIPContentDecoder.State.FLAGS;
                  }
                  break;
               case COMMENT:
                  if (currByte == 0) {
                     this.flags &= -17;
                     this.state = GZIPContentDecoder.State.FLAGS;
                  }
                  break;
               case HCRC:
                  ++this.size;
                  if (this.size == 2) {
                     this.flags &= -3;
                     this.state = GZIPContentDecoder.State.FLAGS;
                  }
                  break;
               case DATA:
                  buffer.position(buffer.position() - 1);

                  while(true) {
                     int decoded = this.inflate(this.bytes);
                     if (decoded == 0) {
                        if (!this.inflater.needsInput()) {
                           if (this.inflater.finished()) {
                              int remaining = this.inflater.getRemaining();
                              buffer.position(buffer.limit() - remaining);
                              this.state = GZIPContentDecoder.State.CRC;
                              this.size = 0;
                              this.value = 0;
                              continue label125;
                           }

                           throw new ZipException("Invalid inflater state");
                        }

                        if (!buffer.hasRemaining()) {
                           if (this.output == null) {
                              continue label125;
                           }

                           ByteBuffer result = ByteBuffer.wrap(this.output);
                           this.output = null;
                           return result;
                        }

                        byte[] input = new byte[buffer.remaining()];
                        buffer.get(input);
                        this.inflater.setInput(input);
                     } else if (this.output == null) {
                        this.output = Arrays.copyOf(this.bytes, decoded);
                     } else {
                        byte[] newOutput = Arrays.copyOf(this.output, this.output.length + decoded);
                        System.arraycopy(this.bytes, 0, newOutput, this.output.length, decoded);
                        this.output = newOutput;
                     }
                  }
               case CRC:
                  this.value += (currByte & 255) << 8 * this.size;
                  ++this.size;
                  if (this.size == 4) {
                     this.state = GZIPContentDecoder.State.ISIZE;
                     this.size = 0;
                     this.value = 0;
                  }
                  break;
               case ISIZE:
                  this.value += (currByte & 255) << 8 * this.size;
                  ++this.size;
                  if (this.size != 4) {
                     break;
                  }

                  if ((long)this.value != this.inflater.getBytesWritten()) {
                     throw new ZipException("Invalid input size");
                  }

                  ByteBuffer result = this.output == null ? BufferUtil.EMPTY_BUFFER : ByteBuffer.wrap(this.output);
                  this.reset();
                  return result;
               default:
                  throw new ZipException();
            }
         }

         return BufferUtil.EMPTY_BUFFER;
      } catch (ZipException x) {
         throw new RuntimeException(x);
      }
   }

   private int inflate(byte[] bytes) throws ZipException {
      try {
         return this.inflater.inflate(bytes);
      } catch (DataFormatException x) {
         throw new ZipException(x.getMessage());
      }
   }

   private void reset() {
      this.inflater.reset();
      Arrays.fill(this.bytes, (byte)0);
      this.output = null;
      this.state = GZIPContentDecoder.State.INITIAL;
      this.size = 0;
      this.value = 0;
      this.flags = 0;
   }

   public void destroy() {
      this.inflater.end();
   }

   protected boolean isFinished() {
      return this.state == GZIPContentDecoder.State.INITIAL;
   }

   public static class Factory extends ContentDecoder.Factory {
      private final int bufferSize;

      public Factory() {
         this(2048);
      }

      public Factory(int bufferSize) {
         super("gzip");
         this.bufferSize = bufferSize;
      }

      public ContentDecoder newContentDecoder() {
         return new GZIPContentDecoder(this.bufferSize);
      }
   }

   private static enum State {
      INITIAL,
      ID,
      CM,
      FLG,
      MTIME,
      XFL,
      OS,
      FLAGS,
      EXTRA_LENGTH,
      EXTRA,
      NAME,
      COMMENT,
      HCRC,
      DATA,
      CRC,
      ISIZE;
   }
}
