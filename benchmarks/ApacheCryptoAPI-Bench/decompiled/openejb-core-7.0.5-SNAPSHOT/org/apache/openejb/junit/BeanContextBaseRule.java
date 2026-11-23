package org.apache.openejb.junit;

import org.apache.openejb.BeanContext;
import org.apache.openejb.Injector;
import org.apache.openejb.core.ThreadContext;

public abstract class BeanContextBaseRule {
   protected final Object instance;

   protected BeanContextBaseRule(Object instance) {
      this.instance = instance;
   }

   protected BeanContext getBeanContext() {
      BeanContext beanContext = null;
      if (this.instance != null) {
         beanContext = Injector.resolve(this.instance.getClass());
      }

      if (beanContext == null) {
         ThreadContext threadContext = ThreadContext.getThreadContext();
         if (threadContext != null) {
            beanContext = threadContext.getBeanContext();
         }
      }

      if (beanContext == null) {
         throw new IllegalStateException("Can't find test BeanContext");
      } else {
         return beanContext;
      }
   }
}
