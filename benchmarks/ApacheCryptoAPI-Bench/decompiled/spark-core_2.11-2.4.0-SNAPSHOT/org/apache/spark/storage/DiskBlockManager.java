package org.apache.spark.storage;

import [Ljava.io.File;;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.spark.SparkConf;
import org.apache.spark.executor.ExecutorExitCode$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.ShutdownHookManager$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.None$;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayOps;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055b!B\u0001\u0003\u0001\u0011Q!\u0001\u0005#jg.\u0014En\\2l\u001b\u0006t\u0017mZ3s\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c2\u0001A\u0006\u0012!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!#F\u0007\u0002')\u0011A\u0003B\u0001\tS:$XM\u001d8bY&\u0011ac\u0005\u0002\b\u0019><w-\u001b8h\u0011!A\u0002A!A!\u0002\u0013Q\u0012\u0001B2p]\u001a\u001c\u0001\u0001\u0005\u0002\u001c95\tA!\u0003\u0002\u001e\t\tI1\u000b]1sW\u000e{gN\u001a\u0005\t?\u0001\u0011\t\u0011)A\u0005A\u0005\tB-\u001a7fi\u00164\u0015\u000e\\3t\u001f:\u001cFo\u001c9\u0011\u00051\t\u0013B\u0001\u0012\u000e\u0005\u001d\u0011un\u001c7fC:DQ\u0001\n\u0001\u0005\u0002\u0015\na\u0001P5oSRtDc\u0001\u0014)SA\u0011q\u0005A\u0007\u0002\u0005!)\u0001d\ta\u00015!)qd\ta\u0001A!A1\u0006\u0001b\u0001\n\u0003!A&\u0001\ntk\n$\u0015N]:QKJdunY1m\t&\u0014X#A\u0017\u0011\u00051q\u0013BA\u0018\u000e\u0005\rIe\u000e\u001e\u0005\u0007c\u0001\u0001\u000b\u0011B\u0017\u0002'M,(\rR5sgB+'\u000fT8dC2$\u0015N\u001d\u0011\t\u0011M\u0002!\u0019!C\u0001\tQ\n\u0011\u0002\\8dC2$\u0015N]:\u0016\u0003U\u00022\u0001\u0004\u001c9\u0013\t9TBA\u0003BeJ\f\u0017\u0010\u0005\u0002:}5\t!H\u0003\u0002<y\u0005\u0011\u0011n\u001c\u0006\u0002{\u0005!!.\u0019<b\u0013\ty$H\u0001\u0003GS2,\u0007BB!\u0001A\u0003%Q'\u0001\u0006m_\u000e\fG\u000eR5sg\u0002Bqa\u0011\u0001C\u0002\u0013%A)A\u0004tk\n$\u0015N]:\u0016\u0003\u0015\u00032\u0001\u0004\u001c6\u0011\u00199\u0005\u0001)A\u0005\u000b\u0006A1/\u001e2ESJ\u001c\b\u0005C\u0004J\u0001\t\u0007I\u0011\u0002&\u0002\u0019MDW\u000f\u001e3po:Dun\\6\u0016\u0003-Aa\u0001\u0014\u0001!\u0002\u0013Y\u0011!D:ikR$wn\u001e8I_>\\\u0007\u0005C\u0003O\u0001\u0011\u0005q*A\u0004hKR4\u0015\u000e\\3\u0015\u0005a\u0002\u0006\"B)N\u0001\u0004\u0011\u0016\u0001\u00034jY\u0016t\u0017-\\3\u0011\u0005M3fB\u0001\u0007U\u0013\t)V\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003/b\u0013aa\u0015;sS:<'BA+\u000e\u0011\u0015q\u0005\u0001\"\u0001[)\tA4\fC\u0003]3\u0002\u0007Q,A\u0004cY>\u001c7.\u00133\u0011\u0005\u001dr\u0016BA0\u0003\u0005\u001d\u0011En\\2l\u0013\u0012DQ!\u0019\u0001\u0005\u0002\t\fQbY8oi\u0006Lgn\u001d\"m_\u000e\\GC\u0001\u0011d\u0011\u0015a\u0006\r1\u0001^\u0011\u0015)\u0007\u0001\"\u0001g\u0003-9W\r^!mY\u001aKG.Z:\u0015\u0003\u001d\u00042\u0001\u001b99\u001d\tIgN\u0004\u0002k[6\t1N\u0003\u0002m3\u00051AH]8pizJ\u0011AD\u0005\u0003_6\tq\u0001]1dW\u0006<W-\u0003\u0002re\n\u00191+Z9\u000b\u0005=l\u0001\"\u0002;\u0001\t\u0003)\u0018\u0001D4fi\u0006cGN\u00117pG.\u001cH#\u0001<\u0011\u0007!\u0004X\fC\u0003y\u0001\u0011\u0005\u00110\u0001\u000bde\u0016\fG/\u001a+f[BdunY1m\u00052|7m\u001b\u000b\u0002uB!Ab_?9\u0013\taXB\u0001\u0004UkBdWM\r\t\u0003OyL!a \u0002\u0003!Q+W\u000e\u001d'pG\u0006d'\t\\8dW&#\u0007bBA\u0002\u0001\u0011\u0005\u0011QA\u0001\u0017GJ,\u0017\r^3UK6\u00048\u000b[;gM2,'\t\\8dWR\u0011\u0011q\u0001\t\u0006\u0019m\fI\u0001\u000f\t\u0004O\u0005-\u0011bAA\u0007\u0005\t\u0011B+Z7q'\",hM\u001a7f\u00052|7m[%e\u0011\u001d\t\t\u0002\u0001C\u0005\u0003'\tqb\u0019:fCR,Gj\\2bY\u0012K'o\u001d\u000b\u0004k\u0005U\u0001B\u0002\r\u0002\u0010\u0001\u0007!\u0004C\u0004\u0002\u001a\u0001!I!a\u0007\u0002\u001f\u0005$Gm\u00155vi\u0012|wO\u001c%p_.$\u0012a\u0003\u0005\t\u0003?\u0001A\u0011\u0001\u0003\u0002\"\u0005!1\u000f^8q)\t\t\u0019\u0003E\u0002\r\u0003KI1!a\n\u000e\u0005\u0011)f.\u001b;\t\u000f\u0005-\u0002\u0001\"\u0003\u0002\"\u00051Am\\*u_B\u0004"
)
public class DiskBlockManager implements Logging {
   private final boolean deleteFilesOnStop;
   private final int subDirsPerLocalDir;
   private final File[] localDirs;
   private final File[][] subDirs;
   private final Object shutdownHook;
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

