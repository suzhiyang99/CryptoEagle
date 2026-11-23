package org.spark_project.jetty.util.preventers;

import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class AbstractLeakPreventer extends AbstractLifeCycle {
   protected static final Logger LOG = Log.getLogger(AbstractLeakPreventer.class);

   public abstract void prevent(ClassLoader var1);

   protected void doStart() throws Exception {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();

      try {
         Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
         this.prevent(this.getClass().getClassLoader());
         super.doStart();
      } finally {
         Thread.currentThread().setContextClassLoader(loader);
      }

   }
}
