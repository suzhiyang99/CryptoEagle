package org.apache.deltaspike.core.impl.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.spi.config.ConfigSource;

public class DeltaSpikeConfigInfo implements DeltaSpikeConfigInfoMBean {
   private final ClassLoader appConfigClassLoader;

   public DeltaSpikeConfigInfo(ClassLoader appConfigClassLoader) {
      this.appConfigClassLoader = appConfigClassLoader;
   }

   public String[] getConfigSourcesAsString() {
      ClassLoader originalCl = Thread.currentThread().getContextClassLoader();

      String[] var11;
      try {
         Thread.currentThread().setContextClassLoader(this.appConfigClassLoader);
         ConfigSource[] configSources = ConfigResolver.getConfigSources();
         List<String> configSourceInfo = new ArrayList();

         for(ConfigSource configSource : configSources) {
            configSourceInfo.add(Integer.toString(configSource.getOrdinal()) + " - " + configSource.getConfigName());
         }

         var11 = (String[])configSourceInfo.toArray(new String[configSourceInfo.size()]);
      } finally {
         Thread.currentThread().setContextClassLoader(originalCl);
      }

      return var11;
   }

   public String[] getConfigEntriesAsString() {
      ClassLoader originalCl = Thread.currentThread().getContextClassLoader();

      String[] var9;
      try {
         Thread.currentThread().setContextClassLoader(this.appConfigClassLoader);
         List<ConfigEntry> configEntries = this.calculateConfigEntries();
         String[] configArray = new String[configEntries.size()];

         for(int i = 0; i < configEntries.size(); ++i) {
            ConfigEntry configEntry = (ConfigEntry)configEntries.get(i);
            configArray[i] = configEntry.getKey() + " = " + configEntry.getValue() + " - picked up from: " + configEntry.getFromConfigSource();
         }

         var9 = configArray;
      } finally {
         Thread.currentThread().setContextClassLoader(originalCl);
      }

      return var9;
   }

   public TabularData getConfigEntries() {
      ClassLoader originalCl = Thread.currentThread().getContextClassLoader();

      TabularDataSupport var20;
      try {
         Thread.currentThread().setContextClassLoader(this.appConfigClassLoader);
         List<ConfigEntry> configEntries = this.calculateConfigEntries();
         String[] configArray = new String[configEntries.size()];

         for(int i = 0; i < configEntries.size(); ++i) {
            ConfigEntry configEntry = (ConfigEntry)configEntries.get(i);
            configArray[i] = configEntry.getKey() + " = " + configEntry.getValue() + " - picked up from: " + configEntry.getFromConfigSource();
         }

         String typeName = "ConfigEntries";
         OpenType<?>[] types = new OpenType[]{SimpleType.STRING, SimpleType.STRING, SimpleType.STRING};
         String[] keys = new String[]{"Key", "Value", "fromConfigSource"};
         CompositeType ct = new CompositeType(typeName, typeName, keys, keys, types);
         TabularType type = new TabularType(typeName, typeName, ct, keys);
         TabularDataSupport configEntryInfo = new TabularDataSupport(type);
         ConfigSource[] configSources = ConfigResolver.getConfigSources();

         for(ConfigEntry configEntry : configEntries) {
            configEntryInfo.put(new CompositeDataSupport(ct, keys, new Object[]{configEntry.getKey(), configEntry.getValue(), configEntry.getFromConfigSource()}));
         }

         var20 = configEntryInfo;
      } catch (OpenDataException e) {
         throw new RuntimeException(e);
      } finally {
         Thread.currentThread().setContextClassLoader(originalCl);
      }

      return var20;
   }

   public TabularData getConfigSources() {
      ClassLoader originalCl = Thread.currentThread().getContextClassLoader();

      TabularDataSupport var18;
      try {
         Thread.currentThread().setContextClassLoader(this.appConfigClassLoader);
         String typeName = "ConfigSources";
         OpenType<?>[] types = new OpenType[]{SimpleType.INTEGER, SimpleType.STRING};
         String[] keys = new String[]{"Ordinal", "ConfigSource"};
         CompositeType ct = new CompositeType(typeName, typeName, keys, keys, types);
         TabularType type = new TabularType(typeName, typeName, ct, keys);
         TabularDataSupport configSourceInfo = new TabularDataSupport(type);
         ConfigSource[] configSources = ConfigResolver.getConfigSources();

         for(ConfigSource configSource : configSources) {
            configSourceInfo.put(new CompositeDataSupport(ct, keys, new Object[]{configSource.getOrdinal(), configSource.getConfigName()}));
         }

         var18 = configSourceInfo;
      } catch (OpenDataException e) {
         throw new RuntimeException(e);
      } finally {
         Thread.currentThread().setContextClassLoader(originalCl);
      }

      return var18;
   }

   private List<ConfigEntry> calculateConfigEntries() {
      Map<String, String> allProperties = ConfigResolver.getAllProperties();
      List<ConfigEntry> configEntries = new ArrayList(allProperties.size());
      ConfigSource[] configSources = ConfigResolver.getConfigSources();

      for(Map.Entry<String, String> configEntry : allProperties.entrySet()) {
         String key = (String)configEntry.getKey();
         String value = ConfigResolver.filterConfigValueForLog(key, ConfigResolver.getProjectStageAwarePropertyValue(key));
         String fromConfigSource = this.getFromConfigSource(configSources, key);
         configEntries.add(new ConfigEntry(key, value, fromConfigSource));
      }

      return configEntries;
   }

   private String getFromConfigSource(ConfigSource[] configSources, String key) {
      for(ConfigSource configSource : configSources) {
         if (configSource.getPropertyValue(key) != null) {
            return configSource.getConfigName();
         }
      }

      return null;
   }

   private class ConfigEntry {
      private final String key;
      private final String value;
      private final String fromConfigSource;

      public ConfigEntry(String key, String value, String fromConfigSource) {
         this.key = key;
         this.value = value;
         this.fromConfigSource = fromConfigSource;
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }

      public String getFromConfigSource() {
         return this.fromConfigSource;
      }
   }
}
