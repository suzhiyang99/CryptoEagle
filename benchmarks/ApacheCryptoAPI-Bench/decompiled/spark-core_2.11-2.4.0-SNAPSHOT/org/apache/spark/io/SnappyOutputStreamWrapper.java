package org.apache.spark.io;

import java.io.IOException;
import java.io.OutputStream;
import org.xerial.snappy.SnappyOutputStream;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-3A!\u0001\u0002\u0007\u0017\tI2K\\1qaf|U\u000f\u001e9viN#(/Z1n/J\f\u0007\u000f]3s\u0015\t\u0019A!\u0001\u0002j_*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001a\u0001CA\u0007\u0012\u001b\u0005q!BA\u0002\u0010\u0015\u0005\u0001\u0012\u0001\u00026bm\u0006L!A\u0005\b\u0003\u0019=+H\u000f];u'R\u0014X-Y7\t\u0011Q\u0001!\u0011!Q\u0001\nU\t!a\\:\u0011\u0005YYR\"A\f\u000b\u0005aI\u0012AB:oCB\u0004\u0018P\u0003\u0002\u001b\u0011\u00051\u00010\u001a:jC2L!\u0001H\f\u0003%Ms\u0017\r\u001d9z\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\u0006=\u0001!\taH\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\u0001\u0012\u0003CA\u0011\u0001\u001b\u0005\u0011\u0001\"\u0002\u000b\u001e\u0001\u0004)\u0002B\u0002\u0013\u0001A\u0003&Q%\u0001\u0004dY>\u001cX\r\u001a\t\u0003M%j\u0011a\n\u0006\u0002Q\u0005)1oY1mC&\u0011!f\n\u0002\b\u0005>|G.Z1o\u0011\u0015a\u0003\u0001\"\u0011.\u0003\u00159(/\u001b;f)\tq\u0013\u0007\u0005\u0002'_%\u0011\u0001g\n\u0002\u0005+:LG\u000fC\u00033W\u0001\u00071'A\u0001c!\t1C'\u0003\u00026O\t\u0019\u0011J\u001c;\t\u000b1\u0002A\u0011I\u001c\u0015\u00059B\u0004\"\u0002\u001a7\u0001\u0004I\u0004c\u0001\u0014;y%\u00111h\n\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003MuJ!AP\u0014\u0003\t\tKH/\u001a\u0005\u0006Y\u0001!\t\u0005\u0011\u000b\u0005]\u0005\u0013E\tC\u00033\u007f\u0001\u0007\u0011\bC\u0003D\u007f\u0001\u00071'A\u0002pM\u001aDQ!R A\u0002M\n1\u0001\\3o\u0011\u00159\u0005\u0001\"\u0011I\u0003\u00151G.^:i)\u0005q\u0003\"\u0002&\u0001\t\u0003B\u0015!B2m_N,\u0007"
)
public final class SnappyOutputStreamWrapper extends OutputStream {
   private final SnappyOutputStream os;
   private boolean closed;

   public void write(int b) {
      if (this.closed) {
         throw new IOException("Stream is closed");
      } else {
         this.os.write(b);
      }
   }

   public void write(byte[] b) {
      if (this.closed) {
         throw new IOException("Stream is closed");
      } else {
         this.os.write(b);
      }
   }

   public void write(byte[] b, int off, int len) {
      if (this.closed) {
         throw new IOException("Stream is closed");
      } else {
         this.os.write(b, off, len);
      }
   }

   public void flush() {
      if (this.closed) {
         throw new IOException("Stream is closed");
      } else {
         this.os.flush();
      }
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.os.close();
      }

   }

   public SnappyOutputStreamWrapper(SnappyOutputStream os) {
      this.os = os;
      this.closed = false;
   }
}
