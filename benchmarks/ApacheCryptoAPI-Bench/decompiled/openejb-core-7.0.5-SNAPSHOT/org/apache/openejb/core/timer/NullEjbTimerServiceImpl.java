package org.apache.openejb.core.timer;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class NullEjbTimerServiceImpl implements EjbTimerService {
   private static final Logger log;

   public Timer createTimer(Object primaryKey, Method timeoutMethod, ScheduleExpression schedule, TimerConfig timerConfig) {
      log.error("Null ! TimerService operation not supported for a bean without an ejbTimeout method or auto-started task");
      return null;
   }

   public Timer createTimer(Object primaryKey, Method timeoutMethod, Date initialExpiration, long intervalDuration, TimerConfig timerConfig) {
      log.error("Null ! TimerService operation not supported for a bean without an ejbTimeout method  or auto-started task");
      return null;
   }

   public Timer createTimer(Object primaryKey, Method timeoutMethod, Date expiration, TimerConfig timerConfig) {
      log.error("Null ! TimerService operation not supported for a bean without an ejbTimeout method  or auto-started task");
      return null;
   }

   public Timer createTimer(Object primaryKey, Method timeoutMethod, long initialDuration, long intervalDuration, TimerConfig timerConfig) {
      log.error("Null ! TimerService operation not supported for a bean without an ejbTimeout method  or auto-started task");
      return null;
   }

   public Timer createTimer(Object primaryKey, Method timeoutMethod, long duration, TimerConfig timerConfig) {
      log.error("Null ! TimerService operation not supported for a bean without an ejbTimeout method  or auto-started task");
      return null;
   }

   public Timer getTimer(long id) {
      log.error("Null ! TimerService operation not supported for a bean without an ejbTimeout method  or auto-started task");
      return null;
   }

   public Collection<Timer> getTimers(Object primaryKey) {
      log.error("Null ! TimerService operation not supported for a bean without an ejbTimeout method  or auto-started task");
      return null;
   }

   public void start() throws OpenEJBException {
   }

   public void stop() {
   }

   public TimerStore getTimerStore() {
      return null;
   }

   public boolean isStarted() {
      return true;
   }

   static {
      log = Logger.getInstance(LogCategory.TIMER, NullEjbTimerServiceImpl.class);
   }
}
