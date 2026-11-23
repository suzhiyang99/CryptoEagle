package org.apache.spark.scheduler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Enumeration;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;
import scala.xml.Elem;
import scala.xml.Node;
import scala.xml.XML.;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015f!B\u0001\u0003\u0001\u0011Q!A\u0006$bSJ\u001c6\r[3ek2\f'\r\\3Ck&dG-\u001a:\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0003\u0002\u0001\f#U\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0014\u001b\u0005\u0011\u0011B\u0001\u000b\u0003\u0005I\u00196\r[3ek2\f'\r\\3Ck&dG-\u001a:\u0011\u0005YIR\"A\f\u000b\u0005a!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005i9\"a\u0002'pO\u001eLgn\u001a\u0005\t9\u0001\u0011)\u0019!C\u0001=\u0005A!o\\8u!>|Gn\u0001\u0001\u0016\u0003}\u0001\"A\u0005\u0011\n\u0005\u0005\u0012!\u0001\u0002)p_2D\u0001b\t\u0001\u0003\u0002\u0003\u0006IaH\u0001\ne>|G\u000fU8pY\u0002B\u0001\"\n\u0001\u0003\u0002\u0003\u0006IAJ\u0001\u0005G>tg\r\u0005\u0002(Q5\tA!\u0003\u0002*\t\tI1\u000b]1sW\u000e{gN\u001a\u0005\u0006W\u0001!\t\u0001L\u0001\u0007y%t\u0017\u000e\u001e \u0015\u00075rs\u0006\u0005\u0002\u0013\u0001!)AD\u000ba\u0001?!)QE\u000ba\u0001M!9\u0011\u0007\u0001b\u0001\n\u0003\u0011\u0014AI*D\u0011\u0016#U\u000bT#S?\u0006cEjT\"B)&{ej\u0018$J\u0019\u0016{\u0006KU(Q\u000bJ#\u0016,F\u00014!\t!\u0014(D\u00016\u0015\t1t'\u0001\u0003mC:<'\"\u0001\u001d\u0002\t)\fg/Y\u0005\u0003uU\u0012aa\u0015;sS:<\u0007B\u0002\u001f\u0001A\u0003%1'A\u0012T\u0007\"+E)\u0016'F%~\u000bE\nT(D\u0003RKuJT0G\u00132+u\f\u0015*P!\u0016\u0013F+\u0017\u0011\t\u000fy\u0002!\u0019!C\u0001\u007f\u0005\u00112o\u00195fIVdWM]!mY>\u001cg)\u001b7f+\u0005\u0001\u0005c\u0001\u0007B\u0007&\u0011!)\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005\u0011;eB\u0001\u0007F\u0013\t1U\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003u!S!AR\u0007\t\r)\u0003\u0001\u0015!\u0003A\u0003M\u00198\r[3ek2,'/\u00117m_\u000e4\u0015\u000e\\3!\u0011\u001da\u0005A1A\u0005\u0002I\na\u0003R#G\u0003VcEkX*D\u0011\u0016#U\u000bT#S?\u001aKE*\u0012\u0005\u0007\u001d\u0002\u0001\u000b\u0011B\u001a\u0002/\u0011+e)Q+M)~\u001b6\tS#E+2+%k\u0018$J\u0019\u0016\u0003\u0003b\u0002)\u0001\u0005\u0004%\tAM\u0001\u001a\r\u0006K%kX*D\u0011\u0016#U\u000bT#S?B\u0013v\nU#S)&+5\u000b\u0003\u0004S\u0001\u0001\u0006IaM\u0001\u001b\r\u0006K%kX*D\u0011\u0016#U\u000bT#S?B\u0013v\nU#S)&+5\u000b\t\u0005\b)\u0002\u0011\r\u0011\"\u00013\u0003E!UIR!V\u0019R{\u0006kT(M?:\u000bU*\u0012\u0005\u0007-\u0002\u0001\u000b\u0011B\u001a\u0002%\u0011+e)Q+M)~\u0003vj\u0014'`\u001d\u0006kU\t\t\u0005\b1\u0002\u0011\r\u0011\"\u00013\u0003]i\u0015JT%N+6{6\u000bS!S\u000bN{\u0006KU(Q\u000bJ#\u0016\f\u0003\u0004[\u0001\u0001\u0006IaM\u0001\u0019\u001b&s\u0015*T+N?NC\u0015IU#T?B\u0013v\nU#S)f\u0003\u0003b\u0002/\u0001\u0005\u0004%\tAM\u0001\u0019'\u000eCU\tR+M\u0013:;u,T(E\u000b~\u0003&k\u0014)F%RK\u0006B\u00020\u0001A\u0003%1'A\rT\u0007\"+E)\u0016'J\u001d\u001e{Vj\u0014#F?B\u0013v\nU#S)f\u0003\u0003b\u00021\u0001\u0005\u0004%\tAM\u0001\u0010/\u0016Ku\t\u0013+`!J{\u0005+\u0012*U3\"1!\r\u0001Q\u0001\nM\n\u0001cV#J\u000f\"#v\f\u0015*P!\u0016\u0013F+\u0017\u0011\t\u000f\u0011\u0004!\u0019!C\u0001e\u0005\u0011\u0002kT(M?:\u000bU*R0Q%>\u0003VI\u0015+Z\u0011\u00191\u0007\u0001)A\u0005g\u0005\u0019\u0002kT(M?:\u000bU*R0Q%>\u0003VI\u0015+ZA!9\u0001\u000e\u0001b\u0001\n\u0003\u0011\u0014A\u0004)P\u001f2\u001bv\f\u0015*P!\u0016\u0013F+\u0017\u0005\u0007U\u0002\u0001\u000b\u0011B\u001a\u0002\u001fA{u\nT*`!J{\u0005+\u0012*U3\u0002Bq\u0001\u001c\u0001C\u0002\u0013\u0005Q.A\fE\u000b\u001a\u000bU\u000b\u0014+`'\u000eCU\tR+M\u0013:;u,T(E\u000bV\ta\u000e\u0005\u0002pe:\u0011!\u0003]\u0005\u0003c\n\tabU2iK\u0012,H.\u001b8h\u001b>$W-\u0003\u0002ti\n)a+\u00197vK&\u0011Q/\u0004\u0002\f\u000b:,X.\u001a:bi&|g\u000e\u0003\u0004x\u0001\u0001\u0006IA\\\u0001\u0019\t\u00163\u0015)\u0016'U?N\u001b\u0005*\u0012#V\u0019&suiX'P\t\u0016\u0003\u0003bB=\u0001\u0005\u0004%\tA_\u0001\u0016\t\u00163\u0015)\u0016'U?6Ke*S'V\u001b~\u001b\u0006*\u0011*F+\u0005Y\bC\u0001\u0007}\u0013\tiXBA\u0002J]RDaa \u0001!\u0002\u0013Y\u0018A\u0006#F\r\u0006+F\nV0N\u0013:KU*V'`'\"\u000b%+\u0012\u0011\t\u0011\u0005\r\u0001A1A\u0005\u0002i\fa\u0002R#G\u0003VcEkX,F\u0013\u001eCE\u000bC\u0004\u0002\b\u0001\u0001\u000b\u0011B>\u0002\u001f\u0011+e)Q+M)~;V)S$I)\u0002Bq!a\u0003\u0001\t\u0003\ni!\u0001\u0006ck&dG\rU8pYN$\"!a\u0004\u0011\u00071\t\t\"C\u0002\u0002\u00145\u0011A!\u00168ji\"9\u0011q\u0003\u0001\u0005\n\u00055\u0011\u0001\u00052vS2$G)\u001a4bk2$\bk\\8m\u0011\u001d\tY\u0002\u0001C\u0005\u0003;\taCY;jY\u00124\u0015-\u001b:TG\",G-\u001e7feB{w\u000e\u001c\u000b\u0007\u0003\u001f\ty\"a\f\t\u0011\u0005\u0005\u0012\u0011\u0004a\u0001\u0003G\t!![:\u0011\t\u0005\u0015\u00121F\u0007\u0003\u0003OQ1!!\u000b8\u0003\tIw.\u0003\u0003\u0002.\u0005\u001d\"aC%oaV$8\u000b\u001e:fC6Dq!!\r\u0002\u001a\u0001\u00071)\u0001\u0005gS2,g*Y7f\u0011\u001d\t)\u0004\u0001C\u0005\u0003o\tacZ3u'\u000eDW\rZ;mS:<Wj\u001c3f-\u0006dW/\u001a\u000b\u000b\u0003s\tI&!\u001b\u0002n\u0005E\u0004\u0003BA\u001e\u0003'r1!!\u0010q\u001d\u0011\ty$!\u0015\u000f\t\u0005\u0005\u0013q\n\b\u0005\u0003\u0007\niE\u0004\u0003\u0002F\u0005-SBAA$\u0015\r\tI%H\u0001\u0007yI|w\u000e\u001e \n\u0003%I!a\u0002\u0005\n\u0005\u00151\u0011BA\u0002\u0005\u0013\u0011\t)&a\u0016\u0003\u001dM\u001b\u0007.\u001a3vY&tw-T8eK*\u0011\u0011O\u0001\u0005\t\u00037\n\u0019\u00041\u0001\u0002^\u0005A\u0001o\\8m\u001d>$W\r\u0005\u0003\u0002`\u0005\u0015TBAA1\u0015\r\t\u0019'D\u0001\u0004q6d\u0017\u0002BA4\u0003C\u0012AAT8eK\"9\u00111NA\u001a\u0001\u0004\u0019\u0015\u0001\u00039p_2t\u0015-\\3\t\u0011\u0005=\u00141\u0007a\u0001\u0003s\tA\u0002Z3gCVdGOV1mk\u0016Dq!!\r\u00024\u0001\u00071\tC\u0004\u0002v\u0001!I!a\u001e\u0002\u0017\u001d,G/\u00138u-\u0006dW/\u001a\u000b\fw\u0006e\u00141PA?\u0003\u0003\u000b\u0019\t\u0003\u0005\u0002\\\u0005M\u0004\u0019AA/\u0011\u001d\tY'a\u001dA\u0002\rCq!a \u0002t\u0001\u00071)\u0001\u0007qe>\u0004XM\u001d;z\u001d\u0006lW\rC\u0004\u0002p\u0005M\u0004\u0019A>\t\u000f\u0005E\u00121\u000fa\u0001\u0007\"9\u0011q\u0011\u0001\u0005B\u0005%\u0015!E1eIR\u000b7o[*fi6\u000bg.Y4feR1\u0011qBAF\u0003+C\u0001\"!$\u0002\u0006\u0002\u0007\u0011qR\u0001\b[\u0006t\u0017mZ3s!\r\u0011\u0012\u0011S\u0005\u0004\u0003'\u0013!aC*dQ\u0016$W\u000f\\1cY\u0016D\u0001\"a&\u0002\u0006\u0002\u0007\u0011\u0011T\u0001\u000baJ|\u0007/\u001a:uS\u0016\u001c\b\u0003BAN\u0003Ck!!!(\u000b\u0007\u0005}u'\u0001\u0003vi&d\u0017\u0002BAR\u0003;\u0013!\u0002\u0015:pa\u0016\u0014H/[3t\u0001"
)
public class FairSchedulableBuilder implements SchedulableBuilder, Logging {
   private final Pool rootPool;
   private final String SCHEDULER_ALLOCATION_FILE_PROPERTY;
   private final Option<String> schedulerAllocFile;
   private final String DEFAULT_SCHEDULER_FILE;
   private final String FAIR_SCHEDULER_PROPERTIES;
   private final String DEFAULT_POOL_NAME;
   private final String MINIMUM_SHARES_PROPERTY;
   private final String SCHEDULING_MODE_PROPERTY;
   private final String WEIGHT_PROPERTY;
   private final String POOL_NAME_PROPERTY;
   private final String POOLS_PROPERTY;
   private final Enumeration.Value DEFAULT_SCHEDULING_MODE;
   private final int DEFAULT_MINIMUM_SHARE;
   private final int DEFAULT_WEIGHT;
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

