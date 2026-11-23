package org.apache.spark.deploy.worker;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.DriverDescription;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.deploy.master.DriverState$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.util.Clock;
import org.apache.spark.util.ShutdownHookManager$;
import org.apache.spark.util.SystemClock;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.spark_project.guava.io.Files;
import scala.Enumeration;
import scala.Function0;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\t\u0015c!B\u0001\u0003\u0001\u0011a!\u0001\u0004#sSZ,'OU;o]\u0016\u0014(BA\u0002\u0005\u0003\u00199xN]6fe*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011AcF\u0007\u0002+)\u0011aCB\u0001\tS:$XM\u001d8bY&\u0011\u0001$\u0006\u0002\b\u0019><w-\u001b8h\u0011!Q\u0002A!A!\u0002\u0013a\u0012\u0001B2p]\u001a\u001c\u0001\u0001\u0005\u0002\u001e=5\ta!\u0003\u0002 \r\tI1\u000b]1sW\u000e{gN\u001a\u0005\tC\u0001\u0011)\u0019!C\u0001E\u0005AAM]5wKJLE-F\u0001$!\t!sE\u0004\u0002\u000fK%\u0011aeD\u0001\u0007!J,G-\u001a4\n\u0005!J#AB*ue&twM\u0003\u0002'\u001f!A1\u0006\u0001B\u0001B\u0003%1%A\u0005ee&4XM]%eA!AQ\u0006\u0001BC\u0002\u0013\u0005a&A\u0004x_J\\G)\u001b:\u0016\u0003=\u0002\"\u0001M\u001b\u000e\u0003ER!AM\u001a\u0002\u0005%|'\"\u0001\u001b\u0002\t)\fg/Y\u0005\u0003mE\u0012AAR5mK\"A\u0001\b\u0001B\u0001B\u0003%q&\u0001\u0005x_J\\G)\u001b:!\u0011!Q\u0004A!b\u0001\n\u0003q\u0013!C:qCJ\\\u0007j\\7f\u0011!a\u0004A!A!\u0002\u0013y\u0013AC:qCJ\\\u0007j\\7fA!Aa\b\u0001BC\u0002\u0013\u0005q(\u0001\u0006ee&4XM\u001d#fg\u000e,\u0012\u0001\u0011\t\u0003\u0003\nk\u0011\u0001B\u0005\u0003\u0007\u0012\u0011\u0011\u0003\u0012:jm\u0016\u0014H)Z:de&\u0004H/[8o\u0011!)\u0005A!A!\u0002\u0013\u0001\u0015a\u00033sSZ,'\u000fR3tG\u0002B\u0001b\u0001\u0001\u0003\u0006\u0004%\taR\u000b\u0002\u0011B\u0011\u0011\nT\u0007\u0002\u0015*\u00111JB\u0001\u0004eB\u001c\u0017BA'K\u00059\u0011\u0006oY#oIB|\u0017N\u001c;SK\u001aD\u0001b\u0014\u0001\u0003\u0002\u0003\u0006I\u0001S\u0001\bo>\u00148.\u001a:!\u0011!\t\u0006A!b\u0001\n\u0003\u0011\u0013!C<pe.,'/\u0016:m\u0011!\u0019\u0006A!A!\u0002\u0013\u0019\u0013AC<pe.,'/\u0016:mA!AQ\u000b\u0001BC\u0002\u0013\u0005a+A\btK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s+\u00059\u0006CA\u000fY\u0013\tIfAA\bTK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s\u0011!Y\u0006A!A!\u0002\u00139\u0016\u0001E:fGV\u0014\u0018\u000e^=NC:\fw-\u001a:!\u0011\u0015i\u0006\u0001\"\u0001_\u0003\u0019a\u0014N\\5u}QIq,\u00192dI\u00164w\r\u001b\t\u0003A\u0002i\u0011A\u0001\u0005\u00065q\u0003\r\u0001\b\u0005\u0006Cq\u0003\ra\t\u0005\u0006[q\u0003\ra\f\u0005\u0006uq\u0003\ra\f\u0005\u0006}q\u0003\r\u0001\u0011\u0005\u0006\u0007q\u0003\r\u0001\u0013\u0005\u0006#r\u0003\ra\t\u0005\u0006+r\u0003\ra\u0016\u0005\bU\u0002\u0001\r\u0011\"\u0003l\u0003\u001d\u0001(o\\2fgN,\u0012\u0001\u001c\t\u0004\u001d5|\u0017B\u00018\u0010\u0005\u0019y\u0005\u000f^5p]B\u0011\u0001o]\u0007\u0002c*\u0011!oM\u0001\u0005Y\u0006tw-\u0003\u0002uc\n9\u0001K]8dKN\u001c\bb\u0002<\u0001\u0001\u0004%Ia^\u0001\faJ|7-Z:t?\u0012*\u0017\u000f\u0006\u0002ywB\u0011a\"_\u0005\u0003u>\u0011A!\u00168ji\"9A0^A\u0001\u0002\u0004a\u0017a\u0001=%c!1a\u0010\u0001Q!\n1\f\u0001\u0002\u001d:pG\u0016\u001c8\u000f\t\u0015\u0004{\u0006\u0005\u0001c\u0001\b\u0002\u0004%\u0019\u0011QA\b\u0003\u0011Y|G.\u0019;jY\u0016D\u0011\"!\u0003\u0001\u0001\u0004%I!a\u0003\u0002\r-LG\u000e\\3e+\t\ti\u0001E\u0002\u000f\u0003\u001fI1!!\u0005\u0010\u0005\u001d\u0011un\u001c7fC:D\u0011\"!\u0006\u0001\u0001\u0004%I!a\u0006\u0002\u0015-LG\u000e\\3e?\u0012*\u0017\u000fF\u0002y\u00033A\u0011\u0002`A\n\u0003\u0003\u0005\r!!\u0004\t\u0011\u0005u\u0001\u0001)Q\u0005\u0003\u001b\tqa[5mY\u0016$\u0007\u0005\u000b\u0003\u0002\u001c\u0005\u0005\u0001BCA\u0012\u0001\u0001\u0007I\u0011\u0001\u0002\u0002&\u0005Qa-\u001b8bYN#\u0018\r^3\u0016\u0005\u0005\u001d\u0002\u0003\u0002\bn\u0003S\u0001B!a\u000b\u0002P9!\u0011QFA%\u001d\u0011\ty#!\u0012\u000f\t\u0005E\u00121\t\b\u0005\u0003g\t\tE\u0004\u0003\u00026\u0005}b\u0002BA\u001c\u0003{i!!!\u000f\u000b\u0007\u0005m2$\u0001\u0004=e>|GOP\u0005\u0002\u0017%\u0011\u0011BC\u0005\u0003\u000f!I!!\u0002\u0004\n\u0007\u0005\u001dC!\u0001\u0004nCN$XM]\u0005\u0005\u0003\u0017\ni%A\u0006Ee&4XM]*uCR,'bAA$\t%!\u0011\u0011KA*\u0005-!%/\u001b<feN#\u0018\r^3\u000b\t\u0005-\u0013Q\n\u0005\u000b\u0003/\u0002\u0001\u0019!C\u0001\u0005\u0005e\u0013A\u00044j]\u0006d7\u000b^1uK~#S-\u001d\u000b\u0004q\u0006m\u0003\"\u0003?\u0002V\u0005\u0005\t\u0019AA\u0014\u0011!\ty\u0006\u0001Q!\n\u0005\u001d\u0012a\u00034j]\u0006d7\u000b^1uK\u0002BC!!\u0018\u0002\u0002!Q\u0011Q\r\u0001A\u0002\u0013\u0005!!a\u001a\u0002\u001d\u0019Lg.\u00197Fq\u000e,\u0007\u000f^5p]V\u0011\u0011\u0011\u000e\t\u0005\u001d5\fY\u0007\u0005\u0003\u0002n\u0005]d\u0002BA8\u0003grA!a\u000e\u0002r%\t\u0001#C\u0002\u0002v=\tq\u0001]1dW\u0006<W-\u0003\u0003\u0002z\u0005m$!C#yG\u0016\u0004H/[8o\u0015\r\t)h\u0004\u0005\u000b\u0003\u007f\u0002\u0001\u0019!C\u0001\u0005\u0005\u0005\u0015A\u00054j]\u0006dW\t_2faRLwN\\0%KF$2\u0001_AB\u0011%a\u0018QPA\u0001\u0002\u0004\tI\u0007\u0003\u0005\u0002\b\u0002\u0001\u000b\u0015BA5\u0003=1\u0017N\\1m\u000bb\u001cW\r\u001d;j_:\u0004\u0003\u0006BAC\u0003\u0003A\u0011\"!$\u0001\u0005\u0004%I!a$\u00027\u0011\u0013\u0016JV#S?R+%+T%O\u0003R+u\fV%N\u000b>+FkX'T+\t\t\t\nE\u0002\u000f\u0003'K1!!&\u0010\u0005\u0011auN\\4\t\u0011\u0005e\u0005\u0001)A\u0005\u0003#\u000bA\u0004\u0012*J-\u0016\u0013v\fV#S\u001b&s\u0015\tV#`)&kUiT+U?6\u001b\u0006\u0005C\u0004\u0002\u001e\u0002!\t!a(\u0002\u0011M,Go\u00117pG.$2\u0001_AQ\u0011!\t\u0019+a'A\u0002\u0005\u0015\u0016AB0dY>\u001c7\u000e\u0005\u0003\u0002(\u00065VBAAU\u0015\r\tYKB\u0001\u0005kRLG.\u0003\u0003\u00020\u0006%&!B\"m_\u000e\\\u0007bBAZ\u0001\u0011\u0005\u0011QW\u0001\u000bg\u0016$8\u000b\\3fa\u0016\u0014Hc\u0001=\u00028\"A\u0011\u0011XAY\u0001\u0004\tY,\u0001\u0005`g2,W\r]3s!\r\u0001\u0017QX\u0005\u0004\u0003\u007f\u0013!aB*mK\u0016\u0004XM\u001d\u0005\n\u0003\u0007\u0004\u0001\u0019!C\u0005\u0003\u000b\fQa\u00197pG.,\"!!*\t\u0013\u0005%\u0007\u00011A\u0005\n\u0005-\u0017!C2m_\u000e\\w\fJ3r)\rA\u0018Q\u001a\u0005\ny\u0006\u001d\u0017\u0011!a\u0001\u0003KC\u0001\"!5\u0001A\u0003&\u0011QU\u0001\u0007G2|7m\u001b\u0011\t\u0013\u0005U\u0007\u00011A\u0005\n\u0005]\u0017aB:mK\u0016\u0004XM]\u000b\u0003\u00033\u0014R!a7\u000e\u0003w3q!!8\u0002`\u0002\tIN\u0001\u0007=e\u00164\u0017N\\3nK:$h\b\u0003\u0005\u0002b\u0002\u0001\u000b\u0015BAm\u0003!\u0019H.Z3qKJ\u0004\u0003\"CAs\u0001\u0001\u0007I\u0011BAt\u0003-\u0019H.Z3qKJ|F%Z9\u0015\u0007a\fI\u000fC\u0005}\u0003G\f\t\u00111\u0001\u0002Z\"A\u0011Q\u001e\u0001\u0005\u0002\t\ty/A\u0003ti\u0006\u0014H\u000fF\u0001y\u0011!\t\u0019\u0010\u0001C\u0001\u0005\u0005=\u0018\u0001B6jY2Dq!a>\u0001\t\u0013\tI0\u0001\fde\u0016\fG/Z,pe.Lgn\u001a#je\u0016\u001cGo\u001c:z)\u0005y\u0003bBA\u007f\u0001\u0011%\u0011q`\u0001\u0010I><h\u000e\\8bIV\u001bXM\u001d&beR\u00191E!\u0001\t\u000f\t\r\u00111 a\u0001_\u0005IAM]5wKJ$\u0015N\u001d\u0005\t\u0005\u000f\u0001A\u0011\u0001\u0002\u0003\n\u0005\u0019\u0002O]3qCJ,\u0017I\u001c3Sk:$%/\u001b<feR\u0011!1\u0002\t\u0004\u001d\t5\u0011b\u0001B\b\u001f\t\u0019\u0011J\u001c;\t\u000f\tM\u0001\u0001\"\u0003\u0003\u0016\u0005I!/\u001e8Ee&4XM\u001d\u000b\t\u0005\u0017\u00119B!\t\u0003&!A!\u0011\u0004B\t\u0001\u0004\u0011Y\"A\u0004ck&dG-\u001a:\u0011\u0007A\u0014i\"C\u0002\u0003 E\u0014a\u0002\u0015:pG\u0016\u001c8OQ;jY\u0012,'\u000fC\u0004\u0003$\tE\u0001\u0019A\u0018\u0002\u000f\t\f7/\u001a#je\"A!q\u0005B\t\u0001\u0004\ti!A\u0005tkB,'O^5tK\"A!1\u0006\u0001\u0005\u0002\t\u0011i#A\nsk:\u001cu.\\7b]\u0012<\u0016\u000e\u001e5SKR\u0014\u0018\u0010\u0006\u0005\u0003\f\t=\"\u0011\bB\"\u0011!\u0011\tD!\u000bA\u0002\tM\u0012aB2p[6\fg\u000e\u001a\t\u0004A\nU\u0012b\u0001B\u001c\u0005\t\u0011\u0002K]8dKN\u001c()^5mI\u0016\u0014H*[6f\u0011!\u0011YD!\u000bA\u0002\tu\u0012AC5oSRL\u0017\r\\5{KB)aBa\u0010pq&\u0019!\u0011I\b\u0003\u0013\u0019+hn\u0019;j_:\f\u0004\u0002\u0003B\u0014\u0005S\u0001\r!!\u0004"
)
public class DriverRunner implements Logging {
   private final SparkConf conf;
   private final String driverId;
   private final File workDir;
   private final File sparkHome;
   private final DriverDescription driverDesc;
   private final RpcEndpointRef worker;
   private final String workerUrl;
   private final SecurityManager securityManager;
   private volatile Option<Process> process;
   private volatile boolean org$apache$spark$deploy$worker$DriverRunner$$killed;
   private volatile Option<Enumeration.Value> finalState;
   private volatile Option<Exception> finalException;
   private final long org$apache$spark$deploy$worker$DriverRunner$$DRIVER_TERMINATE_TIMEOUT_MS;
   private Clock clock;
   private Sleeper sleeper;
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

