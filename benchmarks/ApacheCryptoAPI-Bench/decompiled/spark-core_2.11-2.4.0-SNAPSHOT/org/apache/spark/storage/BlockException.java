package org.apache.spark.storage;

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
   bytes = "\u0006\u0001\u0005=c!B\u0001\u0003\u0001\u0012Q!A\u0004\"m_\u000e\\W\t_2faRLwN\u001c\u0006\u0003\u0007\u0011\tqa\u001d;pe\u0006<WM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\u0011\u00011B\u0007\u0010\u0011\u000519bBA\u0007\u0015\u001d\tq!#D\u0001\u0010\u0015\t\u0001\u0012#\u0001\u0004=e>|GOP\u0002\u0001\u0013\u0005\u0019\u0012!B:dC2\f\u0017BA\u000b\u0017\u0003\u001d\u0001\u0018mY6bO\u0016T\u0011aE\u0005\u00031e\u0011\u0011\"\u0012=dKB$\u0018n\u001c8\u000b\u0005U1\u0002CA\u000e\u001d\u001b\u00051\u0012BA\u000f\u0017\u0005\u001d\u0001&o\u001c3vGR\u0004\"aG\u0010\n\u0005\u00012\"\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\u0012\u0001\u0005+\u0007I\u0011A\u0012\u0002\u000f\tdwnY6JIV\tA\u0005\u0005\u0002&M5\t!!\u0003\u0002(\u0005\t9!\t\\8dW&#\u0007\u0002C\u0015\u0001\u0005#\u0005\u000b\u0011\u0002\u0013\u0002\u0011\tdwnY6JI\u0002B\u0001b\u000b\u0001\u0003\u0016\u0004%\t\u0001L\u0001\b[\u0016\u001c8/Y4f+\u0005i\u0003C\u0001\u00182\u001d\tYr&\u0003\u00021-\u00051\u0001K]3eK\u001aL!AM\u001a\u0003\rM#(/\u001b8h\u0015\t\u0001d\u0003\u0003\u00056\u0001\tE\t\u0015!\u0003.\u0003!iWm]:bO\u0016\u0004\u0003\"B\u001c\u0001\t\u0003A\u0014A\u0002\u001fj]&$h\bF\u0002:um\u0002\"!\n\u0001\t\u000b\t2\u0004\u0019\u0001\u0013\t\u000b-2\u0004\u0019A\u0017\t\u000fu\u0002\u0011\u0011!C\u0001}\u0005!1m\u001c9z)\rIt\b\u0011\u0005\bEq\u0002\n\u00111\u0001%\u0011\u001dYC\b%AA\u00025BqA\u0011\u0001\u0012\u0002\u0013\u00051)\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003\u0011S#\u0001J#,\u0003\u0019\u0003\"a\u0012'\u000e\u0003!S!!\u0013&\u0002\u0013Ut7\r[3dW\u0016$'BA&\u0017\u0003)\tgN\\8uCRLwN\\\u0005\u0003\u001b\"\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001dy\u0005!%A\u0005\u0002A\u000babY8qs\u0012\"WMZ1vYR$#'F\u0001RU\tiS\tC\u0004T\u0001\u0005\u0005I\u0011\t+\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005)\u0006C\u0001,\\\u001b\u00059&B\u0001-Z\u0003\u0011a\u0017M\\4\u000b\u0003i\u000bAA[1wC&\u0011!g\u0016\u0005\b;\u0002\t\t\u0011\"\u0001_\u00031\u0001(o\u001c3vGR\f%/\u001b;z+\u0005y\u0006CA\u000ea\u0013\t\tgCA\u0002J]RDqa\u0019\u0001\u0002\u0002\u0013\u0005A-\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005\u0015D\u0007CA\u000eg\u0013\t9gCA\u0002B]fDq!\u001b2\u0002\u0002\u0003\u0007q,A\u0002yIEBqa\u001b\u0001\u0002\u0002\u0013\u0005C.A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u0005i\u0007c\u00018rK6\tqN\u0003\u0002q-\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005I|'\u0001C%uKJ\fGo\u001c:\t\u000fQ\u0004\u0011\u0011!C\u0001k\u0006A1-\u00198FcV\fG\u000e\u0006\u0002wsB\u00111d^\u0005\u0003qZ\u0011qAQ8pY\u0016\fg\u000eC\u0004jg\u0006\u0005\t\u0019A3\t\u000fm\u0004\u0011\u0011!C!y\u0006A\u0001.Y:i\u0007>$W\rF\u0001`\u0011\u001dq\b!!A\u0005B}\fa!Z9vC2\u001cHc\u0001<\u0002\u0002!9\u0011.`A\u0001\u0002\u0004)wACA\u0003\u0005\u0005\u0005\t\u0012\u0001\u0003\u0002\b\u0005q!\t\\8dW\u0016C8-\u001a9uS>t\u0007cA\u0013\u0002\n\u0019I\u0011AAA\u0001\u0012\u0003!\u00111B\n\u0006\u0003\u0013\tiA\b\t\b\u0003\u001f\t)\u0002J\u0017:\u001b\t\t\tBC\u0002\u0002\u0014Y\tqA];oi&lW-\u0003\u0003\u0002\u0018\u0005E!!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oe!9q'!\u0003\u0005\u0002\u0005mACAA\u0004\u0011)\ty\"!\u0003\u0002\u0002\u0013\u0015\u0013\u0011E\u0001\ti>\u001cFO]5oOR\tQ\u000b\u0003\u0006\u0002&\u0005%\u0011\u0011!CA\u0003O\tQ!\u00199qYf$R!OA\u0015\u0003WAaAIA\u0012\u0001\u0004!\u0003BB\u0016\u0002$\u0001\u0007Q\u0006\u0003\u0006\u00020\u0005%\u0011\u0011!CA\u0003c\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u00024\u0005}\u0002#B\u000e\u00026\u0005e\u0012bAA\u001c-\t1q\n\u001d;j_:\u0004RaGA\u001eI5J1!!\u0010\u0017\u0005\u0019!V\u000f\u001d7fe!I\u0011\u0011IA\u0017\u0003\u0003\u0005\r!O\u0001\u0004q\u0012\u0002\u0004BCA#\u0003\u0013\t\t\u0011\"\u0003\u0002H\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\tI\u0005E\u0002W\u0003\u0017J1!!\u0014X\u0005\u0019y%M[3di\u0002"
)
public class BlockException extends Exception implements Product, Serializable {
   private final BlockId blockId;
   private final String message;