   public int subDirsPerLocalDir() {
      return this.subDirsPerLocalDir;
   }

   public File[] localDirs() {
      return this.localDirs;
   }

   private File[][] subDirs() {
      return this.subDirs;
   }

   private Object shutdownHook() {
      return this.shutdownHook;
   }

   public File getFile(String filename) {
      int hash = Utils$.MODULE$.nonNegativeHash(filename);
      int dirId = hash % this.localDirs().length;
      int subDirId = hash / this.localDirs().length % this.subDirsPerLocalDir();
      File var7;
      synchronized(this.subDirs()[dirId]) {
         File old = this.subDirs()[dirId][subDirId];
         File var10000;
         if (old == null) {
            File newDir = new File(this.localDirs()[dirId], (new StringOps(Predef$.MODULE$.augmentString("%02x"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(subDirId)})));
            if (!newDir.exists() && !newDir.mkdir()) {
               throw new IOException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to create local dir in ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{newDir})));
            }

            this.subDirs()[dirId][subDirId] = newDir;
            var10000 = newDir;
         } else {
            var10000 = old;
         }

         var7 = var10000;
      }

      File subDir = var7;
      return new File(subDir, filename);
   }

   public File getFile(BlockId blockId) {
      return this.getFile(blockId.name());
   }

   public boolean containsBlock(BlockId blockId) {
      return this.getFile(blockId.name()).exists();
   }

   public Seq<File> getAllFiles() {
      return (Seq)Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])this.subDirs()).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayOps<File> apply(File[] dir) {
            Predef$ var10000 = Predef$.MODULE$;
            Object var3;
            synchronized(dir) {
               var3 = ((File;)dir).clone();
            }

            return var10000.<File>refArrayOps(var3);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(File.class)))).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(File x$1) {
            return x$1 != null;
         }
      })).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final SeqLike<File, Object> apply(File dir) {
            File[] files = dir.listFiles();
            return (SeqLike<File, Object>)(files != null ? Predef$.MODULE$.refArrayOps((Object[])files) : (SeqLike)Seq$.MODULE$.empty());
         }
      }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
   }

   public Seq<BlockId> getAllBlocks() {
      return (Seq)this.getAllFiles().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Iterable<BlockId> apply(File f) {
            Iterable var10000;
            try {
               var10000 = Option$.MODULE$.option2Iterable(new Some(BlockId$.MODULE$.apply(f.getName())));
            } catch (UnrecognizedBlockId var2) {
               var10000 = Option$.MODULE$.option2Iterable(None$.MODULE$);
            }

            return var10000;
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public Tuple2<TempLocalBlockId, File> createTempLocalBlock() {
      TempLocalBlockId blockId;
      for(blockId = new TempLocalBlockId(UUID.randomUUID()); this.getFile((BlockId)blockId).exists(); blockId = new TempLocalBlockId(UUID.randomUUID())) {
      }

      return new Tuple2<TempLocalBlockId, File>(blockId, this.getFile((BlockId)blockId));
   }

   public Tuple2<TempShuffleBlockId, File> createTempShuffleBlock() {
      TempShuffleBlockId blockId;
      for(blockId = new TempShuffleBlockId(UUID.randomUUID()); this.getFile((BlockId)blockId).exists(); blockId = new TempShuffleBlockId(UUID.randomUUID())) {
      }

      return new Tuple2<TempShuffleBlockId, File>(blockId, this.getFile((BlockId)blockId));
   }

   private File[] createLocalDirs(SparkConf conf) {
      return (File[])Predef$.MODULE$.refArrayOps((Object[])Utils$.MODULE$.getConfiguredLocalDirs(conf)).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockManager $outer;

         public final Iterable<File> apply(String rootDir) {
            Iterable var10000;
            try {
               File localDir = Utils$.MODULE$.createDirectory(rootDir, "blockmgr");
               this.$outer.logInfo(new Serializable(this, localDir) {
                  public static final long serialVersionUID = 0L;
                  private final File localDir$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Created local directory at ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.localDir$1}));
                  }

                  public {
                     this.localDir$1 = localDir$1;
                  }
               });
               var10000 = Option$.MODULE$.option2Iterable(new Some(localDir));
            } catch (IOException var4) {
               this.$outer.logError(new Serializable(this, rootDir) {
                  public static final long serialVersionUID = 0L;
                  private final String rootDir$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to create local dir in ", ". Ignoring this directory."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rootDir$1}));
                  }

                  public {
                     this.rootDir$1 = rootDir$1;
                  }
               }, var4);
               var10000 = Option$.MODULE$.option2Iterable(None$.MODULE$);
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(File.class)));
   }

   private Object addShutdownHook() {
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Adding shutdown hook";
         }
      });
      return ShutdownHookManager$.MODULE$.addShutdownHook(ShutdownHookManager$.MODULE$.TEMP_DIR_SHUTDOWN_PRIORITY() + 1, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockManager $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Shutdown hook called";
               }
            });
            this.$outer.org$apache$spark$storage$DiskBlockManager$$doStop();
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

   public void stop() {
      try {
         BoxesRunTime.boxToBoolean(ShutdownHookManager$.MODULE$.removeShutdownHook(this.shutdownHook()));
      } catch (Exception var2) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception while removing shutdown hook."})))).s(Nil$.MODULE$);
            }
         }, var2);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this.org$apache$spark$storage$DiskBlockManager$$doStop();
   }

   public void org$apache$spark$storage$DiskBlockManager$$doStop() {
      if (this.deleteFilesOnStop) {
         Predef$.MODULE$.refArrayOps((Object[])this.localDirs()).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DiskBlockManager $outer;

            public final void apply(File localDir) {
               if (localDir.isDirectory() && localDir.exists()) {
                  try {
                     if (!ShutdownHookManager$.MODULE$.hasRootAsShutdownDeleteDir(localDir)) {
                        Utils$.MODULE$.deleteRecursively(localDir);
                     }
                  } catch (Exception var3) {
                     this.$outer.logError(new Serializable(this, localDir) {
                        public static final long serialVersionUID = 0L;
                        private final File localDir$2;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception while deleting local spark dir: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.localDir$2}));
                        }

                        public {
                           this.localDir$2 = localDir$2;
                        }
                     }, var3);
                  }
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

   }

   public DiskBlockManager(SparkConf conf, boolean deleteFilesOnStop) {
      this.deleteFilesOnStop = deleteFilesOnStop;
      Logging$class.$init$(this);
      this.subDirsPerLocalDir = conf.getInt("spark.diskStore.subDirectories", 64);
      this.localDirs = this.createLocalDirs(conf);
      if (Predef$.MODULE$.refArrayOps((Object[])this.localDirs()).isEmpty()) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Failed to create any local dir.";
            }
         });
         System.exit(ExecutorExitCode$.MODULE$.DISK_STORE_FAILED_TO_CREATE_DIR());
      }

      this.subDirs = (File[][])Array$.MODULE$.fill(this.localDirs().length, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DiskBlockManager $outer;

         public final File[] apply() {
            return new File[this.$outer.subDirsPerLocalDir()];
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(File.class)));
      this.shutdownHook = this.addShutdownHook();
   }
}
