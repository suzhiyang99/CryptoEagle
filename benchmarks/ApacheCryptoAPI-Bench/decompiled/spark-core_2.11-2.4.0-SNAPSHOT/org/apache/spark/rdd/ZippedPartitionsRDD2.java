package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import scala.Function2;
import scala.Predef$;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.List$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ed!B\u0001\u0003\u0001\u0011Q!\u0001\u0006.jaB,G\rU1si&$\u0018n\u001c8t%\u0012#%G\u0003\u0002\u0004\t\u0005\u0019!\u000f\u001a3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e,Ba\u0003\u001d=%M\u0011\u0001\u0001\u0004\t\u0004\u001b9\u0001R\"\u0001\u0002\n\u0005=\u0011!a\u0006.jaB,G\rU1si&$\u0018n\u001c8t\u0005\u0006\u001cXM\u0015#E!\t\t\"\u0003\u0004\u0001\u0005\u000bM\u0001!\u0019A\u000b\u0003\u0003Y\u001b\u0001!\u0005\u0002\u00179A\u0011qCG\u0007\u00021)\t\u0011$A\u0003tG\u0006d\u0017-\u0003\u0002\u001c1\t9aj\u001c;iS:<\u0007CA\f\u001e\u0013\tq\u0002DA\u0002B]fD\u0001\u0002\t\u0001\u0003\u0002\u0003\u0006I!I\u0001\u0003g\u000e\u0004\"AI\u0012\u000e\u0003\u0011I!\u0001\n\u0003\u0003\u0019M\u0003\u0018M]6D_:$X\r\u001f;\t\u0011\u0019\u0002!\u00111A\u0005\u0002\u001d\n\u0011AZ\u000b\u0002QA)q#K\u0016;}%\u0011!\u0006\u0007\u0002\n\rVt7\r^5p]J\u00022\u0001\f\u001b8\u001d\ti#G\u0004\u0002/c5\tqF\u0003\u00021)\u00051AH]8pizJ\u0011!G\u0005\u0003ga\tq\u0001]1dW\u0006<W-\u0003\u00026m\tA\u0011\n^3sCR|'O\u0003\u000241A\u0011\u0011\u0003\u000f\u0003\u0006s\u0001\u0011\r!\u0006\u0002\u0002\u0003B\u0019A\u0006N\u001e\u0011\u0005EaD!B\u001f\u0001\u0005\u0004)\"!\u0001\"\u0011\u00071\"\u0004\u0003\u0003\u0005A\u0001\t\u0005\r\u0011\"\u0001B\u0003\u00151w\fJ3r)\t\u0011U\t\u0005\u0002\u0018\u0007&\u0011A\t\u0007\u0002\u0005+:LG\u000fC\u0004G\u007f\u0005\u0005\t\u0019\u0001\u0015\u0002\u0007a$\u0013\u0007\u0003\u0005I\u0001\t\u0005\t\u0015)\u0003)\u0003\t1\u0007\u0005\u0003\u0005K\u0001\t\u0005\r\u0011\"\u0001L\u0003\u0011\u0011H\rZ\u0019\u0016\u00031\u00032!D'8\u0013\tq%AA\u0002S\t\u0012C\u0001\u0002\u0015\u0001\u0003\u0002\u0004%\t!U\u0001\te\u0012$\u0017g\u0018\u0013fcR\u0011!I\u0015\u0005\b\r>\u000b\t\u00111\u0001M\u0011!!\u0006A!A!B\u0013a\u0015!\u0002:eIF\u0002\u0003\u0002\u0003,\u0001\u0005\u0003\u0007I\u0011A,\u0002\tI$GMM\u000b\u00021B\u0019Q\"T\u001e\t\u0011i\u0003!\u00111A\u0005\u0002m\u000b\u0001B\u001d3ee}#S-\u001d\u000b\u0003\u0005rCqAR-\u0002\u0002\u0003\u0007\u0001\f\u0003\u0005_\u0001\t\u0005\t\u0015)\u0003Y\u0003\u0015\u0011H\r\u001a\u001a!\u0011!\u0001\u0007A!A!\u0002\u0013\t\u0017!\u00069sKN,'O^3t!\u0006\u0014H/\u001b;j_:Lgn\u001a\t\u0003/\tL!a\u0019\r\u0003\u000f\t{w\u000e\\3b]\"AQ\r\u0001B\u0002B\u0003-a-\u0001\u0006fm&$WM\\2fII\u00022a\u001a68\u001b\u0005A'BA5\u0019\u0003\u001d\u0011XM\u001a7fGRL!a\u001b5\u0003\u0011\rc\u0017m]:UC\u001eD\u0001\"\u001c\u0001\u0003\u0004\u0003\u0006YA\\\u0001\u000bKZLG-\u001a8dK\u0012\u001a\u0004cA4kw!A\u0001\u000f\u0001B\u0002B\u0003-\u0011/\u0001\u0006fm&$WM\\2fIQ\u00022a\u001a6\u0011\u0011\u0015\u0019\b\u0001\"\u0001u\u0003\u0019a\u0014N\\5u}Q1QO_>}{z$BA^<ysB)Q\u0002A\u001c<!!)QM\u001da\u0002M\")QN\u001da\u0002]\")\u0001O\u001da\u0002c\")\u0001E\u001da\u0001C!)aE\u001da\u0001Q!)!J\u001da\u0001\u0019\")aK\u001da\u00011\"9\u0001M\u001dI\u0001\u0002\u0004\t\u0007bBA\u0001\u0001\u0011\u0005\u00131A\u0001\bG>l\u0007/\u001e;f)\u0015q\u0014QAA\b\u0011\u001d\t9a a\u0001\u0003\u0013\t\u0011a\u001d\t\u0004E\u0005-\u0011bAA\u0007\t\tI\u0001+\u0019:uSRLwN\u001c\u0005\b\u0003#y\b\u0019AA\n\u0003\u001d\u0019wN\u001c;fqR\u00042AIA\u000b\u0013\r\t9\u0002\u0002\u0002\f)\u0006\u001c8nQ8oi\u0016DH\u000fC\u0004\u0002\u001c\u0001!\t%!\b\u0002#\rdW-\u0019:EKB,g\u000eZ3oG&,7\u000fF\u0001C\u000f)\t\tCAA\u0001\u0012\u0003!\u00111E\u0001\u00155&\u0004\b/\u001a3QCJ$\u0018\u000e^5p]N\u0014F\t\u0012\u001a\u0011\u00075\t)CB\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0003\u0002(M1\u0011QEA\u0015\u0003_\u00012aFA\u0016\u0013\r\ti\u0003\u0007\u0002\u0007\u0003:L(+\u001a4\u0011\u0007]\t\t$C\u0002\u00024a\u0011AbU3sS\u0006d\u0017N_1cY\u0016Dqa]A\u0013\t\u0003\t9\u0004\u0006\u0002\u0002$!Q\u00111HA\u0013#\u0003%\t!!\u0010\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00136+!\ty$!\u0016\u0002X\u0005eSCAA!U\r\t\u00171I\u0016\u0003\u0003\u000b\u0002B!a\u0012\u0002R5\u0011\u0011\u0011\n\u0006\u0005\u0003\u0017\ni%A\u0005v]\u000eDWmY6fI*\u0019\u0011q\n\r\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002T\u0005%#!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u00121\u0011(!\u000fC\u0002U!a!PA\u001d\u0005\u0004)BAB\n\u0002:\t\u0007Q\u0003\u0003\u0006\u0002^\u0005\u0015\u0012\u0011!C\u0005\u0003?\n1B]3bIJ+7o\u001c7wKR\u0011\u0011\u0011\r\t\u0005\u0003G\ni'\u0004\u0002\u0002f)!\u0011qMA5\u0003\u0011a\u0017M\\4\u000b\u0005\u0005-\u0014\u0001\u00026bm\u0006LA!a\u001c\u0002f\t1qJ\u00196fGR\u0004"
)
public class ZippedPartitionsRDD2<A, B, V> extends ZippedPartitionsBaseRDD<V> {
   private Function2<Iterator<A>, Iterator<B>, Iterator<V>> f;
   private RDD<A> rdd1;
   private RDD<B> rdd2;

