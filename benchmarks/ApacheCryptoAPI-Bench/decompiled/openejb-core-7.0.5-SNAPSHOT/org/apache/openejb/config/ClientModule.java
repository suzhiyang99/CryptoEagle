package org.apache.openejb.config;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.openejb.jee.ApplicationClient;
import org.apache.openejb.jee.NamedModule;
import org.apache.xbean.finder.IAnnotationFinder;

public class ClientModule extends Module implements DeploymentModule {
   private final ValidationContext validation;
   private ApplicationClient applicationClient;
   private String mainClass;
   private boolean ejbModuleGenerated;
   private AtomicReference<IAnnotationFinder> finder;
   private final Set<String> localClients = new HashSet();
   private final Set<String> remoteClients = new HashSet();
   private DeploymentModule.ID id;
   private final Set<String> watchedResources = new TreeSet();

   public ClientModule(ApplicationClient applicationClient, ClassLoader classLoader, String jarLocation, String mainClass, String moduleId) {
      this.applicationClient = applicationClient;
      this.setClassLoader(classLoader);
      this.mainClass = mainClass;
      File file = jarLocation == null ? null : new File(jarLocation);
      this.id = new DeploymentModule.ID((NamedModule)null, applicationClient, moduleId, file, (URI)null, this);
      this.validation = new ValidationContext(this);
   }

   public boolean isEjbModuleGenerated() {
      return this.ejbModuleGenerated;
   }

   public void setEjbModuleGenerated(boolean ejbModuleGenerated) {
      this.ejbModuleGenerated = ejbModuleGenerated;
   }

   public IAnnotationFinder getFinder() {
      return this.finder != null ? (IAnnotationFinder)this.finder.get() : null;
   }

   public void setFinderReference(AtomicReference<IAnnotationFinder> finder) {
      this.finder = finder;
   }

   public AtomicReference<IAnnotationFinder> getFinderReference() {
      return this.finder;
   }

   public ValidationContext getValidation() {
      return this.validation;
   }

   public String getJarLocation() {
      return this.id.getLocation() != null ? this.id.getLocation().getAbsolutePath() : null;
   }

   public void setJarLocation(String jarLocation) {
      this.id = new DeploymentModule.ID((NamedModule)null, this.applicationClient, this.id.getName(), new File(jarLocation), this.id.getUri(), this);
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

   public ApplicationClient getApplicationClient() {
      return this.applicationClient;
   }

   public void setApplicationClient(ApplicationClient applicationClient) {
      this.applicationClient = applicationClient;
   }

   public Set<String> getLocalClients() {
      return this.localClients;
   }

   public Set<String> getRemoteClients() {
      return this.remoteClients;
   }

   public String getMainClass() {
      return this.mainClass;
   }

   public void setMainClass(String mainClass) {
      this.mainClass = mainClass;
   }

   public Set<String> getWatchedResources() {
      return this.watchedResources;
   }

   public String toString() {
      return "ClientModule{moduleId='" + this.id.getName() + '\'' + ", mainClass='" + this.mainClass + '\'' + '}';
   }

   public AppModule appModule() {
      return super.getAppModule();
   }
}
