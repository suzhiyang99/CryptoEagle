package org.apache.manifoldcf.core.cachemanager;

import org.apache.manifoldcf.core.interfaces.ICacheClass;
import org.apache.manifoldcf.core.interfaces.ICacheDescription;

public abstract class BaseDescription implements ICacheDescription {
   public static final String _rcsid = "@(#)$Id: BaseDescription.java 988245 2010-08-23 18:39:35Z kwright $";
   protected ICacheClass cacheClass = null;
   protected static final Integer max_value = new Integer(Integer.MAX_VALUE);

   public BaseDescription(String objectClassName) {
      if (objectClassName != null) {
         this.cacheClass = new LocalCacheClass(objectClassName);
      }

   }

   public BaseDescription(String objectClassName, int maxLRUCount) {
      if (objectClassName != null) {
         this.cacheClass = new LocalCacheClass(objectClassName, maxLRUCount);
      }

   }

   public ICacheClass getObjectClass() {
      return this.cacheClass;
   }

   public long getObjectExpirationTime(long currentTime) {
      return -1L;
   }

   protected class LocalCacheClass implements ICacheClass {
      protected String objectClassName;
      protected Integer maxLRUCount;

      public LocalCacheClass(String objectClassName) {
         this(objectClassName, -1);
      }

      public LocalCacheClass(String objectClassName, int maxLRUCount) {
         this.maxLRUCount = null;
         this.objectClassName = objectClassName;
         if (maxLRUCount != -1) {
            this.maxLRUCount = new Integer(maxLRUCount);
         }

      }

      public String getClassName() {
         return this.objectClassName;
      }

      public int getMaxLRUCount() {
         if (this.maxLRUCount == null) {
            try {
               String x = null;
               if (x == null) {
                  this.maxLRUCount = BaseDescription.max_value;
               } else {
                  this.maxLRUCount = new Integer(x);
               }
            } catch (Exception var2) {
               this.maxLRUCount = BaseDescription.max_value;
            }
         }

         return this.maxLRUCount;
      }
   }
}
