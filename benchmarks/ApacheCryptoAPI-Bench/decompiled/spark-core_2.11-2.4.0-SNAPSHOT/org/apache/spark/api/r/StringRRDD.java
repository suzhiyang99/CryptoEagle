package org.apache.spark.api.r;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaRDD$;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.rdd.RDD;
import scala.Array$;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u00114A!\u0001\u0002\u0005\u001b\tQ1\u000b\u001e:j]\u001e\u0014&\u000b\u0012#\u000b\u0005\r!\u0011!\u0001:\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0016\u00059)2C\u0001\u0001\u0010!\u0011\u0001\u0012cE\u0011\u000e\u0003\tI!A\u0005\u0002\u0003\u0011\t\u000b7/\u001a*S\t\u0012\u0003\"\u0001F\u000b\r\u0001\u0011)a\u0003\u0001b\u0001/\t\tA+\u0005\u0002\u0019=A\u0011\u0011\u0004H\u0007\u00025)\t1$A\u0003tG\u0006d\u0017-\u0003\u0002\u001e5\t9aj\u001c;iS:<\u0007CA\r \u0013\t\u0001#DA\u0002B]f\u0004\"AI\u0013\u000f\u0005e\u0019\u0013B\u0001\u0013\u001b\u0003\u0019\u0001&/\u001a3fM&\u0011ae\n\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\u0011R\u0002\u0002C\u0015\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0016\u0002\rA\f'/\u001a8u!\rYcfE\u0007\u0002Y)\u0011QFB\u0001\u0004e\u0012$\u0017BA\u0018-\u0005\r\u0011F\t\u0012\u0005\tc\u0001\u0011\t\u0011)A\u0005e\u0005!a-\u001e8d!\rI2'N\u0005\u0003ii\u0011Q!\u0011:sCf\u0004\"!\u0007\u001c\n\u0005]R\"\u0001\u0002\"zi\u0016D\u0001\"\u000f\u0001\u0003\u0002\u0003\u0006I!I\u0001\rI\u0016\u001cXM]5bY&TXM\u001d\u0005\tw\u0001\u0011\t\u0011)A\u0005e\u0005a\u0001/Y2lC\u001e,g*Y7fg\"AQ\b\u0001B\u0001B\u0003%a(A\u0007ce>\fGmY1tiZ\u000b'o\u001d\t\u00043Mz\u0004C\u0001!F\u001b\u0005\t%B\u0001\"D\u0003\u0011a\u0017M\\4\u000b\u0003\u0011\u000bAA[1wC&\u0011a)\u0011\u0002\u0007\u001f\nTWm\u0019;\t\u0011!\u0003!1!Q\u0001\f%\u000b!\"\u001a<jI\u0016t7-\u001a\u00136!\rQUjE\u0007\u0002\u0017*\u0011AJG\u0001\be\u00164G.Z2u\u0013\tq5J\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011\u0015\u0001\u0006\u0001\"\u0001R\u0003\u0019a\u0014N\\5u}Q1!+\u0016,X1f#\"a\u0015+\u0011\u0007A\u00011\u0003C\u0003I\u001f\u0002\u000f\u0011\nC\u0003*\u001f\u0002\u0007!\u0006C\u00032\u001f\u0002\u0007!\u0007C\u0003:\u001f\u0002\u0007\u0011\u0005C\u0003<\u001f\u0002\u0007!\u0007C\u0003>\u001f\u0002\u0007a\b\u0003\u0005\\\u0001!\u0015\r\u0011\"\u0001]\u0003%\t7OS1wCJ#E)F\u0001^!\rq\u0006-I\u0007\u0002?*\u0011A\tB\u0005\u0003C~\u0013qAS1wCJ#E\t\u0003\u0005d\u0001!\u0005\t\u0015)\u0003^\u0003)\t7OS1wCJ#E\t\t"
)
public class StringRRDD<T> extends BaseRRDD<T, String> {
   private JavaRDD<String> asJavaRDD;
   private volatile boolean bitmap$0;

   private JavaRDD asJavaRDD$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.asJavaRDD = JavaRDD$.MODULE$.<String>fromRDD(this, ClassTag$.MODULE$.apply(String.class));
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.asJavaRDD;
   }

   public JavaRDD<String> asJavaRDD() {
      return this.bitmap$0 ? this.asJavaRDD : this.asJavaRDD$lzycompute();
   }

   public StringRRDD(RDD<T> parent, byte[] func, String deserializer, byte[] packageNames, Object[] broadcastVars, ClassTag<T> evidence$5) {
      super(parent, -1, func, deserializer, SerializationFormats$.MODULE$.STRING(), packageNames, (Broadcast[])Predef$.MODULE$.refArrayOps(broadcastVars).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Broadcast<Object> apply(Object x) {
            return (Broadcast)x;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Broadcast.class))), evidence$5, ClassTag$.MODULE$.apply(String.class));
   }
}
