package org.apache.openejb.config.typed;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.openejb.config.sys.Container;
import org.apache.openejb.config.typed.util.Builders;
import org.apache.openejb.config.typed.util.DurationAdapter;
import org.apache.openejb.util.Duration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "SingletonContainer"
)
public class SingletonContainerBuilder extends Container {
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration accessTimeout = Duration.parse("30 seconds");

   public SingletonContainerBuilder() {
      this.setClassName("org.apache.openejb.core.singleton.SingletonContainer");
      this.setType("SINGLETON");
      this.setId("SingletonContainer");
      this.setConstructor("id, securityService");
   }

   public SingletonContainerBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public SingletonContainerBuilder withAccessTimeout(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
      return this;
   }

   public void setAccessTimeout(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
   }

   public Duration getAccessTimeout() {
      return this.accessTimeout;
   }

   public SingletonContainerBuilder withAccessTimeout(long time, TimeUnit unit) {
      return this.withAccessTimeout(new Duration(time, unit));
   }

   public void setAccessTimeout(long time, TimeUnit unit) {
      this.setAccessTimeout(new Duration(time, unit));
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
