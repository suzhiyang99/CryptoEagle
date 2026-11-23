package org.apache.spark.deploy;

import java.io.File;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.util.MutableURLClassLoader;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayOps;
import scala.reflect.ClassTag$;
import scala.sys.package$;

public final class DependencyUtils$ {
   public static final DependencyUtils$ MODULE$;

   static {
      new DependencyUtils$();
   }

   public String resolveMavenDependencies(String packagesExclusions, String packages, String repositories, String ivyRepoPath) {
      Seq exclusions = (Seq)(StringUtils.isBlank(packagesExclusions) ? Nil$.MODULE$ : Predef$.MODULE$.wrapRefArray((Object[])packagesExclusions.split(",")));
      IvySettings ivySettings = (IvySettings)package$.MODULE$.props().get("spark.jars.ivySettings").map(new Serializable(repositories, ivyRepoPath) {
         public static final long serialVersionUID = 0L;
         private final String repositories$1;
         private final String ivyRepoPath$1;

         public final IvySettings apply(String ivySettingsFile) {
            return SparkSubmitUtils$.MODULE$.loadIvySettings(ivySettingsFile, Option$.MODULE$.apply(this.repositories$1), Option$.MODULE$.apply(this.ivyRepoPath$1));
         }

         public {
            this.repositories$1 = repositories$1;
            this.ivyRepoPath$1 = ivyRepoPath$1;
         }
      }).getOrElse(new Serializable(repositories, ivyRepoPath) {
         public static final long serialVersionUID = 0L;
         private final String repositories$1;
         private final String ivyRepoPath$1;

         public final IvySettings apply() {
            return SparkSubmitUtils$.MODULE$.buildIvySettings(Option$.MODULE$.apply(this.repositories$1), Option$.MODULE$.apply(this.ivyRepoPath$1));
         }

         public {
            this.repositories$1 = repositories$1;
            this.ivyRepoPath$1 = ivyRepoPath$1;
         }
      });
      return SparkSubmitUtils$.MODULE$.resolveMavenCoordinates(packages, ivySettings, exclusions, SparkSubmitUtils$.MODULE$.resolveMavenCoordinates$default$4());
   }

