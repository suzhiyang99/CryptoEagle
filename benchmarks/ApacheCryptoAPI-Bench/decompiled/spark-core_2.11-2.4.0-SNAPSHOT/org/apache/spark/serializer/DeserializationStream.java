package org.apache.spark.serializer;

import java.io.Closeable;
import java.io.EOFException;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.util.NextIterator;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001E4Q!\u0001\u0002\u0002\u0002-\u0011Q\u0003R3tKJL\u0017\r\\5{CRLwN\\*ue\u0016\fWN\u0003\u0002\u0004\t\u0005Q1/\u001a:jC2L'0\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001aE\u0002\u0001\u0019Q\u0001\"!\u0004\n\u000e\u00039Q!a\u0004\t\u0002\t1\fgn\u001a\u0006\u0002#\u0005!!.\u0019<b\u0013\t\u0019bB\u0001\u0004PE*,7\r\u001e\t\u0003+ai\u0011A\u0006\u0006\u0003/A\t!![8\n\u0005e1\"!C\"m_N,\u0017M\u00197f\u0011\u0015Y\u0002\u0001\"\u0001\u001d\u0003\u0019a\u0014N\\5u}Q\tQ\u0004\u0005\u0002\u001f\u00015\t!\u0001C\u0003!\u0001\u0019\u0005\u0011%\u0001\u0006sK\u0006$wJ\u00196fGR,\"A\t\u0014\u0015\u0003\r\"\"\u0001\n\u001a\u0011\u0005\u00152C\u0002\u0001\u0003\u0006O}\u0011\r\u0001\u000b\u0002\u0002)F\u0011\u0011f\f\t\u0003U5j\u0011a\u000b\u0006\u0002Y\u0005)1oY1mC&\u0011af\u000b\u0002\b\u001d>$\b.\u001b8h!\tQ\u0003'\u0003\u00022W\t\u0019\u0011I\\=\t\u000fMz\u0012\u0011!a\u0002i\u0005QQM^5eK:\u001cW\r\n\u001d\u0011\u0007UBD%D\u00017\u0015\t94&A\u0004sK\u001adWm\u0019;\n\u0005e2$\u0001C\"mCN\u001cH+Y4\t\u000bm\u0002A\u0011\u0001\u001f\u0002\u000fI,\u0017\rZ&fsV\u0011Q\b\u0011\u000b\u0002}Q\u0011q(\u0011\t\u0003K\u0001#Qa\n\u001eC\u0002!BqA\u0011\u001e\u0002\u0002\u0003\u000f1)\u0001\u0006fm&$WM\\2fIe\u00022!\u000e\u001d@\u0011\u0015)\u0005\u0001\"\u0001G\u0003%\u0011X-\u00193WC2,X-\u0006\u0002H\u0015R\t\u0001\n\u0006\u0002J\u0017B\u0011QE\u0013\u0003\u0006O\u0011\u0013\r\u0001\u000b\u0005\b\u0019\u0012\u000b\t\u0011q\u0001N\u0003-)g/\u001b3f]\u000e,G%\r\u0019\u0011\u0007UB\u0014\nC\u0003P\u0001\u0019\u0005\u0003+A\u0003dY>\u001cX\rF\u0001R!\tQ#+\u0003\u0002TW\t!QK\\5u\u0011\u0015)\u0006\u0001\"\u0001W\u0003)\t7/\u0013;fe\u0006$xN]\u000b\u0002/B\u0019\u0001\fY\u0018\u000f\u0005esfB\u0001.^\u001b\u0005Y&B\u0001/\u000b\u0003\u0019a$o\\8u}%\tA&\u0003\u0002`W\u00059\u0001/Y2lC\u001e,\u0017BA1c\u0005!IE/\u001a:bi>\u0014(BA0,\u0011\u0015!\u0007\u0001\"\u0001f\u0003I\t7oS3z-\u0006dW/Z%uKJ\fGo\u001c:\u0016\u0003\u0019\u00042\u0001\u00171h!\u0011Q\u0003nL\u0018\n\u0005%\\#A\u0002+va2,'\u0007\u000b\u0002\u0001WB\u0011An\\\u0007\u0002[*\u0011a\u000eB\u0001\u000bC:tw\u000e^1uS>t\u0017B\u00019n\u00051!UM^3m_B,'/\u00119j\u0001"
)
public abstract class DeserializationStream implements Closeable {
   public abstract <T> T readObject(ClassTag<T> var1);

   public <T> T readKey(ClassTag<T> evidence$9) {
      return (T)this.readObject(evidence$9);
   }

   public <T> T readValue(ClassTag<T> evidence$10) {
      return (T)this.readObject(evidence$10);
   }

   public abstract void close();

   public Iterator<Object> asIterator() {
      return new NextIterator<Object>(this) {
         // $FF: synthetic field
         private final DeserializationStream $outer;

         public Object getNext() {
            Object var10000;
            try {
               var10000 = this.$outer.readObject(ClassTag$.MODULE$.Any());
            } catch (EOFException var2) {
               this.finished_$eq(true);
               var10000 = null;
            }

            return var10000;
         }

         public void close() {
            this.$outer.close();
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

   public Iterator<Tuple2<Object, Object>> asKeyValueIterator() {
      return new NextIterator<Tuple2<Object, Object>>(this) {
         // $FF: synthetic field
         private final DeserializationStream $outer;

         public Tuple2<Object, Object> getNext() {
            Tuple2 var10000;
            try {
               var10000 = new Tuple2(this.$outer.readKey(ClassTag$.MODULE$.Any()), this.$outer.readValue(ClassTag$.MODULE$.Any()));
            } catch (EOFException var2) {
               this.finished_$eq(true);
               var10000 = null;
            }

            return var10000;
         }

         public void close() {
            this.$outer.close();
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
