package org.apache.spark.util.random;

import java.nio.ByteBuffer;
import java.util.Random;
import org.apache.spark.util.Utils$;
import scala.Function0;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.immutable.Map;
import scala.collection.immutable.Map$;
import scala.collection.immutable.StringOps;
import scala.runtime.BoxesRunTime;
import scala.util.hashing.MurmurHash3$;

public final class XORShiftRandom$ implements Serializable {
   public static final XORShiftRandom$ MODULE$;

   static {
      new XORShiftRandom$();
   }

   public long hashSeed(long seed) {
      byte[] bytes = ByteBuffer.allocate(64).putLong(seed).array();
      int lowBits = MurmurHash3$.MODULE$.bytesHash(bytes);
      int highBits = MurmurHash3$.MODULE$.bytesHash(bytes, lowBits);
      return (long)highBits << 32 | (long)lowBits & 4294967295L;
   }

   public void main(String[] args) {
      if (args.length != 1) {
         Predef$.MODULE$.println("Benchmark of XORShiftRandom vis-a-vis java.util.Random");
         Predef$.MODULE$.println("Usage: XORShiftRandom number_of_random_numbers_to_generate");
         System.exit(1);
      }

      Predef$.MODULE$.println(this.benchmark((new StringOps(Predef$.MODULE$.augmentString(args[0]))).toInt()));
   }

   public Map<String, Object> benchmark(int numIters) {
      long seed = 1L;
      int million = (int)(double)1000000.0F;
      Random javaRand = new Random(seed);
      XORShiftRandom xorRand = new XORShiftRandom(seed);
      Function0 x$2 = new Serializable(javaRand, xorRand) {
         public static final long serialVersionUID = 0L;
         private final Random javaRand$1;
         private final XORShiftRandom xorRand$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.javaRand$1.nextInt();
            this.xorRand$1.nextInt();
         }

         public {
            this.javaRand$1 = javaRand$1;
            this.xorRand$1 = xorRand$1;
         }
      };
      Option x$3 = Utils$.MODULE$.timeIt$default$3(million);
      Utils$.MODULE$.timeIt(million, x$2, x$3);
      Map$ var10000 = Predef$.MODULE$.Map();
      Predef$ var10001 = Predef$.MODULE$;
      Tuple2[] var10002 = new Tuple2[2];
      Predef.ArrowAssoc$ var10005 = Predef.ArrowAssoc$.MODULE$;
      Object var10006 = Predef$.MODULE$.ArrowAssoc("javaTime");
      Function0 x$5 = new Serializable(javaRand) {
         public static final long serialVersionUID = 0L;
         private final Random javaRand$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.javaRand$1.nextInt();
         }

         public {
            this.javaRand$1 = javaRand$1;
         }
      };
      Option x$6 = Utils$.MODULE$.timeIt$default$3(numIters);
      var10002[0] = var10005.$minus$greater$extension(var10006, BoxesRunTime.boxToLong(Utils$.MODULE$.timeIt(numIters, x$5, x$6)));
      var10005 = Predef.ArrowAssoc$.MODULE$;
      var10006 = Predef$.MODULE$.ArrowAssoc("xorTime");
      Function0 x$8 = new Serializable(xorRand) {
         public static final long serialVersionUID = 0L;
         private final XORShiftRandom xorRand$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.xorRand$1.nextInt();
         }

         public {
            this.xorRand$1 = xorRand$1;
         }
      };
      Option x$9 = Utils$.MODULE$.timeIt$default$3(numIters);
      var10002[1] = var10005.$minus$greater$extension(var10006, BoxesRunTime.boxToLong(Utils$.MODULE$.timeIt(numIters, x$8, x$9)));
      return (Map)var10000.apply(var10001.wrapRefArray(var10002));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private XORShiftRandom$() {
      MODULE$ = this;
   }
}
