package org.apache.spark.storage;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.MapOutputTracker;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext$;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.executor.DataReadMethod$;
import org.apache.spark.executor.ShuffleWriteMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.package$;
import org.apache.spark.memory.MemoryManager;
import org.apache.spark.memory.MemoryMode;
import org.apache.spark.metrics.source.Source;
import org.apache.spark.network.BlockDataManager;
import org.apache.spark.network.BlockTransferService;
import org.apache.spark.network.buffer.ManagedBuffer;
import org.apache.spark.network.netty.SparkTransportConf$;
import org.apache.spark.network.shuffle.ExternalShuffleClient;
import org.apache.spark.network.shuffle.ShuffleClient;
import org.apache.spark.network.shuffle.TempFileManager;
import org.apache.spark.network.shuffle.protocol.ExecutorShuffleInfo;
import org.apache.spark.network.util.TransportConf;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.shuffle.ShuffleBlockResolver;
import org.apache.spark.shuffle.ShuffleManager;
import org.apache.spark.storage.memory.BlockEvictionHandler;
import org.apache.spark.storage.memory.MemoryStore;
import org.apache.spark.storage.memory.PartiallySerializedBlock;
import org.apache.spark.storage.memory.PartiallyUnrolledIterator;
import org.apache.spark.unsafe.Platform;
import org.apache.spark.util.CompletionIterator;
import org.apache.spark.util.CompletionIterator$;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.io.ChunkedByteBuffer;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.HashSet$;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.ExecutionContext$;
import scala.concurrent.ExecutionContextExecutorService;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.concurrent.duration.Duration$;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.NonLocalReturnControl$mcV$sp;
import scala.runtime.Nothing$;
import scala.runtime.RichInt$;
import scala.runtime.RichLong$;
import scala.runtime.TraitSetter;
import scala.util.Either;
import scala.util.Left;
import scala.util.Random$;
import scala.util.Right;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u001dEh!B\u0001\u0003\u0001\u0011Q!\u0001\u0004\"m_\u000e\\W*\u00198bO\u0016\u0014(BA\u0002\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0006\u0001-\tr#\b\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I)R\"A\n\u000b\u0005Q!\u0011a\u00028fi^|'o[\u0005\u0003-M\u0011\u0001C\u00117pG.$\u0015\r^1NC:\fw-\u001a:\u0011\u0005aYR\"A\r\u000b\u0005i\u0011\u0011AB7f[>\u0014\u00180\u0003\u0002\u001d3\t!\"\t\\8dW\u00163\u0018n\u0019;j_:D\u0015M\u001c3mKJ\u0004\"AH\u0011\u000e\u0003}Q!\u0001\t\u0003\u0002\u0011%tG/\u001a:oC2L!AI\u0010\u0003\u000f1{wmZ5oO\"AA\u0005\u0001B\u0001B\u0003%a%\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012\u001c\u0001\u0001\u0005\u0002(U9\u0011A\u0002K\u0005\u0003S5\ta\u0001\u0015:fI\u00164\u0017BA\u0016-\u0005\u0019\u0019FO]5oO*\u0011\u0011&\u0004\u0005\t]\u0001\u0011\t\u0011)A\u0005_\u00051!\u000f]2F]Z\u0004\"\u0001M\u001a\u000e\u0003ER!A\r\u0003\u0002\u0007I\u00048-\u0003\u00025c\t1!\u000b]2F]ZD\u0001B\u000e\u0001\u0003\u0006\u0004%\taN\u0001\u0007[\u0006\u001cH/\u001a:\u0016\u0003a\u0002\"!\u000f\u001e\u000e\u0003\tI!a\u000f\u0002\u0003%\tcwnY6NC:\fw-\u001a:NCN$XM\u001d\u0005\t{\u0001\u0011\t\u0011)A\u0005q\u00059Q.Y:uKJ\u0004\u0003\u0002C \u0001\u0005\u000b\u0007I\u0011\u0001!\u0002#M,'/[1mSj,'/T1oC\u001e,'/F\u0001B!\t\u0011U)D\u0001D\u0015\t!E!\u0001\u0006tKJL\u0017\r\\5{KJL!AR\"\u0003#M+'/[1mSj,'/T1oC\u001e,'\u000f\u0003\u0005I\u0001\t\u0005\t\u0015!\u0003B\u0003I\u0019XM]5bY&TXM]'b]\u0006<WM\u001d\u0011\t\u0011)\u0003!Q1A\u0005\u0002-\u000bAaY8oMV\tA\n\u0005\u0002N\u001d6\tA!\u0003\u0002P\t\tI1\u000b]1sW\u000e{gN\u001a\u0005\t#\u0002\u0011\t\u0011)A\u0005\u0019\u0006)1m\u001c8gA!A1\u000b\u0001B\u0001B\u0003%A+A\u0007nK6|'/_'b]\u0006<WM\u001d\t\u0003+^k\u0011A\u0016\u0006\u00035\u0011I!\u0001\u0017,\u0003\u001b5+Wn\u001c:z\u001b\u0006t\u0017mZ3s\u0011!Q\u0006A!A!\u0002\u0013Y\u0016\u0001E7ba>+H\u000f];u)J\f7m[3s!\tiE,\u0003\u0002^\t\t\u0001R*\u00199PkR\u0004X\u000f\u001e+sC\u000e\\WM\u001d\u0005\t?\u0002\u0011\t\u0011)A\u0005A\u0006q1\u000f[;gM2,W*\u00198bO\u0016\u0014\bCA1e\u001b\u0005\u0011'BA2\u0005\u0003\u001d\u0019\b.\u001e4gY\u0016L!!\u001a2\u0003\u001dMCWO\u001a4mK6\u000bg.Y4fe\"Aq\r\u0001BC\u0002\u0013\u0005\u0001.\u0001\u000bcY>\u001c7\u000e\u0016:b]N4WM]*feZL7-Z\u000b\u0002SB\u0011!C[\u0005\u0003WN\u0011AC\u00117pG.$&/\u00198tM\u0016\u00148+\u001a:wS\u000e,\u0007\u0002C7\u0001\u0005\u0003\u0005\u000b\u0011B5\u0002+\tdwnY6Ue\u0006t7OZ3s'\u0016\u0014h/[2fA!Aq\u000e\u0001B\u0001B\u0003%\u0001/A\btK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s!\ti\u0015/\u0003\u0002s\t\ty1+Z2ve&$\u00180T1oC\u001e,'\u000f\u0003\u0005u\u0001\t\u0005\t\u0015!\u0003v\u00039qW/\\+tC\ndWmQ8sKN\u0004\"\u0001\u0004<\n\u0005]l!aA%oi\")\u0011\u0010\u0001C\u0001u\u00061A(\u001b8jiz\"2c\u001f?~}~\f\t!a\u0001\u0002\u0006\u0005\u001d\u0011\u0011BA\u0006\u0003\u001b\u0001\"!\u000f\u0001\t\u000b\u0011B\b\u0019\u0001\u0014\t\u000b9B\b\u0019A\u0018\t\u000bYB\b\u0019\u0001\u001d\t\u000b}B\b\u0019A!\t\u000b)C\b\u0019\u0001'\t\u000bMC\b\u0019\u0001+\t\u000biC\b\u0019A.\t\u000b}C\b\u0019\u00011\t\u000b\u001dD\b\u0019A5\t\u000b=D\b\u0019\u00019\t\u000bQD\b\u0019A;\t\u0015\u0005E\u0001A1A\u0005\u0002\u0011\t\u0019\"A\u000ffqR,'O\\1m'\",hM\u001a7f'\u0016\u0014h/[2f\u000b:\f'\r\\3e+\t\t)\u0002E\u0002\r\u0003/I1!!\u0007\u000e\u0005\u001d\u0011un\u001c7fC:D\u0001\"!\b\u0001A\u0003%\u0011QC\u0001\u001fKb$XM\u001d8bYNCWO\u001a4mKN+'O^5dK\u0016s\u0017M\u00197fI\u0002B\u0011\"!\t\u0001\u0005\u0004%\t!a\t\u0002!\u0011L7o\u001b\"m_\u000e\\W*\u00198bO\u0016\u0014XCAA\u0013!\rI\u0014qE\u0005\u0004\u0003S\u0011!\u0001\u0005#jg.\u0014En\\2l\u001b\u0006t\u0017mZ3s\u0011!\ti\u0003\u0001Q\u0001\n\u0005\u0015\u0012!\u00053jg.\u0014En\\2l\u001b\u0006t\u0017mZ3sA!Q\u0011\u0011\u0007\u0001C\u0002\u0013\u0005!!a\r\u0002!\tdwnY6J]\u001a|W*\u00198bO\u0016\u0014XCAA\u001b!\rI\u0014qG\u0005\u0004\u0003s\u0011!\u0001\u0005\"m_\u000e\\\u0017J\u001c4p\u001b\u0006t\u0017mZ3s\u0011!\ti\u0004\u0001Q\u0001\n\u0005U\u0012!\u00052m_\u000e\\\u0017J\u001c4p\u001b\u0006t\u0017mZ3sA!I\u0011\u0011\t\u0001C\u0002\u0013%\u00111I\u0001\u0017MV$XO]3Fq\u0016\u001cW\u000f^5p]\u000e{g\u000e^3yiV\u0011\u0011Q\t\t\u0005\u0003\u000f\ni%\u0004\u0002\u0002J)\u0019\u00111J\u0007\u0002\u0015\r|gnY;se\u0016tG/\u0003\u0003\u0002P\u0005%#aH#yK\u000e,H/[8o\u0007>tG/\u001a=u\u000bb,7-\u001e;peN+'O^5dK\"A\u00111\u000b\u0001!\u0002\u0013\t)%A\fgkR,(/Z#yK\u000e,H/[8o\u0007>tG/\u001a=uA!Q\u0011q\u000b\u0001C\u0002\u0013\u0005A!!\u0017\u0002\u00175,Wn\u001c:z'R|'/Z\u000b\u0003\u00037\u00022\u0001GA/\u0013\r\ty&\u0007\u0002\f\u001b\u0016lwN]=Ti>\u0014X\r\u0003\u0005\u0002d\u0001\u0001\u000b\u0011BA.\u00031iW-\\8ssN#xN]3!\u0011)\t9\u0007\u0001b\u0001\n\u0003!\u0011\u0011N\u0001\nI&\u001c8n\u0015;pe\u0016,\"!a\u001b\u0011\u0007e\ni'C\u0002\u0002p\t\u0011\u0011\u0002R5tWN#xN]3\t\u0011\u0005M\u0004\u0001)A\u0005\u0003W\n!\u0002Z5tWN#xN]3!\u0011%\t9\b\u0001b\u0001\n\u0013\tI(A\bnCb|e\u000eS3ba6+Wn\u001c:z+\t\tY\bE\u0002\r\u0003{J1!a \u000e\u0005\u0011auN\\4\t\u0011\u0005\r\u0005\u0001)A\u0005\u0003w\n\u0001#\\1y\u001f:DU-\u00199NK6|'/\u001f\u0011\t\u0013\u0005\u001d\u0005A1A\u0005\n\u0005e\u0014\u0001E7bq>3g\rS3ba6+Wn\u001c:z\u0011!\tY\t\u0001Q\u0001\n\u0005m\u0014!E7bq>3g\rS3ba6+Wn\u001c:zA!I\u0011q\u0012\u0001C\u0002\u0013%\u0011\u0011S\u0001\u001bKb$XM\u001d8bYNCWO\u001a4mKN+'O^5dKB{'\u000f^\u000b\u0002k\"9\u0011Q\u0013\u0001!\u0002\u0013)\u0018aG3yi\u0016\u0014h.\u00197TQV4g\r\\3TKJ4\u0018nY3Q_J$\b\u0005C\u0006\u0002\u001a\u0002\u0001\r\u00111A\u0005\u0002\u0005m\u0015A\u00042m_\u000e\\W*\u00198bO\u0016\u0014\u0018\nZ\u000b\u0003\u0003;\u00032!OAP\u0013\r\t\tK\u0001\u0002\u000f\u00052|7m['b]\u0006<WM]%e\u0011-\t)\u000b\u0001a\u0001\u0002\u0004%\t!a*\u0002%\tdwnY6NC:\fw-\u001a:JI~#S-\u001d\u000b\u0005\u0003S\u000by\u000bE\u0002\r\u0003WK1!!,\u000e\u0005\u0011)f.\u001b;\t\u0015\u0005E\u00161UA\u0001\u0002\u0004\ti*A\u0002yIEB\u0001\"!.\u0001A\u0003&\u0011QT\u0001\u0010E2|7m['b]\u0006<WM]%eA!a\u0011\u0011\u0018\u0001A\u0002\u0003\u0007I\u0011\u0001\u0003\u0002\u001c\u0006y1\u000f[;gM2,7+\u001a:wKJLE\r\u0003\u0007\u0002>\u0002\u0001\r\u00111A\u0005\u0002\u0011\ty,A\ntQV4g\r\\3TKJ4XM]%e?\u0012*\u0017\u000f\u0006\u0003\u0002*\u0006\u0005\u0007BCAY\u0003w\u000b\t\u00111\u0001\u0002\u001e\"A\u0011Q\u0019\u0001!B\u0013\ti*\u0001\ttQV4g\r\\3TKJ4XM]%eA!Q\u0011\u0011\u001a\u0001C\u0002\u0013\u0005A!a3\u0002\u001bMDWO\u001a4mK\u000ec\u0017.\u001a8u+\t\ti\r\u0005\u0003\u0002P\u0006MWBAAi\u0015\t\u00197#\u0003\u0003\u0002V\u0006E'!D*ik\u001a4G.Z\"mS\u0016tG\u000f\u0003\u0005\u0002Z\u0002\u0001\u000b\u0011BAg\u00039\u0019\b.\u001e4gY\u0016\u001cE.[3oi\u0002B\u0011\"!8\u0001\u0005\u0004%I!!%\u0002A5\f\u0007PR1jYV\u0014Xm\u001d\"fM>\u0014X\rT8dCRLwN\u001c*fMJ,7\u000f\u001b\u0005\b\u0003C\u0004\u0001\u0015!\u0003v\u0003\u0005j\u0017\r\u001f$bS2,(/Z:CK\u001a|'/\u001a'pG\u0006$\u0018n\u001c8SK\u001a\u0014Xm\u001d5!\u0011%\t)\u000f\u0001b\u0001\n\u0013\t9/A\u0007tY\u00064X-\u00128ea>Lg\u000e^\u000b\u0003\u0003S\u00042\u0001MAv\u0013\r\ti/\r\u0002\u000f%B\u001cWI\u001c3q_&tGOU3g\u0011!\t\t\u0010\u0001Q\u0001\n\u0005%\u0018AD:mCZ,WI\u001c3q_&tG\u000f\t\u0005\n\u0003k\u0004\u0001\u0019!C\u0005\u0003o\f1#Y:z]\u000e\u0014VM]3hSN$XM\u001d+bg.,\"!!?\u0011\r\u0005\u001d\u00131`AU\u0013\u0011\ti0!\u0013\u0003\r\u0019+H/\u001e:f\u0011%\u0011\t\u0001\u0001a\u0001\n\u0013\u0011\u0019!A\fbgft7MU3sK\u001eL7\u000f^3s)\u0006\u001c8n\u0018\u0013fcR!\u0011\u0011\u0016B\u0003\u0011)\t\t,a@\u0002\u0002\u0003\u0007\u0011\u0011 \u0005\t\u0005\u0013\u0001\u0001\u0015)\u0003\u0002z\u0006!\u0012m]=oGJ+'/Z4jgR,'\u000fV1tW\u0002B\u0011B!\u0004\u0001\u0005\u0004%IAa\u0004\u0002'\u0005\u001c\u0018P\\2SKJ,w-[:uKJdunY6\u0016\u0005\tE\u0001\u0003\u0002B\n\u0005;i!A!\u0006\u000b\t\t]!\u0011D\u0001\u0005Y\u0006twM\u0003\u0002\u0003\u001c\u0005!!.\u0019<b\u0013\u0011\u0011yB!\u0006\u0003\r=\u0013'.Z2u\u0011!\u0011\u0019\u0003\u0001Q\u0001\n\tE\u0011\u0001F1ts:\u001c'+\u001a:fO&\u001cH/\u001a:M_\u000e\\\u0007\u0005C\u0006\u0003(\u0001\u0001\r\u00111A\u0005\n\t%\u0012aC2bG\",G\rU3feN,\"Aa\u000b\u0011\r\t5\"QHAO\u001d\u0011\u0011yC!\u000f\u000f\t\tE\"qG\u0007\u0003\u0005gQ1A!\u000e&\u0003\u0019a$o\\8u}%\ta\"C\u0002\u0003<5\tq\u0001]1dW\u0006<W-\u0003\u0003\u0003@\t\u0005#aA*fc*\u0019!1H\u0007\t\u0017\t\u0015\u0003\u00011AA\u0002\u0013%!qI\u0001\u0010G\u0006\u001c\u0007.\u001a3QK\u0016\u00148o\u0018\u0013fcR!\u0011\u0011\u0016B%\u0011)\t\tLa\u0011\u0002\u0002\u0003\u0007!1\u0006\u0005\t\u0005\u001b\u0002\u0001\u0015)\u0003\u0003,\u0005a1-Y2iK\u0012\u0004V-\u001a:tA!\"!1\nB)!\ra!1K\u0005\u0004\u0005+j!\u0001\u0003<pY\u0006$\u0018\u000e\\3\t\u0013\te\u0003A1A\u0005\n\t=\u0011!\u00049fKJ4U\r^2i\u0019>\u001c7\u000e\u0003\u0005\u0003^\u0001\u0001\u000b\u0011\u0002B\t\u00039\u0001X-\u001a:GKR\u001c\u0007\u000eT8dW\u0002B\u0011B!\u0019\u0001\u0001\u0004%I!!\u001f\u0002#1\f7\u000f\u001e)fKJ4U\r^2i)&lW\rC\u0005\u0003f\u0001\u0001\r\u0011\"\u0003\u0003h\u0005)B.Y:u!\u0016,'OR3uG\"$\u0016.\\3`I\u0015\fH\u0003BAU\u0005SB!\"!-\u0003d\u0005\u0005\t\u0019AA>\u0011!\u0011i\u0007\u0001Q!\n\u0005m\u0014A\u00057bgR\u0004V-\u001a:GKR\u001c\u0007\u000eV5nK\u0002B1B!\u001d\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u0003t\u00051\"\r\\8dWJ+\u0007\u000f\\5dCRLwN\u001c)pY&\u001c\u00170\u0006\u0002\u0003vA\u0019\u0011Ha\u001e\n\u0007\te$A\u0001\fCY>\u001c7NU3qY&\u001c\u0017\r^5p]B{G.[2z\u0011-\u0011i\b\u0001a\u0001\u0002\u0004%IAa \u00025\tdwnY6SKBd\u0017nY1uS>t\u0007k\u001c7jGf|F%Z9\u0015\t\u0005%&\u0011\u0011\u0005\u000b\u0003c\u0013Y(!AA\u0002\tU\u0004\u0002\u0003BC\u0001\u0001\u0006KA!\u001e\u0002/\tdwnY6SKBd\u0017nY1uS>t\u0007k\u001c7jGf\u0004\u0003B\u0003BE\u0001\t\u0007I\u0011\u0001\u0002\u0003\f\u0006Q\"/Z7pi\u0016\u0014En\\2l)\u0016l\u0007OR5mK6\u000bg.Y4feV\u0011!Q\u0012\t\u0005\u0005\u001f\u001b\u0019DD\u0002:\u0005#;\u0001Ba%\u0003\u0011\u0003!!QS\u0001\r\u00052|7m['b]\u0006<WM\u001d\t\u0004s\t]eaB\u0001\u0003\u0011\u0003!!\u0011T\n\u0004\u0005/[\u0001bB=\u0003\u0018\u0012\u0005!Q\u0014\u000b\u0003\u0005+C!B!)\u0003\u0018\n\u0007I\u0011\u0002BR\u00031IEiX$F\u001d\u0016\u0013\u0016\tV(S+\t\u0011)\u000b\u0005\u0003\u0003(\n5VB\u0001BU\u0015\r\u0011Y\u000bB\u0001\u0005kRLG.\u0003\u0003\u00030\n%&aC%e\u000f\u0016tWM]1u_JD\u0011Ba-\u0003\u0018\u0002\u0006IA!*\u0002\u001b%#ulR#O\u000bJ\u000bEk\u0014*!\u0011!\u00119La&\u0005\u0002\te\u0016a\u00042m_\u000e\\\u0017\nZ:U_\"{7\u000f^:\u0015\u0011\tm&\u0011\u001aBj\u0005;\u0004ra\nB_\u0005\u0003\u00149-C\u0002\u0003@2\u00121!T1q!\rI$1Y\u0005\u0004\u0005\u000b\u0014!a\u0002\"m_\u000e\\\u0017\n\u001a\t\u0006\u0005[\u0011iD\n\u0005\t\u0005\u0017\u0014)\f1\u0001\u0003N\u0006A!\r\\8dW&#7\u000fE\u0003\r\u0005\u001f\u0014\t-C\u0002\u0003R6\u0011Q!\u0011:sCfD\u0001B!6\u00036\u0002\u0007!q[\u0001\u0004K:4\bcA'\u0003Z&\u0019!1\u001c\u0003\u0003\u0011M\u0003\u0018M]6F]ZD\u0011Ba8\u00036B\u0005\t\u0019\u0001\u001d\u0002%\tdwnY6NC:\fw-\u001a:NCN$XM\u001d\u0004\b\u0005G\u00149\n\u0002Bs\u0005Q\u0019\u0006.\u001e4gY\u0016lU\r\u001e:jGN\u001cv.\u001e:dKN)!\u0011]\u0006\u0003hB!!\u0011\u001eBz\u001b\t\u0011YO\u0003\u0003\u0003n\n=\u0018AB:pkJ\u001cWMC\u0002\u0003r\u0012\tq!\\3ue&\u001c7/\u0003\u0003\u0003v\n-(AB*pkJ\u001cW\rC\u0006\u0003z\n\u0005(Q1A\u0005B\tm\u0018AC:pkJ\u001cWMT1nKV\ta\u0005\u0003\u0006\u0003\u0000\n\u0005(\u0011!Q\u0001\n\u0019\n1b]8ve\u000e,g*Y7fA!Y11\u0001Bq\u0005\u0003\u0005\u000b\u0011BB\u0003\u0003%iW\r\u001e:jGN+G\u000f\u0005\u0003\u0004\b\rMQBAB\u0005\u0015\u0011\u0011\tpa\u0003\u000b\t\r51qB\u0001\tG>$\u0017\r[1mK*\u00111\u0011C\u0001\u0004G>l\u0017\u0002BB\u000b\u0007\u0013\u0011\u0011\"T3ue&\u001c7+\u001a;\t\u000fe\u0014\t\u000f\"\u0001\u0004\u001aQ111DB\u0010\u0007C\u0001Ba!\b\u0003b6\u0011!q\u0013\u0005\b\u0005s\u001c9\u00021\u0001'\u0011!\u0019\u0019aa\u0006A\u0002\r\u0015\u0001BCB\u0013\u0005C\u0014\r\u0011\"\u0011\u0004(\u0005qQ.\u001a;sS\u000e\u0014VmZ5tiJLXCAB\u0015!\u0011\u00199aa\u000b\n\t\r52\u0011\u0002\u0002\u000f\u001b\u0016$(/[2SK\u001eL7\u000f\u001e:z\u0011%\u0019\tD!9!\u0002\u0013\u0019I#A\bnKR\u0014\u0018n\u0019*fO&\u001cHO]=!\r\u001d\u0019)Da&\u0001\u0007o\u0011!DU3n_R,'\t\\8dWR+W\u000e\u001d$jY\u0016l\u0015M\\1hKJ\u001craa\r\u0003\u0012\reR\u0004\u0005\u0003\u0002P\u000em\u0012\u0002BB\u001f\u0003#\u0014q\u0002V3na\u001aKG.Z'b]\u0006<WM\u001d\u0005\u000b\u0007\u0003\u001a\u0019D!A!\u0002\u0013Y\u0018\u0001\u00042m_\u000e\\W*\u00198bO\u0016\u0014\bbB=\u00044\u0011\u00051Q\t\u000b\u0005\u0007\u000f\u001aI\u0005\u0005\u0003\u0004\u001e\rM\u0002bBB!\u0007\u0007\u0002\ra\u001f\u0004\b\u0007\u001b\u001a\u0019\u0004BB(\u0005Q\u0011VMZ3sK:\u001cWmV5uQ\u000ecW-\u00198vaN!11JB)!\u0019\u0019\u0019f!\u0017\u0004^5\u00111Q\u000b\u0006\u0005\u0007/\u0012)\"A\u0002sK\u001aLAaa\u0017\u0004V\tiq+Z1l%\u00164WM]3oG\u0016\u0004Baa\u0018\u0004f5\u00111\u0011\r\u0006\u0005\u0007G\u0012I\"\u0001\u0002j_&!1qMB1\u0005\u00111\u0015\u000e\\3\t\u0017\r-41\nB\u0001B\u0003%1QL\u0001\u0005M&dW\rC\u0006\u0004p\r-#\u0011!Q\u0001\n\rE\u0014A\u0004:fM\u0016\u0014XM\\2f#V,W/\u001a\t\u0007\u0007'\u001a\u0019h!\u0018\n\t\rU4Q\u000b\u0002\u000f%\u00164WM]3oG\u0016\fV/Z;f\u0011\u001dI81\nC\u0001\u0007s\"baa\u001f\u0004\u0000\r\u0005\u0005\u0003BB?\u0007\u0017j!aa\r\t\u0011\r-4q\u000fa\u0001\u0007;B\u0001ba\u001c\u0004x\u0001\u00071\u0011\u000f\u0005\u000b\u0007\u000b\u001bYE1A\u0005\n\r\u001d\u0015\u0001\u00034jY\u0016\u0004\u0016\r\u001e5\u0016\u0005\r%\u0005\u0003\u0002B\n\u0007\u0017K1a\u000bB\u000b\u0011%\u0019yia\u0013!\u0002\u0013\u0019I)A\u0005gS2,\u0007+\u0019;iA!A11SB&\t\u0003\u0019)*A\u0004dY\u0016\fg.\u00169\u0015\u0005\u0005%\u0006BCB8\u0007g\u0011\r\u0011\"\u0003\u0004\u001aV\u00111\u0011\u000f\u0005\n\u0007;\u001b\u0019\u0004)A\u0005\u0007c\nqB]3gKJ,gnY3Rk\u0016,X\r\t\u0005\u000b\u0007C\u001b\u0019D1A\u0005\n\r\r\u0016a\u0004:fM\u0016\u0014XM\\2f\u0005V4g-\u001a:\u0016\u0005\r\u0015\u0006CBBT\u0007W\u001bY(\u0004\u0002\u0004**!!1\u0016B\r\u0013\u0011\u0019ik!+\u0003\u0007M+G\u000fC\u0005\u00042\u000eM\u0002\u0015!\u0003\u0004&\u0006\u0001\"/\u001a4fe\u0016t7-\u001a\"vM\u001a,'\u000f\t\u0005\u000b\u0007k\u001b\u0019D1A\u0005\n\u0005E\u0015\u0001\u0004)P\u00192{F+S'F\u001fV#\u0006\u0002CB]\u0007g\u0001\u000b\u0011B;\u0002\u001bA{E\nT0U\u00136+u*\u0016+!\u0011)\u0019ila\rA\u0002\u0013%\u00111C\u0001\bgR|\u0007\u000f]3e\u0011)\u0019\tma\rA\u0002\u0013%11Y\u0001\fgR|\u0007\u000f]3e?\u0012*\u0017\u000f\u0006\u0003\u0002*\u000e\u0015\u0007BCAY\u0007\u007f\u000b\t\u00111\u0001\u0002\u0016!I1\u0011ZB\u001aA\u0003&\u0011QC\u0001\tgR|\u0007\u000f]3eA!\"1q\u0019B)\u0011)\u0019yma\rC\u0002\u0013%1\u0011[\u0001\u000fG2,\u0017M\\5oORC'/Z1e+\t\u0019\u0019\u000e\u0005\u0003\u0003\u0014\rU\u0017\u0002BBl\u0005+\u0011a\u0001\u00165sK\u0006$\u0007\"CBn\u0007g\u0001\u000b\u0011BBj\u0003=\u0019G.Z1oS:<G\u000b\u001b:fC\u0012\u0004\u0003\u0002CBp\u0007g!\te!9\u0002\u001d\r\u0014X-\u0019;f)\u0016l\u0007OR5mKR\u00111Q\f\u0005\t\u0007K\u001c\u0019\u0004\"\u0011\u0004h\u00069\"/Z4jgR,'\u000fV3na\u001aKG.\u001a+p\u00072,\u0017M\u001c\u000b\u0005\u0003+\u0019I\u000f\u0003\u0005\u0004l\r\r\b\u0019AB/\u0011!\u0019ioa\r\u0005\u0002\rU\u0015\u0001B:u_BD\u0001b!=\u00044\u0011%1QS\u0001\rW\u0016,\u0007o\u00117fC:Lgn\u001a\u0005\u000b\u0007k\u00149*%A\u0005\u0002\r]\u0018!\u00072m_\u000e\\\u0017\nZ:U_\"{7\u000f^:%I\u00164\u0017-\u001e7uIM*\"a!?+\u0007a\u001aYp\u000b\u0002\u0004~B!1q C\u0005\u001b\t!\tA\u0003\u0003\u0005\u0004\u0011\u0015\u0011!C;oG\",7m[3e\u0015\r!9!D\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002\u0002C\u0006\t\u0003\u0011\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011!!y\u0001\u0001Q\u0001\n\t5\u0015a\u0007:f[>$XM\u00117pG.$V-\u001c9GS2,W*\u00198bO\u0016\u0014\b\u0005C\u0005\u0005\u0014\u0001\u0011\r\u0011\"\u0003\u0002z\u0005\u0019R.\u0019=SK6|G/\u001a\"m_\u000e\\Gk\\'f[\"AAq\u0003\u0001!\u0002\u0013\tY(\u0001\u000bnCb\u0014V-\\8uK\ncwnY6U_6+W\u000e\t\u0005\b\t7\u0001A\u0011\u0001C\u000f\u0003)Ig.\u001b;jC2L'0\u001a\u000b\u0005\u0003S#y\u0002C\u0004\u0005\"\u0011e\u0001\u0019\u0001\u0014\u0002\u000b\u0005\u0004\b/\u00133\t\u000f\u0011\u0015\u0002\u0001\"\u0001\u0005(\u0005!2\u000f[;gM2,W*\u001a;sS\u000e\u001c8k\\;sG\u0016,\"Aa:\t\u000f\u0011-\u0002\u0001\"\u0003\u0004\u0016\u0006\t#/Z4jgR,'oV5uQ\u0016CH/\u001a:oC2\u001c\u0006.\u001e4gY\u0016\u001cVM\u001d<fe\"9Aq\u0006\u0001\u0005\n\rU\u0015a\u0004:fa>\u0014H/\u00117m\u00052|7m[:\t\u000f\u0011M\u0002\u0001\"\u0001\u0004\u0016\u0006Q!/\u001a:fO&\u001cH/\u001a:\t\u000f\u0011]\u0002\u0001\"\u0003\u0004\u0016\u0006y\u0011m]=oGJ+'/Z4jgR,'\u000fC\u0004\u0005<\u0001!\ta!&\u0002-]\f\u0017\u000e\u001e$pe\u0006\u001b\u0018P\\2SKJ,w-[:uKJDq\u0001b\u0010\u0001\t\u0003\"\t%\u0001\u0007hKR\u0014En\\2l\t\u0006$\u0018\r\u0006\u0003\u0005D\u0011=\u0003\u0003\u0002C#\t\u0017j!\u0001b\u0012\u000b\u0007\u0011%3#\u0001\u0004ck\u001a4WM]\u0005\u0005\t\u001b\"9EA\u0007NC:\fw-\u001a3Ck\u001a4WM\u001d\u0005\t\t#\"i\u00041\u0001\u0003B\u00069!\r\\8dW&#\u0007b\u0002C+\u0001\u0011\u0005CqK\u0001\raV$(\t\\8dW\u0012\u000bG/\u0019\u000b\u000b\u0003+!I\u0006b\u0017\u0005`\u0011%\u0004\u0002\u0003C)\t'\u0002\rA!1\t\u0011\u0011uC1\u000ba\u0001\t\u0007\nA\u0001Z1uC\"AA\u0011\rC*\u0001\u0004!\u0019'A\u0003mKZ,G\u000eE\u0002:\tKJ1\u0001b\u001a\u0003\u00051\u0019Fo\u001c:bO\u0016dUM^3m\u0011!!Y\u0007b\u0015A\u0002\u00115\u0014\u0001C2mCN\u001cH+Y41\t\u0011=Dq\u0010\t\u0007\tc\"9\bb\u001f\u000e\u0005\u0011M$b\u0001C;\u001b\u00059!/\u001a4mK\u000e$\u0018\u0002\u0002C=\tg\u0012\u0001b\u00117bgN$\u0016m\u001a\t\u0005\t{\"y\b\u0004\u0001\u0005\u0019\u0011\u0005E\u0011NA\u0001\u0002\u0003\u0015\t\u0001b!\u0003\u0007}#\u0013'\u0005\u0003\u0005\u0006\u0012-\u0005c\u0001\u0007\u0005\b&\u0019A\u0011R\u0007\u0003\u000f9{G\u000f[5oOB\u0019A\u0002\"$\n\u0007\u0011=UBA\u0002B]fDq\u0001b%\u0001\t\u0003!)*A\u0005hKR\u001cF/\u0019;vgR!Aq\u0013CR!\u0015aA\u0011\u0014CO\u0013\r!Y*\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0007e\"y*C\u0002\u0005\"\n\u00111B\u00117pG.\u001cF/\u0019;vg\"AA\u0011\u000bCI\u0001\u0004\u0011\t\rC\u0004\u0005(\u0002!\t\u0001\"+\u0002'\u001d,G/T1uG\"Lgn\u001a\"m_\u000e\\\u0017\nZ:\u0015\t\u0011-FQ\u0016\t\u0007\u0005[\u0011iD!1\t\u0011\u0011=FQ\u0015a\u0001\tc\u000baAZ5mi\u0016\u0014\bc\u0002\u0007\u00054\n\u0005\u0017QC\u0005\u0004\tkk!!\u0003$v]\u000e$\u0018n\u001c82\u0011\u001d!I\f\u0001C\u0005\tw\u000b\u0011C]3q_J$(\t\\8dWN#\u0018\r^;t)!\tI\u000b\"0\u0005@\u0012\r\u0007\u0002\u0003C)\to\u0003\rA!1\t\u0011\u0011\u0005Gq\u0017a\u0001\t;\u000baa\u001d;biV\u001c\bB\u0003Cc\to\u0003\n\u00111\u0001\u0002|\u0005\tBM]8qa\u0016$W*Z7pef\u001c\u0016N_3\t\u000f\u0011%\u0007\u0001\"\u0003\u0005L\u00061BO]=U_J+\u0007o\u001c:u\u00052|7m[*uCR,8\u000f\u0006\u0005\u0002\u0016\u00115Gq\u001aCi\u0011!!\t\u0006b2A\u0002\t\u0005\u0007\u0002\u0003Ca\t\u000f\u0004\r\u0001\"(\t\u0015\u0011\u0015Gq\u0019I\u0001\u0002\u0004\tY\bC\u0004\u0005V\u0002!I\u0001b6\u0002+\u001d,GoQ;se\u0016tGO\u00117pG.\u001cF/\u0019;vgR1AQ\u0014Cm\t7D\u0001\u0002\"\u0015\u0005T\u0002\u0007!\u0011\u0019\u0005\t\t;$\u0019\u000e1\u0001\u0005`\u0006!\u0011N\u001c4p!\rID\u0011]\u0005\u0004\tG\u0014!!\u0003\"m_\u000e\\\u0017J\u001c4p\u0011\u001d!9\u000f\u0001C\u0005\tS\f1cZ3u\u0019>\u001c\u0017\r^5p]\ncwnY6JIN$B\u0001b;\u0005nB)ABa4\u0003,!A!1\u001aCs\u0001\u0004\u0011i\rC\u0004\u0005r\u0002!I\u0001b=\u0002-!\fg\u000e\u001a7f\u0019>\u001c\u0017\r\u001c*fC\u00124\u0015-\u001b7ve\u0016$B\u0001\"\"\u0005v\"AA\u0011\u000bCx\u0001\u0004\u0011\t\rC\u0004\u0005z\u0002!\t\u0001b?\u0002\u001d\u001d,G\u000fT8dC24\u0016\r\\;fgR!AQ`C\u0003!\u0015aA\u0011\u0014C\u0000!\rIT\u0011A\u0005\u0004\u000b\u0007\u0011!a\u0003\"m_\u000e\\'+Z:vYRD\u0001\u0002\"\u0015\u0005x\u0002\u0007!\u0011\u0019\u0005\b\u000b\u0013\u0001A\u0011AC\u0006\u000359W\r\u001e'pG\u0006d')\u001f;fgR!QQBC\u000b!\u0015aA\u0011TC\b!\rIT\u0011C\u0005\u0004\u000b'\u0011!!\u0003\"m_\u000e\\G)\u0019;b\u0011!!\t&b\u0002A\u0002\t\u0005\u0007bBC\r\u0001\u0011%Q1D\u0001\u0010I><U\r\u001e'pG\u0006d')\u001f;fgR1QqBC\u000f\u000b?A\u0001\u0002\"\u0015\u0006\u0018\u0001\u0007!\u0011\u0019\u0005\t\t;,9\u00021\u0001\u0005`\"9Q1\u0005\u0001\u0005\n\u0015\u0015\u0012aD4fiJ+Wn\u001c;f-\u0006dW/Z:\u0016\t\u0015\u001dR1\u0007\u000b\u0005\u000bS)9\u0004\u0006\u0003\u0005~\u0016-\u0002BCC\u0017\u000bC\t\t\u0011q\u0001\u00060\u0005QQM^5eK:\u001cW\rJ\u0019\u0011\r\u0011EDqOC\u0019!\u0011!i(b\r\u0005\u0011\u0015UR\u0011\u0005b\u0001\t\u0007\u0013\u0011\u0001\u0016\u0005\t\t#*\t\u00031\u0001\u0003B\"9Q1\b\u0001\u0005\n\u0015u\u0012!D:peRdunY1uS>t7\u000f\u0006\u0003\u0003,\u0015}\u0002\u0002CC!\u000bs\u0001\rAa\u000b\u0002\u00131|7-\u0019;j_:\u001c\bbBC#\u0001\u0011\u0005QqI\u0001\u000fO\u0016$(+Z7pi\u0016\u0014\u0015\u0010^3t)\u0011)I%\"\u0016\u0011\u000b1!I*b\u0013\u0011\t\u00155S\u0011K\u0007\u0003\u000b\u001fRAaa\u0019\u0003*&!Q1KC(\u0005E\u0019\u0005.\u001e8lK\u0012\u0014\u0015\u0010^3Ck\u001a4WM\u001d\u0005\t\t#*\u0019\u00051\u0001\u0003B\"9Q\u0011\f\u0001\u0005\u0002\u0015m\u0013aA4fiV!QQLC5)\u0011)y&b\u001b\u0015\t\u0011uX\u0011\r\u0005\u000b\u000bG*9&!AA\u0004\u0015\u0015\u0014AC3wS\u0012,gnY3%eA1A\u0011\u000fC<\u000bO\u0002B\u0001\" \u0006j\u0011AQQGC,\u0005\u0004!\u0019\t\u0003\u0005\u0005R\u0015]\u0003\u0019\u0001Ba\u0011\u001d)y\u0007\u0001C\u0001\u000bc\nQ\u0002Z8x]\u001e\u0014\u0018\rZ3M_\u000e\\G\u0003BAU\u000bgB\u0001\u0002\"\u0015\u0006n\u0001\u0007!\u0011\u0019\u0005\b\u000bo\u0002A\u0011AC=\u0003-\u0011X\r\\3bg\u0016dunY6\u0015\r\u0005%V1PC?\u0011!!\t&\"\u001eA\u0002\t\u0005\u0007BCC@\u000bk\u0002\n\u00111\u0001\u0006\u0002\u0006iA/Y:l\u0003R$X-\u001c9u\u0013\u0012\u0004R\u0001\u0004CM\u0003wBq!\"\"\u0001\t\u0003)9)\u0001\u0007sK\u001eL7\u000f^3s)\u0006\u001c8\u000e\u0006\u0003\u0002*\u0016%\u0005\u0002CC@\u000b\u0007\u0003\r!a\u001f\t\u000f\u00155\u0005\u0001\"\u0001\u0006\u0010\u00061\"/\u001a7fCN,\u0017\t\u001c7M_\u000e\\7OR8s)\u0006\u001c8\u000e\u0006\u0003\u0005,\u0016E\u0005\u0002CC@\u000b\u0017\u0003\r!a\u001f\t\u000f\u0015U\u0005\u0001\"\u0001\u0006\u0018\u0006yq-\u001a;Pe\u0016c7/Z+qI\u0006$X-\u0006\u0003\u0006\u001a\u0016%FCCCN\u000bW+i+b,\u00064BA!QFCO\t\u007f,\t+\u0003\u0003\u0006 \n\u0005#AB#ji\",'\u000f\u0005\u0004\u0003.\u0015\rVqU\u0005\u0005\u000bK\u0013\tE\u0001\u0005Ji\u0016\u0014\u0018\r^8s!\u0011!i(\"+\u0005\u0011\u0015UR1\u0013b\u0001\t\u0007C\u0001\u0002\"\u0015\u0006\u0014\u0002\u0007!\u0011\u0019\u0005\t\tC*\u0019\n1\u0001\u0005d!AA1NCJ\u0001\u0004)\t\f\u0005\u0004\u0005r\u0011]Tq\u0015\u0005\t\u000bk+\u0019\n1\u0001\u00068\u0006aQ.Y6f\u0013R,'/\u0019;peB)A\"\"/\u0006\"&\u0019Q1X\u0007\u0003\u0013\u0019+hn\u0019;j_:\u0004\u0004bBC`\u0001\u0011\u0005Q\u0011Y\u0001\faV$\u0018\n^3sCR|'/\u0006\u0003\u0006D\u0016=GCCCc\u000b#,\u0019.\"7\u0006\\R!\u0011QCCd\u0011))I-\"0\u0002\u0002\u0003\u000fQ1Z\u0001\u000bKZLG-\u001a8dK\u0012\u001a\u0004C\u0002C9\to*i\r\u0005\u0003\u0005~\u0015=G\u0001CC\u001b\u000b{\u0013\r\u0001b!\t\u0011\u0011ESQ\u0018a\u0001\u0005\u0003D\u0001\"\"6\u0006>\u0002\u0007Qq[\u0001\u0007m\u0006dW/Z:\u0011\r\t5R1UCg\u0011!!\t'\"0A\u0002\u0011\r\u0004BCCo\u000b{\u0003\n\u00111\u0001\u0002\u0016\u0005QA/\u001a7m\u001b\u0006\u001cH/\u001a:\t\u000f\u0015\u0005\b\u0001\"\u0001\u0006d\u0006iq-\u001a;ESN\\wK]5uKJ$B\"\":\u0006l\u00165Xq^C}\u000b{\u00042!OCt\u0013\r)IO\u0001\u0002\u0016\t&\u001c8N\u00117pG.|%M[3di^\u0013\u0018\u000e^3s\u0011!!\t&b8A\u0002\t\u0005\u0007\u0002CB6\u000b?\u0004\ra!\u0018\t\u0011\u0015EXq\u001ca\u0001\u000bg\f!c]3sS\u0006d\u0017N_3s\u0013:\u001cH/\u00198dKB\u0019!)\">\n\u0007\u0015]8I\u0001\nTKJL\u0017\r\\5{KJLen\u001d;b]\u000e,\u0007bBC~\u000b?\u0004\r!^\u0001\u000bEV4g-\u001a:TSj,\u0007\u0002CC\u0000\u000b?\u0004\rA\"\u0001\u0002\u0019]\u0014\u0018\u000e^3NKR\u0014\u0018nY:\u0011\t\u0019\ra\u0011B\u0007\u0003\r\u000bQ1Ab\u0002\u0005\u0003!)\u00070Z2vi>\u0014\u0018\u0002\u0002D\u0006\r\u000b\u00111c\u00155vM\u001adWm\u0016:ji\u0016lU\r\u001e:jGNDqAb\u0004\u0001\t\u00031\t\"\u0001\u0005qkR\u0014\u0015\u0010^3t+\u00111\u0019Bb\b\u0015\u0015\u0019Ua\u0011\u0005D\u0012\rO1I\u0003\u0006\u0003\u0002\u0016\u0019]\u0001B\u0003D\r\r\u001b\t\t\u0011q\u0001\u0007\u001c\u0005QQM^5eK:\u001cW\r\n\u001b\u0011\r\u0011EDq\u000fD\u000f!\u0011!iHb\b\u0005\u0011\u0015UbQ\u0002b\u0001\t\u0007C\u0001\u0002\"\u0015\u0007\u000e\u0001\u0007!\u0011\u0019\u0005\t\rK1i\u00011\u0001\u0006L\u0005)!-\u001f;fg\"AA\u0011\rD\u0007\u0001\u0004!\u0019\u0007\u0003\u0006\u0006^\u001a5\u0001\u0013!a\u0001\u0003+AqA\"\f\u0001\t\u00131y#\u0001\u0006e_B+HOQ=uKN,BA\"\r\u0007@Qq\u0011Q\u0003D\u001a\rk19D\"\u000f\u0007B\u0019\r\u0003\u0002\u0003C)\rW\u0001\rA!1\t\u0011\u0019\u0015b1\u0006a\u0001\u000b\u0017B\u0001\u0002\"\u0019\u0007,\u0001\u0007A1\r\u0005\t\tW2Y\u00031\u0001\u0007<A1A\u0011\u000fC<\r{\u0001B\u0001\" \u0007@\u0011AQQ\u0007D\u0016\u0005\u0004!\u0019\t\u0003\u0006\u0006^\u001a-\u0002\u0013!a\u0001\u0003+A!B\"\u0012\u0007,A\u0005\t\u0019AA\u000b\u00031YW-\u001a9SK\u0006$Gj\\2l\u0011\u001d1I\u0005\u0001C\u0005\r\u0017\nQ\u0001Z8QkR,BA\"\u0014\u0007VQaaq\nD/\r?2\tG\"\u001c\u0007pQ!a\u0011\u000bD,!\u0015aA\u0011\u0014D*!\u0011!iH\"\u0016\u0005\u0011\u0015Ubq\tb\u0001\t\u0007C\u0001B\"\u0017\u0007H\u0001\u0007a1L\u0001\baV$(i\u001c3z!\u001daA1\u0017Cp\r#B\u0001\u0002\"\u0015\u0007H\u0001\u0007!\u0011\u0019\u0005\t\tC29\u00051\u0001\u0005d!AA1\u000eD$\u0001\u00041\u0019\u0007\r\u0003\u0007f\u0019%\u0004C\u0002C9\to29\u0007\u0005\u0003\u0005~\u0019%D\u0001\u0004D6\rC\n\t\u0011!A\u0003\u0002\u0011\r%aA0%e!AQQ\u001cD$\u0001\u0004\t)\u0002\u0003\u0005\u0007F\u0019\u001d\u0003\u0019AA\u000b\u0011\u001d1\u0019\b\u0001C\u0005\rk\nQ\u0002Z8QkRLE/\u001a:bi>\u0014X\u0003\u0002D<\r\u0007#bB\"\u001f\u0007\u0006\u001a\u001deq\u0012DI\r+39\nE\u0003\r\t33Y\bE\u0003\u0019\r{2\t)C\u0002\u0007\u0000e\u0011\u0011\u0004U1si&\fG\u000e\\=V]J|G\u000e\\3e\u0013R,'/\u0019;peB!AQ\u0010DB\t!))D\"\u001dC\u0002\u0011\r\u0005\u0002\u0003C)\rc\u0002\rA!1\t\u0011\u0019%e\u0011\u000fa\u0001\r\u0017\u000b\u0001\"\u001b;fe\u0006$xN\u001d\t\u0006\u0019\u0015efQ\u0012\t\u0007\u0005[)\u0019K\"!\t\u0011\u0011\u0005d\u0011\u000fa\u0001\tGB\u0001\u0002b\u001b\u0007r\u0001\u0007a1\u0013\t\u0007\tc\"9H\"!\t\u0015\u0015ug\u0011\u000fI\u0001\u0002\u0004\t)\u0002\u0003\u0006\u0007F\u0019E\u0004\u0013!a\u0001\u0003+AqAb'\u0001\t\u00131i*A\u000enCf\u0014WmQ1dQ\u0016$\u0015n]6CsR,7/\u00138NK6|'/\u001f\u000b\u000b\u000b\u00132yJb)\u0007&\u001a\u001d\u0006\u0002\u0003DQ\r3\u0003\r\u0001b8\u0002\u0013\tdwnY6J]\u001a|\u0007\u0002\u0003C)\r3\u0003\rA!1\t\u0011\u0011\u0005d\u0011\u0014a\u0001\tGB\u0001B\"+\u0007\u001a\u0002\u0007QqB\u0001\tI&\u001c8\u000eR1uC\"9aQ\u0016\u0001\u0005\n\u0019=\u0016\u0001H7bs\n,7)Y2iK\u0012K7o\u001b,bYV,7/\u00138NK6|'/_\u000b\u0005\rc39\f\u0006\u0006\u00074\u001aef1\u0018D_\r\u007f\u0003bA!\f\u0006$\u001aU\u0006\u0003\u0002C?\ro#\u0001\"\"\u000e\u0007,\n\u0007A1\u0011\u0005\t\rC3Y\u000b1\u0001\u0005`\"AA\u0011\u000bDV\u0001\u0004\u0011\t\r\u0003\u0005\u0005b\u0019-\u0006\u0019\u0001C2\u0011!1\tMb+A\u0002\u0019M\u0016\u0001\u00043jg.LE/\u001a:bi>\u0014\bb\u0002Dc\u0001\u0011%aqY\u0001\tO\u0016$\b+Z3sgR!!1\u0006De\u0011!1YMb1A\u0002\u0005U\u0011A\u00034pe\u000e,g)\u001a;dQ\"9aq\u001a\u0001\u0005\u0002\u0019E\u0017A\u0004:fa2L7-\u0019;f\u00052|7m\u001b\u000b\t\u0003S3\u0019N\"6\u0007^\"AA\u0011\u000bDg\u0001\u0004\u0011\t\r\u0003\u0005\u0007X\u001a5\u0007\u0019\u0001Dm\u0003A)\u00070[:uS:<'+\u001a9mS\u000e\f7\u000fE\u0003(\r7\fi*C\u0002\u0004.2BqAb8\u0007N\u0002\u0007Q/A\u0006nCb\u0014V\r\u001d7jG\u0006\u001c\bb\u0002Dr\u0001\u0011%aQ]\u0001\ne\u0016\u0004H.[2bi\u0016$B\"!+\u0007h\u001a%h1\u001eDw\rsD\u0001\u0002\"\u0015\u0007b\u0002\u0007!\u0011\u0019\u0005\t\t;2\t\u000f1\u0001\u0006\u0010!AA\u0011\rDq\u0001\u0004!\u0019\u0007\u0003\u0005\u0005l\u0019\u0005\b\u0019\u0001Dxa\u00111\tP\">\u0011\r\u0011EDq\u000fDz!\u0011!iH\">\u0005\u0019\u0019]hQ^A\u0001\u0002\u0003\u0015\t\u0001b!\u0003\u0007}#3\u0007\u0003\u0006\u0007X\u001a\u0005\b\u0013!a\u0001\r3DqA\"@\u0001\t\u00031y0A\u0005hKR\u001c\u0016N\\4mKV!q\u0011AD\u0005)\u00119\u0019a\"\u0005\u0015\t\u001d\u0015q1\u0002\t\u0006\u0019\u0011euq\u0001\t\u0005\t{:I\u0001\u0002\u0005\u00066\u0019m(\u0019\u0001CB\u0011)9iAb?\u0002\u0002\u0003\u000fqqB\u0001\u000bKZLG-\u001a8dK\u0012*\u0004C\u0002C9\to:9\u0001\u0003\u0005\u0005R\u0019m\b\u0019\u0001Ba\u0011\u001d9)\u0002\u0001C\u0001\u000f/\t\u0011\u0002];u'&tw\r\\3\u0016\t\u001deqQ\u0005\u000b\u000b\u000f799c\"\u000b\b.\u001d=B\u0003BA\u000b\u000f;A!bb\b\b\u0014\u0005\u0005\t9AD\u0011\u0003))g/\u001b3f]\u000e,GE\u000e\t\u0007\tc\"9hb\t\u0011\t\u0011utQ\u0005\u0003\t\u000bk9\u0019B1\u0001\u0005\u0004\"AA\u0011KD\n\u0001\u0004\u0011\t\r\u0003\u0005\b,\u001dM\u0001\u0019AD\u0012\u0003\u00151\u0018\r\\;f\u0011!!\tgb\u0005A\u0002\u0011\r\u0004BCCo\u000f'\u0001\n\u00111\u0001\u0002\u0016!Aq1\u0007\u0001\u0005B\t9)$\u0001\bee>\u0004hI]8n\u001b\u0016lwN]=\u0016\t\u001d]r1\t\u000b\u0007\u000fs9)eb\u0012\u0015\t\u0011\rt1\b\u0005\u000b\u000f{9\t$!AA\u0004\u001d}\u0012AC3wS\u0012,gnY3%oA1A\u0011\u000fC<\u000f\u0003\u0002B\u0001\" \bD\u0011AQQGD\u0019\u0005\u0004!\u0019\t\u0003\u0005\u0005R\u001dE\u0002\u0019\u0001Ba\u0011!!if\"\rA\u0002\u001d%\u0003#\u0002\u0007\u0006:\u001e-\u0003\u0003\u0003B\u0017\u000b;;i%b\u0013\u0011\u000b1\u0011ym\"\u0011\t\u000f\u001dE\u0003\u0001\"\u0001\bT\u0005I!/Z7pm\u0016\u0014F\r\u001a\u000b\u0004k\u001eU\u0003bBD,\u000f\u001f\u0002\r!^\u0001\u0006e\u0012$\u0017\n\u001a\u0005\b\u000f7\u0002A\u0011AD/\u0003=\u0011X-\\8wK\n\u0013x.\u00193dCN$H#B;\b`\u001d\r\u0004\u0002CD1\u000f3\u0002\r!a\u001f\u0002\u0017\t\u0014x.\u00193dCN$\u0018\n\u001a\u0005\t\u000b;<I\u00061\u0001\u0002\u0016!9qq\r\u0001\u0005\u0002\u001d%\u0014a\u0003:f[>4XM\u00117pG.$b!!+\bl\u001d5\u0004\u0002\u0003C)\u000fK\u0002\rA!1\t\u0015\u0015uwQ\rI\u0001\u0002\u0004\t)\u0002C\u0004\br\u0001!Iab\u001d\u0002'I,Wn\u001c<f\u00052|7m[%oi\u0016\u0014h.\u00197\u0015\r\u0005%vQOD<\u0011!!\tfb\u001cA\u0002\t\u0005\u0007\u0002CCo\u000f_\u0002\r!!\u0006\t\u000f\u001dm\u0004\u0001\"\u0003\b~\u0005\u0011\u0013\r\u001a3Va\u0012\fG/\u001a3CY>\u001c7n\u0015;biV\u001cHk\u001c+bg.lU\r\u001e:jGN$b!!+\b\u0000\u001d\u0005\u0005\u0002\u0003C)\u000fs\u0002\rA!1\t\u0011\u0011\u0005w\u0011\u0010a\u0001\t;Cqa\"\"\u0001\t\u000399)A\u000bsK2,\u0017m]3M_\u000e\\\u0017I\u001c3ESN\u0004xn]3\u0015\u0011\u0005%v\u0011RDF\u000f\u001bC\u0001\u0002\"\u0015\b\u0004\u0002\u0007!\u0011\u0019\u0005\t\t;:\u0019\t1\u0001\u0006\u0010!QQqPDB!\u0003\u0005\r!\"!\t\u000f\r5\b\u0001\"\u0001\u0004\u0016\"Iq1\u0013\u0001\u0012\u0002\u0013\u0005qQS\u0001\u0014aV$8+\u001b8hY\u0016$C-\u001a4bk2$H\u0005N\u000b\u0005\u000f/;Y*\u0006\u0002\b\u001a*\"\u0011QCB~\t!))d\"%C\u0002\u0011\r\u0005\"CDP\u0001E\u0005I\u0011ADQ\u0003I\u0001X\u000f\u001e\"zi\u0016\u001cH\u0005Z3gCVdG\u000f\n\u001b\u0016\t\u001d]u1\u0015\u0003\t\u000bk9iJ1\u0001\u0005\u0004\"Iqq\u0015\u0001\u0012\u0002\u0013\u0005q\u0011V\u0001\u0016e\u0016dW-Y:f\u0019>\u001c7\u000e\n3fM\u0006,H\u000e\u001e\u00133+\t9YK\u000b\u0003\u0006\u0002\u000em\b\"CDX\u0001E\u0005I\u0011BDY\u0003\u0001\"(/\u001f+p%\u0016\u0004xN\u001d;CY>\u001c7n\u0015;biV\u001cH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005\u001dM&\u0006BA>\u0007wD\u0011bb.\u0001#\u0003%Ia\"-\u00027I,\u0007o\u001c:u\u00052|7m[*uCR,8\u000f\n3fM\u0006,H\u000e\u001e\u00134\u0011%9Y\fAI\u0001\n\u000399*A\u000bsK6|g/\u001a\"m_\u000e\\G\u0005Z3gCVdG\u000f\n\u001a\t\u0013\u001d}\u0006!%A\u0005\u0002\u001d%\u0016a\b:fY\u0016\f7/\u001a'pG.\fe\u000e\u001a#jgB|7/\u001a\u0013eK\u001a\fW\u000f\u001c;%g!Iq1\u0019\u0001\u0012\u0002\u0013%qQY\u0001\u0018I>\u0004V\u000f^%uKJ\fGo\u001c:%I\u00164\u0017-\u001e7uIU*Bab&\bH\u0012AQQGDa\u0005\u0004!\u0019\tC\u0005\bL\u0002\t\n\u0011\"\u0003\bN\u00069Bm\u001c)vi&#XM]1u_J$C-\u001a4bk2$HEN\u000b\u0005\u000f/;y\r\u0002\u0005\u00066\u001d%'\u0019\u0001CB\u0011%9\u0019\u000eAI\u0001\n\u00039).A\u000bqkRLE/\u001a:bi>\u0014H\u0005Z3gCVdG\u000f\n\u001b\u0016\t\u001d]uq\u001b\u0003\t\u000bk9\tN1\u0001\u0005\u0004\"Iq1\u001c\u0001\u0012\u0002\u0013%qQ\\\u0001\u0015I>\u0004V\u000f\u001e\"zi\u0016\u001cH\u0005Z3gCVdG\u000fJ\u001b\u0016\t\u001d]uq\u001c\u0003\t\u000bk9IN1\u0001\u0005\u0004\"Iq1\u001d\u0001\u0012\u0002\u0013%qQ]\u0001\u0015I>\u0004V\u000f\u001e\"zi\u0016\u001cH\u0005Z3gCVdG\u000f\n\u001c\u0016\t\u001d]uq\u001d\u0003\t\u000bk9\tO1\u0001\u0005\u0004\"Iq1\u001e\u0001\u0012\u0002\u0013%qQ^\u0001\u0014e\u0016\u0004H.[2bi\u0016$C-\u001a4bk2$H%N\u000b\u0003\u000f_TCA\"7\u0004|\u0002"
)
public class BlockManager implements BlockDataManager, BlockEvictionHandler, Logging {
   private final String executorId;
   private final RpcEnv rpcEnv;
   private final BlockManagerMaster master;
   private final SerializerManager serializerManager;
   private final SparkConf conf;
   private final ShuffleManager shuffleManager;
   private final BlockTransferService blockTransferService;
   private final boolean externalShuffleServiceEnabled;
   private final DiskBlockManager diskBlockManager;
   private final BlockInfoManager blockInfoManager;
   private final ExecutionContextExecutorService org$apache$spark$storage$BlockManager$$futureExecutionContext;
   private final MemoryStore memoryStore;
   private final DiskStore diskStore;
   private final long maxOnHeapMemory;
   private final long maxOffHeapMemory;
   private final int org$apache$spark$storage$BlockManager$$externalShuffleServicePort;
   private BlockManagerId blockManagerId;
   private BlockManagerId shuffleServerId;
   private final ShuffleClient shuffleClient;
   private final int maxFailuresBeforeLocationRefresh;
   private final RpcEndpointRef slaveEndpoint;
   private Future<BoxedUnit> org$apache$spark$storage$BlockManager$$asyncReregisterTask;
   private final Object org$apache$spark$storage$BlockManager$$asyncReregisterLock;
   private volatile Seq<BlockManagerId> org$apache$spark$storage$BlockManager$$cachedPeers;
   private final Object peerFetchLock;
   private long lastPeerFetchTime;
   private BlockReplicationPolicy blockReplicationPolicy;
   private final RemoteBlockTempFileManager remoteBlockTempFileManager;
   private final long maxRemoteBlockToMem;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static BlockManagerMaster blockIdsToHosts$default$3() {
      return BlockManager$.MODULE$.blockIdsToHosts$default$3();
   }

