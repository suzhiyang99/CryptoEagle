package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}b!B\u0001\u0003\u0001\nQ!\u0001\u0004&pE\u000e\u000bgnY3mY\u0016$'BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\u0015\u00011\"E\u000b\u0019!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!cE\u0007\u0002\u0005%\u0011AC\u0001\u0002\u0012\t\u0006;5k\u00195fIVdWM]#wK:$\bC\u0001\u0007\u0017\u0013\t9RBA\u0004Qe>$Wo\u0019;\u0011\u00051I\u0012B\u0001\u000e\u000e\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!a\u0002A!f\u0001\n\u0003q\u0012!\u00026pE&#7\u0001A\u000b\u0002?A\u0011A\u0002I\u0005\u0003C5\u00111!\u00138u\u0011!\u0019\u0003A!E!\u0002\u0013y\u0012A\u00026pE&#\u0007\u0005\u0003\u0005&\u0001\tU\r\u0011\"\u0001'\u0003\u0019\u0011X-Y:p]V\tq\u0005E\u0002\rQ)J!!K\u0007\u0003\r=\u0003H/[8o!\tYcF\u0004\u0002\rY%\u0011Q&D\u0001\u0007!J,G-\u001a4\n\u0005=\u0002$AB*ue&twM\u0003\u0002.\u001b!A!\u0007\u0001B\tB\u0003%q%A\u0004sK\u0006\u001cxN\u001c\u0011\t\u000bQ\u0002A\u0011A\u001b\u0002\rqJg.\u001b;?)\r1t\u0007\u000f\t\u0003%\u0001AQ\u0001H\u001aA\u0002}AQ!J\u001aA\u0002\u001dBqA\u000f\u0001\u0002\u0002\u0013\u00051(\u0001\u0003d_BLHc\u0001\u001c={!9A$\u000fI\u0001\u0002\u0004y\u0002bB\u0013:!\u0003\u0005\ra\n\u0005\b\u007f\u0001\t\n\u0011\"\u0001A\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012!\u0011\u0016\u0003?\t[\u0013a\u0011\t\u0003\t&k\u0011!\u0012\u0006\u0003\r\u001e\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005!k\u0011AC1o]>$\u0018\r^5p]&\u0011!*\u0012\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u0002'\u0001#\u0003%\t!T\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005q%FA\u0014C\u0011\u001d\u0001\u0006!!A\u0005BE\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u0001*\u0011\u0005MCV\"\u0001+\u000b\u0005U3\u0016\u0001\u00027b]\u001eT\u0011aV\u0001\u0005U\u00064\u0018-\u0003\u00020)\"9!\fAA\u0001\n\u0003q\u0012\u0001\u00049s_\u0012,8\r^!sSRL\bb\u0002/\u0001\u0003\u0003%\t!X\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\tq\u0016\r\u0005\u0002\r?&\u0011\u0001-\u0004\u0002\u0004\u0003:L\bb\u00022\\\u0003\u0003\u0005\raH\u0001\u0004q\u0012\n\u0004b\u00023\u0001\u0003\u0003%\t%Z\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\ta\rE\u0002hUzk\u0011\u0001\u001b\u0006\u0003S6\t!bY8mY\u0016\u001cG/[8o\u0013\tY\u0007N\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001di\u0007!!A\u0005\u00029\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003_J\u0004\"\u0001\u00049\n\u0005El!a\u0002\"p_2,\u0017M\u001c\u0005\bE2\f\t\u00111\u0001_\u0011\u001d!\b!!A\u0005BU\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002?!9q\u000fAA\u0001\n\u0003B\u0018\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003ICqA\u001f\u0001\u0002\u0002\u0013\u000530\u0001\u0004fcV\fGn\u001d\u000b\u0003_rDqAY=\u0002\u0002\u0003\u0007al\u0002\u0005\u007f\u0005\u0005\u0005\t\u0012\u0001\u0002\u0000\u00031QuNY\"b]\u000e,G\u000e\\3e!\r\u0011\u0012\u0011\u0001\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0003\u0007\u0019R!!\u0001\u0002\u0006a\u0001r!a\u0002\u0002\u000e}9c'\u0004\u0002\u0002\n)\u0019\u00111B\u0007\u0002\u000fI,h\u000e^5nK&!\u0011qBA\u0005\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|gN\r\u0005\bi\u0005\u0005A\u0011AA\n)\u0005y\b\u0002C<\u0002\u0002\u0005\u0005IQ\t=\t\u0015\u0005e\u0011\u0011AA\u0001\n\u0003\u000bY\"A\u0003baBd\u0017\u0010F\u00037\u0003;\ty\u0002\u0003\u0004\u001d\u0003/\u0001\ra\b\u0005\u0007K\u0005]\u0001\u0019A\u0014\t\u0015\u0005\r\u0012\u0011AA\u0001\n\u0003\u000b)#A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005\u001d\u0012q\u0006\t\u0005\u0019!\nI\u0003E\u0003\r\u0003Wyr%C\u0002\u0002.5\u0011a\u0001V;qY\u0016\u0014\u0004\"CA\u0019\u0003C\t\t\u00111\u00017\u0003\rAH\u0005\r\u0005\u000b\u0003k\t\t!!A\u0005\n\u0005]\u0012a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u000f\u0011\u0007M\u000bY$C\u0002\u0002>Q\u0013aa\u00142kK\u000e$\b"
)
public class JobCancelled implements DAGSchedulerEvent, Product, Serializable {
   private final int jobId;
   private final Option<String> reason;

   public static Option<Tuple2<Object, Option<String>>> unapply(JobCancelled var0) {
      return JobCancelled$.MODULE$.unapply(var0);
   }

   public static JobCancelled apply(int var0, Option<String> var1) {
      return JobCancelled$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<Object, Option<String>>, JobCancelled> tupled() {
      return JobCancelled$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Option<String>, JobCancelled>> curried() {
      return JobCancelled$.MODULE$.curried();
   }

   public int jobId() {
      return this.jobId;
   }

   public Option<String> reason() {
      return this.reason;
   }

   public JobCancelled copy(int jobId, Option<String> reason) {
      return new JobCancelled(jobId, reason);
   }

   public int copy$default$1() {
      return this.jobId();
   }

   public Option<String> copy$default$2() {
      return this.reason();
   }

   public String productPrefix() {
      return "JobCancelled";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.jobId());
            break;
         case 1:
            var10000 = this.reason();
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
      return x$1 instanceof JobCancelled;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.jobId());
      var1 = Statics.mix(var1, Statics.anyHash(this.reason()));
      return Statics.finalizeHash(var1, 2);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label54: {
            boolean var3;
            if (x$1 instanceof JobCancelled) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  JobCancelled var4 = (JobCancelled)x$1;
                  if (this.jobId() == var4.jobId()) {
                     label35: {
                        Option var10000 = this.reason();
                        Option var5 = var4.reason();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label35;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label35;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label37;
                        }
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label54;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public JobCancelled(int jobId, Option<String> reason) {
      this.jobId = jobId;
      this.reason = reason;
      Product$class.$init$(this);
   }
}
