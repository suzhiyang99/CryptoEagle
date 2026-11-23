package org.apache.openejb.core.entity;

import java.util.ArrayList;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.InternalErrorException;
import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.ivm.EjbObjectProxyHandler;
import org.apache.openejb.core.ivm.IntraVmProxy;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.proxy.ProxyManager;

public class EntityContext extends BaseContext implements javax.ejb.EntityContext {
   public EntityContext(SecurityService securityService) {
      super(securityService);
   }

   public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
      this.doCheck(BaseContext.Call.getEJBLocalObject);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      if (di.getLocalInterface() == null) {
         throw new IllegalStateException("EJB " + di.getDeploymentID() + " does not have a local interface");
      } else {
         EjbObjectProxyHandler handler = new EntityEjbObjectHandler(di, threadContext.getPrimaryKey(), InterfaceType.EJB_LOCAL, new ArrayList(), di.getLocalInterface());

         try {
            Class[] interfaces = new Class[]{di.getLocalInterface(), IntraVmProxy.class};
            return (EJBLocalObject)ProxyManager.newProxyInstance((Class[])interfaces, handler);
         } catch (IllegalAccessException iae) {
            throw new InternalErrorException("Could not create IVM proxy for " + di.getLocalInterface() + " interface", iae);
         }
      }
   }

   public EJBObject getEJBObject() throws IllegalStateException {
      this.doCheck(BaseContext.Call.getEJBObject);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      if (di.getRemoteInterface() == null) {
         throw new IllegalStateException("EJB " + di.getDeploymentID() + " does not have a remote interface");
      } else {
         EjbObjectProxyHandler handler = new EntityEjbObjectHandler(di.getContainer().getBeanContext(di.getDeploymentID()), threadContext.getPrimaryKey(), InterfaceType.EJB_OBJECT, new ArrayList(), di.getRemoteInterface());

         try {
            Class[] interfaces = new Class[]{di.getRemoteInterface(), IntraVmProxy.class};
            return (EJBObject)ProxyManager.newProxyInstance((Class[])interfaces, handler);
         } catch (IllegalAccessException iae) {
            throw new InternalErrorException("Could not create IVM proxy for " + di.getRemoteInterface() + " interface", iae);
         }
      }
   }

   public Object getPrimaryKey() throws IllegalStateException {
      this.doCheck(BaseContext.Call.getPrimaryKey);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      return threadContext.getPrimaryKey();
   }

   public void check(ThreadContext context, BaseContext.Call call) {
      Operation operation = context.getCurrentOperation();
      switch (call) {
         case getUserTransaction:
         case getContextData:
            throw this.illegal(call, operation);
         case getPrimaryKey:
         case getEJBLocalObject:
         case getEJBObject:
            switch (operation) {
               case SET_CONTEXT:
               case UNSET_CONTEXT:
               case CREATE:
               case FIND:
               case HOME:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         case isCallerInRole:
         case getCallerPrincipal:
         case setRollbackOnly:
         case getRollbackOnly:
            switch (ThreadContext.getThreadContext().getCurrentOperation()) {
               case SET_CONTEXT:
               case UNSET_CONTEXT:
               case ACTIVATE:
               case PASSIVATE:
                  throw this.illegal(call, operation);
               case CREATE:
               case FIND:
               case HOME:
               default:
                  return;
            }
         case getTimerService:
            switch (operation) {
               case SET_CONTEXT:
               case UNSET_CONTEXT:
               case FIND:
                  throw this.illegal(call, operation);
               case CREATE:
               default:
                  return;
            }
         case timerMethod:
            switch (operation) {
               case SET_CONTEXT:
               case UNSET_CONTEXT:
               case CREATE:
               case FIND:
               case HOME:
               case ACTIVATE:
               case PASSIVATE:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         default:
      }
   }
}
