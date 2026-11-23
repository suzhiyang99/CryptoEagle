package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import scala.Function3;
import scala.Predef$;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.List$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ee!B\u0001\u0003\u0001\u0011Q!\u0001\u0006.jaB,G\rU1si&$\u0018n\u001c8t%\u0012#5G\u0003\u0002\u0004\t\u0005\u0019!\u000f\u001a3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e,Ra\u0003\u001d=\u0001J\u0019\"\u0001\u0001\u0007\u0011\u00075q\u0001#D\u0001\u0003\u0013\ty!AA\f[SB\u0004X\r\u001a)beRLG/[8og\n\u000b7/\u001a*E\tB\u0011\u0011C\u0005\u0007\u0001\t\u0015\u0019\u0002A1\u0001\u0016\u0005\u000516\u0001A\t\u0003-q\u0001\"a\u0006\u000e\u000e\u0003aQ\u0011!G\u0001\u0006g\u000e\fG.Y\u0005\u00037a\u0011qAT8uQ&tw\r\u0005\u0002\u0018;%\u0011a\u0004\u0007\u0002\u0004\u0003:L\b\u0002\u0003\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\u0011\u0002\u0005M\u001c\u0007C\u0001\u0012$\u001b\u0005!\u0011B\u0001\u0013\u0005\u00051\u0019\u0006/\u0019:l\u0007>tG/\u001a=u\u0011!1\u0003A!a\u0001\n\u00039\u0013!\u00014\u0016\u0003!\u0002baF\u0015,uy\u0012\u0015B\u0001\u0016\u0019\u0005%1UO\\2uS>t7\u0007E\u0002-i]r!!\f\u001a\u000f\u00059\nT\"A\u0018\u000b\u0005A\"\u0012A\u0002\u001fs_>$h(C\u0001\u001a\u0013\t\u0019\u0004$A\u0004qC\u000e\\\u0017mZ3\n\u0005U2$\u0001C%uKJ\fGo\u001c:\u000b\u0005MB\u0002CA\t9\t\u0015I\u0004A1\u0001\u0016\u0005\u0005\t\u0005c\u0001\u00175wA\u0011\u0011\u0003\u0010\u0003\u0006{\u0001\u0011\r!\u0006\u0002\u0002\u0005B\u0019A\u0006N \u0011\u0005E\u0001E!B!\u0001\u0005\u0004)\"!A\"\u0011\u00071\"\u0004\u0003\u0003\u0005E\u0001\t\u0005\r\u0011\"\u0001F\u0003\u00151w\fJ3r)\t1\u0015\n\u0005\u0002\u0018\u000f&\u0011\u0001\n\u0007\u0002\u0005+:LG\u000fC\u0004K\u0007\u0006\u0005\t\u0019\u0001\u0015\u0002\u0007a$\u0013\u0007\u0003\u0005M\u0001\t\u0005\t\u0015)\u0003)\u0003\t1\u0007\u0005\u0003\u0005O\u0001\t\u0005\r\u0011\"\u0001P\u0003\u0011\u0011H\rZ\u0019\u0016\u0003A\u00032!D)8\u0013\t\u0011&AA\u0002S\t\u0012C\u0001\u0002\u0016\u0001\u0003\u0002\u0004%\t!V\u0001\te\u0012$\u0017g\u0018\u0013fcR\u0011aI\u0016\u0005\b\u0015N\u000b\t\u00111\u0001Q\u0011!A\u0006A!A!B\u0013\u0001\u0016!\u0002:eIF\u0002\u0003\u0002\u0003.\u0001\u0005\u0003\u0007I\u0011A.\u0002\tI$GMM\u000b\u00029B\u0019Q\"U\u001e\t\u0011y\u0003!\u00111A\u0005\u0002}\u000b\u0001B\u001d3ee}#S-\u001d\u000b\u0003\r\u0002DqAS/\u0002\u0002\u0003\u0007A\f\u0003\u0005c\u0001\t\u0005\t\u0015)\u0003]\u0003\u0015\u0011H\r\u001a\u001a!\u0011!!\u0007A!a\u0001\n\u0003)\u0017\u0001\u0002:eIN*\u0012A\u001a\t\u0004\u001bE{\u0004\u0002\u00035\u0001\u0005\u0003\u0007I\u0011A5\u0002\u0011I$GmM0%KF$\"A\u00126\t\u000f);\u0017\u0011!a\u0001M\"AA\u000e\u0001B\u0001B\u0003&a-A\u0003sI\u0012\u001c\u0004\u0005\u0003\u0005o\u0001\t\u0005\t\u0015!\u0003p\u0003U\u0001(/Z:feZ,7\u000fU1si&$\u0018n\u001c8j]\u001e\u0004\"a\u00069\n\u0005ED\"a\u0002\"p_2,\u0017M\u001c\u0005\tg\u0002\u0011\u0019\u0011)A\u0006i\u0006QQM^5eK:\u001cW\rJ\u001b\u0011\u0007UDx'D\u0001w\u0015\t9\b$A\u0004sK\u001adWm\u0019;\n\u0005e4(\u0001C\"mCN\u001cH+Y4\t\u0011m\u0004!1!Q\u0001\fq\f!\"\u001a<jI\u0016t7-\u001a\u00137!\r)\bp\u000f\u0005\t}\u0002\u0011\u0019\u0011)A\u0006\u007f\u0006QQM^5eK:\u001cW\rJ\u001c\u0011\u0007UDx\b\u0003\u0006\u0002\u0004\u0001\u0011\u0019\u0011)A\u0006\u0003\u000b\t!\"\u001a<jI\u0016t7-\u001a\u00139!\r)\b\u0010\u0005\u0005\b\u0003\u0013\u0001A\u0011AA\u0006\u0003\u0019a\u0014N\\5u}Qq\u0011QBA\r\u00037\ti\"a\b\u0002\"\u0005\rBCCA\b\u0003#\t\u0019\"!\u0006\u0002\u0018A1Q\u0002A\u001c<\u007fAAaa]A\u0004\u0001\b!\bBB>\u0002\b\u0001\u000fA\u0010\u0003\u0004\u007f\u0003\u000f\u0001\u001da \u0005\t\u0003\u0007\t9\u0001q\u0001\u0002\u0006!1\u0001%a\u0002A\u0002\u0005BaAJA\u0004\u0001\u0004A\u0003B\u0002(\u0002\b\u0001\u0007\u0001\u000b\u0003\u0004[\u0003\u000f\u0001\r\u0001\u0018\u0005\u0007I\u0006\u001d\u0001\u0019\u00014\t\u00119\f9\u0001%AA\u0002=Dq!a\n\u0001\t\u0003\nI#A\u0004d_6\u0004X\u000f^3\u0015\u000b\t\u000bY#!\u000e\t\u0011\u00055\u0012Q\u0005a\u0001\u0003_\t\u0011a\u001d\t\u0004E\u0005E\u0012bAA\u001a\t\tI\u0001+\u0019:uSRLwN\u001c\u0005\t\u0003o\t)\u00031\u0001\u0002:\u000591m\u001c8uKb$\bc\u0001\u0012\u0002<%\u0019\u0011Q\b\u0003\u0003\u0017Q\u000b7o[\"p]R,\u0007\u0010\u001e\u0005\b\u0003\u0003\u0002A\u0011IA\"\u0003E\u0019G.Z1s\t\u0016\u0004XM\u001c3f]\u000eLWm\u001d\u000b\u0002\r\u001eQ\u0011q\t\u0002\u0002\u0002#\u0005A!!\u0013\u0002)iK\u0007\u000f]3e!\u0006\u0014H/\u001b;j_:\u001c(\u000b\u0012#4!\ri\u00111\n\u0004\n\u0003\t\t\t\u0011#\u0001\u0005\u0003\u001b\u001ab!a\u0013\u0002P\u0005U\u0003cA\f\u0002R%\u0019\u00111\u000b\r\u0003\r\u0005s\u0017PU3g!\r9\u0012qK\u0005\u0004\u00033B\"\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002CA\u0005\u0003\u0017\"\t!!\u0018\u0015\u0005\u0005%\u0003BCA1\u0003\u0017\n\n\u0011\"\u0001\u0002d\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIY*\"\"!\u001a\u0002|\u0005u\u0014qPAA+\t\t9GK\u0002p\u0003SZ#!a\u001b\u0011\t\u00055\u0014qO\u0007\u0003\u0003_RA!!\u001d\u0002t\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003kB\u0012AC1o]>$\u0018\r^5p]&!\u0011\u0011PA8\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0003\u0007s\u0005}#\u0019A\u000b\u0005\ru\nyF1\u0001\u0016\t\u0019\t\u0015q\fb\u0001+\u001111#a\u0018C\u0002UA!\"!\"\u0002L\u0005\u0005I\u0011BAD\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005%\u0005\u0003BAF\u0003+k!!!$\u000b\t\u0005=\u0015\u0011S\u0001\u0005Y\u0006twM\u0003\u0002\u0002\u0014\u0006!!.\u0019<b\u0013\u0011\t9*!$\u0003\r=\u0013'.Z2u\u0001"
)
public class ZippedPartitionsRDD3<A, B, C, V> extends ZippedPartitionsBaseRDD<V> {
   private Function3<Iterator<A>, Iterator<B>, Iterator<C>, Iterator<V>> f;
   private RDD<A> rdd1;
   private RDD<B> rdd2;
   private RDD<C> rdd3;

