package org.apache.openejb.config.sys;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "Container"
)
public class Container extends AbstractService {
   public Container(String id) {
      super(id);
   }

   public Container(String id, String type, String provider) {
      super(id, type, provider);
   }

   public Container() {
   }

   /** @deprecated */
   public String getCtype() {
      return null;
   }

   @XmlAttribute
   public void setCtype(String value) {
      this.setType(value);
   }
}
