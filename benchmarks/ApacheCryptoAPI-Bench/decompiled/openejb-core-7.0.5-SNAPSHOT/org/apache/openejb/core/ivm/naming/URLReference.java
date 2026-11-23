package org.apache.openejb.core.ivm.naming;

import java.net.MalformedURLException;
import java.net.URL;

public class URLReference extends Reference {
   private final String url;

   public URLReference(String url) {
      this.url = url;
   }

   public Object getObject() throws javax.naming.NamingException {
      try {
         return new URL(this.url);
      } catch (MalformedURLException var2) {
         throw new javax.naming.NamingException("Invalid URL: " + this.url);
      }
   }
}
