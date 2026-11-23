package org.apache.spark.deploy.history;

import com.codahale.metrics.Timer;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.servlet.DispatcherType;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.ui.SparkUI;
import org.apache.spark.util.Clock;
import org.slf4j.Logger;
import org.spark_project.guava.cache.CacheBuilder;
import org.spark_project.guava.cache.CacheLoader;
import org.spark_project.guava.cache.LoadingCache;
import org.spark_project.guava.cache.RemovalListener;
import org.spark_project.guava.cache.RemovalNotification;
import org.spark_project.guava.util.concurrent.UncheckedExecutionException;
import org.spark_project.jetty.servlet.FilterHolder;
import org.spark_project.jetty.servlet.ServletContextHandler;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\t5a!B\u0001\u0003\u0001\ta!\u0001E!qa2L7-\u0019;j_:\u001c\u0015m\u00195f\u0015\t\u0019A!A\u0004iSN$xN]=\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\r\u0001Qb\u0005\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005Q9R\"A\u000b\u000b\u0005Y1\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005a)\"a\u0002'pO\u001eLgn\u001a\u0005\t5\u0001\u0011)\u0019!C\u00019\u0005Qq\u000e]3sCRLwN\\:\u0004\u0001U\tQ\u0004\u0005\u0002\u001f?5\t!!\u0003\u0002!\u0005\tQ\u0012\t\u001d9mS\u000e\fG/[8o\u0007\u0006\u001c\u0007.Z(qKJ\fG/[8og\"A!\u0005\u0001B\u0001B\u0003%Q$A\u0006pa\u0016\u0014\u0018\r^5p]N\u0004\u0003\u0002\u0003\u0013\u0001\u0005\u000b\u0007I\u0011A\u0013\u0002)I,G/Y5oK\u0012\f\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8t+\u00051\u0003C\u0001\b(\u0013\tAsBA\u0002J]RD\u0001B\u000b\u0001\u0003\u0002\u0003\u0006IAJ\u0001\u0016e\u0016$\u0018-\u001b8fI\u0006\u0003\b\u000f\\5dCRLwN\\:!\u0011!a\u0003A!b\u0001\n\u0003i\u0013!B2m_\u000e\\W#\u0001\u0018\u0011\u0005=\u0012T\"\u0001\u0019\u000b\u0005E2\u0011\u0001B;uS2L!a\r\u0019\u0003\u000b\rcwnY6\t\u0011U\u0002!\u0011!Q\u0001\n9\naa\u00197pG.\u0004\u0003\"B\u001c\u0001\t\u0003A\u0014A\u0002\u001fj]&$h\b\u0006\u0003:umb\u0004C\u0001\u0010\u0001\u0011\u0015Qb\u00071\u0001\u001e\u0011\u0015!c\u00071\u0001'\u0011\u0015ac\u00071\u0001/\u0011\u001dq\u0004A1A\u0005\n}\n\u0011\"\u00199q\u0019>\fG-\u001a:\u0016\u0003\u0001\u0003B!\u0011&M\u001f6\t!I\u0003\u0002D\t\u0006)1-Y2iK*\u0011QIR\u0001\u0007G>lWn\u001c8\u000b\u0005\u001dC\u0015AB4p_\u001edWMC\u0001J\u0003\r\u0019w.\\\u0005\u0003\u0017\n\u00131bQ1dQ\u0016du.\u00193feB\u0011a$T\u0005\u0003\u001d\n\u0011\u0001bQ1dQ\u0016\\U-\u001f\t\u0003=AK!!\u0015\u0002\u0003\u0015\r\u000b7\r[3F]R\u0014\u0018\u0010\u0003\u0004T\u0001\u0001\u0006I\u0001Q\u0001\u000bCB\u0004Hj\\1eKJ\u0004\u0003bB+\u0001\u0005\u0004%IAV\u0001\u0010e\u0016lwN^1m\u0019&\u001cH/\u001a8feV\tqKE\u0002Y9\u00124A!\u0017.\u0001/\naAH]3gS:,W.\u001a8u}!11\f\u0001Q\u0001\n]\u000b\u0001C]3n_Z\fG\u000eT5ti\u0016tWM\u001d\u0011\u0011\u0005u\u0013W\"\u00010\u000b\u0005}\u0003\u0017\u0001\u00027b]\u001eT\u0011!Y\u0001\u0005U\u00064\u0018-\u0003\u0002d=\n1qJ\u00196fGR\u0004B!Q3M\u001f&\u0011aM\u0011\u0002\u0010%\u0016lwN^1m\u0019&\u001cH/\u001a8fe\"9\u0001\u000e\u0001b\u0001\n\u0013I\u0017\u0001C1qa\u000e\u000b7\r[3\u0016\u0003)\u0004B!Q6M\u001f&\u0011AN\u0011\u0002\r\u0019>\fG-\u001b8h\u0007\u0006\u001c\u0007.\u001a\u0005\u0007]\u0002\u0001\u000b\u0011\u00026\u0002\u0013\u0005\u0004\boQ1dQ\u0016\u0004\u0003b\u00029\u0001\u0005\u0004%\t!]\u0001\b[\u0016$(/[2t+\u0005\u0011\bC\u0001\u0010t\u0013\t!(A\u0001\u0007DC\u000eDW-T3ue&\u001c7\u000f\u0003\u0004w\u0001\u0001\u0006IA]\u0001\t[\u0016$(/[2tA!)\u0001\u0010\u0001C\u0001s\u0006\u0019q-\u001a;\u0015\t=S\u0018q\u0001\u0005\u0006w^\u0004\r\u0001`\u0001\u0006CB\u0004\u0018\n\u001a\t\u0004{\u0006\u0005aB\u0001\b\u007f\u0013\tyx\"\u0001\u0004Qe\u0016$WMZ\u0005\u0005\u0003\u0007\t)A\u0001\u0004TiJLgn\u001a\u0006\u0003\u007f>A\u0011\"!\u0003x!\u0003\u0005\r!a\u0003\u0002\u0013\u0005$H/Z7qi&#\u0007\u0003\u0002\b\u0002\u000eqL1!a\u0004\u0010\u0005\u0019y\u0005\u000f^5p]\"9\u00111\u0003\u0001\u0005\u0002\u0005U\u0011aC<ji\"\u001c\u0006/\u0019:l+&+B!a\u0006\u0002 Q1\u0011\u0011DA$\u0003\u0013\"B!a\u0007\u00022A!\u0011QDA\u0010\u0019\u0001!\u0001\"!\t\u0002\u0012\t\u0007\u00111\u0005\u0002\u0002)F!\u0011QEA\u0016!\rq\u0011qE\u0005\u0004\u0003Sy!a\u0002(pi\"Lgn\u001a\t\u0004\u001d\u00055\u0012bAA\u0018\u001f\t\u0019\u0011I\\=\t\u0011\u0005M\u0012\u0011\u0003a\u0001\u0003k\t!A\u001a8\u0011\u000f9\t9$a\u000f\u0002\u001c%\u0019\u0011\u0011H\b\u0003\u0013\u0019+hn\u0019;j_:\f\u0004\u0003BA\u001f\u0003\u0007j!!a\u0010\u000b\u0007\u0005\u0005c!\u0001\u0002vS&!\u0011QIA \u0005\u001d\u0019\u0006/\u0019:l+&Caa_A\t\u0001\u0004a\b\u0002CA\u0005\u0003#\u0001\r!a\u0003\t\u000f\u00055\u0003\u0001\"\u0001\u0002P\u0005!1/\u001b>f)\t\t\t\u0006E\u0002\u000f\u0003'J1!!\u0016\u0010\u0005\u0011auN\\4\t\u000f\u0005e\u0003\u0001\"\u0003\u0002\\\u0005!A/[7f+\u0011\ti&a\u0019\u0015\t\u0005}\u0013q\u000e\u000b\u0005\u0003C\n)\u0007\u0005\u0003\u0002\u001e\u0005\rD\u0001CA\u0011\u0003/\u0012\r!a\t\t\u0013\u0005\u001d\u0014q\u000bCA\u0002\u0005%\u0014!\u00014\u0011\u000b9\tY'!\u0019\n\u0007\u00055tB\u0001\u0005=Eft\u0017-\\3?\u0011!\t\t(a\u0016A\u0002\u0005M\u0014!\u0001;\u0011\t\u0005U\u0014QP\u0007\u0003\u0003oR1\u0001]A=\u0015\r\tY\bS\u0001\tG>$\u0017\r[1mK&!\u0011qPA<\u0005\u0015!\u0016.\\3s\u0011\u001d\t\u0019\t\u0001C\u0005\u0003\u000b\u000bA\u0003\\8bI\u0006\u0003\b\u000f\\5dCRLwN\\#oiJLH#B(\u0002\b\u0006%\u0005BB>\u0002\u0002\u0002\u0007A\u0010\u0003\u0005\u0002\n\u0005\u0005\u0005\u0019AA\u0006Q\u0019\t\t)!$\u0002\u001eB)a\"a$\u0002\u0014&\u0019\u0011\u0011S\b\u0003\rQD'o\\<t!\u0011\t)*!'\u000e\u0005\u0005]%BA\u0019a\u0013\u0011\tY*a&\u0003-9{7+^2i\u000b2,W.\u001a8u\u000bb\u001cW\r\u001d;j_:\fdA\b?\u0002 \u0006%\u0017'C\u0012\u0002\"\u0006\u001d\u0016qXAU+\u0011\t\u0019+!*\u0016\u0003q$q!!\t\u001c\u0005\u0004\ty+\u0003\u0003\u0002*\u0006-\u0016a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$\u0013GC\u0002\u0002.>\ta\u0001\u001e5s_^\u001c\u0018\u0003BA\u0013\u0003c\u0003B!a-\u0002::\u0019a\"!.\n\u0007\u0005]v\"A\u0004qC\u000e\\\u0017mZ3\n\t\u0005m\u0016Q\u0018\u0002\n)\"\u0014xn^1cY\u0016T1!a.\u0010c%\u0019\u0013\u0011YAb\u0003\u000b\fiKD\u0002\u000f\u0003\u0007L1!!,\u0010c\u0015\u0011cbDAd\u0005\u0015\u00198-\u00197bc\r1\u00131\u0013\u0005\b\u0003\u001b\u0004A\u0011IAh\u0003!!xn\u0015;sS:<G#\u0001?\t\u000f\u0005M\u0007\u0001\"\u0003\u0002V\u0006q!/Z4jgR,'OR5mi\u0016\u0014HCBAl\u0003;\f\t\u000fE\u0002\u000f\u00033L1!a7\u0010\u0005\u0011)f.\u001b;\t\u000f\u0005}\u0017\u0011\u001ba\u0001\u0019\u0006\u00191.Z=\t\u0011\u0005\r\u0018\u0011\u001ba\u0001\u0003K\f\u0001\u0002\\8bI\u0016$W+\u0013\t\u0004=\u0005\u001d\u0018bAAu\u0005\tYAj\\1eK\u0012\f\u0005\u000f]+J\u0011\u001d\ti\u000f\u0001C\u0001\u0003_\f!\"\u001b8wC2LG-\u0019;f)\u0011\t9.!=\t\u000f\u0005}\u00171\u001ea\u0001\u0019\"I\u0011Q\u001f\u0001\u0012\u0002\u0013\u0005\u0011q_\u0001\u000eO\u0016$H\u0005Z3gCVdG\u000f\n\u001a\u0016\u0005\u0005e(\u0006BA\u0006\u0003w\\#!!@\u0011\t\u0005}(\u0011B\u0007\u0003\u0005\u0003QAAa\u0001\u0003\u0006\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0005\u000fy\u0011AC1o]>$\u0018\r^5p]&!!1\u0002B\u0001\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a"
)
public class ApplicationCache implements Logging {
   private final ApplicationCacheOperations operations;
   private final int retainedApplications;
   private final Clock clock;
   private final CacheLoader<CacheKey, CacheEntry> appLoader;
   private final Object removalListener;
   private final LoadingCache<CacheKey, CacheEntry> appCache;
   private final CacheMetrics metrics;
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

