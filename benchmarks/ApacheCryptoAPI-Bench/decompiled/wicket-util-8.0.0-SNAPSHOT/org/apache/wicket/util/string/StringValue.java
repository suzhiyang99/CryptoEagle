package org.apache.wicket.util.string;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Locale.Category;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringValue implements IClusterable {
   private static final long serialVersionUID = 1L;
   private static final Logger LOG = LoggerFactory.getLogger(StringValue.class);
   private final Locale locale;
   private final String text;

   public static StringValue repeat(int times, char c) {
      AppendingStringBuffer buffer = new AppendingStringBuffer(times);

      for(int i = 0; i < times; ++i) {
         buffer.append(c);
      }

      return valueOf(buffer);
   }

   public static StringValue repeat(int times, String s) {
      AppendingStringBuffer buffer = new AppendingStringBuffer(times);

      for(int i = 0; i < times; ++i) {
         buffer.append(s);
      }

      return valueOf(buffer);
   }

   public static StringValue valueOf(double value) {
      return valueOf(value, Locale.getDefault(Category.FORMAT));
   }

   public static StringValue valueOf(double value, int places, Locale locale) {
      if (!Double.isNaN(value) && !Double.isInfinite(value)) {
         DecimalFormat format = new DecimalFormat("#." + repeat(places, '#'), new DecimalFormatSymbols(locale));
         return valueOf(format.format(value));
      } else {
         return valueOf("N/A");
      }
   }

   public static StringValue valueOf(double value, Locale locale) {
      return valueOf(value, 1, locale);
   }

   public static StringValue valueOf(Object object) {
      return valueOf(Strings.toString(object));
   }

   public static StringValue valueOf(Object object, Locale locale) {
      return valueOf(Strings.toString(object), locale);
   }

   public static StringValue valueOf(String string) {
      return new StringValue(string);
   }

   public static StringValue valueOf(String string, Locale locale) {
      return new StringValue(string, locale);
   }

   public static StringValue valueOf(AppendingStringBuffer buffer) {
      return valueOf(buffer.toString());
   }

   protected StringValue(String text) {
      this(text, Locale.getDefault());
   }

   protected StringValue(String text, Locale locale) {
      this.text = text;
      this.locale = locale;
   }

   public final String afterFirst(char c) {
      return Strings.afterFirst(this.text, c);
   }

   public final String afterLast(char c) {
      return Strings.afterLast(this.text, c);
   }

   public final String beforeFirst(char c) {
      return Strings.beforeFirst(this.text, c);
   }

   public final String beforeLast(char c) {
      return Strings.afterLast(this.text, c);
   }

   public final CharSequence replaceAll(CharSequence searchFor, CharSequence replaceWith) {
      return Strings.replaceAll(this.text, searchFor, replaceWith);
   }

   public final <T> T to(Class<T> type) throws StringValueConversionException {
      if (type == null) {
         return null;
      } else if (type == String.class) {
         return (T)this.toString();
      } else if (type != Integer.TYPE && type != Integer.class) {
         if (type != Long.TYPE && type != Long.class) {
            if (type != Boolean.TYPE && type != Boolean.class) {
               if (type != Double.TYPE && type != Double.class) {
                  if (type != Character.TYPE && type != Character.class) {
                     if (type == Time.class) {
                        return (T)this.toTime();
                     } else if (type == Duration.class) {
                        return (T)this.toDuration();
                     } else if (type.isEnum()) {
                        return (T)this.toEnum(type);
                     } else {
                        throw new StringValueConversionException("Cannot convert '" + this.toString() + "'to type " + type);
                     }
                  } else {
                     return (T)this.toCharacter();
                  }
               } else {
                  return (T)this.toDoubleObject();
               }
            } else {
               return (T)this.toBooleanObject();
            }
         } else {
            return (T)this.toLongObject();
         }
      } else {
         return (T)this.toInteger();
      }
   }

   public final <T> T toOptional(Class<T> type) throws StringValueConversionException {
      return (T)(Strings.isEmpty(this.text) ? null : this.to(type));
   }

   public final boolean toBoolean() throws StringValueConversionException {
      return Strings.isTrue(this.text);
   }

   public final boolean toBoolean(boolean defaultValue) {
      if (this.text != null) {
         try {
            return this.toBoolean();
         } catch (StringValueConversionException x) {
            if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("An error occurred while converting '%s' to a boolean: %s", this.text, x.getMessage()), x);
            }
         }
      }

      return defaultValue;
   }

   public final Boolean toBooleanObject() throws StringValueConversionException {
      return Strings.toBoolean(this.text);
   }

   public final char toChar() throws StringValueConversionException {
      return Strings.toChar(this.text);
   }

   public final char toChar(char defaultValue) {
      if (this.text != null) {
         try {
            return this.toChar();
         } catch (StringValueConversionException x) {
            if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("An error occurred while converting '%s' to a character: %s", this.text, x.getMessage()), x);
            }
         }
      }

      return defaultValue;
   }

   public final Character toCharacter() throws StringValueConversionException {
      return this.toChar();
   }

   public final double toDouble() throws StringValueConversionException {
      try {
         return NumberFormat.getNumberInstance(this.locale).parse(this.text).doubleValue();
      } catch (ParseException e) {
         throw new StringValueConversionException("Unable to convert '" + this.text + "' to a double value", e);
      }
   }

   public final double toDouble(double defaultValue) {
      if (this.text != null) {
         try {
            return this.toDouble();
         } catch (Exception x) {
            if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("An error occurred while converting '%s' to a double: %s", this.text, x.getMessage()), x);
            }
         }
      }

      return defaultValue;
   }

   public final Double toDoubleObject() throws StringValueConversionException {
      return this.toDouble();
   }

   public final Duration toDuration() throws StringValueConversionException {
      return Duration.valueOf(this.text, this.locale);
   }

   public final Duration toDuration(Duration defaultValue) {
      if (this.text != null) {
         try {
            return this.toDuration();
         } catch (Exception x) {
            if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("An error occurred while converting '%s' to a Duration: %s", this.text, x.getMessage()), x);
            }
         }
      }

      return defaultValue;
   }

   public final int toInt() throws StringValueConversionException {
      try {
         return Integer.parseInt(this.text);
      } catch (NumberFormatException e) {
         throw new StringValueConversionException("Unable to convert '" + this.text + "' to an int value", e);
      }
   }

   public final int toInt(int defaultValue) {
      if (this.text != null) {
         try {
            return this.toInt();
         } catch (StringValueConversionException x) {
            if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("An error occurred while converting '%s' to an integer: %s", this.text, x.getMessage()), x);
            }
         }
      }

      return defaultValue;
   }

   public final Integer toInteger() throws StringValueConversionException {
      try {
         return new Integer(this.text);
      } catch (NumberFormatException e) {
         throw new StringValueConversionException("Unable to convert '" + this.text + "' to an Integer value", e);
      }
   }

   public final long toLong() throws StringValueConversionException {
      try {
         return Long.parseLong(this.text);
      } catch (NumberFormatException e) {
         throw new StringValueConversionException("Unable to convert '" + this.text + "' to a long value", e);
      }
   }

   public final long toLong(long defaultValue) {
      if (this.text != null) {
         try {
            return this.toLong();
         } catch (StringValueConversionException x) {
            if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("An error occurred while converting '%s' to a long: %s", this.text, x.getMessage()), x);
            }
         }
      }

      return defaultValue;
   }

   public final Long toLongObject() throws StringValueConversionException {
      try {
         return new Long(this.text);
      } catch (NumberFormatException e) {
         throw new StringValueConversionException("Unable to convert '" + this.text + "' to a Long value", e);
      }
   }

   public final Boolean toOptionalBoolean() throws StringValueConversionException {
      return Strings.isEmpty(this.text) ? null : this.toBooleanObject();
   }

   public final Character toOptionalCharacter() throws StringValueConversionException {
      return Strings.isEmpty(this.text) ? null : this.toCharacter();
   }

   public final Double toOptionalDouble() throws StringValueConversionException {
      return Strings.isEmpty(this.text) ? null : this.toDoubleObject();
   }

   public final Duration toOptionalDuration() throws StringValueConversionException {
      return Strings.isEmpty(this.text) ? null : this.toDuration();
   }

   public final Integer toOptionalInteger() throws StringValueConversionException {
      return Strings.isEmpty(this.text) ? null : this.toInteger();
   }

   public final Long toOptionalLong() throws StringValueConversionException {
      return Strings.isEmpty(this.text) ? null : this.toLongObject();
   }

   public final String toOptionalString() {
      return this.text;
   }

   public final Time toOptionalTime() throws StringValueConversionException {
      return Strings.isEmpty(this.text) ? null : this.toTime();
   }

   public final String toString() {
      return this.text;
   }

   public final String toString(String defaultValue) {
      return this.text == null ? defaultValue : this.text;
   }

   public final Time toTime() throws StringValueConversionException {
      try {
         return Time.valueOf(this.text);
      } catch (ParseException e) {
         throw new StringValueConversionException("Unable to convert '" + this.text + "' to a Time value", e);
      }
   }

   public final Time toTime(Time defaultValue) {
      if (this.text != null) {
         try {
            return this.toTime();
         } catch (StringValueConversionException x) {
            if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("An error occurred while converting '%s' to a Time: %s", this.text, x.getMessage()), x);
            }
         }
      }

      return defaultValue;
   }

   public final <T extends Enum<T>> T toEnum(Class<T> eClass) throws StringValueConversionException {
      return (T)Strings.toEnum(this.text, eClass);
   }

   public final <T extends Enum<T>> T toEnum(T defaultValue) {
      Args.notNull(defaultValue, "defaultValue");
      return (T)this.toEnum(defaultValue.getClass(), defaultValue);
   }

   public final <T extends Enum<T>> T toEnum(Class<T> eClass, T defaultValue) {
      if (this.text != null) {
         try {
            return (T)this.toEnum(eClass);
         } catch (StringValueConversionException x) {
            if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("An error occurred while converting '%s' to a %s: %s", this.text, eClass, x.getMessage()), x);
            }
         }
      }

      return defaultValue;
   }

   public final <T extends Enum<T>> T toOptionalEnum(Class<T> eClass) throws StringValueConversionException {
      return (T)(Strings.isEmpty(this.text) ? null : this.toEnum(eClass));
   }

   public boolean isNull() {
      return this.text == null;
   }

   public boolean isEmpty() {
      return Strings.isEmpty(this.text);
   }

   public int hashCode() {
      return Objects.hashCode(this.locale, this.text);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof StringValue)) {
         return false;
      } else {
         StringValue stringValue = (StringValue)obj;
         return Objects.isEqual(this.text, stringValue.text) && this.locale.equals(stringValue.locale);
      }
   }
}
