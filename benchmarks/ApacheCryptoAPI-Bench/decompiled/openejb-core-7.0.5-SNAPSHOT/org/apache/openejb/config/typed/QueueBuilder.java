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
   name = "Queue"
)
public class QueueBuilder extends Resource {
   @XmlAttribute
   private String destination;

   public QueueBuilder() {
      this.setClassName("org.apache.activemq.command.ActiveMQQueue");
      this.setType("javax.jms.Queue");
      this.setId("Queue");
      this.setConstructor("destination");
   }

   public QueueBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public QueueBuilder withDestination(String destination) {
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
