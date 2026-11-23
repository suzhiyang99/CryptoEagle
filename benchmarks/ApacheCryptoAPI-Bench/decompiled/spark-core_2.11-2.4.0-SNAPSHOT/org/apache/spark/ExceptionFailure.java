package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.scheduler.AccumulableInfo;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple7;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\t=h\u0001B\u0001\u0003\u0001&\u0011\u0001#\u0012=dKB$\u0018n\u001c8GC&dWO]3\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001c\u0001aE\u0003\u0001\u0015A!r\u0003\u0005\u0002\f\u001d5\tABC\u0001\u000e\u0003\u0015\u00198-\u00197b\u0013\tyAB\u0001\u0004B]f\u0014VM\u001a\t\u0003#Ii\u0011AA\u0005\u0003'\t\u0011\u0001\u0003V1tW\u001a\u000b\u0017\u000e\\3e%\u0016\f7o\u001c8\u0011\u0005-)\u0012B\u0001\f\r\u0005\u001d\u0001&o\u001c3vGR\u0004\"a\u0003\r\n\u0005ea!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000e\u0001\u0005+\u0007I\u0011\u0001\u000f\u0002\u0013\rd\u0017m]:OC6,W#A\u000f\u0011\u0005y\tcBA\u0006 \u0013\t\u0001C\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003E\r\u0012aa\u0015;sS:<'B\u0001\u0011\r\u0011!)\u0003A!E!\u0002\u0013i\u0012AC2mCN\u001ch*Y7fA!Aq\u0005\u0001BK\u0002\u0013\u0005A$A\u0006eKN\u001c'/\u001b9uS>t\u0007\u0002C\u0015\u0001\u0005#\u0005\u000b\u0011B\u000f\u0002\u0019\u0011,7o\u0019:jaRLwN\u001c\u0011\t\u0011-\u0002!Q3A\u0005\u00021\n!b\u001d;bG.$&/Y2f+\u0005i\u0003cA\u0006/a%\u0011q\u0006\u0004\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003cYj\u0011A\r\u0006\u0003gQ\nA\u0001\\1oO*\tQ'\u0001\u0003kCZ\f\u0017BA\u001c3\u0005E\u0019F/Y2l)J\f7-Z#mK6,g\u000e\u001e\u0005\ts\u0001\u0011\t\u0012)A\u0005[\u0005Y1\u000f^1dWR\u0013\u0018mY3!\u0011!Y\u0004A!f\u0001\n\u0003a\u0012A\u00044vY2\u001cF/Y2l)J\f7-\u001a\u0005\t{\u0001\u0011\t\u0012)A\u0005;\u0005ya-\u001e7m'R\f7m\u001b+sC\u000e,\u0007\u0005\u0003\u0005@\u0001\t\u0015\r\u0011\"\u0003A\u0003A)\u0007pY3qi&|gn\u0016:baB,'/F\u0001B!\rY!\tR\u0005\u0003\u00072\u0011aa\u00149uS>t\u0007CA\tF\u0013\t1%AA\u000fUQJ|w/\u00192mKN+'/[1mSj\fG/[8o/J\f\u0007\u000f]3s\u0011!A\u0005A!E!\u0002\u0013\t\u0015!E3yG\u0016\u0004H/[8o/J\f\u0007\u000f]3sA!A!\n\u0001BK\u0002\u0013\u00051*\u0001\u0007bG\u000e,X.\u00169eCR,7/F\u0001M!\riU\u000b\u0017\b\u0003\u001dNs!a\u0014*\u000e\u0003AS!!\u0015\u0005\u0002\rq\u0012xn\u001c;?\u0013\u0005i\u0011B\u0001+\r\u0003\u001d\u0001\u0018mY6bO\u0016L!AV,\u0003\u0007M+\u0017O\u0003\u0002U\u0019A\u0011\u0011\fX\u0007\u00025*\u00111LA\u0001\ng\u000eDW\rZ;mKJL!!\u0018.\u0003\u001f\u0005\u001b7-^7vY\u0006\u0014G.Z%oM>D\u0001b\u0018\u0001\u0003\u0012\u0003\u0006I\u0001T\u0001\u000eC\u000e\u001cW/\\+qI\u0006$Xm\u001d\u0011\t\u0013\u0005\u0004!\u00111A\u0005\u0002\t\u0011\u0017AB1dGVl7/F\u0001d!\riU\u000b\u001a\u0019\u0005K6\f\u0019\u0001E\u0003gS.\f\t!D\u0001h\u0015\tA'!\u0001\u0003vi&d\u0017B\u00016h\u00055\t5mY;nk2\fGo\u001c:WeA\u0011A.\u001c\u0007\u0001\t%qw.!A\u0001\u0002\u000b\u0005aOA\u0002`IEB\u0001\u0002\u001d\u0001\u0003\u0012\u0003\u0006K!]\u0001\bC\u000e\u001cW/\\:!!\riUK\u001d\u0019\u0004gVt\b\u0003\u00024jiv\u0004\"\u0001\\;\u0005\u00139|\u0017\u0011!A\u0001\u0006\u00031\u0018CA<{!\tY\u00010\u0003\u0002z\u0019\t9aj\u001c;iS:<\u0007CA\u0006|\u0013\taHBA\u0002B]f\u0004\"\u0001\u001c@\u0005\u0013}|\u0017\u0011!A\u0001\u0006\u00031(aA0%eA\u0019A.a\u0001\u0005\u0013}|\u0017\u0011!A\u0001\u0006\u00031\bbCA\u0004\u0001\t\u0005\r\u0011\"\u0001\u0003\u0003\u0013\t!\"Y2dk6\u001cx\fJ3r)\u0011\tY!!\u0005\u0011\u0007-\ti!C\u0002\u0002\u00101\u0011A!\u00168ji\"Q\u00111CA\u0003\u0003\u0003\u0005\r!!\u0006\u0002\u0007a$\u0013\u0007\u0005\u0003N+\u0006]\u0001GBA\r\u0003;\t\t\u0003\u0005\u0004gS\u0006m\u0011q\u0004\t\u0004Y\u0006uA!\u00038p\u0003\u0003\u0005\tQ!\u0001w!\ra\u0017\u0011\u0005\u0003\n\u007f>\f\t\u0011!A\u0003\u0002YDq!!\n\u0001\t\u0003\t9#\u0001\u0004=S:LGO\u0010\u000b\u0011\u0003S\tY#!\f\u00020\u0005E\u00121GA\u001b\u0003o\u0001\"!\u0005\u0001\t\rm\t\u0019\u00031\u0001\u001e\u0011\u00199\u00131\u0005a\u0001;!11&a\tA\u00025BaaOA\u0012\u0001\u0004i\u0002BB \u0002$\u0001\u0007\u0011\t\u0003\u0005K\u0003G\u0001\n\u00111\u0001M\u0011%\t\u00171\u0005I\u0001\u0002\u0004\tI\u0004\u0005\u0003N+\u0006m\u0002GBA\u001f\u0003\u0003\n)\u0005\u0005\u0004gS\u0006}\u00121\t\t\u0004Y\u0006\u0005CA\u00038\u00028\u0005\u0005\t\u0011!B\u0001mB\u0019A.!\u0012\u0005\u0015}\f9$!A\u0001\u0002\u000b\u0005a\u000f\u0003\u0005\u0002&\u0001!\tAAA%)!\tI#a\u0013\u0002V\u0005]\u0003\u0002CA'\u0003\u000f\u0002\r!a\u0014\u0002\u0003\u0015\u00042!TA)\u0013\r\t\u0019f\u0016\u0002\n)\"\u0014xn^1cY\u0016DaASA$\u0001\u0004a\u0005\u0002CA-\u0003\u000f\u0002\r!a\u0017\u0002\u001bA\u0014Xm]3sm\u0016\u001c\u0015-^:f!\rY\u0011QL\u0005\u0004\u0003?b!a\u0002\"p_2,\u0017M\u001c\u0005\t\u0003K\u0001A\u0011\u0001\u0002\u0002dQ1\u0011\u0011FA3\u0003OB\u0001\"!\u0014\u0002b\u0001\u0007\u0011q\n\u0005\u0007\u0015\u0006\u0005\u0004\u0019\u0001'\t\u0011\u0005-\u0004\u0001\"\u0001\u0003\u0003[\n!b^5uQ\u0006\u001b7-^7t)\u0011\tI#a\u001c\t\u000f\u0005\fI\u00071\u0001\u0002rA!Q*VA:a\u0019\t)(!\u001f\u0002\u0000A1a-[A<\u0003{\u00022\u0001\\A=\t-\tY(a\u001c\u0002\u0002\u0003\u0005)\u0011\u0001<\u0003\u0007}#3\u0007E\u0002m\u0003\u007f\"1\"!!\u0002p\u0005\u0005\t\u0011!B\u0001m\n\u0019q\f\n\u001b\t\u000f\u0005\u0015\u0005\u0001\"\u0001\u0002\b\u0006IQ\r_2faRLwN\\\u000b\u0003\u0003\u0013\u0003Ba\u0003\"\u0002P!1\u0011Q\u0012\u0001\u0005Bq\tQ\u0002^8FeJ|'o\u0015;sS:<\u0007bBAI\u0001\u0011%\u00111S\u0001\u0010Kb\u001cW\r\u001d;j_:\u001cFO]5oOR9Q$!&\u0002\u0018\u0006e\u0005BB\u000e\u0002\u0010\u0002\u0007Q\u0004\u0003\u0004(\u0003\u001f\u0003\r!\b\u0005\u0007W\u0005=\u0005\u0019A\u0017\t\u0013\u0005u\u0005!!A\u0005\u0002\u0005}\u0015\u0001B2paf$\u0002#!\u000b\u0002\"\u0006\r\u0016QUAT\u0003S\u000bY+!,\t\u0011m\tY\n%AA\u0002uA\u0001bJAN!\u0003\u0005\r!\b\u0005\tW\u0005m\u0005\u0013!a\u0001[!A1(a'\u0011\u0002\u0003\u0007Q\u0004\u0003\u0005@\u00037\u0003\n\u00111\u0001B\u0011!Q\u00151\u0014I\u0001\u0002\u0004a\u0005\"C1\u0002\u001cB\u0005\t\u0019AA\u001d\u0011%\t\t\fAI\u0001\n\u0003\t\u0019,\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0005\u0005U&fA\u000f\u00028.\u0012\u0011\u0011\u0018\t\u0005\u0003w\u000b)-\u0004\u0002\u0002>*!\u0011qXAa\u0003%)hn\u00195fG.,GMC\u0002\u0002D2\t!\"\u00198o_R\fG/[8o\u0013\u0011\t9-!0\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0005\u0002L\u0002\t\n\u0011\"\u0001\u00024\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012\u0004\"CAh\u0001E\u0005I\u0011AAi\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\"!a5+\u00075\n9\fC\u0005\u0002X\u0002\t\n\u0011\"\u0001\u00024\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\"\u0004\"CAn\u0001E\u0005I\u0011AAo\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIU*\"!a8+\u0007\u0005\u000b9\fC\u0005\u0002d\u0002\t\n\u0011\"\u0001\u0002f\u0006q1m\u001c9zI\u0011,g-Y;mi\u00122TCAAtU\ra\u0015q\u0017\u0005\n\u0003W\u0004\u0011\u0013!C\u0001\u0003[\fabY8qs\u0012\"WMZ1vYR$s'\u0006\u0002\u0002p*\u001a1-a.\t\u0011\u0005M\ba#A\u0005\u0002\u0001\u000b!#\u001a=dKB$\u0018n\u001c8Xe\u0006\u0004\b/\u001a:%c!A\u0011q\u001f\u0001\n\u0002\u0013\u0005!-\u0001\u0005bG\u000e,Xn\u001d\u00132\u0011%\tY\u0010AA\u0001\n\u0003\ni0A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0003\u0003\u007f\u00042!\rB\u0001\u0013\t\u0011#\u0007C\u0005\u0003\u0006\u0001\t\t\u0011\"\u0001\u0003\b\u0005a\u0001O]8ek\u000e$\u0018I]5usV\u0011!\u0011\u0002\t\u0004\u0017\t-\u0011b\u0001B\u0007\u0019\t\u0019\u0011J\u001c;\t\u0013\tE\u0001!!A\u0005\u0002\tM\u0011A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0004u\nU\u0001BCA\n\u0005\u001f\t\t\u00111\u0001\u0003\n!I!\u0011\u0004\u0001\u0002\u0002\u0013\u0005#1D\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011!Q\u0004\t\u0006\u0005?\u0011)C_\u0007\u0003\u0005CQ1Aa\t\r\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0005O\u0011\tC\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011%\u0011Y\u0003AA\u0001\n\u0003\u0011i#\u0001\u0005dC:,\u0015/^1m)\u0011\tYFa\f\t\u0013\u0005M!\u0011FA\u0001\u0002\u0004Q\b\"\u0003B\u001a\u0001\u0005\u0005I\u0011\tB\u001b\u0003!A\u0017m\u001d5D_\u0012,GC\u0001B\u0005\u0011%\u0011I\u0004AA\u0001\n\u0003\u0012Y$\u0001\u0005u_N#(/\u001b8h)\t\ty\u0010C\u0005\u0003@\u0001\t\t\u0011\"\u0011\u0003B\u00051Q-];bYN$B!a\u0017\u0003D!I\u00111\u0003B\u001f\u0003\u0003\u0005\rA\u001f\u0015\u0004\u0001\t\u001d\u0003\u0003\u0002B%\u0005\u001bj!Aa\u0013\u000b\u0007\u0005\r'!\u0003\u0003\u0003P\t-#\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLw!\u0003B*\u0005\u0005\u0005\t\u0012\u0001B+\u0003A)\u0005pY3qi&|gNR1jYV\u0014X\rE\u0002\u0012\u0005/2\u0001\"\u0001\u0002\u0002\u0002#\u0005!\u0011L\n\u0006\u0005/\u0012Yf\u0006\t\u000f\u0005;\u0012\u0019'H\u000f.;\u0005c%qMA\u0015\u001b\t\u0011yFC\u0002\u0003b1\tqA];oi&lW-\u0003\u0003\u0003f\t}#!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8ooA!Q*\u0016B5a\u0019\u0011YGa\u001c\u0003tA1a-\u001bB7\u0005c\u00022\u0001\u001cB8\t)q'qKA\u0001\u0002\u0003\u0015\tA\u001e\t\u0004Y\nMDAC@\u0003X\u0005\u0005\t\u0011!B\u0001m\"A\u0011Q\u0005B,\t\u0003\u00119\b\u0006\u0002\u0003V!Q!\u0011\bB,\u0003\u0003%)Ea\u000f\t\u0015\tu$qKA\u0001\n\u0003\u0013y(A\u0003baBd\u0017\u0010\u0006\t\u0002*\t\u0005%1\u0011BC\u0005\u000f\u0013IIa#\u0003\u000e\"11Da\u001fA\u0002uAaa\nB>\u0001\u0004i\u0002BB\u0016\u0003|\u0001\u0007Q\u0006\u0003\u0004<\u0005w\u0002\r!\b\u0005\u0007\u007f\tm\u0004\u0019A!\t\u0011)\u0013Y\b%AA\u00021C\u0011\"\u0019B>!\u0003\u0005\rAa$\u0011\t5+&\u0011\u0013\u0019\u0007\u0005'\u00139Ja'\u0011\r\u0019L'Q\u0013BM!\ra'q\u0013\u0003\u000b]\n5\u0015\u0011!A\u0001\u0006\u00031\bc\u00017\u0003\u001c\u0012QqP!$\u0002\u0002\u0003\u0005)\u0011\u0001<\t\u0015\t}%qKA\u0001\n\u0003\u0013\t+A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\t\r&1\u0016\t\u0005\u0017\t\u0013)\u000b\u0005\u0006\f\u0005OkR$L\u000fB\u0019\u000eL1A!+\r\u0005\u0019!V\u000f\u001d7fo!Q!Q\u0016BO\u0003\u0003\u0005\r!!\u000b\u0002\u0007a$\u0003\u0007\u0003\u0006\u00032\n]\u0013\u0013!C\u0001\u0003K\f1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u00122\u0004B\u0003B[\u0005/\n\n\u0011\"\u0001\u00038\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uI]*\"A!/+\t\tm\u0016q\u0017\t\u0005\u001bV\u0013i\f\r\u0004\u0003@\n\r'q\u0019\t\u0007M&\u0014\tM!2\u0011\u00071\u0014\u0019\r\u0002\u0006o\u0005g\u000b\t\u0011!A\u0003\u0002Y\u00042\u0001\u001cBd\t)y(1WA\u0001\u0002\u0003\u0015\tA\u001e\u0005\u000b\u0005\u0017\u00149&%A\u0005\u0002\u0005\u0015\u0018aD1qa2LH\u0005Z3gCVdG\u000f\n\u001c\t\u0015\t='qKI\u0001\n\u0003\u0011\t.A\bbaBd\u0017\u0010\n3fM\u0006,H\u000e\u001e\u00138+\t\u0011\u0019N\u000b\u0003\u0003V\u0006]\u0006\u0003B'V\u0005/\u0004dA!7\u0003^\n\u0005\bC\u00024j\u00057\u0014y\u000eE\u0002m\u0005;$!B\u001cBg\u0003\u0003\u0005\tQ!\u0001w!\ra'\u0011\u001d\u0003\u000b\u007f\n5\u0017\u0011!A\u0001\u0006\u00031\bB\u0003Bs\u0005/\n\t\u0011\"\u0003\u0003h\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\u0011I\u000fE\u00022\u0005WL1A!<3\u0005\u0019y%M[3di\u0002"
)
public class ExceptionFailure implements TaskFailedReason, Product, Serializable {
   private final String className;
   private final String description;
   private final StackTraceElement[] stackTrace;
   private final String fullStackTrace;
   private final Option<ThrowableSerializationWrapper> org$apache$spark$ExceptionFailure$$exceptionWrapper;
   private final Seq<AccumulableInfo> accumUpdates;
   private Seq<AccumulatorV2<?, ?>> accums;

