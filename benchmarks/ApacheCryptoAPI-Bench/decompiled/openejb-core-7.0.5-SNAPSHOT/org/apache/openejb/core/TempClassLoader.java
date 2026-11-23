package org.apache.openejb.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.classloader.URLClassLoaderFirst;
import org.apache.xbean.asm5.ClassReader;
import org.apache.xbean.asm5.shade.commons.EmptyVisitor;

public class TempClassLoader extends URLClassLoader {
   private static final ClassLoader PARENT_LOADER = ParentClassLoaderFinder.Helper.get();
   private static final URL[] EMPTY_URLS = new URL[0];
   private final Set<Skip> skip;
   private final ClassLoader system;
   private final boolean embedded;
   private final boolean parentURLClassLoader;
   private final ByteArrayOutputStream bout = new ByteArrayOutputStream(6144);

   public TempClassLoader(ClassLoader parent) {
      super(EMPTY_URLS, parent);
      this.skip = SystemInstance.get().getOptions().getAll("openejb.tempclassloader.skip", new Skip[]{TempClassLoader.Skip.NONE});
      this.system = ClassLoader.getSystemClassLoader();
      this.embedded = this.getClass().getClassLoader() == this.system;
      this.parentURLClassLoader = URLClassLoader.class.isInstance(parent);
   }

   public void skip(Skip s) {
      this.skip.add(s);
   }

   public Class loadClass(String name) throws ClassNotFoundException {
      return this.loadClass(name, false);
   }

   public URL getResource(String name) {
      URL url = this.parentURLClassLoader ? ((URLClassLoader)URLClassLoader.class.cast(this.getParent())).findResource(name) : null;
      return url != null ? url : super.getResource(name);
   }

   public URL getInternalResource(String name) {
      if (!name.startsWith("java/") && !name.startsWith("javax/") && name.endsWith(".class")) {
         try {
            Enumeration<URL> resources = this.getResources(name);
            if (!resources.hasMoreElements()) {
               return null;
            } else {
               URL url = (URL)resources.nextElement();
               if (!resources.hasMoreElements()) {
                  return url;
               } else {
                  List<URL> l = new ArrayList(2);
                  l.add(url);

                  while(resources.hasMoreElements()) {
                     l.add(resources.nextElement());
                  }

                  Collections.sort(l, new ResourceComparator(this.getParent(), name));
                  return (URL)l.iterator().next();
               }
            }
         } catch (IOException var5) {
            return super.getResource(name);
         }
      } else {
         return super.getResource(name);
      }
   }

   public Enumeration<URL> getResources(String name) throws IOException {
      return URLClassLoaderFirst.filterResources(name, super.getResources(name));
   }

   protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
      if (name == null) {
         throw new NullPointerException("name cannot be null");
      } else {
         Class c = this.findLoadedClass(name);
         if (c != null) {
            return c;
         } else if (!this.skip(name) && (!name.startsWith("javax.faces.") || !URLClassLoaderFirst.shouldSkipJsf(this.getParent(), name))) {
            if (!this.embedded && URLClassLoaderFirst.canBeLoadedFromSystem(name)) {
               try {
                  c = this.system.loadClass(name);
                  if (c != null) {
                     return c;
                  }
               } catch (NoClassDefFoundError | ClassNotFoundException var16) {
               }
            }

            String resourceName = name.replace('.', '/') + ".class";
            this.bout.reset();
            InputStream in = null;

            byte[] bytes;
            try {
               in = this.getResourceAsStream(resourceName);
               if (in != null && !(in instanceof BufferedInputStream)) {
                  in = new BufferedInputStream(in);
               }

               if (in == null) {
                  throw new ClassNotFoundException(name);
               }

               IO.copy(in, this.bout);
               bytes = this.bout.toByteArray();
            } catch (IOException e) {
               throw new ClassNotFoundException(name, e);
            } finally {
               IO.close(in);
            }

            if (this.skip.contains(TempClassLoader.Skip.ANNOTATIONS) && isAnnotationClass(bytes)) {
               return Class.forName(name, resolve, PARENT_LOADER);
            } else if (this.skip.contains(TempClassLoader.Skip.ENUMS) && isEnum(bytes)) {
               return Class.forName(name, resolve, PARENT_LOADER);
            } else {
               int packageEndIndex = name.lastIndexOf(46);
               if (packageEndIndex != -1) {
                  String packageName = name.substring(0, packageEndIndex);
                  if (this.getPackage(packageName) == null) {
                     this.definePackage(packageName, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null);
                  }
               }

               try {
                  return this.defineClass(name, bytes, 0, bytes.length);
               } catch (SecurityException var14) {
                  return super.loadClass(name, resolve);
               } catch (LinkageError var15) {
                  return super.loadClass(name, resolve);
               }
            }
         } else {
            return Class.forName(name, resolve, PARENT_LOADER);
         }
      }
   }

   private boolean skip(String name) {
      return this.skip.contains(TempClassLoader.Skip.ALL) || URLClassLoaderFirst.shouldSkip(name);
   }

   private static boolean isEnum(byte[] bytes) {
      IsEnumVisitor isEnumVisitor = new IsEnumVisitor();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(isEnumVisitor, 2);
      return isEnumVisitor.isEnum;
   }

   private static boolean isAnnotationClass(byte[] bytes) {
      IsAnnotationVisitor isAnnotationVisitor = new IsAnnotationVisitor();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(isAnnotationVisitor, 2);
      return isAnnotationVisitor.isAnnotation;
   }

   public static enum Skip {
      NONE,
      ANNOTATIONS,
      ENUMS,
      ALL;
   }

   public static class IsAnnotationVisitor extends EmptyVisitor {
      public boolean isAnnotation;

      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         this.isAnnotation = (access & 8192) != 0;
      }
   }

   public static class IsEnumVisitor extends EmptyVisitor {
      public boolean isEnum;

      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         this.isEnum = (access & 16384) != 0;
      }
   }

   private static final class ResourceComparator implements Comparator<URL> {
      private static final boolean FORCE_MAVEN_FIRST = "true".equals(SystemInstance.get().getProperty("openejb.classloader.force-maven", "false"));
      private static final ClassLoader STOP_LOADER = ClassLoader.getSystemClassLoader().getParent();
      private final ClassLoader loader;
      private final String name;

      private ResourceComparator(ClassLoader loader, String name) {
         this.loader = loader;
         this.name = name;
      }

      public int compare(URL o1, URL o2) {
         if (o1.equals(o2)) {
            return 0;
         } else {
            int weight1 = this.weight(o1);
            int weight2 = this.weight(o2);
            if (weight1 != weight2) {
               return weight1 - weight2;
            } else {
               String s1 = o1.toExternalForm().replace(File.separatorChar, '/');
               String s2 = o2.toExternalForm().replace(File.separatorChar, '/');
               if (FORCE_MAVEN_FIRST) {
                  if (s1.contains("/target/classes/") || s1.contains("/build/classes/main/")) {
                     return -1;
                  }

                  if (s2.contains("/target/classes/") || s2.contains("/build/classes/main/")) {
                     return 1;
                  }

                  if (s1.contains("/target/test-classes/") || s1.contains("/build/classes/test/")) {
                     return -1;
                  }

                  if (s2.contains("/target/test-classes/") || s2.contains("/build/classes/test/")) {
                     return 1;
                  }
               }

               if (s1.contains("/WEB-INF/classes/")) {
                  return -1;
               } else if (s2.contains("/WEB-INF/classes/")) {
                  return 1;
               } else {
                  return s1.compareTo(s2);
               }
            }
         }
      }

      private int weight(URL url) {
         int w = 0;
         ClassLoader c = this.loader;

         while(c != null) {
            try {
               if (!Collections.list(c.getResources(this.name)).contains(url)) {
                  break;
               }

               ++w;
            } catch (IOException var5) {
               break;
            }

            c = c.getParent();
            if (c == STOP_LOADER) {
               break;
            }
         }

         return w;
      }
   }
}
