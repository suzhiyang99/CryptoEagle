package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mb\u0001B\u0001\u0003\u0001&\u0011!\u0002V1tW.KG\u000e\\3e\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0001A\n\u0006\u0001)\u0001Bc\u0006\t\u0003\u00179i\u0011\u0001\u0004\u0006\u0002\u001b\u0005)1oY1mC&\u0011q\u0002\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005E\u0011R\"\u0001\u0002\n\u0005M\u0011!\u0001\u0005+bg.4\u0015-\u001b7fIJ+\u0017m]8o!\tYQ#\u0003\u0002\u0017\u0019\t9\u0001K]8ek\u000e$\bCA\u0006\u0019\u0013\tIBB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001c\u0001\tU\r\u0011\"\u0001\u001d\u0003\u0019\u0011X-Y:p]V\tQ\u0004\u0005\u0002\u001fC9\u00111bH\u0005\u0003A1\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0012$\u0005\u0019\u0019FO]5oO*\u0011\u0001\u0005\u0004\u0005\tK\u0001\u0011\t\u0012)A\u0005;\u00059!/Z1t_:\u0004\u0003\"B\u0014\u0001\t\u0003A\u0013A\u0002\u001fj]&$h\b\u0006\u0002*UA\u0011\u0011\u0003\u0001\u0005\u00067\u0019\u0002\r!\b\u0005\u0006Y\u0001!\t\u0005H\u0001\u000ei>,%O]8s'R\u0014\u0018N\\4\t\u000b9\u0002A\u0011I\u0018\u00021\r|WO\u001c;U_^\f'\u000fZ:UCN\\g)Y5mkJ,7/F\u00011!\tY\u0011'\u0003\u00023\u0019\t9!i\\8mK\u0006t\u0007b\u0002\u001b\u0001\u0003\u0003%\t!N\u0001\u0005G>\u0004\u0018\u0010\u0006\u0002*m!91d\rI\u0001\u0002\u0004i\u0002b\u0002\u001d\u0001#\u0003%\t!O\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005Q$FA\u000f<W\u0005a\u0004CA\u001fC\u001b\u0005q$BA A\u0003%)hn\u00195fG.,GM\u0003\u0002B\u0019\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\rs$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9Q\tAA\u0001\n\u00032\u0015!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001H!\tAU*D\u0001J\u0015\tQ5*\u0001\u0003mC:<'\"\u0001'\u0002\t)\fg/Y\u0005\u0003E%Cqa\u0014\u0001\u0002\u0002\u0013\u0005\u0001+\u0001\u0007qe>$Wo\u0019;Be&$\u00180F\u0001R!\tY!+\u0003\u0002T\u0019\t\u0019\u0011J\u001c;\t\u000fU\u0003\u0011\u0011!C\u0001-\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HCA,[!\tY\u0001,\u0003\u0002Z\u0019\t\u0019\u0011I\\=\t\u000fm#\u0016\u0011!a\u0001#\u0006\u0019\u0001\u0010J\u0019\t\u000fu\u0003\u0011\u0011!C!=\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001`!\r\u00017mV\u0007\u0002C*\u0011!\rD\u0001\u000bG>dG.Z2uS>t\u0017B\u00013b\u0005!IE/\u001a:bi>\u0014\bb\u00024\u0001\u0003\u0003%\taZ\u0001\tG\u0006tW)];bYR\u0011\u0001\u0007\u001b\u0005\b7\u0016\f\t\u00111\u0001X\u0011\u001dQ\u0007!!A\u0005B-\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002#\"9Q\u000eAA\u0001\n\u0003r\u0017\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\u001dCq\u0001\u001d\u0001\u0002\u0002\u0013\u0005\u0013/\u0001\u0004fcV\fGn\u001d\u000b\u0003aIDqaW8\u0002\u0002\u0003\u0007q\u000b\u000b\u0002\u0001iB\u0011Qo^\u0007\u0002m*\u0011\u0011IA\u0005\u0003qZ\u0014A\u0002R3wK2|\u0007/\u001a:Ba&<qA\u001f\u0002\u0002\u0002#\u000510\u0001\u0006UCN\\7*\u001b7mK\u0012\u0004\"!\u0005?\u0007\u000f\u0005\u0011\u0011\u0011!E\u0001{N\u0019AP`\f\u0011\u000b}\f)!H\u0015\u000e\u0005\u0005\u0005!bAA\u0002\u0019\u00059!/\u001e8uS6,\u0017\u0002BA\u0004\u0003\u0003\u0011\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c82\u0011\u00199C\u0010\"\u0001\u0002\fQ\t1\u0010C\u0004ny\u0006\u0005IQ\t8\t\u0013\u0005EA0!A\u0005\u0002\u0006M\u0011!B1qa2LHcA\u0015\u0002\u0016!11$a\u0004A\u0002uA\u0011\"!\u0007}\u0003\u0003%\t)a\u0007\u0002\u000fUt\u0017\r\u001d9msR!\u0011QDA\u0012!\u0011Y\u0011qD\u000f\n\u0007\u0005\u0005BB\u0001\u0004PaRLwN\u001c\u0005\n\u0003K\t9\"!AA\u0002%\n1\u0001\u001f\u00131\u0011%\tI\u0003`A\u0001\n\u0013\tY#A\u0006sK\u0006$'+Z:pYZ,GCAA\u0017!\rA\u0015qF\u0005\u0004\u0003cI%AB(cU\u0016\u001cG\u000f"
)
public class TaskKilled implements TaskFailedReason, Product, Serializable {
   private final String reason;

   public static Option<String> unapply(TaskKilled var0) {
      return TaskKilled$.MODULE$.unapply(var0);
   }

   public static TaskKilled apply(String var0) {
      return TaskKilled$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<TaskKilled, A> var0) {
      return TaskKilled$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, TaskKilled> compose(Function1<A, String> var0) {
      return TaskKilled$.MODULE$.compose(var0);
   }

   public String reason() {
      return this.reason;
   }

   public String toErrorString() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"TaskKilled (", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.reason()}));
   }

   public boolean countTowardsTaskFailures() {
      return false;
   }

   public TaskKilled copy(String reason) {
      return new TaskKilled(reason);
   }

   public String copy$default$1() {
      return this.reason();
   }

   public String productPrefix() {
      return "TaskKilled";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.reason();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof TaskKilled;
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
            if (x$1 instanceof TaskKilled) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     TaskKilled var4 = (TaskKilled)x$1;
                     String var10000 = this.reason();
                     String var5 = var4.reason();
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

   public TaskKilled(String reason) {
      this.reason = reason;
      TaskFailedReason$class.$init$(this);
      Product$class.$init$(this);
   }
}
