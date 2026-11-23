package org.apache.openejb.assembler.classic;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.NamingException;
import javax.validation.BootstrapConfiguration;
import javax.validation.Configuration;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableType;
import org.apache.openejb.bval.ValidatorUtil;
import org.apache.openejb.jee.bval.DefaultValidatedExecutableTypesType;
import org.apache.openejb.jee.bval.ExecutableValidationType;
import org.apache.openejb.jee.bval.PropertyType;
import org.apache.openejb.jee.bval.ValidationConfigType;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.AppFinder;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.BeanManagerImpl;

public final class ValidatorBuilder {
   public static final Logger logger;
   public static final String VALIDATION_PROVIDER_KEY = "openejb.bean-validation.provider";

   private ValidatorBuilder() {
   }

   public static ValidatorFactory buildFactory(ClassLoader classLoader, ValidationInfo info) {
      return buildFactory(info, classLoader);
   }

   public static ValidationInfo getInfo(ValidationConfigType config) {
      ValidationInfo info = new ValidationInfo();
      if (config != null) {
         info.version = config.getVersion();
         info.providerClassName = config.getDefaultProvider();
         info.constraintFactoryClass = config.getConstraintValidatorFactory();
         info.traversableResolverClass = config.getTraversableResolver();
         info.messageInterpolatorClass = config.getMessageInterpolator();
         info.parameterNameProviderClass = config.getParameterNameProvider();
         ExecutableValidationType executableValidation = config.getExecutableValidation();
         if (executableValidation != null) {
            info.executableValidationEnabled = executableValidation.getEnabled();
            DefaultValidatedExecutableTypesType executableTypes = executableValidation.getDefaultValidatedExecutableTypes();
            if (executableTypes != null) {
               for(ExecutableType type : executableTypes.getExecutableType()) {
                  info.validatedTypes.add(type.name());
               }
            }
         }

         for(PropertyType p : config.getProperty()) {
            info.propertyTypes.put(p.getName(), p.getValue());
         }

         for(String element : config.getConstraintMapping()) {
            info.constraintMappings.add(element);
         }
      }

      return info;
   }

   public static ValidatorFactory buildFactory(ValidationInfo config, ClassLoader classLoader) {
      ValidatorFactory factory = null;
      Thread thread = Thread.currentThread();
      ClassLoader oldContextLoader = thread.getContextClassLoader();

      try {
         thread.setContextClassLoader(classLoader);
         if (config == null) {
            factory = Validation.buildDefaultValidatorFactory();
         } else {
            Configuration<?> configuration = getConfig(config);

            try {
               factory = configuration.buildValidatorFactory();
            } catch (ValidationException ve) {
               thread.setContextClassLoader(ValidatorBuilder.class.getClassLoader());
               factory = Validation.buildDefaultValidatorFactory();
               thread.setContextClassLoader(classLoader);
               logger.warning("Unable create validator factory with config " + config + " (" + ve.getMessage() + "). Default factory will be used.");
            }
         }
      } finally {
         thread.setContextClassLoader(oldContextLoader);
      }

      return factory;
   }

