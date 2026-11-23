package org.apache.spark.internal.config;

import scala.Predef$;
import scala.collection.immutable.StringOps;
import scala.util.matching.Regex;

public final class ConfigReader$ {
   public static final ConfigReader$ MODULE$;
   private final Regex org$apache$spark$internal$config$ConfigReader$$REF_RE;

   static {
      new ConfigReader$();
   }

   public Regex org$apache$spark$internal$config$ConfigReader$$REF_RE() {
      return this.org$apache$spark$internal$config$ConfigReader$$REF_RE;
   }

   private ConfigReader$() {
      MODULE$ = this;
      this.org$apache$spark$internal$config$ConfigReader$$REF_RE = (new StringOps(Predef$.MODULE$.augmentString("\\$\\{(?:(\\w+?):)?(\\S+?)\\}"))).r();
   }
}
