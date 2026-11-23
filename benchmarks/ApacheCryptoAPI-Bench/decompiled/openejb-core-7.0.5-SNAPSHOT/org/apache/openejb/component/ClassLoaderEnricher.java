package org.apache.openejb.component;

import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClassLoaderEnricher {
   private final Collection<URL> applicationURLs = new CopyOnWriteArrayList();

   public URL[] applicationEnrichment() {
      return (URL[])this.applicationURLs.toArray(new URL[this.applicationURLs.size()]);
   }

   public void addUrl(URL url) {
      if (!this.applicationURLs.contains(url)) {
         this.applicationURLs.add(url);
      }

   }

   public void removeUrl(URL url) {
      this.applicationURLs.remove(url);
   }
}
