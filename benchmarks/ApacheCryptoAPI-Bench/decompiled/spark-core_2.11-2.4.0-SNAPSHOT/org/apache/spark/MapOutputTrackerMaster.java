package org.apache.spark;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.spark.broadcast.BroadcastManager;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.scheduler.MapStatus;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.util.RpcUtils$;
import org.apache.spark.util.ThreadUtils$;
import scala.Array$;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.concurrent.Map;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Range;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.ExecutionContext$;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.concurrent.duration.Duration$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.LongRef;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.RichInt$;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\t\u001df!B\u0001\u0003\u0001\tA!AF'ba>+H\u000f];u)J\f7m[3s\u001b\u0006\u001cH/\u001a:\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001c\"\u0001A\u0005\u0011\u0005)YQ\"\u0001\u0002\n\u00051\u0011!\u0001E'ba>+H\u000f];u)J\f7m[3s\u0011!q\u0001A!A!\u0002\u0013\u0001\u0012\u0001B2p]\u001a\u001c\u0001\u0001\u0005\u0002\u000b#%\u0011!C\u0001\u0002\n'B\f'o[\"p]\u001aD\u0001\u0002\u0006\u0001\u0003\u0002\u0003\u0006I!F\u0001\u0011EJ|\u0017\rZ2bgRl\u0015M\\1hKJ\u0004\"AF\r\u000e\u0003]Q!\u0001\u0007\u0002\u0002\u0013\t\u0014x.\u00193dCN$\u0018B\u0001\u000e\u0018\u0005A\u0011%o\\1eG\u0006\u001cH/T1oC\u001e,'\u000f\u0003\u0005\u001d\u0001\t\u0005\t\u0015!\u0003\u001e\u0003\u001dI7\u000fT8dC2\u0004\"AH\u0011\u000e\u0003}Q\u0011\u0001I\u0001\u0006g\u000e\fG.Y\u0005\u0003E}\u0011qAQ8pY\u0016\fg\u000eC\u0003%\u0001\u0011\u0005Q%\u0001\u0004=S:LGO\u0010\u000b\u0005M\u001dB\u0013\u0006\u0005\u0002\u000b\u0001!)ab\ta\u0001!!)Ac\ta\u0001+!)Ad\ta\u0001;!91\u0006\u0001b\u0001\n\u0013a\u0013aE7j]NK'0\u001a$pe\n\u0013x.\u00193dCN$X#A\u0017\u0011\u0005yq\u0013BA\u0018 \u0005\rIe\u000e\u001e\u0005\u0007c\u0001\u0001\u000b\u0011B\u0017\u0002)5LgnU5{K\u001a{'O\u0011:pC\u0012\u001c\u0017m\u001d;!\u0011\u001d\u0019\u0004A1A\u0005\nQ\nac\u001d5vM\u001adW\rT8dC2LG/_#oC\ndW\rZ\u000b\u0002;!1a\u0007\u0001Q\u0001\nu\tqc\u001d5vM\u001adW\rT8dC2LG/_#oC\ndW\r\u001a\u0011\t\u000fa\u0002!\u0019!C\u0005Y\u0005Q2\u000bS+G\r2+u\f\u0015*F\r~k\u0015\tU0U\u0011J+5\u000bS(M\t\"1!\b\u0001Q\u0001\n5\n1d\u0015%V\r\u001acUi\u0018)S\u000b\u001a{V*\u0011)`)\"\u0013Vi\u0015%P\u0019\u0012\u0003\u0003b\u0002\u001f\u0001\u0005\u0004%I\u0001L\u0001\u001e'\"+fI\u0012'F?B\u0013VIR0S\u000b\u0012+6)R0U\u0011J+5\u000bS(M\t\"1a\b\u0001Q\u0001\n5\nad\u0015%V\r\u001acUi\u0018)S\u000b\u001a{&+\u0012#V\u0007\u0016{F\u000b\u0013*F'\"{E\n\u0012\u0011\t\u000f\u0001\u0003!\u0019!C\u0005\u0003\u0006Q\"+\u0012#V\u0007\u0016\u0013v\f\u0015*F\r~cujQ*`\rJ\u000b5\tV%P\u001dV\t!\t\u0005\u0002\u001f\u0007&\u0011Ai\b\u0002\u0007\t>,(\r\\3\t\r\u0019\u0003\u0001\u0015!\u0003C\u0003m\u0011V\tR+D\u000bJ{\u0006KU#G?2{5iU0G%\u0006\u001bE+S(OA!9\u0001\n\u0001b\u0001\n\u0003I\u0015aD:ik\u001a4G.Z*uCR,8/Z:\u0016\u0003)\u0003Ba\u0013).%6\tAJ\u0003\u0002N\u001d\u0006Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005={\u0012AC2pY2,7\r^5p]&\u0011\u0011\u000b\u0014\u0002\u0004\u001b\u0006\u0004\bC\u0001\u0006T\u0013\t!&AA\u0007TQV4g\r\\3Ti\u0006$Xo\u001d\u0005\u0007-\u0002\u0001\u000b\u0011\u0002&\u0002!MDWO\u001a4mKN#\u0018\r^;tKN\u0004\u0003b\u0002-\u0001\u0005\u0004%I\u0001L\u0001\u0012[\u0006D(\u000b]2NKN\u001c\u0018mZ3TSj,\u0007B\u0002.\u0001A\u0003%Q&\u0001\nnCb\u0014\u0006oY'fgN\fw-Z*ju\u0016\u0004\u0003b\u0002/\u0001\u0005\u0004%I!X\u0001\u0012[\u0006\u0004x*\u001e;qkR\u0014V-];fgR\u001cX#\u00010\u0011\u0007}+w-D\u0001a\u0015\ti\u0015M\u0003\u0002cG\u0006!Q\u000f^5m\u0015\u0005!\u0017\u0001\u00026bm\u0006L!A\u001a1\u0003'1Kgn[3e\u00052|7m[5oOF+X-^3\u0011\u0005)A\u0017BA5\u0003\u0005M9U\r^'ba>+H\u000f];u\u001b\u0016\u001c8/Y4f\u0011\u0019Y\u0007\u0001)A\u0005=\u0006\u0011R.\u00199PkR\u0004X\u000f\u001e*fcV,7\u000f^:!\u0011\u001di\u0007A1A\u0005\n9\f!\u0002\u001e5sK\u0006$\u0007o\\8m+\u0005y\u0007CA0q\u0013\t\t\bM\u0001\nUQJ,\u0017\r\u001a)p_2,\u00050Z2vi>\u0014\bBB:\u0001A\u0003%q.A\u0006uQJ,\u0017\r\u001a9p_2\u0004\u0003\"B;\u0001\t\u00031\u0018\u0001\u00029pgR$\"a\u001e>\u0011\u0005yA\u0018BA= \u0005\u0011)f.\u001b;\t\u000bm$\b\u0019A4\u0002\u000f5,7o]1hK\u001a!Q\u0010\u0001\u0003\u007f\u0005-iUm]:bO\u0016dun\u001c9\u0014\tq|\u00181\u0002\t\u0005\u0003\u0003\t9!\u0004\u0002\u0002\u0004)\u0019\u0011QA2\u0002\t1\fgnZ\u0005\u0005\u0003\u0013\t\u0019A\u0001\u0004PE*,7\r\u001e\t\u0005\u0003\u0003\ti!\u0003\u0003\u0002\u0010\u0005\r!\u0001\u0003*v]:\f'\r\\3\t\r\u0011bH\u0011AA\n)\t\t)\u0002E\u0002\u0002\u0018ql\u0011\u0001\u0001\u0005\b\u00037aH\u0011IA\u000f\u0003\r\u0011XO\u001c\u000b\u0002o\"I\u0011\u0011\u0005\u0001C\u0002\u0013%\u00111E\u0001\u000b!>L7o\u001c8QS2dW#A4\t\u000f\u0005\u001d\u0002\u0001)A\u0005O\u0006Y\u0001k\\5t_:\u0004\u0016\u000e\u001c7!\u0011\u001d\tY\u0003\u0001C\u0001\u00051\nqdZ3u\u001dVl7)Y2iK\u0012\u001cVM]5bY&TX\r\u001a\"s_\u0006$7-Y:u\u0011\u001d\ty\u0003\u0001C\u0001\u0003c\tqB]3hSN$XM]*ik\u001a4G.\u001a\u000b\u0006o\u0006M\u0012q\u0007\u0005\b\u0003k\ti\u00031\u0001.\u0003%\u0019\b.\u001e4gY\u0016LE\rC\u0004\u0002:\u00055\u0002\u0019A\u0017\u0002\u000f9,X.T1qg\"9\u0011Q\b\u0001\u0005\u0002\u0005}\u0012!\u0005:fO&\u001cH/\u001a:NCB|U\u000f\u001e9viR9q/!\u0011\u0002D\u0005\u001d\u0003bBA\u001b\u0003w\u0001\r!\f\u0005\b\u0003\u000b\nY\u00041\u0001.\u0003\u0015i\u0017\r]%e\u0011!\tI%a\u000fA\u0002\u0005-\u0013AB:uCR,8\u000f\u0005\u0003\u0002N\u0005MSBAA(\u0015\r\t\tFA\u0001\ng\u000eDW\rZ;mKJLA!!\u0016\u0002P\tIQ*\u00199Ti\u0006$Xo\u001d\u0005\b\u00033\u0002A\u0011AA.\u0003M)hN]3hSN$XM]'ba>+H\u000f];u)\u001d9\u0018QLA0\u0003CBq!!\u000e\u0002X\u0001\u0007Q\u0006C\u0004\u0002F\u0005]\u0003\u0019A\u0017\t\u0011\u0005\r\u0014q\u000ba\u0001\u0003K\n\u0011BY7BI\u0012\u0014Xm]:\u0011\t\u0005\u001d\u0014QN\u0007\u0003\u0003SR1!a\u001b\u0003\u0003\u001d\u0019Ho\u001c:bO\u0016LA!a\u001c\u0002j\tq!\t\\8dW6\u000bg.Y4fe&#\u0007bBA:\u0001\u0011\u0005\u0011QO\u0001\u0012k:\u0014XmZ5ti\u0016\u00148\u000b[;gM2,GcA<\u0002x!9\u0011QGA9\u0001\u0004i\u0003bBA>\u0001\u0011\u0005\u0011QP\u0001\u0014e\u0016lwN^3PkR\u0004X\u000f^:P]\"{7\u000f\u001e\u000b\u0004o\u0006}\u0004\u0002CAA\u0003s\u0002\r!a!\u0002\t!|7\u000f\u001e\t\u0005\u0003\u000b\u000bYID\u0002\u001f\u0003\u000fK1!!# \u0003\u0019\u0001&/\u001a3fM&!\u0011QRAH\u0005\u0019\u0019FO]5oO*\u0019\u0011\u0011R\u0010\t\u000f\u0005M\u0005\u0001\"\u0001\u0002\u0016\u00069\"/Z7pm\u0016|U\u000f\u001e9viN|e.\u0012=fGV$xN\u001d\u000b\u0004o\u0006]\u0005\u0002CAM\u0003#\u0003\r!a!\u0002\r\u0015DXmY%e\u0011\u001d\ti\n\u0001C\u0001\u0003?\u000bqbY8oi\u0006Lgn]*ik\u001a4G.\u001a\u000b\u0004;\u0005\u0005\u0006bBA\u001b\u00037\u0003\r!\f\u0005\b\u0003K\u0003A\u0011AAT\u0003Y9W\r\u001e(v[\u00063\u0018-\u001b7bE2,w*\u001e;qkR\u001cHcA\u0017\u0002*\"9\u0011QGAR\u0001\u0004i\u0003bBAW\u0001\u0011\u0005\u0011qV\u0001\u0016M&tG-T5tg&tw\rU1si&$\u0018n\u001c8t)\u0011\t\t,a4\u0011\u000by\t\u0019,a.\n\u0007\u0005UvD\u0001\u0004PaRLwN\u001c\t\u0006\u0003s\u000bI-\f\b\u0005\u0003w\u000b)M\u0004\u0003\u0002>\u0006\rWBAA`\u0015\r\t\tmD\u0001\u0007yI|w\u000e\u001e \n\u0003\u0001J1!a2 \u0003\u001d\u0001\u0018mY6bO\u0016LA!a3\u0002N\n\u00191+Z9\u000b\u0007\u0005\u001dw\u0004C\u0004\u00026\u0005-\u0006\u0019A\u0017\t\u000f\u0005M\u0007\u0001\"\u0001\u0002V\u0006a!/\u00198hK\u001e\u0013x.\u001e9fIR1\u0011q[Ap\u0003G\u0004b!!/\u0002J\u0006e\u0007\u0003BA]\u00037LA!!8\u0002N\n)!+\u00198hK\"A\u0011\u0011]Ai\u0001\u0004\tI.A\u0003sC:<W\rC\u0004\u0002f\u0006E\u0007\u0019A\u0017\u0002\tML'0\u001a\u0005\b\u0003S\u0004A\u0011AAv\u00035)\u0017/^1mYf$\u0015N^5eKR1\u0011Q^Ax\u0003g\u0004b!!/\u0002J\u0006]\u0006bBAy\u0003O\u0004\r!L\u0001\f]VlW\t\\3nK:$8\u000fC\u0004\u0002v\u0006\u001d\b\u0019A\u0017\u0002\u00159,XNQ;dW\u0016$8\u000fC\u0004\u0002z\u0002!\t!a?\u0002\u001b\u001d,Go\u0015;bi&\u001cH/[2t)\u0011\tiPa\u0001\u0011\u0007)\ty0C\u0002\u0003\u0002\t\u00111#T1q\u001fV$\b/\u001e;Ti\u0006$\u0018n\u001d;jGND\u0001B!\u0002\u0002x\u0002\u0007!qA\u0001\u0004I\u0016\u0004\b\u0007\u0003B\u0005\u0005'\u00119C!\f\u0011\u0013)\u0011YAa\u0004\u0003&\t-\u0012b\u0001B\u0007\u0005\t\t2\u000b[;gM2,G)\u001a9f]\u0012,gnY=\u0011\t\tE!1\u0003\u0007\u0001\t1\u0011)Ba\u0001\u0002\u0002\u0003\u0005)\u0011\u0001B\f\u0005\ryF%M\t\u0005\u00053\u0011y\u0002E\u0002\u001f\u00057I1A!\b \u0005\u001dqu\u000e\u001e5j]\u001e\u00042A\bB\u0011\u0013\r\u0011\u0019c\b\u0002\u0004\u0003:L\b\u0003\u0002B\t\u0005O!AB!\u000b\u0003\u0004\u0005\u0005\t\u0011!B\u0001\u0005/\u00111a\u0018\u00133!\u0011\u0011\tB!\f\u0005\u0019\t=\"1AA\u0001\u0002\u0003\u0015\tAa\u0006\u0003\u0007}#3\u0007C\u0004\u00034\u0001!\tA!\u000e\u0002?\u001d,G\u000f\u0015:fM\u0016\u0014(/\u001a3M_\u000e\fG/[8og\u001a{'o\u00155vM\u001adW\r\u0006\u0004\u00038\te\"\u0011\u000b\t\u0007\u0003s\u000bI-a!\t\u0011\t\u0015!\u0011\u0007a\u0001\u0005w\u0001\u0004B!\u0010\u0003B\t\u001d#Q\n\t\n\u0015\t-!q\bB#\u0005\u0017\u0002BA!\u0005\u0003B\u0011a!1\tB\u001d\u0003\u0003\u0005\tQ!\u0001\u0003\u0018\t\u0019q\f\n\u001b\u0011\t\tE!q\t\u0003\r\u0005\u0013\u0012I$!A\u0001\u0002\u000b\u0005!q\u0003\u0002\u0004?\u0012*\u0004\u0003\u0002B\t\u0005\u001b\"ABa\u0014\u0003:\u0005\u0005\t\u0011!B\u0001\u0005/\u00111a\u0018\u00137\u0011\u001d\u0011\u0019F!\rA\u00025\n1\u0002]1si&$\u0018n\u001c8JI\"9!q\u000b\u0001\u0005\u0002\te\u0013AH4fi2{7-\u0019;j_:\u001cx+\u001b;i\u0019\u0006\u0014x-Z:u\u001fV$\b/\u001e;t))\u0011YFa\u0019\u0003f\t%$Q\u000e\t\u0006=\u0005M&Q\f\t\u0006=\t}\u0013QM\u0005\u0004\u0005Cz\"!B!se\u0006L\bbBA\u001b\u0005+\u0002\r!\f\u0005\b\u0005O\u0012)\u00061\u0001.\u0003%\u0011X\rZ;dKJLE\rC\u0004\u0003l\tU\u0003\u0019A\u0017\u0002\u00179,XNU3ek\u000e,'o\u001d\u0005\b\u0005_\u0012)\u00061\u0001C\u0003E1'/Y2uS>tG\u000b\u001b:fg\"|G\u000e\u001a\u0005\b\u0005g\u0002A\u0011AA\u000f\u00039Ign\u0019:f[\u0016tG/\u00129pG\"DqAa\u001e\u0001\t\u0003\u0011I(\u0001\u0005hKR,\u0005o\\2i+\t\u0011Y\bE\u0002\u001f\u0005{J1Aa  \u0005\u0011auN\\4\t\u000f\t\r\u0005\u0001\"\u0001\u0003\u0006\u00069r-\u001a;NCB\u001c\u0016N_3t\u0005f,\u00050Z2vi>\u0014\u0018\n\u001a\u000b\t\u0005\u000f\u0013IJa'\u0003 B1\u0011\u0011XAe\u0005\u0013\u0003rA\bBF\u0003K\u0012y)C\u0002\u0003\u000e~\u0011a\u0001V;qY\u0016\u0014\u0004CBA]\u0003\u0013\u0014\t\nE\u0004\u001f\u0005\u0017\u0013\u0019Ja\u001f\u0011\t\u0005\u001d$QS\u0005\u0005\u0005/\u000bIGA\u0004CY>\u001c7.\u00133\t\u000f\u0005U\"\u0011\u0011a\u0001[!9!Q\u0014BA\u0001\u0004i\u0013AD:uCJ$\b+\u0019:uSRLwN\u001c\u0005\b\u0005C\u0013\t\t1\u0001.\u00031)g\u000e\u001a)beRLG/[8o\u0011\u001d\u0011)\u000b\u0001C!\u0003;\tAa\u001d;pa\u0002"
)
public class MapOutputTrackerMaster extends MapOutputTracker {
   public final SparkConf org$apache$spark$MapOutputTrackerMaster$$conf;
   public final BroadcastManager org$apache$spark$MapOutputTrackerMaster$$broadcastManager;
   public final boolean org$apache$spark$MapOutputTrackerMaster$$isLocal;
   private final int org$apache$spark$MapOutputTrackerMaster$$minSizeForBroadcast;
   private final boolean shuffleLocalityEnabled;
   private final int SHUFFLE_PREF_MAP_THRESHOLD;
   private final int SHUFFLE_PREF_REDUCE_THRESHOLD;
   private final double REDUCER_PREF_LOCS_FRACTION;
   private final Map<Object, ShuffleStatus> shuffleStatuses;
   private final int maxRpcMessageSize;
   private final LinkedBlockingQueue<GetMapOutputMessage> org$apache$spark$MapOutputTrackerMaster$$mapOutputRequests;
   private final ThreadPoolExecutor threadpool;
   private final GetMapOutputMessage org$apache$spark$MapOutputTrackerMaster$$PoisonPill;

