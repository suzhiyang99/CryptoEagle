package org.apache.spark.util.logging;

import java.io.File;
import scala.Array$;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.Seq;
import scala.collection.immutable.StringOps;
import scala.math.Ordering;
import scala.math.Ordering$;
import scala.runtime.BoxesRunTime;

public final class RollingFileAppender$ {
   public static final RollingFileAppender$ MODULE$;
   private final String STRATEGY_PROPERTY;
   private final String STRATEGY_DEFAULT;
   private final String INTERVAL_PROPERTY;
   private final String INTERVAL_DEFAULT;
   private final String SIZE_PROPERTY;
   private final String SIZE_DEFAULT;
   private final String RETAINED_FILES_PROPERTY;
   private final int DEFAULT_BUFFER_SIZE;
   private final String ENABLE_COMPRESSION;
   private final String GZIP_LOG_SUFFIX;

   static {
      new RollingFileAppender$();
   }

   public String STRATEGY_PROPERTY() {
      return this.STRATEGY_PROPERTY;
   }

   public String STRATEGY_DEFAULT() {
      return this.STRATEGY_DEFAULT;
   }

   public String INTERVAL_PROPERTY() {
      return this.INTERVAL_PROPERTY;
   }

   public String INTERVAL_DEFAULT() {
      return this.INTERVAL_DEFAULT;
   }

   public String SIZE_PROPERTY() {
      return this.SIZE_PROPERTY;
   }

   public String SIZE_DEFAULT() {
      return this.SIZE_DEFAULT;
   }

   public String RETAINED_FILES_PROPERTY() {
      return this.RETAINED_FILES_PROPERTY;
   }

   public int DEFAULT_BUFFER_SIZE() {
      return this.DEFAULT_BUFFER_SIZE;
   }

   public String ENABLE_COMPRESSION() {
      return this.ENABLE_COMPRESSION;
   }

   public String GZIP_LOG_SUFFIX() {
      return this.GZIP_LOG_SUFFIX;
   }

   public Seq<File> getSortedRolledOverFiles(String directory, String activeFileName) {
      File[] rolledOverFiles = (File[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])(new File(directory)).getAbsoluteFile().listFiles()).filter(new Serializable(activeFileName) {
         public static final long serialVersionUID = 0L;
         private final String activeFileName$1;

         public final boolean apply(File file) {
            boolean var10000;
            label25: {
               String fileName = file.getName();
               if (fileName.startsWith(this.activeFileName$1)) {
                  String var3 = this.activeFileName$1;
                  if (fileName == null) {
                     if (var3 != null) {
                        break label25;
                     }
                  } else if (!fileName.equals(var3)) {
                     break label25;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }

         public {
            this.activeFileName$1 = activeFileName$1;
         }
      })).sorted(Ordering$.MODULE$.ordered(Predef$.MODULE$.$conforms()));
      File file = (new File(directory, activeFileName)).getAbsoluteFile();
      Option activeFile = (Option)(file.exists() ? new Some(file) : None$.MODULE$);
      return (Seq)Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])rolledOverFiles).sortBy(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(File x$1) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$1.getName()))).stripSuffix(RollingFileAppender$.MODULE$.GZIP_LOG_SUFFIX());
         }
      }, Ordering.String$.MODULE$)).$plus$plus(Option$.MODULE$.option2Iterable(activeFile), Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
   }

   public int $lessinit$greater$default$5() {
      return this.DEFAULT_BUFFER_SIZE();
   }

   private RollingFileAppender$() {
      MODULE$ = this;
      this.STRATEGY_PROPERTY = "spark.executor.logs.rolling.strategy";
      this.STRATEGY_DEFAULT = "";
      this.INTERVAL_PROPERTY = "spark.executor.logs.rolling.time.interval";
      this.INTERVAL_DEFAULT = "daily";
      this.SIZE_PROPERTY = "spark.executor.logs.rolling.maxSize";
      this.SIZE_DEFAULT = BoxesRunTime.boxToInteger(1048576).toString();
      this.RETAINED_FILES_PROPERTY = "spark.executor.logs.rolling.maxRetainedFiles";
      this.DEFAULT_BUFFER_SIZE = 8192;
      this.ENABLE_COMPRESSION = "spark.executor.logs.rolling.enableCompression";
      this.GZIP_LOG_SUFFIX = ".gz";
   }
}
