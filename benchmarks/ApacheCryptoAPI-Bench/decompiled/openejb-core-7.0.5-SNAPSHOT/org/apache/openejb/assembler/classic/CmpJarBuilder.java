package org.apache.openejb.assembler.classic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.apache.openejb.ClassLoaderUtil;
import org.apache.openejb.core.cmp.CmpUtil;
import org.apache.openejb.core.cmp.cmp2.Cmp1Generator;
import org.apache.openejb.core.cmp.cmp2.Cmp2Generator;
import org.apache.openejb.core.cmp.cmp2.CmrField;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.UrlCache;

public class CmpJarBuilder {
   private final ClassLoader tempClassLoader;
   private File jarFile;
   private final Set<String> entries = new TreeSet();
   private final AppInfo appInfo;

   public CmpJarBuilder(AppInfo appInfo, ClassLoader classLoader) {
      this.appInfo = appInfo;
      this.tempClassLoader = ClassLoaderUtil.createTempClassLoader(classLoader);
   }

   public File getJarFile() throws IOException {
      if (this.jarFile == null) {
         this.generate();
      }

      return this.jarFile;
   }

   private void generate() throws IOException {
      if (this.hasCmpBeans()) {
         JarOutputStream jarOutputStream = null;

         try {
            jarOutputStream = openJarFile(this);
            Map<String, Entry> classes = new HashMap();

            for(EjbJarInfo ejbJar : this.appInfo.ejbJars) {
               for(EnterpriseBeanInfo beanInfo : ejbJar.enterpriseBeans) {
                  if (beanInfo instanceof EntityBeanInfo) {
                     EntityBeanInfo entityBeanInfo = (EntityBeanInfo)beanInfo;
                     if ("CONTAINER".equalsIgnoreCase(entityBeanInfo.persistenceType)) {
                        Entry entry = this.generateClass(jarOutputStream, entityBeanInfo);
                        classes.put(entry.clazz, entry);
                     }
                  }
               }
            }

            for(Entry e : classes.values()) {
               this.addJarEntry(jarOutputStream, e.name, e.bytes);
            }

            if (this.appInfo.cmpMappingsXml != null) {
               this.addJarEntry(jarOutputStream, "META-INF/openejb-cmp-generated-orm.xml", this.appInfo.cmpMappingsXml.getBytes());
            }
         } catch (Throwable e) {
            if (null != this.jarFile && !this.jarFile.delete()) {
               this.jarFile.deleteOnExit();
            }

            this.jarFile = null;
            throw new IOException("CmpJarBuilder.generate()", e);
         } finally {
            this.close(jarOutputStream);
         }

      }
   }

