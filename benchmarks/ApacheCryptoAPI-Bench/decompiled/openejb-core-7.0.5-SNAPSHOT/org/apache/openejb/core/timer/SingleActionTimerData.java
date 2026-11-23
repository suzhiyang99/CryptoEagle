package org.apache.openejb.core.timer;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import javax.ejb.TimerConfig;
import org.apache.openejb.quartz.impl.triggers.AbstractTrigger;
import org.apache.openejb.quartz.impl.triggers.SimpleTriggerImpl;

public class SingleActionTimerData extends TimerData {
   private static final long serialVersionUID = 1L;
   private final Date expiration;

   public SingleActionTimerData(long id, EjbTimerServiceImpl timerService, String deploymentId, Object primaryKey, Method timeoutMethod, TimerConfig timerConfig, Date expiration) {
      super(id, timerService, deploymentId, primaryKey, timeoutMethod, timerConfig);
      this.expiration = expiration;
   }

   public TimerType getType() {
      return TimerType.SingleAction;
   }

   public Date getExpiration() {
      return this.expiration;
   }

   public AbstractTrigger<?> initializeTrigger() {
      SimpleTriggerImpl simpleTrigger = new SimpleTriggerImpl();
      simpleTrigger.setStartTime(this.expiration);
      return simpleTrigger;
   }

   public String toString() {
      return TimerType.SingleAction.name() + " expiration = [" + DateFormat.getDateTimeInstance().format(this.expiration) + "]";
   }
}
