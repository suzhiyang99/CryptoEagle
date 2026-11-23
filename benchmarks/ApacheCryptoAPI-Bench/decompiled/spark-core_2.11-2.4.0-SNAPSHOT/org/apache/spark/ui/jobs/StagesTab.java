package org.apache.spark.ui.jobs;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.scheduler.SchedulingMode$;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.StageStatus;
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
   bytes = "\u0006\u0001}3Q!\u0001\u0002\u0001\t1\u0011\u0011b\u0015;bO\u0016\u001cH+\u00192\u000b\u0005\r!\u0011\u0001\u00026pENT!!\u0002\u0004\u0002\u0005UL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0010\u001b\u0005!\u0011B\u0001\t\u0005\u0005)\u0019\u0006/\u0019:l+&#\u0016M\u0019\u0005\t%\u0001\u0011)\u0019!C\u0001)\u00051\u0001/\u0019:f]R\u001c\u0001!F\u0001\u0016!\tqa#\u0003\u0002\u0018\t\t91\u000b]1sWVK\u0005\u0002C\r\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002\u000fA\f'/\u001a8uA!A1\u0004\u0001BC\u0002\u0013\u0005A$A\u0003ti>\u0014X-F\u0001\u001e!\tq\u0012%D\u0001 \u0015\t\u0001c!\u0001\u0004ti\u0006$Xo]\u0005\u0003E}\u0011a\"\u00119q'R\fG/^:Ti>\u0014X\r\u0003\u0005%\u0001\t\u0005\t\u0015!\u0003\u001e\u0003\u0019\u0019Ho\u001c:fA!)a\u0005\u0001C\u0001O\u00051A(\u001b8jiz\"2\u0001\u000b\u0016,!\tI\u0003!D\u0001\u0003\u0011\u0015\u0011R\u00051\u0001\u0016\u0011\u0015YR\u00051\u0001\u001e\u0011\u001di\u0003A1A\u0005\u00029\n!a]2\u0016\u0003=\u00022\u0001M\u001a6\u001b\u0005\t$\"\u0001\u001a\u0002\u000bM\u001c\u0017\r\\1\n\u0005Q\n$AB(qi&|g\u000e\u0005\u00027o5\ta!\u0003\u00029\r\ta1\u000b]1sW\u000e{g\u000e^3yi\"1!\b\u0001Q\u0001\n=\n1a]2!\u0011\u001da\u0004A1A\u0005\u0002u\nAaY8oMV\ta\b\u0005\u00027\u007f%\u0011\u0001I\u0002\u0002\n'B\f'o[\"p]\u001aDaA\u0011\u0001!\u0002\u0013q\u0014!B2p]\u001a\u0004\u0003b\u0002#\u0001\u0005\u0004%\t!R\u0001\fW&dG.\u00128bE2,G-F\u0001G!\t\u0001t)\u0003\u0002Ic\t9!i\\8mK\u0006t\u0007B\u0002&\u0001A\u0003%a)\u0001\u0007lS2dWI\\1cY\u0016$\u0007\u0005C\u0003M\u0001\u0011\u0005Q)A\bjg\u001a\u000b\u0017N]*dQ\u0016$W\u000f\\3s\u0011\u0015q\u0005\u0001\"\u0001P\u0003EA\u0017M\u001c3mK.KG\u000e\u001c*fcV,7\u000f\u001e\u000b\u0003!N\u0003\"\u0001M)\n\u0005I\u000b$\u0001B+oSRDQ\u0001V'A\u0002U\u000bqA]3rk\u0016\u001cH\u000f\u0005\u0002W;6\tqK\u0003\u0002Y3\u0006!\u0001\u000e\u001e;q\u0015\tQ6,A\u0004tKJ4H.\u001a;\u000b\u0003q\u000bQA[1wCbL!AX,\u0003%!#H\u000f]*feZdW\r\u001e*fcV,7\u000f\u001e"
)
public class StagesTab extends SparkUITab {
   private final SparkUI parent;
   private final AppStatusStore store;
   private final Option<SparkContext> sc;
   private final SparkConf conf;
   private final boolean killEnabled;

   public SparkUI parent() {
      return this.parent;
   }

   public AppStatusStore store() {
      return this.store;
   }

   public Option<SparkContext> sc() {
      return this.sc;
   }

   public SparkConf conf() {
      return this.conf;
   }

   public boolean killEnabled() {
      return this.killEnabled;
   }

   public boolean isFairScheduler() {
      return this.store().environmentInfo().sparkProperties().contains(new Tuple2("spark.scheduler.mode", SchedulingMode$.MODULE$.FAIR().toString()));
   }

   public void handleKillRequest(HttpServletRequest request) {
      if (this.killEnabled() && this.parent().securityManager().checkModifyPermissions(request.getRemoteUser())) {
         Option stageId = Option$.MODULE$.apply(UIUtils$.MODULE$.stripXSS(request.getParameter("id"))).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(String x$1) {
               return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toInt();
            }
         });
         stageId.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final StagesTab $outer;

            public final void apply(int id) {
               this.apply$mcVI$sp(id);
            }

            public void apply$mcVI$sp(int id) {
               this.$outer.store().asOption(new Serializable(this, id) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int id$1;

                  public final StageData apply() {
                     return this.$outer.org$apache$spark$ui$jobs$StagesTab$$anonfun$$$outer().store().lastStageAttempt(this.id$1);
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

                  public final void apply(StageData stage) {
                     label22: {
                        StageStatus status = stage.status();
                        StageStatus var3 = StageStatus.ACTIVE;
                        if (status == null) {
                           if (var3 == null) {
                              break label22;
                           }
                        } else if (status.equals(var3)) {
                           break label22;
                        }

                        StageStatus var4 = StageStatus.PENDING;
                        if (status == null) {
                           if (var4 != null) {
                              return;
                           }
                        } else if (!status.equals(var4)) {
                           return;
                        }
                     }

                     this.$outer.org$apache$spark$ui$jobs$StagesTab$$anonfun$$$outer().sc().foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final void apply(SparkContext x$2) {
                           x$2.cancelStage(this.$outer.id$1, "killed via the Web UI");
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
            public StagesTab org$apache$spark$ui$jobs$StagesTab$$anonfun$$$outer() {
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

   public StagesTab(SparkUI parent, AppStatusStore store) {
      super(parent, "stages");
      this.parent = parent;
      this.store = store;
      this.sc = parent.sc();
      this.conf = parent.conf();
      this.killEnabled = parent.killEnabled();
      this.attachPage(new AllStagesPage(this));
      this.attachPage(new StagePage(this, store));
      this.attachPage(new PoolPage(this));
   }
}
