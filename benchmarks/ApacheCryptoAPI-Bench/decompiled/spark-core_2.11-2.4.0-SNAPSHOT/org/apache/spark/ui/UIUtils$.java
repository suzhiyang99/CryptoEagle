package org.apache.spark.ui;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.ui.scope.RDDOperationEdge;
import org.apache.spark.ui.scope.RDDOperationGraph;
import org.apache.spark.ui.scope.RDDOperationGraph$;
import org.apache.spark.ui.scope.RDDOperationNode;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.IterableLike;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.SeqView$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.TraversableViewLike;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.Nothing$;
import scala.runtime.TraitSetter;
import scala.sys.package$;
import scala.util.control.NonFatal$;
import scala.util.matching.Regex;
import scala.xml.Elem;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.NodeSeq;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.UnprefixedAttribute;
import scala.xml.Null.;
import scala.xml.transform.RewriteRule;
import scala.xml.transform.RuleTransformer;

public final class UIUtils$ implements Logging {
   public static final UIUtils$ MODULE$;
   private final String TABLE_CLASS_NOT_STRIPED;
   private final String TABLE_CLASS_STRIPED;
   private final String TABLE_CLASS_STRIPED_SORTABLE;
   private final Regex NEWLINE_AND_SINGLE_QUOTE_REGEX;
   private final ThreadLocal<SimpleDateFormat> dateFormat;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new UIUtils$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   public String TABLE_CLASS_NOT_STRIPED() {
      return this.TABLE_CLASS_NOT_STRIPED;
   }

   public String TABLE_CLASS_STRIPED() {
      return this.TABLE_CLASS_STRIPED;
   }

   public String TABLE_CLASS_STRIPED_SORTABLE() {
      return this.TABLE_CLASS_STRIPED_SORTABLE;
   }

   private Regex NEWLINE_AND_SINGLE_QUOTE_REGEX() {
      return this.NEWLINE_AND_SINGLE_QUOTE_REGEX;
   }

   private ThreadLocal<SimpleDateFormat> dateFormat() {
      return this.dateFormat;
   }

   public String formatDate(Date date) {
      return ((DateFormat)this.dateFormat().get()).format(date);
   }

   public String formatDate(long timestamp) {
      return ((DateFormat)this.dateFormat().get()).format(new Date(timestamp));
   }

