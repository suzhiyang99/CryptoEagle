package org.apache.spark.deploy.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.serializer.DeserializationStream;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.ObjectRef;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ua!B\u0001\u0003\u0001\ta!a\u0007$jY\u0016\u001c\u0016p\u001d;f[B+'o]5ti\u0016t7-Z#oO&tWM\u0003\u0002\u0004\t\u00051Q.Y:uKJT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e#A\u0011abD\u0007\u0002\u0005%\u0011\u0001C\u0001\u0002\u0012!\u0016\u00148/[:uK:\u001cW-\u00128hS:,\u0007C\u0001\n\u0016\u001b\u0005\u0019\"B\u0001\u000b\u0007\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\f\u0014\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0007\u0001\u0003\u0006\u0004%\tAG\u0001\u0004I&\u00148\u0001A\u000b\u00027A\u0011AD\t\b\u0003;\u0001j\u0011A\b\u0006\u0002?\u0005)1oY1mC&\u0011\u0011EH\u0001\u0007!J,G-\u001a4\n\u0005\r\"#AB*ue&twM\u0003\u0002\"=!Aa\u0005\u0001B\u0001B\u0003%1$\u0001\u0003eSJ\u0004\u0003\u0002\u0003\u0015\u0001\u0005\u000b\u0007I\u0011A\u0015\u0002\u0015M,'/[1mSj,'/F\u0001+!\tYS&D\u0001-\u0015\tAc!\u0003\u0002/Y\tQ1+\u001a:jC2L'0\u001a:\t\u0011A\u0002!\u0011!Q\u0001\n)\n1b]3sS\u0006d\u0017N_3sA!)!\u0007\u0001C\u0001g\u00051A(\u001b8jiz\"2\u0001N\u001b7!\tq\u0001\u0001C\u0003\u0019c\u0001\u00071\u0004C\u0003)c\u0001\u0007!\u0006C\u00039\u0001\u0011\u0005\u0013(A\u0004qKJ\u001c\u0018n\u001d;\u0015\u0007ijt\b\u0005\u0002\u001ew%\u0011AH\b\u0002\u0005+:LG\u000fC\u0003?o\u0001\u00071$\u0001\u0003oC6,\u0007\"\u0002!8\u0001\u0004\t\u0015aA8cUB\u0011!iR\u0007\u0002\u0007*\u0011A)R\u0001\u0005Y\u0006twMC\u0001G\u0003\u0011Q\u0017M^1\n\u0005!\u001b%AB(cU\u0016\u001cG\u000fC\u0003K\u0001\u0011\u00053*A\u0005v]B,'o]5tiR\u0011!\b\u0014\u0005\u0006}%\u0003\ra\u0007\u0005\u0006\u001d\u0002!\teT\u0001\u0005e\u0016\fG-\u0006\u0002QAR\u0011\u0011+\u001d\u000b\u0003%&\u00042aU._\u001d\t!\u0016L\u0004\u0002V16\taK\u0003\u0002X3\u00051AH]8pizJ\u0011aH\u0005\u00035z\tq\u0001]1dW\u0006<W-\u0003\u0002];\n\u00191+Z9\u000b\u0005is\u0002CA0a\u0019\u0001!Q!Y'C\u0002\t\u0014\u0011\u0001V\t\u0003G\u001a\u0004\"!\b3\n\u0005\u0015t\"a\u0002(pi\"Lgn\u001a\t\u0003;\u001dL!\u0001\u001b\u0010\u0003\u0007\u0005s\u0017\u0010C\u0004k\u001b\u0006\u0005\t9A6\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007E\u0002m_zk\u0011!\u001c\u0006\u0003]z\tqA]3gY\u0016\u001cG/\u0003\u0002q[\nA1\t\\1tgR\u000bw\rC\u0003s\u001b\u0002\u00071$\u0001\u0004qe\u00164\u0017\u000e\u001f\u0005\u0006i\u0002!I!^\u0001\u0012g\u0016\u0014\u0018.\u00197ju\u0016Le\u000e^8GS2,Gc\u0001\u001ew}\")qo\u001da\u0001q\u0006!a-\u001b7f!\tIH0D\u0001{\u0015\tYX)\u0001\u0002j_&\u0011QP\u001f\u0002\u0005\r&dW\r\u0003\u0004\u0000g\u0002\u0007\u0011\u0011A\u0001\u0006m\u0006dW/\u001a\t\u0004;\u0005\r\u0011bAA\u0003=\t1\u0011I\\=SK\u001aDq!!\u0003\u0001\t\u0013\tY!A\neKN,'/[1mSj,gI]8n\r&dW-\u0006\u0003\u0002\u000e\u0005MA\u0003BA\b\u00037!B!!\u0005\u0002\u0016A\u0019q,a\u0005\u0005\r\u0005\f9A1\u0001c\u0011!\t9\"a\u0002A\u0004\u0005e\u0011!A7\u0011\t1|\u0017\u0011\u0003\u0005\u0007o\u0006\u001d\u0001\u0019\u0001="
)
public class FileSystemPersistenceEngine extends PersistenceEngine implements Logging {
   private final String dir;
   private final Serializer serializer;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   public String dir() {
      return this.dir;
   }

