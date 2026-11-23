package org.apache.spark.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%3Q!\u0001\u0002\u0001\t)\u0011!cU3sS\u0006d\u0017N_1cY\u0016\u0014UO\u001a4fe*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\"\u0005\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00051\u0011\u0012BA\n\u000e\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!)\u0002A!a\u0001\n\u00039\u0012A\u00022vM\u001a,'o\u0001\u0001\u0016\u0003a\u0001\"!\u0007\u0010\u000e\u0003iQ!a\u0007\u000f\u0002\u00079LwNC\u0001\u001e\u0003\u0011Q\u0017M^1\n\u0005}Q\"A\u0003\"zi\u0016\u0014UO\u001a4fe\"A\u0011\u0005\u0001BA\u0002\u0013\u0005!%\u0001\u0006ck\u001a4WM]0%KF$\"a\t\u0014\u0011\u00051!\u0013BA\u0013\u000e\u0005\u0011)f.\u001b;\t\u000f\u001d\u0002\u0013\u0011!a\u00011\u0005\u0019\u0001\u0010J\u0019\t\u0011%\u0002!\u0011!Q!\na\tqAY;gM\u0016\u0014\b\u0005\u000b\u0002)WA\u0011A\u0002L\u0005\u0003[5\u0011\u0011\u0002\u001e:b]NLWM\u001c;\t\u000b=\u0002A\u0011\u0001\u0019\u0002\rqJg.\u001b;?)\t\t4\u0007\u0005\u00023\u00015\t!\u0001C\u0003\u0016]\u0001\u0007\u0001\u0004C\u00036\u0001\u0011\u0005q#A\u0003wC2,X\rC\u00038\u0001\u0011%\u0001(\u0001\u0006sK\u0006$wJ\u00196fGR$\"aI\u001d\t\u000bi2\u0004\u0019A\u001e\u0002\u0005%t\u0007C\u0001\u001f@\u001b\u0005i$B\u0001 \u001d\u0003\tIw.\u0003\u0002A{\t\trJ\u00196fGRLe\u000e];u'R\u0014X-Y7\t\u000b\t\u0003A\u0011B\"\u0002\u0017]\u0014\u0018\u000e^3PE*,7\r\u001e\u000b\u0003G\u0011CQ!R!A\u0002\u0019\u000b1a\\;u!\tat)\u0003\u0002I{\t\u0011rJ\u00196fGR|U\u000f\u001e9viN#(/Z1n\u0001"
)
public class SerializableBuffer implements Serializable {
   private transient ByteBuffer buffer;

   public ByteBuffer buffer() {
      return this.buffer;
   }

   public void buffer_$eq(ByteBuffer x$1) {
      this.buffer = x$1;
   }

   public ByteBuffer value() {
      return this.buffer();
   }

   private void readObject(ObjectInputStream in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SerializableBuffer $outer;
         private final ObjectInputStream in$1;

         public final Buffer apply() {
            int length = this.in$1.readInt();
            this.$outer.buffer_$eq(ByteBuffer.allocate(length));
            int amountRead = 0;

            int ret;
            for(ReadableByteChannel channel = Channels.newChannel(this.in$1); amountRead < length; amountRead += ret) {
               ret = channel.read(this.$outer.buffer());
               if (ret == -1) {
                  throw new EOFException("End of file before fully reading buffer");
               }
            }

            return this.$outer.buffer().rewind();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   private void writeObject(ObjectOutputStream out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SerializableBuffer $outer;
         private final ObjectOutputStream out$1;

         public final Buffer apply() {
            this.out$1.writeInt(this.$outer.buffer().limit());
            if (Channels.newChannel(this.out$1).write(this.$outer.buffer()) != this.$outer.buffer().limit()) {
               throw new IOException("Could not fully write buffer to output stream");
            } else {
               return this.$outer.buffer().rewind();
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.out$1 = out$1;
            }
         }
      });
   }

   public SerializableBuffer(ByteBuffer buffer) {
      this.buffer = buffer;
      super();
   }
}
