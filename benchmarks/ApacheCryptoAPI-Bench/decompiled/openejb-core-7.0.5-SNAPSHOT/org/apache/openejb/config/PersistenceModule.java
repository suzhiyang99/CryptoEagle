package org.apache.openejb.config;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.util.URLs;

public class PersistenceModule implements DeploymentModule {
   private String rootUrl;
   private Persistence persistence;
   private final Set<String> watchedResources;
   private URI uri;
   private final AppModule appModule;

   public PersistenceModule(String rootUrl, Persistence persistence) {
      this((AppModule)null, rootUrl, persistence);
   }

   public PersistenceModule(AppModule module, String rootUrl, Persistence persistence) {
      this.watchedResources = new TreeSet();
      this.setRootUrl(rootUrl);
      this.appModule = module;
      this.persistence = persistence;
   }

   public String getRootUrl() {
      return this.rootUrl;
   }

   public void setRootUrl(String rootUrl) {
      this.rootUrl = rootUrl;
      this.uri = rootUrl == null ? null : URLs.uri(rootUrl);
   }

   public Persistence getPersistence() {
      return this.persistence;
   }

   public void setPersistence(Persistence persistence) {
      this.persistence = persistence;
   }

   public Set<String> getWatchedResources() {
      return this.watchedResources;
   }

   public Map<String, Object> getAltDDs() {
      return null;
   }

   public ClassLoader getClassLoader() {
      return null;
   }

   public String getJarLocation() {
      return null;
   }

   public String getModuleId() {
      return null;
   }

   public URI getModuleUri() {
      return this.uri;
   }

   public File getFile() {
      return null;
   }

   public ValidationContext getValidation() {
      return null;
   }

   public Properties getProperties() {
      return null;
   }

   public AppModule appModule() {
      return this.appModule;
   }

   public String toString() {
      return "PersistenceModule{rootUrl='" + this.rootUrl + '\'' + '}';
   }

   public boolean isStandaloneModule() {
      return false;
   }

   public void setStandaloneModule(boolean isStandalone) {
   }
}
