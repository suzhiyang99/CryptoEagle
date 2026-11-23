package org.apache.openejb.resource.activemq;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.management.ObjectName;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapterInternalException;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.ra.ActiveMQConnectionRequestInfo;
import org.apache.activemq.ra.ActiveMQEndpointActivationKey;
import org.apache.activemq.ra.ActiveMQEndpointWorker;
import org.apache.activemq.ra.ActiveMQManagedConnection;
import org.apache.activemq.ra.MessageActivationSpec;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.mdb.MdbContainer;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.AutoConnectionTracker;
import org.apache.openejb.resource.activemq.jms2.TomEEConnectionFactory;
import org.apache.openejb.resource.activemq.jms2.TomEEManagedConnectionProxy;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.URISupport;
import org.apache.openejb.util.URLs;
import org.apache.openejb.util.reflection.Reflections;

public class ActiveMQResourceAdapter extends org.apache.activemq.ra.ActiveMQResourceAdapter {
   private String dataSource;
   private String useDatabaseLock;
   private String startupTimeout = "60000";
   private BootstrapContext bootstrapContext;
   private final Map<BeanContext, ObjectName> mbeanNames = new ConcurrentHashMap();

   public String getDataSource() {
      return this.dataSource;
   }

   public void setDataSource(String dataSource) {
      this.dataSource = dataSource;
   }

   public void setUseDatabaseLock(String useDatabaseLock) {
      this.useDatabaseLock = useDatabaseLock;
   }

   public int getStartupTimeout() {
      return Integer.parseInt(this.startupTimeout);
   }

   public void setStartupTimeout(Duration startupTimeout) {
      if (startupTimeout.getUnit() == null) {
         startupTimeout.setUnit(TimeUnit.MILLISECONDS);
      }

      this.startupTimeout = String.valueOf(TimeUnit.MILLISECONDS.convert(startupTimeout.getTime(), startupTimeout.getUnit()));
   }

   public void setServerUrl(String url) {
      super.setServerUrl(url);
   }

   public void start(BootstrapContext bootstrapContext) throws ResourceAdapterInternalException {
      this.bootstrapContext = bootstrapContext;
      String brokerXmlConfig = this.getBrokerXmlConfig();
      super.setBrokerXmlConfig((String)null);
      super.start(bootstrapContext);
      Properties properties = new Properties();
      if (null != this.dataSource) {
         properties.put("DataSource", this.dataSource);
      }

      if (null != this.useDatabaseLock) {
         properties.put("UseDatabaseLock", this.useDatabaseLock);
      }

      if (null != this.startupTimeout) {
         properties.put("StartupTimeout", this.startupTimeout);
      }

      if (brokerXmlConfig != null && !brokerXmlConfig.trim().isEmpty()) {
         try {
            if (brokerXmlConfig.startsWith("broker:")) {
               URISupport.CompositeData compositeData = URISupport.parseComposite(URLs.uri(brokerXmlConfig));
               if (!compositeData.getParameters().containsKey("persistent")) {
                  compositeData.getParameters().put("persistent", "false");
               }

               if ("false".equalsIgnoreCase(compositeData.getParameters().get("persistent").toString())) {
                  properties.remove("DataSource");
               }

               this.setBrokerXmlConfig(ActiveMQFactory.getBrokerMetaFile() + compositeData.toURI());
            } else if (brokerXmlConfig.toLowerCase(Locale.ENGLISH).startsWith("xbean:")) {
               this.setBrokerXmlConfig(ActiveMQFactory.getBrokerMetaFile() + brokerXmlConfig);
            }
         } catch (URISyntaxException e) {
            throw new ResourceAdapterInternalException("Invalid BrokerXmlConfig", e);
         }

         this.createInternalBroker(brokerXmlConfig, properties);
      }

   }

