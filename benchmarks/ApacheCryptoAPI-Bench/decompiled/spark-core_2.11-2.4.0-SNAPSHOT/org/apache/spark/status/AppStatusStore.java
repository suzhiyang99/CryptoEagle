package org.apache.spark.status;

import java.util.List;
import java.util.NoSuchElementException;
import org.apache.spark.JobExecutionStatus;
import org.apache.spark.SparkConf;
import org.apache.spark.status.api.v1.ApplicationEnvironmentInfo;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.status.api.v1.ExecutorStageSummary;
import org.apache.spark.status.api.v1.ExecutorSummary;
import org.apache.spark.status.api.v1.InputMetricDistributions;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.status.api.v1.OutputMetricDistributions;
import org.apache.spark.status.api.v1.RDDStorageInfo;
import org.apache.spark.status.api.v1.ShuffleReadMetricDistributions;
import org.apache.spark.status.api.v1.ShuffleWriteMetricDistributions;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.StageStatus;
import org.apache.spark.status.api.v1.TaskData;
import org.apache.spark.status.api.v1.TaskMetricDistributions;
import org.apache.spark.status.api.v1.TaskSorting;
import org.apache.spark.ui.scope.RDDOperationGraph;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.kvstore.KVStore;
import org.apache.spark.util.kvstore.KVStoreIterator;
import org.apache.spark.util.kvstore.KVStoreView;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.IndexedSeq;
import scala.collection.IndexedSeq$;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.math.package$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.DoubleRef;
import scala.runtime.LongRef;

