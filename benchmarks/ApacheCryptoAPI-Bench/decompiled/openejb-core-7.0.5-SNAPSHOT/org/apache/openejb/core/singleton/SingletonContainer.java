package org.apache.openejb.core.singleton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import javax.ejb.ConcurrentAccessTimeoutException;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.LockType;
import javax.interceptor.AroundInvoke;
import javax.security.auth.login.LoginException;
import javax.xml.rpc.handler.MessageContext;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.ContainerType;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.RpcContainer;
import org.apache.openejb.cdi.CurrentCreationalContext;
import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.ExceptionType;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.core.interceptor.InterceptorStack;
import org.apache.openejb.core.security.AbstractSecurityService;
import org.apache.openejb.core.timer.EjbTimerService;
import org.apache.openejb.core.transaction.EjbTransactionUtil;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.webservices.AddressingSupport;
import org.apache.openejb.core.webservices.NoAddressingSupport;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.Duration;
import org.apache.xbean.finder.ClassFinder;

public class SingletonContainer implements RpcContainer {
   private final SingletonInstanceManager instanceManager;
   private final HashMap<String, BeanContext> deploymentRegistry = new HashMap();
   private final ConcurrentMap<Class<?>, List<Method>> interceptorCache = new ConcurrentHashMap();
   private final Object containerID;
   private final SecurityService securityService;
   private Duration accessTimeout;

   public SingletonContainer(Object id, SecurityService securityService) throws OpenEJBException {
      this.containerID = id;
      this.securityService = securityService;
      this.instanceManager = new SingletonInstanceManager(securityService);

      for(BeanContext beanContext : this.deploymentRegistry.values()) {
         beanContext.setContainer(this);
      }

   }

   public void setAccessTimeout(Duration duration) {
      this.accessTimeout = duration;
   }

   public synchronized BeanContext[] getBeanContexts() {
      return (BeanContext[])this.deploymentRegistry.values().toArray(new BeanContext[this.deploymentRegistry.size()]);
   }

   public synchronized BeanContext getBeanContext(Object deploymentID) {
      String id = (String)deploymentID;
      return (BeanContext)this.deploymentRegistry.get(id);
   }

   public ContainerType getContainerType() {
      return ContainerType.SINGLETON;
   }

   public Object getContainerID() {
      return this.containerID;
   }

   public void deploy(BeanContext beanContext) throws OpenEJBException {
      this.instanceManager.deploy(beanContext);
      String id = (String)beanContext.getDeploymentID();
      synchronized(this) {
         this.deploymentRegistry.put(id, beanContext);
         beanContext.setContainer(this);
      }
   }

   public void start(BeanContext info) throws OpenEJBException {
      this.instanceManager.start(info);
      EjbTimerService timerService = info.getEjbTimerService();
      if (timerService != null) {
         timerService.start();
      }

   }

   public void stop(BeanContext info) throws OpenEJBException {
      info.stop();
   }

   public void undeploy(BeanContext beanContext) {
      ThreadContext threadContext = new ThreadContext(beanContext, (Object)null);
      ThreadContext old = ThreadContext.enter(threadContext);

      try {
         this.instanceManager.freeInstance(threadContext);
      } finally {
         ThreadContext.exit(old);
      }

      this.instanceManager.undeploy(beanContext);
      synchronized(this) {
         String id = (String)beanContext.getDeploymentID();
         beanContext.setContainer((Container)null);
         beanContext.setContainerData((Object)null);
         this.deploymentRegistry.remove(id);
      }
   }

