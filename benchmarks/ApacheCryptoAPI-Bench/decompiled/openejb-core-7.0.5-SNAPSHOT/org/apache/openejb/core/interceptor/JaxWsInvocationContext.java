package org.apache.openejb.core.interceptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import javax.xml.ws.handler.MessageContext;
import org.apache.openejb.core.Operation;

public class JaxWsInvocationContext extends ReflectionInvocationContext {
   private final MessageContext messageContext;

   public JaxWsInvocationContext(Operation operation, List<Interceptor> interceptors, Object target, Method method, MessageContext messageContext, Object... parameters) {
      super(operation, interceptors, target, method, parameters);
      this.messageContext = messageContext;
   }

   public Map<String, Object> getContextData() {
      return this.messageContext;
   }
}
