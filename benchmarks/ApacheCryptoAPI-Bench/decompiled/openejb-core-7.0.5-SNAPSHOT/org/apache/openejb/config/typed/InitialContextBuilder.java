package org.apache.openejb.config.typed;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.openejb.config.sys.JndiProvider;
import org.apache.openejb.config.typed.util.Builders;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "InitialContext"
)
public class InitialContextBuilder extends JndiProvider {
   public InitialContextBuilder() {
      this.setClassName("org.apache.openejb.core.NoProvider");
      this.setType("javax.naming.InitialContext");
      this.setId("InitialContext");
   }

   public InitialContextBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
