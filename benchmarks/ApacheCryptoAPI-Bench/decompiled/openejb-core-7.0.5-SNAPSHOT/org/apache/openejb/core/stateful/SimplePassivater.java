package org.apache.openejb.core.stateful;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.ivm.EjbObjectInputStream;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class SimplePassivater implements PassivationStrategy {
   private static final Logger logger;
   private File sessionDirectory;

   public SimplePassivater() throws SystemException {
      this.init((Properties)null);
   }

   public void init(Properties props) throws SystemException {
      if (props == null) {
         props = new Properties();
      }

      String dir = props.getProperty("org/openejb/core/InstanceManager/PASSIVATOR_PATH_PREFIX");

      try {
         if (dir != null) {
            this.sessionDirectory = SystemInstance.get().getBase().getDirectory(dir);
         } else {
            this.sessionDirectory = new File(JavaSecurityManagers.getSystemProperty("java.io.tmpdir", File.separator + "tmp"));
         }

         if (!this.sessionDirectory.exists() && !this.sessionDirectory.mkdirs()) {
            throw new IOException("Failed to create session directory: " + this.sessionDirectory.getAbsolutePath());
         } else if (this.sessionDirectory.exists() && !this.sessionDirectory.isDirectory()) {
            throw new IOException("Session directory exists as a file: " + this.sessionDirectory.getAbsolutePath());
         } else {
            logger.info("Using directory " + this.sessionDirectory + " for stateful session passivation");
         }
      } catch (IOException e) {
         throw new SystemException(this.getClass().getName() + ".init(): can't use directory prefix " + dir + ":" + e, e);
      }
   }

   public void passivate(Object primaryKey, Object state) throws SystemException {
      try {
         String filename = primaryKey.toString().replace(':', '=');
         File sessionFile = new File(this.sessionDirectory, filename);
         logger.info("Passivating to file " + sessionFile);

         try {
            OutputStream os = IO.write(sessionFile);
            Throwable var6 = null;

            try {
               ObjectOutputStream oos = new ObjectOutputStream(os);
               Throwable var8 = null;

               try {
                  oos.writeObject(state);
               } catch (Throwable var49) {
                  var8 = var49;
                  throw var49;
               } finally {
                  if (oos != null) {
                     if (var8 != null) {
                        try {
                           oos.close();
                        } catch (Throwable var48) {
                           var8.addSuppressed(var48);
                        }
                     } else {
                        oos.close();
                     }
                  }

               }
            } catch (Throwable var51) {
               var6 = var51;
               throw var51;
            } finally {
               if (os != null) {
                  if (var6 != null) {
                     try {
                        os.close();
                     } catch (Throwable var47) {
                        var6.addSuppressed(var47);
                     }
                  } else {
                     os.close();
                  }
               }

            }
         } finally {
            sessionFile.deleteOnExit();
         }

      } catch (NotSerializableException nse) {
         logger.error("Passivation failed ", (Throwable)nse);
         throw (SystemException)(new SystemException("The type " + nse.getMessage() + " is not serializable as mandated by the EJB specification.")).initCause(nse);
      } catch (Exception t) {
         logger.error("Passivation failed ", (Throwable)t);
         throw new SystemException(t);
      }
   }

   public void passivate(Map hash) throws SystemException {
      for(Object id : hash.keySet()) {
         this.passivate(id, hash.get(id));
      }

   }

   public Object activate(Object primaryKey) throws SystemException {
      try {
         String filename = primaryKey.toString().replace(':', '=');
         File sessionFile = new File(this.sessionDirectory, filename);
         if (sessionFile.exists()) {
            logger.info("Activating from file " + sessionFile);

            Object var8;
            try {
               InputStream source = IO.read(sessionFile);
               Throwable var5 = null;

               try {
                  ObjectInputStream ois = new EjbObjectInputStream(source);
                  Throwable var7 = null;

                  try {
                     var8 = ois.readObject();
                  } catch (Throwable var46) {
                     var8 = var46;
                     var7 = var46;
                     throw var46;
                  } finally {
                     if (ois != null) {
                        if (var7 != null) {
                           try {
                              ois.close();
                           } catch (Throwable var45) {
                              var7.addSuppressed(var45);
                           }
                        } else {
                           ois.close();
                        }
                     }

                  }
               } catch (Throwable var48) {
                  var5 = var48;
                  throw var48;
               } finally {
                  if (source != null) {
                     if (var5 != null) {
                        try {
                           source.close();
                        } catch (Throwable var44) {
                           var5.addSuppressed(var44);
                        }
                     } else {
                        source.close();
                     }
                  }

               }
            } finally {
               if (!sessionFile.delete()) {
                  sessionFile.deleteOnExit();
               }

            }

            return var8;
         } else {
            logger.info("Activation failed: file not found " + sessionFile);
            return null;
         }
      } catch (Exception t) {
         logger.info("Activation failed ", (Throwable)t);
         throw new SystemException(t);
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }
}
