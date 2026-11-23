package org.apache.spark.rpc.netty;

import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public final class OnStart$ implements InboxMessage, Product, Serializable {
   public static final OnStart$ MODULE$;

   static {
      new OnStart$();
   }

   public String productPrefix() {
      return "OnStart";
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
      return x$1 instanceof OnStart$;
   }

   public int hashCode() {
      return 327758243;
   }

   public String toString() {
      return "OnStart";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private OnStart$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
