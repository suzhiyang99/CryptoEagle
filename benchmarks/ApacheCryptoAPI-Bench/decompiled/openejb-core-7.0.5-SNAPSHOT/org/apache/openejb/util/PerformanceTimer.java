package org.apache.openejb.util;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class PerformanceTimer {
   protected Start start;
   private Event event;

   public PerformanceTimer() {
      this.event = this.start = new Start();
   }

   public void event(String event) {
      this.event = new Event(this.event, event);
   }

   public void stop(PrintStream out) {
      Event event = new Event(this.event, "stop");
      this.event.stop(event, out);
      this.start.stop(event, out);
   }

   private class Event {
      protected final long start = System.nanoTime();
      private final Event previous;
      private final String description;

      Event(Event previous, String description) {
         this.previous = previous;
         this.description = description;
      }

      public void stop(Event next, PrintStream out) {
         if (this.previous != PerformanceTimer.this.start) {
            this.previous.stop(this, out);
         }

         out.printf("%s  %s", TimeUnit.NANOSECONDS.toMillis(next.start - this.start), this.description);
         out.println();
      }
   }

   private final class Start extends Event {
      private Start() {
         super((Event)null, "start");
      }

      public void stop(Event next, PrintStream out) {
         out.printf("%s  %s", TimeUnit.NANOSECONDS.toMillis(next.start - this.start), "total");
         out.println();
      }
   }
}
