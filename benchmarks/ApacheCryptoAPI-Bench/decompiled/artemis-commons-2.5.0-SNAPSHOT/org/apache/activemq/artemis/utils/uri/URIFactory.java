package org.apache.activemq.artemis.utils.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class URIFactory<T, P> {
   private URI defaultURI;
   private final Map<String, URISchema<T, P>> schemas = new ConcurrentHashMap();

   public URI getDefaultURI() {
      return this.defaultURI;
   }

   public void setDefaultURI(URI uri) {
      this.defaultURI = uri;
   }

   public void registerSchema(URISchema<T, P> schemaFactory) {
      this.schemas.put(schemaFactory.getSchemaName(), schemaFactory);
      schemaFactory.setFactory(this);
   }

   public void removeSchema(String schemaName) {
      this.schemas.remove(schemaName);
   }

   public URI expandURI(String uriString) throws Exception {
      return this.normalise(uriString);
   }

   public T newObject(URI uri, P param) throws Exception {
      URISchema<T, P> schemaFactory = (URISchema)this.schemas.get(uri.getScheme());
      if (schemaFactory == null) {
         throw new NullPointerException("Schema " + uri.getScheme() + " not found");
      } else {
         return schemaFactory.newObject(uri, param);
      }
   }

   public T newObject(URI uri, Map<String, String> overrides, P param) throws Exception {
      URISchema<T, P> schemaFactory = (URISchema)this.schemas.get(uri.getScheme());
      if (schemaFactory == null) {
         throw new NullPointerException("Schema " + uri.getScheme() + " not found");
      } else {
         return schemaFactory.newObject(uri, overrides, param);
      }
   }

   public T newObject(String uri, P param) throws Exception {
      return (T)this.newObject(new URI(uri), param);
   }

   public void populateObject(URI uri, T bean) throws Exception {
      URISchema<T, P> schemaFactory = (URISchema)this.schemas.get(uri.getScheme());
      if (schemaFactory == null) {
         throw new NullPointerException("Schema " + uri.getScheme() + " not found");
      } else {
         schemaFactory.populateObject(uri, bean);
      }
   }

   public void populateObject(String uri, T bean) throws Exception {
      this.populateObject(new URI(uri), bean);
   }

   public URI createSchema(String scheme, T bean) throws Exception {
      URISchema<T, P> schemaFactory = (URISchema)this.schemas.get(scheme);
      if (schemaFactory == null) {
         throw new NullPointerException("Schema " + scheme + " not found");
      } else {
         return schemaFactory.newURI(bean);
      }
   }

   private URI normalise(String uri) throws URISyntaxException {
      if (uri.startsWith("(")) {
         String[] split = uri.split("\\)");
         String[] connectorURIS = split[0].substring(split[0].indexOf(40) + 1).split(",");
         String factoryQuery = split.length > 1 ? split[1] : "";
         StringBuilder builder = new StringBuilder(connectorURIS[0]);
         if (factoryQuery != null && factoryQuery.length() > 0) {
            if (connectorURIS[0].contains("?")) {
               builder.append("&").append(factoryQuery.substring(1));
            } else {
               builder.append(factoryQuery);
            }
         }

         if (connectorURIS.length > 1) {
            builder.append("#");

            for(int i = 1; i < connectorURIS.length; ++i) {
               if (i > 1) {
                  builder.append(",");
               }

               builder.append(connectorURIS[i]);
            }
         }

         return new URI(builder.toString().replace(";", "&"));
      } else {
         return new URI(uri.replace(";", "&"));
      }
   }
}