   private boolean hasCmpBeans() {
      for(EjbJarInfo ejbJar : this.appInfo.ejbJars) {
         for(EnterpriseBeanInfo beanInfo : ejbJar.enterpriseBeans) {
            if (beanInfo instanceof EntityBeanInfo) {
               EntityBeanInfo entityBeanInfo = (EntityBeanInfo)beanInfo;
               if ("CONTAINER".equalsIgnoreCase(entityBeanInfo.persistenceType)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private Entry generateClass(JarOutputStream jarOutputStream, EntityBeanInfo entityBeanInfo) throws IOException {
      String cmpImplClass = CmpUtil.getCmpImplClassName(entityBeanInfo.abstractSchemaName, entityBeanInfo.ejbClass);
      String entryName = cmpImplClass.replace(".", "/") + ".class";
      if (!this.entries.contains(entryName) && this.tempClassLoader.getResource(entryName) == null) {
         Class<?> beanClass = null;

         try {
            beanClass = this.tempClassLoader.loadClass(entityBeanInfo.ejbClass);
         } catch (ClassNotFoundException e) {
            throw (IOException)(new IOException("Could not find entity bean class " + beanClass)).initCause(e);
         }

         Class<?> primKeyClass = null;
         if (entityBeanInfo.primKeyClass != null) {
            try {
               primKeyClass = this.tempClassLoader.loadClass(entityBeanInfo.primKeyClass);
            } catch (ClassNotFoundException e) {
               throw (IOException)(new IOException("Could not find entity primary key class " + entityBeanInfo.primKeyClass)).initCause(e);
            }
         }

         byte[] bytes;
         if (entityBeanInfo.cmpVersion != 2) {
            Cmp1Generator cmp1Generator = new Cmp1Generator(cmpImplClass, beanClass);
            if ("java.lang.Object".equals(entityBeanInfo.primKeyClass)) {
               cmp1Generator.setUnknownPk(true);
            }

            bytes = cmp1Generator.generate();
         } else {
            Cmp2Generator cmp2Generator = new Cmp2Generator(cmpImplClass, beanClass, entityBeanInfo.primKeyField, primKeyClass, (String[])entityBeanInfo.cmpFieldNames.toArray(new String[entityBeanInfo.cmpFieldNames.size()]));

            for(CmrFieldInfo cmrFieldInfo : entityBeanInfo.cmrFields) {
               EntityBeanInfo roleSource = cmrFieldInfo.mappedBy.roleSource;
               CmrField cmrField = new CmrField(cmrFieldInfo.fieldName, cmrFieldInfo.fieldType, CmpUtil.getCmpImplClassName(roleSource.abstractSchemaName, roleSource.ejbClass), roleSource.local, cmrFieldInfo.mappedBy.fieldName, cmrFieldInfo.synthetic);
               cmp2Generator.addCmrField(cmrField);
            }

            bytes = cmp2Generator.generate();
         }

         return new Entry(cmpImplClass, entryName, bytes);
      } else {
         return null;
      }
   }

   private void addJarEntry(JarOutputStream jarOutputStream, String fileName, byte[] bytes) throws IOException {
      fileName = fileName.replace('\\', '/');
      String path = "";
      StringTokenizer tokenizer = new StringTokenizer(fileName, "/");

      while(tokenizer.hasMoreTokens()) {
         String part = tokenizer.nextToken();
         if (tokenizer.hasMoreTokens()) {
            path = path + part + "/";
            if (!this.entries.contains(path)) {
               jarOutputStream.putNextEntry(new JarEntry(path));
               jarOutputStream.closeEntry();
               this.entries.add(path);
            }
         }
      }

      jarOutputStream.putNextEntry(new JarEntry(fileName));

      try {
         jarOutputStream.write(bytes);
      } finally {
         jarOutputStream.closeEntry();
         this.entries.add(fileName);
      }

   }

   private static synchronized JarOutputStream openJarFile(CmpJarBuilder instance) throws IOException {
      if (instance.jarFile != null) {
         throw new IllegalStateException("Jar file exists already");
      } else {
         File dir = tmpDir();

         try {
            instance.jarFile = File.createTempFile("OpenEJBGenerated.", ".jar", dir).getAbsoluteFile();
         } catch (Throwable e) {
            Logger.getInstance(LogCategory.OPENEJB_STARTUP, CmpJarBuilder.class).warning("Failed to create temp jar file in: " + dir, e);

            try {
               Thread.sleep(50L);
            } catch (InterruptedException var4) {
            }

            instance.jarFile = File.createTempFile("OpenEJBGenerated.", ".jar", dir).getAbsoluteFile();
         }

         Thread.yield();
         instance.jarFile.deleteOnExit();
         Logger.getInstance(LogCategory.OPENEJB_STARTUP, CmpJarBuilder.class).debug("Using temp jar file: " + instance.jarFile);
         return new JarOutputStream(IO.write(instance.jarFile));
      }
   }

   private static File tmpDir() throws IOException {
      File dir = UrlCache.cacheDir;
      if (null == dir) {
         dir = SystemInstance.get().getBase().getDirectory("tmp", true);
      }

      return dir;
   }

   private void close(JarOutputStream jarOutputStream) {
      if (jarOutputStream != null) {
         try {
            jarOutputStream.close();
         } catch (Throwable var3) {
         }
      }

   }

   private static final class Entry {
      private final String clazz;
      private final String name;
      private final byte[] bytes;

      private Entry(String clazz, String name, byte[] bytes) {
         this.clazz = clazz;
         this.name = name;
         this.bytes = bytes;
      }
   }
}
