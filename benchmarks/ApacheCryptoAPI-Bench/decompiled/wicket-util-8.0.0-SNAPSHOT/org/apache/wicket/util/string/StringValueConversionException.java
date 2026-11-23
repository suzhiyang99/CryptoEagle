package org.apache.wicket.util.string;

import org.apache.wicket.util.convert.ConversionException;

public final class StringValueConversionException extends ConversionException {
   private static final long serialVersionUID = 1L;

   public StringValueConversionException(String message) {
      super(message);
   }

   public StringValueConversionException(String message, Throwable cause) {
      super(message, cause);
   }
}
