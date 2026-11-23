package org.apache.spark.internal.config;

import java.util.concurrent.TimeUnit;
import org.apache.spark.network.util.ByteUnit;
import org.apache.spark.util.Utils$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.runtime.BoxesRunTime;
import scala.util.matching.Regex;

public final class package$ {
   public static final package$ MODULE$;
   private final OptionalConfigEntry<String> DRIVER_CLASS_PATH;
   private final OptionalConfigEntry<String> DRIVER_JAVA_OPTIONS;
   private final OptionalConfigEntry<String> DRIVER_LIBRARY_PATH;
   private final ConfigEntry<Object> DRIVER_USER_CLASS_PATH_FIRST;
   private final ConfigEntry<Object> DRIVER_MEMORY;
   private final OptionalConfigEntry<Object> DRIVER_MEMORY_OVERHEAD;
   private final ConfigEntry<Object> EVENT_LOG_COMPRESS;
   private final ConfigEntry<Object> EVENT_LOG_BLOCK_UPDATES;
   private final ConfigEntry<Object> EVENT_LOG_TESTING;
   private final ConfigEntry<Object> EVENT_LOG_OUTPUT_BUFFER_SIZE;
   private final ConfigEntry<Object> EVENT_LOG_OVERWRITE;
   private final OptionalConfigEntry<String> EXECUTOR_CLASS_PATH;
   private final OptionalConfigEntry<String> EXECUTOR_JAVA_OPTIONS;
   private final OptionalConfigEntry<String> EXECUTOR_LIBRARY_PATH;
   private final ConfigEntry<Object> EXECUTOR_USER_CLASS_PATH_FIRST;
   private final ConfigEntry<Object> EXECUTOR_MEMORY;
   private final OptionalConfigEntry<Object> EXECUTOR_MEMORY_OVERHEAD;
   private final ConfigEntry<Object> MEMORY_OFFHEAP_ENABLED;
   private final ConfigEntry<Object> MEMORY_OFFHEAP_SIZE;
   private final ConfigEntry<Object> IS_PYTHON_APP;
   private final ConfigEntry<Object> CPUS_PER_TASK;
   private final ConfigEntry<Object> DYN_ALLOCATION_MIN_EXECUTORS;
   private final ConfigEntry<Object> DYN_ALLOCATION_INITIAL_EXECUTORS;
   private final ConfigEntry<Object> DYN_ALLOCATION_MAX_EXECUTORS;
   private final ConfigEntry<Object> LOCALITY_WAIT;
   private final ConfigEntry<Object> SHUFFLE_SERVICE_ENABLED;
   private final OptionalConfigEntry<String> KEYTAB;
   private final OptionalConfigEntry<String> PRINCIPAL;
   private final OptionalConfigEntry<Object> EXECUTOR_INSTANCES;
   private final ConfigEntry<Seq<String>> PY_FILES;
   private final ConfigEntry<Object> MAX_TASK_FAILURES;
   private final OptionalConfigEntry<Object> BLACKLIST_ENABLED;
   private final ConfigEntry<Object> MAX_TASK_ATTEMPTS_PER_EXECUTOR;
   private final ConfigEntry<Object> MAX_TASK_ATTEMPTS_PER_NODE;
   private final ConfigEntry<Object> MAX_FAILURES_PER_EXEC;
   private final ConfigEntry<Object> MAX_FAILURES_PER_EXEC_STAGE;
   private final ConfigEntry<Object> MAX_FAILED_EXEC_PER_NODE;
   private final ConfigEntry<Object> MAX_FAILED_EXEC_PER_NODE_STAGE;
   private final OptionalConfigEntry<Object> BLACKLIST_TIMEOUT_CONF;
   private final ConfigEntry<Object> BLACKLIST_KILL_ENABLED;
   private final OptionalConfigEntry<Object> BLACKLIST_LEGACY_TIMEOUT_CONF;
   private final ConfigEntry<Object> BLACKLIST_FETCH_FAILURE_ENABLED;
   private final ConfigEntry<Object> UNREGISTER_OUTPUT_ON_HOST_ON_FETCH_FAILURE;
   private final ConfigEntry<Object> LISTENER_BUS_EVENT_QUEUE_CAPACITY;
   private final ConfigEntry<Object> LISTENER_BUS_METRICS_MAX_LISTENER_CLASSES_TIMED;
   private final OptionalConfigEntry<String> METRICS_NAMESPACE;
   private final OptionalConfigEntry<String> PYSPARK_DRIVER_PYTHON;
   private final OptionalConfigEntry<String> PYSPARK_PYTHON;
   private final ConfigEntry<Object> HISTORY_UI_MAX_APPS;
   private final ConfigEntry<Object> UI_SHOW_CONSOLE_PROGRESS;
   private final ConfigEntry<Object> IO_ENCRYPTION_ENABLED;
   private final ConfigEntry<String> IO_ENCRYPTION_KEYGEN_ALGORITHM;
   private final ConfigEntry<Object> IO_ENCRYPTION_KEY_SIZE_BITS;
   private final ConfigEntry<String> IO_CRYPTO_CIPHER_TRANSFORMATION;
   private final ConfigEntry<String> DRIVER_HOST_ADDRESS;
   private final ConfigEntry<String> DRIVER_BIND_ADDRESS;
   private final ConfigEntry<Object> BLOCK_MANAGER_PORT;
   private final ConfigEntry<Object> DRIVER_BLOCK_MANAGER_PORT;
   private final ConfigEntry<Object> IGNORE_CORRUPT_FILES;
   private final OptionalConfigEntry<String> APP_CALLER_CONTEXT;
   private final ConfigEntry<Object> FILES_MAX_PARTITION_BYTES;
   private final ConfigEntry<Object> FILES_OPEN_COST_IN_BYTES;
   private final ConfigEntry<Object> HADOOP_RDD_IGNORE_EMPTY_SPLITS;
   private final ConfigEntry<Regex> SECRET_REDACTION_PATTERN;
   private final OptionalConfigEntry<Regex> STRING_REDACTION_PATTERN;
   private final ConfigEntry<Object> NETWORK_AUTH_ENABLED;
   private final ConfigEntry<Object> SASL_ENCRYPTION_ENABLED;
   private final ConfigEntry<Object> NETWORK_ENCRYPTION_ENABLED;
   private final ConfigEntry<Object> BUFFER_WRITE_CHUNK_SIZE;
   private final ConfigEntry<Object> CHECKPOINT_COMPRESS;
   private final ConfigEntry<Object> SHUFFLE_ACCURATE_BLOCK_THRESHOLD;
   private final ConfigEntry<Object> SHUFFLE_REGISTRATION_TIMEOUT;
   private final ConfigEntry<Object> SHUFFLE_REGISTRATION_MAX_ATTEMPTS;
   private final ConfigEntry<Object> REDUCER_MAX_BLOCKS_IN_FLIGHT_PER_ADDRESS;
   private final ConfigEntry<Object> MAX_REMOTE_BLOCK_SIZE_FETCH_TO_MEM;
   private final ConfigEntry<Object> TASK_METRICS_TRACK_UPDATED_BLOCK_STATUSES;
   private final ConfigEntry<Object> SHUFFLE_FILE_BUFFER_SIZE;
   private final ConfigEntry<Object> SHUFFLE_UNSAFE_FILE_OUTPUT_BUFFER_SIZE;
   private final ConfigEntry<Object> SHUFFLE_DISK_WRITE_BUFFER_SIZE;
   private final ConfigEntry<Object> UNROLL_MEMORY_CHECK_PERIOD;
   private final ConfigEntry<Object> UNROLL_MEMORY_GROWTH_FACTOR;
   private final ConfigEntry<Seq<String>> FORCE_DOWNLOAD_SCHEMES;
   private final ConfigEntry<String> UI_X_XSS_PROTECTION;
   private final ConfigEntry<Object> UI_X_CONTENT_TYPE_OPTIONS;
   private final OptionalConfigEntry<String> UI_STRICT_TRANSPORT_SECURITY;
   private final OptionalConfigEntry<Seq<String>> EXTRA_LISTENERS;
   private final ConfigEntry<Object> SHUFFLE_SPILL_NUM_ELEMENTS_FORCE_SPILL_THRESHOLD;
   private final ConfigEntry<Object> SHUFFLE_MAP_OUTPUT_PARALLEL_AGGREGATION_THRESHOLD;

