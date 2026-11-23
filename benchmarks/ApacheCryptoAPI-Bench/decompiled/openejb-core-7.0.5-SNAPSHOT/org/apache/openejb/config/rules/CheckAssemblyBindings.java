package org.apache.openejb.config.rules;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.AssemblyDescriptor;
import org.apache.openejb.jee.ContainerTransaction;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.Interceptor;
import org.apache.openejb.jee.InterceptorBinding;
import org.apache.openejb.jee.Method;
import org.apache.openejb.jee.MethodPermission;
import org.apache.openejb.util.Join;

public class CheckAssemblyBindings extends ValidationBase {
   public void validate(EjbModule ejbModule) {
      this.checkUnusedInterceptors(ejbModule);
      Map<String, EnterpriseBean> ejbsByName = ejbModule.getEjbJar().getEnterpriseBeansByEjbName();
      AssemblyDescriptor assembly = ejbModule.getEjbJar().getAssemblyDescriptor();
      if (assembly != null) {
         for(InterceptorBinding binding : assembly.getInterceptorBinding()) {
            List<String> interceptorClasses = binding.getInterceptorClass();
            if (binding.getInterceptorOrder() != null) {
               interceptorClasses.addAll(binding.getInterceptorOrder().getInterceptorClass());
            }

            if (binding.getEjbName() != null && !binding.getEjbName().equals("*") && !ejbsByName.containsKey(binding.getEjbName())) {
               this.fail("InterceptorBinding", "interceptorBinding.noSuchEjbName", new Object[]{binding.getEjbName(), Join.join(",", (Collection)interceptorClasses)});
            }

            if (binding.getMethod() != null && binding.getEjbName() == null) {
               this.fail("InterceptorBinding", "interceptorBinding.ejbNameRequiredWithMethod", new Object[]{binding.getMethod().getMethodName(), Join.join(",", (Collection)interceptorClasses)});
            }
         }

         for(MethodPermission permission : assembly.getMethodPermission()) {
            for(Method method : permission.getMethod()) {
               if (method.getEjbName() == null) {
                  this.fail("MethodPermission", "methodPermission.ejbNameRequired", new Object[]{method.getMethodName(), Join.join(",", (Collection)permission.getRoleName())});
               } else if (!method.getEjbName().equals("*") && !ejbsByName.containsKey(method.getEjbName())) {
                  this.fail("MethodPermission", "methodPermission.noSuchEjbName", new Object[]{method.getEjbName(), method.getMethodName(), Join.join(",", (Collection)permission.getRoleName())});
               }
            }
         }

         for(ContainerTransaction transaction : assembly.getContainerTransaction()) {
            for(Method method : transaction.getMethod()) {
               if (method.getEjbName() == null) {
                  this.fail("ContainerTransaction", "containerTransaction.ejbNameRequired", new Object[]{method.getMethodName(), transaction.getTransAttribute()});
               } else if (!method.getEjbName().equals("*") && !ejbsByName.containsKey(method.getEjbName())) {
                  this.fail("ContainerTransaction", "containerTransaction.noSuchEjbName", new Object[]{method.getEjbName(), method.getMethodName(), transaction.getTransAttribute()});
               }
            }
         }

      }
   }

   private void checkUnusedInterceptors(EjbModule ejbModule) {
      AssemblyDescriptor assembly = ejbModule.getEjbJar().getAssemblyDescriptor();
      Interceptor[] interceptorsArray = ejbModule.getEjbJar().getInterceptors();
      List<Interceptor> interceptors = Arrays.asList(interceptorsArray);
      Set<String> interceptorClassNames = new HashSet(interceptors.size());

      for(Interceptor interceptor : interceptors) {
         interceptorClassNames.add(interceptor.getInterceptorClass());
      }

      Set<String> interceptorClassNamesUsedInBindings = new HashSet();

      for(InterceptorBinding binding : assembly.getInterceptorBinding()) {
         List<String> interceptorClass = binding.getInterceptorClass();
         interceptorClassNamesUsedInBindings.addAll(interceptorClass);
      }

      Set<String> unusedInterceptors = new HashSet();

      for(String clazz : interceptorClassNames) {
         if (!interceptorClassNamesUsedInBindings.contains(clazz)) {
            unusedInterceptors.add(clazz);
         }
      }

      for(String clazz : unusedInterceptors) {
         this.warn("Interceptors", "interceptor.unused", new Object[]{clazz});
      }

   }
}
