package org.apache.spark.deploy;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkUserAppException;
import org.apache.spark.package$;
import org.apache.spark.api.r.RUtils$;
import org.apache.spark.deploy.rest.RestSubmissionClient;
import org.apache.spark.deploy.rest.RestSubmissionClientApp;
import org.apache.spark.deploy.rest.SubmitRestConnectionException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.OptionalConfigEntry;
import org.apache.spark.util.ChildFirstURLClassLoader;
import org.apache.spark.util.CommandLineUtils;
import org.apache.spark.util.CommandLineUtils$class;
import org.apache.spark.util.MutableURLClassLoader;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.App;
import scala.Enumeration;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple2$mcII$sp;
import scala.Tuple4;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.ArrayOps;
import scala.collection.mutable.BufferLike;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;
import scala.runtime.VolatileByteRef;
import scala.util.Properties$;
import scala.util.Try$;

public final class SparkSubmit$ implements CommandLineUtils, Logging {
   public static final SparkSubmit$ MODULE$;
   private final int YARN;
   private final int STANDALONE;
   private final int MESOS;
   private final int LOCAL;
   private final int KUBERNETES;
   private final int ALL_CLUSTER_MGRS;
   private final int CLIENT;
   private final int CLUSTER;
   private final int ALL_DEPLOY_MODES;
   private final String SPARK_SHELL;
   private final String PYSPARK_SHELL;
   private final String SPARKR_SHELL;
   private final String SPARKR_PACKAGE_ARCHIVE;
   private final String R_PACKAGE_ARCHIVE;
   private final int CLASS_NOT_FOUND_EXIT_STATUS;
   private final String YARN_CLUSTER_SUBMIT_CLASS;
   private final String REST_CLUSTER_SUBMIT_CLASS;
   private final String STANDALONE_CLUSTER_SUBMIT_CLASS;
   private final String KUBERNETES_CLUSTER_SUBMIT_CLASS;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private Function1<Object, BoxedUnit> exitFn;
   private PrintStream printStream;

