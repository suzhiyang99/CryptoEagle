package org.apache.spark.api.java;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.function.DoubleFlatMapFunction;
import org.apache.spark.api.java.function.DoubleFunction;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.FlatMapFunction2;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.partial.BoundedDouble;
import org.apache.spark.partial.PartialResult;
import org.apache.spark.rdd.RDD;
import org.apache.spark.storage.StorageLevel;
import scala.Serializable;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0015\u0005daB\u0001\u0003!\u0003\r\t!\u0004\u0002\f\u0015\u00064\u0018M\u0015#E\u0019&\\WM\u0003\u0002\u0004\t\u0005!!.\u0019<b\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001+\rqAfI\n\u0004\u0001=)\u0002C\u0001\t\u0014\u001b\u0005\t\"\"\u0001\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005Q\t\"AB!osJ+g\r\u0005\u0002\u0011-%\u0011q#\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\u00063\u0001!\tAG\u0001\u0007I%t\u0017\u000e\u001e\u0013\u0015\u0003m\u0001\"\u0001\u0005\u000f\n\u0005u\t\"\u0001B+oSRDQa\b\u0001\u0007\u0002\u0001\nqa\u001e:baJ#E\t\u0006\u0002\"eA\u0011!e\t\u0007\u0001\t\u0015!\u0003A1\u0001&\u0005\u0011!\u0006.[:\u0012\u0005\u0019J\u0003C\u0001\t(\u0013\tA\u0013CA\u0004O_RD\u0017N\\4\u0011\t)\u00021&I\u0007\u0002\u0005A\u0011!\u0005\f\u0003\u0006[\u0001\u0011\rA\f\u0002\u0002)F\u0011ae\f\t\u0003!AJ!!M\t\u0003\u0007\u0005s\u0017\u0010C\u00034=\u0001\u0007A'A\u0002sI\u0012\u00042!N\u001c,\u001b\u00051$BA\u001a\u0007\u0013\tAdGA\u0002S\t\u0012CqA\u000f\u0001C\u0002\u001b\r1(\u0001\u0005dY\u0006\u001c8\u000fV1h+\u0005a\u0004cA\u001fAW5\taH\u0003\u0002@#\u00059!/\u001a4mK\u000e$\u0018BA!?\u0005!\u0019E.Y:t)\u0006<\u0007\"B\u001a\u0001\r\u0003\u0019U#\u0001\u001b\t\u000b\u0015\u0003A\u0011\u0001$\u0002\u0015A\f'\u000f^5uS>t7/F\u0001H!\rAEJT\u0007\u0002\u0013*\u0011!jS\u0001\u0005kRLGNC\u0001\u0004\u0013\ti\u0015J\u0001\u0003MSN$\bCA(Q\u001b\u00051\u0011BA)\u0007\u0005%\u0001\u0016M\u001d;ji&|g\u000eC\u0003T\u0001\u0011\u0005A+\u0001\thKRtU/\u001c)beRLG/[8ogV\tQ\u000b\u0005\u0002\u0011-&\u0011q+\u0005\u0002\u0004\u0013:$\bf\u0001*Z?B\u0011!,X\u0007\u00027*\u0011ALB\u0001\u000bC:tw\u000e^1uS>t\u0017B\u00010\\\u0005\u0015\u0019\u0016N\\2fC\u0005\u0001\u0017!B\u0019/m9\u0002\u0004\"\u00022\u0001\t\u0003\u0019\u0017a\u00039beRLG/[8oKJ,\u0012\u0001\u001a\t\u0004U\u0015<\u0017B\u00014\u0003\u0005!y\u0005\u000f^5p]\u0006d\u0007CA(i\u0013\tIgAA\u0006QCJ$\u0018\u000e^5p]\u0016\u0014\b\"B6\u0001\t\u0003a\u0017aB2p]R,\u0007\u0010^\u000b\u0002[B\u0011qJ\\\u0005\u0003_\u001a\u0011Ab\u00159be.\u001cuN\u001c;fqRDQ!\u001d\u0001\u0005\u0002Q\u000b!!\u001b3\t\u000bM\u0004A\u0011\u0001;\u0002\u001f\u001d,Go\u0015;pe\u0006<W\rT3wK2,\u0012!\u001e\t\u0003mfl\u0011a\u001e\u0006\u0003q\u001a\tqa\u001d;pe\u0006<W-\u0003\u0002{o\na1\u000b^8sC\u001e,G*\u001a<fY\")A\u0010\u0001C\u0001{\u0006A\u0011\u000e^3sCR|'\u000fF\u0003\u007f\u0003\u0007\t9\u0001E\u0002I\u007f.J1!!\u0001J\u0005!IE/\u001a:bi>\u0014\bBBA\u0003w\u0002\u0007a*A\u0003ta2LG\u000fC\u0004\u0002\nm\u0004\r!a\u0003\u0002\u0017Q\f7o[\"p]R,\u0007\u0010\u001e\t\u0004\u001f\u00065\u0011bAA\b\r\tYA+Y:l\u0007>tG/\u001a=u\u0011\u001d\t\u0019\u0002\u0001C\u0001\u0003+\t1!\\1q+\u0011\t9\"!\t\u0015\t\u0005e\u0011Q\u0005\t\u0006U\u0005m\u0011qD\u0005\u0004\u0003;\u0011!a\u0002&bm\u0006\u0014F\t\u0012\t\u0004E\u0005\u0005BaBA\u0012\u0003#\u0011\rA\f\u0002\u0002%\"A\u0011qEA\t\u0001\u0004\tI#A\u0001g!\u001d\tY#!\r,\u0003?i!!!\f\u000b\u0007\u0005=\"!\u0001\u0005gk:\u001cG/[8o\u0013\u0011\t\u0019$!\f\u0003\u0011\u0019+hn\u0019;j_:Dq!a\u000e\u0001\t\u0003\tI$\u0001\fnCB\u0004\u0016M\u001d;ji&|gn],ji\"Le\u000eZ3y+\u0011\tY$!\u0011\u0015\r\u0005u\u00121IA-!\u0015Q\u00131DA !\r\u0011\u0013\u0011\t\u0003\b\u0003G\t)D1\u0001/\u0011!\t9#!\u000eA\u0002\u0005\u0015\u0003#CA\u0016\u0003\u000f\nYE`A,\u0013\u0011\tI%!\f\u0003\u0013\u0019+hn\u0019;j_:\u0014\u0004\u0003BA'\u0003'j!!a\u0014\u000b\u0007\u0005E3*\u0001\u0003mC:<\u0017\u0002BA+\u0003\u001f\u0012q!\u00138uK\u001e,'\u000f\u0005\u0003I\u007f\u0006}\u0002BCA.\u0003k\u0001\n\u00111\u0001\u0002^\u0005)\u0002O]3tKJ4Xm\u001d)beRLG/[8oS:<\u0007c\u0001\t\u0002`%\u0019\u0011\u0011M\t\u0003\u000f\t{w\u000e\\3b]\"9\u0011Q\r\u0001\u0005\u0002\u0005\u001d\u0014aC7baR{Gi\\;cY\u0016,B!!\u001b\u0002zQ!\u00111NA9!\rQ\u0013QN\u0005\u0004\u0003_\u0012!!\u0004&bm\u0006$u.\u001e2mKJ#E\t\u0003\u0005\u0002(\u0005\r\u0004\u0019AA:!\u0015\tY#!\u001e,\u0013\u0011\t9(!\f\u0003\u001d\u0011{WO\u00197f\rVt7\r^5p]\u00129\u00111EA2\u0005\u0004q\u0003bBA?\u0001\u0011\u0005\u0011qP\u0001\n[\u0006\u0004Hk\u001c)bSJ,b!!!\u0002\f\u0006EE\u0003BAB\u0003+\u0003rAKAC\u0003\u0013\u000by)C\u0002\u0002\b\n\u00111BS1wCB\u000b\u0017N\u001d*E\tB\u0019!%a#\u0005\u000f\u00055\u00151\u0010b\u0001]\t\u00111J\r\t\u0004E\u0005EEaBAJ\u0003w\u0012\rA\f\u0002\u0003-JB\u0001\"a\n\u0002|\u0001\u0007\u0011q\u0013\t\n\u0003W\tIjKAE\u0003\u001fKA!a'\u0002.\ta\u0001+Y5s\rVt7\r^5p]\"9\u0011q\u0014\u0001\u0005\u0002\u0005\u0005\u0016a\u00024mCRl\u0015\r]\u000b\u0005\u0003G\u000bI\u000b\u0006\u0003\u0002&\u00065\u0006#\u0002\u0016\u0002\u001c\u0005\u001d\u0006c\u0001\u0012\u0002*\u00129\u00111VAO\u0005\u0004q#!A+\t\u0011\u0005\u001d\u0012Q\u0014a\u0001\u0003_\u0003r!a\u000b\u00022.\n9+\u0003\u0003\u00024\u00065\"a\u0004$mCRl\u0015\r\u001d$v]\u000e$\u0018n\u001c8\t\u000f\u0005]\u0006\u0001\"\u0001\u0002:\u0006ya\r\\1u\u001b\u0006\u0004Hk\u001c#pk\ndW\r\u0006\u0003\u0002l\u0005m\u0006\u0002CA\u0014\u0003k\u0003\r!!0\u0011\u000b\u0005-\u0012qX\u0016\n\t\u0005\u0005\u0017Q\u0006\u0002\u0016\t>,(\r\\3GY\u0006$X*\u00199Gk:\u001cG/[8o\u0011\u001d\t)\r\u0001C\u0001\u0003\u000f\fQB\u001a7bi6\u000b\u0007\u000fV8QC&\u0014XCBAe\u0003\u001f\f\u0019\u000e\u0006\u0003\u0002L\u0006U\u0007c\u0002\u0016\u0002\u0006\u00065\u0017\u0011\u001b\t\u0004E\u0005=GaBAG\u0003\u0007\u0014\rA\f\t\u0004E\u0005MGaBAJ\u0003\u0007\u0014\rA\f\u0005\t\u0003O\t\u0019\r1\u0001\u0002XBI\u00111FAmW\u00055\u0017\u0011[\u0005\u0005\u00037\fiCA\nQC&\u0014h\t\\1u\u001b\u0006\u0004h)\u001e8di&|g\u000eC\u0004\u0002`\u0002!\t!!9\u0002\u001b5\f\u0007\u000fU1si&$\u0018n\u001c8t+\u0011\t\u0019/!;\u0015\t\u0005\u0015\u00181\u001e\t\u0006U\u0005m\u0011q\u001d\t\u0004E\u0005%HaBAV\u0003;\u0014\rA\f\u0005\t\u0003O\ti\u000e1\u0001\u0002nB9\u00111FAY}\u0006\u001d\bbBAp\u0001\u0011\u0005\u0011\u0011_\u000b\u0005\u0003g\fI\u0010\u0006\u0004\u0002v\u0006m\u0018q \t\u0006U\u0005m\u0011q\u001f\t\u0004E\u0005eHaBAV\u0003_\u0014\rA\f\u0005\t\u0003O\ty\u000f1\u0001\u0002~B9\u00111FAY}\u0006]\b\u0002CA.\u0003_\u0004\r!!\u0018\t\u000f\t\r\u0001\u0001\"\u0001\u0003\u0006\u0005)R.\u00199QCJ$\u0018\u000e^5p]N$v\u000eR8vE2,G\u0003BA6\u0005\u000fA\u0001\"a\n\u0003\u0002\u0001\u0007!\u0011\u0002\t\u0006\u0003W\tyL \u0005\b\u0005\u001b\u0001A\u0011\u0001B\b\u0003Mi\u0017\r\u001d)beRLG/[8ogR{\u0007+Y5s+\u0019\u0011\tBa\u0006\u0003\u001cQ!!1\u0003B\u000f!\u001dQ\u0013Q\u0011B\u000b\u00053\u00012A\tB\f\t\u001d\tiIa\u0003C\u00029\u00022A\tB\u000e\t\u001d\t\u0019Ja\u0003C\u00029B\u0001\"a\n\u0003\f\u0001\u0007!q\u0004\t\n\u0003W\tIN B\u000b\u00053AqAa\u0001\u0001\t\u0003\u0011\u0019\u0003\u0006\u0004\u0002l\t\u0015\"q\u0005\u0005\t\u0003O\u0011\t\u00031\u0001\u0003\n!A\u00111\fB\u0011\u0001\u0004\ti\u0006C\u0004\u0003\u000e\u0001!\tAa\u000b\u0016\r\t5\"1\u0007B\u001c)\u0019\u0011yC!\u000f\u0003>A9!&!\"\u00032\tU\u0002c\u0001\u0012\u00034\u00119\u0011Q\u0012B\u0015\u0005\u0004q\u0003c\u0001\u0012\u00038\u00119\u00111\u0013B\u0015\u0005\u0004q\u0003\u0002CA\u0014\u0005S\u0001\rAa\u000f\u0011\u0013\u0005-\u0012\u0011\u001c@\u00032\tU\u0002\u0002CA.\u0005S\u0001\r!!\u0018\t\u000f\t\u0005\u0003\u0001\"\u0001\u0003D\u0005\u0001bm\u001c:fC\u000eD\u0007+\u0019:uSRLwN\u001c\u000b\u00047\t\u0015\u0003\u0002CA\u0014\u0005\u007f\u0001\rAa\u0012\u0011\u000b\u0005-\"\u0011\n@\n\t\t-\u0013Q\u0006\u0002\r->LGMR;oGRLwN\u001c\u0005\b\u0005\u001f\u0002A\u0011\u0001B)\u0003\u00119Gn\\7\u0015\u0005\tM\u0003#\u0002\u0016\u0002\u001c\tU\u0003c\u0001%MW!9!\u0011\f\u0001\u0005\u0002\tm\u0013!C2beR,7/[1o+\u0011\u0011iFa\u0019\u0015\t\t}#Q\r\t\u0007U\u0005\u00155F!\u0019\u0011\u0007\t\u0012\u0019\u0007B\u0004\u0002,\n]#\u0019\u0001\u0018\t\u0011\t\u001d$q\u000ba\u0001\u0005S\nQa\u001c;iKJ\u0004DAa\u001b\u0003pA1!\u0006\u0001B1\u0005[\u00022A\tB8\t-\u0011\tH!\u001a\u0002\u0002\u0003\u0005)\u0011\u0001\u0018\u0003\u0007}#\u0013\u0007C\u0004\u0003v\u0001!\tAa\u001e\u0002\u000f\u001d\u0014x.\u001e9CsV!!\u0011\u0010B@)\u0011\u0011YHa\"\u0011\u000f)\n)I! \u0003\u0002B\u0019!Ea \u0005\u000f\u0005-&1\u000fb\u0001]A)\u0011Q\nBBW%!!QQA(\u0005!IE/\u001a:bE2,\u0007\u0002CA\u0014\u0005g\u0002\rA!#\u0011\u000f\u0005-\u0012\u0011G\u0016\u0003~!9!Q\u000f\u0001\u0005\u0002\t5U\u0003\u0002BH\u0005+#bA!%\u0003\u0018\nm\u0005c\u0002\u0016\u0002\u0006\nM%\u0011\u0011\t\u0004E\tUEaBAV\u0005\u0017\u0013\rA\f\u0005\t\u0003O\u0011Y\t1\u0001\u0003\u001aB9\u00111FA\u0019W\tM\u0005b\u0002BO\u0005\u0017\u0003\r!V\u0001\u000e]Vl\u0007+\u0019:uSRLwN\\:\t\u000f\t\u0005\u0006\u0001\"\u0001\u0003$\u0006!\u0001/\u001b9f)\u0011\u0011)K!.\u0011\u000b)\nYBa*\u0011\t\t%&q\u0016\b\u0004!\t-\u0016b\u0001BW#\u00051\u0001K]3eK\u001aLAA!-\u00034\n11\u000b\u001e:j]\u001eT1A!,\u0012\u0011!\u00119La(A\u0002\t\u001d\u0016aB2p[6\fg\u000e\u001a\u0005\b\u0005C\u0003A\u0011\u0001B^)\u0011\u0011)K!0\t\u0011\t]&\u0011\u0018a\u0001\u0005\u007f\u0003B\u0001\u0013'\u0003(\"9!\u0011\u0015\u0001\u0005\u0002\t\rGC\u0002BS\u0005\u000b\u00149\r\u0003\u0005\u00038\n\u0005\u0007\u0019\u0001B`\u0011!\u0011IM!1A\u0002\t-\u0017aA3omB9\u0001J!4\u0003(\n\u001d\u0016b\u0001Bh\u0013\n\u0019Q*\u00199\t\u000f\t\u0005\u0006\u0001\"\u0001\u0003TRQ!Q\u0015Bk\u0005/\u0014IN!8\t\u0011\t]&\u0011\u001ba\u0001\u0005\u007fC\u0001B!3\u0003R\u0002\u0007!1\u001a\u0005\t\u00057\u0014\t\u000e1\u0001\u0002^\u0005\u00112/\u001a9be\u0006$XmV8sW&tw\rR5s\u0011\u001d\u0011yN!5A\u0002U\u000b!BY;gM\u0016\u00148+\u001b>f\u0011\u001d\u0011\t\u000b\u0001C\u0001\u0005G$BB!*\u0003f\n\u001d(\u0011\u001eBv\u0005[D\u0001Ba.\u0003b\u0002\u0007!q\u0018\u0005\t\u0005\u0013\u0014\t\u000f1\u0001\u0003L\"A!1\u001cBq\u0001\u0004\ti\u0006C\u0004\u0003`\n\u0005\b\u0019A+\t\u0011\t=(\u0011\u001da\u0001\u0005O\u000b\u0001\"\u001a8d_\u0012Lgn\u001a\u0005\b\u0005g\u0004A\u0011\u0001B{\u0003\rQ\u0018\u000e]\u000b\u0005\u0005o\u0014i\u0010\u0006\u0003\u0003z\n}\bC\u0002\u0016\u0002\u0006.\u0012Y\u0010E\u0002#\u0005{$q!a+\u0003r\n\u0007a\u0006\u0003\u0005\u0003h\tE\b\u0019AB\u0001a\u0011\u0019\u0019aa\u0002\u0011\r)\u0002!1`B\u0003!\r\u00113q\u0001\u0003\f\u0007\u0013\u0011y0!A\u0001\u0002\u000b\u0005aFA\u0002`IIBqa!\u0004\u0001\t\u0003\u0019y!A\u0007{SB\u0004\u0016M\u001d;ji&|gn]\u000b\u0007\u0007#\u0019\u0019ca\u0006\u0015\r\rM11DB\u0016!\u0015Q\u00131DB\u000b!\r\u00113q\u0003\u0003\b\u00073\u0019YA1\u0001/\u0005\u00051\u0006\u0002\u0003B4\u0007\u0017\u0001\ra!\b1\t\r}1q\u0005\t\u0007U\u0001\u0019\tc!\n\u0011\u0007\t\u001a\u0019\u0003B\u0004\u0002,\u000e-!\u0019\u0001\u0018\u0011\u0007\t\u001a9\u0003B\u0006\u0004*\rm\u0011\u0011!A\u0001\u0006\u0003q#aA0%g!A\u0011qEB\u0006\u0001\u0004\u0019i\u0003E\u0005\u0002,\r=bpa\r\u0004\u0016%!1\u0011GA\u0017\u0005A1E.\u0019;NCB4UO\\2uS>t'\u0007\u0005\u0003I\u007f\u000e\u0005\u0002bBB\u001c\u0001\u0011\u00051\u0011H\u0001\u0010u&\u0004x+\u001b;i+:L\u0017/^3JIR\u001111\b\t\u0007U\u0005\u00155f!\u0010\u0011\t\u000553qH\u0005\u0005\u0007\u0003\nyE\u0001\u0003M_:<\u0007bBB#\u0001\u0011\u00051\u0011H\u0001\ru&\u0004x+\u001b;i\u0013:$W\r\u001f\u0005\b\u0007\u0013\u0002A\u0011AB&\u0003\u001d1wN]3bG\"$2aGB'\u0011!\t9ca\u0012A\u0002\r=\u0003#BA\u0016\u0005\u0013Z\u0003bBB*\u0001\u0011\u00051QK\u0001\bG>dG.Z2u)\t\u0011)\u0006C\u0004\u0004Z\u0001!\taa\u0017\u0002\u001fQ|Gj\\2bY&#XM]1u_J$\u0012A \u0005\b\u0007?\u0002A\u0011AB1\u0003E\u0019w\u000e\u001c7fGR\u0004\u0016M\u001d;ji&|gn\u001d\u000b\u0005\u0007G\u001aI\u0007E\u0003\u0011\u0007K\u0012)&C\u0002\u0004hE\u0011Q!\u0011:sCfD\u0001ba\u001b\u0004^\u0001\u00071QN\u0001\ra\u0006\u0014H/\u001b;j_:LEm\u001d\t\u0005!\r\u0015T\u000bC\u0004\u0004r\u0001!\taa\u001d\u0002\rI,G-^2f)\rY3Q\u000f\u0005\t\u0003O\u0019y\u00071\u0001\u0004xA9\u00111FA$W-Z\u0003bBB>\u0001\u0011\u00051QP\u0001\u000biJ,WMU3ek\u000e,G#B\u0016\u0004\u0000\r\u0005\u0005\u0002CA\u0014\u0007s\u0002\raa\u001e\t\u000f\r\r5\u0011\u0010a\u0001+\u0006)A-\u001a9uQ\"911\u0010\u0001\u0005\u0002\r\u001dEcA\u0016\u0004\n\"A\u0011qEBC\u0001\u0004\u00199\bC\u0004\u0004\u000e\u0002!\taa$\u0002\t\u0019|G\u000e\u001a\u000b\u0005\u0007#\u001b)\nF\u0002,\u0007'C\u0001\"a\n\u0004\f\u0002\u00071q\u000f\u0005\b\u0007/\u001bY\t1\u0001,\u0003%QXM]8WC2,X\rC\u0004\u0004\u001c\u0002!\ta!(\u0002\u0013\u0005<wM]3hCR,W\u0003BBP\u0007K#Ba!)\u00044R111UBT\u0007[\u00032AIBS\t\u001d\tYk!'C\u00029B\u0001b!+\u0004\u001a\u0002\u000711V\u0001\u0006g\u0016\fx\n\u001d\t\n\u0003W\t9ea),\u0007GC\u0001ba,\u0004\u001a\u0002\u00071\u0011W\u0001\u0007G>l'm\u00149\u0011\u0015\u0005-\u0012qIBR\u0007G\u001b\u0019\u000b\u0003\u0005\u0004\u0018\u000ee\u0005\u0019ABR\u0011\u001d\u00199\f\u0001C\u0001\u0007s\u000bQ\u0002\u001e:fK\u0006;wM]3hCR,W\u0003BB^\u0007\u007f#\"b!0\u0004B\u000e\r7qYBf!\r\u00113q\u0018\u0003\b\u0003W\u001b)L1\u0001/\u0011!\u00199j!.A\u0002\ru\u0006\u0002CBU\u0007k\u0003\ra!2\u0011\u0013\u0005-\u0012qIB_W\ru\u0006\u0002CBX\u0007k\u0003\ra!3\u0011\u0015\u0005-\u0012qIB_\u0007{\u001bi\fC\u0004\u0004\u0004\u000eU\u0006\u0019A+\t\u000f\r]\u0006\u0001\"\u0001\u0004PV!1\u0011[Bk)!\u0019\u0019na6\u0004Z\u000eu\u0007c\u0001\u0012\u0004V\u00129\u00111VBg\u0005\u0004q\u0003\u0002CBL\u0007\u001b\u0004\raa5\t\u0011\r%6Q\u001aa\u0001\u00077\u0004\u0012\"a\u000b\u0002H\rM7fa5\t\u0011\r=6Q\u001aa\u0001\u0007?\u0004\"\"a\u000b\u0002H\rM71[Bj\u0011\u001d\u0019\u0019\u000f\u0001C\u0001\u0007K\fQaY8v]R$\"aa:\u0011\u0007A\u0019I/C\u0002\u0004BEAqa!<\u0001\t\u0003\u0019y/A\u0006d_VtG/\u00119qe>DHCBBy\t\u0007!9\u0001\u0005\u0004\u0004t\u000ee8Q`\u0007\u0003\u0007kT1aa>\u0007\u0003\u001d\u0001\u0018M\u001d;jC2LAaa?\u0004v\ni\u0001+\u0019:uS\u0006d'+Z:vYR\u0004Baa=\u0004\u0000&!A\u0011AB{\u00055\u0011u.\u001e8eK\u0012$u.\u001e2mK\"AAQABv\u0001\u0004\u00199/A\u0004uS6,w.\u001e;\t\u0011\u0011%11\u001ea\u0001\t\u0017\t!bY8oM&$WM\\2f!\r\u0001BQB\u0005\u0004\t\u001f\t\"A\u0002#pk\ndW\rC\u0004\u0004n\u0002!\t\u0001b\u0005\u0015\t\rEHQ\u0003\u0005\t\t\u000b!\t\u00021\u0001\u0004h\"9A\u0011\u0004\u0001\u0005\u0002\u0011m\u0011\u0001D2pk:$()\u001f,bYV,GC\u0001C\u000f!\u0019A%QZ\u0016\u0004>!9A\u0011\u0005\u0001\u0005\u0002\u0011\r\u0012AE2pk:$()\u001f,bYV,\u0017\t\u001d9s_b$b\u0001\"\n\u0005*\u0011-\u0002CBBz\u0007s$9\u0003\u0005\u0004I\u0005\u001b\\3Q \u0005\t\t\u000b!y\u00021\u0001\u0004h\"AA\u0011\u0002C\u0010\u0001\u0004!Y\u0001C\u0004\u0005\"\u0001!\t\u0001b\f\u0015\t\u0011\u0015B\u0011\u0007\u0005\t\t\u000b!i\u00031\u0001\u0004h\"9AQ\u0007\u0001\u0005\u0002\u0011]\u0012\u0001\u0002;bW\u0016$BA!\u0016\u0005:!9A1\bC\u001a\u0001\u0004)\u0016a\u00018v[\"9Aq\b\u0001\u0005\u0002\u0011\u0005\u0013A\u0003;bW\u0016\u001c\u0016-\u001c9mKR1!Q\u000bC\"\t\u000fB\u0001\u0002\"\u0012\u0005>\u0001\u0007\u0011QL\u0001\u0010o&$\bNU3qY\u0006\u001cW-\\3oi\"9A1\bC\u001f\u0001\u0004)\u0006b\u0002C \u0001\u0011\u0005A1\n\u000b\t\u0005+\"i\u0005b\u0014\u0005R!AAQ\tC%\u0001\u0004\ti\u0006C\u0004\u0005<\u0011%\u0003\u0019A+\t\u0011\u0011MC\u0011\na\u0001\u0007O\fAa]3fI\"9Aq\u000b\u0001\u0005\u0002\u0011e\u0013!\u00024jeN$H#A\u0016\t\u000f\u0011u\u0003\u0001\"\u0001\u0005`\u00059\u0011n]#naRLHCAA/\u0011\u001d!\u0019\u0007\u0001C\u0001\tK\nab]1wK\u0006\u001bH+\u001a=u\r&dW\rF\u0002\u001c\tOB\u0001\u0002\"\u001b\u0005b\u0001\u0007!qU\u0001\u0005a\u0006$\b\u000eC\u0004\u0005d\u0001!\t\u0001\"\u001c\u0015\u000bm!y\u0007\"\u001d\t\u0011\u0011%D1\u000ea\u0001\u0005OC\u0001\u0002b\u001d\u0005l\u0001\u0007AQO\u0001\u0006G>$Wm\u0019\u0019\u0005\to\"y\b\u0005\u0004\u0003*\u0012eDQP\u0005\u0005\tw\u0012\u0019LA\u0003DY\u0006\u001c8\u000fE\u0002#\t\u007f\"A\u0002\"!\u0005r\u0005\u0005\t\u0011!B\u0001\t\u0007\u00131a\u0018\u00135#\r1CQ\u0011\t\u0005\t\u000f#)*\u0004\u0002\u0005\n*!A1\u0012CG\u0003!\u0019w.\u001c9sKN\u001c(\u0002\u0002CH\t#\u000b!![8\u000b\u0007\u0011M\u0005\"\u0001\u0004iC\u0012|w\u000e]\u0005\u0005\t/#II\u0001\tD_6\u0004(/Z:tS>t7i\u001c3fG\"9A1\u0014\u0001\u0005\u0002\u0011u\u0015\u0001E:bm\u0016\f5o\u00142kK\u000e$h)\u001b7f)\rYBq\u0014\u0005\t\tS\"I\n1\u0001\u0003(\"9A1\u0015\u0001\u0005\u0002\u0011\u0015\u0016!B6fs\nKX\u0003\u0002CT\t[#B\u0001\"+\u00050B1!&!\"\u0005,.\u00022A\tCW\t\u001d\tY\u000b\")C\u00029B\u0001\"a\n\u0005\"\u0002\u0007A\u0011\u0017\t\b\u0003W\t\td\u000bCV\u0011\u0019!)\f\u0001C\u00015\u0005Q1\r[3dWB|\u0017N\u001c;\t\u000f\u0011e\u0006\u0001\"\u0001\u0005<\u0006q\u0011n]\"iK\u000e\\\u0007o\\5oi\u0016$WCAA/\u0011\u001d!y\f\u0001C\u0001\t\u0003\f\u0011cZ3u\u0007\",7m\u001b9pS:$h)\u001b7f)\t!\u0019\r\u0005\u0003+K\n\u001d\u0006b\u0002Cd\u0001\u0011\u0005A\u0011Z\u0001\u000ei>$UMY;h'R\u0014\u0018N\\4\u0015\u0005\t\u001d\u0006b\u0002Cg\u0001\u0011\u0005AqZ\u0001\u0004i>\u0004HC\u0002B+\t#$\u0019\u000eC\u0004\u0005<\u0011-\u0007\u0019A+\t\u0011\u0011UG1\u001aa\u0001\t/\fAaY8naB!\u0001\n\"7,\u0013\r!Y.\u0013\u0002\u000b\u0007>l\u0007/\u0019:bi>\u0014\bb\u0002Cg\u0001\u0011\u0005Aq\u001c\u000b\u0005\u0005+\"\t\u000fC\u0004\u0005<\u0011u\u0007\u0019A+\t\u000f\u0011\u0015\b\u0001\"\u0001\u0005h\u0006YA/Y6f\u001fJ$WM]3e)\u0019\u0011)\u0006\";\u0005l\"9A1\bCr\u0001\u0004)\u0006\u0002\u0003Ck\tG\u0004\r\u0001b6\t\u000f\u0011=\b\u0001\"\u0001\u0005r\u0006\u0019Q.\u0019=\u0015\u0007-\"\u0019\u0010\u0003\u0005\u0005V\u00125\b\u0019\u0001Cl\u0011\u001d!9\u0010\u0001C\u0001\ts\f1!\\5o)\rYC1 \u0005\t\t+$)\u00101\u0001\u0005X\"9AQ\u001d\u0001\u0005\u0002\u0011}H\u0003\u0002B+\u000b\u0003Aq\u0001b\u000f\u0005~\u0002\u0007Q\u000bC\u0004\u0006\u0006\u0001!\t!b\u0002\u0002'\r|WO\u001c;BaB\u0014x\u000e\u001f#jgRLgn\u0019;\u0015\t\r\u001dX\u0011\u0002\u0005\t\u000b\u0017)\u0019\u00011\u0001\u0005\f\u0005Q!/\u001a7bi&4Xm\u0015#\t\u000f\u0015=\u0001\u0001\"\u0001\u0005J\u0006!a.Y7f\u0011\u001d)\u0019\u0002\u0001C\u0001\u000b+\t!bY8v]R\f5/\u001f8d)\t)9\u0002E\u0003+\u000b3\u0019i$C\u0002\u0006\u001c\t\u0011\u0001CS1wC\u001a+H/\u001e:f\u0003\u000e$\u0018n\u001c8\t\u000f\u0015}\u0001\u0001\"\u0001\u0006\"\u0005a1m\u001c7mK\u000e$\u0018i]=oGR\u0011Q1\u0005\t\u0006U\u0015e!Q\u000b\u0005\b\u000bO\u0001A\u0011AC\u0015\u0003%!\u0018m[3Bgft7\r\u0006\u0003\u0006$\u0015-\u0002b\u0002C\u001e\u000bK\u0001\r!\u0016\u0005\b\u000b_\u0001A\u0011AC\u0019\u000311wN]3bG\"\f5/\u001f8d)\u0011)\u0019$b\u000f\u0011\u000b)*I\"\"\u000e\u0011\t\u00055SqG\u0005\u0005\u000bs\tyE\u0001\u0003W_&$\u0007\u0002CA\u0014\u000b[\u0001\raa\u0014\t\u000f\u0015}\u0002\u0001\"\u0001\u0006B\u0005)bm\u001c:fC\u000eD\u0007+\u0019:uSRLwN\\!ts:\u001cG\u0003BC\u001a\u000b\u0007B\u0001\"a\n\u0006>\u0001\u0007!q\t\u0005\n\u000b\u000f\u0002\u0011\u0013!C\u0001\u000b\u0013\n\u0001%\\1q!\u0006\u0014H/\u001b;j_:\u001cx+\u001b;i\u0013:$W\r\u001f\u0013eK\u001a\fW\u000f\u001c;%eU!Q1JC0+\t)iE\u000b\u0003\u0002^\u0015=3FAC)!\u0011)\u0019&b\u0017\u000e\u0005\u0015U#\u0002BC,\u000b3\n\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005q\u000b\u0012\u0002BC/\u000b+\u0012\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\t\u001d\t\u0019#\"\u0012C\u00029\u0002"
)
public interface JavaRDDLike<T, This extends JavaRDDLike<T, This>> extends Serializable {
   This wrapRDD(RDD<T> var1);

