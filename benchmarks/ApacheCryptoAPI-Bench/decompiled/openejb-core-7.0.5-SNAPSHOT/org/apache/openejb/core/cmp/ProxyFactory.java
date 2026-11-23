package org.apache.openejb.core.cmp;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EntityBean;
import org.apache.openejb.BeanContext;
import org.apache.openejb.RpcContainer;
import org.apache.openejb.core.entity.EntityEjbHomeHandler;
import org.apache.openejb.util.proxy.ProxyManager;

public class ProxyFactory {
   private final BeanContext beanContext;
   private final KeyGenerator keyGenerator;
   private final Class remoteInterface;
   private final EntityEjbHomeHandler remoteHandler;
   private final Class localInterface;
   private final EntityEjbHomeHandler localHandler;

   public ProxyFactory(BeanContext beanContext) {
      this.beanContext = beanContext;
      this.keyGenerator = beanContext.getKeyGenerator();
      this.remoteInterface = beanContext.getRemoteInterface();
      if (this.remoteInterface != null) {
         EJBHome homeProxy = beanContext.getEJBHome();
         this.remoteHandler = (EntityEjbHomeHandler)ProxyManager.getInvocationHandler(homeProxy);
      } else {
         this.remoteHandler = null;
      }

      this.localInterface = beanContext.getLocalInterface();
      if (this.localInterface != null) {
         EJBLocalHome localHomeProxy = beanContext.getEJBLocalHome();
         this.localHandler = (EntityEjbHomeHandler)ProxyManager.getInvocationHandler(localHomeProxy);
      } else {
         this.localHandler = null;
      }

   }

   public Object createRemoteProxy(EntityBean bean, RpcContainer container) {
      Object primaryKey = this.keyGenerator.getPrimaryKey(bean);
      Object proxy = this.remoteHandler.createProxy(primaryKey, this.beanContext.getRemoteInterface());
      return proxy;
   }

   public Object createLocalProxy(EntityBean bean, RpcContainer container) {
      Object primaryKey = this.keyGenerator.getPrimaryKey(bean);
      Object proxy = this.localHandler.createProxy(primaryKey, this.beanContext.getLocalInterface());
      return proxy;
   }
}
