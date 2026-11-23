package org.apache.openejb.core.interceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.interceptor.InvocationContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.util.Classes;

public class ReflectionInvocationContext implements InvocationContext {
   private final Iterator<Interceptor> interceptors;
   private final Object target;
   private final Method method;
   private final Object[] parameters;
   private final Map<String, Object> contextData = new TreeMap();
   private final Class<?>[] parameterTypes;
   private final Operation operation;

   public ReflectionInvocationContext(Operation operation, List<Interceptor> interceptors, Object target, Method method, Object... parameters) {
      if (operation == null) {
         throw new NullPointerException("operation is null");
      } else if (interceptors == null) {
         throw new NullPointerException("interceptors is null");
      } else if (target == null) {
         throw new NullPointerException("target is null");
      } else {
         this.operation = operation;
         this.interceptors = interceptors.iterator();
         this.target = target;
         this.method = method;
         this.parameters = parameters;
         if (method == null) {
            this.parameterTypes = new Class[0];
         } else {
            this.parameterTypes = method.getParameterTypes();
         }

      }
   }

   public Object getTimer() {
      return this.operation.equals(Operation.TIMEOUT) ? this.parameters[0] : null;
   }

   public Object getTarget() {
      return this.target;
   }

   public Method getMethod() {
      return this.method;
   }

   public Constructor<?> getConstructor() {
      throw new IllegalStateException();
   }

   public Object[] getParameters() {
      if (!Operation.POST_CONSTRUCT.equals(this.operation) && !Operation.PRE_DESTROY.equals(this.operation)) {
         return this.parameters;
      } else {
         throw new IllegalStateException(this.getIllegalParameterAccessMessage());
      }
   }

   private String getIllegalParameterAccessMessage() {
      String m = "Callback methods cannot access parameters.";
      m = m + "  Callback Type: " + this.operation;
      if (this.method != null) {
         m = m + ", Target Method: " + this.method.getName();
      }

      if (this.target != null) {
         m = m + ", Target Bean: " + this.target.getClass().getName();
      }

      return m;
   }

   public void setParameters(Object[] parameters) {
      if (this.operation.isCallback() && !this.operation.equals(Operation.TIMEOUT)) {
         throw new IllegalStateException(this.getIllegalParameterAccessMessage());
      } else if (parameters == null) {
         throw new IllegalArgumentException("parameters is null");
      } else if (parameters.length != this.parameters.length) {
         throw new IllegalArgumentException("Expected " + this.parameters.length + " parameters, but only got " + parameters.length + " parameters");
      } else {
         for(int i = 0; i < parameters.length; ++i) {
            Object parameter = parameters[i];
            Class<?> parameterType = this.parameterTypes[i];
            if (parameter == null) {
               if (parameterType.isPrimitive()) {
                  throw new IllegalArgumentException("Expected parameter " + i + " to be primitive type " + parameterType.getName() + ", but got a parameter that is null");
               }
            } else {
               Class<?> actual = Classes.deprimitivize(parameterType);
               Class<?> given = Classes.deprimitivize(parameter.getClass());
               if (!actual.isAssignableFrom(given)) {
                  throw new IllegalArgumentException("Expected parameter " + i + " to be of type " + parameterType.getName() + ", but got a parameter of type " + parameter.getClass().getName());
               }
            }
         }

         System.arraycopy(parameters, 0, this.parameters, 0, parameters.length);
      }
   }

   public Map<String, Object> getContextData() {
      return this.contextData;
   }

   private Invocation next() {
      if (this.interceptors.hasNext()) {
         Interceptor interceptor = (Interceptor)this.interceptors.next();
         Object nextInstance = interceptor.getInstance();
         Method nextMethod = interceptor.getMethod();
         return (Invocation)(nextMethod.getParameterTypes().length == 1 && nextMethod.getParameterTypes()[0] == InvocationContext.class ? new InterceptorInvocation(nextInstance, nextMethod, this) : new LifecycleInvocation(nextInstance, nextMethod, this, this.parameters));
      } else if (this.method == null) {
         return new NoOpInvocation();
      } else {
         Object[] methodParameters;
         if (this.operation.equals(Operation.TIMEOUT) && this.method.getParameterTypes().length == 0) {
            methodParameters = new Object[0];
         } else {
            methodParameters = this.parameters;
         }

         return new BeanInvocation(this.target, this.method, methodParameters);
      }
   }

   public Object proceed() throws Exception {
      try {
         Invocation next = this.next();
         return next.invoke();
      } catch (InvocationTargetException e) {
         throw this.unwrapInvocationTargetException(e);
      }
   }

   private Exception unwrapInvocationTargetException(InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause == null) {
         return e;
      } else if (cause instanceof Exception) {
         return (Exception)cause;
      } else if (cause instanceof Error) {
         throw (Error)cause;
      } else {
         throw new AssertionError(cause);
      }
   }

   public String toString() {
      String methodName = this.method != null ? this.method.getName() : null;
      return "InvocationContext(operation=" + this.operation + ", target=" + this.target.getClass().getName() + ", method=" + methodName + ")";
   }

   private abstract static class Invocation {
      private final Method method;
      private final Object[] args;
      private final Object target;

      public Invocation(Object target, Method method, Object[] args) {
         this.target = target;
         this.method = method;
         this.args = args;
      }

      public Object invoke() throws Exception {
         Object value = this.method.invoke(this.target, this.args);
         return value;
      }

      public String toString() {
         return this.method.getDeclaringClass().getName() + "." + this.method.getName();
      }
   }

   private static class BeanInvocation extends Invocation {
      public BeanInvocation(Object target, Method method, Object[] args) {
         super(target, method, args);
      }
   }

   private static class InterceptorInvocation extends Invocation {
      public InterceptorInvocation(Object target, Method method, InvocationContext invocationContext) {
         super(target, method, new Object[]{invocationContext});
      }
   }

   private static class LifecycleInvocation extends Invocation {
      private final InvocationContext invocationContext;

      public LifecycleInvocation(Object target, Method method, InvocationContext invocationContext, Object[] args) {
         super(target, method, args);
         this.invocationContext = invocationContext;
      }

      public Object invoke() throws Exception {
         super.invoke();
         Object value = this.invocationContext.proceed();
         return value;
      }
   }

   private static class NoOpInvocation extends Invocation {
      public NoOpInvocation() {
         super((Object)null, (Method)null, (Object[])null);
      }

      public Object invoke() throws IllegalAccessException, InvocationTargetException {
         return null;
      }
   }
}
