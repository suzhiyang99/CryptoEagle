package org.apache.spark;

import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.broadcast.BroadcastManager;
import org.apache.spark.scheduler.MapStatus;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.util.Utils$;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.IndexedSeq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005c\u0001B\u0001\u0003\t%\u0011Qb\u00155vM\u001adWm\u0015;biV\u001c(BA\u0002\u0005\u0003\u0015\u0019\b/\u0019:l\u0015\t)a!\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u000f\u0005\u0019qN]4\u0004\u0001M\u0011\u0001A\u0003\t\u0003\u00179i\u0011\u0001\u0004\u0006\u0002\u001b\u0005)1oY1mC&\u0011q\u0002\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011E\u0001!\u0011!Q\u0001\nI\tQB\\;n!\u0006\u0014H/\u001b;j_:\u001c\bCA\u0006\u0014\u0013\t!BBA\u0002J]RDQA\u0006\u0001\u0005\u0002]\ta\u0001P5oSRtDC\u0001\r\u001b!\tI\u0002!D\u0001\u0003\u0011\u0015\tR\u00031\u0001\u0013\u0011\u001da\u0002A1A\u0005\u0002u\t1\"\\1q'R\fG/^:fgV\ta\u0004E\u0002\f?\u0005J!\u0001\t\u0007\u0003\u000b\u0005\u0013(/Y=\u0011\u0005\t*S\"A\u0012\u000b\u0005\u0011\u0012\u0011!C:dQ\u0016$W\u000f\\3s\u0013\t13EA\u0005NCB\u001cF/\u0019;vg\"1\u0001\u0006\u0001Q\u0001\ny\tA\"\\1q'R\fG/^:fg\u0002B\u0011B\u000b\u0001A\u0002\u0003\u0005\u000b\u0015B\u0016\u00023\r\f7\r[3e'\u0016\u0014\u0018.\u00197ju\u0016$W*\u00199Ti\u0006$Xo\u001d\t\u0004\u0017}a\u0003CA\u0006.\u0013\tqCB\u0001\u0003CsR,\u0007\"\u0003\u0019\u0001\u0001\u0004\u0005\t\u0015)\u00032\u0003e\u0019\u0017m\u00195fIN+'/[1mSj,GM\u0011:pC\u0012\u001c\u0017m\u001d;\u0011\u0007I*4&D\u00014\u0015\t!$!A\u0005ce>\fGmY1ti&\u0011ag\r\u0002\n\u0005J|\u0017\rZ2bgRDa\u0001\u000f\u0001!B\u0013\u0011\u0012\u0001F0ok6\fe/Y5mC\ndWmT;uaV$8\u000fC\u0003;\u0001\u0011\u00051(\u0001\u0007bI\u0012l\u0015\r](viB,H\u000fF\u0002=\u007f\u0005\u0003\"aC\u001f\n\u0005yb!\u0001B+oSRDQ\u0001Q\u001dA\u0002I\tQ!\\1q\u0013\u0012DQAQ\u001dA\u0002\u0005\naa\u001d;biV\u001c\b\"\u0002#\u0001\t\u0003)\u0015a\u0004:f[>4X-T1q\u001fV$\b/\u001e;\u0015\u0007q2u\tC\u0003A\u0007\u0002\u0007!\u0003C\u0003I\u0007\u0002\u0007\u0011*A\u0005c[\u0006#GM]3tgB\u0011!*T\u0007\u0002\u0017*\u0011AJA\u0001\bgR|'/Y4f\u0013\tq5J\u0001\bCY>\u001c7.T1oC\u001e,'/\u00133\t\u000bA\u0003A\u0011A)\u0002'I,Wn\u001c<f\u001fV$\b/\u001e;t\u001f:Dun\u001d;\u0015\u0005q\u0012\u0006\"B*P\u0001\u0004!\u0016\u0001\u00025pgR\u0004\"!\u0016-\u000f\u0005-1\u0016BA,\r\u0003\u0019\u0001&/\u001a3fM&\u0011\u0011L\u0017\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005]c\u0001\"\u0002/\u0001\t\u0003i\u0016a\u0006:f[>4XmT;uaV$8o\u00148Fq\u0016\u001cW\u000f^8s)\tad\fC\u0003`7\u0002\u0007A+\u0001\u0004fq\u0016\u001c\u0017\n\u001a\u0005\u0006C\u0002!\tAY\u0001\u0016e\u0016lwN^3PkR\u0004X\u000f^:Cs\u001aKG\u000e^3s)\ta4\rC\u0003eA\u0002\u0007Q-A\u0001g!\u0011Ya-\u00135\n\u0005\u001dd!!\u0003$v]\u000e$\u0018n\u001c82!\tY\u0011.\u0003\u0002k\u0019\t9!i\\8mK\u0006t\u0007\"\u00027\u0001\t\u0003i\u0017a\u00058v[\u00063\u0018-\u001b7bE2,w*\u001e;qkR\u001cX#\u0001\n\t\u000b=\u0004A\u0011\u00019\u0002+\u0019Lg\u000eZ'jgNLgn\u001a)beRLG/[8ogR\t\u0011\u000fE\u0002suJq!a\u001d=\u000f\u0005Q<X\"A;\u000b\u0005YD\u0011A\u0002\u001fs_>$h(C\u0001\u000e\u0013\tIH\"A\u0004qC\u000e\\\u0017mZ3\n\u0005md(aA*fc*\u0011\u0011\u0010\u0004\u0005\u0006}\u0002!\ta`\u0001\u0014g\u0016\u0014\u0018.\u00197ju\u0016$W*\u00199Ti\u0006$Xo\u001d\u000b\bW\u0005\u0005\u00111BA\b\u0011\u001d\t\u0019! a\u0001\u0003\u000b\t\u0001C\u0019:pC\u0012\u001c\u0017m\u001d;NC:\fw-\u001a:\u0011\u0007I\n9!C\u0002\u0002\nM\u0012\u0001C\u0011:pC\u0012\u001c\u0017m\u001d;NC:\fw-\u001a:\t\r\u00055Q\u00101\u0001i\u0003\u001dI7\u000fT8dC2Da!!\u0005~\u0001\u0004\u0011\u0012\u0001E7j]\n\u0013x.\u00193dCN$8+\u001b>f\u0011\u001d\t)\u0002\u0001C\u0001\u0003/\tA\u0004[1t\u0007\u0006\u001c\u0007.\u001a3TKJL\u0017\r\\5{K\u0012\u0014%o\\1eG\u0006\u001cH/F\u0001i\u0011\u001d\tY\u0002\u0001C\u0001\u0003;\tqb^5uQ6\u000b\u0007o\u0015;biV\u001cXm]\u000b\u0005\u0003?\t)\u0003\u0006\u0003\u0002\"\u0005]\u0002\u0003BA\u0012\u0003Ka\u0001\u0001\u0002\u0005\u0002(\u0005e!\u0019AA\u0015\u0005\u0005!\u0016\u0003BA\u0016\u0003c\u00012aCA\u0017\u0013\r\ty\u0003\u0004\u0002\b\u001d>$\b.\u001b8h!\rY\u00111G\u0005\u0004\u0003ka!aA!os\"9A-!\u0007A\u0002\u0005e\u0002#B\u0006g=\u0005\u0005\u0002bBA\u001f\u0001\u0011\u0005\u0011qH\u0001)S:4\u0018\r\\5eCR,7+\u001a:jC2L'0\u001a3NCB|U\u000f\u001e9viN#\u0018\r^;t\u0007\u0006\u001c\u0007.\u001a\u000b\u0002y\u0001"
)
public class ShuffleStatus {
   public final int org$apache$spark$ShuffleStatus$$numPartitions;
   private final MapStatus[] mapStatuses;
   private byte[] cachedSerializedMapStatus;
   public Broadcast<byte[]> org$apache$spark$ShuffleStatus$$cachedSerializedBroadcast;
   public int org$apache$spark$ShuffleStatus$$_numAvailableOutputs;

