package org.apache.spark.status;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.spark.SparkConf;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.kvstore.KVStore;
import org.apache.spark.util.kvstore.KVStoreView;
import org.spark_project.guava.util.concurrent.MoreExecutors;
import scala.Function0;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.ListBuffer;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;
import scala.util.Try;

@ScalaSignature(
   bytes = "\u0006\u0001\t=h!B\u0001\u0003\u0001\u0011Q!\u0001F#mK6,g\u000e\u001e+sC\u000e\\\u0017N\\4Ti>\u0014XM\u0003\u0002\u0004\t\u000511\u000f^1ukNT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0004\u0001-\u0019\u0002C\u0001\u0007\u0012\u001b\u0005i!B\u0001\b\u0010\u0003\u0011a\u0017M\\4\u000b\u0003A\tAA[1wC&\u0011!#\u0004\u0002\u0007\u001f\nTWm\u0019;\u0011\u0005QIR\"A\u000b\u000b\u0005Y9\u0012aB6wgR|'/\u001a\u0006\u00031\u0011\tA!\u001e;jY&\u0011!$\u0006\u0002\b\u0017Z\u001bFo\u001c:f\u0011!a\u0002A!A!\u0002\u0013\u0019\u0012!B:u_J,7\u0001\u0001\u0005\t?\u0001\u0011\t\u0011)A\u0005A\u0005!1m\u001c8g!\t\t#%D\u0001\u0005\u0013\t\u0019CAA\u0005Ta\u0006\u00148nQ8oM\")Q\u0005\u0001C\u0001M\u00051A(\u001b8jiz\"2aJ\u0015+!\tA\u0003!D\u0001\u0003\u0011\u0015aB\u00051\u0001\u0014\u0011\u0015yB\u00051\u0001!\u0011\u001da\u0003A1A\u0005\n5\n\u0001\u0002\u001e:jO\u001e,'o]\u000b\u0002]A!qF\u000e\u001dK\u001b\u0005\u0001$BA\u00193\u0003\u001diW\u000f^1cY\u0016T!a\r\u001b\u0002\u0015\r|G\u000e\\3di&|gNC\u00016\u0003\u0015\u00198-\u00197b\u0013\t9\u0004GA\u0004ICNDW*\u001991\u0005er\u0004c\u0001\u0007;y%\u00111(\u0004\u0002\u0006\u00072\f7o\u001d\t\u0003{yb\u0001\u0001B\u0005@\u0001\u0006\u0005\t\u0011!B\u0001\u0005\n\u0019q\fJ\u0019\t\r\u0005\u0003\u0001\u0015!\u0003/\u0003%!(/[4hKJ\u001c\b%\u0005\u0002D\u000fB\u0011A)R\u0007\u0002i%\u0011a\t\u000e\u0002\b\u001d>$\b.\u001b8h!\t!\u0005*\u0003\u0002Ji\t\u0019\u0011I\\=\u0011\u0007-ce*D\u00013\u0013\ti%GA\u0002TKF\u00044aTAA!\u0011\u0001\u0016+a \u000e\u0003\u00011AA\u0015\u0001E'\n9AK]5hO\u0016\u0014XC\u0001+v'\u0011\tV\u000bW.\u0011\u0005\u00113\u0016BA,5\u0005\u0019\te.\u001f*fMB\u0011A)W\u0005\u00035R\u0012q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002E9&\u0011Q\f\u000e\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t?F\u0013)\u001a!C\u0001A\u0006IA\u000f\u001b:fg\"|G\u000eZ\u000b\u0002CB\u0011AIY\u0005\u0003GR\u0012A\u0001T8oO\"AQ-\u0015B\tB\u0003%\u0011-\u0001\u0006uQJ,7\u000f[8mI\u0002B\u0001bZ)\u0003\u0016\u0004%\t\u0001[\u0001\u0007C\u000e$\u0018n\u001c8\u0016\u0003%\u0004B\u0001\u00126bY&\u00111\u000e\u000e\u0002\n\rVt7\r^5p]F\u0002\"\u0001R7\n\u00059$$\u0001B+oSRD\u0001\u0002])\u0003\u0012\u0003\u0006I![\u0001\bC\u000e$\u0018n\u001c8!\u0011\u0015)\u0013\u000b\"\u0001s)\r\u0019x\u000f\u001f\t\u0004!F#\bCA\u001fv\t\u00151\u0018K1\u0001C\u0005\u0005!\u0006\"B0r\u0001\u0004\t\u0007\"B4r\u0001\u0004I\u0007b\u0002>R\u0003\u0003%\ta_\u0001\u0005G>\u0004\u00180\u0006\u0002}\u007fR)Q0!\u0001\u0002\u0004A\u0019\u0001+\u0015@\u0011\u0005uzH!\u0002<z\u0005\u0004\u0011\u0005bB0z!\u0003\u0005\r!\u0019\u0005\bOf\u0004\n\u00111\u0001j\u0011%\t9!UI\u0001\n\u0003\tI!\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\t\u0005-\u0011\u0011E\u000b\u0003\u0003\u001bQ3!YA\bW\t\t\t\u0002\u0005\u0003\u0002\u0014\u0005uQBAA\u000b\u0015\u0011\t9\"!\u0007\u0002\u0013Ut7\r[3dW\u0016$'bAA\u000ei\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005}\u0011Q\u0003\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,GA\u0002<\u0002\u0006\t\u0007!\tC\u0005\u0002&E\u000b\n\u0011\"\u0001\u0002(\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012T\u0003BA\u0015\u0003[)\"!a\u000b+\u0007%\fy\u0001\u0002\u0004w\u0003G\u0011\rA\u0011\u0005\n\u0003c\t\u0016\u0011!C!\u0003g\tQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DXCAA\u001b!\ra\u0011qG\u0005\u0004\u0003si!AB*ue&tw\rC\u0005\u0002>E\u000b\t\u0011\"\u0001\u0002@\u0005a\u0001O]8ek\u000e$\u0018I]5usV\u0011\u0011\u0011\t\t\u0004\t\u0006\r\u0013bAA#i\t\u0019\u0011J\u001c;\t\u0013\u0005%\u0013+!A\u0005\u0002\u0005-\u0013A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0004\u000f\u00065\u0003BCA(\u0003\u000f\n\t\u00111\u0001\u0002B\u0005\u0019\u0001\u0010J\u0019\t\u0013\u0005M\u0013+!A\u0005B\u0005U\u0013a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0005\u0005]\u0003\u0003B&\u0002Z\u001dK1!a\u00173\u0005!IE/\u001a:bi>\u0014\b\"CA0#\u0006\u0005I\u0011AA1\u0003!\u0019\u0017M\\#rk\u0006dG\u0003BA2\u0003S\u00022\u0001RA3\u0013\r\t9\u0007\u000e\u0002\b\u0005>|G.Z1o\u0011%\ty%!\u0018\u0002\u0002\u0003\u0007q\tC\u0005\u0002nE\u000b\t\u0011\"\u0011\u0002p\u0005A\u0001.Y:i\u0007>$W\r\u0006\u0002\u0002B!I\u00111O)\u0002\u0002\u0013\u0005\u0013QO\u0001\ti>\u001cFO]5oOR\u0011\u0011Q\u0007\u0005\n\u0003s\n\u0016\u0011!C!\u0003w\na!Z9vC2\u001cH\u0003BA2\u0003{B\u0011\"a\u0014\u0002x\u0005\u0005\t\u0019A$\u0011\u0007u\n\t\t\u0002\u0006\u0002\u0004\u0002\u000b\t\u0011!A\u0003\u0002\t\u00131a\u0018\u00133\u0011%\t9\t\u0001b\u0001\n\u0013\tI)A\u0007gYV\u001c\b\u000e\u0016:jO\u001e,'o]\u000b\u0003\u0003\u0017\u0003RaLAG\u0003#K1!a$1\u0005)a\u0015n\u001d;Ck\u001a4WM\u001d\t\u0005\t\u0006ME.C\u0002\u0002\u0016R\u0012\u0011BR;oGRLwN\u001c\u0019\t\u0011\u0005e\u0005\u0001)A\u0005\u0003\u0017\u000baB\u001a7vg\"$&/[4hKJ\u001c\b\u0005C\u0005\u0002\u001e\u0002\u0011\r\u0011\"\u0003\u0002 \u0006AQ\r_3dkR|'/\u0006\u0002\u0002\"B!\u00111UAV\u001b\t\t)K\u0003\u0003\u0002(\u0006%\u0016AC2p]\u000e,(O]3oi*\u0011\u0001dD\u0005\u0005\u0003[\u000b)KA\bFq\u0016\u001cW\u000f^8s'\u0016\u0014h/[2f\u0011!\t\t\f\u0001Q\u0001\n\u0005\u0005\u0016!C3yK\u000e,Ho\u001c:!\u0011%\t)\f\u0001a\u0001\n\u0013\t9,A\u0004ti>\u0004\b/\u001a3\u0016\u0005\u0005\r\u0004\"CA^\u0001\u0001\u0007I\u0011BA_\u0003-\u0019Ho\u001c9qK\u0012|F%Z9\u0015\u00071\fy\f\u0003\u0006\u0002P\u0005e\u0016\u0011!a\u0001\u0003GB\u0001\"a1\u0001A\u0003&\u00111M\u0001\tgR|\u0007\u000f]3eA!\"\u0011\u0011YAd!\r!\u0015\u0011Z\u0005\u0004\u0003\u0017$$\u0001\u0003<pY\u0006$\u0018\u000e\\3\t\u000f\u0005=\u0007\u0001\"\u0001\u0002R\u0006Q\u0011\r\u001a3Ue&<w-\u001a:\u0015\r\u0005M\u0017q[Ax)\ra\u0017Q\u001b\u0005\u0007O\u00065\u0007\u0019A5\t\u0011\u0005e\u0017Q\u001aa\u0001\u00037\fQa\u001b7bgN\u0004D!!8\u0002lB1\u0011q\\As\u0003St1\u0001RAq\u0013\r\t\u0019\u000fN\u0001\u0007!J,G-\u001a4\n\u0007m\n9OC\u0002\u0002dR\u00022!PAv\t-\ti/a6\u0002\u0002\u0003\u0005)\u0011\u0001\"\u0003\u0007}#3\u0007\u0003\u0004`\u0003\u001b\u0004\r!\u0019\u0005\b\u0003g\u0004A\u0011AA{\u0003\u001dygN\u00127vg\"$2\u0001\\A|\u0011!9\u0017\u0011\u001fCA\u0002\u0005e\b\u0003\u0002#\u0002|2L1!!@5\u0005!a$-\u001f8b[\u0016t\u0004b\u0002B\u0001\u0001\u0011\u0005!1A\u0001\bI>\f5/\u001f8d)\ra'Q\u0001\u0005\n\u0005\u000f\ty\u0010\"a\u0001\u0003s\f!A\u001a8\t\u000f\t-\u0001\u0001\"\u0011\u0003\u000e\u0005!!/Z1e+\u0011\u0011yAa\u0005\u0015\r\tE!Q\u0003B\r!\ri$1\u0003\u0003\u0007m\n%!\u0019\u0001\"\t\u0011\u0005e'\u0011\u0002a\u0001\u0005/\u0001b!a8\u0002f\nE\u0001b\u0002B\u000e\u0005\u0013\u0001\raR\u0001\u000b]\u0006$XO]1m\u0017\u0016L\bb\u0002B\u0010\u0001\u0011\u0005#\u0011E\u0001\u0006oJLG/\u001a\u000b\u0004Y\n\r\u0002b\u0002B\u0013\u0005;\u0001\raR\u0001\u0006m\u0006dW/\u001a\u0005\b\u0005?\u0001A\u0011\u0001B\u0015)\u0015a'1\u0006B\u0017\u0011\u001d\u0011)Ca\nA\u0002\u001dC\u0001Ba\f\u0003(\u0001\u0007\u00111M\u0001\u000eG\",7m\u001b+sS\u001e<WM]:\t\u000f\tM\u0002\u0001\"\u0011\u00036\u00051A-\u001a7fi\u0016$R\u0001\u001cB\u001c\u0005\u0007B\u0001\"!7\u00032\u0001\u0007!\u0011\b\u0019\u0005\u0005w\u0011y\u0004\u0005\u0004\u0002`\u0006\u0015(Q\b\t\u0004{\t}Ba\u0003B!\u0005o\t\t\u0011!A\u0003\u0002\t\u00131a\u0018\u00135\u0011\u001d\u0011YB!\rA\u0002\u001dCqAa\u0012\u0001\t\u0003\u0012I%A\u0006hKRlU\r^1eCR\fW\u0003\u0002B&\u0005\u001f\"BA!\u0014\u0003RA\u0019QHa\u0014\u0005\rY\u0014)E1\u0001C\u0011!\tIN!\u0012A\u0002\tM\u0003CBAp\u0003K\u0014i\u0005C\u0004\u0003X\u0001!\tE!\u0017\u0002\u0017M,G/T3uC\u0012\fG/\u0019\u000b\u0004Y\nm\u0003b\u0002B\u0013\u0005+\u0002\ra\u0012\u0005\b\u0005?\u0002A\u0011\tB1\u0003\u00111\u0018.Z<\u0016\t\t\r$Q\u000e\u000b\u0005\u0005K\u0012y\u0007E\u0003\u0015\u0005O\u0012Y'C\u0002\u0003jU\u00111b\u0013,Ti>\u0014XMV5foB\u0019QH!\u001c\u0005\rY\u0014iF1\u0001C\u0011!\tIN!\u0018A\u0002\tE\u0004CBAp\u0003K\u0014Y\u0007C\u0004\u0003v\u0001!\tEa\u001e\u0002\u000b\r|WO\u001c;\u0015\u0007\u0005\u0014I\b\u0003\u0005\u0002Z\nM\u0004\u0019\u0001B>a\u0011\u0011iH!!\u0011\r\u0005}\u0017Q\u001dB@!\ri$\u0011\u0011\u0003\f\u0005\u0007\u0013I(!A\u0001\u0002\u000b\u0005!IA\u0002`IUBqA!\u001e\u0001\t\u0003\u00129\tF\u0004b\u0005\u0013\u0013)J!(\t\u0011\u0005e'Q\u0011a\u0001\u0005\u0017\u0003DA!$\u0003\u0012B1\u0011q\\As\u0005\u001f\u00032!\u0010BI\t-\u0011\u0019J!#\u0002\u0002\u0003\u0005)\u0011\u0001\"\u0003\u0007}#c\u0007\u0003\u0005\u0003\u0018\n\u0015\u0005\u0019\u0001BM\u0003\u0015Ig\u000eZ3y!\u0011\tyNa'\n\t\u0005e\u0012q\u001d\u0005\b\u0005?\u0013)\t1\u0001H\u00031Ig\u000eZ3yK\u00124\u0016\r\\;f\u0011\u001d\u0011\u0019\u000b\u0001C!\u0005K\u000bQa\u00197pg\u0016$\u0012\u0001\u001c\u0005\b\u0005G\u0003A\u0011\u0001BU)\ra'1\u0016\u0005\t\u0005[\u00139\u000b1\u0001\u0002d\u0005Y1\r\\8tKB\u000b'/\u001a8u\u000f%\u0011\t\fAA\u0001\u0012\u0013\u0011\u0019,A\u0004Ue&<w-\u001a:\u0011\u0007A\u0013)L\u0002\u0005S\u0001\u0005\u0005\t\u0012\u0002B\\'\u0011\u0011),V.\t\u000f\u0015\u0012)\f\"\u0001\u0003<R\u0011!1\u0017\u0005\u000b\u0003g\u0012),!A\u0005F\u0005U\u0004B\u0003Ba\u0005k\u000b\t\u0011\"!\u0003D\u0006)\u0011\r\u001d9msV!!Q\u0019Bf)\u0019\u00119M!4\u0003PB!\u0001+\u0015Be!\ri$1\u001a\u0003\u0007m\n}&\u0019\u0001\"\t\r}\u0013y\f1\u0001b\u0011\u00199'q\u0018a\u0001S\"Q!1\u001bB[\u0003\u0003%\tI!6\u0002\u000fUt\u0017\r\u001d9msV!!q\u001bBw)\u0011\u0011IN!:\u0011\u000b\u0011\u0013YNa8\n\u0007\tuGG\u0001\u0004PaRLwN\u001c\t\u0006\t\n\u0005\u0018-[\u0005\u0004\u0005G$$A\u0002+va2,'\u0007\u0003\u0006\u0003h\nE\u0017\u0011!a\u0001\u0005S\f1\u0001\u001f\u00131!\u0011\u0001\u0016Ka;\u0011\u0007u\u0012i\u000f\u0002\u0004w\u0005#\u0014\rA\u0011"
)
public class ElementTrackingStore implements KVStore {
   public final KVStore org$apache$spark$status$ElementTrackingStore$$store;
   private final HashMap<Class<?>, Seq<Trigger<?>>> triggers;
   private final ListBuffer<Function0<BoxedUnit>> flushTriggers;
   private final ExecutorService executor;
   private volatile boolean stopped;
   private volatile Trigger$ org$apache$spark$status$ElementTrackingStore$$Trigger$module;

