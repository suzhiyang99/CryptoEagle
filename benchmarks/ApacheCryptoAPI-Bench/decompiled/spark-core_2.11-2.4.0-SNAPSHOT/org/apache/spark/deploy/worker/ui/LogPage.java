package org.apache.spark.deploy.worker.ui;

import java.io.File;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.apache.spark.deploy.worker.Worker;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.logging.RollingFileAppender$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple3;
import scala.Tuple4;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.xml.Elem;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.UnprefixedAttribute;
import scala.xml.Null.;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Eb!B\u0001\u0003\u0001\tq!a\u0002'pOB\u000bw-\u001a\u0006\u0003\u0007\u0011\t!!^5\u000b\u0005\u00151\u0011AB<pe.,'O\u0003\u0002\b\u0011\u00051A-\u001a9m_fT!!\u0003\u0006\u0002\u000bM\u0004\u0018M]6\u000b\u0005-a\u0011AB1qC\u000eDWMC\u0001\u000e\u0003\ry'oZ\n\u0004\u0001=!\u0002C\u0001\t\u0013\u001b\u0005\t\"BA\u0002\t\u0013\t\u0019\u0012CA\u0005XK\n,\u0016\nU1hKB\u0011Q\u0003G\u0007\u0002-)\u0011q\u0003C\u0001\tS:$XM\u001d8bY&\u0011\u0011D\u0006\u0002\b\u0019><w-\u001b8h\u0011!Y\u0002A!A!\u0002\u0013i\u0012A\u00029be\u0016tGo\u0001\u0001\u0011\u0005yyR\"\u0001\u0002\n\u0005\u0001\u0012!aC,pe.,'oV3c+&CQA\t\u0001\u0005\u0002\r\na\u0001P5oSRtDC\u0001\u0013&!\tq\u0002\u0001C\u0003\u001cC\u0001\u0007Q\u0004C\u0004\u0006\u0001\t\u0007I\u0011B\u0014\u0016\u0003!\u0002\"!\u000b\u0016\u000e\u0003\u0011I!a\u000b\u0003\u0003\r]{'o[3s\u0011\u0019i\u0003\u0001)A\u0005Q\u00059qo\u001c:lKJ\u0004\u0003bB\u0018\u0001\u0005\u0004%I\u0001M\u0001\bo>\u00148\u000eR5s+\u0005\t\u0004C\u0001\u001a8\u001b\u0005\u0019$B\u0001\u001b6\u0003\tIwNC\u00017\u0003\u0011Q\u0017M^1\n\u0005a\u001a$\u0001\u0002$jY\u0016DaA\u000f\u0001!\u0002\u0013\t\u0014\u0001C<pe.$\u0015N\u001d\u0011\t\u000fq\u0002!\u0019!C\u0005{\u0005\t2/\u001e9q_J$X\r\u001a'pORK\b/Z:\u0016\u0003y\u00022a\u0010$I\u001b\u0005\u0001%BA!C\u0003%IW.\\;uC\ndWM\u0003\u0002D\t\u0006Q1m\u001c7mK\u000e$\u0018n\u001c8\u000b\u0003\u0015\u000bQa]2bY\u0006L!a\u0012!\u0003\u0007M+G\u000f\u0005\u0002J\u00196\t!J\u0003\u0002Lk\u0005!A.\u00198h\u0013\ti%J\u0001\u0004TiJLgn\u001a\u0005\u0007\u001f\u0002\u0001\u000b\u0011\u0002 \u0002%M,\b\u000f]8si\u0016$Gj\\4UsB,7\u000f\t\u0005\b#\u0002\u0011\r\u0011\"\u0003S\u00031!WMZ1vYR\u0014\u0015\u0010^3t+\u0005\u0019\u0006C\u0001+V\u001b\u0005!\u0015B\u0001,E\u0005\rIe\u000e\u001e\u0005\u00071\u0002\u0001\u000b\u0011B*\u0002\u001b\u0011,g-Y;mi\nKH/Z:!\u0011\u0015Q\u0006\u0001\"\u0001\\\u0003%\u0011XM\u001c3fe2{w\r\u0006\u0002]EB\u0011Q\f\u0019\b\u0003)zK!a\u0018#\u0002\rA\u0013X\rZ3g\u0013\ti\u0015M\u0003\u0002`\t\")1-\u0017a\u0001I\u00069!/Z9vKN$\bCA3m\u001b\u00051'BA4i\u0003\u0011AG\u000f\u001e9\u000b\u0005%T\u0017aB:feZdW\r\u001e\u0006\u0002W\u0006)!.\u0019<bq&\u0011QN\u001a\u0002\u0013\u0011R$\boU3sm2,GOU3rk\u0016\u001cH\u000fC\u0003p\u0001\u0011\u0005\u0001/\u0001\u0004sK:$WM\u001d\u000b\u0004c\u0006\u001d\u0001c\u0001:{{:\u00111\u000f\u001f\b\u0003i^l\u0011!\u001e\u0006\u0003mr\ta\u0001\u0010:p_Rt\u0014\"A#\n\u0005e$\u0015a\u00029bG.\fw-Z\u0005\u0003wr\u00141aU3r\u0015\tIH\tE\u0002\u007f\u0003\u0007i\u0011a \u0006\u0004\u0003\u0003!\u0015a\u0001=nY&\u0019\u0011QA@\u0003\t9{G-\u001a\u0005\u0006G:\u0004\r\u0001\u001a\u0005\b\u0003\u0017\u0001A\u0011BA\u0007\u0003\u00199W\r\u001e'pORQ\u0011qBA\u000e\u0003?\t\u0019#!\f\u0011\u0015Q\u000b\t\u0002XA\u000b\u0003+\t)\"C\u0002\u0002\u0014\u0011\u0013a\u0001V;qY\u0016$\u0004c\u0001+\u0002\u0018%\u0019\u0011\u0011\u0004#\u0003\t1{gn\u001a\u0005\b\u0003;\tI\u00011\u0001]\u00031awn\u001a#je\u0016\u001cGo\u001c:z\u0011\u001d\t\t#!\u0003A\u0002q\u000bq\u0001\\8h)f\u0004X\r\u0003\u0005\u0002&\u0005%\u0001\u0019AA\u0014\u00031ygMZ:fi>\u0003H/[8o!\u0015!\u0016\u0011FA\u000b\u0013\r\tY\u0003\u0012\u0002\u0007\u001fB$\u0018n\u001c8\t\u000f\u0005=\u0012\u0011\u0002a\u0001'\u0006Q!-\u001f;f\u0019\u0016tw\r\u001e5"
)
public class LogPage extends WebUIPage implements Logging {
   private final Worker org$apache$spark$deploy$worker$ui$LogPage$$worker;
   private final File workDir;
   private final Set<String> supportedLogTypes;
   private final int org$apache$spark$deploy$worker$ui$LogPage$$defaultBytes;
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

