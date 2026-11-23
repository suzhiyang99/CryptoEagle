package org.apache.spark.util;

import io.netty.channel.unix.Errors;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.lang3.SystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext$;
import org.apache.spark.TaskContextImpl;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.network.util.JavaUtils;
import org.apache.spark.security.GroupMappingServiceProvider;
import org.apache.spark.serializer.DeserializationStream;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.SerializerInstance;
import org.json4s.JsonAST;
import org.json4s.package.;
import org.slf4j.Logger;
import org.spark_project.guava.cache.CacheBuilder;
import org.spark_project.guava.cache.CacheLoader;
import org.spark_project.guava.cache.LoadingCache;
import org.spark_project.guava.io.ByteStreams;
import org.spark_project.guava.net.InetAddresses;
import org.spark_project.jetty.util.MultiException;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.None$;
import scala.NotImplementedError;
import scala.Option;
import scala.Option$;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.BufferedIterator;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.IterableLike;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.JavaConverters$;
import scala.collection.LinearSeqOptimized;
import scala.collection.Map;
import scala.collection.Map$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SetLike;
import scala.collection.Traversable;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.StringOps;
import scala.collection.immutable.Vector;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.ArrayOps;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Set$;
import scala.collection.mutable.StringBuilder;
import scala.io.Codec$;
import scala.io.Source$;
import scala.math.BigDecimal;
import scala.math.BigDecimal$;
import scala.math.BigInt;
import scala.math.BigInt$;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.BooleanRef;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.LongRef;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;
import scala.util.Try$;
import scala.util.control.ControlThrowable;
import scala.util.control.NonFatal$;
import scala.util.matching.Regex;

public final class Utils$ implements Logging {
   public static final Utils$ MODULE$;
   private final Random random;
   private final SparkUncaughtExceptionHandler sparkUncaughtExceptionHandler;
   private final int DEFAULT_DRIVER_MEM_MB;
   private final int MAX_DIR_CREATION_ATTEMPTS;
   private volatile String[] localRootDirs;
   private final int DEFAULT_MAX_TO_STRING_FIELDS;
   private final AtomicBoolean truncationWarningPrinted;
   private InetAddress org$apache$spark$util$Utils$$localIpAddress;
   private Option<String> customHostname;
   private final ConcurrentHashMap<String, Tuple2<String, Object>> hostPortParseResults;
   private final String UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE_CONF;
   private final int DEFAULT_UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE;
   private LoadingCache<String, Long> compressedLogFileLengthCache;
   private final boolean isWindows;
   private final boolean isMac;
   private final Regex windowsDrive;
   private final Set<String> EMPTY_USER_GROUPS;
   private final String BACKUP_STANDALONE_MASTER_PREFIX;
   private final String REDACTION_REPLACEMENT_TEXT;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private volatile boolean bitmap$0;

   static {
      new Utils$();
   }

   private InetAddress org$apache$spark$util$Utils$$localIpAddress$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.org$apache$spark$util$Utils$$localIpAddress = this.findLocalInetAddress();
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.org$apache$spark$util$Utils$$localIpAddress;
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

   public Random random() {
      return this.random;
   }

   private SparkUncaughtExceptionHandler sparkUncaughtExceptionHandler() {
      return this.sparkUncaughtExceptionHandler;
   }

   public int DEFAULT_DRIVER_MEM_MB() {
      return this.DEFAULT_DRIVER_MEM_MB;
   }

   private int MAX_DIR_CREATION_ATTEMPTS() {
      return this.MAX_DIR_CREATION_ATTEMPTS;
   }

   private String[] localRootDirs() {
      return this.localRootDirs;
   }

   private void localRootDirs_$eq(String[] x$1) {
      this.localRootDirs = x$1;
   }

   public int DEFAULT_MAX_TO_STRING_FIELDS() {
      return this.DEFAULT_MAX_TO_STRING_FIELDS;
   }

   private int maxNumToStringFields() {
      return SparkEnv$.MODULE$.get() == null ? this.DEFAULT_MAX_TO_STRING_FIELDS() : SparkEnv$.MODULE$.get().conf().getInt("spark.debug.maxToStringFields", this.DEFAULT_MAX_TO_STRING_FIELDS());
   }

   private AtomicBoolean truncationWarningPrinted() {
      return this.truncationWarningPrinted;
   }

   public <T> String truncatedString(Seq<T> seq, String start, String sep, String end, int maxNumFields) {
      String var10000;
      if (seq.length() > maxNumFields) {
         if (this.truncationWarningPrinted().compareAndSet(false, true)) {
            this.logWarning(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Truncated the string representation of a plan since it was too large. This behavior can be adjusted by setting 'spark.debug.maxToStringFields' in SparkEnv.conf.";
               }
            });
         }

