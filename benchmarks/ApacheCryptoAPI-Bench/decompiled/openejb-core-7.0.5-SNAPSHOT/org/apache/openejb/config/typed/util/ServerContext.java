package org.apache.openejb.config.typed.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.ContainerInfo;
import org.apache.openejb.assembler.classic.JndiContextInfo;
import org.apache.openejb.assembler.classic.ResourceInfo;
import org.apache.openejb.assembler.classic.SecurityServiceInfo;
import org.apache.openejb.assembler.classic.ServiceInfo;
import org.apache.openejb.assembler.classic.TransactionServiceInfo;
import org.apache.openejb.config.ConfigurationFactory;
import org.apache.openejb.config.Service;
import org.apache.openejb.config.sys.Container;
import org.apache.openejb.config.sys.JndiProvider;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.sys.SecurityService;
import org.apache.openejb.config.sys.TransactionManager;

@XmlRootElement(
   name = "ServerContext"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerContext {
   @XmlTransient
   private final ConfigurationFactory factory = new ConfigurationFactory();
   @XmlTransient
   private final Assembler assembler = new Assembler();

   public void createTransactionManager(TransactionManager service) throws OpenEJBException {
      TransactionServiceInfo serviceInfo = (TransactionServiceInfo)this.factory.configureService((Service)service, TransactionServiceInfo.class);
      this.assembler.createTransactionManager(serviceInfo);
   }

   public void createSecurityService(SecurityService service) throws OpenEJBException {
      SecurityServiceInfo serviceInfo = (SecurityServiceInfo)this.factory.configureService((Service)service, SecurityServiceInfo.class);
      this.assembler.createSecurityService(serviceInfo);
   }

   public void createResource(Resource service) throws OpenEJBException {
      ResourceInfo serviceInfo = (ResourceInfo)this.factory.configureService((Service)service, ResourceInfo.class);
      this.assembler.createResource(serviceInfo);
   }

   public void createService(org.apache.openejb.config.sys.Service service) throws OpenEJBException {
      ServiceInfo serviceInfo = this.factory.configureService((Service)service, ServiceInfo.class);
      this.assembler.createService(serviceInfo);
   }

   public void createContainer(Container service) throws OpenEJBException {
      ContainerInfo serviceInfo = (ContainerInfo)this.factory.configureService((Service)service, ContainerInfo.class);
      this.assembler.createContainer(serviceInfo);
   }

   public void createExternalContext(JndiProvider service) throws OpenEJBException {
      JndiContextInfo jndiContextInfo = (JndiContextInfo)this.factory.configureService((Service)service, JndiContextInfo.class);
      this.assembler.createExternalContext(jndiContextInfo);
   }
}
