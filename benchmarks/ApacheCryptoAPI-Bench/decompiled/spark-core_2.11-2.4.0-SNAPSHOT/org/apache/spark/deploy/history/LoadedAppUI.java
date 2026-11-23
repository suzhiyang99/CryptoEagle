package org.apache.spark.deploy.history;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.spark.ui.SparkUI;
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
   bytes = "\u0006\u0001\u0005\u0015d!B\u0001\u0003\u0001\na!a\u0003'pC\u0012,G-\u00119q+&S!a\u0001\u0003\u0002\u000f!L7\u000f^8ss*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001cB\u0001A\u0007\u0014-A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"A\u0004\u000b\n\u0005Uy!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001d]I!\u0001G\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011i\u0001!Q3A\u0005\u0002q\t!!^5\u0004\u0001U\tQ\u0004\u0005\u0002\u001fA5\tqD\u0003\u0002\u001b\r%\u0011\u0011e\b\u0002\b'B\f'o[+J\u0011!\u0019\u0003A!E!\u0002\u0013i\u0012aA;jA!)Q\u0005\u0001C\u0001M\u00051A(\u001b8jiz\"\"aJ\u0015\u0011\u0005!\u0002Q\"\u0001\u0002\t\u000bi!\u0003\u0019A\u000f\t\u000f-\u0002!\u0019!C\u0001Y\u0005!An\\2l+\u0005i\u0003C\u0001\u00188\u001b\u0005y#B\u0001\u00192\u0003\u0015awnY6t\u0015\t\u00114'\u0001\u0006d_:\u001cWO\u001d:f]RT!\u0001N\u001b\u0002\tU$\u0018\u000e\u001c\u0006\u0002m\u0005!!.\u0019<b\u0013\tAtF\u0001\fSK\u0016tGO]1oiJ+\u0017\rZ,sSR,Gj\\2l\u0011\u0019Q\u0004\u0001)A\u0005[\u0005)An\\2lA!9A\b\u0001a\u0001\n\u0013i\u0014AB0wC2LG-F\u0001?!\tqq(\u0003\u0002A\u001f\t9!i\\8mK\u0006t\u0007b\u0002\"\u0001\u0001\u0004%IaQ\u0001\u000b?Z\fG.\u001b3`I\u0015\fHC\u0001#H!\tqQ)\u0003\u0002G\u001f\t!QK\\5u\u0011\u001dA\u0015)!AA\u0002y\n1\u0001\u001f\u00132\u0011\u0019Q\u0005\u0001)Q\u0005}\u00059qL^1mS\u0012\u0004\u0003FA%M!\tqQ*\u0003\u0002O\u001f\tAao\u001c7bi&dW\rC\u0003Q\u0001\u0011\u0005Q(A\u0003wC2LG\rC\u0003S\u0001\u0011\u00051+\u0001\u0006j]Z\fG.\u001b3bi\u0016$\u0012\u0001\u0012\u0005\b+\u0002\t\t\u0011\"\u0001W\u0003\u0011\u0019w\u000e]=\u0015\u0005\u001d:\u0006b\u0002\u000eU!\u0003\u0005\r!\b\u0005\b3\u0002\t\n\u0011\"\u0001[\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012a\u0017\u0016\u0003;q[\u0013!\u0018\t\u0003=\u000el\u0011a\u0018\u0006\u0003A\u0006\f\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005\t|\u0011AC1o]>$\u0018\r^5p]&\u0011Am\u0018\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u00024\u0001\u0003\u0003%\teZ\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003!\u0004\"!\u001b7\u000e\u0003)T!a[\u001b\u0002\t1\fgnZ\u0005\u0003[*\u0014aa\u0015;sS:<\u0007bB8\u0001\u0003\u0003%\t\u0001]\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002cB\u0011aB]\u0005\u0003g>\u00111!\u00138u\u0011\u001d)\b!!A\u0005\u0002Y\fa\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002xuB\u0011a\u0002_\u0005\u0003s>\u00111!\u00118z\u0011\u001dAE/!AA\u0002EDq\u0001 \u0001\u0002\u0002\u0013\u0005S0A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005q\b\u0003B@\u0002\u0006]l!!!\u0001\u000b\u0007\u0005\rq\"\u0001\u0006d_2dWm\u0019;j_:LA!a\u0002\u0002\u0002\tA\u0011\n^3sCR|'\u000fC\u0005\u0002\f\u0001\t\t\u0011\"\u0001\u0002\u000e\u0005A1-\u00198FcV\fG\u000eF\u0002?\u0003\u001fA\u0001\u0002SA\u0005\u0003\u0003\u0005\ra\u001e\u0005\n\u0003'\u0001\u0011\u0011!C!\u0003+\t\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002c\"I\u0011\u0011\u0004\u0001\u0002\u0002\u0013\u0005\u00131D\u0001\ti>\u001cFO]5oOR\t\u0001\u000eC\u0005\u0002 \u0001\t\t\u0011\"\u0011\u0002\"\u00051Q-];bYN$2APA\u0012\u0011!A\u0015QDA\u0001\u0002\u00049xACA\u0014\u0005\u0005\u0005\t\u0012\u0001\u0002\u0002*\u0005YAj\\1eK\u0012\f\u0005\u000f]+J!\rA\u00131\u0006\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0003[\u0019R!a\u000b\u00020Y\u0001b!!\r\u00028u9SBAA\u001a\u0015\r\t)dD\u0001\beVtG/[7f\u0013\u0011\tI$a\r\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t\u0017\u0007C\u0004&\u0003W!\t!!\u0010\u0015\u0005\u0005%\u0002BCA\r\u0003W\t\t\u0011\"\u0012\u0002\u001c!Q\u00111IA\u0016\u0003\u0003%\t)!\u0012\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0007\u001d\n9\u0005\u0003\u0004\u001b\u0003\u0003\u0002\r!\b\u0005\u000b\u0003\u0017\nY#!A\u0005\u0002\u00065\u0013aB;oCB\u0004H.\u001f\u000b\u0005\u0003\u001f\n)\u0006\u0005\u0003\u000f\u0003#j\u0012bAA*\u001f\t1q\n\u001d;j_:D\u0011\"a\u0016\u0002J\u0005\u0005\t\u0019A\u0014\u0002\u0007a$\u0003\u0007\u0003\u0006\u0002\\\u0005-\u0012\u0011!C\u0005\u0003;\n1B]3bIJ+7o\u001c7wKR\u0011\u0011q\f\t\u0004S\u0006\u0005\u0014bAA2U\n1qJ\u00196fGR\u0004"
)
public class LoadedAppUI implements Product, Serializable {
   private final SparkUI ui;
   private final ReentrantReadWriteLock lock;
   private volatile boolean _valid;

