package org.apache.spark.deploy.rest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import org.apache.spark.package$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.json4s.Diff;
import org.json4s.JsonAST;
import org.json4s.jackson.JsonMethods.;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A4a!\u0001\u0002\u0002\u0002\ta!a\u0003*fgR\u001cVM\u001d<mKRT!a\u0001\u0003\u0002\tI,7\u000f\u001e\u0006\u0003\u000b\u0019\ta\u0001Z3qY>L(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0007\u0001iq\u0003\u0005\u0002\u000f+5\tqB\u0003\u0002\u0011#\u0005!\u0001\u000e\u001e;q\u0015\t\u00112#A\u0004tKJ4H.\u001a;\u000b\u0003Q\tQA[1wCbL!AF\b\u0003\u0017!#H\u000f]*feZdW\r\u001e\t\u00031mi\u0011!\u0007\u0006\u00035\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00039e\u0011q\u0001T8hO&tw\rC\u0003\u001f\u0001\u0011\u0005\u0001%\u0001\u0004=S:LGOP\u0002\u0001)\u0005\t\u0003C\u0001\u0012\u0001\u001b\u0005\u0011\u0001\"\u0002\u0013\u0001\t#)\u0013\u0001D:f]\u0012\u0014Vm\u001d9p]N,Gc\u0001\u0014-cA\u0011qEK\u0007\u0002Q)\t\u0011&A\u0003tG\u0006d\u0017-\u0003\u0002,Q\t!QK\\5u\u0011\u0015i3\u00051\u0001/\u0003=\u0011Xm\u001d9p]N,W*Z:tC\u001e,\u0007C\u0001\u00120\u0013\t\u0001$A\u0001\u000eTk\nl\u0017\u000e\u001e*fgR\u0004&o\u001c;pG>d'+Z:q_:\u001cX\rC\u00033G\u0001\u00071'A\bsKN\u0004xN\\:f'\u0016\u0014h\u000f\\3u!\tqA'\u0003\u00026\u001f\t\u0019\u0002\n\u001e;q'\u0016\u0014h\u000f\\3u%\u0016\u001c\bo\u001c8tK\")q\u0007\u0001C\tq\u0005\tb-\u001b8e+:\\gn\\<o\r&,G\u000eZ:\u0015\u0007e\u001aU\tE\u0002(uqJ!a\u000f\u0015\u0003\u000b\u0005\u0013(/Y=\u0011\u0005u\u0002eBA\u0014?\u0013\ty\u0004&\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u0003\n\u0013aa\u0015;sS:<'BA )\u0011\u0015!e\u00071\u0001=\u0003-\u0011X-];fgRT5o\u001c8\t\u000b\u00193\u0004\u0019A$\u0002\u001dI,\u0017/^3ti6+7o]1hKB\u0011!\u0005S\u0005\u0003\u0013\n\u0011\u0011dU;c[&$(+Z:u!J|Go\\2pY6+7o]1hK\")1\n\u0001C\t\u0019\u0006yam\u001c:nCR,\u0005pY3qi&|g\u000e\u0006\u0002=\u001b\")aJ\u0013a\u0001\u001f\u0006\tQ\r\u0005\u0002Q1:\u0011\u0011K\u0016\b\u0003%Vk\u0011a\u0015\u0006\u0003)~\ta\u0001\u0010:p_Rt\u0014\"A\u0015\n\u0005]C\u0013a\u00029bG.\fw-Z\u0005\u00033j\u0013\u0011\u0002\u00165s_^\f'\r\\3\u000b\u0005]C\u0003\"\u0002/\u0001\t#i\u0016a\u00035b]\u0012dW-\u0012:s_J$\"AX1\u0011\u0005\tz\u0016B\u00011\u0003\u00055)%O]8s%\u0016\u001c\bo\u001c8tK\")!m\u0017a\u0001y\u00059Q.Z:tC\u001e,\u0007\"\u00023\u0001\t#)\u0017!\u00059beN,7+\u001e2nSN\u001c\u0018n\u001c8JIR\u0011a-\u001b\t\u0004O\u001dd\u0014B\u00015)\u0005\u0019y\u0005\u000f^5p]\")!n\u0019a\u0001y\u0005!\u0001/\u0019;i\u0011\u0015a\u0007\u0001\"\u0003n\u0003A1\u0018\r\\5eCR,'+Z:q_:\u001cX\rF\u0002/]>DQ!L6A\u00029BQAM6A\u0002M\u0002"
)
public abstract class RestServlet extends HttpServlet implements Logging {
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

   public void sendResponse(SubmitRestProtocolResponse responseMessage, HttpServletResponse responseServlet) {
      SubmitRestProtocolResponse message = this.validateResponse(responseMessage, responseServlet);
      responseServlet.setContentType("application/json");
      responseServlet.setCharacterEncoding("utf-8");
      responseServlet.getWriter().write(message.toJson());
   }

   public String[] findUnknownFields(String requestJson, SubmitRestProtocolMessage requestMessage) {
      JsonAST.JValue clientSideJson = .MODULE$.parse(org.json4s.package..MODULE$.string2JsonInput(requestJson), .MODULE$.parse$default$2(), .MODULE$.parse$default$3());
      JsonAST.JValue serverSideJson = .MODULE$.parse(org.json4s.package..MODULE$.string2JsonInput(requestMessage.toJson()), .MODULE$.parse$default$2(), .MODULE$.parse$default$3());
      Diff var6 = clientSideJson.diff(serverSideJson);
      if (var6 != null) {
         JsonAST.JValue unknown = var6.deleted();
         String[] var11;
         if (unknown instanceof JsonAST.JObject) {
            JsonAST.JObject var10 = (JsonAST.JObject)unknown;
            var11 = (String[])((TraversableOnce)var10.obj().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(Tuple2<String, JsonAST.JValue> x0$2) {
                  if (x0$2 != null) {
                     String k = (String)x0$2._1();
                     return k;
                  } else {
                     throw new MatchError(x0$2);
                  }
               }
            }, List$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(String.class));
         } else {
            var11 = (String[])Array$.MODULE$.empty(ClassTag$.MODULE$.apply(String.class));
         }

         return var11;
      } else {
         throw new MatchError(var6);
      }
   }

   public String formatException(Throwable e) {
      String stackTraceString = Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])e.getStackTrace()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(StackTraceElement x$3) {
            return (new StringBuilder()).append((Object)"\t").append((Object)x$3).toString();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).mkString("\n");
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{e, stackTraceString}));
   }

   public ErrorResponse handleError(String message) {
      ErrorResponse e = new ErrorResponse();
      e.serverSparkVersion_$eq(package$.MODULE$.SPARK_VERSION());
      e.message_$eq(message);
      return e;
   }

   public Option<String> parseSubmissionId(String path) {
      return (Option<String>)(path != null && !path.isEmpty() ? Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(path))).stripPrefix("/").split("/")).headOption().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$4) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$4))).nonEmpty();
         }
      }) : None$.MODULE$);
   }

   private SubmitRestProtocolResponse validateResponse(SubmitRestProtocolResponse responseMessage, HttpServletResponse responseServlet) {
      Object var10000;
      try {
         responseMessage.validate();
         var10000 = responseMessage;
      } catch (Exception var4) {
         responseServlet.setStatus(500);
         var10000 = this.handleError((new StringBuilder()).append((Object)"Internal server error: ").append((Object)this.formatException(var4)).toString());
      }

      return (SubmitRestProtocolResponse)var10000;
   }

   public RestServlet() {
      Logging$class.$init$(this);
   }
}
