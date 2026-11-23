package org.apache.spark.scheduler.local;

import java.nio.ByteBuffer;
import scala.Enumeration;
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
   bytes = "\u0006\u0001\u0005md\u0001B\u0001\u0003\t6\u0011Ab\u0015;biV\u001cX\u000b\u001d3bi\u0016T!a\u0001\u0003\u0002\u000b1|7-\u00197\u000b\u0005\u00151\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001A\n\u0005\u00019!r\u0003\u0005\u0002\u0010%5\t\u0001CC\u0001\u0012\u0003\u0015\u00198-\u00197b\u0013\t\u0019\u0002C\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001fUI!A\u0006\t\u0003\u000fA\u0013x\u000eZ;diB\u0011q\u0002G\u0005\u00033A\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001b\u0007\u0001\u0003\u0016\u0004%\t\u0001H\u0001\u0007i\u0006\u001c8.\u00133\u0016\u0003u\u0001\"a\u0004\u0010\n\u0005}\u0001\"\u0001\u0002'p]\u001eD\u0001\"\t\u0001\u0003\u0012\u0003\u0006I!H\u0001\bi\u0006\u001c8.\u00133!\u0011!\u0019\u0003A!f\u0001\n\u0003!\u0013!B:uCR,W#A\u0013\u0011\u0005\u0019\u0012dBA\u00141\u001d\tAsF\u0004\u0002*]9\u0011!&L\u0007\u0002W)\u0011A\u0006D\u0001\u0007yI|w\u000e\u001e \n\u0003-I!!\u0003\u0006\n\u0005\u001dA\u0011BA\u0019\u0007\u0003%!\u0016m]6Ti\u0006$X-\u0003\u00024i\tIA+Y:l'R\fG/\u001a\u0006\u0003c\u0019A\u0001B\u000e\u0001\u0003\u0012\u0003\u0006I!J\u0001\u0007gR\fG/\u001a\u0011\t\u0011a\u0002!Q3A\u0005\u0002e\nab]3sS\u0006d\u0017N_3e\t\u0006$\u0018-F\u0001;!\tY\u0004)D\u0001=\u0015\tid(A\u0002oS>T\u0011aP\u0001\u0005U\u00064\u0018-\u0003\u0002By\tQ!)\u001f;f\u0005V4g-\u001a:\t\u0011\r\u0003!\u0011#Q\u0001\ni\nqb]3sS\u0006d\u0017N_3e\t\u0006$\u0018\r\t\u0005\u0006\u000b\u0002!\tAR\u0001\u0007y%t\u0017\u000e\u001e \u0015\t\u001dK%j\u0013\t\u0003\u0011\u0002i\u0011A\u0001\u0005\u00067\u0011\u0003\r!\b\u0005\u0006G\u0011\u0003\r!\n\u0005\u0006q\u0011\u0003\rA\u000f\u0005\b\u001b\u0002\t\t\u0011\"\u0001O\u0003\u0011\u0019w\u000e]=\u0015\t\u001d{\u0005+\u0015\u0005\b71\u0003\n\u00111\u0001\u001e\u0011\u001d\u0019C\n%AA\u0002\u0015Bq\u0001\u000f'\u0011\u0002\u0003\u0007!\bC\u0004T\u0001E\u0005I\u0011\u0001+\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\tQK\u000b\u0002\u001e-.\nq\u000b\u0005\u0002Y;6\t\u0011L\u0003\u0002[7\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u00039B\t!\"\u00198o_R\fG/[8o\u0013\tq\u0016LA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq\u0001\u0019\u0001\u0012\u0002\u0013\u0005\u0011-\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\u0016\u0003\tT#!\n,\t\u000f\u0011\u0004\u0011\u0013!C\u0001K\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u001aT#\u00014+\u0005i2\u0006b\u00025\u0001\u0003\u0003%\t%[\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003)\u0004\"a\u001b8\u000e\u00031T!!\u001c \u0002\t1\fgnZ\u0005\u0003_2\u0014aa\u0015;sS:<\u0007bB9\u0001\u0003\u0003%\tA]\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002gB\u0011q\u0002^\u0005\u0003kB\u00111!\u00138u\u0011\u001d9\b!!A\u0005\u0002a\fa\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002zyB\u0011qB_\u0005\u0003wB\u00111!\u00118z\u0011\u001dih/!AA\u0002M\f1\u0001\u001f\u00132\u0011!y\b!!A\u0005B\u0005\u0005\u0011a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0005\u0005\r\u0001#BA\u0003\u0003\u0017IXBAA\u0004\u0015\r\tI\u0001E\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA\u0007\u0003\u000f\u0011\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u0003#\u0001\u0011\u0011!C\u0001\u0003'\t\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0005\u0003+\tY\u0002E\u0002\u0010\u0003/I1!!\u0007\u0011\u0005\u001d\u0011un\u001c7fC:D\u0001\"`A\b\u0003\u0003\u0005\r!\u001f\u0005\n\u0003?\u0001\u0011\u0011!C!\u0003C\t\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002g\"I\u0011Q\u0005\u0001\u0002\u0002\u0013\u0005\u0013qE\u0001\ti>\u001cFO]5oOR\t!\u000eC\u0005\u0002,\u0001\t\t\u0011\"\u0011\u0002.\u00051Q-];bYN$B!!\u0006\u00020!AQ0!\u000b\u0002\u0002\u0003\u0007\u0011pB\u0005\u00024\t\t\t\u0011#\u0003\u00026\u0005a1\u000b^1ukN,\u0006\u000fZ1uKB\u0019\u0001*a\u000e\u0007\u0011\u0005\u0011\u0011\u0011!E\u0005\u0003s\u0019R!a\u000e\u0002<]\u0001\u0002\"!\u0010\u0002Du)#hR\u0007\u0003\u0003\u007fQ1!!\u0011\u0011\u0003\u001d\u0011XO\u001c;j[\u0016LA!!\u0012\u0002@\t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u001a\t\u000f\u0015\u000b9\u0004\"\u0001\u0002JQ\u0011\u0011Q\u0007\u0005\u000b\u0003K\t9$!A\u0005F\u0005\u001d\u0002BCA(\u0003o\t\t\u0011\"!\u0002R\u0005)\u0011\r\u001d9msR9q)a\u0015\u0002V\u0005]\u0003BB\u000e\u0002N\u0001\u0007Q\u0004\u0003\u0004$\u0003\u001b\u0002\r!\n\u0005\u0007q\u00055\u0003\u0019\u0001\u001e\t\u0015\u0005m\u0013qGA\u0001\n\u0003\u000bi&A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005}\u00131\u000e\t\u0006\u001f\u0005\u0005\u0014QM\u0005\u0004\u0003G\u0002\"AB(qi&|g\u000e\u0005\u0004\u0010\u0003OjREO\u0005\u0004\u0003S\u0002\"A\u0002+va2,7\u0007C\u0005\u0002n\u0005e\u0013\u0011!a\u0001\u000f\u0006\u0019\u0001\u0010\n\u0019\t\u0015\u0005E\u0014qGA\u0001\n\u0013\t\u0019(A\u0006sK\u0006$'+Z:pYZ,GCAA;!\rY\u0017qO\u0005\u0004\u0003sb'AB(cU\u0016\u001cG\u000f"
)
public class StatusUpdate implements Product, Serializable {
   private final long taskId;
   private final Enumeration.Value state;
   private final ByteBuffer serializedData;

