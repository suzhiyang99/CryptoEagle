package org.apache.spark.api.python;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Writable;
import org.apache.spark.SparkException;
import scala.Array$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001=2Q!\u0001\u0002\u0001\u00051\u0011a\u0004R8vE2,\u0017I\u001d:bsR{wK]5uC\ndWmQ8om\u0016\u0014H/\u001a:\u000b\u0005\r!\u0011A\u00029zi\"|gN\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB!A#F\f\u001b\u001b\u0005\u0011\u0011B\u0001\f\u0003\u0005%\u0019uN\u001c<feR,'\u000f\u0005\u0002\u000f1%\u0011\u0011d\u0004\u0002\u0004\u0003:L\bCA\u000e!\u001b\u0005a\"BA\u000f\u001f\u0003\tIwN\u0003\u0002 \u0011\u00051\u0001.\u00193p_BL!!\t\u000f\u0003\u0011]\u0013\u0018\u000e^1cY\u0016DQa\t\u0001\u0005\u0002\u0015\na\u0001P5oSRt4\u0001\u0001\u000b\u0002MA\u0011A\u0003\u0001\u0005\u0006Q\u0001!\t%K\u0001\bG>tg/\u001a:u)\tQS\u0006\u0005\u0002\u0015W%\u0011AF\u0001\u0002\u0014\t>,(\r\\3BeJ\f\u0017p\u0016:ji\u0006\u0014G.\u001a\u0005\u0006]\u001d\u0002\raF\u0001\u0004_\nT\u0007"
)
public class DoubleArrayToWritableConverter implements Converter<Object, Writable> {
   public DoubleArrayWritable convert(Object obj) {
      label16: {
         if (obj.getClass().isArray()) {
            Class var10000 = obj.getClass().getComponentType();
            Class var3 = Double.TYPE;
            if (var10000 == null) {
               if (var3 == null) {
                  break label16;
               }
            } else if (var10000.equals(var3)) {
               break label16;
            }
         }

         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Data of type ", " is not supported"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{obj})));
      }

      DoubleArrayWritable daw = new DoubleArrayWritable();
      daw.set((Writable[])Predef$.MODULE$.doubleArrayOps((double[])obj).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final DoubleWritable apply(double x$2) {
            return new DoubleWritable(x$2);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Writable.class))));
      return daw;
   }
}
