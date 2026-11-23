package org.apache.spark.deploy.master;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.package$;
import org.apache.spark.deploy.ApplicationDescription;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.DriverDescription;
import org.apache.spark.deploy.ExecutorDescription;
import org.apache.spark.deploy.ExecutorState$;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.deploy.master.ui.MasterWebUI;
import org.apache.spark.deploy.rest.StandaloneRestServer;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.metrics.MetricsSystem;
import org.apache.spark.metrics.MetricsSystem$;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.apache.spark.serializer.JavaSerializer;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.spark_project.jetty.servlet.ServletContextHandler;
import scala.Array$;
import scala.Enumeration;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.PartialFunction;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.Iterable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayOps;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.HashSet$;
import scala.collection.mutable.ResizableArray;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.RichInt$;
import scala.util.Random$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0015=e!B\u0001\u0003\u0001\u0011a!AB'bgR,'O\u0003\u0002\u0004\t\u00051Q.Y:uKJT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7#\u0002\u0001\u000e'ey\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015/5\tQC\u0003\u0002\u0017\r\u0005\u0019!\u000f]2\n\u0005a)\"!\u0006+ie\u0016\fGmU1gKJ\u00038-\u00128ea>Lg\u000e\u001e\t\u00035ui\u0011a\u0007\u0006\u00039\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003=m\u0011q\u0001T8hO&tw\r\u0005\u0002!C5\t!!\u0003\u0002#\u0005\tyA*Z1eKJ,E.Z2uC\ndW\r\u0003\u0005%\u0001\t\u0015\r\u0011\"\u0011'\u0003\u0019\u0011\boY#om\u000e\u0001Q#A\u0014\u0011\u0005QA\u0013BA\u0015\u0016\u0005\u0019\u0011\u0006oY#om\"A1\u0006\u0001B\u0001B\u0003%q%A\u0004sa\u000e,eN\u001e\u0011\t\u00115\u0002!\u0011!Q\u0001\n9\nq!\u00193ee\u0016\u001c8\u000f\u0005\u0002\u0015_%\u0011\u0001'\u0006\u0002\u000b%B\u001c\u0017\t\u001a3sKN\u001c\b\u0002\u0003\u001a\u0001\u0005\u0003\u0005\u000b\u0011B\u001a\u0002\u0013],'-V5Q_J$\bC\u0001\b5\u0013\t)tBA\u0002J]RD\u0001b\u000e\u0001\u0003\u0006\u0004%\t\u0001O\u0001\fg\u0016\u001cWO]5us6;'/F\u0001:!\tQ4(D\u0001\u0007\u0013\tadAA\bTK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s\u0011!q\u0004A!A!\u0002\u0013I\u0014\u0001D:fGV\u0014\u0018\u000e^=NOJ\u0004\u0003\u0002\u0003!\u0001\u0005\u000b\u0007I\u0011A!\u0002\t\r|gNZ\u000b\u0002\u0005B\u0011!hQ\u0005\u0003\t\u001a\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011\u0019\u0003!\u0011!Q\u0001\n\t\u000bQaY8oM\u0002BQ\u0001\u0013\u0001\u0005\u0002%\u000ba\u0001P5oSRtDC\u0002&L\u00196su\n\u0005\u0002!\u0001!)Ae\u0012a\u0001O!)Qf\u0012a\u0001]!)!g\u0012a\u0001g!)qg\u0012a\u0001s!)\u0001i\u0012a\u0001\u0005\"9\u0011\u000b\u0001b\u0001\n\u0013\u0011\u0016\u0001\u00064pe^\f'\u000fZ'fgN\fw-\u001a+ie\u0016\fG-F\u0001T!\t!6,D\u0001V\u0015\t1v+\u0001\u0006d_:\u001cWO\u001d:f]RT!\u0001W-\u0002\tU$\u0018\u000e\u001c\u0006\u00025\u0006!!.\u0019<b\u0013\taVK\u0001\rTG\",G-\u001e7fI\u0016CXmY;u_J\u001cVM\u001d<jG\u0016DaA\u0018\u0001!\u0002\u0013\u0019\u0016!\u00064pe^\f'\u000fZ'fgN\fw-\u001a+ie\u0016\fG\r\t\u0005\bA\u0002\u0011\r\u0011\"\u0003b\u0003)A\u0017\rZ8pa\u000e{gNZ\u000b\u0002EB\u00111mZ\u0007\u0002I*\u0011\u0001)\u001a\u0006\u0003M\"\ta\u0001[1e_>\u0004\u0018B\u00015e\u00055\u0019uN\u001c4jOV\u0014\u0018\r^5p]\"1!\u000e\u0001Q\u0001\n\t\f1\u0002[1e_>\u00048i\u001c8gA!)A\u000e\u0001C\u0005[\u0006\u00012M]3bi\u0016$\u0015\r^3G_Jl\u0017\r^\u000b\u0002]B\u0011qN]\u0007\u0002a*\u0011\u0011/W\u0001\u0005i\u0016DH/\u0003\u0002ta\n\u00012+[7qY\u0016$\u0015\r^3G_Jl\u0017\r\u001e\u0005\bk\u0002\u0011\r\u0011\"\u0003w\u0003E9vJU&F%~#\u0016*T#P+R{VjU\u000b\u0002oB\u0011a\u0002_\u0005\u0003s>\u0011A\u0001T8oO\"11\u0010\u0001Q\u0001\n]\f!cV(S\u0017\u0016\u0013v\fV%N\u000b>+FkX'TA!9Q\u0010\u0001b\u0001\n\u0013q\u0018!\u0006*F)\u0006Ke*\u0012#`\u0003B\u0003F*S\"B)&{ejU\u000b\u0002g!9\u0011\u0011\u0001\u0001!\u0002\u0013\u0019\u0014A\u0006*F)\u0006Ke*\u0012#`\u0003B\u0003F*S\"B)&{ej\u0015\u0011\t\u0011\u0005\u0015\u0001A1A\u0005\ny\f\u0001CU#U\u0003&sU\tR0E%&3VIU*\t\u000f\u0005%\u0001\u0001)A\u0005g\u0005\t\"+\u0012+B\u0013:+Ei\u0018#S\u0013Z+%k\u0015\u0011\t\u0011\u00055\u0001A1A\u0005\ny\f\u0011CU#B!\u0016\u0013v,\u0013+F%\u0006#\u0016j\u0014(T\u0011\u001d\t\t\u0002\u0001Q\u0001\nM\n!CU#B!\u0016\u0013v,\u0013+F%\u0006#\u0016j\u0014(TA!I\u0011Q\u0003\u0001C\u0002\u0013%\u0011qC\u0001\u000e%\u0016\u001buJV#S3~ku\nR#\u0016\u0005\u0005e\u0001\u0003BA\u000e\u0003Cq1ADA\u000f\u0013\r\tybD\u0001\u0007!J,G-\u001a4\n\t\u0005\r\u0012Q\u0005\u0002\u0007'R\u0014\u0018N\\4\u000b\u0007\u0005}q\u0002\u0003\u0005\u0002*\u0001\u0001\u000b\u0011BA\r\u00039\u0011ViQ(W\u000bJKv,T(E\u000b\u0002B\u0001\"!\f\u0001\u0005\u0004%IA`\u0001\u0015\u001b\u0006Cv,\u0012-F\u0007V#vJU0S\u000bR\u0013\u0016*R*\t\u000f\u0005E\u0002\u0001)A\u0005g\u0005)R*\u0011-`\u000bb+5)\u0016+P%~\u0013V\t\u0016*J\u000bN\u0003\u0003\"CA\u001b\u0001\t\u0007I\u0011AA\u001c\u0003\u001d9xN]6feN,\"!!\u000f\u0011\r\u0005m\u0012QIA%\u001b\t\tiD\u0003\u0003\u0002@\u0005\u0005\u0013aB7vi\u0006\u0014G.\u001a\u0006\u0004\u0003\u0007z\u0011AC2pY2,7\r^5p]&!\u0011qIA\u001f\u0005\u001dA\u0015m\u001d5TKR\u00042\u0001IA&\u0013\r\tiE\u0001\u0002\u000b/>\u00148.\u001a:J]\u001a|\u0007\u0002CA)\u0001\u0001\u0006I!!\u000f\u0002\u0011]|'o[3sg\u0002B\u0011\"!\u0016\u0001\u0005\u0004%\t!a\u0016\u0002\u000f%$Gk\\!qaV\u0011\u0011\u0011\f\t\t\u0003w\tY&!\u0007\u0002`%!\u0011QLA\u001f\u0005\u001dA\u0015m\u001d5NCB\u00042\u0001IA1\u0013\r\t\u0019G\u0001\u0002\u0010\u0003B\u0004H.[2bi&|g.\u00138g_\"A\u0011q\r\u0001!\u0002\u0013\tI&\u0001\u0005jIR{\u0017\t\u001d9!\u0011%\tY\u0007\u0001b\u0001\n\u0013\ti'A\u0006xC&$\u0018N\\4BaB\u001cXCAA8!\u0019\tY$!\u001d\u0002`%!\u00111OA\u001f\u0005-\t%O]1z\u0005V4g-\u001a:\t\u0011\u0005]\u0004\u0001)A\u0005\u0003_\nAb^1ji&tw-\u00119qg\u0002B\u0011\"a\u001f\u0001\u0005\u0004%\t!! \u0002\t\u0005\u0004\bo]\u000b\u0003\u0003\u007f\u0002b!a\u000f\u0002F\u0005}\u0003\u0002CAB\u0001\u0001\u0006I!a \u0002\u000b\u0005\u0004\bo\u001d\u0011\t\u0013\u0005\u001d\u0005A1A\u0005\n\u0005%\u0015AC5e)><vN]6feV\u0011\u00111\u0012\t\t\u0003w\tY&!\u0007\u0002J!A\u0011q\u0012\u0001!\u0002\u0013\tY)A\u0006jIR{wk\u001c:lKJ\u0004\u0003\"CAJ\u0001\t\u0007I\u0011BAK\u0003=\tG\r\u001a:fgN$vnV8sW\u0016\u0014XCAAL!\u001d\tY$a\u0017/\u0003\u0013B\u0001\"a'\u0001A\u0003%\u0011qS\u0001\u0011C\u0012$'/Z:t)><vN]6fe\u0002B\u0011\"a(\u0001\u0005\u0004%I!!)\u0002\u001b\u0015tG\r]8j]R$v.\u00119q+\t\t\u0019\u000b\u0005\u0005\u0002<\u0005m\u0013QUA0!\r!\u0012qU\u0005\u0004\u0003S+\"A\u0004*qG\u0016sG\r]8j]R\u0014VM\u001a\u0005\t\u0003[\u0003\u0001\u0015!\u0003\u0002$\u0006qQM\u001c3q_&tG\u000fV8BaB\u0004\u0003\"CAY\u0001\t\u0007I\u0011BAZ\u00031\tG\r\u001a:fgN$v.\u00119q+\t\t)\fE\u0004\u0002<\u0005mc&a\u0018\t\u0011\u0005e\u0006\u0001)A\u0005\u0003k\u000bQ\"\u00193ee\u0016\u001c8\u000fV8BaB\u0004\u0003\"CA_\u0001\t\u0007I\u0011BA7\u00035\u0019w.\u001c9mKR,G-\u00119qg\"A\u0011\u0011\u0019\u0001!\u0002\u0013\ty'\u0001\bd_6\u0004H.\u001a;fI\u0006\u0003\bo\u001d\u0011\t\u0011\u0005\u0015\u0007\u00011A\u0005\ny\fQB\\3yi\u0006\u0003\bOT;nE\u0016\u0014\b\"CAe\u0001\u0001\u0007I\u0011BAf\u0003EqW\r\u001f;BaBtU/\u001c2fe~#S-\u001d\u000b\u0005\u0003\u001b\f\u0019\u000eE\u0002\u000f\u0003\u001fL1!!5\u0010\u0005\u0011)f.\u001b;\t\u0013\u0005U\u0017qYA\u0001\u0002\u0004\u0019\u0014a\u0001=%c!9\u0011\u0011\u001c\u0001!B\u0013\u0019\u0014A\u00048fqR\f\u0005\u000f\u001d(v[\n,'\u000f\t\u0005\n\u0003;\u0004!\u0019!C\u0005\u0003?\fq\u0001\u001a:jm\u0016\u00148/\u0006\u0002\u0002bB1\u00111HA#\u0003G\u00042\u0001IAs\u0013\r\t9O\u0001\u0002\u000b\tJLg/\u001a:J]\u001a|\u0007\u0002CAv\u0001\u0001\u0006I!!9\u0002\u0011\u0011\u0014\u0018N^3sg\u0002B\u0011\"a<\u0001\u0005\u0004%I!!=\u0002!\r|W\u000e\u001d7fi\u0016$GI]5wKJ\u001cXCAAz!\u0019\tY$!\u001d\u0002d\"A\u0011q\u001f\u0001!\u0002\u0013\t\u00190A\td_6\u0004H.\u001a;fI\u0012\u0013\u0018N^3sg\u0002B\u0011\"a?\u0001\u0005\u0004%I!!=\u0002\u001d]\f\u0017\u000e^5oO\u0012\u0013\u0018N^3sg\"A\u0011q \u0001!\u0002\u0013\t\u00190A\bxC&$\u0018N\\4Ee&4XM]:!\u0011!\u0011\u0019\u0001\u0001a\u0001\n\u0013q\u0018\u0001\u00058fqR$%/\u001b<fe:+XNY3s\u0011%\u00119\u0001\u0001a\u0001\n\u0013\u0011I!\u0001\u000boKb$HI]5wKJtU/\u001c2fe~#S-\u001d\u000b\u0005\u0003\u001b\u0014Y\u0001C\u0005\u0002V\n\u0015\u0011\u0011!a\u0001g!9!q\u0002\u0001!B\u0013\u0019\u0014!\u00058fqR$%/\u001b<fe:+XNY3sA!I!1\u0003\u0001C\u0002\u0013%!QC\u0001\u0014[\u0006\u001cH/\u001a:NKR\u0014\u0018nY:TsN$X-\\\u000b\u0003\u0005/\u0001BA!\u0007\u0003 5\u0011!1\u0004\u0006\u0004\u0005;1\u0011aB7fiJL7m]\u0005\u0005\u0005C\u0011YBA\u0007NKR\u0014\u0018nY:TsN$X-\u001c\u0005\t\u0005K\u0001\u0001\u0015!\u0003\u0003\u0018\u0005!R.Y:uKJlU\r\u001e:jGN\u001c\u0016p\u001d;f[\u0002B\u0011B!\u000b\u0001\u0005\u0004%IA!\u0006\u00021\u0005\u0004\b\u000f\\5dCRLwN\\'fiJL7m]*zgR,W\u000e\u0003\u0005\u0003.\u0001\u0001\u000b\u0011\u0002B\f\u0003e\t\u0007\u000f\u001d7jG\u0006$\u0018n\u001c8NKR\u0014\u0018nY:TsN$X-\u001c\u0011\t\u0013\tE\u0002A1A\u0005\n\tM\u0012\u0001D7bgR,'oU8ve\u000e,WC\u0001B\u001b!\r\u0001#qG\u0005\u0004\u0005s\u0011!\u0001D'bgR,'oU8ve\u000e,\u0007\u0002\u0003B\u001f\u0001\u0001\u0006IA!\u000e\u0002\u001b5\f7\u000f^3s'>,(oY3!\u0011%\u0011\t\u0005\u0001a\u0001\n\u0013\u0011\u0019%A\u0003xK\n,\u0016.\u0006\u0002\u0003FA!!q\tB'\u001b\t\u0011IEC\u0002\u0003L\t\t!!^5\n\t\t=#\u0011\n\u0002\f\u001b\u0006\u001cH/\u001a:XK\n,\u0016\nC\u0005\u0003T\u0001\u0001\r\u0011\"\u0003\u0003V\u0005Iq/\u001a2VS~#S-\u001d\u000b\u0005\u0003\u001b\u00149\u0006\u0003\u0006\u0002V\nE\u0013\u0011!a\u0001\u0005\u000bB\u0001Ba\u0017\u0001A\u0003&!QI\u0001\u0007o\u0016\u0014W+\u001b\u0011\t\u0013\t}\u0003A1A\u0005\n\u0005]\u0011aE7bgR,'\u000fU;cY&\u001c\u0017\t\u001a3sKN\u001c\b\u0002\u0003B2\u0001\u0001\u0006I!!\u0007\u0002)5\f7\u000f^3s!V\u0014G.[2BI\u0012\u0014Xm]:!\u0011%\u00119\u0007\u0001b\u0001\n\u0013\t9\"A\u0005nCN$XM]+sY\"A!1\u000e\u0001!\u0002\u0013\tI\"\u0001\u0006nCN$XM]+sY\u0002B1Ba\u001c\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u0002\u0018\u0005qQ.Y:uKJ<VMY+j+Jd\u0007b\u0003B:\u0001\u0001\u0007\t\u0019!C\u0005\u0005k\n!#\\1ti\u0016\u0014x+\u001a2VSV\u0013Hn\u0018\u0013fcR!\u0011Q\u001aB<\u0011)\t)N!\u001d\u0002\u0002\u0003\u0007\u0011\u0011\u0004\u0005\t\u0005w\u0002\u0001\u0015)\u0003\u0002\u001a\u0005yQ.Y:uKJ<VMY+j+Jd\u0007\u0005C\u0005\u0003\u0000\u0001\u0001\r\u0011\"\u0003\u0003\u0002\u0006)1\u000f^1uKV\u0011!1\u0011\t\u0005\u0005\u000b\u0013YID\u0002!\u0005\u000fK1A!#\u0003\u00035\u0011VmY8wKJL8\u000b^1uK&!!Q\u0012BH\u0005\u00151\u0016\r\\;f\u0013\r\u0011\tj\u0004\u0002\f\u000b:,X.\u001a:bi&|g\u000eC\u0005\u0003\u0016\u0002\u0001\r\u0011\"\u0003\u0003\u0018\u0006I1\u000f^1uK~#S-\u001d\u000b\u0005\u0003\u001b\u0014I\n\u0003\u0006\u0002V\nM\u0015\u0011!a\u0001\u0005\u0007C\u0001B!(\u0001A\u0003&!1Q\u0001\u0007gR\fG/\u001a\u0011\t\u0017\t\u0005\u0006\u00011AA\u0002\u0013%!1U\u0001\u0012a\u0016\u00148/[:uK:\u001cW-\u00128hS:,WC\u0001BS!\r\u0001#qU\u0005\u0004\u0005S\u0013!!\u0005)feNL7\u000f^3oG\u0016,enZ5oK\"Y!Q\u0016\u0001A\u0002\u0003\u0007I\u0011\u0002BX\u0003U\u0001XM]:jgR,gnY3F]\u001eLg.Z0%KF$B!!4\u00032\"Q\u0011Q\u001bBV\u0003\u0003\u0005\rA!*\t\u0011\tU\u0006\u0001)Q\u0005\u0005K\u000b!\u0003]3sg&\u001cH/\u001a8dK\u0016sw-\u001b8fA!Y!\u0011\u0018\u0001A\u0002\u0003\u0007I\u0011\u0002B^\u0003MaW-\u00193fe\u0016cWm\u0019;j_:\fu-\u001a8u+\t\u0011i\fE\u0002!\u0005\u007fK1A!1\u0003\u0005MaU-\u00193fe\u0016cWm\u0019;j_:\fu-\u001a8u\u0011-\u0011)\r\u0001a\u0001\u0002\u0004%IAa2\u0002/1,\u0017\rZ3s\u000b2,7\r^5p]\u0006;WM\u001c;`I\u0015\fH\u0003BAg\u0005\u0013D!\"!6\u0003D\u0006\u0005\t\u0019\u0001B_\u0011!\u0011i\r\u0001Q!\n\tu\u0016\u0001\u00067fC\u0012,'/\u00127fGRLwN\\!hK:$\b\u0005C\u0006\u0003R\u0002\u0001\r\u00111A\u0005\n\tM\u0017A\u0006:fG>4XM]=D_6\u0004H.\u001a;j_:$\u0016m]6\u0016\u0005\tU\u0007\u0007\u0002Bl\u0005C\u0004R\u0001\u0016Bm\u0005;L1Aa7V\u0005=\u00196\r[3ek2,GMR;ukJ,\u0007\u0003\u0002Bp\u0005Cd\u0001\u0001\u0002\u0007\u0003d\n\u0015\u0018\u0011!A\u0001\u0006\u0003\u0011\tPA\u0002`IEB\u0001Ba:\u0001A\u0003&!\u0011^\u0001\u0018e\u0016\u001cwN^3ss\u000e{W\u000e\u001d7fi&|g\u000eV1tW\u0002\u0002DAa;\u0003pB)AK!7\u0003nB!!q\u001cBx\t1\u0011\u0019O!:\u0002\u0002\u0003\u0005)\u0011\u0001By#\u0011\u0011\u0019P!?\u0011\u00079\u0011)0C\u0002\u0003x>\u0011qAT8uQ&tw\rE\u0002\u000f\u0005wL1A!@\u0010\u0005\r\te.\u001f\u0005\f\u0007\u0003\u0001\u0001\u0019!a\u0001\n\u0013\u0019\u0019!\u0001\u000esK\u000e|g/\u001a:z\u0007>l\u0007\u000f\\3uS>tG+Y:l?\u0012*\u0017\u000f\u0006\u0003\u0002N\u000e\u0015\u0001BCAk\u0005\u007f\f\t\u00111\u0001\u0004\bA\"1\u0011BB\u0007!\u0015!&\u0011\\B\u0006!\u0011\u0011yn!\u0004\u0005\u0019\t\r(Q]A\u0001\u0002\u0003\u0015\tA!=\t\u0017\rE\u0001\u00011AA\u0002\u0013%11C\u0001\u001aG\",7m\u001b$pe^{'o[3s)&lWmT;u)\u0006\u001c8.\u0006\u0002\u0004\u0016A\"1qCB\u000e!\u0015!&\u0011\\B\r!\u0011\u0011yna\u0007\u0005\u0019\ru1qDA\u0001\u0002\u0003\u0015\tA!=\u0003\u0007}##\u0007\u0003\u0005\u0004\"\u0001\u0001\u000b\u0015BB\u0012\u0003i\u0019\u0007.Z2l\r>\u0014xk\u001c:lKJ$\u0016.\\3PkR$\u0016m]6!a\u0011\u0019)c!\u000b\u0011\u000bQ\u0013Ina\n\u0011\t\t}7\u0011\u0006\u0003\r\u0007;\u0019y\"!A\u0001\u0002\u000b\u0005!\u0011\u001f\u0005\f\u0007[\u0001\u0001\u0019!a\u0001\n\u0013\u0019y#A\u000fdQ\u0016\u001c7NR8s/>\u00148.\u001a:US6,w*\u001e;UCN\\w\fJ3r)\u0011\tim!\r\t\u0015\u0005U71FA\u0001\u0002\u0004\u0019\u0019\u0004\r\u0003\u00046\re\u0002#\u0002+\u0003Z\u000e]\u0002\u0003\u0002Bp\u0007s!Ab!\b\u0004 \u0005\u0005\t\u0011!B\u0001\u0005cD\u0011b!\u0010\u0001\u0005\u0004%Iaa\u0010\u0002\u001bM\u0004(/Z1e\u001fV$\u0018\t\u001d9t+\t\u0019\t\u0005E\u0002\u000f\u0007\u0007J1a!\u0012\u0010\u0005\u001d\u0011un\u001c7fC:D\u0001b!\u0013\u0001A\u0003%1\u0011I\u0001\u000fgB\u0014X-\u00193PkR\f\u0005\u000f]:!\u0011!\u0019i\u0005\u0001b\u0001\n\u0013q\u0018\u0001\u00043fM\u0006,H\u000e^\"pe\u0016\u001c\bbBB)\u0001\u0001\u0006IaM\u0001\u000eI\u00164\u0017-\u001e7u\u0007>\u0014Xm\u001d\u0011\t\u0013\rU\u0003A1A\u0005\u0002\r}\u0012\u0001\u0004:fm\u0016\u00148/\u001a)s_bL\b\u0002CB-\u0001\u0001\u0006Ia!\u0011\u0002\u001bI,g/\u001a:tKB\u0013x\u000e_=!\u0011%\u0019i\u0006\u0001b\u0001\n\u0013\u0019y$A\tsKN$8+\u001a:wKJ,e.\u00192mK\u0012D\u0001b!\u0019\u0001A\u0003%1\u0011I\u0001\u0013e\u0016\u001cHoU3sm\u0016\u0014XI\\1cY\u0016$\u0007\u0005C\u0005\u0004f\u0001\u0001\r\u0011\"\u0003\u0004h\u0005Q!/Z:u'\u0016\u0014h/\u001a:\u0016\u0005\r%\u0004#\u0002\b\u0004l\r=\u0014bAB7\u001f\t1q\n\u001d;j_:\u0004Ba!\u001d\u0004x5\u001111\u000f\u0006\u0004\u0007k\"\u0011\u0001\u0002:fgRLAa!\u001f\u0004t\t!2\u000b^1oI\u0006dwN\\3SKN$8+\u001a:wKJD\u0011b! \u0001\u0001\u0004%Iaa \u0002\u001dI,7\u000f^*feZ,'o\u0018\u0013fcR!\u0011QZBA\u0011)\t)na\u001f\u0002\u0002\u0003\u00071\u0011\u000e\u0005\t\u0007\u000b\u0003\u0001\u0015)\u0003\u0004j\u0005Y!/Z:u'\u0016\u0014h/\u001a:!\u0011%\u0019I\t\u0001a\u0001\n\u0013\u0019Y)A\nsKN$8+\u001a:wKJ\u0014u.\u001e8e!>\u0014H/\u0006\u0002\u0004\u000eB!aba\u001b4\u0011%\u0019\t\n\u0001a\u0001\n\u0013\u0019\u0019*A\fsKN$8+\u001a:wKJ\u0014u.\u001e8e!>\u0014Ho\u0018\u0013fcR!\u0011QZBK\u0011)\t)na$\u0002\u0002\u0003\u00071Q\u0012\u0005\t\u00073\u0003\u0001\u0015)\u0003\u0004\u000e\u0006!\"/Z:u'\u0016\u0014h/\u001a:C_VtG\rU8si\u0002Bqa!(\u0001\t\u0003\u001ay*A\u0004p]N#\u0018M\u001d;\u0015\u0005\u00055\u0007bBBR\u0001\u0011\u00053qT\u0001\u0007_:\u001cFo\u001c9\t\u000f\r\u001d\u0006\u0001\"\u0011\u0004 \u0006iQ\r\\3di\u0016$G*Z1eKJDqaa+\u0001\t\u0003\u001ay*A\tsKZ|7.\u001a3MK\u0006$WM]:iSBDqaa,\u0001\t\u0003\u001a\t,A\u0004sK\u000e,\u0017N^3\u0016\u0005\rM\u0006c\u0002\b\u00046\ne\u0018QZ\u0005\u0004\u0007o{!a\u0004)beRL\u0017\r\u001c$v]\u000e$\u0018n\u001c8\t\u000f\rm\u0006\u0001\"\u0011\u0004>\u0006y!/Z2fSZ,\u0017I\u001c3SKBd\u0017\u0010\u0006\u0003\u00044\u000e}\u0006\u0002CBa\u0007s\u0003\raa1\u0002\u000f\r|g\u000e^3yiB\u0019Ac!2\n\u0007\r\u001dWC\u0001\bSa\u000e\u001c\u0015\r\u001c7D_:$X\r\u001f;\t\u000f\r-\u0007\u0001\"\u0011\u0004N\u0006qqN\u001c#jg\u000e|gN\\3di\u0016$G\u0003BAg\u0007\u001fDa!LBe\u0001\u0004q\u0003bBBj\u0001\u0011%1qH\u0001\u0014G\u0006t7i\\7qY\u0016$XMU3d_Z,'/\u001f\u0005\b\u0007/\u0004A\u0011BBm\u00035\u0011WmZ5o%\u0016\u001cwN^3ssRA\u0011QZBn\u0007o\u001ci\u0010\u0003\u0005\u0004^\u000eU\u0007\u0019ABp\u0003)\u0019Ho\u001c:fI\u0006\u0003\bo\u001d\t\u0007\u0007C\u001c\t0a\u0018\u000f\t\r\r8Q\u001e\b\u0005\u0007K\u001cY/\u0004\u0002\u0004h*\u00191\u0011^\u0013\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0001\u0012bABx\u001f\u00059\u0001/Y2lC\u001e,\u0017\u0002BBz\u0007k\u00141aU3r\u0015\r\u0019yo\u0004\u0005\t\u0007s\u001c)\u000e1\u0001\u0004|\u0006i1\u000f^8sK\u0012$%/\u001b<feN\u0004ba!9\u0004r\u0006\r\b\u0002CB\u0000\u0007+\u0004\r\u0001\"\u0001\u0002\u001bM$xN]3e/>\u00148.\u001a:t!\u0019\u0019\to!=\u0002J!9AQ\u0001\u0001\u0005\n\r}\u0015\u0001E2p[BdW\r^3SK\u000e|g/\u001a:z\u0011\u001d!I\u0001\u0001C\u0005\t\u0017\t!d]2iK\u0012,H.Z#yK\u000e,Ho\u001c:t\u001f:<vN]6feN$\u0002\u0002\"\u0004\u0005\u0014\u0011]AQ\u0004\t\u0005\u001d\u0011=1'C\u0002\u0005\u0012=\u0011Q!\u0011:sCfD\u0001\u0002\"\u0006\u0005\b\u0001\u0007\u0011qL\u0001\u0004CB\u0004\b\u0002\u0003C\r\t\u000f\u0001\r\u0001b\u0007\u0002\u001bU\u001c\u0018M\u00197f/>\u00148.\u001a:t!\u0015qAqBA%\u0011!\u0019i\u0004b\u0002A\u0002\r\u0005\u0003b\u0002C\u0011\u0001\u0011%1qT\u0001\u0018gR\f'\u000f^#yK\u000e,Ho\u001c:t\u001f:<vN]6feNDq\u0001\"\n\u0001\t\u0013!9#A\u0011bY2|7-\u0019;f/>\u00148.\u001a:SKN|WO]2f)>,\u00050Z2vi>\u00148\u000f\u0006\u0006\u0002N\u0012%B1\u0006C\u0018\tgA\u0001\u0002\"\u0006\u0005$\u0001\u0007\u0011q\f\u0005\b\t[!\u0019\u00031\u00014\u00035\t7o]5h]\u0016$7i\u001c:fg\"AA\u0011\u0007C\u0012\u0001\u0004\u0019i)\u0001\td_J,7\u000fU3s\u000bb,7-\u001e;pe\"AAQ\u0007C\u0012\u0001\u0004\tI%\u0001\u0004x_J\\WM\u001d\u0005\b\ts\u0001A\u0011BBP\u0003!\u00198\r[3ek2,\u0007b\u0002C\u001f\u0001\u0011%AqH\u0001\u000fY\u0006,hn\u00195Fq\u0016\u001cW\u000f^8s)\u0019\ti\r\"\u0011\u0005D!AAQ\u0007C\u001e\u0001\u0004\tI\u0005\u0003\u0005\u0005F\u0011m\u0002\u0019\u0001C$\u0003\u0011)\u00070Z2\u0011\u0007\u0001\"I%C\u0002\u0005L\t\u0011A\"\u0012=fGV$xN\u001d#fg\u000eDq\u0001b\u0014\u0001\t\u0013!\t&\u0001\bsK\u001eL7\u000f^3s/>\u00148.\u001a:\u0015\t\r\u0005C1\u000b\u0005\t\tk!i\u00051\u0001\u0002J!9Aq\u000b\u0001\u0005\n\u0011e\u0013\u0001\u0004:f[>4XmV8sW\u0016\u0014HCBAg\t7\"i\u0006\u0003\u0005\u00056\u0011U\u0003\u0019AA%\u0011!!y\u0006\"\u0016A\u0002\u0005e\u0011aA7tO\"9A1\r\u0001\u0005\n\u0011\u0015\u0014A\u0004:fY\u0006,hn\u00195Ee&4XM\u001d\u000b\u0005\u0003\u001b$9\u0007\u0003\u0005\u0005j\u0011\u0005\u0004\u0019AAr\u0003\u0019!'/\u001b<fe\"9AQ\u000e\u0001\u0005\n\u0011=\u0014!E2sK\u0006$X-\u00119qY&\u001c\u0017\r^5p]R1\u0011q\fC9\t{B\u0001\u0002b\u001d\u0005l\u0001\u0007AQO\u0001\u0005I\u0016\u001c8\r\u0005\u0003\u0005x\u0011eT\"\u0001\u0003\n\u0007\u0011mDA\u0001\fBaBd\u0017nY1uS>tG)Z:de&\u0004H/[8o\u0011!!I\u0007b\u001bA\u0002\u0005\u0015\u0006b\u0002CA\u0001\u0011%A1Q\u0001\u0014e\u0016<\u0017n\u001d;fe\u0006\u0003\b\u000f\\5dCRLwN\u001c\u000b\u0005\u0003\u001b$)\t\u0003\u0005\u0005\u0016\u0011}\u0004\u0019AA0\u0011\u001d!I\t\u0001C\u0005\t\u0017\u000b\u0011CZ5oSND\u0017\t\u001d9mS\u000e\fG/[8o)\u0011\ti\r\"$\t\u0011\u0011UAq\u0011a\u0001\u0003?Bq\u0001\"%\u0001\t\u0003!\u0019*A\tsK6|g/Z!qa2L7-\u0019;j_:$b!!4\u0005\u0016\u0012]\u0005\u0002\u0003C\u000b\t\u001f\u0003\r!a\u0018\t\u0011\t}Dq\u0012a\u0001\t3\u0003B\u0001b'\u0003\f:\u0019\u0001\u0005\"(\n\u0007\u0011}%!\u0001\tBaBd\u0017nY1uS>t7\u000b^1uK\"9A1\u0015\u0001\u0005\n\u0011\u0015\u0016A\u00065b]\u0012dWMU3rk\u0016\u001cH/\u0012=fGV$xN]:\u0015\r\r\u0005Cq\u0015CV\u0011!!I\u000b\")A\u0002\u0005e\u0011!B1qa&#\u0007b\u0002CW\tC\u0003\raM\u0001\u000fe\u0016\fX/Z:uK\u0012$v\u000e^1m\u0011\u001d!\t\f\u0001C\u0005\tg\u000b1\u0003[1oI2,7*\u001b7m\u000bb,7-\u001e;peN$ba!\u0011\u00056\u0012]\u0006\u0002\u0003CU\t_\u0003\r!!\u0007\t\u0011\u0011eFq\u0016a\u0001\tw\u000b1\"\u001a=fGV$xN]%egB)1\u0011]Byg!9Aq\u0018\u0001\u0005\n\u0011\u0005\u0017!\u00054pe6\fG/\u0012=fGV$xN]%egR!A1\u0018Cb\u0011!!I\f\"0A\u0002\u0011\u0015\u0007CBBq\u0007c\fI\u0002C\u0004\u0005J\u0002!I\u0001b3\u0002\u0019-LG\u000e\\#yK\u000e,Ho\u001c:\u0015\t\u00055GQ\u001a\u0005\t\t\u000b\"9\r1\u0001\u0005H!9A\u0011\u001b\u0001\u0005\n\u0011M\u0017\u0001\u00058fo\u0006\u0003\b\u000f\\5dCRLwN\\%e)\u0011\tI\u0002\"6\t\u0011\u0011]Gq\u001aa\u0001\t3\f!b];c[&$H)\u0019;f!\u0011!Y\u000e\"8\u000e\u0003]K1\u0001b8X\u0005\u0011!\u0015\r^3\t\u000f\u0011\r\b\u0001\"\u0003\u0004 \u0006\u0011B/[7f\u001fV$H)Z1e/>\u00148.\u001a:t\u0011\u001d!9\u000f\u0001C\u0005\tS\f1B\\3x\tJLg/\u001a:JIR!\u0011\u0011\u0004Cv\u0011!!9\u000e\":A\u0002\u0011e\u0007b\u0002Cx\u0001\u0011%A\u0011_\u0001\rGJ,\u0017\r^3Ee&4XM\u001d\u000b\u0005\u0003G$\u0019\u0010\u0003\u0005\u0005t\u00115\b\u0019\u0001C{!\u0011!9\bb>\n\u0007\u0011eHAA\tEe&4XM\u001d#fg\u000e\u0014\u0018\u000e\u001d;j_:Dq\u0001\"@\u0001\t\u0013!y0\u0001\u0007mCVt7\r\u001b#sSZ,'\u000f\u0006\u0004\u0002N\u0016\u0005Q1\u0001\u0005\t\tk!Y\u00101\u0001\u0002J!AA\u0011\u000eC~\u0001\u0004\t\u0019\u000fC\u0004\u0006\b\u0001!I!\"\u0003\u0002\u0019I,Wn\u001c<f\tJLg/\u001a:\u0015\u0011\u00055W1BC\b\u000bkA\u0001\"\"\u0004\u0006\u0006\u0001\u0007\u0011\u0011D\u0001\tIJLg/\u001a:JI\"AQ\u0011CC\u0003\u0001\u0004)\u0019\"\u0001\u0006gS:\fGn\u0015;bi\u0016\u0004B!\"\u0006\u000609!QqCC\u0016\u001d\u0011)I\"\"\u000b\u000f\t\u0015mQq\u0005\b\u0005\u000b;))C\u0004\u0003\u0006 \u0015\rb\u0002BBs\u000bCI\u0011aC\u0005\u0003\u0013)I!a\u0002\u0005\n\u0005\u00151\u0011BA\u0002\u0005\u0013\r)iCA\u0001\f\tJLg/\u001a:Ti\u0006$X-\u0003\u0003\u00062\u0015M\"a\u0003#sSZ,'o\u0015;bi\u0016T1!\"\f\u0003\u0011!)9$\"\u0002A\u0002\u0015e\u0012!C3yG\u0016\u0004H/[8o!\u0015q11NC\u001e!\u0011\u0019\t/\"\u0010\n\t\u0015}2Q\u001f\u0002\n\u000bb\u001cW\r\u001d;j_:<\u0001\"b\u0011\u0003\u0011\u0003!QQI\u0001\u0007\u001b\u0006\u001cH/\u001a:\u0011\u0007\u0001*9EB\u0004\u0002\u0005!\u0005A!\"\u0013\u0014\t\u0015\u001dS\"\u0007\u0005\b\u0011\u0016\u001dC\u0011AC')\t))\u0005\u0003\u0006\u0006R\u0015\u001d#\u0019!C\u0001\u000b'\n1bU-T)\u0016kuLT!N\u000bV\u0011QQ\u000b\t\u0005\u000b/*i&\u0004\u0002\u0006Z)\u0019Q1L-\u0002\t1\fgnZ\u0005\u0005\u0003G)I\u0006C\u0005\u0006b\u0015\u001d\u0003\u0015!\u0003\u0006V\u0005a1+W*U\u000b6{f*Q'FA!QQQMC$\u0005\u0004%\t!b\u0015\u0002\u001b\u0015sE\tU(J\u001dR{f*Q'F\u0011%)I'b\u0012!\u0002\u0013))&\u0001\bF\u001d\u0012\u0003v*\u0013(U?:\u000bU*\u0012\u0011\t\u0011\u00155Tq\tC\u0001\u000b_\nA!\\1j]R!\u0011QZC9\u0011!)\u0019(b\u001bA\u0002\u0015U\u0014AC1sON#(/\u001b8hgB)a\u0002b\u0004\u0002\u001a!AQ\u0011PC$\t\u0003)Y(\u0001\fti\u0006\u0014HO\u00159d\u000b:4\u0018I\u001c3F]\u0012\u0004x.\u001b8u)))i(b!\u0006\b\u0016-UQ\u0012\t\b\u001d\u0015}teMBG\u0013\r)\ti\u0004\u0002\u0007)V\u0004H.Z\u001a\t\u0011\u0015\u0015Uq\u000fa\u0001\u00033\tA\u0001[8ti\"9Q\u0011RC<\u0001\u0004\u0019\u0014\u0001\u00029peRDaAMC<\u0001\u0004\u0019\u0004B\u0002!\u0006x\u0001\u0007!\t"
)
public class Master implements ThreadSafeRpcEndpoint, Logging, LeaderElectable {
   private final RpcEnv rpcEnv;
   public final RpcAddress org$apache$spark$deploy$master$Master$$address;
   private final int webUiPort;
   private final SecurityManager securityMgr;
   private final SparkConf conf;
   private final ScheduledExecutorService org$apache$spark$deploy$master$Master$$forwardMessageThread;
   private final Configuration hadoopConf;
   private final long org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS;
   private final int RETAINED_APPLICATIONS;
   private final int RETAINED_DRIVERS;
   private final int org$apache$spark$deploy$master$Master$$REAPER_ITERATIONS;
   private final String RECOVERY_MODE;
   private final int org$apache$spark$deploy$master$Master$$MAX_EXECUTOR_RETRIES;
   private final HashSet<WorkerInfo> workers;
   private final HashMap<String, ApplicationInfo> idToApp;
   private final ArrayBuffer<ApplicationInfo> waitingApps;
   private final HashSet<ApplicationInfo> apps;
   private final HashMap<String, WorkerInfo> org$apache$spark$deploy$master$Master$$idToWorker;
   private final HashMap<RpcAddress, WorkerInfo> addressToWorker;
   private final HashMap<RpcEndpointRef, ApplicationInfo> endpointToApp;
   private final HashMap<RpcAddress, ApplicationInfo> addressToApp;
   private final ArrayBuffer<ApplicationInfo> org$apache$spark$deploy$master$Master$$completedApps;
   private int nextAppNumber;
   private final HashSet<DriverInfo> org$apache$spark$deploy$master$Master$$drivers;
   private final ArrayBuffer<DriverInfo> org$apache$spark$deploy$master$Master$$completedDrivers;
   private final ArrayBuffer<DriverInfo> org$apache$spark$deploy$master$Master$$waitingDrivers;
   private int nextDriverNumber;
   private final MetricsSystem masterMetricsSystem;
   private final MetricsSystem org$apache$spark$deploy$master$Master$$applicationMetricsSystem;
   private final MasterSource masterSource;
   private MasterWebUI org$apache$spark$deploy$master$Master$$webUi;
   private final String masterPublicAddress;
   private final String org$apache$spark$deploy$master$Master$$masterUrl;
   private String org$apache$spark$deploy$master$Master$$masterWebUiUrl;
   private Enumeration.Value org$apache$spark$deploy$master$Master$$state;
   private PersistenceEngine org$apache$spark$deploy$master$Master$$persistenceEngine;
   private LeaderElectionAgent leaderElectionAgent;
   private ScheduledFuture<?> org$apache$spark$deploy$master$Master$$recoveryCompletionTask;
   private ScheduledFuture<?> checkForWorkerTimeOutTask;
   private final boolean org$apache$spark$deploy$master$Master$$spreadOutApps;
   private final int defaultCores;
   private final boolean reverseProxy;
   private final boolean restServerEnabled;
   private Option<StandaloneRestServer> restServer;
   private Option<Object> org$apache$spark$deploy$master$Master$$restServerBoundPort;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Tuple3<RpcEnv, Object, Option<Object>> startRpcEnvAndEndpoint(String var0, int var1, int var2, SparkConf var3) {
      return Master$.MODULE$.startRpcEnvAndEndpoint(var0, var1, var2, var3);
   }

