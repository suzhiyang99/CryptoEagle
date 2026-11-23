package org.apache.spark.api.python;

import java.util.HashMap;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.util.SerializableConfiguration;
import scala.Array$;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u00013Q!\u0001\u0002\u0001\u00051\u0011qc\u0016:ji\u0006\u0014G.\u001a+p\u0015\u00064\u0018mQ8om\u0016\u0014H/\u001a:\u000b\u0005\r!\u0011A\u00029zi\"|gN\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB!A#F\f\u0018\u001b\u0005\u0011\u0011B\u0001\f\u0003\u0005%\u0019uN\u001c<feR,'\u000f\u0005\u0002\u000f1%\u0011\u0011d\u0004\u0002\u0004\u0003:L\b\u0002C\u000e\u0001\u0005\u0003\u0005\u000b\u0011B\u000f\u0002\t\r|gNZ\u0002\u0001!\rq\u0012eI\u0007\u0002?)\u0011\u0001EB\u0001\nEJ|\u0017\rZ2bgRL!AI\u0010\u0003\u0013\t\u0013x.\u00193dCN$\bC\u0001\u0013(\u001b\u0005)#B\u0001\u0014\u0007\u0003\u0011)H/\u001b7\n\u0005!*#!G*fe&\fG.\u001b>bE2,7i\u001c8gS\u001e,(/\u0019;j_:DQA\u000b\u0001\u0005\u0002-\na\u0001P5oSRtDC\u0001\u0017.!\t!\u0002\u0001C\u0003\u001cS\u0001\u0007Q\u0004C\u00030\u0001\u0011%\u0001'A\bd_:4XM\u001d;Xe&$\u0018M\u00197f)\t9\u0012\u0007C\u00033]\u0001\u00071'\u0001\u0005xe&$\u0018M\u00197f!\t!\u0014(D\u00016\u0015\t1t'\u0001\u0002j_*\u0011\u0001\bC\u0001\u0007Q\u0006$wn\u001c9\n\u0005i*$\u0001C,sSR\f'\r\\3\t\u000bq\u0002A\u0011I\u001f\u0002\u000f\r|gN^3siR\u0011qC\u0010\u0005\u0006\u007fm\u0002\raF\u0001\u0004_\nT\u0007"
)
public class WritableToJavaConverter implements Converter<Object, Object> {
   private final Broadcast<SerializableConfiguration> conf;

   public Object org$apache$spark$api$python$WritableToJavaConverter$$convertWritable(Writable writable) {
      Object var4;
      if (writable instanceof IntWritable) {
         IntWritable var3 = (IntWritable)writable;
         var4 = BoxesRunTime.boxToInteger(var3.get());
      } else if (writable instanceof DoubleWritable) {
         DoubleWritable var5 = (DoubleWritable)writable;
         var4 = BoxesRunTime.boxToDouble(var5.get());
      } else if (writable instanceof LongWritable) {
         LongWritable var6 = (LongWritable)writable;
         var4 = BoxesRunTime.boxToLong(var6.get());
      } else if (writable instanceof FloatWritable) {
         FloatWritable var7 = (FloatWritable)writable;
         var4 = BoxesRunTime.boxToFloat(var7.get());
      } else if (writable instanceof Text) {
         Text var8 = (Text)writable;
         var4 = var8.toString();
      } else if (writable instanceof BooleanWritable) {
         BooleanWritable var9 = (BooleanWritable)writable;
         var4 = BoxesRunTime.boxToBoolean(var9.get());
      } else if (writable instanceof BytesWritable) {
         BytesWritable var10 = (BytesWritable)writable;
         byte[] bytes = new byte[var10.getLength()];
         System.arraycopy(var10.getBytes(), 0, bytes, 0, var10.getLength());
         var4 = bytes;
      } else if (writable instanceof NullWritable) {
         var4 = null;
      } else if (writable instanceof ArrayWritable) {
         ArrayWritable var12 = (ArrayWritable)writable;
         var4 = Predef$.MODULE$.refArrayOps((Object[])var12.get()).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final WritableToJavaConverter $outer;

            public final Object apply(Writable x$1) {
               return this.$outer.org$apache$spark$api$python$WritableToJavaConverter$$convertWritable(x$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Any()));
      } else if (writable instanceof MapWritable) {
         MapWritable var13 = (MapWritable)writable;
         HashMap map = new HashMap();
         ((IterableLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(var13).asScala()).foreach(new Serializable(this, map) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final WritableToJavaConverter $outer;
            private final HashMap map$1;

            public final Object apply(Tuple2<Writable, Writable> x0$1) {
               if (x0$1 != null) {
                  Writable k = (Writable)x0$1._1();
                  Writable v = (Writable)x0$1._2();
                  Object var5 = this.map$1.put(this.$outer.org$apache$spark$api$python$WritableToJavaConverter$$convertWritable(k), this.$outer.org$apache$spark$api$python$WritableToJavaConverter$$convertWritable(v));
                  return var5;
               } else {
                  throw new MatchError(x0$1);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.map$1 = map$1;
               }
            }
         });
         var4 = map;
      } else if (writable != null) {
         var4 = WritableUtils.clone(writable, ((SerializableConfiguration)this.conf.value()).value());
      } else {
         var4 = writable;
      }

      return var4;
   }

   public Object convert(Object obj) {
      Object var4;
      if (obj instanceof Writable) {
         Writable var3 = (Writable)obj;
         var4 = this.org$apache$spark$api$python$WritableToJavaConverter$$convertWritable(var3);
      } else {
         var4 = obj;
      }

      return var4;
   }

   public WritableToJavaConverter(Broadcast<SerializableConfiguration> conf) {
      this.conf = conf;
   }
}
