package org.apache.spark.ui.jobs;

import java.net.URLEncoder;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.ui.PagedTable;
import org.apache.spark.ui.PagedTable$class;
import org.apache.spark.ui.UIUtils$;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple3;
import scala.collection.Iterable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.xml.Elem;
import scala.xml.EntityRef;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.Unparsed;
import scala.xml.UnprefixedAttribute;
import scala.xml.Unparsed.;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mc!B\u0001\u0003\u0001\u0011a!!\u0004&pEB\u000bw-\u001a3UC\ndWM\u0003\u0002\u0004\t\u0005!!n\u001c2t\u0015\t)a!\u0001\u0002vS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0002\u0001\u001bM\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007c\u0001\u000b\u0016/5\tA!\u0003\u0002\u0017\t\tQ\u0001+Y4fIR\u000b'\r\\3\u0011\u0005aIR\"\u0001\u0002\n\u0005i\u0011!a\u0004&pER\u000b'\r\\3S_^$\u0015\r^1\t\u0011q\u0001!\u0011!Q\u0001\ny\tQa\u001d;pe\u0016\u001c\u0001\u0001\u0005\u0002 E5\t\u0001E\u0003\u0002\"\r\u000511\u000f^1ukNL!a\t\u0011\u0003\u001d\u0005\u0003\bo\u0015;biV\u001c8\u000b^8sK\"AQ\u0005\u0001B\u0001B\u0003%a%\u0001\u0003eCR\f\u0007cA\u00140e9\u0011\u0001&\f\b\u0003S1j\u0011A\u000b\u0006\u0003Wu\ta\u0001\u0010:p_Rt\u0014\"\u0001\t\n\u00059z\u0011a\u00029bG.\fw-Z\u0005\u0003aE\u00121aU3r\u0015\tqs\u0002\u0005\u00024q5\tAG\u0003\u00026m\u0005\u0011a/\r\u0006\u0003o\u0001\n1!\u00199j\u0013\tIDGA\u0004K_\n$\u0015\r^1\t\u0011m\u0002!\u0011!Q\u0001\nq\nQ\u0002^1cY\u0016DU-\u00193fe&#\u0007CA\u001fA\u001d\tqa(\u0003\u0002@\u001f\u00051\u0001K]3eK\u001aL!!\u0011\"\u0003\rM#(/\u001b8h\u0015\tyt\u0002\u0003\u0005E\u0001\t\u0005\t\u0015!\u0003=\u0003\u0019QwN\u0019+bO\"Aa\t\u0001B\u0001B\u0003%A(\u0001\u0005cCN,\u0007+\u0019;i\u0011!A\u0005A!A!\u0002\u0013a\u0014aB:vEB\u000bG\u000f\u001b\u0005\t\u0015\u0002\u0011\t\u0011)A\u0005\u0017\u0006\u0019\u0002/\u0019:b[\u0016$XM](uQ\u0016\u0014H+\u00192mKB\u0019q\u0005\u0014\u001f\n\u00055\u000b$\u0001C%uKJ\f'\r\\3\t\u0011=\u0003!\u0011!Q\u0001\nA\u000b1b[5mY\u0016s\u0017M\u00197fIB\u0011a\"U\u0005\u0003%>\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005U\u0001\t\u0005\t\u0015!\u0003V\u0003-\u0019WO\u001d:f]R$\u0016.\\3\u0011\u000591\u0016BA,\u0010\u0005\u0011auN\\4\t\u0011e\u0003!\u0011!Q\u0001\nq\n!B[8c\u0013\u0012$\u0016\u000e\u001e7f\u0011!Y\u0006A!A!\u0002\u0013a\u0016\u0001\u00039bO\u0016\u001c\u0016N_3\u0011\u00059i\u0016B\u00010\u0010\u0005\rIe\u000e\u001e\u0005\tA\u0002\u0011\t\u0011)A\u0005y\u0005Q1o\u001c:u\u0007>dW/\u001c8\t\u0011\t\u0004!\u0011!Q\u0001\nA\u000bA\u0001Z3tG\")A\r\u0001C\u0001K\u00061A(\u001b8jiz\"bBZ4iS*\\G.\u001c8paF\u00148\u000f\u0005\u0002\u0019\u0001!)Ad\u0019a\u0001=!)Qe\u0019a\u0001M!)1h\u0019a\u0001y!)Ai\u0019a\u0001y!)ai\u0019a\u0001y!)\u0001j\u0019a\u0001y!)!j\u0019a\u0001\u0017\")qj\u0019a\u0001!\")Ak\u0019a\u0001+\")\u0011l\u0019a\u0001y!)1l\u0019a\u00019\")\u0001m\u0019a\u0001y!)!m\u0019a\u0001!\"9Q\u000f\u0001b\u0001\n\u00031\u0018!\u00049be\u0006lW\r^3s!\u0006$\b.F\u0001x!\tAX0D\u0001z\u0015\tQ80\u0001\u0003mC:<'\"\u0001?\u0002\t)\fg/Y\u0005\u0003\u0003fDaa \u0001!\u0002\u00139\u0018A\u00049be\u0006lW\r^3s!\u0006$\b\u000e\t\u0005\b\u0003\u0007\u0001A\u0011IA\u0003\u0003\u001d!\u0018M\u00197f\u0013\u0012,\u0012\u0001\u0010\u0005\b\u0003\u0013\u0001A\u0011IA\u0003\u00035!\u0018M\u00197f\u0007N\u001c8\t\\1tg\"9\u0011Q\u0002\u0001\u0005B\u0005\u0015\u0011!\u00059bO\u0016\u001c\u0016N_3G_Jlg)[3mI\"9\u0011\u0011\u0003\u0001\u0005B\u0005\u0015\u0011!\u00069sKZ\u0004\u0016mZ3TSj,gi\u001c:n\r&,G\u000e\u001a\u0005\b\u0003+\u0001A\u0011IA\u0003\u0003M\u0001\u0018mZ3Ok6\u0014WM\u001d$pe64\u0015.\u001a7e\u0011%\tI\u0002\u0001b\u0001\n\u0003\nY\"\u0001\u0006eCR\f7k\\;sG\u0016,\"!!\b\u0011\u0007a\ty\"C\u0002\u0002\"\t\u0011QBS8c\t\u0006$\u0018mU8ve\u000e,\u0007\u0002CA\u0013\u0001\u0001\u0006I!!\b\u0002\u0017\u0011\fG/Y*pkJ\u001cW\r\t\u0005\b\u0003S\u0001A\u0011IA\u0016\u0003!\u0001\u0018mZ3MS:\\Gc\u0001\u001f\u0002.!9\u0011qFA\u0014\u0001\u0004a\u0016\u0001\u00029bO\u0016Dq!a\r\u0001\t\u0003\n)!\u0001\th_\n+H\u000f^8o\r>\u0014X\u000eU1uQ\"9\u0011q\u0007\u0001\u0005B\u0005e\u0012a\u00025fC\u0012,'o]\u000b\u0003\u0003w\u0001BaJ\u0018\u0002>A!\u0011qHA#\u001b\t\t\tEC\u0002\u0002D=\t1\u0001_7m\u0013\u0011\t9%!\u0011\u0003\t9{G-\u001a\u0005\b\u0003\u0017\u0002A\u0011IA'\u0003\r\u0011xn\u001e\u000b\u0005\u0003w\ty\u0005C\u0004\u0002R\u0005%\u0003\u0019A\f\u0002\u0017)|'\rV1cY\u0016\u0014vn\u001e"
)
public class JobPagedTable implements PagedTable<JobTableRowData> {
   public final String org$apache$spark$ui$jobs$JobPagedTable$$tableHeaderId;
   public final String org$apache$spark$ui$jobs$JobPagedTable$$jobTag;
   private final String basePath;
   private final boolean killEnabled;
   private final String jobIdTitle;
   public final int org$apache$spark$ui$jobs$JobPagedTable$$pageSize;
   public final String org$apache$spark$ui$jobs$JobPagedTable$$sortColumn;
   public final boolean org$apache$spark$ui$jobs$JobPagedTable$$desc;
   private final String parameterPath;
   private final JobDataSource dataSource;

