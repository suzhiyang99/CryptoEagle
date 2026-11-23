package org.apache.openejb.cdi;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;
import org.apache.webbeans.annotation.NewLiteral;
import org.apache.webbeans.component.BeanAttributesImpl;
import org.apache.webbeans.component.NewBean;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.InjectionTargetFactoryImpl;

public class NewCdiEjbBean<T> extends CdiEjbBean<T> implements NewBean<T> {
   private static final Set<Annotation> QUALIFIERS = Collections.singleton(Annotation.class.cast(new NewLiteral()));
   private final String id;

   public NewCdiEjbBean(CdiEjbBean<T> that) {
      super(that.getBeanContext(), that.getWebBeansContext(), that.getBeanContext().getManagedClass(), that.getAnnotatedType(), new NewEjbInjectionTargetFactory(that.getAnnotatedType(), that.getWebBeansContext(), that.getInjectionTarget()), new BeanAttributesImpl(that));
      this.id = that.getId() + "NewBean";
      this.initInternals();
   }

   public Class<? extends Annotation> getScope() {
      return Dependent.class;
   }

   public Set<Class<? extends Annotation>> getStereotypes() {
      return Collections.emptySet();
   }

   public String getName() {
      return null;
   }

   public String getId() {
      return this.id;
   }

   public Set<Annotation> getQualifiers() {
      return QUALIFIERS;
   }

   private static final class NewEjbInjectionTargetFactory<T> extends InjectionTargetFactoryImpl<T> {
      private final InjectionTarget<T> injectionTarget;

      public NewEjbInjectionTargetFactory(AnnotatedType<T> annotatedType, WebBeansContext webBeansContext, InjectionTarget<T> it) {
         super(annotatedType, webBeansContext);
         this.injectionTarget = it;
      }

      public InjectionTarget<T> createInjectionTarget(Bean<T> bean) {
         return this.injectionTarget;
      }
   }
}
