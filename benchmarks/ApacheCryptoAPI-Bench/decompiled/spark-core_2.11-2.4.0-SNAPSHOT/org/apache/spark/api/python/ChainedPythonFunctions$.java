package org.apache.spark.api.python;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.collection.Seq;
import scala.runtime.AbstractFunction1;

public final class ChainedPythonFunctions$ extends AbstractFunction1<Seq<PythonFunction>, ChainedPythonFunctions> implements Serializable {
   public static final ChainedPythonFunctions$ MODULE$;

   static {
      new ChainedPythonFunctions$();
   }

   public final String toString() {
      return "ChainedPythonFunctions";
   }

   public ChainedPythonFunctions apply(Seq<PythonFunction> funcs) {
      return new ChainedPythonFunctions(funcs);
   }

   public Option<Seq<PythonFunction>> unapply(ChainedPythonFunctions x$0) {
      return (Option<Seq<PythonFunction>>)(x$0 == null ? None$.MODULE$ : new Some(x$0.funcs()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ChainedPythonFunctions$() {
      MODULE$ = this;
   }
}
