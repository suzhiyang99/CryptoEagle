package org.apache.openejb.core.timer;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import javax.ejb.TimerConfig;
import org.apache.openejb.quartz.impl.triggers.AbstractTrigger;
import org.apache.openejb.quartz.impl.triggers.SimpleTriggerImpl;

public class IntervalTimerData extends TimerData {
   private static final long serialVersionUID = 1L;
   private final long intervalDuration;
   private final Date initialExpiration;

   public IntervalTimerData(long id, EjbTimerServiceImpl timerService, String deploymentId, Object primaryKey, Method timeoutMethod, TimerConfig timerConfig, Date initialExpiration, long intervalDuration) {
      super(id, timerService, deploymentId, primaryKey, timeoutMethod, timerConfig);
      this.initialExpiration = initialExpiration;
      this.intervalDuration = intervalDuration;
   }

   public TimerType getType() {
      return TimerType.Interval;
   }

   public long getIntervalDuration() {
      return this.intervalDuration;
   }

   public Date getInitialExpiration() {
      return this.initialExpiration;
   }

   public AbstractTrigger<?> initializeTrigger() {
      SimpleTriggerImpl simpleTrigger = new SimpleTriggerImpl();
      Date startTime = new Date(this.initialExpiration.getTime());
      simpleTrigger.setStartTime(startTime);
      simpleTrigger.setRepeatInterval(this.intervalDuration);
      simpleTrigger.setRepeatCount(-1);
      return simpleTrigger;
   }

   public String toString() {
      return TimerType.Interval.name() + " initialExpiration = [" + DateFormat.getDateTimeInstance().format(this.initialExpiration) + "] intervalDuration = [" + this.intervalDuration + "]";
   }
}
