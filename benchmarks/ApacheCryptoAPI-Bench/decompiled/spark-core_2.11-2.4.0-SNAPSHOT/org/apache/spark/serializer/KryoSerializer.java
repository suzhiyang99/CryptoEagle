package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.twitter.chill.AllScalaRegistrar;
import com.twitter.chill.EmptyScalaKryoInstantiator;
import com.twitter.chill.KryoBase;
import java.io.Serializable;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.spark.SerializableWritable;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.api.python.PythonBroadcast;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.util.ByteUnit;
import org.apache.spark.util.SerializableConfiguration;
import org.apache.spark.util.SerializableJobConf;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.StringContext;
import scala.Tuple1;
import scala.Tuple10;
import scala.Tuple11;
import scala.Tuple12;
import scala.Tuple13;
import scala.Tuple14;
import scala.Tuple15;
import scala.Tuple16;
import scala.Tuple17;
import scala.Tuple18;
import scala.Tuple19;
import scala.Tuple2;
import scala.Tuple20;
import scala.Tuple21;
import scala.Tuple22;
import scala.Tuple3;
import scala.Tuple4;
import scala.Tuple5;
import scala.Tuple6;
import scala.Tuple7;
import scala.Tuple8;
import scala.Tuple9;
import scala.collection.IterableLike;
import scala.collection.Seq$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015g\u0001B\u0001\u0003\u0001-\u0011ab\u0013:z_N+'/[1mSj,'O\u0003\u0002\u0004\t\u0005Q1/\u001a:jC2L'0\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001\u0019A1\u0002CA\u0007\u000f\u001b\u0005\u0011\u0011BA\b\u0003\u0005)\u0019VM]5bY&TXM\u001d\t\u0003#Qi\u0011A\u0005\u0006\u0003'\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003+I\u0011q\u0001T8hO&tw\r\u0005\u0002\u001895\t\u0001D\u0003\u0002\u001a5\u0005\u0011\u0011n\u001c\u0006\u00027\u0005!!.\u0019<b\u0013\ti\u0002D\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005 \u0001\t\u0005\t\u0015!\u0003!\u0003\u0011\u0019wN\u001c4\u0011\u0005\u0005\u0012S\"\u0001\u0003\n\u0005\r\"!!C*qCJ\\7i\u001c8g\u0011\u0015)\u0003\u0001\"\u0001'\u0003\u0019a\u0014N\\5u}Q\u0011q\u0005\u000b\t\u0003\u001b\u0001AQa\b\u0013A\u0002\u0001BqA\u000b\u0001C\u0002\u0013%1&\u0001\u0007ck\u001a4WM]*ju\u0016\\%-F\u0001-!\ti\u0003'D\u0001/\u0015\u0005y\u0013!B:dC2\f\u0017BA\u0019/\u0005\u0011auN\\4\t\rM\u0002\u0001\u0015!\u0003-\u00035\u0011WO\u001a4feNK'0Z&cA!9Q\u0007\u0001b\u0001\n\u00131\u0014A\u00032vM\u001a,'oU5{KV\tq\u0007\u0005\u0002.q%\u0011\u0011H\f\u0002\u0004\u0013:$\bBB\u001e\u0001A\u0003%q'A\u0006ck\u001a4WM]*ju\u0016\u0004\u0003bB\u001f\u0001\u0005\u0004%\tAN\u0001\u0010[\u0006D()\u001e4gKJ\u001c\u0016N_3NE\"1q\b\u0001Q\u0001\n]\n\u0001#\\1y\u0005V4g-\u001a:TSj,WJ\u0019\u0011\t\u000f\u0005\u0003!\u0019!C\u0005m\u0005iQ.\u0019=Ck\u001a4WM]*ju\u0016Daa\u0011\u0001!\u0002\u00139\u0014AD7bq\n+hMZ3s'&TX\r\t\u0005\b\u000b\u0002\u0011\r\u0011\"\u0003G\u0003E\u0011XMZ3sK:\u001cW\r\u0016:bG.LgnZ\u000b\u0002\u000fB\u0011Q\u0006S\u0005\u0003\u0013:\u0012qAQ8pY\u0016\fg\u000e\u0003\u0004L\u0001\u0001\u0006IaR\u0001\u0013e\u00164WM]3oG\u0016$&/Y2lS:<\u0007\u0005C\u0004N\u0001\t\u0007I\u0011\u0002$\u0002)I,w-[:ue\u0006$\u0018n\u001c8SKF,\u0018N]3e\u0011\u0019y\u0005\u0001)A\u0005\u000f\u0006)\"/Z4jgR\u0014\u0018\r^5p]J+\u0017/^5sK\u0012\u0004\u0003bB)\u0001\u0005\u0004%IAU\u0001\u0011kN,'OU3hSN$(/\u0019;peN,\u0012a\u0015\t\u0004[Q3\u0016BA+/\u0005\u0015\t%O]1z!\t9&,D\u0001Y\u0015\tI&$\u0001\u0003mC:<\u0017BA.Y\u0005\u0019\u0019FO]5oO\"1Q\f\u0001Q\u0001\nM\u000b\u0011#^:feJ+w-[:ue\u0006$xN]:!\u0011\u001dy\u0006A1A\u0005\nI\u000b\u0011c\u00197bgN,7\u000fV8SK\u001eL7\u000f^3s\u0011\u0019\t\u0007\u0001)A\u0005'\u0006\u00112\r\\1tg\u0016\u001cHk\u001c*fO&\u001cH/\u001a:!\u0011\u001d\u0019\u0007A1A\u0005\n\u0011\f1\"\u0019<s_N\u001b\u0007.Z7bgV\tQ\r\u0005\u0003gS2bgBA\u0017h\u0013\tAg&\u0001\u0004Qe\u0016$WMZ\u0005\u0003U.\u00141!T1q\u0015\tAg\u0006\u0005\u0002g[&\u00111l\u001b\u0005\u0007_\u0002\u0001\u000b\u0011B3\u0002\u0019\u00054(o\\*dQ\u0016l\u0017m\u001d\u0011\t\u000fE\u0004!\u0019!C\u0005\r\u0006IQo]3V]N\fg-\u001a\u0005\u0007g\u0002\u0001\u000b\u0011B$\u0002\u0015U\u001cX-\u00168tC\u001a,\u0007\u0005C\u0003v\u0001\u0011\u0005a/A\u0007oK^\\%/_8PkR\u0004X\u000f\u001e\u000b\u0002oB\u0019\u00010!\u0001\u000e\u0003eT!!\u0007>\u000b\u0005md\u0018\u0001B6ss>T!! @\u0002!\u0015\u001cx\u000e^3sS\u000e\u001cxN\u001a;xCJ,'\"A@\u0002\u0007\r|W.C\u0002\u0002\u0004e\u0014aaT;uaV$\bbBA\u0004\u0001\u0011\u0005\u0011\u0011B\u0001\b]\u0016<8J]=p)\t\tY\u0001\u0005\u0003\u0002\u000e\u0005=Q\"\u0001>\n\u0007\u0005E!P\u0001\u0003Lef|\u0007bBA\u000b\u0001\u0011\u0005\u0013qC\u0001\f]\u0016<\u0018J\\:uC:\u001cW\r\u0006\u0002\u0002\u001aA\u0019Q\"a\u0007\n\u0007\u0005u!A\u0001\nTKJL\u0017\r\\5{KJLen\u001d;b]\u000e,\u0007BCA\u0011\u0001!\u0015\r\u0011\"\u0011\u0005\r\u0006)3/\u001e9q_J$8OU3m_\u000e\fG/[8o\u001f\u001a\u001cVM]5bY&TX\rZ(cU\u0016\u001cGo\u001d\u0005\n\u0003K\u0001\u0001\u0012!Q!\n\u001d\u000bae];qa>\u0014Ho\u001d*fY>\u001c\u0017\r^5p]>37+\u001a:jC2L'0\u001a3PE*,7\r^:!\u000f!\tIC\u0001E\u0001\u0005\u0005-\u0012AD&ss>\u001cVM]5bY&TXM\u001d\t\u0004\u001b\u00055baB\u0001\u0003\u0011\u0003\u0011\u0011qF\n\u0007\u0003[\t\t$a\u000e\u0011\u00075\n\u0019$C\u0002\u000269\u0012a!\u00118z%\u00164\u0007cA\u0017\u0002:%\u0011QD\f\u0005\bK\u00055B\u0011AA\u001f)\t\tY\u0003\u0003\u0006\u0002B\u00055\"\u0019!C\u0005\u0003\u0007\n!\u0002^8SK\u001eL7\u000f^3s+\t\t)\u0005\u0005\u0004\u0002H\u0005]\u0013Q\f\b\u0005\u0003\u0013\n\u0019F\u0004\u0003\u0002L\u0005ESBAA'\u0015\r\tyEC\u0001\u0007yI|w\u000e\u001e \n\u0003=J1!!\u0016/\u0003\u001d\u0001\u0018mY6bO\u0016LA!!\u0017\u0002\\\t\u00191+Z9\u000b\u0007\u0005Uc\u0006\r\u0003\u0002`\u0005%\u0004#\u00024\u0002b\u0005\u0015\u0014bAA2W\n)1\t\\1tgB!\u0011qMA5\u0019\u0001!A\"a\u001b\u0002n\u0005\u0005\t\u0011!B\u0001\u0003w\u00121a\u0018\u00133\u0011%\ty'!\f!\u0002\u0013\t\t(A\u0006u_J+w-[:uKJ\u0004\u0003CBA$\u0003/\n\u0019\b\r\u0003\u0002v\u0005e\u0004#\u00024\u0002b\u0005]\u0004\u0003BA4\u0003s\"A\"a\u001b\u0002n\u0005\u0005\t\u0011!B\u0001\u0003w\nB!! \u0002\u0004B\u0019Q&a \n\u0007\u0005\u0005eFA\u0004O_RD\u0017N\\4\u0011\u00075\n))C\u0002\u0002\b:\u00121!\u00118z\u0011)\tY)!\fC\u0002\u0013%\u0011QR\u0001\u0015i>\u0014VmZ5ti\u0016\u00148+\u001a:jC2L'0\u001a:\u0016\u0005\u0005=\u0005\u0003CAI\u00037\u000bi*!,\u000e\u0005\u0005M%\u0002BAK\u0003/\u000b\u0011\"[7nkR\f'\r\\3\u000b\u0007\u0005ee&\u0001\u0006d_2dWm\u0019;j_:L1A[AJa\u0011\ty*!*\u0011\u000b]\u000b\t+a)\n\u0007\u0005\r\u0004\f\u0005\u0003\u0002h\u0005\u0015F\u0001DAT\u0003S\u000b\t\u0011!A\u0003\u0002\u0005m$aA0%k!I\u00111VA\u0017A\u0003%\u0011qR\u0001\u0016i>\u0014VmZ5ti\u0016\u00148+\u001a:jC2L'0\u001a:!a\u0011\ty+!.\u0011\r\u00055\u0011\u0011WAZ\u0013\ty!\u0010\u0005\u0003\u0002h\u0005UF\u0001DA\\\u0003S\u000b\t\u0011!A\u0003\u0002\u0005m$aA0%m!Q\u00111XA\u0017\u0003\u0003%I!!0\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003\u007f\u00032aVAa\u0013\r\t\u0019\r\u0017\u0002\u0007\u001f\nTWm\u0019;"
)
public class KryoSerializer extends Serializer implements Logging, Serializable {
   private final long bufferSizeKb;
   private final int bufferSize;
   private final int maxBufferSizeMb;
   private final int maxBufferSize;
   private final boolean referenceTracking;
   private final boolean registrationRequired;
   private final String[] userRegistrators;
   private final String[] classesToRegister;
   private final Map<Object, String> avroSchemas;
   private final boolean useUnsafe;
   private boolean supportsRelocationOfSerializedObjects;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private volatile boolean bitmap$0;

