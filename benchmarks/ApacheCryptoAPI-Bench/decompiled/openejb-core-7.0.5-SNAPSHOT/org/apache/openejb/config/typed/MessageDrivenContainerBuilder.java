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
   name = "MessageDrivenContainer"
)
public class MessageDrivenContainerBuilder extends Container {
   @XmlAttribute
   private String resourceAdapter = "Default JMS Resource Adapter";
   @XmlAttribute
   private String messageListenerInterface = "javax.jms.MessageListener";
   @XmlAttribute
   private String activationSpecClass = "org.apache.activemq.ra.ActiveMQActivationSpec";
   @XmlAttribute
   private int instanceLimit = 10;

   public MessageDrivenContainerBuilder() {
      this.setClassName("org.apache.openejb.core.mdb.MdbContainer");
      this.setType("MESSAGE");
      this.setId("MessageDrivenContainer");
      this.setConstructor("id, securityService, resourceAdapter, messageListenerInterface, activationSpecClass, instanceLimit, failOnUnknownActivationSpec");
   }

   public MessageDrivenContainerBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public MessageDrivenContainerBuilder withResourceAdapter(String resourceAdapter) {
      this.resourceAdapter = resourceAdapter;
      return this;
   }

   public void setResourceAdapter(String resourceAdapter) {
      this.resourceAdapter = resourceAdapter;
   }

   public String getResourceAdapter() {
      return this.resourceAdapter;
   }

   public MessageDrivenContainerBuilder withMessageListenerInterface(String messageListenerInterface) {
      this.messageListenerInterface = messageListenerInterface;
      return this;
   }

   public void setMessageListenerInterface(String messageListenerInterface) {
      this.messageListenerInterface = messageListenerInterface;
   }

   public String getMessageListenerInterface() {
      return this.messageListenerInterface;
   }

   public MessageDrivenContainerBuilder withActivationSpecClass(String activationSpecClass) {
      this.activationSpecClass = activationSpecClass;
      return this;
   }

   public void setActivationSpecClass(String activationSpecClass) {
      this.activationSpecClass = activationSpecClass;
   }

   public String getActivationSpecClass() {
      return this.activationSpecClass;
   }

   public MessageDrivenContainerBuilder withInstanceLimit(int instanceLimit) {
      this.instanceLimit = instanceLimit;
      return this;
   }

   public void setInstanceLimit(int instanceLimit) {
      this.instanceLimit = instanceLimit;
   }

   public int getInstanceLimit() {
      return this.instanceLimit;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