   public MapStatus[] mapStatuses() {
      return this.mapStatuses;
   }

   public synchronized void addMapOutput(int mapId, MapStatus status) {
      if (this.mapStatuses()[mapId] == null) {
         ++this.org$apache$spark$ShuffleStatus$$_numAvailableOutputs;
         this.invalidateSerializedMapOutputStatusCache();
      }

      this.mapStatuses()[mapId] = status;
   }

   public synchronized void removeMapOutput(int mapId, BlockManagerId bmAddress) {
      if (this.mapStatuses()[mapId] != null) {
         BlockManagerId var10000 = this.mapStatuses()[mapId].location();
         if (var10000 == null) {
            if (bmAddress != null) {
               return;
            }
         } else if (!var10000.equals(bmAddress)) {
            return;
         }

         --this.org$apache$spark$ShuffleStatus$$_numAvailableOutputs;
         this.mapStatuses()[mapId] = null;
         this.invalidateSerializedMapOutputStatusCache();
      }

   }

   public void removeOutputsOnHost(String host) {
      this.removeOutputsByFilter(new Serializable(this, host) {
         public static final long serialVersionUID = 0L;
         private final String host$1;

         public final boolean apply(BlockManagerId x) {
            boolean var3;
            label23: {
               String var10000 = x.host();
               String var2 = this.host$1;
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
            this.host$1 = host$1;
         }
      });
   }

   public synchronized void removeOutputsOnExecutor(String execId) {
      this.removeOutputsByFilter(new Serializable(this, execId) {
         public static final long serialVersionUID = 0L;
         private final String execId$1;

         public final boolean apply(BlockManagerId x) {
            boolean var3;
            label23: {
               String var10000 = x.executorId();
               String var2 = this.execId$1;
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
            this.execId$1 = execId$1;
         }
      });
   }

   public synchronized void removeOutputsByFilter(Function1<BlockManagerId, Object> f) {
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.mapStatuses().length).foreach$mVc$sp(new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleStatus $outer;
         private final Function1 f$1;

         public final void apply(int mapId) {
            this.apply$mcVI$sp(mapId);
         }

         public void apply$mcVI$sp(int mapId) {
            if (this.$outer.mapStatuses()[mapId] != null && BoxesRunTime.unboxToBoolean(this.f$1.apply(this.$outer.mapStatuses()[mapId].location()))) {
               --this.$outer.org$apache$spark$ShuffleStatus$$_numAvailableOutputs;
               this.$outer.mapStatuses()[mapId] = null;
               this.$outer.invalidateSerializedMapOutputStatusCache();
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.f$1 = f$1;
            }
         }
      });
   }

