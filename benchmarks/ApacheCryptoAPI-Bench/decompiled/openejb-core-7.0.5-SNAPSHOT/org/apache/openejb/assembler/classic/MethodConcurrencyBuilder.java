package org.apache.openejb.assembler.classic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ejb.LockType;
import org.apache.openejb.BeanContext;
import org.apache.openejb.MethodContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class MethodConcurrencyBuilder {
   public static final Logger logger;

   public void build(HashMap<String, BeanContext> deployments, List<MethodConcurrencyInfo> methodConcurrencys) throws OpenEJBException {
      for(BeanContext beanContext : deployments.values()) {
         applyConcurrencyAttributes(beanContext, methodConcurrencys);
      }

   }

   public static void applyConcurrencyAttributes(BeanContext beanContext, List<MethodConcurrencyInfo> methodConcurrencyInfos) throws OpenEJBException {
      if (!beanContext.isBeanManagedConcurrency()) {
         Logger log = Logger.getInstance(LogCategory.OPENEJB_STARTUP.createChild("attributes"), MethodConcurrencyBuilder.class);
         List<MethodConcurrencyInfo> lockInfos = new ArrayList();
         List<MethodConcurrencyInfo> accessTimeoutInfos = new ArrayList();
         normalize(methodConcurrencyInfos, lockInfos, accessTimeoutInfos);
         Map<Method, MethodAttributeInfo> attributes = MethodInfoUtil.resolveAttributes(lockInfos, beanContext);
         if (log.isDebugEnabled()) {
            for(Map.Entry<Method, MethodAttributeInfo> entry : attributes.entrySet()) {
               Method method = (Method)entry.getKey();
               MethodConcurrencyInfo value = (MethodConcurrencyInfo)entry.getValue();
               log.debug("Lock: " + method + " -- " + MethodInfoUtil.toString((MethodInfo)value.methods.get(0)) + " " + value.concurrencyAttribute);
            }
         }

         for(Map.Entry<Method, MethodAttributeInfo> entry : attributes.entrySet()) {
            MethodConcurrencyInfo value = (MethodConcurrencyInfo)entry.getValue();
            MethodContext methodContext = beanContext.getMethodContext((Method)entry.getKey());
            String s = value.concurrencyAttribute.toUpperCase();
            methodContext.setLockType(LockType.valueOf(s));
         }

         attributes = MethodInfoUtil.resolveAttributes(accessTimeoutInfos, beanContext);
         if (log.isDebugEnabled()) {
            for(Map.Entry<Method, MethodAttributeInfo> entry : attributes.entrySet()) {
               Method method = (Method)entry.getKey();
               MethodConcurrencyInfo value = (MethodConcurrencyInfo)entry.getValue();
               log.debug("AccessTimeout: " + method + " -- " + MethodInfoUtil.toString((MethodInfo)value.methods.get(0)) + "  " + value.accessTimeout.time + " " + value.accessTimeout.unit);
            }
         }

         for(Map.Entry<Method, MethodAttributeInfo> entry : attributes.entrySet()) {
            MethodConcurrencyInfo value = (MethodConcurrencyInfo)entry.getValue();
            MethodContext methodContext = beanContext.getMethodContext((Method)entry.getKey());
            Duration accessTimeout = new Duration(value.accessTimeout.time, TimeUnit.valueOf(value.accessTimeout.unit));
            methodContext.setAccessTimeout(accessTimeout);
         }

      }
   }

   public static void normalize(List<MethodConcurrencyInfo> infos, List<MethodConcurrencyInfo> lockInfos, List<MethodConcurrencyInfo> accessTimeoutInfos) {
      for(MethodConcurrencyInfo oldInfo : infos) {
         for(MethodInfo methodInfo : oldInfo.methods) {
            MethodConcurrencyInfo newInfo = new MethodConcurrencyInfo();
            newInfo.description = oldInfo.description;
            newInfo.methods.add(methodInfo);
            newInfo.concurrencyAttribute = oldInfo.concurrencyAttribute;
            newInfo.accessTimeout = oldInfo.accessTimeout;
            if (oldInfo.concurrencyAttribute != null) {
               lockInfos.add(newInfo);
            }

            if (oldInfo.accessTimeout != null) {
               accessTimeoutInfos.add(newInfo);
            }
         }
      }

      Collections.reverse(lockInfos);
      Collections.sort(lockInfos, new MethodConcurrencyComparator());
      Collections.reverse(accessTimeoutInfos);
      Collections.sort(accessTimeoutInfos, new MethodConcurrencyComparator());
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, MethodConcurrencyBuilder.class);
   }

   public static class MethodConcurrencyComparator extends MethodInfoUtil.BaseComparator<MethodConcurrencyInfo> {
      public int compare(MethodConcurrencyInfo a, MethodConcurrencyInfo b) {
         return this.compare((MethodInfo)((MethodInfo)a.methods.get(0)), (MethodInfo)((MethodInfo)b.methods.get(0)));
      }
   }
}
