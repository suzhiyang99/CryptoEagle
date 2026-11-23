package org.apache.openejb.config;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.assembler.classic.ApplicationExceptionInfo;
import org.apache.openejb.assembler.classic.BeansInfo;
import org.apache.openejb.assembler.classic.CallbackInfo;
import org.apache.openejb.assembler.classic.CmrFieldInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.EnterpriseBeanInfo;
import org.apache.openejb.assembler.classic.EntityBeanInfo;
import org.apache.openejb.assembler.classic.ExclusionInfo;
import org.apache.openejb.assembler.classic.InitMethodInfo;
import org.apache.openejb.assembler.classic.InterceptorBindingInfo;
import org.apache.openejb.assembler.classic.InterceptorInfo;
import org.apache.openejb.assembler.classic.JndiNameInfo;
import org.apache.openejb.assembler.classic.ManagedBeanInfo;
import org.apache.openejb.assembler.classic.MessageDrivenBeanInfo;
import org.apache.openejb.assembler.classic.MethodConcurrencyInfo;
import org.apache.openejb.assembler.classic.MethodInfo;
import org.apache.openejb.assembler.classic.MethodPermissionInfo;
import org.apache.openejb.assembler.classic.MethodScheduleInfo;
import org.apache.openejb.assembler.classic.MethodTransactionInfo;
import org.apache.openejb.assembler.classic.NamedMethodInfo;
import org.apache.openejb.assembler.classic.QueryInfo;
import org.apache.openejb.assembler.classic.RemoveMethodInfo;
import org.apache.openejb.assembler.classic.ScheduleInfo;
import org.apache.openejb.assembler.classic.SecurityRoleInfo;
import org.apache.openejb.assembler.classic.SecurityRoleReferenceInfo;
import org.apache.openejb.assembler.classic.SingletonBeanInfo;
import org.apache.openejb.assembler.classic.StatefulBeanInfo;
import org.apache.openejb.assembler.classic.StatelessBeanInfo;
import org.apache.openejb.assembler.classic.TimeoutInfo;
import org.apache.openejb.cdi.CompositeBeans;
import org.apache.openejb.jee.ActivationConfig;
import org.apache.openejb.jee.ActivationConfigProperty;
import org.apache.openejb.jee.ApplicationException;
import org.apache.openejb.jee.AsyncMethod;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.CallbackMethod;
import org.apache.openejb.jee.CmpField;
import org.apache.openejb.jee.CmpVersion;
import org.apache.openejb.jee.ConcurrencyManagementType;
import org.apache.openejb.jee.ConcurrentMethod;
import org.apache.openejb.jee.ContainerConcurrency;
import org.apache.openejb.jee.ContainerTransaction;
import org.apache.openejb.jee.EjbRelation;
import org.apache.openejb.jee.EjbRelationshipRole;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.ExcludeList;
import org.apache.openejb.jee.Icon;
import org.apache.openejb.jee.InitMethod;
import org.apache.openejb.jee.Interceptor;
import org.apache.openejb.jee.InterceptorBinding;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.ManagedBean;
import org.apache.openejb.jee.MessageDrivenBean;
import org.apache.openejb.jee.Method;
import org.apache.openejb.jee.MethodParams;
import org.apache.openejb.jee.MethodPermission;
import org.apache.openejb.jee.Multiplicity;
import org.apache.openejb.jee.NamedMethod;
import org.apache.openejb.jee.PersistenceType;
import org.apache.openejb.jee.Query;
import org.apache.openejb.jee.QueryMethod;
import org.apache.openejb.jee.RemoteBean;
import org.apache.openejb.jee.RemoveMethod;
import org.apache.openejb.jee.ResultTypeMapping;
import org.apache.openejb.jee.SecurityRole;
import org.apache.openejb.jee.SecurityRoleRef;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.SessionType;
import org.apache.openejb.jee.StatefulBean;
import org.apache.openejb.jee.Timeout;
import org.apache.openejb.jee.Timer;
import org.apache.openejb.jee.TimerSchedule;
import org.apache.openejb.jee.TransactionType;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.Jndi;
import org.apache.openejb.jee.oejb3.ResourceLink;
import org.apache.openejb.jee.oejb3.RoleMapping;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Messages;
import org.apache.webbeans.spi.BeanArchiveService.BeanDiscoveryMode;

public class EjbJarInfoBuilder {
   private static final URL DEFAULT_BEANS_XML_KEY;
   public static Messages messages;
   public static Logger logger;
   private final List<String> deploymentIds = new ArrayList();
   private final List<String> securityRoles = new ArrayList();