   public String formatDuration(long milliseconds) {
      if (milliseconds < 100L) {
         return (new StringOps(Predef$.MODULE$.augmentString("%d ms"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(milliseconds)}));
      } else {
         double seconds = (double)milliseconds / (double)1000;
         if (seconds < (double)1) {
            return (new StringOps(Predef$.MODULE$.augmentString("%.1f s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(seconds)}));
         } else if (seconds < (double)60) {
            return (new StringOps(Predef$.MODULE$.augmentString("%.0f s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(seconds)}));
         } else {
            double minutes = seconds / (double)60;
            if (minutes < (double)10) {
               return (new StringOps(Predef$.MODULE$.augmentString("%.1f min"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(minutes)}));
            } else if (minutes < (double)60) {
               return (new StringOps(Predef$.MODULE$.augmentString("%.0f min"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(minutes)}));
            } else {
               double hours = minutes / (double)60;
               return (new StringOps(Predef$.MODULE$.augmentString("%.1f h"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(hours)}));
            }
         }
      }
   }

   public String formatDurationVerbose(long ms) {
      Object var3 = new Object();

      try {
         try {
            long second = 1000L;
            long minute = 60L * second;
            long hour = 60L * minute;
            long day = 24L * hour;
            long week = 7L * day;
            long year = 365L * day;
            String millisecondsString = ms >= second && ms % second == 0L ? "" : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(ms % second)}));
            String secondString = this.toString$1(ms % minute / second, "second");
            String minuteString = this.toString$1(ms % hour / minute, "minute");
            String hourString = this.toString$1(ms % day / hour, "hour");
            String dayString = this.toString$1(ms % week / day, "day");
            String weekString = this.toString$1(ms % year / week, "week");
            String yearString = this.toString$1(ms / year, "year");
            ((IterableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToLong(second)), millisecondsString), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToLong(minute)), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{secondString, millisecondsString}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToLong(hour)), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{minuteString, secondString}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToLong(day)), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ", " ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{hourString, minuteString, secondString}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToLong(week)), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ", " ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{dayString, hourString, minuteString}))), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToLong(year)), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ", " ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{weekString, dayString, hourString})))}))).foreach(new Serializable(ms, var3) {
               public static final long serialVersionUID = 0L;
               private final long ms$1;
               private final Object nonLocalReturnKey1$1;

               public final void apply(Tuple2<Object, String> x0$1) {
                  if (x0$1 != null) {
                     long durationLimit = x0$1._1$mcJ$sp();
                     String durationString = (String)x0$1._2();
                     if (this.ms$1 < durationLimit) {
                        throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, durationString);
                     } else {
                        BoxedUnit var6 = BoxedUnit.UNIT;
                     }
                  } else {
                     throw new MatchError(x0$1);
                  }
               }

               public {
                  this.ms$1 = ms$1;
                  this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
               }
            });
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ", " ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{yearString, weekString, dayString}));
         } catch (Exception var25) {
            this.logError(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Error converting time to string";
               }
            }, var25);
            return "";
         }
      } catch (NonLocalReturnControl var26) {
         if (var26.key() == var3) {
            return (String)var26.value();
         } else {
            throw var26;
         }
      }
   }

   public String formatNumber(double records) {
      double trillion = 1.0E12;
      double billion = (double)1.0E9F;
      double million = (double)1000000.0F;
      double thousand = (double)1000.0F;
      Tuple2 var12 = records >= (double)2 * trillion ? new Tuple2(BoxesRunTime.boxToDouble(records / trillion), " T") : (records >= (double)2 * billion ? new Tuple2(BoxesRunTime.boxToDouble(records / billion), " B") : (records >= (double)2 * million ? new Tuple2(BoxesRunTime.boxToDouble(records / million), " M") : (records >= (double)2 * thousand ? new Tuple2(BoxesRunTime.boxToDouble(records / thousand), " K") : new Tuple2(BoxesRunTime.boxToDouble(records), ""))));
      if (var12 != null) {
         double value = var12._1$mcD$sp();
         String unit = (String)var12._2();
         Tuple2 var16 = new Tuple2(BoxesRunTime.boxToDouble(value), unit);
         double value = var16._1$mcD$sp();
         String unit = (String)var16._2();
         return unit.isEmpty() ? (new StringOps(Predef$.MODULE$.augmentString("%d"))).formatLocal(Locale.US, Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger((int)value)})) : (new StringOps(Predef$.MODULE$.augmentString("%.1f%s"))).formatLocal(Locale.US, Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(value), unit}));
      } else {
         throw new MatchError(var12);
      }
   }

   public String uiRoot() {
      return (String)package$.MODULE$.props().get("spark.ui.proxyBase").orElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply() {
            return package$.MODULE$.env().get("APPLICATION_WEB_PROXY_BASE");
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
   }

   public String prependBaseUri(String basePath, String resource) {
      return (new StringBuilder()).append((Object)this.uiRoot()).append((Object)basePath).append((Object)resource).toString();
   }

   public String prependBaseUri$default$1() {
      return "";
   }

   public String prependBaseUri$default$2() {
      return "";
   }

   public Seq<Node> commonHeaderNodes() {
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = .MODULE$;
      MetaData var18 = new UnprefixedAttribute("content", new Text("text/html; charset=utf-8"), $md);
      var18 = new UnprefixedAttribute("http-equiv", new Text("Content-type"), var18);
      $buf.$amp$plus(new Elem((String)null, "meta", var18, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var20 = new UnprefixedAttribute("type", new Text("text/css"), $md);
      var20 = new UnprefixedAttribute("href", this.prependBaseUri("/static/bootstrap.min.css", this.prependBaseUri$default$2()), var20);
      var20 = new UnprefixedAttribute("rel", new Text("stylesheet"), var20);
      $buf.$amp$plus(new Elem((String)null, "link", var20, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var23 = new UnprefixedAttribute("type", new Text("text/css"), $md);
      var23 = new UnprefixedAttribute("href", this.prependBaseUri("/static/vis.min.css", this.prependBaseUri$default$2()), var23);
      var23 = new UnprefixedAttribute("rel", new Text("stylesheet"), var23);
      $buf.$amp$plus(new Elem((String)null, "link", var23, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var26 = new UnprefixedAttribute("type", new Text("text/css"), $md);
      var26 = new UnprefixedAttribute("href", this.prependBaseUri("/static/webui.css", this.prependBaseUri$default$2()), var26);
      var26 = new UnprefixedAttribute("rel", new Text("stylesheet"), var26);
      $buf.$amp$plus(new Elem((String)null, "link", var26, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var29 = new UnprefixedAttribute("type", new Text("text/css"), $md);
      var29 = new UnprefixedAttribute("href", this.prependBaseUri("/static/timeline-view.css", this.prependBaseUri$default$2()), var29);
      var29 = new UnprefixedAttribute("rel", new Text("stylesheet"), var29);
      $buf.$amp$plus(new Elem((String)null, "link", var29, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var32 = new UnprefixedAttribute("src", this.prependBaseUri("/static/sorttable.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var32, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var33 = new UnprefixedAttribute("src", this.prependBaseUri("/static/jquery-1.11.1.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var33, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var34 = new UnprefixedAttribute("src", this.prependBaseUri("/static/vis.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var34, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var35 = new UnprefixedAttribute("src", this.prependBaseUri("/static/bootstrap-tooltip.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var35, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var36 = new UnprefixedAttribute("src", this.prependBaseUri("/static/initialize-tooltips.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var36, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var37 = new UnprefixedAttribute("src", this.prependBaseUri("/static/table.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var37, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var38 = new UnprefixedAttribute("src", this.prependBaseUri("/static/additional-metrics.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var38, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var39 = new UnprefixedAttribute("src", this.prependBaseUri("/static/timeline-view.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var39, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var40 = new UnprefixedAttribute("src", this.prependBaseUri("/static/log-view.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var40, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var41 = new UnprefixedAttribute("src", this.prependBaseUri("/static/webui.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var41, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      Null var10005 = .MODULE$;
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("setUIRoot('"));
      $buf.$amp$plus(this.uiRoot());
      $buf.$amp$plus(new Text("')"));
      $buf.$amp$plus(new Elem((String)null, "script", var10005, var10006, false, $buf));
      return $buf;
   }

   public Seq<Node> vizHeaderNodes() {
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = .MODULE$;
      MetaData var7 = new UnprefixedAttribute("type", new Text("text/css"), $md);
      var7 = new UnprefixedAttribute("href", this.prependBaseUri("/static/spark-dag-viz.css", this.prependBaseUri$default$2()), var7);
      var7 = new UnprefixedAttribute("rel", new Text("stylesheet"), var7);
      $buf.$amp$plus(new Elem((String)null, "link", var7, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var10 = new UnprefixedAttribute("src", this.prependBaseUri("/static/d3.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var10, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var11 = new UnprefixedAttribute("src", this.prependBaseUri("/static/dagre-d3.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var11, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var12 = new UnprefixedAttribute("src", this.prependBaseUri("/static/graphlib-dot.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var12, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var13 = new UnprefixedAttribute("src", this.prependBaseUri("/static/spark-dag-viz.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var13, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      return $buf;
   }

   public Seq<Node> dataTablesHeaderNodes() {
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = .MODULE$;
      MetaData var11 = new UnprefixedAttribute("type", new Text("text/css"), $md);
      var11 = new UnprefixedAttribute("href", this.prependBaseUri("/static/jquery.dataTables.1.10.4.min.css", this.prependBaseUri$default$2()), var11);
      var11 = new UnprefixedAttribute("rel", new Text("stylesheet"), var11);
      $buf.$amp$plus(new Elem((String)null, "link", var11, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var14 = new UnprefixedAttribute("type", new Text("text/css"), $md);
      var14 = new UnprefixedAttribute("href", this.prependBaseUri("/static/dataTables.bootstrap.css", this.prependBaseUri$default$2()), var14);
      var14 = new UnprefixedAttribute("rel", new Text("stylesheet"), var14);
      $buf.$amp$plus(new Elem((String)null, "link", var14, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var17 = new UnprefixedAttribute("type", new Text("text/css"), $md);
      var17 = new UnprefixedAttribute("href", this.prependBaseUri("/static/jsonFormatter.min.css", this.prependBaseUri$default$2()), var17);
      var17 = new UnprefixedAttribute("rel", new Text("stylesheet"), var17);
      $buf.$amp$plus(new Elem((String)null, "link", var17, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var20 = new UnprefixedAttribute("src", this.prependBaseUri("/static/jquery.dataTables.1.10.4.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var20, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var21 = new UnprefixedAttribute("src", this.prependBaseUri("/static/jquery.cookies.2.2.0.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var21, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var22 = new UnprefixedAttribute("src", this.prependBaseUri("/static/jquery.blockUI.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var22, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var23 = new UnprefixedAttribute("src", this.prependBaseUri("/static/dataTables.bootstrap.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var23, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var24 = new UnprefixedAttribute("src", this.prependBaseUri("/static/jsonFormatter.min.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var24, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      MetaData $md = .MODULE$;
      MetaData var25 = new UnprefixedAttribute("src", this.prependBaseUri("/static/jquery.mustache.js", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "script", var25, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      return $buf;
   }

   public Seq<Node> headerSparkPage(String title, Function0<Seq<Node>> content, SparkUITab activeTab, Option<Object> refreshInterval, Option<String> helpText, boolean showVisualization, boolean useDataTables) {
      String appName = activeTab.appName();
      String shortAppName = appName.length() < 36 ? appName : (new StringBuilder()).append((Object)((String)(new StringOps(Predef$.MODULE$.augmentString(appName))).take(32))).append((Object)"...").toString();
      Seq header = (Seq)activeTab.headerTabs().map(new Serializable(activeTab) {
         public static final long serialVersionUID = 0L;
         private final SparkUITab activeTab$1;

         public final Elem apply(WebUITab tab) {
            MetaData $md;
            UnprefixedAttribute var10000;
            String var10003;
            label17: {
               label16: {
                  $md = .MODULE$;
                  var10000 = new UnprefixedAttribute;
                  SparkUITab var3 = this.activeTab$1;
                  if (tab == null) {
                     if (var3 == null) {
                        break label16;
                     }
                  } else if (tab.equals(var3)) {
                     break label16;
                  }

                  var10003 = "";
                  break label17;
               }

               var10003 = "active";
            }

            var10000.<init>("class", var10003, $md);
            MetaData var7 = var10000;
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n        "));
            MetaData $md = .MODULE$;
            MetaData var8 = new UnprefixedAttribute("href", UIUtils$.MODULE$.prependBaseUri(this.activeTab$1.basePath(), (new StringBuilder()).append((Object)"/").append((Object)tab.prefix()).append((Object)"/").toString()), $md);
            TopScope var10013 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(tab.name());
            $buf.$amp$plus(new Elem((String)null, "a", var8, var10013, false, $buf));
            $buf.$amp$plus(new Text("\n      "));
            return new Elem((String)null, "li", var7, var10005, false, $buf);
         }

         public {
            this.activeTab$1 = activeTab$1;
         }
      }, Seq$.MODULE$.canBuildFrom());
      Seq helpButton = (Seq)helpText.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Seq<Node> apply(String x$2) {
            return UIUtils$.MODULE$.tooltip(x$2, "bottom");
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      });
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(this.commonHeaderNodes());
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(showVisualization ? this.vizHeaderNodes() : Seq$.MODULE$.empty());
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(useDataTables ? this.dataTablesHeaderNodes() : Seq$.MODULE$.empty());
      $buf.$amp$plus(new Text("\n        "));
      Null var10020 = .MODULE$;
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(appName);
      $buf.$amp$plus(new Text(" - "));
      $buf.$amp$plus(title);
      $buf.$amp$plus(new Elem((String)null, "title", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "head", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var41 = new UnprefixedAttribute("class", new Text("navbar navbar-static-top"), $md);
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var42 = new UnprefixedAttribute("class", new Text("navbar-inner"), $md);
      TopScope var10029 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var43 = new UnprefixedAttribute("class", new Text("brand"), $md);
      TopScope var10037 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      MetaData $md = .MODULE$;
      MetaData var44 = new UnprefixedAttribute("class", new Text("brand"), $md);
      var44 = new UnprefixedAttribute("href", this.prependBaseUri("/", this.prependBaseUri$default$2()), var44);
      TopScope var10045 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n                "));
      MetaData $md = .MODULE$;
      MetaData var46 = new UnprefixedAttribute("src", this.prependBaseUri("/static/spark-logo-77x50px-hd.png", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "img", var46, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n                "));
      MetaData $md = .MODULE$;
      MetaData var47 = new UnprefixedAttribute("class", new Text("version"), $md);
      TopScope var10053 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(activeTab.appSparkVersion());
      $buf.$amp$plus(new Elem((String)null, "span", var47, var10053, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(new Elem((String)null, "a", var44, var10045, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "div", var43, var10037, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var48 = new UnprefixedAttribute("class", new Text("navbar-text pull-right"), $md);
      var10037 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      MetaData $md = .MODULE$;
      MetaData var49 = new UnprefixedAttribute("title", appName, $md);
      var10045 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(shortAppName);
      $buf.$amp$plus(new Elem((String)null, "strong", var49, var10045, false, $buf));
      $buf.$amp$plus(new Text(" application UI\n            "));
      $buf.$amp$plus(new Elem((String)null, "p", var48, var10037, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var50 = new UnprefixedAttribute("class", new Text("nav"), $md);
      var10037 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(header);
      $buf.$amp$plus(new Elem((String)null, "ul", var50, var10037, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "div", var42, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var41, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var51 = new UnprefixedAttribute("class", new Text("container-fluid"), $md);
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var52 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      var10029 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var53 = new UnprefixedAttribute("class", new Text("span12"), $md);
      var10037 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      MetaData $md = .MODULE$;
      MetaData var54 = new UnprefixedAttribute("style", new Text("vertical-align: bottom; display: inline-block;"), $md);
      var10045 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n                "));
      $buf.$amp$plus(title);
      $buf.$amp$plus(new Text("\n                "));
      $buf.$amp$plus(helpButton);
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(new Elem((String)null, "h3", var54, var10045, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "div", var53, var10037, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "div", var52, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(content.apply());
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var51, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "body", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "html", var10004, var10005, false, $buf);
   }

   public Option<Object> headerSparkPage$default$4() {
      return None$.MODULE$;
   }

   public Option<String> headerSparkPage$default$5() {
      return None$.MODULE$;
   }

   public boolean headerSparkPage$default$6() {
      return false;
   }

   public boolean headerSparkPage$default$7() {
      return false;
   }

   public Seq<Node> basicSparkPage(Function0<Seq<Node>> content, String title, boolean useDataTables) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(this.commonHeaderNodes());
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(useDataTables ? this.dataTablesHeaderNodes() : Seq$.MODULE$.empty());
      $buf.$amp$plus(new Text("\n        "));
      Null var10020 = .MODULE$;
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(title);
      $buf.$amp$plus(new Elem((String)null, "title", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "head", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var21 = new UnprefixedAttribute("class", new Text("container-fluid"), $md);
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var22 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      TopScope var10029 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      MetaData $md = .MODULE$;
      MetaData var23 = new UnprefixedAttribute("class", new Text("span12"), $md);
      TopScope var10037 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      MetaData $md = .MODULE$;
      MetaData var24 = new UnprefixedAttribute("style", new Text("vertical-align: middle; display: inline-block;"), $md);
      TopScope var10045 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n                "));
      MetaData $md = .MODULE$;
      MetaData var25 = new UnprefixedAttribute("href", this.prependBaseUri("/", this.prependBaseUri$default$2()), $md);
      var25 = new UnprefixedAttribute("style", new Text("text-decoration: none"), var25);
      TopScope var10053 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n                  "));
      MetaData $md = .MODULE$;
      MetaData var27 = new UnprefixedAttribute("src", this.prependBaseUri("/static/spark-logo-77x50px-hd.png", this.prependBaseUri$default$2()), $md);
      $buf.$amp$plus(new Elem((String)null, "img", var27, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n                  "));
      MetaData $md = .MODULE$;
      MetaData var28 = new UnprefixedAttribute("style", new Text("margin-right: 15px;"), $md);
      var28 = new UnprefixedAttribute("class", new Text("version"), var28);
      TopScope var10061 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(org.apache.spark.package$.MODULE$.SPARK_VERSION());
      $buf.$amp$plus(new Elem((String)null, "span", var28, var10061, false, $buf));
      $buf.$amp$plus(new Text("\n                "));
      $buf.$amp$plus(new Elem((String)null, "a", var25, var10053, false, $buf));
      $buf.$amp$plus(new Text("\n                "));
      $buf.$amp$plus(title);
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(new Elem((String)null, "h3", var24, var10045, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "div", var23, var10037, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "div", var22, var10029, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(content.apply());
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var21, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "body", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "html", var10004, var10005, false, $buf);
   }

   public boolean basicSparkPage$default$3() {
      return false;
   }

   public <T> Seq<Node> listingTable(Seq<String> headers, Function1<T, Seq<Node>> generateDataRow, Iterable<T> data, boolean fixedWidth, Option<String> id, Seq<String> headerClasses, boolean stripeRowsWithCss, boolean sortable) {
      String _tableClass = stripeRowsWithCss ? this.TABLE_CLASS_STRIPED() : this.TABLE_CLASS_NOT_STRIPED();
      String listingTableClass = sortable ? (new StringBuilder()).append((Object)_tableClass).append((Object)" sortable").toString() : _tableClass;
      double colWidth = (double)100 / (double)headers.size();
      String colWidthAttr = fixedWidth ? (new StringBuilder()).append(colWidth).append((Object)"%").toString() : "";
      boolean newlinesInHeader = headers.exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$3) {
            return x$3.contains("\n");
         }
      });
      Seq headerRow = (Seq)((TraversableViewLike)headers.view().zipWithIndex(SeqView$.MODULE$.canBuildFrom())).map(new Serializable(headerClasses, colWidthAttr, newlinesInHeader) {
         public static final long serialVersionUID = 0L;
         private final Seq headerClasses$1;
         private final String colWidthAttr$1;
         private final boolean newlinesInHeader$1;

         public final Elem apply(Tuple2<String, Object> x) {
            MetaData $md = .MODULE$;
            MetaData var4 = new UnprefixedAttribute("class", UIUtils$.MODULE$.org$apache$spark$ui$UIUtils$$getClass$1(x._2$mcI$sp(), this.headerClasses$1), $md);
            var4 = new UnprefixedAttribute("width", this.colWidthAttr$1, var4);
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(UIUtils$.MODULE$.org$apache$spark$ui$UIUtils$$getHeaderContent$1((String)x._1(), this.newlinesInHeader$1));
            return new Elem((String)null, "th", var4, var10005, false, $buf);
         }

         public {
            this.headerClasses$1 = headerClasses$1;
            this.colWidthAttr$1 = colWidthAttr$1;
            this.newlinesInHeader$1 = newlinesInHeader$1;
         }
      }, SeqView$.MODULE$.canBuildFrom());
      MetaData $md = .MODULE$;
      MetaData var20 = new UnprefixedAttribute("id", id.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Text apply(String data) {
            return scala.xml.Text..MODULE$.apply(data);
         }
      }), $md);
      var20 = new UnprefixedAttribute("class", listingTableClass, var20);
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(headerRow);
      $buf.$amp$plus(new Elem((String)null, "thead", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(data.map(new Serializable(generateDataRow) {
         public static final long serialVersionUID = 0L;
         private final Function1 generateDataRow$1;

         public final Seq<Node> apply(T r) {
            return (Seq)this.generateDataRow$1.apply(r);
         }

         public {
            this.generateDataRow$1 = generateDataRow$1;
         }
      }, Iterable$.MODULE$.canBuildFrom()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "tbody", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "table", var20, var10005, false, $buf);
   }

   public <T> boolean listingTable$default$4() {
      return false;
   }

   public <T> Option<String> listingTable$default$5() {
      return None$.MODULE$;
   }

   public <T> Seq<String> listingTable$default$6() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public <T> boolean listingTable$default$7() {
      return true;
   }

   public <T> boolean listingTable$default$8() {
      return true;
   }

   public Seq<Node> makeProgressBar(int started, int completed, int failed, int skipped, Map<String, Object> reasonToNumKilled, int total) {
      String completeWidth = (new StringOps(Predef$.MODULE$.augmentString("width: %s%%"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble((double)completed / (double)total * (double)100)}));
      int boundedStarted = scala.math.package$.MODULE$.min(started, total - completed);
      String startWidth = (new StringOps(Predef$.MODULE$.augmentString("width: %s%%"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble((double)boundedStarted / (double)total * (double)100)}));
      MetaData $md = .MODULE$;
      MetaData var16 = new UnprefixedAttribute("class", new Text("progress"), $md);
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var17 = new UnprefixedAttribute("style", new Text("text-align:center; position:absolute; width:100%; left:0;"), $md);
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(completed));
      $buf.$amp$plus(new Text("/"));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(total));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(failed == 0 && skipped == 0 && started > 0 ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " running)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(started)})) : BoxedUnit.UNIT);
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(failed > 0 ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " failed)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(failed)})) : BoxedUnit.UNIT);
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(skipped > 0 ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " skipped)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(skipped)})) : BoxedUnit.UNIT);
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(((TraversableLike)reasonToNumKilled.toSeq().sortBy(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(Tuple2<String, Object> x$4) {
            return -x$4._2$mcI$sp();
         }
      }, Ordering.Int$.MODULE$)).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<String, Object> x0$3) {
            if (x0$3 != null) {
               String reason = (String)x0$3._1();
               int count = x0$3._2$mcI$sp();
               String var5 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " killed: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(count), reason}));
               return var5;
            } else {
               throw new MatchError(x0$3);
            }
         }
      }, Seq$.MODULE$.canBuildFrom()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "span", var17, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var18 = new UnprefixedAttribute("style", completeWidth, $md);
      var18 = new UnprefixedAttribute("class", new Text("bar bar-completed"), var18);
      $buf.$amp$plus(new Elem((String)null, "div", var18, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var20 = new UnprefixedAttribute("style", startWidth, $md);
      var20 = new UnprefixedAttribute("class", new Text("bar bar-running"), var20);
      $buf.$amp$plus(new Elem((String)null, "div", var20, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "div", var16, var10005, false, $buf);
   }

   public Seq<Node> showDagVizForStage(int stageId, Option<RDDOperationGraph> graph) {
      return this.showDagViz(Option$.MODULE$.option2Iterable(graph).toSeq(), false);
   }

   public Seq<Node> showDagVizForJob(int jobId, Seq<RDDOperationGraph> graphs) {
      return this.showDagViz(graphs, true);
   }

   private Seq<Node> showDagViz(Seq<RDDOperationGraph> graphs, boolean forJob) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var12 = new UnprefixedAttribute("onclick", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"toggleDagViz(", ");"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToBoolean(forJob)})), $md);
      var12 = new UnprefixedAttribute("class", new Text("expand-dag-viz"), var12);
      var12 = new UnprefixedAttribute("id", forJob ? "job-dag-viz" : "stage-dag-viz", var12);
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var15 = new UnprefixedAttribute("class", new Text("expand-dag-viz-arrow arrow-closed"), $md);
      $buf.$amp$plus(new Elem((String)null, "span", var15, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var16 = new UnprefixedAttribute("data-placement", new Text("right"), $md);
      var16 = new UnprefixedAttribute("title", forJob ? ToolTips$.MODULE$.JOB_DAG() : ToolTips$.MODULE$.STAGE_DAG(), var16);
      var16 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var16);
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          DAG Visualization\n        "));
      $buf.$amp$plus(new Elem((String)null, "a", var16, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "span", var12, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var19 = new UnprefixedAttribute("id", new Text("dag-viz-graph"), $md);
      $buf.$amp$plus(new Elem((String)null, "div", var19, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var20 = new UnprefixedAttribute("style", new Text("display:none"), $md);
      var20 = new UnprefixedAttribute("id", new Text("dag-viz-metadata"), var20);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(graphs.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Elem apply(RDDOperationGraph g) {
            String stageId = g.rootCluster().id().replaceAll(RDDOperationGraph$.MODULE$.STAGE_CLUSTER_PREFIX(), "");
            String skipped = BoxesRunTime.boxToBoolean(g.rootCluster().name().contains("skipped")).toString();
            MetaData $md = .MODULE$;
            MetaData var8 = new UnprefixedAttribute("skipped", skipped, $md);
            var8 = new UnprefixedAttribute("stage-id", stageId, var8);
            var8 = new UnprefixedAttribute("class", new Text("stage-metadata"), var8);
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            MetaData $md = .MODULE$;
            MetaData var11 = new UnprefixedAttribute("class", new Text("dot-file"), $md);
            TopScope var10013 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(RDDOperationGraph$.MODULE$.makeDotFile(g));
            $buf.$amp$plus(new Elem((String)null, "div", var11, var10013, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(g.incomingEdges().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Elem apply(RDDOperationEdge e) {
                  MetaData $md = .MODULE$;
                  MetaData var4 = new UnprefixedAttribute("class", new Text("incoming-edge"), $md);
                  TopScope var10005 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(BoxesRunTime.boxToInteger(e.fromId()));
                  $buf.$amp$plus(new Text(","));
                  $buf.$amp$plus(BoxesRunTime.boxToInteger(e.toId()));
                  return new Elem((String)null, "div", var4, var10005, false, $buf);
               }
            }, Seq$.MODULE$.canBuildFrom()));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(g.outgoingEdges().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Elem apply(RDDOperationEdge e) {
                  MetaData $md = .MODULE$;
                  MetaData var4 = new UnprefixedAttribute("class", new Text("outgoing-edge"), $md);
                  TopScope var10005 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(BoxesRunTime.boxToInteger(e.fromId()));
                  $buf.$amp$plus(new Text(","));
                  $buf.$amp$plus(BoxesRunTime.boxToInteger(e.toId()));
                  return new Elem((String)null, "div", var4, var10005, false, $buf);
               }
            }, Seq$.MODULE$.canBuildFrom()));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(g.rootCluster().getCachedNodes().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Elem apply(RDDOperationNode n) {
                  MetaData $md = .MODULE$;
                  MetaData var4 = new UnprefixedAttribute("class", new Text("cached-rdd"), $md);
                  TopScope var10005 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(BoxesRunTime.boxToInteger(n.id()));
                  return new Elem((String)null, "div", var4, var10005, false, $buf);
               }
            }, Seq$.MODULE$.canBuildFrom()));
            $buf.$amp$plus(new Text("\n            "));
            return new Elem((String)null, "div", var8, var10005, false, $buf);
         }
      }, Seq$.MODULE$.canBuildFrom()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "div", var20, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "div", var10004, var10005, false, $buf);
   }

   public Seq<Node> tooltip(String text, String position) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      ("));
      MetaData $md = .MODULE$;
      MetaData var6 = new UnprefixedAttribute("title", text, $md);
      var6 = new UnprefixedAttribute("data-placement", position, var6);
      var6 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var6);
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("?"));
      $buf.$amp$plus(new Elem((String)null, "a", var6, var10013, false, $buf));
      $buf.$amp$plus(new Text(")\n    "));
      return new Elem((String)null, "sup", var10004, var10005, false, $buf);
   }

   public NodeSeq makeDescription(String desc, String basePathUri, boolean plainText) {
      Object var18;
      try {
         Elem xml = (Elem)scala.xml.XML..MODULE$.loadString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"<span class=\"description-input\">", "</span>"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{desc})));
         Set allowedNodeLabels = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"a", "span", "br"})));
         NodeSeq illegalNodes = (NodeSeq)xml.$bslash$bslash("_").filterNot(new Serializable(allowedNodeLabels) {
            public static final long serialVersionUID = 0L;
            private final Set allowedNodeLabels$1;

            public final boolean apply(Node x0$4) {
               if (x0$4 != null) {
                  boolean var4 = this.allowedNodeLabels$1.contains(x0$4.label());
                  return var4;
               } else {
                  throw new MatchError(x0$4);
               }
            }

            public {
               this.allowedNodeLabels$1 = allowedNodeLabels$1;
            }
         });
         if (illegalNodes.nonEmpty()) {
            throw new IllegalArgumentException((new StringBuilder()).append((Object)"Only HTML anchors allowed in job descriptions\n").append((Object)((TraversableOnce)illegalNodes.map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply(Node n) {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " in ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{n.label(), n}));
               }
            }, scala.collection.immutable.Seq$.MODULE$.canBuildFrom())).mkString("\n\t")).toString());
         }

         scala.collection.immutable.Seq allLinks = (scala.collection.immutable.Seq)((TraversableLike)((TraversableLike)xml.$bslash$bslash("a").flatMap(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final MetaData apply(Node x$5) {
               return x$5.attributes();
            }
         }, scala.collection.immutable.Seq$.MODULE$.canBuildFrom())).filter(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final boolean apply(MetaData x$6) {
               boolean var3;
               label23: {
                  String var10000 = x$6.key();
                  String var2 = "href";
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
         })).map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply(MetaData x$7) {
               return x$7.value().toString();
            }
         }, scala.collection.immutable.Seq$.MODULE$.canBuildFrom());
         if (allLinks.exists(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final boolean apply(String x$8) {
               return !x$8.startsWith("/");
            }
         })) {
            throw new IllegalArgumentException((new StringBuilder()).append((Object)"Links in job descriptions must be root-relative:\n").append((Object)allLinks.mkString("\n\t")).toString());
         }

         RewriteRule rule = plainText ? new RewriteRule() {
            public Seq<Node> transform(Node n) {
               boolean var2 = false;
               Elem var3 = null;
               Object var5;
               if (n instanceof Elem) {
                  var2 = true;
                  var3 = (Elem)n;
                  if (var3.child().isEmpty()) {
                     var5 = scala.xml.Text..MODULE$.apply(var3.text());
                     return (Seq<Node>)var5;
                  }
               }

               if (var2 && var3.child().nonEmpty()) {
                  var5 = scala.xml.Text..MODULE$.apply(scala.xml.NodeSeq..MODULE$.seqToNodeSeq((Seq)var3.child().flatMap(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final Seq<Node> apply(Node n) {
                        return this.$outer.transform(n);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  }, Seq$.MODULE$.canBuildFrom())).text());
               } else {
                  var5 = n;
               }

               return (Seq<Node>)var5;
            }
         } : new RewriteRule(basePathUri) {
            private final String basePathUri$1;

            public Seq<Node> transform(Node n) {
               Object var4;
               if (n instanceof Elem) {
                  Elem var3 = (Elem)n;
                  if (var3.$bslash("@href").nonEmpty()) {
                     String relativePath = ((SeqLike)var3.attribute("href").get()).toString();
                     String fullUri = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{(new StringOps(Predef$.MODULE$.augmentString(this.basePathUri$1))).stripSuffix("/"), (new StringOps(Predef$.MODULE$.augmentString(relativePath))).stripPrefix("/")}));
                     var4 = var3.$percent((MetaData)scala.xml.Attribute..MODULE$.apply((String)null, "href", fullUri, .MODULE$));
                     return (Seq<Node>)var4;
                  }
               }

               var4 = n;
               return (Seq<Node>)var4;
            }

            public {
               this.basePathUri$1 = basePathUri$1;
            }
         };
         var18 = scala.xml.NodeSeq..MODULE$.seqToNodeSeq((new RuleTransformer(Predef$.MODULE$.wrapRefArray((Object[])(new RewriteRule[]{rule})))).transform(xml));
      } catch (Throwable var16) {
         Option var6 = NonFatal$.MODULE$.unapply(var16);
         if (var6.isEmpty()) {
            throw var16;
         }

         if (plainText) {
            var18 = scala.xml.Text..MODULE$.apply(desc);
         } else {
            MetaData $md = .MODULE$;
            MetaData var17 = new UnprefixedAttribute("class", new Text("description-input"), $md);
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(desc);
            var18 = new Elem((String)null, "span", var17, var10005, false, $buf);
         }

         Object var7 = var18;
         var18 = var7;
         return (NodeSeq)var18;
      }

      return (NodeSeq)var18;
   }

   public boolean makeDescription$default$3() {
      return false;
   }

   public String decodeURLParameter(String urlParam) {
      String param = urlParam;
      String decodedParam = URLDecoder.decode(urlParam, "UTF-8");

      while(true) {
         if (param == null) {
            if (decodedParam == null) {
               break;
            }
         } else if (param.equals(decodedParam)) {
            break;
         }

         param = decodedParam;
         decodedParam = URLDecoder.decode(decodedParam, "UTF-8");
      }

      return param;
   }

   public int getTimeZoneOffset() {
      return TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000 / 60;
   }

   public String makeHref(boolean proxy, String id, String origHref) {
      return proxy ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/proxy/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{id})) : origHref;
   }

   public String stripXSS(String requestParameter) {
      return requestParameter == null ? null : StringEscapeUtils.escapeHtml4(this.NEWLINE_AND_SINGLE_QUOTE_REGEX().replaceAllIn(requestParameter, ""));
   }

   private final String toString$1(long num, String unit) {
      return num == 0L ? "" : (num == 1L ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(num), unit})) : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ", "s"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(num), unit})));
   }

   public final String org$apache$spark$ui$UIUtils$$getClass$1(int index, Seq headerClasses$1) {
      return index < headerClasses$1.size() ? (String)headerClasses$1.apply(index) : "";
   }

   public final Seq org$apache$spark$ui$UIUtils$$getHeaderContent$1(String header, boolean newlinesInHeader$1) {
      Object var10000;
      if (newlinesInHeader$1) {
         MetaData $md = .MODULE$;
         MetaData var5 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(Predef$.MODULE$.refArrayOps((Object[])header.split("\n")).map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final Elem apply(String x0$2) {
               Null var10004 = .MODULE$;
               TopScope var10005 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text(" "));
               $buf.$amp$plus(x0$2);
               $buf.$amp$plus(new Text(" "));
               Elem var3 = new Elem((String)null, "li", var10004, var10005, false, $buf);
               return var3;
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Elem.class))));
         $buf.$amp$plus(new Text("\n        "));
         var10000 = new Elem((String)null, "ul", var5, var10005, false, $buf);
      } else {
         var10000 = scala.xml.Text..MODULE$.apply(header);
      }

      return (Seq)var10000;
   }

   private UIUtils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.TABLE_CLASS_NOT_STRIPED = "table table-bordered table-condensed";
      this.TABLE_CLASS_STRIPED = (new StringBuilder()).append((Object)this.TABLE_CLASS_NOT_STRIPED()).append((Object)" table-striped").toString();
      this.TABLE_CLASS_STRIPED_SORTABLE = (new StringBuilder()).append((Object)this.TABLE_CLASS_STRIPED()).append((Object)" sortable").toString();
      this.NEWLINE_AND_SINGLE_QUOTE_REGEX = (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(?i)(\\r\\n|\\n|\\r|%0D%0A|%0A|%0D|'|%27)"})))).raw(Nil$.MODULE$)))).r();
      this.dateFormat = new ThreadLocal<SimpleDateFormat>() {
         public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
         }
      };
   }
}
