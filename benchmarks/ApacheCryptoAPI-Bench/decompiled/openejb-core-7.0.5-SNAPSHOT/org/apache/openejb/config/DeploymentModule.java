package org.apache.openejb.config;

import java.io.File;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.openejb.jee.NamedModule;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.URLs;

public interface DeploymentModule {
   String OPENEJB_MODULENAME_USE_HASH = "openejb.modulename.useHash";

   String getModuleId();

   URI getModuleUri();

   ClassLoader getClassLoader();

   String getJarLocation();

   File getFile();

   Map<String, Object> getAltDDs();

   ValidationContext getValidation();

   Set<String> getWatchedResources();

   boolean isStandaloneModule();

   void setStandaloneModule(boolean var1);

   Properties getProperties();

   AppModule appModule();

   public static class ID {
      private final String name;
      private final File location;
      private final URI uri;
      private boolean overriden;
      private final Set<String> locations = new LinkedHashSet();

      public ID(NamedModule vendorDd, NamedModule specDd, String name, File location, URI uri, DeploymentModule module) {
         this.name = this.name(vendorDd, specDd, uri, location, name, module);
         this.location = this.location(location, uri);
         this.uri = this.uri(uri, location, this.name);
         if (location != null) {
            this.locations.add(location.getAbsolutePath());
         }

      }

      public Set<String> getLocations() {
         return this.locations;
      }

      private URI uri(URI uri, File location, String name) {
         if (uri != null) {
            return uri;
         } else {
            return location != null ? location.toURI() : URLs.uri(name);
         }
      }

      private File location(File location, URI uri) {
         if (location != null) {
            return location;
         } else {
            return uri != null && uri.isAbsolute() ? new File(uri) : null;
         }
      }

      private String name(NamedModule vendor, NamedModule spec, URI uri, File location, String name, DeploymentModule module) {
         if (location != null) {
            String systPropName = SystemInstance.get().getOptions().get(location.getName() + ".moduleId", (String)null);
            if (systPropName != null) {
               this.overriden = true;
               return systPropName;
            }
         }

         if (spec != null && spec.getModuleName() != null) {
            return spec.getModuleName().trim();
         } else if (name != null && !name.startsWith("@")) {
            return name;
         } else if (vendor != null && vendor.getModuleName() != null) {
            return vendor.getModuleName().trim();
         } else if (vendor != null && vendor.getId() != null) {
            return vendor.getId().trim();
         } else if (spec != null && spec.getId() != null) {
            return spec.getId().trim();
         } else if (uri != null) {
            return this.stripExtension(uri.getPath());
         } else if (location != null && SystemInstance.get().getOptions().get("openejb.modulename.useHash", false)) {
            return this.moduleName(location) + module.hashCode();
         } else if (location != null) {
            return this.moduleName(location);
         } else {
            return name != null ? name : "@" + module.getClass().getSimpleName() + module.hashCode();
         }
      }

      public boolean isOverriden() {
         return this.overriden;
      }

      private String moduleName(File location) {
         return this.stripExtension(NameFiltering.filter(location).getName());
      }

      private String stripExtension(String name) {
         String[] exts = new String[]{".jar", ".zip", ".ear", ".war", ".rar", ".unpacked"};

         for(String ext : exts) {
            if (name.endsWith(ext)) {
               return name.substring(0, name.length() - ext.length());
            }
         }

         return name;
      }

      public String getName() {
         return this.name.startsWith("@") ? this.name.substring(1) : this.name;
      }

      public File getLocation() {
         return this.location;
      }

      public URI getUri() {
         return this.uri;
      }
   }
}
