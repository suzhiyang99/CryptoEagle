package org.apache.wicket.util.resource;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.watch.IModifiable;

public interface IResourceStream extends IModifiable, IClusterable, Closeable {
   String getContentType();

   Bytes length();

   InputStream getInputStream() throws ResourceStreamNotFoundException;

   void close() throws IOException;

   Locale getLocale();

   void setLocale(Locale var1);

   String getStyle();

   void setStyle(String var1);

   String getVariation();

   void setVariation(String var1);
}
