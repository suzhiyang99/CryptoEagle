package org.apache.spark;

import org.apache.spark.scheduler.AccumulableInfo;
import org.apache.spark.util.AccumulatorV2;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple7;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Nil$;
import scala.runtime.AbstractFunction7;

public final class ExceptionFailure$ extends AbstractFunction7<String, String, StackTraceElement[], String, Option<ThrowableSerializationWrapper>, Seq<AccumulableInfo>, Seq<AccumulatorV2<?, ?>>, ExceptionFailure> implements Serializable {
   public static final ExceptionFailure$ MODULE$;

   static {
      new ExceptionFailure$();
   }

   public final String toString() {
      return "ExceptionFailure";
   }

   public ExceptionFailure apply(String className, String description, StackTraceElement[] stackTrace, String fullStackTrace, Option<ThrowableSerializationWrapper> exceptionWrapper, Seq<AccumulableInfo> accumUpdates, Seq<AccumulatorV2<?, ?>> accums) {
      return new ExceptionFailure(className, description, stackTrace, fullStackTrace, exceptionWrapper, accumUpdates, accums);
   }

   public Option<Tuple7<String, String, StackTraceElement[], String, Option<ThrowableSerializationWrapper>, Seq<AccumulableInfo>, Seq<AccumulatorV2<?, ?>>>> unapply(ExceptionFailure x$0) {
      return (Option<Tuple7<String, String, StackTraceElement[], String, Option<ThrowableSerializationWrapper>, Seq<AccumulableInfo>, Seq<AccumulatorV2<?, ?>>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple7(x$0.className(), x$0.description(), x$0.stackTrace(), x$0.fullStackTrace(), x$0.org$apache$spark$ExceptionFailure$$exceptionWrapper(), x$0.accumUpdates(), x$0.accums())));
   }

   public Seq<AccumulableInfo> $lessinit$greater$default$6() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public Seq<AccumulatorV2<?, ?>> $lessinit$greater$default$7() {
      return Nil$.MODULE$;
   }

   public Seq<AccumulableInfo> apply$default$6() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public Seq<AccumulatorV2<?, ?>> apply$default$7() {
      return Nil$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ExceptionFailure$() {
      MODULE$ = this;
   }
}
