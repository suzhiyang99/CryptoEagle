package org.apache.spark;

import java.util.concurrent.TimeoutException;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.rdd.RDD;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Nil$;
import scala.concurrent.CanAwait;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Future$class;
import scala.concurrent.Promise;
import scala.concurrent.Promise$;
import scala.concurrent.duration.Duration;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.util.Try;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Eh!B\u0001\u0003\u0001%\t$aE\"p[BdW\r\u001f$viV\u0014X-Q2uS>t'BA\u0002\u0005\u0003\u0015\u0019\b/\u0019:l\u0015\t)a!\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u000f\u0005\u0019qN]4\u0004\u0001U\u0011!bF\n\u0004\u0001-\t\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\rE\u0002\u0013'Ui\u0011AA\u0005\u0003)\t\u0011ABR;ukJ,\u0017i\u0019;j_:\u0004\"AF\f\r\u0001\u0011)\u0001\u0004\u0001b\u00013\t\tA+\u0005\u0002\u001b;A\u0011AbG\u0005\u000395\u0011qAT8uQ&tw\r\u0005\u0002\r=%\u0011q$\u0004\u0002\u0004\u0003:L\b\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002\u0007I,h\u000e\u0005\u0003\rG\u0015B\u0013B\u0001\u0013\u000e\u0005%1UO\\2uS>t\u0017\u0007\u0005\u0002\u0013M%\u0011qE\u0001\u0002\r\u0015>\u00147+\u001e2nSR$XM\u001d\t\u0004S1*R\"\u0001\u0016\u000b\u0005-j\u0011AC2p]\u000e,(O]3oi&\u0011QF\u000b\u0002\u0007\rV$XO]3\t\u000b=\u0002A\u0011\u0001\u0019\u0002\rqJg.\u001b;?)\t\t$\u0007E\u0002\u0013\u0001UAQ!\t\u0018A\u0002\tBq\u0001\u000e\u0001A\u0002\u0013%Q'\u0001\u0006`G\u0006t7-\u001a7mK\u0012,\u0012A\u000e\t\u0003\u0019]J!\u0001O\u0007\u0003\u000f\t{w\u000e\\3b]\"9!\b\u0001a\u0001\n\u0013Y\u0014AD0dC:\u001cW\r\u001c7fI~#S-\u001d\u000b\u0003y}\u0002\"\u0001D\u001f\n\u0005yj!\u0001B+oSRDq\u0001Q\u001d\u0002\u0002\u0003\u0007a'A\u0002yIEBaA\u0011\u0001!B\u00131\u0014aC0dC:\u001cW\r\u001c7fI\u0002B#!\u0011#\u0011\u00051)\u0015B\u0001$\u000e\u0005!1x\u000e\\1uS2,\u0007b\u0002%\u0001\u0001\u0004%I!S\u0001\u000bgV\u0014\u0017i\u0019;j_:\u001cX#\u0001&\u0011\u0007-\u001bfK\u0004\u0002M#:\u0011Q\nU\u0007\u0002\u001d*\u0011q\nC\u0001\u0007yI|w\u000e\u001e \n\u00039I!AU\u0007\u0002\u000fA\f7m[1hK&\u0011A+\u0016\u0002\u0005\u0019&\u001cHO\u0003\u0002S\u001bA\u0012q+\u0017\t\u0004%MA\u0006C\u0001\fZ\t%Q6,!A\u0001\u0002\u000b\u0005\u0011DA\u0002`IaBa\u0001\u0018\u0001!B\u0013i\u0016aC:vE\u0006\u001bG/[8og\u0002\u00022aS*_a\ty\u0016\rE\u0002\u0013'\u0001\u0004\"AF1\u0005\u0013i[\u0016\u0011!A\u0001\u0006\u0003I\u0002FA.E\u0011\u001d!\u0007\u00011A\u0005\n\u0015\fab];c\u0003\u000e$\u0018n\u001c8t?\u0012*\u0017\u000f\u0006\u0002=M\"9\u0001iYA\u0001\u0002\u00049\u0007cA&TQB\u0012\u0011n\u001b\t\u0004%MQ\u0007C\u0001\fl\t%Q6,!A\u0001\u0002\u000b\u0005\u0011\u0004C\u0004n\u0001\t\u0007I\u0011\u00028\u0002\u0003A,\u0012a\u001c\t\u0004SA,\u0012BA9+\u0005\u001d\u0001&o\\7jg\u0016Daa\u001d\u0001!\u0002\u0013y\u0017A\u00019!\u0011\u0015)\b\u0001\"\u0011w\u0003\u0019\u0019\u0017M\\2fYR\tA\bC\u0003y\u0001\u0011%\u00110\u0001\u0007k_\n\u001cVOY7jiR,'/F\u0001{%\rY8\"\n\u0004\u0005y^\u0004!P\u0001\u0007=e\u00164\u0017N\\3nK:$h\bC\u0003\u007f\u0001\u0011\u0005S'A\u0006jg\u000e\u000bgnY3mY\u0016$\u0007bBA\u0001\u0001\u0011\u0005\u00131A\u0001\u0006e\u0016\fG-\u001f\u000b\u0005\u0003\u000b\t\u0019\u0002\u0006\u0003\u0002\b\u0005%Q\"\u0001\u0001\t\u000f\u0005-q\u0010q\u0001\u0002\u000e\u00051\u0001/\u001a:nSR\u00042!KA\b\u0013\r\t\tB\u000b\u0002\t\u0007\u0006t\u0017i^1ji\"9\u0011QC@A\u0002\u0005]\u0011AB1u\u001b>\u001cH\u000f\u0005\u0003\u0002\u001a\u0005}QBAA\u000e\u0015\r\tiBK\u0001\tIV\u0014\u0018\r^5p]&!\u0011\u0011EA\u000e\u0005!!UO]1uS>t\u0007&B@\u0002&\u0005m\u0002#\u0002\u0007\u0002(\u0005-\u0012bAA\u0015\u001b\t1A\u000f\u001b:poN\u0004B!!\f\u000269!\u0011qFA\u001a\u001d\ra\u0015\u0011G\u0005\u0003W5I!A\u0015\u0016\n\t\u0005]\u0012\u0011\b\u0002\u0011)&lWm\\;u\u000bb\u001cW\r\u001d;j_:T!A\u0015\u0016$\u0005\u0005-\u0002&B@\u0002@\u0005\u001d\u0003#\u0002\u0007\u0002(\u0005\u0005\u0003cA&\u0002D%\u0019\u0011QI+\u0003)%sG/\u001a:skB$X\rZ#yG\u0016\u0004H/[8oG\t\t\t\u0005C\u0004\u0002L\u0001!\t%!\u0014\u0002\rI,7/\u001e7u)\u0011\ty%a\u0015\u0015\u0007U\t\t\u0006\u0003\u0005\u0002\f\u0005%\u00039AA\u0007\u0011!\t)\"!\u0013A\u0002\u0005]\u0001FBA%\u0003/\ny\u0006E\u0003\r\u0003O\tI\u0006E\u0002L\u00037J1!!\u0018V\u0005%)\u0005pY3qi&|gn\t\u0002\u0002Z!9\u00111\r\u0001\u0005B\u0005\u0015\u0014AC8o\u0007>l\u0007\u000f\\3uKV!\u0011qMAE)\u0011\tI'!\u001e\u0015\u0007q\nY\u0007\u0003\u0005\u0002n\u0005\u0005\u00049AA8\u0003!)\u00070Z2vi>\u0014\bcA\u0015\u0002r%\u0019\u00111\u000f\u0016\u0003!\u0015CXmY;uS>t7i\u001c8uKb$\b\u0002CA<\u0003C\u0002\r!!\u001f\u0002\t\u0019,hn\u0019\t\u0007\u0019\r\nY(a\"\u0011\u000b\u0005u\u00141Q\u000b\u000e\u0005\u0005}$bAAA\u001b\u0005!Q\u000f^5m\u0013\u0011\t))a \u0003\u0007Q\u0013\u0018\u0010E\u0002\u0017\u0003\u0013#q!a#\u0002b\t\u0007\u0011DA\u0001V\u0011\u0019\ty\t\u0001C!k\u0005Y\u0011n]\"p[BdW\r^3e\u0011\u001d\t\u0019\n\u0001C!\u0003+\u000bQA^1mk\u0016,\"!a&\u0011\u000b1\tI*a\u001f\n\u0007\u0005mUB\u0001\u0004PaRLwN\u001c\u0005\b\u0003?\u0003A\u0011AAQ\u0003\u0019QwNY%egV\u0011\u00111\u0015\t\u0006\u0017\u0006\u0015\u0016\u0011V\u0005\u0004\u0003O+&aA*fcB\u0019A\"a+\n\u0007\u00055VBA\u0002J]RDq!!-\u0001\t\u0003\n\u0019,A\u0005ue\u0006t7OZ8s[V!\u0011QWA_)\u0011\t9,!2\u0015\t\u0005e\u0016\u0011\u0019\t\u0005S1\nY\fE\u0002\u0017\u0003{#q!a0\u00020\n\u0007\u0011DA\u0001T\u0011!\t\u0019-a,A\u0004\u0005=\u0014!A3\t\u0011\u0005\u001d\u0017q\u0016a\u0001\u0003\u0013\f\u0011A\u001a\t\u0007\u0019\r\nY(a3\u0011\r\u0005u\u00141QA^\u0011\u001d\ty\r\u0001C!\u0003#\fQ\u0002\u001e:b]N4wN]7XSRDW\u0003BAj\u00037$B!!6\u0002`R!\u0011q[Ao!\u0011IC&!7\u0011\u0007Y\tY\u000eB\u0004\u0002@\u00065'\u0019A\r\t\u0011\u0005\r\u0017Q\u001aa\u0002\u0003_B\u0001\"a2\u0002N\u0002\u0007\u0011\u0011\u001d\t\u0007\u0019\r\nY(a6)\u0007\u0001\t)\u000f\u0005\u0003\u0002h\u00065XBAAu\u0015\r\tYOA\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002BAx\u0003S\u0014A\u0002R3wK2|\u0007/\u001a:Ba&\u0004"
)
public class ComplexFutureAction<T> implements FutureAction<T> {
   private volatile boolean _cancelled;
   private volatile List<FutureAction<?>> org$apache$spark$ComplexFutureAction$$subActions;
   private final Promise<T> p;

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

