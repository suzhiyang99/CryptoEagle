package org.apache.spark.scheduler;

import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015d\u0001B\u0001\u0003\u0001-\u0011q\"\u00138qkR4uN]7bi&sgm\u001c\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001aE\u0002\u0001\u0019I\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\n\u0017\u001b\u0005!\"BA\u000b\u0005\u0003!Ig\u000e^3s]\u0006d\u0017BA\f\u0015\u0005\u001daunZ4j]\u001eD\u0001\"\u0007\u0001\u0003\u0006\u0004%\tAG\u0001\u000eG>tg-[4ve\u0006$\u0018n\u001c8\u0016\u0003m\u0001\"\u0001H\u0011\u000e\u0003uQ!AH\u0010\u0002\t\r|gN\u001a\u0006\u0003A\u0019\ta\u0001[1e_>\u0004\u0018B\u0001\u0012\u001e\u00055\u0019uN\u001c4jOV\u0014\u0018\r^5p]\"AA\u0005\u0001B\u0001B\u0003%1$\u0001\bd_:4\u0017nZ;sCRLwN\u001c\u0011\t\u0011\u0019\u0002!Q1A\u0005\u0002\u001d\n\u0001#\u001b8qkR4uN]7bi\u000ec\u0017M\u001f>\u0016\u0003!\u0002$!\u000b\u001a\u0011\u0007)j\u0003G\u0004\u0002\u000eW%\u0011AFD\u0001\u0007!J,G-\u001a4\n\u00059z#!B\"mCN\u001c(B\u0001\u0017\u000f!\t\t$\u0007\u0004\u0001\u0005\u0013M\"\u0014\u0011!A\u0001\u0006\u0003Q$aA0%c!AQ\u0007\u0001B\u0001B\u0003%a'A\tj]B,HOR8s[\u0006$8\t\\1{u\u0002\u0002$aN\u001d\u0011\u0007)j\u0003\b\u0005\u00022s\u0011I1\u0007NA\u0001\u0002\u0003\u0015\tAO\t\u0003wy\u0002\"!\u0004\u001f\n\u0005ur!a\u0002(pi\"Lgn\u001a\t\u0003\u001b}J!\u0001\u0011\b\u0003\u0007\u0005s\u0017\u0010\u0003\u0005C\u0001\t\u0015\r\u0011\"\u0001D\u0003\u0011\u0001\u0018\r\u001e5\u0016\u0003\u0011\u0003\"AK#\n\u0005\u0019{#AB*ue&tw\r\u0003\u0005I\u0001\t\u0005\t\u0015!\u0003E\u0003\u0015\u0001\u0018\r\u001e5!\u0011\u0015Q\u0005\u0001\"\u0001L\u0003\u0019a\u0014N\\5u}Q!AJT(U!\ti\u0005!D\u0001\u0003\u0011\u0015I\u0012\n1\u0001\u001c\u0011\u00151\u0013\n1\u0001Qa\t\t6\u000bE\u0002+[I\u0003\"!M*\u0005\u0013Mz\u0015\u0011!A\u0001\u0006\u0003Q\u0004\"\u0002\"J\u0001\u0004!\u0005b\u0002,\u0001\u0001\u0004%\taV\u0001\u0015[\u0006\u0004(/\u001a3vG\u0016Le\u000e];u\r>\u0014X.\u0019;\u0016\u0003a\u0003\"!D-\n\u0005is!a\u0002\"p_2,\u0017M\u001c\u0005\b9\u0002\u0001\r\u0011\"\u0001^\u0003ai\u0017\r\u001d:fIV\u001cW-\u00138qkR4uN]7bi~#S-\u001d\u000b\u0003=\u0006\u0004\"!D0\n\u0005\u0001t!\u0001B+oSRDqAY.\u0002\u0002\u0003\u0007\u0001,A\u0002yIEBa\u0001\u001a\u0001!B\u0013A\u0016!F7baJ,G-^2f\u0013:\u0004X\u000f\u001e$pe6\fG\u000f\t\u0005\bM\u0002\u0001\r\u0011\"\u0001X\u0003Ei\u0017\r\u001d:fI&s\u0007/\u001e;G_Jl\u0017\r\u001e\u0005\bQ\u0002\u0001\r\u0011\"\u0001j\u0003Ui\u0017\r\u001d:fI&s\u0007/\u001e;G_Jl\u0017\r^0%KF$\"A\u00186\t\u000f\t<\u0017\u0011!a\u00011\"1A\u000e\u0001Q!\na\u000b!#\\1qe\u0016$\u0017J\u001c9vi\u001a{'/\\1uA!)a\u000e\u0001C!_\u0006AAo\\*ue&tw\rF\u0001E\u0011\u0015\t\b\u0001\"\u0011s\u0003!A\u0017m\u001d5D_\u0012,G#A:\u0011\u00055!\u0018BA;\u000f\u0005\rIe\u000e\u001e\u0005\u0006o\u0002!\t\u0005_\u0001\u0007KF,\u0018\r\\:\u0015\u0005aK\b\"\u0002>w\u0001\u0004q\u0014!B8uQ\u0016\u0014\b\"\u0002?\u0001\t\u0013i\u0018\u0001\u0003<bY&$\u0017\r^3\u0015\u0003yCaa \u0001\u0005\n\u0005\u0005\u0011\u0001\t9sK\u001adunY:Ge>lW*\u00199sK\u0012,8-Z%oaV$hi\u001c:nCR$\"!a\u0001\u0011\r\u0005\u0015\u0011qBA\n\u001b\t\t9A\u0003\u0003\u0002\n\u0005-\u0011!C5n[V$\u0018M\u00197f\u0015\r\tiAD\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA\t\u0003\u000f\u00111aU3u!\ri\u0015QC\u0005\u0004\u0003/\u0011!!C*qY&$\u0018J\u001c4p\u0011\u001d\tY\u0002\u0001C\u0005\u0003\u0003\tQ\u0004\u001d:fM2{7m\u001d$s_6l\u0015\r\u001d:fI&s\u0007/\u001e;G_Jl\u0017\r\u001e\u0005\b\u0003?\u0001A\u0011BA\u0001\u0003Y1\u0017N\u001c3Qe\u00164WM\u001d:fI2{7-\u0019;j_:\u001c\bf\u0001\u0001\u0002$A!\u0011QEA\u0016\u001b\t\t9CC\u0002\u0002*\u0011\t!\"\u00198o_R\fG/[8o\u0013\u0011\ti#a\n\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5\b\u000f\u0005E\"\u0001#\u0001\u00024\u0005y\u0011J\u001c9vi\u001a{'/\\1u\u0013:4w\u000eE\u0002N\u0003k1a!\u0001\u0002\t\u0002\u0005]2cAA\u001b\u0019!9!*!\u000e\u0005\u0002\u0005mBCAA\u001a\u0011!\ty$!\u000e\u0005\u0002\u0005\u0005\u0013!G2p[B,H/\u001a)sK\u001a,'O]3e\u0019>\u001c\u0017\r^5p]N$B!a\u0011\u0002JA1!&!\u0012E\u0003\u0007I1!a\u00120\u0005\ri\u0015\r\u001d\u0005\t\u0003\u0017\ni\u00041\u0001\u0002N\u00059am\u001c:nCR\u001c\b#BA(\u0003?be\u0002BA)\u00037rA!a\u0015\u0002Z5\u0011\u0011Q\u000b\u0006\u0004\u0003/R\u0011A\u0002\u001fs_>$h(C\u0001\u0010\u0013\r\tiFD\u0001\ba\u0006\u001c7.Y4f\u0013\u0011\t\t'a\u0019\u0003\u0007M+\u0017OC\u0002\u0002^9\u0001"
)
public class InputFormatInfo implements Logging {
   private final Configuration configuration;
   private final Class<?> inputFormatClazz;
   private final String path;
   private boolean mapreduceInputFormat;
   private boolean mapredInputFormat;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Map<String, Set<SplitInfo>> computePreferredLocations(Seq<InputFormatInfo> var0) {
      return InputFormatInfo$.MODULE$.computePreferredLocations(var0);
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   public Configuration configuration() {
      return this.configuration;
   }

   public Class<?> inputFormatClazz() {
      return this.inputFormatClazz;
   }

   public String path() {
      return this.path;
   }

   public boolean mapreduceInputFormat() {
      return this.mapreduceInputFormat;
   }

   public void mapreduceInputFormat_$eq(boolean x$1) {
      this.mapreduceInputFormat = x$1;
   }

   public boolean mapredInputFormat() {
      return this.mapredInputFormat;
   }

   public void mapredInputFormat_$eq(boolean x$1) {
      this.mapredInputFormat = x$1;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"InputFormatInfo ").append((Object)super.toString()).append((Object)" .. inputFormatClazz ").append((Object)this.inputFormatClazz()).append((Object)", ").append((Object)"path : ").append((Object)this.path()).toString();
   }

