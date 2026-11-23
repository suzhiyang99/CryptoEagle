package org.apache.openejb.core.mdb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJBContext;
import javax.ejb.MessageDrivenBean;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.resource.spi.UnavailableException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.InstanceContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.core.interceptor.InterceptorStack;
import org.apache.openejb.core.timer.TimerServiceWrapper;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class MdbInstanceFactory {
   private static final Logger logger;
   private final BeanContext beanContext;
   private final int instanceLimit;
   private int instanceCount;
   private final MdbContext mdbContext;

   public MdbInstanceFactory(BeanContext beanContext, SecurityService securityService, int instanceLimit) throws OpenEJBException {
      this.beanContext = beanContext;
      this.instanceLimit = instanceLimit;
      this.mdbContext = new MdbContext(securityService);

      try {
         Context context = beanContext.getJndiEnc();
         context.bind("comp/EJBContext", this.mdbContext);
         context.bind("comp/TimerService", new TimerServiceWrapper());
      } catch (NamingException e) {
         throw new OpenEJBException("Failed to bind EJBContext/TimerService", e);
      }

      beanContext.set(EJBContext.class, this.mdbContext);
   }

   public int getInstanceLimit() {
      return this.instanceLimit;
   }

   public synchronized int getInstanceCount() {
      return this.instanceCount;
   }

   public Object createInstance(boolean ignoreInstanceCount) throws UnavailableException {
      if (!ignoreInstanceCount) {
         synchronized(this) {
            if (this.instanceLimit > 0 && this.instanceCount >= this.instanceLimit) {
               throw new UnavailableException("Only " + this.instanceLimit + " instances can be created");
            }

            ++this.instanceCount;
         }
      }

      try {
         Object bean = this.constructBean();
         return bean;
      } catch (UnavailableException e) {
         if (!ignoreInstanceCount) {
            synchronized(this) {
               --this.instanceCount;
            }
         }

         throw e;
      }
   }

   public void freeInstance(Instance instance, boolean ignoredInstanceCount) {
      if (instance == null) {
         throw new NullPointerException("bean is null");
      } else {
         if (!ignoredInstanceCount) {
            synchronized(this) {
               --this.instanceCount;
            }
         }

         ThreadContext callContext = ThreadContext.getThreadContext();
         Operation originalOperation = callContext == null ? null : callContext.getCurrentOperation();
         BaseContext.State[] originalAllowedStates = callContext == null ? null : callContext.getCurrentAllowedStates();

         try {
            if (callContext != null) {
               callContext.setCurrentOperation(Operation.PRE_DESTROY);
            }

            Method remove = instance.bean instanceof MessageDrivenBean ? MessageDrivenBean.class.getMethod("ejbRemove") : null;
            List<InterceptorData> callbackInterceptors = this.beanContext.getCallbackInterceptors();
            InterceptorStack interceptorStack = new InterceptorStack(instance.bean, remove, Operation.PRE_DESTROY, callbackInterceptors, instance.interceptors);
            interceptorStack.invoke();
            if (instance.creationalContext != null) {
               instance.creationalContext.release();
            }
         } catch (Throwable re) {
            logger.error("The bean instance " + instance.bean + " threw a system exception:" + re, re);
         } finally {
            if (callContext != null) {
               callContext.setCurrentOperation(originalOperation);
               callContext.setCurrentAllowedStates(originalAllowedStates);
            }

         }

      }
   }

   public Object recreateInstance(Object bean) throws UnavailableException {
      if (bean == null) {
         throw new NullPointerException("bean is null");
      } else {
         Object newBean = this.constructBean();
         return newBean;
      }
   }

   private Object constructBean() throws UnavailableException {
      BeanContext beanContext = this.beanContext;
      ThreadContext callContext = new ThreadContext(beanContext, (Object)null, Operation.INJECTION);
      ThreadContext oldContext = ThreadContext.enter(callContext);

      Instance var14;
      try {
         InstanceContext context = beanContext.newInstance();
         if (context.getBean() instanceof MessageDrivenBean) {
            callContext.setCurrentOperation(Operation.CREATE);
            Method create = beanContext.getCreateMethod();
            InterceptorStack ejbCreate = new InterceptorStack(context.getBean(), create, Operation.CREATE, new ArrayList(), new HashMap());
            ejbCreate.invoke();
         }

         var14 = new Instance(context.getBean(), context.getInterceptors(), context.getCreationalContext());
      } catch (Throwable var10) {
         Throwable e = var10;
         if (var10 instanceof InvocationTargetException) {
            e = ((InvocationTargetException)var10).getTargetException();
         }

         String message = "The bean instance threw a system exception:" + e;
         logger.error(message, e);
         throw new UnavailableException(message, e);
      } finally {
         ThreadContext.exit(oldContext);
      }

      return var14;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }
}
