package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.storage.BlockUpdatedInfo;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u00055b\u0001B\u0001\u0003\u0001.\u0011\u0011d\u00159be.d\u0015n\u001d;f]\u0016\u0014(\t\\8dWV\u0003H-\u0019;fI*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\u0015\u0001AB\u0005\f\u001a!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u00111\u0003F\u0007\u0002\u0005%\u0011QC\u0001\u0002\u0013'B\f'o\u001b'jgR,g.\u001a:Fm\u0016tG\u000f\u0005\u0002\u000e/%\u0011\u0001D\u0004\u0002\b!J|G-^2u!\ti!$\u0003\u0002\u001c\u001d\ta1+\u001a:jC2L'0\u00192mK\"AQ\u0004\u0001BK\u0002\u0013\u0005a$\u0001\tcY>\u001c7.\u00169eCR,G-\u00138g_V\tq\u0004\u0005\u0002!G5\t\u0011E\u0003\u0002#\t\u000591\u000f^8sC\u001e,\u0017B\u0001\u0013\"\u0005A\u0011En\\2l+B$\u0017\r^3e\u0013:4w\u000e\u0003\u0005'\u0001\tE\t\u0015!\u0003 \u0003E\u0011Gn\\2l+B$\u0017\r^3e\u0013:4w\u000e\t\u0005\u0006Q\u0001!\t!K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005)Z\u0003CA\n\u0001\u0011\u0015ir\u00051\u0001 \u0011\u001di\u0003!!A\u0005\u00029\nAaY8qsR\u0011!f\f\u0005\b;1\u0002\n\u00111\u0001 \u0011\u001d\t\u0004!%A\u0005\u0002I\nabY8qs\u0012\"WMZ1vYR$\u0013'F\u00014U\tyBgK\u00016!\t14(D\u00018\u0015\tA\u0014(A\u0005v]\u000eDWmY6fI*\u0011!HD\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001\u001f8\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b}\u0001\t\t\u0011\"\u0011@\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\t\u0001\t\u0005\u0002B\r6\t!I\u0003\u0002D\t\u0006!A.\u00198h\u0015\u0005)\u0015\u0001\u00026bm\u0006L!a\u0012\"\u0003\rM#(/\u001b8h\u0011\u001dI\u0005!!A\u0005\u0002)\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012a\u0013\t\u0003\u001b1K!!\u0014\b\u0003\u0007%sG\u000fC\u0004P\u0001\u0005\u0005I\u0011\u0001)\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011\u0011\u000b\u0016\t\u0003\u001bIK!a\u0015\b\u0003\u0007\u0005s\u0017\u0010C\u0004V\u001d\u0006\u0005\t\u0019A&\u0002\u0007a$\u0013\u0007C\u0004X\u0001\u0005\u0005I\u0011\t-\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012!\u0017\t\u00045v\u000bV\"A.\u000b\u0005qs\u0011AC2pY2,7\r^5p]&\u0011al\u0017\u0002\t\u0013R,'/\u0019;pe\"9\u0001\rAA\u0001\n\u0003\t\u0017\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005\t,\u0007CA\u0007d\u0013\t!gBA\u0004C_>dW-\u00198\t\u000fU{\u0016\u0011!a\u0001#\"9q\rAA\u0001\n\u0003B\u0017\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003-CqA\u001b\u0001\u0002\u0002\u0013\u00053.\u0001\u0005u_N#(/\u001b8h)\u0005\u0001\u0005bB7\u0001\u0003\u0003%\tE\\\u0001\u0007KF,\u0018\r\\:\u0015\u0005\t|\u0007bB+m\u0003\u0003\u0005\r!\u0015\u0015\u0003\u0001E\u0004\"A\u001d;\u000e\u0003MT!A\u000f\u0003\n\u0005U\u001c(\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLwaB<\u0003\u0003\u0003E\t\u0001_\u0001\u001a'B\f'o\u001b'jgR,g.\u001a:CY>\u001c7.\u00169eCR,G\r\u0005\u0002\u0014s\u001a9\u0011AAA\u0001\u0012\u0003Q8cA=|3A!Ap`\u0010+\u001b\u0005i(B\u0001@\u000f\u0003\u001d\u0011XO\u001c;j[\u0016L1!!\u0001~\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|g.\r\u0005\u0007Qe$\t!!\u0002\u0015\u0003aDqA[=\u0002\u0002\u0013\u00153\u000eC\u0005\u0002\fe\f\t\u0011\"!\u0002\u000e\u0005)\u0011\r\u001d9msR\u0019!&a\u0004\t\ru\tI\u00011\u0001 \u0011%\t\u0019\"_A\u0001\n\u0003\u000b)\"A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005]\u0011Q\u0004\t\u0005\u001b\u0005eq$C\u0002\u0002\u001c9\u0011aa\u00149uS>t\u0007\"CA\u0010\u0003#\t\t\u00111\u0001+\u0003\rAH\u0005\r\u0005\n\u0003GI\u0018\u0011!C\u0005\u0003K\t1B]3bIJ+7o\u001c7wKR\u0011\u0011q\u0005\t\u0004\u0003\u0006%\u0012bAA\u0016\u0005\n1qJ\u00196fGR\u0004"
)
public class SparkListenerBlockUpdated implements SparkListenerEvent, Product, Serializable {
   private final BlockUpdatedInfo blockUpdatedInfo;

   public static Option<BlockUpdatedInfo> unapply(SparkListenerBlockUpdated var0) {
      return SparkListenerBlockUpdated$.MODULE$.unapply(var0);
   }

   public static SparkListenerBlockUpdated apply(BlockUpdatedInfo var0) {
      return SparkListenerBlockUpdated$.MODULE$.apply(var0);
   }

   public static <A> Function1<BlockUpdatedInfo, A> andThen(Function1<SparkListenerBlockUpdated, A> var0) {
      return SparkListenerBlockUpdated$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SparkListenerBlockUpdated> compose(Function1<A, BlockUpdatedInfo> var0) {
      return SparkListenerBlockUpdated$.MODULE$.compose(var0);
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public BlockUpdatedInfo blockUpdatedInfo() {
      return this.blockUpdatedInfo;
   }

   public SparkListenerBlockUpdated copy(BlockUpdatedInfo blockUpdatedInfo) {
      return new SparkListenerBlockUpdated(blockUpdatedInfo);
   }

   public BlockUpdatedInfo copy$default$1() {
      return this.blockUpdatedInfo();
   }

   public String productPrefix() {
      return "SparkListenerBlockUpdated";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.blockUpdatedInfo();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerBlockUpdated;
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
            if (x$1 instanceof SparkListenerBlockUpdated) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     SparkListenerBlockUpdated var4 = (SparkListenerBlockUpdated)x$1;
                     BlockUpdatedInfo var10000 = this.blockUpdatedInfo();
                     BlockUpdatedInfo var5 = var4.blockUpdatedInfo();
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

   public SparkListenerBlockUpdated(BlockUpdatedInfo blockUpdatedInfo) {
      this.blockUpdatedInfo = blockUpdatedInfo;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
