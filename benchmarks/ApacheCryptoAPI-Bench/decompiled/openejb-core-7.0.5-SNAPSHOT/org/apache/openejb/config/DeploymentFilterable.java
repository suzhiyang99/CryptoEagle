package org.apache.openejb.config;

public interface DeploymentFilterable {
   String DEPLOYMENTS_CLASSPATH_PROPERTY = "openejb.deployments.classpath";
   String SEARCH_CLASSPATH_FOR_DEPLOYMENTS_PROPERTY = "openejb.deployments.classpath";
   String CLASSPATH_INCLUDE = "openejb.deployments.classpath.include";
   String CLASSPATH_EXCLUDE = "openejb.deployments.classpath.exclude";
   String PACKAGE_INCLUDE = "openejb.deployments.package.include";
   String PACKAGE_EXCLUDE = "openejb.deployments.package.exclude";
   String CLASSPATH_REQUIRE_DESCRIPTOR = "openejb.deployments.classpath.require.descriptor";
   String CLASSPATH_FILTER_DESCRIPTORS = "openejb.deployments.classpath.filter.descriptors";
   String CLASSPATH_FILTER_SYSTEMAPPS = "openejb.deployments.classpath.filter.systemapps";
}
