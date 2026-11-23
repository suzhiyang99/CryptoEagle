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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}d!B\u0001\u0003\u0001\nQ!A\u0003\"fO&tWI^3oi*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0006\u0001-\tR\u0003\u0007\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!!\u0005#B\u000fN\u001b\u0007.\u001a3vY\u0016\u0014XI^3oiB\u0011ABF\u0005\u0003/5\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\r3%\u0011!$\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t9\u0001\u0011)\u001a!C\u0001=\u0005!A/Y:l\u0007\u0001)\u0012a\b\u0019\u0003A\u0015\u00022AE\u0011$\u0013\t\u0011#A\u0001\u0003UCN\\\u0007C\u0001\u0013&\u0019\u0001!\u0011BJ\u0014\u0002\u0002\u0003\u0005)\u0011A\u0017\u0003\u0007}#s\u0007\u0003\u0005)\u0001\tE\t\u0015!\u0003*\u0003\u0015!\u0018m]6!a\tQC\u0006E\u0002\u0013C-\u0002\"\u0001\n\u0017\u0005\u0013\u0019:\u0013\u0011!A\u0001\u0006\u0003i\u0013C\u0001\u00182!\taq&\u0003\u00021\u001b\t9aj\u001c;iS:<\u0007C\u0001\u00073\u0013\t\u0019TBA\u0002B]fD\u0001\"\u000e\u0001\u0003\u0016\u0004%\tAN\u0001\ti\u0006\u001c8.\u00138g_V\tq\u0007\u0005\u0002\u0013q%\u0011\u0011H\u0001\u0002\t)\u0006\u001c8.\u00138g_\"A1\b\u0001B\tB\u0003%q'A\u0005uCN\\\u0017J\u001c4pA!)Q\b\u0001C\u0001}\u00051A(\u001b8jiz\"2a\u0010!F!\t\u0011\u0002\u0001C\u0003\u001dy\u0001\u0007\u0011\t\r\u0002C\tB\u0019!#I\"\u0011\u0005\u0011\"E!\u0003\u0014A\u0003\u0003\u0005\tQ!\u0001.\u0011\u0015)D\b1\u00018\u0011\u001d9\u0005!!A\u0005\u0002!\u000bAaY8qsR\u0019q(\u0013&\t\u000fq1\u0005\u0013!a\u0001\u0003\"9QG\u0012I\u0001\u0002\u00049\u0004b\u0002'\u0001#\u0003%\t!T\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005q\u0005GA(R!\r\u0011\u0012\u0005\u0015\t\u0003IE#\u0011BJ&\u0002\u0002\u0003\u0005)\u0011A\u0017\t\u000fM\u0003\u0011\u0013!C\u0001)\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012T#A++\u0005]26&A,\u0011\u0005akV\"A-\u000b\u0005i[\u0016!C;oG\",7m[3e\u0015\taV\"\u0001\u0006b]:|G/\u0019;j_:L!AX-\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004a\u0001\u0005\u0005I\u0011I1\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005\u0011\u0007CA2i\u001b\u0005!'BA3g\u0003\u0011a\u0017M\\4\u000b\u0003\u001d\fAA[1wC&\u0011\u0011\u000e\u001a\u0002\u0007'R\u0014\u0018N\\4\t\u000f-\u0004\u0011\u0011!C\u0001Y\u0006a\u0001O]8ek\u000e$\u0018I]5usV\tQ\u000e\u0005\u0002\r]&\u0011q.\u0004\u0002\u0004\u0013:$\bbB9\u0001\u0003\u0003%\tA]\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\t\t4\u000fC\u0004ua\u0006\u0005\t\u0019A7\u0002\u0007a$\u0013\u0007C\u0004w\u0001\u0005\u0005I\u0011I<\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012\u0001\u001f\t\u0004sr\fT\"\u0001>\u000b\u0005ml\u0011AC2pY2,7\r^5p]&\u0011QP\u001f\u0002\t\u0013R,'/\u0019;pe\"Aq\u0010AA\u0001\n\u0003\t\t!\u0001\u0005dC:,\u0015/^1m)\u0011\t\u0019!!\u0003\u0011\u00071\t)!C\u0002\u0002\b5\u0011qAQ8pY\u0016\fg\u000eC\u0004u}\u0006\u0005\t\u0019A\u0019\t\u0013\u00055\u0001!!A\u0005B\u0005=\u0011\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u00035D\u0011\"a\u0005\u0001\u0003\u0003%\t%!\u0006\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012A\u0019\u0005\n\u00033\u0001\u0011\u0011!C!\u00037\ta!Z9vC2\u001cH\u0003BA\u0002\u0003;A\u0001\u0002^A\f\u0003\u0003\u0005\r!M\u0004\u000b\u0003C\u0011\u0011\u0011!E\u0001\u0005\u0005\r\u0012A\u0003\"fO&tWI^3oiB\u0019!#!\n\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\u0005\u0005\u001d2#BA\u0013\u0003SA\u0002\u0003CA\u0016\u0003c\t)dN \u000e\u0005\u00055\"bAA\u0018\u001b\u00059!/\u001e8uS6,\u0017\u0002BA\u001a\u0003[\u0011\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c83a\u0011\t9$a\u000f\u0011\tI\t\u0013\u0011\b\t\u0004I\u0005mBA\u0003\u0014\u0002&\u0005\u0005\t\u0011!B\u0001[!9Q(!\n\u0005\u0002\u0005}BCAA\u0012\u0011)\t\u0019\"!\n\u0002\u0002\u0013\u0015\u0013Q\u0003\u0005\u000b\u0003\u000b\n)#!A\u0005\u0002\u0006\u001d\u0013!B1qa2LH#B \u0002J\u0005M\u0003b\u0002\u000f\u0002D\u0001\u0007\u00111\n\u0019\u0005\u0003\u001b\n\t\u0006\u0005\u0003\u0013C\u0005=\u0003c\u0001\u0013\u0002R\u0011Qa%!\u0013\u0002\u0002\u0003\u0005)\u0011A\u0017\t\rU\n\u0019\u00051\u00018\u0011)\t9&!\n\u0002\u0002\u0013\u0005\u0015\u0011L\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\tY&a\u001c1\t\u0005u\u0013Q\u000e\t\u0006\u0019\u0005}\u00131M\u0005\u0004\u0003Cj!AB(qi&|g\u000e\u0005\u0004\r\u0003K\nIgN\u0005\u0004\u0003Oj!A\u0002+va2,'\u0007\u0005\u0003\u0013C\u0005-\u0004c\u0001\u0013\u0002n\u0011Qa%!\u0016\u0002\u0002\u0003\u0005)\u0011A\u0017\t\u0013\u0005E\u0014QKA\u0001\u0002\u0004y\u0014a\u0001=%a!Q\u0011QOA\u0013\u0003\u0003%I!a\u001e\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003s\u00022aYA>\u0013\r\ti\b\u001a\u0002\u0007\u001f\nTWm\u0019;"
)
public class BeginEvent implements DAGSchedulerEvent, Product, Serializable {
   private final Task<?> task;
   private final TaskInfo taskInfo;