   public static Map<BlockId, Seq<String>> blockIdsToHosts(BlockId[] var0, SparkEnv var1, BlockManagerMaster var2) {
      return BlockManager$.MODULE$.blockIdsToHosts(var0, var1, var2);
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

   public BlockManagerMaster master() {
      return this.master;
   }

   public SerializerManager serializerManager() {
      return this.serializerManager;
   }

   public SparkConf conf() {
      return this.conf;
   }

   public BlockTransferService blockTransferService() {
      return this.blockTransferService;
   }

   public boolean externalShuffleServiceEnabled() {
      return this.externalShuffleServiceEnabled;
   }

   public DiskBlockManager diskBlockManager() {
      return this.diskBlockManager;
   }

   public BlockInfoManager blockInfoManager() {
      return this.blockInfoManager;
   }

   public ExecutionContextExecutorService org$apache$spark$storage$BlockManager$$futureExecutionContext() {
      return this.org$apache$spark$storage$BlockManager$$futureExecutionContext;
   }

   public MemoryStore memoryStore() {
      return this.memoryStore;
   }

   public DiskStore diskStore() {
      return this.diskStore;
   }

   private long maxOnHeapMemory() {
      return this.maxOnHeapMemory;
   }

   private long maxOffHeapMemory() {
      return this.maxOffHeapMemory;
   }

   public int org$apache$spark$storage$BlockManager$$externalShuffleServicePort() {
      return this.org$apache$spark$storage$BlockManager$$externalShuffleServicePort;
   }

   public BlockManagerId blockManagerId() {
      return this.blockManagerId;
   }

   public void blockManagerId_$eq(BlockManagerId x$1) {
      this.blockManagerId = x$1;
   }

   public BlockManagerId shuffleServerId() {
      return this.shuffleServerId;
   }

   public void shuffleServerId_$eq(BlockManagerId x$1) {
      this.shuffleServerId = x$1;
   }

   public ShuffleClient shuffleClient() {
      return this.shuffleClient;
   }

   private int maxFailuresBeforeLocationRefresh() {
      return this.maxFailuresBeforeLocationRefresh;
   }

   private RpcEndpointRef slaveEndpoint() {
      return this.slaveEndpoint;
   }

   private Future<BoxedUnit> org$apache$spark$storage$BlockManager$$asyncReregisterTask() {
      return this.org$apache$spark$storage$BlockManager$$asyncReregisterTask;
   }

   public void org$apache$spark$storage$BlockManager$$asyncReregisterTask_$eq(Future<BoxedUnit> x$1) {
      this.org$apache$spark$storage$BlockManager$$asyncReregisterTask = x$1;
   }

   public Object org$apache$spark$storage$BlockManager$$asyncReregisterLock() {
      return this.org$apache$spark$storage$BlockManager$$asyncReregisterLock;
   }

   public Seq<BlockManagerId> org$apache$spark$storage$BlockManager$$cachedPeers() {
      return this.org$apache$spark$storage$BlockManager$$cachedPeers;
   }

   private void org$apache$spark$storage$BlockManager$$cachedPeers_$eq(Seq<BlockManagerId> x$1) {
      this.org$apache$spark$storage$BlockManager$$cachedPeers = x$1;
   }

   private Object peerFetchLock() {
      return this.peerFetchLock;
   }

   private long lastPeerFetchTime() {
      return this.lastPeerFetchTime;
   }

   private void lastPeerFetchTime_$eq(long x$1) {
      this.lastPeerFetchTime = x$1;
   }

   private BlockReplicationPolicy blockReplicationPolicy() {
      return this.blockReplicationPolicy;
   }

   private void blockReplicationPolicy_$eq(BlockReplicationPolicy x$1) {
      this.blockReplicationPolicy = x$1;
   }

   public RemoteBlockTempFileManager remoteBlockTempFileManager() {
      return this.remoteBlockTempFileManager;
   }

   private long maxRemoteBlockToMem() {
      return this.maxRemoteBlockToMem;
   }

   public void initialize(String appId) {
      this.blockTransferService().init(this);
      this.shuffleClient().init(appId);
      String priorityClass = this.conf().get("spark.storage.replication.policy", RandomBlockReplicationPolicy.class.getName());
      Class clazz = Utils$.MODULE$.classForName(priorityClass);
      BlockReplicationPolicy ret = (BlockReplicationPolicy)clazz.newInstance();
      this.logInfo(new Serializable(this, priorityClass) {
         public static final long serialVersionUID = 0L;
         private final String priorityClass$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using ", " for block replication policy"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.priorityClass$1}));
         }

         public {
            this.priorityClass$1 = priorityClass$1;
         }
      });
      this.blockReplicationPolicy_$eq(ret);
      BlockManagerId id = BlockManagerId$.MODULE$.apply(this.executorId, this.blockTransferService().hostName(), this.blockTransferService().port(), None$.MODULE$);
      BlockManagerId idFromMaster = this.master().registerBlockManager(id, this.maxOnHeapMemory(), this.maxOffHeapMemory(), this.slaveEndpoint());
      this.blockManagerId_$eq(idFromMaster == null ? id : idFromMaster);
      BlockManagerId var10001;
      if (this.externalShuffleServiceEnabled()) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BlockManager $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"external shuffle service port = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$storage$BlockManager$$externalShuffleServicePort())}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         var10001 = BlockManagerId$.MODULE$.apply(this.executorId, this.blockTransferService().hostName(), this.org$apache$spark$storage$BlockManager$$externalShuffleServicePort(), BlockManagerId$.MODULE$.apply$default$4());
      } else {
         var10001 = this.blockManagerId();
      }

