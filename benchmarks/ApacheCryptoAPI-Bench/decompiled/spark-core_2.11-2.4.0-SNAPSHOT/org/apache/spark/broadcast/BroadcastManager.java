package org.apache.spark.broadcast;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]b!B\u0001\u0003\u0001\u0011Q!\u0001\u0005\"s_\u0006$7-Y:u\u001b\u0006t\u0017mZ3s\u0015\t\u0019A!A\u0005ce>\fGmY1ti*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0016\u001b\u0005\u0019\"B\u0001\u000b\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\f\u0014\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0007\u0001\u0003\u0006\u0004%\tAG\u0001\tSN$%/\u001b<fe\u000e\u0001Q#A\u000e\u0011\u00051a\u0012BA\u000f\u000e\u0005\u001d\u0011un\u001c7fC:D\u0001b\b\u0001\u0003\u0002\u0003\u0006IaG\u0001\nSN$%/\u001b<fe\u0002B\u0001\"\t\u0001\u0003\u0002\u0003\u0006IAI\u0001\u0005G>tg\r\u0005\u0002$I5\tA!\u0003\u0002&\t\tI1\u000b]1sW\u000e{gN\u001a\u0005\tO\u0001\u0011\t\u0011)A\u0005Q\u0005y1/Z2ve&$\u00180T1oC\u001e,'\u000f\u0005\u0002$S%\u0011!\u0006\u0002\u0002\u0010'\u0016\u001cWO]5us6\u000bg.Y4fe\")A\u0006\u0001C\u0001[\u00051A(\u001b8jiz\"BA\f\u00192eA\u0011q\u0006A\u0007\u0002\u0005!)\u0001d\u000ba\u00017!)\u0011e\u000ba\u0001E!)qe\u000ba\u0001Q!9A\u0007\u0001a\u0001\n\u0013Q\u0012aC5oSRL\u0017\r\\5{K\u0012DqA\u000e\u0001A\u0002\u0013%q'A\bj]&$\u0018.\u00197ju\u0016$w\fJ3r)\tA4\b\u0005\u0002\rs%\u0011!(\u0004\u0002\u0005+:LG\u000fC\u0004=k\u0005\u0005\t\u0019A\u000e\u0002\u0007a$\u0013\u0007\u0003\u0004?\u0001\u0001\u0006KaG\u0001\rS:LG/[1mSj,G\r\t\u0005\b\u0001\u0002\u0001\r\u0011\"\u0003B\u0003A\u0011'o\\1eG\u0006\u001cHOR1di>\u0014\u00180F\u0001C!\ty3)\u0003\u0002E\u0005\t\u0001\"I]8bI\u000e\f7\u000f\u001e$bGR|'/\u001f\u0005\b\r\u0002\u0001\r\u0011\"\u0003H\u0003Q\u0011'o\\1eG\u0006\u001cHOR1di>\u0014\u0018p\u0018\u0013fcR\u0011\u0001\b\u0013\u0005\by\u0015\u000b\t\u00111\u0001C\u0011\u0019Q\u0005\u0001)Q\u0005\u0005\u0006\t\"M]8bI\u000e\f7\u000f\u001e$bGR|'/\u001f\u0011\t\u000b1\u0003A\u0011B'\u0002\u0015%t\u0017\u000e^5bY&TX\rF\u00019\u0011\u0015y\u0005\u0001\"\u0001N\u0003\u0011\u0019Ho\u001c9\t\u000fE\u0003!\u0019!C\u0005%\u0006ya.\u001a=u\u0005J|\u0017\rZ2bgRLE-F\u0001T!\t!V,D\u0001V\u0015\t1v+\u0001\u0004bi>l\u0017n\u0019\u0006\u00031f\u000b!bY8oGV\u0014(/\u001a8u\u0015\tQ6,\u0001\u0003vi&d'\"\u0001/\u0002\t)\fg/Y\u0005\u0003=V\u0013!\"\u0011;p[&\u001cGj\u001c8h\u0011\u0019\u0001\u0007\u0001)A\u0005'\u0006\u0001b.\u001a=u\u0005J|\u0017\rZ2bgRLE\r\t\u0005\tE\u0002\u0011\r\u0011\"\u0001\u0003G\u0006a1-Y2iK\u00124\u0016\r\\;fgV\tA\r\u0005\u0002fY6\taM\u0003\u0002hQ\u0006\u0019Q.\u00199\u000b\u0005%T\u0017aC2pY2,7\r^5p]NT!a\u001b\u0004\u0002\u000f\r|W.\\8og&\u0011QN\u001a\u0002\r%\u00164WM]3oG\u0016l\u0015\r\u001d\u0005\u0007_\u0002\u0001\u000b\u0011\u00023\u0002\u001b\r\f7\r[3e-\u0006dW/Z:!\u0011\u0015\t\b\u0001\"\u0001s\u00031qWm\u001e\"s_\u0006$7-Y:u+\t\u0019(\u0010F\u0003u\u0003/\tY\u0002F\u0002v\u0003\u000f\u00012a\f<y\u0013\t9(AA\u0005Ce>\fGmY1tiB\u0011\u0011P\u001f\u0007\u0001\t\u0015Y\bO1\u0001}\u0005\u0005!\u0016cA?\u0002\u0002A\u0011AB`\u0005\u0003\u007f6\u0011qAT8uQ&tw\rE\u0002\r\u0003\u0007I1!!\u0002\u000e\u0005\r\te.\u001f\u0005\n\u0003\u0013\u0001\u0018\u0011!a\u0002\u0003\u0017\t!\"\u001a<jI\u0016t7-\u001a\u00132!\u0015\ti!a\u0005y\u001b\t\tyAC\u0002\u0002\u00125\tqA]3gY\u0016\u001cG/\u0003\u0003\u0002\u0016\u0005=!\u0001C\"mCN\u001cH+Y4\t\r\u0005e\u0001\u000f1\u0001y\u0003\u00191\u0018\r\\;f?\"1\u0011Q\u00049A\u0002m\tq![:M_\u000e\fG\u000eC\u0004\u0002\"\u0001!\t!a\t\u0002\u0017Ut'M]8bI\u000e\f7\u000f\u001e\u000b\bq\u0005\u0015\u0012qFA\u001a\u0011!\t9#a\bA\u0002\u0005%\u0012AA5e!\ra\u00111F\u0005\u0004\u0003[i!\u0001\u0002'p]\u001eDq!!\r\u0002 \u0001\u00071$\u0001\tsK6|g/\u001a$s_6$%/\u001b<fe\"9\u0011QGA\u0010\u0001\u0004Y\u0012\u0001\u00032m_\u000e\\\u0017N\\4"
)
public class BroadcastManager implements Logging {
   private final boolean isDriver;
   private final SparkConf conf;
   private final SecurityManager securityManager;
   private boolean initialized;
   private BroadcastFactory broadcastFactory;
   private final AtomicLong nextBroadcastId;
   private final ReferenceMap cachedValues;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   public boolean isDriver() {
      return this.isDriver;
   }