   public String driverId() {
      return this.driverId;
   }

   public File workDir() {
      return this.workDir;
   }

   public File sparkHome() {
      return this.sparkHome;
   }

   public DriverDescription driverDesc() {
      return this.driverDesc;
   }

   public RpcEndpointRef worker() {
      return this.worker;
   }

   public String workerUrl() {
      return this.workerUrl;
   }

   public SecurityManager securityManager() {
      return this.securityManager;
   }

   private Option<Process> process() {
      return this.process;
   }

   private void process_$eq(Option<Process> x$1) {
      this.process = x$1;
   }

   public boolean org$apache$spark$deploy$worker$DriverRunner$$killed() {
      return this.org$apache$spark$deploy$worker$DriverRunner$$killed;
   }

   private void org$apache$spark$deploy$worker$DriverRunner$$killed_$eq(boolean x$1) {
      this.org$apache$spark$deploy$worker$DriverRunner$$killed = x$1;
   }

   public Option<Enumeration.Value> finalState() {
      return this.finalState;
   }

   public void finalState_$eq(Option<Enumeration.Value> x$1) {
      this.finalState = x$1;
   }

   public Option<Exception> finalException() {
      return this.finalException;
   }

   public void finalException_$eq(Option<Exception> x$1) {
      this.finalException = x$1;
   }

