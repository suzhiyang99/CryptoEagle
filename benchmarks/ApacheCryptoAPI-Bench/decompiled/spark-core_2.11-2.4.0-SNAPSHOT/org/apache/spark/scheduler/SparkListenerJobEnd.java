package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005d\u0001B\u0001\u0003\u0001.\u00111c\u00159be.d\u0015n\u001d;f]\u0016\u0014(j\u001c2F]\u0012T!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M)\u0001\u0001\u0004\n\u00173A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"a\u0005\u000b\u000e\u0003\tI!!\u0006\u0002\u0003%M\u0003\u0018M]6MSN$XM\\3s\u000bZ,g\u000e\u001e\t\u0003\u001b]I!\u0001\u0007\b\u0003\u000fA\u0013x\u000eZ;diB\u0011QBG\u0005\u000379\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\"\b\u0001\u0003\u0016\u0004%\tAH\u0001\u0006U>\u0014\u0017\nZ\u000b\u0002?A\u0011Q\u0002I\u0005\u0003C9\u00111!\u00138u\u0011!\u0019\u0003A!E!\u0002\u0013y\u0012A\u00026pE&#\u0007\u0005\u0003\u0005&\u0001\tU\r\u0011\"\u0001'\u0003\u0011!\u0018.\\3\u0016\u0003\u001d\u0002\"!\u0004\u0015\n\u0005%r!\u0001\u0002'p]\u001eD\u0001b\u000b\u0001\u0003\u0012\u0003\u0006IaJ\u0001\u0006i&lW\r\t\u0005\t[\u0001\u0011)\u001a!C\u0001]\u0005I!n\u001c2SKN,H\u000e^\u000b\u0002_A\u00111\u0003M\u0005\u0003c\t\u0011\u0011BS8c%\u0016\u001cX\u000f\u001c;\t\u0011M\u0002!\u0011#Q\u0001\n=\n!B[8c%\u0016\u001cX\u000f\u001c;!\u0011\u0015)\u0004\u0001\"\u00017\u0003\u0019a\u0014N\\5u}Q!q\u0007O\u001d;!\t\u0019\u0002\u0001C\u0003\u001ei\u0001\u0007q\u0004C\u0003&i\u0001\u0007q\u0005C\u0003.i\u0001\u0007q\u0006C\u0004=\u0001\u0005\u0005I\u0011A\u001f\u0002\t\r|\u0007/\u001f\u000b\u0005oyz\u0004\tC\u0004\u001ewA\u0005\t\u0019A\u0010\t\u000f\u0015Z\u0004\u0013!a\u0001O!9Qf\u000fI\u0001\u0002\u0004y\u0003b\u0002\"\u0001#\u0003%\taQ\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005!%FA\u0010FW\u00051\u0005CA$M\u001b\u0005A%BA%K\u0003%)hn\u00195fG.,GM\u0003\u0002L\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u00055C%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9q\nAI\u0001\n\u0003\u0001\u0016AD2paf$C-\u001a4bk2$HEM\u000b\u0002#*\u0012q%\u0012\u0005\b'\u0002\t\n\u0011\"\u0001U\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\u0012!\u0016\u0016\u0003_\u0015Cqa\u0016\u0001\u0002\u0002\u0013\u0005\u0003,A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u00023B\u0011!lX\u0007\u00027*\u0011A,X\u0001\u0005Y\u0006twMC\u0001_\u0003\u0011Q\u0017M^1\n\u0005\u0001\\&AB*ue&tw\rC\u0004c\u0001\u0005\u0005I\u0011\u0001\u0010\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\t\u000f\u0011\u0004\u0011\u0011!C\u0001K\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HC\u00014j!\tiq-\u0003\u0002i\u001d\t\u0019\u0011I\\=\t\u000f)\u001c\u0017\u0011!a\u0001?\u0005\u0019\u0001\u0010J\u0019\t\u000f1\u0004\u0011\u0011!C![\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001o!\ry'OZ\u0007\u0002a*\u0011\u0011OD\u0001\u000bG>dG.Z2uS>t\u0017BA:q\u0005!IE/\u001a:bi>\u0014\bbB;\u0001\u0003\u0003%\tA^\u0001\tG\u0006tW)];bYR\u0011qO\u001f\t\u0003\u001baL!!\u001f\b\u0003\u000f\t{w\u000e\\3b]\"9!\u000e^A\u0001\u0002\u00041\u0007b\u0002?\u0001\u0003\u0003%\t%`\u0001\tQ\u0006\u001c\bnQ8eKR\tq\u0004\u0003\u0005\u0000\u0001\u0005\u0005I\u0011IA\u0001\u0003!!xn\u0015;sS:<G#A-\t\u0013\u0005\u0015\u0001!!A\u0005B\u0005\u001d\u0011AB3rk\u0006d7\u000fF\u0002x\u0003\u0013A\u0001B[A\u0002\u0003\u0003\u0005\rA\u001a\u0015\u0004\u0001\u00055\u0001\u0003BA\b\u0003'i!!!\u0005\u000b\u0005-#\u0011\u0002BA\u000b\u0003#\u0011A\u0002R3wK2|\u0007/\u001a:Ba&<\u0011\"!\u0007\u0003\u0003\u0003E\t!a\u0007\u0002'M\u0003\u0018M]6MSN$XM\\3s\u0015>\u0014WI\u001c3\u0011\u0007M\tiB\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012AA\u0010'\u0015\ti\"!\t\u001a!!\t\u0019#!\u000b O=:TBAA\u0013\u0015\r\t9CD\u0001\beVtG/[7f\u0013\u0011\tY#!\n\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t7\u0007C\u00046\u0003;!\t!a\f\u0015\u0005\u0005m\u0001\"C@\u0002\u001e\u0005\u0005IQIA\u0001\u0011)\t)$!\b\u0002\u0002\u0013\u0005\u0015qG\u0001\u0006CB\u0004H.\u001f\u000b\bo\u0005e\u00121HA\u001f\u0011\u0019i\u00121\u0007a\u0001?!1Q%a\rA\u0002\u001dBa!LA\u001a\u0001\u0004y\u0003BCA!\u0003;\t\t\u0011\"!\u0002D\u00059QO\\1qa2LH\u0003BA#\u0003#\u0002R!DA$\u0003\u0017J1!!\u0013\u000f\u0005\u0019y\u0005\u000f^5p]B1Q\"!\u0014 O=J1!a\u0014\u000f\u0005\u0019!V\u000f\u001d7fg!I\u00111KA \u0003\u0003\u0005\raN\u0001\u0004q\u0012\u0002\u0004BCA,\u0003;\t\t\u0011\"\u0003\u0002Z\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\tY\u0006E\u0002[\u0003;J1!a\u0018\\\u0005\u0019y%M[3di\u0002"
)
public class SparkListenerJobEnd implements SparkListenerEvent, Product, Serializable {
   private final int jobId;
   private final long time;
   private final JobResult jobResult;

