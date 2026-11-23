package org.apache.wicket.util.time;

import java.text.ParseException;
import java.util.Calendar;
import org.apache.wicket.util.lang.EnumeratedType;

public final class TimeOfDay extends AbstractTime {
   private static final long serialVersionUID = 1L;
   public static final Meridian AM = new Meridian("AM");
   public static final TimeOfDay MIDNIGHT;
   public static final Meridian PM;
   public static final TimeOfDay NOON;

   public static TimeOfDay militaryTime(int hour, int minute, int second) {
      if (hour <= 23 && hour >= 0) {
         if (minute <= 59 && minute >= 0) {
            if (second <= 59 && second >= 0) {
               return valueOf(Duration.hours(hour).add(Duration.minutes(minute)).add(Duration.seconds(second)));
            } else {
               throw new IllegalArgumentException("Second " + second + " is not valid");
            }
         } else {
            throw new IllegalArgumentException("Minute " + minute + " is not valid");
         }
      } else {
         throw new IllegalArgumentException("Hour " + hour + " is not valid");
      }
   }

   public static TimeOfDay now() {
      return valueOf(Time.now());
   }

   public static TimeOfDay now(Calendar calendar) {
      return valueOf(calendar, Time.now());
   }

   public static TimeOfDay time(int hour, int minute, int second, Meridian meridian) {
      if (meridian == PM) {
         return hour == 12 ? militaryTime(12, minute, second) : militaryTime(hour + 12, minute, second);
      } else {
         return hour == 12 ? militaryTime(0, minute, second) : militaryTime(hour, minute, second);
      }
   }

   public static TimeOfDay time(int hour, int minute, Meridian meridian) {
      return time(hour, minute, 0, meridian);
   }

   public static TimeOfDay valueOf(Calendar calendar, String time) throws ParseException {
      synchronized(timeFormat) {
         TimeOfDay var10000;
         synchronized(calendar) {
            timeFormat.setCalendar(calendar);
            var10000 = new TimeOfDay(timeFormat.parse(time).getTime());
         }

         return var10000;
      }
   }

   public static TimeOfDay valueOf(Calendar calendar, Time time) {
      return militaryTime(time.getHour(calendar), time.getMinute(calendar), time.getSecond(calendar));
   }

   public static TimeOfDay valueOf(Duration duration) {
      return new TimeOfDay(duration.getMilliseconds());
   }

   public static TimeOfDay valueOf(long time) {
      return new TimeOfDay(time);
   }

   public static TimeOfDay valueOf(String time) throws ParseException {
      return valueOf(localtime, time);
   }

   public static TimeOfDay valueOf(Time time) {
      return valueOf(AbstractTime.localtime, time);
   }

   private TimeOfDay(long time) {
      super(time);
      if (Duration.valueOf(time).greaterThan(Duration.ONE_DAY)) {
         throw new IllegalArgumentException("Time " + this + " is not a time of day value");
      }
   }

   public int hour() {
      return this.toHours(this.getMilliseconds());
   }

   public int minute() {
      return this.toMinutes(this.getMilliseconds()) % 60;
   }

   public Time next() {
      return this.next(AbstractTime.localtime);
   }

   public Time next(Calendar calendar) {
      Time timeToday = Time.valueOf(calendar, this);
      return timeToday.before(Time.now()) ? Time.valueOf(calendar, this).add(Duration.ONE_DAY) : timeToday;
   }

   public int second() {
      return this.toSeconds(this.getMilliseconds()) % 60;
   }

   public String toString() {
      int second = this.second();
      return "" + this.hour() + ":" + this.minute() + (second != 0 ? ":" + second : "");
   }

   private int toHours(long milliseconds) {
      return this.toMinutes(milliseconds) / 60;
   }

   private int toMinutes(long milliseconds) {
      return this.toSeconds(milliseconds) / 60;
   }

   private int toSeconds(long milliseconds) {
      return (int)(milliseconds / 1000L);
   }

   static {
      MIDNIGHT = time(12, 0, AM);
      PM = new Meridian("PM");
      NOON = time(12, 0, PM);
   }

   public static final class Meridian extends EnumeratedType {
      private static final long serialVersionUID = 1L;

      Meridian(String name) {
         super(name);
      }
   }
}
