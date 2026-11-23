package org.apache.openejb.core.ivm;

import java.io.ObjectStreamException;
import java.io.Serializable;
import javax.ejb.EJBHome;
import javax.ejb.EJBMetaData;
import org.apache.openejb.BeanType;
import org.apache.openejb.core.ServerFederation;
import org.apache.openejb.spi.ApplicationServer;
import org.apache.openejb.util.proxy.ProxyManager;

public class IntraVmMetaData implements EJBMetaData, Serializable {
   protected Class homeClass;
   protected Class remoteClass;
   protected Class keyClass;
   protected EJBHome homeStub;
   protected BeanType type;

   public IntraVmMetaData(Class homeInterface, Class remoteInterface, BeanType typeOfBean) {
      this(homeInterface, remoteInterface, (Class)null, typeOfBean);
   }

   public IntraVmMetaData(Class homeInterface, Class remoteInterface, Class primaryKeyClass, BeanType typeOfBean) {
      this.type = typeOfBean;
      if (homeInterface != null && remoteInterface != null) {
         if (typeOfBean.isEntity() && primaryKeyClass == null) {
            throw new IllegalArgumentException("Entity beans must have a primary key class");
         } else {
            this.type = typeOfBean;
            this.homeClass = homeInterface;
            this.remoteClass = remoteInterface;
            this.keyClass = primaryKeyClass;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Class getHomeInterfaceClass() {
      return this.homeClass;
   }

   public Class getRemoteInterfaceClass() {
      return this.remoteClass;
   }

   public Class getPrimaryKeyClass() {
      if (this.type.isEntity()) {
         return this.keyClass;
      } else {
         throw new UnsupportedOperationException("Session objects are private resources and do not have primary keys");
      }
   }

   public boolean isSession() {
      return this.type.isSession();
   }

   public boolean isStatelessSession() {
      return this.type == BeanType.STATELESS;
   }

   public boolean isSingletonSession() {
      return this.type == BeanType.SINGLETON;
   }

   public boolean isManagedSession() {
      return this.type == BeanType.MANAGED;
   }

   public boolean isStatefulSession() {
      return this.type == BeanType.STATEFUL;
   }

   public void setEJBHome(EJBHome home) {
      this.homeStub = home;
   }

   public EJBHome getEJBHome() {
      return this.homeStub;
   }

   protected Object writeReplace() throws ObjectStreamException {
      if (IntraVmCopyMonitor.isIntraVmCopyOperation()) {
         return new IntraVmArtifact(this);
      } else if (IntraVmCopyMonitor.isStatefulPassivationOperation()) {
         return this;
      } else if (IntraVmCopyMonitor.isCrossClassLoaderOperation()) {
         return this;
      } else {
         BaseEjbProxyHandler handler = (BaseEjbProxyHandler)ProxyManager.getInvocationHandler(this.homeStub);
         ApplicationServer applicationServer = ServerFederation.getApplicationServer();
         return applicationServer.getEJBMetaData(handler.getProxyInfo());
      }
   }
}
