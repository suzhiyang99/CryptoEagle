package org.apache.openejb.config;

import javax.ejb.MessageDrivenContext;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.InjectionTarget;
import org.apache.openejb.jee.LifecycleCallback;
import org.apache.openejb.jee.MessageDrivenBean;
import org.apache.openejb.jee.ResourceEnvRef;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.SessionType;

public class LegacyProcessor implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         ClassLoader classLoader = ejbModule.getClassLoader();

         for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
            if (bean.getEjbClass() != null) {
               try {
                  Class<?> clazz = classLoader.loadClass(bean.getEjbClass());
                  process(clazz, bean);
               } catch (ClassNotFoundException var10) {
               }
            }
         }
      }

      return appModule;
   }

   public static void process(Class<?> clazz, EnterpriseBean bean) {
      if (bean instanceof SessionBean) {
         SessionBean sessionBean = (SessionBean)bean;
         if (sessionBean.getSessionType() == SessionType.STATEFUL && SessionSynchronization.class.isAssignableFrom(clazz)) {
            try {
               sessionBean.getAfterBegin().add(new LifecycleCallback(clazz.getMethod("afterBegin")));
               sessionBean.getBeforeCompletion().add(new LifecycleCallback(clazz.getMethod("beforeCompletion")));
               sessionBean.getAfterCompletion().add(new LifecycleCallback(clazz.getMethod("afterCompletion", Boolean.TYPE)));
            } catch (NoSuchMethodException var5) {
            }
         }

         if (javax.ejb.SessionBean.class.isAssignableFrom(clazz)) {
            ResourceEnvRef ref = new ResourceEnvRef("javax.ejb.SessionBean/sessionContext", SessionContext.class);
            InjectionTarget target = new InjectionTarget();
            target.setInjectionTargetClass(clazz);
            target.setInjectionTargetName("sessionContext");
            ref.getInjectionTarget().add(target);
            sessionBean.getResourceEnvRef().add(ref);
         }
      }

      if (bean instanceof MessageDrivenBean) {
         MessageDrivenBean messageDrivenBean = (MessageDrivenBean)bean;
         if (javax.ejb.MessageDrivenBean.class.isAssignableFrom(clazz)) {
            ResourceEnvRef ref = new ResourceEnvRef("javax.ejb.MessageDrivenBean/messageDrivenContext", MessageDrivenContext.class);
            InjectionTarget target = new InjectionTarget();
            target.setInjectionTargetClass(clazz);
            target.setInjectionTargetName("messageDrivenContext");
            ref.getInjectionTarget().add(target);
            messageDrivenBean.getResourceEnvRef().add(ref);
         }
      }

   }
}
