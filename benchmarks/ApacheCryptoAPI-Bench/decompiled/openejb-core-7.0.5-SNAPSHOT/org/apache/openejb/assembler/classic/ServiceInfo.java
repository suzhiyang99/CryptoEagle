package org.apache.openejb.assembler.classic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ServiceInfo extends InfoObject {
   public String service;
   public List<String> types = new ArrayList();
   public String description;
   public String id;
   public String displayName;
   public String className;
   public String codebase;
   public URI[] classpath;
   public String classpathAPI;
   public Properties properties;
   public final List<String> constructorArgs = new ArrayList();
   public Properties unsetProperties;
   public String factoryMethod;
}
