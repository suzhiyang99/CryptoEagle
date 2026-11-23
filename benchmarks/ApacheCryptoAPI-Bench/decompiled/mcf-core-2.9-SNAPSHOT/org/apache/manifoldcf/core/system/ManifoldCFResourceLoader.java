package org.apache.manifoldcf.core.system;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class ManifoldCFResourceLoader {
   public static final String _rcsid = "@(#)$Id: ManifoldCFResourceLoader.java 988245 2010-08-23 18:39:35Z kwright $";
   protected ClassLoader parent;
   protected ClassLoader classLoader = null;
   protected ArrayList currentClasspath = new ArrayList();

   public ManifoldCFResourceLoader(ClassLoader parent) throws ManifoldCFException {
      this.parent = parent;
   }

   public synchronized void setClassPath(ArrayList libdirList) throws ManifoldCFException {
      if (this.currentClasspath.size() > 0) {
         this.currentClasspath.clear();
         this.classLoader = null;
      }

      int i = 0;

      while(i < libdirList.size()) {
         File dir = (File)libdirList.get(i++);
         this.addToClassPath(dir, (FileFilter)null);
      }

   }

   public synchronized void clearClassPath() {
      if (this.currentClasspath.size() != 0) {
         this.currentClasspath.clear();
         this.classLoader = null;
      }
   }

   public synchronized void addToClassPath(final File file) throws ManifoldCFException {
      if (file.canRead()) {
         this.addDirsToClassPath(new File[]{file.getParentFile()}, new FileFilter[]{new FileFilter() {
            public boolean accept(File pathname) {
               return pathname.equals(file);
            }
         }});
      } else {
         throw new ManifoldCFException("Path '" + file.toString() + "' does not exist or is not readable");
      }
   }

   public synchronized void addToClassPath(File dir, FileFilter filter) throws ManifoldCFException {
      this.addDirsToClassPath(new File[]{dir}, new FileFilter[]{filter});
   }

   public synchronized ClassLoader getClassLoader() throws ManifoldCFException {
      if (this.classLoader == null) {
         if (this.currentClasspath.size() == 0) {
            this.classLoader = this.parent;
         } else {
            URL[] elements = new URL[this.currentClasspath.size()];

            for(int j = 0; j < this.currentClasspath.size(); ++j) {
               try {
                  URL element = ((File)this.currentClasspath.get(j)).toURI().normalize().toURL();
                  elements[j] = element;
               } catch (MalformedURLException e) {
                  throw new ManifoldCFException(e.getMessage(), e);
               }
            }

            this.classLoader = URLClassLoader.newInstance(elements, this.parent);
         }
      }

      return this.classLoader;
   }

   public Class findClass(String cname) throws ClassNotFoundException, ManifoldCFException {
      return Class.forName(cname, true, this.getClassLoader());
   }

   protected void addDirsToClassPath(File[] baseList, FileFilter[] filterList) throws ManifoldCFException {
      for(int i = 0; i < baseList.length; ++i) {
         File base = baseList[i];
         FileFilter filter;
         if (filterList != null) {
            filter = filterList[i];
         } else {
            filter = null;
         }

         if (!base.canRead() || !base.isDirectory()) {
            throw new ManifoldCFException("Supposed directory '" + base.toString() + "' is either not a directory, or is unreadable.");
         }

         File[] files = base.listFiles(filter);
         if (files != null && files.length > 0) {
            for(int j = 0; j < files.length; this.classLoader = null) {
               File file = files[j++];
               this.currentClasspath.add(file);
            }
         }
      }

   }
}
