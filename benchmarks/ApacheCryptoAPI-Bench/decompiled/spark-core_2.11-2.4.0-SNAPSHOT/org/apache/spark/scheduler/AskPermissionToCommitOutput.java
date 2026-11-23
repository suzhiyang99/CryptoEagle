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
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]b\u0001B\u0001\u0003\t.\u00111$Q:l!\u0016\u0014X.[:tS>tGk\\\"p[6LGoT;uaV$(BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019B\u0001\u0001\u0007\u0013+A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"!D\n\n\u0005Qq!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001bYI!a\u0006\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011e\u0001!Q3A\u0005\u0002i\tQa\u001d;bO\u0016,\u0012a\u0007\t\u0003\u001bqI!!\b\b\u0003\u0007%sG\u000f\u0003\u0005 \u0001\tE\t\u0015!\u0003\u001c\u0003\u0019\u0019H/Y4fA!A\u0011\u0005\u0001BK\u0002\u0013\u0005!$A\u0005qCJ$\u0018\u000e^5p]\"A1\u0005\u0001B\tB\u0003%1$\u0001\u0006qCJ$\u0018\u000e^5p]\u0002B\u0001\"\n\u0001\u0003\u0016\u0004%\tAG\u0001\u000eCR$X-\u001c9u\u001dVl'-\u001a:\t\u0011\u001d\u0002!\u0011#Q\u0001\nm\ta\"\u0019;uK6\u0004HOT;nE\u0016\u0014\b\u0005C\u0003*\u0001\u0011\u0005!&\u0001\u0004=S:LGO\u0010\u000b\u0005W5rs\u0006\u0005\u0002-\u00015\t!\u0001C\u0003\u001aQ\u0001\u00071\u0004C\u0003\"Q\u0001\u00071\u0004C\u0003&Q\u0001\u00071\u0004C\u00042\u0001\u0005\u0005I\u0011\u0001\u001a\u0002\t\r|\u0007/\u001f\u000b\u0005WM\"T\u0007C\u0004\u001aaA\u0005\t\u0019A\u000e\t\u000f\u0005\u0002\u0004\u0013!a\u00017!9Q\u0005\rI\u0001\u0002\u0004Y\u0002bB\u001c\u0001#\u0003%\t\u0001O\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005I$FA\u000e;W\u0005Y\u0004C\u0001\u001fB\u001b\u0005i$B\u0001 @\u0003%)hn\u00195fG.,GM\u0003\u0002A\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\tk$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9A\tAI\u0001\n\u0003A\u0014AD2paf$C-\u001a4bk2$HE\r\u0005\b\r\u0002\t\n\u0011\"\u00019\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIMBq\u0001\u0013\u0001\u0002\u0002\u0013\u0005\u0013*A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002\u0015B\u00111\nU\u0007\u0002\u0019*\u0011QJT\u0001\u0005Y\u0006twMC\u0001P\u0003\u0011Q\u0017M^1\n\u0005Ec%AB*ue&tw\rC\u0004T\u0001\u0005\u0005I\u0011\u0001\u000e\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\t\u000fU\u0003\u0011\u0011!C\u0001-\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HCA,[!\ti\u0001,\u0003\u0002Z\u001d\t\u0019\u0011I\\=\t\u000fm#\u0016\u0011!a\u00017\u0005\u0019\u0001\u0010J\u0019\t\u000fu\u0003\u0011\u0011!C!=\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001`!\r\u00017mV\u0007\u0002C*\u0011!MD\u0001\u000bG>dG.Z2uS>t\u0017B\u00013b\u0005!IE/\u001a:bi>\u0014\bb\u00024\u0001\u0003\u0003%\taZ\u0001\tG\u0006tW)];bYR\u0011\u0001n\u001b\t\u0003\u001b%L!A\u001b\b\u0003\u000f\t{w\u000e\\3b]\"91,ZA\u0001\u0002\u00049\u0006bB7\u0001\u0003\u0003%\tE\\\u0001\tQ\u0006\u001c\bnQ8eKR\t1\u0004C\u0004q\u0001\u0005\u0005I\u0011I9\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012A\u0013\u0005\bg\u0002\t\t\u0011\"\u0011u\u0003\u0019)\u0017/^1mgR\u0011\u0001.\u001e\u0005\b7J\f\t\u00111\u0001X\u000f\u001d9(!!A\t\na\f1$Q:l!\u0016\u0014X.[:tS>tGk\\\"p[6LGoT;uaV$\bC\u0001\u0017z\r\u001d\t!!!A\t\ni\u001c2!_>\u0016!\u0019axpG\u000e\u001cW5\tQP\u0003\u0002\u007f\u001d\u00059!/\u001e8uS6,\u0017bAA\u0001{\n\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u001a\t\r%JH\u0011AA\u0003)\u0005A\bb\u00029z\u0003\u0003%)%\u001d\u0005\n\u0003\u0017I\u0018\u0011!CA\u0003\u001b\tQ!\u00199qYf$raKA\b\u0003#\t\u0019\u0002\u0003\u0004\u001a\u0003\u0013\u0001\ra\u0007\u0005\u0007C\u0005%\u0001\u0019A\u000e\t\r\u0015\nI\u00011\u0001\u001c\u0011%\t9\"_A\u0001\n\u0003\u000bI\"A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005m\u0011q\u0005\t\u0006\u001b\u0005u\u0011\u0011E\u0005\u0004\u0003?q!AB(qi&|g\u000e\u0005\u0004\u000e\u0003GY2dG\u0005\u0004\u0003Kq!A\u0002+va2,7\u0007C\u0005\u0002*\u0005U\u0011\u0011!a\u0001W\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u00055\u00120!A\u0005\n\u0005=\u0012a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\r\u0011\u0007-\u000b\u0019$C\u0002\u000261\u0013aa\u00142kK\u000e$\b"
)
public class AskPermissionToCommitOutput implements Product, Serializable {
   private final int stage;
   private final int partition;
   private final int attemptNumber;

