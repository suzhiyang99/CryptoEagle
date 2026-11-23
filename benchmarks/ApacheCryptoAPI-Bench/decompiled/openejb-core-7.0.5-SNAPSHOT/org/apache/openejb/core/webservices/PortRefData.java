package org.apache.openejb.core.webservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.namespace.QName;

public class PortRefData {
   private QName qname;
   private String serviceEndpointInterface;
   private boolean enableMtom;
   private final Properties properties = new Properties();
   private final List<String> addresses = new ArrayList(1);

   public PortRefData() {
   }

   public PortRefData(PortRefData src) {
      this.qname = src.qname;
      this.serviceEndpointInterface = src.serviceEndpointInterface;
      this.enableMtom = src.enableMtom;
      this.properties.putAll(src.properties);
      this.addresses.addAll(src.addresses);
   }

   public QName getQName() {
      return this.qname;
   }

   public void setQName(QName qname) {
      this.qname = qname;
   }

   public String getServiceEndpointInterface() {
      return this.serviceEndpointInterface;
   }

   public void setServiceEndpointInterface(String serviceEndpointInterface) {
      this.serviceEndpointInterface = serviceEndpointInterface;
   }

   public boolean isEnableMtom() {
      return this.enableMtom;
   }

   public void setEnableMtom(boolean value) {
      this.enableMtom = value;
   }

   public Properties getProperties() {
      return this.properties;
   }

   public List<String> getAddresses() {
      return this.addresses;
   }
}
