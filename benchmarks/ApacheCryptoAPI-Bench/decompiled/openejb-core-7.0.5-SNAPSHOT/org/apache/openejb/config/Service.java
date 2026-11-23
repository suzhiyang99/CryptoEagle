package org.apache.openejb.config;

import java.util.Properties;

public interface Service {
   Properties getProperties();

   String getId();

   void setId(String var1);

   String getJar();

   void setJar(String var1);

   String getProvider();

   void setProvider(String var1);

   String getType();

   void setType(String var1);

   String getClassName();

   String getConstructor();

   String getFactoryName();

   void setClasspath(String var1);

   String getClasspath();

   String getClasspathAPI();

   String getPropertiesProvider();

   String getTemplate();
}
