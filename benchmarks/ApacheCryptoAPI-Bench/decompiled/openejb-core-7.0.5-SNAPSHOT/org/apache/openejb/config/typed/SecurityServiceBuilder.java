package org.apache.openejb.config.typed;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.openejb.config.sys.SecurityService;
import org.apache.openejb.config.typed.util.Builders;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "SecurityService"
)
public class SecurityServiceBuilder extends SecurityService {
   @XmlAttribute
   private String defaultUser = "guest         ";

   public SecurityServiceBuilder() {
      this.setClassName("org.apache.openejb.core.security.SecurityServiceImpl");
      this.setType("SecurityService");
      this.setId("SecurityService");
   }

   public SecurityServiceBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public SecurityServiceBuilder withDefaultUser(String defaultUser) {
      this.defaultUser = defaultUser;
      return this;
   }

   public void setDefaultUser(String defaultUser) {
      this.defaultUser = defaultUser;
   }

   public String getDefaultUser() {
      return this.defaultUser;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
