package org.apache.spark.ui;

import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.package$;
import org.apache.spark.status.AppStatusStore;
import scala.Option;

public final class SparkUI$ {
   public static final SparkUI$ MODULE$;
   private final int DEFAULT_PORT;
   private final String STATIC_RESOURCE_DIR;
   private final String DEFAULT_POOL_NAME;

   static {
      new SparkUI$();
   }

   public int DEFAULT_PORT() {
      return this.DEFAULT_PORT;
   }

   public String STATIC_RESOURCE_DIR() {
      return this.STATIC_RESOURCE_DIR;
   }

   public String DEFAULT_POOL_NAME() {
      return this.DEFAULT_POOL_NAME;
   }

   public int getUIPort(SparkConf conf) {
      return conf.getInt("spark.ui.port", this.DEFAULT_PORT());
   }

   public SparkUI create(Option<SparkContext> sc, AppStatusStore store, SparkConf conf, SecurityManager securityManager, String appName, String basePath, long startTime, String appSparkVersion) {
      return new SparkUI(store, sc, conf, securityManager, appName, basePath, startTime, appSparkVersion);
   }

   public String create$default$8() {
      return package$.MODULE$.SPARK_VERSION();
   }

   private SparkUI$() {
      MODULE$ = this;
      this.DEFAULT_PORT = 4040;
      this.STATIC_RESOURCE_DIR = "org/apache/spark/ui/static";
      this.DEFAULT_POOL_NAME = "default";
   }
}
