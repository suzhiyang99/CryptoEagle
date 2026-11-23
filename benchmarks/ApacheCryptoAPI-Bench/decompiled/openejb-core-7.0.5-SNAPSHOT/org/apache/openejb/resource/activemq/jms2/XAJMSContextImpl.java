package org.apache.openejb.resource.activemq.jms2;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.XAJMSContext;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;

public class XAJMSContextImpl extends JMSContextImpl implements XAJMSContext {
   public XAJMSContextImpl(ConnectionFactory factory, int sessionMode, String user, String pwd) {
      super(factory, sessionMode, user, pwd, true);
   }

   public JMSContext getContext() {
      return this;
   }

   public XAResource getXAResource() {
      return ((XASession)XASession.class.cast(this.session())).getXAResource();
   }
}
