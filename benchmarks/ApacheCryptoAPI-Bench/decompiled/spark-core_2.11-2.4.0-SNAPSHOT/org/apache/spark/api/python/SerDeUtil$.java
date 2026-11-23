package org.apache.spark.api.python;

import java.util.ArrayList;
import net.razorvine.pickle.Pickler;
import net.razorvine.pickle.Unpickler;
import org.apache.spark.SparkException;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaRDD$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rdd.RDD;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple2$mcZZ$sp;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.immutable.StringOps;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.ScalaRunTime$;
import scala.util.Failure;
import scala.util.Try;
import scala.util.Try$;

public final class SerDeUtil$ implements Logging {
   public static final SerDeUtil$ MODULE$;
   private boolean initialized;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SerDeUtil$();
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

   private boolean initialized() {
      return this.initialized;
   }

   private void initialized_$eq(boolean x$1) {
      this.initialized = x$1;
   }

   public synchronized void initialize() {
      if (!this.initialized()) {
         Unpickler.registerConstructor("array", "array", new SerDeUtil.ArrayConstructor());
         Unpickler.registerConstructor("__builtin__", "bytearray", new SerDeUtil.ByteArrayConstructor());
         Unpickler.registerConstructor("builtins", "bytearray", new SerDeUtil.ByteArrayConstructor());
         Unpickler.registerConstructor("__builtin__", "bytes", new SerDeUtil.ByteArrayConstructor());
         Unpickler.registerConstructor("_codecs", "encode", new SerDeUtil.ByteArrayConstructor());
         this.initialized_$eq(true);
      }

   }

