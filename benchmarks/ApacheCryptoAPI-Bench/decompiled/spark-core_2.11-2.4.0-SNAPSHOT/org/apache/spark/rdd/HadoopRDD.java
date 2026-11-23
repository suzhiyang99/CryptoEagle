package org.apache.spark.rdd;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.InputSplitWithLocationInfo;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.CombineFileSplit;
import org.apache.hadoop.util.ReflectionUtils;
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
import org.apache.spark.util.NextIterator;
import org.apache.spark.util.SerializableConfiguration;
import org.apache.spark.util.ShutdownHookManager$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.collection.mutable.WrappedArray;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\r]c\u0001B\u0001\u0003\u0001-\u0011\u0011\u0002S1e_>\u0004(\u000b\u0012#\u000b\u0005\r!\u0011a\u0001:eI*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0016\u00071I2eE\u0002\u0001\u001b\u0015\u00022AD\b\u0012\u001b\u0005\u0011\u0011B\u0001\t\u0003\u0005\r\u0011F\t\u0012\t\u0005%U9\"%D\u0001\u0014\u0015\u0005!\u0012!B:dC2\f\u0017B\u0001\f\u0014\u0005\u0019!V\u000f\u001d7feA\u0011\u0001$\u0007\u0007\u0001\t\u0015Q\u0002A1\u0001\u001c\u0005\u0005Y\u0015C\u0001\u000f !\t\u0011R$\u0003\u0002\u001f'\t9aj\u001c;iS:<\u0007C\u0001\n!\u0013\t\t3CA\u0002B]f\u0004\"\u0001G\u0012\u0005\u000b\u0011\u0002!\u0019A\u000e\u0003\u0003Y\u0003\"AJ\u0015\u000e\u0003\u001dR!\u0001\u000b\u0003\u0002\u0011%tG/\u001a:oC2L!AK\u0014\u0003\u000f1{wmZ5oO\"AA\u0006\u0001B\u0001B\u0003%Q&\u0001\u0002tGB\u0011afL\u0007\u0002\t%\u0011\u0001\u0007\u0002\u0002\r'B\f'o[\"p]R,\u0007\u0010\u001e\u0005\te\u0001\u0011\t\u0011)A\u0005g\u0005y!M]8bI\u000e\f7\u000f^3e\u0007>tg\rE\u00025oej\u0011!\u000e\u0006\u0003m\u0011\t\u0011B\u0019:pC\u0012\u001c\u0017m\u001d;\n\u0005a*$!\u0003\"s_\u0006$7-Y:u!\tQT(D\u0001<\u0015\taD!\u0001\u0003vi&d\u0017B\u0001 <\u0005e\u0019VM]5bY&T\u0018M\u00197f\u0007>tg-[4ve\u0006$\u0018n\u001c8\t\u0011\u0001\u0003!\u0011!Q\u0001\n\u0005\u000bq#\u001b8ji2{7-\u00197K_\n\u001cuN\u001c4Gk:\u001cw\n\u001d;\u0011\u0007I\u0011E)\u0003\u0002D'\t1q\n\u001d;j_:\u0004BAE#H\u001f&\u0011ai\u0005\u0002\n\rVt7\r^5p]F\u0002\"\u0001S'\u000e\u0003%S!AS&\u0002\r5\f\u0007O]3e\u0015\tae!\u0001\u0004iC\u0012|w\u000e]\u0005\u0003\u001d&\u0013qAS8c\u0007>tg\r\u0005\u0002\u0013!&\u0011\u0011k\u0005\u0002\u0005+:LG\u000f\u0003\u0005T\u0001\t\u0005\t\u0015!\u0003U\u0003AIg\u000e];u\r>\u0014X.\u0019;DY\u0006\u001c8\u000f\r\u0002V;B\u0019a+\u0017/\u000f\u0005I9\u0016B\u0001-\u0014\u0003\u0019\u0001&/\u001a3fM&\u0011!l\u0017\u0002\u0006\u00072\f7o\u001d\u0006\u00031N\u0001\"\u0001G/\u0005\u0013y\u0013\u0016\u0011!A\u0001\u0006\u0003y&aA0%cE\u0011A\u0004\u0019\t\u0005\u0011\u0006<\"%\u0003\u0002c\u0013\nY\u0011J\u001c9vi\u001a{'/\\1u\u0011!!\u0007A!A!\u0002\u0013)\u0017\u0001C6fs\u000ec\u0017m]:\u0011\u0007YKv\u0003\u0003\u0005h\u0001\t\u0005\t\u0015!\u0003i\u0003)1\u0018\r\\;f\u00072\f7o\u001d\t\u0004-f\u0013\u0003\u0002\u00036\u0001\u0005\u0003\u0005\u000b\u0011B6\u0002\u001b5Lg\u000eU1si&$\u0018n\u001c8t!\t\u0011B.\u0003\u0002n'\t\u0019\u0011J\u001c;\t\u000b=\u0004A\u0011\u00019\u0002\rqJg.\u001b;?)!\t(o\u001d;vund\b\u0003\u0002\b\u0001/\tBQ\u0001\f8A\u00025BQA\r8A\u0002MBQ\u0001\u00118A\u0002\u0005CQa\u00158A\u0002Y\u0004$a^=\u0011\u0007YK\u0006\u0010\u0005\u0002\u0019s\u0012Ia,^A\u0001\u0002\u0003\u0015\ta\u0018\u0005\u0006I:\u0004\r!\u001a\u0005\u0006O:\u0004\r\u0001\u001b\u0005\u0006U:\u0004\ra\u001b\u0005\u0006_\u0002!\tA \u000b\rc~\f\t!!\u0002\u0002\u0012\u0005M\u0011Q\u0003\u0005\u0006Yu\u0004\r!\f\u0005\u0007\u0003\u0007i\b\u0019A$\u0002\t\r|gN\u001a\u0005\u0007'v\u0004\r!a\u00021\t\u0005%\u0011Q\u0002\t\u0005-f\u000bY\u0001E\u0002\u0019\u0003\u001b!1\"a\u0004\u0002\u0006\u0005\u0005\t\u0011!B\u0001?\n\u0019q\f\n\u001a\t\u000b\u0011l\b\u0019A3\t\u000b\u001dl\b\u0019\u00015\t\u000b)l\b\u0019A6\t\u0013\u0005e\u0001A1A\u0005\u0012\u0005m\u0011a\u00046pE\u000e{gNZ\"bG\",7*Z=\u0016\u0005\u0005u\u0001c\u0001,\u0002 %\u0019\u0011\u0011E.\u0003\rM#(/\u001b8h\u0011!\t)\u0003\u0001Q\u0001\n\u0005u\u0011\u0001\u00056pE\u000e{gNZ\"bG\",7*Z=!\u0011%\tI\u0003\u0001b\u0001\n#\tY\"A\nj]B,HOR8s[\u0006$8)Y2iK.+\u0017\u0010\u0003\u0005\u0002.\u0001\u0001\u000b\u0011BA\u000f\u0003QIg\u000e];u\r>\u0014X.\u0019;DC\u000eDWmS3zA!I\u0011\u0011\u0007\u0001C\u0002\u0013%\u00111G\u0001\u000bGJ,\u0017\r^3US6,WCAA\u001b!\u0011\t9$a\u0010\u000e\u0005\u0005e\"b\u0001\u001f\u0002<)\u0011\u0011QH\u0001\u0005U\u00064\u0018-\u0003\u0003\u0002B\u0005e\"\u0001\u0002#bi\u0016D\u0001\"!\u0012\u0001A\u0003%\u0011QG\u0001\fGJ,\u0017\r^3US6,\u0007\u0005C\u0005\u0002J\u0001\u0011\r\u0011\"\u0003\u0002L\u0005\u00112\u000f[8vY\u0012\u001cEn\u001c8f\u0015>\u00147i\u001c8g+\t\ti\u0005E\u0002\u0013\u0003\u001fJ1!!\u0015\u0014\u0005\u001d\u0011un\u001c7fC:D\u0001\"!\u0016\u0001A\u0003%\u0011QJ\u0001\u0014g\"|W\u000f\u001c3DY>tWMS8c\u0007>tg\r\t\u0005\n\u00033\u0002!\u0019!C\u0005\u0003\u0017\n!#[4o_J,7i\u001c:skB$h)\u001b7fg\"A\u0011Q\f\u0001!\u0002\u0013\ti%A\njO:|'/Z\"peJ,\b\u000f\u001e$jY\u0016\u001c\b\u0005C\u0005\u0002b\u0001\u0011\r\u0011\"\u0003\u0002L\u0005\t\u0012n\u001a8pe\u0016,U\u000e\u001d;z'Bd\u0017\u000e^:\t\u0011\u0005\u0015\u0004\u0001)A\u0005\u0003\u001b\n!#[4o_J,W)\u001c9usN\u0003H.\u001b;tA!9\u0011\u0011\u000e\u0001\u0005\u0012\u0005-\u0014AC4fi*{'mQ8oMR\tq\tC\u0004\u0002p\u0001!\t\"!\u001d\u0002\u001d\u001d,G/\u00138qkR4uN]7biR\u0019\u0001-a\u001d\t\u000f\u0005\r\u0011Q\u000ea\u0001\u000f\"9\u0011q\u000f\u0001\u0005B\u0005e\u0014!D4fiB\u000b'\u000f^5uS>t7/\u0006\u0002\u0002|A)!#! \u0002\u0002&\u0019\u0011qP\n\u0003\u000b\u0005\u0013(/Y=\u0011\u00079\n\u0019)C\u0002\u0002\u0006\u0012\u0011\u0011\u0002U1si&$\u0018n\u001c8\t\u000f\u0005%\u0005\u0001\"\u0011\u0002\f\u000691m\\7qkR,GCBAG\u0003'\u000b9\n\u0005\u0003/\u0003\u001f\u000b\u0012bAAI\t\t)\u0012J\u001c;feJ,\b\u000f^5cY\u0016LE/\u001a:bi>\u0014\b\u0002CAK\u0003\u000f\u0003\r!!!\u0002\u0011QDWm\u00159mSRD\u0001\"!'\u0002\b\u0002\u0007\u00111T\u0001\bG>tG/\u001a=u!\rq\u0013QT\u0005\u0004\u0003?#!a\u0003+bg.\u001cuN\u001c;fqRDq!a)\u0001\t\u0003\t)+A\u000enCB\u0004\u0016M\u001d;ji&|gn],ji\"Le\u000e];u'Bd\u0017\u000e^\u000b\u0005\u0003O\u000by\u000b\u0006\u0004\u0002*\u0006\r\u0017Q\u001e\u000b\u0005\u0003W\u000b\u0019\f\u0005\u0003\u000f\u001f\u00055\u0006c\u0001\r\u00020\u00129\u0011\u0011WAQ\u0005\u0004Y\"!A+\t\u0015\u0005U\u0016\u0011UA\u0001\u0002\b\t9,\u0001\u0006fm&$WM\\2fIE\u0002b!!/\u0002@\u00065VBAA^\u0015\r\tilE\u0001\be\u00164G.Z2u\u0013\u0011\t\t-a/\u0003\u0011\rc\u0017m]:UC\u001eD\u0001\"!2\u0002\"\u0002\u0007\u0011qY\u0001\u0002MBI!#!3\u0002N\u0006M\u00171^\u0005\u0004\u0003\u0017\u001c\"!\u0003$v]\u000e$\u0018n\u001c83!\rA\u0015qZ\u0005\u0004\u0003#L%AC%oaV$8\u000b\u001d7jiB)\u0011Q[As#9!\u0011q[Aq\u001d\u0011\tI.a8\u000e\u0005\u0005m'bAAo\u0015\u00051AH]8pizJ\u0011\u0001F\u0005\u0004\u0003G\u001c\u0012a\u00029bG.\fw-Z\u0005\u0005\u0003O\fIO\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0015\r\t\u0019o\u0005\t\u0007\u0003+\f)/!,\t\u0015\u0005=\u0018\u0011\u0015I\u0001\u0002\u0004\ti%A\u000bqe\u0016\u001cXM\u001d<fgB\u000b'\u000f^5uS>t\u0017N\\4)\t\u0005\u0005\u00161\u001f\t\u0005\u0003k\fY0\u0004\u0002\u0002x*\u0019\u0011\u0011 \u0003\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002~\u0006](\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007b\u0002B\u0001\u0001\u0011\u0005#1A\u0001\u0016O\u0016$\bK]3gKJ\u0014X\r\u001a'pG\u0006$\u0018n\u001c8t)\u0011\u0011)Aa\u0003\u0011\r\u0005U'qAA\u000f\u0013\u0011\u0011I!!;\u0003\u0007M+\u0017\u000f\u0003\u0005\u0003\u000e\u0005}\b\u0019AAA\u0003\u0015\u0019\b\u000f\\5u\u0011\u001d\u0011\t\u0002\u0001C!\u0005'\t!b\u00195fG.\u0004x.\u001b8u)\u0005y\u0005b\u0002B\f\u0001\u0011\u0005#\u0011D\u0001\ba\u0016\u00148/[:u)\u0011\u0011YB!\b\u000e\u0003\u0001A\u0001Ba\b\u0003\u0016\u0001\u0007!\u0011E\u0001\rgR|'/Y4f\u0019\u00164X\r\u001c\t\u0005\u0005G\u0011I#\u0004\u0002\u0003&)\u0019!q\u0005\u0003\u0002\u000fM$xN]1hK&!!1\u0006B\u0013\u00051\u0019Fo\u001c:bO\u0016dUM^3m\u0011\u001d\u0011y\u0003\u0001C\u0001\u0005c\tqaZ3u\u0007>tg-\u0006\u0002\u00034A!!Q\u0007B\u001d\u001b\t\u00119DC\u0002\u0002\u0004-KAAa\u000f\u00038\ti1i\u001c8gS\u001e,(/\u0019;j_:D\u0011Ba\u0010\u0001#\u0003%\tA!\u0011\u0002K5\f\u0007\u000fU1si&$\u0018n\u001c8t/&$\b.\u00138qkR\u001c\u0006\u000f\\5uI\u0011,g-Y;mi\u0012\u0012T\u0003\u0002B\"\u0005/*\"A!\u0012+\t\u00055#qI\u0016\u0003\u0005\u0013\u0002BAa\u0013\u0003T5\u0011!Q\n\u0006\u0005\u0005\u001f\u0012\t&A\u0005v]\u000eDWmY6fI*\u0019\u0011\u0011`\n\n\t\tU#Q\n\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,GaBAY\u0005{\u0011\ra\u0007\u0015\u0004\u0001\u0005Mx\u0001\u0003B/\u0005!\u0005AAa\u0018\u0002\u0013!\u000bGm\\8q%\u0012#\u0005c\u0001\b\u0003b\u00199\u0011A\u0001E\u0001\t\t\r4c\u0002B1\u0005K*#1\u000e\t\u0004%\t\u001d\u0014b\u0001B5'\t1\u0011I\\=SK\u001a\u00042A\u0005B7\u0013\r\u0011yg\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\b_\n\u0005D\u0011\u0001B:)\t\u0011y\u0006\u0003\u0006\u0003x\t\u0005$\u0019!C\u0001\u0005s\n\u0001eQ(O\r&;UKU!U\u0013>su,\u0013(T)\u0006sE+S!U\u0013>su\fT(D\u0017V\u0011!1\u0010\t\u0005\u0005{\u0012\u0019)\u0004\u0002\u0003\u0000)!!\u0011QA\u001e\u0003\u0011a\u0017M\\4\n\t\t\u0015%q\u0010\u0002\u0007\u001f\nTWm\u0019;\t\u0013\t%%\u0011\rQ\u0001\n\tm\u0014!I\"P\u001d\u001aKu)\u0016*B)&{ejX%O'R\u000be\nV%B)&{ej\u0018'P\u0007.\u0003\u0003B\u0003BG\u0005C\u0012\r\u0011\"\u0001\u0003\u0010\u0006I#+R\"P%\u0012\u001bvLQ#U/\u0016+ej\u0018\"Z)\u0016\u001bvLU#B\t~kU\t\u0016*J\u0007~+\u0006\u000bR!U\u000bN+\u0012a\u001b\u0005\t\u0005'\u0013\t\u0007)A\u0005W\u0006Q#+R\"P%\u0012\u001bvLQ#U/\u0016+ej\u0018\"Z)\u0016\u001bvLU#B\t~kU\t\u0016*J\u0007~+\u0006\u000bR!U\u000bN\u0003\u0003\u0002\u0003BL\u0005C\"\tA!'\u0002#\u001d,GoQ1dQ\u0016$W*\u001a;bI\u0006$\u0018\rF\u0002 \u00057C\u0001B!(\u0003\u0016\u0002\u0007\u0011QD\u0001\u0004W\u0016L\b\u0002\u0003BQ\u0005C\"IAa)\u0002#A,HoQ1dQ\u0016$W*\u001a;bI\u0006$\u0018\rF\u0003P\u0005K\u00139\u000b\u0003\u0005\u0003\u001e\n}\u0005\u0019AA\u000f\u0011\u001d\u0011IKa(A\u0002}\tQA^1mk\u0016D\u0001B!,\u0003b\u0011\u0005!qV\u0001\u0016C\u0012$Gj\\2bY\u000e{gNZ5hkJ\fG/[8o)-y%\u0011\u0017B[\u0005s\u0013iL!1\t\u0011\tM&1\u0016a\u0001\u0003;\tAB[8c)J\f7m[3s\u0013\u0012DqAa.\u0003,\u0002\u00071.A\u0003k_\nLE\rC\u0004\u0003<\n-\u0006\u0019A6\u0002\u000fM\u0004H.\u001b;JI\"9!q\u0018BV\u0001\u0004Y\u0017!C1ui\u0016l\u0007\u000f^%e\u0011\u001d\t\u0019Aa+A\u0002\u001d3\u0001B!2\u0003b\u0001!!q\u0019\u0002 \u0011\u0006$wn\u001c9NCB\u0004\u0016M\u001d;ji&|gn],ji\"\u001c\u0006\u000f\\5u%\u0012#UC\u0002Be\u0005\u001f\u0014In\u0005\u0003\u0003D\n-\u0007\u0003\u0002\b\u0010\u0005\u001b\u00042\u0001\u0007Bh\t\u001d\t\tLa1C\u0002mA1Ba5\u0003D\n\u0005\t\u0015!\u0003\u0003V\u0006!\u0001O]3w!\u0011qqBa6\u0011\u0007a\u0011I\u000eB\u0004\u0003\\\n\r'\u0019A\u000e\u0003\u0003QC1\"!2\u0003D\n\u0005\t\u0015!\u0003\u0003`BI!#!3\u0002N\n\u0005(1\u001d\t\u0007\u0003+\f)Oa6\u0011\r\u0005U\u0017Q\u001dBg\u0011-\tyOa1\u0003\u0002\u0003\u0006I!!\u0014\t\u0017\t%(1\u0019B\u0002B\u0003-!1^\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004CBA]\u0003\u007f\u0013i\rC\u0006\u0003p\n\r'1!Q\u0001\f\tE\u0018AC3wS\u0012,gnY3%gA1\u0011\u0011XA`\u0005/Dqa\u001cBb\t\u0003\u0011)\u0010\u0006\u0005\u0003x\u000e\u000511AB\u0003)\u0019\u0011IP!@\u0003\u0000BA!1 Bb\u0005\u001b\u00149.\u0004\u0002\u0003b!A!\u0011\u001eBz\u0001\b\u0011Y\u000f\u0003\u0005\u0003p\nM\b9\u0001By\u0011!\u0011\u0019Na=A\u0002\tU\u0007\u0002CAc\u0005g\u0004\rAa8\t\u0015\u0005=(1\u001fI\u0001\u0002\u0004\ti\u0005\u0003\u0006\u0004\n\t\r'\u0019!C!\u0007\u0017\t1\u0002]1si&$\u0018n\u001c8feV\u00111Q\u0002\t\u0005%\t\u001by\u0001E\u0002/\u0007#I1aa\u0005\u0005\u0005-\u0001\u0016M\u001d;ji&|g.\u001a:\t\u0013\r]!1\u0019Q\u0001\n\r5\u0011\u0001\u00049beRLG/[8oKJ\u0004\u0003\u0002CA<\u0005\u0007$\t%!\u001f\t\u0011\u0005%%1\u0019C!\u0007;!bAa9\u0004 \r\u0005\u0002\u0002\u0003B\u0007\u00077\u0001\r!!!\t\u0011\u0005e51\u0004a\u0001\u00037;1b!\n\u0003b\u0005\u0005\t\u0012\u0001\u0003\u0004(\u0005y\u0002*\u00193p_Bl\u0015\r\u001d)beRLG/[8og^KG\u000f[*qY&$(\u000b\u0012#\u0011\t\tm8\u0011\u0006\u0004\f\u0005\u000b\u0014\t'!A\t\u0002\u0011\u0019Yc\u0005\u0004\u0004*\t\u0015$1\u000e\u0005\b_\u000e%B\u0011AB\u0018)\t\u00199\u0003\u0003\u0006\u00044\r%\u0012\u0013!C\u0001\u0007k\t1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\u001aTC\u0002B\"\u0007o\u0019I\u0004B\u0004\u00022\u000eE\"\u0019A\u000e\u0005\u000f\tm7\u0011\u0007b\u00017!Q1QHB\u0015\u0003\u0003%Iaa\u0010\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0005wB\u0011ba\u0011\u0003b\u0011\u0005Aa!\u0012\u00021\r|gN^3siN\u0003H.\u001b;M_\u000e\fG/[8o\u0013:4w\u000e\u0006\u0003\u0004H\r%\u0003\u0003\u0002\nC\u0005\u000bA\u0001ba\u0013\u0004B\u0001\u00071QJ\u0001\u0006S:4wn\u001d\t\u0006%\u0005u4q\n\t\u0004\u0011\u000eE\u0013bAB*\u0013\n\t2\u000b\u001d7ji2{7-\u0019;j_:LeNZ8\t\u0015\ru\"\u0011MA\u0001\n\u0013\u0019y\u0004"
)
public class HadoopRDD<K, V> extends RDD<Tuple2<K, V>> {
   private final Broadcast<SerializableConfiguration> broadcastedConf;
   public final Option<Function1<JobConf, BoxedUnit>> org$apache$spark$rdd$HadoopRDD$$initLocalJobConfFuncOpt;
   private final Class<? extends InputFormat<K, V>> inputFormatClass;
   private final int minPartitions;
   private final String jobConfCacheKey;
   private final String inputFormatCacheKey;
   private final Date org$apache$spark$rdd$HadoopRDD$$createTime;
   private final boolean shouldCloneJobConf;
   private final boolean org$apache$spark$rdd$HadoopRDD$$ignoreCorruptFiles;
   private final boolean ignoreEmptySplits;

