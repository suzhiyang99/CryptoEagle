package org.apache.spark;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.spark.rdd.PartitionPruningRDD;
import org.apache.spark.rdd.RDD;
import org.apache.spark.serializer.DeserializationStream;
import org.apache.spark.serializer.JavaSerializer;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.util.CollectionsUtils$;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Function2;
import scala.MatchError;
import scala.Predef;
import scala.Predef$;
import scala.Product2;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.TraversableOnce;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.Set;
import scala.collection.mutable.Set$;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\tuc\u0001B\u0001\u0003\u0001%\u0011\u0001CU1oO\u0016\u0004\u0016M\u001d;ji&|g.\u001a:\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001c\u0001!F\u0002\u000bWI\u001a\"\u0001A\u0006\u0011\u00051iQ\"\u0001\u0002\n\u00059\u0011!a\u0003)beRLG/[8oKJD\u0001\u0002\u0005\u0001\u0003\u0002\u0003\u0006I!E\u0001\u000ba\u0006\u0014H/\u001b;j_:\u001c\bC\u0001\n\u0016\u001b\u0005\u0019\"\"\u0001\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Y\u0019\"aA%oi\"A\u0001\u0004\u0001B\u0001B\u0003%\u0011$A\u0002sI\u0012\u0004$AG\u0011\u0011\u0007mir$D\u0001\u001d\u0015\tA\"!\u0003\u0002\u001f9\t\u0019!\u000b\u0012#\u0011\u0005\u0001\nC\u0002\u0001\u0003\nE]\t\t\u0011!A\u0003\u0002\r\u00121a\u0018\u00137#\t!s\u0005\u0005\u0002\u0013K%\u0011ae\u0005\u0002\b\u001d>$\b.\u001b8h!\u0011\u0011\u0002FK\u0019\n\u0005%\u001a\"\u0001\u0003)s_\u0012,8\r\u001e\u001a\u0011\u0005\u0001ZC!\u0002\u0017\u0001\u0005\u0004i#!A&\u0012\u0005\u0011r\u0003C\u0001\n0\u0013\t\u00014CA\u0002B]f\u0004\"\u0001\t\u001a\u0005\u000bM\u0002!\u0019A\u0017\u0003\u0003YC\u0001\"\u000e\u0001\u0003\u0002\u0004%IAN\u0001\nCN\u001cWM\u001c3j]\u001e,\u0012a\u000e\t\u0003%aJ!!O\n\u0003\u000f\t{w\u000e\\3b]\"A1\b\u0001BA\u0002\u0013%A(A\u0007bg\u000e,g\u000eZ5oO~#S-\u001d\u000b\u0003{\u0001\u0003\"A\u0005 \n\u0005}\u001a\"\u0001B+oSRDq!\u0011\u001e\u0002\u0002\u0003\u0007q'A\u0002yIEB\u0001b\u0011\u0001\u0003\u0002\u0003\u0006KaN\u0001\u000bCN\u001cWM\u001c3j]\u001e\u0004\u0003\u0002C#\u0001\u0005\u000b\u0007I\u0011\u0001$\u00029M\fW\u000e\u001d7f!>Lg\u000e^:QKJ\u0004\u0016M\u001d;ji&|g\u000eS5oiV\t\u0011\u0003\u0003\u0005I\u0001\t\u0005\t\u0015!\u0003\u0012\u0003u\u0019\u0018-\u001c9mKB{\u0017N\u001c;t!\u0016\u0014\b+\u0019:uSRLwN\u001c%j]R\u0004\u0003\u0002\u0003&\u0001\u0005\u0007\u0005\u000b1B&\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007E\u0002M)*r!!\u0014*\u000f\u00059\u000bV\"A(\u000b\u0005AC\u0011A\u0002\u001fs_>$h(C\u0001\u0015\u0013\t\u00196#A\u0004qC\u000e\\\u0017mZ3\n\u0005U3&\u0001C(sI\u0016\u0014\u0018N\\4\u000b\u0005M\u001b\u0002\u0002\u0003-\u0001\u0005\u0007\u0005\u000b1B-\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$#\u0007E\u0002[;*j\u0011a\u0017\u0006\u00039N\tqA]3gY\u0016\u001cG/\u0003\u0002_7\nA1\t\\1tgR\u000bw\rC\u0003a\u0001\u0011\u0005\u0011-\u0001\u0004=S:LGO\u0010\u000b\u0006E\u001a<G.\u001c\u000b\u0004G\u0012,\u0007\u0003\u0002\u0007\u0001UEBQAS0A\u0004-CQ\u0001W0A\u0004eCQ\u0001E0A\u0002EAQ\u0001G0A\u0002!\u0004$![6\u0011\u0007mi\"\u000e\u0005\u0002!W\u0012I!eZA\u0001\u0002\u0003\u0015\ta\t\u0005\bk}\u0003\n\u00111\u00018\u0011\u001d)u\f%AA\u0002EAQ\u0001\u0019\u0001\u0005\u0002=$B\u0001];wyR\u00191-]:\t\u000fIt\u0017\u0011!a\u0002\u0017\u0006QQM^5eK:\u001cW\rJ\u001a\t\u000fQt\u0017\u0011!a\u00023\u0006QQM^5eK:\u001cW\r\n\u001b\t\u000bAq\u0007\u0019A\t\t\u000baq\u0007\u0019A<1\u0005aT\bcA\u000e\u001esB\u0011\u0001E\u001f\u0003\nwZ\f\t\u0011!A\u0003\u0002\r\u00121a\u0018\u00138\u0011\u0015)d\u000e1\u00018\u0011\u001dq\b\u00011A\u0005\n}\f\u0001b\u001c:eKJLgnZ\u000b\u0002\u0017\"I\u00111\u0001\u0001A\u0002\u0013%\u0011QA\u0001\r_J$WM]5oO~#S-\u001d\u000b\u0004{\u0005\u001d\u0001\u0002C!\u0002\u0002\u0005\u0005\t\u0019A&\t\u000f\u0005-\u0001\u0001)Q\u0005\u0017\u0006IqN\u001d3fe&tw\r\t\u0005\n\u0003\u001f\u0001\u0001\u0019!C\u0005\u0003#\t1B]1oO\u0016\u0014u.\u001e8egV\u0011\u00111\u0003\t\u0005%\u0005U!&C\u0002\u0002\u0018M\u0011Q!\u0011:sCfD\u0011\"a\u0007\u0001\u0001\u0004%I!!\b\u0002\u001fI\fgnZ3C_VtGm]0%KF$2!PA\u0010\u0011%\t\u0015\u0011DA\u0001\u0002\u0004\t\u0019\u0002\u0003\u0005\u0002$\u0001\u0001\u000b\u0015BA\n\u00031\u0011\u0018M\\4f\u0005>,h\u000eZ:!\u0011\u0019\t9\u0003\u0001C\u0001\r\u0006ia.^7QCJ$\u0018\u000e^5p]ND\u0011\"a\u000b\u0001\u0001\u0004%I!!\f\u0002\u0019\tLg.\u0019:z'\u0016\f'o\u00195\u0016\u0005\u0005=\u0002c\u0002\n\u00022\u0005M!&E\u0005\u0004\u0003g\u0019\"!\u0003$v]\u000e$\u0018n\u001c83\u0011%\t9\u0004\u0001a\u0001\n\u0013\tI$\u0001\tcS:\f'/_*fCJ\u001c\u0007n\u0018\u0013fcR\u0019Q(a\u000f\t\u0013\u0005\u000b)$!AA\u0002\u0005=\u0002\u0002CA \u0001\u0001\u0006K!a\f\u0002\u001b\tLg.\u0019:z'\u0016\f'o\u00195!\u0011\u001d\t\u0019\u0005\u0001C\u0001\u0003\u000b\nAbZ3u!\u0006\u0014H/\u001b;j_:$2!EA$\u0011\u001d\tI%!\u0011A\u00029\n1a[3z\u0011\u001d\ti\u0005\u0001C!\u0003\u001f\na!Z9vC2\u001cHcA\u001c\u0002R!9\u00111KA&\u0001\u0004q\u0013!B8uQ\u0016\u0014\bbBA,\u0001\u0011\u0005\u0013\u0011L\u0001\tQ\u0006\u001c\bnQ8eKR\t\u0011\u0003C\u0004\u0002^\u0001!I!a\u0018\u0002\u0017]\u0014\u0018\u000e^3PE*,7\r\u001e\u000b\u0004{\u0005\u0005\u0004\u0002CA2\u00037\u0002\r!!\u001a\u0002\u0007=,H\u000f\u0005\u0003\u0002h\u0005ETBAA5\u0015\u0011\tY'!\u001c\u0002\u0005%|'BAA8\u0003\u0011Q\u0017M^1\n\t\u0005M\u0014\u0011\u000e\u0002\u0013\u001f\nTWm\u0019;PkR\u0004X\u000f^*ue\u0016\fW\u000e\u000b\u0004\u0002\\\u0005]\u00141\u0011\t\u0006%\u0005e\u0014QP\u0005\u0004\u0003w\u001a\"A\u0002;ie><8\u000f\u0005\u0003\u0002h\u0005}\u0014\u0002BAA\u0003S\u00121\"S(Fq\u000e,\u0007\u000f^5p]\u000e\u0012\u0011Q\u0010\u0005\b\u0003\u000f\u0003A\u0011BAE\u0003)\u0011X-\u00193PE*,7\r\u001e\u000b\u0004{\u0005-\u0005\u0002CAG\u0003\u000b\u0003\r!a$\u0002\u0005%t\u0007\u0003BA4\u0003#KA!a%\u0002j\t\trJ\u00196fGRLe\u000e];u'R\u0014X-Y7)\r\u0005\u0015\u0015qOAB\u000f!\tIJ\u0001E\u0001\u0005\u0005m\u0015\u0001\u0005*b]\u001e,\u0007+\u0019:uSRLwN\\3s!\ra\u0011Q\u0014\u0004\b\u0003\tA\tAAAP'\u0019\ti*!)\u0002(B\u0019!#a)\n\u0007\u0005\u00156C\u0001\u0004B]f\u0014VM\u001a\t\u0004%\u0005%\u0016bAAV'\ta1+\u001a:jC2L'0\u00192mK\"9\u0001-!(\u0005\u0002\u0005=FCAAN\u0011!\t\u0019,!(\u0005\u0002\u0005U\u0016AB:lKR\u001c\u0007.\u0006\u0003\u00028\u0006MGCBA]\u00037\fy\u000e\u0006\u0003\u0002<\u0006U\u0007c\u0002\n\u0002>\u0006\u0005\u0017qY\u0005\u0004\u0003\u007f\u001b\"A\u0002+va2,'\u0007E\u0002\u0013\u0003\u0007L1!!2\u0014\u0005\u0011auN\\4\u0011\u000bI\t)\"!3\u0011\u0011I\tY-EAa\u0003\u001fL1!!4\u0014\u0005\u0019!V\u000f\u001d7fgA)!#!\u0006\u0002RB\u0019\u0001%a5\u0005\r1\n\tL1\u0001.\u0011)\t9.!-\u0002\u0002\u0003\u000f\u0011\u0011\\\u0001\u000bKZLG-\u001a8dK\u0012*\u0004\u0003\u0002.^\u0003#Dq\u0001GAY\u0001\u0004\ti\u000e\u0005\u0003\u001c;\u0005E\u0007bBAq\u0003c\u0003\r!E\u0001\u0017g\u0006l\u0007\u000f\\3TSj,\u0007+\u001a:QCJ$\u0018\u000e^5p]\"A\u0011Q]AO\t\u0003\t9/A\beKR,'/\\5oK\n{WO\u001c3t+\u0011\tI/!=\u0015\r\u0005-\u0018q B\u000e)\u0019\ti/a=\u0002zB)!#!\u0006\u0002pB\u0019\u0001%!=\u0005\r1\n\u0019O1\u0001.\u0011)\t)0a9\u0002\u0002\u0003\u000f\u0011q_\u0001\u000bKZLG-\u001a8dK\u00122\u0004\u0003\u0002'U\u0003_D!\"a?\u0002d\u0006\u0005\t9AA\u007f\u0003))g/\u001b3f]\u000e,Ge\u000e\t\u00055v\u000by\u000f\u0003\u0005\u0003\u0002\u0005\r\b\u0019\u0001B\u0002\u0003)\u0019\u0017M\u001c3jI\u0006$Xm\u001d\t\u0007\u0005\u000b\u0011yAa\u0005\u000e\u0005\t\u001d!\u0002\u0002B\u0005\u0005\u0017\tq!\\;uC\ndWMC\u0002\u0003\u000eM\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\u0011\tBa\u0002\u0003\u0017\u0005\u0013(/Y=Ck\u001a4WM\u001d\t\b%\u0005u\u0016q\u001eB\u000b!\r\u0011\"qC\u0005\u0004\u00053\u0019\"!\u0002$m_\u0006$\bB\u0002\t\u0002d\u0002\u0007\u0011\u0003\u0003\u0006\u0003 \u0005u\u0015\u0013!C\u0001\u0005C\t1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\u001aTC\u0002B\u0012\u0005s\u0011Y$\u0006\u0002\u0003&)\u001aqGa\n,\u0005\t%\u0002\u0003\u0002B\u0016\u0005ki!A!\f\u000b\t\t=\"\u0011G\u0001\nk:\u001c\u0007.Z2lK\u0012T1Aa\r\u0014\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0005o\u0011iCA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016$a\u0001\fB\u000f\u0005\u0004iCAB\u001a\u0003\u001e\t\u0007Q\u0006\u0003\u0006\u0003@\u0005u\u0015\u0013!C\u0001\u0005\u0003\n1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\"TC\u0002B\"\u0005\u000f\u0012I%\u0006\u0002\u0003F)\u001a\u0011Ca\n\u0005\r1\u0012iD1\u0001.\t\u0019\u0019$Q\bb\u0001[!Q!QJAO\u0003\u0003%IAa\u0014\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0005#\u0002BAa\u0015\u0003Z5\u0011!Q\u000b\u0006\u0005\u0005/\ni'\u0001\u0003mC:<\u0017\u0002\u0002B.\u0005+\u0012aa\u00142kK\u000e$\b"
)
public class RangePartitioner<K, V> extends Partitioner {
   public final int org$apache$spark$RangePartitioner$$partitions;
   private boolean org$apache$spark$RangePartitioner$$ascending;
   private final int samplePointsPerPartitionHint;
   public final ClassTag<K> org$apache$spark$RangePartitioner$$evidence$2;
   private Ordering<K> org$apache$spark$RangePartitioner$$ordering;
   private Object org$apache$spark$RangePartitioner$$rangeBounds;
   private Function2<Object, K, Object> org$apache$spark$RangePartitioner$$binarySearch;

