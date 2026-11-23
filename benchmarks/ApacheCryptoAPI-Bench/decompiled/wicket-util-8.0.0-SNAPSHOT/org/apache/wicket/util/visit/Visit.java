package org.apache.wicket.util.visit;

public class Visit<R> implements IVisit<R> {
   private R result;
   private Action action;

   public Visit() {
      this.action = Visit.Action.CONTINUE;
   }

   public void stop() {
      this.stop((Object)null);
   }

   public void stop(R result) {
      this.action = Visit.Action.STOP;
      this.result = result;
   }

   public void dontGoDeeper() {
      this.action = Visit.Action.CONTINUE_BUT_DONT_GO_DEEPER;
   }

   public boolean isStopped() {
      return this.action == Visit.Action.STOP;
   }

   public boolean isContinue() {
      return this.action == Visit.Action.CONTINUE;
   }

   public boolean isDontGoDeeper() {
      return this.action == Visit.Action.CONTINUE_BUT_DONT_GO_DEEPER;
   }

   public R getResult() {
      return this.result;
   }

   private static enum Action {
      CONTINUE,
      CONTINUE_BUT_DONT_GO_DEEPER,
      STOP;
   }
}
