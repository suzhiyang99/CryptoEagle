package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
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
   bytes = "\u0006\u0001\u0005\u001db\u0001B\u0001\u0003\u0001.\u00111d\u00159be.d\u0015n\u001d;f]\u0016\u00148\u000b^1hK\u000e{W\u000e\u001d7fi\u0016$'BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019R\u0001\u0001\u0007\u0013-e\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\n\u0015\u001b\u0005\u0011\u0011BA\u000b\u0003\u0005I\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u00163XM\u001c;\u0011\u000559\u0012B\u0001\r\u000f\u0005\u001d\u0001&o\u001c3vGR\u0004\"!\u0004\u000e\n\u0005mq!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000f\u0001\u0005+\u0007I\u0011\u0001\u0010\u0002\u0013M$\u0018mZ3J]\u001a|W#A\u0010\u0011\u0005M\u0001\u0013BA\u0011\u0003\u0005%\u0019F/Y4f\u0013:4w\u000e\u0003\u0005$\u0001\tE\t\u0015!\u0003 \u0003)\u0019H/Y4f\u0013:4w\u000e\t\u0005\u0006K\u0001!\tAJ\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\u001dB\u0003CA\n\u0001\u0011\u0015iB\u00051\u0001 \u0011\u001dQ\u0003!!A\u0005\u0002-\nAaY8qsR\u0011q\u0005\f\u0005\b;%\u0002\n\u00111\u0001 \u0011\u001dq\u0003!%A\u0005\u0002=\nabY8qs\u0012\"WMZ1vYR$\u0013'F\u00011U\ty\u0012gK\u00013!\t\u0019\u0004(D\u00015\u0015\t)d'A\u0005v]\u000eDWmY6fI*\u0011qGD\u0001\u000bC:tw\u000e^1uS>t\u0017BA\u001d5\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\bw\u0001\t\t\u0011\"\u0011=\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\tQ\b\u0005\u0002?\u00076\tqH\u0003\u0002A\u0003\u0006!A.\u00198h\u0015\u0005\u0011\u0015\u0001\u00026bm\u0006L!\u0001R \u0003\rM#(/\u001b8h\u0011\u001d1\u0005!!A\u0005\u0002\u001d\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012\u0001\u0013\t\u0003\u001b%K!A\u0013\b\u0003\u0007%sG\u000fC\u0004M\u0001\u0005\u0005I\u0011A'\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011a*\u0015\t\u0003\u001b=K!\u0001\u0015\b\u0003\u0007\u0005s\u0017\u0010C\u0004S\u0017\u0006\u0005\t\u0019\u0001%\u0002\u0007a$\u0013\u0007C\u0004U\u0001\u0005\u0005I\u0011I+\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012A\u0016\t\u0004/jsU\"\u0001-\u000b\u0005es\u0011AC2pY2,7\r^5p]&\u00111\f\u0017\u0002\t\u0013R,'/\u0019;pe\"9Q\fAA\u0001\n\u0003q\u0016\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005}\u0013\u0007CA\u0007a\u0013\t\tgBA\u0004C_>dW-\u00198\t\u000fIc\u0016\u0011!a\u0001\u001d\"9A\rAA\u0001\n\u0003*\u0017\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003!Cqa\u001a\u0001\u0002\u0002\u0013\u0005\u0003.\u0001\u0005u_N#(/\u001b8h)\u0005i\u0004b\u00026\u0001\u0003\u0003%\te[\u0001\u0007KF,\u0018\r\\:\u0015\u0005}c\u0007b\u0002*j\u0003\u0003\u0005\rA\u0014\u0015\u0003\u00019\u0004\"a\\9\u000e\u0003AT!a\u000e\u0003\n\u0005I\u0004(\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLwa\u0002;\u0003\u0003\u0003E\t!^\u0001\u001c'B\f'o\u001b'jgR,g.\u001a:Ti\u0006<WmQ8na2,G/\u001a3\u0011\u0005M1haB\u0001\u0003\u0003\u0003E\ta^\n\u0004mbL\u0002\u0003B=}?\u001dj\u0011A\u001f\u0006\u0003w:\tqA];oi&lW-\u0003\u0002~u\n\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u0019\t\u000b\u00152H\u0011A@\u0015\u0003UDqa\u001a<\u0002\u0002\u0013\u0015\u0003\u000eC\u0005\u0002\u0006Y\f\t\u0011\"!\u0002\b\u0005)\u0011\r\u001d9msR\u0019q%!\u0003\t\ru\t\u0019\u00011\u0001 \u0011%\tiA^A\u0001\n\u0003\u000by!A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005E\u0011q\u0003\t\u0005\u001b\u0005Mq$C\u0002\u0002\u00169\u0011aa\u00149uS>t\u0007\"CA\r\u0003\u0017\t\t\u00111\u0001(\u0003\rAH\u0005\r\u0005\n\u0003;1\u0018\u0011!C\u0005\u0003?\t1B]3bIJ+7o\u001c7wKR\u0011\u0011\u0011\u0005\t\u0004}\u0005\r\u0012bAA\u0013\u007f\t1qJ\u00196fGR\u0004"
)
public class SparkListenerStageCompleted implements SparkListenerEvent, Product, Serializable {
   private final StageInfo stageInfo;

   public static Option<StageInfo> unapply(SparkListenerStageCompleted var0) {
      return SparkListenerStageCompleted$.MODULE$.unapply(var0);
   }

   public static SparkListenerStageCompleted apply(StageInfo var0) {
      return SparkListenerStageCompleted$.MODULE$.apply(var0);
   }

   public static <A> Function1<StageInfo, A> andThen(Function1<SparkListenerStageCompleted, A> var0) {
      return SparkListenerStageCompleted$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, SparkListenerStageCompleted> compose(Function1<A, StageInfo> var0) {
      return SparkListenerStageCompleted$.MODULE$.compose(var0);
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public StageInfo stageInfo() {
      return this.stageInfo;
   }

   public SparkListenerStageCompleted copy(StageInfo stageInfo) {
      return new SparkListenerStageCompleted(stageInfo);
   }

   public StageInfo copy$default$1() {
      return this.stageInfo();
   }

   public String productPrefix() {
      return "SparkListenerStageCompleted";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.stageInfo();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerStageCompleted;
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
            if (x$1 instanceof SparkListenerStageCompleted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     SparkListenerStageCompleted var4 = (SparkListenerStageCompleted)x$1;
                     StageInfo var10000 = this.stageInfo();
                     StageInfo var5 = var4.stageInfo();
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

   public SparkListenerStageCompleted(StageInfo stageInfo) {
      this.stageInfo = stageInfo;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
