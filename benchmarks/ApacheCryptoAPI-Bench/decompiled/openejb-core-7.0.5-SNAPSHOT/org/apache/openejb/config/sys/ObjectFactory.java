package org.apache.openejb.config.sys;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
   public Container createContainer() {
      return new Container();
   }

   public ConnectionManager createConnectionManager() {
      return new ConnectionManager();
   }

   public Resource createResource() {
      return new Resource();
   }

   public SecurityService createSecurityService() {
      return new SecurityService();
   }

   public JndiProvider createJndiProvider() {
      return new JndiProvider();
   }

   public Deployments createDeployments() {
      return new Deployments();
   }

   public Connector createConnector() {
      return new Connector();
   }

   public ProxyFactory createProxyFactory() {
      return new ProxyFactory();
   }

   public Openejb createOpenejb() {
      return new Openejb();
   }

   public Tomee createTomee() {
      return new Tomee();
   }

   public TransactionManager createTransactionManager() {
      return new TransactionManager();
   }

   public ServiceProvider createServiceProvider() {
      return new ServiceProvider();
   }

   public ServicesJar createServicesJar() {
      return new ServicesJar();
   }
}