   public Worker org$apache$spark$deploy$worker$ui$LogPage$$worker() {
      return this.org$apache$spark$deploy$worker$ui$LogPage$$worker;
   }

   private File workDir() {
      return this.workDir;
   }

   private Set<String> supportedLogTypes() {
      return this.supportedLogTypes;
   }

   public int org$apache$spark$deploy$worker$ui$LogPage$$defaultBytes() {
      return this.org$apache$spark$deploy$worker$ui$LogPage$$defaultBytes;
   }

   public String renderLog(HttpServletRequest request) {
      String logType;
      Option offset;
      int byteLength;
      String logDir;
      label38: {
         Option appId = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("appId")));
         Option executorId = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("executorId")));
         Option driverId = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("driverId")));
         logType = UIUtils$.MODULE$.stripXSS(request.getParameter("logType"));
         offset = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("offset"))).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(String x$1) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toLong();
            }
         });
         byteLength = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("byteLength"))).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(String x$2) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$2))).toInt();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final LogPage $outer;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return this.$outer.org$apache$spark$deploy$worker$ui$LogPage$$defaultBytes();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }));
         Tuple3 var9 = new Tuple3(appId, executorId, driverId);
         if (var9 != null) {
            Option var10 = (Option)var9._1();
            Option var11 = (Option)var9._2();
            Option var12 = (Option)var9._3();
            if (var10 instanceof Some) {
               Some var13 = (Some)var10;
               String a = (String)var13.x();
               if (var11 instanceof Some) {
                  Some var15 = (Some)var11;
                  String e = (String)var15.x();
                  if (None$.MODULE$.equals(var12)) {
                     logDir = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/", "/", "/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.workDir().getPath(), a, e}));
                     break label38;
                  }
               }
            }
         }

         if (var9 == null) {
            throw new Exception("Request must specify either application or driver identifiers");
         }

         Option var18 = (Option)var9._1();
         Option var19 = (Option)var9._2();
         Option var20 = (Option)var9._3();
         if (!None$.MODULE$.equals(var18) || !None$.MODULE$.equals(var19) || !(var20 instanceof Some)) {
            throw new Exception("Request must specify either application or driver identifiers");
         }

         Some var21 = (Some)var20;
         String d = (String)var21.x();
         logDir = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/", "/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.workDir().getPath(), d}));
      }

      Tuple4 var24 = this.getLog(logDir, logType, offset, byteLength);
      if (var24 != null) {
         String logText = (String)var24._1();
         long startByte = BoxesRunTime.unboxToLong(var24._2());
         long endByte = BoxesRunTime.unboxToLong(var24._3());
         long logLength = BoxesRunTime.unboxToLong(var24._4());
         Tuple4 var32 = new Tuple4(logText, BoxesRunTime.boxToLong(startByte), BoxesRunTime.boxToLong(endByte), BoxesRunTime.boxToLong(logLength));
         String logText = (String)var32._1();
         long startByte = BoxesRunTime.unboxToLong(var32._2());
         long endByte = BoxesRunTime.unboxToLong(var32._3());
         long logLength = BoxesRunTime.unboxToLong(var32._4());
         String pre = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"==== Bytes ", "-", " of ", " of ", "", " ====\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(startByte), BoxesRunTime.boxToLong(endByte), BoxesRunTime.boxToLong(logLength), logDir, logType}));
         return (new StringBuilder()).append((Object)pre).append((Object)logText).toString();
      } else {
         throw new MatchError(var24);
      }
   }

   public Seq<Node> render(HttpServletRequest request) {
      String logType;
      Option offset;
      int byteLength;
      Tuple3 var18;
      label42: {
         Option appId = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("appId")));
         Option executorId = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("executorId")));
         Option driverId = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("driverId")));
         logType = UIUtils$.MODULE$.stripXSS(request.getParameter("logType"));
         offset = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("offset"))).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(String x$4) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$4))).toLong();
            }
         });
         byteLength = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("byteLength"))).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(String x$5) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$5))).toInt();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final LogPage $outer;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return this.$outer.org$apache$spark$deploy$worker$ui$LogPage$$defaultBytes();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }));
         Tuple3 var10 = new Tuple3(appId, executorId, driverId);
         if (var10 != null) {
            Option var11 = (Option)var10._1();
            Option var12 = (Option)var10._2();
            Option var13 = (Option)var10._3();
            if (var11 instanceof Some) {
               Some var14 = (Some)var11;
               String a = (String)var14.x();
               if (var12 instanceof Some) {
                  Some var16 = (Some)var12;
                  String e = (String)var16.x();
                  if (None$.MODULE$.equals(var13)) {
                     var18 = new Tuple3((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/", "/", "/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.workDir().getPath(), a, e})), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"appId=", "&executorId=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{a, e})), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{a, e})));
                     break label42;
                  }
               }
            }
         }

         if (var10 == null) {
            throw new Exception("Request must specify either application or driver identifiers");
         }

         Option var19 = (Option)var10._1();
         Option var20 = (Option)var10._2();
         Option var21 = (Option)var10._3();
         if (!None$.MODULE$.equals(var19) || !None$.MODULE$.equals(var20) || !(var21 instanceof Some)) {
            throw new Exception("Request must specify either application or driver identifiers");
         }

         Some var22 = (Some)var21;
         String d = (String)var22.x();
         var18 = new Tuple3((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/", "/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.workDir().getPath(), d})), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"driverId=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{d})), d);
      }

      if (var18 != null) {
         String logDir = (String)var18._1();
         String params = (String)var18._2();
         String pageName = (String)var18._3();
         Tuple3 var27 = new Tuple3(logDir, params, pageName);
         String logDir = (String)var27._1();
         String params = (String)var27._2();
         String pageName = (String)var27._3();
         Tuple4 var32 = this.getLog(logDir, logType, offset, byteLength);
         if (var32 != null) {
            String logText = (String)var32._1();
            long startByte = BoxesRunTime.unboxToLong(var32._2());
            long endByte = BoxesRunTime.unboxToLong(var32._3());
            long logLength = BoxesRunTime.unboxToLong(var32._4());
            Tuple4 var40 = new Tuple4(logText, BoxesRunTime.boxToLong(startByte), BoxesRunTime.boxToLong(endByte), BoxesRunTime.boxToLong(logLength));
            String logText = (String)var40._1();
            long startByte = BoxesRunTime.unboxToLong(var40._2());
            long endByte = BoxesRunTime.unboxToLong(var40._3());
            long logLength = BoxesRunTime.unboxToLong(var40._4());
            Null var10004 = .MODULE$;
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            MetaData $md = .MODULE$;
            MetaData var76 = new UnprefixedAttribute("href", this.org$apache$spark$deploy$worker$ui$LogPage$$worker().activeMasterWebUiUrl(), $md);
            TopScope var10013 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("Back to Master"));
            $buf.$amp$plus(new Elem((String)null, "a", var76, var10013, false, $buf));
            Elem linkToMaster = new Elem((String)null, "p", var10004, var10005, false, $buf);
            long curLogLength = endByte - startByte;
            MetaData $md = .MODULE$;
            MetaData var77 = new UnprefixedAttribute("id", new Text("log-data"), $md);
            var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        Showing "));
            $buf.$amp$plus(BoxesRunTime.boxToLong(curLogLength));
            $buf.$amp$plus(new Text(" Bytes: "));
            $buf.$amp$plus(BoxesRunTime.boxToLong(startByte).toString());
            $buf.$amp$plus(new Text(" - "));
            $buf.$amp$plus(BoxesRunTime.boxToLong(endByte).toString());
            $buf.$amp$plus(new Text(" of "));
            $buf.$amp$plus(BoxesRunTime.boxToLong(logLength));
            $buf.$amp$plus(new Text("\n      "));
            Elem range = new Elem((String)null, "span", var77, var10005, false, $buf);
            MetaData $md = .MODULE$;
            MetaData var78 = new UnprefixedAttribute("class", new Text("log-more-btn btn btn-default"), $md);
            var78 = new UnprefixedAttribute("onclick", "loadMore()", var78);
            var78 = new UnprefixedAttribute("type", new Text("button"), var78);
            var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        Load More\n      "));
            Elem moreButton = new Elem((String)null, "button", var78, var10005, false, $buf);
            MetaData $md = .MODULE$;
            MetaData var81 = new UnprefixedAttribute("class", new Text("log-new-btn btn btn-default"), $md);
            var81 = new UnprefixedAttribute("onclick", "loadNew()", var81);
            var81 = new UnprefixedAttribute("type", new Text("button"), var81);
            var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        Load New\n      "));
            Elem newButton = new Elem((String)null, "button", var81, var10005, false, $buf);
            MetaData $md = .MODULE$;
            MetaData var84 = new UnprefixedAttribute("style", new Text("display: none;"), $md);
            var84 = new UnprefixedAttribute("class", new Text("no-new-alert alert alert-info"), var84);
            var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        End of Log\n      "));
            Elem alert = new Elem((String)null, "div", var84, var10005, false, $buf);
            String logParams = (new StringOps(Predef$.MODULE$.augmentString("?%s&logType=%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{params, logType}));
            String jsOnload = (new StringBuilder()).append((Object)"window.onload = ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"initLogPage('", "', ", ", ", ", ", ", ", ", ", ");"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{logParams, BoxesRunTime.boxToLong(curLogLength), BoxesRunTime.boxToLong(startByte), BoxesRunTime.boxToLong(endByte), BoxesRunTime.boxToLong(logLength), BoxesRunTime.boxToInteger(byteLength)}))).toString();
            var10004 = .MODULE$;
            var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        "));
            $buf.$amp$plus(linkToMaster);
            $buf.$amp$plus(new Text("\n        "));
            $buf.$amp$plus(range);
            $buf.$amp$plus(new Text("\n        "));
            MetaData $md = .MODULE$;
            MetaData var86 = new UnprefixedAttribute("style", new Text("height:80vh; overflow:auto; padding:5px;"), $md);
            var86 = new UnprefixedAttribute("class", new Text("log-content"), var86);
            var10013 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n          "));
            Null var10020 = .MODULE$;
            TopScope var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(moreButton);
            $buf.$amp$plus(new Elem((String)null, "div", var10020, var10021, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            var10020 = .MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(logText);
            $buf.$amp$plus(new Elem((String)null, "pre", var10020, var10021, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(alert);
            $buf.$amp$plus(new Text("\n          "));
            var10020 = .MODULE$;
            var10021 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(newButton);
            $buf.$amp$plus(new Elem((String)null, "div", var10020, var10021, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            $buf.$amp$plus(new Elem((String)null, "div", var86, var10013, false, $buf));
            $buf.$amp$plus(new Text("\n        "));
            Null var10012 = .MODULE$;
            var10013 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(scala.xml.Unparsed..MODULE$.apply(jsOnload));
            $buf.$amp$plus(new Elem((String)null, "script", var10012, var10013, false, $buf));
            $buf.$amp$plus(new Text("\n      "));
            Elem content = new Elem((String)null, "div", var10004, var10005, false, $buf);
            return UIUtils$.MODULE$.basicSparkPage(new Serializable(this, content) {
               public static final long serialVersionUID = 0L;
               private final Elem content$1;

               public final Elem apply() {
                  return this.content$1;
               }

               public {
                  this.content$1 = content$1;
               }
            }, (new StringBuilder()).append((Object)logType).append((Object)" log page for ").append((Object)pageName).toString(), UIUtils$.MODULE$.basicSparkPage$default$3());
         } else {
            throw new MatchError(var32);
         }
      } else {
         throw new MatchError(var18);
      }
   }

   private Tuple4<String, Object, Object, Object> getLog(String logDirectory, String logType, Option<Object> offsetOption, int byteLength) {
      if (this.supportedLogTypes().contains(logType)) {
         URI normalizedUri = (new File(logDirectory)).toURI().normalize();
         File normalizedLogDir = new File(normalizedUri.getPath());
         if (Utils$.MODULE$.isInDirectory(this.workDir(), normalizedLogDir)) {
            Tuple4 var10000;
            try {
               Seq files = RollingFileAppender$.MODULE$.getSortedRolledOverFiles(logDirectory, logType);
               this.logDebug(new Serializable(this, logDirectory, logType, files) {
                  public static final long serialVersionUID = 0L;
                  private final String logDirectory$1;
                  private final String logType$1;
                  private final Seq files$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sorted log files of type ", " in ", ":\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.logType$1, this.logDirectory$1, this.files$1.mkString("\n")}));
                  }

                  public {
                     this.logDirectory$1 = logDirectory$1;
                     this.logType$1 = logType$1;
                     this.files$1 = files$1;
                  }
               });
               Seq fileLengths = (Seq)files.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final LogPage $outer;

                  public final long apply(File x$8) {
                     return Utils$.MODULE$.getFileLength(x$8, this.$outer.org$apache$spark$deploy$worker$ui$LogPage$$worker().conf());
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, Seq$.MODULE$.canBuildFrom());
               long totalLength = BoxesRunTime.unboxToLong(fileLengths.sum(Numeric.LongIsIntegral$.MODULE$));
               long offset = BoxesRunTime.unboxToLong(offsetOption.getOrElse(new Serializable(this, byteLength, totalLength) {
                  public static final long serialVersionUID = 0L;
                  private final int byteLength$1;
                  private final long totalLength$1;

                  public final long apply() {
                     return this.apply$mcJ$sp();
                  }

                  public long apply$mcJ$sp() {
                     return this.totalLength$1 - (long)this.byteLength$1;
                  }

                  public {
                     this.byteLength$1 = byteLength$1;
                     this.totalLength$1 = totalLength$1;
                  }
               }));
               long startIndex = offset < 0L ? 0L : (offset > totalLength ? totalLength : offset);
               long endIndex = package$.MODULE$.min(startIndex + (long)byteLength, totalLength);
               this.logDebug(new Serializable(this, startIndex, endIndex) {
                  public static final long serialVersionUID = 0L;
                  private final long startIndex$1;
                  private final long endIndex$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Getting log from ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.startIndex$1), BoxesRunTime.boxToLong(this.endIndex$1)}));
                  }

                  public {
                     this.startIndex$1 = startIndex$1;
                     this.endIndex$1 = endIndex$1;
                  }
               });
               String logText = Utils$.MODULE$.offsetBytes(files, fileLengths, startIndex, endIndex);
               this.logDebug(new Serializable(this, logText) {
                  public static final long serialVersionUID = 0L;
                  private final String logText$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Got log of length ", " bytes"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.logText$1.length())}));
                  }

                  public {
                     this.logText$1 = logText$1;
                  }
               });
               var10000 = new Tuple4(logText, BoxesRunTime.boxToLong(startIndex), BoxesRunTime.boxToLong(endIndex), BoxesRunTime.boxToLong(totalLength));
            } catch (Exception var19) {
               this.logError(new Serializable(this, logDirectory, logType) {
                  public static final long serialVersionUID = 0L;
                  private final String logDirectory$1;
                  private final String logType$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error getting ", " logs from directory ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.logType$1, this.logDirectory$1}));
                  }

                  public {
                     this.logDirectory$1 = logDirectory$1;
                     this.logType$1 = logType$1;
                  }
               }, var19);
               var10000 = new Tuple4((new StringBuilder()).append((Object)"Error getting logs due to exception: ").append((Object)var19.getMessage()).toString(), BoxesRunTime.boxToLong(0L), BoxesRunTime.boxToLong(0L), BoxesRunTime.boxToLong(0L));
            }

            return var10000;
         } else {
            return new Tuple4<String, Object, Object, Object>((new StringBuilder()).append((Object)"Error: invalid log directory ").append((Object)logDirectory).toString(), BoxesRunTime.boxToLong(0L), BoxesRunTime.boxToLong(0L), BoxesRunTime.boxToLong(0L));
         }
      } else {
         return new Tuple4<String, Object, Object, Object>((new StringBuilder()).append((Object)"Error: Log type must be one of ").append((Object)this.supportedLogTypes().mkString(", ")).toString(), BoxesRunTime.boxToLong(0L), BoxesRunTime.boxToLong(0L), BoxesRunTime.boxToLong(0L));
      }
   }

   public LogPage(WorkerWebUI parent) {
      super("logPage");
      Logging$class.$init$(this);
      this.org$apache$spark$deploy$worker$ui$LogPage$$worker = parent.worker();
      this.workDir = new File(parent.workDir().toURI().normalize().getPath());
      this.supportedLogTypes = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"stderr", "stdout"})));
      this.org$apache$spark$deploy$worker$ui$LogPage$$defaultBytes = 102400;
   }
}
