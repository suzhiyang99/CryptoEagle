package org.apache.spark.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001daAB\u0001\u0003\u0003\u0003!!BA\u0005Fm\u0016tG\u000fT8pa*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h+\tY\u0011fE\u0002\u0001\u0019I\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\n\u0017\u001b\u0005!\"BA\u000b\u0005\u0003!Ig\u000e^3s]\u0006d\u0017BA\f\u0015\u0005\u001daunZ4j]\u001eD\u0001\"\u0007\u0001\u0003\u0002\u0003\u0006IaG\u0001\u0005]\u0006lWm\u0001\u0001\u0011\u0005qybBA\u0007\u001e\u0013\tqb\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003A\u0005\u0012aa\u0015;sS:<'B\u0001\u0010\u000f\u0011\u0015\u0019\u0003\u0001\"\u0001%\u0003\u0019a\u0014N\\5u}Q\u0011QE\r\t\u0004M\u00019S\"\u0001\u0002\u0011\u0005!JC\u0002\u0001\u0003\u0006U\u0001\u0011\ra\u000b\u0002\u0002\u000bF\u0011Af\f\t\u0003\u001b5J!A\f\b\u0003\u000f9{G\u000f[5oOB\u0011Q\u0002M\u0005\u0003c9\u00111!\u00118z\u0011\u0015I\"\u00051\u0001\u001c\u0011\u001d!\u0004A1A\u0005\nU\n!\"\u001a<f]R\fV/Z;f+\u00051\u0004cA\u001c>O5\t\u0001H\u0003\u0002:u\u0005Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005\rY$\"\u0001\u001f\u0002\t)\fg/Y\u0005\u0003}a\u0012QB\u00117pG.LgnZ)vKV,\u0007B\u0002!\u0001A\u0003%a'A\u0006fm\u0016tG/U;fk\u0016\u0004\u0003b\u0002\"\u0001\u0005\u0004%IaQ\u0001\bgR|\u0007\u000f]3e+\u0005!\u0005CA#I\u001b\u00051%BA$9\u0003\u0019\tGo\\7jG&\u0011\u0011J\u0012\u0002\u000e\u0003R|W.[2C_>dW-\u00198\t\r-\u0003\u0001\u0015!\u0003E\u0003!\u0019Ho\u001c9qK\u0012\u0004\u0003bB'\u0001\u0005\u0004%IAT\u0001\fKZ,g\u000e\u001e+ie\u0016\fG-F\u0001P!\t\u00016+D\u0001R\u0015\t\u00116(\u0001\u0003mC:<\u0017B\u0001+R\u0005\u0019!\u0006N]3bI\"1a\u000b\u0001Q\u0001\n=\u000bA\"\u001a<f]R$\u0006N]3bI\u0002BQ\u0001\u0017\u0001\u0005\u0002e\u000bQa\u001d;beR$\u0012A\u0017\t\u0003\u001bmK!\u0001\u0018\b\u0003\tUs\u0017\u000e\u001e\u0005\u0006=\u0002!\t!W\u0001\u0005gR|\u0007\u000fC\u0003a\u0001\u0011\u0005\u0011-\u0001\u0003q_N$HC\u0001.c\u0011\u0015\u0019w\f1\u0001(\u0003\u0015)g/\u001a8u\u0011\u0015)\u0007\u0001\"\u0001g\u0003!I7/Q2uSZ,W#A4\u0011\u00055A\u0017BA5\u000f\u0005\u001d\u0011un\u001c7fC:DQa\u001b\u0001\u0005\u0012e\u000bqa\u001c8Ti\u0006\u0014H\u000fC\u0003n\u0001\u0011E\u0011,\u0001\u0004p]N#x\u000e\u001d\u0005\u0006_\u00021\t\u0002]\u0001\n_:\u0014VmY3jm\u0016$\"AW9\t\u000b\rt\u0007\u0019A\u0014\t\u000bM\u0004a\u0011\u0003;\u0002\u000f=tWI\u001d:peR\u0011!,\u001e\u0005\u0006mJ\u0004\ra^\u0001\u0002KB\u0019\u00010!\u0001\u000f\u0005ethB\u0001>~\u001b\u0005Y(B\u0001?\u001b\u0003\u0019a$o\\8u}%\tq\"\u0003\u0002\u0000\u001d\u00059\u0001/Y2lC\u001e,\u0017\u0002BA\u0002\u0003\u000b\u0011\u0011\u0002\u00165s_^\f'\r\\3\u000b\u0005}t\u0001"
)
public abstract class EventLoop<E> implements Logging {
   public final String org$apache$spark$util$EventLoop$$name;
   private final BlockingQueue<E> org$apache$spark$util$EventLoop$$eventQueue;
   private final AtomicBoolean org$apache$spark$util$EventLoop$$stopped;
   private final Thread eventThread;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public BlockingQueue<E> org$apache$spark$util$EventLoop$$eventQueue() {
      return this.org$apache$spark$util$EventLoop$$eventQueue;
   }

