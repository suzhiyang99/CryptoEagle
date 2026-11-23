package org.apache.spark;

import scala.Predef$;
import scala.collection.Seq;
import scala.collection.Seq$;

public abstract class ExecutorAllocationClient$class {
   public static boolean killExecutor(ExecutorAllocationClient $this, String executorId) {
      Seq killedExecutors = $this.killExecutors((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{executorId}))), $this.killExecutors$default$2(), $this.killExecutors$default$3());
      return killedExecutors.nonEmpty() && ((String)killedExecutors.apply(0)).equals(executorId);
   }

   public static boolean killExecutors$default$2(ExecutorAllocationClient $this) {
      return false;
   }

   public static boolean killExecutors$default$3(ExecutorAllocationClient $this) {
      return false;
   }

   public static void $init$(ExecutorAllocationClient $this) {
   }
}
