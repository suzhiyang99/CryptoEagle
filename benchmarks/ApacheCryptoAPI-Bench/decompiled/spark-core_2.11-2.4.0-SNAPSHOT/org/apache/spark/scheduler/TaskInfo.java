package org.apache.spark.scheduler;

import org.apache.spark.TaskState$;
import org.apache.spark.annotation.DeveloperApi;
import scala.Enumeration;
import scala.Predef$;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ue\u0001B\u0001\u0003\u0001-\u0011\u0001\u0002V1tW&sgm\u001c\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001\u0019A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001aD\u0001b\u0005\u0001\u0003\u0006\u0004%\t\u0001F\u0001\u0007i\u0006\u001c8.\u00133\u0016\u0003U\u0001\"!\u0004\f\n\u0005]q!\u0001\u0002'p]\u001eD\u0001\"\u0007\u0001\u0003\u0002\u0003\u0006I!F\u0001\bi\u0006\u001c8.\u00133!\u0011!Y\u0002A!b\u0001\n\u0003a\u0012!B5oI\u0016DX#A\u000f\u0011\u00055q\u0012BA\u0010\u000f\u0005\rIe\u000e\u001e\u0005\tC\u0001\u0011\t\u0011)A\u0005;\u00051\u0011N\u001c3fq\u0002B\u0001b\t\u0001\u0003\u0006\u0004%\t\u0001H\u0001\u000eCR$X-\u001c9u\u001dVl'-\u001a:\t\u0011\u0015\u0002!\u0011!Q\u0001\nu\ta\"\u0019;uK6\u0004HOT;nE\u0016\u0014\b\u0005\u0003\u0005(\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003)a\u0017-\u001e8dQRKW.\u001a\u0005\tS\u0001\u0011\t\u0011)A\u0005+\u0005YA.Y;oG\"$\u0016.\\3!\u0011!Y\u0003A!b\u0001\n\u0003a\u0013AC3yK\u000e,Ho\u001c:JIV\tQ\u0006\u0005\u0002/c9\u0011QbL\u0005\u0003a9\ta\u0001\u0015:fI\u00164\u0017B\u0001\u001a4\u0005\u0019\u0019FO]5oO*\u0011\u0001G\u0004\u0005\tk\u0001\u0011\t\u0011)A\u0005[\u0005YQ\r_3dkR|'/\u00133!\u0011!9\u0004A!b\u0001\n\u0003a\u0013\u0001\u00025pgRD\u0001\"\u000f\u0001\u0003\u0002\u0003\u0006I!L\u0001\u0006Q>\u001cH\u000f\t\u0005\tw\u0001\u0011)\u0019!C\u0001y\u0005aA/Y:l\u0019>\u001c\u0017\r\\5usV\tQ\b\u0005\u0002?\u0005:\u0011q\bQ\u0007\u0002\u0005%\u0011\u0011IA\u0001\r)\u0006\u001c8\u000eT8dC2LG/_\u0005\u0003\u0007\u0012\u0013A\u0002V1tW2{7-\u00197jifT!!\u0011\u0002\t\u0011\u0019\u0003!\u0011!Q\u0001\nu\nQ\u0002^1tW2{7-\u00197jif\u0004\u0003\u0002\u0003%\u0001\u0005\u000b\u0007I\u0011A%\u0002\u0017M\u0004XmY;mCRLg/Z\u000b\u0002\u0015B\u0011QbS\u0005\u0003\u0019:\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005O\u0001\t\u0005\t\u0015!\u0003K\u00031\u0019\b/Z2vY\u0006$\u0018N^3!\u0011\u0015\u0001\u0006\u0001\"\u0001R\u0003\u0019a\u0014N\\5u}QI!k\u0015+V-^C\u0016L\u0017\t\u0003\u007f\u0001AQaE(A\u0002UAQaG(A\u0002uAQaI(A\u0002uAQaJ(A\u0002UAQaK(A\u00025BQaN(A\u00025BQaO(A\u0002uBQ\u0001S(A\u0002)Cq\u0001\u0018\u0001A\u0002\u0013\u0005A#A\thKR$\u0018N\\4SKN,H\u000e\u001e+j[\u0016DqA\u0018\u0001A\u0002\u0013\u0005q,A\u000bhKR$\u0018N\\4SKN,H\u000e\u001e+j[\u0016|F%Z9\u0015\u0005\u0001\u001c\u0007CA\u0007b\u0013\t\u0011gB\u0001\u0003V]&$\bb\u00023^\u0003\u0003\u0005\r!F\u0001\u0004q\u0012\n\u0004B\u00024\u0001A\u0003&Q#\u0001\nhKR$\u0018N\\4SKN,H\u000e\u001e+j[\u0016\u0004\u0003\"\u00025\u0001\t\u0003I\u0017\u0001D1dGVlW\u000f\\1cY\u0016\u001cX#\u00016\u0011\u0007-\u001chO\u0004\u0002mc:\u0011Q\u000e]\u0007\u0002]*\u0011qNC\u0001\u0007yI|w\u000e\u001e \n\u0003=I!A\u001d\b\u0002\u000fA\f7m[1hK&\u0011A/\u001e\u0002\u0004'\u0016\f(B\u0001:\u000f!\tyt/\u0003\u0002y\u0005\ty\u0011iY2v[Vd\u0017M\u00197f\u0013:4w\u000e\u0003\u0004{\u0001\u0001\u0006KA[\u0001\u000e?\u0006\u001c7-^7vY\u0006\u0014G.Z:\t\rq\u0004A\u0011\u0001\u0003~\u0003=\u0019X\r^!dGVlW\u000f\\1cY\u0016\u001cHC\u00011\u007f\u0011\u0015y8\u00101\u0001k\u0003=qWm^!dGVlW\u000f\\1cY\u0016\u001c\b\u0002CA\u0002\u0001\u0001\u0007I\u0011\u0001\u000b\u0002\u0015\u0019Lg.[:i)&lW\rC\u0005\u0002\b\u0001\u0001\r\u0011\"\u0001\u0002\n\u0005qa-\u001b8jg\"$\u0016.\\3`I\u0015\fHc\u00011\u0002\f!AA-!\u0002\u0002\u0002\u0003\u0007Q\u0003C\u0004\u0002\u0010\u0001\u0001\u000b\u0015B\u000b\u0002\u0017\u0019Lg.[:i)&lW\r\t\u0005\t\u0003'\u0001\u0001\u0019!C\u0001\u0013\u00061a-Y5mK\u0012D\u0011\"a\u0006\u0001\u0001\u0004%\t!!\u0007\u0002\u0015\u0019\f\u0017\u000e\\3e?\u0012*\u0017\u000fF\u0002a\u00037A\u0001\u0002ZA\u000b\u0003\u0003\u0005\rA\u0013\u0005\b\u0003?\u0001\u0001\u0015)\u0003K\u0003\u001d1\u0017-\u001b7fI\u0002B\u0001\"a\t\u0001\u0001\u0004%\t!S\u0001\u0007W&dG.\u001a3\t\u0013\u0005\u001d\u0002\u00011A\u0005\u0002\u0005%\u0012AC6jY2,Gm\u0018\u0013fcR\u0019\u0001-a\u000b\t\u0011\u0011\f)#!AA\u0002)Cq!a\f\u0001A\u0003&!*A\u0004lS2dW\r\u001a\u0011\t\u0011\u0005M\u0002\u0001\"\u0001\u0005\u0003k\t\u0011#\\1sW\u001e+G\u000f^5oOJ+7/\u001e7u)\r\u0001\u0017q\u0007\u0005\b\u0003s\t\t\u00041\u0001\u0016\u0003\u0011!\u0018.\\3\t\u0011\u0005u\u0002\u0001\"\u0001\u0005\u0003\u007f\tA\"\\1sW\u001aKg.[:iK\u0012$R\u0001YA!\u0003?B\u0001\"a\u0011\u0002<\u0001\u0007\u0011QI\u0001\u0006gR\fG/\u001a\t\u0005\u0003\u000f\nIF\u0004\u0003\u0002J\u0005Uc\u0002BA&\u0003'rA!!\u0014\u0002R9\u0019Q.a\u0014\n\u0003%I!a\u0002\u0005\n\u0005\u00151\u0011bAA,\t\u0005IA+Y:l'R\fG/Z\u0005\u0005\u00037\niFA\u0005UCN\\7\u000b^1uK*\u0019\u0011q\u000b\u0003\t\u000f\u0005e\u00121\ba\u0001+!1\u00111\r\u0001\u0005\u0002%\u000bQbZ3ui&twMU3tk2$\bBBA4\u0001\u0011\u0005\u0011*\u0001\u0005gS:L7\u000f[3e\u0011\u0019\tY\u0007\u0001C\u0001\u0013\u0006Q1/^2dKN\u001ch-\u001e7\t\r\u0005=\u0004\u0001\"\u0001J\u0003\u001d\u0011XO\u001c8j]\u001eDa!a\u001d\u0001\t\u0003a\u0013AB:uCR,8\u000f\u0003\u0004\u0002x\u0001!\t\u0001L\u0001\u0003S\u0012Da!a\u001f\u0001\t\u0003!\u0012\u0001\u00033ve\u0006$\u0018n\u001c8\t\u0011\u0005}\u0004\u0001\"\u0001\u0005\u0003\u0003\u000b1\u0002^5nKJ+hN\\5oOR\u0019Q#a!\t\u000f\u0005\u0015\u0015Q\u0010a\u0001+\u0005Y1-\u001e:sK:$H+[7fQ\r\u0001\u0011\u0011\u0012\t\u0005\u0003\u0017\u000b\t*\u0004\u0002\u0002\u000e*\u0019\u0011q\u0012\u0003\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002\u0014\u00065%\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007"
)
public class TaskInfo {
   private final long taskId;
   private final int index;
   private final int attemptNumber;
   private final long launchTime;
   private final String executorId;
   private final String host;
   private final Enumeration.Value taskLocality;
   private final boolean speculative;
   private long gettingResultTime;
   private Seq<AccumulableInfo> _accumulables;
   private long finishTime;
   private boolean failed;
   private boolean killed;