   public static Option<SparkUI> unapply(LoadedAppUI var0) {
      return LoadedAppUI$.MODULE$.unapply(var0);
   }

   public static LoadedAppUI apply(SparkUI var0) {
      return LoadedAppUI$.MODULE$.apply(var0);
   }

   public static <A> Function1<SparkUI, A> andThen(Function1<LoadedAppUI, A> var0) {
      return LoadedAppUI$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, LoadedAppUI> compose(Function1<A, SparkUI> var0) {
      return LoadedAppUI$.MODULE$.compose(var0);
   }

   public SparkUI ui() {
      return this.ui;
   }

   public ReentrantReadWriteLock lock() {
      return this.lock;
   }

   private boolean _valid() {
      return this._valid;
   }

   private void _valid_$eq(boolean x$1) {
      this._valid = x$1;
   }

   public boolean valid() {
      return this._valid();
   }

   public void invalidate() {
      this.lock().writeLock().lock();

      try {
         this._valid_$eq(false);
      } finally {
         this.lock().writeLock().unlock();
      }

   }

   public LoadedAppUI copy(SparkUI ui) {
      return new LoadedAppUI(ui);
   }

   public SparkUI copy$default$1() {
      return this.ui();
   }

   public String productPrefix() {
      return "LoadedAppUI";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.ui();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof LoadedAppUI;
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
            if (x$1 instanceof LoadedAppUI) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     LoadedAppUI var4 = (LoadedAppUI)x$1;
                     SparkUI var10000 = this.ui();
                     SparkUI var5 = var4.ui();
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

   public LoadedAppUI(SparkUI ui) {
      this.ui = ui;
      Product$class.$init$(this);
      this.lock = new ReentrantReadWriteLock();
      this._valid = true;
   }
}
