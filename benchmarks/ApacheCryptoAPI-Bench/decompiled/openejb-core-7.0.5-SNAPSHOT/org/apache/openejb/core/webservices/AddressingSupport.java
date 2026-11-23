package org.apache.openejb.core.webservices;

import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;

public interface AddressingSupport {
   EndpointReference getEndpointReference(Element... var1);

   <T extends EndpointReference> T getEndpointReference(Class<T> var1, Element... var2);
}
