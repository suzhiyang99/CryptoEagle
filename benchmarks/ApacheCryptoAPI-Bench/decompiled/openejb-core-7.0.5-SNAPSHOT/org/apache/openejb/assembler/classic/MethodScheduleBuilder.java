package org.apache.openejb.assembler.classic;

import java.lang.reflect.Method;
import java.util.Iterator;
import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;
import org.apache.openejb.BeanContext;
import org.apache.openejb.MethodContext;
import org.apache.openejb.core.timer.ScheduleData;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class MethodScheduleBuilder {
   public static final Logger logger;

   public void build(BeanContext beanContext, EnterpriseBeanInfo beanInfo) {
      Class<?> clazz = beanContext.getBeanClass();
      Iterator var4 = beanInfo.methodScheduleInfos.iterator();

      while(true) {
         MethodScheduleInfo info;
         Method timeoutMethodOfSchedule;
         while(true) {
            if (!var4.hasNext()) {
               return;
            }

            info = (MethodScheduleInfo)var4.next();
            timeoutMethodOfSchedule = null;
            if (info.method.methodParams == null) {
               logger.info("Schedule timeout method with 'null' method parameters is invalid: " + info.method.methodName);
               break;
            }

            try {
               timeoutMethodOfSchedule = MethodInfoUtil.toMethod(clazz, info.method);
               break;
            } catch (IllegalStateException e) {
               logger.warning("Schedule method does not exist: " + info.method.methodName, (Throwable)e);
            }
         }

         MethodContext methodContext = null;
         if (timeoutMethodOfSchedule == null && beanContext.getEjbTimeout() != null) {
            methodContext = beanContext.getMethodContext(beanContext.getEjbTimeout());
         } else if (info.method.className == null || timeoutMethodOfSchedule.getDeclaringClass().getName().equals(info.method.className)) {
            methodContext = beanContext.getMethodContext(timeoutMethodOfSchedule);
         }

         this.addSchedulesToMethod(methodContext, info);
      }
   }

   private void addSchedulesToMethod(MethodContext methodContext, MethodScheduleInfo info) {
      if (methodContext != null) {
         for(ScheduleInfo scheduleInfo : info.schedules) {
            ScheduleExpression expr = new ScheduleExpression();
            expr.second(scheduleInfo.second == null ? "0" : scheduleInfo.second);
            expr.minute(scheduleInfo.minute == null ? "0" : scheduleInfo.minute);
            expr.hour(scheduleInfo.hour == null ? "0" : scheduleInfo.hour);
            expr.dayOfWeek(scheduleInfo.dayOfWeek == null ? "*" : scheduleInfo.dayOfWeek);
            expr.dayOfMonth(scheduleInfo.dayOfMonth == null ? "*" : scheduleInfo.dayOfMonth);
            expr.month(scheduleInfo.month == null ? "*" : scheduleInfo.month);
            expr.year(scheduleInfo.year == null ? "*" : scheduleInfo.year);
            expr.timezone(scheduleInfo.timezone);
            expr.start(scheduleInfo.start);
            expr.end(scheduleInfo.end);
            TimerConfig config = new TimerConfig();
            config.setInfo(scheduleInfo.info);
            config.setPersistent(scheduleInfo.persistent);
            methodContext.getSchedules().add(new ScheduleData(config, expr));
         }

      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, MethodScheduleBuilder.class.getPackage().getName());
   }
}
