package org.apache.spark.util.logging;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.spark.SparkConf;
import org.spark_project.guava.io.Files;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering$;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ObjectRef;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005f!B\u0001\u0003\u0001\u0019a!a\u0005*pY2Lgn\u001a$jY\u0016\f\u0005\u000f]3oI\u0016\u0014(BA\u0002\u0005\u0003\u001dawnZ4j]\u001eT!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001d=i\u0011AA\u0005\u0003!\t\u0011ABR5mK\u0006\u0003\b/\u001a8eKJD\u0001B\u0005\u0001\u0003\u0002\u0003\u0006I\u0001F\u0001\fS:\u0004X\u000f^*ue\u0016\fWn\u0001\u0001\u0011\u0005UQR\"\u0001\f\u000b\u0005]A\u0012AA5p\u0015\u0005I\u0012\u0001\u00026bm\u0006L!a\u0007\f\u0003\u0017%s\u0007/\u001e;TiJ,\u0017-\u001c\u0005\t;\u0001\u0011\t\u0011)A\u0005=\u0005Q\u0011m\u0019;jm\u00164\u0015\u000e\\3\u0011\u0005Uy\u0012B\u0001\u0011\u0017\u0005\u00111\u0015\u000e\\3\t\u0011\t\u0002!Q1A\u0005\u0002\r\nQB]8mY&tw\rU8mS\u000eLX#\u0001\u0013\u0011\u00059)\u0013B\u0001\u0014\u0003\u00055\u0011v\u000e\u001c7j]\u001e\u0004v\u000e\\5ds\"A\u0001\u0006\u0001B\u0001B\u0003%A%\u0001\bs_2d\u0017N\\4Q_2L7-\u001f\u0011\t\u0011)\u0002!\u0011!Q\u0001\n-\nAaY8oMB\u0011A&L\u0007\u0002\r%\u0011aF\u0002\u0002\n'B\f'o[\"p]\u001aD\u0001\u0002\r\u0001\u0003\u0002\u0003\u0006I!M\u0001\u000bEV4g-\u001a:TSj,\u0007C\u0001\u001a6\u001b\u0005\u0019$\"\u0001\u001b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Y\u001a$aA%oi\")\u0001\b\u0001C\u0001s\u00051A(\u001b8jiz\"bAO\u001e={yz\u0004C\u0001\b\u0001\u0011\u0015\u0011r\u00071\u0001\u0015\u0011\u0015ir\u00071\u0001\u001f\u0011\u0015\u0011s\u00071\u0001%\u0011\u0015Qs\u00071\u0001,\u0011\u001d\u0001t\u0007%AA\u0002EBq!\u0011\u0001C\u0002\u0013%!)\u0001\tnCb\u0014V\r^1j]\u0016$g)\u001b7fgV\t\u0011\u0007\u0003\u0004E\u0001\u0001\u0006I!M\u0001\u0012[\u0006D(+\u001a;bS:,GMR5mKN\u0004\u0003b\u0002$\u0001\u0005\u0004%IaR\u0001\u0012K:\f'\r\\3D_6\u0004(/Z:tS>tW#\u0001%\u0011\u0005IJ\u0015B\u0001&4\u0005\u001d\u0011un\u001c7fC:Da\u0001\u0014\u0001!\u0002\u0013A\u0015AE3oC\ndWmQ8naJ,7o]5p]\u0002BQA\u0014\u0001\u0005B=\u000bAa\u001d;paR\t\u0001\u000b\u0005\u00023#&\u0011!k\r\u0002\u0005+:LG\u000fC\u0003U\u0001\u0011ES+\u0001\u0007baB,g\u000e\u001a+p\r&dW\rF\u0002Q-zCQaV*A\u0002a\u000bQAY=uKN\u00042AM-\\\u0013\tQ6GA\u0003BeJ\f\u0017\u0010\u0005\u000239&\u0011Ql\r\u0002\u0005\u0005f$X\rC\u0003`'\u0002\u0007\u0011'A\u0002mK:DQ!\u0019\u0001\u0005\n=\u000b\u0001B]8mY>4XM\u001d\u0005\u0006G\u0002!I\u0001Z\u0001\u000be>$\u0018\r^3GS2,Gc\u0001)fM\")QD\u0019a\u0001=!)qM\u0019a\u0001=\u0005a!o\u001c7m_Z,'OR5mK\")\u0011\u000e\u0001C\u0005U\u0006\t\"o\u001c7m_Z,'OR5mK\u0016C\u0018n\u001d;\u0015\u0005![\u0007\"\u00027i\u0001\u0004q\u0012\u0001\u00024jY\u0016DQA\u001c\u0001\u0005\n=\u000b\u0001\"\\8wK\u001aKG.\u001a\u0005\u0007a\u0002!\t\u0001B(\u0002\u001d\u0011,G.\u001a;f\u001f2$g)\u001b7fg\u001e1!O\u0001E\u0001\rM\f1CU8mY&twMR5mK\u0006\u0003\b/\u001a8eKJ\u0004\"A\u0004;\u0007\r\u0005\u0011\u0001\u0012\u0001\u0004v'\t!h\u000f\u0005\u00023o&\u0011\u0001p\r\u0002\u0007\u0003:L(+\u001a4\t\u000ba\"H\u0011\u0001>\u0015\u0003MDq\u0001 ;C\u0002\u0013\u0005Q0A\tT)J\u000bE+R$Z?B\u0013v\nU#S)f+\u0012A \t\u0004\u007f\u0006\u0015QBAA\u0001\u0015\r\t\u0019\u0001G\u0001\u0005Y\u0006tw-\u0003\u0003\u0002\b\u0005\u0005!AB*ue&tw\rC\u0004\u0002\fQ\u0004\u000b\u0011\u0002@\u0002%M#&+\u0011+F\u000ff{\u0006KU(Q\u000bJ#\u0016\f\t\u0005\t\u0003\u001f!(\u0019!C\u0001{\u0006\u00012\u000b\u0016*B)\u0016;\u0015l\u0018#F\r\u0006+F\n\u0016\u0005\b\u0003'!\b\u0015!\u0003\u007f\u0003E\u0019FKU!U\u000b\u001eKv\fR#G\u0003VcE\u000b\t\u0005\t\u0003/!(\u0019!C\u0001{\u0006\t\u0012J\u0014+F%Z\u000bEj\u0018)S\u001fB+%\u000bV-\t\u000f\u0005mA\u000f)A\u0005}\u0006\u0011\u0012J\u0014+F%Z\u000bEj\u0018)S\u001fB+%\u000bV-!\u0011!\ty\u0002\u001eb\u0001\n\u0003i\u0018\u0001E%O)\u0016\u0013f+\u0011'`\t\u00163\u0015)\u0016'U\u0011\u001d\t\u0019\u0003\u001eQ\u0001\ny\f\u0011#\u0013(U\u000bJ3\u0016\tT0E\u000b\u001a\u000bU\u000b\u0014+!\u0011!\t9\u0003\u001eb\u0001\n\u0003i\u0018!D*J5\u0016{\u0006KU(Q\u000bJ#\u0016\fC\u0004\u0002,Q\u0004\u000b\u0011\u0002@\u0002\u001dMK%,R0Q%>\u0003VI\u0015+ZA!A\u0011q\u0006;C\u0002\u0013\u0005Q0\u0001\u0007T\u0013j+u\fR#G\u0003VcE\u000bC\u0004\u00024Q\u0004\u000b\u0011\u0002@\u0002\u001bMK%,R0E\u000b\u001a\u000bU\u000b\u0014+!\u0011!\t9\u0004\u001eb\u0001\n\u0003i\u0018a\u0006*F)\u0006Ke*\u0012#`\r&cUiU0Q%>\u0003VI\u0015+Z\u0011\u001d\tY\u0004\u001eQ\u0001\ny\f\u0001DU#U\u0003&sU\tR0G\u00132+5k\u0018)S\u001fB+%\u000bV-!\u0011!\ty\u0004\u001eb\u0001\n\u0003\u0011\u0015a\u0005#F\r\u0006+F\nV0C+\u001a3UIU0T\u0013j+\u0005bBA\"i\u0002\u0006I!M\u0001\u0015\t\u00163\u0015)\u0016'U?\n+fIR#S?NK%,\u0012\u0011\t\u0011\u0005\u001dCO1A\u0005\u0002u\f!#\u0012(B\u00052+ulQ(N!J+5kU%P\u001d\"9\u00111\n;!\u0002\u0013q\u0018aE#O\u0003\ncUiX\"P\u001bB\u0013ViU*J\u001f:\u0003\u0003\u0002CA(i\n\u0007I\u0011A?\u0002\u001f\u001dS\u0016\nU0M\u001f\u001e{6+\u0016$G\u0013bCq!a\u0015uA\u0003%a0\u0001\tH5&\u0003v\fT(H?N+fIR%YA!9\u0011q\u000b;\u0005\u0002\u0005e\u0013\u0001G4fiN{'\u000f^3e%>dG.\u001a3Pm\u0016\u0014h)\u001b7fgR1\u00111LA:\u0003\u0007\u0003R!!\u0018\u0002nyqA!a\u0018\u0002j9!\u0011\u0011MA4\u001b\t\t\u0019GC\u0002\u0002fM\ta\u0001\u0010:p_Rt\u0014\"\u0001\u001b\n\u0007\u0005-4'A\u0004qC\u000e\\\u0017mZ3\n\t\u0005=\u0014\u0011\u000f\u0002\u0004'\u0016\f(bAA6g!A\u0011QOA+\u0001\u0004\t9(A\u0005eSJ,7\r^8ssB!\u0011\u0011PA@\u001d\r\u0011\u00141P\u0005\u0004\u0003{\u001a\u0014A\u0002)sK\u0012,g-\u0003\u0003\u0002\b\u0005\u0005%bAA?g!A\u0011QQA+\u0001\u0004\t9(\u0001\bbGRLg/\u001a$jY\u0016t\u0015-\\3\t\u0013\u0005%E/%A\u0005\u0002\u0005-\u0015a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$S'\u0006\u0002\u0002\u000e*\u001a\u0011'a$,\u0005\u0005E\u0005\u0003BAJ\u0003;k!!!&\u000b\t\u0005]\u0015\u0011T\u0001\nk:\u001c\u0007.Z2lK\u0012T1!a'4\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003?\u000b)JA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016\u0004"
)
public class RollingFileAppender extends FileAppender {
   public final File org$apache$spark$util$logging$RollingFileAppender$$activeFile;
   private final RollingPolicy rollingPolicy;
   private final int maxRetainedFiles;
   private final boolean enableCompression;

