package org.apache.spark.internal.config;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class ConfigBuilder$ extends AbstractFunction1<String, ConfigBuilder> implements Serializable {
   public static final ConfigBuilder$ MODULE$;

   static {
      new ConfigBuilder$();
   }

   public final String toString() {
      return "ConfigBuilder";
   }

   public ConfigBuilder apply(String key) {
      return new ConfigBuilder(key);
   }

   public Option<String> unapply(ConfigBuilder x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.key()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ConfigBuilder$() {
      MODULE$ = this;
   }
}
