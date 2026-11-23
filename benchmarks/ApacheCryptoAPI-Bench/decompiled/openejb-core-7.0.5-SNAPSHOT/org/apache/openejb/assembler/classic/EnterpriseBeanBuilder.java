package org.apache.openejb.assembler.classic;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.Injection;
import org.apache.openejb.ModuleContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.cmp.CmpUtil;
import org.apache.openejb.dyni.DynamicSubclass;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.persistence.JtaEntityManager;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.Index;
import org.apache.openejb.util.Messages;

class EnterpriseBeanBuilder {
   private final EnterpriseBeanInfo bean;
   private final BeanType ejbType;
   private final List<Exception> warnings = new ArrayList();
   private final ModuleContext moduleContext;
   private final List<Injection> moduleInjections;

   public EnterpriseBeanBuilder(EnterpriseBeanInfo bean, ModuleContext moduleContext, List<Injection> moduleInjections) {
      this.moduleContext = moduleContext;
      this.bean = bean;
      this.moduleInjections = moduleInjections;
      if (bean.type == 1) {
         this.ejbType = BeanType.STATEFUL;
      } else if (bean.type == 2) {
         this.ejbType = BeanType.STATELESS;
      } else if (bean.type == 4) {
         this.ejbType = BeanType.SINGLETON;
      } else if (bean.type == 5) {
         this.ejbType = BeanType.MANAGED;
      } else if (bean.type == 3) {
         this.ejbType = BeanType.MESSAGE_DRIVEN;
      } else {
         if (bean.type != 0) {
            throw new UnsupportedOperationException("No building support for bean type: " + bean);
         }

         String persistenceType = ((EntityBeanInfo)bean).persistenceType;
         this.ejbType = persistenceType.equalsIgnoreCase("Container") ? BeanType.CMP_ENTITY : BeanType.BMP_ENTITY;
      }

   }

