package org.apache.wicket.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Time;

public class ResourceStreamWrapper implements IResourceStream {
   private static final long serialVersionUID = 1L;
   private final IResourceStream delegate;

   public ResourceStreamWrapper(IResourceStream delegate) {
      this.delegate = (IResourceStream)Args.notNull(delegate, "delegate");
   }

   public IResourceStream getDelegate() {
      return this.delegate;
   }

   public String getContentType() {
      return this.delegate.getContentType();
   }

   public Bytes length() {
      return this.delegate.length();
   }

   public InputStream getInputStream() throws ResourceStreamNotFoundException {
      return this.delegate.getInputStream();
   }

   public void close() throws IOException {
      this.delegate.close();
   }

   public Locale getLocale() {
      return this.delegate.getLocale();
   }

   public void setLocale(Locale locale) {
      this.delegate.setLocale(locale);
   }

   public String getStyle() {
      return this.delegate.getStyle();
   }

   public void setStyle(String style) {
      this.delegate.setStyle(style);
   }

   public String getVariation() {
      return this.delegate.getVariation();
   }

   public void setVariation(String variation) {
      this.delegate.setVariation(variation);
   }

   public Time lastModifiedTime() {
      return this.delegate.lastModifiedTime();
   }
}
