package org.apache.spark.api.r;

import java.io.File;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext$;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaRDD$;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.python.PythonRDD$;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.ScalaRunTime$;

public final class RRDD$ implements Serializable {
   public static final RRDD$ MODULE$;

   static {
      new RRDD$();
   }

   public JavaSparkContext createSparkContext(String master, String appName, String sparkHome, String[] jars, Map<Object, Object> sparkEnvirMap, Map<Object, Object> sparkExecutorEnvMap) {
      SparkConf sparkConf;
      label26: {
         label25: {
            sparkConf = (new SparkConf()).setAppName(appName).setSparkHome(sparkHome);
            String var8 = "";
            if (master == null) {
               if (var8 == null) {
                  break label25;
               }
            } else if (master.equals(var8)) {
               break label25;
            }

            sparkConf.setMaster(master);
            break label26;
         }

         sparkConf.setIfMissing("spark.master", "local");
      }

      ((TraversableLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(sparkEnvirMap).asScala()).withFilter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<Object, Object> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(sparkConf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf sparkConf$1;

         public final SparkConf apply(Tuple2<Object, Object> x$1) {
            if (x$1 != null) {
               Object name = x$1._1();
               Object value = x$1._2();
               SparkConf var5 = this.sparkConf$1.set(name.toString(), value.toString());
               return var5;
            } else {
               throw new MatchError(x$1);
            }
         }

         public {
            this.sparkConf$1 = sparkConf$1;
         }
      });
      ((TraversableLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(sparkExecutorEnvMap).asScala()).withFilter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<Object, Object> check$ifrefutable$2) {
            boolean var3;
            if (check$ifrefutable$2 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(sparkConf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf sparkConf$1;

         public final SparkConf apply(Tuple2<Object, Object> x$2) {
            if (x$2 != null) {
               Object name = x$2._1();
               Object value = x$2._2();
               SparkConf var5 = this.sparkConf$1.setExecutorEnv(name.toString(), value.toString());
               return var5;
            } else {
               throw new MatchError(x$2);
            }
         }

         public {
            this.sparkConf$1 = sparkConf$1;
         }
      });
      if (sparkEnvirMap.containsKey("spark.r.sql.derby.temp.dir") && System.getProperty("derby.stream.error.file") == null) {
         System.setProperty("derby.stream.error.file", ((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{sparkEnvirMap.get("spark.r.sql.derby.temp.dir").toString(), "derby.log"})))).mkString(File.separator));
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      JavaSparkContext jsc = new JavaSparkContext(SparkContext$.MODULE$.getOrCreate(sparkConf));
      Predef$.MODULE$.refArrayOps((Object[])jars).foreach(new Serializable(jsc) {
         public static final long serialVersionUID = 0L;
         private final JavaSparkContext jsc$1;

         public final void apply(String jar) {
            this.jsc$1.addJar(jar);
         }

         public {
            this.jsc$1 = jsc$1;
         }
      });
      return jsc;
   }

   public JavaRDD<byte[]> createRDDFromArray(JavaSparkContext jsc, byte[][] arr) {
      return JavaRDD$.MODULE$.<byte[]>fromRDD(jsc.sc().parallelize(Predef$.MODULE$.wrapRefArray((Object[])arr), arr.length, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE))), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   public JavaRDD<byte[]> createRDDFromFile(JavaSparkContext jsc, String fileName, int parallelism) {
      return PythonRDD$.MODULE$.readRDDFromFile(jsc, fileName, parallelism);
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RRDD$() {
      MODULE$ = this;
   }
}
