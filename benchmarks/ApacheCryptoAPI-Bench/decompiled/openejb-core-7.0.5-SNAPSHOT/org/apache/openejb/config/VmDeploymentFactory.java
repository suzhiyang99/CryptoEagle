package org.apache.openejb.config;

import java.net.URI;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.apache.openejb.util.URLs;

public class VmDeploymentFactory implements DeploymentFactory {
   public static final String URI_SCHEME = "openejb";

   public String getDisplayName() {
      return "OpenEJB - VM";
   }

   public String getProductVersion() {
      return "3.1.1";
   }

   public boolean handlesURI(String uri) {
      URI fullUri = URLs.uri(uri);
      return "openejb".equals(fullUri.getScheme());
   }

   public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
      if (!this.handlesURI(uri)) {
         throw new DeploymentManagerCreationException("Invalid URI: " + uri);
      } else {
         VmDeploymentManager deploymentManager = new VmDeploymentManager();
         deploymentManager.release();
         return deploymentManager;
      }
   }

   public DeploymentManager getDeploymentManager(String uri, String username, String password) throws DeploymentManagerCreationException {
      return new VmDeploymentManager();
   }
}
