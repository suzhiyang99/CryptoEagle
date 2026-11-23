package org.apache.openejb.core.webservices;

import javax.xml.namespace.QName;

public class PortAddress {
   private final String portId;
   private final QName serviceQName;
   private final QName portQName;
   private final String address;
   private final String serviceEndpointInterface;

   public PortAddress(String portId, QName serviceQName, QName portQName, String address, String serviceEndpointInterface) {
      this.portId = portId;
      this.serviceQName = serviceQName;
      this.portQName = portQName;
      this.address = address;
      this.serviceEndpointInterface = serviceEndpointInterface;
   }

   public String getPortId() {
      return this.portId;
   }

   public QName getServiceQName() {
      return this.serviceQName;
   }

   public QName getPortQName() {
      return this.portQName;
   }

   public String getAddress() {
      return this.address;
   }

   public String getServiceEndpointInterface() {
      return this.serviceEndpointInterface;
   }
}
