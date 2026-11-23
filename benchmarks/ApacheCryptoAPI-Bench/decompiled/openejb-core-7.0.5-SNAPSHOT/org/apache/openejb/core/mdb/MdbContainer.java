package org.apache.openejb.core.mdb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.UnavailableException;
import javax.transaction.xa.XAResource;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.ContainerType;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.RpcContainer;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.ExceptionType;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.core.interceptor.InterceptorStack;
import org.apache.openejb.core.timer.EjbTimerService;
import org.apache.openejb.core.transaction.EjbTransactionUtil;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.monitoring.ManagedMBean;
import org.apache.openejb.monitoring.ObjectNameBuilder;
import org.apache.openejb.monitoring.StatsInterceptor;
import org.apache.openejb.resource.XAResourceWrapper;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

public class MdbContainer implements RpcContainer {
   private static final Logger logger;
   private static final ThreadLocal<BeanContext> CURRENT;
   private static final Object[] NO_ARGS;
   private final Map<BeanContext, ObjectName> mbeanNames = new ConcurrentHashMap();
   private final Map<BeanContext, MdbActivationContext> activationContexts = new ConcurrentHashMap();
   private final Object containerID;
   private final SecurityService securityService;
   private final ResourceAdapter resourceAdapter;
   private final Class messageListenerInterface;
   private final Class activationSpecClass;
   private final int instanceLimit;
   private final boolean failOnUnknownActivationSpec;
   private final ConcurrentMap<Object, BeanContext> deployments = new ConcurrentHashMap();
   private final XAResourceWrapper xaResourceWrapper;
   private final InboundRecovery inboundRecovery;
   private final Properties properties = new Properties();

   public MdbContainer(Object containerID, SecurityService securityService, ResourceAdapter resourceAdapter, Class messageListenerInterface, Class activationSpecClass, int instanceLimit, boolean failOnUnknownActivationSpec) {
      this.containerID = containerID;
      this.securityService = securityService;
      this.resourceAdapter = resourceAdapter;
      this.messageListenerInterface = messageListenerInterface;
      this.activationSpecClass = activationSpecClass;
      this.instanceLimit = instanceLimit;
      this.failOnUnknownActivationSpec = failOnUnknownActivationSpec;
      this.xaResourceWrapper = (XAResourceWrapper)SystemInstance.get().getComponent(XAResourceWrapper.class);
      this.inboundRecovery = (InboundRecovery)SystemInstance.get().getComponent(InboundRecovery.class);
   }

   public BeanContext[] getBeanContexts() {
      return (BeanContext[])this.deployments.values().toArray(new BeanContext[this.deployments.size()]);
   }

   public BeanContext getBeanContext(Object deploymentID) {
      return (BeanContext)this.deployments.get(deploymentID);
   }

   public ContainerType getContainerType() {
      return ContainerType.MESSAGE_DRIVEN;
   }

   public Object getContainerID() {
      return this.containerID;
   }

   public ResourceAdapter getResourceAdapter() {
      return this.resourceAdapter;
   }

   public Class getMessageListenerInterface() {
      return this.messageListenerInterface;
   }

   public Class getActivationSpecClass() {
      return this.activationSpecClass;
   }

   public Properties getProperties() {
      return this.properties;
   }

