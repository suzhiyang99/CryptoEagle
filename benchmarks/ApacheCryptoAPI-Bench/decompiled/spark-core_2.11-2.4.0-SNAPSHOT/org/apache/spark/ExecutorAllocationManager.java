package org.apache.spark;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.metrics.source.Source;
import org.apache.spark.scheduler.LiveListenerBus;
import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.scheduler.SparkListenerExecutorAdded;
import org.apache.spark.scheduler.SparkListenerExecutorRemoved;
import org.apache.spark.scheduler.SparkListenerSpeculativeTaskSubmitted;
import org.apache.spark.scheduler.SparkListenerStageCompleted;
import org.apache.spark.scheduler.SparkListenerStageSubmitted;
import org.apache.spark.scheduler.SparkListenerTaskEnd;
import org.apache.spark.scheduler.SparkListenerTaskStart;
import org.apache.spark.scheduler.TaskLocation;
import org.apache.spark.util.Clock;
import org.apache.spark.util.SystemClock;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SetLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.Iterable$;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.util.control.ControlThrowable;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\r5h!B\u0001\u0003\u0001\tA!!G#yK\u000e,Ho\u001c:BY2|7-\u0019;j_:l\u0015M\\1hKJT!a\u0001\u0003\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u00151\u0011AB1qC\u000eDWMC\u0001\b\u0003\ry'oZ\n\u0004\u0001%y\u0001C\u0001\u0006\u000e\u001b\u0005Y!\"\u0001\u0007\u0002\u000bM\u001c\u0017\r\\1\n\u00059Y!AB!osJ+g\r\u0005\u0002\u0011'5\t\u0011C\u0003\u0002\u0013\u0005\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u0015#\t9Aj\\4hS:<\u0007\u0002\u0003\f\u0001\u0005\u0003\u0005\u000b\u0011\u0002\r\u0002\r\rd\u0017.\u001a8u\u0007\u0001\u0001\"!\u0007\u000e\u000e\u0003\tI!a\u0007\u0002\u00031\u0015CXmY;u_J\fE\u000e\\8dCRLwN\\\"mS\u0016tG\u000f\u0003\u0005\u001e\u0001\t\u0005\t\u0015!\u0003\u001f\u0003-a\u0017n\u001d;f]\u0016\u0014()^:\u0011\u0005}\u0011S\"\u0001\u0011\u000b\u0005\u0005\u0012\u0011!C:dQ\u0016$W\u000f\\3s\u0013\t\u0019\u0003EA\bMSZ,G*[:uK:,'OQ;t\u0011!)\u0003A!A!\u0002\u00131\u0013\u0001B2p]\u001a\u0004\"!G\u0014\n\u0005!\u0012!!C*qCJ\\7i\u001c8g\u0011\u0015Q\u0003\u0001\"\u0001,\u0003\u0019a\u0014N\\5u}Q!A&\f\u00180!\tI\u0002\u0001C\u0003\u0017S\u0001\u0007\u0001\u0004C\u0003\u001eS\u0001\u0007a\u0004C\u0003&S\u0001\u0007a\u0005C\u00042\u0001\t\u0007I\u0011\u0002\u001a\u0002\u001f5LgNT;n\u000bb,7-\u001e;peN,\u0012a\r\t\u0003\u0015QJ!!N\u0006\u0003\u0007%sG\u000f\u0003\u00048\u0001\u0001\u0006IaM\u0001\u0011[&tg*^7Fq\u0016\u001cW\u000f^8sg\u0002Bq!\u000f\u0001C\u0002\u0013%!'A\bnCbtU/\\#yK\u000e,Ho\u001c:t\u0011\u0019Y\u0004\u0001)A\u0005g\u0005\u0001R.\u0019=Ok6,\u00050Z2vi>\u00148\u000f\t\u0005\b{\u0001\u0011\r\u0011\"\u00033\u0003MIg.\u001b;jC2tU/\\#yK\u000e,Ho\u001c:t\u0011\u0019y\u0004\u0001)A\u0005g\u0005!\u0012N\\5uS\u0006dg*^7Fq\u0016\u001cW\u000f^8sg\u0002Bq!\u0011\u0001C\u0002\u0013%!)\u0001\rtG\",G-\u001e7fe\n\u000b7m\u001b7pORKW.Z8viN+\u0012a\u0011\t\u0003\u0015\u0011K!!R\u0006\u0003\t1{gn\u001a\u0005\u0007\u000f\u0002\u0001\u000b\u0011B\"\u00023M\u001c\u0007.\u001a3vY\u0016\u0014()Y2lY><G+[7f_V$8\u000b\t\u0005\b\u0013\u0002\u0011\r\u0011\"\u0003C\u0003\u0005\u001aXo\u001d;bS:,GmU2iK\u0012,H.\u001a:CC\u000e\\Gn\\4US6,w.\u001e;T\u0011\u0019Y\u0005\u0001)A\u0005\u0007\u0006\u00113/^:uC&tW\rZ*dQ\u0016$W\u000f\\3s\u0005\u0006\u001c7\u000e\\8h)&lWm\\;u'\u0002Bq!\u0014\u0001C\u0002\u0013%!)\u0001\u000bfq\u0016\u001cW\u000f^8s\u0013\u0012dW\rV5nK>,Ho\u0015\u0005\u0007\u001f\u0002\u0001\u000b\u0011B\"\u0002+\u0015DXmY;u_JLE\r\\3US6,w.\u001e;TA!9\u0011\u000b\u0001b\u0001\n\u0013\u0011\u0015AG2bG\",G-\u0012=fGV$xN]%eY\u0016$\u0016.\\3pkR\u001c\u0006BB*\u0001A\u0003%1)A\u000edC\u000eDW\rZ#yK\u000e,Ho\u001c:JI2,G+[7f_V$8\u000b\t\u0005\b+\u0002\u0011\r\u0011\"\u0003W\u0003\u001d!Xm\u001d;j]\u001e,\u0012a\u0016\t\u0003\u0015aK!!W\u0006\u0003\u000f\t{w\u000e\\3b]\"11\f\u0001Q\u0001\n]\u000b\u0001\u0002^3ti&tw\r\t\u0005\b;\u0002\u0011\r\u0011\"\u00033\u0003A!\u0018m]6t!\u0016\u0014X\t_3dkR|'\u000f\u0003\u0004`\u0001\u0001\u0006IaM\u0001\u0012i\u0006\u001c8n\u001d)fe\u0016CXmY;u_J\u0004\u0003bB1\u0001\u0001\u0004%IAM\u0001\u0012]VlW\t_3dkR|'o\u001d+p\u0003\u0012$\u0007bB2\u0001\u0001\u0004%I\u0001Z\u0001\u0016]VlW\t_3dkR|'o\u001d+p\u0003\u0012$w\fJ3r)\t)\u0007\u000e\u0005\u0002\u000bM&\u0011qm\u0003\u0002\u0005+:LG\u000fC\u0004jE\u0006\u0005\t\u0019A\u001a\u0002\u0007a$\u0013\u0007\u0003\u0004l\u0001\u0001\u0006KaM\u0001\u0013]VlW\t_3dkR|'o\u001d+p\u0003\u0012$\u0007\u0005C\u0004n\u0001\u0001\u0007I\u0011\u0002\u001a\u0002%9,X.\u0012=fGV$xN]:UCJ<W\r\u001e\u0005\b_\u0002\u0001\r\u0011\"\u0003q\u0003YqW/\\#yK\u000e,Ho\u001c:t)\u0006\u0014x-\u001a;`I\u0015\fHCA3r\u0011\u001dIg.!AA\u0002MBaa\u001d\u0001!B\u0013\u0019\u0014a\u00058v[\u0016CXmY;u_J\u001cH+\u0019:hKR\u0004\u0003bB;\u0001\u0005\u0004%IA^\u0001\u0019Kb,7-\u001e;peN\u0004VM\u001c3j]\u001e$vNU3n_Z,W#A<\u0011\u0007alx0D\u0001z\u0015\tQ80A\u0004nkR\f'\r\\3\u000b\u0005q\\\u0011AC2pY2,7\r^5p]&\u0011a0\u001f\u0002\b\u0011\u0006\u001c\bnU3u!\u0011\t\t!a\u0002\u000f\u0007)\t\u0019!C\u0002\u0002\u0006-\ta\u0001\u0015:fI\u00164\u0017\u0002BA\u0005\u0003\u0017\u0011aa\u0015;sS:<'bAA\u0003\u0017!9\u0011q\u0002\u0001!\u0002\u00139\u0018!G3yK\u000e,Ho\u001c:t!\u0016tG-\u001b8h)>\u0014V-\\8wK\u0002B\u0001\"a\u0005\u0001\u0005\u0004%IA^\u0001\fKb,7-\u001e;pe&#7\u000fC\u0004\u0002\u0018\u0001\u0001\u000b\u0011B<\u0002\u0019\u0015DXmY;u_JLEm\u001d\u0011\t\u0011\u0005m\u0001\u00011A\u0005\n\t\u000bq!\u00193e)&lW\rC\u0005\u0002 \u0001\u0001\r\u0011\"\u0003\u0002\"\u0005Y\u0011\r\u001a3US6,w\fJ3r)\r)\u00171\u0005\u0005\tS\u0006u\u0011\u0011!a\u0001\u0007\"9\u0011q\u0005\u0001!B\u0013\u0019\u0015\u0001C1eIRKW.\u001a\u0011\t\u0013\u0005-\u0002A1A\u0005\n\u00055\u0012a\u0003:f[>4X\rV5nKN,\"!a\f\u0011\u000ba\f\td`\"\n\u0007\u0005M\u0012PA\u0004ICNDW*\u00199\t\u0011\u0005]\u0002\u0001)A\u0005\u0003_\tAB]3n_Z,G+[7fg\u0002B\u0001\"a\u000f\u0001\u0005\u0004%IAQ\u0001\u000fS:$XM\u001d<bY6KG\u000e\\5t\u0011\u001d\ty\u0004\u0001Q\u0001\n\r\u000bq\"\u001b8uKJ4\u0018\r\\'jY2L7\u000f\t\u0005\n\u0003\u0007\u0002\u0001\u0019!C\u0005\u0003\u000b\nQa\u00197pG.,\"!a\u0012\u0011\t\u0005%\u0013qJ\u0007\u0003\u0003\u0017R1!!\u0014\u0003\u0003\u0011)H/\u001b7\n\t\u0005E\u00131\n\u0002\u0006\u00072|7m\u001b\u0005\n\u0003+\u0002\u0001\u0019!C\u0005\u0003/\n\u0011b\u00197pG.|F%Z9\u0015\u0007\u0015\fI\u0006C\u0005j\u0003'\n\t\u00111\u0001\u0002H!A\u0011Q\f\u0001!B\u0013\t9%\u0001\u0004dY>\u001c7\u000e\t\u0005\n\u0003C\u0002!\u0019!C\u0005\u0003G\n\u0001\u0002\\5ti\u0016tWM]\u000b\u0003\u0003K\u0002B!a\u001a\u0002j5\t\u0001A\u0002\u0004\u0002l\u0001!\u0011Q\u000e\u0002\u001b\u000bb,7-\u001e;pe\u0006cGn\\2bi&|g\u000eT5ti\u0016tWM]\n\u0005\u0003S\ny\u0007E\u0002 \u0003cJ1!a\u001d!\u00055\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\"9!&!\u001b\u0005\u0002\u0005]DCAA3\u0011)\tY(!\u001bC\u0002\u0013%\u0011QP\u0001\u0012gR\fw-Z%e)>tU/\u001c+bg.\u001cXCAA@!\u0015A\u0018\u0011G\u001a4\u0011%\t\u0019)!\u001b!\u0002\u0013\ty(\u0001\nti\u0006<W-\u00133U_:+X\u000eV1tWN\u0004\u0003BCAD\u0003S\u0012\r\u0011\"\u0003\u0002~\u000592\u000f^1hK&#Gk\u001c(v[J+hN\\5oOR\u000b7o\u001b\u0005\n\u0003\u0017\u000bI\u0007)A\u0005\u0003\u007f\n\u0001d\u001d;bO\u0016LE\rV8Ok6\u0014VO\u001c8j]\u001e$\u0016m]6!\u0011)\ty)!\u001bC\u0002\u0013%\u0011\u0011S\u0001\u0015gR\fw-Z%e)>$\u0016m]6J]\u0012L7-Z:\u0016\u0005\u0005M\u0005C\u0002=\u00022M\n)\nE\u0002y{NB\u0011\"!'\u0002j\u0001\u0006I!a%\u0002+M$\u0018mZ3JIR{G+Y:l\u0013:$\u0017nY3tA!Q\u0011QTA5\u0005\u0004%I!a(\u0002'\u0015DXmY;u_JLE\rV8UCN\\\u0017\nZ:\u0016\u0005\u0005\u0005\u0006C\u0002=\u00022}\f\u0019\u000bE\u0002y{\u000eC\u0011\"a*\u0002j\u0001\u0006I!!)\u0002)\u0015DXmY;u_JLE\rV8UCN\\\u0017\nZ:!\u0011)\tY+!\u001bC\u0002\u0013%\u0011QP\u0001\u001dgR\fw-Z%e)>tU/\\*qK\u000e,H.\u0019;jm\u0016$\u0016m]6t\u0011%\ty+!\u001b!\u0002\u0013\ty(A\u000fti\u0006<W-\u00133U_:+Xn\u00159fGVd\u0017\r^5wKR\u000b7o[:!\u0011)\t\u0019,!\u001bC\u0002\u0013%\u0011\u0011S\u0001 gR\fw-Z%e)>\u001c\u0006/Z2vY\u0006$\u0018N^3UCN\\\u0017J\u001c3jG\u0016\u001c\b\"CA\\\u0003S\u0002\u000b\u0011BAJ\u0003\u0001\u001aH/Y4f\u0013\u0012$vn\u00159fGVd\u0017\r^5wKR\u000b7o[%oI&\u001cWm\u001d\u0011\t\u0015\u0005m\u0016\u0011\u000eb\u0001\n\u0013\ti,A\u0010ti\u0006<W-\u00133U_\u0016CXmY;u_J\u0004F.Y2f[\u0016tG\u000fS5oiN,\"!a0\u0011\ra\f\tdMAa!\u0019Q\u00111Y\u001a\u0002H&\u0019\u0011QY\u0006\u0003\rQ+\b\u000f\\33!\u0019\t\t!!3\u0000g%!\u00111ZA\u0006\u0005\ri\u0015\r\u001d\u0005\n\u0003\u001f\fI\u0007)A\u0005\u0003\u007f\u000b\u0001e\u001d;bO\u0016LE\rV8Fq\u0016\u001cW\u000f^8s!2\f7-Z7f]RD\u0015N\u001c;tA!A\u00111[A5\t\u0003\n).\u0001\tp]N#\u0018mZ3Tk\nl\u0017\u000e\u001e;fIR\u0019Q-a6\t\u0011\u0005e\u0017\u0011\u001ba\u0001\u00037\fab\u001d;bO\u0016\u001cVOY7jiR,G\rE\u0002 \u0003;L1!a8!\u0005m\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8feN#\u0018mZ3Tk\nl\u0017\u000e\u001e;fI\"A\u00111]A5\t\u0003\n)/\u0001\tp]N#\u0018mZ3D_6\u0004H.\u001a;fIR\u0019Q-a:\t\u0011\u0005%\u0018\u0011\u001da\u0001\u0003W\fab\u001d;bO\u0016\u001cu.\u001c9mKR,G\rE\u0002 \u0003[L1!a<!\u0005m\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8feN#\u0018mZ3D_6\u0004H.\u001a;fI\"A\u00111_A5\t\u0003\n)0A\u0006p]R\u000b7o[*uCJ$HcA3\u0002x\"A\u0011\u0011`Ay\u0001\u0004\tY0A\u0005uCN\\7\u000b^1siB\u0019q$!@\n\u0007\u0005}\bE\u0001\fTa\u0006\u00148\u000eT5ti\u0016tWM\u001d+bg.\u001cF/\u0019:u\u0011!\u0011\u0019!!\u001b\u0005B\t\u0015\u0011!C8o)\u0006\u001c8.\u00128e)\r)'q\u0001\u0005\t\u0005\u0013\u0011\t\u00011\u0001\u0003\f\u00059A/Y:l\u000b:$\u0007cA\u0010\u0003\u000e%\u0019!q\u0002\u0011\u0003)M\u0003\u0018M]6MSN$XM\\3s)\u0006\u001c8.\u00128e\u0011!\u0011\u0019\"!\u001b\u0005B\tU\u0011aD8o\u000bb,7-\u001e;pe\u0006#G-\u001a3\u0015\u0007\u0015\u00149\u0002\u0003\u0005\u0003\u001a\tE\u0001\u0019\u0001B\u000e\u00035)\u00070Z2vi>\u0014\u0018\t\u001a3fIB\u0019qD!\b\n\u0007\t}\u0001E\u0001\u000eTa\u0006\u00148\u000eT5ti\u0016tWM]#yK\u000e,Ho\u001c:BI\u0012,G\r\u0003\u0005\u0003$\u0005%D\u0011\tB\u0013\u0003Eyg.\u0012=fGV$xN\u001d*f[>4X\r\u001a\u000b\u0004K\n\u001d\u0002\u0002\u0003B\u0015\u0005C\u0001\rAa\u000b\u0002\u001f\u0015DXmY;u_J\u0014V-\\8wK\u0012\u00042a\bB\u0017\u0013\r\u0011y\u0003\t\u0002\u001d'B\f'o\u001b'jgR,g.\u001a:Fq\u0016\u001cW\u000f^8s%\u0016lwN^3e\u0011!\u0011\u0019$!\u001b\u0005B\tU\u0012AG8o'B,7-\u001e7bi&4X\rV1tWN+(-\\5ui\u0016$GcA3\u00038!A!\u0011\bB\u0019\u0001\u0004\u0011Y$A\bta\u0016\u001cW\u000f\\1uSZ,G+Y:l!\ry\"QH\u0005\u0004\u0005\u007f\u0001#!J*qCJ\\G*[:uK:,'o\u00159fGVd\u0017\r^5wKR\u000b7o[*vE6LG\u000f^3e\u0011!\u0011\u0019%!\u001b\u0005\u0002\t\u0015\u0013\u0001\u00049f]\u0012Lgn\u001a+bg.\u001cH#A\u001a\t\u0011\t%\u0013\u0011\u000eC\u0001\u0005\u000b\nq\u0003]3oI&twm\u00159fGVd\u0017\r^5wKR\u000b7o[:\t\u0011\t5\u0013\u0011\u000eC\u0001\u0005\u000b\n\u0011\u0003^8uC2\u0004VM\u001c3j]\u001e$\u0016m]6t\u0011!\u0011\t&!\u001b\u0005\u0002\t\u0015\u0013!\u0005;pi\u0006d'+\u001e8oS:<G+Y:lg\"A!QKA5\t\u0003\u00119&\u0001\bjg\u0016CXmY;u_JLE\r\\3\u0015\u0007]\u0013I\u0006C\u0004\u0003\\\tM\u0003\u0019A@\u0002\u0015\u0015DXmY;u_JLE\r\u0003\u0005\u0003`\u0005%D\u0011\u0001B1\u0003q)\b\u000fZ1uK\u0016CXmY;u_J\u0004F.Y2f[\u0016tG\u000fS5oiN$\u0012!\u001a\u0005\t\u0005K\u0002\u0001\u0015!\u0003\u0002f\u0005IA.[:uK:,'\u000f\t\u0005\n\u0005S\u0002!\u0019!C\u0005\u0005W\n\u0001\"\u001a=fGV$xN]\u000b\u0003\u0005[\u0002BAa\u001c\u0003|5\u0011!\u0011\u000f\u0006\u0005\u0005g\u0012)(\u0001\u0006d_:\u001cWO\u001d:f]RTA!!\u0014\u0003x)\u0011!\u0011P\u0001\u0005U\u00064\u0018-\u0003\u0003\u0003~\tE$\u0001G*dQ\u0016$W\u000f\\3e\u000bb,7-\u001e;peN+'O^5dK\"A!\u0011\u0011\u0001!\u0002\u0013\u0011i'A\u0005fq\u0016\u001cW\u000f^8sA!I!Q\u0011\u0001C\u0002\u0013\u0005!qQ\u0001 Kb,7-\u001e;pe\u0006cGn\\2bi&|g.T1oC\u001e,'oU8ve\u000e,WC\u0001BE!\u0011\t9Ga#\u0007\u000f\t5\u0005\u0001\u0001\u0002\u0003\u0010\nyR\t_3dkR|'/\u00117m_\u000e\fG/[8o\u001b\u0006t\u0017mZ3s'>,(oY3\u0014\u000b\t-\u0015B!%\u0011\t\tM%QT\u0007\u0003\u0005+SAAa&\u0003\u001a\u000611o\\;sG\u0016T1Aa'\u0003\u0003\u001diW\r\u001e:jGNLAAa(\u0003\u0016\n11k\\;sG\u0016DqA\u000bBF\t\u0003\u0011\u0019\u000b\u0006\u0002\u0003\n\"Q!q\u0015BF\u0005\u0004%\tA!+\u0002\u0015M|WO]2f\u001d\u0006lW-\u0006\u0002\u0003,B!!Q\u0016BZ\u001b\t\u0011yK\u0003\u0003\u00032\n]\u0014\u0001\u00027b]\u001eLA!!\u0003\u00030\"I!q\u0017BFA\u0003%!1V\u0001\fg>,(oY3OC6,\u0007\u0005\u0003\u0006\u0003<\n-%\u0019!C\u0001\u0005{\u000ba\"\\3ue&\u001c'+Z4jgR\u0014\u00180\u0006\u0002\u0003@B!!\u0011\u0019Bg\u001b\t\u0011\u0019M\u0003\u0003\u0003\u001c\n\u0015'\u0002\u0002Bd\u0005\u0013\f\u0001bY8eC\"\fG.\u001a\u0006\u0003\u0005\u0017\f1aY8n\u0013\u0011\u0011yMa1\u0003\u001d5+GO]5d%\u0016<\u0017n\u001d;ss\"I!1\u001bBFA\u0003%!qX\u0001\u0010[\u0016$(/[2SK\u001eL7\u000f\u001e:zA!A!q\u001bBF\t\u0013\u0011I.A\u0007sK\u001eL7\u000f^3s\u000f\u0006,x-Z\u000b\u0005\u00057\u0014y\u000fF\u0004f\u0005;\u0014\to!\u0001\t\u000f\t}'Q\u001ba\u0001\u007f\u0006!a.Y7f\u0011%\u0011\u0019O!6\u0005\u0002\u0004\u0011)/A\u0003wC2,X\rE\u0003\u000b\u0005O\u0014Y/C\u0002\u0003j.\u0011\u0001\u0002\u00102z]\u0006lWM\u0010\t\u0005\u0005[\u0014y\u000f\u0004\u0001\u0005\u0011\tE(Q\u001bb\u0001\u0005g\u0014\u0011\u0001V\t\u0005\u0005k\u0014Y\u0010E\u0002\u000b\u0005oL1A!?\f\u0005\u001dqu\u000e\u001e5j]\u001e\u00042A\u0003B\u007f\u0013\r\u0011yp\u0003\u0002\u0004\u0003:L\b\u0002CB\u0002\u0005+\u0004\rAa;\u0002\u0019\u0011,g-Y;miZ\u000bG.^3\t\u0011\r\u001d\u0001\u0001)A\u0005\u0005\u0013\u000b\u0001%\u001a=fGV$xN]!mY>\u001c\u0017\r^5p]6\u000bg.Y4feN{WO]2fA!A11\u0002\u0001A\u0002\u0013%a+\u0001\u0007j]&$\u0018.\u00197ju&tw\rC\u0005\u0004\u0010\u0001\u0001\r\u0011\"\u0003\u0004\u0012\u0005\u0001\u0012N\\5uS\u0006d\u0017N_5oO~#S-\u001d\u000b\u0004K\u000eM\u0001\u0002C5\u0004\u000e\u0005\u0005\t\u0019A,\t\u000f\r]\u0001\u0001)Q\u0005/\u0006i\u0011N\\5uS\u0006d\u0017N_5oO\u0002BCa!\u0006\u0004\u001cA\u0019!b!\b\n\u0007\r}1B\u0001\u0005w_2\fG/\u001b7f\u0011!\u0019\u0019\u0003\u0001a\u0001\n\u0013\u0011\u0014A\u00057pG\u0006d\u0017\u000e^=Bo\u0006\u0014X\rV1tWND\u0011ba\n\u0001\u0001\u0004%Ia!\u000b\u0002-1|7-\u00197jif\fu/\u0019:f)\u0006\u001c8n]0%KF$2!ZB\u0016\u0011!I7QEA\u0001\u0002\u0004\u0019\u0004bBB\u0018\u0001\u0001\u0006KaM\u0001\u0014Y>\u001c\u0017\r\\5us\u0006;\u0018M]3UCN\\7\u000f\t\u0005\n\u0007g\u0001\u0001\u0019!C\u0005\u0007k\tA\u0003[8tiR{Gj\\2bYR\u000b7o[\"pk:$XCAAd\u0011%\u0019I\u0004\u0001a\u0001\n\u0013\u0019Y$\u0001\ri_N$Hk\u001c'pG\u0006dG+Y:l\u0007>,h\u000e^0%KF$2!ZB\u001f\u0011%I7qGA\u0001\u0002\u0004\t9\r\u0003\u0005\u0004B\u0001\u0001\u000b\u0015BAd\u0003UAwn\u001d;U_2{7-\u00197UCN\\7i\\;oi\u0002Bqa!\u0012\u0001\t\u0013\u0011\t'\u0001\twC2LG-\u0019;f'\u0016$H/\u001b8hg\"91\u0011\n\u0001\u0005\u0002\r-\u0013\u0001C:fi\u000ecwnY6\u0015\u0007\u0015\u001ci\u0005\u0003\u0005\u0004P\r\u001d\u0003\u0019AA$\u0003!qWm^\"m_\u000e\\\u0007bBB*\u0001\u0011\u0005!\u0011M\u0001\u0006gR\f'\u000f\u001e\u0005\b\u0007/\u0002A\u0011\u0001B1\u0003\u0011\u0019Ho\u001c9\t\u000f\rm\u0003\u0001\"\u0001\u0003b\u0005)!/Z:fi\"91q\f\u0001\u0005\n\t\u0015\u0013!F7bq:+X.\u0012=fGV$xN]:OK\u0016$W\r\u001a\u0005\b\u0005#\u0002A\u0011\u0002B#\u0011\u001d\u0019)\u0007\u0001C\u0005\u0005C\n\u0001b]2iK\u0012,H.\u001a\u0005\b\u0007S\u0002A\u0011BB6\u0003})\b\u000fZ1uK\u0006sGmU=oG:+X.\u0012=fGV$xN]:UCJ<W\r\u001e\u000b\u0004g\r5\u0004bBB8\u0007O\u0002\raQ\u0001\u0004]><\bbBB:\u0001\u0011%1QO\u0001\rC\u0012$W\t_3dkR|'o\u001d\u000b\u0004g\r]\u0004bBB0\u0007c\u0002\ra\r\u0005\b\u0007w\u0002A\u0011BB?\u0003=\u0011X-\\8wK\u0016CXmY;u_J\u001cH\u0003BB@\u0007/\u0003Ra!!\u0004\u0012~tAaa!\u0004\u000e:!1QQBF\u001b\t\u00199IC\u0002\u0004\n^\ta\u0001\u0010:p_Rt\u0014\"\u0001\u0007\n\u0007\r=5\"A\u0004qC\u000e\\\u0017mZ3\n\t\rM5Q\u0013\u0002\u0004'\u0016\f(bABH\u0017!A1\u0011TB=\u0001\u0004\u0019y(A\u0005fq\u0016\u001cW\u000f^8sg\"91Q\u0014\u0001\u0005\n\r}\u0015A\u0004:f[>4X-\u0012=fGV$xN\u001d\u000b\u0004/\u000e\u0005\u0006b\u0002B.\u00077\u0003\ra \u0005\b\u0007K\u0003A\u0011BBT\u0003-\u0019\u0017M\u001c\"f\u0017&dG.\u001a3\u0015\u0007]\u001bI\u000bC\u0004\u0003\\\r\r\u0006\u0019A@\t\u000f\tM\u0001\u0001\"\u0003\u0004.R\u0019Qma,\t\u000f\tm31\u0016a\u0001\u007f\"9!1\u0005\u0001\u0005\n\rMFcA3\u00046\"9!1LBY\u0001\u0004y\bbBB]\u0001\u0011%!\u0011M\u0001\u0016_:\u001c6\r[3ek2,'OQ1dW2|wmZ3e\u0011\u001d\u0019i\f\u0001C\u0005\u0005C\nQc\u001c8TG\",G-\u001e7feF+X-^3F[B$\u0018\u0010C\u0004\u0004B\u0002!Iaa1\u0002\u001d=tW\t_3dkR|'/\u00133mKR\u0019Qm!2\t\u000f\tm3q\u0018a\u0001\u007f\"91\u0011\u001a\u0001\u0005\n\r-\u0017AD8o\u000bb,7-\u001e;pe\n+8/\u001f\u000b\u0004K\u000e5\u0007b\u0002B.\u0007\u000f\u0004\ra`\u0004\b\u0007#\u0014\u0001\u0012BBj\u0003e)\u00050Z2vi>\u0014\u0018\t\u001c7pG\u0006$\u0018n\u001c8NC:\fw-\u001a:\u0011\u0007e\u0019)N\u0002\u0004\u0002\u0005!%1q[\n\u0004\u0007+L\u0001b\u0002\u0016\u0004V\u0012\u000511\u001c\u000b\u0003\u0007'D\u0011ba8\u0004V\n\u0007I\u0011\u0001\"\u0002\u000f9{EkX*F)\"A11]BkA\u0003%1)\u0001\u0005O\u001fR{6+\u0012+!\u0011)\u00199o!6C\u0002\u0013\u0005!\u0011V\u0001\u001e)\u0016\u001bF+\u0013(H?N\u001b\u0005*\u0012#V\u0019\u0016{\u0016J\u0014+F%Z\u000bEjX&F3\"I11^BkA\u0003%!1V\u0001\u001f)\u0016\u001bF+\u0013(H?N\u001b\u0005*\u0012#V\u0019\u0016{\u0016J\u0014+F%Z\u000bEjX&F3\u0002\u0002"
)
public class ExecutorAllocationManager implements Logging {
   private final ExecutorAllocationClient client;
   private final LiveListenerBus listenerBus;
   private final SparkConf conf;
   private final int org$apache$spark$ExecutorAllocationManager$$minNumExecutors;
   private final int org$apache$spark$ExecutorAllocationManager$$maxNumExecutors;
   private final int initialNumExecutors;
   private final long org$apache$spark$ExecutorAllocationManager$$schedulerBacklogTimeoutS;
   private final long org$apache$spark$ExecutorAllocationManager$$sustainedSchedulerBacklogTimeoutS;
   private final long org$apache$spark$ExecutorAllocationManager$$executorIdleTimeoutS;
   private final long cachedExecutorIdleTimeoutS;
   private final boolean testing;
   private final int tasksPerExecutor;
   private int org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd;
   private int org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget;
   private final HashSet<String> org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove;
   private final HashSet<String> org$apache$spark$ExecutorAllocationManager$$executorIds;
   private long addTime;
   private final HashMap<String, Object> removeTimes;
   private final long intervalMillis;
   private Clock clock;
   private final ExecutorAllocationListener org$apache$spark$ExecutorAllocationManager$$listener;
   private final ScheduledExecutorService executor;
   private final ExecutorAllocationManagerSource executorAllocationManagerSource;
   private volatile boolean org$apache$spark$ExecutorAllocationManager$$initializing;
   private int org$apache$spark$ExecutorAllocationManager$$localityAwareTasks;
   private Map<String, Object> org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static String TESTING_SCHEDULE_INTERVAL_KEY() {
      return ExecutorAllocationManager$.MODULE$.TESTING_SCHEDULE_INTERVAL_KEY();
   }

