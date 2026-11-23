package org.apache.spark.rpc.netty;

import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public final class OnStop$ implements InboxMessage, Product, Serializable {
   public static final OnStop$ MODULE$;

   static {
      new OnStop$();
   }

   public String productPrefix() {
      return "OnStop";
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
      return x$1 instanceof OnStop$;
   }

   public int hashCode() {
      return -1929089375;
   }

   public String toString() {
      return "OnStop";
   }

   private Object readResolve() {
      return MODULE$;
   }

   private OnStop$() {
      MODULE$ = this;
      Product$class.$init$(this);
   }
}
