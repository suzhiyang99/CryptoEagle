package org.apache.spark.api.python;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import net.razorvine.pickle.Pickler;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Tuple2;
import scala.collection.BufferedIterator;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005uxAB\u0001\u0003\u0011\u00031A\"A\u0005TKJ$U-\u0016;jY*\u00111\u0001B\u0001\u0007af$\bn\u001c8\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x\r\u0005\u0002\u000e\u001d5\t!A\u0002\u0004\u0010\u0005!\u0005a\u0001\u0005\u0002\n'\u0016\u0014H)Z+uS2\u001c2AD\t\u0018!\t\u0011R#D\u0001\u0014\u0015\u0005!\u0012!B:dC2\f\u0017B\u0001\f\u0014\u0005\u0019\te.\u001f*fMB\u0011\u0001dG\u0007\u00023)\u0011!DB\u0001\tS:$XM\u001d8bY&\u0011A$\u0007\u0002\b\u0019><w-\u001b8h\u0011\u0015qb\u0002\"\u0001!\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0007\u0007\t\tr\u0001a\t\u0002\u0015\u0005f$X-\u0011:sCf\u001cuN\\:ueV\u001cGo\u001c:\u0014\u0005\u0005\"\u0003CA\u0013/\u001b\u00051#BA\u0014)\u0003\u001dy'M[3diNT!!\u000b\u0016\u0002\rAL7m\u001b7f\u0015\tYC&A\u0005sCj|'O^5oK*\tQ&A\u0002oKRL!A\t\u0014\t\u000by\tC\u0011\u0001\u0019\u0015\u0003E\u0002\"AM\u0011\u000e\u00039AQ\u0001N\u0011\u0005BU\n\u0011bY8ogR\u0014Xo\u0019;\u0015\u0005Yr\u0004CA\u001c=\u001b\u0005A$BA\u001d;\u0003\u0011a\u0017M\\4\u000b\u0003m\nAA[1wC&\u0011Q\b\u000f\u0002\u0007\u001f\nTWm\u0019;\t\u000b}\u001a\u0004\u0019\u0001!\u0002\t\u0005\u0014xm\u001d\t\u0004%\u00053\u0014B\u0001\"\u0014\u0005\u0015\t%O]1z\r\u0011!e\u0002A#\u0003!\u0005\u0013(/Y=D_:\u001cHO];di>\u00148CA\"G!\t)s)\u0003\u0002EM!)ad\u0011C\u0001\u0013R\t!\n\u0005\u00023\u0007\"9Aj\u0011b\u0001\n\u0003i\u0015\u0001D7bG\"Lg.Z\"pI\u0016\u001cX#\u0001(\u0011\t=\u0013V\u000b\u0017\b\u0003%AK!!U\n\u0002\rA\u0013X\rZ3g\u0013\t\u0019FKA\u0002NCBT!!U\n\u0011\u0005I1\u0016BA,\u0014\u0005\u0011\u0019\u0005.\u0019:\u0011\u0005II\u0016B\u0001.\u0014\u0005\rIe\u000e\u001e\u0005\u00079\u000e\u0003\u000b\u0011\u0002(\u0002\u001b5\f7\r[5oK\u000e{G-Z:!\u0011\u0015!4\t\"\u0011_)\t1t\fC\u0003@;\u0002\u0007\u0001\tC\u0004b\u001d\u0001\u0007I\u0011\u00022\u0002\u0017%t\u0017\u000e^5bY&TX\rZ\u000b\u0002GB\u0011!\u0003Z\u0005\u0003KN\u0011qAQ8pY\u0016\fg\u000eC\u0004h\u001d\u0001\u0007I\u0011\u00025\u0002\u001f%t\u0017\u000e^5bY&TX\rZ0%KF$\"!\u001b7\u0011\u0005IQ\u0017BA6\u0014\u0005\u0011)f.\u001b;\t\u000f54\u0017\u0011!a\u0001G\u0006\u0019\u0001\u0010J\u0019\t\r=t\u0001\u0015)\u0003d\u00031Ig.\u001b;jC2L'0\u001a3!\u0011\u0015\th\u0002\"\u0001s\u0003)Ig.\u001b;jC2L'0\u001a\u000b\u0002S\")AO\u0004C\u0001k\u0006YAo\u001c&bm\u0006\f%O]1z)\r1\u0018\u0011\u0003\t\u0004of\\X\"\u0001=\u000b\u0005m\"\u0011B\u0001>y\u0005\u001dQ\u0015M^1S\t\u0012\u0003$\u0001`@\u0011\u0007I\tU\u0010\u0005\u0002\u007f\u007f2\u0001AaCA\u0001g\u0006\u0005\t\u0011!B\u0001\u0003\u0007\u00111a\u0018\u00133#\u0011\t)!a\u0003\u0011\u0007I\t9!C\u0002\u0002\nM\u0011qAT8uQ&tw\rE\u0002\u0013\u0003\u001bI1!a\u0004\u0014\u0005\r\te.\u001f\u0005\b\u0003'\u0019\b\u0019AA\u000b\u0003\u0011Q'\u000f\u001a3\u0011\t]L\u00181\u0002\u0004\b\u00033q\u0001ABA\u000e\u0005I\tU\u000f^8CCR\u001c\u0007.\u001a3QS\u000e\\G.\u001a:\u0014\u000b\u0005]\u0011#!\b\u0011\r\u0005}\u0011qFA\u001b\u001d\u0011\t\t#a\u000b\u000f\t\u0005\r\u0012\u0011F\u0007\u0003\u0003KQ1!a\n \u0003\u0019a$o\\8u}%\tA#C\u0002\u0002.M\tq\u0001]1dW\u0006<W-\u0003\u0003\u00022\u0005M\"\u0001C%uKJ\fGo\u001c:\u000b\u0007\u000552\u0003\u0005\u0003\u0013\u0003\u0006]\u0002c\u0001\n\u0002:%\u0019\u00111H\n\u0003\t\tKH/\u001a\u0005\f\u0003\u007f\t9B!A!\u0002\u0013\t\t%\u0001\u0003ji\u0016\u0014\bCBA\u0010\u0003_\tY\u0001C\u0004\u001f\u0003/!\t!!\u0012\u0015\t\u0005\u001d\u0013\u0011\n\t\u0004e\u0005]\u0001\u0002CA \u0003\u0007\u0002\r!!\u0011\t\u0013%\n9B1A\u0005\n\u00055SCAA(!\u0011\t\t&a\u0015\u000e\u0003!J1!!\u0016)\u0005\u001d\u0001\u0016nY6mKJD\u0011\"!\u0017\u0002\u0018\u0001\u0006I!a\u0014\u0002\u000fAL7m\u001b7fA!Q\u0011QLA\f\u0001\u0004%I!a\u0018\u0002\u000b\t\fGo\u00195\u0016\u0003aC!\"a\u0019\u0002\u0018\u0001\u0007I\u0011BA3\u0003%\u0011\u0017\r^2i?\u0012*\u0017\u000fF\u0002j\u0003OB\u0001\"\\A1\u0003\u0003\u0005\r\u0001\u0017\u0005\t\u0003W\n9\u0002)Q\u00051\u00061!-\u0019;dQ\u0002B!\"a\u001c\u0002\u0018\t\u0007I\u0011BA9\u0003\u0019\u0011WO\u001a4feV\u0011\u00111\u000f\t\u0007\u0003k\ny(a\u0003\u000e\u0005\u0005]$\u0002BA=\u0003w\nq!\\;uC\ndWMC\u0002\u0002~M\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\t\t)a\u001e\u0003\u0017\u0005\u0013(/Y=Ck\u001a4WM\u001d\u0005\n\u0003\u000b\u000b9\u0002)A\u0005\u0003g\nqAY;gM\u0016\u0014\b\u0005C\u0004\u0002\n\u0006]A\u0011\t2\u0002\u000f!\f7OT3yi\"A\u0011QRA\f\t\u0003\ny)\u0001\u0003oKb$HCAA\u001b\u0011\u001d\t\u0019J\u0004C\u0001\u0003+\u000bAB[1wCR{\u0007+\u001f;i_:$B!a&\u0002\u001aB!q/_A\u001b\u0011!\tY*!%A\u0002\u0005u\u0015\u0001\u00026S\t\u0012\u0003D!a(\u0002$B!q/_AQ!\rq\u00181\u0015\u0003\r\u0003K\u000bI*!A\u0001\u0002\u000b\u0005\u00111\u0001\u0002\u0004?\u0012\"\u0004bBAU\u001d\u0011\u0005\u00111V\u0001\raf$\bn\u001c8U_*\u000bg/\u0019\u000b\u0007\u0003+\ti+!-\t\u0011\u0005=\u0016q\u0015a\u0001\u0003/\u000bQ\u0001]=S\t\u0012Cq!a-\u0002(\u0002\u00071-A\u0004cCR\u001c\u0007.\u001a3\t\u000f\u0005]f\u0002\"\u0003\u0002:\u0006Y1\r[3dWBK7m\u001b7f)\u0011\tY,!1\u0011\u000bI\tilY2\n\u0007\u0005}6C\u0001\u0004UkBdWM\r\u0005\t\u0003\u0007\f)\f1\u0001\u0002F\u0006\tA\u000fE\u0004\u0013\u0003{\u000bY!a\u0003\t\u000f\u0005%g\u0002\"\u0001\u0002L\u0006y\u0001/Y5s%\u0012#Ek\u001c)zi\"|g\u000e\u0006\u0004\u0002N\u0006e\u0017Q\u001c\t\u0007\u0003\u001f\f).!\u000e\u000e\u0005\u0005E'bAAj\r\u0005\u0019!\u000f\u001a3\n\t\u0005]\u0017\u0011\u001b\u0002\u0004%\u0012#\u0005\u0002CAj\u0003\u000f\u0004\r!a7\u0011\r\u0005=\u0017Q[Ac\u0011\u001d\ty.a2A\u0002a\u000b\u0011BY1uG\"\u001c\u0016N_3\t\u000f\u0005\rh\u0002\"\u0001\u0002f\u0006y\u0001/\u001f;i_:$v\u000eU1jeJ#E)\u0006\u0004\u0002h\u0006=\u0018Q\u001f\u000b\u0007\u0003S\fI0a?\u0011\r\u0005=\u0017Q[Av!\u001d\u0011\u0012QXAw\u0003g\u00042A`Ax\t!\t\t0!9C\u0002\u0005\r!!A&\u0011\u0007y\f)\u0010\u0002\u0005\u0002x\u0006\u0005(\u0019AA\u0002\u0005\u00051\u0006\u0002CAX\u0003C\u0004\r!!4\t\u000f\u0005M\u0016\u0011\u001da\u0001G\u0002"
)
public final class SerDeUtil {
   public static boolean initializeLogIfNecessary$default$2() {
      return SerDeUtil$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return SerDeUtil$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      SerDeUtil$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return SerDeUtil$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      SerDeUtil$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      SerDeUtil$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      SerDeUtil$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      SerDeUtil$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      SerDeUtil$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      SerDeUtil$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      SerDeUtil$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      SerDeUtil$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      SerDeUtil$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      SerDeUtil$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return SerDeUtil$.MODULE$.log();
   }

