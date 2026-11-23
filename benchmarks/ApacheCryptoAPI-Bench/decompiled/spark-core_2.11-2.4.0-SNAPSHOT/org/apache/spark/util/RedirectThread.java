package org.apache.spark.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.util.control.Exception$;

@ScalaSignature(
   bytes = "\u0006\u0001a3Q!\u0001\u0002\u0001\t)\u0011aBU3eSJ,7\r\u001e+ie\u0016\fGM\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\ta\u0011#D\u0001\u000e\u0015\tqq\"\u0001\u0003mC:<'\"\u0001\t\u0002\t)\fg/Y\u0005\u0003%5\u0011a\u0001\u00165sK\u0006$\u0007\u0002\u0003\u000b\u0001\u0005\u0003\u0005\u000b\u0011\u0002\f\u0002\u0005%t7\u0001\u0001\t\u0003/ii\u0011\u0001\u0007\u0006\u00033=\t!![8\n\u0005mA\"aC%oaV$8\u000b\u001e:fC6D\u0001\"\b\u0001\u0003\u0002\u0003\u0006IAH\u0001\u0004_V$\bCA\f \u0013\t\u0001\u0003D\u0001\u0007PkR\u0004X\u000f^*ue\u0016\fW\u000e\u0003\u0005#\u0001\t\u0005\t\u0015!\u0003$\u0003\u0011q\u0017-\\3\u0011\u0005\u0011RcBA\u0013)\u001b\u00051#\"A\u0014\u0002\u000bM\u001c\u0017\r\\1\n\u0005%2\u0013A\u0002)sK\u0012,g-\u0003\u0002,Y\t11\u000b\u001e:j]\u001eT!!\u000b\u0014\t\u00119\u0002!\u0011!Q\u0001\n=\nA\u0002\u001d:pa\u0006<\u0017\r^3F_\u001a\u0004\"!\n\u0019\n\u0005E2#a\u0002\"p_2,\u0017M\u001c\u0005\u0006g\u0001!\t\u0001N\u0001\u0007y%t\u0017\u000e\u001e \u0015\u000bU:\u0004(\u000f\u001e\u0011\u0005Y\u0002Q\"\u0001\u0002\t\u000bQ\u0011\u0004\u0019\u0001\f\t\u000bu\u0011\u0004\u0019\u0001\u0010\t\u000b\t\u0012\u0004\u0019A\u0012\t\u000f9\u0012\u0004\u0013!a\u0001_!)A\b\u0001C!{\u0005\u0019!/\u001e8\u0015\u0003y\u0002\"!J \n\u0005\u00013#\u0001B+oSR<\u0001B\u0011\u0002\u0002\u0002#\u0005AaQ\u0001\u000f%\u0016$\u0017N]3diRC'/Z1e!\t1DI\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0003F'\t!e\t\u0005\u0002&\u000f&\u0011\u0001J\n\u0002\u0007\u0003:L(+\u001a4\t\u000bM\"E\u0011\u0001&\u0015\u0003\rCq\u0001\u0014#\u0012\u0002\u0013\u0005Q*A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H\u0005N\u000b\u0002\u001d*\u0012qfT\u0016\u0002!B\u0011\u0011KV\u0007\u0002%*\u00111\u000bV\u0001\nk:\u001c\u0007.Z2lK\u0012T!!\u0016\u0014\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002X%\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3"
)
public class RedirectThread extends Thread {
   public final InputStream org$apache$spark$util$RedirectThread$$in;
   public final OutputStream org$apache$spark$util$RedirectThread$$out;
   public final boolean org$apache$spark$util$RedirectThread$$propagateEof;

   public static boolean $lessinit$greater$default$4() {
      return RedirectThread$.MODULE$.$lessinit$greater$default$4();
   }

   public void run() {
      Exception$.MODULE$.ignoring(Predef$.MODULE$.wrapRefArray((Object[])(new Class[]{IOException.class}))).apply(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RedirectThread $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            Utils$.MODULE$.tryWithSafeFinally(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  byte[] buf = new byte[1024];

                  for(int len = this.$outer.org$apache$spark$util$RedirectThread$$anonfun$$$outer().org$apache$spark$util$RedirectThread$$in.read(buf); len != -1; len = this.$outer.org$apache$spark$util$RedirectThread$$anonfun$$$outer().org$apache$spark$util$RedirectThread$$in.read(buf)) {
                     this.$outer.org$apache$spark$util$RedirectThread$$anonfun$$$outer().org$apache$spark$util$RedirectThread$$out.write(buf, 0, len);
                     this.$outer.org$apache$spark$util$RedirectThread$$anonfun$$$outer().org$apache$spark$util$RedirectThread$$out.flush();
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  if (this.$outer.org$apache$spark$util$RedirectThread$$anonfun$$$outer().org$apache$spark$util$RedirectThread$$propagateEof) {
                     this.$outer.org$apache$spark$util$RedirectThread$$anonfun$$$outer().org$apache$spark$util$RedirectThread$$out.close();
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         // $FF: synthetic method
         public RedirectThread org$apache$spark$util$RedirectThread$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public RedirectThread(InputStream in, OutputStream out, String name, boolean propagateEof) {
      super(name);
      this.org$apache$spark$util$RedirectThread$$in = in;
      this.org$apache$spark$util$RedirectThread$$out = out;
      this.org$apache$spark$util$RedirectThread$$propagateEof = propagateEof;
      this.setDaemon(true);
   }
}
