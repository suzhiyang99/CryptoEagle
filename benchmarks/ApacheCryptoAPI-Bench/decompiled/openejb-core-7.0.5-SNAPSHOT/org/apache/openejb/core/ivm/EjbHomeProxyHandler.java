package org.apache.openejb.core.ivm;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.ejb.AccessLocalException;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBException;
import javax.ejb.EJBHome;
import javax.ejb.Handle;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.InvalidateReferenceException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.ServerFederation;
import org.apache.openejb.core.entity.EntityEjbHomeHandler;
import org.apache.openejb.core.managed.ManagedHomeHandler;
import org.apache.openejb.core.singleton.SingletonEjbHomeHandler;
import org.apache.openejb.core.stateful.StatefulEjbHomeHandler;
import org.apache.openejb.core.stateless.StatelessEjbHomeHandler;
import org.apache.openejb.spi.ApplicationServer;
import org.apache.openejb.threads.task.CUCallable;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.proxy.LocalBeanProxyFactory;
import org.apache.openejb.util.proxy.ProxyManager;

public abstract class EjbHomeProxyHandler extends BaseEjbProxyHandler {
   public static final Logger logger;
   private final Map<String, MethodType> dispatchTable = new HashMap();

   public EjbHomeProxyHandler(BeanContext beanContext, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      super(beanContext, (Object)null, interfaceType, interfaces, mainInterface);
      this.dispatchTable.put("create", EjbHomeProxyHandler.MethodType.CREATE);
      this.dispatchTable.put("getEJBMetaData", EjbHomeProxyHandler.MethodType.META_DATA);
      this.dispatchTable.put("getHomeHandle", EjbHomeProxyHandler.MethodType.HOME_HANDLE);
      this.dispatchTable.put("remove", EjbHomeProxyHandler.MethodType.REMOVE);
      if (interfaceType.isHome()) {
         Class homeInterface = beanContext.getInterface(interfaceType);
         Method[] methods = homeInterface.getMethods();

         for(Method method : methods) {
            if (method.getName().startsWith("create")) {
               this.dispatchTable.put(method.getName(), EjbHomeProxyHandler.MethodType.CREATE);
            } else if (method.getName().startsWith("find")) {
               this.dispatchTable.put(method.getName(), EjbHomeProxyHandler.MethodType.FIND);
            }
         }
      }

   }

   public void invalidateReference() {
      throw new IllegalStateException("A home reference must never be invalidated!");
   }

   protected static EjbHomeProxyHandler createHomeHandler(BeanContext beanContext, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      switch (beanContext.getComponentType()) {
         case STATEFUL:
            return new StatefulEjbHomeHandler(beanContext, interfaceType, interfaces, mainInterface);
         case STATELESS:
            return new StatelessEjbHomeHandler(beanContext, interfaceType, interfaces, mainInterface);
         case SINGLETON:
            return new SingletonEjbHomeHandler(beanContext, interfaceType, interfaces, mainInterface);
         case MANAGED:
            return new ManagedHomeHandler(beanContext, interfaceType, interfaces, mainInterface);
         case CMP_ENTITY:
         case BMP_ENTITY:
            return new EntityEjbHomeHandler(beanContext, interfaceType, interfaces, mainInterface);
         default:
            throw new IllegalStateException("Component type does not support rpc interfaces: " + beanContext.getComponentType());
      }
   }

   public static Object createHomeProxy(BeanContext beanContext, InterfaceType interfaceType) {
      return createHomeProxy(beanContext, interfaceType, (List)null, interfaceType.isRemote() ? beanContext.getRemoteInterface() : beanContext.getLocalInterface());
   }

   public static Object createHomeProxy(BeanContext beanContext, InterfaceType interfaceType, List<Class> objectInterfaces, Class mainInterface) {
      if (!interfaceType.isHome()) {
         throw new IllegalArgumentException("InterfaceType is not a Home type: " + interfaceType);
      } else {
         try {
            EjbHomeProxyHandler handler = createHomeHandler(beanContext, interfaceType, objectInterfaces, mainInterface);
            List<Class> proxyInterfaces = new ArrayList(2);
            Class homeInterface = beanContext.getInterface(interfaceType);
            proxyInterfaces.add(homeInterface);
            proxyInterfaces.add(IntraVmProxy.class);
            if (BeanType.STATEFUL.equals(beanContext.getComponentType()) || BeanType.MANAGED.equals(beanContext.getComponentType())) {
               proxyInterfaces.add(BeanContext.Removable.class);
            }

            return ProxyManager.newProxyInstance((Class[])((Class[])proxyInterfaces.toArray(new Class[proxyInterfaces.size()])), handler);
         } catch (Exception e) {
            throw new OpenEJBRuntimeException("Can't create EJBHome stub" + e.getMessage(), e);
         }
      }
   }

