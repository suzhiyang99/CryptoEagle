package org.apache.spark.deploy;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.spark.deploy.master.RecoveryState$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.json4s.DefaultFormats;
import org.json4s.JsonAST;
import org.json4s.jackson.JsonMethods.;
import org.slf4j.Logger;
import scala.Enumeration;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.collection.IterableLike;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ManifestFactory$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005b\u0001B\u0001\u0003\t-\u0011a\u0002V3ti6\u000b7\u000f^3s\u0013:4wN\u0003\u0002\u0004\t\u00051A-\u001a9m_fT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\r\u0001AB\u0005\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M1R\"\u0001\u000b\u000b\u0005U!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005]!\"a\u0002'pO\u001eLgn\u001a\u0005\t3\u0001\u0011)\u0019!C\u00015\u0005\u0011\u0011\u000e]\u000b\u00027A\u0011Ad\b\b\u0003\u001buI!A\b\b\u0002\rA\u0013X\rZ3g\u0013\t\u0001\u0013E\u0001\u0004TiJLgn\u001a\u0006\u0003=9A\u0001b\t\u0001\u0003\u0002\u0003\u0006IaG\u0001\u0004SB\u0004\u0003\u0002C\u0013\u0001\u0005\u000b\u0007I\u0011\u0001\u0014\u0002\u0011\u0011|7m[3s\u0013\u0012,\u0012a\n\t\u0003Q%j\u0011AA\u0005\u0003U\t\u0011\u0001\u0002R8dW\u0016\u0014\u0018\n\u001a\u0005\tY\u0001\u0011\t\u0011)A\u0005O\u0005IAm\\2lKJLE\r\t\u0005\t]\u0001\u0011)\u0019!C\u0001_\u00059An\\4GS2,W#\u0001\u0019\u0011\u0005E2T\"\u0001\u001a\u000b\u0005M\"\u0014AA5p\u0015\u0005)\u0014\u0001\u00026bm\u0006L!a\u000e\u001a\u0003\t\u0019KG.\u001a\u0005\ts\u0001\u0011\t\u0011)A\u0005a\u0005AAn\\4GS2,\u0007\u0005C\u0003<\u0001\u0011\u0005A(\u0001\u0004=S:LGO\u0010\u000b\u0005{yz\u0004\t\u0005\u0002)\u0001!)\u0011D\u000fa\u00017!)QE\u000fa\u0001O!)aF\u000fa\u0001a!9!\t\u0001b\u0001\n\u0007\u0019\u0015a\u00024pe6\fGo]\u000b\u0002\t:\u0011Q)\u0014\b\u0003\r.s!a\u0012&\u000e\u0003!S!!\u0013\u0006\u0002\rq\u0012xn\u001c;?\u0013\u0005I\u0011B\u0001'\t\u0003\u0019Q7o\u001c85g&\u0011ajT\u0001\u000f\t\u00164\u0017-\u001e7u\r>\u0014X.\u0019;t\u0015\ta\u0005\u0002\u0003\u0004R\u0001\u0001\u0006I\u0001R\u0001\tM>\u0014X.\u0019;tA!I1\u000b\u0001a\u0001\u0002\u0004%\t\u0001V\u0001\u0006gR\fG/Z\u000b\u0002+B\u0011a\u000b\u0018\b\u0003/jk\u0011\u0001\u0017\u0006\u00033\n\ta!\\1ti\u0016\u0014\u0018BA.Y\u00035\u0011VmY8wKJL8\u000b^1uK&\u0011QL\u0018\u0002\u0006-\u0006dW/Z\u0005\u0003?:\u00111\"\u00128v[\u0016\u0014\u0018\r^5p]\"I\u0011\r\u0001a\u0001\u0002\u0004%\tAY\u0001\ngR\fG/Z0%KF$\"a\u00194\u0011\u00055!\u0017BA3\u000f\u0005\u0011)f.\u001b;\t\u000f\u001d\u0004\u0017\u0011!a\u0001+\u0006\u0019\u0001\u0010J\u0019\t\r%\u0004\u0001\u0015)\u0003V\u0003\u0019\u0019H/\u0019;fA!I1\u000e\u0001a\u0001\u0002\u0004%\t\u0001\\\u0001\u000eY&4XmV8sW\u0016\u0014\u0018\nU:\u0016\u00035\u00042A\\:\u001c\u001d\ty\u0017O\u0004\u0002Ha&\tq\"\u0003\u0002s\u001d\u00059\u0001/Y2lC\u001e,\u0017B\u0001;v\u0005\u0011a\u0015n\u001d;\u000b\u0005It\u0001\"C<\u0001\u0001\u0004\u0005\r\u0011\"\u0001y\u0003Ea\u0017N^3X_J\\WM]%Qg~#S-\u001d\u000b\u0003GfDqa\u001a<\u0002\u0002\u0003\u0007Q\u000e\u0003\u0004|\u0001\u0001\u0006K!\\\u0001\u000fY&4XmV8sW\u0016\u0014\u0018\nU:!\u0011\u001di\b\u00011A\u0005\u0002y\f1B\\;n\u0019&4X-\u00119qgV\tq\u0010E\u0002\u000e\u0003\u0003I1!a\u0001\u000f\u0005\rIe\u000e\u001e\u0005\n\u0003\u000f\u0001\u0001\u0019!C\u0001\u0003\u0013\tqB\\;n\u0019&4X-\u00119qg~#S-\u001d\u000b\u0004G\u0006-\u0001\u0002C4\u0002\u0006\u0005\u0005\t\u0019A@\t\u000f\u0005=\u0001\u0001)Q\u0005\u007f\u0006aa.^7MSZ,\u0017\t\u001d9tA!9\u00111\u0003\u0001\u0005\u0002\u0005U\u0011!\u0003:fC\u0012\u001cF/\u0019;f)\u0005\u0019\u0007bBA\r\u0001\u0011\u0005\u0011QC\u0001\u0005W&dG\u000eC\u0004\u0002\u001e\u0001!\t%a\b\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012a\u0007"
)
public class TestMasterInfo implements Logging {
   private final String ip;
   private final DockerId dockerId;
   private final File logFile;
   private final DefaultFormats formats;
   private Enumeration.Value state;
   private List<String> liveWorkerIPs;
   private int numLiveApps;
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