   public BeanContext build() throws OpenEJBException {
      Class ejbClass = this.loadClass(this.bean.ejbClass, "classNotFound.ejbClass");
      if (DynamicSubclass.isDynamic(ejbClass)) {
         ejbClass = DynamicSubclass.createSubclass(ejbClass, this.moduleContext.getClassLoader());
      }

      Class home = null;
      Class remote = null;
      if (this.bean.home != null) {
         home = this.loadClass(this.bean.home, "classNotFound.home");
         remote = this.loadClass(this.bean.remote, "classNotFound.remote");
      }

      Class<?> proxy = null;
      if (this.bean.proxy != null) {
         proxy = this.loadClass(this.bean.proxy, "classNotFound.proxy");
      }

      Class<?> localhome = null;
      Class<?> local = null;
      if (this.bean.localHome != null) {
         localhome = this.loadClass(this.bean.localHome, "classNotFound.localHome");
         local = this.loadClass(this.bean.local, "classNotFound.local");
      }

      List<Class> businessLocals = new ArrayList();

      for(String businessLocal : this.bean.businessLocal) {
         businessLocals.add(this.loadClass(businessLocal, "classNotFound.businessLocal"));
      }

      List<Class> businessRemotes = new ArrayList();

      for(String businessRemote : this.bean.businessRemote) {
         businessRemotes.add(this.loadClass(businessRemote, "classNotFound.businessRemote"));
      }

      Class serviceEndpoint = null;
      if ((BeanType.STATELESS == this.ejbType || BeanType.SINGLETON == this.ejbType) && this.bean.serviceEndpoint != null) {
         serviceEndpoint = this.loadClass(this.bean.serviceEndpoint, "classNotFound.sei");
      }

      Class primaryKey = null;
      if (this.ejbType.isEntity() && ((EntityBeanInfo)this.bean).primKeyClass != null) {
         String className = ((EntityBeanInfo)this.bean).primKeyClass;
         primaryKey = this.loadClass(className, "classNotFound.primaryKey");
      }

      String transactionType = this.bean.transactionType;
      InjectionBuilder injectionBuilder = new InjectionBuilder(this.moduleContext.getClassLoader());
      List<Injection> injections = injectionBuilder.buildInjections(this.bean.jndiEnc);
      Set<Class<?>> relevantClasses = new HashSet();
      Class c = ejbClass;

      do {
         relevantClasses.add(c);
         c = c.getSuperclass();
      } while(c != null && c != Object.class);

      for(Injection injection : this.moduleInjections) {
         if (relevantClasses.contains(injection.getTarget())) {
            injections.add(injection);
         }
      }

      JndiEncBuilder jndiEncBuilder = new JndiEncBuilder(this.bean.jndiEnc, injections, transactionType, this.moduleContext.getId(), (URI)null, this.moduleContext.getUniqueId(), this.moduleContext.getClassLoader(), this.moduleContext.getAppContext() == null ? this.moduleContext.getProperties() : this.moduleContext.getAppContext().getProperties());
      Context compJndiContext = jndiEncBuilder.build(JndiEncBuilder.JndiScope.comp);
      this.bind(compJndiContext, "module", this.moduleContext.getModuleJndiContext());
      this.bind(compJndiContext, "app", this.moduleContext.getAppContext().getAppJndiContext());
      this.bind(compJndiContext, "global", this.moduleContext.getAppContext().getGlobalJndiContext());
      BeanContext deployment;
      if (BeanType.MESSAGE_DRIVEN != this.ejbType) {
         deployment = new BeanContext(this.bean.ejbDeploymentId, compJndiContext, this.moduleContext, ejbClass, home, remote, localhome, local, proxy, serviceEndpoint, businessLocals, businessRemotes, primaryKey, this.ejbType, this.bean.localbean && this.ejbType.isSession(), this.bean.passivable);
         if (this.bean instanceof ManagedBeanInfo) {
            deployment.setHidden(((ManagedBeanInfo)this.bean).hidden);
         }
      } else {
         MessageDrivenBeanInfo messageDrivenBeanInfo = (MessageDrivenBeanInfo)this.bean;
         Class mdbInterface = this.loadClass(messageDrivenBeanInfo.mdbInterface, "classNotFound.mdbInterface");
         deployment = new BeanContext(this.bean.ejbDeploymentId, compJndiContext, this.moduleContext, ejbClass, mdbInterface, messageDrivenBeanInfo.activationProperties);
         deployment.setDestinationId(messageDrivenBeanInfo.destinationId);
      }

      deployment.getProperties().putAll(this.bean.properties);
      deployment.setEjbName(this.bean.ejbName);
      deployment.setRunAs(this.bean.runAs);
      deployment.setRunAsUser(this.bean.runAsUser);
      deployment.getInjections().addAll(injections);
      deployment.setEjbTimeout(this.getTimeout(ejbClass, this.bean.timeoutMethod));
      if (this.bean.statefulTimeout != null) {
         deployment.setStatefulTimeout(new Duration(this.bean.statefulTimeout.time, TimeUnit.valueOf(this.bean.statefulTimeout.unit)));
      }

      if (this.bean instanceof StatefulBeanInfo) {
         StatefulBeanInfo statefulBeanInfo = (StatefulBeanInfo)this.bean;

         for(InitMethodInfo init : statefulBeanInfo.initMethods) {
            Method beanMethod = MethodInfoUtil.toMethod(ejbClass, init.beanMethod);
            List<Method> methods = new ArrayList();
            if (home != null) {
               methods.addAll(Arrays.asList(home.getMethods()));
            }

            if (localhome != null) {
               methods.addAll(Arrays.asList(localhome.getMethods()));
            }

            for(Method homeMethod : methods) {
               if ((init.createMethod == null || init.createMethod.methodName.equals(homeMethod.getName())) && homeMethod.getName().startsWith("create") && paramsMatch(beanMethod, homeMethod)) {
                  deployment.mapMethods(homeMethod, beanMethod);
               }
            }
         }

         for(RemoveMethodInfo removeMethod : statefulBeanInfo.removeMethods) {
            if (removeMethod.beanMethod.methodParams == null) {
               MethodInfo methodInfo = new MethodInfo();
               methodInfo.methodName = removeMethod.beanMethod.methodName;
               methodInfo.methodParams = removeMethod.beanMethod.methodParams;
               methodInfo.className = removeMethod.beanMethod.className;

               for(Method method : MethodInfoUtil.matchingMethods(methodInfo, ejbClass)) {
                  deployment.getRemoveMethods().add(method);
                  deployment.setRetainIfExeption(method, removeMethod.retainIfException);
               }
            } else {
               Method method = MethodInfoUtil.toMethod(ejbClass, removeMethod.beanMethod);
               deployment.getRemoveMethods().add(method);
               deployment.setRetainIfExeption(method, removeMethod.retainIfException);
            }
         }

         Map<EntityManagerFactory, BeanContext.EntityManagerConfiguration> extendedEntityManagerFactories = new HashMap();

         for(PersistenceContextReferenceInfo info : statefulBeanInfo.jndiEnc.persistenceContextRefs) {
            if (info.extended) {
               try {
                  ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
                  Object o = containerSystem.getJNDIContext().lookup(PersistenceBuilder.getOpenEJBJndiName(info.unitId));
                  EntityManagerFactory emf = (EntityManagerFactory)EntityManagerFactory.class.cast(o);
                  extendedEntityManagerFactories.put(emf, new BeanContext.EntityManagerConfiguration(info.properties, JtaEntityManager.isJPA21(emf) && info.synchronizationType != null ? SynchronizationType.valueOf(info.synchronizationType) : null));
               } catch (NamingException var28) {
                  throw new OpenEJBException("PersistenceUnit '" + info.unitId + "' not found for EXTENDED ref '" + info.referenceName + "'");
               }
            }
         }

         deployment.setExtendedEntityManagerFactories(new Index(extendedEntityManagerFactories));
      }

      if (this.ejbType.isSession() || this.ejbType.isMessageDriven()) {
         deployment.setBeanManagedTransaction("Bean".equalsIgnoreCase(this.bean.transactionType));
      }

      if (this.ejbType.isSession()) {
         deployment.getDependsOn().addAll(this.bean.dependsOn);
      }

      if (this.ejbType == BeanType.SINGLETON) {
         deployment.setBeanManagedConcurrency("Bean".equalsIgnoreCase(this.bean.concurrencyType));
         deployment.setLoadOnStartup(this.bean.loadOnStartup);
      }

      if (this.ejbType.isEntity()) {
         EntityBeanInfo entity = (EntityBeanInfo)this.bean;
         deployment.setCmp2(entity.cmpVersion == 2);
         deployment.setIsReentrant(entity.reentrant.equalsIgnoreCase("true"));
         if (this.ejbType == BeanType.CMP_ENTITY) {
            Class cmpImplClass = null;
            String cmpImplClassName = CmpUtil.getCmpImplClassName(entity.abstractSchemaName, entity.ejbClass);
            cmpImplClass = this.loadClass(cmpImplClassName, "classNotFound.cmpImplClass");
            deployment.setCmpImplClass(cmpImplClass);
            deployment.setAbstractSchemaName(entity.abstractSchemaName);

            for(QueryInfo query : entity.queries) {
               if (query.remoteResultType) {
                  StringBuilder methodSignature = new StringBuilder();
                  methodSignature.append(query.method.methodName);
                  if (query.method.methodParams != null && !query.method.methodParams.isEmpty()) {
                     methodSignature.append('(');
                     boolean first = true;

                     for(String methodParam : query.method.methodParams) {
                        if (!first) {
                           methodSignature.append(",");
                        }

                        methodSignature.append(methodParam);
                        first = false;
                     }

                     methodSignature.append(')');
                  }

                  deployment.setRemoteQueryResults(methodSignature.toString());
               }
            }

            if (entity.primKeyField != null) {
               deployment.setPrimaryKeyField(entity.primKeyField);
            }
         }
      }

      deployment.createMethodMap();
      if (this.ejbType == BeanType.STATELESS || this.ejbType == BeanType.SINGLETON || this.ejbType == BeanType.STATEFUL) {
         for(NamedMethodInfo methodInfo : this.bean.asynchronous) {
            Method method = MethodInfoUtil.toMethod(ejbClass, methodInfo);
            deployment.getMethodContext(deployment.getMatchingBeanMethod(method)).setAsynchronous(true);
         }

         for(String className : this.bean.asynchronousClasses) {
            deployment.getAsynchronousClasses().add(this.loadClass(className, "classNotFound.ejbClass"));
         }

         deployment.createAsynchronousMethodSet();
      }

      for(SecurityRoleReferenceInfo securityRoleReference : this.bean.securityRoleReferences) {
         deployment.addSecurityRoleReference(securityRoleReference.roleName, securityRoleReference.roleLink);
      }

      return deployment;
   }

