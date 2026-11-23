package org.spark_project.jetty.util.preventers;

import java.lang.reflect.Method;

public class GCThreadLeakPreventer extends AbstractLeakPreventer {
   public void prevent(ClassLoader loader) {
      try {
         Class<?> clazz = Class.forName("sun.misc.GC");
         Method requestLatency = clazz.getMethod("requestLatency", Long.TYPE);
         requestLatency.invoke((Object)null, 9223372036854775806L);
      } catch (ClassNotFoundException e) {
         LOG.ignore(e);
      } catch (Exception e) {
         LOG.warn(e);
      }

   }
}
