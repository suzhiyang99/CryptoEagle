package org.apache.spark.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import org.apache.spark.executor.ShuffleWriteMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\t\u0015d!B\u0001\u0003\u0001\u0011Q!!\u0006#jg.\u0014En\\2l\u001f\nTWm\u0019;Xe&$XM\u001d\u0006\u0003\u0007\u0011\tqa\u001d;pe\u0006<WM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011b\u0005\t\u0003\u0019Ei\u0011!\u0004\u0006\u0003\u001d=\t!![8\u000b\u0003A\tAA[1wC&\u0011!#\u0004\u0002\r\u001fV$\b/\u001e;TiJ,\u0017-\u001c\t\u0003)]i\u0011!\u0006\u0006\u0003-\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00031U\u0011q\u0001T8hO&tw\r\u0003\u0005\u001b\u0001\t\u0015\r\u0011\"\u0001\u001d\u0003\u00111\u0017\u000e\\3\u0004\u0001U\tQ\u0004\u0005\u0002\r=%\u0011q$\u0004\u0002\u0005\r&dW\r\u0003\u0005\"\u0001\t\u0005\t\u0015!\u0003\u001e\u0003\u00151\u0017\u000e\\3!\u0011!\u0019\u0003A!A!\u0002\u0013!\u0013!E:fe&\fG.\u001b>fe6\u000bg.Y4feB\u0011Q\u0005K\u0007\u0002M)\u0011q\u0005B\u0001\u000bg\u0016\u0014\u0018.\u00197ju\u0016\u0014\u0018BA\u0015'\u0005E\u0019VM]5bY&TXM]'b]\u0006<WM\u001d\u0005\tW\u0001\u0011\t\u0011)A\u0005Y\u0005\u00112/\u001a:jC2L'0\u001a:J]N$\u0018M\\2f!\t)S&\u0003\u0002/M\t\u00112+\u001a:jC2L'0\u001a:J]N$\u0018M\\2f\u0011!\u0001\u0004A!A!\u0002\u0013\t\u0014A\u00032vM\u001a,'oU5{KB\u0011!'N\u0007\u0002g)\tA'A\u0003tG\u0006d\u0017-\u0003\u00027g\t\u0019\u0011J\u001c;\t\u0011a\u0002!\u0011!Q\u0001\ne\n!b]=oG^\u0013\u0018\u000e^3t!\t\u0011$(\u0003\u0002<g\t9!i\\8mK\u0006t\u0007\u0002C\u001f\u0001\u0005\u0003\u0005\u000b\u0011\u0002 \u0002\u0019]\u0014\u0018\u000e^3NKR\u0014\u0018nY:\u0011\u0005}\u0012U\"\u0001!\u000b\u0005\u0005#\u0011\u0001C3yK\u000e,Ho\u001c:\n\u0005\r\u0003%aE*ik\u001a4G.Z,sSR,W*\u001a;sS\u000e\u001c\b\u0002C#\u0001\u0005\u000b\u0007I\u0011\u0001$\u0002\u000f\tdwnY6JIV\tq\t\u0005\u0002I\u00136\t!!\u0003\u0002K\u0005\t9!\t\\8dW&#\u0007\u0002\u0003'\u0001\u0005\u0003\u0005\u000b\u0011B$\u0002\u0011\tdwnY6JI\u0002BQA\u0014\u0001\u0005\u0002=\u000ba\u0001P5oSRtD\u0003\u0003)R%N#VKV,\u0011\u0005!\u0003\u0001\"\u0002\u000eN\u0001\u0004i\u0002\"B\u0012N\u0001\u0004!\u0003\"B\u0016N\u0001\u0004a\u0003\"\u0002\u0019N\u0001\u0004\t\u0004\"\u0002\u001dN\u0001\u0004I\u0004\"B\u001fN\u0001\u0004q\u0004bB#N!\u0003\u0005\ra\u0012\u0004\b3\u0002\u0001\n1!\u0003[\u0005]i\u0015M\\;bY\u000ecwn]3PkR\u0004X\u000f^*ue\u0016\fWn\u0005\u0002Y\u0017!)A\f\u0017C\u0001;\u00061A%\u001b8ji\u0012\"\u0012A\u0018\t\u0003e}K!\u0001Y\u001a\u0003\tUs\u0017\u000e\u001e\u0005\u0007Eb\u0003J\u0011A/\u0002\u000b\rdwn]3\t\u000b\u0011DF\u0011A/\u0002\u00175\fg.^1m\u00072|7/\u001a\u0005\fMb\u0003\n1!A\u0001\n\u0013iv-A\u0006tkB,'\u000fJ2m_N,\u0017B\u00012\u0012\u0011\u001dI\u0007\u00011A\u0005\n)\fqa\u00195b]:,G.F\u0001l!\ta\u0017/D\u0001n\u0015\tqw.\u0001\u0005dQ\u0006tg.\u001a7t\u0015\t\u0001x\"A\u0002oS>L!A]7\u0003\u0017\u0019KG.Z\"iC:tW\r\u001c\u0005\bi\u0002\u0001\r\u0011\"\u0003v\u0003-\u0019\u0007.\u00198oK2|F%Z9\u0015\u0005y3\bbB<t\u0003\u0003\u0005\ra[\u0001\u0004q\u0012\n\u0004BB=\u0001A\u0003&1.\u0001\u0005dQ\u0006tg.\u001a7!\u0011\u001dY\b\u00011A\u0005\nq\f1!\\2t+\u0005i\bC\u0001@Y\u001b\u0005\u0001\u0001\"CA\u0001\u0001\u0001\u0007I\u0011BA\u0002\u0003\u001di7m]0%KF$2AXA\u0003\u0011\u001d9x0!AA\u0002uDq!!\u0003\u0001A\u0003&Q0\u0001\u0003nGN\u0004\u0003\"CA\u0007\u0001\u0001\u0007I\u0011BA\b\u0003\t\u00117/F\u0001\f\u0011%\t\u0019\u0002\u0001a\u0001\n\u0013\t)\"\u0001\u0004cg~#S-\u001d\u000b\u0004=\u0006]\u0001\u0002C<\u0002\u0012\u0005\u0005\t\u0019A\u0006\t\u000f\u0005m\u0001\u0001)Q\u0005\u0017\u0005\u0019!m\u001d\u0011\t\u0013\u0005}\u0001\u00011A\u0005\n\u0005\u0005\u0012a\u00014pgV\u0011\u00111\u0005\t\u0004\u0019\u0005\u0015\u0012bAA\u0014\u001b\t\u0001b)\u001b7f\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\n\u0003W\u0001\u0001\u0019!C\u0005\u0003[\tqAZ8t?\u0012*\u0017\u000fF\u0002_\u0003_A\u0011b^A\u0015\u0003\u0003\u0005\r!a\t\t\u0011\u0005M\u0002\u0001)Q\u0005\u0003G\tAAZ8tA!I\u0011q\u0007\u0001A\u0002\u0013%\u0011\u0011H\u0001\u0003iN,\"!a\u000f\u0011\u0007!\u000bi$C\u0002\u0002@\t\u0011\u0001\u0004V5nKR\u0013\u0018mY6j]\u001e|U\u000f\u001e9viN#(/Z1n\u0011%\t\u0019\u0005\u0001a\u0001\n\u0013\t)%\u0001\u0004ug~#S-\u001d\u000b\u0004=\u0006\u001d\u0003\"C<\u0002B\u0005\u0005\t\u0019AA\u001e\u0011!\tY\u0005\u0001Q!\n\u0005m\u0012a\u0001;tA!I\u0011q\n\u0001A\u0002\u0013%\u0011\u0011K\u0001\u0007_\nTw*\u001e;\u0016\u0005\u0005M\u0003cA\u0013\u0002V%\u0019\u0011q\u000b\u0014\u0003'M+'/[1mSj\fG/[8o'R\u0014X-Y7\t\u0013\u0005m\u0003\u00011A\u0005\n\u0005u\u0013AC8cU>+Ho\u0018\u0013fcR\u0019a,a\u0018\t\u0013]\fI&!AA\u0002\u0005M\u0003\u0002CA2\u0001\u0001\u0006K!a\u0015\u0002\u000f=\u0014'nT;uA!I\u0011q\r\u0001A\u0002\u0013%\u0011\u0011N\u0001\fS:LG/[1mSj,G-F\u0001:\u0011%\ti\u0007\u0001a\u0001\n\u0013\ty'A\bj]&$\u0018.\u00197ju\u0016$w\fJ3r)\rq\u0016\u0011\u000f\u0005\to\u0006-\u0014\u0011!a\u0001s!9\u0011Q\u000f\u0001!B\u0013I\u0014\u0001D5oSRL\u0017\r\\5{K\u0012\u0004\u0003\"CA=\u0001\u0001\u0007I\u0011BA5\u0003)\u0019HO]3b[>\u0003XM\u001c\u0005\n\u0003{\u0002\u0001\u0019!C\u0005\u0003\u007f\nab\u001d;sK\u0006lw\n]3o?\u0012*\u0017\u000fF\u0002_\u0003\u0003C\u0001b^A>\u0003\u0003\u0005\r!\u000f\u0005\b\u0003\u000b\u0003\u0001\u0015)\u0003:\u0003-\u0019HO]3b[>\u0003XM\u001c\u0011\t\u0013\u0005%\u0005\u00011A\u0005\n\u0005%\u0014!\u00045bg\n+WM\\\"m_N,G\rC\u0005\u0002\u000e\u0002\u0001\r\u0011\"\u0003\u0002\u0010\u0006\t\u0002.Y:CK\u0016t7\t\\8tK\u0012|F%Z9\u0015\u0007y\u000b\t\n\u0003\u0005x\u0003\u0017\u000b\t\u00111\u0001:\u0011\u001d\t)\n\u0001Q!\ne\na\u0002[1t\u0005\u0016,gn\u00117pg\u0016$\u0007\u0005C\u0005\u0002\u001a\u0002\u0001\r\u0011\"\u0003\u0002\u001c\u0006\t2m\\7nSR$X\r\u001a)pg&$\u0018n\u001c8\u0016\u0005\u0005u\u0005c\u0001\u001a\u0002 &\u0019\u0011\u0011U\u001a\u0003\t1{gn\u001a\u0005\n\u0003K\u0003\u0001\u0019!C\u0005\u0003O\u000bQcY8n[&$H/\u001a3Q_NLG/[8o?\u0012*\u0017\u000fF\u0002_\u0003SC\u0011b^AR\u0003\u0003\u0005\r!!(\t\u0011\u00055\u0006\u0001)Q\u0005\u0003;\u000b!cY8n[&$H/\u001a3Q_NLG/[8oA!I\u0011\u0011\u0017\u0001A\u0002\u0013%\u00111T\u0001\u0011e\u0016\u0004xN\u001d;fIB{7/\u001b;j_:D\u0011\"!.\u0001\u0001\u0004%I!a.\u0002)I,\u0007o\u001c:uK\u0012\u0004vn]5uS>tw\fJ3r)\rq\u0016\u0011\u0018\u0005\no\u0006M\u0016\u0011!a\u0001\u0003;C\u0001\"!0\u0001A\u0003&\u0011QT\u0001\u0012e\u0016\u0004xN\u001d;fIB{7/\u001b;j_:\u0004\u0003\"CAa\u0001\u0001\u0007I\u0011BAb\u0003EqW/\u001c*fG>\u0014Hm],sSR$XM\\\u000b\u0002c!I\u0011q\u0019\u0001A\u0002\u0013%\u0011\u0011Z\u0001\u0016]Vl'+Z2pe\u0012\u001cxK]5ui\u0016tw\fJ3r)\rq\u00161\u001a\u0005\to\u0006\u0015\u0017\u0011!a\u0001c!9\u0011q\u001a\u0001!B\u0013\t\u0014A\u00058v[J+7m\u001c:eg^\u0013\u0018\u000e\u001e;f]\u0002Ba!a5\u0001\t\u0013i\u0016AC5oSRL\u0017\r\\5{K\"9\u0011q\u001b\u0001\u0005\u0002\u0005e\u0017\u0001B8qK:$\u0012\u0001\u0015\u0005\u0007\u0003;\u0004A\u0011B/\u0002\u001d\rdwn]3SKN|WO]2fg\")!\r\u0001C!;\"9\u00111\u001d\u0001\u0005\u0002\u0005\u0015\u0018\u0001D2p[6LG/\u00118e\u000f\u0016$HCAAt!\rA\u0015\u0011^\u0005\u0004\u0003W\u0014!a\u0003$jY\u0016\u001cVmZ7f]RDq!a<\u0001\t\u0003\t\t0A\u000esKZ,'\u000f\u001e)beRL\u0017\r\\,sSR,7/\u00118e\u00072|7/\u001a\u000b\u0002;!9\u0011Q\u001f\u0001\u0005\u0002\u0005]\u0018!B<sSR,G#\u00020\u0002z\n\r\u0001\u0002CA~\u0003g\u0004\r!!@\u0002\u0007-,\u0017\u0010E\u00023\u0003\u007fL1A!\u00014\u0005\r\te.\u001f\u0005\t\u0005\u000b\t\u0019\u00101\u0001\u0002~\u0006)a/\u00197vK\"9\u0011Q\u001f\u0001\u0005B\t%Ac\u00010\u0003\f!9!Q\u0002B\u0004\u0001\u0004\t\u0014!\u00012\t\u000f\u0005U\b\u0001\"\u0011\u0003\u0012Q9aLa\u0005\u0003$\t\u001d\u0002\u0002\u0003B\u000b\u0005\u001f\u0001\rAa\u0006\u0002\u000f-4()\u001f;fgB)!G!\u0007\u0003\u001e%\u0019!1D\u001a\u0003\u000b\u0005\u0013(/Y=\u0011\u0007I\u0012y\"C\u0002\u0003\"M\u0012AAQ=uK\"9!Q\u0005B\b\u0001\u0004\t\u0014\u0001B8gMNDqA!\u000b\u0003\u0010\u0001\u0007\u0011'A\u0002mK:DaA!\f\u0001\t\u0003i\u0016!\u0004:fG>\u0014Hm\u0016:jiR,g\u000e\u0003\u0004\u00032\u0001!I!X\u0001\u0013kB$\u0017\r^3CsR,7o\u0016:jiR,g\u000eC\u0004\u00036\u0001!\t\u0005B/\u0002\u000b\u0019dWo\u001d5\b\u0015\te\"!!A\t\u0002\u0011\u0011Y$A\u000bESN\\'\t\\8dW>\u0013'.Z2u/JLG/\u001a:\u0011\u0007!\u0013iDB\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0003\u0003@M!!Q\bB!!\r\u0011$1I\u0005\u0004\u0005\u000b\u001a$AB!osJ+g\rC\u0004O\u0005{!\tA!\u0013\u0015\u0005\tm\u0002B\u0003B'\u0005{\t\n\u0011\"\u0001\u0003P\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uI]*\"A!\u0015+\u0007\u001d\u0013\u0019f\u000b\u0002\u0003VA!!q\u000bB1\u001b\t\u0011IF\u0003\u0003\u0003\\\tu\u0013!C;oG\",7m[3e\u0015\r\u0011yfM\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002\u0002B2\u00053\u0012\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0001"
)
public class DiskBlockObjectWriter extends OutputStream implements Logging {
   private final File file;
   private final SerializerManager serializerManager;
   private final SerializerInstance serializerInstance;
   public final int org$apache$spark$storage$DiskBlockObjectWriter$$bufferSize;
   private final boolean syncWrites;
   public final ShuffleWriteMetrics org$apache$spark$storage$DiskBlockObjectWriter$$writeMetrics;
   private final BlockId blockId;
   private FileChannel org$apache$spark$storage$DiskBlockObjectWriter$$channel;
   private ManualCloseOutputStream org$apache$spark$storage$DiskBlockObjectWriter$$mcs;
   private OutputStream org$apache$spark$storage$DiskBlockObjectWriter$$bs;
   private FileOutputStream org$apache$spark$storage$DiskBlockObjectWriter$$fos;
   private TimeTrackingOutputStream org$apache$spark$storage$DiskBlockObjectWriter$$ts;
   private SerializationStream org$apache$spark$storage$DiskBlockObjectWriter$$objOut;
   private boolean org$apache$spark$storage$DiskBlockObjectWriter$$initialized;
   private boolean org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen;
   private boolean org$apache$spark$storage$DiskBlockObjectWriter$$hasBeenClosed;
   private long org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition;
   private long org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition;
   private int org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static BlockId $lessinit$greater$default$7() {
      return DiskBlockObjectWriter$.MODULE$.$lessinit$greater$default$7();
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

   public File file() {
      return this.file;
   }

   public BlockId blockId() {
      return this.blockId;
   }

   private FileChannel org$apache$spark$storage$DiskBlockObjectWriter$$channel() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$channel;
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$channel_$eq(FileChannel x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$channel = x$1;
   }

   public ManualCloseOutputStream org$apache$spark$storage$DiskBlockObjectWriter$$mcs() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$mcs;
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$mcs_$eq(ManualCloseOutputStream x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$mcs = x$1;
   }

   private OutputStream org$apache$spark$storage$DiskBlockObjectWriter$$bs() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$bs;
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$bs_$eq(OutputStream x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$bs = x$1;
   }

   private FileOutputStream org$apache$spark$storage$DiskBlockObjectWriter$$fos() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$fos;
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$fos_$eq(FileOutputStream x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$fos = x$1;
   }

   public TimeTrackingOutputStream org$apache$spark$storage$DiskBlockObjectWriter$$ts() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$ts;
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$ts_$eq(TimeTrackingOutputStream x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$ts = x$1;
   }

   private SerializationStream org$apache$spark$storage$DiskBlockObjectWriter$$objOut() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$objOut;
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$objOut_$eq(SerializationStream x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$objOut = x$1;
   }

   public boolean org$apache$spark$storage$DiskBlockObjectWriter$$initialized() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$initialized;
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$initialized_$eq(boolean x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$initialized = x$1;
   }

   private boolean org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen;
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen_$eq(boolean x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen = x$1;
   }

   private boolean org$apache$spark$storage$DiskBlockObjectWriter$$hasBeenClosed() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$hasBeenClosed;
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$hasBeenClosed_$eq(boolean x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$hasBeenClosed = x$1;
   }

   public long org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition;
   }

