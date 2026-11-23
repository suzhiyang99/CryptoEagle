package org.apache.openejb.core.ivm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.ejb.AccessLocalException;
import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRequiredException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.NoSuchEJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.TransactionRequiredLocalException;
import javax.ejb.TransactionRolledbackLocalException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionRolledbackException;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.RpcContainer;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.ThreadContextListener;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.proxy.LocalBeanProxyFactory;

public abstract class BaseEjbProxyHandler implements InvocationHandler, Serializable {
   private static final String OPENEJB_LOCALCOPY = "openejb.localcopy";
   private static final boolean REMOTE_COPY_ENABLED = parseRemoteCopySetting();
   public final Object deploymentID;
   public final Object primaryKey;
   protected final InterfaceType interfaceType;
   private final ReentrantLock lock = new ReentrantLock();
   public boolean inProxyMap;
   public transient RpcContainer container;
   protected boolean isInvalidReference;
   protected Object clientIdentity;
   private IntraVmCopyMonitor.State strategy;
   private transient WeakReference<BeanContext> beanContextRef;
   private boolean doIntraVmCopy;
   private boolean doCrossClassLoaderCopy;
   private transient WeakHashMap<Class, Object> interfaces;
   private transient WeakReference<Class> mainInterface;

   public BaseEjbProxyHandler(BeanContext beanContext, Object pk, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      this.strategy = IntraVmCopyMonitor.State.NONE;
      this.container = (RpcContainer)beanContext.getContainer();
      this.deploymentID = beanContext.getDeploymentID();
      this.interfaceType = interfaceType;
      this.primaryKey = pk;
      this.setBeanContext(beanContext);
      if (interfaces == null || interfaces.size() == 0) {
         InterfaceType objectInterfaceType = interfaceType.isHome() ? interfaceType.getCounterpart() : interfaceType;
         interfaces = new ArrayList(beanContext.getInterfaces(objectInterfaceType));
      }

      if (mainInterface == null && interfaces.size() == 1) {
         mainInterface = (Class)interfaces.get(0);
      }

      this.setInterfaces(interfaces);
      this.setMainInterface(mainInterface);
      if (mainInterface == null) {
         throw new IllegalArgumentException("No mainInterface: otherwise di: " + beanContext + " InterfaceType: " + interfaceType + " interfaces: " + interfaces);
      } else {
         this.setDoIntraVmCopy(REMOTE_COPY_ENABLED && !interfaceType.isLocal() && !interfaceType.isLocalBean());
      }
   }

   private static boolean parseRemoteCopySetting() {
      return SystemInstance.get().getOptions().get("openejb.localcopy", true);
   }

   protected void setDoIntraVmCopy(boolean doIntraVmCopy) {
      this.doIntraVmCopy = doIntraVmCopy;
      this.setStrategy();
   }

   protected void setDoCrossClassLoaderCopy(boolean doCrossClassLoaderCopy) {
      this.doCrossClassLoaderCopy = doCrossClassLoaderCopy;
      this.setStrategy();
   }

   private void setStrategy() {
      if (!this.doIntraVmCopy) {
         this.strategy = IntraVmCopyMonitor.State.NONE;
      } else if (this.doCrossClassLoaderCopy) {
         this.strategy = IntraVmCopyMonitor.State.CLASSLOADER_COPY;
      } else {
         this.strategy = IntraVmCopyMonitor.State.COPY;
      }

   }

   protected Class<?> getInvokedInterface(Method method) {
      Class mainInterface = this.getMainInterface();
      if (this.interfaceType.isHome()) {
         return mainInterface;
      } else if (this.interfaceType.isLocalBean()) {
         return mainInterface;
      } else {
         Class declaringClass = method.getDeclaringClass();
         if (mainInterface != null && declaringClass.isAssignableFrom(mainInterface)) {
            return mainInterface;
         } else {
            for(Class secondaryInterface : this.interfaces.keySet()) {
               if (declaringClass.isAssignableFrom(secondaryInterface)) {
                  return secondaryInterface;
               }
            }

            throw new IllegalStateException("Received method invocation and cannot determine corresponding business interface: method=" + method);
         }
      }
   }

