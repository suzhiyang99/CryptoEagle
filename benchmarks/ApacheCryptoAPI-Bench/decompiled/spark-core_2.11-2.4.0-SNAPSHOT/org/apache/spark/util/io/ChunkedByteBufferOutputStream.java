package org.apache.spark.util.io;

import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.apache.spark.storage.StorageUtils$;
import scala.Array$;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.collection.mutable.ArrayBuffer;
import scala.math.package$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001Y4Q!\u0001\u0002\u0001\r1\u0011Qd\u00115v].,GMQ=uK\n+hMZ3s\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0006\u0003\u0007\u0011\t!![8\u000b\u0005\u00151\u0011\u0001B;uS2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"A\u0004\n\u000e\u0003=Q!a\u0001\t\u000b\u0003E\tAA[1wC&\u00111c\u0004\u0002\r\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\r+\u0001!\t\u0011!B\u0003\u0002\u0003\u0006IaF\u0001B_J<G%\u00199bG\",Ge\u001d9be.$S\u000f^5mI%|Ge\u00115v].,GMQ=uK\n+hMZ3s\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0013%G\",hn[*ju\u0016\u001c\u0001\u0001\u0005\u0002\u001975\t\u0011DC\u0001\u001b\u0003\u0015\u00198-\u00197b\u0013\ta\u0012DA\u0002J]RDAB\b\u0001\u0005\u0002\u0003\u0015)\u0011!Q\u0001\n}\t\u0011i\u001c:hI\u0005\u0004\u0018m\u00195fIM\u0004\u0018M]6%kRLG\u000eJ5pI\rCWO\\6fI\nKH/\u001a\"vM\u001a,'oT;uaV$8\u000b\u001e:fC6$C%\u00197m_\u000e\fGo\u001c:\u0011\ta\u0001sCI\u0005\u0003Ce\u0011\u0011BR;oGRLwN\\\u0019\u0011\u0005\r2S\"\u0001\u0013\u000b\u0005\u0015\u0002\u0012a\u00018j_&\u0011q\u0005\n\u0002\u000b\u0005f$XMQ;gM\u0016\u0014\b\"B\u0015\u0001\t\u0003Q\u0013A\u0002\u001fj]&$h\bF\u0002,[=\u0002\"\u0001\f\u0001\u000e\u0003\tAQA\f\u0015A\u0002]\t\u0011b\u00195v].\u001c\u0016N_3\t\u000bAB\u0003\u0019A\u0010\u0002\u0013\u0005dGn\\2bi>\u0014\bB\u0002\u001a\u0001A\u0003&1'\u0001\u000fu_\u000eCWO\\6fI\nKH/\u001a\"vM\u001a,'oV1t\u0007\u0006dG.\u001a3\u0011\u0005a!\u0014BA\u001b\u001a\u0005\u001d\u0011un\u001c7fC:DAb\u000e\u0001\u0005\u0002\u0003\u0015\tQ1A\u0005\na\nah\u001c:hI\u0005\u0004\u0018m\u00195fIM\u0004\u0018M]6%kRLG\u000eJ5pI\rCWO\\6fI\nKH/\u001a\"vM\u001a,'oT;uaV$8\u000b\u001e:fC6$Ce\u00195v].\u001cX#A\u001d\u0011\u0007iz$%D\u0001<\u0015\taT(A\u0004nkR\f'\r\\3\u000b\u0005yJ\u0012AC2pY2,7\r^5p]&\u0011\u0001i\u000f\u0002\f\u0003J\u0014\u0018-\u001f\"vM\u001a,'\u000fC\u0005C\u0001\t\u0005\t\u0011)A\u0005s\u0005ytN]4%CB\f7\r[3%gB\f'o\u001b\u0013vi&dG%[8%\u0007\",hn[3e\u0005f$XMQ;gM\u0016\u0014x*\u001e;qkR\u001cFO]3b[\u0012\"3\r[;oWN\u0004\u0003\u0002\u0004#\u0001\t\u0003\u0005)\u0011!A!B\u00139\u0012AR8sO\u0012\n\u0007/Y2iK\u0012\u001a\b/\u0019:lIU$\u0018\u000e\u001c\u0013j_\u0012\u001a\u0005.\u001e8lK\u0012\u0014\u0015\u0010^3Ck\u001a4WM](viB,Ho\u0015;sK\u0006lG\u0005\n7bgR\u001c\u0005.\u001e8l\u0013:$W\r\u001f\u0005\r\r\u0002!\t\u0011!B\u0001\u0002\u0003\u0006KaF\u0001A_J<G%\u00199bG\",Ge\u001d9be.$S\u000f^5mI%|Ge\u00115v].,GMQ=uK\n+hMZ3s\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0013%a>\u001c\u0018\u000e^5p]\"1\u0001\n\u0001Q!\n]\tQaX:ju\u0016DaA\u0013\u0001!B\u0013\u0019\u0014AB2m_N,G\rC\u0003M\u0001\u0011\u0005Q*\u0001\u0003tSj,W#\u0001(\u0011\u0005ay\u0015B\u0001)\u001a\u0005\u0011auN\\4\t\u000bI\u0003A\u0011I*\u0002\u000b\rdwn]3\u0015\u0003Q\u0003\"\u0001G+\n\u0005YK\"\u0001B+oSRDQ\u0001\u0017\u0001\u0005Be\u000bQa\u001e:ji\u0016$\"\u0001\u0016.\t\u000bm;\u0006\u0019A\f\u0002\u0003\tDQ\u0001\u0017\u0001\u0005Bu#B\u0001\u00160gQ\")q\f\u0018a\u0001A\u0006)!-\u001f;fgB\u0019\u0001$Y2\n\u0005\tL\"!B!se\u0006L\bC\u0001\re\u0013\t)\u0017D\u0001\u0003CsR,\u0007\"B4]\u0001\u00049\u0012aA8gM\")\u0011\u000e\u0018a\u0001/\u0005\u0019A.\u001a8\t\u000b-\u0004A\u0011B*\u00021\u0005dGn\\2bi\u0016tUm^\"ik:\\\u0017J\u001a(fK\u0012,G\r\u000b\u0002k[B\u0011\u0001D\\\u0005\u0003_f\u0011a!\u001b8mS:,\u0007\"B9\u0001\t\u0003\u0011\u0018a\u0005;p\u0007\",hn[3e\u0005f$XMQ;gM\u0016\u0014X#A:\u0011\u00051\"\u0018BA;\u0003\u0005E\u0019\u0005.\u001e8lK\u0012\u0014\u0015\u0010^3Ck\u001a4WM\u001d"
)
public class ChunkedByteBufferOutputStream extends OutputStream {
   public final int org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunkSize;
   public final Function1<Object, ByteBuffer> org$apache$spark$util$io$ChunkedByteBufferOutputStream$$allocator;
   private boolean toChunkedByteBufferWasCalled;
   private final ArrayBuffer<ByteBuffer> org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks;
   public int org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex;
   public int org$apache$spark$util$io$ChunkedByteBufferOutputStream$$position;
   private int _size;
   private boolean closed;

