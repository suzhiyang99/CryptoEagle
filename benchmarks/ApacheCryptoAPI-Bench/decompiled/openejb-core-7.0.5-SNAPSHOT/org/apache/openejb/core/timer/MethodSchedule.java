package org.apache.openejb.core.timer;

import java.lang.reflect.Method;
import java.util.List;

public class MethodSchedule {
   private final Method method;
   private final List<ScheduleData> schedules;

   public MethodSchedule(Method method, List<ScheduleData> schedules) {
      this.method = method;
      this.schedules = schedules;
   }

   public Method getMethod() {
      return this.method;
   }

   public List<ScheduleData> getSchedules() {
      return this.schedules;
   }
}
