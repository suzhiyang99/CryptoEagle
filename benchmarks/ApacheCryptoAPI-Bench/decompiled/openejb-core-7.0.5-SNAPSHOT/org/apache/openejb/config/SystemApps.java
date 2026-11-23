package org.apache.openejb.config;

import org.apache.openejb.assembler.DeployerEjb;
import org.apache.openejb.assembler.classic.cmd.ConfigurationInfoEjb;
import org.apache.openejb.assembler.monitoring.JMXDeployer;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.Interceptor;
import org.apache.openejb.jee.InterceptorBinding;
import org.apache.openejb.jee.SingletonBean;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.mgmt.MEJBBean;
import org.apache.openejb.security.internal.InternalSecurityInterceptor;

public class SystemApps {
   public static EjbModule getSystemModule() {
      EjbModule module = new EjbModule(new EjbJar("openejb"), new OpenejbJar());
      OpenejbJar openejbJar = module.getOpenejbJar();
      EjbJar ejbJar = module.getEjbJar();
      ejbJar.addEnterpriseBean(singleton(DeployerEjb.class));
      ejbJar.addEnterpriseBean(singleton(ConfigurationInfoEjb.class));
      ejbJar.addEnterpriseBean(singleton(MEJBBean.class));
      ejbJar.addInterceptor(new Interceptor(InternalSecurityInterceptor.class));
      ejbJar.getAssemblyDescriptor().addInterceptorBinding(new InterceptorBinding("*", new String[]{InternalSecurityInterceptor.class.getName()}));
      module.getMbeans().add(JMXDeployer.class.getName());
      SingletonBean bean = (SingletonBean)ejbJar.addEnterpriseBean(new SingletonBean("openejb/WebappDeployer", "org.apache.tomee.catalina.deployer.WebappDeployer"));
      EjbDeployment deployment = openejbJar.addEjbDeployment(bean);
      deployment.getProperties().put("openejb.jndiname.format", "{deploymentId}{interfaceType.annotationName}");
      SingletonBean exceptionManager = (SingletonBean)ejbJar.addEnterpriseBean(new SingletonBean("openejb/ExceptionManagerFacade", "org.apache.tomee.catalina.facade.ExceptionManagerFacadeBean"));
      EjbDeployment exceptionMgr = openejbJar.addEjbDeployment(exceptionManager);
      exceptionMgr.getProperties().put("openejb.jndiname.format", "{deploymentId}{interfaceType.annotationName}");
      openejbJar.getProperties().put("openejb.deploymentId.format", "{ejbName}");
      openejbJar.getProperties().put("openejb.jndiname.format", "{deploymentId}{interfaceType.openejbLegacyName}");
      return module;
   }

   public static boolean isExtended() {
      return exists("org.apache.tomee.catalina.deployer.WebappDeployer");
   }

   private static SingletonBean singleton(Class<?> ejbClass) {
      return new SingletonBean((String)null, ejbClass);
   }

   private static boolean exists(String className) {
      try {
         SystemApps.class.getClassLoader().loadClass(className);
         return true;
      } catch (Throwable var2) {
         return false;
      }
   }
}