   public static void addLocalConfiguration(String var0, int var1, int var2, int var3, JobConf var4) {
      HadoopRDD$.MODULE$.addLocalConfiguration(var0, var1, var2, var3, var4);
   }

   public static Object getCachedMetadata(String var0) {
      return HadoopRDD$.MODULE$.getCachedMetadata(var0);
   }

   public static int RECORDS_BETWEEN_BYTES_READ_METRIC_UPDATES() {
      return HadoopRDD$.MODULE$.RECORDS_BETWEEN_BYTES_READ_METRIC_UPDATES();
   }

   public static Object CONFIGURATION_INSTANTIATION_LOCK() {
      return HadoopRDD$.MODULE$.CONFIGURATION_INSTANTIATION_LOCK();
   }

   public String jobConfCacheKey() {
      return this.jobConfCacheKey;
   }

   public String inputFormatCacheKey() {
      return this.inputFormatCacheKey;
   }

   public Date org$apache$spark$rdd$HadoopRDD$$createTime() {
      return this.org$apache$spark$rdd$HadoopRDD$$createTime;
   }

   private boolean shouldCloneJobConf() {
      return this.shouldCloneJobConf;
   }

   public boolean org$apache$spark$rdd$HadoopRDD$$ignoreCorruptFiles() {
      return this.org$apache$spark$rdd$HadoopRDD$$ignoreCorruptFiles;
   }

