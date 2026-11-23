package org.apache.wicket.util.convert.converter;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class AbstractIntegerConverter<I extends Number> extends AbstractNumberConverter<I> {
   private static final long serialVersionUID = 1L;

   protected NumberFormat newNumberFormat(Locale locale) {
      NumberFormat numberFormat = NumberFormat.getIntegerInstance(locale);
      numberFormat.setParseIntegerOnly(true);
      numberFormat.setGroupingUsed(false);
      return numberFormat;
   }
}
