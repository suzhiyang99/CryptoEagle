package org.apache.spark.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.security.CryptoStreamUtils$;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.io.ChunkedByteBuffer;
import org.slf4j.Logger;
import org.spark_project.guava.io.Closeables;
import scala.Function0;
import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rb!B\u0001\u0003\u0001\u0011Q!!\u0003#jg.\u001cFo\u001c:f\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c2\u0001A\u0006\u0012!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!#F\u0007\u0002')\u0011A\u0003B\u0001\tS:$XM\u001d8bY&\u0011ac\u0005\u0002\b\u0019><w-\u001b8h\u0011!A\u0002A!A!\u0002\u0013Q\u0012\u0001B2p]\u001a\u001c\u0001\u0001\u0005\u0002\u001c95\tA!\u0003\u0002\u001e\t\tI1\u000b]1sW\u000e{gN\u001a\u0005\t?\u0001\u0011\t\u0011)A\u0005A\u0005YA-[:l\u001b\u0006t\u0017mZ3s!\t\t#%D\u0001\u0003\u0013\t\u0019#A\u0001\tESN\\'\t\\8dW6\u000bg.Y4fe\"AQ\u0005\u0001B\u0001B\u0003%a%A\btK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s!\tYr%\u0003\u0002)\t\ty1+Z2ve&$\u00180T1oC\u001e,'\u000fC\u0003+\u0001\u0011\u00051&\u0001\u0004=S:LGO\u0010\u000b\u0005Y5rs\u0006\u0005\u0002\"\u0001!)\u0001$\u000ba\u00015!)q$\u000ba\u0001A!)Q%\u000ba\u0001M!9\u0011\u0007\u0001b\u0001\n\u0013\u0011\u0014!E7j]6+Wn\u001c:z\u001b\u0006\u0004()\u001f;fgV\t1\u0007\u0005\u0002\ri%\u0011Q'\u0004\u0002\u0005\u0019>tw\r\u0003\u00048\u0001\u0001\u0006IaM\u0001\u0013[&tW*Z7pefl\u0015\r\u001d\"zi\u0016\u001c\b\u0005C\u0004:\u0001\t\u0007I\u0011\u0002\u001a\u0002#5\f\u00070T3n_JLX*\u00199CsR,7\u000f\u0003\u0004<\u0001\u0001\u0006IaM\u0001\u0013[\u0006DX*Z7pefl\u0015\r\u001d\"zi\u0016\u001c\b\u0005C\u0004>\u0001\t\u0007I\u0011\u0002 \u0002\u0015\tdwnY6TSj,7/F\u0001@!\u0011\u0001u)S\u001a\u000e\u0003\u0005S!AQ\"\u0002\u0015\r|gnY;se\u0016tGO\u0003\u0002E\u000b\u0006!Q\u000f^5m\u0015\u00051\u0015\u0001\u00026bm\u0006L!\u0001S!\u0003#\r{gnY;se\u0016tG\u000fS1tQ6\u000b\u0007\u000f\u0005\u0002\"\u0015&\u00111J\u0001\u0002\b\u00052|7m[%e\u0011\u0019i\u0005\u0001)A\u0005\u007f\u0005Y!\r\\8dWNK'0Z:!\u0011\u0015y\u0005\u0001\"\u0001Q\u0003\u001d9W\r^*ju\u0016$\"aM)\t\u000bIs\u0005\u0019A%\u0002\u000f\tdwnY6JI\")A\u000b\u0001C\u0001+\u0006\u0019\u0001/\u001e;\u0015\u0005Y;GCA,[!\ta\u0001,\u0003\u0002Z\u001b\t!QK\\5u\u0011\u0015Y6\u000b1\u0001]\u0003%9(/\u001b;f\rVt7\r\u0005\u0003\r;~;\u0016B\u00010\u000e\u0005%1UO\\2uS>t\u0017\u0007\u0005\u0002aK6\t\u0011M\u0003\u0002cG\u0006A1\r[1o]\u0016d7O\u0003\u0002e\u000b\u0006\u0019a.[8\n\u0005\u0019\f'aE,sSR\f'\r\\3CsR,7\t[1o]\u0016d\u0007\"\u0002*T\u0001\u0004I\u0005\"B5\u0001\t\u0003Q\u0017\u0001\u00039vi\nKH/Z:\u0015\u0007][G\u000eC\u0003SQ\u0002\u0007\u0011\nC\u0003nQ\u0002\u0007a.A\u0003csR,7\u000f\u0005\u0002pg6\t\u0001O\u0003\u0002re\u0006\u0011\u0011n\u001c\u0006\u0003\t\u0012I!\u0001\u001e9\u0003#\rCWO\\6fI\nKH/\u001a\"vM\u001a,'\u000fC\u0003w\u0001\u0011\u0005q/\u0001\u0005hKR\u0014\u0015\u0010^3t)\tA8\u0010\u0005\u0002\"s&\u0011!P\u0001\u0002\n\u00052|7m\u001b#bi\u0006DQAU;A\u0002%CQ! \u0001\u0005\u0002y\faA]3n_Z,GcA@\u0002\u0006A\u0019A\"!\u0001\n\u0007\u0005\rQBA\u0004C_>dW-\u00198\t\u000bIc\b\u0019A%\t\u000f\u0005%\u0001\u0001\"\u0001\u0002\f\u0005A1m\u001c8uC&t7\u000fF\u0002\u0000\u0003\u001bAaAUA\u0004\u0001\u0004I\u0005bBA\t\u0001\u0011%\u00111C\u0001\r_B,gNR8s/JLG/\u001a\u000b\u0004?\u0006U\u0001\u0002CA\f\u0003\u001f\u0001\r!!\u0007\u0002\t\u0019LG.\u001a\t\u0005\u00037\ty\"\u0004\u0002\u0002\u001e)\u0011\u0011/R\u0005\u0005\u0003C\tiB\u0001\u0003GS2,\u0007"
)
public class DiskStore implements Logging {
   public final SparkConf org$apache$spark$storage$DiskStore$$conf;
   private final DiskBlockManager diskManager;
   private final SecurityManager securityManager;
   private final long minMemoryMapBytes;
   private final long maxMemoryMapBytes;
   private final ConcurrentHashMap<BlockId, Object> blockSizes;
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

