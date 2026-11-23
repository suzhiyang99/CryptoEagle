package org.apache.deltaspike.core.impl.exception.control.extension;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.ProcessBean;
import org.apache.deltaspike.core.api.exception.control.ExceptionHandler;
import org.apache.deltaspike.core.api.exception.control.HandlerMethod;
import org.apache.deltaspike.core.impl.exception.control.HandlerMethodImpl;
import org.apache.deltaspike.core.spi.activation.Deactivatable;
import org.apache.deltaspike.core.util.ClassDeactivationUtils;

public class ExceptionControlExtension implements Extension, Deactivatable {
   private static final Logger LOG = Logger.getLogger(ExceptionControlExtension.class.getName());
   private Map<Type, Collection<HandlerMethod<? extends Throwable>>> allHandlers = new HashMap();
   private Boolean isActivated = true;

   protected void init(@Observes BeforeBeanDiscovery beforeBeanDiscovery) {
      this.isActivated = ClassDeactivationUtils.isActivated(this.getClass());
   }

   public <T> void findHandlers(@Observes ProcessBean<?> processBean, BeanManager beanManager) {
      if (this.isActivated) {
         if (!(processBean.getBean() instanceof Interceptor) && !(processBean.getBean() instanceof Decorator) && processBean.getAnnotated() instanceof AnnotatedType) {
            AnnotatedType annotatedType = (AnnotatedType)processBean.getAnnotated();
            if (annotatedType.getJavaClass().isAnnotationPresent(ExceptionHandler.class)) {
               for(AnnotatedMethod<? super T> method : annotatedType.getMethods()) {
                  if (HandlerMethodImpl.isHandler(method)) {
                     if (method.getJavaMember().getExceptionTypes().length != 0) {
                        processBean.addDefinitionError(new IllegalArgumentException(String.format("Handler method %s must not throw exceptions", method.getJavaMember())));
                     }

                     this.registerHandlerMethod(new HandlerMethodImpl(processBean.getBean(), method, beanManager));
                  }
               }
            }

         }
      }
   }

   public void verifyInjectionPoints(@Observes AfterDeploymentValidation afterDeploymentValidation, BeanManager bm) {
      if (this.isActivated) {
         for(Map.Entry<Type, Collection<HandlerMethod<? extends Throwable>>> entry : this.allHandlers.entrySet()) {
            for(HandlerMethod<? extends Throwable> handler : (Collection)entry.getValue()) {
               for(InjectionPoint ip : ((HandlerMethodImpl)handler).getInjectionPoints(bm)) {
                  try {
                     bm.validate(ip);
                  } catch (InjectionException e) {
                     afterDeploymentValidation.addDeploymentProblem(e);
                  }
               }
            }
         }

      }
   }

   public Map<Type, Collection<HandlerMethod<? extends Throwable>>> getAllExceptionHandlers() {
      return Collections.unmodifiableMap(this.allHandlers);
   }

   private <T extends Throwable> void registerHandlerMethod(HandlerMethod<T> handlerMethod) {
      LOG.fine(String.format("Adding handler %s to known handlers", handlerMethod));
      if (this.allHandlers.containsKey(handlerMethod.getExceptionType())) {
         ((Collection)this.allHandlers.get(handlerMethod.getExceptionType())).add(handlerMethod);
      } else {
         this.allHandlers.put(handlerMethod.getExceptionType(), new HashSet(Arrays.asList(handlerMethod)));
      }

   }
}
