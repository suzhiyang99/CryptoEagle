package org.apache.spark.deploy.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.apache.spark.SparkException;
import org.apache.spark.package$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Await$;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.concurrent.duration.package;
import scala.io.Codec$;
import scala.io.Source$;
import scala.reflect.ScalaSignature;
import scala.runtime.BooleanRef;
import scala.runtime.BoxedUnit;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.NonLocalReturnControl$mcV$sp;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015h!B\u0001\u0003\u0001\u0019a!\u0001\u0006*fgR\u001cVOY7jgNLwN\\\"mS\u0016tGO\u0003\u0002\u0004\t\u0005!!/Z:u\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\n\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!r#D\u0001\u0016\u0015\t1b!\u0001\u0005j]R,'O\\1m\u0013\tARCA\u0004M_\u001e<\u0017N\\4\t\u0011i\u0001!\u0011!Q\u0001\nq\ta!\\1ti\u0016\u00148\u0001\u0001\t\u0003;\u0001r!A\u0004\u0010\n\u0005}y\u0011A\u0002)sK\u0012,g-\u0003\u0002\"E\t11\u000b\u001e:j]\u001eT!aH\b\t\u000b\u0011\u0002A\u0011A\u0013\u0002\rqJg.\u001b;?)\t1\u0003\u0006\u0005\u0002(\u00015\t!\u0001C\u0003\u001bG\u0001\u0007A\u0004C\u0004+\u0001\t\u0007I\u0011B\u0016\u0002/M,\b\u000f]8si\u0016$W*Y:uKJ\u0004&/\u001a4jq\u0016\u001cX#\u0001\u0017\u0011\u00075\u0002$'D\u0001/\u0015\tys\"\u0001\u0006d_2dWm\u0019;j_:L!!\r\u0018\u0003\u0007M+\u0017\u000f\u0005\u00024q5\tAG\u0003\u00026m\u0005!A.\u00198h\u0015\u00059\u0014\u0001\u00026bm\u0006L!!\t\u001b\t\ri\u0002\u0001\u0015!\u0003-\u0003a\u0019X\u000f\u001d9peR,G-T1ti\u0016\u0014\bK]3gSb,7\u000f\t\u0005\by\u0001\u0011\r\u0011\"\u0003>\u0003\u001di\u0017m\u001d;feN,\u0012A\u0010\t\u0004\u001d}b\u0012B\u0001!\u0010\u0005\u0015\t%O]1z\u0011\u0019\u0011\u0005\u0001)A\u0005}\u0005AQ.Y:uKJ\u001c\b\u0005C\u0004E\u0001\t\u0007I\u0011B#\u0002\u00171|7\u000f^'bgR,'o]\u000b\u0002\rB\u0019qI\u0013\u000f\u000e\u0003!S!!\u0013\u0018\u0002\u000f5,H/\u00192mK&\u00111\n\u0013\u0002\b\u0011\u0006\u001c\bnU3u\u0011\u0019i\u0005\u0001)A\u0005\r\u0006aAn\\:u\u001b\u0006\u001cH/\u001a:tA!)q\n\u0001C\u0001!\u0006\u00012M]3bi\u0016\u001cVOY7jgNLwN\u001c\u000b\u0003#R\u0003\"a\n*\n\u0005M\u0013!AG*vE6LGOU3tiB\u0013x\u000e^8d_2\u0014Vm\u001d9p]N,\u0007\"B+O\u0001\u00041\u0016a\u0002:fcV,7\u000f\u001e\t\u0003O]K!\u0001\u0017\u0002\u0003/\r\u0013X-\u0019;f'V\u0014W.[:tS>t'+Z9vKN$\b\"\u0002.\u0001\t\u0003Y\u0016AD6jY2\u001cVOY7jgNLwN\u001c\u000b\u0003#rCQ!X-A\u0002q\tAb];c[&\u001c8/[8o\u0013\u0012DQa\u0018\u0001\u0005\u0002\u0001\fqC]3rk\u0016\u001cHoU;c[&\u001c8/[8o'R\fG/^:\u0015\u0007E\u000b'\rC\u0003^=\u0002\u0007A\u0004C\u0004d=B\u0005\t\u0019\u00013\u0002\u000bE,\u0018.\u001a;\u0011\u00059)\u0017B\u00014\u0010\u0005\u001d\u0011un\u001c7fC:DQ\u0001\u001b\u0001\u0005\u0002%\facY8ogR\u0014Xo\u0019;Tk\nl\u0017\u000e\u001e*fcV,7\u000f\u001e\u000b\u0007-*dg\u000e];\t\u000b-<\u0007\u0019\u0001\u000f\u0002\u0017\u0005\u0004\bOU3t_V\u00148-\u001a\u0005\u0006[\u001e\u0004\r\u0001H\u0001\n[\u0006Lgn\u00117bgNDQa\\4A\u0002y\nq!\u00199q\u0003J<7\u000fC\u0003rO\u0002\u0007!/A\bta\u0006\u00148\u000e\u0015:pa\u0016\u0014H/[3t!\u0011i2\u000f\b\u000f\n\u0005Q\u0014#aA'ba\")ao\u001aa\u0001e\u0006!RM\u001c<je>tW.\u001a8u-\u0006\u0014\u0018.\u00192mKNDQ\u0001\u001f\u0001\u0005\ne\f1aZ3u)\t\t&\u0010C\u0003|o\u0002\u0007A0A\u0002ve2\u00042!`A\u0001\u001b\u0005q(BA@7\u0003\rqW\r^\u0005\u0004\u0003\u0007q(aA+S\u0019\"9\u0011q\u0001\u0001\u0005\n\u0005%\u0011\u0001\u00029pgR$2!UA\u0006\u0011\u0019Y\u0018Q\u0001a\u0001y\"9\u0011q\u0002\u0001\u0005\n\u0005E\u0011\u0001\u00039pgRT5o\u001c8\u0015\u000bE\u000b\u0019\"!\u0006\t\rm\fi\u00011\u0001}\u0011\u001d\t9\"!\u0004A\u0002q\tAA[:p]\"A\u00111\u0004\u0001\u0005\u0002\t\ti\"\u0001\u0007sK\u0006$'+Z:q_:\u001cX\rF\u0002R\u0003?A\u0001\"!\t\u0002\u001a\u0001\u0007\u00111E\u0001\u000bG>tg.Z2uS>t\u0007cA?\u0002&%\u0019\u0011q\u0005@\u0003#!#H\u000f]+S\u0019\u000e{gN\\3di&|g\u000eC\u0004\u0002,\u0001!I!!\f\u0002\u0019\u001d,GoU;c[&$XK\u001d7\u0015\u0007q\fy\u0003\u0003\u0004\u001b\u0003S\u0001\r\u0001\b\u0005\b\u0003g\u0001A\u0011BA\u001b\u0003)9W\r^&jY2,&\u000f\u001c\u000b\u0006y\u0006]\u0012\u0011\b\u0005\u00075\u0005E\u0002\u0019\u0001\u000f\t\ru\u000b\t\u00041\u0001\u001d\u0011\u001d\ti\u0004\u0001C\u0005\u0003\u007f\tAbZ3u'R\fG/^:Ve2$R\u0001`A!\u0003\u0007BaAGA\u001e\u0001\u0004a\u0002BB/\u0002<\u0001\u0007A\u0004C\u0004\u0002H\u0001!I!!\u0013\u0002\u0015\u001d,GOQ1tKV\u0013H\u000eF\u0002\u001d\u0003\u0017BaAGA#\u0001\u0004a\u0002bBA(\u0001\u0011%\u0011\u0011K\u0001\u000fm\u0006d\u0017\u000eZ1uK6\u000b7\u000f^3s)\u0011\t\u0019&!\u0017\u0011\u00079\t)&C\u0002\u0002X=\u0011A!\u00168ji\"1!$!\u0014A\u0002qAq!!\u0018\u0001\t\u0013\ty&\u0001\fsKB|'\u000f^*vE6L7o]5p]N#\u0018\r^;t)\u0011\t\u0019&!\u0019\t\u0011\u0005\r\u00141\fa\u0001\u0003K\nab];c[&$(+Z:q_:\u001cX\rE\u0002(\u0003OJ1!!\u001b\u0003\u0005a\u0019%/Z1uKN+(-\\5tg&|gNU3ta>t7/\u001a\u0005\b\u0003[\u0002A\u0011BA8\u0003Q\u0001x\u000e\u001c7Tk\nl\u0017n]:j_:\u001cF/\u0019;vgR!\u00111KA9\u0011\u0019i\u00161\u000ea\u00019!9\u0011Q\u000f\u0001\u0005\n\u0005]\u0014A\u00055b]\u0012dWMU3tiJ+7\u000f]8og\u0016$B!a\u0015\u0002z!9\u00111PA:\u0001\u0004\t\u0016\u0001\u0003:fgB|gn]3\t\u000f\u0005}\u0004\u0001\"\u0003\u0002\u0002\u0006a\u0002.\u00198eY\u0016,f.\u001a=qK\u000e$X\r\u001a*fgR\u0014Vm\u001d9p]N,G\u0003BA*\u0003\u0007Cq!!\"\u0002~\u0001\u0007\u0011+\u0001\u0006v]\u0016D\b/Z2uK\u0012Dq!!#\u0001\t\u0013\tY)A\riC:$G.Z\"p]:,7\r^5p]\u0016C8-\u001a9uS>tGc\u00013\u0002\u000e\"9\u0011qRAD\u0001\u0004a\u0012!C7bgR,'/\u0016:m\u0011%\t\u0019\nAI\u0001\n\u0003\t)*A\u0011sKF,Xm\u001d;Tk\nl\u0017n]:j_:\u001cF/\u0019;vg\u0012\"WMZ1vYR$#'\u0006\u0002\u0002\u0018*\u001aA-!',\u0005\u0005m\u0005\u0003BAO\u0003Ok!!a(\u000b\t\u0005\u0005\u00161U\u0001\nk:\u001c\u0007.Z2lK\u0012T1!!*\u0010\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003S\u000byJA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016<\u0001\"!,\u0003\u0011\u00031\u0011qV\u0001\u0015%\u0016\u001cHoU;c[&\u001c8/[8o\u00072LWM\u001c;\u0011\u0007\u001d\n\tLB\u0004\u0002\u0005!\u0005a!a-\u0014\u0007\u0005EV\u0002C\u0004%\u0003c#\t!a.\u0015\u0005\u0005=\u0006BCA^\u0003c\u0013\r\u0011\"\u0003\u0002>\u0006i\"+\u0012)P%R{FIU%W\u000bJ{6\u000bV!U+N{\u0016J\u0014+F%Z\u000bE*\u0006\u0002\u0002@B\u0019a\"!1\n\u0007\u0005\rwBA\u0002J]RD\u0011\"a2\u00022\u0002\u0006I!a0\u0002=I+\u0005k\u0014*U?\u0012\u0013\u0016JV#S?N#\u0016\tV+T?&sE+\u0012*W\u00032\u0003\u0003BCAf\u0003c\u0013\r\u0011\"\u0003\u0002>\u0006q\"+\u0012)P%R{FIU%W\u000bJ{6\u000bV!U+N{V*\u0011-`)JKUi\u0015\u0005\n\u0003\u001f\f\t\f)A\u0005\u0003\u007f\u000bqDU#Q\u001fJ#v\f\u0012*J-\u0016\u0013vl\u0015+B)V\u001bv,T!Y?R\u0013\u0016*R*!\u0011)\t\u0019.!-C\u0002\u0013\u0005\u0011Q[\u0001\u0011!J{EkT\"P\u0019~3VIU*J\u001f:+\u0012A\r\u0005\t\u00033\f\t\f)A\u0005e\u0005\t\u0002KU(U\u001f\u000e{Ej\u0018,F%NKuJ\u0014\u0011\t\u0013\u0005u\u0017\u0011\u0017C\u0001\u0005\u0005}\u0017a\u00064jYR,'oU=ti\u0016lWI\u001c<je>tW.\u001a8u)\r\u0011\u0018\u0011\u001d\u0005\b\u0003G\fY\u000e1\u0001s\u0003\r)gN\u001e"
)
public class RestSubmissionClient implements Logging {
   public final String org$apache$spark$deploy$rest$RestSubmissionClient$$master;
   private final Seq<String> supportedMasterPrefixes;
   private final String[] masters;
   private final HashSet<String> lostMasters;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static String PROTOCOL_VERSION() {
      return RestSubmissionClient$.MODULE$.PROTOCOL_VERSION();
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

   private Seq<String> supportedMasterPrefixes() {
      return this.supportedMasterPrefixes;
   }

   private String[] masters() {
      return this.masters;
   }

   private HashSet<String> lostMasters() {
      return this.lostMasters;
   }

   public SubmitRestProtocolResponse createSubmission(CreateSubmissionRequest request) {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RestSubmissionClient $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Submitting a request to launch an application in ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$master}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      BooleanRef handled = BooleanRef.create(false);
      ObjectRef response = ObjectRef.create((Object)null);
      Predef$.MODULE$.refArrayOps((Object[])this.masters()).withFilter(new Serializable(this, handled) {
         public static final long serialVersionUID = 0L;
         private final BooleanRef handled$1;

         public final boolean apply(String m) {
            return !this.handled$1.elem;
         }

         public {
            this.handled$1 = handled$1;
         }
      }).foreach(new Serializable(this, request, handled, response) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RestSubmissionClient $outer;
         private final CreateSubmissionRequest request$1;
         private final BooleanRef handled$1;
         private final ObjectRef response$1;

         public final void apply(String m) {
            this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$validateMaster(m);
            URL url = this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$getSubmitUrl(m);

            try {
               this.response$1.elem = (T)this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$postJson(url, this.request$1.toJson());
               SubmitRestProtocolResponse var4 = (SubmitRestProtocolResponse)this.response$1.elem;
               if (var4 instanceof CreateSubmissionResponse) {
                  CreateSubmissionResponse var5 = (CreateSubmissionResponse)var4;
                  if (Predef$.MODULE$.Boolean2boolean(var5.success())) {
                     this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$reportSubmissionStatus(var5);
                     this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$handleRestResponse(var5);
                     this.handled$1.elem = true;
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     BoxedUnit var8 = BoxedUnit.UNIT;
                  }
               } else {
                  this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$handleUnexpectedRestResponse(var4);
                  BoxedUnit var6 = BoxedUnit.UNIT;
               }
            } catch (SubmitRestConnectionException var7) {
               if (this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$handleConnectionException(m)) {
                  throw new SubmitRestConnectionException("Unable to connect to server", var7);
               }
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.request$1 = request$1;
               this.handled$1 = handled$1;
               this.response$1 = response$1;
            }
         }
      });
      return (SubmitRestProtocolResponse)response.elem;
   }

   public SubmitRestProtocolResponse killSubmission(String submissionId) {
      this.logInfo(new Serializable(this, submissionId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RestSubmissionClient $outer;
         private final String submissionId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Submitting a request to kill submission ", " in ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.submissionId$1, this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$master}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.submissionId$1 = submissionId$1;
            }
         }
      });
      BooleanRef handled = BooleanRef.create(false);
      ObjectRef response = ObjectRef.create((Object)null);
      Predef$.MODULE$.refArrayOps((Object[])this.masters()).withFilter(new Serializable(this, handled) {
         public static final long serialVersionUID = 0L;
         private final BooleanRef handled$2;

         public final boolean apply(String m) {
            return !this.handled$2.elem;
         }

         public {
            this.handled$2 = handled$2;
         }
      }).foreach(new Serializable(this, submissionId, handled, response) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RestSubmissionClient $outer;
         private final String submissionId$1;
         private final BooleanRef handled$2;
         private final ObjectRef response$3;

         public final void apply(String m) {
            this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$validateMaster(m);
            URL url = this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$getKillUrl(m, this.submissionId$1);

            try {
               this.response$3.elem = (T)this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$post(url);
               SubmitRestProtocolResponse var4 = (SubmitRestProtocolResponse)this.response$3.elem;
               if (var4 instanceof KillSubmissionResponse) {
                  KillSubmissionResponse var5 = (KillSubmissionResponse)var4;
                  if (Utils$.MODULE$.responseFromBackup(var5.message())) {
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$handleRestResponse(var5);
                     this.handled$2.elem = true;
                     BoxedUnit var8 = BoxedUnit.UNIT;
                  }
               } else {
                  this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$handleUnexpectedRestResponse(var4);
                  BoxedUnit var6 = BoxedUnit.UNIT;
               }
            } catch (SubmitRestConnectionException var7) {
               if (this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$handleConnectionException(m)) {
                  throw new SubmitRestConnectionException("Unable to connect to server", var7);
               }
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.submissionId$1 = submissionId$1;
               this.handled$2 = handled$2;
               this.response$3 = response$3;
            }
         }
      });
      return (SubmitRestProtocolResponse)response.elem;
   }

   public SubmitRestProtocolResponse requestSubmissionStatus(String submissionId, boolean quiet) {
      this.logInfo(new Serializable(this, submissionId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RestSubmissionClient $outer;
         private final String submissionId$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Submitting a request for the status of submission ", " in ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.submissionId$2, this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$master}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.submissionId$2 = submissionId$2;
            }
         }
      });
      BooleanRef handled = BooleanRef.create(false);
      ObjectRef response = ObjectRef.create((Object)null);
      Predef$.MODULE$.refArrayOps((Object[])this.masters()).withFilter(new Serializable(this, handled) {
         public static final long serialVersionUID = 0L;
         private final BooleanRef handled$3;

         public final boolean apply(String m) {
            return !this.handled$3.elem;
         }

         public {
            this.handled$3 = handled$3;
         }
      }).foreach(new Serializable(this, submissionId, quiet, handled, response) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RestSubmissionClient $outer;
         private final String submissionId$2;
         private final boolean quiet$1;
         private final BooleanRef handled$3;
         private final ObjectRef response$4;

         public final void apply(String m) {
            this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$validateMaster(m);
            URL url = this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$getStatusUrl(m, this.submissionId$2);

            try {
               this.response$4.elem = (T)this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$get(url);
               SubmitRestProtocolResponse var4 = (SubmitRestProtocolResponse)this.response$4.elem;
               if (var4 instanceof SubmissionStatusResponse) {
                  SubmissionStatusResponse var5 = (SubmissionStatusResponse)var4;
                  if (Predef$.MODULE$.Boolean2boolean(var5.success())) {
                     if (!this.quiet$1) {
                        this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$handleRestResponse(var5);
                     }

                     this.handled$3.elem = true;
                     BoxedUnit var8 = BoxedUnit.UNIT;
                     return;
                  }
               }

               this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$handleUnexpectedRestResponse(var4);
               BoxedUnit var6 = BoxedUnit.UNIT;
            } catch (SubmitRestConnectionException var7) {
               if (this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$handleConnectionException(m)) {
                  throw new SubmitRestConnectionException("Unable to connect to server", var7);
               }
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.submissionId$2 = submissionId$2;
               this.quiet$1 = quiet$1;
               this.handled$3 = handled$3;
               this.response$4 = response$4;
            }
         }
      });
      return (SubmitRestProtocolResponse)response.elem;
   }

   public boolean requestSubmissionStatus$default$2() {
      return false;
   }

   public CreateSubmissionRequest constructSubmitRequest(String appResource, String mainClass, String[] appArgs, Map<String, String> sparkProperties, Map<String, String> environmentVariables) {
      CreateSubmissionRequest message = new CreateSubmissionRequest();
      message.clientSparkVersion_$eq(package$.MODULE$.SPARK_VERSION());
      message.appResource_$eq(appResource);
      message.mainClass_$eq(mainClass);
      message.appArgs_$eq(appArgs);
      message.sparkProperties_$eq(sparkProperties);
      message.environmentVariables_$eq(environmentVariables);
      message.validate();
      return message;
   }

   public SubmitRestProtocolResponse org$apache$spark$deploy$rest$RestSubmissionClient$$get(URL url) {
      this.logDebug(new Serializable(this, url) {
         public static final long serialVersionUID = 0L;
         private final URL url$3;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sending GET request to server at ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.url$3}));
         }

         public {
            this.url$3 = url$3;
         }
      });
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setRequestMethod("GET");
      return this.readResponse(conn);
   }

   public SubmitRestProtocolResponse org$apache$spark$deploy$rest$RestSubmissionClient$$post(URL url) {
      this.logDebug(new Serializable(this, url) {
         public static final long serialVersionUID = 0L;
         private final URL url$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sending POST request to server at ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.url$2}));
         }

         public {
            this.url$2 = url$2;
         }
      });
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setRequestMethod("POST");
      return this.readResponse(conn);
   }

   public SubmitRestProtocolResponse org$apache$spark$deploy$rest$RestSubmissionClient$$postJson(URL url, String json) {
      this.logDebug(new Serializable(this, url, json) {
         public static final long serialVersionUID = 0L;
         private final URL url$1;
         private final String json$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sending POST request to server at ", ":\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.url$1, this.json$1}));
         }

         public {
            this.url$1 = url$1;
            this.json$1 = json$1;
         }
      });
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("charset", "utf-8");
      conn.setDoOutput(true);

      try {
         DataOutputStream out = new DataOutputStream(conn.getOutputStream());
         Utils$.MODULE$.tryWithSafeFinally(new Serializable(this, json, out) {
            public static final long serialVersionUID = 0L;
            private final String json$1;
            private final DataOutputStream out$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.out$1.write(this.json$1.getBytes(StandardCharsets.UTF_8));
            }

            public {
               this.json$1 = json$1;
               this.out$1 = out$1;
            }
         }, new Serializable(this, out) {
            public static final long serialVersionUID = 0L;
            private final DataOutputStream out$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.out$1.close();
            }

            public {
               this.out$1 = out$1;
            }
         });
      } catch (ConnectException var6) {
         throw new SubmitRestConnectionException("Connect Exception when connect to server", var6);
      }

      return this.readResponse(conn);
   }

   public SubmitRestProtocolResponse readResponse(HttpURLConnection connection) {
      Future responseFuture = Future$.MODULE$.apply(new Serializable(this, connection) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RestSubmissionClient $outer;
         private final HttpURLConnection connection$1;

         public final SubmitRestProtocolResponse apply() {
            InputStream dataStream = this.connection$1.getResponseCode() == 200 ? this.connection$1.getInputStream() : this.connection$1.getErrorStream();
            if (dataStream == null) {
               throw new SubmitRestProtocolException("Server returned empty body", SubmitRestProtocolException$.MODULE$.$lessinit$greater$default$2());
            } else {
               String responseJson = Source$.MODULE$.fromInputStream(dataStream, Codec$.MODULE$.fallbackSystemCodec()).mkString();
               this.$outer.logDebug(new Serializable(this, responseJson) {
                  public static final long serialVersionUID = 0L;
                  private final String responseJson$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Response from the server:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.responseJson$1}));
                  }

                  public {
                     this.responseJson$1 = responseJson$1;
                  }
               });
               SubmitRestProtocolMessage response = SubmitRestProtocolMessage$.MODULE$.fromJson(responseJson);
               response.validate();
               Object var6;
               if (response instanceof ErrorResponse) {
                  ErrorResponse var5 = (ErrorResponse)response;
                  this.$outer.logError(new Serializable(this, var5) {
                     public static final long serialVersionUID = 0L;
                     private final ErrorResponse x2$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Server responded with error:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x2$1.message()}));
                     }

                     public {
                        this.x2$1 = x2$1;
                     }
                  });
                  var6 = var5;
               } else {
                  if (!(response instanceof SubmitRestProtocolResponse)) {
                     throw new SubmitRestProtocolException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Message received from server was not a response:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{response.toJson()})), SubmitRestProtocolException$.MODULE$.$lessinit$greater$default$2());
                  }

                  SubmitRestProtocolResponse var7 = (SubmitRestProtocolResponse)response;
                  var6 = var7;
               }

               return (SubmitRestProtocolResponse)var6;
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.connection$1 = connection$1;
            }
         }
      }, ExecutionContext.Implicits$.MODULE$.global());

      try {
         return (SubmitRestProtocolResponse)Await$.MODULE$.result(responseFuture, (new package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(10))).seconds());
      } catch (Throwable var12) {
         boolean var5;
         if (var12 instanceof FileNotFoundException) {
            var5 = true;
         } else if (var12 instanceof SocketException) {
            var5 = true;
         } else {
            var5 = false;
         }

         if (var5) {
            throw new SubmitRestConnectionException("Unable to connect to server", var12);
         } else {
            boolean var7;
            if (var12 instanceof JsonProcessingException) {
               var7 = true;
            } else if (var12 instanceof SubmitRestProtocolException) {
               var7 = true;
            } else {
               var7 = false;
            }

            if (var7) {
               throw new SubmitRestProtocolException("Malformed response received from server", var12);
            } else if (var12 instanceof TimeoutException) {
               TimeoutException var8 = (TimeoutException)var12;
               throw new SubmitRestConnectionException("No response from server", var8);
            } else {
               Option var9 = NonFatal$.MODULE$.unapply(var12);
               if (var9.isEmpty()) {
                  throw var12;
               } else {
                  Throwable t = (Throwable)var9.get();
                  throw new SparkException("Exception while waiting for response", t);
               }
            }
         }
      }
   }

   public URL org$apache$spark$deploy$rest$RestSubmissionClient$$getSubmitUrl(String master) {
      String baseUrl = this.getBaseUrl(master);
      return new URL((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/create"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{baseUrl})));
   }

   public URL org$apache$spark$deploy$rest$RestSubmissionClient$$getKillUrl(String master, String submissionId) {
      String baseUrl = this.getBaseUrl(master);
      return new URL((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/kill/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{baseUrl, submissionId})));
   }

   public URL org$apache$spark$deploy$rest$RestSubmissionClient$$getStatusUrl(String master, String submissionId) {
      String baseUrl = this.getBaseUrl(master);
      return new URL((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/status/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{baseUrl, submissionId})));
   }

   private String getBaseUrl(String master) {
      ObjectRef masterUrl = ObjectRef.create(master);
      this.supportedMasterPrefixes().foreach(new Serializable(this, master, masterUrl) {
         public static final long serialVersionUID = 0L;
         private final String master$2;
         private final ObjectRef masterUrl$2;

         public final void apply(String prefix) {
            if (this.master$2.startsWith(prefix)) {
               this.masterUrl$2.elem = (T)(new StringOps(Predef$.MODULE$.augmentString(this.master$2))).stripPrefix(prefix);
            }

         }

         public {
            this.master$2 = master$2;
            this.masterUrl$2 = masterUrl$2;
         }
      });
      masterUrl.elem = (T)(new StringOps(Predef$.MODULE$.augmentString((String)masterUrl.elem))).stripSuffix("/");
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"http://", "/", "/submissions"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{(String)masterUrl.elem, RestSubmissionClient$.MODULE$.PROTOCOL_VERSION()}));
   }

   public void org$apache$spark$deploy$rest$RestSubmissionClient$$validateMaster(String master) {
      boolean valid = this.supportedMasterPrefixes().exists(new Serializable(this, master) {
         public static final long serialVersionUID = 0L;
         private final String master$1;

         public final boolean apply(String prefix) {
            return this.master$1.startsWith(prefix);
         }

         public {
            this.master$1 = master$1;
         }
      });
      if (!valid) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)"This REST client only supports master URLs that start with one of the following: ").append((Object)this.supportedMasterPrefixes().mkString(",")).toString());
      }
   }

   public void org$apache$spark$deploy$rest$RestSubmissionClient$$reportSubmissionStatus(CreateSubmissionResponse submitResponse) {
      if (Predef$.MODULE$.Boolean2boolean(submitResponse.success())) {
         String submissionId = submitResponse.submissionId();
         if (submissionId == null) {
            this.logError(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Application successfully submitted, but submission ID was not provided!";
               }
            });
         } else {
            this.logInfo(new Serializable(this, submissionId) {
               public static final long serialVersionUID = 0L;
               private final String submissionId$3;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Submission successfully created as ", ". Polling submission state..."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.submissionId$3}));
               }

               public {
                  this.submissionId$3 = submissionId$3;
               }
            });
            this.pollSubmissionStatus(submissionId);
         }
      } else {
         String failMessage = (String)Option$.MODULE$.apply(submitResponse.message()).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(String x$1) {
               return (new StringBuilder()).append((Object)": ").append((Object)x$1).toString();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "";
            }
         });
         this.logError(new Serializable(this, failMessage) {
            public static final long serialVersionUID = 0L;
            private final String failMessage$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Application submission failed", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.failMessage$1}));
            }

            public {
               this.failMessage$1 = failMessage$1;
            }
         });
      }

   }

   private void pollSubmissionStatus(String submissionId) {
      Object var2 = new Object();

      try {
         RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), RestSubmissionClient$.MODULE$.org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_MAX_TRIES()).foreach$mVc$sp(new Serializable(this, submissionId, var2) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final RestSubmissionClient $outer;
            public final String submissionId$4;
            private final Object nonLocalReturnKey1$1;

            public final void apply(int x$2) {
               this.apply$mcVI$sp(x$2);
            }

            public void apply$mcVI$sp(int x$2) {
               SubmitRestProtocolResponse response = this.$outer.requestSubmissionStatus(this.submissionId$4, true);
               if (!(response instanceof SubmissionStatusResponse)) {
                  throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey1$1, BoxedUnit.UNIT);
               } else {
                  SubmissionStatusResponse statusResponse = (SubmissionStatusResponse)response;
                  if (Predef$.MODULE$.Boolean2boolean(statusResponse.success())) {
                     Option driverState = Option$.MODULE$.apply(statusResponse.driverState());
                     Option workerId = Option$.MODULE$.apply(statusResponse.workerId());
                     Option workerHostPort = Option$.MODULE$.apply(statusResponse.workerHostPort());
                     Option exception = Option$.MODULE$.apply(statusResponse.message());
                     if (driverState instanceof Some) {
                        Some var12 = (Some)driverState;
                        String state = (String)var12.x();
                        this.$outer.logInfo(new Serializable(this, state) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final String state$1;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"State of driver ", " is now ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.submissionId$4, this.state$1}));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.state$1 = state$1;
                              }
                           }
                        });
                        BoxedUnit var14 = BoxedUnit.UNIT;
                     } else {
                        this.$outer.logError(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"State of driver ", " was not found!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.submissionId$4}));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                        BoxedUnit var23 = BoxedUnit.UNIT;
                     }

                     label23: {
                        Tuple2 var15 = new Tuple2(workerId, workerHostPort);
                        if (var15 != null) {
                           Option var16 = (Option)var15._1();
                           Option var17 = (Option)var15._2();
                           if (var16 instanceof Some) {
                              Some var18 = (Some)var16;
                              String id = (String)var18.x();
                              if (var17 instanceof Some) {
                                 Some var20 = (Some)var17;
                                 String hp = (String)var20.x();
                                 this.$outer.logInfo(new Serializable(this, id, hp) {
                                    public static final long serialVersionUID = 0L;
                                    private final String id$1;
                                    private final String hp$1;

                                    public final String apply() {
                                       return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver is running on worker ", " at ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.id$1, this.hp$1}));
                                    }

                                    public {
                                       this.id$1 = id$1;
                                       this.hp$1 = hp$1;
                                    }
                                 });
                                 BoxedUnit var24 = BoxedUnit.UNIT;
                                 break label23;
                              }
                           }
                        }

                        BoxedUnit var22 = BoxedUnit.UNIT;
                     }

                     exception.foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final void apply(String e) {
                           this.$outer.org$apache$spark$deploy$rest$RestSubmissionClient$$anonfun$$$outer().logError(new Serializable(this, e) {
                              public static final long serialVersionUID = 0L;
                              private final String e$1;

                              public final String apply() {
                                 return this.e$1;
                              }

                              public {
                                 this.e$1 = e$1;
                              }
                           });
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey1$1, BoxedUnit.UNIT);
                  } else {
                     Thread.sleep((long)RestSubmissionClient$.MODULE$.org$apache$spark$deploy$rest$RestSubmissionClient$$REPORT_DRIVER_STATUS_INTERVAL());
                  }
               }
            }

            // $FF: synthetic method
            public RestSubmissionClient org$apache$spark$deploy$rest$RestSubmissionClient$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.submissionId$4 = submissionId$4;
                  this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
               }
            }
         });
         this.logError(new Serializable(this, submissionId) {
            public static final long serialVersionUID = 0L;
            private final String submissionId$4;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error: Master did not recognize driver ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.submissionId$4}));
            }

            public {
               this.submissionId$4 = submissionId$4;
            }
         });
      } catch (NonLocalReturnControl var4) {
         if (var4.key() != var2) {
            throw var4;
         }

         var4.value$mcV$sp();
      }

   }

   public void org$apache$spark$deploy$rest$RestSubmissionClient$$handleRestResponse(SubmitRestProtocolResponse response) {
      this.logInfo(new Serializable(this, response) {
         public static final long serialVersionUID = 0L;
         private final SubmitRestProtocolResponse response$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Server responded with ", ":\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.response$2.messageType(), this.response$2.toJson()}));
         }

         public {
            this.response$2 = response$2;
         }
      });
   }

   public void org$apache$spark$deploy$rest$RestSubmissionClient$$handleUnexpectedRestResponse(SubmitRestProtocolResponse unexpected) {
      this.logError(new Serializable(this, unexpected) {
         public static final long serialVersionUID = 0L;
         private final SubmitRestProtocolResponse unexpected$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error: Server responded with message of unexpected type ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.unexpected$1.messageType()}));
         }

         public {
            this.unexpected$1 = unexpected$1;
         }
      });
   }

   public boolean org$apache$spark$deploy$rest$RestSubmissionClient$$handleConnectionException(String masterUrl) {
      if (this.lostMasters().contains(masterUrl)) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         this.logWarning(new Serializable(this, masterUrl) {
            public static final long serialVersionUID = 0L;
            private final String masterUrl$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unable to connect to server ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.masterUrl$1}));
            }

            public {
               this.masterUrl$1 = masterUrl$1;
            }
         });
         this.lostMasters().$plus$eq(masterUrl);
      }

      return this.lostMasters().size() >= this.masters().length;
   }

   public RestSubmissionClient(String master) {
      this.org$apache$spark$deploy$rest$RestSubmissionClient$$master = master;
      Logging$class.$init$(this);
      this.supportedMasterPrefixes = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark://", "mesos://"})));
      this.masters = master.startsWith("spark://") ? Utils$.MODULE$.parseStandaloneMasterUrls(master) : (String[])((Object[])(new String[]{master}));
      this.lostMasters = new HashSet<String>();
   }
}
