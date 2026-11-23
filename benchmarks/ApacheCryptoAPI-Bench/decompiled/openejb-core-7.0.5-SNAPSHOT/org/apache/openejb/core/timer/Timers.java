package org.apache.openejb.core.timer;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.MethodContext;
import org.apache.openejb.ModuleContext;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public final class Timers {
   private static final Logger LOGGER;

   private Timers() {
   }

   public static Collection<Timer> all() {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext beanContext = threadContext.getBeanContext();
      ModuleContext module = beanContext.getModuleContext();
      Collection<Timer> timers = new HashSet();

      for(BeanContext c : module.getAppContext().getBeanContexts()) {
         if (c.getModuleContext() == module) {
            if (c.getComponentType() != BeanType.STATEFUL) {
               TimerService timerService = getTimerService((Object)null, c, true);
               if (timerService != null) {
                  Collection<Timer> beanTimers = timerService.getTimers();
                  timers.addAll(beanTimers);
               }
            } else {
               TimerService timerService = getTimerService((Object)null, c, true);
               if (timerService != null) {
                  Collection<Timer> beanTimers = timerService.getTimers();
                  timers.addAll(beanTimers);
               }
            }
         }
      }

      return timers;
   }

   public static TimerService getTimerService(Object pk, BeanContext beanContext, boolean nullIfNotRelevant) throws IllegalStateException {
      EjbTimerService timerService = beanContext.getEjbTimerService();
      if (timerService == null) {
         if (nullIfNotRelevant) {
            return null;
         } else {
            throw new IllegalStateException("This ejb does not support timers " + beanContext.getDeploymentID());
         }
      } else {
         if (beanContext.getEjbTimeout() == null) {
            HasSchedule hasSchedule = (HasSchedule)beanContext.get(HasSchedule.class);
            boolean hasSchedules = false;
            if (hasSchedule != null) {
               hasSchedules = hasSchedule.value;
            } else {
               Iterator<Map.Entry<Method, MethodContext>> it = beanContext.iteratorMethodContext();

               while(it.hasNext()) {
                  Map.Entry<Method, MethodContext> entry = (Map.Entry)it.next();
                  MethodContext methodContext = (MethodContext)entry.getValue();
                  if (methodContext.getSchedules().size() > 0) {
                     hasSchedules = true;
                  }
               }

               synchronized(beanContext) {
                  if (beanContext.get(HasSchedule.class) == null) {
                     beanContext.set(HasSchedule.class, new HasSchedule(hasSchedules));
                  }
               }
            }

            if (!hasSchedules) {
               if (nullIfNotRelevant) {
                  return null;
               }

               LOGGER.error("This ejb does not support timers " + beanContext.getDeploymentID() + " due to no timeout method nor schedules in methodContext is configured");
            }
         }

         return new TimerServiceImpl(timerService, pk, beanContext.getEjbTimeout());
      }
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.TIMER, TimerServiceWrapper.class);
   }

   private static final class HasSchedule {
      private final boolean value;

      private HasSchedule(boolean value) {
         this.value = value;
      }
   }
}