   public static Option<Tuple3<Object, Object, JobResult>> unapply(SparkListenerJobEnd var0) {
      return SparkListenerJobEnd$.MODULE$.unapply(var0);
   }

   public static SparkListenerJobEnd apply(int var0, long var1, JobResult var3) {
      return SparkListenerJobEnd$.MODULE$.apply(var0, var1, var3);
   }

   public static Function1<Tuple3<Object, Object, JobResult>, SparkListenerJobEnd> tupled() {
      return SparkListenerJobEnd$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<JobResult, SparkListenerJobEnd>>> curried() {
      return SparkListenerJobEnd$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public int jobId() {
      return this.jobId;
   }

   public long time() {
      return this.time;
   }

   public JobResult jobResult() {
      return this.jobResult;
   }

   public SparkListenerJobEnd copy(int jobId, long time, JobResult jobResult) {
      return new SparkListenerJobEnd(jobId, time, jobResult);
   }

   public int copy$default$1() {
      return this.jobId();
   }

   public long copy$default$2() {
      return this.time();
   }

   public JobResult copy$default$3() {
      return this.jobResult();
   }

   public String productPrefix() {
      return "SparkListenerJobEnd";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.jobId());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.time());
            break;
         case 2:
            var10000 = this.jobResult();
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
      return x$1 instanceof SparkListenerJobEnd;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.jobId());
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.jobResult()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label56: {
            boolean var3;
            if (x$1 instanceof SparkListenerJobEnd) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  SparkListenerJobEnd var4 = (SparkListenerJobEnd)x$1;
                  if (this.jobId() == var4.jobId() && this.time() == var4.time()) {
                     label36: {
                        JobResult var10000 = this.jobResult();
                        JobResult var5 = var4.jobResult();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label36;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label36;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label39;
                        }
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label56;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public SparkListenerJobEnd(int jobId, long time, JobResult jobResult) {
      this.jobId = jobId;
      this.time = time;
      this.jobResult = jobResult;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
