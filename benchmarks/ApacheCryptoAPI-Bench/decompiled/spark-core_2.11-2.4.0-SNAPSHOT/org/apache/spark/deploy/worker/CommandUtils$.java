package org.apache.spark.deploy.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.deploy.Command;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.launcher.WorkerCommandBuilder;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.Map;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.BufferLike;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.sys.package$;

public final class CommandUtils$ implements Logging {
   public static final CommandUtils$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new CommandUtils$();
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

   public ProcessBuilder buildProcessBuilder(Command command, SecurityManager securityMgr, int memory, String sparkHome, Function1<String, String> substituteArguments, Seq<String> classPaths, Map<String, String> env) {
      Command localCommand = this.buildLocalCommand(command, securityMgr, substituteArguments, classPaths, env);
      Seq commandSeq = this.buildCommandSeq(localCommand, memory, sparkHome);
      ProcessBuilder builder = new ProcessBuilder((String[])commandSeq.toArray(ClassTag$.MODULE$.apply(String.class)));
      java.util.Map environment = builder.environment();
      localCommand.environment().withFilter(new Serializable() {
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
      }).foreach(new Serializable(environment) {
         public static final long serialVersionUID = 0L;
         private final java.util.Map environment$1;

         public final String apply(Tuple2<String, String> x$1) {
            if (x$1 != null) {
               String key = (String)x$1._1();
               String value = (String)x$1._2();
               String var5 = (String)this.environment$1.put(key, value);
               return var5;
            } else {
               throw new MatchError(x$1);
            }
         }

         public {
            this.environment$1 = environment$1;
         }
      });
      return builder;
   }

   public Seq<String> buildProcessBuilder$default$6() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public Map<String, String> buildProcessBuilder$default$7() {
      return package$.MODULE$.env();
   }

   private Seq<String> buildCommandSeq(Command command, int memory, String sparkHome) {
      List cmd = (new WorkerCommandBuilder(sparkHome, memory, command)).buildCommand();
      return ((BufferLike)JavaConverters$.MODULE$.asScalaBufferConverter(cmd).asScala()).$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{command.mainClass()})))).$plus$plus(command.arguments());
   }

   private Command buildLocalCommand(Command command, SecurityManager securityMgr, Function1<String, String> substituteArguments, Seq<String> classPath, Map<String, String> env) {
      String libraryPathName = Utils$.MODULE$.libraryPathEnvName();
      Seq libraryPathEntries = command.libraryPathEntries();
      Option cmdLibraryPath = command.environment().get(libraryPathName);
      Map var10000;
      if (libraryPathEntries.nonEmpty() && (new StringOps(Predef$.MODULE$.augmentString(libraryPathName))).nonEmpty()) {
         Seq libraryPaths = (Seq)((TraversableLike)libraryPathEntries.$plus$plus(Option$.MODULE$.option2Iterable(cmdLibraryPath), Seq$.MODULE$.canBuildFrom())).$plus$plus(Option$.MODULE$.option2Iterable(env.get(libraryPathName)), Seq$.MODULE$.canBuildFrom());
         var10000 = command.environment().$plus(new Tuple2(libraryPathName, libraryPaths.mkString(File.pathSeparator)));
      } else {
         var10000 = command.environment();
      }

      Map newEnvironment = var10000;
      if (securityMgr.isAuthenticationEnabled()) {
         newEnvironment = newEnvironment.$plus(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(SecurityManager$.MODULE$.ENV_AUTH_SECRET()), securityMgr.getSecretKey()));
      }

      return new Command(command.mainClass(), (Seq)command.arguments().map(substituteArguments, Seq$.MODULE$.canBuildFrom()), newEnvironment, (Seq)command.classPathEntries().$plus$plus(classPath, Seq$.MODULE$.canBuildFrom()), (Seq)Seq$.MODULE$.empty(), (Seq)command.javaOpts().filterNot(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$2) {
            return x$2.startsWith((new StringBuilder()).append((Object)"-D").append((Object)SecurityManager$.MODULE$.SPARK_AUTH_SECRET_CONF()).toString());
         }
      }));
   }

   private Seq<String> buildLocalCommand$default$4() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public void redirectStream(InputStream in, File file) {
      FileOutputStream out = new FileOutputStream(file, true);
      (new Thread(in, file, out) {
         private final InputStream in$1;
         public final File file$1;
         private final FileOutputStream out$1;

         public void run() {
            try {
               Utils$.MODULE$.copyStream(this.in$1, this.out$1, true, Utils$.MODULE$.copyStream$default$4());
            } catch (IOException var2) {
               CommandUtils$.MODULE$.logInfo(new Serializable(this, var2) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final IOException e$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Redirection to ").append((Object)this.$outer.file$1).append((Object)" closed: ").append((Object)this.e$1.getMessage()).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.e$1 = e$1;
                     }
                  }
               });
            }

         }

         public {
            this.in$1 = in$1;
            this.file$1 = file$1;
            this.out$1 = out$1;
         }
      }).start();
   }

   private CommandUtils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
