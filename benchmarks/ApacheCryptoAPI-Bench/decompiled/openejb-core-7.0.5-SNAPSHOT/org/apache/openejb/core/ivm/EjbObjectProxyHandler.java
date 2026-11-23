package org.apache.openejb.core.ivm;

import java.io.ObjectStreamException;
import java.lang.reflect.Method;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.ejb.AccessLocalException;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.InvalidateReferenceException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.ServerFederation;
import org.apache.openejb.spi.ApplicationServer;
import org.apache.openejb.threads.task.CUCallable;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public abstract class EjbObjectProxyHandler extends BaseEjbProxyHandler {
   private static final Logger logger;
   static final Map<String, Integer> dispatchTable;

   public EjbObjectProxyHandler(BeanContext beanContext, Object pk, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      super(beanContext, pk, interfaceType, interfaces, mainInterface);
   }

   public abstract Object getRegistryId();

   public Object _invoke(Object p, Class interfce, Method m, Object[] a) throws Throwable {
      Object retValue = null;
      Throwable exc = null;
      String methodName = m.getName();

      Object var9;
      try {
         if (logger.isDebugEnabled()) {
            logger.debug("EjbObjectProxyHandler: invoking method " + methodName + " on " + this.deploymentID + " with identity " + this.primaryKey);
         }

         Integer operation = (Integer)dispatchTable.get(methodName);
         if (operation != null) {
            if (operation == 3) {
               if (m.getParameterTypes()[0] != EJBObject.class && m.getParameterTypes()[0] != EJBLocalObject.class) {
                  operation = null;
               }
            } else {
               operation = m.getParameterTypes().length == 0 ? operation : null;
            }
         }

         if (operation != null && this.interfaceType.isComponent()) {
            switch (operation) {
               case 1:
                  retValue = this.getHandle(m, a, p);
                  break;
               case 2:
                  retValue = this.getPrimaryKey(m, a, p);
                  break;
               case 3:
                  retValue = this.isIdentical(m, a, p);
                  break;
               case 4:
                  retValue = this.remove(interfce, m, a, p);
                  break;
               case 5:
                  retValue = this.getEJBHome(m, a, p);
                  break;
               case 6:
                  retValue = this.getEJBLocalHome(m, a, p);
                  break;
               default:
                  throw new OpenEJBRuntimeException("Inconsistent internal state");
            }
         } else {
            retValue = this.businessMethod(interfce, m, a, p);
         }

         var9 = retValue;
      } catch (InvalidateReferenceException ire) {
         this.invalidateAllHandlers(this.getRegistryId());
         exc = (Throwable)(ire.getRootCause() != null ? ire.getRootCause() : new RemoteException("InvalidateReferenceException: " + ire));
         throw exc;
      } catch (ApplicationException ae) {
         exc = (Throwable)(ae.getRootCause() != null ? ae.getRootCause() : ae);
         if (exc instanceof EJBAccessException) {
            if (this.interfaceType.isBusiness()) {
               throw exc;
            }

            if (this.interfaceType.isLocal()) {
               throw (new AccessLocalException(exc.getMessage())).initCause(exc.getCause());
            }

            throw new AccessException(exc.getMessage());
         }

         throw exc;
      } catch (SystemException se) {
         this.invalidateReference();
         exc = (Throwable)(se.getRootCause() != null ? se.getRootCause() : se);
         logger.debug("The container received an unexpected exception: ", exc);
         throw new RemoteException("Container has suffered a SystemException", exc);
      } catch (OpenEJBException var26) {
         exc = (Throwable)(var26.getRootCause() != null ? var26.getRootCause() : var26);
         logger.debug("The container received an unexpected exception: ", exc);
         throw new RemoteException("Unknown Container Exception", var26.getRootCause());
      } finally {
         if (logger.isDebugEnabled()) {
            if (exc == null) {
               String ret = "void";
               if (null != retValue) {
                  try {
                     ret = retValue.toString();
                  } catch (Exception e) {
                     ret = "toString() failed on (" + e.getMessage() + ")";
                  }
               }

               logger.debug("EjbObjectProxyHandler: finished invoking method " + methodName + ". Return value:" + ret);
            } else {
               logger.debug("EjbObjectProxyHandler: finished invoking method " + methodName + " with exception " + exc);
            }
         }

      }

      return var9;
   }

   protected Object getEJBHome(Method method, Object[] args, Object proxy) throws Throwable {
      this.checkAuthorization(method);
      return this.getBeanContext().getEJBHome();
   }

   protected Object getEJBLocalHome(Method method, Object[] args, Object proxy) throws Throwable {
      this.checkAuthorization(method);
      return this.getBeanContext().getEJBLocalHome();
   }

   protected Object getHandle(Method method, Object[] args, Object proxy) throws Throwable {
      this.checkAuthorization(method);
      return new IntraVmHandle(proxy);
   }

   public ProxyInfo getProxyInfo() {
      return new ProxyInfo(this.getBeanContext(), this.primaryKey, this.getInterfaces(), this.interfaceType, this.getMainInterface());
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
         return this.interfaceType.isBusiness() ? applicationServer.getBusinessObject(this.getProxyInfo()) : applicationServer.getEJBObject(this.getProxyInfo());
      }
   }

   protected abstract Object getPrimaryKey(Method var1, Object[] var2, Object var3) throws Throwable;

   protected abstract Object isIdentical(Method var1, Object[] var2, Object var3) throws Throwable;

   protected abstract Object remove(Class var1, Method var2, Object[] var3, Object var4) throws Throwable;

   protected Object businessMethod(final Class<?> interfce, final Method method, final Object[] args, Object proxy) throws Throwable {
      BeanContext beanContext = this.getBeanContext();
      return beanContext.isAsynchronous(method) ? beanContext.getModuleContext().getAppContext().getAsynchronousPool().invoke(new CUCallable(new Callable<Object>() {
         public Object call() throws Exception {
            try {
               return EjbObjectProxyHandler.this.synchronizedBusinessMethod(interfce, method, args);
            } catch (ApplicationException ae) {
               EjbObjectProxyHandler.logger.error("EjbObjectProxyHandler: Asynchronous call to '" + interfce.getSimpleName() + "' on '" + method.getName() + "' failed", (Throwable)ae);
               throw ae;
            }
         }
      }), method.getReturnType() == Void.TYPE) : this.synchronizedBusinessMethod(interfce, method, args);
   }

   protected Object synchronizedBusinessMethod(Class<?> interfce, Method method, Object[] args) throws OpenEJBException {
      return this.container.invoke(this.deploymentID, this.interfaceType, interfce, method, args, this.primaryKey);
   }

   public static Object createProxy(BeanContext beanContext, Object primaryKey, InterfaceType interfaceType, Class mainInterface) {
      return createProxy(beanContext, primaryKey, interfaceType, (List)null, mainInterface);
   }

   public static Object createProxy(BeanContext beanContext, Object primaryKey, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      if (!interfaceType.isHome()) {
         interfaceType = interfaceType.getCounterpart();
      }

      EjbHomeProxyHandler homeHandler = EjbHomeProxyHandler.createHomeHandler(beanContext, interfaceType, interfaces, mainInterface);
      return homeHandler.createProxy(primaryKey, mainInterface);
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
      dispatchTable = new HashMap();
      dispatchTable.put("getHandle", 1);
      dispatchTable.put("getPrimaryKey", 2);
      dispatchTable.put("isIdentical", 3);
      dispatchTable.put("remove", 4);
      dispatchTable.put("getEJBHome", 5);
      dispatchTable.put("getEJBLocalHome", 6);
   }
}