   private boolean initialized() {
      return this.initialized;
   }

   private void initialized_$eq(boolean x$1) {
      this.initialized = x$1;
   }

   private BroadcastFactory broadcastFactory() {
      return this.broadcastFactory;
   }

   private void broadcastFactory_$eq(BroadcastFactory x$1) {
      this.broadcastFactory = x$1;
   }

   private synchronized void initialize() {
      if (!this.initialized()) {
         this.broadcastFactory_$eq(new TorrentBroadcastFactory());
         this.broadcastFactory().initialize(this.isDriver(), this.conf, this.securityManager);
         this.initialized_$eq(true);
      }

   }

   public void stop() {
      this.broadcastFactory().stop();
   }

   private AtomicLong nextBroadcastId() {
      return this.nextBroadcastId;
   }

   public ReferenceMap cachedValues() {
      return this.cachedValues;
   }

   public <T> Broadcast<T> newBroadcast(T value_, boolean isLocal, ClassTag<T> evidence$1) {
      return this.broadcastFactory().<T>newBroadcast(value_, isLocal, this.nextBroadcastId().getAndIncrement(), evidence$1);
   }

   public void unbroadcast(long id, boolean removeFromDriver, boolean blocking) {
      this.broadcastFactory().unbroadcast(id, removeFromDriver, blocking);
   }

   public BroadcastManager(boolean isDriver, SparkConf conf, SecurityManager securityManager) {
      this.isDriver = isDriver;
      this.conf = conf;
      this.securityManager = securityManager;
      Logging$class.$init$(this);
      this.initialized = false;
      this.broadcastFactory = null;
      this.initialize();
      this.nextBroadcastId = new AtomicLong(0L);
      this.cachedValues = new ReferenceMap(0, 2);
   }
}
