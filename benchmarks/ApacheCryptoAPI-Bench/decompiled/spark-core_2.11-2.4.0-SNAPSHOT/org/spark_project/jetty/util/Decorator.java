package org.spark_project.jetty.util;

public interface Decorator {
   <T> T decorate(T var1);

   void destroy(Object var1);
}
