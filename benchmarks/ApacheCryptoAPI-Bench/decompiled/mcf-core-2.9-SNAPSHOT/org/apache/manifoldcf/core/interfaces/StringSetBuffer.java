package org.apache.manifoldcf.core.interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StringSetBuffer {
   public static final String _rcsid = "@(#)$Id: StringSetBuffer.java 988245 2010-08-23 18:39:35Z kwright $";
   protected HashMap hashtable = new HashMap();

   public void clear() {
      this.hashtable.clear();
   }

   public boolean contains(String x) {
      return this.hashtable.get(x) != null;
   }

   public boolean contains(StringSet x) {
      Iterator iter = x.getKeys();

      while(iter.hasNext()) {
         String key = (String)iter.next();
         if (this.contains(key)) {
            return true;
         }
      }

      return false;
   }

   public void add(String s) {
      this.add((String)s, (String)null);
   }

   public void add(String s, String prefix) {
      if (prefix != null) {
         s = prefix + s;
      }

      this.hashtable.put(s, s);
   }

   public void add(StringSet ss) {
      this.add((StringSet)ss, (String)null);
   }

   public void add(StringSet ss, String prefix) {
      Iterator iterator = ss.getKeys();

      while(iterator.hasNext()) {
         String key = (String)iterator.next();
         this.add(key, prefix);
      }

   }

   public void add(String[] stringArray) {
      this.add((String[])stringArray, (String)null);
   }

   public void add(String[] stringArray, String prefix) {
      int i = 0;

      while(i < stringArray.length) {
         this.add(stringArray[i++], prefix);
      }

   }

   public void add(Map table) {
      this.add((Map)table, (String)null);
   }

   public void add(Map table, String prefix) {
      for(String key : table.values()) {
         this.add(key, prefix);
      }

   }

   public void remove(String value) {
      this.hashtable.remove(value);
   }

   public int size() {
      return this.hashtable.size();
   }

   public Iterator getKeys() {
      return this.hashtable.keySet().iterator();
   }

   public String[] getArray(String prefix) {
      String[] rval = new String[this.hashtable.size()];
      int i = 0;

      for(String x : this.hashtable.keySet()) {
         if (prefix != null) {
            x = prefix + x;
         }

         rval[i++] = x;
      }

      return rval;
   }
}
