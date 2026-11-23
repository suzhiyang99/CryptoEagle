package org.spark_project.jetty.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class AttributesMap implements Attributes {
   private final AtomicReference<ConcurrentMap<String, Object>> _map = new AtomicReference();

   public AttributesMap() {
   }

   public AttributesMap(AttributesMap attributes) {
      ConcurrentMap<String, Object> map = attributes.map();
      if (map != null) {
         this._map.set(new ConcurrentHashMap(map));
      }

   }

   private ConcurrentMap<String, Object> map() {
      return (ConcurrentMap)this._map.get();
   }

   private ConcurrentMap<String, Object> ensureMap() {
      ConcurrentMap<String, Object> map;
      do {
         map = this.map();
         if (map != null) {
            return map;
         }

         map = new ConcurrentHashMap();
      } while(!this._map.compareAndSet((Object)null, map));

      return map;
   }

   public void removeAttribute(String name) {
      Map<String, Object> map = this.map();
      if (map != null) {
         map.remove(name);
      }

   }

   public void setAttribute(String name, Object attribute) {
      if (attribute == null) {
         this.removeAttribute(name);
      } else {
         this.ensureMap().put(name, attribute);
      }

   }

   public Object getAttribute(String name) {
      Map<String, Object> map = this.map();
      return map == null ? null : map.get(name);
   }

   public Enumeration<String> getAttributeNames() {
      return Collections.enumeration(this.getAttributeNameSet());
   }

   public Set<String> getAttributeNameSet() {
      return this.keySet();
   }

   public Set<Map.Entry<String, Object>> getAttributeEntrySet() {
      Map<String, Object> map = this.map();
      return map == null ? Collections.emptySet() : map.entrySet();
   }

   public static Enumeration<String> getAttributeNamesCopy(Attributes attrs) {
      if (attrs instanceof AttributesMap) {
         return Collections.enumeration(((AttributesMap)attrs).keySet());
      } else {
         List<String> names = new ArrayList();
         names.addAll(Collections.list(attrs.getAttributeNames()));
         return Collections.enumeration(names);
      }
   }

   public void clearAttributes() {
      Map<String, Object> map = this.map();
      if (map != null) {
         map.clear();
      }

   }

   public int size() {
      Map<String, Object> map = this.map();
      return map == null ? 0 : map.size();
   }

   public String toString() {
      Map<String, Object> map = this.map();
      return map == null ? "{}" : map.toString();
   }

   private Set<String> keySet() {
      Map<String, Object> map = this.map();
      return map == null ? Collections.emptySet() : map.keySet();
   }

   public void addAll(Attributes attributes) {
      Enumeration<String> e = attributes.getAttributeNames();

      while(e.hasMoreElements()) {
         String name = (String)e.nextElement();
         this.setAttribute(name, attributes.getAttribute(name));
      }

   }
}
