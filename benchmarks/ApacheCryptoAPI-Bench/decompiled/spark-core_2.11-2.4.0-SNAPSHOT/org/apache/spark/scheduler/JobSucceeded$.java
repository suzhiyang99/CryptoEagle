package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
public final class JobSucceeded$ implements JobResult, Product, Serializable {
   public static final JobSucceeded$ MODULE$;

   static {
      new JobSucceeded$();
   }

   public String productPrefix() {
      return "JobSucceeded";
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
      return x$1 instanceof JobSucceeded$;
   }

   public int hashCode() {
      return -59670716;
   }

   public String toString() {
      return "JobSucceeded";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JobSucceeded$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
