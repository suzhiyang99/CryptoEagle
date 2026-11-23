package org.apache.openejb.cdi;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.openejb.assembler.classic.BeansInfo;
import org.apache.openejb.loader.SystemInstance;
import org.apache.webbeans.exception.WebBeansConfigurationException;
import org.apache.webbeans.spi.BeanArchiveService;
import org.apache.webbeans.spi.BeanArchiveService.BeanDiscoveryMode;
import org.apache.webbeans.xml.DefaultBeanArchiveInformation;

public class OpenEJBBeanInfoService implements BeanArchiveService {
   private Map<URL, BeanArchiveService.BeanArchiveInformation> beanArchiveInfo = new HashMap();

   public Map<URL, BeanArchiveService.BeanArchiveInformation> getBeanArchiveInfo() {
      return this.beanArchiveInfo;
   }

   public DefaultBeanArchiveInformation createBeanArchiveInformation(BeansInfo.BDAInfo bda, BeansInfo info, ClassLoader loader) {
      String mode = bda.discoveryMode == null ? "ALL" : bda.discoveryMode;
      if (info != null && info.version != null && !"1.0".equals(info.version) && info.discoveryMode == null) {
         throw new WebBeansConfigurationException("beans.xml with version 1.1 and higher must declare a bean-discovery-mode!");
      } else {
         DefaultBeanArchiveInformation information = new DefaultBeanArchiveInformation(bda.uri.toASCIIString());
         information.setVersion(info == null ? "1.1" : info.version);
         information.setBeanDiscoveryMode(BeanDiscoveryMode.valueOf(mode.trim().toUpperCase(Locale.ENGLISH)));
         information.setDecorators(bda.decorators);
         information.setInterceptors(bda.interceptors);
         if (info != null) {
            information.getAlternativeClasses().addAll(bda.alternatives);
            information.getAlternativeStereotypes().addAll(bda.stereotypeAlternatives);

            for(BeansInfo.ExclusionEntryInfo exclusionInfo : info.excludes) {
               boolean skip = false;

               for(String n : exclusionInfo.exclusion.availableClasses) {
                  if (!isClassAvailable(loader, n)) {
                     skip = true;
                     break;
                  }
               }

               if (!skip) {
                  for(String n : exclusionInfo.exclusion.notAvailableClasses) {
                     if (isClassAvailable(loader, n)) {
                        skip = true;
                        break;
                     }
                  }
               }

               if (!skip) {
                  for(String n : exclusionInfo.exclusion.systemPropertiesPresence) {
                     if (SystemInstance.get().getProperty(n) == null) {
                        skip = true;
                        break;
                     }
                  }
               }

               if (!skip) {
                  for(String n : exclusionInfo.exclusion.systemProperties.stringPropertyNames()) {
                     if (!exclusionInfo.exclusion.systemProperties.getProperty(n).equals(SystemInstance.get().getProperty(n))) {
                        skip = true;
                        break;
                     }
                  }
               }

               if (!skip) {
                  String name = exclusionInfo.name;
                  if (name.endsWith(".*")) {
                     information.addClassExclude(name.substring(0, name.length() - 2));
                  } else if (name.endsWith(".**")) {
                     information.addPackageExclude(name.substring(0, name.length() - 3));
                  } else {
                     information.addClassExclude(name);
                  }
               }
            }
         }

         return information;
      }
   }

   private static boolean isClassAvailable(ClassLoader loader, String name) {
      try {
         loader.loadClass(name);
         return true;
      } catch (Throwable var3) {
         return false;
      }
   }

   public BeanArchiveService.BeanArchiveInformation getBeanArchiveInformation(URL beanArchiveUrl) {
      return (BeanArchiveService.BeanArchiveInformation)this.beanArchiveInfo.get(beanArchiveUrl);
   }

   public Set<URL> getRegisteredBeanArchives() {
      return Collections.emptySet();
   }

   public void release() {
   }
}
