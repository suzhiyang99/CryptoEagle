package org.apache.spark.api.r;

import java.util.Map;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaRDD$;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.rdd.RDD;
import scala.Array$;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001dc\u0001B\u0001\u0003\t5\u0011AA\u0015*E\t*\u00111\u0001B\u0001\u0002e*\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001U\u0011a\"F\n\u0003\u0001=\u0001B\u0001E\t\u0014C5\t!!\u0003\u0002\u0013\u0005\tA!)Y:f%J#E\t\u0005\u0002\u0015+1\u0001A!\u0002\f\u0001\u0005\u00049\"!\u0001+\u0012\u0005aq\u0002CA\r\u001d\u001b\u0005Q\"\"A\u000e\u0002\u000bM\u001c\u0017\r\\1\n\u0005uQ\"a\u0002(pi\"Lgn\u001a\t\u00033}I!\u0001\t\u000e\u0003\u0007\u0005s\u0017\u0010E\u0002\u001aE\u0011J!a\t\u000e\u0003\u000b\u0005\u0013(/Y=\u0011\u0005e)\u0013B\u0001\u0014\u001b\u0005\u0011\u0011\u0015\u0010^3\t\u0011!\u0002!\u0011!Q\u0001\n%\na\u0001]1sK:$\bc\u0001\u0016.'5\t1F\u0003\u0002-\r\u0005\u0019!\u000f\u001a3\n\u00059Z#a\u0001*E\t\"A\u0001\u0007\u0001B\u0001B\u0003%\u0011%\u0001\u0003gk:\u001c\u0007\u0002\u0003\u001a\u0001\u0005\u0003\u0005\u000b\u0011B\u001a\u0002\u0019\u0011,7/\u001a:jC2L'0\u001a:\u0011\u0005Q:dBA\r6\u0013\t1$$\u0001\u0004Qe\u0016$WMZ\u0005\u0003qe\u0012aa\u0015;sS:<'B\u0001\u001c\u001b\u0011!Y\u0004A!A!\u0002\u0013\u0019\u0014AC:fe&\fG.\u001b>fe\"AQ\b\u0001B\u0001B\u0003%\u0011%\u0001\u0007qC\u000e\\\u0017mZ3OC6,7\u000f\u0003\u0005@\u0001\t\u0005\t\u0015!\u0003A\u00035\u0011'o\\1eG\u0006\u001cHOV1sgB\u0019\u0011DI!\u0011\u0005\t;U\"A\"\u000b\u0005\u0011+\u0015\u0001\u00027b]\u001eT\u0011AR\u0001\u0005U\u00064\u0018-\u0003\u0002I\u0007\n1qJ\u00196fGRD\u0001B\u0013\u0001\u0003\u0004\u0003\u0006YaS\u0001\u000bKZLG-\u001a8dK\u0012\"\u0004c\u0001'P'5\tQJ\u0003\u0002O5\u00059!/\u001a4mK\u000e$\u0018B\u0001)N\u0005!\u0019E.Y:t)\u0006<\u0007\"\u0002*\u0001\t\u0003\u0019\u0016A\u0002\u001fj]&$h\bF\u0004U/bK&l\u0017/\u0015\u0005U3\u0006c\u0001\t\u0001'!)!*\u0015a\u0002\u0017\")\u0001&\u0015a\u0001S!)\u0001'\u0015a\u0001C!)!'\u0015a\u0001g!)1(\u0015a\u0001g!)Q(\u0015a\u0001C!)q(\u0015a\u0001\u0001\"Aa\f\u0001EC\u0002\u0013\u0005q,A\u0005bg*\u000bg/\u0019*E\tV\t\u0001\rE\u0002bG\u0006j\u0011A\u0019\u0006\u0003\r\u0012I!\u0001\u001a2\u0003\u000f)\u000bg/\u0019*E\t\"Aa\r\u0001E\u0001B\u0003&\u0001-\u0001\u0006bg*\u000bg/\u0019*E\t\u0002:a\u0001\u001b\u0002\t\u0002\tI\u0017\u0001\u0002*S\t\u0012\u0003\"\u0001\u00056\u0007\r\u0005\u0011\u0001\u0012\u0001\u0002l'\rQGn\u001c\t\u000335L!A\u001c\u000e\u0003\r\u0005s\u0017PU3g!\tI\u0002/\u0003\u0002r5\ta1+\u001a:jC2L'0\u00192mK\")!K\u001bC\u0001gR\t\u0011\u000eC\u0003vU\u0012\u0005a/\u0001\nde\u0016\fG/Z*qCJ\\7i\u001c8uKb$HCC<{yz\f\t!a\u0002\u0002\u0018A\u0011\u0011\r_\u0005\u0003s\n\u0014\u0001CS1wCN\u0003\u0018M]6D_:$X\r\u001f;\t\u000bm$\b\u0019A\u001a\u0002\r5\f7\u000f^3s\u0011\u0015iH\u000f1\u00014\u0003\u001d\t\u0007\u000f\u001d(b[\u0016DQa ;A\u0002M\n\u0011b\u001d9be.Du.\\3\t\u000f\u0005\rA\u000f1\u0001\u0002\u0006\u0005!!.\u0019:t!\rI\"e\r\u0005\b\u0003\u0013!\b\u0019AA\u0006\u00035\u0019\b/\u0019:l\u000b:4\u0018N]'baB1\u0011QBA\n\u0003\u0006k!!a\u0004\u000b\u0007\u0005EQ)\u0001\u0003vi&d\u0017\u0002BA\u000b\u0003\u001f\u00111!T1q\u0011\u001d\tI\u0002\u001ea\u0001\u0003\u0017\t1c\u001d9be.,\u00050Z2vi>\u0014XI\u001c<NCBDq!!\bk\t\u0003\ty\"\u0001\nde\u0016\fG/\u001a*E\t\u001a\u0013x.\\!se\u0006LH#\u00021\u0002\"\u0005\u0015\u0002bBA\u0012\u00037\u0001\ra^\u0001\u0004UN\u001c\u0007\u0002CA\u0014\u00037\u0001\r!!\u000b\u0002\u0007\u0005\u0014(\u000fE\u0002\u001aE\u0005Bq!!\fk\t\u0003\ty#A\tde\u0016\fG/\u001a*E\t\u001a\u0013x.\u001c$jY\u0016$r\u0001YA\u0019\u0003g\t9\u0004C\u0004\u0002$\u0005-\u0002\u0019A<\t\u000f\u0005U\u00121\u0006a\u0001g\u0005Aa-\u001b7f\u001d\u0006lW\r\u0003\u0005\u0002:\u0005-\u0002\u0019AA\u001e\u0003-\u0001\u0018M]1mY\u0016d\u0017n]7\u0011\u0007e\ti$C\u0002\u0002@i\u00111!\u00138u\u0011%\t\u0019E[A\u0001\n\u0013\t)%A\u0006sK\u0006$'+Z:pYZ,G#A!"
)
public class RRDD<T> extends BaseRRDD<T, byte[]> {
   private JavaRDD<byte[]> asJavaRDD;
   private volatile boolean bitmap$0;

