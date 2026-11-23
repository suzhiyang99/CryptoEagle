package org.apache.openejb.cdi;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.InjectionPoint;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.webbeans.component.InjectionTargetBean;
import org.apache.webbeans.component.WebBeansType;
import org.apache.webbeans.component.creation.BeanAttributesBuilder;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.portable.InjectionTargetImpl;

public class ConstructorInjectionBean<T> extends InjectionTargetBean<T> {
   private static final Field INJECTION_TARGET_FIELD;
   private final boolean passivationCapable;

   public ConstructorInjectionBean(WebBeansContext webBeansContext, Class<T> returnType, AnnotatedType<T> at) {
      this(webBeansContext, returnType, at, (Boolean)null);
   }

   public ConstructorInjectionBean(WebBeansContext webBeansContext, Class<T> returnType, AnnotatedType<T> at, Boolean passivationCapable) {
      super(webBeansContext, WebBeansType.DEPENDENT, at, BeanAttributesBuilder.forContext(webBeansContext).newBeanAttibutes(at).build(), returnType);

      try {
         INJECTION_TARGET_FIELD.set(this, new ConstructorInjectionTarget(this.getAnnotatedType(), this.getInjectionPoints(), this.getWebBeansContext()));
      } catch (Exception e) {
         throw new OpenEJBRuntimeException(e);
      }

      if (passivationCapable != null) {
         this.passivationCapable = passivationCapable;
      } else {
         this.passivationCapable = super.isPassivationCapable();
      }

   }

   public boolean isPassivationCapable() {
      return this.passivationCapable;
   }

   static {
      try {
         INJECTION_TARGET_FIELD = InjectionTargetBean.class.getDeclaredField("injectionTarget");
      } catch (NoSuchFieldException e) {
         throw new OpenEJBRuntimeException(e);
      }

      INJECTION_TARGET_FIELD.setAccessible(true);
   }

   private static final class ConstructorInjectionTarget<T> extends InjectionTargetImpl<T> {
      public ConstructorInjectionTarget(AnnotatedType<T> annotatedType, Set<InjectionPoint> points, WebBeansContext webBeansContext) {
         super(annotatedType, points, webBeansContext, Collections.emptyList(), Collections.emptyList());
      }
   }
}
