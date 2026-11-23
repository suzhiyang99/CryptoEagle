package org.apache.spark.api.python;

import scala.Predef$;
import scala.collection.Seq;
import scala.collection.Seq$;

public final class PythonRunner$ {
   public static final PythonRunner$ MODULE$;

   static {
      new PythonRunner$();
   }

   public PythonRunner apply(PythonFunction func, int bufferSize, boolean reuseWorker) {
      return new PythonRunner((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new ChainedPythonFunctions[]{new ChainedPythonFunctions((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new PythonFunction[]{func})))})), bufferSize, reuseWorker);
   }

   private PythonRunner$() {
      MODULE$ = this;
   }
}
