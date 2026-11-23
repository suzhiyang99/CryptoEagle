package org.apache.spark.serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.spark.util.ByteBufferInputStream;
import org.apache.spark.util.ByteBufferOutputStream;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015a!B\u0001\u0003\u0001\u0011Q!A\u0006&bm\u0006\u001cVM]5bY&TXM]%ogR\fgnY3\u000b\u0005\r!\u0011AC:fe&\fG.\u001b>fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011A\"D\u0007\u0002\u0005%\u0011aB\u0001\u0002\u0013'\u0016\u0014\u0018.\u00197ju\u0016\u0014\u0018J\\:uC:\u001cW\r\u0003\u0005\u0011\u0001\t\u0005\t\u0015!\u0003\u0013\u00031\u0019w.\u001e8uKJ\u0014Vm]3u\u0007\u0001\u0001\"a\u0005\f\u000e\u0003QQ\u0011!F\u0001\u0006g\u000e\fG.Y\u0005\u0003/Q\u00111!\u00138u\u0011!I\u0002A!A!\u0002\u0013Q\u0012AD3yiJ\fG)\u001a2vO&sgm\u001c\t\u0003'mI!\u0001\b\u000b\u0003\u000f\t{w\u000e\\3b]\"Aa\u0004\u0001B\u0001B\u0003%q$\u0001\neK\u001a\fW\u000f\u001c;DY\u0006\u001c8\u000fT8bI\u0016\u0014\bC\u0001\u0011&\u001b\u0005\t#B\u0001\u0012$\u0003\u0011a\u0017M\\4\u000b\u0003\u0011\nAA[1wC&\u0011a%\t\u0002\f\u00072\f7o\u001d'pC\u0012,'\u000fC\u0003)\u0001\u0011\u0005\u0011&\u0001\u0004=S:LGO\u0010\u000b\u0005U-bS\u0006\u0005\u0002\r\u0001!)\u0001c\na\u0001%!)\u0011d\na\u00015!)ad\na\u0001?!)q\u0006\u0001C!a\u0005I1/\u001a:jC2L'0Z\u000b\u0003c\r#\"A\r'\u0015\u0005MJ\u0004C\u0001\u001b8\u001b\u0005)$B\u0001\u001c$\u0003\rq\u0017n\\\u0005\u0003qU\u0012!BQ=uK\n+hMZ3s\u0011\u001dQd&!AA\u0004m\n!\"\u001a<jI\u0016t7-\u001a\u00134!\rat(Q\u0007\u0002{)\u0011a\bF\u0001\be\u00164G.Z2u\u0013\t\u0001UH\u0001\u0005DY\u0006\u001c8\u000fV1h!\t\u00115\t\u0004\u0001\u0005\u000b\u0011s#\u0019A#\u0003\u0003Q\u000b\"AR%\u0011\u0005M9\u0015B\u0001%\u0015\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"a\u0005&\n\u0005-#\"aA!os\")QJ\fa\u0001\u0003\u0006\tA\u000fC\u0003P\u0001\u0011\u0005\u0003+A\u0006eKN,'/[1mSj,WCA)U)\t\u0011\u0006\f\u0006\u0002T+B\u0011!\t\u0016\u0003\u0006\t:\u0013\r!\u0012\u0005\b-:\u000b\t\u0011q\u0001X\u0003))g/\u001b3f]\u000e,G\u0005\u000e\t\u0004y}\u001a\u0006\"B-O\u0001\u0004\u0019\u0014!\u00022zi\u0016\u001c\b\"B(\u0001\t\u0003ZVC\u0001/`)\ri6\r\u001a\u000b\u0003=\u0002\u0004\"AQ0\u0005\u000b\u0011S&\u0019A#\t\u000f\u0005T\u0016\u0011!a\u0002E\u0006QQM^5eK:\u001cW\rJ\u001b\u0011\u0007qzd\fC\u0003Z5\u0002\u00071\u0007C\u0003f5\u0002\u0007q$\u0001\u0004m_\u0006$WM\u001d\u0005\u0006O\u0002!\t\u0005[\u0001\u0010g\u0016\u0014\u0018.\u00197ju\u0016\u001cFO]3b[R\u0011\u0011\u000e\u001c\t\u0003\u0019)L!a\u001b\u0002\u0003'M+'/[1mSj\fG/[8o'R\u0014X-Y7\t\u000b54\u0007\u0019\u00018\u0002\u0003M\u0004\"a\u001c:\u000e\u0003AT!!]\u0012\u0002\u0005%|\u0017BA:q\u00051yU\u000f\u001e9viN#(/Z1n\u0011\u0015)\b\u0001\"\u0011w\u0003E!Wm]3sS\u0006d\u0017N_3TiJ,\u0017-\u001c\u000b\u0003oj\u0004\"\u0001\u0004=\n\u0005e\u0014!!\u0006#fg\u0016\u0014\u0018.\u00197ju\u0006$\u0018n\u001c8TiJ,\u0017-\u001c\u0005\u0006[R\u0004\ra\u001f\t\u0003_rL!! 9\u0003\u0017%s\u0007/\u001e;TiJ,\u0017-\u001c\u0005\u0006k\u0002!\ta \u000b\u0006o\u0006\u0005\u00111\u0001\u0005\u0006[z\u0004\ra\u001f\u0005\u0006Kz\u0004\ra\b"
)
public class JavaSerializerInstance extends SerializerInstance {
   private final int counterReset;
   private final boolean extraDebugInfo;
   private final ClassLoader defaultClassLoader;

   public <T> ByteBuffer serialize(T t, ClassTag<T> evidence$3) {
      ByteBufferOutputStream bos = new ByteBufferOutputStream();
      SerializationStream out = this.serializeStream(bos);
      out.writeObject(t, evidence$3);
      out.close();
      return bos.toByteBuffer();
   }

   public <T> T deserialize(ByteBuffer bytes, ClassTag<T> evidence$4) {
      ByteBufferInputStream bis = new ByteBufferInputStream(bytes);
      DeserializationStream in = this.deserializeStream(bis);
      return (T)in.readObject(evidence$4);
   }

   public <T> T deserialize(ByteBuffer bytes, ClassLoader loader, ClassTag<T> evidence$5) {
      ByteBufferInputStream bis = new ByteBufferInputStream(bytes);
      DeserializationStream in = this.deserializeStream(bis, loader);
      return (T)in.readObject(evidence$5);
   }

   public SerializationStream serializeStream(OutputStream s) {
      return new JavaSerializationStream(s, this.counterReset, this.extraDebugInfo);
   }

   public DeserializationStream deserializeStream(InputStream s) {
      return new JavaDeserializationStream(s, this.defaultClassLoader);
   }

   public DeserializationStream deserializeStream(InputStream s, ClassLoader loader) {
      return new JavaDeserializationStream(s, loader);
   }

   public JavaSerializerInstance(int counterReset, boolean extraDebugInfo, ClassLoader defaultClassLoader) {
      this.counterReset = counterReset;
      this.extraDebugInfo = extraDebugInfo;
      this.defaultClassLoader = defaultClassLoader;
   }
}
