package org.apache.spark.api.r;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaPairRDD$;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.rdd.RDD;
import scala.Array$;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u000154A!\u0001\u0002\u0005\u001b\ta\u0001+Y5so&\u001cXM\u0015*E\t*\u00111\u0001B\u0001\u0002e*\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001U\u0011a\"F\n\u0003\u0001=\u0001B\u0001E\t\u0014C5\t!!\u0003\u0002\u0013\u0005\tA!)Y:f%J#E\t\u0005\u0002\u0015+1\u0001A!\u0002\f\u0001\u0005\u00049\"!\u0001+\u0012\u0005aq\u0002CA\r\u001d\u001b\u0005Q\"\"A\u000e\u0002\u000bM\u001c\u0017\r\\1\n\u0005uQ\"a\u0002(pi\"Lgn\u001a\t\u00033}I!\u0001\t\u000e\u0003\u0007\u0005s\u0017\u0010\u0005\u0003\u001aE\u0011:\u0013BA\u0012\u001b\u0005\u0019!V\u000f\u001d7feA\u0011\u0011$J\u0005\u0003Mi\u00111!\u00138u!\rI\u0002FK\u0005\u0003Si\u0011Q!\u0011:sCf\u0004\"!G\u0016\n\u00051R\"\u0001\u0002\"zi\u0016D\u0001B\f\u0001\u0003\u0002\u0003\u0006IaL\u0001\u0007a\u0006\u0014XM\u001c;\u0011\u0007A\u001a4#D\u00012\u0015\t\u0011d!A\u0002sI\u0012L!\u0001N\u0019\u0003\u0007I#E\t\u0003\u00057\u0001\t\u0005\t\u0015!\u0003%\u00035qW/\u001c)beRLG/[8og\"A\u0001\b\u0001B\u0001B\u0003%q%\u0001\u0005iCNDg)\u001e8d\u0011!Q\u0004A!A!\u0002\u0013Y\u0014\u0001\u00043fg\u0016\u0014\u0018.\u00197ju\u0016\u0014\bC\u0001\u001f@\u001d\tIR(\u0003\u0002?5\u00051\u0001K]3eK\u001aL!\u0001Q!\u0003\rM#(/\u001b8h\u0015\tq$\u0004\u0003\u0005D\u0001\t\u0005\t\u0015!\u0003(\u00031\u0001\u0018mY6bO\u0016t\u0015-\\3t\u0011!)\u0005A!A!\u0002\u00131\u0015!\u00042s_\u0006$7-Y:u-\u0006\u00148\u000fE\u0002\u001aQ\u001d\u0003\"\u0001S'\u000e\u0003%S!AS&\u0002\t1\fgn\u001a\u0006\u0002\u0019\u0006!!.\u0019<b\u0013\tq\u0015J\u0001\u0004PE*,7\r\u001e\u0005\t!\u0002\u0011\u0019\u0011)A\u0006#\u0006QQM^5eK:\u001cW\rJ\u001a\u0011\u0007I+6#D\u0001T\u0015\t!&$A\u0004sK\u001adWm\u0019;\n\u0005Y\u001b&\u0001C\"mCN\u001cH+Y4\t\u000ba\u0003A\u0011A-\u0002\rqJg.\u001b;?)\u001dQVLX0aC\n$\"a\u0017/\u0011\u0007A\u00011\u0003C\u0003Q/\u0002\u000f\u0011\u000bC\u0003//\u0002\u0007q\u0006C\u00037/\u0002\u0007A\u0005C\u00039/\u0002\u0007q\u0005C\u0003;/\u0002\u00071\bC\u0003D/\u0002\u0007q\u0005C\u0003F/\u0002\u0007a\t\u0003\u0005e\u0001!\u0015\r\u0011\"\u0001f\u00035\t7OS1wCB\u000b\u0017N\u001d*E\tV\ta\r\u0005\u0003hS\u0012:S\"\u00015\u000b\u00051#\u0011B\u00016i\u0005-Q\u0015M^1QC&\u0014(\u000b\u0012#\t\u00111\u0004\u0001\u0012!Q!\n\u0019\fa\"Y:KCZ\f\u0007+Y5s%\u0012#\u0005\u0005"
)
public class PairwiseRRDD<T> extends BaseRRDD<T, Tuple2<Object, byte[]>> {
   private JavaPairRDD<Object, byte[]> asJavaPairRDD;
   private volatile boolean bitmap$0;

   private JavaPairRDD asJavaPairRDD$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.asJavaPairRDD = JavaPairRDD$.MODULE$.<Object, byte[]>fromRDD(this, ClassTag$.MODULE$.Int(), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.asJavaPairRDD;
   }

   public JavaPairRDD<Object, byte[]> asJavaPairRDD() {
      return this.bitmap$0 ? this.asJavaPairRDD : this.asJavaPairRDD$lzycompute();
   }

   public PairwiseRRDD(RDD<T> parent, int numPartitions, byte[] hashFunc, String deserializer, byte[] packageNames, Object[] broadcastVars, ClassTag<T> evidence$3) {
      super(parent, numPartitions, hashFunc, deserializer, SerializationFormats$.MODULE$.BYTE(), packageNames, (Broadcast[])Predef$.MODULE$.refArrayOps(broadcastVars).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Broadcast<Object> apply(Object x) {
            return (Broadcast)x;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Broadcast.class))), evidence$3, ClassTag$.MODULE$.apply(Tuple2.class));
   }
}
