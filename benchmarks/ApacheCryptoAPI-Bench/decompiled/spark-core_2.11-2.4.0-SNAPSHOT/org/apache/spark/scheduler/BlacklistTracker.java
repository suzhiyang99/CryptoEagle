package org.apache.spark.scheduler;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.spark.ExecutorAllocationClient;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Clock;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.MapLike;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.HashSet$;
import scala.collection.mutable.Iterable$;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.LongRef;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\r\rc!B\u0001\u0003\u0001\tQ!\u0001\u0005\"mC\u000e\\G.[:u)J\f7m[3s\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0016\u001b\u0005\u0019\"B\u0001\u000b\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\f\u0014\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0007\u0001\u0003\u0006\u0004%IAG\u0001\fY&\u001cH/\u001a8fe\n+8o\u0001\u0001\u0016\u0003m\u0001\"\u0001H\u000f\u000e\u0003\tI!A\b\u0002\u0003\u001f1Kg/\u001a'jgR,g.\u001a:CkND\u0001\u0002\t\u0001\u0003\u0002\u0003\u0006IaG\u0001\rY&\u001cH/\u001a8fe\n+8\u000f\t\u0005\tE\u0001\u0011\t\u0011)A\u0005G\u0005!1m\u001c8g!\t!S%D\u0001\u0005\u0013\t1CAA\u0005Ta\u0006\u00148nQ8oM\"A\u0001\u0006\u0001B\u0001B\u0003%\u0011&\u0001\tbY2|7-\u0019;j_:\u001cE.[3oiB\u0019AB\u000b\u0017\n\u0005-j!AB(qi&|g\u000e\u0005\u0002%[%\u0011a\u0006\u0002\u0002\u0019\u000bb,7-\u001e;pe\u0006cGn\\2bi&|gn\u00117jK:$\b\u0002\u0003\u0019\u0001\u0005\u0003\u0005\u000b\u0011B\u0019\u0002\u000b\rdwnY6\u0011\u0005I*T\"A\u001a\u000b\u0005Q\"\u0011\u0001B;uS2L!AN\u001a\u0003\u000b\rcwnY6\t\u000ba\u0002A\u0011A\u001d\u0002\rqJg.\u001b;?)\u0015Q4\bP\u001f?!\ta\u0002\u0001C\u0003\u0019o\u0001\u00071\u0004C\u0003#o\u0001\u00071\u0005C\u0003)o\u0001\u0007\u0011\u0006C\u00041oA\u0005\t\u0019A\u0019\t\u000ba\u0002A\u0011\u0001!\u0015\u0007i\ne\tC\u0003C\u007f\u0001\u00071)\u0001\u0002tGB\u0011A\u0005R\u0005\u0003\u000b\u0012\u0011Ab\u00159be.\u001cuN\u001c;fqRDQ\u0001K A\u0002%Bq\u0001\u0013\u0001C\u0002\u0013%\u0011*A\u000bN\u0003b{f)Q%M+J+5k\u0018)F%~+\u0005,R\"\u0016\u0003)\u0003\"\u0001D&\n\u00051k!aA%oi\"1a\n\u0001Q\u0001\n)\u000ba#T!Y?\u001a\u000b\u0015\nT+S\u000bN{\u0006+\u0012*`\u000bb+5\t\t\u0005\b!\u0002\u0011\r\u0011\"\u0003J\u0003ai\u0015\tW0G\u0003&cU\tR0F1\u0016\u001bu\fU#S?:{E)\u0012\u0005\u0007%\u0002\u0001\u000b\u0011\u0002&\u000235\u000b\u0005l\u0018$B\u00132+EiX#Y\u000b\u000e{\u0006+\u0012*`\u001d>#U\t\t\u0005\b)\u0002\u0011\r\u0011\"\u0001V\u0003a\u0011E*Q\"L\u0019&\u001bFk\u0018+J\u001b\u0016{U\u000bV0N\u00132c\u0015jU\u000b\u0002-B\u0011AbV\u0005\u000316\u0011A\u0001T8oO\"1!\f\u0001Q\u0001\nY\u000b\u0011D\u0011'B\u0007.c\u0015j\u0015+`)&kUiT+U?6KE\nT%TA!9A\f\u0001b\u0001\n\u0013i\u0016a\b\"M\u0003\u000e[E*S*U?\u001a+Ek\u0011%`\r\u0006KE*\u0016*F?\u0016s\u0015I\u0011'F\tV\ta\f\u0005\u0002\r?&\u0011\u0001-\u0004\u0002\b\u0005>|G.Z1o\u0011\u0019\u0011\u0007\u0001)A\u0005=\u0006\u0001#\tT!D\u00172K5\u000bV0G\u000bR\u001b\u0005j\u0018$B\u00132+&+R0F\u001d\u0006\u0013E*\u0012#!\u0011\u001d!\u0007A1A\u0005\n\u0015\fq#\u001a=fGV$xN]%e)>4\u0015-\u001b7ve\u0016d\u0015n\u001d;\u0016\u0003\u0019\u0004Ba\u001a7ok6\t\u0001N\u0003\u0002jU\u00069Q.\u001e;bE2,'BA6\u000e\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003[\"\u0014q\u0001S1tQ6\u000b\u0007\u000f\u0005\u0002pe:\u0011A\u0002]\u0005\u0003c6\ta\u0001\u0015:fI\u00164\u0017BA:u\u0005\u0019\u0019FO]5oO*\u0011\u0011/\u0004\t\u0003m^l\u0011\u0001\u0001\u0004\u0006q\u0002\u0011!!\u001f\u0002\u0014\u000bb,7-\u001e;pe\u001a\u000b\u0017\u000e\\;sK2K7\u000f^\n\u0004o.\t\u0002\"\u0002\u001dx\t\u0003YH#A;\u0007\tu<HI \u0002\u0007)\u0006\u001c8.\u00133\u0014\u000bq\\q0!\u0002\u0011\u00071\t\t!C\u0002\u0002\u00045\u0011q\u0001\u0015:pIV\u001cG\u000fE\u0002\r\u0003\u000fI1!!\u0003\u000e\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011%\ti\u0001 BK\u0002\u0013\u0005\u0011*A\u0003ti\u0006<W\rC\u0005\u0002\u0012q\u0014\t\u0012)A\u0005\u0015\u000611\u000f^1hK\u0002B\u0011\"!\u0006}\u0005+\u0007I\u0011A%\u0002\u0019M$\u0018mZ3BiR,W\u000e\u001d;\t\u0013\u0005eAP!E!\u0002\u0013Q\u0015!D:uC\u001e,\u0017\t\u001e;f[B$\b\u0005C\u0005\u0002\u001eq\u0014)\u001a!C\u0001\u0013\u0006IA/Y:l\u0013:$W\r\u001f\u0005\n\u0003Ca(\u0011#Q\u0001\n)\u000b!\u0002^1tW&sG-\u001a=!\u0011\u0019AD\u0010\"\u0001\u0002&QA\u0011qEA\u0016\u0003[\ty\u0003E\u0002\u0002*ql\u0011a\u001e\u0005\b\u0003\u001b\t\u0019\u00031\u0001K\u0011\u001d\t)\"a\tA\u0002)Cq!!\b\u0002$\u0001\u0007!\nC\u0005\u00024q\f\t\u0011\"\u0001\u00026\u0005!1m\u001c9z)!\t9#a\u000e\u0002:\u0005m\u0002\"CA\u0007\u0003c\u0001\n\u00111\u0001K\u0011%\t)\"!\r\u0011\u0002\u0003\u0007!\nC\u0005\u0002\u001e\u0005E\u0002\u0013!a\u0001\u0015\"I\u0011q\b?\u0012\u0002\u0013\u0005\u0011\u0011I\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\t\t\u0019EK\u0002K\u0003\u000bZ#!a\u0012\u0011\t\u0005%\u00131K\u0007\u0003\u0003\u0017RA!!\u0014\u0002P\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003#j\u0011AC1o]>$\u0018\r^5p]&!\u0011QKA&\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\n\u00033b\u0018\u0013!C\u0001\u0003\u0003\nabY8qs\u0012\"WMZ1vYR$#\u0007C\u0005\u0002^q\f\n\u0011\"\u0001\u0002B\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\u001a\u0004\"CA1y\u0006\u0005I\u0011IA2\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\u0011\u0011Q\r\t\u0005\u0003O\n\t(\u0004\u0002\u0002j)!\u00111NA7\u0003\u0011a\u0017M\\4\u000b\u0005\u0005=\u0014\u0001\u00026bm\u0006L1a]A5\u0011!\t)\b`A\u0001\n\u0003I\u0015\u0001\u00049s_\u0012,8\r^!sSRL\b\"CA=y\u0006\u0005I\u0011AA>\u00039\u0001(o\u001c3vGR,E.Z7f]R$B!! \u0002\u0004B\u0019A\"a \n\u0007\u0005\u0005UBA\u0002B]fD\u0011\"!\"\u0002x\u0005\u0005\t\u0019\u0001&\u0002\u0007a$\u0013\u0007C\u0005\u0002\nr\f\t\u0011\"\u0011\u0002\f\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/\u0006\u0002\u0002\u000eB1\u0011qRAI\u0003{j\u0011A[\u0005\u0004\u0003'S'\u0001C%uKJ\fGo\u001c:\t\u0013\u0005]E0!A\u0005\u0002\u0005e\u0015\u0001C2b]\u0016\u000bX/\u00197\u0015\u0007y\u000bY\n\u0003\u0006\u0002\u0006\u0006U\u0015\u0011!a\u0001\u0003{B\u0011\"a(}\u0003\u0003%\t%!)\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012A\u0013\u0005\n\u0003Kc\u0018\u0011!C!\u0003O\u000b\u0001\u0002^8TiJLgn\u001a\u000b\u0003\u0003KB\u0011\"a+}\u0003\u0003%\t%!,\u0002\r\u0015\fX/\u00197t)\rq\u0016q\u0016\u0005\u000b\u0003\u000b\u000bI+!AA\u0002\u0005ut!CAZo\u0006\u0005\t\u0012BA[\u0003\u0019!\u0016m]6JIB!\u0011\u0011FA\\\r!ix/!A\t\n\u0005e6CBA\\\u0003w\u000b)\u0001E\u0005\u0002>\u0006\r'J\u0013&\u0002(5\u0011\u0011q\u0018\u0006\u0004\u0003\u0003l\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003\u000b\fyLA\tBEN$(/Y2u\rVt7\r^5p]NBq\u0001OA\\\t\u0003\tI\r\u0006\u0002\u00026\"Q\u0011QUA\\\u0003\u0003%)%a*\t\u0015\u0005=\u0017qWA\u0001\n\u0003\u000b\t.A\u0003baBd\u0017\u0010\u0006\u0005\u0002(\u0005M\u0017Q[Al\u0011\u001d\ti!!4A\u0002)Cq!!\u0006\u0002N\u0002\u0007!\nC\u0004\u0002\u001e\u00055\u0007\u0019\u0001&\t\u0015\u0005m\u0017qWA\u0001\n\u0003\u000bi.A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005}\u0017q\u001d\t\u0005\u0019)\n\t\u000f\u0005\u0004\r\u0003GT%JS\u0005\u0004\u0003Kl!A\u0002+va2,7\u0007\u0003\u0006\u0002j\u0006e\u0017\u0011!a\u0001\u0003O\t1\u0001\u001f\u00131\u0011%\tio\u001ea\u0001\n\u0013\ty/\u0001\fgC&dWO]3t\u0003:$W\t\u001f9jef$\u0016.\\3t+\t\t\t\u0010E\u0003h\u0003g\f90C\u0002\u0002v\"\u00141\"\u0011:sCf\u0014UO\u001a4feB1A\"!?\u0002(YK1!a?\u000e\u0005\u0019!V\u000f\u001d7fe!I\u0011q`<A\u0002\u0013%!\u0011A\u0001\u001bM\u0006LG.\u001e:fg\u0006sG-\u0012=qSJLH+[7fg~#S-\u001d\u000b\u0005\u0005\u0007\u0011I\u0001E\u0002\r\u0005\u000bI1Aa\u0002\u000e\u0005\u0011)f.\u001b;\t\u0015\u0005\u0015\u0015Q`A\u0001\u0002\u0004\t\t\u0010\u0003\u0005\u0003\u000e]\u0004\u000b\u0015BAy\u0003]1\u0017-\u001b7ve\u0016\u001c\u0018I\u001c3FqBL'/\u001f+j[\u0016\u001c\b\u0005\u0003\u0005\u0003\u0012]\u0004\r\u0011\"\u0003V\u00035i\u0017N\\#ya&\u0014\u0018\u0010V5nK\"I!QC<A\u0002\u0013%!qC\u0001\u0012[&tW\t\u001f9jef$\u0016.\\3`I\u0015\fH\u0003\u0002B\u0002\u00053A\u0011\"!\"\u0003\u0014\u0005\u0005\t\u0019\u0001,\t\u000f\tuq\u000f)Q\u0005-\u0006qQ.\u001b8FqBL'/\u001f+j[\u0016\u0004\u0003b\u0002B\u0011o\u0012\u0005!1E\u0001\fC\u0012$g)Y5mkJ,7\u000f\u0006\u0005\u0003\u0004\t\u0015\"q\u0005B\u0015\u0011\u001d\tiAa\bA\u0002)Cq!!\u0006\u0003 \u0001\u0007!\n\u0003\u0005\u0003,\t}\u0001\u0019\u0001B\u0017\u0003E1\u0017-\u001b7ve\u0016\u001c\u0018J\u001c+bg.\u001cV\r\u001e\t\u00049\t=\u0012b\u0001B\u0019\u0005\tIR\t_3dkR|'OR1jYV\u0014Xm]%o)\u0006\u001c8nU3u\u0011\u0019\u0011)d\u001eC\u0001\u0013\u0006)b.^7V]&\fX/\u001a+bg.4\u0015-\u001b7ve\u0016\u001c\bB\u0002B\u001do\u0012\u0005Q,A\u0004jg\u0016k\u0007\u000f^=\t\u000f\tur\u000f\"\u0001\u0003@\u0005iBM]8q\r\u0006LG.\u001e:fg^KG\u000f\u001b+j[\u0016|W\u000f\u001e\"fM>\u0014X\r\u0006\u0003\u0003\u0004\t\u0005\u0003b\u0002B\"\u0005w\u0001\rAV\u0001\u000bIJ|\u0007OQ3g_J,\u0007bBASo\u0012\u0005#q\t\u000b\u0002]\"9!1\n\u0001!\u0002\u00131\u0017\u0001G3yK\u000e,Ho\u001c:JIR{g)Y5mkJ,G*[:uA!I!q\n\u0001C\u0002\u0013\u0005!\u0011K\u0001\u001cKb,7-\u001e;pe&#Gk\u001c\"mC\u000e\\G.[:u'R\fG/^:\u0016\u0005\tM\u0003#B4m]\nU\u0003c\u0001\u000f\u0003X%\u0019!\u0011\f\u0002\u0003'\tc\u0017mY6mSN$X\rZ#yK\u000e,Ho\u001c:\t\u0011\tu\u0003\u0001)A\u0005\u0005'\nA$\u001a=fGV$xN]%e)>\u0014E.Y2lY&\u001cHo\u0015;biV\u001c\b\u0005C\u0005\u0003b\u0001\u0011\r\u0011\"\u0001\u0003d\u0005Ybn\u001c3f\u0013\u0012$vN\u00117bG.d\u0017n\u001d;FqBL'/\u001f+j[\u0016,\"A!\u001a\u0011\t\u001ddgN\u0016\u0005\t\u0005S\u0002\u0001\u0015!\u0003\u0003f\u0005abn\u001c3f\u0013\u0012$vN\u00117bG.d\u0017n\u001d;FqBL'/\u001f+j[\u0016\u0004\u0003\"\u0003B7\u0001\t\u0007I\u0011\u0002B8\u00039yfn\u001c3f\u00052\f7m\u001b7jgR,\"A!\u001d\u0011\r\tM$q\u0010BB\u001b\t\u0011)H\u0003\u0003\u0003x\te\u0014AB1u_6L7M\u0003\u0003\u0003|\tu\u0014AC2p]\u000e,(O]3oi*\u0019A'!\u001c\n\t\t\u0005%Q\u000f\u0002\u0010\u0003R|W.[2SK\u001a,'/\u001a8dKB!qN!\"o\u0013\r\u00119\t\u001e\u0002\u0004'\u0016$\b\u0002\u0003BF\u0001\u0001\u0006IA!\u001d\u0002\u001f}sw\u000eZ3CY\u0006\u001c7\u000e\\5ti\u0002B\u0001Ba$\u0001\u0001\u0004%\t!V\u0001\u000f]\u0016DH/\u0012=qSJLH+[7f\u0011%\u0011\u0019\n\u0001a\u0001\n\u0003\u0011)*\u0001\noKb$X\t\u001f9jef$\u0016.\\3`I\u0015\fH\u0003\u0002B\u0002\u0005/C\u0011\"!\"\u0003\u0012\u0006\u0005\t\u0019\u0001,\t\u000f\tm\u0005\u0001)Q\u0005-\u0006ya.\u001a=u\u000bb\u0004\u0018N]=US6,\u0007\u0005C\u0005\u0003 \u0002\u0011\r\u0011\"\u0001\u0003\"\u00061bn\u001c3f)>\u0014E.Y2lY&\u001cH/\u001a3Fq\u0016\u001c7/\u0006\u0002\u0003$B)q\r\u001c8\u0003&B!qMa*o\u0013\r\u0011I\u000b\u001b\u0002\b\u0011\u0006\u001c\bnU3u\u0011!\u0011i\u000b\u0001Q\u0001\n\t\r\u0016a\u00068pI\u0016$vN\u00117bG.d\u0017n\u001d;fI\u0016CXmY:!\u0011\u001d\u0011\t\f\u0001C\u0001\u0005g\u000bQ#\u00199qYf\u0014E.Y2lY&\u001cH\u000fV5nK>,H\u000f\u0006\u0002\u0003\u0004!9!q\u0017\u0001\u0005\n\tM\u0016\u0001F;qI\u0006$XMT3yi\u0016C\b/\u001b:z)&lW\rC\u0004\u0003<\u0002!IA!0\u0002/-LG\u000e\u001c\"mC\u000e\\G.[:uK\u0012,\u00050Z2vi>\u0014H\u0003\u0002B\u0002\u0005\u007fCqA!1\u0003:\u0002\u0007a.\u0001\u0003fq\u0016\u001c\u0007b\u0002Bc\u0001\u0011%!qY\u0001\u001fW&dG.\u0012=fGV$xN]:P]\nc\u0017mY6mSN$X\r\u001a(pI\u0016$BAa\u0001\u0003J\"9!1\u001aBb\u0001\u0004q\u0017\u0001\u00028pI\u0016DqAa4\u0001\t\u0003\u0011\t.\u0001\u0010va\u0012\fG/\u001a\"mC\u000e\\G.[:u\r>\u0014h)\u001a;dQ\u001a\u000b\u0017\u000e\\;sKR1!1\u0001Bj\u0005/DqA!6\u0003N\u0002\u0007a.\u0001\u0003i_N$\bb\u0002Ba\u0005\u001b\u0004\rA\u001c\u0005\b\u00057\u0004A\u0011\u0001Bo\u0003\r*\b\u000fZ1uK\nc\u0017mY6mSN$hi\u001c:Tk\u000e\u001cWm]:gk2$\u0016m]6TKR$\u0002Ba\u0001\u0003`\n\r(q\u001d\u0005\b\u0005C\u0014I\u000e1\u0001K\u0003\u001d\u0019H/Y4f\u0013\u0012DqA!:\u0003Z\u0002\u0007!*\u0001\bti\u0006<W-\u0011;uK6\u0004H/\u00133\t\u0011\t%(\u0011\u001ca\u0001\u0005W\faBZ1jYV\u0014Xm\u001d\"z\u000bb,7\rE\u0003hY:\u0014i\u0003C\u0004\u0003p\u0002!\tA!=\u0002+%\u001cX\t_3dkR|'O\u00117bG.d\u0017n\u001d;fIR\u0019aLa=\t\u000f\tU(Q\u001ea\u0001]\u0006QQ\r_3dkR|'/\u00133\t\u000f\te\b\u0001\"\u0001\u0003|\u0006ian\u001c3f\u00052\f7m\u001b7jgR$\"Aa!\t\u000f\t}\b\u0001\"\u0001\u0004\u0002\u0005\t\u0012n\u001d(pI\u0016\u0014E.Y2lY&\u001cH/\u001a3\u0015\u0007y\u001b\u0019\u0001C\u0004\u0003L\nu\b\u0019\u00018\t\u000f\r\u001d\u0001\u0001\"\u0001\u0004\n\u0005)\u0002.\u00198eY\u0016\u0014V-\\8wK\u0012,\u00050Z2vi>\u0014H\u0003\u0002B\u0002\u0007\u0017AqA!>\u0004\u0006\u0001\u0007an\u0002\u0005\u0004\u0010\tA\tAAB\t\u0003A\u0011E.Y2lY&\u001cH\u000f\u0016:bG.,'\u000fE\u0002\u001d\u0007'1q!\u0001\u0002\t\u0002\t\u0019)b\u0005\u0003\u0004\u0014-\t\u0002b\u0002\u001d\u0004\u0014\u0011\u00051\u0011\u0004\u000b\u0003\u0007#A!b!\b\u0004\u0014\t\u0007I\u0011BA2\u0003=!UIR!V\u0019R{F+S'F\u001fV#\u0006\"CB\u0011\u0007'\u0001\u000b\u0011BA3\u0003A!UIR!V\u0019R{F+S'F\u001fV#\u0006\u0005\u0003\u0005\u0004&\rMA\u0011AB\u0014\u0003II7O\u00117bG.d\u0017n\u001d;F]\u0006\u0014G.\u001a3\u0015\u0007y\u001bI\u0003\u0003\u0004#\u0007G\u0001\ra\t\u0005\t\u0007[\u0019\u0019\u0002\"\u0001\u00040\u0005\u0019r-\u001a;CY\u0006\u001c7\u000e\\5tiRKW.Z8viR\u0019ak!\r\t\r\t\u001aY\u00031\u0001$\u0011!\u0019)da\u0005\u0005\u0002\r]\u0012A\u0006<bY&$\u0017\r^3CY\u0006\u001c7\u000e\\5ti\u000e{gNZ:\u0015\t\t\r1\u0011\b\u0005\u0007E\rM\u0002\u0019A\u0012\t\u0015\ru21CI\u0001\n\u0003\u0019y$A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H\u0005N\u000b\u0003\u0007\u0003R3!MA#\u0001"
)
public class BlacklistTracker implements Logging {
   private final LiveListenerBus org$apache$spark$scheduler$BlacklistTracker$$listenerBus;
   private final SparkConf conf;
   private final Option<ExecutorAllocationClient> allocationClient;
   private final Clock clock;
   private final int org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILURES_PER_EXEC;
   private final int org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILED_EXEC_PER_NODE;
   private final long BLACKLIST_TIMEOUT_MILLIS;
   private final boolean BLACKLIST_FETCH_FAILURE_ENABLED;
   private final HashMap<String, ExecutorFailureList> org$apache$spark$scheduler$BlacklistTracker$$executorIdToFailureList;
   private final HashMap<String, BlacklistedExecutor> executorIdToBlacklistStatus;
   private final HashMap<String, Object> nodeIdToBlacklistExpiryTime;
   private final AtomicReference<Set<String>> org$apache$spark$scheduler$BlacklistTracker$$_nodeBlacklist;
   private long nextExpiryTime;
   private final HashMap<String, HashSet<String>> nodeToBlacklistedExecs;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Clock $lessinit$greater$default$4() {
      return BlacklistTracker$.MODULE$.$lessinit$greater$default$4();
   }

