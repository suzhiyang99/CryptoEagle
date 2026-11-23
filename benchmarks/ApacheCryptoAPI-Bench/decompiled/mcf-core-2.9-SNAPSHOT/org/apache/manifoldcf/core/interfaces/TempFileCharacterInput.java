package org.apache.manifoldcf.core.interfaces;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class TempFileCharacterInput extends CharacterInput {
   public static final String _rcsid = "@(#)$Id: TempFileCharacterInput.java 988245 2010-08-23 18:39:35Z kwright $";
   protected File file;
   protected byte[] inMemoryBuffer;
   protected static final int CHUNK_SIZE = 65536;
   protected static final int DEFAULT_MAX_MEM_SIZE = 8192;

   public TempFileCharacterInput(Reader is) throws ManifoldCFException, IOException {
      this(is, -1L);
   }

   public TempFileCharacterInput(Reader is, long length) throws ManifoldCFException, IOException {
      this(is, length, 8192);
   }

   public TempFileCharacterInput(Reader is, long length, int maxInMemoryLength) throws ManifoldCFException, IOException {
      int chunkSize = 65536;
      char[] buffer = new char[chunkSize];
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

      MessageDigest md = ManifoldCF.startHash();
      String chunkString = new String(buffer, 0, chunkTotal);
      ManifoldCF.addToHash(md, chunkString);
      byte[] byteBuffer;
      if (eofSeen) {
         byteBuffer = chunkString.getBytes(StandardCharsets.UTF_8);
      } else {
         byteBuffer = null;
      }

      if (eofSeen && byteBuffer.length <= maxInMemoryLength) {
         this.file = null;
         this.inMemoryBuffer = byteBuffer;
         this.charLength = (long)chunkTotal;
         this.hashValue = ManifoldCF.getHashValue(md);
      } else {
         this.inMemoryBuffer = null;
         long totalMoved = 0L;

         File outfile;
         try {
            outfile = File.createTempFile("_MC_", "");
         } catch (IOException e) {
            handleIOException(e, "creating backing file");
            outfile = null;
         }

         try {
            ManifoldCF.addFile(outfile);

            OutputStreamWriter outWriter;
            try {
               FileOutputStream outStream = new FileOutputStream(outfile);
               outWriter = new OutputStreamWriter(outStream, StandardCharsets.UTF_8);
            } catch (IOException e) {
               handleIOException(e, "opening backing file");
               FileOutputStream outStream = null;
               outWriter = null;
            }

            try {
               try {
                  outWriter.write(buffer, 0, chunkTotal);
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
                     outWriter.write(buffer, 0, readsize);
                  } catch (IOException e) {
                     handleIOException(e, "writing backing file");
                  }

                  ManifoldCF.addToHash(md, new String(buffer, 0, readsize));
                  totalMoved += (long)readsize;
               }
            } finally {
               try {
                  outWriter.close();
               } catch (IOException e) {
                  handleIOException(e, "closing backing file");
               }

            }

            this.file = outfile;
            this.charLength = totalMoved;
            this.hashValue = ManifoldCF.getHashValue(md);
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

   public TempFileCharacterInput(File tempFile) {
      this.inMemoryBuffer = null;
      this.file = tempFile;
      ManifoldCF.addFile(this.file);
   }

   protected TempFileCharacterInput() {
   }

   public InputStream getUtf8Stream() throws ManifoldCFException {
      if (this.file != null) {
         try {
            return new FileInputStream(this.file);
         } catch (FileNotFoundException e) {
            throw new ManifoldCFException("No such file: " + e.getMessage(), e, 0);
         }
      } else {
         return this.inMemoryBuffer != null ? new ByteArrayInputStream(this.inMemoryBuffer) : null;
      }
   }

   public long getUtf8StreamLength() throws ManifoldCFException {
      if (this.file != null) {
         return this.file.length();
      } else {
         return this.inMemoryBuffer != null ? (long)this.inMemoryBuffer.length : 0L;
      }
   }

   protected void openStream() throws ManifoldCFException {
      if (this.file != null) {
         try {
            InputStream binaryStream = new FileInputStream(this.file);
            this.stream = new InputStreamReader(binaryStream, StandardCharsets.UTF_8);
         } catch (FileNotFoundException e) {
            throw new ManifoldCFException("Can't create stream: " + e.getMessage(), e, 0);
         }
      } else if (this.inMemoryBuffer != null) {
         this.stream = new InputStreamReader(new ByteArrayInputStream(this.inMemoryBuffer), StandardCharsets.UTF_8);
      }

   }

   public CharacterInput transfer() {
      TempFileCharacterInput rval = new TempFileCharacterInput();
      rval.file = this.file;
      rval.inMemoryBuffer = this.inMemoryBuffer;
      rval.stream = this.stream;
      rval.charLength = this.charLength;
      rval.hashValue = this.hashValue;
      this.file = null;
      this.inMemoryBuffer = null;
      this.stream = null;
      this.charLength = -1L;
      this.hashValue = null;
      return rval;
   }

   public void discard() throws ManifoldCFException {
      super.discard();
      if (this.file != null) {
         ManifoldCF.deleteFile(this.file);
         this.file = null;
      }

   }

   protected void calculateLength() throws ManifoldCFException {
      this.scanFile();
   }

   protected void calculateHashValue() throws ManifoldCFException {
      this.scanFile();
   }

   private void scanFile() throws ManifoldCFException {
      try {
         InputStream binaryStream;
         if (this.file != null) {
            binaryStream = new FileInputStream(this.file);
         } else if (this.inMemoryBuffer != null) {
            binaryStream = new ByteArrayInputStream(this.inMemoryBuffer);
         } else {
            binaryStream = null;
         }

         Reader reader = new InputStreamReader(binaryStream, StandardCharsets.UTF_8);

         try {
            MessageDigest md = ManifoldCF.startHash();
            char[] buffer = new char[65536];
            long totalMoved = 0L;

            while(true) {
               int moveAmount = 65536;
               int readsize = reader.read(buffer, 0, moveAmount);
               if (readsize == -1) {
                  this.charLength = totalMoved;
                  this.hashValue = ManifoldCF.getHashValue(md);
                  break;
               }

               ManifoldCF.addToHash(md, new String(buffer, 0, readsize));
               totalMoved += (long)readsize;
            }
         } finally {
            reader.close();
         }
      } catch (IOException e) {
         handleIOException(e, "scanning file");
      }

   }
}
