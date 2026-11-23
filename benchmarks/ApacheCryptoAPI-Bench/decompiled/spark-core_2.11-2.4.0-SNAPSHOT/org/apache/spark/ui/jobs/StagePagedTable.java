package org.apache.spark.ui.jobs;

import java.net.URLEncoder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.RDDStorageInfo;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.ui.PagedTable;
import org.apache.spark.ui.PagedTable$class;
import org.apache.spark.ui.ToolTips$;
import org.apache.spark.ui.UIUtils$;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.xml.Elem;
import scala.xml.EntityRef;
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
   bytes = "\u0006\u0001\u0005\u001de!B\u0001\u0003\u0001\u0011a!aD*uC\u001e,\u0007+Y4fIR\u000b'\r\\3\u000b\u0005\r!\u0011\u0001\u00026pENT!!\u0002\u0004\u0002\u0005UL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0007\u0001i1\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0004)U9R\"\u0001\u0003\n\u0005Y!!A\u0003)bO\u0016$G+\u00192mKB\u0011\u0001$G\u0007\u0002\u0005%\u0011!D\u0001\u0002\u0012'R\fw-\u001a+bE2,'k\\<ECR\f\u0007\u0002\u0003\u000f\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0010\u0002\u000bM$xN]3\u0004\u0001A\u0011qDI\u0007\u0002A)\u0011\u0011EB\u0001\u0007gR\fG/^:\n\u0005\r\u0002#AD!qaN#\u0018\r^;t'R|'/\u001a\u0005\tK\u0001\u0011\t\u0011)A\u0005M\u000511\u000f^1hKN\u00042aJ\u00183\u001d\tASF\u0004\u0002*Y5\t!F\u0003\u0002,;\u00051AH]8pizJ\u0011\u0001E\u0005\u0003]=\tq\u0001]1dW\u0006<W-\u0003\u00021c\t\u00191+Z9\u000b\u00059z\u0001CA\u001a9\u001b\u0005!$BA\u001b7\u0003\t1\u0018G\u0003\u00028A\u0005\u0019\u0011\r]5\n\u0005e\"$!C*uC\u001e,G)\u0019;b\u0011!Y\u0004A!A!\u0002\u0013a\u0014!\u0004;bE2,\u0007*Z1eKJLE\r\u0005\u0002>\u0001:\u0011aBP\u0005\u0003\u007f=\ta\u0001\u0015:fI\u00164\u0017BA!C\u0005\u0019\u0019FO]5oO*\u0011qh\u0004\u0005\t\t\u0002\u0011\t\u0011)A\u0005y\u0005A1\u000f^1hKR\u000bw\r\u0003\u0005G\u0001\t\u0005\t\u0015!\u0003=\u0003!\u0011\u0017m]3QCRD\u0007\u0002\u0003%\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001f\u0002\u000fM,(\rU1uQ\"A!\n\u0001B\u0001B\u0003%1*A\bjg\u001a\u000b\u0017N]*dQ\u0016$W\u000f\\3s!\tqA*\u0003\u0002N\u001f\t9!i\\8mK\u0006t\u0007\u0002C(\u0001\u0005\u0003\u0005\u000b\u0011B&\u0002\u0017-LG\u000e\\#oC\ndW\r\u001a\u0005\t#\u0002\u0011\t\u0011)A\u0005%\u0006Y1-\u001e:sK:$H+[7f!\tq1+\u0003\u0002U\u001f\t!Aj\u001c8h\u0011!1\u0006A!A!\u0002\u00139\u0016\u0001\u00039bO\u0016\u001c\u0016N_3\u0011\u00059A\u0016BA-\u0010\u0005\rIe\u000e\u001e\u0005\t7\u0002\u0011\t\u0011)A\u0005y\u0005Q1o\u001c:u\u0007>dW/\u001c8\t\u0011u\u0003!\u0011!Q\u0001\n-\u000bA\u0001Z3tG\"Aq\f\u0001B\u0001B\u0003%1*A\u0007jg\u001a\u000b\u0017\u000e\\3e'R\fw-\u001a\u0005\tC\u0002\u0011\t\u0011)A\u0005E\u0006\u0019\u0002/\u0019:b[\u0016$XM](uQ\u0016\u0014H+\u00192mKB\u0019qe\u0019\u001f\n\u0005\u0011\f$\u0001C%uKJ\f'\r\\3\t\u000b\u0019\u0004A\u0011A4\u0002\rqJg.\u001b;?)=A\u0017N[6m[:|\u0007/\u001d:tiV4\bC\u0001\r\u0001\u0011\u0015aR\r1\u0001\u001f\u0011\u0015)S\r1\u0001'\u0011\u0015YT\r1\u0001=\u0011\u0015!U\r1\u0001=\u0011\u00151U\r1\u0001=\u0011\u0015AU\r1\u0001=\u0011\u0015QU\r1\u0001L\u0011\u0015yU\r1\u0001L\u0011\u0015\tV\r1\u0001S\u0011\u00151V\r1\u0001X\u0011\u0015YV\r1\u0001=\u0011\u0015iV\r1\u0001L\u0011\u0015yV\r1\u0001L\u0011\u0015\tW\r1\u0001c\u0011\u0015A\b\u0001\"\u0011z\u0003\u001d!\u0018M\u00197f\u0013\u0012,\u0012\u0001\u0010\u0005\u0006w\u0002!\t%_\u0001\u000ei\u0006\u0014G.Z\"tg\u000ec\u0017m]:\t\u000bu\u0004A\u0011I=\u0002#A\fw-Z*ju\u00164uN]7GS\u0016dG\rC\u0003\u0000\u0001\u0011\u0005\u00130A\u000bqe\u00164\b+Y4f'&TXMR8s[\u001aKW\r\u001c3\t\r\u0005\r\u0001\u0001\"\u0011z\u0003M\u0001\u0018mZ3Ok6\u0014WM\u001d$pe64\u0015.\u001a7e\u0011%\t9\u0001\u0001b\u0001\n\u0003\tI!A\u0007qCJ\fW.\u001a;feB\u000bG\u000f[\u000b\u0003\u0003\u0017\u0001B!!\u0004\u0002\u00185\u0011\u0011q\u0002\u0006\u0005\u0003#\t\u0019\"\u0001\u0003mC:<'BAA\u000b\u0003\u0011Q\u0017M^1\n\u0007\u0005\u000by\u0001\u0003\u0005\u0002\u001c\u0001\u0001\u000b\u0011BA\u0006\u00039\u0001\u0018M]1nKR,'\u000fU1uQ\u0002B\u0011\"a\b\u0001\u0005\u0004%\t%!\t\u0002\u0015\u0011\fG/Y*pkJ\u001cW-\u0006\u0002\u0002$A\u0019\u0001$!\n\n\u0007\u0005\u001d\"AA\bTi\u0006<W\rR1uCN{WO]2f\u0011!\tY\u0003\u0001Q\u0001\n\u0005\r\u0012a\u00033bi\u0006\u001cv.\u001e:dK\u0002Bq!a\f\u0001\t\u0003\n\t$\u0001\u0005qC\u001e,G*\u001b8l)\ra\u00141\u0007\u0005\b\u0003k\ti\u00031\u0001X\u0003\u0011\u0001\u0018mZ3\t\r\u0005e\u0002\u0001\"\u0011z\u0003A9wNQ;ui>tgi\u001c:n!\u0006$\b\u000eC\u0004\u0002>\u0001!\t%a\u0010\u0002\u000f!,\u0017\rZ3sgV\u0011\u0011\u0011\t\t\u0005O=\n\u0019\u0005\u0005\u0003\u0002F\u0005-SBAA$\u0015\r\tIeD\u0001\u0004q6d\u0017\u0002BA'\u0003\u000f\u0012AAT8eK\"9\u0011\u0011\u000b\u0001\u0005B\u0005M\u0013a\u0001:poR!\u0011\u0011IA+\u0011\u001d\t9&a\u0014A\u0002]\tA\u0001Z1uC\"9\u00111\f\u0001\u0005\n\u0005u\u0013A\u0003:po\u000e{g\u000e^3oiR!\u0011\u0011IA0\u0011\u001d\t9&!\u0017A\u0002]Aq!a\u0019\u0001\t\u0013\t)'A\tgC&dWO]3SK\u0006\u001cxN\u001c%u[2$B!!\u0011\u0002h!9\u0011\u0011NA1\u0001\u0004\u0011\u0014!A:\t\u000f\u00055\u0004\u0001\"\u0003\u0002p\u0005yQ.Y6f\t\u0016\u001c8M]5qi&|g\u000e\u0006\u0004\u0002B\u0005E\u00141\u000f\u0005\b\u0003S\nY\u00071\u00013\u0011!\t)(a\u001bA\u0002\u0005]\u0014!\u00053fg\u000e\u0014\u0018\u000e\u001d;j_:|\u0005\u000f^5p]B!a\"!\u001f=\u0013\r\tYh\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u000f\u0005}\u0004\u0001\"\u0005\u0002\u0002\u0006yQ.[:tS:<7\u000b^1hKJ{w\u000f\u0006\u0003\u0002B\u0005\r\u0005bBAC\u0003{\u0002\raV\u0001\bgR\fw-Z%e\u0001"
)
public class StagePagedTable implements PagedTable<StageTableRowData> {
   private final AppStatusStore store;
   public final String org$apache$spark$ui$jobs$StagePagedTable$$tableHeaderId;
   public final String org$apache$spark$ui$jobs$StagePagedTable$$stageTag;
   private final String basePath;
   private final boolean isFairScheduler;
   private final boolean killEnabled;
   public final int org$apache$spark$ui$jobs$StagePagedTable$$pageSize;
   public final String org$apache$spark$ui$jobs$StagePagedTable$$sortColumn;
   public final boolean org$apache$spark$ui$jobs$StagePagedTable$$desc;
   private final boolean isFailedStage;
   private final String parameterPath;
   private final StageDataSource dataSource;

