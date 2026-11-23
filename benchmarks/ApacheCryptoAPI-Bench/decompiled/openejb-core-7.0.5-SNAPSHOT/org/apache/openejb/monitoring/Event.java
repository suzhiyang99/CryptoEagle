package org.apache.openejb.monitoring;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Managed(
   append = true
)
public class Event {
   private final AtomicLong count = new AtomicLong();
   private final AtomicLong last = new AtomicLong();

   public void record() {
      this.last.getAndSet(System.currentTimeMillis());
      this.count.incrementAndGet();
   }

   @Managed
   public long get() {
      return this.count.get();
   }

   @Managed
   public String getLatest() {
      long last = this.last.get();
      if (last <= 0L) {
         return "-";
      } else {
         DateFormat format = SimpleDateFormat.getDateTimeInstance();
         return format.format(new Date(last));
      }
   }

   @Managed
   public long getLatestTime() {
      return this.last.get();
   }
}
