package org.apache.wicket.util.convert.converter;

import java.math.BigDecimal;
import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;

public class DoubleConverter extends AbstractDecimalConverter<Double> {
   private static final long serialVersionUID = 1L;
   private static final BigDecimal MIN_VALUE = new BigDecimal(-Double.MAX_VALUE);
   private static final BigDecimal MAX_VALUE = new BigDecimal(Double.MAX_VALUE);
   public static final IConverter<Double> INSTANCE = new DoubleConverter();

   public Double convertToObject(String value, Locale locale) {
      BigDecimal number = this.parse(value, MIN_VALUE, MAX_VALUE, locale);
      return number == null ? null : number.doubleValue();
   }

   protected Class<Double> getTargetType() {
      return Double.class;
   }
}
