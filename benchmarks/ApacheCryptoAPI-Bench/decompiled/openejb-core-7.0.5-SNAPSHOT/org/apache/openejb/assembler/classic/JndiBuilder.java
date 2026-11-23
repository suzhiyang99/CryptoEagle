package org.apache.openejb.assembler.classic;

import java.lang.reflect.Constructor;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.ejb.embeddable.EJBContainer;
import javax.jms.MessageListener;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.Reference;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.ModuleContext;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.core.ivm.naming.BusinessLocalBeanReference;
import org.apache.openejb.core.ivm.naming.BusinessLocalReference;
import org.apache.openejb.core.ivm.naming.BusinessRemoteReference;
import org.apache.openejb.core.ivm.naming.IntraVmJndiReference;
import org.apache.openejb.core.ivm.naming.ObjectReference;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Classes;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.StringTemplate;
import org.apache.openejb.util.Strings;

public class JndiBuilder {
   public static final String DEFAULT_NAME_KEY = "default";
   final boolean embeddedEjbContainerApi;
   public static final Logger logger;
   private static final boolean USE_OLD_JNDI_NAMES;
   private final Context openejbContext;
   private static final String JNDINAME_STRATEGY_CLASS = "openejb.jndiname.strategy.class";
   private static final String JNDINAME_FAILONCOLLISION = "openejb.jndiname.failoncollision";
   private final boolean failOnCollision;

   public JndiBuilder(Context openejbContext) {
      this.openejbContext = openejbContext;
      Options options = SystemInstance.get().getOptions();
      this.failOnCollision = options.get("openejb.jndiname.failoncollision", true);
      this.embeddedEjbContainerApi = options.get(EJBContainer.class.getName(), false);
   }

   public void build(EjbJarInfo ejbJar, HashMap<String, BeanContext> deployments) {
      JndiNameStrategy strategy = createStrategy(ejbJar, deployments);

      for(EnterpriseBeanInfo beanInfo : ejbJar.enterpriseBeans) {
         BeanContext beanContext = (BeanContext)deployments.get(beanInfo.ejbDeploymentId);
         strategy.begin(beanContext);

         try {
            this.bind(ejbJar, beanContext, beanInfo, strategy);
         } finally {
            strategy.end();
         }
      }

   }

   public static JndiNameStrategy createStrategy(EjbJarInfo ejbJar, Map<String, BeanContext> deployments) {
      Options options = new Options(ejbJar.properties, SystemInstance.get().getOptions());
      Class strategyClass = options.get("openejb.jndiname.strategy.class", TemplatedStrategy.class);
      String strategyClassName = strategyClass.getName();

      try {
         try {
            Constructor constructor = strategyClass.getConstructor(EjbJarInfo.class, Map.class);
            return (JndiNameStrategy)constructor.newInstance(ejbJar, deployments);
         } catch (NoSuchMethodException var6) {
            Constructor constructor = strategyClass.getConstructor();
            return (JndiNameStrategy)constructor.newInstance();
         }
      } catch (InstantiationException e) {
         throw new IllegalStateException("Could not instantiate JndiNameStrategy: " + strategyClassName, e);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Could not access JndiNameStrategy: " + strategyClassName, e);
      } catch (Throwable t) {
         throw new IllegalStateException("Could not create JndiNameStrategy: " + strategyClassName, t);
      }
   }