   public static <K, V> int $lessinit$greater$default$4() {
      return RangePartitioner$.MODULE$.$lessinit$greater$default$4();
   }

   public static <K, V> boolean $lessinit$greater$default$3() {
      return RangePartitioner$.MODULE$.$lessinit$greater$default$3();
   }

   public static Object determineBounds(ArrayBuffer var0, int var1, Ordering var2, ClassTag var3) {
      return RangePartitioner$.MODULE$.determineBounds(var0, var1, var2, var3);
   }

   public static <K> Tuple2<Object, Tuple3<Object, Object, Object>[]> sketch(RDD<K> var0, int var1, ClassTag<K> var2) {
      return RangePartitioner$.MODULE$.sketch(var0, var1, var2);
   }

   public boolean org$apache$spark$RangePartitioner$$ascending() {
      return this.org$apache$spark$RangePartitioner$$ascending;
   }

   public void org$apache$spark$RangePartitioner$$ascending_$eq(boolean x$1) {
      this.org$apache$spark$RangePartitioner$$ascending = x$1;
   }

   public int samplePointsPerPartitionHint() {
      return this.samplePointsPerPartitionHint;
   }

   public Ordering<K> org$apache$spark$RangePartitioner$$ordering() {
      return this.org$apache$spark$RangePartitioner$$ordering;
   }