   public Object invoke(Object deployID, InterfaceType type, Class callInterface, Method callMethod, Object[] args, Object primKey) throws OpenEJBException {
      BeanContext beanContext = this.getBeanContext(deployID);
      if (beanContext == null) {
         throw new OpenEJBException("Deployment does not exist in this container. Deployment(id='" + deployID + "'), Container(id='" + this.containerID + "')");
      } else {
         if (type == null) {
            type = beanContext.getInterfaceType(callInterface);
         }

         Method runMethod = beanContext.getMatchingBeanMethod(callMethod);
         ThreadContext callContext = new ThreadContext(beanContext, primKey);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);
         CurrentCreationalContext currentCreationalContext = (CurrentCreationalContext)beanContext.get(CurrentCreationalContext.class);
         Object runAs = null;

         ProxyInfo var15;
         try {
            if (oldCallContext != null) {
               BeanContext oldBc = oldCallContext.getBeanContext();
               if (oldBc.getRunAsUser() != null || oldBc.getRunAs() != null) {
                  runAs = ((AbstractSecurityService)AbstractSecurityService.class.cast(this.securityService)).overrideWithRunAsContext(callContext, beanContext, oldBc);
               }
            }

            boolean authorized = type == InterfaceType.TIMEOUT || this.getSecurityService().isCallerAuthorized(callMethod, type);
            if (!authorized) {
               throw new ApplicationException(new EJBAccessException("Unauthorized Access by Principal Denied"));
            }

            Class declaringClass = callMethod.getDeclaringClass();
            if (!EJBHome.class.isAssignableFrom(declaringClass) && !EJBLocalHome.class.isAssignableFrom(declaringClass)) {
               if (EJBObject.class != declaringClass && EJBLocalObject.class != declaringClass) {
                  Instance instance = this.instanceManager.getInstance(callContext);
                  callContext.setCurrentOperation(type == InterfaceType.TIMEOUT ? Operation.TIMEOUT : Operation.BUSINESS);
                  callContext.setCurrentAllowedStates((BaseContext.State[])null);
                  callContext.set(Method.class, runMethod);
                  callContext.setInvokedInterface(callInterface);
                  if (currentCreationalContext != null) {
                     currentCreationalContext.set(instance.creationalContext);
                  }

                  Object var16 = this._invoke(callMethod, runMethod, args, instance, callContext, type);
                  return var16;
               }

               var15 = null;
               return var15;
            }

            if (!callMethod.getName().startsWith("create")) {
               var15 = null;
               return var15;
            }

            var15 = this.createEJBObject(beanContext, callMethod);
         } finally {
            if (runAs != null) {
               try {
                  this.securityService.associate(runAs);
               } catch (LoginException var26) {
               }
            }

            ThreadContext.exit(oldCallContext);
            if (currentCreationalContext != null) {
               currentCreationalContext.remove();
            }

         }

         return var15;
      }
   }

   private SecurityService getSecurityService() {
      return this.securityService;
   }

   protected Object _invoke(Method callMethod, Method runMethod, Object[] args, Instance instance, ThreadContext callContext, InterfaceType callType) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      Duration accessTimeout = this.getAccessTimeout(beanContext, runMethod);
      boolean read = LockType.READ.equals(beanContext.getConcurrencyAttribute(runMethod));
      Lock lock = this.aquireLock(read, accessTimeout, instance, runMethod);

      Object returnValue;
      try {
         TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, callType), callContext);
         returnValue = null;

         try {
            if (callType == InterfaceType.SERVICE_ENDPOINT) {
               callContext.setCurrentOperation(Operation.BUSINESS_WS);
               returnValue = this.invokeWebService(args, beanContext, runMethod, instance);
            } else {
               List<InterceptorData> interceptors = beanContext.getMethodInterceptors(runMethod);
               InterceptorStack interceptorStack = new InterceptorStack(instance.bean, runMethod, callType == InterfaceType.TIMEOUT ? Operation.TIMEOUT : Operation.BUSINESS, interceptors, instance.interceptors);
               returnValue = interceptorStack.invoke(args);
            }
         } catch (Throwable e) {
            ExceptionType type = beanContext.getExceptionType(e);
            if (type == ExceptionType.SYSTEM) {
               EjbTransactionUtil.handleSystemException(txPolicy, e, callContext);
            } else {
               EjbTransactionUtil.handleApplicationException(txPolicy, e, type == ExceptionType.APPLICATION_ROLLBACK);
            }
         } finally {
            EjbTransactionUtil.afterInvoke(txPolicy, callContext);
         }
      } finally {
         lock.unlock();
      }

      return returnValue;
   }

   private Duration getAccessTimeout(BeanContext beanContext, Method callMethod) {
      Duration accessTimeout = beanContext.getAccessTimeout(callMethod);
      if (accessTimeout == null) {
         accessTimeout = beanContext.getAccessTimeout();
         if (accessTimeout == null) {
            accessTimeout = this.accessTimeout;
         }
      }

      return accessTimeout;
   }

   private Lock aquireLock(boolean read, Duration accessTimeout, Instance instance, Method runMethod) {
      Lock lock;
      if (read) {
         lock = instance.lock.readLock();
      } else {
         lock = instance.lock.writeLock();
      }

      boolean lockAcquired;
      if (accessTimeout != null && accessTimeout.getTime() >= 0L) {
         if (accessTimeout.getTime() == 0L) {
            lockAcquired = lock.tryLock();
         } else {
            try {
               lockAcquired = lock.tryLock(accessTimeout.getTime(), accessTimeout.getUnit());
            } catch (InterruptedException e) {
               throw (ConcurrentAccessTimeoutException)(new ConcurrentAccessTimeoutException("Unable to get " + (read ? "read" : "write") + " lock within specified time on '" + runMethod.getName() + "' method for: " + instance.bean.getClass().getName())).initCause(e);
            }
         }
      } else {
         lock.lock();
         lockAcquired = true;
      }

      if (!lockAcquired) {
         throw new ConcurrentAccessTimeoutException("Unable to get " + (read ? "read" : "write") + " lock on '" + runMethod.getName() + "' method for: " + instance.bean.getClass().getName());
      } else {
         return lock;
      }
   }

   private Object invokeWebService(Object[] args, BeanContext beanContext, Method runMethod, Instance instance) throws Exception {
      if (args.length < 2) {
         throw new IllegalArgumentException("WebService calls must follow format {messageContext, interceptor, [arg...]}.");
      } else {
         Object messageContext = args[0];
         if (messageContext == null) {
            throw new IllegalArgumentException("MessageContext is null.");
         } else {
            Object interceptor = args[1];
            if (interceptor == null) {
               throw new IllegalArgumentException("Interceptor instance is null.");
            } else {
               Class<?> interceptorClass = interceptor.getClass();
               Map<String, Object> interceptors = new HashMap(instance.interceptors);
               interceptors.put(interceptorClass.getName(), interceptor);
               List<InterceptorData> interceptorDatas = new ArrayList();
               InterceptorData providerData = new InterceptorData(interceptorClass);
               List<Method> aroundInvokes = (List)this.interceptorCache.get(interceptorClass);
               if (aroundInvokes == null) {
                  aroundInvokes = (new ClassFinder(new Class[]{interceptorClass})).findAnnotatedMethods(AroundInvoke.class);
                  if (SingletonContainer.class.getClassLoader() == interceptorClass.getClassLoader()) {
                     List<Method> value = new CopyOnWriteArrayList(aroundInvokes);
                     aroundInvokes = (List)this.interceptorCache.putIfAbsent(interceptorClass, value);
                     if (aroundInvokes == null) {
                        aroundInvokes = value;
                     }
                  }
               }

               providerData.getAroundInvoke().addAll(aroundInvokes);
               interceptorDatas.add(0, providerData);
               interceptorDatas.addAll(beanContext.getMethodInterceptors(runMethod));
               InterceptorStack interceptorStack = new InterceptorStack(instance.bean, runMethod, Operation.BUSINESS_WS, interceptorDatas, interceptors);
               aroundInvokes = new Object[runMethod.getParameterTypes().length];
               if (messageContext instanceof MessageContext) {
                  ThreadContext.getThreadContext().set(MessageContext.class, (MessageContext)messageContext);
                  return interceptorStack.invoke((MessageContext)((MessageContext)messageContext), aroundInvokes);
               } else if (messageContext instanceof javax.xml.ws.handler.MessageContext) {
                  AddressingSupport wsaSupport = NoAddressingSupport.INSTANCE;

                  for(int i = 2; i < args.length; ++i) {
                     if (args[i] instanceof AddressingSupport) {
                        wsaSupport = (AddressingSupport)args[i];
                     }
                  }

                  ThreadContext.getThreadContext().set(AddressingSupport.class, wsaSupport);
                  ThreadContext.getThreadContext().set(javax.xml.ws.handler.MessageContext.class, (javax.xml.ws.handler.MessageContext)messageContext);
                  return interceptorStack.invoke((javax.xml.ws.handler.MessageContext)((javax.xml.ws.handler.MessageContext)messageContext), aroundInvokes);
               } else {
                  throw new IllegalArgumentException("Uknown MessageContext type: " + messageContext.getClass().getName());
               }
            }
         }
      }
   }

   protected ProxyInfo createEJBObject(BeanContext beanContext, Method callMethod) {
      return new ProxyInfo(beanContext, (Object)null);
   }
}
