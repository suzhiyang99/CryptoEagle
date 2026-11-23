package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.scheduler.JobWaiter;
import scala.Function0;
import scala.Function1;
import scala.Option;
import scala.PartialFunction;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.concurrent.CanAwait;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Future$class;
import scala.concurrent.duration.Duration;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.util.Try;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005ee\u0001B\u0001\u0003\u0001%\u0011!cU5na2,g)\u001e;ve\u0016\f5\r^5p]*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014xm\u0001\u0001\u0016\u0005)92c\u0001\u0001\f#A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u00042AE\n\u0016\u001b\u0005\u0011\u0011B\u0001\u000b\u0003\u000511U\u000f^;sK\u0006\u001bG/[8o!\t1r\u0003\u0004\u0001\u0005\u000ba\u0001!\u0019A\r\u0003\u0003Q\u000b\"AG\u000f\u0011\u00051Y\u0012B\u0001\u000f\u000e\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"\u0001\u0004\u0010\n\u0005}i!aA!os\"A\u0011\u0005\u0001B\u0001B\u0003%!%A\u0005k_\n<\u0016-\u001b;feB\u00121E\u000b\t\u0004I\u001dJS\"A\u0013\u000b\u0005\u0019\u0012\u0011!C:dQ\u0016$W\u000f\\3s\u0013\tASEA\u0005K_\n<\u0016-\u001b;feB\u0011aC\u000b\u0003\nW\u0001\n\t\u0011!A\u0003\u0002e\u00111a\u0018\u00138\u0011!i\u0003A!A%\u0002\u0013q\u0013A\u0003:fgVdGOR;oGB\u0019AbL\u000b\n\u0005Aj!\u0001\u0003\u001fcs:\fW.\u001a \t\rI\u0002A\u0011\u0001\u00024\u0003\u0019a\u0014N\\5u}Q\u0019A'\u000e\u001e\u0011\u0007I\u0001Q\u0003C\u0003\"c\u0001\u0007a\u0007\r\u00028sA\u0019Ae\n\u001d\u0011\u0005YID!C\u00166\u0003\u0003\u0005\tQ!\u0001\u001a\u0011\u0019i\u0013\u0007\"a\u0001]!9A\b\u0001a\u0001\n\u0013i\u0014AC0dC:\u001cW\r\u001c7fIV\ta\b\u0005\u0002\r\u007f%\u0011\u0001)\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d\u0011\u0005\u00011A\u0005\n\r\u000babX2b]\u000e,G\u000e\\3e?\u0012*\u0017\u000f\u0006\u0002E\u000fB\u0011A\"R\u0005\u0003\r6\u0011A!\u00168ji\"9\u0001*QA\u0001\u0002\u0004q\u0014a\u0001=%c!1!\n\u0001Q!\ny\n1bX2b]\u000e,G\u000e\\3eA!\u0012\u0011\n\u0014\t\u0003\u00195K!AT\u0007\u0003\u0011Y|G.\u0019;jY\u0016DQ\u0001\u0015\u0001\u0005BE\u000baaY1oG\u0016dG#\u0001#\t\u000bM\u0003A\u0011\t+\u0002\u000bI,\u0017\rZ=\u0015\u0005U{FC\u0001,X\u001b\u0005\u0001\u0001\"\u0002-S\u0001\bI\u0016A\u00029fe6LG\u000f\u0005\u0002[;6\t1L\u0003\u0002]\u001b\u0005Q1m\u001c8dkJ\u0014XM\u001c;\n\u0005y[&\u0001C\"b]\u0006;\u0018-\u001b;\t\u000b\u0001\u0014\u0006\u0019A1\u0002\r\u0005$Xj\\:u!\t\u0011W-D\u0001d\u0015\t!7,\u0001\u0005ekJ\fG/[8o\u0013\t17M\u0001\u0005EkJ\fG/[8o\u0011\u0015A\u0007\u0001\"\u0011j\u0003\u0019\u0011Xm];miR\u0011!\u000e\u001c\u000b\u0003+-DQ\u0001W4A\u0004eCQ\u0001Y4A\u0002\u0005D3a\u001a8~!\raq.]\u0005\u0003a6\u0011a\u0001\u001e5s_^\u001c\bC\u0001:{\u001d\t\u0019\bP\u0004\u0002uo6\tQO\u0003\u0002w\u0011\u00051AH]8pizJ\u0011AD\u0005\u0003s6\tq\u0001]1dW\u0006<W-\u0003\u0002|y\nIQ\t_2faRLwN\u001c\u0006\u0003s6\u0019\u0013!\u001d\u0005\u0007\u007f\u0002!\t%!\u0001\u0002\u0015=t7i\\7qY\u0016$X-\u0006\u0003\u0002\u0004\u0005%B\u0003BA\u0003\u0003#!2\u0001RA\u0004\u0011\u001d\tIA a\u0002\u0003\u0017\t\u0001\"\u001a=fGV$xN\u001d\t\u00045\u00065\u0011bAA\b7\n\u0001R\t_3dkRLwN\\\"p]R,\u0007\u0010\u001e\u0005\b\u0003'q\b\u0019AA\u000b\u0003\u00111WO\\2\u0011\u000f1\t9\"a\u0007\u0002(%\u0019\u0011\u0011D\u0007\u0003\u0013\u0019+hn\u0019;j_:\f\u0004#BA\u000f\u0003G)RBAA\u0010\u0015\r\t\t#D\u0001\u0005kRLG.\u0003\u0003\u0002&\u0005}!a\u0001+ssB\u0019a#!\u000b\u0005\r\u0005-bP1\u0001\u001a\u0005\u0005)\u0006BBA\u0018\u0001\u0011\u0005S(A\u0006jg\u000e{W\u000e\u001d7fi\u0016$\u0007BBA\u001a\u0001\u0011\u0005S(A\u0006jg\u000e\u000bgnY3mY\u0016$\u0007bBA\u001c\u0001\u0011\u0005\u0013\u0011H\u0001\u0006m\u0006dW/Z\u000b\u0003\u0003w\u0001R\u0001DA\u001f\u00037I1!a\u0010\u000e\u0005\u0019y\u0005\u000f^5p]\"9\u00111\t\u0001\u0005\u0002\u0005\u0015\u0013A\u00026pE&#7/\u0006\u0002\u0002HA)!/!\u0013\u0002N%\u0019\u00111\n?\u0003\u0007M+\u0017\u000fE\u0002\r\u0003\u001fJ1!!\u0015\u000e\u0005\rIe\u000e\u001e\u0005\b\u0003+\u0002A\u0011IA,\u0003%!(/\u00198tM>\u0014X.\u0006\u0003\u0002Z\u0005\u0015D\u0003BA.\u0003[\"B!!\u0018\u0002jA)!,a\u0018\u0002d%\u0019\u0011\u0011M.\u0003\r\u0019+H/\u001e:f!\r1\u0012Q\r\u0003\b\u0003O\n\u0019F1\u0001\u001a\u0005\u0005\u0019\u0006\u0002CA6\u0003'\u0002\u001d!a\u0003\u0002\u0003\u0015D\u0001\"a\u001c\u0002T\u0001\u0007\u0011\u0011O\u0001\u0002MB9A\"a\u0006\u0002\u001c\u0005M\u0004CBA\u000f\u0003G\t\u0019\u0007C\u0004\u0002x\u0001!\t%!\u001f\u0002\u001bQ\u0014\u0018M\\:g_Jlw+\u001b;i+\u0011\tY(a!\u0015\t\u0005u\u0014q\u0011\u000b\u0005\u0003\u007f\n)\tE\u0003[\u0003?\n\t\tE\u0002\u0017\u0003\u0007#q!a\u001a\u0002v\t\u0007\u0011\u0004\u0003\u0005\u0002l\u0005U\u00049AA\u0006\u0011!\ty'!\u001eA\u0002\u0005%\u0005c\u0002\u0007\u0002\u0018\u0005m\u0011q\u0010\u0015\u0004\u0001\u00055\u0005\u0003BAH\u0003+k!!!%\u000b\u0007\u0005M%!\u0001\u0006b]:|G/\u0019;j_:LA!a&\u0002\u0012\naA)\u001a<fY>\u0004XM]!qS\u0002"
)
public class SimpleFutureAction<T> implements FutureAction<T> {
   private final JobWaiter<?> jobWaiter;
   public final Function0<T> org$apache$spark$SimpleFutureAction$$resultFunc;
   private volatile boolean _cancelled;

