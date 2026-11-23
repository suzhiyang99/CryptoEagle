package org.apache.spark.ui.storage;

import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.RDDDataDistribution;
import org.apache.spark.status.api.v1.RDDPartitionInfo;
import org.apache.spark.status.api.v1.RDDStorageInfo;
import org.apache.spark.ui.SparkUITab;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.ui.WebUIPage;
import org.apache.spark.util.Utils$;
import scala.Function1;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
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
   bytes = "\u0006\u0001\u00194Q!\u0001\u0002\u0001\t1\u0011qA\u0015#E!\u0006<WM\u0003\u0002\u0004\t\u000591\u000f^8sC\u001e,'BA\u0003\u0007\u0003\t)\u0018N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\t\u0001Q\u0002\u0005\u0002\u000f\u001f5\tA!\u0003\u0002\u0011\t\tIq+\u001a2V\u0013B\u000bw-\u001a\u0005\t%\u0001\u0011\t\u0011)A\u0005)\u00051\u0001/\u0019:f]R\u001c\u0001\u0001\u0005\u0002\u000f+%\u0011a\u0003\u0002\u0002\u000b'B\f'o[+J)\u0006\u0014\u0007\u0002\u0003\r\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u000bM$xN]3\u0011\u0005iiR\"A\u000e\u000b\u0005q1\u0011AB:uCR,8/\u0003\u0002\u001f7\tq\u0011\t\u001d9Ti\u0006$Xo]*u_J,\u0007\"\u0002\u0011\u0001\t\u0003\t\u0013A\u0002\u001fj]&$h\bF\u0002#I\u0015\u0002\"a\t\u0001\u000e\u0003\tAQAE\u0010A\u0002QAQ\u0001G\u0010A\u0002eAQa\n\u0001\u0005\u0002!\naA]3oI\u0016\u0014HCA\u0015>!\rQCg\u000e\b\u0003WEr!\u0001L\u0018\u000e\u00035R!AL\n\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0001\u0014!B:dC2\f\u0017B\u0001\u001a4\u0003\u001d\u0001\u0018mY6bO\u0016T\u0011\u0001M\u0005\u0003kY\u00121aU3r\u0015\t\u00114\u0007\u0005\u00029w5\t\u0011H\u0003\u0002;g\u0005\u0019\u00010\u001c7\n\u0005qJ$\u0001\u0002(pI\u0016DQA\u0010\u0014A\u0002}\nqA]3rk\u0016\u001cH\u000f\u0005\u0002A\u000f6\t\u0011I\u0003\u0002C\u0007\u0006!\u0001\u000e\u001e;q\u0015\t!U)A\u0004tKJ4H.\u001a;\u000b\u0003\u0019\u000bQA[1wCbL!\u0001S!\u0003%!#H\u000f]*feZdW\r\u001e*fcV,7\u000f\u001e\u0005\u0006\u0015\u0002!IaS\u0001\ro>\u00148.\u001a:IK\u0006$WM]\u000b\u0002\u0019B\u0019Q\nU)\u000e\u00039S!aT\u001a\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u00026\u001dB\u0011!kV\u0007\u0002'*\u0011A+V\u0001\u0005Y\u0006twMC\u0001W\u0003\u0011Q\u0017M^1\n\u0005a\u001b&AB*ue&tw\rC\u0003[\u0001\u0011%1,A\u0005x_J\\WM\u001d*poR\u0011\u0011\u0006\u0018\u0005\u0006;f\u0003\rAX\u0001\u0007o>\u00148.\u001a:\u0011\u0005}#W\"\u00011\u000b\u0005\u0005\u0014\u0017A\u0001<2\u0015\t\u00197$A\u0002ba&L!!\u001a1\u0003'I#E\tR1uC\u0012K7\u000f\u001e:jEV$\u0018n\u001c8"
)
public class RDDPage extends WebUIPage {
   private final SparkUITab parent;
   private final AppStatusStore store;

