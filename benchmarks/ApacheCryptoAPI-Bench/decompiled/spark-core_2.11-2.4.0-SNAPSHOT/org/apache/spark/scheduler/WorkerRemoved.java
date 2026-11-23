package org.apache.spark.scheduler;

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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-c!B\u0001\u0003\u0001\nQ!!D,pe.,'OU3n_Z,GM\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON)\u0001aC\t\u00161A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003#\u0011\u000buiU2iK\u0012,H.\u001a:Fm\u0016tG\u000f\u0005\u0002\r-%\u0011q#\u0004\u0002\b!J|G-^2u!\ta\u0011$\u0003\u0002\u001b\u001b\ta1+\u001a:jC2L'0\u00192mK\"AA\u0004\u0001BK\u0002\u0013\u0005a$\u0001\u0005x_J\\WM]%e\u0007\u0001)\u0012a\b\t\u0003A\rr!\u0001D\u0011\n\u0005\tj\u0011A\u0002)sK\u0012,g-\u0003\u0002%K\t11\u000b\u001e:j]\u001eT!AI\u0007\t\u0011\u001d\u0002!\u0011#Q\u0001\n}\t\u0011b^8sW\u0016\u0014\u0018\n\u001a\u0011\t\u0011%\u0002!Q3A\u0005\u0002y\tA\u0001[8ti\"A1\u0006\u0001B\tB\u0003%q$A\u0003i_N$\b\u0005\u0003\u0005.\u0001\tU\r\u0011\"\u0001\u001f\u0003\u001diWm]:bO\u0016D\u0001b\f\u0001\u0003\u0012\u0003\u0006IaH\u0001\t[\u0016\u001c8/Y4fA!)\u0011\u0007\u0001C\u0001e\u00051A(\u001b8jiz\"Ba\r\u001b6mA\u0011!\u0003\u0001\u0005\u00069A\u0002\ra\b\u0005\u0006SA\u0002\ra\b\u0005\u0006[A\u0002\ra\b\u0005\bq\u0001\t\t\u0011\"\u0001:\u0003\u0011\u0019w\u000e]=\u0015\tMR4\b\u0010\u0005\b9]\u0002\n\u00111\u0001 \u0011\u001dIs\u0007%AA\u0002}Aq!L\u001c\u0011\u0002\u0003\u0007q\u0004C\u0004?\u0001E\u0005I\u0011A \u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t\u0001I\u000b\u0002 \u0003.\n!\t\u0005\u0002D\u00116\tAI\u0003\u0002F\r\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003\u000f6\t!\"\u00198o_R\fG/[8o\u0013\tIEIA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dqa\u0013\u0001\u0012\u0002\u0013\u0005q(\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\t\u000f5\u0003\u0011\u0013!C\u0001\u007f\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\u001a\u0004bB(\u0001\u0003\u0003%\t\u0005U\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003E\u0003\"AU,\u000e\u0003MS!\u0001V+\u0002\t1\fgn\u001a\u0006\u0002-\u0006!!.\u0019<b\u0013\t!3\u000bC\u0004Z\u0001\u0005\u0005I\u0011\u0001.\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003m\u0003\"\u0001\u0004/\n\u0005uk!aA%oi\"9q\fAA\u0001\n\u0003\u0001\u0017A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003C\u0012\u0004\"\u0001\u00042\n\u0005\rl!aA!os\"9QMXA\u0001\u0002\u0004Y\u0016a\u0001=%c!9q\rAA\u0001\n\u0003B\u0017a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003%\u00042A[7b\u001b\u0005Y'B\u00017\u000e\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003].\u0014\u0001\"\u0013;fe\u0006$xN\u001d\u0005\ba\u0002\t\t\u0011\"\u0001r\u0003!\u0019\u0017M\\#rk\u0006dGC\u0001:v!\ta1/\u0003\u0002u\u001b\t9!i\\8mK\u0006t\u0007bB3p\u0003\u0003\u0005\r!\u0019\u0005\bo\u0002\t\t\u0011\"\u0011y\u0003!A\u0017m\u001d5D_\u0012,G#A.\t\u000fi\u0004\u0011\u0011!C!w\u0006AAo\\*ue&tw\rF\u0001R\u0011\u001di\b!!A\u0005By\fa!Z9vC2\u001cHC\u0001:\u0000\u0011\u001d)G0!AA\u0002\u0005<!\"a\u0001\u0003\u0003\u0003E\tAAA\u0003\u000359vN]6feJ+Wn\u001c<fIB\u0019!#a\u0002\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\u0005\u0005%1#BA\u0004\u0003\u0017A\u0002\u0003CA\u0007\u0003'yrdH\u001a\u000e\u0005\u0005=!bAA\t\u001b\u00059!/\u001e8uS6,\u0017\u0002BA\u000b\u0003\u001f\u0011\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c84\u0011\u001d\t\u0014q\u0001C\u0001\u00033!\"!!\u0002\t\u0011i\f9!!A\u0005FmD!\"a\b\u0002\b\u0005\u0005I\u0011QA\u0011\u0003\u0015\t\u0007\u000f\u001d7z)\u001d\u0019\u00141EA\u0013\u0003OAa\u0001HA\u000f\u0001\u0004y\u0002BB\u0015\u0002\u001e\u0001\u0007q\u0004\u0003\u0004.\u0003;\u0001\ra\b\u0005\u000b\u0003W\t9!!A\u0005\u0002\u00065\u0012aB;oCB\u0004H.\u001f\u000b\u0005\u0003_\tY\u0004E\u0003\r\u0003c\t)$C\u0002\u000245\u0011aa\u00149uS>t\u0007C\u0002\u0007\u00028}yr$C\u0002\u0002:5\u0011a\u0001V;qY\u0016\u001c\u0004\"CA\u001f\u0003S\t\t\u00111\u00014\u0003\rAH\u0005\r\u0005\u000b\u0003\u0003\n9!!A\u0005\n\u0005\r\u0013a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u0012\u0011\u0007I\u000b9%C\u0002\u0002JM\u0013aa\u00142kK\u000e$\b"
)
public class WorkerRemoved implements DAGSchedulerEvent, Product, Serializable {
   private final String workerId;
   private final String host;
   private final String message;