   public long taskId() {
      return this.taskId;
   }

   public int index() {
      return this.index;
   }

   public int attemptNumber() {
      return this.attemptNumber;
   }

   public long launchTime() {
      return this.launchTime;
   }

   public String executorId() {
      return this.executorId;
   }

   public String host() {
      return this.host;
   }

   public Enumeration.Value taskLocality() {
      return this.taskLocality;
   }

   public boolean speculative() {
      return this.speculative;
   }

   public long gettingResultTime() {
      return this.gettingResultTime;
   }

   public void gettingResultTime_$eq(long x$1) {
      this.gettingResultTime = x$1;
   }

   public Seq<AccumulableInfo> accumulables() {
      return this._accumulables;
   }

   public void setAccumulables(Seq<AccumulableInfo> newAccumulables) {
      this._accumulables = newAccumulables;
   }

   public long finishTime() {
      return this.finishTime;
   }

   public void finishTime_$eq(long x$1) {
      this.finishTime = x$1;
   }

   public boolean failed() {
      return this.failed;
   }

   public void failed_$eq(boolean x$1) {
      this.failed = x$1;
   }

   public boolean killed() {
      return this.killed;
   }

   public void killed_$eq(boolean x$1) {
      this.killed = x$1;
   }

   public void markGettingResult(long time) {
      this.gettingResultTime_$eq(time);
   }

