package org.apache.spark.io;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.spark.SparkConf;
import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u001d3A!\u0001\u0002\u0001\u0017\t!\"l\u0015;e\u0007>l\u0007O]3tg&|gnQ8eK\u000eT!a\u0001\u0003\u0002\u0005%|'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0019\u0001\u0001\u0004\n\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\t\u0019B#D\u0001\u0003\u0013\t)\"A\u0001\tD_6\u0004(/Z:tS>t7i\u001c3fG\"Aq\u0003\u0001B\u0001B\u0003%\u0001$\u0001\u0003d_:4\u0007CA\r\u001b\u001b\u0005!\u0011BA\u000e\u0005\u0005%\u0019\u0006/\u0019:l\u0007>tg\rC\u0003\u001e\u0001\u0011\u0005a$\u0001\u0004=S:LGO\u0010\u000b\u0003?\u0001\u0002\"a\u0005\u0001\t\u000b]a\u0002\u0019\u0001\r\t\u000f\t\u0002!\u0019!C\u0005G\u0005Q!-\u001e4gKJ\u001c\u0016N_3\u0016\u0003\u0011\u0002\"!D\u0013\n\u0005\u0019r!aA%oi\"1\u0001\u0006\u0001Q\u0001\n\u0011\n1BY;gM\u0016\u00148+\u001b>fA!9!\u0006\u0001b\u0001\n\u0013\u0019\u0013!\u00027fm\u0016d\u0007B\u0002\u0017\u0001A\u0003%A%\u0001\u0004mKZ,G\u000e\t\u0005\u0006]\u0001!\teL\u0001\u0017G>l\u0007O]3tg\u0016$w*\u001e;qkR\u001cFO]3b[R\u0011\u0001g\u000e\t\u0003cUj\u0011A\r\u0006\u0003\u0007MR\u0011\u0001N\u0001\u0005U\u00064\u0018-\u0003\u00027e\taq*\u001e;qkR\u001cFO]3b[\")\u0001(\fa\u0001a\u0005\t1\u000fC\u0003;\u0001\u0011\u00053(A\u000bd_6\u0004(/Z:tK\u0012Le\u000e];u'R\u0014X-Y7\u0015\u0005qz\u0004CA\u0019>\u0013\tq$GA\u0006J]B,Ho\u0015;sK\u0006l\u0007\"\u0002\u001d:\u0001\u0004a\u0004F\u0001\u0001B!\t\u0011U)D\u0001D\u0015\t!E!\u0001\u0006b]:|G/\u0019;j_:L!AR\"\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5"
)
public class ZStdCompressionCodec implements CompressionCodec {
   private final int bufferSize;
   private final int level;

   private int bufferSize() {
      return this.bufferSize;
   }

   private int level() {
      return this.level;
   }

   public OutputStream compressedOutputStream(OutputStream s) {
      return new BufferedOutputStream(new ZstdOutputStream(s, this.level()), this.bufferSize());
   }

   public InputStream compressedInputStream(InputStream s) {
      return new BufferedInputStream(new ZstdInputStream(s), this.bufferSize());
   }

   public ZStdCompressionCodec(SparkConf conf) {
      this.bufferSize = (int)conf.getSizeAsBytes("spark.io.compression.zstd.bufferSize", "32k");
      this.level = conf.getInt("spark.io.compression.zstd.level", 1);
   }
}