   public Class getMainInterface() {
      return (Class)this.mainInterface.get();
   }

   private void setMainInterface(Class referent) {
      this.mainInterface = new WeakReference(referent);
   }

   public List<Class> getInterfaces() {
      Set<Class> classes = this.interfaces.keySet();
      List<Class> list = new ArrayList();
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

      for(Class<?> clazz : classes) {
         if (clazz.isInterface() && this.getBeanContext().getInterfaceType(clazz) == InterfaceType.BUSINESS_REMOTE) {
            try {
               list.add(contextClassLoader.loadClass(clazz.getName()));
            } catch (NoClassDefFoundError | ClassNotFoundException var7) {
               list.add(clazz);
            }
         } else {
            list.add(clazz);
         }
      }

      return list;
   }

   private void setInterfaces(List<Class> interfaces) {
      this.interfaces = new WeakHashMap(interfaces.size());

      for(Class clazz : interfaces) {
         this.interfaces.put(clazz, (Object)null);
      }

   }

   protected void checkAuthorization(Method method) throws OpenEJBException {
   }

   public void setIntraVmCopyMode(boolean on) {
      this.setDoIntraVmCopy(on);
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      try {
         this.isValidReference(method);
      } catch (IllegalStateException ise) {
         if (method.getName().equals("writeReplace")) {
            BeanContext beanContext = (BeanContext)this.beanContextRef.get();
            if (beanContext != null) {
               return this._writeReplace(proxy);
            }
         }

         throw ise;
      }

      if (args == null) {
         args = new Object[0];
      }

      if (method.getDeclaringClass() == Object.class) {
         String methodName = method.getName();
         if (methodName.equals("toString")) {
            return this.toString();
         } else if (methodName.equals("equals")) {
            return this.equals(args[0]) ? Boolean.TRUE : Boolean.FALSE;
         } else if (methodName.equals("hashCode")) {
            return this.hashCode();
         } else {
            throw new UnsupportedOperationException("Unknown method: " + method);
         }
      } else if (method.getDeclaringClass() == IntraVmProxy.class) {
         String methodName = method.getName();
         if (methodName.equals("writeReplace")) {
            return this._writeReplace(proxy);
         } else {
            throw new UnsupportedOperationException("Unknown method: " + method);
         }
      } else if (method.getDeclaringClass() == BeanContext.Removable.class) {
         return this._invoke(proxy, BeanContext.Removable.class, method, args);
      } else {
         Class interfce = this.getInvokedInterface(method);
         ThreadContext callContext = ThreadContext.getThreadContext();
         Object localClientIdentity = ClientSecurity.getIdentity();

         Object var9;
         try {
            if (callContext == null && localClientIdentity != null) {
               SecurityService securityService = (SecurityService)SystemInstance.get().getComponent(SecurityService.class);
               securityService.associate(localClientIdentity);
            }

            if (this.strategy != IntraVmCopyMonitor.State.CLASSLOADER_COPY && this.getBeanContext().getInterfaceType(interfce) != InterfaceType.BUSINESS_REMOTE) {
               if (this.strategy == IntraVmCopyMonitor.State.COPY && args != null && args.length > 0) {
                  IntraVmCopyMonitor.pre(this.strategy);

                  try {
                     args = this.copyArgs(args);
                  } finally {
                     IntraVmCopyMonitor.post();
                  }
               }
            } else {
               IntraVmCopyMonitor.pre(this.strategy);
               ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
               Thread.currentThread().setContextClassLoader(this.getBeanContext().getClassLoader());

               try {
                  args = this.copyArgs(args);
                  method = this.copyMethod(method);
                  interfce = (Class)this.copyObj(interfce);
               } finally {
                  Thread.currentThread().setContextClassLoader(oldClassLoader);
                  IntraVmCopyMonitor.post();
               }
            }

            IntraVmCopyMonitor.State oldStrategy = this.strategy;
            if (this.getBeanContext().isAsynchronous(method) || this.getBeanContext().getComponentType().equals(BeanType.MANAGED)) {
               this.strategy = IntraVmCopyMonitor.State.NONE;
            }

            try {
               Object returnValue = this._invoke(proxy, interfce, method, args);
               var9 = this.copy(this.strategy, returnValue);
            } catch (Throwable throwable) {
               Throwable returnValue = (Throwable)this.copy(this.strategy, throwable);
               throw this.convertException(returnValue, method, interfce);
            } finally {
               this.strategy = oldStrategy;
            }
         } finally {
            if (callContext == null && localClientIdentity != null) {
               SecurityService securityService = (SecurityService)SystemInstance.get().getComponent(SecurityService.class);
               securityService.disassociate();
            }

         }

         return var9;
      }
   }

