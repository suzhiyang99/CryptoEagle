package org.apache.wicket.util.time;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TimeMap implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<ITimeFrameSource, Object> sources = new ConcurrentHashMap();

   public Object get() {
      return this.get(Time.now());
   }

   public Object get(Time time) {
      for(ITimeFrameSource source : this.sources.keySet()) {
         TimeFrame current = source.getTimeFrame();
         if (current.contains(time)) {
            return this.sources.get(current);
         }
      }

      return null;
   }

   public void put(ITimeFrameSource source, Object o) {
      TimeFrame timeframe = source.getTimeFrame();

      for(ITimeFrameSource tfs : this.sources.keySet()) {
         TimeFrame current = tfs.getTimeFrame();
         if (timeframe.overlaps(current)) {
            throw new IllegalArgumentException("Timeframe " + timeframe + " overlaps timeframe " + current);
         }
      }

      this.sources.put(source, o);
   }
}
