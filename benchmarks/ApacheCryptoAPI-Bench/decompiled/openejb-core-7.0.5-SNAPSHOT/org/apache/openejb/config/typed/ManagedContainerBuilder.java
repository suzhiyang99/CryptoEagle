package org.apache.openejb.config.typed;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.openejb.config.sys.Container;
import org.apache.openejb.config.typed.util.Builders;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "ManagedContainer"
)
public class ManagedContainerBuilder extends Container {
   public ManagedContainerBuilder() {
      this.setClassName("org.apache.openejb.core.managed.ManagedContainer");
      this.setType("MANAGED");
      this.setId("ManagedContainer");
      this.setConstructor("id, securityService");
   }

   public ManagedContainerBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