      this.shuffleServerId_$eq(var10001);
      if (this.externalShuffleServiceEnabled() && !this.blockManagerId().isDriver()) {
         this.registerWithExternalShuffleServer();
      }

      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Initialized BlockManager: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockManagerId()}));
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

   public Source shuffleMetricsSource() {
      return this.externalShuffleServiceEnabled() ? new ShuffleMetricsSource("ExternalShuffle", this.shuffleClient().shuffleMetrics()) : new ShuffleMetricsSource("NettyBlockTransfer", this.shuffleClient().shuffleMetrics());
   }

   private void registerWithExternalShuffleServer() {
      Object var1 = new Object();

      try {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Registering executor with local external shuffle service.";
            }
         });
         ExecutorShuffleInfo shuffleConfig = new ExecutorShuffleInfo((String[])Predef$.MODULE$.refArrayOps((Object[])this.diskBlockManager().localDirs()).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(File x$1) {
               return x$1.toString();
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class))), this.diskBlockManager().subDirsPerLocalDir(), this.shuffleManager.getClass().getName());
         int MAX_ATTEMPTS = BoxesRunTime.unboxToInt(this.conf().get(package$.MODULE$.SHUFFLE_REGISTRATION_MAX_ATTEMPTS()));
         int SLEEP_TIME_SECS = 5;
         RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), MAX_ATTEMPTS).foreach$mVc$sp(new Serializable(this, shuffleConfig, MAX_ATTEMPTS, SLEEP_TIME_SECS, var1) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BlockManager $outer;
            private final ExecutorShuffleInfo shuffleConfig$1;
            public final int MAX_ATTEMPTS$1;
            public final int SLEEP_TIME_SECS$1;
            private final Object nonLocalReturnKey1$1;

            public final void apply(int i) {
               this.apply$mcVI$sp(i);
            }

            public void apply$mcVI$sp(int i) {
               try {
                  ((ExternalShuffleClient)this.$outer.shuffleClient()).registerWithShuffleServer(this.$outer.shuffleServerId().host(), this.$outer.shuffleServerId().port(), this.$outer.shuffleServerId().executorId(), this.shuffleConfig$1);
                  throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey1$1, BoxedUnit.UNIT);
               } catch (Throwable var9) {
                  if (var9 instanceof Exception) {
                     Exception var4 = (Exception)var9;
                     if (i < this.MAX_ATTEMPTS$1) {
                        this.$outer.logError(new Serializable(this, i) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final int i$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to connect to external shuffle server, will retry ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.MAX_ATTEMPTS$1 - this.i$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" more times after waiting ", " seconds..."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.SLEEP_TIME_SECS$1)}))).toString();
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.i$1 = i$1;
                              }
                           }
                        }, var4);
                        Thread.sleep((long)(this.SLEEP_TIME_SECS$1 * 1000));
                        BoxedUnit var5 = BoxedUnit.UNIT;
                        return;
                     }
                  }

                  Option var6 = NonFatal$.MODULE$.unapply(var9);
                  if (var6.isEmpty()) {
                     throw var9;
                  } else {
                     Throwable e = (Throwable)var6.get();
                     throw new SparkException((new StringBuilder()).append((Object)"Unable to register with external shuffle server due to : ").append((Object)e.getMessage()).toString(), e);
                  }
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.shuffleConfig$1 = shuffleConfig$1;
                  this.MAX_ATTEMPTS$1 = MAX_ATTEMPTS$1;
                  this.SLEEP_TIME_SECS$1 = SLEEP_TIME_SECS$1;
                  this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
               }
            }
         });
      } catch (NonLocalReturnControl var6) {
         if (var6.key() != var1) {
            throw var6;
         }

         var6.value$mcV$sp();
      }

   }

   private void reportAllBlocks() {
      Object var1 = new Object();

      try {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BlockManager $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Reporting ", " blocks to the master."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.blockInfoManager().size())}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.blockInfoManager().entries().withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Tuple2<BlockId, BlockInfo> check$ifrefutable$1) {
               boolean var3;
               if (check$ifrefutable$1 != null) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }
         }).foreach(new Serializable(this, var1) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BlockManager $outer;
            private final Object nonLocalReturnKey2$1;

            public final void apply(Tuple2<BlockId, BlockInfo> x$2) {
               if (x$2 != null) {
                  BlockId blockId = (BlockId)x$2._1();
                  BlockInfo info = (BlockInfo)x$2._2();
                  BlockStatus status = this.$outer.org$apache$spark$storage$BlockManager$$getCurrentBlockStatus(blockId, info);
                  if (info.tellMaster() && !this.$outer.org$apache$spark$storage$BlockManager$$tryToReportBlockStatus(blockId, status, this.$outer.org$apache$spark$storage$BlockManager$$tryToReportBlockStatus$default$3())) {
                     this.$outer.logError(new Serializable(this, blockId) {
                        public static final long serialVersionUID = 0L;
                        private final BlockId blockId$6;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to report ", " to master; giving up."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$6}));
                        }

                        public {
                           this.blockId$6 = blockId$6;
                        }
                     });
                     throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey2$1, BoxedUnit.UNIT);
                  } else {
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  }
               } else {
                  throw new MatchError(x$2);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.nonLocalReturnKey2$1 = nonLocalReturnKey2$1;
               }
            }
         });
      } catch (NonLocalReturnControl var3) {
         if (var3.key() != var1) {
            throw var3;
         }

         var3.value$mcV$sp();
      }

   }

   public void reregister() {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"BlockManager ", " re-registering with master"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockManagerId()}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.master().registerBlockManager(this.blockManagerId(), this.maxOnHeapMemory(), this.maxOffHeapMemory(), this.slaveEndpoint());
      this.reportAllBlocks();
   }

   private void asyncReregister() {
      synchronized(this.org$apache$spark$storage$BlockManager$$asyncReregisterLock()) {
         if (this.org$apache$spark$storage$BlockManager$$asyncReregisterTask() == null) {
            this.org$apache$spark$storage$BlockManager$$asyncReregisterTask_$eq(Future$.MODULE$.apply(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockManager $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.reregister();
                  synchronized(this.$outer.org$apache$spark$storage$BlockManager$$asyncReregisterLock()) {
                     this.$outer.org$apache$spark$storage$BlockManager$$asyncReregisterTask_$eq((Future)null);
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  }
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, this.org$apache$spark$storage$BlockManager$$futureExecutionContext()));
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            BoxedUnit var3 = BoxedUnit.UNIT;
         }

      }
   }

   public void waitForAsyncReregister() {
      Future task = this.org$apache$spark$storage$BlockManager$$asyncReregisterTask();
      if (task != null) {
         try {
            ThreadUtils$.MODULE$.awaitReady(task, Duration$.MODULE$.Inf());
         } catch (Throwable var7) {
            Option var4 = NonFatal$.MODULE$.unapply(var7);
            if (var4.isEmpty()) {
               throw var7;
            }

            Throwable t = (Throwable)var4.get();
            throw new Exception("Error occurred while waiting for async. reregistration", t);
         }
      }

   }

   public ManagedBuffer getBlockData(BlockId blockId) {
      Object var10000;
      if (blockId.isShuffle()) {
         var10000 = this.shuffleManager.shuffleBlockResolver().getBlockData((ShuffleBlockId)blockId);
      } else {
         Option var2 = this.getLocalBytes(blockId);
         if (!(var2 instanceof Some)) {
            if (None$.MODULE$.equals(var2)) {
               this.org$apache$spark$storage$BlockManager$$reportBlockStatus(blockId, BlockStatus$.MODULE$.empty(), this.org$apache$spark$storage$BlockManager$$reportBlockStatus$default$3());
               throw new BlockNotFoundException(blockId.toString());
            }

            throw new MatchError(var2);
         }

         Some var3 = (Some)var2;
         BlockData blockData = (BlockData)var3.x();
         BlockManagerManagedBuffer var5 = new BlockManagerManagedBuffer(this.blockInfoManager(), blockId, blockData, true);
         var10000 = var5;
      }

      return (ManagedBuffer)var10000;
   }

   public boolean putBlockData(BlockId blockId, ManagedBuffer data, StorageLevel level, ClassTag<?> classTag) {
      return this.putBytes(blockId, new ChunkedByteBuffer(data.nioByteBuffer()), level, this.putBytes$default$4(), classTag);
   }

   public Option<BlockStatus> getStatus(BlockId blockId) {
      return this.blockInfoManager().get(blockId).<BlockStatus>map(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;
         private final BlockId blockId$8;

         public final BlockStatus apply(BlockInfo info) {
            long memSize = this.$outer.memoryStore().contains(this.blockId$8) ? this.$outer.memoryStore().getSize(this.blockId$8) : 0L;
            long diskSize = this.$outer.diskStore().contains(this.blockId$8) ? this.$outer.diskStore().getSize(this.blockId$8) : 0L;
            return new BlockStatus(info.level(), memSize, diskSize);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$8 = blockId$8;
            }
         }
      });
   }

   public Seq<BlockId> getMatchingBlockIds(Function1<BlockId, Object> filter) {
      return Predef$.MODULE$.refArrayOps(this.blockInfoManager().entries().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final BlockId apply(Tuple2<BlockId, BlockInfo> x$3) {
            return (BlockId)x$3._1();
         }
      }).$plus$plus(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;

         public final Seq<BlockId> apply() {
            return this.$outer.diskBlockManager().getAllBlocks();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).filter(filter).toArray(ClassTag$.MODULE$.apply(BlockId.class))).toSeq();
   }

   public void org$apache$spark$storage$BlockManager$$reportBlockStatus(BlockId blockId, BlockStatus status, long droppedMemorySize) {
      boolean needReregister = !this.org$apache$spark$storage$BlockManager$$tryToReportBlockStatus(blockId, status, droppedMemorySize);
      if (needReregister) {
         this.logInfo(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$7;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Got told to re-register updating block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$7}));
            }

            public {
               this.blockId$7 = blockId$7;
            }
         });
         this.asyncReregister();
      }

      this.logDebug(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$7;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Told master about block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$7}));
         }

         public {
            this.blockId$7 = blockId$7;
         }
      });
   }

   public long org$apache$spark$storage$BlockManager$$reportBlockStatus$default$3() {
      return 0L;
   }

   public boolean org$apache$spark$storage$BlockManager$$tryToReportBlockStatus(BlockId blockId, BlockStatus status, long droppedMemorySize) {
      StorageLevel storageLevel = status.storageLevel();
      long inMemSize = Math.max(status.memSize(), droppedMemorySize);
      long onDiskSize = status.diskSize();
      return this.master().updateBlockInfo(this.blockManagerId(), blockId, storageLevel, inMemSize, onDiskSize);
   }

   public long org$apache$spark$storage$BlockManager$$tryToReportBlockStatus$default$3() {
      return 0L;
   }

   public BlockStatus org$apache$spark$storage$BlockManager$$getCurrentBlockStatus(BlockId blockId, BlockInfo info) {
      BlockStatus var4;
      synchronized(info) {
         StorageLevel var5 = info.level();
         BlockStatus var6;
         if (var5 == null) {
            var6 = BlockStatus$.MODULE$.empty();
         } else {
            boolean inMem = var5.useMemory() && this.memoryStore().contains(blockId);
            boolean onDisk = var5.useDisk() && this.diskStore().contains(blockId);
            boolean deserialized = inMem ? var5.deserialized() : false;
            int replication = !inMem && !onDisk ? 1 : var5.replication();
            StorageLevel storageLevel = StorageLevel$.MODULE$.apply(onDisk, inMem, var5.useOffHeap(), deserialized, replication);
            long memSize = inMem ? this.memoryStore().getSize(blockId) : 0L;
            long diskSize = onDisk ? this.diskStore().getSize(blockId) : 0L;
            var6 = new BlockStatus(storageLevel, memSize, diskSize);
         }

         var4 = var6;
      }

      return var4;
   }

   public Seq<BlockManagerId>[] org$apache$spark$storage$BlockManager$$getLocationBlockIds(BlockId[] blockIds) {
      long startTimeMs = System.currentTimeMillis();
      Seq[] locations = (Seq[])this.master().getLocations(blockIds).toArray(ClassTag$.MODULE$.apply(Seq.class));
      this.logDebug(new Serializable(this, startTimeMs) {
         public static final long serialVersionUID = 0L;
         private final long startTimeMs$1;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Got multiple block location in %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.getUsedTimeMs(this.startTimeMs$1)}));
         }

         public {
            this.startTimeMs$1 = startTimeMs$1;
         }
      });
      return locations;
   }

   private Nothing$ handleLocalReadFailure(BlockId blockId) {
      this.releaseLock(blockId, this.releaseLock$default$2());
      this.removeBlock(blockId, this.removeBlock$default$2());
      throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " was not found even though it's read-locked"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{blockId})));
   }

   public Option<BlockResult> getLocalValues(BlockId blockId) {
      this.logDebug(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$3;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Getting local block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$3}));
         }

         public {
            this.blockId$3 = blockId$3;
         }
      });
      Option var2 = this.blockInfoManager().lockForReading(blockId, this.blockInfoManager().lockForReading$default$2());
      Object var3;
      if (None$.MODULE$.equals(var2)) {
         this.logDebug(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$3;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " was not found"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$3}));
            }

            public {
               this.blockId$3 = blockId$3;
            }
         });
         var3 = None$.MODULE$;
      } else {
         if (!(var2 instanceof Some)) {
            throw new MatchError(var2);
         }

         Some var4 = (Some)var2;
         BlockInfo info = (BlockInfo)var4.x();
         StorageLevel level = info.level();
         this.logDebug(new Serializable(this, blockId, level) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$3;
            private final StorageLevel level$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Level for block ", " is ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$3, this.level$1}));
            }

            public {
               this.blockId$3 = blockId$3;
               this.level$1 = level$1;
            }
         });
         Option taskAttemptId = Option$.MODULE$.apply(TaskContext$.MODULE$.get()).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(TaskContext x$4) {
               return x$4.taskAttemptId();
            }
         });
         Some var17;
         if (level.useMemory() && this.memoryStore().contains(blockId)) {
            Iterator var18;
            if (level.deserialized()) {
               var18 = (Iterator)this.memoryStore().getValues(blockId).get();
            } else {
               SerializerManager var19 = this.serializerManager();
               ChunkedByteBuffer qual$1 = (ChunkedByteBuffer)this.memoryStore().getBytes(blockId).get();
               boolean x$17 = qual$1.toInputStream$default$1();
               var18 = var19.dataDeserializeStream(blockId, qual$1.toInputStream(x$17), info.classTag());
            }

            Iterator iter = var18;
            CompletionIterator ci = CompletionIterator$.MODULE$.apply(iter, new Serializable(this, blockId, taskAttemptId) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockManager $outer;
               private final BlockId blockId$3;
               private final Option taskAttemptId$1;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.releaseLock(this.blockId$3, this.taskAttemptId$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$3 = blockId$3;
                     this.taskAttemptId$1 = taskAttemptId$1;
                  }
               }
            });
            var17 = new Some(new BlockResult(ci, DataReadMethod$.MODULE$.Memory(), info.size()));
         } else {
            if (!level.useDisk() || !this.diskStore().contains(blockId)) {
               throw this.handleLocalReadFailure(blockId);
            }

            BlockData diskData = this.diskStore().getBytes(blockId);
            Iterator var10000;
            if (level.deserialized()) {
               Iterator diskValues = this.serializerManager().dataDeserializeStream(blockId, diskData.toInputStream(), info.classTag());
               var10000 = this.maybeCacheDiskValuesInMemory(info, blockId, level, diskValues);
            } else {
               InputStream stream = (InputStream)this.maybeCacheDiskBytesInMemory(info, blockId, level, diskData).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final InputStream apply(ChunkedByteBuffer x$5) {
                     return x$5.toInputStream(false);
                  }
               }).getOrElse(new Serializable(this, diskData) {
                  public static final long serialVersionUID = 0L;
                  private final BlockData diskData$1;

                  public final InputStream apply() {
                     return this.diskData$1.toInputStream();
                  }

                  public {
                     this.diskData$1 = diskData$1;
                  }
               });
               var10000 = this.serializerManager().dataDeserializeStream(blockId, stream, info.classTag());
            }

            Iterator iterToReturn = var10000;
            CompletionIterator ci = CompletionIterator$.MODULE$.apply(iterToReturn, new Serializable(this, blockId, taskAttemptId, diskData) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockManager $outer;
               private final BlockId blockId$3;
               private final Option taskAttemptId$1;
               private final BlockData diskData$1;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.releaseLockAndDispose(this.blockId$3, this.diskData$1, this.taskAttemptId$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$3 = blockId$3;
                     this.taskAttemptId$1 = taskAttemptId$1;
                     this.diskData$1 = diskData$1;
                  }
               }
            });
            var17 = new Some(new BlockResult(ci, DataReadMethod$.MODULE$.Disk(), info.size()));
         }

         var3 = var17;
      }

      return (Option<BlockResult>)var3;
   }

   public Option<BlockData> getLocalBytes(BlockId blockId) {
      this.logDebug(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Getting local block ", " as bytes"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$1}));
         }

         public {
            this.blockId$1 = blockId$1;
         }
      });
      Object var10000;
      if (blockId.isShuffle()) {
         ShuffleBlockResolver shuffleBlockResolver = this.shuffleManager.shuffleBlockResolver();
         ChunkedByteBuffer buf = new ChunkedByteBuffer(shuffleBlockResolver.getBlockData((ShuffleBlockId)blockId).nioByteBuffer());
         var10000 = new Some(new ByteBufferBlockData(buf, true));
      } else {
         var10000 = this.blockInfoManager().lockForReading(blockId, this.blockInfoManager().lockForReading$default$2()).map(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BlockManager $outer;
            private final BlockId blockId$1;

            public final BlockData apply(BlockInfo info) {
               return this.$outer.org$apache$spark$storage$BlockManager$$doGetLocalBytes(this.blockId$1, info);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.blockId$1 = blockId$1;
               }
            }
         });
      }

      return (Option<BlockData>)var10000;
   }

   public BlockData org$apache$spark$storage$BlockManager$$doGetLocalBytes(BlockId blockId, BlockInfo info) {
      StorageLevel level = info.level();
      this.logDebug(new Serializable(this, blockId, level) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$10;
         private final StorageLevel level$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Level for block ", " is ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$10, this.level$2}));
         }

         public {
            this.blockId$10 = blockId$10;
            this.level$2 = level$2;
         }
      });
      Object var10000;
      if (level.deserialized()) {
         if (level.useDisk() && this.diskStore().contains(blockId)) {
            var10000 = this.diskStore().getBytes(blockId);
         } else {
            if (!level.useMemory() || !this.memoryStore().contains(blockId)) {
               throw this.handleLocalReadFailure(blockId);
            }

            var10000 = new ByteBufferBlockData(this.serializerManager().dataSerializeWithExplicitClassTag(blockId, (Iterator)this.memoryStore().getValues(blockId).get(), info.classTag()), true);
         }
      } else if (level.useMemory() && this.memoryStore().contains(blockId)) {
         var10000 = new ByteBufferBlockData((ChunkedByteBuffer)this.memoryStore().getBytes(blockId).get(), false);
      } else {
         if (!level.useDisk() || !this.diskStore().contains(blockId)) {
            throw this.handleLocalReadFailure(blockId);
         }

         BlockData diskData = this.diskStore().getBytes(blockId);
         var10000 = (BlockData)this.maybeCacheDiskBytesInMemory(info, blockId, level, diskData).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final ByteBufferBlockData apply(ChunkedByteBuffer x$6) {
               return new ByteBufferBlockData(x$6, false);
            }
         }).getOrElse(new Serializable(this, diskData) {
            public static final long serialVersionUID = 0L;
            private final BlockData diskData$3;

            public final BlockData apply() {
               return this.diskData$3;
            }

            public {
               this.diskData$3 = diskData$3;
            }
         });
      }

      return (BlockData)var10000;
   }

   private <T> Option<BlockResult> getRemoteValues(BlockId blockId, ClassTag<T> evidence$1) {
      ClassTag ct = (ClassTag)Predef$.MODULE$.implicitly(evidence$1);
      return this.getRemoteBytes(blockId).<BlockResult>map(new Serializable(this, blockId, ct) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;
         private final BlockId blockId$11;
         private final ClassTag ct$1;

         public final BlockResult apply(ChunkedByteBuffer data) {
            Iterator values = this.$outer.serializerManager().dataDeserializeStream(this.blockId$11, data.toInputStream(true), this.ct$1);
            return new BlockResult(values, DataReadMethod$.MODULE$.Network(), data.size());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$11 = blockId$11;
               this.ct$1 = ct$1;
            }
         }
      });
   }

   private Seq<BlockManagerId> sortLocations(Seq<BlockManagerId> locations) {
      Seq locs = (Seq)Random$.MODULE$.shuffle(locations, Seq$.MODULE$.canBuildFrom());
      Tuple2 var4 = locs.partition(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;

         public final boolean apply(BlockManagerId loc) {
            boolean var3;
            label23: {
               String var10000 = this.$outer.blockManagerId().host();
               String var2 = loc.host();
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
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      if (var4 != null) {
         Seq preferredLocs = (Seq)var4._1();
         Seq otherLocs = (Seq)var4._2();
         Tuple2 var7 = new Tuple2(preferredLocs, otherLocs);
         Seq preferredLocs = (Seq)var7._1();
         Seq otherLocs = (Seq)var7._2();
         Option var10 = this.blockManagerId().topologyInfo();
         Seq var11;
         if (None$.MODULE$.equals(var10)) {
            var11 = (Seq)preferredLocs.$plus$plus(otherLocs, Seq$.MODULE$.canBuildFrom());
         } else {
            if (!(var10 instanceof Some)) {
               throw new MatchError(var10);
            }

            Tuple2 var13 = otherLocs.partition(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockManager $outer;

               public final boolean apply(BlockManagerId loc) {
                  boolean var3;
                  label23: {
                     Option var10000 = this.$outer.blockManagerId().topologyInfo();
                     Option var2 = loc.topologyInfo();
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
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            if (var13 == null) {
               throw new MatchError(var13);
            }

            Seq sameRackLocs = (Seq)var13._1();
            Seq differentRackLocs = (Seq)var13._2();
            Tuple2 var16 = new Tuple2(sameRackLocs, differentRackLocs);
            Seq sameRackLocs = (Seq)var16._1();
            Seq differentRackLocs = (Seq)var16._2();
            var11 = (Seq)((TraversableLike)preferredLocs.$plus$plus(sameRackLocs, Seq$.MODULE$.canBuildFrom())).$plus$plus(differentRackLocs, Seq$.MODULE$.canBuildFrom());
         }

         return var11;
      } else {
         throw new MatchError(var4);
      }
   }

   public Option<ChunkedByteBuffer> getRemoteBytes(BlockId blockId) {
      this.logDebug(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Getting remote block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$2}));
         }

         public {
            this.blockId$2 = blockId$2;
         }
      });
      Predef$.MODULE$.require(blockId != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "BlockId is null";
         }
      });
      IntRef runningFailureCount = IntRef.create(0);
      IntRef totalFailureCount = IntRef.create(0);
      Option locationsAndStatus = this.master().getLocationsAndStatus(blockId);
      long blockSize = BoxesRunTime.unboxToLong(locationsAndStatus.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(BlockManagerMessages.BlockLocationsAndStatus b) {
            return RichLong$.MODULE$.max$extension(Predef$.MODULE$.longWrapper(b.status().diskSize()), b.status().memSize());
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }));
      Seq blockLocations = (Seq)locationsAndStatus.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<BlockManagerId> apply(BlockManagerMessages.BlockLocationsAndStatus x$9) {
            return x$9.locations();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      });
      RemoteBlockTempFileManager tempFileManager = blockSize > this.maxRemoteBlockToMem() ? this.remoteBlockTempFileManager() : null;
      Seq locations = this.sortLocations(blockLocations);
      int maxFetchFailures = locations.size();
      Iterator locationIterator = locations.iterator();

      while(locationIterator.hasNext()) {
         BlockManagerId loc = (BlockManagerId)locationIterator.next();
         this.logDebug(new Serializable(this, blockId, loc) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$2;
            private final BlockManagerId loc$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Getting remote block ", " from ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$2, this.loc$1}));
            }

            public {
               this.blockId$2 = blockId$2;
               this.loc$1 = loc$1;
            }
         });

         ByteBuffer var10000;
         try {
            var10000 = this.blockTransferService().fetchBlockSync(loc.host(), loc.port(), loc.executorId(), blockId.toString(), tempFileManager).nioByteBuffer();
         } catch (Throwable var20) {
            label102: {
               Option var16 = NonFatal$.MODULE$.unapply(var20);
               if (var16.isEmpty()) {
                  throw var20;
               }

               Throwable e = (Throwable)var16.get();
               ++runningFailureCount.elem;
               ++totalFailureCount.elem;
               if (totalFailureCount.elem >= maxFetchFailures) {
                  this.logWarning(new Serializable(this, totalFailureCount) {
                     public static final long serialVersionUID = 0L;
                     private final IntRef totalFailureCount$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to fetch block after ", " fetch failures. "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.totalFailureCount$1.elem)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Most recent failure cause:"})))).s(Nil$.MODULE$)).toString();
                     }

                     public {
                        this.totalFailureCount$1 = totalFailureCount$1;
                     }
                  }, e);
                  return None$.MODULE$;
               }

               this.logWarning(new Serializable(this, blockId, runningFailureCount, loc) {
                  public static final long serialVersionUID = 0L;
                  private final BlockId blockId$2;
                  private final IntRef runningFailureCount$1;
                  private final BlockManagerId loc$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to fetch remote block ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$2}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"from ", " (failed attempt ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.loc$1, BoxesRunTime.boxToInteger(this.runningFailureCount$1.elem)}))).toString();
                  }

                  public {
                     this.blockId$2 = blockId$2;
                     this.runningFailureCount$1 = runningFailureCount$1;
                     this.loc$1 = loc$1;
                  }
               }, e);
               if (runningFailureCount.elem >= this.maxFailuresBeforeLocationRefresh()) {
                  locationIterator = this.sortLocations(this.master().getLocations(blockId)).iterator();
                  this.logDebug(new Serializable(this, runningFailureCount) {
                     public static final long serialVersionUID = 0L;
                     private final IntRef runningFailureCount$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Refreshed locations from the driver "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"after ", " fetch failures."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.runningFailureCount$1.elem)}))).toString();
                     }

                     public {
                        this.runningFailureCount$1 = runningFailureCount$1;
                     }
                  });
                  runningFailureCount.elem = 0;
               }

               Object var18 = null;
               var10000 = (ByteBuffer)var18;
               break label102;
            }
         }

         ByteBuffer data = var10000;
         if (data != null) {
            return new Some<ChunkedByteBuffer>(new ChunkedByteBuffer(data));
         }

         this.logDebug(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$2;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The value of block ", " is null"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$2}));
            }

            public {
               this.blockId$2 = blockId$2;
            }
         });
      }

      this.logDebug(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " not found"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$2}));
         }

         public {
            this.blockId$2 = blockId$2;
         }
      });
      return None$.MODULE$;
   }

   public <T> Option<BlockResult> get(BlockId blockId, ClassTag<T> evidence$2) {
      Option local = this.getLocalValues(blockId);
      if (local.isDefined()) {
         this.logInfo(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$4;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Found block ", " locally"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$4}));
            }

            public {
               this.blockId$4 = blockId$4;
            }
         });
         return local;
      } else {
         Option remote = this.getRemoteValues(blockId, evidence$2);
         if (remote.isDefined()) {
            this.logInfo(new Serializable(this, blockId) {
               public static final long serialVersionUID = 0L;
               private final BlockId blockId$4;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Found block ", " remotely"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$4}));
               }

               public {
                  this.blockId$4 = blockId$4;
               }
            });
            return remote;
         } else {
            return None$.MODULE$;
         }
      }
   }

   public void downgradeLock(BlockId blockId) {
      this.blockInfoManager().downgradeLock(blockId);
   }

   public void releaseLock(BlockId blockId, Option<Object> taskAttemptId) {
      this.blockInfoManager().unlock(blockId, taskAttemptId);
   }

   public Option<Object> releaseLock$default$2() {
      return None$.MODULE$;
   }

   public void registerTask(long taskAttemptId) {
      this.blockInfoManager().registerTask(taskAttemptId);
   }

   public Seq<BlockId> releaseAllLocksForTask(long taskAttemptId) {
      return this.blockInfoManager().releaseAllLocksForTask(taskAttemptId);
   }

   public <T> Either<BlockResult, Iterator<T>> getOrElseUpdate(BlockId blockId, StorageLevel level, ClassTag<T> classTag, Function0<Iterator<T>> makeIterator) {
      Option var5 = this.get(blockId, classTag);
      if (var5 instanceof Some) {
         Some var6 = (Some)var5;
         BlockResult block = (BlockResult)var6.x();
         return scala.package$.MODULE$.Left().<BlockResult, Iterator<T>>apply(block);
      } else {
         BoxedUnit var8 = BoxedUnit.UNIT;
         boolean x$22 = true;
         boolean x$23 = this.doPutIterator$default$5();
         Option var9 = this.doPutIterator(blockId, makeIterator, level, classTag, x$23, x$22);
         Object var16;
         if (None$.MODULE$.equals(var9)) {
            BlockResult blockResult = (BlockResult)this.getLocalValues(blockId).getOrElse(new Serializable(this, blockId) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockManager $outer;
               private final BlockId blockId$5;

               public final Nothing$ apply() {
                  this.$outer.releaseLock(this.blockId$5, this.$outer.releaseLock$default$2());
                  throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"get() failed for block ", " even though we held a lock"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$5})));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$5 = blockId$5;
                  }
               }
            });
            this.releaseLock(blockId, this.releaseLock$default$2());
            var16 = scala.package$.MODULE$.Left().apply(blockResult);
         } else {
            if (!(var9 instanceof Some)) {
               throw new MatchError(var9);
            }

            Some var18 = (Some)var9;
            PartiallyUnrolledIterator iter = (PartiallyUnrolledIterator)var18.x();
            var16 = scala.package$.MODULE$.Right().apply(iter);
         }

         return (Either<BlockResult, Iterator<T>>)var16;
      }
   }

   public <T> boolean putIterator(BlockId blockId, Iterator<T> values, StorageLevel level, boolean tellMaster, ClassTag<T> evidence$3) {
      Predef$.MODULE$.require(values != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Values is null";
         }
      });
      Option var6 = this.doPutIterator(blockId, new Serializable(this, values) {
         public static final long serialVersionUID = 0L;
         private final Iterator values$1;

         public final Iterator<T> apply() {
            return this.values$1;
         }

         public {
            this.values$1 = values$1;
         }
      }, level, (ClassTag)Predef$.MODULE$.implicitly(evidence$3), tellMaster, this.doPutIterator$default$6());
      boolean var7;
      if (None$.MODULE$.equals(var6)) {
         var7 = true;
      } else {
         if (!(var6 instanceof Some)) {
            throw new MatchError(var6);
         }

         Some var8 = (Some)var6;
         PartiallyUnrolledIterator iter = (PartiallyUnrolledIterator)var8.x();
         iter.close();
         var7 = false;
      }

      return var7;
   }

   public <T> boolean putIterator$default$4() {
      return true;
   }

   public DiskBlockObjectWriter getDiskWriter(BlockId blockId, File file, SerializerInstance serializerInstance, int bufferSize, ShuffleWriteMetrics writeMetrics) {
      boolean syncWrites = this.conf().getBoolean("spark.shuffle.sync", false);
      return new DiskBlockObjectWriter(file, this.serializerManager(), serializerInstance, bufferSize, syncWrites, writeMetrics, blockId);
   }

   public <T> boolean putBytes(BlockId blockId, ChunkedByteBuffer bytes, StorageLevel level, boolean tellMaster, ClassTag<T> evidence$4) {
      Predef$.MODULE$.require(bytes != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Bytes is null";
         }
      });
      return this.doPutBytes(blockId, bytes, level, (ClassTag)Predef$.MODULE$.implicitly(evidence$4), tellMaster, this.doPutBytes$default$6());
   }

   public <T> boolean putBytes$default$4() {
      return true;
   }

   private <T> boolean doPutBytes(BlockId blockId, ChunkedByteBuffer bytes, StorageLevel level, ClassTag<T> classTag, boolean tellMaster, boolean keepReadLock) {
      return this.doPut(blockId, level, classTag, tellMaster, keepReadLock, new Serializable(this, blockId, bytes, level, classTag, tellMaster) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;
         public final BlockId blockId$13;
         public final ChunkedByteBuffer bytes$1;
         public final StorageLevel level$4;
         public final ClassTag classTag$2;
         private final boolean tellMaster$2;

         public final Option<ChunkedByteBuffer> apply(BlockInfo info) {
            long startTimeMs = System.currentTimeMillis();
            Future replicationFuture = this.level$4.replication() > 1 ? Future$.MODULE$.apply(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.org$apache$spark$storage$BlockManager$$anonfun$$$outer().org$apache$spark$storage$BlockManager$$replicate(this.$outer.blockId$13, new ByteBufferBlockData(this.$outer.bytes$1, false), this.$outer.level$4, this.$outer.classTag$2, this.$outer.org$apache$spark$storage$BlockManager$$anonfun$$$outer().org$apache$spark$storage$BlockManager$$replicate$default$5());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, this.$outer.org$apache$spark$storage$BlockManager$$futureExecutionContext()) : null;
            long size = this.bytes$1.size();
            if (this.level$4.useMemory()) {
               boolean var10000;
               if (this.level$4.deserialized()) {
                  Iterator values = this.$outer.serializerManager().dataDeserializeStream(this.blockId$13, this.bytes$1.toInputStream(this.bytes$1.toInputStream$default$1()), this.classTag$2);
                  Either var9 = this.$outer.memoryStore().putIteratorAsValues(this.blockId$13, values, this.classTag$2);
                  boolean var10;
                  if (var9 instanceof Right) {
                     var10 = true;
                  } else {
                     if (!(var9 instanceof Left)) {
                        throw new MatchError(var9);
                     }

                     Left var11 = (Left)var9;
                     PartiallyUnrolledIterator iter = (PartiallyUnrolledIterator)var11.a();
                     iter.close();
                     var10 = false;
                  }

                  var10000 = var10;
               } else {
                  MemoryMode memoryMode = this.level$4.memoryMode();
                  var10000 = this.$outer.memoryStore().putBytes(this.blockId$13, size, memoryMode, new Serializable(this, memoryMode) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final MemoryMode memoryMode$1;

                     public final ChunkedByteBuffer apply() {
                        ChunkedByteBuffer var2;
                        label18: {
                           MemoryMode var10000 = this.memoryMode$1;
                           MemoryMode var1 = MemoryMode.OFF_HEAP;
                           if (var10000 == null) {
                              if (var1 != null) {
                                 break label18;
                              }
                           } else if (!var10000.equals(var1)) {
                              break label18;
                           }

                           if (Predef$.MODULE$.refArrayOps((Object[])this.$outer.bytes$1.chunks()).exists(new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final boolean apply(ByteBuffer buffer) {
                                 return !buffer.isDirect();
                              }
                           })) {
                              var2 = this.$outer.bytes$1.copy(new Serializable(this) {
                                 public static final long serialVersionUID = 0L;

                                 public final ByteBuffer apply(int x$1) {
                                    return Platform.allocateDirectBuffer(x$1);
                                 }
                              });
                              return var2;
                           }
                        }

                        var2 = this.$outer.bytes$1;
                        return var2;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.memoryMode$1 = memoryMode$1;
                        }
                     }
                  }, ClassTag$.MODULE$.Nothing());
               }

               boolean putSucceeded = var10000;
               if (!putSucceeded && this.level$4.useDisk()) {
                  this.$outer.logWarning(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Persisting block ", " to disk instead."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockId$13}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  this.$outer.diskStore().putBytes(this.blockId$13, this.bytes$1);
               }
            } else if (this.level$4.useDisk()) {
               this.$outer.diskStore().putBytes(this.blockId$13, this.bytes$1);
            }

            BlockStatus putBlockStatus = this.$outer.org$apache$spark$storage$BlockManager$$getCurrentBlockStatus(this.blockId$13, info);
            boolean blockWasSuccessfullyStored = putBlockStatus.storageLevel().isValid();
            if (blockWasSuccessfullyStored) {
               info.size_$eq(size);
               if (this.tellMaster$2 && info.tellMaster()) {
                  this.$outer.org$apache$spark$storage$BlockManager$$reportBlockStatus(this.blockId$13, putBlockStatus, this.$outer.org$apache$spark$storage$BlockManager$$reportBlockStatus$default$3());
               }

               this.$outer.org$apache$spark$storage$BlockManager$$addUpdatedBlockStatusToTaskMetrics(this.blockId$13, putBlockStatus);
            }

            this.$outer.logDebug(new Serializable(this, startTimeMs) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final long startTimeMs$2;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Put block %s locally took %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockId$13, Utils$.MODULE$.getUsedTimeMs(this.startTimeMs$2)}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.startTimeMs$2 = startTimeMs$2;
                  }
               }
            });
            if (this.level$4.replication() > 1) {
               try {
                  ThreadUtils$.MODULE$.awaitReady(replicationFuture, Duration$.MODULE$.Inf());
               } catch (Throwable var21) {
                  Option var18 = NonFatal$.MODULE$.unapply(var21);
                  if (var18.isEmpty()) {
                     throw var21;
                  }

                  Throwable t = (Throwable)var18.get();
                  throw new Exception("Error occurred while waiting for replication to finish", t);
               }
            } else {
               BoxedUnit var22 = BoxedUnit.UNIT;
            }

            return (Option<ChunkedByteBuffer>)(blockWasSuccessfullyStored ? None$.MODULE$ : new Some(this.bytes$1));
         }

         // $FF: synthetic method
         public BlockManager org$apache$spark$storage$BlockManager$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$13 = blockId$13;
               this.bytes$1 = bytes$1;
               this.level$4 = level$4;
               this.classTag$2 = classTag$2;
               this.tellMaster$2 = tellMaster$2;
            }
         }
      }).isEmpty();
   }

   private <T> Option<T> doPut(BlockId blockId, StorageLevel level, ClassTag<?> classTag, boolean tellMaster, boolean keepReadLock, Function1<BlockInfo, Option<T>> putBody) {
      Predef$.MODULE$.require(blockId != null, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "BlockId is null";
         }
      });
      Predef$.MODULE$.require(level != null && level.isValid(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "StorageLevel is null or invalid";
         }
      });
      BlockInfo newInfo = new BlockInfo(level, classTag, tellMaster);
      if (this.blockInfoManager().lockNewBlockForWriting(blockId, newInfo)) {
         BlockInfo putBlockInfo = newInfo;
         long startTimeMs = System.currentTimeMillis();
         boolean exceptionWasThrown = true;

         Option var10000;
         try {
            try {
               Option res = (Option)putBody.apply(putBlockInfo);
               exceptionWasThrown = false;
               if (res.isEmpty()) {
                  if (keepReadLock) {
                     this.blockInfoManager().downgradeLock(blockId);
                  } else {
                     this.blockInfoManager().unlock(blockId, this.blockInfoManager().unlock$default$2());
                  }
               } else {
                  this.removeBlockInternal(blockId, false);
                  this.logWarning(new Serializable(this, blockId) {
                     public static final long serialVersionUID = 0L;
                     private final BlockId blockId$14;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Putting block ", " failed"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$14}));
                     }

                     public {
                        this.blockId$14 = blockId$14;
                     }
                  });
               }

               var10000 = res;
            } catch (Throwable var24) {
               Option var16 = NonFatal$.MODULE$.unapply(var24);
               if (var16.isEmpty()) {
                  throw var24;
               }

               Throwable e = (Throwable)var16.get();
               this.logWarning(new Serializable(this, blockId, e) {
                  public static final long serialVersionUID = 0L;
                  private final BlockId blockId$14;
                  private final Throwable e$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Putting block ", " failed due to exception ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$14, this.e$1}));
                  }

                  public {
                     this.blockId$14 = blockId$14;
                     this.e$1 = e$1;
                  }
               });
               throw e;
            }
         } finally {
            if (exceptionWasThrown) {
               this.removeBlockInternal(blockId, tellMaster);
               this.org$apache$spark$storage$BlockManager$$addUpdatedBlockStatusToTaskMetrics(blockId, BlockStatus$.MODULE$.empty());
            }

         }

         Option result = var10000;
         if (level.replication() > 1) {
            this.logDebug(new Serializable(this, blockId, startTimeMs) {
               public static final long serialVersionUID = 0L;
               private final BlockId blockId$14;
               private final long startTimeMs$3;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Putting block %s with replication took %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$14, Utils$.MODULE$.getUsedTimeMs(this.startTimeMs$3)}));
               }

               public {
                  this.blockId$14 = blockId$14;
                  this.startTimeMs$3 = startTimeMs$3;
               }
            });
         } else {
            this.logDebug(new Serializable(this, blockId, startTimeMs) {
               public static final long serialVersionUID = 0L;
               private final BlockId blockId$14;
               private final long startTimeMs$3;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Putting block %s without replication took %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$14, Utils$.MODULE$.getUsedTimeMs(this.startTimeMs$3)}));
               }

               public {
                  this.blockId$14 = blockId$14;
                  this.startTimeMs$3 = startTimeMs$3;
               }
            });
         }

         return result;
      } else {
         this.logWarning(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$14;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " already exists on this machine; not re-adding it"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$14}));
            }

            public {
               this.blockId$14 = blockId$14;
            }
         });
         if (!keepReadLock) {
            this.releaseLock(blockId, this.releaseLock$default$2());
         }

         return None$.MODULE$;
      }
   }

   private <T> boolean doPutBytes$default$5() {
      return true;
   }

   private <T> boolean doPutBytes$default$6() {
      return false;
   }

   private <T> Option<PartiallyUnrolledIterator<T>> doPutIterator(BlockId blockId, Function0<Iterator<T>> iterator, StorageLevel level, ClassTag<T> classTag, boolean tellMaster, boolean keepReadLock) {
      return this.<PartiallyUnrolledIterator<T>>doPut(blockId, level, classTag, tellMaster, keepReadLock, new Serializable(this, blockId, iterator, level, classTag, tellMaster) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;
         public final BlockId blockId$12;
         public final Function0 iterator$1;
         private final StorageLevel level$3;
         public final ClassTag classTag$1;
         private final boolean tellMaster$1;

         public final Option<PartiallyUnrolledIterator<T>> apply(BlockInfo info) {
            long startTimeMs = System.currentTimeMillis();
            Option iteratorFromFailedMemoryStorePut = None$.MODULE$;
            long size = 0L;
            if (this.level$3.useMemory()) {
               if (this.level$3.deserialized()) {
                  Either var7 = this.$outer.memoryStore().putIteratorAsValues(this.blockId$12, (Iterator)this.iterator$1.apply(), this.classTag$1);
                  if (var7 instanceof Right) {
                     Right var8 = (Right)var7;
                     long s = BoxesRunTime.unboxToLong(var8.b());
                     size = s;
                     BoxedUnit var11 = BoxedUnit.UNIT;
                  } else {
                     if (!(var7 instanceof Left)) {
                        throw new MatchError(var7);
                     }

                     Left var12 = (Left)var7;
                     PartiallyUnrolledIterator iter = (PartiallyUnrolledIterator)var12.a();
                     if (this.level$3.useDisk()) {
                        this.$outer.logWarning(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Persisting block ", " to disk instead."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockId$12}));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                        this.$outer.diskStore().put(this.blockId$12, new Serializable(this, iter) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final PartiallyUnrolledIterator iter$1;

                           public final void apply(WritableByteChannel channel) {
                              OutputStream out = Channels.newOutputStream(channel);
                              this.$outer.org$apache$spark$storage$BlockManager$$anonfun$$$outer().serializerManager().dataSerializeStream(this.$outer.blockId$12, out, this.iter$1, this.$outer.classTag$1);
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.iter$1 = iter$1;
                              }
                           }
                        });
                        size = this.$outer.diskStore().getSize(this.blockId$12);
                        BoxedUnit var10000 = BoxedUnit.UNIT;
                     } else {
                        iteratorFromFailedMemoryStorePut = new Some(iter);
                        BoxedUnit var30 = BoxedUnit.UNIT;
                     }
                  }
               } else {
                  Either var14 = this.$outer.memoryStore().putIteratorAsBytes(this.blockId$12, (Iterator)this.iterator$1.apply(), this.classTag$1, this.level$3.memoryMode());
                  if (var14 instanceof Right) {
                     Right var15 = (Right)var14;
                     long s = BoxesRunTime.unboxToLong(var15.b());
                     size = s;
                     BoxedUnit var18 = BoxedUnit.UNIT;
                  } else {
                     if (!(var14 instanceof Left)) {
                        throw new MatchError(var14);
                     }

                     Left var19 = (Left)var14;
                     PartiallySerializedBlock partiallySerializedValues = (PartiallySerializedBlock)var19.a();
                     if (this.level$3.useDisk()) {
                        this.$outer.logWarning(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Persisting block ", " to disk instead."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockId$12}));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                        this.$outer.diskStore().put(this.blockId$12, new Serializable(this, partiallySerializedValues) {
                           public static final long serialVersionUID = 0L;
                           private final PartiallySerializedBlock partiallySerializedValues$1;

                           public final void apply(WritableByteChannel channel) {
                              OutputStream out = Channels.newOutputStream(channel);
                              this.partiallySerializedValues$1.finishWritingToStream(out);
                           }

                           public {
                              this.partiallySerializedValues$1 = partiallySerializedValues$1;
                           }
                        });
                        size = this.$outer.diskStore().getSize(this.blockId$12);
                        BoxedUnit var31 = BoxedUnit.UNIT;
                     } else {
                        iteratorFromFailedMemoryStorePut = new Some(partiallySerializedValues.valuesIterator());
                        BoxedUnit var32 = BoxedUnit.UNIT;
                     }
                  }
               }
            } else if (this.level$3.useDisk()) {
               this.$outer.diskStore().put(this.blockId$12, new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(WritableByteChannel channel) {
                     OutputStream out = Channels.newOutputStream(channel);
                     this.$outer.org$apache$spark$storage$BlockManager$$anonfun$$$outer().serializerManager().dataSerializeStream(this.$outer.blockId$12, out, (Iterator)this.$outer.iterator$1.apply(), this.$outer.classTag$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               size = this.$outer.diskStore().getSize(this.blockId$12);
            }

            BlockStatus putBlockStatus = this.$outer.org$apache$spark$storage$BlockManager$$getCurrentBlockStatus(this.blockId$12, info);
            boolean blockWasSuccessfullyStored = putBlockStatus.storageLevel().isValid();
            if (blockWasSuccessfullyStored) {
               info.size_$eq(size);
               if (this.tellMaster$1 && info.tellMaster()) {
                  this.$outer.org$apache$spark$storage$BlockManager$$reportBlockStatus(this.blockId$12, putBlockStatus, this.$outer.org$apache$spark$storage$BlockManager$$reportBlockStatus$default$3());
               }

               this.$outer.org$apache$spark$storage$BlockManager$$addUpdatedBlockStatusToTaskMetrics(this.blockId$12, putBlockStatus);
               this.$outer.logDebug(new Serializable(this, startTimeMs) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final long startTimeMs$4;

                  public final String apply() {
                     return (new StringOps(Predef$.MODULE$.augmentString("Put block %s locally took %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockId$12, Utils$.MODULE$.getUsedTimeMs(this.startTimeMs$4)}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.startTimeMs$4 = startTimeMs$4;
                     }
                  }
               });
               if (this.level$3.replication() > 1) {
                  long remoteStartTime = System.currentTimeMillis();
                  BlockData bytesToReplicate = this.$outer.org$apache$spark$storage$BlockManager$$doGetLocalBytes(this.blockId$12, info);
                  ClassTag remoteClassTag = this.$outer.serializerManager().canUseKryo(this.classTag$1) ? this.classTag$1 : scala.reflect.package$.MODULE$.classTag(ClassTag$.MODULE$.Any());

                  try {
                     this.$outer.org$apache$spark$storage$BlockManager$$replicate(this.blockId$12, bytesToReplicate, this.level$3, remoteClassTag, this.$outer.org$apache$spark$storage$BlockManager$$replicate$default$5());
                  } finally {
                     bytesToReplicate.dispose();
                  }

                  this.$outer.logDebug(new Serializable(this, remoteStartTime) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final long remoteStartTime$1;

                     public final String apply() {
                        return (new StringOps(Predef$.MODULE$.augmentString("Put block %s remotely took %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockId$12, Utils$.MODULE$.getUsedTimeMs(this.remoteStartTime$1)}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.remoteStartTime$1 = remoteStartTime$1;
                        }
                     }
                  });
               }
            }

            Predef$.MODULE$.assert(blockWasSuccessfullyStored == iteratorFromFailedMemoryStorePut.isEmpty());
            return iteratorFromFailedMemoryStorePut;
         }

         // $FF: synthetic method
         public BlockManager org$apache$spark$storage$BlockManager$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$12 = blockId$12;
               this.iterator$1 = iterator$1;
               this.level$3 = level$3;
               this.classTag$1 = classTag$1;
               this.tellMaster$1 = tellMaster$1;
            }
         }
      });
   }

   private <T> boolean doPutIterator$default$5() {
      return true;
   }

   private <T> boolean doPutIterator$default$6() {
      return false;
   }

   private Option<ChunkedByteBuffer> maybeCacheDiskBytesInMemory(BlockInfo blockInfo, BlockId blockId, StorageLevel level, BlockData diskData) {
      Predef$.MODULE$.require(!level.deserialized());
      Object var12;
      if (level.useMemory()) {
         Object var6;
         synchronized(blockInfo) {
            if (this.memoryStore().contains(blockId)) {
               diskData.dispose();
               var12 = new Some(this.memoryStore().getBytes(blockId).get());
            } else {
               MemoryMode var8 = level.memoryMode();
               Serializable allocator;
               if (MemoryMode.ON_HEAP.equals(var8)) {
                  allocator = new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final ByteBuffer apply(int x$1) {
                        return ByteBuffer.allocate(x$1);
                     }
                  };
               } else {
                  if (!MemoryMode.OFF_HEAP.equals(var8)) {
                     throw new MatchError(var8);
                  }

                  allocator = new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final ByteBuffer apply(int x$1) {
                        return Platform.allocateDirectBuffer(x$1);
                     }
                  };
               }

               boolean putSucceeded = this.memoryStore().putBytes(blockId, diskData.size(), level.memoryMode(), new Serializable(this, diskData, allocator) {
                  public static final long serialVersionUID = 0L;
                  private final BlockData diskData$2;
                  private final Function1 allocator$1;

                  public final ChunkedByteBuffer apply() {
                     return this.diskData$2.toChunkedByteBuffer(this.allocator$1);
                  }

                  public {
                     this.diskData$2 = diskData$2;
                     this.allocator$1 = allocator$1;
                  }
               }, ClassTag$.MODULE$.Nothing());
               if (putSucceeded) {
                  diskData.dispose();
                  var12 = new Some(this.memoryStore().getBytes(blockId).get());
               } else {
                  var12 = None$.MODULE$;
               }
            }

            var6 = var12;
         }

         var12 = (Option)var6;
      } else {
         var12 = None$.MODULE$;
      }

      return (Option<ChunkedByteBuffer>)var12;
   }

   private <T> Iterator<T> maybeCacheDiskValuesInMemory(BlockInfo blockInfo, BlockId blockId, StorageLevel level, Iterator<T> diskIterator) {
      Predef$.MODULE$.require(level.deserialized());
      ClassTag classTag = blockInfo.classTag();
      Iterator var13;
      if (level.useMemory()) {
         Object var7;
         synchronized(blockInfo) {
            Object var10000;
            if (this.memoryStore().contains(blockId)) {
               var10000 = this.memoryStore().getValues(blockId).get();
            } else {
               Either var8 = this.memoryStore().putIteratorAsValues(blockId, diskIterator, classTag);
               Object var11;
               if (var8 instanceof Left) {
                  Left var9 = (Left)var8;
                  PartiallyUnrolledIterator iter = (PartiallyUnrolledIterator)var9.a();
                  var11 = iter;
               } else {
                  if (!(var8 instanceof Right)) {
                     throw new MatchError(var8);
                  }

                  var11 = (Iterator)this.memoryStore().getValues(blockId).get();
               }

               var10000 = var11;
            }

            var7 = var10000;
         }

         var13 = (Iterator)var7;
      } else {
         var13 = diskIterator;
      }

      return var13;
   }

   public Seq<BlockManagerId> org$apache$spark$storage$BlockManager$$getPeers(boolean forceFetch) {
      Seq var3;
      synchronized(this.peerFetchLock()) {
         int cachedPeersTtl = this.conf().getInt("spark.storage.cachedPeersTtl", 60000);
         boolean timeout = System.currentTimeMillis() - this.lastPeerFetchTime() > (long)cachedPeersTtl;
         if (this.org$apache$spark$storage$BlockManager$$cachedPeers() == null || forceFetch || timeout) {
            this.org$apache$spark$storage$BlockManager$$cachedPeers_$eq((Seq)this.master().getPeers(this.blockManagerId()).sortBy(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(BlockManagerId x$10) {
                  return x$10.hashCode();
               }
            }, Ordering.Int$.MODULE$));
            this.lastPeerFetchTime_$eq(System.currentTimeMillis());
            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockManager $outer;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Fetched peers from master: ").append((Object)this.$outer.org$apache$spark$storage$BlockManager$$cachedPeers().mkString("[", ",", "]")).toString();
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

         var3 = this.org$apache$spark$storage$BlockManager$$cachedPeers();
      }

      return var3;
   }

   public void replicateBlock(BlockId blockId, Set<BlockManagerId> existingReplicas, int maxReplicas) {
      this.logInfo(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;
         private final BlockId blockId$18;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using ", " to pro-actively replicate ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockManagerId(), this.blockId$18}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$18 = blockId$18;
            }
         }
      });
      this.blockInfoManager().lockForReading(blockId, this.blockInfoManager().lockForReading$default$2()).foreach(new Serializable(this, blockId, existingReplicas, maxReplicas) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;
         public final BlockId blockId$18;
         private final Set existingReplicas$2;
         private final int maxReplicas$1;

         public final void apply(BlockInfo info) {
            BlockData data = this.$outer.org$apache$spark$storage$BlockManager$$doGetLocalBytes(this.blockId$18, info);
            StorageLevel storageLevel = StorageLevel$.MODULE$.apply(info.level().useDisk(), info.level().useMemory(), info.level().useOffHeap(), info.level().deserialized(), this.maxReplicas$1);
            this.$outer.org$apache$spark$storage$BlockManager$$getPeers(true);

            try {
               this.$outer.org$apache$spark$storage$BlockManager$$replicate(this.blockId$18, data, storageLevel, info.classTag(), this.existingReplicas$2);
            } finally {
               this.$outer.logDebug(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Releasing lock for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.blockId$18}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               this.$outer.releaseLockAndDispose(this.blockId$18, data, this.$outer.releaseLockAndDispose$default$3());
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blockId$18 = blockId$18;
               this.existingReplicas$2 = existingReplicas$2;
               this.maxReplicas$1 = maxReplicas$1;
            }
         }
      });
   }

   public void org$apache$spark$storage$BlockManager$$replicate(BlockId blockId, BlockData data, StorageLevel level, ClassTag<?> classTag, Set<BlockManagerId> existingReplicas) {
      int maxReplicationFailures = this.conf().getInt("spark.storage.maxReplicationFailures", 1);
      StorageLevel tLevel = StorageLevel$.MODULE$.apply(level.useDisk(), level.useMemory(), level.useOffHeap(), level.deserialized(), 1);
      int numPeersToReplicateTo = level.replication() - 1;
      long startTime = System.nanoTime();
      HashSet peersReplicatedTo = (HashSet)HashSet$.MODULE$.empty().$plus$plus(existingReplicas);
      HashSet peersFailedToReplicateTo = HashSet$.MODULE$.empty();
      IntRef numFailures = IntRef.create(0);
      Seq initialPeers = (Seq)this.org$apache$spark$storage$BlockManager$$getPeers(false).filterNot(new Serializable(this, existingReplicas) {
         public static final long serialVersionUID = 0L;
         private final Set existingReplicas$1;

         public final boolean apply(BlockManagerId elem) {
            return this.existingReplicas$1.contains(elem);
         }

         public {
            this.existingReplicas$1 = existingReplicas$1;
         }
      });
      List peersForReplication = this.blockReplicationPolicy().prioritize(this.blockManagerId(), initialPeers, peersReplicatedTo, blockId, numPeersToReplicateTo);

      while(numFailures.elem <= maxReplicationFailures && !peersForReplication.isEmpty() && peersReplicatedTo.size() < numPeersToReplicateTo) {
         BlockManagerId peer = (BlockManagerId)peersForReplication.head();

         try {
            long onePeerStartTime = System.nanoTime();
            this.logTrace(new Serializable(this, blockId, data, peer) {
               public static final long serialVersionUID = 0L;
               private final BlockId blockId$15;
               private final BlockData data$1;
               private final BlockManagerId peer$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Trying to replicate ", " of ", " bytes to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$15, BoxesRunTime.boxToLong(this.data$1.size()), this.peer$1}));
               }

               public {
                  this.blockId$15 = blockId$15;
                  this.data$1 = data$1;
                  this.peer$1 = peer$1;
               }
            });
            this.blockTransferService().uploadBlockSync(peer.host(), peer.port(), peer.executorId(), blockId, new BlockManagerManagedBuffer(this.blockInfoManager(), blockId, data, false), tLevel, classTag);
            this.logTrace(new Serializable(this, blockId, data, peer, onePeerStartTime) {
               public static final long serialVersionUID = 0L;
               private final BlockId blockId$15;
               private final BlockData data$1;
               private final BlockManagerId peer$1;
               private final long onePeerStartTime$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Replicated ", " of ", " bytes to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$15, BoxesRunTime.boxToLong(this.data$1.size()), this.peer$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" in ", " ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble((double)(System.nanoTime() - this.onePeerStartTime$1) / (double)1000000.0F)}))).toString();
               }

               public {
                  this.blockId$15 = blockId$15;
                  this.data$1 = data$1;
                  this.peer$1 = peer$1;
                  this.onePeerStartTime$1 = onePeerStartTime$1;
               }
            });
            peersForReplication = (List)peersForReplication.tail();
            peersReplicatedTo.$plus$eq(peer);
         } catch (Throwable var26) {
            Option var19 = NonFatal$.MODULE$.unapply(var26);
            if (var19.isEmpty()) {
               throw var26;
            }

            Throwable e = (Throwable)var19.get();
            this.logWarning(new Serializable(this, blockId, numFailures, peer) {
               public static final long serialVersionUID = 0L;
               private final BlockId blockId$15;
               private final IntRef numFailures$1;
               private final BlockManagerId peer$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to replicate ", " to ", ", failure #", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$15, this.peer$1, BoxesRunTime.boxToInteger(this.numFailures$1.elem)}));
               }

               public {
                  this.blockId$15 = blockId$15;
                  this.numFailures$1 = numFailures$1;
                  this.peer$1 = peer$1;
               }
            }, e);
            peersFailedToReplicateTo.$plus$eq(peer);
            Seq filteredPeers = (Seq)this.org$apache$spark$storage$BlockManager$$getPeers(true).filter(new Serializable(this, peersReplicatedTo, peersFailedToReplicateTo) {
               public static final long serialVersionUID = 0L;
               private final HashSet peersReplicatedTo$1;
               private final HashSet peersFailedToReplicateTo$1;

               public final boolean apply(BlockManagerId p) {
                  return !this.peersFailedToReplicateTo$1.contains(p) && !this.peersReplicatedTo$1.contains(p);
               }

               public {
                  this.peersReplicatedTo$1 = peersReplicatedTo$1;
                  this.peersFailedToReplicateTo$1 = peersFailedToReplicateTo$1;
               }
            });
            ++numFailures.elem;
            peersForReplication = this.blockReplicationPolicy().prioritize(this.blockManagerId(), filteredPeers, peersReplicatedTo, blockId, numPeersToReplicateTo - peersReplicatedTo.size());
            BoxedUnit var21 = BoxedUnit.UNIT;
            continue;
         }
      }

      this.logDebug(new Serializable(this, blockId, data, startTime, peersReplicatedTo) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$15;
         private final BlockData data$1;
         private final long startTime$1;
         private final HashSet peersReplicatedTo$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Replicating ", " of ", " bytes to "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$15, BoxesRunTime.boxToLong(this.data$1.size())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " peer(s) took ", " ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.peersReplicatedTo$1.size()), BoxesRunTime.boxToDouble((double)(System.nanoTime() - this.startTime$1) / (double)1000000.0F)}))).toString();
         }

         public {
            this.blockId$15 = blockId$15;
            this.data$1 = data$1;
            this.startTime$1 = startTime$1;
            this.peersReplicatedTo$1 = peersReplicatedTo$1;
         }
      });
      if (peersReplicatedTo.size() < numPeersToReplicateTo) {
         this.logWarning(new Serializable(this, blockId, numPeersToReplicateTo, peersReplicatedTo) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$15;
            private final int numPeersToReplicateTo$1;
            private final HashSet peersReplicatedTo$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " replicated to only "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$15}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " peer(s) instead of ", " peers"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.peersReplicatedTo$1.size()), BoxesRunTime.boxToInteger(this.numPeersToReplicateTo$1)}))).toString();
            }

            public {
               this.blockId$15 = blockId$15;
               this.numPeersToReplicateTo$1 = numPeersToReplicateTo$1;
               this.peersReplicatedTo$1 = peersReplicatedTo$1;
            }
         });
      }

      this.logDebug(new Serializable(this, blockId, peersReplicatedTo) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$15;
         private final HashSet peersReplicatedTo$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"block ", " replicated to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$15, this.peersReplicatedTo$1.mkString(", ")}));
         }

         public {
            this.blockId$15 = blockId$15;
            this.peersReplicatedTo$1 = peersReplicatedTo$1;
         }
      });
   }

   public Set<BlockManagerId> org$apache$spark$storage$BlockManager$$replicate$default$5() {
      return Predef$.MODULE$.Set().empty();
   }

   public <T> Option<T> getSingle(BlockId blockId, ClassTag<T> evidence$5) {
      return this.get(blockId, evidence$5).<T>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final T apply(BlockResult x$11) {
            return (T)x$11.data().next();
         }
      });
   }

   public <T> boolean putSingle(BlockId blockId, T value, StorageLevel level, boolean tellMaster, ClassTag<T> evidence$6) {
      return this.putIterator(blockId, scala.package$.MODULE$.Iterator().apply(Predef$.MODULE$.genericWrapArray(new Object[]{value})), level, tellMaster, evidence$6);
   }

   public <T> boolean putSingle$default$4() {
      return true;
   }

   public <T> StorageLevel dropFromMemory(BlockId blockId, Function0<Either<Object, ChunkedByteBuffer>> data, ClassTag<T> evidence$7) {
      this.logInfo(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$19;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Dropping block ", " from memory"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$19}));
         }

         public {
            this.blockId$19 = blockId$19;
         }
      });
      BlockInfo info = this.blockInfoManager().assertBlockIsLockedForWriting(blockId);
      boolean blockIsUpdated = false;
      StorageLevel level = info.level();
      if (level.useDisk() && !this.diskStore().contains(blockId)) {
         this.logInfo(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$19;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Writing block ", " to disk"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$19}));
            }

            public {
               this.blockId$19 = blockId$19;
            }
         });
         Either var7 = (Either)data.apply();
         if (var7 instanceof Left) {
            Left var8 = (Left)var7;
            Object elements = var8.a();
            this.diskStore().put(blockId, new Serializable(this, blockId, info, elements) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final BlockManager $outer;
               private final BlockId blockId$19;
               private final BlockInfo info$1;
               private final Object elements$1;

               public final void apply(WritableByteChannel channel) {
                  OutputStream out = Channels.newOutputStream(channel);
                  this.$outer.serializerManager().dataSerializeStream(this.blockId$19, out, Predef$.MODULE$.genericArrayOps(this.elements$1).toIterator(), this.info$1.classTag());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.blockId$19 = blockId$19;
                     this.info$1 = info$1;
                     this.elements$1 = elements$1;
                  }
               }
            });
            BoxedUnit var10 = BoxedUnit.UNIT;
         } else {
            if (!(var7 instanceof Right)) {
               throw new MatchError(var7);
            }

            Right var11 = (Right)var7;
            ChunkedByteBuffer bytes = (ChunkedByteBuffer)var11.b();
            this.diskStore().putBytes(blockId, bytes);
            BoxedUnit var17 = BoxedUnit.UNIT;
         }

         blockIsUpdated = true;
      }

      long droppedMemorySize = this.memoryStore().contains(blockId) ? this.memoryStore().getSize(blockId) : 0L;
      boolean blockIsRemoved = this.memoryStore().remove(blockId);
      if (blockIsRemoved) {
         blockIsUpdated = true;
      } else {
         this.logWarning(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$19;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " could not be dropped from memory as it does not exist"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$19}));
            }

            public {
               this.blockId$19 = blockId$19;
            }
         });
      }

      BlockStatus status = this.org$apache$spark$storage$BlockManager$$getCurrentBlockStatus(blockId, info);
      if (info.tellMaster()) {
         this.org$apache$spark$storage$BlockManager$$reportBlockStatus(blockId, status, droppedMemorySize);
      }

      if (blockIsUpdated) {
         this.org$apache$spark$storage$BlockManager$$addUpdatedBlockStatusToTaskMetrics(blockId, status);
      }

      return status.storageLevel();
   }

   public int removeRdd(int rddId) {
      this.logInfo(new Serializable(this, rddId) {
         public static final long serialVersionUID = 0L;
         private final int rddId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing RDD ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.rddId$1)}));
         }

         public {
            this.rddId$1 = rddId$1;
         }
      });
      Iterator blocksToRemove = this.blockInfoManager().entries().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Iterable<RDDBlockId> apply(Tuple2<BlockId, BlockInfo> x$12) {
            return Option$.MODULE$.<RDDBlockId>option2Iterable(((BlockId)x$12._1()).asRDDId());
         }
      }).filter(new Serializable(this, rddId) {
         public static final long serialVersionUID = 0L;
         private final int rddId$1;

         public final boolean apply(RDDBlockId x$13) {
            return x$13.rddId() == this.rddId$1;
         }

         public {
            this.rddId$1 = rddId$1;
         }
      });
      blocksToRemove.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;

         public final void apply(RDDBlockId blockId) {
            this.$outer.removeBlock(blockId, false);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      return blocksToRemove.size();
   }

   public int removeBroadcast(long broadcastId, boolean tellMaster) {
      this.logDebug(new Serializable(this, broadcastId) {
         public static final long serialVersionUID = 0L;
         private final long broadcastId$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing broadcast ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.broadcastId$1)}));
         }

         public {
            this.broadcastId$1 = broadcastId$1;
         }
      });
      Iterator blocksToRemove = this.blockInfoManager().entries().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final BlockId apply(Tuple2<BlockId, BlockInfo> x$14) {
            return (BlockId)x$14._1();
         }
      }).collect(new Serializable(this, broadcastId) {
         public static final long serialVersionUID = 0L;
         private final long broadcastId$1;

         public final <A1 extends BlockId, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var7;
            if (x1 instanceof BroadcastBlockId) {
               BroadcastBlockId var4 = (BroadcastBlockId)x1;
               long var5 = var4.broadcastId();
               if (this.broadcastId$1 == var5) {
                  var7 = var4;
                  return (B1)var7;
               }
            }

            var7 = var2.apply(x1);
            return (B1)var7;
         }

         public final boolean isDefinedAt(BlockId x1) {
            boolean var6;
            if (x1 instanceof BroadcastBlockId) {
               BroadcastBlockId var3 = (BroadcastBlockId)x1;
               long var4 = var3.broadcastId();
               if (this.broadcastId$1 == var4) {
                  var6 = true;
                  return var6;
               }
            }

            var6 = false;
            return var6;
         }

         public {
            this.broadcastId$1 = broadcastId$1;
         }
      });
      blocksToRemove.foreach(new Serializable(this, tellMaster) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockManager $outer;
         private final boolean tellMaster$3;

         public final void apply(BroadcastBlockId blockId) {
            this.$outer.removeBlock(blockId, this.tellMaster$3);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.tellMaster$3 = tellMaster$3;
            }
         }
      });
      return blocksToRemove.size();
   }

   public void removeBlock(BlockId blockId, boolean tellMaster) {
      this.logDebug(new Serializable(this, blockId) {
         public static final long serialVersionUID = 0L;
         private final BlockId blockId$9;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removing block ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$9}));
         }

         public {
            this.blockId$9 = blockId$9;
         }
      });
      Option var3 = this.blockInfoManager().lockForWriting(blockId, this.blockInfoManager().lockForWriting$default$2());
      if (None$.MODULE$.equals(var3)) {
         this.logWarning(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$9;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Asked to remove block ", ", which does not exist"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$9}));
            }

            public {
               this.blockId$9 = blockId$9;
            }
         });
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else {
         if (!(var3 instanceof Some)) {
            throw new MatchError(var3);
         }

         Some var5 = (Some)var3;
         BlockInfo info = (BlockInfo)var5.x();
         this.removeBlockInternal(blockId, tellMaster && info.tellMaster());
         this.org$apache$spark$storage$BlockManager$$addUpdatedBlockStatusToTaskMetrics(blockId, BlockStatus$.MODULE$.empty());
         BoxedUnit var7 = BoxedUnit.UNIT;
      }

   }

   public boolean removeBlock$default$2() {
      return true;
   }

   private void removeBlockInternal(BlockId blockId, boolean tellMaster) {
      boolean removedFromMemory = this.memoryStore().remove(blockId);
      boolean removedFromDisk = this.diskStore().remove(blockId);
      if (!removedFromMemory && !removedFromDisk) {
         this.logWarning(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$17;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " could not be removed as it was not found on disk or in memory"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$17}));
            }

            public {
               this.blockId$17 = blockId$17;
            }
         });
      }

      this.blockInfoManager().removeBlock(blockId);
      if (tellMaster) {
         this.org$apache$spark$storage$BlockManager$$reportBlockStatus(blockId, BlockStatus$.MODULE$.empty(), this.org$apache$spark$storage$BlockManager$$reportBlockStatus$default$3());
      }

   }

   public void org$apache$spark$storage$BlockManager$$addUpdatedBlockStatusToTaskMetrics(BlockId blockId, BlockStatus status) {
      if (BoxesRunTime.unboxToBoolean(this.conf().get(package$.MODULE$.TASK_METRICS_TRACK_UPDATED_BLOCK_STATUSES()))) {
         Option$.MODULE$.apply(TaskContext$.MODULE$.get()).foreach(new Serializable(this, blockId, status) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$16;
            private final BlockStatus status$1;

            public final void apply(TaskContext c) {
               c.taskMetrics().incUpdatedBlockStatuses(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(this.blockId$16), this.status$1));
            }

            public {
               this.blockId$16 = blockId$16;
               this.status$1 = status$1;
            }
         });
      }

   }

   public void releaseLockAndDispose(BlockId blockId, BlockData data, Option<Object> taskAttemptId) {
      this.releaseLock(blockId, taskAttemptId);
      data.dispose();
   }

   public Option<Object> releaseLockAndDispose$default$3() {
      return None$.MODULE$;
   }

   public void stop() {
      this.blockTransferService().close();
      if (this.shuffleClient() != this.blockTransferService()) {
         this.shuffleClient().close();
      }

      this.remoteBlockTempFileManager().stop();
      this.diskBlockManager().stop();
      this.rpcEnv.stop(this.slaveEndpoint());
      this.blockInfoManager().clear();
      this.memoryStore().clear();
      this.org$apache$spark$storage$BlockManager$$futureExecutionContext().shutdownNow();
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "BlockManager stopped";
         }
      });
   }

   public BlockManager(String executorId, RpcEnv rpcEnv, BlockManagerMaster master, SerializerManager serializerManager, SparkConf conf, MemoryManager memoryManager, MapOutputTracker mapOutputTracker, ShuffleManager shuffleManager, BlockTransferService blockTransferService, SecurityManager securityManager, int numUsableCores) {
      boolean var10001;
      label29: {
         label28: {
            this.executorId = executorId;
            this.rpcEnv = rpcEnv;
            this.master = master;
            this.serializerManager = serializerManager;
            this.conf = conf;
            this.shuffleManager = shuffleManager;
            this.blockTransferService = blockTransferService;
            super();
            Logging$class.$init$(this);
            this.externalShuffleServiceEnabled = conf.getBoolean("spark.shuffle.service.enabled", false);
            if (this.externalShuffleServiceEnabled()) {
               String var13 = SparkContext$.MODULE$.DRIVER_IDENTIFIER();
               if (executorId == null) {
                  if (var13 != null) {
                     break label28;
                  }
               } else if (!executorId.equals(var13)) {
                  break label28;
               }
            }

            var10001 = true;
            break label29;
         }

         var10001 = false;
      }

      boolean deleteFilesOnStop = var10001;
      this.diskBlockManager = new DiskBlockManager(conf, deleteFilesOnStop);
      this.blockInfoManager = new BlockInfoManager();
      this.org$apache$spark$storage$BlockManager$$futureExecutionContext = ExecutionContext$.MODULE$.fromExecutorService(ThreadUtils$.MODULE$.newDaemonCachedThreadPool("block-manager-future", 128, ThreadUtils$.MODULE$.newDaemonCachedThreadPool$default$3()));
      this.memoryStore = new MemoryStore(conf, this.blockInfoManager(), serializerManager, memoryManager, this);
      this.diskStore = new DiskStore(conf, this.diskBlockManager(), securityManager);
      memoryManager.setMemoryStore(this.memoryStore());
      this.maxOnHeapMemory = memoryManager.maxOnHeapStorageMemory();
      this.maxOffHeapMemory = memoryManager.maxOffHeapStorageMemory();
      int tmpPort = (new StringOps(Predef$.MODULE$.augmentString(Utils$.MODULE$.getSparkOrYarnConfig(conf, "spark.shuffle.service.port", "7337")))).toInt();
      this.org$apache$spark$storage$BlockManager$$externalShuffleServicePort = tmpPort == 0 ? (new StringOps(Predef$.MODULE$.augmentString(conf.get("spark.shuffle.service.port")))).toInt() : tmpPort;
      Object var16;
      if (this.externalShuffleServiceEnabled()) {
         TransportConf transConf = SparkTransportConf$.MODULE$.fromSparkConf(conf, "shuffle", numUsableCores);
         var16 = new ExternalShuffleClient(transConf, securityManager, securityManager.isAuthenticationEnabled(), BoxesRunTime.unboxToLong(conf.get(package$.MODULE$.SHUFFLE_REGISTRATION_TIMEOUT())));
      } else {
         var16 = blockTransferService;
      }

      this.shuffleClient = (ShuffleClient)var16;
      this.maxFailuresBeforeLocationRefresh = conf.getInt("spark.block.failures.beforeLocationRefresh", 5);
      this.slaveEndpoint = rpcEnv.setupEndpoint((new StringBuilder()).append((Object)"BlockManagerEndpoint").append((Object)BoxesRunTime.boxToInteger(BlockManager$.MODULE$.org$apache$spark$storage$BlockManager$$ID_GENERATOR().next())).toString(), new BlockManagerSlaveEndpoint(rpcEnv, this, mapOutputTracker));
      this.org$apache$spark$storage$BlockManager$$asyncReregisterTask = null;
      this.org$apache$spark$storage$BlockManager$$asyncReregisterLock = new Object();
      this.peerFetchLock = new Object();
      this.lastPeerFetchTime = 0L;
      this.remoteBlockTempFileManager = new RemoteBlockTempFileManager(this);
      this.maxRemoteBlockToMem = BoxesRunTime.unboxToLong(conf.get(package$.MODULE$.MAX_REMOTE_BLOCK_SIZE_FETCH_TO_MEM()));
   }

   public static class ShuffleMetricsSource implements Source {
      private final String sourceName;
      private final MetricRegistry metricRegistry;

      public String sourceName() {
         return this.sourceName;
      }

      public MetricRegistry metricRegistry() {
         return this.metricRegistry;
      }

      public ShuffleMetricsSource(String sourceName, MetricSet metricSet) {
         this.sourceName = sourceName;
         this.metricRegistry = new MetricRegistry();
         this.metricRegistry().registerAll(metricSet);
      }
   }

   public static class RemoteBlockTempFileManager implements TempFileManager, Logging {
      private final BlockManager blockManager;
      private final ReferenceQueue<File> referenceQueue;
      private final java.util.Set<ReferenceWithCleanup> org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$$referenceBuffer;
      private final int POLL_TIMEOUT;
      private volatile boolean stopped;
      private final Thread cleaningThread;
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

      private ReferenceQueue<File> referenceQueue() {
         return this.referenceQueue;
      }

      public java.util.Set<ReferenceWithCleanup> org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$$referenceBuffer() {
         return this.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$$referenceBuffer;
      }

      private int POLL_TIMEOUT() {
         return this.POLL_TIMEOUT;
      }

      private boolean stopped() {
         return this.stopped;
      }

      private void stopped_$eq(boolean x$1) {
         this.stopped = x$1;
      }

      private Thread cleaningThread() {
         return this.cleaningThread;
      }

      public File createTempFile() {
         return (File)this.blockManager.diskBlockManager().createTempLocalBlock()._2();
      }

      public boolean registerTempFileToClean(File file) {
         return this.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$$referenceBuffer().add(new ReferenceWithCleanup(file, this.referenceQueue()));
      }

      public void stop() {
         this.stopped_$eq(true);
         this.cleaningThread().interrupt();
         this.cleaningThread().join();
      }

      public void org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$$keepCleaning() {
         // $FF: Couldn't be decompiled
      }

      public RemoteBlockTempFileManager(BlockManager blockManager) {
         this.blockManager = blockManager;
         Logging$class.$init$(this);
         this.referenceQueue = new ReferenceQueue();
         this.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$$referenceBuffer = Collections.newSetFromMap(new ConcurrentHashMap());
         this.POLL_TIMEOUT = 1000;
         this.stopped = false;
         this.cleaningThread = new Thread(this) {
            // $FF: synthetic field
            private final RemoteBlockTempFileManager $outer;

            public void run() {
               this.$outer.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$$keepCleaning();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
         this.cleaningThread().setDaemon(true);
         this.cleaningThread().setName("RemoteBlock-temp-file-clean-thread");
         this.cleaningThread().start();
      }

      public class ReferenceWithCleanup extends WeakReference<File> {
         private final String org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$filePath;
         // $FF: synthetic field
         public final RemoteBlockTempFileManager $outer;

         public String org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$filePath() {
            return this.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$filePath;
         }

         public void cleanUp() {
            this.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$$outer().logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ReferenceWithCleanup $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Clean up file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$filePath()}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            if (!(new File(this.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$filePath())).delete()) {
               this.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$$outer().logDebug(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final ReferenceWithCleanup $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Fail to delete file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$filePath()}));
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
         public RemoteBlockTempFileManager org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$$outer() {
            return this.$outer;
         }

         public ReferenceWithCleanup(RemoteBlockTempFileManager file, File referenceQueue) {
            if (RemoteBlockTempFileManager.this == null) {
               throw null;
            } else {
               this.$outer = RemoteBlockTempFileManager.this;
               super(file, referenceQueue);
               this.org$apache$spark$storage$BlockManager$RemoteBlockTempFileManager$ReferenceWithCleanup$$filePath = file.getAbsolutePath();
            }
         }
      }
   }
}
