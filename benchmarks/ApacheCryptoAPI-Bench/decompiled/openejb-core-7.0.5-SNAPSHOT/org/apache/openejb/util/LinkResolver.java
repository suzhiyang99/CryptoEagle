package org.apache.openejb.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class LinkResolver<E> {
   private final Map<URI, E> byFullName = new TreeMap();
   private final Map<String, Collection<E>> byShortName = new TreeMap();

   public boolean add(String modulePackageName, String name, E value) {
      return this.add(URLs.uri(modulePackageName), name, value);
   }

   public boolean add(URI moduleURI, String name, E value) {
      URI uri = this.resolve(moduleURI, name);
      if (this.byFullName.containsKey(uri)) {
         return false;
      } else {
         this.byFullName.put(uri, value);
         Collection<E> values = (Collection)this.byShortName.get(name);
         if (values == null) {
            values = new ArrayList();
            this.byShortName.put(name, values);
         }

         values.add(value);
         return true;
      }
   }

   private URI resolve(URI moduleURI, String name) {
      name = name.replace(" ", "%20").replace("#", "%23");
      return moduleURI.resolve("#" + name);
   }

   public Collection<E> values() {
      return this.byFullName.values();
   }

   public Collection<E> values(String shortName) {
      Collection<E> es = (Collection)this.byShortName.get(shortName);
      return (Collection<E>)(es != null ? es : Collections.EMPTY_LIST);
   }

   public E resolveLink(String link, String modulePackageName) {
      URI moduleURI = URLs.uri(modulePackageName);
      return (E)this.resolveLink(link, moduleURI);
   }

   public E resolveLink(String link, URI moduleUri) {
      if (!link.contains("#")) {
         E value = null;
         if (moduleUri != null && !moduleUri.toString().isEmpty()) {
            value = (E)this.byFullName.get(this.resolve(moduleUri, link));
         }

         if (value != null) {
            return value;
         } else {
            Collection<E> values = (Collection)this.byShortName.get(link);
            if (values == null) {
               return null;
            } else {
               if (values.size() > 1) {
                  values = this.tryToResolveForEar(values, moduleUri, link);
               }

               if (values.size() != 1) {
                  return null;
               } else {
                  value = (E)values.iterator().next();
                  return value;
               }
            }
         }
      } else if (moduleUri != null) {
         URI uri = moduleUri.resolve(link);
         return (E)this.byFullName.get(uri);
      } else {
         return null;
      }
   }

   protected Collection<E> tryToResolveForEar(Collection<E> values, URI moduleUri, String link) {
      return values;
   }

   protected E getUniqueMember() {
      return (E)(this.byFullName.size() == 1 ? this.byFullName.values().iterator().next() : null);
   }
}
