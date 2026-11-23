package org.apache.openejb.resource.activemq;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerFactoryHandler;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.PersistenceAdapterFactory;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.URISupport;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.xbean.propertyeditor.PropertyEditorException;
import org.apache.xbean.propertyeditor.PropertyEditors;
import org.apache.xbean.recipe.ObjectRecipe;

public class ActiveMQ5Factory implements BrokerFactoryHandler {
   private static Properties properties;
   protected static final Map<URI, BrokerService> brokers = new HashMap();
   private static Throwable throwable;
   private static final AtomicBoolean started = new AtomicBoolean(false);

   public static void setThreadProperties(Properties p) {
      properties = p;
   }

   public synchronized BrokerService createBroker(URI brokerURI) throws Exception {
      Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQ5Factory.class).getChildLogger("service").info("ActiveMQ5Factory creating broker");
      final BrokerService broker = (BrokerService)brokers.get(brokerURI);
      if (null == broker || !broker.isStarted()) {
         Properties properties = this.getLowerCaseProperties();
         URISupport.CompositeData compositeData = URISupport.parseComposite(new URI(brokerURI.getRawSchemeSpecificPart()));
         Map<String, String> params = new HashMap(compositeData.getParameters());
         PersistenceAdapter persistenceAdapter;
         if ("true".equals(params.remove("usekahadb"))) {
            persistenceAdapter = createPersistenceAdapter("org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter", "kahadb", params);
         } else if ("true".equals(params.remove("useleveldb"))) {
            persistenceAdapter = createPersistenceAdapter("org.apache.activemq.store.leveldb.LevelDBPersistenceAdapter", "leveldb", params);
         } else if (params.get("persistenceadapter") != null) {
            String adapter = (String)params.remove("persistenceadapter");
            persistenceAdapter = createPersistenceAdapter(adapter, "persistence", params);
         } else {
            persistenceAdapter = null;
         }

         String systemUsage = (String)params.remove("systemUsage");
         SystemUsage systemUsageInstance;
         if ("true".equalsIgnoreCase(systemUsage)) {
            systemUsageInstance = this.newSystemUsage(params);
         } else {
            systemUsageInstance = null;
         }

         BrokerPlugin[] plugins = this.createPlugins(params);
         URI uri = new URI(cleanUpUri(brokerURI.getRawSchemeSpecificPart(), compositeData.getParameters(), params));
         broker = "broker".equals(uri.getScheme()) ? this.newDefaultBroker(uri) : BrokerFactory.createBroker(uri);
         if (plugins != null) {
            broker.setPlugins(plugins);
         }

         if (systemUsageInstance != null) {
            broker.setSystemUsage(systemUsageInstance);
         }

         brokers.put(brokerURI, broker);
         if (persistenceAdapter != null) {
            broker.setPersistenceAdapter(persistenceAdapter);
            broker.setPersistenceFactory(new ProvidedPersistenceAdapterPersistenceAdapterFactory(persistenceAdapter));
            broker.setPersistent(true);
            this.tomeeConfig(broker);
         } else {
            boolean notXbean = !uri.getScheme().toLowerCase(Locale.ENGLISH).startsWith("xbean");
            if (notXbean) {
               Object value = properties.get("datasource");
               if (String.class.isInstance(value) && value.toString().length() == 0) {
                  value = null;
               }

               DataSource dataSource;
               if (value != null) {
                  if (DataSource.class.isInstance(value)) {
                     dataSource = (DataSource)DataSource.class.cast(value);
                  } else {
                     if (!String.class.isInstance(value)) {
                        throw new IllegalArgumentException("Unexpected datasource definition: " + value);
                     }

                     String resouceId = (String)String.class.cast(value);

                     try {
                        ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
                        Context context = containerSystem.getJNDIContext();
                        Object obj = context.lookup("openejb/Resource/" + resouceId);
                        if (!(obj instanceof DataSource)) {
                           throw new IllegalArgumentException("Resource with id " + resouceId + " is not a DataSource, but is " + obj.getClass().getName());
                        }

                        dataSource = (DataSource)obj;
                     } catch (NamingException var20) {
                        throw new IllegalArgumentException("Unknown datasource " + resouceId);
                     }
                  }
               } else {
                  dataSource = null;
               }

               if (null != dataSource) {
                  JDBCPersistenceAdapter adapter = new JDBCPersistenceAdapter();
                  if (properties.containsKey("usedatabaselock")) {
                     adapter.setUseLock(Boolean.parseBoolean(properties.getProperty("usedatabaselock", "true")));
                  }

                  adapter.setDataSource(dataSource);
                  broker.setPersistent(true);
                  broker.setPersistenceAdapter(adapter);
               } else {
                  broker.setPersistenceAdapter(new MemoryPersistenceAdapter());
               }

               this.tomeeConfig(broker);
            }
         }

         broker.setUseShutdownHook(false);
         broker.setSystemExitOnShutdown(false);
         broker.setStartAsync(false);
         Thread start = new Thread("ActiveMQFactory start and checkpoint") {
            public void run() {
               Thread.currentThread().setContextClassLoader(org.apache.activemq.ra.ActiveMQResourceAdapter.class.getClassLoader());

               try {
                  if (!broker.isStarted()) {
                     Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQ5Factory.class).getChildLogger("service").info("Starting ActiveMQ BrokerService");
                     broker.start();
                  }

                  broker.waitUntilStarted();
                  Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQ5Factory.class).getChildLogger("service").info("Starting ActiveMQ checkpoint");
                  broker.getPersistenceAdapter().checkpoint(true);
                  ActiveMQ5Factory.started.set(true);
               } catch (Throwable t) {
                  ActiveMQ5Factory.throwable = t;
               }

            }
         };
         int timeout = 30000;

         try {
            timeout = Integer.parseInt(properties.getProperty("startuptimeout", "30000"));
            Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQ5Factory.class).getChildLogger("service").info("Using ActiveMQ startup timeout of " + timeout + "ms");
         } catch (Throwable var19) {
         }

         start.setDaemon(true);
         start.start();

         try {
            start.join((long)timeout);
         } catch (InterruptedException var18) {
         }

         if (null != throwable) {
            Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQ5Factory.class).getChildLogger("service").error("ActiveMQ failed to start broker", throwable);
         } else if (started.get()) {
            Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQ5Factory.class).getChildLogger("service").info("ActiveMQ broker started");
         } else {
            Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQ5Factory.class).getChildLogger("service").warning("ActiveMQ failed to start broker within " + timeout + " seconds - It may be unusable");
         }
      }

      return broker;
   }

   private SystemUsage newSystemUsage(Map<String, String> params) {
      SystemUsage systemUsage = new SystemUsage();
      String memory = (String)params.remove("systemUsage.memory.limit");
      if (memory != null) {
         systemUsage.getMemoryUsage().setLimit((long)Integer.parseInt(memory.trim()));
      } else {
         systemUsage.getMemoryUsage().setLimit(1073741824L);
      }

      memory = (String)params.remove("systemUsage.temp.limit");
      if (memory != null) {
         systemUsage.getTempUsage().setLimit((long)Integer.parseInt(memory.trim()));
      } else {
         systemUsage.getTempUsage().setLimit(53687091200L);
      }

      memory = (String)params.remove("systemUsage.store.limit");
      if (memory != null) {
         systemUsage.getStoreUsage().setLimit((long)Integer.parseInt(memory.trim()));
      } else {
         systemUsage.getStoreUsage().setLimit(107374182400L);
      }

      memory = (String)params.remove("systemUsage.scheduler.limit");
      if (memory != null) {
         systemUsage.getJobSchedulerUsage().setLimit((long)Integer.parseInt(memory.trim()));
      } else {
         systemUsage.getJobSchedulerUsage().setLimit(1073741824L);
      }

      return systemUsage;
   }

   private BrokerService newDefaultBroker(URI uri) throws Exception {
      URISupport.CompositeData compositeData = URISupport.parseComposite(uri);
      Map<String, String> params = new HashMap(compositeData.getParameters());
      BrokerService brokerService = this.newPatchedBrokerService();
      IntrospectionSupport.setProperties(brokerService, params);
      if (!params.isEmpty()) {
         String msg = "There are " + params.size() + " Broker options that couldn't be set on the BrokerService. Check the options are spelled correctly. Unknown parameters=[" + params + "]. This BrokerService cannot be started.";
         throw new IllegalArgumentException(msg);
      } else {
         if (compositeData.getPath() != null) {
            brokerService.setBrokerName(compositeData.getPath());
         }

         for(URI component : compositeData.getComponents()) {
            if ("network".equals(component.getScheme())) {
               brokerService.addNetworkConnector(component.getSchemeSpecificPart());
            } else if ("proxy".equals(component.getScheme())) {
               brokerService.addProxyConnector(component.getSchemeSpecificPart());
            } else {
               brokerService.addConnector(component);
            }
         }

         return brokerService;
      }
   }

   private BrokerService newPatchedBrokerService() {
      return new BrokerService() {
         public NetworkConnector addNetworkConnector(URI discoveryAddress) throws Exception {
            NetworkConnector connector = new DiscoveryNetworkConnector(discoveryAddress);

            try {
               Map<String, String> props = URISupport.parseParameters(discoveryAddress);
               if (!props.containsKey("skipConnector")) {
                  IntrospectionSupport.setProperties(connector, props);
               }
            } catch (URISyntaxException e) {
               Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQ5Factory.class).getChildLogger("service").debug(e.getMessage());
            }

            return this.addNetworkConnector(connector);
         }
      };
   }

   private BrokerPlugin[] createPlugins(Map<String, String> params) {
      String plugins = (String)params.remove("amq.plugins");
      if (plugins == null) {
         return null;
      } else {
         Collection<BrokerPlugin> instances = new LinkedList();

         for(String p : plugins.split(" *, *")) {
            if (!p.isEmpty()) {
               String prefix = p + ".";
               ObjectRecipe recipe = new ObjectRecipe((String)params.remove(prefix + "class"));
               Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();

               while(iterator.hasNext()) {
                  Map.Entry<String, String> entry = (Map.Entry)iterator.next();
                  String key = (String)entry.getKey();
                  if (key.startsWith(prefix)) {
                     recipe.setProperty(key.substring(prefix.length()), entry.getValue());
                     iterator.remove();
                  }
               }

               instances.add(BrokerPlugin.class.cast(recipe.create()));
            }
         }

         return (BrokerPlugin[])instances.toArray(new BrokerPlugin[instances.size()]);
      }
   }

   private static String cleanUpUri(String schemeSpecificPart, Map<String, String> parameters, Map<String, String> params) {
      String uri = schemeSpecificPart;

      for(Map.Entry<String, String> entry : parameters.entrySet()) {
         if (!params.containsKey(entry.getKey())) {
            String kv = (String)entry.getKey() + "=" + encodeURI((String)entry.getValue());
            int idx = uri.indexOf(kv);
            if (idx < 0) {
               kv = (String)entry.getKey() + "=" + (String)entry.getValue();
               idx = uri.indexOf(kv);
            }

            if (idx >= 0) {
               int andIdx = idx + kv.length();
               if (andIdx < uri.length() && uri.charAt(andIdx) == '&') {
                  uri = uri.replace(kv + "&", "");
               } else {
                  uri = uri.replace(kv, "");
               }
            }
         }
      }

      return uri;
   }

   private static String encodeURI(String value) {
      try {
         return URLEncoder.encode(value, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         return value;
      }
   }

   private static PersistenceAdapter createPersistenceAdapter(String clazz, String prefix, Map<String, String> params) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, InstantiationException {
      Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(clazz);

      PersistenceAdapter persistenceAdapter;
      for(persistenceAdapter = (PersistenceAdapter)PersistenceAdapter.class.cast(aClass.newInstance()); aClass != null; aClass = aClass.getSuperclass()) {
         for(Method m : aClass.getDeclaredMethods()) {
            if (m.getName().startsWith("set") && m.getParameterTypes().length == 1 && Modifier.isPublic(m.getModifiers())) {
               String key = prefix + "." + m.getName().substring(3).toLowerCase(Locale.ENGLISH);
               Object field = params.remove(key);
               if (field != null) {
                  try {
                     Object toSet = PropertyEditors.getValue(m.getParameterTypes()[0], field.toString());
                     m.invoke(persistenceAdapter, toSet);
                  } catch (PropertyEditorException cantConvertException) {
                     throw new IllegalArgumentException("can't convert " + field + " for " + m.getName(), cantConvertException);
                  }
               }
            }
         }
      }

      return persistenceAdapter;
   }

   private void tomeeConfig(BrokerService broker) {
      broker.setUseLoggingForShutdownErrors(Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQ5Factory.class).isErrorEnabled());
   }

   private Properties getLowerCaseProperties() {
      Properties newProperties = new Properties();
      if (properties != null) {
         for(Map.Entry<Object, Object> entry : properties.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
               key = ((String)key).toLowerCase();
            }

            newProperties.put(key, entry.getValue());
         }
      }

      return newProperties;
   }

   public Collection<BrokerService> getBrokers() {
      return brokers.values();
   }

   private static class ProvidedPersistenceAdapterPersistenceAdapterFactory implements PersistenceAdapterFactory {
      private final PersistenceAdapter instance;

      public ProvidedPersistenceAdapterPersistenceAdapterFactory(PersistenceAdapter persistenceAdapter) {
         this.instance = persistenceAdapter;
      }

      public PersistenceAdapter createPersistenceAdapter() throws IOException {
         return this.instance;
      }
   }
}
