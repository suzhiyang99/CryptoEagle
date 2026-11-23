package org.apache.spark.storage;

import io.netty.channel.DefaultFileRegion;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import org.apache.spark.network.util.JavaUtils;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.io.ChunkedByteBuffer;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.StringBuilder;
import scala.math.package$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d4A!\u0001\u0002\u0005\u0017\tiA)[:l\u00052|7m\u001b#bi\u0006T!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003'Qi\u0011AA\u0005\u0003+\t\u0011\u0011B\u00117pG.$\u0015\r^1\t\u0011]\u0001!\u0011!Q\u0001\na\t\u0011#\\5o\u001b\u0016lwN]=NCB\u0014\u0015\u0010^3t!\ti\u0011$\u0003\u0002\u001b\u001d\t!Aj\u001c8h\u0011!a\u0002A!A!\u0002\u0013A\u0012!E7bq6+Wn\u001c:z\u001b\u0006\u0004()\u001f;fg\"Aa\u0004\u0001B\u0001B\u0003%q$\u0001\u0003gS2,\u0007C\u0001\u0011&\u001b\u0005\t#B\u0001\u0012$\u0003\tIwNC\u0001%\u0003\u0011Q\u0017M^1\n\u0005\u0019\n#\u0001\u0002$jY\u0016D\u0001\u0002\u000b\u0001\u0003\u0002\u0003\u0006I\u0001G\u0001\nE2|7m[*ju\u0016DQA\u000b\u0001\u0005\u0002-\na\u0001P5oSRtD#\u0002\u0017.]=\u0002\u0004CA\n\u0001\u0011\u00159\u0012\u00061\u0001\u0019\u0011\u0015a\u0012\u00061\u0001\u0019\u0011\u0015q\u0012\u00061\u0001 \u0011\u0015A\u0013\u00061\u0001\u0019\u0011\u0015\u0011\u0004\u0001\"\u00114\u00035!x.\u00138qkR\u001cFO]3b[R\tA\u0007\u0005\u0002!k%\u0011a'\t\u0002\f\u0013:\u0004X\u000f^*ue\u0016\fW\u000eC\u00039\u0001\u0011\u0005\u0013(A\u0004u_:+G\u000f^=\u0015\u00031AQa\u000f\u0001\u0005Bq\n1\u0003^8DQVt7.\u001a3CsR,')\u001e4gKJ$\"!\u0010#\u0011\u0005y\u0012U\"A \u000b\u0005\t\u0002%BA!\u0005\u0003\u0011)H/\u001b7\n\u0005\r{$!E\"ik:\\W\r\u001a\"zi\u0016\u0014UO\u001a4fe\")QI\u000fa\u0001\r\u0006I\u0011\r\u001c7pG\u0006$xN\u001d\t\u0005\u001b\u001dKE*\u0003\u0002I\u001d\tIa)\u001e8di&|g.\r\t\u0003\u001b)K!a\u0013\b\u0003\u0007%sG\u000f\u0005\u0002N!6\taJ\u0003\u0002PG\u0005\u0019a.[8\n\u0005Es%A\u0003\"zi\u0016\u0014UO\u001a4fe\")1\u000b\u0001C!)\u0006aAo\u001c\"zi\u0016\u0014UO\u001a4feR\tA\nC\u0003W\u0001\u0011\u0005s+\u0001\u0003tSj,W#\u0001\r\t\u000be\u0003A\u0011\t.\u0002\u000f\u0011L7\u000f]8tKR\t1\f\u0005\u0002\u000e9&\u0011QL\u0004\u0002\u0005+:LG\u000fC\u0003`\u0001\u0011%\u0001-\u0001\u0003pa\u0016tG#A1\u0011\u0005\t,W\"A2\u000b\u0005\u0011t\u0015\u0001C2iC:tW\r\\:\n\u0005\u0019\u001c'a\u0003$jY\u0016\u001c\u0005.\u00198oK2\u0004"
)
public class DiskBlockData implements BlockData {
   public final long org$apache$spark$storage$DiskBlockData$$minMemoryMapBytes;
   public final long org$apache$spark$storage$DiskBlockData$$maxMemoryMapBytes;
   public final File org$apache$spark$storage$DiskBlockData$$file;
   public final long org$apache$spark$storage$DiskBlockData$$blockSize;