   public ApplicationCacheOperations operations() {
      return this.operations;
   }

   public int retainedApplications() {
      return this.retainedApplications;
   }

   public Clock clock() {
      return this.clock;
   }

   private CacheLoader<CacheKey, CacheEntry> appLoader() {
      return this.appLoader;
   }

   private Object removalListener() {
      return this.removalListener;
   }

   private LoadingCache<CacheKey, CacheEntry> appCache() {
      return this.appCache;
   }

   public CacheMetrics metrics() {
      return this.metrics;
   }

   public CacheEntry get(String appId, Option<String> attemptId) {
      try {
         return (CacheEntry)this.appCache().get(new CacheKey(appId, attemptId));
      } catch (Throwable var7) {
         boolean var5;
         if (var7 instanceof ExecutionException) {
            var5 = true;
         } else if (var7 instanceof UncheckedExecutionException) {
            var5 = true;
         } else {
            var5 = false;
         }

         if (var5) {
            throw (Throwable)Option$.MODULE$.apply(var7.getCause()).getOrElse(new Serializable(this, var7) {
               public static final long serialVersionUID = 0L;
               private final Throwable x5$1;

               public final Throwable apply() {
                  return this.x5$1;
               }

               public {
                  this.x5$1 = x5$1;
               }
            });
         } else {
            throw var7;
         }
      }
   }

