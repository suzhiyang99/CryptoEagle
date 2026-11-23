package org.apache.spark.util.collection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.apache.spark.Aggregator;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.executor.ShuffleWriteMetrics;
import org.apache.spark.serializer.DeserializationStream;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.DiskBlockManager;
import org.apache.spark.storage.DiskBlockObjectWriter;
import org.apache.spark.storage.FileSegment;
import org.apache.spark.storage.TempShuffleBlockId;
import org.spark_project.guava.io.ByteStreams;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Product2;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple4;
import scala.collection.BufferedIterator;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Traversable;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce$;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.PriorityQueue;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.math.Ordering$class;
import scala.math.PartialOrdering$class;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction4;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.LongRef;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0015\rh!B\u0001\u0003\u0001\u0019a!AD#yi\u0016\u0014h.\u00197T_J$XM\u001d\u0006\u0003\u0007\u0011\t!bY8mY\u0016\u001cG/[8o\u0015\t)a!\u0001\u0003vi&d'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0016\t59B(J\n\u0004\u000199\u0003cA\b\u0011%5\t!!\u0003\u0002\u0012\u0005\tI1\u000b]5mY\u0006\u0014G.\u001a\t\u0005\u001fM)B%\u0003\u0002\u0015\u0005\t\tsK]5uC\ndW\rU1si&$\u0018n\u001c8fIB\u000b\u0017N]\"pY2,7\r^5p]B\u0011ac\u0006\u0007\u0001\t\u0015A\u0002A1\u0001\u001b\u0005\u0005Y5\u0001A\t\u00037\u0005\u0002\"\u0001H\u0010\u000e\u0003uQ\u0011AH\u0001\u0006g\u000e\fG.Y\u0005\u0003Au\u0011qAT8uQ&tw\r\u0005\u0002\u001dE%\u00111%\b\u0002\u0004\u0003:L\bC\u0001\f&\t\u00151\u0003A1\u0001\u001b\u0005\u0005\u0019\u0005C\u0001\u0015,\u001b\u0005I#B\u0001\u0016\u0007\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u0017*\u0005\u001daunZ4j]\u001eD\u0001B\f\u0001\u0003\u0002\u0003\u0006IaL\u0001\bG>tG/\u001a=u!\t\u0001\u0014'D\u0001\u0007\u0013\t\u0011dAA\u0006UCN\\7i\u001c8uKb$\b\u0002\u0003\u001b\u0001\u0005\u0003\u0005\u000b\u0011B\u001b\u0002\u0015\u0005<wM]3hCR|'\u000fE\u0002\u001dmaJ!aN\u000f\u0003\r=\u0003H/[8o!\u0015\u0001\u0014(F\u001e%\u0013\tQdA\u0001\u0006BO\u001e\u0014XmZ1u_J\u0004\"A\u0006\u001f\u0005\u000bu\u0002!\u0019\u0001\u000e\u0003\u0003YC\u0001b\u0010\u0001\u0003\u0002\u0003\u0006I\u0001Q\u0001\fa\u0006\u0014H/\u001b;j_:,'\u000fE\u0002\u001dm\u0005\u0003\"\u0001\r\"\n\u0005\r3!a\u0003)beRLG/[8oKJD\u0001\"\u0012\u0001\u0003\u0002\u0003\u0006IAR\u0001\t_J$WM]5oOB\u0019ADN$\u0011\u0007!\u0003VC\u0004\u0002J\u001d:\u0011!*T\u0007\u0002\u0017*\u0011A*G\u0001\u0007yI|w\u000e\u001e \n\u0003yI!aT\u000f\u0002\u000fA\f7m[1hK&\u0011\u0011K\u0015\u0002\t\u001fJ$WM]5oO*\u0011q*\b\u0005\t)\u0002\u0011\t\u0011)A\u0005+\u0006Q1/\u001a:jC2L'0\u001a:\u0011\u0005YCV\"A,\u000b\u0005Q3\u0011BA-X\u0005)\u0019VM]5bY&TXM\u001d\u0005\u00067\u0002!\t\u0001X\u0001\u0007y%t\u0017\u000e\u001e \u0015\rusv\fY1c!\u0015y\u0001!F\u001e%\u0011\u0015q#\f1\u00010\u0011\u001d!$\f%AA\u0002UBqa\u0010.\u0011\u0002\u0003\u0007\u0001\tC\u0004F5B\u0005\t\u0019\u0001$\t\u000fQS\u0006\u0013!a\u0001+\"9A\r\u0001b\u0001\n\u0013)\u0017\u0001B2p]\u001a,\u0012A\u001a\t\u0003a\u001dL!\u0001\u001b\u0004\u0003\u0013M\u0003\u0018M]6D_:4\u0007B\u00026\u0001A\u0003%a-A\u0003d_:4\u0007\u0005C\u0004m\u0001\t\u0007I\u0011B7\u0002\u001b9,X\u000eU1si&$\u0018n\u001c8t+\u0005q\u0007C\u0001\u000fp\u0013\t\u0001XDA\u0002J]RDaA\u001d\u0001!\u0002\u0013q\u0017A\u00048v[B\u000b'\u000f^5uS>t7\u000f\t\u0005\bi\u0002\u0011\r\u0011\"\u0003v\u0003=\u0019\bn\\;mIB\u000b'\u000f^5uS>tW#\u0001<\u0011\u0005q9\u0018B\u0001=\u001e\u0005\u001d\u0011un\u001c7fC:DaA\u001f\u0001!\u0002\u00131\u0018\u0001E:i_VdG\rU1si&$\u0018n\u001c8!\u0011\u0015a\b\u0001\"\u0003~\u000319W\r\u001e)beRLG/[8o)\tqg\u0010C\u0003\u0000w\u0002\u0007Q#A\u0002lKfD\u0011\"a\u0001\u0001\u0005\u0004%I!!\u0002\u0002\u0019\tdwnY6NC:\fw-\u001a:\u0016\u0005\u0005\u001d\u0001\u0003BA\u0005\u0003\u001fi!!a\u0003\u000b\u0007\u00055a!A\u0004ti>\u0014\u0018mZ3\n\t\u0005E\u00111\u0002\u0002\r\u00052|7m['b]\u0006<WM\u001d\u0005\t\u0003+\u0001\u0001\u0015!\u0003\u0002\b\u0005i!\r\\8dW6\u000bg.Y4fe\u0002B\u0011\"!\u0007\u0001\u0005\u0004%I!a\u0007\u0002!\u0011L7o\u001b\"m_\u000e\\W*\u00198bO\u0016\u0014XCAA\u000f!\u0011\tI!a\b\n\t\u0005\u0005\u00121\u0002\u0002\u0011\t&\u001c8N\u00117pG.l\u0015M\\1hKJD\u0001\"!\n\u0001A\u0003%\u0011QD\u0001\u0012I&\u001c8N\u00117pG.l\u0015M\\1hKJ\u0004\u0003\"CA\u0015\u0001\t\u0007I\u0011BA\u0016\u0003E\u0019XM]5bY&TXM]'b]\u0006<WM]\u000b\u0003\u0003[\u00012AVA\u0018\u0013\r\t\td\u0016\u0002\u0012'\u0016\u0014\u0018.\u00197ju\u0016\u0014X*\u00198bO\u0016\u0014\b\u0002CA\u001b\u0001\u0001\u0006I!!\f\u0002%M,'/[1mSj,'/T1oC\u001e,'\u000f\t\u0005\n\u0003s\u0001!\u0019!C\u0005\u0003w\t1b]3s\u0013:\u001cH/\u00198dKV\u0011\u0011Q\b\t\u0004-\u0006}\u0012bAA!/\n\u00112+\u001a:jC2L'0\u001a:J]N$\u0018M\\2f\u0011!\t)\u0005\u0001Q\u0001\n\u0005u\u0012\u0001D:fe&s7\u000f^1oG\u0016\u0004\u0003\u0002CA%\u0001\t\u0007I\u0011B7\u0002\u001d\u0019LG.\u001a\"vM\u001a,'oU5{K\"9\u0011Q\n\u0001!\u0002\u0013q\u0017a\u00044jY\u0016\u0014UO\u001a4feNK'0\u001a\u0011\t\u0013\u0005E\u0003A1A\u0005\n\u0005M\u0013aE:fe&\fG.\u001b>fe\n\u000bGo\u00195TSj,WCAA+!\ra\u0012qK\u0005\u0004\u00033j\"\u0001\u0002'p]\u001eD\u0001\"!\u0018\u0001A\u0003%\u0011QK\u0001\u0015g\u0016\u0014\u0018.\u00197ju\u0016\u0014()\u0019;dQNK'0\u001a\u0011\t\u0013\u0005\u0005\u0004\u00011A\u0005\n\u0005\r\u0014aA7baV\u0011\u0011Q\r\t\u0006\u001f\u0005\u001dT\u0003J\u0005\u0004\u0003S\u0012!\u0001\u0007)beRLG/[8oK\u0012\f\u0005\u000f]3oI>sG._'ba\"I\u0011Q\u000e\u0001A\u0002\u0013%\u0011qN\u0001\b[\u0006\u0004x\fJ3r)\u0011\t\t(a\u001e\u0011\u0007q\t\u0019(C\u0002\u0002vu\u0011A!\u00168ji\"Q\u0011\u0011PA6\u0003\u0003\u0005\r!!\u001a\u0002\u0007a$\u0013\u0007\u0003\u0005\u0002~\u0001\u0001\u000b\u0015BA3\u0003\u0011i\u0017\r\u001d\u0011)\t\u0005m\u0014\u0011\u0011\t\u00049\u0005\r\u0015bAAC;\tAao\u001c7bi&dW\rC\u0005\u0002\n\u0002\u0001\r\u0011\"\u0003\u0002\f\u00061!-\u001e4gKJ,\"!!$\u0011\u000b=\ty)\u0006\u0013\n\u0007\u0005E%AA\u000bQCJ$\u0018\u000e^5p]\u0016$\u0007+Y5s\u0005V4g-\u001a:\t\u0013\u0005U\u0005\u00011A\u0005\n\u0005]\u0015A\u00032vM\u001a,'o\u0018\u0013fcR!\u0011\u0011OAM\u0011)\tI(a%\u0002\u0002\u0003\u0007\u0011Q\u0012\u0005\t\u0003;\u0003\u0001\u0015)\u0003\u0002\u000e\u00069!-\u001e4gKJ\u0004\u0003\u0006BAN\u0003\u0003C\u0011\"a)\u0001\u0001\u0004%I!a\u0015\u0002#}#\u0017n]6CsR,7o\u00159jY2,G\rC\u0005\u0002(\u0002\u0001\r\u0011\"\u0003\u0002*\u0006)r\fZ5tW\nKH/Z:Ta&dG.\u001a3`I\u0015\fH\u0003BA9\u0003WC!\"!\u001f\u0002&\u0006\u0005\t\u0019AA+\u0011!\ty\u000b\u0001Q!\n\u0005U\u0013AE0eSN\\')\u001f;fgN\u0003\u0018\u000e\u001c7fI\u0002Bq!a-\u0001\t\u0003\t\u0019&\u0001\teSN\\')\u001f;fgN\u0003\u0018\u000e\u001c7fI\"I\u0011q\u0017\u0001A\u0002\u0013%\u00111K\u0001\u0015?B,\u0017m['f[>\u0014\u00180V:fI\nKH/Z:\t\u0013\u0005m\u0006\u00011A\u0005\n\u0005u\u0016\u0001G0qK\u0006\\W*Z7pef,6/\u001a3CsR,7o\u0018\u0013fcR!\u0011\u0011OA`\u0011)\tI(!/\u0002\u0002\u0003\u0007\u0011Q\u000b\u0005\t\u0003\u0007\u0004\u0001\u0015)\u0003\u0002V\u0005)r\f]3bW6+Wn\u001c:z+N,GMQ=uKN\u0004\u0003bBAd\u0001\u0011\u0005\u00111K\u0001\u0014a\u0016\f7.T3n_JLXk]3e\u0005f$Xm\u001d\u0005\t\u0003\u0017\u0004\u0001\u0019!C\u0005k\u0006i\u0011n]*ik\u001a4G.Z*peRD\u0011\"a4\u0001\u0001\u0004%I!!5\u0002#%\u001c8\u000b[;gM2,7k\u001c:u?\u0012*\u0017\u000f\u0006\u0003\u0002r\u0005M\u0007\"CA=\u0003\u001b\f\t\u00111\u0001w\u0011\u001d\t9\u000e\u0001Q!\nY\fa\"[:TQV4g\r\\3T_J$\b\u0005\u000b\u0003\u0002V\u0006\u0005\u0005\"CAo\u0001\t\u0007I\u0011BAp\u0003=1wN]2f'BLG\u000e\u001c$jY\u0016\u001cXCAAq!\u0019\t\u0019/a;\u0002p6\u0011\u0011Q\u001d\u0006\u0005\u0003O\fI/A\u0004nkR\f'\r\\3\u000b\u0005\ri\u0012\u0002BAw\u0003K\u00141\"\u0011:sCf\u0014UO\u001a4feB!\u0011\u0011_Az\u001b\u0005\u0001a\u0001CA{\u0001\u0001\u0006I)a>\u0003\u0017M\u0003\u0018\u000e\u001c7fI\u001aKG.Z\n\t\u0003g\fI0a@\u0003\u0006A\u0019A$a?\n\u0007\u0005uXD\u0001\u0004B]f\u0014VM\u001a\t\u00049\t\u0005\u0011b\u0001B\u0002;\t9\u0001K]8ek\u000e$\bc\u0001\u000f\u0003\b%\u0019!\u0011B\u000f\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0017\t5\u00111\u001fBK\u0002\u0013\u0005!qB\u0001\u0005M&dW-\u0006\u0002\u0003\u0012A!!1\u0003B\u000f\u001b\t\u0011)B\u0003\u0003\u0003\u0018\te\u0011AA5p\u0015\t\u0011Y\"\u0001\u0003kCZ\f\u0017\u0002\u0002B\u0010\u0005+\u0011AAR5mK\"Y!1EAz\u0005#\u0005\u000b\u0011\u0002B\t\u0003\u00151\u0017\u000e\\3!\u0011-\u00119#a=\u0003\u0016\u0004%\tA!\u000b\u0002\u000f\tdwnY6JIV\u0011!1\u0006\t\u0005\u0003\u0013\u0011i#\u0003\u0003\u00030\u0005-!a\u0002\"m_\u000e\\\u0017\n\u001a\u0005\f\u0005g\t\u0019P!E!\u0002\u0013\u0011Y#\u0001\u0005cY>\u001c7.\u00133!\u0011-\u00119$a=\u0003\u0016\u0004%\tA!\u000f\u0002)M,'/[1mSj,'OQ1uG\"\u001c\u0016N_3t+\t\u0011Y\u0004E\u0003\u001d\u0005{\t)&C\u0002\u0003@u\u0011Q!\u0011:sCfD1Ba\u0011\u0002t\nE\t\u0015!\u0003\u0003<\u0005)2/\u001a:jC2L'0\u001a:CCR\u001c\u0007nU5{KN\u0004\u0003b\u0003B$\u0003g\u0014)\u001a!C\u0001\u0005s\tA#\u001a7f[\u0016tGo\u001d)feB\u000b'\u000f^5uS>t\u0007b\u0003B&\u0003g\u0014\t\u0012)A\u0005\u0005w\tQ#\u001a7f[\u0016tGo\u001d)feB\u000b'\u000f^5uS>t\u0007\u0005C\u0004\\\u0003g$\tAa\u0014\u0015\u0015\u0005=(\u0011\u000bB*\u0005+\u00129\u0006\u0003\u0005\u0003\u000e\t5\u0003\u0019\u0001B\t\u0011!\u00119C!\u0014A\u0002\t-\u0002\u0002\u0003B\u001c\u0005\u001b\u0002\rAa\u000f\t\u0011\t\u001d#Q\na\u0001\u0005wA!Ba\u0017\u0002t\u0006\u0005I\u0011\u0001B/\u0003\u0011\u0019w\u000e]=\u0015\u0015\u0005=(q\fB1\u0005G\u0012)\u0007\u0003\u0006\u0003\u000e\te\u0003\u0013!a\u0001\u0005#A!Ba\n\u0003ZA\u0005\t\u0019\u0001B\u0016\u0011)\u00119D!\u0017\u0011\u0002\u0003\u0007!1\b\u0005\u000b\u0005\u000f\u0012I\u0006%AA\u0002\tm\u0002B\u0003B5\u0003g\f\n\u0011\"\u0001\u0003l\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nTC\u0001B7U\u0011\u0011\tBa\u001c,\u0005\tE\u0004\u0003\u0002B:\u0005{j!A!\u001e\u000b\t\t]$\u0011P\u0001\nk:\u001c\u0007.Z2lK\u0012T1Aa\u001f\u001e\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0005\u007f\u0012)HA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016D!Ba!\u0002tF\u0005I\u0011\u0001BC\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*\"Aa\"+\t\t-\"q\u000e\u0005\u000b\u0005\u0017\u000b\u00190%A\u0005\u0002\t5\u0015AD2paf$C-\u001a4bk2$HeM\u000b\u0003\u0005\u001fSCAa\u000f\u0003p!Q!1SAz#\u0003%\tA!$\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%i!Q!qSAz\u0003\u0003%\tE!'\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\t\u0011Y\n\u0005\u0003\u0003\u001e\n\rVB\u0001BP\u0015\u0011\u0011\tK!\u0007\u0002\t1\fgnZ\u0005\u0005\u0005K\u0013yJ\u0001\u0004TiJLgn\u001a\u0005\n\u0005S\u000b\u00190!A\u0005\u00025\fA\u0002\u001d:pIV\u001cG/\u0011:jifD!B!,\u0002t\u0006\u0005I\u0011\u0001BX\u00039\u0001(o\u001c3vGR,E.Z7f]R$2!\tBY\u0011%\tIHa+\u0002\u0002\u0003\u0007a\u000e\u0003\u0006\u00036\u0006M\u0018\u0011!C!\u0005o\u000bq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0005s\u0003RAa/\u0003>\u0006j!!!;\n\t\t}\u0016\u0011\u001e\u0002\t\u0013R,'/\u0019;pe\"Q!1YAz\u0003\u0003%\tA!2\u0002\u0011\r\fg.R9vC2$2A\u001eBd\u0011%\tIH!1\u0002\u0002\u0003\u0007\u0011\u0005\u0003\u0006\u0003L\u0006M\u0018\u0011!C!\u0005\u001b\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002]\"Q!\u0011[Az\u0003\u0003%\tEa5\u0002\u0011Q|7\u000b\u001e:j]\u001e$\"Aa'\t\u0015\t]\u00171_A\u0001\n\u0003\u0012I.\u0001\u0004fcV\fGn\u001d\u000b\u0004m\nm\u0007\"CA=\u0005+\f\t\u00111\u0001\"\u0011!\u0011y\u000e\u0001Q\u0001\n\u0005\u0005\u0018\u0001\u00054pe\u000e,7\u000b]5mY\u001aKG.Z:!\u0011%\u0011\u0019\u000f\u0001a\u0001\n\u0013\u0011)/A\bsK\u0006$\u0017N\\4Ji\u0016\u0014\u0018\r^8s+\t\u00119\u000f\u0005\u0003\u0002r\n%h\u0001\u0003Bv\u0001\u0001\u0006IA!<\u0003#M\u0003\u0018\u000e\u001c7bE2,\u0017\n^3sCR|'o\u0005\u0004\u0003j\u0006e(q\u001e\t\u0006\u0011\nE(1_\u0005\u0004\u0005\u007f\u0013\u0006C\u0002\u000f\u0003v\neH%C\u0002\u0003xv\u0011a\u0001V;qY\u0016\u0014\u0004#\u0002\u000f\u0003v:,\u0002b\u0003B\u007f\u0005S\u0014\t\u0019!C\u0001\u0005\u007f\f\u0001\"\u001e9tiJ,\u0017-\\\u000b\u0003\u0005_D1ba\u0001\u0003j\n\u0005\r\u0011\"\u0001\u0004\u0006\u0005aQ\u000f]:ue\u0016\fWn\u0018\u0013fcR!\u0011\u0011OB\u0004\u0011)\tIh!\u0001\u0002\u0002\u0003\u0007!q\u001e\u0005\f\u0007\u0017\u0011IO!A!B\u0013\u0011y/A\u0005vaN$(/Z1nA!91L!;\u0005\u0002\r=A\u0003\u0002Bt\u0007#A\u0001B!@\u0004\u000e\u0001\u0007!q\u001e\u0005\u000b\u0007+\u0011IO1A\u0005\n\r]\u0011AC*Q\u00132cu\fT(D\u0017V\u00111\u0011\u0004\t\u0005\u0005;\u001bY\"\u0003\u0003\u0004\u001e\t}%AB(cU\u0016\u001cG\u000fC\u0005\u0004\"\t%\b\u0015!\u0003\u0004\u001a\u0005Y1\u000bU%M\u0019~cujQ&!\u0011)\u0019)C!;A\u0002\u0013%!q`\u0001\r]\u0016DH/\u00169tiJ,\u0017-\u001c\u0005\u000b\u0007S\u0011I\u000f1A\u0005\n\r-\u0012\u0001\u00058fqR,\u0006o\u001d;sK\u0006lw\fJ3r)\u0011\t\th!\f\t\u0015\u0005e4qEA\u0001\u0002\u0004\u0011y\u000fC\u0005\u00042\t%\b\u0015)\u0003\u0003p\u0006ia.\u001a=u+B\u001cHO]3b[\u0002B!b!\u000e\u0003j\u0002\u0007I\u0011BB\u001c\u0003\r\u0019WO]\u000b\u0003\u0005gD!ba\u000f\u0003j\u0002\u0007I\u0011BB\u001f\u0003\u001d\u0019WO]0%KF$B!!\u001d\u0004@!Q\u0011\u0011PB\u001d\u0003\u0003\u0005\rAa=\t\u0013\r\r#\u0011\u001eQ!\n\tM\u0018\u0001B2ve\u0002B\u0011ba\u0012\u0003j\u0002\u0007I\u0011B;\u0002\u0015!\f7o\u00159jY2,G\r\u0003\u0006\u0004L\t%\b\u0019!C\u0005\u0007\u001b\na\u0002[1t'BLG\u000e\\3e?\u0012*\u0017\u000f\u0006\u0003\u0002r\r=\u0003\"CA=\u0007\u0013\n\t\u00111\u0001w\u0011!\u0019\u0019F!;!B\u00131\u0018a\u00035bgN\u0003\u0018\u000e\u001c7fI\u0002B\u0001ba\u0016\u0003j\u0012\u00051\u0011L\u0001\u0006gBLG\u000e\u001c\u000b\u0002m\"A1Q\fBu\t\u0003\u0019y&\u0001\u0005sK\u0006$g*\u001a=u)\t\u0011\u0019\u0010\u0003\u0005\u0004d\t%H\u0011IB-\u0003\u001dA\u0017m\u001d(fqRD\u0001ba\u001a\u0003j\u0012\u00053qL\u0001\u0005]\u0016DH\u000fC\u0005\u0004l\u0001\u0001\r\u0011\"\u0003\u0004n\u0005\u0019\"/Z1eS:<\u0017\n^3sCR|'o\u0018\u0013fcR!\u0011\u0011OB8\u0011)\tIh!\u001b\u0002\u0002\u0003\u0007!q\u001d\u0005\t\u0007g\u0002\u0001\u0015)\u0003\u0003h\u0006\u0001\"/Z1eS:<\u0017\n^3sCR|'\u000f\t\u0015\u0005\u0007c\n\t\tC\u0005\u0004z\u0001\u0011\r\u0011\"\u0003\u0004|\u0005i1.Z=D_6\u0004\u0018M]1u_J,\"a! \u0011\u000b\r}41Q\u000b\u000e\u0005\r\u0005%bA\u0003\u0003\u001a%!1QQBA\u0005)\u0019u.\u001c9be\u0006$xN\u001d\u0005\t\u0007\u0013\u0003\u0001\u0015!\u0003\u0004~\u0005q1.Z=D_6\u0004\u0018M]1u_J\u0004\u0003bBBG\u0001\u0011%1qR\u0001\u000bG>l\u0007/\u0019:bi>\u0014XCABI!\u0011abg! \b\u0013\rU\u0005!!Q\t\n\r]\u0015aC*qS2dW\r\u001a$jY\u0016\u0004B!!=\u0004\u001a\u001aI\u0011Q\u001f\u0001\u0002B#%11T\n\u0007\u00073\u001biJ!\u0002\u0011\u001d\r}5Q\u0015B\t\u0005W\u0011YDa\u000f\u0002p6\u00111\u0011\u0015\u0006\u0004\u0007Gk\u0012a\u0002:v]RLW.Z\u0005\u0005\u0007O\u001b\tKA\tBEN$(/Y2u\rVt7\r^5p]RBqaWBM\t\u0003\u0019Y\u000b\u0006\u0002\u0004\u0018\"Q!\u0011[BM\u0003\u0003%)Ea5\t\u0015\rE6\u0011TA\u0001\n\u0003\u001b\u0019,A\u0003baBd\u0017\u0010\u0006\u0006\u0002p\u000eU6qWB]\u0007wC\u0001B!\u0004\u00040\u0002\u0007!\u0011\u0003\u0005\t\u0005O\u0019y\u000b1\u0001\u0003,!A!qGBX\u0001\u0004\u0011Y\u0004\u0003\u0005\u0003H\r=\u0006\u0019\u0001B\u001e\u0011)\u0019yl!'\u0002\u0002\u0013\u00055\u0011Y\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\u0019\u0019ma3\u0011\tq14Q\u0019\t\f9\r\u001d'\u0011\u0003B\u0016\u0005w\u0011Y$C\u0002\u0004Jv\u0011a\u0001V;qY\u0016$\u0004BCBg\u0007{\u000b\t\u00111\u0001\u0002p\u0006\u0019\u0001\u0010\n\u0019\t\u0013\rE\u0007A1A\u0005\n\u0005}\u0017AB:qS2d7\u000f\u0003\u0005\u0004V\u0002\u0001\u000b\u0011BAq\u0003\u001d\u0019\b/\u001b7mg\u0002Bqa!7\u0001\t\u00031Q.A\u0005ok6\u001c\u0006/\u001b7mg\"91Q\u001c\u0001\u0005\u0002\r}\u0017!C5og\u0016\u0014H/\u00117m)\u0011\t\th!9\t\u0011\r\r81\u001ca\u0001\u0007K\fqA]3d_J$7\u000fE\u0003I\u0005c\u001c9\u000fE\u0003\u001d\u0007S,2(C\u0002\u0004lv\u0011\u0001\u0002\u0015:pIV\u001cGO\r\u0005\b\u0007_\u0004A\u0011BBy\u0003Qi\u0017-\u001f2f'BLG\u000e\\\"pY2,7\r^5p]R!\u0011\u0011OBz\u0011\u001d\u0019)p!<A\u0002Y\f\u0001\"^:j]\u001el\u0015\r\u001d\u0005\t\u0007/\u0002\u0001\u0015\"\u0015\u0004zR!\u0011\u0011OB~\u0011\u0019\u00191q\u001fa\u0001%!A1q \u0001!\n#\u001aI&\u0001\u0006g_J\u001cWm\u00159jY2D\u0001\u0002b\u0001\u0001A\u0013%AQA\u0001\u001agBLG\u000e\\'f[>\u0014\u00180\u0013;fe\u0006$xN\u001d+p\t&\u001c8\u000e\u0006\u0003\u0002p\u0012\u001d\u0001\u0002\u0003C\u0005\t\u0003\u0001\r\u0001b\u0003\u0002!%tW*Z7pefLE/\u001a:bi>\u0014\bcA\b\u0005\u000e%\u0019Aq\u0002\u0002\u00037]\u0013\u0018\u000e^1cY\u0016\u0004\u0016M\u001d;ji&|g.\u001a3Ji\u0016\u0014\u0018\r^8s\u0011\u001d!\u0019\u0002\u0001C\u0005\t+\tQ!\\3sO\u0016$b\u0001b\u0006\u0005 \u0011\u001d\u0002#\u0002%\u0003r\u0012e\u0001C\u0002\u000f\u0003v:$Y\u0002E\u0003I\u0005c$i\u0002E\u0003\u001d\u0007S,B\u0005\u0003\u0005\u0004R\u0012E\u0001\u0019\u0001C\u0011!\u0015AE1EAx\u0013\r!)C\u0015\u0002\u0004'\u0016\f\b\u0002\u0003C\u0015\t#\u0001\rAa<\u0002\u0011%tW*Z7pefDq\u0001\"\f\u0001\t\u0013!y#A\u0005nKJ<WmU8siR1A1\u0004C\u0019\toA\u0001\u0002b\r\u0005,\u0001\u0007AQG\u0001\nSR,'/\u0019;peN\u0004R\u0001\u0013C\u0012\t7A\u0001b!$\u0005,\u0001\u00071Q\u0010\u0005\b\tw\u0001A\u0011\u0002C\u001f\u0003QiWM]4f/&$\b.Q4he\u0016<\u0017\r^5p]RQA1\u0004C \t\u0003\"Y\u0005\"\u0014\t\u0011\u0011MB\u0011\ba\u0001\tkA\u0001\u0002b\u0011\u0005:\u0001\u0007AQI\u0001\u000f[\u0016\u0014x-Z\"p[\nLg.\u001a:t!\u0019aBq\t\u0013%I%\u0019A\u0011J\u000f\u0003\u0013\u0019+hn\u0019;j_:\u0014\u0004\u0002CBG\ts\u0001\ra! \t\u000f\u0011=C\u0011\ba\u0001m\u0006QAo\u001c;bY>\u0013H-\u001a:\u0007\u0011\u0011M\u0003\u0001)A\u0005\t+\u00121b\u00159jY2\u0014V-\u00193feN!A\u0011KA}\u0011-\u00199\u0006\"\u0015\u0003\u0002\u0003\u0006I!a<\t\u000fm#\t\u0006\"\u0001\u0005\\Q!AQ\fC0!\u0011\t\t\u0010\"\u0015\t\u0011\r]C\u0011\fa\u0001\u0003_D!\u0002b\u0019\u0005R\t\u0007I\u0011\u0001B\u001d\u00031\u0011\u0017\r^2i\u001f\u001a47/\u001a;t\u0011%!9\u0007\"\u0015!\u0002\u0013\u0011Y$A\u0007cCR\u001c\u0007n\u00144gg\u0016$8\u000f\t\u0005\n\tW\"\t\u00061A\u0005\u00025\f1\u0002]1si&$\u0018n\u001c8JI\"QAq\u000eC)\u0001\u0004%\t\u0001\"\u001d\u0002\u001fA\f'\u000f^5uS>t\u0017\nZ0%KF$B!!\u001d\u0005t!I\u0011\u0011\u0010C7\u0003\u0003\u0005\rA\u001c\u0005\t\to\"\t\u0006)Q\u0005]\u0006a\u0001/\u0019:uSRLwN\\%eA!QA1\u0010C)\u0001\u0004%\t!a\u0015\u0002!%tG-\u001a=J]B\u000b'\u000f^5uS>t\u0007B\u0003C@\t#\u0002\r\u0011\"\u0001\u0005\u0002\u0006!\u0012N\u001c3fq&s\u0007+\u0019:uSRLwN\\0%KF$B!!\u001d\u0005\u0004\"Q\u0011\u0011\u0010C?\u0003\u0003\u0005\r!!\u0016\t\u0013\u0011\u001dE\u0011\u000bQ!\n\u0005U\u0013!E5oI\u0016D\u0018J\u001c)beRLG/[8oA!IA1\u0012C)\u0001\u0004%\t!\\\u0001\bE\u0006$8\r[%e\u0011)!y\t\"\u0015A\u0002\u0013\u0005A\u0011S\u0001\fE\u0006$8\r[%e?\u0012*\u0017\u000f\u0006\u0003\u0002r\u0011M\u0005\"CA=\t\u001b\u000b\t\u00111\u0001o\u0011!!9\n\"\u0015!B\u0013q\u0017\u0001\u00032bi\u000eD\u0017\n\u001a\u0011\t\u0013\u0011mE\u0011\u000ba\u0001\n\u0003i\u0017\u0001D5oI\u0016D\u0018J\u001c\"bi\u000eD\u0007B\u0003CP\t#\u0002\r\u0011\"\u0001\u0005\"\u0006\u0001\u0012N\u001c3fq&s')\u0019;dQ~#S-\u001d\u000b\u0005\u0003c\"\u0019\u000bC\u0005\u0002z\u0011u\u0015\u0011!a\u0001]\"AAq\u0015C)A\u0003&a.A\u0007j]\u0012,\u00070\u00138CCR\u001c\u0007\u000e\t\u0005\n\tW#\t\u00061A\u0005\u00025\fq\u0002\\1tiB\u000b'\u000f^5uS>t\u0017\n\u001a\u0005\u000b\t_#\t\u00061A\u0005\u0002\u0011E\u0016a\u00057bgR\u0004\u0016M\u001d;ji&|g.\u00133`I\u0015\fH\u0003BA9\tgC\u0011\"!\u001f\u0005.\u0006\u0005\t\u0019\u00018\t\u0011\u0011]F\u0011\u000bQ!\n9\f\u0001\u0003\\1tiB\u000b'\u000f^5uS>t\u0017\n\u001a\u0011\t\u0015\u0011mF\u0011\u000ba\u0001\n\u0003!i,\u0001\u0006gS2,7\u000b\u001e:fC6,\"\u0001b0\u0011\t\tMA\u0011Y\u0005\u0005\t\u0007\u0014)BA\bGS2,\u0017J\u001c9viN#(/Z1n\u0011)!9\r\"\u0015A\u0002\u0013\u0005A\u0011Z\u0001\u000fM&dWm\u0015;sK\u0006lw\fJ3r)\u0011\t\t\bb3\t\u0015\u0005eDQYA\u0001\u0002\u0004!y\fC\u0005\u0005P\u0012E\u0003\u0015)\u0003\u0005@\u0006Ya-\u001b7f'R\u0014X-Y7!\u0011)!\u0019\u000e\"\u0015A\u0002\u0013\u0005AQ[\u0001\u0012I\u0016\u001cXM]5bY&TXm\u0015;sK\u0006lWC\u0001Cl!\r1F\u0011\\\u0005\u0004\t7<&!\u0006#fg\u0016\u0014\u0018.\u00197ju\u0006$\u0018n\u001c8TiJ,\u0017-\u001c\u0005\u000b\t?$\t\u00061A\u0005\u0002\u0011\u0005\u0018!\u00063fg\u0016\u0014\u0018.\u00197ju\u0016\u001cFO]3b[~#S-\u001d\u000b\u0005\u0003c\"\u0019\u000f\u0003\u0006\u0002z\u0011u\u0017\u0011!a\u0001\t/D\u0011\u0002b:\u0005R\u0001\u0006K\u0001b6\u0002%\u0011,7/\u001a:jC2L'0Z*ue\u0016\fW\u000e\t\u0005\u000b\tW$\t\u00061A\u0005\u0002\u00115\u0018\u0001\u00038fqRLE/Z7\u0016\u0005\u0011=\b#\u0002\u000f\u0003vV!\u0003B\u0003Cz\t#\u0002\r\u0011\"\u0001\u0005v\u0006aa.\u001a=u\u0013R,Wn\u0018\u0013fcR!\u0011\u0011\u000fC|\u0011)\tI\b\"=\u0002\u0002\u0003\u0007Aq\u001e\u0005\n\tw$\t\u0006)Q\u0005\t_\f\u0011B\\3yi&#X-\u001c\u0011\t\u0013\u0011}H\u0011\u000ba\u0001\n\u0003)\u0018\u0001\u00034j]&\u001c\b.\u001a3\t\u0015\u0015\rA\u0011\u000ba\u0001\n\u0003))!\u0001\u0007gS:L7\u000f[3e?\u0012*\u0017\u000f\u0006\u0003\u0002r\u0015\u001d\u0001\"CA=\u000b\u0003\t\t\u00111\u0001w\u0011!)Y\u0001\"\u0015!B\u00131\u0018!\u00034j]&\u001c\b.\u001a3!\u0011!)y\u0001\"\u0015\u0005\u0002\u0015E\u0011a\u00048fqR\u0014\u0015\r^2i'R\u0014X-Y7\u0015\u0005\u0011]\u0007\u0002CC\u000b\t#\"I!b\u0006\u0002'M\\\u0017\u000e\u001d+p\u001d\u0016DH\u000fU1si&$\u0018n\u001c8\u0015\u0005\u0005E\u0004\u0002CC\u000e\t#\"I!\"\b\u0002\u0019I,\u0017\r\u001a(fqRLE/Z7\u0015\u0005\u0011=\b\"CC\u0011\t#\u0002\r\u0011\"\u0001n\u0003MqW\r\u001f;QCJ$\u0018\u000e^5p]R{'+Z1e\u0011)))\u0003\"\u0015A\u0002\u0013\u0005QqE\u0001\u0018]\u0016DH\u000fU1si&$\u0018n\u001c8U_J+\u0017\rZ0%KF$B!!\u001d\u0006*!I\u0011\u0011PC\u0012\u0003\u0003\u0005\rA\u001c\u0005\t\u000b[!\t\u0006)Q\u0005]\u0006!b.\u001a=u!\u0006\u0014H/\u001b;j_:$vNU3bI\u0002B\u0001\"\"\r\u0005R\u0011\u0005Q1G\u0001\u0012e\u0016\fGMT3yiB\u000b'\u000f^5uS>tGC\u0001C\u000e\u0011!)9\u0004\"\u0015\u0005\u0002\u0015]\u0011aB2mK\u0006tW\u000f\u001d\u0005\b\u000bw\u0001A\u0011AC\u001f\u0003M!Wm\u001d;sk\u000e$\u0018N^3Ji\u0016\u0014\u0018\r^8s)\u0011\u0011y/b\u0010\t\u0011\u0015\u0005S\u0011\ba\u0001\u0005_\fa\"\\3n_JL\u0018\n^3sCR|'\u000fC\u0004\u0006F\u0001!\t!b\u0012\u0002'A\f'\u000f^5uS>tW\rZ%uKJ\fGo\u001c:\u0016\u0005\u0011]\u0001bBC&\u0001\u0011\u0005QQJ\u0001\tSR,'/\u0019;peV\u0011A1\u0004\u0005\b\u000b#\u0002A\u0011AC*\u0003Q9(/\u001b;f!\u0006\u0014H/\u001b;j_:,GMR5mKR1!1HC+\u000b/B\u0001Ba\n\u0006P\u0001\u0007!1\u0006\u0005\t\u000b3*y\u00051\u0001\u0003\u0012\u0005Qq.\u001e;qkR4\u0015\u000e\\3\t\u000f\u0015u\u0003\u0001\"\u0001\u0006\u0018\u0005!1\u000f^8q\u0011\u001d)\t\u0007\u0001C\u0005\u000bG\n\u0001c\u001a:pkB\u0014\u0015\u0010U1si&$\u0018n\u001c8\u0015\t\u0011]QQ\r\u0005\t\u000bO*y\u00061\u0001\u0003p\u0006!A-\u0019;b\r!)Y\u0007\u0001Q\u0001\n\u00155$\u0001F%uKJ\fGo\u001c:G_J\u0004\u0016M\u001d;ji&|gn\u0005\u0004\u0006j\u0005eH1\u0004\u0005\u000b\tW*IG!A!\u0002\u0013q\u0007bCC4\u000bS\u0012\t\u0011)A\u0005\u000bg\u0002R\u0001SC;\u0005gL1!b\u001eS\u0005A\u0011UO\u001a4fe\u0016$\u0017\n^3sCR|'\u000fC\u0004\\\u000bS\"\t!b\u001f\u0015\r\u0015uTqPCA!\u0011\t\t0\"\u001b\t\u000f\u0011-T\u0011\u0010a\u0001]\"AQqMC=\u0001\u0004)\u0019\bC\u0004\u0004d\u0015%D\u0011I;\t\u0011\r\u001dT\u0011\u000eC!\u000b\u000f#\"\u0001\"\b\t\u0019\u0015-\u0005\u0001%A\u0001\u0002\u0003%\t!\"$\u0002#A\u0014x\u000e^3di\u0016$GeZ3u+N,G\r\u0006\u0003\u0006\u0010\u0016EECAA+\u0011%\tI(\"#\u0002\u0002\u0003\u0007Ql\u0002\u0006\u0006\u0016\n\t\t\u0011#\u0001\u0007\u000b/\u000ba\"\u0012=uKJt\u0017\r\\*peR,'\u000fE\u0002\u0010\u000b33\u0011\"\u0001\u0002\u0002\u0002#\u0005a!b'\u0014\t\u0015e\u0015\u0011 \u0005\b7\u0016eE\u0011ACP)\t)9\n\u0003\u0006\u0006$\u0016e\u0015\u0013!C\u0001\u000bK\u000b1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\u0012T\u0003CCT\u000bc+\u0019,\".\u0016\u0005\u0015%&\u0006BCV\u0005_r1\u0001HCW\u0013\r)y+H\u0001\u0005\u001d>tW\r\u0002\u0004\u0019\u000bC\u0013\rA\u0007\u0003\u0007{\u0015\u0005&\u0019\u0001\u000e\u0005\r\u0019*\tK1\u0001\u001b\u0011))I,\"'\u0012\u0002\u0013\u0005Q1X\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001a\u0016\u0011\u0015uV\u0011YCb\u000b\u000b,\"!b0+\u0007\u0001\u0013y\u0007\u0002\u0004\u0019\u000bo\u0013\rA\u0007\u0003\u0007{\u0015]&\u0019\u0001\u000e\u0005\r\u0019*9L1\u0001\u001b\u0011))I-\"'\u0012\u0002\u0013\u0005Q1Z\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000f\n\u001b\u0016\u0011\u0015\u001dVQZCh\u000b#$a\u0001GCd\u0005\u0004QBAB\u001f\u0006H\n\u0007!\u0004\u0002\u0004'\u000b\u000f\u0014\rA\u0007\u0005\u000b\u000b+,I*%A\u0005\u0002\u0015]\u0017a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$S'\u0006\u0005\u0006Z\u0016uWq\\Cq+\t)YNK\u0002V\u0005_\"a\u0001GCj\u0005\u0004QBAB\u001f\u0006T\n\u0007!\u0004\u0002\u0004'\u000b'\u0014\rA\u0007"
)
public class ExternalSorter<K, V, C> extends Spillable<WritablePartitionedPairCollection<K, C>> {
   public final TaskContext org$apache$spark$util$collection$ExternalSorter$$context;
   public final Option<Aggregator<K, V, C>> org$apache$spark$util$collection$ExternalSorter$$aggregator;
   private final Option<Partitioner> partitioner;
   public final Option<Ordering<K>> org$apache$spark$util$collection$ExternalSorter$$ordering;
   private final SparkConf conf;
   private final int org$apache$spark$util$collection$ExternalSorter$$numPartitions;
   private final boolean shouldPartition;
   private final BlockManager blockManager;
   private final DiskBlockManager diskBlockManager;
   private final SerializerManager org$apache$spark$util$collection$ExternalSorter$$serializerManager;
   private final SerializerInstance org$apache$spark$util$collection$ExternalSorter$$serInstance;
   private final int fileBufferSize;
   private final long org$apache$spark$util$collection$ExternalSorter$$serializerBatchSize;
   private volatile PartitionedAppendOnlyMap<K, C> map;
   private volatile PartitionedPairBuffer<K, C> buffer;
   private long _diskBytesSpilled;
   private long _peakMemoryUsedBytes;
   private volatile boolean isShuffleSort;
   private final ArrayBuffer<ExternalSorter<K, V, C>.SpilledFile> org$apache$spark$util$collection$ExternalSorter$$forceSpillFiles;
   private volatile ExternalSorter<K, V, C>.SpillableIterator readingIterator;
   private final Comparator<K> org$apache$spark$util$collection$ExternalSorter$$keyComparator;
   private final ArrayBuffer<ExternalSorter<K, V, C>.SpilledFile> spills;
   private volatile ExternalSorter<K, V, C>.SpilledFile$ org$apache$spark$util$collection$ExternalSorter$$SpilledFile$module;

