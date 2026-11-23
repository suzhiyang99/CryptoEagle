package org.apache.openejb.core;

import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;
import org.apache.openejb.core.ivm.naming.Reference;

public class ConnectorReference extends Reference {
   private final ConnectionManager conMngr;
   private final ManagedConnectionFactory mngedConFactory;

   public ConnectorReference(ConnectionManager manager, ManagedConnectionFactory factory) {
      this.conMngr = manager;
      this.mngedConFactory = factory;
   }

   public Object getObject() throws NamingException {
      try {
         Object connection = this.mngedConFactory.createConnectionFactory(this.conMngr);
         return connection;
      } catch (ResourceException re) {
         throw (NamingException)(new NamingException("Could not create ConnectionFactory from " + this.mngedConFactory.getClass())).initCause(re);
      }
   }

   public ConnectionManager getConnectionManager() {
      return this.conMngr;
   }
}
