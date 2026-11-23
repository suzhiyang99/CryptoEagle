package org.apache.spark.deploy.security;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.Master;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.package$;
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
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.JavaConverters$;
import scala.collection.TraversableLike;
import scala.collection.immutable.Set;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.util.Try$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%a!B\u0001\u0003\u0001\u0011a!a\b%bI>|\u0007OR*EK2,w-\u0019;j_:$vn[3o!J|g/\u001b3fe*\u00111\u0001B\u0001\tg\u0016\u001cWO]5us*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001cB\u0001A\u0007\u0014/A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"\u0001F\u000b\u000e\u0003\tI!A\u0006\u0002\u0003;!\u000bGm\\8q\t\u0016dWmZ1uS>tGk\\6f]B\u0013xN^5eKJ\u0004\"\u0001G\u000e\u000e\u0003eQ!A\u0007\u0004\u0002\u0011%tG/\u001a:oC2L!\u0001H\r\u0003\u000f1{wmZ5oO\"Aa\u0004\u0001B\u0001B\u0003%\u0001%A\u0006gS2,7+_:uK6\u001c8\u0001\u0001\t\u0005\u001d\u0005\u001a3&\u0003\u0002#\u001f\tIa)\u001e8di&|g.\r\t\u0003I%j\u0011!\n\u0006\u0003M\u001d\nAaY8oM*\u0011\u0001\u0006C\u0001\u0007Q\u0006$wn\u001c9\n\u0005)*#!D\"p]\u001aLw-\u001e:bi&|g\u000eE\u0002-_Ir!AD\u0017\n\u00059z\u0011A\u0002)sK\u0012,g-\u0003\u00021c\t\u00191+\u001a;\u000b\u00059z\u0001CA\u001a7\u001b\u0005!$BA\u001b(\u0003\t17/\u0003\u00028i\tQa)\u001b7f'f\u001cH/Z7\t\u000be\u0002A\u0011\u0001\u001e\u0002\rqJg.\u001b;?)\tYD\b\u0005\u0002\u0015\u0001!)a\u0004\u000fa\u0001A!9a\b\u0001a\u0001\n\u0013y\u0014\u0001\u0006;pW\u0016t'+\u001a8fo\u0006d\u0017J\u001c;feZ\fG.F\u0001A!\rq\u0011iQ\u0005\u0003\u0005>\u0011aa\u00149uS>t\u0007C\u0001\bE\u0013\t)uB\u0001\u0003M_:<\u0007bB$\u0001\u0001\u0004%I\u0001S\u0001\u0019i>\\WM\u001c*f]\u0016<\u0018\r\\%oi\u0016\u0014h/\u00197`I\u0015\fHCA%M!\tq!*\u0003\u0002L\u001f\t!QK\\5u\u0011\u001die)!AA\u0002\u0001\u000b1\u0001\u001f\u00132\u0011\u0019y\u0005\u0001)Q\u0005\u0001\u0006)Bo\\6f]J+g.Z<bY&sG/\u001a:wC2\u0004\u0003bB)\u0001\u0005\u0004%\tEU\u0001\fg\u0016\u0014h/[2f\u001d\u0006lW-F\u0001T!\taC+\u0003\u0002Vc\t11\u000b\u001e:j]\u001eDaa\u0016\u0001!\u0002\u0013\u0019\u0016\u0001D:feZL7-\u001a(b[\u0016\u0004\u0003\"B-\u0001\t\u0003R\u0016AF8ci\u0006Lg\u000eR3mK\u001e\fG/[8o)>\\WM\\:\u0015\t\u0001[Vl\u0019\u0005\u00069b\u0003\raI\u0001\u000bQ\u0006$wn\u001c9D_:4\u0007\"\u00020Y\u0001\u0004y\u0016!C:qCJ\\7i\u001c8g!\t\u0001\u0017-D\u0001\u0007\u0013\t\u0011gAA\u0005Ta\u0006\u00148nQ8oM\")A\r\u0017a\u0001K\u0006)1M]3egB\u0011a\r[\u0007\u0002O*\u00111aJ\u0005\u0003S\u001e\u00141b\u0011:fI\u0016tG/[1mg\")1\u000e\u0001C!Y\u0006AB-\u001a7fO\u0006$\u0018n\u001c8U_.,gn\u001d*fcVL'/\u001a3\u0015\u00075\u0004\u0018\u000f\u0005\u0002\u000f]&\u0011qn\u0004\u0002\b\u0005>|G.Z1o\u0011\u0015q&\u000e1\u0001`\u0011\u0015a&\u000e1\u0001$\u0011\u0015\u0019\b\u0001\"\u0003u\u0003=9W\r\u001e+pW\u0016t'+\u001a8fo\u0016\u0014HCA*v\u0011\u0015a&\u000f1\u0001$\u0011\u00159\b\u0001\"\u0003y\u0003U1W\r^2i\t\u0016dWmZ1uS>tGk\\6f]N$B!Z=|{\")!P\u001ea\u0001'\u00069!/\u001a8fo\u0016\u0014\b\"\u0002?w\u0001\u0004Y\u0013a\u00034jY\u0016\u001c\u0018p\u001d;f[NDQ\u0001\u001a<A\u0002\u0015Daa \u0001\u0005\n\u0005\u0005\u0011aF4fiR{7.\u001a8SK:,w/\u00197J]R,'O^1m)\u001d\u0001\u00151AA\u0003\u0003\u000fAQ\u0001\u0018@A\u0002\rBQA\u0018@A\u0002}CQ\u0001 @A\u0002-\u0002"
)
public class HadoopFSDelegationTokenProvider implements HadoopDelegationTokenProvider, Logging {
   private final Function1<Configuration, Set<FileSystem>> fileSystems;
   private Option<Object> tokenRenewalInterval;
   private final String serviceName;
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

