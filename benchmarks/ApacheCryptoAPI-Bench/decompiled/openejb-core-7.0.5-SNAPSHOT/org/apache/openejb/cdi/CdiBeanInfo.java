package org.apache.openejb.cdi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.openejb.Injection;
import org.apache.openejb.jee.DataSource;
import org.apache.openejb.jee.EjbLocalRef;
import org.apache.openejb.jee.EjbRef;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.JMSConnectionFactory;
import org.apache.openejb.jee.JMSDestination;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.KeyedCollection;
import org.apache.openejb.jee.LifecycleCallback;
import org.apache.openejb.jee.MessageDestinationRef;
import org.apache.openejb.jee.PersistenceContextRef;
import org.apache.openejb.jee.PersistenceUnitRef;
import org.apache.openejb.jee.ResourceEnvRef;
import org.apache.openejb.jee.ResourceRef;
import org.apache.openejb.jee.SecurityIdentity;
import org.apache.openejb.jee.SecurityRoleRef;
import org.apache.openejb.jee.ServiceRef;

public class CdiBeanInfo implements JndiConsumer {
   protected KeyedCollection<String, EnvEntry> envEntry;
   protected KeyedCollection<String, EjbRef> ejbRef;
   protected KeyedCollection<String, EjbLocalRef> ejbLocalRef;
   protected KeyedCollection<String, ServiceRef> serviceRef;
   protected KeyedCollection<String, ResourceRef> resourceRef;
   protected KeyedCollection<String, ResourceEnvRef> resourceEnvRef;
   protected KeyedCollection<String, MessageDestinationRef> messageDestinationRef;
   protected KeyedCollection<String, PersistenceContextRef> persistenceContextRef;
   protected KeyedCollection<String, PersistenceUnitRef> persistenceUnitRef;
   protected List<LifecycleCallback> postConstruct;
   protected List<LifecycleCallback> preDestroy;
   protected KeyedCollection<String, DataSource> dataSource;
   protected KeyedCollection<String, JMSConnectionFactory> jmsConnectionFactories;
   protected KeyedCollection<String, JMSDestination> jmsDestinations;
   protected List<LifecycleCallback> postActivate;
   protected List<LifecycleCallback> prePassivate;
   protected List<SecurityRoleRef> securityRoleRef;
   protected SecurityIdentity securityIdentity;
   private String beanName;
   private ClassLoader classLoader;
   private List<Injection> injections;
   private List<LifecycleCallback> afterBegin;
   private List<LifecycleCallback> beforeCompletion;
   private List<LifecycleCallback> afterCompletion;
   private Class<?> beanClass;

   public String getBeanName() {
      return this.beanName;
   }

   public List<Injection> getInjections() {
      return this.injections;
   }

   public void setInjections(List<Injection> injections) {
      this.injections = injections;
   }

