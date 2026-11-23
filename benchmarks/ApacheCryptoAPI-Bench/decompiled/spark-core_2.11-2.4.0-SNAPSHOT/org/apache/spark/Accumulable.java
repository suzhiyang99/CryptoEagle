package org.apache.spark;

import java.io.Serializable;
import org.apache.spark.scheduler.AccumulableInfo;
import org.apache.spark.scheduler.AccumulableInfo$;
import org.apache.spark.util.AccumulatorContext$;
import org.apache.spark.util.AccumulatorMetadata;
import org.apache.spark.util.LegacyAccumulatorWrapper;
import scala.None$;
import scala.Option;
import scala.reflect.ScalaSignature;

/** @deprecated */
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Md\u0001B\u0001\u0003\u0001%\u00111\"Q2dk6,H.\u00192mK*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014xm\u0001\u0001\u0016\u0007)1ChE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0018\u001b\u0005\u0019\"B\u0001\u000b\u0016\u0003\tIwNC\u0001\u0017\u0003\u0011Q\u0017M^1\n\u0005a\u0019\"\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\u000e\u0001\u0005\u000b\u0007I\u0011A\u000e\u0002\u0005%$W#\u0001\u000f\u0011\u00051i\u0012B\u0001\u0010\u000e\u0005\u0011auN\\4\t\u0011\u0001\u0002!\u0011!Q\u0001\nq\t1!\u001b3!\u0011!\u0011\u0003A!b\u0001\n\u0013\u0019\u0013\u0001D5oSRL\u0017\r\u001c,bYV,W#\u0001\u0013\u0011\u0005\u00152C\u0002\u0001\u0003\u0006O\u0001\u0011\r\u0001\u000b\u0002\u0002%F\u0011\u0011\u0006\f\t\u0003\u0019)J!aK\u0007\u0003\u000f9{G\u000f[5oOB\u0011A\"L\u0005\u0003]5\u00111!\u00118z\u0011!\u0001\u0004A!A!\u0002\u0013!\u0013!D5oSRL\u0017\r\u001c,bYV,\u0007\u0005\u000b\u00020eA\u0011AbM\u0005\u0003i5\u0011\u0011\u0002\u001e:b]NLWM\u001c;\t\u0011Y\u0002!\u0011!Q\u0001\n]\nQ\u0001]1sC6\u0004B\u0001O\u001d%w5\t!!\u0003\u0002;\u0005\t\u0001\u0012iY2v[Vd\u0017M\u00197f!\u0006\u0014\u0018-\u001c\t\u0003Kq\"Q!\u0010\u0001C\u0002!\u0012\u0011\u0001\u0016\u0005\t\u007f\u0001\u0011)\u0019!C\u0001\u0001\u0006!a.Y7f+\u0005\t\u0005c\u0001\u0007C\t&\u00111)\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005\u0015CeB\u0001\u0007G\u0013\t9U\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u0013*\u0013aa\u0015;sS:<'BA$\u000e\u0011!a\u0005A!A!\u0002\u0013\t\u0015!\u00028b[\u0016\u0004\u0003\"\u0003(\u0001\u0005\u000b\u0007I\u0011\u0001\u0002P\u0003E\u0019w.\u001e8u\r\u0006LG.\u001a3WC2,Xm]\u000b\u0002!B\u0011A\"U\u0005\u0003%6\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005U\u0001\t\u0005\t\u0015!\u0003Q\u0003I\u0019w.\u001e8u\r\u0006LG.\u001a3WC2,Xm\u001d\u0011\t\u000bY\u0003A\u0011B,\u0002\rqJg.\u001b;?)\u0019A\u0016LW.];B!\u0001\b\u0001\u0013<\u0011\u0015QR\u000b1\u0001\u001d\u0011\u0015\u0011S\u000b1\u0001%\u0011\u00151T\u000b1\u00018\u0011\u0015yT\u000b1\u0001B\u0011\u0015qU\u000b1\u0001Q\u0011\u00191\u0006\u0001\"\u0001\u0003?R)\u0001\fY1cG\")!E\u0018a\u0001I!)aG\u0018a\u0001o!)qH\u0018a\u0001\u0003\")aJ\u0018a\u0001!\"1a\u000b\u0001C\u0001\u0005\u0015$B\u0001\u00174hQ\")!\u0005\u001aa\u0001I!)a\u0007\u001aa\u0001o!)q\b\u001aa\u0001\u0003\")a\u000b\u0001C\u0001UR\u0019\u0001l\u001b7\t\u000b\tJ\u0007\u0019\u0001\u0013\t\u000bYJ\u0007\u0019A\u001c\t\u000f9\u0004!\u0019!C\u0001G\u0005!!0\u001a:p\u0011\u0019\u0001\b\u0001)A\u0005I\u0005)!0\u001a:pA!A!\u000f\u0001b\u0001\n\u0003\u00111/\u0001\u0004oK^\f5mY\u000b\u0002iB!Q\u000f\u001f\u0013<\u001b\u00051(BA<\u0003\u0003\u0011)H/\u001b7\n\u0005e4(\u0001\u0007'fO\u0006\u001c\u00170Q2dk6,H.\u0019;pe^\u0013\u0018\r\u001d9fe\"11\u0010\u0001Q\u0001\nQ\fqA\\3x\u0003\u000e\u001c\u0007\u0005C\u0003~\u0001\u0011\u0005a0\u0001\u0005%a2,8\u000fJ3r)\ry\u0018Q\u0001\t\u0004\u0019\u0005\u0005\u0011bAA\u0002\u001b\t!QK\\5u\u0011\u0019\t9\u0001 a\u0001w\u0005!A/\u001a:n\u0011\u001d\tY\u0001\u0001C\u0001\u0003\u001b\t1!\u00193e)\ry\u0018q\u0002\u0005\b\u0003\u000f\tI\u00011\u0001<\u0011\u001d\t\u0019\u0002\u0001C\u0001\u0003+\tQ\u0002\n9mkN$\u0003\u000f\\;tI\u0015\fHcA@\u0002\u0018!9\u0011qAA\t\u0001\u0004!\u0003bBA\u000e\u0001\u0011\u0005\u0011QD\u0001\u0006[\u0016\u0014x-\u001a\u000b\u0004\u007f\u0006}\u0001bBA\u0004\u00033\u0001\r\u0001\n\u0005\u0007\u0003G\u0001A\u0011A\u0012\u0002\u000bY\fG.^3\t\r\u0005\u001d\u0002\u0001\"\u0001$\u0003)awnY1m-\u0006dW/\u001a\u0005\b\u0003W\u0001A\u0011AA\u0017\u0003%1\u0018\r\\;f?\u0012*\u0017\u000fF\u0002\u0000\u0003_Aq!!\r\u0002*\u0001\u0007A%\u0001\u0005oK^4\u0016\r\\;f\u0011\u001d\t)\u0004\u0001C\u0001\u0003o\t\u0001b]3u-\u0006dW/\u001a\u000b\u0004\u007f\u0006e\u0002bBA\u0019\u0003g\u0001\r\u0001\n\u0005\t\u0003{\u0001A\u0011\u0001\u0002\u0002@\u0005Y1/\u001a;WC2,X-\u00118z)\ry\u0018\u0011\t\u0005\b\u0003c\tY\u00041\u0001-\u0011!\t)\u0005\u0001C\u0001\u0005\u0005\u001d\u0013A\u0002;p\u0013:4w\u000e\u0006\u0004\u0002J\u0005U\u00131\f\t\u0005\u0003\u0017\n\t&\u0004\u0002\u0002N)\u0019\u0011q\n\u0002\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014\u0018\u0002BA*\u0003\u001b\u0012q\"Q2dk6,H.\u00192mK&sgm\u001c\u0005\t\u0003/\n\u0019\u00051\u0001\u0002Z\u00051Q\u000f\u001d3bi\u0016\u00042\u0001\u0004\"-\u0011!\t\u0019#a\u0011A\u0002\u0005e\u0003bBA0\u0001\u0011\u0005\u0013\u0011M\u0001\ti>\u001cFO]5oOR\tA\tK\u0004\u0001\u0003K\nY'a\u001c\u0011\u00071\t9'C\u0002\u0002j5\u0011!\u0002Z3qe\u0016\u001c\u0017\r^3eC\t\ti'A\tvg\u0016\u0004\u0013iY2v[Vd\u0017\r^8s-J\n#!!\u001d\u0002\u000bIr\u0003G\f\u0019"
)
public class Accumulable<R, T> implements Serializable {
   private final long id;
   private final transient R initialValue;
   private final AccumulableParam<R, T> param;
   private final Option<String> name;
   private final boolean countFailedValues;
   private final R zero;
   private final LegacyAccumulatorWrapper<R, T> newAcc;

