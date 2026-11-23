package org.apache.wicket.util.convert.converter;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Locale.Category;

public class SqlTimeConverter extends AbstractDateConverter<Time> {
   private static final long serialVersionUID = 1L;

   protected Time createDateLike(long date) {
      return new Time(date);
   }

   public DateFormat getDateFormat(Locale locale) {
      if (locale == null) {
         locale = Locale.getDefault(Category.FORMAT);
      }

      return (DateFormat)DateFormat.getTimeInstance(3, locale).clone();
   }

   protected Class<Time> getTargetType() {
      return Time.class;
   }
}