   public static int $lessinit$greater$default$5() {
      return RollingFileAppender$.MODULE$.$lessinit$greater$default$5();
   }

   public static Seq<File> getSortedRolledOverFiles(String var0, String var1) {
      return RollingFileAppender$.MODULE$.getSortedRolledOverFiles(var0, var1);
   }

   public static String GZIP_LOG_SUFFIX() {
      return RollingFileAppender$.MODULE$.GZIP_LOG_SUFFIX();
   }

   public static String ENABLE_COMPRESSION() {
      return RollingFileAppender$.MODULE$.ENABLE_COMPRESSION();
   }

   public static int DEFAULT_BUFFER_SIZE() {
      return RollingFileAppender$.MODULE$.DEFAULT_BUFFER_SIZE();
   }

   public static String RETAINED_FILES_PROPERTY() {
      return RollingFileAppender$.MODULE$.RETAINED_FILES_PROPERTY();
   }

   public static String SIZE_DEFAULT() {
      return RollingFileAppender$.MODULE$.SIZE_DEFAULT();
   }

   public static String SIZE_PROPERTY() {
      return RollingFileAppender$.MODULE$.SIZE_PROPERTY();
   }

   public static String INTERVAL_DEFAULT() {
      return RollingFileAppender$.MODULE$.INTERVAL_DEFAULT();
   }

