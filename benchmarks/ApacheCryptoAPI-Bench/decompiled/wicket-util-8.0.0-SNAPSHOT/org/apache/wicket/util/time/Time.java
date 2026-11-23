package org.apache.wicket.util.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public final class Time extends AbstractTime {
   private static final long serialVersionUID = 1L;
   public static final Time START_OF_UNIX_TIME = millis(0L);
   private static final SimpleDateFormat dateFormat;
   private static final SimpleDateFormat dateTimeFormat;
   private static final String[] DAYS;
   private static final String[] MONTHS;
   public static final TimeZone GMT;

   public static Time now() {
      return millis(System.currentTimeMillis());
   }

   public static Time millis(long time) {
      return new Time(time);
   }

   public static Time parseDate(Calendar calendar, String string) throws ParseException {
      synchronized(dateFormat) {
         Time var10000;
         synchronized(calendar) {
            dateFormat.setCalendar(calendar);
            var10000 = valueOf(dateFormat.parse(string));
         }

         return var10000;
      }
   }

   public static Time parseDate(String string) throws ParseException {
      return parseDate(localtime, string);
   }

   public static Time valueOf(Calendar calendar, String string) throws ParseException {
      synchronized(dateTimeFormat) {
         Time var10000;
         synchronized(calendar) {
            dateTimeFormat.setCalendar(calendar);
            var10000 = valueOf(dateTimeFormat.parse(string));
         }

         return var10000;
      }
   }

   public static Time valueOf(Calendar calendar, TimeOfDay timeOfDay) {
      synchronized(calendar) {
         calendar.setTimeInMillis(System.currentTimeMillis());
         calendar.set(11, 0);
         calendar.set(12, 0);
         calendar.set(13, 0);
         calendar.set(14, 0);
         return millis(calendar.getTimeInMillis() + timeOfDay.getMilliseconds());
      }
   }

   public static Time valueOf(Date date) {
      return new Time(date.getTime());
   }

   public static Time valueOf(String string) throws ParseException {
      return valueOf(localtime, string);
   }

   public static Time valueOf(String string, String pattern) throws ParseException {
      SimpleDateFormat dateTimeFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
      dateTimeFormat.setCalendar(localtime);
      return valueOf(dateTimeFormat.parse(string));
   }

   public static Time valueOf(TimeOfDay timeOfDay) {
      return valueOf(localtime, timeOfDay);
   }

   private Time(long time) {
      super(time);
   }

   public Time add(Duration duration) {
      return millis(this.getMilliseconds() + duration.getMilliseconds());
   }

   public Duration elapsedSince() {
      Time now = now();
      if (this.greaterThan(now)) {
         throw new IllegalStateException("This time is in the future");
      } else {
         return now.subtract(this);
      }
   }

   public Duration fromNow() {
      return this.subtract(now());
   }

   public int get(Calendar calendar, int field) {
      synchronized(calendar) {
         calendar.setTimeInMillis(this.getMilliseconds());
         return calendar.get(field);
      }
   }

   public int get(int field) {
      return this.get(localtime, field);
   }

   public int getDayOfMonth() {
      return this.getDayOfMonth(localtime);
   }

   public int getDayOfMonth(Calendar calendar) {
      return this.get(calendar, 5);
   }

   public int getHour() {
      return this.getHour(localtime);
   }

   public int getHour(Calendar calendar) {
      return this.get(calendar, 11);
   }

   public int getMinute() {
      return this.getMinute(localtime);
   }

   public int getMinute(Calendar calendar) {
      return this.get(calendar, 12);
   }

   public int getMonth() {
      return this.getMonth(localtime);
   }

   public int getMonth(Calendar calendar) {
      return this.get(calendar, 2);
   }

   public int getSecond() {
      return this.getSecond(localtime);
   }

   public int getSecond(Calendar calendar) {
      return this.get(calendar, 13);
   }

   public int getYear() {
      return this.getYear(localtime);
   }

   public int getYear(Calendar calendar) {
      return this.get(calendar, 1);
   }

   public Time subtract(Duration duration) {
      return millis(this.getMilliseconds() - duration.getMilliseconds());
   }

   public Duration subtract(Time that) {
      return Duration.milliseconds(this.getMilliseconds() - that.getMilliseconds());
   }

   public Date toDate() {
      return new Date(this.getMilliseconds());
   }

   public String toDateString() {
      return this.toDateString(localtime);
   }

   public String toDateString(Calendar calendar) {
      synchronized(dateFormat) {
         String var10000;
         synchronized(calendar) {
            dateFormat.setCalendar(calendar);
            var10000 = dateFormat.format(new Date(this.getMilliseconds())).toLowerCase();
         }

         return var10000;
      }
   }

   public String toString() {
      return this.toDateString() + "-" + this.toTimeString();
   }

   public String toString(Calendar calendar, String format) {
      SimpleDateFormat dateTimeFormat = new SimpleDateFormat(format, Locale.ENGLISH);
      dateTimeFormat.setCalendar(calendar == null ? localtime : calendar);
      return dateTimeFormat.format(new Date(this.getMilliseconds()));
   }

   public String toString(String format) {
      return this.toString((Calendar)null, format);
   }

   public String toRfc1123TimestampString() {
      Calendar cal = GregorianCalendar.getInstance(GMT);
      StringBuilder buf = new StringBuilder(32);
      cal.setTimeInMillis(this.getMilliseconds());
      int day_of_week = cal.get(7);
      int day_of_month = cal.get(5);
      int month = cal.get(2);
      int year = cal.get(1);
      int century = year / 100;
      year %= 100;
      int hours = cal.get(11);
      int minutes = cal.get(12);
      int seconds = cal.get(13);
      buf.append(DAYS[day_of_week]);
      buf.append(',');
      buf.append(' ');
      appendTwoDigits(buf, day_of_month);
      buf.append(' ');
      buf.append(MONTHS[month]);
      buf.append(' ');
      appendTwoDigits(buf, century);
      appendTwoDigits(buf, year);
      buf.append(' ');
      appendTwoDigits(buf, hours);
      buf.append(':');
      appendTwoDigits(buf, minutes);
      buf.append(':');
      appendTwoDigits(buf, seconds);
      buf.append(" GMT");
      return buf.toString();
   }

   private static void appendTwoDigits(StringBuilder str, int number) {
      str.append((char)(number / 10 + 48));
      str.append((char)(number % 10 + 48));
   }

   static {
      dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
      dateTimeFormat = new SimpleDateFormat("yyyy.MM.dd-h.mma", Locale.ENGLISH);
      DAYS = new String[]{"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
      MONTHS = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan"};
      GMT = TimeZone.getTimeZone("GMT");
   }
}
