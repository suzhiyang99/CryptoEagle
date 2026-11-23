package org.apache.openejb.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import org.apache.openejb.config.DeploymentsResolver;
import org.apache.xbean.asm5.AnnotationVisitor;
import org.apache.xbean.asm5.Attribute;
import org.apache.xbean.asm5.ClassReader;
import org.apache.xbean.asm5.ClassVisitor;
import org.apache.xbean.asm5.FieldVisitor;
import org.apache.xbean.asm5.MethodVisitor;
import org.apache.xbean.finder.UrlSet;

public class AnnotationFinder {
   private static final int ASM_FLAGS = 7;
   private final ClassLoader classLoader;
   private final List<String> classesNotLoaded;
   private final List<String> classNames;
   public static int classes;

   public AnnotationFinder(ClassLoader classLoader) throws Exception {
      this(classLoader, true);
   }

   public AnnotationFinder(ClassLoader classLoader, boolean excludeParent) throws Exception {
      this(classLoader, getUrls(classLoader, excludeParent));
   }

   public AnnotationFinder(ClassLoader classLoader, ClassLoader exclude) throws Exception {
      this(classLoader, getUrls(classLoader, exclude));
   }

   public AnnotationFinder(ClassLoader classLoader, URL url) {
      this(classLoader, (Collection)Arrays.asList(url));
   }

