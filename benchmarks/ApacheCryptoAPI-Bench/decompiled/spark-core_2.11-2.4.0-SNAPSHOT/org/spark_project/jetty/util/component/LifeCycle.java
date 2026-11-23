package org.spark_project.jetty.util.component;

import java.util.EventListener;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;

@ManagedObject("Lifecycle Interface for startable components")
public interface LifeCycle {
   @ManagedOperation(
      value = "Starts the instance",
      impact = "ACTION"
   )
   void start() throws Exception;

   @ManagedOperation(
      value = "Stops the instance",
      impact = "ACTION"
   )
   void stop() throws Exception;

   boolean isRunning();

   boolean isStarted();

   boolean isStarting();

   boolean isStopping();

   boolean isStopped();

   boolean isFailed();

   void addLifeCycleListener(Listener var1);

   void removeLifeCycleListener(Listener var1);

   public interface Listener extends EventListener {
      void lifeCycleStarting(LifeCycle var1);

      void lifeCycleStarted(LifeCycle var1);

      void lifeCycleFailure(LifeCycle var1, Throwable var2);

      void lifeCycleStopping(LifeCycle var1);

      void lifeCycleStopped(LifeCycle var1);
   }
}