   public Option<String> get$default$2() {
      return None$.MODULE$;
   }

   public <T> T withSparkUI(String appId, Option<String> attemptId, Function1<SparkUI, T> fn) {
      CacheEntry entry = this.get(appId, attemptId);
      entry.loadedUI().lock().readLock().lock();

      Object var10000;
      try {
         while(!entry.loadedUI().valid()) {
            entry.loadedUI().lock().readLock().unlock();
            entry = null;

            try {
               this.invalidate(new CacheKey(appId, attemptId));
               entry = this.get(appId, attemptId);
               this.metrics().loadCount().inc();
            } finally {
               if (entry != null) {
                  entry.loadedUI().lock().readLock().lock();
               }

            }
         }

         fn.apply(entry.loadedUI().ui());
      } finally {
         var10000 = entry;
         if (entry != null) {
            var10000 = entry.loadedUI().lock().readLock();
            ((ReentrantReadWriteLock.ReadLock)var10000).unlock();
         }

      }

      return (T)var10000;
   }

   public long size() {
      return this.appCache().size();
   }

   private <T> T time(Timer t, Function0<T> f) {
      Timer.Context timeCtx = t.time();

      Object var10000;
      try {
         var10000 = f.apply();
      } finally {
         timeCtx.close();
      }

      return (T)var10000;
   }

