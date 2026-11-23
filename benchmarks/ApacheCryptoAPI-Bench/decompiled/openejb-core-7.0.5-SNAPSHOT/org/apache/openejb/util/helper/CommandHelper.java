package org.apache.openejb.util.helper;

import java.util.Arrays;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.table.Line;
import org.apache.openejb.table.Lines;
import org.apache.openejb.util.JavaSecurityManagers;

public final class CommandHelper {
   private CommandHelper() {
   }

   public static Lines listEJBs() throws Exception {
      return listEJBs(JavaSecurityManagers.getSystemProperty("line.separator"));
   }

   public static Lines listEJBs(String cr) throws Exception {
      ContainerSystem cs = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      Lines lines = new Lines(cr);
      lines.add(new Line(new String[]{"Name", "Class", "Interface Type", "Bean Type"}));

      for(BeanContext bc : cs.deployments()) {
         if (!bc.isHidden()) {
            lines.add(new Line(new String[]{bc.getEjbName(), bc.getBeanClass().getName(), getType(bc), componentType(bc.getComponentType())}));
         }
      }

      return lines;
   }

   private static String componentType(BeanType componentType) {
      return componentType == null ? "unknown" : componentType.name();
   }

   private static String getType(BeanContext bc) {
      boolean empty = true;
      StringBuilder sb = new StringBuilder();
      if (bc.isLocalbean()) {
         sb.append("LocalBean[").append(bc.getBeanClass()).append("]");
         empty = false;
      }

      if (bc.getBusinessLocalInterface() != null) {
         if (!empty) {
            sb.append(", ");
         }

         sb.append("Local").append(Arrays.asList(bc.getBusinessLocalInterfaces()));
         empty = false;
      }

      if (bc.getBusinessRemoteInterface() != null) {
         if (!empty) {
            sb.append(", ");
         }

         sb.append("Remote").append(Arrays.asList(bc.getBusinessRemoteInterfaces()));
      }

      return sb.toString();
   }
}
