package org.apache.spark;

import java.util.Properties;
import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.memory.TaskMemoryManager;
import org.apache.spark.metrics.MetricsSystem;
import org.apache.spark.metrics.source.Source;
import org.apache.spark.shuffle.FetchFailedException;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.TaskCompletionListener;
import org.apache.spark.util.TaskCompletionListenerException;
import org.apache.spark.util.TaskFailureListener;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.IterableLike;
import scala.collection.Seq;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\tuh!B\u0001\u0003\u0001\tA!a\u0004+bg.\u001cuN\u001c;fqRLU\u000e\u001d7\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001c2\u0001A\u0005\u000e!\tQ1\"D\u0001\u0003\u0013\ta!AA\u0006UCN\\7i\u001c8uKb$\bC\u0001\b\u0012\u001b\u0005y!B\u0001\t\u0003\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\n\u0010\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0006\u0001\u0003\u0006\u0004%\tEF\u0001\bgR\fw-Z%e\u0007\u0001)\u0012a\u0006\t\u00031mi\u0011!\u0007\u0006\u00025\u0005)1oY1mC&\u0011A$\u0007\u0002\u0004\u0013:$\b\u0002\u0003\u0010\u0001\u0005\u0003\u0005\u000b\u0011B\f\u0002\u0011M$\u0018mZ3JI\u0002B\u0001\u0002\t\u0001\u0003\u0006\u0004%\tEF\u0001\u0013gR\fw-Z!ui\u0016l\u0007\u000f\u001e(v[\n,'\u000f\u0003\u0005#\u0001\t\u0005\t\u0015!\u0003\u0018\u0003M\u0019H/Y4f\u0003R$X-\u001c9u\u001dVl'-\u001a:!\u0011!!\u0003A!b\u0001\n\u00032\u0012a\u00039beRLG/[8o\u0013\u0012D\u0001B\n\u0001\u0003\u0002\u0003\u0006IaF\u0001\ra\u0006\u0014H/\u001b;j_:LE\r\t\u0005\tQ\u0001\u0011)\u0019!C!S\u0005iA/Y:l\u0003R$X-\u001c9u\u0013\u0012,\u0012A\u000b\t\u00031-J!\u0001L\r\u0003\t1{gn\u001a\u0005\t]\u0001\u0011\t\u0011)A\u0005U\u0005qA/Y:l\u0003R$X-\u001c9u\u0013\u0012\u0004\u0003\u0002\u0003\u0019\u0001\u0005\u000b\u0007I\u0011\t\f\u0002\u001b\u0005$H/Z7qi:+XNY3s\u0011!\u0011\u0004A!A!\u0002\u00139\u0012AD1ui\u0016l\u0007\u000f\u001e(v[\n,'\u000f\t\u0005\ti\u0001\u0011)\u0019!C!k\u0005\tB/Y:l\u001b\u0016lwN]=NC:\fw-\u001a:\u0016\u0003Y\u0002\"a\u000e\u001e\u000e\u0003aR!!\u000f\u0002\u0002\r5,Wn\u001c:z\u0013\tY\u0004HA\tUCN\\W*Z7pefl\u0015M\\1hKJD\u0001\"\u0010\u0001\u0003\u0002\u0003\u0006IAN\u0001\u0013i\u0006\u001c8.T3n_JLX*\u00198bO\u0016\u0014\b\u0005\u0003\u0005@\u0001\t\u0005\t\u0015!\u0003A\u0003=awnY1m!J|\u0007/\u001a:uS\u0016\u001c\bCA!G\u001b\u0005\u0011%BA\"E\u0003\u0011)H/\u001b7\u000b\u0003\u0015\u000bAA[1wC&\u0011qI\u0011\u0002\u000b!J|\u0007/\u001a:uS\u0016\u001c\b\u0002C%\u0001\u0005\u000b\u0007I\u0011\u0002&\u0002\u001b5,GO]5dgNK8\u000f^3n+\u0005Y\u0005C\u0001'P\u001b\u0005i%B\u0001(\u0003\u0003\u001diW\r\u001e:jGNL!\u0001U'\u0003\u001b5+GO]5dgNK8\u000f^3n\u0011!\u0011\u0006A!A!\u0002\u0013Y\u0015AD7fiJL7m]*zgR,W\u000e\t\u0015\u0003#R\u0003\"\u0001G+\n\u0005YK\"!\u0003;sC:\u001c\u0018.\u001a8u\u0011!A\u0006A!b\u0001\n\u0003J\u0016a\u0003;bg.lU\r\u001e:jGN,\u0012A\u0017\t\u00037zk\u0011\u0001\u0018\u0006\u0003;\n\t\u0001\"\u001a=fGV$xN]\u0005\u0003?r\u00131\u0002V1tW6+GO]5dg\"A\u0011\r\u0001B\u0001B\u0003%!,\u0001\u0007uCN\\W*\u001a;sS\u000e\u001c\b\u0005C\u0003d\u0001\u0011\u0005A-\u0001\u0004=S:LGO\u0010\u000b\u000bK\u001a<\u0007.\u001b6lY6t\u0007C\u0001\u0006\u0001\u0011\u0015!\"\r1\u0001\u0018\u0011\u0015\u0001#\r1\u0001\u0018\u0011\u0015!#\r1\u0001\u0018\u0011\u0015A#\r1\u0001+\u0011\u0015\u0001$\r1\u0001\u0018\u0011\u0015!$\r1\u00017\u0011\u0015y$\r1\u0001A\u0011\u0015I%\r1\u0001L\u0011\u001dA&\r%AA\u0002iCq\u0001\u001d\u0001C\u0002\u0013%\u0011/A\np]\u000e{W\u000e\u001d7fi\u0016\u001c\u0015\r\u001c7cC\u000e\\7/F\u0001s!\r\u0019\bP_\u0007\u0002i*\u0011QO^\u0001\b[V$\u0018M\u00197f\u0015\t9\u0018$\u0001\u0006d_2dWm\u0019;j_:L!!\u001f;\u0003\u0017\u0005\u0013(/Y=Ck\u001a4WM\u001d\t\u0003wvl\u0011\u0001 \u0006\u0003\u0007\nI!A ?\u0003-Q\u000b7o[\"p[BdW\r^5p]2K7\u000f^3oKJDq!!\u0001\u0001A\u0003%!/\u0001\u000bp]\u000e{W\u000e\u001d7fi\u0016\u001c\u0015\r\u001c7cC\u000e\\7\u000f\t\u0015\u0003\u007fRC\u0011\"a\u0002\u0001\u0005\u0004%I!!\u0003\u0002%=tg)Y5mkJ,7)\u00197mE\u0006\u001c7n]\u000b\u0003\u0003\u0017\u0001Ba\u001d=\u0002\u000eA\u001910a\u0004\n\u0007\u0005EAPA\nUCN\\g)Y5mkJ,G*[:uK:,'\u000f\u0003\u0005\u0002\u0016\u0001\u0001\u000b\u0011BA\u0006\u0003MygNR1jYV\u0014XmQ1mY\n\f7m[:!Q\r\t\u0019\u0002\u0016\u0005\n\u00037\u0001\u0001\u0019!C\u0005\u0003;\taB]3bg>t\u0017JZ&jY2,G-\u0006\u0002\u0002 A)\u0001$!\t\u0002&%\u0019\u00111E\r\u0003\r=\u0003H/[8o!\u0011\t9#!\f\u000f\u0007a\tI#C\u0002\u0002,e\ta\u0001\u0015:fI\u00164\u0017\u0002BA\u0018\u0003c\u0011aa\u0015;sS:<'bAA\u00163!I\u0011Q\u0007\u0001A\u0002\u0013%\u0011qG\u0001\u0013e\u0016\f7o\u001c8JM.KG\u000e\\3e?\u0012*\u0017\u000f\u0006\u0003\u0002:\u0005}\u0002c\u0001\r\u0002<%\u0019\u0011QH\r\u0003\tUs\u0017\u000e\u001e\u0005\u000b\u0003\u0003\n\u0019$!AA\u0002\u0005}\u0011a\u0001=%c!A\u0011Q\t\u0001!B\u0013\ty\"A\bsK\u0006\u001cxN\\%g\u0017&dG.\u001a3!Q\u0011\t\u0019%!\u0013\u0011\u0007a\tY%C\u0002\u0002Ne\u0011\u0001B^8mCRLG.\u001a\u0005\n\u0003#\u0002\u0001\u0019!C\u0005\u0003'\n\u0011bY8na2,G/\u001a3\u0016\u0005\u0005U\u0003c\u0001\r\u0002X%\u0019\u0011\u0011L\r\u0003\u000f\t{w\u000e\\3b]\"I\u0011Q\f\u0001A\u0002\u0013%\u0011qL\u0001\u000eG>l\u0007\u000f\\3uK\u0012|F%Z9\u0015\t\u0005e\u0012\u0011\r\u0005\u000b\u0003\u0003\nY&!AA\u0002\u0005U\u0003\u0002CA3\u0001\u0001\u0006K!!\u0016\u0002\u0015\r|W\u000e\u001d7fi\u0016$\u0007\u0005C\u0005\u0002j\u0001\u0001\r\u0011\"\u0003\u0002T\u00051a-Y5mK\u0012D\u0011\"!\u001c\u0001\u0001\u0004%I!a\u001c\u0002\u0015\u0019\f\u0017\u000e\\3e?\u0012*\u0017\u000f\u0006\u0003\u0002:\u0005E\u0004BCA!\u0003W\n\t\u00111\u0001\u0002V!A\u0011Q\u000f\u0001!B\u0013\t)&A\u0004gC&dW\r\u001a\u0011\t\u0017\u0005e\u0004\u00011AA\u0002\u0013%\u00111P\u0001\bM\u0006LG.\u001e:f+\t\ti\b\u0005\u0003\u0002\u0000\u0005=e\u0002BAA\u0003\u0017sA!a!\u0002\n6\u0011\u0011Q\u0011\u0006\u0004\u0003\u000f+\u0012A\u0002\u001fs_>$h(C\u0001\u001b\u0013\r\ti)G\u0001\ba\u0006\u001c7.Y4f\u0013\u0011\t\t*a%\u0003\u0013QC'o\\<bE2,'bAAG3!Y\u0011q\u0013\u0001A\u0002\u0003\u0007I\u0011BAM\u0003-1\u0017-\u001b7ve\u0016|F%Z9\u0015\t\u0005e\u00121\u0014\u0005\u000b\u0003\u0003\n)*!AA\u0002\u0005u\u0004\u0002CAP\u0001\u0001\u0006K!! \u0002\u0011\u0019\f\u0017\u000e\\;sK\u0002B\u0011\"a)\u0001\u0001\u0004%I!!*\u0002+}3W\r^2i\r\u0006LG.\u001a3Fq\u000e,\u0007\u000f^5p]V\u0011\u0011q\u0015\t\u00061\u0005\u0005\u0012\u0011\u0016\t\u0005\u0003W\u000b\t,\u0004\u0002\u0002.*\u0019\u0011q\u0016\u0002\u0002\u000fMDWO\u001a4mK&!\u00111WAW\u0005Q1U\r^2i\r\u0006LG.\u001a3Fq\u000e,\u0007\u000f^5p]\"I\u0011q\u0017\u0001A\u0002\u0013%\u0011\u0011X\u0001\u001a?\u001a,Go\u00195GC&dW\rZ#yG\u0016\u0004H/[8o?\u0012*\u0017\u000f\u0006\u0003\u0002:\u0005m\u0006BCA!\u0003k\u000b\t\u00111\u0001\u0002(\"A\u0011q\u0018\u0001!B\u0013\t9+\u0001\f`M\u0016$8\r\u001b$bS2,G-\u0012=dKB$\u0018n\u001c8!Q\u0011\ti,!\u0013\t\u000f\u0005\u0015\u0007\u0001\"\u0011\u0002H\u0006I\u0012\r\u001a3UCN\\7i\\7qY\u0016$\u0018n\u001c8MSN$XM\\3s)\u0011\tI-a3\u000e\u0003\u0001Aq!!4\u0002D\u0002\u0007!0\u0001\u0005mSN$XM\\3sQ!\t\u0019-!5\u0002f\u0006\u001d\b\u0003BAj\u0003Cl!!!6\u000b\t\u0005]\u0017\u0011\\\u0001\u000bG>t7-\u001e:sK:$(\u0002BAn\u0003;\f!\"\u00198o_R\fG/[8o\u0015\t\ty.A\u0003kCZ\f\u00070\u0003\u0003\u0002d\u0006U'!C$vCJ$W\r\u001a\"z\u0003\u00151\u0018\r\\;fC\t\tI/\u0001\u0003uQ&\u001c\bbBAw\u0001\u0011\u0005\u0013q^\u0001\u0017C\u0012$G+Y:l\r\u0006LG.\u001e:f\u0019&\u001cH/\u001a8feR!\u0011\u0011ZAy\u0011!\ti-a;A\u0002\u00055\u0001\u0006CAv\u0003#\f)/a:\t\u0011\u0005]\b\u0001\"\u0001\u0003\u0003s\fa\"\\1sWR\u000b7o\u001b$bS2,G\r\u0006\u0003\u0002:\u0005m\b\u0002CA\u007f\u0003k\u0004\r!! \u0002\u000b\u0015\u0014(o\u001c:)\u0011\u0005U\u0018\u0011[As\u0003OD\u0001Ba\u0001\u0001\t\u0003\u0011!QA\u0001\u0012[\u0006\u00148\u000eV1tW\u000e{W\u000e\u001d7fi\u0016$G\u0003BA\u001d\u0005\u000fA\u0001\"!@\u0003\u0002\u0001\u0007!\u0011\u0002\t\u00061\u0005\u0005\u0012Q\u0010\u0015\t\u0005\u0003\t\t.!:\u0002h\"9!q\u0002\u0001\u0005\n\tE\u0011aD5om>\\W\rT5ti\u0016tWM]:\u0016\t\tM!Q\u0005\u000b\t\u0005+\u00119D!\u0011\u0003FQ!\u0011\u0011\bB\f\u0011!\u0011IB!\u0004A\u0002\tm\u0011\u0001C2bY2\u0014\u0017mY6\u0011\u000fa\u0011iB!\t\u0002:%\u0019!qD\r\u0003\u0013\u0019+hn\u0019;j_:\f\u0004\u0003\u0002B\u0012\u0005Ka\u0001\u0001\u0002\u0005\u0003(\t5!\u0019\u0001B\u0015\u0005\u0005!\u0016\u0003\u0002B\u0016\u0005c\u00012\u0001\u0007B\u0017\u0013\r\u0011y#\u0007\u0002\b\u001d>$\b.\u001b8h!\rA\"1G\u0005\u0004\u0005kI\"aA!os\"A!\u0011\bB\u0007\u0001\u0004\u0011Y$A\u0005mSN$XM\\3sgB1\u0011q\u0010B\u001f\u0005CIAAa\u0010\u0002\u0014\n\u00191+Z9\t\u0011\t\r#Q\u0002a\u0001\u0003K\tAA\\1nK\"A\u0011Q B\u0007\u0001\u0004\u0011I\u0001\u0003\u0005\u0003J\u0001!\tA\u0001B&\u0003=i\u0017M]6J]R,'O];qi\u0016$G\u0003BA\u001d\u0005\u001bB\u0001Ba\u0014\u0003H\u0001\u0007\u0011QE\u0001\u0007e\u0016\f7o\u001c8\t\u0011\tM\u0003\u0001\"\u0011\u0003\u0005+\nQc[5mYR\u000b7o[%g\u0013:$XM\u001d:vaR,G\r\u0006\u0002\u0002:!A!\u0011\f\u0001\u0005B\t\u0011Y&A\u0007hKR\\\u0015\u000e\u001c7SK\u0006\u001cxN\u001c\u000b\u0003\u0003?AqAa\u0018\u0001\t\u0003\u0012\t'A\u0006jg\u000e{W\u000e\u001d7fi\u0016$GCAA+Q!\u0011i&!5\u0002f\u0006\u001d\bb\u0002B4\u0001\u0011\u0005#\u0011M\u0001\u0011SN\u0014VO\u001c8j]\u001edunY1mYfDqAa\u001b\u0001\t\u0003\u0012\t'A\u0007jg&sG/\u001a:skB$X\r\u001a\u0005\b\u0005_\u0002A\u0011\tB9\u0003A9W\r\u001e'pG\u0006d\u0007K]8qKJ$\u0018\u0010\u0006\u0003\u0002&\tM\u0004\u0002\u0003B;\u0005[\u0002\r!!\n\u0002\u0007-,\u0017\u0010C\u0004\u0003z\u0001!\tEa\u001f\u0002#\u001d,G/T3ue&\u001c7oU8ve\u000e,7\u000f\u0006\u0003\u0003~\t-\u0005CBA@\u0005{\u0011y\b\u0005\u0003\u0003\u0002\n\u001dUB\u0001BB\u0015\r\u0011))T\u0001\u0007g>,(oY3\n\t\t%%1\u0011\u0002\u0007'>,(oY3\t\u0011\t5%q\u000fa\u0001\u0003K\t!b]8ve\u000e,g*Y7f\u0011!\u0011\t\n\u0001C!\u0005\tM\u0015a\u0005:fO&\u001cH/\u001a:BG\u000e,X.\u001e7bi>\u0014H\u0003BA\u001d\u0005+C\u0001Ba&\u0003\u0010\u0002\u0007!\u0011T\u0001\u0002CB2!1\u0014BR\u0005S\u0003ra\u001fBO\u0005C\u00139+C\u0002\u0003 r\u0014Q\"Q2dk6,H.\u0019;peZ\u0013\u0004\u0003\u0002B\u0012\u0005G#AB!*\u0003\u0016\u0006\u0005\t\u0011!B\u0001\u0005S\u00111a\u0018\u00132!\u0011\u0011\u0019C!+\u0005\u0019\t-&QSA\u0001\u0002\u0003\u0015\tA!\u000b\u0003\u0007}##\u0007\u0003\u0005\u00030\u0002!\tE\u0001BY\u00039\u0019X\r\u001e$fi\u000eDg)Y5mK\u0012$B!!\u000f\u00034\"A!Q\u0017BW\u0001\u0004\tI+A\u0006gKR\u001c\u0007NR1jY\u0016$\u0007\u0002\u0003B[\u0001\u0011\u0005!!!*\b\u0015\tm&!!A\t\u0002\t\u0011i,A\bUCN\\7i\u001c8uKb$\u0018*\u001c9m!\rQ!q\u0018\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0005\u0003\u001cbAa0\u0003D\n%\u0007c\u0001\r\u0003F&\u0019!qY\r\u0003\r\u0005s\u0017PU3g!\rA\"1Z\u0005\u0004\u0005\u001bL\"\u0001D*fe&\fG.\u001b>bE2,\u0007bB2\u0003@\u0012\u0005!\u0011\u001b\u000b\u0003\u0005{C!B!6\u0003@F\u0005I\u0011\u0001Bl\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%sU\u0011!\u0011\u001c\u0016\u00045\nm7F\u0001Bo!\u0011\u0011yNa:\u000e\u0005\t\u0005(\u0002\u0002Br\u0005K\f\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\u0005m\u0017$\u0003\u0003\u0003j\n\u0005(!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"Q!Q\u001eB`\u0003\u0003%IAa<\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0005c\u0004BAa=\u0003z6\u0011!Q\u001f\u0006\u0004\u0005o$\u0015\u0001\u00027b]\u001eLAAa?\u0003v\n1qJ\u00196fGR\u0004"
)
public class TaskContextImpl extends TaskContext implements Logging {
   private final int stageId;
   private final int stageAttemptNumber;
   private final int partitionId;
   private final long taskAttemptId;
   private final int attemptNumber;
   private final TaskMemoryManager taskMemoryManager;
   private final Properties localProperties;
   private final transient MetricsSystem metricsSystem;
   private final TaskMetrics taskMetrics;
   private final transient ArrayBuffer<TaskCompletionListener> onCompleteCallbacks;
   private final transient ArrayBuffer<TaskFailureListener> onFailureCallbacks;
   private volatile Option<String> reasonIfKilled;
   private boolean completed;
   private boolean failed;
   private Throwable failure;
   private volatile Option<FetchFailedException> _fetchFailedException;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static TaskMetrics $lessinit$greater$default$9() {
      return TaskContextImpl$.MODULE$.$lessinit$greater$default$9();
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

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptNumber() {
      return this.stageAttemptNumber;
   }

   public int partitionId() {
      return this.partitionId;
   }

   public long taskAttemptId() {
      return this.taskAttemptId;
   }

   public int attemptNumber() {
      return this.attemptNumber;
   }

   public TaskMemoryManager taskMemoryManager() {
      return this.taskMemoryManager;
   }

   private MetricsSystem metricsSystem() {
      return this.metricsSystem;
   }

   public TaskMetrics taskMetrics() {
      return this.taskMetrics;
   }

   private ArrayBuffer<TaskCompletionListener> onCompleteCallbacks() {
      return this.onCompleteCallbacks;
   }

   private ArrayBuffer<TaskFailureListener> onFailureCallbacks() {
      return this.onFailureCallbacks;
   }

   private Option<String> reasonIfKilled() {
      return this.reasonIfKilled;
   }

   private void reasonIfKilled_$eq(Option<String> x$1) {
      this.reasonIfKilled = x$1;
   }

   private boolean completed() {
      return this.completed;
   }

   private void completed_$eq(boolean x$1) {
      this.completed = x$1;
   }

   private boolean failed() {
      return this.failed;
   }

   private void failed_$eq(boolean x$1) {
      this.failed = x$1;
   }

   private Throwable failure() {
      return this.failure;
   }

   private void failure_$eq(Throwable x$1) {
      this.failure = x$1;
   }

   private Option<FetchFailedException> _fetchFailedException() {
      return this._fetchFailedException;
   }

   private void _fetchFailedException_$eq(Option<FetchFailedException> x$1) {
      this._fetchFailedException = x$1;
   }

   @GuardedBy("this")
   public synchronized TaskContextImpl addTaskCompletionListener(TaskCompletionListener listener) {
      if (this.completed()) {
         listener.onTaskCompletion(this);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         this.onCompleteCallbacks().$plus$eq(listener);
      }

      return this;
   }

   @GuardedBy("this")
   public synchronized TaskContextImpl addTaskFailureListener(TaskFailureListener listener) {
      if (this.failed()) {
         listener.onTaskFailure(this, this.failure());
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         this.onFailureCallbacks().$plus$eq(listener);
      }

      return this;
   }

   @GuardedBy("this")
   public synchronized void markTaskFailed(Throwable error) {
      if (!this.failed()) {
         this.failed_$eq(true);
         this.failure_$eq(error);
         this.invokeListeners(this.onFailureCallbacks(), "TaskFailureListener", Option$.MODULE$.apply(error), new Serializable(this, error) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskContextImpl $outer;
            private final Throwable error$1;

            public final void apply(TaskFailureListener x$1) {
               x$1.onTaskFailure(this.$outer, this.error$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.error$1 = error$1;
               }
            }
         });
      }
   }

   @GuardedBy("this")
   public synchronized void markTaskCompleted(Option<Throwable> error) {
      if (!this.completed()) {
         this.completed_$eq(true);
         this.invokeListeners(this.onCompleteCallbacks(), "TaskCompletionListener", error, new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskContextImpl $outer;

            public final void apply(TaskCompletionListener x$2) {
               x$2.onTaskCompletion(this.$outer);
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
   }

   private <T> void invokeListeners(Seq<T> listeners, String name, Option<Throwable> error, Function1<T, BoxedUnit> callback) {
      ArrayBuffer errorMsgs = new ArrayBuffer(2);
      ((IterableLike)listeners.reverse()).foreach(new Serializable(this, name, callback, errorMsgs) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskContextImpl $outer;
         public final String name$1;
         private final Function1 callback$1;
         private final ArrayBuffer errorMsgs$1;

         public final void apply(T listener) {
            try {
               this.callback$1.apply(listener);
            } catch (Throwable var4) {
               this.errorMsgs$1.$plus$eq(var4.getMessage());
               this.$outer.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error in ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.name$1}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, var4);
               return;
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.name$1 = name$1;
               this.callback$1 = callback$1;
               this.errorMsgs$1 = errorMsgs$1;
            }
         }
      });
      if (errorMsgs.nonEmpty()) {
         throw new TaskCompletionListenerException(errorMsgs, error);
      }
   }

   public void markInterrupted(String reason) {
      this.reasonIfKilled_$eq(new Some(reason));
   }

   public void killTaskIfInterrupted() {
      Option reason = this.reasonIfKilled();
      if (reason.isDefined()) {
         throw new TaskKilledException((String)reason.get());
      }
   }

   public Option<String> getKillReason() {
      return this.reasonIfKilled();
   }

   @GuardedBy("this")
   public synchronized boolean isCompleted() {
      return this.completed();
   }

   public boolean isRunningLocally() {
      return false;
   }

   public boolean isInterrupted() {
      return this.reasonIfKilled().isDefined();
   }

   public String getLocalProperty(String key) {
      return this.localProperties.getProperty(key);
   }

   public Seq<Source> getMetricsSources(String sourceName) {
      return this.metricsSystem().getSourcesByName(sourceName);
   }

   public void registerAccumulator(AccumulatorV2<?, ?> a) {
      this.taskMetrics().registerAccumulator(a);
   }

   public void setFetchFailed(FetchFailedException fetchFailed) {
      this._fetchFailedException_$eq(Option$.MODULE$.apply(fetchFailed));
   }

   public Option<FetchFailedException> fetchFailed() {
      return this._fetchFailedException();
   }

   public TaskContextImpl(int stageId, int stageAttemptNumber, int partitionId, long taskAttemptId, int attemptNumber, TaskMemoryManager taskMemoryManager, Properties localProperties, MetricsSystem metricsSystem, TaskMetrics taskMetrics) {
      this.stageId = stageId;
      this.stageAttemptNumber = stageAttemptNumber;
      this.partitionId = partitionId;
      this.taskAttemptId = taskAttemptId;
      this.attemptNumber = attemptNumber;
      this.taskMemoryManager = taskMemoryManager;
      this.localProperties = localProperties;
      this.metricsSystem = metricsSystem;
      this.taskMetrics = taskMetrics;
      Logging$class.$init$(this);
      this.onCompleteCallbacks = new ArrayBuffer<TaskCompletionListener>();
      this.onFailureCallbacks = new ArrayBuffer<TaskFailureListener>();
      this.reasonIfKilled = None$.MODULE$;
      this.completed = false;
      this.failed = false;
      this._fetchFailedException = None$.MODULE$;
   }
}
