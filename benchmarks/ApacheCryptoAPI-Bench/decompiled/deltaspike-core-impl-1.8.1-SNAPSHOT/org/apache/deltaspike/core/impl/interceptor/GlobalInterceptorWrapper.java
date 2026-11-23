package org.apache.deltaspike.core.impl.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedType;

class GlobalInterceptorWrapper implements AnnotatedType<Object> {
   private final AnnotatedType wrapped;
   private Map<Class<? extends Annotation>, Annotation> annotations;
   private Set<Annotation> annotationSet;

   GlobalInterceptorWrapper(AnnotatedType wrapped, Annotation priorityAnnotation) {
      this.wrapped = wrapped;
      Set<Annotation> originalAnnotationSet = wrapped.getAnnotations();
      this.annotations = new HashMap(originalAnnotationSet.size());

      for(Annotation originalAnnotation : originalAnnotationSet) {
         this.annotations.put(originalAnnotation.annotationType(), originalAnnotation);
      }

      this.annotations.put(priorityAnnotation.annotationType(), priorityAnnotation);
      this.annotationSet = new HashSet(this.annotations.size());
      this.annotationSet.addAll(this.annotations.values());
   }

   public Class getJavaClass() {
      return this.wrapped.getJavaClass();
   }

   public Set getConstructors() {
      return this.wrapped.getConstructors();
   }

   public Set getMethods() {
      return this.wrapped.getMethods();
   }

   public Set getFields() {
      return this.wrapped.getFields();
   }

   public Type getBaseType() {
      return this.wrapped.getBaseType();
   }

   public Set<Type> getTypeClosure() {
      return this.wrapped.getTypeClosure();
   }

   public <T extends Annotation> T getAnnotation(Class<T> targetClass) {
      return (T)(this.annotations.get(targetClass));
   }

   public Set<Annotation> getAnnotations() {
      return this.annotationSet;
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> targetClass) {
      return this.annotations.containsKey(targetClass);
   }
}