   public void bind(EjbJarInfo ejbJarInfo, BeanContext bean, EnterpriseBeanInfo beanInfo, JndiNameStrategy strategy) {
      if (!BeanContext.Comp.class.equals(bean.getBeanClass())) {
         Bindings bindings = new Bindings();
         bean.set(Bindings.class, bindings);
         Reference simpleNameRef = null;
         Object id = bean.getDeploymentID();

         try {
            if (bean.isLocalbean()) {
               Class beanClass = bean.getBeanClass();
               BeanContext.BusinessLocalBeanHome home = bean.getBusinessLocalBeanHome();
               BusinessLocalBeanReference ref = new BusinessLocalBeanReference(home);
               this.optionalBind(bindings, ref, "openejb/Deployment/" + format(id, beanClass.getName(), InterfaceType.LOCALBEAN));
               if (!bean.getBeanClass().isInterface()) {
                  for(Class<?> clazz = bean.getBeanClass().getSuperclass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
                     this.optionalBind(bindings, ref, "openejb/Deployment/" + format(id, clazz.getName(), InterfaceType.LOCALBEAN));
                  }
               }

               String internalName = "openejb/Deployment/" + format(id, beanClass.getName(), InterfaceType.BUSINESS_LOCALBEAN_HOME);
               this.bind(internalName, ref, bindings, beanInfo, beanClass);
               String name = strategy.getName(beanClass, "default", JndiBuilder.JndiNameStrategy.Interface.LOCALBEAN);
               this.bind("openejb/local/" + name, ref, bindings, beanInfo, beanClass);
               this.bindJava(bean, beanClass, ref, bindings, beanInfo);
               if (USE_OLD_JNDI_NAMES) {
                  bean.getModuleContext().getAppContext().getBindings().put(name, ref);
               }

               simpleNameRef = ref;
            }
         } catch (NamingException e) {
            throw new OpenEJBRuntimeException("Unable to bind business remote deployment in jndi.", e);
         }

         try {
            for(Class interfce : bean.getBusinessLocalInterfaces()) {
               BeanContext.BusinessLocalHome home = bean.getBusinessLocalHome(interfce);
               BusinessLocalReference ref = new BusinessLocalReference(home);
               this.optionalBind(bindings, ref, "openejb/Deployment/" + format(id, interfce.getName()));
               String internalName = "openejb/Deployment/" + format(id, interfce.getName(), InterfaceType.BUSINESS_LOCAL);
               this.bind(internalName, ref, bindings, beanInfo, interfce);
               String name = strategy.getName(interfce, "default", JndiBuilder.JndiNameStrategy.Interface.BUSINESS_LOCAL);
               String externalName = "openejb/local/" + name;
               this.bind(externalName, ref, bindings, beanInfo, interfce);
               this.bindJava(bean, interfce, ref, bindings, beanInfo);
               if (USE_OLD_JNDI_NAMES) {
                  bean.getModuleContext().getAppContext().getBindings().put(name, ref);
               }

               if (simpleNameRef == null) {
                  simpleNameRef = ref;
               }
            }
         } catch (NamingException e) {
            throw new OpenEJBRuntimeException("Unable to bind business local interface for deployment " + id, e);
         }

         try {
            for(Class interfce : bean.getBusinessRemoteInterfaces()) {
               BeanContext.BusinessRemoteHome home = bean.getBusinessRemoteHome(interfce);
               BusinessRemoteReference ref = new BusinessRemoteReference(home);
               this.optionalBind(bindings, ref, "openejb/Deployment/" + format((Object)id, interfce.getName(), (InterfaceType)null));
               String internalName = "openejb/Deployment/" + format(id, interfce.getName(), InterfaceType.BUSINESS_REMOTE);
               this.bind(internalName, ref, bindings, beanInfo, interfce);
               String name = strategy.getName(interfce, "default", JndiBuilder.JndiNameStrategy.Interface.BUSINESS_REMOTE);
               this.bind("openejb/local/" + name, ref, bindings, beanInfo, interfce);
               this.bind("openejb/remote/" + name, ref, bindings, beanInfo, interfce);
               this.bind("openejb/remote/" + this.computeGlobalName(bean, interfce), ref, bindings, beanInfo, interfce);
               this.bindJava(bean, interfce, ref, bindings, beanInfo);
               if (USE_OLD_JNDI_NAMES) {
                  bean.getModuleContext().getAppContext().getBindings().put(name, ref);
               }

               if (simpleNameRef == null) {
                  simpleNameRef = ref;
               }
            }
         } catch (NamingException e) {
            throw new OpenEJBRuntimeException("Unable to bind business remote deployment in jndi.", e);
         }

         try {
            Class localHomeInterface = bean.getLocalHomeInterface();
            if (localHomeInterface != null) {
               ObjectReference ref = new ObjectReference(bean.getEJBLocalHome());
               String name = strategy.getName(bean.getLocalHomeInterface(), "default", JndiBuilder.JndiNameStrategy.Interface.LOCAL_HOME);
               this.bind("openejb/local/" + name, ref, bindings, beanInfo, localHomeInterface);
               this.optionalBind(bindings, ref, "openejb/Deployment/" + format(id, localHomeInterface.getName(), InterfaceType.EJB_LOCAL_HOME));
               name = "openejb/Deployment/" + format(id, bean.getLocalInterface().getName());
               this.bind(name, ref, bindings, beanInfo, localHomeInterface);
               name = "openejb/Deployment/" + format(id, bean.getLocalInterface().getName(), InterfaceType.EJB_LOCAL);
               this.bind(name, ref, bindings, beanInfo, localHomeInterface);
               this.bindJava(bean, localHomeInterface, ref, bindings, beanInfo);
               if (simpleNameRef == null) {
                  simpleNameRef = ref;
               }
            }
         } catch (NamingException e) {
            throw new OpenEJBRuntimeException("Unable to bind local home interface for deployment " + id, e);
         }

         try {
            Class homeInterface = bean.getHomeInterface();
            if (homeInterface != null) {
               ObjectReference ref = new ObjectReference(bean.getEJBHome());
               String name = strategy.getName(homeInterface, "default", JndiBuilder.JndiNameStrategy.Interface.REMOTE_HOME);
               this.bind("openejb/local/" + name, ref, bindings, beanInfo, homeInterface);
               this.bind("openejb/remote/" + name, ref, bindings, beanInfo, homeInterface);
               this.optionalBind(bindings, ref, "openejb/Deployment/" + format(id, homeInterface.getName(), InterfaceType.EJB_HOME));
               name = "openejb/Deployment/" + format(id, bean.getRemoteInterface().getName());
               this.bind(name, ref, bindings, beanInfo, homeInterface);
               name = "openejb/Deployment/" + format(id, bean.getRemoteInterface().getName(), InterfaceType.EJB_OBJECT);
               this.bind(name, ref, bindings, beanInfo, homeInterface);
               this.bindJava(bean, homeInterface, ref, bindings, beanInfo);
               if (simpleNameRef == null) {
                  simpleNameRef = ref;
               }
            }
         } catch (NamingException e) {
            throw new OpenEJBRuntimeException("Unable to bind remote home interface for deployment " + id, e);
         }

         try {
            if (simpleNameRef != null) {
               this.bindJava(bean, (Class)null, simpleNameRef, bindings, beanInfo);
            }
         } catch (NamingException e) {
            throw new OpenEJBRuntimeException("Unable to bind simple java:global name in jndi", e);
         }

         try {
            if (MessageListener.class.equals(bean.getMdbInterface())) {
               String destinationId = bean.getDestinationId();
               String jndiName = "openejb/Resource/" + destinationId;
               Reference reference = new IntraVmJndiReference(jndiName);
               String deploymentId = id.toString();
               this.bind("openejb/local/" + deploymentId, reference, bindings, beanInfo, MessageListener.class);
               this.bind("openejb/remote/" + deploymentId, reference, bindings, beanInfo, MessageListener.class);
            }
         } catch (NamingException e) {
            throw new OpenEJBRuntimeException("Unable to bind mdb destination in jndi.", e);
         } catch (NoClassDefFoundError var16) {
         }

      }
   }

