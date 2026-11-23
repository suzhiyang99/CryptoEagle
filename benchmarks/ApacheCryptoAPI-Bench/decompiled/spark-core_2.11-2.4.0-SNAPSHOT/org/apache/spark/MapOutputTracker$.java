package org.apache.spark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.broadcast.BroadcastManager;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.scheduler.MapStatus;
import org.apache.spark.shuffle.MetadataFetchFailedException;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.storage.ShuffleBlockId;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;

public final class MapOutputTracker$ implements Logging {
   public static final MapOutputTracker$ MODULE$;
   private final String ENDPOINT_NAME;
   private final int DIRECT;
   private final int BROADCAST;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new MapOutputTracker$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

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

   public String ENDPOINT_NAME() {
      return this.ENDPOINT_NAME;
   }

   private int DIRECT() {
      return this.DIRECT;
   }

   private int BROADCAST() {
      return this.BROADCAST;
   }

   public Tuple2<byte[], Broadcast<byte[]>> serializeMapStatuses(MapStatus[] statuses, BroadcastManager broadcastManager, boolean isLocal, int minBroadcastSize) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      out.write(this.DIRECT());
      ObjectOutputStream objOut = new ObjectOutputStream(new GZIPOutputStream(out));
      Utils$.MODULE$.tryWithSafeFinally(new Serializable(statuses, objOut) {
         public static final long serialVersionUID = 0L;
         private final MapStatus[] statuses$1;
         private final ObjectOutputStream objOut$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            synchronized(this.statuses$1) {
               this.objOut$1.writeObject(this.statuses$1);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }
         }

         public {
            this.statuses$1 = statuses$1;
            this.objOut$1 = objOut$1;
         }
      }, new Serializable(objOut) {
         public static final long serialVersionUID = 0L;
         private final ObjectOutputStream objOut$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.objOut$1.close();
         }

