package org.apache.openejb.core.mdb;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.management.ObjectName;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openejb.resource.XAResourceWrapper;
import org.apache.openejb.util.proxy.LocalBeanProxyFactory;

public class EndpointFactory implements MessageEndpointFactory {
   private final ActivationSpec activationSpec;
   private final MdbContainer container;
   private final BeanContext beanContext;
   private final MdbInstanceFactory instanceFactory;
   private final ClassLoader classLoader;
   private final Class[] interfaces;
   private final XAResourceWrapper xaResourceWrapper;
   protected final List<ObjectName> jmxNames = new ArrayList();
   private final Class<?> proxy;

   public EndpointFactory(ActivationSpec activationSpec, MdbContainer container, BeanContext beanContext, MdbInstanceFactory instanceFactory, XAResourceWrapper xaResourceWrapper) {
      this.activationSpec = activationSpec;
      this.container = container;
      this.beanContext = beanContext;
      this.instanceFactory = instanceFactory;
      this.classLoader = container.getMessageListenerInterface().getClassLoader();
      this.interfaces = new Class[]{container.getMessageListenerInterface(), MessageEndpoint.class};
      this.xaResourceWrapper = xaResourceWrapper;
      BeanContext.ProxyClass proxyClass = (BeanContext.ProxyClass)beanContext.get(BeanContext.ProxyClass.class);
      if (proxyClass == null) {
         this.proxy = LocalBeanProxyFactory.createProxy(beanContext.getBeanClass(), beanContext.getClassLoader(), this.interfaces);
         beanContext.set(BeanContext.ProxyClass.class, new BeanContext.ProxyClass(beanContext, this.interfaces));
      } else {
         this.proxy = proxyClass.getProxy();
      }

   }

   public ActivationSpec getActivationSpec() {
      return this.activationSpec;
   }

   public MdbInstanceFactory getInstanceFactory() {
      return this.instanceFactory;
   }

   public MessageEndpoint createEndpoint(XAResource xaResource) throws UnavailableException {
      if (xaResource != null && this.xaResourceWrapper != null) {
         xaResource = this.xaResourceWrapper.wrap(xaResource, this.container.getContainerID().toString());
      }

      EndpointHandler endpointHandler = new EndpointHandler(this.container, this.beanContext, this.instanceFactory, xaResource);

      try {
         return (MessageEndpoint)LocalBeanProxyFactory.constructProxy(this.proxy, endpointHandler);
      } catch (InternalError e) {
         try {
            return (MessageEndpoint)MessageEndpoint.class.cast(LocalBeanProxyFactory.newProxyInstance(Thread.currentThread().getContextClassLoader(), endpointHandler, this.beanContext.getBeanClass(), this.interfaces));
         } catch (InternalError var7) {
            try {
               return (MessageEndpoint)MessageEndpoint.class.cast(LocalBeanProxyFactory.newProxyInstance(this.classLoader, endpointHandler, this.beanContext.getBeanClass(), this.interfaces));
            } catch (InternalError var6) {
               throw e;
            }
         }
      }
   }

   public MessageEndpoint createEndpoint(XAResource xaResource, long timeout) throws UnavailableException {
      if (timeout <= 0L) {
         return this.createEndpoint(xaResource);
      } else {
         long end = System.currentTimeMillis() + timeout;
         MessageEndpoint messageEndpoint = null;

         while(System.currentTimeMillis() <= end) {
            try {
               messageEndpoint = this.createEndpoint(xaResource);
               break;
            } catch (Exception var8) {
            }
         }

         if (messageEndpoint != null) {
            return messageEndpoint;
         } else {
            throw new UnavailableException("Unable to create end point within the specified timeout " + timeout);
         }
      }
   }

   public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
      TransactionType transactionType = this.beanContext.getTransactionType(method);
      return TransactionType.Required == transactionType;
   }
}
