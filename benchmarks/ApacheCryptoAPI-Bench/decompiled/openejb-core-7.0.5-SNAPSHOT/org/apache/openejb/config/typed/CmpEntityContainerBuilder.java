package org.apache.openejb.config.typed;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.openejb.config.sys.Container;
import org.apache.openejb.config.typed.util.Builders;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "CmpEntityContainer"
)
public class CmpEntityContainerBuilder extends Container {
   @XmlAttribute
   private String cmpEngineFactory = "org.apache.openejb.core.cmp.jpa.JpaCmpEngineFactory";

   public CmpEntityContainerBuilder() {
      this.setClassName("org.apache.openejb.core.cmp.CmpContainer");
      this.setType("CMP_ENTITY");
      this.setId("CmpEntityContainer");
      this.setConstructor("id, transactionManager, securityService, cmpEngineFactory");
   }

   public CmpEntityContainerBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public CmpEntityContainerBuilder withCmpEngineFactory(String cmpEngineFactory) {
      this.cmpEngineFactory = cmpEngineFactory;
      return this;
   }

   public void setCmpEngineFactory(String cmpEngineFactory) {
      this.cmpEngineFactory = cmpEngineFactory;
   }

   public String getCmpEngineFactory() {
      return this.cmpEngineFactory;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