   public Pool rootPool() {
      return this.rootPool;
   }

   public String SCHEDULER_ALLOCATION_FILE_PROPERTY() {
      return this.SCHEDULER_ALLOCATION_FILE_PROPERTY;
   }

   public Option<String> schedulerAllocFile() {
      return this.schedulerAllocFile;
   }

   public String DEFAULT_SCHEDULER_FILE() {
      return this.DEFAULT_SCHEDULER_FILE;
   }

   public String FAIR_SCHEDULER_PROPERTIES() {
      return this.FAIR_SCHEDULER_PROPERTIES;
   }

   public String DEFAULT_POOL_NAME() {
      return this.DEFAULT_POOL_NAME;
   }

   public String MINIMUM_SHARES_PROPERTY() {
      return this.MINIMUM_SHARES_PROPERTY;
   }

   public String SCHEDULING_MODE_PROPERTY() {
      return this.SCHEDULING_MODE_PROPERTY;
   }

   public String WEIGHT_PROPERTY() {
      return this.WEIGHT_PROPERTY;
   }

   public String POOL_NAME_PROPERTY() {
      return this.POOL_NAME_PROPERTY;
   }

   public String POOLS_PROPERTY() {
      return this.POOLS_PROPERTY;
   }

   public Enumeration.Value DEFAULT_SCHEDULING_MODE() {
      return this.DEFAULT_SCHEDULING_MODE;
   }

