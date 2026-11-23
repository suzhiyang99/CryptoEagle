package org.apache.spark.ui.jobs;

public final class TaskDetailsClassNames$ {
   public static final TaskDetailsClassNames$ MODULE$;
   private final String SCHEDULER_DELAY;
   private final String TASK_DESERIALIZATION_TIME;
   private final String SHUFFLE_READ_BLOCKED_TIME;
   private final String SHUFFLE_READ_REMOTE_SIZE;
   private final String RESULT_SERIALIZATION_TIME;
   private final String GETTING_RESULT_TIME;
   private final String PEAK_EXECUTION_MEMORY;

   static {
      new TaskDetailsClassNames$();
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

   public String SHUFFLE_READ_REMOTE_SIZE() {
      return this.SHUFFLE_READ_REMOTE_SIZE;
   }

   public String RESULT_SERIALIZATION_TIME() {
      return this.RESULT_SERIALIZATION_TIME;
   }

   public String GETTING_RESULT_TIME() {
      return this.GETTING_RESULT_TIME;
   }

   public String PEAK_EXECUTION_MEMORY() {
      return this.PEAK_EXECUTION_MEMORY;
   }

   private TaskDetailsClassNames$() {
      MODULE$ = this;
      this.SCHEDULER_DELAY = "scheduler_delay";
      this.TASK_DESERIALIZATION_TIME = "deserialization_time";
      this.SHUFFLE_READ_BLOCKED_TIME = "fetch_wait_time";
      this.SHUFFLE_READ_REMOTE_SIZE = "shuffle_read_remote";
      this.RESULT_SERIALIZATION_TIME = "serialization_time";
      this.GETTING_RESULT_TIME = "getting_result_time";
      this.PEAK_EXECUTION_MEMORY = "peak_execution_memory";
   }
}