   public static void main(String[] var0) {
      Master$.MODULE$.main(var0);
   }

   public static String ENDPOINT_NAME() {
      return Master$.MODULE$.ENDPOINT_NAME();
   }

   public static String SYSTEM_NAME() {
      return Master$.MODULE$.SYSTEM_NAME();
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

   public final RpcEndpointRef self() {
      return RpcEndpoint$class.self(this);
   }

   public void onError(Throwable cause) {
      RpcEndpoint$class.onError(this, cause);
   }

   public void onConnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onConnected(this, remoteAddress);
   }

   public void onNetworkError(Throwable cause, RpcAddress remoteAddress) {
      RpcEndpoint$class.onNetworkError(this, cause, remoteAddress);
   }

   public final void stop() {
      RpcEndpoint$class.stop(this);
   }

   public RpcEnv rpcEnv() {
      return this.rpcEnv;
   }

   public SecurityManager securityMgr() {
      return this.securityMgr;
   }

   public SparkConf conf() {
      return this.conf;
   }

   public ScheduledExecutorService org$apache$spark$deploy$master$Master$$forwardMessageThread() {
      return this.org$apache$spark$deploy$master$Master$$forwardMessageThread;
   }

   private Configuration hadoopConf() {
      return this.hadoopConf;
   }