   public EjbJarInfo buildInfo(EjbModule jar) throws OpenEJBException {
      this.deploymentIds.clear();
      this.securityRoles.clear();
      Map<String, EjbDeployment> ejbds = jar.getOpenejbJar().getDeploymentsByEjbName();
      int beansDeployed = jar.getOpenejbJar().getEjbDeploymentCount();
      int beansInEjbJar = jar.getEjbJar().getEnterpriseBeans().length;
      if (beansInEjbJar != beansDeployed) {
         for(EnterpriseBean bean : jar.getEjbJar().getEnterpriseBeans()) {
            if (!ejbds.containsKey(bean.getEjbName())) {
               ConfigUtils.logger.warning("conf.0018", bean.getEjbName(), jar.getJarLocation());
            }
         }

         String message = messages.format("conf.0008", jar.getJarLocation(), String.valueOf(beansInEjbJar), String.valueOf(beansDeployed));
         logger.warning(message);
         throw new OpenEJBException(message);
      } else {
         Map<String, EnterpriseBeanInfo> infos = new HashMap();
         Map<String, EnterpriseBean> items = new HashMap();
         EjbJarInfo ejbJar = new EjbJarInfo();
         ejbJar.path = jar.getJarLocation();
         ejbJar.moduleUri = jar.getModuleUri();
         ejbJar.moduleId = jar.getModuleId();
         if (jar.getEjbJar() != null && jar.getEjbJar().getModuleName() != null) {
            ejbJar.moduleName = jar.getEjbJar().getModuleName();
         } else {
            ejbJar.moduleName = jar.getModuleId();
         }

         ejbJar.watchedResources.addAll(jar.getWatchedResources());
         ejbJar.properties.putAll(jar.getProperties());
         ejbJar.properties.putAll(jar.getOpenejbJar().getProperties());

         for(EnterpriseBean bean : jar.getEjbJar().getEnterpriseBeans()) {
            EnterpriseBeanInfo beanInfo;
            if (bean instanceof SessionBean) {
               beanInfo = this.initSessionBean((SessionBean)bean, ejbJar, ejbds);
            } else if (bean instanceof EntityBean) {
               beanInfo = this.initEntityBean((EntityBean)bean, ejbds);
            } else {
               if (!(bean instanceof MessageDrivenBean)) {
                  throw new OpenEJBException("Unknown bean type: " + bean.getClass().getName());
               }

               beanInfo = this.initMessageBean((MessageDrivenBean)bean, ejbds);
            }

            ejbJar.enterpriseBeans.add(beanInfo);
            if (this.deploymentIds.contains(beanInfo.ejbDeploymentId)) {
               String message = messages.format("conf.0100", beanInfo.ejbDeploymentId, jar.getJarLocation(), beanInfo.ejbName);
               logger.warning(message);
               throw new OpenEJBException(message);
            }

            this.deploymentIds.add(beanInfo.ejbDeploymentId);
            beanInfo.codebase = jar.getJarLocation();
            infos.put(beanInfo.ejbName, beanInfo);
            items.put(beanInfo.ejbName, bean);
            if (bean.getSecurityIdentity() != null) {
               beanInfo.runAs = bean.getSecurityIdentity().getRunAs();
               EjbDeployment deployment = (EjbDeployment)ejbds.get(beanInfo.ejbName);
               if (deployment != null) {
                  for(RoleMapping mapping : deployment.getRoleMapping()) {
                     if (mapping.getRoleName().equals(beanInfo.runAs)) {
                        beanInfo.runAsUser = mapping.getPrincipalName();
                        break;
                     }
                  }
               }
            }

            this.initJndiNames(ejbds, beanInfo);
         }

         if (jar.getEjbJar().getAssemblyDescriptor() != null) {
            this.initInterceptors(jar, ejbJar);
            this.initSecurityRoles(jar, ejbJar);
            this.initMethodPermissions(jar, ejbds, ejbJar);
            this.initExcludesList(jar, ejbds, ejbJar);
            this.initMethodTransactions(jar, ejbds, ejbJar);
            this.initMethodConcurrency(jar, ejbds, ejbJar);
            this.initApplicationExceptions(jar, ejbJar);

            for(EnterpriseBeanInfo bean : ejbJar.enterpriseBeans) {
               this.resolveRoleLinks(bean, (JndiConsumer)items.get(bean.ejbName));
            }
         }

         if (jar.getEjbJar().getRelationships() != null) {
            this.initRelationships(jar, infos);
         }

         Beans beans = jar.getBeans();
         if (beans != null) {
            ejbJar.beans = new BeansInfo();
            ejbJar.beans.version = beans.getVersion();
            ejbJar.beans.discoveryMode = beans.getBeanDiscoveryMode();
            if (beans.getScan() != null) {
               for(Beans.Scan.Exclude exclude : beans.getScan().getExclude()) {
                  ExclusionInfo exclusionInfo = new ExclusionInfo();

                  for(Object config : exclude.getIfClassAvailableOrIfClassNotAvailableOrIfSystemProperty()) {
                     if (Beans.Scan.Exclude.IfAvailableClassCondition.class.isInstance(config)) {
                        exclusionInfo.availableClasses.add(((Beans.Scan.Exclude.ClassCondition)Beans.Scan.Exclude.ClassCondition.class.cast(config)).getName());
                     } else if (Beans.Scan.Exclude.IfNotAvailableClassCondition.class.isInstance(config)) {
                        exclusionInfo.notAvailableClasses.add(((Beans.Scan.Exclude.ClassCondition)Beans.Scan.Exclude.ClassCondition.class.cast(config)).getName());
                     } else {
                        if (!Beans.Scan.Exclude.IfSystemProperty.class.isInstance(config)) {
                           throw new IllegalArgumentException("Not supported: " + config);
                        }

                        Beans.Scan.Exclude.IfSystemProperty systemProperty = (Beans.Scan.Exclude.IfSystemProperty)Beans.Scan.Exclude.IfSystemProperty.class.cast(config);
                        if (systemProperty.getValue() == null) {
                           exclusionInfo.systemPropertiesPresence.add(systemProperty.getName());
                        } else {
                           exclusionInfo.systemProperties.put(systemProperty.getName(), systemProperty.getValue());
                        }
                     }
                  }

                  BeansInfo.ExclusionEntryInfo exclusionEntryInfo = new BeansInfo.ExclusionEntryInfo();
                  exclusionEntryInfo.name = exclude.getName();
                  exclusionEntryInfo.exclusion = exclusionInfo;
                  ejbJar.beans.excludes.add(exclusionEntryInfo);
               }
            }

            ejbJar.beans.duplicatedAlternativeClasses.addAll(beans.getDuplicatedAlternatives().getClasses());
            ejbJar.beans.duplicatedAlternativeStereotypes.addAll(beans.getDuplicatedAlternatives().getStereotypes());
            ejbJar.beans.duplicatedInterceptors.addAll(beans.getDuplicatedInterceptors());
            ejbJar.beans.duplicatedDecorators.addAll(beans.getDuplicatedDecorators());
            ejbJar.beans.startupClasses.addAll(beans.getStartupBeans());
            Map<URL, String> discoveryModeByUrl = new HashMap();
            boolean isComposite = CompositeBeans.class.isInstance(beans);
            CompositeBeans composite;
            if (isComposite) {
               composite = (CompositeBeans)CompositeBeans.class.cast(beans);
               discoveryModeByUrl.putAll(composite.getDiscoveryByUrl());
            } else {
               composite = null;
               URL key = DEFAULT_BEANS_XML_KEY;
               if (beans.getUri() != null) {
                  try {
                     key = new URL(beans.getUri());
                  } catch (MalformedURLException var16) {
                  }
               }

               discoveryModeByUrl.put(key, beans.getBeanDiscoveryMode());
            }

            for(Map.Entry<URL, List<String>> next : beans.getManagedClasses().entrySet()) {
               URL key = (URL)next.getKey();
               BeansInfo.BDAInfo bdaInfo = new BeansInfo.BDAInfo();
               bdaInfo.discoveryMode = (String)discoveryModeByUrl.get(key);
               this.merge(composite, key == null ? DEFAULT_BEANS_XML_KEY : key, bdaInfo, (List)next.getValue());
               ejbJar.beans.bdas.add(bdaInfo);
            }

            for(Map.Entry<URL, List<String>> next : beans.getNotManagedClasses().entrySet()) {
               URL key = (URL)next.getKey();
               BeansInfo.BDAInfo bdaInfo = new BeansInfo.BDAInfo();
               bdaInfo.discoveryMode = BeanDiscoveryMode.ANNOTATED.name();
               this.merge(composite, key == null ? DEFAULT_BEANS_XML_KEY : key, bdaInfo, (List)next.getValue());
               ejbJar.beans.noDescriptorBdas.add(bdaInfo);
            }

            if (composite == null && ejbJar.beans.bdas.size() == 1) {
               BeansInfo.BDAInfo bda = (BeansInfo.BDAInfo)ejbJar.beans.bdas.iterator().next();
               bda.alternatives.addAll(beans.getAlternativeClasses());
               bda.interceptors.addAll(beans.getInterceptors());
               bda.decorators.addAll(beans.getDecorators());
               bda.stereotypeAlternatives.addAll(beans.getAlternativeStereotypes());
            }
         }

         return ejbJar;
      }
   }

