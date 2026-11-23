package org.apache.spark.storage.memory;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.package$;
import org.apache.spark.memory.MemoryManager;
import org.apache.spark.memory.MemoryMode;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockInfoManager;
import org.apache.spark.storage.RDDBlockId;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.io.ChunkedByteBuffer;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Product;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Iterable$;
import scala.collection.Iterator;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashMap$;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.LongRef;
import scala.runtime.RichInt$;
import scala.runtime.TraitSetter;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;

@ScalaSignature(
   bytes = "\u0006\u0001\t]e!B\u0001\u0003\u0001\u0019a!aC'f[>\u0014\u0018p\u0015;pe\u0016T!a\u0001\u0003\u0002\r5,Wn\u001c:z\u0015\t)a!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011AcF\u0007\u0002+)\u0011aCB\u0001\tS:$XM\u001d8bY&\u0011\u0001$\u0006\u0002\b\u0019><w-\u001b8h\u0011!Q\u0002A!A!\u0002\u0013a\u0012\u0001B2p]\u001a\u001c\u0001\u0001\u0005\u0002\u001e=5\ta!\u0003\u0002 \r\tI1\u000b]1sW\u000e{gN\u001a\u0005\tC\u0001\u0011\t\u0011)A\u0005E\u0005\u0001\"\r\\8dW&sgm\\'b]\u0006<WM\u001d\t\u0003G\u0011j\u0011\u0001B\u0005\u0003K\u0011\u0011\u0001C\u00117pG.LeNZ8NC:\fw-\u001a:\t\u0011\u001d\u0002!\u0011!Q\u0001\n!\n\u0011c]3sS\u0006d\u0017N_3s\u001b\u0006t\u0017mZ3s!\tIC&D\u0001+\u0015\tYc!\u0001\u0006tKJL\u0017\r\\5{KJL!!\f\u0016\u0003#M+'/[1mSj,'/T1oC\u001e,'\u000f\u0003\u00050\u0001\t\u0005\t\u0015!\u00031\u00035iW-\\8ss6\u000bg.Y4feB\u0011\u0011gM\u0007\u0002e)\u00111AB\u0005\u0003iI\u0012Q\"T3n_JLX*\u00198bO\u0016\u0014\b\u0002\u0003\u001c\u0001\u0005\u0003\u0005\u000b\u0011B\u001c\u0002)\tdwnY6Fm&\u001cG/[8o\u0011\u0006tG\r\\3s!\tA\u0014(D\u0001\u0003\u0013\tQ$A\u0001\u000bCY>\u001c7.\u0012<jGRLwN\u001c%b]\u0012dWM\u001d\u0005\u0006y\u0001!\t!P\u0001\u0007y%t\u0017\u000e\u001e \u0015\ryz\u0004)\u0011\"D!\tA\u0004\u0001C\u0003\u001bw\u0001\u0007A\u0004C\u0003\"w\u0001\u0007!\u0005C\u0003(w\u0001\u0007\u0001\u0006C\u00030w\u0001\u0007\u0001\u0007C\u00037w\u0001\u0007q\u0007C\u0004F\u0001\t\u0007I\u0011\u0002$\u0002\u000f\u0015tGO]5fgV\tq\t\u0005\u0003I\u001b>\u0013V\"A%\u000b\u0005)[\u0015\u0001B;uS2T\u0011\u0001T\u0001\u0005U\u00064\u0018-\u0003\u0002O\u0013\niA*\u001b8lK\u0012D\u0015m\u001d5NCB\u0004\"a\t)\n\u0005E#!a\u0002\"m_\u000e\\\u0017\n\u001a\u0019\u0003'b\u00032\u0001\u000f+W\u0013\t)&AA\u0006NK6|'/_#oiJL\bCA,Y\u0019\u0001!\u0011\"\u0017.\u0002\u0002\u0003\u0005)\u0011\u0001/\u0003\u0007}#\u0013\u0007\u0003\u0004\\\u0001\u0001\u0006IaR\u0001\tK:$(/[3tAE\u0011Q\f\u0019\t\u0003\u001dyK!aX\b\u0003\u000f9{G\u000f[5oOB\u0011a\"Y\u0005\u0003E>\u00111!\u00118z\u0011\u001d!\u0007A1A\u0005\n\u0015\fQc\u001c8IK\u0006\u0004XK\u001c:pY2lU-\\8ss6\u000b\u0007/F\u0001g!\u00119GN\u001c8\u000e\u0003!T!!\u001b6\u0002\u000f5,H/\u00192mK*\u00111nD\u0001\u000bG>dG.Z2uS>t\u0017BA7i\u0005\u001dA\u0015m\u001d5NCB\u0004\"AD8\n\u0005A|!\u0001\u0002'p]\u001eDaA\u001d\u0001!\u0002\u00131\u0017AF8o\u0011\u0016\f\u0007/\u00168s_2dW*Z7pefl\u0015\r\u001d\u0011\t\u000fQ\u0004!\u0019!C\u0005K\u00061rN\u001a4IK\u0006\u0004XK\u001c:pY2lU-\\8ss6\u000b\u0007\u000f\u0003\u0004w\u0001\u0001\u0006IAZ\u0001\u0018_\u001a4\u0007*Z1q+:\u0014x\u000e\u001c7NK6|'/_'ba\u0002Bq\u0001\u001f\u0001C\u0002\u0013%\u00110A\u000bv]J|G\u000e\\'f[>\u0014\u0018\u0010\u00165sKNDw\u000e\u001c3\u0016\u00039Daa\u001f\u0001!\u0002\u0013q\u0017AF;oe>dG.T3n_JLH\u000b\u001b:fg\"|G\u000e\u001a\u0011\t\u000bu\u0004A\u0011B=\u0002\u00135\f\u00070T3n_JL\b\"B@\u0001\t\u0013I\u0018AC7f[>\u0014\u00180V:fI\"1\u00111\u0001\u0001\u0005\ne\f\u0001C\u00197pG.\u001cX*Z7pef,6/\u001a3\t\u000f\u0005\u001d\u0001\u0001\"\u0001\u0002\n\u00059q-\u001a;TSj,Gc\u00018\u0002\f!9\u0011QBA\u0003\u0001\u0004y\u0015a\u00022m_\u000e\\\u0017\n\u001a\u0005\b\u0003#\u0001A\u0011AA\n\u0003!\u0001X\u000f\u001e\"zi\u0016\u001cX\u0003BA\u000b\u0003c!\"\"a\u0006\u00026\u0005]\u00121HA#)\u0011\tI\"a\b\u0011\u00079\tY\"C\u0002\u0002\u001e=\u0011qAQ8pY\u0016\fg\u000e\u0003\u0006\u0002\"\u0005=\u0011\u0011!a\u0002\u0003G\t!\"\u001a<jI\u0016t7-\u001a\u00133!\u0019\t)#a\u000b\u000205\u0011\u0011q\u0005\u0006\u0004\u0003Sy\u0011a\u0002:fM2,7\r^\u0005\u0005\u0003[\t9C\u0001\u0005DY\u0006\u001c8\u000fV1h!\r9\u0016\u0011\u0007\u0003\b\u0003g\tyA1\u0001]\u0005\u0005!\u0006bBA\u0007\u0003\u001f\u0001\ra\u0014\u0005\b\u0003s\ty\u00011\u0001o\u0003\u0011\u0019\u0018N_3\t\u0011\u0005u\u0012q\u0002a\u0001\u0003\u007f\t!\"\\3n_JLXj\u001c3f!\r\t\u0014\u0011I\u0005\u0004\u0003\u0007\u0012$AC'f[>\u0014\u00180T8eK\"A\u0011qIA\b\u0001\u0004\tI%\u0001\u0004`Ef$Xm\u001d\t\u0006\u001d\u0005-\u0013qJ\u0005\u0004\u0003\u001bz!!\u0003$v]\u000e$\u0018n\u001c81!\u0011\t\t&!\u0017\u000e\u0005\u0005M#\u0002BA+\u0003/\n!![8\u000b\u0005)3\u0011\u0002BA.\u0003'\u0012\u0011c\u00115v].,GMQ=uK\n+hMZ3s\u0011\u001d\ty\u0006\u0001C\u0005\u0003C\n1\u0002];u\u0013R,'/\u0019;peV!\u00111MAF)1\t)'! \u0002\u0000\u00055\u00151SAK!\u0019\t9'a\u001eo]:!\u0011\u0011NA:\u001d\u0011\tY'!\u001d\u000e\u0005\u00055$bAA87\u00051AH]8pizJ\u0011\u0001E\u0005\u0004\u0003kz\u0011a\u00029bG.\fw-Z\u0005\u0005\u0003s\nYH\u0001\u0004FSRDWM\u001d\u0006\u0004\u0003kz\u0001bBA\u0007\u0003;\u0002\ra\u0014\u0005\t\u0003\u0003\u000bi\u00061\u0001\u0002\u0004\u00061a/\u00197vKN\u0004b!a\u001a\u0002\u0006\u0006%\u0015\u0002BAD\u0003w\u0012\u0001\"\u0013;fe\u0006$xN\u001d\t\u0004/\u0006-EaBA\u001a\u0003;\u0012\r\u0001\u0018\u0005\t\u0003\u001f\u000bi\u00061\u0001\u0002\u0012\u0006A1\r\\1tgR\u000bw\r\u0005\u0004\u0002&\u0005-\u0012\u0011\u0012\u0005\t\u0003{\ti\u00061\u0001\u0002@!A\u0011qSA/\u0001\u0004\tI*\u0001\u0007wC2,Xm\u001d%pY\u0012,'\u000fE\u00039\u00037\u000bI)C\u0002\u0002\u001e\n\u0011ABV1mk\u0016\u001c\bj\u001c7eKJD\u0001\"!)\u0001\t\u0003!\u00111U\u0001\u0014aV$\u0018\n^3sCR|'/Q:WC2,Xm]\u000b\u0005\u0003K\u000b\t\f\u0006\u0005\u0002(\u0006M\u0016QWA]!\u001d\t9'a\u001e\u0002*:\u0004R\u0001OAV\u0003_K1!!,\u0003\u0005e\u0001\u0016M\u001d;jC2d\u00170\u00168s_2dW\rZ%uKJ\fGo\u001c:\u0011\u0007]\u000b\t\fB\u0004\u00024\u0005}%\u0019\u0001/\t\u000f\u00055\u0011q\u0014a\u0001\u001f\"A\u0011\u0011QAP\u0001\u0004\t9\f\u0005\u0004\u0002h\u0005\u0015\u0015q\u0016\u0005\t\u0003\u001f\u000by\n1\u0001\u0002<B1\u0011QEA\u0016\u0003_C\u0001\"a0\u0001\t\u0003!\u0011\u0011Y\u0001\u0013aV$\u0018\n^3sCR|'/Q:CsR,7/\u0006\u0003\u0002D\u0006=GCCAc\u0003#\f\u0019.a6\u0002\\B9\u0011qMA<\u0003\u000ft\u0007#\u0002\u001d\u0002J\u00065\u0017bAAf\u0005\tA\u0002+\u0019:uS\u0006dG._*fe&\fG.\u001b>fI\ncwnY6\u0011\u0007]\u000by\rB\u0004\u00024\u0005u&\u0019\u0001/\t\u000f\u00055\u0011Q\u0018a\u0001\u001f\"A\u0011\u0011QA_\u0001\u0004\t)\u000e\u0005\u0004\u0002h\u0005\u0015\u0015Q\u001a\u0005\t\u0003\u001f\u000bi\f1\u0001\u0002ZB1\u0011QEA\u0016\u0003\u001bD\u0001\"!\u0010\u0002>\u0002\u0007\u0011q\b\u0005\b\u0003?\u0004A\u0011AAq\u0003!9W\r\u001e\"zi\u0016\u001cH\u0003BAr\u0003S\u0004RADAs\u0003\u001fJ1!a:\u0010\u0005\u0019y\u0005\u000f^5p]\"9\u0011QBAo\u0001\u0004y\u0005bBAw\u0001\u0011\u0005\u0011q^\u0001\nO\u0016$h+\u00197vKN$B!!=\u0002~B)a\"!:\u0002tB\"\u0011Q_A}!\u0019\t9'!\"\u0002xB\u0019q+!?\u0005\u0017\u0005m\u00181^A\u0001\u0002\u0003\u0015\t\u0001\u0018\u0002\u0004?\u0012\u0012\u0004bBA\u0007\u0003W\u0004\ra\u0014\u0005\b\u0005\u0003\u0001A\u0011\u0001B\u0002\u0003\u0019\u0011X-\\8wKR!\u0011\u0011\u0004B\u0003\u0011\u001d\ti!a@A\u0002=CqA!\u0003\u0001\t\u0003\u0011Y!A\u0003dY\u0016\f'\u000f\u0006\u0002\u0003\u000eA\u0019aBa\u0004\n\u0007\tEqB\u0001\u0003V]&$\bb\u0002B\u000b\u0001\u0011%!qC\u0001\tO\u0016$(\u000b\u001a3JIR!!\u0011\u0004B\u0011!\u0015q\u0011Q\u001dB\u000e!\rq!QD\u0005\u0004\u0005?y!aA%oi\"9\u0011Q\u0002B\n\u0001\u0004y\u0005\u0002\u0003B\u0013\u0001\u0011\u0005aAa\n\u0002-\u00154\u0018n\u0019;CY>\u001c7n\u001d+p\rJ,Wm\u00159bG\u0016$rA\u001cB\u0015\u0005[\u0011\t\u0004\u0003\u0005\u0002\u000e\t\r\u0002\u0019\u0001B\u0016!\u0011q\u0011Q](\t\u000f\t=\"1\u0005a\u0001]\u0006)1\u000f]1dK\"A\u0011Q\bB\u0012\u0001\u0004\ty\u0004C\u0004\u00036\u0001!\tBa\u000e\u0002\u001f\u00054G/\u001a:Ee>\u0004\u0018i\u0019;j_:$BA!\u0004\u0003:!9\u0011Q\u0002B\u001a\u0001\u0004y\u0005b\u0002B\u001f\u0001\u0011\u0005!qH\u0001\tG>tG/Y5ogR!\u0011\u0011\u0004B!\u0011\u001d\tiAa\u000fA\u0002=CqA!\u0012\u0001\t\u0013\u00119%\u0001\u000bdkJ\u0014XM\u001c;UCN\\\u0017\t\u001e;f[B$\u0018\n\u001a\u000b\u0002]\"9!1\n\u0001\u0005\u0002\t5\u0013A\b:fg\u0016\u0014h/Z+oe>dG.T3n_JLhi\u001c:UQ&\u001cH+Y:l)!\tIBa\u0014\u0003R\tM\u0003bBA\u0007\u0005\u0013\u0002\ra\u0014\u0005\u0007\u0007\t%\u0003\u0019\u00018\t\u0011\u0005u\"\u0011\na\u0001\u0003\u007fAqAa\u0016\u0001\t\u0003\u0011I&\u0001\u0010sK2,\u0017m]3V]J|G\u000e\\'f[>\u0014\u0018PR8s)\"L7\u000fV1tWR1!Q\u0002B.\u0005;B\u0001\"!\u0010\u0003V\u0001\u0007\u0011q\b\u0005\t\u0007\tU\u0003\u0013!a\u0001]\"1!\u0011\r\u0001\u0005\u0002e\f1cY;se\u0016tG/\u00168s_2dW*Z7pefDaA!\u001a\u0001\t\u0003I\u0018AH2veJ,g\u000e^+oe>dG.T3n_JLhi\u001c:UQ&\u001cH+Y:l\u0011\u001d\u0011I\u0007\u0001C\u0005\u0005W\n\u0011C\\;n)\u0006\u001c8n]+oe>dG.\u001b8h+\t\u0011Y\u0002C\u0004\u0003p\u0001!IAa\u0003\u0002\u001d1|w-T3n_JLXk]1hK\"9!1\u000f\u0001\u0005\n\tU\u0014a\u00067pOVs'o\u001c7m\r\u0006LG.\u001e:f\u001b\u0016\u001c8/Y4f)\u0019\u0011iAa\u001e\u0003z!9\u0011Q\u0002B9\u0001\u0004y\u0005b\u0002B>\u0005c\u0002\rA\\\u0001\u0010M&t\u0017\r\u001c,fGR|'oU5{K\"I!q\u0010\u0001\u0012\u0002\u0013\u0005!\u0011Q\u0001)e\u0016dW-Y:f+:\u0014x\u000e\u001c7NK6|'/\u001f$peRC\u0017n\u001d+bg.$C-\u001a4bk2$HEM\u000b\u0003\u0005\u0007S3A\u001cBCW\t\u00119\t\u0005\u0003\u0003\n\nMUB\u0001BF\u0015\u0011\u0011iIa$\u0002\u0013Ut7\r[3dW\u0016$'b\u0001BI\u001f\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\tU%1\u0012\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007"
)
public class MemoryStore implements Logging {
   private final SparkConf conf;
   public final BlockInfoManager org$apache$spark$storage$memory$MemoryStore$$blockInfoManager;
   private final SerializerManager serializerManager;
   private final MemoryManager memoryManager;
   private final BlockEvictionHandler blockEvictionHandler;
   private final LinkedHashMap<BlockId, MemoryEntry<?>> org$apache$spark$storage$memory$MemoryStore$$entries;
   private final HashMap<Object, Object> onHeapUnrollMemoryMap;
   private final HashMap<Object, Object> offHeapUnrollMemoryMap;
   private final long org$apache$spark$storage$memory$MemoryStore$$unrollMemoryThreshold;
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

