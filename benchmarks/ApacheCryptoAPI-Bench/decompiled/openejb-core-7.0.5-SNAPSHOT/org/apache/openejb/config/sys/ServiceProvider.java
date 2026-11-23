package org.apache.openejb.config.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.openejb.util.SuperProperties;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "ServiceProvider"
)
public class ServiceProvider {
   @XmlValue
   @XmlJavaTypeAdapter(PropertiesAdapter.class)
   protected Properties properties;
   @XmlAttribute(
      name = "class-name"
   )
   protected String className;
   @XmlAttribute(
      name = "constructor"
   )
   protected String constructor;
   @XmlAttribute
   protected String description;
   @XmlAttribute(
      name = "display-name"
   )
   protected String displayName;
   @XmlAttribute(
      name = "factory-name"
   )
   protected String factoryName;
   @XmlAttribute(
      required = true
   )
   protected String id;
   @XmlAttribute(
      name = "service",
      required = true
   )
   protected String service;
   @XmlAttribute(
      name = "types",
      required = false
   )
   protected List<String> types;
   @XmlAttribute(
      name = "parent"
   )
   protected String parent;

   public ServiceProvider() {
   }

   public ServiceProvider(Class clazz, String id, String service) {
      this(clazz.getName(), id, service);
   }

   public ServiceProvider(String className, String id, String service) {
      this.className = className;
      this.id = id;
      this.service = service;
   }

   public Properties getProperties() {
      if (this.properties == null) {
         SuperProperties sp = new SuperProperties();
         sp.setCaseInsensitive(true);
         this.properties = sp;
      }

      return this.properties;
   }

   public String getClassName() {
      return this.className;
   }

   public void setClassName(String value) {
      this.className = value;
   }

   public String getConstructor() {
      return this.constructor;
   }

   public void setConstructor(String value) {
      this.constructor = value;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String value) {
      this.description = value;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setDisplayName(String value) {
      this.displayName = value;
   }

   public String getFactoryName() {
      return this.factoryName;
   }

   public void setFactoryName(String value) {
      this.factoryName = value;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String value) {
      this.id = value;
   }

   public String getService() {
      return this.service;
   }

   public void setService(String value) {
      this.service = value;
   }

   public List<String> getTypes() {
      if (this.types == null) {
         this.types = new ArrayList();
      }

      return this.types;
   }

   public String getParent() {
      return this.parent;
   }

   public void setParent(String parent) {
      this.parent = parent;
   }

   public String toString() {
      return "ServiceProvider{id='" + this.id + '\'' + ", service='" + this.service + '\'' + '}';
   }
}
