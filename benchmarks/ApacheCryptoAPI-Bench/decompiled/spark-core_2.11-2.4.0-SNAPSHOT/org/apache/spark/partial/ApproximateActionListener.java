package org.apache.spark.partial;

import org.apache.spark.TaskContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.scheduler.JobListener;
import scala.Function2;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mb!B\u0001\u0003\u0001\u0011Q!!G!qaJ|\u00070[7bi\u0016\f5\r^5p]2K7\u000f^3oKJT!a\u0001\u0003\u0002\u000fA\f'\u000f^5bY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x-\u0006\u0003\fE\u0005S5c\u0001\u0001\r%A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"a\u0005\f\u000e\u0003QQ!!\u0006\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014\u0018BA\f\u0015\u0005-QuN\u0019'jgR,g.\u001a:\t\u0011e\u0001!\u0011!Q\u0001\nm\t1A\u001d3e\u0007\u0001\u00012\u0001\b\u0010!\u001b\u0005i\"BA\r\u0005\u0013\tyRDA\u0002S\t\u0012\u0003\"!\t\u0012\r\u0001\u0011)1\u0005\u0001b\u0001I\t\tA+\u0005\u0002&QA\u0011QBJ\u0005\u0003O9\u0011qAT8uQ&tw\r\u0005\u0002\u000eS%\u0011!F\u0004\u0002\u0004\u0003:L\b\u0002\u0003\u0017\u0001\u0005\u0003\u0005\u000b\u0011B\u0017\u0002\t\u0019,hn\u0019\t\u0006\u001b9\u0002D\u0007Q\u0005\u0003_9\u0011\u0011BR;oGRLwN\u001c\u001a\u0011\u0005E\u0012T\"\u0001\u0003\n\u0005M\"!a\u0003+bg.\u001cuN\u001c;fqR\u00042!N\u001f!\u001d\t14H\u0004\u00028u5\t\u0001H\u0003\u0002:5\u00051AH]8pizJ\u0011aD\u0005\u0003y9\tq\u0001]1dW\u0006<W-\u0003\u0002?\u007f\tA\u0011\n^3sCR|'O\u0003\u0002=\u001dA\u0011\u0011%\u0011\u0003\u0006\u0005\u0002\u0011\r\u0001\n\u0002\u0002+\"AA\t\u0001B\u0001B\u0003%Q)A\u0005fm\u0006dW/\u0019;peB!ai\u0012!J\u001b\u0005\u0011\u0011B\u0001%\u0003\u0005Q\t\u0005\u000f\u001d:pq&l\u0017\r^3Fm\u0006dW/\u0019;peB\u0011\u0011E\u0013\u0003\u0006\u0017\u0002\u0011\r\u0001\n\u0002\u0002%\"AQ\n\u0001B\u0001B\u0003%a*A\u0004uS6,w.\u001e;\u0011\u00055y\u0015B\u0001)\u000f\u0005\u0011auN\\4\t\u000bI\u0003A\u0011A*\u0002\rqJg.\u001b;?)\u0015!VKV,Y!\u00151\u0005\u0001\t!J\u0011\u0015I\u0012\u000b1\u0001\u001c\u0011\u0015a\u0013\u000b1\u0001.\u0011\u0015!\u0015\u000b1\u0001F\u0011\u0015i\u0015\u000b1\u0001O\u0011\u001dQ\u0006A1A\u0005\u0002m\u000b\u0011b\u001d;beR$\u0016.\\3\u0016\u00039Ca!\u0018\u0001!\u0002\u0013q\u0015AC:uCJ$H+[7fA!9q\f\u0001b\u0001\n\u0003\u0001\u0017A\u0003;pi\u0006dG+Y:lgV\t\u0011\r\u0005\u0002\u000eE&\u00111M\u0004\u0002\u0004\u0013:$\bBB3\u0001A\u0003%\u0011-A\u0006u_R\fG\u000eV1tWN\u0004\u0003bB4\u0001\u0001\u0004%\t\u0001Y\u0001\u000eM&t\u0017n\u001d5fIR\u000b7o[:\t\u000f%\u0004\u0001\u0019!C\u0001U\u0006\tb-\u001b8jg\",G\rV1tWN|F%Z9\u0015\u0005-t\u0007CA\u0007m\u0013\tigB\u0001\u0003V]&$\bbB8i\u0003\u0003\u0005\r!Y\u0001\u0004q\u0012\n\u0004BB9\u0001A\u0003&\u0011-\u0001\bgS:L7\u000f[3e)\u0006\u001c8n\u001d\u0011\t\u000fM\u0004\u0001\u0019!C\u0001i\u00069a-Y5mkJ,W#A;\u0011\u000751\b0\u0003\u0002x\u001d\t1q\n\u001d;j_:\u0004\"!N=\n\u0005i|$!C#yG\u0016\u0004H/[8o\u0011\u001da\b\u00011A\u0005\u0002u\f1BZ1jYV\u0014Xm\u0018\u0013fcR\u00111N \u0005\b_n\f\t\u00111\u0001v\u0011\u001d\t\t\u0001\u0001Q!\nU\f\u0001BZ1jYV\u0014X\r\t\u0005\n\u0003\u000b\u0001\u0001\u0019!C\u0001\u0003\u000f\tAB]3tk2$xJ\u00196fGR,\"!!\u0003\u0011\t51\u00181\u0002\t\u0005\r\u00065\u0011*C\u0002\u0002\u0010\t\u0011Q\u0002U1si&\fGNU3tk2$\b\"CA\n\u0001\u0001\u0007I\u0011AA\u000b\u0003A\u0011Xm];mi>\u0013'.Z2u?\u0012*\u0017\u000fF\u0002l\u0003/A\u0011b\\A\t\u0003\u0003\u0005\r!!\u0003\t\u0011\u0005m\u0001\u0001)Q\u0005\u0003\u0013\tQB]3tk2$xJ\u00196fGR\u0004\u0003bBA\u0010\u0001\u0011\u0005\u0013\u0011E\u0001\u000ei\u0006\u001c8nU;dG\u0016,G-\u001a3\u0015\u000b-\f\u0019#a\n\t\u000f\u0005\u0015\u0012Q\u0004a\u0001C\u0006)\u0011N\u001c3fq\"9\u0011\u0011FA\u000f\u0001\u0004A\u0013A\u0002:fgVdG\u000fC\u0004\u0002.\u0001!\t%a\f\u0002\u0013)|'MR1jY\u0016$GcA6\u00022!9\u00111GA\u0016\u0001\u0004A\u0018!C3yG\u0016\u0004H/[8o\u0011\u001d\t9\u0004\u0001C\u0001\u0003s\t1\"Y<bSR\u0014Vm];miR\u0011\u00111\u0002"
)
public class ApproximateActionListener<T, U, R> implements JobListener {
   public final ApproximateEvaluator<U, R> org$apache$spark$partial$ApproximateActionListener$$evaluator;
   private final long timeout;
   private final long startTime;
   private final int totalTasks;
   private int finishedTasks;
   private Option<Exception> failure;
   private Option<PartialResult<R>> resultObject;

