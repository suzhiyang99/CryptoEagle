package org.apache.openejb.core.mdb;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.ejb.EJBException;
import javax.jms.Message;
import javax.resource.spi.ApplicationServerInternalException;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.transaction.xa.XAResource;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.SystemException;
import org.apache.openejb.resource.activemq.jms2.DelegateMessage;
import org.apache.openejb.resource.activemq.jms2.JMS2;

public class EndpointHandler implements InvocationHandler, MessageEndpoint {
   private volatile Boolean isAmq;
   private final MdbContainer container;
   private final BeanContext deployment;
   private final MdbInstanceFactory instanceFactory;
   private final XAResource xaResource;
   private State state;
   private Object instance;

   public EndpointHandler(MdbContainer container, BeanContext deployment, MdbInstanceFactory instanceFactory, XAResource xaResource) throws UnavailableException {
      this.state = EndpointHandler.State.NONE;
      this.container = container;
      this.deployment = deployment;
      this.instanceFactory = instanceFactory;
      this.xaResource = xaResource;
      this.instance = instanceFactory.createInstance(false);
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      String methodName = method.getName();
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (method.getDeclaringClass() == Object.class) {
         if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return this.toString();
         } else if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return this.equals(args[0]);
         } else if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return this.hashCode();
         } else {
            throw new UnsupportedOperationException("Unkown method: " + method);
         }
      } else if ("beforeDelivery".equals(methodName) && Arrays.deepEquals(new Class[]{Method.class}, parameterTypes)) {
         this.beforeDelivery((Method)args[0]);
         return null;
      } else if ("afterDelivery".equals(methodName) && parameterTypes.length == 0) {
         this.afterDelivery();
         return null;
      } else if ("release".equals(methodName) && parameterTypes.length == 0) {
         this.release();
         return null;
      } else {
         Object value = this.deliverMessage(method, args);
         return value;
      }
   }

   public void beforeDelivery(Method method) throws ApplicationServerInternalException {
      switch (this.state) {
         case RELEASED:
            throw new IllegalStateException("Message endpoint factory has been released");
         case BEFORE_CALLED:
            throw new IllegalStateException("beforeDelivery can not be called again until message is delivered and afterDelivery is called");
         case METHOD_CALLED:
         case SYSTEM_EXCEPTION:
            throw new IllegalStateException("The last message delivery must be completed with an afterDeliver before beforeDeliver can be called again");
         default:
            try {
               this.container.beforeDelivery(this.deployment, this.instance, method, this.xaResource);
            } catch (SystemException se) {
               Throwable throwable = (Throwable)(se.getRootCause() != null ? se.getRootCause() : se);
               throw new ApplicationServerInternalException(throwable);
            }

            this.state = EndpointHandler.State.BEFORE_CALLED;
      }
   }

   public Object deliverMessage(Method method, Object[] args) throws Throwable {
      boolean callBeforeAfter = false;
      switch (this.state) {
         case RELEASED:
            throw new IllegalStateException("Message endpoint factory has been released");
         case BEFORE_CALLED:
            this.state = EndpointHandler.State.METHOD_CALLED;
            break;
         case METHOD_CALLED:
         case SYSTEM_EXCEPTION:
            throw new IllegalStateException("The last message delivery must be completed with an afterDeliver before another message can be delivered");
         case NONE:
            try {
               this.beforeDelivery(method);
            } catch (ApplicationServerInternalException e) {
               throw (EJBException)(new EJBException()).initCause(e.getCause());
            }

            callBeforeAfter = true;
            this.state = EndpointHandler.State.METHOD_CALLED;
      }

      Throwable throwable = null;
      Object value = null;

      try {
         value = this.container.invoke(this.instance, method, (InterfaceType)null, this.wrapMessageForAmq5(args));
      } catch (SystemException se) {
         throwable = (Throwable)(se.getRootCause() != null ? se.getRootCause() : se);
         this.state = EndpointHandler.State.SYSTEM_EXCEPTION;
      } catch (ApplicationException ae) {
         throwable = (Throwable)(ae.getRootCause() != null ? ae.getRootCause() : ae);
      } finally {
         if (callBeforeAfter) {
            try {
               this.afterDelivery();
            } catch (ApplicationServerInternalException e) {
               throwable = throwable == null ? e.getCause() : throwable;
            } catch (UnavailableException e) {
               throwable = (Throwable)(throwable == null ? e : throwable);
            }
         }

      }

      if (throwable != null) {
         throwable.printStackTrace();
         if (this.isValidException(method, throwable)) {
            throw throwable;
         } else {
            throw (new EJBException()).initCause(throwable);
         }
      } else {
         return value;
      }
   }

   private Object[] wrapMessageForAmq5(Object[] args) {
      if (args != null && args.length == 1 && !DelegateMessage.class.isInstance(args[0])) {
         if (this.isAmq == null) {
            synchronized(this) {
               if (this.isAmq == null) {
                  this.isAmq = args[0].getClass().getName().startsWith("org.apache.activemq.");
               }
            }
         }

         if (this.isAmq) {
            args[0] = JMS2.wrap((Message)Message.class.cast(args[0]));
         }

         return args;
      } else {
         return args;
      }
   }

   public void afterDelivery() throws ApplicationServerInternalException, UnavailableException {
      switch (this.state) {
         case RELEASED:
            throw new IllegalStateException("Message endpoint factory has been released");
         case BEFORE_CALLED:
            throw new IllegalStateException("Exactally one message must be delivered between beforeDelivery and afterDelivery");
         case METHOD_CALLED:
         case SYSTEM_EXCEPTION:
         default:
            boolean exceptionThrown = false;

            try {
               this.container.afterDelivery(this.instance);
            } catch (SystemException se) {
               exceptionThrown = true;
               Throwable throwable = (Throwable)(se.getRootCause() != null ? se.getRootCause() : se);
               throwable.printStackTrace();
               throw new ApplicationServerInternalException(throwable);
            } finally {
               if (this.state == EndpointHandler.State.SYSTEM_EXCEPTION) {
                  this.recreateInstance(exceptionThrown);
               }

               this.state = EndpointHandler.State.NONE;
            }

            return;
         case NONE:
            throw new IllegalStateException("afterDelivery may only be called if message delivery began with a beforeDelivery call");
      }
   }

   private void recreateInstance(boolean exceptionAlreadyThrown) throws UnavailableException {
      try {
         this.instance = this.instanceFactory.recreateInstance(this.instance);
      } catch (UnavailableException e) {
         this.state = EndpointHandler.State.RELEASED;
         if (!exceptionAlreadyThrown) {
            throw e;
         }
      }

   }

   public void release() {
      if (this.state != EndpointHandler.State.RELEASED) {
         this.state = EndpointHandler.State.RELEASED;

         try {
            this.container.release(this.deployment, this.instance);
         } finally {
            this.instanceFactory.freeInstance((Instance)this.instance, false);
            this.instance = null;
         }

      }
   }

   private boolean isValidException(Method method, Throwable throwable) {
      if (!(throwable instanceof RuntimeException) && !(throwable instanceof Error)) {
         Class<?>[] exceptionTypes = method.getExceptionTypes();

         for(Class<?> exceptionType : exceptionTypes) {
            if (exceptionType.isInstance(throwable)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private static enum State {
      NONE,
      BEFORE_CALLED,
      METHOD_CALLED,
      SYSTEM_EXCEPTION,
      RELEASED;
   }
}
