package org.apache.spark.rpc;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.spark.SparkException;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.mutable.StringBuilder;

public final class RpcEndpointAddress$ implements Serializable {
   public static final RpcEndpointAddress$ MODULE$;

   static {
      new RpcEndpointAddress$();
   }

   public RpcEndpointAddress apply(String host, int port, String name) {
      return new RpcEndpointAddress(host, port, name);
   }

   public RpcEndpointAddress apply(String sparkUrl) {
      try {
         URI uri = new URI(sparkUrl);
         String host = uri.getHost();
         int port = uri.getPort();
         String name = uri.getUserInfo();
         String var10000 = uri.getScheme();
         String var7 = "spark";
         if (var10000 == null) {
            if (var7 != null) {
               throw new SparkException((new StringBuilder()).append((Object)"Invalid Spark URL: ").append((Object)sparkUrl).toString());
            }
         } else if (!var10000.equals(var7)) {
            throw new SparkException((new StringBuilder()).append((Object)"Invalid Spark URL: ").append((Object)sparkUrl).toString());
         }

         if (host != null && port >= 0 && name != null && (uri.getPath() == null || uri.getPath().isEmpty()) && uri.getFragment() == null && uri.getQuery() == null) {
            return new RpcEndpointAddress(host, port, name);
         } else {
            throw new SparkException((new StringBuilder()).append((Object)"Invalid Spark URL: ").append((Object)sparkUrl).toString());
         }
      } catch (URISyntaxException var8) {
         throw new SparkException((new StringBuilder()).append((Object)"Invalid Spark URL: ").append((Object)sparkUrl).toString(), var8);
      }
   }

   public RpcEndpointAddress apply(RpcAddress rpcAddress, String name) {
      return new RpcEndpointAddress(rpcAddress, name);
   }

   public Option<Tuple2<RpcAddress, String>> unapply(RpcEndpointAddress x$0) {
      return (Option<Tuple2<RpcAddress, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.rpcAddress(), x$0.name())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RpcEndpointAddress$() {
      MODULE$ = this;
   }
}
