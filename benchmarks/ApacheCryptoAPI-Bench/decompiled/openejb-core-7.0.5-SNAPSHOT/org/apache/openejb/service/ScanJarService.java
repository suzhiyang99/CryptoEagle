package org.apache.openejb.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.openejb.cdi.CompositeBeans;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.FinderFactory;
import org.apache.openejb.config.ReadDescriptors;
import org.apache.openejb.config.WebModule;
import org.apache.openejb.config.event.BeforeAppInfoBuilderEvent;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.observer.Observes;
import org.apache.xbean.finder.archive.FileArchive;
import org.apache.xbean.finder.archive.JarArchive;

public class ScanJarService {
   private File path;
   private boolean directory;
   private volatile CompositeBeans beans;

   public void addScanningResult(@Observes BeforeAppInfoBuilderEvent event) throws Exception {
      for(EjbModule ejbModule : event.getAppModule().getEjbModules()) {
         if (ejbModule.getModuleId().startsWith("ear-scoped-cdi-beans")) {
            this.doMerge(ejbModule);
            return;
         }
      }

      for(WebModule webModule : event.getAppModule().getWebModules()) {
         for(EjbModule ejbModule : event.getAppModule().getEjbModules()) {
            if (ejbModule.getModuleId().equals(webModule.getModuleId())) {
               this.doMerge(ejbModule);
               return;
            }
         }
      }

   }

   private void doMerge(EjbModule ejbModule) throws Exception {
      Beans beans = ejbModule.getBeans();
      if (CompositeBeans.class.isInstance(beans)) {
         CompositeBeans cb = (CompositeBeans)CompositeBeans.class.cast(beans);
         this.ensureInit();
         this.merge(cb);
      } else if (beans != null) {
         this.ensureInit();

         for(URL key : this.beans.getManagedClasses().keySet()) {
            beans.getManagedClasses().putAll(this.beans.getManagedClasses());
            this.addIfNotNull(beans.getInterceptors(), (Collection)this.beans.getInterceptorsByUrl().get(key));
            this.addIfNotNull(beans.getAlternativeClasses(), (Collection)this.beans.getAlternativesByUrl().get(key));
            this.addIfNotNull(beans.getAlternativeStereotypes(), (Collection)this.beans.getAlternativeStereotypesByUrl().get(key));
            this.addIfNotNull(beans.getDecorators(), (Collection)this.beans.getDecoratorsByUrl().get(key));
         }
      }

   }

   private void addIfNotNull(List<String> out, Collection<String> in) {
      if (in != null) {
         out.addAll(in);
      }

   }

   private void merge(CompositeBeans cb) {
      cb.getManagedClasses().putAll(this.beans.getManagedClasses());
      cb.getNotManagedClasses().putAll(this.beans.getNotManagedClasses());
      cb.getAlternativesByUrl().putAll(this.beans.getAlternativesByUrl());
      cb.getAlternativeStereotypesByUrl().putAll(this.beans.getAlternativeStereotypesByUrl());
      cb.getInterceptorsByUrl().putAll(this.beans.getInterceptorsByUrl());
      cb.getDecoratorsByUrl().putAll(this.beans.getDecoratorsByUrl());
   }

   private void ensureInit() throws Exception {
      if (this.beans == null) {
         synchronized(this) {
            if (this.beans == null) {
               ClassLoader loader = ParentClassLoaderFinder.Helper.get();
               CompositeBeans mergedModel = new CompositeBeans();

               for(File file : this.findFiles()) {
                  URL url = file.toURI().toURL();
                  if (file.isDirectory()) {
                     FinderFactory.OpenEJBAnnotationFinder finder = new FinderFactory.OpenEJBAnnotationFinder(new FileArchive(loader, file));
                     mergedModel.getManagedClasses().put(url, finder.getAnnotatedClassNames());
                     File beansXml = new File(file, "META-INF/beans.xml");
                     if (beansXml.exists()) {
                        FileInputStream inputStream = new FileInputStream(beansXml);
                        Throwable var10 = null;

                        try {
                           Beans beansModel = ReadDescriptors.readBeans((InputStream)inputStream);
                           mergedModel.mergeClasses(url, beansModel);
                        } catch (Throwable var58) {
                           var10 = var58;
                           throw var58;
                        } finally {
                           if (inputStream != null) {
                              if (var10 != null) {
                                 try {
                                    inputStream.close();
                                 } catch (Throwable var57) {
                                    var10.addSuppressed(var57);
                                 }
                              } else {
                                 inputStream.close();
                              }
                           }

                        }
                     }
                  } else {
                     FinderFactory.OpenEJBAnnotationFinder finder = new FinderFactory.OpenEJBAnnotationFinder(new JarArchive(loader, url));
                     mergedModel.getManagedClasses().put(url, finder.getAnnotatedClassNames());
                     URLClassLoader cl = new URLClassLoader(new URL[]{url});
                     Throwable var67 = null;

                     try {
                        InputStream is = cl.getResourceAsStream("META-INF/beans.xml");
                        Throwable var69 = null;

                        try {
                           if (is != null) {
                              Beans beansModel = ReadDescriptors.readBeans(is);
                              mergedModel.mergeClasses(url, beansModel);
                           }
                        } catch (Throwable var59) {
                           var69 = var59;
                           throw var59;
                        } finally {
                           if (is != null) {
                              if (var69 != null) {
                                 try {
                                    is.close();
                                 } catch (Throwable var56) {
                                    var69.addSuppressed(var56);
                                 }
                              } else {
                                 is.close();
                              }
                           }

                        }
                     } catch (Throwable var61) {
                        var67 = var61;
                        throw var61;
                     } finally {
                        if (cl != null) {
                           if (var67 != null) {
                              try {
                                 cl.close();
                              } catch (Throwable var55) {
                                 var67.addSuppressed(var55);
                              }
                           } else {
                              cl.close();
                           }
                        }

                     }
                  }
               }

               this.beans = mergedModel;
            }
         }
      }
   }

   private Iterable<? extends File> findFiles() {
      Collection<File> files = new LinkedList();
      if (!this.directory) {
         files.add(this.path);
      } else {
         File[] children = this.path.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
               String name = pathname.getName();
               return name.endsWith(".jar") || name.endsWith(".zip") || pathname.isDirectory();
            }
         });
         if (children != null) {
            Collections.addAll(files, children);
         }
      }

      return files;
   }

   public void setPath(String path) throws Exception {
      this.path = (new File(path)).getCanonicalFile();
   }

   public void setDirectory(boolean directory) {
      this.directory = directory;
   }
}
