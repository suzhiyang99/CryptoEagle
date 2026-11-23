package org.apache.spark.rdd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.scheduler.TaskLocation;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Function1;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple4;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\t\u001db!B\u0001\u0003\u0001\u0012Q!!F\"pC2,7oY3e%\u0012#\u0005+\u0019:uSRLwN\u001c\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7#\u0002\u0001\f#UA\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\u0013'5\tA!\u0003\u0002\u0015\t\tI\u0001+\u0019:uSRLwN\u001c\t\u0003\u0019YI!aF\u0007\u0003\u000fA\u0013x\u000eZ;diB\u0011A\"G\u0005\u000355\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\u0002\b\u0001\u0003\u0016\u0004%\tAH\u0001\u0006S:$W\r_\u0002\u0001+\u0005y\u0002C\u0001\u0007!\u0013\t\tSBA\u0002J]RD\u0001b\t\u0001\u0003\u0012\u0003\u0006IaH\u0001\u0007S:$W\r\u001f\u0011\t\u0011\r\u0001!Q3A\u0005\u0002\u0015*\u0012A\n\u0019\u0003O5\u00022\u0001K\u0015,\u001b\u0005\u0011\u0011B\u0001\u0016\u0003\u0005\r\u0011F\t\u0012\t\u0003Y5b\u0001\u0001B\u0005/_\u0005\u0005\t\u0011!B\u0001k\t\u0019q\fJ\u0019\t\u0011A\u0002!\u0011#Q\u0001\nE\nAA\u001d3eAA\u0012!\u0007\u000e\t\u0004Q%\u001a\u0004C\u0001\u00175\t%qs&!A\u0001\u0002\u000b\u0005Q'\u0005\u00027sA\u0011AbN\u0005\u0003q5\u0011qAT8uQ&tw\r\u0005\u0002\ru%\u00111(\u0004\u0002\u0004\u0003:L\bFA\u0018>!\taa(\u0003\u0002@\u001b\tIAO]1og&,g\u000e\u001e\u0005\t\u0003\u0002\u0011)\u001a!C\u0001\u0005\u0006q\u0001/\u0019:f]R\u001c\u0018J\u001c3jG\u0016\u001cX#A\"\u0011\u00071!u$\u0003\u0002F\u001b\t)\u0011I\u001d:bs\"Aq\t\u0001B\tB\u0003%1)A\bqCJ,g\u000e^:J]\u0012L7-Z:!\u0011!I\u0005A!f\u0001\n\u0003Q\u0015!\u00059sK\u001a,'O]3e\u0019>\u001c\u0017\r^5p]V\t1\nE\u0002\r\u0019:K!!T\u0007\u0003\r=\u0003H/[8o!\ty%K\u0004\u0002\r!&\u0011\u0011+D\u0001\u0007!J,G-\u001a4\n\u0005M#&AB*ue&twM\u0003\u0002R\u001b!Aa\u000b\u0001B\tB\u0003%1*\u0001\nqe\u00164WM\u001d:fI2{7-\u0019;j_:\u0004\u0003FA+>\u0011\u0015I\u0006\u0001\"\u0001[\u0003\u0019a\u0014N\\5u}Q)1\fX/cGB\u0011\u0001\u0006\u0001\u0005\u00069a\u0003\ra\b\u0005\u0006\u0007a\u0003\rA\u0018\u0019\u0003?\u0006\u00042\u0001K\u0015a!\ta\u0013\rB\u0005/;\u0006\u0005\t\u0011!B\u0001k!)\u0011\t\u0017a\u0001\u0007\"9\u0011\n\u0017I\u0001\u0002\u0004Y\u0005bB3\u0001\u0001\u0004%\tAZ\u0001\ba\u0006\u0014XM\u001c;t+\u00059\u0007c\u00015q#9\u0011\u0011N\u001c\b\u0003U6l\u0011a\u001b\u0006\u0003Yv\ta\u0001\u0010:p_Rt\u0014\"\u0001\b\n\u0005=l\u0011a\u00029bG.\fw-Z\u0005\u0003cJ\u00141aU3r\u0015\tyW\u0002C\u0004u\u0001\u0001\u0007I\u0011A;\u0002\u0017A\f'/\u001a8ug~#S-\u001d\u000b\u0003mf\u0004\"\u0001D<\n\u0005al!\u0001B+oSRDqA_:\u0002\u0002\u0003\u0007q-A\u0002yIEBa\u0001 \u0001!B\u00139\u0017\u0001\u00039be\u0016tGo\u001d\u0011\t\u000by\u0004A\u0011B@\u0002\u0017]\u0014\u0018\u000e^3PE*,7\r\u001e\u000b\u0004m\u0006\u0005\u0001bBA\u0002{\u0002\u0007\u0011QA\u0001\u0004_>\u001c\b\u0003BA\u0004\u0003#i!!!\u0003\u000b\t\u0005-\u0011QB\u0001\u0003S>T!!a\u0004\u0002\t)\fg/Y\u0005\u0005\u0003'\tIA\u0001\nPE*,7\r^(viB,Ho\u0015;sK\u0006l\u0007&B?\u0002\u0018\u0005\r\u0002#\u0002\u0007\u0002\u001a\u0005u\u0011bAA\u000e\u001b\t1A\u000f\u001b:poN\u0004B!a\u0002\u0002 %!\u0011\u0011EA\u0005\u0005-Iu*\u0012=dKB$\u0018n\u001c8$\u0005\u0005u\u0001bBA\u0014\u0001\u0011\u0005\u0011\u0011F\u0001\u000eY>\u001c\u0017\r\u001c$sC\u000e$\u0018n\u001c8\u0016\u0005\u0005-\u0002c\u0001\u0007\u0002.%\u0019\u0011qF\u0007\u0003\r\u0011{WO\u00197f\u0011%\t\u0019\u0004AA\u0001\n\u0003\t)$\u0001\u0003d_BLH#C.\u00028\u0005e\u00121HA\u001f\u0011!a\u0012\u0011\u0007I\u0001\u0002\u0004y\u0002\u0002C\u0002\u00022A\u0005\t\u0019\u00010\t\u0011\u0005\u000b\t\u0004%AA\u0002\rC\u0001\"SA\u0019!\u0003\u0005\ra\u0013\u0005\n\u0003\u0003\u0002\u0011\u0013!C\u0001\u0003\u0007\nabY8qs\u0012\"WMZ1vYR$\u0013'\u0006\u0002\u0002F)\u001aq$a\u0012,\u0005\u0005%\u0003\u0003BA&\u0003+j!!!\u0014\u000b\t\u0005=\u0013\u0011K\u0001\nk:\u001c\u0007.Z2lK\u0012T1!a\u0015\u000e\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003/\niEA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016D\u0011\"a\u0017\u0001#\u0003%\t!!\u0018\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%eU\u0011\u0011q\f\u0019\u0005\u0003C\n)\u0007\u0005\u0003)S\u0005\r\u0004c\u0001\u0017\u0002f\u0011Qa&!\u0017\u0002\u0002\u0003\u0005)\u0011A\u001b\t\u0013\u0005%\u0004!%A\u0005\u0002\u0005-\u0014AD2paf$C-\u001a4bk2$HeM\u000b\u0003\u0003[R3aQA$\u0011%\t\t\bAI\u0001\n\u0003\t\u0019(\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001b\u0016\u0005\u0005U$fA&\u0002H!I\u0011\u0011\u0010\u0001\u0002\u0002\u0013\u0005\u00131P\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0005\u0005u\u0004\u0003BA@\u0003\u000bk!!!!\u000b\t\u0005\r\u0015QB\u0001\u0005Y\u0006tw-C\u0002T\u0003\u0003C\u0001\"!#\u0001\u0003\u0003%\tAH\u0001\raJ|G-^2u\u0003JLG/\u001f\u0005\n\u0003\u001b\u0003\u0011\u0011!C\u0001\u0003\u001f\u000ba\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000fF\u0002:\u0003#C\u0001B_AF\u0003\u0003\u0005\ra\b\u0005\n\u0003+\u0003\u0011\u0011!C!\u0003/\u000bq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u00033\u0003R!a'\u0002\"fj!!!(\u000b\u0007\u0005}U\"\u0001\u0006d_2dWm\u0019;j_:LA!a)\u0002\u001e\nA\u0011\n^3sCR|'\u000fC\u0005\u0002(\u0002\t\t\u0011\"\u0001\u0002*\u0006A1-\u00198FcV\fG\u000e\u0006\u0003\u0002,\u0006E\u0006c\u0001\u0007\u0002.&\u0019\u0011qV\u0007\u0003\u000f\t{w\u000e\\3b]\"A!0!*\u0002\u0002\u0003\u0007\u0011\bC\u0005\u00026\u0002\t\t\u0011\"\u0011\u00028\u0006AAo\\*ue&tw\r\u0006\u0002\u0002~\u001dQ\u00111\u0018\u0002\u0002\u0002#\u0005A!!0\u0002+\r{\u0017\r\\3tG\u0016$'\u000b\u0012#QCJ$\u0018\u000e^5p]B\u0019\u0001&a0\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\t\u0005\u00057#BA`\u0003\u0007D\u0002CCAc\u0003\u0017|\u0012qZ\"L76\u0011\u0011q\u0019\u0006\u0004\u0003\u0013l\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003\u001b\f9MA\tBEN$(/Y2u\rVt7\r^5p]R\u0002D!!5\u0002VB!\u0001&KAj!\ra\u0013Q\u001b\u0003\u000b]\u0005}\u0016\u0011!A\u0001\u0006\u0003)\u0004bB-\u0002@\u0012\u0005\u0011\u0011\u001c\u000b\u0003\u0003{C!\"!.\u0002@\u0006\u0005IQIA\\\u0011)\ty.a0\u0002\u0002\u0013\u0005\u0015\u0011]\u0001\u0006CB\u0004H.\u001f\u000b\n7\u0006\r\u0018Q]Ay\u0003gDa\u0001HAo\u0001\u0004y\u0002bB\u0002\u0002^\u0002\u0007\u0011q\u001d\u0019\u0005\u0003S\fi\u000f\u0005\u0003)S\u0005-\bc\u0001\u0017\u0002n\u0012Qa&!:\u0002\u0002\u0003\u0005)\u0011A\u001b)\u0007\u0005\u0015X\b\u0003\u0004B\u0003;\u0004\ra\u0011\u0005\t\u0013\u0006u\u0007\u0013!a\u0001\u0017\"\u001a\u00111_\u001f\t\u0015\u0005e\u0018qXA\u0001\n\u0003\u000bY0A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005u(q\u0002\u0019\u0005\u0003\u007f\u0014Y\u0001\u0005\u0003\r\u0019\n\u0005\u0001#\u0003\u0007\u0003\u0004}\u00119a\u0011B\u0007\u0013\r\u0011)!\u0004\u0002\u0007)V\u0004H.\u001a\u001b\u0011\t!J#\u0011\u0002\t\u0004Y\t-AA\u0003\u0018\u0002x\u0006\u0005\t\u0011!B\u0001kA!A\u0002TA?\u0011%\u0011\t\"a>\u0002\u0002\u0003\u00071,A\u0002yIAB!B!\u0006\u0002@F\u0005I\u0011AA:\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%i!Q!\u0011DA`#\u0003%\t!a\u001d\u0002\u001f\u0005\u0004\b\u000f\\=%I\u00164\u0017-\u001e7uIQB!B!\b\u0002@\u0006\u0005I\u0011\u0002B\u0010\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\t\u0005\u0002\u0003BA@\u0005GIAA!\n\u0002\u0002\n1qJ\u00196fGR\u0004"
)
public class CoalescedRDDPartition implements Partition, Product {
   private final int index;
   private final transient RDD<?> rdd;
   private final int[] parentsIndices;
   private final transient Option<String> preferredLocation;
   private Seq<Partition> parents;

