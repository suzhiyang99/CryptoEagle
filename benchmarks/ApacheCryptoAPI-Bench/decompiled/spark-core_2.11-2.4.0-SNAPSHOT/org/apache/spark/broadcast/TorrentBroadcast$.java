package org.apache.spark.broadcast;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import org.apache.spark.SparkEnv$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.io.CompressionCodec;
import org.apache.spark.serializer.DeserializationStream;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.io.ChunkedByteBufferOutputStream;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.reflect.ClassTag;
import scala.runtime.BoxesRunTime;

public final class TorrentBroadcast$ implements Logging, Serializable {
   public static final TorrentBroadcast$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new TorrentBroadcast$();
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

   public <T> ByteBuffer[] blockifyObject(T obj, int blockSize, Serializer serializer, Option<CompressionCodec> compressionCodec, ClassTag<T> evidence$2) {
      ChunkedByteBufferOutputStream cbbos = new ChunkedByteBufferOutputStream(blockSize, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final ByteBuffer apply(int x$1) {
            return ByteBuffer.allocate(x$1);
         }
      });
      OutputStream out = (OutputStream)compressionCodec.map(new Serializable(cbbos) {
         public static final long serialVersionUID = 0L;
         private final ChunkedByteBufferOutputStream cbbos$1;

         public final OutputStream apply(CompressionCodec c) {
            return c.compressedOutputStream(this.cbbos$1);
         }

         public {
            this.cbbos$1 = cbbos$1;
         }
      }).getOrElse(new Serializable(cbbos) {
         public static final long serialVersionUID = 0L;
         private final ChunkedByteBufferOutputStream cbbos$1;

         public final ChunkedByteBufferOutputStream apply() {
            return this.cbbos$1;
         }

         public {
            this.cbbos$1 = cbbos$1;
         }
      });
      SerializerInstance ser = serializer.newInstance();
      SerializationStream serOut = ser.serializeStream(out);
      Utils$.MODULE$.tryWithSafeFinally(new Serializable(obj, evidence$2, serOut) {
         public static final long serialVersionUID = 0L;
         private final Object obj$1;
         private final ClassTag evidence$2$1;
         private final SerializationStream serOut$1;

         public final SerializationStream apply() {
            return this.serOut$1.writeObject(this.obj$1, this.evidence$2$1);
         }

         public {
            this.obj$1 = obj$1;
            this.evidence$2$1 = evidence$2$1;
            this.serOut$1 = serOut$1;
         }
      }, new Serializable(serOut) {
         public static final long serialVersionUID = 0L;
         private final SerializationStream serOut$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.serOut$1.close();
         }

         public {
            this.serOut$1 = serOut$1;
         }
      });
      return cbbos.toChunkedByteBuffer().getChunks();
   }

   public <T> T unBlockifyObject(InputStream[] blocks, Serializer serializer, Option<CompressionCodec> compressionCodec, ClassTag<T> evidence$3) {
      Predef$.MODULE$.require(Predef$.MODULE$.refArrayOps((Object[])blocks).nonEmpty(), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Cannot unblockify an empty array of blocks";
         }
      });
      SequenceInputStream is = new SequenceInputStream(JavaConverters$.MODULE$.asJavaEnumerationConverter(Predef$.MODULE$.refArrayOps((Object[])blocks).iterator()).asJavaEnumeration());
      InputStream in = (InputStream)compressionCodec.map(new Serializable(is) {
         public static final long serialVersionUID = 0L;
         private final SequenceInputStream is$1;

         public final InputStream apply(CompressionCodec c) {
            return c.compressedInputStream(this.is$1);
         }

         public {
            this.is$1 = is$1;
         }
      }).getOrElse(new Serializable(is) {
         public static final long serialVersionUID = 0L;
         private final SequenceInputStream is$1;

         public final SequenceInputStream apply() {
            return this.is$1;
         }

         public {
            this.is$1 = is$1;
         }
      });
      SerializerInstance ser = serializer.newInstance();
      DeserializationStream serIn = ser.deserializeStream(in);
      Object obj = Utils$.MODULE$.tryWithSafeFinally(new Serializable(evidence$3, serIn) {
         public static final long serialVersionUID = 0L;
         private final ClassTag evidence$3$1;
         private final DeserializationStream serIn$1;

         public final T apply() {
            return (T)this.serIn$1.readObject(this.evidence$3$1);
         }

         public {
            this.evidence$3$1 = evidence$3$1;
            this.serIn$1 = serIn$1;
         }
      }, new Serializable(serIn) {
         public static final long serialVersionUID = 0L;
         private final DeserializationStream serIn$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.serIn$1.close();
         }

         public {
            this.serIn$1 = serIn$1;
         }
      });
      return (T)obj;
   }

   public void unpersist(long id, boolean removeFromDriver, boolean blocking) {
      this.logDebug(new Serializable(id) {
         public static final long serialVersionUID = 0L;
         private final long id$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unpersisting TorrentBroadcast ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.id$1)}));
         }

         public {
            this.id$1 = id$1;
         }
      });
      SparkEnv$.MODULE$.get().blockManager().master().removeBroadcast(id, removeFromDriver, blocking);
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TorrentBroadcast$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
