package org.apache.spark.deploy.worker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.package$;
import org.apache.spark.deploy.ApplicationDescription;
import org.apache.spark.deploy.Command;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.DriverDescription;
import org.apache.spark.deploy.ExecutorDescription;
import org.apache.spark.deploy.ExecutorState$;
import org.apache.spark.deploy.ExternalShuffleService;
import org.apache.spark.deploy.master.DriverState$;
import org.apache.spark.deploy.master.Master$;
import org.apache.spark.deploy.worker.ui.WorkerWebUI;
import org.apache.spark.deploy.worker.ui.WorkerWebUI$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.metrics.MetricsSystem;
import org.apache.spark.metrics.MetricsSystem$;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointAddress;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
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
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.Seq;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayOps;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.LinkedHashMap;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.ExecutionContext$;
import scala.concurrent.ExecutionContextExecutorService;
import scala.concurrent.Future$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.util.Random;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0015-e!B\u0001\u0003\u0001\u0011a!AB,pe.,'O\u0003\u0002\u0004\t\u00051qo\u001c:lKJT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0003\u0002\u0001\u000e'e\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\u000b\u0018\u001b\u0005)\"B\u0001\f\u0007\u0003\r\u0011\boY\u0005\u00031U\u0011Q\u0003\u00165sK\u0006$7+\u00194f%B\u001cWI\u001c3q_&tG\u000f\u0005\u0002\u001b;5\t1D\u0003\u0002\u001d\r\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u001f7\t9Aj\\4hS:<\u0007\u0002\u0003\u0011\u0001\u0005\u000b\u0007I\u0011\t\u0012\u0002\rI\u00048-\u00128w\u0007\u0001)\u0012a\t\t\u0003)\u0011J!!J\u000b\u0003\rI\u00038-\u00128w\u0011!9\u0003A!A!\u0002\u0013\u0019\u0013a\u0002:qG\u0016sg\u000f\t\u0005\tS\u0001\u0011\t\u0011)A\u0005U\u0005Iq/\u001a2VSB{'\u000f\u001e\t\u0003\u001d-J!\u0001L\b\u0003\u0007%sG\u000f\u0003\u0005/\u0001\t\u0005\t\u0015!\u0003+\u0003\u0015\u0019wN]3t\u0011!\u0001\u0004A!A!\u0002\u0013Q\u0013AB7f[>\u0014\u0018\u0010\u0003\u00053\u0001\t\u0005\t\u0015!\u00034\u0003Ii\u0017m\u001d;feJ\u00038-\u00113ee\u0016\u001c8/Z:\u0011\u00079!d'\u0003\u00026\u001f\t)\u0011I\u001d:bsB\u0011AcN\u0005\u0003qU\u0011!B\u00159d\u0003\u0012$'/Z:t\u0011!Q\u0004A!A!\u0002\u0013Y\u0014\u0001D3oIB|\u0017N\u001c;OC6,\u0007C\u0001\u001f@\u001d\tqQ(\u0003\u0002?\u001f\u00051\u0001K]3eK\u001aL!\u0001Q!\u0003\rM#(/\u001b8h\u0015\tqt\u0002\u0003\u0005D\u0001\t\u0005\t\u0015!\u0003<\u0003-9xN]6ESJ\u0004\u0016\r\u001e5\t\u0011\u0015\u0003!Q1A\u0005\u0002\u0019\u000bAaY8oMV\tq\t\u0005\u0002I\u00136\ta!\u0003\u0002K\r\tI1\u000b]1sW\u000e{gN\u001a\u0005\t\u0019\u0002\u0011\t\u0011)A\u0005\u000f\u0006)1m\u001c8gA!Aa\n\u0001BC\u0002\u0013\u0005q*A\u0006tK\u000e,(/\u001b;z\u001b\u001e\u0014X#\u0001)\u0011\u0005!\u000b\u0016B\u0001*\u0007\u0005=\u0019VmY;sSRLX*\u00198bO\u0016\u0014\b\u0002\u0003+\u0001\u0005\u0003\u0005\u000b\u0011\u0002)\u0002\u0019M,7-\u001e:jifluM\u001d\u0011\t\u000bY\u0003A\u0011A,\u0002\rqJg.\u001b;?))A&l\u0017/^=~\u0003\u0017M\u0019\t\u00033\u0002i\u0011A\u0001\u0005\u0006AU\u0003\ra\t\u0005\u0006SU\u0003\rA\u000b\u0005\u0006]U\u0003\rA\u000b\u0005\u0006aU\u0003\rA\u000b\u0005\u0006eU\u0003\ra\r\u0005\u0006uU\u0003\ra\u000f\u0005\b\u0007V\u0003\n\u00111\u0001<\u0011\u0015)U\u000b1\u0001H\u0011\u0015qU\u000b1\u0001Q\u0011\u001d!\u0007A1A\u0005\n\u0015\fA\u0001[8tiV\t1\b\u0003\u0004h\u0001\u0001\u0006IaO\u0001\u0006Q>\u001cH\u000f\t\u0005\bS\u0002\u0011\r\u0011\"\u0003k\u0003\u0011\u0001xN\u001d;\u0016\u0003)Ba\u0001\u001c\u0001!\u0002\u0013Q\u0013!\u00029peR\u0004\u0003b\u00028\u0001\u0005\u0004%Ia\\\u0001\u0018M>\u0014xo\u001c:e\u001b\u0016\u001c8/Y4f'\u000eDW\rZ;mKJ,\u0012\u0001\u001d\t\u0003cbl\u0011A\u001d\u0006\u0003gR\f!bY8oGV\u0014(/\u001a8u\u0015\t)h/\u0001\u0003vi&d'\"A<\u0002\t)\fg/Y\u0005\u0003sJ\u0014\u0001dU2iK\u0012,H.\u001a3Fq\u0016\u001cW\u000f^8s'\u0016\u0014h/[2f\u0011\u0019Y\b\u0001)A\u0005a\u0006Abm\u001c:x_J$W*Z:tC\u001e,7k\u00195fIVdWM\u001d\u0011\t\u000fu\u0004!\u0019!C\u0005}\u0006)2\r\\3b]V\u0004H\u000b\u001b:fC\u0012,\u00050Z2vi>\u0014X#A@\u0011\t\u0005\u0005\u0011QA\u0007\u0003\u0003\u0007Q!a]\b\n\t\u0005\u001d\u00111\u0001\u0002 \u000bb,7-\u001e;j_:\u001cuN\u001c;fqR,\u00050Z2vi>\u00148+\u001a:wS\u000e,\u0007bBA\u0006\u0001\u0001\u0006Ia`\u0001\u0017G2,\u0017M\\;q)\"\u0014X-\u00193Fq\u0016\u001cW\u000f^8sA!9\u0011q\u0002\u0001\u0005\n\u0005E\u0011\u0001E2sK\u0006$X\rR1uK\u001a{'/\\1u+\t\t\u0019\u0002\u0005\u0003\u0002\u0016\u0005mQBAA\f\u0015\r\tIB^\u0001\u0005i\u0016DH/\u0003\u0003\u0002\u001e\u0005]!\u0001E*j[BdW\rR1uK\u001a{'/\\1u\u0011%\t\t\u0003\u0001b\u0001\n\u0013\t\u0019#\u0001\tI\u000b\u0006\u0013FKQ#B)~k\u0015\n\u0014'J'V\u0011\u0011Q\u0005\t\u0004\u001d\u0005\u001d\u0012bAA\u0015\u001f\t!Aj\u001c8h\u0011!\ti\u0003\u0001Q\u0001\n\u0005\u0015\u0012!\u0005%F\u0003J#&)R!U?6KE\nT%TA!A\u0011\u0011\u0007\u0001C\u0002\u0013%!.\u0001\u000fJ\u001d&#\u0016*\u0011'`%\u0016;\u0015j\u0015+S\u0003RKuJT0S\u000bR\u0013\u0016*R*\t\u000f\u0005U\u0002\u0001)A\u0005U\u0005i\u0012JT%U\u0013\u0006cuLU#H\u0013N#&+\u0011+J\u001f:{&+\u0012+S\u0013\u0016\u001b\u0006\u0005\u0003\u0005\u0002:\u0001\u0011\r\u0011\"\u0003k\u0003i!v\nV!M?J+u)S*U%\u0006#\u0016j\u0014(`%\u0016#&+S#T\u0011\u001d\ti\u0004\u0001Q\u0001\n)\n1\u0004V(U\u00032{&+R$J'R\u0013\u0016\tV%P\u001d~\u0013V\t\u0016*J\u000bN\u0003\u0003\"CA!\u0001\t\u0007I\u0011BA\"\u0003\u00112UK\u0017.`\u001bVcE+\u0013)M\u0013\u0016\u0013v,\u0013(U\u000bJ3\u0016\tT0M\u001f^+%k\u0018\"P+:#UCAA#!\rq\u0011qI\u0005\u0004\u0003\u0013z!A\u0002#pk\ndW\r\u0003\u0005\u0002N\u0001\u0001\u000b\u0011BA#\u0003\u00152UK\u0017.`\u001bVcE+\u0013)M\u0013\u0016\u0013v,\u0013(U\u000bJ3\u0016\tT0M\u001f^+%k\u0018\"P+:#\u0005\u0005C\u0005\u0002R\u0001\u0011\r\u0011\"\u0003\u0002D\u0005\u0011#+R$J'R\u0013\u0016\tV%P\u001d~\u0013V\t\u0016*Z?\u001a+&LW0N+2#\u0016\n\u0015'J\u000bJC\u0001\"!\u0016\u0001A\u0003%\u0011QI\u0001$%\u0016;\u0015j\u0015+S\u0003RKuJT0S\u000bR\u0013\u0016l\u0018$V5j{V*\u0016'U\u0013Bc\u0015*\u0012*!\u0011%\tI\u0006\u0001b\u0001\n\u0013\t\u0019#A\u0016J\u001d&#\u0016*\u0011'`%\u0016;\u0015j\u0015+S\u0003RKuJT0S\u000bR\u0013\u0016lX%O)\u0016\u0013f+\u0011'`'\u0016\u001buJ\u0014#T\u0011!\ti\u0006\u0001Q\u0001\n\u0005\u0015\u0012\u0001L%O\u0013RK\u0015\tT0S\u000b\u001eK5\u000b\u0016*B)&{ej\u0018*F)JKv,\u0013(U\u000bJ3\u0016\tT0T\u000b\u000e{e\nR*!\u0011%\t\t\u0007\u0001b\u0001\n\u0013\t\u0019#A\u0017Q%>cuJT$F\t~\u0013ViR%T)J\u000bE+S(O?J+EKU-`\u0013:#VI\u0015,B\u0019~\u001bViQ(O\tNC\u0001\"!\u001a\u0001A\u0003%\u0011QE\u0001/!J{Ej\u0014(H\u000b\u0012{&+R$J'R\u0013\u0016\tV%P\u001d~\u0013V\t\u0016*Z?&sE+\u0012*W\u00032{6+R\"P\u001d\u0012\u001b\u0006\u0005C\u0005\u0002j\u0001\u0011\r\u0011\"\u0003\u0002l\u0005y1\tT#B\u001dV\u0003v,\u0012(B\u00052+E)\u0006\u0002\u0002nA\u0019a\"a\u001c\n\u0007\u0005EtBA\u0004C_>dW-\u00198\t\u0011\u0005U\u0004\u0001)A\u0005\u0003[\n\u0001c\u0011'F\u0003:+\u0006kX#O\u0003\ncU\t\u0012\u0011\t\u0013\u0005e\u0004A1A\u0005\n\u0005\r\u0012aF\"M\u000b\u0006sU\u000bU0J\u001dR+%KV!M?6KE\nT%T\u0011!\ti\b\u0001Q\u0001\n\u0005\u0015\u0012\u0001G\"M\u000b\u0006sU\u000bU0J\u001dR+%KV!M?6KE\nT%TA!I\u0011\u0011\u0011\u0001C\u0002\u0013%\u00111E\u0001\u001b\u0003B\u0003v\fR!U\u0003~\u0013V\tV#O)&{ejX*F\u0007>sEi\u0015\u0005\t\u0003\u000b\u0003\u0001\u0015!\u0003\u0002&\u0005Y\u0012\t\u0015)`\t\u0006#\u0016i\u0018*F)\u0016sE+S(O?N+5i\u0014(E'\u0002B\u0011\"!#\u0001\u0005\u0004%I!a\u001b\u0002\u000fQ,7\u000f^5oO\"A\u0011Q\u0012\u0001!\u0002\u0013\ti'\u0001\u0005uKN$\u0018N\\4!\u0011%\t\t\n\u0001a\u0001\n\u0013\t\u0019*\u0001\u0004nCN$XM]\u000b\u0003\u0003+\u0003RADAL\u00037K1!!'\u0010\u0005\u0019y\u0005\u000f^5p]B\u0019A#!(\n\u0007\u0005}UC\u0001\bSa\u000e,e\u000e\u001a9pS:$(+\u001a4\t\u0013\u0005\r\u0006\u00011A\u0005\n\u0005\u0015\u0016AC7bgR,'o\u0018\u0013fcR!\u0011qUAW!\rq\u0011\u0011V\u0005\u0004\u0003W{!\u0001B+oSRD!\"a,\u0002\"\u0006\u0005\t\u0019AAK\u0003\rAH%\r\u0005\t\u0003g\u0003\u0001\u0015)\u0003\u0002\u0016\u00069Q.Y:uKJ\u0004\u0003\"CA\\\u0001\t\u0007I\u0011BA6\u0003u\u0001(/\u001a4fe\u000e{gNZ5hkJ,G-T1ti\u0016\u0014\u0018\t\u001a3sKN\u001c\b\u0002CA^\u0001\u0001\u0006I!!\u001c\u0002=A\u0014XMZ3s\u0007>tg-[4ve\u0016$W*Y:uKJ\fE\r\u001a:fgN\u0004\u0003\"CA`\u0001\u0001\u0007I\u0011BAa\u0003Yi\u0017m\u001d;fe\u0006#GM]3tgR{7i\u001c8oK\u000e$XCAAb!\u0011q\u0011q\u0013\u001c\t\u0013\u0005\u001d\u0007\u00011A\u0005\n\u0005%\u0017AG7bgR,'/\u00113ee\u0016\u001c8\u000fV8D_:tWm\u0019;`I\u0015\fH\u0003BAT\u0003\u0017D!\"a,\u0002F\u0006\u0005\t\u0019AAb\u0011!\ty\r\u0001Q!\n\u0005\r\u0017aF7bgR,'/\u00113ee\u0016\u001c8\u000fV8D_:tWm\u0019;!\u0011!\t\u0019\u000e\u0001a\u0001\n\u0013)\u0017aD1di&4X-T1ti\u0016\u0014XK\u001d7\t\u0013\u0005]\u0007\u00011A\u0005\n\u0005e\u0017aE1di&4X-T1ti\u0016\u0014XK\u001d7`I\u0015\fH\u0003BAT\u00037D\u0011\"a,\u0002V\u0006\u0005\t\u0019A\u001e\t\u000f\u0005}\u0007\u0001)Q\u0005w\u0005\u0001\u0012m\u0019;jm\u0016l\u0015m\u001d;feV\u0013H\u000e\t\u0005\n\u0003G\u0004\u0001\u0019!C\u0001\u0005\u0015\fA#Y2uSZ,W*Y:uKJ<VMY+j+Jd\u0007BCAt\u0001\u0001\u0007I\u0011\u0001\u0002\u0002j\u0006A\u0012m\u0019;jm\u0016l\u0015m\u001d;fe^+'-V5Ve2|F%Z9\u0015\t\u0005\u001d\u00161\u001e\u0005\n\u0003_\u000b)/!AA\u0002mBq!a<\u0001A\u0003&1(A\u000bbGRLg/Z'bgR,'oV3c+&,&\u000f\u001c\u0011\t\u0011\u0005M\b\u00011A\u0005\n\u0015\fab^8sW\u0016\u0014x+\u001a2VSV\u0013H\u000eC\u0005\u0002x\u0002\u0001\r\u0011\"\u0003\u0002z\u0006\u0011ro\u001c:lKJ<VMY+j+Jdw\fJ3r)\u0011\t9+a?\t\u0013\u0005=\u0016Q_A\u0001\u0002\u0004Y\u0004bBA\u0000\u0001\u0001\u0006KaO\u0001\u0010o>\u00148.\u001a:XK\n,\u0016.\u0016:mA!A!1\u0001\u0001C\u0002\u0013%Q-A\u0005x_J\\WM]+sS\"9!q\u0001\u0001!\u0002\u0013Y\u0014AC<pe.,'/\u0016:jA!I!1\u0002\u0001A\u0002\u0013%\u00111N\u0001\u000be\u0016<\u0017n\u001d;fe\u0016$\u0007\"\u0003B\b\u0001\u0001\u0007I\u0011\u0002B\t\u00039\u0011XmZ5ti\u0016\u0014X\rZ0%KF$B!a*\u0003\u0014!Q\u0011q\u0016B\u0007\u0003\u0003\u0005\r!!\u001c\t\u0011\t]\u0001\u0001)Q\u0005\u0003[\n1B]3hSN$XM]3eA!I!1\u0004\u0001A\u0002\u0013%\u00111N\u0001\nG>tg.Z2uK\u0012D\u0011Ba\b\u0001\u0001\u0004%IA!\t\u0002\u001b\r|gN\\3di\u0016$w\fJ3r)\u0011\t9Ka\t\t\u0015\u0005=&QDA\u0001\u0002\u0004\ti\u0007\u0003\u0005\u0003(\u0001\u0001\u000b\u0015BA7\u0003)\u0019wN\u001c8fGR,G\r\t\u0005\t\u0005W\u0001!\u0019!C\u0005K\u0006Aqo\u001c:lKJLE\rC\u0004\u00030\u0001\u0001\u000b\u0011B\u001e\u0002\u0013]|'o[3s\u0013\u0012\u0004\u0003\"\u0003B\u001a\u0001\t\u0007I\u0011\u0002B\u001b\u0003%\u0019\b/\u0019:l\u0011>lW-\u0006\u0002\u00038A!!\u0011\bB \u001b\t\u0011YDC\u0002\u0003>Y\f!![8\n\t\t\u0005#1\b\u0002\u0005\r&dW\r\u0003\u0005\u0003F\u0001\u0001\u000b\u0011\u0002B\u001c\u0003)\u0019\b/\u0019:l\u0011>lW\r\t\u0005\n\u0005\u0013\u0002\u0001\u0019!C\u0001\u0005k\tqa^8sW\u0012K'\u000fC\u0005\u0003N\u0001\u0001\r\u0011\"\u0001\u0003P\u0005Yqo\u001c:l\t&\u0014x\fJ3r)\u0011\t9K!\u0015\t\u0015\u0005=&1JA\u0001\u0002\u0004\u00119\u0004\u0003\u0005\u0003V\u0001\u0001\u000b\u0015\u0002B\u001c\u0003!9xN]6ESJ\u0004\u0003\"\u0003B-\u0001\t\u0007I\u0011\u0001B.\u0003E1\u0017N\\5tQ\u0016$W\t_3dkR|'o]\u000b\u0003\u0005;\u0002rAa\u0018\u0003jm\u0012i'\u0004\u0002\u0003b)!!1\rB3\u0003\u001diW\u000f^1cY\u0016T1Aa\u001a\u0010\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0005W\u0012\tGA\u0007MS:\\W\r\u001a%bg\"l\u0015\r\u001d\t\u00043\n=\u0014b\u0001B9\u0005\tqQ\t_3dkR|'OU;o]\u0016\u0014\b\u0002\u0003B;\u0001\u0001\u0006IA!\u0018\u0002%\u0019Lg.[:iK\u0012,\u00050Z2vi>\u00148\u000f\t\u0005\n\u0005s\u0002!\u0019!C\u0001\u0005w\nq\u0001\u001a:jm\u0016\u00148/\u0006\u0002\u0003~A9!q\fB@w\t\r\u0015\u0002\u0002BA\u0005C\u0012q\u0001S1tQ6\u000b\u0007\u000fE\u0002Z\u0005\u000bK1Aa\"\u0003\u00051!%/\u001b<feJ+hN\\3s\u0011!\u0011Y\t\u0001Q\u0001\n\tu\u0014\u0001\u00033sSZ,'o\u001d\u0011\t\u0013\t=\u0005A1A\u0005\u0002\tE\u0015!C3yK\u000e,Ho\u001c:t+\t\u0011\u0019\nE\u0004\u0003`\t}4H!\u001c\t\u0011\t]\u0005\u0001)A\u0005\u0005'\u000b!\"\u001a=fGV$xN]:!\u0011%\u0011Y\n\u0001b\u0001\n\u0003\u0011i*A\bgS:L7\u000f[3e\tJLg/\u001a:t+\t\u0011y\nE\u0004\u0003`\t%4Ha!\t\u0011\t\r\u0006\u0001)A\u0005\u0005?\u000b\u0001CZ5oSNDW\r\u001a#sSZ,'o\u001d\u0011\t\u0013\t\u001d\u0006A1A\u0005\u0002\t%\u0016AD1qa\u0012K'/Z2u_JLWm]\u000b\u0003\u0005W\u0003rAa\u0018\u0003\u0000m\u0012i\u000bE\u0003\u00030\n}6H\u0004\u0003\u00032\nmf\u0002\u0002BZ\u0005sk!A!.\u000b\u0007\t]\u0016%\u0001\u0004=e>|GOP\u0005\u0002!%\u0019!QX\b\u0002\u000fA\f7m[1hK&!!\u0011\u0019Bb\u0005\r\u0019V-\u001d\u0006\u0004\u0005{{\u0001\u0002\u0003Bd\u0001\u0001\u0006IAa+\u0002\u001f\u0005\u0004\b\u000fR5sK\u000e$xN]5fg\u0002B\u0011Ba3\u0001\u0005\u0004%\tA!4\u0002\u0019\u0019Lg.[:iK\u0012\f\u0005\u000f]:\u0016\u0005\t=\u0007#\u0002B0\u0005#\\\u0014\u0002\u0002Bj\u0005C\u0012q\u0001S1tQN+G\u000f\u0003\u0005\u0003X\u0002\u0001\u000b\u0011\u0002Bh\u000351\u0017N\\5tQ\u0016$\u0017\t\u001d9tA!A!1\u001c\u0001C\u0002\u0013\u0005!.A\tsKR\f\u0017N\\3e\u000bb,7-\u001e;peNDqAa8\u0001A\u0003%!&\u0001\nsKR\f\u0017N\\3e\u000bb,7-\u001e;peN\u0004\u0003\u0002\u0003Br\u0001\t\u0007I\u0011\u00016\u0002\u001fI,G/Y5oK\u0012$%/\u001b<feNDqAa:\u0001A\u0003%!&\u0001\tsKR\f\u0017N\\3e\tJLg/\u001a:tA!I!1\u001e\u0001C\u0002\u0013%!Q^\u0001\u000fg\",hM\u001a7f'\u0016\u0014h/[2f+\t\u0011y\u000f\u0005\u0003\u0003r\nMX\"\u0001\u0003\n\u0007\tUHA\u0001\fFqR,'O\\1m'\",hM\u001a7f'\u0016\u0014h/[2f\u0011!\u0011I\u0010\u0001Q\u0001\n\t=\u0018aD:ik\u001a4G.Z*feZL7-\u001a\u0011\t\u0011\tu\bA1A\u0005\n\u0015\fQ\u0002];cY&\u001c\u0017\t\u001a3sKN\u001c\bbBB\u0001\u0001\u0001\u0006IaO\u0001\u000faV\u0014G.[2BI\u0012\u0014Xm]:!\u0011%\u0019)\u0001\u0001a\u0001\n\u0013\u00199!A\u0003xK\n,\u0016.\u0006\u0002\u0004\nA!11BB\t\u001b\t\u0019iAC\u0002\u0004\u0010\t\t!!^5\n\t\rM1Q\u0002\u0002\f/>\u00148.\u001a:XK\n,\u0016\nC\u0005\u0004\u0018\u0001\u0001\r\u0011\"\u0003\u0004\u001a\u0005Iq/\u001a2VS~#S-\u001d\u000b\u0005\u0003O\u001bY\u0002\u0003\u0006\u00020\u000eU\u0011\u0011!a\u0001\u0007\u0013A\u0001ba\b\u0001A\u0003&1\u0011B\u0001\u0007o\u0016\u0014W+\u001b\u0011\t\u0011\r\r\u0002\u00011A\u0005\n)\facY8o]\u0016\u001cG/[8o\u0003R$X-\u001c9u\u0007>,h\u000e\u001e\u0005\n\u0007O\u0001\u0001\u0019!C\u0005\u0007S\t!dY8o]\u0016\u001cG/[8o\u0003R$X-\u001c9u\u0007>,h\u000e^0%KF$B!a*\u0004,!I\u0011qVB\u0013\u0003\u0003\u0005\rA\u000b\u0005\b\u0007_\u0001\u0001\u0015)\u0003+\u0003]\u0019wN\u001c8fGRLwN\\!ui\u0016l\u0007\u000f^\"pk:$\b\u0005C\u0005\u00044\u0001\u0011\r\u0011\"\u0003\u00046\u0005iQ.\u001a;sS\u000e\u001c8+_:uK6,\"aa\u000e\u0011\t\re2qH\u0007\u0003\u0007wQ1a!\u0010\u0007\u0003\u001diW\r\u001e:jGNLAa!\u0011\u0004<\tiQ*\u001a;sS\u000e\u001c8+_:uK6D\u0001b!\u0012\u0001A\u0003%1qG\u0001\u000f[\u0016$(/[2t'f\u001cH/Z7!\u0011%\u0019I\u0005\u0001b\u0001\n\u0013\u0019Y%\u0001\u0007x_J\\WM]*pkJ\u001cW-\u0006\u0002\u0004NA\u0019\u0011la\u0014\n\u0007\rE#A\u0001\u0007X_J\\WM]*pkJ\u001cW\r\u0003\u0005\u0004V\u0001\u0001\u000b\u0011BB'\u000359xN]6feN{WO]2fA!I1\u0011\f\u0001C\u0002\u0013\u0005\u00111N\u0001\re\u00164XM]:f!J|\u00070\u001f\u0005\t\u0007;\u0002\u0001\u0015!\u0003\u0002n\u0005i!/\u001a<feN,\u0007K]8ys\u0002B\u0011b!\u0019\u0001\u0001\u0004%Iaa\u0019\u0002+I,w-[:uKJl\u0015m\u001d;fe\u001a+H/\u001e:fgV\u00111Q\r\t\u0005\u001dQ\u001a9\u0007\r\u0003\u0004j\rM\u0004#B9\u0004l\r=\u0014bAB7e\n1a)\u001e;ve\u0016\u0004Ba!\u001d\u0004t1\u0001A\u0001DB;\u0007o\n\t\u0011!A\u0003\u0002\r\u0015%aA0%c!A1\u0011\u0010\u0001!B\u0013\u0019Y(\u0001\fsK\u001eL7\u000f^3s\u001b\u0006\u001cH/\u001a:GkR,(/Z:!!\u0011qAg! 1\t\r}41\u0011\t\u0006c\u000e-4\u0011\u0011\t\u0005\u0007c\u001a\u0019\t\u0002\u0007\u0004v\r]\u0014\u0011!A\u0001\u0006\u0003\u0019))\u0005\u0003\u0004\b\u000e5\u0005c\u0001\b\u0004\n&\u001911R\b\u0003\u000f9{G\u000f[5oOB\u0019aba$\n\u0007\rEuBA\u0002B]fD\u0011b!&\u0001\u0001\u0004%Iaa&\u00023I,w-[:uKJl\u0015m\u001d;fe\u001a+H/\u001e:fg~#S-\u001d\u000b\u0005\u0003O\u001bI\n\u0003\u0006\u00020\u000eM\u0015\u0011!a\u0001\u00077\u0003BA\u0004\u001b\u0004\u001eB\"1qTBR!\u0015\t81NBQ!\u0011\u0019\tha)\u0005\u0019\rU4qOA\u0001\u0002\u0003\u0015\ta!\"\t\u0013\r\u001d\u0006\u00011A\u0005\n\r%\u0016A\u0006:fO&\u001cHO]1uS>t'+\u001a;ssRKW.\u001a:\u0016\u0005\r-\u0006#\u0002\b\u0002\u0018\u000e5\u0006\u0007BBX\u0007o\u0003R!]BY\u0007kK1aa-s\u0005=\u00196\r[3ek2,GMR;ukJ,\u0007\u0003BB9\u0007o#Ab!/\u0004<\u0006\u0005\t\u0011!B\u0001\u0007\u000b\u00131a\u0018\u00133\u0011!\u0019i\f\u0001Q!\n\r}\u0016a\u0006:fO&\u001cHO]1uS>t'+\u001a;ssRKW.\u001a:!!\u0015q\u0011qSBaa\u0011\u0019\u0019ma2\u0011\u000bE\u001c\tl!2\u0011\t\rE4q\u0019\u0003\r\u0007s\u001bY,!A\u0001\u0002\u000b\u00051Q\u0011\u0005\n\u0007\u0017\u0004\u0001\u0019!C\u0005\u0007\u001b\f!D]3hSN$(/\u0019;j_:\u0014V\r\u001e:z)&lWM]0%KF$B!a*\u0004P\"Q\u0011qVBe\u0003\u0003\u0005\ra!5\u0011\u000b9\t9ja51\t\rU7\u0011\u001c\t\u0006c\u000eE6q\u001b\t\u0005\u0007c\u001aI\u000e\u0002\u0007\u0004:\u000em\u0016\u0011!A\u0001\u0006\u0003\u0019)\tC\u0005\u0004^\u0002\u0011\r\u0011\"\u0003\u0004`\u0006A\"/Z4jgR,'/T1ti\u0016\u0014H\u000b\u001b:fC\u0012\u0004vn\u001c7\u0016\u0005\r\u0005\bcA9\u0004d&\u00191Q\u001d:\u0003%QC'/Z1e!>|G.\u0012=fGV$xN\u001d\u0005\t\u0007S\u0004\u0001\u0015!\u0003\u0004b\u0006I\"/Z4jgR,'/T1ti\u0016\u0014H\u000b\u001b:fC\u0012\u0004vn\u001c7!\u0011!\u0019i\u000f\u0001a\u0001\n\u0003Q\u0017!C2pe\u0016\u001cXk]3e\u0011%\u0019\t\u0010\u0001a\u0001\n\u0003\u0019\u00190A\u0007d_J,7/V:fI~#S-\u001d\u000b\u0005\u0003O\u001b)\u0010C\u0005\u00020\u000e=\u0018\u0011!a\u0001U!91\u0011 \u0001!B\u0013Q\u0013AC2pe\u0016\u001cXk]3eA!A1Q \u0001A\u0002\u0013\u0005!.\u0001\u0006nK6|'/_+tK\u0012D\u0011\u0002\"\u0001\u0001\u0001\u0004%\t\u0001b\u0001\u0002\u001d5,Wn\u001c:z+N,Gm\u0018\u0013fcR!\u0011q\u0015C\u0003\u0011%\tyka@\u0002\u0002\u0003\u0007!\u0006C\u0004\u0005\n\u0001\u0001\u000b\u0015\u0002\u0016\u0002\u00175,Wn\u001c:z+N,G\r\t\u0005\u0007\t\u001b\u0001A\u0011\u00016\u0002\u0013\r|'/Z:Ge\u0016,\u0007B\u0002C\t\u0001\u0011\u0005!.\u0001\u0006nK6|'/\u001f$sK\u0016Dq\u0001\"\u0006\u0001\t\u0013!9\"A\u0007de\u0016\fG/Z,pe.$\u0015N\u001d\u000b\u0003\u0003OCq\u0001b\u0007\u0001\t\u0003\"9\"A\u0004p]N#\u0018M\u001d;\t\u000f\u0011}\u0001\u0001\"\u0003\u0005\"\u0005a1\r[1oO\u0016l\u0015m\u001d;feRA\u0011q\u0015C\u0012\tO!Y\u0003\u0003\u0005\u0005&\u0011u\u0001\u0019AAN\u0003%i\u0017m\u001d;feJ+g\rC\u0004\u0005*\u0011u\u0001\u0019A\u001e\u0002\u000bULWK\u001d7\t\u000f\u00115BQ\u0004a\u0001m\u0005iQ.Y:uKJ\fE\r\u001a:fgNDq\u0001\"\r\u0001\t\u0013!\u0019$A\u000buef\u0014VmZ5ti\u0016\u0014\u0018\t\u001c7NCN$XM]:\u0015\u0005\u0011U\u0002\u0003\u0002\b5\to\u0001D\u0001\"\u000f\u0005>A)\u0011oa\u001b\u0005<A!1\u0011\u000fC\u001f\t1!y\u0004b\f\u0002\u0002\u0003\u0005)\u0011ABC\u0005\ryFe\r\u0005\b\t\u0007\u0002A\u0011\u0002C\f\u0003Q\u0011XM]3hSN$XM],ji\"l\u0015m\u001d;fe\"9Aq\t\u0001\u0005\n\u0011]\u0011aG2b]\u000e,G\u000eT1tiJ+w-[:ue\u0006$\u0018n\u001c8SKR\u0014\u0018\u0010C\u0004\u0005L\u0001!I\u0001b\u0006\u0002%I,w-[:uKJ<\u0016\u000e\u001e5NCN$XM\u001d\u0005\b\t\u001f\u0002A\u0011\u0002C\f\u0003m\u0019H/\u0019:u\u000bb$XM\u001d8bYNCWO\u001a4mKN+'O^5dK\"9A1\u000b\u0001\u0005\n\u0011U\u0013aG:f]\u0012\u0014VmZ5ti\u0016\u0014X*Z:tC\u001e,Gk\\'bgR,'\u000f\u0006\u0003\u0002(\u0012]\u0003\u0002\u0003C-\t#\u0002\r!a'\u0002\u001d5\f7\u000f^3s\u000b:$\u0007o\\5oi\"9AQ\f\u0001\u0005\n\u0011}\u0013A\u00065b]\u0012dWMU3hSN$XM\u001d*fgB|gn]3\u0015\t\u0005\u001dF\u0011\r\u0005\t\tG\"Y\u00061\u0001\u0005f\u0005\u0019Qn]4\u0011\t\u0011\u001dDQ\u0010\b\u0005\tS\"IH\u0004\u0003\u0005l\u0011]d\u0002\u0002C7\tkrA\u0001b\u001c\u0005t9!!1\u0017C9\u0013\u0005Y\u0011BA\u0005\u000b\u0013\t9\u0001\"\u0003\u0002\u0006\r%\u0019A1\u0010\u0003\u0002\u001d\u0011+\u0007\u000f\\8z\u001b\u0016\u001c8/Y4fg&!Aq\u0010CA\u0005Y\u0011VmZ5ti\u0016\u0014xk\u001c:lKJ\u0014Vm\u001d9p]N,'b\u0001C>\t!9AQ\u0011\u0001\u0005B\u0011\u001d\u0015a\u0002:fG\u0016Lg/Z\u000b\u0003\t\u0013\u0003rA\u0004CF\u0007\u001b\u000b9+C\u0002\u0005\u000e>\u0011q\u0002U1si&\fGNR;oGRLwN\u001c\u0005\b\t#\u0003A\u0011\tCJ\u0003=\u0011XmY3jm\u0016\fe\u000e\u001a*fa2LH\u0003\u0002CE\t+C\u0001\u0002b&\u0005\u0010\u0002\u0007A\u0011T\u0001\bG>tG/\u001a=u!\r!B1T\u0005\u0004\t;+\"A\u0004*qG\u000e\u000bG\u000e\\\"p]R,\u0007\u0010\u001e\u0005\b\tC\u0003A\u0011\tCR\u00039yg\u000eR5tG>tg.Z2uK\u0012$B!a*\u0005&\"9Aq\u0015CP\u0001\u00041\u0014!\u0004:f[>$X-\u00113ee\u0016\u001c8\u000fC\u0004\u0005,\u0002!I\u0001b\u0006\u0002%5\f7\u000f^3s\t&\u001c8m\u001c8oK\u000e$X\r\u001a\u0005\b\t_\u0003A\u0011\u0002CY\u0003]i\u0017-\u001f2f\u00072,\u0017M\\;q\u0003B\u0004H.[2bi&|g\u000e\u0006\u0003\u0002(\u0012M\u0006b\u0002C[\t[\u0003\raO\u0001\u0003S\u0012Dq\u0001\"/\u0001\t\u0013!Y,\u0001\u0007tK:$Gk\\'bgR,'\u000f\u0006\u0003\u0002(\u0012u\u0006\u0002\u0003C`\to\u0003\ra!$\u0002\u000f5,7o]1hK\"9A1\u0019\u0001\u0005\n\u0011\u0015\u0017\u0001E4f]\u0016\u0014\u0018\r^3X_J\\WM]%e)\u0005Y\u0004b\u0002Ce\u0001\u0011\u0005CqC\u0001\u0007_:\u001cFo\u001c9\t\u000f\u00115\u0007\u0001\"\u0003\u0005\u0018\u0005\u0001CO]5n\r&t\u0017n\u001d5fI\u0016CXmY;u_J\u001c\u0018J\u001a(fG\u0016\u001c8/\u0019:z\u0011\u001d!\t\u000e\u0001C\u0005\t/\ta\u0004\u001e:j[\u001aKg.[:iK\u0012$%/\u001b<feNLeMT3dKN\u001c\u0018M]=\t\u0011\u0011U\u0007\u0001\"\u0001\u0003\t/\f\u0001\u0004[1oI2,GI]5wKJ\u001cF/\u0019;f\u0007\"\fgnZ3e)\u0011\t9\u000b\"7\t\u0011\u0011mG1\u001ba\u0001\t;\f!\u0003\u001a:jm\u0016\u00148\u000b^1uK\u000eC\u0017M\\4fIB!Aq\rCp\u0013\u0011!\t\u000f\"!\u0003%\u0011\u0013\u0018N^3s'R\fG/Z\"iC:<W\r\u001a\u0005\t\tK\u0004A\u0011\u0001\u0002\u0005h\u0006Q\u0002.\u00198eY\u0016,\u00050Z2vi>\u00148\u000b^1uK\u000eC\u0017M\\4fIR!\u0011q\u0015Cu\u0011!!Y\u000fb9A\u0002\u00115\u0018\u0001F3yK\u000e,Ho\u001c:Ti\u0006$Xm\u00115b]\u001e,G\r\u0005\u0003\u0005h\u0011=\u0018\u0002\u0002Cy\t\u0003\u0013A#\u0012=fGV$xN]*uCR,7\t[1oO\u0016$w\u0001\u0003C{\u0005!\u0005A\u0001b>\u0002\r]{'o[3s!\rIF\u0011 \u0004\b\u0003\tA\t\u0001\u0002C~'\u0011!I0D\r\t\u000fY#I\u0010\"\u0001\u0005\u0000R\u0011Aq\u001f\u0005\u000b\u000b\u0007!IP1A\u0005\u0002\u0015\u0015\u0011aC*Z'R+Uj\u0018(B\u001b\u0016+\"!b\u0002\u0011\t\u0015%QqB\u0007\u0003\u000b\u0017Q1!\"\u0004w\u0003\u0011a\u0017M\\4\n\u0007\u0001+Y\u0001C\u0005\u0006\u0014\u0011e\b\u0015!\u0003\u0006\b\u0005a1+W*U\u000b6{f*Q'FA!QQq\u0003C}\u0005\u0004%\t!\"\u0002\u0002\u001b\u0015sE\tU(J\u001dR{f*Q'F\u0011%)Y\u0002\"?!\u0002\u0013)9!\u0001\bF\u001d\u0012\u0003v*\u0013(U?:\u000bU*\u0012\u0011\t\u0011\u0015}A\u0011 C\u0001\u000bC\tA!\\1j]R!\u0011qUC\u0012\u0011!))#\"\bA\u0002\u0015\u001d\u0012AC1sON#(/\u001b8hgB\u0019a\u0002N\u001e\t\u0011\u0015-B\u0011 C\u0001\u000b[\tac\u001d;beR\u0014\u0006oY#om\u0006sG-\u00128ea>Lg\u000e\u001e\u000b\u0014G\u0015=R\u0011GC\u001a\u000bk)9$\"\u000f\u0006>\u0015}RQ\t\u0005\u0007I\u0016%\u0002\u0019A\u001e\t\r%,I\u00031\u0001+\u0011\u0019IS\u0011\u0006a\u0001U!1a&\"\u000bA\u0002)Ba\u0001MC\u0015\u0001\u0004Q\u0003\u0002CC\u001e\u000bS\u0001\r!b\n\u0002\u00155\f7\u000f^3s+Jd7\u000fC\u0004\u0003J\u0015%\u0002\u0019A\u001e\t\u0015\u0015\u0005S\u0011\u0006I\u0001\u0002\u0004)\u0019%\u0001\u0007x_J\\WM\u001d(v[\n,'\u000f\u0005\u0003\u000f\u0003/S\u0003\u0002C#\u0006*A\u0005\t\u0019A$\t\u0011\u0015%C\u0011 C\u0001\u000b\u0017\nq#[:Vg\u0016dunY1m\u001d>$WmU*M\u0007>tg-[4\u0015\t\u00055TQ\n\u0005\t\u000b\u001f*9\u00051\u0001\u0006R\u0005\u00191-\u001c3\u0011\t\tEX1K\u0005\u0004\u000b+\"!aB\"p[6\fg\u000e\u001a\u0005\t\u000b3\"I\u0010\"\u0001\u0006\\\u00051R.Y=cKV\u0003H-\u0019;f'Nc5+\u001a;uS:<7\u000f\u0006\u0004\u0006R\u0015uSq\f\u0005\t\u000b\u001f*9\u00061\u0001\u0006R!1Q)b\u0016A\u0002\u001dC!\"b\u0019\u0005zF\u0005I\u0011AC3\u0003\u0001\u001aH/\u0019:u%B\u001cWI\u001c<B]\u0012,e\u000e\u001a9pS:$H\u0005Z3gCVdG\u000f\n\u001d\u0016\u0005\u0015\u001d$\u0006BC\"\u000bSZ#!b\u001b\u0011\t\u00155TqO\u0007\u0003\u000b_RA!\"\u001d\u0006t\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u000bkz\u0011AC1o]>$\u0018\r^5p]&!Q\u0011PC8\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\u000b\u000b{\"I0%A\u0005\u0002\u0015}\u0014\u0001I:uCJ$(\u000b]2F]Z\fe\u000eZ#oIB|\u0017N\u001c;%I\u00164\u0017-\u001e7uIe*\"!\"!+\u0007\u001d+I\u0007\u0003\u0006\u0006\u0006\u0012e\u0018\u0013!C\u0001\u000b\u000f\u000b1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012:TCACEU\rYT\u0011\u000e"
)
public class Worker implements ThreadSafeRpcEndpoint, Logging {
   private final RpcEnv rpcEnv;
   private final int webUiPort;
   public final int org$apache$spark$deploy$worker$Worker$$cores;
   public final int org$apache$spark$deploy$worker$Worker$$memory;
   private final RpcAddress[] masterRpcAddresses;
   private final String workDirPath;
   private final SparkConf conf;
   private final SecurityManager securityMgr;
   private final String org$apache$spark$deploy$worker$Worker$$host;
   private final int org$apache$spark$deploy$worker$Worker$$port;
   private final ScheduledExecutorService org$apache$spark$deploy$worker$Worker$$forwordMessageScheduler;
   private final ExecutionContextExecutorService org$apache$spark$deploy$worker$Worker$$cleanupThreadExecutor;
   private final long HEARTBEAT_MILLIS;
   private final int org$apache$spark$deploy$worker$Worker$$INITIAL_REGISTRATION_RETRIES;
   private final int org$apache$spark$deploy$worker$Worker$$TOTAL_REGISTRATION_RETRIES;
   private final double FUZZ_MULTIPLIER_INTERVAL_LOWER_BOUND;
   private final double REGISTRATION_RETRY_FUZZ_MULTIPLIER;
   private final long INITIAL_REGISTRATION_RETRY_INTERVAL_SECONDS;
   private final long org$apache$spark$deploy$worker$Worker$$PROLONGED_REGISTRATION_RETRY_INTERVAL_SECONDS;
   private final boolean CLEANUP_ENABLED;
   private final long CLEANUP_INTERVAL_MILLIS;
   private final long org$apache$spark$deploy$worker$Worker$$APP_DATA_RETENTION_SECONDS;
   private final boolean testing;
   private Option<RpcEndpointRef> org$apache$spark$deploy$worker$Worker$$master;
   private final boolean org$apache$spark$deploy$worker$Worker$$preferConfiguredMasterAddress;
   private Option<RpcAddress> org$apache$spark$deploy$worker$Worker$$masterAddressToConnect;
   private String org$apache$spark$deploy$worker$Worker$$activeMasterUrl;
   private String activeMasterWebUiUrl;
   private String workerWebUiUrl;
   private final String org$apache$spark$deploy$worker$Worker$$workerUri;
   private boolean org$apache$spark$deploy$worker$Worker$$registered;
   private boolean org$apache$spark$deploy$worker$Worker$$connected;
   private final String org$apache$spark$deploy$worker$Worker$$workerId;
   private final File org$apache$spark$deploy$worker$Worker$$sparkHome;
   private File workDir;
   private final LinkedHashMap<String, ExecutorRunner> finishedExecutors;
   private final HashMap<String, DriverRunner> drivers;
   private final HashMap<String, ExecutorRunner> executors;
   private final LinkedHashMap<String, DriverRunner> finishedDrivers;
   private final HashMap<String, Seq<String>> appDirectories;
   private final HashSet<String> finishedApps;
   private final int retainedExecutors;
   private final int retainedDrivers;
   private final ExternalShuffleService shuffleService;
   private final String org$apache$spark$deploy$worker$Worker$$publicAddress;
   private WorkerWebUI org$apache$spark$deploy$worker$Worker$$webUi;
   private int org$apache$spark$deploy$worker$Worker$$connectionAttemptCount;
   private final MetricsSystem metricsSystem;
   private final WorkerSource workerSource;
   private final boolean reverseProxy;
   private Future<?>[] org$apache$spark$deploy$worker$Worker$$registerMasterFutures;
   private Option<ScheduledFuture<?>> org$apache$spark$deploy$worker$Worker$$registrationRetryTimer;
   private final ThreadPoolExecutor org$apache$spark$deploy$worker$Worker$$registerMasterThreadPool;
   private int coresUsed;
   private int memoryUsed;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static String $lessinit$greater$default$7() {
      return Worker$.MODULE$.$lessinit$greater$default$7();
   }