   ClassTag<T> classTag();

   RDD<T> rdd();

   List<Partition> partitions();

   int getNumPartitions();

   Optional<Partitioner> partitioner();

   SparkContext context();

   int id();

   StorageLevel getStorageLevel();

   Iterator<T> iterator(Partition var1, TaskContext var2);

   <R> JavaRDD<R> map(Function<T, R> var1);

   <R> JavaRDD<R> mapPartitionsWithIndex(Function2<Integer, Iterator<T>, Iterator<R>> var1, boolean var2);

   <R> JavaDoubleRDD mapToDouble(DoubleFunction<T> var1);

   <K2, V2> JavaPairRDD<K2, V2> mapToPair(PairFunction<T, K2, V2> var1);

   <U> JavaRDD<U> flatMap(FlatMapFunction<T, U> var1);

   JavaDoubleRDD flatMapToDouble(DoubleFlatMapFunction<T> var1);

   <K2, V2> JavaPairRDD<K2, V2> flatMapToPair(PairFlatMapFunction<T, K2, V2> var1);

   <U> JavaRDD<U> mapPartitions(FlatMapFunction<Iterator<T>, U> var1);

   <U> JavaRDD<U> mapPartitions(FlatMapFunction<Iterator<T>, U> var1, boolean var2);

   <R> boolean mapPartitionsWithIndex$default$2();

