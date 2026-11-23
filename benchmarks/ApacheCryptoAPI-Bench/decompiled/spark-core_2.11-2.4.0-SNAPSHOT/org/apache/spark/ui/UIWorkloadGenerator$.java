package org.apache.spark.ui;

import java.util.concurrent.Semaphore;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkContext$;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDD$;
import org.apache.spark.scheduler.SchedulingMode$;
import scala.Enumeration;
import scala.Function0;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple2$mcII$sp;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.RichInt$;
import scala.util.Random;

public final class UIWorkloadGenerator$ {
   public static final UIWorkloadGenerator$ MODULE$;
   private final int NUM_PARTITIONS;
   private final int INTER_JOB_WAIT_MS;

   static {
      new UIWorkloadGenerator$();
   }

   public int NUM_PARTITIONS() {
      return this.NUM_PARTITIONS;
   }

   public int INTER_JOB_WAIT_MS() {
      return this.INTER_JOB_WAIT_MS;
   }

   public void main(String[] args) {
      if (args.length < 3) {
         Predef$.MODULE$.println("Usage: ./bin/spark-class org.apache.spark.ui.UIWorkloadGenerator [master] [FIFO|FAIR] [#job set (4 jobs per set)]");
         System.exit(1);
      }

      SparkConf conf;
      Enumeration.Value schedulingMode;
      label20: {
         label19: {
            conf = (new SparkConf()).setMaster(args[0]).setAppName("Spark UI tester");
            schedulingMode = SchedulingMode$.MODULE$.withName(args[1]);
            Enumeration.Value var4 = SchedulingMode$.MODULE$.FAIR();
            if (schedulingMode == null) {
               if (var4 == null) {
                  break label19;
               }
            } else if (schedulingMode.equals(var4)) {
               break label19;
            }

            BoxedUnit var10000 = BoxedUnit.UNIT;
            break label20;
         }

         conf.set("spark.scheduler.mode", "FAIR");
      }

      int nJobSet = (new StringOps(Predef$.MODULE$.augmentString(args[2]))).toInt();
      SparkContext sc = new SparkContext(conf);
      RDD baseData = sc.makeRDD(RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), this.NUM_PARTITIONS() * 10), this.NUM_PARTITIONS(), ClassTag$.MODULE$.Int());
      Seq jobs = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("Count", new Serializable(baseData) {
         public static final long serialVersionUID = 0L;
         private final RDD baseData$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.baseData$1.count();
         }

         public {
            this.baseData$1 = baseData$1;
         }
      }), new Tuple2("Cache and Count", new Serializable(baseData) {
         public static final long serialVersionUID = 0L;
         private final RDD baseData$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.baseData$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(int x) {
                  return this.apply$mcII$sp(x);
               }

               public int apply$mcII$sp(int x) {
                  return x;
               }
            }, ClassTag$.MODULE$.Int()).cache().count();
         }

         public {
            this.baseData$1 = baseData$1;
         }
      }), new Tuple2("Single Shuffle", new Serializable(baseData) {
         public static final long serialVersionUID = 0L;
         private final RDD baseData$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return RDD$.MODULE$.rddToPairRDDFunctions(this.baseData$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Tuple2<Object, Object> apply(int x) {
                  return new Tuple2$mcII$sp(x % 10, x);
               }
            }, ClassTag$.MODULE$.apply(Tuple2.class)), ClassTag$.MODULE$.Int(), ClassTag$.MODULE$.Int(), Ordering.Int$.MODULE$).reduceByKey(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(int x$1, int x$2) {
                  return this.apply$mcIII$sp(x$1, x$2);
               }

               public int apply$mcIII$sp(int x$1, int x$2) {
                  return x$1 + x$2;
               }
            }).count();
         }

         public {
            this.baseData$1 = baseData$1;
         }
      }), new Tuple2("Entirely failed phase", new Serializable(baseData) {
         public static final long serialVersionUID = 0L;
         private final RDD baseData$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.baseData$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(int x) {
                  return this.apply$mcII$sp(x);
               }

               public int apply$mcII$sp(int x) {
                  throw new Exception();
               }
            }, ClassTag$.MODULE$.Int()).count();
         }

         public {
            this.baseData$1 = baseData$1;
         }
      }), new Tuple2("Partially failed phase", new Serializable(baseData) {
         public static final long serialVersionUID = 0L;
         private final RDD baseData$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.baseData$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(int x) {
                  return this.apply$mcII$sp(x);
               }

               public int apply$mcII$sp(int x) {
                  double probFailure = (double)4.0F / (double)UIWorkloadGenerator$.MODULE$.NUM_PARTITIONS();
                  if ((double)UIWorkloadGenerator$.MODULE$.org$apache$spark$ui$UIWorkloadGenerator$$nextFloat$1() < probFailure) {
                     throw new Exception("This is a task failure");
                  } else {
                     return 1;
                  }
               }
            }, ClassTag$.MODULE$.Int()).count();
         }

         public {
            this.baseData$1 = baseData$1;
         }
      }), new Tuple2("Partially failed phase (longer tasks)", new Serializable(baseData) {
         public static final long serialVersionUID = 0L;
         private final RDD baseData$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.baseData$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(int x) {
                  return this.apply$mcII$sp(x);
               }

               public int apply$mcII$sp(int x) {
                  double probFailure = (double)4.0F / (double)UIWorkloadGenerator$.MODULE$.NUM_PARTITIONS();
                  if ((double)UIWorkloadGenerator$.MODULE$.org$apache$spark$ui$UIWorkloadGenerator$$nextFloat$1() < probFailure) {
                     Thread.sleep(100L);
                     throw new Exception("This is a task failure");
                  } else {
                     return 1;
                  }
               }
            }, ClassTag$.MODULE$.Int()).count();
         }

         public {
            this.baseData$1 = baseData$1;
         }
      }), new Tuple2("Job with delays", new Serializable(baseData) {
         public static final long serialVersionUID = 0L;
         private final RDD baseData$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.baseData$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(int x) {
                  this.apply$mcVI$sp(x);
               }

               public void apply$mcVI$sp(int x) {
                  Thread.sleep(100L);
               }
            }, ClassTag$.MODULE$.Unit()).count();
         }

         public {
            this.baseData$1 = baseData$1;
         }
      })}));
      Semaphore barrier = new Semaphore(-nJobSet * jobs.size() + 1);
      RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), nJobSet).foreach$mVc$sp(new Serializable(schedulingMode, sc, jobs, barrier) {
         public static final long serialVersionUID = 0L;
         public final Enumeration.Value schedulingMode$1;
         public final SparkContext sc$1;
         private final Seq jobs$1;
         public final Semaphore barrier$1;

         public final void apply(int x$3) {
            this.apply$mcVI$sp(x$3);
         }

         public void apply$mcVI$sp(int x$3) {
            this.jobs$1.withFilter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(Tuple2<String, Function0<Object>> check$ifrefutable$1) {
                  boolean var3;
                  if (check$ifrefutable$1 != null) {
                     var3 = true;
                  } else {
                     var3 = false;
                  }

                  return var3;
               }
            }).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(Tuple2<String, Function0<Object>> x$4) {
                  if (x$4 != null) {
                     String desc = (String)x$4._1();
                     Function0 job = (Function0)x$4._2();
                     (new Thread(this, desc, job) {
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final String desc$1;
                        private final Function0 job$1;

                        public void run() {
                           try {
                              UIWorkloadGenerator$.MODULE$.org$apache$spark$ui$UIWorkloadGenerator$$setProperties$1(this.desc$1, this.$outer.org$apache$spark$ui$UIWorkloadGenerator$$anonfun$$anonfun$$$outer().schedulingMode$1, this.$outer.org$apache$spark$ui$UIWorkloadGenerator$$anonfun$$anonfun$$$outer().sc$1);
                              this.job$1.apply$mcJ$sp();
                              Predef$.MODULE$.println((new StringBuilder()).append((Object)"Job finished: ").append((Object)this.desc$1).toString());
                           } catch (Exception var5) {
                              Predef$.MODULE$.println((new StringBuilder()).append((Object)"Job Failed: ").append((Object)this.desc$1).toString());
                           } finally {
                              this.$outer.org$apache$spark$ui$UIWorkloadGenerator$$anonfun$$anonfun$$$outer().barrier$1.release();
                           }

                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.desc$1 = desc$1;
                              this.job$1 = job$1;
                           }
                        }
                     }).start();
                     Thread.sleep((long)UIWorkloadGenerator$.MODULE$.INTER_JOB_WAIT_MS());
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  } else {
                     throw new MatchError(x$4);
                  }
               }

               // $FF: synthetic method
               public <undefinedtype> org$apache$spark$ui$UIWorkloadGenerator$$anonfun$$anonfun$$$outer() {
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

         public {
            this.schedulingMode$1 = schedulingMode$1;
            this.sc$1 = sc$1;
            this.jobs$1 = jobs$1;
            this.barrier$1 = barrier$1;
         }
      });
      barrier.acquire();
      sc.stop();
   }

   public final void org$apache$spark$ui$UIWorkloadGenerator$$setProperties$1(String s, Enumeration.Value schedulingMode$1, SparkContext sc$1) {
      label14: {
         Enumeration.Value var4 = SchedulingMode$.MODULE$.FAIR();
         if (schedulingMode$1 == null) {
            if (var4 != null) {
               break label14;
            }
         } else if (!schedulingMode$1.equals(var4)) {
            break label14;
         }

         sc$1.setLocalProperty("spark.scheduler.pool", s);
      }

      sc$1.setLocalProperty(SparkContext$.MODULE$.SPARK_JOB_DESCRIPTION(), s);
   }

   public final float org$apache$spark$ui$UIWorkloadGenerator$$nextFloat$1() {
      return (new Random()).nextFloat();
   }

   private UIWorkloadGenerator$() {
      MODULE$ = this;
      this.NUM_PARTITIONS = 100;
      this.INTER_JOB_WAIT_MS = 5000;
   }
}
