package org.apache.spark.scheduler;

import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.spark.MapOutputTrackerMaster;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.TaskFailedReason;
import org.apache.spark.TaskNotSerializableException;
import org.apache.spark.TaskState$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Enumeration;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple4;
import scala.collection.IndexedSeq;
import scala.collection.IndexedSeq$;
import scala.collection.Iterable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Set;
import scala.collection.SetLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.HashSet$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BooleanRef;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.NonLocalReturnControl$mcZ$sp;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.runtime.TraitSetter;
import scala.util.Random$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0015\rc!B\u0001\u0003\u0001\u0011Q!!\u0005+bg.\u001c6\r[3ek2,'/S7qY*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0005\u0001-\tR\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Mi\u0011AA\u0005\u0003)\t\u0011Q\u0002V1tWN\u001b\u0007.\u001a3vY\u0016\u0014\bC\u0001\f\u001a\u001b\u00059\"B\u0001\r\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u000e\u0018\u0005\u001daunZ4j]\u001eD\u0001\u0002\b\u0001\u0003\u0006\u0004%\tAH\u0001\u0003g\u000e\u001c\u0001!F\u0001 !\t\u0001\u0013%D\u0001\u0005\u0013\t\u0011CA\u0001\u0007Ta\u0006\u00148nQ8oi\u0016DH\u000f\u0003\u0005%\u0001\t\u0005\t\u0015!\u0003 \u0003\r\u00198\r\t\u0005\tM\u0001\u0011)\u0019!C\u0001O\u0005yQ.\u0019=UCN\\g)Y5mkJ,7/F\u0001)!\ta\u0011&\u0003\u0002+\u001b\t\u0019\u0011J\u001c;\t\u00111\u0002!\u0011!Q\u0001\n!\n\u0001#\\1y)\u0006\u001c8NR1jYV\u0014Xm\u001d\u0011\t\u00119\u0002!\u0011!Q\u0001\n=\nq![:M_\u000e\fG\u000e\u0005\u0002\ra%\u0011\u0011'\u0004\u0002\b\u0005>|G.Z1o\u0011\u0015\u0019\u0004\u0001\"\u00015\u0003\u0019a\u0014N\\5u}Q!QGN\u001c9!\t\u0011\u0002\u0001C\u0003\u001de\u0001\u0007q\u0004C\u0003'e\u0001\u0007\u0001\u0006C\u0004/eA\u0005\t\u0019A\u0018\t\u000bM\u0002A\u0011\u0001\u001e\u0015\u0005UZ\u0004\"\u0002\u000f:\u0001\u0004y\u0002\"C\u001f\u0001\u0011\u000b\u0007I\u0011\u0001\u0002?\u0003M\u0011G.Y2lY&\u001cH\u000f\u0016:bG.,'o\u00149u+\u0005y\u0004c\u0001\u0007A\u0005&\u0011\u0011)\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005I\u0019\u0015B\u0001#\u0003\u0005A\u0011E.Y2lY&\u001cH\u000f\u0016:bG.,'\u000f\u0003\u0005G\u0001!\u0005\t\u0015)\u0003@\u0003Q\u0011G.Y2lY&\u001cH\u000f\u0016:bG.,'o\u00149uA!9\u0001\n\u0001b\u0001\n\u0003I\u0015\u0001B2p]\u001a,\u0012A\u0013\t\u0003A-K!\u0001\u0014\u0003\u0003\u0013M\u0003\u0018M]6D_:4\u0007B\u0002(\u0001A\u0003%!*A\u0003d_:4\u0007\u0005C\u0004Q\u0001\t\u0007I\u0011A)\u0002/M\u0003ViQ+M\u0003RKuJT0J\u001dR+%KV!M?6\u001bV#\u0001*\u0011\u00051\u0019\u0016B\u0001+\u000e\u0005\u0011auN\\4\t\rY\u0003\u0001\u0015!\u0003S\u0003a\u0019\u0006+R\"V\u0019\u0006#\u0016j\u0014(`\u0013:#VI\u0015,B\u0019~k5\u000b\t\u0005\b1\u0002\u0011\r\u0011\"\u0001(\u0003]i\u0015JT0U\u00136+u\fV(`'B+5)\u0016'B)&{e\n\u0003\u0004[\u0001\u0001\u0006I\u0001K\u0001\u0019\u001b&su\fV%N\u000b~#vjX*Q\u000b\u000e+F*\u0011+J\u001f:\u0003\u0003b\u0002/\u0001\u0005\u0004%I!X\u0001\u0015gB,7-\u001e7bi&|gnU2iK\u0012,H.\u001a:\u0016\u0003y\u0003\"a\u00184\u000e\u0003\u0001T!!\u00192\u0002\u0015\r|gnY;se\u0016tGO\u0003\u0002dI\u0006!Q\u000f^5m\u0015\u0005)\u0017\u0001\u00026bm\u0006L!a\u001a1\u00031M\u001b\u0007.\u001a3vY\u0016$W\t_3dkR|'oU3sm&\u001cW\r\u0003\u0004j\u0001\u0001\u0006IAX\u0001\u0016gB,7-\u001e7bi&|gnU2iK\u0012,H.\u001a:!\u0011\u001dY\u0007A1A\u0005\u0002E\u000bQc\u0015+B%Z\u000bE+S(O?RKU*R(V)~k5\u000b\u0003\u0004n\u0001\u0001\u0006IAU\u0001\u0017'R\u000b%KV!U\u0013>su\fV%N\u000b>+FkX'TA!9q\u000e\u0001b\u0001\n\u00039\u0013!D\"Q+N{\u0006+\u0012*`)\u0006\u001b6\n\u0003\u0004r\u0001\u0001\u0006I\u0001K\u0001\u000f\u0007B+6k\u0018)F%~#\u0016iU&!\u0011\u001d\u0019\bA1A\u0005\nQ\f1\u0004^1tWN+Go\u001d\"z'R\fw-Z%e\u0003:$\u0017\t\u001e;f[B$X#A;\u0011\tY\\\b&`\u0007\u0002o*\u0011\u00010_\u0001\b[V$\u0018M\u00197f\u0015\tQX\"\u0001\u0006d_2dWm\u0019;j_:L!\u0001`<\u0003\u000f!\u000b7\u000f['baB!ao\u001f\u0015\u007f!\t\u0011r0C\u0002\u0002\u0002\t\u0011a\u0002V1tWN+G/T1oC\u001e,'\u000fC\u0004\u0002\u0006\u0001\u0001\u000b\u0011B;\u00029Q\f7o[*fiN\u0014\u0015p\u0015;bO\u0016LE-\u00118e\u0003R$X-\u001c9uA!Q\u0011\u0011\u0002\u0001C\u0002\u0013\u0005!!a\u0003\u0002-Q\f7o[%e)>$\u0016m]6TKRl\u0015M\\1hKJ,\"!!\u0004\u0011\tY\\(K \u0005\t\u0003#\u0001\u0001\u0015!\u0003\u0002\u000e\u00059B/Y:l\u0013\u0012$v\u000eV1tWN+G/T1oC\u001e,'\u000f\t\u0005\n\u0003+\u0001!\u0019!C\u0001\u0003/\t!\u0003^1tW&#Gk\\#yK\u000e,Ho\u001c:JIV\u0011\u0011\u0011\u0004\t\u0006mn\u0014\u00161\u0004\t\u0005\u0003;\t\u0019CD\u0002\r\u0003?I1!!\t\u000e\u0003\u0019\u0001&/\u001a3fM&!\u0011QEA\u0014\u0005\u0019\u0019FO]5oO*\u0019\u0011\u0011E\u0007\t\u0011\u0005-\u0002\u0001)A\u0005\u00033\t1\u0003^1tW&#Gk\\#yK\u000e,Ho\u001c:JI\u0002B\u0011\"a\f\u0001\u0001\u0004%I!!\r\u0002\u001f!\f7OU3dK&4X\r\u001a+bg.,\u0012a\f\u0005\n\u0003k\u0001\u0001\u0019!C\u0005\u0003o\t1\u0003[1t%\u0016\u001cW-\u001b<fIR\u000b7o[0%KF$B!!\u000f\u0002@A\u0019A\"a\u000f\n\u0007\u0005uRB\u0001\u0003V]&$\b\"CA!\u0003g\t\t\u00111\u00010\u0003\rAH%\r\u0005\b\u0003\u000b\u0002\u0001\u0015)\u00030\u0003AA\u0017m\u001d*fG\u0016Lg/\u001a3UCN\\\u0007\u0005\u000b\u0003\u0002D\u0005%\u0003c\u0001\u0007\u0002L%\u0019\u0011QJ\u0007\u0003\u0011Y|G.\u0019;jY\u0016D\u0011\"!\u0015\u0001\u0001\u0004%I!!\r\u0002\u001f!\f7\u000fT1v]\u000eDW\r\u001a+bg.D\u0011\"!\u0016\u0001\u0001\u0004%I!a\u0016\u0002'!\f7\u000fT1v]\u000eDW\r\u001a+bg.|F%Z9\u0015\t\u0005e\u0012\u0011\f\u0005\n\u0003\u0003\n\u0019&!AA\u0002=Bq!!\u0018\u0001A\u0003&q&\u0001\tiCNd\u0015-\u001e8dQ\u0016$G+Y:lA!\"\u00111LA%\u0011%\t\u0019\u0007\u0001b\u0001\n\u0013\t)'A\bti\u0006\u0014h/\u0019;j_:$\u0016.\\3s+\t\t9\u0007\u0005\u0003\u0002j\u0005-T\"\u00012\n\u0007\u00055$MA\u0003US6,'\u000f\u0003\u0005\u0002r\u0001\u0001\u000b\u0011BA4\u0003A\u0019H/\u0019:wCRLwN\u001c+j[\u0016\u0014\b\u0005C\u0005\u0002v\u0001\u0011\r\u0011\"\u0001\u0002x\u0005Qa.\u001a=u)\u0006\u001c8.\u00133\u0016\u0005\u0005e\u0004\u0003BA>\u0003\u0003k!!! \u000b\u0007\u0005}\u0004-\u0001\u0004bi>l\u0017nY\u0005\u0005\u0003\u0007\u000biH\u0001\u0006Bi>l\u0017n\u0019'p]\u001eD\u0001\"a\"\u0001A\u0003%\u0011\u0011P\u0001\f]\u0016DH\u000fV1tW&#\u0007\u0005C\u0005\u0002\f\u0002\u0011\r\u0011\"\u0003\u0002\u000e\u0006QR\r_3dkR|'/\u00133U_J+hN\\5oOR\u000b7o[%egV\u0011\u0011q\u0012\t\u0007mn\fY\"!%\u0011\tY\f\u0019JU\u0005\u0004\u0003+;(a\u0002%bg\"\u001cV\r\u001e\u0005\t\u00033\u0003\u0001\u0015!\u0003\u0002\u0010\u0006YR\r_3dkR|'/\u00133U_J+hN\\5oOR\u000b7o[%eg\u0002Bq!!(\u0001\t\u0003\ty*A\fsk:t\u0017N\\4UCN\\7OQ=Fq\u0016\u001cW\u000f^8sgV\u0011\u0011\u0011\u0015\t\b\u0003;\t\u0019+a\u0007)\u0013\u0011\t)+a\n\u0003\u00075\u000b\u0007\u000fC\u0005\u0002*\u0002\u0011\r\u0011\"\u0005\u0002,\u0006y\u0001n\\:u)>,\u00050Z2vi>\u00148/\u0006\u0002\u0002.B1ao_A\u000e\u0003_\u0003RA^AJ\u00037A\u0001\"a-\u0001A\u0003%\u0011QV\u0001\u0011Q>\u001cH\u000fV8Fq\u0016\u001cW\u000f^8sg\u0002B\u0011\"a.\u0001\u0005\u0004%\t\"a+\u0002\u0017!|7\u000f^:CsJ\u000b7m\u001b\u0005\t\u0003w\u0003\u0001\u0015!\u0003\u0002.\u0006a\u0001n\\:ug\nK(+Y2lA!I\u0011q\u0018\u0001C\u0002\u0013E\u0011\u0011Y\u0001\u0011Kb,7-\u001e;pe&#Gk\u001c%pgR,\"!a1\u0011\rY\\\u00181DA\u000e\u0011!\t9\r\u0001Q\u0001\n\u0005\r\u0017!E3yK\u000e,Ho\u001c:JIR{\u0007j\\:uA!I\u00111\u001a\u0001A\u0002\u0013\u0005\u0011QZ\u0001\rI\u0006<7k\u00195fIVdWM]\u000b\u0003\u0003\u001f\u00042AEAi\u0013\r\t\u0019N\u0001\u0002\r\t\u0006;5k\u00195fIVdWM\u001d\u0005\n\u0003/\u0004\u0001\u0019!C\u0001\u00033\f\u0001\u0003Z1h'\u000eDW\rZ;mKJ|F%Z9\u0015\t\u0005e\u00121\u001c\u0005\u000b\u0003\u0003\n).!AA\u0002\u0005=\u0007\u0002CAp\u0001\u0001\u0006K!a4\u0002\u001b\u0011\fwmU2iK\u0012,H.\u001a:!\u0011%\t\u0019\u000f\u0001a\u0001\n\u0003\t)/A\u0004cC\u000e\\WM\u001c3\u0016\u0005\u0005\u001d\bc\u0001\n\u0002j&\u0019\u00111\u001e\u0002\u0003!M\u001b\u0007.\u001a3vY\u0016\u0014()Y2lK:$\u0007\"CAx\u0001\u0001\u0007I\u0011AAy\u0003-\u0011\u0017mY6f]\u0012|F%Z9\u0015\t\u0005e\u00121\u001f\u0005\u000b\u0003\u0003\ni/!AA\u0002\u0005\u001d\b\u0002CA|\u0001\u0001\u0006K!a:\u0002\u0011\t\f7m[3oI\u0002B\u0011\"a?\u0001\u0005\u0004%\t!!@\u0002!5\f\u0007oT;uaV$HK]1dW\u0016\u0014XCAA\u0000!\r\u0001#\u0011A\u0005\u0004\u0005\u0007!!AF'ba>+H\u000f];u)J\f7m[3s\u001b\u0006\u001cH/\u001a:\t\u0011\t\u001d\u0001\u0001)A\u0005\u0003\u007f\f\u0011#\\1q\u001fV$\b/\u001e;Ue\u0006\u001c7.\u001a:!\u0011%\u0011Y\u0001\u0001a\u0001\n\u0013\u0011i!\u0001\ntG\",G-\u001e7bE2,')^5mI\u0016\u0014XC\u0001B\b!\r\u0011\"\u0011C\u0005\u0004\u0005'\u0011!AE*dQ\u0016$W\u000f\\1cY\u0016\u0014U/\u001b7eKJD\u0011Ba\u0006\u0001\u0001\u0004%IA!\u0007\u0002-M\u001c\u0007.\u001a3vY\u0006\u0014G.\u001a\"vS2$WM]0%KF$B!!\u000f\u0003\u001c!Q\u0011\u0011\tB\u000b\u0003\u0003\u0005\rAa\u0004\t\u0011\t}\u0001\u0001)Q\u0005\u0005\u001f\t1c]2iK\u0012,H.\u00192mK\n+\u0018\u000e\u001c3fe\u0002B\u0011Ba\t\u0001\u0005\u0004%IA!\n\u0002%M\u001c\u0007.\u001a3vY&tw-T8eK\u000e{gNZ\u000b\u0003\u00037A\u0001B!\u000b\u0001A\u0003%\u00111D\u0001\u0014g\u000eDW\rZ;mS:<Wj\u001c3f\u0007>tg\r\t\u0005\n\u0005[\u0001!\u0019!C\u0001\u0005_\tab]2iK\u0012,H.\u001b8h\u001b>$W-\u0006\u0002\u00032A!!1\u0007B(\u001d\u0011\u0011)Da\u0013\u000f\t\t]\"\u0011\n\b\u0005\u0005s\u00119E\u0004\u0003\u0003<\t\u0015c\u0002\u0002B\u001f\u0005\u0007j!Aa\u0010\u000b\u0007\t\u0005S$\u0001\u0004=e>|GOP\u0005\u0002\u0013%\u0011q\u0001C\u0005\u0003\u000b\u0019I!a\u0001\u0003\n\u0007\t5#!\u0001\bTG\",G-\u001e7j]\u001elu\u000eZ3\n\t\tE#1\u000b\u0002\u000f'\u000eDW\rZ;mS:<Wj\u001c3f\u0015\r\u0011iE\u0001\u0005\t\u0005/\u0002\u0001\u0015!\u0003\u00032\u0005y1o\u00195fIVd\u0017N\\4N_\u0012,\u0007\u0005C\u0005\u0003\\\u0001\u0011\r\u0011\"\u0001\u0003^\u0005A!o\\8u!>|G.\u0006\u0002\u0003`A\u0019!C!\u0019\n\u0007\t\r$A\u0001\u0003Q_>d\u0007\u0002\u0003B4\u0001\u0001\u0006IAa\u0018\u0002\u0013I|w\u000e\u001e)p_2\u0004\u0003B\u0003B6\u0001\u0001\u0007I\u0011\u0001\u0003\u0003n\u0005\u0001B/Y:l%\u0016\u001cX\u000f\u001c;HKR$XM]\u000b\u0003\u0005_\u00022A\u0005B9\u0013\r\u0011\u0019H\u0001\u0002\u0011)\u0006\u001c8NU3tk2$x)\u001a;uKJD!Ba\u001e\u0001\u0001\u0004%\t\u0001\u0002B=\u0003Q!\u0018m]6SKN,H\u000e^$fiR,'o\u0018\u0013fcR!\u0011\u0011\bB>\u0011)\t\tE!\u001e\u0002\u0002\u0003\u0007!q\u000e\u0005\t\u0005\u007f\u0002\u0001\u0015)\u0003\u0003p\u0005\tB/Y:l%\u0016\u001cX\u000f\u001c;HKR$XM\u001d\u0011\t\u000f\t\r\u0005\u0001\"\u0011\u0003\u0006\u0006y1/\u001a;E\u0003\u001e\u001b6\r[3ek2,'\u000f\u0006\u0003\u0002:\t\u001d\u0005\u0002CAf\u0005\u0003\u0003\r!a4\t\u000f\t-\u0005\u0001\"\u0001\u0003\u000e\u0006Q\u0011N\\5uS\u0006d\u0017N_3\u0015\t\u0005e\"q\u0012\u0005\t\u0003G\u0014I\t1\u0001\u0002h\"9!1\u0013\u0001\u0005\u0002\tU\u0015!\u00038foR\u000b7o[%e)\u0005\u0011\u0006b\u0002BM\u0001\u0011\u0005#1T\u0001\u0006gR\f'\u000f\u001e\u000b\u0003\u0003sAqAa(\u0001\t\u0003\u0012Y*A\u0007q_N$8\u000b^1si\"{wn\u001b\u0005\b\u0005G\u0003A\u0011\tBS\u0003-\u0019XOY7jiR\u000b7o[:\u0015\t\u0005e\"q\u0015\u0005\t\u0005S\u0013\t\u000b1\u0001\u0003,\u00069A/Y:l'\u0016$\bc\u0001\n\u0003.&\u0019!q\u0016\u0002\u0003\u000fQ\u000b7o[*fi\"A!1\u0017\u0001\u0005\u0002\t\u0011),\u0001\u000bde\u0016\fG/\u001a+bg.\u001cV\r^'b]\u0006<WM\u001d\u000b\u0006}\n]&\u0011\u0018\u0005\t\u0005S\u0013\t\f1\u0001\u0003,\"1aE!-A\u0002!BqA!0\u0001\t\u0003\u0012y,A\u0006dC:\u001cW\r\u001c+bg.\u001cHCBA\u001d\u0005\u0003\u0014)\rC\u0004\u0003D\nm\u0006\u0019\u0001\u0015\u0002\u000fM$\u0018mZ3JI\"9!q\u0019B^\u0001\u0004y\u0013aD5oi\u0016\u0014(/\u001e9u)\"\u0014X-\u00193\t\u000f\t-\u0007\u0001\"\u0011\u0003N\u0006y1.\u001b7m)\u0006\u001c8.\u0011;uK6\u0004H\u000fF\u00040\u0005\u001f\u0014\u0019N!6\t\u000f\tE'\u0011\u001aa\u0001%\u00061A/Y:l\u0013\u0012DqAa2\u0003J\u0002\u0007q\u0006\u0003\u0005\u0003X\n%\u0007\u0019AA\u000e\u0003\u0019\u0011X-Y:p]\"9!1\u001c\u0001\u0005\u0002\tu\u0017a\u0004;bg.\u001cV\r\u001e$j]&\u001c\b.\u001a3\u0015\t\u0005e\"q\u001c\u0005\b\u0005C\u0014I\u000e1\u0001\u007f\u0003\u001di\u0017M\\1hKJDqA!:\u0001\t\u0013\u00119/\u0001\u000esKN|WO]2f\u001f\u001a4WM]*j]\u001edW\rV1tWN+G\u000fF\u00060\u0005S\u0014YO!@\u0004\u001a\r\r\u0002b\u0002BU\u0005G\u0004\rA \u0005\t\u0005[\u0014\u0019\u000f1\u0001\u0003p\u0006YQ.\u0019=M_\u000e\fG.\u001b;z!\u0011\u0011\tPa>\u000f\t\tU\"1_\u0005\u0004\u0005k\u0014\u0011\u0001\u0004+bg.dunY1mSRL\u0018\u0002\u0002B}\u0005w\u0014A\u0002V1tW2{7-\u00197jifT1A!>\u0003\u0011!\u0011yPa9A\u0002\r\u0005\u0011AD:ik\u001a4G.\u001a3PM\u001a,'o\u001d\t\u0007\u0007\u0007\u0019iaa\u0005\u000f\t\r\u00151\u0011\u0002\b\u0005\u0005{\u00199!C\u0001\u000f\u0013\r\u0019Y!D\u0001\ba\u0006\u001c7.Y4f\u0013\u0011\u0019ya!\u0005\u0003\u0007M+\u0017OC\u0002\u0004\f5\u00012AEB\u000b\u0013\r\u00199B\u0001\u0002\f/>\u00148.\u001a:PM\u001a,'\u000f\u0003\u0005\u0004\u001c\t\r\b\u0019AB\u000f\u00035\tg/Y5mC\ndWm\u00119vgB!Aba\b)\u0013\r\u0019\t#\u0004\u0002\u0006\u0003J\u0014\u0018-\u001f\u0005\t\u0007K\u0011\u0019\u000f1\u0001\u0004(\u0005)A/Y:lgB111AB\u0015\u0007[IAaa\u000b\u0004\u0012\tQ\u0011J\u001c3fq\u0016$7+Z9\u0011\u000bY\u001cyca\r\n\u0007\rErOA\u0006BeJ\f\u0017PQ;gM\u0016\u0014\bc\u0001\n\u00046%\u00191q\u0007\u0002\u0003\u001fQ\u000b7o\u001b#fg\u000e\u0014\u0018\u000e\u001d;j_:Dqaa\u000f\u0001\t\u0003\u0019i$\u0001\bsKN|WO]2f\u001f\u001a4WM]:\u0015\t\r}21\t\t\u0007\u0007\u0007\u0019ia!\u0011\u0011\r\r\r1QBB\u001a\u0011!\u0019)e!\u000fA\u0002\r\u001d\u0013AB8gM\u0016\u00148\u000f\u0005\u0004\u0004\u0004\r%21\u0003\u0005\b\u0007\u0017\u0002A\u0011CB'\u00035\u0019\b.\u001e4gY\u0016|eMZ3sgR!1qIB(\u0011!\u0019)e!\u0013A\u0002\r\u001d\u0003bBB*\u0001\u0011\u00051QK\u0001\rgR\fG/^:Va\u0012\fG/\u001a\u000b\t\u0003s\u00199fa\u0017\u0004n!91\u0011LB)\u0001\u0004\u0011\u0016a\u0001;jI\"A1QLB)\u0001\u0004\u0019y&A\u0003ti\u0006$X\r\u0005\u0003\u0004b\r\u001dd\u0002\u0002B\u001c\u0007GJ1a!\u001a\u0005\u0003%!\u0016m]6Ti\u0006$X-\u0003\u0003\u0004j\r-$!\u0003+bg.\u001cF/\u0019;f\u0015\r\u0019)\u0007\u0002\u0005\t\u0007_\u001a\t\u00061\u0001\u0004r\u0005q1/\u001a:jC2L'0\u001a3ECR\f\u0007\u0003BB:\u0007sj!a!\u001e\u000b\u0007\r]D-A\u0002oS>LAaa\u001f\u0004v\tQ!)\u001f;f\u0005V4g-\u001a:\t\u000f\r}\u0004\u0001\"\u0011\u0004\u0002\u0006IR\r_3dkR|'\u000fS3beR\u0014W-\u0019;SK\u000e,\u0017N^3e)\u001dy31QBD\u0007{C\u0001b!\"\u0004~\u0001\u0007\u00111D\u0001\u0007Kb,7-\u00133\t\u0011\r%5Q\u0010a\u0001\u0007\u0017\u000bA\"Y2dk6,\u0006\u000fZ1uKN\u0004R\u0001DB\u0010\u0007\u001b\u0003b\u0001DBH%\u000eM\u0015bABI\u001b\t1A+\u001e9mKJ\u0002baa\u0001\u0004\u000e\rU\u0005GBBL\u0007K\u001bI\f\u0005\u0005\u0004\u001a\u000eu5\u0011UB\\\u001b\t\u0019YJ\u0003\u0002d\t%!1qTBN\u00055\t5mY;nk2\fGo\u001c:WeA!11UBS\u0019\u0001!Aba*\u0004\b\u0006\u0005\t\u0011!B\u0001\u0007S\u00131a\u0018\u00132#\u0011\u0019Yk!-\u0011\u00071\u0019i+C\u0002\u000406\u0011qAT8uQ&tw\rE\u0002\r\u0007gK1a!.\u000e\u0005\r\te.\u001f\t\u0005\u0007G\u001bI\f\u0002\u0007\u0004<\u000e\u001d\u0015\u0011!A\u0001\u0006\u0003\u0019IKA\u0002`IIB\u0001ba0\u0004~\u0001\u00071\u0011Y\u0001\u000fE2|7m['b]\u0006<WM]%e!\u0011\u0019\u0019m!3\u000e\u0005\r\u0015'bABd\t\u000591\u000f^8sC\u001e,\u0017\u0002BBf\u0007\u000b\u0014aB\u00117pG.l\u0015M\\1hKJLE\rC\u0004\u0004P\u0002!\ta!5\u0002/!\fg\u000e\u001a7f)\u0006\u001c8nR3ui&twMU3tk2$HCBA\u001d\u0007'\u001c9\u000eC\u0004\u0004V\u000e5\u0007\u0019\u0001@\u0002\u001dQ\f7o[*fi6\u000bg.Y4fe\"91\u0011LBg\u0001\u0004\u0011\u0006bBBn\u0001\u0011\u00051Q\\\u0001\u0015Q\u0006tG\r\\3Tk\u000e\u001cWm]:gk2$\u0016m]6\u0015\u0011\u0005e2q\\Bq\u0007GDqa!6\u0004Z\u0002\u0007a\u0010C\u0004\u0004Z\re\u0007\u0019\u0001*\t\u0011\r\u00158\u0011\u001ca\u0001\u0007O\f!\u0002^1tWJ+7/\u001e7ua\u0011\u0019Io!=\u0011\u000bI\u0019Yoa<\n\u0007\r5(A\u0001\tESJ,7\r\u001e+bg.\u0014Vm];miB!11UBy\t1\u0019\u0019pa9\u0002\u0002\u0003\u0005)\u0011ABU\u0005\ryFe\r\u0005\b\u0007o\u0004A\u0011AB}\u0003AA\u0017M\u001c3mK\u001a\u000b\u0017\u000e\\3e)\u0006\u001c8\u000e\u0006\u0006\u0002:\rm8Q`B\u0000\t\u0007Aqa!6\u0004v\u0002\u0007a\u0010C\u0004\u0004Z\rU\b\u0019\u0001*\t\u0011\u0011\u00051Q\u001fa\u0001\u0007?\n\u0011\u0002^1tWN#\u0018\r^3\t\u0011\t]7Q\u001fa\u0001\t\u000b\u00012\u0001\tC\u0004\u0013\r!I\u0001\u0002\u0002\u0011)\u0006\u001c8NR1jY\u0016$'+Z1t_:Dq\u0001\"\u0004\u0001\t\u0003!y!A\u0003feJ|'\u000f\u0006\u0003\u0002:\u0011E\u0001\u0002\u0003C\n\t\u0017\u0001\r!a\u0007\u0002\u000f5,7o]1hK\"9Aq\u0003\u0001\u0005B\tm\u0015\u0001B:u_BDq\u0001b\u0007\u0001\t\u0003\"i\"\u0001\neK\u001a\fW\u000f\u001c;QCJ\fG\u000e\\3mSNlG#\u0001\u0015\t\u000f\u0011\u0005\u0002\u0001\"\u0001\u0003\u001c\u000612\r[3dWN\u0003XmY;mCR\f'\r\\3UCN\\7\u000fC\u0004\u0005&\u0001!\t\u0005b\n\u0002\u0019\u0015DXmY;u_Jdun\u001d;\u0015\r\u0005eB\u0011\u0006C\u0017\u0011!!Y\u0003b\tA\u0002\u0005m\u0011AC3yK\u000e,Ho\u001c:JI\"A!q\u001bC\u0012\u0001\u0004!y\u0003E\u0002\u0013\tcI1\u0001b\r\u0003\u0005I)\u00050Z2vi>\u0014Hj\\:t%\u0016\f7o\u001c8\t\u000f\u0011]\u0002\u0001\"\u0011\u0005:\u0005iqo\u001c:lKJ\u0014V-\\8wK\u0012$\u0002\"!\u000f\u0005<\u0011}B1\t\u0005\t\t{!)\u00041\u0001\u0002\u001c\u0005Aqo\u001c:lKJLE\r\u0003\u0005\u0005B\u0011U\u0002\u0019AA\u000e\u0003\u0011Awn\u001d;\t\u0011\u0011MAQ\u0007a\u0001\u00037Aq\u0001b\u0012\u0001\t\u0013!I%A\bm_\u001e,\u00050Z2vi>\u0014Hj\\:t)!\tI\u0004b\u0013\u0005N\u0011E\u0003\u0002\u0003C\u0016\t\u000b\u0002\r!a\u0007\t\u0011\u0011=CQ\ta\u0001\u00037\t\u0001\u0002[8tiB{'\u000f\u001e\u0005\t\u0005/$)\u00051\u0001\u00050!9AQ\u000b\u0001\u0005\n\u0011]\u0013\u0001E2mK\u0006tW\u000f\u001d+bg.\u001cF/\u0019;f)\u0011\tI\u0004\"\u0017\t\u000f\reC1\u000ba\u0001%\"9AQ\f\u0001\u0005\n\u0011}\u0013A\u0004:f[>4X-\u0012=fGV$xN\u001d\u000b\u0007\u0003s!\t\u0007b\u0019\t\u0011\u0011-B1\fa\u0001\u00037A\u0001Ba6\u0005\\\u0001\u0007Aq\u0006\u0005\b\tO\u0002A\u0011\u0001C5\u00035)\u00070Z2vi>\u0014\u0018\t\u001a3fIR1\u0011\u0011\bC6\t[B\u0001b!\"\u0005f\u0001\u0007\u00111\u0004\u0005\t\t\u0003\")\u00071\u0001\u0002\u001c!9A\u0011\u000f\u0001\u0005\u0002\u0011M\u0014aF4fi\u0016CXmY;u_J\u001c\u0018\t\\5wK>s\u0007j\\:u)\u0011!)\bb \u0011\t1\u0001Eq\u000f\t\u0007\ts\"Y(a\u0007\u000e\u0003eL1\u0001\" z\u0005\r\u0019V\r\u001e\u0005\t\t\u0003\"y\u00071\u0001\u0002\u001c!9A1\u0011\u0001\u0005\u0002\u0011\u0015\u0015a\u00065bg\u0016CXmY;u_J\u001c\u0018\t\\5wK>s\u0007j\\:u)\ryCq\u0011\u0005\t\t\u0003\"\t\t1\u0001\u0002\u001c!9A1\u0012\u0001\u0005\u0002\u00115\u0015A\u00055bg\"{7\u000f^!mSZ,wJ\u001c*bG.$2a\fCH\u0011!!\t\n\"#A\u0002\u0005m\u0011\u0001\u0002:bG.Dq\u0001\"&\u0001\t\u0003!9*A\bjg\u0016CXmY;u_J\fE.\u001b<f)\ryC\u0011\u0014\u0005\t\u0007\u000b#\u0019\n1\u0001\u0002\u001c!9AQ\u0014\u0001\u0005\u0002\u0011}\u0015AD5t\u000bb,7-\u001e;pe\n+8/\u001f\u000b\u0004_\u0011\u0005\u0006\u0002CBC\t7\u0003\r!a\u0007\t\u000f\u0011\u0015\u0006\u0001\"\u0001\u0005(\u0006ian\u001c3f\u00052\f7m\u001b7jgR$\"\u0001\"+\u0011\r\u0011-F\u0011WA\u000e\u001b\t!iKC\u0002\u00050f\f\u0011\"[7nkR\f'\r\\3\n\t\u0011uDQ\u0016\u0005\b\tk\u0003A\u0011\u0001C\\\u000399W\r\u001e*bG.4uN\u001d%pgR$B\u0001\"/\u0005<B!A\u0002QA\u000e\u0011!!i\fb-A\u0002\u0005m\u0011!\u0002<bYV,\u0007b\u0002Ca\u0001\u0011%!1T\u0001\u0011o\u0006LGOQ1dW\u0016tGMU3bIfDq\u0001\"2\u0001\t\u0003\"9-A\u0007baBd\u0017nY1uS>t\u0017\n\u001a\u000b\u0003\u00037Aq\u0001b3\u0001\t\u0003\"i-\u0001\u000bbaBd\u0017nY1uS>t\u0017\t\u001e;f[B$\u0018\n\u001a\u000b\u0003\tsC\u0001\u0002\"5\u0001\t\u0003\u0011A1[\u0001\u0019i\u0006\u001c8nU3u\u001b\u0006t\u0017mZ3s\r>\u0014\u0018\t\u001e;f[B$HC\u0002Ck\t/$I\u000eE\u0002\r\u0001zDqAa1\u0005P\u0002\u0007\u0001\u0006C\u0004\u0005\\\u0012=\u0007\u0019\u0001\u0015\u0002\u001dM$\u0018mZ3BiR,W\u000e\u001d;JI\u001eAAq\u001c\u0002\t\u0002\u0011!\t/A\tUCN\\7k\u00195fIVdWM]%na2\u00042A\u0005Cr\r\u001d\t!\u0001#\u0001\u0005\tK\u001c2\u0001b9\f\u0011\u001d\u0019D1\u001dC\u0001\tS$\"\u0001\"9\t\u0015\u00115H1\u001db\u0001\n\u0003!y/A\fT\u0007\"+E)\u0016'F%~ku\nR#`!J{\u0005+\u0012*U3V\u0011A\u0011\u001f\t\u0005\tg$I0\u0004\u0002\u0005v*\u0019Aq\u001f3\u0002\t1\fgnZ\u0005\u0005\u0003K!)\u0010C\u0005\u0005~\u0012\r\b\u0015!\u0003\u0005r\u0006A2k\u0011%F\tVcUIU0N\u001f\u0012+u\f\u0015*P!\u0016\u0013F+\u0017\u0011\t\u0011\u0015\u0005A1\u001dC\u0001\u000b\u0007\tA\u0003\u001d:j_JLG/\u001b>f\u0007>tG/Y5oKJ\u001cXCBC\u0003\u000b7)y\u0001\u0006\u0003\u0006\b\u0015M\u0001CBB\u0002\u000b\u0013)i!\u0003\u0003\u0006\f\rE!\u0001\u0002'jgR\u0004Baa)\u0006\u0010\u0011AQ\u0011\u0003C\u0000\u0005\u0004\u0019IKA\u0001U\u0011!))\u0002b@A\u0002\u0015]\u0011aA7baB1ao_C\r\u000b?\u0001Baa)\u0006\u001c\u0011AQQ\u0004C\u0000\u0005\u0004\u0019IKA\u0001L!\u001518qFC\u0007\u0011!)\u0019\u0003b9\u0005\n\u0015\u0015\u0012aG7bs\n,7I]3bi\u0016\u0014E.Y2lY&\u001cH\u000f\u0016:bG.,'\u000fF\u0002@\u000bOAa\u0001HC\u0011\u0001\u0004y\u0002BCC\u0016\tG\f\n\u0011\"\u0001\u0006.\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIM*\"!b\f+\u0007=*\td\u000b\u0002\u00064A!QQGC \u001b\t)9D\u0003\u0003\u0006:\u0015m\u0012!C;oG\",7m[3e\u0015\r)i$D\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002BC!\u000bo\u0011\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0001"
)
public class TaskSchedulerImpl implements TaskScheduler, Logging {
   private final SparkContext sc;
   private final int maxTaskFailures;
   private final boolean isLocal;
   private Option<BlacklistTracker> blacklistTrackerOpt;
   private final SparkConf conf;
   private final long SPECULATION_INTERVAL_MS;
   private final int MIN_TIME_TO_SPECULATION;
   private final ScheduledExecutorService speculationScheduler;
   private final long STARVATION_TIMEOUT_MS;
   private final int CPUS_PER_TASK;
   private final HashMap<Object, HashMap<Object, TaskSetManager>> org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt;
   private final HashMap<Object, TaskSetManager> taskIdToTaskSetManager;
   private final HashMap<Object, String> taskIdToExecutorId;
   private volatile boolean hasReceivedTask;
   private volatile boolean org$apache$spark$scheduler$TaskSchedulerImpl$$hasLaunchedTask;
   private final Timer starvationTimer;
   private final AtomicLong nextTaskId;
   private final HashMap<String, HashSet<Object>> org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds;
   private final HashMap<String, HashSet<String>> hostToExecutors;
   private final HashMap<String, HashSet<String>> hostsByRack;
   private final HashMap<String, String> executorIdToHost;
   private DAGScheduler dagScheduler;
   private SchedulerBackend backend;
   private final MapOutputTrackerMaster mapOutputTracker;
   private SchedulableBuilder schedulableBuilder;
   private final String schedulingModeConf;
   private final Enumeration.Value schedulingMode;
   private final Pool rootPool;
   private TaskResultGetter taskResultGetter;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private final String org$apache$spark$scheduler$TaskScheduler$$appId;
   private volatile boolean bitmap$0;

