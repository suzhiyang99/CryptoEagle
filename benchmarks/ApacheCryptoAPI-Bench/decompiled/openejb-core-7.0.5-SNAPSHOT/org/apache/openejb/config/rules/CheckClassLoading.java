package org.apache.openejb.config.rules;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.ClientModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.WebModule;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.URLs;
import org.apache.xbean.finder.UrlSet;

public class CheckClassLoading extends ValidationBase {
   public static final String OPENEJB_CHECK_CLASSLOADER_VERBOSE = "openejb.check.classloader.verbose";
   protected AppModule appModule;

   public void validate(AppModule appModule) {
      this.appModule = appModule;
      this.module = appModule;
      this.check(appModule.getClassLoader());

      for(WebModule webModule : appModule.getWebModules()) {
         this.module = webModule;
         this.validate(webModule);
      }

      super.validate(appModule);
   }

   private void check(ClassLoader classLoader) {
      UrlSet openejbSet;
      UrlSet set;
      try {
         openejbSet = new UrlSet(OpenEJB.class.getClassLoader());
         set = new UrlSet(classLoader);
         set = set.exclude(openejbSet);
      } catch (IOException e) {
         this.warn(this.module.getModuleId() + " application", e.getMessage(), new Object[0]);
         return;
      }

      List<URL> parentUrls = openejbSet.getUrls();
      List<URL> currentUrls = set.getUrls();
      Classes fcl = new Classes((URL[])currentUrls.toArray(new URL[currentUrls.size()]));
      Classes scl = new Classes((URL[])parentUrls.toArray(new URL[parentUrls.size()]));

      for(DiffItem diff : intersection(fcl, scl)) {
         this.warn(this.module.getModuleId() + " application", diff.toScreen(), new Object[0]);
      }

   }

   private void validate(WebModule webModule) {
      this.check(webModule.getClassLoader());
   }

   public void validate(ClientModule clientModule) {
      this.check(clientModule.getClassLoader());
   }

   public void validate(EjbModule ejbModule) {
      this.check(ejbModule.getClassLoader());
   }

   public static Collection<DiffItem> intersection(Classes cl1, Classes cl2) {
      List<DiffItem> diff = new ArrayList();

      for(Map.Entry<String, Collection<String>> entry1 : cl1.fileByArchive.entrySet()) {
         for(Map.Entry<String, Collection<String>> entry2 : cl2.fileByArchive.entrySet()) {
            Collection<String> v1 = (Collection)entry1.getValue();
            Collection<String> v2 = (Collection)entry2.getValue();
            Collection<String> inter = CollectionUtils.intersection(v1, v2);
            if (inter.size() != 0) {
               if (inter.size() == v1.size() && v1.size() == v2.size()) {
                  diff.add(new SameItem(inter, (String)entry1.getKey(), (String)entry2.getKey()));
               } else if (inter.size() == v1.size()) {
                  diff.add(new IncludedItem(inter, (String)entry1.getKey(), (String)entry2.getKey()));
               } else if (inter.size() == v2.size()) {
                  diff.add(new ContainingItem(inter, (String)entry1.getKey(), (String)entry2.getKey()));
               } else {
                  diff.add(new DiffItem(inter, (String)entry1.getKey(), (String)entry2.getKey()));
               }
            }
         }
      }

      Collections.sort(diff, CheckClassLoading.DiffItemComparator.getInstance());
      return diff;
   }

   public static class Classes {
      private static final String[] CLASS_EXTENSION = new String[]{".class"};
      private final Map<String, Collection<String>> fileByArchive = new TreeMap();

      public Classes(URL[] urls) {
         this.list(urls);
      }

      public void list(URL[] urls) {
         this.fileByArchive.clear();

         for(URL archive : urls) {
            try {
               File file = URLs.toFile(archive);
               List<String> files = CheckClassLoading.JarUtil.listFiles(file, CLASS_EXTENSION);
               Collections.sort(files);
               this.fileByArchive.put(file.getName(), files);
            } catch (Exception var8) {
            }
         }

      }
   }

   public static final class JarUtil {
      public static final String CLASS_EXT = ".class";

      private JarUtil() {
      }

      public static List<String> listFiles(File archive, String[] extensions) throws IOException {
         if (archive.exists() && archive.isFile()) {
            List<String> files = new ArrayList();
            JarFile file = new JarFile(archive);
            Throwable var4 = null;

            Object var21;
            try {
               Enumeration<JarEntry> entries = file.entries();

               while(entries.hasMoreElements()) {
                  JarEntry entry = (JarEntry)entries.nextElement();
                  String name = entry.getName();

                  for(String ext : extensions) {
                     if (name.endsWith(ext)) {
                        if (".class".equals(ext)) {
                           files.add(name.replace("/", "."));
                        } else {
                           files.add(name);
                        }
                        break;
                     }
                  }
               }

               var21 = files;
            } catch (Throwable var19) {
               var4 = var19;
               throw var19;
            } finally {
               if (file != null) {
                  if (var4 != null) {
                     try {
                        file.close();
                     } catch (Throwable var18) {
                        var4.addSuppressed(var18);
                     }
                  } else {
                     file.close();
                  }
               }

            }

            return (List<String>)var21;
         } else {
            throw new IllegalArgumentException(archive.getPath() + " is not a file");
         }
      }
   }

   public static class DiffItem {
      private Collection<String> files = new ArrayList();
      private final String file1;
      private final String file2;

      public DiffItem(Collection<String> files, String file1, String file2) {
         this.files = files;
         this.file1 = file1;
         this.file2 = file2;
      }

      public String getFile1() {
         return this.file1;
      }

      public String getFile2() {
         return this.file2;
      }

      public String toScreen() {
         String str = "both files " + this.file1 + '\'' + " and " + this.file2 + '\'';
         return SystemInstance.get().getOptions().get("openejb.check.classloader.verbose", false) ? str + " contains files=" + this.files : str;
      }
   }

   public static class ContainingItem extends DiffItem {
      public ContainingItem(Collection<String> inter, String dir1, String dir2) {
         super(inter, dir1, dir2);
      }

      public String toScreen() {
         return this.getFile1() + " contains " + this.getFile2();
      }
   }

   public static class IncludedItem extends DiffItem {
      public IncludedItem(Collection<String> files, String file1, String file2) {
         super(files, file1, file2);
      }

      public String toScreen() {
         return this.getFile1() + " is included inside " + this.getFile2();
      }
   }

   public static class SameItem extends DiffItem {
      public SameItem(Collection<String> files, String file1, String file2) {
         super(files, file1, file2);
      }

      public String toScreen() {
         return this.getFile1() + " is the same than " + this.getFile2();
      }
   }

   public static class DiffItemComparator implements Comparator<DiffItem> {
      private static final DiffItemComparator INSTANCE = new DiffItemComparator();
      private static final Map<Class<?>, Integer> ORDER = new HashMap();

      public static DiffItemComparator getInstance() {
         return INSTANCE;
      }

      public int compare(DiffItem o1, DiffItem o2) {
         int index1 = (Integer)ORDER.get(o1.getClass());
         int index2 = (Integer)ORDER.get(o2.getClass());
         return index1 == index2 ? o1.getFile1().compareTo(o1.getFile1()) : index1 - index2;
      }

      static {
         ORDER.put(SameItem.class, 0);
         ORDER.put(IncludedItem.class, 1);
         ORDER.put(ContainingItem.class, 2);
         ORDER.put(DiffItem.class, 3);
      }
   }
}
