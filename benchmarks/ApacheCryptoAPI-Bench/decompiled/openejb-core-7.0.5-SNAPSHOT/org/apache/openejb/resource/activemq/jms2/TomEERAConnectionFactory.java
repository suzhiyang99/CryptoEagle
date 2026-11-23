package org.apache.openejb.resource.activemq.jms2;

import javax.jms.JMSContext;
import javax.resource.spi.ConnectionManager;
import org.apache.activemq.ra.ActiveMQConnectionFactory;
import org.apache.activemq.ra.ActiveMQConnectionRequestInfo;
import org.apache.activemq.ra.ActiveMQManagedConnectionFactory;

public class TomEERAConnectionFactory extends ActiveMQConnectionFactory {
   public TomEERAConnectionFactory(ActiveMQManagedConnectionFactory factory, ConnectionManager manager, ActiveMQConnectionRequestInfo connectionRequestInfo) {
      super(factory, manager, connectionRequestInfo);
   }

   public JMSContext createContext() {
      return new JMSContextImpl(this, 1, (String)null, (String)null, false);
   }

   public JMSContext createContext(int sessionMode) {
      return new JMSContextImpl(this, sessionMode, (String)null, (String)null, false);
   }

   public JMSContext createContext(String userName, String password) {
      return new JMSContextImpl(this, 1, userName, password, false);
   }

   public JMSContext createContext(String userName, String password, int sessionMode) {
      return new JMSContextImpl(this, sessionMode, userName, password, false);
   }
}
