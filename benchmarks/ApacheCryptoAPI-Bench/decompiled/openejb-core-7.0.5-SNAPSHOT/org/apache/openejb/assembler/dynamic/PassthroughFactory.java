package org.apache.openejb.assembler.dynamic;

import java.util.Properties;
import org.apache.openejb.assembler.classic.ServiceInfo;

public class PassthroughFactory {
   public static Object create(Object object) {
      return object;
   }

   public static void add(ServiceInfo info, Object object) {
      info.className = PassthroughFactory.class.getName();
      info.constructorArgs.add("object");
      info.factoryMethod = "create";
      info.properties = new Properties();
      info.properties.put("object", object);
   }
}
