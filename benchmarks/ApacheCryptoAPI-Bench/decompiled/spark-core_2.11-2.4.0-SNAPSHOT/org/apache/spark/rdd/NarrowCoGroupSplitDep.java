package org.apache.spark.rdd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.spark.Partition;
import org.apache.spark.util.Utils$;
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
   bytes = "\u0006\u0001\u0005Eg!B\u0001\u0003\u0001\u0012Q!!\u0006(beJ|woQ8He>,\bo\u00159mSR$U\r\u001d\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0003\u0002\u0001\f#Q\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\u0007\u0013\u0013\t\u0019RB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0005\u0002\r+%\u0011a#\u0004\u0002\b!J|G-^2u\u0011!\u0019\u0001A!f\u0001\n\u0003I2\u0001A\u000b\u00025A\u00121$\t\t\u00049uyR\"\u0001\u0002\n\u0005y\u0011!a\u0001*E\tB\u0011\u0001%\t\u0007\u0001\t%\u00113%!A\u0001\u0002\u000b\u0005\u0011FA\u0002`IEB\u0001\u0002\n\u0001\u0003\u0012\u0003\u0006I!J\u0001\u0005e\u0012$\u0007\u0005\r\u0002'QA\u0019A$H\u0014\u0011\u0005\u0001BC!\u0003\u0012$\u0003\u0003\u0005\tQ!\u0001*#\tQS\u0006\u0005\u0002\rW%\u0011A&\u0004\u0002\b\u001d>$\b.\u001b8h!\taa&\u0003\u00020\u001b\t\u0019\u0011I\\=)\u0005\r\n\u0004C\u0001\u00073\u0013\t\u0019TBA\u0005ue\u0006t7/[3oi\"AQ\u0007\u0001BK\u0002\u0013\u0005a'\u0001\u0006ta2LG/\u00138eKb,\u0012a\u000e\t\u0003\u0019aJ!!O\u0007\u0003\u0007%sG\u000f\u0003\u0005<\u0001\tE\t\u0015!\u00038\u0003-\u0019\b\u000f\\5u\u0013:$W\r\u001f\u0011)\u0005i\n\u0004\u0002\u0003 \u0001\u0005#\u0007I\u0011A \u0002\u000bM\u0004H.\u001b;\u0016\u0003\u0001\u0003\"!\u0011\"\u000e\u0003\u0011I!a\u0011\u0003\u0003\u0013A\u000b'\u000f^5uS>t\u0007\u0002C#\u0001\u0005\u0003\u0007I\u0011\u0001$\u0002\u0013M\u0004H.\u001b;`I\u0015\fHCA$K!\ta\u0001*\u0003\u0002J\u001b\t!QK\\5u\u0011\u001dYE)!AA\u0002\u0001\u000b1\u0001\u001f\u00132\u0011!i\u0005A!E!B\u0013\u0001\u0015AB:qY&$\b\u0005C\u0003P\u0001\u0011\u0005\u0001+\u0001\u0004=S:LGO\u0010\u000b\u0005#J;\u0006\f\u0005\u0002\u001d\u0001!)1A\u0014a\u0001'B\u0012AK\u0016\t\u00049u)\u0006C\u0001\u0011W\t%\u0011#+!A\u0001\u0002\u000b\u0005\u0011\u0006C\u00036\u001d\u0002\u0007q\u0007C\u0003?\u001d\u0002\u0007\u0001\tC\u0003[\u0001\u0011%1,A\u0006xe&$Xm\u00142kK\u000e$HCA$]\u0011\u0015i\u0016\f1\u0001_\u0003\rywn\u001d\t\u0003?\u0012l\u0011\u0001\u0019\u0006\u0003C\n\f!![8\u000b\u0003\r\fAA[1wC&\u0011Q\r\u0019\u0002\u0013\u001f\nTWm\u0019;PkR\u0004X\u000f^*ue\u0016\fW\u000eK\u0002ZO6\u00042\u0001\u00045k\u0013\tIWB\u0001\u0004uQJ|wo\u001d\t\u0003?.L!\u0001\u001c1\u0003\u0017%{U\t_2faRLwN\\\u0012\u0002U\"9q\u000eAA\u0001\n\u0003\u0001\u0018\u0001B2paf$B!U9sg\"91A\u001cI\u0001\u0002\u0004\u0019\u0006bB\u001bo!\u0003\u0005\ra\u000e\u0005\b}9\u0004\n\u00111\u0001A\u0011\u001d)\b!%A\u0005\u0002Y\fabY8qs\u0012\"WMZ1vYR$\u0013'F\u0001xa\tA(\u0010E\u0002\u001d;e\u0004\"\u0001\t>\u0005\u0013\t\"\u0018\u0011!A\u0001\u0006\u0003I\u0003b\u0002?\u0001#\u0003%\t!`\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005q(FA\u001c\u0000W\t\t\t\u0001\u0005\u0003\u0002\u0004\u00055QBAA\u0003\u0015\u0011\t9!!\u0003\u0002\u0013Ut7\r[3dW\u0016$'bAA\u0006\u001b\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005=\u0011Q\u0001\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007\"CA\n\u0001E\u0005I\u0011AA\u000b\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\"!a\u0006+\u0005\u0001{\b\"CA\u000e\u0001\u0005\u0005I\u0011IA\u000f\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\u0011\u0011q\u0004\t\u0005\u0003C\t9#\u0004\u0002\u0002$)\u0019\u0011Q\u00052\u0002\t1\fgnZ\u0005\u0005\u0003S\t\u0019C\u0001\u0004TiJLgn\u001a\u0005\t\u0003[\u0001\u0011\u0011!C\u0001m\u0005a\u0001O]8ek\u000e$\u0018I]5us\"I\u0011\u0011\u0007\u0001\u0002\u0002\u0013\u0005\u00111G\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\ri\u0013Q\u0007\u0005\t\u0017\u0006=\u0012\u0011!a\u0001o!I\u0011\u0011\b\u0001\u0002\u0002\u0013\u0005\u00131H\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u0011Q\b\t\u0006\u0003\u007f\t)%L\u0007\u0003\u0003\u0003R1!a\u0011\u000e\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0003\u000f\n\tE\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011%\tY\u0005AA\u0001\n\u0003\ti%\u0001\u0005dC:,\u0015/^1m)\u0011\ty%!\u0016\u0011\u00071\t\t&C\u0002\u0002T5\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005L\u0003\u0013\n\t\u00111\u0001.\u0011%\tI\u0006AA\u0001\n\u0003\nY&\u0001\u0005iCND7i\u001c3f)\u00059\u0004\"CA0\u0001\u0005\u0005I\u0011IA1\u0003!!xn\u0015;sS:<GCAA\u0010\u0011%\t)\u0007AA\u0001\n\u0003\n9'\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003\u001f\nI\u0007\u0003\u0005L\u0003G\n\t\u00111\u0001.\u000f)\tiGAA\u0001\u0012\u0003!\u0011qN\u0001\u0016\u001d\u0006\u0014(o\\<D_\u001e\u0013x.\u001e9Ta2LG\u000fR3q!\ra\u0012\u0011\u000f\u0004\n\u0003\t\t\t\u0011#\u0001\u0005\u0003g\u001aR!!\u001d\u0002vE\u0001\u0012\"a\u001e\u0002~\u0005\u0005u\u0007Q)\u000e\u0005\u0005e$bAA>\u001b\u00059!/\u001e8uS6,\u0017\u0002BA@\u0003s\u0012\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c84a\u0011\t\u0019)a\"\u0011\tqi\u0012Q\u0011\t\u0004A\u0005\u001dEA\u0003\u0012\u0002r\u0005\u0005\t\u0011!B\u0001S!9q*!\u001d\u0005\u0002\u0005-ECAA8\u0011)\ty&!\u001d\u0002\u0002\u0013\u0015\u0013\u0011\r\u0005\u000b\u0003#\u000b\t(!A\u0005\u0002\u0006M\u0015!B1qa2LHcB)\u0002\u0016\u0006\u0005\u0016Q\u0015\u0005\b\u0007\u0005=\u0005\u0019AALa\u0011\tI*!(\u0011\tqi\u00121\u0014\t\u0004A\u0005uEA\u0003\u0012\u0002\u0016\u0006\u0005\t\u0011!B\u0001S!\u001a\u0011QS\u0019\t\rU\ny\t1\u00018Q\r\t\t+\r\u0005\u0007}\u0005=\u0005\u0019\u0001!\t\u0015\u0005%\u0016\u0011OA\u0001\n\u0003\u000bY+A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u00055\u0016\u0011\u0019\u0019\u0005\u0003_\u000by\fE\u0003\r\u0003c\u000b),C\u0002\u000246\u0011aa\u00149uS>t\u0007c\u0002\u0007\u00028\u0006mv\u0007Q\u0005\u0004\u0003sk!A\u0002+va2,7\u0007\u0005\u0003\u001d;\u0005u\u0006c\u0001\u0011\u0002@\u0012Q!%a*\u0002\u0002\u0003\u0005)\u0011A\u0015\t\u0013\u0005\r\u0017qUA\u0001\u0002\u0004\t\u0016a\u0001=%a!Q\u0011qYA9\u0003\u0003%I!!3\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u0017\u0004B!!\t\u0002N&!\u0011qZA\u0012\u0005\u0019y%M[3di\u0002"
)
public class NarrowCoGroupSplitDep implements Serializable, Product {
   private final transient RDD<?> rdd;
   private final transient int splitIndex;
   private Partition split;

