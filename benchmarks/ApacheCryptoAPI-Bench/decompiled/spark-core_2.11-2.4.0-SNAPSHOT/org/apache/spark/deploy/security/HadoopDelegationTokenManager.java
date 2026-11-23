package org.apache.spark.deploy.security;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.Credentials;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=b!B\u0001\u0003\u0001\u0019a!\u0001\b%bI>|\u0007\u000fR3mK\u001e\fG/[8o)>\\WM\\'b]\u0006<WM\u001d\u0006\u0003\u0007\u0011\t\u0001b]3dkJLG/\u001f\u0006\u0003\u000b\u0019\ta\u0001Z3qY>L(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0007\u0001i1\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)]i\u0011!\u0006\u0006\u0003-\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00031U\u0011q\u0001T8hO&tw\r\u0003\u0005\u001b\u0001\t\u0005\t\u0015!\u0003\u001d\u0003%\u0019\b/\u0019:l\u0007>tgm\u0001\u0001\u0011\u0005uqR\"\u0001\u0004\n\u0005}1!!C*qCJ\\7i\u001c8g\u0011!\t\u0003A!A!\u0002\u0013\u0011\u0013A\u00035bI>|\u0007oQ8oMB\u00111\u0005K\u0007\u0002I)\u0011QEJ\u0001\u0005G>tgM\u0003\u0002(\u0011\u00051\u0001.\u00193p_BL!!\u000b\u0013\u0003\u001b\r{gNZ5hkJ\fG/[8o\u0011!Y\u0003A!A!\u0002\u0013a\u0013a\u00034jY\u0016\u001c\u0016p\u001d;f[N\u0004BAD\u0017#_%\u0011af\u0004\u0002\n\rVt7\r^5p]F\u00022\u0001M\u001a7\u001d\tq\u0011'\u0003\u00023\u001f\u00051\u0001K]3eK\u001aL!\u0001N\u001b\u0003\u0007M+GO\u0003\u00023\u001fA\u0011qGO\u0007\u0002q)\u0011\u0011HJ\u0001\u0003MNL!a\u000f\u001d\u0003\u0015\u0019KG.Z*zgR,W\u000eC\u0003>\u0001\u0011\u0005a(\u0001\u0004=S:LGO\u0010\u000b\u0005\u007f\u0005\u00135\t\u0005\u0002A\u00015\t!\u0001C\u0003\u001by\u0001\u0007A\u0004C\u0003\"y\u0001\u0007!\u0005C\u0003,y\u0001\u0007A\u0006C\u0004F\u0001\t\u0007I\u0011\u0002$\u0002A\u0011,\u0007O]3dCR,G\r\u0015:pm&$WM]#oC\ndW\rZ\"p]\u001aLwm]\u000b\u0002\u000fB\u0019\u0001*T(\u000e\u0003%S!AS&\u0002\u0013%lW.\u001e;bE2,'B\u0001'\u0010\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003\u001d&\u0013A\u0001T5tiB\u0011\u0001+V\u0007\u0002#*\u0011!kU\u0001\u0005Y\u0006twMC\u0001U\u0003\u0011Q\u0017M^1\n\u0005Y\u000b&AB*ue&tw\r\u0003\u0004Y\u0001\u0001\u0006IaR\u0001\"I\u0016\u0004(/Z2bi\u0016$\u0007K]8wS\u0012,'/\u00128bE2,GmQ8oM&<7\u000f\t\u0005\b5\u0002\u0011\r\u0011\"\u0003\\\u0003U\u0001(o\u001c<jI\u0016\u0014XI\\1cY\u0016$7i\u001c8gS\u001e,\u0012a\u0014\u0005\u0007;\u0002\u0001\u000b\u0011B(\u0002-A\u0014xN^5eKJ,e.\u00192mK\u0012\u001cuN\u001c4jO\u0002Bqa\u0018\u0001C\u0002\u0013%\u0001-\u0001\reK2,w-\u0019;j_:$vn[3o!J|g/\u001b3feN,\u0012!\u0019\t\u0005a\t$g-\u0003\u0002dk\t\u0019Q*\u00199\u0011\u0005A*\u0017B\u0001,6!\t\u0001u-\u0003\u0002i\u0005\ti\u0002*\u00193p_B$U\r\\3hCRLwN\u001c+pW\u0016t\u0007K]8wS\u0012,'\u000f\u0003\u0004k\u0001\u0001\u0006I!Y\u0001\u001aI\u0016dWmZ1uS>tGk\\6f]B\u0013xN^5eKJ\u001c\b\u0005C\u0003>\u0001\u0011\u0005A\u000eF\u0002@[:DQAG6A\u0002qAQ!I6A\u0002\tBQ\u0001\u001d\u0001\u0005\n\u0001\f1dZ3u\t\u0016dWmZ1uS>tGk\\6f]B\u0013xN^5eKJ\u001c\b\"\u0002:\u0001\t\u0013\u0019\u0018AE:bM\u0016\u001c%/Z1uKB\u0013xN^5eKJ$\"\u0001^<\u0011\u00079)h-\u0003\u0002w\u001f\t1q\n\u001d;j_:Da\u0001_9\u0005\u0002\u0004I\u0018\u0001C2sK\u0006$XM\u00128\u0011\u00079Qh-\u0003\u0002|\u001f\tAAHY=oC6,g\bC\u0003~\u0001\u0011\u0005a0\u0001\tjgN+'O^5dK\u0016s\u0017M\u00197fIR\u0019q0!\u0002\u0011\u00079\t\t!C\u0002\u0002\u0004=\u0011qAQ8pY\u0016\fg\u000e\u0003\u0004\u0002\bq\u0004\r\u0001Z\u0001\fg\u0016\u0014h/[2f\u001d\u0006lW\rC\u0004\u0002\f\u0001!\t!!\u0004\u0002C\u001d,GoU3sm&\u001cW\rR3mK\u001e\fG/[8o)>\\WM\u001c)s_ZLG-\u001a:\u0015\u0007Q\fy\u0001C\u0004\u0002\u0012\u0005%\u0001\u0019\u00013\u0002\u000fM,'O^5dK\"9\u0011Q\u0003\u0001\u0005\u0002\u0005]\u0011AF8ci\u0006Lg\u000eR3mK\u001e\fG/[8o)>\\WM\\:\u0015\r\u0005e\u0011qDA\u0011!\rq\u00111D\u0005\u0004\u0003;y!\u0001\u0002'p]\u001eDa!IA\n\u0001\u0004\u0011\u0003\u0002CA\u0012\u0003'\u0001\r!!\n\u0002\u000b\r\u0014X\rZ:\u0011\t\u0005\u001d\u00121F\u0007\u0003\u0003SQ!a\u0001\u0014\n\t\u00055\u0012\u0011\u0006\u0002\f\u0007J,G-\u001a8uS\u0006d7\u000f"
)
public class HadoopDelegationTokenManager implements Logging {
   public final SparkConf org$apache$spark$deploy$security$HadoopDelegationTokenManager$$sparkConf;
   private final Function1<Configuration, Set<FileSystem>> fileSystems;
   private final List<String> deprecatedProviderEnabledConfigs;
   private final String providerEnabledConfig;
   private final Map<String, HadoopDelegationTokenProvider> org$apache$spark$deploy$security$HadoopDelegationTokenManager$$delegationTokenProviders;
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

