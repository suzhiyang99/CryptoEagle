package org.apache.spark.util;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import javax.management.MBeanServer;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.collection.OpenHashSet;
import org.apache.spark.util.collection.OpenHashSet$mcI$sp;
import org.slf4j.Logger;
import org.spark_project.guava.collect.MapMaker;
import scala.Array$;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.LongRef;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;

@DeveloperApi
public final class SizeEstimator$ implements Logging {
   public static final SizeEstimator$ MODULE$;
   private final int BYTE_SIZE;
   private final int BOOLEAN_SIZE;
   private final int CHAR_SIZE;
   private final int SHORT_SIZE;
   private final int INT_SIZE;
   private final int LONG_SIZE;
   private final int FLOAT_SIZE;
   private final int DOUBLE_SIZE;
   private final List<Object> fieldSizes;
   private final int ALIGN_SIZE;
   private final ConcurrentMap<Class<?>, SizeEstimator.ClassInfo> classInfos;
   private boolean is64bit;
   private boolean isCompressedOops;
   private int org$apache$spark$util$SizeEstimator$$pointerSize;
   private int objectSize;
   private final int ARRAY_SIZE_FOR_SAMPLING;
   private final int ARRAY_SAMPLE_SIZE;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SizeEstimator$();
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

   public long estimate(Object obj) {
      return this.org$apache$spark$util$SizeEstimator$$estimate(obj, new IdentityHashMap());
   }

   private int BYTE_SIZE() {
      return this.BYTE_SIZE;
   }

   private int BOOLEAN_SIZE() {
      return this.BOOLEAN_SIZE;
   }

   private int CHAR_SIZE() {
      return this.CHAR_SIZE;
   }

   private int SHORT_SIZE() {
      return this.SHORT_SIZE;
   }

   private int INT_SIZE() {
      return this.INT_SIZE;
   }

   private int LONG_SIZE() {
      return this.LONG_SIZE;
   }

   private int FLOAT_SIZE() {
      return this.FLOAT_SIZE;
   }

   private int DOUBLE_SIZE() {
      return this.DOUBLE_SIZE;
   }

   private List<Object> fieldSizes() {
      return this.fieldSizes;
   }

   private int ALIGN_SIZE() {
      return this.ALIGN_SIZE;
   }

   private ConcurrentMap<Class<?>, SizeEstimator.ClassInfo> classInfos() {
      return this.classInfos;
   }

   private boolean is64bit() {
      return this.is64bit;
   }

   private void is64bit_$eq(boolean x$1) {
      this.is64bit = x$1;
   }

   private boolean isCompressedOops() {
      return this.isCompressedOops;
   }

   private void isCompressedOops_$eq(boolean x$1) {
      this.isCompressedOops = x$1;
   }

   public int org$apache$spark$util$SizeEstimator$$pointerSize() {
      return this.org$apache$spark$util$SizeEstimator$$pointerSize;
   }

   private void org$apache$spark$util$SizeEstimator$$pointerSize_$eq(int x$1) {
      this.org$apache$spark$util$SizeEstimator$$pointerSize = x$1;
   }

   private int objectSize() {
      return this.objectSize;
   }

   private void objectSize_$eq(int x$1) {
      this.objectSize = x$1;
   }

   private void initialize() {
      String arch = System.getProperty("os.arch");
      this.is64bit_$eq(arch.contains("64") || arch.contains("s390x"));
      this.isCompressedOops_$eq(this.getIsCompressedOops());
      this.objectSize_$eq(this.is64bit() ? (this.isCompressedOops() ? 12 : 16) : 8);
      this.org$apache$spark$util$SizeEstimator$$pointerSize_$eq(this.is64bit() && !this.isCompressedOops() ? 8 : 4);
      this.classInfos().clear();
      this.classInfos().put(Object.class, new SizeEstimator.ClassInfo((long)this.objectSize(), Nil$.MODULE$));
   }

