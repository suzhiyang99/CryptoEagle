package org.apache.spark.storage.memory;

import java.io.OutputStream;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.memory.MemoryMode;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.storage.BlockId;
import org.apache.spark.util.io.ChunkedByteBuffer;
import org.apache.spark.util.io.ChunkedByteBufferOutputStream;
import org.spark_project.guava.io.ByteStreams;
import scala.Function1;
import scala.Option$;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ec!B\u0001\u0003\u0001\u0011a!\u0001\u0007)beRL\u0017\r\u001c7z'\u0016\u0014\u0018.\u00197ju\u0016$'\t\\8dW*\u00111\u0001B\u0001\u0007[\u0016lwN]=\u000b\u0005\u00151\u0011aB:u_J\fw-\u001a\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOV\u0011QBY\n\u0003\u00019\u0001\"a\u0004\n\u000e\u0003AQ\u0011!E\u0001\u0006g\u000e\fG.Y\u0005\u0003'A\u0011a!\u00118z%\u00164\u0007\u0002C\u000b\u0001\u0005\u0003\u0005\u000b\u0011B\f\u0002\u00175,Wn\u001c:z'R|'/Z\u0002\u0001!\tA\u0012$D\u0001\u0003\u0013\tQ\"AA\u0006NK6|'/_*u_J,\u0007\u0002\u0003\u000f\u0001\u0005\u0003\u0005\u000b\u0011B\u000f\u0002#M,'/[1mSj,'/T1oC\u001e,'\u000f\u0005\u0002\u001fC5\tqD\u0003\u0002!\r\u0005Q1/\u001a:jC2L'0\u001a:\n\u0005\tz\"!E*fe&\fG.\u001b>fe6\u000bg.Y4fe\"AA\u0005\u0001B\u0001B\u0003%Q%A\u0004cY>\u001c7.\u00133\u0011\u0005\u0019:S\"\u0001\u0003\n\u0005!\"!a\u0002\"m_\u000e\\\u0017\n\u001a\u0005\tU\u0001\u0011)\u0019!C\u0005W\u0005\u00192/\u001a:jC2L'0\u0019;j_:\u001cFO]3b[V\tA\u0006\u0005\u0002\u001f[%\u0011af\b\u0002\u0014'\u0016\u0014\u0018.\u00197ju\u0006$\u0018n\u001c8TiJ,\u0017-\u001c\u0005\ta\u0001\u0011\t\u0011)A\u0005Y\u0005!2/\u001a:jC2L'0\u0019;j_:\u001cFO]3b[\u0002B\u0001B\r\u0001\u0003\u0006\u0004%IaM\u0001\u0019e\u0016$\u0017N]3di\u0006\u0014G.Z(viB,Ho\u0015;sK\u0006lW#\u0001\u001b\u0011\u0005a)\u0014B\u0001\u001c\u0003\u0005a\u0011V\rZ5sK\u000e$\u0018M\u00197f\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\tq\u0001\u0011\t\u0011)A\u0005i\u0005I\"/\u001a3je\u0016\u001cG/\u00192mK>+H\u000f];u'R\u0014X-Y7!\u0011!Q\u0004A!b\u0001\n\u0003Y\u0014\u0001D;oe>dG.T3n_JLX#\u0001\u001f\u0011\u0005=i\u0014B\u0001 \u0011\u0005\u0011auN\\4\t\u0011\u0001\u0003!\u0011!Q\u0001\nq\nQ\"\u001e8s_2dW*Z7pef\u0004\u0003\u0002\u0003\"\u0001\u0005\u0003\u0005\u000b\u0011B\"\u0002\u00155,Wn\u001c:z\u001b>$W\r\u0005\u0002E\r6\tQI\u0003\u0002\u0004\r%\u0011q)\u0012\u0002\u000b\u001b\u0016lwN]=N_\u0012,\u0007\u0002C%\u0001\u0005\u0003\u0005\u000b\u0011\u0002&\u0002\t\t\u0014wn\u001d\t\u0003\u0017Bk\u0011\u0001\u0014\u0006\u0003\u001b:\u000b!![8\u000b\u0005=3\u0011\u0001B;uS2L!!\u0015'\u0003;\rCWO\\6fI\nKH/\u001a\"vM\u001a,'oT;uaV$8\u000b\u001e:fC6D\u0001b\u0015\u0001\u0003\u0002\u0003\u0006I\u0001V\u0001\u0005e\u0016\u001cH\u000fE\u0002V;\u0002t!AV.\u000f\u0005]SV\"\u0001-\u000b\u0005e3\u0012A\u0002\u001fs_>$h(C\u0001\u0012\u0013\ta\u0006#A\u0004qC\u000e\\\u0017mZ3\n\u0005y{&\u0001C%uKJ\fGo\u001c:\u000b\u0005q\u0003\u0002CA1c\u0019\u0001!Qa\u0019\u0001C\u0002\u0011\u0014\u0011\u0001V\t\u0003K\"\u0004\"a\u00044\n\u0005\u001d\u0004\"a\u0002(pi\"Lgn\u001a\t\u0003\u001f%L!A\u001b\t\u0003\u0007\u0005s\u0017\u0010\u0003\u0005m\u0001\t\u0005\t\u0015!\u0003n\u0003!\u0019G.Y:t)\u0006<\u0007c\u00018rA6\tqN\u0003\u0002q!\u00059!/\u001a4mK\u000e$\u0018B\u0001:p\u0005!\u0019E.Y:t)\u0006<\u0007\"\u0002;\u0001\t\u0003)\u0018A\u0002\u001fj]&$h\b\u0006\u0007wobL(p\u001f?~}~\f\t\u0001E\u0002\u0019\u0001\u0001DQ!F:A\u0002]AQ\u0001H:A\u0002uAQ\u0001J:A\u0002\u0015BQAK:A\u00021BQAM:A\u0002QBQAO:A\u0002qBQAQ:A\u0002\rCQ!S:A\u0002)CQaU:A\u0002QCQ\u0001\\:A\u00025D!\"!\u0002\u0001\u0011\u000b\u0007I\u0011BA\u0004\u00039)hN]8mY\u0016$')\u001e4gKJ,\"!!\u0003\u0011\u0007-\u000bY!C\u0002\u0002\u000e1\u0013\u0011c\u00115v].,GMQ=uK\n+hMZ3s\u0011)\t\t\u0002\u0001E\u0001B\u0003&\u0011\u0011B\u0001\u0010k:\u0014x\u000e\u001c7fI\n+hMZ3sA!A\u0011Q\u0003\u0001\u0005\u0002\u0011\t9!\u0001\u000fhKR,fN]8mY\u0016$7\t[;oW\u0016$')\u001f;f\u0005V4g-\u001a:\t\u0011\u0005e\u0001\u0001)Q\u0005\u00037\t\u0011\u0002Z5tG\u0006\u0014H-\u001a3\u0011\u0007=\ti\"C\u0002\u0002 A\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005\u0002$\u0001\u0001\u000b\u0015BA\u000e\u0003!\u0019wN\\:v[\u0016$\u0007bBA\u0014\u0001\u0011%\u0011\u0011F\u0001!m\u0016\u0014\u0018NZ=O_R\u001cuN\\:v[\u0016$\u0017I\u001c3O_R$\u0015n]2be\u0012,G\r\u0006\u0002\u0002,A\u0019q\"!\f\n\u0007\u0005=\u0002C\u0001\u0003V]&$\bbBA\u001a\u0001\u0011\u0005\u0011\u0011F\u0001\bI&\u001c8-\u0019:e\u0011\u001d\t9\u0004\u0001C\u0001\u0003s\tQCZ5oSNDwK]5uS:<Gk\\*ue\u0016\fW\u000e\u0006\u0003\u0002,\u0005m\u0002\u0002CA\u001f\u0003k\u0001\r!a\u0010\u0002\u0005=\u001c\b\u0003BA!\u0003\u0013j!!a\u0011\u000b\u00075\u000b)E\u0003\u0002\u0002H\u0005!!.\u0019<b\u0013\u0011\tY%a\u0011\u0003\u0019=+H\u000f];u'R\u0014X-Y7\t\u000f\u0005=\u0003\u0001\"\u0001\u0002R\u0005qa/\u00197vKNLE/\u001a:bi>\u0014XCAA*!\u0011A\u0012Q\u000b1\n\u0007\u0005]#AA\rQCJ$\u0018.\u00197msVs'o\u001c7mK\u0012LE/\u001a:bi>\u0014\b"
)
public class PartiallySerializedBlock<T> {
   private final MemoryStore memoryStore;
   private final SerializerManager serializerManager;
   private final BlockId blockId;
   private final SerializationStream serializationStream;
   private final RedirectableOutputStream redirectableOutputStream;
   private final long unrollMemory;
   private final MemoryMode memoryMode;
   private final ChunkedByteBufferOutputStream bbos;
   private final Iterator<T> rest;
   private final ClassTag<T> classTag;
   private ChunkedByteBuffer org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer;
   private boolean discarded;
   private boolean consumed;
   private volatile boolean bitmap$0;