   public static Option<Tuple3<RDD<Object>, Object, Partition>> unapply(NarrowCoGroupSplitDep var0) {
      return NarrowCoGroupSplitDep$.MODULE$.unapply(var0);
   }

   public static NarrowCoGroupSplitDep apply(RDD<?> var0, int var1, Partition var2) {
      return NarrowCoGroupSplitDep$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<RDD<?>, Object, Partition>, NarrowCoGroupSplitDep> tupled() {
      return NarrowCoGroupSplitDep$.MODULE$.tupled();
   }

   public static Function1<RDD<?>, Function1<Object, Function1<Partition, NarrowCoGroupSplitDep>>> curried() {
      return NarrowCoGroupSplitDep$.MODULE$.curried();
   }

   public RDD<?> rdd() {
      return this.rdd;
   }

   public int splitIndex() {
      return this.splitIndex;
   }

   public Partition split() {
      return this.split;
   }

   public void split_$eq(Partition x$1) {
      this.split = x$1;
   }

   private void writeObject(ObjectOutputStream oos) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, oos) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final NarrowCoGroupSplitDep $outer;
         private final ObjectOutputStream oos$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.split_$eq(this.$outer.rdd().partitions()[this.$outer.splitIndex()]);
            this.oos$1.defaultWriteObject();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.oos$1 = oos$1;
            }
         }
      });
   }

   public NarrowCoGroupSplitDep copy(RDD<?> rdd, int splitIndex, Partition split) {
      return new NarrowCoGroupSplitDep(rdd, splitIndex, split);
   }

   public RDD<?> copy$default$1() {
      return this.rdd();
   }

   public int copy$default$2() {
      return this.splitIndex();
   }

   public Partition copy$default$3() {
      return this.split();
   }

   public String productPrefix() {
      return "NarrowCoGroupSplitDep";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.rdd();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.splitIndex());
            break;
         case 2:
            var10000 = this.split();
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
      return x$1 instanceof NarrowCoGroupSplitDep;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.rdd()));
      var1 = Statics.mix(var1, this.splitIndex());
      var1 = Statics.mix(var1, Statics.anyHash(this.split()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label63: {
            boolean var3;
            if (x$1 instanceof NarrowCoGroupSplitDep) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label46: {
                  label45: {
                     NarrowCoGroupSplitDep var4 = (NarrowCoGroupSplitDep)x$1;
                     RDD var10000 = this.rdd();
                     RDD var5 = var4.rdd();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label45;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label45;
                     }

                     if (this.splitIndex() == var4.splitIndex()) {
                        label39: {
                           Partition var7 = this.split();
                           Partition var6 = var4.split();
                           if (var7 == null) {
                              if (var6 != null) {
                                 break label39;
                              }
                           } else if (!var7.equals(var6)) {
                              break label39;
                           }

                           if (var4.canEqual(this)) {
                              var9 = true;
                              break label46;
                           }
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label63;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public NarrowCoGroupSplitDep(RDD<?> rdd, int splitIndex, Partition split) {
      this.rdd = rdd;
      this.splitIndex = splitIndex;
      this.split = split;
      super();
      Product$class.$init$(this);
   }
}