   public static String logName() {
      return SerDeUtil$.MODULE$.logName();
   }

   public static <K, V> RDD<Tuple2<K, V>> pythonToPairRDD(RDD<byte[]> var0, boolean var1) {
      return SerDeUtil$.MODULE$.pythonToPairRDD(var0, var1);
   }

   public static RDD<byte[]> pairRDDToPython(RDD<Tuple2<Object, Object>> var0, int var1) {
      return SerDeUtil$.MODULE$.pairRDDToPython(var0, var1);
   }

   public static JavaRDD<Object> pythonToJava(JavaRDD<byte[]> var0, boolean var1) {
      return SerDeUtil$.MODULE$.pythonToJava(var0, var1);
   }

   public static JavaRDD<byte[]> javaToPython(JavaRDD<?> var0) {
      return SerDeUtil$.MODULE$.javaToPython(var0);
   }

   public static JavaRDD<Object> toJavaArray(JavaRDD<Object> var0) {
      return SerDeUtil$.MODULE$.toJavaArray(var0);
   }

   public static void initialize() {
      SerDeUtil$.MODULE$.initialize();
   }

   public static class ByteArrayConstructor extends net.razorvine.pickle.objects.ByteArrayConstructor {
      public Object construct(Object[] args) {
         return args.length == 0 ? Array$.MODULE$.emptyByteArray() : super.construct(args);
      }
   }

