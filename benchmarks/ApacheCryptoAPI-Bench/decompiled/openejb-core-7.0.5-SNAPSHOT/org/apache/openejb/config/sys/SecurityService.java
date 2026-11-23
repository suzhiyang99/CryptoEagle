package org.apache.openejb.config.sys;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "SecurityService"
)
public class SecurityService extends AbstractService {
   public SecurityService(String id) {
      super(id);
   }

   public SecurityService(String id, String type, String provider) {
      super(id, type, provider);
   }

   public SecurityService() {
   }
}
