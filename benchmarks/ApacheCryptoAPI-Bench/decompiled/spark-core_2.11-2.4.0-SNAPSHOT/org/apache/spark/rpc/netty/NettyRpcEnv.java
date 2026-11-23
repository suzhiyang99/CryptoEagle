package org.apache.spark.rpc.netty;

import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.TransportContext;
import org.apache.spark.network.client.StreamCallback;
import org.apache.spark.network.client.TransportClient;
import org.apache.spark.network.client.TransportClientBootstrap;
import org.apache.spark.network.client.TransportClientFactory;
import org.apache.spark.network.crypto.AuthClientBootstrap;
import org.apache.spark.network.crypto.AuthServerBootstrap;
import org.apache.spark.network.netty.SparkTransportConf$;
import org.apache.spark.network.server.NoOpRpcHandler;
import org.apache.spark.network.server.TransportServer;
import org.apache.spark.network.server.TransportServerBootstrap;
import org.apache.spark.network.util.TransportConf;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcEndpoint;
import org.apache.spark.rpc.RpcEndpointAddress;
import org.apache.spark.rpc.RpcEndpointAddress$;
import org.apache.spark.rpc.RpcEndpointNotFoundException;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcEnvFileServer;
import org.apache.spark.rpc.RpcEnvStoppedException;
import org.apache.spark.rpc.RpcTimeout;
import org.apache.spark.serializer.JavaSerializerInstance;
import org.apache.spark.serializer.SerializationStream;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.concurrent.Promise;
import scala.concurrent.Promise$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;
import scala.util.Try$;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0011ma!B\u0001\u0003\u0001\ta!a\u0003(fiRL(\u000b]2F]ZT!a\u0001\u0003\u0002\u000b9,G\u000f^=\u000b\u0005\u00151\u0011a\u0001:qG*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0002\u0001\u001bE\u0001\"AD\b\u000e\u0003\u0011I!\u0001\u0005\u0003\u0003\rI\u00038-\u00128w!\t\u0011R#D\u0001\u0014\u0015\t!b!\u0001\u0005j]R,'O\\1m\u0013\t12CA\u0004M_\u001e<\u0017N\\4\t\u0011a\u0001!Q1A\u0005\u0002i\tAaY8oM\u000e\u0001Q#A\u000e\u0011\u0005qiR\"\u0001\u0004\n\u0005y1!!C*qCJ\\7i\u001c8g\u0011!\u0001\u0003A!A!\u0002\u0013Y\u0012!B2p]\u001a\u0004\u0003\u0002\u0003\u0012\u0001\u0005\u0003\u0005\u000b\u0011B\u0012\u0002-)\fg/Y*fe&\fG.\u001b>fe&s7\u000f^1oG\u0016\u0004\"\u0001J\u0014\u000e\u0003\u0015R!A\n\u0004\u0002\u0015M,'/[1mSj,'/\u0003\u0002)K\t1\"*\u0019<b'\u0016\u0014\u0018.\u00197ju\u0016\u0014\u0018J\\:uC:\u001cW\r\u0003\u0005+\u0001\t\u0005\t\u0015!\u0003,\u0003\u0011Awn\u001d;\u0011\u00051\u0012dBA\u00171\u001b\u0005q#\"A\u0018\u0002\u000bM\u001c\u0017\r\\1\n\u0005Er\u0013A\u0002)sK\u0012,g-\u0003\u00024i\t11\u000b\u001e:j]\u001eT!!\r\u0018\t\u0011Y\u0002!\u0011!Q\u0001\n]\nqb]3dkJLG/_'b]\u0006<WM\u001d\t\u00039aJ!!\u000f\u0004\u0003\u001fM+7-\u001e:jifl\u0015M\\1hKJD\u0001b\u000f\u0001\u0003\u0002\u0003\u0006I\u0001P\u0001\u000f]VlWk]1cY\u0016\u001cuN]3t!\tiS(\u0003\u0002?]\t\u0019\u0011J\u001c;\t\u000b\u0001\u0003A\u0011A!\u0002\rqJg.\u001b;?)\u0019\u0011E)\u0012$H\u0011B\u00111\tA\u0007\u0002\u0005!)\u0001d\u0010a\u00017!)!e\u0010a\u0001G!)!f\u0010a\u0001W!)ag\u0010a\u0001o!)1h\u0010a\u0001y!A!\n\u0001b\u0001\n\u0003\u00111*A\u0007ue\u0006t7\u000f]8si\u000e{gNZ\u000b\u0002\u0019B\u0011QJU\u0007\u0002\u001d*\u0011q\nU\u0001\u0005kRLGN\u0003\u0002R\r\u00059a.\u001a;x_J\\\u0017BA*O\u00055!&/\u00198ta>\u0014HoQ8oM\"1Q\u000b\u0001Q\u0001\n1\u000ba\u0002\u001e:b]N\u0004xN\u001d;D_:4\u0007\u0005C\u0004X\u0001\t\u0007I\u0011\u0002-\u0002\u0015\u0011L7\u000f]1uG\",'/F\u0001Z!\t\u0019%,\u0003\u0002\\\u0005\tQA)[:qCR\u001c\u0007.\u001a:\t\ru\u0003\u0001\u0015!\u0003Z\u0003-!\u0017n\u001d9bi\u000eDWM\u001d\u0011\t\u000f}\u0003!\u0019!C\u0005A\u0006i1\u000f\u001e:fC6l\u0015M\\1hKJ,\u0012!\u0019\t\u0003\u0007\nL!a\u0019\u0002\u0003%9+G\u000f^=TiJ,\u0017-\\'b]\u0006<WM\u001d\u0005\u0007K\u0002\u0001\u000b\u0011B1\u0002\u001dM$(/Z1n\u001b\u0006t\u0017mZ3sA!9q\r\u0001b\u0001\n\u0013A\u0017\u0001\u0005;sC:\u001c\bo\u001c:u\u0007>tG/\u001a=u+\u0005I\u0007C\u00016l\u001b\u0005\u0001\u0016B\u00017Q\u0005A!&/\u00198ta>\u0014HoQ8oi\u0016DH\u000f\u0003\u0004o\u0001\u0001\u0006I![\u0001\u0012iJ\fgn\u001d9peR\u001cuN\u001c;fqR\u0004\u0003\"\u00029\u0001\t\u0013\t\u0018AF2sK\u0006$Xm\u00117jK:$(i\\8ugR\u0014\u0018\r]:\u0015\u0003I\u00042a]<z\u001b\u0005!(BA(v\u0015\u00051\u0018\u0001\u00026bm\u0006L!\u0001\u001f;\u0003\t1K7\u000f\u001e\t\u0003uvl\u0011a\u001f\u0006\u0003yB\u000baa\u00197jK:$\u0018B\u0001@|\u0005a!&/\u00198ta>\u0014Ho\u00117jK:$(i\\8ugR\u0014\u0018\r\u001d\u0005\n\u0003\u0003\u0001!\u0019!C\u0005\u0003\u0007\tQb\u00197jK:$h)Y2u_JLXCAA\u0003!\rQ\u0018qA\u0005\u0004\u0003\u0013Y(A\u0006+sC:\u001c\bo\u001c:u\u00072LWM\u001c;GC\u000e$xN]=\t\u0011\u00055\u0001\u0001)A\u0005\u0003\u000b\tab\u00197jK:$h)Y2u_JL\b\u0005C\u0006\u0002\u0012\u0001\u0001\r\u00111A\u0005\n\u0005\r\u0011a\u00054jY\u0016$un\u001e8m_\u0006$g)Y2u_JL\bbCA\u000b\u0001\u0001\u0007\t\u0019!C\u0005\u0003/\tqCZ5mK\u0012{wO\u001c7pC\u00124\u0015m\u0019;pef|F%Z9\u0015\t\u0005e\u0011q\u0004\t\u0004[\u0005m\u0011bAA\u000f]\t!QK\\5u\u0011)\t\t#a\u0005\u0002\u0002\u0003\u0007\u0011QA\u0001\u0004q\u0012\n\u0004\u0002CA\u0013\u0001\u0001\u0006K!!\u0002\u0002)\u0019LG.\u001a#po:dw.\u00193GC\u000e$xN]=!Q\u0011\t\u0019#!\u000b\u0011\u00075\nY#C\u0002\u0002.9\u0012\u0001B^8mCRLG.\u001a\u0005\n\u0003c\u0001!\u0019!C\u0001\u0003g\t\u0001\u0003^5nK>,HoU2iK\u0012,H.\u001a:\u0016\u0005\u0005U\u0002\u0003BA\u001c\u0003{i!!!\u000f\u000b\u0007\u0005mB/\u0001\u0006d_:\u001cWO\u001d:f]RLA!a\u0010\u0002:\tA2k\u00195fIVdW\rZ#yK\u000e,Ho\u001c:TKJ4\u0018nY3\t\u0011\u0005\r\u0003\u0001)A\u0005\u0003k\t\u0011\u0003^5nK>,HoU2iK\u0012,H.\u001a:!\u0011)\t9\u0005\u0001b\u0001\n\u0003\u0011\u0011\u0011J\u0001\u0019G2LWM\u001c;D_:tWm\u0019;j_:,\u00050Z2vi>\u0014XCAA&!\u0011\t9$!\u0014\n\t\u0005=\u0013\u0011\b\u0002\u0013)\"\u0014X-\u00193Q_>dW\t_3dkR|'\u000f\u0003\u0005\u0002T\u0001\u0001\u000b\u0011BA&\u0003e\u0019G.[3oi\u000e{gN\\3di&|g.\u0012=fGV$xN\u001d\u0011\t\u0017\u0005]\u0003\u00011AA\u0002\u0013%\u0011\u0011L\u0001\u0007g\u0016\u0014h/\u001a:\u0016\u0005\u0005m\u0003\u0003BA/\u0003Cj!!a\u0018\u000b\u0007\u0005]\u0003+\u0003\u0003\u0002d\u0005}#a\u0004+sC:\u001c\bo\u001c:u'\u0016\u0014h/\u001a:\t\u0017\u0005\u001d\u0004\u00011AA\u0002\u0013%\u0011\u0011N\u0001\u000bg\u0016\u0014h/\u001a:`I\u0015\fH\u0003BA\r\u0003WB!\"!\t\u0002f\u0005\u0005\t\u0019AA.\u0011!\ty\u0007\u0001Q!\n\u0005m\u0013aB:feZ,'\u000f\t\u0015\u0005\u0003[\nI\u0003C\u0005\u0002v\u0001\u0011\r\u0011\"\u0003\u0002x\u000591\u000f^8qa\u0016$WCAA=!\u0011\tY(!!\u000e\u0005\u0005u$\u0002BA@\u0003s\ta!\u0019;p[&\u001c\u0017\u0002BAB\u0003{\u0012Q\"\u0011;p[&\u001c'i\\8mK\u0006t\u0007\u0002CAD\u0001\u0001\u0006I!!\u001f\u0002\u0011M$x\u000e\u001d9fI\u0002B\u0011\"a#\u0001\u0005\u0004%I!!$\u0002\u0011=,HOY8yKN,\"!a$\u0011\u0011\u0005]\u0012\u0011SAK\u00037KA!a%\u0002:\t\t2i\u001c8dkJ\u0014XM\u001c;ICNDW*\u00199\u0011\u00079\t9*C\u0002\u0002\u001a\u0012\u0011!B\u00159d\u0003\u0012$'/Z:t!\r\u0019\u0015QT\u0005\u0004\u0003?\u0013!AB(vi\n|\u0007\u0010\u0003\u0005\u0002$\u0002\u0001\u000b\u0011BAH\u0003%yW\u000f\u001e2pq\u0016\u001c\b\u0005\u0003\u0005\u0002(\u0002!\tAAAU\u00031\u0011X-\\8wK>+HOY8y)\u0011\tI\"a+\t\u0011\u00055\u0016Q\u0015a\u0001\u0003+\u000bq!\u00193ee\u0016\u001c8\u000fC\u0004\u00022\u0002!\t!a-\u0002\u0017M$\u0018M\u001d;TKJ4XM\u001d\u000b\u0007\u00033\t),!/\t\u000f\u0005]\u0016q\u0016a\u0001W\u0005Y!-\u001b8e\u0003\u0012$'/Z:t\u0011\u001d\tY,a,A\u0002q\nA\u0001]8si\"Q\u0011Q\u0016\u0001\t\u0006\u0004%\t%a0\u0016\u0005\u0005U\u0005BCAb\u0001!\u0005\t\u0015)\u0003\u0002\u0016\u0006A\u0011\r\u001a3sKN\u001c\b\u0005\u000b\u0003\u0002B\u0006\u001d\u0007\u0003BAe\u0003'l!!a3\u000b\t\u00055\u0017qZ\u0001\u000bC:tw\u000e^1uS>t'BAAi\u0003\u0015Q\u0017M^1y\u0013\u0011\t).a3\u0003\u00119+H\u000e\\1cY\u0016Dq!!7\u0001\t\u0003\nY.A\u0007tKR,\b/\u00128ea>Lg\u000e\u001e\u000b\u0007\u0003;\f\u0019/a:\u0011\u00079\ty.C\u0002\u0002b\u0012\u0011aB\u00159d\u000b:$\u0007o\\5oiJ+g\rC\u0004\u0002f\u0006]\u0007\u0019A\u0016\u0002\t9\fW.\u001a\u0005\t\u0003S\f9\u000e1\u0001\u0002l\u0006AQM\u001c3q_&tG\u000fE\u0002\u000f\u0003[L1!a<\u0005\u0005-\u0011\u0006oY#oIB|\u0017N\u001c;\t\u000f\u0005M\b\u0001\"\u0001\u0002v\u0006Q\u0012m]=oGN+G/\u001e9F]\u0012\u0004x.\u001b8u%\u00164')_+S\u0013R!\u0011q\u001fB\u0001!\u0019\tI0!@\u0002^6\u0011\u00111 \u0006\u0004\u0003wq\u0013\u0002BA\u0000\u0003w\u0014aAR;ukJ,\u0007b\u0002B\u0002\u0003c\u0004\raK\u0001\u0004kJL\u0007b\u0002B\u0004\u0001\u0011\u0005#\u0011B\u0001\u0005gR|\u0007\u000f\u0006\u0003\u0002\u001a\t-\u0001\u0002\u0003B\u0007\u0005\u000b\u0001\r!!8\u0002\u0017\u0015tG\r]8j]R\u0014VM\u001a\u0005\b\u0005#\u0001A\u0011\u0002B\n\u00031\u0001xn\u001d;U_>+HOY8y)\u0019\tIB!\u0006\u0003 !A!q\u0003B\b\u0001\u0004\u0011I\"\u0001\u0005sK\u000e,\u0017N^3s!\r\u0019%1D\u0005\u0004\u0005;\u0011!a\u0005(fiRL(\u000b]2F]\u0012\u0004x.\u001b8u%\u00164\u0007\u0002\u0003B\u0011\u0005\u001f\u0001\rAa\t\u0002\u000f5,7o]1hKB\u00191I!\n\n\u0007\t\u001d\"AA\u0007PkR\u0014w\u000e_'fgN\fw-\u001a\u0005\t\u0005W\u0001A\u0011\u0001\u0002\u0003.\u0005!1/\u001a8e)\u0011\tIBa\f\t\u0011\t\u0005\"\u0011\u0006a\u0001\u0005c\u00012a\u0011B\u001a\u0013\r\u0011)D\u0001\u0002\u000f%\u0016\fX/Z:u\u001b\u0016\u001c8/Y4f\u0011!\u0011I\u0004\u0001C\u0001\u0005\tm\u0012\u0001D2sK\u0006$Xm\u00117jK:$H\u0003\u0002B\u001f\u0005\u0007\u00022A\u001fB \u0013\r\u0011\te\u001f\u0002\u0010)J\fgn\u001d9peR\u001cE.[3oi\"A\u0011Q\u0016B\u001c\u0001\u0004\t)\n\u0003\u0005\u0003H\u0001!\tA\u0001B%\u0003\r\t7o[\u000b\u0005\u0005\u0017\u0012)\u0006\u0006\u0004\u0003N\t]$\u0011\u0010\u000b\u0005\u0005\u001f\u00129\u0007\u0005\u0004\u0002z\u0006u(\u0011\u000b\t\u0005\u0005'\u0012)\u0006\u0004\u0001\u0005\u0011\t]#Q\tb\u0001\u00053\u0012\u0011\u0001V\t\u0005\u00057\u0012\t\u0007E\u0002.\u0005;J1Aa\u0018/\u0005\u001dqu\u000e\u001e5j]\u001e\u00042!\fB2\u0013\r\u0011)G\f\u0002\u0004\u0003:L\bB\u0003B5\u0005\u000b\n\t\u0011q\u0001\u0003l\u0005QQM^5eK:\u001cW\rJ\u0019\u0011\r\t5$1\u000fB)\u001b\t\u0011yGC\u0002\u0003r9\nqA]3gY\u0016\u001cG/\u0003\u0003\u0003v\t=$\u0001C\"mCN\u001cH+Y4\t\u0011\t\u0005\"Q\ta\u0001\u0005cA\u0001Ba\u001f\u0003F\u0001\u0007!QP\u0001\bi&lWm\\;u!\rq!qP\u0005\u0004\u0005\u0003#!A\u0003*qGRKW.Z8vi\"A!Q\u0011\u0001\u0005\u0002\t\u00119)A\u0005tKJL\u0017\r\\5{KR!!\u0011\u0012BK!\u0011\u0011YI!%\u000e\u0005\t5%b\u0001BHk\u0006\u0019a.[8\n\t\tM%Q\u0012\u0002\u000b\u0005f$XMQ;gM\u0016\u0014\b\u0002\u0003BL\u0005\u0007\u0003\rA!\u0019\u0002\u000f\r|g\u000e^3oi\"A!1\u0014\u0001\u0005\u0002\t\u0011i*A\btKJL\u0017\r\\5{KN#(/Z1n)\u0011\u0011yJ!*\u0011\u0007\u0011\u0012\t+C\u0002\u0003$\u0016\u00121cU3sS\u0006d\u0017N_1uS>t7\u000b\u001e:fC6D\u0001Ba*\u0003\u001a\u0002\u0007!\u0011V\u0001\u0004_V$\b\u0003\u0002BV\u0005ck!A!,\u000b\u0007\t=V/\u0001\u0002j_&!!1\u0017BW\u00051yU\u000f\u001e9viN#(/Z1n\u0011!\u00119\f\u0001C\u0001\u0005\te\u0016a\u00033fg\u0016\u0014\u0018.\u00197ju\u0016,BAa/\u0003BR1!Q\u0018Be\u0005\u0017$BAa0\u0003DB!!1\u000bBa\t!\u00119F!.C\u0002\te\u0003B\u0003Bc\u0005k\u000b\t\u0011q\u0001\u0003H\u0006QQM^5eK:\u001cW\r\n\u001a\u0011\r\t5$1\u000fB`\u0011\u001da(Q\u0017a\u0001\u0005{A\u0001B!4\u00036\u0002\u0007!\u0011R\u0001\u0006Ef$Xm\u001d\u0005\b\u0005\u001b\u0001A\u0011\tBi)\u0011\tiNa5\t\u0011\u0005%(q\u001aa\u0001\u0003WDqAa6\u0001\t\u0003\u0012I.\u0001\u0005tQV$Hm\\<o)\t\tI\u0002C\u0004\u0003^\u0002!\tE!7\u0002!\u0005<\u0018-\u001b;UKJl\u0017N\\1uS>t\u0007b\u0002Bq\u0001\u0011%!\u0011\\\u0001\bG2,\u0017M\\;q\u0011\u001d\u00119\f\u0001C!\u0005K,BAa:\u0003lR!!\u0011\u001eBw!\u0011\u0011\u0019Fa;\u0005\u0011\t]#1\u001db\u0001\u00053B\u0001Ba<\u0003d\u0002\u0007!\u0011_\u0001\u0016I\u0016\u001cXM]5bY&T\u0018\r^5p]\u0006\u001bG/[8o!\u0015i#1\u001fBu\u0013\r\u0011)P\f\u0002\n\rVt7\r^5p]BBqA!?\u0001\t\u0003\u0012Y0\u0001\u0006gS2,7+\u001a:wKJ,\"A!@\u0011\u00079\u0011y0C\u0002\u0004\u0002\u0011\u0011\u0001C\u00159d\u000b:4h)\u001b7f'\u0016\u0014h/\u001a:\t\u000f\r\u0015\u0001\u0001\"\u0011\u0004\b\u0005Yq\u000e]3o\u0007\"\fgN\\3m)\u0011\u0019Ia!\u0006\u0011\t\r-1\u0011C\u0007\u0003\u0007\u001bQAaa\u0004\u0003\u000e\u0006A1\r[1o]\u0016d7/\u0003\u0003\u0004\u0014\r5!a\u0005*fC\u0012\f'\r\\3CsR,7\t[1o]\u0016d\u0007b\u0002B\u0002\u0007\u0007\u0001\ra\u000b\u0005\b\u00073\u0001A\u0011BB\u000e\u00039!wn\u001e8m_\u0006$7\t\\5f]R$bA!\u0010\u0004\u001e\r}\u0001B\u0002\u0016\u0004\u0018\u0001\u00071\u0006C\u0004\u0002<\u000e]\u0001\u0019\u0001\u001f\u0007\r\r\r\u0002\u0001BB\u0013\u0005M1\u0015\u000e\\3E_^tGn\\1e\u0007\"\fgN\\3m'\u0019\u0019\tca\n\u0004\nA!1\u0011FB\u0018\u001b\t\u0019YCC\u0002\u0004.U\fA\u0001\\1oO&!1\u0011GB\u0016\u0005\u0019y%M[3di\"Y1QGB\u0011\u0005\u0003\u0005\u000b\u0011BB\u001c\u0003\u0019\u0019x.\u001e:dKB!1\u0011HB \u001d\u0011\u0019Yaa\u000f\n\t\ru2QB\u0001\u0005!&\u0004X-\u0003\u0003\u0004B\r\r#!D*pkJ\u001cWm\u00115b]:,GN\u0003\u0003\u0004>\r5\u0001b\u0002!\u0004\"\u0011\u00051q\t\u000b\u0005\u0007\u0013\u001ai\u0005\u0005\u0003\u0004L\r\u0005R\"\u0001\u0001\t\u0011\rU2Q\ta\u0001\u0007oAAb!\u0015\u0004\"\u0001\u0007\t\u0019!C\u0005\u0007'\nQ!\u001a:s_J,\"a!\u0016\u0011\t\r]3q\r\b\u0005\u00073\u001a\u0019G\u0004\u0003\u0004\\\r\u0005TBAB/\u0015\r\u0019y&G\u0001\u0007yI|w\u000e\u001e \n\u0003=J1a!\u001a/\u0003\u001d\u0001\u0018mY6bO\u0016LAa!\u001b\u0004l\tIA\u000b\u001b:po\u0006\u0014G.\u001a\u0006\u0004\u0007Kr\u0003\u0002DB8\u0007C\u0001\r\u00111A\u0005\n\rE\u0014!C3se>\u0014x\fJ3r)\u0011\tIba\u001d\t\u0015\u0005\u00052QNA\u0001\u0002\u0004\u0019)\u0006C\u0005\u0004x\r\u0005\u0002\u0015)\u0003\u0004V\u00051QM\u001d:pe\u0002BCa!\u001e\u0002*!A1QPB\u0011\t\u0003\u0019y(\u0001\u0005tKR,%O]8s)\u0011\tIb!!\t\u0011\r\r51\u0010a\u0001\u0007+\n\u0011!\u001a\u0005\t\u0007\u000f\u001b\t\u0003\"\u0011\u0004\n\u0006!!/Z1e)\ra41\u0012\u0005\t\u0007\u001b\u001b)\t1\u0001\u0003\n\u0006\u0019Am\u001d;\t\u0011\rE5\u0011\u0005C!\u00053\fQa\u00197pg\u0016D\u0001b!&\u0004\"\u0011\u00053qS\u0001\u0007SN|\u0005/\u001a8\u0015\u0005\re\u0005cA\u0017\u0004\u001c&\u00191Q\u0014\u0018\u0003\u000f\t{w\u000e\\3b]\u001a11\u0011\u0015\u0001\u0005\u0007G\u0013ACR5mK\u0012{wO\u001c7pC\u0012\u001c\u0015\r\u001c7cC\u000e\\7CBBP\u0007O\u0019)\u000bE\u0002{\u0007OK1a!+|\u00059\u0019FO]3b[\u000e\u000bG\u000e\u001c2bG.D1b!,\u0004 \n\u0005\t\u0015!\u0003\u00040\u0006!1/\u001b8l!\u0011\u0019Ya!-\n\t\rM6Q\u0002\u0002\u0014/JLG/\u00192mK\nKH/Z\"iC:tW\r\u001c\u0005\f\u0007k\u0019yJ!A!\u0002\u0013\u0019I\u0005\u0003\u0006}\u0007?\u0013\t\u0011)A\u0005\u0005{Aq\u0001QBP\t\u0003\u0019Y\f\u0006\u0005\u0004>\u000e}6\u0011YBb!\u0011\u0019Yea(\t\u0011\r56\u0011\u0018a\u0001\u0007_C\u0001b!\u000e\u0004:\u0002\u00071\u0011\n\u0005\by\u000ee\u0006\u0019\u0001B\u001f\u0011!\u00199ma(\u0005B\r%\u0017AB8o\t\u0006$\u0018\r\u0006\u0004\u0002\u001a\r-7q\u001a\u0005\b\u0007\u001b\u001c)\r1\u0001,\u0003!\u0019HO]3b[&#\u0007\u0002CBi\u0007\u000b\u0004\rA!#\u0002\u0007\t,h\r\u0003\u0005\u0004V\u000e}E\u0011IBl\u0003)ygnQ8na2,G/\u001a\u000b\u0005\u00033\u0019I\u000eC\u0004\u0004N\u000eM\u0007\u0019A\u0016\t\u0011\ru7q\u0014C!\u0007?\f\u0011b\u001c8GC&dWO]3\u0015\r\u0005e1\u0011]Br\u0011\u001d\u0019ima7A\u0002-B\u0001b!:\u0004\\\u0002\u00071QK\u0001\u0006G\u0006,8/Z\u0004\t\u0007S\u0014\u0001\u0012\u0001\u0002\u0004l\u0006Ya*\u001a;usJ\u00038-\u00128w!\r\u00195Q\u001e\u0004\b\u0003\tA\tAABx'\u0015\u0019io!=\u0012!\ri31_\u0005\u0004\u0007kt#AB!osJ+g\rC\u0004A\u0007[$\ta!?\u0015\u0005\r-\bbCB\u007f\u0007[\u0014\r\u0011\"\u0001\u0003\u0007\u007f\f!bY;se\u0016tG/\u00128w+\t!\t\u0001E\u0003\u0005\u0004\u0011\u001d!)\u0004\u0002\u0005\u0006)\u0011qJL\u0005\u0005\t\u0013!)AA\bEs:\fW.[2WCJL\u0017M\u00197f\u0011%!ia!<!\u0002\u0013!\t!A\u0006dkJ\u0014XM\u001c;F]Z\u0004\u0003b\u0003C\t\u0007[\u0014\r\u0011\"\u0001\u0003\t'\tQbY;se\u0016tGo\u00117jK:$XC\u0001C\u000b!\u0019!\u0019\u0001b\u0002\u0003>!IA\u0011DBwA\u0003%AQC\u0001\u000fGV\u0014(/\u001a8u\u00072LWM\u001c;!\u0001"
)
public class NettyRpcEnv extends RpcEnv implements Logging {
   private final SparkConf conf;
   public final JavaSerializerInstance org$apache$spark$rpc$netty$NettyRpcEnv$$javaSerializerInstance;
   private final String host;
   private final SecurityManager securityManager;
   private final TransportConf transportConf;
   private final Dispatcher dispatcher;
   private final NettyStreamManager streamManager;
   private final TransportContext transportContext;
   private final TransportClientFactory clientFactory;
   private volatile TransportClientFactory fileDownloadFactory;
   private final ScheduledExecutorService timeoutScheduler;
   private final ThreadPoolExecutor clientConnectionExecutor;
   private volatile TransportServer server;
   private final AtomicBoolean stopped;
   private final ConcurrentHashMap<RpcAddress, Outbox> outboxes;
   @Nullable
   private RpcAddress address;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private volatile boolean bitmap$0;