   public void deploy(BeanContext beanContext) throws OpenEJBException {
      Object deploymentId = beanContext.getDeploymentID();
      if (!beanContext.getMdbInterface().equals(this.messageListenerInterface)) {
         throw new OpenEJBException("Deployment '" + deploymentId + "' has message listener interface " + beanContext.getMdbInterface().getName() + " but this MDB container only supports " + this.messageListenerInterface);
      } else {
         ActivationSpec activationSpec = this.createActivationSpec(beanContext);
         if (this.inboundRecovery != null) {
            this.inboundRecovery.recover(this.resourceAdapter, activationSpec, this.containerID.toString());
         }

         Options options = new Options(beanContext.getProperties());
         int instanceLimit = options.get("InstanceLimit", this.instanceLimit);
         MdbInstanceFactory instanceFactory = new MdbInstanceFactory(beanContext, this.securityService, instanceLimit);
         EndpointFactory endpointFactory = new EndpointFactory(activationSpec, this, beanContext, instanceFactory, this.xaResourceWrapper);
         beanContext.setContainer(this);
         beanContext.setContainerData(endpointFactory);
         this.deployments.put(deploymentId, beanContext);
         if (StatsInterceptor.isStatsActivated()) {
            StatsInterceptor stats = new StatsInterceptor(beanContext.getBeanClass());
            beanContext.addFirstSystemInterceptor(stats);
            MBeanServer server = LocalMBeanServer.get();
            ObjectNameBuilder jmxName = new ObjectNameBuilder("openejb.management");
            jmxName.set("J2EEServer", "openejb");
            jmxName.set("J2EEApplication", (String)null);
            jmxName.set("EJBModule", beanContext.getModuleID());
            jmxName.set("StatelessSessionBean", beanContext.getEjbName());
            jmxName.set("j2eeType", "");
            jmxName.set("name", beanContext.getEjbName());

            try {
               ObjectName objectName = jmxName.set("j2eeType", "Invocations").build();
               if (server.isRegistered(objectName)) {
                  server.unregisterMBean(objectName);
               }

               server.registerMBean(new ManagedMBean(stats), objectName);
               endpointFactory.jmxNames.add(objectName);
            } catch (Exception e) {
               logger.error("Unable to register MBean ", (Throwable)e);
            }
         }

         CURRENT.set(beanContext);

         try {
            MdbActivationContext activationContext = new MdbActivationContext(Thread.currentThread().getContextClassLoader(), beanContext, this.resourceAdapter, endpointFactory, activationSpec);
            this.activationContexts.put(beanContext, activationContext);
            boolean activeOnStartup = true;
            String activeOnStartupSetting = (String)beanContext.getActivationProperties().get("MdbActiveOnStartup");
            if (activeOnStartupSetting == null) {
               activeOnStartupSetting = (String)beanContext.getActivationProperties().get("DeliveryActive");
            }

            if (activeOnStartupSetting != null) {
               activeOnStartup = Boolean.parseBoolean(activeOnStartupSetting);
            }

            if (activeOnStartup) {
               activationContext.start();
            } else {
               logger.info("Not auto-activating endpoint for " + beanContext.getDeploymentID());
            }

            String jmxName = (String)beanContext.getActivationProperties().get("MdbJMXControl");
            if (jmxName == null) {
               jmxName = "true";
            }

            this.addJMxControl(beanContext, jmxName, activationContext);
         } catch (ResourceException e) {
            beanContext.setContainer((Container)null);
            beanContext.setContainerData((Object)null);
            this.deployments.remove(deploymentId);
            throw new OpenEJBException(e);
         } finally {
            CURRENT.remove();
         }

      }
   }

