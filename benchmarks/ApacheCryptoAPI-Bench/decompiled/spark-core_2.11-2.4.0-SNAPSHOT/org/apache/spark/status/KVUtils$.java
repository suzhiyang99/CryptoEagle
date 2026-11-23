package org.apache.spark.status;

import java.io.File;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.kvstore.KVStoreIterator;
import org.apache.spark.util.kvstore.KVStoreView;
import org.apache.spark.util.kvstore.LevelDB;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.immutable.List;
import scala.reflect.ClassTag;
import scala.reflect.package$;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

public final class KVUtils$ implements Logging {
   public static final KVUtils$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new KVUtils$();
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

   public <M> LevelDB open(File path, M metadata, ClassTag<M> evidence$1) {
      Predef$.MODULE$.require(metadata != null, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Metadata is required.";
         }
      });
      LevelDB db = new LevelDB(path, new KVUtils.KVStoreScalaSerializer());
      Object dbMeta = db.getMetadata(package$.MODULE$.classTag(evidence$1).runtimeClass());
      if (dbMeta == null) {
         db.setMetadata(metadata);
      } else if (!BoxesRunTime.equals(dbMeta, metadata)) {
         db.close();
         throw new KVUtils.MetadataMismatchException();
      }

      return db;
   }

   public <T> Seq<T> viewToSeq(KVStoreView<T> view, int max, Function1<T, Object> filter) {
      KVStoreIterator iter = view.closeableIterator();

      List var10000;
      try {
         var10000 = ((Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(iter).asScala()).filter(filter).take(max).toList();
      } finally {
         iter.close();
      }

      return var10000;
   }

   private KVUtils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
