package org.apache.openejb.core.timer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.ScheduleExpression;
import org.apache.openejb.quartz.JobDetail;
import org.apache.openejb.quartz.ScheduleBuilder;
import org.apache.openejb.quartz.TriggerKey;
import org.apache.openejb.quartz.impl.jdbcjobstore.CronTriggerPersistenceDelegate;
import org.apache.openejb.quartz.impl.jdbcjobstore.TriggerPersistenceDelegate;
import org.apache.openejb.quartz.impl.jdbcjobstore.Util;
import org.apache.openejb.quartz.spi.MutableTrigger;
import org.apache.openejb.quartz.spi.OperableTrigger;

public class EJBCronTriggerPersistenceDelegate extends CronTriggerPersistenceDelegate {
   public String getHandledTriggerTypeDiscriminator() {
      return "EJB_CRON";
   }

   public boolean canHandleTriggerType(OperableTrigger trigger) {
      return trigger instanceof EJBCronTrigger;
   }

   public TriggerPersistenceDelegate.TriggerPropertyBundle loadExtendedTriggerProperties(Connection conn, TriggerKey triggerKey) throws SQLException {
      PreparedStatement ps = null;
      ResultSet rs = null;

      TriggerPersistenceDelegate.TriggerPropertyBundle var9;
      try {
         ps = conn.prepareStatement(Util.rtp("SELECT * FROM {0}CRON_TRIGGERS WHERE SCHED_NAME = {1} AND TRIGGER_NAME = ? AND TRIGGER_GROUP = ?", this.tablePrefix, this.schedNameLiteral));
         ps.setString(1, triggerKey.getName());
         ps.setString(2, triggerKey.getGroup());
         rs = ps.executeQuery();
         if (!rs.next()) {
            throw new IllegalStateException("No record found for selection of Trigger with key: '" + triggerKey + "' and statement: " + Util.rtp("SELECT * FROM {0}CRON_TRIGGERS WHERE SCHED_NAME = {1} AND TRIGGER_NAME = ? AND TRIGGER_GROUP = ?", this.tablePrefix, this.schedNameLiteral));
         }

         String cronExpr = rs.getString("CRON_EXPRESSION");
         String timeZoneId = rs.getString("TIME_ZONE_ID");
         String[] parts = cronExpr.split(";");

         try {
            EJBCronTrigger cb = new EJBCronTrigger((new ScheduleExpression()).year(parts[0]).month(parts[1]).dayOfMonth(parts[2]).dayOfWeek(parts[3]).hour(parts[4]).minute(parts[5]).second(parts[6]).timezone(timeZoneId));
            var9 = new TriggerPersistenceDelegate.TriggerPropertyBundle(new EJBCronTriggerSceduleBuilder(cb), (String[])null, (Object[])null);
         } catch (EJBCronTrigger.ParseException var13) {
            throw new IllegalStateException("Can't build the Trigger with key: '" + triggerKey + "' and statement: " + Util.rtp("SELECT * FROM {0}CRON_TRIGGERS WHERE SCHED_NAME = {1} AND TRIGGER_NAME = ? AND TRIGGER_GROUP = ?", this.tablePrefix, this.schedNameLiteral));
         }
      } finally {
         Util.closeResultSet(rs);
         Util.closeStatement(ps);
      }

      return var9;
   }

   public int insertExtendedTriggerProperties(Connection conn, OperableTrigger trigger, String state, JobDetail jobDetail) throws SQLException, IOException {
      EJBCronTrigger cronTrigger = (EJBCronTrigger)trigger;
      PreparedStatement ps = null;

      int var7;
      try {
         ps = conn.prepareStatement(Util.rtp("INSERT INTO {0}CRON_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, CRON_EXPRESSION, TIME_ZONE_ID)  VALUES({1}, ?, ?, ?, ?)", this.tablePrefix, this.schedNameLiteral));
         ps.setString(1, trigger.getKey().getName());
         ps.setString(2, trigger.getKey().getGroup());
         ps.setString(3, cronTrigger.getRawValue());
         ps.setString(4, cronTrigger.getTimeZone().getID());
         var7 = ps.executeUpdate();
      } finally {
         Util.closeStatement(ps);
      }

      return var7;
   }

   public int updateExtendedTriggerProperties(Connection conn, OperableTrigger trigger, String state, JobDetail jobDetail) throws SQLException, IOException {
      EJBCronTrigger cronTrigger = (EJBCronTrigger)trigger;
      PreparedStatement ps = null;

      int var7;
      try {
         ps = conn.prepareStatement(Util.rtp("UPDATE {0}CRON_TRIGGERS SET CRON_EXPRESSION = ?, TIME_ZONE_ID = ? WHERE SCHED_NAME = {1} AND TRIGGER_NAME = ? AND TRIGGER_GROUP = ?", this.tablePrefix, this.schedNameLiteral));
         ps.setString(1, cronTrigger.getRawValue());
         ps.setString(2, cronTrigger.getTimeZone().getID());
         ps.setString(3, trigger.getKey().getName());
         ps.setString(4, trigger.getKey().getGroup());
         var7 = ps.executeUpdate();
      } finally {
         Util.closeStatement(ps);
      }

      return var7;
   }

   private static class EJBCronTriggerSceduleBuilder extends ScheduleBuilder<EJBCronTrigger> {
      private final EJBCronTrigger trigger;

      public EJBCronTriggerSceduleBuilder(EJBCronTrigger trig) {
         this.trigger = trig;
      }

      protected MutableTrigger build() {
         return this.trigger;
      }
   }
}