   private void bind(Context compJndiContext, String s, Context moduleJndiContext) throws OpenEJBException {
      Context c;
      try {
         c = (Context)moduleJndiContext.lookup(s);
      } catch (NamingException var7) {
         return;
      }

      try {
         compJndiContext.bind(s, c);
      } catch (NamingException e) {
         throw new OpenEJBException("Could not bind context at " + s, e);
      }
   }

   public static boolean paramsMatch(Method methodA, Method methodB) {
      if (methodA.getParameterTypes().length != methodB.getParameterTypes().length) {
         return false;
      } else {
         for(int i = 0; i < methodA.getParameterTypes().length; ++i) {
            Class<?> a = methodA.getParameterTypes()[i];
            Class<?> b = methodB.getParameterTypes()[i];
            if (!a.equals(b)) {
               return false;
            }
         }

         return true;
      }
   }

   public List<Exception> getWarnings() {
      return this.warnings;
   }

   private Method getTimeout(Class ejbClass, NamedMethodInfo info) {
      Method timeout = null;

      try {
         if (TimedObject.class.isAssignableFrom(ejbClass)) {
            timeout = ejbClass.getMethod("ejbTimeout", Timer.class);
         } else if (info != null) {
            try {
               timeout = MethodInfoUtil.toMethod(ejbClass, info);
            } catch (IllegalStateException var6) {
               if (info.methodParams == null) {
                  NamedMethodInfo candidateInfo = new NamedMethodInfo();
                  candidateInfo.className = info.className;
                  candidateInfo.id = info.id;
                  candidateInfo.methodName = info.methodName;
                  candidateInfo.methodParams = Arrays.asList(Timer.class.getName());
                  timeout = MethodInfoUtil.toMethod(ejbClass, candidateInfo);
               }
            }
         }
      } catch (Exception e) {
         this.warnings.add(e);
      }

      return timeout;
   }

   private Class loadClass(String className, String messageCode) throws OpenEJBException {
      Class clazz = this.load(className, messageCode);

      try {
         return clazz;
      } catch (NoClassDefFoundError e) {
         if (clazz.getClassLoader() != this.moduleContext.getClassLoader()) {
            String message = this.messages().format("cl0008", className, clazz.getClassLoader(), this.moduleContext.getClassLoader(), e.getMessage());
            throw new OpenEJBException(this.messages().format(messageCode, className, this.bean.ejbDeploymentId, message), e);
         } else {
            String message = this.messages().format("cl0009", className, clazz.getClassLoader(), e.getMessage());
            throw new OpenEJBException(this.messages().format(messageCode, className, this.bean.ejbDeploymentId, message), e);
         }
      }
   }

   private Messages messages() {
      return new Messages("org.apache.openejb.util.resources");
   }

   private Class load(String className, String messageCode) throws OpenEJBException {
      try {
         return Class.forName(className, true, this.moduleContext.getClassLoader());
      } catch (ClassNotFoundException var5) {
         String message = this.messages().format("cl0007", className, this.bean.codebase);
         throw new OpenEJBException(this.messages().format(messageCode, className, this.bean.ejbDeploymentId, message));
      }
   }
}
