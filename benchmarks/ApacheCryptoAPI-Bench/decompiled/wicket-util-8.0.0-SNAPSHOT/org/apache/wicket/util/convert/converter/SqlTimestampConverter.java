package org.apache.wicket.util.convert.converter;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Locale.Category;

public class SqlTimestampConverter extends AbstractDateConverter<Timestamp> {
   private static final long serialVersionUID = 1L;
   private final int dateFormat;
   private final int timeFormat;

   public SqlTimestampConverter() {
      this(3, 3);
   }

   public SqlTimestampConverter(int dateFormat) {
      this(dateFormat, 3);
   }

   public SqlTimestampConverter(int dateFormat, int timeFormat) {
      this.dateFormat = dateFormat;
      this.timeFormat = timeFormat;
   }

   public DateFormat getDateFormat(Locale locale) {
      if (locale == null) {
         locale = Locale.getDefault(Category.FORMAT);
      }

      return (DateFormat)DateFormat.getDateTimeInstance(this.dateFormat, this.timeFormat, locale).clone();
   }

   protected Timestamp createDateLike(long date) {
      return new Timestamp(date);
   }

   protected Class<Timestamp> getTargetType() {
      return Timestamp.class;
   }
}
