package org.apache.meecrowave.openwebbeans;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.ws.rs.Path;
import org.apache.meecrowave.cxf.JAXRSFieldInjectionInterceptor;
import org.apache.meecrowave.cxf.MeecrowaveBus;
import org.apache.webbeans.container.AnnotatedTypeWrapper;

public class MeecrowaveExtension implements Extension {
   void addBeansFromJava(@Observes BeforeBeanDiscovery bbd, BeanManager bm) {
      Stream.of(MeecrowaveBus.class).forEach((type) -> bbd.addAnnotatedType(bm.createAnnotatedType(type)));
   }

   void enableContextFieldInjectionWorks(@Observes ProcessAnnotatedType<?> pat, BeanManager bm) {
      AnnotatedType<?> at = pat.getAnnotatedType();
      if (at.isAnnotationPresent(Path.class) && !at.isAnnotationPresent(JAXRSFieldInjectionInterceptor.Binding.class) && at.getAnnotations().stream().anyMatch((a) -> bm.isNormalScope(a.annotationType()))) {
         pat.setAnnotatedType(new JAXRSFIeldInjectionAT(this, at));
      }

   }

   private static class JAXRSFIeldInjectionAT<T> extends AnnotatedTypeWrapper<T> {
      private final Set<Annotation> annotations;

      private JAXRSFIeldInjectionAT(Extension extension, AnnotatedType<T> original) {
         super(extension, original);
         this.annotations = new HashSet(original.getAnnotations().size() + 1);
         this.annotations.addAll(original.getAnnotations());
         this.annotations.add(JAXRSFieldInjectionInterceptor.Binding.INSTANCE);
      }

      public Set<Annotation> getAnnotations() {
         return this.annotations;
      }

      public <T1 extends Annotation> T1 getAnnotation(Class<T1> t1Class) {
         return (T1)(t1Class == JAXRSFieldInjectionInterceptor.Binding.class ? (Annotation)t1Class.cast(JAXRSFieldInjectionInterceptor.Binding.INSTANCE) : super.getAnnotation(t1Class));
      }

      public boolean isAnnotationPresent(Class<? extends Annotation> aClass) {
         return JAXRSFieldInjectionInterceptor.Binding.class == aClass || super.isAnnotationPresent(aClass);
      }
   }
}
