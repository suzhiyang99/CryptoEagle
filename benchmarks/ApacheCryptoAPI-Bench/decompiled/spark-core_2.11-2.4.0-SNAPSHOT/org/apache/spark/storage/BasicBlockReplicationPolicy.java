package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.mutable.HashSet;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;
import scala.util.Random;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001Q3A!\u0001\u0002\u0001\u0017\tY\")Y:jG\ncwnY6SKBd\u0017nY1uS>t\u0007k\u001c7jGfT!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\t\u0001a!C\u0006\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M!R\"\u0001\u0002\n\u0005U\u0011!A\u0006\"m_\u000e\\'+\u001a9mS\u000e\fG/[8o!>d\u0017nY=\u0011\u0005]QR\"\u0001\r\u000b\u0005e!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005mA\"a\u0002'pO\u001eLgn\u001a\u0005\u0006;\u0001!\tAH\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003}\u0001\"a\u0005\u0001\t\u000b\u0005\u0002A\u0011\t\u0012\u0002\u0015A\u0014\u0018n\u001c:ji&TX\r\u0006\u0004$eQJ4\t\u0013\t\u0004I1zcBA\u0013+\u001d\t1\u0013&D\u0001(\u0015\tA#\"\u0001\u0004=e>|GOP\u0005\u0002\u001f%\u00111FD\u0001\ba\u0006\u001c7.Y4f\u0013\ticF\u0001\u0003MSN$(BA\u0016\u000f!\t\u0019\u0002'\u0003\u00022\u0005\tq!\t\\8dW6\u000bg.Y4fe&#\u0007\"B\u001a!\u0001\u0004y\u0013A\u00042m_\u000e\\W*\u00198bO\u0016\u0014\u0018\n\u001a\u0005\u0006k\u0001\u0002\rAN\u0001\u0006a\u0016,'o\u001d\t\u0004I]z\u0013B\u0001\u001d/\u0005\r\u0019V-\u001d\u0005\u0006u\u0001\u0002\raO\u0001\u0012a\u0016,'o\u001d*fa2L7-\u0019;fIR{\u0007c\u0001\u001fB_5\tQH\u0003\u0002?\u007f\u00059Q.\u001e;bE2,'B\u0001!\u000f\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003\u0005v\u0012q\u0001S1tQN+G\u000fC\u0003EA\u0001\u0007Q)A\u0004cY>\u001c7.\u00133\u0011\u0005M1\u0015BA$\u0003\u0005\u001d\u0011En\\2l\u0013\u0012DQ!\u0013\u0011A\u0002)\u000b1B\\;n%\u0016\u0004H.[2bgB\u0011QbS\u0005\u0003\u0019:\u00111!\u00138uQ\t\u0001a\n\u0005\u0002P%6\t\u0001K\u0003\u0002R\t\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005M\u0003&\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007"
)
public class BasicBlockReplicationPolicy implements BlockReplicationPolicy, Logging {
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

