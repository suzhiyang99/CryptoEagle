package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
public final class Success$ implements TaskEndReason, Product, Serializable {
   public static final Success$ MODULE$;

   static {
      new Success$();
   }

   public String productPrefix() {
      return "Success";
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
      return x$1 instanceof Success$;
   }

   public int hashCode() {
      return -202516509;
   }

   public String toString() {
      return "Success";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private Success$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