   public static <A, B, V> boolean $lessinit$greater$default$5() {
      return ZippedPartitionsRDD2$.MODULE$.$lessinit$greater$default$5();
   }

   public Function2<Iterator<A>, Iterator<B>, Iterator<V>> f() {
      return this.f;
   }

   public void f_$eq(Function2<Iterator<A>, Iterator<B>, Iterator<V>> x$1) {
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

   public Iterator<V> compute(Partition s, TaskContext context) {
      Seq partitions = ((ZippedPartitionsPartition)s).partitions();
      return (Iterator)this.f().apply(this.rdd1().iterator((Partition)partitions.apply(0), context), this.rdd2().iterator((Partition)partitions.apply(1), context));
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.rdd1_$eq((RDD)null);
      this.rdd2_$eq((RDD)null);
      this.f_$eq((Function2)null);
   }

   public ZippedPartitionsRDD2(SparkContext sc, Function2<Iterator<A>, Iterator<B>, Iterator<V>> f, RDD<A> rdd1, RDD<B> rdd2, boolean preservesPartitioning, ClassTag<A> evidence$2, ClassTag<B> evidence$3, ClassTag<V> evidence$4) {
      this.f = f;
      this.rdd1 = rdd1;
      this.rdd2 = rdd2;
      super(sc, List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDD[]{rdd1, rdd2})), preservesPartitioning, evidence$4);
   }
}
