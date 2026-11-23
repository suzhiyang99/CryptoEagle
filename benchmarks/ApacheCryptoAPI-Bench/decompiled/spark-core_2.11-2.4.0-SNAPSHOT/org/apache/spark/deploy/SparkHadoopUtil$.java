package org.apache.spark.deploy;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.runtime.BoxedUnit;

public final class SparkHadoopUtil$ {
   public static final SparkHadoopUtil$ MODULE$;
   private SparkHadoopUtil instance;
   private final String SPARK_YARN_CREDS_TEMP_EXTENSION;
   private final String SPARK_YARN_CREDS_COUNTER_DELIM;
   private final int UPDATE_INPUT_METRICS_INTERVAL_RECORDS;
   private volatile boolean bitmap$0;

   static {
      new SparkHadoopUtil$();
   }

   private SparkHadoopUtil instance$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.instance = new SparkHadoopUtil();
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.instance;
   }

   private SparkHadoopUtil instance() {
      return this.bitmap$0 ? this.instance : this.instance$lzycompute();
   }

   public String SPARK_YARN_CREDS_TEMP_EXTENSION() {
      return this.SPARK_YARN_CREDS_TEMP_EXTENSION;
   }

   public String SPARK_YARN_CREDS_COUNTER_DELIM() {
      return this.SPARK_YARN_CREDS_COUNTER_DELIM;
   }

   public int UPDATE_INPUT_METRICS_INTERVAL_RECORDS() {
      return this.UPDATE_INPUT_METRICS_INTERVAL_RECORDS;
   }

   public SparkHadoopUtil get() {
      return this.instance();
   }

   public long getDateOfNextUpdate(long expirationDate, double fraction) {
      long ct = System.currentTimeMillis();
      return (long)((double)ct + fraction * (double)(expirationDate - ct));
   }

   public Configuration newConfiguration(SparkConf conf) {
      Configuration hadoopConf = new Configuration();
      this.org$apache$spark$deploy$SparkHadoopUtil$$appendS3AndSparkHadoopConfigurations(conf, hadoopConf);
      return hadoopConf;
   }

   public void org$apache$spark$deploy$SparkHadoopUtil$$appendS3AndSparkHadoopConfigurations(SparkConf conf, Configuration hadoopConf) {
      if (conf != null) {
         String keyId = System.getenv("AWS_ACCESS_KEY_ID");
         String accessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
         if (keyId != null && accessKey != null) {
            hadoopConf.set("fs.s3.awsAccessKeyId", keyId);
            hadoopConf.set("fs.s3n.awsAccessKeyId", keyId);
            hadoopConf.set("fs.s3a.access.key", keyId);
            hadoopConf.set("fs.s3.awsSecretAccessKey", accessKey);
            hadoopConf.set("fs.s3n.awsSecretAccessKey", accessKey);
            hadoopConf.set("fs.s3a.secret.key", accessKey);
            String sessionToken = System.getenv("AWS_SESSION_TOKEN");
            if (sessionToken != null) {
               hadoopConf.set("fs.s3a.session.token", sessionToken);
            }
         }

         this.org$apache$spark$deploy$SparkHadoopUtil$$appendSparkHadoopConfigs(conf, hadoopConf);
         String bufferSize = conf.get("spark.buffer.size", "65536");
         hadoopConf.set("io.file.buffer.size", bufferSize);
      }

   }

   public void org$apache$spark$deploy$SparkHadoopUtil$$appendSparkHadoopConfigs(SparkConf conf, Configuration hadoopConf) {
      Predef$.MODULE$.refArrayOps(conf.getAll()).withFilter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> check$ifrefutable$2) {
            boolean var3;
            if (check$ifrefutable$2 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).withFilter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> x$11) {
            if (x$11 != null) {
               String key = (String)x$11._1();
               boolean var4 = key.startsWith("spark.hadoop.");
               return var4;
            } else {
               throw new MatchError(x$11);
            }
         }
      }).foreach(new Serializable(hadoopConf) {
         public static final long serialVersionUID = 0L;
         private final Configuration hadoopConf$1;

         public final void apply(Tuple2<String, String> x$12) {
            if (x$12 != null) {
               String key = (String)x$12._1();
               String value = (String)x$12._2();
               this.hadoopConf$1.set(key.substring("spark.hadoop.".length()), value);
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x$12);
            }
         }

         public {
            this.hadoopConf$1 = hadoopConf$1;
         }
      });
   }

   private SparkHadoopUtil$() {
      MODULE$ = this;
      this.SPARK_YARN_CREDS_TEMP_EXTENSION = ".tmp";
      this.SPARK_YARN_CREDS_COUNTER_DELIM = "-";
      this.UPDATE_INPUT_METRICS_INTERVAL_RECORDS = 1000;
   }
}
