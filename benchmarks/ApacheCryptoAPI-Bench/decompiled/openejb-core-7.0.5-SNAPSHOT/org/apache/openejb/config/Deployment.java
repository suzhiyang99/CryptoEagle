package org.apache.openejb.config;

import java.io.File;
import java.util.Properties;
import java.util.Set;

public interface Deployment {
   void release();

   Properties getProperties();

   Set<String> list(String var1, String var2, Set<String> var3) throws DeploymentException;

   Set<String> deploy(Set<String> var1, File var2) throws DeploymentException;

   Set<String> start(Set<String> var1) throws DeploymentException;

   Set<String> stop(Set<String> var1) throws DeploymentException;

   Set<String> restart(Set<String> var1) throws DeploymentException;

   Set<String> undeploy(Set<String> var1) throws DeploymentException;
}
