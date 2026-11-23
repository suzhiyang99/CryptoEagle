package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.storage.BlockManagerId;
import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple5;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005]e\u0001B\u0001\u0003\u0001&\u00111BR3uG\"4\u0015-\u001b7fI*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014xm\u0001\u0001\u0014\u000b\u0001Q\u0001\u0003F\f\u0011\u0005-qQ\"\u0001\u0007\u000b\u00035\tQa]2bY\u0006L!a\u0004\u0007\u0003\r\u0005s\u0017PU3g!\t\t\"#D\u0001\u0003\u0013\t\u0019\"A\u0001\tUCN\\g)Y5mK\u0012\u0014V-Y:p]B\u00111\"F\u0005\u0003-1\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\f1%\u0011\u0011\u0004\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t7\u0001\u0011)\u001a!C\u00019\u0005I!-\\!eIJ,7o]\u000b\u0002;A\u0011a$I\u0007\u0002?)\u0011\u0001EA\u0001\bgR|'/Y4f\u0013\t\u0011sD\u0001\bCY>\u001c7.T1oC\u001e,'/\u00133\t\u0011\u0011\u0002!\u0011#Q\u0001\nu\t!BY7BI\u0012\u0014Xm]:!\u0011!1\u0003A!f\u0001\n\u00039\u0013!C:ik\u001a4G.Z%e+\u0005A\u0003CA\u0006*\u0013\tQCBA\u0002J]RD\u0001\u0002\f\u0001\u0003\u0012\u0003\u0006I\u0001K\u0001\u000bg\",hM\u001a7f\u0013\u0012\u0004\u0003\u0002\u0003\u0018\u0001\u0005+\u0007I\u0011A\u0014\u0002\u000b5\f\u0007/\u00133\t\u0011A\u0002!\u0011#Q\u0001\n!\na!\\1q\u0013\u0012\u0004\u0003\u0002\u0003\u001a\u0001\u0005+\u0007I\u0011A\u0014\u0002\u0011I,G-^2f\u0013\u0012D\u0001\u0002\u000e\u0001\u0003\u0012\u0003\u0006I\u0001K\u0001\ne\u0016$WoY3JI\u0002B\u0001B\u000e\u0001\u0003\u0016\u0004%\taN\u0001\b[\u0016\u001c8/Y4f+\u0005A\u0004CA\u001d=\u001d\tY!(\u0003\u0002<\u0019\u00051\u0001K]3eK\u001aL!!\u0010 \u0003\rM#(/\u001b8h\u0015\tYD\u0002\u0003\u0005A\u0001\tE\t\u0015!\u00039\u0003!iWm]:bO\u0016\u0004\u0003\"\u0002\"\u0001\t\u0003\u0019\u0015A\u0002\u001fj]&$h\b\u0006\u0004E\u000b\u001a;\u0005*\u0013\t\u0003#\u0001AQaG!A\u0002uAQAJ!A\u0002!BQAL!A\u0002!BQAM!A\u0002!BQAN!A\u0002aBQa\u0013\u0001\u0005B]\nQ\u0002^8FeJ|'o\u0015;sS:<\u0007\"B'\u0001\t\u0003r\u0015\u0001G2pk:$Hk\\<be\u0012\u001cH+Y:l\r\u0006LG.\u001e:fgV\tq\n\u0005\u0002\f!&\u0011\u0011\u000b\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d\u0019\u0006!!A\u0005\u0002Q\u000bAaY8qsR1A)\u0016,X1fCqa\u0007*\u0011\u0002\u0003\u0007Q\u0004C\u0004'%B\u0005\t\u0019\u0001\u0015\t\u000f9\u0012\u0006\u0013!a\u0001Q!9!G\u0015I\u0001\u0002\u0004A\u0003b\u0002\u001cS!\u0003\u0005\r\u0001\u000f\u0005\b7\u0002\t\n\u0011\"\u0001]\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012!\u0018\u0016\u0003;y[\u0013a\u0018\t\u0003A\u0016l\u0011!\u0019\u0006\u0003E\u000e\f\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005\u0011d\u0011AC1o]>$\u0018\r^5p]&\u0011a-\u0019\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u00025\u0001#\u0003%\t![\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005Q'F\u0001\u0015_\u0011\u001da\u0007!%A\u0005\u0002%\fabY8qs\u0012\"WMZ1vYR$3\u0007C\u0004o\u0001E\u0005I\u0011A5\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%i!9\u0001\u000fAI\u0001\n\u0003\t\u0018AD2paf$C-\u001a4bk2$H%N\u000b\u0002e*\u0012\u0001H\u0018\u0005\bi\u0002\t\t\u0011\"\u0011v\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\ta\u000f\u0005\u0002xy6\t\u0001P\u0003\u0002zu\u0006!A.\u00198h\u0015\u0005Y\u0018\u0001\u00026bm\u0006L!!\u0010=\t\u000fy\u0004\u0011\u0011!C\u0001O\u0005a\u0001O]8ek\u000e$\u0018I]5us\"I\u0011\u0011\u0001\u0001\u0002\u0002\u0013\u0005\u00111A\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\u0011\t)!a\u0003\u0011\u0007-\t9!C\u0002\u0002\n1\u00111!\u00118z\u0011!\tia`A\u0001\u0002\u0004A\u0013a\u0001=%c!I\u0011\u0011\u0003\u0001\u0002\u0002\u0013\u0005\u00131C\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u0011Q\u0003\t\u0007\u0003/\ti\"!\u0002\u000e\u0005\u0005e!bAA\u000e\u0019\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\t\u0005}\u0011\u0011\u0004\u0002\t\u0013R,'/\u0019;pe\"I\u00111\u0005\u0001\u0002\u0002\u0013\u0005\u0011QE\u0001\tG\u0006tW)];bYR\u0019q*a\n\t\u0015\u00055\u0011\u0011EA\u0001\u0002\u0004\t)\u0001C\u0005\u0002,\u0001\t\t\u0011\"\u0011\u0002.\u0005A\u0001.Y:i\u0007>$W\rF\u0001)\u0011%\t\t\u0004AA\u0001\n\u0003\n\u0019$\u0001\u0005u_N#(/\u001b8h)\u00051\b\"CA\u001c\u0001\u0005\u0005I\u0011IA\u001d\u0003\u0019)\u0017/^1mgR\u0019q*a\u000f\t\u0015\u00055\u0011QGA\u0001\u0002\u0004\t)\u0001K\u0002\u0001\u0003\u007f\u0001B!!\u0011\u0002F5\u0011\u00111\t\u0006\u0003I\nIA!a\u0012\u0002D\taA)\u001a<fY>\u0004XM]!qS\u001eI\u00111\n\u0002\u0002\u0002#\u0005\u0011QJ\u0001\f\r\u0016$8\r\u001b$bS2,G\rE\u0002\u0012\u0003\u001f2\u0001\"\u0001\u0002\u0002\u0002#\u0005\u0011\u0011K\n\u0006\u0003\u001f\n\u0019f\u0006\t\u000b\u0003+\nY&\b\u0015)Qa\"UBAA,\u0015\r\tI\u0006D\u0001\beVtG/[7f\u0013\u0011\ti&a\u0016\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>tW\u0007C\u0004C\u0003\u001f\"\t!!\u0019\u0015\u0005\u00055\u0003BCA\u0019\u0003\u001f\n\t\u0011\"\u0012\u00024!Q\u0011qMA(\u0003\u0003%\t)!\u001b\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0017\u0011\u000bY'!\u001c\u0002p\u0005E\u00141\u000f\u0005\u00077\u0005\u0015\u0004\u0019A\u000f\t\r\u0019\n)\u00071\u0001)\u0011\u0019q\u0013Q\ra\u0001Q!1!'!\u001aA\u0002!BaANA3\u0001\u0004A\u0004BCA<\u0003\u001f\n\t\u0011\"!\u0002z\u00059QO\\1qa2LH\u0003BA>\u0003\u000f\u0003RaCA?\u0003\u0003K1!a \r\u0005\u0019y\u0005\u000f^5p]BA1\"a!\u001eQ!B\u0003(C\u0002\u0002\u00062\u0011a\u0001V;qY\u0016,\u0004\"CAE\u0003k\n\t\u00111\u0001E\u0003\rAH\u0005\r\u0005\u000b\u0003\u001b\u000by%!A\u0005\n\u0005=\u0015a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!%\u0011\u0007]\f\u0019*C\u0002\u0002\u0016b\u0014aa\u00142kK\u000e$\b"
)
public class FetchFailed implements TaskFailedReason, Product, Serializable {
   private final BlockManagerId bmAddress;
   private final int shuffleId;
   private final int mapId;
   private final int reduceId;
   private final String message;

