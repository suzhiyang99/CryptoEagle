package org.apache.spark;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.spark.api.java.JavaFutureAction;
import org.apache.spark.util.ThreadUtils$;
import scala.Function1;
import scala.MatchError;
import scala.Serializable;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.Duration$;
import scala.reflect.ScalaSignature;
import scala.util.Failure;
import scala.util.Try;

@ScalaSignature(
   bytes = "\u0006\u0001I4Q!\u0001\u0002\u0001\u0005!\u0011qCS1wC\u001a+H/\u001e:f\u0003\u000e$\u0018n\u001c8Xe\u0006\u0004\b/\u001a:\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e,2!C\u0018\u001c'\r\u0001!B\u0005\t\u0003\u0017Ai\u0011\u0001\u0004\u0006\u0003\u001b9\tA\u0001\\1oO*\tq\"\u0001\u0003kCZ\f\u0017BA\t\r\u0005\u0019y%M[3diB\u00191cF\r\u000e\u0003QQ!aD\u000b\u000b\u0005Y\u0011\u0011aA1qS&\u0011\u0001\u0004\u0006\u0002\u0011\u0015\u00064\u0018MR;ukJ,\u0017i\u0019;j_:\u0004\"AG\u000e\r\u0001\u0011)A\u0004\u0001b\u0001=\t\tAk\u0001\u0001\u0012\u0005})\u0003C\u0001\u0011$\u001b\u0005\t#\"\u0001\u0012\u0002\u000bM\u001c\u0017\r\\1\n\u0005\u0011\n#a\u0002(pi\"Lgn\u001a\t\u0003A\u0019J!aJ\u0011\u0003\u0007\u0005s\u0017\u0010\u0003\u0005*\u0001\t\u0005\t\u0015!\u0003+\u000311W\u000f^;sK\u0006\u001bG/[8o!\rYCFL\u0007\u0002\u0005%\u0011QF\u0001\u0002\r\rV$XO]3BGRLwN\u001c\t\u00035=\"Q\u0001\r\u0001C\u0002y\u0011\u0011a\u0015\u0005\te\u0001\u0011\t\u0011)A\u0005g\u0005I1m\u001c8wKJ$XM\u001d\t\u0005AQr\u0013$\u0003\u00026C\tIa)\u001e8di&|g.\r\u0005\u0006o\u0001!\t\u0001O\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007eR4\b\u0005\u0003,\u00019J\u0002\"B\u00157\u0001\u0004Q\u0003\"\u0002\u001a7\u0001\u0004\u0019\u0004\"B\u001f\u0001\t\u0003r\u0014aC5t\u0007\u0006t7-\u001a7mK\u0012$\u0012a\u0010\t\u0003A\u0001K!!Q\u0011\u0003\u000f\t{w\u000e\\3b]\")1\t\u0001C!}\u00051\u0011n\u001d#p]\u0016DQ!\u0012\u0001\u0005B\u0019\u000baA[8c\u0013\u0012\u001cH#A$\u0011\u0007![U*D\u0001J\u0015\tQe\"\u0001\u0003vi&d\u0017B\u0001'J\u0005\u0011a\u0015n\u001d;\u0011\u0005-q\u0015BA(\r\u0005\u001dIe\u000e^3hKJDQ!\u0015\u0001\u0005\nI\u000bqaZ3u\u00136\u0004H\u000e\u0006\u0002\u001a'\")A\u000b\u0015a\u0001+\u00069A/[7f_V$\bC\u0001,\\\u001b\u00059&B\u0001-Z\u0003!!WO]1uS>t'B\u0001.\"\u0003)\u0019wN\\2veJ,g\u000e^\u0005\u00039^\u0013\u0001\u0002R;sCRLwN\u001c\u0005\u0006=\u0002!\teX\u0001\u0004O\u0016$H#A\r\t\u000by\u0003A\u0011I1\u0015\u0007e\u0011g\rC\u0003UA\u0002\u00071\r\u0005\u0002!I&\u0011Q-\t\u0002\u0005\u0019>tw\rC\u0003hA\u0002\u0007\u0001.\u0001\u0003v]&$\bCA5l\u001b\u0005Q'B\u0001.J\u0013\ta'N\u0001\u0005US6,WK\\5u\u0011\u0015q\u0007\u0001\"\u0011p\u0003\u0019\u0019\u0017M\\2fYR\u0011q\b\u001d\u0005\u0006c6\u0004\raP\u0001\u0016[\u0006L\u0018J\u001c;feJ,\b\u000f^%g%Vtg.\u001b8h\u0001"
)
public class JavaFutureActionWrapper<S, T> implements JavaFutureAction<T> {
   private final FutureAction<S> futureAction;
   private final Function1<S, T> converter;

   public boolean isCancelled() {
      return this.futureAction.isCancelled();
   }

   public boolean isDone() {
      return this.futureAction.isCancelled() || this.futureAction.isCompleted();
   }

   public List<Integer> jobIds() {
      return Collections.unmodifiableList((List)JavaConverters$.MODULE$.seqAsJavaListConverter((Seq)this.futureAction.jobIds().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Integer apply(int x$1) {
            return x$1;
         }
      }, Seq$.MODULE$.canBuildFrom())).asJava());
   }

   private T getImpl(Duration timeout) {
      ThreadUtils$.MODULE$.awaitReady(this.futureAction, timeout);
      Try var2 = this.futureAction.value().get();
      if (var2 instanceof scala.util.Success) {
         scala.util.Success var3 = (scala.util.Success)var2;
         Object value = var3.value();
         Object var5 = this.converter.apply(value);
         return (T)var5;
      } else if (var2 instanceof Failure) {
         Failure var6 = (Failure)var2;
         Throwable exception = var6.exception();
         if (this.isCancelled()) {
            throw (new CancellationException("Job cancelled")).initCause(exception);
         } else {
            throw new ExecutionException("Exception thrown by job", exception);
         }
      } else {
         throw new MatchError(var2);
      }
   }

   public T get() {
      return (T)this.getImpl(Duration$.MODULE$.Inf());
   }

   public T get(long timeout, TimeUnit unit) {
      return (T)this.getImpl(Duration$.MODULE$.fromNanos(unit.toNanos(timeout)));
   }

   public synchronized boolean cancel(boolean mayInterruptIfRunning) {
      boolean var10000;
      if (this.isDone()) {
         var10000 = false;
      } else {
         this.futureAction.cancel();
         var10000 = true;
      }

      return var10000;
   }

   public JavaFutureActionWrapper(FutureAction<S> futureAction, Function1<S, T> converter) {
      this.futureAction = futureAction;
      this.converter = converter;
   }
}