   private RpcAddress address$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.address = this.server() == null ? null : new RpcAddress(this.host, this.server().getPort());
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this.host = null;
      return this.address;
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

   public SparkConf conf() {
      return this.conf;
   }

   public TransportConf transportConf() {
      return this.transportConf;
   }

   private Dispatcher dispatcher() {
      return this.dispatcher;
   }

   private NettyStreamManager streamManager() {
      return this.streamManager;
   }

   private TransportContext transportContext() {
      return this.transportContext;
   }

   private List<TransportClientBootstrap> createClientBootstraps() {
      return this.securityManager.isAuthenticationEnabled() ? Arrays.asList((Object[])(new TransportClientBootstrap[]{new AuthClientBootstrap(this.transportConf(), this.securityManager.getSaslUser(), this.securityManager)})) : Collections.emptyList();
   }

   private TransportClientFactory clientFactory() {
      return this.clientFactory;
   }

   private TransportClientFactory fileDownloadFactory() {
      return this.fileDownloadFactory;
   }

   private void fileDownloadFactory_$eq(TransportClientFactory x$1) {
      this.fileDownloadFactory = x$1;
   }

   public ScheduledExecutorService timeoutScheduler() {
      return this.timeoutScheduler;
   }

   public ThreadPoolExecutor clientConnectionExecutor() {
      return this.clientConnectionExecutor;
   }

