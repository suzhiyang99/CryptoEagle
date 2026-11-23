package org.apache.spark.util;

import javax.annotation.concurrent.GuardedBy;
import scala.Function0;
import scala.Predef$;
import scala.StringContext;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001Q4Q!\u0001\u0002\u0001\t)\u0011Q#\u00168j]R,'O];qi&\u0014G.\u001a+ie\u0016\fGM\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\ta\u0011#D\u0001\u000e\u0015\tqq\"\u0001\u0003mC:<'\"\u0001\t\u0002\t)\fg/Y\u0005\u0003%5\u0011a\u0001\u00165sK\u0006$\u0007\u0002\u0003\u000b\u0001\u0005\u0003\u0005\u000b\u0011\u0002\f\u0002\rQ\f'oZ3u\u0007\u0001\u0001\"\u0001D\f\n\u0005ai!\u0001\u0003*v]:\f'\r\\3\t\u0011i\u0001!\u0011!Q\u0001\nm\tAA\\1nKB\u0011AD\t\b\u0003;\u0001j\u0011A\b\u0006\u0002?\u0005)1oY1mC&\u0011\u0011EH\u0001\u0007!J,G-\u001a4\n\u0005\r\"#AB*ue&twM\u0003\u0002\"=!)a\u0005\u0001C\u0001O\u00051A(\u001b8jiz\"2\u0001\u000b\u0016,!\tI\u0003!D\u0001\u0003\u0011\u0015!R\u00051\u0001\u0017\u0011\u0015QR\u00051\u0001\u001c\u0011\u00151\u0003\u0001\"\u0001.)\tAc\u0006C\u0003\u001bY\u0001\u00071\u0004C\u00041\u0001\t\u0007I\u0011B\u0019\u0002'Ut\u0017N\u001c;feJ,\b\u000f^5cY\u0016dunY6\u0016\u0003I\u0002\"\u0001D\u001a\n\u0005Qj!AB(cU\u0016\u001cG\u000f\u0003\u00047\u0001\u0001\u0006IAM\u0001\u0015k:Lg\u000e^3seV\u0004H/\u001b2mK2{7m\u001b\u0011\t\u000fa\u0002\u0001\u0019!C\u0005s\u0005yQO\\5oi\u0016\u0014(/\u001e9uS\ndW-F\u0001;!\ti2(\u0003\u0002==\t9!i\\8mK\u0006t\u0007b\u0002 \u0001\u0001\u0004%IaP\u0001\u0014k:Lg\u000e^3seV\u0004H/\u001b2mK~#S-\u001d\u000b\u0003\u0001\u000e\u0003\"!H!\n\u0005\ts\"\u0001B+oSRDq\u0001R\u001f\u0002\u0002\u0003\u0007!(A\u0002yIEBaA\u0012\u0001!B\u0013Q\u0014\u0001E;oS:$XM\u001d:vaRL'\r\\3!Q\u0011)\u0005JU*\u0011\u0005%\u0003V\"\u0001&\u000b\u0005-c\u0015AC2p]\u000e,(O]3oi*\u0011QJT\u0001\u000bC:tw\u000e^1uS>t'\"A(\u0002\u000b)\fg/\u0019=\n\u0005ES%!C$vCJ$W\r\u001a\"z\u0003\u00151\u0018\r\\;fC\u0005\u0001\u0004bB+\u0001\u0001\u0004%I!O\u0001\u0016g\"|W\u000f\u001c3J]R,'O];qiRC'/Z1e\u0011\u001d9\u0006\u00011A\u0005\na\u000b\u0011d\u001d5pk2$\u0017J\u001c;feJ,\b\u000f\u001e+ie\u0016\fGm\u0018\u0013fcR\u0011\u0001)\u0017\u0005\b\tZ\u000b\t\u00111\u0001;\u0011\u0019Y\u0006\u0001)Q\u0005u\u000512\u000f[8vY\u0012Le\u000e^3seV\u0004H\u000f\u00165sK\u0006$\u0007\u0005\u000b\u0003[\u0011J\u001b\u0006\"\u00020\u0001\t\u0003y\u0016A\u0005:v]Vs\u0017N\u001c;feJ,\b\u000f^5cYf,\"\u0001Y2\u0015\u0005\u0005d\u0007C\u00012d\u0019\u0001!Q\u0001Z/C\u0002\u0015\u0014\u0011\u0001V\t\u0003M&\u0004\"!H4\n\u0005!t\"a\u0002(pi\"Lgn\u001a\t\u0003;)L!a\u001b\u0010\u0003\u0007\u0005s\u0017\u0010\u0003\u0004n;\u0012\u0005\rA\\\u0001\u0002MB\u0019Qd\\1\n\u0005At\"\u0001\u0003\u001fcs:\fW.\u001a \t\u000bI\u0004A\u0011I:\u0002\u0013%tG/\u001a:skB$H#\u0001!"
)
public class UninterruptibleThread extends Thread {
   private final Object uninterruptibleLock;
   @GuardedBy("uninterruptibleLock")
   private boolean uninterruptible;
   @GuardedBy("uninterruptibleLock")
   private boolean shouldInterruptThread;