   public List<FutureAction<?>> org$apache$spark$ComplexFutureAction$$subActions() {
      return this.org$apache$spark$ComplexFutureAction$$subActions;
   }

   public void org$apache$spark$ComplexFutureAction$$subActions_$eq(List<FutureAction<?>> x$1) {
      this.org$apache$spark$ComplexFutureAction$$subActions = x$1;
   }

   private Promise<T> p() {
      return this.p;
   }

   public synchronized void cancel() {
      this._cancelled_$eq(true);
      this.p().tryFailure(new SparkException("Action has been cancelled"));
      this.org$apache$spark$ComplexFutureAction$$subActions().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(FutureAction<?> x$5) {
            x$5.cancel();
         }
      });
   }

   private JobSubmitter jobSubmitter() {
      return new JobSubmitter(this) {
         // $FF: synthetic field
         private final ComplexFutureAction $outer;

         public <T, U, R> FutureAction<R> submitJob(RDD<T> rdd, Function1<Iterator<T>, U> processPartition, Seq<Object> partitions, Function2<Object, U, BoxedUnit> resultHandler, Function0<R> resultFunc) {
            SimpleFutureAction var7;
            synchronized(this.$outer) {
               if (this.$outer.isCancelled()) {
                  throw new SparkException("Action has been cancelled");
               }

               SimpleFutureAction job = rdd.context().submitJob(rdd, processPartition, partitions, resultHandler, resultFunc);
               this.$outer.org$apache$spark$ComplexFutureAction$$subActions_$eq(this.$outer.org$apache$spark$ComplexFutureAction$$subActions().$colon$colon(job));
               var7 = job;
            }

            return var7;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
   }

   public boolean isCancelled() {
      return this._cancelled();
   }

   public ComplexFutureAction<T> ready(Duration atMost, CanAwait permit) throws InterruptedException, TimeoutException {
      this.p().future().ready(atMost, permit);
      return this;
   }

   public T result(Duration atMost, CanAwait permit) throws Exception {
      return (T)this.p().future().result(atMost, permit);
   }

   public <U> void onComplete(Function1<Try<T>, U> func, ExecutionContext executor) {
      this.p().future().onComplete(func, executor);
   }

   public boolean isCompleted() {
      return this.p().isCompleted();
   }

   public Option<Try<T>> value() {
      return this.p().future().value();
   }

   public Seq<Object> jobIds() {
      return (Seq)this.org$apache$spark$ComplexFutureAction$$subActions().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Object> apply(FutureAction<?> x$7) {
            return x$7.jobIds();
         }
      }, List$.MODULE$.canBuildFrom());
   }

   public <S> Future<S> transform(Function1<Try<T>, Try<S>> f, ExecutionContext e) {
      return FutureAction$.MODULE$.transform(this.p().future(), f, e);
   }

   public <S> Future<S> transformWith(Function1<Try<T>, Future<S>> f, ExecutionContext e) {
      return FutureAction$.MODULE$.transformWith(this.p().future(), f, e);
   }

   public ComplexFutureAction(Function1<JobSubmitter, Future<T>> run) {
      Future$class.$init$(this);
      FutureAction$class.$init$(this);
      this._cancelled = false;
      this.org$apache$spark$ComplexFutureAction$$subActions = Nil$.MODULE$;
      this.p = Promise$.MODULE$.<T>apply().tryCompleteWith((Future)run.apply(this.jobSubmitter()));
   }
}
