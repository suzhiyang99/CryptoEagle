package org.apache.openejb.config.rules;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.DeploymentModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.WebModule;
import org.apache.openejb.util.CollectionsUtil;
import org.apache.xbean.finder.ResourceFinder;

public class CheckDescriptorLocation extends ValidationBase {
   public void validate(AppModule appModule) {
      List<String> validated = new ArrayList();

      for(WebModule webModule : CollectionsUtil.safe(appModule.getWebModules())) {
         validated.add(webModule.getModuleId());
         this.validateWebModule(webModule);
      }

      for(EjbModule ejbModule : CollectionsUtil.safe(appModule.getEjbModules())) {
         if (!validated.contains(ejbModule.getModuleId())) {
            this.validateEjbModule(ejbModule);
         }
      }

   }

   private void validateWebModule(DeploymentModule webModule) {
      this.module = webModule;
      List<String> descriptorsToSearch = Arrays.asList("beans.xml", "ejb-jar.xml", "faces-config.xml");
      File file = webModule.getFile();
      if (file != null) {
         try {
            URL rootOfArchive = file.toURI().toURL();
            URL metaInf = new URL(rootOfArchive.toExternalForm() + "META-INF/");
            Map<String, URL> incorrectlyLocatedDescriptors = retrieveDescriptors(descriptorsToSearch, rootOfArchive, metaInf);
            this.warnIncorrectLocationOfDescriptors(incorrectlyLocatedDescriptors, "WEB-INF");
         } catch (MalformedURLException var7) {
         }
      }

   }

   public void validateEjbModule(DeploymentModule deploymentModule) {
      this.module = deploymentModule;
      List<String> descriptorsToSearch = Arrays.asList("beans.xml", "ejb-jar.xml", "openejb-jar.xml", "env-entries.properties");
      File file = deploymentModule.getFile();
      if (file != null) {
         try {
            URL rootOfArchive = file.toURI().toURL();
            Map<String, URL> incorrectlyLocatedDescriptors = retrieveDescriptors(descriptorsToSearch, rootOfArchive);
            this.warnIncorrectLocationOfDescriptors(incorrectlyLocatedDescriptors, "META-INF");
         } catch (MalformedURLException var6) {
         }
      }

   }

   private static Map<String, URL> retrieveDescriptors(List<String> descriptorsToSearch, URL... locationsToSearch) {
      Map<String, URL> descriptorAndWrongLocation = new HashMap();
      ResourceFinder finder = new ResourceFinder(locationsToSearch);

      for(String descriptor : descriptorsToSearch) {
         URL resource = finder.getResource(descriptor);
         if (resource != null) {
            descriptorAndWrongLocation.put(descriptor, resource);
         }
      }

      return descriptorAndWrongLocation;
   }

   private void warnIncorrectLocationOfDescriptors(Map<String, URL> descriptorsPlacedInWrongLocation, String expectedLocation) {
      for(Map.Entry<String, URL> map : descriptorsPlacedInWrongLocation.entrySet()) {
         this.warn(this.module.toString(), "descriptor.incorrectLocation", new Object[]{((URL)map.getValue()).toExternalForm(), expectedLocation});
      }

   }
}