   private SimpleDateFormat createDateFormat() {
      return new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
   }

   public long org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS() {
      return this.org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS;
   }

   private int RETAINED_APPLICATIONS() {
      return this.RETAINED_APPLICATIONS;
   }

   private int RETAINED_DRIVERS() {
      return this.RETAINED_DRIVERS;
   }

   public int org$apache$spark$deploy$master$Master$$REAPER_ITERATIONS() {
      return this.org$apache$spark$deploy$master$Master$$REAPER_ITERATIONS;
   }

   private String RECOVERY_MODE() {
      return this.RECOVERY_MODE;
   }

   public int org$apache$spark$deploy$master$Master$$MAX_EXECUTOR_RETRIES() {
      return this.org$apache$spark$deploy$master$Master$$MAX_EXECUTOR_RETRIES;
   }

   public HashSet<WorkerInfo> workers() {
      return this.workers;
   }

   public HashMap<String, ApplicationInfo> idToApp() {
      return this.idToApp;
   }

   private ArrayBuffer<ApplicationInfo> waitingApps() {
      return this.waitingApps;
   }

   public HashSet<ApplicationInfo> apps() {
      return this.apps;
   }

   public HashMap<String, WorkerInfo> org$apache$spark$deploy$master$Master$$idToWorker() {
      return this.org$apache$spark$deploy$master$Master$$idToWorker;
   }

   private HashMap<RpcAddress, WorkerInfo> addressToWorker() {
      return this.addressToWorker;
   }

   private HashMap<RpcEndpointRef, ApplicationInfo> endpointToApp() {
      return this.endpointToApp;
   }

   private HashMap<RpcAddress, ApplicationInfo> addressToApp() {
      return this.addressToApp;
   }

   public ArrayBuffer<ApplicationInfo> org$apache$spark$deploy$master$Master$$completedApps() {
      return this.org$apache$spark$deploy$master$Master$$completedApps;
   }

   private int nextAppNumber() {
      return this.nextAppNumber;
   }

   private void nextAppNumber_$eq(int x$1) {
      this.nextAppNumber = x$1;
   }

   public HashSet<DriverInfo> org$apache$spark$deploy$master$Master$$drivers() {
      return this.org$apache$spark$deploy$master$Master$$drivers;
   }

   public ArrayBuffer<DriverInfo> org$apache$spark$deploy$master$Master$$completedDrivers() {
      return this.org$apache$spark$deploy$master$Master$$completedDrivers;
   }

   public ArrayBuffer<DriverInfo> org$apache$spark$deploy$master$Master$$waitingDrivers() {
      return this.org$apache$spark$deploy$master$Master$$waitingDrivers;
   }

   private int nextDriverNumber() {
      return this.nextDriverNumber;
   }

   private void nextDriverNumber_$eq(int x$1) {
      this.nextDriverNumber = x$1;
   }

   private MetricsSystem masterMetricsSystem() {
      return this.masterMetricsSystem;
   }

   public MetricsSystem org$apache$spark$deploy$master$Master$$applicationMetricsSystem() {
      return this.org$apache$spark$deploy$master$Master$$applicationMetricsSystem;
   }

   private MasterSource masterSource() {
      return this.masterSource;
   }

   public MasterWebUI org$apache$spark$deploy$master$Master$$webUi() {
      return this.org$apache$spark$deploy$master$Master$$webUi;
   }

   private void org$apache$spark$deploy$master$Master$$webUi_$eq(MasterWebUI x$1) {
      this.org$apache$spark$deploy$master$Master$$webUi = x$1;
   }

   private String masterPublicAddress() {
      return this.masterPublicAddress;
   }

   public String org$apache$spark$deploy$master$Master$$masterUrl() {
      return this.org$apache$spark$deploy$master$Master$$masterUrl;
   }

   public String org$apache$spark$deploy$master$Master$$masterWebUiUrl() {
      return this.org$apache$spark$deploy$master$Master$$masterWebUiUrl;
   }

   private void org$apache$spark$deploy$master$Master$$masterWebUiUrl_$eq(String x$1) {
      this.org$apache$spark$deploy$master$Master$$masterWebUiUrl = x$1;
   }

   public Enumeration.Value org$apache$spark$deploy$master$Master$$state() {
      return this.org$apache$spark$deploy$master$Master$$state;
   }

   public void org$apache$spark$deploy$master$Master$$state_$eq(Enumeration.Value x$1) {
      this.org$apache$spark$deploy$master$Master$$state = x$1;
   }

   public PersistenceEngine org$apache$spark$deploy$master$Master$$persistenceEngine() {
      return this.org$apache$spark$deploy$master$Master$$persistenceEngine;
   }

   private void org$apache$spark$deploy$master$Master$$persistenceEngine_$eq(PersistenceEngine x$1) {
      this.org$apache$spark$deploy$master$Master$$persistenceEngine = x$1;
   }

   private LeaderElectionAgent leaderElectionAgent() {
      return this.leaderElectionAgent;
   }

   private void leaderElectionAgent_$eq(LeaderElectionAgent x$1) {
      this.leaderElectionAgent = x$1;
   }

   private ScheduledFuture<?> org$apache$spark$deploy$master$Master$$recoveryCompletionTask() {
      return this.org$apache$spark$deploy$master$Master$$recoveryCompletionTask;
   }

   public void org$apache$spark$deploy$master$Master$$recoveryCompletionTask_$eq(ScheduledFuture<?> x$1) {
      this.org$apache$spark$deploy$master$Master$$recoveryCompletionTask = x$1;
   }

   private ScheduledFuture<?> checkForWorkerTimeOutTask() {
      return this.checkForWorkerTimeOutTask;
   }

   private void checkForWorkerTimeOutTask_$eq(ScheduledFuture<?> x$1) {
      this.checkForWorkerTimeOutTask = x$1;
   }

   public boolean org$apache$spark$deploy$master$Master$$spreadOutApps() {
      return this.org$apache$spark$deploy$master$Master$$spreadOutApps;
   }

   private int defaultCores() {
      return this.defaultCores;
   }

   public boolean reverseProxy() {
      return this.reverseProxy;
   }

   private boolean restServerEnabled() {
      return this.restServerEnabled;
   }

   private Option<StandaloneRestServer> restServer() {
      return this.restServer;
   }

   private void restServer_$eq(Option<StandaloneRestServer> x$1) {
      this.restServer = x$1;
   }

   public Option<Object> org$apache$spark$deploy$master$Master$$restServerBoundPort() {
      return this.org$apache$spark$deploy$master$Master$$restServerBoundPort;
   }

   private void org$apache$spark$deploy$master$Master$$restServerBoundPort_$eq(Option<Object> x$1) {
      this.org$apache$spark$deploy$master$Master$$restServerBoundPort = x$1;
   }

