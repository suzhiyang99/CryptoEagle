package org.apache.spark.scheduler.cluster;

import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.ExecutorAllocationClient;
import org.apache.spark.ExecutorAllocationClient$class;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.TaskState$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.rpc.RpcTimeout;
import org.apache.spark.rpc.ThreadSafeRpcEndpoint;
import org.apache.spark.scheduler.ExecutorKilled$;
import org.apache.spark.scheduler.ExecutorLossReason;
import org.apache.spark.scheduler.LiveListenerBus;
import org.apache.spark.scheduler.LossReasonPending$;
import org.apache.spark.scheduler.SchedulerBackend;
import org.apache.spark.scheduler.SchedulerBackend$class;
import org.apache.spark.scheduler.SlaveLost;
import org.apache.spark.scheduler.SlaveLost$;
import org.apache.spark.scheduler.SparkListenerExecutorAdded;
import org.apache.spark.scheduler.SparkListenerExecutorRemoved;
import org.apache.spark.scheduler.TaskDescription;
import org.apache.spark.scheduler.TaskDescription$;
import org.apache.spark.scheduler.TaskSchedulerImpl;
import org.apache.spark.scheduler.TaskSetManager;
import org.apache.spark.scheduler.WorkerOffer;
import org.apache.spark.util.RpcUtils$;
import org.apache.spark.util.SerializableBuffer;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
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
import scala.collection.Iterable$;
import scala.collection.IterableLike;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.math.package$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\rUg!B\u0001\u0003\u0001\u0019a!!H\"pCJ\u001cXm\u0012:bS:,GmU2iK\u0012,H.\u001a:CC\u000e\\WM\u001c3\u000b\u0005\r!\u0011aB2mkN$XM\u001d\u0006\u0003\u000b\u0019\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001cR\u0001A\u0007\u0014/m\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\u000b\u0016\u001b\u00051\u0011B\u0001\f\u0007\u0005a)\u00050Z2vi>\u0014\u0018\t\u001c7pG\u0006$\u0018n\u001c8DY&,g\u000e\u001e\t\u00031ei\u0011\u0001B\u0005\u00035\u0011\u0011\u0001cU2iK\u0012,H.\u001a:CC\u000e\\WM\u001c3\u0011\u0005qyR\"A\u000f\u000b\u0005y1\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005\u0001j\"a\u0002'pO\u001eLgn\u001a\u0005\t\u000b\u0001\u0011\t\u0011)A\u0005G\r\u0001\u0001C\u0001\r%\u0013\t)CAA\tUCN\\7k\u00195fIVdWM]%na2D\u0001b\n\u0001\u0003\u0006\u0004%\t\u0001K\u0001\u0007eB\u001cWI\u001c<\u0016\u0003%\u0002\"AK\u0017\u000e\u0003-R!\u0001\f\u0004\u0002\u0007I\u00048-\u0003\u0002/W\t1!\u000b]2F]ZD\u0001\u0002\r\u0001\u0003\u0002\u0003\u0006I!K\u0001\beB\u001cWI\u001c<!\u0011\u0015\u0011\u0004\u0001\"\u00014\u0003\u0019a\u0014N\\5u}Q\u0019AGN\u001c\u0011\u0005U\u0002Q\"\u0001\u0002\t\u000b\u0015\t\u0004\u0019A\u0012\t\u000b\u001d\n\u0004\u0019A\u0015\t\u000fe\u0002!\u0019!C\tu\u0005qAo\u001c;bY\u000e{'/Z\"pk:$X#A\u001e\u0011\u0005q*U\"A\u001f\u000b\u0005yz\u0014AB1u_6L7M\u0003\u0002A\u0003\u0006Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0005\t\u001b\u0015\u0001B;uS2T\u0011\u0001R\u0001\u0005U\u00064\u0018-\u0003\u0002G{\ti\u0011\t^8nS\u000eLe\u000e^3hKJDa\u0001\u0013\u0001!\u0002\u0013Y\u0014a\u0004;pi\u0006d7i\u001c:f\u0007>,h\u000e\u001e\u0011\t\u000f)\u0003!\u0019!C\tu\u0005ABo\u001c;bYJ+w-[:uKJ,G-\u0012=fGV$xN]:\t\r1\u0003\u0001\u0015!\u0003<\u0003e!x\u000e^1m%\u0016<\u0017n\u001d;fe\u0016$W\t_3dkR|'o\u001d\u0011\t\u000f9\u0003!\u0019!C\t\u001f\u0006!1m\u001c8g+\u0005\u0001\u0006C\u0001\u000bR\u0013\t\u0011fAA\u0005Ta\u0006\u00148nQ8oM\"1A\u000b\u0001Q\u0001\nA\u000bQaY8oM\u0002BqA\u0016\u0001C\u0002\u0013%q+A\tnCb\u0014\u0006oY'fgN\fw-Z*ju\u0016,\u0012\u0001\u0017\t\u0003\u001deK!AW\b\u0003\u0007%sG\u000f\u0003\u0004]\u0001\u0001\u0006I\u0001W\u0001\u0013[\u0006D(\u000b]2NKN\u001c\u0018mZ3TSj,\u0007\u0005C\u0004_\u0001\t\u0007I\u0011B0\u0002#\u0011,g-Y;mi\u0006\u001b8\u000eV5nK>,H/F\u0001a!\tQ\u0013-\u0003\u0002cW\tQ!\u000b]2US6,w.\u001e;\t\r\u0011\u0004\u0001\u0015!\u0003a\u0003I!WMZ1vYR\f5o\u001b+j[\u0016|W\u000f\u001e\u0011\t\u000f\u0019\u0004!\u0019!C\u0005O\u0006\u0019r,\\5o%\u0016<\u0017n\u001d;fe\u0016$'+\u0019;j_V\t\u0001\u000e\u0005\u0002\u000fS&\u0011!n\u0004\u0002\u0007\t>,(\r\\3\t\r1\u0004\u0001\u0015!\u0003i\u0003QyV.\u001b8SK\u001eL7\u000f^3sK\u0012\u0014\u0016\r^5pA!9a\u000e\u0001b\u0001\n\u0013y\u0017AG7bqJ+w-[:uKJ,GmV1ji&tw\rV5nK6\u001bX#\u00019\u0011\u00059\t\u0018B\u0001:\u0010\u0005\u0011auN\\4\t\rQ\u0004\u0001\u0015!\u0003q\u0003mi\u0017\r\u001f*fO&\u001cH/\u001a:fI^\u000b\u0017\u000e^5oORKW.Z'tA!9a\u000f\u0001b\u0001\n\u0013y\u0017AC2sK\u0006$X\rV5nK\"1\u0001\u0010\u0001Q\u0001\nA\f1b\u0019:fCR,G+[7fA!9!\u0010\u0001b\u0001\n\u0013Y\u0018aD3yK\u000e,Ho\u001c:ECR\fW*\u00199\u0016\u0003q\u0004r!`A\u0003\u0003\u0013\t9\"D\u0001\u007f\u0015\ry\u0018\u0011A\u0001\b[V$\u0018M\u00197f\u0015\r\t\u0019aD\u0001\u000bG>dG.Z2uS>t\u0017bAA\u0004}\n9\u0001*Y:i\u001b\u0006\u0004\b\u0003BA\u0006\u0003#q1ADA\u0007\u0013\r\tyaD\u0001\u0007!J,G-\u001a4\n\t\u0005M\u0011Q\u0003\u0002\u0007'R\u0014\u0018N\\4\u000b\u0007\u0005=q\u0002E\u00026\u00033I1!a\u0007\u0003\u00051)\u00050Z2vi>\u0014H)\u0019;b\u0011\u001d\ty\u0002\u0001Q\u0001\nq\f\u0001#\u001a=fGV$xN\u001d#bi\u0006l\u0015\r\u001d\u0011\t\u0011\u0005\r\u0002\u00011A\u0005\n]\u000bqC]3rk\u0016\u001cH/\u001a3U_R\fG.\u0012=fGV$xN]:\t\u0013\u0005\u001d\u0002\u00011A\u0005\n\u0005%\u0012a\u0007:fcV,7\u000f^3e)>$\u0018\r\\#yK\u000e,Ho\u001c:t?\u0012*\u0017\u000f\u0006\u0003\u0002,\u0005E\u0002c\u0001\b\u0002.%\u0019\u0011qF\b\u0003\tUs\u0017\u000e\u001e\u0005\n\u0003g\t)#!AA\u0002a\u000b1\u0001\u001f\u00132\u0011\u001d\t9\u0004\u0001Q!\na\u000b\u0001D]3rk\u0016\u001cH/\u001a3U_R\fG.\u0012=fGV$xN]:!Q!\t)$a\u000f\u0002N\u0005=\u0003\u0003BA\u001f\u0003\u0013j!!a\u0010\u000b\u0007\u0001\u000b\tE\u0003\u0003\u0002D\u0005\u0015\u0013AC1o]>$\u0018\r^5p]*\u0011\u0011qI\u0001\u0006U\u00064\u0018\r_\u0005\u0005\u0003\u0017\nyDA\u0005Hk\u0006\u0014H-\u001a3Cs\u0006)a/\u00197vK\u0006\u0012\u0011\u0011K\u0001#\u0007>\f'o]3He\u0006Lg.\u001a3TG\",G-\u001e7fe\n\u000b7m[3oI:\"\b.[:\t\u0011\u0005U\u0003\u00011A\u0005\n]\u000b1C\\;n!\u0016tG-\u001b8h\u000bb,7-\u001e;peND\u0011\"!\u0017\u0001\u0001\u0004%I!a\u0017\u0002/9,X\u000eU3oI&tw-\u0012=fGV$xN]:`I\u0015\fH\u0003BA\u0016\u0003;B\u0011\"a\r\u0002X\u0005\u0005\t\u0019\u0001-\t\u000f\u0005\u0005\u0004\u0001)Q\u00051\u0006!b.^7QK:$\u0017N\\4Fq\u0016\u001cW\u000f^8sg\u0002B\u0003\"a\u0018\u0002<\u00055\u0013q\n\u0005\n\u0003O\u0002!\u0019!C\u0005\u0003S\n1\u0002\\5ti\u0016tWM\u001d\"vgV\u0011\u00111\u000e\t\u00041\u00055\u0014bAA8\t\tyA*\u001b<f\u0019&\u001cH/\u001a8fe\n+8\u000f\u0003\u0005\u0002t\u0001\u0001\u000b\u0011BA6\u00031a\u0017n\u001d;f]\u0016\u0014()^:!\u0011%\t9\b\u0001b\u0001\n\u0013\tI(\u0001\rfq\u0016\u001cW\u000f^8sgB+g\u000eZ5oOR{'+Z7pm\u0016,\"!a\u001f\u0011\u000fu\f)!!\u0003\u0002~A\u0019a\"a \n\u0007\u0005\u0005uBA\u0004C_>dW-\u00198\t\u0011\u0005\u0015\u0005\u0001)A\u0005\u0003w\n\u0011$\u001a=fGV$xN]:QK:$\u0017N\\4U_J+Wn\u001c<fA!B\u00111QA\u001e\u0003\u001b\ny\u0005C\u0005\u0002\f\u0002\u0001\r\u0011\"\u0005\u0002\u000e\u0006!\u0002n\\:u)>dunY1m)\u0006\u001c8nQ8v]R,\"!a$\u0011\u000f\u0005-\u0011\u0011SA\u00051&!\u00111SA\u000b\u0005\ri\u0015\r\u001d\u0005\n\u0003/\u0003\u0001\u0019!C\t\u00033\u000b\u0001\u0004[8tiR{Gj\\2bYR\u000b7o[\"pk:$x\fJ3r)\u0011\tY#a'\t\u0015\u0005M\u0012QSA\u0001\u0002\u0004\ty\t\u0003\u0005\u0002 \u0002\u0001\u000b\u0015BAH\u0003UAwn\u001d;U_2{7-\u00197UCN\\7i\\;oi\u0002B\u0003\"!(\u0002<\u00055\u0013q\n\u0005\t\u0003K\u0003\u0001\u0019!C\t/\u0006\u0011Bn\\2bY&$\u00180Q<be\u0016$\u0016m]6t\u0011%\tI\u000b\u0001a\u0001\n#\tY+\u0001\fm_\u000e\fG.\u001b;z\u0003^\f'/\u001a+bg.\u001cx\fJ3r)\u0011\tY#!,\t\u0013\u0005M\u0012qUA\u0001\u0002\u0004A\u0006bBAY\u0001\u0001\u0006K\u0001W\u0001\u0014Y>\u001c\u0017\r\\5us\u0006;\u0018M]3UCN\\7\u000f\t\u0015\t\u0003_\u000bY$!\u0014\u0002P!A\u0011q\u0017\u0001A\u0002\u0013Eq+\u0001\rdkJ\u0014XM\u001c;Fq\u0016\u001cW\u000f^8s\u0013\u0012\u001cu.\u001e8uKJD\u0011\"a/\u0001\u0001\u0004%\t\"!0\u00029\r,(O]3oi\u0016CXmY;u_JLEmQ8v]R,'o\u0018\u0013fcR!\u00111FA`\u0011%\t\u0019$!/\u0002\u0002\u0003\u0007\u0001\fC\u0004\u0002D\u0002\u0001\u000b\u0015\u0002-\u00023\r,(O]3oi\u0016CXmY;u_JLEmQ8v]R,'\u000f\t\u0015\u0005\u0003\u0003\f9\rE\u0002\u000f\u0003\u0013L1!a3\u0010\u0005!1x\u000e\\1uS2,\u0007\"CAh\u0001\t\u0007I\u0011BAi\u00031\u0011XM^5wKRC'/Z1e+\t\t\u0019\u000e\u0005\u0003\u0002V\u0006]W\"A \n\u0007\u0005ewH\u0001\rTG\",G-\u001e7fI\u0016CXmY;u_J\u001cVM\u001d<jG\u0016D\u0001\"!8\u0001A\u0003%\u00111[\u0001\u000ee\u00164\u0018N^3UQJ,\u0017\r\u001a\u0011\u0007\r\u0005\u0005\b\u0001AAr\u00059!%/\u001b<fe\u0016sG\r]8j]R\u001cb!a8\u000e\u0003K\\\u0002c\u0001\u0016\u0002h&\u0019\u0011\u0011^\u0016\u0003+QC'/Z1e'\u00064WM\u00159d\u000b:$\u0007o\\5oi\"Iq%a8\u0003\u0006\u0004%\t\u0005\u000b\u0005\na\u0005}'\u0011!Q\u0001\n%B1\"!=\u0002`\n\u0005\t\u0015!\u0003\u0002t\u0006y1\u000f]1sWB\u0013x\u000e]3si&,7\u000f\u0005\u0004\u0002v\n\u0015!1\u0002\b\u0005\u0003o\u0014\tA\u0004\u0003\u0002z\u0006}XBAA~\u0015\r\tiPI\u0001\u0007yI|w\u000e\u001e \n\u0003AI1Aa\u0001\u0010\u0003\u001d\u0001\u0018mY6bO\u0016LAAa\u0002\u0003\n\t\u00191+Z9\u000b\u0007\t\rq\u0002E\u0004\u000f\u0005\u001b\tI!!\u0003\n\u0007\t=qB\u0001\u0004UkBdWM\r\u0005\be\u0005}G\u0011\u0001B\n)\u0019\u0011)B!\u0007\u0003\u001cA!!qCAp\u001b\u0005\u0001\u0001BB\u0014\u0003\u0012\u0001\u0007\u0011\u0006\u0003\u0005\u0002r\nE\u0001\u0019AAz\u0011)\u0011y\"a8C\u0002\u0013E!\u0011E\u0001\u001bKb,7-\u001e;peN\u0004VM\u001c3j]\u001edun]:SK\u0006\u001cxN\\\u000b\u0003\u0005G\u0001R! B\u0013\u0003\u0013I1Aa\n\u007f\u0005\u001dA\u0015m\u001d5TKRD\u0011Ba\u000b\u0002`\u0002\u0006IAa\t\u00027\u0015DXmY;u_J\u001c\b+\u001a8eS:<Gj\\:t%\u0016\f7o\u001c8!\u0011)\u0011y#a8C\u0002\u0013E!\u0011G\u0001\u0014C\u0012$'/Z:t)>,\u00050Z2vi>\u0014\u0018\nZ\u000b\u0003\u0005g\u0001r!`A\u0003\u0005k\tI\u0001E\u0002+\u0005oI1A!\u000f,\u0005)\u0011\u0006oY!eIJ,7o\u001d\u0005\n\u0005{\ty\u000e)A\u0005\u0005g\tA#\u00193ee\u0016\u001c8\u000fV8Fq\u0016\u001cW\u000f^8s\u0013\u0012\u0004\u0003\u0002\u0003B!\u0003?$\tEa\u0011\u0002\u000f=t7\u000b^1siR\u0011\u00111\u0006\u0005\t\u0005\u000f\ny\u000e\"\u0011\u0003J\u00059!/Z2fSZ,WC\u0001B&!\u001dq!Q\nB)\u0003WI1Aa\u0014\u0010\u0005=\u0001\u0016M\u001d;jC24UO\\2uS>t\u0007c\u0001\b\u0003T%\u0019!QK\b\u0003\u0007\u0005s\u0017\u0010\u0003\u0005\u0003Z\u0005}G\u0011\tB.\u0003=\u0011XmY3jm\u0016\fe\u000e\u001a*fa2LH\u0003\u0002B&\u0005;B\u0001Ba\u0018\u0003X\u0001\u0007!\u0011M\u0001\bG>tG/\u001a=u!\rQ#1M\u0005\u0004\u0005KZ#A\u0004*qG\u000e\u000bG\u000e\\\"p]R,\u0007\u0010\u001e\u0005\t\u0005S\ny\u000e\"\u0003\u0003D\u0005QQ.Y6f\u001f\u001a4WM]:\t\u0011\t5\u0014q\u001cC!\u0005_\nab\u001c8ESN\u001cwN\u001c8fGR,G\r\u0006\u0003\u0002,\tE\u0004\u0002\u0003B:\u0005W\u0002\rA!\u000e\u0002\u001bI,Wn\u001c;f\u0003\u0012$'/Z:t\u0011!\u0011I'a8\u0005\n\t]D\u0003BA\u0016\u0005sB\u0001Ba\u001f\u0003v\u0001\u0007\u0011\u0011B\u0001\u000bKb,7-\u001e;pe&#\u0007\u0002\u0003B@\u0003?$IA!!\u0002\u001f\u0015DXmY;u_JL5/\u00117jm\u0016$B!! \u0003\u0004\"A!1\u0010B?\u0001\u0004\tI\u0001\u0003\u0005\u0003\b\u0006}G\u0011\u0002BE\u0003-a\u0017-\u001e8dQR\u000b7o[:\u0015\t\u0005-\"1\u0012\u0005\t\u0005\u001b\u0013)\t1\u0001\u0003\u0010\u0006)A/Y:lgB1\u0011Q\u001fB\u0003\u0005#\u0003b!!>\u0003\u0006\tM\u0005c\u0001\r\u0003\u0016&\u0019!q\u0013\u0003\u0003\u001fQ\u000b7o\u001b#fg\u000e\u0014\u0018\u000e\u001d;j_:D\u0001Ba'\u0002`\u0012%!QT\u0001\u000fe\u0016lwN^3Fq\u0016\u001cW\u000f^8s)\u0019\tYCa(\u0003\"\"A!1\u0010BM\u0001\u0004\tI\u0001\u0003\u0005\u0003$\ne\u0005\u0019\u0001BS\u0003\u0019\u0011X-Y:p]B\u0019\u0001Da*\n\u0007\t%FA\u0001\nFq\u0016\u001cW\u000f^8s\u0019>\u001c8OU3bg>t\u0007\u0002\u0003BW\u0003?$IAa,\u0002\u0019I,Wn\u001c<f/>\u00148.\u001a:\u0015\u0011\u0005-\"\u0011\u0017B[\u0005sC\u0001Ba-\u0003,\u0002\u0007\u0011\u0011B\u0001\to>\u00148.\u001a:JI\"A!q\u0017BV\u0001\u0004\tI!\u0001\u0003i_N$\b\u0002\u0003B^\u0005W\u0003\r!!\u0003\u0002\u000f5,7o]1hK\"A!qXAp\t#\u0011\t-A\beSN\f'\r\\3Fq\u0016\u001cW\u000f^8s)\u0011\tiHa1\t\u0011\tm$Q\u0018a\u0001\u0003\u0013A\u0011Ba2\u0001\u0001\u0004%\tA!3\u0002\u001d\u0011\u0014\u0018N^3s\u000b:$\u0007o\\5oiV\u0011!1\u001a\t\u0004U\t5\u0017b\u0001BhW\tq!\u000b]2F]\u0012\u0004x.\u001b8u%\u00164\u0007\"\u0003Bj\u0001\u0001\u0007I\u0011\u0001Bk\u0003I!'/\u001b<fe\u0016sG\r]8j]R|F%Z9\u0015\t\u0005-\"q\u001b\u0005\u000b\u0003g\u0011\t.!AA\u0002\t-\u0007\u0002\u0003Bn\u0001\u0001\u0006KAa3\u0002\u001f\u0011\u0014\u0018N^3s\u000b:$\u0007o\\5oi\u0002BaAa8\u0001\t#9\u0017AE7j]J+w-[:uKJ,GMU1uS>DqAa9\u0001\t\u0003\u0012\u0019%A\u0003ti\u0006\u0014H\u000fC\u0004\u0003h\u0002!\tB!;\u0002/\r\u0014X-\u0019;f\tJLg/\u001a:F]\u0012\u0004x.\u001b8u%\u00164G\u0003\u0002Bf\u0005WD\u0001B!<\u0003f\u0002\u0007!q^\u0001\u000baJ|\u0007/\u001a:uS\u0016\u001c\b#B?\u0003r\n-\u0011b\u0001Bz}\nY\u0011I\u001d:bs\n+hMZ3s\u0011\u001d\u00119\u0010\u0001C\t\u0005s\fAc\u0019:fCR,GI]5wKJ,e\u000e\u001a9pS:$H\u0003\u0002B\u000b\u0005wD\u0001B!<\u0003v\u0002\u0007\u00111\u001f\u0005\b\u0005\u007f\u0004A\u0011\u0001B\"\u00035\u0019Ho\u001c9Fq\u0016\u001cW\u000f^8sg\"911\u0001\u0001\u0005B\t\r\u0013\u0001B:u_BDqaa\u0002\u0001\t#\u0011\u0019%A\u0003sKN,G\u000fC\u0004\u0004\f\u0001!\tEa\u0011\u0002\u0019I,g/\u001b<f\u001f\u001a4WM]:\t\u000f\r=\u0001\u0001\"\u0011\u0004\u0012\u0005A1.\u001b7m)\u0006\u001c8\u000e\u0006\u0006\u0002,\rM1qCB\r\u0007;Aqa!\u0006\u0004\u000e\u0001\u0007\u0001/\u0001\u0004uCN\\\u0017\n\u001a\u0005\t\u0005w\u001ai\u00011\u0001\u0002\n!A11DB\u0007\u0001\u0004\ti(A\bj]R,'O];qiRC'/Z1e\u0011!\u0011\u0019k!\u0004A\u0002\u0005%\u0001bBB\u0011\u0001\u0011\u000531E\u0001\u0013I\u00164\u0017-\u001e7u!\u0006\u0014\u0018\r\u001c7fY&\u001cX\u000eF\u0001Y\u0011\u001d\u0011Y\n\u0001C\t\u0007O!b!a\u000b\u0004*\r-\u0002\u0002\u0003B>\u0007K\u0001\r!!\u0003\t\u0011\t\r6Q\u0005a\u0001\u0005KCqA!,\u0001\t#\u0019y\u0003\u0006\u0005\u0002,\rE21GB\u001b\u0011!\u0011\u0019l!\fA\u0002\u0005%\u0001\u0002\u0003B\\\u0007[\u0001\r!!\u0003\t\u0011\tm6Q\u0006a\u0001\u0003\u0013Aqa!\u000f\u0001\t\u0003\u0019Y$A\u000ftk\u001a4\u0017nY5f]R\u0014Vm]8ve\u000e,7OU3hSN$XM]3e)\t\ti\bC\u0004\u0004@\u0001!\tea\u000f\u0002\u000f%\u001c(+Z1es\"111\t\u0001\u0005\n]\u000bAC\\;n\u000bbL7\u000f^5oO\u0016CXmY;u_J\u001c\bbBB$\u0001\u0011\u00053\u0011J\u0001\u000fO\u0016$X\t_3dkR|'/\u00133t)\t\u0019Y\u0005\u0005\u0004\u0002v\n\u0015\u0011\u0011\u0002\u0005\b\u0007\u001f\u0002AQIB)\u0003A\u0011X-];fgR,\u00050Z2vi>\u00148\u000f\u0006\u0003\u0002~\rM\u0003bBB+\u0007\u001b\u0002\r\u0001W\u0001\u0017]Vl\u0017\t\u001a3ji&|g.\u00197Fq\u0016\u001cW\u000f^8sg\"91\u0011\f\u0001\u0005F\rm\u0013!\u0006:fcV,7\u000f\u001e+pi\u0006dW\t_3dkR|'o\u001d\u000b\t\u0003{\u001aif!\u0019\u0004d!91qLB,\u0001\u0004A\u0016\u0001\u00048v[\u0016CXmY;u_J\u001c\bbBAS\u0007/\u0002\r\u0001\u0017\u0005\t\u0003\u0017\u001b9\u00061\u0001\u0002\u0010\"91q\r\u0001\u0005\u0012\r%\u0014a\u00063p%\u0016\fX/Z:u)>$\u0018\r\\#yK\u000e,Ho\u001c:t)\u0011\u0019Yg!\u001e\u0011\r\r54\u0011OA?\u001b\t\u0019yG\u0003\u0002A\u001f%!11OB8\u0005\u00191U\u000f^;sK\"91qOB3\u0001\u0004A\u0016A\u0004:fcV,7\u000f^3e)>$\u0018\r\u001c\u0005\b\u0007w\u0002AQIB?\u00035Y\u0017\u000e\u001c7Fq\u0016\u001cW\u000f^8sgRA11JB@\u0007\u0007\u001b9\t\u0003\u0005\u0004\u0002\u000ee\u0004\u0019AB&\u0003-)\u00070Z2vi>\u0014\u0018\nZ:\t\u0015\r\u00155\u0011\u0010I\u0001\u0002\u0004\ti(A\u0004sKBd\u0017mY3\t\u0015\r%5\u0011\u0010I\u0001\u0002\u0004\ti(A\u0003g_J\u001cW\rC\u0004\u0004\u000e\u0002!\tba$\u0002\u001f\u0011|7*\u001b7m\u000bb,7-\u001e;peN$Baa\u001b\u0004\u0012\"A1\u0011QBF\u0001\u0004\u0019Y\u0005C\u0004\u0004\u0016\u0002!)ea&\u0002'-LG\u000e\\#yK\u000e,Ho\u001c:t\u001f:Dun\u001d;\u0015\t\u0005u4\u0011\u0014\u0005\t\u0005o\u001b\u0019\n1\u0001\u0002\n!91Q\u0014\u0001\u0005\u0012\r}\u0015a\u00074fi\u000eD\u0007*\u00193p_B$U\r\\3hCRLwN\u001c+pW\u0016t7\u000f\u0006\u0002\u0004\"B)aba)\u0004(&\u00191QU\b\u0003\r=\u0003H/[8o!\u0015q1\u0011VBW\u0013\r\u0019Yk\u0004\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0004\u001d\r=\u0016bABY\u001f\t!!)\u001f;f\u000f!\u0019)L\u0001E\u0001\r\r]\u0016!H\"pCJ\u001cXm\u0012:bS:,GmU2iK\u0012,H.\u001a:CC\u000e\\WM\u001c3\u0011\u0007U\u001aILB\u0004\u0002\u0005!\u0005aaa/\u0014\u0007\reV\u0002C\u00043\u0007s#\taa0\u0015\u0005\r]\u0006BCBb\u0007s\u0013\r\u0011\"\u0001\u0004F\u0006iQI\u0014#Q\u001f&sEk\u0018(B\u001b\u0016+\"aa2\u0011\t\r%7qZ\u0007\u0003\u0007\u0017T1a!4D\u0003\u0011a\u0017M\\4\n\t\u0005M11\u001a\u0005\n\u0007'\u001cI\f)A\u0005\u0007\u000f\fa\"\u0012(E!>Ke\nV0O\u00036+\u0005\u0005"
)
public class CoarseGrainedSchedulerBackend implements ExecutorAllocationClient, SchedulerBackend, Logging {
   public final TaskSchedulerImpl org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler;
   private final RpcEnv rpcEnv;
   private final AtomicInteger totalCoreCount;
   private final AtomicInteger totalRegisteredExecutors;
   private final SparkConf conf;
   private final int org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRpcMessageSize;
   private final RpcTimeout defaultAskTimeout;
   private final double _minRegisteredRatio;
   private final long org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRegisteredWaitingTimeMs;
   private final long createTime;
   private final HashMap<String, ExecutorData> org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap;
   @GuardedBy("CoarseGrainedSchedulerBackend.this")
   private int org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors;
   @GuardedBy("CoarseGrainedSchedulerBackend.this")
   private int org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors;
   private final LiveListenerBus org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$listenerBus;
   @GuardedBy("CoarseGrainedSchedulerBackend.this")
   private final HashMap<String, Object> org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove;
   @GuardedBy("CoarseGrainedSchedulerBackend.this")
   private Map<String, Object> hostToLocalTaskCount;
   @GuardedBy("CoarseGrainedSchedulerBackend.this")
   private int localityAwareTasks;
   private volatile int currentExecutorIdCounter;
   private final ScheduledExecutorService org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$reviveThread;
   private RpcEndpointRef driverEndpoint;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private final String org$apache$spark$scheduler$SchedulerBackend$$appId;

