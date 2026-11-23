package org.spark_project.jetty.util.preventers;

public class Java2DLeakPreventer extends AbstractLeakPreventer {
   public void prevent(ClassLoader loader) {
      try {
         Class.forName("sun.java2d.Disposer", true, loader);
      } catch (ClassNotFoundException e) {
         LOG.ignore(e);
      }

   }
}
