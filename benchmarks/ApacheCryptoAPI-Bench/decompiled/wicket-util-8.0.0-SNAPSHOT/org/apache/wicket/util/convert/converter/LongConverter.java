package org.apache.wicket.util.convert.converter;

import java.math.BigDecimal;
import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;

public class LongConverter extends AbstractIntegerConverter<Long> {
   private static final long serialVersionUID = 1L;
   private static final BigDecimal MIN_VALUE = new BigDecimal(Long.MIN_VALUE);
   private static final BigDecimal MAX_VALUE = new BigDecimal(Long.MAX_VALUE);
   public static final IConverter<Long> INSTANCE = new LongConverter();

   public Long convertToObject(String value, Locale locale) {
      BigDecimal number = this.parse(value, MIN_VALUE, MAX_VALUE, locale);
      return number == null ? null : number.longValue();
   }

   protected Class<Long> getTargetType() {
      return Long.class;
   }
}
