package org.apache.meecrowave.cdi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.logging.jul.Log4j2Logger;
import org.apache.meecrowave.logging.openwebbeans.Log4j2LoggerFactory;
import org.apache.meecrowave.logging.tomcat.Log4j2Log;
import org.apache.meecrowave.openwebbeans.KnowClassesFilter;
import org.apache.meecrowave.openwebbeans.OWBTomcatWebScannerService;
import org.apache.openwebbeans.se.OWBContainer;
import org.apache.openwebbeans.se.OWBInitializer;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ScannerService;
import org.apache.xbean.finder.filter.Filter;

public class MeecrowaveSeContainerInitializer extends OWBInitializer {
   private Meecrowave.Builder builder = new Meecrowave.Builder();

   public SeContainerInitializer addProperty(String s, Object o) {
      if (Meecrowave.Builder.class.isInstance(o)) {
         this.builder = (Meecrowave.Builder)Meecrowave.Builder.class.cast(o);
         return this;
      } else {
         String setter = "set" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
         Class<? extends Meecrowave.Builder> builderClass = this.builder.getClass();
         Optional<Method> setterOpt = Stream.of(builderClass.getMethods()).filter((m) -> m.getName().equals(setter) && m.getParameterCount() == 1).findFirst();
         if (!setterOpt.isPresent()) {
            super.addProperty(s, o);
            return this;
         } else {
            try {
               builderClass.getMethod(setter, o.getClass()).invoke(this.builder, o);
            } catch (NoSuchMethodException nsme) {
               if (Integer.class.isInstance(o)) {
                  try {
                     builderClass.getMethod(setter, Integer.TYPE).invoke(this.builder, o);
                  } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var10) {
                     throw new IllegalArgumentException(nsme);
                  }
               } else if (Long.class.isInstance(o)) {
                  try {
                     builderClass.getMethod(setter, Long.TYPE).invoke(this.builder, o);
                  } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var9) {
                     throw new IllegalArgumentException(nsme);
                  }
               } else {
                  if (!Boolean.class.isInstance(o)) {
                     throw new IllegalArgumentException(nsme);
                  }

                  try {
                     builderClass.getMethod(setter, Boolean.TYPE).invoke(this.builder, o);
                  } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var8) {
                     throw new IllegalArgumentException(nsme);
                  }
               }
            } catch (InvocationTargetException | IllegalAccessException ex) {
               throw new IllegalArgumentException(ex);
            }

            return this;
         }
      }
   }

   protected void addCustomServices(Map<String, Object> services) {
      final Set<String> forced = (Set)this.scannerService.configuredClasses().stream().map(Class::getName).collect(Collectors.toSet());
      services.put(Filter.class.getName(), new KnowClassesFilter() {
         public boolean accept(String name) {
            return forced.contains(name) || super.accept(name);
         }
      });
   }

   protected SeContainer newContainer(WebBeansContext context) {
      final Meecrowave meecrowave = new Meecrowave(this.builder);
      return new OWBContainer(context, meecrowave) {
         {
            meecrowave.bake();
         }

         protected void doClose() {
            meecrowave.close();
         }
      };
   }

   protected ScannerService getScannerService() {
      return new OWBTomcatWebScannerService(this.scannerService);
   }

   static {
      System.setProperty("java.util.logging.manager", System.getProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager"));
      System.setProperty("openwebbeans.logging.factory", System.getProperty("openwebbeans.logging.factory", Log4j2LoggerFactory.class.getName()));
      System.setProperty("org.apache.cxf.Logger", System.getProperty("org.apache.cxf.Logger", Log4j2Logger.class.getName()));
      System.setProperty("org.apache.tomcat.Logger", System.getProperty("org.apache.tomcat.Logger", Log4j2Log.class.getName()));
   }
}
