package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
public final class UnknownReason$ implements TaskFailedReason, Product, Serializable {
   public static final UnknownReason$ MODULE$;

   static {
      new UnknownReason$();
   }

   public boolean countTowardsTaskFailures() {
      return TaskFailedReason$class.countTowardsTaskFailures(this);
   }

   public String toErrorString() {
      return "UnknownReason";
   }

   public String productPrefix() {
      return "UnknownReason";
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
      return x$1 instanceof UnknownReason$;
   }

   public int hashCode() {
      return -1830061298;
   }

   public String toString() {
      return "UnknownReason";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private UnknownReason$() {
      MODULE$ = this;
      TaskFailedReason$class.$init$(this);
      Product$class.$init$(this);
   }
}
