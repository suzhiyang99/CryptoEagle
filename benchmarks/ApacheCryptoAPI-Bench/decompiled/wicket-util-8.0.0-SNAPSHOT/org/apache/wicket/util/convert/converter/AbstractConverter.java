package org.apache.wicket.util.convert.converter;

import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

public abstract class AbstractConverter<C> implements IConverter<C> {
   private static final long serialVersionUID = 1L;

   protected C parse(Format format, Object value, Locale locale) {
      ParsePosition position = new ParsePosition(0);
      String stringValue = value.toString();
      C result = (C)format.parseObject(stringValue, position);
      if (position.getIndex() != stringValue.length()) {
         throw this.newConversionException("Cannot parse '" + value + "' using format " + format, value, locale).setFormat(format);
      } else {
         return result;
      }
   }

   protected ConversionException newConversionException(String message, Object value, Locale locale) {
      return (new ConversionException(message)).setSourceValue(value).setTargetType(this.getTargetType()).setConverter(this).setLocale(locale);
   }

   protected abstract Class<C> getTargetType();

   public String convertToString(C value, Locale locale) {
      return value == null ? null : value.toString();
   }
}
