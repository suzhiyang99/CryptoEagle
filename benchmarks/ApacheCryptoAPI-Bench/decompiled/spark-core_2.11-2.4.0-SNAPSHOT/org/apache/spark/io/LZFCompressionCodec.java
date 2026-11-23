package org.apache.spark.io;

import com.ning.compress.lzf.LZFInputStream;
import com.ning.compress.lzf.LZFOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.spark.SparkConf;
import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001m2A!\u0001\u0002\u0001\u0017\t\u0019BJ\u0017$D_6\u0004(/Z:tS>t7i\u001c3fG*\u00111\u0001B\u0001\u0003S>T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\r\u0001AB\u0005\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005M!R\"\u0001\u0002\n\u0005U\u0011!\u0001E\"p[B\u0014Xm]:j_:\u001cu\u000eZ3d\u0011!9\u0002A!A!\u0002\u0013A\u0012\u0001B2p]\u001a\u0004\"!\u0007\u000e\u000e\u0003\u0011I!a\u0007\u0003\u0003\u0013M\u0003\u0018M]6D_:4\u0007\"B\u000f\u0001\t\u0003q\u0012A\u0002\u001fj]&$h\b\u0006\u0002 AA\u00111\u0003\u0001\u0005\u0006/q\u0001\r\u0001\u0007\u0005\u0006E\u0001!\teI\u0001\u0017G>l\u0007O]3tg\u0016$w*\u001e;qkR\u001cFO]3b[R\u0011Ae\u000b\t\u0003K%j\u0011A\n\u0006\u0003\u0007\u001dR\u0011\u0001K\u0001\u0005U\u00064\u0018-\u0003\u0002+M\taq*\u001e;qkR\u001cFO]3b[\")A&\ta\u0001I\u0005\t1\u000fC\u0003/\u0001\u0011\u0005s&A\u000bd_6\u0004(/Z:tK\u0012Le\u000e];u'R\u0014X-Y7\u0015\u0005A\u001a\u0004CA\u00132\u0013\t\u0011dEA\u0006J]B,Ho\u0015;sK\u0006l\u0007\"\u0002\u0017.\u0001\u0004\u0001\u0004F\u0001\u00016!\t1\u0014(D\u00018\u0015\tAD!\u0001\u0006b]:|G/\u0019;j_:L!AO\u001c\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5"
)
public class LZFCompressionCodec implements CompressionCodec {
   public OutputStream compressedOutputStream(OutputStream s) {
      return (new LZFOutputStream(s)).setFinishBlockOnFlush(true);
   }

   public InputStream compressedInputStream(InputStream s) {
      return new LZFInputStream(s);
   }

   public LZFCompressionCodec(SparkConf conf) {
   }
}
