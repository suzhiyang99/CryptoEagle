package org.apache.openejb.assembler.classic;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import javax.xml.parsers.SAXParser;
import org.apache.openejb.config.event.BeforeDeploymentEvent;
import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.observer.Observes;
import org.apache.openejb.util.JarCreator;
import org.apache.openejb.util.JarExtractor;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Saxs;
import org.apache.openejb.util.URLs;
import org.apache.openejb.util.classloader.URLClassLoaderFirst;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DeployTimeEnhancer {
   private static final Logger LOGGER;
   private static final String OPENEJB_JAR_ENHANCEMENT_INCLUDE = "openejb.jar.enhancement.include";
   private static final String OPENEJB_JAR_ENHANCEMENT_EXCLUDE = "openejb.jar.enhancement.exclude";
   private static final String CLASS_EXT = ".class";
   private static final String PROPERTIES_FILE_PROP = "propertiesFile";
   private static final String META_INF_PERSISTENCE_XML = "META-INF/persistence.xml";
   private static final String TMP_ENHANCEMENT_SUFFIX = ".tmp-enhancement";
   private final Method enhancerMethod;
   private final Constructor<?> optionsConstructor;

   public DeployTimeEnhancer() {
      ClassLoader cl = DeployTimeEnhancer.class.getClassLoader();

      Method mtd;
      Constructor<?> cstr;
      try {
         Class<?> enhancerClass = cl.loadClass("org.apache.openjpa.enhance.PCEnhancer");
         Class<?> arg2 = cl.loadClass("org.apache.openjpa.lib.util.Options");
         cstr = arg2.getConstructor(Properties.class);
         mtd = enhancerClass.getMethod("run", String[].class, arg2);
      } catch (Exception var6) {
         LOGGER.warning("openjpa enhancer can't be found in the container, will be skipped");
         mtd = null;
         cstr = null;
      }

      this.optionsConstructor = cstr;
      this.enhancerMethod = mtd;
   }

   public void enhance(@Observes BeforeDeploymentEvent event) {
      if (this.enhancerMethod == null) {
         LOGGER.debug("OpenJPA is not available so no deploy-time enhancement will be done");
      } else {
         Map<String, List<String>> classesByPXml = new HashMap();
         List<URL> usedUrls = new ArrayList();

         for(URL url : event.getUrls()) {
            File file = URLs.toFile(url);
            if (file.isDirectory()) {
               String pXmls = this.getWarPersistenceXml(url);
               if (pXmls != null) {
                  this.feed(classesByPXml, pXmls);
               }

               usedUrls.add(url);
            } else if (file.getName().endsWith(".jar")) {
               JarFile jar = null;

               try {
                  jar = new JarFile(file);
                  ZipEntry entry = jar.getEntry("META-INF/persistence.xml");
                  if (entry != null) {
                     String path = file.getAbsolutePath();
                     File unpacked = new File(path.substring(0, path.length() - 4) + ".tmp-enhancement");
                     JarExtractor.extract(file, unpacked);
                     usedUrls.add(unpacked.toURI().toURL());
                     this.feed(classesByPXml, (new File(unpacked, "META-INF/persistence.xml")).getAbsolutePath());
                  } else {
                     usedUrls.add(url);
                  }
               } catch (IOException var35) {
               } finally {
                  try {
                     if (jar != null) {
                        jar.close();
                     }
                  } catch (IOException var32) {
                  }

               }
            } else {
               usedUrls.add(url);
            }
         }

         ClassLoader tccl = Thread.currentThread().getContextClassLoader();
         ClassLoader fakeClassLoader = new URLClassLoaderFirst((URL[])usedUrls.toArray(new URL[usedUrls.size()]), event.getParentClassLoader());
         LOGGER.info("Enhancing url(s): " + usedUrls);
         Thread.currentThread().setContextClassLoader(fakeClassLoader);

         try {
            for(Map.Entry<String, List<String>> entry : classesByPXml.entrySet()) {
               Properties opts = new Properties();
               opts.setProperty("propertiesFile", (String)entry.getKey());

               Object optsArg;
               try {
                  optsArg = this.optionsConstructor.newInstance(opts);
               } catch (Exception var37) {
                  LOGGER.debug("can't create options for enhancing");
                  return;
               }

               String[] args = this.toFilePaths((List)entry.getValue());
               LOGGER.info("Enhancing: " + Arrays.asList(args));

               try {
                  this.enhancerMethod.invoke((Object)null, args, optsArg);
               } catch (Exception e) {
                  LOGGER.warning("can't enhanced at deploy-time entities", (Throwable)e);
               }
            }
         } finally {
            Thread.currentThread().setContextClassLoader(tccl);
            usedUrls.clear();
         }

         for(Map.Entry<String, List<String>> entry : classesByPXml.entrySet()) {
            List<String> values = (List)entry.getValue();

            for(String rawPath : values) {
               if (rawPath.endsWith(".tmp-enhancement/") || rawPath.endsWith(".tmp-enhancement")) {
                  File dir = new File(rawPath);
                  File file = new File(rawPath.substring(0, rawPath.length() - ".tmp-enhancement".length() - 1) + ".jar");
                  if (file.exists()) {
                     String name = dir.getName();
                     name = name.substring(0, name.length() - ".tmp-enhancement".length()) + ".jar";
                     File target = new File(dir.getParentFile(), name);

                     try {
                        Files.delete(file);
                        JarCreator.jarDir(dir, target);
                     } catch (IOException var33) {
                        LOGGER.error("can't repackage enhanced jar file " + file.getName());
                     }

                     Files.delete(dir);
                  }
               }
            }

            values.clear();
         }

         classesByPXml.clear();
      }
   }

   private void feed(Map<String, List<String>> classesByPXml, String pXml) {
      List<String> paths = new ArrayList();
      if (pXml.endsWith("META-INF/persistence.xml")) {
         paths.add(pXml.substring(0, pXml.length() - "META-INF/persistence.xml".length()));
      } else if (pXml.endsWith("/WEB-INF/persistence.xml")) {
         paths.add(pXml.substring(0, pXml.length() - 24));
      }

      try {
         SAXParser parser = Saxs.factory().newSAXParser();
         JarFileParser handler = new JarFileParser();
         parser.parse(new File(pXml), handler);

         for(String path : handler.getPaths()) {
            paths.add(this.relative((String)paths.iterator().next(), path));
         }
      } catch (Exception e) {
         LOGGER.error("can't parse '" + pXml + "'", (Throwable)e);
      }

      classesByPXml.put(pXml, paths);
   }

   private String relative(String relativePath, String pXmlPath) {
      return (new File((new File(pXmlPath)).getParent(), relativePath)).getAbsolutePath();
   }

   private String getWarPersistenceXml(URL url) {
      File dir = URLs.toFile(url);
      if (dir.isDirectory() && (dir.getAbsolutePath().endsWith("/WEB-INF/classes") || dir.getAbsolutePath().endsWith("/WEB-INF/classes/"))) {
         File pXmlStd = new File(dir.getParentFile(), "persistence.xml");
         if (pXmlStd.exists()) {
            return pXmlStd.getAbsolutePath();
         }

         File pXml = new File(dir, "META-INF/persistence.xml");
         if (pXml.exists()) {
            return pXml.getAbsolutePath();
         }
      }

      return null;
   }

   private String[] toFilePaths(List<String> urls) {
      List<String> files = new ArrayList();

      for(String url : urls) {
         File dir = new File(url);
         if (dir.isDirectory()) {
            for(File f : Files.collect(dir, new ClassFilter())) {
               files.add(f.getAbsolutePath());
            }
         }
      }

      return (String[])files.toArray(new String[files.size()]);
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB_DEPLOY, DeployTimeEnhancer.class);
   }

   private static class ClassFilter implements FileFilter {
      private static final String DEFAULT_INCLUDE = "\\*";
      private static final String DEFAULT_EXCLUDE = "";
      private static final Pattern INCLUDE_PATTERN = Pattern.compile(SystemInstance.get().getOptions().get("openejb.jar.enhancement.include", "\\*"));
      private static final Pattern EXCLUDE_PATTERN = Pattern.compile(SystemInstance.get().getOptions().get("openejb.jar.enhancement.exclude", ""));

      private ClassFilter() {
      }

      public boolean accept(File file) {
         boolean isClass = file.getName().endsWith(".class");
         if ("".equals(EXCLUDE_PATTERN.pattern()) && "\\*".equals(INCLUDE_PATTERN.pattern())) {
            return isClass;
         } else {
            String path = file.getAbsolutePath();
            return isClass && INCLUDE_PATTERN.matcher(path).matches() && !EXCLUDE_PATTERN.matcher(path).matches();
         }
      }
   }

   private static class JarFileParser extends DefaultHandler {
      private final List<String> paths;
      private boolean getIt;

      private JarFileParser() {
         this.paths = new ArrayList();
      }

      public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {
         if (localName.endsWith("jar-file")) {
            this.getIt = true;
         }
      }

      public void characters(char[] ch, int start, int length) throws SAXException {
         if (this.getIt) {
            this.paths.add(String.valueOf(ch, start, length));
         }

      }

      public void endElement(String uri, String localName, String qName) throws SAXException {
         this.getIt = false;
      }

      public List<String> getPaths() {
         return this.paths;
      }
   }
}
