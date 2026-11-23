package org.apache.manifoldcf.core.interfaces;

public class TimeMarker {
   public static final String _rcsid = "@(#)$Id: TimeMarker.java 988245 2010-08-23 18:39:35Z kwright $";
   long timeValue;

   public TimeMarker(long timeValue) {
      this.timeValue = timeValue;
   }

   public long longValue() {
      return this.timeValue;
   }

   public String toString() {
      return (new Long(this.timeValue)).toString();
   }
}
