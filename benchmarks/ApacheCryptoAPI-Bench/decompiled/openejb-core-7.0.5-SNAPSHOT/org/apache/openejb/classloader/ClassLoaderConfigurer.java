package org.apache.openejb.classloader;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public interface ClassLoaderConfigurer {
   URL[] additionalURLs();

   boolean accept(URL var1);

   public static final class Helper {
      private Helper() {
      }

      public static void configure(Collection<URL> urls, ClassLoaderConfigurer configurer) {
         Iterator<URL> it = urls.iterator();

         while(it.hasNext()) {
            if (!configurer.accept((URL)it.next())) {
               it.remove();
            }
         }

         urls.addAll(Arrays.asList(configurer.additionalURLs()));
      }
   }
}
