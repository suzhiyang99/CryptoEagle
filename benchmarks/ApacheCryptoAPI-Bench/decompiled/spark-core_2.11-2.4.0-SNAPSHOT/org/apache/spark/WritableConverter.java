package org.apache.spark;

import java.io.Serializable;
import org.apache.hadoop.io.Writable;
import scala.Function0;
import scala.Function1;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005h!B\u0001\u0003\u0001\tA!!E,sSR\f'\r\\3D_:4XM\u001d;fe*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014x-\u0006\u0002\nOM\u0019\u0001A\u0003\t\u0011\u0005-qQ\"\u0001\u0007\u000b\u00035\tQa]2bY\u0006L!a\u0004\u0007\u0003\r\u0005s\u0017PU3g!\t\tb#D\u0001\u0013\u0015\t\u0019B#\u0001\u0002j_*\tQ#\u0001\u0003kCZ\f\u0017BA\f\u0013\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!I\u0002A!b\u0001\n\u0003Y\u0012!D<sSR\f'\r\\3DY\u0006\u001c8o\u0001\u0001\u0016\u0003q\u0001BaC\u000f a%\u0011a\u0004\u0004\u0002\n\rVt7\r^5p]F\u00022\u0001I\u0012&\u001b\u0005\t#B\u0001\u0012\r\u0003\u001d\u0011XM\u001a7fGRL!\u0001J\u0011\u0003\u0011\rc\u0017m]:UC\u001e\u0004\"AJ\u0014\r\u0001\u0011)\u0001\u0006\u0001b\u0001S\t\tA+\u0005\u0002+[A\u00111bK\u0005\u0003Y1\u0011qAT8uQ&tw\r\u0005\u0002\f]%\u0011q\u0006\u0004\u0002\u0004\u0003:L\bGA\u0019:!\r\u0011T\u0007\u000f\b\u0003\u0017MJ!\u0001\u000e\u0007\u0002\rA\u0013X\rZ3g\u0013\t1tGA\u0003DY\u0006\u001c8O\u0003\u00025\u0019A\u0011a%\u000f\u0003\num\n\t\u0011!A\u0003\u0002\t\u0013Aa\u0018\u00132k!AA\b\u0001B\u0001B\u0003%Q(\u0001\bxe&$\u0018M\u00197f\u00072\f7o\u001d\u0011\u0011\t-irD\u0010\u0019\u0003\u007f\u0005\u00032AM\u001bA!\t1\u0013\tB\u0005;w\u0005\u0005\t\u0011!B\u0001\u0005F\u0011!f\u0011\t\u0003\t\"k\u0011!\u0012\u0006\u0003'\u0019S!a\u0012\u0003\u0002\r!\fGm\\8q\u0013\tIUI\u0001\u0005Xe&$\u0018M\u00197f\u0011!Y\u0005A!b\u0001\n\u0003a\u0015aB2p]Z,'\u000f^\u000b\u0002\u001bB!1\"H\"&\u0011!y\u0005A!A!\u0002\u0013i\u0015\u0001C2p]Z,'\u000f\u001e\u0011\t\u000bE\u0003A\u0011\u0001*\u0002\rqJg.\u001b;?)\r\u0019Vk\u0017\t\u0004)\u0002)S\"\u0001\u0002\t\u000be\u0001\u0006\u0019\u0001,\u0011\t-ird\u0016\u0019\u00031j\u00032AM\u001bZ!\t1#\fB\u0005;+\u0006\u0005\t\u0011!B\u0001\u0005\")1\n\u0015a\u0001\u001b\u001e)QL\u0001E\u0001=\u0006\trK]5uC\ndWmQ8om\u0016\u0014H/\u001a:\u0011\u0005Q{f!B\u0001\u0003\u0011\u0003\u00017cA0\u000bCB\u00111BY\u0005\u0003/1AQ!U0\u0005\u0002\u0011$\u0012A\u0018\u0005\u0007M~#\tAA4\u0002/MLW\u000e\u001d7f/JLG/\u00192mK\u000e{gN^3si\u0016\u0014Xc\u00015mcR\u0011\u0011n\u001d\u000b\u0003U6\u00042\u0001\u0016\u0001l!\t1C\u000eB\u0003)K\n\u0007\u0011\u0006C\u0004oK\u0006\u0005\t9A8\u0002\u0017\u00154\u0018\u000eZ3oG\u0016$#\u0007\r\t\u0004A\r\u0002\bC\u0001\u0014r\t\u0015\u0011XM1\u0001C\u0005\u00059\u0006\"B&f\u0001\u0004!\b\u0003B\u0006\u001ea.DqA^0C\u0002\u0013\rq/\u0001\fj]R<&/\u001b;bE2,7i\u001c8wKJ$XM\u001d$o+\u0005A\bcA\u0006zw&\u0011!\u0010\u0004\u0002\n\rVt7\r^5p]B\u00022\u0001\u0016\u0001}!\tYQ0\u0003\u0002\u007f\u0019\t\u0019\u0011J\u001c;\t\u000f\u0005\u0005q\f)A\u0005q\u00069\u0012N\u001c;Xe&$\u0018M\u00197f\u0007>tg/\u001a:uKJ4e\u000e\t\u0005\n\u0003\u000by&\u0019!C\u0002\u0003\u000f\tq\u0003\\8oO^\u0013\u0018\u000e^1cY\u0016\u001cuN\u001c<feR,'O\u00128\u0016\u0005\u0005%\u0001\u0003B\u0006z\u0003\u0017\u0001B\u0001\u0016\u0001\u0002\u000eA\u00191\"a\u0004\n\u0007\u0005EAB\u0001\u0003M_:<\u0007\u0002CA\u000b?\u0002\u0006I!!\u0003\u000211|gnZ,sSR\f'\r\\3D_:4XM\u001d;fe\u001as\u0007\u0005C\u0005\u0002\u001a}\u0013\r\u0011b\u0001\u0002\u001c\u0005IBm\\;cY\u0016<&/\u001b;bE2,7i\u001c8wKJ$XM\u001d$o+\t\ti\u0002\u0005\u0003\fs\u0006}\u0001\u0003\u0002+\u0001\u0003C\u00012aCA\u0012\u0013\r\t)\u0003\u0004\u0002\u0007\t>,(\r\\3\t\u0011\u0005%r\f)A\u0005\u0003;\t!\u0004Z8vE2,wK]5uC\ndWmQ8om\u0016\u0014H/\u001a:G]\u0002B\u0011\"!\f`\u0005\u0004%\u0019!a\f\u00021\u0019dw.\u0019;Xe&$\u0018M\u00197f\u0007>tg/\u001a:uKJ4e.\u0006\u0002\u00022A!1\"_A\u001a!\u0011!\u0006!!\u000e\u0011\u0007-\t9$C\u0002\u0002:1\u0011QA\u00127pCRD\u0001\"!\u0010`A\u0003%\u0011\u0011G\u0001\u001aM2|\u0017\r^,sSR\f'\r\\3D_:4XM\u001d;fe\u001as\u0007\u0005C\u0005\u0002B}\u0013\r\u0011b\u0001\u0002D\u0005Q\"m\\8mK\u0006twK]5uC\ndWmQ8om\u0016\u0014H/\u001a:G]V\u0011\u0011Q\t\t\u0005\u0017e\f9\u0005\u0005\u0003U\u0001\u0005%\u0003cA\u0006\u0002L%\u0019\u0011Q\n\u0007\u0003\u000f\t{w\u000e\\3b]\"A\u0011\u0011K0!\u0002\u0013\t)%A\u000ec_>dW-\u00198Xe&$\u0018M\u00197f\u0007>tg/\u001a:uKJ4e\u000e\t\u0005\n\u0003+z&\u0019!C\u0002\u0003/\n\u0001DY=uKN<&/\u001b;bE2,7i\u001c8wKJ$XM\u001d$o+\t\tI\u0006\u0005\u0003\fs\u0006m\u0003\u0003\u0002+\u0001\u0003;\u0002RaCA0\u0003GJ1!!\u0019\r\u0005\u0015\t%O]1z!\rY\u0011QM\u0005\u0004\u0003Ob!\u0001\u0002\"zi\u0016D\u0001\"a\u001b`A\u0003%\u0011\u0011L\u0001\u001aEf$Xm],sSR\f'\r\\3D_:4XM\u001d;fe\u001as\u0007\u0005C\u0005\u0002p}\u0013\r\u0011b\u0001\u0002r\u0005I2\u000f\u001e:j]\u001e<&/\u001b;bE2,7i\u001c8wKJ$XM\u001d$o+\t\t\u0019\b\u0005\u0003\fs\u0006U\u0004\u0003\u0002+\u0001\u0003o\u00022AMA=\u0013\r\tYh\u000e\u0002\u0007'R\u0014\u0018N\\4\t\u0011\u0005}t\f)A\u0005\u0003g\n!d\u001d;sS:<wK]5uC\ndWmQ8om\u0016\u0014H/\u001a:G]\u0002Bq!a!`\t\u0007\t))A\u000exe&$\u0018M\u00197f/JLG/\u00192mK\u000e{gN^3si\u0016\u0014hI\\\u000b\u0005\u0003\u000f\u000by\t\u0006\u0003\u0002\n\u0006E\u0005\u0003B\u0006z\u0003\u0017\u0003B\u0001\u0016\u0001\u0002\u000eB\u0019a%a$\u0005\r!\n\tI1\u0001C\u0011)\t\u0019*!!\u0002\u0002\u0003\u000f\u0011QS\u0001\fKZLG-\u001a8dK\u0012\u0012\u0014\u0007\u0005\u0003!G\u00055\u0005bBAM?\u0012\r\u00111T\u0001\u0015S:$xK]5uC\ndWmQ8om\u0016\u0014H/\u001a:\u0015\u0003mDq!a(`\t\u0007\t\t+A\u000bm_:<wK]5uC\ndWmQ8om\u0016\u0014H/\u001a:\u0015\u0005\u0005-\u0001bBAS?\u0012\r\u0011qU\u0001\u0018I>,(\r\\3Xe&$\u0018M\u00197f\u0007>tg/\u001a:uKJ$\"!a\b\t\u000f\u0005-v\fb\u0001\u0002.\u00061b\r\\8bi^\u0013\u0018\u000e^1cY\u0016\u001cuN\u001c<feR,'\u000f\u0006\u0002\u00024!9\u0011\u0011W0\u0005\u0004\u0005M\u0016\u0001\u00072p_2,\u0017M\\,sSR\f'\r\\3D_:4XM\u001d;feR\u0011\u0011q\t\u0005\b\u0003o{F1AA]\u0003Y\u0011\u0017\u0010^3t/JLG/\u00192mK\u000e{gN^3si\u0016\u0014HCAA.\u0011\u001d\til\u0018C\u0002\u0003\u007f\u000bqc\u001d;sS:<wK]5uC\ndWmQ8om\u0016\u0014H/\u001a:\u0015\u0005\u0005U\u0004bBAb?\u0012\r\u0011QY\u0001\u001aoJLG/\u00192mK^\u0013\u0018\u000e^1cY\u0016\u001cuN\u001c<feR,'/\u0006\u0003\u0002H\u00065GCAAe!\u0011!\u0006!a3\u0011\u0007\u0019\ni\r\u0002\u0004)\u0003\u0003\u0014\rA\u0011\u0005\n\u0003#|\u0016\u0011!C\u0005\u0003'\f1B]3bIJ+7o\u001c7wKR\u0011\u0011Q\u001b\t\u0005\u0003/\fi.\u0004\u0002\u0002Z*\u0019\u00111\u001c\u000b\u0002\t1\fgnZ\u0005\u0005\u0003?\fIN\u0001\u0004PE*,7\r\u001e"
)
public class WritableConverter<T> implements Serializable {
   private final Function1<ClassTag<T>, Class<? extends Writable>> writableClass;
   private final Function1<Writable, T> convert;

