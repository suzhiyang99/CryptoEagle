package org.apache.spark.api.python;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;

public final class TestWritable$ extends AbstractFunction3<String, Object, Object, TestWritable> implements Serializable {
   public static final TestWritable$ MODULE$;

   static {
      new TestWritable$();
   }

   public final String toString() {
      return "TestWritable";
   }

   public TestWritable apply(String str, int var2, double var3) {
      return new TestWritable(str, var2, var3);
   }

   public Option<Tuple3<String, Object, Object>> unapply(TestWritable x$0) {
      return (Option<Tuple3<String, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.str(), BoxesRunTime.boxToInteger(x$0.int()), BoxesRunTime.boxToDouble(x$0.double()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TestWritable$() {
      MODULE$ = this;
   }
}