   public String ip() {
      return this.ip;
   }

   public DockerId dockerId() {
      return this.dockerId;
   }

   public File logFile() {
      return this.logFile;
   }

   public DefaultFormats formats() {
      return this.formats;
   }

   public Enumeration.Value state() {
      return this.state;
   }

   public void state_$eq(Enumeration.Value x$1) {
      this.state = x$1;
   }

   public List<String> liveWorkerIPs() {
      return this.liveWorkerIPs;
   }

   public void liveWorkerIPs_$eq(List<String> x$1) {
      this.liveWorkerIPs = x$1;
   }

   public int numLiveApps() {
      return this.numLiveApps;
   }

   public void numLiveApps_$eq(int x$1) {
      this.numLiveApps = x$1;
   }

   public void readState() {
      try {
         InputStreamReader masterStream = new InputStreamReader((new URL((new StringOps(Predef$.MODULE$.augmentString("http://%s:8080/json"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.ip()})))).openStream(), StandardCharsets.UTF_8);
         JsonAST.JValue json = .MODULE$.parse(org.json4s.package..MODULE$.reader2JsonInput(masterStream), .MODULE$.parse$default$2(), .MODULE$.parse$default$3());
         JsonAST.JValue workers = org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("workers");
         List liveWorkers = (List)workers.children().filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TestMasterInfo $outer;

            public final boolean apply(JsonAST.JValue w) {
               boolean var3;
               label23: {
                  Object var10000 = org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(w).$bslash("state")).extract(this.$outer.formats(), ManifestFactory$.MODULE$.classType(String.class));
                  String var2 = "ALIVE";
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label23;
                     }
                  } else if (var10000.equals(var2)) {
                     break label23;
                  }

                  var3 = false;
                  return var3;
               }

               var3 = true;
               return var3;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.liveWorkerIPs_$eq((List)liveWorkers.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TestMasterInfo $outer;

            public final String apply(JsonAST.JValue w) {
               return (new StringOps(Predef$.MODULE$.augmentString((new StringOps(Predef$.MODULE$.augmentString((String)org.json4s.package..MODULE$.jvalue2extractable(org.json4s.package..MODULE$.jvalue2monadic(w).$bslash("webuiaddress")).extract(this.$outer.formats(), ManifestFactory$.MODULE$.classType(String.class))))).stripPrefix("http://")))).stripSuffix(":8081");
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, List$.MODULE$.canBuildFrom()));
         this.numLiveApps_$eq(org.json4s.package..MODULE$.jvalue2monadic(json).$bslash("activeapps").children().size());
         JsonAST.JValue status = org.json4s.package..MODULE$.jvalue2monadic(json).$bslash$bslash("status");
         String stateString = (String)org.json4s.package..MODULE$.jvalue2extractable(status).extract(this.formats(), ManifestFactory$.MODULE$.classType(String.class));
         this.state_$eq((Enumeration.Value)((IterableLike)RecoveryState$.MODULE$.values().filter(new Serializable(this, stateString) {
            public static final long serialVersionUID = 0L;
            private final String stateString$1;

            public final boolean apply(Enumeration.Value state) {
               boolean var3;
               label23: {
                  String var10000 = state.toString();
                  String var2 = this.stateString$1;
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label23;
                     }
                  } else if (var10000.equals(var2)) {
                     break label23;
                  }

                  var3 = false;
                  return var3;
               }

               var3 = true;
               return var3;
            }

            public {
               this.stateString$1 = stateString$1;
            }
         })).head());
      } catch (Exception var8) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Exception";
            }
         }, var8);
      }

   }

   public void kill() {
      Docker$.MODULE$.kill(this.dockerId());
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString("[ip=%s, id=%s, logFile=%s, state=%s]"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.ip(), this.dockerId().id(), this.logFile().getAbsolutePath(), this.state()}));
   }

   public TestMasterInfo(String ip, DockerId dockerId, File logFile) {
      this.ip = ip;
      this.dockerId = dockerId;
      this.logFile = logFile;
      Logging$class.$init$(this);
      this.formats = org.json4s.DefaultFormats..MODULE$;
      this.numLiveApps = 0;
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TestMasterInfo $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Created master: ").append((Object)this.$outer).toString();
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
