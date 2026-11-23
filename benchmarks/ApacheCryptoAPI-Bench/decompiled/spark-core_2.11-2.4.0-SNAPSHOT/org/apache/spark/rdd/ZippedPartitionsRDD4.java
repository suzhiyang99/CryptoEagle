package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import scala.Function4;
import scala.Predef$;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.List$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005g!B\u0001\u0003\u0001\u0011Q!\u0001\u0006.jaB,G\rU1si&$\u0018n\u001c8t%\u0012#EG\u0003\u0002\u0004\t\u0005\u0019!\u000f\u001a3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e,ba\u0003\u001d=\u0001\u0012\u00132C\u0001\u0001\r!\ria\u0002E\u0007\u0002\u0005%\u0011qB\u0001\u0002\u00185&\u0004\b/\u001a3QCJ$\u0018\u000e^5p]N\u0014\u0015m]3S\t\u0012\u0003\"!\u0005\n\r\u0001\u0011)1\u0003\u0001b\u0001+\t\tak\u0001\u0001\u0012\u0005Ya\u0002CA\f\u001b\u001b\u0005A\"\"A\r\u0002\u000bM\u001c\u0017\r\\1\n\u0005mA\"a\u0002(pi\"Lgn\u001a\t\u0003/uI!A\b\r\u0003\u0007\u0005s\u0017\u0010\u0003\u0005!\u0001\t\u0005\t\u0015!\u0003\"\u0003\t\u00198\r\u0005\u0002#G5\tA!\u0003\u0002%\t\ta1\u000b]1sW\u000e{g\u000e^3yi\"Aa\u0005\u0001BA\u0002\u0013\u0005q%A\u0001g+\u0005A\u0003cB\f*Wir$IR\u0005\u0003Ua\u0011\u0011BR;oGRLwN\u001c\u001b\u0011\u00071\"tG\u0004\u0002.e9\u0011a&M\u0007\u0002_)\u0011\u0001\u0007F\u0001\u0007yI|w\u000e\u001e \n\u0003eI!a\r\r\u0002\u000fA\f7m[1hK&\u0011QG\u000e\u0002\t\u0013R,'/\u0019;pe*\u00111\u0007\u0007\t\u0003#a\"Q!\u000f\u0001C\u0002U\u0011\u0011!\u0011\t\u0004YQZ\u0004CA\t=\t\u0015i\u0004A1\u0001\u0016\u0005\u0005\u0011\u0005c\u0001\u00175\u007fA\u0011\u0011\u0003\u0011\u0003\u0006\u0003\u0002\u0011\r!\u0006\u0002\u0002\u0007B\u0019A\u0006N\"\u0011\u0005E!E!B#\u0001\u0005\u0004)\"!\u0001#\u0011\u00071\"\u0004\u0003\u0003\u0005I\u0001\t\u0005\r\u0011\"\u0001J\u0003\u00151w\fJ3r)\tQU\n\u0005\u0002\u0018\u0017&\u0011A\n\u0007\u0002\u0005+:LG\u000fC\u0004O\u000f\u0006\u0005\t\u0019\u0001\u0015\u0002\u0007a$\u0013\u0007\u0003\u0005Q\u0001\t\u0005\t\u0015)\u0003)\u0003\t1\u0007\u0005\u0003\u0005S\u0001\t\u0005\r\u0011\"\u0001T\u0003\u0011\u0011H\rZ\u0019\u0016\u0003Q\u00032!D+8\u0013\t1&AA\u0002S\t\u0012C\u0001\u0002\u0017\u0001\u0003\u0002\u0004%\t!W\u0001\te\u0012$\u0017g\u0018\u0013fcR\u0011!J\u0017\u0005\b\u001d^\u000b\t\u00111\u0001U\u0011!a\u0006A!A!B\u0013!\u0016!\u0002:eIF\u0002\u0003\u0002\u00030\u0001\u0005\u0003\u0007I\u0011A0\u0002\tI$GMM\u000b\u0002AB\u0019Q\"V\u001e\t\u0011\t\u0004!\u00111A\u0005\u0002\r\f\u0001B\u001d3ee}#S-\u001d\u000b\u0003\u0015\u0012DqAT1\u0002\u0002\u0003\u0007\u0001\r\u0003\u0005g\u0001\t\u0005\t\u0015)\u0003a\u0003\u0015\u0011H\r\u001a\u001a!\u0011!A\u0007A!a\u0001\n\u0003I\u0017\u0001\u0002:eIN*\u0012A\u001b\t\u0004\u001bU{\u0004\u0002\u00037\u0001\u0005\u0003\u0007I\u0011A7\u0002\u0011I$GmM0%KF$\"A\u00138\t\u000f9[\u0017\u0011!a\u0001U\"A\u0001\u000f\u0001B\u0001B\u0003&!.A\u0003sI\u0012\u001c\u0004\u0005\u0003\u0005s\u0001\t\u0005\r\u0011\"\u0001t\u0003\u0011\u0011H\r\u001a\u001b\u0016\u0003Q\u00042!D+D\u0011!1\bA!a\u0001\n\u00039\u0018\u0001\u0003:eIRzF%Z9\u0015\u0005)C\bb\u0002(v\u0003\u0003\u0005\r\u0001\u001e\u0005\tu\u0002\u0011\t\u0011)Q\u0005i\u0006)!\u000f\u001a35A!AA\u0010\u0001B\u0001B\u0003%Q0A\u000bqe\u0016\u001cXM\u001d<fgB\u000b'\u000f^5uS>t\u0017N\\4\u0011\u0005]q\u0018BA@\u0019\u0005\u001d\u0011un\u001c7fC:D!\"a\u0001\u0001\u0005\u0007\u0005\u000b1BA\u0003\u0003))g/\u001b3f]\u000e,G%\u000f\t\u0006\u0003\u000f\tiaN\u0007\u0003\u0003\u0013Q1!a\u0003\u0019\u0003\u001d\u0011XM\u001a7fGRLA!a\u0004\u0002\n\tA1\t\\1tgR\u000bw\r\u0003\u0006\u0002\u0014\u0001\u0011\u0019\u0011)A\u0006\u0003+\t1\"\u001a<jI\u0016t7-\u001a\u00132aA)\u0011qAA\u0007w!Q\u0011\u0011\u0004\u0001\u0003\u0004\u0003\u0006Y!a\u0007\u0002\u0017\u00154\u0018\u000eZ3oG\u0016$\u0013'\r\t\u0006\u0003\u000f\tia\u0010\u0005\u000b\u0003?\u0001!1!Q\u0001\f\u0005\u0005\u0012aC3wS\u0012,gnY3%cI\u0002R!a\u0002\u0002\u000e\rC!\"!\n\u0001\u0005\u0007\u0005\u000b1BA\u0014\u0003-)g/\u001b3f]\u000e,G%M\u001a\u0011\u000b\u0005\u001d\u0011Q\u0002\t\t\u000f\u0005-\u0002\u0001\"\u0001\u0002.\u00051A(\u001b8jiz\"\u0002#a\f\u0002>\u0005}\u0012\u0011IA\"\u0003\u000b\n9%!\u0013\u0015\u0019\u0005E\u00121GA\u001b\u0003o\tI$a\u000f\u0011\u000f5\u0001qgO D!!A\u00111AA\u0015\u0001\b\t)\u0001\u0003\u0005\u0002\u0014\u0005%\u00029AA\u000b\u0011!\tI\"!\u000bA\u0004\u0005m\u0001\u0002CA\u0010\u0003S\u0001\u001d!!\t\t\u0011\u0005\u0015\u0012\u0011\u0006a\u0002\u0003OAa\u0001IA\u0015\u0001\u0004\t\u0003B\u0002\u0014\u0002*\u0001\u0007\u0001\u0006\u0003\u0004S\u0003S\u0001\r\u0001\u0016\u0005\u0007=\u0006%\u0002\u0019\u00011\t\r!\fI\u00031\u0001k\u0011\u0019\u0011\u0018\u0011\u0006a\u0001i\"AA0!\u000b\u0011\u0002\u0003\u0007Q\u0010C\u0004\u0002N\u0001!\t%a\u0014\u0002\u000f\r|W\u000e];uKR)a)!\u0015\u0002\\!A\u00111KA&\u0001\u0004\t)&A\u0001t!\r\u0011\u0013qK\u0005\u0004\u00033\"!!\u0003)beRLG/[8o\u0011!\ti&a\u0013A\u0002\u0005}\u0013aB2p]R,\u0007\u0010\u001e\t\u0004E\u0005\u0005\u0014bAA2\t\tYA+Y:l\u0007>tG/\u001a=u\u0011\u001d\t9\u0007\u0001C!\u0003S\n\u0011c\u00197fCJ$U\r]3oI\u0016t7-[3t)\u0005QuACA7\u0005\u0005\u0005\t\u0012\u0001\u0003\u0002p\u0005!\",\u001b9qK\u0012\u0004\u0016M\u001d;ji&|gn\u001d*E\tR\u00022!DA9\r%\t!!!A\t\u0002\u0011\t\u0019h\u0005\u0004\u0002r\u0005U\u00141\u0010\t\u0004/\u0005]\u0014bAA=1\t1\u0011I\\=SK\u001a\u00042aFA?\u0013\r\ty\b\u0007\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t\u0003W\t\t\b\"\u0001\u0002\u0004R\u0011\u0011q\u000e\u0005\u000b\u0003\u000f\u000b\t(%A\u0005\u0002\u0005%\u0015a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$s'\u0006\u0007\u0002\f\u0006\u0005\u00161UAS\u0003O\u000bI+\u0006\u0002\u0002\u000e*\u001aQ0a$,\u0005\u0005E\u0005\u0003BAJ\u0003;k!!!&\u000b\t\u0005]\u0015\u0011T\u0001\nk:\u001c\u0007.Z2lK\u0012T1!a'\u0019\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003?\u000b)JA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016$a!OAC\u0005\u0004)BAB\u001f\u0002\u0006\n\u0007Q\u0003\u0002\u0004B\u0003\u000b\u0013\r!\u0006\u0003\u0007\u000b\u0006\u0015%\u0019A\u000b\u0005\rM\t)I1\u0001\u0016\u0011)\ti+!\u001d\u0002\u0002\u0013%\u0011qV\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u00022B!\u00111WA_\u001b\t\t)L\u0003\u0003\u00028\u0006e\u0016\u0001\u00027b]\u001eT!!a/\u0002\t)\fg/Y\u0005\u0005\u0003\u007f\u000b)L\u0001\u0004PE*,7\r\u001e"
)
public class ZippedPartitionsRDD4<A, B, C, D, V> extends ZippedPartitionsBaseRDD<V> {
   private Function4<Iterator<A>, Iterator<B>, Iterator<C>, Iterator<D>, Iterator<V>> f;
   private RDD<A> rdd1;
   private RDD<B> rdd2;
   private RDD<C> rdd3;
   private RDD<D> rdd4;

