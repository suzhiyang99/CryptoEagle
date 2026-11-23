package org.apache.wicket.util.convert.converter;

import java.math.BigDecimal;
import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;

public class ByteConverter extends AbstractIntegerConverter<Byte> {
   private static final long serialVersionUID = 1L;
   private static final BigDecimal MIN_VALUE = new BigDecimal(-128);
   private static final BigDecimal MAX_VALUE = new BigDecimal(127);
   public static final IConverter<Byte> INSTANCE = new ByteConverter();

   public Byte convertToObject(String value, Locale locale) {
      BigDecimal number = this.parse(value, MIN_VALUE, MAX_VALUE, locale);
      return number == null ? null : number.byteValue();
   }

   protected Class<Byte> getTargetType() {
      return Byte.class;
   }
}
