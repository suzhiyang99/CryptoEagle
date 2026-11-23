package org.apache.spark.ui.jobs;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Iterable;
import scala.collection.immutable.Iterable$;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
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
   bytes = "\u0006\u0001\u0005Ue!B\u0001\u0003\u0001\u0011a!AD*uC\u001e,G+\u00192mK\n\u000b7/\u001a\u0006\u0003\u0007\u0011\tAA[8cg*\u0011QAB\u0001\u0003k&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007\u0002\u0003\u000b\u0001\u0005\u0003\u0005\u000b\u0011\u0002\f\u0002\u000bM$xN]3\u0004\u0001A\u0011qCG\u0007\u00021)\u0011\u0011DB\u0001\u0007gR\fG/^:\n\u0005mA\"AD!qaN#\u0018\r^;t'R|'/\u001a\u0005\t;\u0001\u0011\t\u0011)A\u0005=\u00059!/Z9vKN$\bCA\u0010'\u001b\u0005\u0001#BA\u0011#\u0003\u0011AG\u000f\u001e9\u000b\u0005\r\"\u0013aB:feZdW\r\u001e\u0006\u0002K\u0005)!.\u0019<bq&\u0011q\u0005\t\u0002\u0013\u0011R$\boU3sm2,GOU3rk\u0016\u001cH\u000f\u0003\u0005*\u0001\t\u0005\t\u0015!\u0003+\u0003\u0019\u0019H/Y4fgB\u00191f\r\u001c\u000f\u00051\ndBA\u00171\u001b\u0005q#BA\u0018\u0016\u0003\u0019a$o\\8u}%\t\u0001#\u0003\u00023\u001f\u00059\u0001/Y2lC\u001e,\u0017B\u0001\u001b6\u0005\r\u0019V-\u001d\u0006\u0003e=\u0001\"a\u000e\u001f\u000e\u0003aR!!\u000f\u001e\u0002\u0005Y\f$BA\u001e\u0019\u0003\r\t\u0007/[\u0005\u0003{a\u0012\u0011b\u0015;bO\u0016$\u0015\r^1\t\u0011}\u0002!\u0011!Q\u0001\n\u0001\u000bQ\u0002^1cY\u0016DU-\u00193fe&#\u0005CA!E\u001d\tq!)\u0003\u0002D\u001f\u00051\u0001K]3eK\u001aL!!\u0012$\u0003\rM#(/\u001b8h\u0015\t\u0019u\u0002\u0003\u0005I\u0001\t\u0005\t\u0015!\u0003A\u0003!\u0019H/Y4f)\u0006<\u0007\u0002\u0003&\u0001\u0005\u0003\u0005\u000b\u0011\u0002!\u0002\u0011\t\f7/\u001a)bi\"D\u0001\u0002\u0014\u0001\u0003\u0002\u0003\u0006I\u0001Q\u0001\bgV\u0014\u0007+\u0019;i\u0011!q\u0005A!A!\u0002\u0013y\u0015aD5t\r\u0006L'oU2iK\u0012,H.\u001a:\u0011\u00059\u0001\u0016BA)\u0010\u0005\u001d\u0011un\u001c7fC:D\u0001b\u0015\u0001\u0003\u0002\u0003\u0006IaT\u0001\fW&dG.\u00128bE2,G\r\u0003\u0005V\u0001\t\u0005\t\u0015!\u0003P\u00035I7OR1jY\u0016$7\u000b^1hK\")q\u000b\u0001C\u00011\u00061A(\u001b8jiz\"2\"W.];z{\u0006-\u00192dIB\u0011!\fA\u0007\u0002\u0005!)AC\u0016a\u0001-!)QD\u0016a\u0001=!)\u0011F\u0016a\u0001U!)qH\u0016a\u0001\u0001\")\u0001J\u0016a\u0001\u0001\")!J\u0016a\u0001\u0001\")AJ\u0016a\u0001\u0001\")aJ\u0016a\u0001\u001f\")1K\u0016a\u0001\u001f\")QK\u0016a\u0001\u001f\"9a\r\u0001b\u0001\n\u00039\u0017!D1mYB\u000b'/Y7fi\u0016\u00148/F\u0001i!\u0011Ig\u000e]<\u000e\u0003)T!a\u001b7\u0002\u0013%lW.\u001e;bE2,'BA7\u0010\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003_*\u00141!T1q!\t\th/D\u0001s\u0015\t\u0019H/\u0001\u0003mC:<'\"A;\u0002\t)\fg/Y\u0005\u0003\u000bJ\u00042A\u0004=A\u0013\tIxBA\u0003BeJ\f\u0017\u0010\u0003\u0004|\u0001\u0001\u0006I\u0001[\u0001\u000fC2d\u0007+\u0019:b[\u0016$XM]:!\u0011\u001di\bA1A\u0005\u0002y\f1\u0003]1sC6,G/\u001a:Pi\",'\u000fV1cY\u0016,\u0012a \t\u0005S\u0006\u0005\u0001/C\u0002\u0002\u0004)\u0014\u0001\"\u0013;fe\u0006\u0014G.\u001a\u0005\b\u0003\u000f\u0001\u0001\u0015!\u0003\u0000\u0003Q\u0001\u0018M]1nKR,'o\u0014;iKJ$\u0016M\u00197fA!I\u00111\u0002\u0001C\u0002\u0013\u0005\u0011QB\u0001\u0013a\u0006\u0014\u0018-\\3uKJ\u001cF/Y4f!\u0006<W-F\u0001A\u0011\u001d\t\t\u0002\u0001Q\u0001\n\u0001\u000b1\u0003]1sC6,G/\u001a:Ti\u0006<W\rU1hK\u0002B\u0011\"!\u0006\u0001\u0005\u0004%\t!!\u0004\u00021A\f'/Y7fi\u0016\u00148\u000b^1hKN{'\u000f^\"pYVlg\u000eC\u0004\u0002\u001a\u0001\u0001\u000b\u0011\u0002!\u00023A\f'/Y7fi\u0016\u00148\u000b^1hKN{'\u000f^\"pYVlg\u000e\t\u0005\n\u0003;\u0001!\u0019!C\u0001\u0003\u001b\ta\u0003]1sC6,G/\u001a:Ti\u0006<WmU8si\u0012+7o\u0019\u0005\b\u0003C\u0001\u0001\u0015!\u0003A\u0003]\u0001\u0018M]1nKR,'o\u0015;bO\u0016\u001cvN\u001d;EKN\u001c\u0007\u0005C\u0005\u0002&\u0001\u0011\r\u0011\"\u0001\u0002\u000e\u00051\u0002/\u0019:b[\u0016$XM]*uC\u001e,\u0007+Y4f'&TX\rC\u0004\u0002*\u0001\u0001\u000b\u0011\u0002!\u0002/A\f'/Y7fi\u0016\u00148\u000b^1hKB\u000bw-Z*ju\u0016\u0004\u0003\"CA\u0017\u0001\t\u0007I\u0011AA\u0007\u0003i\u0001\u0018M]1nKR,'o\u0015;bO\u0016\u0004&/\u001a<QC\u001e,7+\u001b>f\u0011\u001d\t\t\u0004\u0001Q\u0001\n\u0001\u000b1\u0004]1sC6,G/\u001a:Ti\u0006<W\r\u0015:fmB\u000bw-Z*ju\u0016\u0004\u0003\"CA\u001b\u0001\t\u0007I\u0011AA\u001c\u0003%\u0019H/Y4f!\u0006<W-\u0006\u0002\u0002:A\u0019a\"a\u000f\n\u0007\u0005urBA\u0002J]RD\u0001\"!\u0011\u0001A\u0003%\u0011\u0011H\u0001\u000bgR\fw-\u001a)bO\u0016\u0004\u0003\"CA#\u0001\t\u0007I\u0011AA\u0007\u0003=\u0019H/Y4f'>\u0014HoQ8mk6t\u0007bBA%\u0001\u0001\u0006I\u0001Q\u0001\u0011gR\fw-Z*peR\u001cu\u000e\\;n]\u0002B\u0011\"!\u0014\u0001\u0005\u0004%\t!a\u0014\u0002\u001bM$\u0018mZ3T_J$H)Z:d+\u0005y\u0005bBA*\u0001\u0001\u0006IaT\u0001\u000fgR\fw-Z*peR$Um]2!\u0011%\t9\u0006\u0001b\u0001\n\u0003\t9$A\u0007ti\u0006<W\rU1hKNK'0\u001a\u0005\t\u00037\u0002\u0001\u0015!\u0003\u0002:\u0005q1\u000f^1hKB\u000bw-Z*ju\u0016\u0004\u0003\"CA0\u0001\t\u0007I\u0011AA\u001c\u0003E\u0019H/Y4f!J,g\u000fU1hKNK'0\u001a\u0005\t\u0003G\u0002\u0001\u0015!\u0003\u0002:\u0005\u00112\u000f^1hKB\u0013XM\u001e)bO\u0016\u001c\u0016N_3!\u0011%\t9\u0007\u0001b\u0001\n\u0003\t9$\u0001\u0003qC\u001e,\u0007\u0002CA6\u0001\u0001\u0006I!!\u000f\u0002\u000bA\fw-\u001a\u0011\t\u0013\u0005=\u0004A1A\u0005\u0002\u0005E\u0014aC2veJ,g\u000e\u001e+j[\u0016,\"!a\u001d\u0011\u00079\t)(C\u0002\u0002x=\u0011A\u0001T8oO\"A\u00111\u0010\u0001!\u0002\u0013\t\u0019(\u0001\u0007dkJ\u0014XM\u001c;US6,\u0007\u0005C\u0005\u0002\u0000\u0001\u0011\r\u0011\"\u0001\u0002\u0002\u0006IAo\u001c(pI\u0016\u001cV-]\u000b\u0003\u0003\u0007\u0003BaK\u001a\u0002\u0006B!\u0011qQAG\u001b\t\tIIC\u0002\u0002\f>\t1\u0001_7m\u0013\u0011\ty)!#\u0003\t9{G-\u001a\u0005\t\u0003'\u0003\u0001\u0015!\u0003\u0002\u0004\u0006QAo\u001c(pI\u0016\u001cV-\u001d\u0011"
)
public class StageTableBase {
   private final AppStatusStore store;
   private final Seq<StageData> stages;
   private final String tableHeaderID;
   public final String org$apache$spark$ui$jobs$StageTableBase$$stageTag;
   private final String basePath;
   private final String subPath;
   private final boolean isFairScheduler;
   private final boolean killEnabled;
   private final boolean isFailedStage;
   private final Map<String, String[]> allParameters;
   private final Iterable<String> parameterOtherTable;
   private final String parameterStagePage;
   private final String parameterStageSortColumn;
   private final String parameterStageSortDesc;
   private final String parameterStagePageSize;
   private final String parameterStagePrevPageSize;
   private final int stagePage;
   private final String stageSortColumn;
   private final boolean stageSortDesc;
   private final int stagePageSize;
   private final int stagePrevPageSize;
   private final int page;
   private final long currentTime;
   private final Seq<Node> toNodeSeq;

