package org.apache.spark.partial;

import org.apache.spark.util.collection.OpenHashMap;
import org.apache.spark.util.collection.OpenHashMap$mcJ$sp;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterable$;
import scala.collection.Map;
import scala.collection.TraversableOnce;
import scala.collection.mutable.HashMap;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-4Q!\u0001\u0002\u0001\t)\u0011Qc\u0012:pkB,GmQ8v]R,e/\u00197vCR|'O\u0003\u0002\u0004\t\u00059\u0001/\u0019:uS\u0006d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0016\u0005-\u00013c\u0001\u0001\r%A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004Ba\u0005\u000b\u0017[5\t!!\u0003\u0002\u0016\u0005\t!\u0012\t\u001d9s_bLW.\u0019;f\u000bZ\fG.^1u_J\u0004Ba\u0006\u000f\u001fU5\t\u0001D\u0003\u0002\u001a5\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\u000b\u0005m!\u0011\u0001B;uS2L!!\b\r\u0003\u0017=\u0003XM\u001c%bg\"l\u0015\r\u001d\t\u0003?\u0001b\u0001\u0001B\u0003\"\u0001\t\u00071EA\u0001U\u0007\u0001\t\"\u0001J\u0014\u0011\u00055)\u0013B\u0001\u0014\u000f\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"!\u0004\u0015\n\u0005%r!aA!osB\u0011QbK\u0005\u0003Y9\u0011A\u0001T8oOB!a\u0006\r\u00103\u001b\u0005y#BA\r\u000f\u0013\t\ttFA\u0002NCB\u0004\"aE\u001a\n\u0005Q\u0012!!\u0004\"pk:$W\r\u001a#pk\ndW\r\u0003\u00057\u0001\t\u0005\t\u0015!\u00038\u00031!x\u000e^1m\u001fV$\b/\u001e;t!\ti\u0001(\u0003\u0002:\u001d\t\u0019\u0011J\u001c;\t\u0011m\u0002!\u0011!Q\u0001\nq\n!bY8oM&$WM\\2f!\tiQ(\u0003\u0002?\u001d\t1Ai\\;cY\u0016D\u0001\u0002\u0011\u0001\u0003\u0004\u0003\u0006Y!Q\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004c\u0001\"F=5\t1I\u0003\u0002E\u001d\u00059!/\u001a4mK\u000e$\u0018B\u0001$D\u0005!\u0019E.Y:t)\u0006<\u0007\"\u0002%\u0001\t\u0003I\u0015A\u0002\u001fj]&$h\bF\u0002K\u001b:#\"a\u0013'\u0011\u0007M\u0001a\u0004C\u0003A\u000f\u0002\u000f\u0011\tC\u00037\u000f\u0002\u0007q\u0007C\u0003<\u000f\u0002\u0007A\bC\u0004Q\u0001\u0001\u0007I\u0011B)\u0002\u001b=,H\u000f];ug6+'oZ3e+\u00059\u0004bB*\u0001\u0001\u0004%I\u0001V\u0001\u0012_V$\b/\u001e;t\u001b\u0016\u0014x-\u001a3`I\u0015\fHCA+Y!\tia+\u0003\u0002X\u001d\t!QK\\5u\u0011\u001dI&+!AA\u0002]\n1\u0001\u001f\u00132\u0011\u0019Y\u0006\u0001)Q\u0005o\u0005qq.\u001e;qkR\u001cX*\u001a:hK\u0012\u0004\u0003bB/\u0001\u0005\u0004%IAX\u0001\u0005gVl7/F\u0001\u0017\u0011\u0019\u0001\u0007\u0001)A\u0005-\u0005)1/^7tA!)!\r\u0001C!G\u0006)Q.\u001a:hKR\u0019Q\u000b\u001a4\t\u000b\u0015\f\u0007\u0019A\u001c\u0002\u0011=,H\u000f];u\u0013\u0012DQaZ1A\u0002Y\t!\u0002^1tWJ+7/\u001e7u\u0011\u0015I\u0007\u0001\"\u0011k\u00035\u0019WO\u001d:f]R\u0014Vm];miR\tQ\u0006"
)
public class GroupedCountEvaluator<T> implements ApproximateEvaluator<OpenHashMap<T, Object>, Map<T, BoundedDouble>> {
   private final int totalOutputs;
   public final double org$apache$spark$partial$GroupedCountEvaluator$$confidence;
   private int outputsMerged;
   private final OpenHashMap<T, Object> org$apache$spark$partial$GroupedCountEvaluator$$sums;