   public int DEFAULT_MINIMUM_SHARE() {
      return this.DEFAULT_MINIMUM_SHARE;
   }

   public int DEFAULT_WEIGHT() {
      return this.DEFAULT_WEIGHT;
   }

   public void buildPools() {
      Option fileData = None$.MODULE$;

      try {
         try {
            fileData = (Option)this.schedulerAllocFile().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final FairSchedulableBuilder $outer;

               public final Some<Tuple2<FileInputStream, String>> apply(String f) {
                  FileInputStream fis = new FileInputStream(f);
                  this.$outer.logInfo(new Serializable(this, f) {
                     public static final long serialVersionUID = 0L;
                     private final String f$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Creating Fair Scheduler pools from ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.f$1}));
                     }

                     public {
                        this.f$1 = f$1;
                     }
                  });
                  return new Some<Tuple2<FileInputStream, String>>(new Tuple2(fis, f));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final FairSchedulableBuilder $outer;

               public final Option<Tuple2<InputStream, String>> apply() {
                  InputStream is = Utils$.MODULE$.getSparkClassLoader().getResourceAsStream(this.$outer.DEFAULT_SCHEDULER_FILE());
                  Object var10000;
                  if (is == null) {
                     this.$outer.logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Fair Scheduler configuration file not found so jobs will be scheduled in ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"FIFO order. To use fair scheduling, configure pools in ", " or "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$scheduler$FairSchedulableBuilder$$anonfun$$$outer().DEFAULT_SCHEDULER_FILE()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"set ", " to a file that contains the configuration."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$scheduler$FairSchedulableBuilder$$anonfun$$$outer().SCHEDULER_ALLOCATION_FILE_PROPERTY()}))).toString();
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     var10000 = None$.MODULE$;
                  } else {
                     this.$outer.logInfo(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Creating Fair Scheduler pools from default file: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$scheduler$FairSchedulableBuilder$$anonfun$$$outer().DEFAULT_SCHEDULER_FILE()}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     var10000 = new Some(new Tuple2(is, this.$outer.DEFAULT_SCHEDULER_FILE()));
                  }

                  return (Option<Tuple2<InputStream, String>>)var10000;
               }

               // $FF: synthetic method
               public FairSchedulableBuilder org$apache$spark$scheduler$FairSchedulableBuilder$$anonfun$$$outer() {
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
            fileData.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final FairSchedulableBuilder $outer;

               public final void apply(Tuple2<InputStream, String> x0$1) {
                  if (x0$1 != null) {
                     InputStream is = (InputStream)x0$1._1();
                     String fileName = (String)x0$1._2();
                     this.$outer.org$apache$spark$scheduler$FairSchedulableBuilder$$buildFairSchedulerPool(is, fileName);
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  } else {
                     throw new MatchError(x0$1);
                  }
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         } catch (Throwable var14) {
            Option var5 = NonFatal$.MODULE$.unapply(var14);
            if (var5.isEmpty()) {
               throw var14;
            }

            Throwable t = (Throwable)var5.get();
            String defaultMessage = "Error while building the fair scheduler pools";
            String message = (String)fileData.map(new Serializable(this, defaultMessage) {
               public static final long serialVersionUID = 0L;
               private final String defaultMessage$1;

               public final String apply(Tuple2<InputStream, String> x0$2) {
                  if (x0$2 != null) {
                     String fileName = (String)x0$2._2();
                     String var4 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " from ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.defaultMessage$1, fileName}));
                     return var4;
                  } else {
                     throw new MatchError(x0$2);
                  }
               }

               public {
                  this.defaultMessage$1 = defaultMessage$1;
               }
            }).getOrElse(new Serializable(this, defaultMessage) {
               public static final long serialVersionUID = 0L;
               private final String defaultMessage$1;

               public final String apply() {
                  return this.defaultMessage$1;
               }

               public {
                  this.defaultMessage$1 = defaultMessage$1;
               }
            });
            this.logError(new Serializable(this, message) {
               public static final long serialVersionUID = 0L;
               private final String message$1;

               public final String apply() {
                  return this.message$1;
               }

               public {
                  this.message$1 = message$1;
               }
            }, t);
            throw t;
         }
      } finally {
         fileData.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(Tuple2<InputStream, String> x0$3) {
               if (x0$3 != null) {
                  InputStream is = (InputStream)x0$3._1();
                  is.close();
                  BoxedUnit var4 = BoxedUnit.UNIT;
               } else {
                  throw new MatchError(x0$3);
               }
            }
         });
      }

      this.buildDefaultPool();
   }

   private void buildDefaultPool() {
      if (this.rootPool().getSchedulableByName(this.DEFAULT_POOL_NAME()) == null) {
         Pool pool = new Pool(this.DEFAULT_POOL_NAME(), this.DEFAULT_SCHEDULING_MODE(), this.DEFAULT_MINIMUM_SHARE(), this.DEFAULT_WEIGHT());
         this.rootPool().addSchedulable(pool);
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FairSchedulableBuilder $outer;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("Created default pool: %s, schedulingMode: %s, minShare: %d, weight: %d"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.DEFAULT_POOL_NAME(), this.$outer.DEFAULT_SCHEDULING_MODE(), BoxesRunTime.boxToInteger(this.$outer.DEFAULT_MINIMUM_SHARE()), BoxesRunTime.boxToInteger(this.$outer.DEFAULT_WEIGHT())}));
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

   public void org$apache$spark$scheduler$FairSchedulableBuilder$$buildFairSchedulerPool(InputStream is, String fileName) {
      Elem xml = (Elem).MODULE$.load(is);
      xml.$bslash$bslash(this.POOLS_PROPERTY()).foreach(new Serializable(this, fileName) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FairSchedulableBuilder $outer;
         private final String fileName$1;

         public final void apply(Node poolNode) {
            String poolName = poolNode.$bslash(this.$outer.POOL_NAME_PROPERTY()).text();
            Enumeration.Value schedulingMode = this.$outer.org$apache$spark$scheduler$FairSchedulableBuilder$$getSchedulingModeValue(poolNode, poolName, this.$outer.DEFAULT_SCHEDULING_MODE(), this.fileName$1);
            int minShare = this.$outer.org$apache$spark$scheduler$FairSchedulableBuilder$$getIntValue(poolNode, poolName, this.$outer.MINIMUM_SHARES_PROPERTY(), this.$outer.DEFAULT_MINIMUM_SHARE(), this.fileName$1);
            int weight = this.$outer.org$apache$spark$scheduler$FairSchedulableBuilder$$getIntValue(poolNode, poolName, this.$outer.WEIGHT_PROPERTY(), this.$outer.DEFAULT_WEIGHT(), this.fileName$1);
            this.$outer.rootPool().addSchedulable(new Pool(poolName, schedulingMode, minShare, weight));
            this.$outer.logInfo(new Serializable(this, poolName, schedulingMode, minShare, weight) {
               public static final long serialVersionUID = 0L;
               private final String poolName$1;
               private final Enumeration.Value schedulingMode$1;
               private final int minShare$1;
               private final int weight$1;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Created pool: %s, schedulingMode: %s, minShare: %d, weight: %d"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.poolName$1, this.schedulingMode$1, BoxesRunTime.boxToInteger(this.minShare$1), BoxesRunTime.boxToInteger(this.weight$1)}));
               }

               public {
                  this.poolName$1 = poolName$1;
                  this.schedulingMode$1 = schedulingMode$1;
                  this.minShare$1 = minShare$1;
                  this.weight$1 = weight$1;
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.fileName$1 = fileName$1;
            }
         }
      });
   }

   public Enumeration.Value org$apache$spark$scheduler$FairSchedulableBuilder$$getSchedulingModeValue(Node poolNode, String poolName, Enumeration.Value defaultValue, String fileName) {
      String xmlSchedulingMode = poolNode.$bslash(this.SCHEDULING_MODE_PROPERTY()).text().trim().toUpperCase(Locale.ROOT);
      String warningMessage = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unsupported schedulingMode: ", " found in "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{xmlSchedulingMode}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Fair Scheduler configuration file: ", ", using "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{fileName}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"the default schedulingMode: ", " for pool: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{defaultValue, poolName}))).toString();

      Enumeration.Value var10000;
      try {
         label29: {
            var10000 = SchedulingMode$.MODULE$.withName(xmlSchedulingMode);
            Enumeration.Value var8 = SchedulingMode$.MODULE$.NONE();
            if (var10000 == null) {
               if (var8 == null) {
                  break label29;
               }
            } else if (var10000.equals(var8)) {
               break label29;
            }

            var10000 = SchedulingMode$.MODULE$.withName(xmlSchedulingMode);
            return var10000;
         }

         this.logWarning(new Serializable(this, warningMessage) {
            public static final long serialVersionUID = 0L;
            private final String warningMessage$1;

            public final String apply() {
               return this.warningMessage$1;
            }

            public {
               this.warningMessage$1 = warningMessage$1;
            }
         });
         var10000 = defaultValue;
      } catch (NoSuchElementException var9) {
         this.logWarning(new Serializable(this, warningMessage) {
            public static final long serialVersionUID = 0L;
            private final String warningMessage$1;

            public final String apply() {
               return this.warningMessage$1;
            }

            public {
               this.warningMessage$1 = warningMessage$1;
            }
         });
         var10000 = defaultValue;
      }

      return var10000;
   }

   public int org$apache$spark$scheduler$FairSchedulableBuilder$$getIntValue(Node poolNode, String poolName, String propertyName, int defaultValue, String fileName) {
      String data = poolNode.$bslash(propertyName).text().trim();

      int var10000;
      try {
         var10000 = (new StringOps(Predef$.MODULE$.augmentString(data))).toInt();
      } catch (NumberFormatException var8) {
         this.logWarning(new Serializable(this, poolName, propertyName, defaultValue, fileName, data) {
            public static final long serialVersionUID = 0L;
            private final String poolName$2;
            private final String propertyName$1;
            private final int defaultValue$1;
            private final String fileName$2;
            private final String data$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error while loading fair scheduler configuration from ", ": "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.fileName$2}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " is blank or invalid: ", ", using the default ", ": "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.propertyName$1, this.data$1, this.propertyName$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " for pool: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.defaultValue$1), this.poolName$2}))).toString();
            }

            public {
               this.poolName$2 = poolName$2;
               this.propertyName$1 = propertyName$1;
               this.defaultValue$1 = defaultValue$1;
               this.fileName$2 = fileName$2;
               this.data$1 = data$1;
            }
         });
         var10000 = defaultValue;
      }

      return var10000;
   }

   public void addTaskSetManager(Schedulable manager, Properties properties) {
      String poolName = properties == null ? this.DEFAULT_POOL_NAME() : properties.getProperty(this.FAIR_SCHEDULER_PROPERTIES(), this.DEFAULT_POOL_NAME());
      Schedulable parentPool = this.rootPool().getSchedulableByName(poolName);
      if (parentPool == null) {
         parentPool = new Pool(poolName, this.DEFAULT_SCHEDULING_MODE(), this.DEFAULT_MINIMUM_SHARE(), this.DEFAULT_WEIGHT());
         this.rootPool().addSchedulable(parentPool);
         this.logWarning(new Serializable(this, poolName) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FairSchedulableBuilder $outer;
            private final String poolName$3;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"A job was submitted with scheduler pool ", ", which has not been "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.poolName$3}))).append((Object)"configured. This can happen when the file that pools are read from isn't set, or ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"when that file doesn't contain ", ". Created ", " with default "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.poolName$3, this.poolName$3}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"configuration (schedulingMode: ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.DEFAULT_SCHEDULING_MODE()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"minShare: ", ", weight: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.DEFAULT_MINIMUM_SHARE()), BoxesRunTime.boxToInteger(this.$outer.DEFAULT_WEIGHT())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.poolName$3 = poolName$3;
               }
            }
         });
      }

      parentPool.addSchedulable(manager);
      this.logInfo(new Serializable(this, manager, poolName) {
         public static final long serialVersionUID = 0L;
         private final Schedulable manager$1;
         private final String poolName$3;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Added task set ").append((Object)this.manager$1.name()).append((Object)" tasks to pool ").append((Object)this.poolName$3).toString();
         }

         public {
            this.manager$1 = manager$1;
            this.poolName$3 = poolName$3;
         }
      });
   }

   public FairSchedulableBuilder(Pool rootPool, SparkConf conf) {
      this.rootPool = rootPool;
      Logging$class.$init$(this);
      this.SCHEDULER_ALLOCATION_FILE_PROPERTY = "spark.scheduler.allocation.file";
      this.schedulerAllocFile = conf.getOption(this.SCHEDULER_ALLOCATION_FILE_PROPERTY());
      this.DEFAULT_SCHEDULER_FILE = "fairscheduler.xml";
      this.FAIR_SCHEDULER_PROPERTIES = "spark.scheduler.pool";
      this.DEFAULT_POOL_NAME = "default";
      this.MINIMUM_SHARES_PROPERTY = "minShare";
      this.SCHEDULING_MODE_PROPERTY = "schedulingMode";
      this.WEIGHT_PROPERTY = "weight";
      this.POOL_NAME_PROPERTY = "@name";
      this.POOLS_PROPERTY = "pool";
      this.DEFAULT_SCHEDULING_MODE = SchedulingMode$.MODULE$.FIFO();
      this.DEFAULT_MINIMUM_SHARE = 0;
      this.DEFAULT_WEIGHT = 1;
   }
}
