package org.apache.activemq.artemis.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public final class TimeUtils {
   private TimeUtils() {
   }

   public static String printDuration(double uptime) {
      NumberFormat fmtI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ENGLISH));
      NumberFormat fmtD = new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.ENGLISH));
      uptime /= (double)1000.0F;
      if (uptime < (double)60.0F) {
         return fmtD.format(uptime) + " seconds";
      } else {
         uptime /= (double)60.0F;
         if (uptime < (double)60.0F) {
            long minutes = (long)uptime;
            String s = fmtI.format(minutes) + (minutes > 1L ? " minutes" : " minute");
            return s;
         } else {
            uptime /= (double)60.0F;
            if (uptime < (double)24.0F) {
               long hours = (long)uptime;
               long minutes = (long)((uptime - (double)hours) * (double)60.0F);
               String s = fmtI.format(hours) + (hours > 1L ? " hours" : " hour");
               if (minutes != 0L) {
                  s = s + " " + fmtI.format(minutes) + (minutes > 1L ? " minutes" : " minute");
               }

               return s;
            } else {
               uptime /= (double)24.0F;
               long days = (long)uptime;
               long hours = (long)((uptime - (double)days) * (double)24.0F);
               String s = fmtI.format(days) + (days > 1L ? " days" : " day");
               if (hours != 0L) {
                  s = s + " " + fmtI.format(hours) + (hours > 1L ? " hours" : " hour");
               }

               return s;
            }
         }
      }
   }

   public static boolean waitOnBoolean(boolean expected, long timeout, CheckMethod check) {
      long timeLeft = timeout;

      for(long interval = 10L; check.check() != expected && timeLeft > 0L; timeLeft -= interval) {
         try {
            Thread.sleep(interval);
         } catch (InterruptedException var9) {
         }
      }

      return check.check() == expected;
   }

   public interface CheckMethod {
      boolean check();
   }
}
