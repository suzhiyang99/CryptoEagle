package org.apache.spark.ui.jobs;

import java.net.URLEncoder;
import org.apache.spark.scheduler.Schedulable;
import org.apache.spark.status.PoolData;
import org.apache.spark.ui.UIUtils$;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Iterable$;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
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
   bytes = "\u0006\u0001E3Q!\u0001\u0002\u0001\t1\u0011\u0011\u0002U8pYR\u000b'\r\\3\u000b\u0005\r!\u0011\u0001\u00026pENT!!\u0002\u0004\u0002\u0005UL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0003\u0005\u0015\u0001\t\u0005\t\u0015!\u0003\u0017\u0003\u0015\u0001xn\u001c7t\u0007\u0001\u0001Ba\u0006\u000e\u001eG9\u0011a\u0002G\u0005\u00033=\ta\u0001\u0015:fI\u00164\u0017BA\u000e\u001d\u0005\ri\u0015\r\u001d\u0006\u00033=\u0001\"AH\u0011\u000e\u0003}Q!\u0001\t\u0004\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014\u0018B\u0001\u0012 \u0005-\u00196\r[3ek2\f'\r\\3\u0011\u0005\u0011:S\"A\u0013\u000b\u0005\u00192\u0011AB:uCR,8/\u0003\u0002)K\tA\u0001k\\8m\t\u0006$\u0018\r\u0003\u0005+\u0001\t\u0005\t\u0015!\u0003,\u0003\u0019\u0001\u0018M]3oiB\u0011A&L\u0007\u0002\u0005%\u0011aF\u0001\u0002\n'R\fw-Z:UC\nDQ\u0001\r\u0001\u0005\u0002E\na\u0001P5oSRtDc\u0001\u001a4iA\u0011A\u0006\u0001\u0005\u0006)=\u0002\rA\u0006\u0005\u0006U=\u0002\ra\u000b\u0005\u0006m\u0001!\taN\u0001\ni>tu\u000eZ3TKF,\u0012\u0001\u000f\t\u0004s\u0005#eB\u0001\u001e@\u001d\tYd(D\u0001=\u0015\tiT#\u0001\u0004=e>|GOP\u0005\u0002!%\u0011\u0001iD\u0001\ba\u0006\u001c7.Y4f\u0013\t\u00115IA\u0002TKFT!\u0001Q\b\u0011\u0005\u0015CU\"\u0001$\u000b\u0005\u001d{\u0011a\u0001=nY&\u0011\u0011J\u0012\u0002\u0005\u001d>$W\rC\u0003L\u0001\u0011%A*A\u0004q_>d'k\\<\u0015\u0007aju\nC\u0003O\u0015\u0002\u0007Q$A\u0001t\u0011\u0015\u0001&\n1\u0001$\u0003\u0005\u0001\b"
)
public class PoolTable {
   private final Map<Schedulable, PoolData> pools;
   private final StagesTab parent;

   public Seq<Node> toNodeSeq() {
      MetaData $md = .MODULE$;
      MetaData var11 = new UnprefixedAttribute("class", new Text("table table-bordered table-striped table-condensed sortable table-fixed"), $md);
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      Null var10020 = .MODULE$;
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Pool Name"));
      $buf.$amp$plus(new Elem((String)null, "th", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10020 = .MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Minimum Share"));
      $buf.$amp$plus(new Elem((String)null, "th", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10020 = .MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Pool Weight"));
      $buf.$amp$plus(new Elem((String)null, "th", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10020 = .MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Active Stages"));
      $buf.$amp$plus(new Elem((String)null, "th", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10020 = .MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Running Tasks"));
      $buf.$amp$plus(new Elem((String)null, "th", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      var10020 = .MODULE$;
      var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("SchedulingMode"));
      $buf.$amp$plus(new Elem((String)null, "th", var10020, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "thead", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(this.pools.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PoolTable $outer;

         public final Seq<Node> apply(Tuple2<Schedulable, PoolData> x0$1) {
            if (x0$1 != null) {
               Schedulable s = (Schedulable)x0$1._1();
               PoolData p = (PoolData)x0$1._2();
               Seq var5 = this.$outer.org$apache$spark$ui$jobs$PoolTable$$poolRow(s, p);
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Iterable$.MODULE$.canBuildFrom()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "tbody", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "table", var11, var10005, false, $buf);
   }

   public Seq<Node> org$apache$spark$ui$jobs$PoolTable$$poolRow(Schedulable s, PoolData p) {
      int activeStages = p.stageIds().size();
      String href = (new StringOps(Predef$.MODULE$.augmentString("%s/stages/pool?poolname=%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{UIUtils$.MODULE$.prependBaseUri(this.parent.basePath(), UIUtils$.MODULE$.prependBaseUri$default$2()), URLEncoder.encode(p.name(), "UTF-8")}));
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var14 = new UnprefixedAttribute("href", href, $md);
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(p.name());
      $buf.$amp$plus(new Elem((String)null, "a", var14, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(s.minShare()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(s.weight()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(activeStages));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(s.runningTasks()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(s.schedulingMode());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   public PoolTable(Map<Schedulable, PoolData> pools, StagesTab parent) {
      this.pools = pools;
      this.parent = parent;
   }
}