         public {
            this.objOut$1 = objOut$1;
         }
      });
      byte[] arr = out.toByteArray();
      Tuple2 var10000;
      if (arr.length >= minBroadcastSize) {
         Broadcast bcast = broadcastManager.newBroadcast(arr, isLocal, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
         out.reset();
         out.write(this.BROADCAST());
         ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(out));
         oos.writeObject(bcast);
         oos.close();
         byte[] outArr = out.toByteArray();
         this.logInfo(new Serializable(arr, outArr) {
            public static final long serialVersionUID = 0L;
            private final byte[] arr$1;
            private final byte[] outArr$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Broadcast mapstatuses size = ").append((Object)BoxesRunTime.boxToInteger(this.outArr$1.length)).append((Object)", actual size = ").append((Object)BoxesRunTime.boxToInteger(this.arr$1.length)).toString();
            }

            public {
               this.arr$1 = arr$1;
               this.outArr$1 = outArr$1;
            }
         });
         var10000 = new Tuple2(outArr, bcast);
      } else {
         var10000 = new Tuple2(arr, (Object)null);
      }

      return var10000;
   }

   public MapStatus[] deserializeMapStatuses(byte[] bytes) {
      Predef$.MODULE$.assert(bytes.length > 0);
      byte var2 = bytes[0];
      MapStatus[] var3;
      if (this.DIRECT() == var2) {
         var3 = (MapStatus[])this.deserializeObject$1(bytes, 1, bytes.length - 1);
      } else {
         if (this.BROADCAST() != var2) {
            throw new IllegalArgumentException((new StringBuilder()).append((Object)"Unexpected byte tag = ").append((Object)BoxesRunTime.boxToByte(bytes[0])).toString());
         }

         Broadcast bcast = (Broadcast)this.deserializeObject$1(bytes, 1, bytes.length - 1);
         this.logInfo(new Serializable(bytes, bcast) {
            public static final long serialVersionUID = 0L;
            private final byte[] bytes$1;
            private final Broadcast bcast$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Broadcast mapstatuses size = ").append((Object)BoxesRunTime.boxToInteger(this.bytes$1.length)).append((Object)", actual size = ").append((Object)BoxesRunTime.boxToInteger(((byte[])this.bcast$1.value()).length)).toString();
            }

            public {
               this.bytes$1 = bytes$1;
               this.bcast$1 = bcast$1;
            }
         });
         var3 = (MapStatus[])this.deserializeObject$1((byte[])bcast.value(), 1, ((byte[])bcast.value()).length - 1);
      }

      return var3;
   }

   public Seq<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> convertMapStatuses(int shuffleId, int startPartition, int endPartition, MapStatus[] statuses) {
      Predef$.MODULE$.assert(statuses != null);
      HashMap splitsByAddress = new HashMap();
      Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(statuses).zipWithIndex(Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).withFilter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<MapStatus, Object> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(shuffleId, startPartition, endPartition, splitsByAddress) {
         public static final long serialVersionUID = 0L;
         public final int shuffleId$3;
         private final int startPartition$2;
         private final int endPartition$2;
         public final HashMap splitsByAddress$1;

         public final void apply(Tuple2<MapStatus, Object> x$7) {
            if (x$7 != null) {
               MapStatus status = (MapStatus)x$7._1();
               int mapId = x$7._2$mcI$sp();
               if (status == null) {
                  String errorMessage = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Missing an output location for shuffle ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.shuffleId$3)}));
                  MapOutputTracker$.MODULE$.logError(new Serializable(this, errorMessage) {
                     public static final long serialVersionUID = 0L;
                     private final String errorMessage$1;

                     public final String apply() {
                        return this.errorMessage$1;
                     }

                     public {
                        this.errorMessage$1 = errorMessage$1;
                     }
                  });
                  throw new MetadataFetchFailedException(this.shuffleId$3, this.startPartition$2, errorMessage);
               } else {
                  RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(this.startPartition$2), this.endPartition$2).foreach(new Serializable(this, status, mapId) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final MapStatus status$1;
                     private final int mapId$1;

                     public final ArrayBuffer<Tuple2<BlockId, Object>> apply(int part) {
                        return ((ArrayBuffer)this.$outer.splitsByAddress$1.getOrElseUpdate(this.status$1.location(), new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final ArrayBuffer<Tuple2<BlockId, Object>> apply() {
                              return (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
                           }
                        })).$plus$eq(new Tuple2(new ShuffleBlockId(this.$outer.shuffleId$3, this.mapId$1, part), BoxesRunTime.boxToLong(this.status$1.getSizeForBlock(part))));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.status$1 = status$1;
                           this.mapId$1 = mapId$1;
                        }
                     }
                  });
                  BoxedUnit var5 = BoxedUnit.UNIT;
               }
            } else {
               throw new MatchError(x$7);
            }
         }

         public {
            this.shuffleId$3 = shuffleId$3;
            this.startPartition$2 = startPartition$2;
            this.endPartition$2 = endPartition$2;
            this.splitsByAddress$1 = splitsByAddress$1;
         }
      });
      return splitsByAddress.toSeq();
   }

   private final Object deserializeObject$1(byte[] arr, int off, int len) {
      ObjectInputStream objIn = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(arr, off, len)));
      return Utils$.MODULE$.tryWithSafeFinally(new Serializable(objIn) {
         public static final long serialVersionUID = 0L;
         private final ObjectInputStream objIn$1;

         public final Object apply() {
            return this.objIn$1.readObject();
         }

         public {
            this.objIn$1 = objIn$1;
         }
      }, new Serializable(objIn) {
         public static final long serialVersionUID = 0L;
         private final ObjectInputStream objIn$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.objIn$1.close();
         }

         public {
            this.objIn$1 = objIn$1;
         }
      });
   }

   private MapOutputTracker$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.ENDPOINT_NAME = "MapOutputTracker";
      this.DIRECT = 0;
      this.BROADCAST = 1;
   }
}
