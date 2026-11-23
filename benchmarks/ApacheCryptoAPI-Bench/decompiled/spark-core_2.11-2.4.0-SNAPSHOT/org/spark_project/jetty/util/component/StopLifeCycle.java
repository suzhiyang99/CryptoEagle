package org.spark_project.jetty.util.component;

import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class StopLifeCycle extends AbstractLifeCycle implements LifeCycle.Listener {
   private static final Logger LOG = Log.getLogger(StopLifeCycle.class);
   private final LifeCycle _lifecycle;

   public StopLifeCycle(LifeCycle lifecycle) {
      this._lifecycle = lifecycle;
      this.addLifeCycleListener(this);
   }

   public void lifeCycleStarting(LifeCycle lifecycle) {
   }

   public void lifeCycleStarted(LifeCycle lifecycle) {
      try {
         this._lifecycle.stop();
      } catch (Exception e) {
         LOG.warn(e);
      }

   }

   public void lifeCycleFailure(LifeCycle lifecycle, Throwable cause) {
   }

   public void lifeCycleStopping(LifeCycle lifecycle) {
   }

   public void lifeCycleStopped(LifeCycle lifecycle) {
   }
}
