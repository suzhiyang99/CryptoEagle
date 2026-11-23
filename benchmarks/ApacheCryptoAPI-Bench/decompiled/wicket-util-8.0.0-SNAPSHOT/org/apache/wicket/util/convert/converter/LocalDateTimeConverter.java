package org.apache.wicket.util.convert.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

public class LocalDateTimeConverter extends AbstractJavaTimeConverter<LocalDateTime> {
   private static final long serialVersionUID = 1L;
   private static final DateTimeFormatter DATE_TIME_FORMATTER;

   protected Class<LocalDateTime> getTargetType() {
      return LocalDateTime.class;
   }

   protected LocalDateTime createTemporal(TemporalAccessor temporalAccessor) {
      return LocalDateTime.from(temporalAccessor);
   }

   protected DateTimeFormatter getDateTimeFormatter() {
      return DATE_TIME_FORMATTER;
   }

   static {
      DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
   }
}