   public long org$apache$spark$deploy$worker$DriverRunner$$DRIVER_TERMINATE_TIMEOUT_MS() {
      return this.org$apache$spark$deploy$worker$DriverRunner$$DRIVER_TERMINATE_TIMEOUT_MS;
   }

   public void setClock(Clock _clock) {
      this.clock_$eq(_clock);
   }

   public void setSleeper(Sleeper _sleeper) {
      this.sleeper_$eq(_sleeper);
   }

   private Clock clock() {
      return this.clock;
   }

   private void clock_$eq(Clock x$1) {
      this.clock = x$1;
   }

   private Sleeper sleeper() {
      return this.sleeper;
   }

   private void sleeper_$eq(Sleeper x$1) {
      this.sleeper = x$1;
   }

   public void start() {
      (new Thread(this) {
         // $FF: synthetic field
         private final DriverRunner $outer;

         public void run() {
            Object shutdownHook = null;

            try {
               shutdownHook = ShutdownHookManager$.MODULE$.addShutdownHook(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     this.$outer.org$apache$spark$deploy$worker$DriverRunner$$anon$$$outer().logInfo(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Worker shutting down, killing driver ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$worker$DriverRunner$$anon$$anonfun$$$outer().org$apache$spark$deploy$worker$DriverRunner$$anon$$$outer().driverId()}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     this.$outer.org$apache$spark$deploy$worker$DriverRunner$$anon$$$outer().kill();
                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$deploy$worker$DriverRunner$$anon$$anonfun$$$outer() {
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
               int exitCode = this.$outer.prepareAndRunDriver();
               this.$outer.finalState_$eq(exitCode == 0 ? new Some(DriverState$.MODULE$.FINISHED()) : (this.$outer.org$apache$spark$deploy$worker$DriverRunner$$killed() ? new Some(DriverState$.MODULE$.KILLED()) : new Some(DriverState$.MODULE$.FAILED())));
            } catch (Exception var7) {
               this.$outer.kill();
               this.$outer.finalState_$eq(new Some(DriverState$.MODULE$.ERROR()));
               this.$outer.finalException_$eq(new Some(var7));
            } finally {
               if (shutdownHook != null) {
                  ShutdownHookManager$.MODULE$.removeShutdownHook(shutdownHook);
               }

            }

            this.$outer.worker().send(new DeployMessages.DriverStateChanged(this.$outer.driverId(), (Enumeration.Value)this.$outer.finalState().get(), this.$outer.finalException()));
         }

         // $FF: synthetic method
         public DriverRunner org$apache$spark$deploy$worker$DriverRunner$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).start();
   }

   public void kill() {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Killing driver process!";
         }
      });
      this.org$apache$spark$deploy$worker$DriverRunner$$killed_$eq(true);
      synchronized(this) {
         this.process().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DriverRunner $outer;

            public final void apply(Process p) {
               Option exitCode = Utils$.MODULE$.terminateProcess(p, this.$outer.org$apache$spark$deploy$worker$DriverRunner$$DRIVER_TERMINATE_TIMEOUT_MS());
               if (exitCode.isEmpty()) {
                  this.$outer.logWarning(new Serializable(this, p) {
                     public static final long serialVersionUID = 0L;
                     private final Process p$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Failed to terminate driver process: ").append((Object)this.p$1).append((Object)". This process will likely be orphaned.").toString();
                     }

                     public {
                        this.p$1 = p$1;
                     }
                  });
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
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   private File createWorkingDirectory() {
      File driverDir = new File(this.workDir(), this.driverId());
      if (!driverDir.exists() && !driverDir.mkdirs()) {
         throw new IOException((new StringBuilder()).append((Object)"Failed to create directory ").append((Object)driverDir).toString());
      } else {
         return driverDir;
      }
   }

   private String downloadUserJar(File driverDir) {
      String jarFileName = (String)Predef$.MODULE$.refArrayOps((Object[])(new URI(this.driverDesc().jarUrl())).getPath().split("/")).last();
      File localJarFile = new File(driverDir, jarFileName);
      if (!localJarFile.exists()) {
         this.logInfo(new Serializable(this, localJarFile) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DriverRunner $outer;
            private final File localJarFile$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Copying user jar ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.driverDesc().jarUrl(), this.localJarFile$1}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.localJarFile$1 = localJarFile$1;
               }
            }
         });
         Utils$.MODULE$.fetchFile(this.driverDesc().jarUrl(), driverDir, this.conf, this.securityManager(), SparkHadoopUtil$.MODULE$.get().newConfiguration(this.conf), System.currentTimeMillis(), false);
         if (!localJarFile.exists()) {
            throw new IOException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can not find expected jar ", " which should have been loaded in ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{jarFileName, driverDir})));
         }
      }

      return localJarFile.getAbsolutePath();
   }

   public int prepareAndRunDriver() {
      File driverDir = this.createWorkingDirectory();
      String localJarFilename = this.downloadUserJar(driverDir);
      ProcessBuilder builder = CommandUtils$.MODULE$.buildProcessBuilder(this.driverDesc().command(), this.securityManager(), this.driverDesc().mem(), this.sparkHome().getAbsolutePath(), new Serializable(this, localJarFilename) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DriverRunner $outer;
         private final String localJarFilename$1;

         public final String apply(String argument) {
            return this.$outer.org$apache$spark$deploy$worker$DriverRunner$$substituteVariables$1(argument, this.localJarFilename$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.localJarFilename$1 = localJarFilename$1;
            }
         }
      }, CommandUtils$.MODULE$.buildProcessBuilder$default$6(), CommandUtils$.MODULE$.buildProcessBuilder$default$7());
      return this.runDriver(builder, driverDir, this.driverDesc().supervise());
   }

   private int runDriver(ProcessBuilder builder, File baseDir, boolean supervise) {
      builder.directory(baseDir);
      return this.runCommandWithRetry(ProcessBuilderLike$.MODULE$.apply(builder), new Serializable(this, builder, baseDir) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DriverRunner $outer;
         private final ProcessBuilder builder$1;
         private final File baseDir$1;

         public final void apply(Process process) {
            this.$outer.org$apache$spark$deploy$worker$DriverRunner$$initialize$1(process, this.builder$1, this.baseDir$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.builder$1 = builder$1;
               this.baseDir$1 = baseDir$1;
            }
         }
      }, supervise);
   }

   public int runCommandWithRetry(ProcessBuilderLike command, Function1<Process, BoxedUnit> initialize, boolean supervise) {
      IntRef exitCode = IntRef.create(-1);
      IntRef waitSeconds = IntRef.create(1);
      int successfulRunDuration = 5;
      boolean keepTrying = !this.org$apache$spark$deploy$worker$DriverRunner$$killed();

      while(keepTrying) {
         this.logInfo(new Serializable(this, command) {
            public static final long serialVersionUID = 0L;
            private final ProcessBuilderLike command$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Launch Command: ").append((Object)this.command$1.command().mkString("\"", "\" \"", "\"")).toString();
            }

            public {
               this.command$1 = command$1;
            }
         });
         synchronized(this) {
            if (this.org$apache$spark$deploy$worker$DriverRunner$$killed()) {
               return exitCode.elem;
            }

            this.process_$eq(new Some(command.start()));
            initialize.apply(this.process().get());
         }

         long processStart = this.clock().getTimeMillis();
         exitCode.elem = ((Process)this.process().get()).waitFor();
         keepTrying = supervise && exitCode.elem != 0 && !this.org$apache$spark$deploy$worker$DriverRunner$$killed();
         if (keepTrying) {
            if (this.clock().getTimeMillis() - processStart > (long)(successfulRunDuration * 1000)) {
               waitSeconds.elem = 1;
            }

            this.logInfo(new Serializable(this, exitCode, waitSeconds) {
               public static final long serialVersionUID = 0L;
               private final IntRef exitCode$1;
               private final IntRef waitSeconds$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Command exited with status ", ", re-launching after ", " s."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.exitCode$1.elem), BoxesRunTime.boxToInteger(this.waitSeconds$1.elem)}));
               }

               public {
                  this.exitCode$1 = exitCode$1;
                  this.waitSeconds$1 = waitSeconds$1;
               }
            });
            this.sleeper().sleep(waitSeconds.elem);
            waitSeconds.elem *= 2;
         }
      }

      return exitCode.elem;
   }

   public final String org$apache$spark$deploy$worker$DriverRunner$$substituteVariables$1(String argument, String localJarFilename$1) {
      String var4;
      if ("{{WORKER_URL}}".equals(argument)) {
         var4 = this.workerUrl();
      } else if ("{{USER_JAR}}".equals(argument)) {
         var4 = localJarFilename$1;
      } else {
         var4 = argument;
      }

      return var4;
   }

   public final void org$apache$spark$deploy$worker$DriverRunner$$initialize$1(Process process, ProcessBuilder builder$1, File baseDir$1) {
      File stdout = new File(baseDir$1, "stdout");
      CommandUtils$.MODULE$.redirectStream(process.getInputStream(), stdout);
      File stderr = new File(baseDir$1, "stderr");
      String formattedCommand = ((TraversableOnce)JavaConverters$.MODULE$.asScalaBufferConverter(builder$1.command()).asScala()).mkString("\"", "\" \"", "\"");
      String header = (new StringOps(Predef$.MODULE$.augmentString("Launch Command: %s\n%s\n\n"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{formattedCommand, (new StringOps(Predef$.MODULE$.augmentString("="))).$times(40)}));
      Files.append(header, stderr, StandardCharsets.UTF_8);
      CommandUtils$.MODULE$.redirectStream(process.getErrorStream(), stderr);
   }

   public DriverRunner(SparkConf conf, String driverId, File workDir, File sparkHome, DriverDescription driverDesc, RpcEndpointRef worker, String workerUrl, SecurityManager securityManager) {
      this.conf = conf;
      this.driverId = driverId;
      this.workDir = workDir;
      this.sparkHome = sparkHome;
      this.driverDesc = driverDesc;
      this.worker = worker;
      this.workerUrl = workerUrl;
      this.securityManager = securityManager;
      Logging$class.$init$(this);
      this.process = None$.MODULE$;
      this.org$apache$spark$deploy$worker$DriverRunner$$killed = false;
      this.finalState = None$.MODULE$;
      this.finalException = None$.MODULE$;
      this.org$apache$spark$deploy$worker$DriverRunner$$DRIVER_TERMINATE_TIMEOUT_MS = conf.getTimeAsMs("spark.worker.driverTerminateTimeout", "10s");
      this.clock = new SystemClock();
      this.sleeper = new Sleeper(this) {
         // $FF: synthetic field
         private final DriverRunner $outer;

         public void sleep(int seconds) {
            RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), seconds).takeWhile(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final boolean apply(int x$1) {
                  return this.apply$mcZI$sp(x$1);
               }

               public boolean apply$mcZI$sp(int x$1) {
                  Thread.sleep(1000L);
                  return !this.$outer.org$apache$spark$deploy$worker$DriverRunner$$anon$$$outer().org$apache$spark$deploy$worker$DriverRunner$$killed();
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

         // $FF: synthetic method
         public DriverRunner org$apache$spark$deploy$worker$DriverRunner$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
   }
}
