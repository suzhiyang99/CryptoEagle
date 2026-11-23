package org.apache.wicket.util.convert.converter;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.lang.Args;

public class CalendarConverter implements IConverter<Calendar> {
   private static final long serialVersionUID = 1L;
   private final IConverter<Date> dateConverter;

   public CalendarConverter() {
      this(new DateConverter());
   }

   public CalendarConverter(IConverter<Date> dateConverter) {
      Args.notNull(dateConverter, "dateConverter");
      this.dateConverter = dateConverter;
   }

   public Calendar convertToObject(String value, Locale locale) {
      Date date = this.dateConverter.convertToObject(value, locale);
      if (date == null) {
         return null;
      } else {
         Calendar calendar = locale != null ? Calendar.getInstance(locale) : Calendar.getInstance();
         calendar.setTime(date);
         return calendar;
      }
   }

   public String convertToString(Calendar value, Locale locale) {
      return value == null ? null : this.dateConverter.convertToString(value.getTime(), locale);
   }
}
