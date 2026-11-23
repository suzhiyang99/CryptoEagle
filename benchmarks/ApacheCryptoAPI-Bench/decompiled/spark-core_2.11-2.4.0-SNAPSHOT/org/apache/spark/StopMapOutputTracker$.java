package org.apache.spark;

import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public final class StopMapOutputTracker$ implements MapOutputTrackerMessage, Product, Serializable {
   public static final StopMapOutputTracker$ MODULE$;

   static {
      new StopMapOutputTracker$();
   }

   public String productPrefix() {
      return "StopMapOutputTracker";
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
      return x$1 instanceof StopMapOutputTracker$;
   }

   public int hashCode() {
      return -174801091;
   }

   public String toString() {
      return "StopMapOutputTracker";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private StopMapOutputTracker$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