   private void optionalBind(Bindings bindings, Reference ref, String name) throws NamingException {
      try {
         this.openejbContext.bind(name, ref);
         logger.debug("bound ejb at name: " + name + ", ref: " + ref);
         bindings.add(name);
      } catch (NamingException var5) {
         logger.debug("failed to bind ejb at name: " + name + ", ref: " + ref);
      }

   }

   public static String format(Object deploymentId, String interfaceClassName) {
      return format((String)((String)deploymentId), interfaceClassName, (InterfaceType)null);
   }

   public static String format(Object deploymentId, String interfaceClassName, InterfaceType interfaceType) {
      return format((String)deploymentId, interfaceClassName, interfaceType);
   }

   public static String format(String deploymentId, String interfaceClassName, InterfaceType interfaceType) {
      return deploymentId + "/" + interfaceClassName + (interfaceType == null ? "" : "!" + interfaceType.getSpecName());
   }

   private void bind(String name, Reference ref, Bindings bindings, EnterpriseBeanInfo beanInfo, Class intrface) throws NamingException {
      if (!name.startsWith("openejb/local/") && !name.startsWith("openejb/remote/") && !name.startsWith("openejb/localbean/") && !name.startsWith("openejb/global/")) {
         try {
            this.openejbContext.bind(name, ref);
            logger.debug("bound ejb at name: " + name + ", ref: " + ref);
            bindings.add(name);
         } catch (NameAlreadyBoundException var9) {
            logger.error("Jndi name could not be bound; it may be taken by another ejb.  Jndi(name=" + name + ")");
            throw new NameAlreadyBoundException(name);
         }
      } else {
         String externalName = name.replaceFirst("openejb/[^/]+/", "");
         if (bindings.contains(name)) {
            if (name.startsWith("openejb/local/")) {
               logger.debug("Duplicate: Jndi(name=" + externalName + ")");
            }

            return;
         }

         try {
            this.openejbContext.bind(name, ref);
            bindings.add(name);
            if (!beanInfo.jndiNames.contains(externalName)) {
               beanInfo.jndiNames.add(externalName);
               JndiNameInfo nameInfo = new JndiNameInfo();
               nameInfo.intrface = intrface == null ? null : intrface.getName();
               nameInfo.name = externalName;
               beanInfo.jndiNamess.add(nameInfo);
               if (!this.embeddedEjbContainerApi && (!(beanInfo instanceof ManagedBeanInfo) || !((ManagedBeanInfo)beanInfo).hidden)) {
                  logger.info("Jndi(name=" + externalName + ") --> Ejb(deployment-id=" + beanInfo.ejbDeploymentId + ")");
               }
            }
         } catch (NameAlreadyBoundException var10) {
            BeanContext deployment = this.findNameOwner(name);
            if (deployment != null) {
               logger.error("Jndi(name=" + externalName + ") cannot be bound to Ejb(deployment-id=" + beanInfo.ejbDeploymentId + ").  Name already taken by Ejb(deployment-id=" + deployment.getDeploymentID() + ")");
            } else {
               logger.error("Jndi(name=" + externalName + ") cannot be bound to Ejb(deployment-id=" + beanInfo.ejbDeploymentId + ").  Name already taken by another object in the system.");
            }

            if (this.failOnCollision) {
               throw new NameAlreadyBoundException(externalName);
            }
         }
      }

   }