   public Seq<Node> table(int page) {
      return PagedTable$class.table(this, page);
   }

   public Seq<Node> pageNavigation(int page, int pageSize, int totalPages) {
      return PagedTable$class.pageNavigation(this, page, pageSize, totalPages);
   }

   public String parameterPath() {
      return this.parameterPath;
   }

   public String tableId() {
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$jobs$JobPagedTable$$jobTag).append((Object)"-table").toString();
   }

   public String tableCssClass() {
      return "table table-bordered table-condensed table-striped table-head-clickable table-cell-width-limited";
   }

   public String pageSizeFormField() {
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$jobs$JobPagedTable$$jobTag).append((Object)".pageSize").toString();
   }

   public String prevPageSizeFormField() {
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$jobs$JobPagedTable$$jobTag).append((Object)".prevPageSize").toString();
   }

   public String pageNumberFormField() {
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$jobs$JobPagedTable$$jobTag).append((Object)".page").toString();
   }

   public JobDataSource dataSource() {
      return this.dataSource;
   }

   public String pageLink(int page) {
      String encodedSortColumn = URLEncoder.encode(this.org$apache$spark$ui$jobs$JobPagedTable$$sortColumn, "UTF-8");
      return (new StringBuilder()).append((Object)this.parameterPath()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.pageNumberFormField(), BoxesRunTime.boxToInteger(page)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$JobPagedTable$$jobTag, encodedSortColumn}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$JobPagedTable$$jobTag, BoxesRunTime.boxToBoolean(this.org$apache$spark$ui$jobs$JobPagedTable$$desc)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.pageSizeFormField(), BoxesRunTime.boxToInteger(this.org$apache$spark$ui$jobs$JobPagedTable$$pageSize)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"#", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$JobPagedTable$$tableHeaderId}))).toString();
   }

   public String goButtonFormPath() {
      String encodedSortColumn = URLEncoder.encode(this.org$apache$spark$ui$jobs$JobPagedTable$$sortColumn, "UTF-8");
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "&", ".sort=", "&", ".desc=", "#", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.parameterPath(), this.org$apache$spark$ui$jobs$JobPagedTable$$jobTag, encodedSortColumn, this.org$apache$spark$ui$jobs$JobPagedTable$$jobTag, BoxesRunTime.boxToBoolean(this.org$apache$spark$ui$jobs$JobPagedTable$$desc), this.org$apache$spark$ui$jobs$JobPagedTable$$tableHeaderId}));
   }

   public Seq<Node> headers() {
      Seq jobHeadersAndCssClasses = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple3[]{new Tuple3(this.jobIdTitle, "", BoxesRunTime.boxToBoolean(true)), new Tuple3("Description", "", BoxesRunTime.boxToBoolean(true)), new Tuple3("Submitted", "", BoxesRunTime.boxToBoolean(true)), new Tuple3("Duration", "", BoxesRunTime.boxToBoolean(true)), new Tuple3("Stages: Succeeded/Total", "", BoxesRunTime.boxToBoolean(false)), new Tuple3("Tasks (for all stages): Succeeded/Total", "", BoxesRunTime.boxToBoolean(false))}));
      if (((SeqLike)((TraversableLike)jobHeadersAndCssClasses.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple3<String, String, Object> x$18) {
            return BoxesRunTime.unboxToBoolean(x$18._3());
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple3<String, String, Object> x$19) {
            return (String)x$19._1();
         }
      }, Seq$.MODULE$.canBuildFrom())).contains(this.org$apache$spark$ui$jobs$JobPagedTable$$sortColumn)) {
         Seq headerRow = (Seq)jobHeadersAndCssClasses.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final JobPagedTable $outer;

            public final Elem apply(Tuple3<String, String, Object> x0$1) {
               if (x0$1 == null) {
                  throw new MatchError(x0$1);
               } else {
                  Elem var10000;
                  label33: {
                     String header;
                     String cssClass;
                     boolean sortable;
                     label37: {
                        header = (String)x0$1._1();
                        cssClass = (String)x0$1._2();
                        sortable = BoxesRunTime.unboxToBoolean(x0$1._3());
                        String var7 = this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$sortColumn;
                        if (header == null) {
                           if (var7 != null) {
                              break label37;
                           }
                        } else if (!header.equals(var7)) {
                           break label37;
                        }

                        Unparsed headerLink = .MODULE$.apply((new StringBuilder()).append((Object)this.$outer.parameterPath()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$jobTag, URLEncoder.encode(header, "UTF-8")}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$jobTag, BoxesRunTime.boxToBoolean(!this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$desc)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".pageSize=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$jobTag, BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$pageSize)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"#", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$tableHeaderId}))).toString());
                        String arrow = this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$desc ? "&#x25BE;" : "&#x25B4;";
                        MetaData $md = scala.xml.Null..MODULE$;
                        MetaData var22 = new UnprefixedAttribute("class", cssClass, $md);
                        TopScope var10005 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n            "));
                        MetaData $md = scala.xml.Null..MODULE$;
                        MetaData var23 = new UnprefixedAttribute("href", headerLink, $md);
                        TopScope var10013 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n              "));
                        $buf.$amp$plus(header);
                        Null var10020 = scala.xml.Null..MODULE$;
                        TopScope var10021 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n              "));
                        $buf.$amp$plus(new EntityRef("nbsp"));
                        $buf.$amp$plus(.MODULE$.apply(arrow));
                        $buf.$amp$plus(new Text("\n            "));
                        $buf.$amp$plus(new Elem((String)null, "span", var10020, var10021, false, $buf));
                        $buf.$amp$plus(new Text("\n            "));
                        $buf.$amp$plus(new Elem((String)null, "a", var23, var10013, false, $buf));
                        $buf.$amp$plus(new Text("\n          "));
                        var10000 = new Elem((String)null, "th", var22, var10005, false, $buf);
                        break label33;
                     }

                     if (sortable) {
                        Unparsed headerLink = .MODULE$.apply((new StringBuilder()).append((Object)this.$outer.parameterPath()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$jobTag, URLEncoder.encode(header, "UTF-8")}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", ".pageSize=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$jobTag, BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$pageSize)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"#", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$jobs$JobPagedTable$$tableHeaderId}))).toString());
                        MetaData $md = scala.xml.Null..MODULE$;
                        MetaData var24 = new UnprefixedAttribute("class", cssClass, $md);
                        TopScope var27 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n              "));
                        MetaData $md = scala.xml.Null..MODULE$;
                        MetaData var25 = new UnprefixedAttribute("href", headerLink, $md);
                        TopScope var29 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n                "));
                        $buf.$amp$plus(header);
                        $buf.$amp$plus(new Text("\n              "));
                        $buf.$amp$plus(new Elem((String)null, "a", var25, var29, false, $buf));
                        $buf.$amp$plus(new Text("\n            "));
                        var10000 = new Elem((String)null, "th", var24, var27, false, $buf);
                     } else {
                        MetaData $md = scala.xml.Null..MODULE$;
                        MetaData var26 = new UnprefixedAttribute("class", cssClass, $md);
                        TopScope var28 = scala.xml.TopScope..MODULE$;
                        NodeBuffer $buf = new NodeBuffer();
                        $buf.$amp$plus(new Text("\n              "));
                        $buf.$amp$plus(header);
                        $buf.$amp$plus(new Text("\n            "));
                        var10000 = new Elem((String)null, "th", var26, var28, false, $buf);
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
         Null var10004 = scala.xml.Null..MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(headerRow);
         return new Elem((String)null, "thead", var10004, var10005, false, $buf);
      } else {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$jobs$JobPagedTable$$sortColumn})));
      }
   }

   public Seq<Node> row(JobTableRowData jobTableRow) {
      JobData job = jobTableRow.jobData();
      Object var10000;
      if (this.killEnabled) {
         String confirm = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"if (window.confirm('Are you sure you want to kill job ", " ?')) "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(job.jobId())}))).append((Object)"{ this.parentNode.submit(); return true; } else { return false; }").toString();
         String killLinkUri = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/jobs/job/kill/?id=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.basePath, BoxesRunTime.boxToInteger(job.jobId())}));
         MetaData $md = scala.xml.Null..MODULE$;
         MetaData var20 = new UnprefixedAttribute("class", new Text("kill-link"), $md);
         var20 = new UnprefixedAttribute("onclick", confirm, var20);
         var20 = new UnprefixedAttribute("href", killLinkUri, var20);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("(kill)"));
         var10000 = new Elem((String)null, "a", var20, var10005, false, $buf);
      } else {
         var10000 = (Seq)Seq$.MODULE$.empty();
      }

      Seq killLink = (Seq)var10000;
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var23 = new UnprefixedAttribute("id", (new StringBuilder()).append((Object)"job-").append((Object)BoxesRunTime.boxToInteger(job.jobId())).toString(), $md);
      TopScope var28 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = scala.xml.Null..MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(job.jobId()));
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(job.jobGroup().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String id) {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{id}));
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      }));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(jobTableRow.jobDescription());
      $buf.$amp$plus(new Text(" "));
      $buf.$amp$plus(killLink);
      $buf.$amp$plus(new Text("\n        "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var24 = new UnprefixedAttribute("class", new Text("name-link"), $md);
      var24 = new UnprefixedAttribute("href", jobTableRow.detailUrl(), var24);
      TopScope var10021 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(jobTableRow.lastStageName());
      $buf.$amp$plus(new Elem((String)null, "a", var24, var10021, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(jobTableRow.formattedSubmissionTime());
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(jobTableRow.formattedDuration());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var26 = new UnprefixedAttribute("class", new Text("stage-progress-cell"), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(job.numCompletedStages()));
      $buf.$amp$plus(new Text("/"));
      $buf.$amp$plus(BoxesRunTime.boxToInteger(job.stageIds().size() - job.numSkippedStages()));
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(job.numFailedStages() > 0 ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " failed)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(job.numFailedStages())})) : BoxedUnit.UNIT);
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(job.numSkippedStages() > 0 ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " skipped)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(job.numSkippedStages())})) : BoxedUnit.UNIT);
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var26, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      MetaData $md = scala.xml.Null..MODULE$;
      MetaData var27 = new UnprefixedAttribute("class", new Text("progress-cell"), $md);
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n        "));
      $buf.$amp$plus(UIUtils$.MODULE$.makeProgressBar(job.numActiveTasks(), job.numCompletedIndices(), job.numFailedTasks(), job.numSkippedTasks(), job.killedTasksSummary(), job.numTasks() - job.numSkippedTasks()));
      $buf.$amp$plus(new Text("\n      "));
      $buf.$amp$plus(new Elem((String)null, "td", var27, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var23, var28, false, $buf);
   }

   public JobPagedTable(AppStatusStore store, Seq<JobData> data, String tableHeaderId, String jobTag, String basePath, String subPath, Iterable<String> parameterOtherTable, boolean killEnabled, long currentTime, String jobIdTitle, int pageSize, String sortColumn, boolean desc) {
      this.org$apache$spark$ui$jobs$JobPagedTable$$tableHeaderId = tableHeaderId;
      this.org$apache$spark$ui$jobs$JobPagedTable$$jobTag = jobTag;
      this.basePath = basePath;
      this.killEnabled = killEnabled;
      this.jobIdTitle = jobIdTitle;
      this.org$apache$spark$ui$jobs$JobPagedTable$$pageSize = pageSize;
      this.org$apache$spark$ui$jobs$JobPagedTable$$sortColumn = sortColumn;
      this.org$apache$spark$ui$jobs$JobPagedTable$$desc = desc;
      PagedTable$class.$init$(this);
      this.parameterPath = (new StringBuilder()).append((Object)basePath).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/", "/?"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{subPath}))).append((Object)parameterOtherTable.mkString("&")).toString();
      this.dataSource = new JobDataSource(store, data, basePath, currentTime, pageSize, sortColumn, desc);
   }
}
