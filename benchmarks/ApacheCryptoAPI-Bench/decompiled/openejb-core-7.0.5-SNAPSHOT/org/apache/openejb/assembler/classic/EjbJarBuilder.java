package org.apache.openejb.assembler.classic;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.Injection;
import org.apache.openejb.ModuleContext;
import org.apache.openejb.ModuleTestContext;
import org.apache.openejb.OpenEJBException;

public class EjbJarBuilder {
   private final Properties props;
   private final AppContext context;

   public EjbJarBuilder(Properties props, AppContext context) {
      this.props = props;
      this.context = context;
   }

   public HashMap<String, BeanContext> build(EjbJarInfo ejbJar, Collection<Injection> appInjections, ClassLoader classLoader) throws OpenEJBException {
      InjectionBuilder injectionBuilder = new InjectionBuilder(classLoader);
      List<Injection> moduleInjections = injectionBuilder.buildInjections(ejbJar.moduleJndiEnc);
      moduleInjections.addAll(appInjections);
      Context moduleJndiContext = (new JndiEncBuilder(ejbJar.moduleJndiEnc, moduleInjections, (String)null, ejbJar.moduleName, ejbJar.moduleUri, ejbJar.uniqueId, classLoader, this.context.getProperties())).build(JndiEncBuilder.JndiScope.module);
      HashMap<String, BeanContext> deployments = new HashMap();
      ModuleContext moduleContext = (ModuleContext)(!ejbJar.properties.containsKey("openejb.test.module") ? new ModuleContext(ejbJar.moduleName, ejbJar.moduleUri, ejbJar.uniqueId, this.context, moduleJndiContext, classLoader) : new ModuleTestContext(ejbJar.moduleName, ejbJar.moduleUri, ejbJar.uniqueId, this.context, moduleJndiContext, classLoader));
      moduleContext.getProperties().putAll(ejbJar.properties);
      InterceptorBindingBuilder interceptorBindingBuilder = new InterceptorBindingBuilder(classLoader, ejbJar);
      MethodScheduleBuilder methodScheduleBuilder = new MethodScheduleBuilder();

      for(EnterpriseBeanInfo ejbInfo : ejbJar.enterpriseBeans) {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(moduleContext.getClassLoader());

         try {
            EnterpriseBeanBuilder deploymentBuilder = new EnterpriseBeanBuilder(ejbInfo, moduleContext, moduleInjections);
            BeanContext bean = deploymentBuilder.build();
            interceptorBindingBuilder.build(bean, ejbInfo);
            methodScheduleBuilder.build(bean, ejbInfo);
            deployments.put(ejbInfo.ejbDeploymentId, bean);
            Container container = (Container)this.props.get(ejbInfo.containerId);
            if (container == null) {
               throw new IllegalStateException("Container does not exist: " + ejbInfo.containerId + ".  Referenced by deployment: " + bean.getDeploymentID());
            }

            bean.setContainer(container);
         } catch (Throwable e) {
            throw new OpenEJBException("Error building bean '" + ejbInfo.ejbName + "'.  Exception: " + e.getClass() + ": " + e.getMessage(), e);
         } finally {
            Thread.currentThread().setContextClassLoader(loader);
         }
      }

      return deployments;
   }
}
