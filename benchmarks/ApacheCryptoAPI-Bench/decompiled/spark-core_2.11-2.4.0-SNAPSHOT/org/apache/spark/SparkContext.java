package org.apache.spark;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.log4j.Level;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.input.FixedLengthBinaryInputFormat;
import org.apache.spark.input.FixedLengthBinaryInputFormat$;
import org.apache.spark.input.PortableDataStream;
import org.apache.spark.input.StreamInputFormat;
import org.apache.spark.input.WholeTextFileInputFormat;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.io.CompressionCodec$;
import org.apache.spark.partial.ApproximateEvaluator;
import org.apache.spark.partial.PartialResult;
import org.apache.spark.rdd.BinaryFileRDD;
import org.apache.spark.rdd.EmptyRDD;
import org.apache.spark.rdd.HadoopRDD;
import org.apache.spark.rdd.NewHadoopRDD;
import org.apache.spark.rdd.ParallelCollectionRDD;
import org.apache.spark.rdd.PartitionerAwareUnionRDD;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDDOperationScope$;
import org.apache.spark.rdd.ReliableCheckpointRDD;
import org.apache.spark.rdd.ReliableCheckpointRDD$;
import org.apache.spark.rdd.UnionRDD;
import org.apache.spark.rdd.WholeTextFileRDD;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.scheduler.DAGScheduler;
import org.apache.spark.scheduler.EventLoggingListener;
import org.apache.spark.scheduler.EventLoggingListener$;
import org.apache.spark.scheduler.JobWaiter;
import org.apache.spark.scheduler.LiveListenerBus;
import org.apache.spark.scheduler.LiveListenerBus$;
import org.apache.spark.scheduler.Schedulable;
import org.apache.spark.scheduler.SchedulerBackend;
import org.apache.spark.scheduler.SparkListenerApplicationEnd;
import org.apache.spark.scheduler.SparkListenerApplicationStart;
import org.apache.spark.scheduler.SparkListenerEnvironmentUpdate;
import org.apache.spark.scheduler.SparkListenerInterface;
import org.apache.spark.scheduler.SparkListenerUnpersistRDD;
import org.apache.spark.scheduler.TaskLocation;
import org.apache.spark.scheduler.TaskScheduler;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.AppStatusStore$;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.storage.BlockManagerMessages;
import org.apache.spark.storage.BlockManagerSource;
import org.apache.spark.storage.RDDInfo;
import org.apache.spark.storage.RDDInfo$;
import org.apache.spark.storage.StorageStatus;
import org.apache.spark.storage.StorageUtils$;
import org.apache.spark.ui.ConsoleProgressBar;
import org.apache.spark.ui.SparkUI;
import org.apache.spark.ui.SparkUI$;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.CallSite;
import org.apache.spark.util.CallSite$;
import org.apache.spark.util.ClosureCleaner$;
import org.apache.spark.util.CollectionAccumulator;
import org.apache.spark.util.DoubleAccumulator;
import org.apache.spark.util.LongAccumulator;
import org.apache.spark.util.SerializableConfiguration;
import org.apache.spark.util.ShutdownHookManager$;
import org.apache.spark.util.ThreadStackTrace;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.spark_project.guava.collect.MapMaker;
import org.spark_project.jetty.servlet.ServletContextHandler;
import scala.Enumeration;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.BufferedIterator;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.JavaConverters$;
import scala.collection.Map$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Traversable;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$class;
import scala.collection.concurrent.Map;
import scala.collection.generic.CanBuildFrom;
import scala.collection.generic.Growable;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.StringOps;
import scala.collection.immutable.Vector;
import scala.collection.mutable.ArrayOps;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashMap$;
import scala.collection.mutable.StringBuilder;
import scala.math.BigInt;
import scala.math.BigInt$;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;
import scala.runtime.VolatileByteRef;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001=Uf\u0001B\u0001\u0003\u0001%\u0011Ab\u00159be.\u001cuN\u001c;fqRT!a\u0001\u0003\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u00151\u0011AB1qC\u000eDWMC\u0001\b\u0003\ry'oZ\u0002\u0001'\r\u0001!\u0002\u0005\t\u0003\u00179i\u0011\u0001\u0004\u0006\u0002\u001b\u0005)1oY1mC&\u0011q\u0002\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005E!R\"\u0001\n\u000b\u0005M\u0011\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005U\u0011\"a\u0002'pO\u001eLgn\u001a\u0005\t/\u0001\u0011\t\u0011)A\u00051\u000511m\u001c8gS\u001e\u0004\"!\u0007\u000e\u000e\u0003\tI!a\u0007\u0002\u0003\u0013M\u0003\u0018M]6D_:4\u0007\"B\u000f\u0001\t\u0003q\u0012A\u0002\u001fj]&$h\b\u0006\u0002 AA\u0011\u0011\u0004\u0001\u0005\u0006/q\u0001\r\u0001\u0007\u0005\bE\u0001\u0011\r\u0011\"\u0003$\u00031\u0019'/Z1uS>t7+\u001b;f+\u0005!\u0003CA\u0013)\u001b\u00051#BA\u0014\u0003\u0003\u0011)H/\u001b7\n\u0005%2#\u0001C\"bY2\u001c\u0016\u000e^3\t\r-\u0002\u0001\u0015!\u0003%\u00035\u0019'/Z1uS>t7+\u001b;fA!9Q\u0006\u0001b\u0001\n\u0013q\u0013!F1mY><X*\u001e7uSBdWmQ8oi\u0016DHo]\u000b\u0002_A\u00111\u0002M\u0005\u0003c1\u0011qAQ8pY\u0016\fg\u000e\u0003\u00044\u0001\u0001\u0006IaL\u0001\u0017C2dwn^'vYRL\u0007\u000f\\3D_:$X\r\u001f;tA!9Q\u0007\u0001b\u0001\n\u00031\u0014!C:uCJ$H+[7f+\u00059\u0004CA\u00069\u0013\tIDB\u0001\u0003M_:<\u0007BB\u001e\u0001A\u0003%q'\u0001\u0006ti\u0006\u0014H\u000fV5nK\u0002B\u0001\"\u0010\u0001C\u0002\u0013\u0005!AP\u0001\bgR|\u0007\u000f]3e+\u0005y\u0004C\u0001!I\u001b\u0005\t%B\u0001\"D\u0003\u0019\tGo\\7jG*\u0011A)R\u0001\u000bG>t7-\u001e:sK:$(BA\u0014G\u0015\u00059\u0015\u0001\u00026bm\u0006L!!S!\u0003\u001b\u0005#x.\\5d\u0005>|G.Z1o\u0011\u0019Y\u0005\u0001)A\u0005\u007f\u0005A1\u000f^8qa\u0016$\u0007\u0005\u0003\u0004N\u0001\u0011\u0005!AT\u0001\u0011CN\u001cXM\u001d;O_R\u001cFo\u001c9qK\u0012$\u0012a\u0014\t\u0003\u0017AK!!\u0015\u0007\u0003\tUs\u0017\u000e\u001e\u0005\u0006;\u0001!\ta\u0015\u000b\u0002?!)Q\u0004\u0001C\u0001+R!qDV0b\u0011\u00159F\u000b1\u0001Y\u0003\u0019i\u0017m\u001d;feB\u0011\u0011\f\u0018\b\u0003\u0017iK!a\u0017\u0007\u0002\rA\u0013X\rZ3g\u0013\tifL\u0001\u0004TiJLgn\u001a\u0006\u000372AQ\u0001\u0019+A\u0002a\u000bq!\u00199q\u001d\u0006lW\rC\u0003c)\u0002\u0007\u0001$\u0001\u0003d_:4\u0007\"B\u000f\u0001\t\u0003!GCB\u0010fM\u001eLw\u000fC\u0003XG\u0002\u0007\u0001\fC\u0003aG\u0002\u0007\u0001\fC\u0004iGB\u0005\t\u0019\u0001-\u0002\u0013M\u0004\u0018M]6I_6,\u0007b\u00026d!\u0003\u0005\ra[\u0001\u0005U\u0006\u00148\u000fE\u0002mibs!!\u001c:\u000f\u00059\fX\"A8\u000b\u0005AD\u0011A\u0002\u001fs_>$h(C\u0001\u000e\u0013\t\u0019H\"A\u0004qC\u000e\\\u0017mZ3\n\u0005U4(aA*fc*\u00111\u000f\u0004\u0005\bq\u000e\u0004\n\u00111\u0001z\u0003-)gN^5s_:lWM\u001c;\u0011\til\b\fW\u0007\u0002w*\u0011A\u0010D\u0001\u000bG>dG.Z2uS>t\u0017B\u0001@|\u0005\ri\u0015\r\u001d\u0005\b;\u0001!\tAAA\u0001)\u0015y\u00121AA\u0003\u0011\u00159v\u00101\u0001Y\u0011\u0015\u0001w\u00101\u0001Y\u0011\u001di\u0002\u0001\"\u0001\u0003\u0003\u0013!raHA\u0006\u0003\u001b\ty\u0001\u0003\u0004X\u0003\u000f\u0001\r\u0001\u0017\u0005\u0007A\u0006\u001d\u0001\u0019\u0001-\t\r!\f9\u00011\u0001Y\u0011\u001di\u0002\u0001\"\u0001\u0003\u0003'!\u0012bHA\u000b\u0003/\tI\"a\u0007\t\r]\u000b\t\u00021\u0001Y\u0011\u0019\u0001\u0017\u0011\u0003a\u00011\"1\u0001.!\u0005A\u0002aCaA[A\t\u0001\u0004Y\u0007bCA\u0010\u0001\u0001\u0007\t\u0019!C\u0005\u0003C\tQaX2p]\u001a,\u0012\u0001\u0007\u0005\f\u0003K\u0001\u0001\u0019!a\u0001\n\u0013\t9#A\u0005`G>tgm\u0018\u0013fcR\u0019q*!\u000b\t\u0013\u0005-\u00121EA\u0001\u0002\u0004A\u0012a\u0001=%c!9\u0011q\u0006\u0001!B\u0013A\u0012AB0d_:4\u0007\u0005C\u0005\u00024\u0001\u0001\r\u0011\"\u0003\u00026\u0005aq,\u001a<f]Rdun\u001a#jeV\u0011\u0011q\u0007\t\u0006\u0017\u0005e\u0012QH\u0005\u0004\u0003wa!AB(qi&|g\u000e\u0005\u0003\u0002@\u0005\u0015SBAA!\u0015\r\t\u0019ER\u0001\u0004]\u0016$\u0018\u0002BA$\u0003\u0003\u00121!\u0016*J\u0011%\tY\u0005\u0001a\u0001\n\u0013\ti%\u0001\t`KZ,g\u000e\u001e'pO\u0012K'o\u0018\u0013fcR\u0019q*a\u0014\t\u0015\u0005-\u0012\u0011JA\u0001\u0002\u0004\t9\u0004\u0003\u0005\u0002T\u0001\u0001\u000b\u0015BA\u001c\u00035yVM^3oi2{w\rR5sA!I\u0011q\u000b\u0001A\u0002\u0013%\u0011\u0011L\u0001\u000f?\u00164XM\u001c;M_\u001e\u001cu\u000eZ3d+\t\tY\u0006\u0005\u0003\f\u0003sA\u0006\"CA0\u0001\u0001\u0007I\u0011BA1\u0003IyVM^3oi2{wmQ8eK\u000e|F%Z9\u0015\u0007=\u000b\u0019\u0007\u0003\u0006\u0002,\u0005u\u0013\u0011!a\u0001\u00037B\u0001\"a\u001a\u0001A\u0003&\u00111L\u0001\u0010?\u00164XM\u001c;M_\u001e\u001cu\u000eZ3dA!Y\u00111\u000e\u0001A\u0002\u0003\u0007I\u0011BA7\u00031yF.[:uK:,'OQ;t+\t\ty\u0007\u0005\u0003\u0002r\u0005]TBAA:\u0015\r\t)HA\u0001\ng\u000eDW\rZ;mKJLA!!\u001f\u0002t\tyA*\u001b<f\u0019&\u001cH/\u001a8fe\n+8\u000fC\u0006\u0002~\u0001\u0001\r\u00111A\u0005\n\u0005}\u0014\u0001E0mSN$XM\\3s\u0005V\u001cx\fJ3r)\ry\u0015\u0011\u0011\u0005\u000b\u0003W\tY(!AA\u0002\u0005=\u0004\u0002CAC\u0001\u0001\u0006K!a\u001c\u0002\u001b}c\u0017n\u001d;f]\u0016\u0014()^:!\u0011-\tI\t\u0001a\u0001\u0002\u0004%I!a#\u0002\t}+gN^\u000b\u0003\u0003\u001b\u00032!GAH\u0013\r\t\tJ\u0001\u0002\t'B\f'o[#om\"Y\u0011Q\u0013\u0001A\u0002\u0003\u0007I\u0011BAL\u0003!yVM\u001c<`I\u0015\fHcA(\u0002\u001a\"Q\u00111FAJ\u0003\u0003\u0005\r!!$\t\u0011\u0005u\u0005\u0001)Q\u0005\u0003\u001b\u000bQaX3om\u0002B1\"!)\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u0002$\u0006qql\u001d;biV\u001cHK]1dW\u0016\u0014XCAAS!\rI\u0012qU\u0005\u0004\u0003S\u0013!AE*qCJ\\7\u000b^1ukN$&/Y2lKJD1\"!,\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u00020\u0006\u0011rl\u001d;biV\u001cHK]1dW\u0016\u0014x\fJ3r)\ry\u0015\u0011\u0017\u0005\u000b\u0003W\tY+!AA\u0002\u0005\u0015\u0006\u0002CA[\u0001\u0001\u0006K!!*\u0002\u001f}\u001bH/\u0019;vgR\u0013\u0018mY6fe\u0002B\u0011\"!/\u0001\u0001\u0004%I!a/\u0002\u0019}\u0003(o\\4sKN\u001c()\u0019:\u0016\u0005\u0005u\u0006#B\u0006\u0002:\u0005}\u0006\u0003BAa\u0003\u000fl!!a1\u000b\u0007\u0005\u0015'!\u0001\u0002vS&!\u0011\u0011ZAb\u0005I\u0019uN\\:pY\u0016\u0004&o\\4sKN\u001c()\u0019:\t\u0013\u00055\u0007\u00011A\u0005\n\u0005=\u0017\u0001E0qe><'/Z:t\u0005\u0006\u0014x\fJ3r)\ry\u0015\u0011\u001b\u0005\u000b\u0003W\tY-!AA\u0002\u0005u\u0006\u0002CAk\u0001\u0001\u0006K!!0\u0002\u001b}\u0003(o\\4sKN\u001c()\u0019:!\u0011%\tI\u000e\u0001a\u0001\n\u0013\tY.A\u0002`k&,\"!!8\u0011\u000b-\tI$a8\u0011\t\u0005\u0005\u0017\u0011]\u0005\u0005\u0003G\f\u0019MA\u0004Ta\u0006\u00148.V%\t\u0013\u0005\u001d\b\u00011A\u0005\n\u0005%\u0018aB0vS~#S-\u001d\u000b\u0004\u001f\u0006-\bBCA\u0016\u0003K\f\t\u00111\u0001\u0002^\"A\u0011q\u001e\u0001!B\u0013\ti.\u0001\u0003`k&\u0004\u0003bCAz\u0001\u0001\u0007\t\u0019!C\u0005\u0003k\fAc\u00185bI>|\u0007oQ8oM&<WO]1uS>tWCAA|!\u0011\tIP!\u0001\u000e\u0005\u0005m(b\u00012\u0002~*\u0019\u0011q \u0003\u0002\r!\fGm\\8q\u0013\u0011\u0011\u0019!a?\u0003\u001b\r{gNZ5hkJ\fG/[8o\u0011-\u00119\u0001\u0001a\u0001\u0002\u0004%IA!\u0003\u00021}C\u0017\rZ8pa\u000e{gNZ5hkJ\fG/[8o?\u0012*\u0017\u000fF\u0002P\u0005\u0017A!\"a\u000b\u0003\u0006\u0005\u0005\t\u0019AA|\u0011!\u0011y\u0001\u0001Q!\n\u0005]\u0018!F0iC\u0012|w\u000e]\"p]\u001aLw-\u001e:bi&|g\u000e\t\u0005\f\u0005'\u0001\u0001\u0019!a\u0001\n\u0013\u0011)\"A\b`Kb,7-\u001e;pe6+Wn\u001c:z+\t\u00119\u0002E\u0002\f\u00053I1Aa\u0007\r\u0005\rIe\u000e\u001e\u0005\f\u0005?\u0001\u0001\u0019!a\u0001\n\u0013\u0011\t#A\n`Kb,7-\u001e;pe6+Wn\u001c:z?\u0012*\u0017\u000fF\u0002P\u0005GA!\"a\u000b\u0003\u001e\u0005\u0005\t\u0019\u0001B\f\u0011!\u00119\u0003\u0001Q!\n\t]\u0011\u0001E0fq\u0016\u001cW\u000f^8s\u001b\u0016lwN]=!\u0011-\u0011Y\u0003\u0001a\u0001\u0002\u0004%IA!\f\u0002#}\u001b8\r[3ek2,'OQ1dW\u0016tG-\u0006\u0002\u00030A!\u0011\u0011\u000fB\u0019\u0013\u0011\u0011\u0019$a\u001d\u0003!M\u001b\u0007.\u001a3vY\u0016\u0014()Y2lK:$\u0007b\u0003B\u001c\u0001\u0001\u0007\t\u0019!C\u0005\u0005s\tQcX:dQ\u0016$W\u000f\\3s\u0005\u0006\u001c7.\u001a8e?\u0012*\u0017\u000fF\u0002P\u0005wA!\"a\u000b\u00036\u0005\u0005\t\u0019\u0001B\u0018\u0011!\u0011y\u0004\u0001Q!\n\t=\u0012AE0tG\",G-\u001e7fe\n\u000b7m[3oI\u0002B1Ba\u0011\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u0003F\u0005qq\f^1tWN\u001b\u0007.\u001a3vY\u0016\u0014XC\u0001B$!\u0011\t\tH!\u0013\n\t\t-\u00131\u000f\u0002\u000e)\u0006\u001c8nU2iK\u0012,H.\u001a:\t\u0017\t=\u0003\u00011AA\u0002\u0013%!\u0011K\u0001\u0013?R\f7o[*dQ\u0016$W\u000f\\3s?\u0012*\u0017\u000fF\u0002P\u0005'B!\"a\u000b\u0003N\u0005\u0005\t\u0019\u0001B$\u0011!\u00119\u0006\u0001Q!\n\t\u001d\u0013aD0uCN\\7k\u00195fIVdWM\u001d\u0011\t\u0017\tm\u0003\u00011AA\u0002\u0013%!QL\u0001\u0013?\",\u0017M\u001d;cK\u0006$(+Z2fSZ,'/\u0006\u0002\u0003`A!!\u0011\rB4\u001b\t\u0011\u0019GC\u0002\u0003f\t\t1A\u001d9d\u0013\u0011\u0011IGa\u0019\u0003\u001dI\u00038-\u00128ea>Lg\u000e\u001e*fM\"Y!Q\u000e\u0001A\u0002\u0003\u0007I\u0011\u0002B8\u0003Yy\u0006.Z1si\n,\u0017\r\u001e*fG\u0016Lg/\u001a:`I\u0015\fHcA(\u0003r!Q\u00111\u0006B6\u0003\u0003\u0005\rAa\u0018\t\u0011\tU\u0004\u0001)Q\u0005\u0005?\n1c\u00185fCJ$(-Z1u%\u0016\u001cW-\u001b<fe\u0002B1B!\u001f\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u0003|\u0005iq\fZ1h'\u000eDW\rZ;mKJ,\"A! \u0011\t\u0005E$qP\u0005\u0005\u0005\u0003\u000b\u0019H\u0001\u0007E\u0003\u001e\u001b6\r[3ek2,'\u000fC\u0006\u0003\u0006\u0002\u0001\r\u00111A\u0005\n\t\u001d\u0015!E0eC\u001e\u001c6\r[3ek2,'o\u0018\u0013fcR\u0019qJ!#\t\u0015\u0005-\"1QA\u0001\u0002\u0004\u0011i\b\u0003\u0005\u0003\u000e\u0002\u0001\u000b\u0015\u0002B?\u00039yF-Y4TG\",G-\u001e7fe\u0002BCAa#\u0003\u0012B\u00191Ba%\n\u0007\tUEB\u0001\u0005w_2\fG/\u001b7f\u0011-\u0011I\n\u0001a\u0001\u0002\u0004%IAa'\u0002\u001d}\u000b\u0007\u000f\u001d7jG\u0006$\u0018n\u001c8JIV\t\u0001\fC\u0006\u0003 \u0002\u0001\r\u00111A\u0005\n\t\u0005\u0016AE0baBd\u0017nY1uS>t\u0017\nZ0%KF$2a\u0014BR\u0011%\tYC!(\u0002\u0002\u0003\u0007\u0001\fC\u0004\u0003(\u0002\u0001\u000b\u0015\u0002-\u0002\u001f}\u000b\u0007\u000f\u001d7jG\u0006$\u0018n\u001c8JI\u0002B\u0011Ba+\u0001\u0001\u0004%I!!\u0017\u0002+}\u000b\u0007\u000f\u001d7jG\u0006$\u0018n\u001c8BiR,W\u000e\u001d;JI\"I!q\u0016\u0001A\u0002\u0013%!\u0011W\u0001\u001a?\u0006\u0004\b\u000f\\5dCRLwN\\!ui\u0016l\u0007\u000f^%e?\u0012*\u0017\u000fF\u0002P\u0005gC!\"a\u000b\u0003.\u0006\u0005\t\u0019AA.\u0011!\u00119\f\u0001Q!\n\u0005m\u0013AF0baBd\u0017nY1uS>t\u0017\t\u001e;f[B$\u0018\n\u001a\u0011\t\u0013\tm\u0006\u00011A\u0005\n\tu\u0016\u0001D0fm\u0016tG\u000fT8hO\u0016\u0014XC\u0001B`!\u0015Y\u0011\u0011\bBa!\u0011\t\tHa1\n\t\t\u0015\u00171\u000f\u0002\u0015\u000bZ,g\u000e\u001e'pO\u001eLgn\u001a'jgR,g.\u001a:\t\u0013\t%\u0007\u00011A\u0005\n\t-\u0017\u0001E0fm\u0016tG\u000fT8hO\u0016\u0014x\fJ3r)\ry%Q\u001a\u0005\u000b\u0003W\u00119-!AA\u0002\t}\u0006\u0002\u0003Bi\u0001\u0001\u0006KAa0\u0002\u001b}+g/\u001a8u\u0019><w-\u001a:!\u0011%\u0011)\u000e\u0001a\u0001\n\u0013\u00119.\u0001\u000e`Kb,7-\u001e;pe\u0006cGn\\2bi&|g.T1oC\u001e,'/\u0006\u0002\u0003ZB)1\"!\u000f\u0003\\B\u0019\u0011D!8\n\u0007\t}'AA\rFq\u0016\u001cW\u000f^8s\u00032dwnY1uS>tW*\u00198bO\u0016\u0014\b\"\u0003Br\u0001\u0001\u0007I\u0011\u0002Bs\u0003yyV\r_3dkR|'/\u00117m_\u000e\fG/[8o\u001b\u0006t\u0017mZ3s?\u0012*\u0017\u000fF\u0002P\u0005OD!\"a\u000b\u0003b\u0006\u0005\t\u0019\u0001Bm\u0011!\u0011Y\u000f\u0001Q!\n\te\u0017aG0fq\u0016\u001cW\u000f^8s\u00032dwnY1uS>tW*\u00198bO\u0016\u0014\b\u0005C\u0005\u0003p\u0002\u0001\r\u0011\"\u0003\u0003r\u0006Aql\u00197fC:,'/\u0006\u0002\u0003tB)1\"!\u000f\u0003vB\u0019\u0011Da>\n\u0007\te(A\u0001\bD_:$X\r\u001f;DY\u0016\fg.\u001a:\t\u0013\tu\b\u00011A\u0005\n\t}\u0018\u0001D0dY\u0016\fg.\u001a:`I\u0015\fHcA(\u0004\u0002!Q\u00111\u0006B~\u0003\u0003\u0005\rAa=\t\u0011\r\u0015\u0001\u0001)Q\u0005\u0005g\f\u0011bX2mK\u0006tWM\u001d\u0011\t\u0011\r%\u0001\u00011A\u0005\n9\n1c\u00187jgR,g.\u001a:CkN\u001cF/\u0019:uK\u0012D\u0011b!\u0004\u0001\u0001\u0004%Iaa\u0004\u0002/}c\u0017n\u001d;f]\u0016\u0014()^:Ti\u0006\u0014H/\u001a3`I\u0015\fHcA(\u0004\u0012!I\u00111FB\u0006\u0003\u0003\u0005\ra\f\u0005\b\u0007+\u0001\u0001\u0015)\u00030\u0003QyF.[:uK:,'OQ;t'R\f'\u000f^3eA!Y1\u0011\u0004\u0001A\u0002\u0003\u0007I\u0011BB\u000e\u0003\u0015y&.\u0019:t+\u0005Y\u0007bCB\u0010\u0001\u0001\u0007\t\u0019!C\u0005\u0007C\t\u0011b\u00186beN|F%Z9\u0015\u0007=\u001b\u0019\u0003C\u0005\u0002,\ru\u0011\u0011!a\u0001W\"91q\u0005\u0001!B\u0013Y\u0017AB0kCJ\u001c\b\u0005C\u0006\u0004,\u0001\u0001\r\u00111A\u0005\n\rm\u0011AB0gS2,7\u000fC\u0006\u00040\u0001\u0001\r\u00111A\u0005\n\rE\u0012AC0gS2,7o\u0018\u0013fcR\u0019qja\r\t\u0013\u0005-2QFA\u0001\u0002\u0004Y\u0007bBB\u001c\u0001\u0001\u0006Ka[\u0001\b?\u001aLG.Z:!\u0011-\u0019Y\u0004\u0001a\u0001\u0002\u0004%Ia!\u0010\u0002!}\u001b\b.\u001e;e_^t\u0007j\\8l%\u00164W#\u0001\u0006\t\u0017\r\u0005\u0003\u00011AA\u0002\u0013%11I\u0001\u0015?NDW\u000f\u001e3po:Dun\\6SK\u001a|F%Z9\u0015\u0007=\u001b)\u0005C\u0005\u0002,\r}\u0012\u0011!a\u0001\u0015!91\u0011\n\u0001!B\u0013Q\u0011!E0tQV$Hm\\<o\u0011>|7NU3gA!Y1Q\n\u0001A\u0002\u0003\u0007I\u0011BB(\u00031y6\u000f^1ukN\u001cFo\u001c:f+\t\u0019\t\u0006\u0005\u0003\u0004T\reSBAB+\u0015\r\u00199FA\u0001\u0007gR\fG/^:\n\t\rm3Q\u000b\u0002\u000f\u0003B\u00048\u000b^1ukN\u001cFo\u001c:f\u0011-\u0019y\u0006\u0001a\u0001\u0002\u0004%Ia!\u0019\u0002!}\u001bH/\u0019;vgN#xN]3`I\u0015\fHcA(\u0004d!Q\u00111FB/\u0003\u0003\u0005\ra!\u0015\t\u0011\r\u001d\u0004\u0001)Q\u0005\u0007#\nQbX:uCR,8o\u0015;pe\u0016\u0004\u0003b\u00022\u0001\t\u0003\u0011\u0011\u0011\u0005\u0005\b\u0007[\u0002A\u0011AA\u0011\u0003\u001d9W\r^\"p]\u001aDaA\u001b\u0001\u0005\u0002\rm\u0001bBB:\u0001\u0011\u000511D\u0001\u0006M&dWm\u001d\u0005\u0007/\u0002!\tAa'\t\u000f\re\u0004\u0001\"\u0001\u0003\u001c\u0006QA-\u001a9m_flu\u000eZ3\t\r\u0001\u0004A\u0011\u0001BN\u0011\u001d\u0019y\b\u0001C\u0001\u00059\n\u0011#[:Fm\u0016tG\u000fT8h\u000b:\f'\r\\3e\u0011!\u0019\u0019\t\u0001C\u0001\u0005\u0005U\u0012aC3wK:$Hj\\4ESJD\u0001ba\"\u0001\t\u0003\u0011\u0011\u0011L\u0001\u000eKZ,g\u000e\u001e'pO\u000e{G-Z2\t\r\r-\u0005\u0001\"\u0001/\u0003\u001dI7\u000fT8dC2Daaa$\u0001\t\u0003q\u0013!C5t'R|\u0007\u000f]3e\u0011!\u0019\u0019\n\u0001C\u0001\u0005\r=\u0013aC:uCR,8o\u0015;pe\u0016D\u0001ba&\u0001\t\u0003\u0011\u0011QN\u0001\fY&\u001cH/\u001a8fe\n+8\u000f\u0003\u0005\u0004\u001c\u0002!\tAABO\u00039\u0019'/Z1uKN\u0003\u0018M]6F]Z$\u0002\"!$\u0004 \u000e\u000561\u0015\u0005\u0007E\u000ee\u0005\u0019\u0001\r\t\u000f\r-5\u0011\u0014a\u0001_!A1qSBM\u0001\u0004\ty\u0007\u0003\u0005\u0004(\u0002!\tAAAF\u0003\r)gN\u001e\u0005\u000b\u0007W\u0003!\u0019!C\u0001\u0005\r5\u0016AC1eI\u0016$g)\u001b7fgV\u00111q\u0016\t\u0007\u0007c\u001b)\fW\u001c\u000e\u0005\rM&B\u0001#|\u0013\rq81\u0017\u0005\t\u0007s\u0003\u0001\u0015!\u0003\u00040\u0006Y\u0011\r\u001a3fI\u001aKG.Z:!\u0011)\u0019i\f\u0001b\u0001\n\u0003\u00111QV\u0001\nC\u0012$W\r\u001a&beND\u0001b!1\u0001A\u0003%1qV\u0001\u000bC\u0012$W\r\u001a&beN\u0004\u0003BCBc\u0001\t\u0007I\u0011\u0001\u0002\u0004H\u0006q\u0001/\u001a:tSN$XM\u001c;SI\u0012\u001cXCABe!!\u0019\tl!.\u0003\u0018\r-\u0007\u0007BBg\u0007;\u0004baa4\u0004V\u000eeWBABi\u0015\r\u0019\u0019NA\u0001\u0004e\u0012$\u0017\u0002BBl\u0007#\u00141A\u0015#E!\u0011\u0019Yn!8\r\u0001\u0011a1q\\Bq\u0003\u0003\u0005\tQ!\u0001\u0004r\n\u0019q\fJ\u0019\t\u000f\r\r8Q\u001d\u0001\u0004j\u0006\u0019Q.\u00199\t\u0011\r\u001d\b\u0001)A\u0005\u0007\u0013\fq\u0002]3sg&\u001cH/\u001a8u%\u0012$7\u000f\t\t\t\u0007W\u001ciOa\u0006\u0004L6\t1)C\u0002\u0004p\u000e\u0013QbQ8oGV\u0014(/\u001a8u\u001b\u0006\u0004\u0018\u0003BBz\u0007s\u00042aCB{\u0013\r\u00199\u0010\u0004\u0002\b\u001d>$\b.\u001b8h!\rY11`\u0005\u0004\u0007{d!aA!os\"9A\u0011\u0001\u0001\u0005\u0002\u0005\r\u0016!D:uCR,8\u000f\u0016:bG.,'\u000f\u0003\u0005\u0005\u0006\u0001!\tAAA^\u0003-\u0001(o\\4sKN\u001c()\u0019:\t\u0011\u0005\u0015\u0007\u0001\"\u0001\u0003\u00037Dq\u0001b\u0003\u0001\t\u0003\tI&\u0001\u0005vS^+'-\u0016:m\u0011\u001d!y\u0001\u0001C\u0001\u0003k\f1\u0003[1e_>\u00048i\u001c8gS\u001e,(/\u0019;j_:D\u0001\u0002b\u0005\u0001\t\u0003\u0011!QC\u0001\u000fKb,7-\u001e;pe6+Wn\u001c:z\u0011)!9\u0002\u0001b\u0001\n\u0003\u0011A\u0011D\u0001\rKb,7-\u001e;pe\u0016sgo]\u000b\u0003\t7\u0001b\u0001\"\b\u0005$aCVB\u0001C\u0010\u0015\r!\tc_\u0001\b[V$\u0018M\u00197f\u0013\u0011!)\u0003b\b\u0003\u000f!\u000b7\u000f['ba\"AA\u0011\u0006\u0001!\u0002\u0013!Y\"A\u0007fq\u0016\u001cW\u000f^8s\u000b:48\u000f\t\u0005\n\t[\u0001!\u0019!C\u0001\u00057\u000b\u0011b\u001d9be.,6/\u001a:\t\u000f\u0011E\u0002\u0001)A\u00051\u0006Q1\u000f]1sWV\u001bXM\u001d\u0011\t\u0011\u0011U\u0002\u0001\"\u0001\u0003\u0005[\t\u0001c]2iK\u0012,H.\u001a:CC\u000e\\WM\u001c3\t\u0011\u0011e\u0002\u0001\"\u0001\u0003\u0005\u000b\nQ\u0002^1tWN\u001b\u0007.\u001a3vY\u0016\u0014\b\u0002\u0003C\u001f\u0001\u0011\u0005!\u0001b\u0010\u0002#Q\f7o[*dQ\u0016$W\u000f\\3s?\u0012*\u0017\u000fF\u0002P\t\u0003B\u0001\u0002b\u0011\u0005<\u0001\u0007!qI\u0001\u0003iND\u0001\u0002b\u0012\u0001\t\u0003\u0011!1P\u0001\rI\u0006<7k\u00195fIVdWM\u001d\u0005\t\t\u0017\u0002A\u0011\u0001\u0002\u0005N\u0005\u0001B-Y4TG\",G-\u001e7fe~#S-\u001d\u000b\u0004\u001f\u0012=\u0003\u0002\u0003C)\t\u0013\u0002\rA! \u0002\u0005\u0011\u001c\bb\u0002C+\u0001\u0011\u0005!1T\u0001\u000eCB\u0004H.[2bi&|g.\u00133\t\u000f\u0011e\u0003\u0001\"\u0001\u0002Z\u0005!\u0012\r\u001d9mS\u000e\fG/[8o\u0003R$X-\u001c9u\u0013\u0012D\u0001\u0002\"\u0018\u0001\t\u0003\u0011!QX\u0001\fKZ,g\u000e\u001e'pO\u001e,'\u000f\u0003\u0005\u0005b\u0001!\tA\u0001Bl\u0003e)\u00070Z2vi>\u0014\u0018\t\u001c7pG\u0006$\u0018n\u001c8NC:\fw-\u001a:\t\u0011\u0011\u0015\u0004\u0001\"\u0001\u0003\u0005c\fqa\u00197fC:,'\u000f\u0003\u0006\u0005j\u0001\u0001\r\u0011\"\u0001\u0003\u00033\nQb\u00195fG.\u0004x.\u001b8u\t&\u0014\bB\u0003C7\u0001\u0001\u0007I\u0011\u0001\u0002\u0005p\u0005\t2\r[3dWB|\u0017N\u001c;ESJ|F%Z9\u0015\u0007=#\t\b\u0003\u0006\u0002,\u0011-\u0014\u0011!a\u0001\u00037B\u0001\u0002\"\u001e\u0001A\u0003&\u00111L\u0001\u000fG\",7m\u001b9pS:$H)\u001b:!\u0011)!I\b\u0001b\u0001\n#\u0011A1P\u0001\u0010Y>\u001c\u0017\r\u001c)s_B,'\u000f^5fgV\u0011AQ\u0010\t\u0007\t\u007f\")\t\"#\u000e\u0005\u0011\u0005%b\u0001CB\r\u0006!A.\u00198h\u0013\u0011!9\t\"!\u0003-%s\u0007.\u001a:ji\u0006\u0014G.\u001a+ie\u0016\fG\rT8dC2\u0004B\u0001b#\u0005\u000e6\tQ)C\u0002\u0005\u0010\u0016\u0013!\u0002\u0015:pa\u0016\u0014H/[3t\u0011!!\u0019\n\u0001Q\u0001\n\u0011u\u0014\u0001\u00057pG\u0006d\u0007K]8qKJ$\u0018.Z:!\u0011\u001d!9\n\u0001C\u0005\t3\u000bAb^1s]N\u0003\u0018M]6NK6$2\u0001\u0017CN\u0011\u001d!i\n\"&A\u0002a\u000bQA^1mk\u0016Dq\u0001\")\u0001\t\u0003!\u0019+A\u0006tKRdun\u001a'fm\u0016dGcA(\u0005&\"9Aq\u0015CP\u0001\u0004A\u0016\u0001\u00037pO2+g/\u001a7\t\u0011\u0011-\u0006\u0001\"\u0001\u0003\t[\u000bQcZ3u\u000bb,7-\u001e;peRC'/Z1e\tVl\u0007\u000f\u0006\u0003\u00050\u0012u\u0006#B\u0006\u0002:\u0011E\u0006#B\u0006\u00054\u0012]\u0016b\u0001C[\u0019\t)\u0011I\u001d:bsB\u0019Q\u0005\"/\n\u0007\u0011mfE\u0001\tUQJ,\u0017\rZ*uC\u000e\\GK]1dK\"9Aq\u0018CU\u0001\u0004A\u0016AC3yK\u000e,Ho\u001c:JI\"AA1\u0019\u0001\u0005\u0002\t!)-\u0001\nhKRdunY1m!J|\u0007/\u001a:uS\u0016\u001cXC\u0001CE\u0011!!I\r\u0001C\u0001\u0005\u0011-\u0017AE:fi2{7-\u00197Qe>\u0004XM\u001d;jKN$2a\u0014Cg\u0011!!y\rb2A\u0002\u0011%\u0015!\u00029s_B\u001c\bb\u0002Cj\u0001\u0011\u0005AQ[\u0001\u0011g\u0016$Hj\\2bYB\u0013x\u000e]3sif$Ra\u0014Cl\t7Dq\u0001\"7\u0005R\u0002\u0007\u0001,A\u0002lKfDq\u0001\"(\u0005R\u0002\u0007\u0001\fC\u0004\u0005`\u0002!\t\u0001\"9\u0002!\u001d,G\u000fT8dC2\u0004&o\u001c9feRLHc\u0001-\u0005d\"9A\u0011\u001cCo\u0001\u0004A\u0006b\u0002Ct\u0001\u0011\u0005A\u0011^\u0001\u0012g\u0016$(j\u001c2EKN\u001c'/\u001b9uS>tGcA(\u0005l\"9AQ\u0014Cs\u0001\u0004A\u0006b\u0002Cx\u0001\u0011\u0005A\u0011_\u0001\fg\u0016$(j\u001c2He>,\b\u000fF\u0004P\tg$9\u0010b?\t\u000f\u0011UHQ\u001ea\u00011\u00069qM]8va&#\u0007b\u0002C}\t[\u0004\r\u0001W\u0001\fI\u0016\u001c8M]5qi&|g\u000eC\u0005\u0005~\u00125\b\u0013!a\u0001_\u0005\t\u0012N\u001c;feJ,\b\u000f^(o\u0007\u0006t7-\u001a7\t\r\u0015\u0005\u0001\u0001\"\u0001O\u00035\u0019G.Z1s\u0015>\u0014wI]8va\"AQQ\u0001\u0001\u0005\u0002\t)9!A\u0005xSRD7kY8qKV!Q\u0011BC\u0007)\u0011)Y!\"\u0005\u0011\t\rmWQ\u0002\u0003\t\u000b\u001f)\u0019A1\u0001\u0004r\n\tQ\u000bC\u0005\u0006\u0014\u0015\rA\u00111\u0001\u0006\u0016\u0005!!m\u001c3z!\u0015YQqCC\u0006\u0013\r)I\u0002\u0004\u0002\ty\tLh.Y7f}!9QQ\u0004\u0001\u0005\u0002\u0015}\u0011a\u00039be\u0006dG.\u001a7ju\u0016,B!\"\t\u0006*Q1Q1EC\u001f\u000b\u0007\"B!\"\n\u0006.A11qZBk\u000bO\u0001Baa7\u0006*\u0011AQ1FC\u000e\u0005\u0004\u0019\tPA\u0001U\u0011))y#b\u0007\u0002\u0002\u0003\u000fQ\u0011G\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004CBC\u001a\u000bs)9#\u0004\u0002\u00066)\u0019Qq\u0007\u0007\u0002\u000fI,g\r\\3di&!Q1HC\u001b\u0005!\u0019E.Y:t)\u0006<\u0007\u0002CC \u000b7\u0001\r!\"\u0011\u0002\u0007M,\u0017\u000f\u0005\u0003mi\u0016\u001d\u0002BCC#\u000b7\u0001\n\u00111\u0001\u0003\u0018\u0005Ia.^7TY&\u001cWm\u001d\u0005\b\u000b\u0013\u0002A\u0011AC&\u0003\u0015\u0011\u0018M\\4f)))i%b\u0014\u0006T\u0015]S1\f\t\u0006\u0007\u001f\u001c)n\u000e\u0005\b\u000b#*9\u00051\u00018\u0003\u0015\u0019H/\u0019:u\u0011\u001d))&b\u0012A\u0002]\n1!\u001a8e\u0011%)I&b\u0012\u0011\u0002\u0003\u0007q'\u0001\u0003ti\u0016\u0004\bBCC#\u000b\u000f\u0002\n\u00111\u0001\u0003\u0018!9Qq\f\u0001\u0005\u0002\u0015\u0005\u0014aB7bW\u0016\u0014F\tR\u000b\u0005\u000bG*Y\u0007\u0006\u0004\u0006f\u0015MTq\u000f\u000b\u0005\u000bO*i\u0007\u0005\u0004\u0004P\u000eUW\u0011\u000e\t\u0005\u00077,Y\u0007\u0002\u0005\u0006,\u0015u#\u0019ABy\u0011))y'\"\u0018\u0002\u0002\u0003\u000fQ\u0011O\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004CBC\u001a\u000bs)I\u0007\u0003\u0005\u0006@\u0015u\u0003\u0019AC;!\u0011aG/\"\u001b\t\u0015\u0015\u0015SQ\fI\u0001\u0002\u0004\u00119\u0002C\u0004\u0006`\u0001!\t!b\u001f\u0016\t\u0015uTQ\u0011\u000b\u0005\u000b\u007f*i\t\u0006\u0003\u0006\u0002\u0016\u001d\u0005CBBh\u0007+,\u0019\t\u0005\u0003\u0004\\\u0016\u0015E\u0001CC\u0016\u000bs\u0012\ra!=\t\u0015\u0015%U\u0011PA\u0001\u0002\b)Y)\u0001\u0006fm&$WM\\2fIM\u0002b!b\r\u0006:\u0015\r\u0005\u0002CC \u000bs\u0002\r!b$\u0011\t1$X\u0011\u0013\t\u0007\u0017\u0015MU1Q6\n\u0007\u0015UEB\u0001\u0004UkBdWM\r\u0005\b\u000b3\u0003A\u0011ACN\u0003!!X\r\u001f;GS2,GCBCO\u000b?+\u0019\u000bE\u0003\u0004P\u000eU\u0007\fC\u0004\u0006\"\u0016]\u0005\u0019\u0001-\u0002\tA\fG\u000f\u001b\u0005\u000b\u000bK+9\n%AA\u0002\t]\u0011!D7j]B\u000b'\u000f^5uS>t7\u000fC\u0004\u0006*\u0002!\t!b+\u0002\u001d]Dw\u000e\\3UKb$h)\u001b7fgR1QQVCY\u000bg\u0003baa4\u0004V\u0016=\u0006#B\u0006\u0006\u0014bC\u0006bBCQ\u000bO\u0003\r\u0001\u0017\u0005\u000b\u000bK+9\u000b%AA\u0002\t]\u0001bBC\\\u0001\u0011\u0005Q\u0011X\u0001\fE&t\u0017M]=GS2,7\u000f\u0006\u0004\u0006<\u0016-WQ\u001a\t\u0007\u0007\u001f\u001c).\"0\u0011\r-)\u0019\nWC`!\u0011)\t-b2\u000e\u0005\u0015\r'bACc\u0005\u0005)\u0011N\u001c9vi&!Q\u0011ZCb\u0005I\u0001vN\u001d;bE2,G)\u0019;b'R\u0014X-Y7\t\u000f\u0015\u0005VQ\u0017a\u00011\"QQQUC[!\u0003\u0005\rAa\u0006\t\u000f\u0015E\u0007\u0001\"\u0001\u0006T\u0006i!-\u001b8bef\u0014VmY8sIN$\u0002\"\"6\u0006`\u0016\u0005XQ\u001d\t\u0007\u0007\u001f\u001c).b6\u0011\u000b-!\u0019,\"7\u0011\u0007-)Y.C\u0002\u0006^2\u0011AAQ=uK\"9Q\u0011UCh\u0001\u0004A\u0006\u0002CCr\u000b\u001f\u0004\rAa\u0006\u0002\u0019I,7m\u001c:e\u0019\u0016tw\r\u001e5\t\u0013\t,y\r%AA\u0002\u0005]\bbBCu\u0001\u0011\u0005Q1^\u0001\nQ\u0006$wn\u001c9S\t\u0012+b!\"<\u0006v\u0016mH\u0003DCx\u000b\u007f4iAb\n\u0007.\u0019M\u0002CBBh\u0007+,\t\u0010E\u0004\f\u000b'+\u00190\"?\u0011\t\rmWQ\u001f\u0003\t\u000bo,9O1\u0001\u0004r\n\t1\n\u0005\u0003\u0004\\\u0016mH\u0001CC\u007f\u000bO\u0014\ra!=\u0003\u0003YCqAYCt\u0001\u00041\t\u0001\u0005\u0003\u0007\u0004\u0019%QB\u0001D\u0003\u0015\u001119!!@\u0002\r5\f\u0007O]3e\u0013\u00111YA\"\u0002\u0003\u000f){'mQ8oM\"AaqBCt\u0001\u00041\t\"\u0001\tj]B,HOR8s[\u0006$8\t\\1tgB\"a1\u0003D\u000e!\u0015IfQ\u0003D\r\u0013\r19B\u0018\u0002\u0006\u00072\f7o\u001d\t\u0005\u000774Y\u0002\u0002\u0007\u0007\u001e\u00195\u0011\u0011!A\u0001\u0006\u00031yBA\u0002`IM\nBaa=\u0007\"AAa1\u0001D\u0012\u000bg,I0\u0003\u0003\u0007&\u0019\u0015!aC%oaV$hi\u001c:nCRD\u0001B\"\u000b\u0006h\u0002\u0007a1F\u0001\tW\u0016L8\t\\1tgB)\u0011L\"\u0006\u0006t\"AaqFCt\u0001\u00041\t$\u0001\u0006wC2,Xm\u00117bgN\u0004R!\u0017D\u000b\u000bsD!\"\"*\u0006hB\u0005\t\u0019\u0001B\f\u0011\u001d19\u0004\u0001C\u0001\rs\t!\u0002[1e_>\u0004h)\u001b7f+\u00191YDb\u0011\u0007HQaaQ\bD%\r\u00172YFb\u0018\u0007dA11qZBk\r\u007f\u0001raCCJ\r\u00032)\u0005\u0005\u0003\u0004\\\u001a\rC\u0001CC|\rk\u0011\ra!=\u0011\t\rmgq\t\u0003\t\u000b{4)D1\u0001\u0004r\"9Q\u0011\u0015D\u001b\u0001\u0004A\u0006\u0002\u0003D\b\rk\u0001\rA\"\u00141\t\u0019=c1\u000b\t\u00063\u001aUa\u0011\u000b\t\u0005\u000774\u0019\u0006\u0002\u0007\u0007V\u0019-\u0013\u0011!A\u0001\u0006\u000319FA\u0002`IQ\nBaa=\u0007ZAAa1\u0001D\u0012\r\u00032)\u0005\u0003\u0005\u0007*\u0019U\u0002\u0019\u0001D/!\u0015IfQ\u0003D!\u0011!1yC\"\u000eA\u0002\u0019\u0005\u0004#B-\u0007\u0016\u0019\u0015\u0003BCCS\rk\u0001\n\u00111\u0001\u0003\u0018!9aq\u0007\u0001\u0005\u0002\u0019\u001dT\u0003\u0003D5\rg29H\"$\u0015\r\u0019-dQ\u0013DL)!1iG\"\u001f\u0007\u0000\u0019\u0015\u0005CBBh\u0007+4y\u0007E\u0004\f\u000b'3\tH\"\u001e\u0011\t\rmg1\u000f\u0003\t\u000bo4)G1\u0001\u0004rB!11\u001cD<\t!)iP\"\u001aC\u0002\rE\b\u0002\u0003D>\rK\u0002\u001dA\" \u0002\u0005-l\u0007CBC\u001a\u000bs1\t\b\u0003\u0005\u0007\u0002\u001a\u0015\u00049\u0001DB\u0003\t1X\u000e\u0005\u0004\u00064\u0015ebQ\u000f\u0005\t\r\u000f3)\u0007q\u0001\u0007\n\u0006\u0011a-\u001c\t\u0007\u000bg)IDb#\u0011\t\rmgQ\u0012\u0003\t\r\u001f3)G1\u0001\u0007\u0012\n\ta)\u0005\u0003\u0004t\u001aM\u0005\u0003\u0003D\u0002\rG1\tH\"\u001e\t\u000f\u0015\u0005fQ\ra\u00011\"AQQ\u0015D3\u0001\u0004\u00119\u0002C\u0004\u00078\u0001!\tAb'\u0016\u0011\u0019ueq\u0015DV\rw#BAb(\u0007BRAa\u0011\u0015DW\rc3)\f\u0005\u0004\u0004P\u000eUg1\u0015\t\b\u0017\u0015MeQ\u0015DU!\u0011\u0019YNb*\u0005\u0011\u0015]h\u0011\u0014b\u0001\u0007c\u0004Baa7\u0007,\u0012AQQ DM\u0005\u0004\u0019\t\u0010\u0003\u0005\u0007|\u0019e\u00059\u0001DX!\u0019)\u0019$\"\u000f\u0007&\"Aa\u0011\u0011DM\u0001\b1\u0019\f\u0005\u0004\u00064\u0015eb\u0011\u0016\u0005\t\r\u000f3I\nq\u0001\u00078B1Q1GC\u001d\rs\u0003Baa7\u0007<\u0012Aaq\u0012DM\u0005\u00041i,\u0005\u0003\u0004t\u001a}\u0006\u0003\u0003D\u0002\rG1)K\"+\t\u000f\u0015\u0005f\u0011\u0014a\u00011\"9aQ\u0019\u0001\u0005\u0002\u0019\u001d\u0017\u0001\u00058fo\u0006\u0003\u0016\nS1e_>\u0004h)\u001b7f+!1IMb5\u0007X\u001a\u001dH\u0003\u0002Df\rk$\u0002B\"4\u0007Z\u001aug\u0011\u001d\t\u0007\u0007\u001f\u001c)Nb4\u0011\u000f-)\u0019J\"5\u0007VB!11\u001cDj\t!)9Pb1C\u0002\rE\b\u0003BBn\r/$\u0001\"\"@\u0007D\n\u00071\u0011\u001f\u0005\t\rw2\u0019\rq\u0001\u0007\\B1Q1GC\u001d\r#D\u0001B\"!\u0007D\u0002\u000faq\u001c\t\u0007\u000bg)ID\"6\t\u0011\u0019\u001de1\u0019a\u0002\rG\u0004b!b\r\u0006:\u0019\u0015\b\u0003BBn\rO$\u0001Bb$\u0007D\n\u0007a\u0011^\t\u0005\u0007g4Y\u000f\u0005\u0005\u0007n\u001aMh\u0011\u001bDk\u001b\t1yO\u0003\u0003\u0007r\u0006u\u0018!C7baJ,G-^2f\u0013\u00111)Cb<\t\u000f\u0015\u0005f1\u0019a\u00011\"9aQ\u0019\u0001\u0005\u0002\u0019eX\u0003\u0003D~\u000f\u000799ab\u0005\u0015\u0019\u0019ux\u0011BD\u0006\u000f39yb\"\n\u0011\r\r=7Q\u001bD\u0000!\u001dYQ1SD\u0001\u000f\u000b\u0001Baa7\b\u0004\u0011AQq\u001fD|\u0005\u0004\u0019\t\u0010\u0005\u0003\u0004\\\u001e\u001dA\u0001CC\u007f\ro\u0014\ra!=\t\u000f\u0015\u0005fq\u001fa\u00011\"AqQ\u0002D|\u0001\u00049y!\u0001\u0004g\u00072\f7o\u001d\t\u00063\u001aUq\u0011\u0003\t\u0005\u00077<\u0019\u0002\u0002\u0005\u0007\u0010\u001a](\u0019AD\u000b#\u0011\u0019\u0019pb\u0006\u0011\u0011\u00195h1_D\u0001\u000f\u000bA\u0001bb\u0007\u0007x\u0002\u0007qQD\u0001\u0007W\u000ec\u0017m]:\u0011\u000be3)b\"\u0001\t\u0011\u001d\u0005bq\u001fa\u0001\u000fG\taA^\"mCN\u001c\b#B-\u0007\u0016\u001d\u0015\u0001\"\u00032\u0007xB\u0005\t\u0019AA|\u0011\u001d9I\u0003\u0001C\u0001\u000fW\tqB\\3x\u0003BK\u0005*\u00193p_B\u0014F\tR\u000b\t\u000f[9)d\"\u000f\bDQQqqFD\u001e\u000f{9Ie\"\u0014\u0011\r\r=7Q[D\u0019!\u001dYQ1SD\u001a\u000fo\u0001Baa7\b6\u0011AQq_D\u0014\u0005\u0004\u0019\t\u0010\u0005\u0003\u0004\\\u001eeB\u0001CC\u007f\u000fO\u0011\ra!=\t\u0013\t<9\u0003%AA\u0002\u0005]\b\u0002CD\u0007\u000fO\u0001\rab\u0010\u0011\u000be3)b\"\u0011\u0011\t\rmw1\t\u0003\t\r\u001f;9C1\u0001\bFE!11_D$!!1iOb=\b4\u001d]\u0002\u0002CD\u000e\u000fO\u0001\rab\u0013\u0011\u000be3)bb\r\t\u0011\u001d\u0005rq\u0005a\u0001\u000f\u001f\u0002R!\u0017D\u000b\u000foAqab\u0015\u0001\t\u00039)&\u0001\u0007tKF,XM\\2f\r&dW-\u0006\u0004\bX\u001d}s1\r\u000b\u000b\u000f3:)gb\u001a\bl\u001d=\u0004CBBh\u0007+<Y\u0006E\u0004\f\u000b';if\"\u0019\u0011\t\rmwq\f\u0003\t\u000bo<\tF1\u0001\u0004rB!11\\D2\t!)ip\"\u0015C\u0002\rE\bbBCQ\u000f#\u0002\r\u0001\u0017\u0005\t\rS9\t\u00061\u0001\bjA)\u0011L\"\u0006\b^!AaqFD)\u0001\u00049i\u0007E\u0003Z\r+9\t\u0007\u0003\u0005\u0006&\u001eE\u0003\u0019\u0001B\f\u0011\u001d9\u0019\u0006\u0001C\u0001\u000fg*ba\"\u001e\b~\u001d\u0005E\u0003CD<\u000f\u0007;)i\"#\u0011\r\r=7Q[D=!\u001dYQ1SD>\u000f\u007f\u0002Baa7\b~\u0011AQq_D9\u0005\u0004\u0019\t\u0010\u0005\u0003\u0004\\\u001e\u0005E\u0001CC\u007f\u000fc\u0012\ra!=\t\u000f\u0015\u0005v\u0011\u000fa\u00011\"Aa\u0011FD9\u0001\u000499\tE\u0003Z\r+9Y\b\u0003\u0005\u00070\u001dE\u0004\u0019ADF!\u0015IfQCD@\u0011\u001d9\u0019\u0006\u0001C\u0001\u000f\u001f+ba\"%\b\u001c\u001e}ECBDJ\u000f\u0003<\u0019\r\u0006\u0006\b\u0016\u001e\u0005vQUDU\u000fs\u0003baa4\u0004V\u001e]\u0005cB\u0006\u0006\u0014\u001eeuQ\u0014\t\u0005\u00077<Y\n\u0002\u0005\u0006x\u001e5%\u0019ABy!\u0011\u0019Ynb(\u0005\u0011\u0015uxQ\u0012b\u0001\u0007cD\u0001Bb\u001f\b\u000e\u0002\u000fq1\u0015\t\u0007\u000bg)Id\"'\t\u0011\u0019\u0005uQ\u0012a\u0002\u000fO\u0003b!b\r\u0006:\u001du\u0005\u0002CDV\u000f\u001b\u0003\u001da\",\u0002\u0007-\u001cg\rE\u0003\f\u000f_;\u0019,C\u0002\b22\u0011\u0011BR;oGRLwN\u001c\u0019\u0011\u000be9)l\"'\n\u0007\u001d]&AA\tXe&$\u0018M\u00197f\u0007>tg/\u001a:uKJD\u0001bb/\b\u000e\u0002\u000fqQX\u0001\u0004m\u000e4\u0007#B\u0006\b0\u001e}\u0006#B\r\b6\u001eu\u0005bBCQ\u000f\u001b\u0003\r\u0001\u0017\u0005\u000b\u000bK;i\t%AA\u0002\t]\u0001bBDd\u0001\u0011\u0005q\u0011Z\u0001\u000b_\nTWm\u0019;GS2,W\u0003BDf\u000f'$ba\"4\b\\\u001euG\u0003BDh\u000f+\u0004baa4\u0004V\u001eE\u0007\u0003BBn\u000f'$\u0001\"b\u000b\bF\n\u00071\u0011\u001f\u0005\u000b\u000f/<)-!AA\u0004\u001de\u0017AC3wS\u0012,gnY3%iA1Q1GC\u001d\u000f#Dq!\")\bF\u0002\u0007\u0001\f\u0003\u0006\u0006&\u001e\u0015\u0007\u0013!a\u0001\u0005/A\u0001b\"9\u0001\t#\u0011q1]\u0001\u000fG\",7m\u001b9pS:$h)\u001b7f+\u00119)o\"<\u0015\t\u001d\u001dxQ\u001f\u000b\u0005\u000fS<y\u000f\u0005\u0004\u0004P\u000eUw1\u001e\t\u0005\u00077<i\u000f\u0002\u0005\u0006,\u001d}'\u0019ABy\u0011)9\tpb8\u0002\u0002\u0003\u000fq1_\u0001\u000bKZLG-\u001a8dK\u0012*\u0004CBC\u001a\u000bs9Y\u000fC\u0004\u0006\"\u001e}\u0007\u0019\u0001-\t\u000f\u001de\b\u0001\"\u0001\b|\u0006)QO\\5p]V!qQ E\u0003)\u00119y\u0010#\u0004\u0015\t!\u0005\u0001r\u0001\t\u0007\u0007\u001f\u001c)\u000ec\u0001\u0011\t\rm\u0007R\u0001\u0003\t\u000bW99P1\u0001\u0004r\"Q\u0001\u0012BD|\u0003\u0003\u0005\u001d\u0001c\u0003\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$c\u0007\u0005\u0004\u00064\u0015e\u00022\u0001\u0005\t\u0011\u001f99\u00101\u0001\t\u0012\u0005!!\u000f\u001a3t!\u0011aG\u000f#\u0001\t\u000f\u001de\b\u0001\"\u0001\t\u0016U!\u0001r\u0003E\u0010)\u0019AI\u0002c\n\t,Q!\u00012\u0004E\u0011!\u0019\u0019ym!6\t\u001eA!11\u001cE\u0010\t!)Y\u0003c\u0005C\u0002\rE\bB\u0003E\u0012\u0011'\t\t\u0011q\u0001\t&\u0005QQM^5eK:\u001cW\rJ\u001c\u0011\r\u0015MR\u0011\bE\u000f\u0011!AI\u0003c\u0005A\u0002!m\u0011!\u00024jeN$\b\u0002\u0003E\u0017\u0011'\u0001\r\u0001c\f\u0002\tI,7\u000f\u001e\t\u0006\u0017!E\u00022D\u0005\u0004\u0011ga!A\u0003\u001fsKB,\u0017\r^3e}!9\u0001r\u0007\u0001\u0005\u0002!e\u0012\u0001C3naRL(\u000b\u0012#\u0016\t!m\u0002\u0012\t\u000b\u0005\u0011{A\u0019\u0005\u0005\u0004\u0004P\u000eU\u0007r\b\t\u0005\u00077D\t\u0005\u0002\u0005\u0006,!U\"\u0019ABy\u0011)A)\u0005#\u000e\u0002\u0002\u0003\u000f\u0001rI\u0001\u000bKZLG-\u001a8dK\u0012B\u0004CBC\u001a\u000bsAy\u0004C\u0004\tL\u0001!\t\u0001#\u0014\u0002\u0017\u0005\u001c7-^7vY\u0006$xN]\u000b\u0005\u0011\u001fBY\u0006\u0006\u0003\tR!\u001dD\u0003\u0002E*\u0011;\u0002R!\u0007E+\u00113J1\u0001c\u0016\u0003\u0005-\t5mY;nk2\fGo\u001c:\u0011\t\rm\u00072\f\u0003\t\u000bWAIE1\u0001\u0004r\"A\u0001r\fE%\u0001\bA\t'A\u0003qCJ\fW\u000eE\u0003\u001a\u0011GBI&C\u0002\tf\t\u0011\u0001#Q2dk6,H.\u0019;peB\u000b'/Y7\t\u0011!%\u0004\u0012\na\u0001\u00113\nA\"\u001b8ji&\fGNV1mk\u0016D\u0003\u0002#\u0013\tn!M\u0004r\u000f\t\u0004\u0017!=\u0014b\u0001E9\u0019\tQA-\u001a9sK\u000e\fG/\u001a3\"\u0005!U\u0014!E;tK\u0002\n5mY;nk2\fGo\u001c:We\u0005\u0012\u0001\u0012P\u0001\u0006e9\u0002d\u0006\r\u0005\b\u0011\u0017\u0002A\u0011\u0001E?+\u0011Ay\bc\"\u0015\r!\u0005\u0005R\u0012EH)\u0011A\u0019\t##\u0011\u000beA)\u0006#\"\u0011\t\rm\u0007r\u0011\u0003\t\u000bWAYH1\u0001\u0004r\"A\u0001r\fE>\u0001\bAY\tE\u0003\u001a\u0011GB)\t\u0003\u0005\tj!m\u0004\u0019\u0001EC\u0011\u001dA\t\nc\u001fA\u0002a\u000bAA\\1nK\"B\u00012\u0010E7\u0011gB9\bC\u0004\t\u0018\u0002!\t\u0001#'\u0002\u0017\u0005\u001c7-^7vY\u0006\u0014G.Z\u000b\u0007\u00117C9\u000b#,\u0015\t!u\u0005r\u0017\u000b\u0005\u0011?Cy\u000bE\u0004\u001a\u0011CC)\u000bc+\n\u0007!\r&AA\u0006BG\u000e,X.\u001e7bE2,\u0007\u0003BBn\u0011O#\u0001\u0002#+\t\u0016\n\u00071\u0011\u001f\u0002\u0002%B!11\u001cEW\t!)Y\u0003#&C\u0002\rE\b\u0002\u0003E0\u0011+\u0003\u001d\u0001#-\u0011\u000feA\u0019\f#*\t,&\u0019\u0001R\u0017\u0002\u0003!\u0005\u001b7-^7vY\u0006\u0014G.\u001a)be\u0006l\u0007\u0002\u0003E5\u0011+\u0003\r\u0001#*)\u0011!U\u0005R\u000eE:\u0011oBq\u0001c&\u0001\t\u0003Ai,\u0006\u0004\t@\"\u001d\u00072\u001a\u000b\u0007\u0011\u0003D\t\u000ec5\u0015\t!\r\u0007R\u001a\t\b3!\u0005\u0006R\u0019Ee!\u0011\u0019Y\u000ec2\u0005\u0011!%\u00062\u0018b\u0001\u0007c\u0004Baa7\tL\u0012AQ1\u0006E^\u0005\u0004\u0019\t\u0010\u0003\u0005\t`!m\u00069\u0001Eh!\u001dI\u00022\u0017Ec\u0011\u0013D\u0001\u0002#\u001b\t<\u0002\u0007\u0001R\u0019\u0005\b\u0011#CY\f1\u0001YQ!AY\f#\u001c\tt!]\u0004b\u0002Em\u0001\u0011\u0005\u00012\\\u0001\u0016C\u000e\u001cW/\\;mC\ndWmQ8mY\u0016\u001cG/[8o+\u0019Ai\u000e#:\tjR!\u0001r\\E\u0010)\u0019A\t\u000fc;\n\u001aA9\u0011\u0004#)\td\"\u001d\b\u0003BBn\u0011K$\u0001\u0002#+\tX\n\u00071\u0011\u001f\t\u0005\u00077DI\u000f\u0002\u0005\u0006,!]'\u0019ABy\u0011)Ai\u000fc6\u0002\u0002\u0003\u000f\u0001r^\u0001\u000bKZLG-\u001a8dK\u0012J\u0004cB\u0006\tr\"\r\bR_\u0005\u0004\u0011gd!!\u0003$v]\u000e$\u0018n\u001c82%!A9\u0010c?\n\b%5aA\u0002E}\u0001\u0001A)P\u0001\u0007=e\u00164\u0017N\\3nK:$h\b\u0005\u0004\t~&\r\u0001r]\u0007\u0003\u0011\u007fT1!#\u0001|\u0003\u001d9WM\\3sS\u000eLA!#\u0002\t\u0000\nAqI]8xC\ndW\rE\u0003m\u0013\u0013A9/C\u0002\n\fY\u0014q\u0002\u0016:bm\u0016\u00148/\u00192mK>s7-\u001a\t\u0005\u0013\u001fI)\"\u0004\u0002\n\u0012)\u0019\u00112\u0003$\u0002\u0005%|\u0017\u0002BE\f\u0013#\u0011AbU3sS\u0006d\u0017N_1cY\u0016D!\"c\u0007\tX\u0006\u0005\t9AE\u000f\u0003-)g/\u001b3f]\u000e,G%\r\u0019\u0011\r\u0015MR\u0011\bEr\u0011!AI\u0007c6A\u0002!\r\b\u0006\u0003El\u0011[B\u0019\bc\u001e\t\u000f%\u0015\u0002\u0001\"\u0001\n(\u0005A!/Z4jgR,'\u000fF\u0002P\u0013SA\u0001\"c\u000b\n$\u0001\u0007\u0011RF\u0001\u0004C\u000e\u001c\u0007GBE\u0018\u0013oIi\u0004E\u0004&\u0013cI)$c\u000f\n\u0007%MbEA\u0007BG\u000e,X.\u001e7bi>\u0014hK\r\t\u0005\u00077L9\u0004\u0002\u0007\n:%%\u0012\u0011!A\u0001\u0006\u0003\u0019\tPA\u0002`IU\u0002Baa7\n>\u0011a\u0011rHE\u0015\u0003\u0003\u0005\tQ!\u0001\u0004r\n\u0019q\f\n\u001c\t\u000f%\u0015\u0002\u0001\"\u0001\nDQ)q*#\u0012\nX!A\u00112FE!\u0001\u0004I9\u0005\r\u0004\nJ%5\u00132\u000b\t\bK%E\u00122JE)!\u0011\u0019Y.#\u0014\u0005\u0019%=\u0013RIA\u0001\u0002\u0003\u0015\ta!=\u0003\u0007}#s\u0007\u0005\u0003\u0004\\&MC\u0001DE+\u0013\u000b\n\t\u0011!A\u0003\u0002\rE(aA0%q!9\u0001\u0012SE!\u0001\u0004A\u0006bBE.\u0001\u0011\u0005\u0011RL\u0001\u0010Y>tw-Q2dk6,H.\u0019;peV\u0011\u0011r\f\t\u0004K%\u0005\u0014bAE2M\tyAj\u001c8h\u0003\u000e\u001cW/\\;mCR|'\u000fC\u0004\n\\\u0001!\t!c\u001a\u0015\t%}\u0013\u0012\u000e\u0005\b\u0011#K)\u00071\u0001Y\u0011\u001dIi\u0007\u0001C\u0001\u0013_\n\u0011\u0003Z8vE2,\u0017iY2v[Vd\u0017\r^8s+\tI\t\bE\u0002&\u0013gJ1!#\u001e'\u0005E!u.\u001e2mK\u0006\u001b7-^7vY\u0006$xN\u001d\u0005\b\u0013[\u0002A\u0011AE=)\u0011I\t(c\u001f\t\u000f!E\u0015r\u000fa\u00011\"9\u0011r\u0010\u0001\u0005\u0002%\u0005\u0015!F2pY2,7\r^5p]\u0006\u001b7-^7vY\u0006$xN]\u000b\u0005\u0013\u0007Ki)\u0006\u0002\n\u0006B)Q%c\"\n\f&\u0019\u0011\u0012\u0012\u0014\u0003+\r{G\u000e\\3di&|g.Q2dk6,H.\u0019;peB!11\\EG\t!)Y## C\u0002\rE\bbBE@\u0001\u0011\u0005\u0011\u0012S\u000b\u0005\u0013'KI\n\u0006\u0003\n\u0016&m\u0005#B\u0013\n\b&]\u0005\u0003BBn\u00133#\u0001\"b\u000b\n\u0010\n\u00071\u0011\u001f\u0005\b\u0011#Ky\t1\u0001Y\u0011\u001dIy\n\u0001C\u0001\u0013C\u000b\u0011B\u0019:pC\u0012\u001c\u0017m\u001d;\u0016\t%\r\u00162\u0017\u000b\u0005\u0013KKY\f\u0006\u0003\n(&U\u0006CBEU\u0013[K\t,\u0004\u0002\n,*\u0019\u0011r\u0014\u0002\n\t%=\u00162\u0016\u0002\n\u0005J|\u0017\rZ2bgR\u0004Baa7\n4\u0012AQ1FEO\u0005\u0004\u0019\t\u0010\u0003\u0006\n8&u\u0015\u0011!a\u0002\u0013s\u000b1\"\u001a<jI\u0016t7-\u001a\u00132cA1Q1GC\u001d\u0013cC\u0001\u0002\"(\n\u001e\u0002\u0007\u0011\u0012\u0017\u0005\b\u0013\u007f\u0003A\u0011AEa\u0003\u001d\tG\r\u001a$jY\u0016$2aTEb\u0011\u001d)\t+#0A\u0002aCq!c2\u0001\t\u0003II-A\u0005mSN$h)\u001b7fgR\t1\u000eC\u0004\n@\u0002!\t!#4\u0015\u000b=Ky-#5\t\u000f\u0015\u0005\u00162\u001aa\u00011\"9\u00112[Ef\u0001\u0004y\u0013!\u0003:fGV\u00148/\u001b<f\u0011\u001dI9\u000e\u0001C\u0001\u00133\f\u0001#\u00193e'B\f'o\u001b'jgR,g.\u001a:\u0015\u0007=KY\u000e\u0003\u0005\n^&U\u0007\u0019AEp\u0003!a\u0017n\u001d;f]\u0016\u0014\b\u0003BA9\u0013CLA!c9\u0002t\t12\u000b]1sW2K7\u000f^3oKJLe\u000e^3sM\u0006\u001cW\r\u000b\u0003\nV&\u001d\b\u0003BEu\u0013_l!!c;\u000b\u0007%5(!\u0001\u0006b]:|G/\u0019;j_:LA!#=\nl\naA)\u001a<fY>\u0004XM]!qS\"9\u0011R\u001f\u0001\u0005\u0002%]\u0018a\u0005:f[>4Xm\u00159be.d\u0015n\u001d;f]\u0016\u0014HcA(\nz\"A\u0011R\\Ez\u0001\u0004Iy\u000e\u000b\u0003\nt&\u001d\b\u0002CE\u0000\u0001\u0011\u0005!!#3\u0002\u001d\u001d,G/\u0012=fGV$xN]%eg\"9!2\u0001\u0001\u0005\u0002)\u0015\u0011!\u0006:fcV,7\u000f\u001e+pi\u0006dW\t_3dkR|'o\u001d\u000b\b_)\u001d!2\u0002F\b\u0011!QIA#\u0001A\u0002\t]\u0011\u0001\u00048v[\u0016CXmY;u_J\u001c\b\u0002\u0003F\u0007\u0015\u0003\u0001\rAa\u0006\u0002%1|7-\u00197jif\fu/\u0019:f)\u0006\u001c8n\u001d\u0005\t\u0015#Q\t\u00011\u0001\u000b\u0014\u0005!\u0002n\\:u)>dunY1m)\u0006\u001c8nQ8v]R\u0004rA#\u0006\u000b\u001ca\u00139\"\u0004\u0002\u000b\u0018)\u0019!\u0012D>\u0002\u0013%lW.\u001e;bE2,\u0017b\u0001@\u000b\u0018!\"!\u0012AEt\u0011\u001dQ\t\u0003\u0001C\u0001\u0015G\t\u0001C]3rk\u0016\u001cH/\u0012=fGV$xN]:\u0015\u0007=R)\u0003\u0003\u0005\u000b()}\u0001\u0019\u0001B\f\u0003YqW/\\!eI&$\u0018n\u001c8bY\u0016CXmY;u_J\u001c\b\u0006\u0002F\u0010\u0013ODqA#\f\u0001\t\u0003Qy#A\u0007lS2dW\t_3dkR|'o\u001d\u000b\u0004_)E\u0002b\u0002F\u001a\u0015W\u0001\ra[\u0001\fKb,7-\u001e;pe&#7\u000f\u000b\u0003\u000b,%\u001d\bb\u0002F\u001d\u0001\u0011\u0005!2H\u0001\rW&dG.\u0012=fGV$xN\u001d\u000b\u0004_)u\u0002b\u0002C`\u0015o\u0001\r\u0001\u0017\u0015\u0005\u0015oI9\u000f\u0003\u0005\u000bD\u0001!\tA\u0001F#\u0003YY\u0017\u000e\u001c7B]\u0012\u0014V\r\u001d7bG\u0016,\u00050Z2vi>\u0014HcA\u0018\u000bH!9Aq\u0018F!\u0001\u0004A\u0006b\u0002F&\u0001\u0011\u0005!1T\u0001\bm\u0016\u00148/[8o\u0011\u001dQy\u0005\u0001C\u0001\u0015#\nqcZ3u\u000bb,7-\u001e;pe6+Wn\u001c:z'R\fG/^:\u0016\u0005)M\u0003#\u0002>~1*U\u0003#B\u0006\u0006\u0014^:\u0004b\u0002F-\u0001\u0011\u0005!2L\u0001\u0012O\u0016$(\u000b\u0012#Ti>\u0014\u0018mZ3J]\u001a|WC\u0001F/!\u0015YA1\u0017F0!\u0011Q\tGc\u001a\u000e\u0005)\r$b\u0001F3\u0005\u000591\u000f^8sC\u001e,\u0017\u0002\u0002F5\u0015G\u0012qA\u0015#E\u0013:4w\u000e\u000b\u0003\u000bX%\u001d\b\u0002\u0003F-\u0001\u0011\u0005!Ac\u001c\u0015\t)u#\u0012\u000f\u0005\t\u0015gRi\u00071\u0001\u000bv\u00051a-\u001b7uKJ\u0004ba\u0003Ey\u0015oz\u0003\u0007\u0002F=\u0015{\u0002baa4\u0004V*m\u0004\u0003BBn\u0015{\"ABc \u000br\u0005\u0005\t\u0011!B\u0001\u0007c\u0014Aa\u0018\u00132a!9!2\u0011\u0001\u0005\u0002)\u0015\u0015!E4fiB+'o]5ti\u0016tGO\u0015#EgV\u0011!r\u0011\t\u0007uv\u00149B##1\t)-%r\u0012\t\u0007\u0007\u001f\u001c)N#$\u0011\t\rm'r\u0012\u0003\r\u0015#S\t)!A\u0001\u0002\u000b\u00051\u0011\u001f\u0002\u0005?\u0012\n\u0014\u0007C\u0004\u000b\u0016\u0002!\tAc&\u00021\u001d,G/\u0012=fGV$xN]*u_J\fw-Z*uCR,8/\u0006\u0002\u000b\u001aB)1\u0002b-\u000b\u001cB!!\u0012\rFO\u0013\u0011QyJc\u0019\u0003\u001bM#xN]1hKN#\u0018\r^;tQ!Q\u0019\n#\u001c\u000b$*\u001d\u0016E\u0001FS\u0003e\"\u0006.[:![\u0016$\bn\u001c3![\u0006L\be\u00195b]\u001e,\u0007e\u001c:!E\u0016\u0004#/Z7pm\u0016$\u0007%\u001b8!C\u00022W\u000f^;sK\u0002\u0012X\r\\3bg\u0016t\u0013E\u0001FU\u0003\u0015\u0011dF\r\u00181Q\u0011Q\u0019*c:\t\u000f)=\u0006\u0001\"\u0001\u000b2\u0006Yq-\u001a;BY2\u0004vn\u001c7t+\tQ\u0019\f\u0005\u0003mi*U\u0006\u0003BA9\u0015oKAA#/\u0002t\tY1k\u00195fIVd\u0017M\u00197fQ\u0011Qi+c:\t\u000f)}\u0006\u0001\"\u0001\u000bB\u0006qq-\u001a;Q_>dgi\u001c:OC6,G\u0003\u0002Fb\u0015\u000b\u0004RaCA\u001d\u0015kCqAc2\u000b>\u0002\u0007\u0001,\u0001\u0003q_>d\u0007\u0006\u0002F_\u0013ODqA#4\u0001\t\u0003Qy-A\thKR\u001c6\r[3ek2LgnZ'pI\u0016,\"A#5\u0011\t)M'\u0012\u001c\b\u0005\u0003cR).\u0003\u0003\u000bX\u0006M\u0014AD*dQ\u0016$W\u000f\\5oO6{G-Z\u0005\u0005\u00157TiN\u0001\bTG\",G-\u001e7j]\u001elu\u000eZ3\u000b\t)]\u00171\u000f\u0005\t\u0015C\u0004A\u0011\u0001\u0002\u000bd\u0006\u0001r-\u001a;Qe\u00164WM\u001d:fI2{7m\u001d\u000b\u0007\u0015KTiO#?\u0011\t1$(r\u001d\t\u0005\u0003cRI/\u0003\u0003\u000bl\u0006M$\u0001\u0004+bg.dunY1uS>t\u0007\u0002CBj\u0015?\u0004\rAc<1\t)E(R\u001f\t\u0007\u0007\u001f\u001c)Nc=\u0011\t\rm'R\u001f\u0003\r\u0015oTi/!A\u0001\u0002\u000b\u00051\u0011\u001f\u0002\u0005?\u0012\n$\u0007\u0003\u0005\u000b|*}\u0007\u0019\u0001B\f\u0003%\u0001\u0018M\u001d;ji&|g\u000e\u0003\u0005\u000b\u0000\u0002!\tAAF\u0001\u0003)\u0001XM]:jgR\u0014F\t\u0012\u000b\u0004\u001f.\r\u0001\u0002CBj\u0015{\u0004\ra#\u00021\t-\u001d12\u0002\t\u0007\u0007\u001f\u001c)n#\u0003\u0011\t\rm72\u0002\u0003\r\u0017\u001bY\u0019!!A\u0001\u0002\u000b\u00051\u0011\u001f\u0002\u0005?\u0012\n4\u0007\u0003\u0005\f\u0012\u0001!\tAAF\n\u00031)h\u000e]3sg&\u001cHO\u0015#E)\u0015y5RCF\r\u0011!Y9bc\u0004A\u0002\t]\u0011!\u0002:eI&#\u0007\"CF\u000e\u0017\u001f\u0001\n\u00111\u00010\u0003!\u0011Gn\\2lS:<\u0007bBF\u0010\u0001\u0011\u00051\u0012E\u0001\u0007C\u0012$'*\u0019:\u0015\u0007=[\u0019\u0003C\u0004\u0006\".u\u0001\u0019\u0001-\t\u000f-\u001d\u0002\u0001\"\u0001\nJ\u0006AA.[:u\u0015\u0006\u00148\u000fC\u0004\f,\u0001!\tA\u0001(\u0002\u001fM$x\u000e]%o\u001d\u0016<H\u000b\u001b:fC\u0012Daac\f\u0001\t\u0003q\u0015\u0001B:u_BD\u0001bc\r\u0001\t\u0003\u00111RG\u0001\rO\u0016$8\u000b]1sW\"{W.\u001a\u000b\u0003\u00037Bqa#\u000f\u0001\t\u0003YY$A\u0006tKR\u001c\u0015\r\u001c7TSR,GcA(\f>!91rHF\u001c\u0001\u0004A\u0016!D:i_J$8)\u00197m'&$X\r\u0003\u0005\f:\u0001!\tAAF\")\ry5R\t\u0005\b\u0017\u000fZ\t\u00051\u0001%\u0003!\u0019\u0017\r\u001c7TSR,\u0007BBF&\u0001\u0011\u0005a*A\u0007dY\u0016\f'oQ1mYNKG/\u001a\u0005\t\u0017\u001f\u0002A\u0011\u0001\u0002\fR\u0005Yq-\u001a;DC2d7+\u001b;f)\u0005!\u0003bBF+\u0001\u0011\u00051rK\u0001\u0007eVt'j\u001c2\u0016\r-e3RNF3))YYfc\u001a\fp-\u001552\u0012\u000b\u0004\u001f.u\u0003BCF0\u0017'\n\t\u0011q\u0001\fb\u0005YQM^5eK:\u001cW\rJ\u00193!\u0019)\u0019$\"\u000f\fdA!11\\F3\t!)yac\u0015C\u0002\rE\b\u0002CBj\u0017'\u0002\ra#\u001b\u0011\r\r=7Q[F6!\u0011\u0019Yn#\u001c\u0005\u0011\u0015-22\u000bb\u0001\u0007cD\u0001b#\u001d\fT\u0001\u000712O\u0001\u0005MVt7\rE\u0005\f\u0017kZIhc \fd%\u00191r\u000f\u0007\u0003\u0013\u0019+hn\u0019;j_:\u0014\u0004cA\r\f|%\u00191R\u0010\u0002\u0003\u0017Q\u000b7o[\"p]R,\u0007\u0010\u001e\t\u0006Y.\u000552N\u0005\u0004\u0017\u00073(\u0001C%uKJ\fGo\u001c:\t\u0011-\u001d52\u000ba\u0001\u0017\u0013\u000b!\u0002]1si&$\u0018n\u001c8t!\u0011aGOa\u0006\t\u0011-552\u000ba\u0001\u0017\u001f\u000bQB]3tk2$\b*\u00198eY\u0016\u0014\b\u0003C\u0006\fv\t]12M(\t\u000f-U\u0003\u0001\"\u0001\f\u0014V11RSFV\u0017;#\u0002bc&\f&.562\u0017\u000b\u0005\u00173[y\nE\u0003\f\tg[Y\n\u0005\u0003\u0004\\.uE\u0001CC\b\u0017#\u0013\ra!=\t\u0015-\u00056\u0012SA\u0001\u0002\bY\u0019+A\u0006fm&$WM\\2fIE\u001a\u0004CBC\u001a\u000bsYY\n\u0003\u0005\u0004T.E\u0005\u0019AFT!\u0019\u0019ym!6\f*B!11\\FV\t!)Yc#%C\u0002\rE\b\u0002CF9\u0017#\u0003\rac,\u0011\u0013-Y)h#\u001f\f2.m\u0005#\u00027\f\u0002.%\u0006\u0002CFD\u0017#\u0003\ra##\t\u000f-U\u0003\u0001\"\u0001\f8V11\u0012XFh\u0017\u0003$\u0002bc/\fJ.E7r\u001b\u000b\u0005\u0017{[\u0019\rE\u0003\f\tg[y\f\u0005\u0003\u0004\\.\u0005G\u0001CC\b\u0017k\u0013\ra!=\t\u0015-\u00157RWA\u0001\u0002\bY9-A\u0006fm&$WM\\2fIE\"\u0004CBC\u001a\u000bsYy\f\u0003\u0005\u0004T.U\u0006\u0019AFf!\u0019\u0019ym!6\fNB!11\\Fh\t!)Yc#.C\u0002\rE\b\u0002CF9\u0017k\u0003\rac5\u0011\u000f-A\tp#6\f@B)An#!\fN\"A1rQF[\u0001\u0004YI\tC\u0004\fV\u0001!\tac7\u0016\r-u72_Fs)\u0019Yyn#<\fvR!1\u0012]Ft!\u0015YA1WFr!\u0011\u0019Yn#:\u0005\u0011\u0015=1\u0012\u001cb\u0001\u0007cD!b#;\fZ\u0006\u0005\t9AFv\u0003-)g/\u001b3f]\u000e,G%M\u001b\u0011\r\u0015MR\u0011HFr\u0011!\u0019\u0019n#7A\u0002-=\bCBBh\u0007+\\\t\u0010\u0005\u0003\u0004\\.MH\u0001CC\u0016\u00173\u0014\ra!=\t\u0011-E4\u0012\u001ca\u0001\u0017o\u0004\u0012bCF;\u0017sZIpc9\u0011\u000b1\\\ti#=\t\u000f-U\u0003\u0001\"\u0001\f~V11r G\u000b\u0019\u000f!b\u0001$\u0001\r\u00101]A\u0003\u0002G\u0002\u0019\u0013\u0001Ra\u0003CZ\u0019\u000b\u0001Baa7\r\b\u0011AQqBF~\u0005\u0004\u0019\t\u0010\u0003\u0006\r\f-m\u0018\u0011!a\u0002\u0019\u001b\t1\"\u001a<jI\u0016t7-\u001a\u00132mA1Q1GC\u001d\u0019\u000bA\u0001ba5\f|\u0002\u0007A\u0012\u0003\t\u0007\u0007\u001f\u001c)\u000ed\u0005\u0011\t\rmGR\u0003\u0003\t\u000bWYYP1\u0001\u0004r\"A1\u0012OF~\u0001\u0004aI\u0002E\u0004\f\u0011cdY\u0002$\u0002\u0011\u000b1\\\t\td\u0005\t\u000f-U\u0003\u0001\"\u0001\r U1A\u0012\u0005G\u001b\u0019[!\u0002\u0002d\t\r01]Br\b\u000b\u0004\u001f2\u0015\u0002B\u0003G\u0014\u0019;\t\t\u0011q\u0001\r*\u0005YQM^5eK:\u001cW\rJ\u00198!\u0019)\u0019$\"\u000f\r,A!11\u001cG\u0017\t!)y\u0001$\bC\u0002\rE\b\u0002CBj\u0019;\u0001\r\u0001$\r\u0011\r\r=7Q\u001bG\u001a!\u0011\u0019Y\u000e$\u000e\u0005\u0011\u0015-BR\u0004b\u0001\u0007cD\u0001\u0002$\u000f\r\u001e\u0001\u0007A2H\u0001\u0011aJ|7-Z:t!\u0006\u0014H/\u001b;j_:\u0004\u0012bCF;\u0017sbi\u0004d\u000b\u0011\u000b1\\\t\td\r\t\u0011-5ER\u0004a\u0001\u0019\u0003\u0002\u0002bCF;\u0005/aYc\u0014\u0005\b\u0017+\u0002A\u0011\u0001G#+\u0019a9\u0005d\u0017\rTQAA\u0012\nG+\u0019;b\u0019\u0007F\u0002P\u0019\u0017B!\u0002$\u0014\rD\u0005\u0005\t9\u0001G(\u0003-)g/\u001b3f]\u000e,G%\r\u001d\u0011\r\u0015MR\u0011\bG)!\u0011\u0019Y\u000ed\u0015\u0005\u0011\u0015=A2\tb\u0001\u0007cD\u0001ba5\rD\u0001\u0007Ar\u000b\t\u0007\u0007\u001f\u001c)\u000e$\u0017\u0011\t\rmG2\f\u0003\t\u000bWa\u0019E1\u0001\u0004r\"AA\u0012\bG\"\u0001\u0004ay\u0006E\u0004\f\u0011cd\t\u0007$\u0015\u0011\u000b1\\\t\t$\u0017\t\u0011-5E2\ta\u0001\u0019K\u0002\u0002bCF;\u0005/a\tf\u0014\u0005\b\u0019S\u0002A\u0011\u0001G6\u0003E\u0011XO\\!qaJ|\u00070[7bi\u0016TuNY\u000b\t\u0019[b)\td$\r~QQAr\u000eG@\u0019\u000fc\t\nd'\u0011\r1EDr\u000fG>\u001b\ta\u0019HC\u0002\rv\t\tq\u0001]1si&\fG.\u0003\u0003\rz1M$!\u0004)beRL\u0017\r\u001c*fgVdG\u000f\u0005\u0003\u0004\\2uD\u0001\u0003EU\u0019O\u0012\ra!=\t\u0011\rMGr\ra\u0001\u0019\u0003\u0003baa4\u0004V2\r\u0005\u0003BBn\u0019\u000b#\u0001\"b\u000b\rh\t\u00071\u0011\u001f\u0005\t\u0017cb9\u00071\u0001\r\nBI1b#\u001e\fz1-ER\u0012\t\u0006Y.\u0005E2\u0011\t\u0005\u00077dy\t\u0002\u0005\u0006\u00101\u001d$\u0019ABy\u0011!a\u0019\nd\u001aA\u00021U\u0015!C3wC2,\u0018\r^8s!!a\t\bd&\r\u000e2m\u0014\u0002\u0002GM\u0019g\u0012A#\u00119qe>D\u0018.\\1uK\u00163\u0018\r\\;bi>\u0014\bb\u0002GO\u0019O\u0002\raN\u0001\bi&lWm\\;uQ\u0011a9'c:\t\u000f1\r\u0006\u0001\"\u0001\r&\u0006I1/\u001e2nSRTuNY\u000b\t\u0019OcI\fd1\r2RaA\u0012\u0016GZ\u0019wc)\rd2\rLB)\u0011\u0004d+\r0&\u0019AR\u0016\u0002\u0003%MKW\u000e\u001d7f\rV$XO]3BGRLwN\u001c\t\u0005\u00077d\t\f\u0002\u0005\t*2\u0005&\u0019ABy\u0011!\u0019\u0019\u000e$)A\u00021U\u0006CBBh\u0007+d9\f\u0005\u0003\u0004\\2eF\u0001CC\u0016\u0019C\u0013\ra!=\t\u00111eB\u0012\u0015a\u0001\u0019{\u0003ra\u0003Ey\u0019\u007fc\t\rE\u0003m\u0017\u0003c9\f\u0005\u0003\u0004\\2\rG\u0001CC\b\u0019C\u0013\ra!=\t\u0011-\u001dE\u0012\u0015a\u0001\u0017\u0013C\u0001b#$\r\"\u0002\u0007A\u0012\u001a\t\t\u0017-U$q\u0003Ga\u001f\"IAR\u001aGQ\t\u0003\u0007ArZ\u0001\u000be\u0016\u001cX\u000f\u001c;Gk:\u001c\u0007#B\u0006\u0006\u00181=\u0006\u0002\u0003Gj\u0001\u0011\u0005!\u0001$6\u0002\u001dM,(-\\5u\u001b\u0006\u00048\u000b^1hKVAAr\u001bGw\u0019cd)\u0010\u0006\u0003\rZ2\u0005\b#B\r\r,2m\u0007cA\r\r^&\u0019Ar\u001c\u0002\u0003'5\u000b\u0007oT;uaV$8\u000b^1uSN$\u0018nY:\t\u00111\rH\u0012\u001ba\u0001\u0019K\f!\u0002Z3qK:$WM\\2z!%IBr\u001dGv\u0019_d\u00190C\u0002\rj\n\u0011\u0011c\u00155vM\u001adW\rR3qK:$WM\\2z!\u0011\u0019Y\u000e$<\u0005\u0011\u0015]H\u0012\u001bb\u0001\u0007c\u0004Baa7\rr\u0012AQQ Gi\u0005\u0004\u0019\t\u0010\u0005\u0003\u0004\\2UH\u0001\u0003G|\u0019#\u0014\ra!=\u0003\u0003\rCq\u0001d?\u0001\t\u0003ai0\u0001\bdC:\u001cW\r\u001c&pE\u001e\u0013x.\u001e9\u0015\u0007=cy\u0010C\u0004\u0005v2e\b\u0019\u0001-\t\r5\r\u0001\u0001\"\u0001O\u00035\u0019\u0017M\\2fY\u0006cGNS8cg\"9Qr\u0001\u0001\u0005\u00025%\u0011!C2b]\u000e,GNS8c)\u0015yU2BG\b\u0011!ii!$\u0002A\u0002\t]\u0011!\u00026pE&#\u0007bBG\t\u001b\u000b\u0001\r\u0001W\u0001\u0007e\u0016\f7o\u001c8\t\u000f5\u001d\u0001\u0001\"\u0001\u000e\u0016Q\u0019q*d\u0006\t\u001155Q2\u0003a\u0001\u0005/Aq!d\u0007\u0001\t\u0003ii\"A\u0006dC:\u001cW\r\\*uC\u001e,G#B(\u000e 5\r\u0002\u0002CG\u0011\u001b3\u0001\rAa\u0006\u0002\u000fM$\u0018mZ3JI\"9Q\u0012CG\r\u0001\u0004A\u0006bBG\u000e\u0001\u0011\u0005Qr\u0005\u000b\u0004\u001f6%\u0002\u0002CG\u0011\u001bK\u0001\rAa\u0006\t\u000f55\u0002\u0001\"\u0001\u000e0\u0005y1.\u001b7m)\u0006\u001c8.\u0011;uK6\u0004H\u000fF\u00040\u001bci)$$\u000f\t\u000f5MR2\u0006a\u0001o\u00051A/Y:l\u0013\u0012D\u0011\"d\u000e\u000e,A\u0005\t\u0019A\u0018\u0002\u001f%tG/\u001a:skB$H\u000b\u001b:fC\u0012D\u0011\"$\u0005\u000e,A\u0005\t\u0019\u0001-\t\u00115u\u0002\u0001\"\u0001\u0003\u001b\u007f\tQa\u00197fC:,B!$\u0011\u000eFQ1Q2IG%\u001b\u001b\u0002Baa7\u000eF\u0011AaqRG\u001e\u0005\u0004i9%E\u0002\u0004t*A\u0001\"d\u0013\u000e<\u0001\u0007Q2I\u0001\u0002M\"IQrJG\u001e!\u0003\u0005\raL\u0001\u0012G\",7m[*fe&\fG.\u001b>bE2,\u0007bBG*\u0001\u0011\u0005QRK\u0001\u0011g\u0016$8\t[3dWB|\u0017N\u001c;ESJ$2aTG,\u0011\u001diI&$\u0015A\u0002a\u000b\u0011\u0002Z5sK\u000e$xN]=\t\u000f5u\u0003\u0001\"\u0001\u0002Z\u0005\u0001r-\u001a;DQ\u0016\u001c7\u000e]8j]R$\u0015N\u001d\u0005\b\u001bC\u0002A\u0011\u0001B\u000b\u0003I!WMZ1vYR\u0004\u0016M]1mY\u0016d\u0017n]7\t\u000f5\u0015\u0004\u0001\"\u0001\u0003\u0016\u0005!B-\u001a4bk2$X*\u001b8QCJ$\u0018\u000e^5p]ND\u0011\"$\u001b\u0001\u0005\u0004%I!d\u001b\u0002\u001b9,\u0007\u0010^*ik\u001a4G.Z%e+\tii\u0007E\u0002A\u001b_J1!$\u001dB\u00055\tEo\\7jG&sG/Z4fe\"AQR\u000f\u0001!\u0002\u0013ii'\u0001\boKb$8\u000b[;gM2,\u0017\n\u001a\u0011\t\u00115e\u0004\u0001\"\u0001\u0003\u001bw\nAB\\3x'\",hM\u001a7f\u0013\u0012$\"Aa\u0006\t\u00135}\u0004A1A\u0005\n5-\u0014!\u00038fqR\u0014F\rZ%e\u0011!i\u0019\t\u0001Q\u0001\n55\u0014A\u00038fqR\u0014F\rZ%eA!AQr\u0011\u0001\u0005\u0002\tiY(\u0001\u0005oK^\u0014F\rZ%e\u0011\u0019iY\t\u0001C\u0005\u001d\u0006A2/\u001a;va\u0006sGm\u0015;beRd\u0015n\u001d;f]\u0016\u0014()^:\t\r5=\u0005\u0001\"\u0003O\u0003Q\u0001xn\u001d;BaBd\u0017nY1uS>t7\u000b^1si\"1Q2\u0013\u0001\u0005\n9\u000b!\u0003]8ti\u0006\u0003\b\u000f\\5dCRLwN\\#oI\"1Qr\u0013\u0001\u0005\n9\u000bQ\u0003]8ti\u0016sg/\u001b:p]6,g\u000e^+qI\u0006$X\rC\u0005\u000e\u001c\u0002\t\n\u0011\"\u0001\u000e\u001e\u00061RO\u001c9feNL7\u000f\u001e*E\t\u0012\"WMZ1vYR$#'\u0006\u0002\u000e *\u001aq&$),\u00055\r\u0006\u0003BGS\u001b[k!!d*\u000b\t5%V2V\u0001\nk:\u001c\u0007.Z2lK\u0012T1!#<\r\u0013\u0011iy+d*\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0005\u000e4\u0002\t\n\u0011\"\u0001\u000e\u001e\u0006)2/\u001a;K_\n<%o\\;qI\u0011,g-Y;mi\u0012\u001a\u0004\"CG\\\u0001E\u0005I\u0011AG]\u0003U\u0001\u0018M]1mY\u0016d\u0017N_3%I\u00164\u0017-\u001e7uII*B!d/\u000e@V\u0011QR\u0018\u0016\u0005\u0005/i\t\u000b\u0002\u0005\u0006,5U&\u0019ABy\u0011%i\u0019\rAI\u0001\n\u0003i)-A\bsC:<W\r\n3fM\u0006,H\u000e\u001e\u00134+\ti9MK\u00028\u001bCC\u0011\"d3\u0001#\u0003%\t!d/\u0002\u001fI\fgnZ3%I\u00164\u0017-\u001e7uIQB\u0011\"d4\u0001#\u0003%\t!$5\u0002#5\f7.\u001a*E\t\u0012\"WMZ1vYR$#'\u0006\u0003\u000e<6MG\u0001CC\u0016\u001b\u001b\u0014\ra!=\t\u00135]\u0007!%A\u0005\u00025m\u0016A\u0005;fqR4\u0015\u000e\\3%I\u00164\u0017-\u001e7uIIB\u0011\"d7\u0001#\u0003%\t!$8\u0002)!\fGm\\8q\r&dW\r\n3fM\u0006,H\u000e\u001e\u00136+\u0019iY,d8\u000eb\u0012AQq_Gm\u0005\u0004\u0019\t\u0010\u0002\u0005\u0006~6e'\u0019ABy\u0011%i)\u000fAI\u0001\n\u0003iY,\u0001\rxQ>dW\rV3yi\u001aKG.Z:%I\u00164\u0017-\u001e7uIIB\u0011\"$;\u0001#\u0003%\t!d/\u0002+\tLg.\u0019:z\r&dWm\u001d\u0013eK\u001a\fW\u000f\u001c;%e!IQR\u001e\u0001\u0012\u0002\u0013\u0005Qr^\u0001\u0018E&t\u0017M]=SK\u000e|'\u000fZ:%I\u00164\u0017-\u001e7uIM*\"!$=+\t\u0005]X\u0012\u0015\u0005\n\u001bk\u0004\u0011\u0013!C\u0001\u001bo\f!D\\3x\u0003BK\u0005*\u00193p_B4\u0015\u000e\\3%I\u00164\u0017-\u001e7uIU*\u0002\"d<\u000ez6mXR \u0003\t\u000bol\u0019P1\u0001\u0004r\u0012AQQ`Gz\u0005\u0004\u0019\t\u0010\u0002\u0005\u0007\u00106M(\u0019AG\u0000#\u0011\u0019\u0019P$\u0001\u0011\u0011\u00195h1\u001fH\u0002\u001d\u000b\u0001Baa7\u000ezB!11\\G~\u0011%qI\u0001AI\u0001\n\u0003qY!A\niC\u0012|w\u000e\u001d*E\t\u0012\"WMZ1vYR$S'\u0006\u0004\u000e<:5ar\u0002\u0003\t\u000bot9A1\u0001\u0004r\u0012AQQ H\u0004\u0005\u0004\u0019\t\u0010C\u0005\u000f\u0014\u0001\t\n\u0011\"\u0001\u000f\u0016\u0005Ib.Z<B!&C\u0015\rZ8paJ#E\t\n3fM\u0006,H\u000e\u001e\u00132+!iyOd\u0006\u000f\u001a9mA\u0001CC|\u001d#\u0011\ra!=\u0005\u0011\u0015uh\u0012\u0003b\u0001\u0007c$\u0001Bb$\u000f\u0012\t\u0007aRD\t\u0005\u0007gty\u0002\u0005\u0005\u0007n\u001aMh\u0012\u0005H\u0012!\u0011\u0019YNd\u0006\u0011\t\rmg\u0012\u0004\u0005\n\u001dO\u0001\u0011\u0013!C\u0001\u001dS\tac]3rk\u0016t7-\u001a$jY\u0016$C-\u001a4bk2$HEM\u000b\u0007\u001bwsYC$\f\u0005\u0011\u0015]hR\u0005b\u0001\u0007c$\u0001\"\"@\u000f&\t\u00071\u0011\u001f\u0005\n\u001dc\u0001\u0011\u0013!C\u0001\u001dg\tqb\u00197fC:$C-\u001a4bk2$HEM\u000b\u0005\u001b;s)\u0004\u0002\u0005\u0007\u0010:=\"\u0019AG$\u0011%qI\u0004AI\u0001\n\u0003qY$\u0001\u000bpE*,7\r\u001e$jY\u0016$C-\u001a4bk2$HEM\u000b\u0005\u001bwsi\u0004\u0002\u0005\u0006,9]\"\u0019ABy\u0011%q\t\u0005AI\u0001\n\u0003ii*A\rlS2dG+Y:l\u0003R$X-\u001c9uI\u0011,g-Y;mi\u0012\u0012\u0004\"\u0003H#\u0001E\u0005I\u0011\u0001H$\u0003eY\u0017\u000e\u001c7UCN\\\u0017\t\u001e;f[B$H\u0005Z3gCVdG\u000fJ\u001a\u0016\u00059%#f\u0001-\u000e\"\u001e9aR\n\u0002\t\u00029=\u0013\u0001D*qCJ\\7i\u001c8uKb$\bcA\r\u000fR\u00191\u0011A\u0001E\u0001\u001d'\u001aBA$\u0015\u000b!!9QD$\u0015\u0005\u00029]CC\u0001H(\u0011)qYF$\u0015C\u0002\u0013%aRL\u0001\u0011-\u0006c\u0015\nR0M\u001f\u001e{F*\u0012,F\u0019N+\"Ad\u0018\u0011\r)Ua\u0012\rH3\u0013\u0011q\u0019Gc\u0006\u0003\u0007M+G\u000f\u0005\u0003\u0005\u00009\u001d\u0014bA/\u0005\u0002\"Ia2\u000eH)A\u0003%arL\u0001\u0012-\u0006c\u0015\nR0M\u001f\u001e{F*\u0012,F\u0019N\u0003\u0003B\u0003H8\u001d#\u0012\r\u0011\"\u0003\u000fr\u0005q2\u000bU!S\u0017~\u001buJ\u0014+F1R{6i\u0014(T)J+6\tV(S?2{5iS\u000b\u0003\u001dg\u0002B\u0001b \u000fv%!ar\u000fCA\u0005\u0019y%M[3di\"Ia2\u0010H)A\u0003%a2O\u0001 'B\u000b%kS0D\u001f:#V\t\u0017+`\u0007>s5\u000b\u0016*V\u0007R{%k\u0018'P\u0007.\u0003\u0003B\u0003H@\u001d#\u0012\r\u0011\"\u0003\u000f\u0002\u0006i\u0011m\u0019;jm\u0016\u001cuN\u001c;fqR,\"Ad!\u0011\t\u0001s)iH\u0005\u0004\u001d\u000f\u000b%aD!u_6L7MU3gKJ,gnY3\t\u00139-e\u0012\u000bQ\u0001\n9\r\u0015AD1di&4XmQ8oi\u0016DH\u000f\t\u0005\u000b\u001d\u001fs\t\u00061A\u0005\n9E\u0015aF2p]R,\u0007\u0010\u001e\"fS:<7i\u001c8tiJ,8\r^3e+\tq\u0019\n\u0005\u0003\f\u0003sy\u0002B\u0003HL\u001d#\u0002\r\u0011\"\u0003\u000f\u001a\u0006Y2m\u001c8uKb$()Z5oO\u000e{gn\u001d;sk\u000e$X\rZ0%KF$2a\u0014HN\u0011)\tYC$&\u0002\u0002\u0003\u0007a2\u0013\u0005\n\u001d?s\t\u0006)Q\u0005\u001d'\u000b\u0001dY8oi\u0016DHOQ3j]\u001e\u001cuN\\:ueV\u001cG/\u001a3!\u0011!q\u0019K$\u0015\u0005\n9\u0015\u0016!H1tg\u0016\u0014HOT8Pi\",'oQ8oi\u0016DH/S:Sk:t\u0017N\\4\u0015\u000b=s9Kd+\t\u000f9%f\u0012\u0015a\u0001?\u0005\u00111o\u0019\u0005\u0007[9\u0005\u0006\u0019A\u0018\t\u00119=f\u0012\u000bC\u0001\u001dc\u000b1bZ3u\u001fJ\u001c%/Z1uKR\u0019qDd-\t\r]qi\u000b1\u0001\u0019\u0011\u001dqyK$\u0015\u0005\u0002MC\u0011B$/\u000fR\u0011\u0005!A$%\u0002\u0013\u001d,G/Q2uSZ,\u0007\"\u0003H_\u001d#\"\tA\u0001H`\u0003ai\u0017M]6QCJ$\u0018.\u00197ms\u000e{gn\u001d;sk\u000e$X\r\u001a\u000b\u0006\u001f:\u0005g2\u0019\u0005\b\u001dSsY\f1\u0001 \u0011\u0019ic2\u0018a\u0001_!Iar\u0019H)\t\u0003\u0011a\u0012Z\u0001\u0011g\u0016$\u0018i\u0019;jm\u0016\u001cuN\u001c;fqR$Ra\u0014Hf\u001d\u001bDqA$+\u000fF\u0002\u0007q\u0004\u0003\u0004.\u001d\u000b\u0004\ra\f\u0005\t\u001d#t\t\u0006\"\u0001\u0003\u001d\u0006\u00112\r\\3be\u0006\u001bG/\u001b<f\u0007>tG/\u001a=u\u0011-q)N$\u0015C\u0002\u0013\u0005!Ad6\u0002+M\u0003\u0016IU&`\u0015>\u0013u\fR#T\u0007JK\u0005\u000bV%P\u001dV\u0011aR\r\u0005\n\u001d7t\t\u0006)A\u0005\u001dK\nac\u0015)B%.{&j\u0014\"`\t\u0016\u001b6IU%Q)&{e\n\t\u0005\f\u001d?t\tF1A\u0005\u0002\tq9.\u0001\nT!\u0006\u00136j\u0018&P\u0005~;%kT+Q?&#\u0005\"\u0003Hr\u001d#\u0002\u000b\u0011\u0002H3\u0003M\u0019\u0006+\u0011*L?*{%iX$S\u001fV\u0003v,\u0013#!\u0011-q9O$\u0015C\u0002\u0013\u0005!Ad6\u0002;M\u0003\u0016IU&`\u0015>\u0013u,\u0013(U\u000bJ\u0013V\u000b\u0015+`\u001f:{6)\u0011(D\u000b2C\u0011Bd;\u000fR\u0001\u0006IA$\u001a\u0002=M\u0003\u0016IU&`\u0015>\u0013u,\u0013(U\u000bJ\u0013V\u000b\u0015+`\u001f:{6)\u0011(D\u000b2\u0003\u0003b\u0003Hx\u001d#\u0012\r\u0011\"\u0001\u0003\u001d/\fQB\u0015#E?N\u001bu\nU#`\u0017\u0016K\u0006\"\u0003Hz\u001d#\u0002\u000b\u0011\u0002H3\u00039\u0011F\tR0T\u0007>\u0003ViX&F3\u0002B1Bd>\u000fR\t\u0007I\u0011\u0001\u0002\u000fX\u0006I\"\u000b\u0012#`'\u000e{\u0005+R0O\u001f~{e+\u0012*S\u0013\u0012+ulS#Z\u0011%qYP$\u0015!\u0002\u0013q)'\u0001\u000eS\t\u0012{6kQ(Q\u000b~sujX(W\u000bJ\u0013\u0016\nR#`\u0017\u0016K\u0006\u0005C\u0006\u000f\u0000:E#\u0019!C\u0001\u00059]\u0017!\u0005#S\u0013Z+%kX%E\u000b:#\u0016JR%F%\"Iq2\u0001H)A\u0003%aRM\u0001\u0013\tJKe+\u0012*`\u0013\u0012+e\nV%G\u0013\u0016\u0013\u0006\u0005C\u0006\u0010\b9E#\u0019!C\u0001\u00059]\u0017\u0001\u0007'F\u000f\u0006\u001b\u0015l\u0018#S\u0013Z+%kX%E\u000b:#\u0016JR%F%\"Iq2\u0002H)A\u0003%aRM\u0001\u001a\u0019\u0016;\u0015iQ-`\tJKe+\u0012*`\u0013\u0012+e\nV%G\u0013\u0016\u0013\u0006\u0005\u0003\u0005\u0010\u00109EC1BH\t\u0003Q\t'O]1z)>\f%O]1z/JLG/\u00192mKV!q2CH\u0015)\u0011y)bd\r\u0015\t=]q\u0012\u0005\t\u0005\u001f3yi\"\u0004\u0002\u0010\u001c)!\u00112CA\u007f\u0013\u0011yybd\u0007\u0003\u001b\u0005\u0013(/Y=Xe&$\u0018M\u00197f\u0011)y\u0019c$\u0004\u0002\u0002\u0003\u000fqRE\u0001\fKZLG-\u001a8dK\u0012\n\u0014\b\u0005\u0004\u00064\u0015err\u0005\t\u0005\u00077|I\u0003\u0002\u0005\u0006,=5!\u0019AH\u0016#\u0011\u0019\u0019p$\f\u0011\t=eqrF\u0005\u0005\u001fcyYB\u0001\u0005Xe&$\u0018M\u00197f\u0011!y)d$\u0004A\u0002=]\u0012aA1seB)An$\u000f\u0010(%\u0019q2\b<\u0003\u0017Q\u0013\u0018M^3sg\u0006\u0014G.\u001a\u0005\t\u001f\u007fq\t\u0006\"\u0001\u0010B\u0005Q!.\u0019:PM\u000ec\u0017m]:\u0015\t\u0005ms2\t\u0005\t\u001f\u000bzi\u00041\u0001\u0010H\u0005\u00191\r\\:1\t=%sR\n\t\u00063\u001aUq2\n\t\u0005\u00077|i\u0005\u0002\u0007\u0010P=\r\u0013\u0011!A\u0001\u0006\u0003\u0019\tP\u0001\u0003`IE\"\u0004\u0002CH*\u001d#\"\ta$\u0016\u0002\u0017)\f'o\u00144PE*,7\r\u001e\u000b\u0005\u00037z9\u0006C\u0004\u0010Z=E\u0003\u0019\u0001\u0006\u0002\u0007=\u0014'\u000eC\u0005\u0010^9EC\u0011\u0001\u0002\u0010`\u0005YQ\u000f\u001d3bi\u0016$7i\u001c8g)5Ar\u0012MH2\u001fKz9g$\u001b\u0010l!1!md\u0017A\u0002aAaaVH.\u0001\u0004A\u0006B\u00021\u0010\\\u0001\u0007\u0001\f\u0003\u0005i\u001f7\u0002\n\u00111\u0001Y\u0011!Qw2\fI\u0001\u0002\u0004Y\u0007\u0002\u0003=\u0010\\A\u0005\t\u0019A=\t\u0013==d\u0012\u000bC\u0001\u0005=E\u0014A\u00048v[\u0012\u0013\u0018N^3s\u0007>\u0014Xm\u001d\u000b\u0005\u0005/y\u0019\b\u0003\u0004X\u001f[\u0002\r\u0001\u0017\u0005\t\u001for\t\u0006\"\u0003\u0010z\u0005\u00192M]3bi\u0016$\u0016m]6TG\",G-\u001e7feRAq2PH?\u001f\u007fz\t\tE\u0004\f\u000b'\u0013yCa\u0012\t\u000f9%vR\u000fa\u0001?!1qk$\u001eA\u0002aCqa!\u001f\u0010v\u0001\u0007\u0001\f\u0003\u0005\u0010\u0006:EC\u0011BHD\u0003E9W\r^\"mkN$XM]'b]\u0006<WM\u001d\u000b\u0005\u001f\u0013{\t\nE\u0003\f\u0003syY\t\u0005\u0003\u0002r=5\u0015\u0002BHH\u0003g\u0012a#\u0012=uKJt\u0017\r\\\"mkN$XM]'b]\u0006<WM\u001d\u0005\b\u001f'{\u0019\t1\u0001Y\u0003\r)(\u000f\u001c\u0005\u000b\u001f/s\t&%A\u0005\u00029\u001d\u0013a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$3\u0007\u0003\u0006\u0010\u001c:E\u0013\u0013!C\u0001\u001f;\u000b1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\"TCAHPU\rYW\u0012\u0015\u0005\u000b\u001fGs\t&%A\u0005\u0002=\u0015\u0016a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$S'\u0006\u0002\u0010(*\u001a\u00110$)\t\u0015=-f\u0012KI\u0001\n\u0003q9%A\u000bva\u0012\fG/\u001a3D_:4G\u0005Z3gCVdG\u000f\n\u001b\t\u0015==f\u0012KI\u0001\n\u0003yi*A\u000bva\u0012\fG/\u001a3D_:4G\u0005Z3gCVdG\u000fJ\u001b\t\u0015=Mf\u0012KI\u0001\n\u0003y)+A\u000bva\u0012\fG/\u001a3D_:4G\u0005Z3gCVdG\u000f\n\u001c"
)
public class SparkContext implements Logging {
   private final CallSite org$apache$spark$SparkContext$$creationSite;
   private final boolean allowMultipleContexts;
   private final long startTime;
   private final AtomicBoolean stopped;
   private SparkConf org$apache$spark$SparkContext$$_conf;
   private Option<URI> _eventLogDir;
   private Option<String> _eventLogCodec;
   private LiveListenerBus _listenerBus;
   private SparkEnv org$apache$spark$SparkContext$$_env;
   private SparkStatusTracker _statusTracker;
   private Option<ConsoleProgressBar> org$apache$spark$SparkContext$$_progressBar;
   private Option<SparkUI> org$apache$spark$SparkContext$$_ui;
   private Configuration _hadoopConfiguration;
   private int _executorMemory;
   private SchedulerBackend _schedulerBackend;
   private TaskScheduler _taskScheduler;
   private RpcEndpointRef org$apache$spark$SparkContext$$_heartbeatReceiver;
   private volatile DAGScheduler org$apache$spark$SparkContext$$_dagScheduler;
   private String org$apache$spark$SparkContext$$_applicationId;
   private Option<String> _applicationAttemptId;
   private Option<EventLoggingListener> org$apache$spark$SparkContext$$_eventLogger;
   private Option<ExecutorAllocationManager> org$apache$spark$SparkContext$$_executorAllocationManager;
   private Option<ContextCleaner> org$apache$spark$SparkContext$$_cleaner;
   private boolean org$apache$spark$SparkContext$$_listenerBusStarted;
   private Seq<String> _jars;
   private Seq<String> _files;
   private Object _shutdownHookRef;
   private AppStatusStore _statusStore;
   private final Map<String, Object> addedFiles;
   private final Map<String, Object> addedJars;
   private final Map<Object, RDD<?>> persistentRdds;
   private final HashMap<String, String> executorEnvs;
   private final String sparkUser;
   private Option<String> checkpointDir;
   private final InheritableThreadLocal<Properties> localProperties;
   private final AtomicInteger nextShuffleId;
   private final AtomicInteger nextRddId;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static scala.collection.Map<String, String> updatedConf$default$6() {
      return SparkContext$.MODULE$.updatedConf$default$6();
   }

