package org.apache.openejb.resource.jdbc;

import java.io.Flushable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.sql.CommonDataSource;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.apache.openejb.cipher.PasswordCipherException;
import org.apache.openejb.cipher.PasswordCipherFactory;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.XAResourceWrapper;
import org.apache.openejb.resource.jdbc.dbcp.DbcpDataSourceCreator;
import org.apache.openejb.resource.jdbc.driver.AlternativeDriver;
import org.apache.openejb.resource.jdbc.logging.LoggingSqlDataSource;
import org.apache.openejb.resource.jdbc.plugin.AbstractDataSourcePlugin;
import org.apache.openejb.resource.jdbc.plugin.DataSourcePlugin;
import org.apache.openejb.resource.jdbc.pool.DataSourceCreator;
import org.apache.openejb.resource.jdbc.pool.DefaultDataSourceCreator;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.SuperProperties;
import org.apache.xbean.recipe.ExecutionContext;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;
import org.apache.xbean.recipe.Recipe;

public class DataSourceFactory {
   private static final Logger LOGGER;
   public static final String LOG_SQL_PROPERTY = "LogSql";
   public static final String LOG_SQL_PACKAGE_PROPERTY = "LogSqlPackages";
   public static final String FLUSHABLE_PROPERTY = "Flushable";
   public static final String RESET_PROPERTY = "ResetOnError";
   public static final String RESET_METHODS_PROPERTY = "ResetOnErrorMethods";
   public static final String GLOBAL_LOG_SQL_PROPERTY = "openejb.jdbc.log";
   public static final String GLOBAL_LOG_SQL_PACKAGE_PROPERTY = "openejb.jdbc.log.packages";
   public static final String GLOBAL_FLUSH_PROPERTY = "openejb.jdbc.flushable";
   public static final String POOL_PROPERTY = "openejb.datasource.pool";
   public static final String DATA_SOURCE_CREATOR_PROP = "DataSourceCreator";
   public static final String XA_GLOBAL_FORCE_DIFFERENT = "openejb.datasource.xa.force-different-xaresource";
   public static final String XA_FORCE_DIFFERENT = "XAForceDifferent";
   public static final String HANDLER_PROPERTY = "TomEEProxyHandler";
   public static final String GLOBAL_HANDLER_PROPERTY = "openejb.jdbc.handler";
   private static final Map<CommonDataSource, AlternativeDriver> driverByDataSource;
   private static final Map<CommonDataSource, DataSourceCreator> creatorByDataSource;
   private static final Map<String, String> KNOWN_CREATORS;