   private Trigger$ org$apache$spark$status$ElementTrackingStore$$Trigger$lzycompute() {
      synchronized(this) {
         if (this.org$apache$spark$status$ElementTrackingStore$$Trigger$module == null) {
            this.org$apache$spark$status$ElementTrackingStore$$Trigger$module = new Trigger$();
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      Object var3 = null;
      return this.org$apache$spark$status$ElementTrackingStore$$Trigger$module;
   }

   private HashMap<Class<?>, Seq<Trigger<?>>> triggers() {
      return this.triggers;
   }

   private ListBuffer<Function0<BoxedUnit>> flushTriggers() {
      return this.flushTriggers;
   }

   private ExecutorService executor() {
      return this.executor;
   }

   private boolean stopped() {
      return this.stopped;
   }

   private void stopped_$eq(boolean x$1) {
      this.stopped = x$1;
   }

   public void addTrigger(Class<?> klass, long threshold, Function1<Object, BoxedUnit> action) {
      Seq existing = (Seq)this.triggers().getOrElse(klass, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.apply(Nil$.MODULE$);
         }
      });
      this.triggers().update(klass, existing.$colon$plus(new Trigger(threshold, action), Seq$.MODULE$.canBuildFrom()));
   }

   public void onFlush(Function0<BoxedUnit> action) {
      this.flushTriggers().$plus$eq(action);
   }

