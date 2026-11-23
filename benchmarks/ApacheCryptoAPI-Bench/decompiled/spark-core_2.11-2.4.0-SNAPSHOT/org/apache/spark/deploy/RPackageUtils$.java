package org.apache.spark.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.spark.api.r.RUtils$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.RedirectThread;
import org.apache.spark.util.RedirectThread$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.spark_project.guava.io.ByteStreams;
import org.spark_project.guava.io.Files;
import scala.Array$;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

public final class RPackageUtils$ implements Logging {
   public static final RPackageUtils$ MODULE$;
   private final String hasRPackage;
   private final Seq<String> baseInstallCmd;
   private final String RJarEntries;
   private final String RJarDoc;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new RPackageUtils$();
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

   private final String hasRPackage() {
      return "Spark-HasRPackage";
   }

   private final Seq<String> baseInstallCmd() {
      return this.baseInstallCmd;
   }

   private final String RJarEntries() {
      return "R/pkg";
   }

   public final String RJarDoc() {
      return this.RJarDoc;
   }

   public void org$apache$spark$deploy$RPackageUtils$$print(String msg, PrintStream printStream, Level level, Throwable e) {
      if (printStream == null) {
         label59: {
            Level var10000 = Level.INFO;
            if (var10000 == null) {
               if (level == null) {
                  break label59;
               }
            } else if (var10000.equals(level)) {
               break label59;
            }

            label60: {
               var10000 = Level.WARNING;
               if (var10000 == null) {
                  if (level == null) {
                     break label60;
                  }
               } else if (var10000.equals(level)) {
                  break label60;
               }

               label61: {
                  var10000 = Level.SEVERE;
                  if (var10000 == null) {
                     if (level == null) {
                        break label61;
                     }
                  } else if (var10000.equals(level)) {
                     break label61;
                  }

                  this.logDebug(new Serializable(msg) {
                     public static final long serialVersionUID = 0L;
                     private final String msg$1;

                     public final String apply() {
                        return this.msg$1;
                     }

                     public {
                        this.msg$1 = msg$1;
                     }
                  });
                  BoxedUnit var7 = BoxedUnit.UNIT;
                  return;
               }

               this.logError(new Serializable(msg) {
                  public static final long serialVersionUID = 0L;
                  private final String msg$1;

                  public final String apply() {
                     return this.msg$1;
                  }

                  public {
                     this.msg$1 = msg$1;
                  }
               }, e);
               BoxedUnit var10 = BoxedUnit.UNIT;
               return;
            }

            this.logWarning(new Serializable(msg) {
               public static final long serialVersionUID = 0L;
               private final String msg$1;

               public final String apply() {
                  return this.msg$1;
               }

               public {
                  this.msg$1 = msg$1;
               }
            });
            BoxedUnit var11 = BoxedUnit.UNIT;
            return;
         }

         this.logInfo(new Serializable(msg) {
            public static final long serialVersionUID = 0L;
            private final String msg$1;

            public final String apply() {
               return this.msg$1;
            }

            public {
               this.msg$1 = msg$1;
            }
         });
         BoxedUnit var12 = BoxedUnit.UNIT;
      } else {
         printStream.println(msg);
         if (e != null) {
            e.printStackTrace(printStream);
         }
      }

   }

   public Level org$apache$spark$deploy$RPackageUtils$$print$default$3() {
      return Level.FINE;
   }

   public Throwable org$apache$spark$deploy$RPackageUtils$$print$default$4() {
      return null;
   }

   public boolean checkManifestForR(JarFile jar) {
      if (jar.getManifest() == null) {
         return false;
      } else {
         boolean var4;
         label31: {
            Attributes manifest = jar.getManifest().getMainAttributes();
            if (manifest.getValue("Spark-HasRPackage") != null) {
               String var10000 = manifest.getValue("Spark-HasRPackage").trim();
               String var3 = "true";
               if (var10000 == null) {
                  if (var3 == null) {
                     break label31;
                  }
               } else if (var10000.equals(var3)) {
                  break label31;
               }
            }

            var4 = false;
            return var4;
         }

         var4 = true;
         return var4;
      }
   }