   private void merge(CompositeBeans composite, URL key, BeansInfo.BDAInfo bdaInfo, List<String> managedClasses) {
      bdaInfo.managedClasses.addAll(managedClasses);

      try {
         bdaInfo.uri = key == null ? null : key.toURI();
      } catch (URISyntaxException var9) {
         bdaInfo.uri = null;
      }

      if (composite != null) {
         Collection<String> interceptors = (Collection)composite.getInterceptorsByUrl().get(key);
         if (interceptors != null) {
            bdaInfo.interceptors.addAll(interceptors);
         }

         Collection<String> decorators = (Collection)composite.getDecoratorsByUrl().get(key);
         if (decorators != null) {
            bdaInfo.decorators.addAll(decorators);
         }

         Collection<String> alternatives = (Collection)composite.getAlternativesByUrl().get(key);
         if (alternatives != null) {
            bdaInfo.alternatives.addAll(alternatives);
         }

         Collection<String> alternativeStereotypes = (Collection)composite.getAlternativeStereotypesByUrl().get(key);
         if (alternativeStereotypes != null) {
            bdaInfo.stereotypeAlternatives.addAll(alternativeStereotypes);
         }
      }

   }

   private void initJndiNames(Map<String, EjbDeployment> ejbds, EnterpriseBeanInfo info) {
      EjbDeployment deployment = (EjbDeployment)ejbds.get(info.ejbName);
      if (deployment != null) {
         for(Jndi jndi : deployment.getJndi()) {
            JndiNameInfo jndiNameInfo = new JndiNameInfo();
            jndiNameInfo.intrface = jndi.getInterface();
            jndiNameInfo.name = jndi.getName();
            info.jndiNamess.add(jndiNameInfo);
         }
      }

   }

