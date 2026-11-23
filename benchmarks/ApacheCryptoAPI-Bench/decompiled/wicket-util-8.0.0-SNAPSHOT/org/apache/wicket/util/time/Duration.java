package org.apache.wicket.util.time;

import java.util.Locale;
import java.util.Locale.Category;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.util.thread.ICode;
import org.slf4j.Logger;

public class Duration extends AbstractTimeValue {
   private static final long serialVersionUID = 1L;
   public static final Duration MAXIMUM = milliseconds(Long.MAX_VALUE);
   public static final Duration NONE = milliseconds(0L);
   public static final Duration ONE_DAY = days(1);
   public static final Duration ONE_HOUR = hours(1);
   public static final Duration ONE_MINUTE = minutes(1);
   public static final Duration ONE_SECOND = seconds(1);
   public static final Duration ONE_WEEK = days(7);
   private static final Pattern pattern = Pattern.compile("([0-9]+([.,][0-9]+)?)\\s+(millisecond|second|minute|hour|day)s?", 2);

   public static Duration benchmark(ICode code, Logger log) {
      Time start = Time.now();
      code.run(log);
      return Time.now().subtract(start);
   }

   public static Duration benchmark(Runnable code) {
      Time start = Time.now();
      code.run();
      return Time.now().subtract(start);
   }

   public static Duration days(double days) {
      return hours((double)24.0F * days);
   }

   public static Duration days(int days) {
      return hours(24 * days);
   }

   public static Duration elapsed(Time start) {
      return start.elapsedSince();
   }

   public static Duration hours(double hours) {
      return minutes((double)60.0F * hours);
   }

   public static Duration hours(int hours) {
      return minutes(60 * hours);
   }

   public static Duration milliseconds(double milliseconds) {
      return milliseconds(Math.round(milliseconds));
   }

   public static Duration milliseconds(long milliseconds) {
      return new Duration(milliseconds);
   }

   public static Duration minutes(double minutes) {
      return seconds((double)60.0F * minutes);
   }

   public static Duration minutes(int minutes) {
      return seconds(60 * minutes);
   }

   public static Duration seconds(double seconds) {
      return milliseconds(seconds * (double)1000.0F);
   }

   public static Duration seconds(int seconds) {
      return milliseconds((long)seconds * 1000L);
   }

   public static Duration valueOf(long time) {
      return new Duration(time);
   }

   public static Duration valueOf(String string) throws StringValueConversionException {
      return valueOf(string, Locale.getDefault(Category.FORMAT));
   }

   public static Duration valueOf(String string, Locale locale) throws StringValueConversionException {
      Matcher matcher = pattern.matcher(string);
      if (matcher.matches()) {
         double value = StringValue.valueOf(matcher.group(1), locale).toDouble();
         String units = matcher.group(3);
         if (units.equalsIgnoreCase("millisecond")) {
            return milliseconds(value);
         } else if (units.equalsIgnoreCase("second")) {
            return seconds(value);
         } else if (units.equalsIgnoreCase("minute")) {
            return minutes(value);
         } else if (units.equalsIgnoreCase("hour")) {
            return hours(value);
         } else if (units.equalsIgnoreCase("day")) {
            return days(value);
         } else {
            throw new StringValueConversionException("Unrecognized units: " + string);
         }
      } else {
         throw new StringValueConversionException("Unable to parse duration: " + string);
      }
   }

   protected Duration(long milliseconds) {
      super(milliseconds);
   }

   public Duration add(Duration duration) {
      return valueOf(this.getMilliseconds() + duration.getMilliseconds());
   }

   public final double days() {
      return this.hours() / (double)24.0F;
   }

   public final double hours() {
      return this.minutes() / (double)60.0F;
   }

   public final double minutes() {
      return this.seconds() / (double)60.0F;
   }

   public final double seconds() {
      return (double)this.getMilliseconds() / (double)1000.0F;
   }

   public final void sleep() {
      if (this.getMilliseconds() > 0L) {
         try {
            Thread.sleep(this.getMilliseconds());
         } catch (InterruptedException var2) {
         }
      }

   }

   public Duration subtract(Duration that) {
      return valueOf(this.getMilliseconds() - that.getMilliseconds());
   }

   public void wait(Object object) {
      try {
         object.wait(this.getMilliseconds());
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
   }

   public String toString() {
      return this.toString(Locale.getDefault(Category.FORMAT));
   }

   public String toString(Locale locale) {
      if (this.getMilliseconds() >= 0L) {
         if (this.days() >= (double)1.0F) {
            return this.unitString(this.days(), "day", locale);
         } else if (this.hours() >= (double)1.0F) {
            return this.unitString(this.hours(), "hour", locale);
         } else if (this.minutes() >= (double)1.0F) {
            return this.unitString(this.minutes(), "minute", locale);
         } else {
            return this.seconds() >= (double)1.0F ? this.unitString(this.seconds(), "second", locale) : this.unitString((double)this.getMilliseconds(), "millisecond", locale);
         }
      } else {
         return "N/A";
      }
   }

   private String unitString(double value, String units, Locale locale) {
      return StringValue.valueOf(value, locale) + " " + units + (value > (double)1.0F ? "s" : "");
   }
}
