package org.apache.spark.util;

import java.util.PriorityQueue;
import scala.Function0;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ObjectRef;
import scala.util.Try$;

@ScalaSignature(
   bytes = "\u0006\u0001E3Q!\u0001\u0002\u0001\u0005)\u0011\u0001d\u00159be.\u001c\u0006.\u001e;e_^t\u0007j\\8l\u001b\u0006t\u0017mZ3s\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\rC\u0003\u0013\u0001\u0011\u0005A#\u0001\u0004=S:LGOP\u0002\u0001)\u0005)\u0002C\u0001\f\u0001\u001b\u0005\u0011\u0001b\u0002\r\u0001\u0005\u0004%I!G\u0001\u0006Q>|7n]\u000b\u00025A\u00191dH\u0011\u000e\u0003qQ!aA\u000f\u000b\u0003y\tAA[1wC&\u0011\u0001\u0005\b\u0002\u000e!JLwN]5usF+X-^3\u0011\u0005Y\u0011\u0013BA\u0012\u0003\u0005E\u0019\u0006/\u0019:l'\",H\u000fZ8x]\"{wn\u001b\u0005\u0007K\u0001\u0001\u000b\u0011\u0002\u000e\u0002\r!|wn[:!\u0011\u001d9\u0003\u00011A\u0005\n!\nAb\u001d5viRLgn\u001a#po:,\u0012!\u000b\t\u0003\u0019)J!aK\u0007\u0003\u000f\t{w\u000e\\3b]\"9Q\u0006\u0001a\u0001\n\u0013q\u0013\u0001E:ikR$\u0018N\\4E_^tw\fJ3r)\ty#\u0007\u0005\u0002\ra%\u0011\u0011'\u0004\u0002\u0005+:LG\u000fC\u00044Y\u0005\u0005\t\u0019A\u0015\u0002\u0007a$\u0013\u0007\u0003\u00046\u0001\u0001\u0006K!K\u0001\u000eg\",H\u000f^5oO\u0012{wO\u001c\u0011)\u0005Q:\u0004C\u0001\u00079\u0013\tITB\u0001\u0005w_2\fG/\u001b7f\u0011\u0015Y\u0004\u0001\"\u0001=\u0003\u001dIgn\u001d;bY2$\u0012a\f\u0005\u0006}\u0001!\t\u0001P\u0001\u0007eVt\u0017\t\u001c7\t\u000b\u0001\u0003A\u0011A!\u0002\u0007\u0005$G\rF\u0002\f\u0005\u001eCQaQ A\u0002\u0011\u000b\u0001\u0002\u001d:j_JLG/\u001f\t\u0003\u0019\u0015K!AR\u0007\u0003\u0007%sG\u000fC\u0003I\u007f\u0001\u0007\u0011*\u0001\u0003i_>\\\u0007c\u0001\u0007K_%\u00111*\u0004\u0002\n\rVt7\r^5p]BBQ!\u0014\u0001\u0005\u00029\u000baA]3n_Z,GCA\u0015P\u0011\u0015\u0001F\n1\u0001\f\u0003\r\u0011XM\u001a"
)
public class SparkShutdownHookManager {
   private final PriorityQueue<SparkShutdownHook> hooks = new PriorityQueue();
   private volatile boolean shuttingDown = false;

   private PriorityQueue<SparkShutdownHook> hooks() {
      return this.hooks;
   }

   private boolean shuttingDown() {
      return this.shuttingDown;
   }

   private void shuttingDown_$eq(boolean x$1) {
      this.shuttingDown = x$1;
   }

   public void install() {
      Runnable hookTask = new Runnable(this) {
         // $FF: synthetic field
         private final SparkShutdownHookManager $outer;

         public void run() {
            this.$outer.runAll();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      org.apache.hadoop.util.ShutdownHookManager.get().addShutdownHook(hookTask, 40);
   }

   public void runAll() {
      this.shuttingDown_$eq(true);
      ObjectRef nextHook = ObjectRef.create((Object)null);

      while(true) {
         Object var3;
         synchronized(this.hooks()) {
            var3 = this.hooks().poll();
         }

         nextHook.elem = (T)((SparkShutdownHook)var3);
         if ((SparkShutdownHook)nextHook.elem == null) {
            return;
         }

         Try$.MODULE$.apply(new Serializable(this, nextHook) {
            public static final long serialVersionUID = 0L;
            public final ObjectRef nextHook$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               Utils$.MODULE$.logUncaughtExceptions(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     ((SparkShutdownHook)this.$outer.nextHook$1.elem).run();
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

            public {
               this.nextHook$1 = nextHook$1;
            }
         });
      }
   }

   public Object add(int priority, Function0<BoxedUnit> hook) {
      synchronized(this.hooks()) {
         if (this.shuttingDown()) {
            throw new IllegalStateException("Shutdown hooks cannot be modified during shutdown.");
         } else {
            SparkShutdownHook hookRef = new SparkShutdownHook(priority, hook);
            this.hooks().add(hookRef);
            return hookRef;
         }
      }
   }

   public boolean remove(Object ref) {
      Boolean var3;
      synchronized(this.hooks()) {
         var3 = BoxesRunTime.boxToBoolean(this.hooks().remove(ref));
      }

      return BoxesRunTime.unboxToBoolean(var3);
   }
}
