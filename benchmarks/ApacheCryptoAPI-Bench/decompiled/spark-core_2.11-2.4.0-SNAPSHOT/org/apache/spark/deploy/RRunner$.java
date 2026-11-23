package org.apache.spark.deploy;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkException;
import org.apache.spark.SparkUserAppException;
import org.apache.spark.api.r.RBackend;
import org.apache.spark.api.r.RUtils$;
import org.apache.spark.api.r.SparkRDefaults$;
import org.apache.spark.util.RedirectThread;
import org.apache.spark.util.RedirectThread$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.StringOps;
import scala.runtime.BoxesRunTime;
import scala.runtime.ObjectRef;
import scala.runtime.VolatileIntRef;
import scala.sys.package$;

public final class RRunner$ {
   public static final RRunner$ MODULE$;

   static {
      new RRunner$();
   }

   public void main(String[] args) {
      String rFile;
      String[] otherArgs;
      int backendTimeout;
      ObjectRef cmd;
      label66: {
         rFile = PythonRunner$.MODULE$.formatPath(args[0], PythonRunner$.MODULE$.formatPath$default$2());
         otherArgs = (String[])Predef$.MODULE$.refArrayOps((Object[])args).slice(1, args.length);
         backendTimeout = (new StringOps(Predef$.MODULE$.augmentString((String)package$.MODULE$.env().getOrElse("SPARKR_BACKEND_TIMEOUT", new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "120";
            }
         })))).toInt();
         cmd = ObjectRef.create((String)package$.MODULE$.props().getOrElse("spark.sparkr.r.command", new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Rscript";
            }
         }));
         cmd.elem = (T)((String)package$.MODULE$.props().getOrElse("spark.r.command", new Serializable(cmd) {
            public static final long serialVersionUID = 0L;
            private final ObjectRef cmd$1;

            public final String apply() {
               return (String)this.cmd$1.elem;
            }

            public {
               this.cmd$1 = cmd$1;
            }
         }));
         Object var10000 = package$.MODULE$.props().getOrElse("spark.submit.deployMode", new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "client";
            }
         });
         String var7 = "client";
         if (var10000 == null) {
            if (var7 != null) {
               break label66;
            }
         } else if (!var10000.equals(var7)) {
            break label66;
         }

         cmd.elem = (T)((String)package$.MODULE$.props().getOrElse("spark.r.driver.command", new Serializable(cmd) {
            public static final long serialVersionUID = 0L;
            private final ObjectRef cmd$1;

            public final String apply() {
               return (String)this.cmd$1.elem;
            }

            public {
               this.cmd$1 = cmd$1;
            }
         }));
      }

      String rCommand = (String)cmd.elem;
      String backendConnectionTimeout = (String)package$.MODULE$.props().getOrElse("spark.r.backendConnectionTimeout", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return BoxesRunTime.boxToInteger(SparkRDefaults$.MODULE$.DEFAULT_CONNECTION_TIMEOUT()).toString();
         }
      });
      File rF = new File(rFile);
      String rFileNormalized = rF.exists() ? rFile : (new Path(rFile)).getName();
      RBackend sparkRBackend = new RBackend();
      VolatileIntRef sparkRBackendPort = VolatileIntRef.create(0);
      Semaphore initialized = new Semaphore(0);
      Thread sparkRBackendThread = new Thread(sparkRBackend, sparkRBackendPort, initialized) {
         private final RBackend sparkRBackend$1;
         private final VolatileIntRef sparkRBackendPort$1;
         private final Semaphore initialized$1;

         public void run() {
            this.sparkRBackendPort$1.elem = this.sparkRBackend$1.init();
            this.initialized$1.release();
            this.sparkRBackend$1.run();
         }

         public {
            this.sparkRBackend$1 = sparkRBackend$1;
            this.sparkRBackendPort$1 = sparkRBackendPort$1;
            this.initialized$1 = initialized$1;
         }
      };
      sparkRBackendThread.start();
      if (initialized.tryAcquire((long)backendTimeout, TimeUnit.SECONDS)) {
         int var24;
         try {
            ProcessBuilder builder = new ProcessBuilder((List)JavaConverters$.MODULE$.seqAsJavaListConverter((Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{rCommand, rFileNormalized})))).$plus$plus(Predef$.MODULE$.refArrayOps((Object[])otherArgs), Seq$.MODULE$.canBuildFrom())).asJava());
            Map env = builder.environment();
            env.put("EXISTING_SPARKR_BACKEND_PORT", BoxesRunTime.boxToInteger(sparkRBackendPort.elem).toString());
            env.put("SPARKR_BACKEND_CONNECTION_TIMEOUT", backendConnectionTimeout);
            Seq rPackageDir = RUtils$.MODULE$.sparkRPackagePath(true);
            env.put("SPARKR_PACKAGE_DIR", rPackageDir.mkString(","));
            env.put("R_PROFILE_USER", ((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{(String)rPackageDir.apply(0), "SparkR", "profile", "general.R"})))).mkString(File.separator));
            builder.redirectErrorStream(true);
            Process process = builder.start();
            (new RedirectThread(process.getInputStream(), System.out, "redirect R output", RedirectThread$.MODULE$.$lessinit$greater$default$4())).start();
            var24 = process.waitFor();
         } finally {
            sparkRBackend.close();
         }

         int returnCode = var24;
         if (returnCode != 0) {
            throw new SparkUserAppException(returnCode);
         }
      } else {
         String errorMessage = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"SparkR backend did not initialize in ", " seconds"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(backendTimeout)}));
         System.err.println(errorMessage);
         throw new SparkException(errorMessage);
      }
   }

   private RRunner$() {
      MODULE$ = this;
   }
}