   public static <K, V, C> Serializer $lessinit$greater$default$5() {
      return ExternalSorter$.MODULE$.$lessinit$greater$default$5();
   }

   public static <K, V, C> None$ $lessinit$greater$default$4() {
      return ExternalSorter$.MODULE$.$lessinit$greater$default$4();
   }

   public static <K, V, C> Option<Partitioner> $lessinit$greater$default$3() {
      return ExternalSorter$.MODULE$.$lessinit$greater$default$3();
   }

   public static <K, V, C> None$ $lessinit$greater$default$2() {
      return ExternalSorter$.MODULE$.$lessinit$greater$default$2();
   }

   private SpilledFile$ org$apache$spark$util$collection$ExternalSorter$$SpilledFile$lzycompute() {
      synchronized(this) {
         if (this.org$apache$spark$util$collection$ExternalSorter$$SpilledFile$module == null) {
            this.org$apache$spark$util$collection$ExternalSorter$$SpilledFile$module = new SpilledFile$();
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      Object var3 = null;
      return this.org$apache$spark$util$collection$ExternalSorter$$SpilledFile$module;
   }

   // $FF: synthetic method
   public long protected$getUsed(ExternalSorter x$1) {
      return x$1.getUsed();
   }

   private SparkConf conf() {
      return this.conf;
   }

   public int org$apache$spark$util$collection$ExternalSorter$$numPartitions() {
      return this.org$apache$spark$util$collection$ExternalSorter$$numPartitions;
   }

   private boolean shouldPartition() {
      return this.shouldPartition;
   }

   private int getPartition(K key) {
      return this.shouldPartition() ? ((Partitioner)this.partitioner.get()).getPartition(key) : 0;
   }

   private BlockManager blockManager() {
      return this.blockManager;
   }

   private DiskBlockManager diskBlockManager() {
      return this.diskBlockManager;
   }

   public SerializerManager org$apache$spark$util$collection$ExternalSorter$$serializerManager() {
      return this.org$apache$spark$util$collection$ExternalSorter$$serializerManager;
   }

   public SerializerInstance org$apache$spark$util$collection$ExternalSorter$$serInstance() {
      return this.org$apache$spark$util$collection$ExternalSorter$$serInstance;
   }

   private int fileBufferSize() {
      return this.fileBufferSize;
   }

   public long org$apache$spark$util$collection$ExternalSorter$$serializerBatchSize() {
      return this.org$apache$spark$util$collection$ExternalSorter$$serializerBatchSize;
   }

   private PartitionedAppendOnlyMap<K, C> map() {
      return this.map;
   }

   private void map_$eq(PartitionedAppendOnlyMap<K, C> x$1) {
      this.map = x$1;
   }

   private PartitionedPairBuffer<K, C> buffer() {
      return this.buffer;
   }

   private void buffer_$eq(PartitionedPairBuffer<K, C> x$1) {
      this.buffer = x$1;
   }

   private long _diskBytesSpilled() {
      return this._diskBytesSpilled;
   }

   private void _diskBytesSpilled_$eq(long x$1) {
      this._diskBytesSpilled = x$1;
   }

   public long diskBytesSpilled() {
      return this._diskBytesSpilled();
   }

   private long _peakMemoryUsedBytes() {
      return this._peakMemoryUsedBytes;
   }

   private void _peakMemoryUsedBytes_$eq(long x$1) {
      this._peakMemoryUsedBytes = x$1;
   }

   public long peakMemoryUsedBytes() {
      return this._peakMemoryUsedBytes();
   }

   private boolean isShuffleSort() {
      return this.isShuffleSort;
   }

   private void isShuffleSort_$eq(boolean x$1) {
      this.isShuffleSort = x$1;
   }

   public ArrayBuffer<ExternalSorter<K, V, C>.SpilledFile> org$apache$spark$util$collection$ExternalSorter$$forceSpillFiles() {
      return this.org$apache$spark$util$collection$ExternalSorter$$forceSpillFiles;
   }

   private ExternalSorter<K, V, C>.SpillableIterator readingIterator() {
      return this.readingIterator;
   }

   private void readingIterator_$eq(ExternalSorter<K, V, C>.SpillableIterator x$1) {
      this.readingIterator = x$1;
   }

   public Comparator<K> org$apache$spark$util$collection$ExternalSorter$$keyComparator() {
      return this.org$apache$spark$util$collection$ExternalSorter$$keyComparator;
   }

   private Option<Comparator<K>> comparator() {
      return (Option<Comparator<K>>)(!this.org$apache$spark$util$collection$ExternalSorter$$ordering.isDefined() && !this.org$apache$spark$util$collection$ExternalSorter$$aggregator.isDefined() ? None$.MODULE$ : new Some(this.org$apache$spark$util$collection$ExternalSorter$$keyComparator()));
   }

   public ExternalSorter<K, V, C>.SpilledFile$ org$apache$spark$util$collection$ExternalSorter$$SpilledFile() {
      return this.org$apache$spark$util$collection$ExternalSorter$$SpilledFile$module == null ? this.org$apache$spark$util$collection$ExternalSorter$$SpilledFile$lzycompute() : this.org$apache$spark$util$collection$ExternalSorter$$SpilledFile$module;
   }

   private ArrayBuffer<ExternalSorter<K, V, C>.SpilledFile> spills() {
      return this.spills;
   }

   public int numSpills() {
      return this.spills().size();
   }

   public void insertAll(Iterator<Product2<K, V>> records) {
      boolean shouldCombine = this.org$apache$spark$util$collection$ExternalSorter$$aggregator.isDefined();
      if (shouldCombine) {
         Function2 mergeValue = ((Aggregator)this.org$apache$spark$util$collection$ExternalSorter$$aggregator.get()).mergeValue();
         Function1 createCombiner = ((Aggregator)this.org$apache$spark$util$collection$ExternalSorter$$aggregator.get()).createCombiner();
         ObjectRef kv = ObjectRef.create((Object)null);
         Function2 update = new Serializable(this, mergeValue, createCombiner, kv) {
            public static final long serialVersionUID = 0L;
            private final Function2 mergeValue$1;
            private final Function1 createCombiner$1;
            private final ObjectRef kv$1;

            public final C apply(boolean hadValue, C oldValue) {
               return (C)(hadValue ? this.mergeValue$1.apply(oldValue, ((Product2)this.kv$1.elem)._2()) : this.createCombiner$1.apply(((Product2)this.kv$1.elem)._2()));
            }

            public {
               this.mergeValue$1 = mergeValue$1;
               this.createCombiner$1 = createCombiner$1;
               this.kv$1 = kv$1;
            }
         };

         while(records.hasNext()) {
            this.addElementsRead();
            kv.elem = (T)((Product2)records.next());
            this.map().changeValue(new Tuple2(BoxesRunTime.boxToInteger(this.getPartition(((Product2)kv.elem)._1())), ((Product2)kv.elem)._1()), update);
            this.maybeSpillCollection(true);
         }
      } else {
         while(records.hasNext()) {
            this.addElementsRead();
            Product2 kv = (Product2)records.next();
            this.buffer().insert(this.getPartition(kv._1()), kv._1(), kv._2());
            this.maybeSpillCollection(false);
         }
      }

   }

   private void maybeSpillCollection(boolean usingMap) {
      long estimatedSize = 0L;
      if (usingMap) {
         estimatedSize = this.map().estimateSize();
         if (this.maybeSpill(this.map(), estimatedSize)) {
            this.map_$eq(new PartitionedAppendOnlyMap());
         }
      } else {
         estimatedSize = this.buffer().estimateSize();
         if (this.maybeSpill(this.buffer(), estimatedSize)) {
            this.buffer_$eq(new PartitionedPairBuffer(PartitionedPairBuffer$.MODULE$.$lessinit$greater$default$1()));
         }
      }

      if (estimatedSize > this._peakMemoryUsedBytes()) {
         this._peakMemoryUsedBytes_$eq(estimatedSize);
      }

   }

   public void spill(WritablePartitionedPairCollection<K, C> collection) {
      WritablePartitionedIterator inMemoryIterator = collection.destructiveSortedWritablePartitionedIterator(this.comparator());
      SpilledFile spillFile = this.org$apache$spark$util$collection$ExternalSorter$$spillMemoryIteratorToDisk(inMemoryIterator);
      this.spills().$plus$eq(spillFile);
   }

   public boolean forceSpill() {
      boolean var10000;
      if (this.isShuffleSort()) {
         var10000 = false;
      } else {
         Predef$.MODULE$.assert(this.readingIterator() != null);
         boolean isSpilled = this.readingIterator().spill();
         if (isSpilled) {
            this.map_$eq((PartitionedAppendOnlyMap)null);
            this.buffer_$eq((PartitionedPairBuffer)null);
         }

         var10000 = isSpilled;
      }

      return var10000;
   }

   public ExternalSorter<K, V, C>.SpilledFile org$apache$spark$util$collection$ExternalSorter$$spillMemoryIteratorToDisk(WritablePartitionedIterator inMemoryIterator) {
      Tuple2 var3 = this.diskBlockManager().createTempShuffleBlock();
      if (var3 == null) {
         throw new MatchError(var3);
      } else {
         TempShuffleBlockId blockId = (TempShuffleBlockId)var3._1();
         File file = (File)var3._2();
         Tuple2 var6 = new Tuple2(blockId, file);
         TempShuffleBlockId blockId = (TempShuffleBlockId)var6._1();
         File file = (File)var6._2();
         LongRef objectsWritten = LongRef.create(0L);
         ShuffleWriteMetrics spillMetrics = new ShuffleWriteMetrics();
         DiskBlockObjectWriter writer = this.blockManager().getDiskWriter(blockId, file, this.org$apache$spark$util$collection$ExternalSorter$$serInstance(), this.fileBufferSize(), spillMetrics);
         ArrayBuffer batchSizes = new ArrayBuffer();
         long[] elementsPerPartition = new long[this.org$apache$spark$util$collection$ExternalSorter$$numPartitions()];
         boolean success = false;

         try {
            while(inMemoryIterator.hasNext()) {
               int partitionId = inMemoryIterator.nextPartition();
               Predef$.MODULE$.require(partitionId >= 0 && partitionId < this.org$apache$spark$util$collection$ExternalSorter$$numPartitions(), new Serializable(this, partitionId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final ExternalSorter $outer;
                  private final int partitionId$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"partition Id: ", " should be in the range [0, ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.partitionId$1), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$util$collection$ExternalSorter$$numPartitions())}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.partitionId$1 = partitionId$1;
                     }
                  }
               });
               inMemoryIterator.writeNext(writer);
               int var10002 = elementsPerPartition[partitionId]++;
               ++objectsWritten.elem;
               if (objectsWritten.elem == this.org$apache$spark$util$collection$ExternalSorter$$serializerBatchSize()) {
                  this.flush$1(objectsWritten, writer, batchSizes);
               }
            }

            if (objectsWritten.elem > 0L) {
               this.flush$1(objectsWritten, writer, batchSizes);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               writer.revertPartialWritesAndClose();
            }

            success = true;
         } finally {
            if (success) {
               writer.close();
            } else {
               writer.revertPartialWritesAndClose();
               if (file.exists() && !file.delete()) {
                  this.logWarning(new Serializable(this, file) {
                     public static final long serialVersionUID = 0L;
                     private final File file$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.file$1}));
                     }

                     public {
                        this.file$1 = file$1;
                     }
                  });
               }
            }

         }

         return this.org$apache$spark$util$collection$ExternalSorter$$SpilledFile().apply((File)file, (BlockId)blockId, (long[])((long[])batchSizes.toArray(ClassTag$.MODULE$.Long())), (long[])elementsPerPartition);
      }
   }

   private Iterator<Tuple2<Object, Iterator<Product2<K, C>>>> merge(Seq<ExternalSorter<K, V, C>.SpilledFile> spills, Iterator<Tuple2<Tuple2<Object, K>, C>> inMemory) {
      Seq readers = (Seq)spills.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ExternalSorter $outer;

         public final ExternalSorter<K, V, C>.SpillReader apply(ExternalSorter<K, V, C>.SpilledFile x$3) {
            return this.$outer.new SpillReader(x$3);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom());
      BufferedIterator inMemBuffered = inMemory.buffered();
      return RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.org$apache$spark$util$collection$ExternalSorter$$numPartitions()).iterator().<Tuple2<Object, Iterator<Product2<K, C>>>>map(new Serializable(this, readers, inMemBuffered) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ExternalSorter $outer;
         private final Seq readers$1;
         private final BufferedIterator inMemBuffered$1;

         public final Tuple2<Object, Iterator<Product2<K, C>>> apply(int p) {
            IteratorForPartition inMemIterator = this.$outer.new IteratorForPartition(p, this.inMemBuffered$1);
            Seq iterators = (Seq)((TraversableLike)this.readers$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Iterator<Product2<K, C>> apply(ExternalSorter<K, V, C>.SpillReader x$4) {
                  return x$4.readNextPartition();
               }
            }, Seq$.MODULE$.canBuildFrom())).$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new IteratorForPartition[]{inMemIterator})), Seq$.MODULE$.canBuildFrom());
            return this.$outer.org$apache$spark$util$collection$ExternalSorter$$aggregator.isDefined() ? new Tuple2(BoxesRunTime.boxToInteger(p), this.$outer.org$apache$spark$util$collection$ExternalSorter$$mergeWithAggregation(iterators, ((Aggregator)this.$outer.org$apache$spark$util$collection$ExternalSorter$$aggregator.get()).mergeCombiners(), this.$outer.org$apache$spark$util$collection$ExternalSorter$$keyComparator(), this.$outer.org$apache$spark$util$collection$ExternalSorter$$ordering.isDefined())) : (this.$outer.org$apache$spark$util$collection$ExternalSorter$$ordering.isDefined() ? new Tuple2(BoxesRunTime.boxToInteger(p), this.$outer.org$apache$spark$util$collection$ExternalSorter$$mergeSort(iterators, this.$outer.org$apache$spark$util$collection$ExternalSorter$$ordering.get())) : new Tuple2(BoxesRunTime.boxToInteger(p), TraversableOnce$.MODULE$.flattenTraversableOnce(iterators.iterator(), Predef$.MODULE$.$conforms()).flatten()));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.readers$1 = readers$1;
               this.inMemBuffered$1 = inMemBuffered$1;
            }
         }
      });
   }

   public Iterator<Product2<K, C>> org$apache$spark$util$collection$ExternalSorter$$mergeSort(Seq<Iterator<Product2<K, C>>> iterators, Comparator<K> comparator) {
      Seq bufferedIters = (Seq)((TraversableLike)iterators.filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Iterator<Product2<K, C>> x$5) {
            return x$5.hasNext();
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final BufferedIterator<Product2<K, C>> apply(Iterator<Product2<K, C>> x$6) {
            return x$6.buffered();
         }
      }, Seq$.MODULE$.canBuildFrom());
      PriorityQueue heap = new PriorityQueue(new Ordering<BufferedIterator<Product2<K, C>>>(this, comparator) {
         private final Comparator comparator$2;

         public Some tryCompare(Object x, Object y) {
            return Ordering$class.tryCompare(this, x, y);
         }

         public boolean lteq(Object x, Object y) {
            return Ordering$class.lteq(this, x, y);
         }

         public boolean gteq(Object x, Object y) {
            return Ordering$class.gteq(this, x, y);
         }

         public boolean lt(Object x, Object y) {
            return Ordering$class.lt(this, x, y);
         }

         public boolean gt(Object x, Object y) {
            return Ordering$class.gt(this, x, y);
         }

         public boolean equiv(Object x, Object y) {
            return Ordering$class.equiv(this, x, y);
         }

         public Object max(Object x, Object y) {
            return Ordering$class.max(this, x, y);
         }

         public Object min(Object x, Object y) {
            return Ordering$class.min(this, x, y);
         }

         public Ordering<BufferedIterator<Product2<K, C>>> reverse() {
            return Ordering$class.reverse(this);
         }

         public <U> Ordering<U> on(Function1<U, BufferedIterator<Product2<K, C>>> f) {
            return Ordering$class.on(this, f);
         }

         public Ordering.Ops mkOrderingOps(Object lhs) {
            return Ordering$class.mkOrderingOps(this, lhs);
         }

         public int compare(BufferedIterator<Product2<K, C>> x, BufferedIterator<Product2<K, C>> y) {
            return -this.comparator$2.compare(((Product2)x.head())._1(), ((Product2)y.head())._1());
         }

         public {
            this.comparator$2 = comparator$2;
            PartialOrdering$class.$init$(this);
            Ordering$class.$init$(this);
         }
      });
      heap.enqueue(bufferedIters);
      return new Iterator<Product2<K, C>>(this, heap) {
         private final PriorityQueue heap$1;

         public Iterator<Product2<K, C>> seq() {
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

         public Iterator<Product2<K, C>> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<Product2<K, C>> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<Product2<K, C>> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<Product2<K, C>, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<Product2<K, C>, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<Product2<K, C>> filter(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Product2<K, C>, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<Product2<K, C>> withFilter(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<Product2<K, C>> filterNot(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<Product2<K, C>, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, Product2<K, C>, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<Product2<K, C>, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<Product2<K, C>> takeWhile(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> partition(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> span(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<Product2<K, C>> dropWhile(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<Product2<K, C>, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<Product2<K, C>, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<Product2<K, C>, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<Product2<K, C>> find(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<Product2<K, C>> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<Product2<K, C>>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<Product2<K, C>>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> duplicate() {
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

         public Traversable<Product2<K, C>> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<Product2<K, C>> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<Product2<K, C>> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<Product2<K, C>> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<Product2<K, C>, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<Product2<K, C>, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, Product2<K, C>, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<Product2<K, C>, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, Product2<K, C>, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<Product2<K, C>, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, Product2<K, C>, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<Product2<K, C>, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, Product2<K, C>, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<Product2<K, C>, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, Product2<K, C>, B> seqop, Function2<B, B, B> combop) {
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

         public List<Product2<K, C>> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<Product2<K, C>> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<Product2<K, C>> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<Product2<K, C>> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<Product2<K, C>> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, Product2<K, C>, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Product2<K, C>, Tuple2<T, U>> ev) {
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
            return !this.heap$1.isEmpty();
         }

         public Product2<K, C> next() {
            if (this.hasNext()) {
               BufferedIterator firstBuf = (BufferedIterator)this.heap$1.dequeue();
               Product2 firstPair = (Product2)firstBuf.next();
               if (firstBuf.hasNext()) {
                  this.heap$1.enqueue(Predef$.MODULE$.wrapRefArray(new BufferedIterator[]{firstBuf}));
               }

               return firstPair;
            } else {
               throw new NoSuchElementException();
            }
         }

         public {
            this.heap$1 = heap$1;
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
         }
      };
   }

   public Iterator<Product2<K, C>> org$apache$spark$util$collection$ExternalSorter$$mergeWithAggregation(Seq<Iterator<Product2<K, C>>> iterators, Function2<C, C, C> mergeCombiners, Comparator<K> comparator, boolean totalOrder) {
      return totalOrder ? new Iterator<Product2<K, C>>(this, iterators, mergeCombiners, comparator) {
         private final BufferedIterator<Product2<K, C>> sorted;
         private final Function2 mergeCombiners$1;

         public Iterator<Product2<K, C>> seq() {
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

         public Iterator<Product2<K, C>> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<Product2<K, C>> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<Product2<K, C>> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<Product2<K, C>, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<Product2<K, C>, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<Product2<K, C>> filter(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Product2<K, C>, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<Product2<K, C>> withFilter(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<Product2<K, C>> filterNot(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<Product2<K, C>, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, Product2<K, C>, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<Product2<K, C>, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<Product2<K, C>> takeWhile(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> partition(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> span(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<Product2<K, C>> dropWhile(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<Product2<K, C>, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<Product2<K, C>, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<Product2<K, C>, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<Product2<K, C>> find(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<Product2<K, C>, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<Product2<K, C>> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<Product2<K, C>>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<Product2<K, C>>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> duplicate() {
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

         public Traversable<Product2<K, C>> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<Product2<K, C>> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<Product2<K, C>> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<Product2<K, C>> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<Product2<K, C>, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<Product2<K, C>, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, Product2<K, C>, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<Product2<K, C>, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, Product2<K, C>, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<Product2<K, C>, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, Product2<K, C>, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<Product2<K, C>, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, Product2<K, C>, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<Product2<K, C>, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, Product2<K, C>, B> seqop, Function2<B, B, B> combop) {
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

         public List<Product2<K, C>> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<Product2<K, C>> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<Product2<K, C>> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<Product2<K, C>> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<Product2<K, C>> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, Product2<K, C>, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Product2<K, C>, Tuple2<T, U>> ev) {
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

         private BufferedIterator<Product2<K, C>> sorted() {
            return this.sorted;
         }

         public boolean hasNext() {
            return this.sorted().hasNext();
         }

         public Product2<K, C> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Product2 elem = (Product2)this.sorted().next();
               Object k = elem._1();

               Object c;
               Product2 pair;
               for(c = elem._2(); this.sorted().hasNext() && BoxesRunTime.equals(((Product2)this.sorted().head())._1(), k); c = this.mergeCombiners$1.apply(c, pair._2())) {
                  pair = (Product2)this.sorted().next();
               }

               return new Tuple2<K, C>(k, c);
            }
         }

         public {
            this.mergeCombiners$1 = mergeCombiners$1;
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
            this.sorted = $outer.org$apache$spark$util$collection$ExternalSorter$$mergeSort(iterators$1, comparator$1).buffered();
         }
      } : (new Iterator<Iterator<Product2<K, C>>>(this, iterators, mergeCombiners, comparator) {
         private final BufferedIterator<Product2<K, C>> sorted;
         private final ArrayBuffer<K> keys;
         private final ArrayBuffer<C> combiners;
         private final Function2 mergeCombiners$1;
         private final Comparator comparator$1;

         public Iterator<Iterator<Product2<K, C>>> seq() {
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

         public Iterator<Iterator<Product2<K, C>>> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<Iterator<Product2<K, C>>> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<Iterator<Product2<K, C>>> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<Iterator<Product2<K, C>>, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<Iterator<Product2<K, C>>, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<Iterator<Product2<K, C>>> filter(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Iterator<Product2<K, C>>, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<Iterator<Product2<K, C>>> withFilter(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<Iterator<Product2<K, C>>> filterNot(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<Iterator<Product2<K, C>>, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, Iterator<Product2<K, C>>, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<Iterator<Product2<K, C>>, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<Iterator<Product2<K, C>>> takeWhile(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<Iterator<Product2<K, C>>>, Iterator<Iterator<Product2<K, C>>>> partition(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<Iterator<Product2<K, C>>>, Iterator<Iterator<Product2<K, C>>>> span(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<Iterator<Product2<K, C>>> dropWhile(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<Iterator<Product2<K, C>>, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<Iterator<Product2<K, C>>, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<Iterator<Product2<K, C>>, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<Iterator<Product2<K, C>>> find(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<Iterator<Product2<K, C>>, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<Iterator<Product2<K, C>>> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<Iterator<Product2<K, C>>>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<Iterator<Product2<K, C>>>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<Iterator<Product2<K, C>>>, Iterator<Iterator<Product2<K, C>>>> duplicate() {
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

         public Traversable<Iterator<Product2<K, C>>> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<Iterator<Product2<K, C>>> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<Iterator<Product2<K, C>>> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<Iterator<Product2<K, C>>> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<Iterator<Product2<K, C>>, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<Iterator<Product2<K, C>>, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, Iterator<Product2<K, C>>, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<Iterator<Product2<K, C>>, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, Iterator<Product2<K, C>>, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<Iterator<Product2<K, C>>, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, Iterator<Product2<K, C>>, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<Iterator<Product2<K, C>>, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, Iterator<Product2<K, C>>, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<Iterator<Product2<K, C>>, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, Iterator<Product2<K, C>>, B> seqop, Function2<B, B, B> combop) {
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

         public List<Iterator<Product2<K, C>>> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<Iterator<Product2<K, C>>> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<Iterator<Product2<K, C>>> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<Iterator<Product2<K, C>>> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<Iterator<Product2<K, C>>> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, Iterator<Product2<K, C>>, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Iterator<Product2<K, C>>, Tuple2<T, U>> ev) {
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

         public BufferedIterator<Product2<K, C>> sorted() {
            return this.sorted;
         }

         public ArrayBuffer<K> keys() {
            return this.keys;
         }

         public ArrayBuffer<C> combiners() {
            return this.combiners;
         }

         public boolean hasNext() {
            return this.sorted().hasNext();
         }

         public Iterator<Product2<K, C>> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.keys().clear();
               this.combiners().clear();
               Product2 firstPair = (Product2)this.sorted().next();
               this.keys().$plus$eq(firstPair._1());
               this.combiners().$plus$eq(firstPair._2());
               Object key = firstPair._1();

               while(this.sorted().hasNext() && this.comparator$1.compare(((Product2)this.sorted().head())._1(), key) == 0) {
                  Product2 pair = (Product2)this.sorted().next();
                  int i = 0;

                  boolean foundKey;
                  for(foundKey = false; i < this.keys().size() && !foundKey; ++i) {
                     if (BoxesRunTime.equals(this.keys().apply(i), pair._1())) {
                        this.combiners().update(i, this.mergeCombiners$1.apply(this.combiners().apply(i), pair._2()));
                        foundKey = true;
                     }
                  }

                  if (foundKey) {
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     this.keys().$plus$eq(pair._1());
                     this.combiners().$plus$eq(pair._2());
                  }
               }

               return this.keys().iterator().zip(this.combiners().iterator());
            }
         }

         public {
            this.mergeCombiners$1 = mergeCombiners$1;
            this.comparator$1 = comparator$1;
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
            this.sorted = $outer.org$apache$spark$util$collection$ExternalSorter$$mergeSort(iterators$1, comparator$1).buffered();
            this.keys = new ArrayBuffer<K>();
            this.combiners = new ArrayBuffer<C>();
         }
      }).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Iterator<Product2<K, C>> apply(Iterator<Product2<K, C>> i) {
            return i;
         }
      });
   }

   public Iterator<Tuple2<Tuple2<Object, K>, C>> destructiveIterator(Iterator<Tuple2<Tuple2<Object, K>, C>> memoryIterator) {
      Object var10000;
      if (this.isShuffleSort()) {
         var10000 = memoryIterator;
      } else {
         this.readingIterator_$eq(new SpillableIterator(memoryIterator));
         var10000 = this.readingIterator();
      }

      return (Iterator<Tuple2<Tuple2<Object, K>, C>>)var10000;
   }

   public Iterator<Tuple2<Object, Iterator<Product2<K, C>>>> partitionedIterator() {
      boolean usingMap = this.org$apache$spark$util$collection$ExternalSorter$$aggregator.isDefined();
      WritablePartitionedPairCollection collection = (WritablePartitionedPairCollection)(usingMap ? this.map() : this.buffer());
      return this.spills().isEmpty() ? (this.org$apache$spark$util$collection$ExternalSorter$$ordering.isDefined() ? this.groupByPartition(this.destructiveIterator(collection.partitionedDestructiveSortedIterator(new Some(this.org$apache$spark$util$collection$ExternalSorter$$keyComparator())))) : this.groupByPartition(this.destructiveIterator(collection.partitionedDestructiveSortedIterator(None$.MODULE$)))) : this.merge(this.spills(), this.destructiveIterator(collection.partitionedDestructiveSortedIterator(this.comparator())));
   }

   public Iterator<Product2<K, C>> iterator() {
      this.isShuffleSort_$eq(false);
      return this.partitionedIterator().<Product2<K, C>>flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Iterator<Product2<K, C>> apply(Tuple2<Object, Iterator<Product2<K, C>>> pair) {
            return (Iterator)pair._2();
         }
      });
   }

   public long[] writePartitionedFile(BlockId blockId, File outputFile) {
      long[] lengths = new long[this.org$apache$spark$util$collection$ExternalSorter$$numPartitions()];
      DiskBlockObjectWriter writer = this.blockManager().getDiskWriter(blockId, outputFile, this.org$apache$spark$util$collection$ExternalSorter$$serInstance(), this.fileBufferSize(), this.org$apache$spark$util$collection$ExternalSorter$$context.taskMetrics().shuffleWriteMetrics());
      if (this.spills().isEmpty()) {
         SizeTracker collection = (SizeTracker)(this.org$apache$spark$util$collection$ExternalSorter$$aggregator.isDefined() ? this.map() : this.buffer());

         int partitionId;
         FileSegment segment;
         for(WritablePartitionedIterator it = ((WritablePartitionedPairCollection)collection).destructiveSortedWritablePartitionedIterator(this.comparator()); it.hasNext(); lengths[partitionId] = segment.length()) {
            partitionId = it.nextPartition();

            while(it.hasNext() && it.nextPartition() == partitionId) {
               it.writeNext(writer);
            }

            segment = writer.commitAndGet();
         }
      } else {
         this.partitionedIterator().withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Tuple2<Object, Iterator<Product2<K, C>>> check$ifrefutable$1) {
               boolean var3;
               if (check$ifrefutable$1 != null) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }
         }).foreach(new Serializable(this, lengths, writer) {
            public static final long serialVersionUID = 0L;
            private final long[] lengths$1;
            public final DiskBlockObjectWriter writer$2;

            public final void apply(Tuple2<Object, Iterator<Product2<K, C>>> x$9) {
               if (x$9 != null) {
                  int id = x$9._1$mcI$sp();
                  Iterator elements = (Iterator)x$9._2();
                  if (elements.hasNext()) {
                     elements.foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final void apply(Product2<K, C> elem) {
                           this.$outer.writer$2.write(elem._1(), elem._2());
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     FileSegment segment = this.writer$2.commitAndGet();
                     this.lengths$1[id] = segment.length();
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     BoxedUnit var7 = BoxedUnit.UNIT;
                  }

               } else {
                  throw new MatchError(x$9);
               }
            }

            public {
               this.lengths$1 = lengths$1;
               this.writer$2 = writer$2;
            }
         });
      }

      writer.close();
      this.org$apache$spark$util$collection$ExternalSorter$$context.taskMetrics().incMemoryBytesSpilled(this.memoryBytesSpilled());
      this.org$apache$spark$util$collection$ExternalSorter$$context.taskMetrics().incDiskBytesSpilled(this.diskBytesSpilled());
      this.org$apache$spark$util$collection$ExternalSorter$$context.taskMetrics().incPeakExecutionMemory(this.peakMemoryUsedBytes());
      return lengths;
   }

   public void stop() {
      this.spills().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ExternalSorter<K, V, C>.SpilledFile s) {
            return s.file().delete();
         }
      });
      this.spills().clear();
      this.org$apache$spark$util$collection$ExternalSorter$$forceSpillFiles().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(ExternalSorter<K, V, C>.SpilledFile s) {
            return s.file().delete();
         }
      });
      this.org$apache$spark$util$collection$ExternalSorter$$forceSpillFiles().clear();
      if (this.map() != null || this.buffer() != null) {
         this.map_$eq((PartitionedAppendOnlyMap)null);
         this.buffer_$eq((PartitionedPairBuffer)null);
         this.releaseMemory();
      }

   }

   private Iterator<Tuple2<Object, Iterator<Product2<K, C>>>> groupByPartition(Iterator<Tuple2<Tuple2<Object, K>, C>> data) {
      BufferedIterator buffered = data.buffered();
      return RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.org$apache$spark$util$collection$ExternalSorter$$numPartitions()).iterator().<Tuple2<Object, Iterator<Product2<K, C>>>>map(new Serializable(this, buffered) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ExternalSorter $outer;
         private final BufferedIterator buffered$1;

         public final Tuple2<Object, ExternalSorter<K, V, C>.IteratorForPartition> apply(int p) {
            return new Tuple2<Object, ExternalSorter<K, V, C>.IteratorForPartition>(BoxesRunTime.boxToInteger(p), this.$outer.new IteratorForPartition(p, this.buffered$1));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.buffered$1 = buffered$1;
            }
         }
      });
   }

   private final void flush$1(LongRef objectsWritten$1, DiskBlockObjectWriter writer$1, ArrayBuffer batchSizes$1) {
      FileSegment segment = writer$1.commitAndGet();
      batchSizes$1.$plus$eq(BoxesRunTime.boxToLong(segment.length()));
      this._diskBytesSpilled_$eq(this._diskBytesSpilled() + segment.length());
      objectsWritten$1.elem = 0L;
   }

   public ExternalSorter(TaskContext context, Option<Aggregator<K, V, C>> aggregator, Option<Partitioner> partitioner, Option<Ordering<K>> ordering, Serializer serializer) {
      super(context.taskMemoryManager());
      this.org$apache$spark$util$collection$ExternalSorter$$context = context;
      this.org$apache$spark$util$collection$ExternalSorter$$aggregator = aggregator;
      this.partitioner = partitioner;
      this.org$apache$spark$util$collection$ExternalSorter$$ordering = ordering;
      this.conf = SparkEnv$.MODULE$.get().conf();
      this.org$apache$spark$util$collection$ExternalSorter$$numPartitions = BoxesRunTime.unboxToInt(partitioner.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Partitioner x$1) {
            return x$1.numPartitions();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 1;
         }
      }));
      this.shouldPartition = this.org$apache$spark$util$collection$ExternalSorter$$numPartitions() > 1;
      this.blockManager = SparkEnv$.MODULE$.get().blockManager();
      this.diskBlockManager = this.blockManager().diskBlockManager();
      this.org$apache$spark$util$collection$ExternalSorter$$serializerManager = SparkEnv$.MODULE$.get().serializerManager();
      this.org$apache$spark$util$collection$ExternalSorter$$serInstance = serializer.newInstance();
      this.fileBufferSize = (int)this.conf().getSizeAsKb("spark.shuffle.file.buffer", "32k") * 1024;
      this.org$apache$spark$util$collection$ExternalSorter$$serializerBatchSize = this.conf().getLong("spark.shuffle.spill.batchSize", 10000L);
      this.map = new PartitionedAppendOnlyMap<K, C>();
      this.buffer = new PartitionedPairBuffer<K, C>(PartitionedPairBuffer$.MODULE$.$lessinit$greater$default$1());
      this._diskBytesSpilled = 0L;
      this._peakMemoryUsedBytes = 0L;
      this.isShuffleSort = true;
      this.org$apache$spark$util$collection$ExternalSorter$$forceSpillFiles = new ArrayBuffer<ExternalSorter<K, V, C>.SpilledFile>();
      this.readingIterator = null;
      this.org$apache$spark$util$collection$ExternalSorter$$keyComparator = (Comparator)ordering.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final <undefinedtype> apply() {
            return new Comparator<K>(this) {
               public int compare(K a, K b) {
                  int h1 = a == null ? 0 : a.hashCode();
                  int h2 = b == null ? 0 : b.hashCode();
                  return h1 < h2 ? -1 : (h1 == h2 ? 0 : 1);
               }
            };
         }
      });
      this.spills = new ArrayBuffer<ExternalSorter<K, V, C>.SpilledFile>();
   }

   public class SpilledFile$ extends AbstractFunction4<File, BlockId, long[], long[], ExternalSorter<K, V, C>.SpilledFile> implements Serializable {
      // $FF: synthetic field
      private final ExternalSorter $outer;

      public final String toString() {
         return "SpilledFile";
      }

      public ExternalSorter<K, V, C>.SpilledFile apply(File file, BlockId blockId, long[] serializerBatchSizes, long[] elementsPerPartition) {
         return this.$outer.new SpilledFile(file, blockId, serializerBatchSizes, elementsPerPartition);
      }

      public Option<Tuple4<File, BlockId, long[], long[]>> unapply(ExternalSorter<K, V, C>.SpilledFile x$0) {
         return (Option<Tuple4<File, BlockId, long[], long[]>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple4(x$0.file(), x$0.blockId(), x$0.serializerBatchSizes(), x$0.elementsPerPartition())));
      }

      public SpilledFile$() {
         if (ExternalSorter.this == null) {
            throw null;
         } else {
            this.$outer = ExternalSorter.this;
            super();
         }
      }
   }

   public class SpilledFile implements Product, Serializable {
      private final File file;
      private final BlockId blockId;
      private final long[] serializerBatchSizes;
      private final long[] elementsPerPartition;
      // $FF: synthetic field
      public final ExternalSorter $outer;

      public File file() {
         return this.file;
      }

      public BlockId blockId() {
         return this.blockId;
      }

      public long[] serializerBatchSizes() {
         return this.serializerBatchSizes;
      }

      public long[] elementsPerPartition() {
         return this.elementsPerPartition;
      }

      public ExternalSorter<K, V, C>.SpilledFile copy(File file, BlockId blockId, long[] serializerBatchSizes, long[] elementsPerPartition) {
         return this.org$apache$spark$util$collection$ExternalSorter$SpilledFile$$$outer().new SpilledFile(file, blockId, serializerBatchSizes, elementsPerPartition);
      }

      public File copy$default$1() {
         return this.file();
      }

      public BlockId copy$default$2() {
         return this.blockId();
      }

      public long[] copy$default$3() {
         return this.serializerBatchSizes();
      }

      public long[] copy$default$4() {
         return this.elementsPerPartition();
      }

      public String productPrefix() {
         return "SpilledFile";
      }

      public int productArity() {
         return 4;
      }

      public Object productElement(int x$1) {
         Object var10000;
         switch (x$1) {
            case 0:
               var10000 = this.file();
               break;
            case 1:
               var10000 = this.blockId();
               break;
            case 2:
               var10000 = this.serializerBatchSizes();
               break;
            case 3:
               var10000 = this.elementsPerPartition();
               break;
            default:
               throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
         }

         return var10000;
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof SpilledFile;
      }

      public int hashCode() {
         return ScalaRunTime$.MODULE$._hashCode(this);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var9;
         if (this != x$1) {
            label66: {
               boolean var3;
               if (x$1 instanceof SpilledFile) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label48: {
                     label57: {
                        SpilledFile var4 = (SpilledFile)x$1;
                        File var10000 = this.file();
                        File var5 = var4.file();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label57;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label57;
                        }

                        BlockId var7 = this.blockId();
                        BlockId var6 = var4.blockId();
                        if (var7 == null) {
                           if (var6 != null) {
                              break label57;
                           }
                        } else if (!var7.equals(var6)) {
                           break label57;
                        }

                        if (this.serializerBatchSizes() == var4.serializerBatchSizes() && this.elementsPerPartition() == var4.elementsPerPartition() && var4.canEqual(this)) {
                           var9 = true;
                           break label48;
                        }
                     }

                     var9 = false;
                  }

                  if (var9) {
                     break label66;
                  }
               }

               var9 = false;
               return var9;
            }
         }

         var9 = true;
         return var9;
      }

      // $FF: synthetic method
      public ExternalSorter org$apache$spark$util$collection$ExternalSorter$SpilledFile$$$outer() {
         return this.$outer;
      }

      public SpilledFile(ExternalSorter<K, V, C> file, File blockId, BlockId serializerBatchSizes, long[] elementsPerPartition) {
         this.file = file;
         this.blockId = blockId;
         this.serializerBatchSizes = serializerBatchSizes;
         this.elementsPerPartition = elementsPerPartition;
         if (ExternalSorter.this == null) {
            throw null;
         } else {
            this.$outer = ExternalSorter.this;
            super();
            Product$class.$init$(this);
         }
      }
   }

   public class SpillReader {
      private final ExternalSorter<K, V, C>.SpilledFile spill;
      private final long[] batchOffsets;
      private int partitionId;
      private long indexInPartition;
      private int batchId;
      private int indexInBatch;
      private int lastPartitionId;
      private FileInputStream fileStream;
      private DeserializationStream deserializeStream;
      private Tuple2<K, C> nextItem;
      private boolean finished;
      private int nextPartitionToRead;
      // $FF: synthetic field
      public final ExternalSorter $outer;

      public long[] batchOffsets() {
         return this.batchOffsets;
      }

      public int partitionId() {
         return this.partitionId;
      }

      public void partitionId_$eq(int x$1) {
         this.partitionId = x$1;
      }

      public long indexInPartition() {
         return this.indexInPartition;
      }

      public void indexInPartition_$eq(long x$1) {
         this.indexInPartition = x$1;
      }

      public int batchId() {
         return this.batchId;
      }

      public void batchId_$eq(int x$1) {
         this.batchId = x$1;
      }

      public int indexInBatch() {
         return this.indexInBatch;
      }

      public void indexInBatch_$eq(int x$1) {
         this.indexInBatch = x$1;
      }

      public int lastPartitionId() {
         return this.lastPartitionId;
      }

      public void lastPartitionId_$eq(int x$1) {
         this.lastPartitionId = x$1;
      }

      public FileInputStream fileStream() {
         return this.fileStream;
      }

      public void fileStream_$eq(FileInputStream x$1) {
         this.fileStream = x$1;
      }

      public DeserializationStream deserializeStream() {
         return this.deserializeStream;
      }

      public void deserializeStream_$eq(DeserializationStream x$1) {
         this.deserializeStream = x$1;
      }

      public Tuple2<K, C> nextItem() {
         return this.nextItem;
      }

      public void nextItem_$eq(Tuple2<K, C> x$1) {
         this.nextItem = x$1;
      }

      public boolean finished() {
         return this.finished;
      }

      public void finished_$eq(boolean x$1) {
         this.finished = x$1;
      }

      public DeserializationStream nextBatchStream() {
         DeserializationStream var10000;
         if (this.batchId() < this.batchOffsets().length - 1) {
            if (this.deserializeStream() != null) {
               this.deserializeStream().close();
               this.fileStream().close();
               this.deserializeStream_$eq((DeserializationStream)null);
               this.fileStream_$eq((FileInputStream)null);
            }

            long start = this.batchOffsets()[this.batchId()];
            this.fileStream_$eq(new FileInputStream(this.spill.file()));
            this.fileStream().getChannel().position(start);
            this.batchId_$eq(this.batchId() + 1);
            long end = this.batchOffsets()[this.batchId()];
            Predef$.MODULE$.assert(end >= start, new Serializable(this, start, end) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SpillReader $outer;
               private final long start$1;
               private final long end$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"start = ").append((Object)BoxesRunTime.boxToLong(this.start$1)).append((Object)", end = ").append((Object)BoxesRunTime.boxToLong(this.end$1)).append((Object)", batchOffsets = ").append((Object)Predef$.MODULE$.longArrayOps(this.$outer.batchOffsets()).mkString("[", ", ", "]")).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.start$1 = start$1;
                     this.end$1 = end$1;
                  }
               }
            });
            BufferedInputStream bufferedStream = new BufferedInputStream(ByteStreams.limit(this.fileStream(), end - start));
            InputStream wrappedStream = this.org$apache$spark$util$collection$ExternalSorter$SpillReader$$$outer().org$apache$spark$util$collection$ExternalSorter$$serializerManager().wrapStream(this.spill.blockId(), (InputStream)bufferedStream);
            var10000 = this.org$apache$spark$util$collection$ExternalSorter$SpillReader$$$outer().org$apache$spark$util$collection$ExternalSorter$$serInstance().deserializeStream(wrappedStream);
         } else {
            this.cleanup();
            var10000 = null;
         }

         return var10000;
      }

      private void skipToNextPartition() {
         while(this.partitionId() < this.org$apache$spark$util$collection$ExternalSorter$SpillReader$$$outer().org$apache$spark$util$collection$ExternalSorter$$numPartitions() && this.indexInPartition() == this.spill.elementsPerPartition()[this.partitionId()]) {
            this.partitionId_$eq(this.partitionId() + 1);
            this.indexInPartition_$eq(0L);
         }

      }

      public Tuple2<K, C> org$apache$spark$util$collection$ExternalSorter$SpillReader$$readNextItem() {
         if (!this.finished() && this.deserializeStream() != null) {
            Object k = this.deserializeStream().readKey(ClassTag$.MODULE$.Nothing());
            Object c = this.deserializeStream().readValue(ClassTag$.MODULE$.Nothing());
            this.lastPartitionId_$eq(this.partitionId());
            this.indexInBatch_$eq(this.indexInBatch() + 1);
            if ((long)this.indexInBatch() == this.org$apache$spark$util$collection$ExternalSorter$SpillReader$$$outer().org$apache$spark$util$collection$ExternalSorter$$serializerBatchSize()) {
               this.indexInBatch_$eq(0);
               this.deserializeStream_$eq(this.nextBatchStream());
            }

            this.indexInPartition_$eq(this.indexInPartition() + 1L);
            this.skipToNextPartition();
            if (this.partitionId() == this.org$apache$spark$util$collection$ExternalSorter$SpillReader$$$outer().org$apache$spark$util$collection$ExternalSorter$$numPartitions()) {
               this.finished_$eq(true);
               if (this.deserializeStream() != null) {
                  this.deserializeStream().close();
               }
            }

            return new Tuple2<K, C>(k, c);
         } else {
            Object var10000 = null;
            return null;
         }
      }

      public int nextPartitionToRead() {
         return this.nextPartitionToRead;
      }

      public void nextPartitionToRead_$eq(int x$1) {
         this.nextPartitionToRead = x$1;
      }

      public Iterator<Product2<K, C>> readNextPartition() {
         return new Iterator<Product2<K, C>>(this) {
            private final int myPartition;
            // $FF: synthetic field
            private final SpillReader $outer;

            public Iterator<Product2<K, C>> seq() {
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

            public Iterator<Product2<K, C>> take(int n) {
               return Iterator$class.take(this, n);
            }

            public Iterator<Product2<K, C>> drop(int n) {
               return Iterator$class.drop(this, n);
            }

            public Iterator<Product2<K, C>> slice(int from, int until) {
               return Iterator$class.slice(this, from, until);
            }

            public <B> Iterator<B> map(Function1<Product2<K, C>, B> f) {
               return Iterator$class.map(this, f);
            }

            public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
               return Iterator$class.$plus$plus(this, that);
            }

            public <B> Iterator<B> flatMap(Function1<Product2<K, C>, GenTraversableOnce<B>> f) {
               return Iterator$class.flatMap(this, f);
            }

            public Iterator<Product2<K, C>> filter(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.filter(this, p);
            }

            public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Product2<K, C>, B, Object> p) {
               return Iterator$class.corresponds(this, that, p);
            }

            public Iterator<Product2<K, C>> withFilter(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.withFilter(this, p);
            }

            public Iterator<Product2<K, C>> filterNot(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.filterNot(this, p);
            }

            public <B> Iterator<B> collect(PartialFunction<Product2<K, C>, B> pf) {
               return Iterator$class.collect(this, pf);
            }

            public <B> Iterator<B> scanLeft(B z, Function2<B, Product2<K, C>, B> op) {
               return Iterator$class.scanLeft(this, z, op);
            }

            public <B> Iterator<B> scanRight(B z, Function2<Product2<K, C>, B, B> op) {
               return Iterator$class.scanRight(this, z, op);
            }

            public Iterator<Product2<K, C>> takeWhile(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.takeWhile(this, p);
            }

            public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> partition(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.partition(this, p);
            }

            public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> span(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.span(this, p);
            }

            public Iterator<Product2<K, C>> dropWhile(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.dropWhile(this, p);
            }

            public <B> Iterator<Tuple2<Product2<K, C>, B>> zip(Iterator<B> that) {
               return Iterator$class.zip(this, that);
            }

            public <A1> Iterator<A1> padTo(int len, A1 elem) {
               return Iterator$class.padTo(this, len, elem);
            }

            public Iterator<Tuple2<Product2<K, C>, Object>> zipWithIndex() {
               return Iterator$class.zipWithIndex(this);
            }

            public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
               return Iterator$class.zipAll(this, that, thisElem, thatElem);
            }

            public <U> void foreach(Function1<Product2<K, C>, U> f) {
               Iterator$class.foreach(this, f);
            }

            public boolean forall(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.forall(this, p);
            }

            public boolean exists(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.exists(this, p);
            }

            public boolean contains(Object elem) {
               return Iterator$class.contains(this, elem);
            }

            public Option<Product2<K, C>> find(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.find(this, p);
            }

            public int indexWhere(Function1<Product2<K, C>, Object> p) {
               return Iterator$class.indexWhere(this, p);
            }

            public <B> int indexOf(B elem) {
               return Iterator$class.indexOf(this, elem);
            }

            public BufferedIterator<Product2<K, C>> buffered() {
               return Iterator$class.buffered(this);
            }

            public <B> Iterator<Product2<K, C>>.GroupedIterator<B> grouped(int size) {
               return Iterator$class.grouped(this, size);
            }

            public <B> Iterator<Product2<K, C>>.GroupedIterator<B> sliding(int size, int step) {
               return Iterator$class.sliding(this, size, step);
            }

            public int length() {
               return Iterator$class.length(this);
            }

            public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> duplicate() {
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

            public Traversable<Product2<K, C>> toTraversable() {
               return Iterator$class.toTraversable(this);
            }

            public Iterator<Product2<K, C>> toIterator() {
               return Iterator$class.toIterator(this);
            }

            public Stream<Product2<K, C>> toStream() {
               return Iterator$class.toStream(this);
            }

            public String toString() {
               return Iterator$class.toString(this);
            }

            public <B> int sliding$default$2() {
               return Iterator$class.sliding$default$2(this);
            }

            public List<Product2<K, C>> reversed() {
               return TraversableOnce$class.reversed(this);
            }

            public int size() {
               return TraversableOnce$class.size(this);
            }

            public boolean nonEmpty() {
               return TraversableOnce$class.nonEmpty(this);
            }

            public int count(Function1<Product2<K, C>, Object> p) {
               return TraversableOnce$class.count(this, p);
            }

            public <B> Option<B> collectFirst(PartialFunction<Product2<K, C>, B> pf) {
               return TraversableOnce$class.collectFirst(this, pf);
            }

            public <B> B $div$colon(B z, Function2<B, Product2<K, C>, B> op) {
               return (B)TraversableOnce$class.$div$colon(this, z, op);
            }

            public <B> B $colon$bslash(B z, Function2<Product2<K, C>, B, B> op) {
               return (B)TraversableOnce$class.$colon$bslash(this, z, op);
            }

            public <B> B foldLeft(B z, Function2<B, Product2<K, C>, B> op) {
               return (B)TraversableOnce$class.foldLeft(this, z, op);
            }

            public <B> B foldRight(B z, Function2<Product2<K, C>, B, B> op) {
               return (B)TraversableOnce$class.foldRight(this, z, op);
            }

            public <B> B reduceLeft(Function2<B, Product2<K, C>, B> op) {
               return (B)TraversableOnce$class.reduceLeft(this, op);
            }

            public <B> B reduceRight(Function2<Product2<K, C>, B, B> op) {
               return (B)TraversableOnce$class.reduceRight(this, op);
            }

            public <B> Option<B> reduceLeftOption(Function2<B, Product2<K, C>, B> op) {
               return TraversableOnce$class.reduceLeftOption(this, op);
            }

            public <B> Option<B> reduceRightOption(Function2<Product2<K, C>, B, B> op) {
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

            public <B> B aggregate(Function0<B> z, Function2<B, Product2<K, C>, B> seqop, Function2<B, B, B> combop) {
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

            public List<Product2<K, C>> toList() {
               return TraversableOnce$class.toList(this);
            }

            public Iterable<Product2<K, C>> toIterable() {
               return TraversableOnce$class.toIterable(this);
            }

            public Seq<Product2<K, C>> toSeq() {
               return TraversableOnce$class.toSeq(this);
            }

            public IndexedSeq<Product2<K, C>> toIndexedSeq() {
               return TraversableOnce$class.toIndexedSeq(this);
            }

            public <B> Buffer<B> toBuffer() {
               return TraversableOnce$class.toBuffer(this);
            }

            public <B> Set<B> toSet() {
               return TraversableOnce$class.toSet(this);
            }

            public Vector<Product2<K, C>> toVector() {
               return TraversableOnce$class.toVector(this);
            }

            public <Col> Col to(CanBuildFrom<Nothing$, Product2<K, C>, Col> cbf) {
               return (Col)TraversableOnce$class.to(this, cbf);
            }

            public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Product2<K, C>, Tuple2<T, U>> ev) {
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

            private int myPartition() {
               return this.myPartition;
            }

            public boolean hasNext() {
               if (this.$outer.nextItem() == null) {
                  this.$outer.nextItem_$eq(this.$outer.org$apache$spark$util$collection$ExternalSorter$SpillReader$$readNextItem());
                  if (this.$outer.nextItem() == null) {
                     return false;
                  }
               }

               Predef$.MODULE$.assert(this.$outer.lastPartitionId() >= this.myPartition());
               return this.$outer.lastPartitionId() == this.myPartition();
            }

            public Product2<K, C> next() {
               if (this.hasNext()) {
                  Tuple2 item = this.$outer.nextItem();
                  this.$outer.nextItem_$eq((Tuple2)null);
                  return item;
               } else {
                  throw new NoSuchElementException();
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  TraversableOnce$class.$init$(this);
                  Iterator$class.$init$(this);
                  this.myPartition = $outer.nextPartitionToRead();
                  $outer.nextPartitionToRead_$eq($outer.nextPartitionToRead() + 1);
               }
            }
         };
      }

      public void cleanup() {
         this.batchId_$eq(this.batchOffsets().length);
         DeserializationStream ds = this.deserializeStream();
         this.deserializeStream_$eq((DeserializationStream)null);
         this.fileStream_$eq((FileInputStream)null);
         if (ds != null) {
            ds.close();
         }

      }

      // $FF: synthetic method
      public ExternalSorter org$apache$spark$util$collection$ExternalSorter$SpillReader$$$outer() {
         return this.$outer;
      }

      public SpillReader(ExternalSorter<K, V, C> spill) {
         this.spill = spill;
         if (ExternalSorter.this == null) {
            throw null;
         } else {
            this.$outer = ExternalSorter.this;
            super();
            this.batchOffsets = (long[])Predef$.MODULE$.longArrayOps(spill.serializerBatchSizes()).scanLeft(BoxesRunTime.boxToLong(0L), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(long x$7, long x$8) {
                  return this.apply$mcJJJ$sp(x$7, x$8);
               }

               public long apply$mcJJJ$sp(long x$7, long x$8) {
                  return x$7 + x$8;
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()));
            this.partitionId = 0;
            this.indexInPartition = 0L;
            this.batchId = 0;
            this.indexInBatch = 0;
            this.lastPartitionId = 0;
            this.skipToNextPartition();
            this.fileStream = null;
            this.deserializeStream = this.nextBatchStream();
            this.nextItem = null;
            this.finished = false;
            this.nextPartitionToRead = 0;
         }
      }
   }

   public class IteratorForPartition implements Iterator<Product2<K, C>> {
      private final int partitionId;
      private final BufferedIterator<Tuple2<Tuple2<Object, K>, C>> data;
      // $FF: synthetic field
      public final ExternalSorter $outer;

      public Iterator<Product2<K, C>> seq() {
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

      public Iterator<Product2<K, C>> take(int n) {
         return Iterator$class.take(this, n);
      }

      public Iterator<Product2<K, C>> drop(int n) {
         return Iterator$class.drop(this, n);
      }

      public Iterator<Product2<K, C>> slice(int from, int until) {
         return Iterator$class.slice(this, from, until);
      }

      public <B> Iterator<B> map(Function1<Product2<K, C>, B> f) {
         return Iterator$class.map(this, f);
      }

      public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
         return Iterator$class.$plus$plus(this, that);
      }

      public <B> Iterator<B> flatMap(Function1<Product2<K, C>, GenTraversableOnce<B>> f) {
         return Iterator$class.flatMap(this, f);
      }

      public Iterator<Product2<K, C>> filter(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.filter(this, p);
      }

      public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Product2<K, C>, B, Object> p) {
         return Iterator$class.corresponds(this, that, p);
      }

      public Iterator<Product2<K, C>> withFilter(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.withFilter(this, p);
      }

      public Iterator<Product2<K, C>> filterNot(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.filterNot(this, p);
      }

      public <B> Iterator<B> collect(PartialFunction<Product2<K, C>, B> pf) {
         return Iterator$class.collect(this, pf);
      }

      public <B> Iterator<B> scanLeft(B z, Function2<B, Product2<K, C>, B> op) {
         return Iterator$class.scanLeft(this, z, op);
      }

      public <B> Iterator<B> scanRight(B z, Function2<Product2<K, C>, B, B> op) {
         return Iterator$class.scanRight(this, z, op);
      }

      public Iterator<Product2<K, C>> takeWhile(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.takeWhile(this, p);
      }

      public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> partition(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.partition(this, p);
      }

      public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> span(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.span(this, p);
      }

      public Iterator<Product2<K, C>> dropWhile(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.dropWhile(this, p);
      }

      public <B> Iterator<Tuple2<Product2<K, C>, B>> zip(Iterator<B> that) {
         return Iterator$class.zip(this, that);
      }

      public <A1> Iterator<A1> padTo(int len, A1 elem) {
         return Iterator$class.padTo(this, len, elem);
      }

      public Iterator<Tuple2<Product2<K, C>, Object>> zipWithIndex() {
         return Iterator$class.zipWithIndex(this);
      }

      public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
         return Iterator$class.zipAll(this, that, thisElem, thatElem);
      }

      public <U> void foreach(Function1<Product2<K, C>, U> f) {
         Iterator$class.foreach(this, f);
      }

      public boolean forall(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.forall(this, p);
      }

      public boolean exists(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.exists(this, p);
      }

      public boolean contains(Object elem) {
         return Iterator$class.contains(this, elem);
      }

      public Option<Product2<K, C>> find(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.find(this, p);
      }

      public int indexWhere(Function1<Product2<K, C>, Object> p) {
         return Iterator$class.indexWhere(this, p);
      }

      public <B> int indexOf(B elem) {
         return Iterator$class.indexOf(this, elem);
      }

      public BufferedIterator<Product2<K, C>> buffered() {
         return Iterator$class.buffered(this);
      }

      public <B> Iterator<Product2<K, C>>.GroupedIterator<B> grouped(int size) {
         return Iterator$class.grouped(this, size);
      }

      public <B> Iterator<Product2<K, C>>.GroupedIterator<B> sliding(int size, int step) {
         return Iterator$class.sliding(this, size, step);
      }

      public int length() {
         return Iterator$class.length(this);
      }

      public Tuple2<Iterator<Product2<K, C>>, Iterator<Product2<K, C>>> duplicate() {
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

      public Traversable<Product2<K, C>> toTraversable() {
         return Iterator$class.toTraversable(this);
      }

      public Iterator<Product2<K, C>> toIterator() {
         return Iterator$class.toIterator(this);
      }

      public Stream<Product2<K, C>> toStream() {
         return Iterator$class.toStream(this);
      }

      public String toString() {
         return Iterator$class.toString(this);
      }

      public <B> int sliding$default$2() {
         return Iterator$class.sliding$default$2(this);
      }

      public List<Product2<K, C>> reversed() {
         return TraversableOnce$class.reversed(this);
      }

      public int size() {
         return TraversableOnce$class.size(this);
      }

      public boolean nonEmpty() {
         return TraversableOnce$class.nonEmpty(this);
      }

      public int count(Function1<Product2<K, C>, Object> p) {
         return TraversableOnce$class.count(this, p);
      }

      public <B> Option<B> collectFirst(PartialFunction<Product2<K, C>, B> pf) {
         return TraversableOnce$class.collectFirst(this, pf);
      }

      public <B> B $div$colon(B z, Function2<B, Product2<K, C>, B> op) {
         return (B)TraversableOnce$class.$div$colon(this, z, op);
      }

      public <B> B $colon$bslash(B z, Function2<Product2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.$colon$bslash(this, z, op);
      }

      public <B> B foldLeft(B z, Function2<B, Product2<K, C>, B> op) {
         return (B)TraversableOnce$class.foldLeft(this, z, op);
      }

      public <B> B foldRight(B z, Function2<Product2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.foldRight(this, z, op);
      }

      public <B> B reduceLeft(Function2<B, Product2<K, C>, B> op) {
         return (B)TraversableOnce$class.reduceLeft(this, op);
      }

      public <B> B reduceRight(Function2<Product2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.reduceRight(this, op);
      }

      public <B> Option<B> reduceLeftOption(Function2<B, Product2<K, C>, B> op) {
         return TraversableOnce$class.reduceLeftOption(this, op);
      }

      public <B> Option<B> reduceRightOption(Function2<Product2<K, C>, B, B> op) {
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

      public <B> B aggregate(Function0<B> z, Function2<B, Product2<K, C>, B> seqop, Function2<B, B, B> combop) {
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

      public List<Product2<K, C>> toList() {
         return TraversableOnce$class.toList(this);
      }

      public Iterable<Product2<K, C>> toIterable() {
         return TraversableOnce$class.toIterable(this);
      }

      public Seq<Product2<K, C>> toSeq() {
         return TraversableOnce$class.toSeq(this);
      }

      public IndexedSeq<Product2<K, C>> toIndexedSeq() {
         return TraversableOnce$class.toIndexedSeq(this);
      }

      public <B> Buffer<B> toBuffer() {
         return TraversableOnce$class.toBuffer(this);
      }

      public <B> Set<B> toSet() {
         return TraversableOnce$class.toSet(this);
      }

      public Vector<Product2<K, C>> toVector() {
         return TraversableOnce$class.toVector(this);
      }

      public <Col> Col to(CanBuildFrom<Nothing$, Product2<K, C>, Col> cbf) {
         return (Col)TraversableOnce$class.to(this, cbf);
      }

      public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Product2<K, C>, Tuple2<T, U>> ev) {
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
         return this.data.hasNext() && ((Tuple2)((Tuple2)this.data.head())._1())._1$mcI$sp() == this.partitionId;
      }

      public Product2<K, C> next() {
         if (this.hasNext()) {
            Tuple2 elem = (Tuple2)this.data.next();
            return new Tuple2<K, C>(((Tuple2)elem._1())._2(), elem._2());
         } else {
            throw new NoSuchElementException();
         }
      }

      // $FF: synthetic method
      public ExternalSorter org$apache$spark$util$collection$ExternalSorter$IteratorForPartition$$$outer() {
         return this.$outer;
      }

      public IteratorForPartition(ExternalSorter<K, V, C> partitionId, int data) {
         this.partitionId = partitionId;
         this.data = data;
         if (ExternalSorter.this == null) {
            throw null;
         } else {
            this.$outer = ExternalSorter.this;
            super();
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
         }
      }
   }

   public class SpillableIterator implements Iterator<Tuple2<Tuple2<Object, K>, C>> {
      private Iterator<Tuple2<Tuple2<Object, K>, C>> upstream;
      private final Object SPILL_LOCK;
      private Iterator<Tuple2<Tuple2<Object, K>, C>> nextUpstream;
      private Tuple2<Tuple2<Object, K>, C> cur;
      private boolean hasSpilled;
      // $FF: synthetic field
      public final ExternalSorter $outer;

      public Iterator<Tuple2<Tuple2<Object, K>, C>> seq() {
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

      public Iterator<Tuple2<Tuple2<Object, K>, C>> take(int n) {
         return Iterator$class.take(this, n);
      }

      public Iterator<Tuple2<Tuple2<Object, K>, C>> drop(int n) {
         return Iterator$class.drop(this, n);
      }

      public Iterator<Tuple2<Tuple2<Object, K>, C>> slice(int from, int until) {
         return Iterator$class.slice(this, from, until);
      }

      public <B> Iterator<B> map(Function1<Tuple2<Tuple2<Object, K>, C>, B> f) {
         return Iterator$class.map(this, f);
      }

      public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
         return Iterator$class.$plus$plus(this, that);
      }

      public <B> Iterator<B> flatMap(Function1<Tuple2<Tuple2<Object, K>, C>, GenTraversableOnce<B>> f) {
         return Iterator$class.flatMap(this, f);
      }

      public Iterator<Tuple2<Tuple2<Object, K>, C>> filter(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.filter(this, p);
      }

      public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<Tuple2<Object, K>, C>, B, Object> p) {
         return Iterator$class.corresponds(this, that, p);
      }

      public Iterator<Tuple2<Tuple2<Object, K>, C>> withFilter(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.withFilter(this, p);
      }

      public Iterator<Tuple2<Tuple2<Object, K>, C>> filterNot(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.filterNot(this, p);
      }

      public <B> Iterator<B> collect(PartialFunction<Tuple2<Tuple2<Object, K>, C>, B> pf) {
         return Iterator$class.collect(this, pf);
      }

      public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<Tuple2<Object, K>, C>, B> op) {
         return Iterator$class.scanLeft(this, z, op);
      }

      public <B> Iterator<B> scanRight(B z, Function2<Tuple2<Tuple2<Object, K>, C>, B, B> op) {
         return Iterator$class.scanRight(this, z, op);
      }

      public Iterator<Tuple2<Tuple2<Object, K>, C>> takeWhile(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.takeWhile(this, p);
      }

      public Tuple2<Iterator<Tuple2<Tuple2<Object, K>, C>>, Iterator<Tuple2<Tuple2<Object, K>, C>>> partition(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.partition(this, p);
      }

      public Tuple2<Iterator<Tuple2<Tuple2<Object, K>, C>>, Iterator<Tuple2<Tuple2<Object, K>, C>>> span(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.span(this, p);
      }

      public Iterator<Tuple2<Tuple2<Object, K>, C>> dropWhile(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.dropWhile(this, p);
      }

      public <B> Iterator<Tuple2<Tuple2<Tuple2<Object, K>, C>, B>> zip(Iterator<B> that) {
         return Iterator$class.zip(this, that);
      }

      public <A1> Iterator<A1> padTo(int len, A1 elem) {
         return Iterator$class.padTo(this, len, elem);
      }

      public Iterator<Tuple2<Tuple2<Tuple2<Object, K>, C>, Object>> zipWithIndex() {
         return Iterator$class.zipWithIndex(this);
      }

      public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
         return Iterator$class.zipAll(this, that, thisElem, thatElem);
      }

      public <U> void foreach(Function1<Tuple2<Tuple2<Object, K>, C>, U> f) {
         Iterator$class.foreach(this, f);
      }

      public boolean forall(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.forall(this, p);
      }

      public boolean exists(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.exists(this, p);
      }

      public boolean contains(Object elem) {
         return Iterator$class.contains(this, elem);
      }

      public Option<Tuple2<Tuple2<Object, K>, C>> find(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.find(this, p);
      }

      public int indexWhere(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return Iterator$class.indexWhere(this, p);
      }

      public <B> int indexOf(B elem) {
         return Iterator$class.indexOf(this, elem);
      }

      public BufferedIterator<Tuple2<Tuple2<Object, K>, C>> buffered() {
         return Iterator$class.buffered(this);
      }

      public <B> Iterator<Tuple2<Tuple2<Object, K>, C>>.GroupedIterator<B> grouped(int size) {
         return Iterator$class.grouped(this, size);
      }

      public <B> Iterator<Tuple2<Tuple2<Object, K>, C>>.GroupedIterator<B> sliding(int size, int step) {
         return Iterator$class.sliding(this, size, step);
      }

      public int length() {
         return Iterator$class.length(this);
      }

      public Tuple2<Iterator<Tuple2<Tuple2<Object, K>, C>>, Iterator<Tuple2<Tuple2<Object, K>, C>>> duplicate() {
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

      public Traversable<Tuple2<Tuple2<Object, K>, C>> toTraversable() {
         return Iterator$class.toTraversable(this);
      }

      public Iterator<Tuple2<Tuple2<Object, K>, C>> toIterator() {
         return Iterator$class.toIterator(this);
      }

      public Stream<Tuple2<Tuple2<Object, K>, C>> toStream() {
         return Iterator$class.toStream(this);
      }

      public String toString() {
         return Iterator$class.toString(this);
      }

      public <B> int sliding$default$2() {
         return Iterator$class.sliding$default$2(this);
      }

      public List<Tuple2<Tuple2<Object, K>, C>> reversed() {
         return TraversableOnce$class.reversed(this);
      }

      public int size() {
         return TraversableOnce$class.size(this);
      }

      public boolean nonEmpty() {
         return TraversableOnce$class.nonEmpty(this);
      }

      public int count(Function1<Tuple2<Tuple2<Object, K>, C>, Object> p) {
         return TraversableOnce$class.count(this, p);
      }

      public <B> Option<B> collectFirst(PartialFunction<Tuple2<Tuple2<Object, K>, C>, B> pf) {
         return TraversableOnce$class.collectFirst(this, pf);
      }

      public <B> B $div$colon(B z, Function2<B, Tuple2<Tuple2<Object, K>, C>, B> op) {
         return (B)TraversableOnce$class.$div$colon(this, z, op);
      }

      public <B> B $colon$bslash(B z, Function2<Tuple2<Tuple2<Object, K>, C>, B, B> op) {
         return (B)TraversableOnce$class.$colon$bslash(this, z, op);
      }

      public <B> B foldLeft(B z, Function2<B, Tuple2<Tuple2<Object, K>, C>, B> op) {
         return (B)TraversableOnce$class.foldLeft(this, z, op);
      }

      public <B> B foldRight(B z, Function2<Tuple2<Tuple2<Object, K>, C>, B, B> op) {
         return (B)TraversableOnce$class.foldRight(this, z, op);
      }

      public <B> B reduceLeft(Function2<B, Tuple2<Tuple2<Object, K>, C>, B> op) {
         return (B)TraversableOnce$class.reduceLeft(this, op);
      }

      public <B> B reduceRight(Function2<Tuple2<Tuple2<Object, K>, C>, B, B> op) {
         return (B)TraversableOnce$class.reduceRight(this, op);
      }

      public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<Tuple2<Object, K>, C>, B> op) {
         return TraversableOnce$class.reduceLeftOption(this, op);
      }

      public <B> Option<B> reduceRightOption(Function2<Tuple2<Tuple2<Object, K>, C>, B, B> op) {
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

      public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<Tuple2<Object, K>, C>, B> seqop, Function2<B, B, B> combop) {
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

      public List<Tuple2<Tuple2<Object, K>, C>> toList() {
         return TraversableOnce$class.toList(this);
      }

      public Iterable<Tuple2<Tuple2<Object, K>, C>> toIterable() {
         return TraversableOnce$class.toIterable(this);
      }

      public Seq<Tuple2<Tuple2<Object, K>, C>> toSeq() {
         return TraversableOnce$class.toSeq(this);
      }

      public IndexedSeq<Tuple2<Tuple2<Object, K>, C>> toIndexedSeq() {
         return TraversableOnce$class.toIndexedSeq(this);
      }

      public <B> Buffer<B> toBuffer() {
         return TraversableOnce$class.toBuffer(this);
      }

      public <B> Set<B> toSet() {
         return TraversableOnce$class.toSet(this);
      }

      public Vector<Tuple2<Tuple2<Object, K>, C>> toVector() {
         return TraversableOnce$class.toVector(this);
      }

      public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<Tuple2<Object, K>, C>, Col> cbf) {
         return (Col)TraversableOnce$class.to(this, cbf);
      }

      public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<Tuple2<Object, K>, C>, Tuple2<T, U>> ev) {
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

      public Iterator<Tuple2<Tuple2<Object, K>, C>> upstream() {
         return this.upstream;
      }

      public void upstream_$eq(Iterator<Tuple2<Tuple2<Object, K>, C>> x$1) {
         this.upstream = x$1;
      }

      private Object SPILL_LOCK() {
         return this.SPILL_LOCK;
      }

      private Iterator<Tuple2<Tuple2<Object, K>, C>> nextUpstream() {
         return this.nextUpstream;
      }

      private void nextUpstream_$eq(Iterator<Tuple2<Tuple2<Object, K>, C>> x$1) {
         this.nextUpstream = x$1;
      }

      private Tuple2<Tuple2<Object, K>, C> cur() {
         return this.cur;
      }

      private void cur_$eq(Tuple2<Tuple2<Object, K>, C> x$1) {
         this.cur = x$1;
      }

      private boolean hasSpilled() {
         return this.hasSpilled;
      }

      private void hasSpilled_$eq(boolean x$1) {
         this.hasSpilled = x$1;
      }

      public boolean spill() {
         Boolean var2;
         synchronized(this.SPILL_LOCK()) {
            Boolean var10000;
            if (this.hasSpilled()) {
               var10000 = BoxesRunTime.boxToBoolean(false);
            } else {
               WritablePartitionedIterator inMemoryIterator = new WritablePartitionedIterator(this) {
                  private Tuple2<Tuple2<Object, K>, C> cur;
                  // $FF: synthetic field
                  private final SpillableIterator $outer;

                  public void writeNext(DiskBlockObjectWriter writer) {
                     writer.write((this.cur._1())._2(), this.cur._2());
                     this.cur = this.$outer.upstream().hasNext() ? (Tuple2)this.$outer.upstream().next() : null;
                  }

                  public boolean hasNext() {
                     return this.cur != null;
                  }

                  public int nextPartition() {
                     return ((Tuple2)this.cur._1())._1$mcI$sp();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.cur = $outer.upstream().hasNext() ? (Tuple2)$outer.upstream().next() : null;
                     }
                  }
               };
               this.org$apache$spark$util$collection$ExternalSorter$SpillableIterator$$$outer().logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final SpillableIterator $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " force spilling in-memory map to disk and "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$util$collection$ExternalSorter$SpillableIterator$$$outer().org$apache$spark$util$collection$ExternalSorter$$context.taskAttemptId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" it will release ", " memory"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.util.Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$util$collection$ExternalSorter$SpillableIterator$$$outer().protected$getUsed(this.$outer.org$apache$spark$util$collection$ExternalSorter$SpillableIterator$$$outer()))}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               SpilledFile spillFile = this.org$apache$spark$util$collection$ExternalSorter$SpillableIterator$$$outer().org$apache$spark$util$collection$ExternalSorter$$spillMemoryIteratorToDisk(inMemoryIterator);
               this.org$apache$spark$util$collection$ExternalSorter$SpillableIterator$$$outer().org$apache$spark$util$collection$ExternalSorter$$forceSpillFiles().$plus$eq(spillFile);
               SpillReader spillReader = this.org$apache$spark$util$collection$ExternalSorter$SpillableIterator$$$outer().new SpillReader(spillFile);
               this.nextUpstream_$eq(RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.org$apache$spark$util$collection$ExternalSorter$SpillableIterator$$$outer().org$apache$spark$util$collection$ExternalSorter$$numPartitions()).iterator().flatMap(new Serializable(this, spillReader) {
                  public static final long serialVersionUID = 0L;
                  private final SpillReader spillReader$1;

                  public final Iterator<Tuple2<Tuple2<Object, K>, C>> apply(int p) {
                     Iterator iterator = this.spillReader$1.readNextPartition();
                     return iterator.<Tuple2<Tuple2<Object, K>, C>>map(new Serializable(this, p) {
                        public static final long serialVersionUID = 0L;
                        private final int p$1;

                        public final Tuple2<Tuple2<Object, K>, C> apply(Product2<K, C> cur) {
                           return new Tuple2<Tuple2<Object, K>, C>(new Tuple2(BoxesRunTime.boxToInteger(this.p$1), cur._1()), cur._2());
                        }

                        public {
                           this.p$1 = p$1;
                        }
                     });
                  }

                  public {
                     this.spillReader$1 = spillReader$1;
                  }
               }));
               this.hasSpilled_$eq(true);
               var10000 = BoxesRunTime.boxToBoolean(true);
            }

            var2 = var10000;
         }

         return BoxesRunTime.unboxToBoolean(var2);
      }

      public Tuple2<Tuple2<Object, K>, C> readNext() {
         Object var2;
         synchronized(this.SPILL_LOCK()) {
            if (this.nextUpstream() != null) {
               this.upstream_$eq(this.nextUpstream());
               this.nextUpstream_$eq((Iterator)null);
            }

            var2 = this.upstream().hasNext() ? this.upstream().next() : null;
         }

         return (Tuple2)var2;
      }

      public boolean hasNext() {
         return this.cur() != null;
      }

      public Tuple2<Tuple2<Object, K>, C> next() {
         Tuple2 r = this.cur();
         this.cur_$eq(this.readNext());
         return r;
      }

      // $FF: synthetic method
      public ExternalSorter org$apache$spark$util$collection$ExternalSorter$SpillableIterator$$$outer() {
         return this.$outer;
      }

      public SpillableIterator(ExternalSorter<K, V, C> upstream) {
         this.upstream = upstream;
         if (ExternalSorter.this == null) {
            throw null;
         } else {
            this.$outer = ExternalSorter.this;
            super();
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
            this.SPILL_LOCK = new Object();
            this.nextUpstream = null;
            this.cur = this.readNext();
            this.hasSpilled = false;
         }
      }
   }
}