   private TransportServer server() {
      return this.server;
   }

   private void server_$eq(TransportServer x$1) {
      this.server = x$1;
   }

   private AtomicBoolean stopped() {
      return this.stopped;
   }

   private ConcurrentHashMap<RpcAddress, Outbox> outboxes() {
      return this.outboxes;
   }

   public void removeOutbox(RpcAddress address) {
      Outbox outbox = (Outbox)this.outboxes().remove(address);
      if (outbox != null) {
         outbox.stop();
      }

   }

   public void startServer(String bindAddress, int port) {
      List bootstraps = this.securityManager.isAuthenticationEnabled() ? Arrays.asList((Object[])(new TransportServerBootstrap[]{new AuthServerBootstrap(this.transportConf(), this.securityManager)})) : Collections.emptyList();
      this.server_$eq(this.transportContext().createServer(bindAddress, port, bootstraps));
      this.dispatcher().registerRpcEndpoint(RpcEndpointVerifier$.MODULE$.NAME(), new RpcEndpointVerifier(this, this.dispatcher()));
   }

   public RpcAddress address() {
      return this.bitmap$0 ? this.address : this.address$lzycompute();
   }

   public RpcEndpointRef setupEndpoint(String name, RpcEndpoint endpoint) {
      return this.dispatcher().registerRpcEndpoint(name, endpoint);
   }