   public static String INTERVAL_PROPERTY() {
      return RollingFileAppender$.MODULE$.INTERVAL_PROPERTY();
   }

   public static String STRATEGY_DEFAULT() {
      return RollingFileAppender$.MODULE$.STRATEGY_DEFAULT();
   }

   public static String STRATEGY_PROPERTY() {
      return RollingFileAppender$.MODULE$.STRATEGY_PROPERTY();
   }

   public RollingPolicy rollingPolicy() {
      return this.rollingPolicy;
   }

   private int maxRetainedFiles() {
      return this.maxRetainedFiles;
   }

   private boolean enableCompression() {
      return this.enableCompression;
   }

   public void stop() {
      super.stop();
   }

   public void appendToFile(byte[] bytes, int len) {
      if (this.rollingPolicy().shouldRollover((long)len)) {
         this.rollover();
         this.rollingPolicy().rolledOver();
      }

      super.appendToFile(bytes, len);
      this.rollingPolicy().bytesWritten((long)len);
   }

   private void rollover() {
      try {
         this.closeFile();
         this.moveFile();
         this.openFile();
         if (this.maxRetainedFiles() > 0) {
            this.deleteOldFiles();
         }
      } catch (Exception var2) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final RollingFileAppender $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error rolling over ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$logging$RollingFileAppender$$activeFile}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, var2);
      }

   }

   private void rotateFile(File activeFile, File rolloverFile) {
      if (this.enableCompression()) {
         File gzFile = new File((new StringBuilder()).append((Object)rolloverFile.getAbsolutePath()).append((Object)RollingFileAppender$.MODULE$.GZIP_LOG_SUFFIX()).toString());
         GZIPOutputStream gzOutputStream = null;
         InputStream inputStream = null;

         try {
            inputStream = new FileInputStream(activeFile);
            gzOutputStream = new GZIPOutputStream(new FileOutputStream(gzFile));
            IOUtils.copy(inputStream, gzOutputStream);
            inputStream.close();
            gzOutputStream.close();
            activeFile.delete();
         } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(gzOutputStream);
         }
      } else {
         Files.move(activeFile, rolloverFile);
      }

   }

   private boolean rolloverFileExist(File file) {
      return file.exists() || (new File((new StringBuilder()).append((Object)file.getAbsolutePath()).append((Object)RollingFileAppender$.MODULE$.GZIP_LOG_SUFFIX()).toString())).exists();
   }

   private void moveFile() {
      String rolloverSuffix = this.rollingPolicy().generateRolledOverFileSuffix();
      File rolloverFile = (new File(this.org$apache$spark$util$logging$RollingFileAppender$$activeFile.getParentFile(), (new StringBuilder()).append((Object)this.org$apache$spark$util$logging$RollingFileAppender$$activeFile.getName()).append((Object)rolloverSuffix).toString())).getAbsoluteFile();
      this.logDebug(new Serializable(this, rolloverFile) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RollingFileAppender $outer;
         private final File rolloverFile$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempting to rollover file ", " to file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$logging$RollingFileAppender$$activeFile, this.rolloverFile$1}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.rolloverFile$1 = rolloverFile$1;
            }
         }
      });
      if (this.org$apache$spark$util$logging$RollingFileAppender$$activeFile.exists()) {
         if (this.rolloverFileExist(rolloverFile)) {
            int i = 0;
            ObjectRef altRolloverFile = ObjectRef.create((Object)null);

            do {
               altRolloverFile.elem = (T)(new File(this.org$apache$spark$util$logging$RollingFileAppender$$activeFile.getParent(), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "", "--", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$util$logging$RollingFileAppender$$activeFile.getName(), rolloverSuffix, BoxesRunTime.boxToInteger(i)})))).getAbsoluteFile();
               ++i;
            } while(i < 10000 && this.rolloverFileExist((File)altRolloverFile.elem));

            this.logWarning(new Serializable(this, rolloverFile, altRolloverFile) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final RollingFileAppender $outer;
               private final File rolloverFile$1;
               private final ObjectRef altRolloverFile$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rollover file ", " already exists, "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.rolloverFile$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"rolled over ", " to file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$logging$RollingFileAppender$$activeFile, (File)this.altRolloverFile$1.elem}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.rolloverFile$1 = rolloverFile$1;
                     this.altRolloverFile$1 = altRolloverFile$1;
                  }
               }
            });
            this.rotateFile(this.org$apache$spark$util$logging$RollingFileAppender$$activeFile, (File)altRolloverFile.elem);
         } else {
            this.rotateFile(this.org$apache$spark$util$logging$RollingFileAppender$$activeFile, rolloverFile);
            this.logInfo(new Serializable(this, rolloverFile) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final RollingFileAppender $outer;
               private final File rolloverFile$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rolled over ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$logging$RollingFileAppender$$activeFile, this.rolloverFile$1}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.rolloverFile$1 = rolloverFile$1;
                  }
               }
            });
         }
      } else {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final RollingFileAppender $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"File ", " does not exist"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$logging$RollingFileAppender$$activeFile}));
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

   }

   public void deleteOldFiles() {
      try {
         File[] rolledoverFiles = (File[])Predef$.MODULE$.refArrayOps((Object[])this.org$apache$spark$util$logging$RollingFileAppender$$activeFile.getParentFile().listFiles(new FileFilter(this) {
            // $FF: synthetic field
            private final RollingFileAppender $outer;

            public boolean accept(File f) {
               boolean var10000;
               label25: {
                  if (f.getName().startsWith(this.$outer.org$apache$spark$util$logging$RollingFileAppender$$activeFile.getName())) {
                     File var2 = this.$outer.org$apache$spark$util$logging$RollingFileAppender$$activeFile;
                     if (f == null) {
                        if (var2 != null) {
                           break label25;
                        }
                     } else if (!f.equals(var2)) {
                        break label25;
                     }
                  }

                  var10000 = false;
                  return var10000;
               }

               var10000 = true;
               return var10000;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         })).sorted(Ordering$.MODULE$.ordered(Predef$.MODULE$.$conforms()));
         File[] filesToBeDeleted = (File[])Predef$.MODULE$.refArrayOps((Object[])rolledoverFiles).take(package$.MODULE$.max(0, rolledoverFiles.length - this.maxRetainedFiles()));
         Predef$.MODULE$.refArrayOps((Object[])filesToBeDeleted).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final RollingFileAppender $outer;

            public final boolean apply(File file) {
               this.$outer.logInfo(new Serializable(this, file) {
                  public static final long serialVersionUID = 0L;
                  private final File file$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Deleting file executor log file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1.getAbsolutePath()}));
                  }

                  public {
                     this.file$1 = file$1;
                  }
               });
               return file.delete();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
      } catch (Exception var4) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final RollingFileAppender $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Error cleaning logs in directory ").append((Object)this.$outer.org$apache$spark$util$logging$RollingFileAppender$$activeFile.getParentFile().getAbsolutePath()).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, var4);
      }

   }

   public RollingFileAppender(InputStream inputStream, File activeFile, RollingPolicy rollingPolicy, SparkConf conf, int bufferSize) {
      super(inputStream, activeFile, bufferSize);
      this.org$apache$spark$util$logging$RollingFileAppender$$activeFile = activeFile;
      this.rollingPolicy = rollingPolicy;
      this.maxRetainedFiles = conf.getInt(RollingFileAppender$.MODULE$.RETAINED_FILES_PROPERTY(), -1);
      this.enableCompression = conf.getBoolean(RollingFileAppender$.MODULE$.ENABLE_COMPRESSION(), false);
   }
}
