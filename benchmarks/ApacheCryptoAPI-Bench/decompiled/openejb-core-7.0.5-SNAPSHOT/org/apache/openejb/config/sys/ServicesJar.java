package org.apache.openejb.config.sys;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"serviceProvider"}
)
@XmlRootElement(
   name = "ServiceJar"
)
public class ServicesJar {
   @XmlElement(
      name = "ServiceProvider",
      required = true
   )
   protected List<ServiceProvider> serviceProvider;

   public List<ServiceProvider> getServiceProvider() {
      if (this.serviceProvider == null) {
         this.serviceProvider = new ArrayList();
      }

      return this.serviceProvider;
   }
}