   private boolean getIsCompressedOops() {
      if (System.getProperty("spark.test.useCompressedOops") != null) {
         return (new StringOps(Predef$.MODULE$.augmentString(System.getProperty("spark.test.useCompressedOops")))).toBoolean();
      } else if (System.getProperty("java.vendor").contains("IBM")) {
         return System.getProperty("java.vm.info").contains("Compressed Ref");
      } else {
         try {
            String hotSpotMBeanName = "com.sun.management:type=HotSpotDiagnostic";
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            Class hotSpotMBeanClass = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
            Method getVMMethod = hotSpotMBeanClass.getDeclaredMethod("getVMOption", Class.forName("java.lang.String"));
            Object bean = ManagementFactory.newPlatformMXBeanProxy(server, hotSpotMBeanName, hotSpotMBeanClass);
            return getVMMethod.invoke(bean, "UseCompressedOops").toString().contains("true");
         } catch (Exception var9) {
            boolean guess = Runtime.getRuntime().maxMemory() < 34359738368L;
            String guessInWords = guess ? "yes" : "not";
            this.logWarning(new Serializable(guessInWords) {
               public static final long serialVersionUID = 0L;
               private final String guessInWords$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Failed to check whether UseCompressedOops is set; assuming ").append((Object)this.guessInWords$1).toString();
               }

               public {
                  this.guessInWords$1 = guessInWords$1;
               }
            });
            return guess;
         }
      }
   }

   public long org$apache$spark$util$SizeEstimator$$estimate(Object obj, IdentityHashMap<Object, Object> visited) {
      SizeEstimator.SearchState state = new SizeEstimator.SearchState(visited);
      state.enqueue(obj);

      while(!state.isFinished()) {
         this.visitSingleObject(state.dequeue(), state);
      }

      return state.size();
   }

   private void visitSingleObject(Object obj, SizeEstimator.SearchState state) {
      Class cls = obj.getClass();
      if (cls.isArray()) {
         this.visitArray(obj, cls, state);
      } else if (!cls.getName().startsWith("scala.reflect") && !(obj instanceof ClassLoader) && !(obj instanceof Class)) {
         if (obj instanceof KnownSizeEstimation) {
            KnownSizeEstimation var5 = (KnownSizeEstimation)obj;
            state.size_$eq(state.size() + var5.estimatedSize());
            BoxedUnit var6 = BoxedUnit.UNIT;
         } else {
            SizeEstimator.ClassInfo classInfo = this.getClassInfo(cls);
            state.size_$eq(state.size() + this.alignSize(classInfo.shellSize()));
            classInfo.pointerFields().foreach(new Serializable(obj, state) {
               public static final long serialVersionUID = 0L;
               private final Object obj$1;
               private final SizeEstimator.SearchState state$1;

               public final void apply(Field field) {
                  this.state$1.enqueue(field.get(this.obj$1));
               }

               public {
                  this.obj$1 = obj$1;
                  this.state$1 = state$1;
               }
            });
            BoxedUnit var8 = BoxedUnit.UNIT;
         }
      }

   }

   private int ARRAY_SIZE_FOR_SAMPLING() {
      return this.ARRAY_SIZE_FOR_SAMPLING;
   }

   private int ARRAY_SAMPLE_SIZE() {
      return this.ARRAY_SAMPLE_SIZE;
   }

   private void visitArray(Object array, Class<?> arrayClass, SizeEstimator.SearchState state) {
      int length = ScalaRunTime$.MODULE$.array_length(array);
      Class elementClass = arrayClass.getComponentType();
      long arrSize = this.alignSize((long)(this.objectSize() + this.INT_SIZE()));
      if (elementClass.isPrimitive()) {
         arrSize += this.alignSize((long)length * (long)this.org$apache$spark$util$SizeEstimator$$primitiveSize(elementClass));
         state.size_$eq(state.size() + arrSize);
      } else {
         arrSize += this.alignSize((long)length * (long)this.org$apache$spark$util$SizeEstimator$$pointerSize());
         state.size_$eq(state.size() + arrSize);
         if (length <= this.ARRAY_SIZE_FOR_SAMPLING()) {
            for(int arrayIndex = 0; arrayIndex < length; ++arrayIndex) {
               state.enqueue(ScalaRunTime$.MODULE$.array_apply(array, arrayIndex));
            }
         } else {
            Random rand = new Random(42L);
            OpenHashSet drawn = new OpenHashSet$mcI$sp(2 * this.ARRAY_SAMPLE_SIZE(), ClassTag$.MODULE$.Int());
            long s1 = this.sampleArray(array, state, rand, drawn, length);
            long s2 = this.sampleArray(array, state, rand, drawn, length);
            long size = scala.math.package$.MODULE$.min(s1, s2);
            state.size_$eq(state.size() + scala.math.package$.MODULE$.max(s1, s2) + size * (long)((length - this.ARRAY_SAMPLE_SIZE()) / this.ARRAY_SAMPLE_SIZE()));
         }
      }

   }

   private long sampleArray(Object array, SizeEstimator.SearchState state, Random rand, OpenHashSet<Object> drawn, int length) {
      LongRef size = LongRef.create(0L);
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.ARRAY_SAMPLE_SIZE()).foreach$mVc$sp(new Serializable(array, state, rand, drawn, length, size) {
         public static final long serialVersionUID = 0L;
         private final Object array$1;
         private final SizeEstimator.SearchState state$2;
         private final Random rand$1;
         private final OpenHashSet drawn$1;
         private final int length$1;
         private final LongRef size$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            int index = 0;

            do {
               index = this.rand$1.nextInt(this.length$1);
            } while(this.drawn$1.contains$mcI$sp(index));

            this.drawn$1.add$mcI$sp(index);
            Object obj = ScalaRunTime$.MODULE$.array_apply(this.array$1, index);
            if (obj != null) {
               this.size$1.elem += SizeEstimator$.MODULE$.org$apache$spark$util$SizeEstimator$$estimate(obj, this.state$2.visited());
            }

         }

         public {
            this.array$1 = array$1;
            this.state$2 = state$2;
            this.rand$1 = rand$1;
            this.drawn$1 = drawn$1;
            this.length$1 = length$1;
            this.size$1 = size$1;
         }
      });
      return size.elem;
   }

   public int org$apache$spark$util$SizeEstimator$$primitiveSize(Class<?> cls) {
      int var10000;
      label132: {
         Class var2 = Byte.TYPE;
         if (cls == null) {
            if (var2 == null) {
               break label132;
            }
         } else if (cls.equals(var2)) {
            break label132;
         }

         label133: {
            Class var3 = Boolean.TYPE;
            if (cls == null) {
               if (var3 == null) {
                  break label133;
               }
            } else if (cls.equals(var3)) {
               break label133;
            }

            label134: {
               Class var4 = Character.TYPE;
               if (cls == null) {
                  if (var4 == null) {
                     break label134;
                  }
               } else if (cls.equals(var4)) {
                  break label134;
               }

               label135: {
                  Class var5 = Short.TYPE;
                  if (cls == null) {
                     if (var5 == null) {
                        break label135;
                     }
                  } else if (cls.equals(var5)) {
                     break label135;
                  }

                  label136: {
                     Class var6 = Integer.TYPE;
                     if (cls == null) {
                        if (var6 == null) {
                           break label136;
                        }
                     } else if (cls.equals(var6)) {
                        break label136;
                     }

                     label137: {
                        Class var7 = Long.TYPE;
                        if (cls == null) {
                           if (var7 == null) {
                              break label137;
                           }
                        } else if (cls.equals(var7)) {
                           break label137;
                        }

                        label138: {
                           Class var8 = Float.TYPE;
                           if (cls == null) {
                              if (var8 == null) {
                                 break label138;
                              }
                           } else if (cls.equals(var8)) {
                              break label138;
                           }

                           label52: {
                              Class var9 = Double.TYPE;
                              if (cls == null) {
                                 if (var9 == null) {
                                    break label52;
                                 }
                              } else if (cls.equals(var9)) {
                                 break label52;
                              }

                              throw new IllegalArgumentException((new StringBuilder()).append((Object)"Non-primitive class ").append((Object)cls).append((Object)" passed to primitiveSize()").toString());
                           }

                           var10000 = this.DOUBLE_SIZE();
                           return var10000;
                        }

                        var10000 = this.FLOAT_SIZE();
                        return var10000;
                     }

                     var10000 = this.LONG_SIZE();
                     return var10000;
                  }

                  var10000 = this.INT_SIZE();
                  return var10000;
               }

               var10000 = this.SHORT_SIZE();
               return var10000;
            }

            var10000 = this.CHAR_SIZE();
            return var10000;
         }

         var10000 = this.BOOLEAN_SIZE();
         return var10000;
      }

      var10000 = this.BYTE_SIZE();
      return var10000;
   }

   private SizeEstimator.ClassInfo getClassInfo(Class<?> cls) {
      SizeEstimator.ClassInfo info = (SizeEstimator.ClassInfo)this.classInfos().get(cls);
      if (info == null) {
         SizeEstimator.ClassInfo parent = this.getClassInfo(cls.getSuperclass());
         LongRef shellSize = LongRef.create(parent.shellSize());
         ObjectRef pointerFields = ObjectRef.create(parent.pointerFields());
         int[] sizeCount = (int[])Array$.MODULE$.fill(BoxesRunTime.unboxToInt(this.fieldSizes().max(Ordering.Int$.MODULE$)) + 1, new Serializable() {
            public static final long serialVersionUID = 0L;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return 0;
            }
         }, ClassTag$.MODULE$.Int());
         Predef$.MODULE$.refArrayOps((Object[])cls.getDeclaredFields()).foreach(new Serializable(pointerFields, sizeCount) {
            public static final long serialVersionUID = 0L;
            private final ObjectRef pointerFields$1;
            private final int[] sizeCount$1;

            public final void apply(Field field) {
               if (!Modifier.isStatic(field.getModifiers())) {
                  Class fieldClass = field.getType();
                  if (fieldClass.isPrimitive()) {
                     int var3 = SizeEstimator$.MODULE$.org$apache$spark$util$SizeEstimator$$primitiveSize(fieldClass);
                     int var10002 = this.sizeCount$1[var3]++;
                  } else {
                     field.setAccessible(true);
                     int var4 = SizeEstimator$.MODULE$.org$apache$spark$util$SizeEstimator$$pointerSize();
                     int var6 = this.sizeCount$1[var4]++;
                     this.pointerFields$1.elem = (T)(this.pointerFields$1.elem).$colon$colon(field);
                  }
               }

            }

            public {
               this.pointerFields$1 = pointerFields$1;
               this.sizeCount$1 = sizeCount$1;
            }
         });
         LongRef alignedSize = LongRef.create(shellSize.elem);
         this.fieldSizes().withFilter(new Serializable(sizeCount) {
            public static final long serialVersionUID = 0L;
            private final int[] sizeCount$1;

            public final boolean apply(int size) {
               return this.apply$mcZI$sp(size);
            }

            public boolean apply$mcZI$sp(int size) {
               return this.sizeCount$1[size] > 0;
            }

            public {
               this.sizeCount$1 = sizeCount$1;
            }
         }).foreach(new Serializable(shellSize, sizeCount, alignedSize) {
            public static final long serialVersionUID = 0L;
            private final LongRef shellSize$1;
            private final int[] sizeCount$1;
            private final LongRef alignedSize$1;

            public final void apply(int size) {
               this.apply$mcVI$sp(size);
            }

            public void apply$mcVI$sp(int size) {
               long count = (long)this.sizeCount$1[size];
               this.alignedSize$1.elem = scala.math.package$.MODULE$.max(this.alignedSize$1.elem, SizeEstimator$.MODULE$.org$apache$spark$util$SizeEstimator$$alignSizeUp(this.shellSize$1.elem, size) + (long)size * count);
               this.shellSize$1.elem += (long)size * count;
            }

            public {
               this.shellSize$1 = shellSize$1;
               this.sizeCount$1 = sizeCount$1;
               this.alignedSize$1 = alignedSize$1;
            }
         });
         shellSize.elem = this.org$apache$spark$util$SizeEstimator$$alignSizeUp(alignedSize.elem, this.org$apache$spark$util$SizeEstimator$$pointerSize());
         SizeEstimator.ClassInfo newInfo = new SizeEstimator.ClassInfo(shellSize.elem, (List)pointerFields.elem);
         this.classInfos().put(cls, newInfo);
         return newInfo;
      } else {
         return info;
      }
   }

   private long alignSize(long size) {
      return this.org$apache$spark$util$SizeEstimator$$alignSizeUp(size, this.ALIGN_SIZE());
   }

   public long org$apache$spark$util$SizeEstimator$$alignSizeUp(long size, int alignSize) {
      return size + (long)alignSize - 1L & (long)(~(alignSize - 1));
   }

   private SizeEstimator$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.BYTE_SIZE = 1;
      this.BOOLEAN_SIZE = 1;
      this.CHAR_SIZE = 2;
      this.SHORT_SIZE = 2;
      this.INT_SIZE = 4;
      this.LONG_SIZE = 8;
      this.FLOAT_SIZE = 4;
      this.DOUBLE_SIZE = 8;
      this.fieldSizes = List$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{8, 4, 2, 1}));
      this.ALIGN_SIZE = 8;
      this.classInfos = (new MapMaker()).weakKeys().makeMap();
      this.is64bit = false;
      this.isCompressedOops = false;
      this.org$apache$spark$util$SizeEstimator$$pointerSize = 4;
      this.objectSize = 8;
      this.initialize();
      this.ARRAY_SIZE_FOR_SAMPLING = 400;
      this.ARRAY_SAMPLE_SIZE = 100;
   }
}
