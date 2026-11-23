package org.apache.spark.scheduler;

import java.util.Properties;
import org.apache.spark.TaskContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.util.CallSite;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple7;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\tee!B\u0001\u0003\u0001\nQ!\u0001\u0004&pEN+(-\\5ui\u0016$'BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\u0015\u00011\"E\u000b\u0019!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!cE\u0007\u0002\u0005%\u0011AC\u0001\u0002\u0012\t\u0006;5k\u00195fIVdWM]#wK:$\bC\u0001\u0007\u0017\u0013\t9RBA\u0004Qe>$Wo\u0019;\u0011\u00051I\u0012B\u0001\u000e\u000e\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!a\u0002A!f\u0001\n\u0003q\u0012!\u00026pE&#7\u0001A\u000b\u0002?A\u0011A\u0002I\u0005\u0003C5\u00111!\u00138u\u0011!\u0019\u0003A!E!\u0002\u0013y\u0012A\u00026pE&#\u0007\u0005\u0003\u0005&\u0001\tU\r\u0011\"\u0001'\u0003!1\u0017N\\1m%\u0012#U#A\u00141\u0005!\u0002\u0004cA\u0015-]5\t!F\u0003\u0002,\t\u0005\u0019!\u000f\u001a3\n\u00055R#a\u0001*E\tB\u0011q\u0006\r\u0007\u0001\t%\t$'!A\u0001\u0002\u000b\u0005\u0001HA\u0002`IEB\u0001b\r\u0001\u0003\u0012\u0003\u0006I\u0001N\u0001\nM&t\u0017\r\u001c*E\t\u0002\u0002$!N\u001c\u0011\u0007%bc\u0007\u0005\u00020o\u0011I\u0011GMA\u0001\u0002\u0003\u0015\t\u0001O\t\u0003sq\u0002\"\u0001\u0004\u001e\n\u0005mj!a\u0002(pi\"Lgn\u001a\t\u0003\u0019uJ!AP\u0007\u0003\u0007\u0005s\u0017\u0010\u0003\u0005A\u0001\tU\r\u0011\"\u0001B\u0003\u00111WO\\2\u0016\u0003\t\u0003$a\u00114\u0011\u000b1!eIS3\n\u0005\u0015k!!\u0003$v]\u000e$\u0018n\u001c83!\t9\u0005*D\u0001\u0005\u0013\tIEAA\u0006UCN\\7i\u001c8uKb$\bGA&Y!\raEk\u0016\b\u0003\u001bJs!AT)\u000e\u0003=S!\u0001U\u000f\u0002\rq\u0012xn\u001c;?\u0013\u0005q\u0011BA*\u000e\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u0016,\u0003\u0011%#XM]1u_JT!aU\u0007\u0011\u0005=BF!C-[\u0003\u0003\u0005\tQ!\u00019\u0005\ryFE\r\u0005\t7\u0002\u0011\t\u0012)A\u00059\u0006)a-\u001e8dAA\u0012Ql\u0019\t\u0006\u0019\u00113eL\u0019\u0019\u0003?\u0006\u00042\u0001\u0014+a!\ty\u0013\rB\u0005Z5\u0006\u0005\t\u0011!B\u0001qA\u0011qf\u0019\u0003\nIj\u000b\t\u0011!A\u0003\u0002a\u00121a\u0018\u00134!\tyc\rB\u0005e5\u0006\u0005\t\u0011!B\u0001q!A\u0001\u000e\u0001BK\u0002\u0013\u0005\u0011.\u0001\u0006qCJ$\u0018\u000e^5p]N,\u0012A\u001b\t\u0004\u0019-|\u0012B\u00017\u000e\u0005\u0015\t%O]1z\u0011!q\u0007A!E!\u0002\u0013Q\u0017a\u00039beRLG/[8og\u0002B\u0001\u0002\u001d\u0001\u0003\u0016\u0004%\t!]\u0001\tG\u0006dGnU5uKV\t!\u000f\u0005\u0002tm6\tAO\u0003\u0002v\t\u0005!Q\u000f^5m\u0013\t9HO\u0001\u0005DC2d7+\u001b;f\u0011!I\bA!E!\u0002\u0013\u0011\u0018!C2bY2\u001c\u0016\u000e^3!\u0011!Y\bA!f\u0001\n\u0003a\u0018\u0001\u00037jgR,g.\u001a:\u0016\u0003u\u0004\"A\u0005@\n\u0005}\u0014!a\u0003&pE2K7\u000f^3oKJD\u0011\"a\u0001\u0001\u0005#\u0005\u000b\u0011B?\u0002\u00131L7\u000f^3oKJ\u0004\u0003BCA\u0004\u0001\tU\r\u0011\"\u0001\u0002\n\u0005Q\u0001O]8qKJ$\u0018.Z:\u0016\u0005\u0005-\u0001\u0003BA\u0007\u0003+i!!a\u0004\u000b\u0007U\f\tB\u0003\u0002\u0002\u0014\u0005!!.\u0019<b\u0013\u0011\t9\"a\u0004\u0003\u0015A\u0013x\u000e]3si&,7\u000f\u0003\u0006\u0002\u001c\u0001\u0011\t\u0012)A\u0005\u0003\u0017\t1\u0002\u001d:pa\u0016\u0014H/[3tA!9\u0011q\u0004\u0001\u0005\u0002\u0005\u0005\u0012A\u0002\u001fj]&$h\b\u0006\t\u0002$\u0005\u0015\u0012qEA\u0019\u0003\u0007\n)%a\u0012\u0002JA\u0011!\u0003\u0001\u0005\u00079\u0005u\u0001\u0019A\u0010\t\u000f\u0015\ni\u00021\u0001\u0002*A\"\u00111FA\u0018!\u0011IC&!\f\u0011\u0007=\ny\u0003\u0002\u00062\u0003O\t\t\u0011!A\u0003\u0002aBq\u0001QA\u000f\u0001\u0004\t\u0019\u0004\r\u0003\u00026\u0005\u0005\u0003c\u0002\u0007E\r\u0006]\u0012q\b\u0019\u0005\u0003s\ti\u0004\u0005\u0003M)\u0006m\u0002cA\u0018\u0002>\u0011Q\u0011,!\r\u0002\u0002\u0003\u0005)\u0011\u0001\u001d\u0011\u0007=\n\t\u0005\u0002\u0006e\u0003c\t\t\u0011!A\u0003\u0002aBa\u0001[A\u000f\u0001\u0004Q\u0007B\u00029\u0002\u001e\u0001\u0007!\u000f\u0003\u0004|\u0003;\u0001\r! \u0005\u000b\u0003\u000f\ti\u0002%AA\u0002\u0005-\u0001\"CA'\u0001\u0005\u0005I\u0011AA(\u0003\u0011\u0019w\u000e]=\u0015!\u0005\r\u0012\u0011KA*\u0003+\n9&!\u0017\u0002\\\u0005u\u0003\u0002\u0003\u000f\u0002LA\u0005\t\u0019A\u0010\t\u0013\u0015\nY\u0005%AA\u0002\u0005%\u0002\"\u0003!\u0002LA\u0005\t\u0019AA\u001a\u0011!A\u00171\nI\u0001\u0002\u0004Q\u0007\u0002\u00039\u0002LA\u0005\t\u0019\u0001:\t\u0011m\fY\u0005%AA\u0002uD!\"a\u0002\u0002LA\u0005\t\u0019AA\u0006\u0011%\t\t\u0007AI\u0001\n\u0003\t\u0019'\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0005\u0005\u0015$fA\u0010\u0002h-\u0012\u0011\u0011\u000e\t\u0005\u0003W\n)(\u0004\u0002\u0002n)!\u0011qNA9\u0003%)hn\u00195fG.,GMC\u0002\u0002t5\t!\"\u00198o_R\fG/[8o\u0013\u0011\t9(!\u001c\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0005\u0002|\u0001\t\n\u0011\"\u0001\u0002~\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012TCAA@a\u0011\t\t)!\"\u0011\t%b\u00131\u0011\t\u0004_\u0005\u0015EAC\u0019\u0002z\u0005\u0005\t\u0011!B\u0001q!I\u0011\u0011\u0012\u0001\u0012\u0002\u0013\u0005\u00111R\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00134+\t\tiI\u000b\u0003\u0002\u0010\u0006\u001d\u0004C\u0002\u0007E\r\u0006EE\bM\u0002\u0002\u0014b\u0003R!!&\u0002\u001c^k!!a&\u000b\u0007\u0005eU\"\u0001\u0006d_2dWm\u0019;j_:L1!VAL\u0011%\ty\nAI\u0001\n\u0003\t\t+\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001b\u0016\u0005\u0005\r&f\u00016\u0002h!I\u0011q\u0015\u0001\u0012\u0002\u0013\u0005\u0011\u0011V\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00136+\t\tYKK\u0002s\u0003OB\u0011\"a,\u0001#\u0003%\t!!-\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%mU\u0011\u00111\u0017\u0016\u0004{\u0006\u001d\u0004\"CA\\\u0001E\u0005I\u0011AA]\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uI]*\"!a/+\t\u0005-\u0011q\r\u0005\n\u0003\u007f\u0003\u0011\u0011!C!\u0003\u0003\fQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DXCAAb!\u0011\t)-a3\u000e\u0005\u0005\u001d'\u0002BAe\u0003#\tA\u0001\\1oO&!\u0011QZAd\u0005\u0019\u0019FO]5oO\"A\u0011\u0011\u001b\u0001\u0002\u0002\u0013\u0005a$\u0001\u0007qe>$Wo\u0019;Be&$\u0018\u0010C\u0005\u0002V\u0002\t\t\u0011\"\u0001\u0002X\u0006q\u0001O]8ek\u000e$X\t\\3nK:$Hc\u0001\u001f\u0002Z\"I\u00111\\Aj\u0003\u0003\u0005\raH\u0001\u0004q\u0012\n\u0004\"CAp\u0001\u0005\u0005I\u0011IAq\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014XCAAr!\u0015\t)*a'=\u0011%\t9\u000fAA\u0001\n\u0003\tI/\u0001\u0005dC:,\u0015/^1m)\u0011\tY/!=\u0011\u00071\ti/C\u0002\u0002p6\u0011qAQ8pY\u0016\fg\u000eC\u0005\u0002\\\u0006\u0015\u0018\u0011!a\u0001y!I\u0011Q\u001f\u0001\u0002\u0002\u0013\u0005\u0013q_\u0001\tQ\u0006\u001c\bnQ8eKR\tq\u0004C\u0005\u0002|\u0002\t\t\u0011\"\u0011\u0002~\u0006AAo\\*ue&tw\r\u0006\u0002\u0002D\"I!\u0011\u0001\u0001\u0002\u0002\u0013\u0005#1A\u0001\u0007KF,\u0018\r\\:\u0015\t\u0005-(Q\u0001\u0005\n\u00037\fy0!AA\u0002q:!B!\u0003\u0003\u0003\u0003E\tA\u0001B\u0006\u00031QuNY*vE6LG\u000f^3e!\r\u0011\"Q\u0002\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0005\u001f\u0019RA!\u0004\u0003\u0012a\u0001\u0002Ca\u0005\u0003\u001a}\u0011iB!\nkev\fY!a\t\u000e\u0005\tU!b\u0001B\f\u001b\u00059!/\u001e8uS6,\u0017\u0002\u0002B\u000e\u0005+\u0011\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c88a\u0011\u0011yBa\t\u0011\t%b#\u0011\u0005\t\u0004_\t\rBAC\u0019\u0003\u000e\u0005\u0005\t\u0011!B\u0001qA\"!q\u0005B\u001a!\u001daAI\u0012B\u0015\u0005c\u0001DAa\u000b\u00030A!A\n\u0016B\u0017!\ry#q\u0006\u0003\u000b3\n5\u0011\u0011!A\u0001\u0006\u0003A\u0004cA\u0018\u00034\u0011QAM!\u0004\u0002\u0002\u0003\u0005)\u0011\u0001\u001d\t\u0011\u0005}!Q\u0002C\u0001\u0005o!\"Aa\u0003\t\u0015\u0005m(QBA\u0001\n\u000b\ni\u0010\u0003\u0006\u0003>\t5\u0011\u0011!CA\u0005\u007f\tQ!\u00199qYf$\u0002#a\t\u0003B\t\r#Q\nB0\u0005C\u0012\u0019G!\u001a\t\rq\u0011Y\u00041\u0001 \u0011\u001d)#1\ba\u0001\u0005\u000b\u0002DAa\u0012\u0003LA!\u0011\u0006\fB%!\ry#1\n\u0003\u000bc\t\r\u0013\u0011!A\u0001\u0006\u0003A\u0004b\u0002!\u0003<\u0001\u0007!q\n\u0019\u0005\u0005#\u0012i\u0006E\u0004\r\t\u001a\u0013\u0019Fa\u00171\t\tU#\u0011\f\t\u0005\u0019R\u00139\u0006E\u00020\u00053\"!\"\u0017B'\u0003\u0003\u0005\tQ!\u00019!\ry#Q\f\u0003\u000bI\n5\u0013\u0011!A\u0001\u0006\u0003A\u0004B\u00025\u0003<\u0001\u0007!\u000e\u0003\u0004q\u0005w\u0001\rA\u001d\u0005\u0007w\nm\u0002\u0019A?\t\u0015\u0005\u001d!1\bI\u0001\u0002\u0004\tY\u0001\u0003\u0006\u0003j\t5\u0011\u0011!CA\u0005W\nq!\u001e8baBd\u0017\u0010\u0006\u0003\u0003n\t\u0005\u0005\u0007\u0002B8\u0005\u007f\u0002R\u0001\u0004B9\u0005kJ1Aa\u001d\u000e\u0005\u0019y\u0005\u000f^5p]BiABa\u001e \u0005w\nyI\u001b:~\u0003\u0017I1A!\u001f\u000e\u0005\u0019!V\u000f\u001d7foA!\u0011\u0006\fB?!\ry#q\u0010\u0003\u000bc\t\u001d\u0014\u0011!A\u0001\u0006\u0003A\u0004B\u0003BB\u0005O\n\t\u00111\u0001\u0002$\u0005\u0019\u0001\u0010\n\u0019\t\u0015\t\u001d%QBI\u0001\n\u0003\tI,A\bbaBd\u0017\u0010\n3fM\u0006,H\u000e\u001e\u00138\u0011)\u0011YI!\u0004\u0012\u0002\u0013\u0005\u0011\u0011X\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001c\t\u0015\t=%QBA\u0001\n\u0013\u0011\t*A\u0006sK\u0006$'+Z:pYZ,GC\u0001BJ!\u0011\t)M!&\n\t\t]\u0015q\u0019\u0002\u0007\u001f\nTWm\u0019;"
)
public class JobSubmitted implements DAGSchedulerEvent, Product, Serializable {
   private final int jobId;
   private final RDD<?> finalRDD;
   private final Function2<TaskContext, Iterator<?>, ?> func;
   private final int[] partitions;
   private final CallSite callSite;
   private final JobListener listener;
   private final Properties properties;

