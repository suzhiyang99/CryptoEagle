package org.apache.openejb.cdi;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.PassivationCapable;
import org.apache.openejb.util.reflection.Reflections;
import org.apache.webbeans.component.BuiltInOwbBean;
import org.apache.webbeans.component.ExtensionBean;
import org.apache.webbeans.component.OwbBean;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.context.creational.CreationalContextImpl;
import org.apache.webbeans.event.EventMetadataImpl;
import org.apache.webbeans.util.Asserts;
import org.apache.webbeans.util.WebBeansUtil;

public class WebappBeanManager extends BeanManagerImpl {
   private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
   private static final ThreadLocal<Boolean> USE_PARENT_BM = new ThreadLocal();
   private final WebappWebBeansContext webappCtx;
   private final InheritedBeanFilter filter;
   private Set<Bean<?>> deploymentBeans;
   private boolean started;

   public WebappBeanManager(WebappWebBeansContext ctx) {
      super(ctx);
      this.webappCtx = ctx;
      this.deploymentBeans = super.getBeans();
      Reflections.set(this, "injectionResolver", new WebAppInjectionResolver(ctx));
      this.filter = new InheritedBeanFilter(this);
   }

   public void fireEvent(Object event, EventMetadataImpl metadata, boolean isLifecycleEvent) {
      this.getNotificationManager().fireEvent(event, metadata, isLifecycleEvent);
      if (this.isEvent(event)) {
         BeanManagerImpl parentBm = this.getParentBm();
         if (parentBm != null) {
            parentBm.getNotificationManager().fireEvent(event, metadata, isLifecycleEvent);
         }
      }

   }

   public List<Interceptor<?>> resolveInterceptors(InterceptionType type, Annotation... interceptorBindings) {
      List<Interceptor<?>> interceptors = super.resolveInterceptors(type, interceptorBindings);

      for(Interceptor<?> i : this.getParentBm().resolveInterceptors(type, interceptorBindings)) {
         if (!interceptors.contains(i)) {
            interceptors.add(i);
         }
      }

      return interceptors;
   }

   public <T> Set<ObserverMethod<? super T>> resolveObserverMethods(T event, EventMetadataImpl metadata) {
      Set<ObserverMethod<? super T>> set = new HashSet();
      set.addAll(this.getNotificationManager().resolveObservers(event, metadata, false));
      if (this.isEvent(event)) {
         BeanManagerImpl parentBm = this.getParentBm();
         if (parentBm != null) {
            set.addAll(parentBm.getNotificationManager().resolveObservers(event, metadata, false));
         }
      }

      return set;
   }

   public Object getInjectableReference(InjectionPoint injectionPoint, CreationalContext<?> ctx) {
      Asserts.assertNotNull(injectionPoint, "injectionPoint parameter");
      if (injectionPoint == null) {
         return null;
      } else {
         BeanManagerImpl parentBm = this.getParentBm();
         Boolean existing = (Boolean)USE_PARENT_BM.get();
         if (existing != null && existing) {
            return parentBm == null ? null : parentBm.getInjectableReference(injectionPoint, ctx);
         } else {
            Bean<Object> injectedBean = this.getInjectionResolver().getInjectionPointBean(injectionPoint);

            try {
               if (parentBm != null && injectedBean != null && injectedBean == parentBm.getInjectionResolver().getInjectionPointBean(injectionPoint)) {
                  USE_PARENT_BM.set(true);

                  Object var6;
                  try {
                     var6 = parentBm.getInjectableReference(injectionPoint, ctx);
                  } finally {
                     USE_PARENT_BM.remove();
                  }

                  return var6;
               }
            } catch (UnsatisfiedResolutionException var11) {
            }

            return super.getInjectableReference(injectionPoint, ctx);
         }
      }
   }

