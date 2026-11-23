package org.apache.openejb.core.managed;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.SystemException;
import org.apache.openejb.spi.Serializer;
import org.apache.openejb.util.JavaSecurityManagers;

public class RAFPassivater implements PassivationStrategy {
   int fileID;
   Hashtable masterTable = new Hashtable();

   public void init(Properties props) throws SystemException {
   }

   public synchronized void passivate(Map stateTable) throws SystemException {
      try {
         RandomAccessFile ras = new RandomAccessFile(JavaSecurityManagers.getSystemProperty("java.io.tmpdir", File.separator + "tmp") + File.separator + "passivation" + this.fileID + ".ser", "rw");
         Throwable var3 = null;

         try {
            ++this.fileID;
            Iterator iterator = stateTable.keySet().iterator();
            Pointer lastPointer = null;

            while(iterator.hasNext()) {
               Object id = iterator.next();
               Object obj = stateTable.get(id);
               byte[] bytes = Serializer.serialize(obj);
               long filepointer = ras.getFilePointer();
               if (lastPointer == null) {
                  lastPointer = new Pointer(this.fileID, filepointer, (int)filepointer);
               } else {
                  lastPointer = new Pointer(this.fileID, filepointer, (int)(filepointer - lastPointer.filepointer));
               }

               this.masterTable.put(id, lastPointer);
               ras.write(bytes);
            }
         } catch (Throwable var19) {
            var3 = var19;
            throw var19;
         } finally {
            if (ras != null) {
               if (var3 != null) {
                  try {
                     ras.close();
                  } catch (Throwable var18) {
                     var3.addSuppressed(var18);
                  }
               } else {
                  ras.close();
               }
            }

         }

      } catch (Exception e) {
         throw new SystemException(e);
      }
   }

   public synchronized Object activate(Object primaryKey) throws SystemException {
      Pointer pointer = (Pointer)this.masterTable.get(primaryKey);
      if (pointer == null) {
         return null;
      } else {
         try {
            RandomAccessFile ras = new RandomAccessFile(JavaSecurityManagers.getSystemProperty("java.io.tmpdir", File.separator + "tmp") + File.separator + "passivation" + pointer.fileid + ".ser", "r");
            Throwable var4 = null;

            Object var6;
            try {
               byte[] bytes = new byte[pointer.bytesize];
               ras.seek(pointer.filepointer);
               ras.readFully(bytes);
               ras.close();
               var6 = Serializer.deserialize(bytes);
            } catch (Throwable var16) {
               var4 = var16;
               throw var16;
            } finally {
               if (ras != null) {
                  if (var4 != null) {
                     try {
                        ras.close();
                     } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                     }
                  } else {
                     ras.close();
                  }
               }

            }

            return var6;
         } catch (Exception e) {
            throw new SystemException(e);
         }
      }
   }

   static class Pointer {
      int fileid;
      long filepointer;
      int bytesize;

      public Pointer(int file, long pointer, int bytecount) {
         this.fileid = file;
         this.filepointer = pointer;
         this.bytesize = bytecount;
      }
   }
}
