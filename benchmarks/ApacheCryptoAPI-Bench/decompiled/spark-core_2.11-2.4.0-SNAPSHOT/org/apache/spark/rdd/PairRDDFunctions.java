package org.apache.spark.rdd;

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.mapred.FileOutputCommitter;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.Aggregator;
import org.apache.spark.HashPartitioner;
import org.apache.spark.InterruptibleIterator;
import org.apache.spark.Partitioner;
import org.apache.spark.Partitioner$;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.annotation.Experimental;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.io.HadoopMapRedWriteConfigUtil;
import org.apache.spark.internal.io.HadoopMapReduceWriteConfigUtil;
import org.apache.spark.internal.io.SparkHadoopWriter$;
import org.apache.spark.internal.io.SparkHadoopWriterUtils$;
import org.apache.spark.partial.BoundedDouble;
import org.apache.spark.partial.PartialResult;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.util.SerializableConfiguration;
import org.apache.spark.util.SerializableJobConf;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.collection.CompactBuffer;
import org.apache.spark.util.collection.CompactBuffer$;
import org.apache.spark.util.random.StratifiedSamplingUtils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.IterableLike;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.Map;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Null$;
import scala.runtime.ObjectRef;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;
import scala.runtime.VolatileByteRef;

@ScalaSignature(
   bytes = "\u0006\u0001%\u0005c\u0001B\u0001\u0003\u0001-\u0011\u0001\u0003U1jeJ#EIR;oGRLwN\\:\u000b\u0005\r!\u0011a\u0001:eI*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0016\u000719\u0013g\u0005\u0003\u0001\u001bMI\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015/5\tQC\u0003\u0002\u0017\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u0019+\t9Aj\\4hS:<\u0007C\u0001\b\u001b\u0013\tYrB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001e\u0001\t\u0005\t\u0015!\u0003\u001f\u0003\u0011\u0019X\r\u001c4\u0011\u0007}\u0001#%D\u0001\u0003\u0013\t\t#AA\u0002S\t\u0012\u0003BAD\u0012&a%\u0011Ae\u0004\u0002\u0007)V\u0004H.\u001a\u001a\u0011\u0005\u0019:C\u0002\u0001\u0003\u0006Q\u0001\u0011\r!\u000b\u0002\u0002\u0017F\u0011!&\f\t\u0003\u001d-J!\u0001L\b\u0003\u000f9{G\u000f[5oOB\u0011aBL\u0005\u0003_=\u00111!\u00118z!\t1\u0013\u0007B\u00033\u0001\t\u0007\u0011FA\u0001W\u0011!!\u0004A!A!\u0002\u0017)\u0014AA6u!\r1\u0014(J\u0007\u0002o)\u0011\u0001hD\u0001\be\u00164G.Z2u\u0013\tQtG\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011!a\u0004A!A!\u0002\u0017i\u0014A\u0001<u!\r1\u0014\b\r\u0005\t\u007f\u0001\u0011\t\u0011)A\u0006\u0001\u0006\u0019qN\u001d3\u0011\u0007\u0005KUE\u0004\u0002C\u000f:\u00111IR\u0007\u0002\t*\u0011QIC\u0001\u0007yI|w\u000e\u001e \n\u0003AI!\u0001S\b\u0002\u000fA\f7m[1hK&\u0011!j\u0013\u0002\t\u001fJ$WM]5oO*\u0011\u0001j\u0004\u0005\u0006\u001b\u0002!\tAT\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005=#F\u0003\u0002)R%N\u0003Ba\b\u0001&a!)A\u0007\u0014a\u0002k!)A\b\u0014a\u0002{!9q\b\u0014I\u0001\u0002\b\u0001\u0005\"B\u000fM\u0001\u0004q\u0002\"\u0002,\u0001\t\u00039\u0016\u0001G2p[\nLg.\u001a\"z\u0017\u0016Lx+\u001b;i\u00072\f7o\u001d+bOV\u0011\u0001,\u0018\u000b\b3\n<Gn\\;{)\tQv\fE\u0002 Am\u0003BAD\u0012&9B\u0011a%\u0018\u0003\u0006=V\u0013\r!\u000b\u0002\u0002\u0007\")\u0001-\u0016a\u0002C\u0006\u00111\r\u001e\t\u0004meb\u0006\"B2V\u0001\u0004!\u0017AD2sK\u0006$XmQ8nE&tWM\u001d\t\u0005\u001d\u0015\u0004D,\u0003\u0002g\u001f\tIa)\u001e8di&|g.\r\u0005\u0006QV\u0003\r![\u0001\u000b[\u0016\u0014x-\u001a,bYV,\u0007#\u0002\bk9Bb\u0016BA6\u0010\u0005%1UO\\2uS>t'\u0007C\u0003n+\u0002\u0007a.\u0001\bnKJ<WmQ8nE&tWM]:\u0011\u000b9QG\f\u0018/\t\u000bA,\u0006\u0019A9\u0002\u0017A\f'\u000f^5uS>tWM\u001d\t\u0003eNl\u0011\u0001B\u0005\u0003i\u0012\u00111\u0002U1si&$\u0018n\u001c8fe\"9a/\u0016I\u0001\u0002\u00049\u0018AD7baNKG-Z\"p[\nLg.\u001a\t\u0003\u001daL!!_\b\u0003\u000f\t{w\u000e\\3b]\"910\u0016I\u0001\u0002\u0004a\u0018AC:fe&\fG.\u001b>feB\u0011Qp`\u0007\u0002}*\u00111\u0010B\u0005\u0004\u0003\u0003q(AC*fe&\fG.\u001b>fe\"\u001aQ+!\u0002\u0011\t\u0005\u001d\u0011QB\u0007\u0003\u0003\u0013Q1!a\u0003\u0005\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003\u001f\tIA\u0001\u0007FqB,'/[7f]R\fG\u000eC\u0004\u0002\u0014\u0001!\t!!\u0006\u0002\u0019\r|WNY5oK\nK8*Z=\u0016\t\u0005]\u0011q\u0004\u000b\u000f\u00033\t\t#!\n\u0002*\u00055\u0012qFA\u0019!\u0011y\u0002%a\u0007\u0011\u000b9\u0019S%!\b\u0011\u0007\u0019\ny\u0002\u0002\u0004_\u0003#\u0011\r!\u000b\u0005\bG\u0006E\u0001\u0019AA\u0012!\u0015qQ\rMA\u000f\u0011\u001dA\u0017\u0011\u0003a\u0001\u0003O\u0001rA\u00046\u0002\u001eA\ni\u0002C\u0004n\u0003#\u0001\r!a\u000b\u0011\u00119Q\u0017QDA\u000f\u0003;Aa\u0001]A\t\u0001\u0004\t\b\u0002\u0003<\u0002\u0012A\u0005\t\u0019A<\t\u0011m\f\t\u0002%AA\u0002qDq!a\u0005\u0001\t\u0003\t)$\u0006\u0003\u00028\u0005}BCCA\u001d\u0003\u0003\n)%!\u0013\u0002NA!q\u0004IA\u001e!\u0015q1%JA\u001f!\r1\u0013q\b\u0003\u0007=\u0006M\"\u0019A\u0015\t\u000f\r\f\u0019\u00041\u0001\u0002DA)a\"\u001a\u0019\u0002>!9\u0001.a\rA\u0002\u0005\u001d\u0003c\u0002\bk\u0003{\u0001\u0014Q\b\u0005\b[\u0006M\u0002\u0019AA&!!q!.!\u0010\u0002>\u0005u\u0002\u0002CA(\u0003g\u0001\r!!\u0015\u0002\u001b9,X\u000eU1si&$\u0018n\u001c8t!\rq\u00111K\u0005\u0004\u0003+z!aA%oi\"1a\u000b\u0001C\u0001\u00033*B!a\u0017\u0002fQQ\u0011QLA6\u0003_\n\u0019(a\u001e\u0015\t\u0005}\u0013q\r\t\u0005?\u0001\n\t\u0007E\u0003\u000fG\u0015\n\u0019\u0007E\u0002'\u0003K\"aAXA,\u0005\u0004I\u0003b\u00021\u0002X\u0001\u000f\u0011\u0011\u000e\t\u0005me\n\u0019\u0007C\u0004d\u0003/\u0002\r!!\u001c\u0011\u000b9)\u0007'a\u0019\t\u000f!\f9\u00061\u0001\u0002rA9aB[A2a\u0005\r\u0004bB7\u0002X\u0001\u0007\u0011Q\u000f\t\t\u001d)\f\u0019'a\u0019\u0002d!A\u0011qJA,\u0001\u0004\t\t\u0006\u000b\u0003\u0002X\u0005\u0015\u0001bBA?\u0001\u0011\u0005\u0011qP\u0001\u000fC\u001e<'/Z4bi\u0016\u0014\u0015pS3z+\u0011\t\t)!$\u0015\r\u0005\r\u00151UAT)\u0019\t))a&\u0002\u001eR!\u0011qQAI!\u0011y\u0002%!#\u0011\u000b9\u0019S%a#\u0011\u0007\u0019\ni\tB\u0004\u0002\u0010\u0006m$\u0019A\u0015\u0003\u0003UC!\"a%\u0002|\u0005\u0005\t9AAK\u0003))g/\u001b3f]\u000e,G%\r\t\u0005me\nY\t\u0003\u0005\u0002\u001a\u0006m\u0004\u0019AAN\u0003\u0015\u0019X-](q!\u001dq!.a#1\u0003\u0017C\u0001\"a(\u0002|\u0001\u0007\u0011\u0011U\u0001\u0007G>l'm\u00149\u0011\u00119Q\u00171RAF\u0003\u0017C\u0001\"!*\u0002|\u0001\u0007\u00111R\u0001\nu\u0016\u0014xNV1mk\u0016Da\u0001]A>\u0001\u0004\t\bbBA?\u0001\u0011\u0005\u00111V\u000b\u0005\u0003[\u000bI\f\u0006\u0004\u00020\u0006%\u00171\u001a\u000b\u0007\u0003c\u000b\t-!2\u0015\t\u0005M\u00161\u0018\t\u0005?\u0001\n)\fE\u0003\u000fG\u0015\n9\fE\u0002'\u0003s#q!a$\u0002*\n\u0007\u0011\u0006\u0003\u0006\u0002>\u0006%\u0016\u0011!a\u0002\u0003\u007f\u000b!\"\u001a<jI\u0016t7-\u001a\u00133!\u00111\u0014(a.\t\u0011\u0005e\u0015\u0011\u0016a\u0001\u0003\u0007\u0004rA\u00046\u00028B\n9\f\u0003\u0005\u0002 \u0006%\u0006\u0019AAd!!q!.a.\u00028\u0006]\u0006\u0002CAS\u0003S\u0003\r!a.\t\u0011\u0005=\u0013\u0011\u0016a\u0001\u0003#Bq!! \u0001\t\u0003\ty-\u0006\u0003\u0002R\u0006uG\u0003BAj\u0003[$b!!6\u0002f\u0006%H\u0003BAl\u0003?\u0004Ba\b\u0011\u0002ZB)abI\u0013\u0002\\B\u0019a%!8\u0005\u000f\u0005=\u0015Q\u001ab\u0001S!Q\u0011\u0011]Ag\u0003\u0003\u0005\u001d!a9\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$3\u0007\u0005\u00037s\u0005m\u0007\u0002CAM\u0003\u001b\u0004\r!a:\u0011\u000f9Q\u00171\u001c\u0019\u0002\\\"A\u0011qTAg\u0001\u0004\tY\u000f\u0005\u0005\u000fU\u0006m\u00171\\An\u0011!\t)+!4A\u0002\u0005m\u0007bBAy\u0001\u0011\u0005\u00111_\u0001\nM>dGMQ=LKf$b!!>\u0002~\u0006}Hc\u0001\u0010\u0002x\"A\u0011\u0011`Ax\u0001\u0004\tY0\u0001\u0003gk:\u001c\u0007#\u0002\bkaA\u0002\u0004bBAS\u0003_\u0004\r\u0001\r\u0005\u0007a\u0006=\b\u0019A9\t\u000f\u0005E\b\u0001\"\u0001\u0003\u0004Q1!Q\u0001B\u0005\u0005\u0017!2A\bB\u0004\u0011!\tIP!\u0001A\u0002\u0005m\bbBAS\u0005\u0003\u0001\r\u0001\r\u0005\t\u0003\u001f\u0012\t\u00011\u0001\u0002R!9\u0011\u0011\u001f\u0001\u0005\u0002\t=A\u0003\u0002B\t\u0005+!2A\bB\n\u0011!\tIP!\u0004A\u0002\u0005m\bbBAS\u0005\u001b\u0001\r\u0001\r\u0005\b\u00053\u0001A\u0011\u0001B\u000e\u0003-\u0019\u0018-\u001c9mK\nK8*Z=\u0015\u000fy\u0011iB!\t\u00038!9!q\u0004B\f\u0001\u00049\u0018aD<ji\"\u0014V\r\u001d7bG\u0016lWM\u001c;\t\u0011\t\r\"q\u0003a\u0001\u0005K\t\u0011B\u001a:bGRLwN\\:\u0011\u000f\t\u001d\"QF\u0013\u000325\u0011!\u0011\u0006\u0006\u0004\u0005Wy\u0011AC2pY2,7\r^5p]&!!q\u0006B\u0015\u0005\ri\u0015\r\u001d\t\u0004\u001d\tM\u0012b\u0001B\u001b\u001f\t1Ai\\;cY\u0016D!B!\u000f\u0003\u0018A\u0005\t\u0019\u0001B\u001e\u0003\u0011\u0019X-\u001a3\u0011\u00079\u0011i$C\u0002\u0003@=\u0011A\u0001T8oO\"9!1\t\u0001\u0005\u0002\t\u0015\u0013\u0001E:b[BdWMQ=LKf,\u00050Y2u)\u001dq\"q\tB%\u0005\u0017BqAa\b\u0003B\u0001\u0007q\u000f\u0003\u0005\u0003$\t\u0005\u0003\u0019\u0001B\u0013\u0011)\u0011ID!\u0011\u0011\u0002\u0003\u0007!1\b\u0005\b\u0005\u001f\u0002A\u0011\u0001B)\u0003-\u0011X\rZ;dK\nK8*Z=\u0015\u000by\u0011\u0019F!\u0016\t\rA\u0014i\u00051\u0001r\u0011!\tIP!\u0014A\u0002\u0005m\bb\u0002B(\u0001\u0011\u0005!\u0011\f\u000b\u0006=\tm#Q\f\u0005\t\u0003s\u00149\u00061\u0001\u0002|\"A\u0011q\nB,\u0001\u0004\t\t\u0006C\u0004\u0003P\u0001!\tA!\u0019\u0015\u0007y\u0011\u0019\u0007\u0003\u0005\u0002z\n}\u0003\u0019AA~\u0011\u001d\u00119\u0007\u0001C\u0001\u0005S\n!C]3ek\u000e,')_&fs2{7-\u00197msR!!1\u000eB7!\u0019\u00119C!\f&a!A\u0011\u0011 B3\u0001\u0004\tY\u0010C\u0004\u0003r\u0001!\tAa\u001d\u0002\u0015\r|WO\u001c;Cs.+\u0017\u0010\u0006\u0002\u0003vA9!q\u0005B\u0017K\tm\u0002b\u0002B=\u0001\u0011\u0005!1P\u0001\u0011G>,h\u000e\u001e\"z\u0017\u0016L\u0018\t\u001d9s_b$bA! \u0003\u0012\nU\u0005C\u0002B@\u0005\u000b\u0013I)\u0004\u0002\u0003\u0002*\u0019!1\u0011\u0003\u0002\u000fA\f'\u000f^5bY&!!q\u0011BA\u00055\u0001\u0016M\u001d;jC2\u0014Vm];miB9!q\u0005B\u0017K\t-\u0005\u0003\u0002B@\u0005\u001bKAAa$\u0003\u0002\ni!i\\;oI\u0016$Gi\\;cY\u0016D\u0001Ba%\u0003x\u0001\u0007!1H\u0001\bi&lWm\\;u\u0011)\u00119Ja\u001e\u0011\u0002\u0003\u0007!\u0011G\u0001\u000bG>tg-\u001b3f]\u000e,\u0007b\u0002BN\u0001\u0011\u0005!QT\u0001\u0019G>,h\u000e^!qaJ|\u0007\u0010R5ti&t7\r\u001e\"z\u0017\u0016LH\u0003\u0003BP\u0005G\u00139Ka+\u0011\t}\u0001#\u0011\u0015\t\u0006\u001d\r*#1\b\u0005\t\u0005K\u0013I\n1\u0001\u0002R\u0005\t\u0001\u000f\u0003\u0005\u0003*\ne\u0005\u0019AA)\u0003\t\u0019\b\u000f\u0003\u0004q\u00053\u0003\r!\u001d\u0005\b\u00057\u0003A\u0011\u0001BX)\u0019\u0011yJ!-\u00036\"A!1\u0017BW\u0001\u0004\u0011\t$\u0001\u0006sK2\fG/\u001b<f'\u0012Ca\u0001\u001dBW\u0001\u0004\t\bb\u0002BN\u0001\u0011\u0005!\u0011\u0018\u000b\u0007\u0005?\u0013YL!0\t\u0011\tM&q\u0017a\u0001\u0005cA\u0001\"a\u0014\u00038\u0002\u0007\u0011\u0011\u000b\u0005\b\u00057\u0003A\u0011\u0001Ba)\u0011\u0011yJa1\t\u0015\tM&q\u0018I\u0001\u0002\u0004\u0011\t\u0004C\u0004\u0003H\u0002!\tA!3\u0002\u0015\u001d\u0014x.\u001e9Cs.+\u0017\u0010\u0006\u0003\u0003L\nU\u0007\u0003B\u0010!\u0005\u001b\u0004RAD\u0012&\u0005\u001f\u0004B!\u0011Bia%\u0019!1[&\u0003\u0011%#XM]1cY\u0016Da\u0001\u001dBc\u0001\u0004\t\bb\u0002Bd\u0001\u0011\u0005!\u0011\u001c\u000b\u0005\u0005\u0017\u0014Y\u000e\u0003\u0005\u0002P\t]\u0007\u0019AA)\u0011\u001d\u0011y\u000e\u0001C\u0001\u0005C\f1\u0002]1si&$\u0018n\u001c8CsR\u0019aDa9\t\rA\u0014i\u000e1\u0001r\u0011\u001d\u00119\u000f\u0001C\u0001\u0005S\fAA[8j]V!!1\u001eB{)\u0019\u0011iO!?\u0004\u0002A!q\u0004\tBx!\u0015q1%\nBy!\u0015q1\u0005\rBz!\r1#Q\u001f\u0003\b\u0005o\u0014)O1\u0001*\u0005\u00059\u0006\u0002\u0003B~\u0005K\u0004\rA!@\u0002\u000b=$\b.\u001a:\u0011\t}\u0001#q \t\u0006\u001d\r*#1\u001f\u0005\u0007a\n\u0015\b\u0019A9\t\u000f\r\u0015\u0001\u0001\"\u0001\u0004\b\u0005iA.\u001a4u\u001fV$XM\u001d&pS:,Ba!\u0003\u0004\u001aQ111BB\u000e\u0007C\u0001Ba\b\u0011\u0004\u000eA)abI\u0013\u0004\u0010A)ab\t\u0019\u0004\u0012A)aba\u0005\u0004\u0018%\u00191QC\b\u0003\r=\u0003H/[8o!\r13\u0011\u0004\u0003\b\u0005o\u001c\u0019A1\u0001*\u0011!\u0011Ypa\u0001A\u0002\ru\u0001\u0003B\u0010!\u0007?\u0001RAD\u0012&\u0007/Aa\u0001]B\u0002\u0001\u0004\t\bbBB\u0013\u0001\u0011\u00051qE\u0001\u000fe&<\u0007\u000e^(vi\u0016\u0014(j\\5o+\u0011\u0019Ic!\u000e\u0015\r\r-2qGB\u001f!\u0011y\u0002e!\f\u0011\u000b9\u0019Sea\f\u0011\r9\u00193\u0011GB\u001a!\u0011q11\u0003\u0019\u0011\u0007\u0019\u001a)\u0004B\u0004\u0003x\u000e\r\"\u0019A\u0015\t\u0011\tm81\u0005a\u0001\u0007s\u0001Ba\b\u0011\u0004<A)abI\u0013\u00044!1\u0001oa\tA\u0002EDqa!\u0011\u0001\t\u0003\u0019\u0019%A\u0007gk2dw*\u001e;fe*{\u0017N\\\u000b\u0005\u0007\u000b\u001a\t\u0006\u0006\u0004\u0004H\rM3\u0011\f\t\u0005?\u0001\u001aI\u0005E\u0003\u000fG\u0015\u001aY\u0005\u0005\u0004\u000fG\rE2Q\n\t\u0006\u001d\rM1q\n\t\u0004M\rECa\u0002B|\u0007\u007f\u0011\r!\u000b\u0005\t\u0005w\u001cy\u00041\u0001\u0004VA!q\u0004IB,!\u0015q1%JB(\u0011\u0019\u00018q\ba\u0001c\"9\u00111\u0003\u0001\u0005\u0002\ruS\u0003BB0\u0007O\"\u0002b!\u0019\u0004j\r54\u0011\u000f\t\u0005?\u0001\u001a\u0019\u0007E\u0003\u000fG\u0015\u001a)\u0007E\u0002'\u0007O\"aAXB.\u0005\u0004I\u0003bB2\u0004\\\u0001\u000711\u000e\t\u0006\u001d\u0015\u00044Q\r\u0005\bQ\u000em\u0003\u0019AB8!\u001dq!n!\u001a1\u0007KBq!\\B.\u0001\u0004\u0019\u0019\b\u0005\u0005\u000fU\u000e\u00154QMB3\u0011\u00191\u0006\u0001\"\u0001\u0004xU!1\u0011PBB)!\u0019Yh!#\u0004\u000e\u000eEE\u0003BB?\u0007\u000b\u0003Ba\b\u0011\u0004\u0000A)abI\u0013\u0004\u0002B\u0019aea!\u0005\ry\u001b)H1\u0001*\u0011\u001d\u00017Q\u000fa\u0002\u0007\u000f\u0003BAN\u001d\u0004\u0002\"91m!\u001eA\u0002\r-\u0005#\u0002\bfa\r\u0005\u0005b\u00025\u0004v\u0001\u00071q\u0012\t\b\u001d)\u001c\t\tMBA\u0011\u001di7Q\u000fa\u0001\u0007'\u0003\u0002B\u00046\u0004\u0002\u000e\u00055\u0011\u0011\u0015\u0005\u0007k\n)\u0001C\u0004\u0003H\u0002!\ta!'\u0015\u0005\t-\u0007b\u0002Bt\u0001\u0011\u00051QT\u000b\u0005\u0007?\u001bI\u000b\u0006\u0003\u0004\"\u000e-\u0006\u0003B\u0010!\u0007G\u0003RAD\u0012&\u0007K\u0003RAD\u00121\u0007O\u00032AJBU\t\u001d\u00119pa'C\u0002%B\u0001Ba?\u0004\u001c\u0002\u00071Q\u0016\t\u0005?\u0001\u001ay\u000bE\u0003\u000fG\u0015\u001a9\u000bC\u0004\u0003h\u0002!\taa-\u0016\t\rU6q\u0018\u000b\u0007\u0007o\u001b\tma2\u0011\t}\u00013\u0011\u0018\t\u0006\u001d\r*31\u0018\t\u0006\u001d\r\u00024Q\u0018\t\u0004M\r}Fa\u0002B|\u0007c\u0013\r!\u000b\u0005\t\u0005w\u001c\t\f1\u0001\u0004DB!q\u0004IBc!\u0015q1%JB_\u0011!\tye!-A\u0002\u0005E\u0003bBB\u0003\u0001\u0011\u000511Z\u000b\u0005\u0007\u001b\u001cI\u000e\u0006\u0003\u0004P\u000em\u0007\u0003B\u0010!\u0007#\u0004RAD\u0012&\u0007'\u0004RAD\u00121\u0007+\u0004RADB\n\u0007/\u00042AJBm\t\u001d\u00119p!3C\u0002%B\u0001Ba?\u0004J\u0002\u00071Q\u001c\t\u0005?\u0001\u001ay\u000eE\u0003\u000fG\u0015\u001a9\u000eC\u0004\u0004\u0006\u0001!\taa9\u0016\t\r\u00158\u0011\u001f\u000b\u0007\u0007O\u001c\u0019p!?\u0011\t}\u00013\u0011\u001e\t\u0006\u001d\r*31\u001e\t\u0006\u001d\r\u00024Q\u001e\t\u0006\u001d\rM1q\u001e\t\u0004M\rEHa\u0002B|\u0007C\u0014\r!\u000b\u0005\t\u0005w\u001c\t\u000f1\u0001\u0004vB!q\u0004IB|!\u0015q1%JBx\u0011!\tye!9A\u0002\u0005E\u0003bBB\u0013\u0001\u0011\u00051Q`\u000b\u0005\u0007\u007f$I\u0001\u0006\u0003\u0005\u0002\u0011-\u0001\u0003B\u0010!\t\u0007\u0001RAD\u0012&\t\u000b\u0001bAD\u0012\u00042\u0011\u001d\u0001c\u0001\u0014\u0005\n\u00119!q_B~\u0005\u0004I\u0003\u0002\u0003B~\u0007w\u0004\r\u0001\"\u0004\u0011\t}\u0001Cq\u0002\t\u0006\u001d\r*Cq\u0001\u0005\b\u0007K\u0001A\u0011\u0001C\n+\u0011!)\u0002b\b\u0015\r\u0011]A\u0011\u0005C\u0014!\u0011y\u0002\u0005\"\u0007\u0011\u000b9\u0019S\u0005b\u0007\u0011\r9\u00193\u0011\u0007C\u000f!\r1Cq\u0004\u0003\b\u0005o$\tB1\u0001*\u0011!\u0011Y\u0010\"\u0005A\u0002\u0011\r\u0002\u0003B\u0010!\tK\u0001RAD\u0012&\t;A\u0001\"a\u0014\u0005\u0012\u0001\u0007\u0011\u0011\u000b\u0005\b\u0007\u0003\u0002A\u0011\u0001C\u0016+\u0011!i\u0003\"\u000f\u0015\t\u0011=B1\b\t\u0005?\u0001\"\t\u0004E\u0003\u000fG\u0015\"\u0019\u0004\u0005\u0004\u000fG\rEBQ\u0007\t\u0006\u001d\rMAq\u0007\t\u0004M\u0011eBa\u0002B|\tS\u0011\r!\u000b\u0005\t\u0005w$I\u00031\u0001\u0005>A!q\u0004\tC !\u0015q1%\nC\u001c\u0011\u001d\u0019\t\u0005\u0001C\u0001\t\u0007*B\u0001\"\u0012\u0005RQ1Aq\tC*\t3\u0002Ba\b\u0011\u0005JA)abI\u0013\u0005LA1abIB\u0019\t\u001b\u0002RADB\n\t\u001f\u00022A\nC)\t\u001d\u00119\u0010\"\u0011C\u0002%B\u0001Ba?\u0005B\u0001\u0007AQ\u000b\t\u0005?\u0001\"9\u0006E\u0003\u000fG\u0015\"y\u0005\u0003\u0005\u0002P\u0011\u0005\u0003\u0019AA)\u0011\u001d!i\u0006\u0001C\u0001\t?\nAbY8mY\u0016\u001cG/Q:NCB$\"Aa\u001b\t\u000f\u0011\r\u0004\u0001\"\u0001\u0005f\u0005IQ.\u00199WC2,Xm]\u000b\u0005\tO\"y\u0007\u0006\u0003\u0005j\u0011E\u0004\u0003B\u0010!\tW\u0002RAD\u0012&\t[\u00022A\nC8\t\u001d\ty\t\"\u0019C\u0002%B\u0001\u0002b\u001d\u0005b\u0001\u0007AQO\u0001\u0002MB)a\"\u001a\u0019\u0005n!9A\u0011\u0010\u0001\u0005\u0002\u0011m\u0014!\u00044mCRl\u0015\r\u001d,bYV,7/\u0006\u0003\u0005~\u0011\u0015E\u0003\u0002C@\t\u000f\u0003Ba\b\u0011\u0005\u0002B)abI\u0013\u0005\u0004B\u0019a\u0005\"\"\u0005\u000f\u0005=Eq\u000fb\u0001S!AA1\u000fC<\u0001\u0004!I\tE\u0003\u000fKB\"Y\tE\u0003B\t\u001b#\u0019)C\u0002\u0005\u0010.\u0013q\u0002\u0016:bm\u0016\u00148/\u00192mK>s7-\u001a\u0005\b\t'\u0003A\u0011\u0001CK\u0003\u001d\u0019wn\u001a:pkB,\u0002\u0002b&\u0005(\u0012=Fq\u0017\u000b\u000b\t3#Y\fb1\u0005L\u0012M\u0007\u0003B\u0010!\t7\u0003RAD\u0012&\t;\u00032B\u0004CP\u0005\u001f$\u0019\u000bb+\u00054&\u0019A\u0011U\b\u0003\rQ+\b\u000f\\35!\u0015\t%\u0011\u001bCS!\r1Cq\u0015\u0003\b\tS#\tJ1\u0001*\u0005\t9\u0016\u0007E\u0003B\u0005#$i\u000bE\u0002'\t_#q\u0001\"-\u0005\u0012\n\u0007\u0011F\u0001\u0002XeA)\u0011I!5\u00056B\u0019a\u0005b.\u0005\u000f\u0011eF\u0011\u0013b\u0001S\t\u0011qk\r\u0005\t\t{#\t\n1\u0001\u0005@\u00061q\u000e\u001e5feF\u0002Ba\b\u0011\u0005BB)abI\u0013\u0005&\"AAQ\u0019CI\u0001\u0004!9-\u0001\u0004pi\",'O\r\t\u0005?\u0001\"I\rE\u0003\u000fG\u0015\"i\u000b\u0003\u0005\u0005N\u0012E\u0005\u0019\u0001Ch\u0003\u0019yG\u000f[3sgA!q\u0004\tCi!\u0015q1%\nC[\u0011\u0019\u0001H\u0011\u0013a\u0001c\"9A1\u0013\u0001\u0005\u0002\u0011]W\u0003\u0002Cm\tK$b\u0001b7\u0005h\u00125\b\u0003B\u0010!\t;\u0004RAD\u0012&\t?\u0004bAD\u0012\u0003P\u0012\u0005\b#B!\u0003R\u0012\r\bc\u0001\u0014\u0005f\u00129!q\u001fCk\u0005\u0004I\u0003\u0002\u0003B~\t+\u0004\r\u0001\";\u0011\t}\u0001C1\u001e\t\u0006\u001d\r*C1\u001d\u0005\u0007a\u0012U\u0007\u0019A9\t\u000f\u0011M\u0005\u0001\"\u0001\u0005rV1A1_C\u0002\u000b\u0013!\u0002\u0002\">\u0006\f\u0015EQq\u0003\t\u0005?\u0001\"9\u0010E\u0003\u000fG\u0015\"I\u0010E\u0005\u000f\tw\u0014y\rb@\u0006\u0006%\u0019AQ`\b\u0003\rQ+\b\u000f\\34!\u0015\t%\u0011[C\u0001!\r1S1\u0001\u0003\b\tS#yO1\u0001*!\u0015\t%\u0011[C\u0004!\r1S\u0011\u0002\u0003\b\tc#yO1\u0001*\u0011!!i\fb<A\u0002\u00155\u0001\u0003B\u0010!\u000b\u001f\u0001RAD\u0012&\u000b\u0003A\u0001\u0002\"2\u0005p\u0002\u0007Q1\u0003\t\u0005?\u0001*)\u0002E\u0003\u000fG\u0015*9\u0001\u0003\u0004q\t_\u0004\r!\u001d\u0005\b\t'\u0003A\u0011AC\u000e+!)i\"\"\u000b\u00060\u0015UB\u0003CC\u0010\u000bo)i$b\u0011\u0011\t}\u0001S\u0011\u0005\t\u0006\u001d\r*S1\u0005\t\f\u001d\u0011}%qZC\u0013\u000bW)\t\u0004E\u0003B\u0005#,9\u0003E\u0002'\u000bS!q\u0001\"+\u0006\u001a\t\u0007\u0011\u0006E\u0003B\u0005#,i\u0003E\u0002'\u000b_!q\u0001\"-\u0006\u001a\t\u0007\u0011\u0006E\u0003B\u0005#,\u0019\u0004E\u0002'\u000bk!q\u0001\"/\u0006\u001a\t\u0007\u0011\u0006\u0003\u0005\u0005>\u0016e\u0001\u0019AC\u001d!\u0011y\u0002%b\u000f\u0011\u000b9\u0019S%b\n\t\u0011\u0011\u0015W\u0011\u0004a\u0001\u000b\u007f\u0001Ba\b\u0011\u0006BA)abI\u0013\u0006.!AAQZC\r\u0001\u0004))\u0005\u0005\u0003 A\u0015\u001d\u0003#\u0002\b$K\u0015M\u0002b\u0002CJ\u0001\u0011\u0005Q1J\u000b\u0005\u000b\u001b*I\u0006\u0006\u0003\u0006P\u0015m\u0003\u0003B\u0010!\u000b#\u0002RAD\u0012&\u000b'\u0002bAD\u0012\u0003P\u0016U\u0003#B!\u0003R\u0016]\u0003c\u0001\u0014\u0006Z\u00119!q_C%\u0005\u0004I\u0003\u0002\u0003B~\u000b\u0013\u0002\r!\"\u0018\u0011\t}\u0001Sq\f\t\u0006\u001d\r*Sq\u000b\u0005\b\t'\u0003A\u0011AC2+\u0019))'\"\u001d\u0006xQ1QqMC=\u000b\u007f\u0002Ba\b\u0011\u0006jA)abI\u0013\u0006lAIa\u0002b?\u0003P\u00165T1\u000f\t\u0006\u0003\nEWq\u000e\t\u0004M\u0015EDa\u0002CU\u000bC\u0012\r!\u000b\t\u0006\u0003\nEWQ\u000f\t\u0004M\u0015]Da\u0002CY\u000bC\u0012\r!\u000b\u0005\t\t{+\t\u00071\u0001\u0006|A!q\u0004IC?!\u0015q1%JC8\u0011!!)-\"\u0019A\u0002\u0015\u0005\u0005\u0003B\u0010!\u000b\u0007\u0003RAD\u0012&\u000bkBq\u0001b%\u0001\t\u0003)9)\u0006\u0003\u0006\n\u0016UECBCF\u000b/+i\n\u0005\u0003 A\u00155\u0005#\u0002\b$K\u0015=\u0005C\u0002\b$\u0005\u001f,\t\nE\u0003B\u0005#,\u0019\nE\u0002'\u000b+#qAa>\u0006\u0006\n\u0007\u0011\u0006\u0003\u0005\u0003|\u0016\u0015\u0005\u0019ACM!\u0011y\u0002%b'\u0011\u000b9\u0019S%b%\t\u0011\u0005=SQ\u0011a\u0001\u0003#Bq\u0001b%\u0001\t\u0003)\t+\u0006\u0004\u0006$\u0016=VQ\u0017\u000b\t\u000bK+9,\"0\u0006DB!q\u0004ICT!\u0015q1%JCU!%qA1 Bh\u000bW+\t\fE\u0003B\u0005#,i\u000bE\u0002'\u000b_#q\u0001\"+\u0006 \n\u0007\u0011\u0006E\u0003B\u0005#,\u0019\fE\u0002'\u000bk#q\u0001\"-\u0006 \n\u0007\u0011\u0006\u0003\u0005\u0005>\u0016}\u0005\u0019AC]!\u0011y\u0002%b/\u0011\u000b9\u0019S%\",\t\u0011\u0011\u0015Wq\u0014a\u0001\u000b\u007f\u0003Ba\b\u0011\u0006BB)abI\u0013\u00064\"A\u0011qJCP\u0001\u0004\t\t\u0006C\u0004\u0005\u0014\u0002!\t!b2\u0016\u0011\u0015%WQ[Cn\u000bC$\"\"b3\u0006d\u0016%Xq^C{!\u0011y\u0002%\"4\u0011\u000b9\u0019S%b4\u0011\u00179!yJa4\u0006R\u0016]WQ\u001c\t\u0006\u0003\nEW1\u001b\t\u0004M\u0015UGa\u0002CU\u000b\u000b\u0014\r!\u000b\t\u0006\u0003\nEW\u0011\u001c\t\u0004M\u0015mGa\u0002CY\u000b\u000b\u0014\r!\u000b\t\u0006\u0003\nEWq\u001c\t\u0004M\u0015\u0005Ha\u0002C]\u000b\u000b\u0014\r!\u000b\u0005\t\t{+)\r1\u0001\u0006fB!q\u0004ICt!\u0015q1%JCj\u0011!!)-\"2A\u0002\u0015-\b\u0003B\u0010!\u000b[\u0004RAD\u0012&\u000b3D\u0001\u0002\"4\u0006F\u0002\u0007Q\u0011\u001f\t\u0005?\u0001*\u0019\u0010E\u0003\u000fG\u0015*y\u000e\u0003\u0005\u0002P\u0015\u0015\u0007\u0019AA)\u0011\u001d)I\u0010\u0001C\u0001\u000bw\f\u0011b\u001a:pkB<\u0016\u000e\u001e5\u0016\t\u0015uh\u0011\u0002\u000b\u0005\u000b\u007f4Y\u0001\u0005\u0003 A\u0019\u0005\u0001#\u0002\b$K\u0019\r\u0001C\u0002\b$\u0005\u001f4)\u0001E\u0003B\u0005#49\u0001E\u0002'\r\u0013!qAa>\u0006x\n\u0007\u0011\u0006\u0003\u0005\u0003|\u0016]\b\u0019\u0001D\u0007!\u0011y\u0002Eb\u0004\u0011\u000b9\u0019SEb\u0002\t\u000f\u0015e\b\u0001\"\u0001\u0007\u0014U1aQ\u0003D\u0011\rO!bAb\u0006\u0007*\u0019=\u0002\u0003B\u0010!\r3\u0001RAD\u0012&\r7\u0001\u0012B\u0004C~\u0005\u001f4iBb\t\u0011\u000b\u0005\u0013\tNb\b\u0011\u0007\u00192\t\u0003B\u0004\u0005*\u001aE!\u0019A\u0015\u0011\u000b\u0005\u0013\tN\"\n\u0011\u0007\u001929\u0003B\u0004\u00052\u001aE!\u0019A\u0015\t\u0011\u0011uf\u0011\u0003a\u0001\rW\u0001Ba\b\u0011\u0007.A)abI\u0013\u0007 !AAQ\u0019D\t\u0001\u00041\t\u0004\u0005\u0003 A\u0019M\u0002#\u0002\b$K\u0019\u0015\u0002bBC}\u0001\u0011\u0005aqG\u000b\t\rs1)Eb\u0013\u0007RQAa1\bD*\r32y\u0006\u0005\u0003 A\u0019u\u0002#\u0002\b$K\u0019}\u0002c\u0003\b\u0005 \n=g\u0011\tD$\r\u001b\u0002R!\u0011Bi\r\u0007\u00022A\nD#\t\u001d!IK\"\u000eC\u0002%\u0002R!\u0011Bi\r\u0013\u00022A\nD&\t\u001d!\tL\"\u000eC\u0002%\u0002R!\u0011Bi\r\u001f\u00022A\nD)\t\u001d!IL\"\u000eC\u0002%B\u0001\u0002\"0\u00076\u0001\u0007aQ\u000b\t\u0005?\u000129\u0006E\u0003\u000fG\u00152\u0019\u0005\u0003\u0005\u0005F\u001aU\u0002\u0019\u0001D.!\u0011y\u0002E\"\u0018\u0011\u000b9\u0019SE\"\u0013\t\u0011\u00115gQ\u0007a\u0001\rC\u0002Ba\b\u0011\u0007dA)abI\u0013\u0007P!9aq\r\u0001\u0005\u0002\u0019%\u0014!D:vER\u0014\u0018m\u0019;Cs.+\u00170\u0006\u0003\u0007l\u0019]D\u0003\u0002D7\rs\"2A\bD8\u0011)1\tH\"\u001a\u0002\u0002\u0003\u000fa1O\u0001\u000bKZLG-\u001a8dK\u0012\"\u0004\u0003\u0002\u001c:\rk\u00022A\nD<\t\u001d\u00119P\"\u001aC\u0002%B\u0001Ba?\u0007f\u0001\u0007a1\u0010\t\u0005?\u00012i\bE\u0003\u000fG\u00152)\bC\u0004\u0007h\u0001!\tA\"!\u0016\t\u0019\req\u0012\u000b\u0007\r\u000b3\tJb&\u0015\u0007y19\t\u0003\u0006\u0007\n\u001a}\u0014\u0011!a\u0002\r\u0017\u000b!\"\u001a<jI\u0016t7-\u001a\u00136!\u00111\u0014H\"$\u0011\u0007\u00192y\tB\u0004\u0003x\u001a}$\u0019A\u0015\t\u0011\tmhq\u0010a\u0001\r'\u0003Ba\b\u0011\u0007\u0016B)abI\u0013\u0007\u000e\"A\u0011q\nD@\u0001\u0004\t\t\u0006C\u0004\u0007h\u0001!\tAb'\u0016\t\u0019ue\u0011\u0016\u000b\u0007\r?3YK\"-\u0015\u0007y1\t\u000b\u0003\u0006\u0007$\u001ae\u0015\u0011!a\u0002\rK\u000b!\"\u001a<jI\u0016t7-\u001a\u00137!\u00111\u0014Hb*\u0011\u0007\u00192I\u000bB\u0004\u0003x\u001ae%\u0019A\u0015\t\u0011\tmh\u0011\u0014a\u0001\r[\u0003Ba\b\u0011\u00070B)abI\u0013\u0007(\"9!Q\u0015DM\u0001\u0004\t\bb\u0002D[\u0001\u0011\u0005aqW\u0001\u0007Y>|7.\u001e9\u0015\t\u0019efq\u0018\t\u0005\u0003\u001am\u0006'C\u0002\u0007>.\u00131aU3r\u0011\u001d1\tMb-A\u0002\u0015\n1a[3z\u0011\u001d1)\r\u0001C\u0001\r\u000f\f\u0001c]1wK\u0006\u001b\b*\u00193p_B4\u0015\u000e\\3\u0016\t\u0019%g1\u001c\u000b\u0005\r\u00174\t\u0010\u0006\u0003\u0007N\u001aM\u0007c\u0001\b\u0007P&\u0019a\u0011[\b\u0003\tUs\u0017\u000e\u001e\u0005\t\r+4\u0019\rq\u0001\u0007X\u0006\u0011a-\u001c\t\u0005me2I\u000eE\u0002'\r7$\u0001B\"8\u0007D\n\u0007aq\u001c\u0002\u0002\rF\u0019!F\"9\u0011\r\u0019\rhQ^\u00131\u001b\t1)O\u0003\u0003\u0007h\u001a%\u0018AB7baJ,GMC\u0002\u0007l\u001a\ta\u0001[1e_>\u0004\u0018\u0002\u0002Dx\rK\u0014AbT;uaV$hi\u001c:nCRD\u0001Bb=\u0007D\u0002\u0007aQ_\u0001\u0005a\u0006$\b\u000e\u0005\u0003\u0007x\u001auhb\u0001\b\u0007z&\u0019a1`\b\u0002\rA\u0013X\rZ3g\u0013\u00111yp\"\u0001\u0003\rM#(/\u001b8h\u0015\r1Yp\u0004\u0005\b\r\u000b\u0004A\u0011AD\u0003+\u001199a\"\u0005\u0015\r\u001d%q1CD\u000b)\u00111imb\u0003\t\u0011\u0019Uw1\u0001a\u0002\u000f\u001b\u0001BAN\u001d\b\u0010A\u0019ae\"\u0005\u0005\u0011\u0019uw1\u0001b\u0001\r?D\u0001Bb=\b\u0004\u0001\u0007aQ\u001f\u0005\t\u000f/9\u0019\u00011\u0001\b\u001a\u0005)1m\u001c3fGB\"q1DD\u0012!\u001919p\"\b\b\"%!qqDD\u0001\u0005\u0015\u0019E.Y:t!\r1s1\u0005\u0003\r\u000fK9)\"!A\u0001\u0002\u000b\u0005qq\u0005\u0002\u0004?\u0012\n\u0014c\u0001\u0016\b*A!q1FD\u001b\u001b\t9iC\u0003\u0003\b0\u001dE\u0012\u0001C2p[B\u0014Xm]:\u000b\t\u001dMb\u0011^\u0001\u0003S>LAab\u000e\b.\t\u00012i\\7qe\u0016\u001c8/[8o\u0007>$Wm\u0019\u0005\b\u000fw\u0001A\u0011AD\u001f\u0003Y\u0019\u0018M^3Bg:+w/\u0011)J\u0011\u0006$wn\u001c9GS2,W\u0003BD \u000f\u0013\"Ba\"\u0011\bXQ!aQZD\"\u0011!1)n\"\u000fA\u0004\u001d\u0015\u0003\u0003\u0002\u001c:\u000f\u000f\u00022AJD%\t!1in\"\u000fC\u0002\u001d-\u0013c\u0001\u0016\bNA1qqJD+KAj!a\"\u0015\u000b\t\u001dMc\u0011^\u0001\n[\u0006\u0004(/\u001a3vG\u0016LAAb<\bR!Aa1_D\u001d\u0001\u00041)\u0010C\u0004\b<\u0001!\tab\u0017\u0015\u0019\u00195wQLD0\u000f[:Yh\"(\t\u0011\u0019Mx\u0011\fa\u0001\rkD\u0001b\"\u0019\bZ\u0001\u0007q1M\u0001\tW\u0016L8\t\\1tgB\"qQMD5!\u001919p\"\b\bhA\u0019ae\"\u001b\u0005\u0017\u001d-tqLA\u0001\u0002\u0003\u0015\t!\u000b\u0002\u0004?\u0012\u0012\u0004\u0002CD8\u000f3\u0002\ra\"\u001d\u0002\u0015Y\fG.^3DY\u0006\u001c8\u000f\r\u0003\bt\u001d]\u0004C\u0002D|\u000f;9)\bE\u0002'\u000fo\"1b\"\u001f\bn\u0005\u0005\t\u0011!B\u0001S\t\u0019q\fJ\u001a\t\u0011\u001dut\u0011\fa\u0001\u000f\u007f\n\u0011c\\;uaV$hi\u001c:nCR\u001cE.Y:ta\u00119\ti\"\"\u0011\r\u0019]xQDDB!\r1sQ\u0011\u0003\r\u000f\u000f;Y(!A\u0001\u0002\u000b\u0005q\u0011\u0012\u0002\u0004?\u0012\"\u0014c\u0001\u0016\b\fB2qQRDI\u000f3\u0003\u0002bb\u0014\bV\u001d=uq\u0013\t\u0004M\u001dEEaCDJ\u000f+\u000b\t\u0011!A\u0003\u0002%\u00121a\u0018\u00136\t199ib\u001f\u0002\u0002\u0007\u0005)\u0011ADE!\r1s\u0011\u0014\u0003\f\u000f7;)*!A\u0001\u0002\u000b\u0005\u0011FA\u0002`IYB!bb(\bZA\u0005\t\u0019ADQ\u0003\u0011\u0019wN\u001c4\u0011\t\u001d\rvqU\u0007\u0003\u000fKSAab(\u0007j&!q\u0011VDS\u00055\u0019uN\u001c4jOV\u0014\u0018\r^5p]\"9aQ\u0019\u0001\u0005\u0002\u001d5F\u0003\u0004Dg\u000f_;\tl\"0\bJ\u001e%\b\u0002\u0003Dz\u000fW\u0003\rA\">\t\u0011\u001d\u0005t1\u0016a\u0001\u000fg\u0003Da\".\b:B1aq_D\u000f\u000fo\u00032AJD]\t-9Yl\"-\u0002\u0002\u0003\u0005)\u0011A\u0015\u0003\u0007}#s\u0007\u0003\u0005\bp\u001d-\u0006\u0019AD`a\u00119\tm\"2\u0011\r\u0019]xQDDb!\r1sQ\u0019\u0003\f\u000f\u000f<i,!A\u0001\u0002\u000b\u0005\u0011FA\u0002`IaB\u0001b\" \b,\u0002\u0007q1\u001a\u0019\u0005\u000f\u001b<\t\u000e\u0005\u0004\u0007x\u001euqq\u001a\t\u0004M\u001dEG\u0001DDj\u000f\u0013\f\t\u0011!A\u0003\u0002\u001dU'aA0%sE\u0019!fb61\r\u001dewQ\\Ds!!1\u0019O\"<\b\\\u001e\r\bc\u0001\u0014\b^\u0012Yqq\\Dq\u0003\u0003\u0005\tQ!\u0001*\u0005\u0011yF%\r\u0019\u0005\u0019\u001dMw\u0011ZA\u0001\u0004\u0003\u0015\ta\"6\u0011\u0007\u0019:)\u000fB\u0006\bh\u001e\u0005\u0018\u0011!A\u0001\u0006\u0003I#\u0001B0%cEB\u0001bb\u0006\b,\u0002\u0007q1\u001e\u0019\u0005\u000f[<\t\u0010\u0005\u0004\u0007x\u001euqq\u001e\t\u0004M\u001dEH\u0001DDz\u000fS\f\t\u0011!A\u0003\u0002\u001d\u001d\"\u0001B0%cIBqA\"2\u0001\t\u000399\u0010\u0006\b\u0007N\u001eex1 E\u0004\u0011'A\u0019\u0004c\u000f\t\u0011\u0019MxQ\u001fa\u0001\rkD\u0001b\"\u0019\bv\u0002\u0007qQ \u0019\u0005\u000f\u007fD\u0019\u0001\u0005\u0004\u0007x\u001eu\u0001\u0012\u0001\t\u0004M!\rAa\u0003E\u0003\u000fw\f\t\u0011!A\u0003\u0002%\u0012Aa\u0018\u00132g!AqqND{\u0001\u0004AI\u0001\r\u0003\t\f!=\u0001C\u0002D|\u000f;Ai\u0001E\u0002'\u0011\u001f!1\u0002#\u0005\t\b\u0005\u0005\t\u0011!B\u0001S\t!q\fJ\u00195\u0011!9ih\">A\u0002!U\u0001\u0007\u0002E\f\u00117\u0001bAb>\b\u001e!e\u0001c\u0001\u0014\t\u001c\u0011a\u0001R\u0004E\n\u0003\u0003\u0005\tQ!\u0001\t \t!q\fJ\u00196#\rQ\u0003\u0012\u0005\u0019\u0007\u0011GA9\u0003c\f\u0011\u0011\u0019\rhQ\u001eE\u0013\u0011[\u00012A\nE\u0014\t-AI\u0003c\u000b\u0002\u0002\u0003\u0005)\u0011A\u0015\u0003\t}#\u0013G\u000e\u0003\r\u0011;A\u0019\"!A\u0002\u0002\u000b\u0005\u0001r\u0004\t\u0004M!=Ba\u0003E\u0019\u0011W\t\t\u0011!A\u0003\u0002%\u0012Aa\u0018\u00132o!QqqTD{!\u0003\u0005\r\u0001#\u000e\u0011\t\u0019\r\brG\u0005\u0005\u0011s1)OA\u0004K_\n\u001cuN\u001c4\t\u0015\u001d]qQ\u001fI\u0001\u0002\u0004Ai\u0004E\u0003\u000f\u0007'Ay\u0004\r\u0003\tB!\u0015\u0003C\u0002D|\u000f;A\u0019\u0005E\u0002'\u0011\u000b\"A\u0002c\u0012\t<\u0005\u0005\t\u0011!B\u0001\u000fO\u0011Aa\u0018\u00132q!9\u00012\n\u0001\u0005\u0002!5\u0013!G:bm\u0016\f5OT3x\u0003BK\u0005*\u00193p_B$\u0015\r^1tKR$BA\"4\tP!Aqq\u0014E%\u0001\u00049\t\u000bC\u0004\tT\u0001!\t\u0001#\u0016\u0002'M\fg/Z!t\u0011\u0006$wn\u001c9ECR\f7/\u001a;\u0015\t\u00195\u0007r\u000b\u0005\t\u000f?C\t\u00061\u0001\t6!9\u00012\f\u0001\u0005\u0002!u\u0013\u0001B6fsN,\"\u0001c\u0018\u0011\u0007}\u0001S\u0005C\u0004\td\u0001!\t\u0001#\u001a\u0002\rY\fG.^3t+\tA9\u0007E\u0002 AAB\u0001b\"\u0019\u0001\t\u0003!\u00012N\u000b\u0003\u0011[\u0002D\u0001c\u001c\ttA1aq_D\u000f\u0011c\u00022A\nE:\t-A)\b#\u001b\u0002\u0002\u0003\u0005)\u0011A\u0015\u0003\t}#\u0013'\u000f\u0005\t\u000f_\u0002A\u0011\u0001\u0003\tzU\u0011\u00012\u0010\u0019\u0005\u0011{B\t\t\u0005\u0004\u0007x\u001eu\u0001r\u0010\t\u0004M!\u0005Ea\u0003EB\u0011o\n\t\u0011!A\u0003\u0002%\u0012Aa\u0018\u00133a!A\u0001r\u0011\u0001\u0005\u0002\u0011AI)A\u0006lKf|%\u000fZ3sS:<WC\u0001EF!\u0011q11\u0003!\t\u0013!=\u0005!%A\u0005\u0002!E\u0015!F:b[BdWMQ=LKf$C-\u001a4bk2$HeM\u000b\u0003\u0011'SCAa\u000f\t\u0016.\u0012\u0001r\u0013\t\u0005\u00113C\t+\u0004\u0002\t\u001c*!\u0001R\u0014EP\u0003%)hn\u00195fG.,GMC\u0002\u0002\f=IA\u0001c)\t\u001c\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u0013!\u001d\u0006!%A\u0005\u0002!E\u0015AG:b[BdWMQ=LKf,\u00050Y2uI\u0011,g-Y;mi\u0012\u001a\u0004\"\u0003EV\u0001E\u0005I\u0011\u0001EW\u0003\t\u001aw.\u001c2j]\u0016\u0014\u0015pS3z/&$\bn\u00117bgN$\u0016m\u001a\u0013eK\u001a\fW\u000f\u001c;%kU!\u0001r\u0016EZ+\tA\tLK\u0002x\u0011+#aA\u0018EU\u0005\u0004I\u0003\"\u0003E\\\u0001E\u0005I\u0011\u0001E]\u0003\t\u001aw.\u001c2j]\u0016\u0014\u0015pS3z/&$\bn\u00117bgN$\u0016m\u001a\u0013eK\u001a\fW\u000f\u001c;%mU!\u00012\u0018E`+\tAiLK\u0002}\u0011+#aA\u0018E[\u0005\u0004I\u0003\"\u0003Eb\u0001E\u0005I\u0011\u0001Ec\u0003i\u0019w.\u001e8u\u0005f\\U-_!qaJ|\u0007\u0010\n3fM\u0006,H\u000e\u001e\u00133+\tA9M\u000b\u0003\u00032!U\u0005\"\u0003Ef\u0001E\u0005I\u0011\u0001Eg\u0003i\u0019\u0018M^3Bg\"\u000bGm\\8q\r&dW\r\n3fM\u0006,H\u000e\u001e\u00136+\tAyM\u000b\u0003\t6!U\u0005\"\u0003Ej\u0001E\u0005I\u0011\u0001Ek\u0003i\u0019\u0018M^3Bg\"\u000bGm\\8q\r&dW\r\n3fM\u0006,H\u000e\u001e\u00137+\tA9N\u000b\u0003\tZ\"U\u0005#\u0002\b\u0004\u0014!m\u0007\u0007\u0002Eo\u0011C\u0004bAb>\b\u001e!}\u0007c\u0001\u0014\tb\u0012a\u0001r\tEi\u0003\u0003\u0005\tQ!\u0001\b(!I\u0001R\u001d\u0001\u0012\u0002\u0013\u0005\u0001r]\u0001!g\u00064X-Q:OK^\f\u0005+\u0013%bI>|\u0007OR5mK\u0012\"WMZ1vYR$S'\u0006\u0002\tj*\"q\u0011\u0015EK\u0011%Ai\u000fAI\u0001\n\u0003A)-\u0001\u0012d_VtG/\u00119qe>DH)[:uS:\u001cGOQ=LKf$C-\u001a4bk2$H%\r\u0005\n\u0011c\u0004\u0011\u0013!C\u0001\u0011g\facY8nE&tWMQ=LKf$C-\u001a4bk2$H%N\u000b\u0005\u0011_C)\u0010\u0002\u0004_\u0011_\u0014\r!\u000b\u0005\n\u0011s\u0004\u0011\u0013!C\u0001\u0011w\facY8nE&tWMQ=LKf$C-\u001a4bk2$HEN\u000b\u0005\u0011wCi\u0010\u0002\u0004_\u0011o\u0014\r!K\u0004\n\u0013\u0003\u0011\u0011\u0011!E\u0001\u0013\u0007\t\u0001\u0003U1jeJ#EIR;oGRLwN\\:\u0011\u0007}I)A\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012AE\u0004'\u0011I)!D\r\t\u000f5K)\u0001\"\u0001\n\fQ\u0011\u00112\u0001\u0005\u000b\u0013\u001fI)!%A\u0005\u0002%E\u0011a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$C'\u0006\u0004\n\u0014%\u0015\u0012\u0012\u0006\u000b\u0005\u0013+IiB\u000b\u0003\n\u0018!U\u0005c\u0001\b\n\u001a%\u0019\u00112D\b\u0003\t9+H\u000e\u001c\u0005\b;%5\u0001\u0019AE\u0010!\u0011y\u0002%#\t\u0011\r9\u0019\u00132EE\u0014!\r1\u0013R\u0005\u0003\u0007Q%5!\u0019A\u0015\u0011\u0007\u0019JI\u0003\u0002\u00043\u0013\u001b\u0011\r!\u000b\u0005\u000b\u0013[I)!!A\u0005\n%=\u0012a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!#\r\u0011\t%M\u0012RH\u0007\u0003\u0013kQA!c\u000e\n:\u0005!A.\u00198h\u0015\tIY$\u0001\u0003kCZ\f\u0017\u0002BE \u0013k\u0011aa\u00142kK\u000e$\b"
)
public class PairRDDFunctions<K, V> implements Logging, Serializable {
   public final RDD<Tuple2<K, V>> org$apache$spark$rdd$PairRDDFunctions$$self;
   public final ClassTag<K> org$apache$spark$rdd$PairRDDFunctions$$kt;
   public final ClassTag<V> org$apache$spark$rdd$PairRDDFunctions$$vt;
   public final Ordering<K> org$apache$spark$rdd$PairRDDFunctions$$ord;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static <K, V> Null$ $lessinit$greater$default$4(RDD<Tuple2<K, V>> var0) {
      return PairRDDFunctions$.MODULE$.$lessinit$greater$default$4(var0);
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

   @Experimental
   public <C> RDD<Tuple2<K, C>> combineByKeyWithClassTag(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners, Partitioner partitioner, boolean mapSideCombine, Serializer serializer, ClassTag<C> ct) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, createCombiner, mergeValue, mergeCombiners, partitioner, mapSideCombine, serializer, ct) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function1 createCombiner$3;
         private final Function2 mergeValue$3;
         private final Function2 mergeCombiners$3;
         private final Partitioner partitioner$1;
         private final boolean mapSideCombine$1;
         private final Serializer serializer$1;
         private final ClassTag ct$3;