   public static <A, B, C, V> boolean $lessinit$greater$default$6() {
      return ZippedPartitionsRDD3$.MODULE$.$lessinit$greater$default$6();
   }

   public Function3<Iterator<A>, Iterator<B>, Iterator<C>, Iterator<V>> f() {
      return this.f;
   }

   public void f_$eq(Function3<Iterator<A>, Iterator<B>, Iterator<C>, Iterator<V>> x$1) {
      this.f = x$1;
   }

   public RDD<A> rdd1() {
      return this.rdd1;
   }

   public void rdd1_$eq(RDD<A> x$1) {
      this.rdd1 = x$1;
   }

   public RDD<B> rdd2() {
      return this.rdd2;
   }

   public void rdd2_$eq(RDD<B> x$1) {
      this.rdd2 = x$1;
   }

   public RDD<C> rdd3() {
      return this.rdd3;
   }

   public void rdd3_$eq(RDD<C> x$1) {
      this.rdd3 = x$1;
   }

   public Iterator<V> compute(Partition s, TaskContext context) {
      Seq partitions = ((ZippedPartitionsPartition)s).partitions();
      return (Iterator)this.f().apply(this.rdd1().iterator((Partition)partitions.apply(0), context), this.rdd2().iterator((Partition)partitions.apply(1), context), this.rdd3().iterator((Partition)partitions.apply(2), context));
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.rdd1_$eq((RDD)null);
      this.rdd2_$eq((RDD)null);
      this.rdd3_$eq((RDD)null);
      this.f_$eq((Function3)null);
   }

   public ZippedPartitionsRDD3(SparkContext sc, Function3<Iterator<A>, Iterator<B>, Iterator<C>, Iterator<V>> f, RDD<A> rdd1, RDD<B> rdd2, RDD<C> rdd3, boolean preservesPartitioning, ClassTag<A> evidence$5, ClassTag<B> evidence$6, ClassTag<C> evidence$7, ClassTag<V> evidence$8) {
      this.f = f;
      this.rdd1 = rdd1;
      this.rdd2 = rdd2;
      this.rdd3 = rdd3;
      super(sc, List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDD[]{rdd1, rdd2, rdd3})), preservesPartitioning, evidence$8);
   }
}