   private void initRelationships(EjbModule jar, Map<String, EnterpriseBeanInfo> infos) throws OpenEJBException {
      for(EjbRelation ejbRelation : jar.getEjbJar().getRelationships().getEjbRelation()) {
         Iterator<EjbRelationshipRole> iterator = ejbRelation.getEjbRelationshipRole().iterator();
         EjbRelationshipRole left = (EjbRelationshipRole)iterator.next();
         EjbRelationshipRole right = (EjbRelationshipRole)iterator.next();
         CmrFieldInfo leftCmrFieldInfo = this.initRelationshipRole(left, right, infos);
         CmrFieldInfo rightCmrFieldInfo = this.initRelationshipRole(right, left, infos);
         leftCmrFieldInfo.mappedBy = rightCmrFieldInfo;
         rightCmrFieldInfo.mappedBy = leftCmrFieldInfo;
      }

   }

   private CmrFieldInfo initRelationshipRole(EjbRelationshipRole role, EjbRelationshipRole relatedRole, Map<String, EnterpriseBeanInfo> infos) throws OpenEJBException {
      CmrFieldInfo cmrFieldInfo = new CmrFieldInfo();
      String ejbName = role.getRelationshipRoleSource().getEjbName();
      EnterpriseBeanInfo enterpriseBeanInfo = (EnterpriseBeanInfo)infos.get(ejbName);
      if (enterpriseBeanInfo == null) {
         throw new OpenEJBException("Relation role source ejb not found " + ejbName);
      } else if (!(enterpriseBeanInfo instanceof EntityBeanInfo)) {
         throw new OpenEJBException("Relation role source ejb is not an entity bean " + ejbName);
      } else {
         EntityBeanInfo entityBeanInfo = (EntityBeanInfo)enterpriseBeanInfo;
         cmrFieldInfo.roleSource = entityBeanInfo;
         cmrFieldInfo.roleName = role.getEjbRelationshipRoleName();
         cmrFieldInfo.synthetic = role.getCmrField() == null;
         if (role.getCmrField() != null) {
            cmrFieldInfo.fieldName = role.getCmrField().getCmrFieldName();
            if (role.getCmrField().getCmrFieldType() != null) {
               cmrFieldInfo.fieldType = role.getCmrField().getCmrFieldType().toString();
            }

            if (cmrFieldInfo.fieldType == null && relatedRole.getMultiplicity() == Multiplicity.MANY) {
               cmrFieldInfo.fieldType = Collection.class.getName();
            }
         } else {
            String relatedEjbName = relatedRole.getRelationshipRoleSource().getEjbName();
            EnterpriseBeanInfo relatedEjb = (EnterpriseBeanInfo)infos.get(relatedEjbName);
            if (relatedEjb == null) {
               throw new OpenEJBException("Relation role source ejb not found " + relatedEjbName);
            }

            if (!(relatedEjb instanceof EntityBeanInfo)) {
               throw new OpenEJBException("Relation role source ejb is not an entity bean " + relatedEjbName);
            }

            EntityBeanInfo relatedEntity = (EntityBeanInfo)relatedEjb;
            relatedRole.getRelationshipRoleSource();
            cmrFieldInfo.fieldName = relatedEntity.abstractSchemaName + "_" + relatedRole.getCmrField().getCmrFieldName();
            if (relatedRole.getMultiplicity() == Multiplicity.MANY) {
               cmrFieldInfo.fieldType = Collection.class.getName();
            }
         }

         cmrFieldInfo.cascadeDelete = role.getCascadeDelete();
         cmrFieldInfo.many = role.getMultiplicity() == Multiplicity.MANY;
         entityBeanInfo.cmrFields.add(cmrFieldInfo);
         return cmrFieldInfo;
      }
   }

   private void initInterceptors(EjbModule jar, EjbJarInfo ejbJar) throws OpenEJBException {
      if (jar.getEjbJar().getInterceptors().length != 0) {
         if (jar.getEjbJar().getAssemblyDescriptor() != null) {
            if (jar.getEjbJar().getAssemblyDescriptor().getInterceptorBinding() != null) {
               for(Interceptor s : jar.getEjbJar().getInterceptors()) {
                  InterceptorInfo info = new InterceptorInfo();
                  info.clazz = s.getInterceptorClass();
                  this.copyCallbacks(s.getAroundInvoke(), info.aroundInvoke);
                  this.copyCallbacks(s.getPostConstruct(), info.postConstruct);
                  this.copyCallbacks(s.getPreDestroy(), info.preDestroy);
                  this.copyCallbacks(s.getPostActivate(), info.postActivate);
                  this.copyCallbacks(s.getPrePassivate(), info.prePassivate);
                  this.copyCallbacks(s.getAfterBegin(), info.afterBegin);
                  this.copyCallbacks(s.getBeforeCompletion(), info.beforeCompletion);
                  this.copyCallbacks(s.getAfterCompletion(), info.afterCompletion);
                  this.copyCallbacks(s.getAroundTimeout(), info.aroundTimeout);
                  ejbJar.interceptors.add(info);
               }

               for(InterceptorBinding binding : jar.getEjbJar().getAssemblyDescriptor().getInterceptorBinding()) {
                  InterceptorBindingInfo info = new InterceptorBindingInfo();
                  info.ejbName = binding.getEjbName();
                  info.excludeClassInterceptors = binding.getExcludeClassInterceptors();
                  info.excludeDefaultInterceptors = binding.getExcludeDefaultInterceptors();
                  info.interceptors.addAll(binding.getInterceptorClass());
                  if (binding.getInterceptorOrder() != null) {
                     info.interceptorOrder.addAll(binding.getInterceptorOrder().getInterceptorClass());
                  }

                  info.method = this.toInfo(binding.getMethod());
                  info.className = binding.getClassName();
                  ejbJar.interceptorBindings.add(info);
               }

            }
         }
      }
   }

