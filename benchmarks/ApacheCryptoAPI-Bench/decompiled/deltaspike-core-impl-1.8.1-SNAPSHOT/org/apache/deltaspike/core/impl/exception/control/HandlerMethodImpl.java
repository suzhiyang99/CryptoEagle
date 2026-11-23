package org.apache.deltaspike.core.impl.exception.control;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import org.apache.deltaspike.core.api.exception.control.BeforeHandles;
import org.apache.deltaspike.core.api.exception.control.HandlerMethod;
import org.apache.deltaspike.core.api.exception.control.Handles;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;
import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.BeanUtils;
import org.apache.deltaspike.core.util.metadata.builder.ImmutableInjectionPoint;
import org.apache.deltaspike.core.util.metadata.builder.InjectableMethod;

@Typed
public class HandlerMethodImpl<T extends Throwable> implements HandlerMethod<T> {
   private final Class declaringBeanClass;
   private final Bean<?> declaringBean;
   private final Set<Annotation> qualifiers;
   private final Type exceptionType;
   private final AnnotatedMethod<?> handler;
   private final boolean before;
   private final int ordinal;
   private final Method javaMethod;
   private final AnnotatedParameter<?> handlerParameter;
   private Set<InjectionPoint> injectionPoints;

   public HandlerMethodImpl(Bean<?> handlerDeclaringBean, AnnotatedMethod<?> method, BeanManager bm) {
      Set<Annotation> tmpQualifiers = new HashSet();
      this.declaringBean = handlerDeclaringBean;
      this.handler = method;
      this.javaMethod = method.getJavaMember();
      this.handlerParameter = findHandlerParameter(method);
      if (!this.handlerParameter.isAnnotationPresent(Handles.class) && !this.handlerParameter.isAnnotationPresent(BeforeHandles.class)) {
         throw new IllegalArgumentException("Method is not annotated with @Handles or @BeforeHandles");
      } else {
         this.before = this.handlerParameter.getAnnotation(BeforeHandles.class) != null;
         if (this.before) {
            this.ordinal = ((BeforeHandles)this.handlerParameter.getAnnotation(BeforeHandles.class)).ordinal();
         } else {
            this.ordinal = ((Handles)this.handlerParameter.getAnnotation(Handles.class)).ordinal();
         }

         tmpQualifiers.addAll(BeanUtils.getQualifiers(bm, this.handlerParameter.getAnnotations()));
         if (tmpQualifiers.isEmpty()) {
            tmpQualifiers.add(new AnyLiteral());
         }

         this.qualifiers = tmpQualifiers;
         this.declaringBeanClass = method.getJavaMember().getDeclaringClass();
         this.exceptionType = ((ParameterizedType)this.handlerParameter.getBaseType()).getActualTypeArguments()[0];
      }
   }

   public static boolean isHandler(AnnotatedMethod<?> method) {
      if (method == null) {
         throw new IllegalArgumentException("Method must not be null");
      } else {
         for(AnnotatedParameter<?> param : method.getParameters()) {
            if (param.isAnnotationPresent(Handles.class) || param.isAnnotationPresent(BeforeHandles.class)) {
               return true;
            }
         }

         return false;
      }
   }

   public static AnnotatedParameter<?> findHandlerParameter(AnnotatedMethod<?> method) {
      if (!isHandler(method)) {
         throw new IllegalArgumentException("Method is not a valid handler");
      } else {
         AnnotatedParameter<?> returnParam = null;

         for(AnnotatedParameter<?> param : method.getParameters()) {
            if (param.isAnnotationPresent(Handles.class) || param.isAnnotationPresent(BeforeHandles.class)) {
               returnParam = param;
               break;
            }
         }

         return returnParam;
      }
   }

   public Bean<?> getDeclaringBean() {
      return this.declaringBean;
   }

   public Set<Annotation> getQualifiers() {
      return Collections.unmodifiableSet(this.qualifiers);
   }

   public Type getExceptionType() {
      return this.exceptionType;
   }

   public void notify(ExceptionEvent<T> event, BeanManager beanManager) throws Exception {
      CreationalContext<?> ctx = null;

      try {
         ctx = beanManager.createCreationalContext((Contextual)null);
         Object handlerInstance = BeanProvider.getContextualReference(this.declaringBeanClass, new Annotation[0]);
         InjectableMethod<?> im = this.createInjectableMethod(this.handler, this.getDeclaringBean(), beanManager);
         im.invoke(handlerInstance, ctx, new OutboundParameterValueRedefiner(event, this));
      } finally {
         if (ctx != null) {
            ctx.release();
         }

      }

   }

   private <X> InjectableMethod<X> createInjectableMethod(AnnotatedMethod<X> handlerMethod, Bean<?> bean, BeanManager bm) {
      return new InjectableMethod(handlerMethod, bean, bm);
   }

   public boolean isBeforeHandler() {
      return this.before;
   }

   public int getOrdinal() {
      return this.ordinal;
   }

   public AnnotatedParameter<?> getHandlerParameter() {
      return this.handlerParameter;
   }

   public Method getJavaMethod() {
      return this.handler.getJavaMember();
   }

   public Set<InjectionPoint> getInjectionPoints(BeanManager bm) {
      if (this.injectionPoints == null) {
         this.injectionPoints = new HashSet(this.handler.getParameters().size() - 1);

         for(AnnotatedParameter<?> param : this.handler.getParameters()) {
            if (!param.equals(this.handlerParameter)) {
               this.injectionPoints.add(new ImmutableInjectionPoint(param, bm, this.getDeclaringBean(), false, false));
            }
         }
      }

      return new HashSet(this.injectionPoints);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && HandlerMethod.class.isAssignableFrom(o.getClass())) {
         HandlerMethod<?> that = (HandlerMethod)o;
         if (!this.qualifiers.equals(that.getQualifiers())) {
            return false;
         } else if (this.isBeforeHandler() != that.isBeforeHandler()) {
            return false;
         } else if (!this.exceptionType.equals(that.getExceptionType())) {
            return false;
         } else {
            return this.ordinal == that.getOrdinal();
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.declaringBeanClass.hashCode();
      result = 5 * result + this.qualifiers.hashCode();
      result = 5 * result + this.exceptionType.hashCode();
      result = 5 * result + this.ordinal;
      result = 5 * result + this.javaMethod.hashCode();
      result = 5 * result + this.handlerParameter.hashCode();
      return result;
   }

   public String toString() {
      return "{Qualifiers: " + this.qualifiers + ", Handles Type: " + this.exceptionType + ", Before: " + this.before + ", Precedence: " + this.ordinal + ", Method: " + this.handler.getJavaMember().getName() + "}";
   }
}
