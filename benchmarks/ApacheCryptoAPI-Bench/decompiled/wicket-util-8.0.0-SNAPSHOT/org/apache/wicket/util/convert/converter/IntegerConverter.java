package org.apache.wicket.util.convert.converter;

import java.math.BigDecimal;
import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;

public class IntegerConverter extends AbstractIntegerConverter<Integer> {
   private static final long serialVersionUID = 1L;
   private static final BigDecimal MIN_VALUE = new BigDecimal(Integer.MIN_VALUE);
   private static final BigDecimal MAX_VALUE = new BigDecimal(Integer.MAX_VALUE);
   public static final IConverter<Integer> INSTANCE = new IntegerConverter();

   public Integer convertToObject(String value, Locale locale) {
      BigDecimal number = this.parse(value, MIN_VALUE, MAX_VALUE, locale);
      return number == null ? null : number.intValue();
   }

   protected Class<Integer> getTargetType() {
      return Integer.class;
   }
}
