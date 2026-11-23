package org.apache.spark.deploy;

import scala.Enumeration;
import scala.Predef$;
import scala.collection.Seq$;
import scala.collection.SeqLike;

public final class ExecutorState$ extends Enumeration {
   public static final ExecutorState$ MODULE$;
   private final Enumeration.Value LAUNCHING;
   private final Enumeration.Value RUNNING;
   private final Enumeration.Value KILLED;
   private final Enumeration.Value FAILED;
   private final Enumeration.Value LOST;
   private final Enumeration.Value EXITED;

   static {
      new ExecutorState$();
   }

   public Enumeration.Value LAUNCHING() {
      return this.LAUNCHING;
   }

   public Enumeration.Value RUNNING() {
      return this.RUNNING;
   }

   public Enumeration.Value KILLED() {
      return this.KILLED;
   }

   public Enumeration.Value FAILED() {
      return this.FAILED;
   }

   public Enumeration.Value LOST() {
      return this.LOST;
   }

   public Enumeration.Value EXITED() {
      return this.EXITED;
   }

   public boolean isFinished(Enumeration.Value state) {
      return ((SeqLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Enumeration.Value[]{this.KILLED(), this.FAILED(), this.LOST(), this.EXITED()}))).contains(state);
   }

   private ExecutorState$() {
      MODULE$ = this;
      this.LAUNCHING = this.Value();
      this.RUNNING = this.Value();
      this.KILLED = this.Value();
      this.FAILED = this.Value();
      this.LOST = this.Value();
      this.EXITED = this.Value();
   }
}
