package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055b!B\u0001\u0003\u0001\u0012Q!!\u0003&pE\u001a\u000b\u0017\u000e\\3e\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0003\u0001\u0017E)\u0002\u0004\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Mi\u0011AA\u0005\u0003)\t\u0011\u0011BS8c%\u0016\u001cX\u000f\u001c;\u0011\u000511\u0012BA\f\u000e\u0005\u001d\u0001&o\u001c3vGR\u0004\"\u0001D\r\n\u0005ii!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\u000f\u0001\u0005+\u0007I\u0011\u0001\u0010\u0002\u0013\u0015D8-\u001a9uS>t7\u0001A\u000b\u0002?A\u0011\u0001\u0005\u000b\b\u0003C\u0019r!AI\u0013\u000e\u0003\rR!\u0001J\u000f\u0002\rq\u0012xn\u001c;?\u0013\u0005q\u0011BA\u0014\u000e\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u000b\u0016\u0003\u0013\u0015C8-\u001a9uS>t'BA\u0014\u000e\u0011!a\u0003A!E!\u0002\u0013y\u0012AC3yG\u0016\u0004H/[8oA!)a\u0006\u0001C\u0001_\u00051A(\u001b8jiz\"\"\u0001M\u0019\u0011\u0005I\u0001\u0001\"\u0002\u000f.\u0001\u0004y\u0002bB\u001a\u0001\u0003\u0003%\t\u0001N\u0001\u0005G>\u0004\u0018\u0010\u0006\u00021k!9AD\rI\u0001\u0002\u0004y\u0002bB\u001c\u0001#\u0003%\t\u0001O\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005I$FA\u0010;W\u0005Y\u0004C\u0001\u001fB\u001b\u0005i$B\u0001 @\u0003%)hn\u00195fG.,GM\u0003\u0002A\u001b\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\tk$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9A\tAA\u0001\n\u0003*\u0015!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001G!\t9E*D\u0001I\u0015\tI%*\u0001\u0003mC:<'\"A&\u0002\t)\fg/Y\u0005\u0003\u001b\"\u0013aa\u0015;sS:<\u0007bB(\u0001\u0003\u0003%\t\u0001U\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002#B\u0011ABU\u0005\u0003'6\u00111!\u00138u\u0011\u001d)\u0006!!A\u0005\u0002Y\u000ba\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002X5B\u0011A\u0002W\u0005\u000336\u00111!\u00118z\u0011\u001dYF+!AA\u0002E\u000b1\u0001\u001f\u00132\u0011\u001di\u0006!!A\u0005By\u000bq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002?B\u0019\u0001mY,\u000e\u0003\u0005T!AY\u0007\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002eC\nA\u0011\n^3sCR|'\u000fC\u0004g\u0001\u0005\u0005I\u0011A4\u0002\u0011\r\fg.R9vC2$\"\u0001[6\u0011\u00051I\u0017B\u00016\u000e\u0005\u001d\u0011un\u001c7fC:DqaW3\u0002\u0002\u0003\u0007q\u000bC\u0004n\u0001\u0005\u0005I\u0011\t8\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012!\u0015\u0005\ba\u0002\t\t\u0011\"\u0011r\u0003!!xn\u0015;sS:<G#\u0001$\t\u000fM\u0004\u0011\u0011!C!i\u00061Q-];bYN$\"\u0001[;\t\u000fm\u0013\u0018\u0011!a\u0001/\u001eAqOAA\u0001\u0012\u0003!\u00010A\u0005K_\n4\u0015-\u001b7fIB\u0011!#\u001f\u0004\t\u0003\t\t\t\u0011#\u0001\u0005uN\u0019\u0011p\u001f\r\u0011\tq|x\u0004M\u0007\u0002{*\u0011a0D\u0001\beVtG/[7f\u0013\r\t\t! \u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\f\u0004B\u0002\u0018z\t\u0003\t)\u0001F\u0001y\u0011\u001d\u0001\u00180!A\u0005FED\u0011\"a\u0003z\u0003\u0003%\t)!\u0004\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0007A\ny\u0001\u0003\u0004\u001d\u0003\u0013\u0001\ra\b\u0005\n\u0003'I\u0018\u0011!CA\u0003+\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002\u0018\u0005u\u0001\u0003\u0002\u0007\u0002\u001a}I1!a\u0007\u000e\u0005\u0019y\u0005\u000f^5p]\"I\u0011qDA\t\u0003\u0003\u0005\r\u0001M\u0001\u0004q\u0012\u0002\u0004\"CA\u0012s\u0006\u0005I\u0011BA\u0013\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005\u001d\u0002cA$\u0002*%\u0019\u00111\u0006%\u0003\r=\u0013'.Z2u\u0001"
)
public class JobFailed implements JobResult, Product, Serializable {
   private final Exception exception;

   public static Option<Exception> unapply(JobFailed var0) {
      return JobFailed$.MODULE$.unapply(var0);
   }

   public static JobFailed apply(Exception var0) {
      return JobFailed$.MODULE$.apply(var0);
   }

   public static <A> Function1<Exception, A> andThen(Function1<JobFailed, A> var0) {
      return JobFailed$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, JobFailed> compose(Function1<A, Exception> var0) {
      return JobFailed$.MODULE$.compose(var0);
   }

   public Exception exception() {
      return this.exception;
   }

   public JobFailed copy(Exception exception) {
      return new JobFailed(exception);
   }

   public Exception copy$default$1() {
      return this.exception();
   }

   public String productPrefix() {
      return "JobFailed";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.exception();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof JobFailed;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof JobFailed) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     JobFailed var4 = (JobFailed)x$1;
                     Exception var10000 = this.exception();
                     Exception var5 = var4.exception();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label34;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label34;
                     }

                     if (var4.canEqual(this)) {
                        var7 = true;
                        break label35;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label52;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public JobFailed(Exception exception) {
      this.exception = exception;
      Product$class.$init$(this);
   }
}
