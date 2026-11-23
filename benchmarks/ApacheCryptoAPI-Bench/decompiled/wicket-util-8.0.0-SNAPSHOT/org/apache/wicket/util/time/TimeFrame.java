package org.apache.wicket.util.time;

import org.apache.wicket.util.lang.Objects;

public final class TimeFrame implements ITimeFrameSource {
   private static final long serialVersionUID = 1L;
   private final Time end;
   private final Time start;

   public static ITimeFrameSource eachDay(final TimeOfDay startTimeOfDay, final TimeOfDay endTimeOfDay) {
      check(startTimeOfDay, endTimeOfDay);
      return new ITimeFrameSource() {
         private static final long serialVersionUID = 1L;

         public TimeFrame getTimeFrame() {
            return new TimeFrame(Time.valueOf(startTimeOfDay), Time.valueOf(endTimeOfDay));
         }
      };
   }

   public static TimeFrame valueOf(Time start, Duration duration) {
      return new TimeFrame(start, start.add(duration));
   }

   public static TimeFrame valueOf(Time start, Time end) {
      return new TimeFrame(start, end);
   }

   private static void check(AbstractTimeValue start, AbstractTimeValue end) {
      if (end.lessThan(start)) {
         throw new IllegalArgumentException("Start time of time frame " + start + " was after end time " + end);
      }
   }

   private TimeFrame(Time start, Time end) {
      check(start, end);
      this.start = start;
      this.end = end;
   }

   public boolean contains(Time time) {
      return (this.start.equals(time) || this.start.before(time)) && this.end.after(time);
   }

   public Duration getDuration() {
      return this.end.subtract(this.start);
   }

   public Time getEnd() {
      return this.end;
   }

   public Time getStart() {
      return this.start;
   }

   public TimeFrame getTimeFrame() {
      return this;
   }

   public boolean overlaps(TimeFrame timeframe) {
      return this.contains(timeframe.start) || this.contains(timeframe.end) || timeframe.contains(this.start) || timeframe.contains(this.end);
   }

   public int hashCode() {
      return Objects.hashCode(this.start, this.end);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         TimeFrame other = (TimeFrame)obj;
         return Objects.equal(this.start, other.start) && Objects.equal(this.end, other.end);
      }
   }

   public String toString() {
      return "[start=" + this.start + ", end=" + this.end + "]";
   }
}
