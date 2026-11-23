package org.apache.spark.internal.io;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.executor.OutputMetrics;
import scala.Function0;
import scala.Tuple2;
import scala.runtime.BoxesRunTime;
import scala.util.DynamicVariable;

public final class SparkHadoopWriterUtils$ {
   public static final SparkHadoopWriterUtils$ MODULE$;
   private final int RECORDS_BETWEEN_BYTES_WRITTEN_METRIC_UPDATES;
   private final DynamicVariable<Object> disableOutputSpecValidation;

   static {
      new SparkHadoopWriterUtils$();
   }

   private int RECORDS_BETWEEN_BYTES_WRITTEN_METRIC_UPDATES() {
      return this.RECORDS_BETWEEN_BYTES_WRITTEN_METRIC_UPDATES;
   }

   public JobID createJobID(Date time, int id) {
      String jobtrackerID = this.createJobTrackerID(time);
      return new JobID(jobtrackerID, id);
   }

   public String createJobTrackerID(Date time) {
      return (new SimpleDateFormat("yyyyMMddHHmmss", Locale.US)).format(time);
   }

   public Path createPathFromString(String path, JobConf conf) {
      if (path == null) {
         throw new IllegalArgumentException("Output path is null");
      } else {
         Path outputPath = new Path(path);
         FileSystem fs = outputPath.getFileSystem(conf);
         if (fs == null) {
            throw new IllegalArgumentException("Incorrectly formatted output path");
         } else {
            return outputPath.makeQualified(fs.getUri(), fs.getWorkingDirectory());
         }
      }
   }

   public boolean isOutputSpecValidationEnabled(SparkConf conf) {
      boolean validationDisabled = BoxesRunTime.unboxToBoolean(this.disableOutputSpecValidation().value());
      boolean enabledInConf = conf.getBoolean("spark.hadoop.validateOutputSpecs", true);
      return enabledInConf && !validationDisabled;
   }

   public Tuple2<OutputMetrics, Function0<Object>> initHadoopOutputMetrics(TaskContext context) {
      Function0 bytesWrittenCallback = SparkHadoopUtil$.MODULE$.get().getFSBytesWrittenOnThreadCallback();
      return new Tuple2<OutputMetrics, Function0<Object>>(context.taskMetrics().outputMetrics(), bytesWrittenCallback);
   }

   public void maybeUpdateOutputMetrics(OutputMetrics outputMetrics, Function0<Object> callback, long recordsWritten) {
      if (recordsWritten % (long)this.RECORDS_BETWEEN_BYTES_WRITTEN_METRIC_UPDATES() == 0L) {
         outputMetrics.setBytesWritten(callback.apply$mcJ$sp());
         outputMetrics.setRecordsWritten(recordsWritten);
      }

   }

   public DynamicVariable<Object> disableOutputSpecValidation() {
      return this.disableOutputSpecValidation;
   }

   private SparkHadoopWriterUtils$() {
      MODULE$ = this;
      this.RECORDS_BETWEEN_BYTES_WRITTEN_METRIC_UPDATES = 256;
      this.disableOutputSpecValidation = new DynamicVariable<Object>(BoxesRunTime.boxToBoolean(false));
   }
}