   public static boolean $lessinit$greater$default$3() {
      return TaskSchedulerImpl$.MODULE$.$lessinit$greater$default$3();
   }

   public static <K, T> List<T> prioritizeContainers(HashMap<K, ArrayBuffer<T>> var0) {
      return TaskSchedulerImpl$.MODULE$.prioritizeContainers(var0);
   }

   public static String SCHEDULER_MODE_PROPERTY() {
      return TaskSchedulerImpl$.MODULE$.SCHEDULER_MODE_PROPERTY();
   }

   private Option blacklistTrackerOpt$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.blacklistTrackerOpt = TaskSchedulerImpl$.MODULE$.org$apache$spark$scheduler$TaskSchedulerImpl$$maybeCreateBlacklistTracker(this.sc());
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.blacklistTrackerOpt;
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   public String org$apache$spark$scheduler$TaskScheduler$$appId() {
      return this.org$apache$spark$scheduler$TaskScheduler$$appId;
   }

   public void org$apache$spark$scheduler$TaskScheduler$_setter_$org$apache$spark$scheduler$TaskScheduler$$appId_$eq(String x$1) {
      this.org$apache$spark$scheduler$TaskScheduler$$appId = x$1;
   }

   public SparkContext sc() {
      return this.sc;
   }

   public int maxTaskFailures() {
      return this.maxTaskFailures;
   }