   private boolean supportsRelocationOfSerializedObjects$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.supportsRelocationOfSerializedObjects = ((KryoSerializerInstance)this.newInstance()).getAutoReset();
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.supportsRelocationOfSerializedObjects;
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

   private long bufferSizeKb() {
      return this.bufferSizeKb;
   }

   private int bufferSize() {
      return this.bufferSize;
   }

   public int maxBufferSizeMb() {
      return this.maxBufferSizeMb;
   }

   private int maxBufferSize() {
      return this.maxBufferSize;
   }

   private boolean referenceTracking() {
      return this.referenceTracking;
   }

   private boolean registrationRequired() {
      return this.registrationRequired;
   }

   private String[] userRegistrators() {
      return this.userRegistrators;
   }

   private String[] classesToRegister() {
      return this.classesToRegister;
   }

   private Map<Object, String> avroSchemas() {
      return this.avroSchemas;
   }

   private boolean useUnsafe() {
      return this.useUnsafe;
   }

   public Output newKryoOutput() {
      return (Output)(this.useUnsafe() ? new UnsafeOutput(this.bufferSize(), scala.math.package$.MODULE$.max(this.bufferSize(), this.maxBufferSize())) : new Output(this.bufferSize(), scala.math.package$.MODULE$.max(this.bufferSize(), this.maxBufferSize())));
   }