   private static Configuration<?> getConfig(ValidationInfo info) {
      Configuration<?> target = null;
      Thread thread = Thread.currentThread();
      ClassLoader classLoader = thread.getContextClassLoader();
      String providerClassName = info.providerClassName;
      if (providerClassName == null) {
         providerClassName = SystemInstance.get().getOptions().get("openejb.bean-validation.provider", (String)null);
      }

      if (providerClassName != null) {
         try {
            Class clazz = classLoader.loadClass(providerClassName);
            target = Validation.byProvider(clazz).configure();
            logger.info("Using " + providerClassName + " as validation provider.");
         } catch (ClassNotFoundException e) {
            logger.warning("Unable to load provider class " + providerClassName, (Throwable)e);
         } catch (ValidationException ve) {
            logger.warning("Unable create validator factory with provider " + providerClassName + " (" + ve.getMessage() + "). Default one will be used.");
         }
      }

      if (target == null) {
         thread.setContextClassLoader(ValidatorBuilder.class.getClassLoader());
         target = Validation.byDefaultProvider().configure();
         thread.setContextClassLoader(classLoader);
      }

      Set<ExecutableType> types = new HashSet();

      for(String type : info.validatedTypes) {
         types.add(ExecutableType.valueOf(type));
      }

      Map<String, String> props = new HashMap();

      for(Map.Entry<Object, Object> entry : info.propertyTypes.entrySet()) {
         PropertyType property = new PropertyType();
         property.setName((String)entry.getKey());
         property.setValue((String)entry.getValue());
         props.put(property.getName(), property.getValue());
         if (logger.isDebugEnabled()) {
            logger.debug("Found property '" + property.getName() + "' with value '" + property.getValue());
         }

         target.addProperty(property.getName(), property.getValue());
      }

      OpenEjbBootstrapConfig bootstrapConfig = new OpenEjbBootstrapConfig(providerClassName, info.constraintFactoryClass, info.messageInterpolatorClass, info.traversableResolverClass, info.parameterNameProviderClass, new HashSet(info.constraintMappings), info.executableValidationEnabled, types, props);
      OpenEjbConfig config = new OpenEjbConfig(bootstrapConfig, target);
      target.ignoreXmlConfiguration();
      String messageInterpolatorClass = info.messageInterpolatorClass;
      if (messageInterpolatorClass != null) {
         try {
            Class<MessageInterpolator> clazz = classLoader.loadClass(messageInterpolatorClass);
            target.messageInterpolator((MessageInterpolator)newInstance(config, clazz));
         } catch (Exception e) {
            logger.warning("Unable to set " + messageInterpolatorClass + " as message interpolator.", (Throwable)e);
         }

         logger.info("Using " + messageInterpolatorClass + " as message interpolator.");
      }

      String traversableResolverClass = info.traversableResolverClass;
      if (traversableResolverClass != null) {
         try {
            Class<TraversableResolver> clazz = classLoader.loadClass(traversableResolverClass);
            target.traversableResolver((TraversableResolver)newInstance(config, clazz));
         } catch (Exception e) {
            logger.warning("Unable to set " + traversableResolverClass + " as traversable resolver.", (Throwable)e);
         }

         logger.info("Using " + traversableResolverClass + " as traversable resolver.");
      }

      String constraintFactoryClass = info.constraintFactoryClass;
      if (constraintFactoryClass != null) {
         try {
            Class<ConstraintValidatorFactory> clazz = classLoader.loadClass(constraintFactoryClass);
            target.constraintValidatorFactory((ConstraintValidatorFactory)newInstance(config, clazz));
         } catch (Exception e) {
            logger.warning("Unable to set " + constraintFactoryClass + " as constraint factory.", (Throwable)e);
         }

         logger.info("Using " + constraintFactoryClass + " as constraint factory.");
      }

      for(String mappingFileName : info.constraintMappings) {
         if (logger.isDebugEnabled()) {
            logger.debug("Opening input stream for " + mappingFileName);
         }

         InputStream in = classLoader.getResourceAsStream(mappingFileName);
         if (in == null) {
            logger.warning("Unable to open input stream for mapping file " + mappingFileName + ". It will be ignored");
         } else {
            target.addMapping(in);
         }
      }

      if (info.parameterNameProviderClass != null) {
         try {
            Class<ParameterNameProvider> clazz = classLoader.loadClass(info.parameterNameProviderClass);
            target.parameterNameProvider((ParameterNameProvider)newInstance(config, clazz));
         } catch (Exception e) {
            logger.warning("Unable to set " + info.parameterNameProviderClass + " as parameter name provider.", (Throwable)e);
         }

         logger.info("Using " + info.parameterNameProviderClass + " as parameter name provider.");
      }

      return config;
   }

   private static <T> T newInstance(OpenEjbConfig config, Class<T> clazz) throws Exception {
      WebBeansContext webBeansContext = (WebBeansContext)AppFinder.findAppContextOrWeb(Thread.currentThread().getContextClassLoader(), AppFinder.WebBeansContextTransformer.INSTANCE);
      if (webBeansContext == null) {
         return (T)clazz.newInstance();
      } else {
         BeanManagerImpl beanManager = webBeansContext.getBeanManagerImpl();
         if (!beanManager.isInUse()) {
            return (T)clazz.newInstance();
         } else {
            AnnotatedType<T> annotatedType = beanManager.createAnnotatedType(clazz);
            InjectionTarget<T> it = beanManager.createInjectionTarget(annotatedType);
            CreationalContext<T> context = beanManager.createCreationalContext((Contextual)null);
            T instance = (T)it.produce(context);
            it.inject(instance, context);
            it.postConstruct(instance);
            config.releasables.add(new Releasable(context, it, instance));
            return instance;
         }
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, ValidatorBuilder.class);
   }

   private static final class OpenEjbBootstrapConfig implements BootstrapConfiguration, Serializable {
      private final String providerClassName;
      private final String constraintFactoryClass;
      private final String messageInterpolatorClass;
      private final String traversableResolverClass;
      private final String parameterNameProviderClass;
      private final Set<String> constraintMappings;
      private final boolean executableValidationEnabled;
      private final Set<ExecutableType> validatedTypes;
      private final Map<String, String> props;

      public OpenEjbBootstrapConfig(String providerClassName, String constraintFactoryClass, String messageInterpolatorClass, String traversableResolverClass, String parameterNameProviderClass, Set<String> constraintMappings, boolean executableValidationEnabled, Set<ExecutableType> validatedTypes, Map<String, String> props) {
         this.providerClassName = providerClassName;
         this.constraintFactoryClass = constraintFactoryClass;
         this.messageInterpolatorClass = messageInterpolatorClass;
         this.traversableResolverClass = traversableResolverClass;
         this.parameterNameProviderClass = parameterNameProviderClass;
         this.constraintMappings = constraintMappings;
         this.executableValidationEnabled = executableValidationEnabled;
         this.validatedTypes = validatedTypes;
         this.props = props;
      }

      public String getDefaultProviderClassName() {
         return this.providerClassName;
      }

