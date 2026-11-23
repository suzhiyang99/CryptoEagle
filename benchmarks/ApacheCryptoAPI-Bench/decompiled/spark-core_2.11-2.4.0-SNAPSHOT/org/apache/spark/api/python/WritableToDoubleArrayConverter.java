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
   bytes = "\u0006\u0001)2Q!\u0001\u0002\u0001\u00051\u0011ad\u0016:ji\u0006\u0014G.\u001a+p\t>,(\r\\3BeJ\f\u0017pQ8om\u0016\u0014H/\u001a:\u000b\u0005\r!\u0011A\u00029zi\"|gN\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB!A#F\f\u001b\u001b\u0005\u0011\u0011B\u0001\f\u0003\u0005%\u0019uN\u001c<feR,'\u000f\u0005\u0002\u000f1%\u0011\u0011d\u0004\u0002\u0004\u0003:L\bc\u0001\b\u001c;%\u0011Ad\u0004\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003\u001dyI!aH\b\u0003\r\u0011{WO\u00197f\u0011\u0015\t\u0003\u0001\"\u0001$\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0013\u0011\u0005Q\u0001\u0001\"\u0002\u0014\u0001\t\u0003:\u0013aB2p]Z,'\u000f\u001e\u000b\u00035!BQ!K\u0013A\u0002]\t1a\u001c2k\u0001"
)
public class WritableToDoubleArrayConverter implements Converter<Object, double[]> {
   public double[] convert(Object obj) {
      if (obj instanceof DoubleArrayWritable) {
         DoubleArrayWritable var3 = (DoubleArrayWritable)obj;
         double[] var4 = (double[])Predef$.MODULE$.refArrayOps((Object[])var3.get()).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final double apply(Writable x$3) {
               return ((DoubleWritable)x$3).get();
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Double()));
         return var4;
      } else {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Data of type ", " is not supported"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{obj})));
      }
   }
}
