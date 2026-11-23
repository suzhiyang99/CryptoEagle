package org.apache.manifoldcf.core.lockmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class FileLockManager extends BaseLockManager {
   public static final String _rcsid = "@(#)$Id$";
   public static final String synchDirectoryProperty = "org.apache.manifoldcf.synchdirectory";
   protected static final Integer lockPoolInitialization = new Integer(0);
   protected static LockPool myFileLocks = null;
   protected File synchDirectory;
   protected static final int BASE_SIZE = 128;

   public FileLockManager(File synchDirectory) throws ManifoldCFException {
      this.synchDirectory = null;
      this.synchDirectory = synchDirectory;
      if (synchDirectory == null) {
         throw new ManifoldCFException("Synch directory cannot be null");
      } else if (!synchDirectory.isDirectory()) {
         throw new ManifoldCFException("Synch directory must point to an existing, writeable directory!", 3);
      } else {
         synchronized(lockPoolInitialization) {
            if (myFileLocks == null) {
               myFileLocks = new LockPool(new FileLockObjectFactory(synchDirectory));
            }

         }
      }
   }

   public FileLockManager() throws ManifoldCFException {
      this(getSynchDirectoryProperty());
   }

   public static File getSynchDirectoryProperty() throws ManifoldCFException {
      return ManifoldCF.getFileProperty("org.apache.manifoldcf.synchdirectory");
   }

   protected static String getFlagResourceName(String flagName) {
      return "flag-" + flagName;
   }

   public void setGlobalFlag(String flagName) throws ManifoldCFException {
      String resourceName = getFlagResourceName(flagName);
      String path = this.makeFilePath(resourceName);
      (new File(path)).mkdirs();
      File f = new File(path, ManifoldCF.safeFileName(resourceName));

      try {
         f.createNewFile();
      } catch (InterruptedIOException e) {
         throw new ManifoldCFException("Interrupted: " + e.getMessage(), e, 2);
      } catch (IOException e) {
         throw new ManifoldCFException(e.getMessage(), e);
      }
   }

   public void clearGlobalFlag(String flagName) throws ManifoldCFException {
      String resourceName = getFlagResourceName(flagName);
      File f = new File(this.makeFilePath(resourceName), ManifoldCF.safeFileName(resourceName));
      f.delete();
   }

   public boolean checkGlobalFlag(String flagName) throws ManifoldCFException {
      String resourceName = getFlagResourceName(flagName);
      File f = new File(this.makeFilePath(resourceName), ManifoldCF.safeFileName(resourceName));
      return f.exists();
   }

   public byte[] readData(String resourceName) throws ManifoldCFException {
      File f = new File(this.makeFilePath(resourceName), ManifoldCF.safeFileName(resourceName));

      try {
         InputStream is = new FileInputStream(f);

         try {
            ByteArrayBuffer bab = new ByteArrayBuffer();

            while(true) {
               int x = is.read();
               if (x == -1) {
                  byte[] var15 = bab.toArray();
                  return var15;
               }

               bab.add((byte)x);
            }
         } finally {
            is.close();
         }
      } catch (FileNotFoundException var12) {
         return null;
      } catch (InterruptedIOException e) {
         throw new ManifoldCFException("Interrupted: " + e.getMessage(), e, 2);
      } catch (IOException e) {
         throw new ManifoldCFException("IO exception: " + e.getMessage(), e);
      }
   }

   public void writeData(String resourceName, byte[] data) throws ManifoldCFException {
      try {
         String path = this.makeFilePath(resourceName);
         (new File(path)).mkdirs();
         File f = new File(path, ManifoldCF.safeFileName(resourceName));
         if (data == null) {
            f.delete();
         } else {
            FileOutputStream os = new FileOutputStream(f);

            try {
               os.write(data, 0, data.length);
            } finally {
               os.close();
            }

         }
      } catch (InterruptedIOException e) {
         throw new ManifoldCFException("Interrupted: " + e.getMessage(), e, 2);
      } catch (IOException e) {
         throw new ManifoldCFException("IO exception: " + e.getMessage(), e);
      }
   }

   protected LockPool getGlobalLockPool() {
      return myFileLocks;
   }

   protected String makeFilePath(String key) {
      int hashcode = key.hashCode();
      int outerDirNumber = hashcode & 1023;
      int innerDirNumber = hashcode >> 10 & 1023;
      String fullDir = this.synchDirectory.toString();
      if (fullDir.length() == 0 || !fullDir.endsWith("/")) {
         fullDir = fullDir + "/";
      }

      fullDir = fullDir + Integer.toString(outerDirNumber) + "/" + Integer.toString(innerDirNumber);
      return fullDir;
   }

   protected static class ByteArrayBuffer {
      protected byte[] buffer = new byte[128];
      protected int length = 0;

      public ByteArrayBuffer() {
      }

      public void add(byte b) {
         if (this.length == this.buffer.length) {
            byte[] oldbuffer = this.buffer;
            this.buffer = new byte[this.length * 2];
            System.arraycopy(oldbuffer, 0, this.buffer, 0, this.length);
         }

         this.buffer[this.length++] = b;
      }

      public byte[] toArray() {
         byte[] rval = new byte[this.length];
         System.arraycopy(this.buffer, 0, rval, 0, this.length);
         return rval;
      }
   }
}
