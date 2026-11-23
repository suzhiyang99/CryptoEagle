package org.apache.manifoldcf.core.lockmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.apache.manifoldcf.core.interfaces.LockException;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.Logging;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class FileLockObject extends LockObject {
   public static final String _rcsid = "@(#)$Id: LockObject.java 988245 2010-08-23 18:39:35Z kwright $";
   private static final int STATUS_WRITELOCKED = -1;
   private File lockDirectoryName = null;
   private File lockFileName = null;
   private boolean isSync;
   private static final String DOTLOCK = ".lock";
   private static final String DOTFILE = ".file";
   private static final String SLASH = "/";
   private static final String FILELOCKED = "File locked";

   public FileLockObject(LockPool lockPool, Object lockKey, File synchDir) {
      super(lockPool, lockKey);
      this.isSync = synchDir != null;
      if (this.isSync) {
         int hashcode = lockKey.hashCode();
         int outerDirNumber = hashcode & 1023;
         int innerDirNumber = hashcode >> 10 & 1023;
         String fullDir = synchDir.toString();
         if (fullDir.length() == 0 || !fullDir.endsWith("/")) {
            fullDir = fullDir + "/";
         }

         fullDir = fullDir + Integer.toString(outerDirNumber) + "/" + Integer.toString(innerDirNumber);
         (new File(fullDir)).mkdirs();
         String filename = createFileName(lockKey);
         this.lockDirectoryName = new File(fullDir, filename + ".lock");
         this.lockFileName = new File(fullDir, filename + ".file");
      }

   }

   private static String createFileName(Object lockKey) {
      return "lock-" + ManifoldCF.safeFileName(lockKey.toString());
   }

   protected void obtainGlobalWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.isSync) {
         this.grabFileLock();

         try {
            int status = this.readFile();
            if (status != 0) {
               throw new LockException("Locked by another JVM");
            }

            this.writeFile(-1);
         } finally {
            this.releaseFileLock();
         }
      }

   }

   protected void clearGlobalWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.isSync) {
         this.grabFileLock();

         try {
            this.writeFile(0);
         } finally {
            this.releaseFileLock();
         }
      }

   }

   protected void obtainGlobalNonExWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.isSync) {
         this.grabFileLock();

         try {
            int status = this.readFile();
            if (status == -1 || status > 0) {
               throw new LockException("Locked by another JVM");
            }

            if (status == 0) {
               status = -1;
            }

            --status;
            this.writeFile(status);
         } finally {
            this.releaseFileLock();
         }
      }

   }

   protected void clearGlobalNonExWriteLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.isSync) {
         this.grabFileLock();

         try {
            int status = this.readFile();
            if (status >= -1) {
               throw new RuntimeException("JVM error: File lock is not in expected state for object " + this.toString());
            }

            ++status;
            if (status == -1) {
               status = 0;
            }

            this.writeFile(status);
         } finally {
            this.releaseFileLock();
         }
      }

   }

   protected void obtainGlobalReadLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.isSync) {
         this.grabFileLock();

         try {
            int status = this.readFile();
            if (status <= -1) {
               throw new LockException("Locked by another JVM");
            }

            ++status;
            this.writeFile(status);
         } finally {
            this.releaseFileLock();
         }
      }

   }

   protected void clearGlobalReadLockNoWait() throws ManifoldCFException, LockException, InterruptedException {
      if (this.isSync) {
         this.grabFileLock();

         try {
            int status = this.readFile();
            if (status == 0) {
               throw new RuntimeException("JVM error: File lock is not in expected state for object " + this.toString());
            }

            --status;
            this.writeFile(status);
         } finally {
            this.releaseFileLock();
         }
      }

   }

   private synchronized void grabFileLock() throws LockException, InterruptedException {
      while(true) {
         try {
            if (!this.lockDirectoryName.createNewFile()) {
               throw new LockException("File locked");
            }

            return;
         } catch (InterruptedIOException e) {
            throw new InterruptedException("Interrupted IO: " + e.getMessage());
         } catch (IOException var5) {
            IOException e = var5;

            try {
               Logging.lock.warn("Attempt to set file lock '" + this.lockDirectoryName.toString() + "' failed: " + e.getMessage(), e);
            } catch (Throwable var3) {
               var5.printStackTrace();
            }

            ManifoldCF.sleep(100L);
         }
      }
   }

   private synchronized void releaseFileLock() throws InterruptedException {
      Throwable ie = null;

      while(true) {
         try {
            if (this.lockDirectoryName.delete()) {
               break;
            }

            try {
               Logging.lock.fatal("Failure deleting file lock '" + this.lockDirectoryName.toString() + "'");
            } catch (Throwable var7) {
               System.out.println("Failure deleting file lock '" + this.lockDirectoryName.toString() + "'");
            }

            System.exit(-100);
         } catch (Error var8) {
            Error e = var8;
            String message = "Error deleting file lock '" + this.lockDirectoryName.toString() + "': " + var8.getMessage();

            try {
               Logging.lock.error(message, e);
            } catch (Throwable var6) {
               System.out.println(message);
               var8.printStackTrace();
            }

            ie = var8;
            ManifoldCF.sleep(100L);
         } catch (RuntimeException var9) {
            RuntimeException e = var9;
            String message = "Error deleting file lock '" + this.lockDirectoryName.toString() + "': " + var9.getMessage();

            try {
               Logging.lock.error(message, e);
            } catch (Throwable var5) {
               System.out.println(message);
               var9.printStackTrace();
            }

            ie = var9;
            ManifoldCF.sleep(100L);
         }
      }

      if (ie != null) {
         if (ie instanceof InterruptedException) {
            throw (InterruptedException)ie;
         }

         if (ie instanceof Error) {
            throw (Error)ie;
         }

         if (ie instanceof RuntimeException) {
            throw (RuntimeException)ie;
         }
      }

   }

   private synchronized int readFile() throws InterruptedException {
      try {
         InputStreamReader isr = new InputStreamReader(new FileInputStream(this.lockFileName), StandardCharsets.UTF_8);

         try {
            BufferedReader x = new BufferedReader(isr);

            try {
               StringBuilder sb = new StringBuilder();

               while(true) {
                  int rval = x.read();
                  if (rval == -1) {
                     try {
                        e = Integer.parseInt(sb.toString());
                        return (int)e;
                     } catch (NumberFormatException var24) {
                        e = var24;
                        throw new IOException("Lock number read was not valid: " + var24.getMessage());
                     }
                  }

                  sb.append((char)rval);
               }
            } finally {
               x.close();
            }
         } catch (InterruptedIOException e) {
            throw new InterruptedException("Interrupted IO: " + e.getMessage());
         } catch (IOException var27) {
            IOException e = var27;
            String message = "Could not read from lock file: '" + this.lockFileName.toString() + "'";

            try {
               Logging.lock.error(message, e);
            } catch (Throwable var23) {
               System.out.println(message);
               var27.printStackTrace();
            }

            throw var27;
         } finally {
            isr.close();
         }
      } catch (InterruptedIOException e) {
         throw new InterruptedException("Interrupted IO: " + e.getMessage());
      } catch (IOException var30) {
         return 0;
      }
   }

   private synchronized void writeFile(int value) throws InterruptedException {
      try {
         if (value == 0) {
            if (!this.lockFileName.delete()) {
               throw new IOException("Could not delete file '" + this.lockFileName.toString() + "'");
            }
         } else {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(this.lockFileName), StandardCharsets.UTF_8);

            try {
               BufferedWriter x = new BufferedWriter(osw);

               try {
                  x.write(Integer.toString(value));
               } finally {
                  x.close();
               }
            } finally {
               osw.close();
            }
         }
      } catch (Error var29) {
         Error e = var29;
         String message = "Couldn't write to lock file; hard error occurred.  Shutting down process; locks may be left dangling.  You must cleanup before restarting.";

         try {
            Logging.lock.error(message, e);
         } catch (Throwable var26) {
            System.out.println(message);
            var29.printStackTrace();
         }

         System.exit(-100);
      } catch (RuntimeException var30) {
         RuntimeException e = var30;
         String message = "Couldn't write to lock file; JVM error.  Shutting down process; locks may be left dangling.  You must cleanup before restarting.";

         try {
            Logging.lock.error(message, e);
         } catch (Throwable var25) {
            System.out.println(message);
            var30.printStackTrace();
         }

         System.exit(-100);
      } catch (InterruptedIOException e) {
         throw new InterruptedException("Interrupted IO: " + e.getMessage());
      } catch (IOException var32) {
         IOException e = var32;
         String message = "Couldn't write to lock file; disk may be full.  Shutting down process; locks may be left dangling.  You must cleanup before restarting.";

         try {
            Logging.lock.error(message, e);
         } catch (Throwable var24) {
            System.out.println(message);
            var32.printStackTrace();
         }

         System.exit(-100);
      }

   }
}
