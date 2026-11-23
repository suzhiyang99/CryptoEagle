package org.apache.spark;

import scala.Enumeration;
import scala.Predef$;
import scala.collection.immutable.Set;

public final class TaskState$ extends Enumeration {
   public static final TaskState$ MODULE$;
   private final Enumeration.Value LAUNCHING;
   private final Enumeration.Value RUNNING;
   private final Enumeration.Value FINISHED;
   private final Enumeration.Value FAILED;
   private final Enumeration.Value KILLED;
   private final Enumeration.Value LOST;
   private final Set<Enumeration.Value> FINISHED_STATES;

   static {
      new TaskState$();
   }

   public Enumeration.Value LAUNCHING() {
      return this.LAUNCHING;
   }

   public Enumeration.Value RUNNING() {
      return this.RUNNING;
   }

   public Enumeration.Value FINISHED() {
      return this.FINISHED;
   }

   public Enumeration.Value FAILED() {
      return this.FAILED;
   }

   public Enumeration.Value KILLED() {
      return this.KILLED;
   }

   public Enumeration.Value LOST() {
      return this.LOST;
   }

   private Set<Enumeration.Value> FINISHED_STATES() {
      return this.FINISHED_STATES;
   }

   public boolean isFailed(Enumeration.Value state) {
      boolean var5;
      label32: {
         Enumeration.Value var10000 = this.LOST();
         if (var10000 == null) {
            if (state == null) {
               break label32;
            }
         } else if (var10000.equals(state)) {
            break label32;
         }

         var10000 = this.FAILED();
         if (var10000 == null) {
            if (state == null) {
               break label32;
            }
         } else if (var10000.equals(state)) {
            break label32;
         }

         var5 = false;
         return var5;
      }

      var5 = true;
      return var5;
   }

   public boolean isFinished(Enumeration.Value state) {
      return this.FINISHED_STATES().contains(state);
   }

   private TaskState$() {
      MODULE$ = this;
      this.LAUNCHING = this.Value();
      this.RUNNING = this.Value();
      this.FINISHED = this.Value();
      this.FAILED = this.Value();
      this.KILLED = this.Value();
      this.LOST = this.Value();
      this.FINISHED_STATES = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray(new Enumeration.Value[]{this.FINISHED(), this.FAILED(), this.KILLED(), this.LOST()}));
   }
}
