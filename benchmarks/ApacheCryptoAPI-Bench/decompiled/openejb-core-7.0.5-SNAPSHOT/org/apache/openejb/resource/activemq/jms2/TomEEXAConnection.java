package org.apache.openejb.resource.activemq.jms2;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQXAConnection;
import org.apache.activemq.management.JMSStatsImpl;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.util.IdGenerator;

public class TomEEXAConnection extends ActiveMQXAConnection {
   protected TomEEXAConnection(Transport transport, IdGenerator clientIdGenerator, IdGenerator connectionIdGenerator, JMSStatsImpl factoryStats) throws Exception {
      super(transport, clientIdGenerator, connectionIdGenerator, factoryStats);
   }

   public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
      this.checkClosedOrFailed();
      this.ensureConnectionInfoSent();
      return new TomEEXASession(this, this.getNextSessionId(), this.getXaAckMode() > 0 ? this.getXaAckMode() : 0, this.isDispatchAsync());
   }

   public Session createSession(int sessionMode) throws JMSException {
      return super.createSession(sessionMode == 0, sessionMode);
   }

   public Session createSession() throws JMSException {
      return this.createSession(1);
   }

   public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
      throw new IllegalStateException("Not allowed in a RA");
   }

   public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
      throw new IllegalStateException("Not allowed in a RA");
   }
}
