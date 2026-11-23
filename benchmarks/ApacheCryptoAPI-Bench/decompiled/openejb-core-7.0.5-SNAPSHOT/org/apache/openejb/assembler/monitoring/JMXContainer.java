package org.apache.openejb.assembler.monitoring;

import java.util.Map;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.api.internal.Internal;
import org.apache.openejb.api.jmx.Description;
import org.apache.openejb.api.jmx.ManagedAttribute;
import org.apache.openejb.assembler.classic.ContainerInfo;

@Internal
@Description("describe a container")
public class JMXContainer {
   private final Container container;
   private final ContainerInfo info;

   public JMXContainer(ContainerInfo serviceInfo, Container service) {
      this.info = serviceInfo;
      this.container = service;
   }

   @ManagedAttribute
   @Description("Container id.")
   public String getContainerId() {
      return this.container.getContainerID().toString();
   }

   @ManagedAttribute
   @Description("Container type.")
   public String getContainerType() {
      return this.container.getContainerType().name().toLowerCase().replace("_", " ");
   }

   @ManagedAttribute
   @Description("Container managed beans.")
   public String[] getManagedBeans() {
      BeanContext[] beans = this.container.getBeanContexts();
      String[] beanNames = new String[beans.length];
      int i = 0;

      for(BeanContext bc : beans) {
         beanNames[i++] = "bean-class: " + bc.getBeanClass().getName() + ", " + "ejb-name: " + bc.getEjbName() + ", " + "deployment-id: " + bc.getDeploymentID() + ", ";
      }

      return beanNames;
   }

   @ManagedAttribute
   @Description("Container service.")
   public String getService() {
      return this.info.service;
   }

   @ManagedAttribute
   @Description("Container class name.")
   public String getClassName() {
      return this.info.className;
   }

   @ManagedAttribute
   @Description("Container factory method.")
   public String getFactoryMethod() {
      return this.info.factoryMethod;
   }

   @ManagedAttribute
   @Description("Container properties.")
   public String[] getProperties() {
      String[] properties = new String[this.info.properties.size()];
      int i = 0;

      for(Map.Entry<Object, Object> entry : this.info.properties.entrySet()) {
         properties[i++] = entry.getKey().toString() + " = " + entry.getValue().toString();
      }

      return properties;
   }
}
