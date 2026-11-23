package org.apache.openejb.config.typed;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.typed.util.Builders;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "JavaMailSession"
)
public class JavaMailSessionBuilder extends Resource {
   public JavaMailSessionBuilder() {
      this.setClassName("org.apache.openejb.core.MailSessionFactory");
      this.setType("javax.mail.Session");
      this.setId("JavaMailSession");
      this.setFactoryName("create");
   }

   public JavaMailSessionBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