   private void initMethodTransactions(EjbModule jar, Map ejbds, EjbJarInfo ejbJarInfo) {
      for(ContainerTransaction cTx : jar.getEjbJar().getAssemblyDescriptor().getContainerTransaction()) {
         MethodTransactionInfo info = new MethodTransactionInfo();
         info.description = cTx.getDescription();
         info.transAttribute = cTx.getTransAttribute().toString();
         info.methods.addAll(this.getMethodInfos(cTx.getMethod(), ejbds));
         ejbJarInfo.methodTransactions.add(info);
      }

   }

   private void initMethodConcurrency(EjbModule jar, Map ejbds, EjbJarInfo ejbJarInfo) {
      for(ContainerConcurrency att : jar.getEjbJar().getAssemblyDescriptor().getContainerConcurrency()) {
         MethodConcurrencyInfo info = new MethodConcurrencyInfo();
         info.description = att.getDescription();
         if (att.getLock() != null) {
            info.concurrencyAttribute = att.getLock().toString();
         }

         info.accessTimeout = this.toInfo(att.getAccessTimeout());
         info.methods.addAll(this.getMethodInfos(att.getMethod(), ejbds));
         ejbJarInfo.methodConcurrency.add(info);
      }

   }

   private void copyConcurrentMethods(SessionBean bean, EjbJarInfo ejbJarInfo, Map ejbds) {
      for(ConcurrentMethod method : bean.getConcurrentMethod()) {
         MethodConcurrencyInfo info = new MethodConcurrencyInfo();
         if (method.getLock() != null) {
            info.concurrencyAttribute = method.getLock().toString();
         }

         info.accessTimeout = this.toInfo(method.getAccessTimeout());
         Method m = new Method(bean.getEjbName(), (String)null, method.getMethod().getMethodName());
         m.setMethodParams(method.getMethod().getMethodParams());
         info.methods.add(this.getMethodInfo(m, ejbds));
         ejbJarInfo.methodConcurrency.add(info);
      }

   }

   private void copySchedules(List<Timer> timers, List<MethodScheduleInfo> scheduleInfos) {
      Map<NamedMethod, MethodScheduleInfo> methodScheduleInfoMap = new HashMap();

      for(Timer timer : timers) {
         NamedMethod timeoutMethod = timer.getTimeoutMethod();
         MethodScheduleInfo methodScheduleInfo = (MethodScheduleInfo)methodScheduleInfoMap.get(timer.getTimeoutMethod());
         if (methodScheduleInfo == null) {
            methodScheduleInfo = new MethodScheduleInfo();
            methodScheduleInfoMap.put(timeoutMethod, methodScheduleInfo);
            methodScheduleInfo.method = this.toInfo(timeoutMethod);
         }

         ScheduleInfo scheduleInfo = new ScheduleInfo();
         TimerSchedule timerSchedule = timer.getSchedule();
         if (timerSchedule != null) {
            scheduleInfo.second = timerSchedule.getSecond();
            scheduleInfo.minute = timerSchedule.getMinute();
            scheduleInfo.hour = timerSchedule.getHour();
            scheduleInfo.dayOfWeek = timerSchedule.getDayOfWeek();
            scheduleInfo.dayOfMonth = timerSchedule.getDayOfMonth();
            scheduleInfo.month = timerSchedule.getMonth();
            scheduleInfo.year = timerSchedule.getYear();
         }

         scheduleInfo.timezone = timer.getTimezone();
         if (timer.getStart() != null) {
            scheduleInfo.start = timer.getStart().toGregorianCalendar().getTime();
         }

         if (timer.getEnd() != null) {
            scheduleInfo.end = timer.getEnd().toGregorianCalendar().getTime();
         }

         scheduleInfo.info = timer.getInfo();
         if (timer.getPersistent() != null) {
            scheduleInfo.persistent = timer.getPersistent();
         }

         methodScheduleInfo.schedules.add(scheduleInfo);
      }

      scheduleInfos.addAll(methodScheduleInfoMap.values());
   }

   private void initApplicationExceptions(EjbModule jar, EjbJarInfo ejbJarInfo) {
      for(ApplicationException applicationException : jar.getEjbJar().getAssemblyDescriptor().getApplicationException()) {
         ApplicationExceptionInfo info = new ApplicationExceptionInfo();
         info.exceptionClass = applicationException.getExceptionClass();
         info.rollback = applicationException.isRollback();
         info.inherited = applicationException.isInherited();
         ejbJarInfo.applicationException.add(info);
      }

   }

   private void initSecurityRoles(EjbModule jar, EjbJarInfo ejbJarInfo) {
      for(SecurityRole sr : jar.getEjbJar().getAssemblyDescriptor().getSecurityRole()) {
         SecurityRoleInfo info = new SecurityRoleInfo();
         info.description = sr.getDescription();
         info.roleName = sr.getRoleName();
         if (this.securityRoles.contains(sr.getRoleName())) {
            ConfigUtils.logger.warning("conf.0102", jar.getJarLocation(), sr.getRoleName());
         } else {
            this.securityRoles.add(sr.getRoleName());
         }

         ejbJarInfo.securityRoles.add(info);
      }

   }