   JavaDoubleRDD mapPartitionsToDouble(DoubleFlatMapFunction<Iterator<T>> var1);

   <K2, V2> JavaPairRDD<K2, V2> mapPartitionsToPair(PairFlatMapFunction<Iterator<T>, K2, V2> var1);

   JavaDoubleRDD mapPartitionsToDouble(DoubleFlatMapFunction<Iterator<T>> var1, boolean var2);

   <K2, V2> JavaPairRDD<K2, V2> mapPartitionsToPair(PairFlatMapFunction<Iterator<T>, K2, V2> var1, boolean var2);

   void foreachPartition(VoidFunction<Iterator<T>> var1);

   JavaRDD<List<T>> glom();

   <U> JavaPairRDD<T, U> cartesian(JavaRDDLike<U, ?> var1);

   <U> JavaPairRDD<U, Iterable<T>> groupBy(Function<T, U> var1);

   <U> JavaPairRDD<U, Iterable<T>> groupBy(Function<T, U> var1, int var2);

   JavaRDD<String> pipe(String var1);

   JavaRDD<String> pipe(List<String> var1);

   JavaRDD<String> pipe(List<String> var1, Map<String, String> var2);

   JavaRDD<String> pipe(List<String> var1, Map<String, String> var2, boolean var3, int var4);

