package org.apache.spark;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaFileObject.Kind;
import org.apache.spark.util.Utils$;
import org.spark_project.guava.io.ByteStreams;
import org.spark_project.guava.io.Files;
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
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.sys.process.Process$;
import scala.sys.process.ProcessLogger$;
import scala.util.Try;
import scala.util.Try$;

public final class TestUtils$ {
   public static final TestUtils$ MODULE$;
   private final JavaFileObject.Kind org$apache$spark$TestUtils$$SOURCE;

   static {
      new TestUtils$();
   }

   public URL createJarWithClasses(Seq<String> classNames, String toStringValue, Seq<Tuple2<String, String>> classNamesWithBase, Seq<URL> classpathUrls) {
      File tempDir = Utils$.MODULE$.createTempDir(Utils$.MODULE$.createTempDir$default$1(), Utils$.MODULE$.createTempDir$default$2());
      Seq files1 = (Seq)classNames.map(new Serializable(toStringValue, classpathUrls, tempDir) {
         public static final long serialVersionUID = 0L;
         private final String toStringValue$1;
         private final Seq classpathUrls$1;
         private final File tempDir$1;

         public final File apply(String name) {
            File x$6 = this.tempDir$1;
            String x$7 = this.toStringValue$1;
            Seq x$8 = this.classpathUrls$1;
            String x$9 = TestUtils$.MODULE$.createCompiledClass$default$4();
            return TestUtils$.MODULE$.createCompiledClass(name, x$6, x$7, x$9, x$8);
         }

         public {
            this.toStringValue$1 = toStringValue$1;
            this.classpathUrls$1 = classpathUrls$1;
            this.tempDir$1 = tempDir$1;
         }
      }, Seq$.MODULE$.canBuildFrom());
      Seq files2 = (Seq)classNamesWithBase.withFilter(new Serializable() {
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
      }).map(new Serializable(toStringValue, classpathUrls, tempDir) {
         public static final long serialVersionUID = 0L;
         private final String toStringValue$1;
         private final Seq classpathUrls$1;
         private final File tempDir$1;

         public final File apply(Tuple2<String, String> x$1) {
            if (x$1 != null) {
               String childName = (String)x$1._1();
               String baseName = (String)x$1._2();
               File var5 = TestUtils$.MODULE$.createCompiledClass(childName, this.tempDir$1, this.toStringValue$1, baseName, this.classpathUrls$1);
               return var5;
            } else {
               throw new MatchError(x$1);
            }
         }

         public {
            this.toStringValue$1 = toStringValue$1;
            this.classpathUrls$1 = classpathUrls$1;
            this.tempDir$1 = tempDir$1;
         }
      }, Seq$.MODULE$.canBuildFrom());
      File jarFile = new File(tempDir, (new StringOps(Predef$.MODULE$.augmentString("testJar-%s.jar"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(System.currentTimeMillis())})));
      return this.createJar((Seq)files1.$plus$plus(files2, Seq$.MODULE$.canBuildFrom()), jarFile, this.createJar$default$3());
   }

