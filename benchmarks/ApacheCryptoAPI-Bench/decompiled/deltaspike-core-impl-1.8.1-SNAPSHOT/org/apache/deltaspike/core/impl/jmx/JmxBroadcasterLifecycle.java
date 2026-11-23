package org.apache.deltaspike.core.impl.jmx;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import org.apache.deltaspike.core.api.jmx.JmxBroadcaster;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

public class JmxBroadcasterLifecycle implements ContextualLifecycle<JmxBroadcaster> {
   private final DynamicMBeanWrapper delegate;

   public JmxBroadcasterLifecycle(DynamicMBeanWrapper mbean) {
      this.delegate = mbean;
   }

   public JmxBroadcaster create(Bean<JmxBroadcaster> bean, CreationalContext<JmxBroadcaster> creationalContext) {
      return this.delegate;
   }

   public void destroy(Bean<JmxBroadcaster> bean, JmxBroadcaster instance, CreationalContext<JmxBroadcaster> creationalContext) {
   }
}