   public Option<BlacklistTracker> blacklistTrackerOpt() {
      return this.bitmap$0 ? this.blacklistTrackerOpt : this.blacklistTrackerOpt$lzycompute();
   }

   public SparkConf conf() {
      return this.conf;
   }

   public long SPECULATION_INTERVAL_MS() {
      return this.SPECULATION_INTERVAL_MS;
   }

   public int MIN_TIME_TO_SPECULATION() {
      return this.MIN_TIME_TO_SPECULATION;
   }

   private ScheduledExecutorService speculationScheduler() {
      return this.speculationScheduler;
   }

   public long STARVATION_TIMEOUT_MS() {
      return this.STARVATION_TIMEOUT_MS;
   }

   public int CPUS_PER_TASK() {
      return this.CPUS_PER_TASK;
   }

   public HashMap<Object, HashMap<Object, TaskSetManager>> org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt() {
      return this.org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt;
   }

   public HashMap<Object, TaskSetManager> taskIdToTaskSetManager() {
      return this.taskIdToTaskSetManager;
   }

   public HashMap<Object, String> taskIdToExecutorId() {
      return this.taskIdToExecutorId;
   }

   private boolean hasReceivedTask() {
      return this.hasReceivedTask;
   }

   private void hasReceivedTask_$eq(boolean x$1) {
      this.hasReceivedTask = x$1;
   }

