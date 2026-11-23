package org.apache.openejb.spi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.openejb.core.ObjectInputStreamFiltered;

public class Serializer {
   public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
      ObjectInputStream ois = null;

      Object var3;
      try {
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         ois = new ObjectInputStreamFiltered(bais);
         var3 = ois.readObject();
      } finally {
         if (ois != null) {
            ois.close();
         }

      }

      return var3;
   }

   public static byte[] serialize(Object object) throws IOException {
      ObjectOutputStream oos = null;

      byte[] var3;
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         oos = new ObjectOutputStream(baos);
         oos.writeObject(object);
         oos.flush();
         var3 = baos.toByteArray();
      } finally {
         if (oos != null) {
            oos.close();
         }

      }

      return var3;
   }
}
