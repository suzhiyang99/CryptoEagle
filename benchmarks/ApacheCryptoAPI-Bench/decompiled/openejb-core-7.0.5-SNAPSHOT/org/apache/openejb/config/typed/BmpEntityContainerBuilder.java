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
   name = "BmpEntityContainer"
)
public class BmpEntityContainerBuilder extends Container {
   @XmlAttribute
   private int poolSize = 10;

   public BmpEntityContainerBuilder() {
      this.setClassName("org.apache.openejb.core.entity.EntityContainer");
      this.setType("BMP_ENTITY");
      this.setId("BmpEntityContainer");
      this.setConstructor("id, securityService, poolSize");
   }

   public BmpEntityContainerBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public BmpEntityContainerBuilder withPoolSize(int poolSize) {
      this.poolSize = poolSize;
      return this;
   }

   public void setPoolSize(int poolSize) {
      this.poolSize = poolSize;
   }

   public int getPoolSize() {
      return this.poolSize;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