   public static Seq<String> updatedConf$default$5() {
      return SparkContext$.MODULE$.updatedConf$default$5();
   }

   public static String updatedConf$default$4() {
      return SparkContext$.MODULE$.updatedConf$default$4();
   }

   public static scala.collection.Map<String, String> $lessinit$greater$default$5() {
      return SparkContext$.MODULE$.$lessinit$greater$default$5();
   }

   public static Seq<String> $lessinit$greater$default$4() {
      return SparkContext$.MODULE$.$lessinit$greater$default$4();
   }

   public static String $lessinit$greater$default$3() {
      return SparkContext$.MODULE$.$lessinit$greater$default$3();
   }

   public static Option<String> jarOfObject(Object var0) {
      return SparkContext$.MODULE$.jarOfObject(var0);
   }

   public static Option<String> jarOfClass(Class<?> var0) {
      return SparkContext$.MODULE$.jarOfClass(var0);
   }

   public static SparkContext getOrCreate() {
      return SparkContext$.MODULE$.getOrCreate();
   }

   public static SparkContext getOrCreate(SparkConf var0) {
      return SparkContext$.MODULE$.getOrCreate(var0);
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

   public CallSite org$apache$spark$SparkContext$$creationSite() {
      return this.org$apache$spark$SparkContext$$creationSite;
   }

   private boolean allowMultipleContexts() {
      return this.allowMultipleContexts;
   }

   public long startTime() {
      return this.startTime;
   }

   public AtomicBoolean stopped() {
      return this.stopped;
   }

   public void assertNotStopped() {
      if (this.stopped().get()) {
         SparkContext activeContext = (SparkContext)SparkContext$.MODULE$.org$apache$spark$SparkContext$$activeContext().get();
         String activeCreationSite = activeContext == null ? "(No active SparkContext.)" : activeContext.org$apache$spark$SparkContext$$creationSite().longForm();
         throw new IllegalStateException((new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot call methods on a stopped SparkContext.\n           |This stopped SparkContext was created at:\n           |\n           |", "\n           |\n           |The currently active SparkContext was created at:\n           |\n           |", "\n         "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$SparkContext$$creationSite().longForm(), activeCreationSite}))))).stripMargin());
      }
   }

   public SparkConf org$apache$spark$SparkContext$$_conf() {
      return this.org$apache$spark$SparkContext$$_conf;
   }

   private void org$apache$spark$SparkContext$$_conf_$eq(SparkConf x$1) {
      this.org$apache$spark$SparkContext$$_conf = x$1;
   }

   private Option<URI> _eventLogDir() {
      return this._eventLogDir;
   }

   private void _eventLogDir_$eq(Option<URI> x$1) {
      this._eventLogDir = x$1;
   }

   private Option<String> _eventLogCodec() {
      return this._eventLogCodec;
   }

   private void _eventLogCodec_$eq(Option<String> x$1) {
      this._eventLogCodec = x$1;
   }

   private LiveListenerBus _listenerBus() {
      return this._listenerBus;
   }

   private void _listenerBus_$eq(LiveListenerBus x$1) {
      this._listenerBus = x$1;
   }

   public SparkEnv org$apache$spark$SparkContext$$_env() {
      return this.org$apache$spark$SparkContext$$_env;
   }

   private void org$apache$spark$SparkContext$$_env_$eq(SparkEnv x$1) {
      this.org$apache$spark$SparkContext$$_env = x$1;
   }

   private SparkStatusTracker _statusTracker() {
      return this._statusTracker;
   }

   private void _statusTracker_$eq(SparkStatusTracker x$1) {
      this._statusTracker = x$1;
   }

   public Option<ConsoleProgressBar> org$apache$spark$SparkContext$$_progressBar() {
      return this.org$apache$spark$SparkContext$$_progressBar;
   }

   private void org$apache$spark$SparkContext$$_progressBar_$eq(Option<ConsoleProgressBar> x$1) {
      this.org$apache$spark$SparkContext$$_progressBar = x$1;
   }

   public Option<SparkUI> org$apache$spark$SparkContext$$_ui() {
      return this.org$apache$spark$SparkContext$$_ui;
   }

   private void org$apache$spark$SparkContext$$_ui_$eq(Option<SparkUI> x$1) {
      this.org$apache$spark$SparkContext$$_ui = x$1;
   }

   private Configuration _hadoopConfiguration() {
      return this._hadoopConfiguration;
   }

   private void _hadoopConfiguration_$eq(Configuration x$1) {
      this._hadoopConfiguration = x$1;
   }

   private int _executorMemory() {
      return this._executorMemory;
   }

   private void _executorMemory_$eq(int x$1) {
      this._executorMemory = x$1;
   }

   private SchedulerBackend _schedulerBackend() {
      return this._schedulerBackend;
   }

   private void _schedulerBackend_$eq(SchedulerBackend x$1) {
      this._schedulerBackend = x$1;
   }

   private TaskScheduler _taskScheduler() {
      return this._taskScheduler;
   }

   private void _taskScheduler_$eq(TaskScheduler x$1) {
      this._taskScheduler = x$1;
   }

   public RpcEndpointRef org$apache$spark$SparkContext$$_heartbeatReceiver() {
      return this.org$apache$spark$SparkContext$$_heartbeatReceiver;
   }

   private void org$apache$spark$SparkContext$$_heartbeatReceiver_$eq(RpcEndpointRef x$1) {
      this.org$apache$spark$SparkContext$$_heartbeatReceiver = x$1;
   }

   public DAGScheduler org$apache$spark$SparkContext$$_dagScheduler() {
      return this.org$apache$spark$SparkContext$$_dagScheduler;
   }

   private void org$apache$spark$SparkContext$$_dagScheduler_$eq(DAGScheduler x$1) {
      this.org$apache$spark$SparkContext$$_dagScheduler = x$1;
   }

   public String org$apache$spark$SparkContext$$_applicationId() {
      return this.org$apache$spark$SparkContext$$_applicationId;
   }

   private void org$apache$spark$SparkContext$$_applicationId_$eq(String x$1) {
      this.org$apache$spark$SparkContext$$_applicationId = x$1;
   }

   private Option<String> _applicationAttemptId() {
      return this._applicationAttemptId;
   }

   private void _applicationAttemptId_$eq(Option<String> x$1) {
      this._applicationAttemptId = x$1;
   }

   public Option<EventLoggingListener> org$apache$spark$SparkContext$$_eventLogger() {
      return this.org$apache$spark$SparkContext$$_eventLogger;
   }

   private void org$apache$spark$SparkContext$$_eventLogger_$eq(Option<EventLoggingListener> x$1) {
      this.org$apache$spark$SparkContext$$_eventLogger = x$1;
   }

   public Option<ExecutorAllocationManager> org$apache$spark$SparkContext$$_executorAllocationManager() {
      return this.org$apache$spark$SparkContext$$_executorAllocationManager;
   }

   private void org$apache$spark$SparkContext$$_executorAllocationManager_$eq(Option<ExecutorAllocationManager> x$1) {
      this.org$apache$spark$SparkContext$$_executorAllocationManager = x$1;
   }

   public Option<ContextCleaner> org$apache$spark$SparkContext$$_cleaner() {
      return this.org$apache$spark$SparkContext$$_cleaner;
   }

   private void org$apache$spark$SparkContext$$_cleaner_$eq(Option<ContextCleaner> x$1) {
      this.org$apache$spark$SparkContext$$_cleaner = x$1;
   }

   private boolean org$apache$spark$SparkContext$$_listenerBusStarted() {
      return this.org$apache$spark$SparkContext$$_listenerBusStarted;
   }

   public void org$apache$spark$SparkContext$$_listenerBusStarted_$eq(boolean x$1) {
      this.org$apache$spark$SparkContext$$_listenerBusStarted = x$1;
   }

   private Seq<String> _jars() {
      return this._jars;
   }

   private void _jars_$eq(Seq<String> x$1) {
      this._jars = x$1;
   }

   private Seq<String> _files() {
      return this._files;
   }

   private void _files_$eq(Seq<String> x$1) {
      this._files = x$1;
   }

   private Object _shutdownHookRef() {
      return this._shutdownHookRef;
   }

   private void _shutdownHookRef_$eq(Object x$1) {
      this._shutdownHookRef = x$1;
   }

   private AppStatusStore _statusStore() {
      return this._statusStore;
   }

   private void _statusStore_$eq(AppStatusStore x$1) {
      this._statusStore = x$1;
   }

   public SparkConf conf() {
      return this.org$apache$spark$SparkContext$$_conf();
   }

   public SparkConf getConf() {
      return this.conf().clone();
   }

   public Seq<String> jars() {
      return this._jars();
   }

   public Seq<String> files() {
      return this._files();
   }

   public String master() {
      return this.org$apache$spark$SparkContext$$_conf().get("spark.master");
   }

   public String deployMode() {
      return (String)this.org$apache$spark$SparkContext$$_conf().getOption("spark.submit.deployMode").getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "client";
         }
      });
   }

