package org.apache.wicket.util.convert.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

public class BigIntegerConverter extends AbstractIntegerConverter<BigInteger> {
   private static final long serialVersionUID = 1L;

   protected Class<BigInteger> getTargetType() {
      return BigInteger.class;
   }

   public BigInteger convertToObject(String value, Locale locale) {
      BigDecimal number = this.parse(value, (BigDecimal)null, (BigDecimal)null, locale);
      return number == null ? null : number.toBigInteger();
   }
}
