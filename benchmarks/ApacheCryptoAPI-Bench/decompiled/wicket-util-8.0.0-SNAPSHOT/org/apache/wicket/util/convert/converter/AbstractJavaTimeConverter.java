package org.apache.wicket.util.convert.converter;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.apache.wicket.util.string.Strings;

public abstract class AbstractJavaTimeConverter<T extends Temporal> extends AbstractConverter<T> {
   private static final long serialVersionUID = 1L;

   protected abstract T createTemporal(TemporalAccessor var1);

   public T convertToObject(String value, Locale locale) {
      if (Strings.isEmpty(value)) {
         return null;
      } else {
         DateTimeFormatter dateTimeFormatter = this.getDateTimeFormatter(locale);

         TemporalAccessor temporalAccessor;
         try {
            temporalAccessor = dateTimeFormatter.parse(value);
         } catch (DateTimeParseException var6) {
            throw this.newConversionException("Cannot parse '" + value, value, locale);
         }

         return (T)this.createTemporal(temporalAccessor);
      }
   }

   public String convertToString(T value, Locale locale) {
      if (value == null) {
         return null;
      } else {
         DateTimeFormatter dateTimeFormatter = this.getDateTimeFormatter(locale);
         return dateTimeFormatter != null ? dateTimeFormatter.format(value) : value.toString();
      }
   }

   public DateTimeFormatter getDateTimeFormatter(Locale locale) {
      if (locale == null) {
         locale = Locale.getDefault();
      }

      return this.getDateTimeFormatter().withLocale(locale);
   }

   protected abstract DateTimeFormatter getDateTimeFormatter();
}