@ScalaSignature(
   bytes = "\u0006\u0001\t%h!B\u0001\u0003\u0001\u0011Q!AD!qaN#\u0018\r^;t'R|'/\u001a\u0006\u0003\u0007\u0011\taa\u001d;biV\u001c(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0003\u0005\u0013\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003\u0015\u0019Ho\u001c:f\u0007\u0001)\u0012!\u0006\t\u0003-mi\u0011a\u0006\u0006\u00031e\tqa\u001b<ti>\u0014XM\u0003\u0002\u001b\t\u0005!Q\u000f^5m\u0013\tarCA\u0004L-N#xN]3\t\u0011y\u0001!\u0011!Q\u0001\nU\taa\u001d;pe\u0016\u0004\u0003\u0002\u0003\u0011\u0001\u0005\u000b\u0007I\u0011A\u0011\u0002\u00111L7\u000f^3oKJ,\u0012A\t\t\u0004\u0019\r*\u0013B\u0001\u0013\u000e\u0005\u0019y\u0005\u000f^5p]B\u0011aeJ\u0007\u0002\u0005%\u0011\u0001F\u0001\u0002\u0012\u0003B\u00048\u000b^1ukNd\u0015n\u001d;f]\u0016\u0014\b\u0002\u0003\u0016\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002\u00131L7\u000f^3oKJ\u0004\u0003\"\u0002\u0017\u0001\t\u0003i\u0013A\u0002\u001fj]&$h\bF\u0002/_A\u0002\"A\n\u0001\t\u000bIY\u0003\u0019A\u000b\t\u000f\u0001Z\u0003\u0013!a\u0001E!)!\u0007\u0001C\u0001g\u0005y\u0011\r\u001d9mS\u000e\fG/[8o\u0013:4w\u000eF\u00015!\t)$(D\u00017\u0015\t9\u0004(\u0001\u0002wc)\u0011\u0011HA\u0001\u0004CBL\u0017BA\u001e7\u0005=\t\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8J]\u001a|\u0007\"B\u001f\u0001\t\u0003q\u0014aD3om&\u0014xN\\7f]RLeNZ8\u0015\u0003}\u0002\"!\u000e!\n\u0005\u00053$AG!qa2L7-\u0019;j_:,eN^5s_:lWM\u001c;J]\u001a|\u0007\"B\"\u0001\t\u0003!\u0015\u0001\u00036pENd\u0015n\u001d;\u0015\u0005\u0015#\u0006c\u0001$O#:\u0011q\t\u0014\b\u0003\u0011.k\u0011!\u0013\u0006\u0003\u0015N\ta\u0001\u0010:p_Rt\u0014\"\u0001\b\n\u00055k\u0011a\u00029bG.\fw-Z\u0005\u0003\u001fB\u00131aU3r\u0015\tiU\u0002\u0005\u00026%&\u00111K\u000e\u0002\b\u0015>\u0014G)\u0019;b\u0011\u0015)&\t1\u0001W\u0003!\u0019H/\u0019;vg\u0016\u001c\bcA,\\;6\t\u0001L\u0003\u0002\u001b3*\t!,\u0001\u0003kCZ\f\u0017B\u0001/Y\u0005\u0011a\u0015n\u001d;\u0011\u0005y{V\"\u0001\u0003\n\u0005\u0001$!A\u0005&pE\u0016CXmY;uS>t7\u000b^1ukNDQA\u0019\u0001\u0005\u0002\r\f1A[8c)\t\tF\rC\u0003fC\u0002\u0007a-A\u0003k_\nLE\r\u0005\u0002\rO&\u0011\u0001.\u0004\u0002\u0004\u0013:$\b\"\u00026\u0001\t\u0003Y\u0017\u0001D3yK\u000e,Ho\u001c:MSN$HC\u00017q!\r1e*\u001c\t\u0003k9L!a\u001c\u001c\u0003\u001f\u0015CXmY;u_J\u001cV/\\7befDQ!]5A\u0002I\f!\"Y2uSZ,wJ\u001c7z!\ta1/\u0003\u0002u\u001b\t9!i\\8mK\u0006t\u0007\"\u0002<\u0001\t\u00039\u0018aD3yK\u000e,Ho\u001c:Tk6l\u0017M]=\u0015\u00055D\b\"B=v\u0001\u0004Q\u0018AC3yK\u000e,Ho\u001c:JIB\u00111P \b\u0003\u0019qL!!`\u0007\u0002\rA\u0013X\rZ3g\u0013\ry\u0018\u0011\u0001\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005ul\u0001bBA\u0003\u0001\u0011\u0005\u0011qA\u0001\rC\u000e$\u0018N^3Ti\u0006<Wm\u001d\u000b\u0003\u0003\u0013\u0001BA\u0012(\u0002\fA\u0019Q'!\u0004\n\u0007\u0005=aGA\u0005Ti\u0006<W\rR1uC\"9\u00111\u0003\u0001\u0005\u0002\u0005U\u0011!C:uC\u001e,G*[:u)\u0011\tI!a\u0006\t\u000fU\u000b\t\u00021\u0001\u0002\u001aA!qkWA\u000e!\r)\u0014QD\u0005\u0004\u0003?1$aC*uC\u001e,7\u000b^1ukNDq!a\t\u0001\t\u0003\t)#A\u0005ti\u0006<W\rR1uCR1\u0011\u0011BA\u0014\u0003WAq!!\u000b\u0002\"\u0001\u0007a-A\u0004ti\u0006<W-\u00133\t\u0013\u00055\u0012\u0011\u0005I\u0001\u0002\u0004\u0011\u0018a\u00023fi\u0006LGn\u001d\u0005\b\u0003c\u0001A\u0011AA\u001a\u0003Aa\u0017m\u001d;Ti\u0006<W-\u0011;uK6\u0004H\u000f\u0006\u0003\u0002\f\u0005U\u0002bBA\u0015\u0003_\u0001\rA\u001a\u0005\b\u0003s\u0001A\u0011AA\u001e\u00031\u0019H/Y4f\u0003R$X-\u001c9u)!\tY!!\u0010\u0002@\u0005\r\u0003bBA\u0015\u0003o\u0001\rA\u001a\u0005\b\u0003\u0003\n9\u00041\u0001g\u00039\u0019H/Y4f\u0003R$X-\u001c9u\u0013\u0012D\u0011\"!\f\u00028A\u0005\t\u0019\u0001:\t\u000f\u0005\u001d\u0003\u0001\"\u0001\u0002J\u0005IA/Y:l\u0007>,h\u000e\u001e\u000b\u0007\u0003\u0017\n\t&a\u0015\u0011\u00071\ti%C\u0002\u0002P5\u0011A\u0001T8oO\"9\u0011\u0011FA#\u0001\u00041\u0007bBA!\u0003\u000b\u0002\rA\u001a\u0005\b\u0003/\u0002A\u0011AA-\u0003=awnY1mSRL8+^7nCJLHCBA.\u0003C\n\u0019\u0007\u0005\u0004|\u0003;R\u00181J\u0005\u0005\u0003?\n\tAA\u0002NCBDq!!\u000b\u0002V\u0001\u0007a\rC\u0004\u0002B\u0005U\u0003\u0019\u00014\t\u000f\u0005\u001d\u0004\u0001\"\u0001\u0002j\u0005YA/Y:l'VlW.\u0019:z)!\tY'a\u001d\u0002v\u0005]\u0004\u0003\u0002\u0007$\u0003[\u00022!NA8\u0013\r\t\tH\u000e\u0002\u0018)\u0006\u001c8.T3ue&\u001cG)[:ue&\u0014W\u000f^5p]NDq!!\u000b\u0002f\u0001\u0007a\rC\u0004\u0002B\u0005\u0015\u0004\u0019\u00014\t\u0011\u0005e\u0014Q\ra\u0001\u0003w\n\u0011#\u001e8t_J$X\rZ)vC:$\u0018\u000e\\3t!\u0015a\u0011QPAA\u0013\r\ty(\u0004\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0004\u0019\u0005\r\u0015bAAC\u001b\t1Ai\\;cY\u0016Dq!!#\u0001\t\u0013\tY)A\ntQ>,H\u000eZ\"bG\",\u0017+^1oi&dW\rF\u0002s\u0003\u001bC\u0001\"a$\u0002\b\u0002\u0007\u0011\u0011Q\u0001\u0002c\"9\u00111\u0013\u0001\u0005\n\u0005U\u0015\u0001E9vC:$\u0018\u000e\\3U_N#(/\u001b8h)\rQ\u0018q\u0013\u0005\t\u0003\u001f\u000b\t\n1\u0001\u0002\u0002\"9\u00111\u0014\u0001\u0005\u0002\u0005u\u0015\u0001\u0003;bg.d\u0015n\u001d;\u0015\u0011\u0005}\u0015qUAU\u0003W\u0003BA\u0012(\u0002\"B\u0019Q'a)\n\u0007\u0005\u0015fG\u0001\u0005UCN\\G)\u0019;b\u0011\u001d\tI#!'A\u0002\u0019Dq!!\u0011\u0002\u001a\u0002\u0007a\rC\u0004\u0002.\u0006e\u0005\u0019\u00014\u0002\u00115\f\u0007\u0010V1tWNDq!a'\u0001\t\u0003\t\t\f\u0006\u0007\u0002 \u0006M\u0016QWA\\\u0003w\u000by\fC\u0004\u0002*\u0005=\u0006\u0019\u00014\t\u000f\u0005\u0005\u0013q\u0016a\u0001M\"9\u0011\u0011XAX\u0001\u00041\u0017AB8gMN,G\u000fC\u0004\u0002>\u0006=\u0006\u0019\u00014\u0002\r1,gn\u001a;i\u0011!\t\t-a,A\u0002\u0005\r\u0017AB:peR\u0014\u0015\u0010E\u00026\u0003\u000bL1!a27\u0005-!\u0016m]6T_J$\u0018N\\4\t\u000f\u0005m\u0005\u0001\"\u0001\u0002LRq\u0011qTAg\u0003\u001f\f\t.a5\u0002V\u0006e\u0007bBA\u0015\u0003\u0013\u0004\rA\u001a\u0005\b\u0003\u0003\nI\r1\u0001g\u0011\u001d\tI,!3A\u0002\u0019Dq!!0\u0002J\u0002\u0007a\r\u0003\u0005\u0002B\u0006%\u0007\u0019AAl!\ra1E\u001f\u0005\b\u00037\fI\r1\u0001s\u0003%\t7oY3oI&tw\r\u0003\u0004w\u0001\u0011\u0005\u0011q\u001c\u000b\u0007\u0003C\fI/a;\u0011\rm\fiF_Ar!\r)\u0014Q]\u0005\u0004\u0003O4$\u0001F#yK\u000e,Ho\u001c:Ti\u0006<WmU;n[\u0006\u0014\u0018\u0010C\u0004\u0002*\u0005u\u0007\u0019\u00014\t\u000f\u00055\u0018Q\u001ca\u0001M\u0006I\u0011\r\u001e;f[B$\u0018\n\u001a\u0005\b\u0003c\u0004A\u0011AAz\u0003\u001d\u0011H\r\u001a'jgR$B!!>\u0002~B!aITA|!\r)\u0014\u0011`\u0005\u0004\u0003w4$A\u0004*E\tN#xN]1hK&sgm\u001c\u0005\n\u0003\u007f\fy\u000f%AA\u0002I\f!bY1dQ\u0016$wJ\u001c7z\u0011\u001d\u0011\u0019\u0001\u0001C\u0001\u0005\u000b\t\u0001\"Y:PaRLwN\\\u000b\u0005\u0005\u000f\u0011y\u0001\u0006\u0003\u0003\n\t\u0005\u0002\u0003\u0002\u0007$\u0005\u0017\u0001BA!\u0004\u0003\u00101\u0001A\u0001\u0003B\t\u0005\u0003\u0011\rAa\u0005\u0003\u0003Q\u000bBA!\u0006\u0003\u001cA\u0019ABa\u0006\n\u0007\teQBA\u0004O_RD\u0017N\\4\u0011\u00071\u0011i\"C\u0002\u0003 5\u00111!\u00118z\u0011%\u0011\u0019C!\u0001\u0005\u0002\u0004\u0011)#\u0001\u0002g]B)ABa\n\u0003\f%\u0019!\u0011F\u0007\u0003\u0011q\u0012\u0017P\\1nKzBqA!\f\u0001\t\u0013\u0011y#\u0001\tti\u0006<WmV5uQ\u0012+G/Y5mgR!\u00111\u0002B\u0019\u0011!\u0011\u0019Da\u000bA\u0002\u0005-\u0011!B:uC\u001e,\u0007b\u0002B\u001c\u0001\u0011\u0005!\u0011H\u0001\u0004e\u0012$G\u0003BA|\u0005wAqA!\u0010\u00036\u0001\u0007a-A\u0003sI\u0012LE\rC\u0004\u0003B\u0001!\tAa\u0011\u0002!M$(/Z1n\u00052|7m[:MSN$HC\u0001B#!\u00111eJa\u0012\u0011\u0007\u0019\u0012I%C\u0002\u0003L\t\u0011qb\u0015;sK\u0006l'\t\\8dW\u0012\u000bG/\u0019\u0005\b\u0005\u001f\u0002A\u0011\u0001B)\u0003Yy\u0007/\u001a:bi&|gn\u0012:ba\"4uN]*uC\u001e,G\u0003\u0002B*\u0005G\u0002BA!\u0016\u0003`5\u0011!q\u000b\u0006\u0005\u00053\u0012Y&A\u0003tG>\u0004XMC\u0002\u0003^\u0011\t!!^5\n\t\t\u0005$q\u000b\u0002\u0012%\u0012#u\n]3sCRLwN\\$sCBD\u0007bBA\u0015\u0005\u001b\u0002\rA\u001a\u0005\b\u0005O\u0002A\u0011\u0001B5\u0003Qy\u0007/\u001a:bi&|gn\u0012:ba\"4uN\u001d&pER!!1\u000eB7!\u00111eJa\u0015\t\r\u0015\u0014)\u00071\u0001g\u0011\u001d\u0011\t\b\u0001C\u0001\u0005g\nA\u0001]8pYR!!Q\u000fB>!\r1#qO\u0005\u0004\u0005s\u0012!\u0001\u0003)p_2$\u0015\r^1\t\u000f\tu$q\u000ea\u0001u\u0006!a.Y7f\u0011\u001d\u0011\t\t\u0001C\u0001\u0005\u0007\u000b!\"\u00199q'VlW.\u0019:z)\t\u0011)\tE\u0002'\u0005\u000fK1A!#\u0003\u0005)\t\u0005\u000f]*v[6\f'/\u001f\u0005\b\u0005\u001b\u0003A\u0011\u0001BH\u0003\u0015\u0019Gn\\:f)\t\u0011\t\nE\u0002\r\u0005'K1A!&\u000e\u0005\u0011)f.\u001b;\t\u0013\te\u0005!%A\u0005\u0002\tm\u0015aE:uC\u001e,G)\u0019;bI\u0011,g-Y;mi\u0012\u0012TC\u0001BOU\r\u0011(qT\u0016\u0003\u0005C\u0003BAa)\u0003.6\u0011!Q\u0015\u0006\u0005\u0005O\u0013I+A\u0005v]\u000eDWmY6fI*\u0019!1V\u0007\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u00030\n\u0015&!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"I!1\u0017\u0001\u0012\u0002\u0013\u0005!1T\u0001\u0017gR\fw-Z!ui\u0016l\u0007\u000f\u001e\u0013eK\u001a\fW\u000f\u001c;%g!I!q\u0017\u0001\u0012\u0002\u0013\u0005!1T\u0001\u0012e\u0012$G*[:uI\u0011,g-Y;mi\u0012\nt\u0001\u0003B^\u0005!\u0005AA!0\u0002\u001d\u0005\u0003\bo\u0015;biV\u001c8\u000b^8sKB\u0019aEa0\u0007\u000f\u0005\u0011\u0001\u0012\u0001\u0003\u0003BN\u0019!qX\u0006\t\u000f1\u0012y\f\"\u0001\u0003FR\u0011!Q\u0018\u0005\u000b\u0005\u0013\u0014yL1A\u0005\u0002\t-\u0017aD\"V%J+e\nV0W\u000bJ\u001b\u0016j\u0014(\u0016\u0005\u0005-\u0003\"\u0003Bh\u0005\u007f\u0003\u000b\u0011BA&\u0003A\u0019UK\u0015*F\u001dR{f+\u0012*T\u0013>s\u0005\u0005\u0003\u0005\u0003T\n}F\u0011\u0001Bk\u0003=\u0019'/Z1uK2Kg/Z*u_J,Gc\u0001\u0018\u0003X\"A!\u0011\u001cBi\u0001\u0004\u0011Y.\u0001\u0003d_:4\u0007c\u00010\u0003^&\u0019!q\u001c\u0003\u0003\u0013M\u0003\u0018M]6D_:4\u0007B\u0003Br\u0005\u007f\u000b\n\u0011\"\u0001\u0003f\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uII*\"Aa:+\u0007\t\u0012y\n"
)
public class AppStatusStore {
   private final KVStore store;
   private final Option<AppStatusListener> listener;

   public static Option<AppStatusListener> $lessinit$greater$default$2() {
      return AppStatusStore$.MODULE$.$lessinit$greater$default$2();
   }

   public static AppStatusStore createLiveStore(SparkConf var0) {
      return AppStatusStore$.MODULE$.createLiveStore(var0);
   }

   public static long CURRENT_VERSION() {
      return AppStatusStore$.MODULE$.CURRENT_VERSION();
   }

   public KVStore store() {
      return this.store;
   }

   public Option<AppStatusListener> listener() {
      return this.listener;
   }

   public ApplicationInfo applicationInfo() {
      return ((ApplicationInfoWrapper)this.store().view(ApplicationInfoWrapper.class).max(1L).iterator().next()).info();
   }

   public ApplicationEnvironmentInfo environmentInfo() {
      Class klass = ApplicationEnvironmentInfoWrapper.class;
      return ((ApplicationEnvironmentInfoWrapper)this.store().read(klass, klass.getName())).info();
   }

   public Seq<JobData> jobsList(List<JobExecutionStatus> statuses) {
      Iterable it = (Iterable)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.store().view(JobDataWrapper.class).reverse()).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final JobData apply(JobDataWrapper x$1) {
            return x$1.info();
         }
      }, Iterable$.MODULE$.canBuildFrom());
      return statuses != null && !statuses.isEmpty() ? ((TraversableOnce)it.filter(new Serializable(this, statuses) {
         public static final long serialVersionUID = 0L;
         private final List statuses$1;

         public final boolean apply(JobData job) {
            return this.statuses$1.contains(job.status());
         }

         public {
            this.statuses$1 = statuses$1;
         }
      })).toSeq() : it.toSeq();
   }

   public JobData job(int jobId) {
      return ((JobDataWrapper)this.store().read(JobDataWrapper.class, BoxesRunTime.boxToInteger(jobId))).info();
   }

   public Seq<ExecutorSummary> executorList(boolean activeOnly) {
      KVStoreView base = this.store().view(ExecutorSummaryWrapper.class);
      KVStoreView filtered = activeOnly ? base.index("active").reverse().first(BoxesRunTime.boxToBoolean(true)).last(BoxesRunTime.boxToBoolean(true)) : base;
      return ((TraversableOnce)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(filtered).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ExecutorSummary apply(ExecutorSummaryWrapper x$2) {
            return x$2.info();
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq();
   }

   public ExecutorSummary executorSummary(String executorId) {
      return ((ExecutorSummaryWrapper)this.store().read(ExecutorSummaryWrapper.class, executorId)).info();
   }

   public Seq<StageData> activeStages() {
      return (Seq)this.listener().<StageData>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<StageData> apply(AppStatusListener x$3) {
            return x$3.activeStages();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nil$ apply() {
            return Nil$.MODULE$;
         }
      });
   }

   public Seq<StageData> stageList(List<StageStatus> statuses) {
      Iterable it = (Iterable)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.store().view(StageDataWrapper.class).reverse()).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final StageData apply(StageDataWrapper x$4) {
            return x$4.info();
         }
      }, Iterable$.MODULE$.canBuildFrom());
      return statuses != null && !statuses.isEmpty() ? ((TraversableOnce)it.filter(new Serializable(this, statuses) {
         public static final long serialVersionUID = 0L;
         private final List statuses$2;

         public final boolean apply(StageData s) {
            return this.statuses$2.contains(s.status());
         }

         public {
            this.statuses$2 = statuses$2;
         }
      })).toSeq() : it.toSeq();
   }

   public Seq<StageData> stageData(int stageId, boolean details) {
      return ((TraversableOnce)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.store().view(StageDataWrapper.class).index("stageId").first(BoxesRunTime.boxToInteger(stageId)).last(BoxesRunTime.boxToInteger(stageId))).asScala()).map(new Serializable(this, details) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusStore $outer;
         private final boolean details$1;

         public final StageData apply(StageDataWrapper s) {
            return this.details$1 ? this.$outer.org$apache$spark$status$AppStatusStore$$stageWithDetails(s.info()) : s.info();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.details$1 = details$1;
            }
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq();
   }

   public boolean stageData$default$2() {
      return false;
   }

   public StageData lastStageAttempt(int stageId) {
      KVStoreIterator it = this.store().view(StageDataWrapper.class).index("stageId").reverse().first(BoxesRunTime.boxToInteger(stageId)).closeableIterator();

      try {
         if (!it.hasNext()) {
            throw new NoSuchElementException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No stage with id ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(stageId)})));
         }

         ((StageDataWrapper)it.next()).info();
      } finally {
         it.close();
      }

      return it;
   }

   public StageData stageAttempt(int stageId, int stageAttemptId, boolean details) {
      int[] stageKey = new int[]{stageId, stageAttemptId};
      StageData stage = ((StageDataWrapper)this.store().read(StageDataWrapper.class, stageKey)).info();
      return details ? this.org$apache$spark$status$AppStatusStore$$stageWithDetails(stage) : stage;
   }

   public boolean stageAttempt$default$3() {
      return false;
   }

   public long taskCount(int stageId, int stageAttemptId) {
      return this.store().count(TaskDataWrapper.class, "stage", new int[]{stageId, stageAttemptId});
   }

   public Map<String, Object> localitySummary(int stageId, int stageAttemptId) {
      return ((StageDataWrapper)this.store().read(StageDataWrapper.class, new int[]{stageId, stageAttemptId})).locality();
   }

   public Option<TaskMetricDistributions> taskSummary(int stageId, int stageAttemptId, double[] unsortedQuantiles) {
      int[] stageKey = new int[]{stageId, stageAttemptId};
      double[] quantiles = (double[])Predef$.MODULE$.doubleArrayOps(unsortedQuantiles).sorted(Ordering.Double$.MODULE$);
      long count = BoxesRunTime.unboxToLong(Utils$.MODULE$.tryWithResource(new Serializable(this, stageKey) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusStore $outer;
         private final int[] stageKey$1;

         public final KVStoreIterator<TaskDataWrapper> apply() {
            return this.$outer.store().view(TaskDataWrapper.class).parent(this.stageKey$1).index("ert").first(BoxesRunTime.boxToLong(0L)).closeableIterator();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.stageKey$1 = stageKey$1;
            }
         }
      }, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(KVStoreIterator<TaskDataWrapper> it) {
            long _count = 0L;

            while(it.hasNext()) {
               ++_count;
               it.skip(1L);
            }

            return _count;
         }
      }));
      if (count <= 0L) {
         return None$.MODULE$;
      } else {
         CachedQuantile[] cachedQuantiles = (CachedQuantile[])Predef$.MODULE$.doubleArrayOps((double[])Predef$.MODULE$.doubleArrayOps(quantiles).filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final AppStatusStore $outer;

            public final boolean apply(double q) {
               return this.apply$mcZD$sp(q);
            }

            public boolean apply$mcZD$sp(double q) {
               return this.$outer.org$apache$spark$status$AppStatusStore$$shouldCacheQuantile(q);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         })).flatMap(new Serializable(this, stageId, stageAttemptId, count) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final AppStatusStore $outer;
            private final int stageId$1;
            private final int stageAttemptId$1;
            public final long count$1;

            public final Iterable<CachedQuantile> apply(double q) {
               Object[] qkey = Array$.MODULE$.apply(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stageId$1), BoxesRunTime.boxToInteger(this.stageAttemptId$1), this.$outer.org$apache$spark$status$AppStatusStore$$quantileToString(q)}), ClassTag$.MODULE$.Any());
               return Option$.MODULE$.<CachedQuantile>option2Iterable(this.$outer.asOption(new Serializable(this, qkey) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final Object[] qkey$1;

                  public final CachedQuantile apply() {
                     return (CachedQuantile)this.$outer.org$apache$spark$status$AppStatusStore$$anonfun$$$outer().store().read(CachedQuantile.class, this.qkey$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.qkey$1 = qkey$1;
                     }
                  }
               }).filter(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final boolean apply(CachedQuantile x$5) {
                     return x$5.taskCount() == this.$outer.count$1;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }));
            }

            // $FF: synthetic method
            public AppStatusStore org$apache$spark$status$AppStatusStore$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.stageId$1 = stageId$1;
                  this.stageAttemptId$1 = stageAttemptId$1;
                  this.count$1 = count$1;
               }
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(CachedQuantile.class)));
         if (Predef$.MODULE$.refArrayOps(cachedQuantiles).size() == Predef$.MODULE$.doubleArrayOps(quantiles).size()) {
            TaskMetricDistributions distributions = new TaskMetricDistributions(Predef$.MODULE$.wrapDoubleArray(quantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$6) {
                  return x$6.executorDeserializeTime();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$7) {
                  return x$7.executorDeserializeCpuTime();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$8) {
                  return x$8.executorRunTime();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$9) {
                  return x$9.executorCpuTime();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$10) {
                  return x$10.resultSize();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$11) {
                  return x$11.jvmGcTime();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$12) {
                  return x$12.resultSerializationTime();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$13) {
                  return x$13.gettingResultTime();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$14) {
                  return x$14.schedulerDelay();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$15) {
                  return x$15.peakExecutionMemory();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$16) {
                  return x$16.memoryBytesSpilled();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$17) {
                  return x$17.diskBytesSpilled();
               }
            }, cachedQuantiles), new InputMetricDistributions(this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$18) {
                  return x$18.bytesRead();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$19) {
                  return x$19.recordsRead();
               }
            }, cachedQuantiles)), new OutputMetricDistributions(this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$20) {
                  return x$20.bytesWritten();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$21) {
                  return x$21.recordsWritten();
               }
            }, cachedQuantiles)), new ShuffleReadMetricDistributions(this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$22) {
                  return x$22.shuffleReadBytes();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$23) {
                  return x$23.shuffleRecordsRead();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$24) {
                  return x$24.shuffleRemoteBlocksFetched();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$25) {
                  return x$25.shuffleLocalBlocksFetched();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$26) {
                  return x$26.shuffleFetchWaitTime();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$27) {
                  return x$27.shuffleRemoteBytesRead();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$28) {
                  return x$28.shuffleRemoteBytesReadToDisk();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$29) {
                  return x$29.shuffleTotalBlocksFetched();
               }
            }, cachedQuantiles)), new ShuffleWriteMetricDistributions(this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$30) {
                  return x$30.shuffleWriteBytes();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$31) {
                  return x$31.shuffleWriteRecords();
               }
            }, cachedQuantiles), this.toValues$1(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(CachedQuantile x$32) {
                  return x$32.shuffleWriteTime();
               }
            }, cachedQuantiles)));
            return new Some<TaskMetricDistributions>(distributions);
         } else {
            long[] indices = (long[])Predef$.MODULE$.doubleArrayOps(quantiles).map(new Serializable(this, count) {
               public static final long serialVersionUID = 0L;
               private final long count$1;

               public final long apply(double q) {
                  return this.apply$mcJD$sp(q);
               }

               public long apply$mcJD$sp(double q) {
                  return package$.MODULE$.min((long)(q * (double)this.count$1), this.count$1 - 1L);
               }

               public {
                  this.count$1 = count$1;
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()));
            TaskMetricDistributions computedQuantiles = new TaskMetricDistributions(Predef$.MODULE$.wrapDoubleArray(quantiles), this.scanTasks$1("des", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.executorDeserializeTime();
               }
            }, stageKey, indices), this.scanTasks$1("dct", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.executorDeserializeCpuTime();
               }
            }, stageKey, indices), this.scanTasks$1("ert", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.executorRunTime();
               }
            }, stageKey, indices), this.scanTasks$1("ect", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.executorCpuTime();
               }
            }, stageKey, indices), this.scanTasks$1("rs", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.resultSize();
               }
            }, stageKey, indices), this.scanTasks$1("gc", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.jvmGcTime();
               }
            }, stageKey, indices), this.scanTasks$1("rst", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.resultSerializationTime();
               }
            }, stageKey, indices), this.scanTasks$1("grt", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.gettingResultTime();
               }
            }, stageKey, indices), this.scanTasks$1("dly", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.schedulerDelay();
               }
            }, stageKey, indices), this.scanTasks$1("pem", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.peakExecutionMemory();
               }
            }, stageKey, indices), this.scanTasks$1("mbs", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.memoryBytesSpilled();
               }
            }, stageKey, indices), this.scanTasks$1("dbs", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.diskBytesSpilled();
               }
            }, stageKey, indices), new InputMetricDistributions(this.scanTasks$1("is", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.inputBytesRead();
               }
            }, stageKey, indices), this.scanTasks$1("ir", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.inputRecordsRead();
               }
            }, stageKey, indices)), new OutputMetricDistributions(this.scanTasks$1("os", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.outputBytesWritten();
               }
            }, stageKey, indices), this.scanTasks$1("or", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.outputRecordsWritten();
               }
            }, stageKey, indices)), new ShuffleReadMetricDistributions(this.scanTasks$1("stby", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper m) {
                  return m.shuffleLocalBytesRead() + m.shuffleRemoteBytesRead();
               }
            }, stageKey, indices), this.scanTasks$1("srr", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.shuffleRecordsRead();
               }
            }, stageKey, indices), this.scanTasks$1("srbl", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.shuffleRemoteBlocksFetched();
               }
            }, stageKey, indices), this.scanTasks$1("slbl", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.shuffleLocalBlocksFetched();
               }
            }, stageKey, indices), this.scanTasks$1("srt", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.shuffleFetchWaitTime();
               }
            }, stageKey, indices), this.scanTasks$1("srby", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.shuffleRemoteBytesRead();
               }
            }, stageKey, indices), this.scanTasks$1("srbd", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.shuffleRemoteBytesReadToDisk();
               }
            }, stageKey, indices), this.scanTasks$1("stbl", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper m) {
                  return m.shuffleLocalBlocksFetched() + m.shuffleRemoteBlocksFetched();
               }
            }, stageKey, indices)), new ShuffleWriteMetricDistributions(this.scanTasks$1("sws", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.shuffleBytesWritten();
               }
            }, stageKey, indices), this.scanTasks$1("swr", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.shuffleRecordsWritten();
               }
            }, stageKey, indices), this.scanTasks$1("swt", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(TaskDataWrapper t) {
                  return t.shuffleWriteTime();
               }
            }, stageKey, indices)));
            ((IterableLike)((TraversableLike)computedQuantiles.quantiles().zipWithIndex(IndexedSeq$.MODULE$.canBuildFrom())).filter(new Serializable(this, quantiles) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final AppStatusStore $outer;
               private final double[] quantiles$1;

               public final boolean apply(Tuple2<Object, Object> x0$1) {
                  if (x0$1 == null) {
                     throw new MatchError(x0$1);
                  } else {
                     double q = x0$1._1$mcD$sp();
                     boolean var5 = Predef$.MODULE$.doubleArrayOps(this.quantiles$1).contains(BoxesRunTime.boxToDouble(q)) && this.$outer.org$apache$spark$status$AppStatusStore$$shouldCacheQuantile(q);
                     return var5;
                  }
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.quantiles$1 = quantiles$1;
                  }
               }
            })).foreach(new Serializable(this, stageId, stageAttemptId, count, computedQuantiles) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final AppStatusStore $outer;
               private final int stageId$1;
               private final int stageAttemptId$1;
               private final long count$1;
               private final TaskMetricDistributions computedQuantiles$1;

               public final void apply(Tuple2<Object, Object> x0$2) {
                  if (x0$2 != null) {
                     double q = x0$2._1$mcD$sp();
                     int idx = x0$2._2$mcI$sp();
                     CachedQuantile cached = new CachedQuantile(this.stageId$1, this.stageAttemptId$1, this.$outer.org$apache$spark$status$AppStatusStore$$quantileToString(q), this.count$1, BoxesRunTime.unboxToDouble(this.computedQuantiles$1.executorDeserializeTime().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.executorDeserializeCpuTime().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.executorRunTime().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.executorCpuTime().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.resultSize().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.jvmGcTime().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.resultSerializationTime().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.gettingResultTime().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.schedulerDelay().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.peakExecutionMemory().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.memoryBytesSpilled().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.diskBytesSpilled().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.inputMetrics().bytesRead().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.inputMetrics().recordsRead().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.outputMetrics().bytesWritten().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.outputMetrics().recordsWritten().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleReadMetrics().readBytes().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleReadMetrics().readRecords().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleReadMetrics().remoteBlocksFetched().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleReadMetrics().localBlocksFetched().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleReadMetrics().fetchWaitTime().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleReadMetrics().remoteBytesRead().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleReadMetrics().remoteBytesReadToDisk().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleReadMetrics().totalBlocksFetched().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleWriteMetrics().writeBytes().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleWriteMetrics().writeRecords().apply(idx)), BoxesRunTime.unboxToDouble(this.computedQuantiles$1.shuffleWriteMetrics().writeTime().apply(idx)));
                     this.$outer.store().write(cached);
                     BoxedUnit var6 = BoxedUnit.UNIT;
                  } else {
                     throw new MatchError(x0$2);
                  }
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.stageId$1 = stageId$1;
                     this.stageAttemptId$1 = stageAttemptId$1;
                     this.count$1 = count$1;
                     this.computedQuantiles$1 = computedQuantiles$1;
                  }
               }
            });
            return new Some<TaskMetricDistributions>(computedQuantiles);
         }
      }
   }

   public boolean org$apache$spark$status$AppStatusStore$$shouldCacheQuantile(double q) {
      return package$.MODULE$.round(q * (double)100) % 5L == 0L;
   }

   public String org$apache$spark$status$AppStatusStore$$quantileToString(double q) {
      return BoxesRunTime.boxToLong(package$.MODULE$.round(q * (double)100)).toString();
   }

   public Seq<TaskData> taskList(int stageId, int stageAttemptId, int maxTasks) {
      int[] stageKey = new int[]{stageId, stageAttemptId};
      return (Seq)((TraversableOnce)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.store().view(TaskDataWrapper.class).index("stage").first(stageKey).last(stageKey).reverse().max((long)maxTasks)).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final TaskData apply(TaskDataWrapper x$33) {
            return x$33.toApi();
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq().reverse();
   }

   public Seq<TaskData> taskList(int stageId, int stageAttemptId, int offset, int length, TaskSorting sortBy) {
      Tuple2 var9;
      if (TaskSorting.ID.equals(sortBy)) {
         var9 = new Tuple2(None$.MODULE$, BoxesRunTime.boxToBoolean(true));
      } else if (TaskSorting.INCREASING_RUNTIME.equals(sortBy)) {
         var9 = new Tuple2(new Some("ert"), BoxesRunTime.boxToBoolean(true));
      } else {
         if (!TaskSorting.DECREASING_RUNTIME.equals(sortBy)) {
            throw new MatchError(sortBy);
         }

         var9 = new Tuple2(new Some("ert"), BoxesRunTime.boxToBoolean(false));
      }

      if (var9 != null) {
         Option indexName = (Option)var9._1();
         boolean ascending = var9._2$mcZ$sp();
         Tuple2 var12 = new Tuple2(indexName, BoxesRunTime.boxToBoolean(ascending));
         Option indexName = (Option)var12._1();
         boolean ascending = var12._2$mcZ$sp();
         return this.taskList(stageId, stageAttemptId, offset, length, indexName, ascending);
      } else {
         throw new MatchError(var9);
      }
   }

   public Seq<TaskData> taskList(int stageId, int stageAttemptId, int offset, int length, Option<String> sortBy, boolean ascending) {
      int[] stageKey = new int[]{stageId, stageAttemptId};
      KVStoreView base = this.store().view(TaskDataWrapper.class);
      KVStoreView indexed;
      if (sortBy instanceof Some) {
         Some var11 = (Some)sortBy;
         String index = (String)var11.x();
         indexed = base.index(index).parent(stageKey);
      } else {
         indexed = base.index("stage").first(stageKey).last(stageKey);
      }

      KVStoreView ordered = ascending ? indexed : indexed.reverse();
      return ((TraversableOnce)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(ordered.skip((long)offset).max((long)length)).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final TaskData apply(TaskDataWrapper x$35) {
            return x$35.toApi();
         }
      }, Iterable$.MODULE$.canBuildFrom())).toSeq();
   }

   public Map<String, ExecutorStageSummary> executorSummary(int stageId, int attemptId) {
      int[] stageKey = new int[]{stageId, attemptId};
      return ((TraversableOnce)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.store().view(ExecutorStageSummaryWrapper.class).index("stage").first(stageKey).last(stageKey)).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, ExecutorStageSummary> apply(ExecutorStageSummaryWrapper exec) {
            return Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(exec.executorId()), exec.info());
         }
      }, Iterable$.MODULE$.canBuildFrom())).<String, ExecutorStageSummary>toMap(Predef$.MODULE$.$conforms());
   }

   public Seq<RDDStorageInfo> rddList(boolean cachedOnly) {
      return ((TraversableOnce)((TraversableLike)((TraversableLike)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.store().view(RDDStorageInfoWrapper.class)).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final RDDStorageInfo apply(RDDStorageInfoWrapper x$36) {
            return x$36.info();
         }
      }, Iterable$.MODULE$.canBuildFrom())).filter(new Serializable(this, cachedOnly) {
         public static final long serialVersionUID = 0L;
         private final boolean cachedOnly$1;

         public final boolean apply(RDDStorageInfo rdd) {
            return !this.cachedOnly$1 || rdd.numCachedPartitions() > 0;
         }

         public {
            this.cachedOnly$1 = cachedOnly$1;
         }
      })).toSeq();
   }

   public <T> Option<T> asOption(Function0<T> fn) {
      Object var10000;
      try {
         var10000 = new Some(fn.apply());
      } catch (NoSuchElementException var2) {
         var10000 = None$.MODULE$;
      }

      return (Option<T>)var10000;
   }

   public StageData org$apache$spark$status$AppStatusStore$$stageWithDetails(StageData stage) {
      Map tasks = ((TraversableOnce)this.taskList(stage.stageId(), stage.attemptId(), Integer.MAX_VALUE).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<Object, TaskData> apply(TaskData t) {
            return new Tuple2<Object, TaskData>(BoxesRunTime.boxToLong(t.taskId()), t);
         }
      }, Seq$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
      return new StageData(stage.status(), stage.stageId(), stage.attemptId(), stage.numTasks(), stage.numActiveTasks(), stage.numCompleteTasks(), stage.numFailedTasks(), stage.numKilledTasks(), stage.numCompletedIndices(), stage.executorRunTime(), stage.executorCpuTime(), stage.submissionTime(), stage.firstTaskLaunchedTime(), stage.completionTime(), stage.failureReason(), stage.inputBytes(), stage.inputRecords(), stage.outputBytes(), stage.outputRecords(), stage.shuffleReadBytes(), stage.shuffleReadRecords(), stage.shuffleWriteBytes(), stage.shuffleWriteRecords(), stage.memoryBytesSpilled(), stage.diskBytesSpilled(), stage.name(), stage.description(), stage.details(), stage.schedulingPool(), stage.rddIds(), stage.accumulatorUpdates(), new Some(tasks), new Some(this.executorSummary(stage.stageId(), stage.attemptId())), stage.killedTasksSummary());
   }

   public RDDStorageInfo rdd(int rddId) {
      return ((RDDStorageInfoWrapper)this.store().read(RDDStorageInfoWrapper.class, BoxesRunTime.boxToInteger(rddId))).info();
   }

   public boolean rddList$default$1() {
      return true;
   }

   public Seq<StreamBlockData> streamBlocksList() {
      return ((TraversableOnce)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.store().view(StreamBlockData.class)).asScala()).toSeq();
   }

   public RDDOperationGraph operationGraphForStage(int stageId) {
      return ((RDDOperationGraphWrapper)this.store().read(RDDOperationGraphWrapper.class, BoxesRunTime.boxToInteger(stageId))).toRDDOperationGraph();
   }

   public Seq<RDDOperationGraph> operationGraphForJob(int jobId) {
      JobDataWrapper job = (JobDataWrapper)this.store().read(JobDataWrapper.class, BoxesRunTime.boxToInteger(jobId));
      Seq stages = job.info().stageIds();
      return (Seq)stages.map(new Serializable(this, job) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusStore $outer;
         private final JobDataWrapper job$1;

         public final RDDOperationGraph apply(int id) {
            RDDOperationGraph g = ((RDDOperationGraphWrapper)this.$outer.store().read(RDDOperationGraphWrapper.class, BoxesRunTime.boxToInteger(id))).toRDDOperationGraph();
            if (this.job$1.skippedStages().contains(BoxesRunTime.boxToInteger(id)) && !g.rootCluster().name().contains("skipped")) {
               g.rootCluster().setName((new StringBuilder()).append((Object)g.rootCluster().name()).append((Object)" (skipped)").toString());
            }

            return g;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.job$1 = job$1;
            }
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public PoolData pool(String name) {
      return (PoolData)this.store().read(PoolData.class, name);
   }

   public AppSummary appSummary() {
      return (AppSummary)this.store().read(AppSummary.class, AppSummary.class.getName());
   }

   public void close() {
      this.store().close();
   }

   private final IndexedSeq toValues$1(Function1 fn, CachedQuantile[] cachedQuantiles$1) {
      return (IndexedSeq)Predef$.MODULE$.refArrayOps(cachedQuantiles$1).map(fn, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
   }

   private final IndexedSeq scanTasks$1(String index, Function1 fn, int[] stageKey$1, long[] indices$1) {
      return (IndexedSeq)Utils$.MODULE$.tryWithResource(new Serializable(this, stageKey$1, index) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusStore $outer;
         private final int[] stageKey$1;
         private final String index$1;

         public final KVStoreIterator<TaskDataWrapper> apply() {
            return this.$outer.store().view(TaskDataWrapper.class).parent(this.stageKey$1).index(this.index$1).first(BoxesRunTime.boxToLong(0L)).closeableIterator();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.stageKey$1 = stageKey$1;
               this.index$1 = index$1;
            }
         }
      }, new Serializable(this, indices$1, fn) {
         public static final long serialVersionUID = 0L;
         private final long[] indices$1;
         public final Function1 fn$1;

         public final scala.collection.immutable.IndexedSeq<Object> apply(KVStoreIterator<TaskDataWrapper> it) {
            DoubleRef last = DoubleRef.create(Double.NaN);
            LongRef currentIdx = LongRef.create(-1L);
            return Predef$.MODULE$.doubleArrayOps((double[])Predef$.MODULE$.longArrayOps(this.indices$1).map(new Serializable(this, last, currentIdx, it) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final DoubleRef last$1;
               private final LongRef currentIdx$1;
               private final KVStoreIterator it$1;

               public final double apply(long idx) {
                  return this.apply$mcDJ$sp(idx);
               }

               public double apply$mcDJ$sp(long idx) {
                  double var10000;
                  if (idx == this.currentIdx$1.elem) {
                     var10000 = this.last$1.elem;
                  } else {
                     long diff = idx - this.currentIdx$1.elem;
                     this.currentIdx$1.elem = idx;
                     if (this.it$1.skip(diff - 1L)) {
                        this.last$1.elem = (double)BoxesRunTime.unboxToLong(this.$outer.fn$1.apply(this.it$1.next()));
                        var10000 = this.last$1.elem;
                     } else {
                        var10000 = Double.NaN;
                     }
                  }

                  return var10000;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.last$1 = last$1;
                     this.currentIdx$1 = currentIdx$1;
                     this.it$1 = it$1;
                  }
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Double()))).toIndexedSeq();
         }

         public {
            this.indices$1 = indices$1;
            this.fn$1 = fn$1;
         }
      });
   }

   public AppStatusStore(KVStore store, Option<AppStatusListener> listener) {
      this.store = store;
      this.listener = listener;
   }
}
