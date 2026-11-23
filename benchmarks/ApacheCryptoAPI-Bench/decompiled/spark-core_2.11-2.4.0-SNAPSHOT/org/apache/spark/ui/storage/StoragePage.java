package org.apache.spark.ui.storage;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.StreamBlockData;
import org.apache.spark.status.api.v1.RDDStorageInfo;
import org.apache.spark.ui.SparkUITab;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.util.Utils$;
import scala.Function1;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SortedMap$;
import scala.collection.TraversableLike;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.math.Ordering;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
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
   bytes = "\u0006\u0001\u00055d!B\u0001\u0003\u0001\u0011a!aC*u_J\fw-\u001a)bO\u0016T!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0003k&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\b\u000e\u0003\u0011I!\u0001\u0005\u0003\u0003\u0013]+'-V%QC\u001e,\u0007\u0002\u0003\n\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000b\u0002\rA\f'/\u001a8u\u0007\u0001\u0001\"AD\u000b\n\u0005Y!!AC*qCJ\\W+\u0013+bE\"A\u0001\u0004\u0001B\u0001B\u0003%\u0011$A\u0003ti>\u0014X\r\u0005\u0002\u001b;5\t1D\u0003\u0002\u001d\r\u000511\u000f^1ukNL!AH\u000e\u0003\u001d\u0005\u0003\bo\u0015;biV\u001c8\u000b^8sK\")\u0001\u0005\u0001C\u0001C\u00051A(\u001b8jiz\"2A\t\u0013&!\t\u0019\u0003!D\u0001\u0003\u0011\u0015\u0011r\u00041\u0001\u0015\u0011\u0015Ar\u00041\u0001\u001a\u0011\u00159\u0003\u0001\"\u0001)\u0003\u0019\u0011XM\u001c3feR\u0011\u0011&\u0010\t\u0004UQ:dBA\u00162\u001d\tas&D\u0001.\u0015\tq3#\u0001\u0004=e>|GOP\u0005\u0002a\u0005)1oY1mC&\u0011!gM\u0001\ba\u0006\u001c7.Y4f\u0015\u0005\u0001\u0014BA\u001b7\u0005\r\u0019V-\u001d\u0006\u0003eM\u0002\"\u0001O\u001e\u000e\u0003eR!AO\u001a\u0002\u0007alG.\u0003\u0002=s\t!aj\u001c3f\u0011\u0015qd\u00051\u0001@\u0003\u001d\u0011X-];fgR\u0004\"\u0001Q$\u000e\u0003\u0005S!AQ\"\u0002\t!$H\u000f\u001d\u0006\u0003\t\u0016\u000bqa]3sm2,GOC\u0001G\u0003\u0015Q\u0017M^1y\u0013\tA\u0015I\u0001\nIiR\u00048+\u001a:wY\u0016$(+Z9vKN$\bB\u0002&\u0001\t\u0003\u00111*\u0001\u0005sI\u0012$\u0016M\u00197f)\tIC\nC\u0003N\u0013\u0002\u0007a*\u0001\u0003sI\u0012\u001c\bc\u0001\u00165\u001fB\u0011\u0001+V\u0007\u0002#*\u0011!kU\u0001\u0003mFR!\u0001V\u000e\u0002\u0007\u0005\u0004\u0018.\u0003\u0002W#\nq!\u000b\u0012#Ti>\u0014\u0018mZ3J]\u001a|\u0007b\u0002-\u0001\u0005\u0004%I!W\u0001\ne\u0012$\u0007*Z1eKJ,\u0012A\u0017\t\u00047z{V\"\u0001/\u000b\u0005u\u001b\u0014AC2pY2,7\r^5p]&\u0011Q\u0007\u0018\t\u0003A\u0016l\u0011!\u0019\u0006\u0003E\u000e\fA\u0001\\1oO*\tA-\u0001\u0003kCZ\f\u0017B\u00014b\u0005\u0019\u0019FO]5oO\"1\u0001\u000e\u0001Q\u0001\ni\u000b!B\u001d3e\u0011\u0016\fG-\u001a:!\u0011\u0015Q\u0007\u0001\"\u0003l\u0003\u0019\u0011H\r\u001a*poR\u0011\u0011\u0006\u001c\u0005\u0006[&\u0004\raT\u0001\u0004e\u0012$\u0007BB8\u0001\t\u0003\u0011\u0001/A\nsK\u000e,\u0017N^3s\u00052|7m\u001b+bE2,7\u000f\u0006\u0002*c\")!O\u001ca\u0001g\u00061!\r\\8dWN\u00042A\u000b\u001bu!\tQR/\u0003\u0002w7\ty1\u000b\u001e:fC6\u0014En\\2l\t\u0006$\u0018\rC\u0003y\u0001\u0011%\u00110\u0001\u000bfq\u0016\u001cW\u000f^8s\u001b\u0016$(/[2t)\u0006\u0014G.\u001a\u000b\u0003SiDQA]<A\u0002MDq\u0001 \u0001C\u0002\u0013%\u0011,\u0001\u000efq\u0016\u001cW\u000f^8s\u001b\u0016$(/[2t)\u0006\u0014G.\u001a%fC\u0012,'\u000f\u0003\u0004\u007f\u0001\u0001\u0006IAW\u0001\u001cKb,7-\u001e;pe6+GO]5dgR\u000b'\r\\3IK\u0006$WM\u001d\u0011\t\u000f\u0005\u0005\u0001\u0001\"\u0003\u0002\u0004\u00059R\r_3dkR|'/T3ue&\u001c7\u000fV1cY\u0016\u0014vn\u001e\u000b\u0004S\u0005\u0015\u0001B\u0002\u000f\u0000\u0001\u0004\t9\u0001E\u0002$\u0003\u0013I1!a\u0003\u0003\u0005U)\u00050Z2vi>\u00148\u000b\u001e:fC6\u001cV/\\7befDq!a\u0004\u0001\t\u0013\t\t\"\u0001\ttiJ,\u0017-\u001c\"m_\u000e\\G+\u00192mKR\u0019\u0011&a\u0005\t\u000fI\fi\u00011\u0001\u0002\u0016A!!\u0006NA\f!\u001d\tI\"a\u0007\u0002 Ml\u0011aM\u0005\u0004\u0003;\u0019$A\u0002+va2,'\u0007\u0005\u0003\u0002\"\u0005\u001db\u0002BA\r\u0003GI1!!\n4\u0003\u0019\u0001&/\u001a3fM&\u0019a-!\u000b\u000b\u0007\u0005\u00152\u0007\u0003\u0005\u0002.\u0001\u0011\r\u0011\"\u0003Z\u0003Y\u0019HO]3b[\ncwnY6UC\ndW\rS3bI\u0016\u0014\bbBA\u0019\u0001\u0001\u0006IAW\u0001\u0018gR\u0014X-Y7CY>\u001c7\u000eV1cY\u0016DU-\u00193fe\u0002Bq!!\u000e\u0001\t\u0013\t9$A\ntiJ,\u0017-\u001c\"m_\u000e\\G+\u00192mKJ{w\u000fF\u0002*\u0003sA\u0001\"a\u000f\u00024\u0001\u0007\u0011qC\u0001\u0006E2|7m\u001b\u0005\b\u0003\u007f\u0001A\u0011BA!\u0003Y\u0019HO]3b[\ncwnY6UC\ndWmU;ce><H#C\u0015\u0002D\u0005\u001d\u0013\u0011JA*\u0011!\t)%!\u0010A\u0002\u0005}\u0011a\u00022m_\u000e\\\u0017\n\u001a\u0005\b\u0003w\ti\u00041\u0001u\u0011!\tY%!\u0010A\u0002\u00055\u0013a\u0003:fa2L7-\u0019;j_:\u0004B!!\u0007\u0002P%\u0019\u0011\u0011K\u001a\u0003\u0007%sG\u000f\u0003\u0005\u0002V\u0005u\u0002\u0019AA,\u0003-1\u0017N]:u'V\u0014'o\\<\u0011\t\u0005e\u0011\u0011L\u0005\u0004\u00037\u001a$a\u0002\"p_2,\u0017M\u001c\u0005\t\u0003?\u0002A\u0011\u0001\u0002\u0002b\u0005I3\u000f\u001e:fC6\u0014En\\2l'R|'/Y4f\u0019\u00164X\r\u001c#fg\u000e\u0014\u0018\u000e\u001d;j_:\fe\u000eZ*ju\u0016$B!a\u0019\u0002lAA\u0011\u0011DA\u000e\u0003?\t)\u0007\u0005\u0003\u0002\u001a\u0005\u001d\u0014bAA5g\t!Aj\u001c8h\u0011\u001d\tY$!\u0018A\u0002Q\u0004"
)
public class StoragePage extends WebUIPage {
   private final SparkUITab parent;
   private final AppStatusStore store;
   private final Seq<String> rddHeader;
   private final Seq<String> executorMetricsTableHeader;
   private final Seq<String> streamBlockTableHeader;