   private <T> T copy(IntraVmCopyMonitor.State strategy, T object) throws IOException, ClassNotFoundException {
      if (object != null && strategy.isCopy()) {
         IntraVmCopyMonitor.pre(strategy);

         Object var3;
         try {
            var3 = this.copyObj(object);
         } finally {
            IntraVmCopyMonitor.post();
         }

         return (T)var3;
      } else {
         return object;
      }
   }

   public boolean isValid() {
      return !this.isInvalidReference;
   }

   private void isValidReference(Method method) throws NoSuchObjectException {
      if (this.isInvalidReference) {
         if (this.interfaceType.isComponent() && this.interfaceType.isLocal()) {
            throw new NoSuchObjectLocalException("reference is invalid");
         } else if (!this.interfaceType.isComponent() && !Remote.class.isAssignableFrom(method.getDeclaringClass())) {
            throw new NoSuchEJBException("reference is invalid for " + this.deploymentID);
         } else {
            throw new NoSuchObjectException("reference is invalid");
         }
      } else {
         if (!Object.class.equals(method.getDeclaringClass()) || !method.getName().equals("finalize") || method.getExceptionTypes().length != 1 || !Throwable.class.equals(method.getExceptionTypes()[0])) {
            this.getBeanContext();
         }

      }
   }

   protected Throwable convertException(Throwable e, Method method, Class interfce) {
      boolean rmiRemote = Remote.class.isAssignableFrom(interfce);
      if (e instanceof TransactionRequiredException) {
         if (!rmiRemote && this.interfaceType.isBusiness()) {
            return (new EJBTransactionRequiredException(e.getMessage())).initCause(this.getCause(e));
         } else {
            return this.interfaceType.isLocal() ? (new TransactionRequiredLocalException(e.getMessage())).initCause(this.getCause(e)) : e;
         }
      } else if (e instanceof TransactionRolledbackException) {
         if (!rmiRemote && this.interfaceType.isBusiness()) {
            return (new EJBTransactionRolledbackException(e.getMessage())).initCause(this.getCause(e));
         } else {
            return this.interfaceType.isLocal() ? (new TransactionRolledbackLocalException(e.getMessage())).initCause(this.getCause(e)) : e;
         }
      } else if (e instanceof NoSuchObjectException) {
         if (!rmiRemote && this.interfaceType.isBusiness()) {
            return (new NoSuchEJBException(e.getMessage())).initCause(this.getCause(e));
         } else {
            return this.interfaceType.isLocal() ? (new NoSuchObjectLocalException(e.getMessage())).initCause(this.getCause(e)) : e;
         }
      } else if (e instanceof AccessException) {
         if (!rmiRemote && this.interfaceType.isBusiness()) {
            return (new AccessLocalException(e.getMessage())).initCause(this.getCause(e));
         } else {
            return this.interfaceType.isLocal() ? (new AccessLocalException(e.getMessage())).initCause(this.getCause(e)) : e;
         }
      } else if (e instanceof RemoteException) {
         if (!rmiRemote && this.interfaceType.isBusiness()) {
            return (new EJBException(e.getMessage())).initCause(this.getCause(e));
         } else {
            return this.interfaceType.isLocal() ? (new EJBException(e.getMessage())).initCause(this.getCause(e)) : e;
         }
      } else {
         for(Class<?> type : method.getExceptionTypes()) {
            if (type.isAssignableFrom(e.getClass())) {
               return e;
            }
         }

         while(e.getCause() != null && !(e instanceof RuntimeException)) {
            e = e.getCause();
         }

         return e;
      }
   }

