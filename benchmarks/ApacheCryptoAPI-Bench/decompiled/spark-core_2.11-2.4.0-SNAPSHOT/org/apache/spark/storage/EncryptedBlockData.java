package org.apache.spark.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.apache.spark.SparkConf;
import org.apache.spark.network.util.JavaUtils;
import org.apache.spark.security.CryptoStreamUtils$;
import org.apache.spark.util.io.ChunkedByteBuffer;
import org.spark_project.guava.io.Closeables;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.collection.mutable.ListBuffer;
import scala.math.package$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001]4A!\u0001\u0002\u0005\u0017\t\u0011RI\\2ssB$X\r\u001a\"m_\u000e\\G)\u0019;b\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001aE\u0002\u0001\u0019I\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\n\u0015\u001b\u0005\u0011\u0011BA\u000b\u0003\u0005%\u0011En\\2l\t\u0006$\u0018\r\u0003\u0005\u0018\u0001\t\u0005\t\u0015!\u0003\u0019\u0003\u00111\u0017\u000e\\3\u0011\u0005eqR\"\u0001\u000e\u000b\u0005ma\u0012AA5p\u0015\u0005i\u0012\u0001\u00026bm\u0006L!a\b\u000e\u0003\t\u0019KG.\u001a\u0005\tC\u0001\u0011\t\u0011)A\u0005E\u0005I!\r\\8dWNK'0\u001a\t\u0003\u001b\rJ!\u0001\n\b\u0003\t1{gn\u001a\u0005\tM\u0001\u0011\t\u0011)A\u0005O\u0005!1m\u001c8g!\tA\u0013&D\u0001\u0005\u0013\tQCAA\u0005Ta\u0006\u00148nQ8oM\"AA\u0006\u0001B\u0001B\u0003%Q&A\u0002lKf\u00042!\u0004\u00181\u0013\tycBA\u0003BeJ\f\u0017\u0010\u0005\u0002\u000ec%\u0011!G\u0004\u0002\u0005\u0005f$X\rC\u00035\u0001\u0011\u0005Q'\u0001\u0004=S:LGO\u0010\u000b\u0006m]B\u0014H\u000f\t\u0003'\u0001AQaF\u001aA\u0002aAQ!I\u001aA\u0002\tBQAJ\u001aA\u0002\u001dBQ\u0001L\u001aA\u00025BQ\u0001\u0010\u0001\u0005Bu\nQ\u0002^8J]B,Ho\u0015;sK\u0006lG#\u0001 \u0011\u0005ey\u0014B\u0001!\u001b\u0005-Ie\u000e];u'R\u0014X-Y7\t\u000b\t\u0003A\u0011I\"\u0002\u000fQ|g*\u001a;usR\tA\t\u0005\u0002F\u00116\taI\u0003\u0002H9\u0005!A.\u00198h\u0013\tIeI\u0001\u0004PE*,7\r\u001e\u0005\u0006\u0017\u0002!\t\u0005T\u0001\u0014i>\u001c\u0005.\u001e8lK\u0012\u0014\u0015\u0010^3Ck\u001a4WM\u001d\u000b\u0003\u001bR\u0003\"A\u0014*\u000e\u0003=S!a\u0007)\u000b\u0005E#\u0011\u0001B;uS2L!aU(\u0003#\rCWO\\6fI\nKH/\u001a\"vM\u001a,'\u000fC\u0003V\u0015\u0002\u0007a+A\u0005bY2|7-\u0019;peB!QbV-]\u0013\tAfBA\u0005Gk:\u001cG/[8ocA\u0011QBW\u0005\u00037:\u00111!\u00138u!\ti\u0006-D\u0001_\u0015\tyF$A\u0002oS>L!!\u00190\u0003\u0015\tKH/\u001a\"vM\u001a,'\u000fC\u0003d\u0001\u0011\u0005C-\u0001\u0007u_\nKH/\u001a\"vM\u001a,'\u000fF\u0001]\u0011\u00151\u0007\u0001\"\u0011h\u0003\u0011\u0019\u0018N_3\u0016\u0003\tBQ!\u001b\u0001\u0005B)\fq\u0001Z5ta>\u001cX\rF\u0001l!\tiA.\u0003\u0002n\u001d\t!QK\\5u\u0011\u0015y\u0007\u0001\"\u0003q\u0003\u0011y\u0007/\u001a8\u0015\u0003E\u0004\"A];\u000e\u0003MT!\u0001\u001e0\u0002\u0011\rD\u0017M\u001c8fYNL!A^:\u0003'I+\u0017\rZ1cY\u0016\u0014\u0015\u0010^3DQ\u0006tg.\u001a7"
)
public class EncryptedBlockData implements BlockData {
   private final File file;
   private final long blockSize;
   private final SparkConf conf;
   private final byte[] key;

   public InputStream toInputStream() {
      return Channels.newInputStream(this.open());
   }

   public Object toNetty() {
      return new ReadableChannelFileRegion(this.open(), this.blockSize);
   }

   public ChunkedByteBuffer toChunkedByteBuffer(Function1<Object, ByteBuffer> allocator) {
      ReadableByteChannel source = this.open();

      ChunkedByteBuffer var10000;
      try {
         long remaining = this.blockSize;
         ListBuffer chunks = new ListBuffer();

         while(remaining > 0L) {
            long chunkSize = package$.MODULE$.min(remaining, 2147483647L);
            ByteBuffer chunk = (ByteBuffer)allocator.apply(BoxesRunTime.boxToInteger((int)chunkSize));
            remaining -= chunkSize;
            JavaUtils.readFully(source, chunk);
            chunk.flip();
            chunks.$plus$eq(chunk);
         }

         var10000 = new ChunkedByteBuffer((ByteBuffer[])chunks.toArray(ClassTag$.MODULE$.apply(ByteBuffer.class)));
      } finally {
         source.close();
      }

      return var10000;
   }

   public ByteBuffer toByteBuffer() {
      Predef$.MODULE$.assert(this.blockSize <= 2147483647L, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Block is too large to be wrapped in a byte buffer.";
         }
      });
      ByteBuffer dst = ByteBuffer.allocate((int)this.blockSize);
      ReadableByteChannel in = this.open();

      ByteBuffer var10000;
      try {
         JavaUtils.readFully(in, dst);
         dst.flip();
         var10000 = dst;
      } finally {
         Closeables.close(in, true);
      }

      return var10000;
   }

   public long size() {
      return this.blockSize;
   }

   public void dispose() {
   }

   private ReadableByteChannel open() {
      FileChannel channel = (new FileInputStream(this.file)).getChannel();

      try {
         return CryptoStreamUtils$.MODULE$.createReadableChannel(channel, this.conf, this.key);
      } catch (Exception var3) {
         Closeables.close(channel, true);
         throw var3;
      }
   }

   public EncryptedBlockData(File file, long blockSize, SparkConf conf, byte[] key) {
      this.file = file;
      this.blockSize = blockSize;
      this.conf = conf;
      this.key = key;
   }
}
