package org.apache.openejb.assembler.classic;

import java.util.Map;
import java.util.Set;

public interface WebAppBuilder {
   void deployWebApps(AppInfo var1, ClassLoader var2) throws Exception;

   void undeployWebApps(AppInfo var1) throws Exception;

   Map<ClassLoader, Map<String, Set<String>>> getJsfClasses();
}
