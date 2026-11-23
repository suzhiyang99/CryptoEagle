package org.apache.openejb.util;

import java.util.Properties;

public class PropertiesService {
   private final Properties props = new Properties();
   private boolean passOn = true;
   private boolean queryEnvOnInit = true;

   public PropertiesService() {
      if (this.queryEnvOnInit) {
         this.props.putAll(JavaSecurityManagers.getSystemProperties());
      }

   }

   public String setProperty(String name, String value) {
      if (this.passOn) {
         JavaSecurityManagers.setSystemProperty(name, value);
      }

      return (String)this.props.setProperty(name, value);
   }

   public String getProperty(String name) {
      return (String)this.props.get(name);
   }

   public boolean isSet(String name) {
      return this.props.containsKey(name);
   }

   public void putAll(Properties props) {
      props.putAll(props);
   }

   public Properties getProperties() {
      return this.props;
   }

   public boolean isPassOn() {
      return this.passOn;
   }

   public void setPassOn(boolean passOn) {
      this.passOn = passOn;
   }

   public boolean isQueryEnvOnInit() {
      return this.queryEnvOnInit;
   }

   public void setQueryEnvOnInit(boolean queryEnvOnInit) {
      this.queryEnvOnInit = queryEnvOnInit;
   }
}
