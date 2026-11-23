package org.apache.spark.util;

import java.io.ObjectInputStream;
import org.apache.spark.ContextCleaner;
import org.apache.spark.InternalAccumulator$;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.scheduler.AccumulableInfo;
import org.apache.spark.scheduler.AccumulableInfo$;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ec!B\u0001\u0003\u0003\u0003Y!!D!dGVlW\u000f\\1u_J4&G\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\u000b\u0004\u0019u93c\u0001\u0001\u000e'A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"A\u0004\u000b\n\u0005Uy!\u0001D*fe&\fG.\u001b>bE2,\u0007\"B\f\u0001\t\u0003A\u0012A\u0002\u001fj]&$h\bF\u0001\u001a!\u0011Q\u0002a\u0007\u0014\u000e\u0003\t\u0001\"\u0001H\u000f\r\u0001\u0011)a\u0004\u0001b\u0001?\t\u0011\u0011JT\t\u0003A\r\u0002\"AD\u0011\n\u0005\tz!a\u0002(pi\"Lgn\u001a\t\u0003\u001d\u0011J!!J\b\u0003\u0007\u0005s\u0017\u0010\u0005\u0002\u001dO\u0011)\u0001\u0006\u0001b\u0001?\t\u0019q*\u0016+\t\u0015)\u0002\u0001\u0019!a\u0001\n\u0003!1&\u0001\u0005nKR\fG-\u0019;b+\u0005a\u0003C\u0001\u000e.\u0013\tq#AA\nBG\u000e,X.\u001e7bi>\u0014X*\u001a;bI\u0006$\u0018\r\u0003\u00061\u0001\u0001\u0007\t\u0019!C\u0001\tE\nA\"\\3uC\u0012\fG/Y0%KF$\"AM\u001b\u0011\u00059\u0019\u0014B\u0001\u001b\u0010\u0005\u0011)f.\u001b;\t\u000fYz\u0013\u0011!a\u0001Y\u0005\u0019\u0001\u0010J\u0019\t\ra\u0002\u0001\u0015)\u0003-\u0003%iW\r^1eCR\f\u0007\u0005\u0003\u0004;\u0001\u0001\u0006KaO\u0001\rCR$%/\u001b<feNKG-\u001a\t\u0003\u001dqJ!!P\b\u0003\u000f\t{w\u000e\\3b]\"1q\b\u0001C\u0001\t\u0001\u000b\u0001B]3hSN$XM\u001d\u000b\u0005e\u0005;5\u000bC\u0003C}\u0001\u00071)\u0001\u0002tGB\u0011A)R\u0007\u0002\t%\u0011a\t\u0002\u0002\r'B\f'o[\"p]R,\u0007\u0010\u001e\u0005\b\u0011z\u0002\n\u00111\u0001J\u0003\u0011q\u0017-\\3\u0011\u00079QE*\u0003\u0002L\u001f\t1q\n\u001d;j_:\u0004\"!\u0014)\u000f\u00059q\u0015BA(\u0010\u0003\u0019\u0001&/\u001a3fM&\u0011\u0011K\u0015\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005={\u0001b\u0002+?!\u0003\u0005\raO\u0001\u0012G>,h\u000e\u001e$bS2,GMV1mk\u0016\u001c\b\"\u0002,\u0001\t\u000b9\u0016\u0001D5t%\u0016<\u0017n\u001d;fe\u0016$W#A\u001e\t\u000be\u0003A\u0011\u0002.\u0002+\u0005\u001c8/\u001a:u\u001b\u0016$\u0018\rZ1uC:{GOT;mYR\t!\u0007C\u0003]\u0001\u0011\u0015Q,\u0001\u0002jIV\ta\f\u0005\u0002\u000f?&\u0011\u0001m\u0004\u0002\u0005\u0019>tw\rC\u0003I\u0001\u0011\u0015!-F\u0001J\u0011\u0019!\u0006\u0001\"\u0002\u0005/\"1Q\r\u0001C\u0001\t\u0019\fa\u0001^8J]\u001a|GcA4naB\u0011\u0001n[\u0007\u0002S*\u0011!\u000eB\u0001\ng\u000eDW\rZ;mKJL!\u0001\\5\u0003\u001f\u0005\u001b7-^7vY\u0006\u0014G.Z%oM>DQA\u001c3A\u0002=\fa!\u001e9eCR,\u0007c\u0001\bKG!)\u0011\u000f\u001aa\u0001_\u0006)a/\u00197vK\"11\u000f\u0001C\u0003\t]\u000ba\"[:Bi\u0012\u0013\u0018N^3s'&$W\rC\u0003v\u0001\u0019\u0005q+\u0001\u0004jgj+'o\u001c\u0005\u0006o\u0002!\t\u0001G\u0001\rG>\u0004\u00180\u00118e%\u0016\u001cX\r\u001e\u0005\u0006s\u00021\t\u0001G\u0001\u0005G>\u0004\u0018\u0010C\u0003|\u0001\u0019\u0005!,A\u0003sKN,G\u000fC\u0003~\u0001\u0019\u0005a0A\u0002bI\u0012$\"AM@\t\r\u0005\u0005A\u00101\u0001\u001c\u0003\u00051\bbBA\u0003\u0001\u0019\u0005\u0011qA\u0001\u0006[\u0016\u0014x-\u001a\u000b\u0004e\u0005%\u0001bBA\u0006\u0003\u0007\u0001\r!G\u0001\u0006_RDWM\u001d\u0005\u0007c\u00021\t!a\u0004\u0016\u0003\u0019Bq!a\u0005\u0001\t+\t)\"\u0001\u0007xe&$XMU3qY\u0006\u001cW\rF\u0001$\u0011\u001d\tI\u0002\u0001C\u0005\u00037\t!B]3bI>\u0013'.Z2u)\r\u0011\u0014Q\u0004\u0005\t\u0003?\t9\u00021\u0001\u0002\"\u0005\u0011\u0011N\u001c\t\u0005\u0003G\ti#\u0004\u0002\u0002&)!\u0011qEA\u0015\u0003\tIwN\u0003\u0002\u0002,\u0005!!.\u0019<b\u0013\u0011\ty#!\n\u0003#=\u0013'.Z2u\u0013:\u0004X\u000f^*ue\u0016\fW\u000eC\u0004\u00024\u0001!\t%!\u000e\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012\u0001\u0014\u0005\n\u0003s\u0001\u0011\u0013!C\u0001\u0003w\t!C]3hSN$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%eU\u0011\u0011Q\b\u0016\u0004\u0013\u0006}2FAA!!\u0011\t\u0019%!\u0014\u000e\u0005\u0005\u0015#\u0002BA$\u0003\u0013\n\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\u0005-s\"\u0001\u0006b]:|G/\u0019;j_:LA!a\u0014\u0002F\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u0013\u0005M\u0003!%A\u0005\u0002\u0005U\u0013A\u0005:fO&\u001cH/\u001a:%I\u00164\u0017-\u001e7uIM*\"!a\u0016+\u0007m\ny\u0004"
)
public abstract class AccumulatorV2<IN, OUT> implements Serializable {
   private AccumulatorMetadata metadata;
   public boolean org$apache$spark$util$AccumulatorV2$$atDriverSide = true;

