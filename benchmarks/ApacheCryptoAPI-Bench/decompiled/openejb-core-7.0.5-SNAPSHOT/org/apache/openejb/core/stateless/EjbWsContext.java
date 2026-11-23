package org.apache.openejb.core.stateless;

import java.security.Principal;
import javax.ejb.SessionContext;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.webservices.AddressingSupport;
import org.w3c.dom.Element;

public class EjbWsContext implements WebServiceContext {
   private final SessionContext context;

   public EjbWsContext(SessionContext context) {
      this.context = context;
   }

   public MessageContext getMessageContext() {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      MessageContext messageContext = (MessageContext)threadContext.get(MessageContext.class);
      if (messageContext == null) {
         throw new IllegalStateException("Only calls on the service-endpoint have a MessageContext.");
      } else {
         return messageContext;
      }
   }

   public Principal getUserPrincipal() {
      return this.context.getCallerPrincipal();
   }

   public boolean isUserInRole(String roleName) {
      return this.context.isCallerInRole(roleName);
   }

   private AddressingSupport getAddressingSupport() {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      AddressingSupport wsaSupport = (AddressingSupport)threadContext.get(AddressingSupport.class);
      if (wsaSupport == null) {
         throw new IllegalStateException("Only calls on the service-endpoint can get the EndpointReference.");
      } else {
         return wsaSupport;
      }
   }

   public EndpointReference getEndpointReference(Element... referenceParameters) {
      return this.getAddressingSupport().getEndpointReference(referenceParameters);
   }

   public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
      return (T)this.getAddressingSupport().getEndpointReference(clazz, referenceParameters);
   }
}