   public boolean org$apache$spark$deploy$RPackageUtils$$rPackageBuilder(File dir, PrintStream printStream, boolean verbose, String libDir) {
      String pathToPkg = ((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Object[]{dir, "R", "pkg"}))).mkString(File.separator);
      Seq installCmd = (Seq)this.baseInstallCmd().$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{libDir, pathToPkg}))), Seq$.MODULE$.canBuildFrom());
      if (verbose) {
         this.org$apache$spark$deploy$RPackageUtils$$print((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Building R package with the command: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{installCmd})), printStream, this.org$apache$spark$deploy$RPackageUtils$$print$default$3(), this.org$apache$spark$deploy$RPackageUtils$$print$default$4());
      }

      boolean var10000;
      try {
         ProcessBuilder builder = new ProcessBuilder((List)JavaConverters$.MODULE$.seqAsJavaListConverter(installCmd).asJava());
         builder.redirectErrorStream(true);
         Map env = builder.environment();
         Seq rPackageDir = RUtils$.MODULE$.sparkRPackagePath(true);
         env.put("SPARKR_PACKAGE_DIR", rPackageDir.mkString(","));
         env.put("R_PROFILE_USER", ((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{(String)rPackageDir.apply(0), "SparkR", "profile", "general.R"})))).mkString(File.separator));
         Process process = builder.start();
         (new RedirectThread(process.getInputStream(), printStream, "redirect R packaging", RedirectThread$.MODULE$.$lessinit$greater$default$4())).start();
         var10000 = process.waitFor() == 0;
      } catch (Throwable var13) {
         this.org$apache$spark$deploy$RPackageUtils$$print("Failed to build R package.", printStream, Level.SEVERE, var13);
         var10000 = false;
         return var10000;
      }

      return var10000;
   }

   public File org$apache$spark$deploy$RPackageUtils$$extractRFolder(JarFile jar, PrintStream printStream, boolean verbose) {
      File tempDir = Utils$.MODULE$.createTempDir((String)null, Utils$.MODULE$.createTempDir$default$2());
      Enumeration jarEntries = jar.entries();

      while(jarEntries.hasMoreElements()) {
         JarEntry entry = (JarEntry)jarEntries.nextElement();
         int entryRIndex = entry.getName().indexOf("R/pkg");
         if (entryRIndex > -1) {
            String entryPath = entry.getName().substring(entryRIndex);
            if (entry.isDirectory()) {
               File dir = new File(tempDir, entryPath);
               if (verbose) {
                  this.org$apache$spark$deploy$RPackageUtils$$print((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Creating directory: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{dir})), printStream, this.org$apache$spark$deploy$RPackageUtils$$print$default$3(), this.org$apache$spark$deploy$RPackageUtils$$print$default$4());
               }

               BoxesRunTime.boxToBoolean(dir.mkdirs());
            } else {
               InputStream inStream = jar.getInputStream(entry);
               File outPath = new File(tempDir, entryPath);
               Files.createParentDirs(outPath);
               FileOutputStream outStream = new FileOutputStream(outPath);
               if (verbose) {
                  this.org$apache$spark$deploy$RPackageUtils$$print((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Extracting ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{entry, outPath})), printStream, this.org$apache$spark$deploy$RPackageUtils$$print$default$3(), this.org$apache$spark$deploy$RPackageUtils$$print$default$4());
               }

               BoxesRunTime.boxToLong(Utils$.MODULE$.copyStream(inStream, outStream, true, Utils$.MODULE$.copyStream$default$4()));
            }
         } else {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }
      }

      return tempDir;
   }

   public void checkAndBuildRPackage(String jars, PrintStream printStream, boolean verbose) {
      Predef$.MODULE$.refArrayOps((Object[])jars.split(",")).foreach(new Serializable(printStream, verbose) {
         public static final long serialVersionUID = 0L;
         public final PrintStream printStream$1;
         public final boolean verbose$1;

         public final void apply(String jarPath) {
            File file = new File(Utils$.MODULE$.resolveURI(jarPath));
            if (file.exists()) {
               JarFile jar = new JarFile(file);
               Utils$.MODULE$.tryWithSafeFinally(new Serializable(this, file, jar) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final File file$1;
                  private final JarFile jar$1;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     if (RPackageUtils$.MODULE$.checkManifestForR(this.jar$1)) {
                        RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " contains R source code. Now installing package."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1})), this.$outer.printStream$1, Level.INFO, RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print$default$4());
                        File rSource = RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$extractRFolder(this.jar$1, this.$outer.printStream$1, this.$outer.verbose$1);
                        if (RUtils$.MODULE$.rPackages().isEmpty()) {
                           RUtils$.MODULE$.rPackages_$eq(new Some(Utils$.MODULE$.createTempDir(Utils$.MODULE$.createTempDir$default$1(), Utils$.MODULE$.createTempDir$default$2()).getAbsolutePath()));
                        }

                        try {
                           if (!RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$rPackageBuilder(rSource, this.$outer.printStream$1, this.$outer.verbose$1, (String)RUtils$.MODULE$.rPackages().get())) {
                              RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ERROR: Failed to build R package in ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1})), this.$outer.printStream$1, RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print$default$3(), RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print$default$4());
                              RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print(RPackageUtils$.MODULE$.RJarDoc(), this.$outer.printStream$1, RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print$default$3(), RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print$default$4());
                           }
                        } finally {
                           if (!rSource.delete()) {
                              RPackageUtils$.MODULE$.logWarning(new Serializable(this, rSource) {
                                 public static final long serialVersionUID = 0L;
                                 private final File rSource$1;

                                 public final String apply() {
                                    return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rSource$1.getPath()}));
                                 }

                                 public {
                                    this.rSource$1 = rSource$1;
                                 }
                              });
                           }

                        }
                     } else if (this.$outer.verbose$1) {
                        RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " doesn't contain R source code, skipping..."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1})), this.$outer.printStream$1, RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print$default$3(), RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print$default$4());
                     }

                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.file$1 = file$1;
                        this.jar$1 = jar$1;
                     }
                  }
               }, new Serializable(this, jar) {
                  public static final long serialVersionUID = 0L;
                  private final JarFile jar$1;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     this.jar$1.close();
                  }

                  public {
                     this.jar$1 = jar$1;
                  }
               });
            } else {
               RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"WARN: ", " resolved as dependency, but not found."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{file})), this.printStream$1, Level.WARNING, RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$print$default$4());
            }

         }

         public {
            this.printStream$1 = printStream$1;
            this.verbose$1 = verbose$1;
         }
      });
   }

   public PrintStream checkAndBuildRPackage$default$2() {
      return null;
   }

   public boolean checkAndBuildRPackage$default$3() {
      return false;
   }

   public Set<File> org$apache$spark$deploy$RPackageUtils$$listFilesRecursively(File dir, Seq<String> excludePatterns) {
      Set var10000;
      if (dir.exists()) {
         if (dir.isDirectory()) {
            File[] subDir = dir.listFiles(new FilenameFilter(excludePatterns) {
               private final Seq excludePatterns$1;

               public boolean accept(File dir, String name) {
                  return !BoxesRunTime.unboxToBoolean(((TraversableOnce)this.excludePatterns$1.map(new Serializable(this, name) {
                     public static final long serialVersionUID = 0L;
                     private final String name$1;

                     public final boolean apply(CharSequence x$1) {
                        return this.name$1.contains(x$1);
                     }

                     public {
                        this.name$1 = name$1;
                     }
                  }, Seq$.MODULE$.canBuildFrom())).reduce(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final boolean apply(boolean x$1, boolean x$2) {
                        return x$1 || x$2;
                     }
                  }));
               }

               public {
                  this.excludePatterns$1 = excludePatterns$1;
               }
            });
            var10000 = Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])subDir).flatMap(new Serializable(excludePatterns) {
               public static final long serialVersionUID = 0L;
               private final Seq excludePatterns$1;

               public final Set<File> apply(File x$3) {
                  return RPackageUtils$.MODULE$.org$apache$spark$deploy$RPackageUtils$$listFilesRecursively(x$3, this.excludePatterns$1);
               }

               public {
                  this.excludePatterns$1 = excludePatterns$1;
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(File.class)))).toSet();
         } else {
            var10000 = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new File[]{dir})));
         }
      } else {
         var10000 = Predef$.MODULE$.Set().empty();
      }

      return var10000;
   }

   public File zipRLibraries(File dir, String name) {
      Set filesToBundle = this.org$apache$spark$deploy$RPackageUtils$$listFilesRecursively(dir, (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{".zip"}))));
      File zipFile = new File(dir, name);
      if (!zipFile.delete()) {
         this.logWarning(new Serializable(zipFile) {
            public static final long serialVersionUID = 0L;
            private final File zipFile$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.zipFile$1.getPath()}));
            }

            public {
               this.zipFile$1 = zipFile$1;
            }
         });
      }

      ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile, false));

      try {
         filesToBundle.foreach(new Serializable(dir, zipOutputStream) {
            public static final long serialVersionUID = 0L;
            private final File dir$1;
            private final ZipOutputStream zipOutputStream$1;

            public final void apply(File file) {
               String relPath = file.toURI().toString().replaceFirst((new StringOps(Predef$.MODULE$.augmentString(this.dir$1.toURI().toString()))).stripSuffix("/"), "");
               FileInputStream fis = new FileInputStream(file);
               ZipEntry zipEntry = new ZipEntry(relPath);
               this.zipOutputStream$1.putNextEntry(zipEntry);
               ByteStreams.copy(fis, this.zipOutputStream$1);
               this.zipOutputStream$1.closeEntry();
               fis.close();
            }

            public {
               this.dir$1 = dir$1;
               this.zipOutputStream$1 = zipOutputStream$1;
            }
         });
      } finally {
         zipOutputStream.close();
      }

      return zipFile;
   }

   private RPackageUtils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.baseInstallCmd = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"R", "CMD", "INSTALL", "-l"})));
      this.RJarDoc = (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"In order for Spark to build R packages that are parts of Spark Packages, there are a few\n      |requirements. The R source code must be shipped in a jar, with additional Java/Scala\n      |classes. The jar must be in the following format:\n      |  1- The Manifest (META-INF/MANIFEST.mf) must contain the key-value: ", ": true\n      |  2- The standard R package layout must be preserved under R/pkg/ inside the jar. More\n      |  information on the standard R package layout can be found in:\n      |  http://cran.r-project.org/doc/contrib/Leisch-CreatingPackages.pdf\n      |  An example layout is given below. After running `jar tf $JAR_FILE | sort`:\n      |\n      |META-INF/MANIFEST.MF\n      |R/\n      |R/pkg/\n      |R/pkg/DESCRIPTION\n      |R/pkg/NAMESPACE\n      |R/pkg/R/\n      |R/pkg/R/myRcode.R\n      |org/\n      |org/apache/\n      |...\n    "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{"Spark-HasRPackage"}))))).stripMargin().trim();
   }
}
