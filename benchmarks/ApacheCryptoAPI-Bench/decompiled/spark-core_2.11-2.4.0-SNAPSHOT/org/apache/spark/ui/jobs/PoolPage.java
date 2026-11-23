package org.apache.spark.ui.jobs;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SparkContext;
import org.apache.spark.scheduler.Schedulable;
import org.apache.spark.status.PoolData;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
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
   bytes = "\u0006\u0001\u00013Q!\u0001\u0002\u0001\t1\u0011\u0001\u0002U8pYB\u000bw-\u001a\u0006\u0003\u0007\u0011\tAA[8cg*\u0011QAB\u0001\u0003k&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\b\u000e\u0003\u0011I!\u0001\u0005\u0003\u0003\u0013]+'-V%QC\u001e,\u0007\u0002\u0003\n\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000b\u0002\rA\f'/\u001a8u\u0007\u0001\u0001\"!\u0006\f\u000e\u0003\tI!a\u0006\u0002\u0003\u0013M#\u0018mZ3t)\u0006\u0014\u0007\"B\r\u0001\t\u0003Q\u0012A\u0002\u001fj]&$h\b\u0006\u0002\u001c9A\u0011Q\u0003\u0001\u0005\u0006%a\u0001\r\u0001\u0006\u0005\u0006=\u0001!\taH\u0001\u0007e\u0016tG-\u001a:\u0015\u0005\u0001\"\u0004cA\u0011,]9\u0011!\u0005\u000b\b\u0003G\u0019j\u0011\u0001\n\u0006\u0003KM\ta\u0001\u0010:p_Rt\u0014\"A\u0014\u0002\u000bM\u001c\u0017\r\\1\n\u0005%R\u0013a\u00029bG.\fw-\u001a\u0006\u0002O%\u0011A&\f\u0002\u0004'\u0016\f(BA\u0015+!\ty#'D\u00011\u0015\t\t$&A\u0002y[2L!a\r\u0019\u0003\t9{G-\u001a\u0005\u0006ku\u0001\rAN\u0001\be\u0016\fX/Z:u!\t9d(D\u00019\u0015\tI$(\u0001\u0003iiR\u0004(BA\u001e=\u0003\u001d\u0019XM\u001d<mKRT\u0011!P\u0001\u0006U\u00064\u0018\r_\u0005\u0003\u007fa\u0012!\u0003\u0013;uaN+'O\u001e7fiJ+\u0017/^3ti\u0002"
)
public class PoolPage extends WebUIPage {
   public final StagesTab org$apache$spark$ui$jobs$PoolPage$$parent;

   public Seq<Node> render(HttpServletRequest request) {
      String poolName = (String)Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("poolname"))).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String poolname) {
            return UIUtils$.MODULE$.decodeURLParameter(poolname);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nothing$ apply() {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Missing poolname parameter"})))).s(Nil$.MODULE$));
         }
      });
      Schedulable pool = (Schedulable)this.org$apache$spark$ui$jobs$PoolPage$$parent.sc().flatMap(new Serializable(this, poolName) {
         public static final long serialVersionUID = 0L;
         private final String poolName$1;

         public final Option<Schedulable> apply(SparkContext x$1) {
            return x$1.getPoolForName(this.poolName$1);
         }

         public {
            this.poolName$1 = poolName$1;
         }
      }).getOrElse(new Serializable(this, poolName) {
         public static final long serialVersionUID = 0L;
         private final String poolName$1;

         public final Nothing$ apply() {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown pool: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.poolName$1})));
         }

         public {
            this.poolName$1 = poolName$1;
         }
      });
      PoolData uiPool = (PoolData)this.org$apache$spark$ui$jobs$PoolPage$$parent.store().asOption(new Serializable(this, poolName) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PoolPage $outer;
         private final String poolName$1;

         public final PoolData apply() {
            return this.$outer.org$apache$spark$ui$jobs$PoolPage$$parent.store().pool(this.poolName$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.poolName$1 = poolName$1;
            }
         }
      }).getOrElse(new Serializable(this, poolName) {
         public static final long serialVersionUID = 0L;
         private final String poolName$1;

         public final PoolData apply() {
            return new PoolData(this.poolName$1, (Set)Predef$.MODULE$.Set().apply(Nil$.MODULE$));
         }

         public {
            this.poolName$1 = poolName$1;
         }
      });
      Seq activeStages = (Seq)uiPool.stageIds().toSeq().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PoolPage $outer;

         public final StageData apply(int x$2) {
            return this.$outer.org$apache$spark$ui$jobs$PoolPage$$parent.store().lastStageAttempt(x$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom());
      StageTableBase activeStagesTable = new StageTableBase(this.org$apache$spark$ui$jobs$PoolPage$$parent.store(), request, activeStages, "", "activeStage", this.org$apache$spark$ui$jobs$PoolPage$$parent.basePath(), "stages/pool", this.org$apache$spark$ui$jobs$PoolPage$$parent.isFairScheduler(), this.org$apache$spark$ui$jobs$PoolPage$$parent.killEnabled(), false);
      PoolTable poolTable = new PoolTable((Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(pool), uiPool)})), this.org$apache$spark$ui$jobs$PoolPage$$parent);
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Summary "));
      ObjectRef content = ObjectRef.create((NodeSeq)(new Elem((String)null, "h4", var10004, var10005, false, $buf)).$plus$plus(poolTable.toNodeSeq(), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      if (activeStages.nonEmpty()) {
         NodeSeq var10001 = (NodeSeq)content.elem;
         MetaData $md = .MODULE$;
         MetaData var17 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-poolActiveStages',\n            'aggregated-poolActiveStages')"), $md);
         var17 = new UnprefixedAttribute("class", new Text("collapse-aggregated-poolActiveStages collapse-table"), var17);
         TopScope var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var10014 = .MODULE$;
         TopScope var10015 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var19 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var19, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         Null var10022 = .MODULE$;
         TopScope var10023 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Active Stages ("));
         $buf.$amp$plus(BoxesRunTime.boxToInteger(activeStages.size()));
         $buf.$amp$plus(new Text(")"));
         $buf.$amp$plus(new Elem((String)null, "a", var10022, var10023, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "h4", var10014, var10015, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Elem var10002 = new Elem((String)null, "span", var17, var10007, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var20 = new UnprefixedAttribute("class", new Text("aggregated-poolActiveStages collapsible-table"), $md);
         TopScope var10008 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(activeStagesTable.toNodeSeq());
         $buf.$amp$plus(new Text("\n        "));
         content.elem = (T)((NodeSeq)var10001.$plus$plus((GenTraversableOnce)var10002.$plus$plus(new Elem((String)null, "div", var20, var10008, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()));
      }

      return UIUtils$.MODULE$.headerSparkPage((new StringBuilder()).append((Object)"Fair Scheduler Pool: ").append((Object)poolName).toString(), new Serializable(this, content) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef content$1;

         public final NodeSeq apply() {
            return (NodeSeq)this.content$1.elem;
         }

         public {
            this.content$1 = content$1;
         }
      }, this.org$apache$spark$ui$jobs$PoolPage$$parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7());
   }

   public PoolPage(StagesTab parent) {
      super("pool");
      this.org$apache$spark$ui$jobs$PoolPage$$parent = parent;
   }
}
