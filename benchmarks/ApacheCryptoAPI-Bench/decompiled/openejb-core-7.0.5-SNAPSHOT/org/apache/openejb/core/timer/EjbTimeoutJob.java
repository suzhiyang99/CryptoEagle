package org.apache.openejb.core.timer;

import org.apache.openejb.quartz.Job;
import org.apache.openejb.quartz.JobDataMap;
import org.apache.openejb.quartz.JobExecutionContext;
import org.apache.openejb.quartz.JobExecutionException;

public class EjbTimeoutJob implements Job {
   public static final String EJB_TIMERS_SERVICE = "EJB_TIMERS_SERVICE";
   public static final String TIMER_DATA = "TIMER_DATA";

   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
      EjbTimerServiceImpl ejbTimerService = (EjbTimerServiceImpl)jobDataMap.get("EJB_TIMERS_SERVICE");
      TimerData timerData = (TimerData)jobDataMap.get("TIMER_DATA");
      ejbTimerService.ejbTimeout(timerData);
   }
}
