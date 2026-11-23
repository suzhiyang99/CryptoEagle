package org.apache.spark.api.java;

import org.apache.spark.SparkContext;
import org.apache.spark.SparkContext$;
import scala.Option$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

public final class JavaSparkContext$ {
   public static final JavaSparkContext$ MODULE$;

   static {
      new JavaSparkContext$();
   }

   public JavaSparkContext fromSparkContext(SparkContext sc) {
      return new JavaSparkContext(sc);
   }

   public SparkContext toSparkContext(JavaSparkContext jsc) {
      return jsc.sc();
   }

   public String[] jarOfClass(Class<?> cls) {
      return (String[])Option$.MODULE$.option2Iterable(SparkContext$.MODULE$.jarOfClass(cls)).toArray(ClassTag$.MODULE$.apply(String.class));
   }

   public String[] jarOfObject(Object obj) {
      return (String[])Option$.MODULE$.option2Iterable(SparkContext$.MODULE$.jarOfObject(obj)).toArray(ClassTag$.MODULE$.apply(String.class));
   }

   public <T> ClassTag<T> fakeClassTag() {
      return ClassTag$.MODULE$.AnyRef();
   }

   private JavaSparkContext$() {
      MODULE$ = this;
   }
}
