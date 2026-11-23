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
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.Interceptor;
import org.apache.openejb.jee.InterceptorBinding;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.MessageDestinationRef;
import org.apache.openejb.jee.MessageDrivenBean;
import org.apache.openejb.jee.Method;
import org.apache.openejb.jee.MethodPermission;
import org.apache.openejb.jee.Relationships;
import org.apache.openejb.jee.ResourceEnvRef;
import org.apache.openejb.jee.ResourceRef;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.jee.oejb3.ResourceLink;

class DebuggableVmHackery implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         EjbJar ejbJar = ejbModule.getEjbJar();
         OpenejbJar openejbJar = ejbModule.getOpenejbJar();
         Map<String, EjbDeployment> deployments = openejbJar.getDeploymentsByEjbName();
         ejbJar.setRelationships((Relationships)null);
         List<String> removed = new ArrayList();

         for(EnterpriseBean bean : ejbJar.getEnterpriseBeans()) {
            String ejbName = bean.getEjbName();
            EjbDeployment ejbDeployment = (EjbDeployment)deployments.get(ejbName);
            this.pruneRefs(bean, ejbDeployment);
            if (bean instanceof MessageDrivenBean || bean instanceof EntityBean) {
               ejbJar.removeEnterpriseBean(ejbName);
               openejbJar.removeEjbDeployment(ejbDeployment);
               removed.add(ejbName);
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

         for(EnterpriseBean bean : ejbJar.getEnterpriseBeans()) {
            bean.getEjbLocalRefMap().keySet().removeAll(removed);
            bean.getEjbRefMap().keySet().removeAll(removed);
         }

         for(Interceptor interceptor : ejbJar.getInterceptors()) {
            this.pruneRefs(interceptor, new EjbDeployment());
         }
      }

      return appModule;
   }

   private void pruneRefs(JndiConsumer bean, EjbDeployment ejbDeployment) {
      for(ResourceRef ref : this.copy(bean.getResourceRef())) {
         if (ref.getResType().startsWith("javax.jms.")) {
            ResourceLink resourceLink = ejbDeployment.getResourceLink(ref.getName());
            ejbDeployment.getResourceLink().remove(resourceLink);
            bean.getResourceRef().remove(ref);
         }
      }

      for(ResourceEnvRef ref : bean.getResourceEnvRef()) {
         ResourceLink resourceLink = ejbDeployment.getResourceLink(ref.getName());
         ejbDeployment.getResourceLink().remove(resourceLink);
      }

      bean.getResourceEnvRef().clear();

      for(MessageDestinationRef ref : bean.getMessageDestinationRef()) {
         ResourceLink resourceLink = ejbDeployment.getResourceLink(ref.getName());
         ejbDeployment.getResourceLink().remove(resourceLink);
      }

      bean.getMessageDestinationRef().clear();
      bean.getPersistenceContextRef().clear();
      bean.getPersistenceUnitRef().clear();
   }

   public <T> List<T> copy(Collection<T> list) {
      return new ArrayList(list);
   }
}
