package org.apache.openejb.rest;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class ThreadLocalHttpHeaders extends AbstractRestThreadLocalProxy<HttpHeaders> implements HttpHeaders {
   protected ThreadLocalHttpHeaders() {
      super(HttpHeaders.class);
   }

   public List<MediaType> getAcceptableMediaTypes() {
      return ((HttpHeaders)this.get()).getAcceptableMediaTypes();
   }

   public Map<String, Cookie> getCookies() {
      return ((HttpHeaders)this.get()).getCookies();
   }

   public Date getDate() {
      return ((HttpHeaders)this.get()).getDate();
   }

   public int getLength() {
      return ((HttpHeaders)this.get()).getLength();
   }

   public Locale getLanguage() {
      return ((HttpHeaders)this.get()).getLanguage();
   }

   public MediaType getMediaType() {
      return ((HttpHeaders)this.get()).getMediaType();
   }

   public MultivaluedMap<String, String> getRequestHeaders() {
      return new MultivaluedMapWithCaseInsensitiveKeySet(((HttpHeaders)this.get()).getRequestHeaders());
   }

   public List<Locale> getAcceptableLanguages() {
      return ((HttpHeaders)this.get()).getAcceptableLanguages();
   }

   public List<String> getRequestHeader(String name) {
      return ((HttpHeaders)this.get()).getRequestHeader(name);
   }

   public String getHeaderString(String name) {
      return ((HttpHeaders)this.get()).getHeaderString(name);
   }
}