   public T get() throws SparkException {
      return (T)FutureAction$class.get(this);
   }

   public <U> void onSuccess(PartialFunction<T, U> pf, ExecutionContext executor) {
      Future$class.onSuccess(this, pf, executor);
   }

   public <U> void onFailure(PartialFunction<Throwable, U> pf, ExecutionContext executor) {
      Future$class.onFailure(this, pf, executor);
   }

   public Future<Throwable> failed() {
      return Future$class.failed(this);
   }

   public <U> void foreach(Function1<T, U> f, ExecutionContext executor) {
      Future$class.foreach(this, f, executor);
   }

   public <S> Future<S> transform(Function1<T, S> s, Function1<Throwable, Throwable> f, ExecutionContext executor) {
      return Future$class.transform(this, s, f, executor);
   }

   public <S> Future<S> map(Function1<T, S> f, ExecutionContext executor) {
      return Future$class.map(this, f, executor);
   }

   public <S> Future<S> flatMap(Function1<T, Future<S>> f, ExecutionContext executor) {
      return Future$class.flatMap(this, f, executor);
   }

   public Future<T> filter(Function1<T, Object> p, ExecutionContext executor) {
      return Future$class.filter(this, p, executor);
   }

   public final Future<T> withFilter(Function1<T, Object> p, ExecutionContext executor) {
      return Future$class.withFilter(this, p, executor);
   }

