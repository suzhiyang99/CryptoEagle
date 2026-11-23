package org.apache.spark;

import java.util.Arrays;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import scala.Function0;
import scala.Function1;
import scala.Serializable;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

public final class WritableConverter$ implements Serializable {
   public static final WritableConverter$ MODULE$;
   private final Function0<WritableConverter<Object>> intWritableConverterFn;
   private final Function0<WritableConverter<Object>> longWritableConverterFn;
   private final Function0<WritableConverter<Object>> doubleWritableConverterFn;
   private final Function0<WritableConverter<Object>> floatWritableConverterFn;
   private final Function0<WritableConverter<Object>> booleanWritableConverterFn;
   private final Function0<WritableConverter<byte[]>> bytesWritableConverterFn;
   private final Function0<WritableConverter<String>> stringWritableConverterFn;

   static {
      new WritableConverter$();
   }

   public <T, W extends Writable> WritableConverter<T> simpleWritableConverter(Function1<W, T> convert, ClassTag<W> evidence$20) {
      Class wClass = scala.reflect.package$.MODULE$.classTag(evidence$20).runtimeClass();
      return new WritableConverter<T>(new Serializable(wClass) {
         public static final long serialVersionUID = 0L;
         private final Class wClass$1;

         public final Class<W> apply(ClassTag<T> x$36) {
            return this.wClass$1;
         }

         public {
            this.wClass$1 = wClass$1;
         }
      }, new Serializable(convert) {
         public static final long serialVersionUID = 0L;
         private final Function1 convert$1;

         public final T apply(Writable x) {
            return (T)this.convert$1.apply(x);
         }

         public {
            this.convert$1 = convert$1;
         }
      });
   }

   public Function0<WritableConverter<Object>> intWritableConverterFn() {
      return this.intWritableConverterFn;
   }

   public Function0<WritableConverter<Object>> longWritableConverterFn() {
      return this.longWritableConverterFn;
   }

   public Function0<WritableConverter<Object>> doubleWritableConverterFn() {
      return this.doubleWritableConverterFn;
   }

   public Function0<WritableConverter<Object>> floatWritableConverterFn() {
      return this.floatWritableConverterFn;
   }

   public Function0<WritableConverter<Object>> booleanWritableConverterFn() {
      return this.booleanWritableConverterFn;
   }

   public Function0<WritableConverter<byte[]>> bytesWritableConverterFn() {
      return this.bytesWritableConverterFn;
   }

   public Function0<WritableConverter<String>> stringWritableConverterFn() {
      return this.stringWritableConverterFn;
   }

   public <T extends Writable> Function0<WritableConverter<T>> writableWritableConverterFn(ClassTag<T> evidence$21) {
      return new Serializable() {
         public static final long serialVersionUID = 0L;

         public final WritableConverter<T> apply() {
            return new WritableConverter<T>(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Class<T> apply(ClassTag<T> x$43) {
                  return x$43.runtimeClass();
               }
            }, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final T apply(Writable x$44) {
                  return (T)x$44;
               }
            });
         }
      };
   }

   public WritableConverter<Object> intWritableConverter() {
      return this.simpleWritableConverter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(IntWritable x$45) {
            return x$45.get();
         }
      }, ClassTag$.MODULE$.apply(IntWritable.class));
   }

   public WritableConverter<Object> longWritableConverter() {
      return this.simpleWritableConverter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply(LongWritable x$46) {
            return x$46.get();
         }
      }, ClassTag$.MODULE$.apply(LongWritable.class));
   }

   public WritableConverter<Object> doubleWritableConverter() {
      return this.simpleWritableConverter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final double apply(DoubleWritable x$47) {
            return x$47.get();
         }
      }, ClassTag$.MODULE$.apply(DoubleWritable.class));
   }

   public WritableConverter<Object> floatWritableConverter() {
      return this.simpleWritableConverter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final float apply(FloatWritable x$48) {
            return x$48.get();
         }
      }, ClassTag$.MODULE$.apply(FloatWritable.class));
   }

   public WritableConverter<Object> booleanWritableConverter() {
      return this.simpleWritableConverter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(BooleanWritable x$49) {
            return x$49.get();
         }
      }, ClassTag$.MODULE$.apply(BooleanWritable.class));
   }

   public WritableConverter<byte[]> bytesWritableConverter() {
      return this.simpleWritableConverter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final byte[] apply(BytesWritable bw) {
            return Arrays.copyOfRange(bw.getBytes(), 0, bw.getLength());
         }
      }, ClassTag$.MODULE$.apply(BytesWritable.class));
   }

   public WritableConverter<String> stringWritableConverter() {
      return this.simpleWritableConverter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(Text x$50) {
            return x$50.toString();
         }
      }, ClassTag$.MODULE$.apply(Text.class));
   }

   public <T extends Writable> WritableConverter<T> writableWritableConverter() {
      return new WritableConverter<T>(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Class<T> apply(ClassTag<T> x$51) {
            return x$51.runtimeClass();
         }
      }, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final T apply(Writable x$52) {
            return (T)x$52;
         }
      });
   }

   private Object readResolve() {
      return MODULE$;
   }

   private WritableConverter$() {
      MODULE$ = this;
      this.intWritableConverterFn = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final WritableConverter<Object> apply() {
            return WritableConverter$.MODULE$.simpleWritableConverter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(IntWritable x$37) {
                  return x$37.get();
               }
            }, ClassTag$.MODULE$.apply(IntWritable.class));
         }
      };
      this.longWritableConverterFn = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final WritableConverter<Object> apply() {
            return WritableConverter$.MODULE$.simpleWritableConverter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(LongWritable x$38) {
                  return x$38.get();
               }
            }, ClassTag$.MODULE$.apply(LongWritable.class));
         }
      };
      this.doubleWritableConverterFn = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final WritableConverter<Object> apply() {
            return WritableConverter$.MODULE$.simpleWritableConverter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(DoubleWritable x$39) {
                  return x$39.get();
               }
            }, ClassTag$.MODULE$.apply(DoubleWritable.class));
         }
      };
      this.floatWritableConverterFn = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final WritableConverter<Object> apply() {
            return WritableConverter$.MODULE$.simpleWritableConverter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final float apply(FloatWritable x$40) {
                  return x$40.get();
               }
            }, ClassTag$.MODULE$.apply(FloatWritable.class));
         }
      };
      this.booleanWritableConverterFn = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final WritableConverter<Object> apply() {
            return WritableConverter$.MODULE$.simpleWritableConverter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(BooleanWritable x$41) {
                  return x$41.get();
               }
            }, ClassTag$.MODULE$.apply(BooleanWritable.class));
         }
      };
      this.bytesWritableConverterFn = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final WritableConverter<byte[]> apply() {
            return WritableConverter$.MODULE$.simpleWritableConverter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final byte[] apply(BytesWritable bw) {
                  return Arrays.copyOfRange(bw.getBytes(), 0, bw.getLength());
               }
            }, ClassTag$.MODULE$.apply(BytesWritable.class));
         }
      };
      this.stringWritableConverterFn = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final WritableConverter<String> apply() {
            return WritableConverter$.MODULE$.simpleWritableConverter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(Text x$42) {
                  return x$42.toString();
               }
            }, ClassTag$.MODULE$.apply(Text.class));
         }
      };
   }
}
