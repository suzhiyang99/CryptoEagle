package org.apache.openejb.resource.activemq;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.broker.BrokerService;
import org.apache.openejb.OpenEJBRuntimeException;

public class ActiveMQFactory {
   private static final AtomicBoolean initialized = new AtomicBoolean(false);
   private static Method setThreadProperties;
   private static Method createBroker;
   private static Method getBrokers;
   private static Object instance;
   private static String brokerPrefix;

   private static void init() {
      synchronized(initialized) {
         if (!initialized.getAndSet(true)) {
            Class tmp;
            try {
               tmp = Class.forName("org.apache.openejb.resource.activemq.ActiveMQ5Factory");
               brokerPrefix = "amq5factory:";
            } catch (Throwable t1) {
               try {
                  tmp = Class.forName("org.apache.openejb.resource.activemq.ActiveMQ4Factory");
                  brokerPrefix = "amq4factory:";
               } catch (Throwable var10) {
                  throw new OpenEJBRuntimeException("Unable to load ActiveMQFactory: Check ActiveMQ jar files are on classpath", t1);
               }
            }

            Class clazz = tmp;

            try {
               instance = clazz.newInstance();
            } catch (InstantiationException e) {
               throw new OpenEJBRuntimeException("Unable to create ActiveMQFactory instance", e);
            } catch (IllegalAccessException e) {
               throw new OpenEJBRuntimeException("Unable to access ActiveMQFactory instance", e);
            }

            try {
               setThreadProperties = clazz.getDeclaredMethod("setThreadProperties", Properties.class);
            } catch (NoSuchMethodException e) {
               throw new OpenEJBRuntimeException("Unable to create ActiveMQFactory setThreadProperties method", e);
            }

            try {
               createBroker = clazz.getDeclaredMethod("createBroker", URI.class);
            } catch (NoSuchMethodException e) {
               throw new OpenEJBRuntimeException("Unable to create ActiveMQFactory createBroker method", e);
            }

            try {
               getBrokers = clazz.getDeclaredMethod("getBrokers", (Class[])null);
            } catch (NoSuchMethodException e) {
               throw new OpenEJBRuntimeException("Unable to create ActiveMQFactory createBroker method", e);
            }
         }

      }
   }

   public static String getBrokerMetaFile() {
      init();
      return brokerPrefix;
   }

   public static void setThreadProperties(Properties p) {
      init();

      try {
         setThreadProperties.invoke(instance, p);
      } catch (IllegalAccessException e) {
         throw new OpenEJBRuntimeException("ActiveMQFactory.setThreadProperties.IllegalAccessException", e);
      } catch (IllegalArgumentException e) {
         throw new OpenEJBRuntimeException("ActiveMQFactory.setThreadProperties.IllegalArgumentException", e);
      } catch (InvocationTargetException e) {
         throw new OpenEJBRuntimeException("ActiveMQFactory.setThreadProperties.InvocationTargetException", e);
      }
   }

   public static BrokerService createBroker(URI brokerURI) throws Exception {
      init();

      try {
         return (BrokerService)createBroker.invoke(instance, brokerURI);
      } catch (IllegalAccessException e) {
         throw new Exception("ActiveMQFactory.createBroker.IllegalAccessException", e);
      } catch (IllegalArgumentException e) {
         throw new Exception("ActiveMQFactory.createBroker.IllegalArgumentException", e);
      } catch (InvocationTargetException e) {
         throw new Exception("ActiveMQFactory.createBroker.InvocationTargetException", e);
      }
   }

   public static Collection<BrokerService> getBrokers() throws Exception {
      init();

      try {
         return (Collection)getBrokers.invoke(instance, (Object[])null);
      } catch (IllegalAccessException e) {
         throw new Exception("ActiveMQFactory.createBroker.IllegalAccessException", e);
      } catch (IllegalArgumentException e) {
         throw new Exception("ActiveMQFactory.createBroker.IllegalArgumentException", e);
      } catch (InvocationTargetException e) {
         throw new Exception("ActiveMQFactory.createBroker.InvocationTargetException", e);
      }
   }
}
