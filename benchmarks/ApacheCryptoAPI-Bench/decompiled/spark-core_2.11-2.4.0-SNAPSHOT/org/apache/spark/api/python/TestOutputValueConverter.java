package org.apache.spark.api.python;

import java.util.Map;
import org.apache.hadoop.io.DoubleWritable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u000112Q!\u0001\u0002\u0001\u00051\u0011\u0001\u0004V3ti>+H\u000f];u-\u0006dW/Z\"p]Z,'\u000f^3s\u0015\t\u0019A!\u0001\u0004qsRDwN\u001c\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e'A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004B\u0001F\u000b\u0018/5\t!!\u0003\u0002\u0017\u0005\tI1i\u001c8wKJ$XM\u001d\t\u0003\u001daI!!G\b\u0003\u0007\u0005s\u0017\u0010C\u0003\u001c\u0001\u0011\u0005Q$\u0001\u0004=S:LGOP\u0002\u0001)\u0005q\u0002C\u0001\u000b\u0001\u0011\u0015\u0001\u0003\u0001\"\u0011\"\u0003\u001d\u0019wN\u001c<feR$\"A\t\u0016\u0011\u0005\rBS\"\u0001\u0013\u000b\u0005\u00152\u0013AA5p\u0015\t9\u0003\"\u0001\u0004iC\u0012|w\u000e]\u0005\u0003S\u0011\u0012a\u0002R8vE2,wK]5uC\ndW\rC\u0003,?\u0001\u0007q#A\u0002pE*\u0004"
)
public class TestOutputValueConverter implements Converter<Object, Object> {
   public DoubleWritable convert(Object obj) {
      return new DoubleWritable(BoxesRunTime.unboxToDouble(((Map)obj).keySet().iterator().next()));
   }
}
