package org.apache.openejb.config.typed;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.typed.util.Builders;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "Topic"
)
public class TopicBuilder extends Resource {
   @XmlAttribute
   private String destination;

   public TopicBuilder() {
      this.setClassName("org.apache.activemq.command.ActiveMQTopic");
      this.setType("javax.jms.Topic");
      this.setId("Topic");
      this.setConstructor("destination");
   }

   public TopicBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public TopicBuilder withDestination(String destination) {
      this.destination = destination;
      return this;
   }

   public void setDestination(String destination) {
      this.destination = destination;
   }

   public String getDestination() {
      return this.destination;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