   private Method copyMethod(Method method) throws Exception {
      int parameterCount = method.getParameterTypes().length;
      Class[] types = new Class[1 + parameterCount];
      types[0] = method.getDeclaringClass();
      System.arraycopy(method.getParameterTypes(), 0, types, 1, parameterCount);
      types = (Class[])this.copyArgs(types);
      Class targetClass = types[0];
      Class[] targetParameters = new Class[parameterCount];
      System.arraycopy(types, 1, targetParameters, 0, parameterCount);
      return targetClass.getMethod(method.getName(), targetParameters);
   }

   protected Throwable getCause(Throwable e) {
      return e != null && e.getCause() != null ? e.getCause() : e;
   }

   public String toString() {
      String name = null;

      try {
         name = this.getProxyInfo().getInterface().getName();
      } catch (Exception var3) {
      }

      return "proxy=" + name + ";deployment=" + this.deploymentID + ";pk=" + this.primaryKey;
   }

   public int hashCode() {
      return this.primaryKey == null ? this.deploymentID.hashCode() : this.primaryKey.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this == obj) {
         return true;
      } else {
         if (!BaseEjbProxyHandler.class.isInstance(obj)) {
            Class<?> aClass = obj.getClass();
            if (Proxy.isProxyClass(aClass)) {
               obj = Proxy.getInvocationHandler(obj);
            } else {
               if (!LocalBeanProxyFactory.isProxy(aClass)) {
                  return false;
               }

               obj = LocalBeanProxyFactory.getInvocationHandler(obj);
            }
         }

         return this.equalHandler((BaseEjbProxyHandler)BaseEjbProxyHandler.class.cast(obj));
      }
   }

   protected boolean equalHandler(BaseEjbProxyHandler other) {
      boolean var10000;
      label20: {
         if (this.primaryKey == null) {
            if (other.primaryKey != null) {
               break label20;
            }
         } else if (!this.primaryKey.equals(other.primaryKey)) {
            break label20;
         }

         if (this.deploymentID.equals(other.deploymentID) && this.getMainInterface().equals(other.getMainInterface())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   protected abstract Object _invoke(Object var1, Class var2, Method var3, Object[] var4) throws Throwable;

   protected Object[] copyArgs(Object[] objects) throws IOException, ClassNotFoundException {
      if (objects == null) {
         return objects;
      } else {
         for(int i = 0; i < objects.length; ++i) {
            objects[i] = this.copyObj(objects[i]);
         }

         return objects;
      }
   }

   protected <T> T copyObj(T object) throws IOException, ClassNotFoundException {
      if (object == null) {
         return null;
      } else {
         Class ooc = object.getClass();
         if (ooc != Integer.TYPE && ooc != String.class && ooc != Long.TYPE && ooc != Boolean.TYPE && ooc != Byte.TYPE && ooc != Float.TYPE && ooc != Double.TYPE && ooc != Short.TYPE && ooc != Long.class && ooc != Boolean.class && ooc != Byte.class && ooc != Character.class && ooc != Float.class && ooc != Double.class && ooc != Short.class && ooc != BigDecimal.class) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(128);

            try {
               ObjectOutputStream out = new ObjectOutputStream(baos);
               out.writeObject(object);
               out.close();
            } catch (NotSerializableException e) {
               throw (IOException)(new NotSerializableException(e.getMessage() + " : The EJB specification restricts remote interfaces to only serializable data types.  This can be disabled for in-vm use with the " + "openejb.localcopy" + "=false system property.")).initCause(e);
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream in = new EjbObjectInputStream(bais);
            Object obj = in.readObject();
            return (T)obj;
         } else {
            return object;
         }
      }
   }

   public void invalidateReference() {
      this.container = null;
      this.setBeanContext((BeanContext)null);
      this.isInvalidReference = true;
   }

   protected void invalidateAllHandlers(Object key) {
      HashSet<BaseEjbProxyHandler> set = (HashSet)this.getLiveHandleRegistry().remove(key);
      if (set != null) {
         ReentrantLock l = this.lock;
         l.lock();

         try {
            for(BaseEjbProxyHandler handler : set) {
               handler.invalidateReference();
            }
         } finally {
            l.unlock();
         }

      }
   }

   protected abstract Object _writeReplace(Object var1) throws ObjectStreamException;

   protected void registerHandler(Object key, BaseEjbProxyHandler handler) {
      Set set = (Set)this.getLiveHandleRegistry().get(key);
      if (set == null) {
         set = new HashSet();
         Object existing = this.getLiveHandleRegistry().putIfAbsent(key, set);
         if (existing != null) {
            set = (Set)Set.class.cast(existing);
         }
      }

      ReentrantLock l = this.lock;
      l.lock();

      try {
         set.add(handler);
      } finally {
         l.unlock();
      }

   }

   public abstract ProxyInfo getProxyInfo();

   public BeanContext getBeanContext() {
      BeanContext beanContext = (BeanContext)this.beanContextRef.get();
      if (beanContext != null && !beanContext.isDestroyed()) {
         return beanContext;
      } else {
         this.invalidateReference();
         throw new IllegalStateException("Bean '" + this.deploymentID + "' has been undeployed.");
      }
   }

   public void setBeanContext(BeanContext beanContext) {
      this.beanContextRef = new WeakReference(beanContext);
   }

   public ConcurrentMap getLiveHandleRegistry() {
      BeanContext beanContext = this.getBeanContext();
      ThreadContext tc = ThreadContext.getThreadContext();
      if (tc != null && tc.getBeanContext() != beanContext && tc.getCurrentOperation() == Operation.BUSINESS) {
         ProxyRegistry registry = (ProxyRegistry)tc.get(ProxyRegistry.class);
         if (registry == null) {
            registry = new ProxyRegistry();
            tc.set(ProxyRegistry.class, registry);
         }

         return registry.liveHandleRegistry;
      } else {
         SystemInstance systemInstance = SystemInstance.get();
         TransactionManager txMgr = (TransactionManager)systemInstance.getComponent(TransactionManager.class);

         try {
            Transaction tx = txMgr.getTransaction();
            if (tx != null && tx.getStatus() == 0) {
               TransactionSynchronizationRegistry registry = (TransactionSynchronizationRegistry)systemInstance.getComponent(TransactionSynchronizationRegistry.class);
               String resourceKey = ProxyRegistry.class.getName();
               final ConcurrentMap map = (ConcurrentMap)ConcurrentMap.class.cast(registry.getResource(resourceKey));
               if (map == null) {
                  map = new ConcurrentHashMap();
                  registry.putResource(resourceKey, map);

                  try {
                     tx.registerSynchronization(new Synchronization() {
                        public void beforeCompletion() {
                        }

                        public void afterCompletion(int status) {
                           map.clear();
                        }
                     });
                  } catch (RollbackException var10) {
                  }
               }

               return map;
            }
         } catch (SystemException var11) {
         }

         ProxyRegistry proxyRegistry = (ProxyRegistry)beanContext.get(ProxyRegistry.class);
         if (proxyRegistry == null) {
            proxyRegistry = new ProxyRegistry();
            beanContext.set(ProxyRegistry.class, proxyRegistry);
         }

         return proxyRegistry.liveHandleRegistry;
      }
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.getInterfaces());
      out.writeObject(this.getMainInterface());
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      this.setBeanContext(containerSystem.getBeanContext(this.deploymentID));
      this.container = (RpcContainer)this.getBeanContext().getContainer();
      if (IntraVmCopyMonitor.isCrossClassLoaderOperation()) {
         this.setDoCrossClassLoaderCopy(true);
      }

      this.setInterfaces((List)in.readObject());
      this.setMainInterface((Class)in.readObject());
   }

   static {
      ThreadContext.addThreadContextListener(new ThreadContextListener() {
         public void contextEntered(ThreadContext oldContext, ThreadContext newContext) {
         }

         public void contextExited(ThreadContext exitedContext, ThreadContext reenteredContext) {
            if (exitedContext != null) {
               ProxyRegistry proxyRegistry = (ProxyRegistry)exitedContext.get(ProxyRegistry.class);
               if (proxyRegistry != null) {
                  proxyRegistry.liveHandleRegistry.clear();
               }
            }

         }
      });
   }

   private static class ProxyRegistry {
      protected final ConcurrentMap liveHandleRegistry;

      private ProxyRegistry() {
         this.liveHandleRegistry = new ConcurrentHashMap();
      }
   }
}