   public AtomicBoolean org$apache$spark$util$EventLoop$$stopped() {
      return this.org$apache$spark$util$EventLoop$$stopped;
   }

   private Thread eventThread() {
      return this.eventThread;
   }

   public void start() {
      if (this.org$apache$spark$util$EventLoop$$stopped().get()) {
         throw new IllegalStateException((new StringBuilder()).append((Object)this.org$apache$spark$util$EventLoop$$name).append((Object)" has already been stopped").toString());
      } else {
         this.onStart();
         this.eventThread().start();
      }
   }

   public void stop() {
      if (this.org$apache$spark$util$EventLoop$$stopped().compareAndSet(false, true)) {
         this.eventThread().interrupt();
         boolean onStopCalled = false;

         try {
            this.eventThread().join();
            onStopCalled = true;
            this.onStop();
         } catch (InterruptedException var3) {
            Thread.currentThread().interrupt();
            if (!onStopCalled) {
               this.onStop();
            }
         }
      }

   }

   public void post(E event) {
      this.org$apache$spark$util$EventLoop$$eventQueue().put(event);
   }

   public boolean isActive() {
      return this.eventThread().isAlive();
   }

   public void onStart() {
   }

   public void onStop() {
   }

   public abstract void onReceive(E var1);

   public abstract void onError(Throwable var1);

   public EventLoop(String name) {
      this.org$apache$spark$util$EventLoop$$name = name;
      Logging$class.$init$(this);
      this.org$apache$spark$util$EventLoop$$eventQueue = new LinkedBlockingDeque();
      this.org$apache$spark$util$EventLoop$$stopped = new AtomicBoolean(false);
      this.eventThread = new Thread(this) {
         // $FF: synthetic field
         private final EventLoop $outer;

         public void run() {
            label178:
            try {
               while(!this.$outer.org$apache$spark$util$EventLoop$$stopped().get()) {
                  Object event = this.$outer.org$apache$spark$util$EventLoop$$eventQueue().take();

                  try {
                     this.$outer.onReceive(event);
                  } catch (Throwable var27) {
                     Option var9 = NonFatal$.MODULE$.unapply(var27);
                     if (var9.isEmpty()) {
                        throw var27;
                     }

                     Throwable e = (Throwable)var9.get();

                     try {
                        this.$outer.onError(e);
                        BoxedUnit var30 = BoxedUnit.UNIT;
                        continue;
                     } catch (Throwable var26) {
                        Option var14 = NonFatal$.MODULE$.unapply(var26);
                        if (var14.isEmpty()) {
                           throw var26;
                        }

                        Throwable e = (Throwable)var14.get();
                        this.$outer.logError(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"Unexpected error in ").append((Object)this.$outer.org$apache$spark$util$EventLoop$$anon$$$outer().org$apache$spark$util$EventLoop$$name).toString();
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        }, e);
                        BoxedUnit var16 = BoxedUnit.UNIT;
                        BoxedUnit var10000 = BoxedUnit.UNIT;
                        continue;
                     }
                  }
               }
            } catch (Throwable var28) {
               if (var28 instanceof InterruptedException) {
                  BoxedUnit var29 = BoxedUnit.UNIT;
               } else {
                  Option var4 = NonFatal$.MODULE$.unapply(var28);
                  if (var4.isEmpty()) {
                     throw var28;
                  }

                  Throwable e = (Throwable)var4.get();
                  this.$outer.logError(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Unexpected error in ").append((Object)this.$outer.org$apache$spark$util$EventLoop$$anon$$$outer().org$apache$spark$util$EventLoop$$name).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  }, e);
                  BoxedUnit var3 = BoxedUnit.UNIT;
               }
               break label178;
            }

         }

         // $FF: synthetic method
         public EventLoop org$apache$spark$util$EventLoop$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.setDaemon(true);
            }
         }
      };
   }
}
