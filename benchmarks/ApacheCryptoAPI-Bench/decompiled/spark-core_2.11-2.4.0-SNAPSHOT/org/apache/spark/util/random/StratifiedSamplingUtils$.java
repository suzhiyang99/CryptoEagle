package org.apache.spark.util.random;

import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDD$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function2;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterator;
import scala.collection.MapLike;
import scala.collection.TraversableOnce;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.Map;
import scala.collection.mutable.ResizableArray;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Null$;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.runtime.TraitSetter;

public final class StratifiedSamplingUtils$ implements Logging {
   public static final StratifiedSamplingUtils$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new StratifiedSamplingUtils$();
   }

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

   public <K, V> Map<K, AcceptanceResult> getAcceptanceResults(RDD<Tuple2<K, V>> rdd, boolean withReplacement, scala.collection.Map<K, Object> fractions, Option<scala.collection.Map<K, Object>> counts, long seed) {
      Function2 combOp = this.getCombOp();
      RDD mappedPartitionRDD = rdd.mapPartitionsWithIndex(new Serializable(withReplacement, fractions, counts, seed, combOp) {
         public static final long serialVersionUID = 0L;
         private final boolean withReplacement$1;
         private final scala.collection.Map fractions$2;
         private final Option counts$1;
         private final long seed$3;
         private final Function2 combOp$1;

         public final Iterator<Map<K, AcceptanceResult>> apply(int x0$1, Iterator<Tuple2<K, V>> x1$1) {
            Tuple2 var3 = new Tuple2(BoxesRunTime.boxToInteger(x0$1), x1$1);
            if (var3 != null) {
               int partition = var3._1$mcI$sp();
               Iterator iter = (Iterator)var3._2();
               Map zeroU = new HashMap();
               StratifiedSamplingUtils.RandomDataGenerator rng = new StratifiedSamplingUtils.RandomDataGenerator();
               rng.reSeed(this.seed$3 + (long)partition);
               Function2 seqOp = StratifiedSamplingUtils$.MODULE$.getSeqOp(this.withReplacement$1, this.fractions$2, rng, this.counts$1);
               Iterator var6 = scala.package$.MODULE$.Iterator().apply(Predef$.MODULE$.wrapRefArray(new Map[]{(Map)iter.aggregate(new Serializable(this, zeroU) {
                  public static final long serialVersionUID = 0L;
                  private final Map zeroU$1;

                  public final Map<K, AcceptanceResult> apply() {
                     return this.zeroU$1;
                  }

                  public {
                     this.zeroU$1 = zeroU$1;
                  }
               }, seqOp, this.combOp$1)}));
               return var6;
            } else {
               throw new MatchError(var3);
            }
         }

         public {
            this.withReplacement$1 = withReplacement$1;
            this.fractions$2 = fractions$2;
            this.counts$1 = counts$1;
            this.seed$3 = seed$3;
            this.combOp$1 = combOp$1;
         }
      }, rdd.mapPartitionsWithIndex$default$2(), ClassTag$.MODULE$.apply(Map.class));
      return (Map)mappedPartitionRDD.reduce(combOp);
   }

   public <K, V> Function2<Map<K, AcceptanceResult>, Tuple2<K, V>, Map<K, AcceptanceResult>> getSeqOp(boolean withReplacement, scala.collection.Map<K, Object> fractions, StratifiedSamplingUtils.RandomDataGenerator rng, Option<scala.collection.Map<K, Object>> counts) {
      double delta = 5.0E-5;
      return new Serializable(withReplacement, fractions, rng, counts, delta) {
         public static final long serialVersionUID = 0L;
         private final boolean withReplacement$2;
         private final scala.collection.Map fractions$3;
         public final StratifiedSamplingUtils.RandomDataGenerator rng$1;
         private final Option counts$2;
         private final double delta$1;

         public final Map<K, AcceptanceResult> apply(Map<K, AcceptanceResult> result, Tuple2<K, V> item) {
            Object key = item._1();
            double fraction = BoxesRunTime.unboxToDouble(this.fractions$3.apply(key));
            if (result.contains(key)) {
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               result.$plus$eq(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(key), new AcceptanceResult(AcceptanceResult$.MODULE$.$lessinit$greater$default$1(), AcceptanceResult$.MODULE$.$lessinit$greater$default$2())));
            }

            AcceptanceResult acceptResult = (AcceptanceResult)result.apply(key);
            if (this.withReplacement$2) {
               if (acceptResult.areBoundsEmpty()) {
                  long n = BoxesRunTime.unboxToLong(((MapLike)this.counts$2.get()).apply(key));
                  long sampleSize = (long)scala.math.package$.MODULE$.ceil((double)n * fraction);
                  double lmbd1 = PoissonBounds$.MODULE$.getLowerBound((double)sampleSize);
                  double lmbd2 = PoissonBounds$.MODULE$.getUpperBound((double)sampleSize);
                  acceptResult.acceptBound_$eq(lmbd1 / (double)n);
                  acceptResult.waitListBound_$eq((lmbd2 - lmbd1) / (double)n);
               }

               double acceptBound = acceptResult.acceptBound();
               long copiesAccepted = acceptBound == (double)0.0F ? 0L : (long)this.rng$1.nextPoisson(acceptBound);
               if (copiesAccepted > 0L) {
                  acceptResult.numAccepted_$eq(acceptResult.numAccepted() + copiesAccepted);
               }

               int copiesWaitlisted = this.rng$1.nextPoisson(acceptResult.waitListBound());
               if (copiesWaitlisted > 0) {
                  acceptResult.waitList().$plus$plus$eq((TraversableOnce)ArrayBuffer$.MODULE$.fill(copiesWaitlisted, new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final double apply() {
                        return this.apply$mcD$sp();
                     }

                     public double apply$mcD$sp() {
                        return this.$outer.rng$1.nextUniform();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  }));
               } else {
                  BoxedUnit var22 = BoxedUnit.UNIT;
               }
            } else {
               acceptResult.acceptBound_$eq(BinomialBounds$.MODULE$.getLowerBound(this.delta$1, acceptResult.numItems(), fraction));
               acceptResult.waitListBound_$eq(BinomialBounds$.MODULE$.getUpperBound(this.delta$1, acceptResult.numItems(), fraction));
               double x = this.rng$1.nextUniform();
               if (x < acceptResult.acceptBound()) {
                  acceptResult.numAccepted_$eq(acceptResult.numAccepted() + 1L);
                  BoxedUnit var23 = BoxedUnit.UNIT;
               } else if (x < acceptResult.waitListBound()) {
                  acceptResult.waitList().$plus$eq(BoxesRunTime.boxToDouble(x));
               } else {
                  BoxedUnit var24 = BoxedUnit.UNIT;
               }
            }

            acceptResult.numItems_$eq(acceptResult.numItems() + 1L);
            return result;
         }

         public {
            this.withReplacement$2 = withReplacement$2;
            this.fractions$3 = fractions$3;
            this.rng$1 = rng$1;
            this.counts$2 = counts$2;
            this.delta$1 = delta$1;
         }
      };
   }

   public <K> Function2<Map<K, AcceptanceResult>, Map<K, AcceptanceResult>, Map<K, AcceptanceResult>> getCombOp() {
      return new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Map<K, AcceptanceResult> apply(Map<K, AcceptanceResult> result1, Map<K, AcceptanceResult> result2) {
            result1.keySet().union(result2.keySet()).foreach(new Serializable(this, result1, result2) {
               public static final long serialVersionUID = 0L;
               private final Map result1$1;
               private final Map result2$1;

               public final Object apply(K key) {
                  Option entry1 = this.result1$1.get(key);
                  Object var10000;
                  if (this.result2$1.contains(key)) {
                     ((AcceptanceResult)this.result2$1.apply(key)).merge(entry1);
                     var10000 = BoxedUnit.UNIT;
                  } else {
                     var10000 = entry1.isDefined() ? this.result2$1.$plus$eq(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(key), entry1.get())) : BoxedUnit.UNIT;
                  }

                  return var10000;
               }

               public {
                  this.result1$1 = result1$1;
                  this.result2$1 = result2$1;
               }
            });
            return result2;
         }
      };
   }

   public <K> scala.collection.Map<K, Object> computeThresholdByKey(scala.collection.Map<K, AcceptanceResult> finalResult, scala.collection.Map<K, Object> fractions) {
      HashMap thresholdByKey = new HashMap();
      finalResult.withFilter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<K, AcceptanceResult> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(fractions, thresholdByKey) {
         public static final long serialVersionUID = 0L;
         private final scala.collection.Map fractions$4;
         private final HashMap thresholdByKey$1;

         public final HashMap<K, Object> apply(Tuple2<K, AcceptanceResult> x$1) {
            if (x$1 != null) {
               Object key = x$1._1();
               AcceptanceResult acceptResult = (AcceptanceResult)x$1._2();
               long sampleSize = (long)scala.math.package$.MODULE$.ceil((double)acceptResult.numItems() * BoxesRunTime.unboxToDouble(this.fractions$4.apply(key)));
               HashMap var10000;
               if (acceptResult.numAccepted() > sampleSize) {
                  StratifiedSamplingUtils$.MODULE$.logWarning(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Pre-accepted too many";
                     }
                  });
                  var10000 = this.thresholdByKey$1.$plus$eq(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(key), BoxesRunTime.boxToDouble(acceptResult.acceptBound())));
               } else {
                  int numWaitListAccepted = (int)(sampleSize - acceptResult.numAccepted());
                  if (numWaitListAccepted >= acceptResult.waitList().size()) {
                     StratifiedSamplingUtils$.MODULE$.logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "WaitList too short";
                        }
                     });
                     var10000 = this.thresholdByKey$1.$plus$eq(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(key), BoxesRunTime.boxToDouble(acceptResult.waitListBound())));
                  } else {
                     var10000 = this.thresholdByKey$1.$plus$eq(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(key), ((ResizableArray)acceptResult.waitList().sorted(Ordering.Double$.MODULE$)).apply(numWaitListAccepted)));
                  }
               }

               HashMap var5 = var10000;
               return var5;
            } else {
               throw new MatchError(x$1);
            }
         }

         public {
            this.fractions$4 = fractions$4;
            this.thresholdByKey$1 = thresholdByKey$1;
         }
      });
      return thresholdByKey;
   }

   public <K, V> Function2<Object, Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> getBernoulliSamplingFunction(RDD<Tuple2<K, V>> rdd, scala.collection.Map<K, Object> fractions, boolean exact, long seed) {
      ObjectRef samplingRateByKey = ObjectRef.create(fractions);
      if (exact) {
         Map finalResult = this.getAcceptanceResults(rdd, false, fractions, None$.MODULE$, seed);
         samplingRateByKey.elem = (T)this.computeThresholdByKey(finalResult, fractions);
      }

      return new Serializable(seed, samplingRateByKey) {
         public static final long serialVersionUID = 0L;
         private final long seed$2;
         public final ObjectRef samplingRateByKey$1;

         public final Iterator<Tuple2<K, V>> apply(int idx, Iterator<Tuple2<K, V>> iter) {
            StratifiedSamplingUtils.RandomDataGenerator rng = new StratifiedSamplingUtils.RandomDataGenerator();
            rng.reSeed(this.seed$2 + (long)idx);
            return iter.filter(new Serializable(this, rng) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final StratifiedSamplingUtils.RandomDataGenerator rng$2;

               public final boolean apply(Tuple2<K, V> t) {
                  return this.rng$2.nextUniform() < BoxesRunTime.unboxToDouble(((scala.collection.Map)this.$outer.samplingRateByKey$1.elem).apply(t._1()));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.rng$2 = rng$2;
                  }
               }
            });
         }

         public {
            this.seed$2 = seed$2;
            this.samplingRateByKey$1 = samplingRateByKey$1;
         }
      };
   }

   public <K, V> Function2<Object, Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> getPoissonSamplingFunction(RDD<Tuple2<K, V>> rdd, scala.collection.Map<K, Object> fractions, boolean exact, long seed, ClassTag<K> evidence$1, ClassTag<V> evidence$2) {
      Serializable var10000;
      if (exact) {
         RDD$.MODULE$.rddToPairRDDFunctions$default$4(rdd);
         Null$ x$5 = null;
         Some counts = new Some(RDD$.MODULE$.rddToPairRDDFunctions(rdd, evidence$1, evidence$2, (Ordering)null).countByKey());
         Map finalResult = this.getAcceptanceResults(rdd, true, fractions, counts, seed);
         scala.collection.Map thresholdByKey = this.computeThresholdByKey(finalResult, fractions);
         var10000 = new Serializable(seed, finalResult, thresholdByKey) {
            public static final long serialVersionUID = 0L;
            private final long seed$1;
            public final Map finalResult$1;
            public final scala.collection.Map thresholdByKey$2;

            public final Iterator<Tuple2<K, V>> apply(int idx, Iterator<Tuple2<K, V>> iter) {
               StratifiedSamplingUtils.RandomDataGenerator rng = new StratifiedSamplingUtils.RandomDataGenerator();
               rng.reSeed(this.seed$1 + (long)idx);
               return iter.<Tuple2<K, V>>flatMap(new Serializable(this, rng) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  public final StratifiedSamplingUtils.RandomDataGenerator rng$3;

                  public final GenTraversableOnce<Tuple2<K, V>> apply(Tuple2<K, V> item) {
                     Object key = item._1();
                     double acceptBound = ((AcceptanceResult)this.$outer.finalResult$1.apply(key)).acceptBound();
                     long copiesAccepted = acceptBound == (double)0 ? 0L : (long)this.rng$3.nextPoisson(acceptBound);
                     int copiesWaitlisted = this.rng$3.nextPoisson(((AcceptanceResult)this.$outer.finalResult$1.apply(key)).waitListBound());
                     long copiesInSample = copiesAccepted + (long)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), copiesWaitlisted).count(new Serializable(this, key) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final Object key$1;

                        public final boolean apply(int i) {
                           return this.apply$mcZI$sp(i);
                        }

                        public boolean apply$mcZI$sp(int i) {
                           return this.$outer.rng$3.nextUniform() < BoxesRunTime.unboxToDouble(this.$outer.org$apache$spark$util$random$StratifiedSamplingUtils$$anonfun$$anonfun$$$outer().thresholdByKey$2.apply(this.key$1));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.key$1 = key$1;
                           }
                        }
                     });
                     return copiesInSample > 0L ? scala.package$.MODULE$.Iterator().fill((int)copiesInSample, new Serializable(this, item) {
                        public static final long serialVersionUID = 0L;
                        private final Tuple2 item$1;

                        public final Tuple2<K, V> apply() {
                           return this.item$1;
                        }

                        public {
                           this.item$1 = item$1;
                        }
                     }) : scala.package$.MODULE$.Iterator().empty();
                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$util$random$StratifiedSamplingUtils$$anonfun$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.rng$3 = rng$3;
                     }
                  }
               });
            }

            public {
               this.seed$1 = seed$1;
               this.finalResult$1 = finalResult$1;
               this.thresholdByKey$2 = thresholdByKey$2;
            }
         };
      } else {
         var10000 = new Serializable(fractions, seed) {
            public static final long serialVersionUID = 0L;
            public final scala.collection.Map fractions$1;
            private final long seed$1;

            public final Iterator<Tuple2<K, V>> apply(int idx, Iterator<Tuple2<K, V>> iter) {
               StratifiedSamplingUtils.RandomDataGenerator rng = new StratifiedSamplingUtils.RandomDataGenerator();
               rng.reSeed(this.seed$1 + (long)idx);
               return iter.<Tuple2<K, V>>flatMap(new Serializable(this, rng) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final StratifiedSamplingUtils.RandomDataGenerator rng$4;

                  public final GenTraversableOnce<Tuple2<K, V>> apply(Tuple2<K, V> item) {
                     int count = this.rng$4.nextPoisson(BoxesRunTime.unboxToDouble(this.$outer.fractions$1.apply(item._1())));
                     return count == 0 ? scala.package$.MODULE$.Iterator().empty() : scala.package$.MODULE$.Iterator().fill(count, new Serializable(this, item) {
                        public static final long serialVersionUID = 0L;
                        private final Tuple2 item$2;

                        public final Tuple2<K, V> apply() {
                           return this.item$2;
                        }

                        public {
                           this.item$2 = item$2;
                        }
                     });
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.rng$4 = rng$4;
                     }
                  }
               });
            }

            public {
               this.fractions$1 = fractions$1;
               this.seed$1 = seed$1;
            }
         };
      }

      return var10000;
   }

   private StratifiedSamplingUtils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