   public ArrayBuffer<ByteBuffer> org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks() {
      return this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks;
   }

   public long size() {
      return (long)this._size;
   }

   public void close() {
      if (!this.closed) {
         super.close();
         this.closed = true;
      }

   }

   public void write(int b) {
      Predef$.MODULE$.require(!this.closed, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "cannot write to a closed ChunkedByteBufferOutputStream";
         }
      });
      this.allocateNewChunkIfNeeded();
      ((ByteBuffer)this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().apply(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex)).put((byte)b);
      ++this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$position;
      ++this._size;
   }

   public void write(byte[] bytes, int off, int len) {
      Predef$.MODULE$.require(!this.closed, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "cannot write to a closed ChunkedByteBufferOutputStream";
         }
      });

      int thisBatch;
      for(int written = 0; written < len; this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$position += thisBatch) {
         this.allocateNewChunkIfNeeded();
         thisBatch = package$.MODULE$.min(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunkSize - this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$position, len - written);
         ((ByteBuffer)this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().apply(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex)).put(bytes, written + off, thisBatch);
         written += thisBatch;
      }

      this._size += len;
   }

   private void allocateNewChunkIfNeeded() {
      if (this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$position == this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunkSize) {
         this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().$plus$eq(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$allocator.apply(BoxesRunTime.boxToInteger(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunkSize)));
         ++this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex;
         this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$position = 0;
      }

   }

   public ChunkedByteBuffer toChunkedByteBuffer() {
      Predef$.MODULE$.require(this.closed, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "cannot call toChunkedByteBuffer() unless close() has been called";
         }
      });
      Predef$.MODULE$.require(!this.toChunkedByteBufferWasCalled, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "toChunkedByteBuffer() can only be called once";
         }
      });
      this.toChunkedByteBufferWasCalled = true;
      ChunkedByteBuffer var10000;
      if (this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex == -1) {
         var10000 = new ChunkedByteBuffer((ByteBuffer[])Array$.MODULE$.empty(ClassTag$.MODULE$.apply(ByteBuffer.class)));
      } else {
         ByteBuffer[] ret = new ByteBuffer[this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().size()];
         RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().size() - 1).foreach(new Serializable(this, ret) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ChunkedByteBufferOutputStream $outer;
            private final ByteBuffer[] ret$1;

            public final Buffer apply(int i) {
               this.ret$1[i] = (ByteBuffer)this.$outer.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().apply(i);
               return this.ret$1[i].flip();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.ret$1 = ret$1;
               }
            }
         });
         if (this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$position == this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunkSize) {
            ret[this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex] = (ByteBuffer)this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().apply(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex);
            ret[this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex].flip();
         } else {
            ret[this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex] = this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$allocator.apply(BoxesRunTime.boxToInteger(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$position));
            ((Buffer)this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().apply(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex)).flip();
            ret[this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex].put((ByteBuffer)this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().apply(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex));
            ret[this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex].flip();
            StorageUtils$.MODULE$.dispose((ByteBuffer)this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks().apply(this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex));
            BoxedUnit var2 = BoxedUnit.UNIT;
         }

         var10000 = new ChunkedByteBuffer(ret);
      }

      return var10000;
   }

   public ChunkedByteBufferOutputStream(int chunkSize, Function1<Object, ByteBuffer> allocator) {
      this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunkSize = chunkSize;
      this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$allocator = allocator;
      this.toChunkedByteBufferWasCalled = false;
      this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$chunks = new ArrayBuffer<ByteBuffer>();
      this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$lastChunkIndex = -1;
      this.org$apache$spark$util$io$ChunkedByteBufferOutputStream$$position = chunkSize;
      this._size = 0;
      this.closed = false;
   }
}