   public void markFinished(Enumeration.Value state, long time) {
      label45: {
         Predef$.MODULE$.assert(time > 0L);
         this.finishTime_$eq(time);
         Enumeration.Value var4 = TaskState$.MODULE$.FAILED();
         if (state == null) {
            if (var4 == null) {
               break label45;
            }
         } else if (state.equals(var4)) {
            break label45;
         }

         Enumeration.Value var5 = TaskState$.MODULE$.KILLED();
         if (state == null) {
            if (var5 != null) {
               return;
            }
         } else if (!state.equals(var5)) {
            return;
         }

         this.killed_$eq(true);
         return;
      }

      this.failed_$eq(true);
   }

   public boolean gettingResult() {
      return this.gettingResultTime() != 0L;
   }

   public boolean finished() {
      return this.finishTime() != 0L;
   }

   public boolean successful() {
      return this.finished() && !this.failed() && !this.killed();
   }

   public boolean running() {
      return !this.finished();
   }

   public String status() {
      return this.running() ? (this.gettingResult() ? "GET RESULT" : "RUNNING") : (this.failed() ? "FAILED" : (this.killed() ? "KILLED" : (this.successful() ? "SUCCESS" : "UNKNOWN")));
   }

   public String id() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.index()), BoxesRunTime.boxToInteger(this.attemptNumber())}));
   }

   public long duration() {
      if (this.finished()) {
         return this.finishTime() - this.launchTime();
      } else {
         throw new UnsupportedOperationException("duration() called on unfinished task");
      }
   }

   public long timeRunning(long currentTime) {
      return currentTime - this.launchTime();
   }

   public TaskInfo(long taskId, int index, int attemptNumber, long launchTime, String executorId, String host, Enumeration.Value taskLocality, boolean speculative) {
      this.taskId = taskId;
      this.index = index;
      this.attemptNumber = attemptNumber;
      this.launchTime = launchTime;
      this.executorId = executorId;
      this.host = host;
      this.taskLocality = taskLocality;
      this.speculative = speculative;
      this.gettingResultTime = 0L;
      this._accumulables = Nil$.MODULE$;
      this.finishTime = 0L;
      this.failed = false;
      this.killed = false;
   }
}
