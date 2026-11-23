package org.apache.openejb.config.sys;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.openejb.config.SystemProperty;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"systemProperties", "container", "jndiProvider", "securityService", "transactionManager", "connectionManager", "proxyFactory", "connector", "resource", "deployments", "services"}
)
@XmlRootElement(
   name = "openejb"
)
public class Openejb {
   @XmlElement(
      name = "Container",
      required = true
   )
   protected List<Container> container;
   @XmlElement(
      name = "JndiProvider"
   )
   protected List<JndiProvider> jndiProvider;
   @XmlElement(
      name = "SecurityService"
   )
   protected SecurityService securityService;
   @XmlElement(
      name = "TransactionManager"
   )
   protected TransactionManager transactionManager;
   @XmlElement(
      name = "ConnectionManager"
   )
   protected ConnectionManager connectionManager;
   @XmlElement(
      name = "ProxyFactory"
   )
   protected ProxyFactory proxyFactory;
   @XmlElement(
      name = "Connector"
   )
   protected List<Connector> connector;
   @XmlElement(
      name = "Resource"
   )
   protected List<Resource> resource;
   @XmlElement(
      name = "Deployments"
   )
   protected List<Deployments> deployments;
   @XmlElement(
      name = "Service"
   )
   protected List<Service> services;
   @XmlElement(
      name = "System-Property"
   )
   protected List<SystemProperty> systemProperties;

   public List<Container> getContainer() {
      if (this.container == null) {
         this.container = new ArrayList();
      }

      return this.container;
   }

   public List<JndiProvider> getJndiProvider() {
      if (this.jndiProvider == null) {
         this.jndiProvider = new ArrayList();
      }

      return this.jndiProvider;
   }

   public SecurityService getSecurityService() {
      return this.securityService;
   }

   public void setSecurityService(SecurityService value) {
      this.securityService = value;
   }

   public TransactionManager getTransactionManager() {
      return this.transactionManager;
   }

   public void setTransactionManager(TransactionManager value) {
      this.transactionManager = value;
   }

   public ConnectionManager getConnectionManager() {
      return this.connectionManager;
   }

   public void setConnectionManager(ConnectionManager value) {
      this.connectionManager = value;
   }

   public ProxyFactory getProxyFactory() {
      return this.proxyFactory;
   }

   public void setProxyFactory(ProxyFactory value) {
      this.proxyFactory = value;
   }

   public List<Connector> getConnector() {
      if (this.connector == null) {
         this.connector = new ArrayList();
      }

      return this.connector;
   }

   public List<Resource> getResource() {
      if (this.resource == null) {
         this.resource = new ArrayList();
      }

      List<Connector> connectors = this.getConnector();
      if (connectors.size() > 0) {
         for(Connector connector : connectors) {
            Resource resource = new Resource();
            resource.setJar(connector.getJar());
            resource.setId(connector.getId());
            resource.setType(connector.getType());
            resource.setProvider(connector.getProvider());
            resource.getProperties().putAll(connector.getProperties());
            this.resource.add(resource);
         }

         connectors.clear();
      }

      return this.resource;
   }

   public List<Deployments> getDeployments() {
      if (this.deployments == null) {
         this.deployments = new ArrayList();
      }

      return this.deployments;
   }

   public List<Service> getServices() {
      if (this.services == null) {
         this.services = new ArrayList();
      }

      return this.services;
   }

   public List<SystemProperty> getSystemProperties() {
      if (this.systemProperties == null) {
         this.systemProperties = new LinkedList();
      }

      return this.systemProperties;
   }

   public void add(Object service) {
      if (service instanceof Container) {
         this.getContainer().add((Container)service);
      } else if (service instanceof Connector) {
         this.getConnector().add((Connector)service);
      } else if (service instanceof Resource) {
         this.getResource().add((Resource)service);
      } else if (service instanceof JndiProvider) {
         this.getJndiProvider().add((JndiProvider)service);
      } else if (service instanceof ConnectionManager) {
         this.setConnectionManager((ConnectionManager)service);
      } else if (service instanceof ProxyFactory) {
         this.setProxyFactory((ProxyFactory)service);
      } else if (service instanceof TransactionManager) {
         this.setTransactionManager((TransactionManager)service);
      } else if (service instanceof SecurityService) {
         this.setSecurityService((SecurityService)service);
      } else if (service instanceof Deployments) {
         this.getDeployments().add((Deployments)service);
      } else if (service instanceof Service) {
         this.getServices().add((Service)service);
      } else if (service instanceof SystemProperty) {
         this.getSystemProperties().add((SystemProperty)service);
      }

   }
}
