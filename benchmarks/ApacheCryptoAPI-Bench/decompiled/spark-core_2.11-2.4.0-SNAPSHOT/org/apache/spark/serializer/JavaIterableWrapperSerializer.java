package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.collection.JavaConverters$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ub\u0001B\u0001\u0003\t-\u0011QDS1wC&#XM]1cY\u0016<&/\u00199qKJ\u001cVM]5bY&TXM\u001d\u0006\u0003\u0007\u0011\t!b]3sS\u0006d\u0017N_3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0003\u00011\u00012!\u0004\u000b\u0017\u001b\u0005q!BA\b\u0011\u0003\u0011Y'/_8\u000b\u0005E\u0011\u0012\u0001E3t_R,'/[2t_\u001a$x/\u0019:f\u0015\u0005\u0019\u0012aA2p[&\u0011QC\u0004\u0002\u000b'\u0016\u0014\u0018.\u00197ju\u0016\u0014\bGA\f\"!\rARdH\u0007\u00023)\u0011!dG\u0001\u0005Y\u0006twMC\u0001\u001d\u0003\u0011Q\u0017M^1\n\u0005yI\"\u0001C%uKJ\f'\r\\3\u0011\u0005\u0001\nC\u0002\u0001\u0003\nE\u0001\t\t\u0011!A\u0003\u0002\r\u00121a\u0018\u00138#\t!#\u0006\u0005\u0002&Q5\taEC\u0001(\u0003\u0015\u00198-\u00197b\u0013\tIcEA\u0004O_RD\u0017N\\4\u0011\u0005\u0015Z\u0013B\u0001\u0017'\u0005\r\te.\u001f\u0005\u0006]\u0001!\taL\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003A\u0002\"!\r\u0001\u000e\u0003\tAQa\r\u0001\u0005BQ\nQa\u001e:ji\u0016$B!\u000e\u001d=\tB\u0011QEN\u0005\u0003o\u0019\u0012A!\u00168ji\")qB\ra\u0001sA\u0011QBO\u0005\u0003w9\u0011Aa\u0013:z_\")QH\ra\u0001}\u0005\u0019q.\u001e;\u0011\u0005}\u0012U\"\u0001!\u000b\u0005\u0005s\u0011AA5p\u0013\t\u0019\u0005I\u0001\u0004PkR\u0004X\u000f\u001e\u0005\u0006\u000bJ\u0002\rAR\u0001\u0004_\nT\u0007GA$J!\rAR\u0004\u0013\t\u0003A%#\u0011B\u0013#\u0002\u0002\u0003\u0005)\u0011A\u0012\u0003\u0007}#\u0003\bC\u0003M\u0001\u0011\u0005S*\u0001\u0003sK\u0006$G\u0003\u0002(T)f\u0003$aT)\u0011\u0007ai\u0002\u000b\u0005\u0002!#\u0012I!kSA\u0001\u0002\u0003\u0015\ta\t\u0002\u0005?\u0012\n\u0004\u0007C\u0003\u0010\u0017\u0002\u0007\u0011\bC\u0003V\u0017\u0002\u0007a+\u0001\u0002j]B\u0011qhV\u0005\u00031\u0002\u0013Q!\u00138qkRDQAW&A\u0002m\u000b1a\u00197{!\ravL\u0019\b\u0003KuK!A\u0018\u0014\u0002\rA\u0013X\rZ3g\u0013\t\u0001\u0017MA\u0003DY\u0006\u001c8O\u0003\u0002_MA\u00121-\u001a\t\u00041u!\u0007C\u0001\u0011f\t%1\u0017,!A\u0001\u0002\u000b\u00051EA\u0002`Ie:Q\u0001\u001b\u0002\t\n%\fQDS1wC&#XM]1cY\u0016<&/\u00199qKJ\u001cVM]5bY&TXM\u001d\t\u0003c)4Q!\u0001\u0002\t\n-\u001c2A\u001b7p!\t)S.\u0003\u0002oM\t1\u0011I\\=SK\u001a\u0004\"\u0001]:\u000e\u0003ET!A\u001d\u0003\u0002\u0011%tG/\u001a:oC2L!\u0001^9\u0003\u000f1{wmZ5oO\")aF\u001bC\u0001mR\t\u0011\u000eC\u0004yU\n\u0007I\u0011A=\u0002\u0019]\u0014\u0018\r\u001d9fe\u000ec\u0017m]:\u0016\u0003i\u0004$a\u001f@\u0011\u0007aaX0\u0003\u0002a3A\u0011\u0001E \u0003\f\u007f\u0006\u0005\u0011\u0011!A\u0001\u0006\u0003\t)A\u0001\u0002@a!9\u00111\u00016!\u0002\u0013Q\u0018!D<sCB\u0004XM]\"mCN\u001c\b%E\u0002%\u0003\u000f\u0001b!!\u0003\u0002\u0010\u0005MQBAA\u0006\u0015\r\tiaG\u0001\u0005kRLG.\u0003\u0003\u0002\u0012\u0005-!\u0001\u0002'jgR\u00042!JA\u000b\u0013\r\t9B\n\u0002\u0004\u0013:$\b\"CA\u000eU\n\u0007I\u0011BA\u000f\u0003M)h\u000eZ3sYfLgnZ'fi\"|Gm\u00149u+\t\ty\u0002E\u0003&\u0003C\t)#C\u0002\u0002$\u0019\u0012aa\u00149uS>t\u0007\u0003BA\u0014\u0003[i!!!\u000b\u000b\u0007\u0005-\u0012$A\u0004sK\u001adWm\u0019;\n\t\u0005=\u0012\u0011\u0006\u0002\u0007\u001b\u0016$\bn\u001c3\t\u0011\u0005M\"\u000e)A\u0005\u0003?\tA#\u001e8eKJd\u00170\u001b8h\u001b\u0016$\bn\u001c3PaR\u0004\u0003"
)
public class JavaIterableWrapperSerializer extends com.esotericsoftware.kryo.Serializer<Iterable<?>> {
   public static boolean initializeLogIfNecessary$default$2() {
      return JavaIterableWrapperSerializer$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return JavaIterableWrapperSerializer$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      JavaIterableWrapperSerializer$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return JavaIterableWrapperSerializer$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      JavaIterableWrapperSerializer$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      JavaIterableWrapperSerializer$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      JavaIterableWrapperSerializer$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      JavaIterableWrapperSerializer$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      JavaIterableWrapperSerializer$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      JavaIterableWrapperSerializer$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      JavaIterableWrapperSerializer$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      JavaIterableWrapperSerializer$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      JavaIterableWrapperSerializer$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      JavaIterableWrapperSerializer$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return JavaIterableWrapperSerializer$.MODULE$.log();
   }