   public AnnotationFinder(ClassLoader classLoader, Collection<URL> urls) {
      this.classesNotLoaded = new ArrayList();
      this.classLoader = classLoader;
      this.classNames = new ArrayList();

      for(URL location : urls) {
         if (location != null) {
            try {
               if (location.getProtocol().equals("jar")) {
                  this.classNames.addAll(jar(location));
               } else if (location.getProtocol().equals("file")) {
                  try {
                     URL jarUrl = new URL("jar", "", location.toExternalForm().replace("%20", " ").replace("%23", "#") + "!/");
                     JarURLConnection juc = (JarURLConnection)jarUrl.openConnection();
                     this.classNames.addAll(jar(juc.getJarFile()));
                  } catch (IOException var7) {
                     this.classNames.addAll(file(location));
                  }
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }

   }

   public List<String> getClassesNotLoaded() {
      return Collections.unmodifiableList(this.classesNotLoaded);
   }

   public boolean find(Filter filter) {
      Visitor annotationVisitor = new Visitor(filter);

      for(String className : this.classNames) {
         try {
            this.readClassDef(className, annotationVisitor);
         } catch (NotFoundException var6) {
         } catch (FoundException var7) {
            return true;
         }
      }

      return false;
   }

   private static Collection<URL> getUrls(ClassLoader classLoader, boolean excludeParent) throws IOException {
      return getUrls(classLoader, excludeParent ? classLoader.getParent() : null);
   }

   private static Collection<URL> getUrls(ClassLoader classLoader, ClassLoader excludeParent) throws IOException {
      UrlSet urlSet = new UrlSet(classLoader);
      if (excludeParent != null) {
         urlSet = urlSet.exclude(excludeParent);
      }

      return urlSet.getUrls();
   }

   private static List<String> file(URL location) {
      List<String> classNames = new ArrayList();

      File dir;
      try {
         dir = new File(URLDecoder.decode(location.getPath(), "UTF-8"));
      } catch (Exception var4) {
         dir = new File(URLDecoder.decode(location.getPath()));
      }

      if (dir.getName().equals("META-INF")) {
         dir = dir.getParentFile();
      }

      if (dir.isDirectory()) {
         scanDir(dir, classNames, "");
      }

      return classNames;
   }

   private static void scanDir(File dir, List<String> classNames, String packageName) {
      File[] files = dir.listFiles();
      if (files != null) {
         for(File file : files) {
            if (file.isDirectory()) {
               if (!DeploymentsResolver.isExtractedDir(file)) {
                  scanDir(file, classNames, packageName + file.getName() + ".");
               }
            } else if (file.getName().endsWith(".class")) {
               String name = file.getName();
               name = name.replaceFirst(".class$", "");
               if (!name.contains(".")) {
                  classNames.add(packageName + name);
               }
            }
         }
      }

   }

   private static List<String> jar(URL location) throws IOException, URISyntaxException {
      String jarPath = location.getFile();
      if (jarPath.contains("!")) {
         jarPath = jarPath.substring(0, jarPath.indexOf("!"));
      }

      URL url = new URL(jarPath);
      if ("file".equals(url.getProtocol())) {
         JarFile jarFile = new JarFile(url.getFile().replace("%20", " ").replace("%23", "#"));
         return jar(jarFile);
      } else {
         InputStream in = url.openStream();
         in = new BufferedInputStream(in);

         List var5;
         try {
            JarInputStream jarStream = new JarInputStream(in);
            var5 = jar(jarStream);
         } finally {
            in.close();
         }

         return var5;
      }
   }

   private static List<String> jar(JarFile jarFile) {
      List<String> classNames = new ArrayList();
      Enumeration<? extends JarEntry> jarEntries = jarFile.entries();

      while(jarEntries.hasMoreElements()) {
         JarEntry entry = (JarEntry)jarEntries.nextElement();
         addClassName(classNames, entry);
      }

      return classNames;
   }

   private static List<String> jar(JarInputStream jarStream) throws IOException {
      List<String> classNames = new ArrayList();

      JarEntry entry;
      while((entry = jarStream.getNextJarEntry()) != null) {
         addClassName(classNames, entry);
      }

      return classNames;
   }

   private static void addClassName(List<String> classNames, JarEntry entry) {
      if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
         String className = entry.getName();
         className = className.replaceFirst(".class$", "");
         if (!className.contains(".")) {
            className = className.replace(File.separatorChar, '.');
            classNames.add(className);
         }
      }
   }

   private void readClassDef(String className, ClassVisitor visitor) {
      ++classes;
      if (!className.endsWith(".class")) {
         className = className.replace('.', '/') + ".class";
      }

      try {
         URL resource = this.classLoader.getResource(className);
         if (resource != null) {
            InputStream in = resource.openStream();
            in = new BufferedInputStream(in);

            try {
               ClassReader classReader = new ClassReader(in);
               classReader.accept(visitor, 7);
            } finally {
               in.close();
            }
         } else {
            (new Exception("Could not load " + className)).printStackTrace();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   public static class NotFoundException extends RuntimeException {
   }

   public static class FoundException extends RuntimeException {
   }

   public class Visitor extends ClassVisitor {
      private NotFoundException notFoundException;
      private final FoundException foundException;
      private final Filter filter;

      public Visitor(Filter filter) {
         super(327680);
         this.filter = filter;

         try {
            throw new NotFoundException();
         } catch (NotFoundException e) {
            this.notFoundException = e;

            try {
               throw new FoundException();
            } catch (FoundException e) {
               this.foundException = e;
            }
         }
      }

      public AnnotationVisitor visitAnnotation(String name, boolean visible) {
         StringBuilder sb = new StringBuilder(name);
         sb.deleteCharAt(0);
         sb.deleteCharAt(sb.length() - 1);

         for(int i = 0; i < sb.length(); ++i) {
            if (sb.charAt(i) == '/') {
               sb.setCharAt(i, '.');
            }
         }

         name = sb.toString();
         if (this.filter.accept(name)) {
            throw this.foundException;
         } else {
            return null;
         }
      }

      public void visit(int i, int i1, String string, String string1, String string2, String[] strings) {
      }

      public void visitSource(String string, String string1) {
      }

      public void visitOuterClass(String string, String string1, String string2) {
      }

      public void visitAttribute(Attribute attribute) {
         throw this.notFoundException;
      }

      public void visitInnerClass(String string, String string1, String string2, int i) {
         throw this.notFoundException;
      }

      public FieldVisitor visitField(int i, String string, String string1, String string2, Object object) {
         throw this.notFoundException;
      }

      public MethodVisitor visitMethod(int i, String string, String string1, String string2, String[] strings) {
         throw this.notFoundException;
      }

      public void visitEnd() {
         throw this.notFoundException;
      }
   }

   public interface Filter {
      boolean accept(String var1);
   }
}
