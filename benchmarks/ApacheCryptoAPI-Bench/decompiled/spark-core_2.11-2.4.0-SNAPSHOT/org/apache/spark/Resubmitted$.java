package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
public final class Resubmitted$ implements TaskFailedReason, Product, Serializable {
   public static final Resubmitted$ MODULE$;

   static {
      new Resubmitted$();
   }

   public boolean countTowardsTaskFailures() {
      return TaskFailedReason$class.countTowardsTaskFailures(this);
   }

   public String toErrorString() {
      return "Resubmitted (resubmitted due to lost executor)";
   }

   public String productPrefix() {
      return "Resubmitted";
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
      return x$1 instanceof Resubmitted$;
   }

   public int hashCode() {
      return 654527496;
   }

   public String toString() {
      return "Resubmitted";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private Resubmitted$() {
      MODULE$ = this;
      TaskFailedReason$class.$init$(this);
      Product$class.$init$(this);
   }
}
