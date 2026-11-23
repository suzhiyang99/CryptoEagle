package org.apache.manifoldcf.core.database;

import java.util.HashMap;
import java.util.Iterator;

public class BaseObject {
   public static final String _rcsid = "@(#)$Id: BaseObject.java 988245 2010-08-23 18:39:35Z kwright $";
   protected HashMap fields = new HashMap();

   public void clear() {
      this.fields.clear();
   }

   public Iterator listFields() {
      return this.fields.keySet().iterator();
   }

   public Object getValue(String fieldName) {
      return this.fields.get(fieldName);
   }

   public void setValue(String fieldName, Object fieldValue) {
      if (fieldValue == null) {
         this.fields.remove(fieldName);
      } else {
         this.fields.put(fieldName, fieldValue);
      }

   }
}
