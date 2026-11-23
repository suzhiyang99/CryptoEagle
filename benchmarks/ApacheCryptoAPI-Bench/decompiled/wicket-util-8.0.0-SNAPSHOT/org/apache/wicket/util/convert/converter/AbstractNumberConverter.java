package org.apache.wicket.util.convert.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractNumberConverter<N extends Number> extends AbstractConverter<N> {
   private static final long serialVersionUID = 1L;
   private final ConcurrentHashMap<Locale, NumberFormat> numberFormats = new ConcurrentHashMap();

   public NumberFormat getNumberFormat(Locale locale) {
      NumberFormat numberFormat = (NumberFormat)this.numberFormats.get(locale);
      if (numberFormat == null) {
         numberFormat = this.newNumberFormat(locale);
         if (numberFormat instanceof DecimalFormat) {
            ((DecimalFormat)numberFormat).setParseBigDecimal(true);
         }

         NumberFormat tmpNumberFormat = (NumberFormat)this.numberFormats.putIfAbsent(locale, numberFormat);
         if (tmpNumberFormat != null) {
            numberFormat = tmpNumberFormat;
         }
      }

      return (NumberFormat)numberFormat.clone();
   }

   protected abstract NumberFormat newNumberFormat(Locale var1);

   protected BigDecimal parse(Object value, BigDecimal min, BigDecimal max, Locale locale) {
      if (locale == null) {
         locale = Locale.getDefault(Category.FORMAT);
      }

      if (value == null) {
         return null;
      } else {
         if (value instanceof String) {
            value = ((String)value).replaceAll("(\\d+)\\s(?=\\d)", "$1 ");
         }

         NumberFormat numberFormat = this.getNumberFormat(locale);
         N number = (N)(this.parse(numberFormat, value, locale));
         if (number == null) {
            return null;
         } else {
            BigDecimal bigDecimal;
            if (number instanceof BigDecimal) {
               bigDecimal = (BigDecimal)number;
            } else {
               bigDecimal = new BigDecimal(number.toString());
            }

            if (min != null && bigDecimal.compareTo(min) < 0) {
               throw this.newConversionException("Value cannot be less than " + min, value, locale).setFormat(numberFormat);
            } else if (max != null && bigDecimal.compareTo(max) > 0) {
               throw this.newConversionException("Value cannot be greater than " + max, value, locale).setFormat(numberFormat);
            } else {
               return bigDecimal;
            }
         }
      }
   }

   public String convertToString(N value, Locale locale) {
      NumberFormat fmt = this.getNumberFormat(locale);
      return fmt != null ? fmt.format(value) : value.toString();
   }
}