   private long minMemoryMapBytes() {
      return this.minMemoryMapBytes;
   }

   private long maxMemoryMapBytes() {
      return this.maxMemoryMapBytes;
   }

   private ConcurrentHashMap<BlockId, Object> blockSizes() {
      return this.blockSizes;
   }

   public long getSize(BlockId blockId) {
      return BoxesRunTime.unboxToLong(this.blockSizes().get(blockId));
   }

   public void put(BlockId blockId, Function1<WritableByteChannel, BoxedUnit> writeFunc) {
      if (this.contains(blockId)) {
         throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " is already present in the disk store"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{blockId})));
      } else {
         this.logDebug(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempting to put block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$1}));
            }

            public {
               this.blockId$1 = blockId$1;
            }
         });
         long startTime = System.currentTimeMillis();
         File file = this.diskManager.getFile(blockId);
         CountingWritableChannel out = new CountingWritableChannel(this.openForWrite(file));
         boolean threwException = true;

         try {
            writeFunc.apply(out);
            this.blockSizes().put(blockId, BoxesRunTime.boxToLong(out.getCount()));
            threwException = false;
         } finally {
            try {
               out.close();
            } catch (IOException var29) {
               if (!threwException) {
                  threwException = true;
                  throw var29;
               }
            } finally {
               if (threwException) {
                  this.remove(blockId);
               }

            }

         }

         long finishTime = System.currentTimeMillis();
         this.logDebug(new Serializable(this, startTime, file, finishTime) {
            public static final long serialVersionUID = 0L;
            private final long startTime$1;
            private final File file$1;
            private final long finishTime$1;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("Block %s stored as %s file on disk in %d ms"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1.getName(), Utils$.MODULE$.bytesToString(this.file$1.length()), BoxesRunTime.boxToLong(this.finishTime$1 - this.startTime$1)}));
            }

            public {
               this.startTime$1 = startTime$1;
               this.file$1 = file$1;
               this.finishTime$1 = finishTime$1;
            }
         });
      }
   }

   public void putBytes(BlockId blockId, ChunkedByteBuffer bytes) {
      this.put(blockId, new Serializable(this, bytes) {
         public static final long serialVersionUID = 0L;
         private final ChunkedByteBuffer bytes$1;

         public final void apply(WritableByteChannel channel) {
            this.bytes$1.writeFully(channel);
         }

         public {
            this.bytes$1 = bytes$1;
         }
      });
   }

   public BlockData getBytes(BlockId blockId) {
      File file = this.diskManager.getFile(blockId.name());
      long blockSize = this.getSize(blockId);
      Option var5 = this.securityManager.getIOEncryptionKey();
      Object var8;
      if (var5 instanceof Some) {
         Some var6 = (Some)var5;
         byte[] key = (byte[])var6.x();
         var8 = new EncryptedBlockData(file, blockSize, this.org$apache$spark$storage$DiskStore$$conf, key);
      } else {
         var8 = new DiskBlockData(this.minMemoryMapBytes(), this.maxMemoryMapBytes(), file, blockSize);
      }

      return (BlockData)var8;
   }

   public boolean remove(BlockId blockId) {
      this.blockSizes().remove(blockId);
      File file = this.diskManager.getFile(blockId.name());
      boolean var10000;
      if (file.exists()) {
         boolean ret = file.delete();
         if (!ret) {
            this.logWarning(new Serializable(this, file) {
               public static final long serialVersionUID = 0L;
               private final File file$2;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$2.getPath()}));
               }

               public {
                  this.file$2 = file$2;
               }
            });
         }

         var10000 = ret;
      } else {
         var10000 = false;
      }

      return var10000;
   }

   public boolean contains(BlockId blockId) {
      File file = this.diskManager.getFile(blockId.name());
      return file.exists();
   }

   private WritableByteChannel openForWrite(File file) {
      FileChannel out = (new FileOutputStream(file)).getChannel();

      try {
         return (WritableByteChannel)this.securityManager.getIOEncryptionKey().map(new Serializable(this, out) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DiskStore $outer;
            private final FileChannel out$1;

            public final WritableByteChannel apply(byte[] key) {
               return CryptoStreamUtils$.MODULE$.createWritableChannel(this.out$1, this.$outer.org$apache$spark$storage$DiskStore$$conf, key);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.out$1 = out$1;
               }
            }
         }).getOrElse(new Serializable(this, out) {
            public static final long serialVersionUID = 0L;
            private final FileChannel out$1;

            public final FileChannel apply() {
               return this.out$1;
            }

            public {
               this.out$1 = out$1;
            }
         });
      } catch (Exception var4) {
         Closeables.close(out, true);
         file.delete();
         throw var4;
      }
   }

   public DiskStore(SparkConf conf, DiskBlockManager diskManager, SecurityManager securityManager) {
      this.org$apache$spark$storage$DiskStore$$conf = conf;
      this.diskManager = diskManager;
      this.securityManager = securityManager;
      Logging$class.$init$(this);
      this.minMemoryMapBytes = conf.getSizeAsBytes("spark.storage.memoryMapThreshold", "2m");
      this.maxMemoryMapBytes = conf.getSizeAsBytes("spark.storage.memoryMapLimitForTests", BoxesRunTime.boxToInteger(Integer.MAX_VALUE).toString());
      this.blockSizes = new ConcurrentHashMap();
   }
}