   public static Option<Tuple3<String, String, String>> unapply(WorkerRemoved var0) {
      return WorkerRemoved$.MODULE$.unapply(var0);
   }

   public static WorkerRemoved apply(String var0, String var1, String var2) {
      return WorkerRemoved$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<String, String, String>, WorkerRemoved> tupled() {
      return WorkerRemoved$.MODULE$.tupled();
   }

   public static Function1<String, Function1<String, Function1<String, WorkerRemoved>>> curried() {
      return WorkerRemoved$.MODULE$.curried();
   }

   public String workerId() {
      return this.workerId;
   }

   public String host() {
      return this.host;
   }

   public String message() {
      return this.message;
   }

   public WorkerRemoved copy(String workerId, String host, String message) {
      return new WorkerRemoved(workerId, host, message);
   }

   public String copy$default$1() {
      return this.workerId();
   }

   public String copy$default$2() {
      return this.host();
   }

   public String copy$default$3() {
      return this.message();
   }

   public String productPrefix() {
      return "WorkerRemoved";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      String var10000;
      switch (x$1) {
         case 0:
            var10000 = this.workerId();
            break;
         case 1:
            var10000 = this.host();
            break;
         case 2:
            var10000 = this.message();
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
      return x$1 instanceof WorkerRemoved;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label71: {
            boolean var3;
            if (x$1 instanceof WorkerRemoved) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label53: {
                  label62: {
                     WorkerRemoved var4 = (WorkerRemoved)x$1;
                     String var10000 = this.workerId();
                     String var5 = var4.workerId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label62;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label62;
                     }

                     var10000 = this.host();
                     String var6 = var4.host();
                     if (var10000 == null) {
                        if (var6 != null) {
                           break label62;
                        }
                     } else if (!var10000.equals(var6)) {
                        break label62;
                     }

                     var10000 = this.message();
                     String var7 = var4.message();
                     if (var10000 == null) {
                        if (var7 != null) {
                           break label62;
                        }
                     } else if (!var10000.equals(var7)) {
                        break label62;
                     }

                     if (var4.canEqual(this)) {
                        var11 = true;
                        break label53;
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label71;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public WorkerRemoved(String workerId, String host, String message) {
      this.workerId = workerId;
      this.host = host;
      this.message = message;
      Product$class.$init$(this);
   }
}
