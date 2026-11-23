package org.apache.openejb.rest;

import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ThreadLocalServletConfig extends AbstractRestThreadLocalProxy<ServletConfig> implements ServletConfig {
   protected ThreadLocalServletConfig() {
      super(ServletConfig.class);
   }

   public String getServletName() {
      return ((ServletConfig)this.get()).getServletName();
   }

   public ServletContext getServletContext() {
      return ((ServletConfig)this.get()).getServletContext();
   }

   public String getInitParameter(String name) {
      return ((ServletConfig)this.get()).getInitParameter(name);
   }

   public Enumeration<String> getInitParameterNames() {
      return ((ServletConfig)this.get()).getInitParameterNames();
   }
}
