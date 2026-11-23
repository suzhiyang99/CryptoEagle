package org.apache.spark.scheduler.local;

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

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mc\u0001B\u0001\u0003\t6\u0011\u0001bS5mYR\u000b7o\u001b\u0006\u0003\u0007\u0011\tQ\u0001\\8dC2T!!\u0002\u0004\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001M!\u0001A\u0004\u000b\u0018!\ty!#D\u0001\u0011\u0015\u0005\t\u0012!B:dC2\f\u0017BA\n\u0011\u0005\u0019\te.\u001f*fMB\u0011q\"F\u0005\u0003-A\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u00101%\u0011\u0011\u0004\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t7\u0001\u0011)\u001a!C\u00019\u00051A/Y:l\u0013\u0012,\u0012!\b\t\u0003\u001fyI!a\b\t\u0003\t1{gn\u001a\u0005\tC\u0001\u0011\t\u0012)A\u0005;\u00059A/Y:l\u0013\u0012\u0004\u0003\u0002C\u0012\u0001\u0005+\u0007I\u0011\u0001\u0013\u0002\u001f%tG/\u001a:skB$H\u000b\u001b:fC\u0012,\u0012!\n\t\u0003\u001f\u0019J!a\n\t\u0003\u000f\t{w\u000e\\3b]\"A\u0011\u0006\u0001B\tB\u0003%Q%\u0001\tj]R,'O];qiRC'/Z1eA!A1\u0006\u0001BK\u0002\u0013\u0005A&\u0001\u0004sK\u0006\u001cxN\\\u000b\u0002[A\u0011a&\r\b\u0003\u001f=J!\u0001\r\t\u0002\rA\u0013X\rZ3g\u0013\t\u00114G\u0001\u0004TiJLgn\u001a\u0006\u0003aAA\u0001\"\u000e\u0001\u0003\u0012\u0003\u0006I!L\u0001\be\u0016\f7o\u001c8!\u0011\u00159\u0004\u0001\"\u00019\u0003\u0019a\u0014N\\5u}Q!\u0011h\u000f\u001f>!\tQ\u0004!D\u0001\u0003\u0011\u0015Yb\u00071\u0001\u001e\u0011\u0015\u0019c\u00071\u0001&\u0011\u0015Yc\u00071\u0001.\u0011\u001dy\u0004!!A\u0005\u0002\u0001\u000bAaY8qsR!\u0011(\u0011\"D\u0011\u001dYb\b%AA\u0002uAqa\t \u0011\u0002\u0003\u0007Q\u0005C\u0004,}A\u0005\t\u0019A\u0017\t\u000f\u0015\u0003\u0011\u0013!C\u0001\r\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#A$+\u0005uA5&A%\u0011\u0005){U\"A&\u000b\u00051k\u0015!C;oG\",7m[3e\u0015\tq\u0005#\u0001\u0006b]:|G/\u0019;j_:L!\u0001U&\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004S\u0001E\u0005I\u0011A*\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%eU\tAK\u000b\u0002&\u0011\"9a\u000bAI\u0001\n\u00039\u0016AD2paf$C-\u001a4bk2$HeM\u000b\u00021*\u0012Q\u0006\u0013\u0005\b5\u0002\t\t\u0011\"\u0011\\\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\tA\f\u0005\u0002^E6\taL\u0003\u0002`A\u0006!A.\u00198h\u0015\u0005\t\u0017\u0001\u00026bm\u0006L!A\r0\t\u000f\u0011\u0004\u0011\u0011!C\u0001K\u0006a\u0001O]8ek\u000e$\u0018I]5usV\ta\r\u0005\u0002\u0010O&\u0011\u0001\u000e\u0005\u0002\u0004\u0013:$\bb\u00026\u0001\u0003\u0003%\ta[\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\taw\u000e\u0005\u0002\u0010[&\u0011a\u000e\u0005\u0002\u0004\u0003:L\bb\u00029j\u0003\u0003\u0005\rAZ\u0001\u0004q\u0012\n\u0004b\u0002:\u0001\u0003\u0003%\te]\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\tA\u000fE\u0002vq2l\u0011A\u001e\u0006\u0003oB\t!bY8mY\u0016\u001cG/[8o\u0013\tIhO\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001dY\b!!A\u0005\u0002q\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003KuDq\u0001\u001d>\u0002\u0002\u0003\u0007A\u000e\u0003\u0005\u0000\u0001\u0005\u0005I\u0011IA\u0001\u0003!A\u0017m\u001d5D_\u0012,G#\u00014\t\u0013\u0005\u0015\u0001!!A\u0005B\u0005\u001d\u0011\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003qC\u0011\"a\u0003\u0001\u0003\u0003%\t%!\u0004\u0002\r\u0015\fX/\u00197t)\r)\u0013q\u0002\u0005\ta\u0006%\u0011\u0011!a\u0001Y\u001eI\u00111\u0003\u0002\u0002\u0002#%\u0011QC\u0001\t\u0017&dG\u000eV1tWB\u0019!(a\u0006\u0007\u0011\u0005\u0011\u0011\u0011!E\u0005\u00033\u0019R!a\u0006\u0002\u001c]\u0001\u0002\"!\b\u0002$u)S&O\u0007\u0003\u0003?Q1!!\t\u0011\u0003\u001d\u0011XO\u001c;j[\u0016LA!!\n\u0002 \t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u001a\t\u000f]\n9\u0002\"\u0001\u0002*Q\u0011\u0011Q\u0003\u0005\u000b\u0003\u000b\t9\"!A\u0005F\u0005\u001d\u0001BCA\u0018\u0003/\t\t\u0011\"!\u00022\u0005)\u0011\r\u001d9msR9\u0011(a\r\u00026\u0005]\u0002BB\u000e\u0002.\u0001\u0007Q\u0004\u0003\u0004$\u0003[\u0001\r!\n\u0005\u0007W\u00055\u0002\u0019A\u0017\t\u0015\u0005m\u0012qCA\u0001\n\u0003\u000bi$A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005}\u00121\n\t\u0006\u001f\u0005\u0005\u0013QI\u0005\u0004\u0003\u0007\u0002\"AB(qi&|g\u000e\u0005\u0004\u0010\u0003\u000fjR%L\u0005\u0004\u0003\u0013\u0002\"A\u0002+va2,7\u0007C\u0005\u0002N\u0005e\u0012\u0011!a\u0001s\u0005\u0019\u0001\u0010\n\u0019\t\u0015\u0005E\u0013qCA\u0001\n\u0013\t\u0019&A\u0006sK\u0006$'+Z:pYZ,GCAA+!\ri\u0016qK\u0005\u0004\u00033r&AB(cU\u0016\u001cG\u000f"
)
public class KillTask implements Product, Serializable {
   private final long taskId;
   private final boolean interruptThread;
   private final String reason;

