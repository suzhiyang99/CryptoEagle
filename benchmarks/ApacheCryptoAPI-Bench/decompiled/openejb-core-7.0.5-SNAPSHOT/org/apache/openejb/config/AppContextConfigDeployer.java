package org.apache.openejb.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.sys.SaxAppCtxConfig;
import org.apache.openejb.loader.IO;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AppContextConfigDeployer implements DynamicDeployer {
   private static final String CONFIG_NAME = "app-ctx.xml";
   private final EnvEntriesPropertiesDeployer envEntriesDeployer;
   private final BeanProperties beanPropertiesDeployer;

   public AppContextConfigDeployer(EnvEntriesPropertiesDeployer envEntriesPropertiesDeployer, BeanProperties beanProperties) {
      this.envEntriesDeployer = envEntriesPropertiesDeployer;
      this.beanPropertiesDeployer = beanProperties;
   }

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      Collection<DeploymentModule> deploymentModule = appModule.getDeploymentModule();
      deploymentModule.add(appModule);
      Set<String> alreadyParsed = new HashSet();

      for(DeploymentModule module : deploymentModule) {
         Object o = module.getAltDDs().get("app-ctx.xml");
         if (o instanceof URL) {
            URL url = (URL)o;
            if (alreadyParsed.add(url.toExternalForm())) {
               this.configure(appModule, url);
            }
         } else if (o != null) {
            throw new OpenEJBException("Unknown app-ctx.xml type: " + o.getClass().getName());
         }
      }

      alreadyParsed.clear();
      return appModule;
   }

   private void configure(AppModule appModule, URL url) throws OpenEJBException {
      InputStream is = null;

      try {
         is = IO.read(url);
         SaxAppCtxConfig.parse(appModule, new InputSource(is), this.envEntriesDeployer, this.beanPropertiesDeployer);
      } catch (SAXException e) {
         throw new OpenEJBException("can't parse " + url.toExternalForm(), e);
      } catch (ParserConfigurationException e) {
         throw new OpenEJBException("can't configure the parser for " + url.toExternalForm(), e);
      } catch (IOException e) {
         throw new OpenEJBException("can't read " + url.toExternalForm(), e);
      } finally {
         IO.close(is);
      }

   }
}
