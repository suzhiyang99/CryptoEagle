package org.apache.spark.serializer;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import scala.Serializable;
import scala.collection.immutable.Map;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001I4Q!\u0001\u0002\u0001\t)\u0011\u0011DS1wC\u0012+7/\u001a:jC2L'0\u0019;j_:\u001cFO]3b[*\u00111\u0001B\u0001\u000bg\u0016\u0014\u0018.\u00197ju\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u000e\u001b\u0005\u0011\u0011B\u0001\b\u0003\u0005U!Um]3sS\u0006d\u0017N_1uS>t7\u000b\u001e:fC6D\u0001\u0002\u0005\u0001\u0003\u0002\u0003\u0006IAE\u0001\u0003S:\u001c\u0001\u0001\u0005\u0002\u001415\tAC\u0003\u0002\u0016-\u0005\u0011\u0011n\u001c\u0006\u0002/\u0005!!.\u0019<b\u0013\tIBCA\u0006J]B,Ho\u0015;sK\u0006l\u0007\u0002C\u000e\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000f\u0002\r1|\u0017\rZ3s!\ti\u0002%D\u0001\u001f\u0015\tyb#\u0001\u0003mC:<\u0017BA\u0011\u001f\u0005-\u0019E.Y:t\u0019>\fG-\u001a:\t\u000b\r\u0002A\u0011\u0001\u0013\u0002\rqJg.\u001b;?)\r)ce\n\t\u0003\u0019\u0001AQ\u0001\u0005\u0012A\u0002IAQa\u0007\u0012A\u0002qAq!\u000b\u0001C\u0002\u0013%!&A\u0003pE*Le.F\u0001,!\t\u0019B&\u0003\u0002.)\t\trJ\u00196fGRLe\u000e];u'R\u0014X-Y7\t\r=\u0002\u0001\u0015!\u0003,\u0003\u0019y'M[%oA!)\u0011\u0007\u0001C\u0001e\u0005Q!/Z1e\u001f\nTWm\u0019;\u0016\u0005M:D#\u0001\u001b\u0015\u0005U\u001a\u0005C\u0001\u001c8\u0019\u0001!Q\u0001\u000f\u0019C\u0002e\u0012\u0011\u0001V\t\u0003u\u0001\u0003\"a\u000f \u000e\u0003qR\u0011!P\u0001\u0006g\u000e\fG.Y\u0005\u0003\u007fq\u0012qAT8uQ&tw\r\u0005\u0002<\u0003&\u0011!\t\u0010\u0002\u0004\u0003:L\bb\u0002#1\u0003\u0003\u0005\u001d!R\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004c\u0001$Jk5\tqI\u0003\u0002Iy\u00059!/\u001a4mK\u000e$\u0018B\u0001&H\u0005!\u0019E.Y:t)\u0006<\u0007\"\u0002'\u0001\t\u0003i\u0015!B2m_N,G#\u0001(\u0011\u0005mz\u0015B\u0001)=\u0005\u0011)f.\u001b;\b\u000bI\u0013\u0001\u0012B*\u00023)\u000bg/\u0019#fg\u0016\u0014\u0018.\u00197ju\u0006$\u0018n\u001c8TiJ,\u0017-\u001c\t\u0003\u0019Q3Q!\u0001\u0002\t\nU\u001b\"\u0001\u0016,\u0011\u0005m:\u0016B\u0001-=\u0005\u0019\te.\u001f*fM\")1\u0005\u0016C\u00015R\t1\u000bC\u0004])\n\u0007I\u0011A/\u0002#A\u0014\u0018.\\5uSZ,W*\u00199qS:<7/F\u0001_!\u0011yFMZ5\u000e\u0003\u0001T!!\u00192\u0002\u0013%lW.\u001e;bE2,'BA2=\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003K\u0002\u00141!T1q!\tir-\u0003\u0002i=\t11\u000b\u001e:j]\u001e\u0004$A\u001b8\u0011\u0007uYW.\u0003\u0002m=\t)1\t\\1tgB\u0011aG\u001c\u0003\n_B\f\t\u0011!A\u0003\u0002e\u00121a\u0018\u00133\u0011\u0019\tH\u000b)A\u0005=\u0006\u0011\u0002O]5nSRLg/Z'baBLgnZ:!\u0001"
)
public class JavaDeserializationStream extends DeserializationStream {
   public final InputStream org$apache$spark$serializer$JavaDeserializationStream$$in;
   public final ClassLoader org$apache$spark$serializer$JavaDeserializationStream$$loader;
   private final ObjectInputStream objIn;

   public static Map<String, Class<?>> primitiveMappings() {
      return JavaDeserializationStream$.MODULE$.primitiveMappings();
   }

   private ObjectInputStream objIn() {
      return this.objIn;
   }

   public <T> T readObject(ClassTag<T> evidence$2) {
      return (T)this.objIn().readObject();
   }

   public void close() {
      this.objIn().close();
   }

   public JavaDeserializationStream(InputStream in, ClassLoader loader) {
      this.org$apache$spark$serializer$JavaDeserializationStream$$in = in;
      this.org$apache$spark$serializer$JavaDeserializationStream$$loader = loader;
      this.objIn = new ObjectInputStream(this) {
         // $FF: synthetic field
         private final JavaDeserializationStream $outer;

         public Class<?> resolveClass(ObjectStreamClass desc) {
            Class var10000;
            try {
               var10000 = Class.forName(desc.getName(), false, this.$outer.org$apache$spark$serializer$JavaDeserializationStream$$loader);
            } catch (ClassNotFoundException var3) {
               var10000 = (Class)JavaDeserializationStream$.MODULE$.primitiveMappings().getOrElse(desc.getName(), new Serializable(this, var3) {
                  public static final long serialVersionUID = 0L;
                  private final ClassNotFoundException e$1;

                  public final Nothing$ apply() {
                     throw this.e$1;
                  }

                  public {
                     this.e$1 = e$1;
                  }
               });
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
   }
}
