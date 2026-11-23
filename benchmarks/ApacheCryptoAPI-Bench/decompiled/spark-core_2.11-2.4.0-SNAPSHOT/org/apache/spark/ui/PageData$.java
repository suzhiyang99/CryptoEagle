package org.apache.spark.ui;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.Seq;
import scala.runtime.BoxesRunTime;

public final class PageData$ implements Serializable {
   public static final PageData$ MODULE$;

   static {
      new PageData$();
   }

   public final String toString() {
      return "PageData";
   }

   public <T> PageData<T> apply(int totalPage, Seq<T> data) {
      return new PageData<T>(totalPage, data);
   }

   public <T> Option<Tuple2<Object, Seq<T>>> unapply(PageData<T> x$0) {
      return (Option<Tuple2<Object, Seq<T>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(BoxesRunTime.boxToInteger(x$0.totalPage()), x$0.data())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private PageData$() {
      MODULE$ = this;
   }
}
