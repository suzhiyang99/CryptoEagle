package org.apache.wicket.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Time;

public abstract class AbstractResourceStreamWriter implements IResourceStreamWriter {
   private static final long serialVersionUID = 1L;
   private Locale locale;
   private String variation;
   private String style;

   public Bytes length() {
      return null;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public void setLocale(Locale locale) {
      this.locale = locale;
   }

   public Time lastModifiedTime() {
      return Time.now();
   }

   public final InputStream getInputStream() {
      throw new IllegalStateException("getInputStream() is not used with IResourceStreamWriter");
   }

   public void close() throws IOException {
   }

   public String getContentType() {
      return null;
   }

   public String getStyle() {
      return this.style;
   }

   public void setStyle(String style) {
      this.style = style;
   }

   public String getVariation() {
      return this.variation;
   }

   public void setVariation(String variation) {
      this.variation = variation;
   }
}
