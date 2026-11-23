package org.apache.spark.rpc;

import java.net.URI;
import org.apache.spark.util.Utils$;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.runtime.BoxesRunTime;

public final class RpcAddress$ implements Serializable {
   public static final RpcAddress$ MODULE$;

   static {
      new RpcAddress$();
   }

   public RpcAddress fromURIString(String uri) {
      URI uriObj = new URI(uri);
      return new RpcAddress(uriObj.getHost(), uriObj.getPort());
   }

   public RpcAddress fromSparkURL(String sparkUrl) {
      Tuple2 var3 = Utils$.MODULE$.extractHostPortFromSparkUrl(sparkUrl);
      if (var3 != null) {
         String host = (String)var3._1();
         int port = var3._2$mcI$sp();
         Tuple2 var6 = new Tuple2(host, BoxesRunTime.boxToInteger(port));
         String host = (String)var6._1();
         int port = var6._2$mcI$sp();
         return new RpcAddress(host, port);
      } else {
         throw new MatchError(var3);
      }
   }

   public RpcAddress apply(String host, int port) {
      return new RpcAddress(host, port);
   }

   public Option<Tuple2<String, Object>> unapply(RpcAddress x$0) {
      return (Option<Tuple2<String, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.host(), BoxesRunTime.boxToInteger(x$0.port()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RpcAddress$() {
      MODULE$ = this;
   }
}