   private List<String> deprecatedProviderEnabledConfigs() {
      return this.deprecatedProviderEnabledConfigs;
   }

   private String providerEnabledConfig() {
      return this.providerEnabledConfig;
   }

   public Map<String, HadoopDelegationTokenProvider> org$apache$spark$deploy$security$HadoopDelegationTokenManager$$delegationTokenProviders() {
      return this.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$delegationTokenProviders;
   }

   private Map<String, HadoopDelegationTokenProvider> getDelegationTokenProviders() {
      Seq providers = (Seq)((TraversableLike)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new HadoopFSDelegationTokenProvider[]{new HadoopFSDelegationTokenProvider(this.fileSystems)}))).$plus$plus(Option$.MODULE$.option2Iterable(this.safeCreateProvider(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final HiveDelegationTokenProvider apply() {
            return new HiveDelegationTokenProvider();
         }
      })), Seq$.MODULE$.canBuildFrom())).$plus$plus(Option$.MODULE$.option2Iterable(this.safeCreateProvider(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final HBaseDelegationTokenProvider apply() {
            return new HBaseDelegationTokenProvider();
         }
      })), Seq$.MODULE$.canBuildFrom());
      return ((TraversableOnce)((TraversableLike)providers.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HadoopDelegationTokenManager $outer;

         public final boolean apply(HadoopDelegationTokenProvider p) {
            return this.$outer.isServiceEnabled(p.serviceName());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, HadoopDelegationTokenProvider> apply(HadoopDelegationTokenProvider p) {
            return new Tuple2<String, HadoopDelegationTokenProvider>(p.serviceName(), p);
         }
      }, Seq$.MODULE$.canBuildFrom())).<String, HadoopDelegationTokenProvider>toMap(Predef$.MODULE$.$conforms());
   }

   private Option<HadoopDelegationTokenProvider> safeCreateProvider(Function0<HadoopDelegationTokenProvider> createFn) {
      Object var10000;
      try {
         var10000 = new Some(createFn.apply());
      } catch (Throwable var4) {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to load built in provider."})))).s(Nil$.MODULE$);
            }
         }, var4);
         var10000 = None$.MODULE$;
         return (Option<HadoopDelegationTokenProvider>)var10000;
      }

      return (Option<HadoopDelegationTokenProvider>)var10000;
   }

   public boolean isServiceEnabled(String serviceName) {
      String key = (new StringOps(Predef$.MODULE$.augmentString(this.providerEnabledConfig()))).format(Predef$.MODULE$.genericWrapArray(new Object[]{serviceName}));
      this.deprecatedProviderEnabledConfigs().foreach(new Serializable(this, serviceName, key) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HadoopDelegationTokenManager $outer;
         private final String serviceName$1;
         public final String key$1;

         public final void apply(String pattern) {
            String deprecatedKey = (new StringOps(Predef$.MODULE$.augmentString(pattern))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.serviceName$1}));
            if (this.$outer.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$sparkConf.contains(deprecatedKey)) {
               this.$outer.logWarning(new Serializable(this, deprecatedKey) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final String deprecatedKey$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " is deprecated.  Please use ", " instead."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.deprecatedKey$1, this.$outer.key$1}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.deprecatedKey$1 = deprecatedKey$1;
                     }
                  }
               });
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.serviceName$1 = serviceName$1;
               this.key$1 = key$1;
            }
         }
      });
      boolean isEnabledDeprecated = this.deprecatedProviderEnabledConfigs().forall(new Serializable(this, serviceName) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HadoopDelegationTokenManager $outer;
         private final String serviceName$1;

         public final boolean apply(String pattern) {
            return BoxesRunTime.unboxToBoolean(this.$outer.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$sparkConf.getOption((new StringOps(Predef$.MODULE$.augmentString(pattern))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.serviceName$1}))).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(String x$1) {
                  return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toBoolean();
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply() {
                  return this.apply$mcZ$sp();
               }

               public boolean apply$mcZ$sp() {
                  return true;
               }
            }));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.serviceName$1 = serviceName$1;
            }
         }
      });
      return BoxesRunTime.unboxToBoolean(this.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$sparkConf.getOption(key).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$2) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$2))).toBoolean();
         }
      }).getOrElse(new Serializable(this, isEnabledDeprecated) {
         public static final long serialVersionUID = 0L;
         private final boolean isEnabledDeprecated$1;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            return this.isEnabledDeprecated$1;
         }

         public {
            this.isEnabledDeprecated$1 = isEnabledDeprecated$1;
         }
      }));
   }

   public Option<HadoopDelegationTokenProvider> getServiceDelegationTokenProvider(String service) {
      return this.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$delegationTokenProviders().get(service);
   }

   public long obtainDelegationTokens(Configuration hadoopConf, Credentials creds) {
      return BoxesRunTime.unboxToLong(((TraversableOnce)this.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$delegationTokenProviders().values().flatMap(new Serializable(this, hadoopConf, creds) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HadoopDelegationTokenManager $outer;
         private final Configuration hadoopConf$1;
         private final Credentials creds$1;

         public final Iterable<Object> apply(HadoopDelegationTokenProvider provider) {
            Iterable var10000;
            if (provider.delegationTokensRequired(this.$outer.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$sparkConf, this.hadoopConf$1)) {
               var10000 = Option$.MODULE$.option2Iterable(provider.obtainDelegationTokens(this.hadoopConf$1, this.$outer.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$sparkConf, this.creds$1));
            } else {
               this.$outer.logDebug(new Serializable(this, provider) {
                  public static final long serialVersionUID = 0L;
                  private final HadoopDelegationTokenProvider provider$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Service ", " does not require a token."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.provider$1.serviceName()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" Check your configuration to see if security is disabled or not."})))).s(Nil$.MODULE$)).toString();
                  }

                  public {
                     this.provider$1 = provider$1;
                  }
               });
               var10000 = Option$.MODULE$.option2Iterable(None$.MODULE$);
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.hadoopConf$1 = hadoopConf$1;
               this.creds$1 = creds$1;
            }
         }
      }, Iterable$.MODULE$.canBuildFrom())).foldLeft(BoxesRunTime.boxToLong(Long.MAX_VALUE), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(long x, long y) {
            return this.apply$mcJJJ$sp(x, y);
         }

         public long apply$mcJJJ$sp(long x, long y) {
            return package$.MODULE$.min(x, y);
         }
      }));
   }

   public HadoopDelegationTokenManager(SparkConf sparkConf, Configuration hadoopConf, Function1<Configuration, Set<FileSystem>> fileSystems) {
      this.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$sparkConf = sparkConf;
      this.fileSystems = fileSystems;
      Logging$class.$init$(this);
      this.deprecatedProviderEnabledConfigs = List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.yarn.security.tokens.%s.enabled", "spark.yarn.security.credentials.%s.enabled"})));
      this.providerEnabledConfig = "spark.security.credentials.%s.enabled";
      this.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$delegationTokenProviders = this.getDelegationTokenProviders();
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HadoopDelegationTokenManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using the following delegation token providers: "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$security$HadoopDelegationTokenManager$$delegationTokenProviders().keys().mkString(", ")}))).toString();
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

   public HadoopDelegationTokenManager(SparkConf sparkConf, Configuration hadoopConf) {
      this(sparkConf, hadoopConf, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Set<FileSystem> apply(Configuration hadoopConf) {
            return (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new FileSystem[]{FileSystem.get(hadoopConf).getHomeDirectory().getFileSystem(hadoopConf)})));
         }
      });
   }
}