   JavaRDD<String> pipe(List<String> var1, Map<String, String> var2, boolean var3, int var4, String var5);

   <U> JavaPairRDD<T, U> zip(JavaRDDLike<U, ?> var1);

   <U, V> JavaRDD<V> zipPartitions(JavaRDDLike<U, ?> var1, FlatMapFunction2<Iterator<T>, Iterator<U>, V> var2);

   JavaPairRDD<T, Long> zipWithUniqueId();

   JavaPairRDD<T, Long> zipWithIndex();

   void foreach(VoidFunction<T> var1);

   List<T> collect();

   Iterator<T> toLocalIterator();

   List<T>[] collectPartitions(int[] var1);

   T reduce(Function2<T, T, T> var1);

   T treeReduce(Function2<T, T, T> var1, int var2);

   T treeReduce(Function2<T, T, T> var1);

   T fold(T var1, Function2<T, T, T> var2);

   <U> U aggregate(U var1, Function2<U, T, U> var2, Function2<U, U, U> var3);

   <U> U treeAggregate(U var1, Function2<U, T, U> var2, Function2<U, U, U> var3, int var4);

   <U> U treeAggregate(U var1, Function2<U, T, U> var2, Function2<U, U, U> var3);

   long count();

   PartialResult<BoundedDouble> countApprox(long var1, double var3);