   public static Option<Tuple5<BlockManagerId, Object, Object, Object, String>> unapply(FetchFailed var0) {
      return FetchFailed$.MODULE$.unapply(var0);
   }

   public static FetchFailed apply(BlockManagerId var0, int var1, int var2, int var3, String var4) {
      return FetchFailed$.MODULE$.apply(var0, var1, var2, var3, var4);
   }

   public static Function1<Tuple5<BlockManagerId, Object, Object, Object, String>, FetchFailed> tupled() {
      return FetchFailed$.MODULE$.tupled();
   }

   public static Function1<BlockManagerId, Function1<Object, Function1<Object, Function1<Object, Function1<String, FetchFailed>>>>> curried() {
      return FetchFailed$.MODULE$.curried();
   }

   public BlockManagerId bmAddress() {
      return this.bmAddress;
   }

   public int shuffleId() {
      return this.shuffleId;
   }

   public int mapId() {
      return this.mapId;
   }

   public int reduceId() {
      return this.reduceId;
   }

   public String message() {
      return this.message;
   }

   public String toErrorString() {
      String bmAddressString = this.bmAddress() == null ? "null" : this.bmAddress().toString();
      return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"FetchFailed(", ", shuffleId=", ", mapId=", ", reduceId=", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{bmAddressString, BoxesRunTime.boxToInteger(this.shuffleId()), BoxesRunTime.boxToInteger(this.mapId()), BoxesRunTime.boxToInteger(this.reduceId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"message=\\n", "\\n)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.message()}))).toString();
   }

   public boolean countTowardsTaskFailures() {
      return false;
   }

   public FetchFailed copy(BlockManagerId bmAddress, int shuffleId, int mapId, int reduceId, String message) {
      return new FetchFailed(bmAddress, shuffleId, mapId, reduceId, message);
   }

   public BlockManagerId copy$default$1() {
      return this.bmAddress();
   }

   public int copy$default$2() {
      return this.shuffleId();
   }

   public int copy$default$3() {
      return this.mapId();
   }

   public int copy$default$4() {
      return this.reduceId();
   }

   public String copy$default$5() {
      return this.message();
   }

   public String productPrefix() {
      return "FetchFailed";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.bmAddress();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.shuffleId());
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.mapId());
            break;
         case 3:
            var10000 = BoxesRunTime.boxToInteger(this.reduceId());
            break;
         case 4:
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
      return x$1 instanceof FetchFailed;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.bmAddress()));
      var1 = Statics.mix(var1, this.shuffleId());
      var1 = Statics.mix(var1, this.mapId());
      var1 = Statics.mix(var1, this.reduceId());
      var1 = Statics.mix(var1, Statics.anyHash(this.message()));
      return Statics.finalizeHash(var1, 5);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label67: {
            boolean var3;
            if (x$1 instanceof FetchFailed) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label50: {
                  label49: {
                     FetchFailed var4 = (FetchFailed)x$1;
                     BlockManagerId var10000 = this.bmAddress();
                     BlockManagerId var5 = var4.bmAddress();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label49;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label49;
                     }

                     if (this.shuffleId() == var4.shuffleId() && this.mapId() == var4.mapId() && this.reduceId() == var4.reduceId()) {
                        label41: {
                           String var7 = this.message();
                           String var6 = var4.message();
                           if (var7 == null) {
                              if (var6 != null) {
                                 break label41;
                              }
                           } else if (!var7.equals(var6)) {
                              break label41;
                           }

                           if (var4.canEqual(this)) {
                              var9 = true;
                              break label50;
                           }
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label67;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public FetchFailed(BlockManagerId bmAddress, int shuffleId, int mapId, int reduceId, String message) {
      this.bmAddress = bmAddress;
      this.shuffleId = shuffleId;
      this.mapId = mapId;
      this.reduceId = reduceId;
      this.message = message;
      TaskFailedReason$class.$init$(this);
      Product$class.$init$(this);
   }
}