   static {
      new package$();
   }

   public OptionalConfigEntry<String> DRIVER_CLASS_PATH() {
      return this.DRIVER_CLASS_PATH;
   }

   public OptionalConfigEntry<String> DRIVER_JAVA_OPTIONS() {
      return this.DRIVER_JAVA_OPTIONS;
   }

   public OptionalConfigEntry<String> DRIVER_LIBRARY_PATH() {
      return this.DRIVER_LIBRARY_PATH;
   }

   public ConfigEntry<Object> DRIVER_USER_CLASS_PATH_FIRST() {
      return this.DRIVER_USER_CLASS_PATH_FIRST;
   }

   public ConfigEntry<Object> DRIVER_MEMORY() {
      return this.DRIVER_MEMORY;
   }

   public OptionalConfigEntry<Object> DRIVER_MEMORY_OVERHEAD() {
      return this.DRIVER_MEMORY_OVERHEAD;
   }

   public ConfigEntry<Object> EVENT_LOG_COMPRESS() {
      return this.EVENT_LOG_COMPRESS;
   }

   public ConfigEntry<Object> EVENT_LOG_BLOCK_UPDATES() {
      return this.EVENT_LOG_BLOCK_UPDATES;
   }

   public ConfigEntry<Object> EVENT_LOG_TESTING() {
      return this.EVENT_LOG_TESTING;
   }

   public ConfigEntry<Object> EVENT_LOG_OUTPUT_BUFFER_SIZE() {
      return this.EVENT_LOG_OUTPUT_BUFFER_SIZE;
   }

   public ConfigEntry<Object> EVENT_LOG_OVERWRITE() {
      return this.EVENT_LOG_OVERWRITE;
   }

   public OptionalConfigEntry<String> EXECUTOR_CLASS_PATH() {
      return this.EXECUTOR_CLASS_PATH;
   }

   public OptionalConfigEntry<String> EXECUTOR_JAVA_OPTIONS() {
      return this.EXECUTOR_JAVA_OPTIONS;
   }

   public OptionalConfigEntry<String> EXECUTOR_LIBRARY_PATH() {
      return this.EXECUTOR_LIBRARY_PATH;
   }

   public ConfigEntry<Object> EXECUTOR_USER_CLASS_PATH_FIRST() {
      return this.EXECUTOR_USER_CLASS_PATH_FIRST;
   }

   public ConfigEntry<Object> EXECUTOR_MEMORY() {
      return this.EXECUTOR_MEMORY;
   }

   public OptionalConfigEntry<Object> EXECUTOR_MEMORY_OVERHEAD() {
      return this.EXECUTOR_MEMORY_OVERHEAD;
   }

   public ConfigEntry<Object> MEMORY_OFFHEAP_ENABLED() {
      return this.MEMORY_OFFHEAP_ENABLED;
   }

   public ConfigEntry<Object> MEMORY_OFFHEAP_SIZE() {
      return this.MEMORY_OFFHEAP_SIZE;
   }

   public ConfigEntry<Object> IS_PYTHON_APP() {
      return this.IS_PYTHON_APP;
   }

   public ConfigEntry<Object> CPUS_PER_TASK() {
      return this.CPUS_PER_TASK;
   }

