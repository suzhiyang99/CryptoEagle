package org.apache.openejb.assembler.classic;

public interface DeploymentListener {
   void afterApplicationCreated(AppInfo var1);

   void beforeApplicationDestroyed(AppInfo var1);
}