   static {
      new SparkSubmit$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public Function1<Object, BoxedUnit> exitFn() {
      return this.exitFn;
   }

   public void exitFn_$eq(Function1<Object, BoxedUnit> x$1) {
      this.exitFn = x$1;
   }

   public PrintStream printStream() {
      return this.printStream;
   }

   public void printStream_$eq(PrintStream x$1) {
      this.printStream = x$1;
   }

   public void printWarning(String str) {
      CommandLineUtils$class.printWarning(this, str);
   }

   public void printErrorAndExit(String str) {
      CommandLineUtils$class.printErrorAndExit(this, str);
   }

   public Tuple2<String, String> parseSparkConfProperty(String pair) {
      return CommandLineUtils$class.parseSparkConfProperty(this, pair);
   }

   private int YARN() {
      return this.YARN;
   }

   private int STANDALONE() {
      return this.STANDALONE;
   }

   private int MESOS() {
      return this.MESOS;
   }

   private int LOCAL() {
      return this.LOCAL;
   }

   private int KUBERNETES() {
      return this.KUBERNETES;
   }

   private int ALL_CLUSTER_MGRS() {
      return this.ALL_CLUSTER_MGRS;
   }

   private int CLIENT() {
      return this.CLIENT;
   }

   private int CLUSTER() {
      return this.CLUSTER;
   }

   private int ALL_DEPLOY_MODES() {
      return this.ALL_DEPLOY_MODES;
   }

   private String SPARK_SHELL() {
      return this.SPARK_SHELL;
   }

   private String PYSPARK_SHELL() {
      return this.PYSPARK_SHELL;
   }

   private String SPARKR_SHELL() {
      return this.SPARKR_SHELL;
   }

   private String SPARKR_PACKAGE_ARCHIVE() {
      return this.SPARKR_PACKAGE_ARCHIVE;
   }

   private String R_PACKAGE_ARCHIVE() {
      return this.R_PACKAGE_ARCHIVE;
   }

   private int CLASS_NOT_FOUND_EXIT_STATUS() {
      return this.CLASS_NOT_FOUND_EXIT_STATUS;
   }

   public String YARN_CLUSTER_SUBMIT_CLASS() {
      return this.YARN_CLUSTER_SUBMIT_CLASS;
   }

   public String REST_CLUSTER_SUBMIT_CLASS() {
      return this.REST_CLUSTER_SUBMIT_CLASS;
   }

   public String STANDALONE_CLUSTER_SUBMIT_CLASS() {
      return this.STANDALONE_CLUSTER_SUBMIT_CLASS;
   }

   public String KUBERNETES_CLUSTER_SUBMIT_CLASS() {
      return this.KUBERNETES_CLUSTER_SUBMIT_CLASS;
   }

   public void printVersionAndExit() {
      this.printStream().println((new StringOps(Predef$.MODULE$.augmentString("Welcome to\n      ____              __\n     / __/__  ___ _____/ /__\n    _\\ \\/ _ \\/ _ `/ __/  '_/\n   /___/ .__/\\_,_/_/ /_/\\_\\   version %s\n      /_/\n                        "))).format(Predef$.MODULE$.genericWrapArray(new Object[]{package$.MODULE$.SPARK_VERSION()})));
      this.printStream().println((new StringOps(Predef$.MODULE$.augmentString("Using Scala %s, %s, %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{Properties$.MODULE$.versionString(), Properties$.MODULE$.javaVmName(), Properties$.MODULE$.javaVersion()})));
      this.printStream().println((new StringOps(Predef$.MODULE$.augmentString("Branch %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{package$.MODULE$.SPARK_BRANCH()})));
      this.printStream().println((new StringOps(Predef$.MODULE$.augmentString("Compiled by user %s on %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{package$.MODULE$.SPARK_BUILD_USER(), package$.MODULE$.SPARK_BUILD_DATE()})));
      this.printStream().println((new StringOps(Predef$.MODULE$.augmentString("Revision %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{package$.MODULE$.SPARK_REVISION()})));
      this.printStream().println((new StringOps(Predef$.MODULE$.augmentString("Url %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{package$.MODULE$.SPARK_REPO_URL()})));
      this.printStream().println("Type --help for more information.");
      this.exitFn().apply$mcVI$sp(0);
   }

   public void main(String[] args) {
      boolean uninitLog = this.initializeLogIfNecessary(true, true);
      SparkSubmitArguments appArgs = new SparkSubmitArguments(Predef$.MODULE$.wrapRefArray((Object[])args), SparkSubmitArguments$.MODULE$.$lessinit$greater$default$2());
      if (appArgs.verbose()) {
         this.printStream().println(appArgs);
      }

      label58: {
         Enumeration.Value var4 = appArgs.action();
         Enumeration.Value var10000 = SparkSubmitAction$.MODULE$.SUBMIT();
         if (var10000 == null) {
            if (var4 == null) {
               break label58;
            }
         } else if (var10000.equals(var4)) {
            break label58;
         }

         label59: {
            var10000 = SparkSubmitAction$.MODULE$.KILL();
            if (var10000 == null) {
               if (var4 == null) {
                  break label59;
               }
            } else if (var10000.equals(var4)) {
               break label59;
            }

            label31: {
               var10000 = SparkSubmitAction$.MODULE$.REQUEST_STATUS();
               if (var10000 == null) {
                  if (var4 == null) {
                     break label31;
                  }
               } else if (var10000.equals(var4)) {
                  break label31;
               }

               throw new MatchError(var4);
            }

            this.requestStatus(appArgs);
            BoxedUnit var6 = BoxedUnit.UNIT;
            return;
         }

         this.kill(appArgs);
         BoxedUnit var9 = BoxedUnit.UNIT;
         return;
      }

      this.submit(appArgs, uninitLog);
      BoxedUnit var10 = BoxedUnit.UNIT;
   }

   private void kill(SparkSubmitArguments args) {
      (new RestSubmissionClient(args.master())).killSubmission(args.submissionToKill());
   }

   private void requestStatus(SparkSubmitArguments args) {
      RestSubmissionClient qual$1 = new RestSubmissionClient(args.master());
      String x$14 = args.submissionToRequestStatusFor();
      boolean x$15 = qual$1.requestSubmissionStatus$default$2();
      qual$1.requestSubmissionStatus(x$14, x$15);
   }

   private void submit(SparkSubmitArguments args, boolean uninitLog) {
      while(true) {
         Tuple4 var5 = this.prepareSubmitEnvironment(args, this.prepareSubmitEnvironment$default$2());
         if (var5 != null) {
            Seq childArgs = (Seq)var5._1();
            Seq childClasspath = (Seq)var5._2();
            SparkConf sparkConf = (SparkConf)var5._3();
            String childMainClass = (String)var5._4();
            Tuple4 var10 = new Tuple4(childArgs, childClasspath, sparkConf, childMainClass);
            Seq childArgs = (Seq)var10._1();
            Seq childClasspath = (Seq)var10._2();
            SparkConf sparkConf = (SparkConf)var10._3();
            String childMainClass = (String)var10._4();
            if (uninitLog) {
               Logging$.MODULE$.uninitialize();
            }

            if (args.isStandaloneCluster() && args.useRest()) {
               try {
                  this.printStream().println("Running Spark using the REST application submission protocol.");
                  this.doRunMain$1(args, childArgs, childClasspath, sparkConf, childMainClass);
                  BoxedUnit var17 = BoxedUnit.UNIT;
               } catch (SubmitRestConnectionException var16) {
                  this.printWarning((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Master endpoint ", " was not a REST server. "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{args.master()}))).append((Object)"Falling back to legacy submission gateway instead.").toString());
                  args.useRest_$eq(false);
                  uninitLog = false;
                  args = args;
                  continue;
               }
            } else {
               this.doRunMain$1(args, childArgs, childClasspath, sparkConf, childMainClass);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

            return;
         }

         throw new MatchError(var5);
      }
   }

   public Tuple4<Seq<String>, Seq<String>, SparkConf, String> prepareSubmitEnvironment(SparkSubmitArguments args, Option<Configuration> conf) {
      ObjectRef secMgr$lzy = ObjectRef.zero();
      VolatileByteRef bitmap$0 = VolatileByteRef.create((byte)0);
      ArrayBuffer childArgs = new ArrayBuffer();
      ArrayBuffer childClasspath = new ArrayBuffer();
      SparkConf sparkConf = new SparkConf();
      String childMainClass = "";
      String var10 = args.master();
      int var11;
      if ("yarn".equals(var10)) {
         var11 = this.YARN();
      } else {
         boolean var12;
         if ("yarn-client".equals(var10)) {
            var12 = true;
         } else if ("yarn-cluster".equals(var10)) {
            var12 = true;
         } else {
            var12 = false;
         }

         if (var12) {
            this.printWarning((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Master ", " is deprecated since 2.0."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{args.master()}))).append((Object)" Please use master \"yarn\" with specified deploy mode instead.").toString());
            var11 = this.YARN();
         } else if (var10.startsWith("spark")) {
            var11 = this.STANDALONE();
         } else if (var10.startsWith("mesos")) {
            var11 = this.MESOS();
         } else if (var10.startsWith("k8s")) {
            var11 = this.KUBERNETES();
         } else if (var10.startsWith("local")) {
            var11 = this.LOCAL();
         } else {
            this.printErrorAndExit("Master must either be yarn or start with spark, mesos, k8s, or local");
            var11 = -1;
         }
      }

      String var14 = args.deployMode();
      boolean var15;
      if ("client".equals(var14)) {
         var15 = true;
      } else if (var14 == null) {
         var15 = true;
      } else {
         var15 = false;
      }

      int var16;
      if (var15) {
         var16 = this.CLIENT();
      } else if ("cluster".equals(var14)) {
         var16 = this.CLUSTER();
      } else {
         this.printErrorAndExit("Deploy mode must be either client or cluster");
         var16 = -1;
      }

      IntRef deployMode = IntRef.create(var16);
      if (var11 == this.YARN()) {
         label539: {
            Tuple2 var17 = new Tuple2(args.master(), args.deployMode());
            if (var17 != null) {
               String var18 = (String)var17._1();
               String var19 = (String)var17._2();
               if ("yarn-cluster".equals(var18) && var19 == null) {
                  deployMode.elem = this.CLUSTER();
                  args.master_$eq("yarn");
                  BoxedUnit var227 = BoxedUnit.UNIT;
                  break label539;
               }
            }

            if (var17 != null) {
               String var21 = (String)var17._1();
               String var22 = (String)var17._2();
               if ("yarn-cluster".equals(var21) && "client".equals(var22)) {
                  this.printErrorAndExit("Client deploy mode is not compatible with master \"yarn-cluster\"");
                  BoxedUnit var226 = BoxedUnit.UNIT;
                  break label539;
               }
            }

            if (var17 != null) {
               String var23 = (String)var17._1();
               String var24 = (String)var17._2();
               if ("yarn-client".equals(var23) && "cluster".equals(var24)) {
                  this.printErrorAndExit("Cluster deploy mode is not compatible with master \"yarn-client\"");
                  BoxedUnit var225 = BoxedUnit.UNIT;
                  break label539;
               }
            }

            if (var17 == null) {
               throw new MatchError(var17);
            }

            args.master_$eq("yarn");
            BoxedUnit var20 = BoxedUnit.UNIT;
         }

         if (!Utils$.MODULE$.classIsLoadable(this.YARN_CLUSTER_SUBMIT_CLASS()) && !Utils$.MODULE$.isTesting()) {
            this.printErrorAndExit("Could not load YARN classes. This copy of Spark may not have been compiled with YARN support.");
         }
      }

      if (var11 == this.KUBERNETES()) {
         args.master_$eq(Utils$.MODULE$.checkAndGetK8sMasterUrl(args.master()));
         if (!Utils$.MODULE$.classIsLoadable(this.KUBERNETES_CLUSTER_SUBMIT_CLASS()) && !Utils$.MODULE$.isTesting()) {
            this.printErrorAndExit("Could not load KUBERNETES classes. This copy of Spark may not have been compiled with KUBERNETES support.");
         }
      }

      label540: {
         Tuple2$mcII$sp var25 = new Tuple2$mcII$sp(var11, deployMode.elem);
         if (var25 != null) {
            int var26 = ((Tuple2)var25)._1$mcI$sp();
            int var27 = ((Tuple2)var25)._2$mcI$sp();
            if (this.STANDALONE() == var26 && this.CLUSTER() == var27 && args.isPython()) {
               this.printErrorAndExit("Cluster deploy mode is currently not supported for python applications on standalone clusters.");
               BoxedUnit var236 = BoxedUnit.UNIT;
               break label540;
            }
         }

         if (var25 != null) {
            int var29 = ((Tuple2)var25)._1$mcI$sp();
            int var30 = ((Tuple2)var25)._2$mcI$sp();
            if (this.STANDALONE() == var29 && this.CLUSTER() == var30 && args.isR()) {
               this.printErrorAndExit("Cluster deploy mode is currently not supported for R applications on standalone clusters.");
               BoxedUnit var235 = BoxedUnit.UNIT;
               break label540;
            }
         }

         if (var25 != null) {
            int var31 = ((Tuple2)var25)._1$mcI$sp();
            if (this.KUBERNETES() == var31 && args.isPython()) {
               this.printErrorAndExit("Python applications are currently not supported for Kubernetes.");
               BoxedUnit var234 = BoxedUnit.UNIT;
               break label540;
            }
         }

         if (var25 != null) {
            int var32 = ((Tuple2)var25)._1$mcI$sp();
            if (this.KUBERNETES() == var32 && args.isR()) {
               this.printErrorAndExit("R applications are currently not supported for Kubernetes.");
               BoxedUnit var233 = BoxedUnit.UNIT;
               break label540;
            }
         }

         if (var25 != null) {
            int var33 = ((Tuple2)var25)._1$mcI$sp();
            int var34 = ((Tuple2)var25)._2$mcI$sp();
            if (this.KUBERNETES() == var33 && this.CLIENT() == var34) {
               this.printErrorAndExit("Client mode is currently not supported for Kubernetes.");
               BoxedUnit var232 = BoxedUnit.UNIT;
               break label540;
            }
         }

         if (var25 != null) {
            int var35 = ((Tuple2)var25)._1$mcI$sp();
            int var36 = ((Tuple2)var25)._2$mcI$sp();
            if (this.LOCAL() == var35 && this.CLUSTER() == var36) {
               this.printErrorAndExit("Cluster deploy mode is not compatible with master \"local\"");
               BoxedUnit var231 = BoxedUnit.UNIT;
               break label540;
            }
         }

         if (var25 != null) {
            int var37 = ((Tuple2)var25)._2$mcI$sp();
            if (this.CLUSTER() == var37 && this.isShell(args.primaryResource())) {
               this.printErrorAndExit("Cluster deploy mode is not applicable to Spark shells.");
               BoxedUnit var230 = BoxedUnit.UNIT;
               break label540;
            }
         }

         if (var25 != null) {
            int var38 = ((Tuple2)var25)._2$mcI$sp();
            if (this.CLUSTER() == var38 && this.isSqlShell(args.mainClass())) {
               this.printErrorAndExit("Cluster deploy mode is not applicable to Spark SQL shell.");
               BoxedUnit var229 = BoxedUnit.UNIT;
               break label540;
            }
         }

         if (var25 != null) {
            int var39 = ((Tuple2)var25)._2$mcI$sp();
            if (this.CLUSTER() == var39 && this.isThriftServer(args.mainClass())) {
               this.printErrorAndExit("Cluster deploy mode is not applicable to Spark Thrift server.");
               BoxedUnit var228 = BoxedUnit.UNIT;
               break label540;
            }
         }

         BoxedUnit var28 = BoxedUnit.UNIT;
      }

      label541: {
         Tuple2 var40 = new Tuple2(args.deployMode(), BoxesRunTime.boxToInteger(deployMode.elem));
         if (var40 != null) {
            String var41 = (String)var40._1();
            int var42 = var40._2$mcI$sp();
            if (var41 == null && this.CLIENT() == var42) {
               args.deployMode_$eq("client");
               BoxedUnit var238 = BoxedUnit.UNIT;
               break label541;
            }
         }

         if (var40 != null) {
            String var44 = (String)var40._1();
            int var45 = var40._2$mcI$sp();
            if (var44 == null && this.CLUSTER() == var45) {
               args.deployMode_$eq("cluster");
               BoxedUnit var237 = BoxedUnit.UNIT;
               break label541;
            }
         }

         BoxedUnit var43 = BoxedUnit.UNIT;
      }

      boolean isYarnCluster = var11 == this.YARN() && deployMode.elem == this.CLUSTER();
      boolean isMesosCluster = var11 == this.MESOS() && deployMode.elem == this.CLUSTER();
      boolean isStandAloneCluster = var11 == this.STANDALONE() && deployMode.elem == this.CLUSTER();
      boolean isKubernetesCluster = var11 == this.KUBERNETES() && deployMode.elem == this.CLUSTER();
      if (!isMesosCluster && !isStandAloneCluster) {
         String resolvedMavenCoordinates = DependencyUtils$.MODULE$.resolveMavenDependencies(args.packagesExclusions(), args.packages(), args.repositories(), args.ivyRepoPath());
         if (!StringUtils.isBlank(resolvedMavenCoordinates)) {
            args.jars_$eq(this.mergeFileLists(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.jars(), resolvedMavenCoordinates}))));
            if (args.isPython()) {
               args.pyFiles_$eq(this.mergeFileLists(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.pyFiles(), resolvedMavenCoordinates}))));
            }
         }

         if (args.isR() && !StringUtils.isBlank(args.jars())) {
            RPackageUtils$.MODULE$.checkAndBuildRPackage(args.jars(), this.printStream(), args.verbose());
         }
      }

      args.sparkProperties().foreach(new Serializable(sparkConf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf sparkConf$2;

         public final SparkConf apply(Tuple2<String, String> x0$1) {
            if (x0$1 != null) {
               String k = (String)x0$1._1();
               String v = (String)x0$1._2();
               SparkConf var5 = this.sparkConf$2.set(k, v);
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.sparkConf$2 = sparkConf$2;
         }
      });
      Configuration hadoopConf = (Configuration)conf.getOrElse(new Serializable(sparkConf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf sparkConf$2;

         public final Configuration apply() {
            return SparkHadoopUtil$.MODULE$.newConfiguration(this.sparkConf$2);
         }

         public {
            this.sparkConf$2 = sparkConf$2;
         }
      });
      File targetDir = Utils$.MODULE$.createTempDir(Utils$.MODULE$.createTempDir$default$1(), Utils$.MODULE$.createTempDir$default$2());
      if ((var11 == this.YARN() || var11 == this.LOCAL() || var11 == this.MESOS()) && args.principal() != null && args.keytab() != null) {
         Predef$.MODULE$.require((new File(args.keytab())).exists(), new Serializable(args) {
            public static final long serialVersionUID = 0L;
            private final SparkSubmitArguments args$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Keytab file: ", " does not exist"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.args$2.keytab()}));
            }

            public {
               this.args$2 = args$2;
            }
         });
         sparkConf.set((OptionalConfigEntry)org.apache.spark.internal.config.package$.MODULE$.KEYTAB(), (Object)args.keytab());
         sparkConf.set((OptionalConfigEntry)org.apache.spark.internal.config.package$.MODULE$.PRINCIPAL(), (Object)args.principal());
         UserGroupInformation.loginUserFromKeytab(args.principal(), args.keytab());
      }

      args.jars_$eq((String)Option$.MODULE$.apply(args.jars()).map(new Serializable(hadoopConf) {
         public static final long serialVersionUID = 0L;
         private final Configuration hadoopConf$1;

         public final String apply(String x$2) {
            return DependencyUtils$.MODULE$.resolveGlobPaths(x$2, this.hadoopConf$1);
         }

         public {
            this.hadoopConf$1 = hadoopConf$1;
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      args.files_$eq((String)Option$.MODULE$.apply(args.files()).map(new Serializable(hadoopConf) {
         public static final long serialVersionUID = 0L;
         private final Configuration hadoopConf$1;

         public final String apply(String x$3) {
            return DependencyUtils$.MODULE$.resolveGlobPaths(x$3, this.hadoopConf$1);
         }

         public {
            this.hadoopConf$1 = hadoopConf$1;
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      args.pyFiles_$eq((String)Option$.MODULE$.apply(args.pyFiles()).map(new Serializable(hadoopConf) {
         public static final long serialVersionUID = 0L;
         private final Configuration hadoopConf$1;

         public final String apply(String x$4) {
            return DependencyUtils$.MODULE$.resolveGlobPaths(x$4, this.hadoopConf$1);
         }

         public {
            this.hadoopConf$1 = hadoopConf$1;
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      args.archives_$eq((String)Option$.MODULE$.apply(args.archives()).map(new Serializable(hadoopConf) {
         public static final long serialVersionUID = 0L;
         private final Configuration hadoopConf$1;

         public final String apply(String x$5) {
            return DependencyUtils$.MODULE$.resolveGlobPaths(x$5, this.hadoopConf$1);
         }

         public {
            this.hadoopConf$1 = hadoopConf$1;
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      String localPrimaryResource = null;
      String localJars = null;
      String localPyFiles = null;
      if (deployMode.elem == this.CLIENT()) {
         localPrimaryResource = (String)Option$.MODULE$.apply(args.primaryResource()).map(new Serializable(sparkConf, hadoopConf, targetDir, secMgr$lzy, bitmap$0) {
            public static final long serialVersionUID = 0L;
            private final SparkConf sparkConf$2;
            private final Configuration hadoopConf$1;
            private final File targetDir$1;
            private final ObjectRef secMgr$lzy$1;
            private final VolatileByteRef bitmap$0$1;

            public final String apply(String x$6) {
               return DependencyUtils$.MODULE$.downloadFile(x$6, this.targetDir$1, this.sparkConf$2, this.hadoopConf$1, SparkSubmit$.MODULE$.org$apache$spark$deploy$SparkSubmit$$secMgr$1(this.sparkConf$2, this.secMgr$lzy$1, this.bitmap$0$1));
            }

            public {
               this.sparkConf$2 = sparkConf$2;
               this.hadoopConf$1 = hadoopConf$1;
               this.targetDir$1 = targetDir$1;
               this.secMgr$lzy$1 = secMgr$lzy$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }).orNull(Predef$.MODULE$.$conforms());
         localJars = (String)Option$.MODULE$.apply(args.jars()).map(new Serializable(sparkConf, hadoopConf, targetDir, secMgr$lzy, bitmap$0) {
            public static final long serialVersionUID = 0L;
            private final SparkConf sparkConf$2;
            private final Configuration hadoopConf$1;
            private final File targetDir$1;
            private final ObjectRef secMgr$lzy$1;
            private final VolatileByteRef bitmap$0$1;

            public final String apply(String x$7) {
               return DependencyUtils$.MODULE$.downloadFileList(x$7, this.targetDir$1, this.sparkConf$2, this.hadoopConf$1, SparkSubmit$.MODULE$.org$apache$spark$deploy$SparkSubmit$$secMgr$1(this.sparkConf$2, this.secMgr$lzy$1, this.bitmap$0$1));
            }

            public {
               this.sparkConf$2 = sparkConf$2;
               this.hadoopConf$1 = hadoopConf$1;
               this.targetDir$1 = targetDir$1;
               this.secMgr$lzy$1 = secMgr$lzy$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }).orNull(Predef$.MODULE$.$conforms());
         localPyFiles = (String)Option$.MODULE$.apply(args.pyFiles()).map(new Serializable(sparkConf, hadoopConf, targetDir, secMgr$lzy, bitmap$0) {
            public static final long serialVersionUID = 0L;
            private final SparkConf sparkConf$2;
            private final Configuration hadoopConf$1;
            private final File targetDir$1;
            private final ObjectRef secMgr$lzy$1;
            private final VolatileByteRef bitmap$0$1;

            public final String apply(String x$8) {
               return DependencyUtils$.MODULE$.downloadFileList(x$8, this.targetDir$1, this.sparkConf$2, this.hadoopConf$1, SparkSubmit$.MODULE$.org$apache$spark$deploy$SparkSubmit$$secMgr$1(this.sparkConf$2, this.secMgr$lzy$1, this.bitmap$0$1));
            }

            public {
               this.sparkConf$2 = sparkConf$2;
               this.hadoopConf$1 = hadoopConf$1;
               this.targetDir$1 = targetDir$1;
               this.secMgr$lzy$1 = secMgr$lzy$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }).orNull(Predef$.MODULE$.$conforms());
      }

      if (var11 == this.YARN()) {
         Seq forceDownloadSchemes = (Seq)sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.FORCE_DOWNLOAD_SCHEMES());
         args.primaryResource_$eq((String)Option$.MODULE$.apply(args.primaryResource()).map(new Serializable(sparkConf, hadoopConf, targetDir, secMgr$lzy, forceDownloadSchemes, bitmap$0) {
            public static final long serialVersionUID = 0L;
            private final SparkConf sparkConf$2;
            private final Configuration hadoopConf$1;
            private final File targetDir$1;
            private final ObjectRef secMgr$lzy$1;
            private final Seq forceDownloadSchemes$1;
            private final VolatileByteRef bitmap$0$1;

            public final String apply(String resource) {
               return SparkSubmit$.MODULE$.org$apache$spark$deploy$SparkSubmit$$downloadResource$1(resource, this.sparkConf$2, this.hadoopConf$1, this.targetDir$1, this.secMgr$lzy$1, this.forceDownloadSchemes$1, this.bitmap$0$1);
            }

            public {
               this.sparkConf$2 = sparkConf$2;
               this.hadoopConf$1 = hadoopConf$1;
               this.targetDir$1 = targetDir$1;
               this.secMgr$lzy$1 = secMgr$lzy$1;
               this.forceDownloadSchemes$1 = forceDownloadSchemes$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }).orNull(Predef$.MODULE$.$conforms()));
         args.files_$eq((String)Option$.MODULE$.apply(args.files()).map(new Serializable(sparkConf, hadoopConf, targetDir, secMgr$lzy, forceDownloadSchemes, bitmap$0) {
            public static final long serialVersionUID = 0L;
            public final SparkConf sparkConf$2;
            public final Configuration hadoopConf$1;
            public final File targetDir$1;
            public final ObjectRef secMgr$lzy$1;
            public final Seq forceDownloadSchemes$1;
            public final VolatileByteRef bitmap$0$1;

            public final String apply(String files) {
               return ((TraversableOnce)Utils$.MODULE$.stringToSeq(files).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply(String resource) {
                     return SparkSubmit$.MODULE$.org$apache$spark$deploy$SparkSubmit$$downloadResource$1(resource, this.$outer.sparkConf$2, this.$outer.hadoopConf$1, this.$outer.targetDir$1, this.$outer.secMgr$lzy$1, this.$outer.forceDownloadSchemes$1, this.$outer.bitmap$0$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, Seq$.MODULE$.canBuildFrom())).mkString(",");
            }

            public {
               this.sparkConf$2 = sparkConf$2;
               this.hadoopConf$1 = hadoopConf$1;
               this.targetDir$1 = targetDir$1;
               this.secMgr$lzy$1 = secMgr$lzy$1;
               this.forceDownloadSchemes$1 = forceDownloadSchemes$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }).orNull(Predef$.MODULE$.$conforms()));
         args.pyFiles_$eq((String)Option$.MODULE$.apply(args.pyFiles()).map(new Serializable(sparkConf, hadoopConf, targetDir, secMgr$lzy, forceDownloadSchemes, bitmap$0) {
            public static final long serialVersionUID = 0L;
            public final SparkConf sparkConf$2;
            public final Configuration hadoopConf$1;
            public final File targetDir$1;
            public final ObjectRef secMgr$lzy$1;
            public final Seq forceDownloadSchemes$1;
            public final VolatileByteRef bitmap$0$1;

            public final String apply(String pyFiles) {
               return ((TraversableOnce)Utils$.MODULE$.stringToSeq(pyFiles).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply(String resource) {
                     return SparkSubmit$.MODULE$.org$apache$spark$deploy$SparkSubmit$$downloadResource$1(resource, this.$outer.sparkConf$2, this.$outer.hadoopConf$1, this.$outer.targetDir$1, this.$outer.secMgr$lzy$1, this.$outer.forceDownloadSchemes$1, this.$outer.bitmap$0$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, Seq$.MODULE$.canBuildFrom())).mkString(",");
            }

            public {
               this.sparkConf$2 = sparkConf$2;
               this.hadoopConf$1 = hadoopConf$1;
               this.targetDir$1 = targetDir$1;
               this.secMgr$lzy$1 = secMgr$lzy$1;
               this.forceDownloadSchemes$1 = forceDownloadSchemes$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }).orNull(Predef$.MODULE$.$conforms()));
         args.jars_$eq((String)Option$.MODULE$.apply(args.jars()).map(new Serializable(sparkConf, hadoopConf, targetDir, secMgr$lzy, forceDownloadSchemes, bitmap$0) {
            public static final long serialVersionUID = 0L;
            public final SparkConf sparkConf$2;
            public final Configuration hadoopConf$1;
            public final File targetDir$1;
            public final ObjectRef secMgr$lzy$1;
            public final Seq forceDownloadSchemes$1;
            public final VolatileByteRef bitmap$0$1;

            public final String apply(String jars) {
               return ((TraversableOnce)Utils$.MODULE$.stringToSeq(jars).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply(String resource) {
                     return SparkSubmit$.MODULE$.org$apache$spark$deploy$SparkSubmit$$downloadResource$1(resource, this.$outer.sparkConf$2, this.$outer.hadoopConf$1, this.$outer.targetDir$1, this.$outer.secMgr$lzy$1, this.$outer.forceDownloadSchemes$1, this.$outer.bitmap$0$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, Seq$.MODULE$.canBuildFrom())).mkString(",");
            }

            public {
               this.sparkConf$2 = sparkConf$2;
               this.hadoopConf$1 = hadoopConf$1;
               this.targetDir$1 = targetDir$1;
               this.secMgr$lzy$1 = secMgr$lzy$1;
               this.forceDownloadSchemes$1 = forceDownloadSchemes$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }).orNull(Predef$.MODULE$.$conforms()));
         args.archives_$eq((String)Option$.MODULE$.apply(args.archives()).map(new Serializable(sparkConf, hadoopConf, targetDir, secMgr$lzy, forceDownloadSchemes, bitmap$0) {
            public static final long serialVersionUID = 0L;
            public final SparkConf sparkConf$2;
            public final Configuration hadoopConf$1;
            public final File targetDir$1;
            public final ObjectRef secMgr$lzy$1;
            public final Seq forceDownloadSchemes$1;
            public final VolatileByteRef bitmap$0$1;

            public final String apply(String archives) {
               return ((TraversableOnce)Utils$.MODULE$.stringToSeq(archives).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply(String resource) {
                     return SparkSubmit$.MODULE$.org$apache$spark$deploy$SparkSubmit$$downloadResource$1(resource, this.$outer.sparkConf$2, this.$outer.hadoopConf$1, this.$outer.targetDir$1, this.$outer.secMgr$lzy$1, this.$outer.forceDownloadSchemes$1, this.$outer.bitmap$0$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, Seq$.MODULE$.canBuildFrom())).mkString(",");
            }

            public {
               this.sparkConf$2 = sparkConf$2;
               this.hadoopConf$1 = hadoopConf$1;
               this.targetDir$1 = targetDir$1;
               this.secMgr$lzy$1 = secMgr$lzy$1;
               this.forceDownloadSchemes$1 = forceDownloadSchemes$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }).orNull(Predef$.MODULE$.$conforms()));
      }

      if (args.isPython() && deployMode.elem == this.CLIENT()) {
         label424: {
            label423: {
               String var239 = args.primaryResource();
               String var57 = this.PYSPARK_SHELL();
               if (var239 == null) {
                  if (var57 == null) {
                     break label423;
                  }
               } else if (var239.equals(var57)) {
                  break label423;
               }

               args.mainClass_$eq("org.apache.spark.deploy.PythonRunner");
               args.childArgs_$eq((ArrayBuffer)((BufferLike)ArrayBuffer$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{localPrimaryResource, localPyFiles})))).$plus$plus(args.childArgs()));
               if (var11 != this.YARN()) {
                  args.files_$eq(this.mergeFileLists(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.files(), args.primaryResource()}))));
               }
               break label424;
            }

            args.mainClass_$eq("org.apache.spark.api.python.PythonGatewayServer");
         }

         if (var11 != this.YARN()) {
            args.files_$eq(this.mergeFileLists(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.files(), args.pyFiles()}))));
         }

         if (localPyFiles == null) {
            BoxedUnit var240 = BoxedUnit.UNIT;
         } else {
            sparkConf.set("spark.submit.pyFiles", localPyFiles);
         }
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      if (args.isR() && var11 == this.YARN()) {
         Option sparkRPackagePath = RUtils$.MODULE$.localSparkRPackagePath();
         if (sparkRPackagePath.isEmpty()) {
            this.printErrorAndExit("SPARK_HOME does not exist for R application in YARN mode.");
         }

         File sparkRPackageFile = new File((String)sparkRPackagePath.get(), this.SPARKR_PACKAGE_ARCHIVE());
         if (!sparkRPackageFile.exists()) {
            this.printErrorAndExit((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " does not exist for R application in YARN mode."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.SPARKR_PACKAGE_ARCHIVE()})));
         }

         String sparkRPackageURI = Utils$.MODULE$.resolveURI(sparkRPackageFile.getAbsolutePath()).toString();
         args.archives_$eq(this.mergeFileLists(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.archives(), (new StringBuilder()).append((Object)sparkRPackageURI).append((Object)"#sparkr").toString()}))));
         if (!RUtils$.MODULE$.rPackages().isEmpty()) {
            File rPackageFile = RPackageUtils$.MODULE$.zipRLibraries(new File((String)RUtils$.MODULE$.rPackages().get()), this.R_PACKAGE_ARCHIVE());
            if (!rPackageFile.exists()) {
               this.printErrorAndExit("Failed to zip all the built R packages.");
            }

            String rPackageURI = Utils$.MODULE$.resolveURI(rPackageFile.getAbsolutePath()).toString();
            args.archives_$eq(this.mergeFileLists(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.archives(), (new StringBuilder()).append((Object)rPackageURI).append((Object)"#rpkg").toString()}))));
         }
      }

      if (args.isR() && var11 == this.STANDALONE() && !RUtils$.MODULE$.rPackages().isEmpty()) {
         this.printErrorAndExit("Distributing R packages with standalone cluster is not supported.");
      }

      if (args.isR() && var11 == this.MESOS() && !RUtils$.MODULE$.rPackages().isEmpty()) {
         this.printErrorAndExit("Distributing R packages with mesos cluster is not supported.");
      }

      if (args.isR() && deployMode.elem == this.CLIENT()) {
         label410: {
            label409: {
               String var241 = args.primaryResource();
               String var63 = this.SPARKR_SHELL();
               if (var241 == null) {
                  if (var63 == null) {
                     break label409;
                  }
               } else if (var241.equals(var63)) {
                  break label409;
               }

               args.mainClass_$eq("org.apache.spark.deploy.RRunner");
               args.childArgs_$eq((ArrayBuffer)((BufferLike)ArrayBuffer$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{localPrimaryResource})))).$plus$plus(args.childArgs()));
               args.files_$eq(this.mergeFileLists(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.files(), args.primaryResource()}))));
               break label410;
            }

            args.mainClass_$eq("org.apache.spark.api.r.RBackend");
         }
      }

      if (isYarnCluster && args.isR()) {
         args.files_$eq(this.mergeFileLists(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.files(), args.primaryResource()}))));
      }

      scala.sys.package$.MODULE$.props().update("SPARK_SUBMIT", "true");
      List$ var242 = List$.MODULE$;
      Predef$ var10001 = Predef$.MODULE$;
      OptionAssigner[] var10002 = new OptionAssigner[31];
      String x$16 = args.master();
      int x$17 = this.ALL_CLUSTER_MGRS();
      int x$18 = this.ALL_DEPLOY_MODES();
      String x$19 = "spark.master";
      String x$20 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[0] = new OptionAssigner(x$16, x$17, x$18, x$20, x$19);
      String x$21 = args.deployMode();
      int x$22 = this.ALL_CLUSTER_MGRS();
      int x$23 = this.ALL_DEPLOY_MODES();
      String x$24 = "spark.submit.deployMode";
      String x$25 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[1] = new OptionAssigner(x$21, x$22, x$23, x$25, x$24);
      String x$26 = args.name();
      int x$27 = this.ALL_CLUSTER_MGRS();
      int x$28 = this.ALL_DEPLOY_MODES();
      String x$29 = "spark.app.name";
      String x$30 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[2] = new OptionAssigner(x$26, x$27, x$28, x$30, x$29);
      String x$31 = args.ivyRepoPath();
      int x$32 = this.ALL_CLUSTER_MGRS();
      int x$33 = this.CLIENT();
      String x$34 = "spark.jars.ivy";
      String x$35 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[3] = new OptionAssigner(x$31, x$32, x$33, x$35, x$34);
      String x$36 = args.driverMemory();
      int x$37 = this.ALL_CLUSTER_MGRS();
      int x$38 = this.CLIENT();
      String x$39 = "spark.driver.memory";
      String x$40 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[4] = new OptionAssigner(x$36, x$37, x$38, x$40, x$39);
      String x$41 = args.driverExtraClassPath();
      int x$42 = this.ALL_CLUSTER_MGRS();
      int x$43 = this.ALL_DEPLOY_MODES();
      String x$44 = "spark.driver.extraClassPath";
      String x$45 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[5] = new OptionAssigner(x$41, x$42, x$43, x$45, x$44);
      String x$46 = args.driverExtraJavaOptions();
      int x$47 = this.ALL_CLUSTER_MGRS();
      int x$48 = this.ALL_DEPLOY_MODES();
      String x$49 = "spark.driver.extraJavaOptions";
      String x$50 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[6] = new OptionAssigner(x$46, x$47, x$48, x$50, x$49);
      String x$51 = args.driverExtraLibraryPath();
      int x$52 = this.ALL_CLUSTER_MGRS();
      int x$53 = this.ALL_DEPLOY_MODES();
      String x$54 = "spark.driver.extraLibraryPath";
      String x$55 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[7] = new OptionAssigner(x$51, x$52, x$53, x$55, x$54);
      String x$56 = args.packages();
      int x$57 = this.STANDALONE() | this.MESOS();
      int x$58 = this.CLUSTER();
      String x$59 = "spark.jars.packages";
      String x$60 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[8] = new OptionAssigner(x$56, x$57, x$58, x$60, x$59);
      String x$61 = args.repositories();
      int x$62 = this.STANDALONE() | this.MESOS();
      int x$63 = this.CLUSTER();
      String x$64 = "spark.jars.repositories";
      String x$65 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[9] = new OptionAssigner(x$61, x$62, x$63, x$65, x$64);
      String x$66 = args.ivyRepoPath();
      int x$67 = this.STANDALONE() | this.MESOS();
      int x$68 = this.CLUSTER();
      String x$69 = "spark.jars.ivy";
      String x$70 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[10] = new OptionAssigner(x$66, x$67, x$68, x$70, x$69);
      String x$71 = args.packagesExclusions();
      int x$72 = this.STANDALONE() | this.MESOS();
      int x$73 = this.CLUSTER();
      String x$74 = "spark.jars.excludes";
      String x$75 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[11] = new OptionAssigner(x$71, x$72, x$73, x$75, x$74);
      String x$76 = args.queue();
      int x$77 = this.YARN();
      int x$78 = this.ALL_DEPLOY_MODES();
      String x$79 = "spark.yarn.queue";
      String x$80 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[12] = new OptionAssigner(x$76, x$77, x$78, x$80, x$79);
      String x$81 = args.numExecutors();
      int x$82 = this.YARN();
      int x$83 = this.ALL_DEPLOY_MODES();
      String x$84 = "spark.executor.instances";
      String x$85 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[13] = new OptionAssigner(x$81, x$82, x$83, x$85, x$84);
      String x$86 = args.pyFiles();
      int x$87 = this.YARN();
      int x$88 = this.ALL_DEPLOY_MODES();
      String x$89 = "spark.yarn.dist.pyFiles";
      String x$90 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[14] = new OptionAssigner(x$86, x$87, x$88, x$90, x$89);
      String x$91 = args.jars();
      int x$92 = this.YARN();
      int x$93 = this.ALL_DEPLOY_MODES();
      String x$94 = "spark.yarn.dist.jars";
      String x$95 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[15] = new OptionAssigner(x$91, x$92, x$93, x$95, x$94);
      String x$96 = args.files();
      int x$97 = this.YARN();
      int x$98 = this.ALL_DEPLOY_MODES();
      String x$99 = "spark.yarn.dist.files";
      String x$100 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[16] = new OptionAssigner(x$96, x$97, x$98, x$100, x$99);
      String x$101 = args.archives();
      int x$102 = this.YARN();
      int x$103 = this.ALL_DEPLOY_MODES();
      String x$104 = "spark.yarn.dist.archives";
      String x$105 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[17] = new OptionAssigner(x$101, x$102, x$103, x$105, x$104);
      String x$106 = args.principal();
      int x$107 = this.YARN();
      int x$108 = this.ALL_DEPLOY_MODES();
      String x$109 = "spark.yarn.principal";
      String x$110 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[18] = new OptionAssigner(x$106, x$107, x$108, x$110, x$109);
      String x$111 = args.keytab();
      int x$112 = this.YARN();
      int x$113 = this.ALL_DEPLOY_MODES();
      String x$114 = "spark.yarn.keytab";
      String x$115 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[19] = new OptionAssigner(x$111, x$112, x$113, x$115, x$114);
      String x$116 = args.executorCores();
      int x$117 = this.STANDALONE() | this.YARN() | this.KUBERNETES();
      int x$118 = this.ALL_DEPLOY_MODES();
      String x$119 = "spark.executor.cores";
      String x$120 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[20] = new OptionAssigner(x$116, x$117, x$118, x$120, x$119);
      String x$121 = args.executorMemory();
      int x$122 = this.STANDALONE() | this.MESOS() | this.YARN() | this.KUBERNETES();
      int x$123 = this.ALL_DEPLOY_MODES();
      String x$124 = "spark.executor.memory";
      String x$125 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[21] = new OptionAssigner(x$121, x$122, x$123, x$125, x$124);
      String x$126 = args.totalExecutorCores();
      int x$127 = this.STANDALONE() | this.MESOS() | this.KUBERNETES();
      int x$128 = this.ALL_DEPLOY_MODES();
      String x$129 = "spark.cores.max";
      String x$130 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[22] = new OptionAssigner(x$126, x$127, x$128, x$130, x$129);
      String x$131 = args.files();
      int x$132 = this.LOCAL() | this.STANDALONE() | this.MESOS() | this.KUBERNETES();
      int x$133 = this.ALL_DEPLOY_MODES();
      String x$134 = "spark.files";
      String x$135 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[23] = new OptionAssigner(x$131, x$132, x$133, x$135, x$134);
      String x$136 = args.jars();
      int x$137 = this.LOCAL();
      int x$138 = this.CLIENT();
      String x$139 = "spark.jars";
      String x$140 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[24] = new OptionAssigner(x$136, x$137, x$138, x$140, x$139);
      String x$141 = args.jars();
      int x$142 = this.STANDALONE() | this.MESOS() | this.KUBERNETES();
      int x$143 = this.ALL_DEPLOY_MODES();
      String x$144 = "spark.jars";
      String x$145 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[25] = new OptionAssigner(x$141, x$142, x$143, x$145, x$144);
      String x$146 = args.driverMemory();
      int x$147 = this.STANDALONE() | this.MESOS() | this.YARN() | this.KUBERNETES();
      int x$148 = this.CLUSTER();
      String x$149 = "spark.driver.memory";
      String x$150 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[26] = new OptionAssigner(x$146, x$147, x$148, x$150, x$149);
      String x$151 = args.driverCores();
      int x$152 = this.STANDALONE() | this.MESOS() | this.YARN() | this.KUBERNETES();
      int x$153 = this.CLUSTER();
      String x$154 = "spark.driver.cores";
      String x$155 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[27] = new OptionAssigner(x$151, x$152, x$153, x$155, x$154);
      String x$156 = BoxesRunTime.boxToBoolean(args.supervise()).toString();
      int x$157 = this.STANDALONE() | this.MESOS();
      int x$158 = this.CLUSTER();
      String x$159 = "spark.driver.supervise";
      String x$160 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[28] = new OptionAssigner(x$156, x$157, x$158, x$160, x$159);
      String x$161 = args.ivyRepoPath();
      int x$162 = this.STANDALONE();
      int x$163 = this.CLUSTER();
      String x$164 = "spark.jars.ivy";
      String x$165 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[29] = new OptionAssigner(x$161, x$162, x$163, x$165, x$164);
      int x$167 = this.ALL_CLUSTER_MGRS();
      int x$168 = this.CLIENT();
      String x$169 = "spark.repl.local.jars";
      String x$170 = OptionAssigner$.MODULE$.apply$default$4();
      var10002[30] = new OptionAssigner(localJars, x$167, x$168, x$170, x$169);
      List options = var242.apply(var10001.wrapRefArray(var10002));
      if (deployMode.elem == this.CLIENT()) {
         childMainClass = args.mainClass();
         if (localPrimaryResource != null && this.isUserJar(localPrimaryResource)) {
            childClasspath.$plus$eq(localPrimaryResource);
         } else {
            BoxedUnit var243 = BoxedUnit.UNIT;
         }

         if (localJars == null) {
            BoxedUnit var244 = BoxedUnit.UNIT;
         } else {
            childClasspath.$plus$plus$eq(Predef$.MODULE$.refArrayOps((Object[])localJars.split(",")));
         }
      } else {
         BoxedUnit var245 = BoxedUnit.UNIT;
      }

      if (isYarnCluster) {
         if (this.isUserJar(args.primaryResource())) {
            childClasspath.$plus$eq(args.primaryResource());
         } else {
            BoxedUnit var246 = BoxedUnit.UNIT;
         }

         if (args.jars() == null) {
            BoxedUnit var247 = BoxedUnit.UNIT;
         } else {
            childClasspath.$plus$plus$eq(Predef$.MODULE$.refArrayOps((Object[])args.jars().split(",")));
         }
      } else {
         BoxedUnit var248 = BoxedUnit.UNIT;
      }

      if (deployMode.elem == this.CLIENT()) {
         if (args.childArgs() == null) {
            BoxedUnit var249 = BoxedUnit.UNIT;
         } else {
            childArgs.$plus$plus$eq(args.childArgs());
         }
      } else {
         BoxedUnit var250 = BoxedUnit.UNIT;
      }

      options.foreach(new Serializable(childArgs, sparkConf, var11, deployMode) {
         public static final long serialVersionUID = 0L;
         private final ArrayBuffer childArgs$2;
         private final SparkConf sparkConf$2;
         private final int clusterManager$1;
         private final IntRef deployMode$1;

         public final Object apply(OptionAssigner opt) {
            Object var10000;
            if (opt.value() != null && (this.deployMode$1.elem & opt.deployMode()) != 0 && (this.clusterManager$1 & opt.clusterManager()) != 0) {
               if (opt.clOption() == null) {
                  BoxedUnit var2 = BoxedUnit.UNIT;
               } else {
                  this.childArgs$2.$plus$eq(opt.clOption(), opt.value(), Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
               }

               var10000 = opt.confKey() == null ? BoxedUnit.UNIT : this.sparkConf$2.set(opt.confKey(), opt.value());
            } else {
               var10000 = BoxedUnit.UNIT;
            }

            return var10000;
         }

         public {
            this.childArgs$2 = childArgs$2;
            this.sparkConf$2 = sparkConf$2;
            this.clusterManager$1 = clusterManager$1;
            this.deployMode$1 = deployMode$1;
         }
      });
      if (this.isShell(args.primaryResource()) && !sparkConf.contains(org.apache.spark.internal.config.package$.MODULE$.UI_SHOW_CONSOLE_PROGRESS())) {
         sparkConf.set((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.UI_SHOW_CONSOLE_PROGRESS(), (Object)BoxesRunTime.boxToBoolean(true));
      } else {
         BoxedUnit var251 = BoxedUnit.UNIT;
      }

      if (!isYarnCluster && !args.isPython() && !args.isR()) {
         Seq jars = (Seq)sparkConf.getOption("spark.jars").map(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final Seq<String> apply(String x) {
               return Predef$.MODULE$.refArrayOps((Object[])x.split(",")).toSeq();
            }
         }).getOrElse(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final Seq<Nothing$> apply() {
               return (Seq)Seq$.MODULE$.empty();
            }
         });
         if (this.isUserJar(args.primaryResource())) {
            jars = (Seq)jars.$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.primaryResource()}))), Seq$.MODULE$.canBuildFrom());
         }

         sparkConf.set("spark.jars", jars.mkString(","));
      } else {
         BoxedUnit var252 = BoxedUnit.UNIT;
      }

      if (args.isStandaloneCluster()) {
         if (args.useRest()) {
            childMainClass = this.REST_CLUSTER_SUBMIT_CLASS();
            childArgs.$plus$eq(args.primaryResource(), args.mainClass(), Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
         } else {
            childMainClass = this.STANDALONE_CLUSTER_SUBMIT_CLASS();
            if (args.supervise()) {
               childArgs.$plus$eq("--supervise");
            } else {
               BoxedUnit var253 = BoxedUnit.UNIT;
            }

            Option$.MODULE$.apply(args.driverMemory()).foreach(new Serializable(childArgs) {
               public static final long serialVersionUID = 0L;
               private final ArrayBuffer childArgs$2;

               public final ArrayBuffer<String> apply(String m) {
                  return (ArrayBuffer)this.childArgs$2.$plus$eq("--memory", m, Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
               }

               public {
                  this.childArgs$2 = childArgs$2;
               }
            });
            Option$.MODULE$.apply(args.driverCores()).foreach(new Serializable(childArgs) {
               public static final long serialVersionUID = 0L;
               private final ArrayBuffer childArgs$2;

               public final ArrayBuffer<String> apply(String c) {
                  return (ArrayBuffer)this.childArgs$2.$plus$eq("--cores", c, Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
               }

               public {
                  this.childArgs$2 = childArgs$2;
               }
            });
            childArgs.$plus$eq("launch");
            childArgs.$plus$eq(args.master(), args.primaryResource(), Predef$.MODULE$.wrapRefArray((Object[])(new String[]{args.mainClass()})));
         }

         if (args.childArgs() == null) {
            BoxedUnit var254 = BoxedUnit.UNIT;
         } else {
            childArgs.$plus$plus$eq(args.childArgs());
         }
      } else {
         BoxedUnit var255 = BoxedUnit.UNIT;
      }

      if (var11 == this.YARN()) {
         if (args.isPython()) {
            sparkConf.set("spark.yarn.isPython", "true");
         } else {
            BoxedUnit var256 = BoxedUnit.UNIT;
         }
      } else {
         BoxedUnit var257 = BoxedUnit.UNIT;
      }

      if (var11 == this.MESOS() && UserGroupInformation.isSecurityEnabled()) {
         this.setRMPrincipal(sparkConf);
      }

      if (isYarnCluster) {
         childMainClass = this.YARN_CLUSTER_SUBMIT_CLASS();
         if (args.isPython()) {
            childArgs.$plus$eq("--primary-py-file", args.primaryResource(), Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
            childArgs.$plus$eq("--class", "org.apache.spark.deploy.PythonRunner", Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
         } else if (args.isR()) {
            String mainFile = (new Path(args.primaryResource())).getName();
            childArgs.$plus$eq("--primary-r-file", mainFile, Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
            childArgs.$plus$eq("--class", "org.apache.spark.deploy.RRunner", Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
         } else {
            label375: {
               label374: {
                  String var258 = args.primaryResource();
                  String var222 = "spark-internal";
                  if (var258 == null) {
                     if (var222 == null) {
                        break label374;
                     }
                  } else if (var258.equals(var222)) {
                     break label374;
                  }

                  childArgs.$plus$eq("--jar", args.primaryResource(), Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
                  break label375;
               }

               BoxedUnit var259 = BoxedUnit.UNIT;
            }

            childArgs.$plus$eq("--class", args.mainClass(), Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
         }

         if (args.childArgs() != null) {
            args.childArgs().foreach(new Serializable(childArgs) {
               public static final long serialVersionUID = 0L;
               private final ArrayBuffer childArgs$2;

               public final ArrayBuffer<String> apply(String arg) {
                  return (ArrayBuffer)this.childArgs$2.$plus$eq("--arg", arg, Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
               }

               public {
                  this.childArgs$2 = childArgs$2;
               }
            });
         }
      }

      if (isMesosCluster) {
         Predef$.MODULE$.assert(args.useRest(), new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Mesos cluster mode is only supported through the REST submission API";
            }
         });
         childMainClass = this.REST_CLUSTER_SUBMIT_CLASS();
         if (args.isPython()) {
            childArgs.$plus$eq(args.primaryResource(), "", Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
            if (args.pyFiles() == null) {
               BoxedUnit var260 = BoxedUnit.UNIT;
            } else {
               sparkConf.set("spark.submit.pyFiles", args.pyFiles());
            }
         } else if (args.isR()) {
            childArgs.$plus$eq(args.primaryResource(), "", Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
         } else {
            childArgs.$plus$eq(args.primaryResource(), args.mainClass(), Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
         }

         if (args.childArgs() == null) {
            BoxedUnit var261 = BoxedUnit.UNIT;
         } else {
            childArgs.$plus$plus$eq(args.childArgs());
         }
      } else {
         BoxedUnit var262 = BoxedUnit.UNIT;
      }

      if (isKubernetesCluster) {
         label362: {
            label361: {
               childMainClass = this.KUBERNETES_CLUSTER_SUBMIT_CLASS();
               String var263 = args.primaryResource();
               String var223 = "spark-internal";
               if (var263 == null) {
                  if (var223 == null) {
                     break label361;
                  }
               } else if (var263.equals(var223)) {
                  break label361;
               }

               childArgs.$plus$plus$eq(Predef$.MODULE$.refArrayOps((Object[])(new String[]{"--primary-java-resource", args.primaryResource()})));
               break label362;
            }

            BoxedUnit var264 = BoxedUnit.UNIT;
         }

         childArgs.$plus$plus$eq(Predef$.MODULE$.refArrayOps((Object[])(new String[]{"--main-class", args.mainClass()})));
         if (args.childArgs() != null) {
            args.childArgs().foreach(new Serializable(childArgs) {
               public static final long serialVersionUID = 0L;
               private final ArrayBuffer childArgs$2;

               public final ArrayBuffer<String> apply(String arg) {
                  return (ArrayBuffer)this.childArgs$2.$plus$eq("--arg", arg, Predef$.MODULE$.wrapRefArray((Object[])(new String[0])));
               }

               public {
                  this.childArgs$2 = childArgs$2;
               }
            });
         }
      }

      args.sparkProperties().withFilter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(sparkConf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf sparkConf$2;

         public final SparkConf apply(Tuple2<String, String> x$9) {
            if (x$9 != null) {
               String k = (String)x$9._1();
               String v = (String)x$9._2();
               SparkConf var5 = this.sparkConf$2.setIfMissing(k, v);
               return var5;
            } else {
               throw new MatchError(x$9);
            }
         }

         public {
            this.sparkConf$2 = sparkConf$2;
         }
      });
      if (deployMode.elem == this.CLUSTER()) {
         sparkConf.remove("spark.driver.host");
      } else {
         BoxedUnit var265 = BoxedUnit.UNIT;
      }

      Seq pathConfigs = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.jars", "spark.files", "spark.yarn.dist.files", "spark.yarn.dist.archives", "spark.yarn.dist.jars"})));
      pathConfigs.foreach(new Serializable(sparkConf) {
         public static final long serialVersionUID = 0L;
         public final SparkConf sparkConf$2;

         public final void apply(String config) {
            this.sparkConf$2.getOption(config).foreach(new Serializable(this, config) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final String config$1;

               public final SparkConf apply(String oldValue) {
                  return this.$outer.sparkConf$2.set(this.config$1, Utils$.MODULE$.resolveURIs(oldValue));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.config$1 = config$1;
                  }
               }
            });
         }

         public {
            this.sparkConf$2 = sparkConf$2;
         }
      });
      sparkConf.getOption("spark.submit.pyFiles").foreach(new Serializable(sparkConf, isYarnCluster, isMesosCluster) {
         public static final long serialVersionUID = 0L;
         private final SparkConf sparkConf$2;
         private final boolean isYarnCluster$1;
         private final boolean isMesosCluster$1;

         public final SparkConf apply(String pyFiles) {
            String resolvedPyFiles = Utils$.MODULE$.resolveURIs(pyFiles);
            String formattedPyFiles = !this.isYarnCluster$1 && !this.isMesosCluster$1 ? Predef$.MODULE$.refArrayOps((Object[])PythonRunner$.MODULE$.formatPaths(resolvedPyFiles, PythonRunner$.MODULE$.formatPaths$default$2())).mkString(",") : resolvedPyFiles;
            return this.sparkConf$2.set("spark.submit.pyFiles", formattedPyFiles);
         }

         public {
            this.sparkConf$2 = sparkConf$2;
            this.isYarnCluster$1 = isYarnCluster$1;
            this.isMesosCluster$1 = isMesosCluster$1;
         }
      });
      return new Tuple4<Seq<String>, Seq<String>, SparkConf, String>(childArgs, childClasspath, sparkConf, childMainClass);
   }

   public Option<Configuration> prepareSubmitEnvironment$default$2() {
      return None$.MODULE$;
   }

   private void setRMPrincipal(SparkConf sparkConf) {
      String shortUserName = UserGroupInformation.getCurrentUser().getShortUserName();
      String key = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.hadoop.", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{"yarn.resourcemanager.principal"}));
      this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Setting ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{key, shortUserName})));
      sparkConf.set(key, shortUserName);
   }

   public void org$apache$spark$deploy$SparkSubmit$$runMain(Seq<String> childArgs, Seq<String> childClasspath, SparkConf sparkConf, String childMainClass, boolean verbose) {
      if (verbose) {
         this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Main class:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{childMainClass})));
         this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Arguments:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{childArgs.mkString("\n")})));
         this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Spark config:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.redact(Predef$.MODULE$.refArrayOps(sparkConf.getAll()).toMap(Predef$.MODULE$.$conforms())).mkString("\n")})));
         this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Classpath elements:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{childClasspath.mkString("\n")})));
         this.printStream().println("\n");
      }

      MutableURLClassLoader loader = (MutableURLClassLoader)(BoxesRunTime.unboxToBoolean(sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.DRIVER_USER_CLASS_PATH_FIRST())) ? new ChildFirstURLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader()) : new MutableURLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader()));
      Thread.currentThread().setContextClassLoader(loader);
      childClasspath.foreach(new Serializable(loader) {
         public static final long serialVersionUID = 0L;
         private final MutableURLClassLoader loader$1;

         public final void apply(String jar) {
            SparkSubmit$.MODULE$.addJarToClasspath(jar, this.loader$1);
         }

         public {
            this.loader$1 = loader$1;
         }
      });
      Class mainClass = null;

      try {
         mainClass = Utils$.MODULE$.classForName(childMainClass);
      } catch (ClassNotFoundException var21) {
         var21.printStackTrace(this.printStream());
         if (childMainClass.contains("thriftserver")) {
            this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to load main class ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{childMainClass})));
            this.printStream().println("You need to build Spark with -Phive and -Phive-thriftserver.");
         }

         System.exit(this.CLASS_NOT_FOUND_EXIT_STATUS());
      } catch (NoClassDefFoundError var22) {
         var22.printStackTrace(this.printStream());
         if (var22.getMessage().contains("org/apache/hadoop/hive")) {
            this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to load hive class."})))).s(Nil$.MODULE$));
            this.printStream().println("You need to build Spark with -Phive and -Phive-thriftserver.");
         }

         System.exit(this.CLASS_NOT_FOUND_EXIT_STATUS());
      }

      Object var10000;
      if (SparkApplication.class.isAssignableFrom(mainClass)) {
         var10000 = (SparkApplication)mainClass.newInstance();
      } else {
         if (App.class.isAssignableFrom(mainClass)) {
            this.printWarning("Subclasses of scala.App may not work correctly. Use a main() method instead.");
         }

         var10000 = new JavaMainApplication(mainClass);
      }

      SparkApplication app = (SparkApplication)var10000;

      try {
         app.start((String[])childArgs.toArray(ClassTag$.MODULE$.apply(String.class)), sparkConf);
      } catch (Throwable var20) {
         Throwable var12 = this.findCause$1(var20);
         if (!(var12 instanceof SparkUserAppException)) {
            if (var12 != null) {
               throw var12;
            }

            throw new MatchError(var12);
         }

         SparkUserAppException var13 = (SparkUserAppException)var12;
         int exitCode = var13.exitCode();
         System.exit(exitCode);
         BoxedUnit var15 = BoxedUnit.UNIT;
         return;
      }

   }

   public void addJarToClasspath(String localJar, MutableURLClassLoader loader) {
      URI uri = Utils$.MODULE$.resolveURI(localJar);
      String var4 = uri.getScheme();
      boolean var5;
      if ("file".equals(var4)) {
         var5 = true;
      } else if ("local".equals(var4)) {
         var5 = true;
      } else {
         var5 = false;
      }

      if (var5) {
         File file = new File(uri.getPath());
         if (file.exists()) {
            loader.addURL(file.toURI().toURL());
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            this.printWarning((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Local jar ", " does not exist, skipping."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{file})));
            BoxedUnit var8 = BoxedUnit.UNIT;
         }
      } else {
         this.printWarning((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Skip remote jar ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{uri})));
         BoxedUnit var6 = BoxedUnit.UNIT;
      }

   }

   public boolean isUserJar(String res) {
      return !this.isShell(res) && !this.isPython(res) && !this.isInternal(res) && !this.isR(res);
   }

   public boolean isShell(String res) {
      boolean var10000;
      label40: {
         String var2 = this.SPARK_SHELL();
         if (res == null) {
            if (var2 == null) {
               break label40;
            }
         } else if (res.equals(var2)) {
            break label40;
         }

         String var3 = this.PYSPARK_SHELL();
         if (res == null) {
            if (var3 == null) {
               break label40;
            }
         } else if (res.equals(var3)) {
            break label40;
         }

         String var4 = this.SPARKR_SHELL();
         if (res == null) {
            if (var4 == null) {
               break label40;
            }
         } else if (res.equals(var4)) {
            break label40;
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   public boolean isSqlShell(String mainClass) {
      boolean var10000;
      label23: {
         String var2 = "org.apache.spark.sql.hive.thriftserver.SparkSQLCLIDriver";
         if (mainClass == null) {
            if (var2 == null) {
               break label23;
            }
         } else if (mainClass.equals(var2)) {
            break label23;
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   private boolean isThriftServer(String mainClass) {
      boolean var10000;
      label23: {
         String var2 = "org.apache.spark.sql.hive.thriftserver.HiveThriftServer2";
         if (mainClass == null) {
            if (var2 == null) {
               break label23;
            }
         } else if (mainClass.equals(var2)) {
            break label23;
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   public boolean isPython(String res) {
      boolean var10000;
      if (res == null || !res.endsWith(".py")) {
         label31: {
            String var2 = this.PYSPARK_SHELL();
            if (res == null) {
               if (var2 == null) {
                  break label31;
               }
            } else if (res.equals(var2)) {
               break label31;
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public boolean isR(String res) {
      boolean var10000;
      if (res == null || !res.endsWith(".R")) {
         label31: {
            String var2 = this.SPARKR_SHELL();
            if (res == null) {
               if (var2 == null) {
                  break label31;
               }
            } else if (res.equals(var2)) {
               break label31;
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public boolean isInternal(String res) {
      boolean var10000;
      label23: {
         String var2 = "spark-internal";
         if (res == null) {
            if (var2 == null) {
               break label23;
            }
         } else if (res.equals(var2)) {
            break label23;
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   public String mergeFileLists(Seq<String> lists) {
      String var10000;
      label23: {
         String merged = ((TraversableOnce)((TraversableLike)lists.filterNot(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final boolean apply(CharSequence x$1) {
               return StringUtils.isBlank(x$1);
            }
         })).flatMap(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final ArrayOps<String> apply(String x$10) {
               return Predef$.MODULE$.<String>refArrayOps((Object[])x$10.split(","));
            }
         }, Seq$.MODULE$.canBuildFrom())).mkString(",");
         String var3 = "";
         if (merged == null) {
            if (var3 == null) {
               break label23;
            }
         } else if (merged.equals(var3)) {
            break label23;
         }

         var10000 = merged;
         return var10000;
      }

      var10000 = null;
      return var10000;
   }

   private final void doRunMain$1(SparkSubmitArguments args$1, Seq childArgs$1, Seq childClasspath$1, SparkConf sparkConf$1, String childMainClass$1) {
      if (args$1.proxyUser() == null) {
         this.org$apache$spark$deploy$SparkSubmit$$runMain(childArgs$1, childClasspath$1, sparkConf$1, childMainClass$1, args$1.verbose());
      } else {
         UserGroupInformation proxyUser = UserGroupInformation.createProxyUser(args$1.proxyUser(), UserGroupInformation.getCurrentUser());

         try {
            proxyUser.doAs(new PrivilegedExceptionAction<BoxedUnit>(args$1, childArgs$1, childClasspath$1, sparkConf$1, childMainClass$1) {
               private final SparkSubmitArguments args$1;
               private final Seq childArgs$1;
               private final Seq childClasspath$1;
               private final SparkConf sparkConf$1;
               private final String childMainClass$1;

               public void run() {
                  SparkSubmit$.MODULE$.org$apache$spark$deploy$SparkSubmit$$runMain(this.childArgs$1, this.childClasspath$1, this.sparkConf$1, this.childMainClass$1, this.args$1.verbose());
               }

               public {
                  this.args$1 = args$1;
                  this.childArgs$1 = childArgs$1;
                  this.childClasspath$1 = childClasspath$1;
                  this.sparkConf$1 = sparkConf$1;
                  this.childMainClass$1 = childMainClass$1;
               }
            });
         } catch (Exception var8) {
            if (var8.getStackTrace().length != 0) {
               throw var8;
            }

            this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ERROR: ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{var8.getClass().getName(), var8.getMessage()})));
            this.exitFn().apply$mcVI$sp(1);
         }
      }

   }

   private final SecurityManager secMgr$lzycompute$1(SparkConf sparkConf$2, ObjectRef secMgr$lzy$1, VolatileByteRef bitmap$0$1) {
      synchronized(this) {
         if ((byte)(bitmap$0$1.elem & 1) == 0) {
            secMgr$lzy$1.elem = (T)(new SecurityManager(sparkConf$2, SecurityManager$.MODULE$.$lessinit$greater$default$2()));
            bitmap$0$1.elem = (byte)(bitmap$0$1.elem | 1);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return (SecurityManager)secMgr$lzy$1.elem;
   }

   public final SecurityManager org$apache$spark$deploy$SparkSubmit$$secMgr$1(SparkConf sparkConf$2, ObjectRef secMgr$lzy$1, VolatileByteRef bitmap$0$1) {
      return (byte)(bitmap$0$1.elem & 1) == 0 ? this.secMgr$lzycompute$1(sparkConf$2, secMgr$lzy$1, bitmap$0$1) : (SecurityManager)secMgr$lzy$1.elem;
   }

   private final boolean shouldDownload$1(String scheme, Configuration hadoopConf$1, Seq forceDownloadSchemes$1) {
      return forceDownloadSchemes$1.contains(scheme) || Try$.MODULE$.apply(new Serializable(hadoopConf$1, scheme) {
         public static final long serialVersionUID = 0L;
         private final Configuration hadoopConf$1;
         private final String scheme$1;

         public final Class<FileSystem> apply() {
            return FileSystem.getFileSystemClass(this.scheme$1, this.hadoopConf$1);
         }

         public {
            this.hadoopConf$1 = hadoopConf$1;
            this.scheme$1 = scheme$1;
         }
      }).isFailure();
   }

   public final String org$apache$spark$deploy$SparkSubmit$$downloadResource$1(String resource, SparkConf sparkConf$2, Configuration hadoopConf$1, File targetDir$1, ObjectRef secMgr$lzy$1, Seq forceDownloadSchemes$1, VolatileByteRef bitmap$0$1) {
      URI uri = Utils$.MODULE$.resolveURI(resource);
      String var9 = uri.getScheme();
      boolean var10;
      if ("local".equals(var9)) {
         var10 = true;
      } else if ("file".equals(var9)) {
         var10 = true;
      } else {
         var10 = false;
      }

      String var11;
      if (var10) {
         var11 = resource;
      } else if (this.shouldDownload$1(var9, hadoopConf$1, forceDownloadSchemes$1)) {
         File file = new File(targetDir$1, (new Path(uri)).getName());
         var11 = file.exists() ? file.toURI().toString() : DependencyUtils$.MODULE$.downloadFile(resource, targetDir$1, sparkConf$2, hadoopConf$1, this.org$apache$spark$deploy$SparkSubmit$$secMgr$1(sparkConf$2, secMgr$lzy$1, bitmap$0$1));
      } else {
         var11 = uri.toString();
      }

      return var11;
   }

   private final Throwable findCause$1(Throwable t) {
      while(true) {
         Object var5;
         if (t instanceof UndeclaredThrowableException) {
            UndeclaredThrowableException var4 = (UndeclaredThrowableException)t;
            if (var4.getCause() != null) {
               t = var4.getCause();
               continue;
            }

            var5 = var4;
         } else if (t instanceof InvocationTargetException) {
            InvocationTargetException var6 = (InvocationTargetException)t;
            if (var6.getCause() != null) {
               t = var6.getCause();
               continue;
            }

            var5 = var6;
         } else {
            if (t == null) {
               throw new MatchError(t);
            }

            var5 = t;
         }

         return (Throwable)var5;
      }
   }

   private SparkSubmit$() {
      MODULE$ = this;
      CommandLineUtils$class.$init$(this);
      Logging$class.$init$(this);
      this.YARN = 1;
      this.STANDALONE = 2;
      this.MESOS = 4;
      this.LOCAL = 8;
      this.KUBERNETES = 16;
      this.ALL_CLUSTER_MGRS = this.YARN() | this.STANDALONE() | this.MESOS() | this.LOCAL() | this.KUBERNETES();
      this.CLIENT = 1;
      this.CLUSTER = 2;
      this.ALL_DEPLOY_MODES = this.CLIENT() | this.CLUSTER();
      this.SPARK_SHELL = "spark-shell";
      this.PYSPARK_SHELL = "pyspark-shell";
      this.SPARKR_SHELL = "sparkr-shell";
      this.SPARKR_PACKAGE_ARCHIVE = "sparkr.zip";
      this.R_PACKAGE_ARCHIVE = "rpkg.zip";
      this.CLASS_NOT_FOUND_EXIT_STATUS = 101;
      this.YARN_CLUSTER_SUBMIT_CLASS = "org.apache.spark.deploy.yarn.YarnClusterApplication";
      this.REST_CLUSTER_SUBMIT_CLASS = RestSubmissionClientApp.class.getName();
      this.STANDALONE_CLUSTER_SUBMIT_CLASS = ClientApp.class.getName();
      this.KUBERNETES_CLUSTER_SUBMIT_CLASS = "org.apache.spark.deploy.k8s.submit.KubernetesClientApplication";
   }
}
