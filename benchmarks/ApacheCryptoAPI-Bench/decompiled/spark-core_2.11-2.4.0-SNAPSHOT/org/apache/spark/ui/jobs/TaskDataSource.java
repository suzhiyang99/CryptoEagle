package org.apache.spark.ui.jobs;

import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.ExecutorSummary;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.TaskData;
import org.apache.spark.ui.PagedDataSource;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.collection.mutable.HashMap;
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ea!B\u0001\u0003\u0001\u0011a!A\u0004+bg.$\u0015\r^1T_V\u00148-\u001a\u0006\u0003\u0007\u0011\tAA[8cg*\u0011QAB\u0001\u0003k&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u00012AD\b\u0012\u001b\u0005!\u0011B\u0001\t\u0005\u0005=\u0001\u0016mZ3e\t\u0006$\u0018mU8ve\u000e,\u0007C\u0001\n\u001a\u001b\u0005\u0019\"B\u0001\u000b\u0016\u0003\t1\u0018G\u0003\u0002\u0017/\u0005\u0019\u0011\r]5\u000b\u0005a1\u0011AB:uCR,8/\u0003\u0002\u001b'\tAA+Y:l\t\u0006$\u0018\r\u0003\u0005\u001d\u0001\t\u0005\t\u0015!\u0003\u001f\u0003\u0015\u0019H/Y4f\u0007\u0001\u0001\"AE\u0010\n\u0005\u0001\u001a\"!C*uC\u001e,G)\u0019;b\u0011!\u0011\u0003A!A!\u0002\u0013\u0019\u0013aC2veJ,g\u000e\u001e+j[\u0016\u0004\"\u0001J\u0014\u000e\u0003\u0015R\u0011AJ\u0001\u0006g\u000e\fG.Y\u0005\u0003Q\u0015\u0012A\u0001T8oO\"I!\u0006\u0001B\u0001B\u0003%1FL\u0001\ta\u0006<WmU5{KB\u0011A\u0005L\u0005\u0003[\u0015\u00121!\u00138u\u0013\tQs\u0002\u0003\u00051\u0001\t\u0005\t\u0015!\u00032\u0003)\u0019xN\u001d;D_2,XN\u001c\t\u0003eUr!\u0001J\u001a\n\u0005Q*\u0013A\u0002)sK\u0012,g-\u0003\u00027o\t11\u000b\u001e:j]\u001eT!\u0001N\u0013\t\u0011e\u0002!\u0011!Q\u0001\ni\nA\u0001Z3tGB\u0011AeO\u0005\u0003y\u0015\u0012qAQ8pY\u0016\fg\u000e\u0003\u0005?\u0001\t\u0005\t\u0015!\u0003@\u0003\u0015\u0019Ho\u001c:f!\t\u0001\u0015)D\u0001\u0018\u0013\t\u0011uC\u0001\bBaB\u001cF/\u0019;vgN#xN]3\t\u000b\u0011\u0003A\u0011A#\u0002\rqJg.\u001b;?)\u001d1\u0005*\u0013&L\u00196\u0003\"a\u0012\u0001\u000e\u0003\tAQ\u0001H\"A\u0002yAQAI\"A\u0002\rBQAK\"A\u0002-BQ\u0001M\"A\u0002EBQ!O\"A\u0002iBQAP\"A\u0002}Bqa\u0014\u0001C\u0002\u0013%\u0001+\u0001\tfq\u0016\u001cW\u000f^8s\u0013\u0012$v\u000eT8hgV\t\u0011\u000b\u0005\u0003S/FJV\"A*\u000b\u0005Q+\u0016aB7vi\u0006\u0014G.\u001a\u0006\u0003-\u0016\n!bY8mY\u0016\u001cG/[8o\u0013\tA6KA\u0004ICNDW*\u00199\u0011\tIR\u0016'M\u0005\u00037^\u00121!T1q\u0011\u0019i\u0006\u0001)A\u0005#\u0006\tR\r_3dkR|'/\u00133U_2{wm\u001d\u0011\t\u000f}\u0003\u0001\u0019!C\u0005A\u0006aq\f^1tWN$vn\u00155poV\t\u0011\rE\u0002cUFq!a\u00195\u000f\u0005\u0011<W\"A3\u000b\u0005\u0019l\u0012A\u0002\u001fs_>$h(C\u0001'\u0013\tIW%A\u0004qC\u000e\\\u0017mZ3\n\u0005-d'aA*fc*\u0011\u0011.\n\u0005\b]\u0002\u0001\r\u0011\"\u0003p\u0003AyF/Y:lgR{7\u000b[8x?\u0012*\u0017\u000f\u0006\u0002qgB\u0011A%]\u0005\u0003e\u0016\u0012A!\u00168ji\"9A/\\A\u0001\u0002\u0004\t\u0017a\u0001=%c!1a\u000f\u0001Q!\n\u0005\fQb\u0018;bg.\u001cHk\\*i_^\u0004\u0003\"\u0002=\u0001\t\u0003J\u0018\u0001\u00033bi\u0006\u001c\u0016N_3\u0016\u0003-BQa\u001f\u0001\u0005Bq\f\u0011b\u001d7jG\u0016$\u0015\r^1\u0015\u0007\u0005lx\u0010C\u0003\u007fu\u0002\u00071&\u0001\u0003ge>l\u0007BBA\u0001u\u0002\u00071&\u0001\u0002u_\"1\u0011Q\u0001\u0001\u0005\u0002\u0001\fQ\u0001^1tWNDq!!\u0003\u0001\t\u0003\tY!\u0001\u0007fq\u0016\u001cW\u000f^8s\u0019><7\u000fF\u0002Z\u0003\u001bAq!a\u0004\u0002\b\u0001\u0007\u0011'\u0001\u0002jI\u0002"
)
public class TaskDataSource extends PagedDataSource<TaskData> {
   private final StageData stage;
   private final String sortColumn;
   private final boolean desc;
   public final AppStatusStore org$apache$spark$ui$jobs$TaskDataSource$$store;
   private final HashMap<String, Map<String, String>> executorIdToLogs;
   private Seq<TaskData> _tasksToShow;

