package org.apache.wicket.util.convert.converter;

import java.math.BigDecimal;
import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;

public class ShortConverter extends AbstractIntegerConverter<Short> {
   private static final long serialVersionUID = 1L;
   private static final BigDecimal MIN_VALUE = new BigDecimal(-32768);
   private static final BigDecimal MAX_VALUE = new BigDecimal(32767);
   public static final IConverter<Short> INSTANCE = new ShortConverter();

   public Short convertToObject(String value, Locale locale) {
      BigDecimal number = this.parse(value, MIN_VALUE, MAX_VALUE, locale);
      return number == null ? null : number.shortValue();
   }

   protected Class<Short> getTargetType() {
      return Short.class;
   }
}
