package org.apache.openejb.assembler.classic.util;

import java.util.Collection;
import java.util.Properties;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.IdPropertiesInfo;
import org.apache.openejb.assembler.classic.WebAppInfo;

public final class PojoUtil {
   private PojoUtil() {
   }

   public static Properties findConfiguration(Collection<IdPropertiesInfo> infos, String id) {
      for(IdPropertiesInfo info : infos) {
         if (id.equals(info.id)) {
            return info.properties;
         }
      }

      return null;
   }

   public static Collection<IdPropertiesInfo> findPojoConfig(Collection<IdPropertiesInfo> pojoConfigurations, AppInfo appInfo, WebAppInfo webApp) {
      if (pojoConfigurations == null) {
         for(EjbJarInfo ejbJarInfo : appInfo.ejbJars) {
            if (ejbJarInfo.moduleId.equals(webApp.moduleId)) {
               return ejbJarInfo.pojoConfigurations;
            }
         }

         for(EjbJarInfo ejbJarInfo : appInfo.ejbJars) {
            if (ejbJarInfo.moduleName.equals(webApp.moduleId)) {
               return ejbJarInfo.pojoConfigurations;
            }
         }
      }

      return pojoConfigurations;
   }
}
