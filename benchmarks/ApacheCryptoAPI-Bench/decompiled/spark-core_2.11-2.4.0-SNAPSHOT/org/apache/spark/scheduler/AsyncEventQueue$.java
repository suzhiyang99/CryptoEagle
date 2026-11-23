package org.apache.spark.scheduler;

public final class AsyncEventQueue$ {
   public static final AsyncEventQueue$ MODULE$;
   private final SparkListenerEvent POISON_PILL;

   static {
      new AsyncEventQueue$();
   }

   public SparkListenerEvent POISON_PILL() {
      return this.POISON_PILL;
   }

   private AsyncEventQueue$() {
      MODULE$ = this;
      this.POISON_PILL = new SparkListenerEvent() {
         public boolean logEvent() {
            return SparkListenerEvent$class.logEvent(this);
         }

         public {
            SparkListenerEvent$class.$init$(this);
         }
      };
   }
}