   private ActivationSpec createActivationSpec(BeanContext beanContext) throws OpenEJBException {
      try {
         ObjectRecipe objectRecipe = new ObjectRecipe(this.activationSpecClass);
         objectRecipe.allow(Option.IGNORE_MISSING_PROPERTIES);
         objectRecipe.disallow(Option.FIELD_INJECTION);
         Map<String, String> activationProperties = beanContext.getActivationProperties();

         for(Map.Entry<String, String> entry : activationProperties.entrySet()) {
            objectRecipe.setMethodProperty((String)entry.getKey(), entry.getValue());
         }

         objectRecipe.setMethodProperty("beanClass", beanContext.getBeanClass());
         ActivationSpec activationSpec = (ActivationSpec)objectRecipe.create(this.activationSpecClass.getClassLoader());
         Set<String> unusedProperties = new TreeSet(objectRecipe.getUnsetProperties().keySet());
         unusedProperties.remove("destination");
         unusedProperties.remove("destinationType");
         unusedProperties.remove("destinationLookup");
         unusedProperties.remove("connectionFactoryLookup");
         unusedProperties.remove("beanClass");
         unusedProperties.remove("MdbActiveOnStartup");
         unusedProperties.remove("MdbJMXControl");
         unusedProperties.remove("DeliveryActive");
         if (!unusedProperties.isEmpty()) {
            String text = "No setter found for the activation spec properties: " + unusedProperties;
            if (this.failOnUnknownActivationSpec) {
               throw new IllegalArgumentException(text);
            }

            logger.warning(text);
         }

         try {
            activationSpec.validate();
         } catch (UnsupportedOperationException var9) {
            logger.info("ActivationSpec does not support validate. Implementation of validate is optional");
         }

         try {
            Validator validator = (Validator)beanContext.getJndiContext().lookup("comp/Validator");
            Set generalSet = validator.validate(activationSpec, new Class[0]);
            if (!generalSet.isEmpty()) {
               throw new ConstraintViolationException("Constraint violation for ActivationSpec " + this.activationSpecClass.getName(), generalSet);
            }
         } catch (NamingException var8) {
            logger.debug("No Validator bound to JNDI context");
         }

         activationSpec.setResourceAdapter(this.resourceAdapter);
         return activationSpec;
      } catch (Exception e) {
         throw new OpenEJBException("Unable to create activation spec", e);
      }
   }

   public void start(BeanContext info) throws OpenEJBException {
      EjbTimerService timerService = info.getEjbTimerService();
      if (timerService != null) {
         timerService.start();
      }

   }

   public void stop(BeanContext info) throws OpenEJBException {
      info.stop();
   }

   public void undeploy(BeanContext beanContext) throws OpenEJBException {
      if (beanContext instanceof BeanContext) {
         try {
            EndpointFactory endpointFactory = (EndpointFactory)beanContext.getContainerData();
            if (endpointFactory != null) {
               CURRENT.set(beanContext);

               try {
                  ObjectName jmxBeanToRemove = (ObjectName)this.mbeanNames.remove(beanContext);
                  if (jmxBeanToRemove != null) {
                     LocalMBeanServer.unregisterSilently(jmxBeanToRemove);
                     logger.info("Undeployed MDB control for " + beanContext.getDeploymentID());
                  }

                  MdbActivationContext activationContext = (MdbActivationContext)this.activationContexts.remove(beanContext);
                  if (activationContext != null && activationContext.isStarted()) {
                     this.resourceAdapter.endpointDeactivation(endpointFactory, endpointFactory.getActivationSpec());
                  }
               } finally {
                  CURRENT.remove();
               }

               MBeanServer server = LocalMBeanServer.get();

               for(ObjectName objectName : endpointFactory.jmxNames) {
                  try {
                     server.unregisterMBean(objectName);
                  } catch (Exception var14) {
                     logger.error("Unable to unregister MBean " + objectName);
                  }
               }
            }
         } finally {
            beanContext.setContainer((Container)null);
            beanContext.setContainerData((Object)null);
            this.deployments.remove(beanContext.getDeploymentID());
         }

      }
   }

   public Object invoke(Object deploymentId, InterfaceType type, Class callInterface, Method method, Object[] args, Object primKey) throws OpenEJBException {
      BeanContext beanContext = this.getBeanContext(deploymentId);
      EndpointFactory endpointFactory = (EndpointFactory)beanContext.getContainerData();
      MdbInstanceFactory instanceFactory = endpointFactory.getInstanceFactory();

      Instance instance;
      try {
         instance = (Instance)instanceFactory.createInstance(true);
      } catch (UnavailableException e) {
         throw new SystemException("Unable to create instance for invocation", e);
      }

      Object var12;
      try {
         this.beforeDelivery(beanContext, instance, method, (XAResource)null);
         Object value = this.invoke(instance, method, type, args);
         this.afterDelivery(instance);
         var12 = value;
      } finally {
         instanceFactory.freeInstance(instance, true);
      }

      return var12;
   }

