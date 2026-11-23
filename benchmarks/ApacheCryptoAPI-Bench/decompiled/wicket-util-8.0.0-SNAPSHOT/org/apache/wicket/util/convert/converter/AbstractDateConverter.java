package org.apache.wicket.util.convert.converter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Locale.Category;
import org.apache.wicket.util.string.Strings;

public abstract class AbstractDateConverter<D extends Date> extends AbstractConverter<D> {
   private static final long serialVersionUID = 1L;

   protected abstract D createDateLike(long var1);

   public D convertToObject(String value, Locale locale) {
      if (Strings.isEmpty(value)) {
         return null;
      } else {
         DateFormat format = this.getDateFormat(locale);
         Date date = (Date)this.parse(format, value, locale);
         return (D)this.createDateLike(date.getTime());
      }
   }

   public String convertToString(D value, Locale locale) {
      if (value == null) {
         return null;
      } else {
         DateFormat dateFormat = this.getDateFormat(locale);
         return dateFormat != null ? dateFormat.format(value) : value.toString();
      }
   }

   public DateFormat getDateFormat(Locale locale) {
      if (locale == null) {
         locale = Locale.getDefault(Category.FORMAT);
      }

      return (DateFormat)DateFormat.getDateInstance(3, locale).clone();
   }
}
