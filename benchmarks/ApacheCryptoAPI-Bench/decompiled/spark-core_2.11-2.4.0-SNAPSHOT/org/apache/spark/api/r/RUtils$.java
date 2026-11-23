package org.apache.spark.api.r;

import java.io.File;
import java.util.Arrays;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.runtime.Nothing$;
import scala.sys.package$;

public final class RUtils$ {
   public static final RUtils$ MODULE$;
   private Option<String> rPackages;

   static {
      new RUtils$();
   }

   public Option<String> rPackages() {
      return this.rPackages;
   }

   public void rPackages_$eq(Option<String> x$1) {
      this.rPackages = x$1;
   }

   public Option<String> localSparkRPackagePath() {
      Option sparkHome = package$.MODULE$.env().get("SPARK_HOME").orElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply() {
            return package$.MODULE$.props().get("spark.test.home");
         }
      });
      return sparkHome.<String>map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$1) {
            return ((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{x$1, "R", "lib"})))).mkString(File.separator);
         }
      });
   }

   public boolean isSparkRInstalled() {
      return this.localSparkRPackagePath().filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String pkgDir) {
            return (new File(((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{pkgDir, "SparkR"})))).mkString(File.separator))).exists();
         }
      }).isDefined();
   }

   public Seq<String> sparkRPackagePath(boolean isDriver) {
      Tuple2 var10000;
      if (isDriver) {
         var10000 = new Tuple2(package$.MODULE$.props().apply("spark.master"), package$.MODULE$.props().apply("spark.submit.deployMode"));
      } else {
         SparkConf sparkConf = SparkEnv$.MODULE$.get().conf();
         var10000 = new Tuple2(sparkConf.get("spark.master"), sparkConf.get("spark.submit.deployMode", "client"));
      }

      Tuple2 var3 = var10000;
      if (var3 == null) {
         throw new MatchError(var3);
      } else {
         String master;
         String deployMode;
         label67: {
            label66: {
               String master = (String)var3._1();
               String deployMode = (String)var3._2();
               Tuple2 var7 = new Tuple2(master, deployMode);
               master = (String)var7._1();
               deployMode = (String)var7._2();
               if (master != null && master.contains("yarn")) {
                  String var11 = "cluster";
                  if (deployMode == null) {
                     if (var11 == null) {
                        break label66;
                     }
                  } else if (deployMode.equals(var11)) {
                     break label66;
                  }
               }

               var17 = false;
               break label67;
            }

            var17 = true;
         }

         boolean isYarnCluster;
         label57: {
            label56: {
               isYarnCluster = var17;
               if (master != null && master.contains("yarn")) {
                  String var13 = "client";
                  if (deployMode == null) {
                     if (var13 == null) {
                        break label56;
                     }
                  } else if (deployMode.equals(var13)) {
                     break label56;
                  }
               }

               var18 = false;
               break label57;
            }

            var18 = true;
         }

         boolean isYarnClient = var18;
         Seq var19;
         if (isYarnCluster || isYarnClient && !isDriver) {
            String sparkRPkgPath = (new File("sparkr")).getAbsolutePath();
            File rPkgPath = new File("rpkg");
            var19 = rPkgPath.exists() ? (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{sparkRPkgPath, rPkgPath.getAbsolutePath()}))) : (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{sparkRPkgPath})));
         } else {
            String sparkRPkgPath = (String)this.localSparkRPackagePath().getOrElse(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final Nothing$ apply() {
                  throw new SparkException("SPARK_HOME not set. Can't locate SparkR package.");
               }
            });
            var19 = this.rPackages().isEmpty() ? (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{sparkRPkgPath}))) : (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{sparkRPkgPath, (String)this.rPackages().get()})));
         }

         return var19;
      }
   }

   public boolean isRInstalled() {
      boolean var10000;
      try {
         ProcessBuilder builder = new ProcessBuilder(Arrays.asList((Object[])(new String[]{"R", "--version"})));
         var10000 = builder.start().waitFor() == 0;
      } catch (Exception var3) {
         var10000 = false;
      }

      return var10000;
   }

   private RUtils$() {
      MODULE$ = this;
      this.rPackages = None$.MODULE$;
   }
}