   public void doAsync(Function0<BoxedUnit> fn) {
      this.executor().submit(new Runnable(this, fn) {
         private final Function0 fn$1;

         public void run() {
            Utils$.MODULE$.tryLog(this.fn$1);
         }

         public {
            this.fn$1 = fn$1;
         }
      });
   }

   public <T> T read(Class<T> klass, Object naturalKey) {
      return (T)this.org$apache$spark$status$ElementTrackingStore$$store.read(klass, naturalKey);
   }

   public void write(Object value) {
      this.org$apache$spark$status$ElementTrackingStore$$store.write(value);
   }

   public void write(Object value, boolean checkTriggers) {
      this.write(value);
      if (checkTriggers && !this.stopped()) {
         this.triggers().get(value.getClass()).foreach(new Serializable(this, value) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ElementTrackingStore $outer;
            public final Object value$1;

            public final void apply(Seq<Trigger<?>> list) {
               this.$outer.doAsync(new Serializable(this, list) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final Seq list$1;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     long count = this.$outer.org$apache$spark$status$ElementTrackingStore$$anonfun$$$outer().org$apache$spark$status$ElementTrackingStore$$store.count(this.$outer.value$1.getClass());
                     this.list$1.foreach(new Serializable(this, count) {
                        public static final long serialVersionUID = 0L;
                        private final long count$1;

                        public final void apply(Trigger<?> t) {
                           if (this.count$1 > t.threshold()) {
                              t.action().apply$mcVJ$sp(this.count$1);
                           }

                        }

                        public {
                           this.count$1 = count$1;
                        }
                     });
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.list$1 = list$1;
                     }
                  }
               });
            }