   public long startTime() {
      return this.startTime;
   }

   public int totalTasks() {
      return this.totalTasks;
   }

   public int finishedTasks() {
      return this.finishedTasks;
   }

   public void finishedTasks_$eq(int x$1) {
      this.finishedTasks = x$1;
   }

   public Option<Exception> failure() {
      return this.failure;
   }

   public void failure_$eq(Option<Exception> x$1) {
      this.failure = x$1;
   }

   public Option<PartialResult<R>> resultObject() {
      return this.resultObject;
   }

   public void resultObject_$eq(Option<PartialResult<R>> x$1) {
      this.resultObject = x$1;
   }

   public synchronized void taskSucceeded(int index, Object result) {
      this.org$apache$spark$partial$ApproximateActionListener$$evaluator.merge(index, result);
      this.finishedTasks_$eq(this.finishedTasks() + 1);
      if (this.finishedTasks() == this.totalTasks()) {
         this.resultObject().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ApproximateActionListener $outer;

            public final void apply(PartialResult<R> r) {
               r.setFinalValue(this.$outer.org$apache$spark$partial$ApproximateActionListener$$evaluator.currentResult());
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.notifyAll();
      }

   }

   public synchronized void jobFailed(Exception exception) {
      this.failure_$eq(new Some(exception));
      this.notifyAll();
   }

   public synchronized PartialResult<R> awaitResult() {
      long finishTime = this.startTime() + this.timeout;

      while(true) {
         long time = System.currentTimeMillis();
         if (this.failure().isDefined()) {
            throw (Throwable)this.failure().get();
         }

         if (this.finishedTasks() == this.totalTasks()) {
            return new PartialResult<R>(this.org$apache$spark$partial$ApproximateActionListener$$evaluator.currentResult(), true);
         }

         if (time >= finishTime) {
            this.resultObject_$eq(new Some(new PartialResult(this.org$apache$spark$partial$ApproximateActionListener$$evaluator.currentResult(), false)));
            return (PartialResult)this.resultObject().get();
         }

         this.wait(finishTime - time);
      }
   }

   public ApproximateActionListener(RDD<T> rdd, Function2<TaskContext, Iterator<T>, U> func, ApproximateEvaluator<U, R> evaluator, long timeout) {
      this.org$apache$spark$partial$ApproximateActionListener$$evaluator = evaluator;
      this.timeout = timeout;
      this.startTime = System.currentTimeMillis();
      this.totalTasks = rdd.partitions().length;
      this.finishedTasks = 0;
      this.failure = None$.MODULE$;
      this.resultObject = None$.MODULE$;
   }
}
