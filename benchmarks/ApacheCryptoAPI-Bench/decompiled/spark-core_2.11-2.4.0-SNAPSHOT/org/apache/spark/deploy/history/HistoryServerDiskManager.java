package org.apache.spark.deploy.history;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Clock;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.kvstore.KVStore;
import org.apache.spark.util.kvstore.KVStoreIterator;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.generic.TraversableForwarder;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.ListBuffer$;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}g\u0001B\u0001\u0003\t5\u0011\u0001\u0004S5ti>\u0014\u0018pU3sm\u0016\u0014H)[:l\u001b\u0006t\u0017mZ3s\u0015\t\u0019A!A\u0004iSN$xN]=\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001\u00192\u0001\u0001\b\u0015!\ty!#D\u0001\u0011\u0015\u0005\t\u0012!B:dC2\f\u0017BA\n\u0011\u0005\u0019\te.\u001f*fMB\u0011Q\u0003G\u0007\u0002-)\u0011qCB\u0001\tS:$XM\u001d8bY&\u0011\u0011D\u0006\u0002\b\u0019><w-\u001b8h\u0011!Y\u0002A!A!\u0002\u0013a\u0012\u0001B2p]\u001a\u0004\"!\b\u0010\u000e\u0003\u0019I!a\b\u0004\u0003\u0013M\u0003\u0018M]6D_:4\u0007\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002\tA\fG\u000f\u001b\t\u0003G!j\u0011\u0001\n\u0006\u0003K\u0019\n!![8\u000b\u0003\u001d\nAA[1wC&\u0011\u0011\u0006\n\u0002\u0005\r&dW\r\u0003\u0005,\u0001\t\u0005\t\u0015!\u0003-\u0003\u001da\u0017n\u001d;j]\u001e\u0004\"!\f\u001a\u000e\u00039R!a\f\u0019\u0002\u000f-48\u000f^8sK*\u0011\u0011GB\u0001\u0005kRLG.\u0003\u00024]\t91JV*u_J,\u0007\u0002C\u001b\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001c\u0002\u000b\rdwnY6\u0011\u0005]BT\"\u0001\u0019\n\u0005e\u0002$!B\"m_\u000e\\\u0007\"B\u001e\u0001\t\u0003a\u0014A\u0002\u001fj]&$h\bF\u0003>\u007f\u0001\u000b%\t\u0005\u0002?\u00015\t!\u0001C\u0003\u001cu\u0001\u0007A\u0004C\u0003\"u\u0001\u0007!\u0005C\u0003,u\u0001\u0007A\u0006C\u00036u\u0001\u0007a\u0007C\u0004E\u0001\t\u0007I\u0011B#\u0002\u0017\u0005\u0004\bo\u0015;pe\u0016$\u0015N]\u000b\u0002E!1q\t\u0001Q\u0001\n\t\nA\"\u00199q'R|'/\u001a#je\u0002Bq!\u0013\u0001C\u0002\u0013%Q)A\u0006u[B\u001cFo\u001c:f\t&\u0014\bBB&\u0001A\u0003%!%\u0001\u0007u[B\u001cFo\u001c:f\t&\u0014\b\u0005C\u0004N\u0001\t\u0007I\u0011\u0002(\u0002\u00115\f\u00070V:bO\u0016,\u0012a\u0014\t\u0003\u001fAK!!\u0015\t\u0003\t1{gn\u001a\u0005\u0007'\u0002\u0001\u000b\u0011B(\u0002\u00135\f\u00070V:bO\u0016\u0004\u0003bB+\u0001\u0005\u0004%IAV\u0001\rGV\u0014(/\u001a8u+N\fw-Z\u000b\u0002/B\u0011\u0001LX\u0007\u00023*\u0011!lW\u0001\u0007CR|W.[2\u000b\u0005qk\u0016AC2p]\u000e,(O]3oi*\u0011\u0011GJ\u0005\u0003?f\u0013!\"\u0011;p[&\u001cGj\u001c8h\u0011\u0019\t\u0007\u0001)A\u0005/\u0006i1-\u001e:sK:$Xk]1hK\u0002Bqa\u0019\u0001C\u0002\u0013%a+\u0001\bd_6l\u0017\u000e\u001e;fIV\u001b\u0018mZ3\t\r\u0015\u0004\u0001\u0015!\u0003X\u0003=\u0019w.\\7jiR,G-V:bO\u0016\u0004\u0003bB4\u0001\u0005\u0004%I\u0001[\u0001\u0007C\u000e$\u0018N^3\u0016\u0003%\u0004BA[8r\u001f6\t1N\u0003\u0002m[\u00069Q.\u001e;bE2,'B\u00018\u0011\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003a.\u0014q\u0001S1tQ6\u000b\u0007\u000f\u0005\u0003\u0010eR\\\u0018BA:\u0011\u0005\u0019!V\u000f\u001d7feA\u0011Q\u000f\u001f\b\u0003\u001fYL!a\u001e\t\u0002\rA\u0013X\rZ3g\u0013\tI(P\u0001\u0004TiJLgn\u001a\u0006\u0003oB\u00012a\u0004?u\u0013\ti\bC\u0001\u0004PaRLwN\u001c\u0005\u0007\u007f\u0002\u0001\u000b\u0011B5\u0002\u000f\u0005\u001cG/\u001b<fA!9\u00111\u0001\u0001\u0005\u0002\u0005\u0015\u0011AC5oSRL\u0017\r\\5{KR\u0011\u0011q\u0001\t\u0004\u001f\u0005%\u0011bAA\u0006!\t!QK\\5u\u0011\u001d\ty\u0001\u0001C\u0001\u0003#\tQ\u0001\\3bg\u0016$b!a\u0005\u0002H\u0005-\u0003\u0003BA\u000b\u0003/i\u0011\u0001\u0001\u0004\b\u00033\u0001\u0001AAA\u000e\u0005\u0015aU-Y:f'\r\t9B\u0004\u0005\u000b\u0003?\t9B!b\u0001\n\u0003)\u0015a\u0002;naB\u000bG\u000f\u001b\u0005\u000b\u0003G\t9B!A!\u0002\u0013\u0011\u0013\u0001\u0003;naB\u000bG\u000f\u001b\u0011\t\u0015\u0005\u001d\u0012q\u0003BC\u0002\u0013%a*\u0001\u0004mK\u0006\u001cX\r\u001a\u0005\u000b\u0003W\t9B!A!\u0002\u0013y\u0015a\u00027fCN,G\r\t\u0005\bw\u0005]A\u0011AA\u0018)\u0019\t\u0019\"!\r\u00024!9\u0011qDA\u0017\u0001\u0004\u0011\u0003bBA\u0014\u0003[\u0001\ra\u0014\u0005\t\u0003o\t9\u0002\"\u0001\u0002:\u000511m\\7nSR$RAIA\u001e\u0003\u007fAq!!\u0010\u00026\u0001\u0007A/A\u0003baBLE\rC\u0004\u0002B\u0005U\u0002\u0019A>\u0002\u0013\u0005$H/Z7qi&#\u0007\u0002CA#\u0003/!\t!!\u0002\u0002\u0011I|G\u000e\u001c2bG.Dq!!\u0013\u0002\u000e\u0001\u0007q*\u0001\u0007fm\u0016tG\u000fT8h'&TX\r\u0003\u0006\u0002N\u00055\u0001\u0013!a\u0001\u0003\u001f\nA\"[:D_6\u0004(/Z:tK\u0012\u00042aDA)\u0013\r\t\u0019\u0006\u0005\u0002\b\u0005>|G.Z1o\u0011\u001d\t9\u0006\u0001C\u0001\u00033\n\u0011b\u001c9f]N#xN]3\u0015\r\u0005m\u0013QLA0!\ryAP\t\u0005\b\u0003{\t)\u00061\u0001u\u0011\u001d\t\t%!\u0016A\u0002mDq!a\u0019\u0001\t\u0003\t)'A\u0004sK2,\u0017m]3\u0015\u0011\u0005\u001d\u0011qMA5\u0003WBq!!\u0010\u0002b\u0001\u0007A\u000fC\u0004\u0002B\u0005\u0005\u0004\u0019A>\t\u0015\u00055\u0014\u0011\rI\u0001\u0002\u0004\ty%\u0001\u0004eK2,G/\u001a\u0005\b\u0003c\u0002A\u0011AA:\u0003=\t\u0007\u000f\u001d:pq&l\u0017\r^3TSj,G#B(\u0002v\u0005]\u0004bBA%\u0003_\u0002\ra\u0014\u0005\t\u0003\u001b\ny\u00071\u0001\u0002P!9\u00111\u0010\u0001\u0005\u0002\u0005u\u0014\u0001\u00024sK\u0016$\u0012a\u0014\u0005\b\u0003\u0003\u0003A\u0011AA?\u0003%\u0019w.\\7jiR,G\rC\u0004\u0002\u0006\u0002!I!a\"\u0002\u0017\u0011,G.\u001a;f'R|'/\u001a\u000b\u0005\u0003\u000f\tI\t\u0003\u0004\"\u0003\u0007\u0003\rA\t\u0005\b\u0003\u001b\u0003A\u0011BAH\u0003!i\u0017m[3S_>lG\u0003BA\u0004\u0003#Cq!a%\u0002\f\u0002\u0007q*\u0001\u0003tSj,\u0007bBAL\u0001\u0011%\u0011\u0011T\u0001\rCB\u00048\u000b^8sKB\u000bG\u000f\u001b\u000b\u0006E\u0005m\u0015Q\u0014\u0005\b\u0003{\t)\n1\u0001u\u0011\u001d\t\t%!&A\u0002mDq!!)\u0001\t\u0013\t\u0019+\u0001\tva\u0012\fG/Z!dG\u0016\u001c8\u000fV5nKR1\u0011qAAS\u0003OCq!!\u0010\u0002 \u0002\u0007A\u000fC\u0004\u0002B\u0005}\u0005\u0019A>\t\u000f\u0005-\u0006\u0001\"\u0003\u0002.\u0006YQ\u000f\u001d3bi\u0016,6/Y4f)\u0019\t9!a,\u00024\"9\u0011\u0011WAU\u0001\u0004y\u0015!\u00023fYR\f\u0007BCAA\u0003S\u0003\n\u00111\u0001\u0002P!A\u0011q\u0017\u0001\u0005\u0002\t\tI,\u0001\u0004tSj,wJ\u001a\u000b\u0004\u001f\u0006m\u0006BB\u0011\u00026\u0002\u0007!\u0005C\u0005\u0002@\u0002\t\n\u0011\"\u0001\u0002B\u0006\t\"/\u001a7fCN,G\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005\u0005\r'\u0006BA(\u0003\u000b\\#!a2\u0011\t\u0005%\u00171[\u0007\u0003\u0003\u0017TA!!4\u0002P\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003#\u0004\u0012AC1o]>$\u0018\r^5p]&!\u0011Q[Af\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\n\u00033\u0004\u0011\u0013!C\u0001\u0003\u0003\fq\u0002\\3bg\u0016$C-\u001a4bk2$HE\r\u0005\n\u0003;\u0004\u0011\u0013!C\u0005\u0003\u0003\fQ#\u001e9eCR,Wk]1hK\u0012\"WMZ1vYR$#\u0007"
)
public class HistoryServerDiskManager implements Logging {
   public final KVStore org$apache$spark$deploy$history$HistoryServerDiskManager$$listing;
   private final Clock clock;
   private final File appStoreDir;
   private final File tmpStoreDir;
   private final long org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage;
   private final AtomicLong org$apache$spark$deploy$history$HistoryServerDiskManager$$currentUsage;
   private final AtomicLong org$apache$spark$deploy$history$HistoryServerDiskManager$$committedUsage;
   private final HashMap<Tuple2<String, Option<String>>, Object> org$apache$spark$deploy$history$HistoryServerDiskManager$$active;
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