   public static Seq<AccumulatorV2<?, ?>> apply$default$7() {
      return ExceptionFailure$.MODULE$.apply$default$7();
   }

   public static Seq<AccumulableInfo> apply$default$6() {
      return ExceptionFailure$.MODULE$.apply$default$6();
   }

   public static Seq<AccumulatorV2<?, ?>> $lessinit$greater$default$7() {
      return ExceptionFailure$.MODULE$.$lessinit$greater$default$7();
   }

   public static Seq<AccumulableInfo> $lessinit$greater$default$6() {
      return ExceptionFailure$.MODULE$.$lessinit$greater$default$6();
   }

   public static Option<Tuple7<String, String, StackTraceElement[], String, Option<ThrowableSerializationWrapper>, Seq<AccumulableInfo>, Seq<AccumulatorV2<?, ?>>>> unapply(ExceptionFailure var0) {
      return ExceptionFailure$.MODULE$.unapply(var0);
   }

   public static ExceptionFailure apply(String var0, String var1, StackTraceElement[] var2, String var3, Option<ThrowableSerializationWrapper> var4, Seq<AccumulableInfo> var5, Seq<AccumulatorV2<?, ?>> var6) {
      return ExceptionFailure$.MODULE$.apply(var0, var1, var2, var3, var4, var5, var6);
   }

