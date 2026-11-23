package org.apache.spark.scheduler;

import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.util.AccumulatorV2;
import scala.Enumeration;
import scala.Option;
import scala.Tuple2;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055e\u0001C\u0001\u0003!\u0003\r\t\u0001\u0002\u0006\u0003\u001bQ\u000b7o[*dQ\u0016$W\u000f\\3s\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001aDQA\u0005\u0001\u0005\u0002Q\ta\u0001J5oSR$3\u0001\u0001\u000b\u0002+A\u0011ABF\u0005\u0003/5\u0011A!\u00168ji\"9\u0011\u0004\u0001b\u0001\n\u0013Q\u0012!B1qa&#W#A\u000e\u0011\u0005q\tS\"A\u000f\u000b\u0005yy\u0012\u0001\u00027b]\u001eT\u0011\u0001I\u0001\u0005U\u00064\u0018-\u0003\u0002#;\t11\u000b\u001e:j]\u001eDa\u0001\n\u0001!\u0002\u0013Y\u0012AB1qa&#\u0007\u0005C\u0003'\u0001\u0019\u0005q%\u0001\u0005s_>$\bk\\8m+\u0005A\u0003CA\u0015+\u001b\u0005\u0011\u0011BA\u0016\u0003\u0005\u0011\u0001vn\u001c7\t\u000b5\u0002a\u0011\u0001\u0018\u0002\u001dM\u001c\u0007.\u001a3vY&tw-T8eKV\tq\u0006\u0005\u00021}9\u0011\u0011\u0007\u0010\b\u0003emr!a\r\u001e\u000f\u0005QJdBA\u001b9\u001b\u00051$BA\u001c\u0014\u0003\u0019a$o\\8u}%\t\u0011\"\u0003\u0002\b\u0011%\u0011QAB\u0005\u0003\u0007\u0011I!!\u0010\u0002\u0002\u001dM\u001b\u0007.\u001a3vY&tw-T8eK&\u0011q\b\u0011\u0002\u000f'\u000eDW\rZ;mS:<Wj\u001c3f\u0015\ti$\u0001C\u0003C\u0001\u0019\u0005A#A\u0003ti\u0006\u0014H\u000fC\u0003E\u0001\u0011\u0005A#A\u0007q_N$8\u000b^1si\"{wn\u001b\u0005\u0006\r\u00021\t\u0001F\u0001\u0005gR|\u0007\u000fC\u0003I\u0001\u0019\u0005\u0011*A\u0006tk\nl\u0017\u000e\u001e+bg.\u001cHCA\u000bK\u0011\u0015Yu\t1\u0001M\u0003\u001d!\u0018m]6TKR\u0004\"!K'\n\u00059\u0013!a\u0002+bg.\u001cV\r\u001e\u0005\u0006!\u00021\t!U\u0001\fG\u0006t7-\u001a7UCN\\7\u000fF\u0002\u0016%^CQaU(A\u0002Q\u000bqa\u001d;bO\u0016LE\r\u0005\u0002\r+&\u0011a+\u0004\u0002\u0004\u0013:$\b\"\u0002-P\u0001\u0004I\u0016aD5oi\u0016\u0014(/\u001e9u)\"\u0014X-\u00193\u0011\u00051Q\u0016BA.\u000e\u0005\u001d\u0011un\u001c7fC:DQ!\u0018\u0001\u0007\u0002y\u000bqb[5mYR\u000b7o[!ui\u0016l\u0007\u000f\u001e\u000b\u00053~#W\rC\u0003a9\u0002\u0007\u0011-\u0001\u0004uCN\\\u0017\n\u001a\t\u0003\u0019\tL!aY\u0007\u0003\t1{gn\u001a\u0005\u00061r\u0003\r!\u0017\u0005\u0006Mr\u0003\raZ\u0001\u0007e\u0016\f7o\u001c8\u0011\u0005!\\gB\u0001\u0007j\u0013\tQW\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003E1T!A[\u0007\t\u000b9\u0004a\u0011A8\u0002\u001fM,G\u000fR!H'\u000eDW\rZ;mKJ$\"!\u00069\t\u000bEl\u0007\u0019\u0001:\u0002\u0019\u0011\fwmU2iK\u0012,H.\u001a:\u0011\u0005%\u001a\u0018B\u0001;\u0003\u00051!\u0015iR*dQ\u0016$W\u000f\\3s\u0011\u00151\bA\"\u0001x\u0003I!WMZ1vYR\u0004\u0016M]1mY\u0016d\u0017n]7\u0015\u0003QCQ!\u001f\u0001\u0007\u0002i\f\u0011$\u001a=fGV$xN\u001d%fCJ$(-Z1u%\u0016\u001cW-\u001b<fIR)\u0011l_?\u0002H!)A\u0010\u001fa\u0001O\u00061Q\r_3d\u0013\u0012DQA =A\u0002}\fA\"Y2dk6,\u0006\u000fZ1uKN\u0004R\u0001DA\u0001\u0003\u000bI1!a\u0001\u000e\u0005\u0015\t%O]1z!\u0019a\u0011qA1\u0002\f%\u0019\u0011\u0011B\u0007\u0003\rQ+\b\u000f\\33!\u0019\ti!a\u0006\u0002\u001e9!\u0011qBA\n\u001d\r)\u0014\u0011C\u0005\u0002\u001d%\u0019\u0011QC\u0007\u0002\u000fA\f7m[1hK&!\u0011\u0011DA\u000e\u0005\r\u0019V-\u001d\u0006\u0004\u0003+i\u0001GBA\u0010\u0003_\t\u0019\u0005\u0005\u0005\u0002\"\u0005\u001d\u00121FA!\u001b\t\t\u0019CC\u0002\u0002&\u0011\tA!\u001e;jY&!\u0011\u0011FA\u0012\u00055\t5mY;nk2\fGo\u001c:WeA!\u0011QFA\u0018\u0019\u0001!1\"!\r~\u0003\u0003\u0005\tQ!\u0001\u00024\t\u0019q\fJ\u0019\u0012\t\u0005U\u00121\b\t\u0004\u0019\u0005]\u0012bAA\u001d\u001b\t9aj\u001c;iS:<\u0007c\u0001\u0007\u0002>%\u0019\u0011qH\u0007\u0003\u0007\u0005s\u0017\u0010\u0005\u0003\u0002.\u0005\rCaCA#{\u0006\u0005\t\u0011!B\u0001\u0003g\u00111a\u0018\u00133\u0011\u001d\tI\u0005\u001fa\u0001\u0003\u0017\naB\u00197pG.l\u0015M\\1hKJLE\r\u0005\u0003\u0002N\u0005MSBAA(\u0015\r\t\t\u0006B\u0001\bgR|'/Y4f\u0013\u0011\t)&a\u0014\u0003\u001d\tcwnY6NC:\fw-\u001a:JI\"9\u0011\u0011\f\u0001\u0005\u0002\u0005m\u0013!D1qa2L7-\u0019;j_:LE\rF\u0001h\u0011\u001d\ty\u0006\u0001D\u0001\u0003C\nA\"\u001a=fGV$xN\u001d'pgR$R!FA2\u0003OBq!!\u001a\u0002^\u0001\u0007q-\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012DqAZA/\u0001\u0004\tI\u0007E\u0002*\u0003WJ1!!\u001c\u0003\u0005I)\u00050Z2vi>\u0014Hj\\:t%\u0016\f7o\u001c8\t\u000f\u0005E\u0004A\"\u0001\u0002t\u0005iqo\u001c:lKJ\u0014V-\\8wK\u0012$r!FA;\u0003s\ni\bC\u0004\u0002x\u0005=\u0004\u0019A4\u0002\u0011]|'o[3s\u0013\u0012Dq!a\u001f\u0002p\u0001\u0007q-\u0001\u0003i_N$\bbBA@\u0003_\u0002\raZ\u0001\b[\u0016\u001c8/Y4f\u0011\u001d\t\u0019\t\u0001D\u0001\u0003\u000b\u000bA#\u00199qY&\u001c\u0017\r^5p]\u0006#H/Z7qi&#GCAAD!\u0011a\u0011\u0011R4\n\u0007\u0005-UB\u0001\u0004PaRLwN\u001c"
)
public interface TaskScheduler {
   void org$apache$spark$scheduler$TaskScheduler$_setter_$org$apache$spark$scheduler$TaskScheduler$$appId_$eq(String var1);

   String org$apache$spark$scheduler$TaskScheduler$$appId();

   Pool rootPool();

   Enumeration.Value schedulingMode();

   void start();

   void postStartHook();

   void stop();

   void submitTasks(TaskSet var1);

   void cancelTasks(int var1, boolean var2);

   boolean killTaskAttempt(long var1, boolean var3, String var4);

   void setDAGScheduler(DAGScheduler var1);

   int defaultParallelism();

   boolean executorHeartbeatReceived(String var1, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[] var2, BlockManagerId var3);

   String applicationId();

   void executorLost(String var1, ExecutorLossReason var2);

   void workerRemoved(String var1, String var2, String var3);

   Option<String> applicationAttemptId();
}
