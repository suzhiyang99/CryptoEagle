package org.apache.openejb.resource.activemq.jms2;

import javax.jms.JMSContext;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.management.JMSStatsImpl;
import org.apache.activemq.transport.Transport;

public class TomEEConnectionFactory extends ActiveMQSslConnectionFactory {
   protected ActiveMQConnection createActiveMQConnection(Transport transport, JMSStatsImpl stats) throws Exception {
      return new TomEEConnection(transport, this.getClientIdGenerator(), this.getConnectionIdGenerator(), stats);
   }

   public JMSContext createContext() {
      return new JMSContextImpl(this, -1, (String)null, (String)null, false);
   }

   public JMSContext createContext(int sessionMode) {
      return new JMSContextImpl(this, sessionMode, (String)null, (String)null, false);
   }

   public JMSContext createContext(String userName, String password) {
      return new JMSContextImpl(this, -1, userName, password, false);
   }

   public JMSContext createContext(String userName, String password, int sessionMode) {
      return new JMSContextImpl(this, sessionMode, userName, password, false);
   }
}
