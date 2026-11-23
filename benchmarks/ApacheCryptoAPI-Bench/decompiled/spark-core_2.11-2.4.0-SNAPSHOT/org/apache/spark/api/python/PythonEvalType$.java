package org.apache.spark.api.python;

import scala.MatchError;
import scala.runtime.BoxesRunTime;

public final class PythonEvalType$ {
   public static final PythonEvalType$ MODULE$;
   private final int NON_UDF;
   private final int SQL_BATCHED_UDF;
   private final int SQL_SCALAR_PANDAS_UDF;
   private final int SQL_GROUPED_MAP_PANDAS_UDF;
   private final int SQL_GROUPED_AGG_PANDAS_UDF;

   static {
      new PythonEvalType$();
   }

   public int NON_UDF() {
      return this.NON_UDF;
   }

   public int SQL_BATCHED_UDF() {
      return this.SQL_BATCHED_UDF;
   }

   public int SQL_SCALAR_PANDAS_UDF() {
      return this.SQL_SCALAR_PANDAS_UDF;
   }

   public int SQL_GROUPED_MAP_PANDAS_UDF() {
      return this.SQL_GROUPED_MAP_PANDAS_UDF;
   }

   public int SQL_GROUPED_AGG_PANDAS_UDF() {
      return this.SQL_GROUPED_AGG_PANDAS_UDF;
   }

   public String toString(int pythonEvalType) {
      String var3;
      if (this.NON_UDF() == pythonEvalType) {
         var3 = "NON_UDF";
      } else if (this.SQL_BATCHED_UDF() == pythonEvalType) {
         var3 = "SQL_BATCHED_UDF";
      } else if (this.SQL_SCALAR_PANDAS_UDF() == pythonEvalType) {
         var3 = "SQL_SCALAR_PANDAS_UDF";
      } else if (this.SQL_GROUPED_MAP_PANDAS_UDF() == pythonEvalType) {
         var3 = "SQL_GROUPED_MAP_PANDAS_UDF";
      } else {
         if (this.SQL_GROUPED_AGG_PANDAS_UDF() != pythonEvalType) {
            throw new MatchError(BoxesRunTime.boxToInteger(pythonEvalType));
         }

         var3 = "SQL_GROUPED_AGG_PANDAS_UDF";
      }

      return var3;
   }

   private PythonEvalType$() {
      MODULE$ = this;
      this.NON_UDF = 0;
      this.SQL_BATCHED_UDF = 100;
      this.SQL_SCALAR_PANDAS_UDF = 200;
      this.SQL_GROUPED_MAP_PANDAS_UDF = 201;
      this.SQL_GROUPED_AGG_PANDAS_UDF = 202;
   }
}