   private String computeGlobalName(BeanContext cdi, Class<?> intrface) {
      ModuleContext module = cdi.getModuleContext();
      AppContext application = module.getAppContext();
      String appName = application.isStandaloneModule() ? "" : application.getId() + "/";
      String moduleName = this.moduleName(cdi);
      String beanName = cdi.getEjbName();
      if (intrface != null) {
         beanName = beanName + "!" + intrface.getName();
      }

      return "global/" + appName + moduleName + beanName;
   }

   private String moduleName(BeanContext cdi) {
      String moduleName = cdi.getModuleName() + "/";
      if (moduleName.startsWith("ear-scoped-cdi-beans_")) {
         moduleName = moduleName.substring("ear-scoped-cdi-beans_".length());
      }

      return moduleName;
   }

   private void bindJava(BeanContext cdi, Class intrface, Reference ref, Bindings bindings, EnterpriseBeanInfo beanInfo) throws NamingException {
      ModuleContext module = cdi.getModuleContext();
      AppContext application = module.getAppContext();
      Context moduleContext = module.getModuleJndiContext();
      Context appContext = application.getAppJndiContext();
      Context globalContext = application.getGlobalJndiContext();
      String appName = application.isStandaloneModule() ? "" : application.getId() + "/";
      String moduleName = this.moduleName(cdi);
      if (moduleName.startsWith("/")) {
         moduleName = moduleName.substring(1);
      }

      String beanName = cdi.getEjbName();
      if (intrface != null) {
         beanName = beanName + "!" + intrface.getName();
      }

      String globalName = "global/" + appName + moduleName + beanName;

      try {
         if (this.embeddedEjbContainerApi && (!(beanInfo instanceof ManagedBeanInfo) || !((ManagedBeanInfo)beanInfo).hidden)) {
            logger.info(String.format("Jndi(name=\"java:%s\")", globalName));
         }

         globalContext.bind(globalName, ref);
         application.getBindings().put(globalName, ref);
         this.bind("openejb/global/" + globalName, ref, bindings, beanInfo, intrface);
      } catch (NameAlreadyBoundException var18) {
         return;
      }

      appContext.bind("app/" + moduleName + beanName, ref);
      application.getBindings().put("app/" + moduleName + beanName, ref);
      String moduleJndi = "module/" + beanName;
      moduleContext.bind(moduleJndi, ref);
      ContextualEjbLookup contextual = (ContextualEjbLookup)ContextualEjbLookup.class.cast(application.getBindings().get(moduleJndi));
      if (contextual == null) {
         Map<BeanContext, Object> potentials = new HashMap();
         contextual = new ContextualEjbLookup(potentials, ref);
         application.getBindings().put(moduleJndi, contextual);
      }

      contextual.potentials.put(cdi, ref);
   }