   public void org$apache$spark$RangePartitioner$$ordering_$eq(Ordering<K> x$1) {
      this.org$apache$spark$RangePartitioner$$ordering = x$1;
   }

   public Object org$apache$spark$RangePartitioner$$rangeBounds() {
      return this.org$apache$spark$RangePartitioner$$rangeBounds;
   }

   public void org$apache$spark$RangePartitioner$$rangeBounds_$eq(Object x$1) {
      this.org$apache$spark$RangePartitioner$$rangeBounds = x$1;
   }

   public int numPartitions() {
      return ScalaRunTime$.MODULE$.array_length(this.org$apache$spark$RangePartitioner$$rangeBounds()) + 1;
   }

   public Function2<Object, K, Object> org$apache$spark$RangePartitioner$$binarySearch() {
      return this.org$apache$spark$RangePartitioner$$binarySearch;
   }

   public void org$apache$spark$RangePartitioner$$binarySearch_$eq(Function2<Object, K, Object> x$1) {
      this.org$apache$spark$RangePartitioner$$binarySearch = x$1;
   }

   public int getPartition(Object key) {
      Object k = key;
      int partition = 0;
      if (ScalaRunTime$.MODULE$.array_length(this.org$apache$spark$RangePartitioner$$rangeBounds()) <= 128) {
         while(partition < ScalaRunTime$.MODULE$.array_length(this.org$apache$spark$RangePartitioner$$rangeBounds()) && this.org$apache$spark$RangePartitioner$$ordering().gt(k, ScalaRunTime$.MODULE$.array_apply(this.org$apache$spark$RangePartitioner$$rangeBounds(), partition))) {
            ++partition;
         }
      } else {
         partition = BoxesRunTime.unboxToInt(this.org$apache$spark$RangePartitioner$$binarySearch().apply(this.org$apache$spark$RangePartitioner$$rangeBounds(), key));
         if (partition < 0) {
            partition = -partition - 1;
         }

         if (partition > ScalaRunTime$.MODULE$.array_length(this.org$apache$spark$RangePartitioner$$rangeBounds())) {
            partition = ScalaRunTime$.MODULE$.array_length(this.org$apache$spark$RangePartitioner$$rangeBounds());
         }
      }

      return this.org$apache$spark$RangePartitioner$$ascending() ? partition : ScalaRunTime$.MODULE$.array_length(this.org$apache$spark$RangePartitioner$$rangeBounds()) - partition;
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof RangePartitioner) {
         RangePartitioner var3 = (RangePartitioner)other;
         var4 = Predef$.MODULE$.genericArrayOps(var3.org$apache$spark$RangePartitioner$$rangeBounds()).sameElements(Predef$.MODULE$.genericWrapArray(this.org$apache$spark$RangePartitioner$$rangeBounds())) && var3.org$apache$spark$RangePartitioner$$ascending() == this.org$apache$spark$RangePartitioner$$ascending();
      } else {
         var4 = false;
      }

