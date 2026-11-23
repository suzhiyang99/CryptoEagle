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
   name = "ConnectionManager"
)
public class ConnectionManager extends AbstractService {
   public ConnectionManager(String id) {
      super(id);
   }

   public ConnectionManager(String id, String type, String provider) {
      super(id, type, provider);
   }

   public ConnectionManager() {
   }
}
