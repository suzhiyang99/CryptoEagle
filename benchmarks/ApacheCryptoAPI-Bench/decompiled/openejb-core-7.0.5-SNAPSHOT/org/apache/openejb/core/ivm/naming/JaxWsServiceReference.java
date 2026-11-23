package org.apache.openejb.core.ivm.naming;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;
import org.apache.openejb.Injection;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.core.webservices.HandlerChainData;
import org.apache.openejb.core.webservices.HandlerResolverImpl;
import org.apache.openejb.core.webservices.PortAddress;
import org.apache.openejb.core.webservices.PortAddressRegistry;
import org.apache.openejb.core.webservices.PortRefData;
import org.apache.openejb.core.webservices.ProviderWrapper;
import org.apache.openejb.core.webservices.ServiceRefData;
import org.apache.openejb.loader.SystemInstance;

public class JaxWsServiceReference extends Reference {
   private final String id;
   private final QName serviceQName;
   private final QName portQName;
   private final Class<? extends Service> serviceClass;
   private final Class<?> referenceClass;
   private final URL wsdlUrl;
   private final List<HandlerChainData> handlerChains = new ArrayList();
   private final Collection<Injection> injections;
   private final Properties properties;
   private PortAddressRegistry portAddressRegistry;
   private final List<PortRefData> portRefs = new ArrayList();

   public JaxWsServiceReference(String id, QName serviceQName, Class<? extends Service> serviceClass, QName portQName, Class<?> referenceClass, URL wsdlUrl, List<PortRefData> portRefs, List<HandlerChainData> handlerChains, Collection<Injection> injections, Properties properties) {
      this.id = id;
      this.properties = properties;
      this.serviceQName = serviceQName;
      this.serviceClass = serviceClass;
      this.portQName = portQName;
      this.referenceClass = referenceClass;
      this.wsdlUrl = wsdlUrl;
      if (portRefs != null) {
         this.portRefs.addAll(portRefs);
      }

      if (handlerChains != null) {
         this.handlerChains.addAll(handlerChains);
      }

      this.injections = injections;
   }

   public Object getObject() throws javax.naming.NamingException {
      String referenceClassName = this.referenceClass != null ? this.referenceClass.getName() : null;
      Set<PortAddress> portAddresses = this.getPortAddressRegistry().getPorts(this.id, this.serviceQName, referenceClassName);
      URL wsdlUrl = this.wsdlUrl;
      QName serviceQName = this.serviceQName;
      if (portAddresses.size() == 1) {
         PortAddress portAddress = (PortAddress)portAddresses.iterator().next();

         try {
            wsdlUrl = new URL(portAddress.getAddress() + "?wsdl");
         } catch (MalformedURLException var17) {
         }

         serviceQName = portAddress.getServiceQName();
      }

      Map<QName, PortRefData> portsByQName = new HashMap();
      List<PortRefData> ports = new ArrayList(this.portRefs.size() + portAddresses.size());

      for(PortRefData portRef : this.portRefs) {
         PortRefData port = new PortRefData(portRef);
         if (port.getQName() != null) {
            portsByQName.put(port.getQName(), port);
         }

         ports.add(port);
      }

      for(PortAddress portAddress : portAddresses) {
         PortRefData port = (PortRefData)portsByQName.get(portAddress.getPortQName());
         if (port == null) {
            port = new PortRefData();
            port.setQName(portAddress.getPortQName());
            port.setServiceEndpointInterface(portAddress.getServiceEndpointInterface());
            port.getAddresses().add(portAddress.getAddress());
            ports.add(port);
         } else {
            port.getAddresses().add(portAddress.getAddress());
            if (port.getServiceEndpointInterface() == null) {
               port.setServiceEndpointInterface(portAddress.getServiceEndpointInterface());
            }
         }
      }

      WebServiceClientCustomizer customizer = (WebServiceClientCustomizer)SystemInstance.get().getComponent(WebServiceClientCustomizer.class);
      Properties configuration = this.properties == null ? new Properties() : this.properties;
      ProviderWrapper.beforeCreate(ports, customizer, this.properties);

      Service instance;
      try {
         instance = null;
         if (Service.class.equals(this.serviceClass)) {
            instance = Service.create(wsdlUrl, serviceQName);
         } else {
            try {
               instance = (Service)this.serviceClass.getConstructor(URL.class, QName.class).newInstance(wsdlUrl, serviceQName);
            } catch (Throwable e) {
               throw (javax.naming.NamingException)(new javax.naming.NamingException("Could not instantiate jax-ws service class " + this.serviceClass.getName())).initCause(e);
            }
         }
      } finally {
         ProviderWrapper.afterCreate();
      }

      if (!this.handlerChains.isEmpty()) {
         HandlerResolver handlerResolver = new HandlerResolverImpl(this.handlerChains, this.injections, new InitialContext());
         instance.setHandlerResolver(handlerResolver);
      }

      Object port;
      if (this.referenceClass != null && !Service.class.isAssignableFrom(this.referenceClass)) {
         WebServiceFeature[] features = customizer == null ? null : customizer.features(serviceQName, configuration);
         if (features != null && features.length != 0) {
            port = instance.getPort(this.referenceClass, features);
         } else {
            port = instance.getPort(this.referenceClass);
         }
      } else {
         port = instance;
      }

      ServiceRefData serviceRefData = new ServiceRefData(this.id, serviceQName, this.serviceClass, this.portQName, this.referenceClass, wsdlUrl, this.handlerChains, this.portRefs);
      ServiceRefData.putServiceRefData(port, serviceRefData);
      return port;
   }

   private PortAddressRegistry getPortAddressRegistry() {
      if (this.portAddressRegistry == null) {
         this.portAddressRegistry = (PortAddressRegistry)SystemInstance.get().getComponent(PortAddressRegistry.class);
         if (this.portAddressRegistry == null) {
            throw new OpenEJBRuntimeException("No port address registry, it generally means you either didn't activate cxf or don't use tomee+");
         }
      }

      return this.portAddressRegistry;
   }

   public interface WebServiceClientCustomizer {
      WebServiceFeature[] features(QName var1, Properties var2);

      void customize(Object var1, Properties var2);
   }
}