   public long id() {
      return this.id;
   }

   private R initialValue() {
      return this.initialValue;
   }

   public Option<String> name() {
      return this.name;
   }

   public boolean countFailedValues() {
      return this.countFailedValues;
   }

   public R zero() {
      return this.zero;
   }

   public LegacyAccumulatorWrapper<R, T> newAcc() {
      return this.newAcc;
   }

   public void $plus$eq(T term) {
      this.newAcc().add(term);
   }

   public void add(T term) {
      this.newAcc().add(term);
   }

   public void $plus$plus$eq(R term) {
      this.newAcc()._value_$eq(this.param.addInPlace(this.newAcc()._value(), term));
   }

   public void merge(R term) {
      this.newAcc()._value_$eq(this.param.addInPlace(this.newAcc()._value(), term));
   }

   public R value() {
      if (this.newAcc().isAtDriverSide()) {
         return (R)this.newAcc().value();
      } else {
         throw new UnsupportedOperationException("Can't read accumulator value in task");
      }
   }

   public R localValue() {
      return (R)this.newAcc().value();
   }

   public void value_$eq(R newValue) {
      if (this.newAcc().isAtDriverSide()) {
         this.newAcc()._value_$eq(newValue);
      } else {
         throw new UnsupportedOperationException("Can't assign accumulator value in task");
      }
   }

