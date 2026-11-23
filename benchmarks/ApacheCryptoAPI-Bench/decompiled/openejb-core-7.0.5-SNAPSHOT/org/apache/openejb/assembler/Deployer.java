package org.apache.openejb.assembler;

import java.util.Collection;
import java.util.Properties;
import org.apache.openejb.NoSuchApplicationException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.UndeployException;
import org.apache.openejb.assembler.classic.AppInfo;

public interface Deployer {
   String FILENAME = "filename";
   String ALT_DD = "altDD";

   String getUniqueFile();

   Collection<AppInfo> getDeployedApps();

   AppInfo deploy(String var1) throws OpenEJBException;

   AppInfo deploy(Properties var1) throws OpenEJBException;

   AppInfo deploy(String var1, Properties var2) throws OpenEJBException;

   void undeploy(String var1) throws UndeployException, NoSuchApplicationException;

   void reload(String var1);
}