   private boolean ignoreEmptySplits() {
      return this.ignoreEmptySplits;
   }

   public JobConf getJobConf() {
      Configuration conf = ((SerializableConfiguration)this.broadcastedConf.value()).value();
      JobConf var10000;
      if (this.shouldCloneJobConf()) {
         JobConf var3;
         synchronized(HadoopRDD$.MODULE$.CONFIGURATION_INSTANTIATION_LOCK()) {
            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Cloning Hadoop Configuration";
               }
            });
            JobConf newJobConf = new JobConf(conf);
            if (!(conf instanceof JobConf)) {
               this.org$apache$spark$rdd$HadoopRDD$$initLocalJobConfFuncOpt.foreach(new Serializable(this, newJobConf) {
                  public static final long serialVersionUID = 0L;
                  private final JobConf newJobConf$1;

                  public final void apply(Function1<JobConf, BoxedUnit> f) {
                     f.apply(this.newJobConf$1);
                  }

                  public {
                     this.newJobConf$1 = newJobConf$1;
                  }
               });
            }

            var3 = newJobConf;
         }

         var10000 = var3;
      } else if (conf instanceof JobConf) {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Re-using user-broadcasted JobConf";
            }
         });
         var10000 = (JobConf)conf;
      } else {
         var10000 = (JobConf)Option$.MODULE$.apply(HadoopRDD$.MODULE$.getCachedMetadata(this.jobConfCacheKey())).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final HadoopRDD $outer;

            public final JobConf apply(Object conf) {
               this.$outer.logDebug(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Re-using cached JobConf";
                  }
               });
               return (JobConf)conf;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }).getOrElse(new Serializable(this, conf) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final HadoopRDD $outer;
            private final Configuration conf$1;

            public final JobConf apply() {
               JobConf var2;
               synchronized(HadoopRDD$.MODULE$.CONFIGURATION_INSTANTIATION_LOCK()) {
                  this.$outer.logDebug(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Creating new JobConf and caching it for later re-use";
                     }
                  });
                  JobConf newJobConf = new JobConf(this.conf$1);
                  this.$outer.org$apache$spark$rdd$HadoopRDD$$initLocalJobConfFuncOpt.foreach(new Serializable(this, newJobConf) {
                     public static final long serialVersionUID = 0L;
                     private final JobConf newJobConf$2;

                     public final void apply(Function1<JobConf, BoxedUnit> f) {
                        f.apply(this.newJobConf$2);
                     }

                     public {
                        this.newJobConf$2 = newJobConf$2;
                     }
                  });
                  HadoopRDD$.MODULE$.org$apache$spark$rdd$HadoopRDD$$putCachedMetadata(this.$outer.jobConfCacheKey(), newJobConf);
                  var2 = newJobConf;
               }

               return var2;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.conf$1 = conf$1;
               }
            }
         });
      }

      return var10000;
   }

   public InputFormat<K, V> getInputFormat(JobConf conf) {
      InputFormat newInputFormat = (InputFormat)ReflectionUtils.newInstance(this.inputFormatClass, conf);
      if (newInputFormat instanceof Configurable) {
         ((Configurable)newInputFormat).setConf(conf);
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else {
         BoxedUnit var6 = BoxedUnit.UNIT;
      }

      return newInputFormat;
   }

   public Partition[] getPartitions() {
      JobConf jobConf = this.getJobConf();
      SparkHadoopUtil$.MODULE$.get().addCredentials(jobConf);
      InputSplit[] allInputSplits = this.getInputFormat(jobConf).getSplits(jobConf, this.minPartitions);
      InputSplit[] inputSplits = this.ignoreEmptySplits() ? (InputSplit[])Predef$.MODULE$.refArrayOps((Object[])allInputSplits).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(InputSplit x$1) {
            return x$1.getLength() > 0L;
         }
      }) : allInputSplits;
      Partition[] array = new Partition[Predef$.MODULE$.refArrayOps((Object[])inputSplits).size()];
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), Predef$.MODULE$.refArrayOps((Object[])inputSplits).size()).foreach$mVc$sp(new Serializable(this, inputSplits, array) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final HadoopRDD $outer;
         private final InputSplit[] inputSplits$1;
         private final Partition[] array$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.array$1[i] = new HadoopPartition(this.$outer.id(), i, this.inputSplits$1[i]);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.inputSplits$1 = inputSplits$1;
               this.array$1 = array$1;
            }
         }
      });
      return array;
   }

   public InterruptibleIterator<Tuple2<K, V>> compute(Partition theSplit, TaskContext context) {
      NextIterator iter = new NextIterator<Tuple2<K, V>>(this, theSplit, context) {
         private final HadoopPartition org$apache$spark$rdd$HadoopRDD$$anon$$split;
         private final JobConf jobConf;
         private final InputMetrics org$apache$spark$rdd$HadoopRDD$$anon$$inputMetrics;
         private final long org$apache$spark$rdd$HadoopRDD$$anon$$existingBytesRead;
         private final Option<Function0<Object>> getBytesReadCallback;
         private RecordReader<K, V> reader;
         private final InputFormat<K, V> inputFormat;
         private final K key;
         private final V value;
         // $FF: synthetic field
         private final HadoopRDD $outer;

         public HadoopPartition org$apache$spark$rdd$HadoopRDD$$anon$$split() {
            return this.org$apache$spark$rdd$HadoopRDD$$anon$$split;
         }

         private JobConf jobConf() {
            return this.jobConf;
         }

         public InputMetrics org$apache$spark$rdd$HadoopRDD$$anon$$inputMetrics() {
            return this.org$apache$spark$rdd$HadoopRDD$$anon$$inputMetrics;
         }

         public long org$apache$spark$rdd$HadoopRDD$$anon$$existingBytesRead() {
            return this.org$apache$spark$rdd$HadoopRDD$$anon$$existingBytesRead;
         }

         private Option<Function0<Object>> getBytesReadCallback() {
            return this.getBytesReadCallback;
         }

         public void org$apache$spark$rdd$HadoopRDD$$anon$$updateBytesRead() {
            this.getBytesReadCallback().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(Function0<Object> getBytesRead) {
                  this.$outer.org$apache$spark$rdd$HadoopRDD$$anon$$inputMetrics().setBytesRead(this.$outer.org$apache$spark$rdd$HadoopRDD$$anon$$existingBytesRead() + getBytesRead.apply$mcJ$sp());
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

         private RecordReader<K, V> reader() {
            return this.reader;
         }

         private void reader_$eq(RecordReader<K, V> x$1) {
            this.reader = x$1;
         }

         private InputFormat<K, V> inputFormat() {
            return this.inputFormat;
         }

         private K key() {
            return this.key;
         }

         private V value() {
            return this.value;
         }

         public Tuple2<K, V> getNext() {
            try {
               this.finished_$eq(!this.reader().next(this.key(), this.value()));
            } catch (Throwable var6) {
               label67: {
                  if (var6 instanceof IOException) {
                     IOException var3 = (IOException)var6;
                     if (this.$outer.org$apache$spark$rdd$HadoopRDD$$ignoreCorruptFiles()) {
                        this.$outer.logWarning(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Skipped the rest content in the corrupted file: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$rdd$HadoopRDD$$anon$$split().inputSplit()}));
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
                        break label67;
                     }
                  }

                  throw var6;
               }
            }

            if (!this.finished()) {
               this.org$apache$spark$rdd$HadoopRDD$$anon$$inputMetrics().incRecordsRead(1L);
            }

            if (this.org$apache$spark$rdd$HadoopRDD$$anon$$inputMetrics().recordsRead() % (long)SparkHadoopUtil$.MODULE$.UPDATE_INPUT_METRICS_INTERVAL_RECORDS() == 0L) {
               this.org$apache$spark$rdd$HadoopRDD$$anon$$updateBytesRead();
            }

            return new Tuple2<K, V>(this.key(), this.value());
         }

         public void close() {
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
                  this.org$apache$spark$rdd$HadoopRDD$$anon$$updateBytesRead();
               } else if (this.org$apache$spark$rdd$HadoopRDD$$anon$$split().inputSplit().value() instanceof FileSplit || this.org$apache$spark$rdd$HadoopRDD$$anon$$split().inputSplit().value() instanceof CombineFileSplit) {
                  try {
                     this.org$apache$spark$rdd$HadoopRDD$$anon$$inputMetrics().incBytesRead(((InputSplit)this.org$apache$spark$rdd$HadoopRDD$$anon$$split().inputSplit().value()).getLength());
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
               var10000 = this.inputFormat().getRecordReader((InputSplit)this.org$apache$spark$rdd$HadoopRDD$$anon$$split().inputSplit().value(), this.jobConf(), Reporter.NULL);
            } catch (Throwable var6) {
               if (var6 instanceof IOException) {
                  IOException var3 = (IOException)var6;
                  if (this.$outer.org$apache$spark$rdd$HadoopRDD$$ignoreCorruptFiles()) {
                     this.$outer.logWarning(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Skipped the rest content in the corrupted file: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$rdd$HadoopRDD$$anon$$split().inputSplit()}));
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

               throw var6;
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.org$apache$spark$rdd$HadoopRDD$$anon$$split = (HadoopPartition)theSplit$1;
               $outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Input split: ").append((Object)this.$outer.org$apache$spark$rdd$HadoopRDD$$anon$$split().inputSplit()).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               this.jobConf = $outer.getJobConf();
               this.org$apache$spark$rdd$HadoopRDD$$anon$$inputMetrics = context$1.taskMetrics().inputMetrics();
               this.org$apache$spark$rdd$HadoopRDD$$anon$$existingBytesRead = this.org$apache$spark$rdd$HadoopRDD$$anon$$inputMetrics().bytesRead();
               InputSplit var4 = (InputSplit)this.org$apache$spark$rdd$HadoopRDD$$anon$$split().inputSplit().value();
               if (var4 instanceof FileSplit) {
                  FileSplit var5 = (FileSplit)var4;
                  InputFileBlockHolder$.MODULE$.set(var5.getPath().toString(), var5.getStart(), var5.getLength());
                  BoxedUnit var6 = BoxedUnit.UNIT;
               } else {
                  InputFileBlockHolder$.MODULE$.unset();
                  BoxedUnit var10 = BoxedUnit.UNIT;
               }

               InputSplit var7 = (InputSplit)this.org$apache$spark$rdd$HadoopRDD$$anon$$split().inputSplit().value();
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
               this.reader = null;
               this.inputFormat = $outer.getInputFormat(this.jobConf());
               HadoopRDD$.MODULE$.addLocalConfiguration((new SimpleDateFormat("yyyyMMddHHmmss", Locale.US)).format($outer.org$apache$spark$rdd$HadoopRDD$$createTime()), context$1.stageId(), theSplit$1.index(), context$1.attemptNumber(), this.jobConf());
               this.reader_$eq(this.liftedTree1$1());
               context$1.addTaskCompletionListener((Function1)(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(TaskContext context) {
                     this.$outer.org$apache$spark$rdd$HadoopRDD$$anon$$updateBytesRead();
                     this.$outer.closeIfNeeded();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }));
               this.key = (K)(this.reader() == null ? null : this.reader().createKey());
               this.value = (V)(this.reader() == null ? null : this.reader().createValue());
            }
         }
      };
      return new InterruptibleIterator<Tuple2<K, V>>(context, iter);
   }

   @DeveloperApi
   public <U> RDD<U> mapPartitionsWithInputSplit(Function2<InputSplit, Iterator<Tuple2<K, V>>, Iterator<U>> f, boolean preservesPartitioning, ClassTag<U> evidence$1) {
      return new HadoopMapPartitionsWithSplitRDD(this, f, preservesPartitioning, evidence$1, ClassTag$.MODULE$.apply(Tuple2.class));
   }

   public <U> boolean mapPartitionsWithInputSplit$default$2() {
      return false;
   }

   public Seq<String> getPreferredLocations(Partition split) {
      InputSplit hsplit = (InputSplit)((HadoopPartition)split).inputSplit().value();
      Object locs;
      if (hsplit instanceof InputSplitWithLocationInfo) {
         InputSplitWithLocationInfo var5 = (InputSplitWithLocationInfo)hsplit;
         locs = HadoopRDD$.MODULE$.convertSplitLocationInfo(var5.getLocationInfo());
      } else {
         locs = None$.MODULE$;
      }

      return (Seq)((Option)locs).getOrElse(new Serializable(this, hsplit) {
         public static final long serialVersionUID = 0L;
         private final InputSplit hsplit$1;

         public final WrappedArray<String> apply() {
            return Predef$.MODULE$.wrapRefArray(Predef$.MODULE$.refArrayOps((Object[])this.hsplit$1.getLocations()).filter(new Serializable(this) {
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
            this.hsplit$1 = hsplit$1;
         }
      });
   }

   public void checkpoint() {
   }

   public HadoopRDD<K, V> persist(StorageLevel storageLevel) {
      if (storageLevel.deserialized()) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Caching HadoopRDDs as deserialized objects usually leads to undesired behavior because Hadoop's RecordReader reuses the same Writable object for all records. Use a map transformation to make copies of the records.";
            }
         });
      }

      return (HadoopRDD)super.persist(storageLevel);
   }

   public Configuration getConf() {
      return this.getJobConf();
   }

   public HadoopRDD(SparkContext sc, Broadcast<SerializableConfiguration> broadcastedConf, Option<Function1<JobConf, BoxedUnit>> initLocalJobConfFuncOpt, Class<? extends InputFormat<K, V>> inputFormatClass, Class<K> keyClass, Class<V> valueClass, int minPartitions) {
      super(sc, Nil$.MODULE$, ClassTag$.MODULE$.apply(Tuple2.class));
      this.broadcastedConf = broadcastedConf;
      this.org$apache$spark$rdd$HadoopRDD$$initLocalJobConfFuncOpt = initLocalJobConfFuncOpt;
      this.inputFormatClass = inputFormatClass;
      this.minPartitions = minPartitions;
      if (initLocalJobConfFuncOpt.isDefined()) {
         SparkContext qual$1 = this.sparkContext();
         Function1 x$4 = (Function1)initLocalJobConfFuncOpt.get();
         boolean x$5 = qual$1.clean$default$2();
         qual$1.clean(x$4, x$5);
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this.jobConfCacheKey = (new StringOps(Predef$.MODULE$.augmentString("rdd_%d_job_conf"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.id())}));
      this.inputFormatCacheKey = (new StringOps(Predef$.MODULE$.augmentString("rdd_%d_input_format"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.id())}));
      this.org$apache$spark$rdd$HadoopRDD$$createTime = new Date();
      this.shouldCloneJobConf = this.sparkContext().conf().getBoolean("spark.hadoop.cloneConf", false);
      this.org$apache$spark$rdd$HadoopRDD$$ignoreCorruptFiles = BoxesRunTime.unboxToBoolean(this.sparkContext().conf().get(org.apache.spark.internal.config.package$.MODULE$.IGNORE_CORRUPT_FILES()));
      this.ignoreEmptySplits = BoxesRunTime.unboxToBoolean(this.sparkContext().conf().get(org.apache.spark.internal.config.package$.MODULE$.HADOOP_RDD_IGNORE_EMPTY_SPLITS()));
   }

   public HadoopRDD(SparkContext sc, JobConf conf, Class<? extends InputFormat<K, V>> inputFormatClass, Class<K> keyClass, Class<V> valueClass, int minPartitions) {
      this(sc, sc.broadcast(new SerializableConfiguration(conf), ClassTag$.MODULE$.apply(SerializableConfiguration.class)), None$.MODULE$, inputFormatClass, keyClass, valueClass, minPartitions);
   }

   public static class HadoopMapPartitionsWithSplitRDD$ implements Serializable {
      public static final HadoopMapPartitionsWithSplitRDD$ MODULE$;

      static {
         new HadoopMapPartitionsWithSplitRDD$();
      }

      public <U, T> boolean $lessinit$greater$default$3() {
         return false;
      }

      private Object readResolve() {
         return MODULE$;
      }

      public HadoopMapPartitionsWithSplitRDD$() {
         MODULE$ = this;
      }
   }

   public static class HadoopMapPartitionsWithSplitRDD<U, T> extends RDD<U> {
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
         HadoopPartition partition = (HadoopPartition)split;
         InputSplit inputSplit = (InputSplit)partition.inputSplit().value();
         return this.f.apply(inputSplit, this.firstParent(this.evidence$3).iterator(split, context));
      }

      public HadoopMapPartitionsWithSplitRDD(RDD<T> prev, Function2<InputSplit, Iterator<T>, Iterator<U>> f, boolean preservesPartitioning, ClassTag<U> evidence$2, ClassTag<T> evidence$3) {
         super(prev, evidence$2);
         this.f = f;
         this.evidence$3 = evidence$3;
         this.partitioner = (Option<Partitioner>)(preservesPartitioning ? this.firstParent(evidence$3).partitioner() : None$.MODULE$);
      }
   }
}