   public Object createProxy(Object primaryKey, Class mainInterface) {
      try {
         InterfaceType objectInterfaceType = this.interfaceType.getCounterpart();
         BeanType type = this.getBeanContext().getComponentType();
         EjbObjectProxyHandler handler = this.newEjbObjectHandler(this.getBeanContext(), primaryKey, objectInterfaceType, this.getInterfaces(), mainInterface);
         if ((InterfaceType.LOCALBEAN.equals(objectInterfaceType) || this.getBeanContext().getComponentType().equals(BeanType.MANAGED)) && !this.getBeanContext().isDynamicallyImplemented()) {
            return LocalBeanProxyFactory.constructProxy(((BeanContext.ProxyClass)handler.getBeanContext().get(BeanContext.ProxyClass.class)).getProxy(), handler);
         } else {
            List<Class> proxyInterfaces = new ArrayList(handler.getInterfaces().size() + 2);
            proxyInterfaces.addAll(handler.getInterfaces());
            proxyInterfaces.add(Serializable.class);
            proxyInterfaces.add(IntraVmProxy.class);
            if (BeanType.STATEFUL.equals(type) || BeanType.MANAGED.equals(type)) {
               proxyInterfaces.add(BeanContext.Removable.class);
            }

            return ProxyManager.newProxyInstance((Class[])((Class[])proxyInterfaces.toArray(new Class[proxyInterfaces.size()])), handler);
         }
      } catch (IllegalAccessException iae) {
         throw new OpenEJBRuntimeException("Could not create IVM proxy for " + this.getInterfaces().get(0), iae);
      }
   }

   protected abstract EjbObjectProxyHandler newEjbObjectHandler(BeanContext var1, Object var2, InterfaceType var3, List<Class> var4, Class var5);

   protected Object _invoke(Object proxy, Class interfce, Method method, Object[] args) throws Throwable {
      String methodName = method.getName();
      if (logger.isDebugEnabled()) {
         logger.debug("EjbHomeProxyHandler: invoking method " + methodName + " on " + this.deploymentID);
      }

      try {
         MethodType operation = (MethodType)this.dispatchTable.get(methodName);
         Object retValue;
         if (operation == null) {
            retValue = this.homeMethod(interfce, method, args, proxy);
         } else {
            switch (operation) {
               case CREATE:
                  retValue = this.create(interfce, method, args, proxy);
                  break;
               case FIND:
                  retValue = this.findX(interfce, method, args, proxy);
                  break;
               case META_DATA:
                  retValue = this.getEJBMetaData(method, args, proxy);
                  break;
               case HOME_HANDLE:
                  retValue = this.getHomeHandle(method, args, proxy);
                  break;
               case REMOVE:
                  Class type = method.getParameterTypes()[0];
                  if (Handle.class.isAssignableFrom(type)) {
                     retValue = this.removeWithHandle(interfce, method, args, proxy);
                  } else {
                     retValue = this.removeByPrimaryKey(interfce, method, args, proxy);
                  }
                  break;
               default:
                  throw new OpenEJBRuntimeException("Inconsistent internal state: value " + operation + " for operation " + methodName);
            }
         }

         if (logger.isDebugEnabled()) {
            logger.debug("EjbHomeProxyHandler: finished invoking method " + method.getName() + ". Return value:" + retValue);
         }

         return retValue;
      } catch (RemoteException re) {
         if (this.interfaceType.isLocal()) {
            throw (new EJBException(re.getMessage())).initCause(re.detail);
         } else {
            throw re;
         }
      } catch (InvalidateReferenceException ire) {
         Throwable cause = ire.getRootCause();
         if (cause instanceof RemoteException && this.interfaceType.isLocal()) {
            RemoteException re = (RemoteException)cause;
            Throwable detail = (Throwable)(re.detail != null ? re.detail : re);
            cause = (new EJBException(re.getMessage())).initCause(detail);
         }

         throw cause;
      } catch (ApplicationException ae) {
         Throwable exc = (Throwable)(ae.getRootCause() != null ? ae.getRootCause() : ae);
         if (exc instanceof EJBAccessException) {
            if (this.interfaceType.isBusiness()) {
               throw exc;
            } else if (this.interfaceType.isLocal()) {
               throw (AccessLocalException)(new AccessLocalException(exc.getMessage())).initCause(exc);
            } else {
               try {
                  throw (new AccessException(exc.getMessage())).initCause(exc);
               } catch (IllegalStateException var10) {
                  throw new AccessException(exc.getMessage(), (Exception)exc);
               }
            }
         } else {
            throw exc;
         }
      } catch (SystemException se) {
         if (this.interfaceType.isLocal()) {
            throw (new EJBException("Container has suffered a SystemException")).initCause(se.getRootCause());
         } else {
            throw new RemoteException("Container has suffered a SystemException", se.getRootCause());
         }
      } catch (OpenEJBException oe) {
         if (this.interfaceType.isLocal()) {
            throw (new EJBException("Unknown Container Exception")).initCause(oe.getRootCause());
         } else {
            throw new RemoteException("Unknown Container Exception", oe.getRootCause());
         }
      } catch (Throwable t) {
         logger.debug("EjbHomeProxyHandler: finished invoking method " + method.getName() + " with exception:" + t, t);
         throw t;
      }
   }

