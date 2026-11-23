package org.apache.openejb.core.ivm;

import javax.ejb.EJBHome;
import javax.ejb.EJBMetaData;
import javax.ejb.EJBObject;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import org.apache.openejb.BeanContext;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.spi.ApplicationServer;

public class IntraVmServer implements ApplicationServer {
   public EJBMetaData getEJBMetaData(ProxyInfo pi) {
      BeanContext beanContext = pi.getBeanContext();
      IntraVmMetaData metaData = new IntraVmMetaData(beanContext.getHomeInterface(), beanContext.getRemoteInterface(), beanContext.getComponentType());
      metaData.setEJBHome(this.getEJBHome(pi));
      return metaData;
   }

   public Handle getHandle(ProxyInfo pi) {
      return new IntraVmHandle(this.getEJBObject(pi));
   }

   public HomeHandle getHomeHandle(ProxyInfo pi) {
      return new IntraVmHandle(this.getEJBHome(pi));
   }

   public EJBObject getEJBObject(ProxyInfo pi) {
      return (EJBObject)EjbObjectProxyHandler.createProxy(pi.getBeanContext(), pi.getPrimaryKey(), pi.getInterfaceType(), pi.getInterfaces(), pi.getInterface());
   }

   public Object getBusinessObject(ProxyInfo pi) {
      return EjbObjectProxyHandler.createProxy(pi.getBeanContext(), pi.getPrimaryKey(), pi.getInterfaceType(), pi.getInterfaces(), pi.getInterface());
   }

   public EJBHome getEJBHome(ProxyInfo pi) {
      return (EJBHome)EjbHomeProxyHandler.createHomeProxy(pi.getBeanContext(), pi.getInterfaceType());
   }
}
