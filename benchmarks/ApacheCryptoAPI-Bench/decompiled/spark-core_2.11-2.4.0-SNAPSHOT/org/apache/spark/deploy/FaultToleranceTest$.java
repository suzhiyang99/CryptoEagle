package org.apache.spark.deploy;

import java.util.concurrent.TimeoutException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.deploy.master.RecoveryState$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.ThreadUtils$;
import org.slf4j.Logger;
import scala.App;
import scala.App$class;
import scala.Enumeration;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.generic.TraversableForwarder;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Range;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.BufferLike;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.ListBuffer$;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.package;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.sys.package$;

public final class FaultToleranceTest$ implements App, Logging {
   public static final FaultToleranceTest$ MODULE$;
   private final SparkConf conf;
   private final String ZK_DIR;
   private final ListBuffer<TestMasterInfo> org$apache$spark$deploy$FaultToleranceTest$$masters;
   private final ListBuffer<TestWorkerInfo> org$apache$spark$deploy$FaultToleranceTest$$workers;
   private SparkContext org$apache$spark$deploy$FaultToleranceTest$$sc;
   private final CuratorFramework zk;
   private int org$apache$spark$deploy$FaultToleranceTest$$numPassed;
   private int org$apache$spark$deploy$FaultToleranceTest$$numFailed;
   private final String sparkHome;
   private final String containerSparkHome;
   private final String org$apache$spark$deploy$FaultToleranceTest$$dockerMountDir;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private final long executionStart;
   private String[] scala$App$$_args;
   private final ListBuffer<Function0<BoxedUnit>> scala$App$$initCode;

