package org.apache.spark.storage;

import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.spark.util.io.ChunkedByteBuffer;
import scala.Function1;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00114Q!\u0001\u0002\u0001\t)\u00111CQ=uK\n+hMZ3s\u00052|7m\u001b#bi\u0006T!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0014\u001b\u0005\u0011\u0011B\u0001\u000b\u0003\u0005%\u0011En\\2l\t\u0006$\u0018\r\u0003\u0005\u0017\u0001\t\u0015\r\u0011\"\u0001\u0019\u0003\u0019\u0011WO\u001a4fe\u000e\u0001Q#A\r\u0011\u0005iyR\"A\u000e\u000b\u0005qi\u0012AA5p\u0015\tqB!\u0001\u0003vi&d\u0017B\u0001\u0011\u001c\u0005E\u0019\u0005.\u001e8lK\u0012\u0014\u0015\u0010^3Ck\u001a4WM\u001d\u0005\tE\u0001\u0011\t\u0011)A\u00053\u00059!-\u001e4gKJ\u0004\u0003\u0002\u0003\u0013\u0001\u0005\u000b\u0007I\u0011A\u0013\u0002\u001bMDw.\u001e7e\t&\u001c\bo\\:f+\u00051\u0003C\u0001\u0007(\u0013\tASBA\u0004C_>dW-\u00198\t\u0011)\u0002!\u0011!Q\u0001\n\u0019\nab\u001d5pk2$G)[:q_N,\u0007\u0005C\u0003-\u0001\u0011\u0005Q&\u0001\u0004=S:LGO\u0010\u000b\u0004]=\u0002\u0004C\u0001\n\u0001\u0011\u001512\u00061\u0001\u001a\u0011\u0015!3\u00061\u0001'\u0011\u0015\u0011\u0004\u0001\"\u00114\u00035!x.\u00138qkR\u001cFO]3b[R\tA\u0007\u0005\u00026s5\taG\u0003\u0002\u001do)\t\u0001(\u0001\u0003kCZ\f\u0017B\u0001\u001e7\u0005-Ie\u000e];u'R\u0014X-Y7\t\u000bq\u0002A\u0011I\u001f\u0002\u000fQ|g*\u001a;usR\ta\b\u0005\u0002@\u00056\t\u0001I\u0003\u0002Bo\u0005!A.\u00198h\u0013\t\u0019\u0005I\u0001\u0004PE*,7\r\u001e\u0005\u0006\u000b\u0002!\tER\u0001\u0014i>\u001c\u0005.\u001e8lK\u0012\u0014\u0015\u0010^3Ck\u001a4WM\u001d\u000b\u00033\u001dCQ\u0001\u0013#A\u0002%\u000b\u0011\"\u00197m_\u000e\fGo\u001c:\u0011\t1QEjT\u0005\u0003\u00176\u0011\u0011BR;oGRLwN\\\u0019\u0011\u00051i\u0015B\u0001(\u000e\u0005\rIe\u000e\u001e\t\u0003!Nk\u0011!\u0015\u0006\u0003%^\n1A\\5p\u0013\t!\u0016K\u0001\u0006CsR,')\u001e4gKJDQA\u0016\u0001\u0005B]\u000bA\u0002^8CsR,')\u001e4gKJ$\u0012a\u0014\u0005\u00063\u0002!\tEW\u0001\u0005g&TX-F\u0001\\!\taA,\u0003\u0002^\u001b\t!Aj\u001c8h\u0011\u0015y\u0006\u0001\"\u0011a\u0003\u001d!\u0017n\u001d9pg\u0016$\u0012!\u0019\t\u0003\u0019\tL!aY\u0007\u0003\tUs\u0017\u000e\u001e"
)
public class ByteBufferBlockData implements BlockData {
   private final ChunkedByteBuffer buffer;
   private final boolean shouldDispose;

   public ChunkedByteBuffer buffer() {
      return this.buffer;
   }

   public boolean shouldDispose() {
      return this.shouldDispose;
   }

   public InputStream toInputStream() {
      return this.buffer().toInputStream(false);
   }

   public Object toNetty() {
      return this.buffer().toNetty();
   }

   public ChunkedByteBuffer toChunkedByteBuffer(Function1<Object, ByteBuffer> allocator) {
      return this.buffer().copy(allocator);
   }

   public ByteBuffer toByteBuffer() {
      return this.buffer().toByteBuffer();
   }

   public long size() {
      return this.buffer().size();
   }

   public void dispose() {
      if (this.shouldDispose()) {
         this.buffer().dispose();
      }

   }

   public ByteBufferBlockData(ChunkedByteBuffer buffer, boolean shouldDispose) {
      this.buffer = buffer;
      this.shouldDispose = shouldDispose;
   }
}
