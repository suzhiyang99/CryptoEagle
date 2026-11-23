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
   bytes = "\u0006\u0001\u0005%c!B\u0001\u0003\u0001\nQ!\u0001G*qK\u000e,H.\u0019;jm\u0016$\u0016m]6Tk\nl\u0017\u000e\u001e;fI*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0006\u0001-\tR\u0003\u0007\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!!\u0005#B\u000fN\u001b\u0007.\u001a3vY\u0016\u0014XI^3oiB\u0011ABF\u0005\u0003/5\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\r3%\u0011!$\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t9\u0001\u0011)\u001a!C\u0001=\u0005!A/Y:l\u0007\u0001)\u0012a\b\u0019\u0003A\u0015\u00022AE\u0011$\u0013\t\u0011#A\u0001\u0003UCN\\\u0007C\u0001\u0013&\u0019\u0001!\u0011BJ\u0014\u0002\u0002\u0003\u0005)\u0011A\u0017\u0003\t}#\u0013'\r\u0005\tQ\u0001\u0011\t\u0012)A\u0005S\u0005)A/Y:lAA\u0012!\u0006\f\t\u0004%\u0005Z\u0003C\u0001\u0013-\t%1s%!A\u0001\u0002\u000b\u0005Q&\u0005\u0002/cA\u0011AbL\u0005\u0003a5\u0011qAT8uQ&tw\r\u0005\u0002\re%\u00111'\u0004\u0002\u0004\u0003:L\b\"B\u001b\u0001\t\u00031\u0014A\u0002\u001fj]&$h\b\u0006\u00028qA\u0011!\u0003\u0001\u0005\u00069Q\u0002\r!\u000f\u0019\u0003uq\u00022AE\u0011<!\t!C\bB\u0005'q\u0005\u0005\t\u0011!B\u0001[!9a\bAA\u0001\n\u0003y\u0014\u0001B2paf$\"a\u000e!\t\u000fqi\u0004\u0013!a\u0001s!9!\tAI\u0001\n\u0003\u0019\u0015AD2paf$C-\u001a4bk2$H%M\u000b\u0002\tB\u0012Qi\u0012\t\u0004%\u00052\u0005C\u0001\u0013H\t%1\u0013)!A\u0001\u0002\u000b\u0005Q\u0006C\u0004J\u0001\u0005\u0005I\u0011\t&\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005Y\u0005C\u0001'R\u001b\u0005i%B\u0001(P\u0003\u0011a\u0017M\\4\u000b\u0003A\u000bAA[1wC&\u0011!+\u0014\u0002\u0007'R\u0014\u0018N\\4\t\u000fQ\u0003\u0011\u0011!C\u0001+\u0006a\u0001O]8ek\u000e$\u0018I]5usV\ta\u000b\u0005\u0002\r/&\u0011\u0001,\u0004\u0002\u0004\u0013:$\bb\u0002.\u0001\u0003\u0003%\taW\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\t\tD\fC\u0004^3\u0006\u0005\t\u0019\u0001,\u0002\u0007a$\u0013\u0007C\u0004`\u0001\u0005\u0005I\u0011\t1\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012!\u0019\t\u0004E\u0016\fT\"A2\u000b\u0005\u0011l\u0011AC2pY2,7\r^5p]&\u0011am\u0019\u0002\t\u0013R,'/\u0019;pe\"9\u0001\u000eAA\u0001\n\u0003I\u0017\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005)l\u0007C\u0001\u0007l\u0013\taWBA\u0004C_>dW-\u00198\t\u000fu;\u0017\u0011!a\u0001c!9q\u000eAA\u0001\n\u0003\u0002\u0018\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003YCqA\u001d\u0001\u0002\u0002\u0013\u00053/\u0001\u0005u_N#(/\u001b8h)\u0005Y\u0005bB;\u0001\u0003\u0003%\tE^\u0001\u0007KF,\u0018\r\\:\u0015\u0005)<\bbB/u\u0003\u0003\u0005\r!M\u0004\ts\n\t\t\u0011#\u0001\u0003u\u0006A2\u000b]3dk2\fG/\u001b<f)\u0006\u001c8nU;c[&$H/\u001a3\u0011\u0005IYh\u0001C\u0001\u0003\u0003\u0003E\tA\u0001?\u0014\u0007ml\b\u0004\u0005\u0004\u007f\u0003\u0007\t9aN\u0007\u0002\u007f*\u0019\u0011\u0011A\u0007\u0002\u000fI,h\u000e^5nK&\u0019\u0011QA@\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t\u0017\u0007\r\u0003\u0002\n\u00055\u0001\u0003\u0002\n\"\u0003\u0017\u00012\u0001JA\u0007\t%130!A\u0001\u0002\u000b\u0005Q\u0006\u0003\u00046w\u0012\u0005\u0011\u0011\u0003\u000b\u0002u\"9!o_A\u0001\n\u000b\u001a\b\"CA\fw\u0006\u0005I\u0011QA\r\u0003\u0015\t\u0007\u000f\u001d7z)\r9\u00141\u0004\u0005\b9\u0005U\u0001\u0019AA\u000fa\u0011\ty\"a\t\u0011\tI\t\u0013\u0011\u0005\t\u0004I\u0005\rBA\u0003\u0014\u0002\u001c\u0005\u0005\t\u0011!B\u0001[!I\u0011qE>\u0002\u0002\u0013\u0005\u0015\u0011F\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\tY#!\u000f1\t\u00055\u0012q\u0007\t\u0006\u0019\u0005=\u00121G\u0005\u0004\u0003ci!AB(qi&|g\u000e\u0005\u0003\u0013C\u0005U\u0002c\u0001\u0013\u00028\u0011Qa%!\n\u0002\u0002\u0003\u0005)\u0011A\u0017\t\u0013\u0005m\u0012QEA\u0001\u0002\u00049\u0014a\u0001=%a!I\u0011qH>\u0002\u0002\u0013%\u0011\u0011I\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002DA\u0019A*!\u0012\n\u0007\u0005\u001dSJ\u0001\u0004PE*,7\r\u001e"
)
public class SpeculativeTaskSubmitted implements DAGSchedulerEvent, Product, Serializable {
   private final Task<?> task;

   public static Option<Task<Object>> unapply(SpeculativeTaskSubmitted var0) {
      return SpeculativeTaskSubmitted$.MODULE$.unapply(var0);
   }

   public static SpeculativeTaskSubmitted apply(Task<?> var0) {
      return SpeculativeTaskSubmitted$.MODULE$.apply(var0);
   }

   public static <A> Function1<Task<?>, A> andThen(Function1<SpeculativeTaskSubmitted, A> var0) {
      return SpeculativeTaskSubmitted$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SpeculativeTaskSubmitted> compose(Function1<A, Task<?>> var0) {
      return SpeculativeTaskSubmitted$.MODULE$.compose(var0);
   }

   public Task<?> task() {
      return this.task;
   }

   public SpeculativeTaskSubmitted copy(Task<?> task) {
      return new SpeculativeTaskSubmitted(task);
   }

   public Task<?> copy$default$1() {
      return this.task();
   }

   public String productPrefix() {
      return "SpeculativeTaskSubmitted";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.task();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SpeculativeTaskSubmitted;
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
            if (x$1 instanceof SpeculativeTaskSubmitted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     SpeculativeTaskSubmitted var4 = (SpeculativeTaskSubmitted)x$1;
                     Task var10000 = this.task();
                     Task var5 = var4.task();
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

   public SpeculativeTaskSubmitted(Task<?> task) {
      this.task = task;
      Product$class.$init$(this);
   }
}