   public int org$apache$spark$MapOutputTrackerMaster$$minSizeForBroadcast() {
      return this.org$apache$spark$MapOutputTrackerMaster$$minSizeForBroadcast;
   }

   private boolean shuffleLocalityEnabled() {
      return this.shuffleLocalityEnabled;
   }

   private int SHUFFLE_PREF_MAP_THRESHOLD() {
      return this.SHUFFLE_PREF_MAP_THRESHOLD;
   }

   private int SHUFFLE_PREF_REDUCE_THRESHOLD() {
      return this.SHUFFLE_PREF_REDUCE_THRESHOLD;
   }

   private double REDUCER_PREF_LOCS_FRACTION() {
      return this.REDUCER_PREF_LOCS_FRACTION;
   }

   public Map<Object, ShuffleStatus> shuffleStatuses() {
      return this.shuffleStatuses;
   }

   private int maxRpcMessageSize() {
      return this.maxRpcMessageSize;
   }

   public LinkedBlockingQueue<GetMapOutputMessage> org$apache$spark$MapOutputTrackerMaster$$mapOutputRequests() {
      return this.org$apache$spark$MapOutputTrackerMaster$$mapOutputRequests;
   }

   private ThreadPoolExecutor threadpool() {
      return this.threadpool;
   }

   public void post(GetMapOutputMessage message) {
      this.org$apache$spark$MapOutputTrackerMaster$$mapOutputRequests().offer(message);
   }

