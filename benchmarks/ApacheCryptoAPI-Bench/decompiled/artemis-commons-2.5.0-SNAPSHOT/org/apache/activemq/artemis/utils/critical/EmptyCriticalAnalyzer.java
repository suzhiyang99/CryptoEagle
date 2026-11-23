package org.apache.activemq.artemis.utils.critical;

import java.util.concurrent.TimeUnit;

public class EmptyCriticalAnalyzer implements CriticalAnalyzer {
   private static final EmptyCriticalAnalyzer instance = new EmptyCriticalAnalyzer();

   public static EmptyCriticalAnalyzer getInstance() {
      return instance;
   }

   private EmptyCriticalAnalyzer() {
   }

   public void add(CriticalComponent component) {
   }

   public void remove(CriticalComponent component) {
   }

   public boolean isMeasuring() {
      return false;
   }

   public void start() throws Exception {
   }

   public void stop() throws Exception {
   }

   public boolean isStarted() {
      return false;
   }

   public CriticalAnalyzer setCheckTime(long timeout, TimeUnit unit) {
      return this;
   }

   public long getCheckTimeNanoSeconds() {
      return 0L;
   }

   public CriticalAnalyzer setTimeout(long timeout, TimeUnit unit) {
      return this;
   }

   public long getTimeout(TimeUnit unit) {
      return 0L;
   }

   public CriticalAnalyzer addAction(CriticalAction action) {
      return this;
   }

   public void check() {
   }
}
