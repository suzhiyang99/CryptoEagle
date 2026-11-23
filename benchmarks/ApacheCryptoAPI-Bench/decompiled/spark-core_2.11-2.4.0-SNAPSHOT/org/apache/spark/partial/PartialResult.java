package org.apache.spark.partial;

import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ub\u0001B\u0001\u0003\u0001-\u0011Q\u0002U1si&\fGNU3tk2$(BA\u0002\u0005\u0003\u001d\u0001\u0018M\u001d;jC2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001+\taqc\u0005\u0002\u0001\u001bA\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001aD\u0001\u0002\u0006\u0001\u0003\u0002\u0003\u0006I!F\u0001\u000bS:LG/[1m-\u0006d\u0007C\u0001\f\u0018\u0019\u0001!Q\u0001\u0007\u0001C\u0002e\u0011\u0011AU\t\u00035u\u0001\"AD\u000e\n\u0005qy!a\u0002(pi\"Lgn\u001a\t\u0003\u001dyI!aH\b\u0003\u0007\u0005s\u0017\u0010\u0003\u0005\"\u0001\t\u0005\t\u0015!\u0003#\u0003\u001dI7OR5oC2\u0004\"AD\u0012\n\u0005\u0011z!a\u0002\"p_2,\u0017M\u001c\u0005\u0006M\u0001!\taJ\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007!R3\u0006E\u0002*\u0001Ui\u0011A\u0001\u0005\u0006)\u0015\u0002\r!\u0006\u0005\u0006C\u0015\u0002\rA\t\u0005\b[\u0001\u0001\r\u0011\"\u0003/\u0003)1\u0017N\\1m-\u0006dW/Z\u000b\u0002_A\u0019a\u0002M\u000b\n\u0005Ez!AB(qi&|g\u000eC\u00044\u0001\u0001\u0007I\u0011\u0002\u001b\u0002\u001d\u0019Lg.\u00197WC2,Xm\u0018\u0013fcR\u0011Q\u0007\u000f\t\u0003\u001dYJ!aN\b\u0003\tUs\u0017\u000e\u001e\u0005\bsI\n\t\u00111\u00010\u0003\rAH%\r\u0005\u0007w\u0001\u0001\u000b\u0015B\u0018\u0002\u0017\u0019Lg.\u00197WC2,X\r\t\u0005\b{\u0001\u0001\r\u0011\"\u0003?\u0003\u001d1\u0017-\u001b7ve\u0016,\u0012a\u0010\t\u0004\u001dA\u0002\u0005CA!J\u001d\t\u0011uI\u0004\u0002D\r6\tAI\u0003\u0002F\u0015\u00051AH]8pizJ\u0011\u0001E\u0005\u0003\u0011>\tq\u0001]1dW\u0006<W-\u0003\u0002K\u0017\nIQ\t_2faRLwN\u001c\u0006\u0003\u0011>Aq!\u0014\u0001A\u0002\u0013%a*A\u0006gC&dWO]3`I\u0015\fHCA\u001bP\u0011\u001dID*!AA\u0002}Ba!\u0015\u0001!B\u0013y\u0014\u0001\u00034bS2,(/\u001a\u0011\t\u000fM\u0003\u0001\u0019!C\u0005)\u0006\t2m\\7qY\u0016$\u0018n\u001c8IC:$G.\u001a:\u0016\u0003U\u00032A\u0004\u0019W!\u0011qq+F\u001b\n\u0005a{!!\u0003$v]\u000e$\u0018n\u001c82\u0011\u001dQ\u0006\u00011A\u0005\nm\u000bQcY8na2,G/[8o\u0011\u0006tG\r\\3s?\u0012*\u0017\u000f\u0006\u000269\"9\u0011(WA\u0001\u0002\u0004)\u0006B\u00020\u0001A\u0003&Q+\u0001\nd_6\u0004H.\u001a;j_:D\u0015M\u001c3mKJ\u0004\u0003b\u00021\u0001\u0001\u0004%I!Y\u0001\u000fM\u0006LG.\u001e:f\u0011\u0006tG\r\\3s+\u0005\u0011\u0007c\u0001\b1GB!ab\u0016!6\u0011\u001d)\u0007\u00011A\u0005\n\u0019\f!CZ1jYV\u0014X\rS1oI2,'o\u0018\u0013fcR\u0011Qg\u001a\u0005\bs\u0011\f\t\u00111\u0001c\u0011\u0019I\u0007\u0001)Q\u0005E\u0006ya-Y5mkJ,\u0007*\u00198eY\u0016\u0014\b\u0005C\u0003l\u0001\u0011\u0005A.\u0001\u0007j]&$\u0018.\u00197WC2,X-F\u0001\u0016\u0011\u0015q\u0007\u0001\"\u0001p\u0003MI7/\u00138ji&\fGNV1mk\u00164\u0015N\\1m+\u0005\u0011\u0003\"B9\u0001\t\u0003\u0011\u0018!D4fi\u001aKg.\u00197WC2,X\rF\u0001\u0016\u0011\u0015!\b\u0001\"\u0001v\u0003)ygnQ8na2,G/\u001a\u000b\u0003QYDQa^:A\u0002Y\u000bq\u0001[1oI2,'\u000fC\u0003z\u0001\u0011\u0005!0\u0001\u0004p]\u001a\u000b\u0017\u000e\u001c\u000b\u0003kmDQa\u001e=A\u0002\rDQ! \u0001\u0005\u0002y\f1!\\1q+\ry\u0018Q\u0001\u000b\u0005\u0003\u0003\tI\u0001\u0005\u0003*\u0001\u0005\r\u0001c\u0001\f\u0002\u0006\u00111\u0011q\u0001?C\u0002e\u0011\u0011\u0001\u0016\u0005\b\u0003\u0017a\b\u0019AA\u0007\u0003\u00051\u0007#\u0002\bX+\u0005\r\u0001\u0002CA\t\u0001\u0011\u0005A!a\u0005\u0002\u001bM,GOR5oC24\u0016\r\\;f)\r)\u0014Q\u0003\u0005\b\u0003/\ty\u00011\u0001\u0016\u0003\u00151\u0018\r\\;f\u0011\u001d\tY\u0002\u0001C\u0005\u0003;\tQcZ3u\r&t\u0017\r\u001c,bYV,\u0017J\u001c;fe:\fG\u000eF\u00010\u0011!\t\t\u0003\u0001C\u0001\t\u0005\r\u0012AC:fi\u001a\u000b\u0017\u000e\\;sKR\u0019Q'!\n\t\u000f\u0005\u001d\u0012q\u0004a\u0001\u0001\u0006IQ\r_2faRLwN\u001c\u0005\b\u0003W\u0001A\u0011IA\u0017\u0003!!xn\u0015;sS:<GCAA\u0018!\u0011\t\t$a\u000e\u000f\u00079\t\u0019$C\u0002\u00026=\ta\u0001\u0015:fI\u00164\u0017\u0002BA\u001d\u0003w\u0011aa\u0015;sS:<'bAA\u001b\u001f\u0001"
)
public class PartialResult<R> {
   public final R org$apache$spark$partial$PartialResult$$initialVal;
   public final boolean org$apache$spark$partial$PartialResult$$isFinal;
   private Option<R> finalValue;
   private Option<Exception> failure;
   private Option<Function1<R, BoxedUnit>> completionHandler;
   private Option<Function1<Exception, BoxedUnit>> failureHandler;

   private Option<R> finalValue() {
      return this.finalValue;
   }

   private void finalValue_$eq(Option<R> x$1) {
      this.finalValue = x$1;
   }

   private Option<Exception> failure() {
      return this.failure;
   }

   private void failure_$eq(Option<Exception> x$1) {
      this.failure = x$1;
   }

   private Option<Function1<R, BoxedUnit>> completionHandler() {
      return this.completionHandler;
   }

   private void completionHandler_$eq(Option<Function1<R, BoxedUnit>> x$1) {
      this.completionHandler = x$1;
   }

   private Option<Function1<Exception, BoxedUnit>> failureHandler() {
      return this.failureHandler;
   }

   private void failureHandler_$eq(Option<Function1<Exception, BoxedUnit>> x$1) {
      this.failureHandler = x$1;
   }

   public R initialValue() {
      return this.org$apache$spark$partial$PartialResult$$initialVal;
   }

   public boolean isInitialValueFinal() {
      return this.org$apache$spark$partial$PartialResult$$isFinal;
   }

   public synchronized R getFinalValue() {
      while(this.finalValue().isEmpty() && this.failure().isEmpty()) {
         this.wait();
      }

      if (this.finalValue().isDefined()) {
         return (R)this.finalValue().get();
      } else {
         throw (Throwable)this.failure().get();
      }
   }

   public synchronized PartialResult<R> onComplete(Function1<R, BoxedUnit> handler) {
      if (this.completionHandler().isDefined()) {
         throw new UnsupportedOperationException("onComplete cannot be called twice");
      } else {
         this.completionHandler_$eq(new Some(handler));
         if (this.finalValue().isDefined()) {
            handler.apply(this.finalValue().get());
         } else {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         return this;
      }
   }

   public synchronized void onFail(Function1<Exception, BoxedUnit> handler) {
      if (this.failureHandler().isDefined()) {
         throw new UnsupportedOperationException("onFail cannot be called twice");
      } else {
         this.failureHandler_$eq(new Some(handler));
         if (this.failure().isDefined()) {
            handler.apply(this.failure().get());
         }

      }
   }

   public <T> PartialResult<T> map(Function1<R, T> f) {
      return new PartialResult<T>(this, f) {
         // $FF: synthetic field
         private final PartialResult $outer;
         private final Function1 f$1;

         public synchronized T getFinalValue() {
            return (T)this.f$1.apply(this.$outer.getFinalValue());
         }

         public synchronized PartialResult<T> onComplete(Function1<T, BoxedUnit> handler) {
            return this.$outer.onComplete(handler.compose(this.f$1)).<T>map(this.f$1);
         }

         public synchronized void onFail(Function1<Exception, BoxedUnit> handler) {
            this.$outer.onFail(handler);
         }

         public synchronized String toString() {
            Option var1 = this.$outer.org$apache$spark$partial$PartialResult$$getFinalValueInternal();
            String var4;
            if (var1 instanceof Some) {
               Some var2 = (Some)var1;
               Object value = var2.x();
               var4 = (new StringBuilder()).append((Object)"(final: ").append(this.f$1.apply(value)).append((Object)")").toString();
            } else {
               if (!None$.MODULE$.equals(var1)) {
                  throw new MatchError(var1);
               }

               var4 = (new StringBuilder()).append((Object)"(partial: ").append(this.initialValue()).append((Object)")").toString();
            }

            return var4;
         }

         private Option<T> getFinalValueInternal() {
            return this.$outer.org$apache$spark$partial$PartialResult$$getFinalValueInternal().<T>map(this.f$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.f$1 = f$1;
            }
         }
      };
   }

   public synchronized void setFinalValue(R value) {
      if (this.finalValue().isDefined()) {
         throw new UnsupportedOperationException("setFinalValue called twice on a PartialResult");
      } else {
         this.finalValue_$eq(new Some(value));
         this.completionHandler().foreach(new Serializable(this, value) {
            public static final long serialVersionUID = 0L;
            private final Object value$1;

            public final void apply(Function1<R, BoxedUnit> h) {
               h.apply(this.value$1);
            }

            public {
               this.value$1 = value$1;
            }
         });
         this.notifyAll();
      }
   }

   public Option<R> org$apache$spark$partial$PartialResult$$getFinalValueInternal() {
      return this.finalValue();
   }

   public synchronized void setFailure(Exception exception) {
      if (this.failure().isDefined()) {
         throw new UnsupportedOperationException("setFailure called twice on a PartialResult");
      } else {
         this.failure_$eq(new Some(exception));
         this.failureHandler().foreach(new Serializable(this, exception) {
            public static final long serialVersionUID = 0L;
            private final Exception exception$1;

            public final void apply(Function1<Exception, BoxedUnit> h) {
               h.apply(this.exception$1);
            }

            public {
               this.exception$1 = exception$1;
            }
         });
         this.notifyAll();
      }
   }

   public synchronized String toString() {
      Option var1 = this.finalValue();
      String var4;
      if (var1 instanceof Some) {
         Some var2 = (Some)var1;
         Object value = var2.x();
         var4 = (new StringBuilder()).append((Object)"(final: ").append(value).append((Object)")").toString();
      } else {
         if (!None$.MODULE$.equals(var1)) {
            throw new MatchError(var1);
         }

         var4 = (new StringBuilder()).append((Object)"(partial: ").append(this.initialValue()).append((Object)")").toString();
      }

      return var4;
   }

   public PartialResult(R initialVal, boolean isFinal) {
      this.org$apache$spark$partial$PartialResult$$initialVal = (R)initialVal;
      this.org$apache$spark$partial$PartialResult$$isFinal = isFinal;
      this.finalValue = (Option<R>)(isFinal ? new Some(initialVal) : None$.MODULE$);
      this.failure = None$.MODULE$;
      this.completionHandler = None$.MODULE$;
      this.failureHandler = None$.MODULE$;
   }
}