   public GetMapOutputMessage org$apache$spark$MapOutputTrackerMaster$$PoisonPill() {
      return this.org$apache$spark$MapOutputTrackerMaster$$PoisonPill;
   }

   public int getNumCachedSerializedBroadcast() {
      return this.shuffleStatuses().valuesIterator().count(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ShuffleStatus x$1) {
            return x$1.hasCachedSerializedBroadcast();
         }
      });
   }

   public void registerShuffle(int shuffleId, int numMaps) {
      if (this.shuffleStatuses().put(BoxesRunTime.boxToInteger(shuffleId), new ShuffleStatus(numMaps)).isDefined()) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)"Shuffle ID ").append((Object)BoxesRunTime.boxToInteger(shuffleId)).append((Object)" registered twice").toString());
      }
   }

   public void registerMapOutput(int shuffleId, int mapId, MapStatus status) {
      ((ShuffleStatus)this.shuffleStatuses().apply(BoxesRunTime.boxToInteger(shuffleId))).addMapOutput(mapId, status);
   }

   public void unregisterMapOutput(int shuffleId, int mapId, BlockManagerId bmAddress) {
      Option var4 = this.shuffleStatuses().get(BoxesRunTime.boxToInteger(shuffleId));
      if (var4 instanceof Some) {
         Some var5 = (Some)var4;
         ShuffleStatus shuffleStatus = (ShuffleStatus)var5.x();
         shuffleStatus.removeMapOutput(mapId, bmAddress);
         this.incrementEpoch();
         BoxedUnit var7 = BoxedUnit.UNIT;
      } else if (None$.MODULE$.equals(var4)) {
         throw new SparkException("unregisterMapOutput called for nonexistent shuffle ID");
      } else {
         throw new MatchError(var4);
      }
   }

   public void unregisterShuffle(int shuffleId) {
      this.shuffleStatuses().remove(BoxesRunTime.boxToInteger(shuffleId)).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(ShuffleStatus shuffleStatus) {
            shuffleStatus.invalidateSerializedMapOutputStatusCache();
         }
      });
   }

   public void removeOutputsOnHost(String host) {
      this.shuffleStatuses().valuesIterator().foreach(new Serializable(this, host) {
         public static final long serialVersionUID = 0L;
         private final String host$2;

         public final void apply(ShuffleStatus x$2) {
            x$2.removeOutputsOnHost(this.host$2);
         }

         public {
            this.host$2 = host$2;
         }
      });
      this.incrementEpoch();
   }

   public void removeOutputsOnExecutor(String execId) {
      this.shuffleStatuses().valuesIterator().foreach(new Serializable(this, execId) {
         public static final long serialVersionUID = 0L;
         private final String execId$2;

         public final void apply(ShuffleStatus x$3) {
            x$3.removeOutputsOnExecutor(this.execId$2);
         }

         public {
            this.execId$2 = execId$2;
         }
      });
      this.incrementEpoch();
   }

   public boolean containsShuffle(int shuffleId) {
      return this.shuffleStatuses().contains(BoxesRunTime.boxToInteger(shuffleId));
   }

   public int getNumAvailableOutputs(int shuffleId) {
      return BoxesRunTime.unboxToInt(this.shuffleStatuses().get(BoxesRunTime.boxToInteger(shuffleId)).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(ShuffleStatus x$4) {
            return x$4.numAvailableOutputs();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }));
   }

   public Option<Seq<Object>> findMissingPartitions(int shuffleId) {
      return this.shuffleStatuses().get(BoxesRunTime.boxToInteger(shuffleId)).<Seq<Object>>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Object> apply(ShuffleStatus x$5) {
            return x$5.findMissingPartitions();
         }
      });
   }

   public Seq<Range> rangeGrouped(Range range, int size) {
      int start = range.start();
      int step = range.step();
      int end = range.end();
      return (Seq)RichInt$.MODULE$.until$extension1(Predef$.MODULE$.intWrapper(start), end, size * step).map(new Serializable(this, size, step) {
         public static final long serialVersionUID = 0L;
         private final int size$1;
         private final int step$1;

         public final Range apply(int i) {
            return RichInt$.MODULE$.until$extension1(Predef$.MODULE$.intWrapper(i), i + this.size$1 * this.step$1, this.step$1);
         }

         public {
            this.size$1 = size$1;
            this.step$1 = step$1;
         }
      }, IndexedSeq$.MODULE$.canBuildFrom());
   }

   public Seq<Seq<Object>> equallyDivide(int numElements, int numBuckets) {
      int elementsPerBucket = numElements / numBuckets;
      int remaining = numElements % numBuckets;
      int splitPoint = (elementsPerBucket + 1) * remaining;
      return elementsPerBucket == 0 ? this.rangeGrouped(RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), splitPoint), elementsPerBucket + 1) : (Seq)this.rangeGrouped(RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), splitPoint), elementsPerBucket + 1).$plus$plus(this.rangeGrouped(RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(splitPoint), numElements), elementsPerBucket), Seq$.MODULE$.canBuildFrom());
   }

   public MapOutputStatistics getStatistics(ShuffleDependency<?, ?, ?> dep) {
      return (MapOutputStatistics)((ShuffleStatus)this.shuffleStatuses().apply(BoxesRunTime.boxToInteger(dep.shuffleId()))).withMapStatuses(new Serializable(this, dep) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MapOutputTrackerMaster $outer;
         private final ShuffleDependency dep$1;

         public final MapOutputStatistics apply(MapStatus[] statuses) {
            long[] totalSizes = new long[this.dep$1.partitioner().numPartitions()];
            int parallelAggThreshold = BoxesRunTime.unboxToInt(this.$outer.org$apache$spark$MapOutputTrackerMaster$$conf.get(org.apache.spark.internal.config.package$.MODULE$.SHUFFLE_MAP_OUTPUT_PARALLEL_AGGREGATION_THRESHOLD()));
            int parallelism = (int)scala.math.package$.MODULE$.min((long)Runtime.getRuntime().availableProcessors(), (long)statuses.length * (long)totalSizes.length / (long)parallelAggThreshold + 1L);
            if (parallelism <= 1) {
               Predef$.MODULE$.refArrayOps(statuses).foreach(new Serializable(this, totalSizes) {
                  public static final long serialVersionUID = 0L;
                  public final long[] totalSizes$1;

                  public final void apply(MapStatus s) {
                     RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.totalSizes$1.length).foreach$mVc$sp(new Serializable(this, s) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final MapStatus s$1;

                        public final void apply(int i) {
                           this.apply$mcVI$sp(i);
                        }

                        public void apply$mcVI$sp(int i) {
                           this.$outer.totalSizes$1[i] += this.s$1.getSizeForBlock(i);
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.s$1 = s$1;
                           }
                        }
                     });
                  }

                  public {
                     this.totalSizes$1 = totalSizes$1;
                  }
               });
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               ThreadPoolExecutor threadPool = ThreadUtils$.MODULE$.newDaemonFixedThreadPool(parallelism, "map-output-aggregate");

               try {
                  ExecutionContextExecutor executionContext = ExecutionContext$.MODULE$.fromExecutor(threadPool);
                  Seq mapStatusSubmitTasks = (Seq)this.$outer.equallyDivide(totalSizes.length, parallelism).map(new Serializable(this, totalSizes, executionContext, statuses) {
                     public static final long serialVersionUID = 0L;
                     public final long[] totalSizes$1;
                     private final ExecutionContextExecutor executionContext$1;
                     public final MapStatus[] statuses$2;

                     public final Future<BoxedUnit> apply(Seq<Object> reduceIds) {
                        return Future$.MODULE$.<BoxedUnit>apply(new Serializable(this, reduceIds) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           public final Seq reduceIds$1;

                           public final void apply() {
                              this.apply$mcV$sp();
                           }

                           public void apply$mcV$sp() {
                              Predef$.MODULE$.refArrayOps(this.$outer.statuses$2).foreach(new Serializable(this) {
                                 public static final long serialVersionUID = 0L;
                                 // $FF: synthetic field
                                 private final <undefinedtype> $outer;

                                 public final void apply(MapStatus s) {
                                    this.$outer.reduceIds$1.foreach(new Serializable(this, s) {
                                       public static final long serialVersionUID = 0L;
                                       // $FF: synthetic field
                                       private final <undefinedtype> $outer;
                                       private final MapStatus s$2;

                                       public final void apply(int i) {
                                          this.apply$mcVI$sp(i);
                                       }

                                       public void apply$mcVI$sp(int i) {
                                          this.$outer.org$apache$spark$MapOutputTrackerMaster$$anonfun$$anonfun$$anonfun$$anonfun$$$outer().org$apache$spark$MapOutputTrackerMaster$$anonfun$$anonfun$$anonfun$$$outer().totalSizes$1[i] += this.s$2.getSizeForBlock(i);
                                       }

                                       public {
                                          if ($outer == null) {
                                             throw null;
                                          } else {
                                             this.$outer = $outer;
                                             this.s$2 = s$2;
                                          }
                                       }
                                    });
                                 }

                                 // $FF: synthetic method
                                 public <undefinedtype> org$apache$spark$MapOutputTrackerMaster$$anonfun$$anonfun$$anonfun$$anonfun$$$outer() {
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

                           // $FF: synthetic method
                           public <undefinedtype> org$apache$spark$MapOutputTrackerMaster$$anonfun$$anonfun$$anonfun$$$outer() {
                              return this.$outer;
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.reduceIds$1 = reduceIds$1;
                              }
                           }
                        }, this.executionContext$1);
                     }

                     public {
                        this.totalSizes$1 = totalSizes$1;
                        this.executionContext$1 = executionContext$1;
                        this.statuses$2 = statuses$2;
                     }
                  }, Seq$.MODULE$.canBuildFrom());
                  ThreadUtils$.MODULE$.awaitResult(Future$.MODULE$.sequence(mapStatusSubmitTasks, Seq$.MODULE$.canBuildFrom(), executionContext), Duration$.MODULE$.Inf());
               } finally {
                  threadPool.shutdown();
               }
            }

            return new MapOutputStatistics(this.dep$1.shuffleId(), totalSizes);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.dep$1 = dep$1;
            }
         }
      });
   }

   public Seq<String> getPreferredLocationsForShuffle(ShuffleDependency<?, ?, ?> dep, int partitionId) {
      Object var10000;
      if (this.shuffleLocalityEnabled() && dep.rdd().partitions().length < this.SHUFFLE_PREF_MAP_THRESHOLD() && dep.partitioner().numPartitions() < this.SHUFFLE_PREF_REDUCE_THRESHOLD()) {
         Option blockManagerIds = this.getLocationsWithLargestOutputs(dep.shuffleId(), partitionId, dep.partitioner().numPartitions(), this.REDUCER_PREF_LOCS_FRACTION());
         var10000 = blockManagerIds.nonEmpty() ? (Seq)Predef$.MODULE$.refArrayOps(blockManagerIds.get()).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(BlockManagerId x$6) {
               return x$6.host();
            }
         }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit())) : Nil$.MODULE$;
      } else {
         var10000 = Nil$.MODULE$;
      }

      return (Seq<String>)var10000;
   }

   public Option<BlockManagerId[]> getLocationsWithLargestOutputs(int shuffleId, int reducerId, int numReducers, double fractionThreshold) {
      Object var6 = new Object();

      Object var10000;
      try {
         ShuffleStatus shuffleStatus = (ShuffleStatus)this.shuffleStatuses().get(BoxesRunTime.boxToInteger(shuffleId)).orNull(Predef$.MODULE$.$conforms());
         if (shuffleStatus == null) {
            BoxedUnit var10 = BoxedUnit.UNIT;
         } else {
            shuffleStatus.withMapStatuses(new Serializable(this, reducerId, fractionThreshold, var6) {
               public static final long serialVersionUID = 0L;
               private final int reducerId$1;
               public final double fractionThreshold$1;
               private final Object nonLocalReturnKey1$1;

               public final void apply(MapStatus[] statuses) {
                  if (Predef$.MODULE$.refArrayOps(statuses).nonEmpty()) {
                     HashMap locs = new HashMap();
                     LongRef totalOutputSize = LongRef.create(0L);

                     for(int mapIdx = 0; mapIdx < statuses.length; ++mapIdx) {
                        MapStatus status = statuses[mapIdx];
                        if (status != null) {
                           long blockSize = status.getSizeForBlock(this.reducerId$1);
                           if (blockSize > 0L) {
                              locs.update(status.location(), BoxesRunTime.boxToLong(BoxesRunTime.unboxToLong(locs.getOrElse(status.location(), new Serializable(this) {
                                 public static final long serialVersionUID = 0L;

                                 public final long apply() {
                                    return this.apply$mcJ$sp();
                                 }

                                 public long apply$mcJ$sp() {
                                    return 0L;
                                 }
                              })) + blockSize));
                              totalOutputSize.elem += blockSize;
                           }
                        }
                     }

                     HashMap topLocs = (HashMap)locs.filter(new Serializable(this, totalOutputSize) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final LongRef totalOutputSize$1;

                        public final boolean apply(Tuple2<BlockManagerId, Object> x0$1) {
                           if (x0$1 != null) {
                              long size = x0$1._2$mcJ$sp();
                              boolean var5 = (double)size / (double)this.totalOutputSize$1.elem >= this.$outer.fractionThreshold$1;
                              return var5;
                           } else {
                              throw new MatchError(x0$1);
                           }
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.totalOutputSize$1 = totalOutputSize$1;
                           }
                        }
                     });
                     if (topLocs.nonEmpty()) {
                        throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, new Some(topLocs.keys().toArray(ClassTag$.MODULE$.apply(BlockManagerId.class))));
                     }
                  }

               }

               public {
                  this.reducerId$1 = reducerId$1;
                  this.fractionThreshold$1 = fractionThreshold$1;
                  this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
               }
            });
         }

         var10000 = None$.MODULE$;
      } catch (NonLocalReturnControl var9) {
         if (var9.key() != var6) {
            throw var9;
         }

         var10000 = (Option)var9.value();
      }

      return (Option<BlockManagerId[]>)var10000;
   }

   public void incrementEpoch() {
      synchronized(this.epochLock()) {
         this.epoch_$eq(this.epoch() + 1L);
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final MapOutputTrackerMaster $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Increasing epoch to ").append((Object)BoxesRunTime.boxToLong(this.$outer.epoch())).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public long getEpoch() {
      synchronized(this.epochLock()) {
         return this.epoch();
      }
   }

   public Seq<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> getMapSizesByExecutorId(int shuffleId, int startPartition, int endPartition) {
      this.logDebug(new Serializable(this, shuffleId, startPartition, endPartition) {
         public static final long serialVersionUID = 0L;
         private final int shuffleId$2;
         private final int startPartition$1;
         private final int endPartition$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Fetching outputs for shuffle ", ", partitions ", "-", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.shuffleId$2), BoxesRunTime.boxToInteger(this.startPartition$1), BoxesRunTime.boxToInteger(this.endPartition$1)}));
         }

         public {
            this.shuffleId$2 = shuffleId$2;
            this.startPartition$1 = startPartition$1;
            this.endPartition$1 = endPartition$1;
         }
      });
      Option var4 = this.shuffleStatuses().get(BoxesRunTime.boxToInteger(shuffleId));
      Seq var7;
      if (var4 instanceof Some) {
         Some var5 = (Some)var4;
         ShuffleStatus shuffleStatus = (ShuffleStatus)var5.x();
         var7 = (Seq)shuffleStatus.withMapStatuses(new Serializable(this, shuffleId, startPartition, endPartition) {
            public static final long serialVersionUID = 0L;
            private final int shuffleId$2;
            private final int startPartition$1;
            private final int endPartition$1;

            public final Seq<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> apply(MapStatus[] statuses) {
               return MapOutputTracker$.MODULE$.convertMapStatuses(this.shuffleId$2, this.startPartition$1, this.endPartition$1, statuses);
            }

            public {
               this.shuffleId$2 = shuffleId$2;
               this.startPartition$1 = startPartition$1;
               this.endPartition$1 = endPartition$1;
            }
         });
      } else {
         if (!None$.MODULE$.equals(var4)) {
            throw new MatchError(var4);
         }

         var7 = (Seq)Seq$.MODULE$.empty();
      }

      return var7;
   }

   public void stop() {
      this.org$apache$spark$MapOutputTrackerMaster$$mapOutputRequests().offer(this.org$apache$spark$MapOutputTrackerMaster$$PoisonPill());
      this.threadpool().shutdown();
      this.sendTracker(StopMapOutputTracker$.MODULE$);
      this.trackerEndpoint_$eq((RpcEndpointRef)null);
      this.shuffleStatuses().clear();
   }

   public MapOutputTrackerMaster(SparkConf conf, BroadcastManager broadcastManager, boolean isLocal) {
      super(conf);
      this.org$apache$spark$MapOutputTrackerMaster$$conf = conf;
      this.org$apache$spark$MapOutputTrackerMaster$$broadcastManager = broadcastManager;
      this.org$apache$spark$MapOutputTrackerMaster$$isLocal = isLocal;
      this.org$apache$spark$MapOutputTrackerMaster$$minSizeForBroadcast = (int)conf.getSizeAsBytes("spark.shuffle.mapOutput.minSizeForBroadcast", "512k");
      this.shuffleLocalityEnabled = conf.getBoolean("spark.shuffle.reduceLocality.enabled", true);
      this.SHUFFLE_PREF_MAP_THRESHOLD = 1000;
      this.SHUFFLE_PREF_REDUCE_THRESHOLD = 1000;
      this.REDUCER_PREF_LOCS_FRACTION = 0.2;
      this.shuffleStatuses = (Map)JavaConverters$.MODULE$.mapAsScalaConcurrentMapConverter(new ConcurrentHashMap()).asScala();
      this.maxRpcMessageSize = RpcUtils$.MODULE$.maxMessageSizeBytes(conf);
      this.org$apache$spark$MapOutputTrackerMaster$$mapOutputRequests = new LinkedBlockingQueue();
      int numThreads = conf.getInt("spark.shuffle.mapOutput.dispatcher.numThreads", 8);
      ThreadPoolExecutor pool = ThreadUtils$.MODULE$.newDaemonFixedThreadPool(numThreads, "map-output-dispatcher");
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), numThreads).foreach$mVc$sp(new Serializable(this, pool) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MapOutputTrackerMaster $outer;
         private final ThreadPoolExecutor pool$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.pool$1.execute(this.$outer.new MessageLoop());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.pool$1 = pool$1;
            }
         }
      });
      this.threadpool = pool;
      if (this.org$apache$spark$MapOutputTrackerMaster$$minSizeForBroadcast() > this.maxRpcMessageSize()) {
         String msg = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.shuffle.mapOutput.minSizeForBroadcast (", " bytes) must "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.org$apache$spark$MapOutputTrackerMaster$$minSizeForBroadcast())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"be <= spark.rpc.message.maxSize (", " bytes) to prevent sending an rpc "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.maxRpcMessageSize())}))).append((Object)"message that is too large.").toString();
         this.logError(new Serializable(this, msg) {
            public static final long serialVersionUID = 0L;
            private final String msg$1;

            public final String apply() {
               return this.msg$1;
            }

            public {
               this.msg$1 = msg$1;
            }
         });
         throw new IllegalArgumentException(msg);
      } else {
         this.org$apache$spark$MapOutputTrackerMaster$$PoisonPill = new GetMapOutputMessage(-99, (RpcCallContext)null);
      }
   }

   public class MessageLoop implements Runnable {
      // $FF: synthetic field
      public final MapOutputTrackerMaster $outer;

      public void run() {
         try {
            while(true) {
               try {
                  while(true) {
                     GetMapOutputMessage data = (GetMapOutputMessage)this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().org$apache$spark$MapOutputTrackerMaster$$mapOutputRequests().take();
                     GetMapOutputMessage var8 = this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().org$apache$spark$MapOutputTrackerMaster$$PoisonPill();
                     if (data != null) {
                        if (data.equals(var8)) {
                           break;
                        }
                     } else if (var8 == null) {
                        break;
                     }

                     RpcCallContext context = data.context();
                     int shuffleId = data.shuffleId();
                     String hostPort = context.senderAddress().hostPort();
                     this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().logDebug(new Serializable(this, shuffleId, hostPort) {
                        public static final long serialVersionUID = 0L;
                        private final int shuffleId$1;
                        private final String hostPort$2;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Handling request to send map output locations for shuffle ").append((Object)BoxesRunTime.boxToInteger(this.shuffleId$1)).append((Object)" to ").append((Object)this.hostPort$2).toString();
                        }

                        public {
                           this.shuffleId$1 = shuffleId$1;
                           this.hostPort$2 = hostPort$2;
                        }
                     });
                     ShuffleStatus shuffleStatus = (ShuffleStatus)Option$.MODULE$.option2Iterable(this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().shuffleStatuses().get(BoxesRunTime.boxToInteger(shuffleId))).head();
                     context.reply(shuffleStatus.serializedMapStatus(this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().org$apache$spark$MapOutputTrackerMaster$$broadcastManager, this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().org$apache$spark$MapOutputTrackerMaster$$isLocal, this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().org$apache$spark$MapOutputTrackerMaster$$minSizeForBroadcast()));
                  }

                  this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().org$apache$spark$MapOutputTrackerMaster$$mapOutputRequests().offer(this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().org$apache$spark$MapOutputTrackerMaster$$PoisonPill());
                  return;
               } catch (Throwable var15) {
                  Option var4 = NonFatal$.MODULE$.unapply(var15);
                  if (var4.isEmpty()) {
                     throw var15;
                  }

                  Throwable e = (Throwable)var4.get();
                  this.org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer().logError(new Serializable(this, e) {
                     public static final long serialVersionUID = 0L;
                     private final Throwable e$1;

                     public final String apply() {
                        return this.e$1.getMessage();
                     }

                     public {
                        this.e$1 = e$1;
                     }
                  }, e);
                  BoxedUnit var6 = BoxedUnit.UNIT;
                  continue;
               }
            }
         } catch (InterruptedException var16) {
         }
      }

      // $FF: synthetic method
      public MapOutputTrackerMaster org$apache$spark$MapOutputTrackerMaster$MessageLoop$$$outer() {
         return this.$outer;
      }

      public MessageLoop() {
         if (MapOutputTrackerMaster.this == null) {
            throw null;
         } else {
            this.$outer = MapOutputTrackerMaster.this;
            super();
         }
      }
   }
}
