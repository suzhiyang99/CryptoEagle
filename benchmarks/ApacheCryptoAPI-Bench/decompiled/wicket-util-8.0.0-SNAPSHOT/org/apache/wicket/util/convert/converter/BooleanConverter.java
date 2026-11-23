package org.apache.wicket.util.convert.converter;

import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.util.string.Strings;

public class BooleanConverter extends AbstractConverter<Boolean> {
   private static final long serialVersionUID = 1L;
   public static final IConverter<Boolean> INSTANCE = new BooleanConverter();

   public Boolean convertToObject(String value, Locale locale) {
      try {
         return Strings.toBoolean(value);
      } catch (StringValueConversionException var4) {
         throw this.newConversionException("Cannot convert '" + value + "' to Boolean", value, locale);
      }
   }

   protected Class<Boolean> getTargetType() {
      return Boolean.class;
   }
}
