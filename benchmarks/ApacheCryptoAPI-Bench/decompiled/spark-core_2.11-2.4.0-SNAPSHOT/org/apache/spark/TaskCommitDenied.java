package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005uc\u0001B\u0001\u0003\u0001&\u0011\u0001\u0003V1tW\u000e{W.\\5u\t\u0016t\u0017.\u001a3\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001c\u0001aE\u0003\u0001\u0015A!r\u0003\u0005\u0002\f\u001d5\tABC\u0001\u000e\u0003\u0015\u00198-\u00197b\u0013\tyAB\u0001\u0004B]f\u0014VM\u001a\t\u0003#Ii\u0011AA\u0005\u0003'\t\u0011\u0001\u0003V1tW\u001a\u000b\u0017\u000e\\3e%\u0016\f7o\u001c8\u0011\u0005-)\u0012B\u0001\f\r\u0005\u001d\u0001&o\u001c3vGR\u0004\"a\u0003\r\n\u0005ea!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000e\u0001\u0005+\u0007I\u0011\u0001\u000f\u0002\u000b)|'-\u0013#\u0016\u0003u\u0001\"a\u0003\u0010\n\u0005}a!aA%oi\"A\u0011\u0005\u0001B\tB\u0003%Q$\u0001\u0004k_\nLE\t\t\u0005\tG\u0001\u0011)\u001a!C\u00019\u0005Y\u0001/\u0019:uSRLwN\\%E\u0011!)\u0003A!E!\u0002\u0013i\u0012\u0001\u00049beRLG/[8o\u0013\u0012\u0003\u0003\u0002C\u0014\u0001\u0005+\u0007I\u0011\u0001\u000f\u0002\u001b\u0005$H/Z7qi:+XNY3s\u0011!I\u0003A!E!\u0002\u0013i\u0012AD1ui\u0016l\u0007\u000f\u001e(v[\n,'\u000f\t\u0005\u0006W\u0001!\t\u0001L\u0001\u0007y%t\u0017\u000e\u001e \u0015\t5rs\u0006\r\t\u0003#\u0001AQa\u0007\u0016A\u0002uAQa\t\u0016A\u0002uAQa\n\u0016A\u0002uAQA\r\u0001\u0005BM\nQ\u0002^8FeJ|'o\u0015;sS:<W#\u0001\u001b\u0011\u0005UBdBA\u00067\u0013\t9D\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003si\u0012aa\u0015;sS:<'BA\u001c\r\u0011\u0015a\u0004\u0001\"\u0011>\u0003a\u0019w.\u001e8u)><\u0018M\u001d3t)\u0006\u001c8NR1jYV\u0014Xm]\u000b\u0002}A\u00111bP\u0005\u0003\u00012\u0011qAQ8pY\u0016\fg\u000eC\u0004C\u0001\u0005\u0005I\u0011A\"\u0002\t\r|\u0007/\u001f\u000b\u0005[\u0011+e\tC\u0004\u001c\u0003B\u0005\t\u0019A\u000f\t\u000f\r\n\u0005\u0013!a\u0001;!9q%\u0011I\u0001\u0002\u0004i\u0002b\u0002%\u0001#\u0003%\t!S\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005Q%FA\u000fLW\u0005a\u0005CA'S\u001b\u0005q%BA(Q\u0003%)hn\u00195fG.,GM\u0003\u0002R\u0019\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005Ms%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9Q\u000bAI\u0001\n\u0003I\u0015AD2paf$C-\u001a4bk2$HE\r\u0005\b/\u0002\t\n\u0011\"\u0001J\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIMBq!\u0017\u0001\u0002\u0002\u0013\u0005#,A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u00027B\u0011A,Y\u0007\u0002;*\u0011alX\u0001\u0005Y\u0006twMC\u0001a\u0003\u0011Q\u0017M^1\n\u0005ej\u0006bB2\u0001\u0003\u0003%\t\u0001H\u0001\raJ|G-^2u\u0003JLG/\u001f\u0005\bK\u0002\t\t\u0011\"\u0001g\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"a\u001a6\u0011\u0005-A\u0017BA5\r\u0005\r\te.\u001f\u0005\bW\u0012\f\t\u00111\u0001\u001e\u0003\rAH%\r\u0005\b[\u0002\t\t\u0011\"\u0011o\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#A8\u0011\u0007A\u001cx-D\u0001r\u0015\t\u0011H\"\u0001\u0006d_2dWm\u0019;j_:L!\u0001^9\u0003\u0011%#XM]1u_JDqA\u001e\u0001\u0002\u0002\u0013\u0005q/\u0001\u0005dC:,\u0015/^1m)\tq\u0004\u0010C\u0004lk\u0006\u0005\t\u0019A4\t\u000fi\u0004\u0011\u0011!C!w\u0006A\u0001.Y:i\u0007>$W\rF\u0001\u001e\u0011\u001di\b!!A\u0005By\f\u0001\u0002^8TiJLgn\u001a\u000b\u00027\"I\u0011\u0011\u0001\u0001\u0002\u0002\u0013\u0005\u00131A\u0001\u0007KF,\u0018\r\\:\u0015\u0007y\n)\u0001C\u0004l\u007f\u0006\u0005\t\u0019A4)\u0007\u0001\tI\u0001\u0005\u0003\u0002\f\u0005=QBAA\u0007\u0015\t\t&!\u0003\u0003\u0002\u0012\u00055!\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLw!CA\u000b\u0005\u0005\u0005\t\u0012AA\f\u0003A!\u0016m]6D_6l\u0017\u000e\u001e#f]&,G\rE\u0002\u0012\u000331\u0001\"\u0001\u0002\u0002\u0002#\u0005\u00111D\n\u0006\u00033\tib\u0006\t\t\u0003?\t)#H\u000f\u001e[5\u0011\u0011\u0011\u0005\u0006\u0004\u0003Ga\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003O\t\tCA\tBEN$(/Y2u\rVt7\r^5p]NBqaKA\r\t\u0003\tY\u0003\u0006\u0002\u0002\u0018!AQ0!\u0007\u0002\u0002\u0013\u0015c\u0010\u0003\u0006\u00022\u0005e\u0011\u0011!CA\u0003g\tQ!\u00199qYf$r!LA\u001b\u0003o\tI\u0004\u0003\u0004\u001c\u0003_\u0001\r!\b\u0005\u0007G\u0005=\u0002\u0019A\u000f\t\r\u001d\ny\u00031\u0001\u001e\u0011)\ti$!\u0007\u0002\u0002\u0013\u0005\u0015qH\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\t\t%!\u0014\u0011\u000b-\t\u0019%a\u0012\n\u0007\u0005\u0015CB\u0001\u0004PaRLwN\u001c\t\u0007\u0017\u0005%S$H\u000f\n\u0007\u0005-CB\u0001\u0004UkBdWm\r\u0005\n\u0003\u001f\nY$!AA\u00025\n1\u0001\u001f\u00131\u0011)\t\u0019&!\u0007\u0002\u0002\u0013%\u0011QK\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002XA\u0019A,!\u0017\n\u0007\u0005mSL\u0001\u0004PE*,7\r\u001e"
)
public class TaskCommitDenied implements TaskFailedReason, Product, Serializable {
   private final int jobID;
   private final int partitionID;
   private final int attemptNumber;

