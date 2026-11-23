package org.apache.spark.api.python;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.CollectionAccumulator;
import org.apache.spark.util.Utils$;
import scala.Serializable;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%4Q!\u0001\u0002\u0001\r1\u00111\u0003U=uQ>t\u0017iY2v[Vd\u0017\r^8s-JR!a\u0001\u0003\u0002\rALH\u000f[8o\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u00012AD\t\u0014\u001b\u0005y!B\u0001\t\u0007\u0003\u0011)H/\u001b7\n\u0005Iy!!F\"pY2,7\r^5p]\u0006\u001b7-^7vY\u0006$xN\u001d\t\u0004)]IR\"A\u000b\u000b\u0003Y\tQa]2bY\u0006L!\u0001G\u000b\u0003\u000b\u0005\u0013(/Y=\u0011\u0005QQ\u0012BA\u000e\u0016\u0005\u0011\u0011\u0015\u0010^3\t\u0011u\u0001!Q1A\u0005\n}\t!b]3sm\u0016\u0014\bj\\:u\u0007\u0001)\u0012\u0001\t\t\u0003C\u0011r!\u0001\u0006\u0012\n\u0005\r*\u0012A\u0002)sK\u0012,g-\u0003\u0002&M\t11\u000b\u001e:j]\u001eT!aI\u000b\t\u0011!\u0002!\u0011!Q\u0001\n\u0001\n1b]3sm\u0016\u0014\bj\\:uA!\u0012qE\u000b\t\u0003)-J!\u0001L\u000b\u0003\u0013Q\u0014\u0018M\\:jK:$\b\u0002\u0003\u0018\u0001\u0005\u000b\u0007I\u0011B\u0018\u0002\u0015M,'O^3s!>\u0014H/F\u00011!\t!\u0012'\u0003\u00023+\t\u0019\u0011J\u001c;\t\u0011Q\u0002!\u0011!Q\u0001\nA\n1b]3sm\u0016\u0014\bk\u001c:uA!)a\u0007\u0001C\u0001o\u00051A(\u001b8jiz\"2\u0001\u000f\u001e<!\tI\u0004!D\u0001\u0003\u0011\u0015iR\u00071\u0001!\u0011\u0015qS\u00071\u00011\u0011\u001di\u0004A1A\u0005\u0002=\n!BY;gM\u0016\u00148+\u001b>f\u0011\u0019y\u0004\u0001)A\u0005a\u0005Y!-\u001e4gKJ\u001c\u0016N_3!\u0011%\t\u0005\u00011AA\u0002\u0013%!)\u0001\u0004t_\u000e\\W\r^\u000b\u0002\u0007B\u0011A)S\u0007\u0002\u000b*\u0011aiR\u0001\u0004]\u0016$(\"\u0001%\u0002\t)\fg/Y\u0005\u0003\u0015\u0016\u0013aaU8dW\u0016$\b\"\u0003'\u0001\u0001\u0004\u0005\r\u0011\"\u0003N\u0003)\u0019xnY6fi~#S-\u001d\u000b\u0003\u001dF\u0003\"\u0001F(\n\u0005A+\"\u0001B+oSRDqAU&\u0002\u0002\u0003\u00071)A\u0002yIEBa\u0001\u0016\u0001!B\u0013\u0019\u0015aB:pG.,G\u000f\t\u0015\u0003'*BQa\u0016\u0001\u0005\na\u000b!b\u001c9f]N{7m[3u)\u0005\u0019\u0005\"\u0002.\u0001\t\u0003Z\u0016\u0001D2paf\fe\u000e\u001a*fg\u0016$H#\u0001\u001d\t\u000bu\u0003A\u0011\t0\u0002\u000b5,'oZ3\u0015\u00059{\u0006\"\u00021]\u0001\u0004\t\u0017!B8uQ\u0016\u0014\b\u0003\u0002\bc'\u0011L!aY\b\u0003\u001b\u0005\u001b7-^7vY\u0006$xN\u001d,3!\r)wmE\u0007\u0002M*\u0011\u0001cR\u0005\u0003Q\u001a\u0014A\u0001T5ti\u0002"
)
public class PythonAccumulatorV2 extends CollectionAccumulator<byte[]> {
   private final transient String serverHost;
   private final int serverPort;
   private final int bufferSize;
   private transient Socket socket;

   private String serverHost() {
      return this.serverHost;
   }

   private int serverPort() {
      return this.serverPort;
   }

   public int bufferSize() {
      return this.bufferSize;
   }

   private Socket socket() {
      return this.socket;
   }

   private void socket_$eq(Socket x$1) {
      this.socket = x$1;
   }

   private synchronized Socket openSocket() {
      if (this.socket() == null || this.socket().isClosed()) {
         this.socket_$eq(new Socket(this.serverHost(), this.serverPort()));
      }

      return this.socket();
   }

   public PythonAccumulatorV2 copyAndReset() {
      return new PythonAccumulatorV2(this.serverHost(), this.serverPort());
   }

   public synchronized void merge(AccumulatorV2<byte[], List<byte[]>> other) {
      PythonAccumulatorV2 otherPythonAccumulator = (PythonAccumulatorV2)other;
      if (this.serverHost() == null) {
         super.merge(otherPythonAccumulator);
      } else {
         Socket socket = this.openSocket();
         InputStream in = socket.getInputStream();
         DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), this.bufferSize()));
         List values = (List)other.value();
         out.writeInt(values.size());
         ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter(values).asScala()).foreach(new Serializable(this, out) {
            public static final long serialVersionUID = 0L;
            private final DataOutputStream out$2;

            public final void apply(byte[] array) {
               this.out$2.writeInt(array.length);
               this.out$2.write(array);
            }

            public {
               this.out$2 = out$2;
            }
         });
         out.flush();
         int byteRead = in.read();
         if (byteRead == -1) {
            throw new SparkException("EOF reached before Python server acknowledged");
         }
      }

   }

   public PythonAccumulatorV2(String serverHost, int serverPort) {
      this.serverHost = serverHost;
      this.serverPort = serverPort;
      Utils$.MODULE$.checkHost(serverHost);
      this.bufferSize = SparkEnv$.MODULE$.get().conf().getInt("spark.buffer.size", 65536);
   }
}
