package org.apache.openejb.util;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class PropertiesHelper {
   private PropertiesHelper() {
   }

   public static String propertiesToString(Properties p) {
      if (p == null) {
         return "";
      } else {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         try {
            p.store(baos, "");
         } catch (Exception var3) {
         }

         return new String(baos.toByteArray());
      }
   }

   public static Map<String, Object> map(Properties props) {
      Map<String, Object> map = new HashMap();

      for(Map.Entry<Object, Object> entry : props.entrySet()) {
         map.put((String)entry.getKey(), entry.getValue());
      }

      return map;
   }
}
