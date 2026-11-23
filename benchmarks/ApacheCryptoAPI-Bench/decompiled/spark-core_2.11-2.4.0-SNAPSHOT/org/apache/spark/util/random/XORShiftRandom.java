package org.apache.spark.util.random;

import java.util.Random;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A4Q!\u0001\u0002\u0001\r1\u0011a\u0002W(S'\"Lg\r\u001e*b]\u0012|WN\u0003\u0002\u0004\t\u00051!/\u00198e_6T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001dIi\u0011a\u0004\u0006\u0003\u000bAQ\u0011!E\u0001\u0005U\u00064\u0018-\u0003\u0002\u0014\u001f\t1!+\u00198e_6D\u0001\"\u0006\u0001\u0003\u0002\u0003\u0006IaF\u0001\u0005S:LGo\u0001\u0001\u0011\u0005aYR\"A\r\u000b\u0003i\tQa]2bY\u0006L!\u0001H\r\u0003\t1{gn\u001a\u0005\u0006=\u0001!\taH\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\u0001\u0012\u0003CA\u0011\u0001\u001b\u0005\u0011\u0001\"B\u000b\u001e\u0001\u00049\u0002\"\u0002\u0010\u0001\t\u0003!C#\u0001\u0011\t\u000f\u0019\u0002\u0001\u0019!C\u0005O\u0005!1/Z3e+\u00059\u0002bB\u0015\u0001\u0001\u0004%IAK\u0001\tg\u0016,Gm\u0018\u0013fcR\u00111F\f\t\u000311J!!L\r\u0003\tUs\u0017\u000e\u001e\u0005\b_!\n\t\u00111\u0001\u0018\u0003\rAH%\r\u0005\u0007c\u0001\u0001\u000b\u0015B\f\u0002\u000bM,W\r\u001a\u0011\t\u000bM\u0002A\u0011\u000b\u001b\u0002\t9,\u0007\u0010\u001e\u000b\u0003ka\u0002\"\u0001\u0007\u001c\n\u0005]J\"aA%oi\")\u0011H\ra\u0001k\u0005!!-\u001b;t\u0011\u0015Y\u0004\u0001\"\u0011=\u0003\u001d\u0019X\r^*fK\u0012$\"aK\u001f\t\u000byR\u0004\u0019A\f\u0002\u0003M<a\u0001\u0011\u0002\t\u0002\u0019\t\u0015A\u0004-P%NC\u0017N\u001a;SC:$w.\u001c\t\u0003C\t3a!\u0001\u0002\t\u0002\u0019\u00195c\u0001\"E\u000fB\u0011\u0001$R\u0005\u0003\rf\u0011a!\u00118z%\u00164\u0007C\u0001\rI\u0013\tI\u0015D\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0003\u001f\u0005\u0012\u00051\nF\u0001B\u0011\u0019i%\t\"\u0001\u0003\u001d\u0006A\u0001.Y:i'\u0016,G\r\u0006\u0002\u0018\u001f\")a\u0005\u0014a\u0001/!)\u0011K\u0011C\u0001%\u0006!Q.Y5o)\tY3\u000bC\u0003U!\u0002\u0007Q+\u0001\u0003be\u001e\u001c\bc\u0001\rW1&\u0011q+\u0007\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u00033rs!\u0001\u0007.\n\u0005mK\u0012A\u0002)sK\u0012,g-\u0003\u0002^=\n11\u000b\u001e:j]\u001eT!aW\r\t\u000b\u0001\u0014E\u0011A1\u0002\u0013\t,gn\u00195nCJ\\GC\u00012f!\u0011I6\rW\f\n\u0005\u0011t&aA'ba\")am\u0018a\u0001k\u0005Aa.^7Ji\u0016\u00148\u000fC\u0004i\u0005\u0006\u0005I\u0011B5\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0002UB\u00111N\\\u0007\u0002Y*\u0011Q\u000eE\u0001\u0005Y\u0006tw-\u0003\u0002pY\n1qJ\u00196fGR\u0004"
)
public class XORShiftRandom extends Random {
   private long seed;

   public static Map<String, Object> benchmark(int var0) {
      return XORShiftRandom$.MODULE$.benchmark(var0);
   }

   public static void main(String[] var0) {
      XORShiftRandom$.MODULE$.main(var0);
   }

   private long seed() {
      return this.seed;
   }

   private void seed_$eq(long x$1) {
      this.seed = x$1;
   }

   public int next(int bits) {
      long nextSeed = this.seed() ^ this.seed() << 21;
      nextSeed ^= nextSeed >>> 35;
      nextSeed ^= nextSeed << 4;
      this.seed_$eq(nextSeed);
      return (int)(nextSeed & (1L << bits) - 1L);
   }

   public void setSeed(long s) {
      this.seed_$eq(XORShiftRandom$.MODULE$.hashSeed(s));
   }

   public XORShiftRandom(long init) {
      super(init);
      this.seed = XORShiftRandom$.MODULE$.hashSeed(init);
   }

   public XORShiftRandom() {
      this(System.nanoTime());
   }
}