   protected Object homeMethod(final Class interfce, final Method method, final Object[] args, Object proxy) throws Throwable {
      this.checkAuthorization(method);
      BeanContext beanContext = this.getBeanContext();
      return beanContext.isAsynchronous(method) ? beanContext.getModuleContext().getAppContext().getAsynchronousPool().invoke(new CUCallable(new Callable<Object>() {
         public Object call() throws Exception {
            try {
               return EjbHomeProxyHandler.this.homeMethodInvoke(interfce, method, args);
            } catch (ApplicationException ae) {
               EjbHomeProxyHandler.logger.error("EjbHomeProxyHandler: Asynchronous call to '" + interfce.getSimpleName() + "' on '" + method.getName() + "' failed", (Throwable)ae);
               throw ae;
            }
         }
      }), method.getReturnType() == Void.TYPE) : this.homeMethodInvoke(interfce, method, args);
   }

   private Object homeMethodInvoke(Class interfce, Method method, Object[] args) throws OpenEJBException {
      return this.container.invoke(this.deploymentID, this.interfaceType, interfce, method, args, (Object)null);
   }

   protected Object create(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      if (this.container.getBeanContext(this.deploymentID) == null) {
         BeanContext bc = this.getBeanContext();
         synchronized(bc.getId()) {
            if (this.container.getBeanContext(this.deploymentID) == null) {
               this.container.deploy(bc);
               this.container.start(bc);
            }
         }
      }

      ProxyInfo proxyInfo = (ProxyInfo)this.container.invoke(this.deploymentID, this.interfaceType, interfce, method, args, (Object)null);

      assert proxyInfo != null : "Container returned a null ProxyInfo: ContainerID=" + this.container.getContainerID();

      return this.createProxy(proxyInfo.getPrimaryKey(), this.getMainInterface());
   }

   protected abstract Object findX(Class var1, Method var2, Object[] var3, Object var4) throws Throwable;

   protected Object getEJBMetaData(Method method, Object[] args, Object proxy) throws Throwable {
      this.checkAuthorization(method);
      IntraVmMetaData metaData = new IntraVmMetaData(this.getBeanContext().getHomeInterface(), this.getBeanContext().getRemoteInterface(), this.getBeanContext().getPrimaryKeyClass(), this.getBeanContext().getComponentType());
      metaData.setEJBHome((EJBHome)proxy);
      return metaData;
   }

   protected Object getHomeHandle(Method method, Object[] args, Object proxy) throws Throwable {
      this.checkAuthorization(method);
      return new IntraVmHandle(proxy);
   }

   public ProxyInfo getProxyInfo() {
      if (this.getMainInterface() == null) {
         throw new IllegalStateException("no main interface");
      } else {
         return new ProxyInfo(this.getBeanContext(), (Object)null, this.getBeanContext().getInterfaces(this.interfaceType), this.interfaceType, this.getMainInterface());
      }
   }

   protected Object _writeReplace(Object proxy) throws ObjectStreamException {
      if (IntraVmCopyMonitor.isIntraVmCopyOperation()) {
         return new IntraVmArtifact(proxy);
      } else if (IntraVmCopyMonitor.isStatefulPassivationOperation()) {
         return proxy;
      } else if (IntraVmCopyMonitor.isCrossClassLoaderOperation()) {
         return proxy;
      } else if (!this.interfaceType.isRemote()) {
         return proxy;
      } else {
         ApplicationServer applicationServer = ServerFederation.getApplicationServer();
         return applicationServer.getEJBHome(this.getProxyInfo());
      }
   }

   protected Object removeWithHandle(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      IntraVmHandle handle = (IntraVmHandle)args[0];
      Object primKey = handle.getPrimaryKey();

      EjbObjectProxyHandler stub;
      try {
         stub = (EjbObjectProxyHandler)ProxyManager.getInvocationHandler(handle.getEJBObject());
      } catch (IllegalArgumentException var9) {
         stub = null;
      }

      this.container.invoke(this.deploymentID, this.interfaceType, interfce, method, args, primKey);
      if (stub != null) {
         this.invalidateAllHandlers(stub.getRegistryId());
      }

      return null;
   }

   protected abstract Object removeByPrimaryKey(Class var1, Method var2, Object[] var3, Object var4) throws Throwable;

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }

   private static enum MethodType {
      CREATE,
      FIND,
      HOME_HANDLE,
      META_DATA,
      REMOVE;
   }
}