            // $FF: synthetic method
            public ElementTrackingStore org$apache$spark$status$ElementTrackingStore$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.value$1 = value$1;
               }
            }
         });
      }

   }

   public void delete(Class<?> klass, Object naturalKey) {
      this.org$apache$spark$status$ElementTrackingStore$$store.delete(klass, naturalKey);
   }

   public <T> T getMetadata(Class<T> klass) {
      return (T)this.org$apache$spark$status$ElementTrackingStore$$store.getMetadata(klass);
   }

   public void setMetadata(Object value) {
      this.org$apache$spark$status$ElementTrackingStore$$store.setMetadata(value);
   }

   public <T> KVStoreView<T> view(Class<T> klass) {
      return this.org$apache$spark$status$ElementTrackingStore$$store.view(klass);
   }

   public long count(Class<?> klass) {
      return this.org$apache$spark$status$ElementTrackingStore$$store.count(klass);
   }

   public long count(Class<?> klass, String index, Object indexedValue) {
      return this.org$apache$spark$status$ElementTrackingStore$$store.count(klass, index, indexedValue);
   }

   public void close() {
      this.close(true);
   }

   public synchronized void close(boolean closeParent) {
      if (!this.stopped()) {
         this.stopped_$eq(true);
         this.executor().shutdown();
         if (this.executor().awaitTermination(5L, TimeUnit.SECONDS)) {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            this.executor().shutdownNow();
         }

         this.flushTriggers().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Try<BoxedUnit> apply(Function0<BoxedUnit> trigger) {
               return Utils$.MODULE$.<BoxedUnit>tryLog(trigger);
            }
         });
         if (closeParent) {
            this.org$apache$spark$status$ElementTrackingStore$$store.close();
         }

      }
   }

   public Trigger$ org$apache$spark$status$ElementTrackingStore$$Trigger() {
      return this.org$apache$spark$status$ElementTrackingStore$$Trigger$module == null ? this.org$apache$spark$status$ElementTrackingStore$$Trigger$lzycompute() : this.org$apache$spark$status$ElementTrackingStore$$Trigger$module;
   }

   public ElementTrackingStore(KVStore store, SparkConf conf) {
      this.org$apache$spark$status$ElementTrackingStore$$store = store;
      this.triggers = new HashMap<Class<?>, Seq<Trigger<?>>>();
      this.flushTriggers = new ListBuffer<Function0<BoxedUnit>>();
      this.executor = (ExecutorService)(BoxesRunTime.unboxToBoolean(conf.get(config$.MODULE$.ASYNC_TRACKING_ENABLED())) ? ThreadUtils$.MODULE$.newDaemonSingleThreadExecutor("element-tracking-store-worker") : MoreExecutors.sameThreadExecutor());
      this.stopped = false;
   }

   public class Trigger$ implements Serializable {
      // $FF: synthetic field
      private final ElementTrackingStore $outer;

      public final String toString() {
         return "Trigger";
      }

      public <T> Trigger<T> apply(long threshold, Function1<Object, BoxedUnit> action) {
         return this.$outer.new Trigger<T>(threshold, action);
      }

      public <T> Option<Tuple2<Object, Function1<Object, BoxedUnit>>> unapply(Trigger<T> x$0) {
         return (Option<Tuple2<Object, Function1<Object, BoxedUnit>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(BoxesRunTime.boxToLong(x$0.threshold()), x$0.action())));
      }

      public Trigger$() {
         if (ElementTrackingStore.this == null) {
            throw null;
         } else {
            this.$outer = ElementTrackingStore.this;
            super();
         }
      }
   }

   public class Trigger<T> implements Product, Serializable {
      private final long threshold;
      private final Function1<Object, BoxedUnit> action;
      // $FF: synthetic field
      public final ElementTrackingStore $outer;

      public long threshold() {
         return this.threshold;
      }

      public Function1<Object, BoxedUnit> action() {
         return this.action;
      }

      public <T> Trigger<T> copy(long threshold, Function1<Object, BoxedUnit> action) {
         return this.org$apache$spark$status$ElementTrackingStore$Trigger$$$outer().new Trigger<T>(threshold, action);
      }

      public <T> long copy$default$1() {
         return this.threshold();
      }

      public <T> Function1<Object, BoxedUnit> copy$default$2() {
         return this.action();
      }

      public String productPrefix() {
         return "Trigger";
      }

      public int productArity() {
         return 2;
      }

      public Object productElement(int x$1) {
         Object var10000;
         switch (x$1) {
            case 0:
               var10000 = BoxesRunTime.boxToLong(this.threshold());
               break;
            case 1:
               var10000 = this.action();
               break;
            default:
               throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
         }

         return var10000;
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof Trigger;
      }

      public int hashCode() {
         int var1 = -889275714;
         var1 = Statics.mix(var1, Statics.longHash(this.threshold()));
         var1 = Statics.mix(var1, Statics.anyHash(this.action()));
         return Statics.finalizeHash(var1, 2);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var7;
         if (this != x$1) {
            label54: {
               boolean var3;
               if (x$1 instanceof Trigger) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label37: {
                     Trigger var4 = (Trigger)x$1;
                     if (this.threshold() == var4.threshold()) {
                        label35: {
                           Function1 var10000 = this.action();
                           Function1 var5 = var4.action();
                           if (var10000 == null) {
                              if (var5 != null) {
                                 break label35;
                              }
                           } else if (!var10000.equals(var5)) {
                              break label35;
                           }

                           if (var4.canEqual(this)) {
                              var7 = true;
                              break label37;
                           }
                        }
                     }

                     var7 = false;
                  }

                  if (var7) {
                     break label54;
                  }
               }

               var7 = false;
               return var7;
            }
         }

         var7 = true;
         return var7;
      }

      // $FF: synthetic method
      public ElementTrackingStore org$apache$spark$status$ElementTrackingStore$Trigger$$$outer() {
         return this.$outer;
      }

      public Trigger(ElementTrackingStore threshold, long action) {
         this.threshold = threshold;
         this.action = action;
         if (ElementTrackingStore.this == null) {
            throw null;
         } else {
            this.$outer = ElementTrackingStore.this;
            super();
            Product$class.$init$(this);
         }
      }
   }
}
