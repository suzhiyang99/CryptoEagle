package org.apache.directory.shared.kerberos;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.directory.api.util.Strings;

public class KerberosTime implements Comparable<KerberosTime>, Serializable {
   private static final long serialVersionUID = -7541256140193748103L;
   private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
   private String date;
   private long kerberosTime;
   public static final KerberosTime INFINITY = new KerberosTime(Long.MAX_VALUE);
   public static final int MINUTE = 60000;
   public static final int DAY = 86400000;
   public static final int WEEK = 604800000;

   public KerberosTime() {
      this.kerberosTime = System.currentTimeMillis() / 1000L * 1000L;
      this.convertInternal(this.kerberosTime);
   }

   public KerberosTime(String date) {
      try {
         this.setDate(date);
      } catch (ParseException var3) {
         throw new IllegalArgumentException("Bad time : " + date);
      }
   }

   public KerberosTime(long date) {
      this.convertInternal(date);
   }

   public KerberosTime(Date time) {
      this.kerberosTime = time.getTime() / 1000L * 1000L;
      this.convertInternal(this.kerberosTime);
   }

   private void convertInternal(long date) {
      Calendar calendar = Calendar.getInstance(UTC);
      calendar.setTimeInMillis(date);
      synchronized(KerberosUtils.UTC_DATE_FORMAT) {
         this.date = KerberosUtils.UTC_DATE_FORMAT.format(calendar.getTime());
      }

      this.kerberosTime = calendar.getTimeInMillis() / 1000L * 1000L;
   }

   public long getTime() {
      return this.kerberosTime;
   }

   public Date toDate() {
      return new Date(this.kerberosTime);
   }

   public static KerberosTime getTime(String zuluTime) throws ParseException {
      Date date = null;
      synchronized(KerberosUtils.UTC_DATE_FORMAT) {
         date = KerberosUtils.UTC_DATE_FORMAT.parse(zuluTime);
      }

      return new KerberosTime(date);
   }

   public void setDate(String date) throws ParseException {
      synchronized(KerberosUtils.UTC_DATE_FORMAT) {
         this.kerberosTime = KerberosUtils.UTC_DATE_FORMAT.parse(date).getTime();
      }

      this.convertInternal(this.kerberosTime);
   }

   public byte[] getBytes() {
      return Strings.getBytesUtf8(this.date);
   }

   public String getDate() {
      return this.date;
   }

   public int hashCode() {
      return (int)this.kerberosTime;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return true;
      } else {
         KerberosTime other = (KerberosTime)obj;
         return this.kerberosTime == other.kerberosTime;
      }
   }

   public boolean isInClockSkew(long clockSkew) {
      long delta = Math.abs(this.kerberosTime - System.currentTimeMillis());
      return delta < clockSkew;
   }

   public int compareTo(KerberosTime that) {
      int BEFORE = -1;
      int EQUAL = 0;
      int AFTER = 1;
      if (this == that) {
         return 0;
      } else if (this.kerberosTime < that.kerberosTime) {
         return -1;
      } else {
         return this.kerberosTime > that.kerberosTime ? 1 : 0;
      }
   }

   public boolean lessThan(KerberosTime ktime) {
      return this.kerberosTime <= ktime.kerberosTime;
   }

   public boolean greaterThan(KerberosTime ktime) {
      return this.kerberosTime > ktime.kerberosTime;
   }

   public boolean isZero() {
      return this.kerberosTime == 0L;
   }

   public String toString() {
      return this.date;
   }
}
