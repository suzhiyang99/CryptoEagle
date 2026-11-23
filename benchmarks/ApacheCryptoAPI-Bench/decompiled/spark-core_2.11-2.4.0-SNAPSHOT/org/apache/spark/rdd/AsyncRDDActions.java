package org.apache.spark.rdd;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.spark.ComplexFutureAction;
import org.apache.spark.FutureAction;
import org.apache.spark.JobSubmitter;
import org.apache.spark.SimpleFutureAction;
import org.apache.spark.SparkContext;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.CallSite;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.Unit$;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.Range;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.WrappedArray;
import scala.concurrent.ExecutionContextExecutorService;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}a\u0001B\u0001\u0003\u0001-\u0011q\"Q:z]\u000e\u0014F\tR!di&|gn\u001d\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\u000b\u0003\u0019\u0011\u001aB\u0001A\u0007\u0014-A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"A\u0004\u000b\n\u0005Uy!\u0001D*fe&\fG.\u001b>bE2,\u0007CA\f\u001b\u001b\u0005A\"BA\r\u0005\u0003!Ig\u000e^3s]\u0006d\u0017BA\u000e\u0019\u0005\u001daunZ4j]\u001eD\u0001\"\b\u0001\u0003\u0002\u0003\u0006IAH\u0001\u0005g\u0016dg\rE\u0002 A\tj\u0011AA\u0005\u0003C\t\u00111A\u0015#E!\t\u0019C\u0005\u0004\u0001\u0005\u000b\u0015\u0002!\u0019\u0001\u0014\u0003\u0003Q\u000b\"a\n\u0016\u0011\u00059A\u0013BA\u0015\u0010\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"AD\u0016\n\u00051z!aA!os\"Aa\u0006\u0001B\u0002B\u0003-q&\u0001\u0006fm&$WM\\2fIE\u00022\u0001M\u001a#\u001b\u0005\t$B\u0001\u001a\u0010\u0003\u001d\u0011XM\u001a7fGRL!\u0001N\u0019\u0003\u0011\rc\u0017m]:UC\u001eDQA\u000e\u0001\u0005\u0002]\na\u0001P5oSRtDC\u0001\u001d<)\tI$\bE\u0002 \u0001\tBQAL\u001bA\u0004=BQ!H\u001bA\u0002yAQ!\u0010\u0001\u0005\u0002y\n!bY8v]R\f5/\u001f8d)\u0005y\u0004c\u0001!B\u00076\tA!\u0003\u0002C\t\taa)\u001e;ve\u0016\f5\r^5p]B\u0011a\u0002R\u0005\u0003\u000b>\u0011A\u0001T8oO\")q\t\u0001C\u0001\u0011\u0006a1m\u001c7mK\u000e$\u0018i]=oGR\t\u0011\nE\u0002A\u0003*\u00032aS*#\u001d\ta\u0015K\u0004\u0002N!6\taJ\u0003\u0002P\u0015\u00051AH]8pizJ\u0011\u0001E\u0005\u0003%>\tq\u0001]1dW\u0006<W-\u0003\u0002U+\n\u00191+Z9\u000b\u0005I{\u0001\"B,\u0001\t\u0003A\u0016!\u0003;bW\u0016\f5/\u001f8d)\tI\u0015\fC\u0003[-\u0002\u00071,A\u0002ok6\u0004\"A\u0004/\n\u0005u{!aA%oi\")q\f\u0001C\u0001A\u0006aam\u001c:fC\u000eD\u0017i]=oGR\u0011\u0011-\u001a\t\u0004\u0001\u0006\u0013\u0007C\u0001\bd\u0013\t!wB\u0001\u0003V]&$\b\"\u00024_\u0001\u00049\u0017!\u00014\u0011\t9A'EY\u0005\u0003S>\u0011\u0011BR;oGRLwN\\\u0019\t\u000b-\u0004A\u0011\u00017\u0002+\u0019|'/Z1dQB\u000b'\u000f^5uS>t\u0017i]=oGR\u0011\u0011-\u001c\u0005\u0006M*\u0004\rA\u001c\t\u0005\u001d!|'\rE\u0002La\nJ!!]+\u0003\u0011%#XM]1u_J<Qa\u001d\u0002\t\nQ\fq\"Q:z]\u000e\u0014F\tR!di&|gn\u001d\t\u0003?U4Q!\u0001\u0002\t\nY\u001c2!^\u0007\u0014\u0011\u00151T\u000f\"\u0001y)\u0005!\bb\u0002>v\u0005\u0004%\ta_\u0001\u0017MV$XO]3Fq\u0016\u001cW\u000f^5p]\u000e{g\u000e^3yiV\tA\u0010E\u0002~\u0003\u0003i\u0011A \u0006\u0003\u007f>\t!bY8oGV\u0014(/\u001a8u\u0013\r\t\u0019A \u0002 \u000bb,7-\u001e;j_:\u001cuN\u001c;fqR,\u00050Z2vi>\u00148+\u001a:wS\u000e,\u0007bBA\u0004k\u0002\u0006I\u0001`\u0001\u0018MV$XO]3Fq\u0016\u001cW\u000f^5p]\u000e{g\u000e^3yi\u0002B\u0011\"a\u0003v\u0003\u0003%I!!\u0004\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u001f\u0001B!!\u0005\u0002\u001c5\u0011\u00111\u0003\u0006\u0005\u0003+\t9\"\u0001\u0003mC:<'BAA\r\u0003\u0011Q\u0017M^1\n\t\u0005u\u00111\u0003\u0002\u0007\u001f\nTWm\u0019;"
)
public class AsyncRDDActions<T> implements Serializable, Logging {
   public final RDD<T> org$apache$spark$rdd$AsyncRDDActions$$self;
   public final ClassTag<T> org$apache$spark$rdd$AsyncRDDActions$$evidence$1;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static ExecutionContextExecutorService futureExecutionContext() {
      return AsyncRDDActions$.MODULE$.futureExecutionContext();
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

   public FutureAction<Object> countAsync() {
      return (FutureAction)this.org$apache$spark$rdd$AsyncRDDActions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AsyncRDDActions $outer;

         public final SimpleFutureAction<Object> apply() {
            AtomicLong totalCount = new AtomicLong();
            return this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.context().submitJob(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(Iterator<T> iter) {
                  long result = 0L;

                  while(iter.hasNext()) {
                     ++result;
                     iter.next();
                  }

                  return result;
               }
            }, scala.package$.MODULE$.Range().apply(0, this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.partitions().length), new Serializable(this, totalCount) {
               public static final long serialVersionUID = 0L;
               private final AtomicLong totalCount$1;

               public final void apply(int index, long data) {
                  this.apply$mcVIJ$sp(index, data);
               }

               public void apply$mcVIJ$sp(int index, long data) {
                  this.totalCount$1.addAndGet(data);
               }

               public {
                  this.totalCount$1 = totalCount$1;
               }
            }, new Serializable(this, totalCount) {
               public static final long serialVersionUID = 0L;
               private final AtomicLong totalCount$1;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return this.totalCount$1.get();
               }

               public {
                  this.totalCount$1 = totalCount$1;
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

   public FutureAction<Seq<T>> collectAsync() {
      return (FutureAction)this.org$apache$spark$rdd$AsyncRDDActions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AsyncRDDActions $outer;

         public final SimpleFutureAction<Seq<T>> apply() {
            Object[] results = ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$evidence$1.runtimeClass())).newArray(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.partitions().length);
            return this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.context().submitJob(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self, new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final Object apply(Iterator<T> x$1) {
                  return x$1.toArray(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$anonfun$$$outer().org$apache$spark$rdd$AsyncRDDActions$$evidence$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, scala.package$.MODULE$.Range().apply(0, this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.partitions().length), new Serializable(this, results) {
               public static final long serialVersionUID = 0L;
               private final Object[] results$1;

               public final void apply(int index, Object data) {
                  this.results$1[index] = data;
               }

               public {
                  this.results$1 = results$1;
               }
            }, new Serializable(this, results) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Object[] results$1;

               public final Seq<T> apply() {
                  return Predef$.MODULE$.genericArrayOps(Predef$.MODULE$.refArrayOps(this.results$1).flatten(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final WrappedArray<T> apply(Object xs) {
                        return Predef$.MODULE$.genericWrapArray(xs);
                     }
                  }, this.$outer.org$apache$spark$rdd$AsyncRDDActions$$anonfun$$$outer().org$apache$spark$rdd$AsyncRDDActions$$evidence$1)).toSeq();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.results$1 = results$1;
                  }
               }
            });
         }

         // $FF: synthetic method
         public AsyncRDDActions org$apache$spark$rdd$AsyncRDDActions$$anonfun$$$outer() {
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

   public FutureAction<Seq<T>> takeAsync(int num) {
      return (FutureAction)this.org$apache$spark$rdd$AsyncRDDActions$$self.withScope(new Serializable(this, num) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AsyncRDDActions $outer;
         public final int num$1;

         public final ComplexFutureAction<Seq<T>> apply() {
            CallSite callSite = this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.context().getCallSite();
            Properties localProperties = this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.context().getLocalProperties();
            ExecutionContextExecutorService executionContext = AsyncRDDActions$.MODULE$.futureExecutionContext();
            ArrayBuffer results = new ArrayBuffer();
            int totalParts = this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.partitions().length;
            return new ComplexFutureAction<Seq<T>>(new Serializable(this, callSite, localProperties, executionContext, results, totalParts) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final CallSite callSite$1;
               private final Properties localProperties$1;
               private final ExecutionContextExecutorService executionContext$1;
               private final ArrayBuffer results$2;
               private final int totalParts$1;

               public final Future<Seq<T>> apply(JobSubmitter x$4) {
                  return this.$outer.org$apache$spark$rdd$AsyncRDDActions$$anonfun$$continue$1(0, x$4, this.callSite$1, this.localProperties$1, this.executionContext$1, this.results$2, this.totalParts$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.callSite$1 = callSite$1;
                     this.localProperties$1 = localProperties$1;
                     this.executionContext$1 = executionContext$1;
                     this.results$2 = results$2;
                     this.totalParts$1 = totalParts$1;
                  }
               }
            });
         }

         // $FF: synthetic method
         public AsyncRDDActions org$apache$spark$rdd$AsyncRDDActions$$anonfun$$$outer() {
            return this.$outer;
         }

         public final Future org$apache$spark$rdd$AsyncRDDActions$$anonfun$$continue$1(int partsScanned, JobSubmitter jobSubmitter, CallSite callSite$1, Properties localProperties$1, ExecutionContextExecutorService executionContext$1, ArrayBuffer results$2, int totalParts$1) {
            Future var10000;
            if (results$2.size() < this.num$1 && partsScanned < totalParts$1) {
               long numPartsToTry = 1L;
               if (partsScanned > 0) {
                  if (results$2.size() == 0) {
                     numPartsToTry = (long)(partsScanned * 4);
                  } else {
                     numPartsToTry = (long)Math.max(1, (int)((double)1.5F * (double)this.num$1 * (double)partsScanned / (double)results$2.size()) - partsScanned);
                     numPartsToTry = Math.min(numPartsToTry, (long)(partsScanned * 4));
                  }
               }

               int left = this.num$1 - results$2.size();
               Range p = RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(partsScanned), (int)scala.math.package$.MODULE$.min((long)partsScanned + numPartsToTry, (long)totalParts$1));
               Object[] buf = ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$evidence$1.runtimeClass())).newArray(p.size());
               this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.context().setCallSite(callSite$1);
               this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.context().setLocalProperties(localProperties$1);
               FutureAction job = jobSubmitter.submitJob(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self, new Serializable(this, left) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int left$1;

                  public final Object apply(Iterator<T> it) {
                     return it.take(this.left$1).toArray(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$anonfun$$$outer().org$apache$spark$rdd$AsyncRDDActions$$evidence$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.left$1 = left$1;
                     }
                  }
               }, p, new Serializable(this, buf) {
                  public static final long serialVersionUID = 0L;
                  private final Object[] buf$1;

                  public final void apply(int index, Object data) {
                     this.buf$1[index] = data;
                  }

                  public {
                     this.buf$1 = buf$1;
                  }
               }, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Unit$ apply() {
                     return Unit$.MODULE$;
                  }
               });
               var10000 = job.flatMap(new Serializable(this, callSite$1, localProperties$1, executionContext$1, results$2, totalParts$1, partsScanned, jobSubmitter, p, buf) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final CallSite callSite$1;
                  private final Properties localProperties$1;
                  private final ExecutionContextExecutorService executionContext$1;
                  public final ArrayBuffer results$2;
                  private final int totalParts$1;
                  private final int partsScanned$1;
                  private final JobSubmitter jobSubmitter$1;
                  private final Range p$1;
                  private final Object[] buf$1;

                  public final Future<Seq<T>> apply(Unit$ x$2) {
                     Predef$.MODULE$.refArrayOps(this.buf$1).foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final ArrayBuffer<T> apply(Object x$3) {
                           return this.$outer.results$2.$plus$plus$eq(Predef$.MODULE$.genericArrayOps(Predef$.MODULE$.genericArrayOps(x$3).take(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$anonfun$$anonfun$$$outer().num$1 - this.$outer.results$2.size())));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     return this.$outer.org$apache$spark$rdd$AsyncRDDActions$$anonfun$$continue$1(this.partsScanned$1 + this.p$1.size(), this.jobSubmitter$1, this.callSite$1, this.localProperties$1, this.executionContext$1, this.results$2, this.totalParts$1);
                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$rdd$AsyncRDDActions$$anonfun$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.callSite$1 = callSite$1;
                        this.localProperties$1 = localProperties$1;
                        this.executionContext$1 = executionContext$1;
                        this.results$2 = results$2;
                        this.totalParts$1 = totalParts$1;
                        this.partsScanned$1 = partsScanned$1;
                        this.jobSubmitter$1 = jobSubmitter$1;
                        this.p$1 = p$1;
                        this.buf$1 = buf$1;
                     }
                  }
               }, executionContext$1);
            } else {
               var10000 = Future$.MODULE$.successful(results$2.toSeq());
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.num$1 = num$1;
            }
         }
      });
   }

   public FutureAction<BoxedUnit> foreachAsync(Function1<T, BoxedUnit> f) {
      return (FutureAction)this.org$apache$spark$rdd$AsyncRDDActions$$self.withScope(new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AsyncRDDActions $outer;
         private final Function1 f$1;

         public final SimpleFutureAction<BoxedUnit> apply() {
            SparkContext qual$1 = this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.context();
            Function1 x$6 = this.f$1;
            boolean x$7 = qual$1.clean$default$2();
            Function1 cleanF = (Function1)qual$1.clean(x$6, x$7);
            return this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.context().submitJob(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self, new Serializable(this, cleanF) {
               public static final long serialVersionUID = 0L;
               private final Function1 cleanF$1;

               public final void apply(Iterator<T> x$5) {
                  x$5.foreach(this.cleanF$1);
               }

               public {
                  this.cleanF$1 = cleanF$1;
               }
            }, scala.package$.MODULE$.Range().apply(0, this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.partitions().length), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(int index, BoxedUnit data) {
                  Unit$ var10000 = Unit$.MODULE$;
               }
            }, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  Unit$ var10000 = Unit$.MODULE$;
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.f$1 = f$1;
            }
         }
      });
   }

   public FutureAction<BoxedUnit> foreachPartitionAsync(Function1<Iterator<T>, BoxedUnit> f) {
      return (FutureAction)this.org$apache$spark$rdd$AsyncRDDActions$$self.withScope(new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AsyncRDDActions $outer;
         private final Function1 f$2;

         public final SimpleFutureAction<BoxedUnit> apply() {
            return this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.context().submitJob(this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self, this.f$2, scala.package$.MODULE$.Range().apply(0, this.$outer.org$apache$spark$rdd$AsyncRDDActions$$self.partitions().length), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(int index, BoxedUnit data) {
                  Unit$ var10000 = Unit$.MODULE$;
               }
            }, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  Unit$ var10000 = Unit$.MODULE$;
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.f$2 = f$2;
            }
         }
      });
   }

   public AsyncRDDActions(RDD<T> self, ClassTag<T> evidence$1) {
      this.org$apache$spark$rdd$AsyncRDDActions$$self = self;
      this.org$apache$spark$rdd$AsyncRDDActions$$evidence$1 = evidence$1;
      Logging$class.$init$(this);
   }
}
