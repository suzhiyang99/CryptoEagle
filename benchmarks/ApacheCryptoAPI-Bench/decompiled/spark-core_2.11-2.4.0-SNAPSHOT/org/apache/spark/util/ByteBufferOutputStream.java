package org.apache.spark.util;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001=3Q!\u0001\u0002\u0001\t)\u0011aCQ=uK\n+hMZ3s\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0006\u0003\u0007\u0011\tA!\u001e;jY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011A\"E\u0007\u0002\u001b)\u0011abD\u0001\u0003S>T\u0011\u0001E\u0001\u0005U\u00064\u0018-\u0003\u0002\u0013\u001b\t)\")\u001f;f\u0003J\u0014\u0018-_(viB,Ho\u0015;sK\u0006l\u0007\u0002\u0003\u000b\u0001\u0005\u0003\u0005\u000b\u0011\u0002\f\u0002\u0011\r\f\u0007/Y2jif\u001c\u0001\u0001\u0005\u0002\u001855\t\u0001DC\u0001\u001a\u0003\u0015\u00198-\u00197b\u0013\tY\u0002DA\u0002J]RDQ!\b\u0001\u0005\u0002y\ta\u0001P5oSRtDCA\u0010\"!\t\u0001\u0003!D\u0001\u0003\u0011\u0015!B\u00041\u0001\u0017\u0011\u0015i\u0002\u0001\"\u0001$)\u0005y\u0002\"B\u0013\u0001\t\u00031\u0013\u0001C4fi\u000e{WO\u001c;\u0015\u0003YAa\u0001\u000b\u0001!B\u0013I\u0013AB2m_N,G\r\u0005\u0002\u0018U%\u00111\u0006\u0007\u0002\b\u0005>|G.Z1o\u0011\u0015i\u0003\u0001\"\u0011/\u0003\u00159(/\u001b;f)\ty#\u0007\u0005\u0002\u0018a%\u0011\u0011\u0007\u0007\u0002\u0005+:LG\u000fC\u00034Y\u0001\u0007a#A\u0001c\u0011\u0015i\u0003\u0001\"\u00116)\u0011yc'P \t\u000bM\"\u0004\u0019A\u001c\u0011\u0007]A$(\u0003\u0002:1\t)\u0011I\u001d:bsB\u0011qcO\u0005\u0003ya\u0011AAQ=uK\")a\b\u000ea\u0001-\u0005\u0019qN\u001a4\t\u000b\u0001#\u0004\u0019\u0001\f\u0002\u00071,g\u000eC\u0003C\u0001\u0011\u00053)A\u0003sKN,G\u000fF\u00010\u0011\u0015)\u0005\u0001\"\u0011D\u0003\u0015\u0019Gn\\:f\u0011\u00159\u0005\u0001\"\u0001I\u00031!xNQ=uK\n+hMZ3s+\u0005I\u0005C\u0001&N\u001b\u0005Y%B\u0001'\u0010\u0003\rq\u0017n\\\u0005\u0003\u001d.\u0013!BQ=uK\n+hMZ3s\u0001"
)
public class ByteBufferOutputStream extends ByteArrayOutputStream {
   private boolean closed;

   public int getCount() {
      return this.count;
   }

   public void write(int b) {
      Predef$.MODULE$.require(!this.closed, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "cannot write to a closed ByteBufferOutputStream";
         }
      });
      super.write(b);
   }

   public void write(byte[] b, int off, int len) {
      Predef$.MODULE$.require(!this.closed, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "cannot write to a closed ByteBufferOutputStream";
         }
      });
      super.write(b, off, len);
   }

   public void reset() {
      Predef$.MODULE$.require(!this.closed, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "cannot reset a closed ByteBufferOutputStream";
         }
      });
      super.reset();
   }

   public void close() {
      if (!this.closed) {
         super.close();
         this.closed = true;
      }

   }

   public ByteBuffer toByteBuffer() {
      Predef$.MODULE$.require(this.closed, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "can only call toByteBuffer() after ByteBufferOutputStream has been closed";
         }
      });
      return ByteBuffer.wrap(this.buf, 0, this.count);
   }

   public ByteBufferOutputStream(int capacity) {
      super(capacity);
      this.closed = false;
   }

   public ByteBufferOutputStream() {
      this(32);
   }
}