   private void createInternalBroker(String brokerXmlConfig, Properties properties) {
      ActiveMQFactory.setThreadProperties(properties);

      try {
         ActiveMQFactory.createBroker(URLs.uri(this.getBrokerXmlConfig())).start();
      } catch (Exception e) {
         Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQResourceAdapter.class).getChildLogger("service").fatal("Failed to start ActiveMQ", (Throwable)e);
      } finally {
         ActiveMQFactory.setThreadProperties((Properties)null);
         if (brokerXmlConfig != null) {
            this.setBrokerXmlConfig(brokerXmlConfig);
         }

      }

   }

   private ActiveMQEndpointWorker getWorker(BeanContext beanContext) throws ResourceException {
      Map<ActiveMQEndpointActivationKey, ActiveMQEndpointWorker> workers = (Map)Map.class.cast(Reflections.get(((MdbContainer)MdbContainer.class.cast(beanContext.getContainer())).getResourceAdapter(), "endpointWorkers"));

      for(Map.Entry<ActiveMQEndpointActivationKey, ActiveMQEndpointWorker> entry : workers.entrySet()) {
         if (((ActiveMQEndpointActivationKey)entry.getKey()).getMessageEndpointFactory() == beanContext.getContainerData()) {
            return (ActiveMQEndpointWorker)entry.getValue();
         }
      }

      throw new IllegalStateException("No worker for " + beanContext.getDeploymentID());
   }

   public BootstrapContext getBootstrapContext() {
      return this.bootstrapContext;
   }

   public void stop() {
      Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQResourceAdapter.class).getChildLogger("service").info("Stopping ActiveMQ");
      Thread stopThread = new Thread("ActiveMQResourceAdapter stop") {
         public void run() {
            try {
               ActiveMQResourceAdapter.this.stopImpl();
            } catch (Throwable t) {
               Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQResourceAdapter.class).getChildLogger("service").error("ActiveMQ shutdown failed", t);
            }

         }
      };
      stopThread.setDaemon(true);
      stopThread.start();
      int timeout = 60000;

      try {
         timeout = Integer.parseInt(this.startupTimeout);
      } catch (Throwable var5) {
      }

      try {
         stopThread.join((long)timeout);
      } catch (InterruptedException ex) {
         Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQResourceAdapter.class).getChildLogger("service").warning("Gave up on ActiveMQ shutdown after " + timeout + "ms", (Throwable)ex);
      }

   }

   public ActiveMQConnection makeConnection(MessageActivationSpec activationSpec) throws JMSException {
      if (TomEEMessageActivationSpec.class.isInstance(activationSpec)) {
         TomEEMessageActivationSpec s = (TomEEMessageActivationSpec)TomEEMessageActivationSpec.class.cast(activationSpec);
         if (s.getConnectionFactoryLookup() != null) {
            try {
               Object lookup = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup("openejb:Resource/" + s.getConnectionFactoryLookup());
               if (!ActiveMQConnectionFactory.class.isInstance(lookup)) {
                  org.apache.activemq.ra.ActiveMQConnectionFactory connectionFactory = (org.apache.activemq.ra.ActiveMQConnectionFactory)org.apache.activemq.ra.ActiveMQConnectionFactory.class.cast(lookup);
                  Connection connection = connectionFactory.createConnection();
                  if (Proxy.isProxyClass(connection.getClass())) {
                     InvocationHandler invocationHandler = Proxy.getInvocationHandler(connection);
                     ActiveMQConnection physicalConnection = this.getActiveMQConnection(activationSpec, invocationHandler);
                     if (physicalConnection != null) {
                        return physicalConnection;
                     }
                  }

                  try {
                     Field handler = connection.getClass().getDeclaredField("this$handler");
                     handler.setAccessible(true);
                     Object o = handler.get(connection);
                     if (InvocationHandler.class.isInstance(o)) {
                        InvocationHandler invocationHandler = (InvocationHandler)InvocationHandler.class.cast(o);
                        ActiveMQConnection physicalConnection = this.getActiveMQConnection(activationSpec, invocationHandler);
                        if (physicalConnection != null) {
                           return physicalConnection;
                        }
                     }
                  } catch (IllegalAccessException | NoSuchFieldException var10) {
                  }

                  return null;
               }
            } catch (ClassCastException cce) {
               throw new IllegalStateException(cce);
            } catch (NamingException e) {
               throw new IllegalArgumentException(e);
            }
         }
      }

      return super.makeConnection(activationSpec);
   }

   private ActiveMQConnection getActiveMQConnection(MessageActivationSpec activationSpec, InvocationHandler invocationHandler) {
      if (AutoConnectionTracker.ConnectionInvocationHandler.class.isInstance(invocationHandler)) {
         Object handle = Reflections.get(invocationHandler, "handle");
         if (TomEEManagedConnectionProxy.class.isInstance(handle)) {
            ActiveMQManagedConnection c = (ActiveMQManagedConnection)ActiveMQManagedConnection.class.cast(Reflections.get(handle, "connection"));
            ActiveMQConnection physicalConnection = (ActiveMQConnection)ActiveMQConnection.class.cast(Reflections.get(c, "physicalConnection"));
            RedeliveryPolicy redeliveryPolicy = activationSpec.redeliveryPolicy();
            if (redeliveryPolicy != null) {
               physicalConnection.setRedeliveryPolicy(redeliveryPolicy);
            }

            return physicalConnection;
         }
      }

      return null;
   }

   protected ActiveMQConnectionFactory createConnectionFactory(ActiveMQConnectionRequestInfo connectionRequestInfo, MessageActivationSpec activationSpec) {
      if (TomEEMessageActivationSpec.class.isInstance(activationSpec)) {
         TomEEMessageActivationSpec s = (TomEEMessageActivationSpec)TomEEMessageActivationSpec.class.cast(activationSpec);
         if (s.getConnectionFactoryLookup() != null) {
            try {
               Object lookup = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup("openejb:Resource/" + s.getConnectionFactoryLookup());
               if (ActiveMQConnectionFactory.class.isInstance(lookup)) {
                  return (ActiveMQConnectionFactory)ActiveMQConnectionFactory.class.cast(lookup);
               }

               return (ActiveMQConnectionFactory)ActiveMQConnectionFactory.class.cast(lookup);
            } catch (NamingException var5) {
               throw new IllegalArgumentException("");
            }
         }
      }

      ActiveMQConnectionFactory factory = new TomEEConnectionFactory();
      connectionRequestInfo.configure(factory, activationSpec);
      return factory;
   }

   private void stopImpl() throws Exception {
      super.stop();
      Collection<BrokerService> brokers = ActiveMQFactory.getBrokers();

      for(Iterator<BrokerService> it = brokers.iterator(); it.hasNext(); it.remove()) {
         BrokerService bs = (BrokerService)it.next();

         try {
            bs.stop();
            bs.waitUntilStopped();
         } catch (Throwable var5) {
         }
      }

      stopScheduler();
      Logger.getInstance(LogCategory.OPENEJB_STARTUP, ActiveMQResourceAdapter.class).getChildLogger("service").info("Stopped ActiveMQ broker");
   }

   private static void stopScheduler() {
      try {
         Class<?> clazz = Class.forName("org.apache.kahadb.util.Scheduler");
         Method method = clazz.getMethod("shutdown");
         method.invoke((Object)null);
      } catch (Throwable var2) {
      }

   }
}
