package org.apache.spark.storage;

import java.util.concurrent.ThreadPoolExecutor;
import org.apache.spark.MapOutputTracker;
import org.apache.spark.SparkEnv$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.PartialFunction;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.ExecutionContext$;
import scala.concurrent.ExecutionContextExecutorService;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015a!B\u0001\u0003\u0001\tQ!!\u0007\"m_\u000e\\W*\u00198bO\u0016\u00148\u000b\\1wK\u0016sG\r]8j]RT!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0017E9\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\u0013+5\t1C\u0003\u0002\u0015\t\u0005\u0019!\u000f]2\n\u0005Y\u0019\"!\u0006+ie\u0016\fGmU1gKJ\u00038-\u00128ea>Lg\u000e\u001e\t\u00031mi\u0011!\u0007\u0006\u00035\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00039e\u0011q\u0001T8hO&tw\r\u0003\u0005\u001f\u0001\t\u0015\r\u0011\"\u0011!\u0003\u0019\u0011\boY#om\u000e\u0001Q#A\u0011\u0011\u0005I\u0011\u0013BA\u0012\u0014\u0005\u0019\u0011\u0006oY#om\"AQ\u0005\u0001B\u0001B\u0003%\u0011%A\u0004sa\u000e,eN\u001e\u0011\t\u0011\u001d\u0002!\u0011!Q\u0001\n!\nAB\u00197pG.l\u0015M\\1hKJ\u0004\"!\u000b\u0016\u000e\u0003\tI!a\u000b\u0002\u0003\u0019\tcwnY6NC:\fw-\u001a:\t\u00115\u0002!\u0011!Q\u0001\n9\n\u0001#\\1q\u001fV$\b/\u001e;Ue\u0006\u001c7.\u001a:\u0011\u0005=\u0002T\"\u0001\u0003\n\u0005E\"!\u0001E'ba>+H\u000f];u)J\f7m[3s\u0011\u0015\u0019\u0004\u0001\"\u00015\u0003\u0019a\u0014N\\5u}Q!QGN\u001c9!\tI\u0003\u0001C\u0003\u001fe\u0001\u0007\u0011\u0005C\u0003(e\u0001\u0007\u0001\u0006C\u0003.e\u0001\u0007a\u0006C\u0004;\u0001\t\u0007I\u0011B\u001e\u0002\u001f\u0005\u001c\u0018P\\2UQJ,\u0017\r\u001a)p_2,\u0012\u0001\u0010\t\u0003{\u0011k\u0011A\u0010\u0006\u0003\u007f\u0001\u000b!bY8oGV\u0014(/\u001a8u\u0015\t\t%)\u0001\u0003vi&d'\"A\"\u0002\t)\fg/Y\u0005\u0003\u000bz\u0012!\u0003\u00165sK\u0006$\u0007k\\8m\u000bb,7-\u001e;pe\"1q\t\u0001Q\u0001\nq\n\u0001#Y:z]\u000e$\u0006N]3bIB{w\u000e\u001c\u0011\t\u000f%\u0003!\u0019!C\u0006\u0015\u0006)\u0012m]=oG\u0016CXmY;uS>t7i\u001c8uKb$X#A&\u0011\u00051sU\"A'\u000b\u0005}j\u0011BA(N\u0005})\u00050Z2vi&|gnQ8oi\u0016DH/\u0012=fGV$xN]*feZL7-\u001a\u0005\u0007#\u0002\u0001\u000b\u0011B&\u0002-\u0005\u001c\u0018P\\2Fq\u0016\u001cW\u000f^5p]\u000e{g\u000e^3yi\u0002BQa\u0015\u0001\u0005BQ\u000bqB]3dK&4X-\u00118e%\u0016\u0004H.\u001f\u000b\u0003+z\u0003B\u0001\u0004,Y7&\u0011q+\u0004\u0002\u0010!\u0006\u0014H/[1m\rVt7\r^5p]B\u0011A\"W\u0005\u000356\u00111!\u00118z!\taA,\u0003\u0002^\u001b\t!QK\\5u\u0011\u0015y&\u000b1\u0001a\u0003\u001d\u0019wN\u001c;fqR\u0004\"AE1\n\u0005\t\u001c\"A\u0004*qG\u000e\u000bG\u000e\\\"p]R,\u0007\u0010\u001e\u0005\u0006I\u0002!I!Z\u0001\bI>\f5/\u001f8d+\t1w\u000eF\u0002hkz$\"a\u00175\t\r%\u001cG\u00111\u0001k\u0003\u0011\u0011w\u000eZ=\u0011\u00071YW.\u0003\u0002m\u001b\tAAHY=oC6,g\b\u0005\u0002o_2\u0001A!\u00029d\u0005\u0004\t(!\u0001+\u0012\u0005ID\u0006C\u0001\u0007t\u0013\t!XBA\u0004O_RD\u0017N\\4\t\u000bY\u001c\u0007\u0019A<\u0002\u001b\u0005\u001cG/[8o\u001b\u0016\u001c8/Y4f!\tA8P\u0004\u0002\rs&\u0011!0D\u0001\u0007!J,G-\u001a4\n\u0005ql(AB*ue&twM\u0003\u0002{\u001b!)ql\u0019a\u0001A\"9\u0011\u0011\u0001\u0001\u0005B\u0005\r\u0011AB8o'R|\u0007\u000fF\u0001\\\u0001"
)
public class BlockManagerSlaveEndpoint implements ThreadSafeRpcEndpoint, Logging {
   private final RpcEnv rpcEnv;
   public final BlockManager org$apache$spark$storage$BlockManagerSlaveEndpoint$$blockManager;
   public final MapOutputTracker org$apache$spark$storage$BlockManagerSlaveEndpoint$$mapOutputTracker;
   private final ThreadPoolExecutor asyncThreadPool;
   private final ExecutionContextExecutorService asyncExecutionContext;
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

