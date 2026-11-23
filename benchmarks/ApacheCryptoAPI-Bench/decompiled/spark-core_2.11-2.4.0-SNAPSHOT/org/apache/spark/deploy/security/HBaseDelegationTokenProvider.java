package org.apache.spark.deploy.security;

import java.lang.reflect.Method;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.reflect.api.JavaUniverse;
import scala.reflect.runtime.package.;
import scala.runtime.BoxedUnit;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001a3Q!\u0001\u0002\u0001\u00051\u0011A\u0004\u0013\"bg\u0016$U\r\\3hCRLwN\u001c+pW\u0016t\u0007K]8wS\u0012,'O\u0003\u0002\u0004\t\u0005A1/Z2ve&$\u0018P\u0003\u0002\u0006\r\u00051A-\u001a9m_fT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0005\u00015\u0019r\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)Ui\u0011AA\u0005\u0003-\t\u0011Q\u0004S1e_>\u0004H)\u001a7fO\u0006$\u0018n\u001c8U_.,g\u000e\u0015:pm&$WM\u001d\t\u00031mi\u0011!\u0007\u0006\u00035\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00039e\u0011q\u0001T8hO&tw\rC\u0003\u001f\u0001\u0011\u0005\u0001%\u0001\u0004=S:LGOP\u0002\u0001)\u0005\t\u0003C\u0001\u000b\u0001\u0011\u0015\u0019\u0003\u0001\"\u0011%\u0003-\u0019XM\u001d<jG\u0016t\u0015-\\3\u0016\u0003\u0015\u0002\"AJ\u0015\u000f\u000599\u0013B\u0001\u0015\u0010\u0003\u0019\u0001&/\u001a3fM&\u0011!f\u000b\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005!z\u0001\"B\u0017\u0001\t\u0003r\u0013AF8ci\u0006Lg\u000eR3mK\u001e\fG/[8o)>\\WM\\:\u0015\t=*t(\u0012\t\u0004\u001dA\u0012\u0014BA\u0019\u0010\u0005\u0019y\u0005\u000f^5p]B\u0011abM\u0005\u0003i=\u0011A\u0001T8oO\")a\u0007\fa\u0001o\u0005Q\u0001.\u00193p_B\u001cuN\u001c4\u0011\u0005ajT\"A\u001d\u000b\u0005iZ\u0014\u0001B2p]\u001aT!\u0001\u0010\u0005\u0002\r!\fGm\\8q\u0013\tq\u0014HA\u0007D_:4\u0017nZ;sCRLwN\u001c\u0005\u0006\u00012\u0002\r!Q\u0001\ngB\f'o[\"p]\u001a\u0004\"AQ\"\u000e\u0003\u0019I!\u0001\u0012\u0004\u0003\u0013M\u0003\u0018M]6D_:4\u0007\"\u0002$-\u0001\u00049\u0015!B2sK\u0012\u001c\bC\u0001%K\u001b\u0005I%BA\u0002<\u0013\tY\u0015JA\u0006De\u0016$WM\u001c;jC2\u001c\b\"B'\u0001\t\u0003r\u0015\u0001\u00073fY\u0016<\u0017\r^5p]R{7.\u001a8t%\u0016\fX/\u001b:fIR\u0019qJU*\u0011\u00059\u0001\u0016BA)\u0010\u0005\u001d\u0011un\u001c7fC:DQ\u0001\u0011'A\u0002\u0005CQA\u000e'A\u0002]BQ!\u0016\u0001\u0005\nY\u000b\u0011\u0002\u001b2bg\u0016\u001cuN\u001c4\u0015\u0005]:\u0006\"\u0002\u001eU\u0001\u00049\u0004"
)
public class HBaseDelegationTokenProvider implements HadoopDelegationTokenProvider, Logging {
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
      return "hbase";
   }

   public Option<Object> obtainDelegationTokens(Configuration hadoopConf, SparkConf sparkConf, Credentials creds) {
      try {
         JavaUniverse.JavaMirror mirror = .MODULE$.universe().runtimeMirror(Utils$.MODULE$.getContextOrSparkClassLoader());
         Method obtainToken = mirror.classLoader().loadClass("org.apache.hadoop.hbase.security.token.TokenUtil").getMethod("obtainToken", Configuration.class);
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Attempting to fetch HBase security token.";
            }
         });
         Token token = (Token)obtainToken.invoke((Object)null, this.hbaseConf(hadoopConf));
         this.logInfo(new Serializable(this, token) {
            public static final long serialVersionUID = 0L;
            private final Token token$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Get token from HBase: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.token$1.toString()}));
            }

            public {
               this.token$1 = token$1;
            }
         });
         creds.addToken(token.getService(), token);
      } catch (Throwable var13) {
         Option var6 = NonFatal$.MODULE$.unapply(var13);
         if (var6.isEmpty()) {
            throw var13;
         }

         Throwable e = (Throwable)var6.get();
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final HBaseDelegationTokenProvider $outer;

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
         BoxedUnit var8 = BoxedUnit.UNIT;
         return None$.MODULE$;
      }

      return None$.MODULE$;
   }

   public boolean delegationTokensRequired(SparkConf sparkConf, Configuration hadoopConf) {
      boolean var4;
      label23: {
         String var10000 = this.hbaseConf(hadoopConf).get("hbase.security.authentication");
         String var3 = "kerberos";
         if (var10000 == null) {
            if (var3 == null) {
               break label23;
            }
         } else if (var10000.equals(var3)) {
            break label23;
         }

         var4 = false;
         return var4;
      }

      var4 = true;
      return var4;
   }

   private Configuration hbaseConf(Configuration conf) {
      Configuration var10000;
      try {
         JavaUniverse.JavaMirror mirror = .MODULE$.universe().runtimeMirror(Utils$.MODULE$.getContextOrSparkClassLoader());
         Method confCreate = mirror.classLoader().loadClass("org.apache.hadoop.hbase.HBaseConfiguration").getMethod("create", Configuration.class);
         var10000 = (Configuration)confCreate.invoke((Object)null, conf);
      } catch (Throwable var10) {
         Option var4 = NonFatal$.MODULE$.unapply(var10);
         if (var4.isEmpty()) {
            throw var10;
         }

         Throwable e = (Throwable)var4.get();
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Fail to invoke HBaseConfiguration";
            }
         }, e);
         var10000 = conf;
         return var10000;
      }

      return var10000;
   }

   public HBaseDelegationTokenProvider() {
      Logging$class.$init$(this);
   }
}