   public static Option<Tuple3<Object, Object, Object>> unapply(TaskCommitDenied var0) {
      return TaskCommitDenied$.MODULE$.unapply(var0);
   }

   public static TaskCommitDenied apply(int var0, int var1, int var2) {
      return TaskCommitDenied$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<Object, Object, Object>, TaskCommitDenied> tupled() {
      return TaskCommitDenied$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<Object, TaskCommitDenied>>> curried() {
      return TaskCommitDenied$.MODULE$.curried();
   }

   public int jobID() {
      return this.jobID;
   }

   public int partitionID() {
      return this.partitionID;
   }

   public int attemptNumber() {
      return this.attemptNumber;
   }

   public String toErrorString() {
      return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"TaskCommitDenied (Driver denied task commit)"})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" for job: ", ", partition: ", ", attemptNumber: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.jobID()), BoxesRunTime.boxToInteger(this.partitionID()), BoxesRunTime.boxToInteger(this.attemptNumber())}))).toString();
   }

   public boolean countTowardsTaskFailures() {
      return false;
   }

   public TaskCommitDenied copy(int jobID, int partitionID, int attemptNumber) {
      return new TaskCommitDenied(jobID, partitionID, attemptNumber);
   }

   public int copy$default$1() {
      return this.jobID();
   }

   public int copy$default$2() {
      return this.partitionID();
   }

   public int copy$default$3() {
      return this.attemptNumber();
   }

   public String productPrefix() {
      return "TaskCommitDenied";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Integer var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.jobID());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.partitionID());
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.attemptNumber());
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof TaskCommitDenied;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.jobID());
      var1 = Statics.mix(var1, this.partitionID());
      var1 = Statics.mix(var1, this.attemptNumber());
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof TaskCommitDenied) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               TaskCommitDenied var4 = (TaskCommitDenied)x$1;
               if (this.jobID() == var4.jobID() && this.partitionID() == var4.partitionID() && this.attemptNumber() == var4.attemptNumber() && var4.canEqual(this)) {
                  break label52;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public TaskCommitDenied(int jobID, int partitionID, int attemptNumber) {
      this.jobID = jobID;
      this.partitionID = partitionID;
      this.attemptNumber = attemptNumber;
      TaskFailedReason$class.$init$(this);
      Product$class.$init$(this);
   }
}