   public static Properties $lessinit$greater$default$7() {
      return JobSubmitted$.MODULE$.$lessinit$greater$default$7();
   }

   public static Properties apply$default$7() {
      return JobSubmitted$.MODULE$.apply$default$7();
   }

   public static Option<Tuple7<Object, RDD<Object>, Function2<TaskContext, Iterator<?>, Object>, int[], CallSite, JobListener, Properties>> unapply(JobSubmitted var0) {
      return JobSubmitted$.MODULE$.unapply(var0);
   }

   public static JobSubmitted apply(int var0, RDD<?> var1, Function2<TaskContext, Iterator<?>, ?> var2, int[] var3, CallSite var4, JobListener var5, Properties var6) {
      return JobSubmitted$.MODULE$.apply(var0, var1, var2, var3, var4, var5, var6);
   }

   public static Function1<Tuple7<Object, RDD<?>, Function2<TaskContext, Iterator<?>, ?>, int[], CallSite, JobListener, Properties>, JobSubmitted> tupled() {
      return JobSubmitted$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<RDD<?>, Function1<Function2<TaskContext, Iterator<?>, ?>, Function1<int[], Function1<CallSite, Function1<JobListener, Function1<Properties, JobSubmitted>>>>>>> curried() {
      return JobSubmitted$.MODULE$.curried();
   }

   public int jobId() {
      return this.jobId;
   }

   public RDD<?> finalRDD() {
      return this.finalRDD;
   }

   public Function2<TaskContext, Iterator<?>, ?> func() {
      return this.func;
   }

   public int[] partitions() {
      return this.partitions;
   }

   public CallSite callSite() {
      return this.callSite;
   }

   public JobListener listener() {
      return this.listener;
   }

   public Properties properties() {
      return this.properties;
   }

   public JobSubmitted copy(int jobId, RDD<?> finalRDD, Function2<TaskContext, Iterator<?>, ?> func, int[] partitions, CallSite callSite, JobListener listener, Properties properties) {
      return new JobSubmitted(jobId, finalRDD, func, partitions, callSite, listener, properties);
   }

   public int copy$default$1() {
      return this.jobId();
   }

   public RDD<?> copy$default$2() {
      return this.finalRDD();
   }

   public Function2<TaskContext, Iterator<?>, Object> copy$default$3() {
      return this.func();
   }

   public int[] copy$default$4() {
      return this.partitions();
   }

   public CallSite copy$default$5() {
      return this.callSite();
   }

   public JobListener copy$default$6() {
      return this.listener();
   }

   public Properties copy$default$7() {
      return this.properties();
   }

   public String productPrefix() {
      return "JobSubmitted";
   }

   public int productArity() {
      return 7;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.jobId());
            break;
         case 1:
            var10000 = this.finalRDD();
            break;
         case 2:
            var10000 = this.func();
            break;
         case 3:
            var10000 = this.partitions();
            break;
         case 4:
            var10000 = this.callSite();
            break;
         case 5:
            var10000 = this.listener();
            break;
         case 6:
            var10000 = this.properties();
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
      return x$1 instanceof JobSubmitted;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.jobId());
      var1 = Statics.mix(var1, Statics.anyHash(this.finalRDD()));
      var1 = Statics.mix(var1, Statics.anyHash(this.func()));
      var1 = Statics.mix(var1, Statics.anyHash(this.partitions()));
      var1 = Statics.mix(var1, Statics.anyHash(this.callSite()));
      var1 = Statics.mix(var1, Statics.anyHash(this.listener()));
      var1 = Statics.mix(var1, Statics.anyHash(this.properties()));
      return Statics.finalizeHash(var1, 7);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var15;
      if (this != x$1) {
         label94: {
            boolean var3;
            if (x$1 instanceof JobSubmitted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label75: {
                  JobSubmitted var4 = (JobSubmitted)x$1;
                  if (this.jobId() == var4.jobId()) {
                     label84: {
                        RDD var10000 = this.finalRDD();
                        RDD var5 = var4.finalRDD();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label84;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label84;
                        }

                        Function2 var10 = this.func();
                        Function2 var6 = var4.func();
                        if (var10 == null) {
                           if (var6 != null) {
                              break label84;
                           }
                        } else if (!var10.equals(var6)) {
                           break label84;
                        }

                        if (this.partitions() == var4.partitions()) {
                           label85: {
                              CallSite var11 = this.callSite();
                              CallSite var7 = var4.callSite();
                              if (var11 == null) {
                                 if (var7 != null) {
                                    break label85;
                                 }
                              } else if (!var11.equals(var7)) {
                                 break label85;
                              }

                              JobListener var12 = this.listener();
                              JobListener var8 = var4.listener();
                              if (var12 == null) {
                                 if (var8 != null) {
                                    break label85;
                                 }
                              } else if (!var12.equals(var8)) {
                                 break label85;
                              }

                              Properties var13 = this.properties();
                              Properties var9 = var4.properties();
                              if (var13 == null) {
                                 if (var9 != null) {
                                    break label85;
                                 }
                              } else if (!var13.equals(var9)) {
                                 break label85;
                              }

                              if (var4.canEqual(this)) {
                                 var15 = true;
                                 break label75;
                              }
                           }
                        }
                     }
                  }

                  var15 = false;
               }

               if (var15) {
                  break label94;
               }
            }

            var15 = false;
            return var15;
         }
      }

      var15 = true;
      return var15;
   }

   public JobSubmitted(int jobId, RDD<?> finalRDD, Function2<TaskContext, Iterator<?>, ?> func, int[] partitions, CallSite callSite, JobListener listener, Properties properties) {
      this.jobId = jobId;
      this.finalRDD = finalRDD;
      this.func = func;
      this.partitions = partitions;
      this.callSite = callSite;
      this.listener = listener;
      this.properties = properties;
      Product$class.$init$(this);
   }
}