   public Future<RpcEndpointRef> asyncSetupEndpointRefByURI(String uri) {
      RpcEndpointAddress addr = RpcEndpointAddress$.MODULE$.apply(uri);
      NettyRpcEndpointRef endpointRef = new NettyRpcEndpointRef(this.conf(), addr, this);
      NettyRpcEndpointRef verifier = new NettyRpcEndpointRef(this.conf(), new RpcEndpointAddress(addr.rpcAddress(), RpcEndpointVerifier$.MODULE$.NAME()), this);
      return verifier.ask(new RpcEndpointVerifier.CheckExistence(endpointRef.name()), ClassTag$.MODULE$.Boolean()).<RpcEndpointRef>flatMap(new Serializable(this, uri, endpointRef) {
         public static final long serialVersionUID = 0L;
         private final String uri$1;
         private final NettyRpcEndpointRef endpointRef$1;

         public final Future<NettyRpcEndpointRef> apply(boolean find) {
            return find ? Future$.MODULE$.successful(this.endpointRef$1) : Future$.MODULE$.failed(new RpcEndpointNotFoundException(this.uri$1));
         }

         public {
            this.uri$1 = uri$1;
            this.endpointRef$1 = endpointRef$1;
         }
      }, ThreadUtils$.MODULE$.sameThread());
   }

