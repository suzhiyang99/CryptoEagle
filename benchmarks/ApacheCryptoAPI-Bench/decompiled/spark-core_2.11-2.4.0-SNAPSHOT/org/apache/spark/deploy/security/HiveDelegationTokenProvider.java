package org.apache.spark.deploy.security;

import java.lang.reflect.UndeclaredThrowableException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.package$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001a4Q!\u0001\u0002\u0001\u00051\u00111\u0004S5wK\u0012+G.Z4bi&|g\u000eV8lK:\u0004&o\u001c<jI\u0016\u0014(BA\u0002\u0005\u0003!\u0019XmY;sSRL(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0003\u0001\u001bM9\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015+5\t!!\u0003\u0002\u0017\u0005\ti\u0002*\u00193p_B$U\r\\3hCRLwN\u001c+pW\u0016t\u0007K]8wS\u0012,'\u000f\u0005\u0002\u001975\t\u0011D\u0003\u0002\u001b\r\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u001d3\t9Aj\\4hS:<\u0007\"\u0002\u0010\u0001\t\u0003\u0001\u0013A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003\u0005\u0002\"\u0001\u0006\u0001\t\u000b\r\u0002A\u0011\t\u0013\u0002\u0017M,'O^5dK:\u000bW.Z\u000b\u0002KA\u0011a%\u000b\b\u0003\u001d\u001dJ!\u0001K\b\u0002\rA\u0013X\rZ3g\u0013\tQ3F\u0001\u0004TiJLgn\u001a\u0006\u0003Q=Aq!\f\u0001C\u0002\u0013%a&A\u000bdY\u0006\u001c8OT8u\r>,h\u000eZ#se>\u00148\u000b\u001e:\u0016\u0003=\u0002\"\u0001M\u001b\u000e\u0003ER!AM\u001a\u0002\t1\fgn\u001a\u0006\u0002i\u0005!!.\u0019<b\u0013\tQ\u0013\u0007\u0003\u00048\u0001\u0001\u0006IaL\u0001\u0017G2\f7o\u001d(pi\u001a{WO\u001c3FeJ|'o\u0015;sA!)\u0011\b\u0001C\u0005u\u0005A\u0001.\u001b<f\u0007>tg\r\u0006\u0002<\u0007B\u0011A(Q\u0007\u0002{)\u0011ahP\u0001\u0005G>tgM\u0003\u0002A\u0011\u00051\u0001.\u00193p_BL!AQ\u001f\u0003\u001b\r{gNZ5hkJ\fG/[8o\u0011\u0015!\u0005\b1\u0001<\u0003)A\u0017\rZ8pa\u000e{gN\u001a\u0005\u0006\r\u0002!\teR\u0001\u0019I\u0016dWmZ1uS>tGk\\6f]N\u0014V-];je\u0016$Gc\u0001%L#B\u0011a\"S\u0005\u0003\u0015>\u0011qAQ8pY\u0016\fg\u000eC\u0003M\u000b\u0002\u0007Q*A\u0005ta\u0006\u00148nQ8oMB\u0011ajT\u0007\u0002\r%\u0011\u0001K\u0002\u0002\n'B\f'o[\"p]\u001aDQ\u0001R#A\u0002mBQa\u0015\u0001\u0005BQ\u000bac\u001c2uC&tG)\u001a7fO\u0006$\u0018n\u001c8U_.,gn\u001d\u000b\u0005+ncV\fE\u0002\u000f-bK!aV\b\u0003\r=\u0003H/[8o!\tq\u0011,\u0003\u0002[\u001f\t!Aj\u001c8h\u0011\u0015!%\u000b1\u0001<\u0011\u0015a%\u000b1\u0001N\u0011\u0015q&\u000b1\u0001`\u0003\u0015\u0019'/\u001a3t!\t\u0001'-D\u0001b\u0015\t\u0019q(\u0003\u0002dC\nY1I]3eK:$\u0018.\u00197t\u0011\u0015)\u0007\u0001\"\u0003g\u00031!w.Q:SK\u0006dWk]3s+\t9'\u000e\u0006\u0002igB\u0011\u0011N\u001b\u0007\u0001\t\u0015YGM1\u0001m\u0005\u0005!\u0016CA7q!\tqa.\u0003\u0002p\u001f\t9aj\u001c;iS:<\u0007C\u0001\br\u0013\t\u0011xBA\u0002B]fDa\u0001\u001e3\u0005\u0002\u0004)\u0018A\u00014o!\rqa\u000f[\u0005\u0003o>\u0011\u0001\u0002\u00102z]\u0006lWM\u0010"
)
public class HiveDelegationTokenProvider implements HadoopDelegationTokenProvider, Logging {
   private final String org$apache$spark$deploy$security$HiveDelegationTokenProvider$$classNotFoundErrorStr;
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

