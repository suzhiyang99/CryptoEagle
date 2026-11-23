package org.apache.openejb.persistence;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.jdbc.managed.xa.DataSourceXADataSource;
import org.apache.openejb.util.URLs;
import org.apache.openejb.util.classloader.URLClassLoaderFirst;

public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {
   private final PersistenceClassLoaderHandler persistenceClassLoaderHandler;
   private String id;
   private String persistenceUnitName;
   private String persistenceProviderClassName;
   private PersistenceUnitTransactionType transactionType;
   private DataSource jtaDataSource;
   private DataSource nonJtaDataSource;
   private final List<String> mappingFileNames;
   private final List<URL> jarFileUrls;
   private URL persistenceUnitRootUrl;
   private final List<String> managedClassNames;
   private boolean excludeUnlistedClasses;
   private Properties properties;
   private ClassLoader classLoader;
   private String persistenceXMLSchemaVersion;
   private SharedCacheMode sharedCacheMode;
   private ValidationMode validationMode;
   private String jtaDataSourceName;
   private String nonJtaDataSourceName;
   private boolean lazilyInitialized;

   public PersistenceUnitInfoImpl() {
      this.transactionType = PersistenceUnitTransactionType.JTA;
      this.mappingFileNames = new ArrayList();
      this.jarFileUrls = new ArrayList();
      this.managedClassNames = new ArrayList();
      this.sharedCacheMode = SharedCacheMode.UNSPECIFIED;
      this.persistenceClassLoaderHandler = null;
   }

   public PersistenceUnitInfoImpl(PersistenceClassLoaderHandler persistenceClassLoaderHandler) {
      this.transactionType = PersistenceUnitTransactionType.JTA;
      this.mappingFileNames = new ArrayList();
      this.jarFileUrls = new ArrayList();
      this.managedClassNames = new ArrayList();
      this.sharedCacheMode = SharedCacheMode.UNSPECIFIED;
      this.persistenceClassLoaderHandler = persistenceClassLoaderHandler;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getPersistenceUnitName() {
      return this.persistenceUnitName;
   }

   public void setPersistenceUnitName(String persistenceUnitName) {
      this.persistenceUnitName = persistenceUnitName;
   }

   public String getPersistenceProviderClassName() {
      return this.persistenceProviderClassName;
   }

   public void setPersistenceProviderClassName(String persistenceProviderClassName) {
      this.persistenceProviderClassName = persistenceProviderClassName;
   }

   public PersistenceUnitTransactionType getTransactionType() {
      return this.transactionType;
   }

   public void setTransactionType(PersistenceUnitTransactionType transactionType) {
      this.transactionType = transactionType;
   }

   public DataSource getJtaDataSource() {
      return this.jtaDataSource;
   }

   public void setJtaDataSource(CommonDataSource jtaDataSource) {
      if (XADataSource.class.isInstance(jtaDataSource)) {
         this.jtaDataSource = new DataSourceXADataSource(jtaDataSource, OpenEJB.getTransactionManager(), (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class));
      } else {
         this.jtaDataSource = (DataSource)DataSource.class.cast(jtaDataSource);
      }

   }

   public DataSource getNonJtaDataSource() {
      return this.nonJtaDataSource;
   }

   public void setNonJtaDataSource(CommonDataSource nonJtaDataSource) {
      if (XADataSource.class.isInstance(nonJtaDataSource)) {
         this.nonJtaDataSource = new DataSourceXADataSource(nonJtaDataSource, OpenEJB.getTransactionManager(), (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class));
      } else {
         this.nonJtaDataSource = (DataSource)DataSource.class.cast(nonJtaDataSource);
      }

   }

   public List<String> getMappingFileNames() {
      return this.mappingFileNames;
   }

   public void setMappingFileNames(List<String> mappingFileNames) {
      if (mappingFileNames == null) {
         throw new NullPointerException("mappingFileNames is null");
      } else {
         this.mappingFileNames.clear();
         this.mappingFileNames.addAll(mappingFileNames);
      }
   }

   public void addMappingFileName(String mappingFileName) {
      if (mappingFileName == null) {
         throw new NullPointerException("mappingFileName is null");
      } else {
         this.mappingFileNames.add(mappingFileName);
      }
   }

   public List<URL> getJarFileUrls() {
      return this.jarFileUrls;
   }

   public URL getPersistenceUnitRootUrl() {
      return this.persistenceUnitRootUrl;
   }

   public void setRootUrlAndJarUrls(String persistenceUnitRootUrl, List<String> jarFiles) throws MalformedURLException {
      File root;
      try {
         URI rootUri = URLs.uri(persistenceUnitRootUrl);
         root = new File(rootUri);
      } catch (IllegalArgumentException var7) {
         root = new File(persistenceUnitRootUrl);
      }

      this.persistenceUnitRootUrl = this.toUrl(root);

      try {
         if (!jarFiles.isEmpty()) {
            File tmpRoot;
            if (root.getName().endsWith(".jar")) {
               tmpRoot = root.getParentFile();
            } else {
               tmpRoot = root;
            }

            for(String path : jarFiles) {
               this.jarFileUrls.add(this.toUrl((new File(tmpRoot, path)).getCanonicalFile()));
            }
         }

      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
   }

   private URL toUrl(File root) throws MalformedURLException {
      if (!root.isFile() && root.getPath().startsWith("jar:file:")) {
         try {
            String absolutePath = root.getAbsolutePath();
            int endIndex = absolutePath.indexOf(33);
            if (endIndex > 0) {
               File file = new File(absolutePath.substring(0, endIndex));
               if (file.isFile() && file.getName().endsWith(".jar")) {
                  return file.toURI().toURL();
               }
            }

            return new URL(root.getPath());
         } catch (MalformedURLException var5) {
         }
      }

      return root.toURI().toURL();
   }

   public List<String> getManagedClassNames() {
      return this.managedClassNames;
   }

   public void setManagedClassNames(List<String> managedClassNames) {
      if (managedClassNames == null) {
         throw new NullPointerException("managedClassNames is null");
      } else {
         this.managedClassNames.clear();
         this.managedClassNames.addAll(managedClassNames);
      }
   }

   public void addManagedClassName(String className) {
      this.managedClassNames.add(className);
   }

   public boolean excludeUnlistedClasses() {
      return this.excludeUnlistedClasses;
   }

   public void setExcludeUnlistedClasses(boolean excludeUnlistedClasses) {
      this.excludeUnlistedClasses = excludeUnlistedClasses;
   }

   public Properties getProperties() {
      return this.properties;
   }

   public void setProperties(Properties properties) {
      this.properties = properties;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public void addTransformer(ClassTransformer classTransformer) {
      if (this.persistenceClassLoaderHandler != null) {
         PersistenceClassFileTransformer classFileTransformer = new PersistenceClassFileTransformer(classTransformer);
         this.persistenceClassLoaderHandler.addTransformer(this.id, this.classLoader, classFileTransformer);
      }

   }

   public ClassLoader getNewTempClassLoader() {
      return this.persistenceClassLoaderHandler != null ? this.persistenceClassLoaderHandler.getNewTempClassLoader(this.classLoader) : null;
   }

   public boolean isLazilyInitialized() {
      return this.lazilyInitialized;
   }

   public void setLazilyInitialized(boolean lazilyInitialized) {
      this.lazilyInitialized = lazilyInitialized;
   }

   public static boolean isServerClass(String name) {
      if (name == null) {
         return false;
      } else {
         for(String prefix : URLClassLoaderFirst.FORCED_SKIP) {
            if (name.startsWith(prefix)) {
               return true;
            }
         }

         for(String prefix : URLClassLoaderFirst.FORCED_LOAD) {
            if (name.startsWith(prefix)) {
               return false;
            }
         }

         if (name.startsWith("java.")) {
            return true;
         } else if (name.startsWith("javax.")) {
            return true;
         } else if (name.startsWith("sun.")) {
            return true;
         } else if (name.startsWith("com.sun.")) {
            return true;
         } else if (name.startsWith("org.")) {
            String org = name.substring("org.".length());
            if (org.startsWith("apache.")) {
               String apache = org.substring("apache.".length());
               if (apache.startsWith("bval.")) {
                  return true;
               } else if (apache.startsWith("openjpa.")) {
                  return true;
               } else if (apache.startsWith("derby.")) {
                  return true;
               } else if (apache.startsWith("xbean.")) {
                  return true;
               } else if (apache.startsWith("geronimo.")) {
                  return true;
               } else if (apache.startsWith("coyote")) {
                  return true;
               } else if (apache.startsWith("webbeans.")) {
                  return true;
               } else if (apache.startsWith("log4j")) {
                  return true;
               } else if (apache.startsWith("catalina")) {
                  return true;
               } else if (apache.startsWith("jasper.")) {
                  return true;
               } else if (apache.startsWith("tomcat.")) {
                  return true;
               } else if (apache.startsWith("el.")) {
                  return true;
               } else if (apache.startsWith("jsp")) {
                  return true;
               } else if (apache.startsWith("naming")) {
                  return true;
               } else if (apache.startsWith("taglibs.")) {
                  return true;
               } else if (apache.startsWith("openejb.")) {
                  return true;
               } else if (apache.startsWith("openjpa.")) {
                  return true;
               } else if (apache.startsWith("myfaces.")) {
                  return true;
               } else if (apache.startsWith("juli.")) {
                  return true;
               } else if (apache.startsWith("webbeans.")) {
                  return true;
               } else if (apache.startsWith("cxf.")) {
                  return true;
               } else if (apache.startsWith("activemq.")) {
                  return true;
               } else if (apache.startsWith("commons.")) {
                  String commons = apache.substring("commons.".length());
                  if (commons.startsWith("beanutils")) {
                     return true;
                  } else if (commons.startsWith("cli")) {
                     return true;
                  } else if (commons.startsWith("codec")) {
                     return true;
                  } else if (commons.startsWith("collections")) {
                     return true;
                  } else if (commons.startsWith("dbcp")) {
                     return true;
                  } else if (commons.startsWith("digester")) {
                     return true;
                  } else if (commons.startsWith("jocl")) {
                     return true;
                  } else if (commons.startsWith("lang")) {
                     return true;
                  } else if (commons.startsWith("logging")) {
                     return false;
                  } else if (commons.startsWith("pool")) {
                     return true;
                  } else if (commons.startsWith("net")) {
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else if (org.startsWith("codehaus.swizzle")) {
               return true;
            } else if (org.startsWith("w3c.dom")) {
               return true;
            } else if (org.startsWith("quartz")) {
               return true;
            } else if (org.startsWith("eclipse.jdt.")) {
               return true;
            } else if (org.startsWith("slf4j")) {
               return true;
            } else if (org.startsWith("openejb")) {
               return true;
            } else if (org.startsWith("hsqldb")) {
               return true;
            } else if (org.startsWith("hibernate")) {
               return true;
            } else {
               return false;
            }
         } else if (name.startsWith("com.sun.")) {
            return true;
         } else if (name.startsWith("jdk.")) {
            return true;
         } else if (name.startsWith("javassist")) {
            return true;
         } else if (name.startsWith("serp.")) {
            return true;
         } else {
            return false;
         }
      }
   }

   public String getPersistenceXMLSchemaVersion() {
      return this.persistenceXMLSchemaVersion;
   }

   public void setPersistenceXMLSchemaVersion(String persistenceXMLSchemaVersion) {
      this.persistenceXMLSchemaVersion = persistenceXMLSchemaVersion;
   }

   public SharedCacheMode getSharedCacheMode() {
      return this.sharedCacheMode;
   }

   public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
      this.sharedCacheMode = null != sharedCacheMode ? sharedCacheMode : SharedCacheMode.UNSPECIFIED;
   }

   public ValidationMode getValidationMode() {
      return this.validationMode;
   }

   public void setValidationMode(ValidationMode validationMode) {
      this.validationMode = validationMode;
   }

   public String getJtaDataSourceName() {
      return this.jtaDataSourceName;
   }

   public void setJtaDataSourceName(String jtaDataSourceName) {
      this.jtaDataSourceName = jtaDataSourceName;
   }

   public String getNonJtaDataSourceName() {
      return this.nonJtaDataSourceName;
   }

   public void setNonJtaDataSourceName(String nonJtaDataSourceName) {
      this.nonJtaDataSourceName = nonJtaDataSourceName;
   }

   public static class PersistenceClassFileTransformer implements ClassFileTransformer {
      private final ClassTransformer classTransformer;

      public PersistenceClassFileTransformer(ClassTransformer classTransformer) {
         this.classTransformer = classTransformer;
      }

      public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
         if (className == null) {
            return classfileBuffer;
         } else {
            String replace = className.replace('/', '.');
            return PersistenceUnitInfoImpl.isServerClass(replace) ? classfileBuffer : this.classTransformer.transform(classLoader, replace, classBeingRedefined, protectionDomain, classfileBuffer);
         }
      }
   }
}
