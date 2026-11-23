package org.apache.openejb.config.typed;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.typed.util.Builders;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "ORB"
)
public class ORBBuilder extends Resource {
   public ORBBuilder() {
      this.setClassName("org.apache.openejb.core.OrbFactory");
      this.setType("org.omg.CORBA.ORB");
      this.setId("ORB");
      this.setFactoryName("create");
   }

   public ORBBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
