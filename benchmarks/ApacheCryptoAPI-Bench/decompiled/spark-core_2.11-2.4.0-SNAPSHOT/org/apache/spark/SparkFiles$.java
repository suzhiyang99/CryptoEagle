package org.apache.spark;

import java.io.File;
import scala.Serializable;

public final class SparkFiles$ {
   public static final SparkFiles$ MODULE$;

   static {
      new SparkFiles$();
   }

   public String get(String filename) {
      return (new File(this.getRootDirectory(), filename)).getAbsolutePath();
   }

   public String getRootDirectory() {
      return (String)SparkEnv$.MODULE$.get().driverTmpDir().getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return ".";
         }
      });
   }

   private SparkFiles$() {
      MODULE$ = this;
   }
}
