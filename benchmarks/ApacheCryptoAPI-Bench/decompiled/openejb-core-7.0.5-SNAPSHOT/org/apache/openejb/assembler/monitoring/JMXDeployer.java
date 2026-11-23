package org.apache.openejb.assembler.monitoring;

import java.util.Collection;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.openejb.api.jmx.Description;
import org.apache.openejb.api.jmx.MBean;
import org.apache.openejb.api.jmx.ManagedAttribute;
import org.apache.openejb.api.jmx.ManagedOperation;
import org.apache.openejb.assembler.Deployer;
import org.apache.openejb.assembler.DeployerEjb;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.core.LocalInitialContextFactory;

@MBean
@Description("OpenEJB Deployer")
public class JMXDeployer {
   @ManagedOperation
   @Description("Deploy the specified application")
   public String deploy(String location) {
      try {
         deployer().deploy(location);
         return "OK";
      } catch (Exception e) {
         return "ERR:" + e.getMessage();
      }
   }

   @ManagedOperation
   @Description("Undeploy the specified application")
   public String undeploy(String moduleId) {
      try {
         deployer().undeploy(moduleId);
         return "OK";
      } catch (Exception e) {
         return "ERR:" + e.getMessage();
      }
   }

   @ManagedAttribute
   @Description("List available applications")
   public String[] getDeployedApplications() {
      try {
         Collection<AppInfo> apps = deployer().getDeployedApps();
         String[] appsNames = new String[apps.size()];
         int i = 0;

         for(AppInfo info : apps) {
            appsNames[i++] = info.path;
         }

         return appsNames;
      } catch (Exception e) {
         return new String[]{"ERR:" + e.getMessage()};
      }
   }

   @ManagedOperation
   @Description("Reload the specified application")
   public String reload(String moduleId) {
      try {
         Collection<AppInfo> apps = deployer().getDeployedApps();
         boolean found = false;

         for(AppInfo info : apps) {
            if (info.path.equals(moduleId)) {
               found = true;
               break;
            }
         }

         if (found) {
            deployer().reload(moduleId);
            return "OK";
         } else {
            return "NOT FOUND";
         }
      } catch (Exception e) {
         return "ERR:" + e.getMessage();
      }
   }

   private static Deployer deployer() throws NamingException {
      Properties p = new Properties();
      p.setProperty("java.naming.factory.initial", LocalInitialContextFactory.class.getName());
      ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(DeployerEjb.class.getClassLoader());

      Deployer var2;
      try {
         var2 = (Deployer)(new InitialContext(p)).lookup("openejb/DeployerBusinessRemote");
      } finally {
         Thread.currentThread().setContextClassLoader(oldCl);
      }

      return var2;
   }
}
