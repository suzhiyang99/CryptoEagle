package org.apache.openejb.config;

import java.io.File;
import java.net.URI;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.Webservices;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.xbean.finder.IAnnotationFinder;

public class EjbModule extends Module implements WsModule {
   private final ValidationContext validation;
   private EjbJar ejbJar;
   private OpenejbJar openejbJar;
   private Webservices webservices;
   private final AtomicReference<IAnnotationFinder> finder;
   private final Set<String> watchedResources;
   private Beans beans;
   private boolean webapp;
   private ClientModule clientModule;
   private DeploymentModule.ID id;

   public EjbModule(EjbJar ejbJar) {
      this(Thread.currentThread().getContextClassLoader(), (String)null, ejbJar, (OpenejbJar)null);
   }

   public EjbModule(EjbJar ejbJar, OpenejbJar openejbJar) {
      this(Thread.currentThread().getContextClassLoader(), (String)null, ejbJar, openejbJar);
   }

   public EjbModule(ClassLoader classLoader, String moduleId, String jarURI, EjbJar ejbJar, OpenejbJar openejbJar) {
      this.finder = new AtomicReference();
      this.watchedResources = new TreeSet();
      if (classLoader == null) {
         throw new NullPointerException("classLoader is null");
      } else {
         this.setClassLoader(classLoader);
         this.ejbJar = ejbJar;
         this.openejbJar = openejbJar;
         File file = null;
         if (jarURI != null) {
            file = new File(jarURI);
         }

         this.id = new DeploymentModule.ID(openejbJar, ejbJar, moduleId, file, (URI)null, this);
         if (this.id.isOverriden() && ejbJar != null) {
            ejbJar.setModuleName(this.id.getName());
         }

         this.validation = new ValidationContext(this);
      }
   }

   public EjbModule(ClassLoader classLoader, String jarURI, EjbJar ejbJar, OpenejbJar openejbJar) {
      this(classLoader, (String)null, jarURI, ejbJar, openejbJar);
   }

   public Beans getBeans() {
      return this.beans;
   }

   public void setBeans(Beans beans) {
      this.beans = beans;
   }

   public IAnnotationFinder getFinder() {
      return (IAnnotationFinder)this.finder.get();
   }

   public void setFinder(IAnnotationFinder finder) {
      this.finder.set(finder);
   }

   public EjbModule finder(IAnnotationFinder finder) {
      this.finder.set(finder);
      return this;
   }

   public ClientModule getClientModule() {
      return this.clientModule;
   }

   public void setClientModule(ClientModule clientModule) {
      this.clientModule = clientModule;
      if (clientModule != null) {
         clientModule.setEjbModuleGenerated(true);
         clientModule.setFinderReference(this.finder);
      }

   }

   public ValidationContext getValidation() {
      return this.validation;
   }

   public EjbJar getEjbJar() {
      return this.ejbJar;
   }

   public void setEjbJar(EjbJar ejbJar) {
      this.ejbJar = ejbJar;
   }

   public String getJarLocation() {
      return this.id.getLocation() != null ? this.id.getLocation().getAbsolutePath() : null;
   }

   public void setJarLocation(String jarLocation) {
      this.id = new DeploymentModule.ID(this.openejbJar, this.ejbJar, this.id.getName(), new File(jarLocation), this.id.getUri(), this);
   }

   public String getModuleId() {
      return this.id.getName();
   }

   public File getFile() {
      return this.id.getLocation();
   }

   public void setModuleId(String moduleId) {
      if (this.openejbJar == null) {
         this.openejbJar = new OpenejbJar();
      }

      this.openejbJar.setModuleName(moduleId);
      this.id = new DeploymentModule.ID(this.openejbJar, this.ejbJar, moduleId, this.id.getLocation(), this.id.getUri(), this);
   }

   public URI getModuleUri() {
      return this.id.getUri();
   }

   public void setModuleUri(URI moduleUri) {
      this.id = new DeploymentModule.ID(this.openejbJar, this.ejbJar, this.id.getName(), this.id.getLocation(), moduleUri, this);
   }

   public OpenejbJar getOpenejbJar() {
      return this.openejbJar;
   }

   public void setOpenejbJar(OpenejbJar openejbJar) {
      this.openejbJar = openejbJar;
   }

   public Webservices getWebservices() {
      return this.webservices;
   }

   public void setWebservices(Webservices webservices) {
      this.webservices = webservices;
   }

   public Set<String> getWatchedResources() {
      return this.watchedResources;
   }

   public boolean isWebapp() {
      return this.webapp;
   }

   public void setWebapp(boolean webapp) {
      this.webapp = webapp;
   }

   public EjbModule withCdi() {
      if (this.beans == null) {
         this.beans = new Beans();
      }

      return this;
   }

   public String toString() {
      return "EjbModule{moduleId='" + this.id.getName() + '\'' + '}';
   }

   public AppModule appModule() {
      return super.getAppModule();
   }
}
