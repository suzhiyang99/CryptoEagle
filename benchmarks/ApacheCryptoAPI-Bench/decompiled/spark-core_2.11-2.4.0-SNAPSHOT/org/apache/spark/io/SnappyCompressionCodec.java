package org.apache.spark.io;

import java.io.InputStream;
import java.io.OutputStream;
import org.apache.spark.SparkConf;
import org.apache.spark.annotation.DeveloperApi;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001A3A!\u0001\u0002\u0001\u0017\t12K\\1qaf\u001cu.\u001c9sKN\u001c\u0018n\u001c8D_\u0012,7M\u0003\u0002\u0004\t\u0005\u0011\u0011n\u001c\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011c\u0001\u0001\r%A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"a\u0005\u000b\u000e\u0003\tI!!\u0006\u0002\u0003!\r{W\u000e\u001d:fgNLwN\\\"pI\u0016\u001c\u0007\u0002C\f\u0001\u0005\u0003\u0005\u000b\u0011\u0002\r\u0002\t\r|gN\u001a\t\u00033ii\u0011\u0001B\u0005\u00037\u0011\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u000bu\u0001A\u0011\u0001\u0010\u0002\rqJg.\u001b;?)\ty\u0002\u0005\u0005\u0002\u0014\u0001!)q\u0003\ba\u00011!9!\u0005\u0001b\u0001\n\u0003\u0019\u0013a\u0002<feNLwN\\\u000b\u0002IA\u0011Q\u0005\u000b\b\u0003\u001b\u0019J!a\n\b\u0002\rA\u0013X\rZ3g\u0013\tI#F\u0001\u0004TiJLgn\u001a\u0006\u0003O9Aa\u0001\f\u0001!\u0002\u0013!\u0013\u0001\u0003<feNLwN\u001c\u0011\t\u000b9\u0002A\u0011I\u0018\u0002-\r|W\u000e\u001d:fgN,GmT;uaV$8\u000b\u001e:fC6$\"\u0001M\u001c\u0011\u0005E*T\"\u0001\u001a\u000b\u0005\r\u0019$\"\u0001\u001b\u0002\t)\fg/Y\u0005\u0003mI\u0012AbT;uaV$8\u000b\u001e:fC6DQ\u0001O\u0017A\u0002A\n\u0011a\u001d\u0005\u0006u\u0001!\teO\u0001\u0016G>l\u0007O]3tg\u0016$\u0017J\u001c9viN#(/Z1n)\tat\b\u0005\u00022{%\u0011aH\r\u0002\f\u0013:\u0004X\u000f^*ue\u0016\fW\u000eC\u00039s\u0001\u0007A\b\u000b\u0002\u0001\u0003B\u0011!)R\u0007\u0002\u0007*\u0011A\tB\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001$D\u00051!UM^3m_B,'/\u00119j\u000f\u0015A%\u0001#\u0004J\u0003Y\u0019f.\u00199qs\u000e{W\u000e\u001d:fgNLwN\\\"pI\u0016\u001c\u0007CA\nK\r\u0015\t!\u0001#\u0004L'\tQE\u0002C\u0003\u001e\u0015\u0012\u0005Q\nF\u0001J\u0011!\u0011#\n#b\u0001\n\u0013\u0019\u0003\u0002\u0003\u0017K\u0011\u0003\u0005\u000b\u0015\u0002\u0013"
)
public class SnappyCompressionCodec implements CompressionCodec {
   private final SparkConf conf;
   private final String version;

   public String version() {
      return this.version;
   }

   public OutputStream compressedOutputStream(OutputStream s) {
      int blockSize = (int)this.conf.getSizeAsBytes("spark.io.compression.snappy.blockSize", "32k");
      return new SnappyOutputStreamWrapper(new SnappyOutputStream(s, blockSize));
   }

   public InputStream compressedInputStream(InputStream s) {
      return new SnappyInputStream(s);
   }

   public SnappyCompressionCodec(SparkConf conf) {
      this.conf = conf;
      this.version = SnappyCompressionCodec$.MODULE$.org$apache$spark$io$SnappyCompressionCodec$$version();
   }
}