   public static Option<Tuple3<Object, Enumeration.Value, ByteBuffer>> unapply(StatusUpdate var0) {
      return StatusUpdate$.MODULE$.unapply(var0);
   }

   public static StatusUpdate apply(long var0, Enumeration.Value var2, ByteBuffer var3) {
      return StatusUpdate$.MODULE$.apply(var0, var2, var3);
   }

   public static Function1<Tuple3<Object, Enumeration.Value, ByteBuffer>, StatusUpdate> tupled() {
      return StatusUpdate$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Enumeration.Value, Function1<ByteBuffer, StatusUpdate>>> curried() {
      return StatusUpdate$.MODULE$.curried();
   }

   public long taskId() {
      return this.taskId;
   }

   public Enumeration.Value state() {
      return this.state;
   }

   public ByteBuffer serializedData() {
      return this.serializedData;
   }

   public StatusUpdate copy(long taskId, Enumeration.Value state, ByteBuffer serializedData) {
      return new StatusUpdate(taskId, state, serializedData);
   }

   public long copy$default$1() {
      return this.taskId();
   }

   public Enumeration.Value copy$default$2() {
      return this.state();
   }

   public ByteBuffer copy$default$3() {
      return this.serializedData();
   }

   public String productPrefix() {
      return "StatusUpdate";
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
            var10000 = this.state();
            break;
         case 2:
            var10000 = this.serializedData();
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
      return x$1 instanceof StatusUpdate;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.taskId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.state()));
      var1 = Statics.mix(var1, Statics.anyHash(this.serializedData()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label64: {
            boolean var3;
            if (x$1 instanceof StatusUpdate) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label46: {
                  StatusUpdate var4 = (StatusUpdate)x$1;
                  if (this.taskId() == var4.taskId()) {
                     label55: {
                        Enumeration.Value var10000 = this.state();
                        Enumeration.Value var5 = var4.state();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label55;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label55;
                        }

                        ByteBuffer var7 = this.serializedData();
                        ByteBuffer var6 = var4.serializedData();
                        if (var7 == null) {
                           if (var6 != null) {
                              break label55;
                           }
                        } else if (!var7.equals(var6)) {
                           break label55;
                        }

                        if (var4.canEqual(this)) {
                           var9 = true;
                           break label46;
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label64;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public StatusUpdate(long taskId, Enumeration.Value state, ByteBuffer serializedData) {
      this.taskId = taskId;
      this.state = state;
      this.serializedData = serializedData;
      Product$class.$init$(this);
   }
}