   private int outputsMerged() {
      return this.outputsMerged;
   }

   private void outputsMerged_$eq(int x$1) {
      this.outputsMerged = x$1;
   }

   public OpenHashMap<T, Object> org$apache$spark$partial$GroupedCountEvaluator$$sums() {
      return this.org$apache$spark$partial$GroupedCountEvaluator$$sums;
   }

   public void merge(int outputId, OpenHashMap<T, Object> taskResult) {
      this.outputsMerged_$eq(this.outputsMerged() + 1);
      taskResult.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final GroupedCountEvaluator $outer;

         public final long apply(Tuple2<T, Object> x0$1) {
            if (x0$1 != null) {
               Object key = x0$1._1();
               long value = x0$1._2$mcJ$sp();
               long var6 = this.$outer.org$apache$spark$partial$GroupedCountEvaluator$$sums().changeValue$mcJ$sp(key, new Serializable(this, value) {
                  public static final long serialVersionUID = 0L;
                  private final long value$1;

                  public final long apply() {
                     return this.apply$mcJ$sp();
                  }

                  public long apply$mcJ$sp() {
                     return this.value$1;
                  }

                  public {
                     this.value$1 = value$1;
                  }
               }, new Serializable(this, value) {
                  public static final long serialVersionUID = 0L;
                  private final long value$1;

                  public final long apply(long x$1) {
                     return this.apply$mcJJ$sp(x$1);
                  }

                  public long apply$mcJJ$sp(long x$1) {
                     return x$1 + this.value$1;
                  }

                  public {
                     this.value$1 = value$1;
                  }
               });
               return var6;
            } else {
               throw new MatchError(x0$1);
            }
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

   public Map<T, BoundedDouble> currentResult() {
      Object var10000;
      if (this.outputsMerged() == this.totalOutputs) {
         var10000 = ((TraversableOnce)this.org$apache$spark$partial$GroupedCountEvaluator$$sums().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Tuple2<T, BoundedDouble> apply(Tuple2<T, Object> x0$2) {
               if (x0$2 != null) {
                  Object key = x0$2._1();
                  long sum = x0$2._2$mcJ$sp();
                  Tuple2 var6 = new Tuple2(key, new BoundedDouble((double)sum, (double)1.0F, (double)sum, (double)sum));
                  return var6;
               } else {
                  throw new MatchError(x0$2);
               }
            }
         }, Iterable$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
      } else if (this.outputsMerged() == 0) {
         var10000 = new HashMap();
      } else {
         double p = (double)this.outputsMerged() / (double)this.totalOutputs;
         var10000 = ((TraversableOnce)this.org$apache$spark$partial$GroupedCountEvaluator$$sums().map(new Serializable(this, p) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final GroupedCountEvaluator $outer;
            private final double p$1;

            public final Tuple2<T, BoundedDouble> apply(Tuple2<T, Object> x0$3) {
               if (x0$3 != null) {
                  Object key = x0$3._1();
                  long sum = x0$3._2$mcJ$sp();
                  Tuple2 var6 = new Tuple2(key, CountEvaluator$.MODULE$.bound(this.$outer.org$apache$spark$partial$GroupedCountEvaluator$$confidence, sum, this.p$1));
                  return var6;
               } else {
                  throw new MatchError(x0$3);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.p$1 = p$1;
               }
            }
         }, Iterable$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
      }

      return (Map<T, BoundedDouble>)var10000;
   }

   public GroupedCountEvaluator(int totalOutputs, double confidence, ClassTag<T> evidence$1) {
      this.totalOutputs = totalOutputs;
      this.org$apache$spark$partial$GroupedCountEvaluator$$confidence = confidence;
      this.outputsMerged = 0;
      this.org$apache$spark$partial$GroupedCountEvaluator$$sums = new OpenHashMap$mcJ$sp(evidence$1, ClassTag$.MODULE$.Long());
   }
}
