package org.apache.spark.rpc.netty;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.network.buffer.FileSegmentManagedBuffer;
import org.apache.spark.network.buffer.ManagedBuffer;
import org.apache.spark.network.server.StreamManager;
import org.apache.spark.rpc.RpcEnvFileServer;
import org.apache.spark.rpc.RpcEnvFileServer$class;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.SeqLike;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001M4Q!\u0001\u0002\u0001\u00051\u0011!CT3uif\u001cFO]3b[6\u000bg.Y4fe*\u00111\u0001B\u0001\u0006]\u0016$H/\u001f\u0006\u0003\u000b\u0019\t1A\u001d9d\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e+A\u0011abE\u0007\u0002\u001f)\u0011\u0001#E\u0001\u0007g\u0016\u0014h/\u001a:\u000b\u0005I1\u0011a\u00028fi^|'o[\u0005\u0003)=\u0011Qb\u0015;sK\u0006lW*\u00198bO\u0016\u0014\bC\u0001\f\u0018\u001b\u0005!\u0011B\u0001\r\u0005\u0005A\u0011\u0006oY#om\u001aKG.Z*feZ,'\u000f\u0003\u0005\u001b\u0001\t\u0005\t\u0015!\u0003\u001d\u0003\u0019\u0011\boY#om\u000e\u0001\u0001CA\u000f\u001f\u001b\u0005\u0011\u0011BA\u0010\u0003\u0005-qU\r\u001e;z%B\u001cWI\u001c<\t\u000b\u0005\u0002A\u0011\u0001\u0012\u0002\rqJg.\u001b;?)\t\u0019C\u0005\u0005\u0002\u001e\u0001!)!\u0004\ta\u00019!9a\u0005\u0001b\u0001\n\u00139\u0013!\u00024jY\u0016\u001cX#\u0001\u0015\u0011\t%\u0002$\u0007P\u0007\u0002U)\u00111\u0006L\u0001\u000bG>t7-\u001e:sK:$(BA\u0017/\u0003\u0011)H/\u001b7\u000b\u0003=\nAA[1wC&\u0011\u0011G\u000b\u0002\u0012\u0007>t7-\u001e:sK:$\b*Y:i\u001b\u0006\u0004\bCA\u001a:\u001d\t!t'D\u00016\u0015\u00051\u0014!B:dC2\f\u0017B\u0001\u001d6\u0003\u0019\u0001&/\u001a3fM&\u0011!h\u000f\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005a*\u0004CA\u001fA\u001b\u0005q$BA /\u0003\tIw.\u0003\u0002B}\t!a)\u001b7f\u0011\u0019\u0019\u0005\u0001)A\u0005Q\u00051a-\u001b7fg\u0002Bq!\u0012\u0001C\u0002\u0013%q%\u0001\u0003kCJ\u001c\bBB$\u0001A\u0003%\u0001&A\u0003kCJ\u001c\b\u0005C\u0004J\u0001\t\u0007I\u0011B\u0014\u0002\t\u0011L'o\u001d\u0005\u0007\u0017\u0002\u0001\u000b\u0011\u0002\u0015\u0002\u000b\u0011L'o\u001d\u0011\t\u000b5\u0003A\u0011\t(\u0002\u0011\u001d,Go\u00115v].$2aT+[!\t\u00016+D\u0001R\u0015\t\u0011\u0016#\u0001\u0004ck\u001a4WM]\u0005\u0003)F\u0013Q\"T1oC\u001e,GMQ;gM\u0016\u0014\b\"\u0002,M\u0001\u00049\u0016\u0001C:ue\u0016\fW.\u00133\u0011\u0005QB\u0016BA-6\u0005\u0011auN\\4\t\u000bmc\u0005\u0019\u0001/\u0002\u0015\rDWO\\6J]\u0012,\u0007\u0010\u0005\u00025;&\u0011a,\u000e\u0002\u0004\u0013:$\b\"\u00021\u0001\t\u0003\n\u0017AC8qK:\u001cFO]3b[R\u0011qJ\u0019\u0005\u0006-~\u0003\rA\r\u0005\u0006I\u0002!\t%Z\u0001\bC\u0012$g)\u001b7f)\t\u0011d\rC\u0003hG\u0002\u0007A(\u0001\u0003gS2,\u0007\"B5\u0001\t\u0003R\u0017AB1eI*\u000b'\u000f\u0006\u00023W\")q\r\u001ba\u0001y!)Q\u000e\u0001C!]\u0006a\u0011\r\u001a3ESJ,7\r^8ssR\u0019!g\\9\t\u000bAd\u0007\u0019\u0001\u001a\u0002\u000f\t\f7/Z+sS\")!\u000f\u001ca\u0001y\u0005!\u0001/\u0019;i\u0001"
)
public class NettyStreamManager extends StreamManager implements RpcEnvFileServer {
   private final NettyRpcEnv rpcEnv;
   private final ConcurrentHashMap<String, File> files;
   private final ConcurrentHashMap<String, File> jars;
   private final ConcurrentHashMap<String, File> dirs;

