package org.spark_project.jetty.util.component;

import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;

@ManagedObject
public interface Destroyable {
   @ManagedOperation(
      value = "Destroys this component",
      impact = "ACTION"
   )
   void destroy();
}
