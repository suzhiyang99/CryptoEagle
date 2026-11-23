package org.apache.spark.rdd;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.task.JobContextImpl;
import org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl;
import org.apache.spark.InterruptibleIterator;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.executor.InputMetrics;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.SerializableConfiguration;
import org.apache.spark.util.ShutdownHookManager$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.None$;
import scala.Option;
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
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.StringBuilder;
import scala.collection.mutable.WrappedArray;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.RichInt$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\t\u0015g\u0001B\u0001\u0003\u0001-\u0011ABT3x\u0011\u0006$wn\u001c9S\t\u0012S!a\u0001\u0003\u0002\u0007I$GM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001)2\u0001D\r$'\r\u0001Q\"\n\t\u0004\u001d=\tR\"\u0001\u0002\n\u0005A\u0011!a\u0001*E\tB!!#F\f#\u001b\u0005\u0019\"\"\u0001\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Y\u0019\"A\u0002+va2,'\u0007\u0005\u0002\u001931\u0001A!\u0002\u000e\u0001\u0005\u0004Y\"!A&\u0012\u0005qy\u0002C\u0001\n\u001e\u0013\tq2CA\u0004O_RD\u0017N\\4\u0011\u0005I\u0001\u0013BA\u0011\u0014\u0005\r\te.\u001f\t\u00031\r\"Q\u0001\n\u0001C\u0002m\u0011\u0011A\u0016\t\u0003M%j\u0011a\n\u0006\u0003Q\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003U\u001d\u0012q\u0001T8hO&tw\r\u0003\u0005-\u0001\t\u0005\t\u0015!\u0003.\u0003\t\u00198\r\u0005\u0002/_5\tA!\u0003\u00021\t\ta1\u000b]1sW\u000e{g\u000e^3yi\"A!\u0007\u0001B\u0001B\u0003%1'\u0001\tj]B,HOR8s[\u0006$8\t\\1tgB\u0012A\u0007\u0010\t\u0004kaZdB\u0001\n7\u0013\t94#\u0001\u0004Qe\u0016$WMZ\u0005\u0003si\u0012Qa\u00117bgNT!aN\n\u0011\u0005aaD!C\u001f2\u0003\u0003\u0005\tQ!\u0001?\u0005\ryF%M\t\u00039}\u0002B\u0001Q#\u0018E5\t\u0011I\u0003\u0002C\u0007\u0006IQ.\u00199sK\u0012,8-\u001a\u0006\u0003\t\u001a\ta\u0001[1e_>\u0004\u0018B\u0001$B\u0005-Ie\u000e];u\r>\u0014X.\u0019;\t\u0011!\u0003!\u0011!Q\u0001\n%\u000b\u0001b[3z\u00072\f7o\u001d\t\u0004ka:\u0002\u0002C&\u0001\u0005\u0003\u0005\u000b\u0011\u0002'\u0002\u0015Y\fG.^3DY\u0006\u001c8\u000fE\u00026q\tB\u0001B\u0014\u0001\u0003\u0006\u0004%IaT\u0001\u0006?\u000e|gNZ\u000b\u0002!B\u0011\u0011\u000bV\u0007\u0002%*\u00111kQ\u0001\u0005G>tg-\u0003\u0002V%\ni1i\u001c8gS\u001e,(/\u0019;j_:D\u0001b\u0016\u0001\u0003\u0002\u0003\u0006I\u0001U\u0001\u0007?\u000e|gN\u001a\u0011)\u0005YK\u0006C\u0001\n[\u0013\tY6CA\u0005ue\u0006t7/[3oi\")Q\f\u0001C\u0001=\u00061A(\u001b8jiz\"ba\u00181bM\u001eD\u0007\u0003\u0002\b\u0001/\tBQ\u0001\f/A\u00025BQA\r/A\u0002\t\u0004$aY3\u0011\u0007UBD\r\u0005\u0002\u0019K\u0012IQ(YA\u0001\u0002\u0003\u0015\tA\u0010\u0005\u0006\u0011r\u0003\r!\u0013\u0005\u0006\u0017r\u0003\r\u0001\u0014\u0005\u0006\u001dr\u0003\r\u0001\u0015\u0005\bU\u0002\u0011\r\u0011\"\u0003l\u00035\u0019wN\u001c4Ce>\fGmY1tiV\tA\u000eE\u0002naJl\u0011A\u001c\u0006\u0003_\u0012\t\u0011B\u0019:pC\u0012\u001c\u0017m\u001d;\n\u0005Et'!\u0003\"s_\u0006$7-Y:u!\t\u0019h/D\u0001u\u0015\t)H!\u0001\u0003vi&d\u0017BA<u\u0005e\u0019VM]5bY&T\u0018M\u00197f\u0007>tg-[4ve\u0006$\u0018n\u001c8\t\re\u0004\u0001\u0015!\u0003m\u00039\u0019wN\u001c4Ce>\fGmY1ti\u0002Bqa\u001f\u0001C\u0002\u0013%A0\u0001\u0007k_\n$&/Y2lKJLE-F\u0001~!\t)d0\u0003\u0002\u0000u\t11\u000b\u001e:j]\u001eDq!a\u0001\u0001A\u0003%Q0A\u0007k_\n$&/Y2lKJLE\r\t\u0005\n\u0003\u000f\u0001!\u0019!C\t\u0003\u0013\tQA[8c\u0013\u0012,\"!a\u0003\u0011\u0007\u0001\u000bi!C\u0002\u0002\u0010\u0005\u0013QAS8c\u0013\u0012C\u0001\"a\u0005\u0001A\u0003%\u00111B\u0001\u0007U>\u0014\u0017\n\u001a\u0011)\u0007\u0005E\u0011\fC\u0005\u0002\u001a\u0001\u0011\r\u0011\"\u0003\u0002\u001c\u0005\u00112\u000f[8vY\u0012\u001cEn\u001c8f\u0015>\u00147i\u001c8g+\t\ti\u0002E\u0002\u0013\u0003?I1!!\t\u0014\u0005\u001d\u0011un\u001c7fC:D\u0001\"!\n\u0001A\u0003%\u0011QD\u0001\u0014g\"|W\u000f\u001c3DY>tWMS8c\u0007>tg\r\t\u0005\n\u0003S\u0001!\u0019!C\u0005\u00037\t!#[4o_J,7i\u001c:skB$h)\u001b7fg\"A\u0011Q\u0006\u0001!\u0002\u0013\ti\"A\njO:|'/Z\"peJ,\b\u000f\u001e$jY\u0016\u001c\b\u0005C\u0005\u00022\u0001\u0011\r\u0011\"\u0003\u0002\u001c\u0005\t\u0012n\u001a8pe\u0016,U\u000e\u001d;z'Bd\u0017\u000e^:\t\u0011\u0005U\u0002\u0001)A\u0005\u0003;\t!#[4o_J,W)\u001c9usN\u0003H.\u001b;tA!1\u0011\u0011\b\u0001\u0005\u0002=\u000bqaZ3u\u0007>tg\rC\u0004\u0002>\u0001!\t%a\u0010\u0002\u001b\u001d,G\u000fU1si&$\u0018n\u001c8t+\t\t\t\u0005E\u0003\u0013\u0003\u0007\n9%C\u0002\u0002FM\u0011Q!\u0011:sCf\u00042ALA%\u0013\r\tY\u0005\u0002\u0002\n!\u0006\u0014H/\u001b;j_:Dq!a\u0014\u0001\t\u0003\n\t&A\u0004d_6\u0004X\u000f^3\u0015\r\u0005M\u0013\u0011LA/!\u0011q\u0013QK\t\n\u0007\u0005]CAA\u000bJ]R,'O];qi&\u0014G.Z%uKJ\fGo\u001c:\t\u0011\u0005m\u0013Q\na\u0001\u0003\u000f\n\u0001\u0002\u001e5f'Bd\u0017\u000e\u001e\u0005\t\u0003?\ni\u00051\u0001\u0002b\u000591m\u001c8uKb$\bc\u0001\u0018\u0002d%\u0019\u0011Q\r\u0003\u0003\u0017Q\u000b7o[\"p]R,\u0007\u0010\u001e\u0005\b\u0003S\u0002A\u0011AA6\u0003mi\u0017\r\u001d)beRLG/[8og^KG\u000f[%oaV$8\u000b\u001d7jiV!\u0011QNA;)\u0019\ty'!#\u00024R!\u0011\u0011OA=!\u0011qq\"a\u001d\u0011\u0007a\t)\bB\u0004\u0002x\u0005\u001d$\u0019A\u000e\u0003\u0003UC!\"a\u001f\u0002h\u0005\u0005\t9AA?\u0003))g/\u001b3f]\u000e,G%\r\t\u0007\u0003\u007f\n))a\u001d\u000e\u0005\u0005\u0005%bAAB'\u00059!/\u001a4mK\u000e$\u0018\u0002BAD\u0003\u0003\u0013\u0001b\u00117bgN$\u0016m\u001a\u0005\t\u0003\u0017\u000b9\u00071\u0001\u0002\u000e\u0006\ta\rE\u0005\u0013\u0003\u001f\u000b\u0019*!'\u00022&\u0019\u0011\u0011S\n\u0003\u0013\u0019+hn\u0019;j_:\u0014\u0004c\u0001!\u0002\u0016&\u0019\u0011qS!\u0003\u0015%s\u0007/\u001e;Ta2LG\u000fE\u0003\u0002\u001c\u0006-\u0016C\u0004\u0003\u0002\u001e\u0006\u001df\u0002BAP\u0003Kk!!!)\u000b\u0007\u0005\r&\"\u0001\u0004=e>|GOP\u0005\u0002)%\u0019\u0011\u0011V\n\u0002\u000fA\f7m[1hK&!\u0011QVAX\u0005!IE/\u001a:bi>\u0014(bAAU'A1\u00111TAV\u0003gB!\"!.\u0002hA\u0005\t\u0019AA\u000f\u0003U\u0001(/Z:feZ,7\u000fU1si&$\u0018n\u001c8j]\u001eDC!a\u001a\u0002:B!\u00111XAa\u001b\t\tiLC\u0002\u0002@\u0012\t!\"\u00198o_R\fG/[8o\u0013\u0011\t\u0019-!0\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5\t\u000f\u0005\u001d\u0007\u0001\"\u0011\u0002J\u0006)r-\u001a;Qe\u00164WM\u001d:fI2{7-\u0019;j_:\u001cH\u0003BAf\u0003#\u0004R!a'\u0002NvLA!a4\u00020\n\u00191+Z9\t\u0011\u0005M\u0017Q\u0019a\u0001\u0003\u000f\na\u0001[:qY&$\bbBAl\u0001\u0011\u0005\u0013\u0011\\\u0001\ba\u0016\u00148/[:u)\u0011\tY.!8\u000e\u0003\u0001A\u0001\"a8\u0002V\u0002\u0007\u0011\u0011]\u0001\rgR|'/Y4f\u0019\u00164X\r\u001c\t\u0005\u0003G\fI/\u0004\u0002\u0002f*\u0019\u0011q\u001d\u0003\u0002\u000fM$xN]1hK&!\u00111^As\u00051\u0019Fo\u001c:bO\u0016dUM^3m\u0011%\ty\u000fAI\u0001\n\u0003\t\t0A\u0013nCB\u0004\u0016M\u001d;ji&|gn],ji\"Le\u000e];u'Bd\u0017\u000e\u001e\u0013eK\u001a\fW\u000f\u001c;%eU!\u00111\u001fB\u0004+\t\t)P\u000b\u0003\u0002\u001e\u0005]8FAA}!\u0011\tYPa\u0001\u000e\u0005\u0005u(\u0002BA\u0000\u0005\u0003\t\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\u0005}6#\u0003\u0003\u0003\u0006\u0005u(!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u00129\u0011qOAw\u0005\u0004Y\u0002f\u0001\u0001\u0002:\u001eA!Q\u0002\u0002\t\u0002\u0011\u0011y!\u0001\u0007OK^D\u0015\rZ8paJ#E\tE\u0002\u000f\u0005#1q!\u0001\u0002\t\u0002\u0011\u0011\u0019b\u0005\u0004\u0003\u0012\tU!1\u0004\t\u0004%\t]\u0011b\u0001B\r'\t1\u0011I\\=SK\u001a\u00042A\u0005B\u000f\u0013\r\u0011yb\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\b;\nEA\u0011\u0001B\u0012)\t\u0011y\u0001\u0003\u0006\u0003(\tE!\u0019!C\u0001\u0005S\t\u0001eQ(O\r&;UKU!U\u0013>su,\u0013(T)\u0006sE+S!U\u0013>su\fT(D\u0017V\u0011!1\u0006\t\u0005\u0005[\u00119$\u0004\u0002\u00030)!!\u0011\u0007B\u001a\u0003\u0011a\u0017M\\4\u000b\u0005\tU\u0012\u0001\u00026bm\u0006LAA!\u000f\u00030\t1qJ\u00196fGRD\u0011B!\u0010\u0003\u0012\u0001\u0006IAa\u000b\u0002C\r{eJR%H+J\u000bE+S(O?&s5\u000bV!O)&\u000bE+S(O?2{5i\u0013\u0011\u0007\u0011\t\u0005#\u0011\u0003\u0001\u0005\u0005\u0007\u0012!ET3x\u0011\u0006$wn\u001c9NCB\u0004\u0016M\u001d;ji&|gn],ji\"\u001c\u0006\u000f\\5u%\u0012#UC\u0002B#\u0005\u0017\u0012)f\u0005\u0003\u0003@\t\u001d\u0003\u0003\u0002\b\u0010\u0005\u0013\u00022\u0001\u0007B&\t\u001d\t9Ha\u0010C\u0002mA1Ba\u0014\u0003@\t\u0005\t\u0015!\u0003\u0003R\u0005!\u0001O]3w!\u0011qqBa\u0015\u0011\u0007a\u0011)\u0006B\u0004\u0003X\t}\"\u0019A\u000e\u0003\u0003QC1\"a#\u0003@\t\u0005\t\u0015!\u0003\u0003\\AI!#a$\u0002\u0014\nu#q\f\t\u0007\u00037\u000bYKa\u0015\u0011\r\u0005m\u00151\u0016B%\u0011-\t)La\u0010\u0003\u0002\u0003\u0006I!!\b\t\u0017\t\u0015$q\bB\u0002B\u0003-!qM\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004CBA@\u0003\u000b\u0013I\u0005C\u0006\u0003l\t}\"1!Q\u0001\f\t5\u0014AC3wS\u0012,gnY3%gA1\u0011qPAC\u0005'Bq!\u0018B \t\u0003\u0011\t\b\u0006\u0005\u0003t\tu$q\u0010BA)\u0019\u0011)H!\u001f\u0003|AA!q\u000fB \u0005\u0013\u0012\u0019&\u0004\u0002\u0003\u0012!A!Q\rB8\u0001\b\u00119\u0007\u0003\u0005\u0003l\t=\u00049\u0001B7\u0011!\u0011yEa\u001cA\u0002\tE\u0003\u0002CAF\u0005_\u0002\rAa\u0017\t\u0015\u0005U&q\u000eI\u0001\u0002\u0004\ti\u0002\u0003\u0006\u0003\u0006\n}\"\u0019!C!\u0005\u000f\u000b1\u0002]1si&$\u0018n\u001c8feV\u0011!\u0011\u0012\t\u0006%\t-%qR\u0005\u0004\u0005\u001b\u001b\"AB(qi&|g\u000eE\u0002/\u0005#K1Aa%\u0005\u0005-\u0001\u0016M\u001d;ji&|g.\u001a:\t\u0013\t]%q\bQ\u0001\n\t%\u0015\u0001\u00049beRLG/[8oKJ\u0004\u0003\u0002CA\u001f\u0005\u007f!\t%a\u0010\t\u0011\u0005=#q\bC!\u0005;#bAa\u0018\u0003 \n\r\u0006\u0002\u0003BQ\u00057\u0003\r!a\u0012\u0002\u000bM\u0004H.\u001b;\t\u0011\u0005}#1\u0014a\u0001\u0003C:1Ba*\u0003\u0012\u0005\u0005\t\u0012\u0001\u0003\u0003*\u0006\u0011c*Z<IC\u0012|w\u000e]'baB\u000b'\u000f^5uS>t7oV5uQN\u0003H.\u001b;S\t\u0012\u0003BAa\u001e\u0003,\u001aY!\u0011\tB\t\u0003\u0003E\t\u0001\u0002BW'\u0019\u0011YK!\u0006\u0003\u001c!9QLa+\u0005\u0002\tEFC\u0001BU\u0011)\u0011)La+\u0012\u0002\u0013\u0005!qW\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001a\u0016\r\u0005M(\u0011\u0018B^\t\u001d\t9Ha-C\u0002m!qAa\u0016\u00034\n\u00071\u0004\u0003\u0006\u0003@\n-\u0016\u0011!C\u0005\u0005\u0003\f1B]3bIJ+7o\u001c7wKR\u0011!1\u0006\u0005\u000b\u0005\u007f\u0013\t\"!A\u0005\n\t\u0005\u0007"
)
public class NewHadoopRDD<K, V> extends RDD<Tuple2<K, V>> {
   public final Class<? extends InputFormat<K, V>> org$apache$spark$rdd$NewHadoopRDD$$inputFormatClass;
   private final transient Configuration _conf;
   private final Broadcast<SerializableConfiguration> confBroadcast;
   private final String org$apache$spark$rdd$NewHadoopRDD$$jobTrackerId;
   private final transient JobID jobId;
   private final boolean shouldCloneJobConf;
   private final boolean org$apache$spark$rdd$NewHadoopRDD$$ignoreCorruptFiles;
   private final boolean ignoreEmptySplits;