   public void onStart() {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Starting Spark master at ").append((Object)this.$outer.org$apache$spark$deploy$master$Master$$masterUrl()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Running Spark version ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{package$.MODULE$.SPARK_VERSION()}));
         }
      });
      this.org$apache$spark$deploy$master$Master$$webUi_$eq(new MasterWebUI(this, this.webUiPort));
      this.org$apache$spark$deploy$master$Master$$webUi().bind();
      this.org$apache$spark$deploy$master$Master$$masterWebUiUrl_$eq((new StringBuilder()).append((Object)"http://").append((Object)this.masterPublicAddress()).append((Object)":").append((Object)BoxesRunTime.boxToInteger(this.org$apache$spark$deploy$master$Master$$webUi().boundPort())).toString());
      if (this.reverseProxy()) {
         this.org$apache$spark$deploy$master$Master$$masterWebUiUrl_$eq(this.conf().get("spark.ui.reverseProxyUrl", this.org$apache$spark$deploy$master$Master$$masterWebUiUrl()));
         this.org$apache$spark$deploy$master$Master$$webUi().addProxy();
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Master $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Spark Master is acting as a reverse proxy. Master, Workers and "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Applications UIs are available at ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$master$Master$$masterWebUiUrl()}))).toString();
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

      this.checkForWorkerTimeOutTask_$eq(this.org$apache$spark$deploy$master$Master$$forwardMessageThread().scheduleAtFixedRate(new Runnable(this) {
         // $FF: synthetic field
         private final Master $outer;

         public void run() {
            Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.org$apache$spark$deploy$master$Master$$anon$$$outer().self().send(MasterMessages.CheckForWorkerTimeOut$.MODULE$);
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
         public Master org$apache$spark$deploy$master$Master$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, 0L, this.org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS(), TimeUnit.MILLISECONDS));
      if (this.restServerEnabled()) {
         int port = this.conf().getInt("spark.master.rest.port", 6066);
         this.restServer_$eq(new Some(new StandaloneRestServer(this.org$apache$spark$deploy$master$Master$$address.host(), port, this.conf(), this.self(), this.org$apache$spark$deploy$master$Master$$masterUrl())));
      }

      this.org$apache$spark$deploy$master$Master$$restServerBoundPort_$eq(this.restServer().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(StandaloneRestServer x$1) {
            return x$1.start();
         }
      }));
      this.masterMetricsSystem().registerSource(this.masterSource());
      this.masterMetricsSystem().start();
      this.org$apache$spark$deploy$master$Master$$applicationMetricsSystem().start();
      ArrayOps var10000 = Predef$.MODULE$.refArrayOps(this.masterMetricsSystem().getServletHandlers());
      MasterWebUI var2 = this.org$apache$spark$deploy$master$Master$$webUi();
      var10000.foreach(new Serializable(this, var2) {
         public static final long serialVersionUID = 0L;
         private final MasterWebUI eta$0$1$1;

         public final void apply(ServletContextHandler handler) {
            this.eta$0$1$1.attachHandler(handler);
         }

         public {
            this.eta$0$1$1 = eta$0$1$1;
         }
      });
      var10000 = Predef$.MODULE$.refArrayOps(this.org$apache$spark$deploy$master$Master$$applicationMetricsSystem().getServletHandlers());
      MasterWebUI var3 = this.org$apache$spark$deploy$master$Master$$webUi();
      var10000.foreach(new Serializable(this, var3) {
         public static final long serialVersionUID = 0L;
         private final MasterWebUI eta$0$2$1;

         public final void apply(ServletContextHandler handler) {
            this.eta$0$2$1.attachHandler(handler);
         }

         public {
            this.eta$0$2$1 = eta$0$2$1;
         }
      });
      JavaSerializer serializer = new JavaSerializer(this.conf());
      String var7 = this.RECOVERY_MODE();
      Tuple2 var8;
      if ("ZOOKEEPER".equals(var7)) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Persisting recovery state to ZooKeeper";
            }
         });
         ZooKeeperRecoveryModeFactory zkFactory = new ZooKeeperRecoveryModeFactory(this.conf(), serializer);
         var8 = new Tuple2(zkFactory.createPersistenceEngine(), zkFactory.createLeaderElectionAgent(this));
      } else if ("FILESYSTEM".equals(var7)) {
         FileSystemRecoveryModeFactory fsFactory = new FileSystemRecoveryModeFactory(this.conf(), serializer);
         var8 = new Tuple2(fsFactory.createPersistenceEngine(), fsFactory.createLeaderElectionAgent(this));
      } else if ("CUSTOM".equals(var7)) {
         Class clazz = Utils$.MODULE$.classForName(this.conf().get("spark.deploy.recoveryMode.factory"));
         StandaloneRecoveryModeFactory factory = (StandaloneRecoveryModeFactory)clazz.getConstructor(SparkConf.class, Serializer.class).newInstance(this.conf(), serializer);
         var8 = new Tuple2(factory.createPersistenceEngine(), factory.createLeaderElectionAgent(this));
      } else {
         var8 = new Tuple2(new BlackHolePersistenceEngine(), new MonarchyLeaderAgent(this));
      }

      if (var8 != null) {
         PersistenceEngine persistenceEngine_ = (PersistenceEngine)var8._1();
         LeaderElectionAgent leaderElectionAgent_ = (LeaderElectionAgent)var8._2();
         Tuple2 var15 = new Tuple2(persistenceEngine_, leaderElectionAgent_);
         PersistenceEngine persistenceEngine_ = (PersistenceEngine)var15._1();
         LeaderElectionAgent leaderElectionAgent_ = (LeaderElectionAgent)var15._2();
         this.org$apache$spark$deploy$master$Master$$persistenceEngine_$eq(persistenceEngine_);
         this.leaderElectionAgent_$eq(leaderElectionAgent_);
      } else {
         throw new MatchError(var8);
      }
   }

   public void onStop() {
      this.masterMetricsSystem().report();
      this.org$apache$spark$deploy$master$Master$$applicationMetricsSystem().report();
      if (this.org$apache$spark$deploy$master$Master$$recoveryCompletionTask() == null) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         BoxesRunTime.boxToBoolean(this.org$apache$spark$deploy$master$Master$$recoveryCompletionTask().cancel(true));
      }

      if (this.checkForWorkerTimeOutTask() == null) {
         BoxedUnit var1 = BoxedUnit.UNIT;
      } else {
         BoxesRunTime.boxToBoolean(this.checkForWorkerTimeOutTask().cancel(true));
      }

      this.org$apache$spark$deploy$master$Master$$forwardMessageThread().shutdownNow();
      this.org$apache$spark$deploy$master$Master$$webUi().stop();
      this.restServer().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(StandaloneRestServer x$3) {
            x$3.stop();
         }
      });
      this.masterMetricsSystem().stop();
      this.org$apache$spark$deploy$master$Master$$applicationMetricsSystem().stop();
      this.org$apache$spark$deploy$master$Master$$persistenceEngine().close();
      this.leaderElectionAgent().stop();
   }

   public void electedLeader() {
      this.self().send(MasterMessages.ElectedLeader$.MODULE$);
   }

   public void revokedLeadership() {
      this.self().send(MasterMessages.RevokedLeadership$.MODULE$);
   }

   public PartialFunction<Object, BoxedUnit> receive() {
      return new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var4;
            if (MasterMessages.ElectedLeader$.MODULE$.equals(x1)) {
               Tuple3 var6 = this.$outer.org$apache$spark$deploy$master$Master$$persistenceEngine().readPersistedData(this.$outer.rpcEnv());
               if (var6 == null) {
                  throw new MatchError(var6);
               }

               BoxedUnit var97;
               label269: {
                  Seq storedApps;
                  Seq storedDrivers;
                  Seq storedWorkers;
                  label268: {
                     Seq storedApps = (Seq)var6._1();
                     Seq storedDrivers = (Seq)var6._2();
                     Seq storedWorkers = (Seq)var6._3();
                     Tuple3 var10 = new Tuple3(storedApps, storedDrivers, storedWorkers);
                     storedApps = (Seq)var10._1();
                     storedDrivers = (Seq)var10._2();
                     storedWorkers = (Seq)var10._3();
                     this.$outer.org$apache$spark$deploy$master$Master$$state_$eq(storedApps.isEmpty() && storedDrivers.isEmpty() && storedWorkers.isEmpty() ? RecoveryState$.MODULE$.ALIVE() : RecoveryState$.MODULE$.RECOVERING());
                     this.$outer.logInfo(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"I have been elected leader! New state: ").append((Object)this.$outer.org$apache$spark$deploy$master$Master$$anonfun$$$outer().org$apache$spark$deploy$master$Master$$state()).toString();
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     Enumeration.Value var10000 = this.$outer.org$apache$spark$deploy$master$Master$$state();
                     Enumeration.Value var14 = RecoveryState$.MODULE$.RECOVERING();
                     if (var10000 == null) {
                        if (var14 == null) {
                           break label268;
                        }
                     } else if (var10000.equals(var14)) {
                        break label268;
                     }

                     var97 = BoxedUnit.UNIT;
                     break label269;
                  }

                  this.$outer.org$apache$spark$deploy$master$Master$$beginRecovery(storedApps, storedDrivers, storedWorkers);
                  this.$outer.org$apache$spark$deploy$master$Master$$recoveryCompletionTask_$eq(this.$outer.org$apache$spark$deploy$master$Master$$forwardMessageThread().schedule(new Runnable(this) {
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public void run() {
                        Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final void apply() {
                              this.apply$mcV$sp();
                           }

                           public void apply$mcV$sp() {
                              this.$outer.org$apache$spark$deploy$master$Master$$anonfun$$anon$$$outer().org$apache$spark$deploy$master$Master$$anonfun$$$outer().self().send(MasterMessages.CompleteRecovery$.MODULE$);
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
                     public <undefinedtype> org$apache$spark$deploy$master$Master$$anonfun$$anon$$$outer() {
                        return this.$outer;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  }, this.$outer.org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS(), TimeUnit.MILLISECONDS));
                  var97 = BoxedUnit.UNIT;
               }

               var4 = var97;
            } else if (MasterMessages.CompleteRecovery$.MODULE$.equals(x1)) {
               this.$outer.org$apache$spark$deploy$master$Master$$completeRecovery();
               var4 = BoxedUnit.UNIT;
            } else if (MasterMessages.RevokedLeadership$.MODULE$.equals(x1)) {
               this.$outer.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Leadership has been revoked -- master shutting down.";
                  }
               });
               System.exit(0);
               var4 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.RegisterWorker) {
               BoxedUnit var99;
               label258: {
                  RpcEndpointRef workerRef;
                  label257: {
                     DeployMessages.RegisterWorker var15 = (DeployMessages.RegisterWorker)x1;
                     String id = var15.id();
                     String workerHost = var15.host();
                     int workerPort = var15.port();
                     workerRef = var15.worker();
                     int cores = var15.cores();
                     int memory = var15.memory();
                     String workerWebUiUrl = var15.workerWebUiUrl();
                     RpcAddress masterAddress = var15.masterAddress();
                     this.$outer.logInfo(new Serializable(this, workerHost, workerPort, cores, memory) {
                        public static final long serialVersionUID = 0L;
                        private final String workerHost$1;
                        private final int workerPort$1;
                        private final int cores$1;
                        private final int memory$1;

                        public final String apply() {
                           return (new StringOps(Predef$.MODULE$.augmentString("Registering worker %s:%d with %d cores, %s RAM"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.workerHost$1, BoxesRunTime.boxToInteger(this.workerPort$1), BoxesRunTime.boxToInteger(this.cores$1), Utils$.MODULE$.megabytesToString((long)this.memory$1)}));
                        }

                        public {
                           this.workerHost$1 = workerHost$1;
                           this.workerPort$1 = workerPort$1;
                           this.cores$1 = cores$1;
                           this.memory$1 = memory$1;
                        }
                     });
                     Enumeration.Value var98 = this.$outer.org$apache$spark$deploy$master$Master$$state();
                     Enumeration.Value var24 = RecoveryState$.MODULE$.STANDBY();
                     if (var98 == null) {
                        if (var24 == null) {
                           break label257;
                        }
                     } else if (var98.equals(var24)) {
                        break label257;
                     }

                     if (this.$outer.org$apache$spark$deploy$master$Master$$idToWorker().contains(id)) {
                        workerRef.send(new DeployMessages.RegisterWorkerFailed("Duplicate worker ID"));
                        var99 = BoxedUnit.UNIT;
                     } else {
                        WorkerInfo worker = new WorkerInfo(id, workerHost, workerPort, cores, memory, workerRef, workerWebUiUrl);
                        if (this.$outer.org$apache$spark$deploy$master$Master$$registerWorker(worker)) {
                           this.$outer.org$apache$spark$deploy$master$Master$$persistenceEngine().addWorker(worker);
                           workerRef.send(new DeployMessages.RegisteredWorker(this.$outer.self(), this.$outer.org$apache$spark$deploy$master$Master$$masterWebUiUrl(), masterAddress));
                           this.$outer.org$apache$spark$deploy$master$Master$$schedule();
                           var99 = BoxedUnit.UNIT;
                        } else {
                           RpcAddress workerAddress = worker.endpoint().address();
                           this.$outer.logWarning(new Serializable(this, workerAddress) {
                              public static final long serialVersionUID = 0L;
                              private final RpcAddress workerAddress$1;

                              public final String apply() {
                                 return (new StringBuilder()).append((Object)"Worker registration failed. Attempted to re-register worker at same address: ").append((Object)this.workerAddress$1).toString();
                              }

                              public {
                                 this.workerAddress$1 = workerAddress$1;
                              }
                           });
                           workerRef.send(new DeployMessages.RegisterWorkerFailed((new StringBuilder()).append((Object)"Attempted to re-register worker at same address: ").append((Object)workerAddress).toString()));
                           var99 = BoxedUnit.UNIT;
                        }
                     }
                     break label258;
                  }

                  workerRef.send(DeployMessages.MasterInStandby$.MODULE$);
                  var99 = BoxedUnit.UNIT;
               }

               var4 = var99;
            } else if (x1 instanceof DeployMessages.RegisterApplication) {
               BoxedUnit var101;
               label249: {
                  label248: {
                     DeployMessages.RegisterApplication var27 = (DeployMessages.RegisterApplication)x1;
                     ApplicationDescription description = var27.appDescription();
                     RpcEndpointRef driver = var27.driver();
                     Enumeration.Value var100 = this.$outer.org$apache$spark$deploy$master$Master$$state();
                     Enumeration.Value var30 = RecoveryState$.MODULE$.STANDBY();
                     if (var100 == null) {
                        if (var30 == null) {
                           break label248;
                        }
                     } else if (var100.equals(var30)) {
                        break label248;
                     }

                     this.$outer.logInfo(new Serializable(this, description) {
                        public static final long serialVersionUID = 0L;
                        private final ApplicationDescription description$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Registering app ").append((Object)this.description$1.name()).toString();
                        }

                        public {
                           this.description$1 = description$1;
                        }
                     });
                     ApplicationInfo app = this.$outer.org$apache$spark$deploy$master$Master$$createApplication(description, driver);
                     this.$outer.org$apache$spark$deploy$master$Master$$registerApplication(app);
                     this.$outer.logInfo(new Serializable(this, description, app) {
                        public static final long serialVersionUID = 0L;
                        private final ApplicationDescription description$1;
                        private final ApplicationInfo app$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Registered app ").append((Object)this.description$1.name()).append((Object)" with ID ").append((Object)this.app$1.id()).toString();
                        }

                        public {
                           this.description$1 = description$1;
                           this.app$1 = app$1;
                        }
                     });
                     this.$outer.org$apache$spark$deploy$master$Master$$persistenceEngine().addApplication(app);
                     driver.send(new DeployMessages.RegisteredApplication(app.id(), this.$outer.self()));
                     this.$outer.org$apache$spark$deploy$master$Master$$schedule();
                     var101 = BoxedUnit.UNIT;
                     break label249;
                  }

                  var101 = BoxedUnit.UNIT;
               }

               var4 = var101;
            } else if (x1 instanceof DeployMessages.ExecutorStateChanged) {
               DeployMessages.ExecutorStateChanged var32 = (DeployMessages.ExecutorStateChanged)x1;
               String appId = var32.appId();
               int execId = var32.execId();
               Enumeration.Value state = var32.state();
               Option message = var32.message();
               Option exitStatus = var32.exitStatus();
               Option execOption = this.$outer.idToApp().get(appId).flatMap(new Serializable(this, execId) {
                  public static final long serialVersionUID = 0L;
                  private final int execId$1;

                  public final Option<ExecutorDesc> apply(ApplicationInfo app) {
                     return app.executors().get(BoxesRunTime.boxToInteger(this.execId$1));
                  }

                  public {
                     this.execId$1 = execId$1;
                  }
               });
               if (!(execOption instanceof Some)) {
                  if (!None$.MODULE$.equals(execOption)) {
                     throw new MatchError(execOption);
                  }

                  this.$outer.logWarning(new Serializable(this, appId, execId) {
                     public static final long serialVersionUID = 0L;
                     private final String appId$1;
                     private final int execId$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Got status update for unknown executor ", "/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$1, BoxesRunTime.boxToInteger(this.execId$1)}));
                     }

                     public {
                        this.appId$1 = appId$1;
                        this.execId$1 = execId$1;
                     }
                  });
                  BoxedUnit var93 = BoxedUnit.UNIT;
               } else {
                  ExecutorDesc exec;
                  ApplicationInfo appInfo;
                  label282: {
                     Some var40 = (Some)execOption;
                     exec = (ExecutorDesc)var40.x();
                     appInfo = (ApplicationInfo)this.$outer.idToApp().apply(appId);
                     Enumeration.Value oldState = exec.state();
                     exec.state_$eq(state);
                     Enumeration.Value var45 = ExecutorState$.MODULE$.RUNNING();
                     if (state == null) {
                        if (var45 != null) {
                           break label282;
                        }
                     } else if (!state.equals(var45)) {
                        break label282;
                     }

                     Predef$ var102;
                     boolean var10001;
                     label232: {
                        label231: {
                           var102 = Predef$.MODULE$;
                           Enumeration.Value var46 = ExecutorState$.MODULE$.LAUNCHING();
                           if (oldState == null) {
                              if (var46 == null) {
                                 break label231;
                              }
                           } else if (oldState.equals(var46)) {
                              break label231;
                           }

                           var10001 = false;
                           break label232;
                        }

                        var10001 = true;
                     }

                     var102.assert(var10001, new Serializable(this, execId, oldState) {
                        public static final long serialVersionUID = 0L;
                        private final int execId$1;
                        private final Enumeration.Value oldState$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"executor ", " state transfer from ", " to RUNNING is illegal"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.execId$1), this.oldState$1}));
                        }

                        public {
                           this.execId$1 = execId$1;
                           this.oldState$1 = oldState$1;
                        }
                     });
                     appInfo.resetRetryCount();
                  }

                  exec.application().driver().send(new DeployMessages.ExecutorUpdated(execId, state, message, exitStatus, false));
                  if (ExecutorState$.MODULE$.isFinished(state)) {
                     this.$outer.logInfo(new Serializable(this, state, exec) {
                        public static final long serialVersionUID = 0L;
                        private final Enumeration.Value state$1;
                        private final ExecutorDesc exec$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing executor ", " because it is ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.exec$1.fullId(), this.state$1}));
                        }

                        public {
                           this.state$1 = state$1;
                           this.exec$1 = exec$1;
                        }
                     });
                     if (!appInfo.isFinished()) {
                        appInfo.removeExecutor(exec);
                     }

                     boolean var103;
                     label221: {
                        label220: {
                           exec.worker().removeExecutor(exec);
                           Some var48 = new Some(BoxesRunTime.boxToInteger(0));
                           if (exitStatus == null) {
                              if (var48 == null) {
                                 break label220;
                              }
                           } else if (exitStatus.equals(var48)) {
                              break label220;
                           }

                           var103 = false;
                           break label221;
                        }

                        var103 = true;
                     }

                     boolean normalExit = var103;
                     if (!normalExit && appInfo.incrementRetryCount() >= this.$outer.org$apache$spark$deploy$master$Master$$MAX_EXECUTOR_RETRIES() && this.$outer.org$apache$spark$deploy$master$Master$$MAX_EXECUTOR_RETRIES() >= 0) {
                        Iterable execs = appInfo.executors().values();
                        if (!execs.exists(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final boolean apply(ExecutorDesc x$5) {
                              boolean var3;
                              label23: {
                                 Enumeration.Value var10000 = x$5.state();
                                 Enumeration.Value var2 = ExecutorState$.MODULE$.RUNNING();
                                 if (var10000 == null) {
                                    if (var2 == null) {
                                       break label23;
                                    }
                                 } else if (var10000.equals(var2)) {
                                    break label23;
                                 }

                                 var3 = false;
                                 return var3;
                              }

                              var3 = true;
                              return var3;
                           }
                        })) {
                           this.$outer.logError(new Serializable(this, appInfo) {
                              public static final long serialVersionUID = 0L;
                              private final ApplicationInfo appInfo$1;

                              public final String apply() {
                                 return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Application ", " with ID ", " failed "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appInfo$1.desc().name(), this.appInfo$1.id()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " times; removing it"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.appInfo$1.retryCount())}))).toString();
                              }

                              public {
                                 this.appInfo$1 = appInfo$1;
                              }
                           });
                           this.$outer.removeApplication(appInfo, ApplicationState$.MODULE$.FAILED());
                        }
                     }
                  }

                  this.$outer.org$apache$spark$deploy$master$Master$$schedule();
                  BoxedUnit var42 = BoxedUnit.UNIT;
               }

               var4 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.DriverStateChanged) {
               String driverId;
               Enumeration.Value state;
               Option exception;
               boolean var56;
               label210: {
                  label285: {
                     DeployMessages.DriverStateChanged var50 = (DeployMessages.DriverStateChanged)x1;
                     driverId = var50.driverId();
                     state = var50.state();
                     exception = var50.exception();
                     Enumeration.Value var104 = DriverState$.MODULE$.ERROR();
                     if (var104 == null) {
                        if (state == null) {
                           break label285;
                        }
                     } else if (var104.equals(state)) {
                        break label285;
                     }

                     label286: {
                        var104 = DriverState$.MODULE$.FINISHED();
                        if (var104 == null) {
                           if (state == null) {
                              break label286;
                           }
                        } else if (var104.equals(state)) {
                           break label286;
                        }

                        label287: {
                           var104 = DriverState$.MODULE$.KILLED();
                           if (var104 == null) {
                              if (state == null) {
                                 break label287;
                              }
                           } else if (var104.equals(state)) {
                              break label287;
                           }

                           label189: {
                              var104 = DriverState$.MODULE$.FAILED();
                              if (var104 == null) {
                                 if (state == null) {
                                    break label189;
                                 }
                              } else if (var104.equals(state)) {
                                 break label189;
                              }

                              var56 = false;
                              break label210;
                           }

                           var56 = true;
                           break label210;
                        }

                        var56 = true;
                        break label210;
                     }

                     var56 = true;
                     break label210;
                  }

                  var56 = true;
               }

               if (!var56) {
                  throw new Exception((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Received unexpected state update for driver ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{driverId, state})));
               }

               this.$outer.org$apache$spark$deploy$master$Master$$removeDriver(driverId, state, exception);
               BoxedUnit var60 = BoxedUnit.UNIT;
               var4 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.Heartbeat) {
               DeployMessages.Heartbeat var61 = (DeployMessages.Heartbeat)x1;
               String workerId = var61.workerId();
               RpcEndpointRef worker = var61.worker();
               Option var64 = this.$outer.org$apache$spark$deploy$master$Master$$idToWorker().get(workerId);
               if (var64 instanceof Some) {
                  Some var65 = (Some)var64;
                  WorkerInfo workerInfo = (WorkerInfo)var65.x();
                  workerInfo.lastHeartbeat_$eq(System.currentTimeMillis());
                  BoxedUnit var67 = BoxedUnit.UNIT;
               } else {
                  if (!None$.MODULE$.equals(var64)) {
                     throw new MatchError(var64);
                  }

                  if (((HashSet)this.$outer.workers().map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply(WorkerInfo x$6) {
                        return x$6.id();
                     }
                  }, HashSet$.MODULE$.canBuildFrom())).contains(workerId)) {
                     this.$outer.logWarning(new Serializable(this, workerId) {
                        public static final long serialVersionUID = 0L;
                        private final String workerId$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Got heartbeat from unregistered worker ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.workerId$1}))).append((Object)" Asking it to re-register.").toString();
                        }

                        public {
                           this.workerId$1 = workerId$1;
                        }
                     });
                     worker.send(new DeployMessages.ReconnectWorker(this.$outer.org$apache$spark$deploy$master$Master$$masterUrl()));
                     BoxedUnit var108 = BoxedUnit.UNIT;
                  } else {
                     this.$outer.logWarning(new Serializable(this, workerId) {
                        public static final long serialVersionUID = 0L;
                        private final String workerId$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Got heartbeat from unregistered worker ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.workerId$1}))).append((Object)" This worker was never registered, so ignoring the heartbeat.").toString();
                        }

                        public {
                           this.workerId$1 = workerId$1;
                        }
                     });
                     BoxedUnit var109 = BoxedUnit.UNIT;
                  }
               }

               var4 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.MasterChangeAcknowledged) {
               DeployMessages.MasterChangeAcknowledged var68 = (DeployMessages.MasterChangeAcknowledged)x1;
               String appId = var68.appId();
               Option var70 = this.$outer.idToApp().get(appId);
               if (var70 instanceof Some) {
                  Some var71 = (Some)var70;
                  ApplicationInfo app = (ApplicationInfo)var71.x();
                  this.$outer.logInfo(new Serializable(this, appId) {
                     public static final long serialVersionUID = 0L;
                     private final String appId$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Application has been re-registered: ").append((Object)this.appId$2).toString();
                     }

                     public {
                        this.appId$2 = appId$2;
                     }
                  });
                  app.state_$eq(ApplicationState$.MODULE$.WAITING());
                  BoxedUnit var73 = BoxedUnit.UNIT;
               } else {
                  if (!None$.MODULE$.equals(var70)) {
                     throw new MatchError(var70);
                  }

                  this.$outer.logWarning(new Serializable(this, appId) {
                     public static final long serialVersionUID = 0L;
                     private final String appId$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Master change ack from unknown app: ").append((Object)this.appId$2).toString();
                     }

                     public {
                        this.appId$2 = appId$2;
                     }
                  });
                  BoxedUnit var94 = BoxedUnit.UNIT;
               }

               BoxedUnit var110;
               if (this.$outer.org$apache$spark$deploy$master$Master$$canCompleteRecovery()) {
                  this.$outer.org$apache$spark$deploy$master$Master$$completeRecovery();
                  var110 = BoxedUnit.UNIT;
               } else {
                  var110 = BoxedUnit.UNIT;
               }

               var4 = var110;
            } else if (x1 instanceof DeployMessages.WorkerSchedulerStateResponse) {
               DeployMessages.WorkerSchedulerStateResponse var74 = (DeployMessages.WorkerSchedulerStateResponse)x1;
               String workerId = var74.id();
               List executors = var74.executors();
               Seq driverIds = var74.driverIds();
               Option var78 = this.$outer.org$apache$spark$deploy$master$Master$$idToWorker().get(workerId);
               if (var78 instanceof Some) {
                  Some var79 = (Some)var78;
                  WorkerInfo worker = (WorkerInfo)var79.x();
                  this.$outer.logInfo(new Serializable(this, workerId) {
                     public static final long serialVersionUID = 0L;
                     private final String workerId$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Worker has been re-registered: ").append((Object)this.workerId$2).toString();
                     }

                     public {
                        this.workerId$2 = workerId$2;
                     }
                  });
                  worker.state_$eq(WorkerState$.MODULE$.ALIVE());
                  List validExecutors = (List)executors.filter(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final boolean apply(ExecutorDescription exec) {
                        return this.$outer.org$apache$spark$deploy$master$Master$$anonfun$$$outer().idToApp().get(exec.appId()).isDefined();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  validExecutors.foreach(new Serializable(this, worker) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final WorkerInfo worker$2;

                     public final void apply(ExecutorDescription exec) {
                        ApplicationInfo app = (ApplicationInfo)this.$outer.org$apache$spark$deploy$master$Master$$anonfun$$$outer().idToApp().get(exec.appId()).get();
                        ExecutorDesc execInfo = app.addExecutor(this.worker$2, exec.cores(), new Some(BoxesRunTime.boxToInteger(exec.execId())));
                        this.worker$2.addExecutor(execInfo);
                        execInfo.copyState(exec);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.worker$2 = worker$2;
                        }
                     }
                  });
                  driverIds.foreach(new Serializable(this, worker) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     public final WorkerInfo worker$2;

                     public final void apply(String driverId) {
                        this.$outer.org$apache$spark$deploy$master$Master$$anonfun$$$outer().org$apache$spark$deploy$master$Master$$drivers().find(new Serializable(this, driverId) {
                           public static final long serialVersionUID = 0L;
                           private final String driverId$4;

                           public final boolean apply(DriverInfo x$7) {
                              boolean var3;
                              label23: {
                                 String var10000 = x$7.id();
                                 String var2 = this.driverId$4;
                                 if (var10000 == null) {
                                    if (var2 == null) {
                                       break label23;
                                    }
                                 } else if (var10000.equals(var2)) {
                                    break label23;
                                 }

                                 var3 = false;
                                 return var3;
                              }

                              var3 = true;
                              return var3;
                           }

                           public {
                              this.driverId$4 = driverId$4;
                           }
                        }).foreach(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final void apply(DriverInfo driver) {
                              driver.worker_$eq(new Some(this.$outer.worker$2));
                              driver.state_$eq(DriverState$.MODULE$.RUNNING());
                              this.$outer.worker$2.addDriver(driver);
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
                           this.worker$2 = worker$2;
                        }
                     }
                  });
                  BoxedUnit var81 = BoxedUnit.UNIT;
               } else {
                  if (!None$.MODULE$.equals(var78)) {
                     throw new MatchError(var78);
                  }

                  this.$outer.logWarning(new Serializable(this, workerId) {
                     public static final long serialVersionUID = 0L;
                     private final String workerId$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Scheduler state from unknown worker: ").append((Object)this.workerId$2).toString();
                     }

                     public {
                        this.workerId$2 = workerId$2;
                     }
                  });
                  BoxedUnit var95 = BoxedUnit.UNIT;
               }

               BoxedUnit var111;
               if (this.$outer.org$apache$spark$deploy$master$Master$$canCompleteRecovery()) {
                  this.$outer.org$apache$spark$deploy$master$Master$$completeRecovery();
                  var111 = BoxedUnit.UNIT;
               } else {
                  var111 = BoxedUnit.UNIT;
               }

               var4 = var111;
            } else if (x1 instanceof DeployMessages.WorkerLatestState) {
               DeployMessages.WorkerLatestState var83 = (DeployMessages.WorkerLatestState)x1;
               String workerId = var83.id();
               Seq executors = var83.executors();
               Seq driverIds = var83.driverIds();
               Option var87 = this.$outer.org$apache$spark$deploy$master$Master$$idToWorker().get(workerId);
               if (var87 instanceof Some) {
                  Some var88 = (Some)var87;
                  WorkerInfo worker = (WorkerInfo)var88.x();
                  executors.foreach(new Serializable(this, worker) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final WorkerInfo worker$3;

                     public final void apply(ExecutorDescription exec) {
                        boolean executorMatches = this.worker$3.executors().exists(new Serializable(this, exec) {
                           public static final long serialVersionUID = 0L;
                           private final ExecutorDescription exec$3;

                           public final boolean apply(Tuple2<String, ExecutorDesc> x0$1) {
                              if (x0$1 == null) {
                                 throw new MatchError(x0$1);
                              } else {
                                 boolean var6;
                                 label22: {
                                    label21: {
                                       ExecutorDesc e = (ExecutorDesc)x0$1._2();
                                       String var10000 = e.application().id();
                                       String var5 = this.exec$3.appId();
                                       if (var10000 == null) {
                                          if (var5 != null) {
                                             break label21;
                                          }
                                       } else if (!var10000.equals(var5)) {
                                          break label21;
                                       }

                                       if (e.id() == this.exec$3.execId()) {
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
                              this.exec$3 = exec$3;
                           }
                        });
                        if (!executorMatches) {
                           this.worker$3.endpoint().send(new DeployMessages.KillExecutor(this.$outer.org$apache$spark$deploy$master$Master$$anonfun$$$outer().org$apache$spark$deploy$master$Master$$masterUrl(), exec.appId(), exec.execId()));
                        }

                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.worker$3 = worker$3;
                        }
                     }
                  });
                  driverIds.foreach(new Serializable(this, worker) {
                     public static final long serialVersionUID = 0L;
                     private final WorkerInfo worker$3;

                     public final void apply(String driverId) {
                        boolean driverMatches = this.worker$3.drivers().exists(new Serializable(this, driverId) {
                           public static final long serialVersionUID = 0L;
                           private final String driverId$5;

                           public final boolean apply(Tuple2<String, DriverInfo> x0$2) {
                              if (x0$2 == null) {
                                 throw new MatchError(x0$2);
                              } else {
                                 boolean var10000;
                                 label20: {
                                    label19: {
                                       String id = (String)x0$2._1();
                                       String var5 = this.driverId$5;
                                       if (id == null) {
                                          if (var5 == null) {
                                             break label19;
                                          }
                                       } else if (id.equals(var5)) {
                                          break label19;
                                       }

                                       var10000 = false;
                                       break label20;
                                    }

                                    var10000 = true;
                                 }

                                 boolean var4 = var10000;
                                 return var4;
                              }
                           }

                           public {
                              this.driverId$5 = driverId$5;
                           }
                        });
                        if (!driverMatches) {
                           this.worker$3.endpoint().send(new DeployMessages.KillDriver(driverId));
                        }

                     }

                     public {
                        this.worker$3 = worker$3;
                     }
                  });
                  BoxedUnit var90 = BoxedUnit.UNIT;
               } else {
                  if (!None$.MODULE$.equals(var87)) {
                     throw new MatchError(var87);
                  }

                  this.$outer.logWarning(new Serializable(this, workerId) {
                     public static final long serialVersionUID = 0L;
                     private final String workerId$3;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Worker state from unknown worker: ").append((Object)this.workerId$3).toString();
                     }

                     public {
                        this.workerId$3 = workerId$3;
                     }
                  });
                  BoxedUnit var96 = BoxedUnit.UNIT;
               }

               var4 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.UnregisterApplication) {
               DeployMessages.UnregisterApplication var91 = (DeployMessages.UnregisterApplication)x1;
               String applicationId = var91.appId();
               this.$outer.logInfo(new Serializable(this, applicationId) {
                  public static final long serialVersionUID = 0L;
                  private final String applicationId$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Received unregister request from application ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.applicationId$1}));
                  }

                  public {
                     this.applicationId$1 = applicationId$1;
                  }
               });
               this.$outer.idToApp().get(applicationId).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(ApplicationInfo app) {
                     this.$outer.org$apache$spark$deploy$master$Master$$anonfun$$$outer().org$apache$spark$deploy$master$Master$$finishApplication(app);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               var4 = BoxedUnit.UNIT;
            } else if (MasterMessages.CheckForWorkerTimeOut$.MODULE$.equals(x1)) {
               this.$outer.org$apache$spark$deploy$master$Master$$timeOutDeadWorkers();
               var4 = BoxedUnit.UNIT;
            } else {
               var4 = var2.apply(x1);
            }

            return (B1)var4;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (MasterMessages.ElectedLeader$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (MasterMessages.CompleteRecovery$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (MasterMessages.RevokedLeadership$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.RegisterWorker) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.RegisterApplication) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.ExecutorStateChanged) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.DriverStateChanged) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.Heartbeat) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.MasterChangeAcknowledged) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.WorkerSchedulerStateResponse) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.WorkerLatestState) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.UnregisterApplication) {
               var3 = true;
            } else if (MasterMessages.CheckForWorkerTimeOut$.MODULE$.equals(x1)) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }

         // $FF: synthetic method
         public Master org$apache$spark$deploy$master$Master$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
   }

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return new Serializable(this, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;
         private final RpcCallContext context$1;

         public final <A1, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
            Object var6;
            if (x2 instanceof DeployMessages.RequestSubmitDriver) {
               BoxedUnit var38;
               label89: {
                  DriverDescription description;
                  label88: {
                     DeployMessages.RequestSubmitDriver var4 = (DeployMessages.RequestSubmitDriver)x2;
                     description = var4.driverDescription();
                     Enumeration.Value var10000 = this.$outer.org$apache$spark$deploy$master$Master$$state();
                     Enumeration.Value var7 = RecoveryState$.MODULE$.ALIVE();
                     if (var10000 == null) {
                        if (var7 == null) {
                           break label88;
                        }
                     } else if (var10000.equals(var7)) {
                        break label88;
                     }

                     String msg = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.BACKUP_STANDALONE_MASTER_PREFIX(), this.$outer.org$apache$spark$deploy$master$Master$$state()}))).append((Object)"Can only accept driver submissions in ALIVE state.").toString();
                     this.context$1.reply(new DeployMessages.SubmitDriverResponse(this.$outer.self(), false, None$.MODULE$, msg));
                     var38 = BoxedUnit.UNIT;
                     break label89;
                  }

                  this.$outer.logInfo(new Serializable(this, description) {
                     public static final long serialVersionUID = 0L;
                     private final DriverDescription description$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Driver submitted ").append((Object)this.description$2.command().mainClass()).toString();
                     }

                     public {
                        this.description$2 = description$2;
                     }
                  });
                  DriverInfo driver = this.$outer.org$apache$spark$deploy$master$Master$$createDriver(description);
                  this.$outer.org$apache$spark$deploy$master$Master$$persistenceEngine().addDriver(driver);
                  this.$outer.org$apache$spark$deploy$master$Master$$waitingDrivers().$plus$eq(driver);
                  this.$outer.org$apache$spark$deploy$master$Master$$drivers().add(driver);
                  this.$outer.org$apache$spark$deploy$master$Master$$schedule();
                  this.context$1.reply(new DeployMessages.SubmitDriverResponse(this.$outer.self(), true, new Some(driver.id()), (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver successfully submitted as ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{driver.id()}))));
                  var38 = BoxedUnit.UNIT;
               }

               var6 = var38;
            } else if (x2 instanceof DeployMessages.RequestKillDriver) {
               BoxedUnit var40;
               label80: {
                  String driverId;
                  label93: {
                     DeployMessages.RequestKillDriver var10 = (DeployMessages.RequestKillDriver)x2;
                     driverId = var10.driverId();
                     Enumeration.Value var39 = this.$outer.org$apache$spark$deploy$master$Master$$state();
                     Enumeration.Value var12 = RecoveryState$.MODULE$.ALIVE();
                     if (var39 == null) {
                        if (var12 != null) {
                           break label93;
                        }
                     } else if (!var39.equals(var12)) {
                        break label93;
                     }

                     this.$outer.logInfo(new Serializable(this, driverId) {
                        public static final long serialVersionUID = 0L;
                        private final String driverId$2;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Asked to kill driver ").append((Object)this.driverId$2).toString();
                        }

                        public {
                           this.driverId$2 = driverId$2;
                        }
                     });
                     Option driver = this.$outer.org$apache$spark$deploy$master$Master$$drivers().find(new Serializable(this, driverId) {
                        public static final long serialVersionUID = 0L;
                        private final String driverId$2;

                        public final boolean apply(DriverInfo x$8) {
                           boolean var3;
                           label23: {
                              String var10000 = x$8.id();
                              String var2 = this.driverId$2;
                              if (var10000 == null) {
                                 if (var2 == null) {
                                    break label23;
                                 }
                              } else if (var10000.equals(var2)) {
                                 break label23;
                              }

                              var3 = false;
                              return var3;
                           }

                           var3 = true;
                           return var3;
                        }

                        public {
                           this.driverId$2 = driverId$2;
                        }
                     });
                     if (driver instanceof Some) {
                        Some var16 = (Some)driver;
                        DriverInfo d = (DriverInfo)var16.x();
                        if (this.$outer.org$apache$spark$deploy$master$Master$$waitingDrivers().contains(d)) {
                           this.$outer.org$apache$spark$deploy$master$Master$$waitingDrivers().$minus$eq(d);
                           this.$outer.self().send(new DeployMessages.DriverStateChanged(driverId, DriverState$.MODULE$.KILLED(), None$.MODULE$));
                        } else {
                           d.worker().foreach(new Serializable(this, driverId) {
                              public static final long serialVersionUID = 0L;
                              private final String driverId$2;

                              public final void apply(WorkerInfo w) {
                                 w.endpoint().send(new DeployMessages.KillDriver(this.driverId$2));
                              }

                              public {
                                 this.driverId$2 = driverId$2;
                              }
                           });
                        }

                        String msg = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Kill request for ", " submitted"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{driverId}));
                        this.$outer.logInfo(new Serializable(this, msg) {
                           public static final long serialVersionUID = 0L;
                           private final String msg$1;

                           public final String apply() {
                              return this.msg$1;
                           }

                           public {
                              this.msg$1 = msg$1;
                           }
                        });
                        this.context$1.reply(new DeployMessages.KillDriverResponse(this.$outer.self(), driverId, true, msg));
                        BoxedUnit var18 = BoxedUnit.UNIT;
                     } else {
                        if (!None$.MODULE$.equals(driver)) {
                           throw new MatchError(driver);
                        }

                        String msg = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver ", " has already finished or does not exist"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{driverId}));
                        this.$outer.logWarning(new Serializable(this, msg) {
                           public static final long serialVersionUID = 0L;
                           private final String msg$2;

                           public final String apply() {
                              return this.msg$2;
                           }

                           public {
                              this.msg$2 = msg$2;
                           }
                        });
                        this.context$1.reply(new DeployMessages.KillDriverResponse(this.$outer.self(), driverId, false, msg));
                        BoxedUnit var36 = BoxedUnit.UNIT;
                     }

                     var40 = BoxedUnit.UNIT;
                     break label80;
                  }

                  String msg = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.BACKUP_STANDALONE_MASTER_PREFIX(), this.$outer.org$apache$spark$deploy$master$Master$$state()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can only kill drivers in ALIVE state."})))).s(Nil$.MODULE$)).toString();
                  this.context$1.reply(new DeployMessages.KillDriverResponse(this.$outer.self(), driverId, false, msg));
                  var40 = BoxedUnit.UNIT;
               }

               var6 = var40;
            } else if (x2 instanceof DeployMessages.RequestDriverStatus) {
               BoxedUnit var42;
               label71: {
                  label94: {
                     DeployMessages.RequestDriverStatus var21 = (DeployMessages.RequestDriverStatus)x2;
                     String driverId = var21.driverId();
                     Enumeration.Value var41 = this.$outer.org$apache$spark$deploy$master$Master$$state();
                     Enumeration.Value var23 = RecoveryState$.MODULE$.ALIVE();
                     if (var41 == null) {
                        if (var23 != null) {
                           break label94;
                        }
                     } else if (!var41.equals(var23)) {
                        break label94;
                     }

                     Option var25 = this.$outer.org$apache$spark$deploy$master$Master$$drivers().$plus$plus(this.$outer.org$apache$spark$deploy$master$Master$$completedDrivers()).find(new Serializable(this, driverId) {
                        public static final long serialVersionUID = 0L;
                        private final String driverId$3;

                        public final boolean apply(DriverInfo x$9) {
                           boolean var3;
                           label23: {
                              String var10000 = x$9.id();
                              String var2 = this.driverId$3;
                              if (var10000 == null) {
                                 if (var2 == null) {
                                    break label23;
                                 }
                              } else if (var10000.equals(var2)) {
                                 break label23;
                              }

                              var3 = false;
                              return var3;
                           }

                           var3 = true;
                           return var3;
                        }

                        public {
                           this.driverId$3 = driverId$3;
                        }
                     });
                     if (var25 instanceof Some) {
                        Some var26 = (Some)var25;
                        DriverInfo driver = (DriverInfo)var26.x();
                        this.context$1.reply(new DeployMessages.DriverStatusResponse(true, new Some(driver.state()), driver.worker().map(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final String apply(WorkerInfo x$10) {
                              return x$10.id();
                           }
                        }), driver.worker().map(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final String apply(WorkerInfo x$11) {
                              return x$11.hostPort();
                           }
                        }), driver.exception()));
                        BoxedUnit var28 = BoxedUnit.UNIT;
                     } else {
                        if (!None$.MODULE$.equals(var25)) {
                           throw new MatchError(var25);
                        }

                        this.context$1.reply(new DeployMessages.DriverStatusResponse(false, None$.MODULE$, None$.MODULE$, None$.MODULE$, None$.MODULE$));
                        BoxedUnit var37 = BoxedUnit.UNIT;
                     }

                     var42 = BoxedUnit.UNIT;
                     break label71;
                  }

                  String msg = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ": ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.BACKUP_STANDALONE_MASTER_PREFIX(), this.$outer.org$apache$spark$deploy$master$Master$$state()}))).append((Object)"Can only request driver status in ALIVE state.").toString();
                  this.context$1.reply(new DeployMessages.DriverStatusResponse(false, None$.MODULE$, None$.MODULE$, None$.MODULE$, new Some(new Exception(msg))));
                  var42 = BoxedUnit.UNIT;
               }

               var6 = var42;
            } else if (DeployMessages.RequestMasterState$.MODULE$.equals(x2)) {
               this.context$1.reply(new DeployMessages.MasterStateResponse(this.$outer.org$apache$spark$deploy$master$Master$$address.host(), this.$outer.org$apache$spark$deploy$master$Master$$address.port(), this.$outer.org$apache$spark$deploy$master$Master$$restServerBoundPort(), (WorkerInfo[])this.$outer.workers().toArray(ClassTag$.MODULE$.apply(WorkerInfo.class)), (ApplicationInfo[])this.$outer.apps().toArray(ClassTag$.MODULE$.apply(ApplicationInfo.class)), (ApplicationInfo[])this.$outer.org$apache$spark$deploy$master$Master$$completedApps().toArray(ClassTag$.MODULE$.apply(ApplicationInfo.class)), (DriverInfo[])this.$outer.org$apache$spark$deploy$master$Master$$drivers().toArray(ClassTag$.MODULE$.apply(DriverInfo.class)), (DriverInfo[])this.$outer.org$apache$spark$deploy$master$Master$$completedDrivers().toArray(ClassTag$.MODULE$.apply(DriverInfo.class)), this.$outer.org$apache$spark$deploy$master$Master$$state()));
               var6 = BoxedUnit.UNIT;
            } else if (MasterMessages.BoundPortsRequest$.MODULE$.equals(x2)) {
               this.context$1.reply(new MasterMessages.BoundPortsResponse(this.$outer.org$apache$spark$deploy$master$Master$$address.port(), this.$outer.org$apache$spark$deploy$master$Master$$webUi().boundPort(), this.$outer.org$apache$spark$deploy$master$Master$$restServerBoundPort()));
               var6 = BoxedUnit.UNIT;
            } else if (x2 instanceof DeployMessages.RequestExecutors) {
               DeployMessages.RequestExecutors var29 = (DeployMessages.RequestExecutors)x2;
               String appId = var29.appId();
               int requestedTotal = var29.requestedTotal();
               this.context$1.reply(BoxesRunTime.boxToBoolean(this.$outer.org$apache$spark$deploy$master$Master$$handleRequestExecutors(appId, requestedTotal)));
               var6 = BoxedUnit.UNIT;
            } else if (x2 instanceof DeployMessages.KillExecutors) {
               DeployMessages.KillExecutors var32 = (DeployMessages.KillExecutors)x2;
               String appId = var32.appId();
               Seq executorIds = var32.executorIds();
               Seq formattedExecutorIds = this.$outer.org$apache$spark$deploy$master$Master$$formatExecutorIds(executorIds);
               this.context$1.reply(BoxesRunTime.boxToBoolean(this.$outer.org$apache$spark$deploy$master$Master$$handleKillExecutors(appId, formattedExecutorIds)));
               var6 = BoxedUnit.UNIT;
            } else {
               var6 = var2.apply(x2);
            }

            return (B1)var6;
         }

         public final boolean isDefinedAt(Object x2) {
            boolean var3;
            if (x2 instanceof DeployMessages.RequestSubmitDriver) {
               var3 = true;
            } else if (x2 instanceof DeployMessages.RequestKillDriver) {
               var3 = true;
            } else if (x2 instanceof DeployMessages.RequestDriverStatus) {
               var3 = true;
            } else if (DeployMessages.RequestMasterState$.MODULE$.equals(x2)) {
               var3 = true;
            } else if (MasterMessages.BoundPortsRequest$.MODULE$.equals(x2)) {
               var3 = true;
            } else if (x2 instanceof DeployMessages.RequestExecutors) {
               var3 = true;
            } else if (x2 instanceof DeployMessages.KillExecutors) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.context$1 = context$1;
            }
         }
      };
   }

   public void onDisconnected(RpcAddress address) {
      this.logInfo(new Serializable(this, address) {
         public static final long serialVersionUID = 0L;
         private final RpcAddress address$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " got disassociated, removing it."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.address$1}));
         }

         public {
            this.address$1 = address$1;
         }
      });
      this.addressToWorker().get(address).foreach(new Serializable(this, address) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;
         private final RpcAddress address$1;

         public final void apply(WorkerInfo x$12) {
            this.$outer.org$apache$spark$deploy$master$Master$$removeWorker(x$12, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " got disassociated"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.address$1})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.address$1 = address$1;
            }
         }
      });
      this.addressToApp().get(address).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final void apply(ApplicationInfo app) {
            this.$outer.org$apache$spark$deploy$master$Master$$finishApplication(app);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Enumeration.Value var10000 = this.org$apache$spark$deploy$master$Master$$state();
      Enumeration.Value var2 = RecoveryState$.MODULE$.RECOVERING();
      if (var10000 == null) {
         if (var2 != null) {
            return;
         }
      } else if (!var10000.equals(var2)) {
         return;
      }

      if (this.org$apache$spark$deploy$master$Master$$canCompleteRecovery()) {
         this.org$apache$spark$deploy$master$Master$$completeRecovery();
      }

   }

   public boolean org$apache$spark$deploy$master$Master$$canCompleteRecovery() {
      return this.workers().count(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(WorkerInfo x$13) {
            boolean var3;
            label23: {
               Enumeration.Value var10000 = x$13.state();
               Enumeration.Value var2 = WorkerState$.MODULE$.UNKNOWN();
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }
      }) == 0 && this.apps().count(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ApplicationInfo x$14) {
            boolean var3;
            label23: {
               Enumeration.Value var10000 = x$14.state();
               Enumeration.Value var2 = ApplicationState$.MODULE$.UNKNOWN();
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }
      }) == 0;
   }

   public void org$apache$spark$deploy$master$Master$$beginRecovery(Seq<ApplicationInfo> storedApps, Seq<DriverInfo> storedDrivers, Seq<WorkerInfo> storedWorkers) {
      storedApps.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final void apply(ApplicationInfo app) {
            this.$outer.logInfo(new Serializable(this, app) {
               public static final long serialVersionUID = 0L;
               private final ApplicationInfo app$5;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Trying to recover app: ").append((Object)this.app$5.id()).toString();
               }

               public {
                  this.app$5 = app$5;
               }
            });

            try {
               this.$outer.org$apache$spark$deploy$master$Master$$registerApplication(app);
               app.state_$eq(ApplicationState$.MODULE$.UNKNOWN());
               app.driver().send(new DeployMessages.MasterChanged(this.$outer.self(), this.$outer.org$apache$spark$deploy$master$Master$$masterWebUiUrl()));
            } catch (Exception var3) {
               this.$outer.logInfo(new Serializable(this, app) {
                  public static final long serialVersionUID = 0L;
                  private final ApplicationInfo app$5;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"App ").append((Object)this.app$5.id()).append((Object)" had exception on reconnect").toString();
                  }

                  public {
                     this.app$5 = app$5;
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
      });
      storedDrivers.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final HashSet<DriverInfo> apply(DriverInfo driver) {
            return this.$outer.org$apache$spark$deploy$master$Master$$drivers().$plus$eq(driver);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      storedWorkers.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final void apply(WorkerInfo worker) {
            this.$outer.logInfo(new Serializable(this, worker) {
               public static final long serialVersionUID = 0L;
               private final WorkerInfo worker$8;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Trying to recover worker: ").append((Object)this.worker$8.id()).toString();
               }

               public {
                  this.worker$8 = worker$8;
               }
            });

            try {
               this.$outer.org$apache$spark$deploy$master$Master$$registerWorker(worker);
               worker.state_$eq(WorkerState$.MODULE$.UNKNOWN());
               worker.endpoint().send(new DeployMessages.MasterChanged(this.$outer.self(), this.$outer.org$apache$spark$deploy$master$Master$$masterWebUiUrl()));
            } catch (Exception var3) {
               this.$outer.logInfo(new Serializable(this, worker) {
                  public static final long serialVersionUID = 0L;
                  private final WorkerInfo worker$8;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Worker ").append((Object)this.worker$8.id()).append((Object)" had exception on reconnect").toString();
                  }

                  public {
                     this.worker$8 = worker$8;
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
      });
   }

   public void org$apache$spark$deploy$master$Master$$completeRecovery() {
      label14: {
         Enumeration.Value var10000 = this.org$apache$spark$deploy$master$Master$$state();
         Enumeration.Value var1 = RecoveryState$.MODULE$.RECOVERING();
         if (var10000 == null) {
            if (var1 == null) {
               break label14;
            }
         } else if (var10000.equals(var1)) {
            break label14;
         }

         return;
      }

      this.org$apache$spark$deploy$master$Master$$state_$eq(RecoveryState$.MODULE$.COMPLETING_RECOVERY());
      ((HashSet)this.workers().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(WorkerInfo x$15) {
            boolean var3;
            label23: {
               Enumeration.Value var10000 = x$15.state();
               Enumeration.Value var2 = WorkerState$.MODULE$.UNKNOWN();
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }
      })).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final void apply(WorkerInfo x$16) {
            this.$outer.org$apache$spark$deploy$master$Master$$removeWorker(x$16, "Not responding for recovery");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      ((HashSet)this.apps().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ApplicationInfo x$17) {
            boolean var3;
            label23: {
               Enumeration.Value var10000 = x$17.state();
               Enumeration.Value var2 = ApplicationState$.MODULE$.UNKNOWN();
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }
      })).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final void apply(ApplicationInfo app) {
            this.$outer.org$apache$spark$deploy$master$Master$$finishApplication(app);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      ((HashSet)this.apps().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ApplicationInfo x$18) {
            boolean var3;
            label23: {
               Enumeration.Value var10000 = x$18.state();
               Enumeration.Value var2 = ApplicationState$.MODULE$.WAITING();
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }
      })).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(ApplicationInfo x$19) {
            x$19.state_$eq(ApplicationState$.MODULE$.RUNNING());
         }
      });
      ((HashSet)this.org$apache$spark$deploy$master$Master$$drivers().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(DriverInfo x$20) {
            return x$20.worker().isEmpty();
         }
      })).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final void apply(DriverInfo d) {
            this.$outer.logWarning(new Serializable(this, d) {
               public static final long serialVersionUID = 0L;
               private final DriverInfo d$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver ", " was not found after master recovery"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.d$1.id()}));
               }

               public {
                  this.d$1 = d$1;
               }
            });
            if (d.desc().supervise()) {
               this.$outer.logWarning(new Serializable(this, d) {
                  public static final long serialVersionUID = 0L;
                  private final DriverInfo d$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Re-launching ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.d$1.id()}));
                  }

                  public {
                     this.d$1 = d$1;
                  }
               });
               this.$outer.org$apache$spark$deploy$master$Master$$relaunchDriver(d);
            } else {
               this.$outer.org$apache$spark$deploy$master$Master$$removeDriver(d.id(), DriverState$.MODULE$.ERROR(), None$.MODULE$);
               this.$outer.logWarning(new Serializable(this, d) {
                  public static final long serialVersionUID = 0L;
                  private final DriverInfo d$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Did not re-launch ", " because it was not supervised"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.d$1.id()}));
                  }

                  public {
                     this.d$1 = d$1;
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
      });
      this.org$apache$spark$deploy$master$Master$$state_$eq(RecoveryState$.MODULE$.ALIVE());
      this.org$apache$spark$deploy$master$Master$$schedule();
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Recovery complete - resuming operations!";
         }
      });
   }

   public int[] org$apache$spark$deploy$master$Master$$scheduleExecutorsOnWorkers(ApplicationInfo app, WorkerInfo[] usableWorkers, boolean spreadOutApps) {
      Option coresPerExecutor = app.desc().coresPerExecutor();
      int minCoresPerExecutor = BoxesRunTime.unboxToInt(coresPerExecutor.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 1;
         }
      }));
      boolean oneExecutorPerWorker = coresPerExecutor.isEmpty();
      int memoryPerExecutor = app.desc().memoryPerExecutorMB();
      int numUsable = usableWorkers.length;
      int[] assignedCores = new int[numUsable];
      int[] assignedExecutors = new int[numUsable];
      IntRef coresToAssign = IntRef.create(scala.math.package$.MODULE$.min(app.coresLeft(), BoxesRunTime.unboxToInt(Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(usableWorkers).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerInfo x$21) {
            return x$21.coresFree();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).sum(Numeric.IntIsIntegral$.MODULE$))));

      for(IndexedSeq freeWorkers = (IndexedSeq)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), numUsable).filter(new Serializable(this, app, usableWorkers, minCoresPerExecutor, oneExecutorPerWorker, memoryPerExecutor, assignedCores, assignedExecutors, coresToAssign) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;
         private final ApplicationInfo app$3;
         private final WorkerInfo[] usableWorkers$1;
         private final int minCoresPerExecutor$1;
         private final boolean oneExecutorPerWorker$1;
         private final int memoryPerExecutor$1;
         private final int[] assignedCores$1;
         private final int[] assignedExecutors$1;
         private final IntRef coresToAssign$1;

         public final boolean apply(int pos) {
            return this.apply$mcZI$sp(pos);
         }

         public boolean apply$mcZI$sp(int pos) {
            return this.$outer.org$apache$spark$deploy$master$Master$$canLaunchExecutor$1(pos, this.app$3, this.usableWorkers$1, this.minCoresPerExecutor$1, this.oneExecutorPerWorker$1, this.memoryPerExecutor$1, this.assignedCores$1, this.assignedExecutors$1, this.coresToAssign$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.app$3 = app$3;
               this.usableWorkers$1 = usableWorkers$1;
               this.minCoresPerExecutor$1 = minCoresPerExecutor$1;
               this.oneExecutorPerWorker$1 = oneExecutorPerWorker$1;
               this.memoryPerExecutor$1 = memoryPerExecutor$1;
               this.assignedCores$1 = assignedCores$1;
               this.assignedExecutors$1 = assignedExecutors$1;
               this.coresToAssign$1 = coresToAssign$1;
            }
         }
      }); freeWorkers.nonEmpty(); freeWorkers = (IndexedSeq)freeWorkers.filter(new Serializable(this, app, usableWorkers, minCoresPerExecutor, oneExecutorPerWorker, memoryPerExecutor, assignedCores, assignedExecutors, coresToAssign) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;
         private final ApplicationInfo app$3;
         private final WorkerInfo[] usableWorkers$1;
         private final int minCoresPerExecutor$1;
         private final boolean oneExecutorPerWorker$1;
         private final int memoryPerExecutor$1;
         private final int[] assignedCores$1;
         private final int[] assignedExecutors$1;
         private final IntRef coresToAssign$1;

         public final boolean apply(int pos) {
            return this.apply$mcZI$sp(pos);
         }

         public boolean apply$mcZI$sp(int pos) {
            return this.$outer.org$apache$spark$deploy$master$Master$$canLaunchExecutor$1(pos, this.app$3, this.usableWorkers$1, this.minCoresPerExecutor$1, this.oneExecutorPerWorker$1, this.memoryPerExecutor$1, this.assignedCores$1, this.assignedExecutors$1, this.coresToAssign$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.app$3 = app$3;
               this.usableWorkers$1 = usableWorkers$1;
               this.minCoresPerExecutor$1 = minCoresPerExecutor$1;
               this.oneExecutorPerWorker$1 = oneExecutorPerWorker$1;
               this.memoryPerExecutor$1 = memoryPerExecutor$1;
               this.assignedCores$1 = assignedCores$1;
               this.assignedExecutors$1 = assignedExecutors$1;
               this.coresToAssign$1 = coresToAssign$1;
            }
         }
      })) {
         freeWorkers.foreach(new Serializable(this, app, usableWorkers, spreadOutApps, minCoresPerExecutor, oneExecutorPerWorker, memoryPerExecutor, assignedCores, assignedExecutors, coresToAssign) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Master $outer;
            private final ApplicationInfo app$3;
            private final WorkerInfo[] usableWorkers$1;
            private final boolean spreadOutApps$1;
            private final int minCoresPerExecutor$1;
            private final boolean oneExecutorPerWorker$1;
            private final int memoryPerExecutor$1;
            private final int[] assignedCores$1;
            private final int[] assignedExecutors$1;
            private final IntRef coresToAssign$1;

            public final void apply(int pos) {
               this.apply$mcVI$sp(pos);
            }

            public void apply$mcVI$sp(int pos) {
               boolean keepScheduling = true;

               while(keepScheduling && this.$outer.org$apache$spark$deploy$master$Master$$canLaunchExecutor$1(pos, this.app$3, this.usableWorkers$1, this.minCoresPerExecutor$1, this.oneExecutorPerWorker$1, this.memoryPerExecutor$1, this.assignedCores$1, this.assignedExecutors$1, this.coresToAssign$1)) {
                  this.coresToAssign$1.elem -= this.minCoresPerExecutor$1;
                  this.assignedCores$1[pos] += this.minCoresPerExecutor$1;
                  if (this.oneExecutorPerWorker$1) {
                     this.assignedExecutors$1[pos] = 1;
                  } else {
                     int var10002 = this.assignedExecutors$1[pos]++;
                  }

                  if (this.spreadOutApps$1) {
                     keepScheduling = false;
                  }
               }

            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.app$3 = app$3;
                  this.usableWorkers$1 = usableWorkers$1;
                  this.spreadOutApps$1 = spreadOutApps$1;
                  this.minCoresPerExecutor$1 = minCoresPerExecutor$1;
                  this.oneExecutorPerWorker$1 = oneExecutorPerWorker$1;
                  this.memoryPerExecutor$1 = memoryPerExecutor$1;
                  this.assignedCores$1 = assignedCores$1;
                  this.assignedExecutors$1 = assignedExecutors$1;
                  this.coresToAssign$1 = coresToAssign$1;
               }
            }
         });
      }

      return assignedCores;
   }

   private void startExecutorsOnWorkers() {
      this.waitingApps().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final void apply(ApplicationInfo app) {
            int coresPerExecutor = BoxesRunTime.unboxToInt(app.desc().coresPerExecutor().getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply() {
                  return this.apply$mcI$sp();
               }

               public int apply$mcI$sp() {
                  return 1;
               }
            }));
            if (app.coresLeft() >= coresPerExecutor) {
               WorkerInfo[] usableWorkers = (WorkerInfo[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(this.$outer.workers().toArray(ClassTag$.MODULE$.apply(WorkerInfo.class))).filter(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final boolean apply(WorkerInfo x$22) {
                     boolean var3;
                     label23: {
                        Enumeration.Value var10000 = x$22.state();
                        Enumeration.Value var2 = WorkerState$.MODULE$.ALIVE();
                        if (var10000 == null) {
                           if (var2 == null) {
                              break label23;
                           }
                        } else if (var10000.equals(var2)) {
                           break label23;
                        }

                        var3 = false;
                        return var3;
                     }

                     var3 = true;
                     return var3;
                  }
               })).filter(new Serializable(this, coresPerExecutor, app) {
                  public static final long serialVersionUID = 0L;
                  private final int coresPerExecutor$1;
                  private final ApplicationInfo app$6;

                  public final boolean apply(WorkerInfo worker) {
                     return worker.memoryFree() >= this.app$6.desc().memoryPerExecutorMB() && worker.coresFree() >= this.coresPerExecutor$1;
                  }

                  public {
                     this.coresPerExecutor$1 = coresPerExecutor$1;
                     this.app$6 = app$6;
                  }
               })).sortBy(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final int apply(WorkerInfo x$23) {
                     return x$23.coresFree();
                  }
               }, Ordering.Int$.MODULE$)).reverse();
               int[] assignedCores = this.$outer.org$apache$spark$deploy$master$Master$$scheduleExecutorsOnWorkers(app, usableWorkers, this.$outer.org$apache$spark$deploy$master$Master$$spreadOutApps());
               RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), usableWorkers.length).withFilter(new Serializable(this, assignedCores) {
                  public static final long serialVersionUID = 0L;
                  private final int[] assignedCores$2;

                  public final boolean apply(int pos) {
                     return this.apply$mcZI$sp(pos);
                  }

                  public boolean apply$mcZI$sp(int pos) {
                     return this.assignedCores$2[pos] > 0;
                  }

                  public {
                     this.assignedCores$2 = assignedCores$2;
                  }
               }).foreach(new Serializable(this, usableWorkers, assignedCores, app) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final WorkerInfo[] usableWorkers$2;
                  private final int[] assignedCores$2;
                  private final ApplicationInfo app$6;

                  public final void apply(int pos) {
                     this.apply$mcVI$sp(pos);
                  }

                  public void apply$mcVI$sp(int pos) {
                     this.$outer.org$apache$spark$deploy$master$Master$$anonfun$$$outer().org$apache$spark$deploy$master$Master$$allocateWorkerResourceToExecutors(this.app$6, this.assignedCores$2[pos], this.app$6.desc().coresPerExecutor(), this.usableWorkers$2[pos]);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.usableWorkers$2 = usableWorkers$2;
                        this.assignedCores$2 = assignedCores$2;
                        this.app$6 = app$6;
                     }
                  }
               });
            }

         }

         // $FF: synthetic method
         public Master org$apache$spark$deploy$master$Master$$anonfun$$$outer() {
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

   public void org$apache$spark$deploy$master$Master$$allocateWorkerResourceToExecutors(ApplicationInfo app, int assignedCores, Option<Object> coresPerExecutor, WorkerInfo worker) {
      int numExecutors = BoxesRunTime.unboxToInt(coresPerExecutor.map(new Serializable(this, assignedCores) {
         public static final long serialVersionUID = 0L;
         private final int assignedCores$3;

         public final int apply(int x$24) {
            return this.apply$mcII$sp(x$24);
         }

         public int apply$mcII$sp(int x$24) {
            return this.assignedCores$3 / x$24;
         }

         public {
            this.assignedCores$3 = assignedCores$3;
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 1;
         }
      }));
      int coresToAssign = BoxesRunTime.unboxToInt(coresPerExecutor.getOrElse(new Serializable(this, assignedCores) {
         public static final long serialVersionUID = 0L;
         private final int assignedCores$3;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return this.assignedCores$3;
         }

         public {
            this.assignedCores$3 = assignedCores$3;
         }
      }));
      RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), numExecutors).foreach$mVc$sp(new Serializable(this, app, worker, coresToAssign) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;
         private final ApplicationInfo app$4;
         private final WorkerInfo worker$5;
         private final int coresToAssign$2;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            ExecutorDesc exec = this.app$4.addExecutor(this.worker$5, this.coresToAssign$2, this.app$4.addExecutor$default$3());
            this.$outer.org$apache$spark$deploy$master$Master$$launchExecutor(this.worker$5, exec);
            this.app$4.state_$eq(ApplicationState$.MODULE$.RUNNING());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.app$4 = app$4;
               this.worker$5 = worker$5;
               this.coresToAssign$2 = coresToAssign$2;
            }
         }
      });
   }

   public void org$apache$spark$deploy$master$Master$$schedule() {
      label14: {
         Enumeration.Value var10000 = this.org$apache$spark$deploy$master$Master$$state();
         Enumeration.Value var1 = RecoveryState$.MODULE$.ALIVE();
         if (var10000 == null) {
            if (var1 == null) {
               break label14;
            }
         } else if (var10000.equals(var1)) {
            break label14;
         }

         return;
      }

      Seq shuffledAliveWorkers = (Seq)Random$.MODULE$.shuffle((TraversableOnce)this.workers().toSeq().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(WorkerInfo x$25) {
            boolean var3;
            label23: {
               Enumeration.Value var10000 = x$25.state();
               Enumeration.Value var2 = WorkerState$.MODULE$.ALIVE();
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }
      }), Seq$.MODULE$.canBuildFrom());
      int numWorkersAlive = shuffledAliveWorkers.size();
      IntRef curPos = IntRef.create(0);
      this.org$apache$spark$deploy$master$Master$$waitingDrivers().toList().foreach(new Serializable(this, shuffledAliveWorkers, numWorkersAlive, curPos) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;
         private final Seq shuffledAliveWorkers$1;
         private final int numWorkersAlive$1;
         private final IntRef curPos$1;

         public final void apply(DriverInfo driver) {
            boolean launched = false;

            for(int numWorkersVisited = 0; numWorkersVisited < this.numWorkersAlive$1 && !launched; this.curPos$1.elem = (this.curPos$1.elem + 1) % this.numWorkersAlive$1) {
               WorkerInfo worker = (WorkerInfo)this.shuffledAliveWorkers$1.apply(this.curPos$1.elem);
               ++numWorkersVisited;
               if (worker.memoryFree() >= driver.desc().mem() && worker.coresFree() >= driver.desc().cores()) {
                  this.$outer.org$apache$spark$deploy$master$Master$$launchDriver(worker, driver);
                  this.$outer.org$apache$spark$deploy$master$Master$$waitingDrivers().$minus$eq(driver);
                  launched = true;
               }
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.shuffledAliveWorkers$1 = shuffledAliveWorkers$1;
               this.numWorkersAlive$1 = numWorkersAlive$1;
               this.curPos$1 = curPos$1;
            }
         }
      });
      this.startExecutorsOnWorkers();
   }

   public void org$apache$spark$deploy$master$Master$$launchExecutor(WorkerInfo worker, ExecutorDesc exec) {
      this.logInfo(new Serializable(this, worker, exec) {
         public static final long serialVersionUID = 0L;
         private final WorkerInfo worker$6;
         private final ExecutorDesc exec$2;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Launching executor ").append((Object)this.exec$2.fullId()).append((Object)" on worker ").append((Object)this.worker$6.id()).toString();
         }

         public {
            this.worker$6 = worker$6;
            this.exec$2 = exec$2;
         }
      });
      worker.addExecutor(exec);
      worker.endpoint().send(new DeployMessages.LaunchExecutor(this.org$apache$spark$deploy$master$Master$$masterUrl(), exec.application().id(), exec.id(), exec.application().desc(), exec.cores(), exec.memory()));
      exec.application().driver().send(new DeployMessages.ExecutorAdded(exec.id(), worker.id(), worker.hostPort(), exec.cores(), exec.memory()));
   }

   public boolean org$apache$spark$deploy$master$Master$$registerWorker(WorkerInfo worker) {
      RpcAddress workerAddress;
      label18: {
         ((HashSet)this.workers().filter(new Serializable(this, worker) {
            public static final long serialVersionUID = 0L;
            private final WorkerInfo worker$1;

            public final boolean apply(WorkerInfo w) {
               boolean var5;
               label34: {
                  label26: {
                     String var10000 = w.host();
                     String var2 = this.worker$1.host();
                     if (var10000 == null) {
                        if (var2 != null) {
                           break label26;
                        }
                     } else if (!var10000.equals(var2)) {
                        break label26;
                     }

                     if (w.port() == this.worker$1.port()) {
                        Enumeration.Value var4 = w.state();
                        Enumeration.Value var3 = WorkerState$.MODULE$.DEAD();
                        if (var4 == null) {
                           if (var3 == null) {
                              break label34;
                           }
                        } else if (var4.equals(var3)) {
                           break label34;
                        }
                     }
                  }

                  var5 = false;
                  return var5;
               }

               var5 = true;
               return var5;
            }

            public {
               this.worker$1 = worker$1;
            }
         })).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Master $outer;

            public final HashSet<WorkerInfo> apply(WorkerInfo w) {
               return this.$outer.workers().$minus$eq(w);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         workerAddress = worker.endpoint().address();
         if (this.addressToWorker().contains(workerAddress)) {
            WorkerInfo oldWorker = (WorkerInfo)this.addressToWorker().apply(workerAddress);
            Enumeration.Value var10000 = oldWorker.state();
            Enumeration.Value var4 = WorkerState$.MODULE$.UNKNOWN();
            if (var10000 == null) {
               if (var4 != null) {
                  break label18;
               }
            } else if (!var10000.equals(var4)) {
               break label18;
            }

            this.org$apache$spark$deploy$master$Master$$removeWorker(oldWorker, "Worker replaced by a new worker with same address");
         }

         this.workers().$plus$eq(worker);
         this.org$apache$spark$deploy$master$Master$$idToWorker().update(worker.id(), worker);
         this.addressToWorker().update(workerAddress, worker);
         return true;
      }

      this.logInfo(new Serializable(this, workerAddress) {
         public static final long serialVersionUID = 0L;
         private final RpcAddress workerAddress$2;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Attempted to re-register worker at same address: ").append((Object)this.workerAddress$2).toString();
         }

         public {
            this.workerAddress$2 = workerAddress$2;
         }
      });
      return false;
   }

   public void org$apache$spark$deploy$master$Master$$removeWorker(WorkerInfo worker, String msg) {
      this.logInfo(new Serializable(this, worker) {
         public static final long serialVersionUID = 0L;
         private final WorkerInfo worker$4;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Removing worker ").append((Object)this.worker$4.id()).append((Object)" on ").append((Object)this.worker$4.host()).append((Object)":").append((Object)BoxesRunTime.boxToInteger(this.worker$4.port())).toString();
         }

         public {
            this.worker$4 = worker$4;
         }
      });
      worker.setState(WorkerState$.MODULE$.DEAD());
      this.org$apache$spark$deploy$master$Master$$idToWorker().$minus$eq(worker.id());
      this.addressToWorker().$minus$eq(worker.endpoint().address());
      worker.executors().values().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final void apply(ExecutorDesc exec) {
            this.$outer.logInfo(new Serializable(this, exec) {
               public static final long serialVersionUID = 0L;
               private final ExecutorDesc exec$4;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Telling app of lost executor: ").append((Object)BoxesRunTime.boxToInteger(this.exec$4.id())).toString();
               }

               public {
                  this.exec$4 = exec$4;
               }
            });
            exec.application().driver().send(new DeployMessages.ExecutorUpdated(exec.id(), ExecutorState$.MODULE$.LOST(), new Some("worker lost"), None$.MODULE$, true));
            exec.state_$eq(ExecutorState$.MODULE$.LOST());
            exec.application().removeExecutor(exec);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      worker.drivers().values().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final void apply(DriverInfo driver) {
            if (driver.desc().supervise()) {
               this.$outer.logInfo(new Serializable(this, driver) {
                  public static final long serialVersionUID = 0L;
                  private final DriverInfo driver$3;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Re-launching ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driver$3.id()}));
                  }

                  public {
                     this.driver$3 = driver$3;
                  }
               });
               this.$outer.org$apache$spark$deploy$master$Master$$relaunchDriver(driver);
            } else {
               this.$outer.logInfo(new Serializable(this, driver) {
                  public static final long serialVersionUID = 0L;
                  private final DriverInfo driver$3;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not re-launching ", " because it was not supervised"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driver$3.id()}));
                  }

                  public {
                     this.driver$3 = driver$3;
                  }
               });
               this.$outer.org$apache$spark$deploy$master$Master$$removeDriver(driver.id(), DriverState$.MODULE$.ERROR(), None$.MODULE$);
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
      this.logInfo(new Serializable(this, worker) {
         public static final long serialVersionUID = 0L;
         private final WorkerInfo worker$4;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Telling app of lost worker: "})))).s(Nil$.MODULE$)).append((Object)this.worker$4.id()).toString();
         }

         public {
            this.worker$4 = worker$4;
         }
      });
      ((HashSet)this.apps().filterNot(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final boolean apply(ApplicationInfo x$26) {
            return this.$outer.org$apache$spark$deploy$master$Master$$completedApps().contains(x$26);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      })).foreach(new Serializable(this, worker, msg) {
         public static final long serialVersionUID = 0L;
         private final WorkerInfo worker$4;
         private final String msg$3;

         public final void apply(ApplicationInfo app) {
            app.driver().send(new DeployMessages.WorkerRemoved(this.worker$4.id(), this.worker$4.host(), this.msg$3));
         }

         public {
            this.worker$4 = worker$4;
            this.msg$3 = msg$3;
         }
      });
      this.org$apache$spark$deploy$master$Master$$persistenceEngine().removeWorker(worker);
   }

   public void org$apache$spark$deploy$master$Master$$relaunchDriver(DriverInfo driver) {
      this.org$apache$spark$deploy$master$Master$$removeDriver(driver.id(), DriverState$.MODULE$.RELAUNCHING(), None$.MODULE$);
      DriverInfo newDriver = this.org$apache$spark$deploy$master$Master$$createDriver(driver.desc());
      this.org$apache$spark$deploy$master$Master$$persistenceEngine().addDriver(newDriver);
      this.org$apache$spark$deploy$master$Master$$drivers().add(newDriver);
      this.org$apache$spark$deploy$master$Master$$waitingDrivers().$plus$eq(newDriver);
      this.org$apache$spark$deploy$master$Master$$schedule();
   }

   public ApplicationInfo org$apache$spark$deploy$master$Master$$createApplication(ApplicationDescription desc, RpcEndpointRef driver) {
      long now = System.currentTimeMillis();
      Date date = new Date(now);
      String appId = this.newApplicationId(date);
      return new ApplicationInfo(now, appId, desc, date, driver, this.defaultCores());
   }

   public void org$apache$spark$deploy$master$Master$$registerApplication(ApplicationInfo app) {
      RpcAddress appAddress = app.driver().address();
      if (this.addressToApp().contains(appAddress)) {
         this.logInfo(new Serializable(this, appAddress) {
            public static final long serialVersionUID = 0L;
            private final RpcAddress appAddress$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Attempted to re-register application at same address: ").append((Object)this.appAddress$1).toString();
            }

            public {
               this.appAddress$1 = appAddress$1;
            }
         });
      } else {
         this.org$apache$spark$deploy$master$Master$$applicationMetricsSystem().registerSource(app.appSource());
         this.apps().$plus$eq(app);
         this.idToApp().update(app.id(), app);
         this.endpointToApp().update(app.driver(), app);
         this.addressToApp().update(appAddress, app);
         this.waitingApps().$plus$eq(app);
      }
   }

   public void org$apache$spark$deploy$master$Master$$finishApplication(ApplicationInfo app) {
      this.removeApplication(app, ApplicationState$.MODULE$.FINISHED());
   }

   public void removeApplication(ApplicationInfo app, Enumeration.Value state) {
      if (this.apps().contains(app)) {
         this.logInfo(new Serializable(this, app) {
            public static final long serialVersionUID = 0L;
            private final ApplicationInfo app$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Removing app ").append((Object)this.app$2.id()).toString();
            }

            public {
               this.app$2 = app$2;
            }
         });
         this.apps().$minus$eq(app);
         this.idToApp().$minus$eq(app.id());
         this.endpointToApp().$minus$eq(app.driver());
         this.addressToApp().$minus$eq(app.driver().address());
         if (this.org$apache$spark$deploy$master$Master$$completedApps().size() >= this.RETAINED_APPLICATIONS()) {
            int toRemove = scala.math.package$.MODULE$.max(this.RETAINED_APPLICATIONS() / 10, 1);
            ((ResizableArray)this.org$apache$spark$deploy$master$Master$$completedApps().take(toRemove)).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final Master $outer;

               public final void apply(ApplicationInfo a) {
                  this.$outer.org$apache$spark$deploy$master$Master$$applicationMetricsSystem().removeSource(a.appSource());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.org$apache$spark$deploy$master$Master$$completedApps().trimStart(toRemove);
         }

         label19: {
            this.org$apache$spark$deploy$master$Master$$completedApps().$plus$eq(app);
            this.waitingApps().$minus$eq(app);
            app.executors().values().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final Master $outer;

               public final void apply(ExecutorDesc exec) {
                  this.$outer.org$apache$spark$deploy$master$Master$$killExecutor(exec);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            app.markFinished(state);
            Enumeration.Value var4 = ApplicationState$.MODULE$.FINISHED();
            if (state == null) {
               if (var4 == null) {
                  break label19;
               }
            } else if (state.equals(var4)) {
               break label19;
            }

            app.driver().send(new DeployMessages.ApplicationRemoved(state.toString()));
         }

         this.org$apache$spark$deploy$master$Master$$persistenceEngine().removeApplication(app);
         this.org$apache$spark$deploy$master$Master$$schedule();
         this.workers().foreach(new Serializable(this, app) {
            public static final long serialVersionUID = 0L;
            private final ApplicationInfo app$2;

            public final void apply(WorkerInfo w) {
               w.endpoint().send(new DeployMessages.ApplicationFinished(this.app$2.id()));
            }

            public {
               this.app$2 = app$2;
            }
         });
      }

   }

   public boolean org$apache$spark$deploy$master$Master$$handleRequestExecutors(String appId, int requestedTotal) {
      Option var3 = this.idToApp().get(appId);
      boolean var6;
      if (var3 instanceof Some) {
         Some var4 = (Some)var3;
         ApplicationInfo appInfo = (ApplicationInfo)var4.x();
         this.logInfo(new Serializable(this, appId, requestedTotal) {
            public static final long serialVersionUID = 0L;
            private final String appId$3;
            private final int requestedTotal$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Application ", " requested to set total executors to ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$3, BoxesRunTime.boxToInteger(this.requestedTotal$1)}));
            }

            public {
               this.appId$3 = appId$3;
               this.requestedTotal$1 = requestedTotal$1;
            }
         });
         appInfo.executorLimit_$eq(requestedTotal);
         this.org$apache$spark$deploy$master$Master$$schedule();
         var6 = true;
      } else {
         if (!None$.MODULE$.equals(var3)) {
            throw new MatchError(var3);
         }

         this.logWarning(new Serializable(this, appId, requestedTotal) {
            public static final long serialVersionUID = 0L;
            private final String appId$3;
            private final int requestedTotal$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown application ", " requested ", " total executors."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$3, BoxesRunTime.boxToInteger(this.requestedTotal$1)}));
            }

            public {
               this.appId$3 = appId$3;
               this.requestedTotal$1 = requestedTotal$1;
            }
         });
         var6 = false;
      }

      return var6;
   }

   public boolean org$apache$spark$deploy$master$Master$$handleKillExecutors(String appId, Seq<Object> executorIds) {
      Option var3 = this.idToApp().get(appId);
      boolean var6;
      if (var3 instanceof Some) {
         Some var4 = (Some)var3;
         ApplicationInfo appInfo = (ApplicationInfo)var4.x();
         this.logInfo(new Serializable(this, appId, executorIds) {
            public static final long serialVersionUID = 0L;
            private final String appId$4;
            private final Seq executorIds$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Application ", " requests to kill executors: "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$4}))).append((Object)this.executorIds$1.mkString(", ")).toString();
            }

            public {
               this.appId$4 = appId$4;
               this.executorIds$1 = executorIds$1;
            }
         });
         HashMap var9 = appInfo.executors();
         Tuple2 var8 = executorIds.partition(new Serializable(this, var9) {
            public static final long serialVersionUID = 0L;
            private final HashMap eta$0$3$1;

            public final boolean apply(int key) {
               return this.apply$mcZI$sp(key);
            }

            public boolean apply$mcZI$sp(int key) {
               return this.eta$0$3$1.contains(BoxesRunTime.boxToInteger(key));
            }

            public {
               this.eta$0$3$1 = eta$0$3$1;
            }
         });
         if (var8 == null) {
            throw new MatchError(var8);
         }

         Seq known = (Seq)var8._1();
         Seq unknown = (Seq)var8._2();
         Tuple2 var12 = new Tuple2(known, unknown);
         Seq known = (Seq)var12._1();
         Seq unknown = (Seq)var12._2();
         known.foreach(new Serializable(this, appInfo) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Master $outer;
            private final ApplicationInfo appInfo$2;

            public final void apply(int executorId) {
               this.apply$mcVI$sp(executorId);
            }

            public void apply$mcVI$sp(int executorId) {
               ExecutorDesc desc = (ExecutorDesc)this.appInfo$2.executors().apply(BoxesRunTime.boxToInteger(executorId));
               this.appInfo$2.removeExecutor(desc);
               this.$outer.org$apache$spark$deploy$master$Master$$killExecutor(desc);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.appInfo$2 = appInfo$2;
               }
            }
         });
         if (unknown.nonEmpty()) {
            this.logWarning(new Serializable(this, appId, unknown) {
               public static final long serialVersionUID = 0L;
               private final String appId$4;
               private final Seq unknown$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Application ", " attempted to kill non-existent executors: "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$4}))).append((Object)this.unknown$1.mkString(", ")).toString();
               }

               public {
                  this.appId$4 = appId$4;
                  this.unknown$1 = unknown$1;
               }
            });
         }

         this.org$apache$spark$deploy$master$Master$$schedule();
         var6 = true;
      } else {
         if (!None$.MODULE$.equals(var3)) {
            throw new MatchError(var3);
         }

         this.logWarning(new Serializable(this, appId) {
            public static final long serialVersionUID = 0L;
            private final String appId$4;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unregistered application ", " requested us to kill executors!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$4}));
            }

            public {
               this.appId$4 = appId$4;
            }
         });
         var6 = false;
      }

      return var6;
   }

   public Seq<Object> org$apache$spark$deploy$master$Master$$formatExecutorIds(Seq<String> executorIds) {
      return (Seq)executorIds.flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;

         public final Iterable<Object> apply(String executorId) {
            Iterable var10000;
            try {
               var10000 = Option$.MODULE$.option2Iterable(new Some(BoxesRunTime.boxToInteger((new StringOps(Predef$.MODULE$.augmentString(executorId))).toInt())));
            } catch (NumberFormatException var3) {
               this.$outer.logError(new Serializable(this, executorId) {
                  public static final long serialVersionUID = 0L;
                  private final String executorId$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Encountered executor with a non-integer ID: ", ". Ignoring"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$1}));
                  }

                  public {
                     this.executorId$1 = executorId$1;
                  }
               });
               var10000 = Option$.MODULE$.option2Iterable(None$.MODULE$);
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public void org$apache$spark$deploy$master$Master$$killExecutor(ExecutorDesc exec) {
      exec.worker().removeExecutor(exec);
      exec.worker().endpoint().send(new DeployMessages.KillExecutor(this.org$apache$spark$deploy$master$Master$$masterUrl(), exec.application().id(), exec.id()));
      exec.state_$eq(ExecutorState$.MODULE$.KILLED());
   }

   private String newApplicationId(Date submitDate) {
      String appId = (new StringOps(Predef$.MODULE$.augmentString("app-%s-%04d"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.createDateFormat().format(submitDate), BoxesRunTime.boxToInteger(this.nextAppNumber())}));
      this.nextAppNumber_$eq(this.nextAppNumber() + 1);
      return appId;
   }

   public void org$apache$spark$deploy$master$Master$$timeOutDeadWorkers() {
      long currentTime = System.currentTimeMillis();
      WorkerInfo[] toRemove = (WorkerInfo[])((TraversableOnce)this.workers().filter(new Serializable(this, currentTime) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;
         private final long currentTime$1;

         public final boolean apply(WorkerInfo x$28) {
            return x$28.lastHeartbeat() < this.currentTime$1 - this.$outer.org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.currentTime$1 = currentTime$1;
            }
         }
      })).toArray(ClassTag$.MODULE$.apply(WorkerInfo.class));
      Predef$.MODULE$.refArrayOps(toRemove).foreach(new Serializable(this, currentTime) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Master $outer;
         private final long currentTime$1;

         public final Object apply(WorkerInfo worker) {
            Object var3;
            label28: {
               Enumeration.Value var10000 = worker.state();
               Enumeration.Value var2 = WorkerState$.MODULE$.DEAD();
               if (var10000 == null) {
                  if (var2 != null) {
                     break label28;
                  }
               } else if (!var10000.equals(var2)) {
                  break label28;
               }

               var3 = worker.lastHeartbeat() < this.currentTime$1 - (long)(this.$outer.org$apache$spark$deploy$master$Master$$REAPER_ITERATIONS() + 1) * this.$outer.org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS() ? this.$outer.workers().$minus$eq(worker) : BoxedUnit.UNIT;
               return var3;
            }

            this.$outer.logWarning(new Serializable(this, worker) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final WorkerInfo worker$9;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Removing %s because we got no heartbeat in %d seconds"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.worker$9.id(), BoxesRunTime.boxToLong(this.$outer.org$apache$spark$deploy$master$Master$$anonfun$$$outer().org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS() / 1000L)}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.worker$9 = worker$9;
                  }
               }
            });
            this.$outer.org$apache$spark$deploy$master$Master$$removeWorker(worker, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Not receiving heartbeat for ", " seconds"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS() / 1000L)})));
            var3 = BoxedUnit.UNIT;
            return var3;
         }

         // $FF: synthetic method
         public Master org$apache$spark$deploy$master$Master$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.currentTime$1 = currentTime$1;
            }
         }
      });
   }

   private String newDriverId(Date submitDate) {
      String appId = (new StringOps(Predef$.MODULE$.augmentString("driver-%s-%04d"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.createDateFormat().format(submitDate), BoxesRunTime.boxToInteger(this.nextDriverNumber())}));
      this.nextDriverNumber_$eq(this.nextDriverNumber() + 1);
      return appId;
   }

   public DriverInfo org$apache$spark$deploy$master$Master$$createDriver(DriverDescription desc) {
      long now = System.currentTimeMillis();
      Date date = new Date(now);
      return new DriverInfo(now, this.newDriverId(date), desc, date);
   }

   public void org$apache$spark$deploy$master$Master$$launchDriver(WorkerInfo worker, DriverInfo driver) {
      this.logInfo(new Serializable(this, worker, driver) {
         public static final long serialVersionUID = 0L;
         private final WorkerInfo worker$7;
         private final DriverInfo driver$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Launching driver ").append((Object)this.driver$1.id()).append((Object)" on worker ").append((Object)this.worker$7.id()).toString();
         }

         public {
            this.worker$7 = worker$7;
            this.driver$1 = driver$1;
         }
      });
      worker.addDriver(driver);
      driver.worker_$eq(new Some(worker));
      worker.endpoint().send(new DeployMessages.LaunchDriver(driver.id(), driver.desc()));
      driver.state_$eq(DriverState$.MODULE$.RUNNING());
   }

   public void org$apache$spark$deploy$master$Master$$removeDriver(String driverId, Enumeration.Value finalState, Option<Exception> exception) {
      Option var4 = this.org$apache$spark$deploy$master$Master$$drivers().find(new Serializable(this, driverId) {
         public static final long serialVersionUID = 0L;
         private final String driverId$1;

         public final boolean apply(DriverInfo d) {
            boolean var3;
            label23: {
               String var10000 = d.id();
               String var2 = this.driverId$1;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }

         public {
            this.driverId$1 = driverId$1;
         }
      });
      if (var4 instanceof Some) {
         Some var5 = (Some)var4;
         DriverInfo driver = (DriverInfo)var5.x();
         this.logInfo(new Serializable(this, driverId) {
            public static final long serialVersionUID = 0L;
            private final String driverId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing driver: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$1}));
            }

            public {
               this.driverId$1 = driverId$1;
            }
         });
         this.org$apache$spark$deploy$master$Master$$drivers().$minus$eq(driver);
         if (this.org$apache$spark$deploy$master$Master$$completedDrivers().size() >= this.RETAINED_DRIVERS()) {
            int toRemove = scala.math.package$.MODULE$.max(this.RETAINED_DRIVERS() / 10, 1);
            this.org$apache$spark$deploy$master$Master$$completedDrivers().trimStart(toRemove);
         }

         this.org$apache$spark$deploy$master$Master$$completedDrivers().$plus$eq(driver);
         this.org$apache$spark$deploy$master$Master$$persistenceEngine().removeDriver(driver);
         driver.state_$eq(finalState);
         driver.exception_$eq(exception);
         driver.worker().foreach(new Serializable(this, driver) {
            public static final long serialVersionUID = 0L;
            private final DriverInfo driver$2;

            public final void apply(WorkerInfo w) {
               w.removeDriver(this.driver$2);
            }

            public {
               this.driver$2 = driver$2;
            }
         });
         this.org$apache$spark$deploy$master$Master$$schedule();
         BoxedUnit var7 = BoxedUnit.UNIT;
      } else {
         if (!None$.MODULE$.equals(var4)) {
            throw new MatchError(var4);
         }

         this.logWarning(new Serializable(this, driverId) {
            public static final long serialVersionUID = 0L;
            private final String driverId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Asked to remove unknown driver: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$1}));
            }

            public {
               this.driverId$1 = driverId$1;
            }
         });
         BoxedUnit var9 = BoxedUnit.UNIT;
      }

   }

   public final boolean org$apache$spark$deploy$master$Master$$canLaunchExecutor$1(int pos, ApplicationInfo app$3, WorkerInfo[] usableWorkers$1, int minCoresPerExecutor$1, boolean oneExecutorPerWorker$1, int memoryPerExecutor$1, int[] assignedCores$1, int[] assignedExecutors$1, IntRef coresToAssign$1) {
      boolean keepScheduling = coresToAssign$1.elem >= minCoresPerExecutor$1;
      boolean enoughCores = usableWorkers$1[pos].coresFree() - assignedCores$1[pos] >= minCoresPerExecutor$1;
      boolean launchingNewExecutor = !oneExecutorPerWorker$1 || assignedExecutors$1[pos] == 0;
      boolean var10000;
      if (launchingNewExecutor) {
         int assignedMemory = assignedExecutors$1[pos] * memoryPerExecutor$1;
         boolean enoughMemory = usableWorkers$1[pos].memoryFree() - assignedMemory >= memoryPerExecutor$1;
         boolean underLimit = BoxesRunTime.unboxToInt(Predef$.MODULE$.intArrayOps(assignedExecutors$1).sum(Numeric.IntIsIntegral$.MODULE$)) + app$3.executors().size() < app$3.executorLimit();
         var10000 = keepScheduling && enoughCores && enoughMemory && underLimit;
      } else {
         var10000 = keepScheduling && enoughCores;
      }

      return var10000;
   }

   public Master(RpcEnv rpcEnv, RpcAddress address, int webUiPort, SecurityManager securityMgr, SparkConf conf) {
      this.rpcEnv = rpcEnv;
      this.org$apache$spark$deploy$master$Master$$address = address;
      this.webUiPort = webUiPort;
      this.securityMgr = securityMgr;
      this.conf = conf;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      this.org$apache$spark$deploy$master$Master$$forwardMessageThread = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("master-forward-message-thread");
      this.hadoopConf = SparkHadoopUtil$.MODULE$.get().newConfiguration(conf);
      this.org$apache$spark$deploy$master$Master$$WORKER_TIMEOUT_MS = conf.getLong("spark.worker.timeout", 60L) * 1000L;
      this.RETAINED_APPLICATIONS = conf.getInt("spark.deploy.retainedApplications", 200);
      this.RETAINED_DRIVERS = conf.getInt("spark.deploy.retainedDrivers", 200);
      this.org$apache$spark$deploy$master$Master$$REAPER_ITERATIONS = conf.getInt("spark.dead.worker.persistence", 15);
      this.RECOVERY_MODE = conf.get("spark.deploy.recoveryMode", "NONE");
      this.org$apache$spark$deploy$master$Master$$MAX_EXECUTOR_RETRIES = conf.getInt("spark.deploy.maxExecutorRetries", 10);
      this.workers = new HashSet<WorkerInfo>();
      this.idToApp = new HashMap<String, ApplicationInfo>();
      this.waitingApps = new ArrayBuffer<ApplicationInfo>();
      this.apps = new HashSet<ApplicationInfo>();
      this.org$apache$spark$deploy$master$Master$$idToWorker = new HashMap<String, WorkerInfo>();
      this.addressToWorker = new HashMap<RpcAddress, WorkerInfo>();
      this.endpointToApp = new HashMap<RpcEndpointRef, ApplicationInfo>();
      this.addressToApp = new HashMap<RpcAddress, ApplicationInfo>();
      this.org$apache$spark$deploy$master$Master$$completedApps = new ArrayBuffer<ApplicationInfo>();
      this.nextAppNumber = 0;
      this.org$apache$spark$deploy$master$Master$$drivers = new HashSet<DriverInfo>();
      this.org$apache$spark$deploy$master$Master$$completedDrivers = new ArrayBuffer<DriverInfo>();
      this.org$apache$spark$deploy$master$Master$$waitingDrivers = new ArrayBuffer<DriverInfo>();
      this.nextDriverNumber = 0;
      Utils$.MODULE$.checkHost(address.host());
      this.masterMetricsSystem = MetricsSystem$.MODULE$.createMetricsSystem("master", conf, securityMgr);
      this.org$apache$spark$deploy$master$Master$$applicationMetricsSystem = MetricsSystem$.MODULE$.createMetricsSystem("applications", conf, securityMgr);
      this.masterSource = new MasterSource(this);
      this.org$apache$spark$deploy$master$Master$$webUi = null;
      String envVar = conf.getenv("SPARK_PUBLIC_DNS");
      this.masterPublicAddress = envVar == null ? address.host() : envVar;
      this.org$apache$spark$deploy$master$Master$$masterUrl = address.toSparkURL();
      this.org$apache$spark$deploy$master$Master$$state = RecoveryState$.MODULE$.STANDBY();
      this.org$apache$spark$deploy$master$Master$$spreadOutApps = conf.getBoolean("spark.deploy.spreadOut", true);
      this.defaultCores = conf.getInt("spark.deploy.defaultCores", Integer.MAX_VALUE);
      this.reverseProxy = conf.getBoolean("spark.ui.reverseProxy", false);
      if (this.defaultCores() < 1) {
         throw new SparkException("spark.deploy.defaultCores must be positive");
      } else {
         this.restServerEnabled = conf.getBoolean("spark.master.rest.enabled", true);
         this.restServer = None$.MODULE$;
         this.org$apache$spark$deploy$master$Master$$restServerBoundPort = None$.MODULE$;
      }
   }
}