   private void initMethodPermissions(EjbModule jar, Map ejbds, EjbJarInfo ejbJarInfo) {
      for(MethodPermission mp : jar.getEjbJar().getAssemblyDescriptor().getMethodPermission()) {
         MethodPermissionInfo info = new MethodPermissionInfo();
         info.description = mp.getDescription();
         info.roleNames.addAll(mp.getRoleName());
         info.methods.addAll(this.getMethodInfos(mp.getMethod(), ejbds));
         info.unchecked = mp.getUnchecked();
         ejbJarInfo.methodPermissions.add(info);
      }

   }

   private void initExcludesList(EjbModule jar, Map ejbds, EjbJarInfo ejbJarInfo) {
      ExcludeList methodPermissions = jar.getEjbJar().getAssemblyDescriptor().getExcludeList();

      for(Method excludedMethod : methodPermissions.getMethod()) {
         ejbJarInfo.excludeList.add(this.getMethodInfo(excludedMethod, ejbds));
      }

   }

   private void resolveRoleLinks(EnterpriseBeanInfo bean, JndiConsumer item) {
      if (item instanceof RemoteBean) {
         RemoteBean rb = (RemoteBean)item;

         for(SecurityRoleRef ref : rb.getSecurityRoleRef()) {
            SecurityRoleReferenceInfo info = new SecurityRoleReferenceInfo();
            info.description = ref.getDescription();
            info.roleLink = ref.getRoleLink();
            info.roleName = ref.getRoleName();
            if (info.roleLink == null) {
               info.roleLink = info.roleName;
            }

            bean.securityRoleReferences.add(info);
         }

      }
   }

   private List<MethodInfo> getMethodInfos(List<Method> ms, Map ejbds) {
      if (ms == null) {
         return Collections.emptyList();
      } else {
         List<MethodInfo> mi = new ArrayList(ms.size());

         for(Method method : ms) {
            MethodInfo methodInfo = this.getMethodInfo(method, ejbds);
            mi.add(methodInfo);
         }

         return mi;
      }
   }

   private MethodInfo getMethodInfo(Method method, Map ejbds) {
      MethodInfo methodInfo = new MethodInfo();
      EjbDeployment d = (EjbDeployment)ejbds.get(method.getEjbName());
      methodInfo.description = method.getDescription();
      methodInfo.ejbDeploymentId = d == null ? null : d.getDeploymentId();
      methodInfo.ejbName = method.getEjbName();
      methodInfo.methodIntf = method.getMethodIntf() == null ? null : method.getMethodIntf().toString();
      methodInfo.methodName = method.getMethodName();
      if (methodInfo.methodName == null || methodInfo.methodName.equals("")) {
         methodInfo.methodName = "*";
      }

      methodInfo.className = method.getClassName();
      if (methodInfo.className == null || methodInfo.className.equals("")) {
         methodInfo.className = "*";
      }

      MethodParams mp = method.getMethodParams();
      if (mp != null) {
         methodInfo.methodParams = mp.getMethodParam();
      }

      return methodInfo;
   }

