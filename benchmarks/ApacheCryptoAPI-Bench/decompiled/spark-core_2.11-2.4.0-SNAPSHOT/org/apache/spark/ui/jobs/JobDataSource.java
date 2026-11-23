package org.apache.spark.ui.jobs;

import java.util.Date;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.ui.PagedDataSource;
import org.apache.spark.ui.UIUtils$;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.package$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.math.Ordering;
import scala.math.Ordering$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.xml.NodeSeq;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ua!B\u0001\u0003\u0001\u0011a!!\u0004&pE\u0012\u000bG/Y*pkJ\u001cWM\u0003\u0002\u0004\t\u0005!!n\u001c2t\u0015\t)a!\u0001\u0002vS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0019abD\t\u000e\u0003\u0011I!\u0001\u0005\u0003\u0003\u001fA\u000bw-\u001a3ECR\f7k\\;sG\u0016\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003\u001f){'\rV1cY\u0016\u0014vn\u001e#bi\u0006D\u0001B\u0006\u0001\u0003\u0002\u0003\u0006I\u0001G\u0001\u0006gR|'/Z\u0002\u0001!\tIB$D\u0001\u001b\u0015\tYb!\u0001\u0004ti\u0006$Xo]\u0005\u0003;i\u0011a\"\u00119q'R\fG/^:Ti>\u0014X\r\u0003\u0005\u0004\u0001\t\u0005\t\u0015!\u0003 !\r\u0001#&\f\b\u0003C\u001dr!AI\u0013\u000e\u0003\rR!\u0001J\f\u0002\rq\u0012xn\u001c;?\u0013\u00051\u0013!B:dC2\f\u0017B\u0001\u0015*\u0003\u001d\u0001\u0018mY6bO\u0016T\u0011AJ\u0005\u0003W1\u00121aU3r\u0015\tA\u0013\u0006\u0005\u0002/g5\tqF\u0003\u00021c\u0005\u0011a/\r\u0006\u0003ei\t1!\u00199j\u0013\t!tFA\u0004K_\n$\u0015\r^1\t\u0011Y\u0002!\u0011!Q\u0001\n]\n\u0001BY1tKB\u000bG\u000f\u001b\t\u0003qqr!!\u000f\u001e\u000e\u0003%J!aO\u0015\u0002\rA\u0013X\rZ3g\u0013\tidH\u0001\u0004TiJLgn\u001a\u0006\u0003w%B\u0001\u0002\u0011\u0001\u0003\u0002\u0003\u0006I!Q\u0001\fGV\u0014(/\u001a8u)&lW\r\u0005\u0002:\u0005&\u00111)\u000b\u0002\u0005\u0019>tw\rC\u0005F\u0001\t\u0005\t\u0015!\u0003G\u0013\u0006A\u0001/Y4f'&TX\r\u0005\u0002:\u000f&\u0011\u0001*\u000b\u0002\u0004\u0013:$\u0018BA#\u0010\u0011!Y\u0005A!A!\u0002\u00139\u0014AC:peR\u001cu\u000e\\;n]\"AQ\n\u0001B\u0001B\u0003%a*\u0001\u0003eKN\u001c\u0007CA\u001dP\u0013\t\u0001\u0016FA\u0004C_>dW-\u00198\t\u000bI\u0003A\u0011A*\u0002\rqJg.\u001b;?)!!VKV,Y3j[\u0006C\u0001\n\u0001\u0011\u00151\u0012\u000b1\u0001\u0019\u0011\u0015\u0019\u0011\u000b1\u0001 \u0011\u00151\u0014\u000b1\u00018\u0011\u0015\u0001\u0015\u000b1\u0001B\u0011\u0015)\u0015\u000b1\u0001G\u0011\u0015Y\u0015\u000b1\u00018\u0011\u0015i\u0015\u000b1\u0001O\u0011\u001di\u0006A1A\u0005\ny\u000bA\u0001Z1uCV\tq\fE\u0002aGFi\u0011!\u0019\u0006\u0003E&\n!bY8mY\u0016\u001cG/[8o\u0013\tY\u0013\r\u0003\u0004f\u0001\u0001\u0006IaX\u0001\u0006I\u0006$\u0018\r\t\u0005\bO\u0002\u0001\r\u0011\"\u0003i\u00035y6\u000f\\5dK\u0012TuNY%egV\t\u0011\u000eE\u00029U\u001aK!a\u001b \u0003\u0007M+G\u000fC\u0004n\u0001\u0001\u0007I\u0011\u00028\u0002#}\u001bH.[2fI*{'-\u00133t?\u0012*\u0017\u000f\u0006\u0002peB\u0011\u0011\b]\u0005\u0003c&\u0012A!\u00168ji\"91\u000f\\A\u0001\u0002\u0004I\u0017a\u0001=%c!1Q\u000f\u0001Q!\n%\fabX:mS\u000e,GMS8c\u0013\u0012\u001c\b\u0005C\u0003x\u0001\u0011\u0005\u00030\u0001\u0005eCR\f7+\u001b>f+\u00051\u0005\"\u0002>\u0001\t\u0003Z\u0018!C:mS\u000e,G)\u0019;b)\raXp \t\u0004A)\n\u0002\"\u0002@z\u0001\u00041\u0015\u0001\u00024s_6Da!!\u0001z\u0001\u00041\u0015A\u0001;p\u0011\u001d\t)\u0001\u0001C\u0005\u0003\u000f\taA[8c%><HcA\t\u0002\n!9\u00111BA\u0002\u0001\u0004i\u0013a\u00026pE\u0012\u000bG/\u0019\u0005\b\u0003\u001f\u0001A\u0011BA\t\u0003!y'\u000fZ3sS:<GCBA\n\u00033\tY\u0002\u0005\u0003!\u0003+\t\u0012bAA\fY\tAqJ\u001d3fe&tw\r\u0003\u0004L\u0003\u001b\u0001\ra\u000e\u0005\u0007\u001b\u00065\u0001\u0019\u0001("
)
public class JobDataSource extends PagedDataSource<JobTableRowData> {
   private final AppStatusStore store;
   private final String basePath;
   private final Seq<JobTableRowData> data;
   private Set<Object> _slicedJobIds;

