package org.apache.spark.api.python;

import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.rdd.RDD;
import scala.MatchError;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;

public final class PythonHadoopUtil$ {
   public static final PythonHadoopUtil$ MODULE$;

   static {
      new PythonHadoopUtil$();
   }

   public Configuration mapToConf(Map<String, String> map) {
      Configuration conf = new Configuration();
      ((IterableLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(map).asScala()).foreach(new Serializable(conf) {
         public static final long serialVersionUID = 0L;
         private final Configuration conf$1;

         public final void apply(Tuple2<String, String> x0$3) {
            if (x0$3 != null) {
               String k = (String)x0$3._1();
               String v = (String)x0$3._2();
               this.conf$1.set(k, v);
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x0$3);
            }
         }

         public {
            this.conf$1 = conf$1;
         }
      });
      return conf;
   }

   public Configuration mergeConfs(Configuration left, Configuration right) {
      Configuration copy = new Configuration(left);
      ((IterableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(right).asScala()).foreach(new Serializable(copy) {
         public static final long serialVersionUID = 0L;
         private final Configuration copy$1;

         public final void apply(Map.Entry<String, String> entry) {
            this.copy$1.set((String)entry.getKey(), (String)entry.getValue());
         }

         public {
            this.copy$1 = copy$1;
         }
      });
      return copy;
   }

   public <K, V> RDD<Tuple2<Object, Object>> convertRDD(RDD<Tuple2<K, V>> rdd, Converter<Object, Object> keyConverter, Converter<Object, Object> valueConverter) {
      return rdd.<Tuple2<Object, Object>>map(new Serializable(keyConverter, valueConverter) {
         public static final long serialVersionUID = 0L;
         private final Converter keyConverter$1;
         private final Converter valueConverter$1;

         public final Tuple2<Object, Object> apply(Tuple2<K, V> x0$4) {
            if (x0$4 != null) {
               Object k = x0$4._1();
               Object v = x0$4._2();
               Tuple2 var5 = new Tuple2(this.keyConverter$1.convert(k), this.valueConverter$1.convert(v));
               return var5;
            } else {
               throw new MatchError(x0$4);
            }
         }

         public {
            this.keyConverter$1 = keyConverter$1;
            this.valueConverter$1 = valueConverter$1;
         }
      }, ClassTag$.MODULE$.apply(Tuple2.class));
   }

   private PythonHadoopUtil$() {
      MODULE$ = this;
   }
}
