package org.apache.spark;

import java.io.Serializable;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.memory.TaskMemoryManager;
import org.apache.spark.metrics.source.Source;
import org.apache.spark.shuffle.FetchFailedException;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.TaskCompletionListener;
import org.apache.spark.util.TaskFailureListener;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ex!B\u0001\u0003\u0011\u0003I\u0011a\u0003+bg.\u001cuN\u001c;fqRT!a\u0001\u0003\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u00151\u0011AB1qC\u000eDWMC\u0001\b\u0003\ry'oZ\u0002\u0001!\tQ1\"D\u0001\u0003\r\u0015a!\u0001#\u0001\u000e\u0005-!\u0016m]6D_:$X\r\u001f;\u0014\u0007-qA\u0003\u0005\u0002\u0010%5\t\u0001CC\u0001\u0012\u0003\u0015\u00198-\u00197b\u0013\t\u0019\u0002C\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001fUI!A\u0006\t\u0003\u0019M+'/[1mSj\f'\r\\3\t\u000baYA\u0011A\r\u0002\rqJg.\u001b;?)\u0005I\u0001\"B\u000e\f\t\u0003a\u0012aA4fiR\tQ\u0004\u0005\u0002\u000b=\u0019)ABAA\u0001?M\u0019aD\u0004\u0011\u0011\u0005\u00052S\"\u0001\u0012\u000b\u0005\r\"\u0013AA5p\u0015\u0005)\u0013\u0001\u00026bm\u0006L!A\u0006\u0012\t\u000baqB\u0011\u0001\u000f\t\u000b%rb\u0011\u0001\u0016\u0002\u0017%\u001c8i\\7qY\u0016$X\r\u001a\u000b\u0002WA\u0011q\u0002L\u0005\u0003[A\u0011qAQ8pY\u0016\fg\u000eC\u00030=\u0019\u0005!&A\u0007jg&sG/\u001a:skB$X\r\u001a\u0005\u0006cy1\tAK\u0001\u0011SN\u0014VO\u001c8j]\u001edunY1mYfDC\u0001M\u001a7qA\u0011q\u0002N\u0005\u0003kA\u0011!\u0002Z3qe\u0016\u001c\u0017\r^3eC\u00059\u0014!\u000f'pG\u0006d\u0007%\u001a=fGV$\u0018n\u001c8!o\u0006\u001c\bE]3n_Z,G\r\f\u0011t_\u0002\"\b.[:!C2<\u0018-_:!e\u0016$XO\u001d8tA\u0019\fGn]3\"\u0003e\nQA\r\u00181]ABQa\u000f\u0010\u0007\u0002q\n\u0011$\u00193e)\u0006\u001c8nQ8na2,G/[8o\u0019&\u001cH/\u001a8feR\u0011Q$\u0010\u0005\u0006}i\u0002\raP\u0001\tY&\u001cH/\u001a8feB\u0011\u0001iQ\u0007\u0002\u0003*\u0011!IA\u0001\u0005kRLG.\u0003\u0002E\u0003\n1B+Y:l\u0007>l\u0007\u000f\\3uS>tG*[:uK:,'\u000fC\u0003<=\u0011\u0005a\t\u0006\u0002\u001e\u000f\")\u0001*\u0012a\u0001\u0013\u0006\ta\r\u0005\u0003\u0010\u0015va\u0015BA&\u0011\u0005%1UO\\2uS>t\u0017\u0007\u0005\u0002\u0010\u001b&\u0011a\n\u0005\u0002\u0005+:LG\u000fC\u0003Q=\u0019\u0005\u0011+\u0001\fbI\u0012$\u0016m]6GC&dWO]3MSN$XM\\3s)\ti\"\u000bC\u0003?\u001f\u0002\u00071\u000b\u0005\u0002A)&\u0011Q+\u0011\u0002\u0014)\u0006\u001c8NR1jYV\u0014X\rT5ti\u0016tWM\u001d\u0005\u0006!z!\ta\u0016\u000b\u0003;aCQ\u0001\u0013,A\u0002e\u0003Ra\u0004.\u001e92K!a\u0017\t\u0003\u0013\u0019+hn\u0019;j_:\u0014\u0004CA/f\u001d\tq6M\u0004\u0002`E6\t\u0001M\u0003\u0002b\u0011\u00051AH]8pizJ\u0011!E\u0005\u0003IB\tq\u0001]1dW\u0006<W-\u0003\u0002gO\nIA\u000b\u001b:po\u0006\u0014G.\u001a\u0006\u0003IBAQ!\u001b\u0010\u0007\u0002)\fqa\u001d;bO\u0016LE\rF\u0001l!\tyA.\u0003\u0002n!\t\u0019\u0011J\u001c;\t\u000b=tb\u0011\u00016\u0002%M$\u0018mZ3BiR,W\u000e\u001d;Ok6\u0014WM\u001d\u0005\u0006cz1\tA[\u0001\fa\u0006\u0014H/\u001b;j_:LE\rC\u0003t=\u0019\u0005!.A\u0007biR,W\u000e\u001d;Ok6\u0014WM\u001d\u0005\u0006kz1\tA^\u0001\u000ei\u0006\u001c8.\u0011;uK6\u0004H/\u00133\u0015\u0003]\u0004\"a\u0004=\n\u0005e\u0004\"\u0001\u0002'p]\u001eDQa\u001f\u0010\u0007\u0002q\f\u0001cZ3u\u0019>\u001c\u0017\r\u001c)s_B,'\u000f^=\u0015\u0007u\fI\u0001E\u0002\u007f\u0003\u0007q!aD@\n\u0007\u0005\u0005\u0001#\u0001\u0004Qe\u0016$WMZ\u0005\u0005\u0003\u000b\t9A\u0001\u0004TiJLgn\u001a\u0006\u0004\u0003\u0003\u0001\u0002BBA\u0006u\u0002\u0007Q0A\u0002lKfDq!a\u0004\u001f\r\u0003\t\t\"A\u0006uCN\\W*\u001a;sS\u000e\u001cHCAA\n!\u0011\t)\"a\u0007\u000e\u0005\u0005]!bAA\r\u0005\u0005AQ\r_3dkR|'/\u0003\u0003\u0002\u001e\u0005]!a\u0003+bg.lU\r\u001e:jGNDC!!\u0004\u0002\"A!\u00111EA\u0015\u001b\t\t)CC\u0002\u0002(\t\t!\"\u00198o_R\fG/[8o\u0013\u0011\tY#!\n\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5\t\u000f\u0005=bD\"\u0001\u00022\u0005\tr-\u001a;NKR\u0014\u0018nY:T_V\u00148-Z:\u0015\t\u0005M\u0012\u0011\n\t\u0006;\u0006U\u0012\u0011H\u0005\u0004\u0003o9'aA*fcB!\u00111HA#\u001b\t\tiD\u0003\u0003\u0002@\u0005\u0005\u0013AB:pkJ\u001cWMC\u0002\u0002D\t\tq!\\3ue&\u001c7/\u0003\u0003\u0002H\u0005u\"AB*pkJ\u001cW\rC\u0004\u0002L\u00055\u0002\u0019A?\u0002\u0015M|WO]2f\u001d\u0006lW\r\u000b\u0003\u0002.\u0005\u0005\u0002\u0002CA)=\u0019\u0005!!a\u0015\u0002+-LG\u000e\u001c+bg.Le-\u00138uKJ\u0014X\u000f\u001d;fIR\tA\n\u0003\u0005\u0002Xy1\tAAA-\u000359W\r^&jY2\u0014V-Y:p]R\u0011\u00111\f\t\u0005\u001f\u0005uS0C\u0002\u0002`A\u0011aa\u00149uS>t\u0007\u0002CA2=\u0019\u0005!!!\u001a\u0002#Q\f7o['f[>\u0014\u00180T1oC\u001e,'\u000f\u0006\u0002\u0002hA!\u0011\u0011NA8\u001b\t\tYGC\u0002\u0002n\t\ta!\\3n_JL\u0018\u0002BA9\u0003W\u0012\u0011\u0003V1tW6+Wn\u001c:z\u001b\u0006t\u0017mZ3s\u0011!\t)H\bD\u0001\u0005\u0005]\u0014a\u0005:fO&\u001cH/\u001a:BG\u000e,X.\u001e7bi>\u0014Hc\u0001'\u0002z!A\u00111PA:\u0001\u0004\ti(A\u0001ba\u0019\ty(!#\u0002\u001eB9\u0001)!!\u0002\u0006\u0006m\u0015bAAB\u0003\ni\u0011iY2v[Vd\u0017\r^8s-J\u0002B!a\"\u0002\n2\u0001A\u0001DAF\u0003s\n\t\u0011!A\u0003\u0002\u00055%aA0%cE!\u0011qRAK!\ry\u0011\u0011S\u0005\u0004\u0003'\u0003\"a\u0002(pi\"Lgn\u001a\t\u0004\u001f\u0005]\u0015bAAM!\t\u0019\u0011I\\=\u0011\t\u0005\u001d\u0015Q\u0014\u0003\r\u0003?\u000bI(!A\u0001\u0002\u000b\u0005\u0011Q\u0012\u0002\u0004?\u0012\u0012\u0004\u0002CAR=\u0019\u0005!!!*\u0002\u001dM,GOR3uG\"4\u0015-\u001b7fIR\u0019A*a*\t\u0011\u0005%\u0016\u0011\u0015a\u0001\u0003W\u000b1BZ3uG\"4\u0015-\u001b7fIB!\u0011QVAZ\u001b\t\tyKC\u0002\u00022\n\tqa\u001d5vM\u001adW-\u0003\u0003\u00026\u0006=&\u0001\u0006$fi\u000eDg)Y5mK\u0012,\u0005pY3qi&|g\u000e\u0003\u0004\u0002:.!\tA[\u0001\u000fO\u0016$\b+\u0019:uSRLwN\\%e\u0011!\til\u0003Q\u0001\n\u0005}\u0016a\u0003;bg.\u001cuN\u001c;fqR\u0004R!!1\u0002Hvi!!a1\u000b\u0007\u0005\u0015G%\u0001\u0003mC:<\u0017\u0002BAe\u0003\u0007\u00141\u0002\u00165sK\u0006$Gj\\2bY\"A\u0011QZ\u0006\u0005\u0012\t\ty-\u0001\btKR$\u0016m]6D_:$X\r\u001f;\u0015\u00071\u000b\t\u000eC\u0004\u0002T\u0006-\u0007\u0019A\u000f\u0002\u0005Q\u001c\u0007\u0002CAl\u0017\u0011E!!a\u0015\u0002\u000bUt7/\u001a;\t\u0011\u0005m7\u0002\"\u0001\u0003\u0003;\fQ!Z7qif$\"!a8\u0011\u0007)\t\t/C\u0002\u0002d\n\u0011q\u0002V1tW\u000e{g\u000e^3yi&k\u0007\u000f\u001c\u0005\n\u0003O\\\u0011\u0011!C\u0005\u0003S\f1B]3bIJ+7o\u001c7wKR\u0011\u00111\u001e\t\u0005\u0003\u0003\fi/\u0003\u0003\u0002p\u0006\r'AB(cU\u0016\u001cG\u000f"
)
public abstract class TaskContext implements Serializable {
   public static int getPartitionId() {
      return TaskContext$.MODULE$.getPartitionId();
   }

