package org.apache.deltaspike.core.impl.jmx;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.jmx.JmxBroadcaster;
import org.apache.deltaspike.core.api.jmx.MBean;

@ApplicationScoped
public class BroadcasterProducer {
   @Inject
   private MBeanExtension extension;

   @Produces
   @Dependent
   public JmxBroadcaster jmxBroadcaster(InjectionPoint ip) {
      Class<?> declaringClass = ip.getMember().getDeclaringClass();
      JmxBroadcaster broadcaster = this.extension.getBroadcasterFor(declaringClass);
      if (broadcaster == null) {
         throw new IllegalStateException("Invalid injection of " + JmxBroadcaster.class.getName() + " in " + declaringClass.getName() + " detected. It is required to annotate the class with @" + MBean.class.getName());
      } else {
         return broadcaster;
      }
   }
}
