package org.apache.spark.api.python;

import java.nio.charset.StandardCharsets;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.spark.SparkContext;
import org.apache.spark.WritableFactory$;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.PairRDDFunctions;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDD$;
import org.apache.spark.rdd.SequenceFileRDDFunctions;
import scala.Array$;
import scala.MatchError;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple2$mcIZ$sp;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.math.Ordering;
import scala.math.Ordering$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;
import scala.runtime.Null$;
import scala.runtime.ScalaRunTime$;

public final class WriteInputFormatTestDataGenerator$ {
   public static final WriteInputFormatTestDataGenerator$ MODULE$;

   static {
      new WriteInputFormatTestDataGenerator$();
   }

   public void main(String[] args) {
      String path = args[0];
      JavaSparkContext sc = new JavaSparkContext("local[4]", "test-writables");
      this.generateData(path, sc);
   }

   public void generateData(String path, JavaSparkContext jsc) {
      SparkContext sc = jsc.sc();
      String basePath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sftestdata/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{path}));
      String textPath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sftext/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePath}));
      String intPath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sfint/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePath}));
      String doublePath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sfdouble/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePath}));
      String arrPath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sfarray/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePath}));
      String mapPath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sfmap/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePath}));
      String classPath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sfclass/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePath}));
      String bytesPath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sfbytes/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePath}));
      String boolPath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sfbool/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePath}));
      String nullPath = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/sfnull/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{basePath}));
      Seq intKeys = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2(BoxesRunTime.boxToInteger(1), "aa"), new Tuple2(BoxesRunTime.boxToInteger(2), "bb"), new Tuple2(BoxesRunTime.boxToInteger(2), "aa"), new Tuple2(BoxesRunTime.boxToInteger(3), "cc"), new Tuple2(BoxesRunTime.boxToInteger(2), "bb"), new Tuple2(BoxesRunTime.boxToInteger(1), "aa")}));
      SequenceFileRDDFunctions qual$1 = RDD$.MODULE$.rddToSequenceFileRDDFunctions(sc.parallelize(intKeys, sc.parallelize$default$2(), ClassTag$.MODULE$.apply(Tuple2.class)), ClassTag$.MODULE$.Int(), ClassTag$.MODULE$.apply(String.class), WritableFactory$.MODULE$.intWritableFactory(), WritableFactory$.MODULE$.stringWritableFactory());
      Option x$6 = qual$1.saveAsSequenceFile$default$2();
      qual$1.saveAsSequenceFile(intPath, x$6);
      SequenceFileRDDFunctions qual$2 = RDD$.MODULE$.rddToSequenceFileRDDFunctions(sc.parallelize((Seq)intKeys.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<Object, String> apply(Tuple2<Object, String> x0$1) {
            if (x0$1 != null) {
               int k = x0$1._1$mcI$sp();
               String v = (String)x0$1._2();
               Tuple2 var5 = new Tuple2(BoxesRunTime.boxToDouble((double)k), v);
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }
      }, Seq$.MODULE$.canBuildFrom()), sc.parallelize$default$2(), ClassTag$.MODULE$.apply(Tuple2.class)), ClassTag$.MODULE$.Double(), ClassTag$.MODULE$.apply(String.class), WritableFactory$.MODULE$.doubleWritableFactory(), WritableFactory$.MODULE$.stringWritableFactory());
      Option x$8 = qual$2.saveAsSequenceFile$default$2();
      qual$2.saveAsSequenceFile(doublePath, x$8);
      SequenceFileRDDFunctions qual$3 = RDD$.MODULE$.rddToSequenceFileRDDFunctions(sc.parallelize((Seq)intKeys.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, String> apply(Tuple2<Object, String> x0$2) {
            if (x0$2 != null) {
               int k = x0$2._1$mcI$sp();
               String v = (String)x0$2._2();
               Tuple2 var5 = new Tuple2(BoxesRunTime.boxToInteger(k).toString(), v);
               return var5;
            } else {
               throw new MatchError(x0$2);
            }
         }
      }, Seq$.MODULE$.canBuildFrom()), sc.parallelize$default$2(), ClassTag$.MODULE$.apply(Tuple2.class)), ClassTag$.MODULE$.apply(String.class), ClassTag$.MODULE$.apply(String.class), WritableFactory$.MODULE$.stringWritableFactory(), WritableFactory$.MODULE$.stringWritableFactory());
      Option x$10 = qual$3.saveAsSequenceFile$default$2();
      qual$3.saveAsSequenceFile(textPath, x$10);
      SequenceFileRDDFunctions qual$4 = RDD$.MODULE$.rddToSequenceFileRDDFunctions(sc.parallelize((Seq)intKeys.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<Object, byte[]> apply(Tuple2<Object, String> x0$3) {
            if (x0$3 != null) {
               int k = x0$3._1$mcI$sp();
               String v = (String)x0$3._2();
               Tuple2 var5 = new Tuple2(BoxesRunTime.boxToInteger(k), v.getBytes(StandardCharsets.UTF_8));
               return var5;
            } else {
               throw new MatchError(x0$3);
            }
         }
      }, Seq$.MODULE$.canBuildFrom()), sc.parallelize$default$2(), ClassTag$.MODULE$.apply(Tuple2.class)), ClassTag$.MODULE$.Int(), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)), WritableFactory$.MODULE$.intWritableFactory(), WritableFactory$.MODULE$.bytesWritableFactory());
      Option x$12 = qual$4.saveAsSequenceFile$default$2();
      qual$4.saveAsSequenceFile(bytesPath, x$12);
      Seq bools = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2$mcIZ$sp(1, true), new Tuple2$mcIZ$sp(2, true), new Tuple2$mcIZ$sp(2, false), new Tuple2$mcIZ$sp(3, true), new Tuple2$mcIZ$sp(2, false), new Tuple2$mcIZ$sp(1, false)}));
      SequenceFileRDDFunctions qual$5 = RDD$.MODULE$.rddToSequenceFileRDDFunctions(sc.parallelize(bools, sc.parallelize$default$2(), ClassTag$.MODULE$.apply(Tuple2.class)), ClassTag$.MODULE$.Int(), ClassTag$.MODULE$.Boolean(), WritableFactory$.MODULE$.intWritableFactory(), WritableFactory$.MODULE$.booleanWritableFactory());
      Option x$14 = qual$5.saveAsSequenceFile$default$2();
      qual$5.saveAsSequenceFile(boolPath, x$14);
      SequenceFileRDDFunctions qual$6 = RDD$.MODULE$.rddToSequenceFileRDDFunctions(sc.parallelize(intKeys, sc.parallelize$default$2(), ClassTag$.MODULE$.apply(Tuple2.class)).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<IntWritable, NullWritable> apply(Tuple2<Object, String> x0$4) {
            if (x0$4 != null) {
               int k = x0$4._1$mcI$sp();
               Tuple2 var4 = new Tuple2(new IntWritable(k), NullWritable.get());
               return var4;
            } else {
               throw new MatchError(x0$4);
            }
         }
      }, ClassTag$.MODULE$.apply(Tuple2.class)), ClassTag$.MODULE$.apply(IntWritable.class), ClassTag$.MODULE$.apply(NullWritable.class), WritableFactory$.MODULE$.writableWritableFactory(ClassTag$.MODULE$.apply(IntWritable.class)), WritableFactory$.MODULE$.writableWritableFactory(ClassTag$.MODULE$.apply(NullWritable.class)));
      Option x$16 = qual$6.saveAsSequenceFile$default$2();
      qual$6.saveAsSequenceFile(nullPath, x$16);
      Seq data = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2(BoxesRunTime.boxToInteger(1), Array$.MODULE$.empty(ClassTag$.MODULE$.Double())), new Tuple2(BoxesRunTime.boxToInteger(2), new double[]{(double)3.0F, (double)4.0F, (double)5.0F}), new Tuple2(BoxesRunTime.boxToInteger(3), new double[]{(double)4.0F, (double)5.0F, (double)6.0F})}));
      RDD$.MODULE$.rddToPairRDDFunctions(sc.parallelize(data, 2, ClassTag$.MODULE$.apply(Tuple2.class)).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<IntWritable, DoubleArrayWritable> apply(Tuple2<Object, double[]> x0$5) {
            if (x0$5 != null) {
               int k = x0$5._1$mcI$sp();
               double[] v = (double[])x0$5._2();
               DoubleArrayWritable va = new DoubleArrayWritable();
               va.set((Writable[])Predef$.MODULE$.doubleArrayOps(v).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final DoubleWritable apply(double x$4) {
                     return new DoubleWritable(x$4);
                  }
               }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Writable.class))));
               Tuple2 var5 = new Tuple2(new IntWritable(k), va);
               return var5;
            } else {
               throw new MatchError(x0$5);
            }
         }
      }, ClassTag$.MODULE$.apply(Tuple2.class)), ClassTag$.MODULE$.apply(IntWritable.class), ClassTag$.MODULE$.apply(DoubleArrayWritable.class), Ordering$.MODULE$.ordered(Predef$.MODULE$.$conforms())).saveAsNewAPIHadoopFile(arrPath, ClassTag$.MODULE$.apply(SequenceFileOutputFormat.class));
      Seq mapData = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2(BoxesRunTime.boxToInteger(1), Predef$.MODULE$.Map().apply(Nil$.MODULE$)), new Tuple2(BoxesRunTime.boxToInteger(2), Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToDouble((double)1.0F)), "cc")}))), new Tuple2(BoxesRunTime.boxToInteger(3), Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToDouble((double)2.0F)), "dd")}))), new Tuple2(BoxesRunTime.boxToInteger(2), Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToDouble((double)1.0F)), "aa")}))), new Tuple2(BoxesRunTime.boxToInteger(1), Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToDouble((double)3.0F)), "bb")})))}));
      SequenceFileRDDFunctions qual$7 = RDD$.MODULE$.rddToSequenceFileRDDFunctions(sc.parallelize(mapData, 2, ClassTag$.MODULE$.apply(Tuple2.class)).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<IntWritable, MapWritable> apply(Tuple2<Object, Map<? extends Object, String>> x0$6) {
            if (x0$6 != null) {
               int i = x0$6._1$mcI$sp();
               Map m = (Map)x0$6._2();
               MapWritable mw = new MapWritable();
               m.foreach(new Serializable(this, mw) {
                  public static final long serialVersionUID = 0L;
                  private final MapWritable mw$1;

                  public final Writable apply(Tuple2<Object, String> x0$7) {
                     if (x0$7 != null) {
                        double k = BoxesRunTime.unboxToDouble(x0$7._1());
                        String v = (String)x0$7._2();
                        Writable var6 = this.mw$1.put(new DoubleWritable(k), new Text(v));
                        return var6;
                     } else {
                        throw new MatchError(x0$7);
                     }
                  }

                  public {
                     this.mw$1 = mw$1;
                  }
               });
               Tuple2 var5 = new Tuple2(new IntWritable(i), mw);
               return var5;
            } else {
               throw new MatchError(x0$6);
            }
         }
      }, ClassTag$.MODULE$.apply(Tuple2.class)), ClassTag$.MODULE$.apply(IntWritable.class), ClassTag$.MODULE$.apply(MapWritable.class), WritableFactory$.MODULE$.writableWritableFactory(ClassTag$.MODULE$.apply(IntWritable.class)), WritableFactory$.MODULE$.writableWritableFactory(ClassTag$.MODULE$.apply(MapWritable.class)));
      Option x$18 = qual$7.saveAsSequenceFile$default$2();
      qual$7.saveAsSequenceFile(mapPath, x$18);
      Seq testClass = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("1", new TestWritable("test1", 1, (double)1.0F)), new Tuple2("2", new TestWritable("test2", 2, 2.3)), new Tuple2("3", new TestWritable("test3", 3, 3.1)), new Tuple2("5", new TestWritable("test56", 5, (double)5.5F)), new Tuple2("4", new TestWritable("test4", 4, 4.2))}));
      RDD rdd = sc.parallelize(testClass, 2, ClassTag$.MODULE$.apply(Tuple2.class)).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<Text, TestWritable> apply(Tuple2<String, TestWritable> x0$8) {
            if (x0$8 != null) {
               String k = (String)x0$8._1();
               TestWritable v = (TestWritable)x0$8._2();
               Tuple2 var5 = new Tuple2(new Text(k), v);
               return var5;
            } else {
               throw new MatchError(x0$8);
            }
         }
      }, ClassTag$.MODULE$.apply(Tuple2.class));
      ClassTag x$20 = ClassTag$.MODULE$.apply(Text.class);
      ClassTag x$21 = ClassTag$.MODULE$.apply(TestWritable.class);
      RDD$.MODULE$.rddToPairRDDFunctions$default$4(rdd);
      Null$ x$22 = null;
      PairRDDFunctions qual$8 = RDD$.MODULE$.rddToPairRDDFunctions(rdd, x$20, x$21, (Ordering)null);
      Class x$24 = Text.class;
      Class x$25 = TestWritable.class;
      Class x$26 = SequenceFileOutputFormat.class;
      Configuration x$27 = qual$8.saveAsNewAPIHadoopFile$default$5();
      qual$8.saveAsNewAPIHadoopFile(classPath, x$24, x$25, x$26, x$27);
   }

   private WriteInputFormatTestDataGenerator$() {
      MODULE$ = this;
   }
}
