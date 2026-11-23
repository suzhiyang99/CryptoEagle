package org.spark_project.jetty.server;

import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.LifeCycle;

@ManagedObject("Handler of Multiple Handlers")
public interface HandlerContainer extends LifeCycle {
   @ManagedAttribute("handlers in this container")
   Handler[] getHandlers();

   @ManagedAttribute("all contained handlers")
   Handler[] getChildHandlers();

   Handler[] getChildHandlersByClass(Class<?> var1);

   <T extends Handler> T getChildHandlerByClass(Class<T> var1);
}
