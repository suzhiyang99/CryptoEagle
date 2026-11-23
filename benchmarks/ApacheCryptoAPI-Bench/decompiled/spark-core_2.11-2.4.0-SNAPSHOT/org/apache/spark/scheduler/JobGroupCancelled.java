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
   bytes = "\u0006\u0001\u0005\u0005b!B\u0001\u0003\u0001\nQ!!\u0005&pE\u001e\u0013x.\u001e9DC:\u001cW\r\u001c7fI*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0006\u0001-\tR\u0003\u0007\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!!\u0005#B\u000fN\u001b\u0007.\u001a3vY\u0016\u0014XI^3oiB\u0011ABF\u0005\u0003/5\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\r3%\u0011!$\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t9\u0001\u0011)\u001a!C\u0001=\u00059qM]8va&#7\u0001A\u000b\u0002?A\u0011\u0001e\t\b\u0003\u0019\u0005J!AI\u0007\u0002\rA\u0013X\rZ3g\u0013\t!SE\u0001\u0004TiJLgn\u001a\u0006\u0003E5A\u0001b\n\u0001\u0003\u0012\u0003\u0006IaH\u0001\tOJ|W\u000f]%eA!)\u0011\u0006\u0001C\u0001U\u00051A(\u001b8jiz\"\"a\u000b\u0017\u0011\u0005I\u0001\u0001\"\u0002\u000f)\u0001\u0004y\u0002b\u0002\u0018\u0001\u0003\u0003%\taL\u0001\u0005G>\u0004\u0018\u0010\u0006\u0002,a!9A$\fI\u0001\u0002\u0004y\u0002b\u0002\u001a\u0001#\u0003%\taM\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005!$FA\u00106W\u00051\u0004CA\u001c=\u001b\u0005A$BA\u001d;\u0003%)hn\u00195fG.,GM\u0003\u0002<\u001b\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005uB$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9q\bAA\u0001\n\u0003\u0002\u0015!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001B!\t\u0011u)D\u0001D\u0015\t!U)\u0001\u0003mC:<'\"\u0001$\u0002\t)\fg/Y\u0005\u0003I\rCq!\u0013\u0001\u0002\u0002\u0013\u0005!*\u0001\u0007qe>$Wo\u0019;Be&$\u00180F\u0001L!\taA*\u0003\u0002N\u001b\t\u0019\u0011J\u001c;\t\u000f=\u0003\u0011\u0011!C\u0001!\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HCA)U!\ta!+\u0003\u0002T\u001b\t\u0019\u0011I\\=\t\u000fUs\u0015\u0011!a\u0001\u0017\u0006\u0019\u0001\u0010J\u0019\t\u000f]\u0003\u0011\u0011!C!1\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001Z!\rQV,U\u0007\u00027*\u0011A,D\u0001\u000bG>dG.Z2uS>t\u0017B\u00010\\\u0005!IE/\u001a:bi>\u0014\bb\u00021\u0001\u0003\u0003%\t!Y\u0001\tG\u0006tW)];bYR\u0011!-\u001a\t\u0003\u0019\rL!\u0001Z\u0007\u0003\u000f\t{w\u000e\\3b]\"9QkXA\u0001\u0002\u0004\t\u0006bB4\u0001\u0003\u0003%\t\u0005[\u0001\tQ\u0006\u001c\bnQ8eKR\t1\nC\u0004k\u0001\u0005\u0005I\u0011I6\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012!\u0011\u0005\b[\u0002\t\t\u0011\"\u0011o\u0003\u0019)\u0017/^1mgR\u0011!m\u001c\u0005\b+2\f\t\u00111\u0001R\u000f!\t(!!A\t\u0002\t\u0011\u0018!\u0005&pE\u001e\u0013x.\u001e9DC:\u001cW\r\u001c7fIB\u0011!c\u001d\u0004\t\u0003\t\t\t\u0011#\u0001\u0003iN\u00191/\u001e\r\u0011\tYLxdK\u0007\u0002o*\u0011\u00010D\u0001\beVtG/[7f\u0013\tQxOA\tBEN$(/Y2u\rVt7\r^5p]FBQ!K:\u0005\u0002q$\u0012A\u001d\u0005\bUN\f\t\u0011\"\u0012l\u0011!y8/!A\u0005\u0002\u0006\u0005\u0011!B1qa2LHcA\u0016\u0002\u0004!)AD a\u0001?!I\u0011qA:\u0002\u0002\u0013\u0005\u0015\u0011B\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\tY!!\u0005\u0011\t1\tiaH\u0005\u0004\u0003\u001fi!AB(qi&|g\u000eC\u0005\u0002\u0014\u0005\u0015\u0011\u0011!a\u0001W\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u0005]1/!A\u0005\n\u0005e\u0011a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\u0007\u0011\u0007\t\u000bi\"C\u0002\u0002 \r\u0013aa\u00142kK\u000e$\b"
)
public class JobGroupCancelled implements DAGSchedulerEvent, Product, Serializable {
   private final String groupId;

   public static Option<String> unapply(JobGroupCancelled var0) {
      return JobGroupCancelled$.MODULE$.unapply(var0);
   }

   public static JobGroupCancelled apply(String var0) {
      return JobGroupCancelled$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<JobGroupCancelled, A> var0) {
      return JobGroupCancelled$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, JobGroupCancelled> compose(Function1<A, String> var0) {
      return JobGroupCancelled$.MODULE$.compose(var0);
   }

   public String groupId() {
      return this.groupId;
   }

   public JobGroupCancelled copy(String groupId) {
      return new JobGroupCancelled(groupId);
   }

   public String copy$default$1() {
      return this.groupId();
   }

   public String productPrefix() {
      return "JobGroupCancelled";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.groupId();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof JobGroupCancelled;
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
            if (x$1 instanceof JobGroupCancelled) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     JobGroupCancelled var4 = (JobGroupCancelled)x$1;
                     String var10000 = this.groupId();
                     String var5 = var4.groupId();
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

   public JobGroupCancelled(String groupId) {
      this.groupId = groupId;
      Product$class.$init$(this);
   }
}