   public Seq<Node> table(int page) {
      return PagedTable$class.table(this, page);
   }

   public Seq<Node> pageNavigation(int page, int pageSize, int totalPages) {
      return PagedTable$class.pageNavigation(this, page, pageSize, totalPages);
   }

   public String tableId() {
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$jobs$StagePagedTable$$stageTag).append((Object)"-table").toString();
   }

   public String tableCssClass() {
      return "table table-bordered table-condensed table-striped table-head-clickable table-cell-width-limited";
   }

   public String pageSizeFormField() {
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$jobs$StagePagedTable$$stageTag).append((Object)".pageSize").toString();
   }

   public String prevPageSizeFormField() {
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$jobs$StagePagedTable$$stageTag).append((Object)".prevPageSize").toString();
   }

   public String pageNumberFormField() {
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$jobs$StagePagedTable$$stageTag).append((Object)".page").toString();
   }

   public String parameterPath() {
      return this.parameterPath;
   }

   public StageDataSource dataSource() {
      return this.dataSource;
   }

   public String pageLink(int page) {
      String encodedSortColumn = URLEncoder.encode(this.org$apache$spark$ui$jobs$StagePagedTable$$sortColumn, "UTF-8");
      return (new StringBuilder()).append((Object)this.parameterPath()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.pageNumberFormField(), BoxesRunTime.boxToInteger(page)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$StagePagedTable$$stageTag, encodedSortColumn}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$StagePagedTable$$stageTag, BoxesRunTime.boxToBoolean(this.org$apache$spark$ui$jobs$StagePagedTable$$desc)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.pageSizeFormField(), BoxesRunTime.boxToInteger(this.org$apache$spark$ui$jobs$StagePagedTable$$pageSize)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"#", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$StagePagedTable$$tableHeaderId}))).toString();
   }

   public String goButtonFormPath() {
      String encodedSortColumn = URLEncoder.encode(this.org$apache$spark$ui$jobs$StagePagedTable$$sortColumn, "UTF-8");
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "&", ".sort=", "&", ".desc=", "#", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.parameterPath(), this.org$apache$spark$ui$jobs$StagePagedTable$$stageTag, encodedSortColumn, this.org$apache$spark$ui$jobs$StagePagedTable$$stageTag, BoxesRunTime.boxToBoolean(this.org$apache$spark$ui$jobs$StagePagedTable$$desc), this.org$apache$spark$ui$jobs$StagePagedTable$$tableHeaderId}));
   }

   public Seq<Node> headers() {
      Seq stageHeadersAndCssClasses = (Seq)((TraversableLike)((TraversableLike)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple3[]{new Tuple3("Stage Id", None$.MODULE$, BoxesRunTime.boxToBoolean(true))}))).$plus$plus(this.isFairScheduler ? Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple3[]{new Tuple3("Pool Name", None$.MODULE$, BoxesRunTime.boxToBoolean(true))})) : Seq$.MODULE$.empty(), Seq$.MODULE$.canBuildFrom())).$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple3[]{new Tuple3("Description", None$.MODULE$, BoxesRunTime.boxToBoolean(true)), new Tuple3("Submitted", None$.MODULE$, BoxesRunTime.boxToBoolean(true)), new Tuple3("Duration", None$.MODULE$, BoxesRunTime.boxToBoolean(true)), new Tuple3("Tasks: Succeeded/Total", None$.MODULE$, BoxesRunTime.boxToBoolean(false)), new Tuple3("Input", new Some(new Tuple2(ToolTips$.MODULE$.INPUT(), BoxesRunTime.boxToBoolean(false))), BoxesRunTime.boxToBoolean(true)), new Tuple3("Output", new Some(new Tuple2(ToolTips$.MODULE$.OUTPUT(), BoxesRunTime.boxToBoolean(false))), BoxesRunTime.boxToBoolean(true)), new Tuple3("Shuffle Read", new Some(new Tuple2(ToolTips$.MODULE$.SHUFFLE_READ(), BoxesRunTime.boxToBoolean(false))), BoxesRunTime.boxToBoolean(true)), new Tuple3("Shuffle Write", new Some(new Tuple2(ToolTips$.MODULE$.SHUFFLE_WRITE(), BoxesRunTime.boxToBoolean(true))), BoxesRunTime.boxToBoolean(true))})), Seq$.MODULE$.canBuildFrom())).$plus$plus(this.isFailedStage ? Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple3[]{new Tuple3("Failure Reason", None$.MODULE$, BoxesRunTime.boxToBoolean(false))})) : Seq$.MODULE$.empty(), Seq$.MODULE$.canBuildFrom());
      if (((SeqLike)((TraversableLike)stageHeadersAndCssClasses.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple3<String, Option<Tuple2<String, Object>>, Object> x$7) {
            return BoxesRunTime.unboxToBoolean(x$7._3());
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple3<String, Option<Tuple2<String, Object>>, Object> x$8) {
            return (String)x$8._1();
         }
      }, Seq$.MODULE$.canBuildFrom())).contains(this.org$apache$spark$ui$jobs$StagePagedTable$$sortColumn)) {
         Seq headerRow = (Seq)stageHeadersAndCssClasses.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StagePagedTable $outer;

            public final Elem apply(Tuple3<String, Option<Tuple2<String, Object>>, Object> x0$1) {
               if (x0$1 == null) {
                  throw new MatchError(x0$1);
               } else {
                  Elem var10000;
                  label33: {
                     String header;
                     boolean sortable;
                     Object headerSpan;
                     label37: {
                        header = (String)x0$1._1();
                        Option tooltip = (Option)x0$1._2();
                        sortable = BoxesRunTime.unboxToBoolean(x0$1._3());
                        headerSpan = tooltip.map(new Serializable(this, header) {
                           public static final long serialVersionUID = 0L;
                           private final String header$1;

                           public final Elem apply(Tuple2<String, Object> x0$2) {
                              if (x0$2 != null) {
                                 String title = (String)x0$2._1();
                                 boolean left = x0$2._2$mcZ$sp();
                                 Elem var10000;
                                 if (left) {
                                    MetaData $md = .MODULE$;
                                    MetaData var10 = new UnprefixedAttribute("title", title, $md);
                                    var10 = new UnprefixedAttribute("data-placement", new Text("left"), var10);
                                    var10 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var10);
                                    TopScope var10005 = scala.xml.TopScope..MODULE$;
                                    NodeBuffer $buf = new NodeBuffer();
                                    $buf.$amp$plus(new Text("\n              "));
                                    $buf.$amp$plus(this.header$1);
                                    $buf.$amp$plus(new Text("\n            "));
                                    var10000 = new Elem((String)null, "span", var10, var10005, false, $buf);
                                 } else {
                                    MetaData $md = .MODULE$;
                                    MetaData var13 = new UnprefixedAttribute("title", title, $md);
                                    var13 = new UnprefixedAttribute("data-toggle", new Text("tooltip"), var13);
                                    TopScope var15 = scala.xml.TopScope..MODULE$;
                                    NodeBuffer $buf = new NodeBuffer();
                                    $buf.$amp$plus(new Text("\n              "));
                                    $buf.$amp$plus(this.header$1);
                                    $buf.$amp$plus(new Text("\n            "));
                                    var10000 = new Elem((String)null, "span", var13, var15, false, $buf);
                                 }

                                 Elem var5 = var10000;
                                 return var5;
                              } else {
                                 throw new MatchError(x0$2);
                              }
                           }

                           public {
                              this.header$1 = header$1;
                           }
                        }).getOrElse(new Serializable(this, header) {
                           public static final long serialVersionUID = 0L;
                           private final String header$1;

                           public final String apply() {
                              return this.header$1;
                           }

                           public {
                              this.header$1 = header$1;
                           }
                        });
                        String var8 = this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$sortColumn;
                        if (header == null) {
                           if (var8 != null) {
                              break label37;
                           }
                        } else if (!header.equals(var8)) {
                           break label37;
                        }

                        String headerLink = Predef.any2stringadd$.MODULE$.$plus$extension(Predef$.MODULE$.any2stringadd(scala.xml.Unparsed..MODULE$.apply((new StringBuilder()).append((Object)this.$outer.parameterPath()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$stageTag, URLEncoder.encode(header, "UTF-8")}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$stageTag, BoxesRunTime.boxToBoolean(!this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$desc)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".pageSize=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$stageTag, BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$pageSize)}))).toString())), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"#", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$tableHeaderId})));
                        String arrow = this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$desc ? "&#x25BE;" : "&#x25B4;";
                        Null var10004 = .MODULE$;
                        TopScope var10005 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n            "));
                        MetaData $md = .MODULE$;
                        MetaData var20 = new UnprefixedAttribute("href", headerLink, $md);
                        TopScope var10013 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n              "));
                        $buf.$amp$plus(headerSpan);
                        Null var10020 = .MODULE$;
                        TopScope var10021 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n              "));
                        $buf.$amp$plus(new EntityRef("nbsp"));
                        $buf.$amp$plus(scala.xml.Unparsed..MODULE$.apply(arrow));
                        $buf.$amp$plus(new Text("\n            "));
                        $buf.$amp$plus(new Elem((String)null, "span", var10020, var10021, false, $buf));
                        $buf.$amp$plus(new Text("\n            "));
                        $buf.$amp$plus(new Elem((String)null, "a", var20, var10013, false, $buf));
                        $buf.$amp$plus(new Text("\n          "));
                        var10000 = new Elem((String)null, "th", var10004, var10005, false, $buf);
                        break label33;
                     }

                     if (sortable) {
                        String headerLink = Predef.any2stringadd$.MODULE$.$plus$extension(Predef$.MODULE$.any2stringadd(scala.xml.Unparsed..MODULE$.apply((new StringBuilder()).append((Object)this.$outer.parameterPath()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$stageTag, URLEncoder.encode(header, "UTF-8")}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".pageSize=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$stageTag, BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$pageSize)}))).toString())), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"#", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$StagePagedTable$$tableHeaderId})));
                        Null var22 = .MODULE$;
                        TopScope var24 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n              "));
                        MetaData $md = .MODULE$;
                        MetaData var21 = new UnprefixedAttribute("href", headerLink, $md);
                        TopScope var26 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n                "));
                        $buf.$amp$plus(headerSpan);
                        $buf.$amp$plus(new Text("\n              "));
                        $buf.$amp$plus(new Elem((String)null, "a", var21, var26, false, $buf));
                        $buf.$amp$plus(new Text("\n            "));
                        var10000 = new Elem((String)null, "th", var22, var24, false, $buf);
                     } else {
                        Null var23 = .MODULE$;
                        TopScope var25 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n              "));
                        $buf.$amp$plus(headerSpan);
                        $buf.$amp$plus(new Text("\n            "));
                        var10000 = new Elem((String)null, "th", var23, var25, false, $buf);
                     }
                  }

                  Elem var6 = var10000;
                  return var6;
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, Seq$.MODULE$.canBuildFrom());
         Null var10004 = .MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(headerRow);
         return new Elem((String)null, "thead", var10004, var10005, false, $buf);
      } else {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$StagePagedTable$$sortColumn})));
      }
   }

   public Seq<Node> row(StageTableRowData data) {
      MetaData $md = .MODULE$;
      MetaData var4 = new UnprefixedAttribute("id", (new StringBuilder()).append((Object)"stage-").append((Object)BoxesRunTime.boxToInteger(data.stageId())).append((Object)"-").append((Object)BoxesRunTime.boxToInteger(data.attemptId())).toString(), $md);
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(this.rowContent(data));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var4, var10005, false, $buf);
   }

   private Seq<Node> rowContent(StageTableRowData data) {
      Option var2 = data.option();
      Seq var3;
      if (None$.MODULE$.equals(var2)) {
         var3 = this.missingStageRow(data.stageId());
      } else {
         if (!(var2 instanceof Some)) {
            throw new MatchError(var2);
         }

         Some var4 = (Some)var2;
         StageData stageData = (StageData)var4.x();
         StageData info = data.stage();
         Elem var10000;
         if (data.attemptId() > 0) {
            Null var10004 = .MODULE$;
            TopScope var10005 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(BoxesRunTime.boxToInteger(data.stageId()));
            $buf.$amp$plus(new Text(" (retry "));
            $buf.$amp$plus(BoxesRunTime.boxToInteger(data.attemptId()));
            $buf.$amp$plus(new Text(")"));
            var10000 = new Elem((String)null, "td", var10004, var10005, false, $buf);
         } else {
            Null var27 = .MODULE$;
            TopScope var28 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(BoxesRunTime.boxToInteger(data.stageId()));
            var10000 = new Elem((String)null, "td", var27, var28, false, $buf);
         }

         Object var10001;
         if (this.isFairScheduler) {
            Null var29 = .MODULE$;
            TopScope var10006 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            MetaData $md = .MODULE$;
            MetaData var23 = new UnprefixedAttribute("href", (new StringOps(Predef$.MODULE$.augmentString("%s/stages/pool?poolname=%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{UIUtils$.MODULE$.prependBaseUri(this.basePath, UIUtils$.MODULE$.prependBaseUri$default$2()), data.schedulingPool()})), $md);
            TopScope var10014 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(data.schedulingPool());
            $buf.$amp$plus(new Text("\n            "));
            $buf.$amp$plus(new Elem((String)null, "a", var23, var10014, false, $buf));
            $buf.$amp$plus(new Text("\n          "));
            var10001 = new Elem((String)null, "td", var29, var10006, false, $buf);
         } else {
            var10001 = Seq$.MODULE$.empty();
         }

         TraversableLike var26 = (TraversableLike)var10000.$plus$plus((GenTraversableOnce)var10001, scala.xml.NodeSeq..MODULE$.canBuildFrom());
         NodeBuffer $buf = new NodeBuffer();
         Null var30 = .MODULE$;
         TopScope var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(this.makeDescription(info, data.descriptionOption()));
         $buf.$amp$plus(new Elem((String)null, "td", var30, var10007, false, $buf));
         MetaData $md = .MODULE$;
         MetaData var24 = new UnprefixedAttribute("valign", new Text("middle"), $md);
         var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(data.formattedSubmissionTime());
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "td", var24, var10007, false, $buf));
         var30 = .MODULE$;
         var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(data.formattedDuration());
         $buf.$amp$plus(new Elem((String)null, "td", var30, var10007, false, $buf));
         MetaData $md = .MODULE$;
         MetaData var25 = new UnprefixedAttribute("class", new Text("progress-cell"), $md);
         var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(UIUtils$.MODULE$.makeProgressBar(stageData.numActiveTasks(), stageData.numCompleteTasks(), stageData.numFailedTasks(), 0, stageData.killedTasksSummary(), info.numTasks()));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "td", var25, var10007, false, $buf));
         var30 = .MODULE$;
         var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(data.inputReadWithUnit());
         $buf.$amp$plus(new Elem((String)null, "td", var30, var10007, false, $buf));
         var30 = .MODULE$;
         var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(data.outputWriteWithUnit());
         $buf.$amp$plus(new Elem((String)null, "td", var30, var10007, false, $buf));
         var30 = .MODULE$;
         var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(data.shuffleReadWithUnit());
         $buf.$amp$plus(new Elem((String)null, "td", var30, var10007, false, $buf));
         var30 = .MODULE$;
         var10007 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(data.shuffleWriteWithUnit());
         $buf.$amp$plus(new Elem((String)null, "td", var30, var10007, false, $buf));
         var3 = (Seq)((TraversableLike)var26.$plus$plus($buf, scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus((GenTraversableOnce)(this.isFailedStage ? this.failureReasonHtml(info) : Seq$.MODULE$.empty()), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      }

      return var3;
   }

   private Seq<Node> failureReasonHtml(StageData s) {
      String failureReason = (String)s.failureReason().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      boolean isMultiline = failureReason.indexOf(10) >= 0;
      String failureReasonSummary = StringEscapeUtils.escapeHtml4(isMultiline ? failureReason.substring(0, failureReason.indexOf(10)) : failureReason);
      Object var17;
      if (isMultiline) {
         MetaData $md = .MODULE$;
         MetaData var13 = new UnprefixedAttribute("class", new Text("expand-details"), $md);
         var13 = new UnprefixedAttribute("onclick", new Text("this.parentNode.querySelector('.stacktrace-details').classList.toggle('collapsed')"), var13);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        +details\n      "));
         Elem var10000 = new Elem((String)null, "span", var13, var10005, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var15 = new UnprefixedAttribute("class", new Text("stacktrace-details collapsed"), $md);
         TopScope var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         Null var10013 = .MODULE$;
         TopScope var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(failureReason);
         $buf.$amp$plus(new Elem((String)null, "pre", var10013, var10014, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         var17 = var10000.$plus$plus(new Elem((String)null, "div", var15, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      } else {
         var17 = "";
      }

      Object details = var17;
      MetaData $md = .MODULE$;
      MetaData var16 = new UnprefixedAttribute("valign", new Text("middle"), $md);
      TopScope var18 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(failureReasonSummary);
      $buf.$amp$plus(details);
      return new Elem((String)null, "td", var16, var18, false, $buf);
   }

   private Seq<Node> makeDescription(StageData s, Option<String> descriptionOption) {
      String basePathUri = UIUtils$.MODULE$.prependBaseUri(this.basePath, UIUtils$.MODULE$.prependBaseUri$default$2());
      Object var10000;
      if (this.killEnabled) {
         String confirm = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"if (window.confirm('Are you sure you want to kill stage ", " ?')) "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(s.stageId())}))).append((Object)"{ this.parentNode.submit(); return true; } else { return false; }").toString();
         String killLinkUri = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/stages/stage/kill/?id=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePathUri, BoxesRunTime.boxToInteger(s.stageId())}));
         MetaData $md = .MODULE$;
         MetaData var22 = new UnprefixedAttribute("class", new Text("kill-link"), $md);
         var22 = new UnprefixedAttribute("onclick", confirm, var22);
         var22 = new UnprefixedAttribute("href", killLinkUri, var22);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("(kill)"));
         var10000 = new Elem((String)null, "a", var22, var10005, false, $buf);
      } else {
         var10000 = (Seq)Seq$.MODULE$.empty();
      }

      Seq killLink = (Seq)var10000;
      String nameLinkUri = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/stages/stage?id=", "&attempt=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePathUri, BoxesRunTime.boxToInteger(s.stageId()), BoxesRunTime.boxToInteger(s.attemptId())}));
      MetaData $md = .MODULE$;
      MetaData var25 = new UnprefixedAttribute("class", new Text("name-link"), $md);
      var25 = new UnprefixedAttribute("href", nameLinkUri, var25);
      TopScope var32 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(s.name());
      Elem nameLink = new Elem((String)null, "a", var25, var32, false, $buf);
      Seq cachedRddInfos = (Seq)this.store.rddList(this.store.rddList$default$1()).filter(new Serializable(this, s) {
         public static final long serialVersionUID = 0L;
         private final StageData s$1;

         public final boolean apply(RDDStorageInfo rdd) {
            return this.s$1.rddIds().contains(BoxesRunTime.boxToInteger(rdd.id()));
         }

         public {
            this.s$1 = s$1;
         }
      });
      if (s.details() != null && (new StringOps(Predef$.MODULE$.augmentString(s.details()))).nonEmpty()) {
         MetaData $md = .MODULE$;
         MetaData var27 = new UnprefixedAttribute("class", new Text("expand-details"), $md);
         var27 = new UnprefixedAttribute("onclick", new Text("this.parentNode.querySelector('.stage-details').classList.toggle('collapsed')"), var27);
         var32 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        +details\n      "));
         Elem var31 = new Elem((String)null, "span", var27, var32, false, $buf);
         MetaData $md = .MODULE$;
         MetaData var29 = new UnprefixedAttribute("class", new Text("stage-details collapsed"), $md);
         TopScope var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(cachedRddInfos.nonEmpty() ? scala.xml.Text..MODULE$.apply("RDD: ").$plus$plus((GenTraversableOnce)cachedRddInfos.map(new Serializable(this, basePathUri) {
            public static final long serialVersionUID = 0L;
            private final String basePathUri$1;

            public final Elem apply(RDDStorageInfo i) {
               MetaData $md = .MODULE$;
               MetaData var4 = new UnprefixedAttribute("href", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/storage/rdd?id=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.basePathUri$1, BoxesRunTime.boxToInteger(i.id())})), $md);
               TopScope var10005 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(i.name());
               return new Elem((String)null, "a", var4, var10005, false, $buf);
            }

            public {
               this.basePathUri$1 = basePathUri$1;
            }
         }, Seq$.MODULE$.canBuildFrom()), scala.xml.NodeSeq..MODULE$.canBuildFrom()) : BoxedUnit.UNIT);
         $buf.$amp$plus(new Text("\n        "));
         Null var10013 = .MODULE$;
         TopScope var10014 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(s.details());
         $buf.$amp$plus(new Elem((String)null, "pre", var10013, var10014, false, $buf));
         $buf.$amp$plus(new Text("\n      "));
         var10000 = var31.$plus$plus(new Elem((String)null, "div", var29, var10006, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom());
      } else {
         var10000 = BoxedUnit.UNIT;
      }

      Object details = var10000;
      Option stageDesc = descriptionOption.map(new Serializable(this, basePathUri) {
         public static final long serialVersionUID = 0L;
         private final String basePathUri$1;

         public final NodeSeq apply(String x$9) {
            return UIUtils$.MODULE$.makeDescription(x$9, this.basePathUri$1, UIUtils$.MODULE$.makeDescription$default$3());
         }

         public {
            this.basePathUri$1 = basePathUri$1;
         }
      });
      Null var10004 = .MODULE$;
      var32 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(stageDesc.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      }));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(killLink);
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(nameLink);
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(details);
      return new Elem((String)null, "div", var10004, var32, false, $buf);
   }

   public Seq<Node> missingStageRow(int stageId) {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(BoxesRunTime.boxToInteger(stageId));
      Elem var10000 = new Elem((String)null, "td", var10004, var10005, false, $buf);
      Object var10001;
      if (this.isFairScheduler) {
         Null var6 = .MODULE$;
         TopScope var10006 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("-"));
         var10001 = new Elem((String)null, "td", var6, var10006, false, $buf);
      } else {
         var10001 = Seq$.MODULE$.empty();
      }

      TraversableLike var5 = (TraversableLike)var10000.$plus$plus((GenTraversableOnce)var10001, scala.xml.NodeSeq..MODULE$.canBuildFrom());
      Null var7 = .MODULE$;
      TopScope var8 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("No data available for this stage"));
      return (Seq)((TraversableLike)((TraversableLike)((TraversableLike)((TraversableLike)((TraversableLike)((TraversableLike)((TraversableLike)var5.$plus$plus(new Elem((String)null, "td", var7, var8, false, $buf), scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(new Elem((String)null, "td", .MODULE$, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(new Elem((String)null, "td", .MODULE$, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(new Elem((String)null, "td", .MODULE$, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(new Elem((String)null, "td", .MODULE$, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(new Elem((String)null, "td", .MODULE$, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(new Elem((String)null, "td", .MODULE$, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom())).$plus$plus(new Elem((String)null, "td", .MODULE$, scala.xml.TopScope..MODULE$, false, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))), scala.xml.NodeSeq..MODULE$.canBuildFrom());
   }

   public StagePagedTable(AppStatusStore store, Seq<StageData> stages, String tableHeaderId, String stageTag, String basePath, String subPath, boolean isFairScheduler, boolean killEnabled, long currentTime, int pageSize, String sortColumn, boolean desc, boolean isFailedStage, Iterable<String> parameterOtherTable) {
      this.store = store;
      this.org$apache$spark$ui$jobs$StagePagedTable$$tableHeaderId = tableHeaderId;
      this.org$apache$spark$ui$jobs$StagePagedTable$$stageTag = stageTag;
      this.basePath = basePath;
      this.isFairScheduler = isFairScheduler;
      this.killEnabled = killEnabled;
      this.org$apache$spark$ui$jobs$StagePagedTable$$pageSize = pageSize;
      this.org$apache$spark$ui$jobs$StagePagedTable$$sortColumn = sortColumn;
      this.org$apache$spark$ui$jobs$StagePagedTable$$desc = desc;
      this.isFailedStage = isFailedStage;
      PagedTable$class.$init$(this);
      this.parameterPath = (new StringBuilder()).append((Object)UIUtils$.MODULE$.prependBaseUri(basePath, UIUtils$.MODULE$.prependBaseUri$default$2())).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/", "/?"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{subPath}))).append((Object)parameterOtherTable.mkString("&")).toString();
      this.dataSource = new StageDataSource(store, stages, currentTime, pageSize, sortColumn, desc);
   }
}
