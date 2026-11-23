package org.apache.spark.util;

import com.codahale.metrics.Timer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Buffer$;
import scala.reflect.ClassTag;
import scala.runtime.BoxedUnit;
import scala.util.control.NonFatal$;

public abstract class ListenerBus$class {
   public static List listeners(ListenerBus $this) {
      return (List)JavaConverters$.MODULE$.bufferAsJavaListConverter((Buffer)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter($this.org$apache$spark$util$ListenerBus$$listenersPlusTimers()).asScala()).map(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final L apply(Tuple2<L, Option<Timer>> x$1) {
            return (L)x$1._1();
         }
      }, Buffer$.MODULE$.canBuildFrom())).asJava();
   }

   public static Option getTimer(ListenerBus $this, Object listener) {
      return None$.MODULE$;
   }

   public static final void addListener(ListenerBus $this, Object listener) {
      $this.org$apache$spark$util$ListenerBus$$listenersPlusTimers().add(new Tuple2(listener, $this.getTimer(listener)));
   }

   public static final void removeListener(ListenerBus $this, Object listener) {
      ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter($this.org$apache$spark$util$ListenerBus$$listenersPlusTimers()).asScala()).find(new Serializable($this, listener) {
         public static final long serialVersionUID = 0L;
         private final Object listener$2;

         public final boolean apply(Tuple2<L, Option<Timer>> x$2) {
            return x$2._1() == this.listener$2;
         }

         public {
            this.listener$2 = listener$2;
         }
      }).foreach(new Serializable($this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ListenerBus $outer;

         public final boolean apply(Tuple2<L, Option<Timer>> listenerAndTimer) {
            return this.$outer.org$apache$spark$util$ListenerBus$$listenersPlusTimers().remove(listenerAndTimer);
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

   public static void postToAll(ListenerBus $this, Object event) {
      for(Tuple2 listenerAndMaybeTimer : $this.org$apache$spark$util$ListenerBus$$listenersPlusTimers()) {
         Object listener = listenerAndMaybeTimer._1();
         Option maybeTimer = (Option)listenerAndMaybeTimer._2();
         Timer.Context maybeTimerContext = maybeTimer.isDefined() ? ((Timer)maybeTimer.get()).time() : null;

         try {
            try {
               $this.doPostEvent(listener, event);
            } catch (Throwable var17) {
               Option var10 = NonFatal$.MODULE$.unapply(var17);
               if (var10.isEmpty()) {
                  throw var17;
               }

               Throwable e = (Throwable)var10.get();
               $this.logError(new Serializable($this, listener) {
                  public static final long serialVersionUID = 0L;
                  private final Object listener$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Listener ", " threw an exception"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.getFormattedClassName(this.listener$1)}));
                  }

                  public {
                     this.listener$1 = listener$1;
                  }
               }, e);
               BoxedUnit var12 = BoxedUnit.UNIT;
               continue;
            }
         } finally {
            if (maybeTimerContext != null) {
               maybeTimerContext.stop();
            }

         }
      }

   }

   public static Seq findListenersByClass(ListenerBus $this, ClassTag evidence$1) {
      Class c = ((ClassTag)Predef$.MODULE$.implicitly(evidence$1)).runtimeClass();
      return ((SeqLike)((TraversableLike)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter($this.listeners()).asScala()).filter(new Serializable($this, c) {
         public static final long serialVersionUID = 0L;
         private final Class c$1;

         public final boolean apply(L x$3) {
            boolean var3;
            label23: {
               Class var10000 = x$3.getClass();
               Class var2 = this.c$1;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }

         public {
            this.c$1 = c$1;
         }
      })).map(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final T apply(L x$4) {
            return (T)x$4;
         }
      }, Buffer$.MODULE$.canBuildFrom())).toSeq();
   }

   public static void $init$(ListenerBus $this) {
      $this.org$apache$spark$util$ListenerBus$_setter_$org$apache$spark$util$ListenerBus$$listenersPlusTimers_$eq(new CopyOnWriteArrayList());
   }
}
