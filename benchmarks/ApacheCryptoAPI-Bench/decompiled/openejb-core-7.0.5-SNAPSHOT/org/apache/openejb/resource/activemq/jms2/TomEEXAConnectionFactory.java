package org.apache.openejb.resource.activemq.jms2;

import java.net.URI;
import javax.jms.JMSContext;
import javax.jms.XAJMSContext;
import org.apache.activemq.ActiveMQXAConnectionFactory;

public class TomEEXAConnectionFactory extends ActiveMQXAConnectionFactory {
   public TomEEXAConnectionFactory() {
   }

   public TomEEXAConnectionFactory(String userName, String password, String brokerURL) {
      super(userName, password, brokerURL);
   }

   public TomEEXAConnectionFactory(String userName, String password, URI brokerURL) {
      super(userName, password, brokerURL);
   }

   public TomEEXAConnectionFactory(String brokerURL) {
      super(brokerURL);
   }

   public TomEEXAConnectionFactory(URI brokerURL) {
      super(brokerURL);
   }

   public JMSContext createContext() {
      return new JMSContextImpl(this, 1, (String)null, (String)null, true);
   }

   public JMSContext createContext(int sessionMode) {
      return new JMSContextImpl(this, sessionMode, (String)null, (String)null, true);
   }

   public JMSContext createContext(String userName, String password) {
      return new JMSContextImpl(this, 1, userName, password, true);
   }

   public JMSContext createContext(String userName, String password, int sessionMode) {
      return new JMSContextImpl(this, sessionMode, userName, password, true);
   }

   public XAJMSContext createXAContext() {
      return new XAJMSContextImpl(this, 0, this.userName, this.password);
   }

   public XAJMSContext createXAContext(String userName, String password) {
      return new XAJMSContextImpl(this, 0, userName, password);
   }
}
