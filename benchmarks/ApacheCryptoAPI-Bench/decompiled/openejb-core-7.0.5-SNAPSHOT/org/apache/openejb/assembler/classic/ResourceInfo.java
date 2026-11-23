package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class ResourceInfo extends ServiceInfo {
   public String jndiName = "";
   public String postConstruct;
   public String preDestroy;
   public List<String> postConstructMethods;
   public List<String> preDestroyMethods;
   public String originAppName;
   public List<String> aliases = new ArrayList();
   public List<String> dependsOn = new ArrayList();
}