   private Object uninterruptibleLock() {
      return this.uninterruptibleLock;
   }

   private boolean uninterruptible() {
      return this.uninterruptible;
   }

   private void uninterruptible_$eq(boolean x$1) {
      this.uninterruptible = x$1;
   }

   private boolean shouldInterruptThread() {
      return this.shouldInterruptThread;
   }

   private void shouldInterruptThread_$eq(boolean x$1) {
      this.shouldInterruptThread = x$1;
   }

   public <T> T runUninterruptibly(Function0<T> f) {
      Thread var10000 = Thread.currentThread();
      if (var10000 == null) {
         if (this != null) {
            throw new IllegalStateException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Call runUninterruptibly in a wrong thread. "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Expected: ", " but was ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this, Thread.currentThread()}))).toString());
         }
      } else if (!var10000.equals(this)) {
         throw new IllegalStateException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Call runUninterruptibly in a wrong thread. "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Expected: ", " but was ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this, Thread.currentThread()}))).toString());
      }

      Boolean var4;
      synchronized(this.uninterruptibleLock()) {
         var4 = BoxesRunTime.boxToBoolean(this.uninterruptible());
      }

      if (BoxesRunTime.unboxToBoolean(var4)) {
         return (T)f.apply();
      } else {
         synchronized(this.uninterruptibleLock()) {
            this.shouldInterruptThread_$eq(Thread.interrupted() || this.shouldInterruptThread());
            this.uninterruptible_$eq(true);
            BoxedUnit var18 = BoxedUnit.UNIT;
         }

         try {
            var19 = f.apply();
         } finally {
            synchronized(this.uninterruptibleLock()) {
               this.uninterruptible_$eq(false);
               if (this.shouldInterruptThread()) {
                  super.interrupt();
                  this.shouldInterruptThread_$eq(false);
                  BoxedUnit var10001 = BoxedUnit.UNIT;
               } else {
                  BoxedUnit var20 = BoxedUnit.UNIT;
               }

            }
         }

         return (T)var19;
      }
   }

   public void interrupt() {
      synchronized(this.uninterruptibleLock()) {
         if (this.uninterruptible()) {
            this.shouldInterruptThread_$eq(true);
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            super.interrupt();
            BoxedUnit var3 = BoxedUnit.UNIT;
         }

      }
   }

   public UninterruptibleThread(Runnable target, String name) {
      super(target, name);
      this.uninterruptibleLock = new Object();
      this.uninterruptible = false;
      this.shouldInterruptThread = false;
   }

   public UninterruptibleThread(String name) {
      this((Runnable)null, name);
   }
}
