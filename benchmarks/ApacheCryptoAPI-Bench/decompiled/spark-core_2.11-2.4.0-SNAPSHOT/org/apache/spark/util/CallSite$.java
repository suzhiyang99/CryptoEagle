package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;

public final class CallSite$ implements Serializable {
   public static final CallSite$ MODULE$;
   private final String SHORT_FORM;
   private final String LONG_FORM;
   private final CallSite empty;

   static {
      new CallSite$();
   }

   public String SHORT_FORM() {
      return this.SHORT_FORM;
   }

   public String LONG_FORM() {
      return this.LONG_FORM;
   }

   public CallSite empty() {
      return this.empty;
   }

   public CallSite apply(String shortForm, String longForm) {
      return new CallSite(shortForm, longForm);
   }

   public Option<Tuple2<String, String>> unapply(CallSite x$0) {
      return (Option<Tuple2<String, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.shortForm(), x$0.longForm())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private CallSite$() {
      MODULE$ = this;
      this.SHORT_FORM = "callSite.short";
      this.LONG_FORM = "callSite.long";
      this.empty = new CallSite("", "");
   }
}
