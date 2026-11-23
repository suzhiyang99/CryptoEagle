package org.apache.spark.broadcast;

import java.io.Serializable;
import org.apache.spark.SparkException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ua!B\u0001\u0003\u0003\u0003Y!!\u0003\"s_\u0006$7-Y:u\u0015\t\u0019A!A\u0005ce>\fGmY1ti*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0016\u00051\u00194\u0003\u0002\u0001\u000e'm\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\u000b\u001a\u001b\u0005)\"B\u0001\f\u0018\u0003\tIwNC\u0001\u0019\u0003\u0011Q\u0017M^1\n\u0005i)\"\u0001D*fe&\fG.\u001b>bE2,\u0007C\u0001\u000f \u001b\u0005i\"B\u0001\u0010\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u0011\u001e\u0005\u001daunZ4j]\u001eD\u0001B\t\u0001\u0003\u0006\u0004%\taI\u0001\u0003S\u0012,\u0012\u0001\n\t\u0003\u001d\u0015J!AJ\b\u0003\t1{gn\u001a\u0005\tQ\u0001\u0011\t\u0011)A\u0005I\u0005\u0019\u0011\u000e\u001a\u0011\t\u0011)\u0002!1!Q\u0001\f-\n!\"\u001a<jI\u0016t7-\u001a\u00132!\ras&M\u0007\u0002[)\u0011afD\u0001\be\u00164G.Z2u\u0013\t\u0001TF\u0001\u0005DY\u0006\u001c8\u000fV1h!\t\u00114\u0007\u0004\u0001\u0005\u000bQ\u0002!\u0019A\u001b\u0003\u0003Q\u000b\"AN\u001d\u0011\u000599\u0014B\u0001\u001d\u0010\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"A\u0004\u001e\n\u0005mz!aA!os\")Q\b\u0001C\u0001}\u00051A(\u001b8jiz\"\"aP\"\u0015\u0005\u0001\u0013\u0005cA!\u0001c5\t!\u0001C\u0003+y\u0001\u000f1\u0006C\u0003#y\u0001\u0007A\u0005C\u0004F\u0001\u0001\u0007I\u0011\u0002$\u0002\u0011}K7OV1mS\u0012,\u0012a\u0012\t\u0003\u001d!K!!S\b\u0003\u000f\t{w\u000e\\3b]\"91\n\u0001a\u0001\n\u0013a\u0015\u0001D0jgZ\u000bG.\u001b3`I\u0015\fHCA'Q!\tqa*\u0003\u0002P\u001f\t!QK\\5u\u0011\u001d\t&*!AA\u0002\u001d\u000b1\u0001\u001f\u00132\u0011\u0019\u0019\u0006\u0001)Q\u0005\u000f\u0006Iq,[:WC2LG\r\t\u0015\u0003%V\u0003\"A\u0004,\n\u0005]{!\u0001\u0003<pY\u0006$\u0018\u000e\\3\t\u000fe\u0003\u0001\u0019!C\u00055\u0006aq\fZ3tiJ|\u0017pU5uKV\t1\f\u0005\u0002]?6\tQL\u0003\u0002_/\u0005!A.\u00198h\u0013\t\u0001WL\u0001\u0004TiJLgn\u001a\u0005\bE\u0002\u0001\r\u0011\"\u0003d\u0003AyF-Z:ue>L8+\u001b;f?\u0012*\u0017\u000f\u0006\u0002NI\"9\u0011+YA\u0001\u0002\u0004Y\u0006B\u00024\u0001A\u0003&1,A\u0007`I\u0016\u001cHO]8z'&$X\r\t\u0005\u0006Q\u0002!\t![\u0001\u0006m\u0006dW/Z\u000b\u0002c!)1\u000e\u0001C\u0001Y\u0006IQO\u001c9feNL7\u000f\u001e\u000b\u0002\u001b\")1\u000e\u0001C\u0001]R\u0011Qj\u001c\u0005\u0006a6\u0004\raR\u0001\tE2|7m[5oO\")!\u000f\u0001C\u0001Y\u00069A-Z:ue>L\bB\u0002:\u0001\t\u0003!A\u000f\u0006\u0002Nk\")\u0001o\u001da\u0001\u000f\"1q\u000f\u0001C\u0001\t\u0019\u000bq![:WC2LG\rC\u0003z\u0001\u0019E!0\u0001\u0005hKR4\u0016\r\\;f)\u0005\t\u0004\"\u0002?\u0001\r#i\u0018a\u00033p+:\u0004XM]:jgR$\"!\u0014@\t\u000bA\\\b\u0019A$\t\u000f\u0005\u0005\u0001A\"\u0005\u0002\u0004\u0005IAm\u001c#fgR\u0014x.\u001f\u000b\u0004\u001b\u0006\u0015\u0001\"\u00029\u0000\u0001\u00049\u0005BBA\u0005\u0001\u0011EA.A\u0006bgN,'\u000f\u001e,bY&$\u0007bBA\u0007\u0001\u0011\u0005\u0013qB\u0001\ti>\u001cFO]5oOR\u0011\u0011\u0011\u0003\t\u0005\u0003'\tIBD\u0002\u000f\u0003+I1!a\u0006\u0010\u0003\u0019\u0001&/\u001a3fM&\u0019\u0001-a\u0007\u000b\u0007\u0005]q\u0002"
)
public abstract class Broadcast<T> implements Serializable, Logging {
   private final long id;
   private volatile boolean _isValid;
   private String org$apache$spark$broadcast$Broadcast$$_destroySite;
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

