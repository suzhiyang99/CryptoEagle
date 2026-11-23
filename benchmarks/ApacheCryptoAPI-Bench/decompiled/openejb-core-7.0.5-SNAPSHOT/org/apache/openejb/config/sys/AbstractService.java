package org.apache.openejb.config.sys;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.openejb.util.SuperProperties;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Service"
)
public abstract class AbstractService implements org.apache.openejb.config.Service {
   @XmlValue
   @XmlJavaTypeAdapter(PropertiesAdapter.class)
   protected Properties properties;
   @XmlAttribute(
      required = true
   )
   protected String id;
   @XmlAttribute
   protected String jar;
   @XmlAttribute
   protected String provider;
   @XmlAttribute
   protected String type;
   @XmlAttribute
   protected String classpath;
   @XmlAttribute(
      name = "classpath-api"
   )
   protected String classpathAPI;
   @XmlAttribute(
      name = "class-name"
   )
   protected String className;
   @XmlAttribute(
      name = "constructor"
   )
   protected String constructor;
   @XmlAttribute(
      name = "factory-name"
   )
   protected String factoryName;
   @XmlAttribute(
      name = "properties-provider"
   )
   private String propertiesProvider;
   @XmlAttribute(
      name = "template"
   )
   private String template;

   protected AbstractService(String id) {
      this(id, (String)null, (String)null);
   }

   protected AbstractService(String id, String type) {
      this.id = id;
      this.type = type;
   }

   protected AbstractService(String id, String type, String provider) {
      this.id = id;
      this.provider = provider;
      this.type = type;
   }

   protected AbstractService() {
   }

   public Properties getProperties() {
      if (this.properties == null) {
         this.properties = new SuperProperties();
      }

      return this.properties;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String value) {
      this.id = value;
   }

   public String getJar() {
      return this.jar;
   }

   public void setJar(String value) {
      this.jar = value;
   }

   public String getProvider() {
      return this.provider;
   }

   public void setProvider(String value) {
      this.provider = value;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getClassName() {
      return this.className;
   }

   public void setClassName(String className) {
      this.className = className;
   }

   public String getClasspathAPI() {
      return this.classpathAPI;
   }

   public void setClasspathAPI(String classpathAPI) {
      this.classpathAPI = classpathAPI;
   }

   public String getConstructor() {
      return this.constructor;
   }

   public void setConstructor(String constructor) {
      this.constructor = constructor;
   }

   public String getFactoryName() {
      return this.factoryName;
   }

   public void setFactoryName(String factoryName) {
      this.factoryName = factoryName;
   }

   public String getClasspath() {
      return this.classpath;
   }

   public void setClasspath(String classpath) {
      this.classpath = classpath;
   }

   public String getTemplate() {
      return this.template;
   }

   public void setTemplate(String template) {
      this.template = template;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof AbstractService)) {
         return false;
      } else {
         AbstractService that = (AbstractService)o;
         if (this.id != null) {
            if (!this.id.equals(that.id)) {
               return false;
            }
         } else if (that.id != null) {
            return false;
         }

         if (this.jar != null) {
            if (!this.jar.equals(that.jar)) {
               return false;
            }
         } else if (that.jar != null) {
            return false;
         }

         if (this.type != null) {
            if (!this.type.equals(that.type)) {
               return false;
            }
         } else if (that.type != null) {
            return false;
         }

         if (this.provider != null) {
            if (!this.provider.equals(that.provider)) {
               return false;
            }
         } else if (that.provider != null) {
            return false;
         }

         if (this.properties != null) {
            if (!this.properties.equals(that.properties)) {
               return false;
            }
         } else if (that.properties != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int result = this.properties != null ? this.properties.hashCode() : 0;
      result = 31 * result + (this.id != null ? this.id.hashCode() : 0);
      result = 31 * result + (this.jar != null ? this.jar.hashCode() : 0);
      result = 31 * result + (this.provider != null ? this.provider.hashCode() : 0);
      result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
      return result;
   }

   public void setPropertiesProvider(String propertiesProvider) {
      this.propertiesProvider = propertiesProvider;
   }

   public String getPropertiesProvider() {
      return this.propertiesProvider;
   }
}