   public static String logName() {
      return JavaIterableWrapperSerializer$.MODULE$.logName();
   }

   public static Class<? extends List<Object>> wrapperClass() {
      return JavaIterableWrapperSerializer$.MODULE$.wrapperClass();
   }

   public void write(Kryo kryo, Output out, Iterable<?> obj) {
      label18: {
         Class var10000 = obj.getClass();
         Class var4 = JavaIterableWrapperSerializer$.MODULE$.wrapperClass();
         if (var10000 == null) {
            if (var4 != null) {
               break label18;
            }
         } else if (!var10000.equals(var4)) {
            break label18;
         }

         if (JavaIterableWrapperSerializer$.MODULE$.org$apache$spark$serializer$JavaIterableWrapperSerializer$$underlyingMethodOpt().isDefined()) {
            kryo.writeClassAndObject(out, ((Method)JavaIterableWrapperSerializer$.MODULE$.org$apache$spark$serializer$JavaIterableWrapperSerializer$$underlyingMethodOpt().get()).invoke(obj));
            return;
         }
      }

      kryo.writeClassAndObject(out, obj);
   }

   public Iterable<?> read(Kryo kryo, Input in, Class<Iterable<?>> clz) {
      Object var4 = kryo.readClassAndObject(in);
      Iterable var6;
      if (var4 instanceof scala.collection.Iterable) {
         scala.collection.Iterable var5 = (scala.collection.Iterable)var4;
         var6 = (Iterable)JavaConverters$.MODULE$.asJavaIterableConverter(var5).asJava();
      } else {
         if (!(var4 instanceof Iterable)) {
            throw new MatchError(var4);
         }

         Iterable var7 = (Iterable)var4;
         var6 = var7;
      }

      return var6;
   }
}