   public Seq<Node> render(HttpServletRequest request) {
      Seq content = (Seq)this.rddTable(this.store.rddList(this.store.rddList$default$1())).$plus$plus(this.receiverBlockTables(this.store.streamBlocksList()), Seq$.MODULE$.canBuildFrom());
      return UIUtils$.MODULE$.headerSparkPage("Storage", new Serializable(this, content) {
         public static final long serialVersionUID = 0L;
         private final Seq content$1;

         public final Seq<Node> apply() {
            return this.content$1;
         }

         public {
            this.content$1 = content$1;
         }
      }, this.parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7());
   }

   public Seq<Node> rddTable(Seq<RDDStorageInfo> rdds) {
      Object var10000;
      if (rdds.isEmpty()) {
         var10000 = Nil$.MODULE$;
      } else {
         Null var10004 = .MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var18 = new UnprefixedAttribute("onClick", new Text("collapseTable('collapse-aggregated-rdds','aggregated-rdds')"), $md);
         var18 = new UnprefixedAttribute("class", new Text("collapse-aggregated-rdds collapse-table"), var18);
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var10020 = .MODULE$;
         TopScope var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var20 = new UnprefixedAttribute("class", new Text("collapse-table-arrow arrow-open"), $md);
         $buf.$amp$plus(new Elem((String)null, "span", var20, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         Null var10028 = .MODULE$;
         TopScope var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("RDDs"));
         $buf.$amp$plus(new Elem((String)null, "a", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "h4", var10020, var10021, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "span", var18, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var21 = new UnprefixedAttribute("class", new Text("aggregated-rdds collapsible-table"), $md);
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Seq x$8 = this.rddHeader();
         Function1 x$9 = new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StoragePage $outer;

            public final Seq<Node> apply(RDDStorageInfo rdd) {
               return this.$outer.org$apache$spark$ui$storage$StoragePage$$rddRow(rdd);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
         Some x$11 = new Some("storage-by-rdd-table");
         boolean x$12 = UIUtils$.MODULE$.listingTable$default$4();
         Seq x$13 = UIUtils$.MODULE$.listingTable$default$6();
         boolean x$14 = UIUtils$.MODULE$.listingTable$default$7();
         boolean x$15 = UIUtils$.MODULE$.listingTable$default$8();
         $buf.$amp$plus(UIUtils$.MODULE$.listingTable(x$8, x$9, rdds, x$12, x$11, x$13, x$14, x$15));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "div", var21, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n      "));
         var10000 = new Elem((String)null, "div", var10004, var10005, false, $buf);
      }

      return (Seq<Node>)var10000;
   }

   private Seq<String> rddHeader() {
      return this.rddHeader;
   }

   public Seq<Node> org$apache$spark$ui$storage$StoragePage$$rddRow(RDDStorageInfo rdd) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(rdd.id()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var14 = new UnprefixedAttribute("href", (new StringOps(Predef$.MODULE$.augmentString("%s/storage/rdd?id=%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{UIUtils$.MODULE$.prependBaseUri(this.parent.basePath(), UIUtils$.MODULE$.prependBaseUri$default$2()), BoxesRunTime.boxToInteger(rdd.id())})), $md);
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(rdd.name());
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "a", var14, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(rdd.storageLevel());
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(rdd.numCachedPartitions()).toString());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus((new StringOps(Predef$.MODULE$.augmentString("%.0f%%"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble((double)rdd.numCachedPartitions() * (double)100.0F / (double)rdd.numPartitions())})));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var15 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(rdd.memoryUsed()).toString(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(rdd.memoryUsed()));
      $buf.$amp$plus(new Elem((String)null, "td", var15, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var16 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(rdd.diskUsed()).toString(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(rdd.diskUsed()));
      $buf.$amp$plus(new Elem((String)null, "td", var16, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   public Seq<Node> receiverBlockTables(Seq<StreamBlockData> blocks) {
      Object var10000;
      if (blocks.isEmpty()) {
         var10000 = Nil$.MODULE$;
      } else {
         Seq sorted = (Seq)blocks.groupBy(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(StreamBlockData x$1) {
               return x$1.name();
            }
         }).toSeq().sortBy(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(Tuple2<String, Seq<StreamBlockData>> x$2) {
               return ((String)x$2._1()).toString();
            }
         }, Ordering.String$.MODULE$);
         Null var10004 = .MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         Null var10012 = .MODULE$;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Receiver Blocks"));
         $buf.$amp$plus(new Elem((String)null, "h4", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(this.executorMetricsTable(blocks));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(this.streamBlockTable(sorted));
         $buf.$amp$plus(new Text("\n      "));
         var10000 = new Elem((String)null, "div", var10004, var10005, false, $buf);
      }

      return (Seq<Node>)var10000;
   }

   private Seq<Node> executorMetricsTable(Seq<StreamBlockData> blocks) {
      Iterable blockManagers = (Iterable)SortedMap$.MODULE$.apply(blocks.groupBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(StreamBlockData x$3) {
            return x$3.executorId();
         }
      }).toSeq(), Ordering.String$.MODULE$).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ExecutorStreamSummary apply(Tuple2<String, Seq<StreamBlockData>> x0$1) {
            if (x0$1 != null) {
               Seq blocks = (Seq)x0$1._2();
               ExecutorStreamSummary var4 = new ExecutorStreamSummary(blocks);
               return var4;
            } else {
               throw new MatchError(x0$1);
            }
         }
      }, Iterable$.MODULE$.canBuildFrom());
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Aggregated Block Metrics by Executor"));
      $buf.$amp$plus(new Elem((String)null, "h5", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      Seq x$16 = this.executorMetricsTableHeader();
      Function1 x$17 = new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StoragePage $outer;

         public final Seq<Node> apply(ExecutorStreamSummary status) {
            return this.$outer.org$apache$spark$ui$storage$StoragePage$$executorMetricsTableRow(status);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      Some x$19 = new Some("storage-by-executor-stream-blocks");
      boolean x$20 = UIUtils$.MODULE$.listingTable$default$4();
      Seq x$21 = UIUtils$.MODULE$.listingTable$default$6();
      boolean x$22 = UIUtils$.MODULE$.listingTable$default$7();
      boolean x$23 = UIUtils$.MODULE$.listingTable$default$8();
      $buf.$amp$plus(UIUtils$.MODULE$.listingTable(x$16, x$17, blockManagers, x$20, x$19, x$21, x$22, x$23));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "div", var10004, var10005, false, $buf);
   }

   private Seq<String> executorMetricsTableHeader() {
      return this.executorMetricsTableHeader;
   }

   public Seq<Node> org$apache$spark$ui$storage$StoragePage$$executorMetricsTableRow(ExecutorStreamSummary status) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(status.executorId());
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(status.location());
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var10 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(status.totalMemSize()).toString(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(status.totalMemSize()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = .MODULE$;
      MetaData var11 = new UnprefixedAttribute("sorttable_customkey", BoxesRunTime.boxToLong(status.totalDiskSize()).toString(), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(status.totalDiskSize()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var11, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(status.numStreamBlocks()).toString());
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   private Seq<Node> streamBlockTable(Seq<Tuple2<String, Seq<StreamBlockData>>> blocks) {
      Object var10000;
      if (blocks.isEmpty()) {
         var10000 = Nil$.MODULE$;
      } else {
         Null var10004 = .MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         Null var10012 = .MODULE$;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Blocks"));
         $buf.$amp$plus(new Elem((String)null, "h5", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         Seq x$24 = this.streamBlockTableHeader();
         Function1 x$25 = new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StoragePage $outer;

            public final Seq<Node> apply(Tuple2<String, Seq<StreamBlockData>> block) {
               return this.$outer.org$apache$spark$ui$storage$StoragePage$$streamBlockTableRow(block);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
         Some x$27 = new Some("storage-by-block-table");
         boolean x$28 = false;
         boolean x$29 = UIUtils$.MODULE$.listingTable$default$4();
         Seq x$30 = UIUtils$.MODULE$.listingTable$default$6();
         boolean x$31 = UIUtils$.MODULE$.listingTable$default$7();
         $buf.$amp$plus(UIUtils$.MODULE$.listingTable(x$24, x$25, blocks, x$29, x$27, x$30, x$31, x$28));
         $buf.$amp$plus(new Text("\n      "));
         var10000 = new Elem((String)null, "div", var10004, var10005, false, $buf);
      }

      return (Seq<Node>)var10000;
   }

   private Seq<String> streamBlockTableHeader() {
      return this.streamBlockTableHeader;
   }

   public Seq<Node> org$apache$spark$ui$storage$StoragePage$$streamBlockTableRow(Tuple2<String, Seq<StreamBlockData>> block) {
      Seq replications = (Seq)block._2();
      Predef$.MODULE$.assert(replications.nonEmpty());
      return replications.size() == 1 ? this.org$apache$spark$ui$storage$StoragePage$$streamBlockTableSubrow((String)block._1(), (StreamBlockData)replications.head(), replications.size(), true) : (Seq)this.org$apache$spark$ui$storage$StoragePage$$streamBlockTableSubrow((String)block._1(), (StreamBlockData)replications.head(), replications.size(), true).$plus$plus((GenTraversableOnce)((TraversableLike)replications.tail()).flatMap(new Serializable(this, block, replications) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StoragePage $outer;
         private final Tuple2 block$1;
         private final Seq replications$1;

         public final Seq<Node> apply(StreamBlockData x$4) {
            return this.$outer.org$apache$spark$ui$storage$StoragePage$$streamBlockTableSubrow((String)this.block$1._1(), x$4, this.replications$1.size(), false);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.block$1 = block$1;
               this.replications$1 = replications$1;
            }
         }
      }, Seq$.MODULE$.canBuildFrom()), Seq$.MODULE$.canBuildFrom());
   }

   public Seq<Node> org$apache$spark$ui$storage$StoragePage$$streamBlockTableSubrow(String blockId, StreamBlockData block, int replication, boolean firstSubrow) {
      Tuple2 var6 = this.streamBlockStorageLevelDescriptionAndSize(block);
      if (var6 != null) {
         String storageLevel = (String)var6._1();
         long size = var6._2$mcJ$sp();
         Tuple2 var10 = new Tuple2(storageLevel, BoxesRunTime.boxToLong(size));
         String storageLevel = (String)var10._1();
         long size = var10._2$mcJ$sp();
         Elem var10000 = new Elem;
         Null var10004 = .MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n      "));
         Object var10008;
         if (firstSubrow) {
            NodeBuffer $buf = new NodeBuffer();
            MetaData $md = .MODULE$;
            MetaData var23 = new UnprefixedAttribute("rowspan", BoxesRunTime.boxToInteger(replication).toString(), $md);
            TopScope var10014 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            $buf.$amp$plus(block.name());
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(new Elem((String)null, "td", var23, var10014, false, $buf));
            MetaData $md = .MODULE$;
            MetaData var24 = new UnprefixedAttribute("rowspan", BoxesRunTime.boxToInteger(replication).toString(), $md);
            var10014 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(replication).toString());
            $buf.$amp$plus(new Text("\n          "));
            $buf.$amp$plus(new Elem((String)null, "td", var24, var10014, false, $buf));
            var10008 = $buf;
         } else {
            var10008 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10008);
         $buf.$amp$plus(new Text("\n      "));
         Null var10012 = .MODULE$;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(block.hostPort());
         $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n      "));
         var10012 = .MODULE$;
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(storageLevel);
         $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n      "));
         var10012 = .MODULE$;
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(Utils$.MODULE$.bytesToString(size));
         $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n    "));
         var10000.<init>((String)null, "tr", var10004, var10005, false, $buf);
         return var10000;
      } else {
         throw new MatchError(var6);
      }
   }

   public Tuple2<String, Object> streamBlockStorageLevelDescriptionAndSize(StreamBlockData block) {
      Tuple2 var10000;
      if (block.useDisk()) {
         var10000 = new Tuple2("Disk", BoxesRunTime.boxToLong(block.diskSize()));
      } else if (block.useMemory() && block.deserialized()) {
         var10000 = new Tuple2("Memory", BoxesRunTime.boxToLong(block.memSize()));
      } else {
         if (!block.useMemory() || block.deserialized()) {
            throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid Storage Level: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{block.storageLevel()})));
         }

         var10000 = new Tuple2("Memory Serialized", BoxesRunTime.boxToLong(block.memSize()));
      }

      return var10000;
   }

   public StoragePage(SparkUITab parent, AppStatusStore store) {
      super("");
      this.parent = parent;
      this.store = store;
      this.rddHeader = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ID", "RDD Name", "Storage Level", "Cached Partitions", "Fraction Cached", "Size in Memory", "Size on Disk"})));
      this.executorMetricsTableHeader = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor ID", "Address", "Total Size in Memory", "Total Size on Disk", "Stream Blocks"})));
      this.streamBlockTableHeader = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ID", "Replication Level", "Location", "Storage Level", "Size"})));
   }
}