   public Seq<Node> render(HttpServletRequest request) {
      String parameterId = UIUtils$.MODULE$.stripXSS(request.getParameter("id"));
      Predef$.MODULE$.require(parameterId != null && (new StringOps(Predef$.MODULE$.augmentString(parameterId))).nonEmpty(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Missing id parameter";
         }
      });
      String parameterBlockPage = UIUtils$.MODULE$.stripXSS(request.getParameter("block.page"));
      String parameterBlockSortColumn = UIUtils$.MODULE$.stripXSS(request.getParameter("block.sort"));
      String parameterBlockSortDesc = UIUtils$.MODULE$.stripXSS(request.getParameter("block.desc"));
      String parameterBlockPageSize = UIUtils$.MODULE$.stripXSS(request.getParameter("block.pageSize"));
      String parameterBlockPrevPageSize = UIUtils$.MODULE$.stripXSS(request.getParameter("block.prevPageSize"));
      int blockPage = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(parameterBlockPage).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$1) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toInt();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 1;
         }
      }));
      String blockSortColumn = (String)Option$.MODULE$.apply(parameterBlockSortColumn).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Block Name";
         }
      });
      boolean blockSortDesc = BoxesRunTime.unboxToBoolean(Option$.MODULE$.apply(parameterBlockSortDesc).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$2) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$2))).toBoolean();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            return false;
         }
      }));
      int blockPageSize = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(parameterBlockPageSize).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$3) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$3))).toInt();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 100;
         }
      }));
      int blockPrevPageSize = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(parameterBlockPrevPageSize).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$4) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$4))).toInt();
         }
      }).getOrElse(new Serializable(this, blockPageSize) {
         public static final long serialVersionUID = 0L;
         private final int blockPageSize$1;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return this.blockPageSize$1;
         }

         public {
            this.blockPageSize$1 = blockPageSize$1;
         }
      }));
      int rddId = (new StringOps(Predef$.MODULE$.augmentString(parameterId))).toInt();

      RDDStorageInfo var10000;
      try {
         var10000 = this.store.rdd(rddId);
      } catch (NoSuchElementException var63) {
         return UIUtils$.MODULE$.headerSparkPage("RDD Not Found", new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Seq<Node> apply() {
               return (Seq)Seq$.MODULE$.empty();
            }
         }, this.parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7());
      }

      RDDStorageInfo rddStorageInfo = var10000;
      Seq x$12 = this.workerHeader();
      Function1 x$13 = new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RDDPage $outer;

         public final Seq<Node> apply(RDDDataDistribution worker) {
            return this.$outer.org$apache$spark$ui$storage$RDDPage$$workerRow(worker);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      Seq x$14 = (Seq)rddStorageInfo.dataDistribution().get();
      Some x$15 = new Some("rdd-storage-by-worker-table");
      boolean x$16 = UIUtils$.MODULE$.listingTable$default$4();
      Seq x$17 = UIUtils$.MODULE$.listingTable$default$6();
      boolean x$18 = UIUtils$.MODULE$.listingTable$default$7();
      boolean x$19 = UIUtils$.MODULE$.listingTable$default$8();
      Seq workerTable = UIUtils$.MODULE$.listingTable(x$12, x$13, x$14, x$16, x$15, x$17, x$18, x$19);
      int page = blockPageSize <= blockPrevPageSize ? blockPage : 1;

      try {
         BlockPagedTable _blockTable = new BlockPagedTable((new StringBuilder()).append((Object)UIUtils$.MODULE$.prependBaseUri(this.parent.basePath(), UIUtils$.MODULE$.prependBaseUri$default$2())).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/storage/rdd/?id=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(rddId)}))).toString(), (Seq)rddStorageInfo.partitions().get(), blockPageSize, blockSortColumn, blockSortDesc);
         var10000 = _blockTable.table(page);
      } catch (Throwable var64) {
         label100: {
            boolean var28;
            if (var64 instanceof IllegalArgumentException) {
               var28 = true;
            } else if (var64 instanceof IndexOutOfBoundsException) {
               var28 = true;
            } else {
               var28 = false;
            }

            if (!var28) {
               throw var64;
            }

            MetaData $md = .MODULE$;
            MetaData var65 = new UnprefixedAttribute("class", new Text("alert alert-error"), $md);
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(var64.getMessage());
            Elem var29 = new Elem((String)null, "div", var65, var10005, false, $buf);
            var10000 = var29;
            break label100;
         }
      }

      Seq blockTableHTML = var10000;
      Null var10004 = .MODULE$;
      TopScope var73 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(scala.xml.Unparsed..MODULE$.apply((new StringOps(Predef$.MODULE$.augmentString("\n              |$(function() {\n              |  if (/.*&block.sort=.*$/.test(location.search)) {\n              |    var topOffset = $(\"#blocks-section\").offset().top;\n              |    $(\"html,body\").animate({scrollTop: topOffset}, 200);\n              |  }\n              |});\n            "))).stripMargin()));
      $buf.$amp$plus(new Text("\n      "));
      Elem jsForScrollingDownToBlockTable = new Elem((String)null, "script", var10004, var73, false, $buf);
      NodeBuffer $buf = new NodeBuffer();
      MetaData $md = .MODULE$;
      MetaData var66 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      TopScope var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var67 = new UnprefixedAttribute("class", new Text("span12"), $md);
      TopScope var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      MetaData $md = .MODULE$;
      MetaData var68 = new UnprefixedAttribute("class", new Text("unstyled"), $md);
      TopScope var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            "));
      Null var10029 = .MODULE$;
      TopScope var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      Null var10037 = .MODULE$;
      TopScope var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Storage Level:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(rddStorageInfo.storageLevel());
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Cached Partitions:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(rddStorageInfo.numCachedPartitions()));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Total Partitions:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(rddStorageInfo.numPartitions()));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Memory Size:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(rddStorageInfo.memoryUsed()));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n            "));
      var10029 = .MODULE$;
      var10030 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n              "));
      var10037 = .MODULE$;
      var10038 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("Disk Size:"));
      $buf.$amp$plus(new Elem((String)null, "strong", var10037, var10038, false, $buf));
      $buf.$amp$plus(new Text("\n              "));
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(rddStorageInfo.diskUsed()));
      $buf.$amp$plus(new Text("\n            "));
      $buf.$amp$plus(new Elem((String)null, "li", var10029, var10030, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(new Elem((String)null, "ul", var68, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var67, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "div", var66, var10006, false, $buf));
      MetaData $md = .MODULE$;
      MetaData var69 = new UnprefixedAttribute("class", new Text("row-fluid"), $md);
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var70 = new UnprefixedAttribute("class", new Text("span12"), $md);
      var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      Null var10021 = .MODULE$;
      var10022 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n            Data Distribution on "));
      $buf.$amp$plus(rddStorageInfo.dataDistribution().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Seq<RDDDataDistribution> x$5) {
            return x$5.size();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }));
      $buf.$amp$plus(new Text("\n            Executors\n          "));
      $buf.$amp$plus(new Elem((String)null, "h4", var10021, var10022, false, $buf));
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(workerTable);
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(new Elem((String)null, "div", var70, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "div", var69, var10006, false, $buf));
      Null var74 = .MODULE$;
      var10006 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = .MODULE$;
      MetaData var71 = new UnprefixedAttribute("id", new Text("blocks-section"), $md);
      var10014 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n          "));
      $buf.$amp$plus(rddStorageInfo.partitions().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Seq<RDDPartitionInfo> x$6) {
            return x$6.size();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }));
      $buf.$amp$plus(new Text(" Partitions\n        "));
      $buf.$amp$plus(new Elem((String)null, "h4", var71, var10014, false, $buf));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(blockTableHTML.$plus$plus(jsForScrollingDownToBlockTable, Seq$.MODULE$.canBuildFrom()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "div", var74, var10006, false, $buf));
      return UIUtils$.MODULE$.headerSparkPage((new StringBuilder()).append((Object)"RDD Storage Info for ").append((Object)rddStorageInfo.name()).toString(), new Serializable(this, $buf) {
         public static final long serialVersionUID = 0L;
         private final NodeBuffer content$1;

         public final NodeBuffer apply() {
            return this.content$1;
         }

         public {
            this.content$1 = content$1;
         }
      }, this.parent, UIUtils$.MODULE$.headerSparkPage$default$4(), UIUtils$.MODULE$.headerSparkPage$default$5(), UIUtils$.MODULE$.headerSparkPage$default$6(), UIUtils$.MODULE$.headerSparkPage$default$7());
   }

   private Seq<String> workerHeader() {
      return (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Host", "On Heap Memory Usage", "Off Heap Memory Usage", "Disk Usage"})));
   }

   public Seq<Node> org$apache$spark$ui$storage$RDDPage$$workerRow(RDDDataDistribution worker) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = .MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(worker.address());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(BoxesRunTime.unboxToLong(worker.onHeapMemoryUsed().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }))));
      $buf.$amp$plus(new Text("\n        ("));
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(BoxesRunTime.unboxToLong(worker.onHeapMemoryRemaining().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }))));
      $buf.$amp$plus(new Text(" Remaining)\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(BoxesRunTime.unboxToLong(worker.offHeapMemoryUsed().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }))));
      $buf.$amp$plus(new Text("\n        ("));
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(BoxesRunTime.unboxToLong(worker.offHeapMemoryRemaining().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }))));
      $buf.$amp$plus(new Text(" Remaining)\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = .MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(worker.diskUsed()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   public RDDPage(SparkUITab parent, AppStatusStore store) {
      super("rdd");
      this.parent = parent;
      this.store = store;
   }
}
