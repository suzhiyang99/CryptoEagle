package org.apache.openejb.core.timer;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJBException;
import javax.ejb.NoMoreTimeoutsException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;

public class TimerImpl implements Timer, Serializable {
   private final TimerData timerData;

   public TimerImpl(TimerData timerData) {
      this.timerData = timerData;
   }

   public void cancel() throws IllegalStateException, NoSuchObjectLocalException {
      this.checkState();
      this.timerData.cancel();
   }

   public long getTimeRemaining() throws IllegalStateException, NoSuchObjectLocalException {
      this.checkState();
      Date nextTimeout = this.timerData.getNextTimeout();
      if (nextTimeout == null) {
         throw new NoMoreTimeoutsException("The timer has no future timeouts");
      } else {
         return this.timerData.getTimeRemaining();
      }
   }

   public Date getNextTimeout() throws IllegalStateException, NoSuchObjectLocalException {
      this.checkState();
      Date nextTimeout = this.timerData.getNextTimeout();
      if (nextTimeout == null) {
         throw new NoMoreTimeoutsException("The timer has no future timeouts");
      } else {
         return this.timerData.getNextTimeout();
      }
   }

   public Serializable getInfo() throws IllegalStateException, NoSuchObjectLocalException {
      this.checkState();
      return (Serializable)this.timerData.getInfo();
   }

   public TimerHandle getHandle() throws IllegalStateException, NoSuchObjectLocalException {
      this.checkState();
      if (!this.timerData.isPersistent()) {
         throw new IllegalStateException("can't getHandle for a non-persistent timer");
      } else {
         return new TimerHandleImpl(this.timerData.getId(), this.timerData.getDeploymentId());
      }
   }

   public ScheduleExpression getSchedule() throws EJBException, IllegalStateException, NoSuchObjectLocalException {
      this.checkState();
      if (this.timerData.getType() == TimerType.Calendar) {
         return ((CalendarTimerData)this.timerData).getSchedule();
      } else {
         throw new IllegalStateException("The target timer is not a calendar-based type ");
      }
   }

   public boolean isPersistent() throws EJBException, IllegalStateException, NoSuchObjectLocalException {
      this.checkState();
      return this.timerData.isPersistent();
   }

   public boolean isCalendarTimer() throws EJBException, IllegalStateException, NoSuchObjectLocalException {
      this.checkState();
      return this.timerData.getType() == TimerType.Calendar;
   }

   private void checkState() throws IllegalStateException, NoSuchObjectLocalException {
      if (this.timerData.isCancelled() && !this.timerData.isStopped()) {
         throw new NoSuchObjectLocalException("Timer has been cancelled");
      } else if (this.timerData.isExpired()) {
         throw new NoSuchObjectLocalException("The timer has expired");
      }
   }
}
