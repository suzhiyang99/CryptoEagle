package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
public final class TaskResultLost$ implements TaskFailedReason, Product, Serializable {
   public static final TaskResultLost$ MODULE$;

   static {
      new TaskResultLost$();
   }

   public boolean countTowardsTaskFailures() {
      return TaskFailedReason$class.countTowardsTaskFailures(this);
   }

   public String toErrorString() {
      return "TaskResultLost (result lost from block manager)";
   }

   public String productPrefix() {
      return "TaskResultLost";
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
      return x$1 instanceof TaskResultLost$;
   }

   public int hashCode() {
      return -2121783194;
   }

   public String toString() {
      return "TaskResultLost";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskResultLost$() {
      MODULE$ = this;
      TaskFailedReason$class.$init$(this);
      Product$class.$init$(this);
   }
}