   public static long NOT_SET() {
      return ExecutorAllocationManager$.MODULE$.NOT_SET();
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

   public int org$apache$spark$ExecutorAllocationManager$$minNumExecutors() {
      return this.org$apache$spark$ExecutorAllocationManager$$minNumExecutors;
   }

   public int org$apache$spark$ExecutorAllocationManager$$maxNumExecutors() {
      return this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors;
   }

   private int initialNumExecutors() {
      return this.initialNumExecutors;
   }

   public long org$apache$spark$ExecutorAllocationManager$$schedulerBacklogTimeoutS() {
      return this.org$apache$spark$ExecutorAllocationManager$$schedulerBacklogTimeoutS;
   }

   public long org$apache$spark$ExecutorAllocationManager$$sustainedSchedulerBacklogTimeoutS() {
      return this.org$apache$spark$ExecutorAllocationManager$$sustainedSchedulerBacklogTimeoutS;
   }

   public long org$apache$spark$ExecutorAllocationManager$$executorIdleTimeoutS() {
      return this.org$apache$spark$ExecutorAllocationManager$$executorIdleTimeoutS;
   }

   private long cachedExecutorIdleTimeoutS() {
      return this.cachedExecutorIdleTimeoutS;
   }

   private boolean testing() {
      return this.testing;
   }

   private int tasksPerExecutor() {
      return this.tasksPerExecutor;
   }

   public int org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd() {
      return this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd;
   }

   private void org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd_$eq(int x$1) {
      this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd = x$1;
   }

   public int org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget() {
      return this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget;
   }

   private void org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget_$eq(int x$1) {
      this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget = x$1;
   }

   public HashSet<String> org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove() {
      return this.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove;
   }

   public HashSet<String> org$apache$spark$ExecutorAllocationManager$$executorIds() {
      return this.org$apache$spark$ExecutorAllocationManager$$executorIds;
   }

   private long addTime() {
      return this.addTime;
   }

   private void addTime_$eq(long x$1) {
      this.addTime = x$1;
   }

   private HashMap<String, Object> removeTimes() {
      return this.removeTimes;
   }

   private long intervalMillis() {
      return this.intervalMillis;
   }

   private Clock clock() {
      return this.clock;
   }

   private void clock_$eq(Clock x$1) {
      this.clock = x$1;
   }

   public ExecutorAllocationListener org$apache$spark$ExecutorAllocationManager$$listener() {
      return this.org$apache$spark$ExecutorAllocationManager$$listener;
   }

   private ScheduledExecutorService executor() {
      return this.executor;
   }

   public ExecutorAllocationManagerSource executorAllocationManagerSource() {
      return this.executorAllocationManagerSource;
   }

   private boolean org$apache$spark$ExecutorAllocationManager$$initializing() {
      return this.org$apache$spark$ExecutorAllocationManager$$initializing;
   }

   public void org$apache$spark$ExecutorAllocationManager$$initializing_$eq(boolean x$1) {
      this.org$apache$spark$ExecutorAllocationManager$$initializing = x$1;
   }

   private int org$apache$spark$ExecutorAllocationManager$$localityAwareTasks() {
      return this.org$apache$spark$ExecutorAllocationManager$$localityAwareTasks;
   }

   public void org$apache$spark$ExecutorAllocationManager$$localityAwareTasks_$eq(int x$1) {
      this.org$apache$spark$ExecutorAllocationManager$$localityAwareTasks = x$1;
   }

   private Map<String, Object> org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount() {
      return this.org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount;
   }

   public void org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount_$eq(Map<String, Object> x$1) {
      this.org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount = x$1;
   }

   private void validateSettings() {
      if (this.org$apache$spark$ExecutorAllocationManager$$minNumExecutors() >= 0 && this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors() >= 0) {
         if (this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors() == 0) {
            throw new SparkException("spark.dynamicAllocation.maxExecutors cannot be 0!");
         } else if (this.org$apache$spark$ExecutorAllocationManager$$minNumExecutors() > this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors()) {
            throw new SparkException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.dynamicAllocation.minExecutors (", ") must "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.org$apache$spark$ExecutorAllocationManager$$minNumExecutors())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"be less than or equal to spark.dynamicAllocation.maxExecutors (", ")!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors())}))).toString());
         } else if (this.org$apache$spark$ExecutorAllocationManager$$schedulerBacklogTimeoutS() <= 0L) {
            throw new SparkException("spark.dynamicAllocation.schedulerBacklogTimeout must be > 0!");
         } else if (this.org$apache$spark$ExecutorAllocationManager$$sustainedSchedulerBacklogTimeoutS() <= 0L) {
            throw new SparkException("spark.dynamicAllocation.sustainedSchedulerBacklogTimeout must be > 0!");
         } else if (this.org$apache$spark$ExecutorAllocationManager$$executorIdleTimeoutS() < 0L) {
            throw new SparkException("spark.dynamicAllocation.executorIdleTimeout must be >= 0!");
         } else if (this.cachedExecutorIdleTimeoutS() < 0L) {
            throw new SparkException("spark.dynamicAllocation.cachedExecutorIdleTimeout must be >= 0!");
         } else if (!this.conf.getBoolean("spark.shuffle.service.enabled", false) && !this.testing()) {
            throw new SparkException("Dynamic allocation of executors requires the external shuffle service. You may enable this through spark.shuffle.service.enabled.");
         } else if (this.tasksPerExecutor() == 0) {
            throw new SparkException("spark.executor.cores must not be less than spark.task.cpus.");
         }
      } else {
         throw new SparkException("spark.dynamicAllocation.{min/max}Executors must be positive!");
      }
   }

   public void setClock(Clock newClock) {
      this.clock_$eq(newClock);
   }

   public void start() {
      this.listenerBus.addToManagementQueue(this.org$apache$spark$ExecutorAllocationManager$$listener());
      Runnable scheduleTask = new Runnable(this) {
         // $FF: synthetic field
         private final ExecutorAllocationManager $outer;

         public void run() {
            try {
               this.$outer.org$apache$spark$ExecutorAllocationManager$$schedule();
            } catch (Throwable var7) {
               if (var7 instanceof ControlThrowable) {
                  ControlThrowable var3 = (ControlThrowable)var7;
                  throw (Throwable)var3;
               }

               if (var7 == null) {
                  throw var7;
               }

               this.$outer.logWarning(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Uncaught exception in thread ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Thread.currentThread().getName()}));
                  }
               }, var7);
               BoxedUnit var4 = BoxedUnit.UNIT;
               return;
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      this.executor().scheduleWithFixedDelay(scheduleTask, 0L, this.intervalMillis(), TimeUnit.MILLISECONDS);
      this.client.requestTotalExecutors(this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget(), this.org$apache$spark$ExecutorAllocationManager$$localityAwareTasks(), this.org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount());
   }

   public void stop() {
      this.executor().shutdown();
      this.executor().awaitTermination(10L, TimeUnit.SECONDS);
   }

   public synchronized void reset() {
      this.addTime_$eq(0L);
      this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget_$eq(this.initialNumExecutors());
      this.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove().clear();
      this.removeTimes().clear();
   }

   public int org$apache$spark$ExecutorAllocationManager$$maxNumExecutorsNeeded() {
      int numRunningOrPendingTasks = this.org$apache$spark$ExecutorAllocationManager$$listener().totalPendingTasks() + this.org$apache$spark$ExecutorAllocationManager$$listener().totalRunningTasks();
      return (numRunningOrPendingTasks + this.tasksPerExecutor() - 1) / this.tasksPerExecutor();
   }

   private synchronized int totalRunningTasks() {
      return this.org$apache$spark$ExecutorAllocationManager$$listener().totalRunningTasks();
   }

   public void org$apache$spark$ExecutorAllocationManager$$schedule() {
      synchronized(this) {
         long now = this.clock().getTimeMillis();
         this.updateAndSyncNumExecutorsTarget(now);
         ArrayBuffer executorIdsToBeRemoved = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
         this.removeTimes().retain(new Serializable(this, now, executorIdsToBeRemoved) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationManager $outer;
            private final long now$1;
            private final ArrayBuffer executorIdsToBeRemoved$1;

            public final boolean apply(String x0$1, long x1$1) {
               Tuple2 var4 = new Tuple2(x0$1, BoxesRunTime.boxToLong(x1$1));
               if (var4 != null) {
                  String executorId = (String)var4._1();
                  long expireTime = var4._2$mcJ$sp();
                  boolean expired = this.now$1 >= expireTime;
                  if (expired) {
                     this.$outer.org$apache$spark$ExecutorAllocationManager$$initializing_$eq(false);
                     this.executorIdsToBeRemoved$1.$plus$eq(executorId);
                  } else {
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  }

                  boolean var8 = !expired;
                  return var8;
               } else {
                  throw new MatchError(var4);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.now$1 = now$1;
                  this.executorIdsToBeRemoved$1 = executorIdsToBeRemoved$1;
               }
            }
         });
         if (executorIdsToBeRemoved.nonEmpty()) {
            this.removeExecutors(executorIdsToBeRemoved);
         } else {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

      }
   }

   private synchronized int updateAndSyncNumExecutorsTarget(long now) {
      int maxNeeded = this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutorsNeeded();
      int var10000;
      if (this.org$apache$spark$ExecutorAllocationManager$$initializing()) {
         var10000 = 0;
      } else if (maxNeeded < this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget()) {
         int oldNumExecutorsTarget = this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget();
         this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget_$eq(scala.math.package$.MODULE$.max(maxNeeded, this.org$apache$spark$ExecutorAllocationManager$$minNumExecutors()));
         this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd_$eq(1);
         if (this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget() < oldNumExecutorsTarget) {
            this.client.requestTotalExecutors(this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget(), this.org$apache$spark$ExecutorAllocationManager$$localityAwareTasks(), this.org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount());
            this.logDebug(new Serializable(this, oldNumExecutorsTarget) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManager $outer;
               private final int oldNumExecutorsTarget$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Lowering target number of executors to ", " (previously "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ") because not all requested executors are actually needed"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.oldNumExecutorsTarget$1)}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.oldNumExecutorsTarget$1 = oldNumExecutorsTarget$1;
                  }
               }
            });
         }

         var10000 = this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget() - oldNumExecutorsTarget;
      } else if (this.addTime() != ExecutorAllocationManager$.MODULE$.NOT_SET() && now >= this.addTime()) {
         int delta = this.addExecutors(maxNeeded);
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationManager $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Starting timer to add more executors (to "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"expire in ", " seconds)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$ExecutorAllocationManager$$sustainedSchedulerBacklogTimeoutS())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.addTime_$eq(now + this.org$apache$spark$ExecutorAllocationManager$$sustainedSchedulerBacklogTimeoutS() * 1000L);
         var10000 = delta;
      } else {
         var10000 = 0;
      }

      return var10000;
   }

   private int addExecutors(int maxNumExecutorsNeeded) {
      if (this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget() >= this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors()) {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationManager $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not adding executors because our current target total "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"is already ", " (limit ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd_$eq(1);
         return 0;
      } else {
         int oldNumExecutorsTarget = this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget();
         this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget_$eq(scala.math.package$.MODULE$.max(this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget(), this.org$apache$spark$ExecutorAllocationManager$$executorIds().size()));
         this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget_$eq(this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget() + this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd());
         this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget_$eq(scala.math.package$.MODULE$.min(this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget(), maxNumExecutorsNeeded));
         this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget_$eq(scala.math.package$.MODULE$.max(scala.math.package$.MODULE$.min(this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget(), this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors()), this.org$apache$spark$ExecutorAllocationManager$$minNumExecutors()));
         int delta = this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget() - oldNumExecutorsTarget;
         if (delta == 0) {
            if (this.org$apache$spark$ExecutorAllocationManager$$listener().pendingTasks() != 0 || this.org$apache$spark$ExecutorAllocationManager$$listener().pendingSpeculativeTasks() <= 0) {
               this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd_$eq(1);
               return 0;
            }

            this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget_$eq(scala.math.package$.MODULE$.max(scala.math.package$.MODULE$.min(maxNumExecutorsNeeded + 1, this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors()), this.org$apache$spark$ExecutorAllocationManager$$minNumExecutors()));
         }

         boolean var10000;
         try {
            var10000 = this.testing() || this.client.requestTotalExecutors(this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget(), this.org$apache$spark$ExecutorAllocationManager$$localityAwareTasks(), this.org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount());
         } catch (Throwable var12) {
            label102: {
               Option var7 = NonFatal$.MODULE$.unapply(var12);
               if (var7.isEmpty()) {
                  throw var12;
               }

               Throwable e = (Throwable)var7.get();
               this.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Error reaching cluster manager.";
                  }
               }, e);
               boolean var9 = false;
               var10000 = var9;
               break label102;
            }
         }

         boolean addRequestAcknowledged = (boolean)var10000;
         if (addRequestAcknowledged) {
            String executorsString = (new StringBuilder()).append((Object)"executor").append((Object)(delta > 1 ? "s" : "")).toString();
            this.logInfo(new Serializable(this, delta, executorsString) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManager $outer;
               private final int delta$1;
               private final String executorsString$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Requesting ", " new ", " because tasks are backlogged"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.delta$1), this.executorsString$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" (new desired total will be ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget())}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.delta$1 = delta$1;
                     this.executorsString$1 = executorsString$1;
                  }
               }
            });
            this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd_$eq(delta == this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd() ? this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd() * 2 : 1);
            var10000 = delta;
         } else {
            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManager $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unable to reach the cluster manager to request ", " total executors!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget())}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget_$eq(oldNumExecutorsTarget);
            var10000 = 0;
         }

         return var10000;
      }
   }

   private synchronized Seq<String> removeExecutors(Seq<String> executors) {
      ArrayBuffer executorIdsToBeRemoved = new ArrayBuffer();
      this.logInfo(new Serializable(this, executors) {
         public static final long serialVersionUID = 0L;
         private final Seq executors$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Request to remove executorIds: ").append((Object)this.executors$1.mkString(", ")).toString();
         }

         public {
            this.executors$1 = executors$1;
         }
      });
      int numExistingExecutors = this.org$apache$spark$ExecutorAllocationManager$$executorIds().size() - this.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove().size();
      IntRef newExecutorTotal = IntRef.create(numExistingExecutors);
      executors.foreach(new Serializable(this, executorIdsToBeRemoved, newExecutorTotal) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ExecutorAllocationManager $outer;
         private final ArrayBuffer executorIdsToBeRemoved$2;
         public final IntRef newExecutorTotal$1;

         public final void apply(String executorIdToBeRemoved) {
            if (this.newExecutorTotal$1.elem - 1 < this.$outer.org$apache$spark$ExecutorAllocationManager$$minNumExecutors()) {
               this.$outer.logDebug(new Serializable(this, executorIdToBeRemoved) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final String executorIdToBeRemoved$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not removing idle executor ", " because there are only "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorIdToBeRemoved$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " executor(s) left (minimum number of executor limit ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.newExecutorTotal$1.elem), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$anonfun$$$outer().org$apache$spark$ExecutorAllocationManager$$minNumExecutors())}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.executorIdToBeRemoved$1 = executorIdToBeRemoved$1;
                     }
                  }
               });
            } else if (this.newExecutorTotal$1.elem - 1 < this.$outer.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget()) {
               this.$outer.logDebug(new Serializable(this, executorIdToBeRemoved) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final String executorIdToBeRemoved$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not removing idle executor ", " because there are only "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorIdToBeRemoved$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " executor(s) left (number of executor target ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.newExecutorTotal$1.elem), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$anonfun$$$outer().org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget())}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.executorIdToBeRemoved$1 = executorIdToBeRemoved$1;
                     }
                  }
               });
            } else if (this.$outer.org$apache$spark$ExecutorAllocationManager$$canBeKilled(executorIdToBeRemoved)) {
               this.executorIdsToBeRemoved$2.$plus$eq(executorIdToBeRemoved);
               --this.newExecutorTotal$1.elem;
            }

         }

         // $FF: synthetic method
         public ExecutorAllocationManager org$apache$spark$ExecutorAllocationManager$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.executorIdsToBeRemoved$2 = executorIdsToBeRemoved$2;
               this.newExecutorTotal$1 = newExecutorTotal$1;
            }
         }
      });
      if (executorIdsToBeRemoved.isEmpty()) {
         return (Seq)Seq$.MODULE$.empty();
      } else {
         Seq executorsRemoved = (Seq)(this.testing() ? executorIdsToBeRemoved : this.client.killExecutors(executorIdsToBeRemoved, this.client.killExecutors$default$2(), this.client.killExecutors$default$3()));
         this.client.requestTotalExecutors(this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget(), this.org$apache$spark$ExecutorAllocationManager$$localityAwareTasks(), this.org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount());
         newExecutorTotal.elem = numExistingExecutors;
         Object var10000;
         if (!this.testing() && !executorsRemoved.nonEmpty()) {
            this.logWarning(new Serializable(this, executorIdsToBeRemoved) {
               public static final long serialVersionUID = 0L;
               private final ArrayBuffer executorIdsToBeRemoved$2;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unable to reach the cluster manager to kill executor/s "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " or no executor eligible to kill!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorIdsToBeRemoved$2.mkString(",")}))).toString();
               }

               public {
                  this.executorIdsToBeRemoved$2 = executorIdsToBeRemoved$2;
               }
            });
            var10000 = (Seq)Seq$.MODULE$.empty();
         } else {
            executorsRemoved.foreach(new Serializable(this, newExecutorTotal) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManager $outer;
               public final IntRef newExecutorTotal$1;

               public final boolean apply(String removedExecutorId) {
                  --this.newExecutorTotal$1.elem;
                  this.$outer.logInfo(new Serializable(this, removedExecutorId) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final String removedExecutorId$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing executor ", " because it has been idle for "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.removedExecutorId$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " seconds (new desired total will be ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$ExecutorAllocationManager$$anonfun$$$outer().org$apache$spark$ExecutorAllocationManager$$executorIdleTimeoutS()), BoxesRunTime.boxToInteger(this.$outer.newExecutorTotal$1.elem)}))).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.removedExecutorId$1 = removedExecutorId$1;
                        }
                     }
                  });
                  return this.$outer.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove().add(removedExecutorId);
               }

               // $FF: synthetic method
               public ExecutorAllocationManager org$apache$spark$ExecutorAllocationManager$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.newExecutorTotal$1 = newExecutorTotal$1;
                  }
               }
            });
            var10000 = executorsRemoved;
         }

         return (Seq<String>)var10000;
      }
   }

   private synchronized boolean removeExecutor(String executorId) {
      boolean var4;
      label25: {
         Seq executorsRemoved = this.removeExecutors((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{executorId}))));
         if (executorsRemoved.nonEmpty()) {
            Object var10000 = executorsRemoved.apply(0);
            if (var10000 == null) {
               if (executorId == null) {
                  break label25;
               }
            } else if (var10000.equals(executorId)) {
               break label25;
            }
         }

         var4 = false;
         return var4;
      }

      var4 = true;
      return var4;
   }

   public synchronized boolean org$apache$spark$ExecutorAllocationManager$$canBeKilled(String executorId) {
      if (this.org$apache$spark$ExecutorAllocationManager$$executorIds().contains(executorId)) {
         if (this.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove().contains(executorId)) {
            this.logWarning(new Serializable(this, executorId) {
               public static final long serialVersionUID = 0L;
               private final String executorId$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to remove executor ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"when it is already pending to be removed!"})))).s(Nil$.MODULE$)).toString();
               }

               public {
                  this.executorId$1 = executorId$1;
               }
            });
            return false;
         } else {
            return true;
         }
      } else {
         this.logWarning(new Serializable(this, executorId) {
            public static final long serialVersionUID = 0L;
            private final String executorId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to remove unknown executor ", "!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$1}));
            }

            public {
               this.executorId$1 = executorId$1;
            }
         });
         return false;
      }
   }

   public synchronized void org$apache$spark$ExecutorAllocationManager$$onExecutorAdded(String executorId) {
      if (this.org$apache$spark$ExecutorAllocationManager$$executorIds().contains(executorId)) {
         this.logWarning(new Serializable(this, executorId) {
            public static final long serialVersionUID = 0L;
            private final String executorId$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Duplicate executor ", " has registered"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$2}));
            }

            public {
               this.executorId$2 = executorId$2;
            }
         });
      } else {
         this.org$apache$spark$ExecutorAllocationManager$$executorIds().add(executorId);
         ((HashSet)this.org$apache$spark$ExecutorAllocationManager$$executorIds().filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationManager $outer;

            public final boolean apply(String executorId) {
               return this.$outer.org$apache$spark$ExecutorAllocationManager$$listener().isExecutorIdle(executorId);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         })).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationManager $outer;

            public final void apply(String executorId) {
               this.$outer.org$apache$spark$ExecutorAllocationManager$$onExecutorIdle(executorId);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.logInfo(new Serializable(this, executorId) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationManager $outer;
            private final String executorId$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"New executor ", " has registered (new total is ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$2, BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$executorIds().size())}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.executorId$2 = executorId$2;
               }
            }
         });
      }

   }

   public synchronized void org$apache$spark$ExecutorAllocationManager$$onExecutorRemoved(String executorId) {
      if (this.org$apache$spark$ExecutorAllocationManager$$executorIds().contains(executorId)) {
         this.org$apache$spark$ExecutorAllocationManager$$executorIds().remove(executorId);
         this.removeTimes().remove(executorId);
         this.logInfo(new Serializable(this, executorId) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationManager $outer;
            private final String executorId$4;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Existing executor ", " has been removed (new total is ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$4, BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$executorIds().size())}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.executorId$4 = executorId$4;
               }
            }
         });
         if (this.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove().contains(executorId)) {
            this.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove().remove(executorId);
            this.logDebug(new Serializable(this, executorId) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManager $outer;
               private final String executorId$4;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor ", " is no longer pending to "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$4}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"be removed (", " left)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove().size())}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.executorId$4 = executorId$4;
                  }
               }
            });
         }
      } else {
         this.logWarning(new Serializable(this, executorId) {
            public static final long serialVersionUID = 0L;
            private final String executorId$4;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown executor ", " has been removed!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$4}));
            }

            public {
               this.executorId$4 = executorId$4;
            }
         });
      }

   }

   public synchronized void org$apache$spark$ExecutorAllocationManager$$onSchedulerBacklogged() {
      if (this.addTime() == ExecutorAllocationManager$.MODULE$.NOT_SET()) {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationManager $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Starting timer to add executors because pending tasks "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"are building up (to expire in ", " seconds)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$ExecutorAllocationManager$$schedulerBacklogTimeoutS())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.addTime_$eq(this.clock().getTimeMillis() + this.org$apache$spark$ExecutorAllocationManager$$schedulerBacklogTimeoutS() * 1000L);
      }

   }

   public synchronized void org$apache$spark$ExecutorAllocationManager$$onSchedulerQueueEmpty() {
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Clearing timer to add executors because there are no more pending tasks";
         }
      });
      this.addTime_$eq(ExecutorAllocationManager$.MODULE$.NOT_SET());
      this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd_$eq(1);
   }

   public synchronized void org$apache$spark$ExecutorAllocationManager$$onExecutorIdle(String executorId) {
      if (this.org$apache$spark$ExecutorAllocationManager$$executorIds().contains(executorId)) {
         if (!this.removeTimes().contains(executorId) && !this.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove().contains(executorId)) {
            boolean hasCachedBlocks = SparkEnv$.MODULE$.get().blockManager().master().hasCachedBlocks(executorId);
            long now = this.clock().getTimeMillis();
            long timeout = hasCachedBlocks ? now + this.cachedExecutorIdleTimeoutS() * 1000L : now + this.org$apache$spark$ExecutorAllocationManager$$executorIdleTimeoutS() * 1000L;
            long realTimeout = timeout <= 0L ? Long.MAX_VALUE : timeout;
            this.removeTimes().update(executorId, BoxesRunTime.boxToLong(realTimeout));
            this.logDebug(new Serializable(this, executorId, now, realTimeout) {
               public static final long serialVersionUID = 0L;
               private final String executorId$3;
               private final long now$2;
               private final long realTimeout$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Starting idle timer for ", " because there are no more tasks "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$3}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"scheduled to run on the executor (to expire in ", " seconds)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong((this.realTimeout$1 - this.now$2) / 1000L)}))).toString();
               }

               public {
                  this.executorId$3 = executorId$3;
                  this.now$2 = now$2;
                  this.realTimeout$1 = realTimeout$1;
               }
            });
         }
      } else {
         this.logWarning(new Serializable(this, executorId) {
            public static final long serialVersionUID = 0L;
            private final String executorId$3;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to mark unknown executor ", " idle"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$3}));
            }

            public {
               this.executorId$3 = executorId$3;
            }
         });
      }

   }

   public void org$apache$spark$ExecutorAllocationManager$$onExecutorBusy(String executorId) {
      synchronized(this) {
         this.logDebug(new Serializable(this, executorId) {
            public static final long serialVersionUID = 0L;
            private final String executorId$5;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Clearing idle timer for ", " because it is now running a task"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$5}));
            }

            public {
               this.executorId$5 = executorId$5;
            }
         });
         this.removeTimes().remove(executorId);
      }
   }

   public ExecutorAllocationManager(ExecutorAllocationClient client, LiveListenerBus listenerBus, SparkConf conf) {
      this.client = client;
      this.listenerBus = listenerBus;
      this.conf = conf;
      Logging$class.$init$(this);
      this.org$apache$spark$ExecutorAllocationManager$$minNumExecutors = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_MIN_EXECUTORS()));
      this.org$apache$spark$ExecutorAllocationManager$$maxNumExecutors = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.DYN_ALLOCATION_MAX_EXECUTORS()));
      this.initialNumExecutors = Utils$.MODULE$.getDynamicAllocationInitialExecutors(conf);
      this.org$apache$spark$ExecutorAllocationManager$$schedulerBacklogTimeoutS = conf.getTimeAsSeconds("spark.dynamicAllocation.schedulerBacklogTimeout", "1s");
      this.org$apache$spark$ExecutorAllocationManager$$sustainedSchedulerBacklogTimeoutS = conf.getTimeAsSeconds("spark.dynamicAllocation.sustainedSchedulerBacklogTimeout", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "s"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.org$apache$spark$ExecutorAllocationManager$$schedulerBacklogTimeoutS())})));
      this.org$apache$spark$ExecutorAllocationManager$$executorIdleTimeoutS = conf.getTimeAsSeconds("spark.dynamicAllocation.executorIdleTimeout", "60s");
      this.cachedExecutorIdleTimeoutS = conf.getTimeAsSeconds("spark.dynamicAllocation.cachedExecutorIdleTimeout", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "s"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(Integer.MAX_VALUE)})));
      this.testing = conf.getBoolean("spark.dynamicAllocation.testing", false);
      this.tasksPerExecutor = conf.getInt("spark.executor.cores", 1) / conf.getInt("spark.task.cpus", 1);
      this.validateSettings();
      this.org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd = 1;
      this.org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget = this.initialNumExecutors();
      this.org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove = new HashSet<String>();
      this.org$apache$spark$ExecutorAllocationManager$$executorIds = new HashSet<String>();
      this.addTime = ExecutorAllocationManager$.MODULE$.NOT_SET();
      this.removeTimes = new HashMap<String, Object>();
      this.intervalMillis = Utils$.MODULE$.isTesting() ? conf.getLong(ExecutorAllocationManager$.MODULE$.TESTING_SCHEDULE_INTERVAL_KEY(), 100L) : 100L;
      this.clock = new SystemClock();
      this.org$apache$spark$ExecutorAllocationManager$$listener = new ExecutorAllocationListener();
      this.executor = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("spark-dynamic-executor-allocation");
      this.executorAllocationManagerSource = new ExecutorAllocationManagerSource();
      this.org$apache$spark$ExecutorAllocationManager$$initializing = true;
      this.org$apache$spark$ExecutorAllocationManager$$localityAwareTasks = 0;
      this.org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount = Predef$.MODULE$.Map().empty();
   }

   public class ExecutorAllocationListener extends SparkListener {
      private final HashMap<Object, Object> stageIdToNumTasks;
      private final HashMap<Object, Object> stageIdToNumRunningTask;
      private final HashMap<Object, HashSet<Object>> org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToTaskIndices;
      private final HashMap<String, HashSet<Object>> executorIdToTaskIds;
      private final HashMap<Object, Object> stageIdToNumSpeculativeTasks;
      private final HashMap<Object, HashSet<Object>> org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToSpeculativeTaskIndices;
      private final HashMap<Object, Tuple2<Object, Map<String, Object>>> stageIdToExecutorPlacementHints;
      // $FF: synthetic field
      public final ExecutorAllocationManager $outer;

      private HashMap<Object, Object> stageIdToNumTasks() {
         return this.stageIdToNumTasks;
      }

      private HashMap<Object, Object> stageIdToNumRunningTask() {
         return this.stageIdToNumRunningTask;
      }

      public HashMap<Object, HashSet<Object>> org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToTaskIndices() {
         return this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToTaskIndices;
      }

      private HashMap<String, HashSet<Object>> executorIdToTaskIds() {
         return this.executorIdToTaskIds;
      }

      private HashMap<Object, Object> stageIdToNumSpeculativeTasks() {
         return this.stageIdToNumSpeculativeTasks;
      }

      public HashMap<Object, HashSet<Object>> org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToSpeculativeTaskIndices() {
         return this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToSpeculativeTaskIndices;
      }

      private HashMap<Object, Tuple2<Object, Map<String, Object>>> stageIdToExecutorPlacementHints() {
         return this.stageIdToExecutorPlacementHints;
      }

      public void onStageSubmitted(SparkListenerStageSubmitted stageSubmitted) {
         this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$initializing_$eq(false);
         int stageId = stageSubmitted.stageInfo().stageId();
         int numTasks = stageSubmitted.stageInfo().numTasks();
         synchronized(this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer()) {
            this.stageIdToNumTasks().update(BoxesRunTime.boxToInteger(stageId), BoxesRunTime.boxToInteger(numTasks));
            this.stageIdToNumRunningTask().update(BoxesRunTime.boxToInteger(stageId), BoxesRunTime.boxToInteger(0));
            this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onSchedulerBacklogged();
            IntRef numTasksPending = IntRef.create(0);
            HashMap hostToLocalTaskCountPerStage = new HashMap();
            stageSubmitted.stageInfo().taskLocalityPreferences().foreach(new Serializable(this, numTasksPending, hostToLocalTaskCountPerStage) {
               public static final long serialVersionUID = 0L;
               private final IntRef numTasksPending$1;
               public final HashMap hostToLocalTaskCountPerStage$1;

               public final void apply(Seq<TaskLocation> locality) {
                  if (!locality.isEmpty()) {
                     ++this.numTasksPending$1.elem;
                     locality.foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final void apply(TaskLocation location) {
                           int count = BoxesRunTime.unboxToInt(this.$outer.hostToLocalTaskCountPerStage$1.getOrElse(location.host(), new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final int apply() {
                                 return this.apply$mcI$sp();
                              }

                              public int apply$mcI$sp() {
                                 return 0;
                              }
                           })) + 1;
                           this.$outer.hostToLocalTaskCountPerStage$1.update(location.host(), BoxesRunTime.boxToInteger(count));
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

               public {
                  this.numTasksPending$1 = numTasksPending$1;
                  this.hostToLocalTaskCountPerStage$1 = hostToLocalTaskCountPerStage$1;
               }
            });
            this.stageIdToExecutorPlacementHints().put(BoxesRunTime.boxToInteger(stageId), new Tuple2(BoxesRunTime.boxToInteger(numTasksPending.elem), hostToLocalTaskCountPerStage.toMap(Predef$.MODULE$.$conforms())));
            this.updateExecutorPlacementHints();
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }
      }

      public void onStageCompleted(SparkListenerStageCompleted stageCompleted) {
         int stageId = stageCompleted.stageInfo().stageId();
         synchronized(this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer()) {
            this.stageIdToNumTasks().$minus$eq(BoxesRunTime.boxToInteger(stageId));
            this.stageIdToNumRunningTask().$minus$eq(BoxesRunTime.boxToInteger(stageId));
            this.stageIdToNumSpeculativeTasks().$minus$eq(BoxesRunTime.boxToInteger(stageId));
            this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToTaskIndices().$minus$eq(BoxesRunTime.boxToInteger(stageId));
            this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToSpeculativeTaskIndices().$minus$eq(BoxesRunTime.boxToInteger(stageId));
            this.stageIdToExecutorPlacementHints().$minus$eq(BoxesRunTime.boxToInteger(stageId));
            this.updateExecutorPlacementHints();
            if (this.stageIdToNumTasks().isEmpty() && this.stageIdToNumSpeculativeTasks().isEmpty()) {
               this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onSchedulerQueueEmpty();
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

         }
      }

      public void onTaskStart(SparkListenerTaskStart taskStart) {
         int stageId = taskStart.stageId();
         long taskId = taskStart.taskInfo().taskId();
         int taskIndex = taskStart.taskInfo().index();
         String executorId = taskStart.taskInfo().executorId();
         synchronized(this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer()) {
            if (this.stageIdToNumRunningTask().contains(BoxesRunTime.boxToInteger(stageId))) {
               this.stageIdToNumRunningTask().update(BoxesRunTime.boxToInteger(stageId), BoxesRunTime.boxToInteger(BoxesRunTime.unboxToInt(this.stageIdToNumRunningTask().apply(BoxesRunTime.boxToInteger(stageId))) + 1));
            }

            if (!this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$executorIds().contains(executorId)) {
               this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onExecutorAdded(executorId);
            }

            if (taskStart.taskInfo().speculative()) {
               ((HashSet)this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToSpeculativeTaskIndices().getOrElseUpdate(BoxesRunTime.boxToInteger(stageId), new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final HashSet<Object> apply() {
                     return new HashSet<Object>();
                  }
               })).$plus$eq(BoxesRunTime.boxToInteger(taskIndex));
            } else {
               ((HashSet)this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToTaskIndices().getOrElseUpdate(BoxesRunTime.boxToInteger(stageId), new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final HashSet<Object> apply() {
                     return new HashSet<Object>();
                  }
               })).$plus$eq(BoxesRunTime.boxToInteger(taskIndex));
            }

            if (this.totalPendingTasks() == 0) {
               this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onSchedulerQueueEmpty();
            }

            ((HashSet)this.executorIdToTaskIds().getOrElseUpdate(executorId, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final HashSet<Object> apply() {
                  return new HashSet<Object>();
               }
            })).$plus$eq(BoxesRunTime.boxToLong(taskId));
            this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onExecutorBusy(executorId);
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }
      }

      public void onTaskEnd(SparkListenerTaskEnd taskEnd) {
         String executorId = taskEnd.taskInfo().executorId();
         long taskId = taskEnd.taskInfo().taskId();
         int taskIndex = taskEnd.taskInfo().index();
         int stageId = taskEnd.stageId();
         synchronized(this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer()) {
            if (this.stageIdToNumRunningTask().contains(BoxesRunTime.boxToInteger(stageId))) {
               this.stageIdToNumRunningTask().update(BoxesRunTime.boxToInteger(stageId), BoxesRunTime.boxToInteger(BoxesRunTime.unboxToInt(this.stageIdToNumRunningTask().apply(BoxesRunTime.boxToInteger(stageId))) - 1));
            }

            if (this.executorIdToTaskIds().contains(executorId)) {
               ((HashSet)this.executorIdToTaskIds().apply(executorId)).$minus$eq(BoxesRunTime.boxToLong(taskId));
               if (((SetLike)this.executorIdToTaskIds().apply(executorId)).isEmpty()) {
                  this.executorIdToTaskIds().$minus$eq(executorId);
                  this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onExecutorIdle(executorId);
               }
            }

            label50: {
               TaskEndReason var10000 = taskEnd.reason();
               Success$ var8 = Success$.MODULE$;
               if (var10000 == null) {
                  if (var8 == null) {
                     break label50;
                  }
               } else if (var10000.equals(var8)) {
                  break label50;
               }

               if (this.totalPendingTasks() == 0) {
                  this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onSchedulerBacklogged();
               }

               if (taskEnd.taskInfo().speculative()) {
                  this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToSpeculativeTaskIndices().get(BoxesRunTime.boxToInteger(stageId)).foreach(new Serializable(this, taskIndex) {
                     public static final long serialVersionUID = 0L;
                     private final int taskIndex$1;

                     public final boolean apply(HashSet<Object> x$1) {
                        return x$1.remove(BoxesRunTime.boxToInteger(this.taskIndex$1));
                     }

                     public {
                        this.taskIndex$1 = taskIndex$1;
                     }
                  });
                  BoxedUnit var10 = BoxedUnit.UNIT;
               } else {
                  this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToTaskIndices().get(BoxesRunTime.boxToInteger(stageId)).foreach(new Serializable(this, taskIndex) {
                     public static final long serialVersionUID = 0L;
                     private final int taskIndex$1;

                     public final boolean apply(HashSet<Object> x$2) {
                        return x$2.remove(BoxesRunTime.boxToInteger(this.taskIndex$1));
                     }

                     public {
                        this.taskIndex$1 = taskIndex$1;
                     }
                  });
                  BoxedUnit var11 = BoxedUnit.UNIT;
               }

               return;
            }

            BoxedUnit var12 = BoxedUnit.UNIT;
         }
      }

      public void onExecutorAdded(SparkListenerExecutorAdded executorAdded) {
         String executorId = executorAdded.executorId();
         String var3 = SparkContext$.MODULE$.DRIVER_IDENTIFIER();
         if (executorId == null) {
            if (var3 == null) {
               return;
            }
         } else if (executorId.equals(var3)) {
            return;
         }

         if (!this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$executorIds().contains(executorId)) {
            this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onExecutorAdded(executorId);
         }

      }

      public void onExecutorRemoved(SparkListenerExecutorRemoved executorRemoved) {
         this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onExecutorRemoved(executorRemoved.executorId());
      }

      public void onSpeculativeTaskSubmitted(SparkListenerSpeculativeTaskSubmitted speculativeTask) {
         int stageId = speculativeTask.stageId();
         synchronized(this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer()) {
            this.stageIdToNumSpeculativeTasks().update(BoxesRunTime.boxToInteger(stageId), BoxesRunTime.boxToInteger(BoxesRunTime.unboxToInt(this.stageIdToNumSpeculativeTasks().getOrElse(BoxesRunTime.boxToInteger(stageId), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply() {
                  return this.apply$mcI$sp();
               }

               public int apply$mcI$sp() {
                  return 0;
               }
            })) + 1));
            this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$onSchedulerBacklogged();
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }
      }

      public int pendingTasks() {
         return BoxesRunTime.unboxToInt(((TraversableOnce)this.stageIdToNumTasks().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationListener $outer;

            public final int apply(Tuple2<Object, Object> x0$2) {
               if (x0$2 != null) {
                  int stageId = x0$2._1$mcI$sp();
                  int numTasks = x0$2._2$mcI$sp();
                  int var5 = numTasks - BoxesRunTime.unboxToInt(this.$outer.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToTaskIndices().get(BoxesRunTime.boxToInteger(stageId)).map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final int apply(HashSet<Object> x$3) {
                        return x$3.size();
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final int apply() {
                        return this.apply$mcI$sp();
                     }

                     public int apply$mcI$sp() {
                        return 0;
                     }
                  }));
                  return var5;
               } else {
                  throw new MatchError(x0$2);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, Iterable$.MODULE$.canBuildFrom())).sum(Numeric.IntIsIntegral$.MODULE$));
      }

      public int pendingSpeculativeTasks() {
         return BoxesRunTime.unboxToInt(((TraversableOnce)this.stageIdToNumSpeculativeTasks().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorAllocationListener $outer;

            public final int apply(Tuple2<Object, Object> x0$3) {
               if (x0$3 != null) {
                  int stageId = x0$3._1$mcI$sp();
                  int numTasks = x0$3._2$mcI$sp();
                  int var5 = numTasks - BoxesRunTime.unboxToInt(this.$outer.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToSpeculativeTaskIndices().get(BoxesRunTime.boxToInteger(stageId)).map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final int apply(HashSet<Object> x$4) {
                        return x$4.size();
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final int apply() {
                        return this.apply$mcI$sp();
                     }

                     public int apply$mcI$sp() {
                        return 0;
                     }
                  }));
                  return var5;
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
         }, Iterable$.MODULE$.canBuildFrom())).sum(Numeric.IntIsIntegral$.MODULE$));
      }

      public int totalPendingTasks() {
         return this.pendingTasks() + this.pendingSpeculativeTasks();
      }

      public int totalRunningTasks() {
         return BoxesRunTime.unboxToInt(this.stageIdToNumRunningTask().values().sum(Numeric.IntIsIntegral$.MODULE$));
      }

      public boolean isExecutorIdle(String executorId) {
         return !this.executorIdToTaskIds().contains(executorId);
      }

      public void updateExecutorPlacementHints() {
         IntRef localityAwareTasks = IntRef.create(0);
         HashMap localityToCount = new HashMap();
         this.stageIdToExecutorPlacementHints().values().foreach(new Serializable(this, localityAwareTasks, localityToCount) {
            public static final long serialVersionUID = 0L;
            private final IntRef localityAwareTasks$1;
            public final HashMap localityToCount$1;

            public final void apply(Tuple2<Object, Map<String, Object>> x0$4) {
               if (x0$4 != null) {
                  int numTasksPending = x0$4._1$mcI$sp();
                  Map localities = (Map)x0$4._2();
                  this.localityAwareTasks$1.elem += numTasksPending;
                  localities.foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final void apply(Tuple2<String, Object> x0$5) {
                        if (x0$5 != null) {
                           String hostname = (String)x0$5._1();
                           int count = x0$5._2$mcI$sp();
                           int updatedCount = BoxesRunTime.unboxToInt(this.$outer.localityToCount$1.getOrElse(hostname, new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final int apply() {
                                 return this.apply$mcI$sp();
                              }

                              public int apply$mcI$sp() {
                                 return 0;
                              }
                           })) + count;
                           this.$outer.localityToCount$1.update(hostname, BoxesRunTime.boxToInteger(updatedCount));
                           BoxedUnit var5 = BoxedUnit.UNIT;
                        } else {
                           throw new MatchError(x0$5);
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
                  BoxedUnit var5 = BoxedUnit.UNIT;
               } else {
                  throw new MatchError(x0$4);
               }
            }

            public {
               this.localityAwareTasks$1 = localityAwareTasks$1;
               this.localityToCount$1 = localityToCount$1;
            }
         });
         this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$localityAwareTasks_$eq(localityAwareTasks.elem);
         this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer().org$apache$spark$ExecutorAllocationManager$$hostToLocalTaskCount_$eq(localityToCount.toMap(Predef$.MODULE$.$conforms()));
      }

      // $FF: synthetic method
      public ExecutorAllocationManager org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$$outer() {
         return this.$outer;
      }

      public ExecutorAllocationListener() {
         if (ExecutorAllocationManager.this == null) {
            throw null;
         } else {
            this.$outer = ExecutorAllocationManager.this;
            super();
            this.stageIdToNumTasks = new HashMap<Object, Object>();
            this.stageIdToNumRunningTask = new HashMap<Object, Object>();
            this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToTaskIndices = new HashMap<Object, HashSet<Object>>();
            this.executorIdToTaskIds = new HashMap<String, HashSet<Object>>();
            this.stageIdToNumSpeculativeTasks = new HashMap<Object, Object>();
            this.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationListener$$stageIdToSpeculativeTaskIndices = new HashMap<Object, HashSet<Object>>();
            this.stageIdToExecutorPlacementHints = new HashMap<Object, Tuple2<Object, Map<String, Object>>>();
         }
      }
   }

   public class ExecutorAllocationManagerSource implements Source {
      private final String sourceName;
      private final MetricRegistry metricRegistry;
      // $FF: synthetic field
      public final ExecutorAllocationManager $outer;

      public String sourceName() {
         return this.sourceName;
      }

      public MetricRegistry metricRegistry() {
         return this.metricRegistry;
      }

      private <T> void registerGauge(String name, Function0<T> value, T defaultValue) {
         this.metricRegistry().register(MetricRegistry.name("executors", new String[]{name}), new Gauge<T>(this, value, defaultValue) {
            private final Function0 value$1;
            public final Object defaultValue$1;

            public synchronized T getValue() {
               return (T)Option$.MODULE$.apply(this.value$1.apply()).getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final T apply() {
                     return (T)this.$outer.defaultValue$1;
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
               this.value$1 = value$1;
               this.defaultValue$1 = defaultValue$1;
            }
         });
      }

      // $FF: synthetic method
      public ExecutorAllocationManager org$apache$spark$ExecutorAllocationManager$ExecutorAllocationManagerSource$$$outer() {
         return this.$outer;
      }

      public ExecutorAllocationManagerSource() {
         if (ExecutorAllocationManager.this == null) {
            throw null;
         } else {
            this.$outer = ExecutorAllocationManager.this;
            super();
            this.sourceName = "ExecutorAllocationManager";
            this.metricRegistry = new MetricRegistry();
            this.registerGauge("numberExecutorsToAdd", new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManagerSource $outer;

               public final int apply() {
                  return this.apply$mcI$sp();
               }

               public int apply$mcI$sp() {
                  return this.$outer.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationManagerSource$$$outer().org$apache$spark$ExecutorAllocationManager$$numExecutorsToAdd();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, BoxesRunTime.boxToInteger(0));
            this.registerGauge("numberExecutorsPendingToRemove", new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManagerSource $outer;

               public final int apply() {
                  return this.apply$mcI$sp();
               }

               public int apply$mcI$sp() {
                  return this.$outer.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationManagerSource$$$outer().org$apache$spark$ExecutorAllocationManager$$executorsPendingToRemove().size();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, BoxesRunTime.boxToInteger(0));
            this.registerGauge("numberAllExecutors", new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManagerSource $outer;

               public final int apply() {
                  return this.apply$mcI$sp();
               }

               public int apply$mcI$sp() {
                  return this.$outer.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationManagerSource$$$outer().org$apache$spark$ExecutorAllocationManager$$executorIds().size();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, BoxesRunTime.boxToInteger(0));
            this.registerGauge("numberTargetExecutors", new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManagerSource $outer;

               public final int apply() {
                  return this.apply$mcI$sp();
               }

               public int apply$mcI$sp() {
                  return this.$outer.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationManagerSource$$$outer().org$apache$spark$ExecutorAllocationManager$$numExecutorsTarget();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, BoxesRunTime.boxToInteger(0));
            this.registerGauge("numberMaxNeededExecutors", new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorAllocationManagerSource $outer;

               public final int apply() {
                  return this.apply$mcI$sp();
               }

               public int apply$mcI$sp() {
                  return this.$outer.org$apache$spark$ExecutorAllocationManager$ExecutorAllocationManagerSource$$$outer().org$apache$spark$ExecutorAllocationManager$$maxNumExecutorsNeeded();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, BoxesRunTime.boxToInteger(0));
         }
      }
   }
}
