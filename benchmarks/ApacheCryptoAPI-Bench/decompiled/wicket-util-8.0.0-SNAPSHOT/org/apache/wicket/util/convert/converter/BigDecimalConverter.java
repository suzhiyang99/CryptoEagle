package org.apache.wicket.util.convert.converter;

import java.math.BigDecimal;
import java.util.Locale;

public class BigDecimalConverter extends AbstractDecimalConverter<BigDecimal> {
   private static final long serialVersionUID = 1L;

   protected Class<BigDecimal> getTargetType() {
      return BigDecimal.class;
   }

   public BigDecimal convertToObject(String value, Locale locale) {
      return this.parse(value, (BigDecimal)null, (BigDecimal)null, locale);
   }
}
