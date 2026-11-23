package org.apache.openejb.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "SystemProperty"
)
public class SystemProperty {
   @XmlAttribute(
      required = true
   )
   protected String name;
   @XmlAttribute
   protected String value;

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public SystemProperty name(String s) {
      this.setName(s);
      return this;
   }

   public SystemProperty value(String s) {
      this.setValue(s);
      return this;
   }
}