   public LinkedHashMap<BlockId, MemoryEntry<?>> org$apache$spark$storage$memory$MemoryStore$$entries() {
      return this.org$apache$spark$storage$memory$MemoryStore$$entries;
   }

   private HashMap<Object, Object> onHeapUnrollMemoryMap() {
      return this.onHeapUnrollMemoryMap;
   }

   private HashMap<Object, Object> offHeapUnrollMemoryMap() {
      return this.offHeapUnrollMemoryMap;
   }

   public long org$apache$spark$storage$memory$MemoryStore$$unrollMemoryThreshold() {
      return this.org$apache$spark$storage$memory$MemoryStore$$unrollMemoryThreshold;
   }

   public long org$apache$spark$storage$memory$MemoryStore$$maxMemory() {
      return this.memoryManager.maxOnHeapStorageMemory() + this.memoryManager.maxOffHeapStorageMemory();
   }

   public long org$apache$spark$storage$memory$MemoryStore$$memoryUsed() {
      return this.memoryManager.storageMemoryUsed();
   }

   public long org$apache$spark$storage$memory$MemoryStore$$blocksMemoryUsed() {
      Long var2;
      synchronized(this.memoryManager) {
         var2 = BoxesRunTime.boxToLong(this.org$apache$spark$storage$memory$MemoryStore$$memoryUsed() - this.currentUnrollMemory());
      }

      return BoxesRunTime.unboxToLong(var2);
   }

