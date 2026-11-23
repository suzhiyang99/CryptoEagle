package org.apache.spark.rdd;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.Null$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-c\u0001B\u0001\u0003\u0001-\u0011\u0001dU3rk\u0016t7-\u001a$jY\u0016\u0014F\t\u0012$v]\u000e$\u0018n\u001c8t\u0015\t\u0019A!A\u0002sI\u0012T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001+\raq%M\n\u0005\u00015\u0019\u0012\u0004\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)]i\u0011!\u0006\u0006\u0003-\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00031U\u0011q\u0001T8hO&tw\r\u0005\u0002\u000f5%\u00111d\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t;\u0001\u0011\t\u0011)A\u0005=\u0005!1/\u001a7g!\ry\u0002EI\u0007\u0002\u0005%\u0011\u0011E\u0001\u0002\u0004%\u0012#\u0005\u0003\u0002\b$KAJ!\u0001J\b\u0003\rQ+\b\u000f\\33!\t1s\u0005\u0004\u0001\u0005\u000b!\u0002!\u0019A\u0015\u0003\u0003-\u000b\"AK\u0017\u0011\u00059Y\u0013B\u0001\u0017\u0010\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"A\u0004\u0018\n\u0005=z!aA!osB\u0011a%\r\u0003\u0006e\u0001\u0011\r!\u000b\u0002\u0002-\"AA\u0007\u0001B\u0001B\u0003%Q'A\t`W\u0016LxK]5uC\ndWm\u00117bgN\u0004$A\u000e \u0011\u0007]RTH\u0004\u0002\u000fq%\u0011\u0011hD\u0001\u0007!J,G-\u001a4\n\u0005mb$!B\"mCN\u001c(BA\u001d\u0010!\t1c\bB\u0005@g\u0005\u0005\t\u0011!B\u0001\u0001\n\u0019q\fJ\u0019\u0012\u0005)\n\u0005C\u0001\"H\u001b\u0005\u0019%B\u0001#F\u0003\tIwN\u0003\u0002G\r\u00051\u0001.\u00193p_BL!\u0001S\"\u0003\u0011]\u0013\u0018\u000e^1cY\u0016D\u0001B\u0013\u0001\u0003\u0002\u0003\u0006IaS\u0001\u0014?Z\fG.^3Xe&$\u0018M\u00197f\u00072\f7o\u001d\u0019\u0003\u0019:\u00032a\u000e\u001eN!\t1c\nB\u0005P\u0013\u0006\u0005\t\u0011!B\u0001\u0001\n\u0019q\f\n\u001a\t\u0011E\u0003!1!Q\u0001\fI\u000b!\"\u001a<jI\u0016t7-\u001a\u00132!\u0011q1+J!\n\u0005Q{!!\u0003$v]\u000e$\u0018n\u001c82\u0011!1\u0006AaA!\u0002\u00179\u0016AC3wS\u0012,gnY3%eA\u0019\u0001lW\u0013\u000e\u0003eS!AW\b\u0002\u000fI,g\r\\3di&\u0011A,\u0017\u0002\t\u00072\f7o\u001d+bO\"Aa\f\u0001B\u0002B\u0003-q,\u0001\u0006fm&$WM\\2fIM\u0002BAD*1\u0003\"A\u0011\r\u0001B\u0002B\u0003-!-\u0001\u0006fm&$WM\\2fIQ\u00022\u0001W.1\u0011\u0015!\u0007\u0001\"\u0001f\u0003\u0019a\u0014N\\5u}Q!a\r\\7s)\u00159\u0007.\u001b6l!\u0011y\u0002!\n\u0019\t\u000bE\u001b\u00079\u0001*\t\u000bY\u001b\u00079A,\t\u000by\u001b\u00079A0\t\u000b\u0005\u001c\u00079\u00012\t\u000bu\u0019\u0007\u0019\u0001\u0010\t\u000bQ\u001a\u0007\u0019\u000181\u0005=\f\bcA\u001c;aB\u0011a%\u001d\u0003\n\u007f5\f\t\u0011!A\u0003\u0002\u0001CQAS2A\u0002M\u0004$\u0001\u001e<\u0011\u0007]RT\u000f\u0005\u0002'm\u0012IqJ]A\u0001\u0002\u0003\u0015\t\u0001\u0011\u0005\u0006q\u0002!\t!_\u0001\u0013g\u00064X-Q:TKF,XM\\2f\r&dW\r\u0006\u0003{{\u0006\u0015\u0001C\u0001\b|\u0013\taxB\u0001\u0003V]&$\b\"\u0002@x\u0001\u0004y\u0018\u0001\u00029bi\"\u00042aNA\u0001\u0013\r\t\u0019\u0001\u0010\u0002\u0007'R\u0014\u0018N\\4\t\u0013\u0005\u001dq\u000f%AA\u0002\u0005%\u0011!B2pI\u0016\u001c\u0007#\u0002\b\u0002\f\u0005=\u0011bAA\u0007\u001f\t1q\n\u001d;j_:\u0004D!!\u0005\u0002\u0016A!qGOA\n!\r1\u0013Q\u0003\u0003\r\u0003/\t)!!A\u0001\u0002\u000b\u0005\u0011\u0011\u0004\u0002\u0004?\u0012\u001a\u0014c\u0001\u0016\u0002\u001cA!\u0011QDA\u0012\u001b\t\tyBC\u0002\u0002\"\r\u000b\u0001bY8naJ,7o]\u0005\u0005\u0003K\tyB\u0001\tD_6\u0004(/Z:tS>t7i\u001c3fG\"I\u0011\u0011\u0006\u0001\u0012\u0002\u0013\u0005\u00111F\u0001\u001dg\u00064X-Q:TKF,XM\\2f\r&dW\r\n3fM\u0006,H\u000e\u001e\u00133+\t\tiC\u000b\u0003\u00020\u0005e\u0002#\u0002\b\u0002\f\u0005E\u0002\u0007BA\u001a\u0003o\u0001Ba\u000e\u001e\u00026A\u0019a%a\u000e\u0005\u0019\u0005]\u0011qEA\u0001\u0002\u0003\u0015\t!!\u0007,\u0005\u0005m\u0002\u0003BA\u001f\u0003\u000fj!!a\u0010\u000b\t\u0005\u0005\u00131I\u0001\nk:\u001c\u0007.Z2lK\u0012T1!!\u0012\u0010\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003\u0013\nyDA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016\u0004"
)
public class SequenceFileRDDFunctions<K, V> implements Logging, Serializable {
   public final RDD<Tuple2<K, V>> org$apache$spark$rdd$SequenceFileRDDFunctions$$self;
   public final Class<? extends Writable> org$apache$spark$rdd$SequenceFileRDDFunctions$$_keyWritableClass;
   public final Class<? extends Writable> org$apache$spark$rdd$SequenceFileRDDFunctions$$_valueWritableClass;
   public final Function1<K, Writable> org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$1;
   public final ClassTag<K> org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$2;
   public final Function1<V, Writable> org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$3;
   public final ClassTag<V> org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$4;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   public void saveAsSequenceFile(String path, Option<Class<? extends CompressionCodec>> codec) {
      this.org$apache$spark$rdd$SequenceFileRDDFunctions$$self.withScope(new Serializable(this, path, codec) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SequenceFileRDDFunctions $outer;
         private final String path$1;
         private final Option codec$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            boolean var31;
            label58: {
               label57: {
                  RDD x$1 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$self;
                  ClassTag x$2 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$2;
                  ClassTag x$3 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$4;
                  RDD$.MODULE$.rddToPairRDDFunctions$default$4(x$1);
                  Null$ x$4 = null;
                  Class var10000 = RDD$.MODULE$.rddToPairRDDFunctions(x$1, x$2, x$3, (Ordering)null).keyClass();
                  Class var2 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_keyWritableClass;
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label57;
                     }
                  } else if (var10000.equals(var2)) {
                     break label57;
                  }

                  var31 = true;
                  break label58;
               }

               var31 = false;
            }