   public CacheEntry org$apache$spark$deploy$history$ApplicationCache$$loadApplicationEntry(String appId, Option<String> attemptId) throws NoSuchElementException {
      this.logDebug(new Serializable(this, appId, attemptId) {
         public static final long serialVersionUID = 0L;
         private final String appId$1;
         private final Option attemptId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Loading application Entry ", "/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$1, this.attemptId$1}));
         }

         public {
            this.appId$1 = appId$1;
            this.attemptId$1 = attemptId$1;
         }
      });
      this.metrics().loadCount().inc();
      LoadedAppUI loadedUI = (LoadedAppUI)this.time(this.metrics().loadTimer(), new Serializable(this, appId, attemptId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ApplicationCache $outer;
         public final String appId$1;
         public final Option attemptId$1;

         public final LoadedAppUI apply() {
            this.$outer.metrics().lookupCount().inc();
            Option var1 = this.$outer.operations().getAppUI(this.appId$1, this.attemptId$1);
            if (var1 instanceof Some) {
               Some var2 = (Some)var1;
               LoadedAppUI loadedUI = (LoadedAppUI)var2.x();
               this.$outer.logDebug(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Loaded application ", "/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.appId$1, this.$outer.attemptId$1}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               return loadedUI;
            } else if (None$.MODULE$.equals(var1)) {
               this.$outer.metrics().lookupFailureCount().inc();
               this.$outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to load application attempt ", "/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.appId$1, this.$outer.attemptId$1}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               throw new NoSuchElementException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"no application with application Id '", "'"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$1}))).append(this.attemptId$1.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply(String id) {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" attemptId '", "'"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{id}));
                  }
               }).getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return " and no attempt Id";
                  }
               })).toString());
            } else {
               throw new MatchError(var1);
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.appId$1 = appId$1;
               this.attemptId$1 = attemptId$1;
            }
         }
      });

      try {
         boolean completed = loadedUI.ui().getApplicationInfoList().exists(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(ApplicationInfo x$1) {
               return ((ApplicationAttemptInfo)x$1.attempts().last()).completed();
            }
         });
         if (!completed) {
            this.registerFilter(new CacheKey(appId, attemptId), loadedUI);
         }

         this.operations().attachSparkUI(appId, attemptId, loadedUI.ui(), completed);
         return new CacheEntry(loadedUI, completed);
      } catch (Exception var6) {
         this.logWarning(new Serializable(this, appId, attemptId) {
            public static final long serialVersionUID = 0L;
            private final String appId$1;
            private final Option attemptId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to initialize application UI for ", "/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$1, this.attemptId$1}));
            }

            public {
               this.appId$1 = appId$1;
               this.attemptId$1 = attemptId$1;
            }
         }, var6);
         this.operations().detachSparkUI(appId, attemptId, loadedUI.ui());
         throw var6;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ApplicationCache("})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" retainedApplications= ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.retainedApplications())}))).toString());
      sb.append((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"; time= ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.clock().getTimeMillis())})));
      sb.append((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"; entry count= ", "\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.appCache().size())})));
      sb.append("----\n");
      ((IterableLike)JavaConverters$.MODULE$.mapAsScalaConcurrentMapConverter(this.appCache().asMap()).asScala()).foreach(new Serializable(this, sb) {
         public static final long serialVersionUID = 0L;
         private final StringBuilder sb$1;

         public final StringBuilder apply(Tuple2<CacheKey, CacheEntry> x0$1) {
            if (x0$1 != null) {
               CacheKey key = (CacheKey)x0$1._1();
               CacheEntry entry = (CacheEntry)x0$1._2();
               StringBuilder var5 = this.sb$1.append((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"  ", " -> ", "\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{key, entry})));
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.sb$1 = sb$1;
         }
      });
      sb.append("----\n");
      sb.append((Object)this.metrics());
      sb.append("----\n");
      return sb.toString();
   }

   private void registerFilter(CacheKey key, LoadedAppUI loadedUI) {
      Predef$.MODULE$.require(loadedUI != null);
      EnumSet enumDispatcher = EnumSet.of(DispatcherType.ASYNC, DispatcherType.REQUEST);
      ApplicationCacheCheckFilter filter = new ApplicationCacheCheckFilter(key, loadedUI, this);
      FilterHolder holder = new FilterHolder(filter);
      Predef$.MODULE$.require(loadedUI.ui().getHandlers() != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "null handlers";
         }
      });
      loadedUI.ui().getHandlers().foreach(new Serializable(this, enumDispatcher, holder) {
         public static final long serialVersionUID = 0L;
         private final EnumSet enumDispatcher$1;
         private final FilterHolder holder$1;

         public final void apply(ServletContextHandler handler) {
            handler.addFilter(this.holder$1, "/*", this.enumDispatcher$1);
         }

         public {
            this.enumDispatcher$1 = enumDispatcher$1;
            this.holder$1 = holder$1;
         }
      });
   }

   public void invalidate(CacheKey key) {
      this.appCache().invalidate(key);
   }

   public ApplicationCache(ApplicationCacheOperations operations, int retainedApplications, Clock clock) {
      this.operations = operations;
      this.retainedApplications = retainedApplications;
      this.clock = clock;
      Logging$class.$init$(this);
      this.appLoader = new CacheLoader<CacheKey, CacheEntry>(this) {
         // $FF: synthetic field
         private final ApplicationCache $outer;

         public CacheEntry load(CacheKey key) {
            return this.$outer.org$apache$spark$deploy$history$ApplicationCache$$loadApplicationEntry(key.appId(), key.attemptId());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      this.removalListener = new RemovalListener<CacheKey, CacheEntry>(this) {
         // $FF: synthetic field
         private final ApplicationCache $outer;

         public void onRemoval(RemovalNotification<CacheKey, CacheEntry> rm) {
            this.$outer.metrics().evictionCount().inc();
            CacheKey key = (CacheKey)rm.getKey();
            this.$outer.logDebug(new Serializable(this, key) {
               public static final long serialVersionUID = 0L;
               private final CacheKey key$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Evicting entry ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.key$1}));
               }

               public {
                  this.key$1 = key$1;
               }
            });
            this.$outer.operations().detachSparkUI(key.appId(), key.attemptId(), ((CacheEntry)rm.getValue()).loadedUI().ui());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      this.appCache = CacheBuilder.newBuilder().maximumSize((long)retainedApplications).removalListener(this.removalListener()).build(this.appLoader());
      this.metrics = new CacheMetrics("history.cache");
   }
}
