package org.apache.spark.util;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000114Q!\u0001\u0002\u0001\t)\u0011abQ5sGVd\u0017M\u001d\"vM\u001a,'O\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\ta\u0011#D\u0001\u000e\u0015\tqq\"\u0001\u0002j_*\t\u0001#\u0001\u0003kCZ\f\u0017B\u0001\n\u000e\u00051yU\u000f\u001e9viN#(/Z1n\u0011!!\u0002A!A!\u0002\u00131\u0012aC:ju\u0016LeNQ=uKN\u001c\u0001\u0001\u0005\u0002\u001855\t\u0001DC\u0001\u001a\u0003\u0015\u00198-\u00197b\u0013\tY\u0002DA\u0002J]RDQ!\b\u0001\u0005\u0002y\ta\u0001P5oSRtDCA\u0010\"!\t\u0001\u0003!D\u0001\u0003\u0011\u001d!B\u0004%AA\u0002YAqa\t\u0001A\u0002\u0013%A%A\u0002q_N,\u0012A\u0006\u0005\bM\u0001\u0001\r\u0011\"\u0003(\u0003\u001d\u0001xn]0%KF$\"\u0001K\u0016\u0011\u0005]I\u0013B\u0001\u0016\u0019\u0005\u0011)f.\u001b;\t\u000f1*\u0013\u0011!a\u0001-\u0005\u0019\u0001\u0010J\u0019\t\r9\u0002\u0001\u0015)\u0003\u0017\u0003\u0011\u0001xn\u001d\u0011\t\u000fA\u0002\u0001\u0019!C\u0005c\u0005a\u0011n\u001d\"vM\u001a,'OR;mYV\t!\u0007\u0005\u0002\u0018g%\u0011A\u0007\u0007\u0002\b\u0005>|G.Z1o\u0011\u001d1\u0004\u00011A\u0005\n]\n\u0001#[:Ck\u001a4WM\u001d$vY2|F%Z9\u0015\u0005!B\u0004b\u0002\u00176\u0003\u0003\u0005\rA\r\u0005\u0007u\u0001\u0001\u000b\u0015\u0002\u001a\u0002\u001b%\u001c()\u001e4gKJ4U\u000f\u001c7!\u0011\u001da\u0004A1A\u0005\nu\naAY;gM\u0016\u0014X#\u0001 \u0011\u0007]y\u0014)\u0003\u0002A1\t)\u0011I\u001d:bsB\u0011qCQ\u0005\u0003\u0007b\u0011AAQ=uK\"1Q\t\u0001Q\u0001\ny\nqAY;gM\u0016\u0014\b\u0005C\u0003H\u0001\u0011\u0005\u0001*A\u0003xe&$X\r\u0006\u0002)\u0013\")!J\u0012a\u0001-\u0005)\u0011N\u001c9vi\")A\n\u0001C!\u001b\u0006AAo\\*ue&tw\rF\u0001O!\ty%K\u0004\u0002\u0018!&\u0011\u0011\u000bG\u0001\u0007!J,G-\u001a4\n\u0005M#&AB*ue&twM\u0003\u0002R1\u001dAaKAA\u0001\u0012\u0003!q+\u0001\bDSJ\u001cW\u000f\\1s\u0005V4g-\u001a:\u0011\u0005\u0001Bf\u0001C\u0001\u0003\u0003\u0003E\t\u0001B-\u0014\u0005aS\u0006CA\f\\\u0013\ta\u0006D\u0001\u0004B]f\u0014VM\u001a\u0005\u0006;a#\tA\u0018\u000b\u0002/\"9\u0001\rWI\u0001\n\u0003\t\u0017a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$\u0013'F\u0001cU\t12mK\u0001e!\t)'.D\u0001g\u0015\t9\u0007.A\u0005v]\u000eDWmY6fI*\u0011\u0011\u000eG\u0001\u000bC:tw\u000e^1uS>t\u0017BA6g\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a"
)
public class CircularBuffer extends OutputStream {
   private final int sizeInBytes;
   private int pos;
   private boolean isBufferFull;
   private final byte[] buffer;

   public static int $lessinit$greater$default$1() {
      return CircularBuffer$.MODULE$.$lessinit$greater$default$1();
   }

   private int pos() {
      return this.pos;
   }

   private void pos_$eq(int x$1) {
      this.pos = x$1;
   }

   private boolean isBufferFull() {
      return this.isBufferFull;
   }

   private void isBufferFull_$eq(boolean x$1) {
      this.isBufferFull = x$1;
   }

   private byte[] buffer() {
      return this.buffer;
   }

   public void write(int input) {
      this.buffer()[this.pos()] = (byte)input;
      this.pos_$eq((this.pos() + 1) % this.buffer().length);
      this.isBufferFull_$eq(this.isBufferFull() || this.pos() == 0);
   }

   public String toString() {
      if (this.isBufferFull()) {
         byte[] nonCircularBuffer = new byte[this.sizeInBytes];
         System.arraycopy(this.buffer(), this.pos(), nonCircularBuffer, 0, this.buffer().length - this.pos());
         System.arraycopy(this.buffer(), 0, nonCircularBuffer, this.buffer().length - this.pos(), this.pos());
         return new String(nonCircularBuffer, StandardCharsets.UTF_8);
      } else {
         return new String(this.buffer(), 0, this.pos(), StandardCharsets.UTF_8);
      }
   }

   public CircularBuffer(int sizeInBytes) {
      this.sizeInBytes = sizeInBytes;
      this.pos = 0;
      this.isBufferFull = false;
      this.buffer = new byte[sizeInBytes];
   }
}
