package org.apache.spark.rdd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.io.CompressionCodec$;
import org.apache.spark.serializer.DeserializationStream;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.util.SerializableConfiguration;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Iterator;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

public final class ReliableCheckpointRDD$ implements Logging, Serializable {
   public static final ReliableCheckpointRDD$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new ReliableCheckpointRDD$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   public String org$apache$spark$rdd$ReliableCheckpointRDD$$checkpointFileName(int partitionIndex) {
      return (new StringOps(Predef$.MODULE$.augmentString("part-%05d"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(partitionIndex)}));
   }

   private String checkpointPartitionerFileName() {
      return "_partitioner";
   }

   public <T> ReliableCheckpointRDD<T> writeRDDToCheckpointDirectory(RDD<T> originalRDD, String checkpointDir, int blockSize, ClassTag<T> evidence$2) {
      long checkpointStartTimeNs = System.nanoTime();
      SparkContext sc = originalRDD.sparkContext();
      Path checkpointDirPath = new Path(checkpointDir);
      FileSystem fs = checkpointDirPath.getFileSystem(sc.hadoopConfiguration());
      if (fs.mkdirs(checkpointDirPath)) {
         Broadcast broadcastedConf = sc.broadcast(new SerializableConfiguration(sc.hadoopConfiguration()), ClassTag$.MODULE$.apply(SerializableConfiguration.class));
         String var11 = checkpointDirPath.toString();
         int var12 = this.writePartitionToCheckpointFile$default$3();
         sc.runJob(originalRDD, new Serializable(evidence$2, broadcastedConf, var11, var12) {
            public static final long serialVersionUID = 0L;
            private final ClassTag evidence$2$1;
            private final Broadcast broadcastedConf$1;
            private final String eta$0$1$1;
            private final int eta$1$1$1;

            public final void apply(TaskContext ctx, Iterator<T> iterator) {
               ReliableCheckpointRDD$.MODULE$.writePartitionToCheckpointFile(this.eta$0$1$1, this.broadcastedConf$1, this.eta$1$1$1, ctx, iterator, this.evidence$2$1);
            }

            public {
               this.evidence$2$1 = evidence$2$1;
               this.broadcastedConf$1 = broadcastedConf$1;
               this.eta$0$1$1 = eta$0$1$1;
               this.eta$1$1$1 = eta$1$1$1;
            }
         }, ClassTag$.MODULE$.Unit());
         if (originalRDD.partitioner().nonEmpty()) {
            this.writePartitionerToCheckpointDir(sc, (Partitioner)originalRDD.partitioner().get(), checkpointDirPath);
         }

         long checkpointDurationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - checkpointStartTimeNs);
         this.logInfo(new Serializable(checkpointDurationMs) {
            public static final long serialVersionUID = 0L;
            private final long checkpointDurationMs$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Checkpointing took ", " ms."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.checkpointDurationMs$1)}));
            }

            public {
               this.checkpointDurationMs$1 = checkpointDurationMs$1;
            }
         });
         ReliableCheckpointRDD newRDD = new ReliableCheckpointRDD(sc, checkpointDirPath.toString(), originalRDD.partitioner(), evidence$2);
         if (newRDD.partitions().length != originalRDD.partitions().length) {
            throw new SparkException((new StringBuilder()).append((Object)"Checkpoint RDD has a different number of partitions from original RDD. Original ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"RDD [ID: ", ", num of partitions: ", "]; "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(originalRDD.id()), BoxesRunTime.boxToInteger(originalRDD.partitions().length)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Checkpoint RDD [ID: ", ", num of partitions: "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(newRDD.id())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "]."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(newRDD.partitions().length)}))).toString());
         } else {
            return newRDD;
         }
      } else {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to create checkpoint path ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{checkpointDirPath})));
      }
   }

   public <T> int writeRDDToCheckpointDirectory$default$3() {
      return -1;
   }

   public <T> void writePartitionToCheckpointFile(String path, Broadcast<SerializableConfiguration> broadcastedConf, int blockSize, TaskContext ctx, Iterator<T> iterator, ClassTag<T> evidence$3) {
      SparkEnv env = SparkEnv$.MODULE$.get();
      Path outputDir = new Path(path);
      FileSystem fs = outputDir.getFileSystem(((SerializableConfiguration)broadcastedConf.value()).value());
      String finalOutputName = this.org$apache$spark$rdd$ReliableCheckpointRDD$$checkpointFileName(ctx.partitionId());
      Path finalOutputPath = new Path(outputDir, finalOutputName);
      Path tempOutputPath = new Path(outputDir, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{".", "-attempt-", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{finalOutputName, BoxesRunTime.boxToInteger(ctx.attemptNumber())})));
      int bufferSize = env.conf().getInt("spark.buffer.size", 65536);
      Object var10000;
      if (blockSize < 0) {
         FSDataOutputStream fileStream = fs.create(tempOutputPath, false, bufferSize);
         var10000 = BoxesRunTime.unboxToBoolean(env.conf().get(org.apache.spark.internal.config.package$.MODULE$.CHECKPOINT_COMPRESS())) ? CompressionCodec$.MODULE$.createCodec(env.conf()).compressedOutputStream(fileStream) : fileStream;
      } else {
         var10000 = fs.create(tempOutputPath, false, bufferSize, fs.getDefaultReplication(fs.getWorkingDirectory()), (long)blockSize);
      }

      OutputStream fileOutputStream = (OutputStream)var10000;
      SerializerInstance serializer = env.serializer().newInstance();
      SerializationStream serializeStream = serializer.serializeStream(fileOutputStream);
      Utils$.MODULE$.tryWithSafeFinally(new Serializable(iterator, evidence$3, serializeStream) {
         public static final long serialVersionUID = 0L;
         private final Iterator iterator$1;
         private final ClassTag evidence$3$1;
         private final SerializationStream serializeStream$1;

         public final SerializationStream apply() {
            return this.serializeStream$1.writeAll(this.iterator$1, this.evidence$3$1);
         }

         public {
            this.iterator$1 = iterator$1;
            this.evidence$3$1 = evidence$3$1;
            this.serializeStream$1 = serializeStream$1;
         }
      }, new Serializable(serializeStream) {
         public static final long serialVersionUID = 0L;
         private final SerializationStream serializeStream$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.serializeStream$1.close();
         }

         public {
            this.serializeStream$1 = serializeStream$1;
         }
      });
      if (!fs.rename(tempOutputPath, finalOutputPath)) {
         if (!fs.exists(finalOutputPath)) {
            this.logInfo(new Serializable(tempOutputPath) {
               public static final long serialVersionUID = 0L;
               private final Path tempOutputPath$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Deleting tempOutputPath ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.tempOutputPath$1}));
               }

               public {
                  this.tempOutputPath$1 = tempOutputPath$1;
               }
            });
            fs.delete(tempOutputPath, false);
            throw new IOException((new StringBuilder()).append((Object)"Checkpoint failed: failed to save output of task: ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " and final output path does not exist: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(ctx.attemptNumber()), finalOutputPath}))).toString());
         }

         this.logInfo(new Serializable(finalOutputPath) {
            public static final long serialVersionUID = 0L;
            private final Path finalOutputPath$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Final output path ", " already exists; not overwriting it"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.finalOutputPath$1}));
            }

            public {
               this.finalOutputPath$1 = finalOutputPath$1;
            }
         });
         if (!fs.delete(tempOutputPath, false)) {
            this.logWarning(new Serializable(tempOutputPath) {
               public static final long serialVersionUID = 0L;
               private final Path tempOutputPath$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.tempOutputPath$1}));
               }

               public {
                  this.tempOutputPath$1 = tempOutputPath$1;
               }
            });
         }
      }

   }

   public <T> int writePartitionToCheckpointFile$default$3() {
      return -1;
   }

   private void writePartitionerToCheckpointDir(SparkContext sc, Partitioner partitioner, Path checkpointDirPath) {
      try {
         Path partitionerFilePath = new Path(checkpointDirPath, this.checkpointPartitionerFileName());
         int bufferSize = sc.conf().getInt("spark.buffer.size", 65536);
         FileSystem fs = partitionerFilePath.getFileSystem(sc.hadoopConfiguration());
         FSDataOutputStream fileOutputStream = fs.create(partitionerFilePath, false, bufferSize);
         SerializerInstance serializer = SparkEnv$.MODULE$.get().serializer().newInstance();
         SerializationStream serializeStream = serializer.serializeStream(fileOutputStream);
         Utils$.MODULE$.tryWithSafeFinally(new Serializable(partitioner, serializeStream) {
            public static final long serialVersionUID = 0L;
            private final Partitioner partitioner$1;
            private final SerializationStream serializeStream$2;

            public final SerializationStream apply() {
               return this.serializeStream$2.writeObject(this.partitioner$1, ClassTag$.MODULE$.apply(Partitioner.class));
            }

            public {
               this.partitioner$1 = partitioner$1;
               this.serializeStream$2 = serializeStream$2;
            }
         }, new Serializable(serializeStream) {
            public static final long serialVersionUID = 0L;
            private final SerializationStream serializeStream$2;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.serializeStream$2.close();
            }

            public {
               this.serializeStream$2 = serializeStream$2;
            }
         });
         this.logDebug(new Serializable(partitionerFilePath) {
            public static final long serialVersionUID = 0L;
            private final Path partitionerFilePath$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Written partitioner to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.partitionerFilePath$1}));
            }

            public {
               this.partitionerFilePath$1 = partitionerFilePath$1;
            }
         });
      } catch (Throwable var15) {
         Option var6 = NonFatal$.MODULE$.unapply(var15);
         if (var6.isEmpty()) {
            throw var15;
         }

         this.logWarning(new Serializable(partitioner, checkpointDirPath) {
            public static final long serialVersionUID = 0L;
            private final Partitioner partitioner$1;
            private final Path checkpointDirPath$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error writing partitioner ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.partitioner$1, this.checkpointDirPath$2}));
            }

            public {
               this.partitioner$1 = partitioner$1;
               this.checkpointDirPath$2 = checkpointDirPath$2;
            }
         });
         BoxedUnit var7 = BoxedUnit.UNIT;
         return;
      }

   }

   public Option<Partitioner> org$apache$spark$rdd$ReliableCheckpointRDD$$readCheckpointedPartitionerFile(SparkContext sc, String checkpointDirPath) {
      Object var10000;
      try {
         int bufferSize = sc.conf().getInt("spark.buffer.size", 65536);
         Path partitionerFilePath = new Path(checkpointDirPath, this.checkpointPartitionerFileName());
         FileSystem fs = partitionerFilePath.getFileSystem(sc.hadoopConfiguration());
         FSDataInputStream fileInputStream = fs.open(partitionerFilePath, bufferSize);
         SerializerInstance serializer = SparkEnv$.MODULE$.get().serializer().newInstance();
         Partitioner partitioner = (Partitioner)Utils$.MODULE$.tryWithSafeFinally(new Serializable(fileInputStream, serializer) {
            public static final long serialVersionUID = 0L;
            private final FSDataInputStream fileInputStream$1;
            private final SerializerInstance serializer$1;

            public final Partitioner apply() {
               DeserializationStream deserializeStream = this.serializer$1.deserializeStream(this.fileInputStream$1);
               return (Partitioner)Utils$.MODULE$.tryWithSafeFinally(new Serializable(this, deserializeStream) {
                  public static final long serialVersionUID = 0L;
                  private final DeserializationStream deserializeStream$1;

                  public final Partitioner apply() {
                     return (Partitioner)this.deserializeStream$1.readObject(ClassTag$.MODULE$.apply(Partitioner.class));
                  }

                  public {
                     this.deserializeStream$1 = deserializeStream$1;
                  }
               }, new Serializable(this, deserializeStream) {
                  public static final long serialVersionUID = 0L;
                  private final DeserializationStream deserializeStream$1;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     this.deserializeStream$1.close();
                  }

                  public {
                     this.deserializeStream$1 = deserializeStream$1;
                  }
               });
            }

            public {
               this.fileInputStream$1 = fileInputStream$1;
               this.serializer$1 = serializer$1;
            }
         }, new Serializable(fileInputStream) {
            public static final long serialVersionUID = 0L;
            private final FSDataInputStream fileInputStream$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.fileInputStream$1.close();
            }

            public {
               this.fileInputStream$1 = fileInputStream$1;
            }
         });
         this.logDebug(new Serializable(partitionerFilePath) {
            public static final long serialVersionUID = 0L;
            private final Path partitionerFilePath$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Read partitioner from ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.partitionerFilePath$2}));
            }

            public {
               this.partitionerFilePath$2 = partitionerFilePath$2;
            }
         });
         var10000 = new Some(partitioner);
      } catch (Throwable var16) {
         None$ var6;
         if (var16 instanceof FileNotFoundException) {
            FileNotFoundException var5 = (FileNotFoundException)var16;
            this.logDebug(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "No partitioner file";
               }
            }, var5);
            var6 = None$.MODULE$;
         } else {
            Option var7 = NonFatal$.MODULE$.unapply(var16);
            if (var7.isEmpty()) {
               throw var16;
            }

            Throwable e = (Throwable)var7.get();
            this.logWarning(new Serializable(checkpointDirPath) {
               public static final long serialVersionUID = 0L;
               private final String checkpointDirPath$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error reading partitioner from ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.checkpointDirPath$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"partitioner will not be recovered which may lead to performance loss"})))).s(Nil$.MODULE$)).toString();
               }

               public {
                  this.checkpointDirPath$1 = checkpointDirPath$1;
               }
            }, e);
            var6 = None$.MODULE$;
         }

         var10000 = var6;
         return (Option<Partitioner>)var10000;
      }

      return (Option<Partitioner>)var10000;
   }

   public <T> Iterator<T> readCheckpointFile(Path path, Broadcast<SerializableConfiguration> broadcastedConf, TaskContext context) {
      SparkEnv env = SparkEnv$.MODULE$.get();
      FileSystem fs = path.getFileSystem(((SerializableConfiguration)broadcastedConf.value()).value());
      int bufferSize = env.conf().getInt("spark.buffer.size", 65536);
      FSDataInputStream fileStream = fs.open(path, bufferSize);
      InputStream fileInputStream = (InputStream)(BoxesRunTime.unboxToBoolean(env.conf().get(org.apache.spark.internal.config.package$.MODULE$.CHECKPOINT_COMPRESS())) ? CompressionCodec$.MODULE$.createCodec(env.conf()).compressedInputStream(fileStream) : fileStream);
      SerializerInstance serializer = env.serializer().newInstance();
      DeserializationStream deserializeStream = serializer.deserializeStream(fileInputStream);
      context.addTaskCompletionListener((Function1)(new Serializable(deserializeStream) {
         public static final long serialVersionUID = 0L;
         private final DeserializationStream deserializeStream$2;

         public final void apply(TaskContext context) {
            this.deserializeStream$2.close();
         }

         public {
            this.deserializeStream$2 = deserializeStream$2;
         }
      }));
      return deserializeStream.asIterator();
   }

   public <T> Option<Partitioner> $lessinit$greater$default$3() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ReliableCheckpointRDD$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
