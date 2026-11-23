package org.apache.manifoldcf.core.interfaces;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class TempFileInput extends BinaryInput {
   public static final String _rcsid = "@(#)$Id: TempFileInput.java 988245 2010-08-23 18:39:35Z kwright $";
   protected File file;
   protected byte[] inMemoryBuffer;
   protected static final int CHUNK_SIZE = 65536;
   protected static final int DEFAULT_MAX_MEM_SIZE = 8192;

   public TempFileInput(InputStream is) throws ManifoldCFException, IOException {
      this(is, -1L);
   }

   public TempFileInput(InputStream is, long length) throws ManifoldCFException, IOException {
      this(is, length, 8192);
   }

   public TempFileInput(InputStream is, long length, int maxMemSize) throws ManifoldCFException, IOException {
      int chunkSize = 65536;
      byte[] buffer = new byte[chunkSize];
      int chunkTotal = 0;
      boolean eofSeen = false;

      while(true) {
         int chunkAmount;
         if (length != -1L && length <= (long)chunkSize) {
            chunkAmount = (int)(length - (long)chunkTotal);
            eofSeen = true;
         } else {
            chunkAmount = chunkSize - chunkTotal;
         }

         if (chunkAmount == 0) {
            break;
         }

         int readsize = is.read(buffer, chunkTotal, chunkAmount);
         if (readsize == -1) {
            eofSeen = true;
            break;
         }

         chunkTotal += readsize;
      }

      if (eofSeen && chunkTotal < maxMemSize) {
         this.file = null;
         this.inMemoryBuffer = new byte[chunkTotal];

         for(int i = 0; i < this.inMemoryBuffer.length; ++i) {
            this.inMemoryBuffer[i] = buffer[i];
         }

         this.length = (long)chunkTotal;
      } else {
         this.inMemoryBuffer = null;

         File outfile;
         try {
            outfile = File.createTempFile("_MC_", "");
         } catch (IOException e) {
            handleIOException(e, "creating backing file");
            outfile = null;
         }

         try {
            ManifoldCF.addFile(outfile);

            FileOutputStream outStream;
            try {
               outStream = new FileOutputStream(outfile);
            } catch (IOException e) {
               handleIOException(e, "opening backing file");
               outStream = null;
            }

            try {
               long totalMoved = 0L;

               try {
                  outStream.write(buffer, 0, chunkTotal);
               } catch (IOException e) {
                  handleIOException(e, "writing backing file");
               }

               totalMoved += (long)chunkTotal;

               while(true) {
                  int moveAmount;
                  if (length != -1L && length - totalMoved <= (long)chunkSize) {
                     moveAmount = (int)(length - totalMoved);
                  } else {
                     moveAmount = chunkSize;
                  }

                  if (moveAmount == 0) {
                     break;
                  }

                  int readsize = is.read(buffer, 0, moveAmount);
                  if (readsize == -1) {
                     break;
                  }

                  try {
                     outStream.write(buffer, 0, readsize);
                  } catch (IOException e) {
                     handleIOException(e, "writing backing file");
                  }

                  totalMoved += (long)readsize;
               }
            } finally {
               try {
                  outStream.close();
               } catch (IOException e) {
                  handleIOException(e, "closing backing file");
               }

            }

            this.file = outfile;
            this.length = this.file.length();
         } catch (Throwable e) {
            ManifoldCF.deleteFile(outfile);
            if (e instanceof Error) {
               throw (Error)e;
            }

            if (e instanceof RuntimeException) {
               throw (RuntimeException)e;
            }

            if (e instanceof ManifoldCFException) {
               throw (ManifoldCFException)e;
            }

            if (e instanceof IOException) {
               throw (IOException)e;
            }

            throw new RuntimeException("Unexpected throwable of type " + e.getClass().getName() + ": " + e.getMessage(), e);
         }
      }

   }

   public TempFileInput(File tempFile) {
      this.inMemoryBuffer = null;
      this.file = tempFile;
      ManifoldCF.addFile(this.file);
   }

   protected TempFileInput() {
   }

   public BinaryInput transfer() {
      TempFileInput rval = new TempFileInput();
      rval.file = this.file;
      rval.inMemoryBuffer = this.inMemoryBuffer;
      rval.stream = this.stream;
      rval.length = this.length;
      this.file = null;
      this.inMemoryBuffer = null;
      this.stream = null;
      this.length = -1L;
      return rval;
   }

   public void discard() throws ManifoldCFException {
      super.discard();
      if (this.file != null) {
         ManifoldCF.deleteFile(this.file);
         this.file = null;
      }

   }

   protected void openStream() throws ManifoldCFException {
      if (this.file != null) {
         try {
            this.stream = new FileInputStream(this.file);
         } catch (FileNotFoundException e) {
            throw new ManifoldCFException("Can't create stream: " + e.getMessage(), e, 0);
         }
      } else if (this.inMemoryBuffer != null) {
         this.stream = new ByteArrayInputStream(this.inMemoryBuffer);
      }

   }

   protected void calculateLength() throws ManifoldCFException {
      if (this.file != null) {
         this.length = this.file.length();
      } else if (this.inMemoryBuffer != null) {
         this.length = (long)this.inMemoryBuffer.length;
      }

   }
}
