package org.apache.spark.api.python;

import org.apache.hadoop.io.Text;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u000112Q!\u0001\u0002\u0001\u00051\u0011a\u0003V3ti>+H\u000f];u\u0017\u0016L8i\u001c8wKJ$XM\u001d\u0006\u0003\u0007\u0011\ta\u0001]=uQ>t'BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\n\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\u0011!RcF\f\u000e\u0003\tI!A\u0006\u0002\u0003\u0013\r{gN^3si\u0016\u0014\bC\u0001\b\u0019\u0013\tIrBA\u0002B]fDQa\u0007\u0001\u0005\u0002u\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002=A\u0011A\u0003\u0001\u0005\u0006A\u0001!\t%I\u0001\bG>tg/\u001a:u)\t\u0011#\u0006\u0005\u0002$Q5\tAE\u0003\u0002&M\u0005\u0011\u0011n\u001c\u0006\u0003O!\ta\u0001[1e_>\u0004\u0018BA\u0015%\u0005\u0011!V\r\u001f;\t\u000b-z\u0002\u0019A\f\u0002\u0007=\u0014'\u000e"
)
public class TestOutputKeyConverter implements Converter<Object, Object> {
   public Text convert(Object obj) {
      return new Text(BoxesRunTime.boxToInteger(BoxesRunTime.unboxToInt(obj)).toString());
   }
}
