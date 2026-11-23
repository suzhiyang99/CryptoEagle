package org.apache.activemq.artemis.utils.uri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class URISchema<T, P> {
   private URIFactory<T, P> parentFactory;

   public abstract String getSchemaName();

   public T newObject(URI uri, P param) throws Exception {
      return (T)this.newObject(uri, (Map)null, param);
   }

   public void populateObject(URI uri, T bean) throws Exception {
      BeanSupport.setData(uri, bean, parseQuery(uri.getQuery(), (Map)null));
   }

   public URI newURI(T bean) throws Exception {
      return this.internalNewURI(bean);
   }

   void setFactory(URIFactory<T, P> factory) {
      this.parentFactory = factory;
   }

   protected URIFactory<T, P> getFactory() {
      return this.parentFactory;
   }

   protected String getHost(URI uri) {
      URI defaultFactory = this.getDefaultURI();
      if (defaultFactory != null && uri.getHost() == null && defaultFactory.getScheme().equals(uri.getScheme())) {
         uri = defaultFactory;
      }

      return uri.getHost();
   }

   protected URI getDefaultURI() {
      URIFactory<T, P> factory = this.getFactory();
      return factory == null ? null : factory.getDefaultURI();
   }

   protected int getPort(URI uri) {
      URI defaultFactory = this.getDefaultURI();
      if (defaultFactory != null && uri.getPort() < 0 && defaultFactory.getScheme().equals(uri.getScheme())) {
         uri = defaultFactory;
      }

      return uri.getPort();
   }

   public T newObject(URI uri, Map<String, String> propertyOverrides, P param) throws Exception {
      return (T)this.internalNewObject(uri, parseQuery(uri.getQuery(), propertyOverrides), param);
   }

   protected abstract T internalNewObject(URI var1, Map<String, String> var2, P var3) throws Exception;

   protected URI internalNewURI(T bean) throws Exception {
      String query = BeanSupport.getData((List)null, bean);
      return new URI(this.getSchemaName(), (String)null, "//", query, (String)null);
   }

   public static Map<String, String> parseQuery(String uri, Map<String, String> propertyOverrides) throws URISyntaxException {
      try {
         Map<String, String> rc = new HashMap();
         if (uri != null && !uri.isEmpty()) {
            String[] parameters = uri.split("&");

            for(String parameter : parameters) {
               int p = parameter.indexOf("=");
               if (p >= 0) {
                  String name = BeanSupport.decodeURI(parameter.substring(0, p));
                  String value = BeanSupport.decodeURI(parameter.substring(p + 1));
                  rc.put(name, value);
               } else if (!parameter.trim().isEmpty()) {
                  rc.put(parameter, (Object)null);
               }
            }
         }

         if (propertyOverrides != null) {
            for(Map.Entry<String, String> entry : propertyOverrides.entrySet()) {
               rc.put((String)entry.getKey(), (String)entry.getValue());
            }
         }

         return rc;
      } catch (UnsupportedEncodingException e) {
         throw (URISyntaxException)(new URISyntaxException(e.toString(), "Invalid encoding")).initCause(e);
      }
   }

   protected String printQuery(Map<String, String> query) {
      StringBuffer buffer = new StringBuffer();

      for(Map.Entry<String, String> entry : query.entrySet()) {
         buffer.append((String)entry.getKey() + "=" + (String)entry.getValue());
         buffer.append("\n");
      }

      return buffer.toString();
   }
}
