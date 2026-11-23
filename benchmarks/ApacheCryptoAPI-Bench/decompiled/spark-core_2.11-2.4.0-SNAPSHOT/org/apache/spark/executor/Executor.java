package org.apache.spark.executor;

import java.io.File;
import java.io.NotSerializableException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.ExceptionFailure;
import org.apache.spark.Heartbeat;
import org.apache.spark.HeartbeatReceiver$;
import org.apache.spark.HeartbeatResponse;
import org.apache.spark.MapOutputTrackerWorker;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkException;
import org.apache.spark.SparkFiles$;
import org.apache.spark.TaskCommitDenied;
import org.apache.spark.TaskFailedReason;
import org.apache.spark.TaskKilled;
import org.apache.spark.TaskKilledException;
import org.apache.spark.TaskState$;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.memory.SparkOutOfMemoryError;
import org.apache.spark.memory.TaskMemoryManager;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcTimeout$;
import org.apache.spark.scheduler.AccumulableInfo;
import org.apache.spark.scheduler.DirectTaskResult;
import org.apache.spark.scheduler.IndirectTaskResult;
import org.apache.spark.scheduler.Task;
import org.apache.spark.scheduler.TaskDescription;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.shuffle.FetchFailedException;
import org.apache.spark.storage.StorageLevel$;
import org.apache.spark.storage.TaskResultBlockId;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.CausedBy$;
import org.apache.spark.util.ChildFirstURLClassLoader;
import org.apache.spark.util.MutableURLClassLoader;
import org.apache.spark.util.RpcUtils$;
import org.apache.spark.util.ShutdownHookManager$;
import org.apache.spark.util.ThreadStackTrace;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.UninterruptibleThread;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.io.ChunkedByteBuffer;
import org.slf4j.Logger;
import org.spark_project.guava.util.concurrent.ThreadFactoryBuilder;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.IterableLike;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Set$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.Buffer$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashMap$;
import scala.collection.mutable.Map;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ObjectRef;
import scala.runtime.VolatileByteRef;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\r]g!B\u0001\u0003\u0001\u0011Q!\u0001C#yK\u000e,Ho\u001c:\u000b\u0005\r!\u0011\u0001C3yK\u000e,Ho\u001c:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c2\u0001A\u0006\u0012!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011!#F\u0007\u0002')\u0011A\u0003B\u0001\tS:$XM\u001d8bY&\u0011ac\u0005\u0002\b\u0019><w-\u001b8h\u0011!A\u0002A!A!\u0002\u0013Q\u0012AC3yK\u000e,Ho\u001c:JI\u000e\u0001\u0001CA\u000e\u001f\u001d\taA$\u0003\u0002\u001e\u001b\u00051\u0001K]3eK\u001aL!a\b\u0011\u0003\rM#(/\u001b8h\u0015\tiR\u0002\u0003\u0005#\u0001\t\u0005\t\u0015!\u0003\u001b\u0003A)\u00070Z2vi>\u0014\bj\\:u]\u0006lW\r\u0003\u0005%\u0001\t\u0005\t\u0015!\u0003&\u0003\r)gN\u001e\t\u0003M\u001dj\u0011\u0001B\u0005\u0003Q\u0011\u0011\u0001b\u00159be.,eN\u001e\u0005\tU\u0001\u0011\t\u0011)A\u0005W\u0005iQo]3s\u00072\f7o\u001d)bi\"\u00042\u0001\f\u001b8\u001d\ti#G\u0004\u0002/c5\tqF\u0003\u000213\u00051AH]8pizJ\u0011AD\u0005\u0003g5\tq\u0001]1dW\u0006<W-\u0003\u00026m\t\u00191+Z9\u000b\u0005Mj\u0001C\u0001\u001d>\u001b\u0005I$B\u0001\u001e<\u0003\rqW\r\u001e\u0006\u0002y\u0005!!.\u0019<b\u0013\tq\u0014HA\u0002V%2C\u0001\u0002\u0011\u0001\u0003\u0002\u0003\u0006I!Q\u0001\bSNdunY1m!\ta!)\u0003\u0002D\u001b\t9!i\\8mK\u0006t\u0007\u0002C#\u0001\u0005\u0003\u0005\u000b\u0011\u0002$\u00021Ut7-Y;hQR,\u0005pY3qi&|g\u000eS1oI2,'\u000f\u0005\u0002H!:\u0011\u0001*\u0014\b\u0003\u0013.s!A\f&\n\u0003qJ!\u0001T\u001e\u0002\t1\fgnZ\u0005\u0003\u001d>\u000ba\u0001\u00165sK\u0006$'B\u0001'<\u0013\t\t&K\u0001\rV]\u000e\fWo\u001a5u\u000bb\u001cW\r\u001d;j_:D\u0015M\u001c3mKJT!AT(\t\u000bQ\u0003A\u0011A+\u0002\rqJg.\u001b;?)\u001d1\u0006,\u0017.\\9v\u0003\"a\u0016\u0001\u000e\u0003\tAQ\u0001G*A\u0002iAQAI*A\u0002iAQ\u0001J*A\u0002\u0015BqAK*\u0011\u0002\u0003\u00071\u0006C\u0004A'B\u0005\t\u0019A!\t\u000f\u0015\u001b\u0006\u0013!a\u0001\r\"9q\f\u0001b\u0001\n\u0013\u0001\u0017\u0001D2veJ,g\u000e\u001e$jY\u0016\u001cX#A1\u0011\t\t<'$[\u0007\u0002G*\u0011A-Z\u0001\b[V$\u0018M\u00197f\u0015\t1W\"\u0001\u0006d_2dWm\u0019;j_:L!\u0001[2\u0003\u000f!\u000b7\u000f['baB\u0011AB[\u0005\u0003W6\u0011A\u0001T8oO\"1Q\u000e\u0001Q\u0001\n\u0005\fQbY;se\u0016tGOR5mKN\u0004\u0003bB8\u0001\u0005\u0004%I\u0001Y\u0001\fGV\u0014(/\u001a8u\u0015\u0006\u00148\u000f\u0003\u0004r\u0001\u0001\u0006I!Y\u0001\rGV\u0014(/\u001a8u\u0015\u0006\u00148\u000f\t\u0005\bg\u0002\u0011\r\u0011\"\u0003u\u0003E)U\n\u0015+Z?\nKF+R0C+\u001a3UIU\u000b\u0002kB\u0011a/_\u0007\u0002o*\u0011\u0001pO\u0001\u0004]&|\u0017B\u0001>x\u0005)\u0011\u0015\u0010^3Ck\u001a4WM\u001d\u0005\u0007y\u0002\u0001\u000b\u0011B;\u0002%\u0015k\u0005\u000bV-`\u0005f#Vi\u0018\"V\r\u001a+%\u000b\t\u0005\b}\u0002\u0011\r\u0011\"\u0003\u0000\u0003\u0011\u0019wN\u001c4\u0016\u0005\u0005\u0005\u0001c\u0001\u0014\u0002\u0004%\u0019\u0011Q\u0001\u0003\u0003\u0013M\u0003\u0018M]6D_:4\u0007\u0002CA\u0005\u0001\u0001\u0006I!!\u0001\u0002\u000b\r|gN\u001a\u0011\t\u0013\u00055\u0001A1A\u0005\n\u0005=\u0011A\u0003;ie\u0016\fG\rU8pYV\u0011\u0011\u0011\u0003\t\u0005\u0003'\ti\"\u0004\u0002\u0002\u0016)!\u0011qCA\r\u0003)\u0019wN\\2veJ,g\u000e\u001e\u0006\u0004\u00037Y\u0014\u0001B;uS2LA!a\b\u0002\u0016\t\u0011B\u000b\u001b:fC\u0012\u0004vn\u001c7Fq\u0016\u001cW\u000f^8s\u0011!\t\u0019\u0003\u0001Q\u0001\n\u0005E\u0011a\u0003;ie\u0016\fG\rU8pY\u0002B\u0011\"a\n\u0001\u0005\u0004%I!!\u000b\u0002\u001d\u0015DXmY;u_J\u001cv.\u001e:dKV\u0011\u00111\u0006\t\u0004/\u00065\u0012bAA\u0018\u0005\tqQ\t_3dkR|'oU8ve\u000e,\u0007\u0002CA\u001a\u0001\u0001\u0006I!a\u000b\u0002\u001f\u0015DXmY;u_J\u001cv.\u001e:dK\u0002B\u0011\"a\u000e\u0001\u0005\u0004%I!a\u0004\u0002\u001dQ\f7o\u001b*fCB,'\u000fU8pY\"A\u00111\b\u0001!\u0002\u0013\t\t\"A\buCN\\'+Z1qKJ\u0004vn\u001c7!\u0011%\ty\u0004\u0001b\u0001\n\u0013\t\t%A\tuCN\\'+Z1qKJ4uN\u001d+bg.,\"!a\u0011\u0011\u000b\t<\u0017.!\u0012\u0011\t\u0005\u001d\u0013\u0011J\u0007\u0002\u0001\u00191\u00111\n\u0001\u0005\u0003\u001b\u0012!\u0002V1tWJ+\u0017\r]3s'\u0019\tI%a\u0014\u0002XA!\u0011\u0011KA*\u001b\u0005y\u0015bAA+\u001f\n1qJ\u00196fGR\u0004B!!\u0015\u0002Z%\u0019\u00111L(\u0003\u0011I+hN\\1cY\u0016D1\"a\u0018\u0002J\t\u0005\t\u0015!\u0003\u0002b\u0005QA/Y:l%Vtg.\u001a:\u0011\t\u0005\u001d\u00131\r\u0004\u0007\u0003K\u0002\u0001!a\u001a\u0003\u0015Q\u000b7o\u001b*v]:,'o\u0005\u0004\u0002d\u0005=\u0013q\u000b\u0005\f\u0003W\n\u0019G!A!\u0002\u0013\ti'A\u0006fq\u0016\u001c')Y2lK:$\u0007cA,\u0002p%\u0019\u0011\u0011\u000f\u0002\u0003\u001f\u0015CXmY;u_J\u0014\u0015mY6f]\u0012D1\"!\u001e\u0002d\t\u0015\r\u0011\"\u0003\u0002x\u0005yA/Y:l\t\u0016\u001c8M]5qi&|g.\u0006\u0002\u0002zA!\u00111PAA\u001b\t\tiHC\u0002\u0002\u0000\u0011\t\u0011b]2iK\u0012,H.\u001a:\n\t\u0005\r\u0015Q\u0010\u0002\u0010)\u0006\u001c8\u000eR3tGJL\u0007\u000f^5p]\"Y\u0011qQA2\u0005\u0003\u0005\u000b\u0011BA=\u0003A!\u0018m]6EKN\u001c'/\u001b9uS>t\u0007\u0005C\u0004U\u0003G\"\t!a#\u0015\r\u0005\u0005\u0014QRAH\u0011!\tY'!#A\u0002\u00055\u0004\u0002CA;\u0003\u0013\u0003\r!!\u001f\t\u0015\u0005M\u00151\rb\u0001\n\u0003\t)*\u0001\u0004uCN\\\u0017\nZ\u000b\u0002S\"A\u0011\u0011TA2A\u0003%\u0011.A\u0004uCN\\\u0017\n\u001a\u0011\t\u0015\u0005u\u00151\rb\u0001\n\u0003\ty*\u0001\u0006uQJ,\u0017\r\u001a(b[\u0016,\u0012A\u0007\u0005\t\u0003G\u000b\u0019\u0007)A\u00055\u0005YA\u000f\u001b:fC\u0012t\u0015-\\3!\u0011)\t9+a\u0019C\u0002\u0013%\u0011qT\u0001\ti\u0006\u001c8NT1nK\"A\u00111VA2A\u0003%!$A\u0005uCN\\g*Y7fA!Q\u0011qVA2\u0001\u0004%I!!-\u0002\u001dI,\u0017m]8o\u0013\u001a\\\u0015\u000e\u001c7fIV\u0011\u00111\u0017\t\u0005\u0019\u0005U&$C\u0002\u000286\u0011aa\u00149uS>t\u0007BCA^\u0003G\u0002\r\u0011\"\u0003\u0002>\u0006\u0011\"/Z1t_:LemS5mY\u0016$w\fJ3r)\u0011\ty,!2\u0011\u00071\t\t-C\u0002\u0002D6\u0011A!\u00168ji\"Q\u0011qYA]\u0003\u0003\u0005\r!a-\u0002\u0007a$\u0013\u0007C\u0005\u0002L\u0006\r\u0004\u0015)\u0003\u00024\u0006y!/Z1t_:LemS5mY\u0016$\u0007\u0005\u000b\u0003\u0002J\u0006=\u0007c\u0001\u0007\u0002R&\u0019\u00111[\u0007\u0003\u0011Y|G.\u0019;jY\u0016D!\"a6\u0002d\u0001\u0007I\u0011BAK\u0003!!\bN]3bI&#\u0007BCAn\u0003G\u0002\r\u0011\"\u0003\u0002^\u0006aA\u000f\u001b:fC\u0012LEm\u0018\u0013fcR!\u0011qXAp\u0011%\t9-!7\u0002\u0002\u0003\u0007\u0011\u000e\u0003\u0005\u0002d\u0006\r\u0004\u0015)\u0003j\u0003%!\bN]3bI&#\u0007\u0005\u000b\u0003\u0002b\u0006=\u0007\u0002CAu\u0003G\"\t!!&\u0002\u0017\u001d,G\u000f\u00165sK\u0006$\u0017\n\u001a\u0005\u000b\u0003[\f\u0019\u00071A\u0005\n\u0005=\u0018\u0001\u00034j]&\u001c\b.\u001a3\u0016\u0003\u0005C!\"a=\u0002d\u0001\u0007I\u0011BA{\u000311\u0017N\\5tQ\u0016$w\fJ3r)\u0011\ty,a>\t\u0013\u0005\u001d\u0017\u0011_A\u0001\u0002\u0004\t\u0005\u0002CA~\u0003G\u0002\u000b\u0015B!\u0002\u0013\u0019Lg.[:iK\u0012\u0004\u0003\u0006CA}\u0003\u007f\u0014\tBa\u0005\u0011\t\t\u0005!QB\u0007\u0003\u0005\u0007QA!a\u0006\u0003\u0006)!!q\u0001B\u0005\u0003)\tgN\\8uCRLwN\u001c\u0006\u0003\u0005\u0017\tQA[1wCbLAAa\u0004\u0003\u0004\tIq)^1sI\u0016$')_\u0001\u0006m\u0006dW/Z\u0011\u0003\u0005+\tq\u0002V1tWJ+hN\\3s]QD\u0017n\u001d\u0005\t\u00053\t\u0019\u0007\"\u0001\u0002p\u0006Q\u0011n\u001d$j]&\u001c\b.\u001a3\t\u0019\tu\u00111\ra\u0001\u0002\u0004%\t!!&\u0002\u0017M$\u0018M\u001d;H\u0007RKW.\u001a\u0005\r\u0005C\t\u0019\u00071AA\u0002\u0013\u0005!1E\u0001\u0010gR\f'\u000f^$D)&lWm\u0018\u0013fcR!\u0011q\u0018B\u0013\u0011%\t9Ma\b\u0002\u0002\u0003\u0007\u0011\u000e\u0003\u0005\u0003*\u0005\r\u0004\u0015)\u0003j\u00031\u0019H/\u0019:u\u000f\u000e#\u0016.\\3!Q\u0011\u00119#a4\t\u0019\t=\u00121\ra\u0001\u0002\u0004%\tA!\r\u0002\tQ\f7o[\u000b\u0003\u0005g\u0001b!a\u001f\u00036\te\u0012\u0002\u0002B\u001c\u0003{\u0012A\u0001V1tWB\u0019ABa\u000f\n\u0007\tuRBA\u0002B]fDAB!\u0011\u0002d\u0001\u0007\t\u0019!C\u0001\u0005\u0007\n\u0001\u0002^1tW~#S-\u001d\u000b\u0005\u0003\u007f\u0013)\u0005\u0003\u0006\u0002H\n}\u0012\u0011!a\u0001\u0005gA\u0011B!\u0013\u0002d\u0001\u0006KAa\r\u0002\u000bQ\f7o\u001b\u0011)\t\t\u001d\u0013q\u001a\u0005\t\u0005\u001f\n\u0019\u0007\"\u0001\u0003R\u0005!1.\u001b7m)\u0019\tyLa\u0015\u0003X!9!Q\u000bB'\u0001\u0004\t\u0015aD5oi\u0016\u0014(/\u001e9u)\"\u0014X-\u00193\t\u000f\te#Q\na\u00015\u00051!/Z1t_:D\u0001B!\u0018\u0002d\u0011%!qL\u0001'g\u0016$H+Y:l\r&t\u0017n\u001d5fI\u0006sGm\u00117fCJLe\u000e^3seV\u0004Ho\u0015;biV\u001cHCAA`\u0011!\u0011\u0019'a\u0019\u0005B\t}\u0013a\u0001:v]\"A!qMA2\t\u0013\ty/A\biCN4U\r^2i\r\u0006LG.\u001e:f\u0011-\u0011)&!\u0013\u0003\u0006\u0004%\t!a<\t\u0015\t5\u0014\u0011\nB\u0001B\u0003%\u0011)\u0001\tj]R,'O];qiRC'/Z1eA!Y!\u0011LA%\u0005\u000b\u0007I\u0011AAP\u0011)\u0011\u0019(!\u0013\u0003\u0002\u0003\u0006IAG\u0001\be\u0016\f7o\u001c8!\u0011\u001d!\u0016\u0011\nC\u0001\u0005o\"\u0002\"!\u0012\u0003z\tm$Q\u0010\u0005\t\u0003?\u0012)\b1\u0001\u0002b!9!Q\u000bB;\u0001\u0004\t\u0005b\u0002B-\u0005k\u0002\rA\u0007\u0005\t\u0003'\u000bI\u0005)A\u0005S\"A!1QA%A\u0003%\u0011.A\u000blS2d\u0007k\u001c7mS:<\u0017J\u001c;feZ\fG.T:\t\u0011\t\u001d\u0015\u0011\nQ\u0001\n%\fQb[5mYRKW.Z8vi6\u001b\b\u0002\u0003BF\u0003\u0013\u0002\u000b\u0011B!\u0002\u001dQ\f7.\u001a+ie\u0016\fG\rR;na\"A!1MA%\t\u0003\u0012y\u0006\u0003\u0005\u0003\u0012\u0002\u0001\u000b\u0011BA\"\u0003I!\u0018m]6SK\u0006\u0004XM\u001d$peR\u000b7o\u001b\u0011\t\u0013\tU\u0005A1A\u0005\n\u0005=\u0018AE;tKJ\u001cE.Y:t!\u0006$\bNR5sgRDqA!'\u0001A\u0003%\u0011)A\nvg\u0016\u00148\t\\1tgB\u000bG\u000f\u001b$jeN$\b\u0005C\u0005\u0003\u001e\u0002\u0011\r\u0011\"\u0003\u0002p\u0006\tB/Y:l%\u0016\f\u0007/\u001a:F]\u0006\u0014G.\u001a3\t\u000f\t\u0005\u0006\u0001)A\u0005\u0003\u0006\u0011B/Y:l%\u0016\f\u0007/\u001a:F]\u0006\u0014G.\u001a3!\u0011%\u0011)\u000b\u0001b\u0001\n\u0013\u00119+\u0001\bve2\u001cE.Y:t\u0019>\fG-\u001a:\u0016\u0005\t%\u0006\u0003\u0002BV\u0005_k!A!,\u000b\u0007\u0005mA!\u0003\u0003\u00032\n5&!F'vi\u0006\u0014G.Z+S\u0019\u000ec\u0017m]:M_\u0006$WM\u001d\u0005\t\u0005k\u0003\u0001\u0015!\u0003\u0003*\u0006yQO\u001d7DY\u0006\u001c8\u000fT8bI\u0016\u0014\b\u0005C\u0005\u0003:\u0002\u0011\r\u0011\"\u0003\u0003<\u0006y!/\u001a9m\u00072\f7o\u001d'pC\u0012,'/\u0006\u0002\u0003>B!\u0011\u0011\u000bB`\u0013\r\u0011\tm\u0014\u0002\f\u00072\f7o\u001d'pC\u0012,'\u000f\u0003\u0005\u0003F\u0002\u0001\u000b\u0011\u0002B_\u0003A\u0011X\r\u001d7DY\u0006\u001c8\u000fT8bI\u0016\u0014\b\u0005C\u0005\u0003J\u0002\u0011\r\u0011\"\u0003\u0002\u0016\u0006\u0019R.\u0019=ESJ,7\r\u001e*fgVdGoU5{K\"9!Q\u001a\u0001!\u0002\u0013I\u0017\u0001F7bq\u0012K'/Z2u%\u0016\u001cX\u000f\u001c;TSj,\u0007\u0005C\u0005\u0003R\u0002\u0011\r\u0011\"\u0003\u0002\u0016\u0006iQ.\u0019=SKN,H\u000e^*ju\u0016DqA!6\u0001A\u0003%\u0011.\u0001\bnCb\u0014Vm];miNK'0\u001a\u0011\t\u0013\te\u0007A1A\u0005\n\tm\u0017\u0001\u0004:v]:Lgn\u001a+bg.\u001cXC\u0001Bo!\u001d\t\u0019Ba8j\u0003CJAA!9\u0002\u0016\t\t2i\u001c8dkJ\u0014XM\u001c;ICNDW*\u00199\t\u0011\t\u0015\b\u0001)A\u0005\u0005;\fQB];o]&tw\rV1tWN\u0004\u0003\"\u0003Bu\u0001\t\u0007I\u0011\u0002Bv\u0003-AW-\u0019:uE\u0016\fG/\u001a:\u0016\u0005\t5\b\u0003BA\n\u0005_LAA!=\u0002\u0016\tA2k\u00195fIVdW\rZ#yK\u000e,Ho\u001c:TKJ4\u0018nY3\t\u0011\tU\b\u0001)A\u0005\u0005[\fA\u0002[3beR\u0014W-\u0019;fe\u0002B\u0011B!?\u0001\u0005\u0004%IAa?\u0002)!,\u0017M\u001d;cK\u0006$(+Z2fSZ,'OU3g+\t\u0011i\u0010\u0005\u0003\u0003\u0000\u000e\u0015QBAB\u0001\u0015\r\u0019\u0019\u0001B\u0001\u0004eB\u001c\u0017\u0002BB\u0004\u0007\u0003\u0011aB\u00159d\u000b:$\u0007o\\5oiJ+g\r\u0003\u0005\u0004\f\u0001\u0001\u000b\u0011\u0002B\u007f\u0003UAW-\u0019:uE\u0016\fGOU3dK&4XM\u001d*fM\u0002B\u0011ba\u0004\u0001\u0005\u0004%Ia!\u0005\u0002-!+\u0015I\u0015+C\u000b\u0006#v,T!Y?\u001a\u000b\u0015\nT+S\u000bN+\"aa\u0005\u0011\u00071\u0019)\"C\u0002\u0004\u00185\u00111!\u00138u\u0011!\u0019Y\u0002\u0001Q\u0001\n\rM\u0011a\u0006%F\u0003J#&)R!U?6\u000b\u0005l\u0018$B\u00132+&+R*!\u0011%\u0019y\u0002\u0001a\u0001\n\u0013\u0019\t\"A\tiK\u0006\u0014HOY3bi\u001a\u000b\u0017\u000e\\;sKND\u0011ba\t\u0001\u0001\u0004%Ia!\n\u0002+!,\u0017M\u001d;cK\u0006$h)Y5mkJ,7o\u0018\u0013fcR!\u0011qXB\u0014\u0011)\t9m!\t\u0002\u0002\u0003\u000711\u0003\u0005\t\u0007W\u0001\u0001\u0015)\u0003\u0004\u0014\u0005\u0011\u0002.Z1si\n,\u0017\r\u001e$bS2,(/Z:!\u0011!\u0019y\u0003\u0001C\u0001\u0005\rE\u0011a\u00048v[J+hN\\5oOR\u000b7o[:\t\u000f\rM\u0002\u0001\"\u0001\u00046\u0005QA.Y;oG\"$\u0016m]6\u0015\r\u0005}6qGB\u001e\u0011!\u0019Id!\rA\u0002\u00055\u0014aB2p]R,\u0007\u0010\u001e\u0005\t\u0003k\u001a\t\u00041\u0001\u0002z!91q\b\u0001\u0005\u0002\r\u0005\u0013\u0001C6jY2$\u0016m]6\u0015\u0011\u0005}61IB#\u0007\u000fBq!a%\u0004>\u0001\u0007\u0011\u000eC\u0004\u0003V\ru\u0002\u0019A!\t\u000f\te3Q\ba\u00015!911\n\u0001\u0005\u0002\r5\u0013\u0001D6jY2\fE\u000e\u001c+bg.\u001cHCBA`\u0007\u001f\u001a\t\u0006C\u0004\u0003V\r%\u0003\u0019A!\t\u000f\te3\u0011\na\u00015!91Q\u000b\u0001\u0005\u0002\t}\u0013\u0001B:u_BDqa!\u0017\u0001\t\u0013\u0019Y&\u0001\nd_6\u0004X\u000f^3U_R\fGnR2US6,G#A5\t\u000f\r}\u0003\u0001\"\u0003\u0004b\u0005\t2M]3bi\u0016\u001cE.Y:t\u0019>\fG-\u001a:\u0015\u0005\t%\u0006bBB3\u0001\u0011%1qM\u0001\u001bC\u0012$'+\u001a9m\u00072\f7o\u001d'pC\u0012,'/\u00134OK\u0016$W\r\u001a\u000b\u0005\u0005{\u001bI\u0007\u0003\u0005\u0004l\r\r\u0004\u0019\u0001B_\u0003\u0019\u0001\u0018M]3oi\"91q\u000e\u0001\u0005\n\rE\u0014AE;qI\u0006$X\rR3qK:$WM\\2jKN$b!a0\u0004t\ru\u0004\u0002CB;\u0007[\u0002\raa\u001e\u0002\u00119,wOR5mKN\u0004RAYB=5%L1aa\u001fd\u0005\ri\u0015\r\u001d\u0005\t\u0007\u007f\u001ai\u00071\u0001\u0004x\u00059a.Z<KCJ\u001c\bbBBB\u0001\u0011%!qL\u0001\u0010e\u0016\u0004xN\u001d;IK\u0006\u0014HOQ3bi\"91q\u0011\u0001\u0005\n\t}\u0013AF:uCJ$HI]5wKJDU-\u0019:uE\u0016\fG/\u001a:\b\u0011\r-%\u0001#\u0001\u0005\u0007\u001b\u000b\u0001\"\u0012=fGV$xN\u001d\t\u0004/\u000e=eaB\u0001\u0003\u0011\u0003!1\u0011S\n\u0004\u0007\u001f[\u0001b\u0002+\u0004\u0010\u0012\u00051Q\u0013\u000b\u0003\u0007\u001bC!b!'\u0004\u0010\n\u0007I\u0011ABN\u0003a!\u0018m]6EKN,'/[1mSj\fG/[8o!J|\u0007o]\u000b\u0003\u0007;\u0003b!!\u0015\u0004 \u000e\r\u0016bABQ\u001f\nYA\u000b\u001b:fC\u0012dunY1m!\u0011\u0019)ka*\u000e\u0005\u0005e\u0011\u0002BBU\u00033\u0011!\u0002\u0015:pa\u0016\u0014H/[3t\u0011%\u0019ika$!\u0002\u0013\u0019i*A\ruCN\\G)Z:fe&\fG.\u001b>bi&|g\u000e\u0015:paN\u0004\u0003BCBY\u0007\u001f\u000b\n\u0011\"\u0001\u00044\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIQ*\"a!.+\u0007-\u001a9l\u000b\u0002\u0004:B!11XBb\u001b\t\u0019iL\u0003\u0003\u0004@\u000e\u0005\u0017!C;oG\",7m[3e\u0015\r\u00119!D\u0005\u0005\u0007\u000b\u001ciLA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016D!b!3\u0004\u0010F\u0005I\u0011ABf\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%kU\u00111Q\u001a\u0016\u0004\u0003\u000e]\u0006BCBi\u0007\u001f\u000b\n\u0011\"\u0001\u0004T\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIY*\"a!6+\u0007\u0019\u001b9\f"
)
public class Executor implements Logging {
   public final String org$apache$spark$executor$Executor$$executorId;
   public final String org$apache$spark$executor$Executor$$executorHostname;
   public final SparkEnv org$apache$spark$executor$Executor$$env;
   private final Seq<URL> userClassPath;
   public final boolean org$apache$spark$executor$Executor$$isLocal;
   public final Thread.UncaughtExceptionHandler org$apache$spark$executor$Executor$$uncaughtExceptionHandler;
   private final HashMap<String, Object> org$apache$spark$executor$Executor$$currentFiles;
   private final HashMap<String, Object> org$apache$spark$executor$Executor$$currentJars;
   private final ByteBuffer org$apache$spark$executor$Executor$$EMPTY_BYTE_BUFFER;
   private final SparkConf org$apache$spark$executor$Executor$$conf;
   private final ThreadPoolExecutor threadPool;
   private final ExecutorSource org$apache$spark$executor$Executor$$executorSource;
   private final ThreadPoolExecutor org$apache$spark$executor$Executor$$taskReaperPool;
   private final HashMap<Object, TaskReaper> org$apache$spark$executor$Executor$$taskReaperForTask;
   private final boolean userClassPathFirst;
   private final boolean taskReaperEnabled;
   private final MutableURLClassLoader org$apache$spark$executor$Executor$$urlClassLoader;
   private final ClassLoader org$apache$spark$executor$Executor$$replClassLoader;
   private final long org$apache$spark$executor$Executor$$maxDirectResultSize;
   private final long org$apache$spark$executor$Executor$$maxResultSize;
   private final ConcurrentHashMap<Object, TaskRunner> org$apache$spark$executor$Executor$$runningTasks;
   private final ScheduledExecutorService heartbeater;
   private final RpcEndpointRef heartbeatReceiverRef;
   private final int org$apache$spark$executor$Executor$$HEARTBEAT_MAX_FAILURES;
   private int heartbeatFailures;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Thread.UncaughtExceptionHandler $lessinit$greater$default$6() {
      return Executor$.MODULE$.$lessinit$greater$default$6();
   }