   public Serializer serializer() {
      return this.serializer;
   }

   public void persist(String name, Object obj) {
      this.serializeIntoFile(new File((new StringBuilder()).append((Object)this.dir()).append((Object)File.separator).append((Object)name).toString()), obj);
   }

   public void unpersist(String name) {
      File f = new File((new StringBuilder()).append((Object)this.dir()).append((Object)File.separator).append((Object)name).toString());
      if (!f.delete()) {
         this.logWarning(new Serializable(this, f) {
            public static final long serialVersionUID = 0L;
            private final File f$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.f$1.getPath()}));
            }

            public {
               this.f$1 = f$1;
            }
         });
      }

   }

   public <T> Seq<T> read(String prefix, ClassTag<T> evidence$1) {
      File[] files = (File[])Predef$.MODULE$.refArrayOps((Object[])(new File(this.dir())).listFiles()).filter(new Serializable(this, prefix) {
         public static final long serialVersionUID = 0L;
         private final String prefix$1;

         public final boolean apply(File x$1) {
            return x$1.getName().startsWith(this.prefix$1);
         }

         public {
            this.prefix$1 = prefix$1;
         }
      });
      return (Seq)Predef$.MODULE$.refArrayOps((Object[])files).map(new Serializable(this, evidence$1) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FileSystemPersistenceEngine $outer;
         private final ClassTag evidence$1$1;

         public final T apply(File file) {
            return (T)this.$outer.org$apache$spark$deploy$master$FileSystemPersistenceEngine$$deserializeFromFile(file, this.evidence$1$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.evidence$1$1 = evidence$1$1;
            }
         }
      }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
   }

   private void serializeIntoFile(File file, Object value) {
      boolean created = file.createNewFile();
      if (created) {
         FileOutputStream fileOut = new FileOutputStream(file);
         ObjectRef out = ObjectRef.create((Object)null);
         Utils$.MODULE$.tryWithSafeFinally(new Serializable(this, value, fileOut, out) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FileSystemPersistenceEngine $outer;
            private final Object value$1;
            private final FileOutputStream fileOut$1;
            private final ObjectRef out$1;

            public final SerializationStream apply() {
               this.out$1.elem = (T)this.$outer.serializer().newInstance().serializeStream(this.fileOut$1);
               return ((SerializationStream)this.out$1.elem).writeObject(this.value$1, ClassTag$.MODULE$.AnyRef());
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.value$1 = value$1;
                  this.fileOut$1 = fileOut$1;
                  this.out$1 = out$1;
               }
            }
         }, new Serializable(this, fileOut, out) {
            public static final long serialVersionUID = 0L;
            private final FileOutputStream fileOut$1;
            private final ObjectRef out$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.fileOut$1.close();
               if ((SerializationStream)this.out$1.elem != null) {
                  ((SerializationStream)this.out$1.elem).close();
               }

            }

            public {
               this.fileOut$1 = fileOut$1;
               this.out$1 = out$1;
            }
         });
      } else {
         throw new IllegalStateException((new StringBuilder()).append((Object)"Could not create file: ").append((Object)file).toString());
      }
   }

   public <T> T org$apache$spark$deploy$master$FileSystemPersistenceEngine$$deserializeFromFile(File file, ClassTag<T> m) {
      FileInputStream fileIn = new FileInputStream(file);
      DeserializationStream in = null;

      Object var10000;
      try {
         in = this.serializer().newInstance().deserializeStream(fileIn);
         var10000 = in.readObject(m);
      } finally {
         fileIn.close();
         if (in != null) {
            in.close();
         }

      }

      return (T)var10000;
   }

   public FileSystemPersistenceEngine(String dir, Serializer serializer) {
      this.dir = dir;
      this.serializer = serializer;
      Logging$class.$init$(this);
      (new File(dir)).mkdir();
   }
}
