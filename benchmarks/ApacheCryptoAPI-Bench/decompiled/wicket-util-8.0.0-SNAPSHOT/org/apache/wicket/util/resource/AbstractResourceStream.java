package org.apache.wicket.util.resource;

import java.util.Locale;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Time;

public abstract class AbstractResourceStream implements IResourceStream {
   private static final long serialVersionUID = 1L;
   private Locale locale;
   private String style;
   private String variation;

   public Locale getLocale() {
      return this.locale;
   }

   public void setLocale(Locale locale) {
      this.locale = locale;
   }

   public String getStyle() {
      return this.style;
   }

   public String getVariation() {
      return this.variation;
   }

   public void setStyle(String style) {
      this.style = style;
   }

   public void setVariation(String variation) {
      this.variation = variation;
   }

   public Bytes length() {
      return null;
   }

   public String getContentType() {
      return null;
   }

   public Time lastModifiedTime() {
      return null;
   }
}
