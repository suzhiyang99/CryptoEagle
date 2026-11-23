package org.apache.manifoldcf.core.interfaces;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class StringSet {
   public static final String _rcsid = "@(#)$Id: StringSet.java 988245 2010-08-23 18:39:35Z kwright $";
   protected HashMap hashtable = new HashMap();
   protected String descriptiveString = null;

   public StringSet() {
   }

   public StringSet(StringSetBuffer buffer) {
      Iterator x = buffer.getKeys();

      while(x.hasNext()) {
         String key = (String)x.next();
         this.hashtable.put(key, key);
      }

   }

   public StringSet(String x) {
      this.hashtable.put(x, x);
   }

   public StringSet(String[] array) {
      int i = 0;

      while(i < array.length) {
         String x = array[i++];
         this.hashtable.put(x, x);
      }

   }

   public StringSet(Vector stringVector) {
      int i = 0;

      while(i < stringVector.size()) {
         String x = (String)stringVector.elementAt(i++);
         this.hashtable.put(x, x);
      }

   }

   public StringSet(Map table) {
      for(String key : table.values()) {
         this.hashtable.put(key, key);
      }

   }

   public boolean contains(String key) {
      return this.hashtable.get(key) != null;
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

   public Iterator getKeys() {
      return this.hashtable.keySet().iterator();
   }

   public int size() {
      return this.hashtable.size();
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

   public StringSet(StringSet oldOne, String prefix) {
      for(String x : oldOne.hashtable.keySet()) {
         if (prefix != null) {
            x = prefix + x;
         }

         this.hashtable.put(x, x);
      }

   }

   public int hashCode() {
      return this.hashtable.hashCode();
   }

   public boolean equals(Object o) {
      if (!(o instanceof StringSet)) {
         return false;
      } else {
         StringSet set = (StringSet)o;
         return this.hashtable.equals(set.hashtable);
      }
   }

   public String getDescriptiveString() {
      if (this.descriptiveString == null) {
         String[] array = this.getArray((String)null);
         Arrays.sort(array);
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < array.length; sb.append(array[i++])) {
            if (i > 0) {
               sb.append(":");
            }
         }

         this.descriptiveString = sb.toString();
      }

      return this.descriptiveString;
   }
}
