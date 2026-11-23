package org.apache.deltaspike.core.impl.message;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import org.apache.deltaspike.core.api.literal.DefaultLiteral;
import org.apache.deltaspike.core.api.message.Message;
import org.apache.deltaspike.core.api.message.MessageBundle;
import org.apache.deltaspike.core.api.message.MessageTemplate;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.api.provider.DependentProvider;
import org.apache.deltaspike.core.spi.activation.Deactivatable;
import org.apache.deltaspike.core.util.ClassDeactivationUtils;
import org.apache.deltaspike.core.util.ClassUtils;
import org.apache.deltaspike.core.util.ParentExtensionStorage;
import org.apache.deltaspike.core.util.bean.BeanBuilder;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

public class MessageBundleExtension implements Extension, Deactivatable {
   private final Collection<AnnotatedType<?>> messageBundleTypes = new HashSet();
   private List<String> deploymentErrors = new ArrayList();
   private Boolean isActivated = true;

   protected void init(@Observes BeforeBeanDiscovery beforeBeanDiscovery) {
      this.isActivated = ClassDeactivationUtils.isActivated(this.getClass());
      ParentExtensionStorage.addExtension(this);
   }

   protected void detectInterfaces(@Observes ProcessAnnotatedType processAnnotatedType) {
      if (this.isActivated) {
         AnnotatedType<?> type = processAnnotatedType.getAnnotatedType();
         if (type.isAnnotationPresent(MessageBundle.class) && this.validateMessageBundle(type.getJavaClass())) {
            this.messageBundleTypes.add(type);
         }

      }
   }

   private boolean validateMessageBundle(Class<?> currentClass) {
      boolean ok = true;
      if (!currentClass.isInterface()) {
         this.deploymentErrors.add("@MessageBundle must only be used on Interfaces, but got used on class " + currentClass.getName());
         return false;
      } else {
         for(Method currentMethod : currentClass.getDeclaredMethods()) {
            if (currentMethod.isAnnotationPresent(MessageTemplate.class) && !String.class.isAssignableFrom(currentMethod.getReturnType()) && !Message.class.isAssignableFrom(currentMethod.getReturnType())) {
               this.deploymentErrors.add(currentMethod.getReturnType().getName() + " isn't supported. Details: " + currentMethod.getDeclaringClass().getName() + "#" + currentMethod.getName() + " only " + String.class.getName() + " or " + Message.class.getName());
               ok = false;
            }
         }

         return ok;
      }
   }

   protected void installMessageBundleProducerBeans(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
      if (!this.deploymentErrors.isEmpty()) {
         abd.addDefinitionError(new IllegalArgumentException("The following MessageBundle problems where found: " + Arrays.toString(this.deploymentErrors.toArray())));
      } else {
         MessageBundleExtension parentExtension = (MessageBundleExtension)ParentExtensionStorage.getParentExtension(this);
         if (parentExtension != null) {
            this.messageBundleTypes.addAll(parentExtension.messageBundleTypes);
         }

         for(AnnotatedType<?> type : this.messageBundleTypes) {
            abd.addBean(this.createMessageBundleBean(type, beanManager));
         }

      }
   }

   private <T> Bean<T> createMessageBundleBean(AnnotatedType<T> annotatedType, BeanManager beanManager) {
      BeanBuilder<T> beanBuilder = (new BeanBuilder(beanManager)).readFromType(annotatedType);
      beanBuilder.beanLifecycle(new MessageBundleLifecycle(beanManager));
      beanBuilder.types(new Type[]{annotatedType.getJavaClass(), Object.class, Serializable.class});
      beanBuilder.addQualifier(new DefaultLiteral());
      beanBuilder.passivationCapable(true);
      beanBuilder.scope(ApplicationScoped.class);
      beanBuilder.id("MessageBundleBean#" + annotatedType.getJavaClass().getName());
      return beanBuilder.create();
   }

   protected void cleanup(@Observes AfterDeploymentValidation afterDeploymentValidation) {
      this.messageBundleTypes.clear();
   }

   private static class MessageBundleLifecycle<T> implements ContextualLifecycle<T> {
      private final BeanManager beanManager;
      private DependentProvider<MessageBundleInvocationHandler> invocationHandlerProvider;

      private MessageBundleLifecycle(BeanManager beanManager) {
         this.beanManager = beanManager;
      }

      public T create(Bean<T> bean, CreationalContext<T> creationalContext) {
         this.invocationHandlerProvider = BeanProvider.getDependent(this.beanManager, MessageBundleInvocationHandler.class, new Annotation[0]);
         return (T)this.createMessageBundleProxy(bean.getBeanClass(), (MessageBundleInvocationHandler)this.invocationHandlerProvider.get());
      }

      public void destroy(Bean<T> bean, T instance, CreationalContext<T> creationalContext) {
         if (this.invocationHandlerProvider != null) {
            this.invocationHandlerProvider.destroy();
         }

      }

      private <T> T createMessageBundleProxy(Class<T> type, MessageBundleInvocationHandler handler) {
         return (T)type.cast(Proxy.newProxyInstance(ClassUtils.getClassLoader((Object)null), new Class[]{type, Serializable.class}, handler));
      }
   }
}
