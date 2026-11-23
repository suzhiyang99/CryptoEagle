package org.apache.activemq.artemis.utils;

import java.util.Random;

public class UUIDTimer {
   private static final long kClockOffset = 122192928000000000L;
   private static final long kClockMultiplier = 10000L;
   private static final long kClockMultiplierL = 10000L;
   private static final long kMaxClockAdvance = 100L;
   private final Random mRnd;
   private final byte[] mClockSequence = new byte[3];
   private long mLastSystemTimestamp = 0L;
   private long mLastUsedTimestamp = 0L;
   private int mClockCounter = 0;
   private static final int MAX_WAIT_COUNT = 50;

   UUIDTimer(Random rnd) {
      this.mRnd = rnd;
      this.initCounters(rnd);
      this.mLastSystemTimestamp = 0L;
      this.mLastUsedTimestamp = 0L;
   }

   private void initCounters(Random rnd) {
      rnd.nextBytes(this.mClockSequence);
      this.mClockCounter = this.mClockSequence[2] & 255;
   }

   public void getTimestamp(byte[] uuidData) {
      uuidData[8] = this.mClockSequence[0];
      uuidData[9] = this.mClockSequence[1];
      long systime = System.currentTimeMillis();
      if (systime < this.mLastSystemTimestamp) {
         this.mLastSystemTimestamp = systime;
      }

      if (systime <= this.mLastUsedTimestamp) {
         if ((long)this.mClockCounter < 10000L) {
            systime = this.mLastUsedTimestamp;
         } else {
            long actDiff = this.mLastUsedTimestamp - systime;
            long origTime = systime;
            systime = this.mLastUsedTimestamp + 1L;
            this.initCounters(this.mRnd);
            if (actDiff >= 100L) {
               slowDown(origTime, actDiff);
            }
         }
      } else {
         this.mClockCounter &= 255;
      }

      this.mLastUsedTimestamp = systime;
      systime *= 10000L;
      systime += 122192928000000000L;
      systime += (long)this.mClockCounter;
      ++this.mClockCounter;
      int clockHi = (int)(systime >>> 32);
      int clockLo = (int)systime;
      uuidData[6] = (byte)(clockHi >>> 24);
      uuidData[7] = (byte)(clockHi >>> 16);
      uuidData[4] = (byte)(clockHi >>> 8);
      uuidData[5] = (byte)clockHi;
      uuidData[0] = (byte)(clockLo >>> 24);
      uuidData[1] = (byte)(clockLo >>> 16);
      uuidData[2] = (byte)(clockLo >>> 8);
      uuidData[3] = (byte)clockLo;
   }

   private static void slowDown(long startTime, long actDiff) {
      long ratio = actDiff / 100L;
      long delay;
      if (ratio < 2L) {
         delay = 1L;
      } else if (ratio < 10L) {
         delay = 2L;
      } else if (ratio < 600L) {
         delay = 3L;
      } else {
         delay = 5L;
      }

      long waitUntil = startTime + delay;
      int counter = 0;

      do {
         try {
            Thread.sleep(delay);
         } catch (InterruptedException var12) {
         }

         delay = 1L;
         ++counter;
      } while(counter <= 50 && System.currentTimeMillis() < waitUntil);

   }
}
