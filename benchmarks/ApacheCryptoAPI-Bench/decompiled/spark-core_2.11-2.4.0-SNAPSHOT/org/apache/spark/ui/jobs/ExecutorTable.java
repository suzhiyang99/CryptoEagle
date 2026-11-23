package org.apache.spark.ui.jobs;

import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.ExecutorStageSummary;
import org.apache.spark.status.api.v1.ExecutorSummary;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.ui.ToolTips$;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.util.Utils$;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.Iterable$;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.math.Ordering;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
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
   bytes = "\u0006\u0001\u00193Q!\u0001\u0002\u0001\t1\u0011Q\"\u0012=fGV$xN\u001d+bE2,'BA\u0002\u0005\u0003\u0011QwNY:\u000b\u0005\u00151\u0011AA;j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fM\"AA\u0003\u0001B\u0001B\u0003%a#A\u0003ti\u0006<Wm\u0001\u0001\u0011\u0005]qR\"\u0001\r\u000b\u0005eQ\u0012A\u0001<2\u0015\tYB$A\u0002ba&T!!\b\u0004\u0002\rM$\u0018\r^;t\u0013\ty\u0002DA\u0005Ti\u0006<W\rR1uC\"A\u0011\u0005\u0001B\u0001B\u0003%!%A\u0003ti>\u0014X\r\u0005\u0002$I5\tA$\u0003\u0002&9\tq\u0011\t\u001d9Ti\u0006$Xo]*u_J,\u0007\"B\u0014\u0001\t\u0003A\u0013A\u0002\u001fj]&$h\bF\u0002*W1\u0002\"A\u000b\u0001\u000e\u0003\tAQ\u0001\u0006\u0014A\u0002YAQ!\t\u0014A\u0002\tBQA\f\u0001\u0005\u0002=\n\u0011\u0002^8O_\u0012,7+Z9\u0016\u0003A\u00022!M\u001d=\u001d\t\u0011tG\u0004\u00024m5\tAG\u0003\u00026+\u00051AH]8pizJ\u0011\u0001E\u0005\u0003q=\tq\u0001]1dW\u0006<W-\u0003\u0002;w\t\u00191+Z9\u000b\u0005az\u0001CA\u001fA\u001b\u0005q$BA \u0010\u0003\rAX\u000e\\\u0005\u0003\u0003z\u0012AAT8eK\")1\t\u0001C\u0005\t\u0006\u00192M]3bi\u0016,\u00050Z2vi>\u0014H+\u00192mKR\u0011\u0001'\u0012\u0005\u0006)\t\u0003\rA\u0006"
)
public class ExecutorTable {
   private final StageData stage;
   public final AppStatusStore org$apache$spark$ui$jobs$ExecutorTable$$store;