   public String appName() {
      return this.org$apache$spark$SparkContext$$_conf().get("spark.app.name");
   }

   public boolean isEventLogEnabled() {
      return this.org$apache$spark$SparkContext$$_conf().getBoolean("spark.eventLog.enabled", false);
   }

   public Option<URI> eventLogDir() {
      return this._eventLogDir();
   }

   public Option<String> eventLogCodec() {
      return this._eventLogCodec();
   }

   public boolean isLocal() {
      return Utils$.MODULE$.isLocalMaster(this.org$apache$spark$SparkContext$$_conf());
   }

   public boolean isStopped() {
      return this.stopped().get();
   }

   public AppStatusStore statusStore() {
      return this._statusStore();
   }

   public LiveListenerBus listenerBus() {
      return this._listenerBus();
   }

   public SparkEnv createSparkEnv(SparkConf conf, boolean isLocal, LiveListenerBus listenerBus) {
      return SparkEnv$.MODULE$.createDriverEnv(conf, isLocal, listenerBus, SparkContext$.MODULE$.numDriverCores(this.master()), SparkEnv$.MODULE$.createDriverEnv$default$5());
   }

   public SparkEnv env() {
      return this.org$apache$spark$SparkContext$$_env();
   }

   public Map<String, Object> addedFiles() {
      return this.addedFiles;
   }

