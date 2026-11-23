package org.apache.spark.scheduler;

import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public final class ResubmitFailedStages$ implements DAGSchedulerEvent, Product, Serializable {
   public static final ResubmitFailedStages$ MODULE$;

   static {
      new ResubmitFailedStages$();
   }

   public String productPrefix() {
      return "ResubmitFailedStages";
   }

   public int productArity() {
      return 0;
   }

   public Object productElement(int x$1) {
      throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof ResubmitFailedStages$;
   }

   public int hashCode() {
      return 1392879517;
   }

   public String toString() {
      return "ResubmitFailedStages";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ResubmitFailedStages$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
