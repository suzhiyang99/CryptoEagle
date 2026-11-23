package org.apache.spark.deploy.master.ui;

import org.apache.spark.ui.SparkUI$;

public final class MasterWebUI$ {
   public static final MasterWebUI$ MODULE$;
   private final String org$apache$spark$deploy$master$ui$MasterWebUI$$STATIC_RESOURCE_DIR;

   static {
      new MasterWebUI$();
   }

   public String org$apache$spark$deploy$master$ui$MasterWebUI$$STATIC_RESOURCE_DIR() {
      return this.org$apache$spark$deploy$master$ui$MasterWebUI$$STATIC_RESOURCE_DIR;
   }

   private MasterWebUI$() {
      MODULE$ = this;
      this.org$apache$spark$deploy$master$ui$MasterWebUI$$STATIC_RESOURCE_DIR = SparkUI$.MODULE$.STATIC_RESOURCE_DIR();
   }
}