   public static Option<Tuple2<BlockId, String>> unapply(BlockException var0) {
      return BlockException$.MODULE$.unapply(var0);
   }

   public static BlockException apply(BlockId var0, String var1) {
      return BlockException$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<BlockId, String>, BlockException> tupled() {
      return BlockException$.MODULE$.tupled();
   }

   public static Function1<BlockId, Function1<String, BlockException>> curried() {
      return BlockException$.MODULE$.curried();
   }

   public BlockId blockId() {
      return this.blockId;
   }

   public String message() {
      return this.message;
   }

   public BlockException copy(BlockId blockId, String message) {
      return new BlockException(blockId, message);
   }

   public BlockId copy$default$1() {
      return this.blockId();
   }

   public String copy$default$2() {
      return this.message();
   }

   public String productPrefix() {
      return "BlockException";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.blockId();
            break;
         case 1:
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
      return x$1 instanceof BlockException;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label62: {
            boolean var3;
            if (x$1 instanceof BlockException) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     BlockException var4 = (BlockException)x$1;
                     BlockId var10000 = this.blockId();
                     BlockId var5 = var4.blockId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     String var7 = this.message();
                     String var6 = var4.message();
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

   public BlockException(BlockId blockId, String message) {
      super(message);
      this.blockId = blockId;
      this.message = message;
      Product$class.$init$(this);
   }
}
