package org.apache.spark.deploy;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Map;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultExcludeRule;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.descriptor.ExcludeRule;
import org.apache.ivy.core.module.id.ArtifactId;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.matcher.GlobPatternMatcher;
import org.apache.ivy.plugins.repository.file.FileRepository;
import org.apache.ivy.plugins.resolver.ChainResolver;
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.apache.ivy.plugins.resolver.FileSystemResolver;
import org.apache.ivy.plugins.resolver.IBiblioResolver;
import org.apache.spark.SparkException;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

public final class SparkSubmitUtils$ {
   public static final SparkSubmitUtils$ MODULE$;
   private PrintStream printStream;
   private final Seq<String> IVY_DEFAULT_EXCLUDES;

   static {
      new SparkSubmitUtils$();
   }

   public PrintStream printStream() {
      return this.printStream;
   }

   public void printStream_$eq(PrintStream x$1) {
      this.printStream = x$1;
   }

   public Seq<String> IVY_DEFAULT_EXCLUDES() {
      return this.IVY_DEFAULT_EXCLUDES;
   }

   public Seq<SparkSubmitUtils.MavenCoordinate> extractMavenCoordinates(String coordinates) {
      return (Seq)Predef$.MODULE$.refArrayOps((Object[])coordinates.split(",")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final SparkSubmitUtils.MavenCoordinate apply(String p) {
            String[] splits = p.replace("/", ":").split(":");
            Predef$.MODULE$.require(splits.length == 3, new Serializable(this, p) {
               public static final long serialVersionUID = 0L;
               private final String p$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Provided Maven Coordinates must be in the form "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"'groupId:artifactId:version'. The coordinate provided is: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.p$1}))).toString();
               }