   public String validateDirectoryUri(String baseUri) {
      return RpcEnvFileServer$class.validateDirectoryUri(this, baseUri);
   }

   private ConcurrentHashMap<String, File> files() {
      return this.files;
   }

   private ConcurrentHashMap<String, File> jars() {
      return this.jars;
   }

   private ConcurrentHashMap<String, File> dirs() {
      return this.dirs;
   }

   public ManagedBuffer getChunk(long streamId, int chunkIndex) {
      throw new UnsupportedOperationException();
   }

   public ManagedBuffer openStream(String streamId) {
      String[] var3 = (new StringOps(Predef$.MODULE$.augmentString(streamId))).stripPrefix("/").split("/", 2);
      Option var4 = Array$.MODULE$.unapplySeq(var3);
      if (!var4.isEmpty() && var4.get() != null && ((SeqLike)var4.get()).lengthCompare(2) == 0) {
         String ftype = (String)((SeqLike)var4.get()).apply(0);
         String fname = (String)((SeqLike)var4.get()).apply(1);
         Tuple2 var7 = new Tuple2(ftype, fname);
         String ftype = (String)var7._1();
         String fname = (String)var7._2();
         File file;
         if ("files".equals(ftype)) {
            file = (File)this.files().get(fname);
         } else if ("jars".equals(ftype)) {
            file = (File)this.jars().get(fname);
         } else {
            File dir = (File)this.dirs().get(ftype);
            Predef$.MODULE$.require(dir != null, new Serializable(this, ftype) {
               public static final long serialVersionUID = 0L;
               private final String ftype$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid stream URI: ", " not found."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.ftype$1}));
               }

               public {
                  this.ftype$1 = ftype$1;
               }
            });
            file = new File(dir, fname);
         }

         return file != null && file.isFile() ? new FileSegmentManagedBuffer(this.rpcEnv.transportConf(), file, 0L, file.length()) : null;
      } else {
         throw new MatchError(var3);
      }
   }

   public String addFile(File file) {
      File existingPath;
      Predef$ var10000;
      boolean var10001;
      label24: {
         existingPath = (File)this.files().putIfAbsent(file.getName(), file);
         var10000 = Predef$.MODULE$;
         if (existingPath != null) {
            label23: {
               if (existingPath == null) {
                  if (file == null) {
                     break label23;
                  }
               } else if (existingPath.equals(file)) {
                  break label23;
               }

               var10001 = false;
               break label24;
            }
         }

         var10001 = true;
      }

      var10000.require(var10001, new Serializable(this, file, existingPath) {
         public static final long serialVersionUID = 0L;
         private final File file$1;
         private final File existingPath$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"File ", " was already registered with a different path "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1.getName()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(old path = ", ", new path = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.existingPath$1, this.file$1}))).toString();
         }

         public {
            this.file$1 = file$1;
            this.existingPath$1 = existingPath$1;
         }
      });
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/files/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rpcEnv.address().toSparkURL(), Utils$.MODULE$.encodeFileNameToURIRawPath(file.getName())}));
   }

   public String addJar(File file) {
      File existingPath;
      Predef$ var10000;
      boolean var10001;
      label24: {
         existingPath = (File)this.jars().putIfAbsent(file.getName(), file);
         var10000 = Predef$.MODULE$;
         if (existingPath != null) {
            label23: {
               if (existingPath == null) {
                  if (file == null) {
                     break label23;
                  }
               } else if (existingPath.equals(file)) {
                  break label23;
               }

               var10001 = false;
               break label24;
            }
         }

         var10001 = true;
      }

      var10000.require(var10001, new Serializable(this, file, existingPath) {
         public static final long serialVersionUID = 0L;
         private final File file$2;
         private final File existingPath$2;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"File ", " was already registered with a different path "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$2.getName()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(old path = ", ", new path = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.existingPath$2, this.file$2}))).toString();
         }

         public {
            this.file$2 = file$2;
            this.existingPath$2 = existingPath$2;
         }
      });
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/jars/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rpcEnv.address().toSparkURL(), Utils$.MODULE$.encodeFileNameToURIRawPath(file.getName())}));
   }

   public String addDirectory(String baseUri, File path) {
      String fixedBaseUri = this.validateDirectoryUri(baseUri);
      Predef$.MODULE$.require(this.dirs().putIfAbsent((new StringOps(Predef$.MODULE$.augmentString(fixedBaseUri))).stripPrefix("/"), path) == null, new Serializable(this, fixedBaseUri) {
         public static final long serialVersionUID = 0L;
         private final String fixedBaseUri$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"URI '", "' already registered."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.fixedBaseUri$1}));
         }

         public {
            this.fixedBaseUri$1 = fixedBaseUri$1;
         }
      });
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rpcEnv.address().toSparkURL(), fixedBaseUri}));
   }

   public NettyStreamManager(NettyRpcEnv rpcEnv) {
      this.rpcEnv = rpcEnv;
      RpcEnvFileServer$class.$init$(this);
      this.files = new ConcurrentHashMap();
      this.jars = new ConcurrentHashMap();
      this.dirs = new ConcurrentHashMap();
   }
}
