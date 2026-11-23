package org.apache.spark.deploy.history;

import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.ShutdownHookManager$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

public final class HistoryServer$ implements Logging {
   public static final HistoryServer$ MODULE$;
   private final SparkConf conf;
   private final String UI_PATH_PREFIX;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new HistoryServer$();
   }

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

   private SparkConf conf() {
      return this.conf;
   }

   public String UI_PATH_PREFIX() {
      return this.UI_PATH_PREFIX;
   }

   public void main(String[] argStrings) {
      Utils$.MODULE$.initDaemon(this.log());
      new HistoryServerArguments(this.conf(), argStrings);
      this.initSecurity();
      SecurityManager securityManager = this.createSecurityManager(this.conf());
      String providerName = (String)this.conf().getOption("spark.history.provider").getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return FsHistoryProvider.class.getName();
         }
      });
      ApplicationHistoryProvider provider = (ApplicationHistoryProvider)Utils$.MODULE$.classForName(providerName).getConstructor(SparkConf.class).newInstance(this.conf());
      int port = BoxesRunTime.unboxToInt(this.conf().get(config$.MODULE$.HISTORY_SERVER_UI_PORT()));
      HistoryServer server = new HistoryServer(this.conf(), provider, securityManager, port);
      server.bind();
      ShutdownHookManager$.MODULE$.addShutdownHook(new Serializable(server) {
         public static final long serialVersionUID = 0L;
         private final HistoryServer server$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.server$1.stop();
         }

         public {
            this.server$1 = server$1;
         }
      });

      while(true) {
         Thread.sleep(2147483647L);
      }
   }

   public SecurityManager createSecurityManager(SparkConf config) {
      if (config.getBoolean(SecurityManager$.MODULE$.SPARK_AUTH_CONF(), false)) {
         this.logDebug(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Clearing ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{SecurityManager$.MODULE$.SPARK_AUTH_CONF()}));
            }
         });
         config.set(SecurityManager$.MODULE$.SPARK_AUTH_CONF(), "false");
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      if (config.getBoolean("spark.acls.enable", config.getBoolean("spark.ui.acls.enable", false))) {
         this.logInfo(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Either spark.acls.enable or spark.ui.acls.enable is configured, clearing it and only using spark.history.ui.acl.enable";
            }
         });
         config.set("spark.acls.enable", "false");
         config.set("spark.ui.acls.enable", "false");
      } else {
         BoxedUnit var2 = BoxedUnit.UNIT;
      }

      return new SecurityManager(config, SecurityManager$.MODULE$.$lessinit$greater$default$2());
   }

   public void initSecurity() {
      if (this.conf().getBoolean("spark.history.kerberos.enabled", false)) {
         String principalName = this.conf().get("spark.history.kerberos.principal");
         String keytabFilename = this.conf().get("spark.history.kerberos.keytab");
         SparkHadoopUtil$.MODULE$.get().loginUserFromKeytab(principalName, keytabFilename);
      }

   }

   public String getAttemptURI(String appId, Option<String> attemptId) {
      String attemptSuffix = (String)attemptId.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String id) {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{id}));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/", "", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.UI_PATH_PREFIX(), appId, attemptSuffix}));
   }

   private HistoryServer$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.conf = new SparkConf();
      this.UI_PATH_PREFIX = "/history";
   }
}