   public static Option<String> apply$default$4() {
      return CoalescedRDDPartition$.MODULE$.apply$default$4();
   }

   public static Option<String> $lessinit$greater$default$4() {
      return CoalescedRDDPartition$.MODULE$.$lessinit$greater$default$4();
   }

   public static Option<Tuple4<Object, RDD<Object>, int[], Option<String>>> unapply(CoalescedRDDPartition var0) {
      return CoalescedRDDPartition$.MODULE$.unapply(var0);
   }

   public static CoalescedRDDPartition apply(int var0, RDD<?> var1, int[] var2, Option<String> var3) {
      return CoalescedRDDPartition$.MODULE$.apply(var0, var1, var2, var3);
   }

   public static Function1<Tuple4<Object, RDD<?>, int[], Option<String>>, CoalescedRDDPartition> tupled() {
      return CoalescedRDDPartition$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<RDD<?>, Function1<int[], Function1<Option<String>, CoalescedRDDPartition>>>> curried() {
      return CoalescedRDDPartition$.MODULE$.curried();
   }

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public int hashCode() {
      return Partition$class.hashCode(this);
   }

   public boolean equals(Object other) {
      return Partition$class.equals(this, other);
   }

   public int index() {
      return this.index;
   }

   public RDD<?> rdd() {
      return this.rdd;
   }

   public int[] parentsIndices() {
      return this.parentsIndices;
   }

   public Option<String> preferredLocation() {
      return this.preferredLocation;
   }

   public Seq<Partition> parents() {
      return this.parents;
   }

   public void parents_$eq(Seq<Partition> x$1) {
      this.parents = x$1;
   }

   private void writeObject(ObjectOutputStream oos) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, oos) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoalescedRDDPartition $outer;
         private final ObjectOutputStream oos$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.parents_$eq((Seq)Predef$.MODULE$.intArrayOps(this.$outer.parentsIndices()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final Partition apply(int x$2) {
                  return this.$outer.org$apache$spark$rdd$CoalescedRDDPartition$$anonfun$$$outer().rdd().partitions()[x$2];
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit())));
            this.oos$1.defaultWriteObject();
         }

         // $FF: synthetic method
         public CoalescedRDDPartition org$apache$spark$rdd$CoalescedRDDPartition$$anonfun$$$outer() {
            return this.$outer;
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

   public double localFraction() {
      int loc = this.parents().count(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoalescedRDDPartition $outer;

         public final boolean apply(Partition p) {
            Seq parentPreferredLocations = (Seq)this.$outer.rdd().context().getPreferredLocs(this.$outer.rdd(), p.index()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(TaskLocation x$3) {
                  return x$3.host();
               }
            }, Seq$.MODULE$.canBuildFrom());
            return this.$outer.preferredLocation().exists(new Serializable(this, parentPreferredLocations) {
               public static final long serialVersionUID = 0L;
               private final Seq parentPreferredLocations$1;

               public final boolean apply(Object elem) {
                  return this.parentPreferredLocations$1.contains(elem);
               }

               public {
                  this.parentPreferredLocations$1 = parentPreferredLocations$1;
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      return this.parents().size() == 0 ? (double)0.0F : (double)loc / (double)this.parents().size();
   }

   public CoalescedRDDPartition copy(int index, RDD<?> rdd, int[] parentsIndices, Option<String> preferredLocation) {
      return new CoalescedRDDPartition(index, rdd, parentsIndices, preferredLocation);
   }

   public int copy$default$1() {
      return this.index();
   }

   public RDD<?> copy$default$2() {
      return this.rdd();
   }

   public int[] copy$default$3() {
      return this.parentsIndices();
   }

   public Option<String> copy$default$4() {
      return this.preferredLocation();
   }

   public String productPrefix() {
      return "CoalescedRDDPartition";
   }

   public int productArity() {
      return 4;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.index());
            break;
         case 1:
            var10000 = this.rdd();
            break;
         case 2:
            var10000 = this.parentsIndices();
            break;
         case 3:
            var10000 = this.preferredLocation();
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
      return x$1 instanceof CoalescedRDDPartition;
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public CoalescedRDDPartition(int index, RDD<?> rdd, int[] parentsIndices, Option<String> preferredLocation) {
      this.index = index;
      this.rdd = rdd;
      this.parentsIndices = parentsIndices;
      this.preferredLocation = preferredLocation;
      Partition$class.$init$(this);
      Product$class.$init$(this);
      this.parents = (Seq)Predef$.MODULE$.intArrayOps(parentsIndices).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoalescedRDDPartition $outer;

         public final Partition apply(int x$1) {
            return this.$outer.rdd().partitions()[x$1];
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
   }
}
