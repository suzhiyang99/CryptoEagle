package org.apache.deltaspike.core.impl.config;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.api.config.Configuration;
import org.apache.deltaspike.core.api.config.Filter;
import org.apache.deltaspike.core.api.config.PropertyFileConfig;
import org.apache.deltaspike.core.api.config.Source;
import org.apache.deltaspike.core.api.exclude.Exclude;
import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.api.literal.DefaultLiteral;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.spi.activation.Deactivatable;
import org.apache.deltaspike.core.spi.config.BaseConfigPropertyProducer;
import org.apache.deltaspike.core.spi.config.ConfigFilter;
import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.apache.deltaspike.core.spi.config.ConfigValidator;
import org.apache.deltaspike.core.util.ClassDeactivationUtils;
import org.apache.deltaspike.core.util.ClassUtils;
import org.apache.deltaspike.core.util.ServiceUtils;
import org.apache.deltaspike.core.util.bean.BeanBuilder;

public class ConfigurationExtension implements Extension, Deactivatable {
   private static final Logger LOG = Logger.getLogger(ConfigurationExtension.class.getName());
   private static final String CANNOT_CREATE_CONFIG_SOURCE_FOR_CUSTOM_PROPERTY_FILE_CONFIG = "Cannot create ConfigSource for custom property-file config ";
   private static Map<ClassLoader, List<Class<? extends PropertyFileConfig>>> detectedParentPropertyFileConfigs = new ConcurrentHashMap();
   private boolean isActivated = true;
   private List<Class<? extends PropertyFileConfig>> propertyFileConfigClasses = new ArrayList();
   private final Set<Type> dynamicConfigTypes = new HashSet();
   private Bean<DynamicBeanProducer> dynamicProducer;
   private final List<Bean<? extends ConfigSource>> cdiSources = new ArrayList();
   private final List<Bean<? extends ConfigFilter>> cdiFilters = new ArrayList();
   private final List<Class<?>> dynamicConfigurationBeanClasses = new ArrayList();

   protected void init(@Observes BeforeBeanDiscovery beforeBeanDiscovery) {
      this.isActivated = ClassDeactivationUtils.isActivated(this.getClass());
   }