   private Seq<JobTableRowData> data() {
      return this.data;
   }

   private Set<Object> _slicedJobIds() {
      return this._slicedJobIds;
   }

   private void _slicedJobIds_$eq(Set<Object> x$1) {
      this._slicedJobIds = x$1;
   }

   public int dataSize() {
      return this.data().size();
   }

   public Seq<JobTableRowData> sliceData(int from, int to) {
      Seq r = (Seq)this.data().slice(from, to);
      this._slicedJobIds_$eq(((TraversableOnce)r.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(JobTableRowData x$11) {
            return x$11.jobData().jobId();
         }
      }, Seq$.MODULE$.canBuildFrom())).toSet());
      return r;
   }

   public JobTableRowData org$apache$spark$ui$jobs$JobDataSource$$jobRow(JobData jobData) {
      Option duration = jobData.submissionTime().map(new Serializable(this, jobData) {
         public static final long serialVersionUID = 0L;
         private final JobData jobData$1;

         public final long apply(Date start) {
            long end = BoxesRunTime.unboxToLong(this.jobData$1.completionTime().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(Date x$12) {
                  return x$12.getTime();
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return System.currentTimeMillis();
               }
            }));
            return end - start.getTime();
         }

         public {
            this.jobData$1 = jobData$1;
         }
      });
      String formattedDuration = (String)duration.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(long d) {
            return UIUtils$.MODULE$.formatDuration(d);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Unknown";
         }
      });
      Option submissionTime = jobData.submissionTime();
      String formattedSubmissionTime = (String)submissionTime.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Date date) {
            return UIUtils$.MODULE$.formatDate(date);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Unknown";
         }
      });
      Tuple2 var7 = ApiHelper$.MODULE$.lastStageNameAndDescription(this.store, jobData);
      if (var7 != null) {
         String lastStageName = (String)var7._1();
         String lastStageDescription = (String)var7._2();
         Tuple2 var10 = new Tuple2(lastStageName, lastStageDescription);
         String lastStageName = (String)var10._1();
         String lastStageDescription = (String)var10._2();
         NodeSeq jobDescription = UIUtils$.MODULE$.makeDescription(lastStageDescription, this.basePath, false);
         String detailUrl = (new StringOps(Predef$.MODULE$.augmentString("%s/jobs/job?id=%s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.basePath, BoxesRunTime.boxToInteger(jobData.jobId())}));
         return new JobTableRowData(jobData, lastStageName, lastStageDescription, BoxesRunTime.unboxToLong(duration.getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply() {
               return this.apply$mcJ$sp();
            }

            public long apply$mcJ$sp() {
               return -1L;
            }
         })), formattedDuration, BoxesRunTime.unboxToLong(submissionTime.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(Date x$14) {
               return x$14.getTime();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply() {
               return this.apply$mcJ$sp();
            }

            public long apply$mcJ$sp() {
               return -1L;
            }
         })), formattedSubmissionTime, jobDescription, detailUrl);
      } else {
         throw new MatchError(var7);
      }
   }

   private Ordering<JobTableRowData> ordering(String sortColumn, boolean desc) {
      boolean var5;
      if ("Job Id".equals(sortColumn)) {
         var5 = true;
      } else if ("Job Id (Job Group)".equals(sortColumn)) {
         var5 = true;
      } else {
         var5 = false;
      }

      Ordering ordering;
      if (var5) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(JobTableRowData x$15) {
               return x$15.jobData().jobId();
            }
         }, Ordering.Int$.MODULE$);
      } else if ("Description".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Tuple2<String, String> apply(JobTableRowData x) {
               return new Tuple2<String, String>(x.lastStageDescription(), x.lastStageName());
            }
         }, Ordering$.MODULE$.Tuple2(Ordering.String$.MODULE$, Ordering.String$.MODULE$));
      } else if ("Submitted".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(JobTableRowData x$16) {
               return x$16.submissionTime();
            }
         }, Ordering.Long$.MODULE$);
      } else {
         if (!"Duration".equals(sortColumn)) {
            boolean var7;
            if ("Stages: Succeeded/Total".equals(sortColumn)) {
               var7 = true;
            } else if ("Tasks (for all stages): Succeeded/Total".equals(sortColumn)) {
               var7 = true;
            } else {
               var7 = false;
            }

            if (var7) {
               throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unsortable column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{sortColumn})));
            }

            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{sortColumn})));
         }

         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(JobTableRowData x$17) {
               return x$17.duration();
            }
         }, Ordering.Long$.MODULE$);
      }

      return desc ? ordering.reverse() : ordering;
   }

   public JobDataSource(AppStatusStore store, Seq<JobData> jobs, String basePath, long currentTime, int pageSize, String sortColumn, boolean desc) {
      super(pageSize);
      this.store = store;
      this.basePath = basePath;
      this.data = (Seq)((SeqLike)jobs.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final JobDataSource $outer;

         public final JobTableRowData apply(JobData jobData) {
            return this.$outer.org$apache$spark$ui$jobs$JobDataSource$$jobRow(jobData);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom())).sorted(this.ordering(sortColumn, desc));
      this._slicedJobIds = null;
   }
}
