package org.spark_project.jetty.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class ResourceCollection extends Resource {
   private static final Logger LOG = Log.getLogger(ResourceCollection.class);
   private Resource[] _resources;

   public ResourceCollection() {
      this._resources = new Resource[0];
   }

   public ResourceCollection(Resource... resources) {
      List<Resource> list = new ArrayList();

      for(Resource r : resources) {
         if (r != null) {
            if (r instanceof ResourceCollection) {
               for(Resource r2 : ((ResourceCollection)r).getResources()) {
                  list.add(r2);
               }
            } else {
               list.add(r);
            }
         }
      }

      this._resources = (Resource[])list.toArray(new Resource[list.size()]);

      for(Resource r : this._resources) {
         if (!r.exists() || !r.isDirectory()) {
            throw new IllegalArgumentException(r + " is not an existing directory.");
         }
      }

   }

   public ResourceCollection(String[] resources) {
      this._resources = new Resource[resources.length];

      try {
         for(int i = 0; i < resources.length; ++i) {
            this._resources[i] = Resource.newResource(resources[i]);
            if (!this._resources[i].exists() || !this._resources[i].isDirectory()) {
               throw new IllegalArgumentException(this._resources[i] + " is not an existing directory.");
            }
         }

      } catch (IllegalArgumentException e) {
         throw e;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public ResourceCollection(String csvResources) {
      this.setResourcesAsCSV(csvResources);
   }

   public Resource[] getResources() {
      return this._resources;
   }

   public void setResources(Resource[] resources) {
      this._resources = resources != null ? resources : new Resource[0];
   }

   public void setResourcesAsCSV(String csvResources) {
      StringTokenizer tokenizer = new StringTokenizer(csvResources, ",;");
      int len = tokenizer.countTokens();
      if (len == 0) {
         throw new IllegalArgumentException("ResourceCollection@setResourcesAsCSV(String)  argument must be a string containing one or more comma-separated resource strings.");
      } else {
         List<Resource> resources = new ArrayList();

         try {
            while(tokenizer.hasMoreTokens()) {
               Resource resource = Resource.newResource(tokenizer.nextToken().trim());
               if (resource.exists() && resource.isDirectory()) {
                  resources.add(resource);
               } else {
                  LOG.warn(" !exist " + resource);
               }
            }
         } catch (Exception e) {
            throw new RuntimeException(e);
         }

         this._resources = (Resource[])resources.toArray(new Resource[resources.size()]);
      }
   }

   public Resource addPath(String path) throws IOException, MalformedURLException {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else if (path == null) {
         throw new MalformedURLException();
      } else if (path.length() != 0 && !"/".equals(path)) {
         Resource resource = null;
         ArrayList<Resource> resources = null;

         int i;
         for(i = 0; i < this._resources.length; ++i) {
            resource = this._resources[i].addPath(path);
            if (resource.exists()) {
               if (!resource.isDirectory()) {
                  return resource;
               }
               break;
            }
         }

         ++i;

         for(; i < this._resources.length; ++i) {
            Resource r = this._resources[i].addPath(path);
            if (r.exists() && r.isDirectory()) {
               if (resources == null) {
                  resources = new ArrayList();
               }

               if (resource != null) {
                  resources.add(resource);
                  resource = null;
               }

               resources.add(r);
            }
         }

         if (resource != null) {
            return resource;
         } else if (resources != null) {
            return new ResourceCollection((Resource[])resources.toArray(new Resource[resources.size()]));
         } else {
            return null;
         }
      } else {
         return this;
      }
   }

   protected Object findResource(String path) throws IOException, MalformedURLException {
      Resource resource = null;
      ArrayList<Resource> resources = null;

      int i;
      for(i = 0; i < this._resources.length; ++i) {
         resource = this._resources[i].addPath(path);
         if (resource.exists()) {
            if (!resource.isDirectory()) {
               return resource;
            }
            break;
         }
      }

      ++i;

      for(; i < this._resources.length; ++i) {
         Resource r = this._resources[i].addPath(path);
         if (r.exists() && r.isDirectory()) {
            if (resource != null) {
               resources = new ArrayList();
               resources.add(resource);
            }

            resources.add(r);
         }
      }

      if (resource != null) {
         return resource;
      } else if (resources != null) {
         return resources;
      } else {
         return null;
      }
   }

   public boolean delete() throws SecurityException {
      throw new UnsupportedOperationException();
   }

   public boolean exists() {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         return true;
      }
   }

   public File getFile() throws IOException {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         for(Resource r : this._resources) {
            File f = r.getFile();
            if (f != null) {
               return f;
            }
         }

         return null;
      }
   }

   public InputStream getInputStream() throws IOException {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         for(Resource r : this._resources) {
            InputStream is = r.getInputStream();
            if (is != null) {
               return is;
            }
         }

         return null;
      }
   }

   public ReadableByteChannel getReadableByteChannel() throws IOException {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         for(Resource r : this._resources) {
            ReadableByteChannel channel = r.getReadableByteChannel();
            if (channel != null) {
               return channel;
            }
         }

         return null;
      }
   }

   public String getName() {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         for(Resource r : this._resources) {
            String name = r.getName();
            if (name != null) {
               return name;
            }
         }

         return null;
      }
   }

   public URL getURL() {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         for(Resource r : this._resources) {
            URL url = r.getURL();
            if (url != null) {
               return url;
            }
         }

         return null;
      }
   }

   public boolean isDirectory() {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         return true;
      }
   }

   public long lastModified() {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         for(Resource r : this._resources) {
            long lm = r.lastModified();
            if (lm != -1L) {
               return lm;
            }
         }

         return -1L;
      }
   }

   public long length() {
      return -1L;
   }

   public String[] list() {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         HashSet<String> set = new HashSet();

         for(Resource r : this._resources) {
            for(String s : r.list()) {
               set.add(s);
            }
         }

         String[] result = (String[])set.toArray(new String[set.size()]);
         Arrays.sort(result);
         return result;
      }
   }

   public void close() {
      if (this._resources == null) {
         throw new IllegalStateException("*resources* not set.");
      } else {
         for(Resource r : this._resources) {
            r.close();
         }

      }
   }

   public boolean renameTo(Resource dest) throws SecurityException {
      throw new UnsupportedOperationException();
   }

   public void copyTo(File destination) throws IOException {
      int r = this._resources.length;

      while(r-- > 0) {
         this._resources[r].copyTo(destination);
      }

   }

   public String toString() {
      return this._resources == null ? "[]" : String.valueOf(Arrays.asList(this._resources));
   }

   public boolean isContainedIn(Resource r) throws MalformedURLException {
      return false;
   }
}