   public synchronized int numAvailableOutputs() {
      return this.org$apache$spark$ShuffleStatus$$_numAvailableOutputs;
   }

   public synchronized Seq<Object> findMissingPartitions() {
      IndexedSeq missing = (IndexedSeq)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.org$apache$spark$ShuffleStatus$$numPartitions).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleStatus $outer;

         public final boolean apply(int id) {
            return this.apply$mcZI$sp(id);
         }

         public boolean apply$mcZI$sp(int id) {
            return this.$outer.mapStatuses()[id] == null;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Predef$.MODULE$.assert(missing.size() == this.org$apache$spark$ShuffleStatus$$numPartitions - this.org$apache$spark$ShuffleStatus$$_numAvailableOutputs, new Serializable(this, missing) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleStatus $outer;
         private final IndexedSeq missing$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " missing, expected ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.missing$1.size()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ShuffleStatus$$numPartitions - this.$outer.org$apache$spark$ShuffleStatus$$_numAvailableOutputs)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.missing$1 = missing$1;
            }
         }
      });
      return missing;
   }

   public synchronized byte[] serializedMapStatus(BroadcastManager broadcastManager, boolean isLocal, int minBroadcastSize) {
      if (this.cachedSerializedMapStatus == null) {
         Tuple2 serResult = MapOutputTracker$.MODULE$.serializeMapStatuses(this.mapStatuses(), broadcastManager, isLocal, minBroadcastSize);
         this.cachedSerializedMapStatus = (byte[])serResult._1();
         this.org$apache$spark$ShuffleStatus$$cachedSerializedBroadcast = (Broadcast)serResult._2();
      }

      return this.cachedSerializedMapStatus;
   }

   public synchronized boolean hasCachedSerializedBroadcast() {
      return this.org$apache$spark$ShuffleStatus$$cachedSerializedBroadcast != null;
   }

   public synchronized <T> T withMapStatuses(Function1<MapStatus[], T> f) {
      return (T)f.apply(this.mapStatuses());
   }

   public synchronized void invalidateSerializedMapOutputStatusCache() {
      if (this.org$apache$spark$ShuffleStatus$$cachedSerializedBroadcast != null) {
         Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ShuffleStatus $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$ShuffleStatus$$cachedSerializedBroadcast.destroy(false);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.org$apache$spark$ShuffleStatus$$cachedSerializedBroadcast = null;
      }

      this.cachedSerializedMapStatus = null;
   }

   public ShuffleStatus(int numPartitions) {
      this.org$apache$spark$ShuffleStatus$$numPartitions = numPartitions;
      this.mapStatuses = new MapStatus[numPartitions];
      this.org$apache$spark$ShuffleStatus$$_numAvailableOutputs = 0;
   }
}
