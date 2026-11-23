package org.apache.openejb.config.sys;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"container", "resource", "service", "connector", "jndiProvider"}
)
@XmlRootElement(
   name = "resources"
)
public class Resources {
   @XmlElement(
      name = "Resource"
   )
   protected List<Resource> resource;
   @XmlElement(
      name = "Container"
   )
   protected List<Container> container;
   @XmlElement(
      name = "Service"
   )
   protected List<Service> service;
   @XmlElement(
      name = "Connector"
   )
   protected List<Connector> connector;
   @XmlElement(
      name = "JndiProvider"
   )
   protected List<JndiProvider> jndiProvider;

   public List<Resource> getResource() {
      if (this.resource == null) {
         this.resource = new ArrayList();
      }

      return this.resource;
   }

   public List<Container> getContainer() {
      if (this.container == null) {
         this.container = new ArrayList();
      }

      return this.container;
   }

   public List<Service> getService() {
      if (this.service == null) {
         this.service = new ArrayList();
      }

      return this.service;
   }

   public List<Connector> getConnector() {
      if (this.connector == null) {
         this.connector = new ArrayList();
      }

      return this.connector;
   }

   public List<JndiProvider> getJndiProvider() {
      if (this.jndiProvider == null) {
         this.jndiProvider = new ArrayList();
      }

      return this.jndiProvider;
   }

   public void add(Object service) {
      if (service instanceof Resource) {
         this.getResource().add((Resource)service);
      } else if (service instanceof Service) {
         this.getService().add((Service)service);
      } else if (service instanceof Connector) {
         this.getConnector().add((Connector)service);
      } else if (JndiProvider.class.isInstance(service)) {
         this.getJndiProvider().add(JndiProvider.class.cast(service));
      }

   }
}
