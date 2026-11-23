package org.apache.spark.deploy.worker;

import scala.collection.JavaConverters$;
import scala.collection.Seq;

public final class ProcessBuilderLike$ {
   public static final ProcessBuilderLike$ MODULE$;

   static {
      new ProcessBuilderLike$();
   }

   public ProcessBuilderLike apply(ProcessBuilder processBuilder) {
      return new ProcessBuilderLike(processBuilder) {
         private final ProcessBuilder processBuilder$1;

         public Process start() {
            return this.processBuilder$1.start();
         }

         public Seq<String> command() {
            return (Seq)JavaConverters$.MODULE$.asScalaBufferConverter(this.processBuilder$1.command()).asScala();
         }

         public {
            this.processBuilder$1 = processBuilder$1;
         }
      };
   }

   private ProcessBuilderLike$() {
      MODULE$ = this;
   }
}