   public String resolveAndDownloadJars(String jars, String userJar, SparkConf sparkConf, Configuration hadoopConf, SecurityManager secMgr) {
      File targetDir = Utils$.MODULE$.createTempDir(Utils$.MODULE$.createTempDir$default$1(), Utils$.MODULE$.createTempDir$default$2());
      return (String)Option$.MODULE$.apply(jars).map(new Serializable(userJar, hadoopConf) {
         public static final long serialVersionUID = 0L;
         public final String userJar$1;
         private final Configuration hadoopConf$1;

         public final String apply(String x$1) {
            return Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])DependencyUtils$.MODULE$.resolveGlobPaths(x$1, this.hadoopConf$1).split(",")).filterNot(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final boolean apply(String x$2) {
                  return x$2.contains((CharSequence)Predef$.MODULE$.refArrayOps((Object[])this.$outer.userJar$1.split("/")).last());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            })).mkString(",");
         }

         public {
            this.userJar$1 = userJar$1;
            this.hadoopConf$1 = hadoopConf$1;
         }
      }).filterNot(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$3) {
            boolean var10000;
            label23: {
               String var2 = "";
               if (x$3 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (x$3.equals(var2)) {
                  break label23;
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }).map(new Serializable(sparkConf, hadoopConf, secMgr, targetDir) {
         public static final long serialVersionUID = 0L;
         private final SparkConf sparkConf$1;
         private final Configuration hadoopConf$1;
         private final SecurityManager secMgr$1;
         private final File targetDir$1;

         public final String apply(String x$4) {
            return DependencyUtils$.MODULE$.downloadFileList(x$4, this.targetDir$1, this.sparkConf$1, this.hadoopConf$1, this.secMgr$1);
         }

         public {
            this.sparkConf$1 = sparkConf$1;
            this.hadoopConf$1 = hadoopConf$1;
            this.secMgr$1 = secMgr$1;
            this.targetDir$1 = targetDir$1;
         }
      }).orNull(Predef$.MODULE$.$conforms());
   }

   public void addJarsToClassPath(String jars, MutableURLClassLoader loader) {
      if (jars != null) {
         Predef$.MODULE$.refArrayOps((Object[])jars.split(",")).foreach(new Serializable(loader) {
            public static final long serialVersionUID = 0L;
            private final MutableURLClassLoader loader$1;

            public final void apply(String jar) {
               SparkSubmit$.MODULE$.addJarToClasspath(jar, this.loader$1);
            }

            public {
               this.loader$1 = loader$1;
            }
         });
      }

   }

   public String downloadFileList(String fileList, File targetDir, SparkConf sparkConf, Configuration hadoopConf, SecurityManager secMgr) {
      Predef$.MODULE$.require(fileList != null, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "fileList cannot be null.";
         }
      });
      return ((TraversableOnce)Utils$.MODULE$.stringToSeq(fileList).map(new Serializable(targetDir, sparkConf, hadoopConf, secMgr) {
         public static final long serialVersionUID = 0L;
         private final File targetDir$2;
         private final SparkConf sparkConf$2;
         private final Configuration hadoopConf$3;
         private final SecurityManager secMgr$2;

         public final String apply(String x$5) {
            return DependencyUtils$.MODULE$.downloadFile(x$5, this.targetDir$2, this.sparkConf$2, this.hadoopConf$3, this.secMgr$2);
         }

         public {
            this.targetDir$2 = targetDir$2;
            this.sparkConf$2 = sparkConf$2;
            this.hadoopConf$3 = hadoopConf$3;
            this.secMgr$2 = secMgr$2;
         }
      }, Seq$.MODULE$.canBuildFrom())).mkString(",");
   }

   public String downloadFile(String path, File targetDir, SparkConf sparkConf, Configuration hadoopConf, SecurityManager secMgr) {
      Predef$.MODULE$.require(path != null, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "path cannot be null.";
         }
      });
      URI uri = Utils$.MODULE$.resolveURI(path);
      String var7 = uri.getScheme();
      boolean var8;
      if ("file".equals(var7)) {
         var8 = true;
      } else if ("local".equals(var7)) {
         var8 = true;
      } else {
         var8 = false;
      }

      String var9;
      if (var8) {
         var9 = path;
      } else {
         boolean var10;
         if ("http".equals(var7)) {
            var10 = true;
         } else if ("https".equals(var7)) {
            var10 = true;
         } else if ("ftp".equals(var7)) {
            var10 = true;
         } else {
            var10 = false;
         }

         if (var10 && Utils$.MODULE$.isTesting()) {
            File file = new File(uri.getPath());
            var9 = (new File(targetDir, file.getName())).toURI().toString();
         } else {
            String fname = (new Path(uri)).getName();
            File localFile = Utils$.MODULE$.doFetchFile(uri.toString(), targetDir, fname, sparkConf, secMgr, hadoopConf);
            var9 = localFile.toURI().toString();
         }
      }

      return var9;
   }

   public String resolveGlobPaths(String paths, Configuration hadoopConf) {
      Predef$.MODULE$.require(paths != null, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "paths cannot be null.";
         }
      });
      return ((TraversableOnce)Utils$.MODULE$.stringToSeq(paths).flatMap(new Serializable(hadoopConf) {
         public static final long serialVersionUID = 0L;
         private final Configuration hadoopConf$2;

         public final ArrayOps<String> apply(String path) {
            URI uri = Utils$.MODULE$.resolveURI(path);
            String var3 = uri.getScheme();
            boolean var4;
            if ("local".equals(var3)) {
               var4 = true;
            } else if ("http".equals(var3)) {
               var4 = true;
            } else if ("https".equals(var3)) {
               var4 = true;
            } else if ("ftp".equals(var3)) {
               var4 = true;
            } else {
               var4 = false;
            }

            ArrayOps var5;
            if (var4) {
               var5 = Predef$.MODULE$.refArrayOps((Object[])(new String[]{path}));
            } else {
               FileSystem fs = FileSystem.get(uri, this.hadoopConf$2);
               var5 = Predef$.MODULE$.refArrayOps(Option$.MODULE$.apply(fs.globStatus(new Path(uri))).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String[] apply(FileStatus[] status) {
                     return (String[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])status).filter(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final boolean apply(FileStatus x$6) {
                           return x$6.isFile();
                        }
                     })).map(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply(FileStatus x$7) {
                           return x$7.getPath().toUri().toString();
                        }
                     }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)));
                  }
               }).getOrElse(new Serializable(this, path) {
                  public static final long serialVersionUID = 0L;
                  private final String path$1;

                  public final String[] apply() {
                     return (String[])((Object[])(new String[]{this.path$1}));
                  }

                  public {
                     this.path$1 = path$1;
                  }
               }));
            }

            return var5;
         }

         public {
            this.hadoopConf$2 = hadoopConf$2;
         }
      }, Seq$.MODULE$.canBuildFrom())).mkString(",");
   }

   private DependencyUtils$() {
      MODULE$ = this;
   }
}
