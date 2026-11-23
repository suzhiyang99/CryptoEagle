package org.apache.wicket.util.convert.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

public class LocalDateConverter extends AbstractJavaTimeConverter<LocalDate> {
   private static final long serialVersionUID = 1L;
   private static final DateTimeFormatter SHORT_DATE_TIME_FORMATTER;

   protected Class<LocalDate> getTargetType() {
      return LocalDate.class;
   }

   protected LocalDate createTemporal(TemporalAccessor temporalAccessor) {
      return LocalDate.from(temporalAccessor);
   }

   protected DateTimeFormatter getDateTimeFormatter() {
      return SHORT_DATE_TIME_FORMATTER;
   }

   static {
      SHORT_DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
   }
}
