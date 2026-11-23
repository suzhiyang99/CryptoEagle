package org.spark_project.jetty.servlet.jmx;

import org.spark_project.jetty.jmx.ObjectMBean;
import org.spark_project.jetty.servlet.ServletMapping;

public class ServletMappingMBean extends ObjectMBean {
   public ServletMappingMBean(Object managedObject) {
      super(managedObject);
   }

   public String getObjectNameBasis() {
      if (this._managed != null && this._managed instanceof ServletMapping) {
         ServletMapping mapping = (ServletMapping)this._managed;
         String name = mapping.getServletName();
         if (name != null) {
            return name;
         }
      }

      return super.getObjectNameBasis();
   }
}