   public <S> Future<S> collect(PartialFunction<T, S> pf, ExecutionContext executor) {
      return Future$class.collect(this, pf, executor);
   }

   public <U> Future<U> recover(PartialFunction<Throwable, U> pf, ExecutionContext executor) {
      return Future$class.recover(this, pf, executor);
   }

   public <U> Future<U> recoverWith(PartialFunction<Throwable, Future<U>> pf, ExecutionContext executor) {
      return Future$class.recoverWith(this, pf, executor);
   }

   public <U> Future<Tuple2<T, U>> zip(Future<U> that) {
      return Future$class.zip(this, that);
   }

   public <U> Future<U> fallbackTo(Future<U> that) {
      return Future$class.fallbackTo(this, that);
   }

   public <S> Future<S> mapTo(ClassTag<S> tag) {
      return Future$class.mapTo(this, tag);
   }

   public <U> Future<T> andThen(PartialFunction<Try<T>, U> pf, ExecutionContext executor) {
      return Future$class.andThen(this, pf, executor);
   }

   private boolean _cancelled() {
      return this._cancelled;
   }

   private void _cancelled_$eq(boolean x$1) {
      this._cancelled = x$1;
   }

   public void cancel() {
      this._cancelled_$eq(true);
      this.jobWaiter.cancel();
   }

   public SimpleFutureAction<T> ready(Duration atMost, CanAwait permit) {
      this.jobWaiter.completionFuture().ready(atMost, permit);
      return this;
   }

   public T result(Duration atMost, CanAwait permit) throws Exception {
      this.jobWaiter.completionFuture().ready(atMost, permit);
      Predef$.MODULE$.assert(this.value().isDefined(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Future has not completed properly";
         }
      });
      return (T)((Try)this.value().get()).get();
   }

   public <U> void onComplete(Function1<Try<T>, U> func, ExecutionContext executor) {
      this.jobWaiter.completionFuture().onComplete(new Serializable(this, func) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SimpleFutureAction $outer;
         private final Function1 func$1;

         public final U apply(Try<BoxedUnit> x$1) {
            return (U)this.func$1.apply(this.$outer.value().get());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.func$1 = func$1;
            }
         }
      }, executor);
   }

   public boolean isCompleted() {
      return this.jobWaiter.jobFinished();
   }

   public boolean isCancelled() {
      return this._cancelled();
   }

   public Option<Try<T>> value() {
      return this.jobWaiter.completionFuture().value().<Try<T>>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SimpleFutureAction $outer;

         public final Try<T> apply(Try<BoxedUnit> res) {
            return res.<T>map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final T apply(BoxedUnit x$2) {
                  return this.$outer.org$apache$spark$SimpleFutureAction$$anonfun$$$outer().org$apache$spark$SimpleFutureAction$$resultFunc.apply();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         // $FF: synthetic method
         public SimpleFutureAction org$apache$spark$SimpleFutureAction$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public Seq<Object> jobIds() {
      return (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{this.jobWaiter.jobId()}));
   }

   public <S> Future<S> transform(Function1<Try<T>, Try<S>> f, ExecutionContext e) {
      return FutureAction$.MODULE$.transform(this.jobWaiter.completionFuture(), new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SimpleFutureAction $outer;
         private final Function1 f$1;

         public final Try<S> apply(Try<BoxedUnit> u) {
            return (Try)this.f$1.apply(u.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final T apply(BoxedUnit x$3) {
                  return this.$outer.org$apache$spark$SimpleFutureAction$$anonfun$$$outer().org$apache$spark$SimpleFutureAction$$resultFunc.apply();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }));
         }

         // $FF: synthetic method
         public SimpleFutureAction org$apache$spark$SimpleFutureAction$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.f$1 = f$1;
            }
         }
      }, e);
   }

   public <S> Future<S> transformWith(Function1<Try<T>, Future<S>> f, ExecutionContext e) {
      return FutureAction$.MODULE$.transformWith(this.jobWaiter.completionFuture(), new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SimpleFutureAction $outer;
         private final Function1 f$2;

         public final Future<S> apply(Try<BoxedUnit> u) {
            return (Future)this.f$2.apply(u.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final T apply(BoxedUnit x$4) {
                  return this.$outer.org$apache$spark$SimpleFutureAction$$anonfun$$$outer().org$apache$spark$SimpleFutureAction$$resultFunc.apply();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }));
         }

         // $FF: synthetic method
         public SimpleFutureAction org$apache$spark$SimpleFutureAction$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.f$2 = f$2;
            }
         }
      }, e);
   }

   public SimpleFutureAction(JobWaiter<?> jobWaiter, Function0<T> resultFunc) {
      this.jobWaiter = jobWaiter;
      this.org$apache$spark$SimpleFutureAction$$resultFunc = resultFunc;
      Future$class.$init$(this);
      FutureAction$class.$init$(this);
      this._cancelled = false;
   }
}