   public List<BlockManagerId> prioritize(BlockManagerId blockManagerId, Seq<BlockManagerId> peers, HashSet<BlockManagerId> peersReplicatedTo, BlockId blockId, int numReplicas) {
      this.logDebug(new Serializable(this, peers) {
         public static final long serialVersionUID = 0L;
         private final Seq peers$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Input peers : ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.peers$2}));
         }

         public {
            this.peers$2 = peers$2;
         }
      });
      this.logDebug(new Serializable(this, blockManagerId) {
         public static final long serialVersionUID = 0L;
         private final BlockManagerId blockManagerId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"BlockManagerId : ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockManagerId$1}));
         }

         public {
            this.blockManagerId$1 = blockManagerId$1;
         }
      });
      Random random = new Random(blockId.hashCode());
      List var10000;
      if (!blockManagerId.topologyInfo().isEmpty() && numReplicas != 0) {
         boolean doneWithinRack = peersReplicatedTo.exists(new Serializable(this, blockManagerId) {
            public static final long serialVersionUID = 0L;
            private final BlockManagerId blockManagerId$1;

            public final boolean apply(BlockManagerId x$3) {
               boolean var3;
               label23: {
                  Option var10000 = x$3.topologyInfo();
                  Option var2 = this.blockManagerId$1.topologyInfo();
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

            public {
               this.blockManagerId$1 = blockManagerId$1;
            }
         });
         boolean doneOutsideRack = peersReplicatedTo.exists(new Serializable(this, blockManagerId) {
            public static final long serialVersionUID = 0L;
            private final BlockManagerId blockManagerId$1;

            public final boolean apply(BlockManagerId p) {
               boolean var3;
               label25: {
                  if (p.topologyInfo().isDefined()) {
                     Option var10000 = p.topologyInfo();
                     Option var2 = this.blockManagerId$1.topologyInfo();
                     if (var10000 == null) {
                        if (var2 != null) {
                           break label25;
                        }
                     } else if (!var10000.equals(var2)) {
                        break label25;
                     }
                  }

                  var3 = false;
                  return var3;
               }

               var3 = true;
               return var3;
            }

            public {
               this.blockManagerId$1 = blockManagerId$1;
            }
         });
         if (doneOutsideRack && doneWithinRack) {
            var10000 = BlockReplicationUtils$.MODULE$.getRandomSample(peers, numReplicas, random);
         } else {
            Tuple2 var10 = ((TraversableLike)peers.filter(new Serializable(this, blockManagerId) {
               public static final long serialVersionUID = 0L;
               private final BlockManagerId blockManagerId$1;

               public final boolean apply(BlockManagerId x$4) {
                  boolean var3;
                  label23: {
                     String var10000 = x$4.host();
                     String var2 = this.blockManagerId$1.host();
                     if (var10000 == null) {
                        if (var2 == null) {
                           break label23;
                        }
                     } else if (var10000.equals(var2)) {
                        break label23;
                     }

                     var3 = true;
                     return var3;
                  }

                  var3 = false;
                  return var3;
               }

               public {
                  this.blockManagerId$1 = blockManagerId$1;
               }
            })).partition(new Serializable(this, blockManagerId) {
               public static final long serialVersionUID = 0L;
               private final BlockManagerId blockManagerId$1;

               public final boolean apply(BlockManagerId x$5) {
                  boolean var3;
                  label23: {
                     Option var10000 = x$5.topologyInfo();
                     Option var2 = this.blockManagerId$1.topologyInfo();
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

               public {
                  this.blockManagerId$1 = blockManagerId$1;
               }
            });
            if (var10 == null) {
               throw new MatchError(var10);
            }

            Seq inRackPeers = (Seq)var10._1();
            Seq outOfRackPeers = (Seq)var10._2();
            Tuple2 var13 = new Tuple2(inRackPeers, outOfRackPeers);
            Seq inRackPeers = (Seq)var13._1();
            Seq outOfRackPeers = (Seq)var13._2();
            Seq peerWithinRack = doneWithinRack ? (Seq)Seq$.MODULE$.empty() : (inRackPeers.isEmpty() ? (Seq)Seq$.MODULE$.empty() : (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new BlockManagerId[]{(BlockManagerId)inRackPeers.apply(random.nextInt(inRackPeers.size()))})));
            Seq peerOutsideRack = !doneOutsideRack && numReplicas - peerWithinRack.size() > 0 ? (outOfRackPeers.isEmpty() ? (Seq)Seq$.MODULE$.empty() : (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new BlockManagerId[]{(BlockManagerId)outOfRackPeers.apply(random.nextInt(outOfRackPeers.size()))}))) : (Seq)Seq$.MODULE$.empty();
            Seq priorityPeers = (Seq)peerWithinRack.$plus$plus(peerOutsideRack, Seq$.MODULE$.canBuildFrom());
            int numRemainingPeers = numReplicas - priorityPeers.size();
            Object var22;
            if (numRemainingPeers > 0) {
               Seq rPeers = (Seq)peers.filter(new Serializable(this, priorityPeers) {
                  public static final long serialVersionUID = 0L;
                  private final Seq priorityPeers$1;

                  public final boolean apply(BlockManagerId p) {
                     return !this.priorityPeers$1.contains(p);
                  }

                  public {
                     this.priorityPeers$1 = priorityPeers$1;
                  }
               });
               var22 = BlockReplicationUtils$.MODULE$.getRandomSample(rPeers, numRemainingPeers, random);
            } else {
               var22 = (Seq)Seq$.MODULE$.empty();
            }

            Seq remainingPeers = (Seq)var22;
            var10000 = ((TraversableOnce)priorityPeers.$plus$plus(remainingPeers, Seq$.MODULE$.canBuildFrom())).toList();
         }
      } else {
         var10000 = BlockReplicationUtils$.MODULE$.getRandomSample(peers, numReplicas, random);
      }

      return var10000;
   }

   public BasicBlockReplicationPolicy() {
      Logging$class.$init$(this);
   }
}
