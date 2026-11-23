package org.apache.wicket.util.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

abstract class AbstractTime extends AbstractTimeValue {
   private static final long serialVersionUID = 1L;
   static final Calendar localtime = Calendar.getInstance();
   static final SimpleDateFormat timeFormat;

   AbstractTime(long milliseconds) {
      super(milliseconds);
   }

   public final boolean after(AbstractTimeValue that) {
      return this.greaterThan(that);
   }

   public final boolean before(AbstractTimeValue that) {
      return this.lessThan(that);
   }

   public final String toTimeString() {
      return this.toTimeString(localtime);
   }

   public final String toTimeString(Calendar calendar) {
      synchronized(timeFormat) {
         String var10000;
         synchronized(calendar) {
            timeFormat.setCalendar(calendar);
            var10000 = timeFormat.format(new Date(this.getMilliseconds())).toLowerCase();
         }

         return var10000;
      }
   }

   public String toString() {
      return this.toTimeString();
   }

   static {
      timeFormat = new SimpleDateFormat("h.mma", Locale.ENGLISH);
   }
}