   public static TaskContext get() {
      return TaskContext$.MODULE$.get();
   }

   public abstract boolean isCompleted();

   public abstract boolean isInterrupted();

   /** @deprecated */
   public abstract boolean isRunningLocally();

   public abstract TaskContext addTaskCompletionListener(TaskCompletionListener var1);

   public TaskContext addTaskCompletionListener(Function1<TaskContext, BoxedUnit> f) {
      return this.addTaskCompletionListener(new TaskCompletionListener(this, f) {
         private final Function1 f$1;

         public void onTaskCompletion(TaskContext context) {
            this.f$1.apply(context);
         }

         public {
            this.f$1 = f$1;
         }
      });
   }

   public abstract TaskContext addTaskFailureListener(TaskFailureListener var1);

   public TaskContext addTaskFailureListener(Function2<TaskContext, Throwable, BoxedUnit> f) {
      return this.addTaskFailureListener(new TaskFailureListener(this, f) {
         private final Function2 f$2;

         public void onTaskFailure(TaskContext context, Throwable error) {
            this.f$2.apply(context, error);
         }

         public {
            this.f$2 = f$2;
         }
      });
   }

   public abstract int stageId();

   public abstract int stageAttemptNumber();

   public abstract int partitionId();

   public abstract int attemptNumber();

   public abstract long taskAttemptId();

   public abstract String getLocalProperty(String var1);

   @DeveloperApi
   public abstract TaskMetrics taskMetrics();

   @DeveloperApi
   public abstract Seq<Source> getMetricsSources(String var1);

   public abstract void killTaskIfInterrupted();

   public abstract Option<String> getKillReason();

   public abstract TaskMemoryManager taskMemoryManager();

   public abstract void registerAccumulator(AccumulatorV2<?, ?> var1);

   public abstract void setFetchFailed(FetchFailedException var1);
}
