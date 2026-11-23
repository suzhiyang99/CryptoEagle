package org.apache.spark.deploy.rest;

import scala.Serializable;
import scala.collection.immutable.Map;

public final class RestSubmissionClient$ {
   public static final RestSubmissionClient$ MODULE$;
   private final int org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_INTERVAL;
   private final int org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_MAX_TRIES;
   private final String PROTOCOL_VERSION;

   static {
      new RestSubmissionClient$();
   }

   public int org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_INTERVAL() {
      return this.org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_INTERVAL;
   }

   public int org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_MAX_TRIES() {
      return this.org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_MAX_TRIES;
   }

   public String PROTOCOL_VERSION() {
      return this.PROTOCOL_VERSION;
   }

   public Map<String, String> filterSystemEnvironment(Map<String, String> env) {
      return env.filterKeys(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String k) {
            boolean var10000;
            label29: {
               if (k.startsWith("SPARK_")) {
                  label27: {
                     String var2 = "SPARK_ENV_LOADED";
                     if (k == null) {
                        if (var2 == null) {
                           break label27;
                        }
                     } else if (k.equals(var2)) {
                        break label27;
                     }

                     String var3 = "SPARK_HOME";
                     if (k == null) {
                        if (var3 != null) {
                           break label29;
                        }
                     } else if (!k.equals(var3)) {
                        break label29;
                     }
                  }
               }

               if (!k.startsWith("MESOS_")) {
                  var10000 = false;
                  return var10000;
               }
            }

            var10000 = true;
            return var10000;
         }
      });
   }

   private RestSubmissionClient$() {
      MODULE$ = this;
      this.org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_INTERVAL = 1000;
      this.org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_MAX_TRIES = 10;
      this.PROTOCOL_VERSION = "v1";
   }
}