   public static String ENDPOINT_NAME() {
      return CoarseGrainedSchedulerBackend$.MODULE$.ENDPOINT_NAME();
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

   public String org$apache$spark$scheduler$SchedulerBackend$$appId() {
      return this.org$apache$spark$scheduler$SchedulerBackend$$appId;
   }

   public void org$apache$spark$scheduler$SchedulerBackend$_setter_$org$apache$spark$scheduler$SchedulerBackend$$appId_$eq(String x$1) {
      this.org$apache$spark$scheduler$SchedulerBackend$$appId = x$1;
   }

   public String applicationId() {
      return SchedulerBackend$class.applicationId(this);
   }

   public Option<String> applicationAttemptId() {
      return SchedulerBackend$class.applicationAttemptId(this);
   }

   public Option<Map<String, String>> getDriverLogUrls() {
      return SchedulerBackend$class.getDriverLogUrls(this);
   }

   public boolean killExecutor(String executorId) {
      return ExecutorAllocationClient$class.killExecutor(this, executorId);
   }

   public boolean killExecutors$default$2() {
      return ExecutorAllocationClient$class.killExecutors$default$2(this);
   }

   public boolean killExecutors$default$3() {
      return ExecutorAllocationClient$class.killExecutors$default$3(this);
   }

   public RpcEnv rpcEnv() {
      return this.rpcEnv;
   }

   public AtomicInteger totalCoreCount() {
      return this.totalCoreCount;
   }

   public AtomicInteger totalRegisteredExecutors() {
      return this.totalRegisteredExecutors;
   }

   public SparkConf conf() {
      return this.conf;
   }

   public int org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRpcMessageSize() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRpcMessageSize;
   }

