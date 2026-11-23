package org.apache.spark.api.r;

public final class RRunnerModes$ {
   public static final RRunnerModes$ MODULE$;
   private final int RDD;
   private final int DATAFRAME_DAPPLY;
   private final int DATAFRAME_GAPPLY;

   static {
      new RRunnerModes$();
   }

   public int RDD() {
      return this.RDD;
   }

   public int DATAFRAME_DAPPLY() {
      return this.DATAFRAME_DAPPLY;
   }

   public int DATAFRAME_GAPPLY() {
      return this.DATAFRAME_GAPPLY;
   }

   private RRunnerModes$() {
      MODULE$ = this;
      this.RDD = 0;
      this.DATAFRAME_DAPPLY = 1;
      this.DATAFRAME_GAPPLY = 2;
   }
}