   PartialResult<BoundedDouble> countApprox(long var1);

   Map<T, Long> countByValue();

   PartialResult<Map<T, BoundedDouble>> countByValueApprox(long var1, double var3);

   PartialResult<Map<T, BoundedDouble>> countByValueApprox(long var1);

   List<T> take(int var1);

   List<T> takeSample(boolean var1, int var2);

   List<T> takeSample(boolean var1, int var2, long var3);

   T first();

   boolean isEmpty();

   void saveAsTextFile(String var1);

   void saveAsTextFile(String var1, Class<? extends CompressionCodec> var2);

   void saveAsObjectFile(String var1);

   <U> JavaPairRDD<U, T> keyBy(Function<T, U> var1);

   void checkpoint();

   boolean isCheckpointed();

   Optional<String> getCheckpointFile();

   String toDebugString();

   List<T> top(int var1, Comparator<T> var2);

   List<T> top(int var1);

   List<T> takeOrdered(int var1, Comparator<T> var2);

   T max(Comparator<T> var1);

   T min(Comparator<T> var1);

   List<T> takeOrdered(int var1);

   long countApproxDistinct(double var1);

   String name();

   JavaFutureAction<Long> countAsync();

   JavaFutureAction<List<T>> collectAsync();

   JavaFutureAction<List<T>> takeAsync(int var1);

   JavaFutureAction<Void> foreachAsync(VoidFunction<T> var1);

   JavaFutureAction<Void> foreachPartitionAsync(VoidFunction<Iterator<T>> var1);
}
