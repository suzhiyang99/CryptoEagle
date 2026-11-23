package org.apache.openejb.config;

public enum RequireDescriptors {
   EJB,
   CLIENT;

   public static final String PROPERTY = "openejb.deployments.classpath.require.descriptor";
}
