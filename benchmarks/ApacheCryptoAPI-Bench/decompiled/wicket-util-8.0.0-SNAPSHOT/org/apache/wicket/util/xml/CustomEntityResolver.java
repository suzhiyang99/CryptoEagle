package org.apache.wicket.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import org.apache.wicket.util.lang.Args;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CustomEntityResolver implements EntityResolver {
   private final Map<EntityKey, EntityLocator> entities = new HashMap(3);

   public static CustomEntityResolver getPreloaded() {
      CustomEntityResolver resolver = new CustomEntityResolver();
      resolver.put(new EntityKey("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", "http://java.sun.com/dtd/web-app_2_3.dtd"), new ServletApiEntityLocator("web-app_2_3.dtd"));
      return resolver;
   }

   public void put(EntityKey key, EntityLocator locator) {
      Args.notNull(key, "key");
      Args.notNull(locator, "locator");
      this.entities.put(key, locator);
   }

   public InputSource resolveEntity(String id, String url) throws SAXException, IOException {
      for(Map.Entry<EntityKey, EntityLocator> entry : this.entities.entrySet()) {
         if (((EntityKey)entry.getKey()).id.equals(id) || ((EntityKey)entry.getKey()).url.equals(url)) {
            return ((EntityLocator)entry.getValue()).locateInputSource();
         }
      }

      return null;
   }

   public static class EntityKey {
      private final String id;
      private final String url;

      private EntityKey(String id, String url) {
         Args.notEmpty(id, "id");
         Args.notEmpty(url, "url");
         this.id = id;
         this.url = url;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof EntityKey)) {
            return false;
         } else {
            EntityKey key = (EntityKey)o;
            return !this.id.equals(key.id) ? false : this.url.equals(key.url);
         }
      }

      public int hashCode() {
         int result = this.id.hashCode();
         result = 31 * result + this.url.hashCode();
         return result;
      }
   }

   public static class ServletApiEntityLocator implements EntityLocator {
      private final String name;

      private ServletApiEntityLocator(String name) {
         this.name = name;
      }

      public InputSource locateInputSource() {
         InputStream stream = Filter.class.getResourceAsStream("resources/" + this.name);
         return stream == null ? null : new InputSource(stream);
      }
   }

   public interface EntityLocator {
      InputSource locateInputSource() throws SAXException, IOException;
   }
}