   public static class ArrayConstructor extends net.razorvine.pickle.objects.ArrayConstructor {
      private final Map<Object, Object> machineCodes;

      public Map<Object, Object> machineCodes() {
         return this.machineCodes;
      }

      public Object construct(Object[] args) {
         Object var10000;
         if (args.length == 1) {
            var10000 = this.construct(Predef$.MODULE$.refArrayOps(args).$plus$plus(Predef$.MODULE$.refArrayOps((Object[])(new String[]{""})), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Object())));
         } else if (args.length == 2 && args[1] instanceof String) {
            char typecode = ((String)args[0]).charAt(0);
            byte[] data = ((String)args[1]).getBytes(StandardCharsets.ISO_8859_1);
            if (typecode == 'c') {
               char[] result = new char[data.length];

               for(int i = 0; i < data.length; ++i) {
                  result[i] = (char)data[i];
               }

               var10000 = result;
            } else {
               var10000 = this.construct(typecode, BoxesRunTime.unboxToInt(this.machineCodes().apply(BoxesRunTime.boxToCharacter(typecode))), data);
            }
         } else {
            if (args.length == 2) {
               label62: {
                  var10000 = args[0];
                  String var6 = "l";
                  if (var10000 == null) {
                     if (var6 != null) {
                        break label62;
                     }
                  } else if (!var10000.equals(var6)) {
                     break label62;
                  }

                  ArrayList values = (ArrayList)args[1];
                  long[] result = new long[values.size()];

                  for(int i = 0; i < values.size(); ++i) {
                     result[i] = ((Number)values.get(i)).longValue();
                  }

                  var10000 = result;
                  return var10000;
               }
            }

            var10000 = super.construct(args);
         }

