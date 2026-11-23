package org.apache.openejb.config.typed;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.openejb.config.sys.ProxyFactory;
import org.apache.openejb.config.typed.util.Builders;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "ProxyFactory"
)
public class ProxyFactoryBuilder extends ProxyFactory {
   public ProxyFactoryBuilder() {
      this.setClassName("org.apache.openejb.util.proxy.Jdk13ProxyFactory");
      this.setType("ProxyFactory");
      this.setId("ProxyFactory");
   }

   public ProxyFactoryBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