   public final RpcEndpointRef self() {
      return RpcEndpoint$class.self(this);
   }

   public PartialFunction<Object, BoxedUnit> receive() {
      return RpcEndpoint$class.receive(this);
   }

   public void onError(Throwable cause) {
      RpcEndpoint$class.onError(this, cause);
   }

   public void onConnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onConnected(this, remoteAddress);
   }

   public void onDisconnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onDisconnected(this, remoteAddress);
   }

   public void onNetworkError(Throwable cause, RpcAddress remoteAddress) {
      RpcEndpoint$class.onNetworkError(this, cause, remoteAddress);
   }

   public void onStart() {
      RpcEndpoint$class.onStart(this);
   }

   public final void stop() {
      RpcEndpoint$class.stop(this);
   }

   public RpcEnv rpcEnv() {
      return this.rpcEnv;
   }

   private ThreadPoolExecutor asyncThreadPool() {
      return this.asyncThreadPool;
   }

   private ExecutionContextExecutorService asyncExecutionContext() {
      return this.asyncExecutionContext;
   }

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return new Serializable(this, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerSlaveEndpoint $outer;
         private final RpcCallContext context$1;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var6;
            if (x1 instanceof BlockManagerMessages.RemoveBlock) {
               BlockManagerMessages.RemoveBlock var4 = (BlockManagerMessages.RemoveBlock)x1;
               BlockId blockId = var4.blockId();
               this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$doAsync((new StringBuilder()).append((Object)"removing block ").append((Object)blockId).toString(), this.context$1, new Serializable(this, blockId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final BlockId blockId$1;

                  public final boolean apply() {
                     return this.apply$mcZ$sp();
                  }

                  public boolean apply$mcZ$sp() {
                     this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$anonfun$$$outer().org$apache$spark$storage$BlockManagerSlaveEndpoint$$blockManager.removeBlock(this.blockId$1, this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$anonfun$$$outer().org$apache$spark$storage$BlockManagerSlaveEndpoint$$blockManager.removeBlock$default$2());
                     return true;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.blockId$1 = blockId$1;
                     }
                  }
               });
               var6 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.RemoveRdd) {
               BlockManagerMessages.RemoveRdd var7 = (BlockManagerMessages.RemoveRdd)x1;
               int rddId = var7.rddId();
               this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$doAsync((new StringBuilder()).append((Object)"removing RDD ").append((Object)BoxesRunTime.boxToInteger(rddId)).toString(), this.context$1, new Serializable(this, rddId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int rddId$1;

                  public final int apply() {
                     return this.apply$mcI$sp();
                  }

                  public int apply$mcI$sp() {
                     return this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$anonfun$$$outer().org$apache$spark$storage$BlockManagerSlaveEndpoint$$blockManager.removeRdd(this.rddId$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.rddId$1 = rddId$1;
                     }
                  }
               });
               var6 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.RemoveShuffle) {
               BlockManagerMessages.RemoveShuffle var9 = (BlockManagerMessages.RemoveShuffle)x1;
               int shuffleId = var9.shuffleId();
               this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$doAsync((new StringBuilder()).append((Object)"removing shuffle ").append((Object)BoxesRunTime.boxToInteger(shuffleId)).toString(), this.context$1, new Serializable(this, shuffleId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int shuffleId$1;

                  public final boolean apply() {
                     return this.apply$mcZ$sp();
                  }

                  public boolean apply$mcZ$sp() {
                     if (this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$anonfun$$$outer().org$apache$spark$storage$BlockManagerSlaveEndpoint$$mapOutputTracker != null) {
                        this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$anonfun$$$outer().org$apache$spark$storage$BlockManagerSlaveEndpoint$$mapOutputTracker.unregisterShuffle(this.shuffleId$1);
                     }

                     return SparkEnv$.MODULE$.get().shuffleManager().unregisterShuffle(this.shuffleId$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.shuffleId$1 = shuffleId$1;
                     }
                  }
               });
               var6 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.RemoveBroadcast) {
               BlockManagerMessages.RemoveBroadcast var11 = (BlockManagerMessages.RemoveBroadcast)x1;
               long broadcastId = var11.broadcastId();
               this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$doAsync((new StringBuilder()).append((Object)"removing broadcast ").append((Object)BoxesRunTime.boxToLong(broadcastId)).toString(), this.context$1, new Serializable(this, broadcastId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final long broadcastId$1;

                  public final int apply() {
                     return this.apply$mcI$sp();
                  }

                  public int apply$mcI$sp() {
                     return this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$anonfun$$$outer().org$apache$spark$storage$BlockManagerSlaveEndpoint$$blockManager.removeBroadcast(this.broadcastId$1, true);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.broadcastId$1 = broadcastId$1;
                     }
                  }
               });
               var6 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.GetBlockStatus) {
               BlockManagerMessages.GetBlockStatus var14 = (BlockManagerMessages.GetBlockStatus)x1;
               BlockId blockId = var14.blockId();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$blockManager.getStatus(blockId));
               var6 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.GetMatchingBlockIds) {
               BlockManagerMessages.GetMatchingBlockIds var16 = (BlockManagerMessages.GetMatchingBlockIds)x1;
               Function1 filter = var16.filter();
               this.context$1.reply(this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$blockManager.getMatchingBlockIds(filter));
               var6 = BoxedUnit.UNIT;
            } else if (BlockManagerMessages.TriggerThreadDump$.MODULE$.equals(x1)) {
               this.context$1.reply(Utils$.MODULE$.getThreadDump());
               var6 = BoxedUnit.UNIT;
            } else if (x1 instanceof BlockManagerMessages.ReplicateBlock) {
               BlockManagerMessages.ReplicateBlock var18 = (BlockManagerMessages.ReplicateBlock)x1;
               BlockId blockId = var18.blockId();
               Seq replicas = var18.replicas();
               int maxReplicas = var18.maxReplicas();
               RpcCallContext var10000 = this.context$1;
               this.$outer.org$apache$spark$storage$BlockManagerSlaveEndpoint$$blockManager.replicateBlock(blockId, replicas.toSet(), maxReplicas);
               var10000.reply(BoxedUnit.UNIT);
               var6 = BoxedUnit.UNIT;
            } else {
               var6 = var2.apply(x1);
            }

            return (B1)var6;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (x1 instanceof BlockManagerMessages.RemoveBlock) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.RemoveRdd) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.RemoveShuffle) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.RemoveBroadcast) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.GetBlockStatus) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.GetMatchingBlockIds) {
               var3 = true;
            } else if (BlockManagerMessages.TriggerThreadDump$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof BlockManagerMessages.ReplicateBlock) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }

         // $FF: synthetic method
         public BlockManagerSlaveEndpoint org$apache$spark$storage$BlockManagerSlaveEndpoint$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.context$1 = context$1;
            }
         }
      };
   }

   public <T> void org$apache$spark$storage$BlockManagerSlaveEndpoint$$doAsync(String actionMessage, RpcCallContext context, Function0<T> body) {
      Future future = Future$.MODULE$.apply(new Serializable(this, actionMessage, body) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerSlaveEndpoint $outer;
         public final String actionMessage$1;
         private final Function0 body$1;

         public final T apply() {
            this.$outer.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return this.$outer.actionMessage$1;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            return (T)this.body$1.apply();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.actionMessage$1 = actionMessage$1;
               this.body$1 = body$1;
            }
         }
      }, this.asyncExecutionContext());
      future.foreach(new Serializable(this, actionMessage, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerSlaveEndpoint $outer;
         public final String actionMessage$1;
         public final RpcCallContext context$2;

         public final void apply(T response) {
            this.$outer.logDebug(new Serializable(this, response) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Object response$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Done ", ", response is ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.actionMessage$1, this.response$1}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.response$1 = response$1;
                  }
               }
            });
            this.context$2.reply(response);
            this.$outer.logDebug(new Serializable(this, response) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Object response$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sent response: ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.response$1, this.$outer.context$2.senderAddress()}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.response$1 = response$1;
                  }
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.actionMessage$1 = actionMessage$1;
               this.context$2 = context$2;
            }
         }
      }, this.asyncExecutionContext());
      future.failed().foreach(new Serializable(this, actionMessage, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManagerSlaveEndpoint $outer;
         public final String actionMessage$1;
         private final RpcCallContext context$2;

         public final void apply(Throwable t) {
            this.$outer.logError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error in ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.actionMessage$1}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, t);
            this.context$2.sendFailure(t);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.actionMessage$1 = actionMessage$1;
               this.context$2 = context$2;
            }
         }
      }, this.asyncExecutionContext());
   }

   public void onStop() {
      this.asyncThreadPool().shutdownNow();
   }

   public BlockManagerSlaveEndpoint(RpcEnv rpcEnv, BlockManager blockManager, MapOutputTracker mapOutputTracker) {
      this.rpcEnv = rpcEnv;
      this.org$apache$spark$storage$BlockManagerSlaveEndpoint$$blockManager = blockManager;
      this.org$apache$spark$storage$BlockManagerSlaveEndpoint$$mapOutputTracker = mapOutputTracker;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      this.asyncThreadPool = ThreadUtils$.MODULE$.newDaemonCachedThreadPool("block-manager-slave-async-thread-pool");
      this.asyncExecutionContext = ExecutionContext$.MODULE$.fromExecutorService(this.asyncThreadPool());
   }
}
