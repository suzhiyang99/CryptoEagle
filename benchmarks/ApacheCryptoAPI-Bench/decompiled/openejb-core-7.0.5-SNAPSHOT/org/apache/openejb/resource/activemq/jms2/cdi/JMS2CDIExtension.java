package org.apache.openejb.resource.activemq.jms2.cdi;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSPasswordCredential;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.JMSSessionMode;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.TransactionScoped;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.assembler.classic.OpenEjbConfiguration;
import org.apache.openejb.assembler.classic.ResourceInfo;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class JMS2CDIExtension implements Extension {
   public void addContextProducer(@Observes BeforeBeanDiscovery beforeBeanDiscovery, BeanManager beanManager) {
      beforeBeanDiscovery.addAnnotatedType(beanManager.createAnnotatedType(ContextProducer.class));
      beforeBeanDiscovery.addAnnotatedType(beanManager.createAnnotatedType(RequestAutoContextDestruction.class));
      beforeBeanDiscovery.addAnnotatedType(beanManager.createAnnotatedType(TransactionAutoContextDestruction.class));
   }

   @ApplicationScoped
   public static class ContextProducer {
      @Inject
      private RequestAutoContextDestruction requestScoped;
      @Inject
      private TransactionAutoContextDestruction transactionScoped;

      @Produces
      public JMSContext context(InjectionPoint ip) {
         return new InternalJMSContext(this.newKey(ip), this.requestScoped, this.transactionScoped);
      }

      private Key newKey(InjectionPoint ip) {
         Annotated annotated = ip.getAnnotated();
         JMSConnectionFactory jmsConnectionFactory = (JMSConnectionFactory)annotated.getAnnotation(JMSConnectionFactory.class);
         JMSSessionMode sessionMode = (JMSSessionMode)annotated.getAnnotation(JMSSessionMode.class);
         JMSPasswordCredential credential = (JMSPasswordCredential)annotated.getAnnotation(JMSPasswordCredential.class);
         String jndi = "openejb:Resource/" + (jmsConnectionFactory == null ? this.findAnyConnectionFactory() : this.findMatchingConnectionFactory(jmsConnectionFactory.value()));
         return new Key(jndi, credential != null ? credential.userName() : null, credential != null ? credential.password() : null, sessionMode != null ? sessionMode.value() : null);
      }

      private String findMatchingConnectionFactory(String value) {
         OpenEjbConfiguration component = (OpenEjbConfiguration)SystemInstance.get().getComponent(OpenEjbConfiguration.class);
         if (component != null && component.facilities != null) {
            for(ResourceInfo ri : component.facilities.resources) {
               if (ri.types.contains("javax.jms.ConnectionFactory") && ri.id.equals(value)) {
                  return ri.id;
               }
            }

            for(ResourceInfo ri : component.facilities.resources) {
               if (ri.types.contains("javax.jms.ConnectionFactory") && ri.id.endsWith(value)) {
                  return ri.id;
               }
            }
         }

         throw new IllegalArgumentException("No connection factory found, either use @JMSConnectionFactory JMSContext or define a connection factory");
      }

      private String findAnyConnectionFactory() {
         OpenEjbConfiguration component = (OpenEjbConfiguration)SystemInstance.get().getComponent(OpenEjbConfiguration.class);
         if (component != null && component.facilities != null) {
            for(ResourceInfo ri : component.facilities.resources) {
               if (ri.types.contains("javax.jms.ConnectionFactory")) {
                  return ri.id;
               }
            }

            return "DefaultJMSConnectionFactory";
         } else {
            throw new IllegalArgumentException("No connection factory found, either use @JMSConnectionFactory JMSContext or define a connection factory");
         }
      }
   }

   public abstract static class AutoContextDestruction implements Serializable {
      private transient Map<Key, JMSContext> contexts = new ConcurrentHashMap();

      public void push(Key key, JMSContext c) {
         this.contexts.put(key, c);
      }

      public JMSContext find(Key key) {
         return (JMSContext)this.contexts.get(key);
      }

      @PreDestroy
      private void destroy() {
         if (this.contexts != null) {
            JMSRuntimeException jre = null;

            for(JMSContext c : this.contexts.values()) {
               try {
                  c.close();
               } catch (JMSRuntimeException e) {
                  jre = e;
               }
            }

            if (jre != null) {
               throw jre;
            }
         }

      }
   }

   @RequestScoped
   public static class RequestAutoContextDestruction extends AutoContextDestruction {
   }

   @TransactionScoped
   public static class TransactionAutoContextDestruction extends AutoContextDestruction {
   }

   public static class Key implements Serializable {
      private volatile ConnectionFactory connectionFactoryInstance;
      private final String connectionFactory;
      private final String username;
      private final String password;
      private final Integer session;
      private final int hash;

      public Key(String connectionFactory, String username, String password, Integer session) {
         this.connectionFactory = connectionFactory;
         this.username = username;
         this.password = password;
         this.session = session;
         int result = connectionFactory != null ? connectionFactory.hashCode() : 0;
         result = 31 * result + (username != null ? username.hashCode() : 0);
         result = 31 * result + (password != null ? password.hashCode() : 0);
         result = 31 * result + (session != null ? session.hashCode() : 0);
         this.hash = result;
      }

      private ConnectionFactory connectionFactory() {
         if (this.connectionFactoryInstance != null) {
            return this.connectionFactoryInstance;
         } else {
            synchronized(this) {
               if (this.connectionFactoryInstance != null) {
                  return this.connectionFactoryInstance;
               } else {
                  ConnectionFactory var10000;
                  try {
                     var10000 = this.connectionFactoryInstance = (ConnectionFactory)ConnectionFactory.class.cast(((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup(this.connectionFactory));
                  } catch (NamingException e) {
                     throw new JMSRuntimeException(e.getMessage(), (String)null, e);
                  }

                  return var10000;
               }
            }
         }
      }

      public JMSContext create() {
         if (this.username != null && this.session != null) {
            return this.connectionFactory().createContext(this.username, this.password, this.session);
         } else if (this.username != null) {
            return this.connectionFactory().createContext(this.username, this.password);
         } else {
            return this.session != null ? this.connectionFactory().createContext(this.session) : this.connectionFactory().createContext();
         }
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Key key = (Key)Key.class.cast(o);
            boolean var10000;
            if (this.connectionFactory != null) {
               var10000 = this.connectionFactory.equals(key.connectionFactory);
            } else {
               label53: {
                  if (key.connectionFactory == null) {
                     if (this.username != null) {
                        if (this.username.equals(key.username)) {
                           break label53;
                        }
                     } else if (key.username == null) {
                        if (this.password != null) {
                           if (this.password.equals(key.password)) {
                              break label53;
                           }
                        } else if (key.password == null) {
                           if (this.session != null) {
                              if (this.session.equals(key.session)) {
                                 break label53;
                              }
                           } else if (key.session == null) {
                              break label53;
                           }
                        }
                     }
                  }

                  var10000 = false;
                  return var10000;
               }

               var10000 = true;
            }

            return var10000;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.hash;
      }
   }

   public static class InternalJMSContext implements JMSContext, Serializable {
      private final Key key;
      private final RequestAutoContextDestruction requestStorage;
      private final TransactionAutoContextDestruction transactionStorage;

      public InternalJMSContext(Key key, RequestAutoContextDestruction requestScoped, TransactionAutoContextDestruction transactionScoped) {
         this.key = key;
         this.requestStorage = requestScoped;
         this.transactionStorage = transactionScoped;
      }

      private synchronized JMSContext context() {
         return this.inTx() ? this.findOrCreateContext(this.transactionStorage) : this.findOrCreateContext(this.requestStorage);
      }

      private JMSContext findOrCreateContext(AutoContextDestruction storage) {
         JMSContext jmsContext = storage.find(this.key);
         if (jmsContext == null) {
            jmsContext = this.key.create();
            storage.push(this.key, jmsContext);
         }

         return jmsContext;
      }

      private boolean inTx() {
         try {
            return OpenEJB.getTransactionManager().getTransaction() != null;
         } catch (SystemException var2) {
            return false;
         }
      }

      public void acknowledge() {
         this.context().acknowledge();
      }

      public void close() {
         this.context().close();
      }

      public void commit() {
         this.context().commit();
      }

      public QueueBrowser createBrowser(Queue queue) {
         return this.context().createBrowser(queue);
      }

      public QueueBrowser createBrowser(Queue queue, String messageSelector) {
         return this.context().createBrowser(queue, messageSelector);
      }

      public BytesMessage createBytesMessage() {
         return this.context().createBytesMessage();
      }

      public JMSConsumer createConsumer(Destination destination) {
         return this.context().createConsumer(destination);
      }

      public JMSConsumer createConsumer(Destination destination, String messageSelector) {
         return this.context().createConsumer(destination, messageSelector);
      }

      public JMSConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) {
         return this.context().createConsumer(destination, messageSelector, noLocal);
      }

      public JMSContext createContext(int sessionMode) {
         return this.context().createContext(sessionMode);
      }

      public JMSConsumer createDurableConsumer(Topic topic, String name) {
         return this.context().createDurableConsumer(topic, name);
      }

      public JMSConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) {
         return this.context().createDurableConsumer(topic, name, messageSelector, noLocal);
      }

      public MapMessage createMapMessage() {
         return this.context().createMapMessage();
      }

      public Message createMessage() {
         return this.context().createMessage();
      }

      public ObjectMessage createObjectMessage() {
         return this.context().createObjectMessage();
      }

      public ObjectMessage createObjectMessage(Serializable object) {
         return this.context().createObjectMessage(object);
      }

      public JMSProducer createProducer() {
         return this.context().createProducer();
      }

      public Queue createQueue(String queueName) {
         return this.context().createQueue(queueName);
      }

      public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) {
         return this.context().createSharedConsumer(topic, sharedSubscriptionName);
      }

      public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) {
         return this.context().createSharedConsumer(topic, sharedSubscriptionName, messageSelector);
      }

      public JMSConsumer createSharedDurableConsumer(Topic topic, String name) {
         return this.context().createSharedDurableConsumer(topic, name);
      }

      public JMSConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) {
         return this.context().createSharedDurableConsumer(topic, name, messageSelector);
      }

      public StreamMessage createStreamMessage() {
         return this.context().createStreamMessage();
      }

      public TemporaryQueue createTemporaryQueue() {
         return this.context().createTemporaryQueue();
      }

      public TemporaryTopic createTemporaryTopic() {
         return this.context().createTemporaryTopic();
      }

      public TextMessage createTextMessage() {
         return this.context().createTextMessage();
      }

      public TextMessage createTextMessage(String text) {
         return this.context().createTextMessage(text);
      }

      public Topic createTopic(String topicName) {
         return this.context().createTopic(topicName);
      }

      public boolean getAutoStart() {
         return this.context().getAutoStart();
      }

      public String getClientID() {
         return this.context().getClientID();
      }

      public ExceptionListener getExceptionListener() {
         return this.context().getExceptionListener();
      }

      public ConnectionMetaData getMetaData() {
         return this.context().getMetaData();
      }

      public int getSessionMode() {
         return this.context().getSessionMode();
      }

      public boolean getTransacted() {
         return this.context().getTransacted();
      }

      public void recover() {
         this.context().recover();
      }

      public void rollback() {
         this.context().rollback();
      }

      public void setAutoStart(boolean autoStart) {
         this.context().setAutoStart(autoStart);
      }

      public void setClientID(String clientID) {
         this.context().setClientID(clientID);
      }

      public void setExceptionListener(ExceptionListener listener) {
         this.context().setExceptionListener(listener);
      }

      public void start() {
         this.context().start();
      }

      public void stop() {
         this.context().stop();
      }

      public void unsubscribe(String name) {
         this.context().unsubscribe(name);
      }
   }
}
