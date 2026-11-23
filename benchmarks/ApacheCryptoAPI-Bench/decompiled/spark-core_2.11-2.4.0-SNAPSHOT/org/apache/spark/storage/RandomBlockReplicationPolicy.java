package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.List;
import scala.collection.mutable.HashSet;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;
import scala.util.Random;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001Q3A!\u0001\u0002\u0001\u0017\ta\"+\u00198e_6\u0014En\\2l%\u0016\u0004H.[2bi&|g\u000eU8mS\u000eL(BA\u0002\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\u0011\u0001AB\u0005\f\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\t\u0019B#D\u0001\u0003\u0013\t)\"A\u0001\fCY>\u001c7NU3qY&\u001c\u0017\r^5p]B{G.[2z!\t9\"$D\u0001\u0019\u0015\tIB!\u0001\u0005j]R,'O\\1m\u0013\tY\u0002DA\u0004M_\u001e<\u0017N\\4\t\u000bu\u0001A\u0011\u0001\u0010\u0002\rqJg.\u001b;?)\u0005y\u0002CA\n\u0001\u0011\u0015\t\u0003\u0001\"\u0011#\u0003)\u0001(/[8sSRL'0\u001a\u000b\u0007GI\"\u0014h\u0011%\u0011\u0007\u0011bsF\u0004\u0002&U9\u0011a%K\u0007\u0002O)\u0011\u0001FC\u0001\u0007yI|w\u000e\u001e \n\u0003=I!a\u000b\b\u0002\u000fA\f7m[1hK&\u0011QF\f\u0002\u0005\u0019&\u001cHO\u0003\u0002,\u001dA\u00111\u0003M\u0005\u0003c\t\u0011aB\u00117pG.l\u0015M\\1hKJLE\rC\u00034A\u0001\u0007q&\u0001\bcY>\u001c7.T1oC\u001e,'/\u00133\t\u000bU\u0002\u0003\u0019\u0001\u001c\u0002\u000bA,WM]:\u0011\u0007\u0011:t&\u0003\u00029]\t\u00191+Z9\t\u000bi\u0002\u0003\u0019A\u001e\u0002#A,WM]:SKBd\u0017nY1uK\u0012$v\u000eE\u0002=\u0003>j\u0011!\u0010\u0006\u0003}}\nq!\\;uC\ndWM\u0003\u0002A\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005\tk$a\u0002%bg\"\u001cV\r\u001e\u0005\u0006\t\u0002\u0002\r!R\u0001\bE2|7m[%e!\t\u0019b)\u0003\u0002H\u0005\t9!\t\\8dW&#\u0007\"B%!\u0001\u0004Q\u0015a\u00038v[J+\u0007\u000f\\5dCN\u0004\"!D&\n\u00051s!aA%oi\"\u0012\u0001A\u0014\t\u0003\u001fJk\u0011\u0001\u0015\u0006\u0003#\u0012\t!\"\u00198o_R\fG/[8o\u0013\t\u0019\u0006K\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018\u000e"
)
public class RandomBlockReplicationPolicy implements BlockReplicationPolicy, Logging {
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
      Random random = new Random(blockId.hashCode());
      this.logDebug(new Serializable(this, peers) {
         public static final long serialVersionUID = 0L;
         private final Seq peers$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Input peers : ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.peers$1.mkString(", ")}));
         }

         public {
            this.peers$1 = peers$1;
         }
      });
      List var10000;
      if (peers.size() > numReplicas) {
         var10000 = BlockReplicationUtils$.MODULE$.getRandomSample(peers, numReplicas, random);
      } else {
         if (peers.size() < numReplicas) {
            this.logWarning(new Serializable(this, peers, numReplicas) {
               public static final long serialVersionUID = 0L;
               private final Seq peers$1;
               private final int numReplicas$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Expecting ", " replicas with only ", " peer/s."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.numReplicas$1), BoxesRunTime.boxToInteger(this.peers$1.size())}));
               }

               public {
                  this.peers$1 = peers$1;
                  this.numReplicas$1 = numReplicas$1;
               }
            });
         }

         var10000 = random.shuffle(peers, Seq$.MODULE$.canBuildFrom()).toList();
      }

      List prioritizedPeers = var10000;
      this.logDebug(new Serializable(this, prioritizedPeers) {
         public static final long serialVersionUID = 0L;
         private final List prioritizedPeers$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Prioritized peers : ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.prioritizedPeers$1.mkString(", ")}));
         }

         public {
            this.prioritizedPeers$1 = prioritizedPeers$1;
         }
      });
      return prioritizedPeers;
   }

   public RandomBlockReplicationPolicy() {
      Logging$class.$init$(this);
   }
}
