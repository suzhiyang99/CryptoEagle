package org.apache.spark;

import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public final class ExpireDeadHosts$ implements Product, Serializable {
   public static final ExpireDeadHosts$ MODULE$;

   static {
      new ExpireDeadHosts$();
   }

   public String productPrefix() {
      return "ExpireDeadHosts";
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
      return x$1 instanceof ExpireDeadHosts$;
   }

   public int hashCode() {
      return 1613438792;
   }

   public String toString() {
      return "ExpireDeadHosts";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExpireDeadHosts$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