   public long getSize(BlockId blockId) {
      Long var3;
      synchronized(this.org$apache$spark$storage$memory$MemoryStore$$entries()) {
         var3 = BoxesRunTime.boxToLong(((MemoryEntry)this.org$apache$spark$storage$memory$MemoryStore$$entries().get(blockId)).size());
      }

      return BoxesRunTime.unboxToLong(var3);
   }

   public <T> boolean putBytes(BlockId blockId, long size, MemoryMode memoryMode, Function0<ChunkedByteBuffer> _bytes, ClassTag<T> evidence$2) {
      Predef$.MODULE$.require(!this.contains(blockId), new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " is already present in the MemoryStore"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$1}));
         }

         public {
            this.blockId$1 = blockId$1;
         }
      });
      boolean var10000;
      if (this.memoryManager.acquireStorageMemory(blockId, size, memoryMode)) {
         ChunkedByteBuffer bytes = (ChunkedByteBuffer)_bytes.apply();
         Predef$.MODULE$.assert(bytes.size() == size);
         SerializedMemoryEntry entry = new SerializedMemoryEntry(bytes, memoryMode, (ClassTag)Predef$.MODULE$.implicitly(evidence$2));
         synchronized(this.org$apache$spark$storage$memory$MemoryStore$$entries()) {
            this.org$apache$spark$storage$memory$MemoryStore$$entries().put(blockId, entry);
         }

         this.logInfo(new Serializable(this, blockId, size) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final MemoryStore $outer;
            private final BlockId blockId$1;
            private final long size$1;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("Block %s stored as bytes in memory (estimated size %s, free %s)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$1, Utils$.MODULE$.bytesToString(this.size$1), Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$memory$MemoryStore$$maxMemory() - this.$outer.org$apache$spark$storage$memory$MemoryStore$$blocksMemoryUsed())}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.blockId$1 = blockId$1;
                  this.size$1 = size$1;
               }
            }
         });
         var10000 = true;
      } else {
         var10000 = false;
      }

      return var10000;
   }

   private <T> Either<Object, Object> putIterator(BlockId blockId, Iterator<T> values, ClassTag<T> classTag, MemoryMode memoryMode, ValuesHolder<T> valuesHolder) {
      Predef$.MODULE$.require(!this.contains(blockId), new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$4;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " is already present in the MemoryStore"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$4}));
         }

         public {
            this.blockId$4 = blockId$4;
         }
      });
      int elementsUnrolled = 0;
      boolean keepUnrolling = true;
      long initialMemoryThreshold = this.org$apache$spark$storage$memory$MemoryStore$$unrollMemoryThreshold();
      long memoryCheckPeriod = BoxesRunTime.unboxToLong(this.conf.get(package$.MODULE$.UNROLL_MEMORY_CHECK_PERIOD()));
      long memoryThreshold = initialMemoryThreshold;
      double memoryGrowthFactor = BoxesRunTime.unboxToDouble(this.conf.get(package$.MODULE$.UNROLL_MEMORY_GROWTH_FACTOR()));
      long unrollMemoryUsedByThisBlock = 0L;
      keepUnrolling = this.reserveUnrollMemoryForThisTask(blockId, initialMemoryThreshold, memoryMode);
      if (keepUnrolling) {
         unrollMemoryUsedByThisBlock += initialMemoryThreshold;
      } else {
         this.logWarning(new Serializable(this, blockId, initialMemoryThreshold) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$4;
            private final long initialMemoryThreshold$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to reserve initial memory threshold of "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " for computing block ", " in memory."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.initialMemoryThreshold$1), this.blockId$4}))).toString();
            }

            public {
               this.blockId$4 = blockId$4;
               this.initialMemoryThreshold$1 = initialMemoryThreshold$1;
            }
         });
      }

      for(; values.hasNext() && keepUnrolling; ++elementsUnrolled) {
         valuesHolder.storeValue(values.next());
         if ((long)elementsUnrolled % memoryCheckPeriod == 0L) {
            long currentSize = valuesHolder.estimatedSize();
            if (currentSize >= memoryThreshold) {
               long amountToRequest = (long)((double)currentSize * memoryGrowthFactor - (double)memoryThreshold);
               keepUnrolling = this.reserveUnrollMemoryForThisTask(blockId, amountToRequest, memoryMode);
               if (keepUnrolling) {
                  unrollMemoryUsedByThisBlock += amountToRequest;
               }

               memoryThreshold += amountToRequest;
            }
         }
      }

      Object var34;
      if (keepUnrolling) {
         MemoryEntryBuilder entryBuilder = valuesHolder.getBuilder();
         long size = entryBuilder.preciseSize();
         if (size > unrollMemoryUsedByThisBlock) {
            long amountToRequest = size - unrollMemoryUsedByThisBlock;
            keepUnrolling = this.reserveUnrollMemoryForThisTask(blockId, amountToRequest, memoryMode);
            if (keepUnrolling) {
               unrollMemoryUsedByThisBlock += amountToRequest;
            }
         }

         if (keepUnrolling) {
            MemoryEntry entry = entryBuilder.build();
            synchronized(this.memoryManager) {
               this.releaseUnrollMemoryForThisTask(memoryMode, unrollMemoryUsedByThisBlock);
               boolean success = this.memoryManager.acquireStorageMemory(blockId, entry.size(), memoryMode);
               Predef$.MODULE$.assert(success, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "transferring unroll memory to storage memory failed";
                  }
               });
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

            synchronized(this.org$apache$spark$storage$memory$MemoryStore$$entries()) {
               this.org$apache$spark$storage$memory$MemoryStore$$entries().put(blockId, entry);
            }

            this.logInfo(new Serializable(this, blockId, entry) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final MemoryStore $outer;
               private final BlockId blockId$4;
               private final MemoryEntry entry$1;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Block %s stored as values in memory (estimated size %s, free %s)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$4, Utils$.MODULE$.bytesToString(this.entry$1.size()), Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$memory$MemoryStore$$maxMemory() - this.$outer.org$apache$spark$storage$memory$MemoryStore$$blocksMemoryUsed())}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$4 = blockId$4;
                     this.entry$1 = entry$1;
                  }
               }
            });
            var34 = scala.package$.MODULE$.Right().apply(BoxesRunTime.boxToLong(entry.size()));
         } else {
            this.logUnrollFailureMessage(blockId, entryBuilder.preciseSize());
            var34 = scala.package$.MODULE$.Left().apply(BoxesRunTime.boxToLong(unrollMemoryUsedByThisBlock));
         }
      } else {
         this.logUnrollFailureMessage(blockId, valuesHolder.estimatedSize());
         var34 = scala.package$.MODULE$.Left().apply(BoxesRunTime.boxToLong(unrollMemoryUsedByThisBlock));
      }

      return (Either<Object, Object>)var34;
   }

   public <T> Either<PartiallyUnrolledIterator<T>, Object> putIteratorAsValues(BlockId blockId, Iterator<T> values, ClassTag<T> classTag) {
      DeserializedValuesHolder valuesHolder = new DeserializedValuesHolder(classTag);
      Either var5 = this.putIterator(blockId, values, classTag, MemoryMode.ON_HEAP, valuesHolder);
      Object var9;
      if (var5 instanceof Right) {
         Right var6 = (Right)var5;
         long storedSize = BoxesRunTime.unboxToLong(var6.b());
         var9 = scala.package$.MODULE$.Right().apply(BoxesRunTime.boxToLong(storedSize));
      } else {
         if (!(var5 instanceof Left)) {
            throw new MatchError(var5);
         }

         Left var10 = (Left)var5;
         long unrollMemoryUsedByThisBlock = BoxesRunTime.unboxToLong(var10.a());
         Iterator unrolledIterator = valuesHolder.vector() == null ? Predef$.MODULE$.genericArrayOps(valuesHolder.arrayValues()).toIterator() : valuesHolder.vector().iterator();
         var9 = scala.package$.MODULE$.Left().apply(new PartiallyUnrolledIterator(this, MemoryMode.ON_HEAP, unrollMemoryUsedByThisBlock, unrolledIterator, values));
      }

      return (Either<PartiallyUnrolledIterator<T>, Object>)var9;
   }

   public <T> Either<PartiallySerializedBlock<T>, Object> putIteratorAsBytes(BlockId blockId, Iterator<T> values, ClassTag<T> classTag, MemoryMode memoryMode) {
      Predef$.MODULE$.require(!this.contains(blockId), new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " is already present in the MemoryStore"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$2}));
         }

         public {
            this.blockId$2 = blockId$2;
         }
      });
      long initialMemoryThreshold = this.org$apache$spark$storage$memory$MemoryStore$$unrollMemoryThreshold();
      int var10000;
      if (initialMemoryThreshold > 2147483647L) {
         this.logWarning(new Serializable(this, initialMemoryThreshold) {
            public static final long serialVersionUID = 0L;
            private final long initialMemoryThreshold$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Initial memory threshold of ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.initialMemoryThreshold$2)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"is too large to be set as chunk size. Chunk size has been capped to "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(2147483647L)}))).toString();
            }

            public {
               this.initialMemoryThreshold$2 = initialMemoryThreshold$2;
            }
         });
         var10000 = Integer.MAX_VALUE;
      } else {
         var10000 = (int)initialMemoryThreshold;
      }

      int chunkSize = var10000;
      SerializedValuesHolder valuesHolder = new SerializedValuesHolder(blockId, chunkSize, classTag, memoryMode, this.serializerManager);
      Either var9 = this.putIterator(blockId, values, classTag, memoryMode, valuesHolder);
      Object var13;
      if (var9 instanceof Right) {
         Right var10 = (Right)var9;
         long storedSize = BoxesRunTime.unboxToLong(var10.b());
         var13 = scala.package$.MODULE$.Right().apply(BoxesRunTime.boxToLong(storedSize));
      } else {
         if (!(var9 instanceof Left)) {
            throw new MatchError(var9);
         }

         Left var14 = (Left)var9;
         long unrollMemoryUsedByThisBlock = BoxesRunTime.unboxToLong(var14.a());
         var13 = scala.package$.MODULE$.Left().apply(new PartiallySerializedBlock(this, this.serializerManager, blockId, valuesHolder.serializationStream(), valuesHolder.redirectableStream(), unrollMemoryUsedByThisBlock, memoryMode, valuesHolder.bbos(), values, classTag));
      }

      return (Either<PartiallySerializedBlock<T>, Object>)var13;
   }

   public Option<ChunkedByteBuffer> getBytes(BlockId blockId) {
      Object var4;
      synchronized(this.org$apache$spark$storage$memory$MemoryStore$$entries()) {
         var4 = this.org$apache$spark$storage$memory$MemoryStore$$entries().get(blockId);
      }

      MemoryEntry entry = (MemoryEntry)var4;
      Object var6;
      if (entry == null) {
         var6 = None$.MODULE$;
      } else {
         if (entry instanceof DeserializedMemoryEntry) {
            throw new IllegalArgumentException("should only call getBytes on serialized blocks");
         }

         if (!(entry instanceof SerializedMemoryEntry)) {
            throw new MatchError(entry);
         }

         SerializedMemoryEntry var7 = (SerializedMemoryEntry)entry;
         ChunkedByteBuffer bytes = var7.buffer();
         var6 = new Some(bytes);
      }

      return (Option<ChunkedByteBuffer>)var6;
   }

   public Option<Iterator<?>> getValues(BlockId blockId) {
      Object var4;
      synchronized(this.org$apache$spark$storage$memory$MemoryStore$$entries()) {
         var4 = this.org$apache$spark$storage$memory$MemoryStore$$entries().get(blockId);
      }

      MemoryEntry entry = (MemoryEntry)var4;
      Object var6;
      if (entry == null) {
         var6 = None$.MODULE$;
      } else {
         if (entry instanceof SerializedMemoryEntry) {
            throw new IllegalArgumentException("should only call getValues on deserialized blocks");
         }

         if (!(entry instanceof DeserializedMemoryEntry)) {
            throw new MatchError(entry);
         }

         DeserializedMemoryEntry var7 = (DeserializedMemoryEntry)entry;
         Object values = var7.value();
         Some x = new Some(values);
         var6 = x.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Iterator<Object> apply(Object x$1) {
               return Predef$.MODULE$.genericArrayOps(x$1).iterator();
            }
         });
      }

      return (Option<Iterator<?>>)var6;
   }

   public boolean remove(BlockId blockId) {
      Boolean var3;
      synchronized(this.memoryManager) {
         Object var6;
         synchronized(this.org$apache$spark$storage$memory$MemoryStore$$entries()) {
            var6 = this.org$apache$spark$storage$memory$MemoryStore$$entries().remove(blockId);
         }

         MemoryEntry entry = (MemoryEntry)var6;
         Boolean var10000;
         if (entry == null) {
            var10000 = BoxesRunTime.boxToBoolean(false);
         } else {
            if (entry instanceof SerializedMemoryEntry) {
               SerializedMemoryEntry var8 = (SerializedMemoryEntry)entry;
               ChunkedByteBuffer buffer = var8.buffer();
               buffer.dispose();
               BoxedUnit var10 = BoxedUnit.UNIT;
            } else {
               BoxedUnit var13 = BoxedUnit.UNIT;
            }

            this.memoryManager.releaseStorageMemory(entry.size(), entry.memoryMode());
            this.logDebug(new Serializable(this, blockId, entry) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final MemoryStore $outer;
               private final BlockId blockId$3;
               private final MemoryEntry entry$2;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " of size ", " dropped "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$3, BoxesRunTime.boxToLong(this.entry$2.size())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"from memory (free ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$memory$MemoryStore$$maxMemory() - this.$outer.org$apache$spark$storage$memory$MemoryStore$$blocksMemoryUsed())}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$3 = blockId$3;
                     this.entry$2 = entry$2;
                  }
               }
            });
            var10000 = BoxesRunTime.boxToBoolean(true);
         }

         var3 = var10000;
      }

      return BoxesRunTime.unboxToBoolean(var3);
   }

   public void clear() {
      synchronized(this.memoryManager) {
         synchronized(this.org$apache$spark$storage$memory$MemoryStore$$entries()) {
            this.org$apache$spark$storage$memory$MemoryStore$$entries().clear();
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         this.onHeapUnrollMemoryMap().clear();
         this.offHeapUnrollMemoryMap().clear();
         this.memoryManager.releaseAllStorageMemory();
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "MemoryStore cleared";
            }
         });
         BoxedUnit var5 = BoxedUnit.UNIT;
      }
   }

   public Option<Object> org$apache$spark$storage$memory$MemoryStore$$getRddId(BlockId blockId) {
      return blockId.asRDDId().<Object>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(RDDBlockId x$2) {
            return x$2.rddId();
         }
      });
   }

   public long evictBlocksToFreeSpace(Option<BlockId> blockId, long space, MemoryMode memoryMode) {
      Predef$.MODULE$.assert(space > 0L);
      Long var6;
      synchronized(this.memoryManager) {
         LongRef freedMemory = LongRef.create(0L);
         Option rddToAdd = blockId.flatMap(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final MemoryStore $outer;

            public final Option<Object> apply(BlockId blockId) {
               return this.$outer.org$apache$spark$storage$memory$MemoryStore$$getRddId(blockId);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         ArrayBuffer selectedBlocks = new ArrayBuffer();
         synchronized(this.org$apache$spark$storage$memory$MemoryStore$$entries()) {
            java.util.Iterator iterator = this.org$apache$spark$storage$memory$MemoryStore$$entries().entrySet().iterator();

            while(freedMemory.elem < space && iterator.hasNext()) {
               Map.Entry pair = (Map.Entry)iterator.next();
               BlockId blockId = (BlockId)pair.getKey();
               MemoryEntry entry = (MemoryEntry)pair.getValue();
               if (this.blockIsEvictable$1(blockId, entry, memoryMode, rddToAdd) && this.org$apache$spark$storage$memory$MemoryStore$$blockInfoManager.lockForWriting(blockId, false).isDefined()) {
                  selectedBlocks.$plus$eq(blockId);
                  freedMemory.elem += ((MemoryEntry)pair.getValue()).size();
               }
            }

            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         Long var23;
         if (freedMemory.elem >= space) {
            IntRef lastSuccessfulBlock = IntRef.create(-1);

            try {
               this.logInfo(new Serializable(this, freedMemory, selectedBlocks) {
                  public static final long serialVersionUID = 0L;
                  private final LongRef freedMemory$1;
                  private final ArrayBuffer selectedBlocks$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " blocks selected for dropping "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.selectedBlocks$1.size())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " bytes)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.freedMemory$1.elem)}))).toString();
                  }

                  public {
                     this.freedMemory$1 = freedMemory$1;
                     this.selectedBlocks$1 = selectedBlocks$1;
                  }
               });
               RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), selectedBlocks.size()).foreach$mVc$sp(new Serializable(this, selectedBlocks, lastSuccessfulBlock) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final MemoryStore $outer;
                  private final ArrayBuffer selectedBlocks$1;
                  private final IntRef lastSuccessfulBlock$1;

                  public final void apply(int idx) {
                     this.apply$mcVI$sp(idx);
                  }

                  public void apply$mcVI$sp(int idx) {
                     BlockId blockId = (BlockId)this.selectedBlocks$1.apply(idx);
                     Object var5;
                     synchronized(this.$outer.org$apache$spark$storage$memory$MemoryStore$$entries()) {
                        var5 = this.$outer.org$apache$spark$storage$memory$MemoryStore$$entries().get(blockId);
                     }

                     MemoryEntry entry = (MemoryEntry)var5;
                     if (entry != null) {
                        this.$outer.org$apache$spark$storage$memory$MemoryStore$$dropBlock$1(blockId, entry);
                        this.$outer.afterDropAction(blockId);
                     }

                     this.lastSuccessfulBlock$1.elem = idx;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.selectedBlocks$1 = selectedBlocks$1;
                        this.lastSuccessfulBlock$1 = lastSuccessfulBlock$1;
                     }
                  }
               });
               this.logInfo(new Serializable(this, selectedBlocks) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final MemoryStore $outer;
                  private final ArrayBuffer selectedBlocks$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"After dropping ", " blocks, "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.selectedBlocks$1.size())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"free memory is ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$memory$MemoryStore$$maxMemory() - this.$outer.org$apache$spark$storage$memory$MemoryStore$$blocksMemoryUsed())}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.selectedBlocks$1 = selectedBlocks$1;
                     }
                  }
               });
               var23 = BoxesRunTime.boxToLong(freedMemory.elem);
            } finally {
               if (lastSuccessfulBlock.elem != selectedBlocks.size() - 1) {
                  RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(lastSuccessfulBlock.elem + 1), selectedBlocks.size()).foreach$mVc$sp(new Serializable(this, selectedBlocks) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final MemoryStore $outer;
                     private final ArrayBuffer selectedBlocks$1;

                     public final void apply(int idx) {
                        this.apply$mcVI$sp(idx);
                     }

                     public void apply$mcVI$sp(int idx) {
                        BlockId blockId = (BlockId)this.selectedBlocks$1.apply(idx);
                        this.$outer.org$apache$spark$storage$memory$MemoryStore$$blockInfoManager.unlock(blockId, this.$outer.org$apache$spark$storage$memory$MemoryStore$$blockInfoManager.unlock$default$2());
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.selectedBlocks$1 = selectedBlocks$1;
                        }
                     }
                  });
               }

            }
         } else {
            blockId.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final MemoryStore $outer;

               public final void apply(BlockId id) {
                  this.$outer.logInfo(new Serializable(this, id) {
                     public static final long serialVersionUID = 0L;
                     private final BlockId id$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Will not store ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.id$1}));
                     }

                     public {
                        this.id$1 = id$1;
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
            selectedBlocks.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final MemoryStore $outer;

               public final void apply(BlockId id) {
                  this.$outer.org$apache$spark$storage$memory$MemoryStore$$blockInfoManager.unlock(id, this.$outer.org$apache$spark$storage$memory$MemoryStore$$blockInfoManager.unlock$default$2());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            var23 = BoxesRunTime.boxToLong(0L);
         }

         var6 = var23;
      }

      return BoxesRunTime.unboxToLong(var6);
   }

   public void afterDropAction(BlockId blockId) {
   }

   public boolean contains(BlockId blockId) {
      Boolean var3;
      synchronized(this.org$apache$spark$storage$memory$MemoryStore$$entries()) {
         var3 = BoxesRunTime.boxToBoolean(this.org$apache$spark$storage$memory$MemoryStore$$entries().containsKey(blockId));
      }

      return BoxesRunTime.unboxToBoolean(var3);
   }

   private long currentTaskAttemptId() {
      return BoxesRunTime.unboxToLong(Option$.MODULE$.apply(TaskContext$.MODULE$.get()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TaskContext x$3) {
            return x$3.taskAttemptId();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return -1L;
         }
      }));
   }

   public boolean reserveUnrollMemoryForThisTask(BlockId blockId, long memory, MemoryMode memoryMode) {
      Boolean var6;
      synchronized(this.memoryManager) {
         boolean success = this.memoryManager.acquireUnrollMemory(blockId, memory, memoryMode);
         if (success) {
            long taskAttemptId = this.currentTaskAttemptId();
            HashMap unrollMemoryMap;
            if (MemoryMode.ON_HEAP.equals(memoryMode)) {
               unrollMemoryMap = this.onHeapUnrollMemoryMap();
            } else {
               if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
                  throw new MatchError(memoryMode);
               }

               unrollMemoryMap = this.offHeapUnrollMemoryMap();
            }

            unrollMemoryMap.update(BoxesRunTime.boxToLong(taskAttemptId), BoxesRunTime.boxToLong(BoxesRunTime.unboxToLong(unrollMemoryMap.getOrElse(BoxesRunTime.boxToLong(taskAttemptId), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            })) + memory));
         }

         var6 = BoxesRunTime.boxToBoolean(success);
      }

      return BoxesRunTime.unboxToBoolean(var6);
   }

   public void releaseUnrollMemoryForThisTask(MemoryMode memoryMode, long memory) {
      long taskAttemptId = this.currentTaskAttemptId();
      synchronized(this.memoryManager) {
         HashMap var9;
         if (MemoryMode.ON_HEAP.equals(memoryMode)) {
            var9 = this.onHeapUnrollMemoryMap();
         } else {
            if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
               throw new MatchError(memoryMode);
            }

            var9 = this.offHeapUnrollMemoryMap();
         }

         if (var9.contains(BoxesRunTime.boxToLong(taskAttemptId))) {
            long memoryToRelease = scala.math.package$.MODULE$.min(memory, BoxesRunTime.unboxToLong(var9.apply(BoxesRunTime.boxToLong(taskAttemptId))));
            if (memoryToRelease > 0L) {
               var9.update(BoxesRunTime.boxToLong(taskAttemptId), BoxesRunTime.boxToLong(BoxesRunTime.unboxToLong(var9.apply(BoxesRunTime.boxToLong(taskAttemptId))) - memoryToRelease));
               this.memoryManager.releaseUnrollMemory(memoryToRelease, memoryMode);
            }

            if (BoxesRunTime.unboxToLong(var9.apply(BoxesRunTime.boxToLong(taskAttemptId))) == 0L) {
               var9.remove(BoxesRunTime.boxToLong(taskAttemptId));
            } else {
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }
         } else {
            BoxedUnit var13 = BoxedUnit.UNIT;
         }

      }
   }

   public long releaseUnrollMemoryForThisTask$default$2() {
      return Long.MAX_VALUE;
   }

   public long currentUnrollMemory() {
      Long var2;
      synchronized(this.memoryManager) {
         var2 = BoxesRunTime.boxToLong(BoxesRunTime.unboxToLong(this.onHeapUnrollMemoryMap().values().sum(Numeric.LongIsIntegral$.MODULE$)) + BoxesRunTime.unboxToLong(this.offHeapUnrollMemoryMap().values().sum(Numeric.LongIsIntegral$.MODULE$)));
      }

      return BoxesRunTime.unboxToLong(var2);
   }

   public long currentUnrollMemoryForThisTask() {
      Long var2;
      synchronized(this.memoryManager) {
         var2 = BoxesRunTime.boxToLong(BoxesRunTime.unboxToLong(this.onHeapUnrollMemoryMap().getOrElse(BoxesRunTime.boxToLong(this.currentTaskAttemptId()), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply() {
               return this.apply$mcJ$sp();
            }

            public long apply$mcJ$sp() {
               return 0L;
            }
         })) + BoxesRunTime.unboxToLong(this.offHeapUnrollMemoryMap().getOrElse(BoxesRunTime.boxToLong(this.currentTaskAttemptId()), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply() {
               return this.apply$mcJ$sp();
            }

            public long apply$mcJ$sp() {
               return 0L;
            }
         })));
      }

      return BoxesRunTime.unboxToLong(var2);
   }

   public int org$apache$spark$storage$memory$MemoryStore$$numTasksUnrolling() {
      Integer var2;
      synchronized(this.memoryManager) {
         var2 = BoxesRunTime.boxToInteger(((TraversableOnce)this.onHeapUnrollMemoryMap().keys().$plus$plus(this.offHeapUnrollMemoryMap().keys(), Iterable$.MODULE$.canBuildFrom())).toSet().size());
      }

      return BoxesRunTime.unboxToInt(var2);
   }

   private void logMemoryUsage() {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MemoryStore $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Memory use = ", " (blocks) + "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$memory$MemoryStore$$blocksMemoryUsed())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " (scratch space shared across "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.currentUnrollMemory())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " tasks(s)) = ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$storage$memory$MemoryStore$$numTasksUnrolling()), Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$memory$MemoryStore$$memoryUsed())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Storage limit = ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$memory$MemoryStore$$maxMemory())}))).toString();
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

   private void logUnrollFailureMessage(BlockId blockId, long finalVectorSize) {
      this.logWarning(new Serializable(this, blockId, finalVectorSize) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$5;
         private final long finalVectorSize$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not enough space to cache ", " in memory! "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$5}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(computed ", " so far)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.finalVectorSize$1)}))).toString();
         }

         public {
            this.blockId$5 = blockId$5;
            this.finalVectorSize$1 = finalVectorSize$1;
         }
      });
      this.logMemoryUsage();
   }

   private final boolean blockIsEvictable$1(BlockId blockId, MemoryEntry entry, MemoryMode memoryMode$1, Option rddToAdd$1) {
      boolean var7;
      label36: {
         label31: {
            MemoryMode var10000 = entry.memoryMode();
            if (var10000 == null) {
               if (memoryMode$1 != null) {
                  break label31;
               }
            } else if (!var10000.equals(memoryMode$1)) {
               break label31;
            }

            if (rddToAdd$1.isEmpty()) {
               break label36;
            }

            Option var6 = this.org$apache$spark$storage$memory$MemoryStore$$getRddId(blockId);
            if (rddToAdd$1 == null) {
               if (var6 != null) {
                  break label36;
               }
            } else if (!rddToAdd$1.equals(var6)) {
               break label36;
            }
         }

         var7 = false;
         return var7;
      }

      var7 = true;
      return var7;
   }

   public final void org$apache$spark$storage$memory$MemoryStore$$dropBlock$1(BlockId blockId, MemoryEntry entry) {
      Object data;
      if (entry instanceof DeserializedMemoryEntry) {
         DeserializedMemoryEntry var5 = (DeserializedMemoryEntry)entry;
         Object values = var5.value();
         data = scala.package$.MODULE$.Left().apply(values);
      } else {
         if (!(entry instanceof SerializedMemoryEntry)) {
            throw new MatchError(entry);
         }

         SerializedMemoryEntry var8 = (SerializedMemoryEntry)entry;
         ChunkedByteBuffer buffer = var8.buffer();
         data = scala.package$.MODULE$.Right().apply(buffer);
      }

      StorageLevel newEffectiveStorageLevel = this.blockEvictionHandler.dropFromMemory(blockId, new Serializable(this, (Either)data) {
         public static final long serialVersionUID = 0L;
         private final Either data$1;

         public final Product apply() {
            return this.data$1;
         }

         public {
            this.data$1 = data$1;
         }
      }, entry.classTag());
      if (newEffectiveStorageLevel.isValid()) {
         this.org$apache$spark$storage$memory$MemoryStore$$blockInfoManager.unlock(blockId, this.org$apache$spark$storage$memory$MemoryStore$$blockInfoManager.unlock$default$2());
      } else {
         this.org$apache$spark$storage$memory$MemoryStore$$blockInfoManager.removeBlock(blockId);
      }

   }

   public MemoryStore(SparkConf conf, BlockInfoManager blockInfoManager, SerializerManager serializerManager, MemoryManager memoryManager, BlockEvictionHandler blockEvictionHandler) {
      this.conf = conf;
      this.org$apache$spark$storage$memory$MemoryStore$$blockInfoManager = blockInfoManager;
      this.serializerManager = serializerManager;
      this.memoryManager = memoryManager;
      this.blockEvictionHandler = blockEvictionHandler;
      Logging$class.$init$(this);
      this.org$apache$spark$storage$memory$MemoryStore$$entries = new LinkedHashMap(32, 0.75F, true);
      this.onHeapUnrollMemoryMap = (HashMap)HashMap$.MODULE$.apply(Nil$.MODULE$);
      this.offHeapUnrollMemoryMap = (HashMap)HashMap$.MODULE$.apply(Nil$.MODULE$);
      this.org$apache$spark$storage$memory$MemoryStore$$unrollMemoryThreshold = conf.getLong("spark.storage.unrollMemoryThreshold", 1048576L);
      if (this.org$apache$spark$storage$memory$MemoryStore$$maxMemory() < this.org$apache$spark$storage$memory$MemoryStore$$unrollMemoryThreshold()) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final MemoryStore $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Max memory ", " is less than the initial memory "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$memory$MemoryStore$$maxMemory())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"threshold ", " needed to store a block in "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$memory$MemoryStore$$unrollMemoryThreshold())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"memory. Please configure Spark with more memory."})))).s(Nil$.MODULE$)).toString();
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

      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final MemoryStore $outer;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("MemoryStore started with capacity %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$memory$MemoryStore$$maxMemory())}));
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
