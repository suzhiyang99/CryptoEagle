package org.apache.wicket.util.convert;

import java.util.Locale;
import org.apache.wicket.util.io.IClusterable;

public interface IConverter<C> extends IClusterable {
   C convertToObject(String var1, Locale var2) throws ConversionException;

   String convertToString(C var1, Locale var2);
}
