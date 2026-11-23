package org.apache.openejb.config.sys;

import java.util.Map;
import java.util.Properties;

public final class MapFactory {
   private Properties props = new Properties();

   public Map<?, ?> create() {
      return this.props;
   }

   public Properties getProps() {
      return this.props;
   }
}
