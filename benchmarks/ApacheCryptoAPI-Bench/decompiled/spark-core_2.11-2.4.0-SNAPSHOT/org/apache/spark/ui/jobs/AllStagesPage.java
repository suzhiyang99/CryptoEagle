package org.apache.spark.ui.jobs;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SparkContext;
import org.apache.spark.scheduler.Schedulable;
import org.apache.spark.status.AppSummary;
import org.apache.spark.status.PoolData;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.StageStatus;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ObjectRef;
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

@ScalaSignature(
   bytes = "\u0006\u0001M3Q!\u0001\u0002\u0001\t1\u0011Q\"\u00117m'R\fw-Z:QC\u001e,'BA\u0002\u0005\u0003\u0011QwNY:\u000b\u0005\u00151\u0011AA;j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tqq\"D\u0001\u0005\u0013\t\u0001BAA\u0005XK\n,\u0016\nU1hK\"A!\u0003\u0001B\u0001B\u0003%A#\u0001\u0004qCJ,g\u000e^\u0002\u0001!\t)b#D\u0001\u0003\u0013\t9\"AA\u0005Ti\u0006<Wm\u001d+bE\")\u0011\u0004\u0001C\u00015\u00051A(\u001b8jiz\"\"a\u0007\u000f\u0011\u0005U\u0001\u0001\"\u0002\n\u0019\u0001\u0004!\u0002b\u0002\u0010\u0001\u0005\u0004%IaH\u0001\u0003g\u000e,\u0012\u0001\t\t\u0004C\u00112S\"\u0001\u0012\u000b\u0003\r\nQa]2bY\u0006L!!\n\u0012\u0003\r=\u0003H/[8o!\t9\u0003&D\u0001\u0007\u0013\tIcA\u0001\u0007Ta\u0006\u00148nQ8oi\u0016DH\u000f\u0003\u0004,\u0001\u0001\u0006I\u0001I\u0001\u0004g\u000e\u0004\u0003\"B\u0017\u0001\t\u0013q\u0013aD5t\r\u0006L'oU2iK\u0012,H.\u001a:\u0016\u0003=\u0002\"!\t\u0019\n\u0005E\u0012#a\u0002\"p_2,\u0017M\u001c\u0005\u0006g\u0001!\t\u0001N\u0001\u0007e\u0016tG-\u001a:\u0015\u0005U:\u0005c\u0001\u001c?\u0003:\u0011q\u0007\u0010\b\u0003qmj\u0011!\u000f\u0006\u0003uM\ta\u0001\u0010:p_Rt\u0014\"A\u0012\n\u0005u\u0012\u0013a\u00029bG.\fw-Z\u0005\u0003\u007f\u0001\u00131aU3r\u0015\ti$\u0005\u0005\u0002C\u000b6\t1I\u0003\u0002EE\u0005\u0019\u00010\u001c7\n\u0005\u0019\u001b%\u0001\u0002(pI\u0016DQ\u0001\u0013\u001aA\u0002%\u000bqA]3rk\u0016\u001cH\u000f\u0005\u0002K#6\t1J\u0003\u0002M\u001b\u0006!\u0001\u000e\u001e;q\u0015\tqu*A\u0004tKJ4H.\u001a;\u000b\u0003A\u000bQA[1wCbL!AU&\u0003%!#H\u000f]*feZdW\r\u001e*fcV,7\u000f\u001e"
)
public class AllStagesPage extends WebUIPage {
   public final StagesTab org$apache$spark$ui$jobs$AllStagesPage$$parent;
   private final Option<SparkContext> sc;

   private Option<SparkContext> sc() {
      return this.sc;
   }

   private boolean isFairScheduler() {
      return this.org$apache$spark$ui$jobs$AllStagesPage$$parent.isFairScheduler();
   }

