package org.apache.spark.api.python;

import java.util.Map;
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
import org.apache.spark.SparkException;
import scala.Array$;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A2Q!\u0001\u0002\u0001\u00051\u0011qCS1wCR{wK]5uC\ndWmQ8om\u0016\u0014H/\u001a:\u000b\u0005\r!\u0011A\u00029zi\"|gN\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB!A#F\f\u001b\u001b\u0005\u0011\u0011B\u0001\f\u0003\u0005%\u0019uN\u001c<feR,'\u000f\u0005\u0002\u000f1%\u0011\u0011d\u0004\u0002\u0004\u0003:L\bCA\u000e!\u001b\u0005a\"BA\u000f\u001f\u0003\tIwN\u0003\u0002 \u0011\u00051\u0001.\u00193p_BL!!\t\u000f\u0003\u0011]\u0013\u0018\u000e^1cY\u0016DQa\t\u0001\u0005\u0002\u0015\na\u0001P5oSRt4\u0001\u0001\u000b\u0002MA\u0011A\u0003\u0001\u0005\u0006Q\u0001!I!K\u0001\u0012G>tg/\u001a:u)><&/\u001b;bE2,GC\u0001\u000e+\u0011\u0015Ys\u00051\u0001\u0018\u0003\ry'M\u001b\u0005\u0006[\u0001!\tEL\u0001\bG>tg/\u001a:u)\tQr\u0006C\u0003,Y\u0001\u0007q\u0003"
)
public class JavaToWritableConverter implements Converter<Object, Writable> {
   public Writable org$apache$spark$api$python$JavaToWritableConverter$$convertToWritable(Object obj) {
      Object var4;
      if (obj instanceof Integer) {
         Integer var3 = (Integer)obj;
         var4 = new IntWritable(Predef$.MODULE$.Integer2int(var3));
      } else if (obj instanceof Double) {
         Double var5 = (Double)obj;
         var4 = new DoubleWritable(Predef$.MODULE$.Double2double(var5));
      } else if (obj instanceof Long) {
         Long var6 = (Long)obj;
         var4 = new LongWritable(Predef$.MODULE$.Long2long(var6));
      } else if (obj instanceof Float) {
         Float var7 = (Float)obj;
         var4 = new FloatWritable(Predef$.MODULE$.Float2float(var7));
      } else if (obj instanceof String) {
         String var8 = (String)obj;
         var4 = new Text(var8);
      } else if (obj instanceof Boolean) {
         Boolean var9 = (Boolean)obj;
         var4 = new BooleanWritable(Predef$.MODULE$.Boolean2boolean(var9));
      } else if (obj instanceof byte[]) {
         byte[] var10 = (byte[])obj;
         var4 = new BytesWritable(var10);
      } else if (obj == null) {
         var4 = NullWritable.get();
      } else if (obj instanceof Map) {
         Map var11 = (Map)obj;
         MapWritable mapWritable = new MapWritable();
         ((IterableLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(var11).asScala()).foreach(new Serializable(this, mapWritable) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final JavaToWritableConverter $outer;
            private final MapWritable mapWritable$1;

            public final Writable apply(Tuple2<Object, Object> x0$2) {
               if (x0$2 != null) {
                  Object k = x0$2._1();
                  Object v = x0$2._2();
                  Writable var5 = this.mapWritable$1.put(this.$outer.org$apache$spark$api$python$JavaToWritableConverter$$convertToWritable(k), this.$outer.org$apache$spark$api$python$JavaToWritableConverter$$convertToWritable(v));
                  return var5;
               } else {
                  throw new MatchError(x0$2);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.mapWritable$1 = mapWritable$1;
               }
            }
         });
         var4 = mapWritable;
      } else {
         if (!(obj instanceof Object[])) {
            throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Data of type ", " cannot be used"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{obj.getClass().getName()})));
         }

         Object[] var13 = obj;
         ArrayWritable arrayWriteable = new ArrayWritable(Writable.class);
         arrayWriteable.set((Writable[])Predef$.MODULE$.genericArrayOps(var13).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final JavaToWritableConverter $outer;

            public final Writable apply(Object x$2) {
               return this.$outer.org$apache$spark$api$python$JavaToWritableConverter$$convertToWritable(x$2);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Writable.class))));
         var4 = arrayWriteable;
      }

      return (Writable)var4;
   }

   public Writable convert(Object obj) {
      Writable var4;
      if (obj instanceof Writable) {
         Writable var3 = (Writable)obj;
         var4 = var3;
      } else {
         var4 = this.org$apache$spark$api$python$JavaToWritableConverter$$convertToWritable(obj);
      }

      return var4;
   }
}