   private EnterpriseBeanInfo initSessionBean(SessionBean s, EjbJarInfo ejbJar, Map m) throws OpenEJBException {
      EnterpriseBeanInfo bean;
      if (s.getSessionType() == SessionType.STATEFUL) {
         bean = new StatefulBeanInfo();
         bean.passivable = s.getPassivationCapable() == null || s.getPassivationCapable();
         StatefulBeanInfo stateful = (StatefulBeanInfo)bean;
         this.copyCallbacks(s.getPostActivate(), stateful.postActivate);
         this.copyCallbacks(s.getPrePassivate(), stateful.prePassivate);
         this.copyCallbacks(s.getAfterBegin(), stateful.afterBegin);
         this.copyCallbacks(s.getBeforeCompletion(), stateful.beforeCompletion);
         this.copyCallbacks(s.getAfterCompletion(), stateful.afterCompletion);

         for(InitMethod initMethod : s.getInitMethod()) {
            InitMethodInfo init = new InitMethodInfo();
            init.beanMethod = this.toInfo(initMethod.getBeanMethod());
            init.createMethod = this.toInfo(initMethod.getCreateMethod());
            stateful.initMethods.add(init);
         }

         for(RemoveMethod removeMethod : s.getRemoveMethod()) {
            RemoveMethodInfo remove = new RemoveMethodInfo();
            remove.beanMethod = this.toInfo(removeMethod.getBeanMethod());
            remove.retainIfException = removeMethod.getRetainIfException();
            stateful.removeMethods.add(remove);
         }

         this.copyConcurrentMethods(s, ejbJar, m);
      } else if (s.getSessionType() == SessionType.MANAGED) {
         bean = new ManagedBeanInfo();
         ManagedBeanInfo managed = (ManagedBeanInfo)bean;
         managed.hidden = !(s instanceof ManagedBean) || ((ManagedBean)s).isHidden();
         this.copyCallbacks(s.getPostActivate(), managed.postActivate);
         this.copyCallbacks(s.getPrePassivate(), managed.prePassivate);

         for(RemoveMethod removeMethod : s.getRemoveMethod()) {
            RemoveMethodInfo remove = new RemoveMethodInfo();
            remove.beanMethod = this.toInfo(removeMethod.getBeanMethod());
            remove.retainIfException = removeMethod.getRetainIfException();
            managed.removeMethods.add(remove);
         }
      } else if (s.getSessionType() == SessionType.SINGLETON) {
         bean = new SingletonBeanInfo();
         ConcurrencyManagementType type = s.getConcurrencyManagementType();
         bean.concurrencyType = type != null ? type.toString() : ConcurrencyManagementType.CONTAINER.toString();
         bean.loadOnStartup = s.getInitOnStartup();
         this.copyCallbacks(s.getAroundTimeout(), bean.aroundTimeout);
         this.copySchedules(s.getTimer(), bean.methodScheduleInfos);
         this.copyConcurrentMethods(s, ejbJar, m);
      } else {
         bean = new StatelessBeanInfo();
         this.copySchedules(s.getTimer(), bean.methodScheduleInfos);
      }

      if (s.getSessionType() != SessionType.STATEFUL) {
         this.copyCallbacks(s.getAroundTimeout(), bean.aroundTimeout);
      }

      bean.localbean = s.getLocalBean() != null;
      bean.timeoutMethod = this.toInfo(s.getTimeoutMethod());
      this.copyCallbacks(s.getAroundInvoke(), bean.aroundInvoke);
      this.copyCallbacks(s.getPostConstruct(), bean.postConstruct);
      this.copyCallbacks(s.getPreDestroy(), bean.preDestroy);
      this.copyAsynchronous(s.getAsyncMethod(), bean.asynchronous);
      bean.asynchronousClasses.addAll(s.getAsynchronousClasses());
      EjbDeployment d = (EjbDeployment)m.get(s.getEjbName());
      if (d == null) {
         throw new OpenEJBException("No deployment information in openejb-jar.xml for bean " + s.getEjbName() + ". Please redeploy the jar");
      } else {
         bean.ejbDeploymentId = d.getDeploymentId();
         bean.containerId = d.getContainerId();
         Icon icon = s.getIcon();
         bean.largeIcon = icon == null ? null : icon.getLargeIcon();
         bean.smallIcon = icon == null ? null : icon.getSmallIcon();
         bean.description = s.getDescription();
         bean.displayName = s.getDisplayName();
         bean.ejbClass = s.getEjbClass();
         bean.ejbName = s.getEjbName();
         bean.home = s.getHome();
         bean.remote = s.getRemote();
         bean.localHome = s.getLocalHome();
         bean.local = s.getLocal();
         bean.proxy = s.getProxy();
         bean.parents.addAll(s.getParents());
         bean.businessLocal.addAll(s.getBusinessLocal());
         bean.businessRemote.addAll(s.getBusinessRemote());
         TransactionType txType = s.getTransactionType();
         bean.transactionType = txType != null ? txType.toString() : TransactionType.CONTAINER.toString();
         bean.serviceEndpoint = s.getServiceEndpoint();
         bean.properties.putAll(d.getProperties());
         bean.statefulTimeout = this.toInfo(s.getStatefulTimeout());
         bean.restService = s.isRestService() && !(s instanceof StatefulBean);
         return bean;
      }
   }

   private void copyAsynchronous(List<AsyncMethod> methods, List<NamedMethodInfo> methodInfos) {
      for(AsyncMethod asyncMethod : methods) {
         NamedMethodInfo info = new NamedMethodInfo();
         info.methodName = asyncMethod.getMethodName();
         if (asyncMethod.getMethodParams() != null) {
            info.methodParams = asyncMethod.getMethodParams().getMethodParam();
         }

         methodInfos.add(info);
      }

   }

   private TimeoutInfo toInfo(Timeout timeout) {
      if (timeout == null) {
         return null;
      } else {
         TimeoutInfo accessTimeout = new TimeoutInfo();
         accessTimeout.time = timeout.getTimeout();
         accessTimeout.unit = timeout.getUnit().toString();
         return accessTimeout;
      }
   }

   private EnterpriseBeanInfo initMessageBean(MessageDrivenBean mdb, Map m) throws OpenEJBException {
      MessageDrivenBeanInfo bean = new MessageDrivenBeanInfo();
      bean.timeoutMethod = this.toInfo(mdb.getTimeoutMethod());
      this.copyCallbacks(mdb.getAroundTimeout(), bean.aroundTimeout);
      this.copyCallbacks(mdb.getAroundInvoke(), bean.aroundInvoke);
      this.copyCallbacks(mdb.getPostConstruct(), bean.postConstruct);
      this.copyCallbacks(mdb.getPreDestroy(), bean.preDestroy);
      this.copySchedules(mdb.getTimer(), bean.methodScheduleInfos);
      EjbDeployment d = (EjbDeployment)m.get(mdb.getEjbName());
      if (d == null) {
         throw new OpenEJBException("No deployment information in openejb-jar.xml for bean " + mdb.getEjbName() + ". Please redeploy the jar");
      } else {
         bean.ejbDeploymentId = d.getDeploymentId();
         bean.containerId = d.getContainerId();
         Icon icon = mdb.getIcon();
         bean.largeIcon = icon == null ? null : icon.getLargeIcon();
         bean.smallIcon = icon == null ? null : icon.getSmallIcon();
         bean.description = mdb.getDescription();
         bean.displayName = mdb.getDisplayName();
         bean.ejbClass = mdb.getEjbClass();
         bean.ejbName = mdb.getEjbName();
         TransactionType txType = mdb.getTransactionType();
         bean.transactionType = txType != null ? txType.toString() : TransactionType.CONTAINER.toString();
         bean.properties.putAll(d.getProperties());
         if (mdb.getMessagingType() != null) {
            bean.mdbInterface = mdb.getMessagingType();
         } else {
            bean.mdbInterface = "javax.jms.MessageListener";
         }

         ResourceLink resourceLink = d.getResourceLink("openejb/destination");
         if (resourceLink != null) {
            bean.destinationId = resourceLink.getResId();
         }

         if (mdb.getMessageDestinationType() != null) {
            bean.activationProperties.put("destinationType", mdb.getMessageDestinationType());
         }

         ActivationConfig activationConfig = mdb.getActivationConfig();
         if (activationConfig != null) {
            for(ActivationConfigProperty property : activationConfig.getActivationConfigProperty()) {
               String name = property.getActivationConfigPropertyName();
               String value = property.getActivationConfigPropertyValue();
               bean.activationProperties.put(name, value);
            }
         }

         return bean;
      }
   }

