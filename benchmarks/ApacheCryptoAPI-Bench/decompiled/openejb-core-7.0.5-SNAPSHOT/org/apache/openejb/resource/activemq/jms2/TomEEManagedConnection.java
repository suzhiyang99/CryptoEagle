package org.apache.openejb.resource.activemq.jms2;

import java.lang.reflect.Field;
import java.util.Collection;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.security.auth.Subject;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ra.ActiveMQConnectionRequestInfo;
import org.apache.activemq.ra.ActiveMQManagedConnection;
import org.apache.activemq.ra.ManagedConnectionProxy;

public class TomEEManagedConnection extends ActiveMQManagedConnection {
   private static final Field PROXY_CONNECTIONS_FIELD;
   private final Collection<ManagedConnectionProxy> proxyConnections;

   public TomEEManagedConnection(Subject subject, ActiveMQConnection physicalConnection, ActiveMQConnectionRequestInfo info) throws ResourceException {
      super(subject, physicalConnection, info);

      try {
         this.proxyConnections = (Collection)Collection.class.cast(PROXY_CONNECTIONS_FIELD.get(this));
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Incompatible AMQ", e);
      }
   }

   public Object getConnection(Subject subject, ConnectionRequestInfo info) throws ResourceException {
      ManagedConnectionProxy proxy = new TomEEManagedConnectionProxy(this, info);
      this.proxyConnections.add(proxy);
      return proxy;
   }

   static {
      try {
         PROXY_CONNECTIONS_FIELD = ActiveMQManagedConnection.class.getDeclaredField("proxyConnections");
      } catch (NoSuchFieldException e) {
         throw new IllegalStateException("Incompatible AMQ", e);
      }

      PROXY_CONNECTIONS_FIELD.setAccessible(true);
   }
}
