package org.apache.spark.ui.jobs;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.JobExecutionStatus;
import org.apache.spark.SparkContext;
import org.apache.spark.scheduler.SchedulingMode$;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.ui.SparkUI;
import org.apache.spark.ui.SparkUITab;
import org.apache.spark.ui.UIUtils$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001m3Q!\u0001\u0002\u0001\t1\u0011qAS8cgR\u000b'M\u0003\u0002\u0004\t\u0005!!n\u001c2t\u0015\t)a!\u0001\u0002vS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011abD\u0007\u0002\t%\u0011\u0001\u0003\u0002\u0002\u000b'B\f'o[+J)\u0006\u0014\u0007\u0002\u0003\n\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000b\u0002\rA\f'/\u001a8u\u0007\u0001\u0001\"AD\u000b\n\u0005Y!!aB*qCJ\\W+\u0013\u0005\t1\u0001\u0011\t\u0011)A\u00053\u0005)1\u000f^8sKB\u0011!$H\u0007\u00027)\u0011ADB\u0001\u0007gR\fG/^:\n\u0005yY\"AD!qaN#\u0018\r^;t'R|'/\u001a\u0005\u0006A\u0001!\t!I\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007\t\"S\u0005\u0005\u0002$\u00015\t!\u0001C\u0003\u0013?\u0001\u0007A\u0003C\u0003\u0019?\u0001\u0007\u0011\u0004C\u0004(\u0001\t\u0007I\u0011\u0001\u0015\u0002\u0005M\u001cW#A\u0015\u0011\u0007)js&D\u0001,\u0015\u0005a\u0013!B:dC2\f\u0017B\u0001\u0018,\u0005\u0019y\u0005\u000f^5p]B\u0011\u0001'M\u0007\u0002\r%\u0011!G\u0002\u0002\r'B\f'o[\"p]R,\u0007\u0010\u001e\u0005\u0007i\u0001\u0001\u000b\u0011B\u0015\u0002\u0007M\u001c\u0007\u0005C\u00047\u0001\t\u0007I\u0011A\u001c\u0002\u0017-LG\u000e\\#oC\ndW\rZ\u000b\u0002qA\u0011!&O\u0005\u0003u-\u0012qAQ8pY\u0016\fg\u000e\u0003\u0004=\u0001\u0001\u0006I\u0001O\u0001\rW&dG.\u00128bE2,G\r\t\u0005\u0006}\u0001!\taN\u0001\u0010SN4\u0015-\u001b:TG\",G-\u001e7fe\")\u0001\t\u0001C\u0001\u0003\u0006aq-\u001a;Ta\u0006\u00148.V:feV\t!\t\u0005\u0002D\r:\u0011!\u0006R\u0005\u0003\u000b.\na\u0001\u0015:fI\u00164\u0017BA$I\u0005\u0019\u0019FO]5oO*\u0011Qi\u000b\u0005\u0006\u0015\u0002!\taS\u0001\u0012Q\u0006tG\r\\3LS2d'+Z9vKN$HC\u0001'P!\tQS*\u0003\u0002OW\t!QK\\5u\u0011\u0015\u0001\u0016\n1\u0001R\u0003\u001d\u0011X-];fgR\u0004\"AU-\u000e\u0003MS!\u0001V+\u0002\t!$H\u000f\u001d\u0006\u0003-^\u000bqa]3sm2,GOC\u0001Y\u0003\u0015Q\u0017M^1y\u0013\tQ6K\u0001\nIiR\u00048+\u001a:wY\u0016$(+Z9vKN$\b"
)
public class JobsTab extends SparkUITab {
   private final SparkUI parent;
   public final AppStatusStore org$apache$spark$ui$jobs$JobsTab$$store;
   private final Option<SparkContext> sc;
   private final boolean killEnabled;

   public Option<SparkContext> sc() {
      return this.sc;
   }

   public boolean killEnabled() {
      return this.killEnabled;
   }

   public boolean isFairScheduler() {
      return this.org$apache$spark$ui$jobs$JobsTab$$store.environmentInfo().sparkProperties().contains(new Tuple2("spark.scheduler.mode", SchedulingMode$.MODULE$.FAIR().toString()));
   }

   public String getSparkUser() {
      return this.parent.getSparkUser();
   }

   public void handleKillRequest(HttpServletRequest request) {
      if (this.killEnabled() && this.parent.securityManager().checkModifyPermissions(request.getRemoteUser())) {
         Option jobId = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("id"))).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(String x$1) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toInt();
            }
         });
         jobId.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final JobsTab $outer;

            public final void apply(int id) {
               this.apply$mcVI$sp(id);
            }

            public void apply$mcVI$sp(int id) {
               this.$outer.org$apache$spark$ui$jobs$JobsTab$$store.asOption(new Serializable(this, id) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int id$1;

                  public final JobData apply() {
                     return this.$outer.org$apache$spark$ui$jobs$JobsTab$$anonfun$$$outer().org$apache$spark$ui$jobs$JobsTab$$store.job(this.id$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.id$1 = id$1;
                     }
                  }
               }).foreach(new Serializable(this, id) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  public final int id$1;

                  public final void apply(JobData job) {
                     JobExecutionStatus var10000 = job.status();
                     JobExecutionStatus var2 = JobExecutionStatus.RUNNING;
                     if (var10000 == null) {
                        if (var2 != null) {
                           return;
                        }
                     } else if (!var10000.equals(var2)) {
                        return;
                     }

                     this.$outer.org$apache$spark$ui$jobs$JobsTab$$anonfun$$$outer().sc().foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final void apply(SparkContext x$2) {
                           x$2.cancelJob(this.$outer.id$1);
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     Thread.sleep(100L);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.id$1 = id$1;
                     }
                  }
               });
            }

            // $FF: synthetic method
            public JobsTab org$apache$spark$ui$jobs$JobsTab$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
      }

   }

   public JobsTab(SparkUI parent, AppStatusStore store) {
      super(parent, "jobs");
      this.parent = parent;
      this.org$apache$spark$ui$jobs$JobsTab$$store = store;
      this.sc = parent.sc();
      this.killEnabled = parent.killEnabled();
      this.attachPage(new AllJobsPage(this, store));
      this.attachPage(new JobPage(this, store));
   }
}
