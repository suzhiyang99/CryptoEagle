package org.apache.spark.status.api.v1;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class ErrorWrapper$ extends AbstractFunction1<String, ErrorWrapper> implements Serializable {
   public static final ErrorWrapper$ MODULE$;

   static {
      new ErrorWrapper$();
   }

   public final String toString() {
      return "ErrorWrapper";
   }

   public ErrorWrapper apply(String s) {
      return new ErrorWrapper(s);
   }

   public Option<String> unapply(ErrorWrapper x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.s()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ErrorWrapper$() {
      MODULE$ = this;
   }
}
