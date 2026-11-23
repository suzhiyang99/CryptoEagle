package org.apache.spark;

import java.io.Serializable;
import org.apache.hadoop.io.Writable;
import scala.Function1;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}d!B\u0001\u0003\u0001\tA!aD,sSR\f'\r\\3GC\u000e$xN]=\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e,\"!C\u0014\u0014\u0007\u0001Q\u0001\u0003\u0005\u0002\f\u001d5\tABC\u0001\u000e\u0003\u0015\u00198-\u00197b\u0013\tyAB\u0001\u0004B]f\u0014VM\u001a\t\u0003#Yi\u0011A\u0005\u0006\u0003'Q\t!![8\u000b\u0003U\tAA[1wC&\u0011qC\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t3\u0001\u0011)\u0019!C\u00017\u0005iqO]5uC\ndWm\u00117bgN\u001c\u0001!F\u0001\u001d!\u0011YQd\b\u0019\n\u0005ya!!\u0003$v]\u000e$\u0018n\u001c82!\r\u00013%J\u0007\u0002C)\u0011!\u0005D\u0001\be\u00164G.Z2u\u0013\t!\u0013E\u0001\u0005DY\u0006\u001c8\u000fV1h!\t1s\u0005\u0004\u0001\u0005\u000b!\u0002!\u0019A\u0015\u0003\u0003Q\u000b\"AK\u0017\u0011\u0005-Y\u0013B\u0001\u0017\r\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"a\u0003\u0018\n\u0005=b!aA!osB\u0012\u0011'\u000f\t\u0004eUBdBA\u00064\u0013\t!D\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003m]\u0012Qa\u00117bgNT!\u0001\u000e\u0007\u0011\u0005\u0019JD!\u0003\u001e<\u0003\u0003\u0005\tQ!\u0001C\u0005\u0011yF%\r\u001c\t\u0011q\u0002!\u0011!Q\u0001\nu\nab\u001e:ji\u0006\u0014G.Z\"mCN\u001c\b\u0005\u0005\u0003\f;}q\u0004GA B!\r\u0011T\u0007\u0011\t\u0003M\u0005#\u0011BO\u001e\u0002\u0002\u0003\u0005)\u0011\u0001\"\u0012\u0005)\u001a\u0005C\u0001#I\u001b\u0005)%BA\nG\u0015\t9E!\u0001\u0004iC\u0012|w\u000e]\u0005\u0003\u0013\u0016\u0013\u0001b\u0016:ji\u0006\u0014G.\u001a\u0005\t\u0017\u0002\u0011)\u0019!C\u0001\u0019\u000691m\u001c8wKJ$X#A'\u0011\t-iRe\u0011\u0005\t\u001f\u0002\u0011\t\u0011)A\u0005\u001b\u0006A1m\u001c8wKJ$\b\u0005C\u0003R\u0001\u0011\u0005!+\u0001\u0004=S:LGO\u0010\u000b\u0004'V[\u0006c\u0001+\u0001K5\t!\u0001C\u0003\u001a!\u0002\u0007a\u000b\u0005\u0003\f;}9\u0006G\u0001-[!\r\u0011T'\u0017\t\u0003Mi#\u0011BO+\u0002\u0002\u0003\u0005)\u0011\u0001\"\t\u000b-\u0003\u0006\u0019A'\b\u000bu\u0013\u0001\u0012\u00010\u0002\u001f]\u0013\u0018\u000e^1cY\u00164\u0015m\u0019;pef\u0004\"\u0001V0\u0007\u000b\u0005\u0011\u0001\u0012\u00011\u0014\u0007}S\u0011\r\u0005\u0002\fE&\u0011q\u0003\u0004\u0005\u0006#~#\t\u0001\u001a\u000b\u0002=\"1am\u0018C\u0001\u0005\u001d\fQc]5na2,wK]5uC\ndWMR1di>\u0014\u00180F\u0002iYR$\"!\u001b<\u0015\u0007)l\u0007\u000fE\u0002U\u0001-\u0004\"A\n7\u0005\u000b!*'\u0019A\u0015\t\u000f9,\u0017\u0011!a\u0002_\u0006YQM^5eK:\u001cW\r\n\u001a3!\r\u00013e\u001b\u0005\bc\u0016\f\t\u0011q\u0001s\u0003-)g/\u001b3f]\u000e,GEM\u001a\u0011\u0007\u0001\u001a3\u000f\u0005\u0002'i\u0012)Q/\u001ab\u0001\u0005\n\tq\u000bC\u0003LK\u0002\u0007q\u000f\u0005\u0003\f;-\u001c\b\"B=`\t\u0007Q\u0018AE5oi^\u0013\u0018\u000e^1cY\u00164\u0015m\u0019;pef,\u0012a\u001f\t\u0004)\u0002a\bCA\u0006~\u0013\tqHBA\u0002J]RDq!!\u0001`\t\u0007\t\u0019!A\nm_:<wK]5uC\ndWMR1di>\u0014\u00180\u0006\u0002\u0002\u0006A!A\u000bAA\u0004!\rY\u0011\u0011B\u0005\u0004\u0003\u0017a!\u0001\u0002'p]\u001eDq!a\u0004`\t\u0007\t\t\"\u0001\u000bgY>\fGo\u0016:ji\u0006\u0014G.\u001a$bGR|'/_\u000b\u0003\u0003'\u0001B\u0001\u0016\u0001\u0002\u0016A\u00191\"a\u0006\n\u0007\u0005eABA\u0003GY>\fG\u000fC\u0004\u0002\u001e}#\u0019!a\b\u0002+\u0011|WO\u00197f/JLG/\u00192mK\u001a\u000b7\r^8ssV\u0011\u0011\u0011\u0005\t\u0005)\u0002\t\u0019\u0003E\u0002\f\u0003KI1!a\n\r\u0005\u0019!u.\u001e2mK\"9\u00111F0\u0005\u0004\u00055\u0012A\u00062p_2,\u0017M\\,sSR\f'\r\\3GC\u000e$xN]=\u0016\u0005\u0005=\u0002\u0003\u0002+\u0001\u0003c\u00012aCA\u001a\u0013\r\t)\u0004\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d\tId\u0018C\u0002\u0003w\tACY=uKN<&/\u001b;bE2,g)Y2u_JLXCAA\u001f!\u0011!\u0006!a\u0010\u0011\u000b-\t\t%!\u0012\n\u0007\u0005\rCBA\u0003BeJ\f\u0017\u0010E\u0002\f\u0003\u000fJ1!!\u0013\r\u0005\u0011\u0011\u0015\u0010^3\t\u000f\u00055s\fb\u0001\u0002P\u0005)2\u000f\u001e:j]\u001e<&/\u001b;bE2,g)Y2u_JLXCAA)!\u0011!\u0006!a\u0015\u0011\u0007I\n)&C\u0002\u0002X]\u0012aa\u0015;sS:<\u0007bBA.?\u0012\r\u0011QL\u0001\u0018oJLG/\u00192mK^\u0013\u0018\u000e^1cY\u00164\u0015m\u0019;pef,B!a\u0018\u0002fQ!\u0011\u0011MA4!\u0011!\u0006!a\u0019\u0011\u0007\u0019\n)\u0007\u0002\u0004)\u00033\u0012\rA\u0011\u0005\u000b\u0003S\nI&!AA\u0004\u0005-\u0014aC3wS\u0012,gnY3%eQ\u0002B\u0001I\u0012\u0002d!I\u0011qN0\u0002\u0002\u0013%\u0011\u0011O\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002tA!\u0011QOA>\u001b\t\t9HC\u0002\u0002zQ\tA\u0001\\1oO&!\u0011QPA<\u0005\u0019y%M[3di\u0002"
)
public class WritableFactory<T> implements Serializable {
   private final Function1<ClassTag<T>, Class<? extends Writable>> writableClass;
   private final Function1<T, Writable> convert;