   public void beforeDelivery(BeanContext deployInfo, Object instance, Method method, XAResource xaResource) throws SystemException {
      ThreadContext callContext = new ThreadContext(deployInfo, (Object)null);
      ThreadContext oldContext = ThreadContext.enter(callContext);
      MdbCallContext mdbCallContext = new MdbCallContext();
      callContext.set(MdbCallContext.class, mdbCallContext);
      mdbCallContext.deliveryMethod = method;
      mdbCallContext.oldCallContext = oldContext;

      try {
         mdbCallContext.txPolicy = EjbTransactionUtil.createTransactionPolicy(deployInfo.getTransactionType(method), callContext);
         if (xaResource != null && mdbCallContext.txPolicy.isNewTransaction()) {
            mdbCallContext.txPolicy.enlistResource(xaResource);
         }

      } catch (ApplicationException e) {
         ThreadContext.exit(oldContext);
         throw new SystemException("Should never get an Application exception", e);
      } catch (SystemException e) {
         ThreadContext.exit(oldContext);
         throw e;
      } catch (Exception e) {
         ThreadContext.exit(oldContext);
         throw new SystemException("Unable to enlist xa resource in the transaction", e);
      }
   }

   public Object invoke(Object instance, Method method, InterfaceType type, Object... args) throws SystemException, ApplicationException {
      if (args == null) {
         args = NO_ARGS;
      }

      ThreadContext callContext = ThreadContext.getThreadContext();
      BeanContext deployInfo = callContext.getBeanContext();
      MdbCallContext mdbCallContext = (MdbCallContext)callContext.get(MdbCallContext.class);
      if (mdbCallContext == null) {
         throw new IllegalStateException("beforeDelivery was not called");
      } else if (mdbCallContext.deliveryMethod.getName().equals(method.getName()) && Arrays.deepEquals(mdbCallContext.deliveryMethod.getParameterTypes(), method.getParameterTypes())) {
         Object returnValue = null;
         OpenEJBException openEjbException = null;
         Operation oldOperation = callContext.getCurrentOperation();
         callContext.setCurrentOperation(type == InterfaceType.TIMEOUT ? Operation.TIMEOUT : Operation.BUSINESS);

         Object var12;
         try {
            if (logger.isDebugEnabled()) {
               logger.info("invoking method " + method.getName() + " on " + deployInfo.getDeploymentID());
            }

            Method targetMethod = deployInfo.getMatchingBeanMethod(method);
            callContext.set(Method.class, targetMethod);
            returnValue = this._invoke(instance, targetMethod, args, deployInfo, type, mdbCallContext);
            var12 = returnValue;
         } catch (SystemException | ApplicationException e) {
            openEjbException = e;
            throw e;
         } finally {
            callContext.setCurrentOperation(oldOperation);
            if (logger.isDebugEnabled()) {
               if (openEjbException == null) {
                  logger.debug("finished invoking method " + method.getName() + ". Return value:" + returnValue);
               } else {
                  Throwable exception = (Throwable)(openEjbException.getRootCause() != null ? openEjbException.getRootCause() : openEjbException);
                  logger.debug("finished invoking method " + method.getName() + " with exception " + exception);
               }
            }

         }

         return var12;
      } else {
         throw new IllegalStateException("Delivery method specified in beforeDelivery is not the delivery method called");
      }
   }

