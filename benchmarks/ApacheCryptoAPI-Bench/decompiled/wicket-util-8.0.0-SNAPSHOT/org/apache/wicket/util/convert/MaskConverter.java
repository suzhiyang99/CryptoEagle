package org.apache.wicket.util.convert;

import java.text.ParseException;
import java.util.Locale;
import javax.swing.text.MaskFormatter;
import org.apache.wicket.util.lang.Args;

public class MaskConverter<C> implements IConverter<C> {
   private static final long serialVersionUID = 1L;
   private final MaskFormatter maskFormatter;

   public MaskConverter(MaskFormatter maskFormatter) {
      Args.notNull(maskFormatter, "maskFormatter");
      this.maskFormatter = maskFormatter;
   }

   public MaskConverter(String mask) {
      this(mask, String.class);
   }

   public MaskConverter(String mask, Class<?> type) {
      try {
         this.maskFormatter = new MaskFormatter(mask);
         this.maskFormatter.setValueClass(type);
         this.maskFormatter.setAllowsInvalid(true);
         this.maskFormatter.setValueContainsLiteralCharacters(true);
      } catch (ParseException e) {
         throw new RuntimeException(e);
      }
   }

   public C convertToObject(String value, Locale locale) {
      try {
         return (C)this.maskFormatter.stringToValue(value);
      } catch (ParseException e) {
         throw new ConversionException(e);
      }
   }

   public String convertToString(C value, Locale locale) {
      try {
         return this.maskFormatter.valueToString(value);
      } catch (ParseException e) {
         throw new ConversionException(e);
      }
   }
}
