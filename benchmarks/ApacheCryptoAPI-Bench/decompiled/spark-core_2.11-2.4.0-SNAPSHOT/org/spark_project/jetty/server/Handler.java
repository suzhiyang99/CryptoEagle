package org.spark_project.jetty.server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;
import org.spark_project.jetty.util.component.Destroyable;
import org.spark_project.jetty.util.component.LifeCycle;

@ManagedObject("Jetty Handler")
public interface Handler extends LifeCycle, Destroyable {
   void handle(String var1, Request var2, HttpServletRequest var3, HttpServletResponse var4) throws IOException, ServletException;

   void setServer(Server var1);

   @ManagedAttribute(
      value = "the jetty server for this handler",
      readonly = true
   )
   Server getServer();

   @ManagedOperation(
      value = "destroy associated resources",
      impact = "ACTION"
   )
   void destroy();
}