   private ChunkedByteBuffer org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.bbos.close();
            this.org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer = this.bbos.toChunkedByteBuffer();
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer;
   }

   private SerializationStream serializationStream() {
      return this.serializationStream;
   }

   private RedirectableOutputStream redirectableOutputStream() {
      return this.redirectableOutputStream;
   }

   public long unrollMemory() {
      return this.unrollMemory;
   }

   public ChunkedByteBuffer org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer() {
      return this.bitmap$0 ? this.org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer : this.org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer$lzycompute();
   }

   public ChunkedByteBuffer getUnrolledChunkedByteBuffer() {
      return this.org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer();
   }

   private void verifyNotConsumedAndNotDiscarded() {
      if (this.consumed) {
         throw new IllegalStateException("Can only call one of finishWritingToStream() or valuesIterator() and can only call once.");
      } else if (this.discarded) {
         throw new IllegalStateException("Cannot call methods on a discarded PartiallySerializedBlock");
      }
   }

   public void discard() {
      if (!this.discarded) {
         try {
            this.redirectableOutputStream().setOutputStream(ByteStreams.nullOutputStream());
            this.serializationStream().close();
         } finally {
            this.discarded = true;
            this.org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer().dispose();
            this.memoryStore.releaseUnrollMemoryForThisTask(this.memoryMode, this.unrollMemory());
         }
      }

   }

   public void finishWritingToStream(OutputStream os) {
      this.verifyNotConsumedAndNotDiscarded();
      this.consumed = true;
      ByteStreams.copy(this.org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer().toInputStream(true), os);
      this.memoryStore.releaseUnrollMemoryForThisTask(this.memoryMode, this.unrollMemory());
      this.redirectableOutputStream().setOutputStream(os);

      while(this.rest.hasNext()) {
         this.serializationStream().writeObject(this.rest.next(), this.classTag);
      }

      this.serializationStream().close();
   }

   public PartiallyUnrolledIterator<T> valuesIterator() {
      this.verifyNotConsumedAndNotDiscarded();
      this.consumed = true;
      this.serializationStream().close();
      Iterator unrolledIter = this.serializerManager.dataDeserializeStream(this.blockId, this.org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer().toInputStream(true), this.classTag);
      return new PartiallyUnrolledIterator<T>(this.memoryStore, this.memoryMode, this.unrollMemory(), unrolledIter, this.rest);
   }

   public PartiallySerializedBlock(MemoryStore memoryStore, SerializerManager serializerManager, BlockId blockId, SerializationStream serializationStream, RedirectableOutputStream redirectableOutputStream, long unrollMemory, MemoryMode memoryMode, ChunkedByteBufferOutputStream bbos, Iterator<T> rest, ClassTag<T> classTag) {
      this.memoryStore = memoryStore;
      this.serializerManager = serializerManager;
      this.blockId = blockId;
      this.serializationStream = serializationStream;
      this.redirectableOutputStream = redirectableOutputStream;
      this.unrollMemory = unrollMemory;
      this.memoryMode = memoryMode;
      this.bbos = bbos;
      this.rest = rest;
      this.classTag = classTag;
      Option$.MODULE$.apply(TaskContext$.MODULE$.get()).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PartiallySerializedBlock $outer;

         public final TaskContext apply(TaskContext taskContext) {
            return taskContext.addTaskCompletionListener((Function1)(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(TaskContext x$4) {
                  this.$outer.org$apache$spark$storage$memory$PartiallySerializedBlock$$anonfun$$$outer().org$apache$spark$storage$memory$PartiallySerializedBlock$$unrolledBuffer().dispose();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }));
         }

         // $FF: synthetic method
         public PartiallySerializedBlock org$apache$spark$storage$memory$PartiallySerializedBlock$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.discarded = false;
      this.consumed = false;
   }
}
