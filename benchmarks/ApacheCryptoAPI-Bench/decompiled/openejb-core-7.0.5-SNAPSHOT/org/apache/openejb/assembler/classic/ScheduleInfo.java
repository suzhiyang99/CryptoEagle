package org.apache.openejb.assembler.classic;

import java.util.Date;

public class ScheduleInfo extends InfoObject {
   public String second;
   public String minute;
   public String hour;
   public String dayOfWeek;
   public String dayOfMonth;
   public String month;
   public String year;
   public String info;
   public boolean persistent;
   public Date start;
   public Date end;
   public String timezone;
}
