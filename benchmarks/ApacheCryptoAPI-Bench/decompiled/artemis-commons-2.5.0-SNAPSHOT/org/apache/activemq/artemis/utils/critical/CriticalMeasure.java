package org.apache.activemq.artemis.utils.critical;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class CriticalMeasure {
   private static final AtomicLongFieldUpdater<CriticalMeasure> TIME_ENTER_UPDATER = AtomicLongFieldUpdater.newUpdater(CriticalMeasure.class, "timeEnter");
   private static final AtomicLongFieldUpdater<CriticalMeasure> TIME_LEFT_UPDATER = AtomicLongFieldUpdater.newUpdater(CriticalMeasure.class, "timeLeft");
   private volatile long timeEnter;
   private volatile long timeLeft;

   public CriticalMeasure() {
      this.enterCritical();
      this.leaveCritical();
   }

   public void enterCritical() {
      TIME_ENTER_UPDATER.lazySet(this, System.nanoTime());
   }

   public void leaveCritical() {
      TIME_LEFT_UPDATER.lazySet(this, System.nanoTime());
   }

   public boolean isExpired(long timeout) {
      long timeLeft = TIME_LEFT_UPDATER.get(this);
      long timeEnter = TIME_ENTER_UPDATER.get(this);
      if (timeLeft - timeEnter < 0L) {
         return System.nanoTime() - timeEnter > timeout;
      } else {
         return false;
      }
   }

   public long enterTime() {
      return TIME_ENTER_UPDATER.get(this);
   }

   public long leaveTime() {
      return TIME_LEFT_UPDATER.get(this);
   }
}