         return var10000;
      }

      public ArrayConstructor() {
         this.machineCodes = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) ? (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('B')), BoxesRunTime.boxToInteger(0)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('b')), BoxesRunTime.boxToInteger(1)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('H')), BoxesRunTime.boxToInteger(3)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('h')), BoxesRunTime.boxToInteger(5)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('I')), BoxesRunTime.boxToInteger(7)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('i')), BoxesRunTime.boxToInteger(9)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('L')), BoxesRunTime.boxToInteger(11)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('l')), BoxesRunTime.boxToInteger(13)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('f')), BoxesRunTime.boxToInteger(15)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('d')), BoxesRunTime.boxToInteger(17)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('u')), BoxesRunTime.boxToInteger(21))})) : (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('B')), BoxesRunTime.boxToInteger(0)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('b')), BoxesRunTime.boxToInteger(1)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('H')), BoxesRunTime.boxToInteger(2)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('h')), BoxesRunTime.boxToInteger(4)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('I')), BoxesRunTime.boxToInteger(6)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('i')), BoxesRunTime.boxToInteger(8)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('L')), BoxesRunTime.boxToInteger(10)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('l')), BoxesRunTime.boxToInteger(12)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('f')), BoxesRunTime.boxToInteger(14)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('d')), BoxesRunTime.boxToInteger(16)), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(BoxesRunTime.boxToCharacter('u')), BoxesRunTime.boxToInteger(20))}));
      }
   }

   public static class AutoBatchedPickler implements Iterator<byte[]> {
      private final Iterator<Object> iter;
      private final Pickler pickle;
      private int batch;
      private final ArrayBuffer<Object> buffer;

      public Iterator<byte[]> seq() {
         return Iterator$class.seq(this);
      }

      public boolean isEmpty() {
         return Iterator$class.isEmpty(this);
      }

      public boolean isTraversableAgain() {
         return Iterator$class.isTraversableAgain(this);
      }

      public boolean hasDefiniteSize() {
         return Iterator$class.hasDefiniteSize(this);
      }

      public Iterator<byte[]> take(int n) {
         return Iterator$class.take(this, n);
      }

      public Iterator<byte[]> drop(int n) {
         return Iterator$class.drop(this, n);
      }

      public Iterator<byte[]> slice(int from, int until) {
         return Iterator$class.slice(this, from, until);
      }

      public <B> Iterator<B> map(Function1<byte[], B> f) {
         return Iterator$class.map(this, f);
      }

      public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
         return Iterator$class.$plus$plus(this, that);
      }

      public <B> Iterator<B> flatMap(Function1<byte[], GenTraversableOnce<B>> f) {
         return Iterator$class.flatMap(this, f);
      }

      public Iterator<byte[]> filter(Function1<byte[], Object> p) {
         return Iterator$class.filter(this, p);
      }

      public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<byte[], B, Object> p) {
         return Iterator$class.corresponds(this, that, p);
      }

      public Iterator<byte[]> withFilter(Function1<byte[], Object> p) {
         return Iterator$class.withFilter(this, p);
      }

      public Iterator<byte[]> filterNot(Function1<byte[], Object> p) {
         return Iterator$class.filterNot(this, p);
      }

      public <B> Iterator<B> collect(PartialFunction<byte[], B> pf) {
         return Iterator$class.collect(this, pf);
      }

      public <B> Iterator<B> scanLeft(B z, Function2<B, byte[], B> op) {
         return Iterator$class.scanLeft(this, z, op);
      }

      public <B> Iterator<B> scanRight(B z, Function2<byte[], B, B> op) {
         return Iterator$class.scanRight(this, z, op);
      }

      public Iterator<byte[]> takeWhile(Function1<byte[], Object> p) {
         return Iterator$class.takeWhile(this, p);
      }

      public Tuple2<Iterator<byte[]>, Iterator<byte[]>> partition(Function1<byte[], Object> p) {
         return Iterator$class.partition(this, p);
      }

      public Tuple2<Iterator<byte[]>, Iterator<byte[]>> span(Function1<byte[], Object> p) {
         return Iterator$class.span(this, p);
      }

      public Iterator<byte[]> dropWhile(Function1<byte[], Object> p) {
         return Iterator$class.dropWhile(this, p);
      }

      public <B> Iterator<Tuple2<byte[], B>> zip(Iterator<B> that) {
         return Iterator$class.zip(this, that);
      }

      public <A1> Iterator<A1> padTo(int len, A1 elem) {
         return Iterator$class.padTo(this, len, elem);
      }

      public Iterator<Tuple2<byte[], Object>> zipWithIndex() {
         return Iterator$class.zipWithIndex(this);
      }

      public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
         return Iterator$class.zipAll(this, that, thisElem, thatElem);
      }

      public <U> void foreach(Function1<byte[], U> f) {
         Iterator$class.foreach(this, f);
      }

      public boolean forall(Function1<byte[], Object> p) {
         return Iterator$class.forall(this, p);
      }

      public boolean exists(Function1<byte[], Object> p) {
         return Iterator$class.exists(this, p);
      }

      public boolean contains(Object elem) {
         return Iterator$class.contains(this, elem);
      }

      public Option<byte[]> find(Function1<byte[], Object> p) {
         return Iterator$class.find(this, p);
      }

      public int indexWhere(Function1<byte[], Object> p) {
         return Iterator$class.indexWhere(this, p);
      }

      public <B> int indexOf(B elem) {
         return Iterator$class.indexOf(this, elem);
      }

      public BufferedIterator<byte[]> buffered() {
         return Iterator$class.buffered(this);
      }

      public <B> Iterator<byte[]>.GroupedIterator<B> grouped(int size) {
         return Iterator$class.grouped(this, size);
      }

      public <B> Iterator<byte[]>.GroupedIterator<B> sliding(int size, int step) {
         return Iterator$class.sliding(this, size, step);
      }

      public int length() {
         return Iterator$class.length(this);
      }

      public Tuple2<Iterator<byte[]>, Iterator<byte[]>> duplicate() {
         return Iterator$class.duplicate(this);
      }

      public <B> Iterator<B> patch(int from, Iterator<B> patchElems, int replaced) {
         return Iterator$class.patch(this, from, patchElems, replaced);
      }

      public <B> void copyToArray(Object xs, int start, int len) {
         Iterator$class.copyToArray(this, xs, start, len);
      }

      public boolean sameElements(Iterator<?> that) {
         return Iterator$class.sameElements(this, that);
      }

      public Traversable<byte[]> toTraversable() {
         return Iterator$class.toTraversable(this);
      }

      public Iterator<byte[]> toIterator() {
         return Iterator$class.toIterator(this);
      }

      public Stream<byte[]> toStream() {
         return Iterator$class.toStream(this);
      }

      public String toString() {
         return Iterator$class.toString(this);
      }

      public <B> int sliding$default$2() {
         return Iterator$class.sliding$default$2(this);
      }

      public List<byte[]> reversed() {
         return TraversableOnce$class.reversed(this);
      }

      public int size() {
         return TraversableOnce$class.size(this);
      }

      public boolean nonEmpty() {
         return TraversableOnce$class.nonEmpty(this);
      }

      public int count(Function1<byte[], Object> p) {
         return TraversableOnce$class.count(this, p);
      }

      public <B> Option<B> collectFirst(PartialFunction<byte[], B> pf) {
         return TraversableOnce$class.collectFirst(this, pf);
      }

      public <B> B $div$colon(B z, Function2<B, byte[], B> op) {
         return (B)TraversableOnce$class.$div$colon(this, z, op);
      }

      public <B> B $colon$bslash(B z, Function2<byte[], B, B> op) {
         return (B)TraversableOnce$class.$colon$bslash(this, z, op);
      }

      public <B> B foldLeft(B z, Function2<B, byte[], B> op) {
         return (B)TraversableOnce$class.foldLeft(this, z, op);
      }

      public <B> B foldRight(B z, Function2<byte[], B, B> op) {
         return (B)TraversableOnce$class.foldRight(this, z, op);
      }

      public <B> B reduceLeft(Function2<B, byte[], B> op) {
         return (B)TraversableOnce$class.reduceLeft(this, op);
      }

      public <B> B reduceRight(Function2<byte[], B, B> op) {
         return (B)TraversableOnce$class.reduceRight(this, op);
      }

      public <B> Option<B> reduceLeftOption(Function2<B, byte[], B> op) {
         return TraversableOnce$class.reduceLeftOption(this, op);
      }

      public <B> Option<B> reduceRightOption(Function2<byte[], B, B> op) {
         return TraversableOnce$class.reduceRightOption(this, op);
      }

      public <A1> A1 reduce(Function2<A1, A1, A1> op) {
         return (A1)TraversableOnce$class.reduce(this, op);
      }

      public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
         return TraversableOnce$class.reduceOption(this, op);
      }

      public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
         return (A1)TraversableOnce$class.fold(this, z, op);
      }

      public <B> B aggregate(Function0<B> z, Function2<B, byte[], B> seqop, Function2<B, B, B> combop) {
         return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
      }

      public <B> B sum(Numeric<B> num) {
         return (B)TraversableOnce$class.sum(this, num);
      }

      public <B> B product(Numeric<B> num) {
         return (B)TraversableOnce$class.product(this, num);
      }

      public Object min(Ordering cmp) {
         return TraversableOnce$class.min(this, cmp);
      }

      public Object max(Ordering cmp) {
         return TraversableOnce$class.max(this, cmp);
      }

      public Object maxBy(Function1 f, Ordering cmp) {
         return TraversableOnce$class.maxBy(this, f, cmp);
      }

      public Object minBy(Function1 f, Ordering cmp) {
         return TraversableOnce$class.minBy(this, f, cmp);
      }

      public <B> void copyToBuffer(Buffer<B> dest) {
         TraversableOnce$class.copyToBuffer(this, dest);
      }

      public <B> void copyToArray(Object xs, int start) {
         TraversableOnce$class.copyToArray(this, xs, start);
      }

      public <B> void copyToArray(Object xs) {
         TraversableOnce$class.copyToArray(this, xs);
      }

      public <B> Object toArray(ClassTag<B> evidence$1) {
         return TraversableOnce$class.toArray(this, evidence$1);
      }

      public List<byte[]> toList() {
         return TraversableOnce$class.toList(this);
      }

      public Iterable<byte[]> toIterable() {
         return TraversableOnce$class.toIterable(this);
      }

      public Seq<byte[]> toSeq() {
         return TraversableOnce$class.toSeq(this);
      }

      public IndexedSeq<byte[]> toIndexedSeq() {
         return TraversableOnce$class.toIndexedSeq(this);
      }

      public <B> Buffer<B> toBuffer() {
         return TraversableOnce$class.toBuffer(this);
      }

      public <B> Set<B> toSet() {
         return TraversableOnce$class.toSet(this);
      }

      public Vector<byte[]> toVector() {
         return TraversableOnce$class.toVector(this);
      }

      public <Col> Col to(CanBuildFrom<Nothing$, byte[], Col> cbf) {
         return (Col)TraversableOnce$class.to(this, cbf);
      }

      public <T, U> Map<T, U> toMap(Predef.$less$colon$less<byte[], Tuple2<T, U>> ev) {
         return TraversableOnce$class.toMap(this, ev);
      }

      public String mkString(String start, String sep, String end) {
         return TraversableOnce$class.mkString(this, start, sep, end);
      }

      public String mkString(String sep) {
         return TraversableOnce$class.mkString(this, sep);
      }

      public String mkString() {
         return TraversableOnce$class.mkString(this);
      }

      public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
         return TraversableOnce$class.addString(this, b, start, sep, end);
      }

      public StringBuilder addString(StringBuilder b, String sep) {
         return TraversableOnce$class.addString(this, b, sep);
      }

      public StringBuilder addString(StringBuilder b) {
         return TraversableOnce$class.addString(this, b);
      }

      private Pickler pickle() {
         return this.pickle;
      }

      private int batch() {
         return this.batch;
      }

      private void batch_$eq(int x$1) {
         this.batch = x$1;
      }

      private ArrayBuffer<Object> buffer() {
         return this.buffer;
      }

      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public byte[] next() {
         while(this.iter.hasNext() && this.buffer().length() < this.batch()) {
            this.buffer().$plus$eq(this.iter.next());
         }

         byte[] bytes = this.pickle().dumps(this.buffer().toArray(ClassTag$.MODULE$.Any()));
         int size = bytes.length;
         if (size < 1048576) {
            this.batch_$eq(this.batch() * 2);
         } else if (size > 10485760 && this.batch() > 1) {
            this.batch_$eq(this.batch() / 2);
         }

         this.buffer().clear();
         return bytes;
      }

      public AutoBatchedPickler(Iterator<Object> iter) {
         this.iter = iter;
         TraversableOnce$class.$init$(this);
         Iterator$class.$init$(this);
         this.pickle = new Pickler();
         this.batch = 1;
         this.buffer = new ArrayBuffer<Object>();
      }
   }
}