   public InputStream toInputStream() {
      return new FileInputStream(this.org$apache$spark$storage$DiskBlockData$$file);
   }

   public Object toNetty() {
      return new DefaultFileRegion(this.org$apache$spark$storage$DiskBlockData$$file, 0L, this.size());
   }

   public ChunkedByteBuffer toChunkedByteBuffer(Function1<Object, ByteBuffer> allocator) {
      return (ChunkedByteBuffer)Utils$.MODULE$.tryWithResource(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockData $outer;

         public final FileChannel apply() {
            return this.$outer.org$apache$spark$storage$DiskBlockData$$open();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, new Serializable(this, allocator) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockData $outer;
         private final Function1 allocator$1;

         public final ChunkedByteBuffer apply(FileChannel channel) {
            long remaining = this.$outer.org$apache$spark$storage$DiskBlockData$$blockSize;
            ListBuffer chunks = new ListBuffer();

            while(remaining > 0L) {
               long chunkSize = package$.MODULE$.min(remaining, this.$outer.org$apache$spark$storage$DiskBlockData$$maxMemoryMapBytes);
               ByteBuffer chunk = (ByteBuffer)this.allocator$1.apply(BoxesRunTime.boxToInteger((int)chunkSize));
               remaining -= chunkSize;
               JavaUtils.readFully(channel, chunk);
               chunk.flip();
               chunks.$plus$eq(chunk);
            }

            return new ChunkedByteBuffer((ByteBuffer[])chunks.toArray(ClassTag$.MODULE$.apply(ByteBuffer.class)));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.allocator$1 = allocator$1;
            }
         }
      });
   }

   public ByteBuffer toByteBuffer() {
      Predef$.MODULE$.require(this.org$apache$spark$storage$DiskBlockData$$blockSize < this.org$apache$spark$storage$DiskBlockData$$maxMemoryMapBytes, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockData $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"can't create a byte buffer of size ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$DiskBlockData$$blockSize)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" since it exceeds ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$storage$DiskBlockData$$maxMemoryMapBytes)}))).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      return (ByteBuffer)Utils$.MODULE$.tryWithResource(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockData $outer;

         public final FileChannel apply() {
            return this.$outer.org$apache$spark$storage$DiskBlockData$$open();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockData $outer;

         public final ByteBuffer apply(FileChannel channel) {
            Object var10000;
            if (this.$outer.org$apache$spark$storage$DiskBlockData$$blockSize < this.$outer.org$apache$spark$storage$DiskBlockData$$minMemoryMapBytes) {
               ByteBuffer buf = ByteBuffer.allocate((int)this.$outer.org$apache$spark$storage$DiskBlockData$$blockSize);
               JavaUtils.readFully(channel, buf);
               buf.flip();
               var10000 = buf;
            } else {
               var10000 = channel.map(MapMode.READ_ONLY, 0L, this.$outer.org$apache$spark$storage$DiskBlockData$$file.length());
            }

            return (ByteBuffer)var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public long size() {
      return this.org$apache$spark$storage$DiskBlockData$$blockSize;
   }

   public void dispose() {
   }

   public FileChannel org$apache$spark$storage$DiskBlockData$$open() {
      return (new FileInputStream(this.org$apache$spark$storage$DiskBlockData$$file)).getChannel();
   }

   public DiskBlockData(long minMemoryMapBytes, long maxMemoryMapBytes, File file, long blockSize) {
      this.org$apache$spark$storage$DiskBlockData$$minMemoryMapBytes = minMemoryMapBytes;
      this.org$apache$spark$storage$DiskBlockData$$maxMemoryMapBytes = maxMemoryMapBytes;
      this.org$apache$spark$storage$DiskBlockData$$file = file;
      this.org$apache$spark$storage$DiskBlockData$$blockSize = blockSize;
   }
}
