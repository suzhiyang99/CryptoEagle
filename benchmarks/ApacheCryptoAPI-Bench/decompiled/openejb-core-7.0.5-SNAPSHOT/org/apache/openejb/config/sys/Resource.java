package org.apache.openejb.config.sys;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "Resource"
)
public class Resource extends AbstractService {
   @XmlAttribute
   protected String jndi;
   @XmlAttribute(
      name = "post-construct"
   )
   protected String postConstruct;
   @XmlAttribute(
      name = "pre-destroy"
   )
   protected String preDestroy;
   @XmlAttribute(
      name = "aliases"
   )
   @XmlJavaTypeAdapter(ListAdapter.class)
   protected List<String> aliases = new ArrayList();
   @XmlAttribute(
      name = "depends-on"
   )
   @XmlJavaTypeAdapter(ListAdapter.class)
   protected List<String> dependsOn = new ArrayList();

   public Resource(String id) {
      super(id);
   }

   public Resource(String id, String type) {
      super(id, type);
   }

   public Resource(String id, String type, String provider) {
      super(id, type, provider);
   }

   public Resource() {
   }

   public String getJndi() {
      return this.jndi;
   }

   public void setJndi(String value) {
      this.jndi = value;
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public List<String> getDependsOn() {
      return this.dependsOn;
   }

   public String getPostConstruct() {
      return this.postConstruct;
   }

   public void setPostConstruct(String postConstruct) {
      this.postConstruct = postConstruct;
   }

   public String getPreDestroy() {
      return this.preDestroy;
   }

   public void setPreDestroy(String preDestroy) {
      this.preDestroy = preDestroy;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Resource)) {
         return false;
      } else if (!super.equals(o)) {
         return false;
      } else {
         Resource resource = (Resource)o;
         if (this.jndi != null) {
            if (!this.jndi.equals(resource.jndi)) {
               return false;
            }
         } else if (resource.jndi != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (this.jndi != null ? this.jndi.hashCode() : 0);
      return result;
   }
}