   private HashMap<String, Map<String, String>> executorIdToLogs() {
      return this.executorIdToLogs;
   }

   private Seq<TaskData> _tasksToShow() {
      return this._tasksToShow;
   }

   private void _tasksToShow_$eq(Seq<TaskData> x$1) {
      this._tasksToShow = x$1;
   }

   public int dataSize() {
      return this.stage.numTasks();
   }

   public Seq<TaskData> sliceData(int from, int to) {
      if (this._tasksToShow() == null) {
         this._tasksToShow_$eq(this.org$apache$spark$ui$jobs$TaskDataSource$$store.taskList(this.stage.stageId(), this.stage.attemptId(), from, to - from, ApiHelper$.MODULE$.indexName(this.sortColumn), !this.desc));
      }

      return this._tasksToShow();
   }

   public Seq<TaskData> tasks() {
      return this._tasksToShow();
   }

   public Map<String, String> executorLogs(String id) {
      return (Map)this.executorIdToLogs().getOrElseUpdate(id, new Serializable(this, id) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskDataSource $outer;
         public final String id$1;

         public final Map<String, String> apply() {
            return (Map)this.$outer.org$apache$spark$ui$jobs$TaskDataSource$$store.asOption(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final ExecutorSummary apply() {
                  return this.$outer.org$apache$spark$ui$jobs$TaskDataSource$$anonfun$$$outer().org$apache$spark$ui$jobs$TaskDataSource$$store.executorSummary(this.$outer.id$1);
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

               public final Map<String, String> apply(ExecutorSummary x$14) {
                  return x$14.executorLogs();
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Map<String, Nothing$> apply() {
                  return Predef$.MODULE$.Map().empty();
               }
            });
         }

         // $FF: synthetic method
         public TaskDataSource org$apache$spark$ui$jobs$TaskDataSource$$anonfun$$$outer() {
            return this.$outer;
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

   public TaskDataSource(StageData stage, long currentTime, int pageSize, String sortColumn, boolean desc, AppStatusStore store) {
      super(pageSize);
      this.stage = stage;
      this.sortColumn = sortColumn;
      this.desc = desc;
      this.org$apache$spark$ui$jobs$TaskDataSource$$store = store;
      this.executorIdToLogs = new HashMap<String, Map<String, String>>();
      this._tasksToShow = null;
   }
}
