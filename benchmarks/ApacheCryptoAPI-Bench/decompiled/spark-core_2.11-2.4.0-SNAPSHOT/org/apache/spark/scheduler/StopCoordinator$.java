package org.apache.spark.scheduler;

import scala.Product;
import scala.Product$class;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public final class StopCoordinator$ implements OutputCommitCoordinationMessage, Product {
   public static final StopCoordinator$ MODULE$;

   static {
      new StopCoordinator$();
   }

   public String productPrefix() {
      return "StopCoordinator";
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
      return x$1 instanceof StopCoordinator$;
   }

   public int hashCode() {
      return 371711406;
   }

   public String toString() {
      return "StopCoordinator";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private StopCoordinator$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
