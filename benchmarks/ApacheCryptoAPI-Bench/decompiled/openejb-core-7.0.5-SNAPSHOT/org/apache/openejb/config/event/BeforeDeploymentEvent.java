package org.apache.openejb.config.event;

import java.net.URL;
import java.util.Arrays;
import org.apache.openejb.observer.Event;

@Event
public class BeforeDeploymentEvent {
   private final URL[] urls;
   private final ClassLoader parentClassLoader;

   public BeforeDeploymentEvent(URL[] files) {
      this(files, (ClassLoader)null);
   }

   public BeforeDeploymentEvent(URL[] files, ClassLoader parent) {
      this.urls = files;
      this.parentClassLoader = parent;
   }

   public URL[] getUrls() {
      return this.urls;
   }

   public ClassLoader getParentClassLoader() {
      return this.parentClassLoader != null ? this.parentClassLoader : this.getClass().getClassLoader();
   }

   public String toString() {
      return "BeforeDeploymentEvent{urls=" + Arrays.asList(this.urls) + '}';
   }
}
