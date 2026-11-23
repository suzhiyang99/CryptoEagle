package org.apache.wicket.util.convert.converter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

public class ZonedDateTimeConverter extends AbstractJavaTimeConverter<ZonedDateTime> {
   private static final long serialVersionUID = 1L;
   private static final DateTimeFormatter DATE_TIME_FORMATTER;

   protected Class<ZonedDateTime> getTargetType() {
      return ZonedDateTime.class;
   }

   protected ZonedDateTime createTemporal(TemporalAccessor temporalAccessor) {
      return ZonedDateTime.from(temporalAccessor);
   }

   protected DateTimeFormatter getDateTimeFormatter() {
      return DATE_TIME_FORMATTER;
   }

   static {
      DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.FULL);
   }
}