   public static CommonDataSource create(String name, boolean configuredManaged, Class impl, String definition, Duration maxWaitTime, Duration timeBetweenEvictionRuns, Duration minEvictableIdleTime, boolean useAlternativeDriver) throws IllegalAccessException, InstantiationException, IOException {
      Properties properties = asProperties(definition);
      Set<String> originalKeys = properties.stringPropertyNames();
      String handler = SystemInstance.get().getOptions().get("openejb.jdbc.handler", (String)properties.remove("TomEEProxyHandler"));
      boolean flushable = SystemInstance.get().getOptions().get("openejb.jdbc.flushable", "true".equalsIgnoreCase((String)properties.remove("Flushable")));
      String forceDifferent = SystemInstance.get().getOptions().get("openejb.datasource.xa.force-different-xaresource", (String)String.class.cast(properties.remove("XAForceDifferent")));
      convert(properties, maxWaitTime, "maxWaitTime", "maxWait");
      convert(properties, timeBetweenEvictionRuns, "timeBetweenEvictionRuns", "timeBetweenEvictionRunsMillis");
      convert(properties, minEvictableIdleTime, "minEvictableIdleTime", "minEvictableIdleTimeMillis");
      properties.remove("Definition");
      properties.remove("JtaManaged");
      properties.remove("ServiceId");
      boolean managed = configuredManaged;
      if (properties.containsKey("transactional")) {
         managed = Boolean.parseBoolean((String)properties.remove("transactional")) || configuredManaged;
      }

      normalizeJdbcUrl(properties);
      String jdbcUrl = properties.getProperty("JdbcUrl");
      AlternativeDriver driver;
      if (Driver.class.isAssignableFrom(impl) && jdbcUrl != null && useAlternativeDriver) {
         try {
            driver = new AlternativeDriver((Driver)impl.newInstance(), jdbcUrl);
            driver.register();
         } catch (SQLException e) {
            throw new IllegalStateException(e);
         }
      } else {
         driver = null;
      }

      boolean logSql = SystemInstance.get().getOptions().get("openejb.jdbc.log", "true".equalsIgnoreCase((String)properties.remove("LogSql")));
      String logPackages = SystemInstance.get().getProperty("openejb.jdbc.log.packages", (String)properties.remove("LogSqlPackages"));
      DataSourceCreator creator = creator(properties.remove("DataSourceCreator"), logSql);
      String resetOnError = (String)properties.remove("ResetOnError");
      String resetMethods = (String)properties.remove("ResetOnErrorMethods");
      boolean useContainerLoader = "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.resources.use-container-loader", "true")) && impl.getClassLoader() == DataSourceFactory.class.getClassLoader();
      ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
      if (useContainerLoader) {
         ClassLoader containerLoader = DataSourceFactory.class.getClassLoader();
         Thread.currentThread().setContextClassLoader(containerLoader);

         try {
            useContainerLoader = basicChecksThatDataSourceCanBeCreatedFromContainerLoader(properties, containerLoader);
         } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
         }

         if (useContainerLoader) {
            Thread.currentThread().setContextClassLoader(containerLoader);
         } else {
            LOGGER.info("Can't use container loader to create datasource " + name + " so using application one");
         }
      }

      CommonDataSource dataSource;
      try {
         CommonDataSource ds;
         if (createDataSourceFromClass(impl)) {
            trimNotSupportedDataSourceProperties(properties);
            ObjectRecipe recipe = new ObjectRecipe(impl);
            recipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
            recipe.allow(Option.IGNORE_MISSING_PROPERTIES);
            recipe.allow(Option.NAMED_PARAMETERS);
            recipe.allow(Option.PRIVATE_PROPERTIES);
            recipe.setAllProperties(properties);
            if (!properties.containsKey("url") && properties.containsKey("JdbcUrl")) {
               recipe.setProperty("url", properties.getProperty("JdbcUrl"));
            }

            dataSource = (CommonDataSource)recipe.create();
            boolean isDs = DataSource.class.isInstance(dataSource);
            if (!isDs && XADataSource.class.isInstance(dataSource) && forceDifferent != null) {
               try {
                  dataSource = (CommonDataSource)CommonDataSource.class.cast(Thread.currentThread().getContextClassLoader().loadClass("true".equals(forceDifferent) ? "org.apache.openejb.resource.jdbc.xa.IsDifferentXaDataSourceWrapper" : forceDifferent).getConstructor(XADataSource.class).newInstance(dataSource));
               } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                  throw new IllegalArgumentException(e);
               }
            }

            if (managed) {
               if (isDs && usePool(properties)) {
                  ds = creator.poolManaged(name, (DataSource)DataSource.class.cast(dataSource), properties);
               } else {
                  ds = creator.managed(name, dataSource);
               }
            } else if (isDs && usePool(properties)) {
               ds = creator.pool(name, (DataSource)DataSource.class.cast(dataSource), properties);
            } else {
               ds = dataSource;
            }
         } else if (managed) {
            XAResourceWrapper xaResourceWrapper = (XAResourceWrapper)SystemInstance.get().getComponent(XAResourceWrapper.class);
            if (xaResourceWrapper != null) {
               ds = creator.poolManagedWithRecovery(name, xaResourceWrapper, impl.getName(), properties);
            } else {
               ds = creator.poolManaged(name, impl.getName(), properties);
            }
         } else {
            ds = creator.pool(name, impl.getName(), properties);
         }

         setCreatedWith(creator, ds);
         if (driver != null) {
            driverByDataSource.put(ds, driver);
         }

         boolean doResetOnError = resetOnError != null && !"false".equals(resetOnError);
         Object var47;
         if (!doResetOnError && !logSql && !flushable) {
            var47 = wrapIfNeeded(handler, ds);
         } else {
            ObjectRecipe objectRecipe = null;
            ResettableDataSourceHandler existingResettableHandler = null;
            FlushableDataSourceHandler flushableDataSourceHandler = null;
            if (ExecutionContext.isContextSet()) {
               ExecutionContext context = ExecutionContext.getContext();
               List<Recipe> stack = context.getStack();
               if (stack.size() > 0) {
                  objectRecipe = (ObjectRecipe)ObjectRecipe.class.cast(stack.get(0));
                  existingResettableHandler = (ResettableDataSourceHandler)ResettableDataSourceHandler.class.cast(objectRecipe.getProperty("resettableHandler"));
                  flushableDataSourceHandler = (FlushableDataSourceHandler)FlushableDataSourceHandler.class.cast(objectRecipe.getProperty("flushableHandler"));
                  final Map<String, Object> props = objectRecipe.getProperties();

                  for(String key : originalKeys) {
                     props.remove(key);
                  }

                  props.remove("properties");
                  props.remove("Definition");
                  props.remove("ServiceId");
                  props.remove("resettableHandler");
                  props.remove("flushableHandler");
                  (new ObjectRecipe(ds.getClass()) {
                     {
                        this.allow(Option.CASE_INSENSITIVE_PROPERTIES);
                        this.allow(Option.IGNORE_MISSING_PROPERTIES);
                        this.allow(Option.NAMED_PARAMETERS);
                        this.allow(Option.PRIVATE_PROPERTIES);
                        this.setAllProperties(props);
                     }
                  }).setProperties(ds);
               }
            }

            var47 = wrapIfNeeded(handler, ds);
            if (logSql) {
               var47 = makeItLogging((CommonDataSource)var47, logPackages);
            }

            ResettableDataSourceHandler resettableDataSourceHandler;
            if (doResetOnError) {
               resettableDataSourceHandler = existingResettableHandler != null ? existingResettableHandler : new ResettableDataSourceHandler((CommonDataSource)var47, resetOnError, resetMethods);
            } else {
               resettableDataSourceHandler = null;
            }

            if (flushable || doResetOnError) {
               if (flushableDataSourceHandler == null) {
                  properties.remove("flushable");
                  Map<String, Object> recipeProps = new HashMap((Map)(objectRecipe == null ? new HashMap() : objectRecipe.getProperties()));
                  recipeProps.remove("properties");
                  recipeProps.put("OpenEJBResourceClasspath", String.valueOf(useAlternativeDriver));
                  FlushableDataSourceHandler.FlushConfig flushConfig = new FlushableDataSourceHandler.FlushConfig(recipeProps);
                  flushableDataSourceHandler = new FlushableDataSourceHandler((CommonDataSource)var47, flushConfig, resettableDataSourceHandler);
               } else {
                  flushableDataSourceHandler.updateDataSource((CommonDataSource)var47);
               }

               var47 = makeSerializableFlushableDataSourceProxy((CommonDataSource)var47, flushableDataSourceHandler);
            }

            if (doResetOnError) {
               resettableDataSourceHandler.updateDelegate((CommonDataSource)var47);
               var47 = makeSerializableFlushableDataSourceProxy((CommonDataSource)var47, resettableDataSourceHandler);
            }
         }

         dataSource = (CommonDataSource)var47;
      } finally {
         if (useContainerLoader) {
            Thread.currentThread().setContextClassLoader(oldLoader);
         }

      }

      return dataSource;
   }

   private static CommonDataSource wrapIfNeeded(String handler, CommonDataSource ds) throws InstantiationException, IllegalAccessException {
      if (handler != null) {
         try {
            Class<?> handlerClass = Thread.currentThread().getContextClassLoader().loadClass(handler);

            InvocationHandler instance;
            try {
               instance = (InvocationHandler)InvocationHandler.class.cast(handlerClass.getConstructor(DataSource.class).newInstance(ds));
            } catch (InvocationTargetException e) {
               throw new IllegalStateException(e.getCause());
            } catch (NoSuchMethodException e) {
               try {
                  instance = (InvocationHandler)InvocationHandler.class.cast(handlerClass.getConstructor(CommonDataSource.class).newInstance(ds));
               } catch (InvocationTargetException var6) {
                  throw new IllegalStateException(e.getCause());
               } catch (NoSuchMethodException var7) {
                  instance = (InvocationHandler)InvocationHandler.class.cast(handlerClass.newInstance());
               }
            }

            return wrapWithHandler(ds, instance);
         } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't find handler: " + handler, e);
         }
      } else {
         return ds;
      }
   }

   public static CommonDataSource makeSerializableFlushableDataSourceProxy(CommonDataSource ds, InvocationHandler handler) {
      return (CommonDataSource)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{DataSource.class.isInstance(ds) ? DataSource.class : XADataSource.class, Serializable.class, Flushable.class}, handler);
   }

   private static boolean basicChecksThatDataSourceCanBeCreatedFromContainerLoader(Properties properties, ClassLoader containerLoader) {
      try {
         for(String property : Arrays.asList("JdbcDriver", "driverClassName")) {
            String value = properties.getProperty(property);
            if (value != null) {
               Class.forName(value, false, containerLoader);
            }
         }
      } catch (ClassNotFoundException var6) {
         return false;
      } catch (NoClassDefFoundError var7) {
         return false;
      }

      try {
         String cipher = properties.getProperty("PasswordCipher");
         if (cipher != null && !"PlainText".equals(cipher) && !"Static3DES".equals(cipher)) {
            PasswordCipherFactory.getPasswordCipher(cipher);
         }

         return true;
      } catch (PasswordCipherException var5) {
         return false;
      }
   }

   public static void setCreatedWith(DataSourceCreator creator, CommonDataSource ds) {
      creatorByDataSource.put(ds, creator);
   }

   private static CommonDataSource wrapWithHandler(CommonDataSource ds, InvocationHandler instance) {
      return (CommonDataSource)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{DataSource.class.isInstance(ds) ? DataSource.class : XADataSource.class, Serializable.class}, instance);
   }

   public static DataSource makeItLogging(CommonDataSource ds, String packagesStr) {
      String[] pck = packagesStr == null ? null : packagesStr.split(" *, *");
      return (DataSource)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{DataSource.class, Serializable.class}, new LoggingSqlDataSource(ds, pck));
   }

   private static void normalizeJdbcUrl(Properties properties) {
      String key = "JdbcUrl";
      String jdbcUrl = properties.getProperty("JdbcUrl");
      if (jdbcUrl != null) {
         try {
            DataSourcePlugin helper = BasicDataSourceUtil.getDataSourcePlugin(jdbcUrl);
            if (AbstractDataSourcePlugin.isActive(helper)) {
               String newUrl = helper.updatedUrl(jdbcUrl);
               properties.setProperty("JdbcUrl", newUrl);
            }

         } catch (SQLException e) {
            throw new IllegalStateException(e);
         }
      }
   }

   private static void convert(Properties properties, Duration duration, String key, String oldKey) {
      properties.remove(key);
      if (!properties.contains(oldKey)) {
         properties.remove(oldKey);
         if (duration != null) {
            if (duration.getUnit() == null) {
               duration.setUnit(TimeUnit.MILLISECONDS);
            }

            long milliseconds = TimeUnit.MILLISECONDS.convert(duration.getTime(), duration.getUnit());
            properties.put(oldKey, String.valueOf(milliseconds));
         }
      }
   }

   public static DataSourceCreator creator(Object creatorName, boolean willBeProxied) {
      DataSourceCreator defaultCreator = (DataSourceCreator)SystemInstance.get().getComponent(DataSourceCreator.class);
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      if (creatorName != null && creatorName instanceof String && (defaultCreator == null || !creatorName.equals(defaultCreator.getClass().getName()))) {
         String clazz = (String)KNOWN_CREATORS.get(creatorName);
         if (clazz == null) {
            clazz = (String)creatorName;
         }

         if (willBeProxied && clazz.equals(DefaultDataSourceCreator.class.getName())) {
            clazz = DbcpDataSourceCreator.class.getName();
         }

         try {
            return (DataSourceCreator)loader.loadClass(clazz).newInstance();
         } catch (Throwable e) {
            LOGGER.error("can't create '" + creatorName + "', the default one will be used: " + defaultCreator, e);
         }
      }

      return (DataSourceCreator)(defaultCreator instanceof DefaultDataSourceCreator && willBeProxied ? new DbcpDataSourceCreator() : defaultCreator);
   }

   private static boolean createDataSourceFromClass(Class<?> impl) {
      return isDataSource(impl) && !SystemInstance.get().getOptions().get("org.apache.openejb.resource.jdbc.hot.deploy", false);
   }

   private static boolean isDataSource(Class<?> impl) {
      return DataSource.class.isAssignableFrom(impl) || XADataSource.class.isAssignableFrom(impl) || ConnectionPoolDataSource.class.isAssignableFrom(impl);
   }

   private static boolean usePool(Properties properties) {
      String property = properties.getProperty("openejb.datasource.pool", SystemInstance.get().getProperty("openejb.datasource.pool"));
      if (property != null) {
         properties.remove("openejb.datasource.pool");
      } else {
         String initialPoolSize = properties.getProperty("initialPoolSize");
         String maxPoolSize = properties.getProperty("maxPoolSize");
         if ((null == initialPoolSize || "-1".equals(initialPoolSize)) && ("-1".equals(maxPoolSize) || maxPoolSize == null)) {
            property = "false";
         }
      }

      return "true".equalsIgnoreCase(property) || null == property;
   }

   private static Properties asProperties(String definition) throws IOException {
      SuperProperties properties = new SuperProperties();
      properties.caseInsensitive(true);
      properties.putAll(IO.readProperties(IO.read(definition), new Properties()));
      return properties;
   }

   public static void trimNotSupportedDataSourceProperties(Properties properties) {
      properties.remove("LoginTimeout");
   }

   public static boolean knows(Object object) {
      return object instanceof CommonDataSource && creatorByDataSource.containsKey(CommonDataSource.class.cast(realInstance(object)));
   }

   public static ObjectRecipe forgetRecipe(Object rawObject, ObjectRecipe defaultValue) {
      Object object = realInstance(rawObject);
      DataSourceCreator creator = (DataSourceCreator)creatorByDataSource.get(object);
      ObjectRecipe recipe = null;
      if (creator != null) {
         recipe = creator.clearRecipe(object);
      }

      return recipe == null ? defaultValue : recipe;
   }

   public static void destroy(Object o) throws Throwable {
      Object instance = realInstance(o);
      if (instance != null) {
         DataSourceCreator remove = (DataSourceCreator)creatorByDataSource.remove(instance);
         remove.destroy(instance);
         AlternativeDriver driver = (AlternativeDriver)driverByDataSource.remove(instance);
         if (driver != null) {
            driver.deregister();
         }

      }
   }

   private static Object realInstance(Object o) {
      if (o != null && o instanceof DataSource) {
         Object ds;
         InvocationHandler handler;
         for(ds = o; Proxy.isProxyClass(ds.getClass()); ds = ((DelegatableHandler)DelegatableHandler.class.cast(handler)).getDelegate()) {
            handler = Proxy.getInvocationHandler(ds);
            if (!DelegatableHandler.class.isInstance(handler)) {
               break;
            }
         }

         return ds;
      } else {
         return o;
      }
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, DataSourceFactory.class);
      driverByDataSource = new HashMap();
      creatorByDataSource = new HashMap();
      KNOWN_CREATORS = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {
         {
            this.put("simple", "org.apache.openejb.resource.jdbc.SimpleDataSourceCreator");
            this.put("dbcp", "org.apache.openejb.resource.jdbc.pool.DefaultDataSourceCreator");
            this.put("dbcp-alternative", "org.apache.openejb.resource.jdbc.dbcp.DbcpDataSourceCreator");
            this.put("tomcat", "org.apache.tomee.jdbc.TomEEDataSourceCreator");
            this.put("bonecp", "org.apache.openejb.bonecp.BoneCPDataSourceCreator");
         }
      };
   }
}
