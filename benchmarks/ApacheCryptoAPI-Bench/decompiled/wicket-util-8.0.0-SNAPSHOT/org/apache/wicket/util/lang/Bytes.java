package org.apache.wicket.util.lang;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.util.value.LongValue;

public final class Bytes extends LongValue {
   private static final long serialVersionUID = 1L;
   private static final Pattern valuePattern = Pattern.compile("([0-9]+([\\.,][0-9]+)?)\\s*(|K|M|G|T)B?", 2);
   public static final Bytes MAX = bytes(Long.MAX_VALUE);

   private Bytes(long bytes) {
      super(bytes);
      if (bytes < 0L) {
         throw new IllegalArgumentException("'bytes' cannot be negative: " + bytes);
      }
   }

   public static Bytes bytes(long bytes) {
      return new Bytes(bytes);
   }

   public static Bytes kilobytes(long kilobytes) {
      return bytes(kilobytes * 1024L);
   }

   public static Bytes megabytes(long megabytes) {
      return kilobytes(megabytes * 1024L);
   }

   public static Bytes gigabytes(long gigabytes) {
      return megabytes(gigabytes * 1024L);
   }

   public static Bytes terabytes(long terabytes) {
      return gigabytes(terabytes * 1024L);
   }

   public static Bytes bytes(double bytes) {
      return bytes(Math.round(bytes));
   }

   public static Bytes kilobytes(double kilobytes) {
      return bytes(kilobytes * (double)1024.0F);
   }

   public static Bytes megabytes(double megabytes) {
      return kilobytes(megabytes * (double)1024.0F);
   }

   public static Bytes gigabytes(double gigabytes) {
      return megabytes(gigabytes * (double)1024.0F);
   }

   public static Bytes terabytes(double terabytes) {
      return gigabytes(terabytes * (double)1024.0F);
   }

   public final long bytes() {
      return this.value;
   }

   public final double kilobytes() {
      return (double)this.value / (double)1024.0F;
   }

   public final double megabytes() {
      return this.kilobytes() / (double)1024.0F;
   }

   public final double gigabytes() {
      return this.megabytes() / (double)1024.0F;
   }

   public final double terabytes() {
      return this.gigabytes() / (double)1024.0F;
   }

   public static Bytes valueOf(String string, Locale locale) throws StringValueConversionException {
      Matcher matcher = valuePattern.matcher(string);
      if (matcher.matches()) {
         try {
            double value = NumberFormat.getNumberInstance(locale).parse(matcher.group(1)).doubleValue();
            String units = matcher.group(3);
            if (units.equalsIgnoreCase("")) {
               return bytes(value);
            } else if (units.equalsIgnoreCase("K")) {
               return kilobytes(value);
            } else if (units.equalsIgnoreCase("M")) {
               return megabytes(value);
            } else if (units.equalsIgnoreCase("G")) {
               return gigabytes(value);
            } else if (units.equalsIgnoreCase("T")) {
               return terabytes(value);
            } else {
               throw new StringValueConversionException("Units not recognized: " + string);
            }
         } catch (ParseException e) {
            throw new StringValueConversionException("Unable to parse numeric part: " + string, e);
         }
      } else {
         throw new StringValueConversionException("Unable to parse bytes: " + string);
      }
   }

   public static Bytes valueOf(String string) throws StringValueConversionException {
      return valueOf(string, Locale.getDefault(Category.FORMAT));
   }

   public String toString() {
      return this.toString(Locale.getDefault(Category.FORMAT));
   }

   public String toString(Locale locale) {
      if (this.terabytes() >= (double)1.0F) {
         return this.unitString(this.terabytes(), "T", locale);
      } else if (this.gigabytes() >= (double)1.0F) {
         return this.unitString(this.gigabytes(), "G", locale);
      } else if (this.megabytes() >= (double)1.0F) {
         return this.unitString(this.megabytes(), "M", locale);
      } else {
         return this.kilobytes() >= (double)1.0F ? this.unitString(this.kilobytes(), "K", locale) : Long.toString(this.value) + " bytes";
      }
   }

   private String unitString(double value, String units, Locale locale) {
      return StringValue.valueOf(value, locale) + units;
   }

   public boolean greaterThan(Bytes other) {
      if (this != other && other != null) {
         return this.bytes() > other.bytes();
      } else {
         return false;
      }
   }
}
