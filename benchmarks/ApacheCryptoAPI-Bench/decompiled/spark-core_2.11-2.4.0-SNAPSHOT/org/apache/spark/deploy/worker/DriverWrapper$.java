package org.apache.spark.deploy.worker;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.DependencyUtils$;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.deploy.SparkSubmit$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcEnv$;
import org.apache.spark.util.ChildFirstURLClassLoader;
import org.apache.spark.util.MutableURLClassLoader;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple4;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.immutable.$colon$colon;
import scala.collection.immutable.List;
import scala.collection.immutable.StringOps;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.sys.package$;

public final class DriverWrapper$ implements Logging {
   public static final DriverWrapper$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new DriverWrapper$();
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

   public void main(String[] args) {
      List var2 = Predef$.MODULE$.refArrayOps((Object[])args).toList();
      if (var2 instanceof $colon$colon) {
         $colon$colon var3 = ($colon$colon)var2;
         String workerUrl = (String)var3.head();
         List var5 = var3.tl$1();
         if (var5 instanceof $colon$colon) {
            $colon$colon var6 = ($colon$colon)var5;
            String userJar = (String)var6.head();
            List var8 = var6.tl$1();
            if (var8 instanceof $colon$colon) {
               $colon$colon var9 = ($colon$colon)var8;
               String mainClass = (String)var9.head();
               List extraArgs = var9.tl$1();
               SparkConf conf = new SparkConf();
               String host = Utils$.MODULE$.localHostName();
               int port = (new StringOps(Predef$.MODULE$.augmentString((String)package$.MODULE$.props().getOrElse("spark.driver.port", new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "0";
                  }
               })))).toInt();
               RpcEnv rpcEnv = RpcEnv$.MODULE$.create("Driver", host, port, conf, new SecurityManager(conf, SecurityManager$.MODULE$.$lessinit$greater$default$2()), RpcEnv$.MODULE$.create$default$6());
               this.logInfo(new Serializable(rpcEnv) {
                  public static final long serialVersionUID = 0L;
                  private final RpcEnv rpcEnv$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver address: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rpcEnv$1.address()}));
                  }

                  public {
                     this.rpcEnv$1 = rpcEnv$1;
                  }
               });
               rpcEnv.setupEndpoint("workerWatcher", new WorkerWatcher(rpcEnv, workerUrl, WorkerWatcher$.MODULE$.$lessinit$greater$default$3()));
               ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
               URL userJarUrl = (new File(userJar)).toURI().toURL();
               MutableURLClassLoader loader = (MutableURLClassLoader)((new StringOps(Predef$.MODULE$.augmentString((String)package$.MODULE$.props().getOrElse("spark.driver.userClassPathFirst", new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "false";
                  }
               })))).toBoolean() ? new ChildFirstURLClassLoader((URL[])((Object[])(new URL[]{userJarUrl})), currentLoader) : new MutableURLClassLoader((URL[])((Object[])(new URL[]{userJarUrl})), currentLoader));
               Thread.currentThread().setContextClassLoader(loader);
               this.setupDependencies(loader, userJar);
               Class clazz = Utils$.MODULE$.classForName(mainClass);
               Method mainMethod = clazz.getMethod("main", String[].class);
               mainMethod.invoke((Object)null, extraArgs.toArray(ClassTag$.MODULE$.apply(String.class)));
               rpcEnv.shutdown();
               BoxedUnit var22 = BoxedUnit.UNIT;
               return;
            }
         }
      }

      System.err.println("Usage: DriverWrapper <workerUrl> <userJar> <driverMainClass> [options]");
      System.exit(-1);
      BoxedUnit var12 = BoxedUnit.UNIT;
   }

   private void setupDependencies(MutableURLClassLoader loader, String userJar) {
      SparkConf sparkConf = new SparkConf();
      SecurityManager secMgr = new SecurityManager(sparkConf, SecurityManager$.MODULE$.$lessinit$greater$default$2());
      Configuration hadoopConf = SparkHadoopUtil$.MODULE$.newConfiguration(sparkConf);
      Seq var7 = (Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.jars.excludes", "spark.jars.packages", "spark.jars.repositories", "spark.jars.ivy"})))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$1) {
            return (String)package$.MODULE$.props().get(x$1).orNull(Predef$.MODULE$.$conforms());
         }
      }, Seq$.MODULE$.canBuildFrom());
      Some var8 = Seq$.MODULE$.unapplySeq(var7);
      if (!var8.isEmpty() && var8.get() != null && ((SeqLike)var8.get()).lengthCompare(4) == 0) {
         String packagesExclusions = (String)((SeqLike)var8.get()).apply(0);
         String packages = (String)((SeqLike)var8.get()).apply(1);
         String repositories = (String)((SeqLike)var8.get()).apply(2);
         String ivyRepoPath = (String)((SeqLike)var8.get()).apply(3);
         Tuple4 var13 = new Tuple4(packagesExclusions, packages, repositories, ivyRepoPath);
         String packagesExclusions = (String)var13._1();
         String packages = (String)var13._2();
         String repositories = (String)var13._3();
         String ivyRepoPath = (String)var13._4();
         String resolvedMavenCoordinates = DependencyUtils$.MODULE$.resolveMavenDependencies(packagesExclusions, packages, repositories, ivyRepoPath);
         String jarsProp = (String)package$.MODULE$.props().get("spark.jars").orNull(Predef$.MODULE$.$conforms());
         String jars = StringUtils.isBlank(resolvedMavenCoordinates) ? jarsProp : SparkSubmit$.MODULE$.mergeFileLists(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{jarsProp, resolvedMavenCoordinates})));
         String localJars = DependencyUtils$.MODULE$.resolveAndDownloadJars(jars, userJar, sparkConf, hadoopConf, secMgr);
         DependencyUtils$.MODULE$.addJarsToClassPath(localJars, loader);
      } else {
         throw new MatchError(var7);
      }
   }

   private DriverWrapper$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
