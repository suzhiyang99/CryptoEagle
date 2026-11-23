package org.apache.openejb.core.webservices;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class ServiceRefData {
   private static final WeakHashMap<Object, ServiceRefData> registry = new WeakHashMap();
   private final String id;
   private final QName serviceQName;
   private final Class<? extends Service> serviceClass;
   private final QName portQName;
   private final Class<?> referenceClass;
   private final URL wsdlURL;
   private final List<HandlerChainData> handlerChains = new ArrayList();
   private final List<PortRefData> portRefs = new ArrayList();

   public static ServiceRefData getServiceRefData(Object key) {
      return (ServiceRefData)registry.get(key);
   }

   public static ServiceRefData putServiceRefData(Object key, ServiceRefData value) {
      return (ServiceRefData)registry.put(key, value);
   }

   public ServiceRefData(String id, QName serviceQName, Class<? extends Service> serviceClass, QName portQName, Class<?> referenceClass, URL wsdlURL, List<HandlerChainData> handlerChains, List<PortRefData> portRefs) {
      this.id = id;
      this.serviceQName = serviceQName;
      this.serviceClass = serviceClass;
      this.portQName = portQName;
      this.referenceClass = referenceClass;
      this.wsdlURL = wsdlURL;
      if (handlerChains != null) {
         this.handlerChains.addAll(handlerChains);
      }

      if (portRefs != null) {
         this.portRefs.addAll(portRefs);
      }

   }

   public String getId() {
      return this.id;
   }

   public QName getServiceQName() {
      return this.serviceQName;
   }

   public Class<? extends Service> getServiceClass() {
      return this.serviceClass;
   }

   public QName getPortQName() {
      return this.portQName;
   }

   public Class<?> getReferenceClass() {
      return this.referenceClass;
   }

   public URL getWsdlURL() {
      return this.wsdlURL;
   }

   public List<HandlerChainData> getHandlerChains() {
      return this.handlerChains;
   }

   public List<PortRefData> getPortRefs() {
      return this.portRefs;
   }
}
