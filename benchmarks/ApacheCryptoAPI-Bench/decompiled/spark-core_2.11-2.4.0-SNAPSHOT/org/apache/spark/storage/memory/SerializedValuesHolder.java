package org.apache.spark.storage.memory;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.spark.memory.MemoryMode;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.StreamBlockId;
import org.apache.spark.unsafe.Platform;
import org.apache.spark.util.io.ChunkedByteBufferOutputStream;
import scala.Function1;
import scala.MatchError;
import scala.Serializable;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005b\u0001B\u0001\u0003\t5\u0011acU3sS\u0006d\u0017N_3e-\u0006dW/Z:I_2$WM\u001d\u0006\u0003\u0007\u0011\ta!\\3n_JL(BA\u0003\u0007\u0003\u001d\u0019Ho\u001c:bO\u0016T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001+\tq1dE\u0002\u0001\u001fU\u0001\"\u0001E\n\u000e\u0003EQ\u0011AE\u0001\u0006g\u000e\fG.Y\u0005\u0003)E\u0011a!\u00118z%\u00164\u0007c\u0001\f\u001835\t!!\u0003\u0002\u0019\u0005\taa+\u00197vKNDu\u000e\u001c3feB\u0011!d\u0007\u0007\u0001\t\u0015a\u0002A1\u0001\u001e\u0005\u0005!\u0016C\u0001\u0010\"!\t\u0001r$\u0003\u0002!#\t9aj\u001c;iS:<\u0007C\u0001\t#\u0013\t\u0019\u0013CA\u0002B]fD\u0001\"\n\u0001\u0003\u0002\u0003\u0006IAJ\u0001\bE2|7m[%e!\t9\u0003&D\u0001\u0005\u0013\tICAA\u0004CY>\u001c7.\u00133\t\u0011-\u0002!\u0011!Q\u0001\n1\n\u0011b\u00195v].\u001c\u0016N_3\u0011\u0005Ai\u0013B\u0001\u0018\u0012\u0005\rIe\u000e\u001e\u0005\ta\u0001\u0011\t\u0011)A\u0005c\u0005A1\r\\1tgR\u000bw\rE\u00023kei\u0011a\r\u0006\u0003iE\tqA]3gY\u0016\u001cG/\u0003\u00027g\tA1\t\\1tgR\u000bw\r\u0003\u00059\u0001\t\u0005\t\u0015!\u0003:\u0003)iW-\\8ss6{G-\u001a\t\u0003uqj\u0011a\u000f\u0006\u0003\u0007\u0019I!!P\u001e\u0003\u00155+Wn\u001c:z\u001b>$W\r\u0003\u0005@\u0001\t\u0005\t\u0015!\u0003A\u0003E\u0019XM]5bY&TXM]'b]\u0006<WM\u001d\t\u0003\u0003\u0012k\u0011A\u0011\u0006\u0003\u0007\u001a\t!b]3sS\u0006d\u0017N_3s\u0013\t)%IA\tTKJL\u0017\r\\5{KJl\u0015M\\1hKJDQa\u0012\u0001\u0005\u0002!\u000ba\u0001P5oSRtDCB%K\u00172ke\nE\u0002\u0017\u0001eAQ!\n$A\u0002\u0019BQa\u000b$A\u00021BQ\u0001\r$A\u0002EBQ\u0001\u000f$A\u0002eBQa\u0010$A\u0002\u0001Cq\u0001\u0015\u0001C\u0002\u0013\u0005\u0011+A\u0005bY2|7-\u0019;peV\t!\u000b\u0005\u0003\u0011'2*\u0016B\u0001+\u0012\u0005%1UO\\2uS>t\u0017\u0007\u0005\u0002W76\tqK\u0003\u0002Y3\u0006\u0019a.[8\u000b\u0003i\u000bAA[1wC&\u0011Al\u0016\u0002\u000b\u0005f$XMQ;gM\u0016\u0014\bB\u00020\u0001A\u0003%!+\u0001\u0006bY2|7-\u0019;pe\u0002Bq\u0001\u0019\u0001C\u0002\u0013\u0005\u0011-\u0001\nsK\u0012L'/Z2uC\ndWm\u0015;sK\u0006lW#\u00012\u0011\u0005Y\u0019\u0017B\u00013\u0003\u0005a\u0011V\rZ5sK\u000e$\u0018M\u00197f\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\u0007M\u0002\u0001\u000b\u0011\u00022\u0002'I,G-\u001b:fGR\f'\r\\3TiJ,\u0017-\u001c\u0011\t\u000f!\u0004!\u0019!C\u0001S\u0006!!MY8t+\u0005Q\u0007CA6q\u001b\u0005a'BA7o\u0003\tIwN\u0003\u0002p\r\u0005!Q\u000f^5m\u0013\t\tHNA\u000fDQVt7.\u001a3CsR,')\u001e4gKJ|U\u000f\u001e9viN#(/Z1n\u0011\u0019\u0019\b\u0001)A\u0005U\u0006)!MY8tA!9Q\u000f\u0001b\u0001\n\u00031\u0018aE:fe&\fG.\u001b>bi&|gn\u0015;sK\u0006lW#A<\u0011\u0005\u0005C\u0018BA=C\u0005M\u0019VM]5bY&T\u0018\r^5p]N#(/Z1n\u0011\u0019Y\b\u0001)A\u0005o\u0006!2/\u001a:jC2L'0\u0019;j_:\u001cFO]3b[\u0002BQ! \u0001\u0005By\f!b\u001d;pe\u00164\u0016\r\\;f)\ry\u0018Q\u0001\t\u0004!\u0005\u0005\u0011bAA\u0002#\t!QK\\5u\u0011\u0019\t9\u0001 a\u00013\u0005)a/\u00197vK\"9\u00111\u0002\u0001\u0005B\u00055\u0011!D3ti&l\u0017\r^3e'&TX\r\u0006\u0002\u0002\u0010A\u0019\u0001#!\u0005\n\u0007\u0005M\u0011C\u0001\u0003M_:<\u0007bBA\f\u0001\u0011\u0005\u0013\u0011D\u0001\u000bO\u0016$()^5mI\u0016\u0014HCAA\u000e!\u00111\u0012QD\r\n\u0007\u0005}!A\u0001\nNK6|'/_#oiJL()^5mI\u0016\u0014\b"
)
public class SerializedValuesHolder<T> implements ValuesHolder<T> {
   public final ClassTag<T> org$apache$spark$storage$memory$SerializedValuesHolder$$classTag;
   public final MemoryMode org$apache$spark$storage$memory$SerializedValuesHolder$$memoryMode;
   private final Function1<Object, ByteBuffer> allocator;
   private final RedirectableOutputStream redirectableStream;
   private final ChunkedByteBufferOutputStream bbos;
   private final SerializationStream serializationStream;

