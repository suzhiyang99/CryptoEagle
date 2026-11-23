package org.apache.spark.input;

import org.apache.hadoop.mapreduce.JobContext;
import scala.Predef$;
import scala.collection.immutable.StringOps;

public final class FixedLengthBinaryInputFormat$ {
   public static final FixedLengthBinaryInputFormat$ MODULE$;
   private final String RECORD_LENGTH_PROPERTY;

   static {
      new FixedLengthBinaryInputFormat$();
   }

   public String RECORD_LENGTH_PROPERTY() {
      return this.RECORD_LENGTH_PROPERTY;
   }

   public int getRecordLength(JobContext context) {
      return (new StringOps(Predef$.MODULE$.augmentString(context.getConfiguration().get(this.RECORD_LENGTH_PROPERTY())))).toInt();
   }

   private FixedLengthBinaryInputFormat$() {
      MODULE$ = this;
      this.RECORD_LENGTH_PROPERTY = "org.apache.spark.input.FixedLengthBinaryInputFormat.recordLength";
   }
}
