package org.apache.spark.deploy;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkUserAppException;
import org.apache.spark.api.python.PythonUtils$;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.package$;
import org.apache.spark.util.RedirectThread;
import org.apache.spark.util.RedirectThread$;
import org.apache.spark.util.Utils$;
import py4j.GatewayServer;
import scala.Array$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;
import scala.util.Try$;

public final class PythonRunner$ {
   public static final PythonRunner$ MODULE$;

   static {
      new PythonRunner$();
   }

   public void main(String[] args) {
      String pythonFile = args[0];
      String pyFiles = args[1];
      String[] otherArgs = (String[])Predef$.MODULE$.refArrayOps((Object[])args).slice(2, args.length);
      SparkConf sparkConf = new SparkConf();
      String pythonExec = (String)((Option)sparkConf.get((ConfigEntry)package$.MODULE$.PYSPARK_DRIVER_PYTHON())).orElse(new Serializable(sparkConf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf sparkConf$1;

         public final Option<String> apply() {
            return (Option)this.sparkConf$1.get((ConfigEntry)package$.MODULE$.PYSPARK_PYTHON());
         }

         public {
            this.sparkConf$1 = sparkConf$1;
         }
      }).orElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply() {
            return scala.sys.package$.MODULE$.env().get("PYSPARK_DRIVER_PYTHON");
         }
      }).orElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply() {
            return scala.sys.package$.MODULE$.env().get("PYSPARK_PYTHON");
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "python";
         }
      });
      String formattedPythonFile = this.formatPath(pythonFile, this.formatPath$default$2());
      String[] formattedPyFiles = this.formatPaths(pyFiles, this.formatPaths$default$2());
      GatewayServer gatewayServer = new GatewayServer((Object)null, 0);
      Thread thread = new Thread(new Runnable(gatewayServer) {
         public final GatewayServer gatewayServer$1;

         public void run() {
            Utils$.MODULE$.logUncaughtExceptions(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.gatewayServer$1.start();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         public {
            this.gatewayServer$1 = gatewayServer$1;
         }
      });
      thread.setName("py4j-gateway-init");
      thread.setDaemon(true);
      thread.start();
      thread.join();
      ArrayBuffer pathElements = new ArrayBuffer();
      pathElements.$plus$plus$eq(Predef$.MODULE$.refArrayOps((Object[])formattedPyFiles));
      pathElements.$plus$eq(PythonUtils$.MODULE$.sparkPythonPath());
      pathElements.$plus$eq(scala.sys.package$.MODULE$.env().getOrElse("PYTHONPATH", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      }));
      String pythonPath = PythonUtils$.MODULE$.mergePythonPaths(pathElements);
      ProcessBuilder builder = new ProcessBuilder((List)JavaConverters$.MODULE$.seqAsJavaListConverter((Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{pythonExec, formattedPythonFile})))).$plus$plus(Predef$.MODULE$.refArrayOps((Object[])otherArgs), Seq$.MODULE$.canBuildFrom())).asJava());
      Map env = builder.environment();
      env.put("PYTHONPATH", pythonPath);
      env.put("PYTHONUNBUFFERED", "YES");
      env.put("PYSPARK_GATEWAY_PORT", String.valueOf(BoxesRunTime.boxToInteger(gatewayServer.getListeningPort())));
      ((Option)sparkConf.get((ConfigEntry)package$.MODULE$.PYSPARK_PYTHON())).foreach(new Serializable(env) {
         public static final long serialVersionUID = 0L;
         private final Map env$1;

         public final String apply(String x$1) {
            return (String)this.env$1.put("PYSPARK_PYTHON", x$1);
         }

         public {
            this.env$1 = env$1;
         }
      });
      scala.sys.package$.MODULE$.env().get("PYTHONHASHSEED").foreach(new Serializable(env) {
         public static final long serialVersionUID = 0L;
         private final Map env$1;

         public final String apply(String x$2) {
            return (String)this.env$1.put("PYTHONHASHSEED", x$2);
         }

         public {
            this.env$1 = env$1;
         }
      });
      builder.redirectErrorStream(true);

      try {
         Process process = builder.start();
         (new RedirectThread(process.getInputStream(), System.out, "redirect output", RedirectThread$.MODULE$.$lessinit$greater$default$4())).start();
         int exitCode = process.waitFor();
         if (exitCode != 0) {
            throw new SparkUserAppException(exitCode);
         }
      } finally {
         gatewayServer.shutdown();
      }

   }

   public String formatPath(String path, boolean testWindows) {
      if (Predef$.MODULE$.refArrayOps((Object[])Utils$.MODULE$.nonLocalPaths(path, testWindows)).nonEmpty()) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)"Launching Python applications through ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark-submit is currently only supported for local files: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{path}))).toString());
      } else {
         URI uri = (URI)Try$.MODULE$.apply(new Serializable(path) {
            public static final long serialVersionUID = 0L;
            private final String path$1;

            public final URI apply() {
               return new URI(this.path$1);
            }

            public {
               this.path$1 = path$1;
            }
         }).getOrElse(new Serializable(path) {
            public static final long serialVersionUID = 0L;
            private final String path$1;

            public final URI apply() {
               return (new File(this.path$1)).toURI();
            }

            public {
               this.path$1 = path$1;
            }
         });
         String var5 = uri.getScheme();
         String var6;
         if (var5 == null) {
            var6 = path;
         } else {
            boolean var7;
            if ("file".equals(var5)) {
               var7 = true;
            } else if ("local".equals(var5)) {
               var7 = true;
            } else {
               var7 = false;
            }

            if (var7) {
               var6 = uri.getPath();
            } else {
               var6 = null;
            }
         }

         String formattedPath = var6;
         if (var6 == null) {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Python file path is malformed: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{path})));
         } else {
            if (Utils$.MODULE$.isWindows() && var6.matches("/[a-zA-Z]:/.*")) {
               formattedPath = (new StringOps(Predef$.MODULE$.augmentString(var6))).stripPrefix("/");
            }

            return formattedPath;
         }
      }
   }

   public boolean formatPath$default$2() {
      return false;
   }

   public String[] formatPaths(String paths, boolean testWindows) {
      return (String[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])((String)Option$.MODULE$.apply(paths).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).split(",")).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$3) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$3))).nonEmpty();
         }
      })).map(new Serializable(testWindows) {
         public static final long serialVersionUID = 0L;
         private final boolean testWindows$1;

         public final String apply(String p) {
            return PythonRunner$.MODULE$.formatPath(p, this.testWindows$1);
         }

         public {
            this.testWindows$1 = testWindows$1;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)));
   }

   public boolean formatPaths$default$2() {
      return false;
   }

   private PythonRunner$() {
      MODULE$ = this;
   }
}
