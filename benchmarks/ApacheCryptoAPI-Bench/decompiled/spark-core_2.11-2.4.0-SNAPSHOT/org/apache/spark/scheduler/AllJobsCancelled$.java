package org.apache.spark.scheduler;

import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public final class AllJobsCancelled$ implements DAGSchedulerEvent, Product, Serializable {
   public static final AllJobsCancelled$ MODULE$;

   static {
      new AllJobsCancelled$();
   }

   public String productPrefix() {
      return "AllJobsCancelled";
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
      return x$1 instanceof AllJobsCancelled$;
   }

   public int hashCode() {
      return 276190618;
   }

   public String toString() {
      return "AllJobsCancelled";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private AllJobsCancelled$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
