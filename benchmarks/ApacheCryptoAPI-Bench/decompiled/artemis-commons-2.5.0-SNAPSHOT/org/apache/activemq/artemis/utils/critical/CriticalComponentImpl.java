package org.apache.activemq.artemis.utils.critical;

public class CriticalComponentImpl implements CriticalComponent {
   private final CriticalMeasure[] measures;
   private final CriticalAnalyzer analyzer;

   public CriticalAnalyzer getCriticalAnalyzer() {
      return this.analyzer;
   }

   public CriticalComponentImpl(CriticalAnalyzer analyzer, int numberOfPaths) {
      if (analyzer == null) {
         analyzer = EmptyCriticalAnalyzer.getInstance();
      }

      this.analyzer = analyzer;
      if (analyzer.isMeasuring()) {
         this.measures = new CriticalMeasure[numberOfPaths];

         for(int i = 0; i < numberOfPaths; ++i) {
            this.measures[i] = new CriticalMeasure();
         }
      } else {
         this.measures = null;
      }

   }

   public void enterCritical(int path) {
      if (this.analyzer.isMeasuring()) {
         this.measures[path].enterCritical();
      }

   }

   public void leaveCritical(int path) {
      if (this.analyzer.isMeasuring()) {
         this.measures[path].leaveCritical();
      }

   }

   public boolean isExpired(long timeout) {
      for(int i = 0; i < this.measures.length; ++i) {
         if (this.measures[i].isExpired(timeout)) {
            return true;
         }
      }

      return false;
   }
}
