package org.apache.openejb.config.sys;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "Service"
)
public class Service extends AbstractService {
   @XmlAttribute(
      name = "class"
   )
   protected String clazz;

   public Service() {
   }

   public Service(String id, String clazz) {
      super(id);
      this.clazz = clazz;
   }

   public Service(String id, String type, String clazz) {
      super(id, type);
      this.clazz = clazz;
   }

   public Service(String id, String type, String provider, String clazz) {
      super(id, type, provider);
      this.clazz = clazz;
   }

   public Service(String clazz) {
      this.clazz = clazz;
   }

   public String getClazz() {
      return this.clazz;
   }

   public void setClazz(String clazz) {
      this.clazz = clazz;
   }
}