   public ConfigEntry<Object> DYN_ALLOCATION_MIN_EXECUTORS() {
      return this.DYN_ALLOCATION_MIN_EXECUTORS;
   }

   public ConfigEntry<Object> DYN_ALLOCATION_INITIAL_EXECUTORS() {
      return this.DYN_ALLOCATION_INITIAL_EXECUTORS;
   }

   public ConfigEntry<Object> DYN_ALLOCATION_MAX_EXECUTORS() {
      return this.DYN_ALLOCATION_MAX_EXECUTORS;
   }

   public ConfigEntry<Object> LOCALITY_WAIT() {
      return this.LOCALITY_WAIT;
   }

   public ConfigEntry<Object> SHUFFLE_SERVICE_ENABLED() {
      return this.SHUFFLE_SERVICE_ENABLED;
   }

   public OptionalConfigEntry<String> KEYTAB() {
      return this.KEYTAB;
   }

   public OptionalConfigEntry<String> PRINCIPAL() {
      return this.PRINCIPAL;
   }

   public OptionalConfigEntry<Object> EXECUTOR_INSTANCES() {
      return this.EXECUTOR_INSTANCES;
   }

   public ConfigEntry<Seq<String>> PY_FILES() {
      return this.PY_FILES;
   }

   public ConfigEntry<Object> MAX_TASK_FAILURES() {
      return this.MAX_TASK_FAILURES;
   }

   public OptionalConfigEntry<Object> BLACKLIST_ENABLED() {
      return this.BLACKLIST_ENABLED;
   }

   public ConfigEntry<Object> MAX_TASK_ATTEMPTS_PER_EXECUTOR() {
      return this.MAX_TASK_ATTEMPTS_PER_EXECUTOR;
   }

   public ConfigEntry<Object> MAX_TASK_ATTEMPTS_PER_NODE() {
      return this.MAX_TASK_ATTEMPTS_PER_NODE;
   }

   public ConfigEntry<Object> MAX_FAILURES_PER_EXEC() {
      return this.MAX_FAILURES_PER_EXEC;
   }

   public ConfigEntry<Object> MAX_FAILURES_PER_EXEC_STAGE() {
      return this.MAX_FAILURES_PER_EXEC_STAGE;
   }

   public ConfigEntry<Object> MAX_FAILED_EXEC_PER_NODE() {
      return this.MAX_FAILED_EXEC_PER_NODE;
   }

   public ConfigEntry<Object> MAX_FAILED_EXEC_PER_NODE_STAGE() {
      return this.MAX_FAILED_EXEC_PER_NODE_STAGE;
   }

   public OptionalConfigEntry<Object> BLACKLIST_TIMEOUT_CONF() {
      return this.BLACKLIST_TIMEOUT_CONF;
   }

   public ConfigEntry<Object> BLACKLIST_KILL_ENABLED() {
      return this.BLACKLIST_KILL_ENABLED;
   }

   public OptionalConfigEntry<Object> BLACKLIST_LEGACY_TIMEOUT_CONF() {
      return this.BLACKLIST_LEGACY_TIMEOUT_CONF;
   }

   public ConfigEntry<Object> BLACKLIST_FETCH_FAILURE_ENABLED() {
      return this.BLACKLIST_FETCH_FAILURE_ENABLED;
   }

   public ConfigEntry<Object> UNREGISTER_OUTPUT_ON_HOST_ON_FETCH_FAILURE() {
      return this.UNREGISTER_OUTPUT_ON_HOST_ON_FETCH_FAILURE;
   }

   public ConfigEntry<Object> LISTENER_BUS_EVENT_QUEUE_CAPACITY() {
      return this.LISTENER_BUS_EVENT_QUEUE_CAPACITY;
   }

   public ConfigEntry<Object> LISTENER_BUS_METRICS_MAX_LISTENER_CLASSES_TIMED() {
      return this.LISTENER_BUS_METRICS_MAX_LISTENER_CLASSES_TIMED;
   }

   public OptionalConfigEntry<String> METRICS_NAMESPACE() {
      return this.METRICS_NAMESPACE;
   }

   public OptionalConfigEntry<String> PYSPARK_DRIVER_PYTHON() {
      return this.PYSPARK_DRIVER_PYTHON;
   }

   public OptionalConfigEntry<String> PYSPARK_PYTHON() {
      return this.PYSPARK_PYTHON;
   }

   public ConfigEntry<Object> HISTORY_UI_MAX_APPS() {
      return this.HISTORY_UI_MAX_APPS;
   }

   public ConfigEntry<Object> UI_SHOW_CONSOLE_PROGRESS() {
      return this.UI_SHOW_CONSOLE_PROGRESS;
   }

   public ConfigEntry<Object> IO_ENCRYPTION_ENABLED() {
      return this.IO_ENCRYPTION_ENABLED;
   }

   public ConfigEntry<String> IO_ENCRYPTION_KEYGEN_ALGORITHM() {
      return this.IO_ENCRYPTION_KEYGEN_ALGORITHM;
   }

   public ConfigEntry<Object> IO_ENCRYPTION_KEY_SIZE_BITS() {
      return this.IO_ENCRYPTION_KEY_SIZE_BITS;
   }

   public ConfigEntry<String> IO_CRYPTO_CIPHER_TRANSFORMATION() {
      return this.IO_CRYPTO_CIPHER_TRANSFORMATION;
   }

   public ConfigEntry<String> DRIVER_HOST_ADDRESS() {
      return this.DRIVER_HOST_ADDRESS;
   }

   public ConfigEntry<String> DRIVER_BIND_ADDRESS() {
      return this.DRIVER_BIND_ADDRESS;
   }

   public ConfigEntry<Object> BLOCK_MANAGER_PORT() {
      return this.BLOCK_MANAGER_PORT;
   }

