package org.apache.spark;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.ConfigProvider;
import org.apache.spark.internal.config.ConfigReader;
import org.apache.spark.internal.config.OptionalConfigEntry;
import org.apache.spark.internal.config.SparkConfigProvider;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Cloneable;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.IterableLike;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SetLike;
import scala.collection.Traversable;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.LinkedHashSet;
import scala.collection.mutable.Set$;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.math.Ordering$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0015ua\u0001B\u0001\u0003\u0001%\u0011\u0011b\u00159be.\u001cuN\u001c4\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001c\u0001aE\u0003\u0001\u0015IAb\u0004\u0005\u0002\f!5\tAB\u0003\u0002\u000e\u001d\u0005!A.\u00198h\u0015\u0005y\u0011\u0001\u00026bm\u0006L!!\u0005\u0007\u0003\r=\u0013'.Z2u!\t\u0019b#D\u0001\u0015\u0015\u0005)\u0012!B:dC2\f\u0017BA\f\u0015\u0005%\u0019En\u001c8fC\ndW\r\u0005\u0002\u001a95\t!D\u0003\u0002\u001c\u0005\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u001e5\t9Aj\\4hS:<\u0007CA\n \u0013\t\u0001CC\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005#\u0001\t\u0005\t\u0015!\u0003$\u00031aw.\u00193EK\u001a\fW\u000f\u001c;t!\t\u0019B%\u0003\u0002&)\t9!i\\8mK\u0006t\u0007\"B\u0014\u0001\t\u0003A\u0013A\u0002\u001fj]&$h\b\u0006\u0002*WA\u0011!\u0006A\u0007\u0002\u0005!)!E\na\u0001G!)q\u0005\u0001C\u0001[Q\t\u0011\u0006C\u00040\u0001\t\u0007I\u0011\u0002\u0019\u0002\u0011M,G\u000f^5oON,\u0012!\r\t\u0005e]J\u0014(D\u00014\u0015\t!T'\u0001\u0006d_:\u001cWO\u001d:f]RT!A\u000e\b\u0002\tU$\u0018\u000e\\\u0005\u0003qM\u0012\u0011cQ8oGV\u0014(/\u001a8u\u0011\u0006\u001c\b.T1q!\tQTH\u0004\u0002\u0014w%\u0011A\bF\u0001\u0007!J,G-\u001a4\n\u0005yz$AB*ue&twM\u0003\u0002=)!1\u0011\t\u0001Q\u0001\nE\n\u0011b]3ui&twm\u001d\u0011\t\u0011\r\u0003\u0001R1A\u0005\n\u0011\u000baA]3bI\u0016\u0014X#A#\u0011\u0005\u0019KU\"A$\u000b\u0005!S\u0012AB2p]\u001aLw-\u0003\u0002K\u000f\na1i\u001c8gS\u001e\u0014V-\u00193fe\"AA\n\u0001E\u0001B\u0003&Q)A\u0004sK\u0006$WM\u001d\u0011)\u0005-s\u0005CA\nP\u0013\t\u0001FCA\u0005ue\u0006t7/[3oi\"1!\u000b\u0001C\u0001\u0005M\u000b\u0001\u0004\\8bI\u001a\u0013x.\\*zgR,W\u000e\u0015:pa\u0016\u0014H/[3t)\tIC\u000bC\u0003V#\u0002\u00071%\u0001\u0004tS2,g\u000e\u001e\u0005\u0006/\u0002!\t\u0001W\u0001\u0004g\u0016$HcA\u0015Z7\")!L\u0016a\u0001s\u0005\u00191.Z=\t\u000bq3\u0006\u0019A\u001d\u0002\u000bY\fG.^3\t\r]\u0003A\u0011\u0001\u0002_)\u0011Is\fY1\t\u000bik\u0006\u0019A\u001d\t\u000bqk\u0006\u0019A\u001d\t\u000bUk\u0006\u0019A\u0012\t\r]\u0003A\u0011\u0001\u0002d+\t!G\u000eF\u0002*KVDQA\u001a2A\u0002\u001d\fQ!\u001a8uef\u00042A\u00125k\u0013\tIwIA\u0006D_:4\u0017nZ#oiJL\bCA6m\u0019\u0001!Q!\u001c2C\u00029\u0014\u0011\u0001V\t\u0003_J\u0004\"a\u00059\n\u0005E$\"a\u0002(pi\"Lgn\u001a\t\u0003'ML!\u0001\u001e\u000b\u0003\u0007\u0005s\u0017\u0010C\u0003]E\u0002\u0007!\u000e\u0003\u0004X\u0001\u0011\u0005!a^\u000b\u0003qz$2!K=\u0000\u0011\u00151g\u000f1\u0001{!\r150`\u0005\u0003y\u001e\u00131c\u00149uS>t\u0017\r\\\"p]\u001aLw-\u00128uef\u0004\"a\u001b@\u0005\u000b54(\u0019\u00018\t\u000bq3\b\u0019A?\t\u000f\u0005\r\u0001\u0001\"\u0001\u0002\u0006\u0005I1/\u001a;NCN$XM\u001d\u000b\u0004S\u0005\u001d\u0001bBA\u0005\u0003\u0003\u0001\r!O\u0001\u0007[\u0006\u001cH/\u001a:\t\u000f\u00055\u0001\u0001\"\u0001\u0002\u0010\u0005Q1/\u001a;BaBt\u0015-\\3\u0015\u0007%\n\t\u0002C\u0004\u0002\u0014\u0005-\u0001\u0019A\u001d\u0002\t9\fW.\u001a\u0005\b\u0003/\u0001A\u0011AA\r\u0003\u001d\u0019X\r\u001e&beN$2!KA\u000e\u0011!\ti\"!\u0006A\u0002\u0005}\u0011\u0001\u00026beN\u0004R!!\t\u00022erA!a\t\u0002.9!\u0011QEA\u0016\u001b\t\t9CC\u0002\u0002*!\ta\u0001\u0010:p_Rt\u0014\"A\u000b\n\u0007\u0005=B#A\u0004qC\u000e\\\u0017mZ3\n\t\u0005M\u0012Q\u0007\u0002\u0004'\u0016\f(bAA\u0018)!9\u0011q\u0003\u0001\u0005\u0002\u0005eBcA\u0015\u0002<!A\u0011QDA\u001c\u0001\u0004\ti\u0004\u0005\u0003\u0014\u0003\u007fI\u0014bAA!)\t)\u0011I\u001d:bs\"9\u0011Q\t\u0001\u0005\u0002\u0005\u001d\u0013AD:fi\u0016CXmY;u_J,eN\u001e\u000b\u0006S\u0005%\u0013Q\n\u0005\b\u0003\u0017\n\u0019\u00051\u0001:\u0003!1\u0018M]5bE2,\u0007B\u0002/\u0002D\u0001\u0007\u0011\bC\u0004\u0002F\u0001!\t!!\u0015\u0015\u0007%\n\u0019\u0006\u0003\u0005\u0002V\u0005=\u0003\u0019AA,\u0003%1\u0018M]5bE2,7\u000f\u0005\u0004\u0002\"\u0005E\u0012\u0011\f\t\u0006'\u0005m\u0013(O\u0005\u0004\u0003;\"\"A\u0002+va2,'\u0007C\u0004\u0002F\u0001!\t!!\u0019\u0015\u0007%\n\u0019\u0007\u0003\u0005\u0002V\u0005}\u0003\u0019AA3!\u0015\u0019\u0012qHA-\u0011\u001d\tI\u0007\u0001C\u0001\u0003W\nAb]3u'B\f'o\u001b%p[\u0016$2!KA7\u0011\u001d\ty'a\u001aA\u0002e\nA\u0001[8nK\"9\u00111\u000f\u0001\u0005\u0002\u0005U\u0014AB:fi\u0006cG\u000eF\u0002*\u0003oBqaLA9\u0001\u0004\tI\b\u0005\u0004\u0002\"\u0005m\u0014\u0011L\u0005\u0005\u0003{\n)DA\u0006Ue\u00064XM]:bE2,\u0007bBAA\u0001\u0011\u0005\u00111Q\u0001\rg\u0016$\u0018JZ'jgNLgn\u001a\u000b\u0006S\u0005\u0015\u0015q\u0011\u0005\u00075\u0006}\u0004\u0019A\u001d\t\rq\u000by\b1\u0001:\u0011!\t\t\t\u0001C\u0001\u0005\u0005-U\u0003BAG\u0003+#R!KAH\u0003/CqAZAE\u0001\u0004\t\t\n\u0005\u0003GQ\u0006M\u0005cA6\u0002\u0016\u00121Q.!#C\u00029Dq\u0001XAE\u0001\u0004\t\u0019\n\u0003\u0005\u0002\u0002\u0002!\tAAAN+\u0011\ti*!*\u0015\u000b%\ny*a*\t\u000f\u0019\fI\n1\u0001\u0002\"B!ai_AR!\rY\u0017Q\u0015\u0003\u0007[\u0006e%\u0019\u00018\t\u000fq\u000bI\n1\u0001\u0002$\"9\u00111\u0016\u0001\u0005\u0002\u00055\u0016a\u0005:fO&\u001cH/\u001a:Lef|7\t\\1tg\u0016\u001cHcA\u0015\u00020\"A\u0011\u0011WAU\u0001\u0004\t\u0019,A\u0004dY\u0006\u001c8/Z:\u0011\u000bM\ty$!.1\t\u0005]\u0016q\u0018\t\u0006u\u0005e\u0016QX\u0005\u0004\u0003w{$!B\"mCN\u001c\bcA6\u0002@\u0012Y\u0011\u0011YAX\u0003\u0003\u0005\tQ!\u0001o\u0005\ryF%\r\u0005\n\u0003\u000b\u0004!\u0019!C\u0007\u0003\u000f\fQ\"\u0019<s_:\u000bW.Z:qC\u000e,WCAAe\u001f\t\tY-\t\u0002\u0002N\u0006a\u0011M\u001e:p]M\u001c\u0007.Z7b]!A\u0011\u0011\u001b\u0001!\u0002\u001b\tI-\u0001\bbmJ|g*Y7fgB\f7-\u001a\u0011\t\u000f\u0005U\u0007\u0001\"\u0001\u0002X\u0006\u0019\"/Z4jgR,'/\u0011<s_N\u001b\u0007.Z7bgR\u0019\u0011&!7\t\u0011\u0005m\u00171\u001ba\u0001\u0003;\fqa]2iK6\f7\u000fE\u0003\u0014\u0003?\f\u0019/C\u0002\u0002bR\u0011!\u0002\u0010:fa\u0016\fG/\u001a3?!\u0011\t)/a;\u000e\u0005\u0005\u001d(bAAu\t\u0005!\u0011M\u001e:p\u0013\u0011\ti/a:\u0003\rM\u001b\u0007.Z7b\u0011\u001d\t\t\u0010\u0001C\u0001\u0003g\fQbZ3u\u0003Z\u0014xnU2iK6\fWCAA{!\u0019Q\u0014q_A~s%\u0019\u0011\u0011` \u0003\u00075\u000b\u0007\u000fE\u0002\u0014\u0003{L1!a@\u0015\u0005\u0011auN\\4\t\u000f\t\r\u0001\u0001\"\u0001\u0003\u0006\u00051!/Z7pm\u0016$2!\u000bB\u0004\u0011\u0019Q&\u0011\u0001a\u0001s!A!1\u0001\u0001\u0005\u0002\t\u0011Y\u0001F\u0002*\u0005\u001bAqA\u001aB\u0005\u0001\u0004\u0011y\u0001\r\u0003\u0003\u0012\tU\u0001\u0003\u0002$i\u0005'\u00012a\u001bB\u000b\t-\u00119B!\u0004\u0002\u0002\u0003\u0005)\u0011\u00018\u0003\u0007}##\u0007C\u0004\u0003\u001c\u0001!\tA!\b\u0002\u0007\u001d,G\u000fF\u0002:\u0005?AaA\u0017B\r\u0001\u0004I\u0004b\u0002B\u000e\u0001\u0011\u0005!1\u0005\u000b\u0006s\t\u0015\"q\u0005\u0005\u00075\n\u0005\u0002\u0019A\u001d\t\u000f\t%\"\u0011\u0005a\u0001s\u0005aA-\u001a4bk2$h+\u00197vK\"A!1\u0004\u0001\u0005\u0002\t\u0011i#\u0006\u0003\u00030\tMB\u0003\u0002B\u0019\u0005k\u00012a\u001bB\u001a\t\u0019i'1\u0006b\u0001]\"9aMa\u000bA\u0002\t]\u0002\u0003\u0002$i\u0005cAqAa\u000f\u0001\t\u0003\u0011i$\u0001\thKR$\u0016.\\3BgN+7m\u001c8egR!\u00111 B \u0011\u0019Q&\u0011\ba\u0001s!9!1\b\u0001\u0005\u0002\t\rCCBA~\u0005\u000b\u00129\u0005\u0003\u0004[\u0005\u0003\u0002\r!\u000f\u0005\b\u0005S\u0011\t\u00051\u0001:\u0011\u001d\u0011Y\u0005\u0001C\u0001\u0005\u001b\n1bZ3u)&lW-Q:NgR!\u00111 B(\u0011\u0019Q&\u0011\na\u0001s!9!1\n\u0001\u0005\u0002\tMCCBA~\u0005+\u00129\u0006\u0003\u0004[\u0005#\u0002\r!\u000f\u0005\b\u0005S\u0011\t\u00061\u0001:\u0011\u001d\u0011Y\u0006\u0001C\u0001\u0005;\nabZ3u'&TX-Q:CsR,7\u000f\u0006\u0003\u0002|\n}\u0003B\u0002.\u0003Z\u0001\u0007\u0011\bC\u0004\u0003\\\u0001!\tAa\u0019\u0015\r\u0005m(Q\rB4\u0011\u0019Q&\u0011\ra\u0001s!9!\u0011\u0006B1\u0001\u0004I\u0004b\u0002B.\u0001\u0011\u0005!1\u000e\u000b\u0007\u0003w\u0014iGa\u001c\t\ri\u0013I\u00071\u0001:\u0011!\u0011IC!\u001bA\u0002\u0005m\bb\u0002B:\u0001\u0011\u0005!QO\u0001\fO\u0016$8+\u001b>f\u0003N\\%\r\u0006\u0003\u0002|\n]\u0004B\u0002.\u0003r\u0001\u0007\u0011\bC\u0004\u0003t\u0001!\tAa\u001f\u0015\r\u0005m(Q\u0010B@\u0011\u0019Q&\u0011\u0010a\u0001s!9!\u0011\u0006B=\u0001\u0004I\u0004b\u0002BB\u0001\u0011\u0005!QQ\u0001\fO\u0016$8+\u001b>f\u0003Nl%\r\u0006\u0003\u0002|\n\u001d\u0005B\u0002.\u0003\u0002\u0002\u0007\u0011\bC\u0004\u0003\u0004\u0002!\tAa#\u0015\r\u0005m(Q\u0012BH\u0011\u0019Q&\u0011\u0012a\u0001s!9!\u0011\u0006BE\u0001\u0004I\u0004b\u0002BJ\u0001\u0011\u0005!QS\u0001\fO\u0016$8+\u001b>f\u0003N<%\r\u0006\u0003\u0002|\n]\u0005B\u0002.\u0003\u0012\u0002\u0007\u0011\bC\u0004\u0003\u0014\u0002!\tAa'\u0015\r\u0005m(Q\u0014BP\u0011\u0019Q&\u0011\u0014a\u0001s!9!\u0011\u0006BM\u0001\u0004I\u0004b\u0002BR\u0001\u0011\u0005!QU\u0001\nO\u0016$x\n\u001d;j_:$BAa*\u0003.B!1C!+:\u0013\r\u0011Y\u000b\u0006\u0002\u0007\u001fB$\u0018n\u001c8\t\ri\u0013\t\u000b1\u0001:\u0011!\u0011\t\f\u0001C\u0001\u0005\tM\u0016aE4fi^KG\u000f[*vEN$\u0018\u000e^;uS>tG\u0003\u0002BT\u0005kCaA\u0017BX\u0001\u0004I\u0004b\u0002B]\u0001\u0011\u0005!1X\u0001\u0007O\u0016$\u0018\t\u001c7\u0016\u0005\u0005\u0015\u0004b\u0002B`\u0001\u0011\u0005!\u0011Y\u0001\u0011O\u0016$\u0018\t\u001c7XSRD\u0007K]3gSb$B!!\u001a\u0003D\"9!Q\u0019B_\u0001\u0004I\u0014A\u00029sK\u001aL\u0007\u0010C\u0004\u0003J\u0002!\tAa3\u0002\r\u001d,G/\u00138u)\u0019\u0011iMa5\u0003VB\u00191Ca4\n\u0007\tEGCA\u0002J]RDaA\u0017Bd\u0001\u0004I\u0004\u0002\u0003B\u0015\u0005\u000f\u0004\rA!4\t\u000f\te\u0007\u0001\"\u0001\u0003\\\u00069q-\u001a;M_:<GCBA~\u0005;\u0014y\u000e\u0003\u0004[\u0005/\u0004\r!\u000f\u0005\t\u0005S\u00119\u000e1\u0001\u0002|\"9!1\u001d\u0001\u0005\u0002\t\u0015\u0018!C4fi\u0012{WO\u00197f)\u0019\u00119O!<\u0003pB\u00191C!;\n\u0007\t-HC\u0001\u0004E_V\u0014G.\u001a\u0005\u00075\n\u0005\b\u0019A\u001d\t\u0011\t%\"\u0011\u001da\u0001\u0005ODqAa=\u0001\t\u0003\u0011)0\u0001\u0006hKR\u0014un\u001c7fC:$Ra\tB|\u0005sDaA\u0017By\u0001\u0004I\u0004b\u0002B\u0015\u0005c\u0004\ra\t\u0005\b\u0005{\u0004A\u0011\u0001B\u0000\u000399W\r^#yK\u000e,Ho\u001c:F]Z,\"!a\u0016\t\u000f\r\r\u0001\u0001\"\u0001\u0004\u0006\u0005Aq-\u001a;BaBLE-F\u0001:\u0011\u001d\u0019I\u0001\u0001C\u0001\u0007\u0017\t\u0001bY8oi\u0006Lgn\u001d\u000b\u0004G\r5\u0001B\u0002.\u0004\b\u0001\u0007\u0011\b\u0003\u0005\u0004\n\u0001!\tAAB\t)\r\u001931\u0003\u0005\bM\u000e=\u0001\u0019AB\u000ba\u0011\u00199ba\u0007\u0011\t\u0019C7\u0011\u0004\t\u0004W\u000emAaCB\u000f\u0007'\t\t\u0011!A\u0003\u00029\u00141a\u0018\u00134\u0011\u0019\u0019\t\u0003\u0001C![\u0005)1\r\\8oK\"A1Q\u0005\u0001\u0005\u0002\t\u00199#\u0001\u0004hKR,gN\u001e\u000b\u0004s\r%\u0002bBA\n\u0007G\u0001\r!\u000f\u0005\t\u0007[\u0001A\u0011\u0001\u0002\u00040\u0005\u0001b/\u00197jI\u0006$XmU3ui&twm\u001d\u000b\u0003\u0007c\u00012aEB\u001a\u0013\r\u0019)\u0004\u0006\u0002\u0005+:LG\u000fC\u0004\u0004:\u0001!\ta!\u0002\u0002\u001bQ|G)\u001a2vON#(/\u001b8h\u000f!\u0019iD\u0001E\u0001\u0005\r}\u0012!C*qCJ\\7i\u001c8g!\rQ3\u0011\t\u0004\b\u0003\tA\tAAB\"'\u0019\u0019\te!\u0012\u0019=A\u00191ca\u0012\n\u0007\r%CC\u0001\u0004B]f\u0014VM\u001a\u0005\bO\r\u0005C\u0011AB')\t\u0019y\u0004\u0003\u0006\u0004R\r\u0005#\u0019!C\u0005\u0007'\n\u0011\u0003Z3qe\u0016\u001c\u0017\r^3e\u0007>tg-[4t+\t\u0019)\u0006\u0005\u0004;\u0003oL4q\u000b\t\u0005\u00073\u001aY&\u0004\u0002\u0004B\u001991QLB!\t\u000e}#\u0001\u0005#faJ,7-\u0019;fI\u000e{gNZ5h'\u001d\u0019Yf!\u0012\u0004by\u00012aEB2\u0013\r\u0019)\u0007\u0006\u0002\b!J|G-^2u\u0011)Q61\fBK\u0002\u0013\u00051Q\u0001\u0005\u000b\u0007W\u001aYF!E!\u0002\u0013I\u0014\u0001B6fs\u0002B1ba\u001c\u0004\\\tU\r\u0011\"\u0001\u0004\u0006\u00059a/\u001a:tS>t\u0007BCB:\u00077\u0012\t\u0012)A\u0005s\u0005Aa/\u001a:tS>t\u0007\u0005C\u0006\u0004x\rm#Q3A\u0005\u0002\r\u0015\u0011A\u00053faJ,7-\u0019;j_:lUm]:bO\u0016D!ba\u001f\u0004\\\tE\t\u0015!\u0003:\u0003M!W\r\u001d:fG\u0006$\u0018n\u001c8NKN\u001c\u0018mZ3!\u0011\u001d931\fC\u0001\u0007\u007f\"\u0002ba\u0016\u0004\u0002\u000e\r5Q\u0011\u0005\u00075\u000eu\u0004\u0019A\u001d\t\u000f\r=4Q\u0010a\u0001s!91qOB?\u0001\u0004I\u0004BCBE\u00077\n\t\u0011\"\u0001\u0004\f\u0006!1m\u001c9z)!\u00199f!$\u0004\u0010\u000eE\u0005\u0002\u0003.\u0004\bB\u0005\t\u0019A\u001d\t\u0013\r=4q\u0011I\u0001\u0002\u0004I\u0004\"CB<\u0007\u000f\u0003\n\u00111\u0001:\u0011)\u0019)ja\u0017\u0012\u0002\u0013\u00051qS\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\t\u0019IJK\u0002:\u00077[#a!(\u0011\t\r}5\u0011V\u0007\u0003\u0007CSAaa)\u0004&\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0007O#\u0012AC1o]>$\u0018\r^5p]&!11VBQ\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\u000b\u0007_\u001bY&%A\u0005\u0002\r]\u0015AD2paf$C-\u001a4bk2$HE\r\u0005\u000b\u0007g\u001bY&%A\u0005\u0002\r]\u0015AD2paf$C-\u001a4bk2$He\r\u0005\u000b\u0007o\u001bY&!A\u0005B\re\u0016!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070\u0006\u0002\u0004<B\u00191b!0\n\u0005yb\u0001BCBa\u00077\n\t\u0011\"\u0001\u0004D\u0006a\u0001O]8ek\u000e$\u0018I]5usV\u0011!Q\u001a\u0005\u000b\u0007\u000f\u001cY&!A\u0005\u0002\r%\u0017A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0004e\u000e-\u0007BCBg\u0007\u000b\f\t\u00111\u0001\u0003N\u0006\u0019\u0001\u0010J\u0019\t\u0015\rE71LA\u0001\n\u0003\u001a\u0019.A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\t\u0019)\u000eE\u0003\u0004X\u000eu'/\u0004\u0002\u0004Z*\u001911\u001c\u000b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0004`\u000ee'\u0001C%uKJ\fGo\u001c:\t\u0015\r\r81LA\u0001\n\u0003\u0019)/\u0001\u0005dC:,\u0015/^1m)\r\u00193q\u001d\u0005\n\u0007\u001b\u001c\t/!AA\u0002ID!ba;\u0004\\\u0005\u0005I\u0011IBw\u0003!A\u0017m\u001d5D_\u0012,GC\u0001Bg\u0011)\u0019\tpa\u0017\u0002\u0002\u0013\u000531_\u0001\ti>\u001cFO]5oOR\u001111\u0018\u0005\u000b\u0007o\u001cY&!A\u0005B\re\u0018AB3rk\u0006d7\u000fF\u0002$\u0007wD\u0011b!4\u0004v\u0006\u0005\t\u0019\u0001:\t\u0013\r}8\u0011\tQ\u0001\n\rU\u0013A\u00053faJ,7-\u0019;fI\u000e{gNZ5hg\u0002B!\u0002b\u0001\u0004B\t\u0007I\u0011\u0002C\u0003\u0003]\u0019wN\u001c4jON<\u0016\u000e\u001e5BYR,'O\\1uSZ,7/\u0006\u0002\u0005\bAAA\u0011\u0002C\b\u0007w#\t\"\u0004\u0002\u0005\f)!AQBBm\u0003%IW.\\;uC\ndW-\u0003\u0003\u0002z\u0012-\u0001CBBl\t'!)\"\u0003\u0003\u00024\re\u0007\u0003BB-\t/1q\u0001\"\u0007\u0004B\u0011#YBA\bBYR,'O\\1uK\u000e{gNZ5h'\u001d!9b!\u0012\u0004byA!B\u0017C\f\u0005+\u0007I\u0011AB\u0003\u0011)\u0019Y\u0007b\u0006\u0003\u0012\u0003\u0006I!\u000f\u0005\f\u0007_\"9B!f\u0001\n\u0003\u0019)\u0001\u0003\u0006\u0004t\u0011]!\u0011#Q\u0001\neB1\u0002b\n\u0005\u0018\tU\r\u0011\"\u0001\u0005*\u0005YAO]1og2\fG/[8o+\t!Y\u0003E\u0003\u0014\t[I\u0014(C\u0002\u00050Q\u0011\u0011BR;oGRLwN\\\u0019\t\u0017\u0011MBq\u0003B\tB\u0003%A1F\u0001\riJ\fgn\u001d7bi&|g\u000e\t\u0005\bO\u0011]A\u0011\u0001C\u001c)!!)\u0002\"\u000f\u0005<\u0011u\u0002B\u0002.\u00056\u0001\u0007\u0011\bC\u0004\u0004p\u0011U\u0002\u0019A\u001d\t\u0015\u0011\u001dBQ\u0007I\u0001\u0002\u0004!Y\u0003\u0003\u0006\u0004\n\u0012]\u0011\u0011!C\u0001\t\u0003\"\u0002\u0002\"\u0006\u0005D\u0011\u0015Cq\t\u0005\t5\u0012}\u0002\u0013!a\u0001s!I1q\u000eC !\u0003\u0005\r!\u000f\u0005\u000b\tO!y\u0004%AA\u0002\u0011-\u0002BCBK\t/\t\n\u0011\"\u0001\u0004\u0018\"Q1q\u0016C\f#\u0003%\taa&\t\u0015\rMFqCI\u0001\n\u0003!y%\u0006\u0002\u0005R)\"A1FBN\u0011)\u00199\fb\u0006\u0002\u0002\u0013\u00053\u0011\u0018\u0005\u000b\u0007\u0003$9\"!A\u0005\u0002\r\r\u0007BCBd\t/\t\t\u0011\"\u0001\u0005ZQ\u0019!\u000fb\u0017\t\u0015\r5GqKA\u0001\u0002\u0004\u0011i\r\u0003\u0006\u0004R\u0012]\u0011\u0011!C!\u0007'D!ba9\u0005\u0018\u0005\u0005I\u0011\u0001C1)\r\u0019C1\r\u0005\n\u0007\u001b$y&!AA\u0002ID!ba;\u0005\u0018\u0005\u0005I\u0011IBw\u0011)\u0019\t\u0010b\u0006\u0002\u0002\u0013\u000531\u001f\u0005\u000b\u0007o$9\"!A\u0005B\u0011-DcA\u0012\u0005n!I1Q\u001aC5\u0003\u0003\u0005\rA\u001d\u0005\n\tc\u001a\t\u0005)A\u0005\t\u000f\t\u0001dY8oM&<7oV5uQ\u0006cG/\u001a:oCRLg/Z:!\u0011)!)h!\u0011C\u0002\u0013%AqO\u0001\u0010C2d\u0017\t\u001c;fe:\fG/\u001b<fgV\u0011A\u0011\u0010\t\u0007u\u0005]\u0018\bb\u001f\u0011\rM\tY&\u000fC\u000b\u0011%!yh!\u0011!\u0002\u0013!I(\u0001\tbY2\fE\u000e^3s]\u0006$\u0018N^3tA!AA1QB!\t\u0003!))A\u000bjg\u0016CXmY;u_J\u001cF/\u0019:ukB\u001cuN\u001c4\u0015\u0007\r\"9\tC\u0004\u0002\u0014\u0011\u0005\u0005\u0019A\u001d\t\u0011\u0011-5\u0011\tC\u0001\t\u001b\u000bq\"[:Ta\u0006\u00148\u000eU8si\u000e{gN\u001a\u000b\u0004G\u0011=\u0005bBA\n\t\u0013\u0003\r!\u000f\u0005\t\t'\u001b\t\u0005\"\u0001\u0005\u0016\u0006\u0019r-\u001a;EKB\u0014XmY1uK\u0012\u001cuN\u001c4jOR1!q\u0015CL\t3CaA\u0017CI\u0001\u0004I\u0004\u0002\u0003CN\t#\u0003\r\u0001\"(\u0002\t\r|gN\u001a\t\u0007\t?#\t+O\u001d\u000e\u0003UJ1!!?6\u0011!!)k!\u0011\u0005\u0002\u0011\u001d\u0016!\u00067pO\u0012+\u0007O]3dCRLwN\\,be:Lgn\u001a\u000b\u0005\u0007c!I\u000b\u0003\u0004[\tG\u0003\r!O\u0004\u000b\t[\u001b\t%!A\t\n\u0011=\u0016\u0001\u0005#faJ,7-\u0019;fI\u000e{gNZ5h!\u0011\u0019I\u0006\"-\u0007\u0015\ru3\u0011IA\u0001\u0012\u0013!\u0019lE\u0003\u00052\u0012Uf\u0004E\u0005\u00058\u0012u\u0016(O\u001d\u0004X5\u0011A\u0011\u0018\u0006\u0004\tw#\u0012a\u0002:v]RLW.Z\u0005\u0005\t\u007f#ILA\tBEN$(/Y2u\rVt7\r^5p]NBqa\nCY\t\u0003!\u0019\r\u0006\u0002\u00050\"Q1\u0011\u001fCY\u0003\u0003%)ea=\t\u0015\u0011%G\u0011WA\u0001\n\u0003#Y-A\u0003baBd\u0017\u0010\u0006\u0005\u0004X\u00115Gq\u001aCi\u0011\u0019QFq\u0019a\u0001s!91q\u000eCd\u0001\u0004I\u0004bBB<\t\u000f\u0004\r!\u000f\u0005\u000b\t+$\t,!A\u0005\u0002\u0012]\u0017aB;oCB\u0004H.\u001f\u000b\u0005\t3$\t\u000fE\u0003\u0014\u0005S#Y\u000e\u0005\u0004\u0014\t;L\u0014(O\u0005\u0004\t?$\"A\u0002+va2,7\u0007\u0003\u0006\u0005d\u0012M\u0017\u0011!a\u0001\u0007/\n1\u0001\u001f\u00131\u0011)!9\u000f\"-\u0002\u0002\u0013%A\u0011^\u0001\fe\u0016\fGMU3t_24X\rF\u0001\u000b\u000f)!io!\u0011\u0002\u0002#%Aq^\u0001\u0010\u00032$XM\u001d8bi\u0016\u001cuN\u001c4jOB!1\u0011\fCy\r)!Ib!\u0011\u0002\u0002#%A1_\n\u0006\tc$)P\b\t\u000b\to#i,O\u001d\u0005,\u0011U\u0001bB\u0014\u0005r\u0012\u0005A\u0011 \u000b\u0003\t_D!b!=\u0005r\u0006\u0005IQIBz\u0011)!I\r\"=\u0002\u0002\u0013\u0005Eq \u000b\t\t+)\t!b\u0001\u0006\u0006!1!\f\"@A\u0002eBqaa\u001c\u0005~\u0002\u0007\u0011\b\u0003\u0006\u0005(\u0011u\b\u0013!a\u0001\tWA!\u0002\"6\u0005r\u0006\u0005I\u0011QC\u0005)\u0011)Y!b\u0004\u0011\u000bM\u0011I+\"\u0004\u0011\u000fM!i.O\u001d\u0005,!QA1]C\u0004\u0003\u0003\u0005\r\u0001\"\u0006\t\u0015\u0015MA\u0011_I\u0001\n\u0003!y%A\bbaBd\u0017\u0010\n3fM\u0006,H\u000e\u001e\u00134\u0011))9\u0002\"=\u0012\u0002\u0013\u0005AqJ\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001a\t\u0015\u0011\u001dH\u0011_A\u0001\n\u0013!I\u000f\u0003\u0006\u0005h\u000e\u0005\u0013\u0011!C\u0005\tS\u0004"
)
public class SparkConf implements Cloneable, Logging, Serializable {
   private final ConcurrentHashMap<String, String> org$apache$spark$SparkConf$$settings;
   private transient ConfigReader org$apache$spark$SparkConf$$reader;
   private final String avroNamespace;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private transient volatile boolean bitmap$trans$0;

