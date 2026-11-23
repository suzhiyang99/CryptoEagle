package org.apache.deltaspike.core.impl.config;

import java.lang.reflect.Type;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.spi.config.BaseConfigPropertyProducer;

@ApplicationScoped
public class DefaultConfigPropertyProducer extends BaseConfigPropertyProducer {
   @Produces
   @Dependent
   @ConfigProperty(
      name = "ignored"
   )
   public String produceStringConfiguration(InjectionPoint injectionPoint) {
      return this.getStringPropertyValue(injectionPoint);
   }

   @Produces
   @Dependent
   @ConfigProperty(
      name = "ignored"
   )
   public Class produceClassConfiguration(InjectionPoint injectionPoint) {
      return (Class)this.getPropertyWithException(injectionPoint, Class.class);
   }

   @Produces
   @Dependent
   @ConfigProperty(
      name = "ignored"
   )
   public Boolean produceBooleanConfiguration(InjectionPoint injectionPoint) {
      return (Boolean)this.getPropertyWithException(injectionPoint, Boolean.class);
   }

   @Produces
   @Dependent
   @ConfigProperty(
      name = "ignored"
   )
   public Integer produceIntegerConfiguration(InjectionPoint injectionPoint) {
      return (Integer)this.getPropertyWithException(injectionPoint, Integer.class);
   }

   @Produces
   @Dependent
   @ConfigProperty(
      name = "ignored"
   )
   public Long produceLongConfiguration(InjectionPoint injectionPoint) {
      return (Long)this.getPropertyWithException(injectionPoint, Long.class);
   }

   @Produces
   @Dependent
   @ConfigProperty(
      name = "ignored"
   )
   public Float produceFloatConfiguration(InjectionPoint injectionPoint) {
      return (Float)this.getPropertyWithException(injectionPoint, Float.class);
   }

   @Produces
   @Dependent
   @ConfigProperty(
      name = "ignored"
   )
   public Double produceDoubleConfiguration(InjectionPoint injectionPoint) {
      return (Double)this.getPropertyWithException(injectionPoint, Double.class);
   }

   private <T> T getPropertyWithException(InjectionPoint ip, Type ipCls) {
      try {
         return (T)this.getUntypedPropertyValue(ip, ipCls);
      } catch (RuntimeException rte) {
         ConfigProperty configProperty = (ConfigProperty)this.getAnnotation(ip, ConfigProperty.class);
         throw new RuntimeException("Error while converting property '" + configProperty.name() + "' happening in bean " + ip.getBean(), rte);
      }
   }
}