         public final RDD<Tuple2<K, C>> apply() {
            Predef$.MODULE$.require(this.mergeCombiners$3 != null, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "mergeCombiners must be defined";
               }
            });
            if (this.$outer.keyClass().isArray()) {
               if (this.mapSideCombine$1) {
                  throw new SparkException("Cannot use map-side combining with array keys.");
               }

               if (this.partitioner$1 instanceof HashPartitioner) {
                  throw new SparkException("HashPartitioner cannot partition array keys.");
               }
            }

            Aggregator aggregator;
            Object var12;
            label41: {
               SparkContext qual$1 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.context();
               Function1 x$10 = this.createCombiner$3;
               boolean x$11 = qual$1.clean$default$2();
               Function1 var10002 = (Function1)qual$1.clean(x$10, x$11);
               SparkContext qual$2 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.context();
               Function2 x$12 = this.mergeValue$3;
               boolean x$13 = qual$2.clean$default$2();
               Function2 var10003 = (Function2)qual$2.clean(x$12, x$13);
               SparkContext qual$3 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.context();
               Function2 x$14 = this.mergeCombiners$3;
               boolean x$15 = qual$3.clean$default$2();
               aggregator = new Aggregator(var10002, var10003, (Function2)qual$3.clean(x$14, x$15));
               Option var10000 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.partitioner();
               Some var11 = new Some(this.partitioner$1);
               if (var10000 == null) {
                  if (var11 == null) {
                     break label41;
                  }
               } else if (var10000.equals(var11)) {
                  break label41;
               }

               var12 = (new ShuffledRDD(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.partitioner$1, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt, this.ct$3)).setSerializer(this.serializer$1).setAggregator(aggregator).setMapSideCombine(this.mapSideCombine$1);
               return (RDD<Tuple2<K, C>>)var12;
            }

            var12 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.mapPartitions(new Serializable(this, aggregator) {
               public static final long serialVersionUID = 0L;
               private final Aggregator aggregator$1;

               public final InterruptibleIterator<Tuple2<K, C>> apply(Iterator<Tuple2<K, V>> iter) {
                  TaskContext context = TaskContext$.MODULE$.get();
                  return new InterruptibleIterator<Tuple2<K, C>>(context, this.aggregator$1.combineValuesByKey(iter, context));
               }

               public {
                  this.aggregator$1 = aggregator$1;
               }
            }, true, ClassTag$.MODULE$.apply(Tuple2.class));
            return (RDD<Tuple2<K, C>>)var12;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.createCombiner$3 = createCombiner$3;
               this.mergeValue$3 = mergeValue$3;
               this.mergeCombiners$3 = mergeCombiners$3;
               this.partitioner$1 = partitioner$1;
               this.mapSideCombine$1 = mapSideCombine$1;
               this.serializer$1 = serializer$1;
               this.ct$3 = ct$3;
            }
         }
      });
   }

   public <C> RDD<Tuple2<K, C>> combineByKey(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners, Partitioner partitioner, boolean mapSideCombine, Serializer serializer) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, createCombiner, mergeValue, mergeCombiners, partitioner, mapSideCombine, serializer) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function1 createCombiner$4;
         private final Function2 mergeValue$4;
         private final Function2 mergeCombiners$4;
         private final Partitioner partitioner$16;
         private final boolean mapSideCombine$2;
         private final Serializer serializer$2;

         public final RDD<Tuple2<K, C>> apply() {
            return this.$outer.combineByKeyWithClassTag(this.createCombiner$4, this.mergeValue$4, this.mergeCombiners$4, this.partitioner$16, this.mapSideCombine$2, this.serializer$2, (ClassTag)null);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.createCombiner$4 = createCombiner$4;
               this.mergeValue$4 = mergeValue$4;
               this.mergeCombiners$4 = mergeCombiners$4;
               this.partitioner$16 = partitioner$16;
               this.mapSideCombine$2 = mapSideCombine$2;
               this.serializer$2 = serializer$2;
            }
         }
      });
   }

   public <C> RDD<Tuple2<K, C>> combineByKey(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, createCombiner, mergeValue, mergeCombiners, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function1 createCombiner$5;
         private final Function2 mergeValue$5;
         private final Function2 mergeCombiners$5;
         private final int numPartitions$15;

         public final RDD<Tuple2<K, C>> apply() {
            return this.$outer.combineByKeyWithClassTag(this.createCombiner$5, this.mergeValue$5, this.mergeCombiners$5, this.numPartitions$15, (ClassTag)null);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.createCombiner$5 = createCombiner$5;
               this.mergeValue$5 = mergeValue$5;
               this.mergeCombiners$5 = mergeCombiners$5;
               this.numPartitions$15 = numPartitions$15;
            }
         }
      });
   }

   @Experimental
   public <C> RDD<Tuple2<K, C>> combineByKeyWithClassTag(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners, int numPartitions, ClassTag<C> ct) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, createCombiner, mergeValue, mergeCombiners, numPartitions, ct) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function1 createCombiner$2;
         private final Function2 mergeValue$2;
         private final Function2 mergeCombiners$2;
         private final int numPartitions$1;
         private final ClassTag ct$2;

         public final RDD<Tuple2<K, C>> apply() {
            return this.$outer.combineByKeyWithClassTag(this.createCombiner$2, this.mergeValue$2, this.mergeCombiners$2, new HashPartitioner(this.numPartitions$1), this.$outer.combineByKeyWithClassTag$default$5(), this.$outer.combineByKeyWithClassTag$default$6(), this.ct$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.createCombiner$2 = createCombiner$2;
               this.mergeValue$2 = mergeValue$2;
               this.mergeCombiners$2 = mergeCombiners$2;
               this.numPartitions$1 = numPartitions$1;
               this.ct$2 = ct$2;
            }
         }
      });
   }

   public <U> RDD<Tuple2<K, U>> aggregateByKey(U zeroValue, Partitioner partitioner, Function2<U, V, U> seqOp, Function2<U, U, U> combOp, ClassTag<U> evidence$1) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, zeroValue, partitioner, seqOp, combOp, evidence$1) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Object zeroValue$3;
         private final Partitioner partitioner$3;
         private final Function2 seqOp$3;
         private final Function2 combOp$3;
         public final ClassTag evidence$1$1;

         public final RDD<Tuple2<K, U>> apply() {
            ObjectRef cachedSerializer$lzy = ObjectRef.zero();
            VolatileByteRef bitmap$0 = VolatileByteRef.create((byte)0);
            ByteBuffer zeroBuffer = SparkEnv$.MODULE$.get().serializer().newInstance().serialize(this.zeroValue$3, this.evidence$1$1);
            byte[] zeroArray = new byte[zeroBuffer.limit()];
            zeroBuffer.get(zeroArray);
            Function0 createZero = new Serializable(this, zeroArray, cachedSerializer$lzy, bitmap$0) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final byte[] zeroArray$1;
               private final ObjectRef cachedSerializer$lzy$1;
               private final VolatileByteRef bitmap$0$1;

               public final U apply() {
                  return (U)this.$outer.org$apache$spark$rdd$PairRDDFunctions$$anonfun$$cachedSerializer$1(this.cachedSerializer$lzy$1, this.bitmap$0$1).deserialize(ByteBuffer.wrap(this.zeroArray$1), this.$outer.evidence$1$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.zeroArray$1 = zeroArray$1;
                     this.cachedSerializer$lzy$1 = cachedSerializer$lzy$1;
                     this.bitmap$0$1 = bitmap$0$1;
                  }
               }
            };
            SparkContext qual$4 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.context();
            Function2 x$16 = this.seqOp$3;
            boolean x$17 = qual$4.clean$default$2();
            Function2 cleanedSeqOp = (Function2)qual$4.clean(x$16, x$17);
            return this.$outer.combineByKeyWithClassTag(new Serializable(this, createZero, cleanedSeqOp) {
               public static final long serialVersionUID = 0L;
               private final Function0 createZero$1;
               private final Function2 cleanedSeqOp$1;

               public final U apply(V v) {
                  return (U)this.cleanedSeqOp$1.apply(this.createZero$1.apply(), v);
               }

               public {
                  this.createZero$1 = createZero$1;
                  this.cleanedSeqOp$1 = cleanedSeqOp$1;
               }
            }, cleanedSeqOp, this.combOp$3, this.partitioner$3, this.$outer.combineByKeyWithClassTag$default$5(), this.$outer.combineByKeyWithClassTag$default$6(), this.evidence$1$1);
         }

         private final SerializerInstance cachedSerializer$lzycompute$1(ObjectRef cachedSerializer$lzy$1, VolatileByteRef bitmap$0$1) {
            synchronized(this) {
               if ((byte)(bitmap$0$1.elem & 1) == 0) {
                  cachedSerializer$lzy$1.elem = (T)SparkEnv$.MODULE$.get().serializer().newInstance();
                  bitmap$0$1.elem = (byte)(bitmap$0$1.elem | 1);
               }

               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

            return (SerializerInstance)cachedSerializer$lzy$1.elem;
         }

         public final SerializerInstance org$apache$spark$rdd$PairRDDFunctions$$anonfun$$cachedSerializer$1(ObjectRef cachedSerializer$lzy$1, VolatileByteRef bitmap$0$1) {
            return (byte)(bitmap$0$1.elem & 1) == 0 ? this.cachedSerializer$lzycompute$1(cachedSerializer$lzy$1, bitmap$0$1) : (SerializerInstance)cachedSerializer$lzy$1.elem;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.zeroValue$3 = zeroValue$3;
               this.partitioner$3 = partitioner$3;
               this.seqOp$3 = seqOp$3;
               this.combOp$3 = combOp$3;
               this.evidence$1$1 = evidence$1$1;
            }
         }
      });
   }

   public <U> RDD<Tuple2<K, U>> aggregateByKey(U zeroValue, int numPartitions, Function2<U, V, U> seqOp, Function2<U, U, U> combOp, ClassTag<U> evidence$2) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, zeroValue, numPartitions, seqOp, combOp, evidence$2) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Object zeroValue$2;
         private final int numPartitions$3;
         private final Function2 seqOp$2;
         private final Function2 combOp$2;
         private final ClassTag evidence$2$1;

         public final RDD<Tuple2<K, U>> apply() {
            return this.$outer.aggregateByKey(this.zeroValue$2, new HashPartitioner(this.numPartitions$3), this.seqOp$2, this.combOp$2, this.evidence$2$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.zeroValue$2 = zeroValue$2;
               this.numPartitions$3 = numPartitions$3;
               this.seqOp$2 = seqOp$2;
               this.combOp$2 = combOp$2;
               this.evidence$2$1 = evidence$2$1;
            }
         }
      });
   }

   public <U> RDD<Tuple2<K, U>> aggregateByKey(U zeroValue, Function2<U, V, U> seqOp, Function2<U, U, U> combOp, ClassTag<U> evidence$3) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, zeroValue, seqOp, combOp, evidence$3) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Object zeroValue$1;
         private final Function2 seqOp$1;
         private final Function2 combOp$1;
         private final ClassTag evidence$3$1;

         public final RDD<Tuple2<K, U>> apply() {
            return this.$outer.aggregateByKey(this.zeroValue$1, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[0])), this.seqOp$1, this.combOp$1, this.evidence$3$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.zeroValue$1 = zeroValue$1;
               this.seqOp$1 = seqOp$1;
               this.combOp$1 = combOp$1;
               this.evidence$3$1 = evidence$3$1;
            }
         }
      });
   }

   public RDD<Tuple2<K, V>> foldByKey(V zeroValue, Partitioner partitioner, Function2<V, V, V> func) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, zeroValue, partitioner, func) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Object zeroValue$6;
         private final Partitioner partitioner$4;
         private final Function2 func$7;

         public final RDD<Tuple2<K, V>> apply() {
            ObjectRef cachedSerializer$lzy = ObjectRef.zero();
            VolatileByteRef bitmap$0 = VolatileByteRef.create((byte)0);
            ByteBuffer zeroBuffer = SparkEnv$.MODULE$.get().serializer().newInstance().serialize(this.zeroValue$6, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt);
            byte[] zeroArray = new byte[zeroBuffer.limit()];
            zeroBuffer.get(zeroArray);
            Function0 createZero = new Serializable(this, zeroArray, cachedSerializer$lzy, bitmap$0) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final byte[] zeroArray$2;
               private final ObjectRef cachedSerializer$lzy$2;
               private final VolatileByteRef bitmap$0$2;

               public final V apply() {
                  return (V)this.$outer.org$apache$spark$rdd$PairRDDFunctions$$anonfun$$cachedSerializer$2(this.cachedSerializer$lzy$2, this.bitmap$0$2).deserialize(ByteBuffer.wrap(this.zeroArray$2), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$anonfun$$$outer().org$apache$spark$rdd$PairRDDFunctions$$vt);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.zeroArray$2 = zeroArray$2;
                     this.cachedSerializer$lzy$2 = cachedSerializer$lzy$2;
                     this.bitmap$0$2 = bitmap$0$2;
                  }
               }
            };
            SparkContext qual$5 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.context();
            Function2 x$18 = this.func$7;
            boolean x$19 = qual$5.clean$default$2();
            Function2 cleanedFunc = (Function2)qual$5.clean(x$18, x$19);
            return this.$outer.combineByKeyWithClassTag(new Serializable(this, createZero, cleanedFunc) {
               public static final long serialVersionUID = 0L;
               private final Function0 createZero$2;
               private final Function2 cleanedFunc$1;

               public final V apply(V v) {
                  return (V)this.cleanedFunc$1.apply(this.createZero$2.apply(), v);
               }

               public {
                  this.createZero$2 = createZero$2;
                  this.cleanedFunc$1 = cleanedFunc$1;
               }
            }, cleanedFunc, cleanedFunc, this.partitioner$4, this.$outer.combineByKeyWithClassTag$default$5(), this.$outer.combineByKeyWithClassTag$default$6(), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt);
         }

         // $FF: synthetic method
         public PairRDDFunctions org$apache$spark$rdd$PairRDDFunctions$$anonfun$$$outer() {
            return this.$outer;
         }

         private final SerializerInstance cachedSerializer$lzycompute$2(ObjectRef cachedSerializer$lzy$2, VolatileByteRef bitmap$0$2) {
            synchronized(this) {
               if ((byte)(bitmap$0$2.elem & 1) == 0) {
                  cachedSerializer$lzy$2.elem = (T)SparkEnv$.MODULE$.get().serializer().newInstance();
                  bitmap$0$2.elem = (byte)(bitmap$0$2.elem | 1);
               }

               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

            return (SerializerInstance)cachedSerializer$lzy$2.elem;
         }

         public final SerializerInstance org$apache$spark$rdd$PairRDDFunctions$$anonfun$$cachedSerializer$2(ObjectRef cachedSerializer$lzy$2, VolatileByteRef bitmap$0$2) {
            return (byte)(bitmap$0$2.elem & 1) == 0 ? this.cachedSerializer$lzycompute$2(cachedSerializer$lzy$2, bitmap$0$2) : (SerializerInstance)cachedSerializer$lzy$2.elem;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.zeroValue$6 = zeroValue$6;
               this.partitioner$4 = partitioner$4;
               this.func$7 = func$7;
            }
         }
      });
   }

   public RDD<Tuple2<K, V>> foldByKey(V zeroValue, int numPartitions, Function2<V, V, V> func) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, zeroValue, numPartitions, func) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Object zeroValue$5;
         private final int numPartitions$4;
         private final Function2 func$6;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.foldByKey(this.zeroValue$5, new HashPartitioner(this.numPartitions$4), this.func$6);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.zeroValue$5 = zeroValue$5;
               this.numPartitions$4 = numPartitions$4;
               this.func$6 = func$6;
            }
         }
      });
   }

   public RDD<Tuple2<K, V>> foldByKey(V zeroValue, Function2<V, V, V> func) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, zeroValue, func) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Object zeroValue$4;
         private final Function2 func$5;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.foldByKey(this.zeroValue$4, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[0])), this.func$5);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.zeroValue$4 = zeroValue$4;
               this.func$5 = func$5;
            }
         }
      });
   }

   public RDD<Tuple2<K, V>> sampleByKey(boolean withReplacement, Map<K, Object> fractions, long seed) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, withReplacement, fractions, seed) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final boolean withReplacement$1;
         private final Map fractions$1;
         private final long seed$1;

         public final RDD<Tuple2<K, V>> apply() {
            Predef$.MODULE$.require(this.fractions$1.values().forall(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(double v) {
                  return this.apply$mcZD$sp(v);
               }

               public boolean apply$mcZD$sp(double v) {
                  return v >= (double)0.0F;
               }
            }), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Negative sampling rates.";
               }
            });
            Function2 samplingFunc = this.withReplacement$1 ? StratifiedSamplingUtils$.MODULE$.getPoissonSamplingFunction(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.fractions$1, false, this.seed$1, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt) : StratifiedSamplingUtils$.MODULE$.getBernoulliSamplingFunction(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.fractions$1, false, this.seed$1);
            return this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.<Tuple2<K, V>>mapPartitionsWithIndex(samplingFunc, true, ClassTag$.MODULE$.apply(Tuple2.class));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.withReplacement$1 = withReplacement$1;
               this.fractions$1 = fractions$1;
               this.seed$1 = seed$1;
            }
         }
      });
   }

   public long sampleByKey$default$3() {
      return Utils$.MODULE$.random().nextLong();
   }

   public RDD<Tuple2<K, V>> sampleByKeyExact(boolean withReplacement, Map<K, Object> fractions, long seed) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, withReplacement, fractions, seed) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final boolean withReplacement$2;
         private final Map fractions$2;
         private final long seed$2;

         public final RDD<Tuple2<K, V>> apply() {
            Predef$.MODULE$.require(this.fractions$2.values().forall(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(double v) {
                  return this.apply$mcZD$sp(v);
               }

               public boolean apply$mcZD$sp(double v) {
                  return v >= (double)0.0F;
               }
            }), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Negative sampling rates.";
               }
            });
            Function2 samplingFunc = this.withReplacement$2 ? StratifiedSamplingUtils$.MODULE$.getPoissonSamplingFunction(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.fractions$2, true, this.seed$2, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt) : StratifiedSamplingUtils$.MODULE$.getBernoulliSamplingFunction(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.fractions$2, true, this.seed$2);
            return this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.<Tuple2<K, V>>mapPartitionsWithIndex(samplingFunc, true, ClassTag$.MODULE$.apply(Tuple2.class));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.withReplacement$2 = withReplacement$2;
               this.fractions$2 = fractions$2;
               this.seed$2 = seed$2;
            }
         }
      });
   }

   public long sampleByKeyExact$default$3() {
      return Utils$.MODULE$.random().nextLong();
   }

   public RDD<Tuple2<K, V>> reduceByKey(Partitioner partitioner, Function2<V, V, V> func) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, partitioner, func) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Partitioner partitioner$2;
         private final Function2 func$3;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.combineByKeyWithClassTag(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final V apply(V v) {
                  return (V)v;
               }
            }, this.func$3, this.func$3, this.partitioner$2, this.$outer.combineByKeyWithClassTag$default$5(), this.$outer.combineByKeyWithClassTag$default$6(), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.partitioner$2 = partitioner$2;
               this.func$3 = func$3;
            }
         }
      });
   }

   public RDD<Tuple2<K, V>> reduceByKey(Function2<V, V, V> func, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, func, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function2 func$2;
         private final int numPartitions$2;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.reduceByKey(new HashPartitioner(this.numPartitions$2), this.func$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.func$2 = func$2;
               this.numPartitions$2 = numPartitions$2;
            }
         }
      });
   }

   public RDD<Tuple2<K, V>> reduceByKey(Function2<V, V, V> func) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, func) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function2 func$1;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.reduceByKey(Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[0])), this.func$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.func$1 = func$1;
            }
         }
      });
   }

   public Map<K, V> reduceByKeyLocally(Function2<V, V, V> func) {
      return (Map)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, func) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function2 func$4;

         public final scala.collection.mutable.Map<K, V> apply() {
            SparkContext qual$6 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.sparkContext();
            Function2 x$20 = this.func$4;
            boolean x$21 = qual$6.clean$default$2();
            Function2 cleanedF = (Function2)qual$6.clean(x$20, x$21);
            if (this.$outer.keyClass().isArray()) {
               throw new SparkException("reduceByKeyLocally() does not support array keys");
            } else {
               Function1 reducePartition = new Serializable(this, cleanedF) {
                  public static final long serialVersionUID = 0L;
                  public final Function2 cleanedF$1;

                  public final Iterator<HashMap<K, V>> apply(Iterator<Tuple2<K, V>> iter) {
                     HashMap map = new HashMap();
                     iter.foreach(new Serializable(this, map) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final HashMap map$1;

                        public final V apply(Tuple2<K, V> pair) {
                           Object old = this.map$1.get(pair._1());
                           return (V)this.map$1.put(pair._1(), old == null ? pair._2() : this.$outer.cleanedF$1.apply(old, pair._2()));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.map$1 = map$1;
                           }
                        }
                     });
                     return scala.package$.MODULE$.Iterator().<HashMap<K, V>>apply(Predef$.MODULE$.wrapRefArray((Object[])(new HashMap[]{map})));
                  }

                  public {
                     this.cleanedF$1 = cleanedF$1;
                  }
               };
               Function2 mergeMaps = new Serializable(this, cleanedF) {
                  public static final long serialVersionUID = 0L;
                  public final Function2 cleanedF$1;

                  public final HashMap<K, V> apply(HashMap<K, V> m1, HashMap<K, V> m2) {
                     ((IterableLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(m2).asScala()).foreach(new Serializable(this, m1) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final HashMap m1$1;

                        public final V apply(Tuple2<K, V> pair) {
                           Object old = this.m1$1.get(pair._1());
                           return (V)this.m1$1.put(pair._1(), old == null ? pair._2() : this.$outer.cleanedF$1.apply(old, pair._2()));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.m1$1 = m1$1;
                           }
                        }
                     });
                     return m1;
                  }

                  public {
                     this.cleanedF$1 = cleanedF$1;
                  }
               };
               return (scala.collection.mutable.Map)JavaConverters$.MODULE$.mapAsScalaMapConverter((java.util.Map)this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.mapPartitions(reducePartition, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.mapPartitions$default$2(), ClassTag$.MODULE$.apply(HashMap.class)).reduce(mergeMaps)).asScala();
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.func$4 = func$4;
            }
         }
      });
   }

   public Map<K, Object> countByKey() {
      return (Map)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;

         public final scala.collection.immutable.Map<K, Object> apply() {
            return Predef$.MODULE$.refArrayOps(RDD$.MODULE$.rddToPairRDDFunctions(RDD$.MODULE$.rddToPairRDDFunctions(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).mapValues(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(V x$1) {
                  return 1L;
               }
            }), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, ClassTag$.MODULE$.Long(), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).reduceByKey(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(long x$2, long x$3) {
                  return this.apply$mcJJJ$sp(x$2, x$3);
               }

               public long apply$mcJJJ$sp(long x$2, long x$3) {
                  return x$2 + x$3;
               }
            }).collect()).toMap(Predef$.MODULE$.$conforms());
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

   public PartialResult<Map<K, BoundedDouble>> countByKeyApprox(long timeout, double confidence) {
      return (PartialResult)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, timeout, confidence) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final long timeout$1;
         private final double confidence$1;

         public final PartialResult<Map<K, BoundedDouble>> apply() {
            return this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.<Map<K, BoundedDouble>>map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final K apply(Tuple2<K, V> x$4) {
                  return (K)x$4._1();
               }
            }, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt).countByValueApprox(this.timeout$1, this.confidence$1, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.timeout$1 = timeout$1;
               this.confidence$1 = confidence$1;
            }
         }
      });
   }

   public double countByKeyApprox$default$2() {
      return 0.95;
   }

   public RDD<Tuple2<K, Object>> countApproxDistinctByKey(int p, int sp, Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, p, sp, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         public final int p$2;
         public final int sp$1;
         private final Partitioner partitioner$15;

         public final RDD<Tuple2<K, Object>> apply() {
            Predef$.MODULE$.require(this.p$2 >= 4, new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"p (", ") must be >= 4"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.p$2)}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            Predef$.MODULE$.require(this.sp$1 <= 32, new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"sp (", ") must be <= 32"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.sp$1)}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            Predef$.MODULE$.require(this.sp$1 == 0 || this.p$2 <= this.sp$1, new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"p (", ") cannot be greater than sp (", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.p$2), BoxesRunTime.boxToInteger(this.$outer.sp$1)}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            Function1 createHLL = new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final HyperLogLogPlus apply(V v) {
                  HyperLogLogPlus hll = new HyperLogLogPlus(this.$outer.p$2, this.$outer.sp$1);
                  hll.offer(v);
                  return hll;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            };
            Function2 mergeValueHLL = new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final HyperLogLogPlus apply(HyperLogLogPlus hll, V v) {
                  hll.offer(v);
                  return hll;
               }
            };
            Function2 mergeHLL = new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final HyperLogLogPlus apply(HyperLogLogPlus h1, HyperLogLogPlus h2) {
                  h1.addAll(h2);
                  return h1;
               }
            };
            return RDD$.MODULE$.rddToPairRDDFunctions(this.$outer.combineByKeyWithClassTag(createHLL, mergeValueHLL, mergeHLL, this.partitioner$15, this.$outer.combineByKeyWithClassTag$default$5(), this.$outer.combineByKeyWithClassTag$default$6(), ClassTag$.MODULE$.apply(HyperLogLogPlus.class)), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, ClassTag$.MODULE$.apply(HyperLogLogPlus.class), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).mapValues(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(HyperLogLogPlus x$5) {
                  return x$5.cardinality();
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.p$2 = p$2;
               this.sp$1 = sp$1;
               this.partitioner$15 = partitioner$15;
            }
         }
      });
   }

   public RDD<Tuple2<K, Object>> countApproxDistinctByKey(double relativeSD, Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, relativeSD, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         public final double relativeSD$3;
         private final Partitioner partitioner$14;

         public final RDD<Tuple2<K, Object>> apply() {
            Predef$.MODULE$.require(this.relativeSD$3 > 1.7E-5, new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"accuracy (", ") must be greater than 0.000017"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.relativeSD$3)}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            int p = (int)scala.math.package$.MODULE$.ceil((double)2.0F * scala.math.package$.MODULE$.log(1.054 / this.relativeSD$3) / scala.math.package$.MODULE$.log((double)2.0F));
            Predef$.MODULE$.assert(p <= 32);
            return this.$outer.countApproxDistinctByKey(p < 4 ? 4 : p, 0, this.partitioner$14);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.relativeSD$3 = relativeSD$3;
               this.partitioner$14 = partitioner$14;
            }
         }
      });
   }

   public RDD<Tuple2<K, Object>> countApproxDistinctByKey(double relativeSD, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, relativeSD, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final double relativeSD$2;
         private final int numPartitions$14;

         public final RDD<Tuple2<K, Object>> apply() {
            return this.$outer.countApproxDistinctByKey(this.relativeSD$2, new HashPartitioner(this.numPartitions$14));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.relativeSD$2 = relativeSD$2;
               this.numPartitions$14 = numPartitions$14;
            }
         }
      });
   }

   public RDD<Tuple2<K, Object>> countApproxDistinctByKey(double relativeSD) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, relativeSD) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final double relativeSD$1;

         public final RDD<Tuple2<K, Object>> apply() {
            return this.$outer.countApproxDistinctByKey(this.relativeSD$1, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[0])));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.relativeSD$1 = relativeSD$1;
            }
         }
      });
   }

   public double countApproxDistinctByKey$default$1() {
      return 0.05;
   }

   public RDD<Tuple2<K, Iterable<V>>> groupByKey(Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Partitioner partitioner$5;

         public final RDD<Tuple2<K, Iterable<V>>> apply() {
            Function1 createCombiner = new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final CompactBuffer<V> apply(V v) {
                  return CompactBuffer$.MODULE$.<V>apply(v, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$anonfun$$$outer().org$apache$spark$rdd$PairRDDFunctions$$vt);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            };
            Function2 mergeValue = new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final CompactBuffer<V> apply(CompactBuffer<V> buf, V v) {
                  return buf.$plus$eq(v);
               }
            };
            Function2 mergeCombiners = new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final CompactBuffer<V> apply(CompactBuffer<V> c1, CompactBuffer<V> c2) {
                  return c1.$plus$plus$eq(c2);
               }
            };
            RDD bufs = this.$outer.combineByKeyWithClassTag(createCombiner, mergeValue, mergeCombiners, this.partitioner$5, false, this.$outer.combineByKeyWithClassTag$default$6(), ClassTag$.MODULE$.apply(CompactBuffer.class));
            return bufs;
         }

         // $FF: synthetic method
         public PairRDDFunctions org$apache$spark$rdd$PairRDDFunctions$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.partitioner$5 = partitioner$5;
            }
         }
      });
   }

   public RDD<Tuple2<K, Iterable<V>>> groupByKey(int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final int numPartitions$5;

         public final RDD<Tuple2<K, Iterable<V>>> apply() {
            return this.$outer.groupByKey(new HashPartitioner(this.numPartitions$5));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.numPartitions$5 = numPartitions$5;
            }
         }
      });
   }

   public RDD<Tuple2<K, V>> partitionBy(Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Partitioner partitioner$6;

         public final RDD<Tuple2<K, V>> apply() {
            if (this.$outer.keyClass().isArray() && this.partitioner$6 instanceof HashPartitioner) {
               throw new SparkException("HashPartitioner cannot partition array keys.");
            } else {
               Object var2;
               label31: {
                  Option var10000 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.partitioner();
                  Some var1 = new Some(this.partitioner$6);
                  if (var10000 == null) {
                     if (var1 == null) {
                        break label31;
                     }
                  } else if (var10000.equals(var1)) {
                     break label31;
                  }

                  var2 = new ShuffledRDD(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.partitioner$6, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt);
                  return (RDD<Tuple2<K, V>>)var2;
               }

               var2 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self;
               return (RDD<Tuple2<K, V>>)var2;
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.partitioner$6 = partitioner$6;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<V, W>>> join(RDD<Tuple2<K, W>> other, Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$6;
         private final Partitioner partitioner$7;

         public final RDD<Tuple2<K, Tuple2<V, W>>> apply() {
            return RDD$.MODULE$.rddToPairRDDFunctions(this.$outer.cogroup(this.other$6, this.partitioner$7), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, ClassTag$.MODULE$.apply(Tuple2.class), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).flatMapValues(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Iterator<Tuple2<V, W>> apply(Tuple2<Iterable<V>, Iterable<W>> pair) {
                  return ((IterableLike)pair._1()).iterator().<Tuple2<V, W>>flatMap(new Serializable(this, pair) {
                     public static final long serialVersionUID = 0L;
                     private final Tuple2 pair$1;

                     public final Iterator<Tuple2<V, W>> apply(V v) {
                        return ((IterableLike)this.pair$1._2()).iterator().<Tuple2<V, W>>map(new Serializable(this, v) {
                           public static final long serialVersionUID = 0L;
                           private final Object v$1;

                           public final Tuple2<V, W> apply(W w) {
                              return new Tuple2<V, W>(this.v$1, w);
                           }

                           public {
                              this.v$1 = v$1;
                           }
                        });
                     }

                     public {
                        this.pair$1 = pair$1;
                     }
                  });
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$6 = other$6;
               this.partitioner$7 = partitioner$7;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<V, Option<W>>>> leftOuterJoin(RDD<Tuple2<K, W>> other, Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$9;
         private final Partitioner partitioner$8;

         public final RDD<Tuple2<K, Tuple2<V, Option<W>>>> apply() {
            return RDD$.MODULE$.rddToPairRDDFunctions(this.$outer.cogroup(this.other$9, this.partitioner$8), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, ClassTag$.MODULE$.apply(Tuple2.class), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).flatMapValues(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final TraversableOnce<Tuple2<V, Option<W>>> apply(Tuple2<Iterable<V>, Iterable<W>> pair) {
                  return ((IterableLike)pair._2()).isEmpty() ? ((IterableLike)pair._1()).iterator().map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final Tuple2<V, None$> apply(V v) {
                        return new Tuple2<V, None$>(v, None$.MODULE$);
                     }
                  }) : ((IterableLike)pair._1()).iterator().flatMap(new Serializable(this, pair) {
                     public static final long serialVersionUID = 0L;
                     private final Tuple2 pair$2;

                     public final Iterator<Tuple2<V, Some<W>>> apply(V v) {
                        return ((IterableLike)this.pair$2._2()).iterator().<Tuple2<V, Some<W>>>map(new Serializable(this, v) {
                           public static final long serialVersionUID = 0L;
                           private final Object v$2;

                           public final Tuple2<V, Some<W>> apply(W w) {
                              return new Tuple2<V, Some<W>>(this.v$2, new Some(w));
                           }

                           public {
                              this.v$2 = v$2;
                           }
                        });
                     }

                     public {
                        this.pair$2 = pair$2;
                     }
                  });
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$9 = other$9;
               this.partitioner$8 = partitioner$8;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Option<V>, W>>> rightOuterJoin(RDD<Tuple2<K, W>> other, Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$12;
         private final Partitioner partitioner$9;

         public final RDD<Tuple2<K, Tuple2<Option<V>, W>>> apply() {
            return RDD$.MODULE$.rddToPairRDDFunctions(this.$outer.cogroup(this.other$12, this.partitioner$9), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, ClassTag$.MODULE$.apply(Tuple2.class), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).flatMapValues(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final TraversableOnce<Tuple2<Option<V>, W>> apply(Tuple2<Iterable<V>, Iterable<W>> pair) {
                  return ((IterableLike)pair._1()).isEmpty() ? ((IterableLike)pair._2()).iterator().map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final Tuple2<None$, W> apply(W w) {
                        return new Tuple2<None$, W>(None$.MODULE$, w);
                     }
                  }) : ((IterableLike)pair._1()).iterator().flatMap(new Serializable(this, pair) {
                     public static final long serialVersionUID = 0L;
                     private final Tuple2 pair$3;

                     public final Iterator<Tuple2<Some<V>, W>> apply(V v) {
                        return ((IterableLike)this.pair$3._2()).iterator().<Tuple2<Some<V>, W>>map(new Serializable(this, v) {
                           public static final long serialVersionUID = 0L;
                           private final Object v$3;

                           public final Tuple2<Some<V>, W> apply(W w) {
                              return new Tuple2<Some<V>, W>(new Some(this.v$3), w);
                           }

                           public {
                              this.v$3 = v$3;
                           }
                        });
                     }

                     public {
                        this.pair$3 = pair$3;
                     }
                  });
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$12 = other$12;
               this.partitioner$9 = partitioner$9;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Option<V>, Option<W>>>> fullOuterJoin(RDD<Tuple2<K, W>> other, Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$15;
         private final Partitioner partitioner$10;

         public final RDD<Tuple2<K, Tuple2<Option<V>, Option<W>>>> apply() {
            return RDD$.MODULE$.rddToPairRDDFunctions(this.$outer.cogroup(this.other$15, this.partitioner$10), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, ClassTag$.MODULE$.apply(Tuple2.class), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).flatMapValues(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final TraversableOnce<Tuple2<Option<V>, Option<W>>> apply(Tuple2<Iterable<V>, Iterable<W>> x0$1) {
                  Iterator var7;
                  if (x0$1 != null) {
                     Iterable vs = (Iterable)x0$1._1();
                     Iterable var4 = (Iterable)x0$1._2();
                     if (var4 instanceof Seq) {
                        Seq var5 = (Seq)var4;
                        Some var6 = Seq$.MODULE$.unapplySeq(var5);
                        if (!var6.isEmpty() && var6.get() != null && ((SeqLike)var6.get()).lengthCompare(0) == 0) {
                           var7 = vs.iterator().map(new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final Tuple2<Some<V>, None$> apply(V v) {
                                 return new Tuple2<Some<V>, None$>(new Some(v), None$.MODULE$);
                              }
                           });
                           return var7;
                        }
                     }
                  }

                  if (x0$1 != null) {
                     Iterable var8 = (Iterable)x0$1._1();
                     Iterable ws = (Iterable)x0$1._2();
                     if (var8 instanceof Seq) {
                        Seq var10 = (Seq)var8;
                        Some var11 = Seq$.MODULE$.unapplySeq(var10);
                        if (!var11.isEmpty() && var11.get() != null && ((SeqLike)var11.get()).lengthCompare(0) == 0) {
                           var7 = ws.iterator().map(new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final Tuple2<None$, Some<W>> apply(W w) {
                                 return new Tuple2<None$, Some<W>>(None$.MODULE$, new Some(w));
                              }
                           });
                           return var7;
                        }
                     }
                  }

                  if (x0$1 == null) {
                     throw new MatchError(x0$1);
                  } else {
                     Iterable vs = (Iterable)x0$1._1();
                     Iterable ws = (Iterable)x0$1._2();
                     var7 = vs.iterator().flatMap(new Serializable(this, ws) {
                        public static final long serialVersionUID = 0L;
                        private final Iterable ws$1;

                        public final Iterator<Tuple2<Some<V>, Some<W>>> apply(V v) {
                           return this.ws$1.iterator().<Tuple2<Some<V>, Some<W>>>map(new Serializable(this, v) {
                              public static final long serialVersionUID = 0L;
                              private final Object v$4;

                              public final Tuple2<Some<V>, Some<W>> apply(W w) {
                                 return new Tuple2<Some<V>, Some<W>>(new Some(this.v$4), new Some(w));
                              }

                              public {
                                 this.v$4 = v$4;
                              }
                           });
                        }

                        public {
                           this.ws$1 = ws$1;
                        }
                     });
                     return var7;
                  }
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$15 = other$15;
               this.partitioner$10 = partitioner$10;
            }
         }
      });
   }

   public <C> RDD<Tuple2<K, C>> combineByKey(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, createCombiner, mergeValue, mergeCombiners) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function1 createCombiner$6;
         private final Function2 mergeValue$6;
         private final Function2 mergeCombiners$6;

         public final RDD<Tuple2<K, C>> apply() {
            return this.$outer.combineByKeyWithClassTag(this.createCombiner$6, this.mergeValue$6, this.mergeCombiners$6, (ClassTag)null);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.createCombiner$6 = createCombiner$6;
               this.mergeValue$6 = mergeValue$6;
               this.mergeCombiners$6 = mergeCombiners$6;
            }
         }
      });
   }

   public <C> boolean combineByKey$default$5() {
      return true;
   }

   public <C> Serializer combineByKey$default$6() {
      return null;
   }

   @Experimental
   public <C> RDD<Tuple2<K, C>> combineByKeyWithClassTag(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners, ClassTag<C> ct) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, createCombiner, mergeValue, mergeCombiners, ct) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function1 createCombiner$1;
         private final Function2 mergeValue$1;
         private final Function2 mergeCombiners$1;
         private final ClassTag ct$1;

         public final RDD<Tuple2<K, C>> apply() {
            return this.$outer.combineByKeyWithClassTag(this.createCombiner$1, this.mergeValue$1, this.mergeCombiners$1, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[0])), this.$outer.combineByKeyWithClassTag$default$5(), this.$outer.combineByKeyWithClassTag$default$6(), this.ct$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.createCombiner$1 = createCombiner$1;
               this.mergeValue$1 = mergeValue$1;
               this.mergeCombiners$1 = mergeCombiners$1;
               this.ct$1 = ct$1;
            }
         }
      });
   }

   public <C> boolean combineByKeyWithClassTag$default$5() {
      return true;
   }

   public <C> Serializer combineByKeyWithClassTag$default$6() {
      return null;
   }

   public RDD<Tuple2<K, Iterable<V>>> groupByKey() {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;

         public final RDD<Tuple2<K, Iterable<V>>> apply() {
            return this.$outer.groupByKey(Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[0])));
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

   public <W> RDD<Tuple2<K, Tuple2<V, W>>> join(RDD<Tuple2<K, W>> other) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$5;

         public final RDD<Tuple2<K, Tuple2<V, W>>> apply() {
            return this.$outer.join(this.other$5, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other$5})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$5 = other$5;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<V, W>>> join(RDD<Tuple2<K, W>> other, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$4;
         private final int numPartitions$7;

         public final RDD<Tuple2<K, Tuple2<V, W>>> apply() {
            return this.$outer.join(this.other$4, new HashPartitioner(this.numPartitions$7));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$4 = other$4;
               this.numPartitions$7 = numPartitions$7;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<V, Option<W>>>> leftOuterJoin(RDD<Tuple2<K, W>> other) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$8;

         public final RDD<Tuple2<K, Tuple2<V, Option<W>>>> apply() {
            return this.$outer.leftOuterJoin(this.other$8, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other$8})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$8 = other$8;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<V, Option<W>>>> leftOuterJoin(RDD<Tuple2<K, W>> other, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$7;
         private final int numPartitions$8;

         public final RDD<Tuple2<K, Tuple2<V, Option<W>>>> apply() {
            return this.$outer.leftOuterJoin(this.other$7, new HashPartitioner(this.numPartitions$8));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$7 = other$7;
               this.numPartitions$8 = numPartitions$8;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Option<V>, W>>> rightOuterJoin(RDD<Tuple2<K, W>> other) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$11;

         public final RDD<Tuple2<K, Tuple2<Option<V>, W>>> apply() {
            return this.$outer.rightOuterJoin(this.other$11, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other$11})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$11 = other$11;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Option<V>, W>>> rightOuterJoin(RDD<Tuple2<K, W>> other, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$10;
         private final int numPartitions$9;

         public final RDD<Tuple2<K, Tuple2<Option<V>, W>>> apply() {
            return this.$outer.rightOuterJoin(this.other$10, new HashPartitioner(this.numPartitions$9));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$10 = other$10;
               this.numPartitions$9 = numPartitions$9;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Option<V>, Option<W>>>> fullOuterJoin(RDD<Tuple2<K, W>> other) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$14;

         public final RDD<Tuple2<K, Tuple2<Option<V>, Option<W>>>> apply() {
            return this.$outer.fullOuterJoin(this.other$14, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other$14})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$14 = other$14;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Option<V>, Option<W>>>> fullOuterJoin(RDD<Tuple2<K, W>> other, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$13;
         private final int numPartitions$10;

         public final RDD<Tuple2<K, Tuple2<Option<V>, Option<W>>>> apply() {
            return this.$outer.fullOuterJoin(this.other$13, new HashPartitioner(this.numPartitions$10));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$13 = other$13;
               this.numPartitions$10 = numPartitions$10;
            }
         }
      });
   }

   public Map<K, V> collectAsMap() {
      return (Map)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;

         public final scala.collection.mutable.HashMap<K, V> apply() {
            Tuple2[] data = (Tuple2[])this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.collect();
            scala.collection.mutable.HashMap map = new scala.collection.mutable.HashMap();
            map.sizeHint(data.length);
            Predef$.MODULE$.refArrayOps(data).foreach(new Serializable(this, map) {
               public static final long serialVersionUID = 0L;
               private final scala.collection.mutable.HashMap map$2;

               public final Option<V> apply(Tuple2<K, V> pair) {
                  return this.map$2.put(pair._1(), pair._2());
               }

               public {
                  this.map$2 = map$2;
               }
            });
            return map;
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

   public <U> RDD<Tuple2<K, U>> mapValues(Function1<V, U> f) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function1 f$1;

         public final MapPartitionsRDD<Tuple2<K, U>, Tuple2<K, V>> apply() {
            SparkContext qual$7 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.context();
            Function1 x$22 = this.f$1;
            boolean x$23 = qual$7.clean$default$2();
            Function1 cleanF = (Function1)qual$7.clean(x$22, x$23);
            return new MapPartitionsRDD<Tuple2<K, U>, Tuple2<K, V>>(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, new Serializable(this, cleanF) {
               public static final long serialVersionUID = 0L;
               public final Function1 cleanF$1;

               public final Iterator<Tuple2<K, U>> apply(TaskContext context, int pid, Iterator<Tuple2<K, V>> iter) {
                  return iter.<Tuple2<K, U>>map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final Tuple2<K, U> apply(Tuple2<K, V> x0$2) {
                        if (x0$2 != null) {
                           Object k = x0$2._1();
                           Object v = x0$2._2();
                           Tuple2 var5 = new Tuple2(k, this.$outer.cleanF$1.apply(v));
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
                  });
               }

               public {
                  this.cleanF$1 = cleanF$1;
               }
            }, true, ClassTag$.MODULE$.apply(Tuple2.class), ClassTag$.MODULE$.apply(Tuple2.class));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.f$1 = f$1;
            }
         }
      });
   }

   public <U> RDD<Tuple2<K, U>> flatMapValues(Function1<V, TraversableOnce<U>> f) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Function1 f$2;

         public final MapPartitionsRDD<Tuple2<K, U>, Tuple2<K, V>> apply() {
            SparkContext qual$8 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.context();
            Function1 x$24 = this.f$2;
            boolean x$25 = qual$8.clean$default$2();
            Function1 cleanF = (Function1)qual$8.clean(x$24, x$25);
            return new MapPartitionsRDD<Tuple2<K, U>, Tuple2<K, V>>(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, new Serializable(this, cleanF) {
               public static final long serialVersionUID = 0L;
               public final Function1 cleanF$2;

               public final Iterator<Tuple2<K, U>> apply(TaskContext context, int pid, Iterator<Tuple2<K, V>> iter) {
                  return iter.<Tuple2<K, U>>flatMap(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final GenTraversableOnce<Tuple2<K, U>> apply(Tuple2<K, V> x0$3) {
                        if (x0$3 != null) {
                           Object k = x0$3._1();
                           Object v = x0$3._2();
                           TraversableOnce var5 = TraversableOnce$.MODULE$.MonadOps((TraversableOnce)this.$outer.cleanF$2.apply(v)).map(new Serializable(this, k) {
                              public static final long serialVersionUID = 0L;
                              private final Object k$1;

                              public final Tuple2<K, U> apply(U x) {
                                 return new Tuple2<K, U>(this.k$1, x);
                              }

                              public {
                                 this.k$1 = k$1;
                              }
                           });
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
                  });
               }

               public {
                  this.cleanF$2 = cleanF$2;
               }
            }, true, ClassTag$.MODULE$.apply(Tuple2.class), ClassTag$.MODULE$.apply(Tuple2.class));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.f$2 = f$2;
            }
         }
      });
   }

   public <W1, W2, W3> RDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> cogroup(RDD<Tuple2<K, W1>> other1, RDD<Tuple2<K, W2>> other2, RDD<Tuple2<K, W3>> other3, Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other1, other2, other3, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other1$6;
         private final RDD other2$6;
         private final RDD other3$3;
         private final Partitioner partitioner$13;

         public final RDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> apply() {
            if (this.partitioner$13 instanceof HashPartitioner && this.$outer.keyClass().isArray()) {
               throw new SparkException("HashPartitioner cannot partition array keys.");
            } else {
               CoGroupedRDD cg = new CoGroupedRDD((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDD[]{this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.other1$6, this.other2$6, this.other3$3})), this.partitioner$13, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt);
               return RDD$.MODULE$.rddToPairRDDFunctions(cg, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Iterable.class)), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).mapValues(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>> apply(Iterable<?>[] x0$4) {
                     Option var3 = Array$.MODULE$.unapplySeq(x0$4);
                     if (!var3.isEmpty() && var3.get() != null && ((SeqLike)var3.get()).lengthCompare(4) == 0) {
                        Iterable vs = (Iterable)((SeqLike)var3.get()).apply(0);
                        Iterable w1s = (Iterable)((SeqLike)var3.get()).apply(1);
                        Iterable w2s = (Iterable)((SeqLike)var3.get()).apply(2);
                        Iterable w3s = (Iterable)((SeqLike)var3.get()).apply(3);
                        Tuple4 var8 = new Tuple4(vs, w1s, w2s, w3s);
                        return var8;
                     } else {
                        throw new MatchError(x0$4);
                     }
                  }
               });
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other1$6 = other1$6;
               this.other2$6 = other2$6;
               this.other3$3 = other3$3;
               this.partitioner$13 = partitioner$13;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> cogroup(RDD<Tuple2<K, W>> other, Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$18;
         private final Partitioner partitioner$12;

         public final RDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> apply() {
            if (this.partitioner$12 instanceof HashPartitioner && this.$outer.keyClass().isArray()) {
               throw new SparkException("HashPartitioner cannot partition array keys.");
            } else {
               CoGroupedRDD cg = new CoGroupedRDD((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDD[]{this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.other$18})), this.partitioner$12, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt);
               return RDD$.MODULE$.rddToPairRDDFunctions(cg, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Iterable.class)), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).mapValues(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Tuple2<Iterable<V>, Iterable<W>> apply(Iterable<?>[] x0$5) {
                     Option var3 = Array$.MODULE$.unapplySeq(x0$5);
                     if (!var3.isEmpty() && var3.get() != null && ((SeqLike)var3.get()).lengthCompare(2) == 0) {
                        Iterable vs = (Iterable)((SeqLike)var3.get()).apply(0);
                        Iterable w1s = (Iterable)((SeqLike)var3.get()).apply(1);
                        Tuple2 var6 = new Tuple2(vs, w1s);
                        return var6;
                     } else {
                        throw new MatchError(x0$5);
                     }
                  }
               });
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$18 = other$18;
               this.partitioner$12 = partitioner$12;
            }
         }
      });
   }

   public <W1, W2> RDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> cogroup(RDD<Tuple2<K, W1>> other1, RDD<Tuple2<K, W2>> other2, Partitioner partitioner) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other1, other2, partitioner) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other1$5;
         private final RDD other2$5;
         private final Partitioner partitioner$11;

         public final RDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> apply() {
            if (this.partitioner$11 instanceof HashPartitioner && this.$outer.keyClass().isArray()) {
               throw new SparkException("HashPartitioner cannot partition array keys.");
            } else {
               CoGroupedRDD cg = new CoGroupedRDD((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDD[]{this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.other1$5, this.other2$5})), this.partitioner$11, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt);
               return RDD$.MODULE$.rddToPairRDDFunctions(cg, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Iterable.class)), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$ord).mapValues(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>> apply(Iterable<?>[] x0$6) {
                     Option var3 = Array$.MODULE$.unapplySeq(x0$6);
                     if (!var3.isEmpty() && var3.get() != null && ((SeqLike)var3.get()).lengthCompare(3) == 0) {
                        Iterable vs = (Iterable)((SeqLike)var3.get()).apply(0);
                        Iterable w1s = (Iterable)((SeqLike)var3.get()).apply(1);
                        Iterable w2s = (Iterable)((SeqLike)var3.get()).apply(2);
                        Tuple3 var7 = new Tuple3(vs, w1s, w2s);
                        return var7;
                     } else {
                        throw new MatchError(x0$6);
                     }
                  }
               });
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other1$5 = other1$5;
               this.other2$5 = other2$5;
               this.partitioner$11 = partitioner$11;
            }
         }
      });
   }

   public <W1, W2, W3> RDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> cogroup(RDD<Tuple2<K, W1>> other1, RDD<Tuple2<K, W2>> other2, RDD<Tuple2<K, W3>> other3) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other1, other2, other3) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other1$4;
         private final RDD other2$4;
         private final RDD other3$2;

         public final RDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> apply() {
            return this.$outer.cogroup(this.other1$4, this.other2$4, this.other3$2, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other1$4, this.other2$4, this.other3$2})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other1$4 = other1$4;
               this.other2$4 = other2$4;
               this.other3$2 = other3$2;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> cogroup(RDD<Tuple2<K, W>> other) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$17;

         public final RDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> apply() {
            return this.$outer.cogroup(this.other$17, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other$17})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$17 = other$17;
            }
         }
      });
   }

   public <W1, W2> RDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> cogroup(RDD<Tuple2<K, W1>> other1, RDD<Tuple2<K, W2>> other2) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other1, other2) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other1$3;
         private final RDD other2$3;

         public final RDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> apply() {
            return this.$outer.cogroup(this.other1$3, this.other2$3, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other1$3, this.other2$3})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other1$3 = other1$3;
               this.other2$3 = other2$3;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> cogroup(RDD<Tuple2<K, W>> other, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$16;
         private final int numPartitions$13;

         public final RDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> apply() {
            return this.$outer.cogroup(this.other$16, new HashPartitioner(this.numPartitions$13));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$16 = other$16;
               this.numPartitions$13 = numPartitions$13;
            }
         }
      });
   }

   public <W1, W2> RDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> cogroup(RDD<Tuple2<K, W1>> other1, RDD<Tuple2<K, W2>> other2, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other1, other2, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other1$2;
         private final RDD other2$2;
         private final int numPartitions$12;

         public final RDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> apply() {
            return this.$outer.cogroup(this.other1$2, this.other2$2, new HashPartitioner(this.numPartitions$12));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other1$2 = other1$2;
               this.other2$2 = other2$2;
               this.numPartitions$12 = numPartitions$12;
            }
         }
      });
   }

   public <W1, W2, W3> RDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> cogroup(RDD<Tuple2<K, W1>> other1, RDD<Tuple2<K, W2>> other2, RDD<Tuple2<K, W3>> other3, int numPartitions) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other1, other2, other3, numPartitions) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other1$1;
         private final RDD other2$1;
         private final RDD other3$1;
         private final int numPartitions$11;

         public final RDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> apply() {
            return this.$outer.cogroup(this.other1$1, this.other2$1, this.other3$1, new HashPartitioner(this.numPartitions$11));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other1$1 = other1$1;
               this.other2$1 = other2$1;
               this.other3$1 = other3$1;
               this.numPartitions$11 = numPartitions$11;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> groupWith(RDD<Tuple2<K, W>> other) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$19;

         public final RDD<Tuple2<K, Tuple2<Iterable<V>, Iterable<W>>>> apply() {
            return this.$outer.cogroup(this.other$19, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other$19})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$19 = other$19;
            }
         }
      });
   }

   public <W1, W2> RDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> groupWith(RDD<Tuple2<K, W1>> other1, RDD<Tuple2<K, W2>> other2) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other1, other2) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other1$8;
         private final RDD other2$8;

         public final RDD<Tuple2<K, Tuple3<Iterable<V>, Iterable<W1>, Iterable<W2>>>> apply() {
            return this.$outer.cogroup(this.other1$8, this.other2$8, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other1$8, this.other2$8})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other1$8 = other1$8;
               this.other2$8 = other2$8;
            }
         }
      });
   }

   public <W1, W2, W3> RDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> groupWith(RDD<Tuple2<K, W1>> other1, RDD<Tuple2<K, W2>> other2, RDD<Tuple2<K, W3>> other3) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other1, other2, other3) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other1$7;
         private final RDD other2$7;
         private final RDD other3$4;

         public final RDD<Tuple2<K, Tuple4<Iterable<V>, Iterable<W1>, Iterable<W2>, Iterable<W3>>>> apply() {
            return this.$outer.cogroup(this.other1$7, this.other2$7, this.other3$4, Partitioner$.MODULE$.defaultPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, Predef$.MODULE$.wrapRefArray(new RDD[]{this.other1$7, this.other2$7, this.other3$4})));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other1$7 = other1$7;
               this.other2$7 = other2$7;
               this.other3$4 = other3$4;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, V>> subtractByKey(RDD<Tuple2<K, W>> other, ClassTag<W> evidence$4) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, evidence$4) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$3;
         private final ClassTag evidence$4$1;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.subtractByKey(this.other$3, (Partitioner)this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.partitioner().getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final HashPartitioner apply() {
                  return new HashPartitioner(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$anonfun$$$outer().org$apache$spark$rdd$PairRDDFunctions$$self.partitions().length);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }), this.evidence$4$1);
         }

         // $FF: synthetic method
         public PairRDDFunctions org$apache$spark$rdd$PairRDDFunctions$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$3 = other$3;
               this.evidence$4$1 = evidence$4$1;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, V>> subtractByKey(RDD<Tuple2<K, W>> other, int numPartitions, ClassTag<W> evidence$5) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, numPartitions, evidence$5) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$2;
         private final int numPartitions$6;
         private final ClassTag evidence$5$1;

         public final RDD<Tuple2<K, V>> apply() {
            return this.$outer.subtractByKey(this.other$2, new HashPartitioner(this.numPartitions$6), this.evidence$5$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$2 = other$2;
               this.numPartitions$6 = numPartitions$6;
               this.evidence$5$1 = evidence$5$1;
            }
         }
      });
   }

   public <W> RDD<Tuple2<K, V>> subtractByKey(RDD<Tuple2<K, W>> other, Partitioner p, ClassTag<W> evidence$6) {
      return (RDD)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, other, p, evidence$6) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final RDD other$1;
         private final Partitioner p$1;
         private final ClassTag evidence$6$1;

         public final SubtractedRDD<K, V, W> apply() {
            return new SubtractedRDD<K, V, W>(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, this.other$1, this.p$1, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$kt, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt, this.evidence$6$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.other$1 = other$1;
               this.p$1 = p$1;
               this.evidence$6$1 = evidence$6$1;
            }
         }
      });
   }

   public Seq<V> lookup(K key) {
      return (Seq)this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, key) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         public final Object key$1;

         public final Seq<V> apply() {
            Option var1 = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.partitioner();
            Object var4;
            if (var1 instanceof Some) {
               Some var2 = (Some)var1;
               Partitioner p = (Partitioner)var2.x();
               int index = p.getPartition(this.key$1);
               Function1 process = new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final Seq<V> apply(Iterator<Tuple2<K, V>> it) {
                     ArrayBuffer buf = new ArrayBuffer();
                     it.withFilter(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final boolean apply(Tuple2<K, V> pair) {
                           return BoxesRunTime.equals(pair._1(), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$anonfun$$anonfun$$$outer().key$1);
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     }).foreach(new Serializable(this, buf) {
                        public static final long serialVersionUID = 0L;
                        private final ArrayBuffer buf$1;

                        public final ArrayBuffer<V> apply(Tuple2<K, V> pair) {
                           return this.buf$1.$plus$eq(pair._2());
                        }

                        public {
                           this.buf$1 = buf$1;
                        }
                     });
                     return buf;
                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$rdd$PairRDDFunctions$$anonfun$$anonfun$$$outer() {
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
               Seq[] res = (Seq[])this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.context().runJob(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, process, Predef$.MODULE$.wrapIntArray(new int[]{index}), ClassTag$.MODULE$.apply(Seq.class));
               var4 = res[0];
            } else {
               if (!None$.MODULE$.equals(var1)) {
                  throw new MatchError(var1);
               }

               var4 = Predef$.MODULE$.genericWrapArray(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.filter(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final boolean apply(Tuple2<K, V> x$6) {
                     return BoxesRunTime.equals(x$6._1(), this.$outer.key$1);
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

                  public final V apply(Tuple2<K, V> x$7) {
                     return (V)x$7._2();
                  }
               }, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt).collect());
            }

            return (Seq<V>)var4;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.key$1 = key$1;
            }
         }
      });
   }

   public <F extends OutputFormat<K, V>> void saveAsHadoopFile(String path, ClassTag<F> fm) {
      this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, path, fm) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final String path$4;
         private final ClassTag fm$2;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.saveAsHadoopFile(this.path$4, this.$outer.keyClass(), this.$outer.valueClass(), this.fm$2.runtimeClass(), this.$outer.saveAsHadoopFile$default$5(), this.$outer.saveAsHadoopFile$default$6());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$4 = path$4;
               this.fm$2 = fm$2;
            }
         }
      });
   }

   public <F extends OutputFormat<K, V>> void saveAsHadoopFile(String path, Class<? extends CompressionCodec> codec, ClassTag<F> fm) {
      this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, path, codec, fm) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final String path$3;
         private final Class codec$3;
         private final ClassTag fm$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            Class runtimeClass = this.fm$1.runtimeClass();
            this.$outer.saveAsHadoopFile(this.path$3, this.$outer.keyClass(), this.$outer.valueClass(), runtimeClass, this.codec$3);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$3 = path$3;
               this.codec$3 = codec$3;
               this.fm$1 = fm$1;
            }
         }
      });
   }

   public <F extends org.apache.hadoop.mapreduce.OutputFormat<K, V>> void saveAsNewAPIHadoopFile(String path, ClassTag<F> fm) {
      this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, path, fm) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final String path$6;
         private final ClassTag fm$3;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.saveAsNewAPIHadoopFile(this.path$6, this.$outer.keyClass(), this.$outer.valueClass(), this.fm$3.runtimeClass(), this.$outer.saveAsNewAPIHadoopFile$default$5());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$6 = path$6;
               this.fm$3 = fm$3;
            }
         }
      });
   }

   public void saveAsNewAPIHadoopFile(String path, Class<?> keyClass, Class<?> valueClass, Class<? extends org.apache.hadoop.mapreduce.OutputFormat<?, ?>> outputFormatClass, Configuration conf) {
      this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, path, keyClass, valueClass, outputFormatClass, conf) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final String path$5;
         private final Class keyClass$3;
         private final Class valueClass$3;
         private final Class outputFormatClass$3;
         private final Configuration conf$2;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            Configuration hadoopConf = this.conf$2;
            Job job = Job.getInstance(hadoopConf);
            job.setOutputKeyClass(this.keyClass$3);
            job.setOutputValueClass(this.valueClass$3);
            job.setOutputFormatClass(this.outputFormatClass$3);
            Configuration jobConfiguration = job.getConfiguration();
            jobConfiguration.set("mapreduce.output.fileoutputformat.outputdir", this.path$5);
            this.$outer.saveAsNewAPIHadoopDataset(jobConfiguration);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$5 = path$5;
               this.keyClass$3 = keyClass$3;
               this.valueClass$3 = valueClass$3;
               this.outputFormatClass$3 = outputFormatClass$3;
               this.conf$2 = conf$2;
            }
         }
      });
   }

   public Configuration saveAsNewAPIHadoopFile$default$5() {
      return this.org$apache$spark$rdd$PairRDDFunctions$$self.context().hadoopConfiguration();
   }

   public void saveAsHadoopFile(String path, Class<?> keyClass, Class<?> valueClass, Class<? extends OutputFormat<?, ?>> outputFormatClass, Class<? extends CompressionCodec> codec) {
      this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, path, keyClass, valueClass, outputFormatClass, codec) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final String path$2;
         private final Class keyClass$2;
         private final Class valueClass$2;
         private final Class outputFormatClass$2;
         private final Class codec$2;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.saveAsHadoopFile(this.path$2, this.keyClass$2, this.valueClass$2, this.outputFormatClass$2, new JobConf(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.context().hadoopConfiguration()), new Some(this.codec$2));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$2 = path$2;
               this.keyClass$2 = keyClass$2;
               this.valueClass$2 = valueClass$2;
               this.outputFormatClass$2 = outputFormatClass$2;
               this.codec$2 = codec$2;
            }
         }
      });
   }

   public void saveAsHadoopFile(String path, Class<?> keyClass, Class<?> valueClass, Class<? extends OutputFormat<?, ?>> outputFormatClass, JobConf conf, Option<Class<? extends CompressionCodec>> codec) {
      this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, path, keyClass, valueClass, outputFormatClass, conf, codec) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final String path$1;
         private final Class keyClass$1;
         private final Class valueClass$1;
         private final Class outputFormatClass$1;
         private final JobConf conf$1;
         private final Option codec$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            JobConf hadoopConf = this.conf$1;
            hadoopConf.setOutputKeyClass(this.keyClass$1);
            hadoopConf.setOutputValueClass(this.valueClass$1);
            this.conf$1.setOutputFormat(this.outputFormatClass$1);
            this.codec$1.foreach(new Serializable(this, hadoopConf) {
               public static final long serialVersionUID = 0L;
               private final JobConf hadoopConf$1;

               public final void apply(Class<? extends CompressionCodec> c) {
                  this.hadoopConf$1.setCompressMapOutput(true);
                  this.hadoopConf$1.set("mapreduce.output.fileoutputformat.compress", "true");
                  this.hadoopConf$1.setMapOutputCompressorClass(c);
                  this.hadoopConf$1.set("mapreduce.output.fileoutputformat.compress.codec", c.getCanonicalName());
                  this.hadoopConf$1.set("mapreduce.output.fileoutputformat.compress.type", CompressionType.BLOCK.toString());
               }

               public {
                  this.hadoopConf$1 = hadoopConf$1;
               }
            });
            if (this.conf$1.getOutputCommitter() == null) {
               hadoopConf.setOutputCommitter(FileOutputCommitter.class);
            }

            boolean speculationEnabled = this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self.conf().getBoolean("spark.speculation", false);
            String outputCommitterClass = hadoopConf.get("mapred.output.committer.class", "");
            if (speculationEnabled && outputCommitterClass.contains("Direct")) {
               String warningMessage = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " may be an output committer that writes data directly to "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{outputCommitterClass}))).append((Object)"the final location. Because speculation is enabled, this output committer may ").append((Object)"cause data loss (see the case in SPARK-10063). If possible, please use an output ").append((Object)"committer that does not have this behavior (e.g. FileOutputCommitter).").toString();
               this.$outer.logWarning(new Serializable(this, warningMessage) {
                  public static final long serialVersionUID = 0L;
                  private final String warningMessage$1;

                  public final String apply() {
                     return this.warningMessage$1;
                  }

                  public {
                     this.warningMessage$1 = warningMessage$1;
                  }
               });
            }

            FileOutputFormat.setOutputPath(hadoopConf, SparkHadoopWriterUtils$.MODULE$.createPathFromString(this.path$1, hadoopConf));
            this.$outer.saveAsHadoopDataset(hadoopConf);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.path$1 = path$1;
               this.keyClass$1 = keyClass$1;
               this.valueClass$1 = valueClass$1;
               this.outputFormatClass$1 = outputFormatClass$1;
               this.conf$1 = conf$1;
               this.codec$1 = codec$1;
            }
         }
      });
   }

   public JobConf saveAsHadoopFile$default$5() {
      return new JobConf(this.org$apache$spark$rdd$PairRDDFunctions$$self.context().hadoopConfiguration());
   }

   public Option<Class<? extends CompressionCodec>> saveAsHadoopFile$default$6() {
      return None$.MODULE$;
   }

   public void saveAsNewAPIHadoopDataset(Configuration conf) {
      this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, conf) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final Configuration conf$3;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            HadoopMapReduceWriteConfigUtil config = new HadoopMapReduceWriteConfigUtil(new SerializableConfiguration(this.conf$3), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt);
            SparkHadoopWriter$.MODULE$.write(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, config, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.conf$3 = conf$3;
            }
         }
      });
   }

   public void saveAsHadoopDataset(JobConf conf) {
      this.org$apache$spark$rdd$PairRDDFunctions$$self.withScope(new Serializable(this, conf) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PairRDDFunctions $outer;
         private final JobConf conf$4;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            HadoopMapRedWriteConfigUtil config = new HadoopMapRedWriteConfigUtil(new SerializableJobConf(this.conf$4), this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt);
            SparkHadoopWriter$.MODULE$.write(this.$outer.org$apache$spark$rdd$PairRDDFunctions$$self, config, this.$outer.org$apache$spark$rdd$PairRDDFunctions$$vt);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.conf$4 = conf$4;
            }
         }
      });
   }

   public RDD<K> keys() {
      return this.org$apache$spark$rdd$PairRDDFunctions$$self.<K>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final K apply(Tuple2<K, V> x$8) {
            return (K)x$8._1();
         }
      }, this.org$apache$spark$rdd$PairRDDFunctions$$kt);
   }

   public RDD<V> values() {
      return this.org$apache$spark$rdd$PairRDDFunctions$$self.<V>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final V apply(Tuple2<K, V> x$9) {
            return (V)x$9._2();
         }
      }, this.org$apache$spark$rdd$PairRDDFunctions$$vt);
   }

   public Class<?> keyClass() {
      return this.org$apache$spark$rdd$PairRDDFunctions$$kt.runtimeClass();
   }

   public Class<?> valueClass() {
      return this.org$apache$spark$rdd$PairRDDFunctions$$vt.runtimeClass();
   }

   public Option<Ordering<K>> keyOrdering() {
      return Option$.MODULE$.<Ordering<K>>apply(this.org$apache$spark$rdd$PairRDDFunctions$$ord);
   }

   public PairRDDFunctions(RDD<Tuple2<K, V>> self, ClassTag<K> kt, ClassTag<V> vt, Ordering<K> ord) {
      this.org$apache$spark$rdd$PairRDDFunctions$$self = self;
      this.org$apache$spark$rdd$PairRDDFunctions$$kt = kt;
      this.org$apache$spark$rdd$PairRDDFunctions$$vt = vt;
      this.org$apache$spark$rdd$PairRDDFunctions$$ord = ord;
      Logging$class.$init$(this);
   }
}