   public static <T extends Writable> WritableFactory<T> writableWritableFactory(ClassTag<T> var0) {
      return WritableFactory$.MODULE$.<T>writableWritableFactory(var0);
   }

   public static WritableFactory<String> stringWritableFactory() {
      return WritableFactory$.MODULE$.stringWritableFactory();
   }

   public static WritableFactory<byte[]> bytesWritableFactory() {
      return WritableFactory$.MODULE$.bytesWritableFactory();
   }

   public static WritableFactory<Object> booleanWritableFactory() {
      return WritableFactory$.MODULE$.booleanWritableFactory();
   }

   public static WritableFactory<Object> doubleWritableFactory() {
      return WritableFactory$.MODULE$.doubleWritableFactory();
   }

   public static WritableFactory<Object> floatWritableFactory() {
      return WritableFactory$.MODULE$.floatWritableFactory();
   }

   public static WritableFactory<Object> longWritableFactory() {
      return WritableFactory$.MODULE$.longWritableFactory();
   }

   public static WritableFactory<Object> intWritableFactory() {
      return WritableFactory$.MODULE$.intWritableFactory();
   }

   public Function1<ClassTag<T>, Class<? extends Writable>> writableClass() {
      return this.writableClass;
   }

   public Function1<T, Writable> convert() {
      return this.convert;
   }

   public WritableFactory(Function1<ClassTag<T>, Class<? extends Writable>> writableClass, Function1<T, Writable> convert) {
      this.writableClass = writableClass;
      this.convert = convert;
   }
}
