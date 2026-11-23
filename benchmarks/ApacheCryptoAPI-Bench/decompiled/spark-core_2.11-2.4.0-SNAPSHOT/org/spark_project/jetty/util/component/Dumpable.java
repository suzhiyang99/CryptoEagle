package org.spark_project.jetty.util.component;

import java.io.IOException;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;

@ManagedObject("Dumpable Object")
public interface Dumpable {
   @ManagedOperation(
      value = "Dump the nested Object state as a String",
      impact = "INFO"
   )
   String dump();

   void dump(Appendable var1, String var2) throws IOException;
}