   public URL createJarWithFiles(Map<String, String> files, File dir) {
      File tempDir = (File)Option$.MODULE$.apply(dir).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final File apply() {
            return Utils$.MODULE$.createTempDir(Utils$.MODULE$.createTempDir$default$1(), Utils$.MODULE$.createTempDir$default$2());
         }
      });
      File jarFile = File.createTempFile("testJar", ".jar", tempDir);
      JarOutputStream jarStream = new JarOutputStream(new FileOutputStream(jarFile));
      files.foreach(new Serializable(jarStream) {
         public static final long serialVersionUID = 0L;
         private final JarOutputStream jarStream$1;

         public final long apply(Tuple2<String, String> x0$1) {
            if (x0$1 != null) {
               String k = (String)x0$1._1();
               String v = (String)x0$1._2();
               JarEntry entry = new JarEntry(k);
               this.jarStream$1.putNextEntry(entry);
               long var5 = ByteStreams.copy(new ByteArrayInputStream(v.getBytes(StandardCharsets.UTF_8)), this.jarStream$1);
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.jarStream$1 = jarStream$1;
         }
      });
      jarStream.close();
      return jarFile.toURI().toURL();
   }

   public URL createJar(Seq<File> files, File jarFile, Option<String> directoryPrefix) {
      FileOutputStream jarFileStream = new FileOutputStream(jarFile);
      JarOutputStream jarStream = new JarOutputStream(jarFileStream, new Manifest());
      files.foreach(new Serializable(directoryPrefix, jarStream) {
         public static final long serialVersionUID = 0L;
         private final Option directoryPrefix$1;
         private final JarOutputStream jarStream$2;

         public final void apply(File file) {
            String prefix = (String)this.directoryPrefix$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String d) {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{d}));
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "";
               }
            });
            JarEntry jarEntry = new JarEntry((new StringBuilder()).append((Object)prefix).append((Object)file.getName()).toString());
            this.jarStream$2.putNextEntry(jarEntry);
            FileInputStream in = new FileInputStream(file);
            ByteStreams.copy(in, this.jarStream$2);
            in.close();
         }

         public {
            this.directoryPrefix$1 = directoryPrefix$1;
            this.jarStream$2 = jarStream$2;
         }
      });
      jarStream.close();
      jarFileStream.close();
      return jarFile.toURI().toURL();
   }

   public String createJarWithClasses$default$2() {
      return "";
   }

   public Seq<Tuple2<String, String>> createJarWithClasses$default$3() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public Seq<URL> createJarWithClasses$default$4() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public Option<String> createJar$default$3() {
      return None$.MODULE$;
   }

   public File createJarWithFiles$default$2() {
      return null;
   }

   public JavaFileObject.Kind org$apache$spark$TestUtils$$SOURCE() {
      return this.org$apache$spark$TestUtils$$SOURCE;
   }

   public URI org$apache$spark$TestUtils$$createURI(String name) {
      return URI.create((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"string:///", "", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{name.replace(".", "/"), this.org$apache$spark$TestUtils$$SOURCE().extension})));
   }

   public File createCompiledClass(String className, File destDir, TestUtils.JavaSourceFromString sourceFile, Seq<URL> classpathUrls) {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      Seq options = classpathUrls.nonEmpty() ? (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"-classpath", ((TraversableOnce)classpathUrls.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(URL x$2) {
            return x$2.getFile();
         }
      }, Seq$.MODULE$.canBuildFrom())).mkString(File.pathSeparator)}))) : (Seq)Seq$.MODULE$.empty();
      compiler.getTask((Writer)null, (JavaFileManager)null, (DiagnosticListener)null, (Iterable)JavaConverters$.MODULE$.seqAsJavaListConverter(options).asJava(), (Iterable)null, Arrays.asList((Object[])(new TestUtils.JavaSourceFromString[]{sourceFile}))).call();
      String fileName = (new StringBuilder()).append((Object)className).append((Object)".class").toString();
      File result = new File(fileName);
      Predef$.MODULE$.assert(result.exists(), new Serializable(result) {
         public static final long serialVersionUID = 0L;
         private final File result$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Compiled file not found: ").append((Object)this.result$1.getAbsolutePath()).toString();
         }

         public {
            this.result$1 = result$1;
         }
      });
      File out = new File(destDir, fileName);
      Files.move(result, out);
      Predef$.MODULE$.assert(out.exists(), new Serializable(out) {
         public static final long serialVersionUID = 0L;
         private final File out$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Destination file not moved: ").append((Object)this.out$1.getAbsolutePath()).toString();
         }

         public {
            this.out$1 = out$1;
         }
      });
      return out;
   }

   public File createCompiledClass(String className, File destDir, String toStringValue, String baseClass, Seq<URL> classpathUrls) {
      String extendsText = (String)Option$.MODULE$.apply(baseClass).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String c) {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" extends ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{c}));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      TestUtils.JavaSourceFromString sourceFile = new TestUtils.JavaSourceFromString(className, (new StringBuilder()).append((Object)"public class ").append((Object)className).append((Object)extendsText).append((Object)" implements java.io.Serializable {").append((Object)"  @Override public String toString() { return \"").append((Object)toStringValue).append((Object)"\"; }}").toString());
      return this.createCompiledClass(className, destDir, sourceFile, classpathUrls);
   }

   public String createCompiledClass$default$3() {
      return "";
   }

   public String createCompiledClass$default$4() {
      return null;
   }

   public Seq<URL> createCompiledClass$default$5() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public <T> void assertSpilled(SparkContext sc, String identifier, Function0<T> body) {
      SpillListener spillListener = new SpillListener();
      sc.addSparkListener(spillListener);
      body.apply();
      Predef$.MODULE$.assert(spillListener.numSpilledStages() > 0, new Serializable(identifier) {
         public static final long serialVersionUID = 0L;
         private final String identifier$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"expected ", " to spill, but did not"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.identifier$1}));
         }

         public {
            this.identifier$1 = identifier$1;
         }
      });
   }

   public <T> void assertNotSpilled(SparkContext sc, String identifier, Function0<T> body) {
      SpillListener spillListener = new SpillListener();
      sc.addSparkListener(spillListener);
      body.apply();
      Predef$.MODULE$.assert(spillListener.numSpilledStages() == 0, new Serializable(identifier) {
         public static final long serialVersionUID = 0L;
         private final String identifier$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"expected ", " to not spill, but did"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.identifier$2}));
         }

         public {
            this.identifier$2 = identifier$2;
         }
      });
   }

   public boolean testCommandAvailable(String command) {
      Try attempt = Try$.MODULE$.apply(new Serializable(command) {
         public static final long serialVersionUID = 0L;
         private final String command$1;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return Process$.MODULE$.apply(this.command$1).run(ProcessLogger$.MODULE$.apply((Function1)(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(String x$3) {
               }
            }))).exitValue();
         }

         public {
            this.command$1 = command$1;
         }
      });
      return attempt.isSuccess() && BoxesRunTime.unboxToInt(attempt.get()) == 0;
   }

   public int httpResponseCode(URL url, String method, Seq<Tuple2<String, String>> headers) {
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod(method);
      headers.foreach(new Serializable(connection) {
         public static final long serialVersionUID = 0L;
         private final HttpURLConnection connection$1;

         public final void apply(Tuple2<String, String> x0$2) {
            if (x0$2 != null) {
               String k = (String)x0$2._1();
               String v = (String)x0$2._2();
               this.connection$1.setRequestProperty(k, v);
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x0$2);
            }
         }

         public {
            this.connection$1 = connection$1;
         }
      });
      if (connection instanceof HttpsURLConnection) {
         SSLContext sslCtx = SSLContext.getInstance("SSL");
         X509TrustManager trustManager = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
               return null;
            }

            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }
         };
         HostnameVerifier verifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
               return true;
            }
         };
         sslCtx.init((KeyManager[])null, (TrustManager[])((Object[])(new TrustManager[]{trustManager})), new SecureRandom());
         ((HttpsURLConnection)connection).setSSLSocketFactory(sslCtx.getSocketFactory());
         ((HttpsURLConnection)connection).setHostnameVerifier(verifier);
      }

      int var10000;
      try {
         connection.connect();
         var10000 = connection.getResponseCode();
      } finally {
         connection.disconnect();
      }

      return var10000;
   }

   public String httpResponseCode$default$2() {
      return "GET";
   }

   public Seq<Tuple2<String, String>> httpResponseCode$default$3() {
      return Nil$.MODULE$;
   }

   public void waitUntilExecutorsUp(SparkContext sc, int numExecutors, long timeout) {
      long finishTime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);

      while(System.nanoTime() < finishTime) {
         if (sc.statusTracker().getExecutorInfos().length > numExecutors) {
            return;
         }

         Thread.sleep(10L);
      }

      throw new TimeoutException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't find ", " executors before ", " milliseconds elapsed"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(numExecutors), BoxesRunTime.boxToLong(timeout)})));
   }

   private TestUtils$() {
      MODULE$ = this;
      this.org$apache$spark$TestUtils$$SOURCE = Kind.SOURCE;
   }
}