   public static Option<Tuple2<Task<Object>, TaskInfo>> unapply(BeginEvent var0) {
      return BeginEvent$.MODULE$.unapply(var0);
   }

   public static BeginEvent apply(Task<?> var0, TaskInfo var1) {
      return BeginEvent$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<Task<?>, TaskInfo>, BeginEvent> tupled() {
      return BeginEvent$.MODULE$.tupled();
   }

   public static Function1<Task<?>, Function1<TaskInfo, BeginEvent>> curried() {
      return BeginEvent$.MODULE$.curried();
   }

   public Task<?> task() {
      return this.task;
   }

   public TaskInfo taskInfo() {
      return this.taskInfo;
   }

   public BeginEvent copy(Task<?> task, TaskInfo taskInfo) {
      return new BeginEvent(task, taskInfo);
   }

   public Task<?> copy$default$1() {
      return this.task();
   }

   public TaskInfo copy$default$2() {
      return this.taskInfo();
   }

   public String productPrefix() {
      return "BeginEvent";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.task();
            break;
         case 1:
            var10000 = this.taskInfo();
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
      return x$1 instanceof BeginEvent;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label62: {
            boolean var3;
            if (x$1 instanceof BeginEvent) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     BeginEvent var4 = (BeginEvent)x$1;
                     Task var10000 = this.task();
                     Task var5 = var4.task();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     TaskInfo var7 = this.taskInfo();
                     TaskInfo var6 = var4.taskInfo();
                     if (var7 == null) {
                        if (var6 != null) {
                           break label53;
                        }
                     } else if (!var7.equals(var6)) {
                        break label53;
                     }

                     if (var4.canEqual(this)) {
                        var9 = true;
                        break label44;
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label62;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public BeginEvent(Task<?> task, TaskInfo taskInfo) {
      this.task = task;
      this.taskInfo = taskInfo;
      Product$class.$init$(this);
   }
}