   public ConfigEntry<Object> DRIVER_BLOCK_MANAGER_PORT() {
      return this.DRIVER_BLOCK_MANAGER_PORT;
   }

   public ConfigEntry<Object> IGNORE_CORRUPT_FILES() {
      return this.IGNORE_CORRUPT_FILES;
   }

   public OptionalConfigEntry<String> APP_CALLER_CONTEXT() {
      return this.APP_CALLER_CONTEXT;
   }

   public ConfigEntry<Object> FILES_MAX_PARTITION_BYTES() {
      return this.FILES_MAX_PARTITION_BYTES;
   }

   public ConfigEntry<Object> FILES_OPEN_COST_IN_BYTES() {
      return this.FILES_OPEN_COST_IN_BYTES;
   }

   public ConfigEntry<Object> HADOOP_RDD_IGNORE_EMPTY_SPLITS() {
      return this.HADOOP_RDD_IGNORE_EMPTY_SPLITS;
   }

   public ConfigEntry<Regex> SECRET_REDACTION_PATTERN() {
      return this.SECRET_REDACTION_PATTERN;
   }

   public OptionalConfigEntry<Regex> STRING_REDACTION_PATTERN() {
      return this.STRING_REDACTION_PATTERN;
   }

   public ConfigEntry<Object> NETWORK_AUTH_ENABLED() {
      return this.NETWORK_AUTH_ENABLED;
   }

   public ConfigEntry<Object> SASL_ENCRYPTION_ENABLED() {
      return this.SASL_ENCRYPTION_ENABLED;
   }

   public ConfigEntry<Object> NETWORK_ENCRYPTION_ENABLED() {
      return this.NETWORK_ENCRYPTION_ENABLED;
   }

   public ConfigEntry<Object> BUFFER_WRITE_CHUNK_SIZE() {
      return this.BUFFER_WRITE_CHUNK_SIZE;
   }

   public ConfigEntry<Object> CHECKPOINT_COMPRESS() {
      return this.CHECKPOINT_COMPRESS;
   }

   public ConfigEntry<Object> SHUFFLE_ACCURATE_BLOCK_THRESHOLD() {
      return this.SHUFFLE_ACCURATE_BLOCK_THRESHOLD;
   }

   public ConfigEntry<Object> SHUFFLE_REGISTRATION_TIMEOUT() {
      return this.SHUFFLE_REGISTRATION_TIMEOUT;
   }

   public ConfigEntry<Object> SHUFFLE_REGISTRATION_MAX_ATTEMPTS() {
      return this.SHUFFLE_REGISTRATION_MAX_ATTEMPTS;
   }

   public ConfigEntry<Object> REDUCER_MAX_BLOCKS_IN_FLIGHT_PER_ADDRESS() {
      return this.REDUCER_MAX_BLOCKS_IN_FLIGHT_PER_ADDRESS;
   }

   public ConfigEntry<Object> MAX_REMOTE_BLOCK_SIZE_FETCH_TO_MEM() {
      return this.MAX_REMOTE_BLOCK_SIZE_FETCH_TO_MEM;
   }

   public ConfigEntry<Object> TASK_METRICS_TRACK_UPDATED_BLOCK_STATUSES() {
      return this.TASK_METRICS_TRACK_UPDATED_BLOCK_STATUSES;
   }

   public ConfigEntry<Object> SHUFFLE_FILE_BUFFER_SIZE() {
      return this.SHUFFLE_FILE_BUFFER_SIZE;
   }

   public ConfigEntry<Object> SHUFFLE_UNSAFE_FILE_OUTPUT_BUFFER_SIZE() {
      return this.SHUFFLE_UNSAFE_FILE_OUTPUT_BUFFER_SIZE;
   }

   public ConfigEntry<Object> SHUFFLE_DISK_WRITE_BUFFER_SIZE() {
      return this.SHUFFLE_DISK_WRITE_BUFFER_SIZE;
   }

   public ConfigEntry<Object> UNROLL_MEMORY_CHECK_PERIOD() {
      return this.UNROLL_MEMORY_CHECK_PERIOD;
   }

   public ConfigEntry<Object> UNROLL_MEMORY_GROWTH_FACTOR() {
      return this.UNROLL_MEMORY_GROWTH_FACTOR;
   }

   public ConfigEntry<Seq<String>> FORCE_DOWNLOAD_SCHEMES() {
      return this.FORCE_DOWNLOAD_SCHEMES;
   }

   public ConfigEntry<String> UI_X_XSS_PROTECTION() {
      return this.UI_X_XSS_PROTECTION;
   }

   public ConfigEntry<Object> UI_X_CONTENT_TYPE_OPTIONS() {
      return this.UI_X_CONTENT_TYPE_OPTIONS;
   }

   public OptionalConfigEntry<String> UI_STRICT_TRANSPORT_SECURITY() {
      return this.UI_STRICT_TRANSPORT_SECURITY;
   }

   public OptionalConfigEntry<Seq<String>> EXTRA_LISTENERS() {
      return this.EXTRA_LISTENERS;
   }

   public ConfigEntry<Object> SHUFFLE_SPILL_NUM_ELEMENTS_FORCE_SPILL_THRESHOLD() {
      return this.SHUFFLE_SPILL_NUM_ELEMENTS_FORCE_SPILL_THRESHOLD;
   }

   public ConfigEntry<Object> SHUFFLE_MAP_OUTPUT_PARALLEL_AGGREGATION_THRESHOLD() {
      return this.SHUFFLE_MAP_OUTPUT_PARALLEL_AGGREGATION_THRESHOLD;
   }