   public static Function1<Tuple7<String, String, StackTraceElement[], String, Option<ThrowableSerializationWrapper>, Seq<AccumulableInfo>, Seq<AccumulatorV2<?, ?>>>, ExceptionFailure> tupled() {
      return ExceptionFailure$.MODULE$.tupled();
   }

   public static Function1<String, Function1<String, Function1<StackTraceElement[], Function1<String, Function1<Option<ThrowableSerializationWrapper>, Function1<Seq<AccumulableInfo>, Function1<Seq<AccumulatorV2<?, ?>>, ExceptionFailure>>>>>>> curried() {
      return ExceptionFailure$.MODULE$.curried();
   }

   public boolean countTowardsTaskFailures() {
      return TaskFailedReason$class.countTowardsTaskFailures(this);
   }

   public Option<ThrowableSerializationWrapper> exceptionWrapper$1() {
      return this.org$apache$spark$ExceptionFailure$$exceptionWrapper;
   }

   public Seq<AccumulatorV2<?, ?>> accums$1() {
      return this.accums;
   }

   public String className() {
      return this.className;
   }

   public String description() {
      return this.description;
   }

   public StackTraceElement[] stackTrace() {
      return this.stackTrace;
   }

   public String fullStackTrace() {
      return this.fullStackTrace;
   }