   public static void validateBlacklistConfs(SparkConf var0) {
      BlacklistTracker$.MODULE$.validateBlacklistConfs(var0);
   }

   public static long getBlacklistTimeout(SparkConf var0) {
      return BlacklistTracker$.MODULE$.getBlacklistTimeout(var0);
   }

   public static boolean isBlacklistEnabled(SparkConf var0) {
      return BlacklistTracker$.MODULE$.isBlacklistEnabled(var0);
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

   public LiveListenerBus org$apache$spark$scheduler$BlacklistTracker$$listenerBus() {
      return this.org$apache$spark$scheduler$BlacklistTracker$$listenerBus;
   }

   public int org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILURES_PER_EXEC() {
      return this.org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILURES_PER_EXEC;
   }

   public int org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILED_EXEC_PER_NODE() {
      return this.org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILED_EXEC_PER_NODE;
   }

   public long BLACKLIST_TIMEOUT_MILLIS() {
      return this.BLACKLIST_TIMEOUT_MILLIS;
   }

   private boolean BLACKLIST_FETCH_FAILURE_ENABLED() {
      return this.BLACKLIST_FETCH_FAILURE_ENABLED;
   }

   public HashMap<String, ExecutorFailureList> org$apache$spark$scheduler$BlacklistTracker$$executorIdToFailureList() {
      return this.org$apache$spark$scheduler$BlacklistTracker$$executorIdToFailureList;
   }

   public HashMap<String, BlacklistedExecutor> executorIdToBlacklistStatus() {
      return this.executorIdToBlacklistStatus;
   }

   public HashMap<String, Object> nodeIdToBlacklistExpiryTime() {
      return this.nodeIdToBlacklistExpiryTime;
   }

   public AtomicReference<Set<String>> org$apache$spark$scheduler$BlacklistTracker$$_nodeBlacklist() {
      return this.org$apache$spark$scheduler$BlacklistTracker$$_nodeBlacklist;
   }

   public long nextExpiryTime() {
      return this.nextExpiryTime;
   }

   public void nextExpiryTime_$eq(long x$1) {
      this.nextExpiryTime = x$1;
   }

   public HashMap<String, HashSet<String>> nodeToBlacklistedExecs() {
      return this.nodeToBlacklistedExecs;
   }

   public void applyBlacklistTimeout() {
      long now = this.clock.getTimeMillis();
      if (now > this.nextExpiryTime()) {
         Iterable execsToUnblacklist = ((MapLike)this.executorIdToBlacklistStatus().filter(new Serializable(this, now) {
            public static final long serialVersionUID = 0L;
            private final long now$1;

            public final boolean apply(Tuple2<String, BlacklistedExecutor> x$1) {
               return ((BlacklistedExecutor)x$1._2()).expiryTime() < this.now$1;
            }

            public {
               this.now$1 = now$1;
            }
         })).keys();
         if (execsToUnblacklist.nonEmpty()) {
            this.logInfo(new Serializable(this, execsToUnblacklist) {
               public static final long serialVersionUID = 0L;
               private final Iterable execsToUnblacklist$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing executors ", " from blacklist because the blacklist "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.execsToUnblacklist$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"for those executors has timed out"})))).s(Nil$.MODULE$)).toString();
               }

               public {
                  this.execsToUnblacklist$1 = execsToUnblacklist$1;
               }
            });
            execsToUnblacklist.foreach(new Serializable(this, now) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlacklistTracker $outer;
               private final long now$1;

               public final Object apply(String exec) {
                  BlacklistedExecutor status = (BlacklistedExecutor)this.$outer.executorIdToBlacklistStatus().remove(exec).get();
                  HashSet failedExecsOnNode = (HashSet)this.$outer.nodeToBlacklistedExecs().apply(status.node());
                  this.$outer.org$apache$spark$scheduler$BlacklistTracker$$listenerBus().post(new SparkListenerExecutorUnblacklisted(this.now$1, exec));
                  failedExecsOnNode.remove(exec);
                  return failedExecsOnNode.isEmpty() ? this.$outer.nodeToBlacklistedExecs().remove(status.node()) : BoxedUnit.UNIT;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.now$1 = now$1;
                  }
               }
            });
         }

