package org.apache.openejb.resource.activemq;

import org.apache.activemq.ra.ActiveMQActivationSpec;

public class TomEEMessageActivationSpec extends ActiveMQActivationSpec {
   private String connectionFactoryLookup;

   public String getConnectionFactoryLookup() {
      return this.connectionFactoryLookup;
   }

   public void setConnectionFactoryLookup(String connectionFactoryLookup) {
      this.connectionFactoryLookup = connectionFactoryLookup;
   }
}