   public Option<ThrowableSerializationWrapper> org$apache$spark$ExceptionFailure$$exceptionWrapper() {
      return this.org$apache$spark$ExceptionFailure$$exceptionWrapper;
   }

   public Seq<AccumulableInfo> accumUpdates() {
      return this.accumUpdates;
   }

   public Seq<AccumulatorV2<?, ?>> accums() {
      return this.accums;
   }

   public void accums_$eq(Seq<AccumulatorV2<?, ?>> x$1) {
      this.accums = x$1;
   }

   public ExceptionFailure withAccums(Seq<AccumulatorV2<?, ?>> accums) {
      this.accums_$eq(accums);
      return this;
   }

   public Option<Throwable> exception() {
      return this.org$apache$spark$ExceptionFailure$$exceptionWrapper().<Throwable>flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Option<Throwable> apply(ThrowableSerializationWrapper w) {
            return Option$.MODULE$.<Throwable>apply(w.exception());
         }
      });
   }

   public String toErrorString() {
      return this.fullStackTrace() == null ? this.exceptionString(this.className(), this.description(), this.stackTrace()) : this.fullStackTrace();
   }

   private String exceptionString(String className, String description, StackTraceElement[] stackTrace) {
      String desc = description == null ? "" : description;
      String st = stackTrace == null ? "" : Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])stackTrace).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(StackTraceElement x$1) {
            return (new StringBuilder()).append((Object)"        ").append((Object)x$1).toString();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).mkString("\n");
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": ", "\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{className, desc, st}));
   }

   public ExceptionFailure copy(String className, String description, StackTraceElement[] stackTrace, String fullStackTrace, Option<ThrowableSerializationWrapper> exceptionWrapper, Seq<AccumulableInfo> accumUpdates, Seq<AccumulatorV2<?, ?>> accums) {
      return new ExceptionFailure(className, description, stackTrace, fullStackTrace, exceptionWrapper, accumUpdates, accums);
   }

   public String copy$default$1() {
      return this.className();
   }

   public String copy$default$2() {
      return this.description();
   }

   public StackTraceElement[] copy$default$3() {
      return this.stackTrace();
   }

   public String copy$default$4() {
      return this.fullStackTrace();
   }

   public Option<ThrowableSerializationWrapper> copy$default$5() {
      return this.org$apache$spark$ExceptionFailure$$exceptionWrapper();
   }

   public Seq<AccumulableInfo> copy$default$6() {
      return this.accumUpdates();
   }

   public Seq<AccumulatorV2<?, ?>> copy$default$7() {
      return this.accums();
   }

   public String productPrefix() {
      return "ExceptionFailure";
   }

   public int productArity() {
      return 7;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.className();
            break;
         case 1:
            var10000 = this.description();
            break;
         case 2:
            var10000 = this.stackTrace();
            break;
         case 3:
            var10000 = this.fullStackTrace();
            break;
         case 4:
            var10000 = this.exceptionWrapper$1();
            break;
         case 5:
            var10000 = this.accumUpdates();
            break;
         case 6:
            var10000 = this.accums$1();
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
      return x$1 instanceof ExceptionFailure;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var17;
      if (this != x$1) {
         label101: {
            boolean var3;
            if (x$1 instanceof ExceptionFailure) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label82: {
                  label91: {
                     ExceptionFailure var4 = (ExceptionFailure)x$1;
                     String var10000 = this.className();
                     String var5 = var4.className();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label91;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label91;
                     }

                     var10000 = this.description();
                     String var6 = var4.description();
                     if (var10000 == null) {
                        if (var6 != null) {
                           break label91;
                        }
                     } else if (!var10000.equals(var6)) {
                        break label91;
                     }

                     if (this.stackTrace() == var4.stackTrace()) {
                        label92: {
                           var10000 = this.fullStackTrace();
                           String var7 = var4.fullStackTrace();
                           if (var10000 == null) {
                              if (var7 != null) {
                                 break label92;
                              }
                           } else if (!var10000.equals(var7)) {
                              break label92;
                           }

                           Option var13 = this.exceptionWrapper$1();
                           Option var8 = var4.exceptionWrapper$1();
                           if (var13 == null) {
                              if (var8 != null) {
                                 break label92;
                              }
                           } else if (!var13.equals(var8)) {
                              break label92;
                           }

                           Seq var14 = this.accumUpdates();
                           Seq var9 = var4.accumUpdates();
                           if (var14 == null) {
                              if (var9 != null) {
                                 break label92;
                              }
                           } else if (!var14.equals(var9)) {
                              break label92;
                           }

                           var14 = this.accums$1();
                           Seq var10 = var4.accums$1();
                           if (var14 == null) {
                              if (var10 != null) {
                                 break label92;
                              }
                           } else if (!var14.equals(var10)) {
                              break label92;
                           }

                           if (var4.canEqual(this)) {
                              var17 = true;
                              break label82;
                           }
                        }
                     }
                  }

                  var17 = false;
               }

               if (var17) {
                  break label101;
               }
            }

            var17 = false;
            return var17;
         }
      }

      var17 = true;
      return var17;
   }

   public ExceptionFailure(String className, String description, StackTraceElement[] stackTrace, String fullStackTrace, Option<ThrowableSerializationWrapper> exceptionWrapper, Seq<AccumulableInfo> accumUpdates, Seq<AccumulatorV2<?, ?>> accums) {
      this.className = className;
      this.description = description;
      this.stackTrace = stackTrace;
      this.fullStackTrace = fullStackTrace;
      this.org$apache$spark$ExceptionFailure$$exceptionWrapper = exceptionWrapper;
      this.accumUpdates = accumUpdates;
      this.accums = accums;
      super();
      TaskFailedReason$class.$init$(this);
      Product$class.$init$(this);
   }

   public ExceptionFailure(Throwable e, Seq<AccumulableInfo> accumUpdates, boolean preserveCause) {
      this(e.getClass().getName(), e.getMessage(), e.getStackTrace(), Utils$.MODULE$.exceptionString(e), (Option)(preserveCause ? new Some(new ThrowableSerializationWrapper(e)) : None$.MODULE$), accumUpdates, ExceptionFailure$.MODULE$.$lessinit$greater$default$7());
   }

   public ExceptionFailure(Throwable e, Seq<AccumulableInfo> accumUpdates) {
      this(e, accumUpdates, true);
   }
}