   private void org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition_$eq(long x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition = x$1;
   }

   public long org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition;
   }

   private void org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition_$eq(long x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition = x$1;
   }

   public int org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten() {
      return this.org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten;
   }

   private void org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten_$eq(int x$1) {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten = x$1;
   }

   private void initialize() {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$fos_$eq(new FileOutputStream(this.file(), true));
      this.org$apache$spark$storage$DiskBlockObjectWriter$$channel_$eq(this.org$apache$spark$storage$DiskBlockObjectWriter$$fos().getChannel());
      this.org$apache$spark$storage$DiskBlockObjectWriter$$ts_$eq(new TimeTrackingOutputStream(this.org$apache$spark$storage$DiskBlockObjectWriter$$writeMetrics, this.org$apache$spark$storage$DiskBlockObjectWriter$$fos()));

      class ManualCloseBufferedOutputStream$1 extends BufferedOutputStream implements ManualCloseOutputStream {
         // $FF: synthetic field
         public final DiskBlockObjectWriter $outer;

         // $FF: synthetic method
         public void org$apache$spark$storage$DiskBlockObjectWriter$ManualCloseOutputStream$$super$close() {
            super.close();
         }

         public void close() {
            DiskBlockObjectWriter$ManualCloseOutputStream$class.close(this);
         }

         public void manualClose() {
            DiskBlockObjectWriter$ManualCloseOutputStream$class.manualClose(this);
         }

         // $FF: synthetic method
         public DiskBlockObjectWriter org$apache$spark$storage$DiskBlockObjectWriter$ManualCloseBufferedOutputStream$$$outer() {
            return this.$outer;
         }

         // $FF: synthetic method
         public DiskBlockObjectWriter org$apache$spark$storage$DiskBlockObjectWriter$ManualCloseOutputStream$$$outer() {
            return this.org$apache$spark$storage$DiskBlockObjectWriter$ManualCloseBufferedOutputStream$$$outer();
         }

         public ManualCloseBufferedOutputStream$1(DiskBlockObjectWriter $outer) {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               super($outer.org$apache$spark$storage$DiskBlockObjectWriter$$ts(), $outer.org$apache$spark$storage$DiskBlockObjectWriter$$bufferSize);
               DiskBlockObjectWriter$ManualCloseOutputStream$class.$init$(this);
            }
         }
      }

      this.org$apache$spark$storage$DiskBlockObjectWriter$$mcs_$eq(new ManualCloseBufferedOutputStream$1(this));
   }

   public DiskBlockObjectWriter open() {
      if (this.org$apache$spark$storage$DiskBlockObjectWriter$$hasBeenClosed()) {
         throw new IllegalStateException("Writer already closed. Cannot be reopened.");
      } else {
         if (!this.org$apache$spark$storage$DiskBlockObjectWriter$$initialized()) {
            this.initialize();
            this.org$apache$spark$storage$DiskBlockObjectWriter$$initialized_$eq(true);
         }

         this.org$apache$spark$storage$DiskBlockObjectWriter$$bs_$eq(this.serializerManager.wrapStream(this.blockId(), (OutputStream)this.org$apache$spark$storage$DiskBlockObjectWriter$$mcs()));
         this.org$apache$spark$storage$DiskBlockObjectWriter$$objOut_$eq(this.serializerInstance.serializeStream(this.org$apache$spark$storage$DiskBlockObjectWriter$$bs()));
         this.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen_$eq(true);
         return this;
      }
   }

   public void org$apache$spark$storage$DiskBlockObjectWriter$$closeResources() {
      if (this.org$apache$spark$storage$DiskBlockObjectWriter$$initialized()) {
         Utils$.MODULE$.tryWithSafeFinally(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DiskBlockObjectWriter $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$mcs().manualClose();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DiskBlockObjectWriter $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$channel_$eq((FileChannel)null);
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$mcs_$eq((ManualCloseOutputStream)null);
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$bs_$eq((OutputStream)null);
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$fos_$eq((FileOutputStream)null);
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$ts_$eq((TimeTrackingOutputStream)null);
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$objOut_$eq((SerializationStream)null);
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$initialized_$eq(false);
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen_$eq(false);
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$hasBeenClosed_$eq(true);
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

   public void close() {
      if (this.org$apache$spark$storage$DiskBlockObjectWriter$$initialized()) {
         Utils$.MODULE$.tryWithSafeFinally(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DiskBlockObjectWriter $outer;

            public final FileSegment apply() {
               return this.$outer.commitAndGet();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DiskBlockObjectWriter $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$closeResources();
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

   public FileSegment commitAndGet() {
      FileSegment var10000;
      if (this.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen()) {
         this.org$apache$spark$storage$DiskBlockObjectWriter$$objOut().flush();
         this.org$apache$spark$storage$DiskBlockObjectWriter$$bs().flush();
         this.org$apache$spark$storage$DiskBlockObjectWriter$$objOut().close();
         this.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen_$eq(false);
         if (this.syncWrites) {
            long start = System.nanoTime();
            this.org$apache$spark$storage$DiskBlockObjectWriter$$fos().getFD().sync();
            this.org$apache$spark$storage$DiskBlockObjectWriter$$writeMetrics.incWriteTime(System.nanoTime() - start);
         }

         long pos = this.org$apache$spark$storage$DiskBlockObjectWriter$$channel().position();
         FileSegment fileSegment = new FileSegment(this.file(), this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition(), pos - this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition());
         this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition_$eq(pos);
         this.org$apache$spark$storage$DiskBlockObjectWriter$$writeMetrics.incBytesWritten(this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition() - this.org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition());
         this.org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition_$eq(this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition());
         this.org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten_$eq(0);
         var10000 = fileSegment;
      } else {
         var10000 = new FileSegment(this.file(), this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition(), 0L);
      }

      return var10000;
   }

   public File revertPartialWritesAndClose() {
      Utils$.MODULE$.tryWithSafeFinally(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockObjectWriter $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            if (this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$initialized()) {
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$writeMetrics.decBytesWritten(this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition() - this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition());
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$writeMetrics.decRecordsWritten((long)this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten());
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen_$eq(false);
               this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$closeResources();
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockObjectWriter $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            FileOutputStream truncateStream = null;

            try {
               truncateStream = new FileOutputStream(this.$outer.file(), true);
               truncateStream.getChannel().truncate(this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition());
            } catch (Exception var6) {
               this.$outer.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Uncaught exception while reverting partial writes to file ").append((Object)this.$outer.org$apache$spark$storage$DiskBlockObjectWriter$$anonfun$$$outer().file()).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, var6);
            } finally {
               if (truncateStream != null) {
                  truncateStream.close();
                  FileOutputStream var8 = null;
               }

            }

         }

         // $FF: synthetic method
         public DiskBlockObjectWriter org$apache$spark$storage$DiskBlockObjectWriter$$anonfun$$$outer() {
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
      return this.file();
   }

   public void write(Object key, Object value) {
      if (this.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen()) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         this.open();
      }

      this.org$apache$spark$storage$DiskBlockObjectWriter$$objOut().writeKey(key, ClassTag$.MODULE$.Any());
      this.org$apache$spark$storage$DiskBlockObjectWriter$$objOut().writeValue(value, ClassTag$.MODULE$.Any());
      this.recordWritten();
   }

   public void write(int b) {
      throw new UnsupportedOperationException();
   }

   public void write(byte[] kvBytes, int offs, int len) {
      if (this.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen()) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         this.open();
      }

      this.org$apache$spark$storage$DiskBlockObjectWriter$$bs().write(kvBytes, offs, len);
   }

   public void recordWritten() {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten_$eq(this.org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten() + 1);
      this.org$apache$spark$storage$DiskBlockObjectWriter$$writeMetrics.incRecordsWritten(1L);
      if (this.org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten() % 16384 == 0) {
         this.updateBytesWritten();
      }

   }

   private void updateBytesWritten() {
      long pos = this.org$apache$spark$storage$DiskBlockObjectWriter$$channel().position();
      this.org$apache$spark$storage$DiskBlockObjectWriter$$writeMetrics.incBytesWritten(pos - this.org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition());
      this.org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition_$eq(pos);
   }

   public void flush() {
      this.org$apache$spark$storage$DiskBlockObjectWriter$$objOut().flush();
      this.org$apache$spark$storage$DiskBlockObjectWriter$$bs().flush();
   }

   public DiskBlockObjectWriter(File file, SerializerManager serializerManager, SerializerInstance serializerInstance, int bufferSize, boolean syncWrites, ShuffleWriteMetrics writeMetrics, BlockId blockId) {
      this.file = file;
      this.serializerManager = serializerManager;
      this.serializerInstance = serializerInstance;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$bufferSize = bufferSize;
      this.syncWrites = syncWrites;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$writeMetrics = writeMetrics;
      this.blockId = blockId;
      Logging$class.$init$(this);
      this.org$apache$spark$storage$DiskBlockObjectWriter$$channel = null;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$mcs = null;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$bs = null;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$fos = null;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$ts = null;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$objOut = null;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$initialized = false;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$streamOpen = false;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$hasBeenClosed = false;
      this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition = file.length();
      this.org$apache$spark$storage$DiskBlockObjectWriter$$reportedPosition = this.org$apache$spark$storage$DiskBlockObjectWriter$$committedPosition();
      this.org$apache$spark$storage$DiskBlockObjectWriter$$numRecordsWritten = 0;
   }

   public interface ManualCloseOutputStream {
      // $FF: synthetic method
      void org$apache$spark$storage$DiskBlockObjectWriter$ManualCloseOutputStream$$super$close();

      void close();

      void manualClose();

      // $FF: synthetic method
      DiskBlockObjectWriter org$apache$spark$storage$DiskBlockObjectWriter$ManualCloseOutputStream$$$outer();
   }
}