   public Map<String, Object> addedJars() {
      return this.addedJars;
   }

   public Map<Object, RDD<?>> persistentRdds() {
      return this.persistentRdds;
   }

   public SparkStatusTracker statusTracker() {
      return this._statusTracker();
   }

   public Option<ConsoleProgressBar> progressBar() {
      return this.org$apache$spark$SparkContext$$_progressBar();
   }

   public Option<SparkUI> ui() {
      return this.org$apache$spark$SparkContext$$_ui();
   }

   public Option<String> uiWebUrl() {
      return this.org$apache$spark$SparkContext$$_ui().<String>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(SparkUI x$1) {
            return x$1.webUrl();
         }
      });
   }

   public Configuration hadoopConfiguration() {
      return this._hadoopConfiguration();
   }

   public int executorMemory() {
      return this._executorMemory();
   }

   public HashMap<String, String> executorEnvs() {
      return this.executorEnvs;
   }

   public String sparkUser() {
      return this.sparkUser;
   }

   public SchedulerBackend schedulerBackend() {
      return this._schedulerBackend();
   }

   public TaskScheduler taskScheduler() {
      return this._taskScheduler();
   }

   public void taskScheduler_$eq(TaskScheduler ts) {
      this._taskScheduler_$eq(ts);
   }

   public DAGScheduler dagScheduler() {
      return this.org$apache$spark$SparkContext$$_dagScheduler();
   }

   public void dagScheduler_$eq(DAGScheduler ds) {
      this.org$apache$spark$SparkContext$$_dagScheduler_$eq(ds);
   }

   public String applicationId() {
      return this.org$apache$spark$SparkContext$$_applicationId();
   }

   public Option<String> applicationAttemptId() {
      return this._applicationAttemptId();
   }

   public Option<EventLoggingListener> eventLogger() {
      return this.org$apache$spark$SparkContext$$_eventLogger();
   }

   public Option<ExecutorAllocationManager> executorAllocationManager() {
      return this.org$apache$spark$SparkContext$$_executorAllocationManager();
   }

   public Option<ContextCleaner> cleaner() {
      return this.org$apache$spark$SparkContext$$_cleaner();
   }

   public Option<String> checkpointDir() {
      return this.checkpointDir;
   }

   public void checkpointDir_$eq(Option<String> x$1) {
      this.checkpointDir = x$1;
   }

   public InheritableThreadLocal<Properties> localProperties() {
      return this.localProperties;
   }

   public String org$apache$spark$SparkContext$$warnSparkMem(String value) {
      this.logWarning(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Using SPARK_MEM to set amount of memory to use per executor process is deprecated, please use spark.executor.memory instead.";
         }
      });
      return value;
   }

   public void setLogLevel(String logLevel) {
      String upperCased = logLevel.toUpperCase(Locale.ROOT);
      Predef$.MODULE$.require(SparkContext$.MODULE$.org$apache$spark$SparkContext$$VALID_LOG_LEVELS().contains(upperCased), new Serializable(this, logLevel) {
         public static final long serialVersionUID = 0L;
         private final String logLevel$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Supplied level ", " did not match one of:"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.logLevel$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{SparkContext$.MODULE$.org$apache$spark$SparkContext$$VALID_LOG_LEVELS().mkString(",")}))).toString();
         }

         public {
            this.logLevel$1 = logLevel$1;
         }
      });
      Utils$.MODULE$.setLogLevel(Level.toLevel(upperCased));
   }

   public Option<ThreadStackTrace[]> getExecutorThreadDump(String executorId) {
      Object var10000;
      try {
         label26: {
            String var3 = SparkContext$.MODULE$.DRIVER_IDENTIFIER();
            if (executorId == null) {
               if (var3 == null) {
                  break label26;
               }
            } else if (executorId.equals(var3)) {
               break label26;
            }

            RpcEndpointRef endpointRef = (RpcEndpointRef)this.env().blockManager().master().getExecutorEndpointRef(executorId).get();
            var10000 = new Some(endpointRef.askSync(BlockManagerMessages.TriggerThreadDump$.MODULE$, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(ThreadStackTrace.class))));
            return (Option<ThreadStackTrace[]>)var10000;
         }

         var10000 = new Some(Utils$.MODULE$.getThreadDump());
      } catch (Exception var5) {
         this.logError(new Serializable(this, executorId) {
            public static final long serialVersionUID = 0L;
            private final String executorId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception getting thread dump from executor ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorId$1}));
            }

            public {
               this.executorId$1 = executorId$1;
            }
         }, var5);
         var10000 = None$.MODULE$;
      }

      return (Option<ThreadStackTrace[]>)var10000;
   }

   public Properties getLocalProperties() {
      return (Properties)this.localProperties().get();
   }

   public void setLocalProperties(Properties props) {
      this.localProperties().set(props);
   }

   public void setLocalProperty(String key, String value) {
      if (value == null) {
         ((Hashtable)this.localProperties().get()).remove(key);
      } else {
         ((Properties)this.localProperties().get()).setProperty(key, value);
      }

   }

   public String getLocalProperty(String key) {
      return (String)Option$.MODULE$.apply(this.localProperties().get()).map(new Serializable(this, key) {
         public static final long serialVersionUID = 0L;
         private final String key$1;

         public final String apply(Properties x$12) {
            return x$12.getProperty(this.key$1);
         }

         public {
            this.key$1 = key$1;
         }
      }).orNull(Predef$.MODULE$.$conforms());
   }

   public void setJobDescription(String value) {
      this.setLocalProperty(SparkContext$.MODULE$.SPARK_JOB_DESCRIPTION(), value);
   }

   public void setJobGroup(String groupId, String description, boolean interruptOnCancel) {
      this.setLocalProperty(SparkContext$.MODULE$.SPARK_JOB_DESCRIPTION(), description);
      this.setLocalProperty(SparkContext$.MODULE$.SPARK_JOB_GROUP_ID(), groupId);
      this.setLocalProperty(SparkContext$.MODULE$.SPARK_JOB_INTERRUPT_ON_CANCEL(), BoxesRunTime.boxToBoolean(interruptOnCancel).toString());
   }

   public boolean setJobGroup$default$3() {
      return false;
   }

   public void clearJobGroup() {
      this.setLocalProperty(SparkContext$.MODULE$.SPARK_JOB_DESCRIPTION(), (String)null);
      this.setLocalProperty(SparkContext$.MODULE$.SPARK_JOB_GROUP_ID(), (String)null);
      this.setLocalProperty(SparkContext$.MODULE$.SPARK_JOB_INTERRUPT_ON_CANCEL(), (String)null);
   }

   public <U> U withScope(Function0<U> body) {
      return (U)RDDOperationScope$.MODULE$.withScope(this, RDDOperationScope$.MODULE$.withScope$default$2(), body);
   }

   public <T> RDD<T> parallelize(Seq<T> seq, int numSlices, ClassTag<T> evidence$1) {
      return (RDD)this.withScope(new Serializable(this, seq, numSlices, evidence$1) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final Seq seq$1;
         private final int numSlices$1;
         private final ClassTag evidence$1$1;

         public final ParallelCollectionRDD<T> apply() {
            this.$outer.assertNotStopped();
            return new ParallelCollectionRDD<T>(this.$outer, this.seq$1, this.numSlices$1, (scala.collection.Map)Map$.MODULE$.apply(Nil$.MODULE$), this.evidence$1$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.seq$1 = seq$1;
               this.numSlices$1 = numSlices$1;
               this.evidence$1$1 = evidence$1$1;
            }
         }
      });
   }

   public <T> int parallelize$default$2() {
      return this.defaultParallelism();
   }

   public RDD<Object> range(long start, long end, long step, int numSlices) {
      return (RDD)this.withScope(new Serializable(this, start, end, step, numSlices) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         public final long start$1;
         private final long end$1;
         public final long step$1;
         public final int numSlices$2;

         public final RDD<Object> apply() {
            this.$outer.assertNotStopped();
            Predef$.MODULE$.require(this.step$1 != 0L, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "step cannot be 0";
               }
            });
            BigInt safeStart = scala.package$.MODULE$.BigInt().apply(this.start$1);
            BigInt safeEnd = scala.package$.MODULE$.BigInt().apply(this.end$1);
            BigInt numElements = !BoxesRunTime.equalsNumObject(safeEnd.$minus(safeStart).$percent(BigInt$.MODULE$.long2bigInt(this.step$1)), BoxesRunTime.boxToInteger(0)) && safeEnd.$greater(safeStart) == this.step$1 > 0L ? safeEnd.$minus(safeStart).$div(BigInt$.MODULE$.long2bigInt(this.step$1)).$plus(BigInt$.MODULE$.int2bigInt(1)) : safeEnd.$minus(safeStart).$div(BigInt$.MODULE$.long2bigInt(this.step$1));
            RDD qual$1 = this.$outer.parallelize(RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.numSlices$2), this.numSlices$2, ClassTag$.MODULE$.Int());
            Function2 x$61 = new Serializable(this, numElements) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final BigInt numElements$1;

               public final Object apply(int i, Iterator<Object> x$13) {
                  BigInt partitionStart = BigInt$.MODULE$.int2bigInt(i).$times(this.numElements$1).$div(BigInt$.MODULE$.int2bigInt(this.$outer.numSlices$2)).$times(BigInt$.MODULE$.long2bigInt(this.$outer.step$1)).$plus(BigInt$.MODULE$.long2bigInt(this.$outer.start$1));
                  BigInt partitionEnd = BigInt$.MODULE$.int2bigInt(i + 1).$times(this.numElements$1).$div(BigInt$.MODULE$.int2bigInt(this.$outer.numSlices$2)).$times(BigInt$.MODULE$.long2bigInt(this.$outer.step$1)).$plus(BigInt$.MODULE$.long2bigInt(this.$outer.start$1));
                  long safePartitionStart = this.getSafeMargin$1(partitionStart);
                  long safePartitionEnd = this.getSafeMargin$1(partitionEnd);
                  return new Iterator<Object>(this, safePartitionStart, safePartitionEnd) {
                     private long number;
                     private boolean overflow;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final long safePartitionEnd$1;

                     public Iterator<Object> seq() {
                        return Iterator$class.seq(this);
                     }

                     public boolean isEmpty() {
                        return Iterator$class.isEmpty(this);
                     }

                     public boolean isTraversableAgain() {
                        return Iterator$class.isTraversableAgain(this);
                     }

                     public boolean hasDefiniteSize() {
                        return Iterator$class.hasDefiniteSize(this);
                     }

                     public Iterator<Object> take(int n) {
                        return Iterator$class.take(this, n);
                     }

                     public Iterator<Object> drop(int n) {
                        return Iterator$class.drop(this, n);
                     }

                     public Iterator<Object> slice(int from, int until) {
                        return Iterator$class.slice(this, from, until);
                     }

                     public <B> Iterator<B> map(Function1<Object, B> f) {
                        return Iterator$class.map(this, f);
                     }

                     public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
                        return Iterator$class.$plus$plus(this, that);
                     }

                     public <B> Iterator<B> flatMap(Function1<Object, GenTraversableOnce<B>> f) {
                        return Iterator$class.flatMap(this, f);
                     }

                     public Iterator<Object> filter(Function1<Object, Object> p) {
                        return Iterator$class.filter(this, p);
                     }

                     public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Object, B, Object> p) {
                        return Iterator$class.corresponds(this, that, p);
                     }

                     public Iterator<Object> withFilter(Function1<Object, Object> p) {
                        return Iterator$class.withFilter(this, p);
                     }

                     public Iterator<Object> filterNot(Function1<Object, Object> p) {
                        return Iterator$class.filterNot(this, p);
                     }

                     public <B> Iterator<B> collect(PartialFunction<Object, B> pf) {
                        return Iterator$class.collect(this, pf);
                     }

                     public <B> Iterator<B> scanLeft(B z, Function2<B, Object, B> op) {
                        return Iterator$class.scanLeft(this, z, op);
                     }

                     public <B> Iterator<B> scanRight(B z, Function2<Object, B, B> op) {
                        return Iterator$class.scanRight(this, z, op);
                     }

                     public Iterator<Object> takeWhile(Function1<Object, Object> p) {
                        return Iterator$class.takeWhile(this, p);
                     }

                     public Tuple2<Iterator<Object>, Iterator<Object>> partition(Function1<Object, Object> p) {
                        return Iterator$class.partition(this, p);
                     }

                     public Tuple2<Iterator<Object>, Iterator<Object>> span(Function1<Object, Object> p) {
                        return Iterator$class.span(this, p);
                     }

                     public Iterator<Object> dropWhile(Function1<Object, Object> p) {
                        return Iterator$class.dropWhile(this, p);
                     }

                     public <B> Iterator<Tuple2<Object, B>> zip(Iterator<B> that) {
                        return Iterator$class.zip(this, that);
                     }

                     public <A1> Iterator<A1> padTo(int len, A1 elem) {
                        return Iterator$class.padTo(this, len, elem);
                     }

                     public Iterator<Tuple2<Object, Object>> zipWithIndex() {
                        return Iterator$class.zipWithIndex(this);
                     }

                     public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
                        return Iterator$class.zipAll(this, that, thisElem, thatElem);
                     }

                     public <U> void foreach(Function1<Object, U> f) {
                        Iterator$class.foreach(this, f);
                     }

                     public boolean forall(Function1<Object, Object> p) {
                        return Iterator$class.forall(this, p);
                     }

                     public boolean exists(Function1<Object, Object> p) {
                        return Iterator$class.exists(this, p);
                     }

                     public boolean contains(Object elem) {
                        return Iterator$class.contains(this, elem);
                     }

                     public Option<Object> find(Function1<Object, Object> p) {
                        return Iterator$class.find(this, p);
                     }

                     public int indexWhere(Function1<Object, Object> p) {
                        return Iterator$class.indexWhere(this, p);
                     }

                     public <B> int indexOf(B elem) {
                        return Iterator$class.indexOf(this, elem);
                     }

                     public BufferedIterator<Object> buffered() {
                        return Iterator$class.buffered(this);
                     }

                     public <B> Iterator<Object>.GroupedIterator<B> grouped(int size) {
                        return Iterator$class.grouped(this, size);
                     }

                     public <B> Iterator<Object>.GroupedIterator<B> sliding(int size, int step) {
                        return Iterator$class.sliding(this, size, step);
                     }

                     public int length() {
                        return Iterator$class.length(this);
                     }

                     public Tuple2<Iterator<Object>, Iterator<Object>> duplicate() {
                        return Iterator$class.duplicate(this);
                     }

                     public <B> Iterator<B> patch(int from, Iterator<B> patchElems, int replaced) {
                        return Iterator$class.patch(this, from, patchElems, replaced);
                     }

                     public <B> void copyToArray(Object xs, int start, int len) {
                        Iterator$class.copyToArray(this, xs, start, len);
                     }

                     public boolean sameElements(Iterator<?> that) {
                        return Iterator$class.sameElements(this, that);
                     }

                     public Traversable<Object> toTraversable() {
                        return Iterator$class.toTraversable(this);
                     }

                     public Iterator<Object> toIterator() {
                        return Iterator$class.toIterator(this);
                     }

                     public Stream<Object> toStream() {
                        return Iterator$class.toStream(this);
                     }

                     public String toString() {
                        return Iterator$class.toString(this);
                     }

                     public <B> int sliding$default$2() {
                        return Iterator$class.sliding$default$2(this);
                     }

                     public List<Object> reversed() {
                        return TraversableOnce$class.reversed(this);
                     }

                     public int size() {
                        return TraversableOnce$class.size(this);
                     }

                     public boolean nonEmpty() {
                        return TraversableOnce$class.nonEmpty(this);
                     }

                     public int count(Function1<Object, Object> p) {
                        return TraversableOnce$class.count(this, p);
                     }

                     public <B> Option<B> collectFirst(PartialFunction<Object, B> pf) {
                        return TraversableOnce$class.collectFirst(this, pf);
                     }

                     public <B> B $div$colon(B z, Function2<B, Object, B> op) {
                        return (B)TraversableOnce$class.$div$colon(this, z, op);
                     }

                     public <B> B $colon$bslash(B z, Function2<Object, B, B> op) {
                        return (B)TraversableOnce$class.$colon$bslash(this, z, op);
                     }

                     public <B> B foldLeft(B z, Function2<B, Object, B> op) {
                        return (B)TraversableOnce$class.foldLeft(this, z, op);
                     }

                     public <B> B foldRight(B z, Function2<Object, B, B> op) {
                        return (B)TraversableOnce$class.foldRight(this, z, op);
                     }

                     public <B> B reduceLeft(Function2<B, Object, B> op) {
                        return (B)TraversableOnce$class.reduceLeft(this, op);
                     }

                     public <B> B reduceRight(Function2<Object, B, B> op) {
                        return (B)TraversableOnce$class.reduceRight(this, op);
                     }

                     public <B> Option<B> reduceLeftOption(Function2<B, Object, B> op) {
                        return TraversableOnce$class.reduceLeftOption(this, op);
                     }

                     public <B> Option<B> reduceRightOption(Function2<Object, B, B> op) {
                        return TraversableOnce$class.reduceRightOption(this, op);
                     }

                     public <A1> A1 reduce(Function2<A1, A1, A1> op) {
                        return (A1)TraversableOnce$class.reduce(this, op);
                     }

                     public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
                        return TraversableOnce$class.reduceOption(this, op);
                     }

                     public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
                        return (A1)TraversableOnce$class.fold(this, z, op);
                     }

                     public <B> B aggregate(Function0<B> z, Function2<B, Object, B> seqop, Function2<B, B, B> combop) {
                        return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
                     }

                     public <B> B sum(Numeric<B> num) {
                        return (B)TraversableOnce$class.sum(this, num);
                     }

                     public <B> B product(Numeric<B> num) {
                        return (B)TraversableOnce$class.product(this, num);
                     }

                     public Object min(Ordering cmp) {
                        return TraversableOnce$class.min(this, cmp);
                     }

                     public Object max(Ordering cmp) {
                        return TraversableOnce$class.max(this, cmp);
                     }

                     public Object maxBy(Function1 f, Ordering cmp) {
                        return TraversableOnce$class.maxBy(this, f, cmp);
                     }

                     public Object minBy(Function1 f, Ordering cmp) {
                        return TraversableOnce$class.minBy(this, f, cmp);
                     }

                     public <B> void copyToBuffer(Buffer<B> dest) {
                        TraversableOnce$class.copyToBuffer(this, dest);
                     }

                     public <B> void copyToArray(Object xs, int start) {
                        TraversableOnce$class.copyToArray(this, xs, start);
                     }

                     public <B> void copyToArray(Object xs) {
                        TraversableOnce$class.copyToArray(this, xs);
                     }

                     public <B> Object toArray(ClassTag<B> evidence$1) {
                        return TraversableOnce$class.toArray(this, evidence$1);
                     }

                     public List<Object> toList() {
                        return TraversableOnce$class.toList(this);
                     }

                     public Iterable<Object> toIterable() {
                        return TraversableOnce$class.toIterable(this);
                     }

                     public Seq<Object> toSeq() {
                        return TraversableOnce$class.toSeq(this);
                     }

                     public IndexedSeq<Object> toIndexedSeq() {
                        return TraversableOnce$class.toIndexedSeq(this);
                     }

                     public <B> Buffer<B> toBuffer() {
                        return TraversableOnce$class.toBuffer(this);
                     }

                     public <B> Set<B> toSet() {
                        return TraversableOnce$class.toSet(this);
                     }

                     public Vector<Object> toVector() {
                        return TraversableOnce$class.toVector(this);
                     }

                     public <Col> Col to(CanBuildFrom<Nothing$, Object, Col> cbf) {
                        return (Col)TraversableOnce$class.to(this, cbf);
                     }

                     public <T, U> scala.collection.immutable.Map<T, U> toMap(Predef.$less$colon$less<Object, Tuple2<T, U>> ev) {
                        return TraversableOnce$class.toMap(this, ev);
                     }

                     public String mkString(String start, String sep, String end) {
                        return TraversableOnce$class.mkString(this, start, sep, end);
                     }

                     public String mkString(String sep) {
                        return TraversableOnce$class.mkString(this, sep);
                     }

                     public String mkString() {
                        return TraversableOnce$class.mkString(this);
                     }

                     public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
                        return TraversableOnce$class.addString(this, b, start, sep, end);
                     }

                     public StringBuilder addString(StringBuilder b, String sep) {
                        return TraversableOnce$class.addString(this, b, sep);
                     }

                     public StringBuilder addString(StringBuilder b) {
                        return TraversableOnce$class.addString(this, b);
                     }

                     public boolean hasNext() {
                        return this.overflow ? false : (this.$outer.org$apache$spark$SparkContext$$anonfun$$anonfun$$$outer().step$1 > 0L ? this.number < this.safePartitionEnd$1 : this.number > this.safePartitionEnd$1);
                     }

                     public long next() {
                        long ret = this.number;
                        this.number += this.$outer.org$apache$spark$SparkContext$$anonfun$$anonfun$$$outer().step$1;
                        if (this.number < ret ^ this.$outer.org$apache$spark$SparkContext$$anonfun$$anonfun$$$outer().step$1 < 0L) {
                           this.overflow = true;
                        }

                        return ret;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.safePartitionEnd$1 = safePartitionEnd$1;
                           TraversableOnce$class.$init$(this);
                           Iterator$class.$init$(this);
                           this.number = safePartitionStart$1;
                           this.overflow = false;
                        }
                     }
                  };
               }

               // $FF: synthetic method
               public <undefinedtype> org$apache$spark$SparkContext$$anonfun$$anonfun$$$outer() {
                  return this.$outer;
               }

               private final long getSafeMargin$1(BigInt bi) {
                  return bi.isValidLong() ? bi.toLong() : (bi.$greater(BigInt$.MODULE$.int2bigInt(0)) ? Long.MAX_VALUE : Long.MIN_VALUE);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.numElements$1 = numElements$1;
                  }
               }
            };
            boolean x$62 = qual$1.mapPartitionsWithIndex$default$2();
            return qual$1.<Object>mapPartitionsWithIndex(x$61, x$62, ClassTag$.MODULE$.Long());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.start$1 = start$1;
               this.end$1 = end$1;
               this.step$1 = step$1;
               this.numSlices$2 = numSlices$2;
            }
         }
      });
   }

   public long range$default$3() {
      return 1L;
   }

   public int range$default$4() {
      return this.defaultParallelism();
   }

   public <T> RDD<T> makeRDD(Seq<T> seq, int numSlices, ClassTag<T> evidence$2) {
      return (RDD)this.withScope(new Serializable(this, seq, numSlices, evidence$2) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final Seq seq$2;
         private final int numSlices$3;
         private final ClassTag evidence$2$1;

         public final RDD<T> apply() {
            return this.$outer.<T>parallelize(this.seq$2, this.numSlices$3, this.evidence$2$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.seq$2 = seq$2;
               this.numSlices$3 = numSlices$3;
               this.evidence$2$1 = evidence$2$1;
            }
         }
      });
   }

   public <T> RDD<T> makeRDD(Seq<Tuple2<T, Seq<String>>> seq, ClassTag<T> evidence$3) {
      return (RDD)this.withScope(new Serializable(this, seq, evidence$3) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final Seq seq$3;
         private final ClassTag evidence$3$1;

         public final ParallelCollectionRDD<T> apply() {
            this.$outer.assertNotStopped();
            scala.collection.immutable.Map indexToPrefs = ((TraversableOnce)((TraversableLike)this.seq$3.zipWithIndex(Seq$.MODULE$.canBuildFrom())).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Tuple2<Object, Seq<String>> apply(Tuple2<Tuple2<T, Seq<String>>, Object> t) {
                  return new Tuple2<Object, Seq<String>>(BoxesRunTime.boxToInteger(t._2$mcI$sp()), ((Tuple2)t._1())._2());
               }
            }, Seq$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
            return new ParallelCollectionRDD<T>(this.$outer, (Seq)this.seq$3.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final T apply(Tuple2<T, Seq<String>> x$14) {
                  return (T)x$14._1();
               }
            }, Seq$.MODULE$.canBuildFrom()), scala.math.package$.MODULE$.max(this.seq$3.size(), 1), indexToPrefs, this.evidence$3$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.seq$3 = seq$3;
               this.evidence$3$1 = evidence$3$1;
            }
         }
      });
   }

   public <T> int makeRDD$default$2() {
      return this.defaultParallelism();
   }

   public RDD<String> textFile(String path, int minPartitions) {
      return (RDD)this.withScope(new Serializable(this, path, minPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$3;
         private final int minPartitions$1;

         public final RDD<String> apply() {
            this.$outer.assertNotStopped();
            return this.$outer.hadoopFile(this.path$3, TextInputFormat.class, LongWritable.class, Text.class, this.minPartitions$1).<String>map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(Tuple2<LongWritable, Text> pair) {
                  return ((Text)pair._2()).toString();
               }
            }, ClassTag$.MODULE$.apply(String.class)).setName(this.path$3);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$3 = path$3;
               this.minPartitions$1 = minPartitions$1;
            }
         }
      });
   }

   public int textFile$default$2() {
      return this.defaultMinPartitions();
   }

   public RDD<Tuple2<String, String>> wholeTextFiles(String path, int minPartitions) {
      return (RDD)this.withScope(new Serializable(this, path, minPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$7;
         private final int minPartitions$4;

         public final RDD<Tuple2<String, String>> apply() {
            this.$outer.assertNotStopped();
            Job job = Job.getInstance(this.$outer.hadoopConfiguration());
            FileInputFormat.setInputPaths(job, this.path$7);
            Configuration updateConf = job.getConfiguration();
            return (new WholeTextFileRDD(this.$outer, WholeTextFileInputFormat.class, Text.class, Text.class, updateConf, this.minPartitions$4)).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Tuple2<String, String> apply(Tuple2<Text, Text> record) {
                  return new Tuple2<String, String>(((Text)record._1()).toString(), ((Text)record._2()).toString());
               }
            }, ClassTag$.MODULE$.apply(Tuple2.class)).setName(this.path$7);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$7 = path$7;
               this.minPartitions$4 = minPartitions$4;
            }
         }
      });
   }

   public int wholeTextFiles$default$2() {
      return this.defaultMinPartitions();
   }

   public RDD<Tuple2<String, PortableDataStream>> binaryFiles(String path, int minPartitions) {
      return (RDD)this.withScope(new Serializable(this, path, minPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$8;
         private final int minPartitions$5;

         public final BinaryFileRDD<PortableDataStream> apply() {
            this.$outer.assertNotStopped();
            Job job = Job.getInstance(this.$outer.hadoopConfiguration());
            FileInputFormat.setInputPaths(job, this.path$8);
            Configuration updateConf = job.getConfiguration();
            return (BinaryFileRDD)(new BinaryFileRDD(this.$outer, StreamInputFormat.class, String.class, PortableDataStream.class, updateConf, this.minPartitions$5)).setName(this.path$8);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$8 = path$8;
               this.minPartitions$5 = minPartitions$5;
            }
         }
      });
   }

   public int binaryFiles$default$2() {
      return this.defaultMinPartitions();
   }

   public RDD<byte[]> binaryRecords(String path, int recordLength, Configuration conf) {
      return (RDD)this.withScope(new Serializable(this, path, recordLength, conf) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$9;
         public final int recordLength$1;
         private final Configuration conf$1;

         public final RDD<byte[]> apply() {
            this.$outer.assertNotStopped();
            this.conf$1.setInt(FixedLengthBinaryInputFormat$.MODULE$.RECORD_LENGTH_PROPERTY(), this.recordLength$1);
            RDD br = this.$outer.newAPIHadoopFile(this.path$9, FixedLengthBinaryInputFormat.class, LongWritable.class, BytesWritable.class, this.conf$1);
            return br.<byte[]>map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final byte[] apply(Tuple2<LongWritable, BytesWritable> x0$1) {
                  if (x0$1 != null) {
                     BytesWritable v = (BytesWritable)x0$1._2();
                     byte[] bytes = v.copyBytes();
                     Predef$.MODULE$.assert(bytes.length == this.$outer.recordLength$1, new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "Byte array does not have correct length";
                        }
                     });
                     return bytes;
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
            }, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$9 = path$9;
               this.recordLength$1 = recordLength$1;
               this.conf$1 = conf$1;
            }
         }
      });
   }

   public Configuration binaryRecords$default$3() {
      return this.hadoopConfiguration();
   }

   public <K, V> RDD<Tuple2<K, V>> hadoopRDD(JobConf conf, Class<? extends InputFormat<K, V>> inputFormatClass, Class<K> keyClass, Class<V> valueClass, int minPartitions) {
      return (RDD)this.withScope(new Serializable(this, conf, inputFormatClass, keyClass, valueClass, minPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final JobConf conf$3;
         private final Class inputFormatClass$2;
         private final Class keyClass$2;
         private final Class valueClass$2;
         private final int minPartitions$6;

         public final HadoopRDD<K, V> apply() {
            this.$outer.assertNotStopped();
            FileSystem.getLocal(this.conf$3);
            SparkHadoopUtil$.MODULE$.get().addCredentials(this.conf$3);
            return new HadoopRDD<K, V>(this.$outer, this.conf$3, this.inputFormatClass$2, this.keyClass$2, this.valueClass$2, this.minPartitions$6);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.conf$3 = conf$3;
               this.inputFormatClass$2 = inputFormatClass$2;
               this.keyClass$2 = keyClass$2;
               this.valueClass$2 = valueClass$2;
               this.minPartitions$6 = minPartitions$6;
            }
         }
      });
   }

   public <K, V> int hadoopRDD$default$5() {
      return this.defaultMinPartitions();
   }

   public <K, V> RDD<Tuple2<K, V>> hadoopFile(String path, Class<? extends InputFormat<K, V>> inputFormatClass, Class<K> keyClass, Class<V> valueClass, int minPartitions) {
      return (RDD)this.withScope(new Serializable(this, path, inputFormatClass, keyClass, valueClass, minPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         public final String path$6;
         private final Class inputFormatClass$1;
         private final Class keyClass$1;
         private final Class valueClass$1;
         private final int minPartitions$3;

         public final HadoopRDD<K, V> apply() {
            this.$outer.assertNotStopped();
            FileSystem.getLocal(this.$outer.hadoopConfiguration());
            Broadcast confBroadcast = this.$outer.broadcast(new SerializableConfiguration(this.$outer.hadoopConfiguration()), ClassTag$.MODULE$.apply(SerializableConfiguration.class));
            Function1 setInputPathsFunc = new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(JobConf jobConf) {
                  org.apache.hadoop.mapred.FileInputFormat.setInputPaths(jobConf, this.$outer.path$6);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            };
            return (HadoopRDD)(new HadoopRDD(this.$outer, confBroadcast, new Some(setInputPathsFunc), this.inputFormatClass$1, this.keyClass$1, this.valueClass$1, this.minPartitions$3)).setName(this.path$6);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$6 = path$6;
               this.inputFormatClass$1 = inputFormatClass$1;
               this.keyClass$1 = keyClass$1;
               this.valueClass$1 = valueClass$1;
               this.minPartitions$3 = minPartitions$3;
            }
         }
      });
   }

   public <K, V, F extends InputFormat<K, V>> RDD<Tuple2<K, V>> hadoopFile(String path, int minPartitions, ClassTag<K> km, ClassTag<V> vm, ClassTag<F> fm) {
      return (RDD)this.withScope(new Serializable(this, path, minPartitions, km, vm, fm) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$5;
         private final int minPartitions$2;
         private final ClassTag km$2;
         private final ClassTag vm$2;
         private final ClassTag fm$2;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.hadoopFile(this.path$5, this.fm$2.runtimeClass(), this.km$2.runtimeClass(), this.vm$2.runtimeClass(), this.minPartitions$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$5 = path$5;
               this.minPartitions$2 = minPartitions$2;
               this.km$2 = km$2;
               this.vm$2 = vm$2;
               this.fm$2 = fm$2;
            }
         }
      });
   }

   public <K, V, F extends InputFormat<K, V>> RDD<Tuple2<K, V>> hadoopFile(String path, ClassTag<K> km, ClassTag<V> vm, ClassTag<F> fm) {
      return (RDD)this.withScope(new Serializable(this, path, km, vm, fm) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$4;
         private final ClassTag km$1;
         private final ClassTag vm$1;
         private final ClassTag fm$1;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.hadoopFile(this.path$4, this.$outer.defaultMinPartitions(), this.km$1, this.vm$1, this.fm$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$4 = path$4;
               this.km$1 = km$1;
               this.vm$1 = vm$1;
               this.fm$1 = fm$1;
            }
         }
      });
   }

   public <K, V> int hadoopFile$default$5() {
      return this.defaultMinPartitions();
   }

   public <K, V, F extends org.apache.hadoop.mapreduce.InputFormat<K, V>> RDD<Tuple2<K, V>> newAPIHadoopFile(String path, ClassTag<K> km, ClassTag<V> vm, ClassTag<F> fm) {
      return (RDD)this.withScope(new Serializable(this, path, km, vm, fm) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$11;
         private final ClassTag km$3;
         private final ClassTag vm$3;
         private final ClassTag fm$3;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.newAPIHadoopFile(this.path$11, this.fm$3.runtimeClass(), this.km$3.runtimeClass(), this.vm$3.runtimeClass(), this.$outer.newAPIHadoopFile$default$5());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$11 = path$11;
               this.km$3 = km$3;
               this.vm$3 = vm$3;
               this.fm$3 = fm$3;
            }
         }
      });
   }

   public <K, V, F extends org.apache.hadoop.mapreduce.InputFormat<K, V>> RDD<Tuple2<K, V>> newAPIHadoopFile(String path, Class<F> fClass, Class<K> kClass, Class<V> vClass, Configuration conf) {
      return (RDD)this.withScope(new Serializable(this, path, fClass, kClass, vClass, conf) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$10;
         private final Class fClass$1;
         private final Class kClass$1;
         private final Class vClass$1;
         private final Configuration conf$2;

         public final NewHadoopRDD<K, V> apply() {
            this.$outer.assertNotStopped();
            FileSystem.getLocal(this.$outer.hadoopConfiguration());
            Job job = Job.getInstance(this.conf$2);
            FileInputFormat.setInputPaths(job, this.path$10);
            Configuration updatedConf = job.getConfiguration();
            return (NewHadoopRDD)(new NewHadoopRDD(this.$outer, this.fClass$1, this.kClass$1, this.vClass$1, updatedConf)).setName(this.path$10);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$10 = path$10;
               this.fClass$1 = fClass$1;
               this.kClass$1 = kClass$1;
               this.vClass$1 = vClass$1;
               this.conf$2 = conf$2;
            }
         }
      });
   }

   public <K, V, F extends org.apache.hadoop.mapreduce.InputFormat<K, V>> Configuration newAPIHadoopFile$default$5() {
      return this.hadoopConfiguration();
   }

   public <K, V, F extends org.apache.hadoop.mapreduce.InputFormat<K, V>> RDD<Tuple2<K, V>> newAPIHadoopRDD(Configuration conf, Class<F> fClass, Class<K> kClass, Class<V> vClass) {
      return (RDD)this.withScope(new Serializable(this, conf, fClass, kClass, vClass) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final Configuration conf$4;
         private final Class fClass$2;
         private final Class kClass$2;
         private final Class vClass$2;

         public final NewHadoopRDD<K, V> apply() {
            this.$outer.assertNotStopped();
            FileSystem.getLocal(this.conf$4);
            JobConf jconf = new JobConf(this.conf$4);
            SparkHadoopUtil$.MODULE$.get().addCredentials(jconf);
            return new NewHadoopRDD<K, V>(this.$outer, this.fClass$2, this.kClass$2, this.vClass$2, jconf);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.conf$4 = conf$4;
               this.fClass$2 = fClass$2;
               this.kClass$2 = kClass$2;
               this.vClass$2 = vClass$2;
            }
         }
      });
   }

   public <K, V, F extends org.apache.hadoop.mapreduce.InputFormat<K, V>> Configuration newAPIHadoopRDD$default$1() {
      return this.hadoopConfiguration();
   }

   public <K, V> RDD<Tuple2<K, V>> sequenceFile(String path, Class<K> keyClass, Class<V> valueClass, int minPartitions) {
      return (RDD)this.withScope(new Serializable(this, path, keyClass, valueClass, minPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$12;
         private final Class keyClass$3;
         private final Class valueClass$3;
         private final int minPartitions$7;

         public final RDD<Tuple2<K, V>> apply() {
            this.$outer.assertNotStopped();
            Class inputFormatClass = SequenceFileInputFormat.class;
            return this.$outer.hadoopFile(this.path$12, inputFormatClass, this.keyClass$3, this.valueClass$3, this.minPartitions$7);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$12 = path$12;
               this.keyClass$3 = keyClass$3;
               this.valueClass$3 = valueClass$3;
               this.minPartitions$7 = minPartitions$7;
            }
         }
      });
   }

   public <K, V> RDD<Tuple2<K, V>> sequenceFile(String path, Class<K> keyClass, Class<V> valueClass) {
      return (RDD)this.withScope(new Serializable(this, path, keyClass, valueClass) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$13;
         private final Class keyClass$4;
         private final Class valueClass$4;

         public final RDD<Tuple2<K, V>> apply() {
            this.$outer.assertNotStopped();
            return this.$outer.sequenceFile(this.path$13, this.keyClass$4, this.valueClass$4, this.$outer.defaultMinPartitions());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$13 = path$13;
               this.keyClass$4 = keyClass$4;
               this.valueClass$4 = valueClass$4;
            }
         }
      });
   }

   public <K, V> RDD<Tuple2<K, V>> sequenceFile(String path, int minPartitions, ClassTag<K> km, ClassTag<V> vm, Function0<WritableConverter<K>> kcf, Function0<WritableConverter<V>> vcf) {
      return (RDD)this.withScope(new Serializable(this, path, minPartitions, km, vm, kcf, vcf) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$14;
         private final int minPartitions$8;
         private final ClassTag km$4;
         private final ClassTag vm$4;
         private final Function0 kcf$1;
         private final Function0 vcf$1;

         public final RDD<Tuple2<K, V>> apply() {
            this.$outer.assertNotStopped();
            WritableConverter kc = (WritableConverter)((Function0)this.$outer.clean(this.kcf$1, this.$outer.clean$default$2())).apply();
            WritableConverter vc = (WritableConverter)((Function0)this.$outer.clean(this.vcf$1, this.$outer.clean$default$2())).apply();
            Class format = SequenceFileInputFormat.class;
            RDD writables = this.$outer.hadoopFile(this.path$14, format, (Class)kc.writableClass().apply(this.km$4), (Class)vc.writableClass().apply(this.vm$4), this.minPartitions$8);
            return writables.<Tuple2<K, V>>map(new Serializable(this, kc, vc) {
               public static final long serialVersionUID = 0L;
               private final WritableConverter kc$1;
               private final WritableConverter vc$1;

               public final Tuple2<K, V> apply(Tuple2<Writable, Writable> x0$2) {
                  if (x0$2 != null) {
                     Writable k = (Writable)x0$2._1();
                     Writable v = (Writable)x0$2._2();
                     Tuple2 var5 = new Tuple2(this.kc$1.convert().apply(k), this.vc$1.convert().apply(v));
                     return var5;
                  } else {
                     throw new MatchError(x0$2);
                  }
               }

               public {
                  this.kc$1 = kc$1;
                  this.vc$1 = vc$1;
               }
            }, ClassTag$.MODULE$.apply(Tuple2.class));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$14 = path$14;
               this.minPartitions$8 = minPartitions$8;
               this.km$4 = km$4;
               this.vm$4 = vm$4;
               this.kcf$1 = kcf$1;
               this.vcf$1 = vcf$1;
            }
         }
      });
   }

   public <K, V> int sequenceFile$default$2() {
      return this.defaultMinPartitions();
   }

   public <T> RDD<T> objectFile(String path, int minPartitions, ClassTag<T> evidence$4) {
      return (RDD)this.withScope(new Serializable(this, path, minPartitions, evidence$4) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$15;
         private final int minPartitions$9;
         private final ClassTag evidence$4$1;

         public final RDD<T> apply() {
            this.$outer.assertNotStopped();
            return this.$outer.sequenceFile(this.path$15, NullWritable.class, BytesWritable.class, this.minPartitions$9).<T>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final ArrayOps<T> apply(Tuple2<NullWritable, BytesWritable> x) {
                  return Predef$.MODULE$.<T>genericArrayOps(Utils$.MODULE$.deserialize(((BytesWritable)x._2()).getBytes(), Utils$.MODULE$.getContextOrSparkClassLoader()));
               }
            }, this.evidence$4$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$15 = path$15;
               this.minPartitions$9 = minPartitions$9;
               this.evidence$4$1 = evidence$4$1;
            }
         }
      });
   }

   public <T> int objectFile$default$2() {
      return this.defaultMinPartitions();
   }

   public <T> RDD<T> checkpointFile(String path, ClassTag<T> evidence$5) {
      return (RDD)this.withScope(new Serializable(this, path, evidence$5) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final String path$16;
         private final ClassTag evidence$5$1;

         public final ReliableCheckpointRDD<T> apply() {
            return new ReliableCheckpointRDD<T>(this.$outer, this.path$16, ReliableCheckpointRDD$.MODULE$.$lessinit$greater$default$3(), this.evidence$5$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$16 = path$16;
               this.evidence$5$1 = evidence$5$1;
            }
         }
      });
   }

   public <T> RDD<T> union(Seq<RDD<T>> rdds, ClassTag<T> evidence$6) {
      return (RDD)this.withScope(new Serializable(this, rdds, evidence$6) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final Seq rdds$1;
         private final ClassTag evidence$6$1;

         public final RDD<T> apply() {
            Set partitioners = ((TraversableOnce)this.rdds$1.flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Iterable<Partitioner> apply(RDD<T> x$15) {
                  return Option$.MODULE$.<Partitioner>option2Iterable(x$15.partitioner());
               }
            }, Seq$.MODULE$.canBuildFrom())).toSet();
            return (RDD<T>)(this.rdds$1.forall(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(RDD<T> x$16) {
                  return x$16.partitioner().isDefined();
               }
            }) && partitioners.size() == 1 ? new PartitionerAwareUnionRDD(this.$outer, this.rdds$1, this.evidence$6$1) : new UnionRDD(this.$outer, this.rdds$1, this.evidence$6$1));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.rdds$1 = rdds$1;
               this.evidence$6$1 = evidence$6$1;
            }
         }
      });
   }

   public <T> RDD<T> union(RDD<T> first, Seq<RDD<T>> rest, ClassTag<T> evidence$7) {
      return (RDD)this.withScope(new Serializable(this, first, rest, evidence$7) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final RDD first$1;
         private final Seq rest$1;
         private final ClassTag evidence$7$1;

         public final RDD<T> apply() {
            return this.$outer.<T>union((Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDD[]{this.first$1}))).$plus$plus(this.rest$1, Seq$.MODULE$.canBuildFrom()), this.evidence$7$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.first$1 = first$1;
               this.rest$1 = rest$1;
               this.evidence$7$1 = evidence$7$1;
            }
         }
      });
   }

   public <T> RDD<T> emptyRDD(ClassTag<T> evidence$8) {
      return new EmptyRDD<T>(this, evidence$8);
   }

   /** @deprecated */
   public <T> Accumulator<T> accumulator(T initialValue, AccumulatorParam<T> param) {
      Accumulator acc = new Accumulator(initialValue, param, Accumulator$.MODULE$.$lessinit$greater$default$3(), Accumulator$.MODULE$.$lessinit$greater$default$4());
      this.cleaner().foreach(new Serializable(this, acc) {
         public static final long serialVersionUID = 0L;
         private final Accumulator acc$1;

         public final void apply(ContextCleaner x$17) {
            x$17.registerAccumulatorForCleanup(this.acc$1.newAcc());
         }

         public {
            this.acc$1 = acc$1;
         }
      });
      return acc;
   }

   /** @deprecated */
   public <T> Accumulator<T> accumulator(T initialValue, String name, AccumulatorParam<T> param) {
      Accumulator acc = new Accumulator(initialValue, param, Option$.MODULE$.apply(name), Accumulator$.MODULE$.$lessinit$greater$default$4());
      this.cleaner().foreach(new Serializable(this, acc) {
         public static final long serialVersionUID = 0L;
         private final Accumulator acc$2;

         public final void apply(ContextCleaner x$18) {
            x$18.registerAccumulatorForCleanup(this.acc$2.newAcc());
         }

         public {
            this.acc$2 = acc$2;
         }
      });
      return acc;
   }

   /** @deprecated */
   public <R, T> Accumulable<R, T> accumulable(R initialValue, AccumulableParam<R, T> param) {
      Accumulable acc = new Accumulable(initialValue, param);
      this.cleaner().foreach(new Serializable(this, acc) {
         public static final long serialVersionUID = 0L;
         private final Accumulable acc$3;

         public final void apply(ContextCleaner x$19) {
            x$19.registerAccumulatorForCleanup(this.acc$3.newAcc());
         }

         public {
            this.acc$3 = acc$3;
         }
      });
      return acc;
   }

   /** @deprecated */
   public <R, T> Accumulable<R, T> accumulable(R initialValue, String name, AccumulableParam<R, T> param) {
      Accumulable acc = new Accumulable(initialValue, param, Option$.MODULE$.apply(name));
      this.cleaner().foreach(new Serializable(this, acc) {
         public static final long serialVersionUID = 0L;
         private final Accumulable acc$4;

         public final void apply(ContextCleaner x$20) {
            x$20.registerAccumulatorForCleanup(this.acc$4.newAcc());
         }

         public {
            this.acc$4 = acc$4;
         }
      });
      return acc;
   }

   /** @deprecated */
   public <R, T> Accumulable<R, T> accumulableCollection(R initialValue, Function1<R, Growable<T>> evidence$9, ClassTag<R> evidence$10) {
      GrowableAccumulableParam param = new GrowableAccumulableParam(evidence$10, evidence$9);
      Accumulable acc = new Accumulable(initialValue, param);
      this.cleaner().foreach(new Serializable(this, acc) {
         public static final long serialVersionUID = 0L;
         private final Accumulable acc$5;

         public final void apply(ContextCleaner x$21) {
            x$21.registerAccumulatorForCleanup(this.acc$5.newAcc());
         }

         public {
            this.acc$5 = acc$5;
         }
      });
      return acc;
   }

   public void register(AccumulatorV2<?, ?> acc) {
      acc.register(this, acc.register$default$2(), acc.register$default$3());
   }

   public void register(AccumulatorV2<?, ?> acc, String name) {
      acc.register(this, Option$.MODULE$.apply(name), acc.register$default$3());
   }

   public LongAccumulator longAccumulator() {
      LongAccumulator acc = new LongAccumulator();
      this.register(acc);
      return acc;
   }

   public LongAccumulator longAccumulator(String name) {
      LongAccumulator acc = new LongAccumulator();
      this.register(acc, name);
      return acc;
   }

   public DoubleAccumulator doubleAccumulator() {
      DoubleAccumulator acc = new DoubleAccumulator();
      this.register(acc);
      return acc;
   }

   public DoubleAccumulator doubleAccumulator(String name) {
      DoubleAccumulator acc = new DoubleAccumulator();
      this.register(acc, name);
      return acc;
   }

   public <T> CollectionAccumulator<T> collectionAccumulator() {
      CollectionAccumulator acc = new CollectionAccumulator();
      this.register(acc);
      return acc;
   }

   public <T> CollectionAccumulator<T> collectionAccumulator(String name) {
      CollectionAccumulator acc = new CollectionAccumulator();
      this.register(acc, name);
      return acc;
   }

   public <T> Broadcast<T> broadcast(T value, ClassTag<T> evidence$11) {
      this.assertNotStopped();
      Predef$.MODULE$.require(!RDD.class.isAssignableFrom(scala.reflect.package$.MODULE$.classTag(evidence$11).runtimeClass()), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Can not directly broadcast RDDs; instead, call collect() and broadcast the result.";
         }
      });
      Broadcast bc = this.env().broadcastManager().newBroadcast(value, this.isLocal(), evidence$11);
      CallSite callSite = this.getCallSite();
      this.logInfo(new Serializable(this, bc, callSite) {
         public static final long serialVersionUID = 0L;
         private final Broadcast bc$1;
         private final CallSite callSite$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Created broadcast ").append((Object)BoxesRunTime.boxToLong(this.bc$1.id())).append((Object)" from ").append((Object)this.callSite$1.shortForm()).toString();
         }

         public {
            this.bc$1 = bc$1;
            this.callSite$1 = callSite$1;
         }
      });
      this.cleaner().foreach(new Serializable(this, bc) {
         public static final long serialVersionUID = 0L;
         private final Broadcast bc$1;

         public final void apply(ContextCleaner x$22) {
            x$22.registerBroadcastForCleanup(this.bc$1);
         }

         public {
            this.bc$1 = bc$1;
         }
      });
      return bc;
   }

   public void addFile(String path) {
      this.addFile(path, false);
   }

   public Seq<String> listFiles() {
      return this.addedFiles().keySet().toSeq();
   }

   public void addFile(String path, boolean recursive) {
      URI uri = (new Path(path)).toUri();
      String var5 = uri.getScheme();
      boolean var6;
      if (var5 == null) {
         var6 = true;
      } else if ("local".equals(var5)) {
         var6 = true;
      } else {
         var6 = false;
      }

      String schemeCorrectedPath;
      if (var6) {
         schemeCorrectedPath = (new File(path)).getCanonicalFile().toURI().toString();
      } else {
         schemeCorrectedPath = path;
      }

      Path hadoopPath = new Path(schemeCorrectedPath);
      String scheme = (new URI(schemeCorrectedPath)).getScheme();
      if (Predef$.MODULE$.refArrayOps((Object[])(new String[]{"http", "https", "ftp"})).contains(scheme)) {
         Utils$.MODULE$.validateURL(uri);
      } else {
         FileSystem fs = hadoopPath.getFileSystem(this.hadoopConfiguration());
         boolean isDir = fs.getFileStatus(hadoopPath).isDirectory();
         if (!this.isLocal()) {
            label65: {
               String var12 = "file";
               if (scheme == null) {
                  if (var12 != null) {
                     break label65;
                  }
               } else if (!scheme.equals(var12)) {
                  break label65;
               }

               if (isDir) {
                  throw new SparkException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"addFile does not support local directories when not running "})))).s(Nil$.MODULE$)).append((Object)"local mode.").toString());
               }
            }
         }

         if (!recursive && isDir) {
            throw new SparkException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Added file ", " is a directory and recursive is not "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{hadoopPath}))).append((Object)"turned on.").toString());
         }
      }

      String var10000;
      label50: {
         label49: {
            if (!this.isLocal()) {
               String var14 = "file";
               if (scheme == null) {
                  if (var14 == null) {
                     break label49;
                  }
               } else if (scheme.equals(var14)) {
                  break label49;
               }
            }

            var10000 = schemeCorrectedPath;
            break label50;
         }

         var10000 = this.env().rpcEnv().fileServer().addFile(new File(uri.getPath()));
      }

      String key = var10000;
      long timestamp = System.currentTimeMillis();
      if (this.addedFiles().putIfAbsent(key, BoxesRunTime.boxToLong(timestamp)).isEmpty()) {
         this.logInfo(new Serializable(this, path, key, timestamp) {
            public static final long serialVersionUID = 0L;
            private final String path$2;
            private final String key$2;
            private final long timestamp$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Added file ", " at ", " with timestamp ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.path$2, this.key$2, BoxesRunTime.boxToLong(this.timestamp$1)}));
            }

            public {
               this.path$2 = path$2;
               this.key$2 = key$2;
               this.timestamp$1 = timestamp$1;
            }
         });
         Utils$.MODULE$.fetchFile(uri.toString(), new File(SparkFiles$.MODULE$.getRootDirectory()), this.conf(), this.env().securityManager(), this.hadoopConfiguration(), timestamp, false);
         this.postEnvironmentUpdate();
      }

   }

   @DeveloperApi
   public void addSparkListener(SparkListenerInterface listener) {
      this.listenerBus().addToSharedQueue(listener);
   }

   @DeveloperApi
   public void removeSparkListener(SparkListenerInterface listener) {
      this.listenerBus().removeListener(listener);
   }

   public Seq<String> getExecutorIds() {
      SchedulerBackend var1 = this.schedulerBackend();
      Object var3;
      if (var1 instanceof ExecutorAllocationClient) {
         var3 = ((ExecutorAllocationClient)var1).getExecutorIds();
      } else {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Requesting executors is not supported by current scheduler.";
            }
         });
         var3 = Nil$.MODULE$;
      }

      return (Seq<String>)var3;
   }

   @DeveloperApi
   public boolean requestTotalExecutors(int numExecutors, int localityAwareTasks, scala.collection.immutable.Map<String, Object> hostToLocalTaskCount) {
      SchedulerBackend var4 = this.schedulerBackend();
      boolean var6;
      if (var4 instanceof ExecutorAllocationClient) {
         var6 = ((ExecutorAllocationClient)var4).requestTotalExecutors(numExecutors, localityAwareTasks, hostToLocalTaskCount);
      } else {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Requesting executors is not supported by current scheduler.";
            }
         });
         var6 = false;
      }

      return var6;
   }

   @DeveloperApi
   public boolean requestExecutors(int numAdditionalExecutors) {
      SchedulerBackend var2 = this.schedulerBackend();
      boolean var4;
      if (var2 instanceof ExecutorAllocationClient) {
         var4 = ((ExecutorAllocationClient)var2).requestExecutors(numAdditionalExecutors);
      } else {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Requesting executors is not supported by current scheduler.";
            }
         });
         var4 = false;
      }

      return var4;
   }

   @DeveloperApi
   public boolean killExecutors(Seq<String> executorIds) {
      SchedulerBackend var2 = this.schedulerBackend();
      boolean var4;
      if (var2 instanceof ExecutorAllocationClient) {
         var4 = ((ExecutorAllocationClient)var2).killExecutors(executorIds, false, true).nonEmpty();
      } else {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Killing executors is not supported by current scheduler.";
            }
         });
         var4 = false;
      }

      return var4;
   }

   @DeveloperApi
   public boolean killExecutor(String executorId) {
      return this.killExecutors((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{executorId}))));
   }

   public boolean killAndReplaceExecutor(String executorId) {
      SchedulerBackend var2 = this.schedulerBackend();
      boolean var4;
      if (var2 instanceof ExecutorAllocationClient) {
         var4 = ((ExecutorAllocationClient)var2).killExecutors((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{executorId}))), true, true).nonEmpty();
      } else {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Killing executors is not supported by current scheduler.";
            }
         });
         var4 = false;
      }

      return var4;
   }

   public String version() {
      return package$.MODULE$.SPARK_VERSION();
   }

   public scala.collection.Map<String, Tuple2<Object, Object>> getExecutorMemoryStatus() {
      this.assertNotStopped();
      return (scala.collection.Map)this.env().blockManager().master().getMemoryStatus().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, Tuple2<Object, Object>> apply(Tuple2<BlockManagerId, Tuple2<Object, Object>> x0$3) {
            if (x0$3 != null) {
               BlockManagerId blockManagerId = (BlockManagerId)x0$3._1();
               Tuple2 mem = (Tuple2)x0$3._2();
               Tuple2 var5 = new Tuple2((new StringBuilder()).append((Object)blockManagerId.host()).append((Object)":").append((Object)BoxesRunTime.boxToInteger(blockManagerId.port())).toString(), mem);
               return var5;
            } else {
               throw new MatchError(x0$3);
            }
         }
      }, scala.collection.immutable.Map$.MODULE$.canBuildFrom());
   }

   @DeveloperApi
   public RDDInfo[] getRDDStorageInfo() {
      return this.getRDDStorageInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(RDD<?> x$23) {
            return true;
         }
      });
   }

   public RDDInfo[] getRDDStorageInfo(Function1<RDD<?>, Object> filter) {
      this.assertNotStopped();
      RDDInfo[] rddInfos = (RDDInfo[])((TraversableOnce)((TraversableLike)this.persistentRdds().values().filter(filter)).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final RDDInfo apply(RDD<?> rdd) {
            return RDDInfo$.MODULE$.fromRdd(rdd);
         }
      }, Iterable$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(RDDInfo.class));
      StorageUtils$.MODULE$.updateRddInfo(Predef$.MODULE$.wrapRefArray(rddInfos), Predef$.MODULE$.wrapRefArray(this.getExecutorStorageStatus()));
      return (RDDInfo[])Predef$.MODULE$.refArrayOps(rddInfos).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(RDDInfo x$24) {
            return x$24.isCached();
         }
      });
   }

   public scala.collection.Map<Object, RDD<?>> getPersistentRDDs() {
      return this.persistentRdds().toMap(Predef$.MODULE$.$conforms());
   }

   /** @deprecated */
   @DeveloperApi
   public StorageStatus[] getExecutorStorageStatus() {
      this.assertNotStopped();
      return this.env().blockManager().master().getStorageStatus();
   }

   @DeveloperApi
   public Seq<Schedulable> getAllPools() {
      this.assertNotStopped();
      return ((TraversableOnce)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.taskScheduler().rootPool().schedulableQueue()).asScala()).toSeq();
   }

   @DeveloperApi
   public Option<Schedulable> getPoolForName(String pool) {
      this.assertNotStopped();
      return Option$.MODULE$.<Schedulable>apply(this.taskScheduler().rootPool().schedulableNameToSchedulable().get(pool));
   }

   public Enumeration.Value getSchedulingMode() {
      this.assertNotStopped();
      return this.taskScheduler().schedulingMode();
   }

   public Seq<TaskLocation> getPreferredLocs(RDD<?> rdd, int partition) {
      return this.dagScheduler().getPreferredLocs(rdd, partition);
   }

   public void persistRDD(RDD<?> rdd) {
      this.persistentRdds().update(BoxesRunTime.boxToInteger(rdd.id()), rdd);
   }

   public void unpersistRDD(int rddId, boolean blocking) {
      this.env().blockManager().master().removeRdd(rddId, blocking);
      this.persistentRdds().remove(BoxesRunTime.boxToInteger(rddId));
      this.listenerBus().post(new SparkListenerUnpersistRDD(rddId));
   }

   public boolean unpersistRDD$default$2() {
      return true;
   }

   public void addJar(String path) {
      if (path == null) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "null specified as parameter to addJar";
            }
         });
      } else {
         String var10000;
         if (path.contains("\\")) {
            var10000 = this.addJarFile$1(new File(path), path);
         } else {
            URI uri = new URI(path);
            Utils$.MODULE$.validateURL(uri);
            String var4 = uri.getScheme();
            String var5;
            if (var4 == null) {
               var5 = this.addJarFile$1(new File(uri.getRawPath()), path);
            } else if ("file".equals(var4)) {
               var5 = this.addJarFile$1(new File(uri.getPath()), path);
            } else if ("local".equals(var4)) {
               var5 = (new StringBuilder()).append((Object)"file:").append((Object)uri.getPath()).toString();
            } else {
               var5 = path;
            }

            var10000 = var5;
         }

         String key = var10000;
         if (key != null) {
            long timestamp = System.currentTimeMillis();
            if (this.addedJars().putIfAbsent(key, BoxesRunTime.boxToLong(timestamp)).isEmpty()) {
               this.logInfo(new Serializable(this, path, key, timestamp) {
                  public static final long serialVersionUID = 0L;
                  private final String path$1;
                  private final String key$3;
                  private final long timestamp$2;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Added JAR ", " at ", " with timestamp ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.path$1, this.key$3, BoxesRunTime.boxToLong(this.timestamp$2)}));
                  }

                  public {
                     this.path$1 = path$1;
                     this.key$3 = key$3;
                     this.timestamp$2 = timestamp$2;
                  }
               });
               this.postEnvironmentUpdate();
            }
         }
      }

   }

   public Seq<String> listJars() {
      return this.addedJars().keySet().toSeq();
   }

   public void stopInNewThread() {
      (new Thread(this) {
         // $FF: synthetic field
         private final SparkContext $outer;

         public void run() {
            try {
               this.$outer.stop();
            } catch (Throwable var3) {
               this.$outer.logError(new Serializable(this, var3) {
                  public static final long serialVersionUID = 0L;
                  private final Throwable e$1;

                  public final String apply() {
                     return this.e$1.getMessage();
                  }

                  public {
                     this.e$1 = e$1;
                  }
               }, var3);
               throw var3;
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.setDaemon(true);
            }
         }
      }).start();
   }

   public void stop() {
      if (BoxesRunTime.unboxToBoolean(LiveListenerBus$.MODULE$.withinListenerThread().value())) {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot stop SparkContext within listener bus thread."})))).s(Nil$.MODULE$));
      } else if (this.stopped().compareAndSet(false, true)) {
         if (this._shutdownHookRef() == null) {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            BoxesRunTime.boxToBoolean(ShutdownHookManager$.MODULE$.removeShutdownHook(this._shutdownHookRef()));
         }

         Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$SparkContext$$postApplicationEnd();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$SparkContext$$_ui().foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final void apply(SparkUI x$25) {
                     x$25.stop();
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
         if (this.env() != null) {
            Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.env().metricsSystem().report();
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

         Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$SparkContext$$_cleaner().foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final void apply(ContextCleaner x$26) {
                     x$26.stop();
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
         Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$SparkContext$$_executorAllocationManager().foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final void apply(ExecutorAllocationManager x$27) {
                     x$27.stop();
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
         if (this.org$apache$spark$SparkContext$$_listenerBusStarted()) {
            Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.listenerBus().stop();
                  this.$outer.org$apache$spark$SparkContext$$_listenerBusStarted_$eq(false);
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

         Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$SparkContext$$_eventLogger().foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final void apply(EventLoggingListener x$28) {
                     x$28.stop();
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
         if (this.org$apache$spark$SparkContext$$_dagScheduler() != null) {
            Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.org$apache$spark$SparkContext$$_dagScheduler().stop();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.org$apache$spark$SparkContext$$_dagScheduler_$eq((DAGScheduler)null);
         }

         if (this.env() != null && this.org$apache$spark$SparkContext$$_heartbeatReceiver() != null) {
            Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.env().rpcEnv().stop(this.$outer.org$apache$spark$SparkContext$$_heartbeatReceiver());
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

         Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$SparkContext$$_progressBar().foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final void apply(ConsoleProgressBar x$29) {
                     x$29.stop();
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
         this._taskScheduler_$eq((TaskScheduler)null);
         if (this.org$apache$spark$SparkContext$$_env() != null) {
            Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.org$apache$spark$SparkContext$$_env().stop();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            SparkEnv$.MODULE$.set((SparkEnv)null);
         }

         if (this._statusStore() != null) {
            this._statusStore().close();
         }

         this.localProperties().remove();
         SparkContext$.MODULE$.clearActiveContext();
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Successfully stopped SparkContext";
            }
         });
      } else {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "SparkContext already stopped.";
            }
         });
      }
   }

   public Option<String> getSparkHome() {
      return this.conf().getOption("spark.home").<String>orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply() {
            return Option$.MODULE$.<String>apply(System.getenv("SPARK_HOME"));
         }
      });
   }

   public void setCallSite(String shortCallSite) {
      this.setLocalProperty(CallSite$.MODULE$.SHORT_FORM(), shortCallSite);
   }

   public void setCallSite(CallSite callSite) {
      this.setLocalProperty(CallSite$.MODULE$.SHORT_FORM(), callSite.shortForm());
      this.setLocalProperty(CallSite$.MODULE$.LONG_FORM(), callSite.longForm());
   }

   public void clearCallSite() {
      this.setLocalProperty(CallSite$.MODULE$.SHORT_FORM(), (String)null);
      this.setLocalProperty(CallSite$.MODULE$.LONG_FORM(), (String)null);
   }

   public CallSite getCallSite() {
      ObjectRef callSite$lzy = ObjectRef.zero();
      VolatileByteRef bitmap$0 = VolatileByteRef.create((byte)0);
      return new CallSite((String)Option$.MODULE$.apply(this.getLocalProperty(CallSite$.MODULE$.SHORT_FORM())).getOrElse(new Serializable(this, callSite$lzy, bitmap$0) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final ObjectRef callSite$lzy$1;
         private final VolatileByteRef bitmap$0$1;

         public final String apply() {
            return this.$outer.org$apache$spark$SparkContext$$callSite$2(this.callSite$lzy$1, this.bitmap$0$1).shortForm();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.callSite$lzy$1 = callSite$lzy$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }
      }), (String)Option$.MODULE$.apply(this.getLocalProperty(CallSite$.MODULE$.LONG_FORM())).getOrElse(new Serializable(this, callSite$lzy, bitmap$0) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;
         private final ObjectRef callSite$lzy$1;
         private final VolatileByteRef bitmap$0$1;

         public final String apply() {
            return this.$outer.org$apache$spark$SparkContext$$callSite$2(this.callSite$lzy$1, this.bitmap$0$1).longForm();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.callSite$lzy$1 = callSite$lzy$1;
               this.bitmap$0$1 = bitmap$0$1;
            }
         }
      }));
   }

   public <T, U> void runJob(RDD<T> rdd, Function2<TaskContext, Iterator<T>, U> func, Seq<Object> partitions, Function2<Object, U, BoxedUnit> resultHandler, ClassTag<U> evidence$12) {
      if (this.stopped().get()) {
         throw new IllegalStateException("SparkContext has been shutdown");
      } else {
         CallSite callSite = this.getCallSite();
         Function2 cleanedFunc = (Function2)this.clean(func, this.clean$default$2());
         this.logInfo(new Serializable(this, callSite) {
            public static final long serialVersionUID = 0L;
            private final CallSite callSite$3;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Starting job: ").append((Object)this.callSite$3.shortForm()).toString();
            }

            public {
               this.callSite$3 = callSite$3;
            }
         });
         if (this.conf().getBoolean("spark.logLineage", false)) {
            this.logInfo(new Serializable(this, rdd) {
               public static final long serialVersionUID = 0L;
               private final RDD rdd$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"RDD's recursive dependencies:\n").append((Object)this.rdd$1.toDebugString()).toString();
               }

               public {
                  this.rdd$1 = rdd$1;
               }
            });
         }

         this.dagScheduler().runJob(rdd, cleanedFunc, partitions, callSite, resultHandler, (Properties)this.localProperties().get());
         this.progressBar().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(ConsoleProgressBar x$30) {
               x$30.finishAll();
            }
         });
         rdd.doCheckpoint();
      }
   }

   public <T, U> Object runJob(RDD<T> rdd, Function2<TaskContext, Iterator<T>, U> func, Seq<Object> partitions, ClassTag<U> evidence$13) {
      Object results = evidence$13.newArray(partitions.size());
      this.runJob(rdd, func, partitions, new Serializable(this, results) {
         public static final long serialVersionUID = 0L;
         private final Object results$1;

         public final void apply(int index, U res) {
            ScalaRunTime$.MODULE$.array_update(this.results$1, index, res);
         }

         public {
            this.results$1 = results$1;
         }
      }, evidence$13);
      return results;
   }

   public <T, U> Object runJob(RDD<T> rdd, Function1<Iterator<T>, U> func, Seq<Object> partitions, ClassTag<U> evidence$14) {
      Function1 cleanedFunc = (Function1)this.clean(func, this.clean$default$2());
      return this.runJob(rdd, new Serializable(this, cleanedFunc) {
         public static final long serialVersionUID = 0L;
         private final Function1 cleanedFunc$1;

         public final U apply(TaskContext ctx, Iterator<T> it) {
            return (U)this.cleanedFunc$1.apply(it);
         }

         public {
            this.cleanedFunc$1 = cleanedFunc$1;
         }
      }, partitions, evidence$14);
   }

   public <T, U> Object runJob(RDD<T> rdd, Function2<TaskContext, Iterator<T>, U> func, ClassTag<U> evidence$15) {
      return this.runJob(rdd, func, RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), rdd.partitions().length), evidence$15);
   }

   public <T, U> Object runJob(RDD<T> rdd, Function1<Iterator<T>, U> func, ClassTag<U> evidence$16) {
      return this.runJob(rdd, func, RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), rdd.partitions().length), evidence$16);
   }

   public <T, U> void runJob(RDD<T> rdd, Function2<TaskContext, Iterator<T>, U> processPartition, Function2<Object, U, BoxedUnit> resultHandler, ClassTag<U> evidence$17) {
      this.runJob(rdd, processPartition, RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), rdd.partitions().length), resultHandler, evidence$17);
   }

   public <T, U> void runJob(RDD<T> rdd, Function1<Iterator<T>, U> processPartition, Function2<Object, U, BoxedUnit> resultHandler, ClassTag<U> evidence$18) {
      Function2 processFunc = new Serializable(this, processPartition) {
         public static final long serialVersionUID = 0L;
         private final Function1 processPartition$1;

         public final U apply(TaskContext context, Iterator<T> iter) {
            return (U)this.processPartition$1.apply(iter);
         }

         public {
            this.processPartition$1 = processPartition$1;
         }
      };
      this.runJob(rdd, processFunc, RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), rdd.partitions().length), resultHandler, evidence$18);
   }

   @DeveloperApi
   public <T, U, R> PartialResult<R> runApproximateJob(RDD<T> rdd, Function2<TaskContext, Iterator<T>, U> func, ApproximateEvaluator<U, R> evaluator, long timeout) {
      this.assertNotStopped();
      CallSite callSite = this.getCallSite();
      this.logInfo(new Serializable(this, callSite) {
         public static final long serialVersionUID = 0L;
         private final CallSite callSite$4;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Starting job: ").append((Object)this.callSite$4.shortForm()).toString();
         }

         public {
            this.callSite$4 = callSite$4;
         }
      });
      long start = System.nanoTime();
      Function2 cleanedFunc = (Function2)this.clean(func, this.clean$default$2());
      PartialResult result = this.dagScheduler().runApproximateJob(rdd, cleanedFunc, evaluator, callSite, timeout, (Properties)this.localProperties().get());
      this.logInfo(new Serializable(this, callSite, start) {
         public static final long serialVersionUID = 0L;
         private final CallSite callSite$4;
         private final long start$2;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Job finished: ").append((Object)this.callSite$4.shortForm()).append((Object)", took ").append((Object)BoxesRunTime.boxToDouble((double)(System.nanoTime() - this.start$2) / (double)1.0E9F)).append((Object)" s").toString();
         }

         public {
            this.callSite$4 = callSite$4;
            this.start$2 = start$2;
         }
      });
      return result;
   }

   public <T, U, R> SimpleFutureAction<R> submitJob(RDD<T> rdd, Function1<Iterator<T>, U> processPartition, Seq<Object> partitions, Function2<Object, U, BoxedUnit> resultHandler, Function0<R> resultFunc) {
      this.assertNotStopped();
      Function1 cleanF = (Function1)this.clean(processPartition, this.clean$default$2());
      CallSite callSite = this.getCallSite();
      JobWaiter waiter = this.dagScheduler().submitJob(rdd, new Serializable(this, cleanF) {
         public static final long serialVersionUID = 0L;
         private final Function1 cleanF$1;

         public final U apply(TaskContext context, Iterator<T> iter) {
            return (U)this.cleanF$1.apply(iter);
         }

         public {
            this.cleanF$1 = cleanF$1;
         }
      }, partitions, callSite, resultHandler, (Properties)this.localProperties().get());
      return new SimpleFutureAction<R>(waiter, resultFunc);
   }

   public <K, V, C> SimpleFutureAction<MapOutputStatistics> submitMapStage(ShuffleDependency<K, V, C> dependency) {
      this.assertNotStopped();
      CallSite callSite = this.getCallSite();
      ObjectRef result = ObjectRef.create((Object)null);
      JobWaiter waiter = this.dagScheduler().submitMapStage(dependency, new Serializable(this, result) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef result$1;

         public final void apply(MapOutputStatistics r) {
            this.result$1.elem = (T)r;
         }

         public {
            this.result$1 = result$1;
         }
      }, callSite, (Properties)this.localProperties().get());
      return new SimpleFutureAction<MapOutputStatistics>(waiter, new Serializable(this, result) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef result$1;

         public final MapOutputStatistics apply() {
            return (MapOutputStatistics)this.result$1.elem;
         }

         public {
            this.result$1 = result$1;
         }
      });
   }

   public void cancelJobGroup(String groupId) {
      this.assertNotStopped();
      this.dagScheduler().cancelJobGroup(groupId);
   }

   public void cancelAllJobs() {
      this.assertNotStopped();
      this.dagScheduler().cancelAllJobs();
   }

   public void cancelJob(int jobId, String reason) {
      this.dagScheduler().cancelJob(jobId, Option$.MODULE$.apply(reason));
   }

   public void cancelJob(int jobId) {
      this.dagScheduler().cancelJob(jobId, None$.MODULE$);
   }

   public void cancelStage(int stageId, String reason) {
      this.dagScheduler().cancelStage(stageId, Option$.MODULE$.apply(reason));
   }

   public void cancelStage(int stageId) {
      this.dagScheduler().cancelStage(stageId, None$.MODULE$);
   }

   public boolean killTaskAttempt(long taskId, boolean interruptThread, String reason) {
      return this.dagScheduler().killTaskAttempt(taskId, interruptThread, reason);
   }

   public boolean killTaskAttempt$default$2() {
      return true;
   }

   public String killTaskAttempt$default$3() {
      return "killed via SparkContext.killTaskAttempt";
   }

   public <F> F clean(F f, boolean checkSerializable) {
      ClosureCleaner$.MODULE$.clean(f, checkSerializable, ClosureCleaner$.MODULE$.clean$default$3());
      return (F)f;
   }

   public <F> boolean clean$default$2() {
      return true;
   }

   public void setCheckpointDir(String directory) {
      if (!this.isLocal() && Predef$.MODULE$.refArrayOps((Object[])Utils$.MODULE$.nonLocalPaths(directory, Utils$.MODULE$.nonLocalPaths$default$2())).isEmpty()) {
         this.logWarning(new Serializable(this, directory) {
            public static final long serialVersionUID = 0L;
            private final String directory$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Spark is not running in local mode, therefore the checkpoint directory ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"must not be on the local filesystem. Directory '", "' "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.directory$1}))).append((Object)"appears to be on the local filesystem.").toString();
            }

            public {
               this.directory$1 = directory$1;
            }
         });
      }

      this.checkpointDir_$eq(Option$.MODULE$.apply(directory).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkContext $outer;

         public final String apply(String dir) {
            Path path = new Path(dir, UUID.randomUUID().toString());
            FileSystem fs = path.getFileSystem(this.$outer.hadoopConfiguration());
            fs.mkdirs(path);
            return fs.getFileStatus(path).getPath().toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
   }

   public Option<String> getCheckpointDir() {
      return this.checkpointDir();
   }

   public int defaultParallelism() {
      this.assertNotStopped();
      return this.taskScheduler().defaultParallelism();
   }

   public int defaultMinPartitions() {
      return scala.math.package$.MODULE$.min(this.defaultParallelism(), 2);
   }

   private AtomicInteger nextShuffleId() {
      return this.nextShuffleId;
   }

   public int newShuffleId() {
      return this.nextShuffleId().getAndIncrement();
   }

   private AtomicInteger nextRddId() {
      return this.nextRddId;
   }

   public int newRddId() {
      return this.nextRddId().getAndIncrement();
   }

   private void setupAndStartListenerBus() {
      label30: {
         try {
            ((Option)this.conf().get((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.EXTRA_LISTENERS())).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final void apply(Seq<String> classNames) {
                  Seq listeners = Utils$.MODULE$.loadExtensions(SparkListenerInterface.class, classNames, this.$outer.conf());
                  listeners.foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final void apply(SparkListenerInterface listener) {
                        this.$outer.org$apache$spark$SparkContext$$anonfun$$$outer().listenerBus().addToSharedQueue(listener);
                        this.$outer.org$apache$spark$SparkContext$$anonfun$$$outer().logInfo(new Serializable(this, listener) {
                           public static final long serialVersionUID = 0L;
                           private final SparkListenerInterface listener$1;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Registered listener ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.listener$1.getClass().getName()}));
                           }

                           public {
                              this.listener$1 = listener$1;
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
               public SparkContext org$apache$spark$SparkContext$$anonfun$$$outer() {
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
         } catch (Exception var6) {
            try {
               this.stop();
            } finally {
               throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception when registering SparkListener"})))).s(Nil$.MODULE$), var6);
            }
         }

         this.listenerBus().start(this, this.org$apache$spark$SparkContext$$_env().metricsSystem());
         this.org$apache$spark$SparkContext$$_listenerBusStarted_$eq(true);
      }
   }

   private void postApplicationStart() {
      this.listenerBus().post(new SparkListenerApplicationStart(this.appName(), new Some(this.applicationId()), this.startTime(), this.sparkUser(), this.applicationAttemptId(), this.schedulerBackend().getDriverLogUrls()));
   }

   public void org$apache$spark$SparkContext$$postApplicationEnd() {
      this.listenerBus().post(new SparkListenerApplicationEnd(System.currentTimeMillis()));
   }

   private void postEnvironmentUpdate() {
      if (this.taskScheduler() != null) {
         String schedulingMode = this.getSchedulingMode().toString();
         Seq addedJarPaths = this.addedJars().keys().toSeq();
         Seq addedFilePaths = this.addedFiles().keys().toSeq();
         scala.collection.immutable.Map environmentDetails = SparkEnv$.MODULE$.environmentDetails(this.conf(), schedulingMode, addedJarPaths, addedFilePaths);
         SparkListenerEnvironmentUpdate environmentUpdate = new SparkListenerEnvironmentUpdate(environmentDetails);
         this.listenerBus().post(environmentUpdate);
      }

   }

   private final String addJarFile$1(File file, String path$1) {
      String var10000;
      try {
         if (!file.exists()) {
            throw new FileNotFoundException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Jar ", " not found"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{file.getAbsolutePath()})));
         }

         if (file.isDirectory()) {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Directory ", " is not allowed for addJar"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{file.getAbsoluteFile()})));
         }

         var10000 = this.env().rpcEnv().fileServer().addJar(file);
      } catch (Throwable var9) {
         Option var5 = NonFatal$.MODULE$.unapply(var9);
         if (var5.isEmpty()) {
            throw var9;
         }

         Throwable e = (Throwable)var5.get();
         this.logError(new Serializable(this, path$1) {
            public static final long serialVersionUID = 0L;
            private final String path$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to add ", " to Spark environment"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.path$1}));
            }

            public {
               this.path$1 = path$1;
            }
         }, e);
         Object var7 = null;
         var10000 = (String)var7;
         return var10000;
      }

      return var10000;
   }

   private final CallSite callSite$lzycompute$1(ObjectRef callSite$lzy$1, VolatileByteRef bitmap$0$1) {
      synchronized(this) {
         if ((byte)(bitmap$0$1.elem & 1) == 0) {
            callSite$lzy$1.elem = (T)Utils$.MODULE$.getCallSite(Utils$.MODULE$.getCallSite$default$1());
            bitmap$0$1.elem = (byte)(bitmap$0$1.elem | 1);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return (CallSite)callSite$lzy$1.elem;
   }

   public final CallSite org$apache$spark$SparkContext$$callSite$2(ObjectRef callSite$lzy$1, VolatileByteRef bitmap$0$1) {
      return (byte)(bitmap$0$1.elem & 1) == 0 ? this.callSite$lzycompute$1(callSite$lzy$1, bitmap$0$1) : (CallSite)callSite$lzy$1.elem;
   }

   public SparkContext(SparkConf config) {
      Logging$class.$init$(this);
      this.org$apache$spark$SparkContext$$creationSite = Utils$.MODULE$.getCallSite(Utils$.MODULE$.getCallSite$default$1());
      this.allowMultipleContexts = config.getBoolean("spark.driver.allowMultipleContexts", false);
      SparkContext$.MODULE$.markPartiallyConstructed(this, this.allowMultipleContexts());
      this.startTime = System.currentTimeMillis();
      this.stopped = new AtomicBoolean(false);
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Running Spark version ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{package$.MODULE$.SPARK_VERSION()}));
         }
      });
      this._eventLogDir = None$.MODULE$;
      this._eventLogCodec = None$.MODULE$;
      this.org$apache$spark$SparkContext$$_progressBar = None$.MODULE$;
      this.org$apache$spark$SparkContext$$_ui = None$.MODULE$;
      this._applicationAttemptId = None$.MODULE$;
      this.org$apache$spark$SparkContext$$_eventLogger = None$.MODULE$;
      this.org$apache$spark$SparkContext$$_executorAllocationManager = None$.MODULE$;
      this.org$apache$spark$SparkContext$$_cleaner = None$.MODULE$;
      this.org$apache$spark$SparkContext$$_listenerBusStarted = false;
      this.addedFiles = (Map)JavaConverters$.MODULE$.mapAsScalaConcurrentMapConverter(new ConcurrentHashMap()).asScala();
      this.addedJars = (Map)JavaConverters$.MODULE$.mapAsScalaConcurrentMapConverter(new ConcurrentHashMap()).asScala();
      ConcurrentMap map = (new MapMaker()).weakValues().makeMap();
      this.persistentRdds = (Map)JavaConverters$.MODULE$.mapAsScalaConcurrentMapConverter(map).asScala();
      this.executorEnvs = (HashMap)HashMap$.MODULE$.apply(Nil$.MODULE$);
      this.sparkUser = Utils$.MODULE$.getCurrentUserName();
      this.checkpointDir = None$.MODULE$;
      this.localProperties = new InheritableThreadLocal<Properties>(this) {
         public Properties childValue(Properties parent) {
            return (Properties)SerializationUtils.clone(parent);
         }

         public Properties initialValue() {
            return new Properties();
         }
      };

      try {
         this.org$apache$spark$SparkContext$$_conf_$eq(config.clone());
         this.org$apache$spark$SparkContext$$_conf().validateSettings();
         if (!this.org$apache$spark$SparkContext$$_conf().contains("spark.master")) {
            throw new SparkException("A master URL must be set in your configuration");
         }

         if (!this.org$apache$spark$SparkContext$$_conf().contains("spark.app.name")) {
            throw new SparkException("An application name must be set in your configuration");
         }

         label549: {
            this.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Submitted application: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.appName()}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            String var42 = this.master();
            String var14 = "yarn";
            if (var42 == null) {
               if (var14 != null) {
                  break label549;
               }
            } else if (!var42.equals(var14)) {
               break label549;
            }

            var42 = this.deployMode();
            String var15 = "cluster";
            if (var42 == null) {
               if (var15 != null) {
                  break label549;
               }
            } else if (!var42.equals(var15)) {
               break label549;
            }

            if (!this.org$apache$spark$SparkContext$$_conf().contains("spark.yarn.app.id")) {
               throw new SparkException("Detected yarn cluster mode, but isn't running on a cluster. Deployment to YARN is not supported directly by SparkContext. Please use spark-submit.");
            }
         }

         if (this.org$apache$spark$SparkContext$$_conf().getBoolean("spark.logConf", false)) {
            this.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Spark configuration:\n").append((Object)this.$outer.org$apache$spark$SparkContext$$_conf().toDebugString()).toString();
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

         this.org$apache$spark$SparkContext$$_conf().set(org.apache.spark.internal.config.package$.MODULE$.DRIVER_HOST_ADDRESS(), this.org$apache$spark$SparkContext$$_conf().get(org.apache.spark.internal.config.package$.MODULE$.DRIVER_HOST_ADDRESS()));
         this.org$apache$spark$SparkContext$$_conf().setIfMissing("spark.driver.port", "0");
         this.org$apache$spark$SparkContext$$_conf().set("spark.executor.id", SparkContext$.MODULE$.DRIVER_IDENTIFIER());
         this._jars_$eq(Utils$.MODULE$.getUserJars(this.org$apache$spark$SparkContext$$_conf()));
         this._files_$eq((Seq)Option$.MODULE$.option2Iterable(this.org$apache$spark$SparkContext$$_conf().getOption("spark.files").map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String[] apply(String x$2) {
               return x$2.split(",");
            }
         }).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String[] apply(String[] x$3) {
               return (String[])Predef$.MODULE$.refArrayOps((Object[])x$3).filter(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final boolean apply(String x$4) {
                     return (new StringOps(Predef$.MODULE$.augmentString(x$4))).nonEmpty();
                  }
               });
            }
         })).toSeq().flatten(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final ArrayOps<String> apply(String[] xs) {
               return Predef$.MODULE$.<String>refArrayOps((Object[])xs);
            }
         }));
         Object var10001;
         if (this.isEventLogEnabled()) {
            String unresolvedDir = (new StringOps(Predef$.MODULE$.augmentString(this.conf().get("spark.eventLog.dir", EventLoggingListener$.MODULE$.DEFAULT_LOG_DIR())))).stripSuffix("/");
            var10001 = new Some(Utils$.MODULE$.resolveURI(unresolvedDir));
         } else {
            var10001 = None$.MODULE$;
         }

         this._eventLogDir_$eq((Option)var10001);
         boolean compress = this.org$apache$spark$SparkContext$$_conf().getBoolean("spark.eventLog.compress", false);
         this._eventLogCodec_$eq((Option)(compress && this.isEventLogEnabled() ? (new Some(CompressionCodec$.MODULE$.getCodecName(this.org$apache$spark$SparkContext$$_conf()))).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(String codecName) {
               return CompressionCodec$.MODULE$.getShortName(codecName);
            }
         }) : None$.MODULE$));
         this._listenerBus_$eq(new LiveListenerBus(this.org$apache$spark$SparkContext$$_conf()));
         this._statusStore_$eq(AppStatusStore$.MODULE$.createLiveStore(this.conf()));
         this.listenerBus().addToStatusQueue((SparkListenerInterface)this._statusStore().listener().get());
         this.org$apache$spark$SparkContext$$_env_$eq(this.createSparkEnv(this.org$apache$spark$SparkContext$$_conf(), this.isLocal(), this.listenerBus()));
         SparkEnv$.MODULE$.set(this.org$apache$spark$SparkContext$$_env());
         this.org$apache$spark$SparkContext$$_conf().getOption("spark.repl.class.outputDir").foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final SparkConf apply(String path) {
               String replUri = this.$outer.org$apache$spark$SparkContext$$_env().rpcEnv().fileServer().addDirectory("/classes", new File(path));
               return this.$outer.org$apache$spark$SparkContext$$_conf().set("spark.repl.class.uri", replUri);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this._statusTracker_$eq(new SparkStatusTracker(this, this._statusStore()));
         this.org$apache$spark$SparkContext$$_progressBar_$eq((Option)(BoxesRunTime.unboxToBoolean(this.org$apache$spark$SparkContext$$_conf().get(org.apache.spark.internal.config.package$.MODULE$.UI_SHOW_CONSOLE_PROGRESS())) && !this.log().isInfoEnabled() ? new Some(new ConsoleProgressBar(this)) : None$.MODULE$));
         this.org$apache$spark$SparkContext$$_ui_$eq((Option)(this.conf().getBoolean("spark.ui.enabled", true) ? new Some(SparkUI$.MODULE$.create(new Some(this), this._statusStore(), this.org$apache$spark$SparkContext$$_conf(), this.org$apache$spark$SparkContext$$_env().securityManager(), this.appName(), "", this.startTime(), SparkUI$.MODULE$.create$default$8())) : None$.MODULE$));
         this.org$apache$spark$SparkContext$$_ui().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(SparkUI x$5) {
               x$5.bind();
            }
         });
         this._hadoopConfiguration_$eq(SparkHadoopUtil$.MODULE$.get().newConfiguration(this.org$apache$spark$SparkContext$$_conf()));
         if (this.jars() != null) {
            this.jars().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final void apply(String path) {
                  this.$outer.addJar(path);
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

         if (this.files() != null) {
            this.files().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkContext $outer;

               public final void apply(String path) {
                  this.$outer.addFile(path);
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

         this._executorMemory_$eq(BoxesRunTime.unboxToInt(this.org$apache$spark$SparkContext$$_conf().getOption("spark.executor.memory").orElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Option<String> apply() {
               return Option$.MODULE$.<String>apply(System.getenv("SPARK_EXECUTOR_MEMORY"));
            }
         }).orElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final Option<String> apply() {
               return Option$.MODULE$.<String>apply(System.getenv("SPARK_MEM")).<String>map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply(String value) {
                     return this.$outer.org$apache$spark$SparkContext$$anonfun$$$outer().org$apache$spark$SparkContext$$warnSparkMem(value);
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
            public SparkContext org$apache$spark$SparkContext$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(String str) {
               return Utils$.MODULE$.memoryStringToMb(str);
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return 1024;
            }
         })));
         ((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("SPARK_TESTING", "spark.testing")}))).withFilter(new Serializable(this) {
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
         }).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply(Tuple2<String, String> x$6) {
               if (x$6 != null) {
                  String envKey = (String)x$6._1();
                  String propKey = (String)x$6._2();
                  Option$.MODULE$.apply(System.getenv(envKey)).orElse(new Serializable(this, propKey) {
                     public static final long serialVersionUID = 0L;
                     private final String propKey$1;

                     public final Option<String> apply() {
                        return Option$.MODULE$.<String>apply(System.getProperty(this.propKey$1));
                     }

                     public {
                        this.propKey$1 = propKey$1;
                     }
                  }).foreach(new Serializable(this, envKey) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final String envKey$1;

                     public final void apply(String value) {
                        this.$outer.org$apache$spark$SparkContext$$anonfun$$$outer().executorEnvs().update(this.envKey$1, value);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.envKey$1 = envKey$1;
                        }
                     }
                  });
                  BoxedUnit var5 = BoxedUnit.UNIT;
               } else {
                  throw new MatchError(x$6);
               }
            }

            // $FF: synthetic method
            public SparkContext org$apache$spark$SparkContext$$anonfun$$$outer() {
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
         Option$.MODULE$.apply(System.getenv("SPARK_PREPEND_CLASSES")).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply(String v) {
               this.$outer.executorEnvs().update("SPARK_PREPEND_CLASSES", v);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.executorEnvs().update("SPARK_EXECUTOR_MEMORY", (new StringBuilder()).append(this.executorMemory()).append((Object)"m").toString());
         this.executorEnvs().$plus$plus$eq(this.org$apache$spark$SparkContext$$_conf().getExecutorEnv());
         this.executorEnvs().update("SPARK_USER", this.sparkUser());
         this.org$apache$spark$SparkContext$$_heartbeatReceiver_$eq(this.env().rpcEnv().setupEndpoint(HeartbeatReceiver$.MODULE$.ENDPOINT_NAME(), new HeartbeatReceiver(this)));
         Tuple2 var19 = SparkContext$.MODULE$.org$apache$spark$SparkContext$$createTaskScheduler(this, this.master(), this.deployMode());
         if (var19 == null) {
            throw new MatchError(var19);
         }

         SchedulerBackend sched = (SchedulerBackend)var19._1();
         TaskScheduler ts = (TaskScheduler)var19._2();
         Tuple2 var22 = new Tuple2(sched, ts);
         SchedulerBackend sched = (SchedulerBackend)var22._1();
         TaskScheduler ts = (TaskScheduler)var22._2();
         this._schedulerBackend_$eq(sched);
         this._taskScheduler_$eq(ts);
         this.org$apache$spark$SparkContext$$_dagScheduler_$eq(new DAGScheduler(this));
         this.org$apache$spark$SparkContext$$_heartbeatReceiver().ask(TaskSchedulerIsSet$.MODULE$, ClassTag$.MODULE$.Boolean());
         this._taskScheduler().start();
         this.org$apache$spark$SparkContext$$_applicationId_$eq(this._taskScheduler().applicationId());
         this._applicationAttemptId_$eq(this.taskScheduler().applicationAttemptId());
         this.org$apache$spark$SparkContext$$_conf().set("spark.app.id", this.org$apache$spark$SparkContext$$_applicationId());
         if (this.org$apache$spark$SparkContext$$_conf().getBoolean("spark.ui.reverseProxy", false)) {
            System.setProperty("spark.ui.proxyBase", (new StringBuilder()).append((Object)"/proxy/").append((Object)this.org$apache$spark$SparkContext$$_applicationId()).toString());
         } else {
            BoxedUnit var44 = BoxedUnit.UNIT;
         }

         this.org$apache$spark$SparkContext$$_ui().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply(SparkUI x$8) {
               x$8.setAppId(this.$outer.org$apache$spark$SparkContext$$_applicationId());
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.org$apache$spark$SparkContext$$_env().blockManager().initialize(this.org$apache$spark$SparkContext$$_applicationId());
         this.org$apache$spark$SparkContext$$_env().metricsSystem().start();
         Predef$.MODULE$.refArrayOps(this.org$apache$spark$SparkContext$$_env().metricsSystem().getServletHandlers()).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply(ServletContextHandler handler) {
               this.$outer.ui().foreach(new Serializable(this, handler) {
                  public static final long serialVersionUID = 0L;
                  private final ServletContextHandler handler$1;

                  public final void apply(SparkUI x$9) {
                     x$9.attachHandler(this.handler$1);
                  }

                  public {
                     this.handler$1 = handler$1;
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
         if (this.isEventLogEnabled()) {
            EventLoggingListener logger = new EventLoggingListener(this.org$apache$spark$SparkContext$$_applicationId(), this._applicationAttemptId(), (URI)this._eventLogDir().get(), this.org$apache$spark$SparkContext$$_conf(), this._hadoopConfiguration());
            logger.start();
            this.listenerBus().addToEventLogQueue(logger);
            var10001 = new Some(logger);
         } else {
            var10001 = None$.MODULE$;
         }

         this.org$apache$spark$SparkContext$$_eventLogger_$eq((Option)var10001);
         boolean dynamicAllocationEnabled = Utils$.MODULE$.isDynamicAllocationEnabled(this.org$apache$spark$SparkContext$$_conf());
         if (dynamicAllocationEnabled) {
            SchedulerBackend var27 = this.schedulerBackend();
            Object var28;
            if (var27 instanceof ExecutorAllocationClient) {
               var28 = new Some(new ExecutorAllocationManager((ExecutorAllocationClient)this.schedulerBackend(), this.listenerBus(), this.org$apache$spark$SparkContext$$_conf()));
            } else {
               var28 = None$.MODULE$;
            }

            var10001 = var28;
         } else {
            var10001 = None$.MODULE$;
         }

         this.org$apache$spark$SparkContext$$_executorAllocationManager_$eq((Option)var10001);
         this.org$apache$spark$SparkContext$$_executorAllocationManager().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(ExecutorAllocationManager x$10) {
               x$10.start();
            }
         });
         this.org$apache$spark$SparkContext$$_cleaner_$eq((Option)(this.org$apache$spark$SparkContext$$_conf().getBoolean("spark.cleaner.referenceTracking", true) ? new Some(new ContextCleaner(this)) : None$.MODULE$));
         this.org$apache$spark$SparkContext$$_cleaner().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(ContextCleaner x$11) {
               x$11.start();
            }
         });
         this.setupAndStartListenerBus();
         this.postEnvironmentUpdate();
         this.postApplicationStart();
         this._taskScheduler().postStartHook();
         this.org$apache$spark$SparkContext$$_env().metricsSystem().registerSource(this.org$apache$spark$SparkContext$$_dagScheduler().metricsSource());
         this.org$apache$spark$SparkContext$$_env().metricsSystem().registerSource(new BlockManagerSource(this.org$apache$spark$SparkContext$$_env().blockManager()));
         this.org$apache$spark$SparkContext$$_executorAllocationManager().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply(ExecutorAllocationManager e) {
               this.$outer.org$apache$spark$SparkContext$$_env().metricsSystem().registerSource(e.executorAllocationManagerSource());
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Adding shutdown hook";
            }
         });
         this._shutdownHookRef_$eq(ShutdownHookManager$.MODULE$.addShutdownHook(ShutdownHookManager$.MODULE$.SPARK_CONTEXT_SHUTDOWN_PRIORITY(), new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkContext $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Invoking stop() from shutdown hook";
                  }
               });
               this.$outer.stop();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }));
      } catch (Throwable var40) {
         Option var5 = NonFatal$.MODULE$.unapply(var40);
         if (var5.isEmpty()) {
            throw var40;
         }

         Throwable e = (Throwable)var5.get();
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Error initializing SparkContext.";
            }
         }, e);

         try {
            try {
               this.stop();
               BoxedUnit var41 = BoxedUnit.UNIT;
            } catch (Throwable var38) {
               Option var11 = NonFatal$.MODULE$.unapply(var38);
               if (var11.isEmpty()) {
                  throw var38;
               }

               Throwable inner = (Throwable)var11.get();
               this.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Error stopping SparkContext after init error.";
                  }
               }, inner);
               BoxedUnit var13 = BoxedUnit.UNIT;
               BoxedUnit var10000 = BoxedUnit.UNIT;
               throw e;
            }
         } finally {
            ;
         }

         throw e;
      }

      this.nextShuffleId = new AtomicInteger(0);
      this.nextRddId = new AtomicInteger(0);
      SparkContext$.MODULE$.setActiveContext(this, this.allowMultipleContexts());
   }

   public SparkContext() {
      this(new SparkConf());
   }

   public SparkContext(String master, String appName, SparkConf conf) {
      this(SparkContext$.MODULE$.updatedConf(conf, master, appName, SparkContext$.MODULE$.updatedConf$default$4(), SparkContext$.MODULE$.updatedConf$default$5(), SparkContext$.MODULE$.updatedConf$default$6()));
   }

   public SparkContext(String master, String appName, String sparkHome, Seq<String> jars, scala.collection.Map<String, String> environment) {
      this(SparkContext$.MODULE$.updatedConf(new SparkConf(), master, appName, sparkHome, jars, environment));
   }

   public SparkContext(String master, String appName) {
      this(master, appName, (String)null, Nil$.MODULE$, (scala.collection.Map)Map$.MODULE$.apply(Nil$.MODULE$));
   }

   public SparkContext(String master, String appName, String sparkHome) {
      this(master, appName, sparkHome, Nil$.MODULE$, (scala.collection.Map)Map$.MODULE$.apply(Nil$.MODULE$));
   }

   public SparkContext(String master, String appName, String sparkHome, Seq<String> jars) {
      this(master, appName, sparkHome, jars, (scala.collection.Map)Map$.MODULE$.apply(Nil$.MODULE$));
   }
}