      return var4;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;

      for(int i = 0; i < ScalaRunTime$.MODULE$.array_length(this.org$apache$spark$RangePartitioner$$rangeBounds()); ++i) {
         result = prime * result + ScalaRunTime$.MODULE$.array_apply(this.org$apache$spark$RangePartitioner$$rangeBounds(), i).hashCode();
      }

      result = prime * result + BoxesRunTime.boxToBoolean(this.org$apache$spark$RangePartitioner$$ascending()).hashCode();
      return result;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RangePartitioner $outer;
         private final ObjectOutputStream out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            Serializer sfactory = SparkEnv$.MODULE$.get().serializer();
            if (sfactory instanceof JavaSerializer) {
               this.out$1.defaultWriteObject();
               BoxedUnit var3 = BoxedUnit.UNIT;
            } else {
               this.out$1.writeBoolean(this.$outer.org$apache$spark$RangePartitioner$$ascending());
               this.out$1.writeObject(this.$outer.org$apache$spark$RangePartitioner$$ordering());
               this.out$1.writeObject(this.$outer.org$apache$spark$RangePartitioner$$binarySearch());
               SerializerInstance ser = sfactory.newInstance();
               Utils$.MODULE$.serializeViaNestedStream(this.out$1, ser, new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(SerializationStream stream) {
                     stream.writeObject(scala.reflect.package$.MODULE$.classTag(ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(this.$outer.org$apache$spark$RangePartitioner$$anonfun$$$outer().org$apache$spark$RangePartitioner$$evidence$2.runtimeClass()))), ClassTag$.MODULE$.apply(ClassTag.class));
                     stream.writeObject(this.$outer.org$apache$spark$RangePartitioner$$anonfun$$$outer().org$apache$spark$RangePartitioner$$rangeBounds(), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(this.$outer.org$apache$spark$RangePartitioner$$anonfun$$$outer().org$apache$spark$RangePartitioner$$evidence$2.runtimeClass())));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               BoxedUnit var5 = BoxedUnit.UNIT;
            }

         }

         // $FF: synthetic method
         public RangePartitioner org$apache$spark$RangePartitioner$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.out$1 = out$1;
            }
         }
      });
   }

   private void readObject(ObjectInputStream in) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RangePartitioner $outer;
         private final ObjectInputStream in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            Serializer sfactory = SparkEnv$.MODULE$.get().serializer();
            if (sfactory instanceof JavaSerializer) {
               this.in$1.defaultReadObject();
               BoxedUnit var3 = BoxedUnit.UNIT;
            } else {
               this.$outer.org$apache$spark$RangePartitioner$$ascending_$eq(this.in$1.readBoolean());
               this.$outer.org$apache$spark$RangePartitioner$$ordering_$eq((Ordering)this.in$1.readObject());
               this.$outer.org$apache$spark$RangePartitioner$$binarySearch_$eq((Function2)this.in$1.readObject());
               SerializerInstance ser = sfactory.newInstance();
               Utils$.MODULE$.deserializeViaNestedStream(this.in$1, ser, new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(DeserializationStream ds) {
                     ClassTag classTag = (ClassTag)ds.readObject(ClassTag$.MODULE$.apply(ClassTag.class));
                     this.$outer.org$apache$spark$RangePartitioner$$anonfun$$$outer().org$apache$spark$RangePartitioner$$rangeBounds_$eq(ds.readObject(classTag));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               BoxedUnit var5 = BoxedUnit.UNIT;
            }

         }

         // $FF: synthetic method
         public RangePartitioner org$apache$spark$RangePartitioner$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   public RangePartitioner(int partitions, RDD<? extends Product2<K, V>> rdd, boolean ascending, int samplePointsPerPartitionHint, Ordering<K> evidence$1, ClassTag<K> evidence$2) {
      this.org$apache$spark$RangePartitioner$$partitions = partitions;
      this.org$apache$spark$RangePartitioner$$ascending = ascending;
      this.samplePointsPerPartitionHint = samplePointsPerPartitionHint;
      this.org$apache$spark$RangePartitioner$$evidence$2 = evidence$2;
      super();
      Predef$.MODULE$.require(partitions >= 0, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RangePartitioner $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Number of partitions cannot be negative but found ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$RangePartitioner$$partitions)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Predef$.MODULE$.require(samplePointsPerPartitionHint > 0, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RangePartitioner $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sample points per partition must be greater than 0 but found ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.samplePointsPerPartitionHint())}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.org$apache$spark$RangePartitioner$$ordering = (Ordering)Predef$.MODULE$.implicitly(evidence$1);
      Object var10001;
      if (partitions <= 1) {
         var10001 = Array$.MODULE$.empty(evidence$2);
      } else {
         double sampleSize = scala.math.package$.MODULE$.min((double)samplePointsPerPartitionHint * (double)partitions, (double)1000000.0F);
         int sampleSizePerPartition = (int)scala.math.package$.MODULE$.ceil((double)3.0F * sampleSize / (double)rdd.partitions().length);
         Tuple2 var11 = RangePartitioner$.MODULE$.sketch(rdd.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final K apply(Product2 x$6) {
               return (K)x$6._1();
            }
         }, evidence$2), sampleSizePerPartition, evidence$2);
         if (var11 == null) {
            throw new MatchError(var11);
         }

         long numItems = var11._1$mcJ$sp();
         Tuple3[] sketched = (Tuple3[])var11._2();
         Tuple2 var15 = new Tuple2(BoxesRunTime.boxToLong(numItems), sketched);
         long numItems = var15._1$mcJ$sp();
         Tuple3[] sketched = (Tuple3[])var15._2();
         if (numItems == 0L) {
            var10001 = Array$.MODULE$.empty(evidence$2);
         } else {
            double fraction = scala.math.package$.MODULE$.min(sampleSize / (double)scala.math.package$.MODULE$.max(numItems, 1L), (double)1.0F);
            ArrayBuffer candidates = (ArrayBuffer)ArrayBuffer$.MODULE$.empty();
            Set imbalancedPartitions = Set$.MODULE$.empty();
            Predef$.MODULE$.refArrayOps(sketched).foreach(new Serializable(this, sampleSizePerPartition, fraction, candidates, imbalancedPartitions) {
               public static final long serialVersionUID = 0L;
               private final int sampleSizePerPartition$1;
               private final double fraction$1;
               public final ArrayBuffer candidates$1;
               private final Set imbalancedPartitions$1;

               public final Object apply(Tuple3<Object, Object, Object> x0$1) {
                  if (x0$1 != null) {
                     int idx = BoxesRunTime.unboxToInt(x0$1._1());
                     long n = BoxesRunTime.unboxToLong(x0$1._2());
                     Object sample = x0$1._3();
                     Object var10000;
                     if (this.fraction$1 * (double)n > (double)this.sampleSizePerPartition$1) {
                        var10000 = this.imbalancedPartitions$1.$plus$eq(BoxesRunTime.boxToInteger(idx));
                     } else {
                        float weight = (float)((double)n / (double)ScalaRunTime$.MODULE$.array_length(sample));
                        Predef$.MODULE$.genericArrayOps(sample).foreach(new Serializable(this, weight) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final float weight$1;

                           public final ArrayBuffer<Tuple2<K, Object>> apply(K key) {
                              return this.$outer.candidates$1.$plus$eq(new Tuple2(key, BoxesRunTime.boxToFloat(this.weight$1)));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.weight$1 = weight$1;
                              }
                           }
                        });
                        var10000 = BoxedUnit.UNIT;
                     }

                     Object var7 = var10000;
                     return var7;
                  } else {
                     throw new MatchError(x0$1);
                  }
               }

               public {
                  this.sampleSizePerPartition$1 = sampleSizePerPartition$1;
                  this.fraction$1 = fraction$1;
                  this.candidates$1 = candidates$1;
                  this.imbalancedPartitions$1 = imbalancedPartitions$1;
               }
            });
            if (imbalancedPartitions.nonEmpty()) {
               PartitionPruningRDD imbalanced = new PartitionPruningRDD(rdd.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final K apply(Product2 x$8) {
                     return (K)x$8._1();
                  }
               }, evidence$2), new Serializable(this, imbalancedPartitions) {
                  public static final long serialVersionUID = 0L;
                  private final Set imbalancedPartitions$1;

                  public final boolean apply(int elem) {
                     return this.apply$mcZI$sp(elem);
                  }

                  public boolean apply$mcZI$sp(int elem) {
                     return this.imbalancedPartitions$1.contains(BoxesRunTime.boxToInteger(elem));
                  }

                  public {
                     this.imbalancedPartitions$1 = imbalancedPartitions$1;
                  }
               }, evidence$2);
               int seed = scala.util.hashing.package$.MODULE$.byteswap32(-rdd.id() - 1);
               Object reSampled = imbalanced.sample(false, fraction, (long)seed).collect();
               float weight = (float)((double)1.0F / fraction);
               candidates.$plus$plus$eq((TraversableOnce)Predef$.MODULE$.genericArrayOps(reSampled).map(new Serializable(this, weight) {
                  public static final long serialVersionUID = 0L;
                  private final float weight$2;

                  public final Tuple2<K, Object> apply(K x) {
                     return new Tuple2<K, Object>(x, BoxesRunTime.boxToFloat(this.weight$2));
                  }

                  public {
                     this.weight$2 = weight$2;
                  }
               }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit())));
            } else {
               BoxedUnit var27 = BoxedUnit.UNIT;
            }

            var10001 = RangePartitioner$.MODULE$.determineBounds(candidates, scala.math.package$.MODULE$.min(partitions, candidates.size()), evidence$1, evidence$2);
         }
      }

      this.org$apache$spark$RangePartitioner$$rangeBounds = var10001;
      this.org$apache$spark$RangePartitioner$$binarySearch = CollectionsUtils$.MODULE$.makeBinarySearch(evidence$1, evidence$2);
   }

   public RangePartitioner(int partitions, RDD<? extends Product2<K, V>> rdd, boolean ascending, Ordering<K> evidence$3, ClassTag<K> evidence$4) {
      this(partitions, rdd, ascending, 20, evidence$3, evidence$4);
   }
}