   public static Option<Tuple3<Object, Object, Object>> unapply(AskPermissionToCommitOutput var0) {
      return AskPermissionToCommitOutput$.MODULE$.unapply(var0);
   }

   public static AskPermissionToCommitOutput apply(int var0, int var1, int var2) {
      return AskPermissionToCommitOutput$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<Object, Object, Object>, AskPermissionToCommitOutput> tupled() {
      return AskPermissionToCommitOutput$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<Object, AskPermissionToCommitOutput>>> curried() {
      return AskPermissionToCommitOutput$.MODULE$.curried();
   }

   public int stage() {
      return this.stage;
   }

   public int partition() {
      return this.partition;
   }

   public int attemptNumber() {
      return this.attemptNumber;
   }

   public AskPermissionToCommitOutput copy(int stage, int partition, int attemptNumber) {
      return new AskPermissionToCommitOutput(stage, partition, attemptNumber);
   }

   public int copy$default$1() {
      return this.stage();
   }

   public int copy$default$2() {
      return this.partition();
   }

   public int copy$default$3() {
      return this.attemptNumber();
   }

   public String productPrefix() {
      return "AskPermissionToCommitOutput";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Integer var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.stage());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.partition());
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
      return x$1 instanceof AskPermissionToCommitOutput;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.stage());
      var1 = Statics.mix(var1, this.partition());
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
            if (x$1 instanceof AskPermissionToCommitOutput) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               AskPermissionToCommitOutput var4 = (AskPermissionToCommitOutput)x$1;
               if (this.stage() == var4.stage() && this.partition() == var4.partition() && this.attemptNumber() == var4.attemptNumber() && var4.canEqual(this)) {
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

   public AskPermissionToCommitOutput(int stage, int partition, int attemptNumber) {
      this.stage = stage;
      this.partition = partition;
      this.attemptNumber = attemptNumber;
      Product$class.$init$(this);
   }
}