   public void setValue(R newValue) {
      this.newAcc()._value_$eq(newValue);
   }

   public void setValueAny(Object newValue) {
      this.setValue(newValue);
   }

   public AccumulableInfo toInfo(Option<Object> update, Option<Object> value) {
      boolean isInternal = this.name().exists(new scala.Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$1) {
            return x$1.startsWith(InternalAccumulator$.MODULE$.METRICS_PREFIX());
         }
      });
      return new AccumulableInfo(this.id(), this.name(), update, value, isInternal, this.countFailedValues(), AccumulableInfo$.MODULE$.$lessinit$greater$default$7());
   }

   public String toString() {
      return this.newAcc()._value() == null ? "null" : this.newAcc()._value().toString();
   }

   private Accumulable(long id, R initialValue, AccumulableParam<R, T> param, Option<String> name, boolean countFailedValues) {
      this.id = id;
      this.initialValue = (R)initialValue;
      this.param = param;
      this.name = name;
      this.countFailedValues = countFailedValues;
      this.zero = (R)param.zero(initialValue);
      this.newAcc = new LegacyAccumulatorWrapper<R, T>(initialValue, param);
      this.newAcc().metadata_$eq(new AccumulatorMetadata(id, name, countFailedValues));
      AccumulatorContext$.MODULE$.register(this.newAcc());
   }

   public Accumulable(R initialValue, AccumulableParam<R, T> param, Option<String> name, boolean countFailedValues) {
      this(AccumulatorContext$.MODULE$.newId(), initialValue, param, name, countFailedValues);
   }

   public Accumulable(R initialValue, AccumulableParam<R, T> param, Option<String> name) {
      this(initialValue, param, name, false);
   }

   public Accumulable(R initialValue, AccumulableParam<R, T> param) {
      this(initialValue, param, None$.MODULE$);
   }
}
