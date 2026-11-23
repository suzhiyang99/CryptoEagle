package org.apache.openejb.core.webservices;

import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;

public class NoAddressingSupport implements AddressingSupport {
   public static final NoAddressingSupport INSTANCE = new NoAddressingSupport();

   public EndpointReference getEndpointReference(Element... referenceParameters) {
      throw new UnsupportedOperationException("JaxWS 2.1 APIs are not supported.");
   }

   public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
      throw new UnsupportedOperationException("JaxWS 2.1 APIs are not supported.");
   }
}
