package org.apache.openejb.core.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AfterBegin;
import javax.ejb.AfterCompletion;
import javax.ejb.BeforeCompletion;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import org.apache.openejb.core.Operation;
import org.apache.openejb.util.SetAccessible;
import org.apache.xbean.finder.ClassFinder;

public class InterceptorData {
   private static final Map<Class<?>, InterceptorData> CACHE = new ConcurrentHashMap();
   private final Class clazz;
   private final Set<Method> aroundInvoke = new LinkedHashSet();
   private final Set<Method> postConstruct = new LinkedHashSet();
   private final Set<Method> preDestroy = new LinkedHashSet();
   private final Set<Method> postActivate = new LinkedHashSet();
   private final Set<Method> prePassivate = new LinkedHashSet();
   private final Set<Method> afterBegin = new LinkedHashSet();
   private final Set<Method> beforeCompletion = new LinkedHashSet();
   private final Set<Method> afterCompletion = new LinkedHashSet();
   private final Set<Method> aroundTimeout = new LinkedHashSet();
   private final Map<Class<?>, Object> data = new HashMap();

   public InterceptorData(Class clazz) {
      this.clazz = clazz;
   }

   public Class getInterceptorClass() {
      return this.clazz;
   }

   public Set<Method> getAroundInvoke() {
      return this.aroundInvoke;
   }

   public Set<Method> getPostConstruct() {
      return this.postConstruct;
   }

   public Set<Method> getPreDestroy() {
      return this.preDestroy;
   }

   public Set<Method> getPostActivate() {
      return this.postActivate;
   }

   public Set<Method> getPrePassivate() {
      return this.prePassivate;
   }

   public Set<Method> getAfterBegin() {
      return this.afterBegin;
   }

   public Set<Method> getBeforeCompletion() {
      return this.beforeCompletion;
   }

   public Set<Method> getAfterCompletion() {
      return this.afterCompletion;
   }

   public Set<Method> getAroundTimeout() {
      return this.aroundTimeout;
   }

   public Set<Method> getMethods(Operation operation) {
      switch (operation) {
         case BUSINESS:
            return this.getAroundInvoke();
         case BUSINESS_WS:
            return this.getAroundInvoke();
         case REMOVE:
            return this.getAroundInvoke();
         case POST_CONSTRUCT:
            return this.getPostConstruct();
         case PRE_DESTROY:
            return this.getPreDestroy();
         case ACTIVATE:
            return this.getPostActivate();
         case PASSIVATE:
            return this.getPrePassivate();
         case AFTER_BEGIN:
            return this.getAfterBegin();
         case AFTER_COMPLETION:
            return this.getAfterCompletion();
         case BEFORE_COMPLETION:
            return this.getBeforeCompletion();
         case TIMEOUT:
            return this.getAroundTimeout();
         default:
            return Collections.EMPTY_SET;
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         InterceptorData that = (InterceptorData)o;
         if (this.clazz != null) {
            if (!this.clazz.equals(that.clazz)) {
               return false;
            }
         } else if (that.clazz != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.clazz != null ? this.clazz.hashCode() : 0;
   }

   public static void cacheScan(Class<?> clazz) {
      CACHE.put(clazz, scan(clazz));
   }

   public static InterceptorData scan(Class<?> clazz) {
      InterceptorData model = (InterceptorData)CACHE.get(clazz);
      if (model != null) {
         InterceptorData data = new InterceptorData(clazz);
         data.aroundInvoke.addAll(model.getAroundInvoke());
         data.postConstruct.addAll(model.getPostConstruct());
         data.preDestroy.addAll(model.getPreDestroy());
         data.postActivate.addAll(model.getPostActivate());
         data.prePassivate.addAll(model.getPrePassivate());
         data.afterBegin.addAll(model.getAfterBegin());
         data.beforeCompletion.addAll(model.getBeforeCompletion());
         data.afterCompletion.addAll(model.getAfterCompletion());
         data.aroundTimeout.addAll(model.getAroundTimeout());
         return data;
      } else {
         ClassFinder finder = new ClassFinder(new Class[]{clazz});
         InterceptorData data = new InterceptorData(clazz);
         add(finder, data.aroundInvoke, AroundInvoke.class);
         add(finder, data.postConstruct, PostConstruct.class);
         add(finder, data.preDestroy, PreDestroy.class);
         add(finder, data.postActivate, PostActivate.class);
         add(finder, data.prePassivate, PrePassivate.class);
         add(finder, data.afterBegin, AfterBegin.class);
         add(finder, data.beforeCompletion, BeforeCompletion.class);
         add(finder, data.afterCompletion, AfterCompletion.class);
         add(finder, data.aroundTimeout, AroundTimeout.class);
         return data;
      }
   }

   private static void add(ClassFinder finder, Set<Method> methods, Class<? extends Annotation> annotation) {
      for(Method method : finder.findAnnotatedMethods(annotation)) {
         SetAccessible.on(method);
         methods.add(method);
      }

   }

   public <T> void set(Class<T> clazz, T value) {
      this.data.put(clazz, value);
   }

   public <T> T get(Class<T> clazz) {
      return (T)clazz.cast(this.data.get(clazz));
   }

   public String toString() {
      return "InterceptorData{clazz=" + this.clazz.getSimpleName() + '}';
   }
}
