package org.apache.spark.internal.io;

import java.lang.reflect.Constructor;
import org.apache.spark.util.Utils$;
import scala.runtime.BoxesRunTime;

public final class FileCommitProtocol$ {
   public static final FileCommitProtocol$ MODULE$;

   static {
      new FileCommitProtocol$();
   }

   public FileCommitProtocol instantiate(String className, String jobId, String outputPath, boolean dynamicPartitionOverwrite) {
      Class clazz = Utils$.MODULE$.classForName(className);

      FileCommitProtocol var10000;
      try {
         Constructor ctor = clazz.getDeclaredConstructor(String.class, String.class, Boolean.TYPE);
         var10000 = (FileCommitProtocol)ctor.newInstance(jobId, outputPath, BoxesRunTime.boxToBoolean(dynamicPartitionOverwrite));
      } catch (NoSuchMethodException var8) {
         Constructor ctor = clazz.getDeclaredConstructor(String.class, String.class);
         var10000 = (FileCommitProtocol)ctor.newInstance(jobId, outputPath);
      }

      return var10000;
   }

   public boolean instantiate$default$4() {
      return false;
   }

   private FileCommitProtocol$() {
      MODULE$ = this;
   }
}
