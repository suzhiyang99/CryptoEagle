package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple7;
import scala.collection.Seq;
import scala.runtime.AbstractFunction7;
import scala.runtime.BoxesRunTime;

public final class ThreadStackTrace$ extends AbstractFunction7<Object, String, Thread.State, String, Option<Object>, String, Seq<String>, ThreadStackTrace> implements Serializable {
   public static final ThreadStackTrace$ MODULE$;

   static {
      new ThreadStackTrace$();
   }

   public final String toString() {
      return "ThreadStackTrace";
   }

   public ThreadStackTrace apply(long threadId, String threadName, Thread.State threadState, String stackTrace, Option<Object> blockedByThreadId, String blockedByLock, Seq<String> holdingLocks) {
      return new ThreadStackTrace(threadId, threadName, threadState, stackTrace, blockedByThreadId, blockedByLock, holdingLocks);
   }

   public Option<Tuple7<Object, String, Thread.State, String, Option<Object>, String, Seq<String>>> unapply(ThreadStackTrace x$0) {
      return (Option<Tuple7<Object, String, Thread.State, String, Option<Object>, String, Seq<String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple7(BoxesRunTime.boxToLong(x$0.threadId()), x$0.threadName(), x$0.threadState(), x$0.stackTrace(), x$0.blockedByThreadId(), x$0.blockedByLock(), x$0.holdingLocks())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ThreadStackTrace$() {
      MODULE$ = this;
   }
}