   static {
      new FaultToleranceTest$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

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

   public long executionStart() {
      return this.executionStart;
   }

   public String[] scala$App$$_args() {
      return this.scala$App$$_args;
   }

   public void scala$App$$_args_$eq(String[] x$1) {
      this.scala$App$$_args = x$1;
   }

   public ListBuffer<Function0<BoxedUnit>> scala$App$$initCode() {
      return this.scala$App$$initCode;
   }

   public void scala$App$_setter_$executionStart_$eq(long x$1) {
      this.executionStart = x$1;
   }

   public void scala$App$_setter_$scala$App$$initCode_$eq(ListBuffer x$1) {
      this.scala$App$$initCode = x$1;
   }

   public String[] args() {
      return App$class.args(this);
   }

   /** @deprecated */
   public void delayedInit(Function0<BoxedUnit> body) {
      App$class.delayedInit(this, body);
   }

   public void main(String[] args) {
      App$class.main(this, args);
   }

   private SparkConf conf() {
      return this.conf;
   }

   private String ZK_DIR() {
      return this.ZK_DIR;
   }

   public ListBuffer<TestMasterInfo> org$apache$spark$deploy$FaultToleranceTest$$masters() {
      return this.org$apache$spark$deploy$FaultToleranceTest$$masters;
   }

   public ListBuffer<TestWorkerInfo> org$apache$spark$deploy$FaultToleranceTest$$workers() {
      return this.org$apache$spark$deploy$FaultToleranceTest$$workers;
   }

   public SparkContext org$apache$spark$deploy$FaultToleranceTest$$sc() {
      return this.org$apache$spark$deploy$FaultToleranceTest$$sc;
   }

   private void org$apache$spark$deploy$FaultToleranceTest$$sc_$eq(SparkContext x$1) {
      this.org$apache$spark$deploy$FaultToleranceTest$$sc = x$1;
   }

   private CuratorFramework zk() {
      return this.zk;
   }

   public int org$apache$spark$deploy$FaultToleranceTest$$numPassed() {
      return this.org$apache$spark$deploy$FaultToleranceTest$$numPassed;
   }

   private void org$apache$spark$deploy$FaultToleranceTest$$numPassed_$eq(int x$1) {
      this.org$apache$spark$deploy$FaultToleranceTest$$numPassed = x$1;
   }

   public int org$apache$spark$deploy$FaultToleranceTest$$numFailed() {
      return this.org$apache$spark$deploy$FaultToleranceTest$$numFailed;
   }

   private void org$apache$spark$deploy$FaultToleranceTest$$numFailed_$eq(int x$1) {
      this.org$apache$spark$deploy$FaultToleranceTest$$numFailed = x$1;
   }

   private String sparkHome() {
      return this.sparkHome;
   }

   private String containerSparkHome() {
      return this.containerSparkHome;
   }

   public String org$apache$spark$deploy$FaultToleranceTest$$dockerMountDir() {
      return this.org$apache$spark$deploy$FaultToleranceTest$$dockerMountDir;
   }

   private void afterEach() {
      if (this.org$apache$spark$deploy$FaultToleranceTest$$sc() != null) {
         this.org$apache$spark$deploy$FaultToleranceTest$$sc().stop();
         this.org$apache$spark$deploy$FaultToleranceTest$$sc_$eq((SparkContext)null);
      }

      this.org$apache$spark$deploy$FaultToleranceTest$$terminateCluster();
      SparkCuratorUtil$.MODULE$.deleteRecursive(this.zk(), (new StringBuilder()).append((Object)this.ZK_DIR()).append((Object)"/spark_leader").toString());
      SparkCuratorUtil$.MODULE$.deleteRecursive(this.zk(), (new StringBuilder()).append((Object)this.ZK_DIR()).append((Object)"/master_status").toString());
   }

   private void test(String name, Function0<BoxedUnit> fn) {
      try {
         fn.apply$mcV$sp();
         this.org$apache$spark$deploy$FaultToleranceTest$$numPassed_$eq(this.org$apache$spark$deploy$FaultToleranceTest$$numPassed() + 1);
         this.logInfo(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "==============================================";
            }
         });
         this.logInfo(new Serializable(name) {
            public static final long serialVersionUID = 0L;
            private final String name$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Passed: ").append((Object)this.name$1).toString();
            }

            public {
               this.name$1 = name$1;
            }
         });
         this.logInfo(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "==============================================";
            }
         });
      } catch (Exception var4) {
         this.org$apache$spark$deploy$FaultToleranceTest$$numFailed_$eq(this.org$apache$spark$deploy$FaultToleranceTest$$numFailed() + 1);
         this.logInfo(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";
            }
         });
         this.logError(new Serializable(name) {
            public static final long serialVersionUID = 0L;
            private final String name$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"FAILED: ").append((Object)this.name$1).toString();
            }

            public {
               this.name$1 = name$1;
            }
         }, var4);
         this.logInfo(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";
            }
         });
         throw package$.MODULE$.exit(1);
      }

      this.afterEach();
   }

   public void org$apache$spark$deploy$FaultToleranceTest$$addMasters(int num) {
      this.logInfo(new Serializable(num) {
         public static final long serialVersionUID = 0L;
         private final int num$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{">>>>> ADD MASTERS ", " <<<<<"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.num$1)}));
         }

         public {
            this.num$1 = num$1;
         }
      });
      RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), num).foreach(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final ListBuffer<TestMasterInfo> apply(int x$3) {
            return FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$masters().$plus$eq(SparkDocker$.MODULE$.startMaster(FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$dockerMountDir()));
         }
      });
   }

   public void org$apache$spark$deploy$FaultToleranceTest$$addWorkers(int num) {
      this.logInfo(new Serializable(num) {
         public static final long serialVersionUID = 0L;
         private final int num$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{">>>>> ADD WORKERS ", " <<<<<"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.num$2)}));
         }

         public {
            this.num$2 = num$2;
         }
      });
      String masterUrls = this.getMasterUrls(this.org$apache$spark$deploy$FaultToleranceTest$$masters());
      RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), num).foreach(new Serializable(masterUrls) {
         public static final long serialVersionUID = 0L;
         private final String masterUrls$1;

         public final ListBuffer<TestWorkerInfo> apply(int x$4) {
            return FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$workers().$plus$eq(SparkDocker$.MODULE$.startWorker(FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$dockerMountDir(), this.masterUrls$1));
         }

         public {
            this.masterUrls$1 = masterUrls$1;
         }
      });
   }

   public void org$apache$spark$deploy$FaultToleranceTest$$createClient() {
      this.logInfo(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return ">>>>> CREATE CLIENT <<<<<";
         }
      });
      if (this.org$apache$spark$deploy$FaultToleranceTest$$sc() != null) {
         this.org$apache$spark$deploy$FaultToleranceTest$$sc().stop();
      }

      System.setProperty("spark.driver.port", "0");
      this.org$apache$spark$deploy$FaultToleranceTest$$sc_$eq(new SparkContext(this.getMasterUrls(this.org$apache$spark$deploy$FaultToleranceTest$$masters()), "fault-tolerance", this.containerSparkHome()));
   }

   private String getMasterUrls(Seq<TestMasterInfo> masters) {
      return (new StringBuilder()).append((Object)"spark://").append((Object)((TraversableOnce)masters.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(TestMasterInfo master) {
            return (new StringBuilder()).append((Object)master.ip()).append((Object)":7077").toString();
         }
      }, Seq$.MODULE$.canBuildFrom())).mkString(",")).toString();
   }

   public TestMasterInfo org$apache$spark$deploy$FaultToleranceTest$$getLeader() {
      ListBuffer leaders = (ListBuffer)this.org$apache$spark$deploy$FaultToleranceTest$$masters().filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(TestMasterInfo x$5) {
            boolean var3;
            label23: {
               Enumeration.Value var10000 = x$5.state();
               Enumeration.Value var2 = RecoveryState$.MODULE$.ALIVE();
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }
      });
      this.org$apache$spark$deploy$FaultToleranceTest$$assertTrue(leaders.size() == 1, this.org$apache$spark$deploy$FaultToleranceTest$$assertTrue$default$2());
      return (TestMasterInfo)leaders.apply(0);
   }

   public void org$apache$spark$deploy$FaultToleranceTest$$killLeader() {
      this.logInfo(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return ">>>>> KILL LEADER <<<<<";
         }
      });
      this.org$apache$spark$deploy$FaultToleranceTest$$masters().foreach(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply(TestMasterInfo x$6) {
            x$6.readState();
         }
      });
      TestMasterInfo leader = this.org$apache$spark$deploy$FaultToleranceTest$$getLeader();
      this.org$apache$spark$deploy$FaultToleranceTest$$masters().$minus$eq(leader);
      leader.kill();
   }

   public void org$apache$spark$deploy$FaultToleranceTest$$delay(Duration secs) {
      Thread.sleep(secs.toMillis());
   }

   public Duration org$apache$spark$deploy$FaultToleranceTest$$delay$default$1() {
      return (new package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(5))).seconds();
   }

   public void org$apache$spark$deploy$FaultToleranceTest$$terminateCluster() {
      this.logInfo(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return ">>>>> TERMINATE CLUSTER <<<<<";
         }
      });
      this.org$apache$spark$deploy$FaultToleranceTest$$masters().foreach(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply(TestMasterInfo x$7) {
            x$7.kill();
         }
      });
      this.org$apache$spark$deploy$FaultToleranceTest$$workers().foreach(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply(TestWorkerInfo x$8) {
            x$8.kill();
         }
      });
      this.org$apache$spark$deploy$FaultToleranceTest$$masters().clear();
      this.org$apache$spark$deploy$FaultToleranceTest$$workers().clear();
   }

   private void assertUsable() {
      Future f = Future$.MODULE$.apply(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            boolean var10000;
            try {
               boolean var9;
               label21: {
                  label20: {
                     SparkContext qual$1 = FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$sc();
                     Range x$15 = RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), 10);
                     int x$16 = qual$1.parallelize$default$2();
                     int[] res = (int[])qual$1.parallelize(x$15, x$16, ClassTag$.MODULE$.Int()).collect();
                     var8 = FaultToleranceTest$.MODULE$;
                     List var10001 = Predef$.MODULE$.intArrayOps(res).toList();
                     List var6 = RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), 10).toList();
                     if (var10001 == null) {
                        if (var6 == null) {
                           break label20;
                        }
                     } else if (var10001.equals(var6)) {
                        break label20;
                     }

                     var9 = false;
                     break label21;
                  }

                  var9 = true;
               }

               var8.org$apache$spark$deploy$FaultToleranceTest$$assertTrue(var9, FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertTrue$default$2());
               var10000 = true;
            } catch (Exception var7) {
               FaultToleranceTest$.MODULE$.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "assertUsable() had exception";
                  }
               }, var7);
               var7.printStackTrace();
               var10000 = false;
            }

            return var10000;
         }
      }, ExecutionContext.Implicits$.MODULE$.global());
      this.org$apache$spark$deploy$FaultToleranceTest$$assertTrue(BoxesRunTime.unboxToBoolean(ThreadUtils$.MODULE$.awaitResult(f, (new package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(120))).seconds())), this.org$apache$spark$deploy$FaultToleranceTest$$assertTrue$default$2());
   }

   public void org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState() {
      this.logInfo(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return ">>>>> ASSERT VALID CLUSTER STATE <<<<<";
         }
      });
      this.assertUsable();
      IntRef numAlive = IntRef.create(0);
      IntRef numStandby = IntRef.create(0);
      IntRef numLiveApps = IntRef.create(0);
      ObjectRef liveWorkerIPs = ObjectRef.create(Nil$.MODULE$);
      Future f = Future$.MODULE$.apply(new Serializable(numAlive, numStandby, numLiveApps, liveWorkerIPs) {
         public static final long serialVersionUID = 0L;
         public final IntRef numAlive$1;
         public final IntRef numStandby$1;
         public final IntRef numLiveApps$1;
         public final ObjectRef liveWorkerIPs$1;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            boolean var10000;
            try {
               while(true) {
                  if (!FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$stateValid$1(this.numAlive$1, this.numStandby$1, this.numLiveApps$1, this.liveWorkerIPs$1)) {
                     Thread.sleep(1000L);
                     this.numAlive$1.elem = 0;
                     this.numStandby$1.elem = 0;
                     this.numLiveApps$1.elem = 0;
                     FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$masters().foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final void apply(TestMasterInfo x$10) {
                           x$10.readState();
                        }
                     });
                     FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$masters().foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final void apply(TestMasterInfo master) {
                           label29: {
                              label31: {
                                 Enumeration.Value var2 = master.state();
                                 Enumeration.Value var10000 = RecoveryState$.MODULE$.ALIVE();
                                 if (var10000 == null) {
                                    if (var2 == null) {
                                       break label31;
                                    }
                                 } else if (var10000.equals(var2)) {
                                    break label31;
                                 }

                                 label21: {
                                    var10000 = RecoveryState$.MODULE$.STANDBY();
                                    if (var10000 == null) {
                                       if (var2 == null) {
                                          break label21;
                                       }
                                    } else if (var10000.equals(var2)) {
                                       break label21;
                                    }

                                    BoxedUnit var4 = BoxedUnit.UNIT;
                                    break label29;
                                 }

                                 ++this.$outer.numStandby$1.elem;
                                 BoxedUnit var6 = BoxedUnit.UNIT;
                                 break label29;
                              }

                              ++this.$outer.numAlive$1.elem;
                              this.$outer.liveWorkerIPs$1.elem = (T)master.liveWorkerIPs();
                              BoxedUnit var7 = BoxedUnit.UNIT;
                           }

                           this.$outer.numLiveApps$1.elem += master.numLiveApps();
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                  } else {
                     var10000 = true;
                     break;
                  }
               }
            } catch (Exception var2) {
               FaultToleranceTest$.MODULE$.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "assertValidClusterState() had exception";
                  }
               }, var2);
               var10000 = false;
            }

            return var10000;
         }

         public {
            this.numAlive$1 = numAlive$1;
            this.numStandby$1 = numStandby$1;
            this.numLiveApps$1 = numLiveApps$1;
            this.liveWorkerIPs$1 = liveWorkerIPs$1;
         }
      }, ExecutionContext.Implicits$.MODULE$.global());

      try {
         this.org$apache$spark$deploy$FaultToleranceTest$$assertTrue(BoxesRunTime.unboxToBoolean(ThreadUtils$.MODULE$.awaitResult(f, (new package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(120))).seconds())), this.org$apache$spark$deploy$FaultToleranceTest$$assertTrue$default$2());
      } catch (TimeoutException var7) {
         this.logError(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Master states: ").append(FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$masters().map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Enumeration.Value apply(TestMasterInfo x$11) {
                     return x$11.state();
                  }
               }, ListBuffer$.MODULE$.canBuildFrom())).toString();
            }
         });
         this.logError(new Serializable(numLiveApps) {
            public static final long serialVersionUID = 0L;
            private final IntRef numLiveApps$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Num apps: ").append((Object)BoxesRunTime.boxToInteger(this.numLiveApps$1.elem)).toString();
            }

            public {
               this.numLiveApps$1 = numLiveApps$1;
            }
         });
         this.logError(new Serializable(liveWorkerIPs) {
            public static final long serialVersionUID = 0L;
            private final ObjectRef liveWorkerIPs$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"IPs expected: ").append(FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$workers().map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply(TestWorkerInfo x$12) {
                     return x$12.ip();
                  }
               }, ListBuffer$.MODULE$.canBuildFrom())).append((Object)" / found: ").append((Object)((Seq)this.liveWorkerIPs$1.elem)).toString();
            }

            public {
               this.liveWorkerIPs$1 = liveWorkerIPs$1;
            }
         });
         throw new RuntimeException("Failed to get into acceptable cluster state after 2 min.", var7);
      }
   }

   public void org$apache$spark$deploy$FaultToleranceTest$$assertTrue(boolean bool, String message) {
      if (!bool) {
         throw new IllegalStateException((new StringBuilder()).append((Object)"Assertion failed: ").append((Object)message).toString());
      }
   }

   public String org$apache$spark$deploy$FaultToleranceTest$$assertTrue$default$2() {
      return "";
   }

   public final boolean org$apache$spark$deploy$FaultToleranceTest$$stateValid$1(IntRef numAlive$1, IntRef numStandby$1, IntRef numLiveApps$1, ObjectRef liveWorkerIPs$1) {
      return ((TraversableForwarder)((BufferLike)this.org$apache$spark$deploy$FaultToleranceTest$$workers().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(TestWorkerInfo x$9) {
            return x$9.ip();
         }
      }, ListBuffer$.MODULE$.canBuildFrom())).$minus$minus((Seq)liveWorkerIPs$1.elem)).isEmpty() && numAlive$1.elem == 1 && numStandby$1.elem == this.org$apache$spark$deploy$FaultToleranceTest$$masters().size() - 1 && numLiveApps$1.elem >= 1;
   }

   public final void delayedEndpoint$org$apache$spark$deploy$FaultToleranceTest$1() {
      this.conf = new SparkConf();
      this.ZK_DIR = this.conf().get("spark.deploy.zookeeper.dir", "/spark");
      this.org$apache$spark$deploy$FaultToleranceTest$$masters = (ListBuffer)ListBuffer$.MODULE$.apply(Nil$.MODULE$);
      this.org$apache$spark$deploy$FaultToleranceTest$$workers = (ListBuffer)ListBuffer$.MODULE$.apply(Nil$.MODULE$);
      this.zk = SparkCuratorUtil$.MODULE$.newClient(this.conf(), SparkCuratorUtil$.MODULE$.newClient$default$2());
      this.org$apache$spark$deploy$FaultToleranceTest$$numPassed = 0;
      this.org$apache$spark$deploy$FaultToleranceTest$$numFailed = 0;
      this.sparkHome = System.getenv("SPARK_HOME");
      this.org$apache$spark$deploy$FaultToleranceTest$$assertTrue(this.sparkHome() != null, "Run with a valid SPARK_HOME");
      this.containerSparkHome = "/opt/spark";
      this.org$apache$spark$deploy$FaultToleranceTest$$dockerMountDir = (new StringOps(Predef$.MODULE$.augmentString("%s:%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.sparkHome(), this.containerSparkHome()}));
      System.setProperty("spark.driver.host", "172.17.42.1");
      this.test("sanity-basic", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(1);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addWorkers(1);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$createClient();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
         }
      });
      this.test("sanity-many-masters", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(3);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addWorkers(3);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$createClient();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
         }
      });
      this.test("single-master-halt", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(3);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addWorkers(2);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$createClient();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$killLeader();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$delay((new package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(30))).seconds());
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$createClient();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
         }
      });
      this.test("single-master-restart", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(1);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addWorkers(2);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$createClient();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$killLeader();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(1);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$delay((new package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(30))).seconds());
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$killLeader();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(1);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$delay((new package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(30))).seconds());
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
         }
      });
      this.test("cluster-failure", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(2);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addWorkers(2);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$createClient();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$terminateCluster();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(2);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addWorkers(2);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
         }
      });
      this.test("all-but-standby-failure", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(2);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addWorkers(2);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$createClient();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$killLeader();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$workers().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(TestWorkerInfo x$1) {
                  x$1.kill();
               }
            });
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$workers().clear();
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$delay((new package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(30))).seconds());
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addWorkers(2);
            FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
         }
      });
      this.test("rolling-outage", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            boolean var2;
            FaultToleranceTest$ var10000;
            label17: {
               label16: {
                  FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(1);
                  FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$delay(FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$delay$default$1());
                  FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(1);
                  FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$delay(FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$delay$default$1());
                  FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(1);
                  FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addWorkers(2);
                  FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$createClient();
                  FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
                  var10000 = FaultToleranceTest$.MODULE$;
                  TestMasterInfo var10001 = FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$getLeader();
                  Object var1 = FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$masters().head();
                  if (var10001 == null) {
                     if (var1 == null) {
                        break label16;
                     }
                  } else if (var10001.equals(var1)) {
                     break label16;
                  }

                  var2 = false;
                  break label17;
               }

               var2 = true;
            }

            var10000.org$apache$spark$deploy$FaultToleranceTest$$assertTrue(var2, FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertTrue$default$2());
            RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), 3).foreach$mVc$sp(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(int x$2) {
                  this.apply$mcVI$sp(x$2);
               }

               public void apply$mcVI$sp(int x$2) {
                  boolean var3;
                  FaultToleranceTest$ var10000;
                  label17: {
                     label16: {
                        FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$killLeader();
                        FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$delay((new package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(30))).seconds());
                        FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertValidClusterState();
                        var10000 = FaultToleranceTest$.MODULE$;
                        TestMasterInfo var10001 = FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$getLeader();
                        Object var2 = FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$masters().head();
                        if (var10001 == null) {
                           if (var2 == null) {
                              break label16;
                           }
                        } else if (var10001.equals(var2)) {
                           break label16;
                        }

                        var3 = false;
                        break label17;
                     }

                     var3 = true;
                  }

                  var10000.org$apache$spark$deploy$FaultToleranceTest$$assertTrue(var3, FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$assertTrue$default$2());
                  FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$addMasters(1);
               }
            });
         }
      });
      this.logInfo(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Ran %s tests, %s passed and %s failed"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$numPassed() + FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$numFailed()), BoxesRunTime.boxToInteger(FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$numPassed()), BoxesRunTime.boxToInteger(FaultToleranceTest$.MODULE$.org$apache$spark$deploy$FaultToleranceTest$$numFailed())}));
         }
      });
   }

   private FaultToleranceTest$() {
      MODULE$ = this;
      App$class.$init$(this);
      Logging$class.$init$(this);
      this.delayedInit(new FaultToleranceTest$delayedInit$body(this));
   }
}