   public static <A, B, C, D, V> boolean $lessinit$greater$default$7() {
      return ZippedPartitionsRDD4$.MODULE$.$lessinit$greater$default$7();
   }

   public Function4<Iterator<A>, Iterator<B>, Iterator<C>, Iterator<D>, Iterator<V>> f() {
      return this.f;
   }

   public void f_$eq(Function4<Iterator<A>, Iterator<B>, Iterator<C>, Iterator<D>, Iterator<V>> x$1) {
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

   public RDD<D> rdd4() {
      return this.rdd4;
   }

   public void rdd4_$eq(RDD<D> x$1) {
      this.rdd4 = x$1;
   }

   public Iterator<V> compute(Partition s, TaskContext context) {
      Seq partitions = ((ZippedPartitionsPartition)s).partitions();
      return (Iterator)this.f().apply(this.rdd1().iterator((Partition)partitions.apply(0), context), this.rdd2().iterator((Partition)partitions.apply(1), context), this.rdd3().iterator((Partition)partitions.apply(2), context), this.rdd4().iterator((Partition)partitions.apply(3), context));
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.rdd1_$eq((RDD)null);
      this.rdd2_$eq((RDD)null);
      this.rdd3_$eq((RDD)null);
      this.rdd4_$eq((RDD)null);
      this.f_$eq((Function4)null);
   }

   public ZippedPartitionsRDD4(SparkContext sc, Function4<Iterator<A>, Iterator<B>, Iterator<C>, Iterator<D>, Iterator<V>> f, RDD<A> rdd1, RDD<B> rdd2, RDD<C> rdd3, RDD<D> rdd4, boolean preservesPartitioning, ClassTag<A> evidence$9, ClassTag<B> evidence$10, ClassTag<C> evidence$11, ClassTag<D> evidence$12, ClassTag<V> evidence$13) {
      this.f = f;
      this.rdd1 = rdd1;
      this.rdd2 = rdd2;
      this.rdd3 = rdd3;
      this.rdd4 = rdd4;
      super(sc, List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDD[]{rdd1, rdd2, rdd3, rdd4})), preservesPartitioning, evidence$13);
   }
}
