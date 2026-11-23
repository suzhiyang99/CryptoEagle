package org.apache.openejb.resource.activemq.jms2;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.management.JMSStatsImpl;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.util.IdGenerator;

public class TomEEConnection extends ActiveMQConnection {
   public TomEEConnection(Transport transport, IdGenerator clientIdGenerator, IdGenerator connectionIdGenerator, JMSStatsImpl factoryStats) throws Exception {
      super(transport, clientIdGenerator, connectionIdGenerator, factoryStats);
   }

   public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
      this.checkClosedOrFailed();
      this.ensureConnectionInfoSent();
      if (!transacted) {
         if (acknowledgeMode == 0) {
            throw new JMSException("acknowledgeMode SESSION_TRANSACTED cannot be used for an non-transacted Session");
         }

         if (acknowledgeMode < 0 || acknowledgeMode > 4) {
            throw new JMSException("invalid acknowledgeMode: " + acknowledgeMode + ". Valid values are Session.AUTO_ACKNOWLEDGE (1), Session.CLIENT_ACKNOWLEDGE (2), Session.DUPS_OK_ACKNOWLEDGE (3), ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE (4) or for transacted sessions Session.SESSION_TRANSACTED (0)");
         }
      }

      return new TomEESession(this, this.getNextSessionId(), transacted ? 0 : acknowledgeMode, this.isDispatchAsync(), this.isAlwaysSessionAsync());
   }

   public Session createSession(int sessionMode) throws JMSException {
      return this.createSession(sessionMode == 0, sessionMode);
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
