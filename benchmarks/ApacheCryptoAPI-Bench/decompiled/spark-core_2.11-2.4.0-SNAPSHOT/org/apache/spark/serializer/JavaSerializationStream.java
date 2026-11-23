package org.apache.spark.serializer;

import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\t4Q!\u0001\u0002\u0001\t)\u0011qCS1wCN+'/[1mSj\fG/[8o'R\u0014X-Y7\u000b\u0005\r!\u0011AC:fe&\fG.\u001b>fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011A\"D\u0007\u0002\u0005%\u0011aB\u0001\u0002\u0014'\u0016\u0014\u0018.\u00197ju\u0006$\u0018n\u001c8TiJ,\u0017-\u001c\u0005\t!\u0001\u0011\t\u0011)A\u0005%\u0005\u0019q.\u001e;\u0004\u0001A\u00111\u0003G\u0007\u0002))\u0011QCF\u0001\u0003S>T\u0011aF\u0001\u0005U\u00064\u0018-\u0003\u0002\u001a)\taq*\u001e;qkR\u001cFO]3b[\"A1\u0004\u0001B\u0001B\u0003%A$\u0001\u0007d_VtG/\u001a:SKN,G\u000f\u0005\u0002\u001eA5\taDC\u0001 \u0003\u0015\u00198-\u00197b\u0013\t\tcDA\u0002J]RD\u0001b\t\u0001\u0003\u0002\u0003\u0006I\u0001J\u0001\u000fKb$(/\u0019#fEV<\u0017J\u001c4p!\tiR%\u0003\u0002'=\t9!i\\8mK\u0006t\u0007\"\u0002\u0015\u0001\t\u0003I\u0013A\u0002\u001fj]&$h\b\u0006\u0003+W1j\u0003C\u0001\u0007\u0001\u0011\u0015\u0001r\u00051\u0001\u0013\u0011\u0015Yr\u00051\u0001\u001d\u0011\u0015\u0019s\u00051\u0001%\u0011\u001dy\u0003A1A\u0005\nA\naa\u001c2k\u001fV$X#A\u0019\u0011\u0005M\u0011\u0014BA\u001a\u0015\u0005Iy%M[3di>+H\u000f];u'R\u0014X-Y7\t\rU\u0002\u0001\u0015!\u00032\u0003\u001dy'M[(vi\u0002Bqa\u000e\u0001A\u0002\u0013%\u0001(A\u0004d_VtG/\u001a:\u0016\u0003qAqA\u000f\u0001A\u0002\u0013%1(A\u0006d_VtG/\u001a:`I\u0015\fHC\u0001\u001f@!\tiR(\u0003\u0002?=\t!QK\\5u\u0011\u001d\u0001\u0015(!AA\u0002q\t1\u0001\u001f\u00132\u0011\u0019\u0011\u0005\u0001)Q\u00059\u0005A1m\\;oi\u0016\u0014\b\u0005C\u0003E\u0001\u0011\u0005Q)A\u0006xe&$Xm\u00142kK\u000e$XC\u0001$S)\t95\f\u0006\u0002\f\u0011\"9\u0011jQA\u0001\u0002\bQ\u0015AC3wS\u0012,gnY3%cA\u00191J\u0014)\u000e\u00031S!!\u0014\u0010\u0002\u000fI,g\r\\3di&\u0011q\n\u0014\u0002\t\u00072\f7o\u001d+bOB\u0011\u0011K\u0015\u0007\u0001\t\u0015\u00196I1\u0001U\u0005\u0005!\u0016CA+Y!\tib+\u0003\u0002X=\t9aj\u001c;iS:<\u0007CA\u000fZ\u0013\tQfDA\u0002B]fDQ\u0001X\"A\u0002A\u000b\u0011\u0001\u001e\u0005\u0006=\u0002!\taX\u0001\u0006M2,8\u000f\u001b\u000b\u0002y!)\u0011\r\u0001C\u0001?\u0006)1\r\\8tK\u0002"
)
public class JavaSerializationStream extends SerializationStream {
   private final int counterReset;
   private final boolean extraDebugInfo;
   private final ObjectOutputStream objOut;
   private int counter;

   private ObjectOutputStream objOut() {
      return this.objOut;
   }

   private int counter() {
      return this.counter;
   }

   private void counter_$eq(int x$1) {
      this.counter = x$1;
   }

   public <T> SerializationStream writeObject(T t, ClassTag<T> evidence$1) {
      try {
         this.objOut().writeObject(t);
      } catch (Throwable var7) {
         if (var7 instanceof NotSerializableException) {
            NotSerializableException var5 = (NotSerializableException)var7;
            if (this.extraDebugInfo) {
               throw SerializationDebugger$.MODULE$.improveException(t, var5);
            }
         }

         throw var7;
      }

      this.counter_$eq(this.counter() + 1);
      if (this.counterReset > 0 && this.counter() >= this.counterReset) {
         this.objOut().reset();
         this.counter_$eq(0);
      }

      return this;
   }

   public void flush() {
      this.objOut().flush();
   }

   public void close() {
      this.objOut().close();
   }

   public JavaSerializationStream(OutputStream out, int counterReset, boolean extraDebugInfo) {
      this.counterReset = counterReset;
      this.extraDebugInfo = extraDebugInfo;
      this.objOut = new ObjectOutputStream(out);
      this.counter = 0;
   }
}