   public static <T extends Writable> WritableConverter<T> writableWritableConverter() {
      return WritableConverter$.MODULE$.<T>writableWritableConverter();
   }

   public static WritableConverter<String> stringWritableConverter() {
      return WritableConverter$.MODULE$.stringWritableConverter();
   }

   public static WritableConverter<byte[]> bytesWritableConverter() {
      return WritableConverter$.MODULE$.bytesWritableConverter();
   }

   public static WritableConverter<Object> booleanWritableConverter() {
      return WritableConverter$.MODULE$.booleanWritableConverter();
   }

   public static WritableConverter<Object> floatWritableConverter() {
      return WritableConverter$.MODULE$.floatWritableConverter();
   }

   public static WritableConverter<Object> doubleWritableConverter() {
      return WritableConverter$.MODULE$.doubleWritableConverter();
   }

   public static WritableConverter<Object> longWritableConverter() {
      return WritableConverter$.MODULE$.longWritableConverter();
   }

   public static WritableConverter<Object> intWritableConverter() {
      return WritableConverter$.MODULE$.intWritableConverter();
   }

   public static <T extends Writable> Function0<WritableConverter<T>> writableWritableConverterFn(ClassTag<T> var0) {
      return WritableConverter$.MODULE$.writableWritableConverterFn(var0);
   }

