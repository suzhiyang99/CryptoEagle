package org.apache.spark.shuffle.sort;

import org.apache.spark.ShuffleDependency;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

public final class SortShuffleManager$ implements Logging {
   public static final SortShuffleManager$ MODULE$;
   private final int MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SortShuffleManager$();
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

   public int MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE() {
      return this.MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE;
   }

   public boolean canUseSerializedShuffle(ShuffleDependency<?, ?, ?> dependency) {
      int shufId = dependency.shuffleId();
      int numPartitions = dependency.partitioner().numPartitions();
      boolean var10000;
      if (dependency.serializer().supportsRelocationOfSerializedObjects()) {
         if (dependency.aggregator().isDefined()) {
            this.log().debug((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't use serialized shuffle for shuffle ", " because an aggregator is defined"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(shufId)})));
            var10000 = false;
         } else if (numPartitions > this.MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE()) {
            this.log().debug((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't use serialized shuffle for shuffle ", " because it has more than "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(shufId)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " partitions"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE())}))).toString());
            var10000 = false;
         } else {
            this.log().debug((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can use serialized shuffle for shuffle ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(shufId)})));
            var10000 = true;
         }
      } else {
         this.log().debug((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't use serialized shuffle for shuffle ", " because the serializer, "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(shufId)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", does not support object relocation"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{dependency.serializer().getClass().getName()}))).toString());
         var10000 = false;
      }

      return var10000;
   }

   private SortShuffleManager$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.MAX_SHUFFLE_OUTPUT_PARTITIONS_FOR_SERIALIZED_MODE = PackedRecordPointer.MAXIMUM_PARTITION_ID + 1;
   }
}
