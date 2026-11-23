package org.apache.openejb.core.timer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.ejb.EJBException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.ThreadContext;

public class TimerServiceWrapper implements TimerService {
   public Timer createTimer(Date initialExpiration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createTimer(initialExpiration, intervalDuration, info);
   }

   public Timer createTimer(Date expiration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createTimer(expiration, info);
   }

   public Timer createTimer(long initialDuration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createTimer(initialDuration, intervalDuration, info);
   }

   public Timer createTimer(long duration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createTimer(duration, info);
   }

   public Collection<Timer> getTimers() throws IllegalStateException, EJBException {
      return this.getTimerService().getTimers();
   }

   public Collection<Timer> getAllTimers() throws IllegalStateException, EJBException {
      return Timers.all();
   }

   public Timer createSingleActionTimer(long l, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createSingleActionTimer(l, timerConfig);
   }

   public Timer createSingleActionTimer(Date date, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createSingleActionTimer(date, timerConfig);
   }

   public Timer createIntervalTimer(long l, long l1, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createIntervalTimer(l, l1, timerConfig);
   }

   public Timer createIntervalTimer(Date date, long l, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createIntervalTimer(date, l, timerConfig);
   }

   public Timer createCalendarTimer(ScheduleExpression scheduleExpression) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createCalendarTimer(scheduleExpression);
   }

   public Timer createCalendarTimer(ScheduleExpression scheduleExpression, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.getTimerService().createCalendarTimer(scheduleExpression, timerConfig);
   }

   private TimerService getTimerService() throws IllegalStateException {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext beanContext = threadContext.getBeanContext();
      return Timers.getTimerService(threadContext.getPrimaryKey(), beanContext, false);
   }
}
