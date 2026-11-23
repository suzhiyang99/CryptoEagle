package org.apache.spark.input;

import org.apache.hadoop.conf.Configuration;

public abstract class Configurable$class {
   public static void setConf(Configurable $this, Configuration c) {
      $this.org$apache$spark$input$Configurable$$conf_$eq(c);
   }

   public static Configuration getConf(Configurable $this) {
      return $this.org$apache$spark$input$Configurable$$conf();
   }

   public static void $init$(Configurable $this) {
   }
}