               public {
                  this.p$1 = p$1;
               }
            });
            Predef$.MODULE$.require(splits[0] != null && (new StringOps(Predef$.MODULE$.augmentString(splits[0].trim()))).nonEmpty(), new Serializable(this, splits) {
               public static final long serialVersionUID = 0L;
               private final String[] splits$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The groupId cannot be null or "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"be whitespace. The groupId provided is: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.splits$1[0]}))).toString();
               }

               public {
                  this.splits$1 = splits$1;
               }
            });
            Predef$.MODULE$.require(splits[1] != null && (new StringOps(Predef$.MODULE$.augmentString(splits[1].trim()))).nonEmpty(), new Serializable(this, splits) {
               public static final long serialVersionUID = 0L;
               private final String[] splits$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The artifactId cannot be null or "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"be whitespace. The artifactId provided is: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.splits$1[1]}))).toString();
               }

               public {
                  this.splits$1 = splits$1;
               }
            });
            Predef$.MODULE$.require(splits[2] != null && (new StringOps(Predef$.MODULE$.augmentString(splits[2].trim()))).nonEmpty(), new Serializable(this, splits) {
               public static final long serialVersionUID = 0L;
               private final String[] splits$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The version cannot be null or "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"be whitespace. The version provided is: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.splits$1[2]}))).toString();
               }

               public {
                  this.splits$1 = splits$1;
               }
            });
            return new SparkSubmitUtils.MavenCoordinate(splits[0], splits[1], splits[2]);
         }
      }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
   }

   public File m2Path() {
      return Utils$.MODULE$.isTesting() ? new File("dummy", (new StringBuilder()).append((Object)".m2").append((Object)File.separator).append((Object)"repository").toString()) : new File(System.getProperty("user.home"), (new StringBuilder()).append((Object)".m2").append((Object)File.separator).append((Object)"repository").toString());
   }

   public ChainResolver createRepoResolvers(File defaultIvyUserDir) {
      ChainResolver cr = new ChainResolver();
      cr.setName("spark-list");
      IBiblioResolver localM2 = new IBiblioResolver();
      localM2.setM2compatible(true);
      localM2.setRoot(this.m2Path().toURI().toString());
      localM2.setUsepoms(true);
      localM2.setName("local-m2-cache");
      cr.add(localM2);
      FileSystemResolver localIvy = new FileSystemResolver();
      File localIvyRoot = new File(defaultIvyUserDir, "local");
      localIvy.setLocal(true);
      localIvy.setRepository(new FileRepository(localIvyRoot));
      String ivyPattern = ((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{localIvyRoot.getAbsolutePath(), "[organisation]", "[module]", "[revision]", "ivys", "ivy.xml"})))).mkString(File.separator);
      localIvy.addIvyPattern(ivyPattern);
      String artifactPattern = ((TraversableOnce)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{localIvyRoot.getAbsolutePath(), "[organisation]", "[module]", "[revision]", "[type]s", "[artifact](-[classifier]).[ext]"})))).mkString(File.separator);
      localIvy.addArtifactPattern(artifactPattern);
      localIvy.setName("local-ivy-cache");
      cr.add(localIvy);
      IBiblioResolver br = new IBiblioResolver();
      br.setM2compatible(true);
      br.setUsepoms(true);
      br.setName("central");
      cr.add(br);
      IBiblioResolver sp = new IBiblioResolver();
      sp.setM2compatible(true);
      sp.setUsepoms(true);
      sp.setRoot("http://dl.bintray.com/spark-packages/maven");
      sp.setName("spark-packages");
      cr.add(sp);
      return cr;
   }

   public String resolveDependencyPaths(Object[] artifacts, File cacheDirectory) {
      return Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(artifacts).map(new Serializable(cacheDirectory) {
         public static final long serialVersionUID = 0L;
         private final File cacheDirectory$1;

         public final String apply(Object artifactInfo) {
            ModuleRevisionId artifact = ((Artifact)artifactInfo).getModuleRevisionId();
            return (new StringBuilder()).append((Object)this.cacheDirectory$1.getAbsolutePath()).append((Object)File.separator).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "_", "-", ".jar"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{artifact.getOrganisation(), artifact.getName(), artifact.getRevision()}))).toString();
         }

         public {
            this.cacheDirectory$1 = cacheDirectory$1;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).mkString(",");
   }

   public void addDependenciesToIvy(DefaultModuleDescriptor md, Seq<SparkSubmitUtils.MavenCoordinate> artifacts, String ivyConfName) {
      artifacts.foreach(new Serializable(md, ivyConfName) {
         public static final long serialVersionUID = 0L;
         private final DefaultModuleDescriptor md$1;
         private final String ivyConfName$1;

         public final void apply(SparkSubmitUtils.MavenCoordinate mvn) {
            ModuleRevisionId ri = ModuleRevisionId.newInstance(mvn.groupId(), mvn.artifactId(), mvn.version());
            DefaultDependencyDescriptor dd = new DefaultDependencyDescriptor(ri, false, false);
            dd.addDependencyConfiguration(this.ivyConfName$1, (new StringBuilder()).append((Object)this.ivyConfName$1).append((Object)"(runtime)").toString());
            SparkSubmitUtils$.MODULE$.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " added as a dependency"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{dd.getDependencyId()})));
            this.md$1.addDependency(dd);
         }

         public {
            this.md$1 = md$1;
            this.ivyConfName$1 = ivyConfName$1;
         }
      });
   }

   public void addExclusionRules(IvySettings ivySettings, String ivyConfName, DefaultModuleDescriptor md) {
      md.addExcludeRule(this.createExclusion("*:scala-library:*", ivySettings, ivyConfName));
      this.IVY_DEFAULT_EXCLUDES().foreach(new Serializable(ivySettings, ivyConfName, md) {
         public static final long serialVersionUID = 0L;
         private final IvySettings ivySettings$2;
         private final String ivyConfName$2;
         private final DefaultModuleDescriptor md$2;

         public final void apply(String comp) {
            this.md$2.addExcludeRule(SparkSubmitUtils$.MODULE$.createExclusion((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"org.apache.spark:spark-", "*:*"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{comp})), this.ivySettings$2, this.ivyConfName$2));
         }

         public {
            this.ivySettings$2 = ivySettings$2;
            this.ivyConfName$2 = ivyConfName$2;
            this.md$2 = md$2;
         }
      });
   }

   public IvySettings buildIvySettings(Option<String> remoteRepos, Option<String> ivyPath) {
      IvySettings ivySettings = new IvySettings();
      this.processIvyPathArg(ivySettings, ivyPath);
      ivySettings.addMatcher(new GlobPatternMatcher());
      ChainResolver repoResolver = this.createRepoResolvers(ivySettings.getDefaultIvyUserDir());
      ivySettings.addResolver(repoResolver);
      ivySettings.setDefaultResolver(repoResolver.getName());
      this.processRemoteRepoArg(ivySettings, remoteRepos);
      return ivySettings;
   }

   public IvySettings loadIvySettings(String settingsFile, Option<String> remoteRepos, Option<String> ivyPath) {
      File file = new File(settingsFile);
      Predef$.MODULE$.require(file.exists(), new Serializable(file) {
         public static final long serialVersionUID = 0L;
         private final File file$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ivy settings file ", " does not exist"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1}));
         }

         public {
            this.file$1 = file$1;
         }
      });
      Predef$.MODULE$.require(file.isFile(), new Serializable(file) {
         public static final long serialVersionUID = 0L;
         private final File file$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ivy settings file ", " is not a normal file"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1}));
         }

         public {
            this.file$1 = file$1;
         }
      });
      IvySettings ivySettings = new IvySettings();

      try {
         ivySettings.load(file);
      } catch (Throwable var10) {
         boolean var8;
         if (var10 instanceof IOException) {
            var8 = true;
         } else if (var10 instanceof ParseException) {
            var8 = true;
         } else {
            var8 = false;
         }

         if (var8) {
            throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed when loading Ivy settings from ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{settingsFile})), var10);
         }

         throw var10;
      }

      this.processIvyPathArg(ivySettings, ivyPath);
      this.processRemoteRepoArg(ivySettings, remoteRepos);
      return ivySettings;
   }

   private void processIvyPathArg(IvySettings ivySettings, Option<String> ivyPath) {
      ivyPath.filterNot(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$11) {
            return x$11.trim().isEmpty();
         }
      }).foreach(new Serializable(ivySettings) {
         public static final long serialVersionUID = 0L;
         private final IvySettings ivySettings$3;

         public final void apply(String alternateIvyDir) {
            this.ivySettings$3.setDefaultIvyUserDir(new File(alternateIvyDir));
            this.ivySettings$3.setDefaultCache(new File(alternateIvyDir, "cache"));
         }

         public {
            this.ivySettings$3 = ivySettings$3;
         }
      });
   }

   private void processRemoteRepoArg(IvySettings ivySettings, Option<String> remoteRepos) {
      remoteRepos.filterNot(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$12) {
            return x$12.trim().isEmpty();
         }
      }).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String[] apply(String x$13) {
            return x$13.split(",");
         }
      }).foreach(new Serializable(ivySettings) {
         public static final long serialVersionUID = 0L;
         private final IvySettings ivySettings$4;

         public final void apply(String[] repositoryList) {
            ChainResolver cr = new ChainResolver();
            cr.setName("user-list");
            Option$.MODULE$.apply(this.ivySettings$4.getDefaultResolver()).foreach(new Serializable(this, cr) {
               public static final long serialVersionUID = 0L;
               private final ChainResolver cr$1;

               public final void apply(DependencyResolver x$1) {
                  this.cr$1.add(x$1);
               }

               public {
                  this.cr$1 = cr$1;
               }
            });
            Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])repositoryList).zipWithIndex(Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).foreach(new Serializable(this, cr) {
               public static final long serialVersionUID = 0L;
               private final ChainResolver cr$1;

               public final void apply(Tuple2<String, Object> x0$2) {
                  if (x0$2 != null) {
                     String repo = (String)x0$2._1();
                     int i = x0$2._2$mcI$sp();
                     IBiblioResolver brr = new IBiblioResolver();
                     brr.setM2compatible(true);
                     brr.setUsepoms(true);
                     brr.setRoot(repo);
                     brr.setName((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"repo-", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(i + 1)})));
                     this.cr$1.add(brr);
                     SparkSubmitUtils$.MODULE$.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " added as a remote repository with the name: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{repo, brr.getName()})));
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  } else {
                     throw new MatchError(x0$2);
                  }
               }

               public {
                  this.cr$1 = cr$1;
               }
            });
            this.ivySettings$4.addResolver(cr);
            this.ivySettings$4.setDefaultResolver(cr.getName());
         }

         public {
            this.ivySettings$4 = ivySettings$4;
         }
      });
   }

   public DefaultModuleDescriptor getModuleDescriptor() {
      return DefaultModuleDescriptor.newDefaultInstance(ModuleRevisionId.newInstance("org.apache.spark", "spark-submit-parent", "1.0"));
   }

   public String resolveMavenCoordinates(String coordinates, IvySettings ivySettings, Seq<String> exclusions, boolean isTest) {
      String var10000;
      if (coordinates != null && !coordinates.trim().isEmpty()) {
         PrintStream sysOut = System.out;

         try {
            System.setOut(this.printStream());
            Seq artifacts = this.extractMavenCoordinates(coordinates);
            File packagesDirectory = new File(ivySettings.getDefaultIvyUserDir(), "jars");
            this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ivy Default Cache set to: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ivySettings.getDefaultCache().getAbsolutePath()})));
            this.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The jars for the packages stored in: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{packagesDirectory})));
            Ivy ivy = Ivy.newInstance(ivySettings);
            ResolveOptions resolveOptions = new ResolveOptions();
            resolveOptions.setTransitive(true);
            RetrieveOptions retrieveOptions = new RetrieveOptions();
            if (isTest) {
               resolveOptions.setDownload(false);
               resolveOptions.setLog("quiet");
               retrieveOptions.setLog("quiet");
            } else {
               resolveOptions.setDownload(true);
            }

            String ivyConfName = "default";
            DefaultModuleDescriptor md = this.getModuleDescriptor();
            ModuleRevisionId mdId = md.getModuleRevisionId();
            File previousResolution = new File(ivySettings.getDefaultCache(), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "-", "-", ".xml"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{mdId.getOrganisation(), mdId.getName(), ivyConfName})));
            if (previousResolution.exists()) {
               BoxesRunTime.boxToBoolean(previousResolution.delete());
            } else {
               BoxedUnit var19 = BoxedUnit.UNIT;
            }

            md.setDefaultConf(ivyConfName);
            this.addExclusionRules(ivySettings, ivyConfName, md);
            this.addDependenciesToIvy(md, artifacts, ivyConfName);
            exclusions.foreach(new Serializable(ivySettings, ivyConfName, md) {
               public static final long serialVersionUID = 0L;
               private final IvySettings ivySettings$1;
               private final String ivyConfName$3;
               private final DefaultModuleDescriptor md$3;

               public final void apply(String e) {
                  this.md$3.addExcludeRule(SparkSubmitUtils$.MODULE$.createExclusion((new StringBuilder()).append((Object)e).append((Object)":*").toString(), this.ivySettings$1, this.ivyConfName$3));
               }

               public {
                  this.ivySettings$1 = ivySettings$1;
                  this.ivyConfName$3 = ivyConfName$3;
                  this.md$3 = md$3;
               }
            });
            ResolveReport rr = ivy.resolve(md, resolveOptions);
            if (rr.hasError()) {
               throw new RuntimeException(rr.getAllProblemMessages().toString());
            }

            ivy.retrieve(rr.getModuleDescriptor().getModuleRevisionId(), (new StringBuilder()).append((Object)packagesDirectory.getAbsolutePath()).append((Object)File.separator).append((Object)"[organization]_[artifact]-[revision](-[classifier]).[ext]").toString(), retrieveOptions.setConfs((String[])((Object[])(new String[]{ivyConfName}))));
            var10000 = this.resolveDependencyPaths(rr.getArtifacts().toArray(), packagesDirectory);
         } finally {
            System.setOut(sysOut);
         }
      } else {
         var10000 = "";
      }

      return var10000;
   }

   public Seq<String> resolveMavenCoordinates$default$3() {
      return Nil$.MODULE$;
   }

   public boolean resolveMavenCoordinates$default$4() {
      return false;
   }

   public ExcludeRule createExclusion(String coords, IvySettings ivySettings, String ivyConfName) {
      SparkSubmitUtils.MavenCoordinate c = (SparkSubmitUtils.MavenCoordinate)this.extractMavenCoordinates(coords).apply(0);
      ArtifactId id = new ArtifactId(new ModuleId(c.groupId(), c.artifactId()), "*", "*", "*");
      DefaultExcludeRule rule = new DefaultExcludeRule(id, ivySettings.getMatcher("glob"), (Map)null);
      rule.addConfiguration(ivyConfName);
      return rule;
   }

   private SparkSubmitUtils$() {
      MODULE$ = this;
      this.printStream = SparkSubmit$.MODULE$.printStream();
      this.IVY_DEFAULT_EXCLUDES = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"catalyst_", "core_", "graphx_", "kvstore_", "launcher_", "mllib_", "mllib-local_", "network-common_", "network-shuffle_", "repl_", "sketch_", "sql_", "streaming_", "tags_", "unsafe_"})));
   }
}