   public static boolean $lessinit$greater$default$5() {
      return Executor$.MODULE$.$lessinit$greater$default$5();
   }

   public static Seq<URL> $lessinit$greater$default$4() {
      return Executor$.MODULE$.$lessinit$greater$default$4();
   }

   public static ThreadLocal<Properties> taskDeserializationProps() {
      return Executor$.MODULE$.taskDeserializationProps();
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

   public HashMap<String, Object> org$apache$spark$executor$Executor$$currentFiles() {
      return this.org$apache$spark$executor$Executor$$currentFiles;
   }

   public HashMap<String, Object> org$apache$spark$executor$Executor$$currentJars() {
      return this.org$apache$spark$executor$Executor$$currentJars;
   }

   public ByteBuffer org$apache$spark$executor$Executor$$EMPTY_BYTE_BUFFER() {
      return this.org$apache$spark$executor$Executor$$EMPTY_BYTE_BUFFER;
   }

   public SparkConf org$apache$spark$executor$Executor$$conf() {
      return this.org$apache$spark$executor$Executor$$conf;
   }

   private ThreadPoolExecutor threadPool() {
      return this.threadPool;
   }

   public ExecutorSource org$apache$spark$executor$Executor$$executorSource() {
      return this.org$apache$spark$executor$Executor$$executorSource;
   }

   public ThreadPoolExecutor org$apache$spark$executor$Executor$$taskReaperPool() {
      return this.org$apache$spark$executor$Executor$$taskReaperPool;
   }

   public HashMap<Object, TaskReaper> org$apache$spark$executor$Executor$$taskReaperForTask() {
      return this.org$apache$spark$executor$Executor$$taskReaperForTask;
   }

   private boolean userClassPathFirst() {
      return this.userClassPathFirst;
   }

   private boolean taskReaperEnabled() {
      return this.taskReaperEnabled;
   }

   public MutableURLClassLoader org$apache$spark$executor$Executor$$urlClassLoader() {
      return this.org$apache$spark$executor$Executor$$urlClassLoader;
   }

   public ClassLoader org$apache$spark$executor$Executor$$replClassLoader() {
      return this.org$apache$spark$executor$Executor$$replClassLoader;
   }

   public long org$apache$spark$executor$Executor$$maxDirectResultSize() {
      return this.org$apache$spark$executor$Executor$$maxDirectResultSize;
   }

   public long org$apache$spark$executor$Executor$$maxResultSize() {
      return this.org$apache$spark$executor$Executor$$maxResultSize;
   }

   public ConcurrentHashMap<Object, TaskRunner> org$apache$spark$executor$Executor$$runningTasks() {
      return this.org$apache$spark$executor$Executor$$runningTasks;
   }

   private ScheduledExecutorService heartbeater() {
      return this.heartbeater;
   }

   private RpcEndpointRef heartbeatReceiverRef() {
      return this.heartbeatReceiverRef;
   }

   public int org$apache$spark$executor$Executor$$HEARTBEAT_MAX_FAILURES() {
      return this.org$apache$spark$executor$Executor$$HEARTBEAT_MAX_FAILURES;
   }

   private int heartbeatFailures() {
      return this.heartbeatFailures;
   }

   private void heartbeatFailures_$eq(int x$1) {
      this.heartbeatFailures = x$1;
   }

   public int numRunningTasks() {
      return this.org$apache$spark$executor$Executor$$runningTasks().size();
   }

   public void launchTask(ExecutorBackend context, TaskDescription taskDescription) {
      TaskRunner tr = new TaskRunner(context, taskDescription);
      this.org$apache$spark$executor$Executor$$runningTasks().put(BoxesRunTime.boxToLong(taskDescription.taskId()), tr);
      this.threadPool().execute(tr);
   }

   public void killTask(long taskId, boolean interruptThread, String reason) {
      TaskRunner taskRunner = (TaskRunner)this.org$apache$spark$executor$Executor$$runningTasks().get(BoxesRunTime.boxToLong(taskId));
      if (taskRunner != null) {
         if (this.taskReaperEnabled()) {
            Object var8;
            synchronized(this.org$apache$spark$executor$Executor$$taskReaperForTask()) {
               Option var10 = this.org$apache$spark$executor$Executor$$taskReaperForTask().get(BoxesRunTime.boxToLong(taskId));
               boolean shouldCreateReaper;
               if (None$.MODULE$.equals(var10)) {
                  shouldCreateReaper = true;
               } else {
                  if (!(var10 instanceof Some)) {
                     throw new MatchError(var10);
                  }

                  Some var12 = (Some)var10;
                  TaskReaper existingReaper = (TaskReaper)var12.x();
                  shouldCreateReaper = interruptThread && !existingReaper.interruptThread();
               }

               Object var10000;
               if (shouldCreateReaper) {
                  TaskReaper taskReaper = new TaskReaper(taskRunner, interruptThread, reason);
                  this.org$apache$spark$executor$Executor$$taskReaperForTask().update(BoxesRunTime.boxToLong(taskId), taskReaper);
                  var10000 = new Some(taskReaper);
               } else {
                  var10000 = None$.MODULE$;
               }

               var8 = var10000;
            }

            Option maybeNewTaskReaper = (Option)var8;
            maybeNewTaskReaper.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final Executor $outer;

               public final void apply(Runnable x$1) {
                  this.$outer.org$apache$spark$executor$Executor$$taskReaperPool().execute(x$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         } else {
            taskRunner.kill(interruptThread, reason);
         }
      }

   }

   public void killAllTasks(boolean interruptThread, String reason) {
      ((Iterator)JavaConverters$.MODULE$.enumerationAsScalaIteratorConverter(this.org$apache$spark$executor$Executor$$runningTasks().keys()).asScala()).foreach(new Serializable(this, interruptThread, reason) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Executor $outer;
         private final boolean interruptThread$1;
         private final String reason$2;

         public final void apply(long t) {
            this.apply$mcVJ$sp(t);
         }

         public void apply$mcVJ$sp(long t) {
            this.$outer.killTask(t, this.interruptThread$1, this.reason$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.interruptThread$1 = interruptThread$1;
               this.reason$2 = reason$2;
            }
         }
      });
   }

   public void stop() {
      this.org$apache$spark$executor$Executor$$env.metricsSystem().report();
      this.heartbeater().shutdown();
      this.heartbeater().awaitTermination(10L, TimeUnit.SECONDS);
      this.threadPool().shutdown();
      if (!this.org$apache$spark$executor$Executor$$isLocal) {
         this.org$apache$spark$executor$Executor$$env.stop();
      }

   }

   public long org$apache$spark$executor$Executor$$computeTotalGcTime() {
      return BoxesRunTime.unboxToLong(((TraversableOnce)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter(ManagementFactory.getGarbageCollectorMXBeans()).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(GarbageCollectorMXBean x$1) {
            return x$1.getCollectionTime();
         }
      }, Buffer$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
   }

   private MutableURLClassLoader createClassLoader() {
      long now = System.currentTimeMillis();
      this.userClassPath.foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Executor $outer;
         private final long now$1;

         public final void apply(URL url) {
            this.$outer.org$apache$spark$executor$Executor$$currentJars().update(Predef$.MODULE$.refArrayOps((Object[])url.getPath().split("/")).last(), BoxesRunTime.boxToLong(this.now$1));
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
      ClassLoader currentLoader = Utils$.MODULE$.getContextOrSparkClassLoader();
      URL[] urls = (URL[])Predef$.MODULE$.refArrayOps(this.userClassPath.toArray(ClassTag$.MODULE$.apply(URL.class))).$plus$plus((GenTraversableOnce)this.org$apache$spark$executor$Executor$$currentJars().keySet().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final URL apply(String uri) {
            return (new File((String)Predef$.MODULE$.refArrayOps((Object[])uri.split("/")).last())).toURI().toURL();
         }
      }, Set$.MODULE$.canBuildFrom()), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(URL.class)));
      return (MutableURLClassLoader)(this.userClassPathFirst() ? new ChildFirstURLClassLoader(urls, currentLoader) : new MutableURLClassLoader(urls, currentLoader));
   }

   private ClassLoader addReplClassLoaderIfNeeded(ClassLoader parent) {
      String classUri = this.org$apache$spark$executor$Executor$$conf().get("spark.repl.class.uri", (String)null);
      ClassLoader var10000;
      if (classUri == null) {
         var10000 = parent;
      } else {
         this.logInfo(new Serializable(this, classUri) {
            public static final long serialVersionUID = 0L;
            private final String classUri$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Using REPL class URI: ").append((Object)this.classUri$1).toString();
            }

            public {
               this.classUri$1 = classUri$1;
            }
         });

         try {
            Boolean _userClassPathFirst = Predef$.MODULE$.boolean2Boolean(this.userClassPathFirst());
            Class klass = Utils$.MODULE$.classForName("org.apache.spark.repl.ExecutorClassLoader");
            Constructor constructor = klass.getConstructor(SparkConf.class, SparkEnv.class, String.class, ClassLoader.class, Boolean.TYPE);
            var10000 = (ClassLoader)constructor.newInstance(this.org$apache$spark$executor$Executor$$conf(), this.org$apache$spark$executor$Executor$$env, classUri, parent, _userClassPathFirst);
         } catch (ClassNotFoundException var6) {
            this.logError(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Could not find org.apache.spark.repl.ExecutorClassLoader on classpath!";
               }
            });
            System.exit(1);
            var10000 = null;
         }
      }

      return var10000;
   }

   public void org$apache$spark$executor$Executor$$updateDependencies(Map<String, Object> newFiles, Map<String, Object> newJars) {
      ObjectRef hadoopConf$lzy = ObjectRef.zero();
      VolatileByteRef bitmap$0 = VolatileByteRef.create((byte)0);
      synchronized(this) {
         newFiles.withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Tuple2<String, Object> check$ifrefutable$1) {
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
            // $FF: synthetic field
            private final Executor $outer;

            public final boolean apply(Tuple2<String, Object> x$2) {
               if (x$2 != null) {
                  String name = (String)x$2._1();
                  long timestamp = x$2._2$mcJ$sp();
                  boolean var6 = BoxesRunTime.unboxToLong(this.$outer.org$apache$spark$executor$Executor$$currentFiles().getOrElse(name, new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        return -1L;
                     }
                  })) < timestamp;
                  return var6;
               } else {
                  throw new MatchError(x$2);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }).foreach(new Serializable(this, hadoopConf$lzy, bitmap$0) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Executor $outer;
            private final ObjectRef hadoopConf$lzy$1;
            private final VolatileByteRef bitmap$0$1;

            public final void apply(Tuple2<String, Object> x$3) {
               if (x$3 != null) {
                  String name = (String)x$3._1();
                  long timestamp = x$3._2$mcJ$sp();
                  this.$outer.logInfo(new Serializable(this, name, timestamp) {
                     public static final long serialVersionUID = 0L;
                     private final String name$1;
                     private final long timestamp$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Fetching ").append((Object)this.name$1).append((Object)" with timestamp ").append((Object)BoxesRunTime.boxToLong(this.timestamp$1)).toString();
                     }

                     public {
                        this.name$1 = name$1;
                        this.timestamp$1 = timestamp$1;
                     }
                  });
                  Utils$.MODULE$.fetchFile(name, new File(SparkFiles$.MODULE$.getRootDirectory()), this.$outer.org$apache$spark$executor$Executor$$conf(), this.$outer.org$apache$spark$executor$Executor$$env.securityManager(), this.$outer.org$apache$spark$executor$Executor$$hadoopConf$1(this.hadoopConf$lzy$1, this.bitmap$0$1), timestamp, !this.$outer.org$apache$spark$executor$Executor$$isLocal);
                  this.$outer.org$apache$spark$executor$Executor$$currentFiles().update(name, BoxesRunTime.boxToLong(timestamp));
                  BoxedUnit var6 = BoxedUnit.UNIT;
               } else {
                  throw new MatchError(x$3);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.hadoopConf$lzy$1 = hadoopConf$lzy$1;
                  this.bitmap$0$1 = bitmap$0$1;
               }
            }
         });
         newJars.withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Tuple2<String, Object> check$ifrefutable$2) {
               boolean var3;
               if (check$ifrefutable$2 != null) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }
         }).foreach(new Serializable(this, hadoopConf$lzy, bitmap$0) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final Executor $outer;
            private final ObjectRef hadoopConf$lzy$1;
            private final VolatileByteRef bitmap$0$1;

            public final void apply(Tuple2<String, Object> x$4) {
               if (x$4 != null) {
                  String name = (String)x$4._1();
                  long timestamp = x$4._2$mcJ$sp();
                  String localName = (String)Predef$.MODULE$.refArrayOps((Object[])(new URI(name)).getPath().split("/")).last();
                  long currentTimeStamp = BoxesRunTime.unboxToLong(this.$outer.org$apache$spark$executor$Executor$$currentJars().get(name).orElse(new Serializable(this, localName) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final String localName$1;

                     public final Option<Object> apply() {
                        return this.$outer.org$apache$spark$executor$Executor$$anonfun$$$outer().org$apache$spark$executor$Executor$$currentJars().get(this.localName$1);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.localName$1 = localName$1;
                        }
                     }
                  }).getOrElse(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply() {
                        return this.apply$mcJ$sp();
                     }

                     public long apply$mcJ$sp() {
                        return -1L;
                     }
                  }));
                  if (currentTimeStamp < timestamp) {
                     this.$outer.logInfo(new Serializable(this, name, timestamp) {
                        public static final long serialVersionUID = 0L;
                        private final String name$2;
                        private final long timestamp$2;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Fetching ").append((Object)this.name$2).append((Object)" with timestamp ").append((Object)BoxesRunTime.boxToLong(this.timestamp$2)).toString();
                        }

                        public {
                           this.name$2 = name$2;
                           this.timestamp$2 = timestamp$2;
                        }
                     });
                     Utils$.MODULE$.fetchFile(name, new File(SparkFiles$.MODULE$.getRootDirectory()), this.$outer.org$apache$spark$executor$Executor$$conf(), this.$outer.org$apache$spark$executor$Executor$$env.securityManager(), this.$outer.org$apache$spark$executor$Executor$$hadoopConf$1(this.hadoopConf$lzy$1, this.bitmap$0$1), timestamp, !this.$outer.org$apache$spark$executor$Executor$$isLocal);
                     this.$outer.org$apache$spark$executor$Executor$$currentJars().update(name, BoxesRunTime.boxToLong(timestamp));
                     URL url = (new File(SparkFiles$.MODULE$.getRootDirectory(), localName)).toURI().toURL();
                     if (Predef$.MODULE$.refArrayOps((Object[])this.$outer.org$apache$spark$executor$Executor$$urlClassLoader().getURLs()).contains(url)) {
                        BoxedUnit var10000 = BoxedUnit.UNIT;
                     } else {
                        this.$outer.logInfo(new Serializable(this, url) {
                           public static final long serialVersionUID = 0L;
                           private final URL url$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"Adding ").append((Object)this.url$1).append((Object)" to class loader").toString();
                           }

                           public {
                              this.url$1 = url$1;
                           }
                        });
                        this.$outer.org$apache$spark$executor$Executor$$urlClassLoader().addURL(url);
                        BoxedUnit var11 = BoxedUnit.UNIT;
                     }
                  } else {
                     BoxedUnit var12 = BoxedUnit.UNIT;
                  }

               } else {
                  throw new MatchError(x$4);
               }
            }

            // $FF: synthetic method
            public Executor org$apache$spark$executor$Executor$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.hadoopConf$lzy$1 = hadoopConf$lzy$1;
                  this.bitmap$0$1 = bitmap$0$1;
               }
            }
         });
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public void org$apache$spark$executor$Executor$$reportHeartBeat() {
      ArrayBuffer accumUpdates = new ArrayBuffer();
      long curGCTime = this.org$apache$spark$executor$Executor$$computeTotalGcTime();
      ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.org$apache$spark$executor$Executor$$runningTasks().values()).asScala()).foreach(new Serializable(this, accumUpdates, curGCTime) {
         public static final long serialVersionUID = 0L;
         private final ArrayBuffer accumUpdates$1;
         private final long curGCTime$1;

         public final Object apply(TaskRunner taskRunner) {
            Object var10000;
            if (taskRunner.task() == null) {
               var10000 = BoxedUnit.UNIT;
            } else {
               taskRunner.task().metrics().mergeShuffleReadMetrics();
               taskRunner.task().metrics().setJvmGCTime(this.curGCTime$1 - taskRunner.startGCTime());
               var10000 = this.accumUpdates$1.$plus$eq(new Tuple2(BoxesRunTime.boxToLong(taskRunner.taskId()), taskRunner.task().metrics().accumulators()));
            }

            return var10000;
         }

         public {
            this.accumUpdates$1 = accumUpdates$1;
            this.curGCTime$1 = curGCTime$1;
         }
      });
      Heartbeat message = new Heartbeat(this.org$apache$spark$executor$Executor$$executorId, (Tuple2[])accumUpdates.toArray(ClassTag$.MODULE$.apply(Tuple2.class)), this.org$apache$spark$executor$Executor$$env.blockManager().blockManagerId());

      try {
         HeartbeatResponse response = (HeartbeatResponse)this.heartbeatReceiverRef().askSync(message, RpcTimeout$.MODULE$.apply(this.org$apache$spark$executor$Executor$$conf(), "spark.executor.heartbeatInterval", "10s"), ClassTag$.MODULE$.apply(HeartbeatResponse.class));
         if (response.reregisterBlockManager()) {
            this.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Told to re-register on heartbeat";
               }
            });
            this.org$apache$spark$executor$Executor$$env.blockManager().reregister();
         }

         this.heartbeatFailures_$eq(0);
      } catch (Throwable var12) {
         label56: {
            Option var7 = NonFatal$.MODULE$.unapply(var12);
            if (var7.isEmpty()) {
               throw var12;
            }

            Throwable e = (Throwable)var7.get();
            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Issue communicating with driver in heartbeater";
               }
            }, e);
            this.heartbeatFailures_$eq(this.heartbeatFailures() + 1);
            if (this.heartbeatFailures() >= this.org$apache$spark$executor$Executor$$HEARTBEAT_MAX_FAILURES()) {
               this.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final Executor $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exit as unable to send heartbeats to driver "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"more than ", " times"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$executor$Executor$$HEARTBEAT_MAX_FAILURES())}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               System.exit(ExecutorExitCode$.MODULE$.HEARTBEAT_FAILURE());
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               BoxedUnit var13 = BoxedUnit.UNIT;
            }
            break label56;
         }
      }

   }

   private void startDriverHeartbeater() {
      long intervalMs = this.org$apache$spark$executor$Executor$$conf().getTimeAsMs("spark.executor.heartbeatInterval", "10s");
      long initialDelay = intervalMs + (long)((int)(scala.math.package$.MODULE$.random() * (double)intervalMs));
      Runnable heartbeatTask = new Runnable(this) {
         // $FF: synthetic field
         private final Executor $outer;

         public void run() {
            Utils$.MODULE$.logUncaughtExceptions(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.org$apache$spark$executor$Executor$$anon$$$outer().org$apache$spark$executor$Executor$$reportHeartBeat();
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
         public Executor org$apache$spark$executor$Executor$$anon$$$outer() {
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
      this.heartbeater().scheduleAtFixedRate(heartbeatTask, initialDelay, intervalMs, TimeUnit.MILLISECONDS);
   }

   private final Configuration hadoopConf$lzycompute$1(ObjectRef hadoopConf$lzy$1, VolatileByteRef bitmap$0$1) {
      synchronized(this) {
         if ((byte)(bitmap$0$1.elem & 1) == 0) {
            hadoopConf$lzy$1.elem = (T)SparkHadoopUtil$.MODULE$.get().newConfiguration(this.org$apache$spark$executor$Executor$$conf());
            bitmap$0$1.elem = (byte)(bitmap$0$1.elem | 1);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return (Configuration)hadoopConf$lzy$1.elem;
   }

   public final Configuration org$apache$spark$executor$Executor$$hadoopConf$1(ObjectRef hadoopConf$lzy$1, VolatileByteRef bitmap$0$1) {
      return (byte)(bitmap$0$1.elem & 1) == 0 ? this.hadoopConf$lzycompute$1(hadoopConf$lzy$1, bitmap$0$1) : (Configuration)hadoopConf$lzy$1.elem;
   }

   public Executor(String executorId, String executorHostname, SparkEnv env, Seq<URL> userClassPath, boolean isLocal, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
      this.org$apache$spark$executor$Executor$$executorId = executorId;
      this.org$apache$spark$executor$Executor$$executorHostname = executorHostname;
      this.org$apache$spark$executor$Executor$$env = env;
      this.userClassPath = userClassPath;
      this.org$apache$spark$executor$Executor$$isLocal = isLocal;
      this.org$apache$spark$executor$Executor$$uncaughtExceptionHandler = uncaughtExceptionHandler;
      Logging$class.$init$(this);
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Executor $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Starting executor ID ", " on host ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$Executor$$executorId, this.$outer.org$apache$spark$executor$Executor$$executorHostname}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.org$apache$spark$executor$Executor$$currentFiles = new HashMap<String, Object>();
      this.org$apache$spark$executor$Executor$$currentJars = new HashMap<String, Object>();
      this.org$apache$spark$executor$Executor$$EMPTY_BYTE_BUFFER = ByteBuffer.wrap(new byte[0]);
      this.org$apache$spark$executor$Executor$$conf = env.conf();
      Utils$.MODULE$.checkHost(executorHostname);
      Predef$.MODULE$.assert(0 == Utils$.MODULE$.parseHostPort(executorHostname)._2$mcI$sp());
      Utils$.MODULE$.setCustomHostname(executorHostname);
      if (!isLocal) {
         Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
      }

      ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("Executor task launch worker-%d").setThreadFactory(new ThreadFactory(this) {
         public Thread newThread(Runnable r) {
            return new UninterruptibleThread(r, "unused");
         }
      }).build();
      this.threadPool = (ThreadPoolExecutor)Executors.newCachedThreadPool(threadFactory);
      this.org$apache$spark$executor$Executor$$executorSource = new ExecutorSource(this.threadPool(), executorId);
      this.org$apache$spark$executor$Executor$$taskReaperPool = ThreadUtils$.MODULE$.newDaemonCachedThreadPool("Task reaper");
      this.org$apache$spark$executor$Executor$$taskReaperForTask = (HashMap)HashMap$.MODULE$.apply(Nil$.MODULE$);
      if (!isLocal) {
         env.blockManager().initialize(this.org$apache$spark$executor$Executor$$conf().getAppId());
         env.metricsSystem().registerSource(this.org$apache$spark$executor$Executor$$executorSource());
         env.metricsSystem().registerSource(env.blockManager().shuffleMetricsSource());
      }

      this.userClassPathFirst = this.org$apache$spark$executor$Executor$$conf().getBoolean("spark.executor.userClassPathFirst", false);
      this.taskReaperEnabled = this.org$apache$spark$executor$Executor$$conf().getBoolean("spark.task.reaper.enabled", false);
      this.org$apache$spark$executor$Executor$$urlClassLoader = this.createClassLoader();
      this.org$apache$spark$executor$Executor$$replClassLoader = this.addReplClassLoaderIfNeeded(this.org$apache$spark$executor$Executor$$urlClassLoader());
      env.serializer().setDefaultClassLoader(this.org$apache$spark$executor$Executor$$replClassLoader());
      env.serializerManager().setDefaultClassLoader(this.org$apache$spark$executor$Executor$$replClassLoader());
      this.org$apache$spark$executor$Executor$$maxDirectResultSize = Math.min(this.org$apache$spark$executor$Executor$$conf().getSizeAsBytes("spark.task.maxDirectResultSize", 1048576L), (long)RpcUtils$.MODULE$.maxMessageSizeBytes(this.org$apache$spark$executor$Executor$$conf()));
      this.org$apache$spark$executor$Executor$$maxResultSize = Utils$.MODULE$.getMaxResultSize(this.org$apache$spark$executor$Executor$$conf());
      this.org$apache$spark$executor$Executor$$runningTasks = new ConcurrentHashMap();
      this.heartbeater = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("driver-heartbeater");
      this.heartbeatReceiverRef = RpcUtils$.MODULE$.makeDriverRef(HeartbeatReceiver$.MODULE$.ENDPOINT_NAME(), this.org$apache$spark$executor$Executor$$conf(), env.rpcEnv());
      this.org$apache$spark$executor$Executor$$HEARTBEAT_MAX_FAILURES = this.org$apache$spark$executor$Executor$$conf().getInt("spark.executor.heartbeat.maxFailures", 60);
      this.heartbeatFailures = 0;
      this.startDriverHeartbeater();
   }

   public class TaskRunner implements Runnable {
      private final ExecutorBackend execBackend;
      private final TaskDescription taskDescription;
      private final long taskId;
      private final String threadName;
      private final String org$apache$spark$executor$Executor$TaskRunner$$taskName;
      private volatile Option<String> reasonIfKilled;
      private volatile long threadId;
      @GuardedBy("TaskRunner.this")
      private boolean finished;
      private volatile long startGCTime;
      private volatile Task<Object> task;
      // $FF: synthetic field
      public final Executor $outer;

      private TaskDescription taskDescription() {
         return this.taskDescription;
      }

      public long taskId() {
         return this.taskId;
      }

      public String threadName() {
         return this.threadName;
      }

      public String org$apache$spark$executor$Executor$TaskRunner$$taskName() {
         return this.org$apache$spark$executor$Executor$TaskRunner$$taskName;
      }

      private Option<String> reasonIfKilled() {
         return this.reasonIfKilled;
      }

      private void reasonIfKilled_$eq(Option<String> x$1) {
         this.reasonIfKilled = x$1;
      }

      private long threadId() {
         return this.threadId;
      }

      private void threadId_$eq(long x$1) {
         this.threadId = x$1;
      }

      public long getThreadId() {
         return this.threadId();
      }

      private boolean finished() {
         return this.finished;
      }

      private void finished_$eq(boolean x$1) {
         this.finished = x$1;
      }

      public synchronized boolean isFinished() {
         return this.finished();
      }

      public long startGCTime() {
         return this.startGCTime;
      }

      public void startGCTime_$eq(long x$1) {
         this.startGCTime = x$1;
      }

      public Task<Object> task() {
         return this.task;
      }

      public void task_$eq(Task<Object> x$1) {
         this.task = x$1;
      }

      public void kill(boolean interruptThread, String reason) {
         this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logInfo(new Serializable(this, reason) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskRunner $outer;
            private final String reason$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor is trying to kill ", " (TID ", "), reason: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$Executor$TaskRunner$$taskName(), BoxesRunTime.boxToLong(this.$outer.taskId()), this.reason$1}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.reason$1 = reason$1;
               }
            }
         });
         this.reasonIfKilled_$eq(new Some(reason));
         if (this.task() != null) {
            synchronized(this) {
               if (this.finished()) {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               } else {
                  this.task().kill(interruptThread, reason);
                  BoxedUnit var5 = BoxedUnit.UNIT;
               }
            }
         }

      }

      private synchronized void setTaskFinishedAndClearInterruptStatus() {
         this.finished_$eq(true);
         Thread.interrupted();
         this.notifyAll();
      }

      public void run() {
         this.threadId_$eq(Thread.currentThread().getId());
         Thread.currentThread().setName(this.threadName());
         ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
         TaskMemoryManager taskMemoryManager = new TaskMemoryManager(this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$env.memoryManager(), this.taskId());
         long deserializeStartTime = System.currentTimeMillis();
         long deserializeStartCpuTime = threadMXBean.isCurrentThreadCpuTimeSupported() ? threadMXBean.getCurrentThreadCpuTime() : 0L;
         Thread.currentThread().setContextClassLoader(this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$replClassLoader());
         SerializerInstance ser = this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$env.closureSerializer().newInstance();
         this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskRunner $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Running ", " (TID ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$Executor$TaskRunner$$taskName(), BoxesRunTime.boxToLong(this.$outer.taskId())}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.execBackend.statusUpdate(this.taskId(), TaskState$.MODULE$.RUNNING(), this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$EMPTY_BYTE_BUFFER());
         long taskStart = 0L;
         long taskStartCpu = 0L;
         this.startGCTime_$eq(this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$computeTotalGcTime());

         try {
            label658:
            try {
               Executor$.MODULE$.taskDeserializationProps().set(this.taskDescription().properties());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$updateDependencies(this.taskDescription().addedFiles(), this.taskDescription().addedJars());
               this.task_$eq((Task)ser.deserialize(this.taskDescription().serializedTask(), Thread.currentThread().getContextClassLoader(), ClassTag$.MODULE$.apply(Task.class)));
               this.task().localProperties_$eq(this.taskDescription().properties());
               this.task().setTaskMemoryManager(taskMemoryManager);
               Option killReason = this.reasonIfKilled();
               if (killReason.isDefined()) {
                  throw new TaskKilledException((String)killReason.get());
               }

               if (!this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$isLocal) {
                  this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logDebug(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final TaskRunner $outer;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Task ").append((Object)BoxesRunTime.boxToLong(this.$outer.taskId())).append((Object)"'s epoch is ").append((Object)BoxesRunTime.boxToLong(this.$outer.task().epoch())).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  ((MapOutputTrackerWorker)this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$env.mapOutputTracker()).updateEpoch(this.task().epoch());
               }

               taskStart = System.currentTimeMillis();
               taskStartCpu = threadMXBean.isCurrentThreadCpuTimeSupported() ? threadMXBean.getCurrentThreadCpuTime() : 0L;
               boolean threwException = true;

               Object var83;
               try {
                  Object res = this.task().run(this.taskId(), this.taskDescription().attemptNumber(), this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$env.metricsSystem());
                  threwException = false;
                  var83 = res;
               } finally {
                  Seq releasedLocks = this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$env.blockManager().releaseAllLocksForTask(this.taskId());
                  long freedMemory = taskMemoryManager.cleanUpAllAllocatedMemory();
                  if (freedMemory > 0L && !threwException) {
                     String errMsg = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Managed memory leak detected; size = ", " bytes, TID = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(freedMemory), BoxesRunTime.boxToLong(this.taskId())}));
                     if (this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$conf().getBoolean("spark.unsafe.exceptionOnMemoryLeak", false)) {
                        throw new SparkException(errMsg);
                     }

                     this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logWarning(new Serializable(this, errMsg) {
                        public static final long serialVersionUID = 0L;
                        private final String errMsg$1;

                        public final String apply() {
                           return this.errMsg$1;
                        }

                        public {
                           this.errMsg$1 = errMsg$1;
                        }
                     });
                  }

                  if (releasedLocks.nonEmpty() && !threwException) {
                     String errMsgx = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " block locks were not released by TID = ", ":\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(releasedLocks.size()), BoxesRunTime.boxToLong(this.taskId())}))).append((Object)releasedLocks.mkString("[", ", ", "]")).toString();
                     if (this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$conf().getBoolean("spark.storage.exceptionOnPinLeak", false)) {
                        throw new SparkException(errMsgx);
                     }

                     this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logInfo(new Serializable(this, errMsgx) {
                        public static final long serialVersionUID = 0L;
                        private final String errMsg$2;

                        public final String apply() {
                           return this.errMsg$2;
                        }

                        public {
                           this.errMsg$2 = errMsg$2;
                        }
                     });
                  }

               }

               Object value = var83;
               this.task().context().fetchFailed().foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskRunner $outer;

                  public final void apply(FetchFailedException fetchFailure) {
                     this.$outer.org$apache$spark$executor$Executor$TaskRunner$$$outer().logError(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"TID ", " completed successfully though internally it encountered "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$executor$Executor$TaskRunner$$anonfun$$$outer().taskId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"unrecoverable fetch failures!  Most likely this means user code is incorrectly "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"swallowing Spark's internal ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{FetchFailedException.class}))).toString();
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     }, fetchFailure);
                  }

                  // $FF: synthetic method
                  public TaskRunner org$apache$spark$executor$Executor$TaskRunner$$anonfun$$$outer() {
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
               long taskFinish = System.currentTimeMillis();
               long taskFinishCpu = threadMXBean.isCurrentThreadCpuTimeSupported() ? threadMXBean.getCurrentThreadCpuTime() : 0L;
               this.task().context().killTaskIfInterrupted();
               SerializerInstance resultSer = this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$env.serializer().newInstance();
               long beforeSerialization = System.currentTimeMillis();
               ByteBuffer valueBytes = resultSer.serialize(value, ClassTag$.MODULE$.Any());
               long afterSerialization = System.currentTimeMillis();
               this.task().metrics().setExecutorDeserializeTime(taskStart - deserializeStartTime + this.task().executorDeserializeTime());
               this.task().metrics().setExecutorDeserializeCpuTime(taskStartCpu - deserializeStartCpuTime + this.task().executorDeserializeCpuTime());
               this.task().metrics().setExecutorRunTime(taskFinish - taskStart - this.task().executorDeserializeTime());
               this.task().metrics().setExecutorCpuTime(taskFinishCpu - taskStartCpu - this.task().executorDeserializeCpuTime());
               this.task().metrics().setJvmGCTime(this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$computeTotalGcTime() - this.startGCTime());
               this.task().metrics().setResultSerializationTime(afterSerialization - beforeSerialization);
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_CPU_TIME().inc(this.task().metrics().executorCpuTime());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_RUN_TIME().inc(this.task().metrics().executorRunTime());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_JVM_GC_TIME().inc(this.task().metrics().jvmGCTime());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_DESERIALIZE_TIME().inc(this.task().metrics().executorDeserializeTime());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_DESERIALIZE_CPU_TIME().inc(this.task().metrics().executorDeserializeCpuTime());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_RESULT_SERIALIZE_TIME().inc(this.task().metrics().resultSerializationTime());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_FETCH_WAIT_TIME().inc(this.task().metrics().shuffleReadMetrics().fetchWaitTime());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_WRITE_TIME().inc(this.task().metrics().shuffleWriteMetrics().writeTime());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_TOTAL_BYTES_READ().inc(this.task().metrics().shuffleReadMetrics().totalBytesRead());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_REMOTE_BYTES_READ().inc(this.task().metrics().shuffleReadMetrics().remoteBytesRead());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_REMOTE_BYTES_READ_TO_DISK().inc(this.task().metrics().shuffleReadMetrics().remoteBytesReadToDisk());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_LOCAL_BYTES_READ().inc(this.task().metrics().shuffleReadMetrics().localBytesRead());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_RECORDS_READ().inc(this.task().metrics().shuffleReadMetrics().recordsRead());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_REMOTE_BLOCKS_FETCHED().inc(this.task().metrics().shuffleReadMetrics().remoteBlocksFetched());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_LOCAL_BLOCKS_FETCHED().inc(this.task().metrics().shuffleReadMetrics().localBlocksFetched());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_BYTES_WRITTEN().inc(this.task().metrics().shuffleWriteMetrics().bytesWritten());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_SHUFFLE_RECORDS_WRITTEN().inc(this.task().metrics().shuffleWriteMetrics().recordsWritten());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_INPUT_BYTES_READ().inc(this.task().metrics().inputMetrics().bytesRead());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_INPUT_RECORDS_READ().inc(this.task().metrics().inputMetrics().recordsRead());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_OUTPUT_BYTES_WRITTEN().inc(this.task().metrics().outputMetrics().bytesWritten());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_OUTPUT_RECORDS_WRITTEN().inc(this.task().metrics().inputMetrics().recordsRead());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_RESULT_SIZE().inc(this.task().metrics().resultSize());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_DISK_BYTES_SPILLED().inc(this.task().metrics().diskBytesSpilled());
               this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$executorSource().METRIC_MEMORY_BYTES_SPILLED().inc(this.task().metrics().memoryBytesSpilled());
               Task qual$1 = this.task();
               boolean x$5 = qual$1.collectAccumulatorUpdates$default$1();
               Seq accumUpdates = qual$1.collectAccumulatorUpdates(x$5);
               DirectTaskResult directResult = new DirectTaskResult(valueBytes, accumUpdates);
               ByteBuffer serializedDirectResult = ser.serialize(directResult, ClassTag$.MODULE$.apply(DirectTaskResult.class));
               int resultSize = serializedDirectResult.limit();
               ByteBuffer var84;
               if (this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$maxResultSize() > 0L && (long)resultSize > this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$maxResultSize()) {
                  this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logWarning(new Serializable(this, resultSize) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final TaskRunner $outer;
                     private final int resultSize$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Finished ", " (TID ", "). Result is larger than maxResultSize "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$Executor$TaskRunner$$taskName(), BoxesRunTime.boxToLong(this.$outer.taskId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " > ", "), "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString((long)this.resultSize$1), Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$maxResultSize())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"dropping it."})))).s(Nil$.MODULE$)).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.resultSize$1 = resultSize$1;
                        }
                     }
                  });
                  var84 = ser.serialize(new IndirectTaskResult(new TaskResultBlockId(this.taskId()), resultSize), ClassTag$.MODULE$.apply(IndirectTaskResult.class));
               } else if ((long)resultSize > this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$maxDirectResultSize()) {
                  TaskResultBlockId blockId = new TaskResultBlockId(this.taskId());
                  this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$env.blockManager().putBytes(blockId, new ChunkedByteBuffer(serializedDirectResult.duplicate()), StorageLevel$.MODULE$.MEMORY_AND_DISK_SER(), this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$env.blockManager().putBytes$default$4(), ClassTag$.MODULE$.Nothing());
                  this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logInfo(new Serializable(this, resultSize) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final TaskRunner $outer;
                     private final int resultSize$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Finished ", " (TID ", "). ", " bytes result sent via BlockManager)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$Executor$TaskRunner$$taskName(), BoxesRunTime.boxToLong(this.$outer.taskId()), BoxesRunTime.boxToInteger(this.resultSize$1)}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.resultSize$1 = resultSize$1;
                        }
                     }
                  });
                  var84 = ser.serialize(new IndirectTaskResult(blockId, resultSize), ClassTag$.MODULE$.apply(IndirectTaskResult.class));
               } else {
                  this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logInfo(new Serializable(this, resultSize) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final TaskRunner $outer;
                     private final int resultSize$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Finished ", " (TID ", "). ", " bytes result sent to driver"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$Executor$TaskRunner$$taskName(), BoxesRunTime.boxToLong(this.$outer.taskId()), BoxesRunTime.boxToInteger(this.resultSize$1)}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.resultSize$1 = resultSize$1;
                        }
                     }
                  });
                  var84 = serializedDirectResult;
               }

               ByteBuffer serializedResult = var84;
               this.setTaskFinishedAndClearInterruptStatus();
               this.execBackend.statusUpdate(this.taskId(), TaskState$.MODULE$.FINISHED(), serializedResult);
            } catch (Throwable var73) {
               if (var73 != null && this.hasFetchFailure() && !Utils$.MODULE$.isFatalError(var73)) {
                  TaskFailedReason reason = ((FetchFailedException)this.task().context().fetchFailed().get()).toTaskFailedReason();
                  if (!(var73 instanceof FetchFailedException)) {
                     String fetchFailedCls = FetchFailedException.class.getName();
                     this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logWarning(new Serializable(this, fetchFailedCls, var73) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final TaskRunner $outer;
                        private final String fetchFailedCls$1;
                        private final Throwable x16$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"TID ", " encountered a ", " and "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.taskId()), this.fetchFailedCls$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"failed, but the ", " was hidden by another "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.fetchFailedCls$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"exception.  Spark is handling this like a fetch failure and ignoring the "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"other exception: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x16$1}))).toString();
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.fetchFailedCls$1 = fetchFailedCls$1;
                              this.x16$1 = x16$1;
                           }
                        }
                     });
                  }

                  this.setTaskFinishedAndClearInterruptStatus();
                  this.execBackend.statusUpdate(this.taskId(), TaskState$.MODULE$.FAILED(), ser.serialize(reason, ClassTag$.MODULE$.apply(TaskFailedReason.class)));
                  BoxedUnit var79 = BoxedUnit.UNIT;
               } else if (var73 instanceof TaskKilledException) {
                  TaskKilledException var19 = (TaskKilledException)var73;
                  this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logInfo(new Serializable(this, var19) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final TaskRunner $outer;
                     private final TaskKilledException x17$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor killed ", " (TID ", "), reason: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$Executor$TaskRunner$$taskName(), BoxesRunTime.boxToLong(this.$outer.taskId()), this.x17$1.reason()}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.x17$1 = x17$1;
                        }
                     }
                  });
                  this.setTaskFinishedAndClearInterruptStatus();
                  this.execBackend.statusUpdate(this.taskId(), TaskState$.MODULE$.KILLED(), ser.serialize(new TaskKilled(var19.reason()), ClassTag$.MODULE$.apply(TaskKilled.class)));
                  BoxedUnit var16 = BoxedUnit.UNIT;
               } else {
                  boolean var20;
                  if (var73 instanceof InterruptedException) {
                     var20 = true;
                  } else {
                     Option var21 = NonFatal$.MODULE$.unapply(var73);
                     if (var21.isEmpty()) {
                        var20 = false;
                     } else {
                        var20 = true;
                     }
                  }

                  if (var20 && this.task() != null && this.task().reasonIfKilled().isDefined()) {
                     String killReason = (String)this.task().reasonIfKilled().getOrElse(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "unknown reason";
                        }
                     });
                     this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logInfo(new Serializable(this, killReason) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final TaskRunner $outer;
                        private final String killReason$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor interrupted and killed ", " (TID ", "), reason: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$Executor$TaskRunner$$taskName(), BoxesRunTime.boxToLong(this.$outer.taskId()), this.killReason$1}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.killReason$1 = killReason$1;
                           }
                        }
                     });
                     this.setTaskFinishedAndClearInterruptStatus();
                     this.execBackend.statusUpdate(this.taskId(), TaskState$.MODULE$.KILLED(), ser.serialize(new TaskKilled(killReason), ClassTag$.MODULE$.apply(TaskKilled.class)));
                     BoxedUnit var78 = BoxedUnit.UNIT;
                  } else {
                     Option var23 = CausedBy$.MODULE$.unapply(var73);
                     if (!var23.isEmpty()) {
                        Throwable cDE = (Throwable)var23.get();
                        if (cDE instanceof CommitDeniedException) {
                           CommitDeniedException var25 = (CommitDeniedException)cDE;
                           TaskCommitDenied reason = var25.toTaskCommitDeniedReason();
                           this.setTaskFinishedAndClearInterruptStatus();
                           this.execBackend.statusUpdate(this.taskId(), TaskState$.MODULE$.KILLED(), ser.serialize(reason, ClassTag$.MODULE$.apply(TaskCommitDenied.class)));
                           BoxedUnit var77 = BoxedUnit.UNIT;
                           return;
                        }
                     }

                     if (var73 == null) {
                        throw var73;
                     }

                     Throwable var27 = var73;
                     this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logError(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final TaskRunner $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception in ", " (TID ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$executor$Executor$TaskRunner$$taskName(), BoxesRunTime.boxToLong(this.$outer.taskId())}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     }, var73);
                     if (ShutdownHookManager$.MODULE$.inShutdown()) {
                        this.org$apache$spark$executor$Executor$TaskRunner$$$outer().logInfo(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final String apply() {
                              return "Not reporting error to driver during JVM shutdown.";
                           }
                        });
                     } else {
                        Seq var10000;
                        if (this.task() == null) {
                           var10000 = (Seq)Seq$.MODULE$.empty();
                        } else {
                           this.task().metrics().setExecutorRunTime(System.currentTimeMillis() - taskStart);
                           this.task().metrics().setJvmGCTime(this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$computeTotalGcTime() - this.startGCTime());
                           var10000 = this.task().collectAccumulatorUpdates(true);
                        }

                        Seq accums = var10000;
                        Seq accUpdates = (Seq)accums.map(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final AccumulableInfo apply(AccumulatorV2<?, ?> acc) {
                              return acc.toInfo(new Some(acc.value()), None$.MODULE$);
                           }
                        }, Seq$.MODULE$.canBuildFrom());

                        try {
                           var80 = ser.serialize((new ExceptionFailure(var27, accUpdates)).withAccums(accums), ClassTag$.MODULE$.apply(ExceptionFailure.class));
                        } catch (NotSerializableException var71) {
                           var80 = ser.serialize((new ExceptionFailure(var73, accUpdates, false)).withAccums(accums), ClassTag$.MODULE$.apply(ExceptionFailure.class));
                        }

                        ByteBuffer serializedTaskEndReason = var80;
                        this.setTaskFinishedAndClearInterruptStatus();
                        this.execBackend.statusUpdate(this.taskId(), TaskState$.MODULE$.FAILED(), serializedTaskEndReason);
                     }

                     if (!(var73 instanceof SparkOutOfMemoryError) && Utils$.MODULE$.isFatalError(var73)) {
                        this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), var73);
                        BoxedUnit var82 = BoxedUnit.UNIT;
                     } else {
                        BoxedUnit var81 = BoxedUnit.UNIT;
                     }
                  }
               }
               break label658;
            }
         } finally {
            this.org$apache$spark$executor$Executor$TaskRunner$$$outer().org$apache$spark$executor$Executor$$runningTasks().remove(BoxesRunTime.boxToLong(this.taskId()));
         }

      }

      private boolean hasFetchFailure() {
         return this.task() != null && this.task().context() != null && this.task().context().fetchFailed().isDefined();
      }

      // $FF: synthetic method
      public Executor org$apache$spark$executor$Executor$TaskRunner$$$outer() {
         return this.$outer;
      }

      public TaskRunner(ExecutorBackend execBackend, TaskDescription taskDescription) {
         this.execBackend = execBackend;
         this.taskDescription = taskDescription;
         if (Executor.this == null) {
            throw null;
         } else {
            this.$outer = Executor.this;
            super();
            this.taskId = taskDescription.taskId();
            this.threadName = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Executor task launch worker for task ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskId())}));
            this.org$apache$spark$executor$Executor$TaskRunner$$taskName = taskDescription.name();
            this.reasonIfKilled = None$.MODULE$;
            this.threadId = -1L;
            this.finished = false;
         }
      }
   }

   public class TaskReaper implements Runnable {
      public final TaskRunner org$apache$spark$executor$Executor$TaskReaper$$taskRunner;
      private final boolean interruptThread;
      private final String reason;
      public final long org$apache$spark$executor$Executor$TaskReaper$$taskId;
      private final long killPollingIntervalMs;
      public final long org$apache$spark$executor$Executor$TaskReaper$$killTimeoutMs;
      private final boolean takeThreadDump;
      // $FF: synthetic field
      public final Executor $outer;

      public boolean interruptThread() {
         return this.interruptThread;
      }

      public String reason() {
         return this.reason;
      }

      public void run() {
         long startTimeMs = System.currentTimeMillis();

         try {
            this.org$apache$spark$executor$Executor$TaskReaper$$taskRunner.kill(this.interruptThread(), this.reason());
            boolean finished = false;

            while(!finished && !this.timeoutExceeded$1(startTimeMs)) {
               synchronized(this.org$apache$spark$executor$Executor$TaskReaper$$taskRunner) {
                  if (this.org$apache$spark$executor$Executor$TaskReaper$$taskRunner.isFinished()) {
                     finished = true;
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     this.org$apache$spark$executor$Executor$TaskReaper$$taskRunner.wait(this.killPollingIntervalMs);
                     BoxedUnit var26 = BoxedUnit.UNIT;
                  }
               }

               if (this.org$apache$spark$executor$Executor$TaskReaper$$taskRunner.isFinished()) {
                  finished = true;
               } else {
                  this.org$apache$spark$executor$Executor$TaskReaper$$$outer().logWarning(new Serializable(this, startTimeMs) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final TaskReaper $outer;
                     private final long startTimeMs$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Killed task ", " is still running after ", " ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$executor$Executor$TaskReaper$$taskId), BoxesRunTime.boxToLong(this.$outer.org$apache$spark$executor$Executor$TaskReaper$$elapsedTimeMs$1(this.startTimeMs$1))}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.startTimeMs$1 = startTimeMs$1;
                        }
                     }
                  });
                  if (this.takeThreadDump) {
                     try {
                        Utils$.MODULE$.getThreadDumpForThread(this.org$apache$spark$executor$Executor$TaskReaper$$taskRunner.getThreadId()).foreach(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final TaskReaper $outer;

                           public final void apply(ThreadStackTrace thread) {
                              String var10000 = thread.threadName();
                              String var2 = this.$outer.org$apache$spark$executor$Executor$TaskReaper$$taskRunner.threadName();
                              if (var10000 == null) {
                                 if (var2 != null) {
                                    return;
                                 }
                              } else if (!var10000.equals(var2)) {
                                 return;
                              }

                              this.$outer.org$apache$spark$executor$Executor$TaskReaper$$$outer().logWarning(new Serializable(this, thread) {
                                 public static final long serialVersionUID = 0L;
                                 // $FF: synthetic field
                                 private final <undefinedtype> $outer;
                                 private final ThreadStackTrace thread$1;

                                 public final String apply() {
                                    return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Thread dump from task ", ":\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$executor$Executor$TaskReaper$$anonfun$$$outer().org$apache$spark$executor$Executor$TaskReaper$$taskId), this.thread$1.stackTrace()}));
                                 }

                                 public {
                                    if ($outer == null) {
                                       throw null;
                                    } else {
                                       this.$outer = $outer;
                                       this.thread$1 = thread$1;
                                    }
                                 }
                              });
                           }

                           // $FF: synthetic method
                           public TaskReaper org$apache$spark$executor$Executor$TaskReaper$$anonfun$$$outer() {
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
                     } catch (Throwable var24) {
                        Option var9 = NonFatal$.MODULE$.unapply(var24);
                        if (var9.isEmpty()) {
                           throw var24;
                        }

                        Throwable e = (Throwable)var9.get();
                        this.org$apache$spark$executor$Executor$TaskReaper$$$outer().logWarning(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final String apply() {
                              return "Exception thrown while obtaining thread dump: ";
                           }
                        }, e);
                        BoxedUnit var11 = BoxedUnit.UNIT;
                        continue;
                     }
                  }
               }
            }

            if (!this.org$apache$spark$executor$Executor$TaskReaper$$taskRunner.isFinished() && this.timeoutExceeded$1(startTimeMs)) {
               if (!this.org$apache$spark$executor$Executor$TaskReaper$$$outer().org$apache$spark$executor$Executor$$isLocal) {
                  throw new SparkException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Killing executor JVM because killed task ", " could not be stopped within "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.org$apache$spark$executor$Executor$TaskReaper$$taskId)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ms."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.org$apache$spark$executor$Executor$TaskReaper$$killTimeoutMs)}))).toString());
               }

               this.org$apache$spark$executor$Executor$TaskReaper$$$outer().logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskReaper $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Killed task ", " could not be stopped within ", " ms; "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$executor$Executor$TaskReaper$$taskId), BoxesRunTime.boxToLong(this.$outer.org$apache$spark$executor$Executor$TaskReaper$$killTimeoutMs)}))).append((Object)"not killing JVM because we are running in local mode.").toString();
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
         } finally {
            synchronized(this.org$apache$spark$executor$Executor$TaskReaper$$$outer().org$apache$spark$executor$Executor$$taskReaperForTask()) {
               this.org$apache$spark$executor$Executor$TaskReaper$$$outer().org$apache$spark$executor$Executor$$taskReaperForTask().get(BoxesRunTime.boxToLong(this.org$apache$spark$executor$Executor$TaskReaper$$taskId)).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskReaper $outer;

                  public final Object apply(TaskReaper taskReaperInMap) {
                     return taskReaperInMap == this.$outer ? this.$outer.org$apache$spark$executor$Executor$TaskReaper$$$outer().org$apache$spark$executor$Executor$$taskReaperForTask().remove(BoxesRunTime.boxToLong(this.$outer.org$apache$spark$executor$Executor$TaskReaper$$taskId)) : BoxedUnit.UNIT;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               BoxedUnit var27 = BoxedUnit.UNIT;
            }
         }

      }

      // $FF: synthetic method
      public Executor org$apache$spark$executor$Executor$TaskReaper$$$outer() {
         return this.$outer;
      }

      public final long org$apache$spark$executor$Executor$TaskReaper$$elapsedTimeMs$1(long startTimeMs$1) {
         return System.currentTimeMillis() - startTimeMs$1;
      }

      private final boolean timeoutExceeded$1(long startTimeMs$1) {
         return this.org$apache$spark$executor$Executor$TaskReaper$$killTimeoutMs > 0L && this.org$apache$spark$executor$Executor$TaskReaper$$elapsedTimeMs$1(startTimeMs$1) > this.org$apache$spark$executor$Executor$TaskReaper$$killTimeoutMs;
      }

      public TaskReaper(TaskRunner taskRunner, boolean interruptThread, String reason) {
         this.org$apache$spark$executor$Executor$TaskReaper$$taskRunner = taskRunner;
         this.interruptThread = interruptThread;
         this.reason = reason;
         if (Executor.this == null) {
            throw null;
         } else {
            this.$outer = Executor.this;
            super();
            this.org$apache$spark$executor$Executor$TaskReaper$$taskId = taskRunner.taskId();
            this.killPollingIntervalMs = Executor.this.org$apache$spark$executor$Executor$$conf().getTimeAsMs("spark.task.reaper.pollingInterval", "10s");
            this.org$apache$spark$executor$Executor$TaskReaper$$killTimeoutMs = Executor.this.org$apache$spark$executor$Executor$$conf().getTimeAsMs("spark.task.reaper.killTimeout", "-1");
            this.takeThreadDump = Executor.this.org$apache$spark$executor$Executor$$conf().getBoolean("spark.task.reaper.threadDump", true);
         }
      }
   }
}