   public AccumulatorMetadata metadata() {
      return this.metadata;
   }

   public void metadata_$eq(AccumulatorMetadata x$1) {
      this.metadata = x$1;
   }

   public void register(SparkContext sc, Option<String> name, boolean countFailedValues) {
      if (this.metadata() == null) {
         this.metadata_$eq(new AccumulatorMetadata(AccumulatorContext$.MODULE$.newId(), name, countFailedValues));
         AccumulatorContext$.MODULE$.register(this);
         sc.cleaner().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final AccumulatorV2 $outer;

            public final void apply(ContextCleaner x$1) {
               x$1.registerAccumulatorForCleanup(this.$outer);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
      } else {
         throw new IllegalStateException("Cannot register an Accumulator twice.");
      }
   }

   public Option<String> register$default$2() {
      return None$.MODULE$;
   }

   public boolean register$default$3() {
      return false;
   }

   public final boolean isRegistered() {
      return this.metadata() != null && AccumulatorContext$.MODULE$.get(this.metadata().id()).isDefined();
   }

   private void assertMetadataNotNull() {
      if (this.metadata() == null) {
         throw new IllegalStateException("The metadata of this accumulator has not been assigned yet.");
      }
   }

   public final long id() {
      this.assertMetadataNotNull();
      return this.metadata().id();
   }

   public final Option<String> name() {
      this.assertMetadataNotNull();
      return this.org$apache$spark$util$AccumulatorV2$$atDriverSide ? this.metadata().name().orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AccumulatorV2 $outer;

         public final Option<String> apply() {
            return AccumulatorContext$.MODULE$.get(this.$outer.id()).<String>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<String> apply(AccumulatorV2<?, ?> x$2) {
                  return x$2.metadata().name();
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }) : this.metadata().name();
   }

   public final boolean countFailedValues() {
      this.assertMetadataNotNull();
      return this.metadata().countFailedValues();
   }

   public AccumulableInfo toInfo(Option<Object> update, Option<Object> value) {
      boolean isInternal = this.name().exists(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$3) {
            return x$3.startsWith(InternalAccumulator$.MODULE$.METRICS_PREFIX());
         }
      });
      return new AccumulableInfo(this.id(), this.name(), update, value, isInternal, this.countFailedValues(), AccumulableInfo$.MODULE$.$lessinit$greater$default$7());
   }

   public final boolean isAtDriverSide() {
      return this.org$apache$spark$util$AccumulatorV2$$atDriverSide;
   }

   public abstract boolean isZero();

   public AccumulatorV2<IN, OUT> copyAndReset() {
      AccumulatorV2 copyAcc = this.copy();
      copyAcc.reset();
      return copyAcc;
   }

   public abstract AccumulatorV2<IN, OUT> copy();

   public abstract void reset();

   public abstract void add(IN var1);

   public abstract void merge(AccumulatorV2<IN, OUT> var1);

   public abstract OUT value();

   public final Object writeReplace() {
      AccumulatorV2 var10000;
      if (this.org$apache$spark$util$AccumulatorV2$$atDriverSide) {
         if (!this.isRegistered()) {
            throw new UnsupportedOperationException("Accumulator must be registered before send to executor");
         }

         AccumulatorV2 copyAcc = this.copyAndReset();
         Predef$.MODULE$.assert(copyAcc.isZero(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "copyAndReset must return a zero value copy";
            }
         });
         boolean isInternalAcc = this.name().isDefined() && ((String)this.name().get()).startsWith(InternalAccumulator$.MODULE$.METRICS_PREFIX());
         if (isInternalAcc) {
            AccumulatorMetadata qual$1 = this.metadata();
            None$ x$4 = None$.MODULE$;
            long x$5 = qual$1.copy$default$1();
            boolean x$6 = qual$1.copy$default$3();
            copyAcc.metadata_$eq(qual$1.copy(x$5, x$4, x$6));
         } else {
            copyAcc.metadata_$eq(this.metadata());
         }

         var10000 = copyAcc;
      } else {
         var10000 = this;
      }

      return var10000;
   }

   private void readObject(ObjectInputStream in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AccumulatorV2 $outer;
         private final ObjectInputStream in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.in$1.defaultReadObject();
            if (this.$outer.org$apache$spark$util$AccumulatorV2$$atDriverSide) {
               this.$outer.org$apache$spark$util$AccumulatorV2$$atDriverSide = false;
               TaskContext taskContext = TaskContext$.MODULE$.get();
               if (taskContext != null) {
                  taskContext.registerAccumulator(this.$outer);
               }
            } else {
               this.$outer.org$apache$spark$util$AccumulatorV2$$atDriverSide = true;
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   public String toString() {
      return this.metadata() == null ? (new StringBuilder()).append((Object)"Un-registered Accumulator: ").append((Object)this.getClass().getSimpleName()).toString() : (new StringBuilder()).append((Object)this.getClass().getSimpleName()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(id: ", ", name: ", ", value: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.id()), this.name(), this.value()}))).toString();
   }
}
