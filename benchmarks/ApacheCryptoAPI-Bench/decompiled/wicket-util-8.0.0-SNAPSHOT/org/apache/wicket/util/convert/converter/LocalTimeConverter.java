package org.apache.wicket.util.convert.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class LocalTimeConverter extends AbstractJavaTimeConverter<LocalTime> {
   private static final long serialVersionUID = 1L;

   protected Class<LocalTime> getTargetType() {
      return LocalTime.class;
   }

   protected LocalTime createTemporal(TemporalAccessor temporalAccessor) {
      return LocalTime.from(temporalAccessor);
   }

   protected DateTimeFormatter getDateTimeFormatter() {
      return DateTimeFormatter.ISO_LOCAL_TIME;
   }
}
