package org.apache.openejb.core;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class EmptyResourcesClassLoader extends ClassLoader {
   public EmptyResourcesClassLoader() {
      super((ClassLoader)null);
   }

   protected URL findResource(String name) {
      return null;
   }

   public Enumeration<URL> getResources(String name) throws IOException {
      return new Enumeration<URL>() {
         public boolean hasMoreElements() {
            return false;
         }

         public URL nextElement() {
            throw new UnsupportedOperationException("this enumeration has no elements");
         }
      };
   }
}
