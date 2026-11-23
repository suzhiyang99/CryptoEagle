package org.apache.spark.util;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple7;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mg!B\u0001\u0003\u0001\u0012Q!\u0001\u0005+ie\u0016\fGm\u0015;bG.$&/Y2f\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\t\u0001Y\u0011\u0003\u0006\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00051\u0011\u0012BA\n\u000e\u0005\u001d\u0001&o\u001c3vGR\u0004\"\u0001D\u000b\n\u0005Yi!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\r\u0001\u0005+\u0007I\u0011\u0001\u000e\u0002\u0011QD'/Z1e\u0013\u0012\u001c\u0001!F\u0001\u001c!\taA$\u0003\u0002\u001e\u001b\t!Aj\u001c8h\u0011!y\u0002A!E!\u0002\u0013Y\u0012!\u0003;ie\u0016\fG-\u00133!\u0011!\t\u0003A!f\u0001\n\u0003\u0011\u0013A\u0003;ie\u0016\fGMT1nKV\t1\u0005\u0005\u0002%O9\u0011A\"J\u0005\u0003M5\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0015*\u0005\u0019\u0019FO]5oO*\u0011a%\u0004\u0005\tW\u0001\u0011\t\u0012)A\u0005G\u0005YA\u000f\u001b:fC\u0012t\u0015-\\3!\u0011!i\u0003A!f\u0001\n\u0003q\u0013a\u0003;ie\u0016\fGm\u0015;bi\u0016,\u0012a\f\t\u0003aar!!\r\u001c\u000e\u0003IR!a\r\u001b\u0002\t1\fgn\u001a\u0006\u0002k\u0005!!.\u0019<b\u0013\t9$'\u0001\u0004UQJ,\u0017\rZ\u0005\u0003si\u0012Qa\u0015;bi\u0016T!a\u000e\u001a\t\u0011q\u0002!\u0011#Q\u0001\n=\nA\u0002\u001e5sK\u0006$7\u000b^1uK\u0002B\u0001B\u0010\u0001\u0003\u0016\u0004%\tAI\u0001\u000bgR\f7m\u001b+sC\u000e,\u0007\u0002\u0003!\u0001\u0005#\u0005\u000b\u0011B\u0012\u0002\u0017M$\u0018mY6Ue\u0006\u001cW\r\t\u0005\t\u0005\u0002\u0011)\u001a!C\u0001\u0007\u0006\t\"\r\\8dW\u0016$')\u001f+ie\u0016\fG-\u00133\u0016\u0003\u0011\u00032\u0001D#\u001c\u0013\t1UB\u0001\u0004PaRLwN\u001c\u0005\t\u0011\u0002\u0011\t\u0012)A\u0005\t\u0006\u0011\"\r\\8dW\u0016$')\u001f+ie\u0016\fG-\u00133!\u0011!Q\u0005A!f\u0001\n\u0003\u0011\u0013!\u00042m_\u000e\\W\r\u001a\"z\u0019>\u001c7\u000e\u0003\u0005M\u0001\tE\t\u0015!\u0003$\u00039\u0011Gn\\2lK\u0012\u0014\u0015\u0010T8dW\u0002B\u0001B\u0014\u0001\u0003\u0016\u0004%\taT\u0001\rQ>dG-\u001b8h\u0019>\u001c7n]\u000b\u0002!B\u0019\u0011+W\u0012\u000f\u0005I;fBA*W\u001b\u0005!&BA+\u001a\u0003\u0019a$o\\8u}%\ta\"\u0003\u0002Y\u001b\u00059\u0001/Y2lC\u001e,\u0017B\u0001.\\\u0005\r\u0019V-\u001d\u0006\u000316A\u0001\"\u0018\u0001\u0003\u0012\u0003\u0006I\u0001U\u0001\u000eQ>dG-\u001b8h\u0019>\u001c7n\u001d\u0011\t\u000b}\u0003A\u0011\u00011\u0002\rqJg.\u001b;?)!\t7\rZ3gO\"L\u0007C\u00012\u0001\u001b\u0005\u0011\u0001\"\u0002\r_\u0001\u0004Y\u0002\"B\u0011_\u0001\u0004\u0019\u0003\"B\u0017_\u0001\u0004y\u0003\"\u0002 _\u0001\u0004\u0019\u0003\"\u0002\"_\u0001\u0004!\u0005\"\u0002&_\u0001\u0004\u0019\u0003\"\u0002(_\u0001\u0004\u0001\u0006bB6\u0001\u0003\u0003%\t\u0001\\\u0001\u0005G>\u0004\u0018\u0010\u0006\u0005b[:|\u0007/\u001d:t\u0011\u001dA\"\u000e%AA\u0002mAq!\t6\u0011\u0002\u0003\u00071\u0005C\u0004.UB\u0005\t\u0019A\u0018\t\u000fyR\u0007\u0013!a\u0001G!9!I\u001bI\u0001\u0002\u0004!\u0005b\u0002&k!\u0003\u0005\ra\t\u0005\b\u001d*\u0004\n\u00111\u0001Q\u0011\u001d)\b!%A\u0005\u0002Y\fabY8qs\u0012\"WMZ1vYR$\u0013'F\u0001xU\tY\u0002pK\u0001z!\tQx0D\u0001|\u0015\taX0A\u0005v]\u000eDWmY6fI*\u0011a0D\u0001\u000bC:tw\u000e^1uS>t\u0017bAA\u0001w\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u0013\u0005\u0015\u0001!%A\u0005\u0002\u0005\u001d\u0011AD2paf$C-\u001a4bk2$HEM\u000b\u0003\u0003\u0013Q#a\t=\t\u0013\u00055\u0001!%A\u0005\u0002\u0005=\u0011AD2paf$C-\u001a4bk2$HeM\u000b\u0003\u0003#Q#a\f=\t\u0013\u0005U\u0001!%A\u0005\u0002\u0005\u001d\u0011AD2paf$C-\u001a4bk2$H\u0005\u000e\u0005\n\u00033\u0001\u0011\u0013!C\u0001\u00037\tabY8qs\u0012\"WMZ1vYR$S'\u0006\u0002\u0002\u001e)\u0012A\t\u001f\u0005\n\u0003C\u0001\u0011\u0013!C\u0001\u0003\u000f\tabY8qs\u0012\"WMZ1vYR$c\u0007C\u0005\u0002&\u0001\t\n\u0011\"\u0001\u0002(\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012:TCAA\u0015U\t\u0001\u0006\u0010C\u0005\u0002.\u0001\t\t\u0011\"\u0011\u00020\u0005i\u0001O]8ek\u000e$\bK]3gSb,\"!!\r\u0011\u0007E\n\u0019$\u0003\u0002)e!I\u0011q\u0007\u0001\u0002\u0002\u0013\u0005\u0011\u0011H\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0003\u0003w\u00012\u0001DA\u001f\u0013\r\ty$\u0004\u0002\u0004\u0013:$\b\"CA\"\u0001\u0005\u0005I\u0011AA#\u00039\u0001(o\u001c3vGR,E.Z7f]R$B!a\u0012\u0002NA\u0019A\"!\u0013\n\u0007\u0005-SBA\u0002B]fD!\"a\u0014\u0002B\u0005\u0005\t\u0019AA\u001e\u0003\rAH%\r\u0005\n\u0003'\u0002\u0011\u0011!C!\u0003+\nq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0003/\u0002b!!\u0017\u0002`\u0005\u001dSBAA.\u0015\r\ti&D\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA1\u00037\u0012\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u0003K\u0002\u0011\u0011!C\u0001\u0003O\n\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0005\u0003S\ny\u0007E\u0002\r\u0003WJ1!!\u001c\u000e\u0005\u001d\u0011un\u001c7fC:D!\"a\u0014\u0002d\u0005\u0005\t\u0019AA$\u0011%\t\u0019\bAA\u0001\n\u0003\n)(\u0001\u0005iCND7i\u001c3f)\t\tY\u0004C\u0005\u0002z\u0001\t\t\u0011\"\u0011\u0002|\u0005AAo\\*ue&tw\r\u0006\u0002\u00022!I\u0011q\u0010\u0001\u0002\u0002\u0013\u0005\u0013\u0011Q\u0001\u0007KF,\u0018\r\\:\u0015\t\u0005%\u00141\u0011\u0005\u000b\u0003\u001f\ni(!AA\u0002\u0005\u001dsACAD\u0005\u0005\u0005\t\u0012\u0001\u0003\u0002\n\u0006\u0001B\u000b\u001b:fC\u0012\u001cF/Y2l)J\f7-\u001a\t\u0004E\u0006-e!C\u0001\u0003\u0003\u0003E\t\u0001BAG'\u0015\tY)a$\u0015!1\t\t*a&\u001cG=\u001aCi\t)b\u001b\t\t\u0019JC\u0002\u0002\u00166\tqA];oi&lW-\u0003\u0003\u0002\u001a\u0006M%!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oo!9q,a#\u0005\u0002\u0005uECAAE\u0011)\tI(a#\u0002\u0002\u0013\u0015\u00131\u0010\u0005\u000b\u0003G\u000bY)!A\u0005\u0002\u0006\u0015\u0016!B1qa2LHcD1\u0002(\u0006%\u00161VAW\u0003_\u000b\t,a-\t\ra\t\t\u000b1\u0001\u001c\u0011\u0019\t\u0013\u0011\u0015a\u0001G!1Q&!)A\u0002=BaAPAQ\u0001\u0004\u0019\u0003B\u0002\"\u0002\"\u0002\u0007A\t\u0003\u0004K\u0003C\u0003\ra\t\u0005\u0007\u001d\u0006\u0005\u0006\u0019\u0001)\t\u0015\u0005]\u00161RA\u0001\n\u0003\u000bI,A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005m\u00161\u0019\t\u0005\u0019\u0015\u000bi\f\u0005\u0006\r\u0003\u007f[2eL\u0012EGAK1!!1\u000e\u0005\u0019!V\u000f\u001d7fo!I\u0011QYA[\u0003\u0003\u0005\r!Y\u0001\u0004q\u0012\u0002\u0004BCAe\u0003\u0017\u000b\t\u0011\"\u0003\u0002L\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\ti\rE\u00022\u0003\u001fL1!!53\u0005\u0019y%M[3di\u0002"
)
public class ThreadStackTrace implements Product, Serializable {
   private final long threadId;
   private final String threadName;
   private final Thread.State threadState;
   private final String stackTrace;
   private final Option<Object> blockedByThreadId;
   private final String blockedByLock;
   private final Seq<String> holdingLocks;

