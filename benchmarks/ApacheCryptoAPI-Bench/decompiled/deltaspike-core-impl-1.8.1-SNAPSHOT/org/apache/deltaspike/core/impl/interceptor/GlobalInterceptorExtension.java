package org.apache.deltaspike.core.impl.interceptor;

import java.lang.annotation.Annotation;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.interceptor.Interceptor;
import org.apache.deltaspike.core.api.config.base.CoreBaseConfig.InterceptorCustomization;
import org.apache.deltaspike.core.impl.util.AnnotationInstanceUtils;
import org.apache.deltaspike.core.spi.activation.Deactivatable;
import org.apache.deltaspike.core.util.ClassDeactivationUtils;

public class GlobalInterceptorExtension implements Deactivatable, Extension {
   private static final Logger LOG = Logger.getLogger(GlobalInterceptorExtension.class.getName());
   private static final String DS_PACKAGE_NAME = "org.apache.deltaspike.";
   private Annotation priorityAnnotationInstance;
   private BeanManager beanManager;

   protected void init(@Observes BeforeBeanDiscovery beforeBeanDiscovery, BeanManager beanManager) {
      if (ClassDeactivationUtils.isActivated(this.getClass())) {
         this.beanManager = beanManager;
         int priorityValue = InterceptorCustomization.PRIORITY;
         this.priorityAnnotationInstance = AnnotationInstanceUtils.getPriorityAnnotationInstance(priorityValue);
      }
   }

   protected void promoteInterceptors(@Observes ProcessAnnotatedType pat) {
      if (this.priorityAnnotationInstance != null) {
         String beanClassName = pat.getAnnotatedType().getJavaClass().getName();
         if (beanClassName.startsWith("org.apache.deltaspike.")) {
            if (pat.getAnnotatedType().isAnnotationPresent(Interceptor.class)) {
               pat.setAnnotatedType(new GlobalInterceptorWrapper(pat.getAnnotatedType(), this.priorityAnnotationInstance));
            } else if (!beanClassName.contains(".test.")) {
               for(Annotation annotation : pat.getAnnotatedType().getAnnotations()) {
                  if (this.beanManager.isInterceptorBinding(annotation.annotationType())) {
                     LOG.warning(beanClassName + " is an bean from DeltaSpike which is intercepted.");
                  }
               }
            }
         }

      }
   }
}
