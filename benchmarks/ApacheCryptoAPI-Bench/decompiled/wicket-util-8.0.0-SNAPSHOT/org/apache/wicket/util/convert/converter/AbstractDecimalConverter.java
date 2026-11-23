package org.apache.wicket.util.convert.converter;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class AbstractDecimalConverter<N extends Number> extends AbstractNumberConverter<N> {
   private static final long serialVersionUID = 1L;

   protected NumberFormat newNumberFormat(Locale locale) {
      return NumberFormat.getInstance(locale);
   }
}