   public Seq<Node> toNodeSeq() {
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = .MODULE$;
      MetaData var33 = new UnprefixedAttribute("class", UIUtils$.MODULE$.TABLE_CLASS_STRIPED_SORTABLE(), $md);
      Elem var10001 = new Elem;
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Elem var10009 = new Elem;
      Null var10013 = .MODULE$;
      TopScope var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var34 = new UnprefixedAttribute("id", new Text("executorid"), $md);
      TopScope var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Executor ID"));
      $buf.$amp$plus(new Elem((String)null, "th", var34, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      Null var10021 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Address"));
      $buf.$amp$plus(new Elem((String)null, "th", var10021, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10021 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Task Time"));
      $buf.$amp$plus(new Elem((String)null, "th", var10021, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10021 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Total Tasks"));
      $buf.$amp$plus(new Elem((String)null, "th", var10021, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10021 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Failed Tasks"));
      $buf.$amp$plus(new Elem((String)null, "th", var10021, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10021 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Killed Tasks"));
      $buf.$amp$plus(new Elem((String)null, "th", var10021, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10021 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Succeeded Tasks"));
      $buf.$amp$plus(new Elem((String)null, "th", var10021, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      Object var10017;
      if (ApiHelper$.MODULE$.hasInput(this.stage)) {
         var10021 = .MODULE$;
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var35 = new UnprefixedAttribute("title", ToolTips$.MODULE$.INPUT(), $md);
         var35 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var35);
         TopScope var10030 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Input Size / Records"));
         $buf.$amp$plus(new Elem((String)null, "span", var35, var10030, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         var10017 = new Elem((String)null, "th", var10021, var10022, false, $buf);
      } else {
         var10017 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10017);
      $buf.$amp$plus(new Text("\n        "));
      if (ApiHelper$.MODULE$.hasOutput(this.stage)) {
         var10021 = .MODULE$;
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var37 = new UnprefixedAttribute("title", ToolTips$.MODULE$.OUTPUT(), $md);
         var37 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var37);
         TopScope var76 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Output Size / Records"));
         $buf.$amp$plus(new Elem((String)null, "span", var37, var76, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         var10017 = new Elem((String)null, "th", var10021, var10022, false, $buf);
      } else {
         var10017 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10017);
      $buf.$amp$plus(new Text("\n        "));
      if (ApiHelper$.MODULE$.hasShuffleRead(this.stage)) {
         var10021 = .MODULE$;
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var39 = new UnprefixedAttribute("title", ToolTips$.MODULE$.SHUFFLE_READ(), $md);
         var39 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var39);
         TopScope var77 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            Shuffle Read Size / Records"));
         $buf.$amp$plus(new Elem((String)null, "span", var39, var77, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         var10017 = new Elem((String)null, "th", var10021, var10022, false, $buf);
      } else {
         var10017 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10017);
      $buf.$amp$plus(new Text("\n        "));
      if (ApiHelper$.MODULE$.hasShuffleWrite(this.stage)) {
         var10021 = .MODULE$;
         var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var41 = new UnprefixedAttribute("title", ToolTips$.MODULE$.SHUFFLE_WRITE(), $md);
         var41 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var41);
         TopScope var78 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            Shuffle Write Size / Records"));
         $buf.$amp$plus(new Elem((String)null, "span", var41, var78, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         var10017 = new Elem((String)null, "th", var10021, var10022, false, $buf);
      } else {
         var10017 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10017);
      $buf.$amp$plus(new Text("\n        "));
      if (ApiHelper$.MODULE$.hasBytesSpilled(this.stage)) {
         NodeBuffer $buf = new NodeBuffer();
         Null var72 = .MODULE$;
         TopScope var10023 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Shuffle Spill (Memory)"));
         $buf.$amp$plus(new Elem((String)null, "th", var72, var10023, false, $buf));
         var72 = .MODULE$;
         var10023 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Shuffle Spill (Disk)"));
         $buf.$amp$plus(new Elem((String)null, "th", var72, var10023, false, $buf));
         var10017 = $buf;
      } else {
         var10017 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10017);
      $buf.$amp$plus(new Text("\n        "));
      var10021 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var43 = new UnprefixedAttribute("title", ToolTips$.MODULE$.BLACKLISTED(), $md);
      var43 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var43);
      TopScope var79 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          Blacklisted\n          "));
      $buf.$amp$plus(new Elem((String)null, "span", var43, var79, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "th", var10021, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10009.<init>((String)null, "thead", var10013, var10014, false, $buf);
      $buf.$amp$plus(var10009);
      $buf.$amp$plus(new Text("\n      "));
      var10013 = .MODULE$;
      var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(this.createExecutorTable(this.stage));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "tbody", var10013, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      var10001.<init>((String)null, "table", var33, var10006, false, $buf);
      $buf.$amp$plus(var10001);
      Null var10005 = .MODULE$;
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(scala.xml.Unparsed..MODULE$.apply((new StringOps(Predef$.MODULE$.augmentString("\n          |      window.onload = function() {\n          |        sorttable.innerSortFunction.apply(document.getElementById('executorid'), [])\n          |      };\n        "))).stripMargin()));
      $buf.$amp$plus(new Text("\n    "));
      $buf.$amp$plus(new Elem((String)null, "script", var10005, var10006, false, $buf));
      return $buf;
   }

   private Seq<Node> createExecutorTable(StageData stage) {
      Map executorSummary = this.org$apache$spark$ui$jobs$ExecutorTable$$store.executorSummary(stage.stageId(), stage.attemptId());
      return (Seq)((TraversableLike)executorSummary.toSeq().sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<String, ExecutorStageSummary> x$1) {
            return (String)x$1._1();
         }
      }, Ordering.String$.MODULE$)).map(new Serializable(this, stage) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ExecutorTable $outer;
         private final StageData stage$1;

         public final Elem apply(Tuple2<String, ExecutorStageSummary> x0$1) {
            if (x0$1 != null) {
               String k = (String)x0$1._1();
               ExecutorStageSummary v = (ExecutorStageSummary)x0$1._2();
               Option executor = this.$outer.org$apache$spark$ui$jobs$ExecutorTable$$store.asOption(new Serializable(this, k) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final String k$1;

                  public final ExecutorSummary apply() {
                     return this.$outer.org$apache$spark$ui$jobs$ExecutorTable$$anonfun$$$outer().org$apache$spark$ui$jobs$ExecutorTable$$store.executorSummary(this.k$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.k$1 = k$1;
                     }
                  }
               });
               Elem var10000 = new Elem;
               Null var10004 = .MODULE$;
               TopScope var10005 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n        "));
               Null var10012 = .MODULE$;
               TopScope var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n          "));
               MetaData $md = .MODULE$;
               MetaData var36 = new UnprefixedAttribute("style", new Text("float: left"), $md);
               TopScope var10021 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(k);
               $buf.$amp$plus(new Elem((String)null, "div", var36, var10021, false, $buf));
               $buf.$amp$plus(new Text("\n          "));
               MetaData $md = .MODULE$;
               MetaData var37 = new UnprefixedAttribute("style", new Text("float: right"), $md);
               var10021 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n          "));
               $buf.$amp$plus(((TraversableLike)executor.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Map<String, String> apply(ExecutorSummary x$2) {
                     return x$2.executorLogs();
                  }
               }).getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Map<String, Nothing$> apply() {
                     return Predef$.MODULE$.Map().empty();
                  }
               })).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Elem apply(Tuple2<String, String> x0$2) {
                     if (x0$2 != null) {
                        String logName = (String)x0$2._1();
                        String logUrl = (String)x0$2._2();
                        Null var10004 = .MODULE$;
                        TopScope var10005 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        MetaData $md = .MODULE$;
                        MetaData var9 = new UnprefixedAttribute("href", logUrl, $md);
                        TopScope var10013 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(logName);
                        $buf.$amp$plus(new Elem((String)null, "a", var9, var10013, false, $buf));
                        Elem var5 = new Elem((String)null, "div", var10004, var10005, false, $buf);
                        return var5;
                     } else {
                        throw new MatchError(x0$2);
                     }
                  }
               }, Iterable$.MODULE$.canBuildFrom()));
               $buf.$amp$plus(new Text("\n          "));
               $buf.$amp$plus(new Elem((String)null, "div", var37, var10021, false, $buf));
               $buf.$amp$plus(new Text("\n        "));
               $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
               $buf.$amp$plus(new Text("\n        "));
               var10012 = .MODULE$;
               var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(executor.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply(ExecutorSummary e) {
                     return e.hostPort();
                  }
               }).getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "CANNOT FIND ADDRESS";
                  }
               }));
               $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
               $buf.$amp$plus(new Text("\n        "));
               MetaData $md = .MODULE$;
               MetaData var38 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(v.taskTime()).toString(), $md);
               var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(UIUtils$.MODULE$.formatDuration(v.taskTime()));
               $buf.$amp$plus(new Elem((String)null, "td", var38, var10013, false, $buf));
               $buf.$amp$plus(new Text("\n        "));
               var10012 = .MODULE$;
               var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(BoxesRunTime.boxToInteger(v.failedTasks() + v.succeededTasks() + v.killedTasks()));
               $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
               $buf.$amp$plus(new Text("\n        "));
               var10012 = .MODULE$;
               var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(BoxesRunTime.boxToInteger(v.failedTasks()));
               $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
               $buf.$amp$plus(new Text("\n        "));
               var10012 = .MODULE$;
               var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(BoxesRunTime.boxToInteger(v.killedTasks()));
               $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
               $buf.$amp$plus(new Text("\n        "));
               var10012 = .MODULE$;
               var10013 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(BoxesRunTime.boxToInteger(v.succeededTasks()));
               $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
               $buf.$amp$plus(new Text("\n        "));
               Object var10008;
               if (ApiHelper$.MODULE$.hasInput(this.stage$1)) {
                  MetaData $md = .MODULE$;
                  MetaData var39 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(v.inputBytes()).toString(), $md);
                  var10013 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n            "));
                  $buf.$amp$plus((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(v.inputBytes()), BoxesRunTime.boxToLong(v.inputRecords())})));
                  $buf.$amp$plus(new Text("\n          "));
                  var10008 = new Elem((String)null, "td", var39, var10013, false, $buf);
               } else {
                  var10008 = BoxedUnit.UNIT;
               }

               $buf.$amp$plus(var10008);
               $buf.$amp$plus(new Text("\n        "));
               if (ApiHelper$.MODULE$.hasOutput(this.stage$1)) {
                  MetaData $md = .MODULE$;
                  MetaData var40 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(v.outputBytes()).toString(), $md);
                  var10013 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n            "));
                  $buf.$amp$plus((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(v.outputBytes()), BoxesRunTime.boxToLong(v.outputRecords())})));
                  $buf.$amp$plus(new Text("\n          "));
                  var10008 = new Elem((String)null, "td", var40, var10013, false, $buf);
               } else {
                  var10008 = BoxedUnit.UNIT;
               }

               $buf.$amp$plus(var10008);
               $buf.$amp$plus(new Text("\n        "));
               if (ApiHelper$.MODULE$.hasShuffleRead(this.stage$1)) {
                  MetaData $md = .MODULE$;
                  MetaData var41 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(v.shuffleRead()).toString(), $md);
                  var10013 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n            "));
                  $buf.$amp$plus((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(v.shuffleRead()), BoxesRunTime.boxToLong(v.shuffleReadRecords())})));
                  $buf.$amp$plus(new Text("\n          "));
                  var10008 = new Elem((String)null, "td", var41, var10013, false, $buf);
               } else {
                  var10008 = BoxedUnit.UNIT;
               }

               $buf.$amp$plus(var10008);
               $buf.$amp$plus(new Text("\n        "));
               if (ApiHelper$.MODULE$.hasShuffleWrite(this.stage$1)) {
                  MetaData $md = .MODULE$;
                  MetaData var42 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(v.shuffleWrite()).toString(), $md);
                  var10013 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n            "));
                  $buf.$amp$plus((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " / ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(v.shuffleWrite()), BoxesRunTime.boxToLong(v.shuffleWriteRecords())})));
                  $buf.$amp$plus(new Text("\n          "));
                  var10008 = new Elem((String)null, "td", var42, var10013, false, $buf);
               } else {
                  var10008 = BoxedUnit.UNIT;
               }

               $buf.$amp$plus(var10008);
               $buf.$amp$plus(new Text("\n        "));
               if (ApiHelper$.MODULE$.hasBytesSpilled(this.stage$1)) {
                  NodeBuffer $buf = new NodeBuffer();
                  MetaData $md = .MODULE$;
                  MetaData var43 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(v.memoryBytesSpilled()).toString(), $md);
                  TopScope var10014 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n            "));
                  $buf.$amp$plus(Utils$.MODULE$.bytesToString(v.memoryBytesSpilled()));
                  $buf.$amp$plus(new Text("\n          "));
                  $buf.$amp$plus(new Elem((String)null, "td", var43, var10014, false, $buf));
                  MetaData $md = .MODULE$;
                  MetaData var44 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(v.diskBytesSpilled()).toString(), $md);
                  var10014 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n            "));
                  $buf.$amp$plus(Utils$.MODULE$.bytesToString(v.diskBytesSpilled()));
                  $buf.$amp$plus(new Text("\n          "));
                  $buf.$amp$plus(new Elem((String)null, "td", var44, var10014, false, $buf));
                  var10008 = $buf;
               } else {
                  var10008 = BoxedUnit.UNIT;
               }

               $buf.$amp$plus(var10008);
               $buf.$amp$plus(new Text("\n        "));
               Elem var49;
               if (BoxesRunTime.unboxToBoolean(executor.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final boolean apply(ExecutorSummary x$3) {
                     return x$3.isBlacklisted();
                  }
               }).getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final boolean apply() {
                     return this.apply$mcZ$sp();
                  }

                  public boolean apply$mcZ$sp() {
                     return false;
                  }
               }))) {
                  var10012 = .MODULE$;
                  var10013 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("for application"));
                  var49 = new Elem((String)null, "td", var10012, var10013, false, $buf);
               } else if (v.isBlacklistedForStage()) {
                  var10012 = .MODULE$;
                  var10013 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("for stage"));
                  var49 = new Elem((String)null, "td", var10012, var10013, false, $buf);
               } else {
                  var10012 = .MODULE$;
                  var10013 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("false"));
                  var49 = new Elem((String)null, "td", var10012, var10013, false, $buf);
               }

               $buf.$amp$plus(var49);
               $buf.$amp$plus(new Text("\n      "));
               var10000.<init>((String)null, "tr", var10004, var10005, false, $buf);
               Elem var5 = var10000;
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }

         // $FF: synthetic method
         public ExecutorTable org$apache$spark$ui$jobs$ExecutorTable$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.stage$1 = stage$1;
            }
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public ExecutorTable(StageData stage, AppStatusStore store) {
      this.stage = stage;
      this.org$apache$spark$ui$jobs$ExecutorTable$$store = store;
   }
}
