package org.apache.spark.rdd;

import java.util.Arrays;
import org.apache.spark.TaskContext;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.partial.BoundedDouble;
import org.apache.spark.partial.MeanEvaluator;
import org.apache.spark.partial.PartialResult;
import org.apache.spark.partial.SumEvaluator;
import org.apache.spark.util.StatCounter;
import org.apache.spark.util.StatCounter$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple2$mcDD$sp;
import scala.collection.IndexedSeq;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.SeqLike;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.List$;
import scala.collection.immutable.Range;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichDouble$;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}a\u0001B\u0001\u0003\u0001-\u0011!\u0003R8vE2,'\u000b\u0012#Gk:\u001cG/[8og*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M!\u0001\u0001\u0004\n\u0019!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u00111CF\u0007\u0002))\u0011Q\u0003B\u0001\tS:$XM\u001d8bY&\u0011q\u0003\u0006\u0002\b\u0019><w-\u001b8h!\ti\u0011$\u0003\u0002\u001b\u001d\ta1+\u001a:jC2L'0\u00192mK\"AA\u0004\u0001B\u0001B\u0003%Q$\u0001\u0003tK24\u0007c\u0001\u0010 C5\t!!\u0003\u0002!\u0005\t\u0019!\u000b\u0012#\u0011\u00055\u0011\u0013BA\u0012\u000f\u0005\u0019!u.\u001e2mK\")Q\u0005\u0001C\u0001M\u00051A(\u001b8jiz\"\"a\n\u0015\u0011\u0005y\u0001\u0001\"\u0002\u000f%\u0001\u0004i\u0002\"\u0002\u0016\u0001\t\u0003Y\u0013aA:v[R\t\u0011\u0005C\u0003.\u0001\u0011\u0005a&A\u0003ti\u0006$8\u000fF\u00010!\t\u00014'D\u00012\u0015\t\u0011D!\u0001\u0003vi&d\u0017B\u0001\u001b2\u0005-\u0019F/\u0019;D_VtG/\u001a:\t\u000bY\u0002A\u0011A\u0016\u0002\t5,\u0017M\u001c\u0005\u0006q\u0001!\taK\u0001\tm\u0006\u0014\u0018.\u00198dK\")!\b\u0001C\u0001W\u0005)1\u000f\u001e3fm\")A\b\u0001C\u0001W\u0005Y1/Y7qY\u0016\u001cF\u000fZ3w\u0011\u0015q\u0004\u0001\"\u0001,\u00039\u0019\u0018-\u001c9mKZ\u000b'/[1oG\u0016DQ\u0001\u0011\u0001\u0005\u0002-\n\u0001\u0002]8q'R$WM\u001e\u0015\u0004\u007f\tC\u0005CA\"G\u001b\u0005!%BA#\u0005\u0003)\tgN\\8uCRLwN\\\u0005\u0003\u000f\u0012\u0013QaU5oG\u0016\f\u0013!S\u0001\u0006e9\nd\u0006\r\u0005\u0006\u0017\u0002!\taK\u0001\fa>\u0004h+\u0019:jC:\u001cW\rK\u0002K\u0005\"CQA\u0014\u0001\u0005\u0002=\u000b!\"\\3b]\u0006\u0003\bO]8y)\r\u0001\u0016L\u0018\t\u0004#R3V\"\u0001*\u000b\u0005M#\u0011a\u00029beRL\u0017\r\\\u0005\u0003+J\u0013Q\u0002U1si&\fGNU3tk2$\bCA)X\u0013\tA&KA\u0007C_VtG-\u001a3E_V\u0014G.\u001a\u0005\u000656\u0003\raW\u0001\bi&lWm\\;u!\tiA,\u0003\u0002^\u001d\t!Aj\u001c8h\u0011\u001dyV\n%AA\u0002\u0005\n!bY8oM&$WM\\2f\u0011\u0015\t\u0007\u0001\"\u0001c\u0003%\u0019X/\\!qaJ|\u0007\u0010F\u0002QG\u0012DQA\u00171A\u0002mCqa\u00181\u0011\u0002\u0003\u0007\u0011\u0005C\u0003g\u0001\u0011\u0005q-A\u0005iSN$xn\u001a:b[R\u0011\u0001n\u001c\t\u0005\u001b%\\g.\u0003\u0002k\u001d\t1A+\u001e9mKJ\u00022!\u00047\"\u0013\tigBA\u0003BeJ\f\u0017\u0010E\u0002\u000eYnCQ\u0001]3A\u0002E\f1BY;dW\u0016$8i\\;oiB\u0011QB]\u0005\u0003g:\u00111!\u00138u\u0011\u00151\u0007\u0001\"\u0001v)\rqg\u000f\u001f\u0005\u0006oR\u0004\ra[\u0001\bEV\u001c7.\u001a;t\u0011\u001dIH\u000f%AA\u0002i\f1\"\u001a<f]\n+8m[3ugB\u0011Qb_\u0005\u0003y:\u0011qAQ8pY\u0016\fg\u000eC\u0004\u007f\u0001E\u0005I\u0011A@\u0002)5,\u0017M\\!qaJ|\u0007\u0010\n3fM\u0006,H\u000e\u001e\u00133+\t\t\tAK\u0002\"\u0003\u0007Y#!!\u0002\u0011\t\u0005\u001d\u0011qB\u0007\u0003\u0003\u0013QA!a\u0003\u0002\u000e\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0003\u000b:IA!!\u0005\u0002\n\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u0011\u0005U\u0001!%A\u0005\u0002}\f1c];n\u0003B\u0004(o\u001c=%I\u00164\u0017-\u001e7uIIB\u0011\"!\u0007\u0001#\u0003%\t!a\u0007\u0002'!L7\u000f^8he\u0006lG\u0005Z3gCVdG\u000f\n\u001a\u0016\u0005\u0005u!f\u0001>\u0002\u0004\u0001"
)
public class DoubleRDDFunctions implements Logging, Serializable {
   public final RDD<Object> org$apache$spark$rdd$DoubleRDDFunctions$$self;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public double sum() {
      return BoxesRunTime.unboxToDouble(this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return BoxesRunTime.unboxToDouble(this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.fold(BoxesRunTime.boxToDouble((double)0.0F), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(double x$1, double x$2) {
                  return this.apply$mcDDD$sp(x$1, x$2);
               }

               public double apply$mcDDD$sp(double x$1, double x$2) {
                  return x$1 + x$2;
               }
            }));
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

   public StatCounter stats() {
      return (StatCounter)this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;

         public final StatCounter apply() {
            return (StatCounter)this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.mapPartitions(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Iterator<StatCounter> apply(Iterator<Object> nums) {
                  return scala.package$.MODULE$.Iterator().<StatCounter>apply(Predef$.MODULE$.wrapRefArray(new StatCounter[]{StatCounter$.MODULE$.apply(nums)}));
               }
            }, this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.mapPartitions$default$2(), ClassTag$.MODULE$.apply(StatCounter.class)).reduce(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final StatCounter apply(StatCounter a, StatCounter b) {
                  return a.merge(b);
               }
            });
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

   public double mean() {
      return BoxesRunTime.unboxToDouble(this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return this.$outer.stats().mean();
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

   public double variance() {
      return BoxesRunTime.unboxToDouble(this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return this.$outer.stats().variance();
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

   public double stdev() {
      return BoxesRunTime.unboxToDouble(this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return this.$outer.stats().stdev();
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

   public double sampleStdev() {
      return BoxesRunTime.unboxToDouble(this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return this.$outer.stats().sampleStdev();
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

   public double sampleVariance() {
      return BoxesRunTime.unboxToDouble(this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return this.$outer.stats().sampleVariance();
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

   public double popStdev() {
      return BoxesRunTime.unboxToDouble(this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return this.$outer.stats().popStdev();
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

   public double popVariance() {
      return BoxesRunTime.unboxToDouble(this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return this.$outer.stats().popVariance();
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

   public PartialResult<BoundedDouble> meanApprox(long timeout, double confidence) {
      return (PartialResult)this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this, timeout, confidence) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;
         private final long timeout$1;
         private final double confidence$1;

         public final PartialResult<BoundedDouble> apply() {
            Function2 processPartition = new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final StatCounter apply(TaskContext ctx, Iterator<Object> ns) {
                  return StatCounter$.MODULE$.apply(ns);
               }
            };
            MeanEvaluator evaluator = new MeanEvaluator(this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.partitions().length, this.confidence$1);
            return this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.context().runApproximateJob(this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self, processPartition, evaluator, this.timeout$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.timeout$1 = timeout$1;
               this.confidence$1 = confidence$1;
            }
         }
      });
   }

   public double meanApprox$default$2() {
      return 0.95;
   }

   public PartialResult<BoundedDouble> sumApprox(long timeout, double confidence) {
      return (PartialResult)this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this, timeout, confidence) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;
         private final long timeout$2;
         private final double confidence$2;

         public final PartialResult<BoundedDouble> apply() {
            Function2 processPartition = new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final StatCounter apply(TaskContext ctx, Iterator<Object> ns) {
                  return StatCounter$.MODULE$.apply(ns);
               }
            };
            SumEvaluator evaluator = new SumEvaluator(this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.partitions().length, this.confidence$2);
            return this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.context().runApproximateJob(this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self, processPartition, evaluator, this.timeout$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.timeout$2 = timeout$2;
               this.confidence$2 = confidence$2;
            }
         }
      });
   }

   public double sumApprox$default$2() {
      return 0.95;
   }

   public Tuple2<double[], long[]> histogram(int bucketCount) {
      return (Tuple2)this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this, bucketCount) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;
         private final int bucketCount$1;

         public final Tuple2<double[], long[]> apply() {
            Tuple2 var2 = (Tuple2)this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.mapPartitions(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Iterator<Tuple2<Object, Object>> apply(Iterator<Object> items) {
                  return scala.package$.MODULE$.Iterator().<Tuple2<Object, Object>>apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{(Tuple2)items.foldRight(new Tuple2$mcDD$sp(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final Tuple2<Object, Object> apply(double e, Tuple2<Object, Object> x) {
                        return new Tuple2$mcDD$sp(RichDouble$.MODULE$.max$extension(Predef$.MODULE$.doubleWrapper(x._1$mcD$sp()), e), RichDouble$.MODULE$.min$extension(Predef$.MODULE$.doubleWrapper(x._2$mcD$sp()), e));
                     }
                  })}));
               }
            }, this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.mapPartitions$default$2(), ClassTag$.MODULE$.apply(Tuple2.class)).reduce(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Tuple2<Object, Object> apply(Tuple2<Object, Object> maxmin1, Tuple2<Object, Object> maxmin2) {
                  return new Tuple2$mcDD$sp(RichDouble$.MODULE$.max$extension(Predef$.MODULE$.doubleWrapper(maxmin1._1$mcD$sp()), maxmin2._1$mcD$sp()), RichDouble$.MODULE$.min$extension(Predef$.MODULE$.doubleWrapper(maxmin1._2$mcD$sp()), maxmin2._2$mcD$sp()));
               }
            });
            if (var2 != null) {
               double max = var2._1$mcD$sp();
               double min = var2._2$mcD$sp();
               Tuple2$mcDD$sp var11 = new Tuple2$mcDD$sp(max, min);
               double max = ((Tuple2)var11)._1$mcD$sp();
               double min = ((Tuple2)var11)._2$mcD$sp();
               if (!Predef$.MODULE$.double2Double(min).isNaN() && !Predef$.MODULE$.double2Double(max).isNaN() && !RichDouble$.MODULE$.isInfinity$extension(Predef$.MODULE$.doubleWrapper(max)) && !RichDouble$.MODULE$.isInfinity$extension(Predef$.MODULE$.doubleWrapper(min))) {
                  Seq range = (Seq)(min != max ? this.customRange$1(min, max, this.bucketCount$1) : List$.MODULE$.apply(Predef$.MODULE$.wrapDoubleArray(new double[]{min, min})));
                  double[] buckets = (double[])range.toArray(ClassTag$.MODULE$.Double());
                  return new Tuple2<double[], long[]>(buckets, this.$outer.histogram(buckets, true));
               } else {
                  throw new UnsupportedOperationException("Histogram on either an empty RDD or RDD containing +/-infinity or NaN");
               }
            } else {
               throw new MatchError(var2);
            }
         }

         private final IndexedSeq customRange$1(double min, double max, int steps) {
            double span = max - min;
            return (IndexedSeq)((SeqLike)Range.Int$.MODULE$.apply(0, steps, 1).map(new Serializable(this, min, steps, span) {
               public static final long serialVersionUID = 0L;
               private final double min$1;
               private final int steps$1;
               private final double span$1;

               public final double apply(int s) {
                  return this.apply$mcDI$sp(s);
               }

               public double apply$mcDI$sp(int s) {
                  return this.min$1 + (double)s * this.span$1 / (double)this.steps$1;
               }

               public {
                  this.min$1 = min$1;
                  this.steps$1 = steps$1;
                  this.span$1 = span$1;
               }
            }, IndexedSeq$.MODULE$.canBuildFrom())).$colon$plus(BoxesRunTime.boxToDouble(max), IndexedSeq$.MODULE$.canBuildFrom());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.bucketCount$1 = bucketCount$1;
            }
         }
      });
   }

   public long[] histogram(double[] buckets, boolean evenBuckets) {
      return (long[])this.org$apache$spark$rdd$DoubleRDDFunctions$$self.withScope(new Serializable(this, buckets, evenBuckets) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DoubleRDDFunctions $outer;
         private final double[] buckets$1;
         private final boolean evenBuckets$1;

         public final long[] apply() {
            if (this.buckets$1.length < 2) {
               throw new IllegalArgumentException("buckets array must have at least two elements");
            } else {
               Serializable var10000;
               if (this.evenBuckets$1) {
                  double var2 = BoxesRunTime.unboxToDouble(Predef$.MODULE$.doubleArrayOps(this.buckets$1).head());
                  double var4 = BoxesRunTime.unboxToDouble(Predef$.MODULE$.doubleArrayOps(this.buckets$1).last());
                  int var6 = this.buckets$1.length - 1;
                  var10000 = new Serializable(this, var2, var4, var6) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final double eta$0$1$1;
                     private final double eta$1$1$1;
                     private final int eta$2$1$1;

                     public final Option<Object> apply(double e) {
                        return this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$anonfun$$fastBucketFunction$1(this.eta$0$1$1, this.eta$1$1$1, this.eta$2$1$1, e);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.eta$0$1$1 = eta$0$1$1;
                           this.eta$1$1$1 = eta$1$1$1;
                           this.eta$2$1$1 = eta$2$1$1;
                        }
                     }
                  };
               } else {
                  var10000 = new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final Option<Object> apply(double e) {
                        return this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$anonfun$$basicBucketFunction$1(e);
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

               Function1 bucketFunction = var10000;
               return this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.partitions().length == 0 ? new long[this.buckets$1.length - 1] : (long[])this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.mapPartitions(new Serializable(this, bucketFunction) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final Function1 bucketFunction$1;

                  public final Iterator<long[]> apply(Iterator<Object> iter) {
                     return this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$anonfun$$histogramPartition$1(this.bucketFunction$1, iter);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.bucketFunction$1 = bucketFunction$1;
                     }
                  }
               }, this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$self.mapPartitions$default$2(), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Long.TYPE))).reduce(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final long[] apply(long[] a1, long[] a2) {
                     return this.$outer.org$apache$spark$rdd$DoubleRDDFunctions$$anonfun$$mergeCounters$1(a1, a2);
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
         }

         public final Iterator org$apache$spark$rdd$DoubleRDDFunctions$$anonfun$$histogramPartition$1(Function1 bucketFunction, Iterator iter) {
            long[] counters = new long[this.buckets$1.length - 1];

            while(iter.hasNext()) {
               Option var4 = (Option)bucketFunction.apply(iter.next());
               if (var4 instanceof Some) {
                  Some var5 = (Some)var4;
                  int x = BoxesRunTime.unboxToInt(var5.x());
                  int var10002 = counters[x]++;
                  BoxedUnit var8 = BoxedUnit.UNIT;
               } else {
                  BoxedUnit var9 = BoxedUnit.UNIT;
               }
            }

            return scala.package$.MODULE$.Iterator().apply(Predef$.MODULE$.wrapRefArray((Object[])(new long[][]{counters})));
         }

         public final long[] org$apache$spark$rdd$DoubleRDDFunctions$$anonfun$$mergeCounters$1(long[] a1, long[] a2) {
            Predef$.MODULE$.longArrayOps(a1).indices().foreach$mVc$sp(new Serializable(this, a1, a2) {
               public static final long serialVersionUID = 0L;
               private final long[] a1$1;
               private final long[] a2$1;

               public final void apply(int i) {
                  this.apply$mcVI$sp(i);
               }

               public void apply$mcVI$sp(int i) {
                  this.a1$1[i] += this.a2$1[i];
               }

               public {
                  this.a1$1 = a1$1;
                  this.a2$1 = a2$1;
               }
            });
            return a1;
         }

         public final Option org$apache$spark$rdd$DoubleRDDFunctions$$anonfun$$basicBucketFunction$1(double e) {
            int location = Arrays.binarySearch(this.buckets$1, e);
            Object var10000;
            if (location < 0) {
               int insertionPoint = -location - 1;
               var10000 = insertionPoint > 0 && insertionPoint < this.buckets$1.length ? new Some(BoxesRunTime.boxToInteger(insertionPoint - 1)) : None$.MODULE$;
            } else {
               var10000 = location < this.buckets$1.length - 1 ? new Some(BoxesRunTime.boxToInteger(location)) : new Some(BoxesRunTime.boxToInteger(location - 1));
            }

            return (Option)var10000;
         }

         public final Option org$apache$spark$rdd$DoubleRDDFunctions$$anonfun$$fastBucketFunction$1(double min, double max, int count, double e) {
            Object var10000;
            if (!Predef$.MODULE$.double2Double(e).isNaN() && !(e < min) && !(e > max)) {
               int bucketNumber = (int)((e - min) / (max - min) * (double)count);
               var10000 = new Some(BoxesRunTime.boxToInteger(scala.math.package$.MODULE$.min(bucketNumber, count - 1)));
            } else {
               var10000 = None$.MODULE$;
            }

            return (Option)var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.buckets$1 = buckets$1;
               this.evenBuckets$1 = evenBuckets$1;
            }
         }
      });
   }

   public boolean histogram$default$2() {
      return false;
   }

   public DoubleRDDFunctions(RDD<Object> self) {
      this.org$apache$spark$rdd$DoubleRDDFunctions$$self = self;
      Logging$class.$init$(this);
   }
}
