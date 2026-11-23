package org.apache.spark.shuffle;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.io.NioBufferedFileInputStream;
import org.apache.spark.network.buffer.FileSegmentManagedBuffer;
import org.apache.spark.network.buffer.ManagedBuffer;
import org.apache.spark.network.netty.SparkTransportConf$;
import org.apache.spark.network.util.TransportConf;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.ShuffleBlockId;
import org.apache.spark.storage.ShuffleDataBlockId;
import org.apache.spark.storage.ShuffleIndexBlockId;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.LongRef;
import scala.runtime.ObjectRef;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001dc!B\u0001\u0003\u0001\u0011Q!!G%oI\u0016D8\u000b[;gM2,'\t\\8dWJ+7o\u001c7wKJT!a\u0001\u0003\u0002\u000fMDWO\u001a4mK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0017E)\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\u0013'5\t!!\u0003\u0002\u0015\u0005\t!2\u000b[;gM2,'\t\\8dWJ+7o\u001c7wKJ\u0004\"AF\r\u000e\u0003]Q!\u0001\u0007\u0003\u0002\u0011%tG/\u001a:oC2L!AG\f\u0003\u000f1{wmZ5oO\"AA\u0004\u0001B\u0001B\u0003%a$\u0001\u0003d_:47\u0001\u0001\t\u0003?\u0001j\u0011\u0001B\u0005\u0003C\u0011\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011\r\u0002!\u0011!Q\u0001\n\u0011\nQb\u00182m_\u000e\\W*\u00198bO\u0016\u0014\bCA\u0013)\u001b\u00051#BA\u0014\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016L!!\u000b\u0014\u0003\u0019\tcwnY6NC:\fw-\u001a:\t\u000b-\u0002A\u0011\u0001\u0017\u0002\rqJg.\u001b;?)\ricf\f\t\u0003%\u0001AQ\u0001\b\u0016A\u0002yAqa\t\u0016\u0011\u0002\u0003\u0007A\u0005\u0003\u00052\u0001!\u0015\r\u0011\"\u00033\u00031\u0011Gn\\2l\u001b\u0006t\u0017mZ3s+\u0005!\u0003\u0002\u0003\u001b\u0001\u0011\u0003\u0005\u000b\u0015\u0002\u0013\u0002\u001b\tdwnY6NC:\fw-\u001a:!\u0011\u001d1\u0004A1A\u0005\n]\nQ\u0002\u001e:b]N\u0004xN\u001d;D_:4W#\u0001\u001d\u0011\u0005erT\"\u0001\u001e\u000b\u0005mb\u0014\u0001B;uS2T!!\u0010\u0003\u0002\u000f9,Go^8sW&\u0011qH\u000f\u0002\u000e)J\fgn\u001d9peR\u001cuN\u001c4\t\r\u0005\u0003\u0001\u0015!\u00039\u00039!(/\u00198ta>\u0014HoQ8oM\u0002BQa\u0011\u0001\u0005\u0002\u0011\u000b1bZ3u\t\u0006$\u0018MR5mKR\u0019Q)\u0014*\u0011\u0005\u0019[U\"A$\u000b\u0005!K\u0015AA5p\u0015\u0005Q\u0015\u0001\u00026bm\u0006L!\u0001T$\u0003\t\u0019KG.\u001a\u0005\u0006\u001d\n\u0003\raT\u0001\ng\",hM\u001a7f\u0013\u0012\u0004\"\u0001\u0004)\n\u0005Ek!aA%oi\")1K\u0011a\u0001\u001f\u0006)Q.\u00199JI\")Q\u000b\u0001C\u0005-\u0006aq-\u001a;J]\u0012,\u0007PR5mKR\u0019Qi\u0016-\t\u000b9#\u0006\u0019A(\t\u000bM#\u0006\u0019A(\t\u000bi\u0003A\u0011A.\u0002\u001fI,Wn\u001c<f\t\u0006$\u0018MQ=NCB$2\u0001X0a!\taQ,\u0003\u0002_\u001b\t!QK\\5u\u0011\u0015q\u0015\f1\u0001P\u0011\u0015\u0019\u0016\f1\u0001P\u0011\u0015\u0011\u0007\u0001\"\u0003d\u0003U\u0019\u0007.Z2l\u0013:$W\r_!oI\u0012\u000bG/\u0019$jY\u0016$B\u0001\u001a6m]B\u0019A\"Z4\n\u0005\u0019l!!B!se\u0006L\bC\u0001\u0007i\u0013\tIWB\u0001\u0003M_:<\u0007\"B6b\u0001\u0004)\u0015!B5oI\u0016D\b\"B7b\u0001\u0004)\u0015\u0001\u00023bi\u0006DQa\\1A\u0002=\u000baA\u00197pG.\u001c\b\"B9\u0001\t\u0003\u0011\u0018aF<sSR,\u0017J\u001c3fq\u001aKG.Z!oI\u000e{W.\\5u)\u0015a6\u000f^;x\u0011\u0015q\u0005\u000f1\u0001P\u0011\u0015\u0019\u0006\u000f1\u0001P\u0011\u00151\b\u000f1\u0001e\u0003\u001daWM\\4uQNDQ\u0001\u001f9A\u0002\u0015\u000bq\u0001Z1uCRk\u0007\u000fC\u0003{\u0001\u0011\u000530\u0001\u0007hKR\u0014En\\2l\t\u0006$\u0018\rF\u0002}\u0003\u000b\u00012!`A\u0001\u001b\u0005q(BA@=\u0003\u0019\u0011WO\u001a4fe&\u0019\u00111\u0001@\u0003\u001b5\u000bg.Y4fI\n+hMZ3s\u0011\u001d\t9!\u001fa\u0001\u0003\u0013\tqA\u00197pG.LE\rE\u0002&\u0003\u0017I1!!\u0004'\u00059\u0019\u0006.\u001e4gY\u0016\u0014En\\2l\u0013\u0012Dq!!\u0005\u0001\t\u0003\n\u0019\"\u0001\u0003ti>\u0004H#\u0001/\b\u0011\u0005]!\u0001#\u0001\u0005\u00033\t\u0011$\u00138eKb\u001c\u0006.\u001e4gY\u0016\u0014En\\2l%\u0016\u001cx\u000e\u001c<feB\u0019!#a\u0007\u0007\u000f\u0005\u0011\u0001\u0012\u0001\u0003\u0002\u001eM\u0019\u00111D\u0006\t\u000f-\nY\u0002\"\u0001\u0002\"Q\u0011\u0011\u0011\u0004\u0005\u000b\u0003K\tYB1A\u0005\u0002\u0005\u001d\u0012A\u0004(P\u001fB{&+\u0012#V\u0007\u0016{\u0016\nR\u000b\u0002\u001f\"A\u00111FA\u000eA\u0003%q*A\bO\u001f>\u0003vLU#E+\u000e+u,\u0013#!\u0011)\ty#a\u0007\u0012\u0002\u0013\u0005\u0011\u0011G\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000f\n\u001a\u0016\u0005\u0005M\"f\u0001\u0013\u00026-\u0012\u0011q\u0007\t\u0005\u0003s\t\u0019%\u0004\u0002\u0002<)!\u0011QHA \u0003%)hn\u00195fG.,GMC\u0002\u0002B5\t!\"\u00198o_R\fG/[8o\u0013\u0011\t)%a\u000f\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r"
)
public class IndexShuffleBlockResolver implements ShuffleBlockResolver, Logging {
   private final BlockManager _blockManager;
   private BlockManager blockManager;
   private final TransportConf transportConf;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private volatile boolean bitmap$0;

   public static BlockManager $lessinit$greater$default$2() {
      return IndexShuffleBlockResolver$.MODULE$.$lessinit$greater$default$2();
   }

   public static int NOOP_REDUCE_ID() {
      return IndexShuffleBlockResolver$.MODULE$.NOOP_REDUCE_ID();
   }

   private BlockManager blockManager$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.blockManager = (BlockManager)Option$.MODULE$.apply(this._blockManager).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final BlockManager apply() {
                  return SparkEnv$.MODULE$.get().blockManager();
               }
            });
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this._blockManager = null;
      return this.blockManager;
   }

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

   private BlockManager blockManager() {
      return this.bitmap$0 ? this.blockManager : this.blockManager$lzycompute();
   }

   private TransportConf transportConf() {
      return this.transportConf;
   }

   public File getDataFile(int shuffleId, int mapId) {
      return this.blockManager().diskBlockManager().getFile((BlockId)(new ShuffleDataBlockId(shuffleId, mapId, IndexShuffleBlockResolver$.MODULE$.NOOP_REDUCE_ID())));
   }

   private File getIndexFile(int shuffleId, int mapId) {
      return this.blockManager().diskBlockManager().getFile((BlockId)(new ShuffleIndexBlockId(shuffleId, mapId, IndexShuffleBlockResolver$.MODULE$.NOOP_REDUCE_ID())));
   }

   public void removeDataByMap(int shuffleId, int mapId) {
      ObjectRef file = ObjectRef.create(this.getDataFile(shuffleId, mapId));
      if (((File)file.elem).exists() && !((File)file.elem).delete()) {
         this.logWarning(new Serializable(this, file) {
            public static final long serialVersionUID = 0L;
            private final ObjectRef file$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting data ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{((File)this.file$1.elem).getPath()}));
            }

            public {
               this.file$1 = file$1;
            }
         });
      }

      file.elem = (T)this.getIndexFile(shuffleId, mapId);
      if (((File)file.elem).exists() && !((File)file.elem).delete()) {
         this.logWarning(new Serializable(this, file) {
            public static final long serialVersionUID = 0L;
            private final ObjectRef file$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting index ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{((File)this.file$1.elem).getPath()}));
            }

            public {
               this.file$1 = file$1;
            }
         });
      }

   }

   private long[] checkIndexAndDataFile(File index, File data, int blocks) {
      if (index.length() != (long)((blocks + 1) * 8)) {
         return null;
      } else {
         long[] lengths = new long[blocks];

         DataInputStream var10000;
         try {
            var10000 = new DataInputStream(new NioBufferedFileInputStream(index));
         } catch (IOException var17) {
            return null;
         }

         DataInputStream in = var10000;

         try {
            long offset = in.readLong();
            if (offset != 0L) {
               Object var21 = null;
               return (long[])in;
            }

            for(int i = 0; i < blocks; ++i) {
               long off = in.readLong();
               lengths[i] = off - offset;
               offset = off;
            }
         } catch (IOException var18) {
            Object var20 = null;
            return (long[])in;
         } finally {
            in.close();
         }

         return data.length() == BoxesRunTime.unboxToLong(Predef$.MODULE$.longArrayOps(lengths).sum(Numeric.LongIsIntegral$.MODULE$)) ? lengths : null;
      }
   }

   public void writeIndexFileAndCommit(int shuffleId, int mapId, long[] lengths, File dataTmp) {
      File indexFile = this.getIndexFile(shuffleId, mapId);
      File indexTmp = Utils$.MODULE$.tempFileWith(indexFile);

      try {
         DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(indexTmp)));
         Utils$.MODULE$.tryWithSafeFinally(new Serializable(this, lengths, out) {
            public static final long serialVersionUID = 0L;
            private final long[] lengths$1;
            public final DataOutputStream out$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               LongRef offset = LongRef.create(0L);
               this.out$1.writeLong(offset.elem);
               Predef$.MODULE$.longArrayOps(this.lengths$1).foreach(new Serializable(this, offset) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final LongRef offset$1;

                  public final void apply(long length) {
                     this.apply$mcVJ$sp(length);
                  }

                  public void apply$mcVJ$sp(long length) {
                     this.offset$1.elem += length;
                     this.$outer.out$1.writeLong(this.offset$1.elem);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.offset$1 = offset$1;
                     }
                  }
               });
            }

            public {
               this.lengths$1 = lengths$1;
               this.out$1 = out$1;
            }
         }, new Serializable(this, out) {
            public static final long serialVersionUID = 0L;
            private final DataOutputStream out$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.out$1.close();
            }

            public {
               this.out$1 = out$1;
            }
         });
         File dataFile = this.getDataFile(shuffleId, mapId);
         synchronized(this) {
            long[] existingLengths = this.checkIndexAndDataFile(indexFile, dataFile, lengths.length);
            if (existingLengths != null) {
               System.arraycopy(existingLengths, 0, lengths, 0, lengths.length);
               if (dataTmp != null && dataTmp.exists()) {
                  BoxesRunTime.boxToBoolean(dataTmp.delete());
               } else {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               }

               BoxesRunTime.boxToBoolean(indexTmp.delete());
            } else {
               if (indexFile.exists()) {
                  BoxesRunTime.boxToBoolean(indexFile.delete());
               } else {
                  BoxedUnit var16 = BoxedUnit.UNIT;
               }

               if (dataFile.exists()) {
                  BoxesRunTime.boxToBoolean(dataFile.delete());
               } else {
                  BoxedUnit var17 = BoxedUnit.UNIT;
               }

               if (!indexTmp.renameTo(indexFile)) {
                  throw new IOException((new StringBuilder()).append((Object)"fail to rename file ").append((Object)indexTmp).append((Object)" to ").append((Object)indexFile).toString());
               }

               if (dataTmp != null && dataTmp.exists() && !dataTmp.renameTo(dataFile)) {
                  throw new IOException((new StringBuilder()).append((Object)"fail to rename file ").append((Object)dataTmp).append((Object)" to ").append((Object)dataFile).toString());
               }

               BoxedUnit var18 = BoxedUnit.UNIT;
            }
         }
      } finally {
         if (indexTmp.exists() && !indexTmp.delete()) {
            this.logError(new Serializable(this, indexTmp) {
               public static final long serialVersionUID = 0L;
               private final File indexTmp$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to delete temporary index file at ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.indexTmp$1.getAbsolutePath()}));
               }

               public {
                  this.indexTmp$1 = indexTmp$1;
               }
            });
         }

      }

   }

   public ManagedBuffer getBlockData(ShuffleBlockId blockId) {
      File indexFile = this.getIndexFile(blockId.shuffleId(), blockId.mapId());
      SeekableByteChannel channel = Files.newByteChannel(indexFile.toPath());
      channel.position((long)(blockId.reduceId() * 8));
      DataInputStream in = new DataInputStream(Channels.newInputStream(channel));

      try {
         long offset = in.readLong();
         long nextOffset = in.readLong();
         long actualPosition = channel.position();
         int expectedPosition = blockId.reduceId() * 8 + 16;
         if (actualPosition != (long)expectedPosition) {
            throw new Exception((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"SPARK-22982: Incorrect channel position after index file reads: "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"expected ", " but actual position was ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(expectedPosition), BoxesRunTime.boxToLong(actualPosition)}))).toString());
         }

         new FileSegmentManagedBuffer(this.transportConf(), this.getDataFile(blockId.shuffleId(), blockId.mapId()), offset, nextOffset - offset);
      } finally {
         in.close();
      }

      return in;
   }

   public void stop() {
   }

   public IndexShuffleBlockResolver(SparkConf conf, BlockManager _blockManager) {
      this._blockManager = _blockManager;
      Logging$class.$init$(this);
      this.transportConf = SparkTransportConf$.MODULE$.fromSparkConf(conf, "shuffle", SparkTransportConf$.MODULE$.fromSparkConf$default$3());
   }
}
