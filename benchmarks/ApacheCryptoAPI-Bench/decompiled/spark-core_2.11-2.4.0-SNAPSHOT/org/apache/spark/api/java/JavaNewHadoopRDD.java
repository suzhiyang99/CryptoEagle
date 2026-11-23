package org.apache.spark.api.java;

import java.util.Iterator;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.rdd.NewHadoopRDD;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005=a\u0001B\u0001\u0003\u00015\u0011\u0001CS1wC:+w\u000fS1e_>\u0004(\u000b\u0012#\u000b\u0005\r!\u0011\u0001\u00026bm\u0006T!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001)2AD\u000b#'\t\u0001q\u0002\u0005\u0003\u0011#M\tS\"\u0001\u0002\n\u0005I\u0011!a\u0003&bm\u0006\u0004\u0016-\u001b:S\t\u0012\u0003\"\u0001F\u000b\r\u0001\u0011)a\u0003\u0001b\u0001/\t\t1*\u0005\u0002\u0019=A\u0011\u0011\u0004H\u0007\u00025)\t1$A\u0003tG\u0006d\u0017-\u0003\u0002\u001e5\t9aj\u001c;iS:<\u0007CA\r \u0013\t\u0001#DA\u0002B]f\u0004\"\u0001\u0006\u0012\u0005\u000b\r\u0002!\u0019A\f\u0003\u0003YC\u0011\"\n\u0001\u0003\u0002\u0003\u0006IAJ\u0016\u0002\u0007I$G\r\u0005\u0003(SM\tS\"\u0001\u0015\u000b\u0005\u00152\u0011B\u0001\u0016)\u00051qUm\u001e%bI>|\u0007O\u0015#E\u0013\t)\u0013\u0003\u0003\u0005.\u0001\t\u0015\r\u0011b\u0011/\u0003%Y7\t\\1tgR\u000bw-F\u00010!\r\u00014gE\u0007\u0002c)\u0011!GG\u0001\be\u00164G.Z2u\u0013\t!\u0014G\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011%1\u0004A!A!\u0002\u0013ys'\u0001\u0006l\u00072\f7o\u001d+bO\u0002J!!L\t\t\u0011e\u0002!Q1A\u0005Di\n\u0011B^\"mCN\u001cH+Y4\u0016\u0003m\u00022\u0001M\u001a\"\u0011%i\u0004A!A!\u0002\u0013Yd(\u0001\u0006w\u00072\f7o\u001d+bO\u0002J!!O\t\t\u000b\u0001\u0003A\u0011A!\u0002\rqJg.\u001b;?)\t\u0011e\tF\u0002D\t\u0016\u0003B\u0001\u0005\u0001\u0014C!)Qf\u0010a\u0002_!)\u0011h\u0010a\u0002w!)Qe\u0010a\u0001M!)\u0001\n\u0001C\u0001\u0013\u0006YR.\u00199QCJ$\u0018\u000e^5p]N<\u0016\u000e\u001e5J]B,Ho\u00159mSR,\"AS(\u0015\u0007-\u000bF\u000eE\u0002\u0011\u0019:K!!\u0014\u0002\u0003\u000f)\u000bg/\u0019*E\tB\u0011Ac\u0014\u0003\u0006!\u001e\u0013\ra\u0006\u0002\u0002%\")!k\u0012a\u0001'\u0006\ta\rE\u0003U/f\u000b7.D\u0001V\u0015\t1&!\u0001\u0005gk:\u001cG/[8o\u0013\tAVKA\u0005Gk:\u001cG/[8oeA\u0011!lX\u0007\u00027*\u0011A,X\u0001\n[\u0006\u0004(/\u001a3vG\u0016T!A\u0018\u0005\u0002\r!\fGm\\8q\u0013\t\u00017L\u0001\u0006J]B,Ho\u00159mSR\u00042A\u00194i\u001b\u0005\u0019'B\u00013f\u0003\u0011)H/\u001b7\u000b\u0003\rI!aZ2\u0003\u0011%#XM]1u_J\u0004B!G5\u0014C%\u0011!N\u0007\u0002\u0007)V\u0004H.\u001a\u001a\u0011\u0007\t4g\nC\u0004n\u000fB\u0005\t\u0019\u00018\u0002+A\u0014Xm]3sm\u0016\u001c\b+\u0019:uSRLwN\\5oOB\u0011\u0011d\\\u0005\u0003aj\u0011qAQ8pY\u0016\fg\u000e\u000b\u0002HeB\u00111O^\u0007\u0002i*\u0011QOB\u0001\u000bC:tw\u000e^1uS>t\u0017BA<u\u00051!UM^3m_B,'/\u00119j\u0011\u001dI\b!%A\u0005\u0002i\fQ%\\1q!\u0006\u0014H/\u001b;j_:\u001cx+\u001b;i\u0013:\u0004X\u000f^*qY&$H\u0005Z3gCVdG\u000f\n\u001a\u0016\u0007m\fY!F\u0001}U\tqWpK\u0001\u007f!\ry\u0018qA\u0007\u0003\u0003\u0003QA!a\u0001\u0002\u0006\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0003kjIA!!\u0003\u0002\u0002\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\u0005\u000bAC(\u0019A\f)\u0005\u0001\u0011\b"
)
public class JavaNewHadoopRDD<K, V> extends JavaPairRDD<K, V> {
   public ClassTag<K> kClassTag() {
      return super.kClassTag();
   }

   public ClassTag<V> vClassTag() {
      return super.vClassTag();
   }

   @DeveloperApi
   public <R> JavaRDD<R> mapPartitionsWithInputSplit(Function2<InputSplit, Iterator<Tuple2<K, V>>, Iterator<R>> f, boolean preservesPartitioning) {
      return new JavaRDD<R>(((NewHadoopRDD)super.rdd()).mapPartitionsWithInputSplit(new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         private final Function2 f$1;

         public final scala.collection.Iterator<R> apply(InputSplit a, scala.collection.Iterator<Tuple2<K, V>> b) {
            return (scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter((Iterator)this.f$1.call(a, JavaConverters$.MODULE$.asJavaIteratorConverter(b).asJava())).asScala();
         }

         public {
            this.f$1 = f$1;
         }
      }, preservesPartitioning, JavaSparkContext$.MODULE$.fakeClassTag()), JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public <R> boolean mapPartitionsWithInputSplit$default$2() {
      return false;
   }

   public JavaNewHadoopRDD(NewHadoopRDD<K, V> rdd, ClassTag<K> kClassTag, ClassTag<V> vClassTag) {
      super(rdd, kClassTag, vClassTag);
   }
}
