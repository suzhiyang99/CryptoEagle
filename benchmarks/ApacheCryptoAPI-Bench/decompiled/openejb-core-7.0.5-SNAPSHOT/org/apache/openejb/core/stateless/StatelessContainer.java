package org.apache.openejb.core.stateless;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
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
import org.apache.openejb.SystemException;
import org.apache.openejb.api.resource.DestroyableResource;
import org.apache.openejb.cdi.CurrentCreationalContext;
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
import org.apache.openejb.monitoring.StatsInterceptor;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.DaemonThreadFactory;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.Pool;
import org.apache.xbean.finder.ClassFinder;

public class StatelessContainer implements RpcContainer, DestroyableResource {
   private final ConcurrentMap<Class<?>, List<Method>> interceptorCache = new ConcurrentHashMap();
   private final StatelessInstanceManager instanceManager;
   private final Map<String, BeanContext> deploymentRegistry = new ConcurrentHashMap();
   private final Object containerID;
   private final SecurityService securityService;

   public StatelessContainer(Object id, SecurityService securityService, Duration accessTimeout, Duration closeTimeout, Pool.Builder poolBuilder, int callbackThreads, boolean useOneSchedulerThreadByBean, int evictionThreads) {
      this.containerID = id;
      this.securityService = securityService;
      this.instanceManager = new StatelessInstanceManager(securityService, accessTimeout, closeTimeout, poolBuilder, callbackThreads, useOneSchedulerThreadByBean ? null : Executors.newScheduledThreadPool(Math.max(evictionThreads, 1), new DaemonThreadFactory(new Object[]{id})));
   }

   public BeanContext[] getBeanContexts() {
      return (BeanContext[])this.deploymentRegistry.values().toArray(new BeanContext[this.deploymentRegistry.size()]);
   }

   public BeanContext getBeanContext(Object deploymentID) {
      String id = (String)deploymentID;
      return (BeanContext)this.deploymentRegistry.get(id);
   }

   public ContainerType getContainerType() {
      return ContainerType.STATELESS;
   }

   public Object getContainerID() {
      return this.containerID;
   }

   public void deploy(BeanContext beanContext) throws OpenEJBException {
      String id = (String)beanContext.getDeploymentID();
      this.deploymentRegistry.put(id, beanContext);
      beanContext.setContainer(this);
      if (StatsInterceptor.isStatsActivated()) {
         StatsInterceptor stats = new StatsInterceptor(beanContext.getBeanClass());
         beanContext.addFirstSystemInterceptor(stats);
      }

   }

   public void start(BeanContext beanContext) throws OpenEJBException {
      this.instanceManager.deploy(beanContext);
      EjbTimerService timerService = beanContext.getEjbTimerService();
      if (timerService != null) {
         timerService.start();
      }

   }

   public void stop(BeanContext beanContext) throws OpenEJBException {
      beanContext.stop();
   }

   public void undeploy(BeanContext beanContext) {
      this.instanceManager.undeploy(beanContext);
      String id = (String)beanContext.getDeploymentID();
      beanContext.setContainer((Container)null);
      beanContext.setContainerData((Object)null);
      this.deploymentRegistry.remove(id);
   }

   public Object invoke(Object deployID, InterfaceType type, Class callInterface, Method callMethod, Object[] args, Object primKey) throws OpenEJBException {
      BeanContext beanContext = this.getBeanContext(deployID);
      if (beanContext == null) {
         String msg = "Deployment does not exist in this container. Deployment(id='" + deployID + "'), Container(id='" + this.containerID + "')";
         throw new OpenEJBException(msg);
      } else {
         if (type == null) {
            type = beanContext.getInterfaceType(callInterface);
         }

         Method runMethod = beanContext.getMatchingBeanMethod(callMethod);
         ThreadContext callContext = new ThreadContext(beanContext, primKey);
         ThreadContext oldCallContext = ThreadContext.enter(callContext);
         Instance bean = null;
         CurrentCreationalContext currentCreationalContext = (CurrentCreationalContext)beanContext.get(CurrentCreationalContext.class);
         Object runAs = null;

         ProxyInfo var16;
         try {
            if (oldCallContext != null) {
               BeanContext oldBc = oldCallContext.getBeanContext();
               if (oldBc.getRunAsUser() != null || oldBc.getRunAs() != null) {
                  runAs = ((AbstractSecurityService)AbstractSecurityService.class.cast(this.securityService)).overrideWithRunAsContext(callContext, beanContext, oldBc);
               }
            }

            boolean authorized = type == InterfaceType.TIMEOUT || this.securityService.isCallerAuthorized(callMethod, type);
            if (!authorized) {
               throw new ApplicationException(new EJBAccessException("Unauthorized Access by Principal Denied"));
            }

            Class declaringClass = callMethod.getDeclaringClass();
            if (!EJBHome.class.isAssignableFrom(declaringClass) && !EJBLocalHome.class.isAssignableFrom(declaringClass)) {
               if (EJBObject.class != declaringClass && EJBLocalObject.class != declaringClass) {
                  bean = this.instanceManager.getInstance(callContext);
                  callContext.setCurrentOperation(type == InterfaceType.TIMEOUT ? Operation.TIMEOUT : Operation.BUSINESS);
                  callContext.set(Method.class, runMethod);
                  callContext.setInvokedInterface(callInterface);
                  if (currentCreationalContext != null) {
                     currentCreationalContext.set(bean.creationalContext);
                  }

                  var16 = (ProxyInfo)this._invoke(callMethod, runMethod, args, bean, callContext, type);
                  return var16;
               }

               var16 = null;
               return var16;
            }

            if (!callMethod.getName().startsWith("create")) {
               var16 = null;
               return var16;
            }

            var16 = new ProxyInfo(beanContext, (Object)null);
         } finally {
            if (runAs != null) {
               try {
                  this.securityService.associate(runAs);
               } catch (LoginException var26) {
               }
            }

            if (bean != null) {
               if (callContext.isDiscardInstance()) {
                  this.instanceManager.discardInstance(callContext, bean);
               } else {
                  this.instanceManager.poolInstance(callContext, bean);
               }
            }

            ThreadContext.exit(oldCallContext);
            if (currentCreationalContext != null) {
               currentCreationalContext.remove();
            }

         }

         return var16;
      }
   }

