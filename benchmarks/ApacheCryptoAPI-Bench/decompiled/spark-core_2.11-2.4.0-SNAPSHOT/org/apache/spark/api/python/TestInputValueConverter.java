package org.apache.spark.api.python;

import java.util.List;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;
import scala.Serializable;
import scala.collection.JavaConverters$;
import scala.collection.SetLike;
import scala.collection.mutable.Set$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001=2Q!\u0001\u0002\u0001\u00051\u0011q\u0003V3ti&s\u0007/\u001e;WC2,XmQ8om\u0016\u0014H/\u001a:\u000b\u0005\r!\u0011A\u00029zi\"|gN\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB!A#F\f\u0018\u001b\u0005\u0011\u0011B\u0001\f\u0003\u0005%\u0019uN\u001c<feR,'\u000f\u0005\u0002\u000f1%\u0011\u0011d\u0004\u0002\u0004\u0003:L\b\"B\u000e\u0001\t\u0003i\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003y\u0001\"\u0001\u0006\u0001\t\u000b\u0001\u0002A\u0011I\u0011\u0002\u000f\r|gN^3siR\u0011!%\f\t\u0004G!RS\"\u0001\u0013\u000b\u0005\u00152\u0013\u0001B;uS2T\u0011aJ\u0001\u0005U\u00064\u0018-\u0003\u0002*I\t!A*[:u!\tq1&\u0003\u0002-\u001f\t1Ai\\;cY\u0016DQAL\u0010A\u0002]\t1a\u001c2k\u0001"
)
public class TestInputValueConverter implements Converter<Object, Object> {
   public List<Object> convert(Object obj) {
      MapWritable m = (MapWritable)obj;
      return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(((SetLike)((SetLike)JavaConverters$.MODULE$.asScalaSetConverter(m.keySet()).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final double apply(Writable x$1) {
            return ((DoubleWritable)x$1).get();
         }
      }, Set$.MODULE$.canBuildFrom())).toSeq()).asJava();
   }
}