   private RpcTimeout defaultAskTimeout() {
      return this.defaultAskTimeout;
   }

   private double _minRegisteredRatio() {
      return this._minRegisteredRatio;
   }

   public long org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRegisteredWaitingTimeMs() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRegisteredWaitingTimeMs;
   }

   private long createTime() {
      return this.createTime;
   }

   public HashMap<String, ExecutorData> org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap;
   }

   public int org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors;
   }

   private void org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors_$eq(int x$1) {
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors = x$1;
   }

   public int org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors;
   }

   public void org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors_$eq(int x$1) {
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors = x$1;
   }

   public LiveListenerBus org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$listenerBus() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$listenerBus;
   }

   public HashMap<String, Object> org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove;
   }

   public Map<String, Object> hostToLocalTaskCount() {
      return this.hostToLocalTaskCount;
   }

   public void hostToLocalTaskCount_$eq(Map<String, Object> x$1) {
      this.hostToLocalTaskCount = x$1;
   }

   public int localityAwareTasks() {
      return this.localityAwareTasks;
   }

   public void localityAwareTasks_$eq(int x$1) {
      this.localityAwareTasks = x$1;
   }

   public int currentExecutorIdCounter() {
      return this.currentExecutorIdCounter;
   }

   public void currentExecutorIdCounter_$eq(int x$1) {
      this.currentExecutorIdCounter = x$1;
   }

   public ScheduledExecutorService org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$reviveThread() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$reviveThread;
   }

   public RpcEndpointRef driverEndpoint() {
      return this.driverEndpoint;
   }

   public void driverEndpoint_$eq(RpcEndpointRef x$1) {
      this.driverEndpoint = x$1;
   }

   public double minRegisteredRatio() {
      return this._minRegisteredRatio();
   }

   public void start() {
      ArrayBuffer properties = new ArrayBuffer();
      Predef$.MODULE$.refArrayOps(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.sc().conf().getAll()).withFilter(new Serializable(this) {
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
      }).foreach(new Serializable(this, properties) {
         public static final long serialVersionUID = 0L;
         private final ArrayBuffer properties$1;

         public final Object apply(Tuple2<String, String> x$5) {
            if (x$5 != null) {
               String key = (String)x$5._1();
               String value = (String)x$5._2();
               Object var5 = key.startsWith("spark.") ? this.properties$1.$plus$eq(new Tuple2(key, value)) : BoxedUnit.UNIT;
               return var5;
            } else {
               throw new MatchError(x$5);
            }
         }

         public {
            this.properties$1 = properties$1;
         }
      });
      this.driverEndpoint_$eq(this.createDriverEndpointRef(properties));
   }

   public RpcEndpointRef createDriverEndpointRef(ArrayBuffer<Tuple2<String, String>> properties) {
      return this.rpcEnv().setupEndpoint(CoarseGrainedSchedulerBackend$.MODULE$.ENDPOINT_NAME(), this.createDriverEndpoint(properties));
   }

   public DriverEndpoint createDriverEndpoint(Seq<Tuple2<String, String>> properties) {
      return new DriverEndpoint(this.rpcEnv(), properties);
   }

   public void stopExecutors() {
      try {
         if (this.driverEndpoint() != null) {
            this.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Shutting down all executors";
               }
            });
            this.driverEndpoint().askSync(CoarseGrainedClusterMessages.StopExecutors$.MODULE$, ClassTag$.MODULE$.Boolean());
         }

      } catch (Exception var2) {
         throw new SparkException("Error asking standalone scheduler to shut down executors", var2);
      }
   }

   public void stop() {
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$reviveThread().shutdownNow();
      this.stopExecutors();

      try {
         if (this.driverEndpoint() != null) {
            this.driverEndpoint().askSync(CoarseGrainedClusterMessages.StopDriver$.MODULE$, ClassTag$.MODULE$.Boolean());
         }

      } catch (Exception var2) {
         throw new SparkException("Error stopping standalone scheduler's driver endpoint", var2);
      }
   }

   public void reset() {
      Set var3;
      synchronized(this) {
         this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors_$eq(0);
         this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors_$eq(0);
         this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().clear();
         var3 = this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().keys().toSet();
      }

      Set executors = var3;
      executors.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoarseGrainedSchedulerBackend $outer;

         public final void apply(String eid) {
            this.$outer.removeExecutor(eid, new SlaveLost("Stale executor after cluster manager re-registered.", SlaveLost$.MODULE$.apply$default$2()));
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

   public void reviveOffers() {
      this.driverEndpoint().send(CoarseGrainedClusterMessages.ReviveOffers$.MODULE$);
   }

   public void killTask(long taskId, String executorId, boolean interruptThread, String reason) {
      this.driverEndpoint().send(new CoarseGrainedClusterMessages.KillTask(taskId, executorId, interruptThread, reason));
   }

   public int defaultParallelism() {
      return this.conf().getInt("spark.default.parallelism", package$.MODULE$.max(this.totalCoreCount().get(), 2));
   }

   public void removeExecutor(String executorId, ExecutorLossReason reason) {
      this.driverEndpoint().send(new CoarseGrainedClusterMessages.RemoveExecutor(executorId, reason));
   }

   public void removeWorker(String workerId, String host, String message) {
      this.driverEndpoint().ask(new CoarseGrainedClusterMessages.RemoveWorker(workerId, host, message), ClassTag$.MODULE$.Boolean()).failed().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CoarseGrainedSchedulerBackend $outer;

         public final void apply(Throwable t) {
            this.$outer.logError(new Serializable(this, t) {
               public static final long serialVersionUID = 0L;
               private final Throwable t$1;

               public final String apply() {
                  return this.t$1.getMessage();
               }

               public {
                  this.t$1 = t$1;
               }
            }, t);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, ThreadUtils$.MODULE$.sameThread());
   }

   public boolean sufficientResourcesRegistered() {
      return true;
   }

   public boolean isReady() {
      if (this.sufficientResourcesRegistered()) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final CoarseGrainedSchedulerBackend $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)"SchedulerBackend is ready for scheduling beginning after ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"reached minRegisteredResourcesRatio: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.minRegisteredRatio())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         return true;
      } else if (System.currentTimeMillis() - this.createTime() >= this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRegisteredWaitingTimeMs()) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final CoarseGrainedSchedulerBackend $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)"SchedulerBackend is ready for scheduling beginning after waiting ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"maxRegisteredResourcesWaitingTime: ", "(ms)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRegisteredWaitingTimeMs())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         return true;
      } else {
         return false;
      }
   }

   public int org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numExistingExecutors() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().size();
   }

   public Seq<String> getExecutorIds() {
      return this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().keySet().toSeq();
   }

   public final boolean requestExecutors(int numAdditionalExecutors) {
      if (numAdditionalExecutors < 0) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)"Attempted to request a negative number of additional executor(s) ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " from the cluster manager. Please specify a positive number!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(numAdditionalExecutors)}))).toString());
      } else {
         this.logInfo(new Serializable(this, numAdditionalExecutors) {
            public static final long serialVersionUID = 0L;
            private final int numAdditionalExecutors$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Requesting ", " additional executor(s) from the cluster manager"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.numAdditionalExecutors$1)}));
            }

            public {
               this.numAdditionalExecutors$1 = numAdditionalExecutors$1;
            }
         });
         Future var4;
         synchronized(this) {
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors_$eq(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors() + numAdditionalExecutors);
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors_$eq(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors() + numAdditionalExecutors);
            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final CoarseGrainedSchedulerBackend $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Number of pending executors is now ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors())}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            if (this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors() != this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numExistingExecutors() + this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors() - this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().size()) {
               this.logDebug(new Serializable(this, numAdditionalExecutors) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final CoarseGrainedSchedulerBackend $outer;
                  private final int numAdditionalExecutors$1;

                  public final String apply() {
                     return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"requestExecutors(", "): Executor request doesn't match:\n             |requestedTotalExecutors  = ", "\n             |numExistingExecutors     = ", "\n             |numPendingExecutors      = ", "\n             |executorsPendingToRemove = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.numAdditionalExecutors$1), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numExistingExecutors()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().size())}))))).stripMargin();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.numAdditionalExecutors$1 = numAdditionalExecutors$1;
                     }
                  }
               });
            }

            var4 = this.doRequestTotalExecutors(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors());
         }

         Future response = var4;
         return BoxesRunTime.unboxToBoolean(this.defaultAskTimeout().awaitResult(response));
      }
   }

   public final boolean requestTotalExecutors(int numExecutors, int localityAwareTasks, Map<String, Object> hostToLocalTaskCount) {
      if (numExecutors < 0) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)"Attempted to request a negative number of executor(s) ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " from the cluster manager. Please specify a positive number!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(numExecutors)}))).toString());
      } else {
         Future var6;
         synchronized(this) {
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors_$eq(numExecutors);
            this.localityAwareTasks_$eq(localityAwareTasks);
            this.hostToLocalTaskCount_$eq(hostToLocalTaskCount);
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors_$eq(package$.MODULE$.max(numExecutors - this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numExistingExecutors() + this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().size(), 0));
            var6 = this.doRequestTotalExecutors(numExecutors);
         }

         Future response = var6;
         return BoxesRunTime.unboxToBoolean(this.defaultAskTimeout().awaitResult(response));
      }
   }

   public Future<Object> doRequestTotalExecutors(int requestedTotal) {
      return Future$.MODULE$.<Object>successful(BoxesRunTime.boxToBoolean(false));
   }

   public final Seq<String> killExecutors(Seq<String> executorIds, boolean replace, boolean force) {
      this.logInfo(new Serializable(this, executorIds) {
         public static final long serialVersionUID = 0L;
         private final Seq executorIds$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Requesting to kill executor(s) ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorIds$1.mkString(", ")}));
         }

         public {
            this.executorIds$1 = executorIds$1;
         }
      });
      Future var6;
      synchronized(this) {
         Tuple2 var8 = executorIds.partition(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final CoarseGrainedSchedulerBackend $outer;

            public final boolean apply(String key) {
               return this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().contains(key);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         if (var8 == null) {
            throw new MatchError(var8);
         }

         Seq knownExecutors = (Seq)var8._1();
         Seq unknownExecutors = (Seq)var8._2();
         Tuple2 var11 = new Tuple2(knownExecutors, unknownExecutors);
         Seq knownExecutors = (Seq)var11._1();
         Seq unknownExecutors = (Seq)var11._2();
         unknownExecutors.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final CoarseGrainedSchedulerBackend $outer;

            public final void apply(String id) {
               this.$outer.logWarning(new Serializable(this, id) {
                  public static final long serialVersionUID = 0L;
                  private final String id$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor to kill ", " does not exist!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.id$1}));
                  }

                  public {
                     this.id$1 = id$1;
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
         Seq executorsToKill = (Seq)((TraversableLike)knownExecutors.filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final CoarseGrainedSchedulerBackend $outer;

            public final boolean apply(String id) {
               return !this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().contains(id);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         })).filter(new Serializable(this, force) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final CoarseGrainedSchedulerBackend $outer;
            private final boolean force$1;

            public final boolean apply(String id) {
               return this.force$1 || !this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.isExecutorBusy(id);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.force$1 = force$1;
               }
            }
         });
         executorsToKill.foreach(new Serializable(this, replace) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final CoarseGrainedSchedulerBackend $outer;
            private final boolean replace$1;

            public final void apply(String id) {
               this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().update(id, BoxesRunTime.boxToBoolean(!this.replace$1));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.replace$1 = replace$1;
               }
            }
         });
         this.logInfo(new Serializable(this, executorsToKill) {
            public static final long serialVersionUID = 0L;
            private final Seq executorsToKill$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Actual list of executor(s) to be killed is ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorsToKill$1.mkString(", ")}));
            }

            public {
               this.executorsToKill$1 = executorsToKill$1;
            }
         });
         Future var10000;
         if (replace) {
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors_$eq(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors() + knownExecutors.size());
            var10000 = Future$.MODULE$.successful(BoxesRunTime.boxToBoolean(true));
         } else {
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors_$eq(package$.MODULE$.max(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors() - executorsToKill.size(), 0));
            if (this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors() != this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numExistingExecutors() + this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors() - this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().size()) {
               this.logDebug(new Serializable(this, executorIds, replace, force) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final CoarseGrainedSchedulerBackend $outer;
                  private final Seq executorIds$1;
                  private final boolean replace$1;
                  private final boolean force$1;

                  public final String apply() {
                     return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"killExecutors(", ", ", ", ", "): Executor counts do not match:\n                 |requestedTotalExecutors  = ", "\n                 |numExistingExecutors     = ", "\n                 |numPendingExecutors      = ", "\n                 |executorsPendingToRemove = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorIds$1, BoxesRunTime.boxToBoolean(this.replace$1), BoxesRunTime.boxToBoolean(this.force$1), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numExistingExecutors()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().size())}))))).stripMargin();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.executorIds$1 = executorIds$1;
                        this.replace$1 = replace$1;
                        this.force$1 = force$1;
                     }
                  }
               });
            }

            var10000 = this.doRequestTotalExecutors(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors());
         }

         Future adjustTotalExecutors = var10000;
         Function1 killExecutors = executorsToKill.isEmpty() ? new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Future<Object> apply(boolean x$8) {
               return Future$.MODULE$.<Object>successful(BoxesRunTime.boxToBoolean(false));
            }
         } : new Serializable(this, executorsToKill) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final CoarseGrainedSchedulerBackend $outer;
            private final Seq executorsToKill$1;

            public final Future<Object> apply(boolean x$7) {
               return this.$outer.doKillExecutors(this.executorsToKill$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.executorsToKill$1 = executorsToKill$1;
               }
            }
         };
         Future killResponse = adjustTotalExecutors.flatMap(killExecutors, ThreadUtils$.MODULE$.sameThread());
         var6 = killResponse.flatMap(new Serializable(this, executorsToKill) {
            public static final long serialVersionUID = 0L;
            private final Seq executorsToKill$1;

            public final Future<Seq<String>> apply(boolean killSuccessful) {
               return Future$.MODULE$.<Seq<String>>successful(killSuccessful ? this.executorsToKill$1 : Seq$.MODULE$.empty());
            }

            public {
               this.executorsToKill$1 = executorsToKill$1;
            }
         }, ThreadUtils$.MODULE$.sameThread());
      }

      Future response = var6;
      return (Seq)this.defaultAskTimeout().awaitResult(response);
   }

   public Future<Object> doKillExecutors(Seq<String> executorIds) {
      return Future$.MODULE$.<Object>successful(BoxesRunTime.boxToBoolean(false));
   }

   public final boolean killExecutorsOnHost(String host) {
      this.logInfo(new Serializable(this, host) {
         public static final long serialVersionUID = 0L;
         private final String host$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Requesting to kill any and all executors on host ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.host$1}));
         }

         public {
            this.host$1 = host$1;
         }
      });
      this.driverEndpoint().send(new CoarseGrainedClusterMessages.KillExecutorsOnHost(host));
      return true;
   }

   public Option<byte[]> fetchHadoopDelegationTokens() {
      return None$.MODULE$;
   }

   public CoarseGrainedSchedulerBackend(TaskSchedulerImpl scheduler, RpcEnv rpcEnv) {
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler = scheduler;
      this.rpcEnv = rpcEnv;
      ExecutorAllocationClient$class.$init$(this);
      SchedulerBackend$class.$init$(this);
      Logging$class.$init$(this);
      this.totalCoreCount = new AtomicInteger(0);
      this.totalRegisteredExecutors = new AtomicInteger(0);
      this.conf = scheduler.sc().conf();
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRpcMessageSize = RpcUtils$.MODULE$.maxMessageSizeBytes(this.conf());
      this.defaultAskTimeout = RpcUtils$.MODULE$.askRpcTimeout(this.conf());
      this._minRegisteredRatio = package$.MODULE$.min((double)1.0F, this.conf().getDouble("spark.scheduler.minRegisteredResourcesRatio", (double)0.0F));
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRegisteredWaitingTimeMs = this.conf().getTimeAsMs("spark.scheduler.maxRegisteredResourcesWaitingTime", "30s");
      this.createTime = System.currentTimeMillis();
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap = new HashMap<String, ExecutorData>();
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$requestedTotalExecutors = 0;
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors = 0;
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$listenerBus = scheduler.sc().listenerBus();
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove = new HashMap<String, Object>();
      this.hostToLocalTaskCount = Predef$.MODULE$.Map().empty();
      this.localityAwareTasks = 0;
      this.currentExecutorIdCounter = 0;
      this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$reviveThread = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("driver-revive-thread");
      this.driverEndpoint = null;
   }

   public class DriverEndpoint implements ThreadSafeRpcEndpoint, Logging {
      private final RpcEnv rpcEnv;
      public final Seq<Tuple2<String, String>> org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$sparkProperties;
      private final HashSet<String> executorsPendingLossReason;
      private final HashMap<RpcAddress, String> addressToExecutorId;
      // $FF: synthetic field
      public final CoarseGrainedSchedulerBackend $outer;
      private transient Logger org$apache$spark$internal$Logging$$log_;

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

      public void onStop() {
         RpcEndpoint$class.onStop(this);
      }

      public final void stop() {
         RpcEndpoint$class.stop(this);
      }

      public RpcEnv rpcEnv() {
         return this.rpcEnv;
      }

      public HashSet<String> executorsPendingLossReason() {
         return this.executorsPendingLossReason;
      }

      public HashMap<RpcAddress, String> addressToExecutorId() {
         return this.addressToExecutorId;
      }

      public void onStart() {
         long reviveIntervalMs = this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().conf().getTimeAsMs("spark.scheduler.revive.interval", "1s");
         this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$reviveThread().scheduleAtFixedRate(new Runnable(this) {
            // $FF: synthetic field
            private final DriverEndpoint $outer;

            public void run() {
               Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     Option$.MODULE$.apply(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$anon$$$outer().self()).foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final void apply(RpcEndpointRef x$1) {
                           x$1.send(CoarseGrainedClusterMessages.ReviveOffers$.MODULE$);
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
            public DriverEndpoint org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$anon$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, 0L, reviveIntervalMs, TimeUnit.MILLISECONDS);
      }

      public PartialFunction<Object, BoxedUnit> receive() {
         return new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DriverEndpoint $outer;

            public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
               Object var10;
               if (x1 instanceof CoarseGrainedClusterMessages.StatusUpdate) {
                  CoarseGrainedClusterMessages.StatusUpdate var4 = (CoarseGrainedClusterMessages.StatusUpdate)x1;
                  String executorId = var4.executorId();
                  long taskId = var4.taskId();
                  Enumeration.Value state = var4.state();
                  SerializableBuffer data = var4.data();
                  this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.statusUpdate(taskId, state, data.value());
                  BoxedUnit var10000;
                  if (TaskState$.MODULE$.isFinished(state)) {
                     Option var11 = this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().get(executorId);
                     if (var11 instanceof Some) {
                        Some var12 = (Some)var11;
                        ExecutorData executorInfo = (ExecutorData)var12.x();
                        executorInfo.freeCores_$eq(executorInfo.freeCores() + this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.CPUS_PER_TASK());
                        this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$makeOffers(executorId);
                        BoxedUnit var14 = BoxedUnit.UNIT;
                     } else {
                        if (!None$.MODULE$.equals(var11)) {
                           throw new MatchError(var11);
                        }

                        this.$outer.logWarning(new Serializable(this, executorId, taskId, state) {
                           public static final long serialVersionUID = 0L;
                           private final String executorId$1;
                           private final long taskId$1;
                           private final Enumeration.Value state$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ignored task status update (", " state ", ") "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskId$1), this.state$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"from unknown executor with ID ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$1}))).toString();
                           }

                           public {
                              this.executorId$1 = executorId$1;
                              this.taskId$1 = taskId$1;
                              this.state$1 = state$1;
                           }
                        });
                        BoxedUnit var32 = BoxedUnit.UNIT;
                     }

                     var10000 = BoxedUnit.UNIT;
                  } else {
                     var10000 = BoxedUnit.UNIT;
                  }

                  var10 = var10000;
               } else if (CoarseGrainedClusterMessages.ReviveOffers$.MODULE$.equals(x1)) {
                  this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$makeOffers();
                  var10 = BoxedUnit.UNIT;
               } else if (x1 instanceof CoarseGrainedClusterMessages.KillTask) {
                  CoarseGrainedClusterMessages.KillTask var15 = (CoarseGrainedClusterMessages.KillTask)x1;
                  long taskId = var15.taskId();
                  String executorId = var15.executor();
                  boolean interruptThread = var15.interruptThread();
                  String reason = var15.reason();
                  Option var21 = this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().get(executorId);
                  if (var21 instanceof Some) {
                     Some var22 = (Some)var21;
                     ExecutorData executorInfo = (ExecutorData)var22.x();
                     executorInfo.executorEndpoint().send(new CoarseGrainedClusterMessages.KillTask(taskId, executorId, interruptThread, reason));
                     BoxedUnit var24 = BoxedUnit.UNIT;
                  } else {
                     if (!None$.MODULE$.equals(var21)) {
                        throw new MatchError(var21);
                     }

                     this.$outer.logWarning(new Serializable(this, taskId, executorId) {
                        public static final long serialVersionUID = 0L;
                        private final long taskId$2;
                        private final String executorId$2;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to kill task ", " for unknown executor ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskId$2), this.executorId$2}));
                        }

                        public {
                           this.taskId$2 = taskId$2;
                           this.executorId$2 = executorId$2;
                        }
                     });
                     BoxedUnit var33 = BoxedUnit.UNIT;
                  }

                  var10 = BoxedUnit.UNIT;
               } else if (x1 instanceof CoarseGrainedClusterMessages.KillExecutorsOnHost) {
                  CoarseGrainedClusterMessages.KillExecutorsOnHost var25 = (CoarseGrainedClusterMessages.KillExecutorsOnHost)x1;
                  String host = var25.host();
                  this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.getExecutorsAliveOnHost(host).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final Seq<String> apply(scala.collection.Set<String> exec) {
                        return this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$anonfun$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().killExecutors(exec.toSeq(), true, true);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  var10 = BoxedUnit.UNIT;
               } else if (x1 instanceof CoarseGrainedClusterMessages.UpdateDelegationTokens) {
                  CoarseGrainedClusterMessages.UpdateDelegationTokens var27 = (CoarseGrainedClusterMessages.UpdateDelegationTokens)x1;
                  byte[] newDelegationTokens = var27.tokens();
                  this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().values().foreach(new Serializable(this, newDelegationTokens) {
                     public static final long serialVersionUID = 0L;
                     private final byte[] newDelegationTokens$1;

                     public final void apply(ExecutorData ed) {
                        ed.executorEndpoint().send(new CoarseGrainedClusterMessages.UpdateDelegationTokens(this.newDelegationTokens$1));
                     }

                     public {
                        this.newDelegationTokens$1 = newDelegationTokens$1;
                     }
                  });
                  var10 = BoxedUnit.UNIT;
               } else if (x1 instanceof CoarseGrainedClusterMessages.RemoveExecutor) {
                  CoarseGrainedClusterMessages.RemoveExecutor var29 = (CoarseGrainedClusterMessages.RemoveExecutor)x1;
                  String executorId = var29.executorId();
                  ExecutorLossReason reason = var29.reason();
                  this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().get(executorId).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final void apply(ExecutorData x$2) {
                        x$2.executorEndpoint().send(CoarseGrainedClusterMessages.StopExecutor$.MODULE$);
                     }
                  });
                  this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$removeExecutor(executorId, reason);
                  var10 = BoxedUnit.UNIT;
               } else {
                  var10 = var2.apply(x1);
               }

               return (B1)var10;
            }

            public final boolean isDefinedAt(Object x1) {
               boolean var3;
               if (x1 instanceof CoarseGrainedClusterMessages.StatusUpdate) {
                  var3 = true;
               } else if (CoarseGrainedClusterMessages.ReviveOffers$.MODULE$.equals(x1)) {
                  var3 = true;
               } else if (x1 instanceof CoarseGrainedClusterMessages.KillTask) {
                  var3 = true;
               } else if (x1 instanceof CoarseGrainedClusterMessages.KillExecutorsOnHost) {
                  var3 = true;
               } else if (x1 instanceof CoarseGrainedClusterMessages.UpdateDelegationTokens) {
                  var3 = true;
               } else if (x1 instanceof CoarseGrainedClusterMessages.RemoveExecutor) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }

            // $FF: synthetic method
            public DriverEndpoint org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$anonfun$$$outer() {
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
            private final DriverEndpoint $outer;
            private final RpcCallContext context$1;

            public final <A1, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
               Object var10;
               if (x2 instanceof CoarseGrainedClusterMessages.RegisterExecutor) {
                  CoarseGrainedClusterMessages.RegisterExecutor var4 = (CoarseGrainedClusterMessages.RegisterExecutor)x2;
                  String executorId = var4.executorId();
                  RpcEndpointRef executorRef = var4.executorRef();
                  String hostname = var4.hostname();
                  int cores = var4.cores();
                  Map logUrls = var4.logUrls();
                  BoxedUnit var21;
                  if (this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().contains(executorId)) {
                     executorRef.send(new CoarseGrainedClusterMessages.RegisterExecutorFailed((new StringBuilder()).append((Object)"Duplicate executor ID: ").append((Object)executorId).toString()));
                     this.context$1.reply(BoxesRunTime.boxToBoolean(true));
                     var21 = BoxedUnit.UNIT;
                  } else if (this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.nodeBlacklist() != null && this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.nodeBlacklist().contains(hostname)) {
                     this.$outer.logInfo(new Serializable(this, executorId) {
                        public static final long serialVersionUID = 0L;
                        private final String executorId$4;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Rejecting ", " as it has been blacklisted."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$4}));
                        }

                        public {
                           this.executorId$4 = executorId$4;
                        }
                     });
                     executorRef.send(new CoarseGrainedClusterMessages.RegisterExecutorFailed((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor is blacklisted: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{executorId}))));
                     this.context$1.reply(BoxesRunTime.boxToBoolean(true));
                     var21 = BoxedUnit.UNIT;
                  } else {
                     RpcAddress executorAddress = executorRef.address() == null ? this.context$1.senderAddress() : executorRef.address();
                     this.$outer.logInfo(new Serializable(this, executorId, executorRef, executorAddress) {
                        public static final long serialVersionUID = 0L;
                        private final String executorId$4;
                        private final RpcEndpointRef executorRef$1;
                        private final RpcAddress executorAddress$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Registered executor ", " (", ") with ID ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorRef$1, this.executorAddress$1, this.executorId$4}));
                        }

                        public {
                           this.executorId$4 = executorId$4;
                           this.executorRef$1 = executorRef$1;
                           this.executorAddress$1 = executorAddress$1;
                        }
                     });
                     this.$outer.addressToExecutorId().update(executorAddress, executorId);
                     this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().totalCoreCount().addAndGet(cores);
                     this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().totalRegisteredExecutors().addAndGet(1);
                     ExecutorData data = new ExecutorData(executorRef, executorAddress, hostname, cores, cores, logUrls);
                     synchronized(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer()) {
                        this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().put(executorId, data);
                        if (this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().currentExecutorIdCounter() < (new StringOps(Predef$.MODULE$.augmentString(executorId))).toInt()) {
                           this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().currentExecutorIdCounter_$eq((new StringOps(Predef$.MODULE$.augmentString(executorId))).toInt());
                        }

                        if (this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors() > 0) {
                           this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors_$eq(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors() - 1);
                           this.$outer.logDebug(new Serializable(this) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;

                              public final String apply() {
                                 return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Decremented number of pending executors (", " left)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$anonfun$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$numPendingExecutors())}));
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                 }
                              }
                           });
                           var21 = BoxedUnit.UNIT;
                        } else {
                           var21 = BoxedUnit.UNIT;
                        }
                     }

                     executorRef.send(CoarseGrainedClusterMessages.RegisteredExecutor$.MODULE$);
                     this.context$1.reply(BoxesRunTime.boxToBoolean(true));
                     this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$listenerBus().post(new SparkListenerExecutorAdded(System.currentTimeMillis(), executorId, data));
                     this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$makeOffers();
                     var21 = BoxedUnit.UNIT;
                  }

                  var10 = var21;
               } else if (CoarseGrainedClusterMessages.StopDriver$.MODULE$.equals(x2)) {
                  this.context$1.reply(BoxesRunTime.boxToBoolean(true));
                  this.$outer.stop();
                  var10 = BoxedUnit.UNIT;
               } else if (CoarseGrainedClusterMessages.StopExecutors$.MODULE$.equals(x2)) {
                  this.$outer.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Asking each executor to shut down";
                     }
                  });
                  this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().withFilter(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final boolean apply(Tuple2<String, ExecutorData> check$ifrefutable$1) {
                        boolean var3;
                        if (check$ifrefutable$1 != null) {
                           var3 = true;
                        } else {
                           var3 = false;
                        }

                        return var3;
                     }
                  }).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final void apply(Tuple2<String, ExecutorData> x$3) {
                        if (x$3 != null) {
                           ExecutorData executorData = (ExecutorData)x$3._2();
                           executorData.executorEndpoint().send(CoarseGrainedClusterMessages.StopExecutor$.MODULE$);
                           BoxedUnit var4 = BoxedUnit.UNIT;
                        } else {
                           throw new MatchError(x$3);
                        }
                     }
                  });
                  this.context$1.reply(BoxesRunTime.boxToBoolean(true));
                  var10 = BoxedUnit.UNIT;
               } else if (x2 instanceof CoarseGrainedClusterMessages.RemoveWorker) {
                  CoarseGrainedClusterMessages.RemoveWorker var14 = (CoarseGrainedClusterMessages.RemoveWorker)x2;
                  String workerId = var14.workerId();
                  String host = var14.host();
                  String message = var14.message();
                  this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$removeWorker(workerId, host, message);
                  this.context$1.reply(BoxesRunTime.boxToBoolean(true));
                  var10 = BoxedUnit.UNIT;
               } else if (CoarseGrainedClusterMessages.RetrieveSparkAppConfig$.MODULE$.equals(x2)) {
                  CoarseGrainedClusterMessages.SparkAppConfig reply = new CoarseGrainedClusterMessages.SparkAppConfig(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$sparkProperties, SparkEnv$.MODULE$.get().securityManager().getIOEncryptionKey(), this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().fetchHadoopDelegationTokens());
                  this.context$1.reply(reply);
                  var10 = BoxedUnit.UNIT;
               } else {
                  var10 = var2.apply(x2);
               }

               return (B1)var10;
            }

            public final boolean isDefinedAt(Object x2) {
               boolean var3;
               if (x2 instanceof CoarseGrainedClusterMessages.RegisterExecutor) {
                  var3 = true;
               } else if (CoarseGrainedClusterMessages.StopDriver$.MODULE$.equals(x2)) {
                  var3 = true;
               } else if (CoarseGrainedClusterMessages.StopExecutors$.MODULE$.equals(x2)) {
                  var3 = true;
               } else if (x2 instanceof CoarseGrainedClusterMessages.RemoveWorker) {
                  var3 = true;
               } else if (CoarseGrainedClusterMessages.RetrieveSparkAppConfig$.MODULE$.equals(x2)) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }

            // $FF: synthetic method
            public DriverEndpoint org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$anonfun$$$outer() {
               return this.$outer;
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

      public void org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$makeOffers() {
         Seq var3;
         synchronized(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer()) {
            scala.collection.Map activeExecutors = this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().filterKeys(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DriverEndpoint $outer;

               public final boolean apply(String executorId) {
                  return this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$executorIsAlive(executorId);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            IndexedSeq workOffers = ((TraversableOnce)activeExecutors.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final WorkerOffer apply(Tuple2<String, ExecutorData> x0$1) {
                  if (x0$1 != null) {
                     String id = (String)x0$1._1();
                     ExecutorData executorData = (ExecutorData)x0$1._2();
                     WorkerOffer var5 = new WorkerOffer(id, executorData.executorHost(), executorData.freeCores());
                     return var5;
                  } else {
                     throw new MatchError(x0$1);
                  }
               }
            }, Iterable$.MODULE$.canBuildFrom())).toIndexedSeq();
            var3 = this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.resourceOffers(workOffers);
         }

         Seq taskDescs = var3;
         if (!taskDescs.isEmpty()) {
            this.launchTasks(taskDescs);
         }

      }

      public void onDisconnected(RpcAddress remoteAddress) {
         this.addressToExecutorId().get(remoteAddress).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DriverEndpoint $outer;

            public final void apply(String x$4) {
               this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$removeExecutor(x$4, new SlaveLost("Remote RPC client disassociated. Likely due to containers exceeding thresholds, or network issues. Check driver logs for WARN messages.", SlaveLost$.MODULE$.apply$default$2()));
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

      public void org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$makeOffers(String executorId) {
         Object var4;
         synchronized(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer()) {
            Object var10000;
            if (this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$executorIsAlive(executorId)) {
               ExecutorData executorData = (ExecutorData)this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().apply(executorId);
               scala.collection.IndexedSeq workOffers = (scala.collection.IndexedSeq)scala.package$.MODULE$.IndexedSeq().apply(Predef$.MODULE$.wrapRefArray(new WorkerOffer[]{new WorkerOffer(executorId, executorData.executorHost(), executorData.freeCores())}));
               var10000 = this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.resourceOffers(workOffers);
            } else {
               var10000 = Seq$.MODULE$.empty();
            }

            var4 = var10000;
         }

         Seq taskDescs = (Seq)var4;
         if (!taskDescs.isEmpty()) {
            this.launchTasks(taskDescs);
         }

      }

      public synchronized boolean org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$executorIsAlive(String executorId) {
         return !this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().contains(executorId) && !this.executorsPendingLossReason().contains(executorId);
      }

      private void launchTasks(Seq<Seq<TaskDescription>> tasks) {
         ((IterableLike)tasks.flatten(Predef$.MODULE$.$conforms())).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DriverEndpoint $outer;

            public final void apply(TaskDescription task) {
               ByteBuffer serializedTask = TaskDescription$.MODULE$.encode(task);
               if (serializedTask.limit() >= this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRpcMessageSize()) {
                  this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.taskIdToTaskSetManager().get(BoxesRunTime.boxToLong(task.taskId())).foreach(new Serializable(this, serializedTask, task) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final ByteBuffer serializedTask$1;
                     private final TaskDescription task$1;

                     public final void apply(TaskSetManager taskSetMgr) {
                        try {
                           String msg = "Serialized task %s:%d was %d bytes, which exceeds max allowed: spark.rpc.message.maxSize (%d bytes). Consider increasing spark.rpc.message.maxSize or using broadcast variables for large values.";
                           msg = (new StringOps(Predef$.MODULE$.augmentString(msg))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.task$1.taskId()), BoxesRunTime.boxToInteger(this.task$1.index()), BoxesRunTime.boxToInteger(this.serializedTask$1.limit()), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$anonfun$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$maxRpcMessageSize())}));
                           taskSetMgr.abort(msg, taskSetMgr.abort$default$2());
                        } catch (Exception var4) {
                           this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$anonfun$$$outer().logError(new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final String apply() {
                                 return "Exception in error callback";
                              }
                           }, var4);
                        }

                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.serializedTask$1 = serializedTask$1;
                           this.task$1 = task$1;
                        }
                     }
                  });
               } else {
                  ExecutorData executorData = (ExecutorData)this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().apply(task.executorId());
                  executorData.freeCores_$eq(executorData.freeCores() - this.$outer.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.CPUS_PER_TASK());
                  this.$outer.logDebug(new Serializable(this, executorData, task) {
                     public static final long serialVersionUID = 0L;
                     private final ExecutorData executorData$1;
                     private final TaskDescription task$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Launching task ", " on executor id: ", " hostname: "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.task$1.taskId()), this.task$1.executorId()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorData$1.executorHost()}))).toString();
                     }

                     public {
                        this.executorData$1 = executorData$1;
                        this.task$1 = task$1;
                     }
                  });
                  executorData.executorEndpoint().send(new CoarseGrainedClusterMessages.LaunchTask(new SerializableBuffer(serializedTask)));
               }

            }

            // $FF: synthetic method
            public DriverEndpoint org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$anonfun$$$outer() {
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

      public void org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$removeExecutor(String executorId, ExecutorLossReason reason) {
         this.logDebug(new Serializable(this, executorId, reason) {
            public static final long serialVersionUID = 0L;
            private final String executorId$3;
            private final ExecutorLossReason reason$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Asked to remove executor ", " with reason ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$3, this.reason$1}));
            }

            public {
               this.executorId$3 = executorId$3;
               this.reason$1 = reason$1;
            }
         });
         Option var3 = this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().get(executorId);
         if (var3 instanceof Some) {
            Some var4 = (Some)var3;
            ExecutorData executorInfo = (ExecutorData)var4.x();
            Object var9;
            synchronized(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer()) {
               this.addressToExecutorId().$minus$eq(executorInfo.executorAddress());
               this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap().$minus$eq(executorId);
               this.executorsPendingLossReason().$minus$eq(executorId);
               var9 = this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().remove(executorId).getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final boolean apply() {
                     return this.apply$mcZ$sp();
                  }

                  public boolean apply$mcZ$sp() {
                     return false;
                  }
               });
            }

            boolean killed = BoxesRunTime.unboxToBoolean(var9);
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().totalCoreCount().addAndGet(-executorInfo.totalCores());
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().totalRegisteredExecutors().addAndGet(-1);
            Object var10002 = killed ? ExecutorKilled$.MODULE$ : reason;
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.executorLost(executorId, (ExecutorLossReason)var10002);
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$listenerBus().post(new SparkListenerExecutorRemoved(System.currentTimeMillis(), executorId, reason.toString()));
            BoxedUnit var6 = BoxedUnit.UNIT;
         } else {
            if (!None$.MODULE$.equals(var3)) {
               throw new MatchError(var3);
            }

            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.sc().env().blockManager().master().removeExecutorAsync(executorId);
            this.logInfo(new Serializable(this, executorId) {
               public static final long serialVersionUID = 0L;
               private final String executorId$3;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Asked to remove non-existent executor ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$3}));
               }

               public {
                  this.executorId$3 = executorId$3;
               }
            });
            BoxedUnit var11 = BoxedUnit.UNIT;
         }

      }

      public void org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$removeWorker(String workerId, String host, String message) {
         this.logDebug(new Serializable(this, workerId, message) {
            public static final long serialVersionUID = 0L;
            private final String workerId$1;
            private final String message$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Asked to remove worker ", " with reason ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.workerId$1, this.message$1}));
            }

            public {
               this.workerId$1 = workerId$1;
               this.message$1 = message$1;
            }
         });
         this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.workerRemoved(workerId, host, message);
      }

      public boolean disableExecutor(String executorId) {
         Boolean var4;
         synchronized(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer()) {
            Boolean var10000;
            if (this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$executorIsAlive(executorId)) {
               this.executorsPendingLossReason().$plus$eq(executorId);
               var10000 = BoxesRunTime.boxToBoolean(true);
            } else {
               var10000 = BoxesRunTime.boxToBoolean(this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorsPendingToRemove().contains(executorId));
            }

            var4 = var10000;
         }

         boolean shouldDisable = BoxesRunTime.unboxToBoolean(var4);
         if (shouldDisable) {
            this.logInfo(new Serializable(this, executorId) {
               public static final long serialVersionUID = 0L;
               private final String executorId$5;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Disabling executor ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$5}));
               }

               public {
                  this.executorId$5 = executorId$5;
               }
            });
            this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer().org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$scheduler.executorLost(executorId, LossReasonPending$.MODULE$);
         }

         return shouldDisable;
      }

      // $FF: synthetic method
      public CoarseGrainedSchedulerBackend org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$$outer() {
         return this.$outer;
      }

      public DriverEndpoint(CoarseGrainedSchedulerBackend rpcEnv, RpcEnv sparkProperties) {
         this.rpcEnv = rpcEnv;
         this.org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$DriverEndpoint$$sparkProperties = sparkProperties;
         if (CoarseGrainedSchedulerBackend.this == null) {
            throw null;
         } else {
            this.$outer = CoarseGrainedSchedulerBackend.this;
            super();
            RpcEndpoint$class.$init$(this);
            Logging$class.$init$(this);
            this.executorsPendingLossReason = new HashSet<String>();
            this.addressToExecutorId = new HashMap<RpcAddress, String>();
         }
      }
   }
}
