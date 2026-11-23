package org.apache.openejb.config;

import org.apache.openejb.jee.AssemblyDescriptor;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.Interceptor;

public class EmptyEjbJar extends EjbJar {
   public void setAssemblyDescriptor(AssemblyDescriptor value) {
   }

   public void setEnterpriseBeans(EnterpriseBean[] v) {
   }

   public EnterpriseBean[] getEnterpriseBeans() {
      return super.getEnterpriseBeans();
   }

   public <T extends EnterpriseBean> T addEnterpriseBean(T bean) {
      if (CompManagedBean.class.isInstance(bean)) {
         super.addEnterpriseBean(bean);
      }

      return bean;
   }

   public <T extends EnterpriseBean> EjbJar enterpriseBean(T bean) {
      return this;
   }

   public Interceptor addInterceptor(Interceptor interceptor) {
      return interceptor;
   }
}