   public Kryo newKryo() {
      EmptyScalaKryoInstantiator instantiator = new EmptyScalaKryoInstantiator();
      KryoBase kryo = instantiator.newKryo();
      kryo.setRegistrationRequired(this.registrationRequired());
      ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
      ClassLoader classLoader = (ClassLoader)this.defaultClassLoader().getOrElse(new scala.Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ClassLoader apply() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
      kryo.setReferences(this.referenceTracking());
      KryoSerializer$.MODULE$.org$apache$spark$serializer$KryoSerializer$$toRegister().foreach(new scala.Serializable(this, kryo) {
         public static final long serialVersionUID = 0L;
         private final KryoBase kryo$1;

         public final Registration apply(Class<?> cls) {
            return this.kryo$1.register(cls);
         }

         public {
            this.kryo$1 = kryo$1;
         }
      });
      KryoSerializer$.MODULE$.org$apache$spark$serializer$KryoSerializer$$toRegisterSerializer().withFilter(new scala.Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<Class<?>, com.esotericsoftware.kryo.Serializer<?>> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new scala.Serializable(this, kryo) {
         public static final long serialVersionUID = 0L;
         private final KryoBase kryo$1;

         public final Registration apply(Tuple2<Class<?>, com.esotericsoftware.kryo.Serializer<?>> x$5) {
            if (x$5 != null) {
               Class cls = (Class)x$5._1();
               com.esotericsoftware.kryo.Serializer ser = (com.esotericsoftware.kryo.Serializer)x$5._2();
               Registration var5 = this.kryo$1.register(cls, ser);
               return var5;
            } else {
               throw new MatchError(x$5);
            }
         }

         public {
            this.kryo$1 = kryo$1;
         }
      });
      kryo.register(JavaIterableWrapperSerializer$.MODULE$.wrapperClass(), new JavaIterableWrapperSerializer());
      kryo.register(SerializableWritable.class, new com.esotericsoftware.kryo.serializers.JavaSerializer());
      kryo.register(SerializableConfiguration.class, new com.esotericsoftware.kryo.serializers.JavaSerializer());
      kryo.register(SerializableJobConf.class, new com.esotericsoftware.kryo.serializers.JavaSerializer());
      kryo.register(PythonBroadcast.class, new com.esotericsoftware.kryo.serializers.JavaSerializer());
      kryo.register(GenericRecord.class, new GenericAvroSerializer(this.avroSchemas()));
      kryo.register(GenericData.Record.class, new GenericAvroSerializer(this.avroSchemas()));

      try {
         Thread.currentThread().setContextClassLoader(classLoader);
         Predef$.MODULE$.refArrayOps((Object[])this.classesToRegister()).foreach(new scala.Serializable(this, kryo, classLoader) {
            public static final long serialVersionUID = 0L;
            private final KryoBase kryo$1;
            private final ClassLoader classLoader$1;

            public final Registration apply(String className) {
               return this.kryo$1.register(Class.forName(className, true, this.classLoader$1));
            }

            public {
               this.kryo$1 = kryo$1;
               this.classLoader$1 = classLoader$1;
            }
         });
         Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])this.userRegistrators()).map(new scala.Serializable(this, classLoader) {
            public static final long serialVersionUID = 0L;
            private final ClassLoader classLoader$1;

            public final KryoRegistrator apply(String x$6) {
               return (KryoRegistrator)Class.forName(x$6, true, this.classLoader$1).newInstance();
            }

            public {
               this.classLoader$1 = classLoader$1;
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(KryoRegistrator.class)))).foreach(new scala.Serializable(this, kryo) {
            public static final long serialVersionUID = 0L;
            private final KryoBase kryo$1;

            public final void apply(KryoRegistrator reg) {
               reg.registerClasses(this.kryo$1);
            }

            public {
               this.kryo$1 = kryo$1;
            }
         });
      } catch (Exception var9) {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to register classes with Kryo"})))).s(Nil$.MODULE$), var9);
      } finally {
         Thread.currentThread().setContextClassLoader(oldClassLoader);
      }

      (new AllScalaRegistrar()).apply(kryo);
      kryo.register(Tuple1[].class);
      kryo.register(Tuple2[].class);
      kryo.register(Tuple3[].class);
      kryo.register(Tuple4[].class);
      kryo.register(Tuple5[].class);
      kryo.register(Tuple6[].class);
      kryo.register(Tuple7[].class);
      kryo.register(Tuple8[].class);
      kryo.register(Tuple9[].class);
      kryo.register(Tuple10[].class);
      kryo.register(Tuple11[].class);
      kryo.register(Tuple12[].class);
      kryo.register(Tuple13[].class);
      kryo.register(Tuple14[].class);
      kryo.register(Tuple15[].class);
      kryo.register(Tuple16[].class);
      kryo.register(Tuple17[].class);
      kryo.register(Tuple18[].class);
      kryo.register(Tuple19[].class);
      kryo.register(Tuple20[].class);
      kryo.register(Tuple21[].class);
      kryo.register(Tuple22[].class);
      kryo.register(None$.MODULE$.getClass());
      kryo.register(Nil$.MODULE$.getClass());
      kryo.register(Utils$.MODULE$.classForName("scala.collection.immutable.$colon$colon"));
      kryo.register(Utils$.MODULE$.classForName("scala.collection.immutable.Map$EmptyMap$"));
      kryo.register(ArrayBuffer.class);
      ((IterableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"org.apache.spark.ml.feature.Instance", "org.apache.spark.ml.feature.LabeledPoint", "org.apache.spark.ml.feature.OffsetInstance", "org.apache.spark.ml.linalg.DenseMatrix", "org.apache.spark.ml.linalg.DenseVector", "org.apache.spark.ml.linalg.Matrix", "org.apache.spark.ml.linalg.SparseMatrix", "org.apache.spark.ml.linalg.SparseVector", "org.apache.spark.ml.linalg.Vector", "org.apache.spark.ml.tree.impl.TreePoint", "org.apache.spark.mllib.clustering.VectorWithNorm", "org.apache.spark.mllib.linalg.DenseMatrix", "org.apache.spark.mllib.linalg.DenseVector", "org.apache.spark.mllib.linalg.Matrix", "org.apache.spark.mllib.linalg.SparseMatrix", "org.apache.spark.mllib.linalg.SparseVector", "org.apache.spark.mllib.linalg.Vector", "org.apache.spark.mllib.regression.LabeledPoint"})))).foreach(new scala.Serializable(this, kryo) {
         public static final long serialVersionUID = 0L;
         private final KryoBase kryo$1;

         public final Object apply(String name) {
            Object var10000;
            try {
               Class clazz = Utils$.MODULE$.classForName(name);
               var10000 = this.kryo$1.register(clazz);
            } catch (Throwable var8) {
               Option var4 = NonFatal$.MODULE$.unapply(var8);
               if (var4.isEmpty()) {
                  throw var8;
               }

               BoxedUnit var5 = BoxedUnit.UNIT;
               var10000 = var5;
               return var10000;
            }

            return var10000;
         }

         public {
            this.kryo$1 = kryo$1;
         }
      });
      kryo.setClassLoader(classLoader);
      return kryo;
   }

   public SerializerInstance newInstance() {
      return new KryoSerializerInstance(this, this.useUnsafe());
   }

   public boolean supportsRelocationOfSerializedObjects() {
      return this.bitmap$0 ? this.supportsRelocationOfSerializedObjects : this.supportsRelocationOfSerializedObjects$lzycompute();
   }

   public KryoSerializer(SparkConf conf) {
      Logging$class.$init$(this);
      this.bufferSizeKb = conf.getSizeAsKb("spark.kryoserializer.buffer", "64k");
      if (this.bufferSizeKb() >= ByteUnit.GiB.toKiB(2L)) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)"spark.kryoserializer.buffer must be less than ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"2048 mb, got: + ", " mb."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(ByteUnit.KiB.toMiB(this.bufferSizeKb()))}))).toString());
      } else {
         this.bufferSize = (int)ByteUnit.KiB.toBytes(this.bufferSizeKb());
         this.maxBufferSizeMb = (int)conf.getSizeAsMb("spark.kryoserializer.buffer.max", "64m");
         if ((long)this.maxBufferSizeMb() >= ByteUnit.GiB.toMiB(2L)) {
            throw new IllegalArgumentException((new StringBuilder()).append((Object)"spark.kryoserializer.buffer.max must be less than ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"2048 mb, got: + ", " mb."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.maxBufferSizeMb())}))).toString());
         } else {
            this.maxBufferSize = (int)ByteUnit.MiB.toBytes((long)this.maxBufferSizeMb());
            this.referenceTracking = conf.getBoolean("spark.kryo.referenceTracking", true);
            this.registrationRequired = conf.getBoolean("spark.kryo.registrationRequired", false);
            this.userRegistrators = (String[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(conf.get("spark.kryo.registrator", "")))).split(',')).map(new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String x$1) {
                  return x$1.trim();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).filter(new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(String x$2) {
                  return !x$2.isEmpty();
               }
            });
            this.classesToRegister = (String[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(conf.get("spark.kryo.classesToRegister", "")))).split(',')).map(new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String x$3) {
                  return x$3.trim();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).filter(new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(String x$4) {
                  return !x$4.isEmpty();
               }
            });
            this.avroSchemas = conf.getAvroSchema();
            this.useUnsafe = conf.getBoolean("spark.kryo.unsafe", false);
         }
      }
   }
}