   private Object _invoke(Object instance, Method runMethod, Object[] args, BeanContext beanContext, InterfaceType interfaceType, MdbCallContext mdbCallContext) throws SystemException, ApplicationException {
      try {
         List<InterceptorData> interceptors = beanContext.getMethodInterceptors(runMethod);
         InterceptorStack interceptorStack = new InterceptorStack(((Instance)instance).bean, runMethod, interfaceType == InterfaceType.TIMEOUT ? Operation.TIMEOUT : Operation.BUSINESS, interceptors, ((Instance)instance).interceptors);
         Object returnValue = interceptorStack.invoke(args);
         return returnValue;
      } catch (Throwable var10) {
         Throwable e = var10;
         if (var10 instanceof InvocationTargetException) {
            e = ((InvocationTargetException)var10).getTargetException();
         }

         ExceptionType type = beanContext.getExceptionType(e);
         if (type == ExceptionType.SYSTEM) {
            EjbTransactionUtil.handleSystemException(mdbCallContext.txPolicy, e, ThreadContext.getThreadContext());
         } else {
            EjbTransactionUtil.handleApplicationException(mdbCallContext.txPolicy, e, false);
         }

         throw new AssertionError("Should not get here");
      }
   }

   public void afterDelivery(Object instance) throws SystemException {
      ThreadContext callContext = ThreadContext.getThreadContext();
      MdbCallContext mdbCallContext = (MdbCallContext)callContext.get(MdbCallContext.class);

      try {
         EjbTransactionUtil.afterInvoke(mdbCallContext.txPolicy, callContext);
      } catch (ApplicationException e) {
         throw new SystemException("Should never get an Application exception", e);
      } finally {
         ThreadContext.exit(mdbCallContext.oldCallContext);
      }

   }

   public void release(BeanContext deployInfo, Object instance) {
      ThreadContext callContext = ThreadContext.getThreadContext();
      boolean contextExitRequired = false;
      if (callContext == null) {
         callContext = new ThreadContext(deployInfo, (Object)null);
         ThreadContext.enter(callContext);
         contextExitRequired = true;
      }

      try {
         MdbCallContext mdbCallContext = (MdbCallContext)callContext.get(MdbCallContext.class);
         if (mdbCallContext != null) {
            try {
               EjbTransactionUtil.afterInvoke(mdbCallContext.txPolicy, callContext);
            } catch (Exception e) {
               logger.error("error while releasing message endpoint", (Throwable)e);
            } finally {
               EndpointFactory endpointFactory = (EndpointFactory)deployInfo.getContainerData();
               endpointFactory.getInstanceFactory().freeInstance((Instance)instance, false);
            }
         }
      } finally {
         if (contextExitRequired) {
            ThreadContext.exit(callContext);
         }

      }

   }