   private BeanContext findNameOwner(String name) {
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);

      for(BeanContext beanContext : containerSystem.deployments()) {
         Bindings bindings = (Bindings)beanContext.get(Bindings.class);
         if (bindings != null && bindings.getBindings().contains(name)) {
            return beanContext;
         }
      }

      return null;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, JndiBuilder.class.getPackage().getName());
      USE_OLD_JNDI_NAMES = SystemInstance.get().getOptions().get("openejb.use-old-jndi-names", false);
   }

   public static class TemplatedStrategy implements JndiNameStrategy {
      private static final String JNDINAME_FORMAT = "openejb.jndiname.format";
      private static final String KEYS = "default,local,global,app";
      private final StringTemplate template;
      private final HashMap<String, EnterpriseBeanInfo> beanInfos;
      private BeanContext bean;
      private HashMap<String, Map<String, StringTemplate>> templates;
      private String format;
      private Map<String, String> appContext;
      private HashMap<String, String> beanContext;

      public TemplatedStrategy(EjbJarInfo ejbJarInfo, Map<String, BeanContext> deployments) {
         Options options = new Options(ejbJarInfo.properties, SystemInstance.get().getOptions());
         this.format = options.get("openejb.jndiname.format", "{deploymentId}{interfaceType.annotationName}");
         int index = this.format.indexOf(":");
         if (index > -1) {
            JndiBuilder.logger.error("Illegal openejb.jndiname.format contains a colon ':'.  Everything before the colon will be removed, '" + this.format + "' ");
            this.format = this.format.substring(index + 1);
         }

         this.template = new StringTemplate(this.format);
         this.beanInfos = new HashMap();

         for(EnterpriseBeanInfo beanInfo : ejbJarInfo.enterpriseBeans) {
            this.beanInfos.put(beanInfo.ejbDeploymentId, beanInfo);
         }

         Iterator<BeanContext> it = deployments.values().iterator();
         if (it.hasNext()) {
            ModuleContext moduleContext = ((BeanContext)it.next()).getModuleContext();
            this.appContext = new HashMap();
            this.putAll(this.appContext, SystemInstance.get().getProperties());
            this.putAll(this.appContext, moduleContext.getAppContext().getProperties());
            this.putAll(this.appContext, moduleContext.getProperties());
            this.appContext.put("appName", moduleContext.getAppContext().getId());
            this.appContext.put("appId", moduleContext.getAppContext().getId());
            this.appContext.put("moduleName", moduleContext.getId());
            this.appContext.put("moduleId", moduleContext.getId());
         }
      }

      private void putAll(Map<String, String> map, Properties properties) {
         for(Map.Entry<Object, Object> e : properties.entrySet()) {
            if (e.getValue() instanceof String && e.getKey() instanceof String) {
               map.put((String)e.getKey(), (String)e.getValue());
            }
         }

      }

      private Map<String, StringTemplate> addTemplate(Map<String, StringTemplate> map, String key, StringTemplate template) {
         Map<String, StringTemplate> m = map;
         if (map == null) {
            m = new TreeMap();
         }

         m.put(key, template);
         return m;
      }

      public void begin(BeanContext bean) {
         this.bean = bean;
         EnterpriseBeanInfo beanInfo = (EnterpriseBeanInfo)this.beanInfos.get(bean.getDeploymentID());
         this.templates = new HashMap();
         this.templates.put("", this.addTemplate((Map)null, "default", this.template));

         for(JndiNameInfo nameInfo : beanInfo.jndiNamess) {
            String intrface = nameInfo.intrface;
            if (intrface == null) {
               intrface = "";
            }

            this.templates.put(intrface, this.addTemplate((Map)this.templates.get(intrface), getType(nameInfo.name), new StringTemplate(nameInfo.name)));
         }

         beanInfo.jndiNames.clear();
         beanInfo.jndiNamess.clear();
         this.beanContext = new HashMap(this.appContext);
         this.putAll(this.beanContext, bean.getProperties());
         this.beanContext.put("ejbType", bean.getComponentType().name());
         this.beanContext.put("ejbClass", bean.getBeanClass().getName());
         this.beanContext.put("ejbClass.simpleName", bean.getBeanClass().getSimpleName());
         this.beanContext.put("ejbClass.packageName", Classes.packageName(bean.getBeanClass()));
         this.beanContext.put("ejbName", bean.getEjbName());
         this.beanContext.put("deploymentId", bean.getDeploymentID().toString());
      }

      private static String getType(String name) {
         int start = 0;
         if (name.charAt(0) == '/') {
            start = 1;
         }

         int end = name.substring(start).indexOf(47);
         return end < 0 ? "default" : name.substring(start, end);
      }

      public void end() {
      }

      public String getName(Class interfce, String key, JndiNameStrategy.Interface type) {
         Map<String, StringTemplate> template = (Map)this.templates.get(interfce.getName());
         if (template == null) {
            template = (Map)this.templates.get(type.getAnnotationName());
         }

         if (template == null) {
            template = (Map)this.templates.get("");
         }

         Map<String, String> contextData = new HashMap(this.beanContext);
         contextData.put("interfaceType", type.getAnnotationName());
         contextData.put("interfaceType.annotationName", type.getAnnotationName());
         contextData.put("interfaceType.annotationNameLC", type.getAnnotationName().toLowerCase());
         contextData.put("interfaceType.xmlName", type.getXmlName());
         contextData.put("interfaceType.xmlNameCc", type.getXmlNameCc());
         contextData.put("interfaceType.openejbLegacyName", type.getOpenejbLegacy());
         contextData.put("interfaceClass", interfce.getName());
         contextData.put("interfaceClass.simpleName", interfce.getSimpleName());
         contextData.put("interfaceClass.packageName", Classes.packageName(interfce));
         StringTemplate stringTemplate = null;
         if (template.containsKey(key)) {
            stringTemplate = (StringTemplate)template.get(key);
         } else {
            stringTemplate = (StringTemplate)template.get("default");
         }

         if (stringTemplate == null) {
            stringTemplate = (StringTemplate)template.values().iterator().next();
         }

         return stringTemplate.apply(contextData);
      }

      public Map<String, String> getNames(Class interfce, JndiNameStrategy.Interface type) {
         Map<String, String> names = new HashMap();

         for(String key : "default,local,global,app".split(",")) {
            names.put(key, this.getName(interfce, key, type));
         }

         return names;
      }
   }

   public static class LegacyAddedSuffixStrategy implements JndiNameStrategy {
      private BeanContext beanContext;

      public void begin(BeanContext beanContext) {
         this.beanContext = beanContext;
      }

      public void end() {
      }

      public String getName(Class interfce, String key, JndiNameStrategy.Interface type) {
         String id = String.valueOf(this.beanContext.getDeploymentID());
         if (id.charAt(0) == '/') {
            id = id.substring(1);
         }

         switch (type) {
            case REMOTE_HOME:
               return id;
            case LOCAL_HOME:
               return id + "Local";
            case BUSINESS_LOCAL:
               return id + "BusinessLocal";
            case BUSINESS_REMOTE:
               return id + "BusinessRemote";
            default:
               return id;
         }
      }

      public Map<String, String> getNames(Class interfce, JndiNameStrategy.Interface type) {
         Map<String, String> names = new HashMap();
         names.put("", this.getName(interfce, "default", type));
         return names;
      }
   }

   protected static final class Bindings {
      private final List<String> bindings = new ArrayList();

      public List<String> getBindings() {
         return this.bindings;
      }

      public boolean add(String o) {
         return this.bindings.add(o);
      }

      public boolean contains(String o) {
         return this.bindings.contains(o);
      }
   }

   public static class RemoteInterfaceComparator implements Comparator<Class> {
      public int compare(Class a, Class b) {
         boolean aIsRmote = Remote.class.isAssignableFrom(a);
         boolean bIsRmote = Remote.class.isAssignableFrom(b);
         if (aIsRmote == bIsRmote) {
            return 0;
         } else {
            return aIsRmote ? 1 : -1;
         }
      }
   }

   public static class ContextualEjbLookup extends org.apache.openejb.core.ivm.naming.Reference {
      private final Map<BeanContext, Object> potentials;
      private final Object defaultValue;

      public ContextualEjbLookup(Map<BeanContext, Object> potentials, Object defaultValue) {
         this.potentials = potentials;
         this.defaultValue = defaultValue;
      }

      public Object getObject() throws NamingException {
         if (this.potentials.size() == 1) {
            return this.unwrap(this.defaultValue);
         } else {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
               for(Map.Entry<BeanContext, Object> o : this.potentials.entrySet()) {
                  if (loader.equals(((BeanContext)o.getKey()).getClassLoader())) {
                     return this.unwrap(o.getValue());
                  }
               }
            }

            return this.unwrap(this.defaultValue);
         }
      }

      private Object unwrap(Object value) throws NamingException {
         return org.apache.openejb.core.ivm.naming.Reference.class.isInstance(value) ? ((org.apache.openejb.core.ivm.naming.Reference)org.apache.openejb.core.ivm.naming.Reference.class.cast(value)).getObject() : value;
      }
   }

   public interface JndiNameStrategy {
      void begin(BeanContext var1);

      String getName(Class var1, String var2, Interface var3);

      Map<String, String> getNames(Class var1, Interface var2);

      void end();

      public static enum Interface {
         REMOTE_HOME(InterfaceType.EJB_HOME, "RemoteHome", "home", ""),
         LOCAL_HOME(InterfaceType.EJB_LOCAL_HOME, "LocalHome", "local-home", "Local"),
         BUSINESS_LOCAL(InterfaceType.BUSINESS_LOCAL, "Local", "business-local", "BusinessLocal"),
         LOCALBEAN(InterfaceType.LOCALBEAN, "LocalBean", "localbean", "LocalBean"),
         BUSINESS_REMOTE(InterfaceType.BUSINESS_REMOTE, "Remote", "business-remote", "BusinessRemote"),
         SERVICE_ENDPOINT(InterfaceType.SERVICE_ENDPOINT, "Endpoint", "service-endpoint", "ServiceEndpoint");

         private final InterfaceType type;
         private final String annotatedName;
         private final String xmlName;
         private final String xmlNameCc;
         private final String openejbLegacy;

         private Interface(InterfaceType type, String annotatedName, String xmlName, String openejbLegacy) {
            this.type = type;
            this.annotatedName = annotatedName;
            this.xmlName = xmlName;
            this.xmlNameCc = Strings.camelCase(xmlName);
            this.openejbLegacy = openejbLegacy;
         }

         public InterfaceType getType() {
            return this.type;
         }

         public String getAnnotationName() {
            return this.annotatedName;
         }

         public String getXmlName() {
            return this.xmlName;
         }

         public String getXmlNameCc() {
            return this.xmlNameCc;
         }

         public String getOpenejbLegacy() {
            return this.openejbLegacy;
         }
      }
   }
}
