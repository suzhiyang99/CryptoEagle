package org.apache.openejb.config;

import org.apache.openejb.jee.Webservices;

public interface WsModule extends DeploymentModule {
   Webservices getWebservices();

   void setWebservices(Webservices var1);
}
