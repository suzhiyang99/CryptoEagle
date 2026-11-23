package org.apache.spark.rdd;

import java.util.StringTokenizer;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.mutable.ArrayBuffer;

public final class PipedRDD$ implements Serializable {
   public static final PipedRDD$ MODULE$;

   static {
      new PipedRDD$();
   }

   public Seq<String> tokenize(String command) {
      ArrayBuffer buf = new ArrayBuffer();
      StringTokenizer tok = new StringTokenizer(command);

      while(tok.hasMoreElements()) {
         buf.$plus$eq(tok.nextToken());
      }

      return buf;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private PipedRDD$() {
      MODULE$ = this;
   }
}