   public void setBeanName(String beanName) {
      this.beanName = beanName;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public void setBeanClass(Class<?> beanClass) {
      this.beanClass = beanClass;
   }

   public Collection<EnvEntry> getEnvEntry() {
      if (this.envEntry == null) {
         this.envEntry = new KeyedCollection();
      }

      return this.envEntry;
   }

   public Map<String, EnvEntry> getEnvEntryMap() {
      if (this.envEntry == null) {
         this.envEntry = new KeyedCollection();
      }

      return this.envEntry.toMap();
   }

   public Collection<EjbRef> getEjbRef() {
      if (this.ejbRef == null) {
         this.ejbRef = new KeyedCollection();
      }

      return this.ejbRef;
   }

   public Map<String, EjbRef> getEjbRefMap() {
      if (this.ejbRef == null) {
         this.ejbRef = new KeyedCollection();
      }

      return this.ejbRef.toMap();
   }

   public Collection<EjbLocalRef> getEjbLocalRef() {
      if (this.ejbLocalRef == null) {
         this.ejbLocalRef = new KeyedCollection();
      }

      return this.ejbLocalRef;
   }

   public Map<String, EjbLocalRef> getEjbLocalRefMap() {
      if (this.ejbLocalRef == null) {
         this.ejbLocalRef = new KeyedCollection();
      }

      return this.ejbLocalRef.toMap();
   }

   public Collection<ServiceRef> getServiceRef() {
      if (this.serviceRef == null) {
         this.serviceRef = new KeyedCollection();
      }

      return this.serviceRef;
   }

   public Map<String, ServiceRef> getServiceRefMap() {
      if (this.serviceRef == null) {
         this.serviceRef = new KeyedCollection();
      }

      return this.serviceRef.toMap();
   }

   public Collection<ResourceRef> getResourceRef() {
      if (this.resourceRef == null) {
         this.resourceRef = new KeyedCollection();
      }

      return this.resourceRef;
   }

   public Map<String, ResourceRef> getResourceRefMap() {
      if (this.resourceRef == null) {
         this.resourceRef = new KeyedCollection();
      }

      return this.resourceRef.toMap();
   }

   public Collection<ResourceEnvRef> getResourceEnvRef() {
      if (this.resourceEnvRef == null) {
         this.resourceEnvRef = new KeyedCollection();
      }

      return this.resourceEnvRef;
   }

   public Map<String, ResourceEnvRef> getResourceEnvRefMap() {
      if (this.resourceEnvRef == null) {
         this.resourceEnvRef = new KeyedCollection();
      }

      return this.resourceEnvRef.toMap();
   }

   public Collection<MessageDestinationRef> getMessageDestinationRef() {
      if (this.messageDestinationRef == null) {
         this.messageDestinationRef = new KeyedCollection();
      }

      return this.messageDestinationRef;
   }

   public Map<String, MessageDestinationRef> getMessageDestinationRefMap() {
      if (this.messageDestinationRef == null) {
         this.messageDestinationRef = new KeyedCollection();
      }

      return this.messageDestinationRef.toMap();
   }

   public Collection<PersistenceContextRef> getPersistenceContextRef() {
      if (this.persistenceContextRef == null) {
         this.persistenceContextRef = new KeyedCollection();
      }

      return this.persistenceContextRef;
   }

   public Map<String, PersistenceContextRef> getPersistenceContextRefMap() {
      if (this.persistenceContextRef == null) {
         this.persistenceContextRef = new KeyedCollection();
      }

      return this.persistenceContextRef.toMap();
   }

   public Collection<PersistenceUnitRef> getPersistenceUnitRef() {
      if (this.persistenceUnitRef == null) {
         this.persistenceUnitRef = new KeyedCollection();
      }

      return this.persistenceUnitRef;
   }

   public Map<String, PersistenceUnitRef> getPersistenceUnitRefMap() {
      if (this.persistenceUnitRef == null) {
         this.persistenceUnitRef = new KeyedCollection();
      }

      return this.persistenceUnitRef.toMap();
   }

   public List<LifecycleCallback> getPostConstruct() {
      if (this.postConstruct == null) {
         this.postConstruct = new ArrayList();
      }

      return this.postConstruct;
   }

   public List<SecurityRoleRef> getSecurityRoleRef() {
      if (this.securityRoleRef == null) {
         this.securityRoleRef = new ArrayList();
      }

      return this.securityRoleRef;
   }

   public SecurityIdentity getSecurityIdentity() {
      return this.securityIdentity;
   }

   public void setSecurityIdentity(SecurityIdentity value) {
      this.securityIdentity = value;
   }

   public List<LifecycleCallback> getAfterBegin() {
      if (this.afterBegin == null) {
         this.afterBegin = new ArrayList();
      }

      return this.afterBegin;
   }

   public List<LifecycleCallback> getAfterCompletion() {
      if (this.afterCompletion == null) {
         this.afterCompletion = new ArrayList();
      }

      return this.afterCompletion;
   }

   public List<LifecycleCallback> getBeforeCompletion() {
      if (this.beforeCompletion == null) {
         this.beforeCompletion = new ArrayList();
      }

      return this.beforeCompletion;
   }

   public Collection<DataSource> getDataSource() {
      if (this.dataSource == null) {
         this.dataSource = new KeyedCollection();
      }

      return this.dataSource;
   }

   public Map<String, DataSource> getDataSourceMap() {
      if (this.dataSource == null) {
         this.dataSource = new KeyedCollection();
      }

      return this.dataSource.toMap();
   }

   public Collection<JMSConnectionFactory> getJMSConnectionFactories() {
      return this.jmsConnectionFactories == null ? (this.jmsConnectionFactories = new KeyedCollection()) : this.jmsConnectionFactories;
   }

   public Map<String, JMSConnectionFactory> getJMSConnectionFactoriesMap() {
      return ((KeyedCollection)KeyedCollection.class.cast(this.getJMSConnectionFactories())).toMap();
   }

   public Collection<JMSDestination> getJMSDestination() {
      return this.jmsDestinations == null ? (this.jmsDestinations = new KeyedCollection()) : this.jmsDestinations;
   }

   public Map<String, JMSDestination> getJMSDestinationMap() {
      return ((KeyedCollection)KeyedCollection.class.cast(this.getJMSDestination())).toMap();
   }

   public String getJndiConsumerName() {
      return this.beanName;
   }

   public Class<?> getBeanClass() {
      return this.beanClass;
   }
}
