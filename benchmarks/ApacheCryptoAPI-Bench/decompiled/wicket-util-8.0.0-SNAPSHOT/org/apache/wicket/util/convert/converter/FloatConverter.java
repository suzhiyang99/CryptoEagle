package org.apache.wicket.util.convert.converter;

import java.math.BigDecimal;
import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;

public class FloatConverter extends AbstractDecimalConverter<Float> {
   private static final long serialVersionUID = 1L;
   private static final BigDecimal MIN_VALUE = new BigDecimal((double)-Float.MAX_VALUE);
   private static final BigDecimal MAX_VALUE = new BigDecimal((double)Float.MAX_VALUE);
   public static final IConverter<Float> INSTANCE = new FloatConverter();

   public Float convertToObject(String value, Locale locale) {
      BigDecimal number = this.parse(value, MIN_VALUE, MAX_VALUE, locale);
      return number == null ? null : number.floatValue();
   }

   protected Class<Float> getTargetType() {
      return Float.class;
   }
}
