package org.apache.spark.api.python;

import java.io.File;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkContext$;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaRDD$;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaSparkContext$;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Map;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ClassTag$;
import scala.sys.package$;

public final class PythonUtils$ {
   public static final PythonUtils$ MODULE$;

   static {
      new PythonUtils$();
   }

   public String sparkPythonPath() {
      ArrayBuffer pythonPath = new ArrayBuffer();
      package$.MODULE$.env().get("SPARK_HOME").foreach(new Serializable(pythonPath) {
         public static final long serialVersionUID = 0L;
         private final ArrayBuffer pythonPath$1;

         public final ArrayBuffer<String> apply(String sparkHome) {
            this.pythonPath$1.$plus$eq(((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{sparkHome, "python", "lib", "pyspark.zip"})))).mkString(File.separator));
            return this.pythonPath$1.$plus$eq(((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{sparkHome, "python", "lib", "py4j-0.10.6-src.zip"})))).mkString(File.separator));
         }

         public {
            this.pythonPath$1 = pythonPath$1;
         }
      });
      pythonPath.$plus$plus$eq(Option$.MODULE$.option2Iterable(SparkContext$.MODULE$.jarOfObject(this)));
      return pythonPath.mkString(File.pathSeparator);
   }

   public String mergePythonPaths(Seq<String> paths) {
      return ((TraversableOnce)paths.filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$1) {
            boolean var10000;
            label23: {
               String var2 = "";
               if (x$1 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (x$1.equals(var2)) {
                  break label23;
               }

               var10000 = true;
               return var10000;
            }

            var10000 = false;
            return var10000;
         }
      })).mkString(File.pathSeparator);
   }

   public JavaRDD<String> generateRDDWithNull(JavaSparkContext sc) {
      SparkContext qual$1 = JavaSparkContext$.MODULE$.toSparkContext(sc);
      List x$2 = List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"a", null, "b"})));
      int x$3 = qual$1.parallelize$default$2();
      return JavaRDD$.MODULE$.<String>fromRDD(qual$1.parallelize(x$2, x$3, ClassTag$.MODULE$.apply(String.class)), ClassTag$.MODULE$.apply(String.class));
   }

   public <T> Seq<T> toSeq(java.util.List<T> vs) {
      return (Seq)JavaConverters$.MODULE$.asScalaBufferConverter(vs).asScala();
   }

   public <T> List<T> toList(java.util.List<T> vs) {
      return ((TraversableOnce)JavaConverters$.MODULE$.asScalaBufferConverter(vs).asScala()).toList();
   }

   public <T> Object toArray(java.util.List<T> vs) {
      return vs.toArray();
   }

   public <K, V> Map<K, V> toScalaMap(java.util.Map<K, V> jm) {
      return ((TraversableOnce)JavaConverters$.MODULE$.mapAsScalaMapConverter(jm).asScala()).<K, V>toMap(Predef$.MODULE$.$conforms());
   }

   private PythonUtils$() {
      MODULE$ = this;
   }
}