   public int hashCode() {
      int hashCode = this.inputFormatClazz().hashCode();
      hashCode = hashCode * 31 + this.path().hashCode();
      return hashCode;
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof InputFormatInfo) {
         boolean var8;
         label29: {
            label28: {
               label27: {
                  InputFormatInfo var3 = (InputFormatInfo)other;
                  Class var10000 = this.inputFormatClazz();
                  Class var5 = var3.inputFormatClazz();
                  if (var10000 == null) {
                     if (var5 != null) {
                        break label27;
                     }
                  } else if (!var10000.equals(var5)) {
                     break label27;
                  }

                  String var7 = this.path();
                  String var6 = var3.path();
                  if (var7 == null) {
                     if (var6 == null) {
                        break label28;
                     }
                  } else if (var7.equals(var6)) {
                     break label28;
                  }
               }

               var8 = false;
               break label29;
            }

            var8 = true;
         }

         var4 = var8;
      } else {
         var4 = false;
      }

      return var4;
   }

   private void validate() {
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final InputFormatInfo $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"validate InputFormatInfo : ").append((Object)this.$outer.inputFormatClazz()).append((Object)", path  ").append((Object)this.$outer.path()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });

      try {
         if (InputFormat.class.isAssignableFrom(this.inputFormatClazz())) {
            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "inputformat is from mapreduce package";
               }
            });
            this.mapreduceInputFormat_$eq(true);
         } else {
            if (!org.apache.hadoop.mapred.InputFormat.class.isAssignableFrom(this.inputFormatClazz())) {
               throw new IllegalArgumentException((new StringBuilder()).append((Object)"Specified inputformat ").append((Object)this.inputFormatClazz()).append((Object)" is NOT a supported input format ? does not implement either of the supported hadoop ").append((Object)"api's").toString());
            }

            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "inputformat is from mapred package";
               }
            });
            this.mapredInputFormat_$eq(true);
         }

      } catch (ClassNotFoundException var2) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)"Specified inputformat ").append((Object)this.inputFormatClazz()).append((Object)" cannot be found ?").toString(), var2);
      }
   }

   private Set<SplitInfo> prefLocsFromMapreduceInputFormat() {
      JobConf conf = new JobConf(this.configuration());
      SparkHadoopUtil$.MODULE$.get().addCredentials(conf);
      FileInputFormat.setInputPaths(conf, this.path());
      InputFormat instance = (InputFormat)ReflectionUtils.newInstance(this.inputFormatClazz(), conf);
      Job job = Job.getInstance(conf);
      ArrayBuffer retval = new ArrayBuffer();
      List list = instance.getSplits(job);
      ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter(list).asScala()).foreach(new Serializable(this, retval) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final InputFormatInfo $outer;
         private final ArrayBuffer retval$1;

         public final ArrayBuffer<SplitInfo> apply(InputSplit split) {
            return this.retval$1.$plus$plus$eq(SplitInfo$.MODULE$.toSplitInfo(this.$outer.inputFormatClazz(), this.$outer.path(), split));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.retval$1 = retval$1;
            }
         }
      });
      return retval.toSet();
   }

   private Set<SplitInfo> prefLocsFromMapredInputFormat() {
      JobConf jobConf = new JobConf(this.configuration());
      SparkHadoopUtil$.MODULE$.get().addCredentials(jobConf);
      FileInputFormat.setInputPaths(jobConf, this.path());
      org.apache.hadoop.mapred.InputFormat instance = (org.apache.hadoop.mapred.InputFormat)ReflectionUtils.newInstance(this.inputFormatClazz(), jobConf);
      ArrayBuffer retval = new ArrayBuffer();
      Predef$.MODULE$.refArrayOps((Object[])instance.getSplits(jobConf, jobConf.getNumMapTasks())).foreach(new Serializable(this, retval) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final InputFormatInfo $outer;
         private final ArrayBuffer retval$2;

         public final ArrayBuffer<SplitInfo> apply(org.apache.hadoop.mapred.InputSplit elem) {
            return this.retval$2.$plus$plus$eq(SplitInfo$.MODULE$.toSplitInfo(this.$outer.inputFormatClazz(), this.$outer.path(), elem));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.retval$2 = retval$2;
            }
         }
      });
      return retval.toSet();
   }

   public Set<SplitInfo> org$apache$spark$scheduler$InputFormatInfo$$findPreferredLocations() {
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final InputFormatInfo $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"mapreduceInputFormat : ").append((Object)BoxesRunTime.boxToBoolean(this.$outer.mapreduceInputFormat())).append((Object)", mapredInputFormat : ").append((Object)BoxesRunTime.boxToBoolean(this.$outer.mapredInputFormat())).append((Object)", inputFormatClazz : ").append((Object)this.$outer.inputFormatClazz()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Set var10000;
      if (this.mapreduceInputFormat()) {
         var10000 = this.prefLocsFromMapreduceInputFormat();
      } else {
         Predef$.MODULE$.assert(this.mapredInputFormat());
         var10000 = this.prefLocsFromMapredInputFormat();
      }

      return var10000;
   }

   public InputFormatInfo(Configuration configuration, Class<?> inputFormatClazz, String path) {
      this.configuration = configuration;
      this.inputFormatClazz = inputFormatClazz;
      this.path = path;
      Logging$class.$init$(this);
      this.mapreduceInputFormat = false;
      this.mapredInputFormat = false;
      this.validate();
   }
}
