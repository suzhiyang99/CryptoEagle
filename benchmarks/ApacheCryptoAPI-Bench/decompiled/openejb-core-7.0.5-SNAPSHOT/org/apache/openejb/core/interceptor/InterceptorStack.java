package org.apache.openejb.core.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.interceptor.InvocationContext;
import javax.xml.ws.handler.MessageContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.util.proxy.DynamicProxyImplFactory;

public class InterceptorStack {
   private final Object beanInstance;
   private final List<Interceptor> interceptors;
   private final Method targetMethod;
   private final Operation operation;

   public InterceptorStack(Object beanInstance, Method targetMethod, Operation operation, List<InterceptorData> interceptorDatas, Map<String, Object> interceptorInstances) {
      if (interceptorDatas == null) {
         throw new NullPointerException("interceptorDatas is null");
      } else if (interceptorInstances == null) {
         throw new NullPointerException("interceptorInstances is null");
      } else {
         this.beanInstance = beanInstance;
         this.targetMethod = targetMethod;
         this.operation = operation;
         this.interceptors = new ArrayList(interceptorDatas.size());

         for(InterceptorData interceptorData : interceptorDatas) {
            Class interceptorClass = interceptorData.getInterceptorClass();
            Object interceptorInstance = interceptorInstances.get(interceptorClass.getName());
            if (interceptorInstance == null) {
               throw new IllegalArgumentException("No interceptor of type " + interceptorClass.getName());
            }

            for(Method method : interceptorData.getMethods(operation)) {
               Object handler = DynamicProxyImplFactory.realHandler(interceptorInstance);
               Interceptor interceptor;
               if (handler != null && method.getDeclaringClass().equals(handler.getClass())) {
                  interceptor = new Interceptor(handler, method);
               } else {
                  interceptor = new Interceptor(interceptorInstance, method);
               }

               this.interceptors.add(interceptor);
            }
         }

      }
   }

   public InvocationContext createInvocationContext(Object... parameters) {
      return new ReflectionInvocationContext(this.operation, this.interceptors, this.beanInstance, this.targetMethod, parameters);
   }

   public Object invoke(Object... parameters) throws Exception {
      Object var3;
      try {
         InvocationContext invocationContext = this.createInvocationContext(parameters);
         if (ThreadContext.getThreadContext() != null) {
            ThreadContext.getThreadContext().set(InvocationContext.class, invocationContext);
         }

         var3 = invocationContext.proceed();
      } finally {
         if (ThreadContext.getThreadContext() != null) {
            ThreadContext.getThreadContext().remove(InvocationContext.class);
         }

      }

      return var3;
   }

   public Object invoke(MessageContext messageContext, Object... parameters) throws Exception {
      Object var4;
      try {
         InvocationContext invocationContext = new JaxWsInvocationContext(this.operation, this.interceptors, this.beanInstance, this.targetMethod, messageContext, parameters);
         ThreadContext.getThreadContext().set(InvocationContext.class, invocationContext);
         var4 = invocationContext.proceed();
      } finally {
         ThreadContext.getThreadContext().remove(InvocationContext.class);
      }

      return var4;
   }

   public Object invoke(javax.xml.rpc.handler.MessageContext messageContext, Object... parameters) throws Exception {
      Object var4;
      try {
         InvocationContext invocationContext = new JaxRpcInvocationContext(this.operation, this.interceptors, this.beanInstance, this.targetMethod, messageContext, parameters);
         ThreadContext.getThreadContext().set(InvocationContext.class, invocationContext);
         var4 = invocationContext.proceed();
      } finally {
         ThreadContext.getThreadContext().remove(InvocationContext.class);
      }

      return var4;
   }
}
