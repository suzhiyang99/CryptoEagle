package org.apache.openejb.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.AssemblyDescriptor;
import org.apache.openejb.jee.ContainerTransaction;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.Interceptor;
import org.apache.openejb.jee.InterceptorBinding;
import org.apache.openejb.jee.Method;
import org.apache.openejb.jee.MethodPermission;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;

class RemoveWebServices implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         EjbJar ejbJar = ejbModule.getEjbJar();
         OpenejbJar openejbJar = ejbModule.getOpenejbJar();
         Map<String, EjbDeployment> deployments = openejbJar.getDeploymentsByEjbName();

         for(EnterpriseBean bean : ejbJar.getEnterpriseBeans()) {
            String ejbName = bean.getEjbName();
            EjbDeployment ejbDeployment = (EjbDeployment)deployments.get(ejbName);
            bean.getServiceRef().clear();
            if (bean instanceof SessionBean) {
               SessionBean sessionBean = (SessionBean)bean;
               if (sessionBean.getServiceEndpoint() != null) {
                  sessionBean.setServiceEndpoint((String)null);
                  if (sessionBean.getHome() == null && sessionBean.getLocalHome() == null && sessionBean.getBusinessLocal().size() <= 0 && sessionBean.getBusinessRemote().size() <= 0) {
                     ejbJar.removeEnterpriseBean(ejbName);
                     openejbJar.removeEjbDeployment(ejbDeployment);
                     AssemblyDescriptor assemblyDescriptor = ejbJar.getAssemblyDescriptor();
                     if (assemblyDescriptor != null) {
                        for(MethodPermission permission : this.copy(assemblyDescriptor.getMethodPermission())) {
                           for(Method method : this.copy(permission.getMethod())) {
                              if (method.getEjbName().equals(ejbName)) {
                                 permission.getMethod().remove(method);
                              }
                           }

                           if (permission.getMethod().size() == 0) {
                              assemblyDescriptor.getMethodPermission().remove(permission);
                           }
                        }

                        for(ContainerTransaction transaction : this.copy(assemblyDescriptor.getContainerTransaction())) {
                           for(Method method : this.copy(transaction.getMethod())) {
                              if (method.getEjbName().equals(ejbName)) {
                                 transaction.getMethod().remove(method);
                              }
                           }

                           if (transaction.getMethod().size() == 0) {
                              assemblyDescriptor.getContainerTransaction().remove(transaction);
                           }
                        }

                        for(InterceptorBinding binding : this.copy(assemblyDescriptor.getInterceptorBinding())) {
                           if (binding.getEjbName().equals(ejbName)) {
                              assemblyDescriptor.getInterceptorBinding().remove(binding);
                           }
                        }
                     }
                  }
               }
            }
         }

         for(Interceptor interceptor : ejbJar.getInterceptors()) {
            interceptor.getServiceRef().clear();
         }
      }

      return appModule;
   }

   public <T> List<T> copy(Collection<T> list) {
      return new ArrayList(list);
   }
}
