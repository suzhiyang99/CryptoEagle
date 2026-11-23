package org.apache.spark.api.python;

public final class SpecialLengths$ {
   public static final SpecialLengths$ MODULE$;
   private final int END_OF_DATA_SECTION;
   private final int PYTHON_EXCEPTION_THROWN;
   private final int TIMING_DATA;
   private final int END_OF_STREAM;
   private final int NULL;
   private final int START_ARROW_STREAM;

   static {
      new SpecialLengths$();
   }

   public int END_OF_DATA_SECTION() {
      return this.END_OF_DATA_SECTION;
   }

   public int PYTHON_EXCEPTION_THROWN() {
      return this.PYTHON_EXCEPTION_THROWN;
   }

   public int TIMING_DATA() {
      return this.TIMING_DATA;
   }

   public int END_OF_STREAM() {
      return this.END_OF_STREAM;
   }

   public int NULL() {
      return this.NULL;
   }

   public int START_ARROW_STREAM() {
      return this.START_ARROW_STREAM;
   }

   private SpecialLengths$() {
      MODULE$ = this;
      this.END_OF_DATA_SECTION = -1;
      this.PYTHON_EXCEPTION_THROWN = -2;
      this.TIMING_DATA = -3;
      this.END_OF_STREAM = -4;
      this.NULL = -5;
      this.START_ARROW_STREAM = -6;
   }
}
