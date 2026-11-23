package org.apache.openejb.config;

import org.apache.openejb.OpenEJBException;

public interface DynamicDeployer {
   AppModule deploy(AppModule var1) throws OpenEJBException;
}