   public Seq<Node> render(HttpServletRequest request) {
      Seq allStages = this.org$apache$spark$ui$jobs$AllStagesPage$$parent.store().stageList((List)null);
      Seq activeStages = (Seq)allStages.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(StageData x$1) {
            boolean var3;
            label23: {
               StageStatus var10000 = x$1.status();
               StageStatus var2 = StageStatus.ACTIVE;
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
      });
      Seq pendingStages = (Seq)allStages.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(StageData x$2) {
            boolean var3;
            label23: {
               StageStatus var10000 = x$2.status();
               StageStatus var2 = StageStatus.PENDING;
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
      });
      Seq completedStages = (Seq)allStages.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(StageData x$3) {
            boolean var3;
            label23: {
               StageStatus var10000 = x$3.status();
               StageStatus var2 = StageStatus.COMPLETE;
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
      });
      Seq failedStages = (Seq)((SeqLike)allStages.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(StageData x$4) {
            boolean var3;
            label23: {
               StageStatus var10000 = x$4.status();
               StageStatus var2 = StageStatus.FAILED;
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
      })).reverse();
      int numFailedStages = failedStages.size();
      String subPath = "stages";
      StageTableBase activeStagesTable = new StageTableBase(this.org$apache$spark$ui$jobs$AllStagesPage$$parent.store(), request, activeStages, "active", "activeStage", this.org$apache$spark$ui$jobs$AllStagesPage$$parent.basePath(), subPath, this.org$apache$spark$ui$jobs$AllStagesPage$$parent.isFairScheduler(), this.org$apache$spark$ui$jobs$AllStagesPage$$parent.killEnabled(), false);
      StageTableBase pendingStagesTable = new StageTableBase(this.org$apache$spark$ui$jobs$AllStagesPage$$parent.store(), request, pendingStages, "pending", "pendingStage", this.org$apache$spark$ui$jobs$AllStagesPage$$parent.basePath(), subPath, this.org$apache$spark$ui$jobs$AllStagesPage$$parent.isFairScheduler(), false, false);
      StageTableBase completedStagesTable = new StageTableBase(this.org$apache$spark$ui$jobs$AllStagesPage$$parent.store(), request, completedStages, "completed", "completedStage", this.org$apache$spark$ui$jobs$AllStagesPage$$parent.basePath(), subPath, this.org$apache$spark$ui$jobs$AllStagesPage$$parent.isFairScheduler(), false, false);
      StageTableBase failedStagesTable = new StageTableBase(this.org$apache$spark$ui$jobs$AllStagesPage$$parent.store(), request, failedStages, "failed", "failedStage", this.org$apache$spark$ui$jobs$AllStagesPage$$parent.basePath(), subPath, this.org$apache$spark$ui$jobs$AllStagesPage$$parent.isFairScheduler(), false, true);
      Map pools = ((TraversableOnce)((TraversableLike)this.sc().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Schedulable> apply(SparkContext x$5) {
            return x$5.getAllPools();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Schedulable> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AllStagesPage $outer;

         public final Tuple2<Schedulable, PoolData> apply(Schedulable pool) {
            PoolData uiPool = (PoolData)this.$outer.org$apache$spark$ui$jobs$AllStagesPage$$parent.store().asOption(new Serializable(this, pool) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Schedulable pool$1;

               public final PoolData apply() {
                  return this.$outer.org$apache$spark$ui$jobs$AllStagesPage$$anonfun$$$outer().org$apache$spark$ui$jobs$AllStagesPage$$parent.store().pool(this.pool$1.name());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.pool$1 = pool$1;
                  }
               }
            }).getOrElse(new Serializable(this, pool) {
               public static final long serialVersionUID = 0L;
               private final Schedulable pool$1;

               public final PoolData apply() {
                  return new PoolData(this.pool$1.name(), (Set)Predef$.MODULE$.Set().apply(Nil$.MODULE$));
               }

               public {
                  this.pool$1 = pool$1;
               }
            });
            return Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(pool), uiPool);
         }

         // $FF: synthetic method
         public AllStagesPage org$apache$spark$ui$jobs$AllStagesPage$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
      PoolTable poolTable = new PoolTable(pools, this.org$apache$spark$ui$jobs$AllStagesPage$$parent);
      boolean shouldShowActiveStages = activeStages.nonEmpty();
      boolean shouldShowPendingStages = pendingStages.nonEmpty();
      boolean shouldShowCompletedStages = completedStages.nonEmpty();
      boolean shouldShowFailedStages = failedStages.nonEmpty();
      AppSummary appSummary = this.org$apache$spark$ui$jobs$AllStagesPage$$parent.store().appSummary();
      String completedStageNumStr = appSummary.numCompletedStages() == completedStages.size() ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(appSummary.numCompletedStages())})) : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", only showing ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(appSummary.numCompletedStages()), BoxesRunTime.boxToInteger(completedStages.size())}));
      Elem var10000 = new Elem;
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var78 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
      Elem var10008 = new Elem;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      Object var10016;
      if (shouldShowActiveStages) {
         Null var10020 = .MODULE$;
         TopScope var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var79 = new UnprefixedAttribute("href", new Text("#active"), $md);
         TopScope var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         Null var10036 = .MODULE$;
         TopScope var10037 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Active Stages:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var10036, var10037, false, $buf));
         $buf.$amp$plus(new Elem((String)null, "a", var79, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(activeStages.size()));
         $buf.$amp$plus(new Text("\n              "));
         var10016 = new Elem((String)null, "li", var10020, var10021, false, $buf);
      } else {
         var10016 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n          "));
      if (shouldShowPendingStages) {
         Null var135 = .MODULE$;
         TopScope var137 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var80 = new UnprefixedAttribute("href", new Text("#pending"), $md);
         TopScope var148 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         Null var151 = .MODULE$;
         TopScope var154 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Pending Stages:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var151, var154, false, $buf));
         $buf.$amp$plus(new Elem((String)null, "a", var80, var148, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(pendingStages.size()));
         $buf.$amp$plus(new Text("\n              "));
         var10016 = new Elem((String)null, "li", var135, var137, false, $buf);
      } else {
         var10016 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n          "));
      if (shouldShowCompletedStages) {
         MetaData $md = .MODULE$;
         MetaData var81 = new UnprefixedAttribute("id", new Text("completed-summary"), $md);
         TopScope var138 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var82 = new UnprefixedAttribute("href", new Text("#completed"), $md);
         TopScope var149 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         Null var152 = .MODULE$;
         TopScope var155 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Completed Stages:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var152, var155, false, $buf));
         $buf.$amp$plus(new Elem((String)null, "a", var82, var149, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(completedStageNumStr);
         $buf.$amp$plus(new Text("\n              "));
         var10016 = new Elem((String)null, "li", var81, var138, false, $buf);
      } else {
         var10016 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n          "));
      if (shouldShowFailedStages) {
         Null var136 = .MODULE$;
         TopScope var139 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n                "));
         MetaData $md = .MODULE$;
         MetaData var83 = new UnprefixedAttribute("href", new Text("#failed"), $md);
         TopScope var150 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         Null var153 = .MODULE$;
         TopScope var156 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Failed Stages:"));
         $buf.$amp$plus(new Elem((String)null, "strong", var153, var156, false, $buf));
         $buf.$amp$plus(new Elem((String)null, "a", var83, var150, false, $buf));
         $buf.$amp$plus(new Text("\n                "));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(numFailedStages));
         $buf.$amp$plus(new Text("\n              "));
         var10016 = new Elem((String)null, "li", var136, var139, false, $buf);
      } else {
         var10016 = BoxedUnit.UNIT;
      }

      $buf.$amp$plus(var10016);
      $buf.$amp$plus(new Text("\n        "));
      var10008.<init>((String)null, "ul", var78, var10013, false, $buf);
      $buf.$amp$plus(var10008);
      $buf.$amp$plus(new Text("\n      "));
      var10000.<init>((String)null, "div", var10004, var10005, false, $buf);
      NodeSeq summary = var10000;
      Object var10001;
      if (this.sc().isDefined() && this.isFairScheduler()) {
         MetaData $md = .MODULE$;
         MetaData var84 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-poolTable','aggregated-poolTable')"), $md);
         var84 = new UnprefixedAttribute("class", new Text("collapse-aggregated-poolTable collapse-table"), var84);
         TopScope var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         Null var124 = .MODULE$;
         TopScope var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         MetaData $md = .MODULE$;
         MetaData var86 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var86, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n              "));
         Null var140 = .MODULE$;
         TopScope var10022 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Fair Scheduler Pools ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(pools.size()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var140, var10022, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "h4", var124, var10014, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         Elem var108 = new Elem((String)null, "span", var84, var10006, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var87 = new UnprefixedAttribute("class", new Text("aggregated-poolTable collapsible-table"), $md);
         TopScope var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(poolTable.toNodeSeq());
         $buf.$amp$plus(new Text("\n          "));
         var10001 = (GenTraversableOnce)var108.$plus$plus(new Elem((String)null, "div", var87, var10007, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      } else {
         var10001 = Seq$.MODULE$.empty();
      }

      ObjectRef content = ObjectRef.create((NodeSeq)summary.$plus$plus((GenTraversableOnce)var10001, scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      if (shouldShowActiveStages) {
         NodeSeq var109 = (NodeSeq)content.elem;
         MetaData $md = .MODULE$;
         MetaData var88 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-allActiveStages',\n            'aggregated-allActiveStages')"), $md);
         var88 = new UnprefixedAttribute("class", new Text("collapse-aggregated-allActiveStages collapse-table"), var88);
         var88 = new UnprefixedAttribute("id", new Text("active"), var88);
         TopScope var116 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var125 = .MODULE$;
         TopScope var10015 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var91 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var91, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         Null var141 = .MODULE$;
         TopScope var10023 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Active Stages ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(activeStages.size()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var141, var10023, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "h4", var125, var10015, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Elem var10002 = new Elem((String)null, "span", var88, var116, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var92 = new UnprefixedAttribute("class", new Text("aggregated-allActiveStages collapsible-table"), $md);
         TopScope var120 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(activeStagesTable.toNodeSeq());
         $buf.$amp$plus(new Text("\n        "));
         content.elem = (T)((NodeSeq)var109.$plus$plus((GenTraversableOnce)var10002.$plus$plus(new Elem((String)null, "div", var92, var120, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      }

      if (shouldShowPendingStages) {
         NodeSeq var110 = (NodeSeq)content.elem;
         MetaData $md = .MODULE$;
         MetaData var93 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-allPendingStages',\n            'aggregated-allPendingStages')"), $md);
         var93 = new UnprefixedAttribute("class", new Text("collapse-aggregated-allPendingStages collapse-table"), var93);
         var93 = new UnprefixedAttribute("id", new Text("pending"), var93);
         TopScope var117 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var126 = .MODULE$;
         TopScope var129 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var96 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var96, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         Null var142 = .MODULE$;
         TopScope var145 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Pending Stages ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(pendingStages.size()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var142, var145, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "h4", var126, var129, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Elem var113 = new Elem((String)null, "span", var93, var117, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var97 = new UnprefixedAttribute("class", new Text("aggregated-allPendingStages collapsible-table"), $md);
         TopScope var121 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(pendingStagesTable.toNodeSeq());
         $buf.$amp$plus(new Text("\n        "));
         content.elem = (T)((NodeSeq)var110.$plus$plus((GenTraversableOnce)var113.$plus$plus(new Elem((String)null, "div", var97, var121, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      }

      if (shouldShowCompletedStages) {
         NodeSeq var111 = (NodeSeq)content.elem;
         MetaData $md = .MODULE$;
         MetaData var98 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-allCompletedStages',\n            'aggregated-allCompletedStages')"), $md);
         var98 = new UnprefixedAttribute("class", new Text("collapse-aggregated-allCompletedStages collapse-table"), var98);
         var98 = new UnprefixedAttribute("id", new Text("completed"), var98);
         TopScope var118 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var127 = .MODULE$;
         TopScope var130 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var101 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var101, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         Null var143 = .MODULE$;
         TopScope var146 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Completed Stages ("));
         $buf.$amp$plus(completedStageNumStr);
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var143, var146, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "h4", var127, var130, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Elem var114 = new Elem((String)null, "span", var98, var118, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var102 = new UnprefixedAttribute("class", new Text("aggregated-allCompletedStages collapsible-table"), $md);
         TopScope var122 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(completedStagesTable.toNodeSeq());
         $buf.$amp$plus(new Text("\n        "));
         content.elem = (T)((NodeSeq)var111.$plus$plus((GenTraversableOnce)var114.$plus$plus(new Elem((String)null, "div", var102, var122, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      }

      if (shouldShowFailedStages) {
         NodeSeq var112 = (NodeSeq)content.elem;
         MetaData $md = .MODULE$;
         MetaData var103 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-allFailedStages',\n            'aggregated-allFailedStages')"), $md);
         var103 = new UnprefixedAttribute("class", new Text("collapse-aggregated-allFailedStages collapse-table"), var103);
         var103 = new UnprefixedAttribute("id", new Text("failed"), var103);
         TopScope var119 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var128 = .MODULE$;
         TopScope var131 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var106 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var106, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         Null var144 = .MODULE$;
         TopScope var147 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Failed Stages ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(numFailedStages));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var144, var147, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "h4", var128, var131, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Elem var115 = new Elem((String)null, "span", var103, var119, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var107 = new UnprefixedAttribute("class", new Text("aggregated-allFailedStages collapsible-table"), $md);
         TopScope var123 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(failedStagesTable.toNodeSeq());
         $buf.$amp$plus(new Text("\n        "));
         content.elem = (T)((NodeSeq)var112.$plus$plus((GenTraversableOnce)var115.$plus$plus(new Elem((String)null, "div", var107, var123, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      }

      return UIUtils$.MODULE$.headerSparkPage("Stages for All Jobs", new Serializable(this, content) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef content$1;

         public final NodeSeq apply() {
            return (NodeSeq)this.content$1.elem;
         }

         public {
            this.content$1 = content$1;
         }
      }, this.org$apache$spark$ui$jobs$AllStagesPage$$parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7());
   }

   public AllStagesPage(StagesTab parent) {
      super("");
      this.org$apache$spark$ui$jobs$AllStagesPage$$parent = parent;
      this.sc = parent.sc();
   }
}