   public static Object CONFIGURATION_INSTANTIATION_LOCK() {
      return NewHadoopRDD$.MODULE$.CONFIGURATION_INSTANTIATION_LOCK();
   }

   private Configuration _conf() {
      return this._conf;
   }

   private Broadcast<SerializableConfiguration> confBroadcast() {
      return this.confBroadcast;
   }

   public String org$apache$spark$rdd$NewHadoopRDD$$jobTrackerId() {
      return this.org$apache$spark$rdd$NewHadoopRDD$$jobTrackerId;
   }

   public JobID jobId() {
      return this.jobId;
   }

   private boolean shouldCloneJobConf() {
      return this.shouldCloneJobConf;
   }

   public boolean org$apache$spark$rdd$NewHadoopRDD$$ignoreCorruptFiles() {
      return this.org$apache$spark$rdd$NewHadoopRDD$$ignoreCorruptFiles;
   }

   private boolean ignoreEmptySplits() {
      return this.ignoreEmptySplits;
   }

   public Configuration getConf() {
      Configuration conf = ((SerializableConfiguration)this.confBroadcast().value()).value();
      Configuration var10000;
      if (this.shouldCloneJobConf()) {
         Object var3;
         synchronized(NewHadoopRDD$.MODULE$.CONFIGURATION_INSTANTIATION_LOCK()) {
            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Cloning Hadoop Configuration";
               }
            });
            var3 = conf instanceof JobConf ? new JobConf(conf) : new Configuration(conf);
         }

         var10000 = (Configuration)var3;
      } else {
         var10000 = conf;
      }

      return var10000;
   }

   public Partition[] getPartitions() {
      InputFormat inputFormat = (InputFormat)this.org$apache$spark$rdd$NewHadoopRDD$$inputFormatClass.newInstance();
      if (inputFormat instanceof Configurable) {
         ((Configurable)inputFormat).setConf(this._conf());
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else {
         BoxedUnit var8 = BoxedUnit.UNIT;
      }

      Buffer allRowSplits = (Buffer)JavaConverters$.MODULE$.asScalaBufferConverter(inputFormat.getSplits(new JobContextImpl(this._conf(), this.jobId()))).asScala();
      Buffer rawSplits = this.ignoreEmptySplits() ? (Buffer)allRowSplits.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(InputSplit x$1) {
            return x$1.getLength() > 0L;
         }
      }) : allRowSplits;
      Partition[] result = new Partition[rawSplits.size()];
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), rawSplits.size()).foreach$mVc$sp(new Serializable(this, rawSplits, result) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final NewHadoopRDD $outer;
         private final Buffer rawSplits$1;
         private final Partition[] result$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.result$1[i] = new NewHadoopPartition(this.$outer.id(), i, (InputSplit)this.rawSplits$1.apply(i));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.rawSplits$1 = rawSplits$1;
               this.result$1 = result$1;
            }
         }
      });
      return result;
   }

   public InterruptibleIterator<Tuple2<K, V>> compute(Partition theSplit, TaskContext context) {
      Iterator iter = new Iterator<Tuple2<K, V>>(this, theSplit, context) {
         private final NewHadoopPartition org$apache$spark$rdd$NewHadoopRDD$$anon$$split;
         private final Configuration conf;
         private final InputMetrics org$apache$spark$rdd$NewHadoopRDD$$anon$$inputMetrics;
         private final long org$apache$spark$rdd$NewHadoopRDD$$anon$$existingBytesRead;
         private final Option<Function0<Object>> getBytesReadCallback;
         private final InputFormat<K, V> format;
         private final TaskAttemptID attemptId;
         private final TaskAttemptContextImpl hadoopAttemptContext;
         private boolean finished;
         private RecordReader<K, V> reader;
         private boolean havePair;
         private int recordsSinceMetricsUpdate;
         // $FF: synthetic field
         private final NewHadoopRDD $outer;

         public Iterator<Tuple2<K, V>> seq() {
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

         public Iterator<Tuple2<K, V>> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<Tuple2<K, V>> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<Tuple2<K, V>> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<Tuple2<K, V>, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<Tuple2<K, V>, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<Tuple2<K, V>> filter(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<K, V>, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<Tuple2<K, V>> withFilter(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<Tuple2<K, V>> filterNot(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<Tuple2<K, V>, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<K, V>, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<Tuple2<K, V>, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<Tuple2<K, V>> takeWhile(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> partition(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> span(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<Tuple2<K, V>> dropWhile(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<Tuple2<K, V>, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<Tuple2<K, V>, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<Tuple2<K, V>, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<Tuple2<K, V>> find(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<Tuple2<K, V>> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<Tuple2<K, V>>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<Tuple2<K, V>>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> duplicate() {
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

         public Traversable<Tuple2<K, V>> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<Tuple2<K, V>> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<Tuple2<K, V>> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<Tuple2<K, V>> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<Tuple2<K, V>, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<Tuple2<K, V>, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, Tuple2<K, V>, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<Tuple2<K, V>, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, Tuple2<K, V>, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<Tuple2<K, V>, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, Tuple2<K, V>, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<Tuple2<K, V>, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<K, V>, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<Tuple2<K, V>, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<K, V>, B> seqop, Function2<B, B, B> combop) {
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

         public List<Tuple2<K, V>> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<Tuple2<K, V>> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<Tuple2<K, V>> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<Tuple2<K, V>> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<Tuple2<K, V>> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<K, V>, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<K, V>, Tuple2<T, U>> ev) {
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

         public NewHadoopPartition org$apache$spark$rdd$NewHadoopRDD$$anon$$split() {
            return this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split;
         }

         private Configuration conf() {
            return this.conf;
         }

         public InputMetrics org$apache$spark$rdd$NewHadoopRDD$$anon$$inputMetrics() {
            return this.org$apache$spark$rdd$NewHadoopRDD$$anon$$inputMetrics;
         }

         public long org$apache$spark$rdd$NewHadoopRDD$$anon$$existingBytesRead() {
            return this.org$apache$spark$rdd$NewHadoopRDD$$anon$$existingBytesRead;
         }

         private Option<Function0<Object>> getBytesReadCallback() {
            return this.getBytesReadCallback;
         }

         public void org$apache$spark$rdd$NewHadoopRDD$$anon$$updateBytesRead() {
            this.getBytesReadCallback().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(Function0<Object> getBytesRead) {
                  this.$outer.org$apache$spark$rdd$NewHadoopRDD$$anon$$inputMetrics().setBytesRead(this.$outer.org$apache$spark$rdd$NewHadoopRDD$$anon$$existingBytesRead() + getBytesRead.apply$mcJ$sp());
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

         private InputFormat<K, V> format() {
            return this.format;
         }

         private TaskAttemptID attemptId() {
            return this.attemptId;
         }

         private TaskAttemptContextImpl hadoopAttemptContext() {
            return this.hadoopAttemptContext;
         }

         private boolean finished() {
            return this.finished;
         }

         private void finished_$eq(boolean x$1) {
            this.finished = x$1;
         }

         private RecordReader<K, V> reader() {
            return this.reader;
         }

         private void reader_$eq(RecordReader<K, V> x$1) {
            this.reader = x$1;
         }

         private boolean havePair() {
            return this.havePair;
         }

         private void havePair_$eq(boolean x$1) {
            this.havePair = x$1;
         }

         private int recordsSinceMetricsUpdate() {
            return this.recordsSinceMetricsUpdate;
         }

         private void recordsSinceMetricsUpdate_$eq(int x$1) {
            this.recordsSinceMetricsUpdate = x$1;
         }

         public boolean hasNext() {
            if (!this.finished() && !this.havePair()) {
               try {
                  this.finished_$eq(!this.reader().nextKeyValue());
               } catch (Throwable var6) {
                  label108: {
                     if (var6 instanceof IOException) {
                        IOException var3 = (IOException)var6;
                        if (this.$outer.org$apache$spark$rdd$NewHadoopRDD$$ignoreCorruptFiles()) {
                           this.$outer.logWarning(new Serializable(this) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;

                              public final String apply() {
                                 return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Skipped the rest content in the corrupted file: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit()}));
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                 }
                              }
                           }, var3);
                           this.finished_$eq(true);
                           BoxedUnit var4 = BoxedUnit.UNIT;
                           break label108;
                        }
                     }

                     throw var6;
                  }
               }

               if (this.finished()) {
                  this.org$apache$spark$rdd$NewHadoopRDD$$anon$$close();
               }

               this.havePair_$eq(!this.finished());
            }

            return !this.finished();
         }

         public Tuple2<K, V> next() {
            if (this.hasNext()) {
               this.havePair_$eq(false);
               if (!this.finished()) {
                  this.org$apache$spark$rdd$NewHadoopRDD$$anon$$inputMetrics().incRecordsRead(1L);
               }

               if (this.org$apache$spark$rdd$NewHadoopRDD$$anon$$inputMetrics().recordsRead() % (long)SparkHadoopUtil$.MODULE$.UPDATE_INPUT_METRICS_INTERVAL_RECORDS() == 0L) {
                  this.org$apache$spark$rdd$NewHadoopRDD$$anon$$updateBytesRead();
               }

               return new Tuple2<K, V>(this.reader().getCurrentKey(), this.reader().getCurrentValue());
            } else {
               throw new NoSuchElementException("End of stream");
            }
         }

         public void org$apache$spark$rdd$NewHadoopRDD$$anon$$close() {
            if (this.reader() != null) {
               InputFileBlockHolder$.MODULE$.unset();

               try {
                  this.reader().close();
               } catch (Exception var8) {
                  if (!ShutdownHookManager$.MODULE$.inShutdown()) {
                     this.$outer.logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "Exception in RecordReader.close()";
                        }
                     }, var8);
                  }
               } finally {
                  this.reader_$eq((RecordReader)null);
               }

               if (this.getBytesReadCallback().isDefined()) {
                  this.org$apache$spark$rdd$NewHadoopRDD$$anon$$updateBytesRead();
               } else if (this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit().value() instanceof FileSplit || this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit().value() instanceof CombineFileSplit) {
                  try {
                     this.org$apache$spark$rdd$NewHadoopRDD$$anon$$inputMetrics().incBytesRead(((InputSplit)this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit().value()).getLength());
                  } catch (IOException var7) {
                     this.$outer.logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "Unable to get input size to set InputMetrics for task";
                        }
                     }, var7);
                  }
               }
            }

         }

         private final RecordReader liftedTree1$1() {
            RecordReader var10000;
            try {
               RecordReader _reader = this.format().createRecordReader((InputSplit)this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit().value(), this.hadoopAttemptContext());
               _reader.initialize((InputSplit)this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit().value(), this.hadoopAttemptContext());
               var10000 = _reader;
            } catch (Throwable var7) {
               if (var7 instanceof IOException) {
                  IOException var3 = (IOException)var7;
                  if (this.$outer.org$apache$spark$rdd$NewHadoopRDD$$ignoreCorruptFiles()) {
                     this.$outer.logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Skipped the rest content in the corrupted file: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit()}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     }, var3);
                     this.finished_$eq(true);
                     Object var4 = null;
                     var10000 = (RecordReader)var4;
                     return var10000;
                  }
               }

               throw var7;
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               TraversableOnce$class.$init$(this);
               Iterator$class.$init$(this);
               this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split = (NewHadoopPartition)theSplit$1;
               $outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Input split: ").append((Object)this.$outer.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit()).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               this.conf = $outer.getConf();
               this.org$apache$spark$rdd$NewHadoopRDD$$anon$$inputMetrics = context$1.taskMetrics().inputMetrics();
               this.org$apache$spark$rdd$NewHadoopRDD$$anon$$existingBytesRead = this.org$apache$spark$rdd$NewHadoopRDD$$anon$$inputMetrics().bytesRead();
               InputSplit var4 = (InputSplit)this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit().value();
               if (var4 instanceof FileSplit) {
                  FileSplit var5 = (FileSplit)var4;
                  InputFileBlockHolder$.MODULE$.set(var5.getPath().toString(), var5.getStart(), var5.getLength());
                  BoxedUnit var6 = BoxedUnit.UNIT;
               } else {
                  InputFileBlockHolder$.MODULE$.unset();
                  BoxedUnit var13 = BoxedUnit.UNIT;
               }

               InputSplit var7 = (InputSplit)this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().serializableHadoopSplit().value();
               boolean var8;
               if (var7 instanceof FileSplit) {
                  var8 = true;
               } else if (var7 instanceof CombineFileSplit) {
                  var8 = true;
               } else {
                  var8 = false;
               }

               Object var9;
               if (var8) {
                  var9 = new Some(SparkHadoopUtil$.MODULE$.get().getFSBytesReadOnThreadCallback());
               } else {
                  var9 = None$.MODULE$;
               }

               this.getBytesReadCallback = (Option<Function0<Object>>)var9;
               this.format = (InputFormat)$outer.org$apache$spark$rdd$NewHadoopRDD$$inputFormatClass.newInstance();
               InputFormat var10 = this.format();
               if (var10 instanceof Configurable) {
                  ((Configurable)var10).setConf(this.conf());
                  BoxedUnit var12 = BoxedUnit.UNIT;
               } else {
                  BoxedUnit var14 = BoxedUnit.UNIT;
               }

               this.attemptId = new TaskAttemptID($outer.org$apache$spark$rdd$NewHadoopRDD$$jobTrackerId(), $outer.id(), TaskType.MAP, this.org$apache$spark$rdd$NewHadoopRDD$$anon$$split().index(), 0);
               this.hadoopAttemptContext = new TaskAttemptContextImpl(this.conf(), this.attemptId());
               this.finished = false;
               this.reader = this.liftedTree1$1();
               context$1.addTaskCompletionListener((Function1)(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(TaskContext context) {
                     this.$outer.org$apache$spark$rdd$NewHadoopRDD$$anon$$updateBytesRead();
                     this.$outer.org$apache$spark$rdd$NewHadoopRDD$$anon$$close();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }));
               this.havePair = false;
               this.recordsSinceMetricsUpdate = 0;
            }
         }
      };
      return new InterruptibleIterator<Tuple2<K, V>>(context, iter);
   }

   @DeveloperApi
   public <U> RDD<U> mapPartitionsWithInputSplit(Function2<InputSplit, Iterator<Tuple2<K, V>>, Iterator<U>> f, boolean preservesPartitioning, ClassTag<U> evidence$1) {
      return new NewHadoopMapPartitionsWithSplitRDD(this, f, preservesPartitioning, evidence$1, ClassTag$.MODULE$.apply(Tuple2.class));
   }

   public <U> boolean mapPartitionsWithInputSplit$default$2() {
      return false;
   }

   public Seq<String> getPreferredLocations(Partition hsplit) {
      InputSplit split = (InputSplit)((NewHadoopPartition)hsplit).serializableHadoopSplit().value();
      Option locs = HadoopRDD$.MODULE$.convertSplitLocationInfo(split.getLocationInfo());
      return (Seq)locs.getOrElse(new Serializable(this, split) {
         public static final long serialVersionUID = 0L;
         private final InputSplit split$1;

         public final WrappedArray<String> apply() {
            return Predef$.MODULE$.wrapRefArray(Predef$.MODULE$.refArrayOps((Object[])this.split$1.getLocations()).filter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(String x$2) {
                  boolean var10000;
                  label23: {
                     String var2 = "localhost";
                     if (x$2 == null) {
                        if (var2 == null) {
                           break label23;
                        }
                     } else if (x$2.equals(var2)) {
                        break label23;
                     }

                     var10000 = true;
                     return var10000;
                  }

                  var10000 = false;
                  return var10000;
               }
            }));
         }

         public {
            this.split$1 = split$1;
         }
      });
   }

   public NewHadoopRDD<K, V> persist(StorageLevel storageLevel) {
      if (storageLevel.deserialized()) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Caching NewHadoopRDDs as deserialized objects usually leads to undesired behavior because Hadoop's RecordReader reuses the same Writable object for all records. Use a map transformation to make copies of the records.";
            }
         });
      }

      return (NewHadoopRDD)super.persist(storageLevel);
   }

   public NewHadoopRDD(SparkContext sc, Class<? extends InputFormat<K, V>> inputFormatClass, Class<K> keyClass, Class<V> valueClass, Configuration _conf) {
      super(sc, Nil$.MODULE$, ClassTag$.MODULE$.apply(Tuple2.class));
      this.org$apache$spark$rdd$NewHadoopRDD$$inputFormatClass = inputFormatClass;
      this._conf = _conf;
      this.confBroadcast = sc.<SerializableConfiguration>broadcast(new SerializableConfiguration(_conf), ClassTag$.MODULE$.apply(SerializableConfiguration.class));
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
      this.org$apache$spark$rdd$NewHadoopRDD$$jobTrackerId = formatter.format(new Date());
      this.jobId = new JobID(this.org$apache$spark$rdd$NewHadoopRDD$$jobTrackerId(), this.id());
      this.shouldCloneJobConf = this.sparkContext().conf().getBoolean("spark.hadoop.cloneConf", false);
      this.org$apache$spark$rdd$NewHadoopRDD$$ignoreCorruptFiles = BoxesRunTime.unboxToBoolean(this.sparkContext().conf().get(org.apache.spark.internal.config.package$.MODULE$.IGNORE_CORRUPT_FILES()));
      this.ignoreEmptySplits = BoxesRunTime.unboxToBoolean(this.sparkContext().conf().get(org.apache.spark.internal.config.package$.MODULE$.HADOOP_RDD_IGNORE_EMPTY_SPLITS()));
   }

   public static class NewHadoopMapPartitionsWithSplitRDD$ implements Serializable {
      public static final NewHadoopMapPartitionsWithSplitRDD$ MODULE$;

      static {
         new NewHadoopMapPartitionsWithSplitRDD$();
      }

      public <U, T> boolean $lessinit$greater$default$3() {
         return false;
      }

      private Object readResolve() {
         return MODULE$;
      }

      public NewHadoopMapPartitionsWithSplitRDD$() {
         MODULE$ = this;
      }
   }

   public static class NewHadoopMapPartitionsWithSplitRDD<U, T> extends RDD<U> {
      private final Function2<InputSplit, Iterator<T>, Iterator<U>> f;
      private final ClassTag<T> evidence$3;
      private final Option<Partitioner> partitioner;

      public Option<Partitioner> partitioner() {
         return this.partitioner;
      }

      public Partition[] getPartitions() {
         return this.firstParent(this.evidence$3).partitions();
      }

      public Iterator<U> compute(Partition split, TaskContext context) {
         NewHadoopPartition partition = (NewHadoopPartition)split;
         InputSplit inputSplit = (InputSplit)partition.serializableHadoopSplit().value();
         return this.f.apply(inputSplit, this.firstParent(this.evidence$3).iterator(split, context));
      }

      public NewHadoopMapPartitionsWithSplitRDD(RDD<T> prev, Function2<InputSplit, Iterator<T>, Iterator<U>> f, boolean preservesPartitioning, ClassTag<U> evidence$2, ClassTag<T> evidence$3) {
         super(prev, evidence$2);
         this.f = f;
         this.evidence$3 = evidence$3;
         this.partitioner = (Option<Partitioner>)(preservesPartitioning ? this.firstParent(evidence$3).partitioner() : None$.MODULE$);
      }
   }
}
