package org.apache.spark.api.python;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkException;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaRDD$;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.input.PortableDataStream;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDD$;
import org.apache.spark.util.SerializableConfiguration;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.Set;
import scala.collection.mutable.StringBuilder;
import scala.collection.mutable.WeakHashMap;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Null$;
import scala.runtime.ScalaRunTime$;
import scala.util.control.NonFatal$;

public final class PythonRDD$ implements Logging, Serializable {
   public static final PythonRDD$ MODULE$;
   private final WeakHashMap<Socket, Set<Object>> workerBroadcasts;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new PythonRDD$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   private WeakHashMap<Socket, Set<Object>> workerBroadcasts() {
      return this.workerBroadcasts;
   }

   public synchronized Set<Object> getWorkerBroadcasts(Socket worker) {
      return (Set)this.workerBroadcasts().getOrElseUpdate(worker, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final HashSet<Object> apply() {
            return new HashSet<Object>();
         }
      });
   }

   public JavaRDD<byte[]> valueOfPair(JavaPairRDD<Object, byte[]> pair) {
      return JavaRDD$.MODULE$.<byte[]>fromRDD(pair.rdd().mapPartitions(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Iterator<byte[]> apply(Iterator<Tuple2<Object, byte[]>> it) {
            return it.<byte[]>map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final byte[] apply(Tuple2<Object, byte[]> x$1) {
                  return (byte[])x$1._2();
               }
            });
         }
      }, true, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE))), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   public int runJob(SparkContext sc, JavaRDD<byte[]> rdd, ArrayList<Object> partitions) {
      byte[][][] allPartitions = (byte[][][])sc.runJob(JavaRDD$.MODULE$.toRDD(rdd), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final byte[][] apply(Iterator<byte[]> x) {
            return (byte[][])x.toArray(ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
         }
      }, (Seq)JavaConverters$.MODULE$.asScalaBufferConverter(partitions).asScala(), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE))));
      byte[][] flattenedPartition = (byte[][])Array$.MODULE$.concat(Predef$.MODULE$.wrapRefArray((Object[])allPartitions), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
      return this.serveIterator(Predef$.MODULE$.refArrayOps((Object[])flattenedPartition).iterator(), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"serve RDD ", " with partitions ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(rdd.id()), ((TraversableOnce)JavaConverters$.MODULE$.asScalaBufferConverter(partitions).asScala()).mkString(",")})));
   }

   public <T> int collectAndServe(RDD<T> rdd) {
      return this.serveIterator(Predef$.MODULE$.genericArrayOps(rdd.collect()).iterator(), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"serve RDD ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(rdd.id())})));
   }

   public <T> int toLocalIteratorAndServe(RDD<T> rdd) {
      return this.serveIterator(rdd.toLocalIterator(), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"serve toLocalIterator"})))).s(Nil$.MODULE$));
   }

   public JavaRDD<byte[]> readRDDFromFile(JavaSparkContext sc, String filename, int parallelism) {
      DataInputStream file = new DataInputStream(new FileInputStream(filename));

      JavaRDD var10000;
      try {
         ArrayBuffer objs = new ArrayBuffer();

         try {
            while(true) {
               int length = file.readInt();
               byte[] obj = new byte[length];
               file.readFully(obj);
               objs.$plus$eq(obj);
            }
         } catch (EOFException var12) {
            var10000 = JavaRDD$.MODULE$.fromRDD(sc.sc().parallelize(objs, parallelism, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE))), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
         }
      } finally {
         file.close();
      }

      return var10000;
   }

   public Broadcast<PythonBroadcast> readBroadcastFromFile(JavaSparkContext sc, String path) {
      return sc.<PythonBroadcast>broadcast(new PythonBroadcast(path));
   }

   public <T> void writeIteratorToStream(Iterator<T> iter, DataOutputStream dataOut) {
      iter.foreach(new Serializable(dataOut) {
         public static final long serialVersionUID = 0L;
         private final DataOutputStream dataOut$1;

         public final void apply(Object obj) {
            PythonRDD$.MODULE$.org$apache$spark$api$python$PythonRDD$$write$1(obj, this.dataOut$1);
         }

         public {
            this.dataOut$1 = dataOut$1;
         }
      });
   }

   public <K, V> JavaRDD<byte[]> sequenceFile(JavaSparkContext sc, String path, String keyClassMaybeNull, String valueClassMaybeNull, String keyConverterClass, String valueConverterClass, int minSplits, int batchSize) {
      String keyClass = (String)Option$.MODULE$.apply(keyClassMaybeNull).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "org.apache.hadoop.io.Text";
         }
      });
      String valueClass = (String)Option$.MODULE$.apply(valueClassMaybeNull).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "org.apache.hadoop.io.Text";
         }
      });
      Class kc = Utils$.MODULE$.classForName(keyClass);
      Class vc = Utils$.MODULE$.classForName(valueClass);
      RDD rdd = sc.sc().sequenceFile(path, kc, vc, minSplits);
      Broadcast confBroadcasted = sc.sc().broadcast(new SerializableConfiguration(sc.hadoopConfiguration()), ClassTag$.MODULE$.apply(SerializableConfiguration.class));
      RDD converted = this.convertRDD(rdd, keyConverterClass, valueConverterClass, new WritableToJavaConverter(confBroadcasted));
      return JavaRDD$.MODULE$.<byte[]>fromRDD(SerDeUtil$.MODULE$.pairRDDToPython(converted, batchSize), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   public <K, V, F extends InputFormat<K, V>> JavaRDD<byte[]> newAPIHadoopFile(JavaSparkContext sc, String path, String inputFormatClass, String keyClass, String valueClass, String keyConverterClass, String valueConverterClass, HashMap<String, String> confAsMap, int batchSize) {
      Configuration mergedConf = this.getMergedConf(confAsMap, sc.hadoopConfiguration());
      RDD rdd = this.newAPIHadoopRDDFromClassNames(sc, new Some(path), inputFormatClass, keyClass, valueClass, mergedConf);
      Broadcast confBroadcasted = sc.sc().broadcast(new SerializableConfiguration(mergedConf), ClassTag$.MODULE$.apply(SerializableConfiguration.class));
      RDD converted = this.convertRDD(rdd, keyConverterClass, valueConverterClass, new WritableToJavaConverter(confBroadcasted));
      return JavaRDD$.MODULE$.<byte[]>fromRDD(SerDeUtil$.MODULE$.pairRDDToPython(converted, batchSize), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   public <K, V, F extends InputFormat<K, V>> JavaRDD<byte[]> newAPIHadoopRDD(JavaSparkContext sc, String inputFormatClass, String keyClass, String valueClass, String keyConverterClass, String valueConverterClass, HashMap<String, String> confAsMap, int batchSize) {
      Configuration conf = PythonHadoopUtil$.MODULE$.mapToConf(confAsMap);
      RDD rdd = this.newAPIHadoopRDDFromClassNames(sc, None$.MODULE$, inputFormatClass, keyClass, valueClass, conf);
      Broadcast confBroadcasted = sc.sc().broadcast(new SerializableConfiguration(conf), ClassTag$.MODULE$.apply(SerializableConfiguration.class));
      RDD converted = this.convertRDD(rdd, keyConverterClass, valueConverterClass, new WritableToJavaConverter(confBroadcasted));
      return JavaRDD$.MODULE$.<byte[]>fromRDD(SerDeUtil$.MODULE$.pairRDDToPython(converted, batchSize), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   private <K, V, F extends InputFormat<K, V>> RDD<Tuple2<K, V>> newAPIHadoopRDDFromClassNames(JavaSparkContext sc, Option<String> path, String inputFormatClass, String keyClass, String valueClass, Configuration conf) {
      Class kc = Utils$.MODULE$.classForName(keyClass);
      Class vc = Utils$.MODULE$.classForName(valueClass);
      Class fc = Utils$.MODULE$.classForName(inputFormatClass);
      return path.isDefined() ? sc.sc().newAPIHadoopFile((String)path.get(), fc, kc, vc, conf) : sc.sc().newAPIHadoopRDD(conf, fc, kc, vc);
   }

   private <K, V, F extends InputFormat<K, V>> Option<String> newAPIHadoopRDDFromClassNames$default$2() {
      return None$.MODULE$;
   }

   public <K, V, F extends org.apache.hadoop.mapred.InputFormat<K, V>> JavaRDD<byte[]> hadoopFile(JavaSparkContext sc, String path, String inputFormatClass, String keyClass, String valueClass, String keyConverterClass, String valueConverterClass, HashMap<String, String> confAsMap, int batchSize) {
      Configuration mergedConf = this.getMergedConf(confAsMap, sc.hadoopConfiguration());
      RDD rdd = this.hadoopRDDFromClassNames(sc, new Some(path), inputFormatClass, keyClass, valueClass, mergedConf);
      Broadcast confBroadcasted = sc.sc().broadcast(new SerializableConfiguration(mergedConf), ClassTag$.MODULE$.apply(SerializableConfiguration.class));
      RDD converted = this.convertRDD(rdd, keyConverterClass, valueConverterClass, new WritableToJavaConverter(confBroadcasted));
      return JavaRDD$.MODULE$.<byte[]>fromRDD(SerDeUtil$.MODULE$.pairRDDToPython(converted, batchSize), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   public <K, V, F extends org.apache.hadoop.mapred.InputFormat<K, V>> JavaRDD<byte[]> hadoopRDD(JavaSparkContext sc, String inputFormatClass, String keyClass, String valueClass, String keyConverterClass, String valueConverterClass, HashMap<String, String> confAsMap, int batchSize) {
      Configuration conf = PythonHadoopUtil$.MODULE$.mapToConf(confAsMap);
      RDD rdd = this.hadoopRDDFromClassNames(sc, None$.MODULE$, inputFormatClass, keyClass, valueClass, conf);
      Broadcast confBroadcasted = sc.sc().broadcast(new SerializableConfiguration(conf), ClassTag$.MODULE$.apply(SerializableConfiguration.class));
      RDD converted = this.convertRDD(rdd, keyConverterClass, valueConverterClass, new WritableToJavaConverter(confBroadcasted));
      return JavaRDD$.MODULE$.<byte[]>fromRDD(SerDeUtil$.MODULE$.pairRDDToPython(converted, batchSize), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   private <K, V, F extends org.apache.hadoop.mapred.InputFormat<K, V>> RDD<Tuple2<K, V>> hadoopRDDFromClassNames(JavaSparkContext sc, Option<String> path, String inputFormatClass, String keyClass, String valueClass, Configuration conf) {
      Class kc = Utils$.MODULE$.classForName(keyClass);
      Class vc = Utils$.MODULE$.classForName(valueClass);
      Class fc = Utils$.MODULE$.classForName(inputFormatClass);
      return path.isDefined() ? sc.sc().hadoopFile((String)path.get(), fc, kc, vc, sc.sc().hadoopFile$default$5()) : sc.sc().hadoopRDD(new JobConf(conf), fc, kc, vc, sc.sc().hadoopRDD$default$5());
   }

   private <K, V, F extends org.apache.hadoop.mapred.InputFormat<K, V>> Option<String> hadoopRDDFromClassNames$default$2() {
      return None$.MODULE$;
   }

   public void writeUTF(String str, DataOutputStream dataOut) {
      byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
      dataOut.writeInt(bytes.length);
      dataOut.write(bytes);
   }

   public <T> int serveIterator(Iterator<T> items, String threadName) {
      ServerSocket serverSocket = new ServerSocket(0, 1, InetAddress.getByName("localhost"));
      serverSocket.setSoTimeout(15000);
      (new Thread(items, threadName, serverSocket) {
         public final Iterator items$1;
         private final ServerSocket serverSocket$1;

         public void run() {
            try {
               try {
                  Socket sock = this.serverSocket$1.accept();
                  DataOutputStream out = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
                  Utils$.MODULE$.tryWithSafeFinally(new Serializable(this, out) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final DataOutputStream out$1;

                     public final void apply() {
                        this.apply$mcV$sp();
                     }

                     public void apply$mcV$sp() {
                        PythonRDD$.MODULE$.writeIteratorToStream(this.$outer.items$1, this.out$1);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.out$1 = out$1;
                        }
                     }
                  }, new Serializable(this, out) {
                     public static final long serialVersionUID = 0L;
                     private final DataOutputStream out$1;

                     public final void apply() {
                        this.apply$mcV$sp();
                     }

                     public void apply$mcV$sp() {
                        this.out$1.close();
                     }

                     public {
                        this.out$1 = out$1;
                     }
                  });
               } catch (Throwable var13) {
                  Option var4 = NonFatal$.MODULE$.unapply(var13);
                  if (var4.isEmpty()) {
                     throw var13;
                  }

                  Throwable e = (Throwable)var4.get();
                  PythonRDD$.MODULE$.logError(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error while sending iterator"})))).s(Nil$.MODULE$);
                     }
                  }, e);
                  BoxedUnit var6 = BoxedUnit.UNIT;
                  return;
               }
            } finally {
               this.serverSocket$1.close();
            }

         }

         public {
            this.items$1 = items$1;
            this.serverSocket$1 = serverSocket$1;
            this.setDaemon(true);
         }
      }).start();
      return serverSocket.getLocalPort();
   }

   private Configuration getMergedConf(HashMap<String, String> confAsMap, Configuration baseConf) {
      Configuration conf = PythonHadoopUtil$.MODULE$.mapToConf(confAsMap);
      return PythonHadoopUtil$.MODULE$.mergeConfs(baseConf, conf);
   }

   public <K, V> Tuple2<Class<?>, Class<?>> org$apache$spark$api$python$PythonRDD$$inferKeyValueTypes(RDD<Tuple2<K, V>> rdd, String keyConverterClass, String valueConverterClass) {
      Tuple2 var5 = (Tuple2)rdd.first();
      if (var5 != null) {
         Object key = var5._1();
         Object value = var5._2();
         Tuple2 var8 = new Tuple2(key, value);
         Object key = var8._1();
         Object value = var8._2();
         Tuple2 var12 = this.getKeyValueConverters(keyConverterClass, valueConverterClass, new JavaToWritableConverter());
         if (var12 != null) {
            Converter kc = (Converter)var12._1();
            Converter vc = (Converter)var12._2();
            Tuple2 var15 = new Tuple2(kc, vc);
            Converter kc = (Converter)var15._1();
            Converter vc = (Converter)var15._2();
            return new Tuple2<Class<?>, Class<?>>(kc.convert(key).getClass(), vc.convert(value).getClass());
         } else {
            throw new MatchError(var12);
         }
      } else {
         throw new MatchError(var5);
      }
   }

   private <K, V> String inferKeyValueTypes$default$2() {
      return null;
   }

   private <K, V> String inferKeyValueTypes$default$3() {
      return null;
   }

   private Option<Tuple2<Class<?>, Class<?>>> getKeyValueTypes(String keyClass, String valueClass) {
      return Option$.MODULE$.<Tuple2<Class<?>, Class<?>>>apply(keyClass).<Tuple2<Class<?>, Class<?>>>flatMap(new Serializable(valueClass) {
         public static final long serialVersionUID = 0L;
         private final String valueClass$1;

         public final Option<Tuple2<Class<Object>, Class<Object>>> apply(String k) {
            return Option$.MODULE$.<Tuple2<Class<Object>, Class<Object>>>apply(this.valueClass$1).<Tuple2<Class<Object>, Class<Object>>>map(new Serializable(this, k) {
               public static final long serialVersionUID = 0L;
               private final String k$1;

               public final Tuple2<Class<Object>, Class<Object>> apply(String v) {
                  return new Tuple2<Class<Object>, Class<Object>>(Utils$.MODULE$.classForName(this.k$1), Utils$.MODULE$.classForName(v));
               }

               public {
                  this.k$1 = k$1;
               }
            });
         }

         public {
            this.valueClass$1 = valueClass$1;
         }
      });
   }

   private Tuple2<Converter<Object, Object>, Converter<Object, Object>> getKeyValueConverters(String keyConverterClass, String valueConverterClass, Converter<Object, Object> defaultConverter) {
      Converter keyConverter = Converter$.MODULE$.getInstance(Option$.MODULE$.apply(keyConverterClass), defaultConverter);
      Converter valueConverter = Converter$.MODULE$.getInstance(Option$.MODULE$.apply(valueConverterClass), defaultConverter);
      return new Tuple2<Converter<Object, Object>, Converter<Object, Object>>(keyConverter, valueConverter);
   }

   private <K, V> RDD<Tuple2<Object, Object>> convertRDD(RDD<Tuple2<K, V>> rdd, String keyConverterClass, String valueConverterClass, Converter<Object, Object> defaultConverter) {
      Tuple2 var6 = this.getKeyValueConverters(keyConverterClass, valueConverterClass, defaultConverter);
      if (var6 != null) {
         Converter kc = (Converter)var6._1();
         Converter vc = (Converter)var6._2();
         Tuple2 var9 = new Tuple2(kc, vc);
         Converter kc = (Converter)var9._1();
         Converter vc = (Converter)var9._2();
         return PythonHadoopUtil$.MODULE$.convertRDD(rdd, kc, vc);
      } else {
         throw new MatchError(var6);
      }
   }

   public <K, V, C extends CompressionCodec> void saveAsSequenceFile(JavaRDD<byte[]> pyRDD, boolean batchSerialized, String path, String compressionCodecClass) {
      this.saveAsHadoopFile(pyRDD, batchSerialized, path, "org.apache.hadoop.mapred.SequenceFileOutputFormat", (String)null, (String)null, (String)null, (String)null, new HashMap(), compressionCodecClass);
   }

   public <K, V, F extends OutputFormat<?, ?>, C extends CompressionCodec> void saveAsHadoopFile(JavaRDD<byte[]> pyRDD, boolean batchSerialized, String path, String outputFormatClass, String keyClass, String valueClass, String keyConverterClass, String valueConverterClass, HashMap<String, String> confAsMap, String compressionCodecClass) {
      RDD rdd = SerDeUtil$.MODULE$.pythonToPairRDD(JavaRDD$.MODULE$.toRDD(pyRDD), batchSerialized);
      Tuple2 var13 = (Tuple2)this.getKeyValueTypes(keyClass, valueClass).getOrElse(new Serializable(keyConverterClass, valueConverterClass, rdd) {
         public static final long serialVersionUID = 0L;
         private final String keyConverterClass$1;
         private final String valueConverterClass$1;
         private final RDD rdd$1;

         public final Tuple2<Class<?>, Class<?>> apply() {
            return PythonRDD$.MODULE$.org$apache$spark$api$python$PythonRDD$$inferKeyValueTypes(this.rdd$1, this.keyConverterClass$1, this.valueConverterClass$1);
         }

         public {
            this.keyConverterClass$1 = keyConverterClass$1;
            this.valueConverterClass$1 = valueConverterClass$1;
            this.rdd$1 = rdd$1;
         }
      });
      if (var13 != null) {
         Class kc = (Class)var13._1();
         Class vc = (Class)var13._2();
         Tuple2 var16 = new Tuple2(kc, vc);
         Class kc = (Class)var16._1();
         Class vc = (Class)var16._2();
         Configuration mergedConf = this.getMergedConf(confAsMap, pyRDD.context().hadoopConfiguration());
         Option codec = Option$.MODULE$.apply(compressionCodecClass).map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final Class<C> apply(String x$6) {
               return Utils$.MODULE$.classForName(x$6);
            }
         });
         RDD converted = this.convertRDD(rdd, keyConverterClass, valueConverterClass, new JavaToWritableConverter());
         Class fc = Utils$.MODULE$.classForName(outputFormatClass);
         ClassTag x$9 = ClassTag$.MODULE$.Any();
         ClassTag x$10 = ClassTag$.MODULE$.Any();
         RDD$.MODULE$.rddToPairRDDFunctions$default$4(converted);
         Null$ x$11 = null;
         RDD$.MODULE$.rddToPairRDDFunctions(converted, x$9, x$10, (Ordering)null).saveAsHadoopFile(path, kc, vc, fc, new JobConf(mergedConf), codec);
      } else {
         throw new MatchError(var13);
      }
   }

   public <K, V, F extends org.apache.hadoop.mapreduce.OutputFormat<?, ?>> void saveAsNewAPIHadoopFile(JavaRDD<byte[]> pyRDD, boolean batchSerialized, String path, String outputFormatClass, String keyClass, String valueClass, String keyConverterClass, String valueConverterClass, HashMap<String, String> confAsMap) {
      RDD rdd = SerDeUtil$.MODULE$.pythonToPairRDD(JavaRDD$.MODULE$.toRDD(pyRDD), batchSerialized);
      Tuple2 var12 = (Tuple2)this.getKeyValueTypes(keyClass, valueClass).getOrElse(new Serializable(keyConverterClass, valueConverterClass, rdd) {
         public static final long serialVersionUID = 0L;
         private final String keyConverterClass$2;
         private final String valueConverterClass$2;
         private final RDD rdd$2;

         public final Tuple2<Class<?>, Class<?>> apply() {
            return PythonRDD$.MODULE$.org$apache$spark$api$python$PythonRDD$$inferKeyValueTypes(this.rdd$2, this.keyConverterClass$2, this.valueConverterClass$2);
         }

         public {
            this.keyConverterClass$2 = keyConverterClass$2;
            this.valueConverterClass$2 = valueConverterClass$2;
            this.rdd$2 = rdd$2;
         }
      });
      if (var12 != null) {
         Class kc = (Class)var12._1();
         Class vc = (Class)var12._2();
         Tuple2 var15 = new Tuple2(kc, vc);
         Class kc = (Class)var15._1();
         Class vc = (Class)var15._2();
         Configuration mergedConf = this.getMergedConf(confAsMap, pyRDD.context().hadoopConfiguration());
         RDD converted = this.convertRDD(rdd, keyConverterClass, valueConverterClass, new JavaToWritableConverter());
         Class fc = Utils$.MODULE$.classForName(outputFormatClass);
         ClassTag x$13 = ClassTag$.MODULE$.Any();
         ClassTag x$14 = ClassTag$.MODULE$.Any();
         RDD$.MODULE$.rddToPairRDDFunctions$default$4(converted);
         Null$ x$15 = null;
         RDD$.MODULE$.rddToPairRDDFunctions(converted, x$13, x$14, (Ordering)null).saveAsNewAPIHadoopFile(path, kc, vc, fc, mergedConf);
      } else {
         throw new MatchError(var12);
      }
   }

   public <K, V> void saveAsHadoopDataset(JavaRDD<byte[]> pyRDD, boolean batchSerialized, HashMap<String, String> confAsMap, String keyConverterClass, String valueConverterClass, boolean useNewAPI) {
      Configuration conf = PythonHadoopUtil$.MODULE$.mapToConf(confAsMap);
      RDD converted = this.convertRDD(SerDeUtil$.MODULE$.pythonToPairRDD(JavaRDD$.MODULE$.toRDD(pyRDD), batchSerialized), keyConverterClass, valueConverterClass, new JavaToWritableConverter());
      if (useNewAPI) {
         ClassTag x$17 = ClassTag$.MODULE$.Any();
         ClassTag x$18 = ClassTag$.MODULE$.Any();
         RDD$.MODULE$.rddToPairRDDFunctions$default$4(converted);
         Null$ x$19 = null;
         RDD$.MODULE$.rddToPairRDDFunctions(converted, x$17, x$18, (Ordering)null).saveAsNewAPIHadoopDataset(conf);
      } else {
         ClassTag x$21 = ClassTag$.MODULE$.Any();
         ClassTag x$22 = ClassTag$.MODULE$.Any();
         RDD$.MODULE$.rddToPairRDDFunctions$default$4(converted);
         Null$ x$23 = null;
         RDD$.MODULE$.rddToPairRDDFunctions(converted, x$21, x$22, (Ordering)null).saveAsHadoopDataset(new JobConf(conf));
      }

   }

   private Object readResolve() {
      return MODULE$;
   }

   public final void org$apache$spark$api$python$PythonRDD$$write$1(Object obj, DataOutputStream dataOut$1) {
      while(true) {
         if (obj == null) {
            dataOut$1.writeInt(SpecialLengths$.MODULE$.NULL());
            BoxedUnit var5 = BoxedUnit.UNIT;
         } else if (obj instanceof byte[]) {
            byte[] var6 = (byte[])obj;
            dataOut$1.writeInt(var6.length);
            dataOut$1.write(var6);
            BoxedUnit var12 = BoxedUnit.UNIT;
         } else {
            if (!(obj instanceof String)) {
               if (obj instanceof PortableDataStream) {
                  PortableDataStream var8 = (PortableDataStream)obj;
                  obj = var8.toArray();
                  continue;
               }

               if (obj instanceof Tuple2) {
                  Tuple2 var9 = (Tuple2)obj;
                  Object key = var9._1();
                  Object value = var9._2();
                  this.org$apache$spark$api$python$PythonRDD$$write$1(key, dataOut$1);
                  obj = value;
                  continue;
               }

               throw new SparkException((new StringBuilder()).append((Object)"Unexpected element type ").append((Object)obj.getClass()).toString());
            }

            String var7 = (String)obj;
            this.writeUTF(var7, dataOut$1);
            BoxedUnit var13 = BoxedUnit.UNIT;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
         return;
      }
   }

   private PythonRDD$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.workerBroadcasts = new WeakHashMap<Socket, Set<Object>>();
   }
}
