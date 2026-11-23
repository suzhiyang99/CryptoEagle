package org.apache.openejb.core.webservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public class HandlerChainData {
   private final QName serviceNamePattern;
   private final QName portNamePattern;
   private final List<String> protocolBindings = new ArrayList();
   private final List<HandlerData> handlers = new ArrayList();

   public HandlerChainData(QName serviceNamePattern, QName portNamePattern, List<String> protocolBindings, List<HandlerData> handlers) {
      this.serviceNamePattern = serviceNamePattern;
      this.portNamePattern = portNamePattern;
      this.protocolBindings.addAll(protocolBindings);
      this.handlers.addAll(handlers);
   }

   public QName getServiceNamePattern() {
      return this.serviceNamePattern;
   }

   public QName getPortNamePattern() {
      return this.portNamePattern;
   }

   public List<String> getProtocolBindings() {
      return this.protocolBindings;
   }

   public List<HandlerData> getHandlers() {
      return this.handlers;
   }
}
