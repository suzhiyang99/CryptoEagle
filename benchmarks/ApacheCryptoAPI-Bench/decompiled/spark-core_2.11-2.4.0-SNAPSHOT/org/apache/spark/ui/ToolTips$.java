package org.apache.spark.ui;

public final class ToolTips$ {
   public static final ToolTips$ MODULE$;
   private final String SCHEDULER_DELAY;
   private final String TASK_DESERIALIZATION_TIME;
   private final String SHUFFLE_READ_BLOCKED_TIME;
   private final String INPUT;
   private final String OUTPUT;
   private final String STORAGE_MEMORY;
   private final String SHUFFLE_WRITE;
   private final String SHUFFLE_READ;
   private final String SHUFFLE_READ_REMOTE_SIZE;
   private final String GETTING_RESULT_TIME;
   private final String RESULT_SERIALIZATION_TIME;
   private final String GC_TIME;
   private final String PEAK_EXECUTION_MEMORY;
   private final String JOB_TIMELINE;
   private final String STAGE_TIMELINE;
   private final String JOB_DAG;
   private final String STAGE_DAG;
   private final String TASK_TIME;
   private final String BLACKLISTED;
   private final String APPLICATION_EXECUTOR_LIMIT;

   static {
      new ToolTips$();
   }

   public String SCHEDULER_DELAY() {
      return this.SCHEDULER_DELAY;
   }

   public String TASK_DESERIALIZATION_TIME() {
      return this.TASK_DESERIALIZATION_TIME;
   }

   public String SHUFFLE_READ_BLOCKED_TIME() {
      return this.SHUFFLE_READ_BLOCKED_TIME;
   }

   public String INPUT() {
      return this.INPUT;
   }

   public String OUTPUT() {
      return this.OUTPUT;
   }

   public String STORAGE_MEMORY() {
      return this.STORAGE_MEMORY;
   }

   public String SHUFFLE_WRITE() {
      return this.SHUFFLE_WRITE;
   }

   public String SHUFFLE_READ() {
      return this.SHUFFLE_READ;
   }

   public String SHUFFLE_READ_REMOTE_SIZE() {
      return this.SHUFFLE_READ_REMOTE_SIZE;
   }

   public String GETTING_RESULT_TIME() {
      return this.GETTING_RESULT_TIME;
   }

   public String RESULT_SERIALIZATION_TIME() {
      return this.RESULT_SERIALIZATION_TIME;
   }

   public String GC_TIME() {
      return this.GC_TIME;
   }

   public String PEAK_EXECUTION_MEMORY() {
      return this.PEAK_EXECUTION_MEMORY;
   }

   public String JOB_TIMELINE() {
      return this.JOB_TIMELINE;
   }

   public String STAGE_TIMELINE() {
      return this.STAGE_TIMELINE;
   }

   public String JOB_DAG() {
      return this.JOB_DAG;
   }

   public String STAGE_DAG() {
      return this.STAGE_DAG;
   }

   public String TASK_TIME() {
      return this.TASK_TIME;
   }

   public String BLACKLISTED() {
      return this.BLACKLISTED;
   }

   public String APPLICATION_EXECUTOR_LIMIT() {
      return this.APPLICATION_EXECUTOR_LIMIT;
   }

   private ToolTips$() {
      MODULE$ = this;
      this.SCHEDULER_DELAY = "Scheduler delay includes time to ship the task from the scheduler to\n       the executor, and time to send the task result from the executor to the scheduler. If\n       scheduler delay is large, consider decreasing the size of tasks or decreasing the size\n       of task results.";
      this.TASK_DESERIALIZATION_TIME = "Time spent deserializing the task closure on the executor, including the time to read the\n       broadcasted task.";
      this.SHUFFLE_READ_BLOCKED_TIME = "Time that the task spent blocked waiting for shuffle data to be read from remote machines.";
      this.INPUT = "Bytes and records read from Hadoop or from Spark storage.";
      this.OUTPUT = "Bytes and records written to Hadoop.";
      this.STORAGE_MEMORY = "Memory used / total available memory for storage of data like RDD partitions cached in memory. ";
      this.SHUFFLE_WRITE = "Bytes and records written to disk in order to be read by a shuffle in a future stage.";
      this.SHUFFLE_READ = "Total shuffle bytes and records read (includes both data read locally and data read from\n       remote executors). ";
      this.SHUFFLE_READ_REMOTE_SIZE = "Total shuffle bytes read from remote executors. This is a subset of the shuffle\n       read bytes; the remaining shuffle data is read locally. ";
      this.GETTING_RESULT_TIME = "Time that the driver spends fetching task results from workers. If this is large, consider\n       decreasing the amount of data returned from each task.";
      this.RESULT_SERIALIZATION_TIME = "Time spent serializing the task result on the executor before sending it back to the\n       driver.";
      this.GC_TIME = "Time that the executor spent paused for Java garbage collection while the task was\n       running.";
      this.PEAK_EXECUTION_MEMORY = "Execution memory refers to the memory used by internal data structures created during\n       shuffles, aggregations and joins when Tungsten is enabled. The value of this accumulator\n       should be approximately the sum of the peak sizes across all such data structures created\n       in this task. For SQL jobs, this only tracks all unsafe operators, broadcast joins, and\n       external sort.";
      this.JOB_TIMELINE = "Shows when jobs started and ended and when executors joined or left. Drag to scroll.\n       Click Enable Zooming and use mouse wheel to zoom in/out.";
      this.STAGE_TIMELINE = "Shows when stages started and ended and when executors joined or left. Drag to scroll.\n       Click Enable Zooming and use mouse wheel to zoom in/out.";
      this.JOB_DAG = "Shows a graph of stages executed for this job, each of which can contain\n       multiple RDD operations (e.g. map() and filter()), and of RDDs inside each operation\n       (shown as dots).";
      this.STAGE_DAG = "Shows a graph of RDD operations in this stage, and RDDs inside each one. A stage can run\n       multiple operations (e.g. two map() functions) if they can be pipelined. Some operations\n       also create multiple RDDs internally. Cached RDDs are shown in green.\n    ";
      this.TASK_TIME = "Shaded red when garbage collection (GC) time is over 10% of task time";
      this.BLACKLISTED = "Shows if this executor has been blacklisted by the scheduler due to task failures.";
      this.APPLICATION_EXECUTOR_LIMIT = "Maximum number of executors that this application will use. This limit is finite only when\n       dynamic allocation is enabled. The number of granted executors may exceed the limit\n       ephemerally when executors are being killed.\n    ";
   }
}
