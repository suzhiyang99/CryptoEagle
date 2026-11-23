package org.apache.wicket.util.collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class MostRecentlyUsedMap<K, V> extends LinkedHashMap<K, V> {
   private static final long serialVersionUID = 1L;
   protected V removedValue;
   private final int maxEntries;

   public MostRecentlyUsedMap(int maxEntries) {
      super(10, 0.75F, true);
      if (maxEntries <= 0) {
         throw new IllegalArgumentException("Must have at least one entry");
      } else {
         this.maxEntries = maxEntries;
      }
   }

   public V getRemovedValue() {
      return this.removedValue;
   }

   protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      boolean remove = this.size() > this.maxEntries;
      if (remove) {
         this.removedValue = (V)eldest.getValue();
      } else {
         this.removedValue = null;
      }

      return remove;
   }
}
