package org.apache.openejb.core.interceptor;

public class InterceptorInstance {
   private final Object interceptor;
   private final InterceptorData data;

   public InterceptorInstance(Object interceptor, InterceptorData data) {
      this.interceptor = interceptor;
      this.data = data;
   }

   public InterceptorInstance(Object interceptor) {
      this(interceptor, InterceptorData.scan(interceptor.getClass()));
   }

   public Object getInterceptor() {
      return this.interceptor;
   }

   public InterceptorData getData() {
      return this.data;
   }
}
