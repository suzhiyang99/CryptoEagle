package org.apache.openejb.core.timer;

import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;

public class ScheduleData {
   private final ScheduleExpression expression;
   private final TimerConfig config;

   public ScheduleData(TimerConfig config, ScheduleExpression expression) {
      this.config = config;
      this.expression = expression;
   }

   public TimerConfig getConfig() {
      return this.config;
   }

   public ScheduleExpression getExpression() {
      return this.expression;
   }
}
