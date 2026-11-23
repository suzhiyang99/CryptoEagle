package org.apache.spark.serializer;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.annotation.Private;
import scala.None$;
import scala.Option;
import scala.Some;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001=3Q!\u0001\u0002\u0002\u0002-\u0011!bU3sS\u0006d\u0017N_3s\u0015\t\u0019A!\u0001\u0006tKJL\u0017\r\\5{KJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\t\u0001A\u0002\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\u0005\u0006'\u0001!\t\u0001F\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003U\u0001\"A\u0006\u0001\u000e\u0003\tAq\u0001\u0007\u0001A\u0002\u0013E\u0011$\u0001\neK\u001a\fW\u000f\u001c;DY\u0006\u001c8\u000fT8bI\u0016\u0014X#\u0001\u000e\u0011\u00075YR$\u0003\u0002\u001d\u001d\t1q\n\u001d;j_:\u0004\"AH\u0012\u000e\u0003}Q!\u0001I\u0011\u0002\t1\fgn\u001a\u0006\u0002E\u0005!!.\u0019<b\u0013\t!sDA\u0006DY\u0006\u001c8\u000fT8bI\u0016\u0014\bb\u0002\u0014\u0001\u0001\u0004%\tbJ\u0001\u0017I\u00164\u0017-\u001e7u\u00072\f7o\u001d'pC\u0012,'o\u0018\u0013fcR\u0011\u0001f\u000b\t\u0003\u001b%J!A\u000b\b\u0003\tUs\u0017\u000e\u001e\u0005\bY\u0015\n\t\u00111\u0001\u001b\u0003\rAH%\r\u0005\u0007]\u0001\u0001\u000b\u0015\u0002\u000e\u0002'\u0011,g-Y;mi\u000ec\u0017m]:M_\u0006$WM\u001d\u0011)\u00055\u0002\u0004CA\u00072\u0013\t\u0011dB\u0001\u0005w_2\fG/\u001b7f\u0011\u0015!\u0004\u0001\"\u00016\u0003U\u0019X\r\u001e#fM\u0006,H\u000e^\"mCN\u001cHj\\1eKJ$\"!\u0006\u001c\t\u000b]\u001a\u0004\u0019A\u000f\u0002\u0017\rd\u0017m]:M_\u0006$WM\u001d\u0005\u0006s\u00011\tAO\u0001\f]\u0016<\u0018J\\:uC:\u001cW\rF\u0001<!\t1B(\u0003\u0002>\u0005\t\u00112+\u001a:jC2L'0\u001a:J]N$\u0018M\\2f\u0011\u0019y\u0004\u0001\"\u0001\u0005\u0001\u0006)3/\u001e9q_J$8OU3m_\u000e\fG/[8o\u001f\u001a\u001cVM]5bY&TX\rZ(cU\u0016\u001cGo]\u000b\u0002\u0003B\u0011QBQ\u0005\u0003\u0007:\u0011qAQ8pY\u0016\fg\u000e\u000b\u0002?\u000bB\u0011a)S\u0007\u0002\u000f*\u0011\u0001\nB\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001&H\u0005\u001d\u0001&/\u001b<bi\u0016D#\u0001\u0001'\u0011\u0005\u0019k\u0015B\u0001(H\u00051!UM^3m_B,'/\u00119j\u0001"
)
public abstract class Serializer {
   private volatile Option<ClassLoader> defaultClassLoader;

   public Option<ClassLoader> defaultClassLoader() {
      return this.defaultClassLoader;
   }

   public void defaultClassLoader_$eq(Option<ClassLoader> x$1) {
      this.defaultClassLoader = x$1;
   }

   public Serializer setDefaultClassLoader(ClassLoader classLoader) {
      this.defaultClassLoader_$eq(new Some(classLoader));
      return this;
   }

   public abstract SerializerInstance newInstance();

   @Private
   public boolean supportsRelocationOfSerializedObjects() {
      return false;
   }

   public Serializer() {
      this.defaultClassLoader = None$.MODULE$;
   }
}
