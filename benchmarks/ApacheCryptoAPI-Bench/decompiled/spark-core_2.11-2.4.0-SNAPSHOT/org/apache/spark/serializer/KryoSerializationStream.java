package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import java.io.IOException;
import java.io.OutputStream;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\t4Q!\u0001\u0002\u0001\t)\u0011qc\u0013:z_N+'/[1mSj\fG/[8o'R\u0014X-Y7\u000b\u0005\r!\u0011AC:fe&\fG.\u001b>fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011A\"D\u0007\u0002\u0005%\u0011aB\u0001\u0002\u0014'\u0016\u0014\u0018.\u00197ju\u0006$\u0018n\u001c8TiJ,\u0017-\u001c\u0005\t!\u0001\u0011\t\u0011)A\u0005%\u0005Y1/\u001a:J]N$\u0018M\\2f\u0007\u0001\u0001\"\u0001D\n\n\u0005Q\u0011!AF&ss>\u001cVM]5bY&TXM]%ogR\fgnY3\t\u0011Y\u0001!\u0011!Q\u0001\n]\t\u0011b\\;u'R\u0014X-Y7\u0011\u0005aiR\"A\r\u000b\u0005iY\u0012AA5p\u0015\u0005a\u0012\u0001\u00026bm\u0006L!AH\r\u0003\u0019=+H\u000f];u'R\u0014X-Y7\t\u0011\u0001\u0002!\u0011!Q\u0001\n\u0005\n\u0011\"^:f+:\u001c\u0018MZ3\u0011\u0005\t*S\"A\u0012\u000b\u0003\u0011\nQa]2bY\u0006L!AJ\u0012\u0003\u000f\t{w\u000e\\3b]\")\u0001\u0006\u0001C\u0001S\u00051A(\u001b8jiz\"BAK\u0016-[A\u0011A\u0002\u0001\u0005\u0006!\u001d\u0002\rA\u0005\u0005\u0006-\u001d\u0002\ra\u0006\u0005\u0006A\u001d\u0002\r!\t\u0005\u0007_\u0001\u0001\u000b\u0015\u0002\u0019\u0002\r=,H\u000f];u!\t\t\u0014(D\u00013\u0015\tQ2G\u0003\u00025k\u0005!1N]=p\u0015\t1t'\u0001\tfg>$XM]5dg>4Go^1sK*\t\u0001(A\u0002d_6L!A\u000f\u001a\u0003\r=+H\u000f];u\u0011\u0019!\u0004\u0001)Q\u0005yA\u0011QHP\u0007\u0002g%\u0011qh\r\u0002\u0005\u0017JLx\u000eC\u0003B\u0001\u0011\u0005#)A\u0006xe&$Xm\u00142kK\u000e$XCA\"P)\t!\u0005\f\u0006\u0002\f\u000b\"9a\tQA\u0001\u0002\b9\u0015AC3wS\u0012,gnY3%cA\u0019\u0001jS'\u000e\u0003%S!AS\u0012\u0002\u000fI,g\r\\3di&\u0011A*\u0013\u0002\t\u00072\f7o\u001d+bOB\u0011aj\u0014\u0007\u0001\t\u0015\u0001\u0006I1\u0001R\u0005\u0005!\u0016C\u0001*V!\t\u00113+\u0003\u0002UG\t9aj\u001c;iS:<\u0007C\u0001\u0012W\u0013\t96EA\u0002B]fDQ!\u0017!A\u00025\u000b\u0011\u0001\u001e\u0005\u00067\u0002!\t\u0005X\u0001\u0006M2,8\u000f\u001b\u000b\u0002;B\u0011!EX\u0005\u0003?\u000e\u0012A!\u00168ji\")\u0011\r\u0001C!9\u0006)1\r\\8tK\u0002"
)
public class KryoSerializationStream extends SerializationStream {
   private final KryoSerializerInstance serInstance;
   private Output output;
   private Kryo kryo;

   public <T> SerializationStream writeObject(T t, ClassTag<T> evidence$1) {
      this.kryo.writeClassAndObject(this.output, t);
      return this;
   }

   public void flush() {
      if (this.output == null) {
         throw new IOException("Stream is closed");
      } else {
         this.output.flush();
      }
   }

   public void close() {
      if (this.output != null) {
         try {
            this.output.close();
         } finally {
            this.serInstance.releaseKryo(this.kryo);
            this.kryo = null;
            this.output = null;
         }
      }

   }

   public KryoSerializationStream(KryoSerializerInstance serInstance, OutputStream outStream, boolean useUnsafe) {
      this.serInstance = serInstance;
      this.output = (Output)(useUnsafe ? new UnsafeOutput(outStream) : new Output(outStream));
      this.kryo = serInstance.borrowKryo();
   }
}