   public void stop(RpcEndpointRef endpointRef) {
      Predef$.MODULE$.require(endpointRef instanceof NettyRpcEndpointRef);
      this.dispatcher().stop(endpointRef);
   }

   private void postToOutbox(NettyRpcEndpointRef receiver, OutboxMessage message) {
      if (receiver.client() == null) {
         Predef$.MODULE$.require(receiver.address() != null, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Cannot send message to client endpoint with no listen address.";
            }
         });
         Outbox outbox = (Outbox)this.outboxes().get(receiver.address());
         Outbox var10000;
         if (outbox == null) {
            Outbox newOutbox = new Outbox(this, receiver.address());
            Outbox oldOutbox = (Outbox)this.outboxes().putIfAbsent(receiver.address(), newOutbox);
            var10000 = oldOutbox == null ? newOutbox : oldOutbox;
         } else {
            var10000 = outbox;
         }

         Outbox targetOutbox = var10000;
         if (this.stopped().get()) {
            this.outboxes().remove(receiver.address());
            targetOutbox.stop();
         } else {
            targetOutbox.send(message);
         }
      } else {
         message.sendWith(receiver.client());
      }

   }

   public void send(RequestMessage message) {
      label27: {
         RpcAddress remoteAddr = message.receiver().address();
         RpcAddress var3 = this.address();
         if (remoteAddr == null) {
            if (var3 != null) {
               break label27;
            }
         } else if (!remoteAddr.equals(var3)) {
            break label27;
         }

         try {
            this.dispatcher().postOneWayMessage(message);
         } catch (RpcEnvStoppedException var5) {
            this.logDebug(new Serializable(this, var5) {
               public static final long serialVersionUID = 0L;
               private final RpcEnvStoppedException e$1;

               public final String apply() {
                  return this.e$1.getMessage();
               }

               public {
                  this.e$1 = e$1;
               }
            });
         }

         return;
      }

      this.postToOutbox(message.receiver(), new OneWayOutboxMessage(message.serialize(this)));
   }

   public TransportClient createClient(RpcAddress address) {
      return this.clientFactory().createClient(address.host(), address.port());
   }

   public <T> Future<T> ask(RequestMessage message, RpcTimeout timeout, ClassTag<T> evidence$1) {
      Promise promise = Promise$.MODULE$.apply();
      RpcAddress remoteAddr = message.receiver().address();

      try {
         label54: {
            label53: {
               RpcAddress var11 = this.address();
               if (remoteAddr == null) {
                  if (var11 == null) {
                     break label53;
                  }
               } else if (remoteAddr.equals(var11)) {
                  break label53;
               }

               RpcOutboxMessage rpcMessage = new RpcOutboxMessage(message.serialize(this), new Serializable(this, promise) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final NettyRpcEnv $outer;
                  private final Promise promise$1;

                  public final void apply(Throwable e) {
                     this.$outer.org$apache$spark$rpc$netty$NettyRpcEnv$$onFailure$1(e, this.promise$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.promise$1 = promise$1;
                     }
                  }
               }, new Serializable(this, promise) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final NettyRpcEnv $outer;
                  private final Promise promise$1;

                  public final void apply(TransportClient client, ByteBuffer response) {
                     this.$outer.org$apache$spark$rpc$netty$NettyRpcEnv$$onSuccess$1(this.$outer.deserialize(client, response, ClassTag$.MODULE$.Any()), this.promise$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.promise$1 = promise$1;
                     }
                  }
               });
               this.postToOutbox(message.receiver(), rpcMessage);
               promise.future().failed().foreach(new Serializable(this, rpcMessage) {
                  public static final long serialVersionUID = 0L;
                  private final RpcOutboxMessage rpcMessage$1;

                  public final void apply(Throwable x0$2) {
                     if (x0$2 instanceof TimeoutException) {
                        this.rpcMessage$1.onTimeout();
                        BoxedUnit var3 = BoxedUnit.UNIT;
                     } else {
                        BoxedUnit var4 = BoxedUnit.UNIT;
                     }

                  }

                  public {
                     this.rpcMessage$1 = rpcMessage$1;
                  }
               }, ThreadUtils$.MODULE$.sameThread());
               break label54;
            }

            Promise p = Promise$.MODULE$.apply();
            p.future().onComplete(new Serializable(this, promise) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final NettyRpcEnv $outer;
               private final Promise promise$1;

               public final void apply(Try<Object> x0$1) {
                  if (x0$1 instanceof Success) {
                     Success var3 = (Success)x0$1;
                     Object response = var3.value();
                     this.$outer.org$apache$spark$rpc$netty$NettyRpcEnv$$onSuccess$1(response, this.promise$1);
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  } else {
                     if (!(x0$1 instanceof Failure)) {
                        throw new MatchError(x0$1);
                     }

                     Failure var6 = (Failure)x0$1;
                     Throwable e = var6.exception();
                     this.$outer.org$apache$spark$rpc$netty$NettyRpcEnv$$onFailure$1(e, this.promise$1);
                     BoxedUnit var8 = BoxedUnit.UNIT;
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.promise$1 = promise$1;
                  }
               }
            }, ThreadUtils$.MODULE$.sameThread());
            this.dispatcher().postLocalMessage(message, p);
         }

         ScheduledFuture timeoutCancelable = this.timeoutScheduler().schedule(new Runnable(this, timeout, promise, remoteAddr) {
            // $FF: synthetic field
            private final NettyRpcEnv $outer;
            private final RpcTimeout timeout$1;
            private final Promise promise$1;
            private final RpcAddress remoteAddr$1;

            public void run() {
               this.$outer.org$apache$spark$rpc$netty$NettyRpcEnv$$onFailure$1(new TimeoutException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot receive any reply from ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.remoteAddr$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"in ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.timeout$1.duration()}))).toString()), this.promise$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.timeout$1 = timeout$1;
                  this.promise$1 = promise$1;
                  this.remoteAddr$1 = remoteAddr$1;
               }
            }
         }, timeout.duration().toNanos(), TimeUnit.NANOSECONDS);
         promise.future().onComplete(new Serializable(this, timeoutCancelable) {
            public static final long serialVersionUID = 0L;
            private final ScheduledFuture timeoutCancelable$1;

            public final boolean apply(Try<Object> v) {
               return this.timeoutCancelable$1.cancel(true);
            }

            public {
               this.timeoutCancelable$1 = timeoutCancelable$1;
            }
         }, ThreadUtils$.MODULE$.sameThread());
      } catch (Throwable var16) {
         Option var8 = NonFatal$.MODULE$.unapply(var16);
         if (var8.isEmpty()) {
            throw var16;
         }

         Throwable e = (Throwable)var8.get();
         this.org$apache$spark$rpc$netty$NettyRpcEnv$$onFailure$1(e, promise);
         BoxedUnit var10 = BoxedUnit.UNIT;
         return promise.future().<T>mapTo(evidence$1).<T>recover(timeout.addMessageIfTimeout(), ThreadUtils$.MODULE$.sameThread());
      }

      return promise.future().<T>mapTo(evidence$1).<T>recover(timeout.addMessageIfTimeout(), ThreadUtils$.MODULE$.sameThread());
   }

   public ByteBuffer serialize(Object content) {
      return this.org$apache$spark$rpc$netty$NettyRpcEnv$$javaSerializerInstance.serialize(content, ClassTag$.MODULE$.Any());
   }

   public SerializationStream serializeStream(OutputStream out) {
      return this.org$apache$spark$rpc$netty$NettyRpcEnv$$javaSerializerInstance.serializeStream(out);
   }

   public <T> T deserialize(TransportClient client, ByteBuffer bytes, ClassTag<T> evidence$2) {
      return (T)NettyRpcEnv$.MODULE$.currentClient().withValue(client, new Serializable(this, bytes, evidence$2) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final NettyRpcEnv $outer;
         public final ByteBuffer bytes$1;
         public final ClassTag evidence$2$1;

         public final T apply() {
            return (T)this.$outer.deserialize(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final T apply() {
                  return (T)this.$outer.org$apache$spark$rpc$netty$NettyRpcEnv$$anonfun$$$outer().org$apache$spark$rpc$netty$NettyRpcEnv$$javaSerializerInstance.deserialize(this.$outer.bytes$1, this.$outer.evidence$2$1);
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
         public NettyRpcEnv org$apache$spark$rpc$netty$NettyRpcEnv$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.bytes$1 = bytes$1;
               this.evidence$2$1 = evidence$2$1;
            }
         }
      });
   }

   public RpcEndpointRef endpointRef(RpcEndpoint endpoint) {
      return this.dispatcher().getRpcEndpointRef(endpoint);
   }

   public void shutdown() {
      this.cleanup();
   }

   public void awaitTermination() {
      this.dispatcher().awaitTermination();
   }

   private void cleanup() {
      if (this.stopped().compareAndSet(false, true)) {
         for(Outbox outbox : this.outboxes().values()) {
            this.outboxes().remove(outbox.address());
            outbox.stop();
         }

         if (this.timeoutScheduler() == null) {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            this.timeoutScheduler().shutdownNow();
         }

         if (this.dispatcher() != null) {
            this.dispatcher().stop();
         }

         if (this.server() != null) {
            this.server().close();
         }

         if (this.clientFactory() != null) {
            this.clientFactory().close();
         }

         if (this.clientConnectionExecutor() == null) {
            BoxedUnit var3 = BoxedUnit.UNIT;
         } else {
            this.clientConnectionExecutor().shutdownNow();
         }

         if (this.fileDownloadFactory() != null) {
            this.fileDownloadFactory().close();
         }

      }
   }

   public <T> T deserialize(Function0<T> deserializationAction) {
      return (T)NettyRpcEnv$.MODULE$.currentEnv().withValue(this, deserializationAction);
   }

   public RpcEnvFileServer fileServer() {
      return this.streamManager();
   }

   public ReadableByteChannel openChannel(String uri) {
      URI parsedUri = new URI(uri);
      Predef$.MODULE$.require(parsedUri.getHost() != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Host name must be defined.";
         }
      });
      Predef$.MODULE$.require(parsedUri.getPort() > 0, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Port must be defined.";
         }
      });
      Predef$.MODULE$.require(parsedUri.getPath() != null && (new StringOps(Predef$.MODULE$.augmentString(parsedUri.getPath()))).nonEmpty(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Path must be defined.";
         }
      });
      Pipe pipe = Pipe.open();
      FileDownloadChannel source = new FileDownloadChannel(pipe.source());
      Function0 x$1 = new Serializable(this, parsedUri, pipe, source) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final NettyRpcEnv $outer;
         private final URI parsedUri$1;
         private final Pipe pipe$1;
         private final FileDownloadChannel source$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            TransportClient client = this.$outer.org$apache$spark$rpc$netty$NettyRpcEnv$$downloadClient(this.parsedUri$1.getHost(), this.parsedUri$1.getPort());
            FileDownloadCallback callback = this.$outer.new FileDownloadCallback(this.pipe$1.sink(), this.source$1, client);
            client.stream(this.parsedUri$1.getPath(), callback);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.parsedUri$1 = parsedUri$1;
               this.pipe$1 = pipe$1;
               this.source$1 = source$1;
            }
         }
      };
      Function0 x$2 = new Serializable(this, pipe, source) {
         public static final long serialVersionUID = 0L;
         private final Pipe pipe$1;
         private final FileDownloadChannel source$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.pipe$1.sink().close();
            this.source$1.close();
         }

         public {
            this.pipe$1 = pipe$1;
            this.source$1 = source$1;
         }
      };
      Function0 x$3 = new Serializable(this, x$1) {
         public static final long serialVersionUID = 0L;
         private final Function0 x$1$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            Utils$.MODULE$.tryWithSafeFinallyAndFailureCallbacks$default$3(this.x$1$1);
         }

         public {
            this.x$1$1 = x$1$1;
         }
      };
      Utils$.MODULE$.tryWithSafeFinallyAndFailureCallbacks(x$1, x$2, x$3);
      return source;
   }

   public TransportClient org$apache$spark$rpc$netty$NettyRpcEnv$$downloadClient(String host, int port) {
      if (this.fileDownloadFactory() == null) {
         synchronized(this) {
            if (this.fileDownloadFactory() == null) {
               String module = "files";
               String prefix = "spark.rpc.io.";
               SparkConf clone = this.conf().clone();
               Predef$.MODULE$.refArrayOps(this.conf().getAll()).foreach(new Serializable(this, module, prefix, clone) {
                  public static final long serialVersionUID = 0L;
                  private final String module$1;
                  private final String prefix$1;
                  private final SparkConf clone$1;

                  public final Object apply(Tuple2<String, String> x0$3) {
                     if (x0$3 != null) {
                        String key = (String)x0$3._1();
                        String value = (String)x0$3._2();
                        Object var10000;
                        if (key.startsWith(this.prefix$1)) {
                           String opt = key.substring(this.prefix$1.length());
                           var10000 = this.clone$1.setIfMissing((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.", ".io.", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.module$1, opt})), value);
                        } else {
                           var10000 = BoxedUnit.UNIT;
                        }

                        Object var5 = var10000;
                        return var5;
                     } else {
                        throw new MatchError(x0$3);
                     }
                  }

                  public {
                     this.module$1 = module$1;
                     this.prefix$1 = prefix$1;
                     this.clone$1 = clone$1;
                  }
               });
               int ioThreads = clone.getInt("spark.files.io.threads", 1);
               TransportConf downloadConf = SparkTransportConf$.MODULE$.fromSparkConf(clone, module, ioThreads);
               TransportContext downloadContext = new TransportContext(downloadConf, new NoOpRpcHandler(), true);
               this.fileDownloadFactory_$eq(downloadContext.createClientFactory(this.createClientBootstraps()));
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               BoxedUnit var12 = BoxedUnit.UNIT;
            }
         }
      } else {
         BoxedUnit var13 = BoxedUnit.UNIT;
      }

      return this.fileDownloadFactory().createClient(host, port);
   }

   public final void org$apache$spark$rpc$netty$NettyRpcEnv$$onFailure$1(Throwable e, Promise promise$1) {
      if (!promise$1.tryFailure(e)) {
         if (e instanceof RpcEnvStoppedException) {
            RpcEnvStoppedException var4 = (RpcEnvStoppedException)e;
            this.logDebug(new Serializable(this, var4) {
               public static final long serialVersionUID = 0L;
               private final RpcEnvStoppedException x2$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ignored failure: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x2$1}));
               }

               public {
                  this.x2$1 = x2$1;
               }
            });
            BoxedUnit var5 = BoxedUnit.UNIT;
         } else {
            this.logWarning(new Serializable(this, e) {
               public static final long serialVersionUID = 0L;
               private final Throwable e$2;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ignored failure: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.e$2}));
               }

               public {
                  this.e$2 = e$2;
               }
            });
            BoxedUnit var6 = BoxedUnit.UNIT;
         }
      }

   }

   public final void org$apache$spark$rpc$netty$NettyRpcEnv$$onSuccess$1(Object reply, Promise promise$1) {
      if (reply instanceof RpcFailure) {
         RpcFailure var4 = (RpcFailure)reply;
         Throwable e = var4.e();
         this.org$apache$spark$rpc$netty$NettyRpcEnv$$onFailure$1(e, promise$1);
         BoxedUnit var6 = BoxedUnit.UNIT;
      } else if (promise$1.trySuccess(reply)) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         this.logWarning(new Serializable(this, reply) {
            public static final long serialVersionUID = 0L;
            private final Object reply$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ignored message: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.reply$1}));
            }

            public {
               this.reply$1 = reply$1;
            }
         });
         BoxedUnit var7 = BoxedUnit.UNIT;
      }

   }

   public NettyRpcEnv(SparkConf conf, JavaSerializerInstance javaSerializerInstance, String host, SecurityManager securityManager, int numUsableCores) {
      super(conf);
      this.conf = conf;
      this.org$apache$spark$rpc$netty$NettyRpcEnv$$javaSerializerInstance = javaSerializerInstance;
      this.host = host;
      this.securityManager = securityManager;
      Logging$class.$init$(this);
      this.transportConf = SparkTransportConf$.MODULE$.fromSparkConf(conf.clone().set("spark.rpc.io.numConnectionsPerPeer", "1"), "rpc", conf.getInt("spark.rpc.io.threads", 0));
      this.dispatcher = new Dispatcher(this, numUsableCores);
      this.streamManager = new NettyStreamManager(this);
      this.transportContext = new TransportContext(this.transportConf(), new NettyRpcHandler(this.dispatcher(), this, this.streamManager()));
      this.clientFactory = this.transportContext().createClientFactory(this.createClientBootstraps());
      this.timeoutScheduler = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("netty-rpc-env-timeout");
      this.clientConnectionExecutor = ThreadUtils$.MODULE$.newDaemonCachedThreadPool("netty-rpc-connection", conf.getInt("spark.rpc.connect.threads", 64), ThreadUtils$.MODULE$.newDaemonCachedThreadPool$default$3());
      this.stopped = new AtomicBoolean(false);
      this.outboxes = new ConcurrentHashMap();
   }

   public class FileDownloadChannel implements ReadableByteChannel {
      public final Pipe.SourceChannel org$apache$spark$rpc$netty$NettyRpcEnv$FileDownloadChannel$$source;
      private volatile Throwable error;
      // $FF: synthetic field
      public final NettyRpcEnv $outer;

      private Throwable error() {
         return this.error;
      }

      private void error_$eq(Throwable x$1) {
         this.error = x$1;
      }

      public void setError(Throwable e) {
         this.error_$eq(e);
      }

      public int read(ByteBuffer dst) {
         Try var2 = Try$.MODULE$.apply(new Serializable(this, dst) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FileDownloadChannel $outer;
            private final ByteBuffer dst$1;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return this.$outer.org$apache$spark$rpc$netty$NettyRpcEnv$FileDownloadChannel$$source.read(this.dst$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.dst$1 = dst$1;
               }
            }
         });
         if (this.error() == null) {
            if (var2 instanceof Success) {
               Success var4 = (Success)var2;
               int bytesRead = BoxesRunTime.unboxToInt(var4.value());
               return bytesRead;
            } else if (var2 instanceof Failure) {
               Failure var6 = (Failure)var2;
               Throwable readErr = var6.exception();
               throw readErr;
            } else {
               throw new MatchError(var2);
            }
         } else {
            throw this.error();
         }
      }

      public void close() {
         this.org$apache$spark$rpc$netty$NettyRpcEnv$FileDownloadChannel$$source.close();
      }

      public boolean isOpen() {
         return this.org$apache$spark$rpc$netty$NettyRpcEnv$FileDownloadChannel$$source.isOpen();
      }

      // $FF: synthetic method
      public NettyRpcEnv org$apache$spark$rpc$netty$NettyRpcEnv$FileDownloadChannel$$$outer() {
         return this.$outer;
      }

      public FileDownloadChannel(Pipe.SourceChannel source) {
         this.org$apache$spark$rpc$netty$NettyRpcEnv$FileDownloadChannel$$source = source;
         if (NettyRpcEnv.this == null) {
            throw null;
         } else {
            this.$outer = NettyRpcEnv.this;
            super();
         }
      }
   }

   public class FileDownloadCallback implements StreamCallback {
      private final WritableByteChannel sink;
      private final FileDownloadChannel source;
      // $FF: synthetic field
      public final NettyRpcEnv $outer;

      public void onData(String streamId, ByteBuffer buf) {
         while(buf.remaining() > 0) {
            this.sink.write(buf);
         }

      }

      public void onComplete(String streamId) {
         this.sink.close();
      }

      public void onFailure(String streamId, Throwable cause) {
         this.org$apache$spark$rpc$netty$NettyRpcEnv$FileDownloadCallback$$$outer().logDebug(new Serializable(this, streamId) {
            public static final long serialVersionUID = 0L;
            private final String streamId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error downloading stream ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.streamId$1}));
            }

            public {
               this.streamId$1 = streamId$1;
            }
         }, cause);
         this.source.setError(cause);
         this.sink.close();
      }

      // $FF: synthetic method
      public NettyRpcEnv org$apache$spark$rpc$netty$NettyRpcEnv$FileDownloadCallback$$$outer() {
         return this.$outer;
      }

      public FileDownloadCallback(WritableByteChannel sink, FileDownloadChannel source, TransportClient client) {
         this.sink = sink;
         this.source = source;
         if (NettyRpcEnv.this == null) {
            throw null;
         } else {
            this.$outer = NettyRpcEnv.this;
            super();
         }
      }
   }
}