   public static SparkConf startRpcEnvAndEndpoint$default$9() {
      return Worker$.MODULE$.startRpcEnvAndEndpoint$default$9();
   }

   public static Option<Object> startRpcEnvAndEndpoint$default$8() {
      return Worker$.MODULE$.startRpcEnvAndEndpoint$default$8();
   }

   public static Command maybeUpdateSSLSettings(Command var0, SparkConf var1) {
      return Worker$.MODULE$.maybeUpdateSSLSettings(var0, var1);
   }

   public static boolean isUseLocalNodeSSLConfig(Command var0) {
      return Worker$.MODULE$.isUseLocalNodeSSLConfig(var0);
   }

   public static RpcEnv startRpcEnvAndEndpoint(String var0, int var1, int var2, int var3, int var4, String[] var5, String var6, Option<Object> var7, SparkConf var8) {
      return Worker$.MODULE$.startRpcEnvAndEndpoint(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void main(String[] var0) {
      Worker$.MODULE$.main(var0);
   }

   public static String ENDPOINT_NAME() {
      return Worker$.MODULE$.ENDPOINT_NAME();
   }

   public static String SYSTEM_NAME() {
      return Worker$.MODULE$.SYSTEM_NAME();
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

   public SparkConf conf() {
      return this.conf;
   }

   public SecurityManager securityMgr() {
      return this.securityMgr;
   }

   public String org$apache$spark$deploy$worker$Worker$$host() {
      return this.org$apache$spark$deploy$worker$Worker$$host;
   }

   public int org$apache$spark$deploy$worker$Worker$$port() {
      return this.org$apache$spark$deploy$worker$Worker$$port;
   }

   public ScheduledExecutorService org$apache$spark$deploy$worker$Worker$$forwordMessageScheduler() {
      return this.org$apache$spark$deploy$worker$Worker$$forwordMessageScheduler;
   }

   public ExecutionContextExecutorService org$apache$spark$deploy$worker$Worker$$cleanupThreadExecutor() {
      return this.org$apache$spark$deploy$worker$Worker$$cleanupThreadExecutor;
   }

   private SimpleDateFormat createDateFormat() {
      return new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
   }

   private long HEARTBEAT_MILLIS() {
      return this.HEARTBEAT_MILLIS;
   }

   public int org$apache$spark$deploy$worker$Worker$$INITIAL_REGISTRATION_RETRIES() {
      return this.org$apache$spark$deploy$worker$Worker$$INITIAL_REGISTRATION_RETRIES;
   }

   public int org$apache$spark$deploy$worker$Worker$$TOTAL_REGISTRATION_RETRIES() {
      return this.org$apache$spark$deploy$worker$Worker$$TOTAL_REGISTRATION_RETRIES;
   }

   private double FUZZ_MULTIPLIER_INTERVAL_LOWER_BOUND() {
      return this.FUZZ_MULTIPLIER_INTERVAL_LOWER_BOUND;
   }

   private double REGISTRATION_RETRY_FUZZ_MULTIPLIER() {
      return this.REGISTRATION_RETRY_FUZZ_MULTIPLIER;
   }

   private long INITIAL_REGISTRATION_RETRY_INTERVAL_SECONDS() {
      return this.INITIAL_REGISTRATION_RETRY_INTERVAL_SECONDS;
   }

   public long org$apache$spark$deploy$worker$Worker$$PROLONGED_REGISTRATION_RETRY_INTERVAL_SECONDS() {
      return this.org$apache$spark$deploy$worker$Worker$$PROLONGED_REGISTRATION_RETRY_INTERVAL_SECONDS;
   }

   private boolean CLEANUP_ENABLED() {
      return this.CLEANUP_ENABLED;
   }

   private long CLEANUP_INTERVAL_MILLIS() {
      return this.CLEANUP_INTERVAL_MILLIS;
   }

   public long org$apache$spark$deploy$worker$Worker$$APP_DATA_RETENTION_SECONDS() {
      return this.org$apache$spark$deploy$worker$Worker$$APP_DATA_RETENTION_SECONDS;
   }

   private boolean testing() {
      return this.testing;
   }

   public Option<RpcEndpointRef> org$apache$spark$deploy$worker$Worker$$master() {
      return this.org$apache$spark$deploy$worker$Worker$$master;
   }

   private void org$apache$spark$deploy$worker$Worker$$master_$eq(Option<RpcEndpointRef> x$1) {
      this.org$apache$spark$deploy$worker$Worker$$master = x$1;
   }

   public boolean org$apache$spark$deploy$worker$Worker$$preferConfiguredMasterAddress() {
      return this.org$apache$spark$deploy$worker$Worker$$preferConfiguredMasterAddress;
   }

   public Option<RpcAddress> org$apache$spark$deploy$worker$Worker$$masterAddressToConnect() {
      return this.org$apache$spark$deploy$worker$Worker$$masterAddressToConnect;
   }

   private void org$apache$spark$deploy$worker$Worker$$masterAddressToConnect_$eq(Option<RpcAddress> x$1) {
      this.org$apache$spark$deploy$worker$Worker$$masterAddressToConnect = x$1;
   }

   public String org$apache$spark$deploy$worker$Worker$$activeMasterUrl() {
      return this.org$apache$spark$deploy$worker$Worker$$activeMasterUrl;
   }

   private void org$apache$spark$deploy$worker$Worker$$activeMasterUrl_$eq(String x$1) {
      this.org$apache$spark$deploy$worker$Worker$$activeMasterUrl = x$1;
   }

   public String activeMasterWebUiUrl() {
      return this.activeMasterWebUiUrl;
   }

   public void activeMasterWebUiUrl_$eq(String x$1) {
      this.activeMasterWebUiUrl = x$1;
   }

   private String workerWebUiUrl() {
      return this.workerWebUiUrl;
   }

   private void workerWebUiUrl_$eq(String x$1) {
      this.workerWebUiUrl = x$1;
   }

   public String org$apache$spark$deploy$worker$Worker$$workerUri() {
      return this.org$apache$spark$deploy$worker$Worker$$workerUri;
   }

   public boolean org$apache$spark$deploy$worker$Worker$$registered() {
      return this.org$apache$spark$deploy$worker$Worker$$registered;
   }

   private void org$apache$spark$deploy$worker$Worker$$registered_$eq(boolean x$1) {
      this.org$apache$spark$deploy$worker$Worker$$registered = x$1;
   }

   public boolean org$apache$spark$deploy$worker$Worker$$connected() {
      return this.org$apache$spark$deploy$worker$Worker$$connected;
   }

   private void org$apache$spark$deploy$worker$Worker$$connected_$eq(boolean x$1) {
      this.org$apache$spark$deploy$worker$Worker$$connected = x$1;
   }

   public String org$apache$spark$deploy$worker$Worker$$workerId() {
      return this.org$apache$spark$deploy$worker$Worker$$workerId;
   }

   public File org$apache$spark$deploy$worker$Worker$$sparkHome() {
      return this.org$apache$spark$deploy$worker$Worker$$sparkHome;
   }

   public File workDir() {
      return this.workDir;
   }

   public void workDir_$eq(File x$1) {
      this.workDir = x$1;
   }

   public LinkedHashMap<String, ExecutorRunner> finishedExecutors() {
      return this.finishedExecutors;
   }

   public HashMap<String, DriverRunner> drivers() {
      return this.drivers;
   }

   public HashMap<String, ExecutorRunner> executors() {
      return this.executors;
   }

   public LinkedHashMap<String, DriverRunner> finishedDrivers() {
      return this.finishedDrivers;
   }

   public HashMap<String, Seq<String>> appDirectories() {
      return this.appDirectories;
   }

   public HashSet<String> finishedApps() {
      return this.finishedApps;
   }

   public int retainedExecutors() {
      return this.retainedExecutors;
   }

   public int retainedDrivers() {
      return this.retainedDrivers;
   }

   private ExternalShuffleService shuffleService() {
      return this.shuffleService;
   }

   public String org$apache$spark$deploy$worker$Worker$$publicAddress() {
      return this.org$apache$spark$deploy$worker$Worker$$publicAddress;
   }

   public WorkerWebUI org$apache$spark$deploy$worker$Worker$$webUi() {
      return this.org$apache$spark$deploy$worker$Worker$$webUi;
   }

   private void org$apache$spark$deploy$worker$Worker$$webUi_$eq(WorkerWebUI x$1) {
      this.org$apache$spark$deploy$worker$Worker$$webUi = x$1;
   }

   public int org$apache$spark$deploy$worker$Worker$$connectionAttemptCount() {
      return this.org$apache$spark$deploy$worker$Worker$$connectionAttemptCount;
   }

   public void org$apache$spark$deploy$worker$Worker$$connectionAttemptCount_$eq(int x$1) {
      this.org$apache$spark$deploy$worker$Worker$$connectionAttemptCount = x$1;
   }

   private MetricsSystem metricsSystem() {
      return this.metricsSystem;
   }

   private WorkerSource workerSource() {
      return this.workerSource;
   }

   public boolean reverseProxy() {
      return this.reverseProxy;
   }

   public Future<?>[] org$apache$spark$deploy$worker$Worker$$registerMasterFutures() {
      return this.org$apache$spark$deploy$worker$Worker$$registerMasterFutures;
   }

   public void org$apache$spark$deploy$worker$Worker$$registerMasterFutures_$eq(Future<?>[] x$1) {
      this.org$apache$spark$deploy$worker$Worker$$registerMasterFutures = x$1;
   }

   public Option<ScheduledFuture<?>> org$apache$spark$deploy$worker$Worker$$registrationRetryTimer() {
      return this.org$apache$spark$deploy$worker$Worker$$registrationRetryTimer;
   }

   public void org$apache$spark$deploy$worker$Worker$$registrationRetryTimer_$eq(Option<ScheduledFuture<?>> x$1) {
      this.org$apache$spark$deploy$worker$Worker$$registrationRetryTimer = x$1;
   }

   public ThreadPoolExecutor org$apache$spark$deploy$worker$Worker$$registerMasterThreadPool() {
      return this.org$apache$spark$deploy$worker$Worker$$registerMasterThreadPool;
   }

   public int coresUsed() {
      return this.coresUsed;
   }

   public void coresUsed_$eq(int x$1) {
      this.coresUsed = x$1;
   }

   public int memoryUsed() {
      return this.memoryUsed;
   }

   public void memoryUsed_$eq(int x$1) {
      this.memoryUsed = x$1;
   }

   public int coresFree() {
      return this.org$apache$spark$deploy$worker$Worker$$cores - this.coresUsed();
   }

   public int memoryFree() {
      return this.org$apache$spark$deploy$worker$Worker$$memory - this.memoryUsed();
   }

   private void createWorkDir() {
      this.workDir_$eq((File)Option$.MODULE$.apply(this.workDirPath).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final File apply(String x$1) {
            return new File(x$1);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Worker $outer;

         public final File apply() {
            return new File(this.$outer.org$apache$spark$deploy$worker$Worker$$sparkHome(), "work");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));

      try {
         this.workDir().mkdirs();
         if (!this.workDir().exists() || !this.workDir().isDirectory()) {
            this.logError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final Worker $outer;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Failed to create work directory ").append((Object)this.$outer.workDir()).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            System.exit(1);
         }

         Predef$.MODULE$.assert(this.workDir().isDirectory());
      } catch (Exception var2) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Worker $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Failed to create work directory ").append((Object)this.$outer.workDir()).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, var2);
         System.exit(1);
      }

   }

   public void onStart() {
      Predef$.MODULE$.assert(!this.org$apache$spark$deploy$worker$Worker$$registered());
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Worker $outer;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Starting Spark worker %s:%d with %d cores, %s RAM"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$worker$Worker$$host(), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$deploy$worker$Worker$$port()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$deploy$worker$Worker$$cores), Utils$.MODULE$.megabytesToString((long)this.$outer.org$apache$spark$deploy$worker$Worker$$memory)}));
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
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Worker $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Spark home: ").append((Object)this.$outer.org$apache$spark$deploy$worker$Worker$$sparkHome()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.createWorkDir();
      this.startExternalShuffleService();
      this.org$apache$spark$deploy$worker$Worker$$webUi_$eq(new WorkerWebUI(this, this.workDir(), this.webUiPort));
      this.org$apache$spark$deploy$worker$Worker$$webUi().bind();
      this.workerWebUiUrl_$eq((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"http://", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$deploy$worker$Worker$$publicAddress(), BoxesRunTime.boxToInteger(this.org$apache$spark$deploy$worker$Worker$$webUi().boundPort())})));
      this.org$apache$spark$deploy$worker$Worker$$registerWithMaster();
      this.metricsSystem().registerSource(this.workerSource());
      this.metricsSystem().start();
      ArrayOps var10000 = Predef$.MODULE$.refArrayOps(this.metricsSystem().getServletHandlers());
      WorkerWebUI var1 = this.org$apache$spark$deploy$worker$Worker$$webUi();
      var10000.foreach(new Serializable(this, var1) {
         public static final long serialVersionUID = 0L;
         private final WorkerWebUI eta$0$1$1;

         public final void apply(ServletContextHandler handler) {
            this.eta$0$1$1.attachHandler(handler);
         }

         public {
            this.eta$0$1$1 = eta$0$1$1;
         }
      });
   }

   public void org$apache$spark$deploy$worker$Worker$$changeMaster(RpcEndpointRef masterRef, String uiUrl, RpcAddress masterAddress) {
      this.org$apache$spark$deploy$worker$Worker$$activeMasterUrl_$eq(masterRef.address().toSparkURL());
      this.activeMasterWebUiUrl_$eq(uiUrl);
      this.org$apache$spark$deploy$worker$Worker$$masterAddressToConnect_$eq(new Some(masterAddress));
      this.org$apache$spark$deploy$worker$Worker$$master_$eq(new Some(masterRef));
      this.org$apache$spark$deploy$worker$Worker$$connected_$eq(true);
      if (this.reverseProxy()) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Worker $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"WorkerWebUI is available at ", "/proxy/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.activeMasterWebUiUrl(), this.$outer.org$apache$spark$deploy$worker$Worker$$workerId()}));
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

      this.org$apache$spark$deploy$worker$Worker$$cancelLastRegistrationRetry();
   }

   public Future<?>[] org$apache$spark$deploy$worker$Worker$$tryRegisterAllMasters() {
      return (Future[])Predef$.MODULE$.refArrayOps(this.masterRpcAddresses).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Worker $outer;

         public final Future<?> apply(RpcAddress masterAddress) {
            return this.$outer.org$apache$spark$deploy$worker$Worker$$registerMasterThreadPool().submit(new Runnable(this, masterAddress) {
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               public final RpcAddress masterAddress$3;

               public void run() {
                  label38:
                  try {
                     this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().logInfo(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Connecting to master ").append((Object)this.$outer.masterAddress$3).append((Object)"...").toString();
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     RpcEndpointRef masterEndpoint = this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().rpcEnv().setupEndpointRef(this.masterAddress$3, Master$.MODULE$.ENDPOINT_NAME());
                     this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().org$apache$spark$deploy$worker$Worker$$sendRegisterMessageToMaster(masterEndpoint);
                  } catch (Throwable var8) {
                     if (var8 instanceof InterruptedException) {
                        BoxedUnit var9 = BoxedUnit.UNIT;
                     } else {
                        Option var4 = NonFatal$.MODULE$.unapply(var8);
                        if (var4.isEmpty()) {
                           throw var8;
                        }

                        Throwable e = (Throwable)var4.get();
                        this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().logWarning(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to connect to master ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.masterAddress$3}));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        }, e);
                        BoxedUnit var3 = BoxedUnit.UNIT;
                     }
                     break label38;
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.masterAddress$3 = masterAddress$3;
                  }
               }
            });
         }

         // $FF: synthetic method
         public Worker org$apache$spark$deploy$worker$Worker$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Future.class)));
   }

   public void org$apache$spark$deploy$worker$Worker$$reregisterWithMaster() {
      Utils$.MODULE$.tryOrExit(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Worker $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.org$apache$spark$deploy$worker$Worker$$connectionAttemptCount_$eq(this.$outer.org$apache$spark$deploy$worker$Worker$$connectionAttemptCount() + 1);
            if (this.$outer.org$apache$spark$deploy$worker$Worker$$registered()) {
               this.$outer.org$apache$spark$deploy$worker$Worker$$cancelLastRegistrationRetry();
            } else if (this.$outer.org$apache$spark$deploy$worker$Worker$$connectionAttemptCount() <= this.$outer.org$apache$spark$deploy$worker$Worker$$TOTAL_REGISTRATION_RETRIES()) {
               this.$outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Retrying connection to master (attempt # ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().org$apache$spark$deploy$worker$Worker$$connectionAttemptCount())}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               Option var1 = this.$outer.org$apache$spark$deploy$worker$Worker$$master();
               if (var1 instanceof Some) {
                  Some var2 = (Some)var1;
                  RpcEndpointRef masterRef = (RpcEndpointRef)var2.x();
                  if (this.$outer.org$apache$spark$deploy$worker$Worker$$registerMasterFutures() != null) {
                     Predef$.MODULE$.refArrayOps((Object[])this.$outer.org$apache$spark$deploy$worker$Worker$$registerMasterFutures()).foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final boolean apply(Future<?> x$2) {
                           return x$2.cancel(true);
                        }
                     });
                  }

                  RpcAddress masterAddress = this.$outer.org$apache$spark$deploy$worker$Worker$$preferConfiguredMasterAddress() ? (RpcAddress)this.$outer.org$apache$spark$deploy$worker$Worker$$masterAddressToConnect().get() : masterRef.address();
                  this.$outer.org$apache$spark$deploy$worker$Worker$$registerMasterFutures_$eq((Future[])((Object[])(new Future[]{this.$outer.org$apache$spark$deploy$worker$Worker$$registerMasterThreadPool().submit(new Runnable(this, masterAddress) {
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     public final RpcAddress masterAddress$1;

                     public void run() {
                        label38:
                        try {
                           this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().logInfo(new Serializable(this) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;

                              public final String apply() {
                                 return (new StringBuilder()).append((Object)"Connecting to master ").append((Object)this.$outer.masterAddress$1).append((Object)"...").toString();
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                 }
                              }
                           });
                           RpcEndpointRef masterEndpoint = this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().rpcEnv().setupEndpointRef(this.masterAddress$1, Master$.MODULE$.ENDPOINT_NAME());
                           this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().org$apache$spark$deploy$worker$Worker$$sendRegisterMessageToMaster(masterEndpoint);
                        } catch (Throwable var8) {
                           if (var8 instanceof InterruptedException) {
                              BoxedUnit var9 = BoxedUnit.UNIT;
                           } else {
                              Option var4 = NonFatal$.MODULE$.unapply(var8);
                              if (var4.isEmpty()) {
                                 throw var8;
                              }

                              Throwable e = (Throwable)var4.get();
                              this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().logWarning(new Serializable(this) {
                                 public static final long serialVersionUID = 0L;
                                 // $FF: synthetic field
                                 private final <undefinedtype> $outer;

                                 public final String apply() {
                                    return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to connect to master ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.masterAddress$1}));
                                 }

                                 public {
                                    if ($outer == null) {
                                       throw null;
                                    } else {
                                       this.$outer = $outer;
                                    }
                                 }
                              }, e);
                              BoxedUnit var3 = BoxedUnit.UNIT;
                           }
                           break label38;
                        }

                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.masterAddress$1 = masterAddress$1;
                        }
                     }
                  })})));
                  BoxedUnit var4 = BoxedUnit.UNIT;
               } else {
                  if (!None$.MODULE$.equals(var1)) {
                     throw new MatchError(var1);
                  }

                  if (this.$outer.org$apache$spark$deploy$worker$Worker$$registerMasterFutures() != null) {
                     Predef$.MODULE$.refArrayOps((Object[])this.$outer.org$apache$spark$deploy$worker$Worker$$registerMasterFutures()).foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final boolean apply(Future<?> x$3) {
                           return x$3.cancel(true);
                        }
                     });
                  }

                  this.$outer.org$apache$spark$deploy$worker$Worker$$registerMasterFutures_$eq(this.$outer.org$apache$spark$deploy$worker$Worker$$tryRegisterAllMasters());
                  BoxedUnit var6 = BoxedUnit.UNIT;
               }

               if (this.$outer.org$apache$spark$deploy$worker$Worker$$connectionAttemptCount() == this.$outer.org$apache$spark$deploy$worker$Worker$$INITIAL_REGISTRATION_RETRIES()) {
                  this.$outer.org$apache$spark$deploy$worker$Worker$$registrationRetryTimer().foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final boolean apply(ScheduledFuture<?> x$4) {
                        return x$4.cancel(true);
                     }
                  });
                  this.$outer.org$apache$spark$deploy$worker$Worker$$registrationRetryTimer_$eq(new Some(this.$outer.org$apache$spark$deploy$worker$Worker$$forwordMessageScheduler().scheduleAtFixedRate(new Runnable(this) {
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
                              this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$anon$$$outer().org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().self().send(DeployMessages.ReregisterWithMaster$.MODULE$);
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
                     public <undefinedtype> org$apache$spark$deploy$worker$Worker$$anonfun$$anon$$$outer() {
                        return this.$outer;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  }, this.$outer.org$apache$spark$deploy$worker$Worker$$PROLONGED_REGISTRATION_RETRY_INTERVAL_SECONDS(), this.$outer.org$apache$spark$deploy$worker$Worker$$PROLONGED_REGISTRATION_RETRY_INTERVAL_SECONDS(), TimeUnit.SECONDS)));
               }
            } else {
               this.$outer.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "All masters are unresponsive! Giving up.";
                  }
               });
               System.exit(1);
            }

         }

         // $FF: synthetic method
         public Worker org$apache$spark$deploy$worker$Worker$$anonfun$$$outer() {
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

   public void org$apache$spark$deploy$worker$Worker$$cancelLastRegistrationRetry() {
      if (this.org$apache$spark$deploy$worker$Worker$$registerMasterFutures() != null) {
         Predef$.MODULE$.refArrayOps((Object[])this.org$apache$spark$deploy$worker$Worker$$registerMasterFutures()).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Future<?> x$5) {
               return x$5.cancel(true);
            }
         });
         this.org$apache$spark$deploy$worker$Worker$$registerMasterFutures_$eq((Future[])null);
      }

      this.org$apache$spark$deploy$worker$Worker$$registrationRetryTimer().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ScheduledFuture<?> x$6) {
            return x$6.cancel(true);
         }
      });
      this.org$apache$spark$deploy$worker$Worker$$registrationRetryTimer_$eq(None$.MODULE$);
   }

   public void org$apache$spark$deploy$worker$Worker$$registerWithMaster() {
      Option var1 = this.org$apache$spark$deploy$worker$Worker$$registrationRetryTimer();
      if (None$.MODULE$.equals(var1)) {
         this.org$apache$spark$deploy$worker$Worker$$registered_$eq(false);
         this.org$apache$spark$deploy$worker$Worker$$registerMasterFutures_$eq(this.org$apache$spark$deploy$worker$Worker$$tryRegisterAllMasters());
         this.org$apache$spark$deploy$worker$Worker$$connectionAttemptCount_$eq(0);
         this.org$apache$spark$deploy$worker$Worker$$registrationRetryTimer_$eq(new Some(this.org$apache$spark$deploy$worker$Worker$$forwordMessageScheduler().scheduleAtFixedRate(new Runnable(this) {
            // $FF: synthetic field
            private final Worker $outer;

            public void run() {
               Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     Option$.MODULE$.apply(this.$outer.org$apache$spark$deploy$worker$Worker$$anon$$$outer().self()).foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final void apply(RpcEndpointRef x$7) {
                           x$7.send(DeployMessages.ReregisterWithMaster$.MODULE$);
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
            }

            // $FF: synthetic method
            public Worker org$apache$spark$deploy$worker$Worker$$anon$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, this.INITIAL_REGISTRATION_RETRY_INTERVAL_SECONDS(), this.INITIAL_REGISTRATION_RETRY_INTERVAL_SECONDS(), TimeUnit.SECONDS)));
         BoxedUnit var2 = BoxedUnit.UNIT;
      } else {
         if (!(var1 instanceof Some)) {
            throw new MatchError(var1);
         }

         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Not spawning another attempt to register with the master, since there is an attempt scheduled already.";
            }
         });
         BoxedUnit var3 = BoxedUnit.UNIT;
      }

   }

   private void startExternalShuffleService() {
      try {
         this.shuffleService().startIfEnabled();
      } catch (Exception var2) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Failed to start external shuffle service";
            }
         }, var2);
         System.exit(1);
      }

   }

   public void org$apache$spark$deploy$worker$Worker$$sendRegisterMessageToMaster(RpcEndpointRef masterEndpoint) {
      masterEndpoint.send(new DeployMessages.RegisterWorker(this.org$apache$spark$deploy$worker$Worker$$workerId(), this.org$apache$spark$deploy$worker$Worker$$host(), this.org$apache$spark$deploy$worker$Worker$$port(), this.self(), this.org$apache$spark$deploy$worker$Worker$$cores, this.org$apache$spark$deploy$worker$Worker$$memory, this.workerWebUiUrl(), masterEndpoint.address()));
   }

   public synchronized void org$apache$spark$deploy$worker$Worker$$handleRegisterResponse(DeployMessages.RegisterWorkerResponse msg) {
      if (msg instanceof DeployMessages.RegisteredWorker) {
         DeployMessages.RegisteredWorker var3 = (DeployMessages.RegisteredWorker)msg;
         RpcEndpointRef masterRef = var3.master();
         String masterWebUiUrl = var3.masterWebUiUrl();
         RpcAddress masterAddress = var3.masterAddress();
         if (this.org$apache$spark$deploy$worker$Worker$$preferConfiguredMasterAddress()) {
            this.logInfo(new Serializable(this, masterAddress) {
               public static final long serialVersionUID = 0L;
               private final RpcAddress masterAddress$2;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Successfully registered with master ").append((Object)this.masterAddress$2.toSparkURL()).toString();
               }

               public {
                  this.masterAddress$2 = masterAddress$2;
               }
            });
         } else {
            this.logInfo(new Serializable(this, masterRef) {
               public static final long serialVersionUID = 0L;
               private final RpcEndpointRef masterRef$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Successfully registered with master ").append((Object)this.masterRef$1.address().toSparkURL()).toString();
               }

               public {
                  this.masterRef$1 = masterRef$1;
               }
            });
         }

         this.org$apache$spark$deploy$worker$Worker$$registered_$eq(true);
         this.org$apache$spark$deploy$worker$Worker$$changeMaster(masterRef, masterWebUiUrl, masterAddress);
         this.org$apache$spark$deploy$worker$Worker$$forwordMessageScheduler().scheduleAtFixedRate(new Runnable(this) {
            // $FF: synthetic field
            private final Worker $outer;

            public void run() {
               Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     this.$outer.org$apache$spark$deploy$worker$Worker$$anon$$$outer().self().send(DeployMessages.SendHeartbeat$.MODULE$);
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
            public Worker org$apache$spark$deploy$worker$Worker$$anon$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, 0L, this.HEARTBEAT_MILLIS(), TimeUnit.MILLISECONDS);
         if (this.CLEANUP_ENABLED()) {
            this.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final Worker $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Worker cleanup enabled; old application directories will be deleted in: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.workDir()}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.org$apache$spark$deploy$worker$Worker$$forwordMessageScheduler().scheduleAtFixedRate(new Runnable(this) {
               // $FF: synthetic field
               private final Worker $outer;

               public void run() {
                  Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final void apply() {
                        this.apply$mcV$sp();
                     }

                     public void apply$mcV$sp() {
                        this.$outer.org$apache$spark$deploy$worker$Worker$$anon$$$outer().self().send(DeployMessages.WorkDirCleanup$.MODULE$);
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
               public Worker org$apache$spark$deploy$worker$Worker$$anon$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, this.CLEANUP_INTERVAL_MILLIS(), this.CLEANUP_INTERVAL_MILLIS(), TimeUnit.MILLISECONDS);
         } else {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         Iterable execs = (Iterable)this.executors().values().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final ExecutorDescription apply(ExecutorRunner e) {
               return new ExecutorDescription(e.appId(), e.execId(), e.cores(), e.state());
            }
         }, Iterable$.MODULE$.canBuildFrom());
         masterRef.send(new DeployMessages.WorkerLatestState(this.org$apache$spark$deploy$worker$Worker$$workerId(), execs.toList(), this.drivers().keys().toSeq()));
         BoxedUnit var7 = BoxedUnit.UNIT;
      } else if (msg instanceof DeployMessages.RegisterWorkerFailed) {
         DeployMessages.RegisterWorkerFailed var9 = (DeployMessages.RegisterWorkerFailed)msg;
         String message = var9.message();
         if (this.org$apache$spark$deploy$worker$Worker$$registered()) {
            BoxedUnit var12 = BoxedUnit.UNIT;
         } else {
            this.logError(new Serializable(this, message) {
               public static final long serialVersionUID = 0L;
               private final String message$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Worker registration failed: ").append((Object)this.message$1).toString();
               }

               public {
                  this.message$1 = message$1;
               }
            });
            System.exit(1);
            BoxedUnit var13 = BoxedUnit.UNIT;
         }
      } else {
         if (!DeployMessages.MasterInStandby$.MODULE$.equals(msg)) {
            throw new MatchError(msg);
         }

         BoxedUnit var11 = BoxedUnit.UNIT;
      }

   }

   public synchronized PartialFunction<Object, BoxedUnit> receive() {
      return new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Worker $outer;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var5;
            if (x1 instanceof DeployMessages.RegisterWorkerResponse) {
               DeployMessages.RegisterWorkerResponse var4 = (DeployMessages.RegisterWorkerResponse)x1;
               this.$outer.org$apache$spark$deploy$worker$Worker$$handleRegisterResponse(var4);
               var5 = BoxedUnit.UNIT;
            } else if (DeployMessages.SendHeartbeat$.MODULE$.equals(x1)) {
               BoxedUnit var10000;
               if (this.$outer.org$apache$spark$deploy$worker$Worker$$connected()) {
                  this.$outer.org$apache$spark$deploy$worker$Worker$$sendToMaster(new DeployMessages.Heartbeat(this.$outer.org$apache$spark$deploy$worker$Worker$$workerId(), this.$outer.self()));
                  var10000 = BoxedUnit.UNIT;
               } else {
                  var10000 = BoxedUnit.UNIT;
               }

               var5 = var10000;
            } else if (DeployMessages.WorkDirCleanup$.MODULE$.equals(x1)) {
               Set appIds = ((TraversableOnce)((TraversableLike)this.$outer.executors().values().map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply(ExecutorRunner x$8) {
                     return x$8.appId();
                  }
               }, Iterable$.MODULE$.canBuildFrom())).$plus$plus((GenTraversableOnce)this.$outer.drivers().values().map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply(DriverRunner x$9) {
                     return x$9.driverId();
                  }
               }, Iterable$.MODULE$.canBuildFrom()), Iterable$.MODULE$.canBuildFrom())).toSet();
               scala.concurrent.Future cleanupFuture = Future$.MODULE$.apply(new Serializable(this, appIds) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  public final Set appIds$1;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     File[] appDirs = this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().workDir().listFiles();
                     if (appDirs == null) {
                        throw new IOException((new StringBuilder()).append((Object)"ERROR: Failed to list files in ").append((Object)appDirs).toString());
                     } else {
                        Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])appDirs).filter(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final boolean apply(File dir) {
                              String appIdFromDir = dir.getName();
                              boolean isAppStillRunning = this.$outer.appIds$1.contains(appIdFromDir);
                              return dir.isDirectory() && !isAppStillRunning && !Utils$.MODULE$.doesDirectoryContainAnyNewFiles(dir, this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$anonfun$$$outer().org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().org$apache$spark$deploy$worker$Worker$$APP_DATA_RETENTION_SECONDS());
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
                           private final <undefinedtype> $outer;

                           public final void apply(File dir) {
                              this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$anonfun$$$outer().org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().logInfo(new Serializable(this, dir) {
                                 public static final long serialVersionUID = 0L;
                                 private final File dir$1;

                                 public final String apply() {
                                    return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing directory: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.dir$1.getPath()}));
                                 }

                                 public {
                                    this.dir$1 = dir$1;
                                 }
                              });
                              Utils$.MODULE$.deleteRecursively(dir);
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

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$deploy$worker$Worker$$anonfun$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.appIds$1 = appIds$1;
                     }
                  }
               }, this.$outer.org$apache$spark$deploy$worker$Worker$$cleanupThreadExecutor());
               cleanupFuture.failed().foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(Throwable e) {
                     this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().logError(new Serializable(this, e) {
                        public static final long serialVersionUID = 0L;
                        private final Throwable e$2;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"App dir cleanup failed: ").append((Object)this.e$2.getMessage()).toString();
                        }

                        public {
                           this.e$2 = e$2;
                        }
                     }, e);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, this.$outer.org$apache$spark$deploy$worker$Worker$$cleanupThreadExecutor());
               var5 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.MasterChanged) {
               DeployMessages.MasterChanged var8 = (DeployMessages.MasterChanged)x1;
               RpcEndpointRef masterRef = var8.master();
               String masterWebUiUrl = var8.masterWebUiUrl();
               this.$outer.logInfo(new Serializable(this, masterRef) {
                  public static final long serialVersionUID = 0L;
                  private final RpcEndpointRef masterRef$2;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Master has changed, new master is at ").append((Object)this.masterRef$2.address().toSparkURL()).toString();
                  }

                  public {
                     this.masterRef$2 = masterRef$2;
                  }
               });
               this.$outer.org$apache$spark$deploy$worker$Worker$$changeMaster(masterRef, masterWebUiUrl, masterRef.address());
               Iterable execs = (Iterable)this.$outer.executors().values().map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final ExecutorDescription apply(ExecutorRunner e) {
                     return new ExecutorDescription(e.appId(), e.execId(), e.cores(), e.state());
                  }
               }, Iterable$.MODULE$.canBuildFrom());
               masterRef.send(new DeployMessages.WorkerSchedulerStateResponse(this.$outer.org$apache$spark$deploy$worker$Worker$$workerId(), execs.toList(), this.$outer.drivers().keys().toSeq()));
               var5 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.ReconnectWorker) {
               DeployMessages.ReconnectWorker var12 = (DeployMessages.ReconnectWorker)x1;
               String masterUrl = var12.masterUrl();
               this.$outer.logInfo(new Serializable(this, masterUrl) {
                  public static final long serialVersionUID = 0L;
                  private final String masterUrl$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Master with url ", " requested this worker to reconnect."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.masterUrl$1}));
                  }

                  public {
                     this.masterUrl$1 = masterUrl$1;
                  }
               });
               this.$outer.org$apache$spark$deploy$worker$Worker$$registerWithMaster();
               var5 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.LaunchExecutor) {
               BoxedUnit var69;
               label93: {
                  String masterUrl;
                  label92: {
                     DeployMessages.LaunchExecutor var14 = (DeployMessages.LaunchExecutor)x1;
                     masterUrl = var14.masterUrl();
                     String appId = var14.appId();
                     int execId = var14.execId();
                     ApplicationDescription appDesc = var14.appDesc();
                     int cores_ = var14.cores();
                     int memory_ = var14.memory();
                     String var21 = this.$outer.org$apache$spark$deploy$worker$Worker$$activeMasterUrl();
                     if (masterUrl == null) {
                        if (var21 != null) {
                           break label92;
                        }
                     } else if (!masterUrl.equals(var21)) {
                        break label92;
                     }

                     try {
                        this.$outer.logInfo(new Serializable(this, appId, execId, appDesc) {
                           public static final long serialVersionUID = 0L;
                           private final String appId$1;
                           private final int execId$1;
                           private final ApplicationDescription appDesc$1;

                           public final String apply() {
                              return (new StringOps(Predef$.MODULE$.augmentString("Asked to launch executor %s/%d for %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$1, BoxesRunTime.boxToInteger(this.execId$1), this.appDesc$1.name()}));
                           }

                           public {
                              this.appId$1 = appId$1;
                              this.execId$1 = execId$1;
                              this.appDesc$1 = appDesc$1;
                           }
                        });
                        File executorDir = new File(this.$outer.workDir(), (new StringBuilder()).append((Object)appId).append((Object)"/").append((Object)BoxesRunTime.boxToInteger(execId)).toString());
                        if (!executorDir.mkdirs()) {
                           throw new IOException((new StringBuilder()).append((Object)"Failed to create directory ").append((Object)executorDir).toString());
                        }

                        Seq appLocalDirs = (Seq)this.$outer.appDirectories().getOrElse(appId, new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final Seq<String> apply() {
                              String[] localRootDirs = Utils$.MODULE$.getOrCreateLocalRootDirs(this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().conf());
                              Seq dirs = Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])localRootDirs).flatMap(new Serializable(this) {
                                 public static final long serialVersionUID = 0L;
                                 // $FF: synthetic field
                                 private final <undefinedtype> $outer;

                                 public final Iterable<String> apply(String dir) {
                                    Iterable var10000;
                                    try {
                                       File appDir = Utils$.MODULE$.createDirectory(dir, "executor");
                                       Utils$.MODULE$.chmod700(appDir);
                                       var10000 = Option$.MODULE$.option2Iterable(new Some(appDir.getAbsolutePath()));
                                    } catch (IOException var4) {
                                       this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$anonfun$$$outer().org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().logWarning(new Serializable(this, var4) {
                                          public static final long serialVersionUID = 0L;
                                          private final IOException e$1;

                                          public final String apply() {
                                             return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ". Ignoring this directory."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.e$1.getMessage()}));
                                          }

                                          public {
                                             this.e$1 = e$1;
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
                              }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).toSeq();
                              if (dirs.isEmpty()) {
                                 throw new IOException((new StringBuilder()).append((Object)"No subfolder can be created in ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Predef$.MODULE$.refArrayOps((Object[])localRootDirs).mkString(",")}))).toString());
                              } else {
                                 return dirs;
                              }
                           }

                           // $FF: synthetic method
                           public <undefinedtype> org$apache$spark$deploy$worker$Worker$$anonfun$$anonfun$$$outer() {
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
                        this.$outer.appDirectories().update(appId, appLocalDirs);
                        Command x$21 = Worker$.MODULE$.maybeUpdateSSLSettings(appDesc.command(), this.$outer.conf());
                        String x$22 = appDesc.copy$default$1();
                        Option x$23 = appDesc.copy$default$2();
                        int x$24 = appDesc.copy$default$3();
                        String x$25 = appDesc.copy$default$5();
                        Option x$26 = appDesc.copy$default$6();
                        Option x$27 = appDesc.copy$default$7();
                        Option x$28 = appDesc.copy$default$8();
                        Option x$29 = appDesc.copy$default$9();
                        String x$30 = appDesc.copy$default$10();
                        ExecutorRunner manager = new ExecutorRunner(appId, execId, appDesc.copy(x$22, x$23, x$24, x$21, x$25, x$26, x$27, x$28, x$29, x$30), cores_, memory_, this.$outer.self(), this.$outer.org$apache$spark$deploy$worker$Worker$$workerId(), this.$outer.org$apache$spark$deploy$worker$Worker$$host(), this.$outer.org$apache$spark$deploy$worker$Worker$$webUi().boundPort(), this.$outer.org$apache$spark$deploy$worker$Worker$$publicAddress(), this.$outer.org$apache$spark$deploy$worker$Worker$$sparkHome(), executorDir, this.$outer.org$apache$spark$deploy$worker$Worker$$workerUri(), this.$outer.conf(), appLocalDirs, ExecutorState$.MODULE$.RUNNING());
                        this.$outer.executors().update((new StringBuilder()).append((Object)appId).append((Object)"/").append((Object)BoxesRunTime.boxToInteger(execId)).toString(), manager);
                        manager.start();
                        this.$outer.coresUsed_$eq(this.$outer.coresUsed() + cores_);
                        this.$outer.memoryUsed_$eq(this.$outer.memoryUsed() + memory_);
                        this.$outer.org$apache$spark$deploy$worker$Worker$$sendToMaster(new DeployMessages.ExecutorStateChanged(appId, execId, manager.state(), None$.MODULE$, None$.MODULE$));
                        var69 = BoxedUnit.UNIT;
                     } catch (Exception var65) {
                        this.$outer.logError(new Serializable(this, appId, execId, appDesc) {
                           public static final long serialVersionUID = 0L;
                           private final String appId$1;
                           private final int execId$1;
                           private final ApplicationDescription appDesc$1;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to launch executor ", "/", " for ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$1, BoxesRunTime.boxToInteger(this.execId$1), this.appDesc$1.name()}));
                           }

                           public {
                              this.appId$1 = appId$1;
                              this.execId$1 = execId$1;
                              this.appDesc$1 = appDesc$1;
                           }
                        }, var65);
                        if (this.$outer.executors().contains((new StringBuilder()).append((Object)appId).append((Object)"/").append((Object)BoxesRunTime.boxToInteger(execId)).toString())) {
                           ((ExecutorRunner)this.$outer.executors().apply((new StringBuilder()).append((Object)appId).append((Object)"/").append((Object)BoxesRunTime.boxToInteger(execId)).toString())).kill();
                           this.$outer.executors().$minus$eq((new StringBuilder()).append((Object)appId).append((Object)"/").append((Object)BoxesRunTime.boxToInteger(execId)).toString());
                        } else {
                           var69 = BoxedUnit.UNIT;
                        }

                        this.$outer.org$apache$spark$deploy$worker$Worker$$sendToMaster(new DeployMessages.ExecutorStateChanged(appId, execId, ExecutorState$.MODULE$.FAILED(), new Some(var65.toString()), None$.MODULE$));
                        var69 = BoxedUnit.UNIT;
                     }
                     break label93;
                  }

                  this.$outer.logWarning(new Serializable(this, masterUrl) {
                     public static final long serialVersionUID = 0L;
                     private final String masterUrl$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Invalid Master (").append((Object)this.masterUrl$2).append((Object)") attempted to launch executor.").toString();
                     }

                     public {
                        this.masterUrl$2 = masterUrl$2;
                     }
                  });
                  var69 = BoxedUnit.UNIT;
               }

               var5 = var69;
            } else if (x1 instanceof DeployMessages.ExecutorStateChanged) {
               DeployMessages.ExecutorStateChanged var36 = (DeployMessages.ExecutorStateChanged)x1;
               this.$outer.handleExecutorStateChanged(var36);
               var5 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.KillExecutor) {
               BoxedUnit var70;
               label101: {
                  String masterUrl;
                  int execId;
                  label112: {
                     DeployMessages.KillExecutor var37 = (DeployMessages.KillExecutor)x1;
                     masterUrl = var37.masterUrl();
                     String appId = var37.appId();
                     execId = var37.execId();
                     String var41 = this.$outer.org$apache$spark$deploy$worker$Worker$$activeMasterUrl();
                     if (masterUrl == null) {
                        if (var41 != null) {
                           break label112;
                        }
                     } else if (!masterUrl.equals(var41)) {
                        break label112;
                     }

                     String fullId = (new StringBuilder()).append((Object)appId).append((Object)"/").append((Object)BoxesRunTime.boxToInteger(execId)).toString();
                     Option var43 = this.$outer.executors().get(fullId);
                     if (var43 instanceof Some) {
                        Some var44 = (Some)var43;
                        ExecutorRunner executor = (ExecutorRunner)var44.x();
                        this.$outer.logInfo(new Serializable(this, fullId) {
                           public static final long serialVersionUID = 0L;
                           private final String fullId$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"Asked to kill executor ").append((Object)this.fullId$1).toString();
                           }

                           public {
                              this.fullId$1 = fullId$1;
                           }
                        });
                        executor.kill();
                        BoxedUnit var46 = BoxedUnit.UNIT;
                     } else {
                        if (!None$.MODULE$.equals(var43)) {
                           throw new MatchError(var43);
                        }

                        this.$outer.logInfo(new Serializable(this, fullId) {
                           public static final long serialVersionUID = 0L;
                           private final String fullId$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"Asked to kill unknown executor ").append((Object)this.fullId$1).toString();
                           }

                           public {
                              this.fullId$1 = fullId$1;
                           }
                        });
                        BoxedUnit var66 = BoxedUnit.UNIT;
                     }

                     var70 = BoxedUnit.UNIT;
                     break label101;
                  }

                  this.$outer.logWarning(new Serializable(this, masterUrl, execId) {
                     public static final long serialVersionUID = 0L;
                     private final String masterUrl$3;
                     private final int execId$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Invalid Master (").append((Object)this.masterUrl$3).append((Object)") attempted to kill executor ").append((Object)BoxesRunTime.boxToInteger(this.execId$2)).toString();
                     }

                     public {
                        this.masterUrl$3 = masterUrl$3;
                        this.execId$2 = execId$2;
                     }
                  });
                  var70 = BoxedUnit.UNIT;
               }

               var5 = var70;
            } else if (x1 instanceof DeployMessages.LaunchDriver) {
               DeployMessages.LaunchDriver var47 = (DeployMessages.LaunchDriver)x1;
               String driverId = var47.driverId();
               DriverDescription driverDesc = var47.driverDesc();
               this.$outer.logInfo(new Serializable(this, driverId) {
                  public static final long serialVersionUID = 0L;
                  private final String driverId$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Asked to launch driver ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$1}));
                  }

                  public {
                     this.driverId$1 = driverId$1;
                  }
               });
               SparkConf var10002 = this.$outer.conf();
               File var10004 = this.$outer.workDir();
               File var10005 = this.$outer.org$apache$spark$deploy$worker$Worker$$sparkHome();
               Command x$31 = Worker$.MODULE$.maybeUpdateSSLSettings(driverDesc.command(), this.$outer.conf());
               String x$32 = driverDesc.copy$default$1();
               int x$33 = driverDesc.copy$default$2();
               int x$34 = driverDesc.copy$default$3();
               boolean x$35 = driverDesc.copy$default$4();
               DriverRunner driver = new DriverRunner(var10002, driverId, var10004, var10005, driverDesc.copy(x$32, x$33, x$34, x$35, x$31), this.$outer.self(), this.$outer.org$apache$spark$deploy$worker$Worker$$workerUri(), this.$outer.securityMgr());
               this.$outer.drivers().update(driverId, driver);
               driver.start();
               this.$outer.coresUsed_$eq(this.$outer.coresUsed() + driverDesc.cores());
               this.$outer.memoryUsed_$eq(this.$outer.memoryUsed() + driverDesc.mem());
               var5 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.KillDriver) {
               DeployMessages.KillDriver var56 = (DeployMessages.KillDriver)x1;
               String driverId = var56.driverId();
               this.$outer.logInfo(new Serializable(this, driverId) {
                  public static final long serialVersionUID = 0L;
                  private final String driverId$2;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Asked to kill driver ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$2}));
                  }

                  public {
                     this.driverId$2 = driverId$2;
                  }
               });
               Option var58 = this.$outer.drivers().get(driverId);
               if (var58 instanceof Some) {
                  Some var59 = (Some)var58;
                  DriverRunner runner = (DriverRunner)var59.x();
                  runner.kill();
                  BoxedUnit var61 = BoxedUnit.UNIT;
               } else {
                  if (!None$.MODULE$.equals(var58)) {
                     throw new MatchError(var58);
                  }

                  this.$outer.logError(new Serializable(this, driverId) {
                     public static final long serialVersionUID = 0L;
                     private final String driverId$2;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Asked to kill unknown driver ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$2}));
                     }

                     public {
                        this.driverId$2 = driverId$2;
                     }
                  });
                  BoxedUnit var67 = BoxedUnit.UNIT;
               }

               var5 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.DriverStateChanged) {
               DeployMessages.DriverStateChanged var62 = (DeployMessages.DriverStateChanged)x1;
               this.$outer.handleDriverStateChanged(var62);
               var5 = BoxedUnit.UNIT;
            } else if (DeployMessages.ReregisterWithMaster$.MODULE$.equals(x1)) {
               this.$outer.org$apache$spark$deploy$worker$Worker$$reregisterWithMaster();
               var5 = BoxedUnit.UNIT;
            } else if (x1 instanceof DeployMessages.ApplicationFinished) {
               DeployMessages.ApplicationFinished var63 = (DeployMessages.ApplicationFinished)x1;
               String id = var63.id();
               this.$outer.finishedApps().$plus$eq(id);
               this.$outer.org$apache$spark$deploy$worker$Worker$$maybeCleanupApplication(id);
               var5 = BoxedUnit.UNIT;
            } else {
               var5 = var2.apply(x1);
            }

            return (B1)var5;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (x1 instanceof DeployMessages.RegisterWorkerResponse) {
               var3 = true;
            } else if (DeployMessages.SendHeartbeat$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (DeployMessages.WorkDirCleanup$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.MasterChanged) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.ReconnectWorker) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.LaunchExecutor) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.ExecutorStateChanged) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.KillExecutor) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.LaunchDriver) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.KillDriver) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.DriverStateChanged) {
               var3 = true;
            } else if (DeployMessages.ReregisterWithMaster$.MODULE$.equals(x1)) {
               var3 = true;
            } else if (x1 instanceof DeployMessages.ApplicationFinished) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }

         // $FF: synthetic method
         public Worker org$apache$spark$deploy$worker$Worker$$anonfun$$$outer() {
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
         private final Worker $outer;
         private final RpcCallContext context$1;

         public final <A1, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
            Object var4;
            if (DeployMessages.RequestWorkerState$.MODULE$.equals(x2)) {
               this.context$1.reply(new DeployMessages.WorkerStateResponse(this.$outer.org$apache$spark$deploy$worker$Worker$$host(), this.$outer.org$apache$spark$deploy$worker$Worker$$port(), this.$outer.org$apache$spark$deploy$worker$Worker$$workerId(), this.$outer.executors().values().toList(), this.$outer.finishedExecutors().values().toList(), this.$outer.drivers().values().toList(), this.$outer.finishedDrivers().values().toList(), this.$outer.org$apache$spark$deploy$worker$Worker$$activeMasterUrl(), this.$outer.org$apache$spark$deploy$worker$Worker$$cores, this.$outer.org$apache$spark$deploy$worker$Worker$$memory, this.$outer.coresUsed(), this.$outer.memoryUsed(), this.$outer.activeMasterWebUiUrl()));
               var4 = BoxedUnit.UNIT;
            } else {
               var4 = var2.apply(x2);
            }

            return (B1)var4;
         }

         public final boolean isDefinedAt(Object x2) {
            boolean var3;
            if (DeployMessages.RequestWorkerState$.MODULE$.equals(x2)) {
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

   public void onDisconnected(RpcAddress remoteAddress) {
      if (this.org$apache$spark$deploy$worker$Worker$$master().exists(new Serializable(this, remoteAddress) {
         public static final long serialVersionUID = 0L;
         private final RpcAddress remoteAddress$1;

         public final boolean apply(RpcEndpointRef x$10) {
            boolean var3;
            label23: {
               RpcAddress var10000 = x$10.address();
               RpcAddress var2 = this.remoteAddress$1;
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
            this.remoteAddress$1 = remoteAddress$1;
         }
      }) || this.org$apache$spark$deploy$worker$Worker$$masterAddressToConnect().exists(new Serializable(this, remoteAddress) {
         public static final long serialVersionUID = 0L;
         private final RpcAddress remoteAddress$1;

         public final boolean apply(RpcAddress x$11) {
            boolean var10000;
            label23: {
               RpcAddress var2 = this.remoteAddress$1;
               if (x$11 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (x$11.equals(var2)) {
                  break label23;
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }

         public {
            this.remoteAddress$1 = remoteAddress$1;
         }
      })) {
         this.logInfo(new Serializable(this, remoteAddress) {
            public static final long serialVersionUID = 0L;
            private final RpcAddress remoteAddress$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " Disassociated !"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.remoteAddress$1}));
            }

            public {
               this.remoteAddress$1 = remoteAddress$1;
            }
         });
         this.masterDisconnected();
      }

   }

   private void masterDisconnected() {
      this.logError(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Connection to master failed! Waiting for master to reconnect...";
         }
      });
      this.org$apache$spark$deploy$worker$Worker$$connected_$eq(false);
      this.org$apache$spark$deploy$worker$Worker$$registerWithMaster();
   }

   public void org$apache$spark$deploy$worker$Worker$$maybeCleanupApplication(String id) {
      boolean shouldCleanup = this.finishedApps().contains(id) && !this.executors().values().exists(new Serializable(this, id) {
         public static final long serialVersionUID = 0L;
         private final String id$1;

         public final boolean apply(ExecutorRunner x$12) {
            boolean var3;
            label23: {
               String var10000 = x$12.appId();
               String var2 = this.id$1;
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
            this.id$1 = id$1;
         }
      });
      if (shouldCleanup) {
         this.finishedApps().$minus$eq(id);
         this.appDirectories().remove(id).foreach(new Serializable(this, id) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Worker $outer;
            public final String id$1;

            public final void apply(Seq<String> dirList) {
               Future$.MODULE$.apply(new Serializable(this, dirList) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final Seq dirList$1;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().logInfo(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cleaning up local directories for application ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$anonfun$$$outer().id$1}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     this.dirList$1.foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final void apply(String dir) {
                           Utils$.MODULE$.deleteRecursively(new File(dir));
                        }
                     });
                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$deploy$worker$Worker$$anonfun$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.dirList$1 = dirList$1;
                     }
                  }
               }, this.$outer.org$apache$spark$deploy$worker$Worker$$cleanupThreadExecutor()).failed().foreach(new Serializable(this, dirList) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  public final Seq dirList$1;

                  public final void apply(Throwable e) {
                     this.$outer.org$apache$spark$deploy$worker$Worker$$anonfun$$$outer().logError(new Serializable(this, e) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final Throwable e$3;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Clean up app dir ", " failed: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.dirList$1, this.e$3.getMessage()}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.e$3 = e$3;
                           }
                        }
                     }, e);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.dirList$1 = dirList$1;
                     }
                  }
               }, this.$outer.org$apache$spark$deploy$worker$Worker$$cleanupThreadExecutor());
            }

            // $FF: synthetic method
            public Worker org$apache$spark$deploy$worker$Worker$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.id$1 = id$1;
               }
            }
         });
         this.shuffleService().applicationRemoved(id);
      }

   }

   public void org$apache$spark$deploy$worker$Worker$$sendToMaster(Object message) {
      Option var2 = this.org$apache$spark$deploy$worker$Worker$$master();
      if (var2 instanceof Some) {
         Some var3 = (Some)var2;
         RpcEndpointRef masterRef = (RpcEndpointRef)var3.x();
         masterRef.send(message);
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else {
         if (!None$.MODULE$.equals(var2)) {
            throw new MatchError(var2);
         }

         this.logWarning(new Serializable(this, message) {
            public static final long serialVersionUID = 0L;
            private final Object message$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Dropping ", " because the connection to master has not yet been established"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.message$2}));
            }

            public {
               this.message$2 = message$2;
            }
         });
         BoxedUnit var6 = BoxedUnit.UNIT;
      }

   }

   private String generateWorkerId() {
      return (new StringOps(Predef$.MODULE$.augmentString("worker-%s-%s-%d"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.createDateFormat().format(new Date()), this.org$apache$spark$deploy$worker$Worker$$host(), BoxesRunTime.boxToInteger(this.org$apache$spark$deploy$worker$Worker$$port())}));
   }

   public void onStop() {
      this.org$apache$spark$deploy$worker$Worker$$cleanupThreadExecutor().shutdownNow();
      this.metricsSystem().report();
      this.org$apache$spark$deploy$worker$Worker$$cancelLastRegistrationRetry();
      this.org$apache$spark$deploy$worker$Worker$$forwordMessageScheduler().shutdownNow();
      this.org$apache$spark$deploy$worker$Worker$$registerMasterThreadPool().shutdownNow();
      this.executors().values().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(ExecutorRunner x$13) {
            x$13.kill();
         }
      });
      this.drivers().values().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(DriverRunner x$14) {
            x$14.kill();
         }
      });
      this.shuffleService().stop();
      this.org$apache$spark$deploy$worker$Worker$$webUi().stop();
      this.metricsSystem().stop();
   }

   private void trimFinishedExecutorsIfNecessary() {
      if (this.finishedExecutors().size() > this.retainedExecutors()) {
         ((LinkedHashMap)this.finishedExecutors().take(scala.math.package$.MODULE$.max(this.finishedExecutors().size() / 10, 1))).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Worker $outer;

            public final Option<ExecutorRunner> apply(Tuple2<String, ExecutorRunner> x0$1) {
               if (x0$1 != null) {
                  String executorId = (String)x0$1._1();
                  Option var4 = this.$outer.finishedExecutors().remove(executorId);
                  return var4;
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
      }

   }

   private void trimFinishedDriversIfNecessary() {
      if (this.finishedDrivers().size() > this.retainedDrivers()) {
         ((LinkedHashMap)this.finishedDrivers().take(scala.math.package$.MODULE$.max(this.finishedDrivers().size() / 10, 1))).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Worker $outer;

            public final Option<DriverRunner> apply(Tuple2<String, DriverRunner> x0$2) {
               if (x0$2 != null) {
                  String driverId = (String)x0$2._1();
                  Option var4 = this.$outer.finishedDrivers().remove(driverId);
                  return var4;
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
         });
      }

   }

   public void handleDriverStateChanged(DeployMessages.DriverStateChanged driverStateChanged) {
      String driverId;
      label53: {
         Option exception;
         label55: {
            driverId = driverStateChanged.driverId();
            exception = driverStateChanged.exception();
            Enumeration.Value state = driverStateChanged.state();
            Enumeration.Value var10000 = DriverState$.MODULE$.ERROR();
            if (var10000 == null) {
               if (state == null) {
                  break label55;
               }
            } else if (var10000.equals(state)) {
               break label55;
            }

            label56: {
               var10000 = DriverState$.MODULE$.FAILED();
               if (var10000 == null) {
                  if (state == null) {
                     break label56;
                  }
               } else if (var10000.equals(state)) {
                  break label56;
               }

               label57: {
                  var10000 = DriverState$.MODULE$.FINISHED();
                  if (var10000 == null) {
                     if (state == null) {
                        break label57;
                     }
                  } else if (var10000.equals(state)) {
                     break label57;
                  }

                  label31: {
                     var10000 = DriverState$.MODULE$.KILLED();
                     if (var10000 == null) {
                        if (state == null) {
                           break label31;
                        }
                     } else if (var10000.equals(state)) {
                        break label31;
                     }

                     this.logDebug(new Serializable(this, driverId, state) {
                        public static final long serialVersionUID = 0L;
                        private final String driverId$3;
                        private final Enumeration.Value state$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver ", " changed state to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$3, this.state$1}));
                        }

                        public {
                           this.driverId$3 = driverId$3;
                           this.state$1 = state$1;
                        }
                     });
                     BoxedUnit var7 = BoxedUnit.UNIT;
                     break label53;
                  }

                  this.logInfo(new Serializable(this, driverId) {
                     public static final long serialVersionUID = 0L;
                     private final String driverId$3;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver ", " was killed by user"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$3}));
                     }

                     public {
                        this.driverId$3 = driverId$3;
                     }
                  });
                  BoxedUnit var12 = BoxedUnit.UNIT;
                  break label53;
               }

               this.logInfo(new Serializable(this, driverId) {
                  public static final long serialVersionUID = 0L;
                  private final String driverId$3;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver ", " exited successfully"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$3}));
                  }

                  public {
                     this.driverId$3 = driverId$3;
                  }
               });
               BoxedUnit var13 = BoxedUnit.UNIT;
               break label53;
            }

            this.logWarning(new Serializable(this, driverId) {
               public static final long serialVersionUID = 0L;
               private final String driverId$3;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver ", " exited with failure"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$3}));
               }

               public {
                  this.driverId$3 = driverId$3;
               }
            });
            BoxedUnit var14 = BoxedUnit.UNIT;
            break label53;
         }

         this.logWarning(new Serializable(this, driverId, exception) {
            public static final long serialVersionUID = 0L;
            private final String driverId$3;
            private final Option exception$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Driver ", " failed with unrecoverable exception: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.driverId$3, this.exception$1.get()}));
            }

            public {
               this.driverId$3 = driverId$3;
               this.exception$1 = exception$1;
            }
         });
         BoxedUnit var15 = BoxedUnit.UNIT;
      }

      this.org$apache$spark$deploy$worker$Worker$$sendToMaster(driverStateChanged);
      DriverRunner driver = (DriverRunner)this.drivers().remove(driverId).get();
      this.finishedDrivers().update(driverId, driver);
      this.trimFinishedDriversIfNecessary();
      this.memoryUsed_$eq(this.memoryUsed() - driver.driverDesc().mem());
      this.coresUsed_$eq(this.coresUsed() - driver.driverDesc().cores());
   }

   public void handleExecutorStateChanged(DeployMessages.ExecutorStateChanged executorStateChanged) {
      this.org$apache$spark$deploy$worker$Worker$$sendToMaster(executorStateChanged);
      Enumeration.Value state = executorStateChanged.state();
      if (ExecutorState$.MODULE$.isFinished(state)) {
         String appId = executorStateChanged.appId();
         String fullId = (new StringBuilder()).append((Object)appId).append((Object)"/").append((Object)BoxesRunTime.boxToInteger(executorStateChanged.execId())).toString();
         Option message = executorStateChanged.message();
         Option exitStatus = executorStateChanged.exitStatus();
         Option var7 = this.executors().get(fullId);
         if (var7 instanceof Some) {
            Some var8 = (Some)var7;
            ExecutorRunner executor = (ExecutorRunner)var8.x();
            this.logInfo(new Serializable(this, state, fullId, message, exitStatus) {
               public static final long serialVersionUID = 0L;
               private final Enumeration.Value state$2;
               private final String fullId$2;
               private final Option message$3;
               private final Option exitStatus$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Executor ").append((Object)this.fullId$2).append((Object)" finished with state ").append((Object)this.state$2).append(this.message$3.map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply(String x$15) {
                        return (new StringBuilder()).append((Object)" message ").append((Object)x$15).toString();
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "";
                     }
                  })).append(this.exitStatus$1.map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply(int x$16) {
                        return (new StringBuilder()).append((Object)" exitStatus ").append((Object)BoxesRunTime.boxToInteger(x$16)).toString();
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "";
                     }
                  })).toString();
               }

               public {
                  this.state$2 = state$2;
                  this.fullId$2 = fullId$2;
                  this.message$3 = message$3;
                  this.exitStatus$1 = exitStatus$1;
               }
            });
            this.executors().$minus$eq(fullId);
            this.finishedExecutors().update(fullId, executor);
            this.trimFinishedExecutorsIfNecessary();
            this.coresUsed_$eq(this.coresUsed() - executor.cores());
            this.memoryUsed_$eq(this.memoryUsed() - executor.memory());
            BoxedUnit var10 = BoxedUnit.UNIT;
         } else {
            if (!None$.MODULE$.equals(var7)) {
               throw new MatchError(var7);
            }

            this.logInfo(new Serializable(this, state, fullId, message, exitStatus) {
               public static final long serialVersionUID = 0L;
               private final Enumeration.Value state$2;
               private final String fullId$2;
               private final Option message$3;
               private final Option exitStatus$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Unknown Executor ").append((Object)this.fullId$2).append((Object)" finished with state ").append((Object)this.state$2).append(this.message$3.map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply(String x$17) {
                        return (new StringBuilder()).append((Object)" message ").append((Object)x$17).toString();
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "";
                     }
                  })).append(this.exitStatus$1.map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply(int x$18) {
                        return (new StringBuilder()).append((Object)" exitStatus ").append((Object)BoxesRunTime.boxToInteger(x$18)).toString();
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "";
                     }
                  })).toString();
               }

               public {
                  this.state$2 = state$2;
                  this.fullId$2 = fullId$2;
                  this.message$3 = message$3;
                  this.exitStatus$1 = exitStatus$1;
               }
            });
            BoxedUnit var11 = BoxedUnit.UNIT;
         }

         this.org$apache$spark$deploy$worker$Worker$$maybeCleanupApplication(appId);
      }

   }

   public Worker(RpcEnv rpcEnv, int webUiPort, int cores, int memory, RpcAddress[] masterRpcAddresses, String endpointName, String workDirPath, SparkConf conf, SecurityManager securityMgr) {
      this.rpcEnv = rpcEnv;
      this.webUiPort = webUiPort;
      this.org$apache$spark$deploy$worker$Worker$$cores = cores;
      this.org$apache$spark$deploy$worker$Worker$$memory = memory;
      this.masterRpcAddresses = masterRpcAddresses;
      this.workDirPath = workDirPath;
      this.conf = conf;
      this.securityMgr = securityMgr;
      RpcEndpoint$class.$init$(this);
      Logging$class.$init$(this);
      this.org$apache$spark$deploy$worker$Worker$$host = rpcEnv.address().host();
      this.org$apache$spark$deploy$worker$Worker$$port = rpcEnv.address().port();
      Utils$.MODULE$.checkHost(this.org$apache$spark$deploy$worker$Worker$$host());
      Predef$.MODULE$.assert(this.org$apache$spark$deploy$worker$Worker$$port() > 0);
      this.org$apache$spark$deploy$worker$Worker$$forwordMessageScheduler = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("worker-forward-message-scheduler");
      this.org$apache$spark$deploy$worker$Worker$$cleanupThreadExecutor = ExecutionContext$.MODULE$.fromExecutorService(ThreadUtils$.MODULE$.newDaemonSingleThreadExecutor("worker-cleanup-thread"));
      this.HEARTBEAT_MILLIS = conf.getLong("spark.worker.timeout", 60L) * 1000L / 4L;
      this.org$apache$spark$deploy$worker$Worker$$INITIAL_REGISTRATION_RETRIES = 6;
      this.org$apache$spark$deploy$worker$Worker$$TOTAL_REGISTRATION_RETRIES = this.org$apache$spark$deploy$worker$Worker$$INITIAL_REGISTRATION_RETRIES() + 10;
      this.FUZZ_MULTIPLIER_INTERVAL_LOWER_BOUND = (double)0.5F;
      Random randomNumberGenerator = new Random(UUID.randomUUID().getMostSignificantBits());
      this.REGISTRATION_RETRY_FUZZ_MULTIPLIER = randomNumberGenerator.nextDouble() + this.FUZZ_MULTIPLIER_INTERVAL_LOWER_BOUND();
      this.INITIAL_REGISTRATION_RETRY_INTERVAL_SECONDS = scala.math.package$.MODULE$.round((double)10 * this.REGISTRATION_RETRY_FUZZ_MULTIPLIER());
      this.org$apache$spark$deploy$worker$Worker$$PROLONGED_REGISTRATION_RETRY_INTERVAL_SECONDS = scala.math.package$.MODULE$.round((double)60 * this.REGISTRATION_RETRY_FUZZ_MULTIPLIER());
      this.CLEANUP_ENABLED = conf.getBoolean("spark.worker.cleanup.enabled", false);
      this.CLEANUP_INTERVAL_MILLIS = conf.getLong("spark.worker.cleanup.interval", 1800L) * 1000L;
      this.org$apache$spark$deploy$worker$Worker$$APP_DATA_RETENTION_SECONDS = conf.getLong("spark.worker.cleanup.appDataTtl", 604800L);
      this.testing = scala.sys.package$.MODULE$.props().contains("spark.testing");
      this.org$apache$spark$deploy$worker$Worker$$master = None$.MODULE$;
      this.org$apache$spark$deploy$worker$Worker$$preferConfiguredMasterAddress = conf.getBoolean("spark.worker.preferConfiguredMasterAddress", false);
      this.org$apache$spark$deploy$worker$Worker$$masterAddressToConnect = None$.MODULE$;
      this.org$apache$spark$deploy$worker$Worker$$activeMasterUrl = "";
      this.activeMasterWebUiUrl = "";
      this.workerWebUiUrl = "";
      this.org$apache$spark$deploy$worker$Worker$$workerUri = (new RpcEndpointAddress(rpcEnv.address(), endpointName)).toString();
      this.org$apache$spark$deploy$worker$Worker$$registered = false;
      this.org$apache$spark$deploy$worker$Worker$$connected = false;
      this.org$apache$spark$deploy$worker$Worker$$workerId = this.generateWorkerId();
      File var10001;
      if (this.testing()) {
         Predef$.MODULE$.assert(scala.sys.package$.MODULE$.props().contains("spark.test.home"), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "spark.test.home is not set!";
            }
         });
         var10001 = new File((String)scala.sys.package$.MODULE$.props().apply("spark.test.home"));
      } else {
         var10001 = new File((String)scala.sys.package$.MODULE$.env().get("SPARK_HOME").getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return ".";
            }
         }));
      }

      this.org$apache$spark$deploy$worker$Worker$$sparkHome = var10001;
      this.workDir = null;
      this.finishedExecutors = new LinkedHashMap<String, ExecutorRunner>();
      this.drivers = new HashMap<String, DriverRunner>();
      this.executors = new HashMap<String, ExecutorRunner>();
      this.finishedDrivers = new LinkedHashMap<String, DriverRunner>();
      this.appDirectories = new HashMap<String, Seq<String>>();
      this.finishedApps = new HashSet<String>();
      this.retainedExecutors = conf.getInt("spark.worker.ui.retainedExecutors", WorkerWebUI$.MODULE$.DEFAULT_RETAINED_EXECUTORS());
      this.retainedDrivers = conf.getInt("spark.worker.ui.retainedDrivers", WorkerWebUI$.MODULE$.DEFAULT_RETAINED_DRIVERS());
      this.shuffleService = new ExternalShuffleService(conf, securityMgr);
      String envVar = conf.getenv("SPARK_PUBLIC_DNS");
      this.org$apache$spark$deploy$worker$Worker$$publicAddress = envVar == null ? this.org$apache$spark$deploy$worker$Worker$$host() : envVar;
      this.org$apache$spark$deploy$worker$Worker$$webUi = null;
      this.org$apache$spark$deploy$worker$Worker$$connectionAttemptCount = 0;
      this.metricsSystem = MetricsSystem$.MODULE$.createMetricsSystem("worker", conf, securityMgr);
      this.workerSource = new WorkerSource(this);
      this.reverseProxy = conf.getBoolean("spark.ui.reverseProxy", false);
      this.org$apache$spark$deploy$worker$Worker$$registerMasterFutures = null;
      this.org$apache$spark$deploy$worker$Worker$$registrationRetryTimer = None$.MODULE$;
      this.org$apache$spark$deploy$worker$Worker$$registerMasterThreadPool = ThreadUtils$.MODULE$.newDaemonCachedThreadPool("worker-register-master-threadpool", masterRpcAddresses.length, ThreadUtils$.MODULE$.newDaemonCachedThreadPool$default$3());
      this.coresUsed = 0;
      this.memoryUsed = 0;
   }
}
