package org.apache.openejb.resource.quartz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.endpoint.MessageEndpoint;
import org.apache.openejb.quartz.CronScheduleBuilder;
import org.apache.openejb.quartz.JobBuilder;
import org.apache.openejb.quartz.JobDetail;
import org.apache.openejb.quartz.JobKey;
import org.apache.openejb.quartz.SchedulerException;
import org.apache.openejb.quartz.Trigger;
import org.apache.openejb.quartz.TriggerBuilder;
import org.apache.openejb.quartz.TriggerKey;
import org.apache.openejb.quartz.impl.triggers.CronTriggerImpl;

public final class JobSpec implements ActivationSpec {
   private MessageEndpoint endpoint;
   private ResourceAdapter resourceAdapter;
   private Trigger trigger;
   private JobDetail detail;
   private InvalidPropertyException invalidProperty;
   private String triggerName;
   private String triggerGroup;
   private String jobName;
   private String jobGroup;
   private String description;
   private boolean recoverable;
   private boolean durable;
   private String calendarName;
   private String cronExpression;
   private String timeZone;
   private String startTime;
   private String endTime;

   public TriggerKey triggerKey() {
      return this.trigger.getKey();
   }

   public String getTriggerName() {
      return this.triggerName;
   }

   public void setTriggerName(String s) {
      this.triggerName = s;
   }

   public String getTriggerGroup() {
      return this.triggerGroup;
   }

   public void setTriggerGroup(String s) {
      this.triggerGroup = s;
   }

   public String getJobName() {
      return this.jobName;
   }

   public void setJobName(String s) {
      this.jobName = s;
   }

   public void setJobGroup(String s) {
      this.jobGroup = s;
   }

   public String getJobGroup() {
      return this.jobGroup;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String s) {
      this.description = s;
   }

   public void setRequestsRecovery(boolean b) {
      this.recoverable = b;
   }

   public boolean isRequestsRecovery() {
      return this.recoverable;
   }

   public boolean isDurable() {
      return this.durable;
   }

   public void setDurable(boolean b) {
      this.durable = b;
   }

   public void setCalendarName(String s) {
      this.calendarName = s;
   }

   public String getCalendarName() {
      return this.calendarName;
   }

   public void setCronExpression(String s) {
      this.cronExpression = s;
   }

   public String getCronExpression() {
      return this.cronExpression;
   }

   public void setCronTrigger(String s) {
      this.setCronExpression(s);
   }

   public void setTimeZone(String timeZone) {
      this.timeZone = timeZone;
   }

   public void setStartTime(String startTime) {
      Date date = this.parse(startTime);
      if (date != null) {
         this.startTime = startTime;
      }

   }

   public void setEndTime(String endTime) {
      Date date = this.parse(endTime);
      if (date != null) {
         this.endTime = endTime;
      }

   }

   private Date parse(String value) {
      String[] formats = new String[]{"EEE MMM d HH:mm:ss z yyyy", "EEE, d MMM yyyy HH:mm:ss Z", "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss.SZ", "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ssZ", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mmZ", "yyyy-MM-dd HH:mm", "yyyy-MM-dd'T'HH:mm:ss.SZ", "yyyy-MM-dd'T'HH:mm:ss.S", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mmZ", "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd", "yyyyMMdd"};

      for(String format : formats) {
         SimpleDateFormat dateFormat = new SimpleDateFormat(format);

         try {
            return dateFormat.parse(value);
         } catch (ParseException e) {
            this.invalidProperty = new InvalidPropertyException("Invalid time format " + value, e);
         }
      }

      return null;
   }

   public void validate() throws InvalidPropertyException {
      if (this.invalidProperty != null) {
         throw this.invalidProperty;
      } else {
         int i = this.hashCode();
         this.detail = JobBuilder.newJob(QuartzResourceAdapter.JobEndpoint.class).withIdentity("Job" + i, "DEFAULT").withDescription(this.description).requestRecovery(this.recoverable).storeDurably(this.durable).build();
         TriggerBuilder tb = TriggerBuilder.newTrigger().forJob(this.detail).withIdentity("Trigger" + i, "DEFAULT").withDescription(this.description);
         if (this.startTime != null) {
            tb.startAt(this.parse(this.startTime));
         }

         if (this.endTime != null) {
            tb.endAt(this.parse(this.endTime));
         }

         if (this.calendarName != null) {
            tb.modifiedByCalendar(this.calendarName);
         }

         CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule(this.getCronExpression());
         if (this.timeZone != null) {
            csb.inTimeZone(TimeZone.getTimeZone(this.timeZone));
         }

         this.trigger = tb.withSchedule(csb).build();

         try {
            ((CronTriggerImpl)this.trigger).validate();
         } catch (SchedulerException e) {
            throw new InvalidPropertyException(e);
         }
      }
   }

   public ResourceAdapter getResourceAdapter() {
      return this.resourceAdapter;
   }

   public void setResourceAdapter(ResourceAdapter resourceAdapter) {
      this.resourceAdapter = resourceAdapter;
   }

   MessageEndpoint getEndpoint() {
      return this.endpoint;
   }

   void setEndpoint(MessageEndpoint endpoint) {
      this.endpoint = endpoint;
   }

   Trigger getTrigger() {
      return this.trigger;
   }

   JobDetail getDetail() {
      return this.detail;
   }

   public JobKey jobKey() {
      return this.detail.getKey();
   }
}