   public long id() {
      return this.id;
   }

   private boolean _isValid() {
      return this._isValid;
   }

   private void _isValid_$eq(boolean x$1) {
      this._isValid = x$1;
   }

   public String org$apache$spark$broadcast$Broadcast$$_destroySite() {
      return this.org$apache$spark$broadcast$Broadcast$$_destroySite;
   }

   private void org$apache$spark$broadcast$Broadcast$$_destroySite_$eq(String x$1) {
      this.org$apache$spark$broadcast$Broadcast$$_destroySite = x$1;
   }

   public T value() {
      this.assertValid();
      return (T)this.getValue();
   }

   public void unpersist() {
      this.unpersist(false);
   }

   public void unpersist(boolean blocking) {
      this.assertValid();
      this.doUnpersist(blocking);
   }

   public void destroy() {
      this.destroy(true);
   }

   public void destroy(boolean blocking) {
      this.assertValid();
      this._isValid_$eq(false);
      this.org$apache$spark$broadcast$Broadcast$$_destroySite_$eq(Utils$.MODULE$.getCallSite(Utils$.MODULE$.getCallSite$default$1()).shortForm());
      this.logInfo(new scala.Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Broadcast $outer;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Destroying %s (from %s)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.toString(), this.$outer.org$apache$spark$broadcast$Broadcast$$_destroySite()}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.doDestroy(blocking);
   }

   public boolean isValid() {
      return this._isValid();
   }

   public abstract T getValue();

   public abstract void doUnpersist(boolean var1);

   public abstract void doDestroy(boolean var1);

   public void assertValid() {
      if (!this._isValid()) {
         throw new SparkException((new StringOps(Predef$.MODULE$.augmentString("Attempted to use %s after it was destroyed (%s) "))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.toString(), this.org$apache$spark$broadcast$Broadcast$$_destroySite()})));
      }
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"Broadcast(").append((Object)BoxesRunTime.boxToLong(this.id())).append((Object)")").toString();
   }

   public Broadcast(long id, ClassTag<T> evidence$1) {
      this.id = id;
      Logging$class.$init$(this);
      this._isValid = true;
      this.org$apache$spark$broadcast$Broadcast$$_destroySite = "";
   }
}