   public static JavaRDD<byte[]> createRDDFromFile(JavaSparkContext var0, String var1, int var2) {
      return RRDD$.MODULE$.createRDDFromFile(var0, var1, var2);
   }

   public static JavaRDD<byte[]> createRDDFromArray(JavaSparkContext var0, byte[][] var1) {
      return RRDD$.MODULE$.createRDDFromArray(var0, var1);
   }

   public static JavaSparkContext createSparkContext(String var0, String var1, String var2, String[] var3, Map<Object, Object> var4, Map<Object, Object> var5) {
      return RRDD$.MODULE$.createSparkContext(var0, var1, var2, var3, var4, var5);
   }

   private JavaRDD asJavaRDD$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.asJavaRDD = JavaRDD$.MODULE$.<byte[]>fromRDD(this, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.asJavaRDD;
   }

   public JavaRDD<byte[]> asJavaRDD() {
      return this.bitmap$0 ? this.asJavaRDD : this.asJavaRDD$lzycompute();
   }

   public RRDD(RDD<T> parent, byte[] func, String deserializer, String serializer, byte[] packageNames, Object[] broadcastVars, ClassTag<T> evidence$4) {
      super(parent, -1, func, deserializer, serializer, packageNames, (Broadcast[])Predef$.MODULE$.refArrayOps(broadcastVars).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Broadcast<Object> apply(Object x) {
            return (Broadcast)x;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Broadcast.class))), evidence$4, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }
}
