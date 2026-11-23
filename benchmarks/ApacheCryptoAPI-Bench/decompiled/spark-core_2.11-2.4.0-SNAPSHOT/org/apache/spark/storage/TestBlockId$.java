package org.apache.spark.storage;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class TestBlockId$ extends AbstractFunction1<String, TestBlockId> implements Serializable {
   public static final TestBlockId$ MODULE$;

   static {
      new TestBlockId$();
   }

   public final String toString() {
      return "TestBlockId";
   }

   public TestBlockId apply(String id) {
      return new TestBlockId(id);
   }

   public Option<String> unapply(TestBlockId x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.id()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TestBlockId$() {
      MODULE$ = this;
   }
}
