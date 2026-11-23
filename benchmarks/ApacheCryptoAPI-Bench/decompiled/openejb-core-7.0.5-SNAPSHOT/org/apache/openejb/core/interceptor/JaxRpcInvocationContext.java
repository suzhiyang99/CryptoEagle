package org.apache.openejb.core.interceptor;

import java.lang.reflect.Method;
import java.util.List;
import javax.xml.rpc.handler.MessageContext;
import org.apache.openejb.core.Operation;

public class JaxRpcInvocationContext extends ReflectionInvocationContext {
   public JaxRpcInvocationContext(Operation operation, List<Interceptor> interceptors, Object target, Method method, MessageContext messageContext, Object... parameters) {
      super(operation, interceptors, target, method, parameters);
      this.getContextData().put(MessageContext.class.getName(), messageContext);
   }
}
