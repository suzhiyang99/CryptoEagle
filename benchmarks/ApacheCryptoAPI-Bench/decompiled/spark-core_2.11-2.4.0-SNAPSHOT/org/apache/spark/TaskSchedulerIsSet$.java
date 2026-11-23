package org.apache.spark;

import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public final class TaskSchedulerIsSet$ implements Product, Serializable {
   public static final TaskSchedulerIsSet$ MODULE$;

   static {
      new TaskSchedulerIsSet$();
   }

   public String productPrefix() {
      return "TaskSchedulerIsSet";
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
      return x$1 instanceof TaskSchedulerIsSet$;
   }

   public int hashCode() {
      return -234951710;
   }

   public String toString() {
      return "TaskSchedulerIsSet";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TaskSchedulerIsSet$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
