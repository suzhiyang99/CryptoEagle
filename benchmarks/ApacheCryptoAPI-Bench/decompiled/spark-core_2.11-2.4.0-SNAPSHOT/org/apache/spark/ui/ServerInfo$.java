package org.apache.spark.ui;

import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.server.handler.ContextHandlerCollection;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple4;
import scala.runtime.AbstractFunction4;
import scala.runtime.BoxesRunTime;

public final class ServerInfo$ extends AbstractFunction4<Server, Object, Option<Object>, ContextHandlerCollection, ServerInfo> implements Serializable {
   public static final ServerInfo$ MODULE$;

   static {
      new ServerInfo$();
   }

   public final String toString() {
      return "ServerInfo";
   }

   public ServerInfo apply(Server server, int boundPort, Option<Object> securePort, ContextHandlerCollection rootHandler) {
      return new ServerInfo(server, boundPort, securePort, rootHandler);
   }

   public Option<Tuple4<Server, Object, Option<Object>, ContextHandlerCollection>> unapply(ServerInfo x$0) {
      return (Option<Tuple4<Server, Object, Option<Object>, ContextHandlerCollection>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple4(x$0.server(), BoxesRunTime.boxToInteger(x$0.boundPort()), x$0.securePort(), x$0.org$apache$spark$ui$ServerInfo$$rootHandler())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ServerInfo$() {
      MODULE$ = this;
   }
}