   public JavaRDD<Object> toJavaArray(JavaRDD<Object> jrdd) {
      return jrdd.rdd().<Object>map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Object apply(Object x0$1) {
            Object var4;
            if (x0$1 instanceof ArrayList) {
               ArrayList var3 = (ArrayList)x0$1;
               var4 = var3.toArray();
            } else {
               if (!x0$1.getClass().isArray()) {
                  throw new MatchError(x0$1);
               }

               var4 = Predef$.MODULE$.genericArrayOps(x0$1).toArray(ClassTag$.MODULE$.apply(Object.class));
            }

            return var4;
         }
      }, ClassTag$.MODULE$.apply(Object.class)).toJavaRDD();
   }

   public JavaRDD<byte[]> javaToPython(JavaRDD<?> jRDD) {
      return JavaRDD$.MODULE$.<byte[]>fromRDD(jRDD.rdd().mapPartitions(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final SerDeUtil.AutoBatchedPickler apply(Iterator<Object> iter) {
            return new SerDeUtil.AutoBatchedPickler(iter);
         }
      }, jRDD.rdd().mapPartitions$default$2(), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE))), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   public JavaRDD<Object> pythonToJava(JavaRDD<byte[]> pyRDD, boolean batched) {
      return pyRDD.rdd().<Object>mapPartitions(new Serializable(batched) {
         public static final long serialVersionUID = 0L;
         public final boolean batched$1;

         public final Iterator<Object> apply(Iterator<byte[]> iter) {
            SerDeUtil$.MODULE$.initialize();
            Unpickler unpickle = new Unpickler();
            return iter.<Object>flatMap(new Serializable(this, unpickle) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Unpickler unpickle$1;

               public final Seq<Object> apply(byte[] row) {
                  Object obj = this.unpickle$1.loads(row);
                  Seq var10000;
                  if (this.$outer.batched$1) {
                     Seq var5;
                     if (obj instanceof Object[]) {
                        Object[] var4 = obj;
                        var5 = Predef$.MODULE$.genericArrayOps(var4).toSeq();
                     } else {
                        var5 = (Seq)JavaConverters$.MODULE$.asScalaBufferConverter((ArrayList)obj).asScala();
                     }

                     var10000 = var5;
                  } else {
                     var10000 = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Object[]{obj}));
                  }

                  return var10000;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.unpickle$1 = unpickle$1;
                  }
               }
            });
         }

         public {
            this.batched$1 = batched$1;
         }
      }, pyRDD.rdd().mapPartitions$default$2(), ClassTag$.MODULE$.Any()).toJavaRDD();
   }

   private Tuple2<Object, Object> checkPickle(Tuple2<Object, Object> t) {
      Pickler pickle = new Pickler();
      Try kt = Try$.MODULE$.apply(new Serializable(t, pickle) {
         public static final long serialVersionUID = 0L;
         private final Tuple2 t$1;
         private final Pickler pickle$1;

         public final byte[] apply() {
            return this.pickle$1.dumps(this.t$1._1());
         }

         public {
            this.t$1 = t$1;
            this.pickle$1 = pickle$1;
         }
      });
      Try vt = Try$.MODULE$.apply(new Serializable(t, pickle) {
         public static final long serialVersionUID = 0L;
         private final Tuple2 t$1;
         private final Pickler pickle$1;

         public final byte[] apply() {
            return this.pickle$1.dumps(this.t$1._2());
         }

         public {
            this.t$1 = t$1;
            this.pickle$1 = pickle$1;
         }
      });
      Tuple2 var5 = new Tuple2(kt, vt);
      Tuple2$mcZZ$sp var12;
      if (var5 != null) {
         Try var6 = (Try)var5._1();
         Try var7 = (Try)var5._2();
         if (var6 instanceof Failure) {
            Failure var8 = (Failure)var6;
            Throwable kf = var8.exception();
            if (var7 instanceof Failure) {
               Failure var10 = (Failure)var7;
               Throwable vf = var10.exception();
               this.logWarning(new Serializable(t, kf) {
                  public static final long serialVersionUID = 0L;
                  private final Tuple2 t$1;
                  private final Throwable kf$1;

                  public final String apply() {
                     return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n               |Failed to pickle Java object as key: ", ", falling back\n               |to 'toString'. Error: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.t$1._1().getClass().getSimpleName(), this.kf$1.getMessage()}))))).stripMargin();
                  }

                  public {
                     this.t$1 = t$1;
                     this.kf$1 = kf$1;
                  }
               });
               this.logWarning(new Serializable(t, vf) {
                  public static final long serialVersionUID = 0L;
                  private final Tuple2 t$1;
                  private final Throwable vf$1;

                  public final String apply() {
                     return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n               |Failed to pickle Java object as value: ", ", falling back\n               |to 'toString'. Error: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.t$1._2().getClass().getSimpleName(), this.vf$1.getMessage()}))))).stripMargin();
                  }

                  public {
                     this.t$1 = t$1;
                     this.vf$1 = vf$1;
                  }
               });
               var12 = new Tuple2$mcZZ$sp(true, true);
               return var12;
            }
         }
      }

      if (var5 != null) {
         Try var13 = (Try)var5._1();
         if (var13 instanceof Failure) {
            Failure var14 = (Failure)var13;
            Throwable kf = var14.exception();
            this.logWarning(new Serializable(t, kf) {
               public static final long serialVersionUID = 0L;
               private final Tuple2 t$1;
               private final Throwable kf$2;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n               |Failed to pickle Java object as key: ", ", falling back\n               |to 'toString'. Error: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.t$1._1().getClass().getSimpleName(), this.kf$2.getMessage()}))))).stripMargin();
               }

               public {
                  this.t$1 = t$1;
                  this.kf$2 = kf$2;
               }
            });
            var12 = new Tuple2$mcZZ$sp(true, false);
            return var12;
         }
      }

      if (var5 != null) {
         Try var16 = (Try)var5._2();
         if (var16 instanceof Failure) {
            Failure var17 = (Failure)var16;
            Throwable vf = var17.exception();
            this.logWarning(new Serializable(t, vf) {
               public static final long serialVersionUID = 0L;
               private final Tuple2 t$1;
               private final Throwable vf$2;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n               |Failed to pickle Java object as value: ", ", falling back\n               |to 'toString'. Error: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.t$1._2().getClass().getSimpleName(), this.vf$2.getMessage()}))))).stripMargin();
               }

               public {
                  this.t$1 = t$1;
                  this.vf$2 = vf$2;
               }
            });
            var12 = new Tuple2$mcZZ$sp(false, true);
            return var12;
         }
      }

      var12 = new Tuple2$mcZZ$sp(false, false);
      return var12;
   }

   public RDD<byte[]> pairRDDToPython(RDD<Tuple2<Object, Object>> rdd, int batchSize) {
      Tuple2[] var5 = (Tuple2[])rdd.take(1);
      Option var6 = Array$.MODULE$.unapplySeq(var5);
      Object var7;
      if (!var6.isEmpty() && var6.get() != null && ((SeqLike)var6.get()).lengthCompare(0) == 0) {
         var7 = new Tuple2$mcZZ$sp(false, false);
      } else {
         Option var8 = Array$.MODULE$.unapplySeq(var5);
         if (var8.isEmpty() || var8.get() == null || ((SeqLike)var8.get()).lengthCompare(1) != 0) {
            throw new MatchError(var5);
         }

         Tuple2 first = (Tuple2)((SeqLike)var8.get()).apply(0);
         var7 = this.checkPickle(first);
      }

      if (var7 != null) {
         boolean keyFailed = ((Tuple2)var7)._1$mcZ$sp();
         boolean valueFailed = ((Tuple2)var7)._2$mcZ$sp();
         Tuple2$mcZZ$sp var12 = new Tuple2$mcZZ$sp(keyFailed, valueFailed);
         boolean keyFailed = ((Tuple2)var12)._1$mcZ$sp();
         boolean valueFailed = ((Tuple2)var12)._2$mcZ$sp();
         return rdd.<byte[]>mapPartitions(new Serializable(batchSize, keyFailed, valueFailed) {
            public static final long serialVersionUID = 0L;
            private final int batchSize$1;
            public final boolean keyFailed$1;
            public final boolean valueFailed$1;

            public final Iterator<byte[]> apply(Iterator<Tuple2<Object, Object>> iter) {
               Iterator cleaned = iter.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final Object[] apply(Tuple2<Object, Object> x0$2) {
                     if (x0$2 != null) {
                        Object k = x0$2._1();
                        Object v = x0$2._2();
                        Object key = this.$outer.keyFailed$1 ? k.toString() : k;
                        Object value = this.$outer.valueFailed$1 ? v.toString() : v;
                        Object[] var5 = Array$.MODULE$.apply(Predef$.MODULE$.genericWrapArray(new Object[]{key, value}), ClassTag$.MODULE$.Any());
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
                     }
                  }
               });
               Object var10000;
               if (this.batchSize$1 == 0) {
                  var10000 = new SerDeUtil.AutoBatchedPickler(cleaned);
               } else {
                  Pickler pickle = new Pickler();
                  var10000 = cleaned.grouped(this.batchSize$1).map(new Serializable(this, pickle) {
                     public static final long serialVersionUID = 0L;
                     private final Pickler pickle$2;

                     public final byte[] apply(Seq<Object[]> batched) {
                        return this.pickle$2.dumps(JavaConverters$.MODULE$.seqAsJavaListConverter(batched).asJava());
                     }

                     public {
                        this.pickle$2 = pickle$2;
                     }
                  });
               }

               return (Iterator<byte[]>)var10000;
            }

            public {
               this.batchSize$1 = batchSize$1;
               this.keyFailed$1 = keyFailed$1;
               this.valueFailed$1 = valueFailed$1;
            }
         }, rdd.mapPartitions$default$2(), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
      } else {
         throw new MatchError(var7);
      }
   }

   public <K, V> RDD<Tuple2<K, V>> pythonToPairRDD(RDD<byte[]> pyRDD, boolean batched) {
      RDD rdd = this.pythonToJava(JavaRDD$.MODULE$.fromRDD(pyRDD, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE))), batched).rdd();
      Object[] var4 = rdd.take(1);
      Option var5 = Array$.MODULE$.unapplySeq(var4);
      if (!var5.isEmpty() && var5.get() != null && ((SeqLike)var5.get()).lengthCompare(1) == 0) {
         Object obj = ((SeqLike)var5.get()).apply(0);
         if (this.isPair$1(obj)) {
            BoxedUnit var11 = BoxedUnit.UNIT;
            return rdd.<Tuple2<K, V>>map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final Tuple2<K, V> apply(Object obj) {
                  return new Tuple2<K, V>(Predef$.MODULE$.genericArrayOps(obj).head(), Predef$.MODULE$.genericArrayOps(obj).last());
               }
            }, ClassTag$.MODULE$.apply(Tuple2.class));
         }
      }

      Option var8 = Array$.MODULE$.unapplySeq(var4);
      if (var8.isEmpty() || var8.get() == null || ((SeqLike)var8.get()).lengthCompare(0) != 0) {
         Option var9 = Array$.MODULE$.unapplySeq(var4);
         if (!var9.isEmpty() && var9.get() != null && ((SeqLike)var9.get()).lengthCompare(1) == 0) {
            Object other = ((SeqLike)var9.get()).apply(0);
            throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"RDD element of type ", " cannot be used"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{other.getClass().getName()})));
         } else {
            throw new MatchError(var4);
         }
      } else {
         BoxedUnit var7 = BoxedUnit.UNIT;
         return rdd.<Tuple2<K, V>>map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final Tuple2<K, V> apply(Object obj) {
               return new Tuple2<K, V>(Predef$.MODULE$.genericArrayOps(obj).head(), Predef$.MODULE$.genericArrayOps(obj).last());
            }
         }, ClassTag$.MODULE$.apply(Tuple2.class));
      }
   }

   private final boolean isPair$1(Object obj) {
      return Option$.MODULE$.apply(obj.getClass().getComponentType()).exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Class<Object> x$2) {
            return !x$2.isPrimitive();
         }
      }) && ScalaRunTime$.MODULE$.array_length(obj) == 2;
   }

   private SerDeUtil$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.initialized = false;
      this.initialize();
   }
}
