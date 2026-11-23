package org.apache.openejb.core.webservices;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.PortInfo;
import org.apache.openejb.Injection;

public class PortData implements PortInfo {
   private String portId;
   private QName serviceName;
   private QName portName;
   private String bindingId;
   private URL wsdlUrl;
   private final List<HandlerChainData> handlerChains = new ArrayList();
   private final List<Injection> injections = new ArrayList();
   private boolean mtomEnabled;
   private QName wsdlPort;
   private QName wsdlService;
   private String location;
   private boolean secure;
   private Properties properties;

   public String getPortId() {
      return this.portId;
   }

   public void setPortId(String portId) {
      this.portId = portId;
   }

   public QName getServiceName() {
      return this.serviceName;
   }

   public void setServiceName(QName serviceName) {
      this.serviceName = serviceName;
   }

   public QName getPortName() {
      return this.portName;
   }

   public void setPortName(QName portName) {
      this.portName = portName;
   }

   public String getBindingID() {
      return this.bindingId;
   }

   public void setBindingID(String bindingId) {
      this.bindingId = bindingId;
   }

   public URL getWsdlUrl() {
      return this.wsdlUrl;
   }

   public void setWsdlUrl(URL wsdlUrl) {
      this.wsdlUrl = wsdlUrl;
   }

   public List<HandlerChainData> getHandlerChains() {
      return this.handlerChains;
   }

   public List<Injection> getInjections() {
      return this.injections;
   }

   public boolean isMtomEnabled() {
      return this.mtomEnabled;
   }

   public void setMtomEnabled(boolean mtomEnabled) {
      this.mtomEnabled = mtomEnabled;
   }

   public QName getWsdlPort() {
      return this.wsdlPort;
   }

   public void setWsdlPort(QName wsdlPort) {
      this.wsdlPort = wsdlPort;
   }

   public QName getWsdlService() {
      return this.wsdlService;
   }

   public void setWsdlService(QName wsdlService) {
      this.wsdlService = wsdlService;
   }

   public String getLocation() {
      return this.location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public void setSecure(boolean secure) {
      this.secure = secure;
   }

   public boolean isSecure() {
      return this.secure;
   }

   public Properties getProperties() {
      return this.properties;
   }

   public void setProperties(Properties properties) {
      this.properties = properties;
   }
}