            boolean convertKey;
            label50: {
               label49: {
                  convertKey = var31;
                  RDD x$5 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$self;
                  ClassTag x$6 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$2;
                  ClassTag x$7 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$4;
                  RDD$.MODULE$.rddToPairRDDFunctions$default$4(x$5);
                  Null$ x$8 = null;
                  Class var32 = RDD$.MODULE$.rddToPairRDDFunctions(x$5, x$6, x$7, (Ordering)null).valueClass();
                  Class var8 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_valueWritableClass;
                  if (var32 == null) {
                     if (var8 == null) {
                        break label49;
                     }
                  } else if (var32.equals(var8)) {
                     break label49;
                  }

                  var31 = true;
                  break label50;
               }

               var31 = false;
            }

            boolean convertValue = var31;
            this.$outer.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Saving as sequence file of type ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", ",", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$$outer().org$apache$spark$rdd$SequenceFileRDDFunctions$$_keyWritableClass.getSimpleName(), this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$$outer().org$apache$spark$rdd$SequenceFileRDDFunctions$$_valueWritableClass.getSimpleName()}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            Class format = SequenceFileOutputFormat.class;
            JobConf jobConf = new JobConf(this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$self.context().hadoopConfiguration());
            if (!convertKey && !convertValue) {
               RDD x$9 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$self;
               ClassTag x$10 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$2;
               ClassTag x$11 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$4;
               RDD$.MODULE$.rddToPairRDDFunctions$default$4(x$9);
               Null$ x$12 = null;
               RDD$.MODULE$.rddToPairRDDFunctions(x$9, x$10, x$11, (Ordering)null).saveAsHadoopFile(this.path$1, this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_keyWritableClass, this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_valueWritableClass, format, jobConf, this.codec$1);
            } else if (!convertKey && convertValue) {
               RDD x$13 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$self.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final Tuple2<K, Writable> apply(Tuple2<K, V> x) {
                     return new Tuple2<K, Writable>(x._1(), this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$anyToWritable$1(x._2(), this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$$outer().org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$3));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, ClassTag$.MODULE$.apply(Tuple2.class));
               ClassTag x$14 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$2;
               ClassTag x$15 = ClassTag$.MODULE$.apply(Writable.class);
               RDD$.MODULE$.rddToPairRDDFunctions$default$4(x$13);
               Null$ x$16 = null;
               RDD$.MODULE$.rddToPairRDDFunctions(x$13, x$14, x$15, (Ordering)null).saveAsHadoopFile(this.path$1, this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_keyWritableClass, this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_valueWritableClass, format, jobConf, this.codec$1);
            } else if (convertKey && !convertValue) {
               RDD x$17 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$self.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final Tuple2<Writable, V> apply(Tuple2<K, V> x) {
                     return new Tuple2<Writable, V>(this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$anyToWritable$1(x._1(), this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$$outer().org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$1), x._2());
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, ClassTag$.MODULE$.apply(Tuple2.class));
               ClassTag x$18 = ClassTag$.MODULE$.apply(Writable.class);
               ClassTag x$19 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$4;
               RDD$.MODULE$.rddToPairRDDFunctions$default$4(x$17);
               Null$ x$20 = null;
               RDD$.MODULE$.rddToPairRDDFunctions(x$17, x$18, x$19, (Ordering)null).saveAsHadoopFile(this.path$1, this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_keyWritableClass, this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_valueWritableClass, format, jobConf, this.codec$1);
            } else if (convertKey && convertValue) {
               RDD x$21 = this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$self.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final Tuple2<Writable, Writable> apply(Tuple2<K, V> x) {
                     return new Tuple2<Writable, Writable>(this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$anyToWritable$1(x._1(), this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$$outer().org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$1), this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$anyToWritable$1(x._2(), this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$$outer().org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$3));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, ClassTag$.MODULE$.apply(Tuple2.class));
               ClassTag x$22 = ClassTag$.MODULE$.apply(Writable.class);
               ClassTag x$23 = ClassTag$.MODULE$.apply(Writable.class);
               RDD$.MODULE$.rddToPairRDDFunctions$default$4(x$21);
               Null$ x$24 = null;
               RDD$.MODULE$.rddToPairRDDFunctions(x$21, x$22, x$23, (Ordering)null).saveAsHadoopFile(this.path$1, this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_keyWritableClass, this.$outer.org$apache$spark$rdd$SequenceFileRDDFunctions$$_valueWritableClass, format, jobConf, this.codec$1);
            }

         }

         // $FF: synthetic method
         public SequenceFileRDDFunctions org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$$outer() {
            return this.$outer;
         }

         public final Writable org$apache$spark$rdd$SequenceFileRDDFunctions$$anonfun$$anyToWritable$1(Object u, Function1 evidence$5) {
            return (Writable)evidence$5.apply(u);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$1 = path$1;
               this.codec$1 = codec$1;
            }
         }
      });
   }

   public Option<Class<? extends CompressionCodec>> saveAsSequenceFile$default$2() {
      return None$.MODULE$;
   }

   public SequenceFileRDDFunctions(RDD<Tuple2<K, V>> self, Class<? extends Writable> _keyWritableClass, Class<? extends Writable> _valueWritableClass, Function1<K, Writable> evidence$1, ClassTag<K> evidence$2, Function1<V, Writable> evidence$3, ClassTag<V> evidence$4) {
      this.org$apache$spark$rdd$SequenceFileRDDFunctions$$self = self;
      this.org$apache$spark$rdd$SequenceFileRDDFunctions$$_keyWritableClass = _keyWritableClass;
      this.org$apache$spark$rdd$SequenceFileRDDFunctions$$_valueWritableClass = _valueWritableClass;
      this.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$1 = evidence$1;
      this.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$2 = evidence$2;
      this.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$3 = evidence$3;
      this.org$apache$spark$rdd$SequenceFileRDDFunctions$$evidence$4 = evidence$4;
      Logging$class.$init$(this);
   }
}