   public static Option<Tuple3<Object, Object, String>> unapply(KillTask var0) {
      return KillTask$.MODULE$.unapply(var0);
   }

   public static KillTask apply(long var0, boolean var2, String var3) {
      return KillTask$.MODULE$.apply(var0, var2, var3);
   }

   public static Function1<Tuple3<Object, Object, String>, KillTask> tupled() {
      return KillTask$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<String, KillTask>>> curried() {
      return KillTask$.MODULE$.curried();
   }

   public long taskId() {
      return this.taskId;
   }

   public boolean interruptThread() {
      return this.interruptThread;
   }

   public String reason() {
      return this.reason;
   }

   public KillTask copy(long taskId, boolean interruptThread, String reason) {
      return new KillTask(taskId, interruptThread, reason);
   }

   public long copy$default$1() {
      return this.taskId();
   }

   public boolean copy$default$2() {
      return this.interruptThread();
   }

   public String copy$default$3() {
      return this.reason();
   }

   public String productPrefix() {
      return "KillTask";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.taskId());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToBoolean(this.interruptThread());
            break;
         case 2:
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
      return x$1 instanceof KillTask;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.taskId()));
      var1 = Statics.mix(var1, this.interruptThread() ? 1231 : 1237);
      var1 = Statics.mix(var1, Statics.anyHash(this.reason()));
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
            if (x$1 instanceof KillTask) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  KillTask var4 = (KillTask)x$1;
                  if (this.taskId() == var4.taskId() && this.interruptThread() == var4.interruptThread()) {
                     label36: {
                        String var10000 = this.reason();
                        String var5 = var4.reason();
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

   public KillTask(long taskId, boolean interruptThread, String reason) {
      this.taskId = taskId;
      this.interruptThread = interruptThread;
      this.reason = reason;
      Product$class.$init$(this);
   }
}
