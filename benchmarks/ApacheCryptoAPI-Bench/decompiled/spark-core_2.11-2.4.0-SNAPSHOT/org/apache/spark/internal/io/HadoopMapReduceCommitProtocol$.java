package org.apache.spark.internal.io;

import scala.Serializable;

public final class HadoopMapReduceCommitProtocol$ implements Serializable {
   public static final HadoopMapReduceCommitProtocol$ MODULE$;

   static {
      new HadoopMapReduceCommitProtocol$();
   }

   public boolean $lessinit$greater$default$3() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private HadoopMapReduceCommitProtocol$() {
      MODULE$ = this;
   }
}
