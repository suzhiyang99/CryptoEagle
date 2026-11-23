package org.apache.spark.api.java;

import java.util.Iterator;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.rdd.HadoopRDD;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005=a\u0001B\u0001\u0003\u00015\u0011QBS1wC\"\u000bGm\\8q%\u0012#%BA\u0002\u0005\u0003\u0011Q\u0017M^1\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0016\u00079)\"e\u0005\u0002\u0001\u001fA!\u0001#E\n\"\u001b\u0005\u0011\u0011B\u0001\n\u0003\u0005-Q\u0015M^1QC&\u0014(\u000b\u0012#\u0011\u0005Q)B\u0002\u0001\u0003\u0006-\u0001\u0011\ra\u0006\u0002\u0002\u0017F\u0011\u0001D\b\t\u00033qi\u0011A\u0007\u0006\u00027\u0005)1oY1mC&\u0011QD\u0007\u0002\b\u001d>$\b.\u001b8h!\tIr$\u0003\u0002!5\t\u0019\u0011I\\=\u0011\u0005Q\u0011C!B\u0012\u0001\u0005\u00049\"!\u0001,\t\u0013\u0015\u0002!\u0011!Q\u0001\n\u0019Z\u0013a\u0001:eIB!q%K\n\"\u001b\u0005A#BA\u0013\u0007\u0013\tQ\u0003FA\u0005IC\u0012|w\u000e\u001d*E\t&\u0011Q%\u0005\u0005\t[\u0001\u0011)\u0019!C\"]\u0005I1n\u00117bgN$\u0016mZ\u000b\u0002_A\u0019\u0001gM\n\u000e\u0003ER!A\r\u000e\u0002\u000fI,g\r\\3di&\u0011A'\r\u0002\t\u00072\f7o\u001d+bO\"Ia\u0007\u0001B\u0001B\u0003%qfN\u0001\u000bW\u000ec\u0017m]:UC\u001e\u0004\u0013BA\u0017\u0012\u0011!I\u0004A!b\u0001\n\u0007R\u0014!\u0003<DY\u0006\u001c8\u000fV1h+\u0005Y\u0004c\u0001\u00194C!IQ\b\u0001B\u0001B\u0003%1HP\u0001\u000bm\u000ec\u0017m]:UC\u001e\u0004\u0013BA\u001d\u0012\u0011\u0015\u0001\u0005\u0001\"\u0001B\u0003\u0019a\u0014N\\5u}Q\u0011!I\u0012\u000b\u0004\u0007\u0012+\u0005\u0003\u0002\t\u0001'\u0005BQ!L A\u0004=BQ!O A\u0004mBQ!J A\u0002\u0019BQ\u0001\u0013\u0001\u0005\u0002%\u000b1$\\1q!\u0006\u0014H/\u001b;j_:\u001cx+\u001b;i\u0013:\u0004X\u000f^*qY&$XC\u0001&P)\rY\u0015\u000b\u001c\t\u0004!1s\u0015BA'\u0003\u0005\u001dQ\u0015M^1S\t\u0012\u0003\"\u0001F(\u0005\u000bA;%\u0019A\f\u0003\u0003ICQAU$A\u0002M\u000b\u0011A\u001a\t\u0006)^K\u0016m[\u0007\u0002+*\u0011aKA\u0001\tMVt7\r^5p]&\u0011\u0001,\u0016\u0002\n\rVt7\r^5p]J\u0002\"AW0\u000e\u0003mS!\u0001X/\u0002\r5\f\u0007O]3e\u0015\tq\u0006\"\u0001\u0004iC\u0012|w\u000e]\u0005\u0003An\u0013!\"\u00138qkR\u001c\u0006\u000f\\5u!\r\u0011g\r[\u0007\u0002G*\u0011A-Z\u0001\u0005kRLGNC\u0001\u0004\u0013\t97M\u0001\u0005Ji\u0016\u0014\u0018\r^8s!\u0011I\u0012nE\u0011\n\u0005)T\"A\u0002+va2,'\u0007E\u0002cM:Cq!\\$\u0011\u0002\u0003\u0007a.A\u000bqe\u0016\u001cXM\u001d<fgB\u000b'\u000f^5uS>t\u0017N\\4\u0011\u0005ey\u0017B\u00019\u001b\u0005\u001d\u0011un\u001c7fC:D#a\u0012:\u0011\u0005M4X\"\u0001;\u000b\u0005U4\u0011AC1o]>$\u0018\r^5p]&\u0011q\u000f\u001e\u0002\r\t\u00164X\r\\8qKJ\f\u0005/\u001b\u0005\bs\u0002\t\n\u0011\"\u0001{\u0003\u0015j\u0017\r\u001d)beRLG/[8og^KG\u000f[%oaV$8\u000b\u001d7ji\u0012\"WMZ1vYR$#'F\u0002|\u0003\u0017)\u0012\u0001 \u0016\u0003]v\\\u0013A \t\u0004\u007f\u0006\u001dQBAA\u0001\u0015\u0011\t\u0019!!\u0002\u0002\u0013Ut7\r[3dW\u0016$'BA;\u001b\u0013\u0011\tI!!\u0001\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rB\u0003Qq\n\u0007q\u0003\u000b\u0002\u0001e\u0002"
)
public class JavaHadoopRDD<K, V> extends JavaPairRDD<K, V> {
   public ClassTag<K> kClassTag() {
      return super.kClassTag();
   }

   public ClassTag<V> vClassTag() {
      return super.vClassTag();
   }

   @DeveloperApi
   public <R> JavaRDD<R> mapPartitionsWithInputSplit(Function2<InputSplit, Iterator<Tuple2<K, V>>, Iterator<R>> f, boolean preservesPartitioning) {
      return new JavaRDD<R>(((HadoopRDD)super.rdd()).mapPartitionsWithInputSplit(new Serializable(this, f) {
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

   public JavaHadoopRDD(HadoopRDD<K, V> rdd, ClassTag<K> kClassTag, ClassTag<V> vClassTag) {
      super(rdd, kClassTag, vClassTag);
   }
}