   public <T> CreationalContextImpl<T> createCreationalContext(Contextual<T> contextual) {
      try {
         return super.createCreationalContext(contextual);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().createCreationalContext(contextual);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public boolean isNormalScope(Class<? extends Annotation> annotationType) {
      try {
         return super.isNormalScope(annotationType);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().isNormalScope(annotationType);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public boolean isPassivatingScope(Class<? extends Annotation> annotationType) {
      try {
         return super.isPassivatingScope(annotationType);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().isPassivatingScope(annotationType);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public boolean isQualifier(Class<? extends Annotation> annotationType) {
      try {
         return super.isQualifier(annotationType);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().isQualifier(annotationType);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
      try {
         return super.isInterceptorBinding(annotationType);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().isInterceptorBinding(annotationType);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public boolean isStereotype(Class<? extends Annotation> annotationType) {
      try {
         return super.isStereotype(annotationType);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().isStereotype(annotationType);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public Set<Annotation> getInterceptorBindingDefinition(Class<? extends Annotation> qualifier) {
      try {
         return super.getInterceptorBindingDefinition(qualifier);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().getInterceptorBindingDefinition(qualifier);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public Context getContext(Class<? extends Annotation> scope) {
      try {
         return super.getContext(scope);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().getContext(scope);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public ELResolver getELResolver() {
      return new WebAppElResolver(super.getELResolver(), this.getParentBm().getELResolver());
   }

   public <T> AnnotatedType<T> createAnnotatedType(Class<T> type) {
      try {
         return super.createAnnotatedType(type);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().createAnnotatedType(type);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public <T> InjectionTarget<T> createInjectionTarget(AnnotatedType<T> type) {
      try {
         return super.createInjectionTarget(type);
      } catch (RuntimeException e) {
         try {
            return this.getParentBm().createInjectionTarget(type);
         } catch (RuntimeException var4) {
            throw e;
         }
      }
   }

   public ExpressionFactory wrapExpressionFactory(ExpressionFactory expressionFactory) {
      return super.wrapExpressionFactory(expressionFactory);
   }

   public BeanManagerImpl getParentBm() {
      WebBeansContext parent = this.webappCtx.getParent();
      return parent != null ? parent.getBeanManagerImpl() : null;
   }

   public boolean isInUse() {
      return super.isInUse() || this.getParentBm().isInUse();
   }

   public Set<Bean<?>> getComponents() {
      return (Set<Bean<?>>)(!this.started ? new IteratorSet(new MultipleIterator(this.filter, new Iterator[]{this.deploymentBeans.iterator(), this.getParentBm().getComponents().iterator()})) : this.deploymentBeans);
   }

   public Set<Bean<?>> getBeans() {
      return this.deploymentBeans;
   }

   public <T> void addAdditionalAnnotatedType(Object extension, AnnotatedType<T> inAnnotatedType, String id) {
      super.addAdditionalAnnotatedType(extension, inAnnotatedType, id);
   }

   public Bean<?> getPassivationCapableBean(String id) {
      Bean<?> bean = super.getPassivationCapableBean(id);
      return bean == null && this.getParentBm() != null ? this.getParentBm().getPassivationCapableBean(id) : bean;
   }

   public void afterStart() {
      this.started = true;
      this.deploymentBeans = this.mergeBeans();
      this.webappCtx.getBeanManagerImpl().getInjectionResolver().clearCaches();
   }

   private Set<Bean<?>> mergeBeans() {
      Set<Bean<?>> allBeans = new CopyOnWriteArraySet();
      BeanManagerImpl parentBm = this.getParentBm();
      if (parentBm != null) {
         for(Bean<?> bean : parentBm.getBeans()) {
            if (this.filter.accept(bean)) {
               allBeans.add(bean);
            }
         }
      }

      allBeans.addAll(super.getBeans());
      return allBeans;
   }

   public void beforeStop() {
   }

   private boolean isEvent(Object event) {
      return !WebBeansUtil.isDefaultExtensionBeanEventType(event.getClass()) && !this.webappCtx.getWebBeansUtil().isContainerEventType(event);
   }

   private static final class InheritedBeanFilter implements Filter<Bean<?>> {
      private final WebappBeanManager beanManager;

      private InheritedBeanFilter(WebappBeanManager beanManager) {
         this.beanManager = beanManager;
      }

      public boolean accept(Bean<?> bean) {
         if (!BuiltInOwbBean.class.isInstance(bean) && !ExtensionBean.class.isInstance(bean)) {
            if (OwbBean.class.isInstance(bean)) {
               OwbBean owbBean = (OwbBean)OwbBean.class.cast(bean);
               if (owbBean.isPassivationCapable() && this.hasBean(owbBean.getId())) {
                  return false;
               }
            } else if (PassivationCapable.class.isInstance(bean) && this.hasBean(((PassivationCapable)PassivationCapable.class.cast(bean)).getId())) {
               return false;
            }

            Set<Annotation> qualifiers = bean.getQualifiers();
            return this.beanManager.getBeans(bean.getBeanClass(), qualifiers.isEmpty() ? WebappBeanManager.EMPTY_ANNOTATIONS : (Annotation[])qualifiers.toArray(new Annotation[qualifiers.size()])).isEmpty();
         } else {
            return false;
         }
      }

      private boolean hasBean(String id) {
         return this.beanManager.getPassivationCapableBean(id) != null;
      }
   }

   private static final class MultipleIterator<A> implements Iterator<A> {
      private final Iterator<A>[] delegates;
      private final Filter<A> filter;
      private A next;
      private int idx;

      private MultipleIterator(Filter<A> filter, Iterator<A>... delegates) {
         this.filter = filter;
         this.delegates = delegates;
      }

      public boolean hasNext() {
         while(this.idx < this.delegates.length) {
            if (!this.delegates[this.idx].hasNext()) {
               ++this.idx;
            } else {
               this.next = (A)this.delegates[this.idx].next();
               if (this.idx == 0 || this.filter.accept(this.next)) {
                  return true;
               }
            }
         }

         return false;
      }

      public A next() {
         return this.next;
      }

      public void remove() {
         this.delegates[this.idx].remove();
      }
   }

   private static final class IteratorSet<A> extends HashSet<A> {
      private final Iterator<A> it;

      private IteratorSet(Iterator<A> it) {
         this.it = it;
      }

      public Iterator<A> iterator() {
         return this.it;
      }
   }

   private interface Filter<A> {
      boolean accept(A var1);
   }
}