   public Map<String, String[]> allParameters() {
      return this.allParameters;
   }

   public Iterable<String> parameterOtherTable() {
      return this.parameterOtherTable;
   }

   public String parameterStagePage() {
      return this.parameterStagePage;
   }

   public String parameterStageSortColumn() {
      return this.parameterStageSortColumn;
   }

   public String parameterStageSortDesc() {
      return this.parameterStageSortDesc;
   }

   public String parameterStagePageSize() {
      return this.parameterStagePageSize;
   }

   public String parameterStagePrevPageSize() {
      return this.parameterStagePrevPageSize;
   }

   public int stagePage() {
      return this.stagePage;
   }

   public String stageSortColumn() {
      return this.stageSortColumn;
   }

   public boolean stageSortDesc() {
      return this.stageSortDesc;
   }

   public int stagePageSize() {
      return this.stagePageSize;
   }

   public int stagePrevPageSize() {
      return this.stagePrevPageSize;
   }

   public int page() {
      return this.page;
   }

   public long currentTime() {
      return this.currentTime;
   }

   public Seq<Node> toNodeSeq() {
      return this.toNodeSeq;
   }

   private final Seq liftedTree1$1() {
      Object var10000;
      try {
         var10000 = (new StagePagedTable(this.store, this.stages, this.tableHeaderID, this.org$apache$spark$ui$jobs$StageTableBase$$stageTag, this.basePath, this.subPath, this.isFairScheduler, this.killEnabled, this.currentTime(), this.stagePageSize(), this.stageSortColumn(), this.stageSortDesc(), this.isFailedStage, this.parameterOtherTable())).table(this.page());
      } catch (Throwable var10) {
         boolean var3;
         if (var10 instanceof IllegalArgumentException) {
            var3 = true;
         } else if (var10 instanceof IndexOutOfBoundsException) {
            var3 = true;
         } else {
            var3 = false;
         }

         if (!var3) {
            throw var10;
         }

         MetaData $md = .MODULE$;
         MetaData var11 = new UnprefixedAttribute("class", new Text("alert alert-error"), $md);
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         Null var10012 = .MODULE$;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Error while rendering stage table:"));
         $buf.$amp$plus(new Elem((String)null, "p", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         var10012 = .MODULE$;
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(Utils$.MODULE$.exceptionString(var10));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "pre", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n      "));
         Elem var4 = new Elem((String)null, "div", var11, var10005, false, $buf);
         var10000 = var4;
         return (Seq)var10000;
      }

      return (Seq)var10000;
   }

   public StageTableBase(AppStatusStore store, HttpServletRequest request, Seq<StageData> stages, String tableHeaderID, String stageTag, String basePath, String subPath, boolean isFairScheduler, boolean killEnabled, boolean isFailedStage) {
      this.store = store;
      this.stages = stages;
      this.tableHeaderID = tableHeaderID;
      this.org$apache$spark$ui$jobs$StageTableBase$$stageTag = stageTag;
      this.basePath = basePath;
      this.subPath = subPath;
      this.isFairScheduler = isFairScheduler;
      this.killEnabled = killEnabled;
      this.isFailedStage = isFailedStage;
      this.allParameters = ((TraversableOnce)JavaConverters$.MODULE$.mapAsScalaMapConverter(request.getParameterMap()).asScala()).toMap(Predef$.MODULE$.$conforms()).mapValues(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String[] apply(String[] x$1) {
            return (String[])Predef$.MODULE$.refArrayOps((Object[])x$1).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String requestParameter) {
                  return UIUtils$.MODULE$.stripXSS(requestParameter);
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)));
         }
      });
      this.parameterOtherTable = (Iterable)this.allParameters().filterNot(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StageTableBase $outer;

         public final boolean apply(Tuple2<String, String[]> x$2) {
            return ((String)x$2._1()).startsWith(this.$outer.org$apache$spark$ui$jobs$StageTableBase$$stageTag);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<String, String[]> para) {
            return (new StringBuilder()).append((Object)((String)para._1())).append((Object)"=").append((Object)((String[])para._2())[0]).toString();
         }
      }, Iterable$.MODULE$.canBuildFrom());
      this.parameterStagePage = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)stageTag).append((Object)".page").toString()));
      this.parameterStageSortColumn = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)stageTag).append((Object)".sort").toString()));
      this.parameterStageSortDesc = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)stageTag).append((Object)".desc").toString()));
      this.parameterStagePageSize = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)stageTag).append((Object)".pageSize").toString()));
      this.parameterStagePrevPageSize = UIUtils$.MODULE$.stripXSS(request.getParameter((new StringBuilder()).append((Object)stageTag).append((Object)".prevPageSize").toString()));
      this.stagePage = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(this.parameterStagePage()).map(new Serializable(this) {
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
            return 1;
         }
      }));
      this.stageSortColumn = (String)Option$.MODULE$.apply(this.parameterStageSortColumn()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String sortColumn) {
            return UIUtils$.MODULE$.decodeURLParameter(sortColumn);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Stage Id";
         }
      });
      this.stageSortDesc = BoxesRunTime.unboxToBoolean(Option$.MODULE$.apply(this.parameterStageSortDesc()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$4) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$4))).toBoolean();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StageTableBase $outer;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            boolean var2;
            label23: {
               String var10000 = this.$outer.stageSortColumn();
               String var1 = "Stage Id";
               if (var10000 == null) {
                  if (var1 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var1)) {
                  break label23;
               }

               var2 = false;
               return var2;
            }

            var2 = true;
            return var2;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      this.stagePageSize = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(this.parameterStagePageSize()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$5) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$5))).toInt();
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
      this.stagePrevPageSize = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(this.parameterStagePrevPageSize()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$6) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$6))).toInt();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StageTableBase $outer;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return this.$outer.stagePageSize();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      this.page = this.stagePageSize() <= this.stagePrevPageSize() ? this.stagePage() : 1;
      this.currentTime = System.currentTimeMillis();
      this.toNodeSeq = this.liftedTree1$1();
   }
}
