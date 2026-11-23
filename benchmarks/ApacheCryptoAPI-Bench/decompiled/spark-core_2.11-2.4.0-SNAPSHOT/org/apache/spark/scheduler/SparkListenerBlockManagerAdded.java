package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.storage.BlockManagerId;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple5;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005}e\u0001B\u0001\u0003\u0001.\u0011ad\u00159be.d\u0015n\u001d;f]\u0016\u0014(\t\\8dW6\u000bg.Y4fe\u0006#G-\u001a3\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0006\u00011\u0011b#\u0007\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M!R\"\u0001\u0002\n\u0005U\u0011!AE*qCJ\\G*[:uK:,'/\u0012<f]R\u0004\"!D\f\n\u0005aq!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001biI!a\u0007\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011u\u0001!Q3A\u0005\u0002y\tA\u0001^5nKV\tq\u0004\u0005\u0002\u000eA%\u0011\u0011E\u0004\u0002\u0005\u0019>tw\r\u0003\u0005$\u0001\tE\t\u0015!\u0003 \u0003\u0015!\u0018.\\3!\u0011!)\u0003A!f\u0001\n\u00031\u0013A\u00042m_\u000e\\W*\u00198bO\u0016\u0014\u0018\nZ\u000b\u0002OA\u0011\u0001fK\u0007\u0002S)\u0011!\u0006B\u0001\bgR|'/Y4f\u0013\ta\u0013F\u0001\bCY>\u001c7.T1oC\u001e,'/\u00133\t\u00119\u0002!\u0011#Q\u0001\n\u001d\nqB\u00197pG.l\u0015M\\1hKJLE\r\t\u0005\ta\u0001\u0011)\u001a!C\u0001=\u00051Q.\u0019=NK6D\u0001B\r\u0001\u0003\u0012\u0003\u0006IaH\u0001\b[\u0006DX*Z7!\u0011!!\u0004A!f\u0001\n\u0003)\u0014\u0001D7bq>s\u0007*Z1q\u001b\u0016lW#\u0001\u001c\u0011\u000759t$\u0003\u00029\u001d\t1q\n\u001d;j_:D\u0001B\u000f\u0001\u0003\u0012\u0003\u0006IAN\u0001\u000e[\u0006DxJ\u001c%fCBlU-\u001c\u0011\t\u0011q\u0002!Q3A\u0005\u0002U\nQ\"\\1y\u001f\u001a4\u0007*Z1q\u001b\u0016l\u0007\u0002\u0003 \u0001\u0005#\u0005\u000b\u0011\u0002\u001c\u0002\u001d5\f\u0007p\u00144g\u0011\u0016\f\u0007/T3nA!)\u0001\t\u0001C\u0001\u0003\u00061A(\u001b8jiz\"bAQ\"E\u000b\u001a;\u0005CA\n\u0001\u0011\u0015ir\b1\u0001 \u0011\u0015)s\b1\u0001(\u0011\u0015\u0001t\b1\u0001 \u0011\u001d!t\b%AA\u0002YBq\u0001P \u0011\u0002\u0003\u0007a\u0007C\u0004J\u0001\u0005\u0005I\u0011\u0001&\u0002\t\r|\u0007/\u001f\u000b\u0007\u0005.cUJT(\t\u000fuA\u0005\u0013!a\u0001?!9Q\u0005\u0013I\u0001\u0002\u00049\u0003b\u0002\u0019I!\u0003\u0005\ra\b\u0005\bi!\u0003\n\u00111\u00017\u0011\u001da\u0004\n%AA\u0002YBq!\u0015\u0001\u0012\u0002\u0013\u0005!+\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003MS#a\b+,\u0003U\u0003\"AV.\u000e\u0003]S!\u0001W-\u0002\u0013Ut7\r[3dW\u0016$'B\u0001.\u000f\u0003)\tgN\\8uCRLwN\\\u0005\u00039^\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001dq\u0006!%A\u0005\u0002}\u000babY8qs\u0012\"WMZ1vYR$#'F\u0001aU\t9C\u000bC\u0004c\u0001E\u0005I\u0011\u0001*\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%g!9A\rAI\u0001\n\u0003)\u0017AD2paf$C-\u001a4bk2$H\u0005N\u000b\u0002M*\u0012a\u0007\u0016\u0005\bQ\u0002\t\n\u0011\"\u0001f\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIUBqA\u001b\u0001\u0002\u0002\u0013\u00053.A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002YB\u0011QN]\u0007\u0002]*\u0011q\u000e]\u0001\u0005Y\u0006twMC\u0001r\u0003\u0011Q\u0017M^1\n\u0005Mt'AB*ue&tw\rC\u0004v\u0001\u0005\u0005I\u0011\u0001<\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003]\u0004\"!\u0004=\n\u0005et!aA%oi\"91\u0010AA\u0001\n\u0003a\u0018A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0004{\u0006\u0005\u0001CA\u0007\u007f\u0013\tyhBA\u0002B]fD\u0001\"a\u0001{\u0003\u0003\u0005\ra^\u0001\u0004q\u0012\n\u0004\"CA\u0004\u0001\u0005\u0005I\u0011IA\u0005\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014XCAA\u0006!\u0015\ti!a\u0005~\u001b\t\tyAC\u0002\u0002\u00129\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\t)\"a\u0004\u0003\u0011%#XM]1u_JD\u0011\"!\u0007\u0001\u0003\u0003%\t!a\u0007\u0002\u0011\r\fg.R9vC2$B!!\b\u0002$A\u0019Q\"a\b\n\u0007\u0005\u0005bBA\u0004C_>dW-\u00198\t\u0013\u0005\r\u0011qCA\u0001\u0002\u0004i\b\"CA\u0014\u0001\u0005\u0005I\u0011IA\u0015\u0003!A\u0017m\u001d5D_\u0012,G#A<\t\u0013\u00055\u0002!!A\u0005B\u0005=\u0012\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u00031D\u0011\"a\r\u0001\u0003\u0003%\t%!\u000e\u0002\r\u0015\fX/\u00197t)\u0011\ti\"a\u000e\t\u0013\u0005\r\u0011\u0011GA\u0001\u0002\u0004i\bf\u0001\u0001\u0002<A!\u0011QHA!\u001b\t\tyD\u0003\u0002[\t%!\u00111IA \u00051!UM^3m_B,'/\u00119j\u000f%\t9EAA\u0001\u0012\u0003\tI%\u0001\u0010Ta\u0006\u00148\u000eT5ti\u0016tWM\u001d\"m_\u000e\\W*\u00198bO\u0016\u0014\u0018\t\u001a3fIB\u00191#a\u0013\u0007\u0011\u0005\u0011\u0011\u0011!E\u0001\u0003\u001b\u001aR!a\u0013\u0002Pe\u0001\"\"!\u0015\u0002X}9sD\u000e\u001cC\u001b\t\t\u0019FC\u0002\u0002V9\tqA];oi&lW-\u0003\u0003\u0002Z\u0005M#!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8ok!9\u0001)a\u0013\u0005\u0002\u0005uCCAA%\u0011)\ti#a\u0013\u0002\u0002\u0013\u0015\u0013q\u0006\u0005\u000b\u0003G\nY%!A\u0005\u0002\u0006\u0015\u0014!B1qa2LHc\u0003\"\u0002h\u0005%\u00141NA7\u0003_Ba!HA1\u0001\u0004y\u0002BB\u0013\u0002b\u0001\u0007q\u0005\u0003\u00041\u0003C\u0002\ra\b\u0005\ti\u0005\u0005\u0004\u0013!a\u0001m!AA(!\u0019\u0011\u0002\u0003\u0007a\u0007\u0003\u0006\u0002t\u0005-\u0013\u0011!CA\u0003k\nq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002x\u0005}\u0004\u0003B\u00078\u0003s\u0002\u0002\"DA>?\u001dzbGN\u0005\u0004\u0003{r!A\u0002+va2,W\u0007C\u0005\u0002\u0002\u0006E\u0014\u0011!a\u0001\u0005\u0006\u0019\u0001\u0010\n\u0019\t\u0013\u0005\u0015\u00151JI\u0001\n\u0003)\u0017a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$C\u0007C\u0005\u0002\n\u0006-\u0013\u0013!C\u0001K\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIUB\u0011\"!$\u0002LE\u0005I\u0011A3\u0002\u001f\u0005\u0004\b\u000f\\=%I\u00164\u0017-\u001e7uIQB\u0011\"!%\u0002LE\u0005I\u0011A3\u0002\u001f\u0005\u0004\b\u000f\\=%I\u00164\u0017-\u001e7uIUB!\"!&\u0002L\u0005\u0005I\u0011BAL\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005e\u0005cA7\u0002\u001c&\u0019\u0011Q\u00148\u0003\r=\u0013'.Z2u\u0001"
)
public class SparkListenerBlockManagerAdded implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final BlockManagerId blockManagerId;
   private final long maxMem;
   private final Option<Object> maxOnHeapMem;
   private final Option<Object> maxOffHeapMem;

   public static Option<Object> apply$default$5() {
      return SparkListenerBlockManagerAdded$.MODULE$.apply$default$5();
   }

   public static Option<Object> apply$default$4() {
      return SparkListenerBlockManagerAdded$.MODULE$.apply$default$4();
   }

   public static Option<Object> $lessinit$greater$default$5() {
      return SparkListenerBlockManagerAdded$.MODULE$.$lessinit$greater$default$5();
   }

   public static Option<Object> $lessinit$greater$default$4() {
      return SparkListenerBlockManagerAdded$.MODULE$.$lessinit$greater$default$4();
   }

   public static Option<Tuple5<Object, BlockManagerId, Object, Option<Object>, Option<Object>>> unapply(SparkListenerBlockManagerAdded var0) {
      return SparkListenerBlockManagerAdded$.MODULE$.unapply(var0);
   }

   public static SparkListenerBlockManagerAdded apply(long var0, BlockManagerId var2, long var3, Option<Object> var5, Option<Object> var6) {
      return SparkListenerBlockManagerAdded$.MODULE$.apply(var0, var2, var3, var5, var6);
   }

   public static Function1<Tuple5<Object, BlockManagerId, Object, Option<Object>, Option<Object>>, SparkListenerBlockManagerAdded> tupled() {
      return SparkListenerBlockManagerAdded$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<BlockManagerId, Function1<Object, Function1<Option<Object>, Function1<Option<Object>, SparkListenerBlockManagerAdded>>>>> curried() {
      return SparkListenerBlockManagerAdded$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public long time() {
      return this.time;
   }

   public BlockManagerId blockManagerId() {
      return this.blockManagerId;
   }

   public long maxMem() {
      return this.maxMem;
   }

   public Option<Object> maxOnHeapMem() {
      return this.maxOnHeapMem;
   }

   public Option<Object> maxOffHeapMem() {
      return this.maxOffHeapMem;
   }

   public SparkListenerBlockManagerAdded copy(long time, BlockManagerId blockManagerId, long maxMem, Option<Object> maxOnHeapMem, Option<Object> maxOffHeapMem) {
      return new SparkListenerBlockManagerAdded(time, blockManagerId, maxMem, maxOnHeapMem, maxOffHeapMem);
   }

   public long copy$default$1() {
      return this.time();
   }

   public BlockManagerId copy$default$2() {
      return this.blockManagerId();
   }

   public long copy$default$3() {
      return this.maxMem();
   }

   public Option<Object> copy$default$4() {
      return this.maxOnHeapMem();
   }

   public Option<Object> copy$default$5() {
      return this.maxOffHeapMem();
   }

   public String productPrefix() {
      return "SparkListenerBlockManagerAdded";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.time());
            break;
         case 1:
            var10000 = this.blockManagerId();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToLong(this.maxMem());
            break;
         case 3:
            var10000 = this.maxOnHeapMem();
            break;
         case 4:
            var10000 = this.maxOffHeapMem();
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
      return x$1 instanceof SparkListenerBlockManagerAdded;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.blockManagerId()));
      var1 = Statics.mix(var1, Statics.longHash(this.maxMem()));
      var1 = Statics.mix(var1, Statics.anyHash(this.maxOnHeapMem()));
      var1 = Statics.mix(var1, Statics.anyHash(this.maxOffHeapMem()));
      return Statics.finalizeHash(var1, 5);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label75: {
            boolean var3;
            if (x$1 instanceof SparkListenerBlockManagerAdded) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label57: {
                  SparkListenerBlockManagerAdded var4 = (SparkListenerBlockManagerAdded)x$1;
                  if (this.time() == var4.time()) {
                     label55: {
                        BlockManagerId var10000 = this.blockManagerId();
                        BlockManagerId var5 = var4.blockManagerId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label55;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label55;
                        }

                        if (this.maxMem() == var4.maxMem()) {
                           label66: {
                              Option var8 = this.maxOnHeapMem();
                              Option var6 = var4.maxOnHeapMem();
                              if (var8 == null) {
                                 if (var6 != null) {
                                    break label66;
                                 }
                              } else if (!var8.equals(var6)) {
                                 break label66;
                              }

                              var8 = this.maxOffHeapMem();
                              Option var7 = var4.maxOffHeapMem();
                              if (var8 == null) {
                                 if (var7 != null) {
                                    break label66;
                                 }
                              } else if (!var8.equals(var7)) {
                                 break label66;
                              }

                              if (var4.canEqual(this)) {
                                 var11 = true;
                                 break label57;
                              }
                           }
                        }
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label75;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public SparkListenerBlockManagerAdded(long time, BlockManagerId blockManagerId, long maxMem, Option<Object> maxOnHeapMem, Option<Object> maxOffHeapMem) {
      this.time = time;
      this.blockManagerId = blockManagerId;
      this.maxMem = maxMem;
      this.maxOnHeapMem = maxOnHeapMem;
      this.maxOffHeapMem = maxOffHeapMem;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
