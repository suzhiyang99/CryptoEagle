package org.apache.openejb.cdi;

import java.util.List;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanAttributes;
import org.apache.openejb.BeanContext;
import org.apache.openejb.assembler.classic.ProxyInterfaceResolver;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.ejb.common.component.EjbBeanBuilder;

public class OpenEJBBeanBuilder<A> extends EjbBeanBuilder<A, CdiEjbBean<A>> {
   private final BeanContext beanContext;

   public OpenEJBBeanBuilder(BeanContext bc, WebBeansContext webBeansContext, AnnotatedType<A> annotatedType, BeanAttributes<A> attributes) {
      super(webBeansContext, annotatedType, attributes);
      this.beanContext = bc;
   }

   protected CdiEjbBean<A> createBean(Class<A> beanClass, boolean beanEnabled) {
      return new CdiEjbBean<A>(this.beanContext, this.webBeansContext, this.annotatedType, this.beanAttributes);
   }

   protected A getInstance(CreationalContext<A> creationalContext) {
      List<Class> classes = this.beanContext.getBusinessLocalInterfaces();
      CurrentCreationalContext currentCreationalContext = (CurrentCreationalContext)this.beanContext.get(CurrentCreationalContext.class);
      CreationalContext existing = currentCreationalContext.get();
      currentCreationalContext.set(creationalContext);

      Object var6;
      try {
         if (classes.size() != 0 || !this.beanContext.isLocalbean()) {
            Class<?> mainInterface = (Class)classes.get(0);
            List<Class> interfaces = ProxyInterfaceResolver.getInterfaces(this.beanContext.getBeanClass(), mainInterface, classes);
            BeanContext.BusinessLocalHome home = this.beanContext.getBusinessLocalHome(interfaces, mainInterface);
            Object var8 = home.create();
            return (A)var8;
         }

         BeanContext.BusinessLocalBeanHome home = this.beanContext.getBusinessLocalBeanHome();
         var6 = home.create();
      } finally {
         currentCreationalContext.set(existing);
      }

      return (A)var6;
   }
}