      public String getConstraintValidatorFactoryClassName() {
         return this.constraintFactoryClass;
      }

      public String getMessageInterpolatorClassName() {
         return this.messageInterpolatorClass;
      }

      public String getTraversableResolverClassName() {
         return this.traversableResolverClass;
      }

      public String getParameterNameProviderClassName() {
         return this.parameterNameProviderClass;
      }

      public Set<String> getConstraintMappingResourcePaths() {
         return this.constraintMappings;
      }

      public boolean isExecutableValidationEnabled() {
         return this.executableValidationEnabled;
      }

      public Set<ExecutableType> getDefaultValidatedExecutableTypes() {
         return this.validatedTypes;
      }

      public Map<String, String> getProperties() {
         return this.props;
      }
   }

   private static final class OpenEjbConfig<T extends Configuration<T>> implements Configuration<T> {
      private final Collection<Releasable<?>> releasables = new LinkedList();
      private final Configuration<T> delegate;
      private final BootstrapConfiguration bootstrap;

      public OpenEjbConfig(BootstrapConfiguration bootstrapConfig, Configuration<T> target) {
         this.bootstrap = bootstrapConfig;
         this.delegate = target;
      }

      public T ignoreXmlConfiguration() {
         return (T)this.delegate.ignoreXmlConfiguration();
      }

      public T messageInterpolator(MessageInterpolator interpolator) {
         return (T)this.delegate.messageInterpolator(interpolator);
      }

      public T traversableResolver(TraversableResolver resolver) {
         return (T)this.delegate.traversableResolver(resolver);
      }

      public T constraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory) {
         return (T)this.delegate.constraintValidatorFactory(constraintValidatorFactory);
      }

      public T addMapping(InputStream stream) {
         return (T)this.delegate.addMapping(stream);
      }

      public T addProperty(String name, String value) {
         return (T)this.delegate.addProperty(name, value);
      }

      public MessageInterpolator getDefaultMessageInterpolator() {
         return this.delegate.getDefaultMessageInterpolator();
      }

      public TraversableResolver getDefaultTraversableResolver() {
         return this.delegate.getDefaultTraversableResolver();
      }

      public ConstraintValidatorFactory getDefaultConstraintValidatorFactory() {
         return this.delegate.getDefaultConstraintValidatorFactory();
      }

      public ValidatorFactory buildValidatorFactory() {
         return new OpenEJBValidatorFactory(this.delegate.buildValidatorFactory(), this.releasables);
      }

      public T parameterNameProvider(ParameterNameProvider parameterNameProvider) {
         return (T)this.delegate.parameterNameProvider(parameterNameProvider);
      }

      public ParameterNameProvider getDefaultParameterNameProvider() {
         return this.delegate.getDefaultParameterNameProvider();
      }

      public BootstrapConfiguration getBootstrapConfiguration() {
         return this.bootstrap;
      }
   }

   private static final class OpenEJBValidatorFactory implements ValidatorFactory, Serializable {
      private transient ValidatorFactory delegate;
      private transient Collection<Releasable<?>> toRelease;

      public OpenEJBValidatorFactory(ValidatorFactory validatorFactory, Collection<Releasable<?>> releasables) {
         this.delegate = validatorFactory;
         this.toRelease = releasables;
      }

      private ValidatorFactory delegate() {
         if (this.delegate != null) {
            return this.delegate;
         } else {
            try {
               return ValidatorUtil.lookupFactory();
            } catch (NamingException var2) {
               return Validation.buildDefaultValidatorFactory();
            }
         }
      }

      public Validator getValidator() {
         return this.delegate().getValidator();
      }

      public ValidatorContext usingContext() {
         return this.delegate().usingContext();
      }

      public MessageInterpolator getMessageInterpolator() {
         return this.delegate().getMessageInterpolator();
      }

      public TraversableResolver getTraversableResolver() {
         return this.delegate().getTraversableResolver();
      }

      public ConstraintValidatorFactory getConstraintValidatorFactory() {
         return this.delegate().getConstraintValidatorFactory();
      }

      public <T> T unwrap(Class<T> type) {
         return (T)this.delegate().unwrap(type);
      }

      public ParameterNameProvider getParameterNameProvider() {
         return this.delegate().getParameterNameProvider();
      }

      public void close() {
         if (this.delegate != null) {
            this.delegate.close();
         }

         if (this.toRelease != null) {
            for(Releasable<?> r : this.toRelease) {
               r.release();
            }
         }

      }
   }

   private static final class Releasable<T> {
      private final CreationalContext<T> context;
      private final InjectionTarget<T> injectionTarget;
      private final T instance;

      private Releasable(CreationalContext<T> context, InjectionTarget<T> injectionTarget, T instance) {
         this.context = context;
         this.injectionTarget = injectionTarget;
         this.instance = instance;
      }

      private void release() {
         try {
            this.injectionTarget.preDestroy(this.instance);
            this.injectionTarget.dispose(this.instance);
            this.context.release();
         } catch (NoClassDefFoundError | Exception var2) {
         }

      }
   }
}