         Iterable nodesToUnblacklist = ((MapLike)this.nodeIdToBlacklistExpiryTime().filter(new Serializable(this, now) {
            public static final long serialVersionUID = 0L;
            private final long now$1;

            public final boolean apply(Tuple2<String, Object> x$2) {
               return x$2._2$mcJ$sp() < this.now$1;
            }

            public {
               this.now$1 = now$1;
            }
         })).keys();
         if (nodesToUnblacklist.nonEmpty()) {
            this.logInfo(new Serializable(this, nodesToUnblacklist) {
               public static final long serialVersionUID = 0L;
               private final Iterable nodesToUnblacklist$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing nodes ", " from blacklist because the blacklist "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.nodesToUnblacklist$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"has timed out"})))).s(Nil$.MODULE$)).toString();
               }

               public {
                  this.nodesToUnblacklist$1 = nodesToUnblacklist$1;
               }
            });
            nodesToUnblacklist.foreach(new Serializable(this, now) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlacklistTracker $outer;
               private final long now$1;

               public final void apply(String node) {
                  this.$outer.nodeIdToBlacklistExpiryTime().remove(node);
                  this.$outer.org$apache$spark$scheduler$BlacklistTracker$$listenerBus().post(new SparkListenerNodeUnblacklisted(this.now$1, node));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.now$1 = now$1;
                  }
               }
            });
            this.org$apache$spark$scheduler$BlacklistTracker$$_nodeBlacklist().set(this.nodeIdToBlacklistExpiryTime().keySet().toSet());
         }

         this.org$apache$spark$scheduler$BlacklistTracker$$updateNextExpiryTime();
      }

   }

   public void org$apache$spark$scheduler$BlacklistTracker$$updateNextExpiryTime() {
      long execMinExpiry = this.executorIdToBlacklistStatus().nonEmpty() ? BoxesRunTime.unboxToLong(((TraversableOnce)this.executorIdToBlacklistStatus().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(Tuple2<String, BlacklistedExecutor> x$3) {
            return ((BlacklistedExecutor)x$3._2()).expiryTime();
         }
      }, Iterable$.MODULE$.canBuildFrom())).min(Ordering.Long$.MODULE$)) : Long.MAX_VALUE;
      long nodeMinExpiry = this.nodeIdToBlacklistExpiryTime().nonEmpty() ? BoxesRunTime.unboxToLong(this.nodeIdToBlacklistExpiryTime().values().min(Ordering.Long$.MODULE$)) : Long.MAX_VALUE;
      this.nextExpiryTime_$eq(scala.math.package$.MODULE$.min(execMinExpiry, nodeMinExpiry));
   }

   public void org$apache$spark$scheduler$BlacklistTracker$$killBlacklistedExecutor(String exec) {
      if (BoxesRunTime.unboxToBoolean(this.conf.get(org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_KILL_ENABLED()))) {
         Option var2 = this.allocationClient;
         if (var2 instanceof Some) {
            Some var3 = (Some)var2;
            ExecutorAllocationClient a = (ExecutorAllocationClient)var3.x();
            this.logInfo(new Serializable(this, exec) {
               public static final long serialVersionUID = 0L;
               private final String exec$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Killing blacklisted executor id ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.exec$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"since ", " is set."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_KILL_ENABLED().key()}))).toString();
               }

               public {
                  this.exec$1 = exec$1;
               }
            });
            a.killExecutors((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{exec}))), true, true);
            BoxedUnit var5 = BoxedUnit.UNIT;
         } else {
            if (!None$.MODULE$.equals(var2)) {
               throw new MatchError(var2);
            }

            this.logWarning(new Serializable(this, exec) {
               public static final long serialVersionUID = 0L;
               private final String exec$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not attempting to kill blacklisted executor id ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.exec$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"since allocation client is not defined."})))).s(Nil$.MODULE$)).toString();
               }

               public {
                  this.exec$1 = exec$1;
               }
            });
            BoxedUnit var6 = BoxedUnit.UNIT;
         }
      }

   }

   public void org$apache$spark$scheduler$BlacklistTracker$$killExecutorsOnBlacklistedNode(String node) {
      if (BoxesRunTime.unboxToBoolean(this.conf.get(org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_KILL_ENABLED()))) {
         Option var2 = this.allocationClient;
         if (var2 instanceof Some) {
            Some var3 = (Some)var2;
            ExecutorAllocationClient a = (ExecutorAllocationClient)var3.x();
            this.logInfo(new Serializable(this, node) {
               public static final long serialVersionUID = 0L;
               private final String node$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Killing all executors on blacklisted host ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.node$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"since ", " is set."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_KILL_ENABLED().key()}))).toString();
               }

               public {
                  this.node$1 = node$1;
               }
            });
            if (!a.killExecutorsOnHost(node)) {
               this.logError(new Serializable(this, node) {
                  public static final long serialVersionUID = 0L;
                  private final String node$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Killing executors on node ", " failed."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.node$1}));
                  }

                  public {
                     this.node$1 = node$1;
                  }
               });
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               BoxedUnit var6 = BoxedUnit.UNIT;
            }
         } else {
            if (!None$.MODULE$.equals(var2)) {
               throw new MatchError(var2);
            }

            this.logWarning(new Serializable(this, node) {
               public static final long serialVersionUID = 0L;
               private final String node$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not attempting to kill executors on blacklisted host ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.node$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"since allocation client is not defined."})))).s(Nil$.MODULE$)).toString();
               }

               public {
                  this.node$1 = node$1;
               }
            });
            BoxedUnit var5 = BoxedUnit.UNIT;
         }
      }

   }

   public void updateBlacklistForFetchFailure(String host, String exec) {
      if (this.BLACKLIST_FETCH_FAILURE_ENABLED()) {
         long now = this.clock.getTimeMillis();
         long expiryTimeForNewBlacklists = now + this.BLACKLIST_TIMEOUT_MILLIS();
         if (BoxesRunTime.unboxToBoolean(this.conf.get(org.apache.spark.internal.config.package$.MODULE$.SHUFFLE_SERVICE_ENABLED()))) {
            if (!this.nodeIdToBlacklistExpiryTime().contains(host)) {
               this.logInfo(new Serializable(this, host) {
                  public static final long serialVersionUID = 0L;
                  private final String host$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"blacklisting node ", " due to fetch failure of external shuffle service"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.host$1}));
                  }

                  public {
                     this.host$1 = host$1;
                  }
               });
               this.nodeIdToBlacklistExpiryTime().put(host, BoxesRunTime.boxToLong(expiryTimeForNewBlacklists));
               this.org$apache$spark$scheduler$BlacklistTracker$$listenerBus().post(new SparkListenerNodeBlacklisted(now, host, 1));
               this.org$apache$spark$scheduler$BlacklistTracker$$_nodeBlacklist().set(this.nodeIdToBlacklistExpiryTime().keySet().toSet());
               this.org$apache$spark$scheduler$BlacklistTracker$$killExecutorsOnBlacklistedNode(host);
               this.org$apache$spark$scheduler$BlacklistTracker$$updateNextExpiryTime();
            }
         } else if (!this.executorIdToBlacklistStatus().contains(exec)) {
            this.logInfo(new Serializable(this, exec) {
               public static final long serialVersionUID = 0L;
               private final String exec$2;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Blacklisting executor ", " due to fetch failure"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.exec$2}));
               }

               public {
                  this.exec$2 = exec$2;
               }
            });
            this.executorIdToBlacklistStatus().put(exec, new BlacklistedExecutor(host, expiryTimeForNewBlacklists));
            this.org$apache$spark$scheduler$BlacklistTracker$$listenerBus().post(new SparkListenerExecutorBlacklisted(now, exec, 1));
            this.org$apache$spark$scheduler$BlacklistTracker$$updateNextExpiryTime();
            this.org$apache$spark$scheduler$BlacklistTracker$$killBlacklistedExecutor(exec);
            HashSet blacklistedExecsOnNode = (HashSet)this.nodeToBlacklistedExecs().getOrElseUpdate(exec, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final HashSet<String> apply() {
                  return (HashSet)HashSet$.MODULE$.apply(Nil$.MODULE$);
               }
            });
            blacklistedExecsOnNode.$plus$eq(exec);
         }
      }

   }

   public void updateBlacklistForSuccessfulTaskSet(int stageId, int stageAttemptId, HashMap<String, ExecutorFailuresInTaskSet> failuresByExec) {
      long now = this.clock.getTimeMillis();
      failuresByExec.foreach(new Serializable(this, stageId, stageAttemptId, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlacklistTracker $outer;
         private final int stageId$1;
         private final int stageAttemptId$1;
         private final long now$2;

         public final void apply(Tuple2<String, ExecutorFailuresInTaskSet> x0$1) {
            if (x0$1 == null) {
               throw new MatchError(x0$1);
            } else {
               String exec = (String)x0$1._1();
               ExecutorFailuresInTaskSet failuresInTaskSet = (ExecutorFailuresInTaskSet)x0$1._2();
               ExecutorFailureList appFailuresOnExecutor = (ExecutorFailureList)this.$outer.org$apache$spark$scheduler$BlacklistTracker$$executorIdToFailureList().getOrElseUpdate(exec, new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final ExecutorFailureList apply() {
                     return this.$outer.org$apache$spark$scheduler$BlacklistTracker$$anonfun$$$outer().new ExecutorFailureList();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               appFailuresOnExecutor.addFailures(this.stageId$1, this.stageAttemptId$1, failuresInTaskSet);
               appFailuresOnExecutor.dropFailuresWithTimeoutBefore(this.now$2);
               int newTotal = appFailuresOnExecutor.numUniqueTaskFailures();
               long expiryTimeForNewBlacklists = this.now$2 + this.$outer.BLACKLIST_TIMEOUT_MILLIS();
               if (newTotal >= this.$outer.org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILURES_PER_EXEC() && !this.$outer.executorIdToBlacklistStatus().contains(exec)) {
                  this.$outer.logInfo(new Serializable(this, exec, newTotal) {
                     public static final long serialVersionUID = 0L;
                     private final String exec$3;
                     private final int newTotal$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Blacklisting executor id: ", " because it has ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.exec$3, BoxesRunTime.boxToInteger(this.newTotal$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" task failures in successful task sets"})))).s(Nil$.MODULE$)).toString();
                     }

                     public {
                        this.exec$3 = exec$3;
                        this.newTotal$1 = newTotal$1;
                     }
                  });
                  String node = failuresInTaskSet.node();
                  this.$outer.executorIdToBlacklistStatus().put(exec, new BlacklistedExecutor(node, expiryTimeForNewBlacklists));
                  this.$outer.org$apache$spark$scheduler$BlacklistTracker$$listenerBus().post(new SparkListenerExecutorBlacklisted(this.now$2, exec, newTotal));
                  this.$outer.org$apache$spark$scheduler$BlacklistTracker$$executorIdToFailureList().remove(exec);
                  this.$outer.org$apache$spark$scheduler$BlacklistTracker$$updateNextExpiryTime();
                  this.$outer.org$apache$spark$scheduler$BlacklistTracker$$killBlacklistedExecutor(exec);
                  HashSet blacklistedExecsOnNode = (HashSet)this.$outer.nodeToBlacklistedExecs().getOrElseUpdate(node, new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final HashSet<String> apply() {
                        return (HashSet)HashSet$.MODULE$.apply(Nil$.MODULE$);
                     }
                  });
                  blacklistedExecsOnNode.$plus$eq(exec);
                  if (blacklistedExecsOnNode.size() >= this.$outer.org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILED_EXEC_PER_NODE() && !this.$outer.nodeIdToBlacklistExpiryTime().contains(node)) {
                     this.$outer.logInfo(new Serializable(this, node, blacklistedExecsOnNode) {
                        public static final long serialVersionUID = 0L;
                        private final String node$2;
                        private final HashSet blacklistedExecsOnNode$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Blacklisting node ", " because it has ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.node$2, BoxesRunTime.boxToInteger(this.blacklistedExecsOnNode$1.size())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"executors blacklisted: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blacklistedExecsOnNode$1}))).toString();
                        }

                        public {
                           this.node$2 = node$2;
                           this.blacklistedExecsOnNode$1 = blacklistedExecsOnNode$1;
                        }
                     });
                     this.$outer.nodeIdToBlacklistExpiryTime().put(node, BoxesRunTime.boxToLong(expiryTimeForNewBlacklists));
                     this.$outer.org$apache$spark$scheduler$BlacklistTracker$$listenerBus().post(new SparkListenerNodeBlacklisted(this.now$2, node, blacklistedExecsOnNode.size()));
                     this.$outer.org$apache$spark$scheduler$BlacklistTracker$$_nodeBlacklist().set(this.$outer.nodeIdToBlacklistExpiryTime().keySet().toSet());
                     this.$outer.org$apache$spark$scheduler$BlacklistTracker$$killExecutorsOnBlacklistedNode(node);
                     BoxedUnit var13 = BoxedUnit.UNIT;
                  } else {
                     BoxedUnit var12 = BoxedUnit.UNIT;
                  }
               } else {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               }

            }
         }

         // $FF: synthetic method
         public BlacklistTracker org$apache$spark$scheduler$BlacklistTracker$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.stageId$1 = stageId$1;
               this.stageAttemptId$1 = stageAttemptId$1;
               this.now$2 = now$2;
            }
         }
      });
   }

   public boolean isExecutorBlacklisted(String executorId) {
      return this.executorIdToBlacklistStatus().contains(executorId);
   }

   public Set<String> nodeBlacklist() {
      return (Set)this.org$apache$spark$scheduler$BlacklistTracker$$_nodeBlacklist().get();
   }

   public boolean isNodeBlacklisted(String node) {
      return this.nodeIdToBlacklistExpiryTime().contains(node);
   }

   public void handleRemovedExecutor(String executorId) {
      this.org$apache$spark$scheduler$BlacklistTracker$$executorIdToFailureList().$minus$eq(executorId);
   }

   public BlacklistTracker(LiveListenerBus listenerBus, SparkConf conf, Option<ExecutorAllocationClient> allocationClient, Clock clock) {
      this.org$apache$spark$scheduler$BlacklistTracker$$listenerBus = listenerBus;
      this.conf = conf;
      this.allocationClient = allocationClient;
      this.clock = clock;
      Logging$class.$init$(this);
      BlacklistTracker$.MODULE$.validateBlacklistConfs(conf);
      this.org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILURES_PER_EXEC = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.MAX_FAILURES_PER_EXEC()));
      this.org$apache$spark$scheduler$BlacklistTracker$$MAX_FAILED_EXEC_PER_NODE = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.MAX_FAILED_EXEC_PER_NODE()));
      this.BLACKLIST_TIMEOUT_MILLIS = BlacklistTracker$.MODULE$.getBlacklistTimeout(conf);
      this.BLACKLIST_FETCH_FAILURE_ENABLED = BoxesRunTime.unboxToBoolean(conf.get(org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_FETCH_FAILURE_ENABLED()));
      this.org$apache$spark$scheduler$BlacklistTracker$$executorIdToFailureList = new HashMap<String, ExecutorFailureList>();
      this.executorIdToBlacklistStatus = new HashMap<String, BlacklistedExecutor>();
      this.nodeIdToBlacklistExpiryTime = new HashMap<String, Object>();
      this.org$apache$spark$scheduler$BlacklistTracker$$_nodeBlacklist = new AtomicReference(Predef$.MODULE$.Set().apply(Nil$.MODULE$));
      this.nextExpiryTime = Long.MAX_VALUE;
      this.nodeToBlacklistedExecs = new HashMap<String, HashSet<String>>();
   }

   public BlacklistTracker(SparkContext sc, Option<ExecutorAllocationClient> allocationClient) {
      this(sc.listenerBus(), sc.conf(), allocationClient, BlacklistTracker$.MODULE$.$lessinit$greater$default$4());
   }

   public final class ExecutorFailureList implements Logging {
      private ArrayBuffer<Tuple2<TaskId, Object>> org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes;
      private long org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime;
      private volatile TaskId$ org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId$module;
      // $FF: synthetic field
      private final BlacklistTracker $outer;
      private transient Logger org$apache$spark$internal$Logging$$log_;

      private TaskId$ org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId$lzycompute() {
         synchronized(this) {
            if (this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId$module == null) {
               this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId$module = new TaskId$();
            }

            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         Object var3 = null;
         return this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId$module;
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

      public TaskId$ org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId() {
         return this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId$module == null ? this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId$lzycompute() : this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId$module;
      }

      public ArrayBuffer<Tuple2<TaskId, Object>> org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes() {
         return this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes;
      }

      private void org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes_$eq(ArrayBuffer<Tuple2<TaskId, Object>> x$1) {
         this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes = x$1;
      }

      public long org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime() {
         return this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime;
      }

      public void org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime_$eq(long x$1) {
         this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime = x$1;
      }

      public void addFailures(int stage, int stageAttempt, ExecutorFailuresInTaskSet failuresInTaskSet) {
         failuresInTaskSet.taskToFailureCountAndFailureTime().foreach(new Serializable(this, stage, stageAttempt) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorFailureList $outer;
            private final int stage$1;
            private final int stageAttempt$1;

            public final void apply(Tuple2<Object, Tuple2<Object, Object>> x0$2) {
               if (x0$2 != null) {
                  int taskIdx = x0$2._1$mcI$sp();
                  Tuple2 var4 = (Tuple2)x0$2._2();
                  if (var4 != null) {
                     long failureTime = var4._2$mcJ$sp();
                     long expiryTime = failureTime + this.$outer.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$$outer().BLACKLIST_TIMEOUT_MILLIS();
                     this.$outer.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes().$plus$eq(new Tuple2(this.$outer.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$TaskId().apply(this.stage$1, this.stageAttempt$1, taskIdx), BoxesRunTime.boxToLong(expiryTime)));
                     if (expiryTime < this.$outer.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime()) {
                        this.$outer.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime_$eq(expiryTime);
                        BoxedUnit var10000 = BoxedUnit.UNIT;
                     } else {
                        BoxedUnit var10 = BoxedUnit.UNIT;
                     }

                     return;
                  }
               }

               throw new MatchError(x0$2);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.stage$1 = stage$1;
                  this.stageAttempt$1 = stageAttempt$1;
               }
            }
         });
      }

      public int numUniqueTaskFailures() {
         return this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes().size();
      }

      public boolean isEmpty() {
         return this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes().isEmpty();
      }

      public void dropFailuresWithTimeoutBefore(long dropBefore) {
         if (this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime() < dropBefore) {
            LongRef newMinExpiry = LongRef.create(Long.MAX_VALUE);
            ArrayBuffer newFailures = new ArrayBuffer();
            this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes().foreach(new Serializable(this, dropBefore, newMinExpiry, newFailures) {
               public static final long serialVersionUID = 0L;
               private final long dropBefore$1;
               private final LongRef newMinExpiry$1;
               private final ArrayBuffer newFailures$1;

               public final void apply(Tuple2<TaskId, Object> x0$3) {
                  if (x0$3 != null) {
                     TaskId task = (TaskId)x0$3._1();
                     long expiryTime = x0$3._2$mcJ$sp();
                     if (expiryTime >= this.dropBefore$1) {
                        this.newFailures$1.$plus$eq(new Tuple2(task, BoxesRunTime.boxToLong(expiryTime)));
                        if (expiryTime < this.newMinExpiry$1.elem) {
                           this.newMinExpiry$1.elem = expiryTime;
                           BoxedUnit var10000 = BoxedUnit.UNIT;
                        } else {
                           BoxedUnit var7 = BoxedUnit.UNIT;
                        }
                     } else {
                        BoxedUnit var8 = BoxedUnit.UNIT;
                     }

                  } else {
                     throw new MatchError(x0$3);
                  }
               }

               public {
                  this.dropBefore$1 = dropBefore$1;
                  this.newMinExpiry$1 = newMinExpiry$1;
                  this.newFailures$1 = newFailures$1;
               }
            });
            this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes_$eq(newFailures);
            this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime_$eq(newMinExpiry.elem);
         }

      }

      public String toString() {
         return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"failures = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes()}));
      }

      // $FF: synthetic method
      public BlacklistTracker org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$$outer() {
         return this.$outer;
      }

      public ExecutorFailureList() {
         if (BlacklistTracker.this == null) {
            throw null;
         } else {
            this.$outer = BlacklistTracker.this;
            super();
            Logging$class.$init$(this);
            this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$failuresAndExpiryTimes = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
            this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$$minExpiryTime = Long.MAX_VALUE;
         }
      }

      public class TaskId implements Product, Serializable {
         private final int stage;
         private final int stageAttempt;
         private final int taskIndex;
         // $FF: synthetic field
         public final ExecutorFailureList $outer;

         public int stage() {
            return this.stage;
         }

         public int stageAttempt() {
            return this.stageAttempt;
         }

         public int taskIndex() {
            return this.taskIndex;
         }

         public TaskId copy(int stage, int stageAttempt, int taskIndex) {
            return this.org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$TaskId$$$outer().new TaskId(stage, stageAttempt, taskIndex);
         }

         public int copy$default$1() {
            return this.stage();
         }

         public int copy$default$2() {
            return this.stageAttempt();
         }

         public int copy$default$3() {
            return this.taskIndex();
         }

         public String productPrefix() {
            return "TaskId";
         }

         public int productArity() {
            return 3;
         }

         public Object productElement(int x$1) {
            Integer var10000;
            switch (x$1) {
               case 0:
                  var10000 = BoxesRunTime.boxToInteger(this.stage());
                  break;
               case 1:
                  var10000 = BoxesRunTime.boxToInteger(this.stageAttempt());
                  break;
               case 2:
                  var10000 = BoxesRunTime.boxToInteger(this.taskIndex());
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
            return x$1 instanceof TaskId;
         }

         public int hashCode() {
            int var1 = -889275714;
            var1 = Statics.mix(var1, this.stage());
            var1 = Statics.mix(var1, this.stageAttempt());
            var1 = Statics.mix(var1, this.taskIndex());
            return Statics.finalizeHash(var1, 3);
         }

         public String toString() {
            return ScalaRunTime$.MODULE$._toString(this);
         }

         public boolean equals(Object x$1) {
            boolean var10000;
            if (this != x$1) {
               label52: {
                  boolean var3;
                  if (x$1 instanceof TaskId) {
                     var3 = true;
                  } else {
                     var3 = false;
                  }

                  if (var3) {
                     TaskId var4 = (TaskId)x$1;
                     if (this.stage() == var4.stage() && this.stageAttempt() == var4.stageAttempt() && this.taskIndex() == var4.taskIndex() && var4.canEqual(this)) {
                        break label52;
                     }
                  }

                  var10000 = false;
                  return var10000;
               }
            }

            var10000 = true;
            return var10000;
         }

         // $FF: synthetic method
         public ExecutorFailureList org$apache$spark$scheduler$BlacklistTracker$ExecutorFailureList$TaskId$$$outer() {
            return this.$outer;
         }

         public TaskId(int stage, int stageAttempt, int taskIndex) {
            this.stage = stage;
            this.stageAttempt = stageAttempt;
            this.taskIndex = taskIndex;
            if (ExecutorFailureList.this == null) {
               throw null;
            } else {
               this.$outer = ExecutorFailureList.this;
               super();
               Product$class.$init$(this);
            }
         }
      }

      public class TaskId$ extends AbstractFunction3<Object, Object, Object, TaskId> implements Serializable {
         // $FF: synthetic field
         private final ExecutorFailureList $outer;

         public final String toString() {
            return "TaskId";
         }

         public TaskId apply(int stage, int stageAttempt, int taskIndex) {
            return this.$outer.new TaskId(stage, stageAttempt, taskIndex);
         }

         public Option<Tuple3<Object, Object, Object>> unapply(TaskId x$0) {
            return (Option<Tuple3<Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.stage()), BoxesRunTime.boxToInteger(x$0.stageAttempt()), BoxesRunTime.boxToInteger(x$0.taskIndex()))));
         }

         public TaskId$() {
            if (ExecutorFailureList.this == null) {
               throw null;
            } else {
               this.$outer = ExecutorFailureList.this;
               super();
            }
         }
      }
   }
}
