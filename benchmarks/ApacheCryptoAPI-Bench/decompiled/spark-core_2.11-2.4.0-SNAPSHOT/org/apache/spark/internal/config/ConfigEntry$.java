package org.apache.spark.internal.config;

import java.util.concurrent.ConcurrentHashMap;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;

public final class ConfigEntry$ {
   public static final ConfigEntry$ MODULE$;
   private final String UNDEFINED;
   private final ConcurrentHashMap<String, ConfigEntry<?>> knownConfigs;

   static {
      new ConfigEntry$();
   }

   public String UNDEFINED() {
      return this.UNDEFINED;
   }

   private ConcurrentHashMap<String, ConfigEntry<?>> knownConfigs() {
      return this.knownConfigs;
   }

   public void registerEntry(ConfigEntry<?> entry) {
      ConfigEntry existing = (ConfigEntry)this.knownConfigs().putIfAbsent(entry.key(), entry);
      Predef$.MODULE$.require(existing == null, new Serializable(entry) {
         public static final long serialVersionUID = 0L;
         private final ConfigEntry entry$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Config entry ", " already registered!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.entry$1.key()}));
         }

         public {
            this.entry$1 = entry$1;
         }
      });
   }

   public ConfigEntry<?> findEntry(String key) {
      return (ConfigEntry)this.knownConfigs().get(key);
   }

   private ConfigEntry$() {
      MODULE$ = this;
      this.UNDEFINED = "<undefined>";
      this.knownConfigs = new ConcurrentHashMap();
   }
}
