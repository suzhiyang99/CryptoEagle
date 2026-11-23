package org.apache.wicket.util.convert.converter;

import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;

public class CharacterConverter extends AbstractConverter<Character> {
   private static final long serialVersionUID = 1L;
   public static final IConverter<Character> INSTANCE = new CharacterConverter();

   public Character convertToObject(String value, Locale locale) {
      int length = value.length();
      if (length == 0) {
         return null;
      } else if (length == 1) {
         return value.charAt(0);
      } else {
         throw this.newConversionException("Cannot convert '" + value + "' to Character", value, locale);
      }
   }

   protected Class<Character> getTargetType() {
      return Character.class;
   }
}