   public boolean org$apache$spark$scheduler$TaskSchedulerImpl$$hasLaunchedTask() {
      return this.org$apache$spark$scheduler$TaskSchedulerImpl$$hasLaunchedTask;
   }

   private void org$apache$spark$scheduler$TaskSchedulerImpl$$hasLaunchedTask_$eq(boolean x$1) {
      this.org$apache$spark$scheduler$TaskSchedulerImpl$$hasLaunchedTask = x$1;
   }

   private Timer starvationTimer() {
      return this.starvationTimer;
   }

   public AtomicLong nextTaskId() {
      return this.nextTaskId;
   }

   public HashMap<String, HashSet<Object>> org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds() {
      return this.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds;
   }

   public synchronized Map<String, Object> runningTasksByExecutors() {
      return this.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().toMap(Predef$.MODULE$.$conforms()).mapValues(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(HashSet<Object> x$1) {
            return x$1.size();
         }
      });
   }

   public HashMap<String, HashSet<String>> hostToExecutors() {
      return this.hostToExecutors;
   }

   public HashMap<String, HashSet<String>> hostsByRack() {
      return this.hostsByRack;
   }

   public HashMap<String, String> executorIdToHost() {
      return this.executorIdToHost;
   }

   public DAGScheduler dagScheduler() {
      return this.dagScheduler;
   }

   public void dagScheduler_$eq(DAGScheduler x$1) {
      this.dagScheduler = x$1;
   }

   public SchedulerBackend backend() {
      return this.backend;
   }

   public void backend_$eq(SchedulerBackend x$1) {
      this.backend = x$1;
   }

   public MapOutputTrackerMaster mapOutputTracker() {
      return this.mapOutputTracker;
   }

   private SchedulableBuilder schedulableBuilder() {
      return this.schedulableBuilder;
   }

   private void schedulableBuilder_$eq(SchedulableBuilder x$1) {
      this.schedulableBuilder = x$1;
   }

   private String schedulingModeConf() {
      return this.schedulingModeConf;
   }

   public Enumeration.Value schedulingMode() {
      return this.schedulingMode;
   }

   public Pool rootPool() {
      return this.rootPool;
   }

   public TaskResultGetter taskResultGetter() {
      return this.taskResultGetter;
   }

   public void taskResultGetter_$eq(TaskResultGetter x$1) {
      this.taskResultGetter = x$1;
   }

   public void setDAGScheduler(DAGScheduler dagScheduler) {
      this.dagScheduler_$eq(dagScheduler);
   }

   public void initialize(SchedulerBackend backend) {
      Object var4;
      label29: {
         label31: {
            this.backend_$eq(backend);
            Enumeration.Value var2 = this.schedulingMode();
            Enumeration.Value var10001 = SchedulingMode$.MODULE$.FIFO();
            if (var10001 == null) {
               if (var2 == null) {
                  break label31;
               }
            } else if (var10001.equals(var2)) {
               break label31;
            }

            label22: {
               var10001 = SchedulingMode$.MODULE$.FAIR();
               if (var10001 == null) {
                  if (var2 == null) {
                     break label22;
                  }
               } else if (var10001.equals(var2)) {
                  break label22;
               }

               throw new IllegalArgumentException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unsupported ", ": "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{TaskSchedulerImpl$.MODULE$.SCHEDULER_MODE_PROPERTY()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.schedulingMode()}))).toString());
            }

            var4 = new FairSchedulableBuilder(this.rootPool(), this.conf());
            break label29;
         }

         var4 = new FIFOSchedulableBuilder(this.rootPool());
      }

      this.schedulableBuilder_$eq((SchedulableBuilder)var4);
      this.schedulableBuilder().buildPools();
   }

   public long newTaskId() {
      return this.nextTaskId().getAndIncrement();
   }

   public void start() {
      this.backend().start();
      if (!this.isLocal && this.conf().getBoolean("spark.speculation", false)) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Starting speculative execution thread";
            }
         });
         this.speculationScheduler().scheduleWithFixedDelay(new Runnable(this) {
            // $FF: synthetic field
            private final TaskSchedulerImpl $outer;

            public void run() {
               Utils$.MODULE$.tryOrStopSparkContext(this.$outer.sc(), new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anon$$$outer().checkSpeculatableTasks();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
            }

            // $FF: synthetic method
            public TaskSchedulerImpl org$apache$spark$scheduler$TaskSchedulerImpl$$anon$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, this.SPECULATION_INTERVAL_MS(), this.SPECULATION_INTERVAL_MS(), TimeUnit.MILLISECONDS);
      }

   }

   public void postStartHook() {
      this.waitBackendReady();
   }

   public void submitTasks(TaskSet taskSet) {
      Task[] tasks = taskSet.tasks();
      this.logInfo(new Serializable(this, taskSet, tasks) {
         public static final long serialVersionUID = 0L;
         private final TaskSet taskSet$1;
         private final Task[] tasks$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Adding task set ").append((Object)this.taskSet$1.id()).append((Object)" with ").append((Object)BoxesRunTime.boxToInteger(this.tasks$1.length)).append((Object)" tasks").toString();
         }

         public {
            this.taskSet$1 = taskSet$1;
            this.tasks$1 = tasks$1;
         }
      });
      synchronized(this) {
         TaskSetManager manager = this.createTaskSetManager(taskSet, this.maxTaskFailures());
         int stage = taskSet.stageId();
         HashMap stageTaskSets = (HashMap)this.org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt().getOrElseUpdate(BoxesRunTime.boxToInteger(stage), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final HashMap<Object, TaskSetManager> apply() {
               return new HashMap<Object, TaskSetManager>();
            }
         });
         stageTaskSets.update(BoxesRunTime.boxToInteger(taskSet.stageAttemptId()), manager);
         boolean conflictingTaskSet = stageTaskSets.exists(new Serializable(this, taskSet) {
            public static final long serialVersionUID = 0L;
            private final TaskSet taskSet$1;

            public final boolean apply(Tuple2<Object, TaskSetManager> x0$1) {
               if (x0$1 == null) {
                  throw new MatchError(x0$1);
               } else {
                  boolean var6;
                  label22: {
                     label21: {
                        TaskSetManager ts = (TaskSetManager)x0$1._2();
                        TaskSet var10000 = ts.taskSet();
                        TaskSet var5 = this.taskSet$1;
                        if (var10000 == null) {
                           if (var5 == null) {
                              break label21;
                           }
                        } else if (var10000.equals(var5)) {
                           break label21;
                        }

                        if (!ts.isZombie()) {
                           var6 = true;
                           break label22;
                        }
                     }

                     var6 = false;
                  }

                  boolean var4 = var6;
                  return var4;
               }
            }

            public {
               this.taskSet$1 = taskSet$1;
            }
         });
         if (conflictingTaskSet) {
            throw new IllegalStateException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"more than one active taskSet for stage ", ":"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(stage)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{((TraversableOnce)stageTaskSets.toSeq().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(Tuple2<Object, TaskSetManager> x$2) {
                  return ((TaskSetManager)x$2._2()).taskSet().id();
               }
            }, Seq$.MODULE$.canBuildFrom())).mkString(",")}))).toString());
         }

         this.schedulableBuilder().addTaskSetManager(manager, manager.taskSet().properties());
         if (!this.isLocal && !this.hasReceivedTask()) {
            this.starvationTimer().scheduleAtFixedRate(new TimerTask(this) {
               // $FF: synthetic field
               private final TaskSchedulerImpl $outer;

               public void run() {
                  if (this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$hasLaunchedTask()) {
                     this.cancel();
                  } else {
                     this.$outer.logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "Initial job has not accepted any resources; check your cluster UI to ensure that workers are registered and have sufficient resources";
                        }
                     });
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, this.STARVATION_TIMEOUT_MS(), this.STARVATION_TIMEOUT_MS());
         }

         this.hasReceivedTask_$eq(true);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this.backend().reviveOffers();
   }

   public TaskSetManager createTaskSetManager(TaskSet taskSet, int maxTaskFailures) {
      return new TaskSetManager(this, taskSet, maxTaskFailures, this.blacklistTrackerOpt(), TaskSetManager$.MODULE$.$lessinit$greater$default$5());
   }

   public synchronized void cancelTasks(int stageId, boolean interruptThread) {
      this.logInfo(new Serializable(this, stageId) {
         public static final long serialVersionUID = 0L;
         private final int stageId$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Cancelling stage ").append((Object)BoxesRunTime.boxToInteger(this.stageId$1)).toString();
         }

         public {
            this.stageId$1 = stageId$1;
         }
      });
      this.org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt().get(BoxesRunTime.boxToInteger(stageId)).foreach(new Serializable(this, stageId, interruptThread) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSchedulerImpl $outer;
         public final int stageId$1;
         public final boolean interruptThread$1;

         public final void apply(HashMap<Object, TaskSetManager> attempts) {
            attempts.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(Tuple2<Object, TaskSetManager> x0$2) {
                  if (x0$2 != null) {
                     TaskSetManager tsm = (TaskSetManager)x0$2._2();
                     tsm.runningTasksSet().foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final void apply(long tid) {
                           this.apply$mcVJ$sp(tid);
                        }

                        public void apply$mcVJ$sp(long tid) {
                           this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$anonfun$$$outer().org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().taskIdToExecutorId().get(BoxesRunTime.boxToLong(tid)).foreach(new Serializable(this, tid) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;
                              private final long tid$3;

                              public final void apply(String execId) {
                                 this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$anonfun$$anonfun$$$outer().org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$anonfun$$$outer().org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().backend().killTask(this.tid$3, execId, this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$anonfun$$anonfun$$$outer().org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$anonfun$$$outer().interruptThread$1, "Stage cancelled");
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                    this.tid$3 = tid$3;
                                 }
                              }
                           });
                        }

                        // $FF: synthetic method
                        public <undefinedtype> org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$anonfun$$anonfun$$$outer() {
                           return this.$outer;
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     tsm.abort((new StringOps(Predef$.MODULE$.augmentString("Stage %s cancelled"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.stageId$1)})), tsm.abort$default$2());
                     this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().logInfo(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringOps(Predef$.MODULE$.augmentString("Stage %d was cancelled"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$anonfun$$$outer().stageId$1)}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     BoxedUnit var4 = BoxedUnit.UNIT;
                  } else {
                     throw new MatchError(x0$2);
                  }
               }

               // $FF: synthetic method
               public <undefinedtype> org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         // $FF: synthetic method
         public TaskSchedulerImpl org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.stageId$1 = stageId$1;
               this.interruptThread$1 = interruptThread$1;
            }
         }
      });
   }

   public boolean killTaskAttempt(long taskId, boolean interruptThread, String reason) {
      this.logInfo(new Serializable(this, taskId, reason) {
         public static final long serialVersionUID = 0L;
         private final long taskId$1;
         private final String reason$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Killing task ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskId$1), this.reason$1}));
         }

         public {
            this.taskId$1 = taskId$1;
            this.reason$1 = reason$1;
         }
      });
      Option execId = this.taskIdToExecutorId().get(BoxesRunTime.boxToLong(taskId));
      boolean var10000;
      if (execId.isDefined()) {
         this.backend().killTask(taskId, (String)execId.get(), interruptThread, reason);
         var10000 = true;
      } else {
         this.logWarning(new Serializable(this, taskId) {
            public static final long serialVersionUID = 0L;
            private final long taskId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Could not kill task ", " because no task with that ID was found."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskId$1)}));
            }

            public {
               this.taskId$1 = taskId$1;
            }
         });
         var10000 = false;
      }

      return var10000;
   }

   public synchronized void taskSetFinished(TaskSetManager manager) {
      this.org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt().get(BoxesRunTime.boxToInteger(manager.taskSet().stageId())).foreach(new Serializable(this, manager) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSchedulerImpl $outer;
         private final TaskSetManager manager$1;

         public final Object apply(HashMap<Object, TaskSetManager> taskSetsForStage) {
            taskSetsForStage.$minus$eq(BoxesRunTime.boxToInteger(this.manager$1.taskSet().stageAttemptId()));
            return taskSetsForStage.isEmpty() ? this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt().$minus$eq(BoxesRunTime.boxToInteger(this.manager$1.taskSet().stageId())) : BoxedUnit.UNIT;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.manager$1 = manager$1;
            }
         }
      });
      manager.parent().removeSchedulable(manager);
      this.logInfo(new Serializable(this, manager) {
         public static final long serialVersionUID = 0L;
         private final TaskSetManager manager$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removed TaskSet ", ", whose tasks have all completed, from pool"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.manager$1.taskSet().id()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.manager$1.parent().name()}))).toString();
         }

         public {
            this.manager$1 = manager$1;
         }
      });
   }

   public boolean org$apache$spark$scheduler$TaskSchedulerImpl$$resourceOfferSingleTaskSet(TaskSetManager taskSet, Enumeration.Value maxLocality, Seq<WorkerOffer> shuffledOffers, int[] availableCpus, IndexedSeq<ArrayBuffer<TaskDescription>> tasks) {
      Object var6 = new Object();

      try {
         BooleanRef launchedTask = BooleanRef.create(false);
         RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), shuffledOffers.size()).foreach$mVc$sp(new Serializable(this, taskSet, maxLocality, shuffledOffers, availableCpus, tasks, launchedTask, var6) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSchedulerImpl $outer;
            public final TaskSetManager taskSet$2;
            private final Enumeration.Value maxLocality$1;
            private final Seq shuffledOffers$1;
            public final int[] availableCpus$1;
            public final IndexedSeq tasks$2;
            public final BooleanRef launchedTask$1;
            private final Object nonLocalReturnKey1$1;

            public final void apply(int i) {
               this.apply$mcVI$sp(i);
            }

            public void apply$mcVI$sp(int i) {
               String execId = ((WorkerOffer)this.shuffledOffers$1.apply(i)).executorId();
               String host = ((WorkerOffer)this.shuffledOffers$1.apply(i)).host();
               if (this.availableCpus$1[i] >= this.$outer.CPUS_PER_TASK()) {
                  try {
                     this.taskSet$2.resourceOffer(execId, host, this.maxLocality$1).foreach(new Serializable(this, execId, i) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final String execId$1;
                        private final int i$1;

                        public final void apply(TaskDescription task) {
                           ((ArrayBuffer)this.$outer.tasks$2.apply(this.i$1)).$plus$eq(task);
                           long tid = task.taskId();
                           this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().taskIdToTaskSetManager().update(BoxesRunTime.boxToLong(tid), this.$outer.taskSet$2);
                           this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().taskIdToExecutorId().update(BoxesRunTime.boxToLong(tid), this.execId$1);
                           ((HashSet)this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().apply(this.execId$1)).add(BoxesRunTime.boxToLong(tid));
                           this.$outer.availableCpus$1[this.i$1] -= this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().CPUS_PER_TASK();
                           Predef$.MODULE$.assert(this.$outer.availableCpus$1[this.i$1] >= 0);
                           this.$outer.launchedTask$1.elem = true;
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.execId$1 = execId$1;
                              this.i$1 = i$1;
                           }
                        }
                     });
                  } catch (TaskNotSerializableException var5) {
                     this.$outer.logError(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Resource offer failed, task set ", " was not serializable"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.taskSet$2.name()}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     throw new NonLocalReturnControl$mcZ$sp(this.nonLocalReturnKey1$1, this.launchedTask$1.elem);
                  }
               }

            }

            // $FF: synthetic method
            public TaskSchedulerImpl org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.taskSet$2 = taskSet$2;
                  this.maxLocality$1 = maxLocality$1;
                  this.shuffledOffers$1 = shuffledOffers$1;
                  this.availableCpus$1 = availableCpus$1;
                  this.tasks$2 = tasks$2;
                  this.launchedTask$1 = launchedTask$1;
                  this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
               }
            }
         });
         return launchedTask.elem;
      } catch (NonLocalReturnControl var9) {
         if (var9.key() == var6) {
            return var9.value$mcZ$sp();
         } else {
            throw var9;
         }
      }
   }

   public synchronized Seq<Seq<TaskDescription>> resourceOffers(IndexedSeq<WorkerOffer> offers) {
      BooleanRef newExecAvail = BooleanRef.create(false);
      offers.foreach(new Serializable(this, newExecAvail) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSchedulerImpl $outer;
         private final BooleanRef newExecAvail$1;

         public final void apply(WorkerOffer o) {
            if (!this.$outer.hostToExecutors().contains(o.host())) {
               this.$outer.hostToExecutors().update(o.host(), new HashSet());
            }

            if (!this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().contains(o.executorId())) {
               ((HashSet)this.$outer.hostToExecutors().apply(o.host())).$plus$eq(o.executorId());
               this.$outer.executorAdded(o.executorId(), o.host());
               this.$outer.executorIdToHost().update(o.executorId(), o.host());
               this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().update(o.executorId(), HashSet$.MODULE$.apply(Nil$.MODULE$));
               this.newExecAvail$1.elem = true;
            }

            this.$outer.getRackForHost(o.host()).foreach(new Serializable(this, o) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final WorkerOffer o$1;

               public final HashSet<String> apply(String rack) {
                  return ((HashSet)this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().hostsByRack().getOrElseUpdate(rack, new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final HashSet<String> apply() {
                        return new HashSet<String>();
                     }
                  })).$plus$eq(this.o$1.host());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.o$1 = o$1;
                  }
               }
            });
         }

         // $FF: synthetic method
         public TaskSchedulerImpl org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.newExecAvail$1 = newExecAvail$1;
            }
         }
      });
      this.blacklistTrackerOpt().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(BlacklistTracker x$3) {
            x$3.applyBlacklistTimeout();
         }
      });
      IndexedSeq filteredOffers = (IndexedSeq)this.blacklistTrackerOpt().map(new Serializable(this, offers) {
         public static final long serialVersionUID = 0L;
         private final IndexedSeq offers$1;

         public final IndexedSeq<WorkerOffer> apply(BlacklistTracker blacklistTracker) {
            return (IndexedSeq)this.offers$1.filter(new Serializable(this, blacklistTracker) {
               public static final long serialVersionUID = 0L;
               private final BlacklistTracker blacklistTracker$1;

               public final boolean apply(WorkerOffer offer) {
                  return !this.blacklistTracker$1.isNodeBlacklisted(offer.host()) && !this.blacklistTracker$1.isExecutorBlacklisted(offer.executorId());
               }

               public {
                  this.blacklistTracker$1 = blacklistTracker$1;
               }
            });
         }

         public {
            this.offers$1 = offers$1;
         }
      }).getOrElse(new Serializable(this, offers) {
         public static final long serialVersionUID = 0L;
         private final IndexedSeq offers$1;

         public final IndexedSeq<WorkerOffer> apply() {
            return this.offers$1;
         }

         public {
            this.offers$1 = offers$1;
         }
      });
      IndexedSeq shuffledOffers = this.shuffleOffers(filteredOffers);
      IndexedSeq tasks = (IndexedSeq)shuffledOffers.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSchedulerImpl $outer;

         public final ArrayBuffer<TaskDescription> apply(WorkerOffer o) {
            return new ArrayBuffer<TaskDescription>(o.cores() / this.$outer.CPUS_PER_TASK());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, IndexedSeq$.MODULE$.canBuildFrom());
      int[] availableCpus = (int[])((TraversableOnce)shuffledOffers.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerOffer o) {
            return o.cores();
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Int());
      ArrayBuffer sortedTaskSets = this.rootPool().getSortedTaskSetQueue();
      sortedTaskSets.foreach(new Serializable(this, newExecAvail) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSchedulerImpl $outer;
         private final BooleanRef newExecAvail$1;

         public final void apply(TaskSetManager taskSet) {
            this.$outer.logDebug(new Serializable(this, taskSet) {
               public static final long serialVersionUID = 0L;
               private final TaskSetManager taskSet$3;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("parentName: %s, name: %s, runningTasks: %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.taskSet$3.parent().name(), this.taskSet$3.name(), BoxesRunTime.boxToInteger(this.taskSet$3.runningTasks())}));
               }

               public {
                  this.taskSet$3 = taskSet$3;
               }
            });
            if (this.newExecAvail$1.elem) {
               taskSet.executorAdded();
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.newExecAvail$1 = newExecAvail$1;
            }
         }
      });
      sortedTaskSets.foreach(new Serializable(this, shuffledOffers, tasks, availableCpus) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSchedulerImpl $outer;
         public final IndexedSeq shuffledOffers$2;
         public final IndexedSeq tasks$3;
         public final int[] availableCpus$2;

         public final void apply(TaskSetManager taskSet) {
            BooleanRef launchedAnyTask = BooleanRef.create(false);
            BooleanRef launchedTaskAtCurrentMaxLocality = BooleanRef.create(false);
            Predef$.MODULE$.refArrayOps(taskSet.myLocalityLevels()).foreach(new Serializable(this, launchedAnyTask, launchedTaskAtCurrentMaxLocality, taskSet) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final BooleanRef launchedAnyTask$1;
               private final BooleanRef launchedTaskAtCurrentMaxLocality$1;
               private final TaskSetManager taskSet$4;

               public final void apply(Enumeration.Value currentMaxLocality) {
                  do {
                     this.launchedTaskAtCurrentMaxLocality$1.elem = this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().org$apache$spark$scheduler$TaskSchedulerImpl$$resourceOfferSingleTaskSet(this.taskSet$4, currentMaxLocality, this.$outer.shuffledOffers$2, this.$outer.availableCpus$2, this.$outer.tasks$3);
                     this.launchedAnyTask$1.elem |= this.launchedTaskAtCurrentMaxLocality$1.elem;
                  } while(this.launchedTaskAtCurrentMaxLocality$1.elem);

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.launchedAnyTask$1 = launchedAnyTask$1;
                     this.launchedTaskAtCurrentMaxLocality$1 = launchedTaskAtCurrentMaxLocality$1;
                     this.taskSet$4 = taskSet$4;
                  }
               }
            });
            if (!launchedAnyTask.elem) {
               taskSet.abortIfCompletelyBlacklisted(this.$outer.hostToExecutors());
            }

         }

         // $FF: synthetic method
         public TaskSchedulerImpl org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.shuffledOffers$2 = shuffledOffers$2;
               this.tasks$3 = tasks$3;
               this.availableCpus$2 = availableCpus$2;
            }
         }
      });
      if (tasks.size() > 0) {
         this.org$apache$spark$scheduler$TaskSchedulerImpl$$hasLaunchedTask_$eq(true);
      }

      return tasks;
   }

   public IndexedSeq<WorkerOffer> shuffleOffers(IndexedSeq<WorkerOffer> offers) {
      return (IndexedSeq)Random$.MODULE$.shuffle(offers, IndexedSeq$.MODULE$.canBuildFrom());
   }

   public void statusUpdate(long tid, Enumeration.Value state, ByteBuffer serializedData) {
      ObjectRef failedExecutor = ObjectRef.create(None$.MODULE$);
      ObjectRef reason = ObjectRef.create(None$.MODULE$);
      synchronized(this) {
         this.liftedTree2$1(tid, state, serializedData, failedExecutor, reason);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      if (((Option)failedExecutor.elem).isDefined()) {
         Predef$.MODULE$.assert(((Option)reason.elem).isDefined());
         this.dagScheduler().executorLost((String)((Option)failedExecutor.elem).get(), (ExecutorLossReason)((Option)reason.elem).get());
         this.backend().reviveOffers();
      }

   }

   public boolean executorHeartbeatReceived(String execId, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[] accumUpdates, BlockManagerId blockManagerId) {
      Object var6;
      synchronized(this) {
         var6 = Predef$.MODULE$.refArrayOps(accumUpdates).flatMap(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSchedulerImpl $outer;

            public final Iterable<Tuple4<Object, Object, Object, Seq<AccumulableInfo>>> apply(Tuple2<Object, Seq<AccumulatorV2<?, ?>>> x0$3) {
               if (x0$3 != null) {
                  long id = x0$3._1$mcJ$sp();
                  Seq updates = (Seq)x0$3._2();
                  Seq accInfos = (Seq)updates.map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final AccumulableInfo apply(AccumulatorV2<?, ?> acc) {
                        return acc.toInfo(new Some(acc.value()), None$.MODULE$);
                     }
                  }, Seq$.MODULE$.canBuildFrom());
                  Iterable var6 = Option$.MODULE$.option2Iterable(this.$outer.taskIdToTaskSetManager().get(BoxesRunTime.boxToLong(id)).map(new Serializable(this, id, accInfos) {
                     public static final long serialVersionUID = 0L;
                     private final long id$1;
                     private final Seq accInfos$1;

                     public final Tuple4<Object, Object, Object, Seq<AccumulableInfo>> apply(TaskSetManager taskSetMgr) {
                        return new Tuple4<Object, Object, Object, Seq<AccumulableInfo>>(BoxesRunTime.boxToLong(this.id$1), BoxesRunTime.boxToInteger(taskSetMgr.stageId()), BoxesRunTime.boxToInteger(taskSetMgr.taskSet().stageAttemptId()), this.accInfos$1);
                     }

                     public {
                        this.id$1 = id$1;
                        this.accInfos$1 = accInfos$1;
                     }
                  }));
                  return var6;
               } else {
                  throw new MatchError(x0$3);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple4.class)));
      }

      Tuple4[] accumUpdatesWithTaskIds = (Tuple4[])var6;
      return this.dagScheduler().executorHeartbeatReceived(execId, accumUpdatesWithTaskIds, blockManagerId);
   }

   public synchronized void handleTaskGettingResult(TaskSetManager taskSetManager, long tid) {
      taskSetManager.handleTaskGettingResult(tid);
   }

   public synchronized void handleSuccessfulTask(TaskSetManager taskSetManager, long tid, DirectTaskResult<?> taskResult) {
      taskSetManager.handleSuccessfulTask(tid, taskResult);
   }

   public synchronized void handleFailedTask(TaskSetManager taskSetManager, long tid, Enumeration.Value taskState, TaskFailedReason reason) {
      taskSetManager.handleFailedTask(tid, taskState, reason);
      if (!taskSetManager.isZombie() && !taskSetManager.someAttemptSucceeded(tid)) {
         this.backend().reviveOffers();
      }

   }

   public synchronized void error(String message) {
      if (this.org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt().nonEmpty()) {
         this.org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt().values().foreach(new Serializable(this, message) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSchedulerImpl $outer;
            public final String message$1;

            public final void apply(HashMap<Object, TaskSetManager> attempts) {
               attempts.values().foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(TaskSetManager manager) {
                     try {
                        manager.abort(this.$outer.message$1, manager.abort$default$2());
                     } catch (Exception var3) {
                        this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().logError(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final String apply() {
                              return "Exception in error callback";
                           }
                        }, var3);
                     }

                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
            }

            // $FF: synthetic method
            public TaskSchedulerImpl org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.message$1 = message$1;
               }
            }
         });
      } else {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exiting due to error from cluster scheduler: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{message})));
      }
   }

   public void stop() {
      this.speculationScheduler().shutdown();
      if (this.backend() != null) {
         this.backend().stop();
      }

      if (this.taskResultGetter() != null) {
         this.taskResultGetter().stop();
      }

      this.starvationTimer().cancel();
   }

   public int defaultParallelism() {
      return this.backend().defaultParallelism();
   }

   public void checkSpeculatableTasks() {
      boolean shouldRevive = false;
      synchronized(this) {
         shouldRevive = this.rootPool().checkSpeculatableTasks(this.MIN_TIME_TO_SPECULATION());
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      if (shouldRevive) {
         this.backend().reviveOffers();
      }

   }

   public void executorLost(String executorId, ExecutorLossReason reason) {
      Option failedExecutor = None$.MODULE$;
      synchronized(this) {
         if (this.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().contains(executorId)) {
            String hostPort = (String)this.executorIdToHost().apply(executorId);
            this.logExecutorLoss(executorId, hostPort, reason);
            this.removeExecutor(executorId, reason);
            failedExecutor = new Some(executorId);
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            Option var6 = this.executorIdToHost().get(executorId);
            if (var6 instanceof Some) {
               Some var7 = (Some)var6;
               String hostPort = (String)var7.x();
               this.logExecutorLoss(executorId, hostPort, reason);
               this.removeExecutor(executorId, reason);
               BoxedUnit var9 = BoxedUnit.UNIT;
            } else {
               if (!None$.MODULE$.equals(var6)) {
                  throw new MatchError(var6);
               }

               this.logError(new Serializable(this, executorId, reason) {
                  public static final long serialVersionUID = 0L;
                  private final String executorId$2;
                  private final ExecutorLossReason reason$3;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Lost an executor ", " (already removed): ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$2, this.reason$3}));
                  }

                  public {
                     this.executorId$2 = executorId$2;
                     this.reason$3 = reason$3;
                  }
               });
               BoxedUnit var11 = BoxedUnit.UNIT;
            }

            BoxedUnit var12 = BoxedUnit.UNIT;
         }
      }

      if (failedExecutor.isDefined()) {
         this.dagScheduler().executorLost((String)failedExecutor.get(), reason);
         this.backend().reviveOffers();
      }

   }

   public void workerRemoved(String workerId, String host, String message) {
      this.logInfo(new Serializable(this, workerId, message) {
         public static final long serialVersionUID = 0L;
         private final String workerId$1;
         private final String message$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Handle removed worker ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.workerId$1, this.message$2}));
         }

         public {
            this.workerId$1 = workerId$1;
            this.message$2 = message$2;
         }
      });
      this.dagScheduler().workerRemoved(workerId, host, message);
   }

   private void logExecutorLoss(String executorId, String hostPort, ExecutorLossReason reason) {
      if (LossReasonPending$.MODULE$.equals(reason)) {
         this.logDebug(new Serializable(this, executorId, hostPort) {
            public static final long serialVersionUID = 0L;
            private final String executorId$3;
            private final String hostPort$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor ", " on ", " lost, but reason not yet known."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$3, this.hostPort$1}));
            }

            public {
               this.executorId$3 = executorId$3;
               this.hostPort$1 = hostPort$1;
            }
         });
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else if (ExecutorKilled$.MODULE$.equals(reason)) {
         this.logInfo(new Serializable(this, executorId, hostPort) {
            public static final long serialVersionUID = 0L;
            private final String executorId$3;
            private final String hostPort$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor ", " on ", " killed by driver."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$3, this.hostPort$1}));
            }

            public {
               this.executorId$3 = executorId$3;
               this.hostPort$1 = hostPort$1;
            }
         });
         BoxedUnit var6 = BoxedUnit.UNIT;
      } else {
         this.logError(new Serializable(this, executorId, hostPort, reason) {
            public static final long serialVersionUID = 0L;
            private final String executorId$3;
            private final String hostPort$1;
            private final ExecutorLossReason reason$4;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Lost executor ", " on ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$3, this.hostPort$1, this.reason$4}));
            }

            public {
               this.executorId$3 = executorId$3;
               this.hostPort$1 = hostPort$1;
               this.reason$4 = reason$4;
            }
         });
         BoxedUnit var7 = BoxedUnit.UNIT;
      }

   }

   public void org$apache$spark$scheduler$TaskSchedulerImpl$$cleanupTaskState(long tid) {
      this.taskIdToTaskSetManager().remove(BoxesRunTime.boxToLong(tid));
      this.taskIdToExecutorId().remove(BoxesRunTime.boxToLong(tid)).foreach(new Serializable(this, tid) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSchedulerImpl $outer;
         public final long tid$2;

         public final void apply(String executorId) {
            this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().get(executorId).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final boolean apply(HashSet<Object> x$4) {
                  return x$4.remove(BoxesRunTime.boxToLong(this.$outer.tid$2));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.tid$2 = tid$2;
            }
         }
      });
   }

   private void removeExecutor(String executorId, ExecutorLossReason reason) {
      this.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().remove(executorId).foreach(new Serializable(this, executorId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSchedulerImpl $outer;
         public final String executorId$1;

         public final void apply(HashSet<Object> taskIds) {
            this.$outer.logDebug(new Serializable(this, taskIds) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final HashSet taskIds$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Cleaning up TaskScheduler state for tasks ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " on failed executor ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.taskIds$1.mkString("[", ",", "]"), this.$outer.executorId$1}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.taskIds$1 = taskIds$1;
                  }
               }
            });
            taskIds.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(long tid) {
                  this.apply$mcVJ$sp(tid);
               }

               public void apply$mcVJ$sp(long tid) {
                  this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().org$apache$spark$scheduler$TaskSchedulerImpl$$cleanupTaskState(tid);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         // $FF: synthetic method
         public TaskSchedulerImpl org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.executorId$1 = executorId$1;
            }
         }
      });
      String host = (String)this.executorIdToHost().apply(executorId);
      HashSet execs = (HashSet)this.hostToExecutors().getOrElse(host, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final HashSet<String> apply() {
            return new HashSet<String>();
         }
      });
      execs.$minus$eq(executorId);
      if (execs.isEmpty()) {
         this.hostToExecutors().$minus$eq(host);
         this.getRackForHost(host).foreach(new Serializable(this, host) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSchedulerImpl $outer;
            public final String host$1;

            public final void apply(String rack) {
               this.$outer.hostsByRack().get(rack).foreach(new Serializable(this, rack) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final String rack$1;

                  public final Object apply(HashSet<String> hosts) {
                     hosts.$minus$eq(this.$outer.host$1);
                     return hosts.isEmpty() ? this.$outer.org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer().hostsByRack().$minus$eq(this.rack$1) : BoxedUnit.UNIT;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.rack$1 = rack$1;
                     }
                  }
               });
            }

            // $FF: synthetic method
            public TaskSchedulerImpl org$apache$spark$scheduler$TaskSchedulerImpl$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.host$1 = host$1;
               }
            }
         });
      }

      label17: {
         LossReasonPending$ var5 = LossReasonPending$.MODULE$;
         if (reason == null) {
            if (var5 == null) {
               break label17;
            }
         } else if (reason.equals(var5)) {
            break label17;
         }

         this.executorIdToHost().$minus$eq(executorId);
         this.rootPool().executorLost(executorId, host, reason);
      }

      this.blacklistTrackerOpt().foreach(new Serializable(this, executorId) {
         public static final long serialVersionUID = 0L;
         private final String executorId$1;

         public final void apply(BlacklistTracker x$5) {
            x$5.handleRemovedExecutor(this.executorId$1);
         }

         public {
            this.executorId$1 = executorId$1;
         }
      });
   }

   public void executorAdded(String execId, String host) {
      this.dagScheduler().executorAdded(execId, host);
   }

   public synchronized Option<Set<String>> getExecutorsAliveOnHost(String host) {
      return this.hostToExecutors().get(host).<Set<String>>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final scala.collection.immutable.Set<String> apply(HashSet<String> x$6) {
            return x$6.toSet();
         }
      });
   }

   public synchronized boolean hasExecutorsAliveOnHost(String host) {
      return this.hostToExecutors().contains(host);
   }

   public synchronized boolean hasHostAliveOnRack(String rack) {
      return this.hostsByRack().contains(rack);
   }

   public synchronized boolean isExecutorAlive(String execId) {
      return this.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().contains(execId);
   }

   public synchronized boolean isExecutorBusy(String execId) {
      return this.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().get(execId).exists(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(HashSet<Object> x$7) {
            return x$7.nonEmpty();
         }
      });
   }

   public scala.collection.immutable.Set<String> nodeBlacklist() {
      return (scala.collection.immutable.Set)this.blacklistTrackerOpt().<String>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final scala.collection.immutable.Set<String> apply(BlacklistTracker x$8) {
            return x$8.nodeBlacklist();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final scala.collection.immutable.Set<String> apply() {
            return (scala.collection.immutable.Set)Set$.MODULE$.apply(Nil$.MODULE$);
         }
      });
   }

   public Option<String> getRackForHost(String value) {
      return None$.MODULE$;
   }

   private void waitBackendReady() {
      if (!this.backend().isReady()) {
         while(!this.backend().isReady()) {
            if (this.sc().stopped().get()) {
               throw new IllegalStateException("Spark context stopped while waiting for backend");
            }

            synchronized(this) {
               this.wait(100L);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }
         }

      }
   }

   public String applicationId() {
      return this.backend().applicationId();
   }

   public Option<String> applicationAttemptId() {
      return this.backend().applicationAttemptId();
   }

   public Option<TaskSetManager> taskSetManagerForAttempt(int stageId, int stageAttemptId) {
      return this.org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt().get(BoxesRunTime.boxToInteger(stageId)).<TaskSetManager>flatMap(new Serializable(this, stageAttemptId) {
         public static final long serialVersionUID = 0L;
         private final int stageAttemptId$1;

         public final Option<TaskSetManager> apply(HashMap<Object, TaskSetManager> attempts) {
            return attempts.get(BoxesRunTime.boxToInteger(this.stageAttemptId$1)).<TaskSetManager>map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final TaskSetManager apply(TaskSetManager manager) {
                  return manager;
               }
            });
         }

         public {
            this.stageAttemptId$1 = stageAttemptId$1;
         }
      });
   }

   private final Enumeration.Value liftedTree1$1() {
      try {
         return SchedulingMode$.MODULE$.withName(this.schedulingModeConf().toUpperCase(Locale.ROOT));
      } catch (NoSuchElementException var2) {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unrecognized ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{TaskSchedulerImpl$.MODULE$.SCHEDULER_MODE_PROPERTY(), this.schedulingModeConf()})));
      }
   }

   private final void liftedTree2$1(long tid$1, Enumeration.Value state$1, ByteBuffer serializedData$1, ObjectRef failedExecutor$1, ObjectRef reason$2) {
      try {
         Option var8 = this.taskIdToTaskSetManager().get(BoxesRunTime.boxToLong(tid$1));
         if (var8 instanceof Some) {
            TaskSetManager taskSet;
            label48: {
               Some var9 = (Some)var8;
               taskSet = (TaskSetManager)var9.x();
               Enumeration.Value var12 = TaskState$.MODULE$.LOST();
               if (state$1 == null) {
                  if (var12 != null) {
                     break label48;
                  }
               } else if (!state$1.equals(var12)) {
                  break label48;
               }

               String execId = (String)this.taskIdToExecutorId().getOrElse(BoxesRunTime.boxToLong(tid$1), new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Nothing$ apply() {
                     throw new IllegalStateException("taskIdToTaskSetManager.contains(tid) <=> taskIdToExecutorId.contains(tid)");
                  }
               });
               if (this.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds().contains(execId)) {
                  reason$2.elem = (T)(new Some(new SlaveLost((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " was lost, so marking the executor as lost as well."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(tid$1)})), SlaveLost$.MODULE$.apply$default$2())));
                  this.removeExecutor(execId, (ExecutorLossReason)((Option)reason$2.elem).get());
                  failedExecutor$1.elem = (T)(new Some(execId));
               }
            }

            if (!TaskState$.MODULE$.isFinished(state$1)) {
               BoxedUnit var18 = BoxedUnit.UNIT;
            } else {
               label60: {
                  this.org$apache$spark$scheduler$TaskSchedulerImpl$$cleanupTaskState(tid$1);
                  taskSet.removeRunningTask(tid$1);
                  Enumeration.Value var14 = TaskState$.MODULE$.FINISHED();
                  if (state$1 == null) {
                     if (var14 == null) {
                        break label60;
                     }
                  } else if (state$1.equals(var14)) {
                     break label60;
                  }

                  if (((SetLike)scala.collection.Set$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Enumeration.Value[]{TaskState$.MODULE$.FAILED(), TaskState$.MODULE$.KILLED(), TaskState$.MODULE$.LOST()}))).contains(state$1)) {
                     this.taskResultGetter().enqueueFailedTask(taskSet, tid$1, state$1, serializedData$1);
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     BoxedUnit var16 = BoxedUnit.UNIT;
                  }

                  return;
               }

               this.taskResultGetter().enqueueSuccessfulTask(taskSet, tid$1, serializedData$1);
               BoxedUnit var17 = BoxedUnit.UNIT;
            }
         } else {
            if (!None$.MODULE$.equals(var8)) {
               throw new MatchError(var8);
            }

            this.logError(new Serializable(this, tid$1, state$1) {
               public static final long serialVersionUID = 0L;
               private final long tid$1;
               private final Enumeration.Value state$1;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Ignoring update with state %s for TID %s because its task set is gone (this is likely the result of receiving duplicate task finished status updates) or its executor has been marked as failed."))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.state$1, BoxesRunTime.boxToLong(this.tid$1)}));
               }

               public {
                  this.tid$1 = tid$1;
                  this.state$1 = state$1;
               }
            });
            BoxedUnit var11 = BoxedUnit.UNIT;
         }
      } catch (Exception var15) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Exception in statusUpdate";
            }
         }, var15);
      }

   }

   public TaskSchedulerImpl(SparkContext sc, int maxTaskFailures, boolean isLocal) {
      this.sc = sc;
      this.maxTaskFailures = maxTaskFailures;
      this.isLocal = isLocal;
      TaskScheduler$class.$init$(this);
      Logging$class.$init$(this);
      this.conf = sc.conf();
      this.SPECULATION_INTERVAL_MS = this.conf().getTimeAsMs("spark.speculation.interval", "100ms");
      this.MIN_TIME_TO_SPECULATION = 100;
      this.speculationScheduler = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("task-scheduler-speculation");
      this.STARVATION_TIMEOUT_MS = this.conf().getTimeAsMs("spark.starvation.timeout", "15s");
      this.CPUS_PER_TASK = this.conf().getInt("spark.task.cpus", 1);
      this.org$apache$spark$scheduler$TaskSchedulerImpl$$taskSetsByStageIdAndAttempt = new HashMap<Object, HashMap<Object, TaskSetManager>>();
      this.taskIdToTaskSetManager = new HashMap<Object, TaskSetManager>();
      this.taskIdToExecutorId = new HashMap<Object, String>();
      this.hasReceivedTask = false;
      this.org$apache$spark$scheduler$TaskSchedulerImpl$$hasLaunchedTask = false;
      this.starvationTimer = new Timer(true);
      this.nextTaskId = new AtomicLong(0L);
      this.org$apache$spark$scheduler$TaskSchedulerImpl$$executorIdToRunningTaskIds = new HashMap<String, HashSet<Object>>();
      this.hostToExecutors = new HashMap<String, HashSet<String>>();
      this.hostsByRack = new HashMap<String, HashSet<String>>();
      this.executorIdToHost = new HashMap<String, String>();
      this.dagScheduler = null;
      this.backend = null;
      this.mapOutputTracker = (MapOutputTrackerMaster)SparkEnv$.MODULE$.get().mapOutputTracker();
      this.schedulableBuilder = null;
      this.schedulingModeConf = this.conf().get(TaskSchedulerImpl$.MODULE$.SCHEDULER_MODE_PROPERTY(), SchedulingMode$.MODULE$.FIFO().toString());
      this.schedulingMode = this.liftedTree1$1();
      this.rootPool = new Pool("", this.schedulingMode(), 0, 0);
      this.taskResultGetter = new TaskResultGetter(sc.env(), this);
   }

   public TaskSchedulerImpl(SparkContext sc) {
      this(sc, BoxesRunTime.unboxToInt(sc.conf().get(org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_FAILURES())), TaskSchedulerImpl$.MODULE$.$lessinit$greater$default$3());
   }
}
