package org.apache.openejb.core.ivm;

import java.io.ObjectStreamException;
import java.io.Serializable;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.core.ServerFederation;
import org.apache.openejb.spi.ApplicationServer;
import org.apache.openejb.util.proxy.ProxyManager;

public class IntraVmHandle implements Serializable, HomeHandle, Handle {
   protected Object theProxy;

   public IntraVmHandle(Object proxy) {
      this.theProxy = proxy;
   }

   public EJBHome getEJBHome() {
      return (EJBHome)this.theProxy;
   }

   public EJBObject getEJBObject() {
      return (EJBObject)this.theProxy;
   }

   public Object getPrimaryKey() {
      return ((BaseEjbProxyHandler)ProxyManager.getInvocationHandler(this.theProxy)).primaryKey;
   }

   protected Object writeReplace() throws ObjectStreamException {
      if (IntraVmCopyMonitor.isIntraVmCopyOperation()) {
         return new IntraVmArtifact(this);
      } else if (IntraVmCopyMonitor.isStatefulPassivationOperation()) {
         return this;
      } else if (IntraVmCopyMonitor.isCrossClassLoaderOperation()) {
         return this;
      } else {
         BaseEjbProxyHandler handler = (BaseEjbProxyHandler)ProxyManager.getInvocationHandler(this.theProxy);
         if (this.theProxy instanceof EJBObject) {
            ApplicationServer applicationServer = ServerFederation.getApplicationServer();
            return applicationServer.getHandle(handler.getProxyInfo());
         } else if (this.theProxy instanceof EJBHome) {
            ApplicationServer applicationServer = ServerFederation.getApplicationServer();
            return applicationServer.getHomeHandle(handler.getProxyInfo());
         } else {
            throw new OpenEJBRuntimeException("Invalid proxy type. Handles are only supported by EJBObject types in EJB 1.1");
         }
      }
   }
}
