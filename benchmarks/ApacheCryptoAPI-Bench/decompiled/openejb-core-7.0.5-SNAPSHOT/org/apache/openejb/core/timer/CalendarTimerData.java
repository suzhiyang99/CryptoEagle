package org.apache.openejb.core.timer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;
import org.apache.openejb.quartz.impl.triggers.AbstractTrigger;

public class CalendarTimerData extends TimerData {
   private static final long serialVersionUID = 1L;
   private ScheduleExpression scheduleExpression;
   private boolean autoCreated;

   public CalendarTimerData(long id, EjbTimerServiceImpl timerService, String deploymentId, Object primaryKey, Method timeoutMethod, TimerConfig timerConfig, ScheduleExpression scheduleExpression, boolean auto) {
      super(id, timerService, deploymentId, primaryKey, timeoutMethod, timerConfig);
      this.scheduleExpression = scheduleExpression;
      this.autoCreated = auto;
   }

   public TimerType getType() {
      return TimerType.Calendar;
   }

   public ScheduleExpression getSchedule() {
      return this.scheduleExpression;
   }

   public boolean isAutoCreated() {
      return this.autoCreated;
   }

   public AbstractTrigger<?> initializeTrigger() {
      try {
         return new EJBCronTrigger(this.scheduleExpression);
      } catch (EJBCronTrigger.ParseException e) {
         throw new IllegalArgumentException("Fail to parse schedule expression " + this.scheduleExpression, e);
      }
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      super.doWriteObject(out);
      out.writeBoolean(this.autoCreated);
      out.writeObject(this.scheduleExpression);
   }

   private void readObject(ObjectInputStream in) throws IOException {
      super.doReadObject(in);
      this.autoCreated = in.readBoolean();

      try {
         this.scheduleExpression = (ScheduleExpression)ScheduleExpression.class.cast(in.readObject());
      } catch (ClassNotFoundException e) {
         throw new IOException(e);
      }
   }

   public String toString() {
      return TimerType.Calendar.name() + " scheduleExpression = [" + this.scheduleExpression + "]";
   }
}