   private package$() {
      MODULE$ = this;
      this.DRIVER_CLASS_PATH = (new ConfigBuilder("spark.driver.extraClassPath")).stringConf().createOptional();
      this.DRIVER_JAVA_OPTIONS = (new ConfigBuilder("spark.driver.extraJavaOptions")).stringConf().createOptional();
      this.DRIVER_LIBRARY_PATH = (new ConfigBuilder("spark.driver.extraLibraryPath")).stringConf().createOptional();
      this.DRIVER_USER_CLASS_PATH_FIRST = (new ConfigBuilder("spark.driver.userClassPathFirst")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.DRIVER_MEMORY = (new ConfigBuilder("spark.driver.memory")).doc("Amount of memory to use for the driver process, in MiB unless otherwise specified.").bytesConf(ByteUnit.MiB).createWithDefaultString("1g");
      this.DRIVER_MEMORY_OVERHEAD = (new ConfigBuilder("spark.driver.memoryOverhead")).doc("The amount of off-heap memory to be allocated per driver in cluster mode, in MiB unless otherwise specified.").bytesConf(ByteUnit.MiB).createOptional();
      this.EVENT_LOG_COMPRESS = (new ConfigBuilder("spark.eventLog.compress")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.EVENT_LOG_BLOCK_UPDATES = (new ConfigBuilder("spark.eventLog.logBlockUpdates.enabled")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.EVENT_LOG_TESTING = (new ConfigBuilder("spark.eventLog.testing")).internal().booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.EVENT_LOG_OUTPUT_BUFFER_SIZE = (new ConfigBuilder("spark.eventLog.buffer.kb")).doc("Buffer size to use when writing to output streams, in KiB unless otherwise specified.").bytesConf(ByteUnit.KiB).createWithDefaultString("100k");
      this.EVENT_LOG_OVERWRITE = (new ConfigBuilder("spark.eventLog.overwrite")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.EXECUTOR_CLASS_PATH = (new ConfigBuilder("spark.executor.extraClassPath")).stringConf().createOptional();
      this.EXECUTOR_JAVA_OPTIONS = (new ConfigBuilder("spark.executor.extraJavaOptions")).stringConf().createOptional();
      this.EXECUTOR_LIBRARY_PATH = (new ConfigBuilder("spark.executor.extraLibraryPath")).stringConf().createOptional();
      this.EXECUTOR_USER_CLASS_PATH_FIRST = (new ConfigBuilder("spark.executor.userClassPathFirst")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.EXECUTOR_MEMORY = (new ConfigBuilder("spark.executor.memory")).doc("Amount of memory to use per executor process, in MiB unless otherwise specified.").bytesConf(ByteUnit.MiB).createWithDefaultString("1g");
      this.EXECUTOR_MEMORY_OVERHEAD = (new ConfigBuilder("spark.executor.memoryOverhead")).doc("The amount of off-heap memory to be allocated per executor in cluster mode, in MiB unless otherwise specified.").bytesConf(ByteUnit.MiB).createOptional();
      this.MEMORY_OFFHEAP_ENABLED = (new ConfigBuilder("spark.memory.offHeap.enabled")).doc("If true, Spark will attempt to use off-heap memory for certain operations. If off-heap memory use is enabled, then spark.memory.offHeap.size must be positive.").withAlternative("spark.unsafe.offHeap").booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.MEMORY_OFFHEAP_SIZE = (new ConfigBuilder("spark.memory.offHeap.size")).doc("The absolute amount of memory in bytes which can be used for off-heap allocation. This setting has no impact on heap memory usage, so if your executors' total memory consumption must fit within some hard limit then be sure to shrink your JVM heap size accordingly. This must be set to a positive value when spark.memory.offHeap.enabled=true.").bytesConf(ByteUnit.BYTE).checkValue(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(long x$1) {
            return this.apply$mcZJ$sp(x$1);
         }

         public boolean apply$mcZJ$sp(long x$1) {
            return x$1 >= 0L;
         }
      }, "The off-heap memory size must not be negative").createWithDefault(BoxesRunTime.boxToLong(0L));
      this.IS_PYTHON_APP = (new ConfigBuilder("spark.yarn.isPython")).internal().booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.CPUS_PER_TASK = (new ConfigBuilder("spark.task.cpus")).intConf().createWithDefault(BoxesRunTime.boxToInteger(1));
      this.DYN_ALLOCATION_MIN_EXECUTORS = (new ConfigBuilder("spark.dynamicAllocation.minExecutors")).intConf().createWithDefault(BoxesRunTime.boxToInteger(0));
      this.DYN_ALLOCATION_INITIAL_EXECUTORS = (new ConfigBuilder("spark.dynamicAllocation.initialExecutors")).<Object>fallbackConf(this.DYN_ALLOCATION_MIN_EXECUTORS());
      this.DYN_ALLOCATION_MAX_EXECUTORS = (new ConfigBuilder("spark.dynamicAllocation.maxExecutors")).intConf().createWithDefault(BoxesRunTime.boxToInteger(Integer.MAX_VALUE));
      this.LOCALITY_WAIT = (new ConfigBuilder("spark.locality.wait")).timeConf(TimeUnit.MILLISECONDS).createWithDefaultString("3s");
      this.SHUFFLE_SERVICE_ENABLED = (new ConfigBuilder("spark.shuffle.service.enabled")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.KEYTAB = (new ConfigBuilder("spark.yarn.keytab")).doc("Location of user's keytab.").stringConf().createOptional();
      this.PRINCIPAL = (new ConfigBuilder("spark.yarn.principal")).doc("Name of the Kerberos principal.").stringConf().createOptional();
      this.EXECUTOR_INSTANCES = (new ConfigBuilder("spark.executor.instances")).intConf().createOptional();
      this.PY_FILES = (new ConfigBuilder("spark.yarn.dist.pyFiles")).internal().stringConf().toSequence().createWithDefault(Nil$.MODULE$);
      this.MAX_TASK_FAILURES = (new ConfigBuilder("spark.task.maxFailures")).intConf().createWithDefault(BoxesRunTime.boxToInteger(4));
      this.BLACKLIST_ENABLED = (new ConfigBuilder("spark.blacklist.enabled")).booleanConf().createOptional();
      this.MAX_TASK_ATTEMPTS_PER_EXECUTOR = (new ConfigBuilder("spark.blacklist.task.maxTaskAttemptsPerExecutor")).intConf().createWithDefault(BoxesRunTime.boxToInteger(1));
      this.MAX_TASK_ATTEMPTS_PER_NODE = (new ConfigBuilder("spark.blacklist.task.maxTaskAttemptsPerNode")).intConf().createWithDefault(BoxesRunTime.boxToInteger(2));
      this.MAX_FAILURES_PER_EXEC = (new ConfigBuilder("spark.blacklist.application.maxFailedTasksPerExecutor")).intConf().createWithDefault(BoxesRunTime.boxToInteger(2));
      this.MAX_FAILURES_PER_EXEC_STAGE = (new ConfigBuilder("spark.blacklist.stage.maxFailedTasksPerExecutor")).intConf().createWithDefault(BoxesRunTime.boxToInteger(2));
      this.MAX_FAILED_EXEC_PER_NODE = (new ConfigBuilder("spark.blacklist.application.maxFailedExecutorsPerNode")).intConf().createWithDefault(BoxesRunTime.boxToInteger(2));
      this.MAX_FAILED_EXEC_PER_NODE_STAGE = (new ConfigBuilder("spark.blacklist.stage.maxFailedExecutorsPerNode")).intConf().createWithDefault(BoxesRunTime.boxToInteger(2));
      this.BLACKLIST_TIMEOUT_CONF = (new ConfigBuilder("spark.blacklist.timeout")).timeConf(TimeUnit.MILLISECONDS).createOptional();
      this.BLACKLIST_KILL_ENABLED = (new ConfigBuilder("spark.blacklist.killBlacklistedExecutors")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.BLACKLIST_LEGACY_TIMEOUT_CONF = (new ConfigBuilder("spark.scheduler.executorTaskBlacklistTime")).internal().timeConf(TimeUnit.MILLISECONDS).createOptional();
      this.BLACKLIST_FETCH_FAILURE_ENABLED = (new ConfigBuilder("spark.blacklist.application.fetchFailure.enabled")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.UNREGISTER_OUTPUT_ON_HOST_ON_FETCH_FAILURE = (new ConfigBuilder("spark.files.fetchFailure.unRegisterOutputOnHost")).doc("Whether to un-register all the outputs on the host in condition that we receive  a FetchFailure. This is set default to false, which means, we only un-register the  outputs related to the exact executor(instead of the host) on a FetchFailure.").booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.LISTENER_BUS_EVENT_QUEUE_CAPACITY = (new ConfigBuilder("spark.scheduler.listenerbus.eventqueue.capacity")).intConf().checkValue(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(int x$2) {
            return this.apply$mcZI$sp(x$2);
         }

         public boolean apply$mcZI$sp(int x$2) {
            return x$2 > 0;
         }
      }, "The capacity of listener bus event queue must not be negative").createWithDefault(BoxesRunTime.boxToInteger(10000));
      this.LISTENER_BUS_METRICS_MAX_LISTENER_CLASSES_TIMED = (new ConfigBuilder("spark.scheduler.listenerbus.metrics.maxListenerClassesTimed")).internal().intConf().createWithDefault(BoxesRunTime.boxToInteger(128));
      this.METRICS_NAMESPACE = (new ConfigBuilder("spark.metrics.namespace")).stringConf().createOptional();
      this.PYSPARK_DRIVER_PYTHON = (new ConfigBuilder("spark.pyspark.driver.python")).stringConf().createOptional();
      this.PYSPARK_PYTHON = (new ConfigBuilder("spark.pyspark.python")).stringConf().createOptional();
      this.HISTORY_UI_MAX_APPS = (new ConfigBuilder("spark.history.ui.maxApplications")).intConf().createWithDefault(BoxesRunTime.boxToInteger(Integer.MAX_VALUE));
      this.UI_SHOW_CONSOLE_PROGRESS = (new ConfigBuilder("spark.ui.showConsoleProgress")).doc("When true, show the progress bar in the console.").booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.IO_ENCRYPTION_ENABLED = (new ConfigBuilder("spark.io.encryption.enabled")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.IO_ENCRYPTION_KEYGEN_ALGORITHM = (new ConfigBuilder("spark.io.encryption.keygen.algorithm")).stringConf().createWithDefault("HmacSHA1");
      this.IO_ENCRYPTION_KEY_SIZE_BITS = (new ConfigBuilder("spark.io.encryption.keySizeBits")).intConf().checkValues((Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapIntArray(new int[]{128, 192, 256}))).createWithDefault(BoxesRunTime.boxToInteger(128));
      this.IO_CRYPTO_CIPHER_TRANSFORMATION = (new ConfigBuilder("spark.io.crypto.cipher.transformation")).internal().stringConf().createWithDefaultString("AES/CTR/NoPadding");
      this.DRIVER_HOST_ADDRESS = (new ConfigBuilder("spark.driver.host")).doc("Address of driver endpoints.").stringConf().createWithDefault(Utils$.MODULE$.localCanonicalHostName());
      this.DRIVER_BIND_ADDRESS = (new ConfigBuilder("spark.driver.bindAddress")).doc("Address where to bind network listen sockets on the driver.").<String>fallbackConf(this.DRIVER_HOST_ADDRESS());
      this.BLOCK_MANAGER_PORT = (new ConfigBuilder("spark.blockManager.port")).doc("Port to use for the block manager when a more specific setting is not provided.").intConf().createWithDefault(BoxesRunTime.boxToInteger(0));
      this.DRIVER_BLOCK_MANAGER_PORT = (new ConfigBuilder("spark.driver.blockManager.port")).doc("Port to use for the block manager on the driver.").<Object>fallbackConf(this.BLOCK_MANAGER_PORT());
      this.IGNORE_CORRUPT_FILES = (new ConfigBuilder("spark.files.ignoreCorruptFiles")).doc("Whether to ignore corrupt files. If true, the Spark jobs will continue to run when encountering corrupted or non-existing files and contents that have been read will still be returned.").booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.APP_CALLER_CONTEXT = (new ConfigBuilder("spark.log.callerContext")).stringConf().createOptional();
      this.FILES_MAX_PARTITION_BYTES = (new ConfigBuilder("spark.files.maxPartitionBytes")).doc("The maximum number of bytes to pack into a single partition when reading files.").longConf().createWithDefault(BoxesRunTime.boxToLong(134217728L));
      this.FILES_OPEN_COST_IN_BYTES = (new ConfigBuilder("spark.files.openCostInBytes")).doc("The estimated cost to open a file, measured by the number of bytes could be scanned in the same time. This is used when putting multiple files into a partition. It's better to over estimate, then the partitions with small files will be faster than partitions with bigger files.").longConf().createWithDefault(BoxesRunTime.boxToLong(4194304L));
      this.HADOOP_RDD_IGNORE_EMPTY_SPLITS = (new ConfigBuilder("spark.hadoopRDD.ignoreEmptySplits")).internal().doc("When true, HadoopRDD/NewHadoopRDD will not create partitions for empty input splits.").booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.SECRET_REDACTION_PATTERN = (new ConfigBuilder("spark.redaction.regex")).doc("Regex to decide which Spark configuration properties and environment variables in driver and executor environments contain sensitive information. When this regex matches a property key or value, the value is redacted from the environment UI and various logs like YARN and event logs.").regexConf().createWithDefault((new StringOps(Predef$.MODULE$.augmentString("(?i)secret|password|url|user|username"))).r());
      this.STRING_REDACTION_PATTERN = (new ConfigBuilder("spark.redaction.string.regex")).doc("Regex to decide which parts of strings produced by Spark contain sensitive information. When this regex matches a string part, that string part is replaced by a dummy value. This is currently used to redact the output of SQL explain commands.").regexConf().createOptional();
      this.NETWORK_AUTH_ENABLED = (new ConfigBuilder("spark.authenticate")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.SASL_ENCRYPTION_ENABLED = (new ConfigBuilder("spark.authenticate.enableSaslEncryption")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.NETWORK_ENCRYPTION_ENABLED = (new ConfigBuilder("spark.network.crypto.enabled")).booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.BUFFER_WRITE_CHUNK_SIZE = (new ConfigBuilder("spark.buffer.write.chunkSize")).internal().doc("The chunk size in bytes during writing out the bytes of ChunkedByteBuffer.").bytesConf(ByteUnit.BYTE).checkValue(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(long x$3) {
            return this.apply$mcZJ$sp(x$3);
         }

         public boolean apply$mcZJ$sp(long x$3) {
            return x$3 <= 2147483647L;
         }
      }, "The chunk size during writing out the bytes of ChunkedByteBuffer should not larger than Int.MaxValue.").createWithDefault(BoxesRunTime.boxToLong(67108864L));
      this.CHECKPOINT_COMPRESS = (new ConfigBuilder("spark.checkpoint.compress")).doc("Whether to compress RDD checkpoints. Generally a good idea. Compression will use spark.io.compression.codec.").booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.SHUFFLE_ACCURATE_BLOCK_THRESHOLD = (new ConfigBuilder("spark.shuffle.accurateBlockThreshold")).doc("Threshold in bytes above which the size of shuffle blocks in HighlyCompressedMapStatus is accurately recorded. This helps to prevent OOM by avoiding underestimating shuffle block size when fetch shuffle blocks.").bytesConf(ByteUnit.BYTE).createWithDefault(BoxesRunTime.boxToLong(104857600L));
      this.SHUFFLE_REGISTRATION_TIMEOUT = (new ConfigBuilder("spark.shuffle.registration.timeout")).doc("Timeout in milliseconds for registration to the external shuffle service.").timeConf(TimeUnit.MILLISECONDS).createWithDefault(BoxesRunTime.boxToLong(5000L));
      this.SHUFFLE_REGISTRATION_MAX_ATTEMPTS = (new ConfigBuilder("spark.shuffle.registration.maxAttempts")).doc("When we fail to register to the external shuffle service, we will retry for maxAttempts times.").intConf().createWithDefault(BoxesRunTime.boxToInteger(3));
      this.REDUCER_MAX_BLOCKS_IN_FLIGHT_PER_ADDRESS = (new ConfigBuilder("spark.reducer.maxBlocksInFlightPerAddress")).doc("This configuration limits the number of remote blocks being fetched per reduce task from a given host port. When a large number of blocks are being requested from a given address in a single fetch or simultaneously, this could crash the serving executor or Node Manager. This is especially useful to reduce the load on the Node Manager when external shuffle is enabled. You can mitigate the issue by setting it to a lower value.").intConf().checkValue(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(int x$4) {
            return this.apply$mcZI$sp(x$4);
         }

         public boolean apply$mcZI$sp(int x$4) {
            return x$4 > 0;
         }
      }, "The max no. of blocks in flight cannot be non-positive.").createWithDefault(BoxesRunTime.boxToInteger(Integer.MAX_VALUE));
      this.MAX_REMOTE_BLOCK_SIZE_FETCH_TO_MEM = (new ConfigBuilder("spark.maxRemoteBlockSizeFetchToMem")).doc("Remote block will be fetched to disk when size of the block is above this threshold in bytes. This is to avoid a giant request takes too much memory. We can enable this config by setting a specific value(e.g. 200m). Note this configuration will affect both shuffle fetch and block manager remote block fetch. For users who enabled external shuffle service, this feature can only be worked when external shuffleservice is newer than Spark 2.2.").bytesConf(ByteUnit.BYTE).createWithDefault(BoxesRunTime.boxToLong(Long.MAX_VALUE));
      this.TASK_METRICS_TRACK_UPDATED_BLOCK_STATUSES = (new ConfigBuilder("spark.taskMetrics.trackUpdatedBlockStatuses")).doc("Enable tracking of updatedBlockStatuses in the TaskMetrics. Off by default since tracking the block statuses can use a lot of memory and its not used anywhere within spark.").booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(false));
      this.SHUFFLE_FILE_BUFFER_SIZE = (new ConfigBuilder("spark.shuffle.file.buffer")).doc("Size of the in-memory buffer for each shuffle file output stream, in KiB unless otherwise specified. These buffers reduce the number of disk seeks and system calls made in creating intermediate shuffle files.").bytesConf(ByteUnit.KiB).checkValue(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(long v) {
            return this.apply$mcZJ$sp(v);
         }

         public boolean apply$mcZJ$sp(long v) {
            return v > 0L && v <= 2097151L;
         }
      }, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The file buffer size must be greater than 0 and less than ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(2097151)}))).createWithDefaultString("32k");
      this.SHUFFLE_UNSAFE_FILE_OUTPUT_BUFFER_SIZE = (new ConfigBuilder("spark.shuffle.unsafe.file.output.buffer")).doc("The file system for this buffer size after each partition is written in unsafe shuffle writer. In KiB unless otherwise specified.").bytesConf(ByteUnit.KiB).checkValue(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(long v) {
            return this.apply$mcZJ$sp(v);
         }

         public boolean apply$mcZJ$sp(long v) {
            return v > 0L && v <= 2097151L;
         }
      }, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The buffer size must be greater than 0 and less than ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(2097151)}))).createWithDefaultString("32k");
      this.SHUFFLE_DISK_WRITE_BUFFER_SIZE = (new ConfigBuilder("spark.shuffle.spill.diskWriteBufferSize")).doc("The buffer size, in bytes, to use when writing the sorted records to an on-disk file.").bytesConf(ByteUnit.BYTE).checkValue(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(long v) {
            return this.apply$mcZJ$sp(v);
         }

         public boolean apply$mcZJ$sp(long v) {
            return v > 0L && v <= 2147483647L;
         }
      }, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The buffer size must be greater than 0 and less than ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(Integer.MAX_VALUE)}))).createWithDefault(BoxesRunTime.boxToLong(1048576L));
      this.UNROLL_MEMORY_CHECK_PERIOD = (new ConfigBuilder("spark.storage.unrollMemoryCheckPeriod")).internal().doc("The memory check period is used to determine how often we should check whether there is a need to request more memory when we try to unroll the given block in memory.").longConf().createWithDefault(BoxesRunTime.boxToLong(16L));
      this.UNROLL_MEMORY_GROWTH_FACTOR = (new ConfigBuilder("spark.storage.unrollMemoryGrowthFactor")).internal().doc("Memory to request as a multiple of the size that used to unroll the block.").doubleConf().createWithDefault(BoxesRunTime.boxToDouble((double)1.5F));
      this.FORCE_DOWNLOAD_SCHEMES = (new ConfigBuilder("spark.yarn.dist.forceDownloadSchemes")).doc("Comma-separated list of schemes for which files will be downloaded to the local disk prior to being added to YARN's distributed cache. For use in cases where the YARN service does not support schemes that are supported by Spark, like http, https and ftp.").stringConf().toSequence().createWithDefault(Nil$.MODULE$);
      this.UI_X_XSS_PROTECTION = (new ConfigBuilder("spark.ui.xXssProtection")).doc("Value for HTTP X-XSS-Protection response header").stringConf().createWithDefaultString("1; mode=block");
      this.UI_X_CONTENT_TYPE_OPTIONS = (new ConfigBuilder("spark.ui.xContentTypeOptions.enabled")).doc("Set to 'true' for setting X-Content-Type-Options HTTP response header to 'nosniff'").booleanConf().createWithDefault(BoxesRunTime.boxToBoolean(true));
      this.UI_STRICT_TRANSPORT_SECURITY = (new ConfigBuilder("spark.ui.strictTransportSecurity")).doc("Value for HTTP Strict Transport Security Response Header").stringConf().createOptional();
      this.EXTRA_LISTENERS = (new ConfigBuilder("spark.extraListeners")).doc("Class names of listeners to add to SparkContext during initialization.").stringConf().toSequence().createOptional();
      this.SHUFFLE_SPILL_NUM_ELEMENTS_FORCE_SPILL_THRESHOLD = (new ConfigBuilder("spark.shuffle.spill.numElementsForceSpillThreshold")).internal().doc("The maximum number of elements in memory before forcing the shuffle sorter to spill. By default it's Integer.MAX_VALUE, which means we never force the sorter to spill, until we reach some limitations, like the max page size limitation for the pointer array in the sorter.").intConf().createWithDefault(BoxesRunTime.boxToInteger(Integer.MAX_VALUE));
      this.SHUFFLE_MAP_OUTPUT_PARALLEL_AGGREGATION_THRESHOLD = (new ConfigBuilder("spark.shuffle.mapOutput.parallelAggregationThreshold")).internal().doc("Multi-thread is used when the number of mappers * shuffle partitions is greater than or equal to this threshold. Note that the actual parallelism is calculated by number of mappers * shuffle partitions / this threshold + 1, so this threshold should be positive.").intConf().checkValue(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(int v) {
            return this.apply$mcZI$sp(v);
         }

         public boolean apply$mcZI$sp(int v) {
            return v > 0;
         }
      }, "The threshold should be positive.").createWithDefault(BoxesRunTime.boxToInteger(10000000));
   }
}