   public Function1<Object, ByteBuffer> allocator() {
      return this.allocator;
   }

   public RedirectableOutputStream redirectableStream() {
      return this.redirectableStream;
   }

   public ChunkedByteBufferOutputStream bbos() {
      return this.bbos;
   }

   public SerializationStream serializationStream() {
      return this.serializationStream;
   }

   public void storeValue(T value) {
      this.serializationStream().writeObject(value, this.org$apache$spark$storage$memory$SerializedValuesHolder$$classTag);
   }

   public long estimatedSize() {
      return this.bbos().size();
   }

   public MemoryEntryBuilder<T> getBuilder() {
      return new MemoryEntryBuilder<T>(this) {
         // $FF: synthetic field
         private final SerializedValuesHolder $outer;

         public long preciseSize() {
            return this.$outer.bbos().size();
         }

         public MemoryEntry<T> build() {
            return new SerializedMemoryEntry<T>(this.$outer.bbos().toChunkedByteBuffer(), this.$outer.org$apache$spark$storage$memory$SerializedValuesHolder$$memoryMode, this.$outer.org$apache$spark$storage$memory$SerializedValuesHolder$$classTag);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               $outer.serializationStream().close();
            }
         }
      };
   }

   public SerializedValuesHolder(BlockId blockId, int chunkSize, ClassTag<T> classTag, MemoryMode memoryMode, SerializerManager serializerManager) {
      this.org$apache$spark$storage$memory$SerializedValuesHolder$$classTag = classTag;
      this.org$apache$spark$storage$memory$SerializedValuesHolder$$memoryMode = memoryMode;
      Serializable var7;
      if (MemoryMode.ON_HEAP.equals(memoryMode)) {
         var7 = new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final ByteBuffer apply(int x$1) {
               return ByteBuffer.allocate(x$1);
            }
         };
      } else {
         if (!MemoryMode.OFF_HEAP.equals(memoryMode)) {
            throw new MatchError(memoryMode);
         }

         var7 = new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final ByteBuffer apply(int x$1) {
               return Platform.allocateDirectBuffer(x$1);
            }
         };
      }

      this.allocator = var7;
      this.redirectableStream = new RedirectableOutputStream();
      this.bbos = new ChunkedByteBufferOutputStream(chunkSize, this.allocator());
      this.redirectableStream().setOutputStream(this.bbos());
      boolean autoPick = !(blockId instanceof StreamBlockId);
      SerializerInstance ser = serializerManager.getSerializer(classTag, autoPick).newInstance();
      this.serializationStream = ser.serializeStream(serializerManager.wrapForCompression(blockId, (OutputStream)this.redirectableStream()));
   }
}