   public String serviceName() {
      return "hive";
   }

   public String org$apache$spark$deploy$security$HiveDelegationTokenProvider$$classNotFoundErrorStr() {
      return this.org$apache$spark$deploy$security$HiveDelegationTokenProvider$$classNotFoundErrorStr;
   }

   private Configuration hiveConf(Configuration hadoopConf) {
      Object var10000;
      try {
         var10000 = new HiveConf(hadoopConf, HiveConf.class);
      } catch (Throwable var8) {
         Option var4 = NonFatal$.MODULE$.unapply(var8);
         Configuration var6;
         if (var4.isEmpty()) {
            if (!(var8 instanceof NoClassDefFoundError)) {
               throw var8;
            }

            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final HiveDelegationTokenProvider $outer;

               public final String apply() {
                  return this.$outer.org$apache$spark$deploy$security$HiveDelegationTokenProvider$$classNotFoundErrorStr();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            var6 = hadoopConf;
         } else {
            Throwable e = (Throwable)var4.get();
            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Fail to create Hive Configuration";
               }
            }, e);
            var6 = hadoopConf;
         }

         var10000 = var6;
         return (Configuration)var10000;
      }

      return (Configuration)var10000;
   }

   public boolean delegationTokensRequired(SparkConf sparkConf, Configuration hadoopConf) {
      boolean var10000;
      label40: {
         String deployMode = sparkConf.get("spark.submit.deployMode", "client");
         if (UserGroupInformation.isSecurityEnabled() && (new StringOps(Predef$.MODULE$.augmentString(this.hiveConf(hadoopConf).getTrimmed("hive.metastore.uris", "")))).nonEmpty()) {
            label34: {
               if (SparkHadoopUtil$.MODULE$.get().isProxyUser(UserGroupInformation.getCurrentUser())) {
                  break label40;
               }

               String var4 = "cluster";
               if (deployMode == null) {
                  if (var4 != null) {
                     break label34;
                  }
               } else if (!deployMode.equals(var4)) {
                  break label34;
               }

               if (!sparkConf.contains((ConfigEntry)package$.MODULE$.KEYTAB())) {
                  break label40;
               }
            }
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   public Option<Object> obtainDelegationTokens(Configuration hadoopConf, SparkConf sparkConf, Credentials creds) {
      None$ var10000;
      try {
         try {
            Configuration conf = this.hiveConf(hadoopConf);
            String principalKey = "hive.metastore.kerberos.principal";
            String principal = conf.getTrimmed(principalKey, "");
            Predef$.MODULE$.require((new StringOps(Predef$.MODULE$.augmentString(principal))).nonEmpty(), new Serializable(this, principalKey) {
               public static final long serialVersionUID = 0L;
               private final String principalKey$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Hive principal ", " undefined"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.principalKey$1}));
               }

               public {
                  this.principalKey$1 = principalKey$1;
               }
            });
            String metastoreUri = conf.getTrimmed("hive.metastore.uris", "");
            Predef$.MODULE$.require((new StringOps(Predef$.MODULE$.augmentString(metastoreUri))).nonEmpty(), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Hive metastore uri undefined";
               }
            });
            UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
            this.logDebug(new Serializable(this, principal, metastoreUri, currentUser) {
               public static final long serialVersionUID = 0L;
               private final String principal$1;
               private final String metastoreUri$1;
               private final UserGroupInformation currentUser$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Getting Hive delegation token for ", " against "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.currentUser$1.getUserName()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " at ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.principal$1, this.metastoreUri$1}))).toString();
               }

               public {
                  this.principal$1 = principal$1;
                  this.metastoreUri$1 = metastoreUri$1;
                  this.currentUser$1 = currentUser$1;
               }
            });
            this.doAsRealUser(new Serializable(this, creds, conf, principal, currentUser) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final HiveDelegationTokenProvider $outer;
               private final Credentials creds$1;
               private final Configuration conf$1;
               private final String principal$1;
               private final UserGroupInformation currentUser$1;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  Hive hive = Hive.get(this.conf$1, HiveConf.class);
                  String tokenStr = hive.getDelegationToken(this.currentUser$1.getUserName(), this.principal$1);
                  Token hive2Token = new Token();
                  hive2Token.decodeFromUrlString(tokenStr);
                  this.$outer.logDebug(new Serializable(this, hive2Token) {
                     public static final long serialVersionUID = 0L;
                     private final Token hive2Token$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Get Token from hive metastore: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.hive2Token$1.toString()}));
                     }

                     public {
                        this.hive2Token$1 = hive2Token$1;
                     }
                  });
                  this.creds$1.addToken(new Text("hive.server2.delegation.token"), hive2Token);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.creds$1 = creds$1;
                     this.conf$1 = conf$1;
                     this.principal$1 = principal$1;
                     this.currentUser$1 = currentUser$1;
                  }
               }
            });
            var10000 = None$.MODULE$;
         } catch (Throwable var19) {
            Option var7 = NonFatal$.MODULE$.unapply(var19);
            None$ var9;
            if (var7.isEmpty()) {
               if (!(var19 instanceof NoClassDefFoundError)) {
                  throw var19;
               }

               this.logWarning(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final HiveDelegationTokenProvider $outer;

                  public final String apply() {
                     return this.$outer.org$apache$spark$deploy$security$HiveDelegationTokenProvider$$classNotFoundErrorStr();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               var9 = None$.MODULE$;
            } else {
               Throwable e = (Throwable)var7.get();
               this.logDebug(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final HiveDelegationTokenProvider $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to get token from service ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.serviceName()}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, e);
               var9 = None$.MODULE$;
            }

            var10000 = var9;
            return var10000;
         }
      } finally {
         Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               Hive.closeCurrent();
            }
         });
      }

      return var10000;
   }

   private <T> T doAsRealUser(Function0<T> fn) {
      UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
      UserGroupInformation realUser = (UserGroupInformation)Option$.MODULE$.apply(currentUser.getRealUser()).getOrElse(new Serializable(this, currentUser) {
         public static final long serialVersionUID = 0L;
         private final UserGroupInformation currentUser$2;

         public final UserGroupInformation apply() {
            return this.currentUser$2;
         }

         public {
            this.currentUser$2 = currentUser$2;
         }
      });

      try {
         return (T)realUser.doAs(new PrivilegedExceptionAction<T>(this, fn) {
            private final Function0 fn$1;

            public T run() {
               return (T)this.fn$1.apply();
            }

            public {
               this.fn$1 = fn$1;
            }
         });
      } catch (UndeclaredThrowableException var5) {
         throw (Throwable)Option$.MODULE$.apply(var5.getCause()).getOrElse(new Serializable(this, var5) {
            public static final long serialVersionUID = 0L;
            private final UndeclaredThrowableException e$1;

            public final UndeclaredThrowableException apply() {
               return this.e$1;
            }

            public {
               this.e$1 = e$1;
            }
         });
      }
   }

   public HiveDelegationTokenProvider() {
      Logging$class.$init$(this);
      this.org$apache$spark$deploy$security$HiveDelegationTokenProvider$$classNotFoundErrorStr = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"You are attempting to use the "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", but your Spark distribution is not built with Hive libraries."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.getClass().getCanonicalName()}))).toString();
   }
}
