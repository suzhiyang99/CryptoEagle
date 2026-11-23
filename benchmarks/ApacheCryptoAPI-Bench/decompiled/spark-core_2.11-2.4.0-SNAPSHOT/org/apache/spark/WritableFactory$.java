package org.apache.spark;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.ScalaRunTime$;

public final class WritableFactory$ implements Serializable {
   public static final WritableFactory$ MODULE$;

   static {
      new WritableFactory$();
   }

   public <T, W extends Writable> WritableFactory<T> simpleWritableFactory(Function1<T, W> convert, ClassTag<T> evidence$22, ClassTag<W> evidence$23) {
      Class writableClass = ((ClassTag)Predef$.MODULE$.implicitly(evidence$23)).runtimeClass();
      return new WritableFactory<T>(new Serializable(writableClass) {
         public static final long serialVersionUID = 0L;
         private final Class writableClass$1;

         public final Class<W> apply(ClassTag<T> x$53) {
            return this.writableClass$1;
         }

         public {
            this.writableClass$1 = writableClass$1;
         }
      }, convert);
   }

   public WritableFactory<Object> intWritableFactory() {
      return this.simpleWritableFactory(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final IntWritable apply(int x$54) {
            return new IntWritable(x$54);
         }
      }, ClassTag$.MODULE$.Int(), ClassTag$.MODULE$.apply(IntWritable.class));
   }

   public WritableFactory<Object> longWritableFactory() {
      return this.simpleWritableFactory(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final LongWritable apply(long x$55) {
            return new LongWritable(x$55);
         }
      }, ClassTag$.MODULE$.Long(), ClassTag$.MODULE$.apply(LongWritable.class));
   }

   public WritableFactory<Object> floatWritableFactory() {
      return this.simpleWritableFactory(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final FloatWritable apply(float x$56) {
            return new FloatWritable(x$56);
         }
      }, ClassTag$.MODULE$.Float(), ClassTag$.MODULE$.apply(FloatWritable.class));
   }

   public WritableFactory<Object> doubleWritableFactory() {
      return this.simpleWritableFactory(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final DoubleWritable apply(double x$57) {
            return new DoubleWritable(x$57);
         }
      }, ClassTag$.MODULE$.Double(), ClassTag$.MODULE$.apply(DoubleWritable.class));
   }

   public WritableFactory<Object> booleanWritableFactory() {
      return this.simpleWritableFactory(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final BooleanWritable apply(boolean x$58) {
            return new BooleanWritable(x$58);
         }
      }, ClassTag$.MODULE$.Boolean(), ClassTag$.MODULE$.apply(BooleanWritable.class));
   }

   public WritableFactory<byte[]> bytesWritableFactory() {
      return this.simpleWritableFactory(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final BytesWritable apply(byte[] x$59) {
            return new BytesWritable(x$59);
         }
      }, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)), ClassTag$.MODULE$.apply(BytesWritable.class));
   }

   public WritableFactory<String> stringWritableFactory() {
      return this.simpleWritableFactory(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Text apply(String x$60) {
            return new Text(x$60);
         }
      }, ClassTag$.MODULE$.apply(String.class), ClassTag$.MODULE$.apply(Text.class));
   }

   public <T extends Writable> WritableFactory<T> writableWritableFactory(ClassTag<T> evidence$24) {
      return this.simpleWritableFactory(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final T apply(T w) {
            return (T)w;
         }
      }, evidence$24, evidence$24);
   }

   private Object readResolve() {
      return MODULE$;
   }

   private WritableFactory$() {
      MODULE$ = this;
   }
}
