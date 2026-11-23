package org.apache.openejb.config;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.openejb.jee.FacesConfig;
import org.apache.openejb.jee.NamedModule;
import org.apache.openejb.jee.TldTaglib;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.jee.Webservices;
import org.apache.openejb.loader.SystemInstance;
import org.apache.xbean.finder.IAnnotationFinder;

public class WebModule extends Module implements WsModule, RESTModule {
   private final ValidationContext validation;
   private WebApp webApp;
   private Webservices webservices;
   private String host;
   private String contextRoot;
   private final List<TldTaglib> taglibs = new ArrayList();
   private final Set<String> watchedResources = new TreeSet();
   private final List<FacesConfig> facesConfigs = new ArrayList();
   private IAnnotationFinder finder;
   private final Set<String> restClasses = new TreeSet();
   private final Set<String> ejbWebServices = new TreeSet();
   private final Set<String> ejbRestServices = new TreeSet();
   private final Set<String> jaxrsProviders = new TreeSet();
   private final Set<String> restApplications = new TreeSet();
   private final Map<String, Set<String>> jsfAnnotatedClasses = new HashMap();
   private final Map<String, Set<String>> webAnnotatedClasses = new HashMap();
   private final DeploymentModule.ID id;
   private List<URL> urls;
   private List<URL> rarUrls;
   private List<URL> addedUrls;
   private List<URL> scannableUrls;

   public WebModule(WebApp webApp, String contextRoot, ClassLoader classLoader, String jarLocation, String moduleId) {
      this.webApp = webApp;
      File file = jarLocation == null ? null : new File(jarLocation);
      this.id = new DeploymentModule.ID((NamedModule)null, webApp, moduleId, file, (URI)null, this);
      this.validation = new ValidationContext(this);
      if (contextRoot == null) {
         contextRoot = null != jarLocation ? jarLocation.substring(jarLocation.lastIndexOf(File.separator)) : ".";
         if (contextRoot.endsWith(".unpacked")) {
            contextRoot = contextRoot.substring(0, contextRoot.length() - ".unpacked".length());
         }

         if (contextRoot.endsWith(".war")) {
            contextRoot = contextRoot.substring(0, contextRoot.length() - ".war".length());
         }
      }

      while(contextRoot.startsWith("/")) {
         contextRoot = contextRoot.substring(1);
      }

      while(contextRoot.startsWith("\\")) {
         contextRoot = contextRoot.substring(1);
      }

      this.contextRoot = contextRoot;
      this.setClassLoader(classLoader);
      if (webApp != null) {
         webApp.setContextRoot(contextRoot);
      }

      this.host = SystemInstance.get().getProperty(this.id.getName() + ".host", (String)null);
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

   public List<URL> getUrls() {
      return this.urls;
   }

   public void setUrls(List<URL> urls) {
      this.urls = urls;
   }

   public IAnnotationFinder getFinder() {
      return this.finder;
   }

   public void setFinder(IAnnotationFinder finder) {
      this.finder = finder;
   }

   public ValidationContext getValidation() {
      return this.validation;
   }

   public WebApp getWebApp() {
      return this.webApp;
   }

   public void setWebApp(WebApp webApp) {
      this.webApp = webApp;
      if (webApp != null) {
         webApp.setContextRoot(this.contextRoot);
      }

   }

   public Webservices getWebservices() {
      return this.webservices;
   }

   public void setWebservices(Webservices webservices) {
      this.webservices = webservices;
   }

   public String getContextRoot() {
      return this.contextRoot;
   }

   public void setContextRoot(String contextRoot) {
      if (this.webApp != null) {
         this.webApp.setContextRoot(contextRoot);
      }

      this.contextRoot = contextRoot;
   }

   public String getHost() {
      return this.host;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public List<TldTaglib> getTaglibs() {
      return this.taglibs;
   }

   public Set<String> getWatchedResources() {
      return this.watchedResources;
   }

   public List<FacesConfig> getFacesConfigs() {
      return this.facesConfigs;
   }

   public String toString() {
      return "WebModule{moduleId='" + this.id.getName() + '\'' + ", contextRoot='" + this.contextRoot + '\'' + '}';
   }

   public Set<String> getRestClasses() {
      return this.restClasses;
   }

   public Set<String> getRestApplications() {
      return this.restApplications;
   }

   public Set<String> getEjbWebServices() {
      return this.ejbWebServices;
   }

   public Set<String> getEjbRestServices() {
      return this.ejbRestServices;
   }

   public List<URL> getScannableUrls() {
      return this.scannableUrls == null ? Collections.emptyList() : this.scannableUrls;
   }

   public void setScannableUrls(List<URL> scannableUrls) {
      this.scannableUrls = scannableUrls;
   }

   public Map<String, Set<String>> getJsfAnnotatedClasses() {
      return this.jsfAnnotatedClasses;
   }

   public Map<String, Set<String>> getWebAnnotatedClasses() {
      return this.webAnnotatedClasses;
   }

   public Set<String> getJaxrsProviders() {
      return this.jaxrsProviders;
   }

   public List<URL> getRarUrls() {
      return this.rarUrls == null ? Collections.emptyList() : this.rarUrls;
   }

   public void setRarUrls(List<URL> rarUrls) {
      this.rarUrls = rarUrls;
   }

   public List<URL> getAddedUrls() {
      return this.addedUrls;
   }

   public void setAddedUrls(List<URL> addedUrls) {
      this.addedUrls = addedUrls;
   }

   public AppModule appModule() {
      return super.getAppModule();
   }
}