   private Object _invoke(Method callMethod, Method runMethod, Object[] args, Instance instance, ThreadContext callContext, InterfaceType type) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(beanContext.getTransactionType(callMethod, type), callContext);
      Object returnValue = null;

      try {
         if (type == InterfaceType.SERVICE_ENDPOINT) {
            callContext.setCurrentOperation(Operation.BUSINESS_WS);
            returnValue = this.invokeWebService(args, beanContext, runMethod, instance);
         } else {
            List<InterceptorData> interceptors = beanContext.getMethodInterceptors(runMethod);
            Operation operation = type == InterfaceType.TIMEOUT ? Operation.TIMEOUT : Operation.BUSINESS;
            InterceptorStack interceptorStack = new InterceptorStack(instance.bean, runMethod, operation, interceptors, instance.interceptors);
            returnValue = interceptorStack.invoke(args);
         }
      } catch (Throwable re) {
         ExceptionType exceptionType = beanContext.getExceptionType(re);
         if (exceptionType == ExceptionType.SYSTEM) {
            callContext.setDiscardInstance(true);
            EjbTransactionUtil.handleSystemException(txPolicy, re, callContext);
         } else {
            EjbTransactionUtil.handleApplicationException(txPolicy, re, exceptionType == ExceptionType.APPLICATION_ROLLBACK);
         }
      } finally {
         try {
            EjbTransactionUtil.afterInvoke(txPolicy, callContext);
         } catch (RuntimeException | SystemException e) {
            callContext.setDiscardInstance(true);
            throw e;
         }
      }

      return returnValue;
   }

   private Object invokeWebService(Object[] args, BeanContext beanContext, Method runMethod, Instance instance) throws Exception {
      if (args.length < 2) {
         throw new IllegalArgumentException("WebService calls must follow format {messageContext, interceptor, [arg...]}.");
      } else {
         Object messageContext = args[0];
         Object interceptor = args[1];
         Class<?> interceptorClass = interceptor.getClass();
         Map<String, Object> interceptors = new HashMap(instance.interceptors);
         interceptors.put(interceptor.getClass().getName(), interceptor);
         List<InterceptorData> interceptorDatas = new ArrayList();
         InterceptorData providerData = new InterceptorData(interceptorClass);
         providerData.getAroundInvoke().addAll(this.retrieveAroundInvokes(interceptorClass));
         interceptorDatas.add(0, providerData);
         interceptorDatas.addAll(beanContext.getMethodInterceptors(runMethod));
         InterceptorStack interceptorStack = new InterceptorStack(instance.bean, runMethod, Operation.BUSINESS_WS, interceptorDatas, interceptors);
         Object[] params = new Object[runMethod.getParameterTypes().length];
         ThreadContext threadContext = ThreadContext.getThreadContext();
         Object returnValue = null;
         if (messageContext instanceof MessageContext) {
            threadContext.set(MessageContext.class, (MessageContext)messageContext);
            returnValue = interceptorStack.invoke((MessageContext)messageContext, params);
         } else if (messageContext instanceof javax.xml.ws.handler.MessageContext) {
            AddressingSupport wsaSupport = NoAddressingSupport.INSTANCE;

            for(int i = 2; i < args.length; ++i) {
               if (args[i] instanceof AddressingSupport) {
                  wsaSupport = (AddressingSupport)args[i];
               }
            }

            threadContext.set(AddressingSupport.class, wsaSupport);
            threadContext.set(javax.xml.ws.handler.MessageContext.class, (javax.xml.ws.handler.MessageContext)messageContext);
            returnValue = interceptorStack.invoke((javax.xml.ws.handler.MessageContext)messageContext, params);
         }

         return returnValue;
      }
   }

   private List<Method> retrieveAroundInvokes(Class<?> interceptorClass) {
      List<Method> cached = (List)this.interceptorCache.get(interceptorClass);
      if (cached != null) {
         return cached;
      } else {
         ClassFinder finder = new ClassFinder(new Class[]{interceptorClass});
         List<Method> annotated = finder.findAnnotatedMethods(AroundInvoke.class);
         if (StatelessContainer.class.getClassLoader() == interceptorClass.getClassLoader()) {
            List<Method> value = new CopyOnWriteArrayList(annotated);
            annotated = (List)this.interceptorCache.putIfAbsent(interceptorClass, annotated);
            if (annotated == null) {
               annotated = value;
            }
         }

         return annotated;
      }
   }

   public void destroyResource() {
      this.instanceManager.destroy();
   }
}
