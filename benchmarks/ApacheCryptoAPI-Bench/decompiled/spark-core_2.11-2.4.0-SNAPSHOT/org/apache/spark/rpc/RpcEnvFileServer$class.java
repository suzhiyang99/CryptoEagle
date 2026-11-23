package org.apache.spark.rpc;

import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;

public abstract class RpcEnvFileServer$class {
   public static String validateDirectoryUri(RpcEnvFileServer $this, String baseUri) {
      String fixedBaseUri;
      Predef$ var10000;
      boolean var10001;
      label25: {
         label27: {
            fixedBaseUri = (new StringBuilder()).append((Object)"/").append((Object)(new StringOps(Predef$.MODULE$.augmentString((new StringOps(Predef$.MODULE$.augmentString(baseUri))).stripPrefix("/")))).stripSuffix("/")).toString();
            var10000 = Predef$.MODULE$;
            String var3 = "/files";
            if (fixedBaseUri == null) {
               if (var3 == null) {
                  break label27;
               }
            } else if (fixedBaseUri.equals(var3)) {
               break label27;
            }

            String var4 = "/jars";
            if (fixedBaseUri == null) {
               if (var4 == null) {
                  break label27;
               }
            } else if (fixedBaseUri.equals(var4)) {
               break label27;
            }

            var10001 = true;
            break label25;
         }

         var10001 = false;
      }

      var10000.require(var10001, new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Directory URI cannot be /files nor /jars.";
         }
      });
      return fixedBaseUri;
   }

   public static void $init$(RpcEnvFileServer $this) {
   }
}
