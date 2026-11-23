package org.apache.spark.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.Properties;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import org.apache.commons.crypto.random.CryptoRandomFactory;
import org.apache.commons.crypto.stream.CryptoInputStream;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.package$;
import org.apache.spark.network.util.CryptoUtils;
import org.apache.spark.network.util.JavaUtils;
import org.slf4j.Logger;
import org.spark_project.guava.io.ByteStreams;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

public final class CryptoStreamUtils$ implements Logging {
   public static final CryptoStreamUtils$ MODULE$;
   private final int IV_LENGTH_IN_BYTES;
   private final String SPARK_IO_ENCRYPTION_COMMONS_CONFIG_PREFIX;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new CryptoStreamUtils$();
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

   public int IV_LENGTH_IN_BYTES() {
      return this.IV_LENGTH_IN_BYTES;
   }

   public String SPARK_IO_ENCRYPTION_COMMONS_CONFIG_PREFIX() {
      return this.SPARK_IO_ENCRYPTION_COMMONS_CONFIG_PREFIX;
   }

   public OutputStream createCryptoOutputStream(OutputStream os, SparkConf sparkConf, byte[] key) {
      CryptoStreamUtils.CryptoParams params = new CryptoStreamUtils.CryptoParams(key, sparkConf);
      byte[] iv = this.createInitializationVector(params.conf());
      os.write(iv);
      return new CryptoOutputStream(params.transformation(), params.conf(), os, params.keySpec(), new IvParameterSpec(iv));
   }

   public WritableByteChannel createWritableChannel(WritableByteChannel channel, SparkConf sparkConf, byte[] key) {
      CryptoStreamUtils.CryptoParams params = new CryptoStreamUtils.CryptoParams(key, sparkConf);
      byte[] iv = this.createInitializationVector(params.conf());
      CryptoStreamUtils.CryptoHelperChannel helper = new CryptoStreamUtils.CryptoHelperChannel(channel);
      helper.write(ByteBuffer.wrap(iv));
      return new CryptoOutputStream(params.transformation(), params.conf(), helper, params.keySpec(), new IvParameterSpec(iv));
   }

   public InputStream createCryptoInputStream(InputStream is, SparkConf sparkConf, byte[] key) {
      byte[] iv = new byte[this.IV_LENGTH_IN_BYTES()];
      ByteStreams.readFully(is, iv);
      CryptoStreamUtils.CryptoParams params = new CryptoStreamUtils.CryptoParams(key, sparkConf);
      return new CryptoInputStream(params.transformation(), params.conf(), is, params.keySpec(), new IvParameterSpec(iv));
   }

   public ReadableByteChannel createReadableChannel(ReadableByteChannel channel, SparkConf sparkConf, byte[] key) {
      byte[] iv = new byte[this.IV_LENGTH_IN_BYTES()];
      ByteBuffer buf = ByteBuffer.wrap(iv);
      JavaUtils.readFully(channel, buf);
      CryptoStreamUtils.CryptoParams params = new CryptoStreamUtils.CryptoParams(key, sparkConf);
      return new CryptoInputStream(params.transformation(), params.conf(), channel, params.keySpec(), new IvParameterSpec(iv));
   }

   public Properties toCryptoConf(SparkConf conf) {
      return CryptoUtils.toCryptoConf(this.SPARK_IO_ENCRYPTION_COMMONS_CONFIG_PREFIX(), ((Map)JavaConverters$.MODULE$.mapAsJavaMapConverter(Predef$.MODULE$.refArrayOps(conf.getAll()).toMap(Predef$.MODULE$.$conforms())).asJava()).entrySet());
   }

   public byte[] createKey(SparkConf conf) {
      int keyLen = BoxesRunTime.unboxToInt(conf.get(package$.MODULE$.IO_ENCRYPTION_KEY_SIZE_BITS()));
      String ioKeyGenAlgorithm = (String)conf.get(package$.MODULE$.IO_ENCRYPTION_KEYGEN_ALGORITHM());
      KeyGenerator keyGen = KeyGenerator.getInstance(ioKeyGenAlgorithm);
      keyGen.init(keyLen);
      return keyGen.generateKey().getEncoded();
   }

   private byte[] createInitializationVector(Properties properties) {
      byte[] iv = new byte[this.IV_LENGTH_IN_BYTES()];
      long initialIVStart = System.currentTimeMillis();
      CryptoRandomFactory.getCryptoRandom(properties).nextBytes(iv);
      long initialIVFinish = System.currentTimeMillis();
      long initialIVTime = initialIVFinish - initialIVStart;
      if (initialIVTime > 2000L) {
         this.logWarning(new Serializable(initialIVTime) {
            public static final long serialVersionUID = 0L;
            private final long initialIVTime$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"It costs ", " milliseconds to create the Initialization Vector "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.initialIVTime$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"used by CryptoStream"})))).s(Nil$.MODULE$)).toString();
            }

            public {
               this.initialIVTime$1 = initialIVTime$1;
            }
         });
      }

      return iv;
   }

   private CryptoStreamUtils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.IV_LENGTH_IN_BYTES = 16;
      this.SPARK_IO_ENCRYPTION_COMMONS_CONFIG_PREFIX = "spark.io.encryption.commons.config.";
   }
}