   public static Function0<WritableConverter<String>> stringWritableConverterFn() {
      return WritableConverter$.MODULE$.stringWritableConverterFn();
   }

   public static Function0<WritableConverter<byte[]>> bytesWritableConverterFn() {
      return WritableConverter$.MODULE$.bytesWritableConverterFn();
   }

   public static Function0<WritableConverter<Object>> booleanWritableConverterFn() {
      return WritableConverter$.MODULE$.booleanWritableConverterFn();
   }

   public static Function0<WritableConverter<Object>> floatWritableConverterFn() {
      return WritableConverter$.MODULE$.floatWritableConverterFn();
   }

   public static Function0<WritableConverter<Object>> doubleWritableConverterFn() {
      return WritableConverter$.MODULE$.doubleWritableConverterFn();
   }

   public static Function0<WritableConverter<Object>> longWritableConverterFn() {
      return WritableConverter$.MODULE$.longWritableConverterFn();
   }

   public static Function0<WritableConverter<Object>> intWritableConverterFn() {
      return WritableConverter$.MODULE$.intWritableConverterFn();
   }

   public Function1<ClassTag<T>, Class<? extends Writable>> writableClass() {
      return this.writableClass;
   }

   public Function1<Writable, T> convert() {
      return this.convert;
   }

   public WritableConverter(Function1<ClassTag<T>, Class<? extends Writable>> writableClass, Function1<Writable, T> convert) {
      this.writableClass = writableClass;
      this.convert = convert;
   }
}