         int numFields = scala.math.package$.MODULE$.max(0, maxNumFields - 1);
         var10000 = ((TraversableOnce)seq.take(numFields)).mkString(start, sep, (new StringBuilder()).append((Object)sep).append((Object)"... ").append((Object)BoxesRunTime.boxToInteger(seq.length() - numFields)).append((Object)" more fields").append((Object)end).toString());
      } else {
         var10000 = seq.mkString(start, sep, end);
      }

      return var10000;
   }

   public <T> String truncatedString(Seq<T> seq, String sep) {
      return this.truncatedString(seq, "", sep, "", this.truncatedString$default$5());
   }

   public <T> int truncatedString$default$5() {
      return this.maxNumToStringFields();
   }

   public <T> byte[] serialize(T o) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(o);
      oos.close();
      return bos.toByteArray();
   }

   public <T> T deserialize(byte[] bytes) {
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bis);
      return (T)ois.readObject();
   }

   public <T> T deserialize(byte[] bytes, ClassLoader loader) {
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(loader, bis) {
         private final ClassLoader loader$1;

         public Class<?> resolveClass(ObjectStreamClass desc) {
            return Class.forName(desc.getName(), false, this.loader$1);
         }

         public {
            this.loader$1 = loader$1;
         }
      };
      return (T)ois.readObject();
   }

   public long deserializeLongValue(byte[] bytes) {
      long result = (long)bytes[7] & 255L;
      result += ((long)bytes[6] & 255L) << 8;
      result += ((long)bytes[5] & 255L) << 16;
      result += ((long)bytes[4] & 255L) << 24;
      result += ((long)bytes[3] & 255L) << 32;
      result += ((long)bytes[2] & 255L) << 40;
      result += ((long)bytes[1] & 255L) << 48;
      return result + (((long)bytes[0] & 255L) << 56);
   }

   public void serializeViaNestedStream(OutputStream os, SerializerInstance ser, Function1<SerializationStream, BoxedUnit> f) {
      SerializationStream osWrapper = ser.serializeStream(new OutputStream(os) {
         private final OutputStream os$1;

         public void write(int b) {
            this.os$1.write(b);
         }

         public void write(byte[] b, int off, int len) {
            this.os$1.write(b, off, len);
         }

         public {
            this.os$1 = os$1;
         }
      });

      try {
         f.apply(osWrapper);
      } finally {
         osWrapper.close();
      }

   }

   public void deserializeViaNestedStream(InputStream is, SerializerInstance ser, Function1<DeserializationStream, BoxedUnit> f) {
      DeserializationStream isWrapper = ser.deserializeStream(new InputStream(is) {
         private final InputStream is$1;

         public int read() {
            return this.is$1.read();
         }

         public int read(byte[] b, int off, int len) {
            return this.is$1.read(b, off, len);
         }

         public {
            this.is$1 = is$1;
         }
      });

      try {
         f.apply(isWrapper);
      } finally {
         isWrapper.close();
      }

   }

   public ClassLoader getSparkClassLoader() {
      return this.getClass().getClassLoader();
   }

   public ClassLoader getContextOrSparkClassLoader() {
      return (ClassLoader)Option$.MODULE$.apply(Thread.currentThread().getContextClassLoader()).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final ClassLoader apply() {
            return Utils$.MODULE$.getSparkClassLoader();
         }
      });
   }

   public boolean classIsLoadable(String clazz) {
      return Try$.MODULE$.apply(new Serializable(clazz) {
         public static final long serialVersionUID = 0L;
         private final String clazz$1;

         public final Class<Object> apply() {
            return Class.forName(this.clazz$1, false, Utils$.MODULE$.getContextOrSparkClassLoader());
         }

         public {
            this.clazz$1 = clazz$1;
         }
      }).isSuccess();
   }

   public Class<?> classForName(String className) {
      return Class.forName(className, true, this.getContextOrSparkClassLoader());
   }

   public void writeByteBuffer(ByteBuffer bb, DataOutput out) {
      if (bb.hasArray()) {
         out.write(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
      } else {
         int originalPosition = bb.position();
         byte[] bbval = new byte[bb.remaining()];
         bb.get(bbval);
         out.write(bbval);
         bb.position(originalPosition);
      }

   }

   public void writeByteBuffer(ByteBuffer bb, OutputStream out) {
      if (bb.hasArray()) {
         out.write(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
      } else {
         int originalPosition = bb.position();
         byte[] bbval = new byte[bb.remaining()];
         bb.get(bbval);
         out.write(bbval);
         bb.position(originalPosition);
      }

   }

   public boolean chmod700(File file) {
      return file.setReadable(false, false) && file.setReadable(true, true) && file.setWritable(false, false) && file.setWritable(true, true) && file.setExecutable(false, false) && file.setExecutable(true, true);
   }

   public File createDirectory(String root, String namePrefix) {
      int attempts = 0;
      int maxAttempts = this.MAX_DIR_CREATION_ATTEMPTS();
      File dir = null;

      while(dir == null) {
         ++attempts;
         if (attempts > maxAttempts) {
            throw new IOException((new StringBuilder()).append((Object)"Failed to create a temp directory (under ").append((Object)root).append((Object)") after ").append((Object)BoxesRunTime.boxToInteger(maxAttempts)).append((Object)" attempts!").toString());
         }

         try {
            dir = new File(root, (new StringBuilder()).append((Object)namePrefix).append((Object)"-").append((Object)UUID.randomUUID().toString()).toString());
            if (dir.exists() || !dir.mkdirs()) {
               dir = null;
            }
         } catch (SecurityException var7) {
            dir = null;
         }
      }

      return dir.getCanonicalFile();
   }

   public String createDirectory$default$2() {
      return "spark";
   }

   public File createTempDir(String root, String namePrefix) {
      File dir = this.createDirectory(root, namePrefix);
      ShutdownHookManager$.MODULE$.registerShutdownDeleteDir(dir);
      return dir;
   }

   public String createTempDir$default$1() {
      return System.getProperty("java.io.tmpdir");
   }

   public String createTempDir$default$2() {
      return "spark";
   }

   public long copyStream(InputStream in, OutputStream out, boolean closeStreams, boolean transferToEnabled) {
      return BoxesRunTime.unboxToLong(this.tryWithSafeFinally(new Serializable(in, out, transferToEnabled) {
         public static final long serialVersionUID = 0L;
         private final InputStream in$1;
         private final OutputStream out$1;
         private final boolean transferToEnabled$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            long var10000;
            if (this.in$1 instanceof FileInputStream && this.out$1 instanceof FileOutputStream && this.transferToEnabled$1) {
               FileChannel inChannel = ((FileInputStream)this.in$1).getChannel();
               FileChannel outChannel = ((FileOutputStream)this.out$1).getChannel();
               long size = inChannel.size();
               Utils$.MODULE$.copyFileStreamNIO(inChannel, outChannel, 0L, size);
               var10000 = size;
            } else {
               long count = 0L;
               byte[] buf = new byte[8192];
               int n = 0;

               while(n != -1) {
                  n = this.in$1.read(buf);
                  if (n != -1) {
                     this.out$1.write(buf, 0, n);
                     count += (long)n;
                  }
               }

               var10000 = count;
            }

            return var10000;
         }

         public {
            this.in$1 = in$1;
            this.out$1 = out$1;
            this.transferToEnabled$1 = transferToEnabled$1;
         }
      }, new Serializable(in, out, closeStreams) {
         public static final long serialVersionUID = 0L;
         private final InputStream in$1;
         private final OutputStream out$1;
         private final boolean closeStreams$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            if (this.closeStreams$1) {
               try {
                  this.in$1.close();
               } finally {
                  this.out$1.close();
               }
            }

         }

         public {
            this.in$1 = in$1;
            this.out$1 = out$1;
            this.closeStreams$1 = closeStreams$1;
         }
      }));
   }

   public boolean copyStream$default$3() {
      return false;
   }

   public boolean copyStream$default$4() {
      return false;
   }

   public void copyFileStreamNIO(FileChannel input, FileChannel output, long startPosition, long bytesToCopy) {
      long initialPos = output.position();

      LongRef count;
      for(count = LongRef.create(0L); count.elem < bytesToCopy; count.elem += input.transferTo(count.elem + startPosition, bytesToCopy - count.elem, output)) {
      }

      Predef$.MODULE$.assert(count.elem == bytesToCopy, new Serializable(bytesToCopy, count) {
         public static final long serialVersionUID = 0L;
         private final long bytesToCopy$1;
         private final LongRef count$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"request to copy ", " bytes, but actually copied ", " bytes."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.bytesToCopy$1), BoxesRunTime.boxToLong(this.count$1.elem)}));
         }

         public {
            this.bytesToCopy$1 = bytesToCopy$1;
            this.count$1 = count$1;
         }
      });
      long finalPos = output.position();
      long expectedPos = initialPos + bytesToCopy;
      Predef$.MODULE$.assert(finalPos == expectedPos, new Serializable(finalPos, expectedPos) {
         public static final long serialVersionUID = 0L;
         private final long finalPos$1;
         private final long expectedPos$1;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n         |Current position ", " do not equal to expected position ", "\n         |after transferTo, please check your kernel version to see if it is 2.6.32,\n         |this is a kernel bug which will lead to unexpected behavior when using transferTo.\n         |You can set spark.file.transferTo = false to disable this NIO feature.\n           "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.finalPos$1), BoxesRunTime.boxToLong(this.expectedPos$1)}))))).stripMargin();
         }

         public {
            this.finalPos$1 = finalPos$1;
            this.expectedPos$1 = expectedPos$1;
         }
      });
   }

   public URI constructURIForAuthentication(URI uri, SecurityManager securityMgr) {
      String userCred = securityMgr.getSecretKey();
      if (userCred == null) {
         throw new Exception("Secret key is null with authentication on");
      } else {
         String userInfo = (new StringBuilder()).append((Object)securityMgr.getHttpUser()).append((Object)":").append((Object)userCred).toString();
         return new URI(uri.getScheme(), userInfo, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
      }
   }

   public String encodeFileNameToURIRawPath(String fileName) {
      Predef$.MODULE$.require(!fileName.contains("/") && !fileName.contains("\\"));
      return (new URI("file", (String)null, "localhost", -1, (new StringBuilder()).append((Object)"/").append((Object)fileName).toString(), (String)null, (String)null)).getRawPath().substring(1);
   }

   public String decodeFileNameInURI(URI uri) {
      String rawPath = uri.getRawPath();
      String rawFileName = (String)Predef$.MODULE$.refArrayOps((Object[])rawPath.split("/")).last();
      return (new URI((new StringBuilder()).append((Object)"file:///").append((Object)rawFileName).toString())).getPath().substring(1);
   }

   public File fetchFile(String url, File targetDir, SparkConf conf, SecurityManager securityMgr, Configuration hadoopConf, long timestamp, boolean useCache) {
      String fileName = this.decodeFileNameInURI(new URI(url));
      File targetFile = new File(targetDir, fileName);
      boolean fetchCacheEnabled = conf.getBoolean("spark.files.useFetchCache", true);
      if (useCache && fetchCacheEnabled) {
         String cachedFileName = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "", "_cache"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(url.hashCode()), BoxesRunTime.boxToLong(timestamp)}));
         String lockFileName = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "", "_lock"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(url.hashCode()), BoxesRunTime.boxToLong(timestamp)}));
         File localDir = new File(this.getLocalDir(conf));
         File lockFile = new File(localDir, lockFileName);
         FileChannel lockFileChannel = (new RandomAccessFile(lockFile, "rw")).getChannel();
         FileLock lock = lockFileChannel.lock();
         File cachedFile = new File(localDir, cachedFileName);

         try {
            if (cachedFile.exists()) {
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               this.doFetchFile(url, localDir, cachedFileName, conf, securityMgr, hadoopConf);
            }
         } finally {
            lock.release();
            lockFileChannel.close();
         }

         this.copyFile(url, cachedFile, targetFile, conf.getBoolean("spark.files.overwrite", false), this.copyFile$default$5());
         BoxedUnit var22 = BoxedUnit.UNIT;
      } else {
         this.doFetchFile(url, targetDir, fileName, conf, securityMgr, hadoopConf);
      }

      if (!fileName.endsWith(".tar.gz") && !fileName.endsWith(".tgz")) {
         if (fileName.endsWith(".tar")) {
            this.logInfo(new Serializable(fileName) {
               public static final long serialVersionUID = 0L;
               private final String fileName$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Untarring ").append((Object)this.fileName$1).toString();
               }

               public {
                  this.fileName$1 = fileName$1;
               }
            });
            this.executeAndGetOutput((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"tar", "-xf", fileName}))), targetDir, this.executeAndGetOutput$default$3(), this.executeAndGetOutput$default$4());
         } else {
            BoxedUnit var23 = BoxedUnit.UNIT;
         }
      } else {
         this.logInfo(new Serializable(fileName) {
            public static final long serialVersionUID = 0L;
            private final String fileName$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Untarring ").append((Object)this.fileName$1).toString();
            }

            public {
               this.fileName$1 = fileName$1;
            }
         });
         this.executeAndGetOutput((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"tar", "-xzf", fileName}))), targetDir, this.executeAndGetOutput$default$3(), this.executeAndGetOutput$default$4());
      }

      FileUtil.chmod(targetFile.getAbsolutePath(), "a+x");
      if (this.isWindows()) {
         BoxesRunTime.boxToInteger(FileUtil.chmod(targetFile.getAbsolutePath(), "u+r"));
      } else {
         BoxedUnit var24 = BoxedUnit.UNIT;
      }

      return targetFile;
   }

   private void downloadFile(String url, InputStream in, File destFile, boolean fileOverwrite) {
      File tempFile = File.createTempFile("fetchFileTemp", (String)null, new File(destFile.getParentFile().getAbsolutePath()));
      this.logInfo(new Serializable(url, tempFile) {
         public static final long serialVersionUID = 0L;
         private final String url$2;
         private final File tempFile$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Fetching ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.url$2, this.tempFile$1}));
         }

         public {
            this.url$2 = url$2;
            this.tempFile$1 = tempFile$1;
         }
      });

      try {
         FileOutputStream out = new FileOutputStream(tempFile);
         this.copyStream(in, out, true, this.copyStream$default$4());
         this.copyFile(url, tempFile, destFile, fileOverwrite, true);
      } finally {
         if (tempFile.exists()) {
            tempFile.delete();
         }

      }

   }

   private void copyFile(String url, File sourceFile, File destFile, boolean fileOverwrite, boolean removeSourceFile) {
      if (destFile.exists()) {
         if (this.org$apache$spark$util$Utils$$filesEqualRecursive(sourceFile, destFile)) {
            this.logInfo(new Serializable(sourceFile, destFile) {
               public static final long serialVersionUID = 0L;
               private final File sourceFile$1;
               private final File destFile$1;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("%s has been previously copied to %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.sourceFile$1.getAbsolutePath(), this.destFile$1.getAbsolutePath()}));
               }

               public {
                  this.sourceFile$1 = sourceFile$1;
                  this.destFile$1 = destFile$1;
               }
            });
            return;
         }

         if (!fileOverwrite) {
            throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"File ", " exists and does not match contents of ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{destFile, url})));
         }

         this.logInfo(new Serializable(url, destFile) {
            public static final long serialVersionUID = 0L;
            private final String url$1;
            private final File destFile$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"File ", " exists and does not match contents of ", ", replacing it with ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.destFile$1, this.url$1, this.url$1}));
            }

            public {
               this.url$1 = url$1;
               this.destFile$1 = destFile$1;
            }
         });
         if (!destFile.delete()) {
            throw new SparkException((new StringOps(Predef$.MODULE$.augmentString("Failed to delete %s while attempting to overwrite it with %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{destFile.getAbsolutePath(), sourceFile.getAbsolutePath()})));
         }
      }

      if (removeSourceFile) {
         Files.move(sourceFile.toPath(), destFile.toPath());
      } else {
         this.logInfo(new Serializable(sourceFile, destFile) {
            public static final long serialVersionUID = 0L;
            private final File sourceFile$1;
            private final File destFile$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Copying ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.sourceFile$1.getAbsolutePath(), this.destFile$1.getAbsolutePath()}));
            }

            public {
               this.sourceFile$1 = sourceFile$1;
               this.destFile$1 = destFile$1;
            }
         });
         this.org$apache$spark$util$Utils$$copyRecursive(sourceFile, destFile);
      }

   }

   private boolean copyFile$default$5() {
      return false;
   }

   public boolean org$apache$spark$util$Utils$$filesEqualRecursive(File file1, File file2) {
      boolean var10000;
      if (file1.isDirectory() && file2.isDirectory()) {
         File[] subfiles1 = file1.listFiles();
         File[] subfiles2 = file2.listFiles();
         if (Predef$.MODULE$.refArrayOps((Object[])subfiles1).size() != Predef$.MODULE$.refArrayOps((Object[])subfiles2).size()) {
            return false;
         }

         var10000 = Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])subfiles1).sortBy(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply(File x$1) {
               return x$1.getName();
            }
         }, Ordering.String$.MODULE$)).zip(Predef$.MODULE$.wrapRefArray(Predef$.MODULE$.refArrayOps((Object[])subfiles2).sortBy(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply(File x$2) {
               return x$2.getName();
            }
         }, Ordering.String$.MODULE$)), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).forall(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Tuple2<File, File> x0$1) {
               if (x0$1 != null) {
                  File f1 = (File)x0$1._1();
                  File f2 = (File)x0$1._2();
                  boolean var5 = Utils$.MODULE$.org$apache$spark$util$Utils$$filesEqualRecursive(f1, f2);
                  return var5;
               } else {
                  throw new MatchError(x0$1);
               }
            }
         });
      } else {
         var10000 = file1.isFile() && file2.isFile() ? org.spark_project.guava.io.Files.equal(file1, file2) : false;
      }

      return var10000;
   }

   public void org$apache$spark$util$Utils$$copyRecursive(File source, File dest) {
      if (source.isDirectory()) {
         if (!dest.mkdir()) {
            throw new IOException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to create directory ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{dest.getPath()})));
         }

         File[] subfiles = source.listFiles();
         Predef$.MODULE$.refArrayOps((Object[])subfiles).foreach(new Serializable(dest) {
            public static final long serialVersionUID = 0L;
            private final File dest$1;

            public final void apply(File f) {
               Utils$.MODULE$.org$apache$spark$util$Utils$$copyRecursive(f, new File(this.dest$1, f.getName()));
            }

            public {
               this.dest$1 = dest$1;
            }
         });
      } else {
         Files.copy(source.toPath(), dest.toPath());
      }

   }

   public File doFetchFile(String url, File targetDir, String filename, SparkConf conf, SecurityManager securityMgr, Configuration hadoopConf) {
      File targetFile = new File(targetDir, filename);
      URI uri = new URI(url);
      boolean fileOverwrite = conf.getBoolean("spark.files.overwrite", false);
      String var10 = (String)Option$.MODULE$.apply(uri.getScheme()).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "file";
         }
      });
      if ("spark".equals(var10)) {
         if (SparkEnv$.MODULE$.get() == null) {
            throw new IllegalStateException("Cannot retrieve files with 'spark' scheme without an active SparkEnv.");
         }

         ReadableByteChannel source = SparkEnv$.MODULE$.get().rpcEnv().openChannel(url);
         InputStream is = Channels.newInputStream(source);
         this.downloadFile(url, is, targetFile, fileOverwrite);
         BoxedUnit var11 = BoxedUnit.UNIT;
      } else {
         boolean var14;
         if ("http".equals(var10)) {
            var14 = true;
         } else if ("https".equals(var10)) {
            var14 = true;
         } else if ("ftp".equals(var10)) {
            var14 = true;
         } else {
            var14 = false;
         }

         if (var14) {
            URLConnection uc = null;
            if (securityMgr.isAuthenticationEnabled()) {
               this.logDebug(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "fetchFile with security enabled";
                  }
               });
               URI newuri = this.constructURIForAuthentication(uri, securityMgr);
               uc = newuri.toURL().openConnection();
               uc.setAllowUserInteraction(false);
            } else {
               this.logDebug(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "fetchFile not using security";
                  }
               });
               uc = (new URL(url)).openConnection();
            }

            this.setupSecureURLConnection(uc, securityMgr);
            int timeoutMs = (int)conf.getTimeAsSeconds("spark.files.fetchTimeout", "60s") * 1000;
            uc.setConnectTimeout(timeoutMs);
            uc.setReadTimeout(timeoutMs);
            uc.connect();
            InputStream in = uc.getInputStream();
            this.downloadFile(url, in, targetFile, fileOverwrite);
            BoxedUnit var22 = BoxedUnit.UNIT;
         } else if ("file".equals(var10)) {
            File sourceFile = uri.isAbsolute() ? new File(uri) : new File(url);
            this.copyFile(url, sourceFile, targetFile, fileOverwrite, this.copyFile$default$5());
            BoxedUnit var23 = BoxedUnit.UNIT;
         } else {
            FileSystem fs = this.getHadoopFileSystem(uri, hadoopConf);
            Path path = new Path(uri);
            this.fetchHcfsFile(path, targetDir, fs, conf, hadoopConf, fileOverwrite, new Some(filename));
            BoxedUnit var24 = BoxedUnit.UNIT;
         }
      }

      return targetFile;
   }

   public void fetchHcfsFile(Path path, File targetDir, FileSystem fs, SparkConf conf, Configuration hadoopConf, boolean fileOverwrite, Option<String> filename) {
      if (!targetDir.exists() && !targetDir.mkdir()) {
         throw new IOException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to create directory ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{targetDir.getPath()})));
      } else {
         File dest = new File(targetDir, (String)filename.getOrElse(new Serializable(path) {
            public static final long serialVersionUID = 0L;
            private final Path path$1;

            public final String apply() {
               return this.path$1.getName();
            }

            public {
               this.path$1 = path$1;
            }
         }));
         if (fs.isFile(path)) {
            FSDataInputStream in = fs.open(path);

            try {
               this.downloadFile(path.toString(), in, dest, fileOverwrite);
            } finally {
               in.close();
            }
         } else {
            Predef$.MODULE$.refArrayOps((Object[])fs.listStatus(path)).foreach(new Serializable(fs, conf, hadoopConf, fileOverwrite, dest) {
               public static final long serialVersionUID = 0L;
               private final FileSystem fs$1;
               private final SparkConf conf$4;
               private final Configuration hadoopConf$1;
               private final boolean fileOverwrite$1;
               private final File dest$2;

               public final void apply(FileStatus fileStatus) {
                  Utils$.MODULE$.fetchHcfsFile(fileStatus.getPath(), this.dest$2, this.fs$1, this.conf$4, this.hadoopConf$1, this.fileOverwrite$1, Utils$.MODULE$.fetchHcfsFile$default$7());
               }

               public {
                  this.fs$1 = fs$1;
                  this.conf$4 = conf$4;
                  this.hadoopConf$1 = hadoopConf$1;
                  this.fileOverwrite$1 = fileOverwrite$1;
                  this.dest$2 = dest$2;
               }
            });
         }

      }
   }

   public Option<String> fetchHcfsFile$default$7() {
      return None$.MODULE$;
   }

   public void validateURL(URI uri) throws MalformedURLException {
      String var2 = (String)Option$.MODULE$.apply(uri.getScheme()).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "file";
         }
      });
      boolean var3;
      if ("http".equals(var2)) {
         var3 = true;
      } else if ("https".equals(var2)) {
         var3 = true;
      } else if ("ftp".equals(var2)) {
         var3 = true;
      } else {
         var3 = false;
      }

      if (var3) {
         try {
            uri.toURL();
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } catch (MalformedURLException var7) {
            MalformedURLException ex = new MalformedURLException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"URI (", ") is not a valid URL."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{uri.toString()})));
            ex.initCause(var7);
            throw ex;
         }
      } else {
         BoxedUnit var4 = BoxedUnit.UNIT;
      }

   }

   public String getLocalDir(SparkConf conf) {
      return (String)Predef$.MODULE$.refArrayOps((Object[])this.getOrCreateLocalRootDirs(conf)).headOption().getOrElse(new Serializable(conf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf conf$2;

         public final Nothing$ apply() {
            String[] configuredLocalDirs = Utils$.MODULE$.getConfiguredLocalDirs(this.conf$2);
            throw new IOException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to get a temp directory under [", "]."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Predef$.MODULE$.refArrayOps((Object[])configuredLocalDirs).mkString(",")})));
         }

         public {
            this.conf$2 = conf$2;
         }
      });
   }

   public boolean isRunningInYarnContainer(SparkConf conf) {
      return conf.getenv("CONTAINER_ID") != null;
   }

   public String[] getOrCreateLocalRootDirs(SparkConf conf) {
      if (this.localRootDirs() == null) {
         synchronized(this) {
            if (this.localRootDirs() == null) {
               this.localRootDirs_$eq(this.getOrCreateLocalRootDirsImpl(conf));
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               BoxedUnit var5 = BoxedUnit.UNIT;
            }
         }
      } else {
         BoxedUnit var6 = BoxedUnit.UNIT;
      }

      return this.localRootDirs();
   }

   public String[] getConfiguredLocalDirs(SparkConf conf) {
      boolean shuffleServiceEnabled = conf.getBoolean("spark.shuffle.service.enabled", false);
      String[] var10000;
      if (this.isRunningInYarnContainer(conf)) {
         var10000 = this.getYarnLocalDirs(conf).split(",");
      } else if (conf.getenv("SPARK_EXECUTOR_DIRS") == null) {
         if (conf.getenv("SPARK_LOCAL_DIRS") == null) {
            if (conf.getenv("MESOS_DIRECTORY") != null && !shuffleServiceEnabled) {
               var10000 = (String[])((Object[])(new String[]{conf.getenv("MESOS_DIRECTORY")}));
            } else {
               if (conf.getenv("MESOS_DIRECTORY") != null && shuffleServiceEnabled) {
                  this.logInfo(new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "MESOS_DIRECTORY available but not using provided Mesos sandbox because spark.shuffle.service.enabled is enabled.";
                     }
                  });
               }

               var10000 = conf.get("spark.local.dir", System.getProperty("java.io.tmpdir")).split(",");
            }
         } else {
            var10000 = conf.getenv("SPARK_LOCAL_DIRS").split(",");
         }
      } else {
         var10000 = conf.getenv("SPARK_EXECUTOR_DIRS").split(File.pathSeparator);
      }

      return var10000;
   }

   private String[] getOrCreateLocalRootDirsImpl(SparkConf conf) {
      String[] configuredLocalDirs = this.getConfiguredLocalDirs(conf);
      String[] uris = (String[])Predef$.MODULE$.refArrayOps((Object[])configuredLocalDirs).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String root) {
            return BoxesRunTime.unboxToBoolean(Try$.MODULE$.apply(new Serializable(this, root) {
               public static final long serialVersionUID = 0L;
               private final String root$1;

               public final boolean apply() {
                  return this.apply$mcZ$sp();
               }

               public boolean apply$mcZ$sp() {
                  return (new URI(this.root$1)).getScheme() != null;
               }

               public {
                  this.root$1 = root$1;
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply() {
                  return this.apply$mcZ$sp();
               }

               public boolean apply$mcZ$sp() {
                  return false;
               }
            }));
         }
      });
      if (Predef$.MODULE$.refArrayOps((Object[])uris).nonEmpty()) {
         this.logWarning(new Serializable(uris) {
            public static final long serialVersionUID = 0L;
            private final String[] uris$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"The configured local directories are not expected to be URIs; however, got suspicious ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"values [", "]. Please check your configured local directories."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Predef$.MODULE$.refArrayOps((Object[])this.uris$1).mkString(", ")}))).toString();
            }

            public {
               this.uris$1 = uris$1;
            }
         });
      }

      return (String[])Predef$.MODULE$.refArrayOps((Object[])configuredLocalDirs).flatMap(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Iterable<String> apply(String root) {
            Iterable var10000;
            try {
               File rootDir = new File(root);
               if (!rootDir.exists() && !rootDir.mkdirs()) {
                  Utils$.MODULE$.logError(new Serializable(this, root) {
                     public static final long serialVersionUID = 0L;
                     private final String root$2;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to create dir in ", ". Ignoring this directory."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.root$2}));
                     }

                     public {
                        this.root$2 = root$2;
                     }
                  });
                  var10000 = Option$.MODULE$.option2Iterable(None$.MODULE$);
               } else {
                  File dir = Utils$.MODULE$.createTempDir(root, Utils$.MODULE$.createTempDir$default$2());
                  Utils$.MODULE$.chmod700(dir);
                  var10000 = Option$.MODULE$.option2Iterable(new Some(dir.getAbsolutePath()));
               }
            } catch (IOException var5) {
               Utils$.MODULE$.logError(new Serializable(this, root) {
                  public static final long serialVersionUID = 0L;
                  private final String root$2;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to create local root dir in ", ". Ignoring this directory."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.root$2}));
                  }

                  public {
                     this.root$2 = root$2;
                  }
               });
               var10000 = Option$.MODULE$.option2Iterable(None$.MODULE$);
            }

            return var10000;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)));
   }

   private String getYarnLocalDirs(SparkConf conf) {
      String localDirs = (String)Option$.MODULE$.apply(conf.getenv("LOCAL_DIRS")).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      if (localDirs.isEmpty()) {
         throw new Exception("Yarn Local dirs can't be empty");
      } else {
         return localDirs;
      }
   }

   public void clearLocalRootDirs() {
      this.localRootDirs_$eq((String[])null);
   }

   public <T> Seq<T> randomize(TraversableOnce<T> seq, ClassTag<T> evidence$1) {
      return Predef$.MODULE$.genericWrapArray(this.randomizeInPlace(seq.toArray(evidence$1), this.randomizeInPlace$default$2()));
   }

   public <T> Object randomizeInPlace(Object arr, Random rand) {
      RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(ScalaRunTime$.MODULE$.array_length(arr) - 1), 1).by(-1).foreach$mVc$sp(new Serializable(arr, rand) {
         public static final long serialVersionUID = 0L;
         private final Object arr$1;
         private final Random rand$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            int j = this.rand$1.nextInt(i + 1);
            Object tmp = ScalaRunTime$.MODULE$.array_apply(this.arr$1, j);
            ScalaRunTime$.MODULE$.array_update(this.arr$1, j, ScalaRunTime$.MODULE$.array_apply(this.arr$1, i));
            ScalaRunTime$.MODULE$.array_update(this.arr$1, i, tmp);
         }

         public {
            this.arr$1 = arr$1;
            this.rand$1 = rand$1;
         }
      });
      return arr;
   }

   public <T> Random randomizeInPlace$default$2() {
      return new Random();
   }

   public InetAddress org$apache$spark$util$Utils$$localIpAddress() {
      return this.bitmap$0 ? this.org$apache$spark$util$Utils$$localIpAddress : this.org$apache$spark$util$Utils$$localIpAddress$lzycompute();
   }

   private InetAddress findLocalInetAddress() {
      Object var1 = new Object();

      InetAddress var10000;
      try {
         String defaultIpOverride = System.getenv("SPARK_LOCAL_IP");
         if (defaultIpOverride == null) {
            InetAddress address = InetAddress.getLocalHost();
            if (address.isLoopbackAddress()) {
               Seq activeNetworkIFs = ((TraversableOnce)JavaConverters$.MODULE$.enumerationAsScalaIteratorConverter(NetworkInterface.getNetworkInterfaces()).asScala()).toSeq();
               Seq reOrderedNetworkIFs = this.isWindows() ? activeNetworkIFs : (Seq)activeNetworkIFs.reverse();
               reOrderedNetworkIFs.foreach(new Serializable(address, var1) {
                  public static final long serialVersionUID = 0L;
                  public final InetAddress address$1;
                  private final Object nonLocalReturnKey1$1;

                  public final void apply(NetworkInterface ni) {
                     Seq addresses = ((Iterator)JavaConverters$.MODULE$.enumerationAsScalaIteratorConverter(ni.getInetAddresses()).asScala()).filterNot(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final boolean apply(InetAddress addr) {
                           return addr.isLinkLocalAddress() || addr.isLoopbackAddress();
                        }
                     }).toSeq();
                     if (addresses.nonEmpty()) {
                        InetAddress addr = (InetAddress)addresses.find(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final boolean apply(InetAddress x$3) {
                              return x$3 instanceof Inet4Address;
                           }
                        }).getOrElse(new Serializable(this, addresses) {
                           public static final long serialVersionUID = 0L;
                           private final Seq addresses$1;

                           public final InetAddress apply() {
                              return (InetAddress)this.addresses$1.head();
                           }

                           public {
                              this.addresses$1 = addresses$1;
                           }
                        });
                        InetAddress strippedAddress = InetAddress.getByAddress(addr.getAddress());
                        Utils$.MODULE$.logWarning(new Serializable(this, strippedAddress, ni) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final InetAddress strippedAddress$1;
                           private final NetworkInterface ni$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"Your hostname, ").append((Object)InetAddress.getLocalHost().getHostName()).append((Object)" resolves to").append((Object)" a loopback address: ").append((Object)this.$outer.address$1.getHostAddress()).append((Object)"; using ").append((Object)this.strippedAddress$1.getHostAddress()).append((Object)" instead (on interface ").append((Object)this.ni$1.getName()).append((Object)")").toString();
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.strippedAddress$1 = strippedAddress$1;
                                 this.ni$1 = ni$1;
                              }
                           }
                        });
                        Utils$.MODULE$.logWarning(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final String apply() {
                              return "Set SPARK_LOCAL_IP if you need to bind to another address";
                           }
                        });
                        throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, strippedAddress);
                     }
                  }

                  public {
                     this.address$1 = address$1;
                     this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
                  }
               });
               this.logWarning(new Serializable(address) {
                  public static final long serialVersionUID = 0L;
                  private final InetAddress address$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Your hostname, ").append((Object)InetAddress.getLocalHost().getHostName()).append((Object)" resolves to").append((Object)" a loopback address: ").append((Object)this.address$1.getHostAddress()).append((Object)", but we couldn't find any").append((Object)" external IP address!").toString();
                  }

                  public {
                     this.address$1 = address$1;
                  }
               });
               this.logWarning(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Set SPARK_LOCAL_IP if you need to bind to another address";
                  }
               });
            }

            var10000 = address;
         } else {
            var10000 = InetAddress.getByName(defaultIpOverride);
         }
      } catch (NonLocalReturnControl var7) {
         if (var7.key() != var1) {
            throw var7;
         }

         var10000 = (InetAddress)var7.value();
      }

      return var10000;
   }

   private Option<String> customHostname() {
      return this.customHostname;
   }

   private void customHostname_$eq(Option<String> x$1) {
      this.customHostname = x$1;
   }

   public void setCustomHostname(String hostname) {
      this.checkHost(hostname);
      this.customHostname_$eq(new Some(hostname));
   }

   public String localCanonicalHostName() {
      return (String)this.customHostname().getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return Utils$.MODULE$.org$apache$spark$util$Utils$$localIpAddress().getCanonicalHostName();
         }
      });
   }

   public String localHostName() {
      return (String)this.customHostname().getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return Utils$.MODULE$.org$apache$spark$util$Utils$$localIpAddress().getHostAddress();
         }
      });
   }

   public String localHostNameForURI() {
      return (String)this.customHostname().getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return InetAddresses.toUriString(Utils$.MODULE$.org$apache$spark$util$Utils$$localIpAddress());
         }
      });
   }

   public void checkHost(String host) {
      Predef$.MODULE$.assert(host != null && host.indexOf(58) == -1, new Serializable(host) {
         public static final long serialVersionUID = 0L;
         private final String host$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Expected hostname (not IP) but got ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.host$1}));
         }

         public {
            this.host$1 = host$1;
         }
      });
   }

   public void checkHostPort(String hostPort) {
      Predef$.MODULE$.assert(hostPort != null && hostPort.indexOf(58) != -1, new Serializable(hostPort) {
         public static final long serialVersionUID = 0L;
         private final String hostPort$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Expected host and port but got ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.hostPort$1}));
         }

         public {
            this.hostPort$1 = hostPort$1;
         }
      });
   }

   private ConcurrentHashMap<String, Tuple2<String, Object>> hostPortParseResults() {
      return this.hostPortParseResults;
   }

   public Tuple2<String, Object> parseHostPort(String hostPort) {
      Tuple2 cached = (Tuple2)this.hostPortParseResults().get(hostPort);
      if (cached == null) {
         int indx = hostPort.lastIndexOf(58);
         if (-1 == indx) {
            Tuple2 retval = new Tuple2(hostPort, BoxesRunTime.boxToInteger(0));
            this.hostPortParseResults().put(hostPort, retval);
            return retval;
         } else {
            Tuple2 retval = new Tuple2(hostPort.substring(0, indx).trim(), BoxesRunTime.boxToInteger((new StringOps(Predef$.MODULE$.augmentString(hostPort.substring(indx + 1).trim()))).toInt()));
            this.hostPortParseResults().putIfAbsent(hostPort, retval);
            return (Tuple2)this.hostPortParseResults().get(hostPort);
         }
      } else {
         return cached;
      }
   }

   public String getUsedTimeMs(long startTimeMs) {
      return (new StringBuilder()).append((Object)" ").append((Object)BoxesRunTime.boxToLong(System.currentTimeMillis() - startTimeMs)).append((Object)" ms").toString();
   }

   private Seq<File> listFilesSafely(File file) {
      Object var10000;
      if (file.exists()) {
         File[] files = file.listFiles();
         if (files == null) {
            throw new IOException((new StringBuilder()).append((Object)"Failed to list files for dir: ").append((Object)file).toString());
         }

         var10000 = Predef$.MODULE$.wrapRefArray((Object[])files);
      } else {
         var10000 = Nil$.MODULE$;
      }

      return (Seq<File>)var10000;
   }

   public File[] recursiveList(File f) {
      Predef$.MODULE$.require(f.isDirectory());
      File[] current = f.listFiles();
      return (File[])Predef$.MODULE$.refArrayOps((Object[])current).$plus$plus(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])current).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(File x$4) {
            return x$4.isDirectory();
         }
      })).flatMap(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final ArrayOps<File> apply(File f) {
            return Predef$.MODULE$.<File>refArrayOps((Object[])Utils$.MODULE$.recursiveList(f));
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(File.class)))), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(File.class)));
   }

   public void deleteRecursively(File file) {
      if (file != null) {
         try {
            if (file.isDirectory() && !this.isSymlink(file)) {
               ObjectRef savedIOException = ObjectRef.create((Object)null);
               this.listFilesSafely(file).foreach(new Serializable(savedIOException) {
                  public static final long serialVersionUID = 0L;
                  private final ObjectRef savedIOException$1;

                  public final void apply(File child) {
                     try {
                        Utils$.MODULE$.deleteRecursively(child);
                     } catch (IOException var3) {
                        this.savedIOException$1.elem = (T)var3;
                     }

                  }

                  public {
                     this.savedIOException$1 = savedIOException$1;
                  }
               });
               if ((IOException)savedIOException.elem != null) {
                  throw (IOException)savedIOException.elem;
               }

               ShutdownHookManager$.MODULE$.removeShutdownDeleteDir(file);
            }
         } finally {
            if (file.delete()) {
               this.logTrace(new Serializable(file) {
                  public static final long serialVersionUID = 0L;
                  private final File file$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " has been deleted"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1.getAbsolutePath()}));
                  }

                  public {
                     this.file$1 = file$1;
                  }
               });
            } else if (file.exists()) {
               throw new IOException((new StringBuilder()).append((Object)"Failed to delete: ").append((Object)file.getAbsolutePath()).toString());
            }

         }
      }

   }

   public boolean isSymlink(File file) {
      return Files.isSymbolicLink(Paths.get(file.toURI()));
   }

   public boolean doesDirectoryContainAnyNewFiles(File dir, long cutoff) {
      if (!dir.isDirectory()) {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " is not a directory!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{dir})));
      } else {
         File[] filesAndDirs = dir.listFiles();
         long cutoffTimeInMillis = System.currentTimeMillis() - cutoff * 1000L;
         return Predef$.MODULE$.refArrayOps((Object[])filesAndDirs).exists(new Serializable(cutoffTimeInMillis) {
            public static final long serialVersionUID = 0L;
            private final long cutoffTimeInMillis$1;

            public final boolean apply(File x$5) {
               return x$5.lastModified() > this.cutoffTimeInMillis$1;
            }

            public {
               this.cutoffTimeInMillis$1 = cutoffTimeInMillis$1;
            }
         }) || Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])filesAndDirs).filter(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final boolean apply(File x$6) {
               return x$6.isDirectory();
            }
         })).exists(new Serializable(cutoff) {
            public static final long serialVersionUID = 0L;
            private final long cutoff$1;

            public final boolean apply(File subdir) {
               return Utils$.MODULE$.doesDirectoryContainAnyNewFiles(subdir, this.cutoff$1);
            }

            public {
               this.cutoff$1 = cutoff$1;
            }
         });
      }
   }

   public long timeStringAsMs(String str) {
      return JavaUtils.timeStringAsMs(str);
   }

   public long timeStringAsSeconds(String str) {
      return JavaUtils.timeStringAsSec(str);
   }

   public long byteStringAsBytes(String str) {
      return JavaUtils.byteStringAsBytes(str);
   }

   public long byteStringAsKb(String str) {
      return JavaUtils.byteStringAsKb(str);
   }

   public long byteStringAsMb(String str) {
      return JavaUtils.byteStringAsMb(str);
   }

   public long byteStringAsGb(String str) {
      return JavaUtils.byteStringAsGb(str);
   }

   public int memoryStringToMb(String str) {
      return (int)(JavaUtils.byteStringAsBytes(str) / 1024L / 1024L);
   }

   public String bytesToString(long size) {
      return this.bytesToString(scala.package$.MODULE$.BigInt().apply(size));
   }

   public String bytesToString(BigInt size) {
      long EB = 1152921504606846976L;
      long PB = 1125899906842624L;
      long TB = 1099511627776L;
      long GB = 1073741824L;
      long MB = 1048576L;
      long KB = 1024L;
      String var10000;
      if (size.$greater$eq(scala.package$.MODULE$.BigInt().apply(2048L).$times(BigInt$.MODULE$.long2bigInt(EB)))) {
         var10000 = (new StringBuilder()).append((Object)scala.package$.MODULE$.BigDecimal().apply(size, new MathContext(3, RoundingMode.HALF_UP)).toString()).append((Object)" B").toString();
      } else {
         Tuple2 var15 = size.$greater$eq(BigInt$.MODULE$.long2bigInt(2L * EB)) ? new Tuple2(scala.package$.MODULE$.BigDecimal().apply(size).$div(BigDecimal$.MODULE$.long2bigDecimal(EB)), "EB") : (size.$greater$eq(BigInt$.MODULE$.long2bigInt(2L * PB)) ? new Tuple2(scala.package$.MODULE$.BigDecimal().apply(size).$div(BigDecimal$.MODULE$.long2bigDecimal(PB)), "PB") : (size.$greater$eq(BigInt$.MODULE$.long2bigInt(2L * TB)) ? new Tuple2(scala.package$.MODULE$.BigDecimal().apply(size).$div(BigDecimal$.MODULE$.long2bigDecimal(TB)), "TB") : (size.$greater$eq(BigInt$.MODULE$.long2bigInt(2L * GB)) ? new Tuple2(scala.package$.MODULE$.BigDecimal().apply(size).$div(BigDecimal$.MODULE$.long2bigDecimal(GB)), "GB") : (size.$greater$eq(BigInt$.MODULE$.long2bigInt(2L * MB)) ? new Tuple2(scala.package$.MODULE$.BigDecimal().apply(size).$div(BigDecimal$.MODULE$.long2bigDecimal(MB)), "MB") : (size.$greater$eq(BigInt$.MODULE$.long2bigInt(2L * KB)) ? new Tuple2(scala.package$.MODULE$.BigDecimal().apply(size).$div(BigDecimal$.MODULE$.long2bigDecimal(KB)), "KB") : new Tuple2(scala.package$.MODULE$.BigDecimal().apply(size), "B"))))));
         if (var15 == null) {
            throw new MatchError(var15);
         }

         BigDecimal value = (BigDecimal)var15._1();
         String unit = (String)var15._2();
         Tuple2 var18 = new Tuple2(value, unit);
         BigDecimal value = (BigDecimal)var18._1();
         String unit = (String)var18._2();
         var10000 = (new StringOps(Predef$.MODULE$.augmentString("%.1f %s"))).formatLocal(Locale.US, Predef$.MODULE$.genericWrapArray(new Object[]{value, unit}));
      }

      return var10000;
   }

   public String msDurationToString(long ms) {
      int second = 1000;
      int minute = 60 * second;
      int hour = 60 * minute;
      Locale locale = Locale.US;
      String var9;
      if (ms < (long)second) {
         var9 = (new StringOps(Predef$.MODULE$.augmentString("%d ms"))).formatLocal(locale, Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(ms)}));
      } else if (ms < (long)minute) {
         var9 = (new StringOps(Predef$.MODULE$.augmentString("%.1f s"))).formatLocal(locale, Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToFloat((float)ms / (float)second)}));
      } else if (ms < (long)hour) {
         var9 = (new StringOps(Predef$.MODULE$.augmentString("%.1f m"))).formatLocal(locale, Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToFloat((float)ms / (float)minute)}));
      } else {
         var9 = (new StringOps(Predef$.MODULE$.augmentString("%.2f h"))).formatLocal(locale, Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToFloat((float)ms / (float)hour)}));
      }

      return var9;
   }

   public String megabytesToString(long megabytes) {
      return this.bytesToString(megabytes * 1024L * 1024L);
   }

   public Process executeCommand(Seq<String> command, File workingDir, Map<String, String> extraEnvironment, boolean redirectStderr) {
      ProcessBuilder builder = (new ProcessBuilder((String[])command.toArray(ClassTag$.MODULE$.apply(String.class)))).directory(workingDir);
      java.util.Map environment = builder.environment();
      extraEnvironment.withFilter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(environment) {
         public static final long serialVersionUID = 0L;
         private final java.util.Map environment$1;

         public final String apply(Tuple2<String, String> x$8) {
            if (x$8 != null) {
               String key = (String)x$8._1();
               String value = (String)x$8._2();
               String var5 = (String)this.environment$1.put(key, value);
               return var5;
            } else {
               throw new MatchError(x$8);
            }
         }

         public {
            this.environment$1 = environment$1;
         }
      });
      Process process = builder.start();
      if (redirectStderr) {
         String threadName = (new StringBuilder()).append((Object)"redirect stderr for command ").append(command.apply(0)).toString();
         this.processStreamByLine(threadName, process.getErrorStream(), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final void apply(String s) {
               Utils$.MODULE$.org$apache$spark$util$Utils$$log$1(s);
            }
         });
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return process;
   }

   public File executeCommand$default$2() {
      return new File(".");
   }

   public Map<String, String> executeCommand$default$3() {
      return Map$.MODULE$.empty();
   }

   public boolean executeCommand$default$4() {
      return true;
   }

   public String executeAndGetOutput(Seq<String> command, File workingDir, Map<String, String> extraEnvironment, boolean redirectStderr) {
      Process process = this.executeCommand(command, workingDir, extraEnvironment, redirectStderr);
      StringBuilder output = new StringBuilder();
      String threadName = (new StringBuilder()).append((Object)"read stdout for ").append(command.apply(0)).toString();
      Thread stdoutThread = this.processStreamByLine(threadName, process.getInputStream(), new Serializable(output) {
         public static final long serialVersionUID = 0L;
         private final StringBuilder output$1;

         public final void apply(String s) {
            Utils$.MODULE$.org$apache$spark$util$Utils$$appendToOutput$1(s, this.output$1);
         }

         public {
            this.output$1 = output$1;
         }
      });
      int exitCode = process.waitFor();
      stdoutThread.join();
      if (exitCode != 0) {
         this.logError(new Serializable(command, output, exitCode) {
            public static final long serialVersionUID = 0L;
            private final Seq command$1;
            private final StringBuilder output$1;
            private final int exitCode$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Process ", " exited with code ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.command$1, BoxesRunTime.boxToInteger(this.exitCode$1), this.output$1}));
            }

            public {
               this.command$1 = command$1;
               this.output$1 = output$1;
               this.exitCode$1 = exitCode$1;
            }
         });
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Process ", " exited with code ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{command, BoxesRunTime.boxToInteger(exitCode)})));
      } else {
         return output.toString();
      }
   }

   public File executeAndGetOutput$default$2() {
      return new File(".");
   }

   public Map<String, String> executeAndGetOutput$default$3() {
      return Map$.MODULE$.empty();
   }

   public boolean executeAndGetOutput$default$4() {
      return true;
   }

   public Thread processStreamByLine(String threadName, InputStream inputStream, Function1<String, BoxedUnit> processLine) {
      Thread t = new Thread(threadName, inputStream, processLine) {
         private final InputStream inputStream$1;
         public final Function1 processLine$1;

         public void run() {
            Source$.MODULE$.fromInputStream(this.inputStream$1, Codec$.MODULE$.fallbackSystemCodec()).getLines().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(String line) {
                  this.$outer.processLine$1.apply(line);
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
            this.inputStream$1 = inputStream$1;
            this.processLine$1 = processLine$1;
         }
      };
      t.setDaemon(true);
      t.start();
      return t;
   }

   public void tryOrExit(Function0<BoxedUnit> block) {
      try {
         block.apply$mcV$sp();
      } catch (Throwable var8) {
         if (var8 instanceof ControlThrowable) {
            ControlThrowable var4 = (ControlThrowable)var8;
            throw (Throwable)var4;
         }

         if (var8 == null) {
            throw var8;
         }

         this.sparkUncaughtExceptionHandler().uncaughtException(var8);
         BoxedUnit var5 = BoxedUnit.UNIT;
         return;
      }

   }

   public void tryOrStopSparkContext(SparkContext sc, Function0<BoxedUnit> block) {
      try {
         block.apply$mcV$sp();
      } catch (Throwable var10) {
         if (var10 instanceof ControlThrowable) {
            ControlThrowable var5 = (ControlThrowable)var10;
            throw (Throwable)var5;
         }

         if (var10 == null) {
            throw var10;
         }

         String currentThreadName = Thread.currentThread().getName();
         if (sc != null) {
            this.logError(new Serializable(currentThreadName) {
               public static final long serialVersionUID = 0L;
               private final String currentThreadName$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"uncaught error in thread ", ", stopping SparkContext"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.currentThreadName$1}));
               }

               public {
                  this.currentThreadName$1 = currentThreadName$1;
               }
            }, var10);
            sc.stopInNewThread();
         }

         if (!NonFatal$.MODULE$.apply(var10)) {
            this.logError(new Serializable(currentThreadName) {
               public static final long serialVersionUID = 0L;
               private final String currentThreadName$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"throw uncaught fatal error in thread ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.currentThreadName$1}));
               }

               public {
                  this.currentThreadName$1 = currentThreadName$1;
               }
            }, var10);
            throw var10;
         }

         BoxedUnit var6 = BoxedUnit.UNIT;
         return;
      }

   }

   public <T> T tryOrIOException(Function0<T> block) {
      try {
         return (T)block.apply();
      } catch (Throwable var9) {
         if (var9 instanceof IOException) {
            IOException var4 = (IOException)var9;
            this.logError(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Exception encountered";
               }
            }, var4);
            throw var4;
         } else {
            Option var6 = NonFatal$.MODULE$.unapply(var9);
            if (var6.isEmpty()) {
               throw var9;
            } else {
               Throwable e = (Throwable)var6.get();
               this.logError(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Exception encountered";
                  }
               }, e);
               throw new IOException(e);
            }
         }
      }
   }

   public void tryLogNonFatalError(Function0<BoxedUnit> block) {
      try {
         block.apply$mcV$sp();
      } catch (Throwable var8) {
         Option var4 = NonFatal$.MODULE$.unapply(var8);
         if (var4.isEmpty()) {
            throw var8;
         }

         Throwable t = (Throwable)var4.get();
         this.logError(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Uncaught exception in thread ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Thread.currentThread().getName()}));
            }
         }, t);
         BoxedUnit var6 = BoxedUnit.UNIT;
         return;
      }

   }

   public <T> T tryWithSafeFinally(Function0<T> block, Function0<BoxedUnit> finallyBlock) {
      Throwable originalThrowable = null;
      boolean var25 = false;

      Object var10000;
      try {
         try {
            var25 = true;
            var10000 = block.apply();
            var25 = false;
         } catch (Throwable var30) {
            originalThrowable = var30;
            throw var30;
         }
      } finally {
         if (var25) {
            try {
               finallyBlock.apply$mcV$sp();
            } catch (Throwable var31) {
               label255: {
                  if (var31 != null && originalThrowable != null) {
                     if (originalThrowable == null) {
                        if (var31 != null) {
                           break label255;
                        }
                     } else if (!originalThrowable.equals(var31)) {
                        break label255;
                     }
                  }

                  throw var31;
               }

               originalThrowable.addSuppressed(var31);
               this.logWarning(new Serializable(var31) {
                  public static final long serialVersionUID = 0L;
                  private final Throwable x5$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Suppressing exception in finally: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x5$1.getMessage()}));
                  }

                  public {
                     this.x5$1 = x5$1;
                  }
               }, var31);
               throw originalThrowable;
            }
         }
      }

      Object var5 = var10000;

      try {
         finallyBlock.apply$mcV$sp();
         return (T)var5;
      } catch (Throwable var33) {
         label269: {
            if (var33 != null && originalThrowable != null) {
               if (originalThrowable == null) {
                  if (var33 != null) {
                     break label269;
                  }
               } else if (!originalThrowable.equals(var33)) {
                  break label269;
               }
            }

            throw var33;
         }

         originalThrowable.addSuppressed(var33);
         this.logWarning(new Serializable(var33) {
            public static final long serialVersionUID = 0L;
            private final Throwable x5$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Suppressing exception in finally: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x5$1.getMessage()}));
            }

            public {
               this.x5$1 = x5$1;
            }
         }, var33);
         throw originalThrowable;
      }
   }

   public <T> T tryWithSafeFinallyAndFailureCallbacks(Function0<T> block, Function0<BoxedUnit> catchBlock, Function0<BoxedUnit> finallyBlock) {
      Throwable originalThrowable = null;
      boolean var31 = false;

      Object var10000;
      try {
         try {
            var31 = true;
            var10000 = block.apply();
            var31 = false;
         } catch (Throwable var44) {
            originalThrowable = var44;

            try {
               this.logError(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Aborting task";
                  }
               }, originalThrowable);
               ((TaskContextImpl)TaskContext$.MODULE$.get()).markTaskFailed(originalThrowable);
               catchBlock.apply$mcV$sp();
            } catch (Throwable var43) {
               if (var44 == null) {
                  if (var43 == null) {
                     throw var44;
                  }
               } else if (var44.equals(var43)) {
                  throw var44;
               }

               var44.addSuppressed(var43);
               this.logWarning(new Serializable(var43) {
                  public static final long serialVersionUID = 0L;
                  private final Throwable t$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Suppressing exception in catch: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.t$1.getMessage()}));
                  }

                  public {
                     this.t$1 = t$1;
                  }
               }, var43);
               throw var44;
            }

            throw var44;
         }
      } finally {
         if (var31) {
            try {
               finallyBlock.apply$mcV$sp();
            } catch (Throwable var42) {
               label393: {
                  if (var42 != null && originalThrowable != null) {
                     if (originalThrowable == null) {
                        if (var42 != null) {
                           break label393;
                        }
                     } else if (!originalThrowable.equals(var42)) {
                        break label393;
                     }
                  }

                  throw var42;
               }

               originalThrowable.addSuppressed(var42);
               this.logWarning(new Serializable(var42) {
                  public static final long serialVersionUID = 0L;
                  private final Throwable x5$2;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Suppressing exception in finally: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x5$2.getMessage()}));
                  }

                  public {
                     this.x5$2 = x5$2;
                  }
               }, var42);
               throw originalThrowable;
            }
         }
      }

      Object var6 = var10000;

      try {
         finallyBlock.apply$mcV$sp();
         return (T)var6;
      } catch (Throwable var46) {
         label417: {
            if (var46 != null && originalThrowable != null) {
               if (originalThrowable == null) {
                  if (var46 != null) {
                     break label417;
                  }
               } else if (!originalThrowable.equals(var46)) {
                  break label417;
               }
            }

            throw var46;
         }

         originalThrowable.addSuppressed(var46);
         this.logWarning(new Serializable(var46) {
            public static final long serialVersionUID = 0L;
            private final Throwable x5$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Suppressing exception in finally: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x5$2.getMessage()}));
            }

            public {
               this.x5$2 = x5$2;
            }
         }, var46);
         throw originalThrowable;
      }
   }

   public <T> void tryWithSafeFinallyAndFailureCallbacks$default$2(Function0<T> block) {
   }

   public <T> void tryWithSafeFinallyAndFailureCallbacks$default$3(Function0<T> block) {
   }

   public boolean org$apache$spark$util$Utils$$sparkInternalExclusionFunction(String className) {
      Regex SPARK_CORE_CLASS_REGEX = (new StringOps(Predef$.MODULE$.augmentString("^org\\.apache\\.spark(\\.api\\.java)?(\\.util)?(\\.rdd)?(\\.broadcast)?\\.[A-Z]"))).r();
      Regex SPARK_SQL_CLASS_REGEX = (new StringOps(Predef$.MODULE$.augmentString("^org\\.apache\\.spark\\.sql.*"))).r();
      String SCALA_CORE_CLASS_PREFIX = "scala";
      boolean isSparkClass = SPARK_CORE_CLASS_REGEX.findFirstIn(className).isDefined() || SPARK_SQL_CLASS_REGEX.findFirstIn(className).isDefined();
      boolean isScalaClass = className.startsWith(SCALA_CORE_CLASS_PREFIX);
      return isSparkClass || isScalaClass;
   }

   public CallSite getCallSite(Function1<String, Object> skipClass) {
      ArrayBuffer callStack;
      int callStackDepth;
      String var11;
      label17: {
         label16: {
            ObjectRef lastSparkMethod = ObjectRef.create("<unknown>");
            ObjectRef firstUserFile = ObjectRef.create("<unknown>");
            IntRef firstUserLine = IntRef.create(0);
            BooleanRef insideSpark = BooleanRef.create(true);
            callStack = (ArrayBuffer)(new ArrayBuffer()).$colon$plus("<unknown>", ArrayBuffer$.MODULE$.canBuildFrom());
            Predef$.MODULE$.refArrayOps((Object[])Thread.currentThread().getStackTrace()).foreach(new Serializable(skipClass, lastSparkMethod, firstUserFile, firstUserLine, insideSpark, callStack) {
               public static final long serialVersionUID = 0L;
               private final Function1 skipClass$1;
               private final ObjectRef lastSparkMethod$1;
               private final ObjectRef firstUserFile$1;
               private final IntRef firstUserLine$1;
               private final BooleanRef insideSpark$1;
               private final ArrayBuffer callStack$1;

               public final Object apply(StackTraceElement ste) {
                  Object var10000;
                  if (ste != null && ste.getMethodName() != null && !ste.getMethodName().contains("getStackTrace")) {
                     if (this.insideSpark$1.elem) {
                        if (BoxesRunTime.unboxToBoolean(this.skipClass$1.apply(ste.getClassName()))) {
                           String var4;
                           label32: {
                              label31: {
                                 var10000 = this.lastSparkMethod$1;
                                 var4 = ste.getMethodName();
                                 String var2 = "<init>";
                                 if (var4 == null) {
                                    if (var2 == null) {
                                       break label31;
                                    }
                                 } else if (var4.equals(var2)) {
                                    break label31;
                                 }

                                 var4 = ste.getMethodName();
                                 break label32;
                              }

                              var4 = ste.getClassName().substring(ste.getClassName().lastIndexOf(46) + 1);
                           }

                           var10000.elem = (T)var4;
                           this.callStack$1.update(0, ste.toString());
                           var10000 = BoxedUnit.UNIT;
                        } else {
                           if (ste.getFileName() != null) {
                              this.firstUserFile$1.elem = (T)ste.getFileName();
                              if (ste.getLineNumber() >= 0) {
                                 this.firstUserLine$1.elem = ste.getLineNumber();
                              }
                           }

                           this.callStack$1.$plus$eq(ste.toString());
                           this.insideSpark$1.elem = false;
                           var10000 = BoxedUnit.UNIT;
                        }
                     } else {
                        var10000 = this.callStack$1.$plus$eq(ste.toString());
                     }
                  } else {
                     var10000 = BoxedUnit.UNIT;
                  }

                  return var10000;
               }

               public {
                  this.skipClass$1 = skipClass$1;
                  this.lastSparkMethod$1 = lastSparkMethod$1;
                  this.firstUserFile$1 = firstUserFile$1;
                  this.firstUserLine$1 = firstUserLine$1;
                  this.insideSpark$1 = insideSpark$1;
                  this.callStack$1 = callStack$1;
               }
            });
            callStackDepth = (new StringOps(Predef$.MODULE$.augmentString(System.getProperty("spark.callstack.depth", "20")))).toInt();
            var11 = (String)firstUserFile.elem;
            String var9 = "HiveSessionImpl.java";
            if (var11 == null) {
               if (var9 == null) {
                  break label16;
               }
            } else if (var11.equals(var9)) {
               break label16;
            }

            var11 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " at ", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{(String)lastSparkMethod.elem, (String)firstUserFile.elem, BoxesRunTime.boxToInteger(firstUserLine.elem)}));
            break label17;
         }

         var11 = "Spark JDBC Server Query";
      }

      String shortForm = var11;
      String longForm = ((TraversableOnce)callStack.take(callStackDepth)).mkString("\n");
      return new CallSite(shortForm, longForm);
   }

   public Function1<String, Object> getCallSite$default$1() {
      return new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String className) {
            return Utils$.MODULE$.org$apache$spark$util$Utils$$sparkInternalExclusionFunction(className);
         }
      };
   }

   private String UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE_CONF() {
      return this.UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE_CONF;
   }

   private int DEFAULT_UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE() {
      return this.DEFAULT_UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE;
   }

   private LoadingCache<String, Long> compressedLogFileLengthCache() {
      return this.compressedLogFileLengthCache;
   }

   private void compressedLogFileLengthCache_$eq(LoadingCache<String, Long> x$1) {
      this.compressedLogFileLengthCache = x$1;
   }

   private synchronized LoadingCache<String, Long> getCompressedLogFileLengthCache(SparkConf sparkConf) {
      if (this.compressedLogFileLengthCache() == null) {
         int compressedLogFileLengthCacheSize = sparkConf.getInt(this.UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE_CONF(), this.DEFAULT_UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE());
         this.compressedLogFileLengthCache_$eq(CacheBuilder.newBuilder().maximumSize((long)compressedLogFileLengthCacheSize).build(new CacheLoader<String, Long>() {
            public Long load(String path) {
               return Predef$.MODULE$.long2Long(Utils$.MODULE$.org$apache$spark$util$Utils$$getCompressedFileLength(new File(path)));
            }
         }));
      }

      return this.compressedLogFileLengthCache();
   }

   public long getFileLength(File file, SparkConf workConf) {
      return file.getName().endsWith(".gz") ? Predef$.MODULE$.Long2long((Long)this.getCompressedLogFileLengthCache(workConf).get(file.getAbsolutePath())) : file.length();
   }

   public long org$apache$spark$util$Utils$$getCompressedFileLength(File file) {
      GZIPInputStream gzInputStream = null;

      long var10000;
      try {
         try {
            long fileSize = 0L;
            gzInputStream = new GZIPInputStream(new FileInputStream(file));
            int bufSize = 1024;
            byte[] buf = new byte[bufSize];

            for(int numBytes = ByteStreams.read(gzInputStream, buf, 0, bufSize); numBytes > 0; numBytes = ByteStreams.read(gzInputStream, buf, 0, bufSize)) {
               fileSize += (long)numBytes;
            }

            var10000 = fileSize;
         } catch (Throwable var14) {
            this.logError(new Serializable(file) {
               public static final long serialVersionUID = 0L;
               private final File file$2;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot get file length of ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$2}));
               }

               public {
                  this.file$2 = file$2;
               }
            }, var14);
            throw var14;
         }
      } finally {
         if (gzInputStream != null) {
            gzInputStream.close();
         }

      }

      return var10000;
   }

   public String offsetBytes(String path, long length, long start, long end) {
      File file = new File(path);
      long effectiveEnd = scala.math.package$.MODULE$.min(length, end);
      long effectiveStart = scala.math.package$.MODULE$.max(0L, start);
      byte[] buff = new byte[(int)(effectiveEnd - effectiveStart)];
      InputStream stream = (InputStream)(path.endsWith(".gz") ? new GZIPInputStream(new FileInputStream(file)) : new FileInputStream(file));

      try {
         ByteStreams.skipFully(stream, effectiveStart);
         ByteStreams.readFully(stream, buff);
      } finally {
         stream.close();
      }

      return Source$.MODULE$.fromBytes(buff, Codec$.MODULE$.fallbackSystemCodec()).mkString();
   }

   public String offsetBytes(Seq<File> files, Seq<Object> fileLengths, long start, long end) {
      Predef$.MODULE$.assert(files.length() == fileLengths.length());
      long startIndex = scala.math.package$.MODULE$.max(start, 0L);
      long endIndex = scala.math.package$.MODULE$.min(end, BoxesRunTime.unboxToLong(fileLengths.sum(Numeric.LongIsIntegral$.MODULE$)));
      scala.collection.immutable.Map fileToLength = ((TraversableOnce)files.zip(fileLengths, Seq$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
      this.logDebug(new Serializable(fileToLength) {
         public static final long serialVersionUID = 0L;
         private final scala.collection.immutable.Map fileToLength$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Log files: \n").append((Object)this.fileToLength$1.mkString("\n")).toString();
         }

         public {
            this.fileToLength$1 = fileToLength$1;
         }
      });
      StringBuffer stringBuffer = new StringBuffer((int)(endIndex - startIndex));
      LongRef sum = LongRef.create(0L);
      ((IterableLike)files.zip(fileLengths, Seq$.MODULE$.canBuildFrom())).foreach(new Serializable(startIndex, endIndex, fileToLength, stringBuffer, sum) {
         public static final long serialVersionUID = 0L;
         private final long startIndex$2;
         public final long endIndex$1;
         private final scala.collection.immutable.Map fileToLength$1;
         public final StringBuffer stringBuffer$1;
         private final LongRef sum$1;

         public final void apply(Tuple2<File, Object> x0$2) {
            if (x0$2 == null) {
               throw new MatchError(x0$2);
            } else {
               File file = (File)x0$2._1();
               long fileLength = x0$2._2$mcJ$sp();
               long startIndexOfFile = this.sum$1.elem;
               long endIndexOfFile = this.sum$1.elem + BoxesRunTime.unboxToLong(this.fileToLength$1.apply(file));
               Utils$.MODULE$.logDebug(new Serializable(this, file, startIndexOfFile) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final File file$3;
                  private final long startIndexOfFile$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Processing file ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$3}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"with start index = ", ", end index = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.startIndexOfFile$1), BoxesRunTime.boxToLong(this.$outer.endIndex$1)}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.file$3 = file$3;
                        this.startIndexOfFile$1 = startIndexOfFile$1;
                     }
                  }
               });
               if (this.startIndex$2 <= startIndexOfFile && this.endIndex$1 >= endIndexOfFile) {
                  this.stringBuffer$1.append(Utils$.MODULE$.offsetBytes(file.getAbsolutePath(), fileLength, 0L, BoxesRunTime.unboxToLong(this.fileToLength$1.apply(file))));
               } else if (this.startIndex$2 > startIndexOfFile && this.startIndex$2 < endIndexOfFile) {
                  long effectiveStartIndex = this.startIndex$2 - startIndexOfFile;
                  long effectiveEndIndex = scala.math.package$.MODULE$.min(this.endIndex$1 - startIndexOfFile, BoxesRunTime.unboxToLong(this.fileToLength$1.apply(file)));
                  this.stringBuffer$1.append(Utils$.MODULE$.offsetBytes(file.getAbsolutePath(), fileLength, effectiveStartIndex, effectiveEndIndex));
               } else if (this.endIndex$1 > startIndexOfFile && this.endIndex$1 < endIndexOfFile) {
                  long effectiveStartIndex = scala.math.package$.MODULE$.max(this.startIndex$2 - startIndexOfFile, 0L);
                  long effectiveEndIndex = this.endIndex$1 - startIndexOfFile;
                  this.stringBuffer$1.append(Utils$.MODULE$.offsetBytes(file.getAbsolutePath(), fileLength, effectiveStartIndex, effectiveEndIndex));
               } else {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               }

               this.sum$1.elem += BoxesRunTime.unboxToLong(this.fileToLength$1.apply(file));
               Utils$.MODULE$.logDebug(new Serializable(this, file) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final File file$3;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"After processing file ", ", string built is ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$3, this.$outer.stringBuffer$1.toString()}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.file$3 = file$3;
                     }
                  }
               });
               BoxedUnit var6 = BoxedUnit.UNIT;
            }
         }

         public {
            this.startIndex$2 = startIndex$2;
            this.endIndex$1 = endIndex$1;
            this.fileToLength$1 = fileToLength$1;
            this.stringBuffer$1 = stringBuffer$1;
            this.sum$1 = sum$1;
         }
      });
      return stringBuffer.toString();
   }

   public <T> T clone(T value, SerializerInstance serializer, ClassTag<T> evidence$2) {
      return (T)serializer.deserialize(serializer.serialize(value, evidence$2), evidence$2);
   }

   private boolean isSpace(char c) {
      return " \t\r\n".indexOf(c) != -1;
   }

   public Seq<String> splitCommandString(String s) {
      ArrayBuffer buf = new ArrayBuffer();
      boolean inWord = false;
      boolean inSingleQuote = false;
      boolean inDoubleQuote = false;
      StringBuilder curWord = new StringBuilder();

      for(int i = 0; i < s.length(); ++i) {
         char nextChar = s.charAt(i);
         if (inDoubleQuote) {
            if (nextChar == '"') {
               inDoubleQuote = false;
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else if (nextChar == '\\') {
               if (i < s.length() - 1) {
                  curWord.append(s.charAt(i + 1));
                  ++i;
               }

               BoxedUnit var9 = BoxedUnit.UNIT;
            } else {
               curWord.append(nextChar);
            }
         } else if (inSingleQuote) {
            if (nextChar == '\'') {
               inSingleQuote = false;
               BoxedUnit var10 = BoxedUnit.UNIT;
            } else {
               curWord.append(nextChar);
            }
         } else {
            if (nextChar == '"') {
               inWord = true;
               inDoubleQuote = true;
            } else if (nextChar == '\'') {
               inWord = true;
               inSingleQuote = true;
            } else if (this.isSpace(nextChar)) {
               if (inWord && this.isSpace(nextChar)) {
                  this.endWord$1(buf, curWord);
                  inWord = false;
               }
            } else {
               curWord.append(nextChar);
               inWord = true;
            }

            BoxedUnit var11 = BoxedUnit.UNIT;
         }
      }

      if (inWord || inDoubleQuote || inSingleQuote) {
         this.endWord$1(buf, curWord);
      }

      return buf;
   }

   public int nonNegativeMod(int x, int mod) {
      int rawMod = x % mod;
      return rawMod + (rawMod < 0 ? mod : 0);
   }

   public int nonNegativeHash(Object obj) {
      if (obj == null) {
         return 0;
      } else {
         int hash = obj.hashCode();
         int hashAbs = Integer.MIN_VALUE != hash ? scala.math.package$.MODULE$.abs(hash) : 0;
         return hashAbs;
      }
   }

   public int nanSafeCompareDoubles(double x, double y) {
      boolean xIsNan = Double.isNaN(x);
      boolean yIsNan = Double.isNaN(y);
      return (!xIsNan || !yIsNan) && x != y ? (xIsNan ? 1 : (yIsNan ? -1 : (x > y ? 1 : -1))) : 0;
   }

   public int nanSafeCompareFloats(float x, float y) {
      boolean xIsNan = Float.isNaN(x);
      boolean yIsNan = Float.isNaN(y);
      return (!xIsNan || !yIsNan) && x != y ? (xIsNan ? 1 : (yIsNan ? -1 : (x > y ? 1 : -1))) : 0;
   }

   public Map<String, String> getSystemProperties() {
      return ((TraversableOnce)((SetLike)JavaConverters$.MODULE$.asScalaSetConverter(System.getProperties().stringPropertyNames()).asScala()).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, String> apply(String key) {
            return new Tuple2<String, String>(key, System.getProperty(key));
         }
      }, Set$.MODULE$.canBuildFrom())).<String, String>toMap(Predef$.MODULE$.$conforms());
   }

   public void times(int numIters, Function0<BoxedUnit> f) {
      for(int i = 0; i < numIters; ++i) {
         f.apply$mcV$sp();
      }

   }

   public long timeIt(int numIters, Function0<BoxedUnit> f, Option<Function0<BoxedUnit>> prepare) {
      long var10000;
      if (prepare.isEmpty()) {
         long start = System.currentTimeMillis();
         this.times(numIters, f);
         var10000 = System.currentTimeMillis() - start;
      } else {
         int i = 0;

         long sum;
         for(sum = 0L; i < numIters; ++i) {
            ((Function0)prepare.get()).apply$mcV$sp();
            long start = System.currentTimeMillis();
            f.apply$mcV$sp();
            sum += System.currentTimeMillis() - start;
         }

         var10000 = sum;
      }

      return var10000;
   }

   public Option<Function0<BoxedUnit>> timeIt$default$3(int numIters) {
      return None$.MODULE$;
   }

   public <T> long getIteratorSize(Iterator<T> iterator) {
      long count = 0L;

      while(iterator.hasNext()) {
         ++count;
         iterator.next();
      }

      return count;
   }

   public <T> Iterator<Tuple2<T, Object>> getIteratorZipWithIndex(Iterator<T> iterator, long startIndex) {
      return new Iterator<Tuple2<T, Object>>(iterator, startIndex) {
         private long index;
         private final Iterator iterator$1;

         public Iterator<Tuple2<T, Object>> seq() {
            return Iterator$class.seq(this);
         }

         public boolean isEmpty() {
            return Iterator$class.isEmpty(this);
         }

         public boolean isTraversableAgain() {
            return Iterator$class.isTraversableAgain(this);
         }

         public boolean hasDefiniteSize() {
            return Iterator$class.hasDefiniteSize(this);
         }

         public Iterator<Tuple2<T, Object>> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<Tuple2<T, Object>> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<Tuple2<T, Object>> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<Tuple2<T, Object>, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<Tuple2<T, Object>, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<Tuple2<T, Object>> filter(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<T, Object>, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<Tuple2<T, Object>> withFilter(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<Tuple2<T, Object>> filterNot(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<Tuple2<T, Object>, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<T, Object>, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<Tuple2<T, Object>, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<Tuple2<T, Object>> takeWhile(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<Tuple2<T, Object>>, Iterator<Tuple2<T, Object>>> partition(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<Tuple2<T, Object>>, Iterator<Tuple2<T, Object>>> span(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<Tuple2<T, Object>> dropWhile(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<Tuple2<T, Object>, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<Tuple2<T, Object>, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<Tuple2<T, Object>, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<Tuple2<T, Object>> find(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<Tuple2<T, Object>, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<Tuple2<T, Object>> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<Tuple2<T, Object>>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<Tuple2<T, Object>>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<Tuple2<T, Object>>, Iterator<Tuple2<T, Object>>> duplicate() {
            return Iterator$class.duplicate(this);
         }

         public <B> Iterator<B> patch(int from, Iterator<B> patchElems, int replaced) {
            return Iterator$class.patch(this, from, patchElems, replaced);
         }

         public <B> void copyToArray(Object xs, int start, int len) {
            Iterator$class.copyToArray(this, xs, start, len);
         }

         public boolean sameElements(Iterator<?> that) {
            return Iterator$class.sameElements(this, that);
         }

         public Traversable<Tuple2<T, Object>> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<Tuple2<T, Object>> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<Tuple2<T, Object>> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<Tuple2<T, Object>> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<Tuple2<T, Object>, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<Tuple2<T, Object>, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, Tuple2<T, Object>, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<Tuple2<T, Object>, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, Tuple2<T, Object>, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<Tuple2<T, Object>, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, Tuple2<T, Object>, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<Tuple2<T, Object>, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<T, Object>, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<Tuple2<T, Object>, B, B> op) {
            return TraversableOnce$class.reduceRightOption(this, op);
         }

         public <A1> A1 reduce(Function2<A1, A1, A1> op) {
            return (A1)TraversableOnce$class.reduce(this, op);
         }

         public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
            return TraversableOnce$class.reduceOption(this, op);
         }

         public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
            return (A1)TraversableOnce$class.fold(this, z, op);
         }

         public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<T, Object>, B> seqop, Function2<B, B, B> combop) {
            return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
         }

         public <B> B sum(Numeric<B> num) {
            return (B)TraversableOnce$class.sum(this, num);
         }

         public <B> B product(Numeric<B> num) {
            return (B)TraversableOnce$class.product(this, num);
         }

         public Object min(Ordering cmp) {
            return TraversableOnce$class.min(this, cmp);
         }

         public Object max(Ordering cmp) {
            return TraversableOnce$class.max(this, cmp);
         }

         public Object maxBy(Function1 f, Ordering cmp) {
            return TraversableOnce$class.maxBy(this, f, cmp);
         }

         public Object minBy(Function1 f, Ordering cmp) {
            return TraversableOnce$class.minBy(this, f, cmp);
         }

         public <B> void copyToBuffer(Buffer<B> dest) {
            TraversableOnce$class.copyToBuffer(this, dest);
         }

         public <B> void copyToArray(Object xs, int start) {
            TraversableOnce$class.copyToArray(this, xs, start);
         }

         public <B> void copyToArray(Object xs) {
            TraversableOnce$class.copyToArray(this, xs);
         }

         public <B> Object toArray(ClassTag<B> evidence$1) {
            return TraversableOnce$class.toArray(this, evidence$1);
         }

         public List<Tuple2<T, Object>> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<Tuple2<T, Object>> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<Tuple2<T, Object>> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<Tuple2<T, Object>> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<Tuple2<T, Object>> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<T, Object>, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> scala.collection.immutable.Map<T, U> toMap(Predef.$less$colon$less<Tuple2<T, Object>, Tuple2<T, U>> ev) {
            return TraversableOnce$class.toMap(this, ev);
         }

         public String mkString(String start, String sep, String end) {
            return TraversableOnce$class.mkString(this, start, sep, end);
         }

         public String mkString(String sep) {
            return TraversableOnce$class.mkString(this, sep);
         }

         public String mkString() {
            return TraversableOnce$class.mkString(this);
         }

         public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
            return TraversableOnce$class.addString(this, b, start, sep, end);
         }

         public StringBuilder addString(StringBuilder b, String sep) {
            return TraversableOnce$class.addString(this, b, sep);
         }

         public StringBuilder addString(StringBuilder b) {
            return TraversableOnce$class.addString(this, b);
         }

         private long index() {
            return this.index;
         }

         private void index_$eq(long x$1) {
            this.index = x$1;
         }

         public boolean hasNext() {
            return this.iterator$1.hasNext();
         }

         public Tuple2<T, Object> next() {
            this.index_$eq(this.index() + 1L);
            return new Tuple2<T, Object>(this.iterator$1.next(), BoxesRunTime.boxToLong(this.index()));
         }

         public {
            this.iterator$1 = iterator$1;
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
            Predef$.MODULE$.require(startIndex$1 >= 0L, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "startIndex should be >= 0.";
               }
            });
            this.index = startIndex$1 - 1L;
         }
      };
   }

   public void symlink(File src, File dst) {
      if (src.isAbsolute()) {
         if (dst.isAbsolute()) {
            throw new IOException("Destination must be relative");
         } else {
            Files.createSymbolicLink(dst.toPath(), src.toPath());
         }
      } else {
         throw new IOException("Source must be absolute");
      }
   }

   public String getFormattedClassName(Object obj) {
      return obj.getClass().getSimpleName().replace("$", "");
   }

   public Option<JsonAST.JValue> jsonOption(JsonAST.JValue json) {
      Object var4;
      label28: {
         JsonAST.JNothing var10000 = .MODULE$.JNothing();
         if (var10000 == null) {
            if (json == null) {
               break label28;
            }
         } else if (var10000.equals(json)) {
            break label28;
         }

         if (json == null) {
            throw new MatchError(json);
         }

         var4 = new Some(json);
         return (Option<JsonAST.JValue>)var4;
      }

      var4 = None$.MODULE$;
      return (Option<JsonAST.JValue>)var4;
   }

   public JsonAST.JObject emptyJson() {
      return .MODULE$.JObject().apply(Nil$.MODULE$);
   }

   public FileSystem getHadoopFileSystem(URI path, Configuration conf) {
      return FileSystem.get(path, conf);
   }

   public FileSystem getHadoopFileSystem(String path, Configuration conf) {
      return this.getHadoopFileSystem(new URI(path), conf);
   }

   public Path getFilePath(File dir, String fileName) {
      Predef$.MODULE$.assert(dir.isDirectory());
      String path = (new File(dir, fileName)).getAbsolutePath();
      return new Path(path);
   }

   public boolean isWindows() {
      return this.isWindows;
   }

   public boolean isMac() {
      return this.isMac;
   }

   public Regex windowsDrive() {
      return this.windowsDrive;
   }

   public boolean isTesting() {
      return scala.sys.package$.MODULE$.env().contains("SPARK_TESTING") || scala.sys.package$.MODULE$.props().contains("spark.testing");
   }

   public String stripDirectory(String path) {
      return (new File(path)).getName();
   }

   public Option<Object> terminateProcess(Process process, long timeoutMs) {
      process.destroy();
      Object var10000;
      if (process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
         var10000 = Option$.MODULE$.apply(BoxesRunTime.boxToInteger(process.exitValue()));
      } else {
         try {
            process.destroyForcibly();
         } catch (Throwable var10) {
            label48: {
               Option var6 = NonFatal$.MODULE$.unapply(var10);
               if (var6.isEmpty()) {
                  throw var10;
               }

               Throwable e = (Throwable)var6.get();
               this.logWarning(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Exception when attempting to kill process";
                  }
               }, e);
               BoxedUnit var8 = BoxedUnit.UNIT;
               break label48;
            }
         }

         if (process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
            var10000 = Option$.MODULE$.apply(BoxesRunTime.boxToInteger(process.exitValue()));
         } else {
            this.logWarning(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Timed out waiting to forcibly kill process";
               }
            });
            var10000 = None$.MODULE$;
         }
      }

      return (Option<Object>)var10000;
   }

   public Option<String> getStderr(Process process, long timeoutMs) {
      boolean terminated = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
      return (Option<String>)(terminated ? new Some(Source$.MODULE$.fromInputStream(process.getErrorStream(), Codec$.MODULE$.fallbackSystemCodec()).getLines().mkString("\n")) : None$.MODULE$);
   }

   public <T> T logUncaughtExceptions(Function0<T> f) {
      try {
         return (T)f.apply();
      } catch (Throwable var8) {
         if (var8 instanceof ControlThrowable) {
            ControlThrowable var4 = (ControlThrowable)var8;
            throw (Throwable)var4;
         } else if (var8 != null) {
            this.logError(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Uncaught exception in thread ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Thread.currentThread().getName()}));
               }
            }, var8);
            throw var8;
         } else {
            throw var8;
         }
      }
   }

   public <T> Try<T> tryLog(Function0<T> f) {
      Object var10000;
      try {
         Object res = f.apply();
         var10000 = new Success(res);
      } catch (Throwable var9) {
         if (var9 instanceof ControlThrowable) {
            ControlThrowable var4 = (ControlThrowable)var9;
            throw (Throwable)var4;
         }

         if (var9 == null) {
            throw var9;
         }

         this.logError(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Uncaught exception in thread ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Thread.currentThread().getName()}));
            }
         }, var9);
         Failure var5 = new Failure(var9);
         var10000 = var5;
         return (Try<T>)var10000;
      }

      return (Try<T>)var10000;
   }

   public boolean isFatalError(Throwable e) {
      Option var3 = NonFatal$.MODULE$.unapply(e);
      boolean var4;
      if (var3.isEmpty()) {
         if (e instanceof InterruptedException) {
            var4 = true;
         } else if (e instanceof NotImplementedError) {
            var4 = true;
         } else if (e instanceof ControlThrowable) {
            var4 = true;
         } else if (e instanceof LinkageError) {
            var4 = true;
         } else {
            var4 = false;
         }
      } else {
         var4 = true;
      }

      boolean var5;
      if (var4) {
         var5 = false;
      } else {
         var5 = true;
      }

      return var5;
   }

   public URI resolveURI(String path) {
      try {
         URI uri = new URI(path);
         if (uri.getScheme() != null) {
            return uri;
         }

         if (uri.getFragment() != null) {
            URI absoluteURI = (new File(uri.getPath())).getAbsoluteFile().toURI();
            return new URI(absoluteURI.getScheme(), absoluteURI.getHost(), absoluteURI.getPath(), uri.getFragment());
         }
      } catch (URISyntaxException var5) {
      }

      return (new File(path)).getAbsoluteFile().toURI();
   }

   public String resolveURIs(String paths) {
      return paths != null && !paths.trim().isEmpty() ? Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])paths.split(",")).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$9) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$9.trim()))).nonEmpty();
         }
      })).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final URI apply(String p) {
            return Utils$.MODULE$.resolveURI(p);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(URI.class)))).mkString(",") : "";
   }

   public String[] nonLocalPaths(String paths, boolean testWindows) {
      boolean windows = this.isWindows() || testWindows;
      return paths != null && !paths.trim().isEmpty() ? (String[])Predef$.MODULE$.refArrayOps((Object[])paths.split(",")).filter(new Serializable(windows) {
         public static final long serialVersionUID = 0L;
         private final boolean windows$1;

         public final boolean apply(String p) {
            URI uri = Utils$.MODULE$.resolveURI(p);
            CharSequence var3 = (CharSequence)Option$.MODULE$.apply(uri.getScheme()).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "file";
               }
            });
            Option var4 = Utils$.MODULE$.windowsDrive().unapplySeq(var3);
            boolean var5;
            if (!var4.isEmpty() && var4.get() != null && ((LinearSeqOptimized)var4.get()).lengthCompare(1) == 0 && this.windows$1) {
               var5 = false;
            } else {
               boolean var6;
               if ("local".equals(var3)) {
                  var6 = true;
               } else if ("file".equals(var3)) {
                  var6 = true;
               } else {
                  var6 = false;
               }

               if (var6) {
                  var5 = false;
               } else {
                  var5 = true;
               }
            }

            return var5;
         }

         public {
            this.windows$1 = windows$1;
         }
      }) : (String[])Array$.MODULE$.empty(ClassTag$.MODULE$.apply(String.class));
   }

   public boolean nonLocalPaths$default$2() {
      return false;
   }

   public String loadDefaultSparkProperties(SparkConf conf, String filePath) {
      String path = (String)Option$.MODULE$.apply(filePath).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return Utils$.MODULE$.getDefaultPropertiesFile(Utils$.MODULE$.getDefaultPropertiesFile$default$1());
         }
      });
      Option$.MODULE$.apply(path).foreach(new Serializable(conf) {
         public static final long serialVersionUID = 0L;
         public final SparkConf conf$3;

         public final void apply(String confFile) {
            ((IterableLike)Utils$.MODULE$.getPropertiesFromFile(confFile).filter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(Tuple2<String, String> x0$3) {
                  if (x0$3 != null) {
                     String k = (String)x0$3._1();
                     boolean var4 = k.startsWith("spark.");
                     return var4;
                  } else {
                     throw new MatchError(x0$3);
                  }
               }
            })).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply(Tuple2<String, String> x0$4) {
                  if (x0$4 != null) {
                     String k = (String)x0$4._1();
                     String v = (String)x0$4._2();
                     this.$outer.conf$3.setIfMissing(k, v);
                     String var5 = (String)scala.sys.package$.MODULE$.props().getOrElseUpdate(k, new Serializable(this, v) {
                        public static final long serialVersionUID = 0L;
                        private final String v$1;

                        public final String apply() {
                           return this.v$1;
                        }

                        public {
                           this.v$1 = v$1;
                        }
                     });
                     return var5;
                  } else {
                     throw new MatchError(x0$4);
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

         public {
            this.conf$3 = conf$3;
         }
      });
      return path;
   }

   public String loadDefaultSparkProperties$default$2() {
      return null;
   }

   public void updateSparkConfigFromProperties(SparkConf conf, Map<String, String> properties) {
      ((IterableLike)properties.filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> x0$5) {
            if (x0$5 != null) {
               String k = (String)x0$5._1();
               boolean var4 = k.startsWith("spark.");
               return var4;
            } else {
               throw new MatchError(x0$5);
            }
         }
      })).foreach(new Serializable(conf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf conf$5;

         public final SparkConf apply(Tuple2<String, String> x0$6) {
            if (x0$6 != null) {
               String k = (String)x0$6._1();
               String v = (String)x0$6._2();
               SparkConf var5 = this.conf$5.set(k, v);
               return var5;
            } else {
               throw new MatchError(x0$6);
            }
         }

         public {
            this.conf$5 = conf$5;
         }
      });
   }

   public Map<String, String> getPropertiesFromFile(String filename) {
      File file = new File(filename);
      Predef$.MODULE$.require(file.exists(), new Serializable(file) {
         public static final long serialVersionUID = 0L;
         private final File file$4;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Properties file ", " does not exist"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$4}));
         }

         public {
            this.file$4 = file$4;
         }
      });
      Predef$.MODULE$.require(file.isFile(), new Serializable(file) {
         public static final long serialVersionUID = 0L;
         private final File file$4;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Properties file ", " is not a normal file"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$4}));
         }

         public {
            this.file$4 = file$4;
         }
      });
      InputStreamReader inReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);

      try {
         Properties properties = new Properties();
         properties.load(inReader);
         ((TraversableOnce)((SetLike)JavaConverters$.MODULE$.asScalaSetConverter(properties.stringPropertyNames()).asScala()).map(new Serializable(properties) {
            public static final long serialVersionUID = 0L;
            private final Properties properties$1;

            public final Tuple2<String, String> apply(String k) {
               return new Tuple2<String, String>(k, this.properties$1.getProperty(k).trim());
            }

            public {
               this.properties$1 = properties$1;
            }
         }, Set$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
      } catch (IOException var9) {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed when loading Spark properties from ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{filename})), var9);
      } finally {
         inReader.close();
      }

      return inReader;
   }

   public String getDefaultPropertiesFile(Map<String, String> env) {
      return (String)env.get("SPARK_CONF_DIR").orElse(new Serializable(env) {
         public static final long serialVersionUID = 0L;
         private final Map env$1;

         public final Option<String> apply() {
            return this.env$1.get("SPARK_HOME").<String>map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String t) {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "", "conf"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{t, File.separator}));
               }
            });
         }

         public {
            this.env$1 = env$1;
         }
      }).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final File apply(String t) {
            return new File((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "", "spark-defaults.conf"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{t, File.separator})));
         }
      }).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(File x$10) {
            return x$10.isFile();
         }
      }).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(File x$11) {
            return x$11.getAbsolutePath();
         }
      }).orNull(Predef$.MODULE$.$conforms());
   }

   public Map<String, String> getDefaultPropertiesFile$default$1() {
      return scala.sys.package$.MODULE$.env();
   }

   public String exceptionString(Throwable e) {
      String var10000;
      if (e == null) {
         var10000 = "";
      } else {
         StringWriter stringWriter = new StringWriter();
         e.printStackTrace(new PrintWriter(stringWriter));
         var10000 = stringWriter.toString();
      }

      return var10000;
   }

   public Utils.Lock org$apache$spark$util$Utils$$Lock(LockInfo lock) {
      return new Utils.Lock(lock);
   }

   public ThreadStackTrace[] getThreadDump() {
      ThreadInfo[] threadInfos = (ThreadInfo[])Predef$.MODULE$.refArrayOps((Object[])ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ThreadInfo x$12) {
            return x$12 != null;
         }
      });
      return (ThreadStackTrace[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])threadInfos).sortBy(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(ThreadInfo x$13) {
            return x$13.getThreadId();
         }
      }, Ordering.Long$.MODULE$)).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final ThreadStackTrace apply(ThreadInfo threadInfo) {
            return Utils$.MODULE$.org$apache$spark$util$Utils$$threadInfoToThreadStackTrace(threadInfo);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(ThreadStackTrace.class)));
   }

   public Option<ThreadStackTrace> getThreadDumpForThread(long threadId) {
      Object var10000;
      if (threadId <= 0L) {
         var10000 = None$.MODULE$;
      } else {
         Option threadInfo = Option$.MODULE$.apply(ManagementFactory.getThreadMXBean().getThreadInfo(threadId, Integer.MAX_VALUE));
         var10000 = threadInfo.map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final ThreadStackTrace apply(ThreadInfo threadInfo) {
               return Utils$.MODULE$.org$apache$spark$util$Utils$$threadInfoToThreadStackTrace(threadInfo);
            }
         });
      }

      return (Option<ThreadStackTrace>)var10000;
   }

   public ThreadStackTrace org$apache$spark$util$Utils$$threadInfoToThreadStackTrace(ThreadInfo threadInfo) {
      scala.collection.immutable.Map monitors = Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])threadInfo.getLockedMonitors()).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Tuple2<StackTraceElement, MonitorInfo> apply(MonitorInfo m) {
            return Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(m.getLockedStackFrame()), m);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).toMap(Predef$.MODULE$.$conforms());
      String stackTrace = Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])threadInfo.getStackTrace()).map(new Serializable(monitors) {
         public static final long serialVersionUID = 0L;
         private final scala.collection.immutable.Map monitors$1;

         public final String apply(StackTraceElement frame) {
            Option var2 = this.monitors$1.get(frame);
            String var5;
            if (var2 instanceof Some) {
               Some var3 = (Some)var2;
               MonitorInfo monitor = (MonitorInfo)var3.x();
               var5 = (new StringBuilder()).append((Object)monitor.getLockedStackFrame().toString()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" => holding ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.org$apache$spark$util$Utils$$Lock(monitor).lockString()}))).toString();
            } else {
               if (!None$.MODULE$.equals(var2)) {
                  throw new MatchError(var2);
               }

               var5 = frame.toString();
            }

            return var5;
         }

         public {
            this.monitors$1 = monitors$1;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).mkString("\n");
      Set heldLocks = Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])threadInfo.getLockedSynchronizers()).$plus$plus(Predef$.MODULE$.refArrayOps((Object[])threadInfo.getLockedMonitors()), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(LockInfo.class)))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(LockInfo x$14) {
            return Utils$.MODULE$.org$apache$spark$util$Utils$$Lock(x$14).lockString();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).toSet();
      return new ThreadStackTrace(threadInfo.getThreadId(), threadInfo.getThreadName(), threadInfo.getThreadState(), stackTrace, (Option)(threadInfo.getLockOwnerId() < 0L ? None$.MODULE$ : new Some(BoxesRunTime.boxToLong(threadInfo.getLockOwnerId()))), (String)Option$.MODULE$.apply(threadInfo.getLockInfo()).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(LockInfo x$15) {
            return Utils$.MODULE$.org$apache$spark$util$Utils$$Lock(x$15).lockString();
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      }), heldLocks.toSeq());
   }

   public Seq<String> sparkJavaOpts(SparkConf conf, Function1<String, Object> filterKey) {
      return (Seq)Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(conf.getAll()).filter(new Serializable(filterKey) {
         public static final long serialVersionUID = 0L;
         private final Function1 filterKey$1;

         public final boolean apply(Tuple2<String, String> x0$7) {
            if (x0$7 != null) {
               String k = (String)x0$7._1();
               boolean var4 = BoxesRunTime.unboxToBoolean(this.filterKey$1.apply(k));
               return var4;
            } else {
               throw new MatchError(x0$7);
            }
         }

         public {
            this.filterKey$1 = filterKey$1;
         }
      })).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<String, String> x0$8) {
            if (x0$8 != null) {
               String k = (String)x0$8._1();
               String v = (String)x0$8._2();
               String var5 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"-D", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{k, v}));
               return var5;
            } else {
               throw new MatchError(x0$8);
            }
         }
      }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
   }

   public Function1<String, Object> sparkJavaOpts$default$2() {
      return new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$16) {
            return true;
         }
      };
   }

   public int portMaxRetries(SparkConf conf) {
      Option maxRetries = conf.getOption("spark.port.maxRetries").map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$17) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$17))).toInt();
         }
      });
      return conf.contains("spark.testing") ? BoxesRunTime.unboxToInt(maxRetries.getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 100;
         }
      })) : BoxesRunTime.unboxToInt(maxRetries.getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 16;
         }
      }));
   }

   public int userPort(int base, int offset) {
      return (base + offset - 1024) % 'ﰀ' + 1024;
   }

   public <T> Tuple2<T, Object> startServiceOnPort(int startPort, Function1<Object, Tuple2<T, Object>> startService, SparkConf conf, String serviceName) {
      Object var5 = new Object();

      try {
         Predef$.MODULE$.require(startPort == 0 || 1024 <= startPort && startPort < 65536, new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "startPort should be between 1024 and 65535 (inclusive), or 0 for a random free port.";
            }
         });
         String serviceString = serviceName.isEmpty() ? "" : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" '", "'"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{serviceName}));
         int maxRetries = this.portMaxRetries(conf);
         RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(0), maxRetries).foreach$mVc$sp(new Serializable(startPort, startService, serviceString, maxRetries, var5) {
            public static final long serialVersionUID = 0L;
            private final int startPort$1;
            private final Function1 startService$1;
            public final String serviceString$1;
            private final int maxRetries$1;
            private final Object nonLocalReturnKey2$1;

            public final void apply(int offset) {
               this.apply$mcVI$sp(offset);
            }

            public void apply$mcVI$sp(int offset) {
               int tryPort = this.startPort$1 == 0 ? this.startPort$1 : Utils$.MODULE$.userPort(this.startPort$1, offset);

               try {
                  Tuple2 var10 = (Tuple2)this.startService$1.apply(BoxesRunTime.boxToInteger(tryPort));
                  if (var10 != null) {
                     Object service = var10._1();
                     int port = var10._2$mcI$sp();
                     Tuple2 var13 = new Tuple2(service, BoxesRunTime.boxToInteger(port));
                     Object service = var13._1();
                     int port = var13._2$mcI$sp();
                     Utils$.MODULE$.logInfo(new Serializable(this, port) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final int port$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Successfully started service", " on port ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.serviceString$1, BoxesRunTime.boxToInteger(this.port$1)}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.port$1 = port$1;
                           }
                        }
                     });
                     throw new NonLocalReturnControl(this.nonLocalReturnKey2$1, new Tuple2(service, BoxesRunTime.boxToInteger(port)));
                  } else {
                     throw new MatchError(var10);
                  }
               } catch (Throwable var17) {
                  if (var17 instanceof Exception) {
                     Exception var5 = (Exception)var17;
                     if (Utils$.MODULE$.isBindCollision(var5)) {
                        if (offset >= this.maxRetries$1) {
                           String exceptionMessage = this.startPort$1 == 0 ? (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": Service", " failed after "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{var5.getMessage(), this.serviceString$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " retries (on a random free port)! "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.maxRetries$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Consider explicitly setting the appropriate binding address for "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"the service", " (for example spark.driver.bindAddress "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.serviceString$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"for SparkDriver) to the correct binding address."})))).s(Nil$.MODULE$)).toString() : (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": Service", " failed after "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{var5.getMessage(), this.serviceString$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " retries (starting from ", ")! Consider explicitly setting "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.maxRetries$1), BoxesRunTime.boxToInteger(this.startPort$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"the appropriate port for the service", " (for example spark.ui.port "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.serviceString$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"for SparkUI) to an available port or increasing spark.port.maxRetries."})))).s(Nil$.MODULE$)).toString();
                           BindException exception = new BindException(exceptionMessage);
                           exception.setStackTrace(var5.getStackTrace());
                           throw exception;
                        }

                        if (this.startPort$1 == 0) {
                           Utils$.MODULE$.logWarning(new Serializable(this) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;

                              public final String apply() {
                                 return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Service", " could not bind on a random free port. "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.serviceString$1}))).append((Object)"You may check whether configuring an appropriate binding address.").toString();
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                 }
                              }
                           });
                           BoxedUnit var10000 = BoxedUnit.UNIT;
                        } else {
                           Utils$.MODULE$.logWarning(new Serializable(this, tryPort) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;
                              private final int tryPort$1;

                              public final String apply() {
                                 return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Service", " could not bind on port ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.serviceString$1, BoxesRunTime.boxToInteger(this.tryPort$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempting port ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.tryPort$1 + 1)}))).toString();
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                    this.tryPort$1 = tryPort$1;
                                 }
                              }
                           });
                           BoxedUnit var18 = BoxedUnit.UNIT;
                        }

                        return;
                     }
                  }

                  throw var17;
               }
            }

            public {
               this.startPort$1 = startPort$1;
               this.startService$1 = startService$1;
               this.serviceString$1 = serviceString$1;
               this.maxRetries$1 = maxRetries$1;
               this.nonLocalReturnKey2$1 = nonLocalReturnKey2$1;
            }
         });
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to start service", " on port ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{serviceString, BoxesRunTime.boxToInteger(startPort)})));
      } catch (NonLocalReturnControl var9) {
         if (var9.key() == var5) {
            return (Tuple2)var9.value();
         } else {
            throw var9;
         }
      }
   }

   public <T> String startServiceOnPort$default$4() {
      return "";
   }

   public boolean isBindCollision(Throwable exception) {
      while(true) {
         if (exception instanceof BindException) {
            BindException var4 = (BindException)exception;
            if (var4.getMessage() != null) {
               return true;
            }

            exception = var4.getCause();
         } else {
            boolean var5;
            if (exception instanceof MultiException) {
               MultiException var6 = (MultiException)exception;
               var5 = ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter(var6.getThrowables()).asScala()).exists(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final boolean apply(Throwable exception) {
                     return Utils$.MODULE$.isBindCollision(exception);
                  }
               });
            } else if (exception instanceof Errors.NativeIoException) {
               Errors.NativeIoException var7 = (Errors.NativeIoException)exception;
               if (var7.getMessage() == null || !var7.getMessage().startsWith("bind() failed: ")) {
                  exception = var7.getCause();
                  continue;
               }

               var5 = true;
            } else {
               if (exception instanceof Exception) {
                  Exception var8 = (Exception)exception;
                  exception = var8.getCause();
                  continue;
               }

               var5 = false;
            }

            return var5;
         }
      }
   }

   public void setLogLevel(Level l) {
      org.apache.log4j.Logger.getRootLogger().setLevel(l);
   }

   public void configTestLog4j(String level) {
      Properties pro = new Properties();
      pro.put("log4j.rootLogger", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", console"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{level})));
      pro.put("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
      pro.put("log4j.appender.console.target", "System.err");
      pro.put("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
      pro.put("log4j.appender.console.layout.ConversionPattern", "%d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n");
      PropertyConfigurator.configure(pro);
   }

   public URLConnection setupSecureURLConnection(URLConnection urlConnection, SecurityManager sm) {
      Object var5;
      if (urlConnection instanceof HttpsURLConnection) {
         HttpsURLConnection var4 = (HttpsURLConnection)urlConnection;
         sm.sslSocketFactory().foreach(new Serializable(var4) {
            public static final long serialVersionUID = 0L;
            private final HttpsURLConnection x2$1;

            public final void apply(SSLSocketFactory x$1) {
               this.x2$1.setSSLSocketFactory(x$1);
            }

            public {
               this.x2$1 = x2$1;
            }
         });
         sm.hostnameVerifier().foreach(new Serializable(var4) {
            public static final long serialVersionUID = 0L;
            private final HttpsURLConnection x2$1;

            public final void apply(HostnameVerifier x$1) {
               this.x2$1.setHostnameVerifier(x$1);
            }

            public {
               this.x2$1 = x2$1;
            }
         });
         var5 = var4;
      } else {
         var5 = urlConnection;
      }

      return (URLConnection)var5;
   }

   public Object invoke(Class<?> clazz, Object obj, String methodName, Seq<Tuple2<Class<?>, Object>> args) {
      Tuple2 var6 = args.unzip(Predef$.MODULE$.$conforms());
      if (var6 != null) {
         Seq types = (Seq)var6._1();
         Seq values = (Seq)var6._2();
         Tuple2 var9 = new Tuple2(types, values);
         Seq types = (Seq)var9._1();
         Seq values = (Seq)var9._2();
         Method method = clazz.getDeclaredMethod(methodName, (Class[])types.toArray(ClassTag$.MODULE$.apply(Class.class)));
         method.setAccessible(true);
         return method.invoke(obj, values.toSeq().toArray(ClassTag$.MODULE$.AnyRef()));
      } else {
         throw new MatchError(var6);
      }
   }

   public long getMaxResultSize(SparkConf conf) {
      return (long)this.memoryStringToMb(conf.get("spark.driver.maxResultSize", "1g")) << 20;
   }

   public String libraryPathEnvName() {
      return this.isWindows() ? "PATH" : (this.isMac() ? "DYLD_LIBRARY_PATH" : "LD_LIBRARY_PATH");
   }

   public String libraryPathEnvPrefix(Seq<String> libraryPaths) {
      String libraryPathScriptVar = this.isWindows() ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"%", "%"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.libraryPathEnvName()})) : (new StringBuilder()).append((Object)"$").append((Object)this.libraryPathEnvName()).toString();
      String libraryPath = ((TraversableOnce)libraryPaths.$colon$plus(libraryPathScriptVar, Seq$.MODULE$.canBuildFrom())).mkString("\"", File.pathSeparator, "\"");
      String ampersand = this.isWindows() ? " &" : "";
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "=", "", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.libraryPathEnvName(), libraryPath, ampersand}));
   }

   public String getSparkOrYarnConfig(SparkConf conf, String key, String var3) {
      String sparkValue;
      String var6;
      label23: {
         sparkValue = conf.get(key, var3);
         var6 = conf.get("spark.master", (String)null);
         String var5 = "yarn";
         if (var6 == null) {
            if (var5 == null) {
               break label23;
            }
         } else if (var6.equals(var5)) {
            break label23;
         }

         var6 = sparkValue;
         return var6;
      }

      var6 = (new YarnConfiguration(SparkHadoopUtil$.MODULE$.get().newConfiguration(conf))).get(key, sparkValue);
      return var6;
   }

   public Tuple2<String, Object> extractHostPortFromSparkUrl(String sparkUrl) throws SparkException {
      try {
         URI uri = new URI(sparkUrl);
         String host = uri.getHost();
         int port = uri.getPort();
         String var10000 = uri.getScheme();
         String var6 = "spark";
         if (var10000 == null) {
            if (var6 != null) {
               throw new SparkException((new StringBuilder()).append((Object)"Invalid master URL: ").append((Object)sparkUrl).toString());
            }
         } else if (!var10000.equals(var6)) {
            throw new SparkException((new StringBuilder()).append((Object)"Invalid master URL: ").append((Object)sparkUrl).toString());
         }

         if (host != null && port >= 0 && (uri.getPath() == null || uri.getPath().isEmpty()) && uri.getFragment() == null && uri.getQuery() == null && uri.getUserInfo() == null) {
            return new Tuple2<String, Object>(host, BoxesRunTime.boxToInteger(port));
         } else {
            throw new SparkException((new StringBuilder()).append((Object)"Invalid master URL: ").append((Object)sparkUrl).toString());
         }
      } catch (URISyntaxException var7) {
         throw new SparkException((new StringBuilder()).append((Object)"Invalid master URL: ").append((Object)sparkUrl).toString(), var7);
      }
   }

   public String getCurrentUserName() {
      return (String)Option$.MODULE$.apply(System.getenv("SPARK_USER")).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return UserGroupInformation.getCurrentUser().getShortUserName();
         }
      });
   }

   public Set<String> EMPTY_USER_GROUPS() {
      return this.EMPTY_USER_GROUPS;
   }

   public Set<String> getCurrentUserGroups(SparkConf sparkConf, String username) {
      String groupProviderClassName = sparkConf.get("spark.user.groups.mapping", "org.apache.spark.security.ShellBasedGroupsMappingProvider");
      String var4 = "";
      if (groupProviderClassName == null) {
         if (var4 == null) {
            return this.EMPTY_USER_GROUPS();
         }
      } else if (groupProviderClassName.equals(var4)) {
         return this.EMPTY_USER_GROUPS();
      }

      try {
         GroupMappingServiceProvider groupMappingServiceProvider = (GroupMappingServiceProvider)this.classForName(groupProviderClassName).newInstance();
         Set currentUserGroups = groupMappingServiceProvider.getGroups(username);
         return currentUserGroups;
      } catch (Exception var8) {
         this.logError(new Serializable(username) {
            public static final long serialVersionUID = 0L;
            private final String username$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error getting groups for user=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.username$1}));
            }

            public {
               this.username$1 = username$1;
            }
         }, var8);
         return this.EMPTY_USER_GROUPS();
      }
   }

   public String[] parseStandaloneMasterUrls(String masterUrls) {
      return (String[])Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(masterUrls))).stripPrefix("spark://").split(",")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$20) {
            return (new StringBuilder()).append((Object)"spark://").append((Object)x$20).toString();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)));
   }

   public String BACKUP_STANDALONE_MASTER_PREFIX() {
      return this.BACKUP_STANDALONE_MASTER_PREFIX;
   }

   public boolean responseFromBackup(String msg) {
      return msg.startsWith(this.BACKUP_STANDALONE_MASTER_PREFIX());
   }

   public <T> T withDummyCallSite(SparkContext sc, Function0<T> body) {
      String oldShortCallSite = sc.getLocalProperty(CallSite$.MODULE$.SHORT_FORM());
      String oldLongCallSite = sc.getLocalProperty(CallSite$.MODULE$.LONG_FORM());

      Object var10000;
      try {
         sc.setLocalProperty(CallSite$.MODULE$.SHORT_FORM(), "");
         sc.setLocalProperty(CallSite$.MODULE$.LONG_FORM(), "");
         var10000 = body.apply();
      } finally {
         sc.setLocalProperty(CallSite$.MODULE$.SHORT_FORM(), oldShortCallSite);
         sc.setLocalProperty(CallSite$.MODULE$.LONG_FORM(), oldLongCallSite);
      }

      return (T)var10000;
   }

   public boolean isInDirectory(File parent, File child) {
      while(true) {
         if (child != null && parent != null) {
            if (child.exists() && parent.exists() && parent.isDirectory()) {
               if (parent.equals(child)) {
                  return true;
               }

               child = child.getParentFile();
               parent = parent;
               continue;
            }

            return false;
         }

         return false;
      }
   }

   public boolean isLocalMaster(SparkConf conf) {
      boolean var10000;
      label18: {
         String master = conf.get("spark.master", "");
         String var3 = "local";
         if (master == null) {
            if (var3 == null) {
               break label18;
            }
         } else if (master.equals(var3)) {
            break label18;
         }

         if (!master.startsWith("local[")) {
            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public boolean isDynamicAllocationEnabled(SparkConf conf) {
      boolean dynamicAllocationEnabled = conf.getBoolean("spark.dynamicAllocation.enabled", false);
      return dynamicAllocationEnabled && (!this.isLocalMaster(conf) || conf.getBoolean("spark.dynamicAllocation.testing", false));
   }

   public int getDynamicAllocationInitialExecutors(SparkConf conf) {
      if (BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_INITIAL_EXECUTORS())) < BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_MIN_EXECUTORS()))) {
         this.logWarning(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " less than "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_INITIAL_EXECUTORS().key()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " is invalid, ignoring its setting, "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_MIN_EXECUTORS().key()}))).append((Object)"please update your configs.").toString();
            }
         });
      }

      if (BoxesRunTime.unboxToInt(((Option)conf.get((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.EXECUTOR_INSTANCES())).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      })) < BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_MIN_EXECUTORS()))) {
         this.logWarning(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " less than "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.EXECUTOR_INSTANCES().key()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " is invalid, ignoring its setting, "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_MIN_EXECUTORS().key()}))).append((Object)"please update your configs.").toString();
            }
         });
      }

      int initialExecutors = BoxesRunTime.unboxToInt(((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_MIN_EXECUTORS())), BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_INITIAL_EXECUTORS())), BoxesRunTime.unboxToInt(((Option)conf.get((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.EXECUTOR_INSTANCES())).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }))}))).max(Ordering.Int$.MODULE$));
      this.logInfo(new Serializable(initialExecutors) {
         public static final long serialVersionUID = 0L;
         private final int initialExecutors$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using initial executors = ", ", max of "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.initialExecutors$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", ", " and "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_INITIAL_EXECUTORS().key(), org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_MIN_EXECUTORS().key()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.EXECUTOR_INSTANCES().key()}))).toString();
         }

         public {
            this.initialExecutors$1 = initialExecutors$1;
         }
      });
      return initialExecutors;
   }

   public <R extends Closeable, T> T tryWithResource(Function0<R> createResource, Function1<R, T> f) {
      Closeable resource = (Closeable)createResource.apply();

      Object var10000;
      try {
         var10000 = f.apply(resource);
      } finally {
         resource.close();
      }

      return (T)var10000;
   }

   public File tempFileWith(File path) {
      return new File((new StringBuilder()).append((Object)path.getAbsolutePath()).append((Object)".").append((Object)UUID.randomUUID()).toString());
   }

   public String getProcessName() {
      return ManagementFactory.getRuntimeMXBean().getName();
   }

   public void initDaemon(Logger log) {
      log.info((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Started daemon with process name: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.getProcessName()})));
      SignalUtils$.MODULE$.registerLogger(log);
   }

   public Set<String> unionFileLists(Option<String> leftList, Option<String> rightList) {
      ObjectRef allFiles = ObjectRef.create(Predef$.MODULE$.Set().empty());
      leftList.foreach(new Serializable(allFiles) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef allFiles$1;

         public final void apply(String value) {
            this.allFiles$1.elem = (T)((Set)((Set)this.allFiles$1.elem).$plus$plus(Predef$.MODULE$.refArrayOps((Object[])value.split(","))));
         }

         public {
            this.allFiles$1 = allFiles$1;
         }
      });
      rightList.foreach(new Serializable(allFiles) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef allFiles$1;

         public final void apply(String value) {
            this.allFiles$1.elem = (T)((Set)((Set)this.allFiles$1.elem).$plus$plus(Predef$.MODULE$.refArrayOps((Object[])value.split(","))));
         }

         public {
            this.allFiles$1 = allFiles$1;
         }
      });
      return (Set)((Set)allFiles.elem).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$21) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$21))).nonEmpty();
         }
      });
   }

   public Seq<String> getUserJars(SparkConf conf) {
      Option sparkJars = conf.getOption("spark.jars");
      return (Seq)Option$.MODULE$.option2Iterable(sparkJars.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String[] apply(String x$22) {
            return x$22.split(",");
         }
      }).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String[] apply(String[] x$23) {
            return (String[])Predef$.MODULE$.refArrayOps((Object[])x$23).filter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(String x$24) {
                  return (new StringOps(Predef$.MODULE$.augmentString(x$24))).nonEmpty();
               }
            });
         }
      })).toSeq().flatten(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final ArrayOps<String> apply(String[] xs) {
            return Predef$.MODULE$.<String>refArrayOps((Object[])xs);
         }
      });
   }

   public Seq<String> getLocalUserJarsForShell(SparkConf conf) {
      Option localJars = conf.getOption("spark.repl.local.jars");
      return (Seq)Option$.MODULE$.option2Iterable(localJars.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String[] apply(String x$25) {
            return x$25.split(",");
         }
      }).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String[] apply(String[] x$26) {
            return (String[])Predef$.MODULE$.refArrayOps((Object[])x$26).filter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(String x$27) {
                  return (new StringOps(Predef$.MODULE$.augmentString(x$27))).nonEmpty();
               }
            });
         }
      })).toSeq().flatten(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final ArrayOps<String> apply(String[] xs) {
            return Predef$.MODULE$.<String>refArrayOps((Object[])xs);
         }
      });
   }

   public String REDACTION_REPLACEMENT_TEXT() {
      return this.REDACTION_REPLACEMENT_TEXT;
   }

   public Seq<Tuple2<String, String>> redact(SparkConf conf, Seq<Tuple2<String, String>> kvs) {
      Regex redactionPattern = (Regex)conf.get(org.apache.spark.internal.config.package$.MODULE$.SECRET_REDACTION_PATTERN());
      return this.redact(redactionPattern, kvs);
   }

   public Seq<Tuple2<String, String>> redact(Option<Regex> regex, Seq<Tuple2<String, String>> kvs) {
      Seq var4;
      if (None$.MODULE$.equals(regex)) {
         var4 = kvs;
      } else {
         if (!(regex instanceof Some)) {
            throw new MatchError(regex);
         }

         Some var5 = (Some)regex;
         Regex r = (Regex)var5.x();
         var4 = this.redact(r, kvs);
      }

      return var4;
   }

   public String redact(Option<Regex> regex, String text) {
      String var4;
      if (None$.MODULE$.equals(regex)) {
         var4 = text;
      } else {
         if (!(regex instanceof Some)) {
            throw new MatchError(regex);
         }

         Some var5 = (Some)regex;
         Regex r = (Regex)var5.x();
         var4 = text != null && !text.isEmpty() ? r.replaceAllIn(text, this.REDACTION_REPLACEMENT_TEXT()) : text;
      }

      return var4;
   }

   private Seq<Tuple2<String, String>> redact(Regex redactionPattern, Seq<Tuple2<String, String>> kvs) {
      return (Seq)kvs.map(new Serializable(redactionPattern) {
         public static final long serialVersionUID = 0L;
         public final Regex redactionPattern$1;

         public final Tuple2<String, String> apply(Tuple2<String, String> x0$9) {
            if (x0$9 != null) {
               String key = (String)x0$9._1();
               String value = (String)x0$9._2();
               Tuple2 var5 = (Tuple2)this.redactionPattern$1.findFirstIn(key).orElse(new Serializable(this, value) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final String value$1;

                  public final Option<String> apply() {
                     return this.$outer.redactionPattern$1.findFirstIn(this.value$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.value$1 = value$1;
                     }
                  }
               }).map(new Serializable(this, key) {
                  public static final long serialVersionUID = 0L;
                  private final String key$1;

                  public final Tuple2<String, String> apply(String x$28) {
                     return new Tuple2<String, String>(this.key$1, Utils$.MODULE$.REDACTION_REPLACEMENT_TEXT());
                  }

                  public {
                     this.key$1 = key$1;
                  }
               }).getOrElse(new Serializable(this, key, value) {
                  public static final long serialVersionUID = 0L;
                  private final String key$1;
                  private final String value$1;

                  public final Tuple2<String, String> apply() {
                     return new Tuple2<String, String>(this.key$1, this.value$1);
                  }

                  public {
                     this.key$1 = key$1;
                     this.value$1 = value$1;
                  }
               });
               return var5;
            } else {
               throw new MatchError(x0$9);
            }
         }

         public {
            this.redactionPattern$1 = redactionPattern$1;
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public Seq<Tuple2<String, String>> redact(Map<String, String> kvs) {
      Regex redactionPattern = (new StringOps(Predef$.MODULE$.augmentString((String)kvs.getOrElse(org.apache.spark.internal.config.package$.MODULE$.SECRET_REDACTION_PATTERN().key(), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return org.apache.spark.internal.config.package$.MODULE$.SECRET_REDACTION_PATTERN().defaultValueString();
         }
      })))).r();
      return this.redact((Regex)redactionPattern, Predef$.MODULE$.wrapRefArray(kvs.toArray(ClassTag$.MODULE$.apply(Tuple2.class))));
   }

   public Seq<String> stringToSeq(String str) {
      return Predef$.MODULE$.wrapRefArray(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])str.split(",")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$29) {
            return x$29.trim();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$30) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$30))).nonEmpty();
         }
      }));
   }

   public <T> Seq<T> loadExtensions(Class<T> extClass, Seq<String> classes, SparkConf conf) {
      return (Seq)classes.flatMap(new Serializable(extClass, conf) {
         public static final long serialVersionUID = 0L;
         public final Class extClass$1;
         private final SparkConf conf$1;

         public final Iterable<T> apply(String name) {
            Iterable var10000;
            try {
               Class klass = Utils$.MODULE$.classForName(name);
               Predef$.MODULE$.require(this.extClass$1.isAssignableFrom(klass), new Serializable(this, name) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final String name$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " is not a subclass of ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.name$1, this.$outer.extClass$1.getName()}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.name$1 = name$1;
                     }
                  }
               });
               Try var8 = Try$.MODULE$.apply(new Serializable(this, klass) {
                  public static final long serialVersionUID = 0L;
                  private final Class klass$1;

                  public final Constructor<?> apply() {
                     return this.klass$1.getConstructor(SparkConf.class);
                  }

                  public {
                     this.klass$1 = klass$1;
                  }
               });
               Object ext;
               if (var8 instanceof Success) {
                  Success var9 = (Success)var8;
                  Constructor ctor = (Constructor)var9.value();
                  ext = ctor.newInstance(this.conf$1);
               } else {
                  if (!(var8 instanceof Failure)) {
                     throw new MatchError(var8);
                  }

                  ext = klass.getConstructor().newInstance();
               }

               var10000 = Option$.MODULE$.option2Iterable(new Some(ext));
            } catch (NoSuchMethodException var12) {
               throw new SparkException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " did not have a zero-argument constructor or a"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name}))).append((Object)" single-argument constructor that accepts SparkConf. Note: if the class is").append((Object)" defined inside of another Scala class, then its constructors may accept an").append((Object)" implicit parameter that references the enclosing class; in this case, you must").append((Object)" define the class as a top-level class in order to prevent this extra").append((Object)" parameter from breaking Spark's ability to find a valid constructor.").toString());
            } catch (InvocationTargetException var13) {
               Throwable var3 = var13.getCause();
               if (!(var3 instanceof UnsupportedOperationException)) {
                  if (var3 == null) {
                     throw var13;
                  }

                  throw var3;
               }

               UnsupportedOperationException var4 = (UnsupportedOperationException)var3;
               Utils$.MODULE$.logDebug(new Serializable(this, name) {
                  public static final long serialVersionUID = 0L;
                  private final String name$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Extension ", " not being initialized."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.name$1}));
                  }

                  public {
                     this.name$1 = name$1;
                  }
               }, var4);
               Utils$.MODULE$.logInfo(new Serializable(this, name) {
                  public static final long serialVersionUID = 0L;
                  private final String name$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Extension ", " not being initialized."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.name$1}));
                  }

                  public {
                     this.name$1 = name$1;
                  }
               });
               Iterable var5 = Option$.MODULE$.option2Iterable(None$.MODULE$);
               var10000 = var5;
            }

            return var10000;
         }

         public {
            this.extClass$1 = extClass$1;
            this.conf$1 = conf$1;
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public String checkAndGetK8sMasterUrl(String rawMasterURL) {
      Predef$.MODULE$.require(rawMasterURL.startsWith("k8s://"), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Kubernetes master URL must start with k8s://.";
         }
      });
      String masterWithoutK8sPrefix = rawMasterURL.substring("k8s://".length());
      if (masterWithoutK8sPrefix.contains("://")) {
         String masterScheme = (new URI(masterWithoutK8sPrefix)).getScheme();
         String var6 = masterScheme.toLowerCase();
         String resolvedURL;
         if ("https".equals(var6)) {
            resolvedURL = masterWithoutK8sPrefix;
         } else if ("http".equals(var6)) {
            this.logWarning(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Kubernetes master URL uses HTTP instead of HTTPS.";
               }
            });
            resolvedURL = masterWithoutK8sPrefix;
         } else {
            if (var6 != null) {
               throw new IllegalArgumentException((new StringBuilder()).append((Object)"Invalid Kubernetes master scheme: ").append((Object)masterScheme).toString());
            }

            String resolvedURL = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"https://", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{masterWithoutK8sPrefix}));
            this.logInfo(new Serializable(resolvedURL) {
               public static final long serialVersionUID = 0L;
               private final String resolvedURL$2;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"No scheme specified for kubernetes master URL, so defaulting to https. Resolved ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"URL is ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.resolvedURL$2}))).toString();
               }

               public {
                  this.resolvedURL$2 = resolvedURL$2;
               }
            });
            resolvedURL = resolvedURL;
         }

         return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"k8s://", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{resolvedURL}));
      } else {
         String resolvedURL = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"https://", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{masterWithoutK8sPrefix}));
         this.logInfo(new Serializable(resolvedURL) {
            public static final long serialVersionUID = 0L;
            private final String resolvedURL$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"No scheme specified for kubernetes master URL, so defaulting to https. Resolved ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"URL is ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.resolvedURL$1}))).toString();
            }

            public {
               this.resolvedURL$1 = resolvedURL$1;
            }
         });
         return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"k8s://", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{resolvedURL}));
      }
   }

   public final void org$apache$spark$util$Utils$$log$1(String s) {
      this.logInfo(new Serializable(s) {
         public static final long serialVersionUID = 0L;
         private final String s$1;

         public final String apply() {
            return this.s$1;
         }

         public {
            this.s$1 = s$1;
         }
      });
   }

   public final void org$apache$spark$util$Utils$$appendToOutput$1(String s, StringBuilder output$1) {
      output$1.append(s).append("\n");
   }

   private final void endWord$1(ArrayBuffer buf$1, StringBuilder curWord$1) {
      buf$1.$plus$eq(curWord$1.toString());
      curWord$1.clear();
   }

   private Utils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.random = new Random();
      this.sparkUncaughtExceptionHandler = new SparkUncaughtExceptionHandler(SparkUncaughtExceptionHandler$.MODULE$.$lessinit$greater$default$1());
      this.DEFAULT_DRIVER_MEM_MB = (int)1024L;
      this.MAX_DIR_CREATION_ATTEMPTS = 10;
      this.localRootDirs = null;
      this.DEFAULT_MAX_TO_STRING_FIELDS = 25;
      this.truncationWarningPrinted = new AtomicBoolean(false);
      this.customHostname = scala.sys.package$.MODULE$.env().get("SPARK_LOCAL_HOSTNAME");
      this.hostPortParseResults = new ConcurrentHashMap();
      this.UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE_CONF = "spark.worker.ui.compressedLogFileLengthCacheSize";
      this.DEFAULT_UNCOMPRESSED_LOG_FILE_LENGTH_CACHE_SIZE = 100;
      this.compressedLogFileLengthCache = null;
      this.isWindows = SystemUtils.IS_OS_WINDOWS;
      this.isMac = SystemUtils.IS_OS_MAC_OSX;
      this.windowsDrive = (new StringOps(Predef$.MODULE$.augmentString("([a-zA-Z])"))).r();
      this.EMPTY_USER_GROUPS = Predef$.MODULE$.Set().empty();
      this.BACKUP_STANDALONE_MASTER_PREFIX = "Current state is not alive";
      this.REDACTION_REPLACEMENT_TEXT = "*********(redacted)";
   }
}
