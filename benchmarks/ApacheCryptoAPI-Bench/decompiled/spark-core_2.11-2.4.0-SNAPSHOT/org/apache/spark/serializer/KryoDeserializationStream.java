package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.UnsafeInput;
import java.io.EOFException;
import java.io.InputStream;
import java.util.Locale;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001y3Q!\u0001\u0002\u0001\t)\u0011\u0011d\u0013:z_\u0012+7/\u001a:jC2L'0\u0019;j_:\u001cFO]3b[*\u00111\u0001B\u0001\u000bg\u0016\u0014\u0018.\u00197ju\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u000e\u001b\u0005\u0011\u0011B\u0001\b\u0003\u0005U!Um]3sS\u0006d\u0017N_1uS>t7\u000b\u001e:fC6D\u0001\u0002\u0005\u0001\u0003\u0002\u0003\u0006IAE\u0001\fg\u0016\u0014\u0018J\\:uC:\u001cWm\u0001\u0001\u0011\u00051\u0019\u0012B\u0001\u000b\u0003\u0005YY%/_8TKJL\u0017\r\\5{KJLen\u001d;b]\u000e,\u0007\u0002\u0003\f\u0001\u0005\u0003\u0005\u000b\u0011B\f\u0002\u0011%t7\u000b\u001e:fC6\u0004\"\u0001G\u000f\u000e\u0003eQ!AG\u000e\u0002\u0005%|'\"\u0001\u000f\u0002\t)\fg/Y\u0005\u0003=e\u00111\"\u00138qkR\u001cFO]3b[\"A\u0001\u0005\u0001B\u0001B\u0003%\u0011%A\u0005vg\u0016,fn]1gKB\u0011!%J\u0007\u0002G)\tA%A\u0003tG\u0006d\u0017-\u0003\u0002'G\t9!i\\8mK\u0006t\u0007\"\u0002\u0015\u0001\t\u0003I\u0013A\u0002\u001fj]&$h\b\u0006\u0003+W1j\u0003C\u0001\u0007\u0001\u0011\u0015\u0001r\u00051\u0001\u0013\u0011\u00151r\u00051\u0001\u0018\u0011\u0015\u0001s\u00051\u0001\"\u0011\u0019y\u0003\u0001)Q\u0005a\u0005)\u0011N\u001c9viB\u0011\u0011'O\u0007\u0002e)\u0011!d\r\u0006\u0003iU\nAa\u001b:z_*\u0011agN\u0001\u0011KN|G/\u001a:jGN|g\r^<be\u0016T\u0011\u0001O\u0001\u0004G>l\u0017B\u0001\u001e3\u0005\u0015Ie\u000e];u\u0011\u0019!\u0004\u0001)Q\u0005yA\u0011QHP\u0007\u0002g%\u0011qh\r\u0002\u0005\u0017JLx\u000eC\u0003B\u0001\u0011\u0005#)\u0001\u0006sK\u0006$wJ\u00196fGR,\"aQ$\u0015\u0003\u0011#\"!\u0012)\u0011\u0005\u0019;E\u0002\u0001\u0003\u0006\u0011\u0002\u0013\r!\u0013\u0002\u0002)F\u0011!*\u0014\t\u0003E-K!\u0001T\u0012\u0003\u000f9{G\u000f[5oOB\u0011!ET\u0005\u0003\u001f\u000e\u00121!\u00118z\u0011\u001d\t\u0006)!AA\u0004I\u000b!\"\u001a<jI\u0016t7-\u001a\u00133!\r\u0019f+R\u0007\u0002)*\u0011QkI\u0001\be\u00164G.Z2u\u0013\t9FK\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011\u0015I\u0006\u0001\"\u0011[\u0003\u0015\u0019Gn\\:f)\u0005Y\u0006C\u0001\u0012]\u0013\ti6E\u0001\u0003V]&$\b"
)
public class KryoDeserializationStream extends DeserializationStream {
   private final KryoSerializerInstance serInstance;
   private Input input;
   private Kryo kryo;

   public <T> T readObject(ClassTag<T> evidence$2) {
      try {
         return (T)this.kryo.readClassAndObject(this.input);
      } catch (Throwable var6) {
         if (var6 instanceof KryoException) {
            KryoException var4 = (KryoException)var6;
            if (var4.getMessage().toLowerCase(Locale.ROOT).contains("buffer underflow")) {
               throw new EOFException();
            }
         }

         throw var6;
      }
   }

   public void close() {
      if (this.input != null) {
         try {
            this.input.close();
         } finally {
            this.serInstance.releaseKryo(this.kryo);
            this.kryo = null;
            this.input = null;
         }
      }

   }

   public KryoDeserializationStream(KryoSerializerInstance serInstance, InputStream inStream, boolean useUnsafe) {
      this.serInstance = serInstance;
      this.input = (Input)(useUnsafe ? new UnsafeInput(inStream) : new Input(inStream));
      this.kryo = serInstance.borrowKryo();
   }
}