   public static void logDeprecationWarning(String var0) {
      SparkConf$.MODULE$.logDeprecationWarning(var0);
   }

   public static Option<String> getDeprecatedConfig(String var0, Map<String, String> var1) {
      return SparkConf$.MODULE$.getDeprecatedConfig(var0, var1);
   }

   public static boolean isSparkPortConf(String var0) {
      return SparkConf$.MODULE$.isSparkPortConf(var0);
   }

   public static boolean isExecutorStartupConf(String var0) {
      return SparkConf$.MODULE$.isExecutorStartupConf(var0);
   }

   private ConfigReader org$apache$spark$SparkConf$$reader$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$trans$0) {
            ConfigReader _reader = new ConfigReader(new SparkConfigProvider(this.org$apache$spark$SparkConf$$settings()));
            _reader.bindEnv(new ConfigProvider(this) {
               // $FF: synthetic field
               private final SparkConf $outer;

               public Option<String> get(String key) {
                  return Option$.MODULE$.<String>apply(this.$outer.getenv(key));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.org$apache$spark$SparkConf$$reader = _reader;
            this.bitmap$trans$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.org$apache$spark$SparkConf$$reader;
   }

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

   public ConcurrentHashMap<String, String> org$apache$spark$SparkConf$$settings() {
      return this.org$apache$spark$SparkConf$$settings;
   }

   public ConfigReader org$apache$spark$SparkConf$$reader() {
      return this.bitmap$trans$0 ? this.org$apache$spark$SparkConf$$reader : this.org$apache$spark$SparkConf$$reader$lzycompute();
   }

   public SparkConf loadFromSystemProperties(boolean silent) {
      Utils$.MODULE$.getSystemProperties().withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> x$1) {
            if (x$1 != null) {
               String key = (String)x$1._1();
               boolean var4 = key.startsWith("spark.");
               return var4;
            } else {
               throw new MatchError(x$1);
            }
         }
      }).foreach(new Serializable(this, silent) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;
         private final boolean silent$1;

         public final SparkConf apply(Tuple2<String, String> x$2) {
            if (x$2 != null) {
               String key = (String)x$2._1();
               String value = (String)x$2._2();
               SparkConf var5 = this.$outer.set(key, value, this.silent$1);
               return var5;
            } else {
               throw new MatchError(x$2);
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.silent$1 = silent$1;
            }
         }
      });
      return this;
   }

   public SparkConf set(String key, String value) {
      return this.set(key, value, false);
   }

   public SparkConf set(String key, String value, boolean silent) {
      if (key == null) {
         throw new NullPointerException("null key");
      } else if (value == null) {
         throw new NullPointerException((new StringBuilder()).append((Object)"null value for ").append((Object)key).toString());
      } else {
         if (!silent) {
            SparkConf$.MODULE$.logDeprecationWarning(key);
         }

         this.org$apache$spark$SparkConf$$settings().put(key, value);
         return this;
      }
   }

   public <T> SparkConf set(ConfigEntry<T> entry, T value) {
      this.set(entry.key(), (String)entry.stringConverter().apply(value));
      return this;
   }

   public <T> SparkConf set(OptionalConfigEntry<T> entry, T value) {
      this.set(entry.key(), (String)entry.rawStringConverter().apply(value));
      return this;
   }

   public SparkConf setMaster(String master) {
      return this.set("spark.master", master);
   }

   public SparkConf setAppName(String name) {
      return this.set("spark.app.name", name);
   }

   public SparkConf setJars(Seq<String> jars) {
      jars.withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String jar) {
            return jar == null;
         }
      }).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;

         public final void apply(String jar) {
            this.$outer.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "null jar passed to SparkContext constructor";
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
      });
      return this.set("spark.jars", ((TraversableOnce)jars.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$3) {
            return x$3 != null;
         }
      })).mkString(","));
   }

   public SparkConf setJars(String[] jars) {
      return this.setJars(Predef$.MODULE$.refArrayOps((Object[])jars).toSeq());
   }

   public SparkConf setExecutorEnv(String variable, String value) {
      return this.set((new StringBuilder()).append((Object)"spark.executorEnv.").append((Object)variable).toString(), value);
   }

   public SparkConf setExecutorEnv(Seq<Tuple2<String, String>> variables) {
      variables.withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> check$ifrefutable$2) {
            boolean var3;
            if (check$ifrefutable$2 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;

         public final SparkConf apply(Tuple2<String, String> x$4) {
            if (x$4 != null) {
               String k = (String)x$4._1();
               String v = (String)x$4._2();
               SparkConf var5 = this.$outer.setExecutorEnv(k, v);
               return var5;
            } else {
               throw new MatchError(x$4);
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
      return this;
   }

   public SparkConf setExecutorEnv(Tuple2<String, String>[] variables) {
      return this.setExecutorEnv(Predef$.MODULE$.refArrayOps(variables).toSeq());
   }

   public SparkConf setSparkHome(String home) {
      return this.set("spark.home", home);
   }

   public SparkConf setAll(Traversable<Tuple2<String, String>> settings) {
      settings.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;

         public final SparkConf apply(Tuple2<String, String> x0$1) {
            if (x0$1 != null) {
               String k = (String)x0$1._1();
               String v = (String)x0$1._2();
               SparkConf var5 = this.$outer.set(k, v);
               return var5;
            } else {
               throw new MatchError(x0$1);
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
      return this;
   }

   public SparkConf setIfMissing(String key, String value) {
      if (this.org$apache$spark$SparkConf$$settings().putIfAbsent(key, value) == null) {
         SparkConf$.MODULE$.logDeprecationWarning(key);
      }

      return this;
   }

   public <T> SparkConf setIfMissing(ConfigEntry<T> entry, T value) {
      if (this.org$apache$spark$SparkConf$$settings().putIfAbsent(entry.key(), entry.stringConverter().apply(value)) == null) {
         SparkConf$.MODULE$.logDeprecationWarning(entry.key());
      }

      return this;
   }

   public <T> SparkConf setIfMissing(OptionalConfigEntry<T> entry, T value) {
      if (this.org$apache$spark$SparkConf$$settings().putIfAbsent(entry.key(), entry.rawStringConverter().apply(value)) == null) {
         SparkConf$.MODULE$.logDeprecationWarning(entry.key());
      }

      return this;
   }

   public SparkConf registerKryoClasses(Class<?>[] classes) {
      LinkedHashSet allClassNames = new LinkedHashSet();
      allClassNames.$plus$plus$eq(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(this.get("spark.kryo.classesToRegister", "")))).split(',')).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$5) {
            return x$5.trim();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$6) {
            return !x$6.isEmpty();
         }
      })));
      allClassNames.$plus$plus$eq((TraversableOnce)Predef$.MODULE$.refArrayOps((Object[])classes).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Class<?> x$7) {
            return x$7.getName();
         }
      }, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit())));
      this.set("spark.kryo.classesToRegister", allClassNames.mkString(","));
      this.set("spark.serializer", KryoSerializer.class.getName());
      return this;
   }

   private final String avroNamespace() {
      return "avro.schema.";
   }

   public SparkConf registerAvroSchemas(Seq<Schema> schemas) {
      schemas.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;

         public final SparkConf apply(Schema schema) {
            return this.$outer.set((new StringBuilder()).append((Object)"avro.schema.").append((Object)BoxesRunTime.boxToLong(SchemaNormalization.parsingFingerprint64(schema))).toString(), schema.toString());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      return this;
   }

   public scala.collection.immutable.Map<Object, String> getAvroSchema() {
      return Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(this.getAll()).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> x0$2) {
            if (x0$2 != null) {
               String k = (String)x0$2._1();
               boolean var4 = k.startsWith("avro.schema.");
               return var4;
            } else {
               throw new MatchError(x0$2);
            }
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<Object, String> apply(Tuple2<String, String> x0$3) {
            if (x0$3 != null) {
               String k = (String)x0$3._1();
               String v = (String)x0$3._2();
               Tuple2 var5 = new Tuple2(BoxesRunTime.boxToLong((new StringOps(Predef$.MODULE$.augmentString(k.substring("avro.schema.".length())))).toLong()), v);
               return var5;
            } else {
               throw new MatchError(x0$3);
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).toMap(Predef$.MODULE$.$conforms());
   }

   public SparkConf remove(String key) {
      this.org$apache$spark$SparkConf$$settings().remove(key);
      return this;
   }

   public SparkConf remove(ConfigEntry<?> entry) {
      return this.remove(entry.key());
   }

   public String get(String key) {
      return (String)this.getOption(key).getOrElse(new Serializable(this, key) {
         public static final long serialVersionUID = 0L;
         private final String key$1;

         public final Nothing$ apply() {
            throw new NoSuchElementException(this.key$1);
         }

         public {
            this.key$1 = key$1;
         }
      });
   }

   public String get(String key, String defaultValue) {
      return (String)this.getOption(key).getOrElse(new Serializable(this, defaultValue) {
         public static final long serialVersionUID = 0L;
         private final String defaultValue$2;

         public final String apply() {
            return this.defaultValue$2;
         }

         public {
            this.defaultValue$2 = defaultValue$2;
         }
      });
   }

   public <T> T get(ConfigEntry<T> entry) {
      return (T)entry.readFrom(this.org$apache$spark$SparkConf$$reader());
   }

   public long getTimeAsSeconds(String key) {
      return Utils$.MODULE$.timeStringAsSeconds(this.get(key));
   }

   public long getTimeAsSeconds(String key, String defaultValue) {
      return Utils$.MODULE$.timeStringAsSeconds(this.get(key, defaultValue));
   }

   public long getTimeAsMs(String key) {
      return Utils$.MODULE$.timeStringAsMs(this.get(key));
   }

   public long getTimeAsMs(String key, String defaultValue) {
      return Utils$.MODULE$.timeStringAsMs(this.get(key, defaultValue));
   }

   public long getSizeAsBytes(String key) {
      return Utils$.MODULE$.byteStringAsBytes(this.get(key));
   }

   public long getSizeAsBytes(String key, String defaultValue) {
      return Utils$.MODULE$.byteStringAsBytes(this.get(key, defaultValue));
   }

   public long getSizeAsBytes(String key, long defaultValue) {
      return Utils$.MODULE$.byteStringAsBytes(this.get(key, (new StringBuilder()).append(defaultValue).append((Object)"B").toString()));
   }

   public long getSizeAsKb(String key) {
      return Utils$.MODULE$.byteStringAsKb(this.get(key));
   }

   public long getSizeAsKb(String key, String defaultValue) {
      return Utils$.MODULE$.byteStringAsKb(this.get(key, defaultValue));
   }

   public long getSizeAsMb(String key) {
      return Utils$.MODULE$.byteStringAsMb(this.get(key));
   }

   public long getSizeAsMb(String key, String defaultValue) {
      return Utils$.MODULE$.byteStringAsMb(this.get(key, defaultValue));
   }

   public long getSizeAsGb(String key) {
      return Utils$.MODULE$.byteStringAsGb(this.get(key));
   }

   public long getSizeAsGb(String key, String defaultValue) {
      return Utils$.MODULE$.byteStringAsGb(this.get(key, defaultValue));
   }

   public Option<String> getOption(String key) {
      return Option$.MODULE$.<String>apply(this.org$apache$spark$SparkConf$$settings().get(key)).<String>orElse(new Serializable(this, key) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;
         private final String key$2;

         public final Option<String> apply() {
            return SparkConf$.MODULE$.getDeprecatedConfig(this.key$2, this.$outer.org$apache$spark$SparkConf$$settings());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.key$2 = key$2;
            }
         }
      });
   }

   public Option<String> getWithSubstitution(String key) {
      return this.getOption(key).<String>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;

         public final String apply(String x$8) {
            return this.$outer.org$apache$spark$SparkConf$$reader().substitute(x$8);
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

   public Tuple2<String, String>[] getAll() {
      return (Tuple2[])((TraversableOnce)((SetLike)JavaConverters$.MODULE$.asScalaSetConverter(this.org$apache$spark$SparkConf$$settings().entrySet()).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, String> apply(Map.Entry<String, String> x) {
            return new Tuple2<String, String>(x.getKey(), x.getValue());
         }
      }, Set$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Tuple2.class));
   }

   public Tuple2<String, String>[] getAllWithPrefix(String prefix) {
      return (Tuple2[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(this.getAll()).filter(new Serializable(this, prefix) {
         public static final long serialVersionUID = 0L;
         private final String prefix$1;

         public final boolean apply(Tuple2<String, String> x0$4) {
            if (x0$4 != null) {
               String k = (String)x0$4._1();
               boolean var4 = k.startsWith(this.prefix$1);
               return var4;
            } else {
               throw new MatchError(x0$4);
            }
         }

         public {
            this.prefix$1 = prefix$1;
         }
      })).map(new Serializable(this, prefix) {
         public static final long serialVersionUID = 0L;
         private final String prefix$1;

         public final Tuple2<String, String> apply(Tuple2<String, String> x0$5) {
            if (x0$5 != null) {
               String k = (String)x0$5._1();
               String v = (String)x0$5._2();
               Tuple2 var5 = new Tuple2(k.substring(this.prefix$1.length()), v);
               return var5;
            } else {
               throw new MatchError(x0$5);
            }
         }

         public {
            this.prefix$1 = prefix$1;
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)));
   }

   public int getInt(String key, int defaultValue) {
      return BoxesRunTime.unboxToInt(this.getOption(key).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$9) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$9))).toInt();
         }
      }).getOrElse(new Serializable(this, defaultValue) {
         public static final long serialVersionUID = 0L;
         private final int defaultValue$3;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return this.defaultValue$3;
         }

         public {
            this.defaultValue$3 = defaultValue$3;
         }
      }));
   }

   public long getLong(String key, long defaultValue) {
      return BoxesRunTime.unboxToLong(this.getOption(key).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(String x$10) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$10))).toLong();
         }
      }).getOrElse(new Serializable(this, defaultValue) {
         public static final long serialVersionUID = 0L;
         private final long defaultValue$4;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.defaultValue$4;
         }

         public {
            this.defaultValue$4 = defaultValue$4;
         }
      }));
   }

   public double getDouble(String key, double defaultValue) {
      return BoxesRunTime.unboxToDouble(this.getOption(key).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final double apply(String x$11) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$11))).toDouble();
         }
      }).getOrElse(new Serializable(this, defaultValue) {
         public static final long serialVersionUID = 0L;
         private final double defaultValue$5;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return this.defaultValue$5;
         }

         public {
            this.defaultValue$5 = defaultValue$5;
         }
      }));
   }

   public boolean getBoolean(String key, boolean defaultValue) {
      return BoxesRunTime.unboxToBoolean(this.getOption(key).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$12) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$12))).toBoolean();
         }
      }).getOrElse(new Serializable(this, defaultValue) {
         public static final long serialVersionUID = 0L;
         private final boolean defaultValue$1;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            return this.defaultValue$1;
         }

         public {
            this.defaultValue$1 = defaultValue$1;
         }
      }));
   }

   public Seq<Tuple2<String, String>> getExecutorEnv() {
      return Predef$.MODULE$.wrapRefArray(this.getAllWithPrefix("spark.executorEnv."));
   }

   public String getAppId() {
      return this.get("spark.app.id");
   }

   public boolean contains(String key) {
      return this.org$apache$spark$SparkConf$$settings().containsKey(key) || ((IterableLike)Option$.MODULE$.option2Iterable(SparkConf$.MODULE$.org$apache$spark$SparkConf$$configsWithAlternatives().get(key)).toSeq().flatten(Predef$.MODULE$.$conforms())).exists(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;

         public final boolean apply(AlternateConfig alt) {
            return this.$outer.contains(alt.key());
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

   public boolean contains(ConfigEntry<?> entry) {
      return this.contains(entry.key());
   }

   public SparkConf clone() {
      SparkConf cloned = new SparkConf(false);
      ((IterableLike)JavaConverters$.MODULE$.asScalaSetConverter(this.org$apache$spark$SparkConf$$settings().entrySet()).asScala()).foreach(new Serializable(this, cloned) {
         public static final long serialVersionUID = 0L;
         private final SparkConf cloned$1;

         public final SparkConf apply(Map.Entry<String, String> e) {
            return this.cloned$1.set((String)e.getKey(), (String)e.getValue(), true);
         }

         public {
            this.cloned$1 = cloned$1;
         }
      });
      return cloned;
   }

   public String getenv(String name) {
      return System.getenv(name);
   }

   public void validateSettings() {
      if (this.contains("spark.local.dir")) {
         String msg = "In Spark 1.0 and later spark.local.dir will be overridden by the value set by the cluster manager (via SPARK_LOCAL_DIRS in mesos/standalone and LOCAL_DIRS in YARN).";
         this.logWarning(new Serializable(this, msg) {
            public static final long serialVersionUID = 0L;
            private final String msg$1;

            public final String apply() {
               return this.msg$1;
            }

            public {
               this.msg$1 = msg$1;
            }
         });
      }

      String executorOptsKey = "spark.executor.extraJavaOptions";
      String executorClasspathKey = "spark.executor.extraClassPath";
      String driverOptsKey = "spark.driver.extraJavaOptions";
      String driverClassPathKey = "spark.driver.extraClassPath";
      String driverLibraryPathKey = "spark.driver.extraLibraryPath";
      String sparkExecutorInstances = "spark.executor.instances";
      scala.sys.package$.MODULE$.props().get("spark.driver.libraryPath").foreach(new Serializable(this, driverLibraryPathKey) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;
         private final String driverLibraryPathKey$1;

         public final void apply(String value) {
            String warning = (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n          |spark.driver.libraryPath was detected (set to '", "').\n          |This is deprecated in Spark 1.2+.\n          |\n          |Please instead use: ", "\n        "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{value, this.driverLibraryPathKey$1}))))).stripMargin();
            this.$outer.logWarning(new Serializable(this, warning) {
               public static final long serialVersionUID = 0L;
               private final String warning$1;

               public final String apply() {
                  return this.warning$1;
               }

               public {
                  this.warning$1 = warning$1;
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.driverLibraryPathKey$1 = driverLibraryPathKey$1;
            }
         }
      });
      this.getOption(executorOptsKey).foreach(new Serializable(this, executorOptsKey) {
         public static final long serialVersionUID = 0L;
         private final String executorOptsKey$1;

         public final void apply(String javaOpts) {
            if (javaOpts.contains("-Dspark")) {
               String msg = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " is not allowed to set Spark options (was '", "'). "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorOptsKey$1, javaOpts}))).append((Object)"Set them directly on a SparkConf or in a properties file when using ./bin/spark-submit.").toString();
               throw new Exception(msg);
            } else if (javaOpts.contains("-Xmx")) {
               String msg = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " is not allowed to specify max heap memory settings "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorOptsKey$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(was '", "'). Use spark.executor.memory instead."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{javaOpts}))).toString();
               throw new Exception(msg);
            }
         }

         public {
            this.executorOptsKey$1 = executorOptsKey$1;
         }
      });
      Seq deprecatedMemoryKeys = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.storage.memoryFraction", "spark.shuffle.memoryFraction", "spark.shuffle.safetyFraction", "spark.storage.unrollFraction", "spark.storage.safetyFraction"})));
      Seq memoryKeys = (Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.memory.fraction", "spark.memory.storageFraction"})))).$plus$plus(deprecatedMemoryKeys, Seq$.MODULE$.canBuildFrom());
      memoryKeys.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConf $outer;

         public final void apply(String key) {
            double value = this.$outer.getDouble(key, (double)0.5F);
            if (value > (double)1 || value < (double)0) {
               throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " should be between 0 and 1 (was '", "')."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{key, BoxesRunTime.boxToDouble(value)})));
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
      String legacyMemoryManagementKey = "spark.memory.useLegacyMode";
      boolean legacyMemoryManagement = this.getBoolean(legacyMemoryManagementKey, false);
      if (!legacyMemoryManagement) {
         Set keyset = deprecatedMemoryKeys.toSet();
         Iterator detected = ((Iterator)JavaConverters$.MODULE$.enumerationAsScalaIteratorConverter(this.org$apache$spark$SparkConf$$settings().keys()).asScala()).filter(new Serializable(this, keyset) {
            public static final long serialVersionUID = 0L;
            private final Set keyset$1;

            public final boolean apply(String elem) {
               return this.keyset$1.contains(elem);
            }

            public {
               this.keyset$1 = keyset$1;
            }
         });
         if (detected.nonEmpty()) {
            this.logWarning(new Serializable(this, legacyMemoryManagementKey, detected) {
               public static final long serialVersionUID = 0L;
               private final String legacyMemoryManagementKey$1;
               private final Iterator detected$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Detected deprecated memory fraction settings: ").append((Object)this.detected$1.mkString("[", ", ", "]")).append((Object)". As of Spark 1.6, execution and storage ").append((Object)"memory management are unified. All memory fractions used in the old model are ").append((Object)"now deprecated and no longer read. If you wish to use the old memory management, ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"you may explicitly enable `", "` (not recommended)."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.legacyMemoryManagementKey$1}))).toString();
               }

               public {
                  this.legacyMemoryManagementKey$1 = legacyMemoryManagementKey$1;
                  this.detected$1 = detected$1;
               }
            });
         }
      }

      if (this.contains("spark.master") && this.get("spark.master").startsWith("yarn-")) {
         String warning = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.master ", " is deprecated in Spark 2.0+, please "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.get("spark.master")}))).append((Object)"instead use \"yarn\" with specified deploy mode.").toString();
         String var15 = this.get("spark.master");
         if ("yarn-cluster".equals(var15)) {
            this.logWarning(new Serializable(this, warning) {
               public static final long serialVersionUID = 0L;
               private final String warning$2;

               public final String apply() {
                  return this.warning$2;
               }

               public {
                  this.warning$2 = warning$2;
               }
            });
            this.set("spark.master", "yarn");
            SparkConf var16 = this.set("spark.submit.deployMode", "cluster");
         } else if ("yarn-client".equals(var15)) {
            this.logWarning(new Serializable(this, warning) {
               public static final long serialVersionUID = 0L;
               private final String warning$2;

               public final String apply() {
                  return this.warning$2;
               }

               public {
                  this.warning$2 = warning$2;
               }
            });
            this.set("spark.master", "yarn");
            SparkConf var28 = this.set("spark.submit.deployMode", "client");
         } else {
            BoxedUnit var29 = BoxedUnit.UNIT;
         }
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      if (this.contains("spark.submit.deployMode")) {
         String var17 = this.get("spark.submit.deployMode");
         boolean var18;
         if ("cluster".equals(var17)) {
            var18 = true;
         } else if ("client".equals(var17)) {
            var18 = true;
         } else {
            var18 = false;
         }

         if (!var18) {
            throw new SparkException("spark.submit.deployMode can only be \"cluster\" or \"client\".");
         }

         BoxedUnit var19 = BoxedUnit.UNIT;
      }

      if (this.contains("spark.cores.max") && this.contains("spark.executor.cores")) {
         int totalCores = this.getInt("spark.cores.max", 1);
         int executorCores = this.getInt("spark.executor.cores", 1);
         int leftCores = totalCores % executorCores;
         if (leftCores != 0) {
            this.logWarning(new Serializable(this, totalCores, executorCores, leftCores) {
               public static final long serialVersionUID = 0L;
               private final int totalCores$1;
               private final int executorCores$1;
               private final int leftCores$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Total executor cores: ", " is not "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.totalCores$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"divisible by cores per executor: ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.executorCores$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"the left cores: ", " will not be allocated"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.leftCores$1)}))).toString();
               }

               public {
                  this.totalCores$1 = totalCores$1;
                  this.executorCores$1 = executorCores$1;
                  this.leftCores$1 = leftCores$1;
               }
            });
         }
      }

      boolean encryptionEnabled = BoxesRunTime.unboxToBoolean(this.get(org.apache.spark.internal.config.package$.MODULE$.NETWORK_ENCRYPTION_ENABLED())) || BoxesRunTime.unboxToBoolean(this.get(org.apache.spark.internal.config.package$.MODULE$.SASL_ENCRYPTION_ENABLED()));
      Predef$.MODULE$.require(!encryptionEnabled || BoxesRunTime.unboxToBoolean(this.get(org.apache.spark.internal.config.package$.MODULE$.NETWORK_AUTH_ENABLED())), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " must be enabled when enabling encryption."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.NETWORK_AUTH_ENABLED().key()}));
         }
      });
      long executorTimeoutThreshold = this.getTimeAsSeconds("spark.network.timeout", "120s");
      long executorHeartbeatInterval = this.getTimeAsSeconds("spark.executor.heartbeatInterval", "10s");
      Predef$.MODULE$.require(executorTimeoutThreshold > executorHeartbeatInterval, new Serializable(this, executorTimeoutThreshold, executorHeartbeatInterval) {
         public static final long serialVersionUID = 0L;
         private final long executorTimeoutThreshold$1;
         private final long executorHeartbeatInterval$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"The value of ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.network.timeout=", "s must be no less than the value of "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.executorTimeoutThreshold$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.executor.heartbeatInterval=", "s."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.executorHeartbeatInterval$1)}))).toString();
         }

         public {
            this.executorTimeoutThreshold$1 = executorTimeoutThreshold$1;
            this.executorHeartbeatInterval$1 = executorHeartbeatInterval$1;
         }
      });
   }

   public String toDebugString() {
      return Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(this.getAll()).sorted(Ordering$.MODULE$.Tuple2(Ordering.String$.MODULE$, Ordering.String$.MODULE$))).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<String, String> x0$6) {
            if (x0$6 != null) {
               String k = (String)x0$6._1();
               String v = (String)x0$6._2();
               String var5 = (new StringBuilder()).append((Object)k).append((Object)"=").append((Object)v).toString();
               return var5;
            } else {
               throw new MatchError(x0$6);
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).mkString("\n");
   }

   public SparkConf(boolean loadDefaults) {
      Logging$class.$init$(this);
      this.org$apache$spark$SparkConf$$settings = new ConcurrentHashMap();
      if (loadDefaults) {
         this.loadFromSystemProperties(false);
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

   }

   public SparkConf() {
      this(true);
   }

   public static class DeprecatedConfig$ extends AbstractFunction3<String, String, String, DeprecatedConfig> implements Serializable {
      public static final DeprecatedConfig$ MODULE$;

      static {
         new DeprecatedConfig$();
      }

      public final String toString() {
         return "DeprecatedConfig";
      }

      public DeprecatedConfig apply(String key, String version, String deprecationMessage) {
         return new DeprecatedConfig(key, version, deprecationMessage);
      }

      public Option<Tuple3<String, String, String>> unapply(DeprecatedConfig x$0) {
         return (Option<Tuple3<String, String, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.key(), x$0.version(), x$0.deprecationMessage())));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public DeprecatedConfig$() {
         MODULE$ = this;
      }
   }

   public static class DeprecatedConfig implements Product, Serializable {
      private final String key;
      private final String version;
      private final String deprecationMessage;

      public String key() {
         return this.key;
      }

      public String version() {
         return this.version;
      }

      public String deprecationMessage() {
         return this.deprecationMessage;
      }

      public DeprecatedConfig copy(String key, String version, String deprecationMessage) {
         return new DeprecatedConfig(key, version, deprecationMessage);
      }

      public String copy$default$1() {
         return this.key();
      }

      public String copy$default$2() {
         return this.version();
      }

      public String copy$default$3() {
         return this.deprecationMessage();
      }

      public String productPrefix() {
         return "DeprecatedConfig";
      }

      public int productArity() {
         return 3;
      }

      public Object productElement(int x$1) {
         String var10000;
         switch (x$1) {
            case 0:
               var10000 = this.key();
               break;
            case 1:
               var10000 = this.version();
               break;
            case 2:
               var10000 = this.deprecationMessage();
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
         return x$1 instanceof DeprecatedConfig;
      }

      public int hashCode() {
         return ScalaRunTime$.MODULE$._hashCode(this);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var11;
         if (this != x$1) {
            label71: {
               boolean var3;
               if (x$1 instanceof DeprecatedConfig) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label53: {
                     label62: {
                        DeprecatedConfig var4 = (DeprecatedConfig)x$1;
                        String var10000 = this.key();
                        String var5 = var4.key();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label62;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label62;
                        }

                        var10000 = this.version();
                        String var6 = var4.version();
                        if (var10000 == null) {
                           if (var6 != null) {
                              break label62;
                           }
                        } else if (!var10000.equals(var6)) {
                           break label62;
                        }

                        var10000 = this.deprecationMessage();
                        String var7 = var4.deprecationMessage();
                        if (var10000 == null) {
                           if (var7 != null) {
                              break label62;
                           }
                        } else if (!var10000.equals(var7)) {
                           break label62;
                        }

                        if (var4.canEqual(this)) {
                           var11 = true;
                           break label53;
                        }
                     }

                     var11 = false;
                  }

                  if (var11) {
                     break label71;
                  }
               }

               var11 = false;
               return var11;
            }
         }

         var11 = true;
         return var11;
      }

      public DeprecatedConfig(String key, String version, String deprecationMessage) {
         this.key = key;
         this.version = version;
         this.deprecationMessage = deprecationMessage;
         Product$class.$init$(this);
      }
   }

   public static class AlternateConfig$ extends AbstractFunction3<String, String, Function1<String, String>, AlternateConfig> implements Serializable {
      public static final AlternateConfig$ MODULE$;

      static {
         new AlternateConfig$();
      }

      public final String toString() {
         return "AlternateConfig";
      }

      public AlternateConfig apply(String key, String version, Function1<String, String> translation) {
         return new AlternateConfig(key, version, translation);
      }

      public Option<Tuple3<String, String, Function1<String, String>>> unapply(AlternateConfig x$0) {
         return (Option<Tuple3<String, String, Function1<String, String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.key(), x$0.version(), x$0.translation())));
      }

      public Function1<String, String> apply$default$3() {
         return null;
      }

      public Function1<String, String> $lessinit$greater$default$3() {
         return null;
      }

      private Object readResolve() {
         return MODULE$;
      }

      public AlternateConfig$() {
         MODULE$ = this;
      }
   }

   public static class AlternateConfig implements Product, Serializable {
      private final String key;
      private final String version;
      private final Function1<String, String> translation;

      public String key() {
         return this.key;
      }

      public String version() {
         return this.version;
      }

      public Function1<String, String> translation() {
         return this.translation;
      }

      public AlternateConfig copy(String key, String version, Function1<String, String> translation) {
         return new AlternateConfig(key, version, translation);
      }

      public String copy$default$1() {
         return this.key();
      }

      public String copy$default$2() {
         return this.version();
      }

      public Function1<String, String> copy$default$3() {
         return this.translation();
      }

      public String productPrefix() {
         return "AlternateConfig";
      }

      public int productArity() {
         return 3;
      }

      public Object productElement(int x$1) {
         Object var10000;
         switch (x$1) {
            case 0:
               var10000 = this.key();
               break;
            case 1:
               var10000 = this.version();
               break;
            case 2:
               var10000 = this.translation();
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
         return x$1 instanceof AlternateConfig;
      }

      public int hashCode() {
         return ScalaRunTime$.MODULE$._hashCode(this);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var11;
         if (this != x$1) {
            label71: {
               boolean var3;
               if (x$1 instanceof AlternateConfig) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label53: {
                     label62: {
                        AlternateConfig var4 = (AlternateConfig)x$1;
                        String var10000 = this.key();
                        String var5 = var4.key();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label62;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label62;
                        }

                        var10000 = this.version();
                        String var6 = var4.version();
                        if (var10000 == null) {
                           if (var6 != null) {
                              break label62;
                           }
                        } else if (!var10000.equals(var6)) {
                           break label62;
                        }

                        Function1 var9 = this.translation();
                        Function1 var7 = var4.translation();
                        if (var9 == null) {
                           if (var7 != null) {
                              break label62;
                           }
                        } else if (!var9.equals(var7)) {
                           break label62;
                        }

                        if (var4.canEqual(this)) {
                           var11 = true;
                           break label53;
                        }
                     }

                     var11 = false;
                  }

                  if (var11) {
                     break label71;
                  }
               }

               var11 = false;
               return var11;
            }
         }

         var11 = true;
         return var11;
      }

      public AlternateConfig(String key, String version, Function1<String, String> translation) {
         this.key = key;
         this.version = version;
         this.translation = translation;
         Product$class.$init$(this);
      }
   }
}