   private NamedMethodInfo toInfo(NamedMethod method) {
      if (method == null) {
         return null;
      } else {
         NamedMethodInfo info = new NamedMethodInfo();
         info.methodName = method.getMethodName();
         if (method.getMethodParams() != null) {
            info.methodParams = method.getMethodParams().getMethodParam();
         }

         return info;
      }
   }

   private void copyCallbacks(List<? extends CallbackMethod> from, List<CallbackInfo> to) {
      for(CallbackMethod callback : from) {
         CallbackInfo info = new CallbackInfo();
         info.className = callback.getClassName();
         info.method = callback.getMethodName();
         to.add(info);
      }

   }

   private EnterpriseBeanInfo initEntityBean(EntityBean e, Map m) throws OpenEJBException {
      EntityBeanInfo bean = new EntityBeanInfo();
      EjbDeployment d = (EjbDeployment)m.get(e.getEjbName());
      if (d == null) {
         throw new OpenEJBException("No deployment information in openejb-jar.xml for bean " + e.getEjbName() + ". Please redeploy the jar");
      } else {
         bean.ejbDeploymentId = d.getDeploymentId();
         bean.containerId = d.getContainerId();
         Icon icon = e.getIcon();
         bean.largeIcon = icon == null ? null : icon.getLargeIcon();
         bean.smallIcon = icon == null ? null : icon.getSmallIcon();
         bean.description = e.getDescription();
         bean.displayName = e.getDisplayName();
         bean.ejbClass = e.getEjbClass();
         bean.abstractSchemaName = e.getAbstractSchemaName();
         bean.ejbName = e.getEjbName();
         bean.home = e.getHome();
         bean.remote = e.getRemote();
         bean.localHome = e.getLocalHome();
         bean.local = e.getLocal();
         bean.transactionType = "Container";
         bean.primKeyClass = e.getPrimKeyClass();
         bean.primKeyField = e.getPrimkeyField();
         bean.persistenceType = e.getPersistenceType().toString();
         bean.reentrant = String.valueOf(e.getReentrant());
         bean.properties.putAll(d.getProperties());
         CmpVersion cmpVersion = e.getCmpVersion();
         if (e.getPersistenceType() == PersistenceType.CONTAINER) {
            if (cmpVersion != null && cmpVersion == CmpVersion.CMP1) {
               bean.cmpVersion = 1;
            } else {
               bean.cmpVersion = 2;
            }
         }

         for(CmpField cmpField : e.getCmpField()) {
            bean.cmpFieldNames.add(cmpField.getFieldName());
         }

         if (bean.persistenceType.equalsIgnoreCase("Container")) {
            for(Query q : e.getQuery()) {
               QueryInfo query = new QueryInfo();
               query.queryStatement = q.getEjbQl().trim();
               MethodInfo method = new MethodInfo();
               method.ejbName = bean.ejbName;
               method.className = "*";
               QueryMethod qm = q.getQueryMethod();
               method.methodName = qm.getMethodName();
               if (qm.getMethodParams() != null) {
                  method.methodParams = qm.getMethodParams().getMethodParam();
               }

               query.method = method;
               ResultTypeMapping resultType = q.getResultTypeMapping();
               if (ResultTypeMapping.REMOTE.equals(resultType)) {
                  query.remoteResultType = true;
               }

               bean.queries.add(query);
            }

            for(org.apache.openejb.jee.oejb3.Query q : d.getQuery()) {
               QueryInfo query = new QueryInfo();
               query.description = q.getDescription();
               query.queryStatement = q.getObjectQl().trim();
               MethodInfo method = new MethodInfo();
               method.ejbName = bean.ejbName;
               method.className = "*";
               org.apache.openejb.jee.oejb3.QueryMethod qm = q.getQueryMethod();
               method.methodName = qm.getMethodName();
               if (qm.getMethodParams() != null) {
                  method.methodParams = qm.getMethodParams().getMethodParam();
               }

               query.method = method;
               bean.queries.add(query);
            }
         }

         return bean;
      }
   }

   static {
      try {
         DEFAULT_BEANS_XML_KEY = new URL("jar:file://!/META-INF/beans.xml");
      } catch (MalformedURLException e) {
         throw new IllegalStateException(e);
      }

      messages = new Messages("org.apache.openejb.util.resources");
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }
}
