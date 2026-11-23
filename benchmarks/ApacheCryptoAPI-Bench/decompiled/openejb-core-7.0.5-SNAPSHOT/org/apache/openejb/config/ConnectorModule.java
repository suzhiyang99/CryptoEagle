package org.apache.openejb.config;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.NamedModule;
import org.apache.xbean.finder.IAnnotationFinder;

public class ConnectorModule extends Module implements DeploymentModule {
   private final ValidationContext validation;
   private Connector connector;
   private final List<URL> libraries;
   private final Set<String> watchedResources;
   private final DeploymentModule.ID id;
   private IAnnotationFinder finder;

   public ConnectorModule(Connector connector) {
      this(connector, Thread.currentThread().getContextClassLoader(), (String)null, (String)null);
   }

   public ConnectorModule(Connector connector, ClassLoader classLoader, String jarLocation, String moduleId) {
      this.libraries = new ArrayList();
      this.watchedResources = new TreeSet();
      this.connector = connector;
      this.setClassLoader(classLoader);
      File file = jarLocation == null ? null : new File(jarLocation);
      this.id = new DeploymentModule.ID((NamedModule)null, connector, moduleId, file, (URI)null, this);
      this.validation = new ValidationContext(this);
   }

   public ValidationContext getValidation() {
      return this.validation;
   }

   public String getJarLocation() {
      return this.id.getLocation() != null ? this.id.getLocation().getAbsolutePath() : null;
   }

   public String getModuleId() {
      return this.id.getName();
   }

   public File getFile() {
      return this.id.getLocation();
   }

   public URI getModuleUri() {
      return this.id.getUri();
   }

   public Connector getConnector() {
      return this.connector;
   }

   public void setConnector(Connector connector) {
      this.connector = connector;
   }

   public List<URL> getLibraries() {
      return this.libraries;
   }

   public Set<String> getWatchedResources() {
      return this.watchedResources;
   }

   public AppModule appModule() {
      return super.getAppModule();
   }

   public String toString() {
      return "ConnectorModule{moduleId='" + this.id.getName() + '\'' + '}';
   }

   public IAnnotationFinder getFinder() {
      return this.finder;
   }

   public void setFinder(IAnnotationFinder finder) {
      this.finder = finder;
   }
}
