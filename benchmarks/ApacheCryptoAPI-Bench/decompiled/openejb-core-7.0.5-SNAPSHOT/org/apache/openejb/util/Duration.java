package org.apache.openejb.util;

import java.beans.PropertyEditorManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Duration {
   private long time;
   private TimeUnit unit;

   public Duration() {
   }

   public Duration(long time, TimeUnit unit) {
      this.time = time;
      this.unit = unit;
   }

   public Duration(String string) {
      String[] strings = string.split(",| and ");
      Duration total = new Duration();

      for(String s : strings) {
         Duration part = new Duration();
         s = s.trim();
         StringBuilder t = new StringBuilder();
         StringBuilder u = new StringBuilder();

         int i;
         for(i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (!Character.isDigit(c) && (i != 0 || c != '-')) {
               break;
            }

            t.append(c);
         }

         if (t.length() == 0) {
            invalidFormat(s);
         }

         while(i < s.length()) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
               break;
            }

            ++i;
         }

         for(; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
               u.append(c);
            } else {
               invalidFormat(s);
            }
         }

         part.time = (long)Integer.parseInt(t.toString());
         part.unit = parseUnit(u.toString());
         total = total.add(part);
      }

      this.time = total.time;
      this.unit = total.unit;
   }

   public long getTime() {
      return this.time;
   }

   public long getTime(TimeUnit unit) {
      return unit.convert(this.time, this.unit);
   }

   public void setTime(long time) {
      this.time = time;
   }

   public TimeUnit getUnit() {
      return this.unit;
   }

   public void setUnit(TimeUnit unit) {
      this.unit = unit;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Duration that = (Duration)o;
         Normalize n = new Normalize(this, that);
         return n.a == n.b;
      } else {
         return false;
      }
   }

   public Duration add(Duration that) {
      Normalize n = new Normalize(this, that);
      return new Duration(n.a + n.b, n.base);
   }

   public Duration subtract(Duration that) {
      Normalize n = new Normalize(this, that);
      return new Duration(n.a - n.b, n.base);
   }

   public static Duration parse(String text) {
      return new Duration(text);
   }

   private static void invalidFormat(String text) {
      throw new IllegalArgumentException("Illegal duration format: '" + text + "'.  Valid examples are '10s' or '10 seconds'.");
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.time);
      if (this.unit != null) {
         sb.append(" ");
         sb.append(this.unit);
      }

      return sb.toString();
   }

   private static TimeUnit parseUnit(String u) {
      if (u.length() == 0) {
         return null;
      } else if (u.equalsIgnoreCase("NANOSECONDS")) {
         return TimeUnit.NANOSECONDS;
      } else if (u.equalsIgnoreCase("NANOSECOND")) {
         return TimeUnit.NANOSECONDS;
      } else if (u.equalsIgnoreCase("NANOS")) {
         return TimeUnit.NANOSECONDS;
      } else if (u.equalsIgnoreCase("NANO")) {
         return TimeUnit.NANOSECONDS;
      } else if (u.equalsIgnoreCase("NS")) {
         return TimeUnit.NANOSECONDS;
      } else if (u.equalsIgnoreCase("MICROSECONDS")) {
         return TimeUnit.MICROSECONDS;
      } else if (u.equalsIgnoreCase("MICROSECOND")) {
         return TimeUnit.MICROSECONDS;
      } else if (u.equalsIgnoreCase("MICROS")) {
         return TimeUnit.MICROSECONDS;
      } else if (u.equalsIgnoreCase("MICRO")) {
         return TimeUnit.MICROSECONDS;
      } else if (u.equalsIgnoreCase("MILLISECONDS")) {
         return TimeUnit.MILLISECONDS;
      } else if (u.equalsIgnoreCase("MILLISECOND")) {
         return TimeUnit.MILLISECONDS;
      } else if (u.equalsIgnoreCase("MILLIS")) {
         return TimeUnit.MILLISECONDS;
      } else if (u.equalsIgnoreCase("MILLI")) {
         return TimeUnit.MILLISECONDS;
      } else if (u.equalsIgnoreCase("MS")) {
         return TimeUnit.MILLISECONDS;
      } else if (u.equalsIgnoreCase("SECONDS")) {
         return TimeUnit.SECONDS;
      } else if (u.equalsIgnoreCase("SECOND")) {
         return TimeUnit.SECONDS;
      } else if (u.equalsIgnoreCase("SEC")) {
         return TimeUnit.SECONDS;
      } else if (u.equalsIgnoreCase("S")) {
         return TimeUnit.SECONDS;
      } else if (u.equalsIgnoreCase("MINUTES")) {
         return TimeUnit.MINUTES;
      } else if (u.equalsIgnoreCase("MINUTE")) {
         return TimeUnit.MINUTES;
      } else if (u.equalsIgnoreCase("MIN")) {
         return TimeUnit.MINUTES;
      } else if (u.equalsIgnoreCase("M")) {
         return TimeUnit.MINUTES;
      } else if (u.equalsIgnoreCase("HOURS")) {
         return TimeUnit.HOURS;
      } else if (u.equalsIgnoreCase("HOUR")) {
         return TimeUnit.HOURS;
      } else if (u.equalsIgnoreCase("HRS")) {
         return TimeUnit.HOURS;
      } else if (u.equalsIgnoreCase("HR")) {
         return TimeUnit.HOURS;
      } else if (u.equalsIgnoreCase("H")) {
         return TimeUnit.HOURS;
      } else if (u.equalsIgnoreCase("DAYS")) {
         return TimeUnit.DAYS;
      } else if (u.equalsIgnoreCase("DAY")) {
         return TimeUnit.DAYS;
      } else if (u.equalsIgnoreCase("D")) {
         return TimeUnit.DAYS;
      } else {
         throw new IllegalArgumentException("Unknown time unit '" + u + "'.  Supported units " + Join.join(", ", (Collection)lowercase(TimeUnit.values())));
      }
   }

   private static List<String> lowercase(Enum... units) {
      List<String> list = new ArrayList();

      for(Enum unit : units) {
         list.add(unit.name().toLowerCase());
      }

      return list;
   }

   static {
      PropertyEditorManager.registerEditor(Duration.class, DurationEditor.class);
   }

   private static final class Normalize {
      private final long a;
      private final long b;
      private final TimeUnit base;

      private Normalize(Duration a, Duration b) {
         this.base = lowest(a, b);
         this.a = a.unit == null ? a.time : this.base.convert(a.time, a.unit);
         this.b = b.unit == null ? b.time : this.base.convert(b.time, b.unit);
      }

      private static TimeUnit lowest(Duration a, Duration b) {
         if (a.unit == null) {
            return b.unit;
         } else if (b.unit == null) {
            return a.unit;
         } else if (a.time == 0L) {
            return b.unit;
         } else {
            return b.time == 0L ? a.unit : TimeUnit.values()[Math.min(a.unit.ordinal(), b.unit.ordinal())];
         }
      }
   }
}
