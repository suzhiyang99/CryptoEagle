package org.apache.spark.serializer;

import java.io.NotSerializableException;
import java.io.ObjectStreamClass;
import java.security.AccessController;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.mutable.StringBuilder;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;
import sun.security.action.GetBooleanAction;

public final class SerializationDebugger$ implements Logging {
   public static final SerializationDebugger$ MODULE$;
   private boolean enableDebugging;
   private final SerializationDebugger.ObjectStreamClassReflection org$apache$spark$serializer$SerializationDebugger$$reflect;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SerializationDebugger$();
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

   public NotSerializableException improveException(Object obj, NotSerializableException e) {
      NotSerializableException var10000;
      if (this.enableDebugging() && this.org$apache$spark$serializer$SerializationDebugger$$reflect() != null) {
         try {
            var10000 = new NotSerializableException((new StringBuilder()).append((Object)e.getMessage()).append((Object)"\nSerialization stack:\n").append((Object)((TraversableOnce)this.find(obj).map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply(String x$1) {
                  return (new StringBuilder()).append((Object)"\t- ").append((Object)x$1).toString();
               }
            }, List$.MODULE$.canBuildFrom())).mkString("\n")).toString());
         } catch (Throwable var9) {
            Option var5 = NonFatal$.MODULE$.unapply(var9);
            if (var5.isEmpty()) {
               throw var9;
            }

            Throwable t = (Throwable)var5.get();
            this.logWarning(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Exception in serialization debugger";
               }
            }, t);
            var10000 = e;
            return var10000;
         }
      } else {
         var10000 = e;
      }

      return var10000;
   }

   public List<String> find(Object obj) {
      return (new SerializationDebugger.SerializationDebugger()).visit(obj, List$.MODULE$.empty());
   }

   public boolean enableDebugging() {
      return this.enableDebugging;
   }

   public void enableDebugging_$eq(boolean x$1) {
      this.enableDebugging = x$1;
   }

   public Tuple2<Object, ObjectStreamClass> org$apache$spark$serializer$SerializationDebugger$$findObjectAndDescriptor(Object o) {
      while(true) {
         Object replaced;
         label19: {
            Class cl = o.getClass();
            ObjectStreamClass desc = ObjectStreamClass.lookupAny(cl);
            Tuple2 var7;
            if (SerializationDebugger.ObjectStreamClassMethods$.MODULE$.hasWriteReplaceMethod$extension(this.ObjectStreamClassMethods(desc))) {
               replaced = SerializationDebugger.ObjectStreamClassMethods$.MODULE$.invokeWriteReplace$extension(this.ObjectStreamClassMethods(desc), o);
               Class var10000 = replaced.getClass();
               Class var6 = o.getClass();
               if (var10000 == null) {
                  if (var6 != null) {
                     break label19;
                  }
               } else if (!var10000.equals(var6)) {
                  break label19;
               }

               var7 = new Tuple2(replaced, desc);
            } else {
               var7 = new Tuple2(o, desc);
            }

            return var7;
         }

         o = replaced;
      }
   }

   public ObjectStreamClass ObjectStreamClassMethods(ObjectStreamClass desc) {
      return desc;
   }

   public SerializationDebugger.ObjectStreamClassReflection org$apache$spark$serializer$SerializationDebugger$$reflect() {
      return this.org$apache$spark$serializer$SerializationDebugger$$reflect;
   }

   private final SerializationDebugger.ObjectStreamClassReflection liftedTree1$1() {
      SerializationDebugger.ObjectStreamClassReflection var10000;
      try {
         var10000 = new SerializationDebugger.ObjectStreamClassReflection();
      } catch (Exception var2) {
         this.logWarning(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Cannot find private methods using reflection";
            }
         }, var2);
         var10000 = null;
      }

      return var10000;
   }

   private SerializationDebugger$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.enableDebugging = !(Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.io.serialization.extendedDebugInfo"));
      this.org$apache$spark$serializer$SerializationDebugger$$reflect = this.liftedTree1$1();
   }
}