   public static Option<Tuple7<Object, String, Thread.State, String, Option<Object>, String, Seq<String>>> unapply(ThreadStackTrace var0) {
      return ThreadStackTrace$.MODULE$.unapply(var0);
   }

   public static ThreadStackTrace apply(long var0, String var2, Thread.State var3, String var4, Option<Object> var5, String var6, Seq<String> var7) {
      return ThreadStackTrace$.MODULE$.apply(var0, var2, var3, var4, var5, var6, var7);
   }

   public static Function1<Tuple7<Object, String, Thread.State, String, Option<Object>, String, Seq<String>>, ThreadStackTrace> tupled() {
      return ThreadStackTrace$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, Function1<Thread.State, Function1<String, Function1<Option<Object>, Function1<String, Function1<Seq<String>, ThreadStackTrace>>>>>>> curried() {
      return ThreadStackTrace$.MODULE$.curried();
   }

   public long threadId() {
      return this.threadId;
   }

   public String threadName() {
      return this.threadName;
   }

   public Thread.State threadState() {
      return this.threadState;
   }

   public String stackTrace() {
      return this.stackTrace;
   }

   public Option<Object> blockedByThreadId() {
      return this.blockedByThreadId;
   }

   public String blockedByLock() {
      return this.blockedByLock;
   }

   public Seq<String> holdingLocks() {
      return this.holdingLocks;
   }

   public ThreadStackTrace copy(long threadId, String threadName, Thread.State threadState, String stackTrace, Option<Object> blockedByThreadId, String blockedByLock, Seq<String> holdingLocks) {
      return new ThreadStackTrace(threadId, threadName, threadState, stackTrace, blockedByThreadId, blockedByLock, holdingLocks);
   }

   public long copy$default$1() {
      return this.threadId();
   }

   public String copy$default$2() {
      return this.threadName();
   }

   public Thread.State copy$default$3() {
      return this.threadState();
   }

   public String copy$default$4() {
      return this.stackTrace();
   }

   public Option<Object> copy$default$5() {
      return this.blockedByThreadId();
   }

   public String copy$default$6() {
      return this.blockedByLock();
   }

   public Seq<String> copy$default$7() {
      return this.holdingLocks();
   }

   public String productPrefix() {
      return "ThreadStackTrace";
   }

   public int productArity() {
      return 7;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.threadId());
            break;
         case 1:
            var10000 = this.threadName();
            break;
         case 2:
            var10000 = this.threadState();
            break;
         case 3:
            var10000 = this.stackTrace();
            break;
         case 4:
            var10000 = this.blockedByThreadId();
            break;
         case 5:
            var10000 = this.blockedByLock();
            break;
         case 6:
            var10000 = this.holdingLocks();
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof ThreadStackTrace;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.threadId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.threadName()));
      var1 = Statics.mix(var1, Statics.anyHash(this.threadState()));
      var1 = Statics.mix(var1, Statics.anyHash(this.stackTrace()));
      var1 = Statics.mix(var1, Statics.anyHash(this.blockedByThreadId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.blockedByLock()));
      var1 = Statics.mix(var1, Statics.anyHash(this.holdingLocks()));
      return Statics.finalizeHash(var1, 7);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var17;
      if (this != x$1) {
         label100: {
            boolean var3;
            if (x$1 instanceof ThreadStackTrace) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label82: {
                  ThreadStackTrace var4 = (ThreadStackTrace)x$1;
                  if (this.threadId() == var4.threadId()) {
                     label91: {
                        String var10000 = this.threadName();
                        String var5 = var4.threadName();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label91;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label91;
                        }

                        Thread.State var11 = this.threadState();
                        Thread.State var6 = var4.threadState();
                        if (var11 == null) {
                           if (var6 != null) {
                              break label91;
                           }
                        } else if (!var11.equals(var6)) {
                           break label91;
                        }

                        String var12 = this.stackTrace();
                        String var7 = var4.stackTrace();
                        if (var12 == null) {
                           if (var7 != null) {
                              break label91;
                           }
                        } else if (!var12.equals(var7)) {
                           break label91;
                        }

                        Option var13 = this.blockedByThreadId();
                        Option var8 = var4.blockedByThreadId();
                        if (var13 == null) {
                           if (var8 != null) {
                              break label91;
                           }
                        } else if (!var13.equals(var8)) {
                           break label91;
                        }

                        String var14 = this.blockedByLock();
                        String var9 = var4.blockedByLock();
                        if (var14 == null) {
                           if (var9 != null) {
                              break label91;
                           }
                        } else if (!var14.equals(var9)) {
                           break label91;
                        }

                        Seq var15 = this.holdingLocks();
                        Seq var10 = var4.holdingLocks();
                        if (var15 == null) {
                           if (var10 != null) {
                              break label91;
                           }
                        } else if (!var15.equals(var10)) {
                           break label91;
                        }

                        if (var4.canEqual(this)) {
                           var17 = true;
                           break label82;
                        }
                     }
                  }

                  var17 = false;
               }

               if (var17) {
                  break label100;
               }
            }

            var17 = false;
            return var17;
         }
      }

      var17 = true;
      return var17;
   }

   public ThreadStackTrace(long threadId, String threadName, Thread.State threadState, String stackTrace, Option<Object> blockedByThreadId, String blockedByLock, Seq<String> holdingLocks) {
      this.threadId = threadId;
      this.threadName = threadName;
      this.threadState = threadState;
      this.stackTrace = stackTrace;
      this.blockedByThreadId = blockedByThreadId;
      this.blockedByLock = blockedByLock;
      this.holdingLocks = holdingLocks;
      Product$class.$init$(this);
   }
}