   private void addJMxControl(BeanContext current, String name, MdbActivationContext activationContext) throws ResourceException {
      if (name != null && !"false".equalsIgnoreCase(name)) {
         ObjectName jmxName;
         try {
            jmxName = "true".equalsIgnoreCase(name) ? (new ObjectNameBuilder()).set("J2EEServer", "openejb").set("J2EEApplication", (String)null).set("EJBModule", current.getModuleID()).set("StatelessSessionBean", current.getEjbName()).set("j2eeType", "control").set("name", current.getEjbName()).build() : new ObjectName(name);
         } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
         }

         this.mbeanNames.put(current, jmxName);
         LocalMBeanServer.registerSilently(new MdbJmxControl(activationContext), jmxName);
         logger.info("Deployed MDB control for " + current.getDeploymentID() + " on " + jmxName);
      } else {
         logger.debug("Not adding JMX control for " + current.getDeploymentID());
      }
   }

   public static BeanContext current() {
      BeanContext beanContext = (BeanContext)CURRENT.get();
      if (beanContext == null) {
         CURRENT.remove();
      }

      return beanContext;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
      CURRENT = new ThreadLocal();
      NO_ARGS = new Object[0];
   }

   private static class MdbCallContext {
      private Method deliveryMethod;
      private TransactionPolicy txPolicy;
      private ThreadContext oldCallContext;

      private MdbCallContext() {
      }
   }

   private static class MdbActivationContext {
      private final ClassLoader classLoader;
      private final BeanContext beanContext;
      private final ResourceAdapter resourceAdapter;
      private final EndpointFactory endpointFactory;
      private final ActivationSpec activationSpec;
      private AtomicBoolean started = new AtomicBoolean(false);

      public MdbActivationContext(ClassLoader classLoader, BeanContext beanContext, ResourceAdapter resourceAdapter, EndpointFactory endpointFactory, ActivationSpec activationSpec) {
         this.classLoader = classLoader;
         this.beanContext = beanContext;
         this.resourceAdapter = resourceAdapter;
         this.endpointFactory = endpointFactory;
         this.activationSpec = activationSpec;
      }

      public ResourceAdapter getResourceAdapter() {
         return this.resourceAdapter;
      }

      public EndpointFactory getEndpointFactory() {
         return this.endpointFactory;
      }

      public ActivationSpec getActivationSpec() {
         return this.activationSpec;
      }

      public boolean isStarted() {
         return this.started.get();
      }

      public void start() throws ResourceException {
         if (this.started.compareAndSet(false, true)) {
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();

            try {
               Thread.currentThread().setContextClassLoader(this.classLoader);
               this.resourceAdapter.endpointActivation(this.endpointFactory, this.activationSpec);
               MdbContainer.logger.info("Activated endpoint for " + this.beanContext.getDeploymentID());
            } finally {
               Thread.currentThread().setContextClassLoader(oldCl);
            }

         }
      }

      public void stop() {
         if (this.started.compareAndSet(true, false)) {
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();

            try {
               Thread.currentThread().setContextClassLoader(this.classLoader);
               this.resourceAdapter.endpointDeactivation(this.endpointFactory, this.activationSpec);
               MdbContainer.logger.info("Deactivated endpoint for " + this.beanContext.getDeploymentID());
            } finally {
               Thread.currentThread().setContextClassLoader(oldCl);
            }

         }
      }
   }

   public static final class MdbJmxControl implements DynamicMBean {
      private static final AttributeList ATTRIBUTE_LIST = new AttributeList();
      private static final MBeanInfo INFO = new MBeanInfo("org.apache.openejb.resource.activemq.ActiveMQResourceAdapter.MdbJmxControl", "Allows to control a MDB (start/stop)", new MBeanAttributeInfo[]{new MBeanAttributeInfo("started", "boolean", "started: boolean indicating whether this MDB endpoint has been activated.", true, false, true)}, new MBeanConstructorInfo[0], new MBeanOperationInfo[]{new MBeanOperationInfo("start", "Ensure the listener is active.", new MBeanParameterInfo[0], "void", 1), new MBeanOperationInfo("stop", "Ensure the listener is not active.", new MBeanParameterInfo[0], "void", 1)}, new MBeanNotificationInfo[0]);
      private final MdbActivationContext activationContext;

      private MdbJmxControl(MdbActivationContext activationContext) {
         this.activationContext = activationContext;
      }

      public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
         if (actionName.equals("stop")) {
            this.activationContext.stop();
         } else {
            if (!actionName.equals("start")) {
               throw new MBeanException(new IllegalStateException("unsupported operation: " + actionName));
            }

            try {
               this.activationContext.start();
            } catch (ResourceException e) {
               MdbContainer.logger.error("Error invoking " + actionName + ": " + e.getMessage());
               throw new MBeanException(new IllegalStateException(e.getMessage(), e));
            }
         }

         return null;
      }

      public MBeanInfo getMBeanInfo() {
         return INFO;
      }

      public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
         if ("started".equals(attribute)) {
            return this.activationContext.isStarted();
         } else {
            throw new AttributeNotFoundException();
         }
      }

      public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
         throw new AttributeNotFoundException();
      }

      public AttributeList getAttributes(String[] attributes) {
         return ATTRIBUTE_LIST;
      }

      public AttributeList setAttributes(AttributeList attributes) {
         return ATTRIBUTE_LIST;
      }
   }
}
