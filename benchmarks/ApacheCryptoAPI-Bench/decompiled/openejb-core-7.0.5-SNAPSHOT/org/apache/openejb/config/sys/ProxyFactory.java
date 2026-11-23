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
   name = "ProxyFactory"
)
public class ProxyFactory extends AbstractService {
   public ProxyFactory(String id) {
      super(id);
   }

   public ProxyFactory(String id, String type, String provider) {
      super(id, type, provider);
   }

   public ProxyFactory() {
   }
}