   private File appStoreDir() {
      return this.appStoreDir;
   }

   private File tmpStoreDir() {
      return this.tmpStoreDir;
   }

   public long org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage() {
      return this.org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage;
   }

   public AtomicLong org$apache$spark$deploy$history$HistoryServerDiskManager$$currentUsage() {
      return this.org$apache$spark$deploy$history$HistoryServerDiskManager$$currentUsage;
   }

   public AtomicLong org$apache$spark$deploy$history$HistoryServerDiskManager$$committedUsage() {
      return this.org$apache$spark$deploy$history$HistoryServerDiskManager$$committedUsage;
   }

   public HashMap<Tuple2<String, Option<String>>, Object> org$apache$spark$deploy$history$HistoryServerDiskManager$$active() {
      return this.org$apache$spark$deploy$history$HistoryServerDiskManager$$active;
   }

   public void initialize() {
      this.org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(this.sizeOf(this.appStoreDir()), true);
      Predef$.MODULE$.refArrayOps((Object[])this.tmpStoreDir().listFiles()).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(File x$1) {
            return FileUtils.deleteQuietly(x$1);
         }
      });
      Seq orphans = ((TraversableOnce)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.org$apache$spark$deploy$history$HistoryServerDiskManager$$listing.view(ApplicationStoreInfo.class)).asScala()).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ApplicationStoreInfo info) {
            return !(new File(info.path())).exists();
         }
      })).toSeq();
      orphans.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HistoryServerDiskManager $outer;

         public final void apply(ApplicationStoreInfo info) {
            this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$listing.delete(info.getClass(), info.path());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HistoryServerDiskManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Initialized disk manager: ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"current usage = ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$currentUsage().get())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"max usage = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage())}))).toString();
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

   public Lease lease(long eventLogSize, boolean isCompressed) {
      long needed = this.approximateSize(eventLogSize, isCompressed);
      this.org$apache$spark$deploy$history$HistoryServerDiskManager$$makeRoom(needed);
      Set perms = PosixFilePermissions.fromString("rwx------");
      File tmp = Files.createTempDirectory(this.tmpStoreDir().toPath(), "appstore", PosixFilePermissions.asFileAttribute(perms)).toFile();
      this.org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(needed, this.org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage$default$2());
      long current = this.org$apache$spark$deploy$history$HistoryServerDiskManager$$currentUsage().get();
      if (current > this.org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage()) {
         this.logInfo(new Serializable(this, needed, current) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final HistoryServerDiskManager $outer;
            private final long needed$1;
            private final long current$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Lease of ", " may cause usage to exceed max "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.needed$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " > ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.current$1), Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.needed$1 = needed$1;
                  this.current$1 = current$1;
               }
            }
         });
      }

      return new Lease(tmp, needed);
   }

   public boolean lease$default$2() {
      return false;
   }

   public Option<File> openStore(String appId, Option<String> attemptId) {
      Object var5;
      synchronized(this.org$apache$spark$deploy$history$HistoryServerDiskManager$$active()) {
         File path = this.org$apache$spark$deploy$history$HistoryServerDiskManager$$appStorePath(appId, attemptId);
         Object var10000;
         if (path.isDirectory()) {
            this.org$apache$spark$deploy$history$HistoryServerDiskManager$$active().update(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(appId), attemptId), BoxesRunTime.boxToLong(this.sizeOf(path)));
            var10000 = new Some(path);
         } else {
            var10000 = None$.MODULE$;
         }

         var5 = var10000;
      }

      Option storePath = (Option)var5;
      storePath.foreach(new Serializable(this, appId, attemptId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HistoryServerDiskManager $outer;
         private final String appId$2;
         private final Option attemptId$2;

         public final void apply(File path) {
            this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$updateAccessTime(this.appId$2, this.attemptId$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.appId$2 = appId$2;
               this.attemptId$2 = attemptId$2;
            }
         }
      });
      return storePath;
   }

   public void release(String appId, Option<String> attemptId, boolean delete) {
      Option var6;
      synchronized(this.org$apache$spark$deploy$history$HistoryServerDiskManager$$active()) {
         var6 = this.org$apache$spark$deploy$history$HistoryServerDiskManager$$active().remove(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(appId), attemptId));
      }

      Option oldSizeOpt = var6;
      oldSizeOpt.foreach(new Serializable(this, appId, attemptId, delete) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HistoryServerDiskManager $outer;
         private final String appId$1;
         private final Option attemptId$1;
         private final boolean delete$1;

         public final void apply(long oldSize) {
            this.apply$mcVJ$sp(oldSize);
         }

         public void apply$mcVJ$sp(long oldSize) {
            File path = this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$appStorePath(this.appId$1, this.attemptId$1);
            this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(-oldSize, true);
            if (path.isDirectory()) {
               if (this.delete$1) {
                  this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$deleteStore(path);
               } else {
                  long newSize = this.$outer.sizeOf(path);
                  ApplicationStoreInfo qual$1 = (ApplicationStoreInfo)this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$listing.read(ApplicationStoreInfo.class, path.getAbsolutePath());
                  String x$3 = qual$1.copy$default$1();
                  long x$4 = qual$1.copy$default$2();
                  String x$5 = qual$1.copy$default$3();
                  Option x$6 = qual$1.copy$default$4();
                  ApplicationStoreInfo newInfo = qual$1.copy(x$3, x$4, x$5, x$6, newSize);
                  this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$listing.write(newInfo);
                  this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(newSize, true);
               }
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.appId$1 = appId$1;
               this.attemptId$1 = attemptId$1;
               this.delete$1 = delete$1;
            }
         }
      });
   }

   public boolean release$default$3() {
      return false;
   }

   public long approximateSize(long eventLogSize, boolean isCompressed) {
      return isCompressed ? eventLogSize * 2L : eventLogSize / 2L;
   }

   public long free() {
      return package$.MODULE$.max(this.org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage() - this.org$apache$spark$deploy$history$HistoryServerDiskManager$$currentUsage().get(), 0L);
   }

   public long committed() {
      return this.org$apache$spark$deploy$history$HistoryServerDiskManager$$committedUsage().get();
   }

   public void org$apache$spark$deploy$history$HistoryServerDiskManager$$deleteStore(File path) {
      FileUtils.deleteDirectory(path);
      this.org$apache$spark$deploy$history$HistoryServerDiskManager$$listing.delete(ApplicationStoreInfo.class, path.getAbsolutePath());
   }

   public void org$apache$spark$deploy$history$HistoryServerDiskManager$$makeRoom(long size) {
      if (this.free() < size) {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not enough free space, looking at candidates for deletion..."})))).s(Nil$.MODULE$);
            }
         });
         ListBuffer evicted = new ListBuffer();
         Utils$.MODULE$.tryWithResource(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final HistoryServerDiskManager $outer;

            public final KVStoreIterator<ApplicationStoreInfo> apply() {
               return this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$listing.view(ApplicationStoreInfo.class).index("lastAccess").closeableIterator();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, new Serializable(this, size, evicted) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final HistoryServerDiskManager $outer;
            private final long size$1;
            private final ListBuffer evicted$1;

            public final void apply(KVStoreIterator<ApplicationStoreInfo> iter) {
               long needed = this.size$1;

               while(needed > 0L && iter.hasNext()) {
                  ApplicationStoreInfo info = (ApplicationStoreInfo)iter.next();
                  Boolean var7;
                  synchronized(this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$active()) {
                     var7 = BoxesRunTime.boxToBoolean(this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$active().contains(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(info.appId()), info.attemptId())));
                  }

                  boolean isActive = BoxesRunTime.unboxToBoolean(var7);
                  if (!isActive) {
                     this.evicted$1.$plus$eq(info);
                     needed -= info.size();
                  }
               }

            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.size$1 = size$1;
                  this.evicted$1 = evicted$1;
               }
            }
         });
         if (evicted.nonEmpty()) {
            long freed = BoxesRunTime.unboxToLong(((TraversableForwarder)evicted.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final HistoryServerDiskManager $outer;

               public final long apply(ApplicationStoreInfo info) {
                  this.$outer.logInfo(new Serializable(this, info) {
                     public static final long serialVersionUID = 0L;
                     private final ApplicationStoreInfo info$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Deleting store for ", "/", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.info$1.appId(), this.info$1.attemptId()}));
                     }

                     public {
                        this.info$1 = info$1;
                     }
                  });
                  this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$deleteStore(new File(info.path()));
                  this.$outer.org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(-info.size(), true);
                  return info.size();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, ListBuffer$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
            this.logInfo(new Serializable(this, size, evicted, freed) {
               public static final long serialVersionUID = 0L;
               private final long size$1;
               private final ListBuffer evicted$1;
               private final long freed$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Deleted ", " store(s) to free ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.evicted$1.size()), Utils$.MODULE$.bytesToString(this.freed$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(target = ", ")."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.size$1)}))).toString();
               }

               public {
                  this.size$1 = size$1;
                  this.evicted$1 = evicted$1;
                  this.freed$1 = freed$1;
               }
            });
         } else {
            this.logWarning(new Serializable(this, size) {
               public static final long serialVersionUID = 0L;
               private final long size$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unable to free any space to make room for ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.size$1)}));
               }

               public {
                  this.size$1 = size$1;
               }
            });
         }
      }

   }

   public File org$apache$spark$deploy$history$HistoryServerDiskManager$$appStorePath(String appId, Option<String> attemptId) {
      String fileName = (new StringBuilder()).append((Object)appId).append(attemptId.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$1) {
            return (new StringBuilder()).append((Object)"_").append((Object)x$1).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).append((Object)".ldb").toString();
      return new File(this.appStoreDir(), fileName);
   }

   public void org$apache$spark$deploy$history$HistoryServerDiskManager$$updateAccessTime(String appId, Option<String> attemptId) {
      File path = this.org$apache$spark$deploy$history$HistoryServerDiskManager$$appStorePath(appId, attemptId);
      ApplicationStoreInfo info = new ApplicationStoreInfo(path.getAbsolutePath(), this.clock.getTimeMillis(), appId, attemptId, this.sizeOf(path));
      this.org$apache$spark$deploy$history$HistoryServerDiskManager$$listing.write(info);
   }

   public void org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(long delta, boolean committed) {
      long updated = this.org$apache$spark$deploy$history$HistoryServerDiskManager$$currentUsage().addAndGet(delta);
      if (updated < 0L) {
         throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Disk usage tracker went negative (now = ", ", delta = ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(updated), BoxesRunTime.boxToLong(delta)})));
      } else {
         if (committed) {
            long updatedCommitted = this.org$apache$spark$deploy$history$HistoryServerDiskManager$$committedUsage().addAndGet(delta);
            if (updatedCommitted < 0L) {
               throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Disk usage tracker went negative (now = ", ", delta = ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(updatedCommitted), BoxesRunTime.boxToLong(delta)})));
            }
         }

      }
   }

   public boolean org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage$default$2() {
      return false;
   }

   public long sizeOf(File path) {
      return FileUtils.sizeOf(path);
   }

   public HistoryServerDiskManager(SparkConf conf, File path, KVStore listing, Clock clock) {
      this.org$apache$spark$deploy$history$HistoryServerDiskManager$$listing = listing;
      this.clock = clock;
      Logging$class.$init$(this);
      this.appStoreDir = new File(path, "apps");
      if (!this.appStoreDir().isDirectory() && !this.appStoreDir().mkdir()) {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to create app directory (", ")."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appStoreDir()})));
      } else {
         this.tmpStoreDir = new File(path, "temp");
         if (!this.tmpStoreDir().isDirectory() && !this.tmpStoreDir().mkdir()) {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to create temp directory (", ")."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.tmpStoreDir()})));
         } else {
            this.org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage = BoxesRunTime.unboxToLong(conf.get(config$.MODULE$.MAX_LOCAL_DISK_USAGE()));
            this.org$apache$spark$deploy$history$HistoryServerDiskManager$$currentUsage = new AtomicLong(0L);
            this.org$apache$spark$deploy$history$HistoryServerDiskManager$$committedUsage = new AtomicLong(0L);
            this.org$apache$spark$deploy$history$HistoryServerDiskManager$$active = new HashMap<Tuple2<String, Option<String>>, Object>();
         }
      }
   }

   public class Lease {
      private final File tmpPath;
      private final long leased;
      // $FF: synthetic field
      public final HistoryServerDiskManager $outer;

      public File tmpPath() {
         return this.tmpPath;
      }

      private long leased() {
         return this.leased;
      }

      public File commit(String appId, Option<String> attemptId) {
         File dst = this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$appStorePath(appId, attemptId);
         synchronized(this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$active()) {
            Predef$.MODULE$.require(!this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$active().contains(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(appId), attemptId)), new Serializable(this, appId, attemptId) {
               public static final long serialVersionUID = 0L;
               private final String appId$3;
               private final Option attemptId$3;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot commit lease for active application ", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$3, this.attemptId$3}));
               }

               public {
                  this.appId$3 = appId$3;
                  this.attemptId$3 = attemptId$3;
               }
            });
            if (dst.isDirectory()) {
               long size = this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().sizeOf(dst);
               this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$deleteStore(dst);
               this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(-size, true);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               BoxedUnit var14 = BoxedUnit.UNIT;
            }
         }

         this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(-this.leased(), this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage$default$2());
         long newSize = this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().sizeOf(this.tmpPath());
         this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$makeRoom(newSize);
         this.tmpPath().renameTo(dst);
         this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(newSize, true);
         if (this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$committedUsage().get() > this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage()) {
            String current = Utils$.MODULE$.bytesToString(this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$committedUsage().get());
            String max = Utils$.MODULE$.bytesToString(this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$maxUsage());
            this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().logWarning(new Serializable(this, appId, attemptId, current, max) {
               public static final long serialVersionUID = 0L;
               private final String appId$3;
               private final Option attemptId$3;
               private final String current$2;
               private final String max$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Commit of application ", " / ", " causes maximum disk usage to be "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$3, this.attemptId$3}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"exceeded (", " > ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.current$2, this.max$1}))).toString();
               }

               public {
                  this.appId$3 = appId$3;
                  this.attemptId$3 = attemptId$3;
                  this.current$2 = current$2;
                  this.max$1 = max$1;
               }
            });
         }

         this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$updateAccessTime(appId, attemptId);
         synchronized(this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$active()) {
            this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$active().update(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(appId), attemptId), BoxesRunTime.boxToLong(newSize));
            BoxedUnit var15 = BoxedUnit.UNIT;
            return dst;
         }
      }

      public void rollback() {
         this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage(-this.leased(), this.org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer().org$apache$spark$deploy$history$HistoryServerDiskManager$$updateUsage$default$2());
         FileUtils.deleteDirectory(this.tmpPath());
      }

      // $FF: synthetic method
      public HistoryServerDiskManager org$apache$spark$deploy$history$HistoryServerDiskManager$Lease$$$outer() {
         return this.$outer;
      }

      public Lease(File tmpPath, long leased) {
         this.tmpPath = tmpPath;
         this.leased = leased;
         if (HistoryServerDiskManager.this == null) {
            throw null;
         } else {
            this.$outer = HistoryServerDiskManager.this;
            super();
         }
      }
   }
}
