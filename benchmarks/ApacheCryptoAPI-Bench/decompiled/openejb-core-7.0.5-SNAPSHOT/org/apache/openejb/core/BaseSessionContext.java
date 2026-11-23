package org.apache.openejb.core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.SessionContext;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.InternalErrorException;
import org.apache.openejb.core.ivm.EjbObjectProxyHandler;
import org.apache.openejb.core.ivm.IntraVmProxy;
import org.apache.openejb.core.managed.ManagedObjectHandler;
import org.apache.openejb.core.singleton.SingletonEjbObjectHandler;
import org.apache.openejb.core.stateful.StatefulEjbObjectHandler;
import org.apache.openejb.core.stateless.StatelessEjbObjectHandler;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.proxy.LocalBeanProxyFactory;
import org.apache.openejb.util.proxy.ProxyManager;

public abstract class BaseSessionContext extends BaseContext implements SessionContext {
   protected BaseSessionContext(SecurityService securityService) {
      super(securityService);
   }

   public BaseSessionContext(SecurityService securityService, UserTransaction userTransaction) {
      super(securityService, userTransaction);
   }

   public boolean wasCancelCalled() {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      Method runningMethod = (Method)threadContext.get(Method.class);
      if (runningMethod == null) {
         throw new IllegalStateException("No running method");
      } else if (di.isAsynchronous(runningMethod)) {
         if (runningMethod.getReturnType() == Void.TYPE) {
            throw new IllegalStateException("Current running method " + runningMethod.getName() + " is an asynchronous method, but its return type is void :" + di.getDestinationId());
         } else {
            return ThreadContext.isAsynchronousCancelled();
         }
      } else {
         throw new IllegalStateException("Current running method " + runningMethod.getName() + " is not an asynchronous method :" + di.getDestinationId());
      }
   }

   public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
      this.doCheck(BaseContext.Call.getEJBLocalObject);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      if (di.getLocalHomeInterface() == null) {
         throw new IllegalStateException("Bean does not have an EJBLocalObject interface: " + di.getDeploymentID());
      } else {
         return (EJBLocalObject)EjbObjectProxyHandler.createProxy(di, threadContext.getPrimaryKey(), InterfaceType.EJB_LOCAL, di.getLocalInterface());
      }
   }

   public EJBObject getEJBObject() throws IllegalStateException {
      this.doCheck(BaseContext.Call.getEJBObject);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      if (di.getHomeInterface() == null) {
         throw new IllegalStateException("Bean does not have an EJBObject interface: " + di.getDeploymentID());
      } else {
         return (EJBObject)EjbObjectProxyHandler.createProxy(di, threadContext.getPrimaryKey(), InterfaceType.EJB_OBJECT, di.getRemoteInterface());
      }
   }

   public MessageContext getMessageContext() throws IllegalStateException {
      this.doCheck(BaseContext.Call.getMessageContext);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      MessageContext messageContext = (MessageContext)threadContext.get(MessageContext.class);
      if (messageContext == null) {
         throw new IllegalStateException("Only calls on the service-endpoint have a MessageContext.");
      } else {
         return messageContext;
      }
   }

   public Object getBusinessObject(Class interfce) {
      this.doCheck(BaseContext.Call.getBusinessObject);
      if (interfce == null) {
         throw new IllegalStateException("Interface argument cannot me null.");
      } else {
         ThreadContext threadContext = ThreadContext.getThreadContext();
         BeanContext di = threadContext.getBeanContext();
         InterfaceType interfaceType = di.getInterfaceType(interfce);
         BeanType type = di.getComponentType();
         if (interfaceType == null) {
            throw new IllegalStateException("Component has no such interface: " + interfce.getName());
         } else if (!interfaceType.isBusiness()) {
            throw new IllegalStateException("Interface is not a business interface for this bean: " + interfce.getName());
         } else {
            try {
               EjbObjectProxyHandler handler;
               switch (di.getComponentType()) {
                  case STATEFUL:
                     handler = new StatefulEjbObjectHandler(di, threadContext.getPrimaryKey(), interfaceType, new ArrayList(), interfce);
                     break;
                  case STATELESS:
                     handler = new StatelessEjbObjectHandler(di, threadContext.getPrimaryKey(), interfaceType, new ArrayList(), interfce);
                     break;
                  case SINGLETON:
                     handler = new SingletonEjbObjectHandler(di, threadContext.getPrimaryKey(), interfaceType, new ArrayList(), interfce);
                     break;
                  case MANAGED:
                     handler = new ManagedObjectHandler(di, threadContext.getPrimaryKey(), interfaceType, new ArrayList(), interfce);
                     break;
                  default:
                     throw new IllegalStateException("Bean is not a session bean: " + di.getComponentType());
               }

               if (InterfaceType.LOCALBEAN.equals(interfaceType)) {
                  return LocalBeanProxyFactory.constructProxy(((BeanContext.ProxyClass)di.get(BeanContext.ProxyClass.class)).getProxy(), handler);
               } else {
                  List<Class> interfaces = new ArrayList();
                  interfaces.addAll(di.getInterfaces(interfaceType));
                  interfaces.add(Serializable.class);
                  interfaces.add(IntraVmProxy.class);
                  if (BeanType.STATEFUL.equals(type) || BeanType.MANAGED.equals(type)) {
                     interfaces.add(BeanContext.Removable.class);
                  }

                  return ProxyManager.newProxyInstance((Class[])((Class[])interfaces.toArray(new Class[interfaces.size()])), handler);
               }
            } catch (IllegalAccessException iae) {
               throw new InternalErrorException("Could not create IVM proxy for " + interfce.getName() + " interface", iae);
            }
         }
      }
   }

   public Class getInvokedBusinessInterface() {
      this.doCheck(BaseContext.Call.getInvokedBusinessInterface);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      Class invokedInterface = threadContext.getInvokedInterface();
      InterfaceType type = threadContext.getBeanContext().getInterfaceType(invokedInterface);
      if (!type.isBusiness()) {
         throw new IllegalStateException("The EJB spec requires us to cripple the use of this method for anything but business interface proxy.  But FYI, your invoked interface is: " + invokedInterface.getName());
      } else if (invokedInterface == null) {
         throw new IllegalStateException("Business interface not set into ThreadContext.");
      } else {
         return invokedInterface;
      }
   }
}