   public static void registerConfigMBean() {
      String appName = ConfigResolver.getPropertyValue("deltaspike.application.name");
      if (appName != null && appName.length() > 0) {
         try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ClassLoader tccl = ClassUtils.getClassLoader(ConfigurationExtension.class);
            DeltaSpikeConfigInfo cfgMBean = new DeltaSpikeConfigInfo(tccl);
            ObjectName name = new ObjectName("deltaspike.config." + appName + ":type=DeltaSpikeConfig");
            mBeanServer.registerMBean(cfgMBean, name);
         } catch (InstanceAlreadyExistsException var5) {
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

   }

   public static void unRegisterConfigMBean(String appName) {
      if (appName != null && appName.length() > 0) {
         try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("deltaspike.config." + appName + ":type=DeltaSpikeConfig");
            mBeanServer.unregisterMBean(name);
         } catch (InstanceNotFoundException var3) {
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

   }

   public void collectUserConfigSources(@Observes ProcessAnnotatedType<? extends PropertyFileConfig> pat) {
      if (this.isActivated) {
         Class<? extends PropertyFileConfig> pcsClass = pat.getAnnotatedType().getJavaClass();
         if (!pcsClass.isAnnotation() && !pcsClass.isInterface() && !pcsClass.isSynthetic() && !pcsClass.isArray() && !pcsClass.isEnum()) {
            if (!pat.getAnnotatedType().isAnnotationPresent(Exclude.class)) {
               this.propertyFileConfigClasses.add(pcsClass);
            }
         }
      }
   }

   public void findDynamicConfigurationBeans(@Observes ProcessAnnotatedType<?> pat) {
      if (pat.getAnnotatedType().isAnnotationPresent(Configuration.class)) {
         Class<?> javaClass = pat.getAnnotatedType().getJavaClass();
         if (javaClass.isInterface()) {
            this.dynamicConfigurationBeanClasses.add(javaClass);
         }
      }
   }

   public void findSources(@Observes ProcessBean<? extends ConfigSource> source) {
      if (source.getAnnotated().isAnnotationPresent(Source.class)) {
         this.cdiSources.add(source.getBean());
      }
   }

   public void findFilters(@Observes ProcessBean<? extends ConfigFilter> filter) {
      if (filter.getAnnotated().isAnnotationPresent(Filter.class)) {
         this.cdiFilters.add(filter.getBean());
      }
   }

   public void findDynamicProducer(@Observes ProcessBean<DynamicBeanProducer> processBean) {
      this.dynamicProducer = processBean.getBean();
   }

   public void collectDynamicTypes(@Observes ProcessBean<?> processBean) {
      for(InjectionPoint ip : processBean.getBean().getInjectionPoints()) {
         ConfigProperty annotation = (ConfigProperty)ip.getAnnotated().getAnnotation(ConfigProperty.class);
         if (annotation != null && annotation.converter() != ConfigResolver.Converter.class) {
            this.dynamicConfigTypes.add(ip.getType());
         }
      }

   }

   public void addDynamicBeans(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager bm) {
      if (this.dynamicProducer != null && !this.dynamicConfigTypes.isEmpty()) {
         afterBeanDiscovery.addBean(new DynamicBean(this.dynamicProducer, this.dynamicConfigTypes));
      }

      for(Class<?> proxyType : this.dynamicConfigurationBeanClasses) {
         afterBeanDiscovery.addBean((new BeanBuilder((BeanManager)null)).types(new Type[]{proxyType, Object.class}).qualifiers(new Annotation[]{new DefaultLiteral(), new AnyLiteral()}).beanLifecycle(new ProxyConfigurationLifecycle(proxyType)).scope(ApplicationScoped.class).passivationCapable(true).id("DeltaSpikeConfiguration#" + proxyType.getName()).beanClass(proxyType).create());
      }

   }

   public void registerUserConfigSources(@Observes AfterDeploymentValidation adv) {
      if (this.isActivated) {
         Set<Class<? extends PropertyFileConfig>> allPropertyFileConfigClasses = new HashSet(this.propertyFileConfigClasses);
         ClassLoader currentClassLoader = ClassUtils.getClassLoader((Object)null);
         this.addParentPropertyFileConfigs(currentClassLoader, allPropertyFileConfigClasses);
         if (!this.propertyFileConfigClasses.isEmpty()) {
            detectedParentPropertyFileConfigs.put(currentClassLoader, this.propertyFileConfigClasses);
         }

         List<ConfigSource> configSources = new ArrayList();

         for(Class<? extends PropertyFileConfig> propertyFileConfigClass : allPropertyFileConfigClasses) {
            configSources.addAll(this.createPropertyConfigSource(propertyFileConfigClass));
         }

         for(Bean bean : this.cdiSources) {
            configSources.add(BeanProvider.getContextualReference(ConfigSource.class, bean));
         }

         ConfigResolver.addConfigSources(configSources);

         for(Bean bean : this.cdiFilters) {
            ConfigResolver.addConfigFilter((ConfigFilter)BeanProvider.getContextualReference(ConfigFilter.class, bean));
         }

         this.processConfigurationValidation(adv);
         registerConfigMBean();
         this.logConfiguration();
      }
   }

   private void logConfiguration() {
      Boolean logConfig = (Boolean)ConfigResolver.resolve("deltaspike.config.log").as(Boolean.class).getValue();
      if (logConfig != null && logConfig && LOG.isLoggable(Level.INFO)) {
         StringBuilder sb = new StringBuilder(65536);
         sb.append("ConfigSources: ");
         ConfigSource[] configSources = ConfigResolver.getConfigSources();

         for(ConfigSource configSource : configSources) {
            sb.append("\n\t").append(configSource.getOrdinal()).append(" - ").append(configSource.getConfigName());
         }

         Map<String, String> allProperties = ConfigResolver.getAllProperties();
         sb.append("\n\nConfigured Values:");

         for(Map.Entry<String, String> entry : allProperties.entrySet()) {
            sb.append("\n\t").append((String)entry.getKey()).append(" = ").append(ConfigResolver.filterConfigValueForLog((String)entry.getKey(), (String)entry.getValue()));
         }

         LOG.info(sb.toString());
      }

   }

   private void addParentPropertyFileConfigs(ClassLoader currentClassLoader, Set<Class<? extends PropertyFileConfig>> propertyFileConfigClasses) {
      if (currentClassLoader.getParent() != null) {
         for(Map.Entry<ClassLoader, List<Class<? extends PropertyFileConfig>>> classLoaderListEntry : detectedParentPropertyFileConfigs.entrySet()) {
            if (currentClassLoader.getParent().equals(classLoaderListEntry.getKey())) {
               propertyFileConfigClasses.addAll((Collection)classLoaderListEntry.getValue());
               this.addParentPropertyFileConfigs((ClassLoader)classLoaderListEntry.getKey(), propertyFileConfigClasses);
               return;
            }
         }

      }
   }

   public void freeConfigSources(@Observes BeforeShutdown bs) {
      String appName = ConfigResolver.getPropertyValue("deltaspike.application.name");
      unRegisterConfigMBean(appName);
      ConfigResolver.freeConfigSources();
      detectedParentPropertyFileConfigs.remove(ClassUtils.getClassLoader((Object)null));
   }

   private List<ConfigSource> createPropertyConfigSource(Class<? extends PropertyFileConfig> propertyFileConfigClass) {
      String fileName = "";

      try {
         PropertyFileConfig propertyFileConfig = (PropertyFileConfig)propertyFileConfigClass.newInstance();
         fileName = propertyFileConfig.getPropertyFileName();
         EnvironmentPropertyConfigSourceProvider environmentPropertyConfigSourceProvider = new EnvironmentPropertyConfigSourceProvider(fileName, propertyFileConfig.isOptional());
         return environmentPropertyConfigSourceProvider.getConfigSources();
      } catch (InstantiationException e) {
         throw new RuntimeException("Cannot create ConfigSource for custom property-file config " + propertyFileConfigClass.getName(), e);
      } catch (IllegalAccessException e) {
         throw new RuntimeException("Cannot create ConfigSource for custom property-file config " + propertyFileConfigClass.getName(), e);
      } catch (IllegalStateException e) {
         throw new IllegalStateException(propertyFileConfigClass.getName() + " points to an invalid file: '" + fileName + "'", e);
      }
   }

   protected void processConfigurationValidation(AfterDeploymentValidation adv) {
      for(ConfigValidator configValidator : ServiceUtils.loadServiceImplementations(ConfigValidator.class)) {
         Set<String> violations = configValidator.processValidation();
         if (violations != null) {
            for(String violation : violations) {
               adv.addDeploymentProblem(new IllegalStateException(violation));
            }
         }
      }

   }

   @ApplicationScoped
   @Typed({DynamicBeanProducer.class})
   static class DynamicBeanProducer extends BaseConfigPropertyProducer {
      @Produces
      @ConfigProperty(
         name = "ignored"
      )
      public Object create(InjectionPoint ip) {
         return super.getUntypedPropertyValue(ip, ip.getType());
      }
   }

   @Typed
   private static final class DynamicBean<T> implements Bean<T> {
      private final Bean<T> producer;
      private final Set<Type> types;

      private DynamicBean(Bean<T> producer, Set<Type> types) {
         this.producer = producer;
         this.types = types;
      }

      public Set<Type> getTypes() {
         return this.types;
      }

      public Set<Annotation> getQualifiers() {
         return this.producer.getQualifiers();
      }

      public Class<? extends Annotation> getScope() {
         return this.producer.getScope();
      }

      public String getName() {
         return this.producer.getName();
      }

      public boolean isNullable() {
         return this.producer.isNullable();
      }

      public Set<InjectionPoint> getInjectionPoints() {
         return this.producer.getInjectionPoints();
      }

      public Class<?> getBeanClass() {
         return this.producer.getBeanClass();
      }

      public Set<Class<? extends Annotation>> getStereotypes() {
         return this.producer.getStereotypes();
      }

      public boolean isAlternative() {
         return this.producer.isAlternative();
      }

      public T create(CreationalContext<T> creationalContext) {
         return (T)this.producer.create(creationalContext);
      }

      public void destroy(T t, CreationalContext<T> creationalContext) {
         this.producer.destroy(t, creationalContext);
      }
   }
}
