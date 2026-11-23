package org.apache.deltaspike.core.impl.jmx;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.api.config.base.CoreBaseConfig.MBeanIntegration;
import org.apache.deltaspike.core.api.jmx.JmxBroadcaster;
import org.apache.deltaspike.core.api.jmx.MBean;
import org.apache.deltaspike.core.spi.activation.Deactivatable;
import org.apache.deltaspike.core.util.BeanUtils;
import org.apache.deltaspike.core.util.ClassDeactivationUtils;

public class MBeanExtension implements Extension, Deactivatable {
   private static final Logger LOGGER = Logger.getLogger(MBeanExtension.class.getName());
   private static final String DEFAULT_TYPE = "MBeans";
   private static final String DEFAULT_CATEGORY = "org.apache.deltaspike";
   private final Map<Class<?>, DynamicMBeanWrapper> wrappers = new ConcurrentHashMap();
   private final Collection<ObjectName> objectNames = new ArrayList();
   private Boolean isActivated = true;

   protected void init(@Observes BeforeBeanDiscovery beforeBeanDiscovery) {
      this.isActivated = ClassDeactivationUtils.isActivated(this.getClass());
   }

   protected void processBean(@Observes ProcessManagedBean<?> bean, BeanManager bm) throws Exception {
      if (this.isActivated) {
         MBean mBeanAnnotation = (MBean)bean.getAnnotated().getAnnotation(MBean.class);
         if (mBeanAnnotation != null) {
            this.registerObject(bean, mBeanAnnotation, bm);
         }

      }
   }

   protected void shutdown(@Observes BeforeShutdown shutdown) throws Exception {
      if (this.isActivated) {
         MBeanServer mBeanServer = this.mBeanServer();

         for(ObjectName objectName : this.objectNames) {
            mBeanServer.unregisterMBean(objectName);
            LOGGER.info("Unregistered MBean " + objectName.getCanonicalName());
         }

         this.objectNames.clear();
      }
   }

   private void registerObject(ProcessManagedBean<?> bean, MBean mBeanAnnotation, BeanManager bm) throws Exception {
      Class<?> clazz = bean.getAnnotatedBeanClass().getJavaClass();
      String objectNameValue = mBeanAnnotation.objectName();
      if (objectNameValue.isEmpty()) {
         String type = this.getConfigurableAttribute(mBeanAnnotation.type(), "MBeans");
         String category = this.getConfigurableAttribute(mBeanAnnotation.category(), "org.apache.deltaspike");
         String properties = this.getConfigurableAttribute(mBeanAnnotation.properties(), "");
         String name = mBeanAnnotation.name();
         StringBuilder builder = (new StringBuilder(category)).append(':');
         if (!properties.contains("type=")) {
            builder.append("type=").append(type);
         } else if (!"MBeans".equals(type)) {
            LOGGER.warning("type() ignored on " + clazz + " since properties contains it.");
         }

         if (!properties.contains("name=") && (!name.isEmpty() || properties.isEmpty())) {
            builder.append(",name=");
            if (name.isEmpty()) {
               builder.append(clazz.getName());
            } else {
               builder.append(name);
            }
         }

         if (!properties.isEmpty()) {
            builder.append(',').append(properties);
         }

         objectNameValue = builder.toString();
      }

      ObjectName objectName = new ObjectName(objectNameValue);
      boolean normalScoped = this.isNormalScope(bean.getAnnotated().getAnnotations(), bm);
      Annotation[] qualifiers = this.qualifiers(bean.getAnnotatedBeanClass(), bm);
      DynamicMBeanWrapper mbean = new DynamicMBeanWrapper(clazz, normalScoped, qualifiers);
      MBeanServer server = this.mBeanServer();
      if (server.isRegistered(objectName) && MBeanIntegration.AUTO_UNREGISTER) {
         server.unregisterMBean(objectName);
      }

      server.registerMBean(mbean, objectName);
      this.objectNames.add(objectName);
      this.wrappers.put(clazz, mbean);
      LOGGER.info("Registered MBean " + objectName);
   }

   private Annotation[] qualifiers(AnnotatedType<?> annotatedBeanClass, BeanManager bm) {
      Set<Annotation> qualifiers = BeanUtils.getQualifiers(bm, annotatedBeanClass.getAnnotations());
      return (Annotation[])qualifiers.toArray(new Annotation[qualifiers.size()]);
   }

   private boolean isNormalScope(Set<Annotation> annotations, BeanManager bm) {
      for(Annotation annotation : annotations) {
         if (bm.isNormalScope(annotation.annotationType())) {
            return true;
         }
      }

      return false;
   }

   JmxBroadcaster getBroadcasterFor(Class<?> clazz) {
      return (JmxBroadcaster)this.wrappers.get(clazz);
   }

   private MBeanServer mBeanServer() {
      return ManagementFactory.getPlatformMBeanServer();
   }

   private String getConfigurableAttribute(String annotationAttributeValue, String defaultValue) {
      String val = annotationAttributeValue.trim();
      if (val.startsWith("{") && val.endsWith("}")) {
         val = ConfigResolver.getPropertyValue(val.substring(1, val.length() - 1), defaultValue);
      }

      return val != null && !val.isEmpty() ? val : defaultValue;
   }
}
