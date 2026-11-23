package org.apache.openejb.core.timer;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import javax.ejb.EJBException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

public class TimerServiceImpl implements TimerService {
   private final EjbTimerService ejbTimerService;
   private final Object primaryKey;
   private final Method ejbTimeout;

   public TimerServiceImpl(EjbTimerService ejbTimerService, Object primaryKey, Method ejbTimeout) {
      this.ejbTimerService = ejbTimerService;
      this.primaryKey = primaryKey;
      this.ejbTimeout = ejbTimeout;
   }

   public Timer createTimer(Date initialExpiration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, initialExpiration, intervalDuration, new TimerConfig(info, true));
   }

   public Timer createTimer(Date expiration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, expiration, new TimerConfig(info, true));
   }

   public Timer createTimer(long initialDuration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, initialDuration, intervalDuration, new TimerConfig(info, true));
   }

   public Timer createTimer(long duration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, duration, new TimerConfig(info, true));
   }

   public Collection<Timer> getTimers() throws IllegalStateException, EJBException {
      return this.ejbTimerService.getTimers(this.primaryKey);
   }

   public Timer createSingleActionTimer(long duration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, duration, timerConfig);
   }

   public Timer createSingleActionTimer(Date expiration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, expiration, timerConfig);
   }

   public Timer createIntervalTimer(long initialDuration, long intervalDuration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, initialDuration, intervalDuration, timerConfig);
   }

   public Timer createIntervalTimer(Date initialExpiration, long lintervalDuration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, initialExpiration, lintervalDuration, timerConfig);
   }

   public Timer createCalendarTimer(ScheduleExpression scheduleExpression) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, this.copy(scheduleExpression), new TimerConfig((Serializable)null, true));
   }

   public Timer createCalendarTimer(ScheduleExpression scheduleExpression, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
      return this.ejbTimerService.createTimer(this.primaryKey, this.ejbTimeout, this.copy(scheduleExpression), timerConfig);
   }

   private ScheduleExpression copy(ScheduleExpression scheduleExpression) {
      ScheduleExpression scheduleExpressionCopy = new ScheduleExpression();
      scheduleExpressionCopy.year(scheduleExpression.getYear());
      scheduleExpressionCopy.month(scheduleExpression.getMonth());
      scheduleExpressionCopy.dayOfMonth(scheduleExpression.getDayOfMonth());
      scheduleExpressionCopy.dayOfWeek(scheduleExpression.getDayOfWeek());
      scheduleExpressionCopy.hour(scheduleExpression.getHour());
      scheduleExpressionCopy.minute(scheduleExpression.getMinute());
      scheduleExpressionCopy.second(scheduleExpression.getSecond());
      scheduleExpressionCopy.start(scheduleExpression.getStart());
      scheduleExpressionCopy.end(scheduleExpression.getEnd());
      scheduleExpressionCopy.timezone(scheduleExpression.getTimezone());
      return scheduleExpressionCopy;
   }

   public Collection<Timer> getAllTimers() throws IllegalStateException, EJBException {
      throw new UnsupportedOperationException("not expecting to call this method from this class, see TimerServiceWrapper");
   }
}
