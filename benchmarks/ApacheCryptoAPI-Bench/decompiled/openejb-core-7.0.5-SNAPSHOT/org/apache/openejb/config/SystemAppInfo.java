package org.apache.openejb.config;

import java.net.URI;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.CallbackInfo;
import org.apache.openejb.assembler.classic.ContainerInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.EnterpriseBeanInfo;
import org.apache.openejb.assembler.classic.InterceptorBindingInfo;
import org.apache.openejb.assembler.classic.InterceptorInfo;
import org.apache.openejb.assembler.classic.MethodConcurrencyInfo;
import org.apache.openejb.assembler.classic.MethodInfo;
import org.apache.openejb.assembler.classic.SingletonBeanInfo;
import org.apache.openejb.assembler.classic.SingletonSessionContainerInfo;

public final class SystemAppInfo {
   public static AppInfo preComputedInfo(ConfigurationFactory factory) {
      String singletonContainerId;
      try {
         singletonContainerId = findSingletonContainer(factory);
      } catch (OpenEJBException e) {
         throw new IllegalStateException(e);
      }

      EjbJarInfo ejbJarInfo = new EjbJarInfo();
      ejbJarInfo.moduleId = "openejb";
      ejbJarInfo.moduleName = ejbJarInfo.moduleId;
      ejbJarInfo.moduleUri = URI.create(ejbJarInfo.moduleId);
      ejbJarInfo.properties.setProperty("openejb.deploymentId.format", "{ejbName}");
      ejbJarInfo.properties.setProperty("openejb.jndiname.format", "{deploymentId}{interfaceType.openejbLegacyName}");
      SingletonBeanInfo deployer = new SingletonBeanInfo();
      deployer.ejbDeploymentId = "openejb/Deployer";
      deployer.ejbName = deployer.ejbDeploymentId;
      deployer.ejbClass = "org.apache.openejb.assembler.DeployerEjb";
      deployer.businessRemote.add("org.apache.openejb.assembler.Deployer");
      deployer.parents.add(deployer.ejbClass);
      deployer.transactionType = "BEAN";
      deployer.concurrencyType = "CONTAINER";
      deployer.containerId = singletonContainerId;
      ejbJarInfo.enterpriseBeans.add(deployer);
      SingletonBeanInfo configuration = new SingletonBeanInfo();
      configuration.ejbDeploymentId = "openejb/ConfigurationInfo";
      configuration.ejbName = deployer.ejbDeploymentId;
      configuration.ejbClass = "org.apache.openejb.assembler.classic.cmd.ConfigurationInfoEjb";
      configuration.businessRemote.add("org.apache.openejb.assembler.classic.cmd.ConfigurationInfo");
      configuration.parents.add(deployer.ejbClass);
      configuration.transactionType = "CONTAINER";
      configuration.concurrencyType = "CONTAINER";
      configuration.containerId = singletonContainerId;
      ejbJarInfo.enterpriseBeans.add(configuration);
      SingletonBeanInfo mejb = new SingletonBeanInfo();
      mejb.ejbDeploymentId = "MEJB";
      mejb.ejbName = deployer.ejbDeploymentId;
      mejb.ejbClass = "org.apache.openejb.mgmt.MEJBBean";
      mejb.home = "javax.management.j2ee.ManagementHome";
      mejb.remote = "javax.management.j2ee.Management";
      mejb.parents.add(deployer.ejbClass);
      mejb.transactionType = "CONTAINER";
      mejb.concurrencyType = "CONTAINER";
      mejb.containerId = singletonContainerId;
      ejbJarInfo.enterpriseBeans.add(mejb);

      for(EnterpriseBeanInfo ebi : ejbJarInfo.enterpriseBeans) {
         MethodInfo methodInfo = new MethodInfo();
         methodInfo.ejbDeploymentId = ebi.ejbDeploymentId;
         methodInfo.ejbName = ebi.ejbName;
         methodInfo.methodName = "*";
         methodInfo.className = ebi.ejbClass;
         MethodConcurrencyInfo methodConcurrencyInfo = new MethodConcurrencyInfo();
         methodConcurrencyInfo.concurrencyAttribute = "READ";
         methodConcurrencyInfo.methods.add(methodInfo);
         ejbJarInfo.methodConcurrency.add(methodConcurrencyInfo);
      }

      CallbackInfo callbackInfo = new CallbackInfo();
      callbackInfo.className = "org.apache.openejb.security.internal.InternalSecurityInterceptor";
      callbackInfo.method = "invoke";
      InterceptorInfo interceptorInfo = new InterceptorInfo();
      interceptorInfo.clazz = "org.apache.openejb.security.internal.InternalSecurityInterceptor";
      interceptorInfo.aroundInvoke.add(callbackInfo);
      ejbJarInfo.interceptors.add(interceptorInfo);
      InterceptorBindingInfo interceptorBindingInfo = new InterceptorBindingInfo();
      interceptorBindingInfo.ejbName = "*";
      interceptorBindingInfo.interceptors.add("org.apache.openejb.security.internal.InternalSecurityInterceptor");
      ejbJarInfo.interceptorBindings.add(interceptorBindingInfo);
      ejbJarInfo.mbeans.add("org.apache.openejb.assembler.monitoring.JMXDeployer");
      ejbJarInfo.uniqueId = "0";
      AppInfo appInfo = new AppInfo();
      appInfo.appId = ejbJarInfo.moduleId;
      appInfo.path = appInfo.appId;
      appInfo.ejbJars.add(ejbJarInfo);
      return appInfo;
   }

   private static String findSingletonContainer(ConfigurationFactory configFactory) throws OpenEJBException {
      for(ContainerInfo containerInfo : configFactory.getContainerInfos()) {
         if (SingletonSessionContainerInfo.class.isInstance(containerInfo)) {
            return containerInfo.id;
         }
      }

      if (configFactory.isOffline()) {
         throw new IllegalStateException("system application (openejb) needs a singleton container. Noone is defined and container is in offline mode. Please define one in tomee.xml.");
      } else {
         ContainerInfo containerInfo = (ContainerInfo)configFactory.configureService(SingletonSessionContainerInfo.class);
         configFactory.install(containerInfo);
         return containerInfo.id;
      }
   }

   private SystemAppInfo() {
   }
}