   private Option<Object> tokenRenewalInterval() {
      return this.tokenRenewalInterval;
   }

   private void tokenRenewalInterval_$eq(Option<Object> x$1) {
      this.tokenRenewalInterval = x$1;
   }

   public String serviceName() {
      return this.serviceName;
   }

   public Option<Object> obtainDelegationTokens(Configuration hadoopConf, SparkConf sparkConf, Credentials creds) {
      Set fsToGetTokens = this.fileSystems.apply(hadoopConf);
      Credentials fetchCreds = this.org$apache$spark$deploy$security$HadoopFSDelegationTokenProvider$$fetchDelegationTokens(this.getTokenRenewer(hadoopConf), fsToGetTokens, creds);
      if (this.tokenRenewalInterval() == null) {
         this.tokenRenewalInterval_$eq(this.getTokenRenewalInterval(hadoopConf, sparkConf, fsToGetTokens));
      }

      Option nextRenewalDate = this.tokenRenewalInterval().flatMap(new Serializable(this, fetchCreds) {
         public static final long serialVersionUID = 0L;
         private final Credentials fetchCreds$1;

         public final Option<Object> apply(long interval) {
            Iterable nextRenewalDates = (Iterable)((TraversableLike)((TraversableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.fetchCreds$1.getAllTokens()).asScala()).filter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(Token<? extends TokenIdentifier> x$1) {
                  return x$1.decodeIdentifier() instanceof AbstractDelegationTokenIdentifier;
               }
            })).map(new Serializable(this, interval) {
               public static final long serialVersionUID = 0L;
               private final long interval$2;

               public final long apply(Token<? extends TokenIdentifier> token) {
                  AbstractDelegationTokenIdentifier identifier = (AbstractDelegationTokenIdentifier)token.decodeIdentifier();
                  return identifier.getIssueDate() + this.interval$2;
               }

               public {
                  this.interval$2 = interval$2;
               }
            }, Iterable$.MODULE$.canBuildFrom());
            return (Option<Object>)(nextRenewalDates.isEmpty() ? None$.MODULE$ : new Some(nextRenewalDates.min(Ordering.Long$.MODULE$)));
         }

         public {
            this.fetchCreds$1 = fetchCreds$1;
         }
      });
      return nextRenewalDate;
   }

   public boolean delegationTokensRequired(SparkConf sparkConf, Configuration hadoopConf) {
      return UserGroupInformation.isSecurityEnabled();
   }

   private String getTokenRenewer(Configuration hadoopConf) {
      String tokenRenewer = Master.getMasterPrincipal(hadoopConf);
      this.logDebug(new Serializable(this, tokenRenewer) {
         public static final long serialVersionUID = 0L;
         private final String tokenRenewer$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Delegation token renewer is: ").append((Object)this.tokenRenewer$1).toString();
         }

         public {
            this.tokenRenewer$1 = tokenRenewer$1;
         }
      });
      if (tokenRenewer != null && tokenRenewer.length() != 0) {
         return tokenRenewer;
      } else {
         String errorMessage = "Can't get Master Kerberos principal for use as renewer.";
         this.logError(new Serializable(this, errorMessage) {
            public static final long serialVersionUID = 0L;
            private final String errorMessage$1;

            public final String apply() {
               return this.errorMessage$1;
            }

            public {
               this.errorMessage$1 = errorMessage$1;
            }
         });
         throw new SparkException(errorMessage);
      }
   }

   public Credentials org$apache$spark$deploy$security$HadoopFSDelegationTokenProvider$$fetchDelegationTokens(String renewer, Set<FileSystem> filesystems, Credentials creds) {
      filesystems.foreach(new Serializable(this, renewer, creds) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HadoopFSDelegationTokenProvider $outer;
         private final String renewer$1;
         private final Credentials creds$1;

         public final Token<?>[] apply(FileSystem fs) {
            this.$outer.logInfo(new Serializable(this, fs) {
               public static final long serialVersionUID = 0L;
               private final FileSystem fs$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"getting token for: ").append((Object)this.fs$1).toString();
               }

               public {
                  this.fs$1 = fs$1;
               }
            });
            return fs.addDelegationTokens(this.renewer$1, this.creds$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.renewer$1 = renewer$1;
               this.creds$1 = creds$1;
            }
         }
      });
      return creds;
   }

   private Option<Object> getTokenRenewalInterval(Configuration hadoopConf, SparkConf sparkConf, Set<FileSystem> filesystems) {
      return ((Option)sparkConf.get((ConfigEntry)package$.MODULE$.PRINCIPAL())).<Object>flatMap(new Serializable(this, hadoopConf, filesystems) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HadoopFSDelegationTokenProvider $outer;
         public final Configuration hadoopConf$1;
         private final Set filesystems$1;

         public final Option<Object> apply(String renewer) {
            Credentials creds = new Credentials();
            this.$outer.org$apache$spark$deploy$security$HadoopFSDelegationTokenProvider$$fetchDelegationTokens(renewer, this.filesystems$1, creds);
            Iterable renewIntervals = (Iterable)((TraversableLike)((TraversableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(creds.getAllTokens()).asScala()).filter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(Token<? extends TokenIdentifier> x$2) {
                  return x$2.decodeIdentifier() instanceof AbstractDelegationTokenIdentifier;
               }
            })).flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final Iterable<Object> apply(Token<? extends TokenIdentifier> token) {
                  return Option$.MODULE$.<Object>option2Iterable(Try$.MODULE$.apply(new Serializable(this, token) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     public final Token token$1;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        long newExpiration = this.token$1.renew(this.$outer.org$apache$spark$deploy$security$HadoopFSDelegationTokenProvider$$anonfun$$anonfun$$$outer().hadoopConf$1);
                        AbstractDelegationTokenIdentifier identifier = (AbstractDelegationTokenIdentifier)this.token$1.decodeIdentifier();
                        long interval = newExpiration - identifier.getIssueDate();
                        this.$outer.org$apache$spark$deploy$security$HadoopFSDelegationTokenProvider$$anonfun$$anonfun$$$outer().org$apache$spark$deploy$security$HadoopFSDelegationTokenProvider$$anonfun$$$outer().logInfo(new Serializable(this, interval) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final long interval$1;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Renewal interval is ", " for token ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.interval$1), this.$outer.token$1.getKind().toString()}));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.interval$1 = interval$1;
                              }
                           }
                        });
                        return interval;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.token$1 = token$1;
                        }
                     }
                  }).toOption());
               }

               // $FF: synthetic method
               public <undefinedtype> org$apache$spark$deploy$security$HadoopFSDelegationTokenProvider$$anonfun$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, Iterable$.MODULE$.canBuildFrom());
            return (Option<Object>)(renewIntervals.isEmpty() ? None$.MODULE$ : new Some(renewIntervals.min(Ordering.Long$.MODULE$)));
         }

         // $FF: synthetic method
         public HadoopFSDelegationTokenProvider org$apache$spark$deploy$security$HadoopFSDelegationTokenProvider$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.hadoopConf$1 = hadoopConf$1;
               this.filesystems$1 = filesystems$1;
            }
         }
      });
   }

   public HadoopFSDelegationTokenProvider(Function1<Configuration, Set<FileSystem>> fileSystems) {
      this.fileSystems = fileSystems;
      Logging$class.$init$(this);
      this.tokenRenewalInterval = null;
      this.serviceName = "hadoopfs";
   }
}
