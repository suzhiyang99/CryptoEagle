package org.apache.activemq.artemis.core.persistence;

import org.apache.activemq.artemis.api.core.ActiveMQBuffer;

public interface Persister<T> {
   default byte getID() {
      return 0;
   }

   int getEncodeSize(T var1);

   void encode(ActiveMQBuffer var1, T var2);

   T decode(ActiveMQBuffer var1, T var2);
}
