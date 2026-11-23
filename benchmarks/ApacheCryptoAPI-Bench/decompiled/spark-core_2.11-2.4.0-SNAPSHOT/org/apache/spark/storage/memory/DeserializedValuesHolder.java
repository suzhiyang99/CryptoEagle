package org.apache.spark.storage.memory;

import org.apache.spark.util.SizeEstimator$;
import org.apache.spark.util.collection.SizeTrackingVector;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\r4A!\u0001\u0002\u0005\u001b\tAB)Z:fe&\fG.\u001b>fIZ\u000bG.^3t\u0011>dG-\u001a:\u000b\u0005\r!\u0011AB7f[>\u0014\u0018P\u0003\u0002\u0006\r\u000591\u000f^8sC\u001e,'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001U\u0011abG\n\u0004\u0001=)\u0002C\u0001\t\u0014\u001b\u0005\t\"\"\u0001\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005Q\t\"AB!osJ+g\rE\u0002\u0017/ei\u0011AA\u0005\u00031\t\u0011ABV1mk\u0016\u001c\bj\u001c7eKJ\u0004\"AG\u000e\r\u0001\u0011)A\u0004\u0001b\u0001;\t\tA+\u0005\u0002\u001fCA\u0011\u0001cH\u0005\u0003AE\u0011qAT8uQ&tw\r\u0005\u0002\u0011E%\u00111%\u0005\u0002\u0004\u0003:L\b\u0002C\u0013\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0014\u0002\u0011\rd\u0017m]:UC\u001e\u00042a\n\u0016\u001a\u001b\u0005A#BA\u0015\u0012\u0003\u001d\u0011XM\u001a7fGRL!a\u000b\u0015\u0003\u0011\rc\u0017m]:UC\u001eDQ!\f\u0001\u0005\u00029\na\u0001P5oSRtDCA\u00181!\r1\u0002!\u0007\u0005\u0006K1\u0002\rA\n\u0005\be\u0001\u0001\r\u0011\"\u00014\u0003\u00191Xm\u0019;peV\tA\u0007E\u00026uei\u0011A\u000e\u0006\u0003oa\n!bY8mY\u0016\u001cG/[8o\u0015\tId!\u0001\u0003vi&d\u0017BA\u001e7\u0005I\u0019\u0016N_3Ue\u0006\u001c7.\u001b8h-\u0016\u001cGo\u001c:\t\u000fu\u0002\u0001\u0019!C\u0001}\u0005Qa/Z2u_J|F%Z9\u0015\u0005}\u0012\u0005C\u0001\tA\u0013\t\t\u0015C\u0001\u0003V]&$\bbB\"=\u0003\u0003\u0005\r\u0001N\u0001\u0004q\u0012\n\u0004BB#\u0001A\u0003&A'A\u0004wK\u000e$xN\u001d\u0011\t\u000f\u001d\u0003\u0001\u0019!C\u0001\u0011\u0006Y\u0011M\u001d:bsZ\u000bG.^3t+\u0005I\u0005c\u0001\tK3%\u00111*\u0005\u0002\u0006\u0003J\u0014\u0018-\u001f\u0005\b\u001b\u0002\u0001\r\u0011\"\u0001O\u0003=\t'O]1z-\u0006dW/Z:`I\u0015\fHCA P\u0011\u001d\u0019E*!AA\u0002%Ca!\u0015\u0001!B\u0013I\u0015\u0001D1se\u0006Lh+\u00197vKN\u0004\u0003\"B*\u0001\t\u0003\"\u0016AC:u_J,g+\u00197vKR\u0011q(\u0016\u0005\u0006-J\u0003\r!G\u0001\u0006m\u0006dW/\u001a\u0005\u00061\u0002!\t%W\u0001\u000eKN$\u0018.\\1uK\u0012\u001c\u0016N_3\u0015\u0003i\u0003\"\u0001E.\n\u0005q\u000b\"\u0001\u0002'p]\u001eDQA\u0018\u0001\u0005B}\u000b!bZ3u\u0005VLG\u000eZ3s)\u0005\u0001\u0007c\u0001\fb3%\u0011!M\u0001\u0002\u0013\u001b\u0016lwN]=F]R\u0014\u0018PQ;jY\u0012,'\u000f"
)
public class DeserializedValuesHolder<T> implements ValuesHolder<T> {
   public final ClassTag<T> org$apache$spark$storage$memory$DeserializedValuesHolder$$classTag;
   private SizeTrackingVector<T> vector;
   private Object arrayValues;

   public SizeTrackingVector<T> vector() {
      return this.vector;
   }

   public void vector_$eq(SizeTrackingVector<T> x$1) {
      this.vector = x$1;
   }

   public Object arrayValues() {
      return this.arrayValues;
   }

   public void arrayValues_$eq(Object x$1) {
      this.arrayValues = x$1;
   }

   public void storeValue(T value) {
      this.vector().$plus$eq(value);
   }

   public long estimatedSize() {
      return this.vector().estimateSize();
   }

   public MemoryEntryBuilder<T> getBuilder() {
      return new MemoryEntryBuilder<T>(this) {
         private final long preciseSize;
         // $FF: synthetic field
         private final DeserializedValuesHolder $outer;

         public long preciseSize() {
            return this.preciseSize;
         }

         public MemoryEntry<T> build() {
            return new DeserializedMemoryEntry<T>(this.$outer.arrayValues(), this.preciseSize(), this.$outer.org$apache$spark$storage$memory$DeserializedValuesHolder$$classTag);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               $outer.arrayValues_$eq($outer.vector().toArray());
               $outer.vector_$eq((SizeTrackingVector)null);
               this.preciseSize = SizeEstimator$.MODULE$.estimate($outer.arrayValues());
            }
         }
      };
   }

   public DeserializedValuesHolder(ClassTag<T> classTag) {
      this.org$apache$spark$storage$memory$DeserializedValuesHolder$$classTag = classTag;
      this.vector = new SizeTrackingVector<T>(classTag);
      this.arrayValues = null;
   }
}
