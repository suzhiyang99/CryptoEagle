package org.apache.spark.util.collection;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.annotation.DeveloperApi;
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
import org.apache.spark.storage.TempLocalBlockId;
import org.apache.spark.util.CompletionIterator;
import org.apache.spark.util.CompletionIterator$;
import org.spark_project.guava.io.ByteStreams;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Product2;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.package$;
import scala.collection.BufferedIterator;
import scala.collection.GenIterable;
import scala.collection.GenIterable$class;
import scala.collection.GenTraversable;
import scala.collection.GenTraversable$class;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterable$class;
import scala.collection.IterableLike$class;
import scala.collection.IterableView;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Parallel;
import scala.collection.Parallelizable$class;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Traversable;
import scala.collection.Traversable$class;
import scala.collection.TraversableLike;
import scala.collection.TraversableLike$class;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.generic.FilterMonadic;
import scala.collection.generic.GenericCompanion;
import scala.collection.generic.GenericTraversableTemplate$class;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Builder;
import scala.collection.mutable.PriorityQueue;
import scala.collection.mutable.StringBuilder;
import scala.collection.parallel.Combiner;
import scala.collection.parallel.ParIterable;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.math.Ordering$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0015\u001db\u0001B\u0001\u0003\u00015\u0011Q#\u0012=uKJt\u0017\r\\!qa\u0016tGm\u00148ms6\u000b\u0007O\u0003\u0002\u0004\t\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\u000b\u0005\u00151\u0011\u0001B;uS2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001+\u0011q\u0001H\u0013\"\u0014\u000b\u0001yaC\b\u0013\u0011\u0007A\t2#D\u0001\u0003\u0013\t\u0011\"AA\u0005Ta&dG.\u00192mKB\u0011\u0001\u0003F\u0005\u0003+\t\u00111bU5{KR\u0013\u0018mY6feB\u0011q\u0003H\u0007\u00021)\u0011\u0011DG\u0001\u0003S>T\u0011aG\u0001\u0005U\u00064\u0018-\u0003\u0002\u001e1\ta1+\u001a:jC2L'0\u00192mKB\u0011qDI\u0007\u0002A)\u0011\u0011EB\u0001\tS:$XM\u001d8bY&\u00111\u0005\t\u0002\b\u0019><w-\u001b8h!\r)sF\r\b\u0003M1r!a\n\u0016\u000e\u0003!R!!\u000b\u0007\u0002\rq\u0012xn\u001c;?\u0013\u0005Y\u0013!B:dC2\f\u0017BA\u0017/\u0003\u001d\u0001\u0018mY6bO\u0016T\u0011aK\u0005\u0003aE\u0012\u0001\"\u0013;fe\u0006\u0014G.\u001a\u0006\u0003[9\u0002Ba\r\u001b7\u00036\ta&\u0003\u00026]\t1A+\u001e9mKJ\u0002\"a\u000e\u001d\r\u0001\u0011)\u0011\b\u0001b\u0001u\t\t1*\u0005\u0002<}A\u00111\u0007P\u0005\u0003{9\u0012qAT8uQ&tw\r\u0005\u00024\u007f%\u0011\u0001I\f\u0002\u0004\u0003:L\bCA\u001cC\t\u0015\u0019\u0005A1\u0001;\u0005\u0005\u0019\u0005\u0002C#\u0001\u0005\u0003\u0005\u000b\u0011\u0002$\u0002\u001d\r\u0014X-\u0019;f\u0007>l'-\u001b8feB!1gR%B\u0013\tAeFA\u0005Gk:\u001cG/[8ocA\u0011qG\u0013\u0003\u0006\u0017\u0002\u0011\rA\u000f\u0002\u0002-\"AQ\n\u0001B\u0001B\u0003%a*\u0001\u0006nKJ<WMV1mk\u0016\u0004RaM(B\u0013\u0006K!\u0001\u0015\u0018\u0003\u0013\u0019+hn\u0019;j_:\u0014\u0004\u0002\u0003*\u0001\u0005\u0003\u0005\u000b\u0011B*\u0002\u001d5,'oZ3D_6\u0014\u0017N\\3sgB)1gT!B\u0003\"AQ\u000b\u0001B\u0001B\u0003%a+\u0001\u0006tKJL\u0017\r\\5{KJ\u0004\"aV-\u000e\u0003aS!!\u0016\u0004\n\u0005iC&AC*fe&\fG.\u001b>fe\"AA\f\u0001B\u0001B\u0003%Q,\u0001\u0007cY>\u001c7.T1oC\u001e,'\u000f\u0005\u0002_C6\tqL\u0003\u0002a\r\u000591\u000f^8sC\u001e,\u0017B\u00012`\u00051\u0011En\\2l\u001b\u0006t\u0017mZ3s\u0011!!\u0007A!A!\u0002\u0013)\u0017aB2p]R,\u0007\u0010\u001e\t\u0003M\u001el\u0011AB\u0005\u0003Q\u001a\u00111\u0002V1tW\u000e{g\u000e^3yi\"A!\u000e\u0001B\u0001B\u0003%1.A\ttKJL\u0017\r\\5{KJl\u0015M\\1hKJ\u0004\"a\u00167\n\u00055D&!E*fe&\fG.\u001b>fe6\u000bg.Y4fe\")q\u000e\u0001C\u0001a\u00061A(\u001b8jiz\"\u0002\"\u001d:tiV4x\u000f\u001f\t\u0006!\u00011\u0014*\u0011\u0005\u0006\u000b:\u0004\rA\u0012\u0005\u0006\u001b:\u0004\rA\u0014\u0005\u0006%:\u0004\ra\u0015\u0005\b+:\u0004\n\u00111\u0001W\u0011\u001daf\u000e%AA\u0002uCq\u0001\u001a8\u0011\u0002\u0003\u0007Q\rC\u0004k]B\u0005\t\u0019A6\t\u000b=\u0004A\u0011\u0001>\u0015\rE\\H0 @\u0000\u0011\u0015)\u0015\u00101\u0001G\u0011\u0015i\u0015\u00101\u0001O\u0011\u0015\u0011\u0016\u00101\u0001T\u0011\u0015)\u0016\u00101\u0001W\u0011\u0015a\u0016\u00101\u0001^\u0011%\t\u0019\u0001\u0001a\u0001\n\u0013\t)!\u0001\u0006dkJ\u0014XM\u001c;NCB,\"!a\u0002\u0011\u000bA\tIAN!\n\u0007\u0005-!AA\rTSj,GK]1dW&tw-\u00119qK:$wJ\u001c7z\u001b\u0006\u0004\b\"CA\b\u0001\u0001\u0007I\u0011BA\t\u00039\u0019WO\u001d:f]Rl\u0015\r]0%KF$B!a\u0005\u0002\u001aA\u00191'!\u0006\n\u0007\u0005]aF\u0001\u0003V]&$\bBCA\u000e\u0003\u001b\t\t\u00111\u0001\u0002\b\u0005\u0019\u0001\u0010J\u0019\t\u0011\u0005}\u0001\u0001)Q\u0005\u0003\u000f\t1bY;se\u0016tG/T1qA!\"\u0011QDA\u0012!\r\u0019\u0014QE\u0005\u0004\u0003Oq#\u0001\u0003<pY\u0006$\u0018\u000e\\3\t\u0013\u0005-\u0002A1A\u0005\n\u00055\u0012aC:qS2dW\rZ'baN,\"!a\f\u0011\r\u0005E\u0012\u0011HA\u001f\u001b\t\t\u0019D\u0003\u0003\u00026\u0005]\u0012aB7vi\u0006\u0014G.\u001a\u0006\u0003\u00079JA!a\u000f\u00024\tY\u0011I\u001d:bs\n+hMZ3s!\u0011\ty$!\u0011\u000e\u0003\u00011a!a\u0011\u0001\t\u0005\u0015#a\u0004#jg.l\u0015\r]%uKJ\fGo\u001c:\u0014\r\u0005\u0005\u0013qIA'!\r\u0019\u0014\u0011J\u0005\u0004\u0003\u0017r#AB!osJ+g\r\u0005\u0003&\u0003\u001f\u0012\u0014bAA)c\tA\u0011\n^3sCR|'\u000fC\u0006\u0002V\u0005\u0005#\u0011!Q\u0001\n\u0005]\u0013\u0001\u00024jY\u0016\u00042aFA-\u0013\r\tY\u0006\u0007\u0002\u0005\r&dW\rC\u0006\u0002`\u0005\u0005#\u0011!Q\u0001\n\u0005\u0005\u0014a\u00022m_\u000e\\\u0017\n\u001a\t\u0004=\u0006\r\u0014bAA3?\n9!\t\\8dW&#\u0007bCA5\u0003\u0003\u0012\t\u0011)A\u0005\u0003W\n!BY1uG\"\u001c\u0016N_3t!\u0019\t\t$!\u000f\u0002nA\u00191'a\u001c\n\u0007\u0005EdF\u0001\u0003M_:<\u0007bB8\u0002B\u0011\u0005\u0011Q\u000f\u000b\t\u0003{\t9(!\u001f\u0002|!A\u0011QKA:\u0001\u0004\t9\u0006\u0003\u0005\u0002`\u0005M\u0004\u0019AA1\u0011!\tI'a\u001dA\u0002\u0005-\u0004BCA@\u0003\u0003\u0012\r\u0011\"\u0003\u0002\u0002\u0006a!-\u0019;dQ>3gm]3ugV\u0011\u00111\u000e\u0005\n\u0003\u000b\u000b\t\u0005)A\u0005\u0003W\nQBY1uG\"|eMZ:fiN\u0004\u0003BCAE\u0003\u0003\u0002\r\u0011\"\u0003\u0002\f\u0006Q!-\u0019;dQ&sG-\u001a=\u0016\u0005\u00055\u0005cA\u001a\u0002\u0010&\u0019\u0011\u0011\u0013\u0018\u0003\u0007%sG\u000f\u0003\u0006\u0002\u0016\u0006\u0005\u0003\u0019!C\u0005\u0003/\u000baBY1uG\"Le\u000eZ3y?\u0012*\u0017\u000f\u0006\u0003\u0002\u0014\u0005e\u0005BCA\u000e\u0003'\u000b\t\u00111\u0001\u0002\u000e\"I\u0011QTA!A\u0003&\u0011QR\u0001\fE\u0006$8\r[%oI\u0016D\b\u0005\u0003\u0006\u0002\"\u0006\u0005\u0003\u0019!C\u0005\u0003G\u000b!BZ5mKN#(/Z1n+\t\t)\u000bE\u0002\u0018\u0003OK1!!+\u0019\u0005=1\u0015\u000e\\3J]B,Ho\u0015;sK\u0006l\u0007BCAW\u0003\u0003\u0002\r\u0011\"\u0003\u00020\u0006qa-\u001b7f'R\u0014X-Y7`I\u0015\fH\u0003BA\n\u0003cC!\"a\u0007\u0002,\u0006\u0005\t\u0019AAS\u0011%\t),!\u0011!B\u0013\t)+A\u0006gS2,7\u000b\u001e:fC6\u0004\u0003BCA]\u0003\u0003\u0002\r\u0011\"\u0003\u0002<\u0006\tB-Z:fe&\fG.\u001b>f'R\u0014X-Y7\u0016\u0005\u0005u\u0006cA,\u0002@&\u0019\u0011\u0011\u0019-\u0003+\u0011+7/\u001a:jC2L'0\u0019;j_:\u001cFO]3b[\"Q\u0011QYA!\u0001\u0004%I!a2\u0002+\u0011,7/\u001a:jC2L'0Z*ue\u0016\fWn\u0018\u0013fcR!\u00111CAe\u0011)\tY\"a1\u0002\u0002\u0003\u0007\u0011Q\u0018\u0005\n\u0003\u001b\f\t\u0005)Q\u0005\u0003{\u000b!\u0003Z3tKJL\u0017\r\\5{KN#(/Z1nA!Q\u0011\u0011[A!\u0001\u0004%I!a5\u0002\u00119,\u0007\u0010^%uK6,\u0012A\r\u0005\u000b\u0003/\f\t\u00051A\u0005\n\u0005e\u0017\u0001\u00048fqRLE/Z7`I\u0015\fH\u0003BA\n\u00037D\u0011\"a\u0007\u0002V\u0006\u0005\t\u0019\u0001\u001a\t\u0011\u0005}\u0017\u0011\tQ!\nI\n\u0011B\\3yi&#X-\u001c\u0011\t\u0015\u0005\r\u0018\u0011\ta\u0001\n\u0013\tY)A\u0006pE*,7\r^:SK\u0006$\u0007BCAt\u0003\u0003\u0002\r\u0011\"\u0003\u0002j\u0006yqN\u00196fGR\u001c(+Z1e?\u0012*\u0017\u000f\u0006\u0003\u0002\u0014\u0005-\bBCA\u000e\u0003K\f\t\u00111\u0001\u0002\u000e\"I\u0011q^A!A\u0003&\u0011QR\u0001\r_\nTWm\u0019;t%\u0016\fG\r\t\u0005\t\u0003g\f\t\u0005\"\u0003\u0002v\u0006ya.\u001a=u\u0005\u0006$8\r[*ue\u0016\fW\u000e\u0006\u0002\u0002>\"A\u0011\u0011`A!\t\u0013\tY0\u0001\u0007sK\u0006$g*\u001a=u\u0013R,W\u000eF\u00013\u0011!\ty0!\u0011\u0005B\t\u0005\u0011a\u00025bg:+\u0007\u0010^\u000b\u0003\u0005\u0007\u00012a\rB\u0003\u0013\r\u00119A\f\u0002\b\u0005>|G.Z1o\u0011!\u0011Y!!\u0011\u0005B\u0005m\u0018\u0001\u00028fqRD\u0001Ba\u0004\u0002B\u0011%!\u0011C\u0001\bG2,\u0017M\\;q)\t\t\u0019\u0002\u0003\u0005\u0003\u0016\u0001\u0001\u000b\u0011BA\u0018\u00031\u0019\b/\u001b7mK\u0012l\u0015\r]:!\u0011%\u0011I\u0002\u0001b\u0001\n\u0013\u0011Y\"A\u0005ta\u0006\u00148nQ8oMV\u0011!Q\u0004\t\u0004M\n}\u0011b\u0001B\u0011\r\tI1\u000b]1sW\u000e{gN\u001a\u0005\t\u0005K\u0001\u0001\u0015!\u0003\u0003\u001e\u0005Q1\u000f]1sW\u000e{gN\u001a\u0011\t\u0013\t%\u0002A1A\u0005\n\t-\u0012\u0001\u00053jg.\u0014En\\2l\u001b\u0006t\u0017mZ3s+\t\u0011i\u0003E\u0002_\u0005_I1A!\r`\u0005A!\u0015n]6CY>\u001c7.T1oC\u001e,'\u000f\u0003\u0005\u00036\u0001\u0001\u000b\u0011\u0002B\u0017\u0003E!\u0017n]6CY>\u001c7.T1oC\u001e,'\u000f\t\u0005\n\u0005s\u0001!\u0019!C\u0005\u0005w\t1c]3sS\u0006d\u0017N_3s\u0005\u0006$8\r[*ju\u0016,\"!!\u001c\t\u0011\t}\u0002\u0001)A\u0005\u0003[\nAc]3sS\u0006d\u0017N_3s\u0005\u0006$8\r[*ju\u0016\u0004\u0003\"\u0003B\"\u0001\u0001\u0007I\u0011\u0002B\u001e\u0003EyF-[:l\u0005f$Xm]*qS2dW\r\u001a\u0005\n\u0005\u000f\u0002\u0001\u0019!C\u0005\u0005\u0013\nQc\u00183jg.\u0014\u0015\u0010^3t'BLG\u000e\\3e?\u0012*\u0017\u000f\u0006\u0003\u0002\u0014\t-\u0003BCA\u000e\u0005\u000b\n\t\u00111\u0001\u0002n!A!q\n\u0001!B\u0013\ti'\u0001\n`I&\u001c8NQ=uKN\u001c\u0006/\u001b7mK\u0012\u0004\u0003b\u0002B*\u0001\u0011\u0005!1H\u0001\u0011I&\u001c8NQ=uKN\u001c\u0006/\u001b7mK\u0012D\u0011Ba\u0016\u0001\u0005\u0004%I!a#\u0002\u001d\u0019LG.\u001a\"vM\u001a,'oU5{K\"A!1\f\u0001!\u0002\u0013\ti)A\bgS2,')\u001e4gKJ\u001c\u0016N_3!\u0011%\u0011y\u0006\u0001b\u0001\n\u0013\u0011\t'\u0001\u0007xe&$X-T3ue&\u001c7/\u0006\u0002\u0003dA!!Q\rB6\u001b\t\u00119GC\u0002\u0003j\u0019\t\u0001\"\u001a=fGV$xN]\u0005\u0005\u0005[\u00129GA\nTQV4g\r\\3Xe&$X-T3ue&\u001c7\u000f\u0003\u0005\u0003r\u0001\u0001\u000b\u0011\u0002B2\u000359(/\u001b;f\u001b\u0016$(/[2tA!I!Q\u000f\u0001A\u0002\u0013%!1H\u0001\u0015?B,\u0017m['f[>\u0014\u00180V:fI\nKH/Z:\t\u0013\te\u0004\u00011A\u0005\n\tm\u0014\u0001G0qK\u0006\\W*Z7pef,6/\u001a3CsR,7o\u0018\u0013fcR!\u00111\u0003B?\u0011)\tYBa\u001e\u0002\u0002\u0003\u0007\u0011Q\u000e\u0005\t\u0005\u0003\u0003\u0001\u0015)\u0003\u0002n\u0005)r\f]3bW6+Wn\u001c:z+N,GMQ=uKN\u0004\u0003b\u0002BC\u0001\u0011\u0005!1H\u0001\u0014a\u0016\f7.T3n_JLXk]3e\u0005f$Xm\u001d\u0005\n\u0005\u0013\u0003!\u0019!C\u0005\u0005\u0017\u000bQb[3z\u0007>l\u0007/\u0019:bi>\u0014XC\u0001BG!\u0015\u0011yI!37\u001d\u0011\u0011\tJ!*\u000f\t\tM%1\u0015\b\u0005\u0005+\u0013\tK\u0004\u0003\u0003\u0018\n}e\u0002\u0002BM\u0005;s1a\nBN\u0013\u0005Y\u0011BA\u0005\u000b\u0013\t9\u0001\"\u0003\u0002\u0006\r%\u00111\u0001B\u0004\t\u0005O\u0013\u0001\u0012\u0001\u0004\u0003*\u0006)R\t\u001f;fe:\fG.\u00119qK:$wJ\u001c7z\u001b\u0006\u0004\bc\u0001\t\u0003,\u001a9\u0011A\u0001E\u0001\r\t56C\u0002BV\u0003\u000f\u0012y\u000bE\u00024\u0005cK!!\b\u0018\t\u000f=\u0014Y\u000b\"\u0001\u00036R\u0011!\u0011\u0016\u0005\t\u0005s\u0013Y\u000b\"\u0003\u0003<\u0006!\u0001.Y:i+\u0011\u0011iL!2\u0015\t\u00055%q\u0018\u0005\t\u0005\u0003\u00149\f1\u0001\u0003D\u0006\u0019qN\u00196\u0011\u0007]\u0012)\rB\u0004\u0003H\n]&\u0019\u0001\u001e\u0003\u0003Q3qAa3\u0003,\u0012\u0011iM\u0001\bICND7i\\7qCJ\fGo\u001c:\u0016\t\t='\u0011^\n\u0007\u0005\u0013\u0014\tN!8\u0011\t\tM'\u0011\\\u0007\u0003\u0005+T1Aa6\u001b\u0003\u0011a\u0017M\\4\n\t\tm'Q\u001b\u0002\u0007\u001f\nTWm\u0019;\u0011\r\t}'1\u001dBt\u001b\t\u0011\tO\u0003\u0002\u00065%!!Q\u001dBq\u0005)\u0019u.\u001c9be\u0006$xN\u001d\t\u0004o\t%HAB\u001d\u0003J\n\u0007!\bC\u0004p\u0005\u0013$\tA!<\u0015\u0005\t=\bC\u0002By\u0005\u0013\u00149/\u0004\u0002\u0003,\"A!Q\u001fBe\t\u0003\u001190A\u0004d_6\u0004\u0018M]3\u0015\r\u00055%\u0011 B\u007f\u0011!\u0011YPa=A\u0002\t\u001d\u0018\u0001B6fsFB\u0001Ba@\u0003t\u0002\u0007!q]\u0001\u0005W\u0016L(\u0007\u0003\u0006\u0004\u0004\t-\u0016\u0013!C\u0001\u0007\u000b\t1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\"T\u0003CB\u0004\u0007;\u0019yb!\t\u0016\u0005\r%!f\u0001,\u0004\f-\u00121Q\u0002\t\u0005\u0007\u001f\u0019I\"\u0004\u0002\u0004\u0012)!11CB\u000b\u0003%)hn\u00195fG.,GMC\u0002\u0004\u00189\n!\"\u00198o_R\fG/[8o\u0013\u0011\u0019Yb!\u0005\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r\u0002\u0004:\u0007\u0003\u0011\rA\u000f\u0003\u0007\u0017\u000e\u0005!\u0019\u0001\u001e\u0005\r\r\u001b\tA1\u0001;\u0011)\u0019)Ca+\u0012\u0002\u0013\u00051qE\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001b\u0016\u0011\r%2QFB\u0018\u0007c)\"aa\u000b+\u0007u\u001bY\u0001\u0002\u0004:\u0007G\u0011\rA\u000f\u0003\u0007\u0017\u000e\r\"\u0019\u0001\u001e\u0005\r\r\u001b\u0019C1\u0001;\u0011)\u0019)Da+\u0012\u0002\u0013\u00051qG\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000f\n\u001c\u0016\u0011\re2QHB \u0007\u0003*\"aa\u000f+\u0007\u0015\u001cY\u0001\u0002\u0004:\u0007g\u0011\rA\u000f\u0003\u0007\u0017\u000eM\"\u0019\u0001\u001e\u0005\r\r\u001b\u0019D1\u0001;\u0011)\u0019)Ea+\u0012\u0002\u0013\u00051qI\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001c\u0016\u0011\r%3QJB(\u0007#*\"aa\u0013+\u0007-\u001cY\u0001\u0002\u0004:\u0007\u0007\u0012\rA\u000f\u0003\u0007\u0017\u000e\r#\u0019\u0001\u001e\u0005\r\r\u001b\u0019E1\u0001;\u0011)\u0019)Fa+\u0002\u0002\u0013%1qK\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0003R\"A11\f\u0001!\u0002\u0013\u0011i)\u0001\blKf\u001cu.\u001c9be\u0006$xN\u001d\u0011\t\u0013\r}\u0003A1A\u0005\n\r\u0005\u0014aA:feV\u001111\r\t\u0004/\u000e\u0015\u0014bAB41\n\u00112+\u001a:jC2L'0\u001a:J]N$\u0018M\\2f\u0011!\u0019Y\u0007\u0001Q\u0001\n\r\r\u0014\u0001B:fe\u0002B\u0011ba\u001c\u0001\u0001\u0004%Ia!\u001d\u0002\u001fI,\u0017\rZ5oO&#XM]1u_J,\"aa\u001d\u0011\t\u0005}2Q\u000f\u0004\t\u0007o\u0002\u0001\u0015!\u0003\u0004z\t\t2\u000b]5mY\u0006\u0014G.Z%uKJ\fGo\u001c:\u0014\r\rU\u0014qIA'\u0011-\u0019ih!\u001e\u0003\u0002\u0004%\taa \u0002\u0011U\u00048\u000f\u001e:fC6,\"!!\u0014\t\u0017\r\r5Q\u000fBA\u0002\u0013\u00051QQ\u0001\rkB\u001cHO]3b[~#S-\u001d\u000b\u0005\u0003'\u00199\t\u0003\u0006\u0002\u001c\r\u0005\u0015\u0011!a\u0001\u0003\u001bB1ba#\u0004v\t\u0005\t\u0015)\u0003\u0002N\u0005IQ\u000f]:ue\u0016\fW\u000e\t\u0005\b_\u000eUD\u0011ABH)\u0011\u0019\u0019h!%\t\u0011\ru4Q\u0012a\u0001\u0003\u001bB!b!&\u0004v\t\u0007I\u0011BBL\u0003)\u0019\u0006+\u0013'M?2{5iS\u000b\u0003\u0005#D\u0011ba'\u0004v\u0001\u0006IA!5\u0002\u0017M\u0003\u0016\n\u0014'`\u0019>\u001b5\n\t\u0005\u000b\u0007?\u001b)\b1A\u0005\n\r}\u0014\u0001\u00048fqR,\u0006o\u001d;sK\u0006l\u0007BCBR\u0007k\u0002\r\u0011\"\u0003\u0004&\u0006\u0001b.\u001a=u+B\u001cHO]3b[~#S-\u001d\u000b\u0005\u0003'\u00199\u000b\u0003\u0006\u0002\u001c\r\u0005\u0016\u0011!a\u0001\u0003\u001bB\u0011ba+\u0004v\u0001\u0006K!!\u0014\u0002\u001b9,\u0007\u0010^+qgR\u0014X-Y7!\u0011)\u0019yk!\u001eA\u0002\u0013%\u00111[\u0001\u0004GV\u0014\bBCBZ\u0007k\u0002\r\u0011\"\u0003\u00046\u000691-\u001e:`I\u0015\fH\u0003BA\n\u0007oC\u0011\"a\u0007\u00042\u0006\u0005\t\u0019\u0001\u001a\t\u0011\rm6Q\u000fQ!\nI\nAaY;sA!Q1qXB;\u0001\u0004%IA!\u0001\u0002\u0015!\f7o\u00159jY2,G\r\u0003\u0006\u0004D\u000eU\u0004\u0019!C\u0005\u0007\u000b\fa\u0002[1t'BLG\u000e\\3e?\u0012*\u0017\u000f\u0006\u0003\u0002\u0014\r\u001d\u0007BCA\u000e\u0007\u0003\f\t\u00111\u0001\u0003\u0004!I11ZB;A\u0003&!1A\u0001\fQ\u0006\u001c8\u000b]5mY\u0016$\u0007\u0005\u0003\u0005\u0004P\u000eUD\u0011ABi\u0003\u0015\u0019\b/\u001b7m)\t\u0011\u0019\u0001\u0003\u0005\u0004V\u000eUD\u0011AA~\u0003!\u0011X-\u00193OKb$\b\u0002CA\u0000\u0007k\"\te!5\t\u0011\t-1Q\u000fC!\u0003wD\u0011b!8\u0001\u0001\u0004%Iaa8\u0002'I,\u0017\rZ5oO&#XM]1u_J|F%Z9\u0015\t\u0005M1\u0011\u001d\u0005\u000b\u00037\u0019Y.!AA\u0002\rM\u0004\u0002CBs\u0001\u0001\u0006Kaa\u001d\u0002!I,\u0017\rZ5oO&#XM]1u_J\u0004\u0003\u0006BBr\u0003GA\u0001ba;\u0001\t\u0003\u0011\u00111R\u0001\n]Vl7\u000b]5mYNDqaa<\u0001\t\u0003\u0019\t0\u0001\u0004j]N,'\u000f\u001e\u000b\u0007\u0003'\u0019\u0019pa>\t\u000f\rU8Q\u001ea\u0001m\u0005\u00191.Z=\t\u000f\re8Q\u001ea\u0001\u0013\u0006)a/\u00197vK\"91Q \u0001\u0005\u0002\r}\u0018!C5og\u0016\u0014H/\u00117m)\u0011\t\u0019\u0002\"\u0001\t\u0011\u0011\r11 a\u0001\t\u000b\tq!\u001a8ue&,7\u000fE\u0003&\u0003\u001f\"9\u0001E\u00034\t\u00131\u0014*C\u0002\u0005\f9\u0012\u0001\u0002\u0015:pIV\u001cGO\r\u0005\b\u0007{\u0004A\u0011\u0001C\b)\u0011\t\u0019\u0002\"\u0005\t\u0011\u0011\rAQ\u0002a\u0001\t'\u0001B!J\u0018\u0005\b!A1q\u001a\u0001!\n#\"9\u0002\u0006\u0003\u0002\u0014\u0011e\u0001BB\u0002\u0005\u0016\u0001\u00071\u0003\u0003\u0005\u0005\u001e\u0001\u0001K\u0011KBi\u0003)1wN]2f'BLG\u000e\u001c\u0005\t\tC\u0001\u0001\u0015\"\u0003\u0005$\u0005I2\u000f]5mY6+Wn\u001c:z\u0013R,'/\u0019;peR{G)[:l)\u0011\ti\u0004\"\n\t\u0011\u0011\u001dBq\u0004a\u0001\u0003\u001b\n\u0001#\u001b8NK6|'/_%uKJ\fGo\u001c:\t\u000f\u0011-\u0002\u0001\"\u0001\u0005.\u0005\u0019B-Z:ueV\u001cG/\u001b<f\u0013R,'/\u0019;peR!\u0011Q\nC\u0018\u0011!!9\u0003\"\u000bA\u0002\u00055\u0003b\u0002C\u001a\u0001\u0011\u00053qP\u0001\tSR,'/\u0019;pe\"9Aq\u0007\u0001\u0005\n\tE\u0011A\u00044sK\u0016\u001cUO\u001d:f]Rl\u0015\r\u001d\u0004\u0007\tw\u0001A\u0001\"\u0010\u0003!\u0015CH/\u001a:oC2LE/\u001a:bi>\u00148C\u0002C\u001d\u0003\u000f\ni\u0005C\u0004p\ts!\t\u0001\"\u0011\u0015\u0005\u0011\r\u0003\u0003BA \tsA!\u0002b\u0012\u0005:\t\u0007I\u0011\u0002C%\u0003%iWM]4f\u0011\u0016\f\u0007/\u0006\u0002\u0005LA1\u0011\u0011\u0007C'\t#JA\u0001b\u0014\u00024\ti\u0001K]5pe&$\u00180U;fk\u0016\u0004B\u0001b\u0015\u0005V5\u0011A\u0011\b\u0004\b\t/\"I\u0004\u0002C-\u00051\u0019FO]3b[\n+hMZ3s'\u0019!)&a\u0012\u0005\\A1!1\u001bC/\t#JA\u0001b\u0018\u0003V\nQ1i\\7qCJ\f'\r\\3\t\u0017\u0011MBQ\u000bBC\u0002\u0013\u0005A1M\u000b\u0003\tK\u0002R\u0001b\u001a\u0005jIj!!a\u000e\n\t\u0011-\u0014q\u0007\u0002\u0011\u0005V4g-\u001a:fI&#XM]1u_JD1\u0002b\u001c\u0005V\t\u0005\t\u0015!\u0003\u0005f\u0005I\u0011\u000e^3sCR|'\u000f\t\u0005\f\tg\")F!b\u0001\n\u0003!)(A\u0003qC&\u00148/\u0006\u0002\u0005xA)\u0011\u0011GA\u001de!YA1\u0010C+\u0005\u0003\u0005\u000b\u0011\u0002C<\u0003\u0019\u0001\u0018-\u001b:tA!9q\u000e\"\u0016\u0005\u0002\u0011}DC\u0002C)\t\u0003#\u0019\t\u0003\u0005\u00054\u0011u\u0004\u0019\u0001C3\u0011!!\u0019\b\" A\u0002\u0011]\u0004\u0002\u0003CD\t+\"\tA!\u0001\u0002\u000f%\u001cX)\u001c9us\"AA1\u0012C+\t\u0003\tY)\u0001\u0006nS:\\U-\u001f%bg\"D\u0001\u0002b$\u0005V\u0011\u0005C\u0011S\u0001\nG>l\u0007/\u0019:f)>$B!!$\u0005\u0014\"AAQ\u0013CG\u0001\u0004!\t&A\u0003pi\",'\u000fC\u0005\u0005\u001a\u0012e\u0002\u0015!\u0003\u0005L\u0005QQ.\u001a:hK\"+\u0017\r\u001d\u0011\t\u0015\u0011uE\u0011\bb\u0001\n\u0013!y*A\u0005t_J$X\rZ'baV\u0011A\u0011\u0015\t\b\tG#)KMA'\u001b\u0005!\u0011b\u0001CT\t\t\u00112i\\7qY\u0016$\u0018n\u001c8Ji\u0016\u0014\u0018\r^8s\u0011%!Y\u000b\"\u000f!\u0002\u0013!\t+\u0001\u0006t_J$X\rZ'ba\u0002B!\u0002b,\u0005:\t\u0007I\u0011\u0002CY\u00031Ig\u000e];u'R\u0014X-Y7t+\t!\u0019\f\u0005\u0004\u0005h\u0011UFQM\u0005\u0005\to\u000b9DA\u0002TKFD\u0011\u0002b/\u0005:\u0001\u0006I\u0001b-\u0002\u001b%t\u0007/\u001e;TiJ,\u0017-\\:!\u0011!!y\f\"\u000f\u0005\n\u0011\u0005\u0017\u0001\u0005:fC\u0012tU\r\u001f;ICND7i\u001c3f)\u0019\t\u0019\u0002b1\u0005H\"AAQ\u0019C_\u0001\u0004!)'\u0001\u0002ji\"AA\u0011\u001aC_\u0001\u0004!9(A\u0002ck\u001aD\u0001\u0002\"4\u0005:\u0011%AqZ\u0001\u0011[\u0016\u0014x-Z%g\u0017\u0016LX\t_5tiN$r!\u0011Ci\t'$9\u000eC\u0004\u0004v\u0012-\u0007\u0019\u0001\u001c\t\u000f\u0011UG1\u001aa\u0001\u0003\u0006a!-Y:f\u0007>l'-\u001b8fe\"AA\u0011\u001cCf\u0001\u0004!\t&\u0001\u0004ck\u001a4WM\u001d\u0005\t\t;$I\u0004\"\u0003\u0005`\u0006\u0001\"/Z7pm\u00164%o\\7Ck\u001a4WM]\u000b\u0005\tC$)\u000f\u0006\u0004\u0005d\u0012\u001dH1\u001e\t\u0004o\u0011\u0015Ha\u0002Bd\t7\u0014\rA\u000f\u0005\t\t3$Y\u000e1\u0001\u0005jB1\u0011\u0011GA\u001d\tGD\u0001\u0002\"<\u0005\\\u0002\u0007\u0011QR\u0001\u0006S:$W\r\u001f\u0005\t\u0003\u007f$I\u0004\"\u0011\u0003\u0002!A!1\u0002C\u001d\t\u0003\nY\u0010C\u0004\u0005v\u0002!I\u0001b>\u0002\u000f!\f7\u000f[&fsR!\u0011Q\u0012C}\u0011\u001d!Y\u0010b=A\u0002I\n!a[2\t\u000f\u0011}\b\u0001\"\u0011\u0006\u0002\u0005AAo\\*ue&tw\r\u0006\u0002\u0006\u0004A!QQAC\u0006\u001d\r\u0019TqA\u0005\u0004\u000b\u0013q\u0013A\u0002)sK\u0012,g-\u0003\u0003\u0006\u000e\u0015=!AB*ue&twMC\u0002\u0006\n9BA\"b\u0005\u0001!\u0003\u0005\t\u0011!C\u0001\u000b+\t\u0011\u0003\u001d:pi\u0016\u001cG/\u001a3%O\u0016$Xk]3e)\u0011)9\"\"\u0007\u0015\u0005\u00055\u0004\"CA\u000e\u000b#\t\t\u00111\u0001rQ\r\u0001QQ\u0004\t\u0005\u000b?)\u0019#\u0004\u0002\u0006\")\u00191q\u0003\u0004\n\t\u0015\u0015R\u0011\u0005\u0002\r\t\u00164X\r\\8qKJ\f\u0005/\u001b"
)
public class ExternalAppendOnlyMap<K, V, C> extends Spillable<SizeTracker> implements Serializable, Iterable<Tuple2<K, C>> {
   public final Function1<V, C> org$apache$spark$util$collection$ExternalAppendOnlyMap$$createCombiner;
   public final Function2<C, V, C> org$apache$spark$util$collection$ExternalAppendOnlyMap$$mergeValue;
   public final Function2<C, C, C> org$apache$spark$util$collection$ExternalAppendOnlyMap$$mergeCombiners;
   private final BlockManager blockManager;
   public final TaskContext org$apache$spark$util$collection$ExternalAppendOnlyMap$$context;
   public final SerializerManager org$apache$spark$util$collection$ExternalAppendOnlyMap$$serializerManager;
   private volatile SizeTrackingAppendOnlyMap<K, C> org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap;
   private final ArrayBuffer<ExternalAppendOnlyMap<K, V, C>.DiskMapIterator> org$apache$spark$util$collection$ExternalAppendOnlyMap$$spilledMaps;
   private final SparkConf sparkConf;
   private final DiskBlockManager diskBlockManager;
   private final long org$apache$spark$util$collection$ExternalAppendOnlyMap$$serializerBatchSize;
   private long _diskBytesSpilled;
   private final int fileBufferSize;
   private final ShuffleWriteMetrics writeMetrics;
   private long _peakMemoryUsedBytes;
   private final HashComparator<K> org$apache$spark$util$collection$ExternalAppendOnlyMap$$keyComparator;
   private final SerializerInstance org$apache$spark$util$collection$ExternalAppendOnlyMap$$ser;
   private volatile ExternalAppendOnlyMap<K, V, C>.SpillableIterator readingIterator;

   public static <K, V, C> SerializerManager $lessinit$greater$default$7() {
      return ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$7();
   }

   public static <K, V, C> TaskContext $lessinit$greater$default$6() {
      return ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$6();
   }

   public static <K, V, C> BlockManager $lessinit$greater$default$5() {
      return ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$5();
   }

   public static <K, V, C> Serializer $lessinit$greater$default$4() {
      return ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$4();
   }

   public GenericCompanion<Iterable> companion() {
      return Iterable$class.companion(this);
   }

   public Iterable<Tuple2<K, C>> seq() {
      return Iterable$class.seq(this);
   }

   public Iterable<Tuple2<K, C>> thisCollection() {
      return IterableLike$class.thisCollection(this);
   }

   public Iterable toCollection(Object repr) {
      return IterableLike$class.toCollection(this, repr);
   }

   public <U> void foreach(Function1<Tuple2<K, C>, U> f) {
      IterableLike$class.foreach(this, f);
   }

   public boolean forall(Function1<Tuple2<K, C>, Object> p) {
      return IterableLike$class.forall(this, p);
   }

   public boolean exists(Function1<Tuple2<K, C>, Object> p) {
      return IterableLike$class.exists(this, p);
   }

   public Option<Tuple2<K, C>> find(Function1<Tuple2<K, C>, Object> p) {
      return IterableLike$class.find(this, p);
   }

   public boolean isEmpty() {
      return IterableLike$class.isEmpty(this);
   }

   public <B> B foldRight(B z, Function2<Tuple2<K, C>, B, B> op) {
      return (B)IterableLike$class.foldRight(this, z, op);
   }

   public <B> B reduceRight(Function2<Tuple2<K, C>, B, B> op) {
      return (B)IterableLike$class.reduceRight(this, op);
   }

   public Iterable<Tuple2<K, C>> toIterable() {
      return IterableLike$class.toIterable(this);
   }

   public Iterator<Tuple2<K, C>> toIterator() {
      return IterableLike$class.toIterator(this);
   }

   public Object head() {
      return IterableLike$class.head(this);
   }

   public Object slice(int from, int until) {
      return IterableLike$class.slice(this, from, until);
   }

   public Object take(int n) {
      return IterableLike$class.take(this, n);
   }

   public Object drop(int n) {
      return IterableLike$class.drop(this, n);
   }

   public Object takeWhile(Function1 p) {
      return IterableLike$class.takeWhile(this, p);
   }

   public Iterator<Iterable<Tuple2<K, C>>> grouped(int size) {
      return IterableLike$class.grouped(this, size);
   }

   public Iterator<Iterable<Tuple2<K, C>>> sliding(int size) {
      return IterableLike$class.sliding(this, size);
   }

   public Iterator<Iterable<Tuple2<K, C>>> sliding(int size, int step) {
      return IterableLike$class.sliding(this, size, step);
   }

   public Object takeRight(int n) {
      return IterableLike$class.takeRight(this, n);
   }

   public Object dropRight(int n) {
      return IterableLike$class.dropRight(this, n);
   }

   public <B> void copyToArray(Object xs, int start, int len) {
      IterableLike$class.copyToArray(this, xs, start, len);
   }

   public <A1, B, That> That zip(GenIterable<B> that, CanBuildFrom<Iterable<Tuple2<K, C>>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zip(this, that, bf);
   }

   public <B, A1, That> That zipAll(GenIterable<B> that, A1 thisElem, B thatElem, CanBuildFrom<Iterable<Tuple2<K, C>>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zipAll(this, that, thisElem, thatElem, bf);
   }

   public <A1, That> That zipWithIndex(CanBuildFrom<Iterable<Tuple2<K, C>>, Tuple2<A1, Object>, That> bf) {
      return (That)IterableLike$class.zipWithIndex(this, bf);
   }

   public <B> boolean sameElements(GenIterable<B> that) {
      return IterableLike$class.sameElements(this, that);
   }

   public Stream<Tuple2<K, C>> toStream() {
      return IterableLike$class.toStream(this);
   }

   public boolean canEqual(Object that) {
      return IterableLike$class.canEqual(this, that);
   }

   public Object view() {
      return IterableLike$class.view(this);
   }

   public IterableView<Tuple2<K, C>, Iterable<Tuple2<K, C>>> view(int from, int until) {
      return IterableLike$class.view(this, from, until);
   }

   public Builder<Tuple2<K, C>, Iterable<Tuple2<K, C>>> newBuilder() {
      return GenericTraversableTemplate$class.newBuilder(this);
   }

   public <B> Builder<B, Iterable<B>> genericBuilder() {
      return GenericTraversableTemplate$class.genericBuilder(this);
   }

   public <A1, A2> Tuple2<Iterable<A1>, Iterable<A2>> unzip(Function1<Tuple2<K, C>, Tuple2<A1, A2>> asPair) {
      return GenericTraversableTemplate$class.unzip(this, asPair);
   }

   public <A1, A2, A3> Tuple3<Iterable<A1>, Iterable<A2>, Iterable<A3>> unzip3(Function1<Tuple2<K, C>, Tuple3<A1, A2, A3>> asTriple) {
      return GenericTraversableTemplate$class.unzip3(this, asTriple);
   }

   public GenTraversable flatten(Function1 asTraversable) {
      return GenericTraversableTemplate$class.flatten(this, asTraversable);
   }

   public GenTraversable transpose(Function1 asTraversable) {
      return GenericTraversableTemplate$class.transpose(this, asTraversable);
   }

   public Object repr() {
      return TraversableLike$class.repr(this);
   }

   public final boolean isTraversableAgain() {
      return TraversableLike$class.isTraversableAgain(this);
   }

   public Combiner<Tuple2<K, C>, ParIterable<Tuple2<K, C>>> parCombiner() {
      return TraversableLike$class.parCombiner(this);
   }

   public boolean hasDefiniteSize() {
      return TraversableLike$class.hasDefiniteSize(this);
   }

   public <B, That> That $plus$plus(GenTraversableOnce<B> that, CanBuildFrom<Iterable<Tuple2<K, C>>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus(this, that, bf);
   }

   public <B, That> That $plus$plus$colon(TraversableOnce<B> that, CanBuildFrom<Iterable<Tuple2<K, C>>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (TraversableOnce)that, bf);
   }

   public <B, That> That $plus$plus$colon(Traversable<B> that, CanBuildFrom<Iterable<Tuple2<K, C>>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (Traversable)that, bf);
   }

   public <B, That> That map(Function1<Tuple2<K, C>, B> f, CanBuildFrom<Iterable<Tuple2<K, C>>, B, That> bf) {
      return (That)TraversableLike$class.map(this, f, bf);
   }

   public <B, That> That flatMap(Function1<Tuple2<K, C>, GenTraversableOnce<B>> f, CanBuildFrom<Iterable<Tuple2<K, C>>, B, That> bf) {
      return (That)TraversableLike$class.flatMap(this, f, bf);
   }

   public Object filter(Function1 p) {
      return TraversableLike$class.filter(this, p);
   }

   public Object filterNot(Function1 p) {
      return TraversableLike$class.filterNot(this, p);
   }

   public <B, That> That collect(PartialFunction<Tuple2<K, C>, B> pf, CanBuildFrom<Iterable<Tuple2<K, C>>, B, That> bf) {
      return (That)TraversableLike$class.collect(this, pf, bf);
   }

   public Tuple2<Iterable<Tuple2<K, C>>, Iterable<Tuple2<K, C>>> partition(Function1<Tuple2<K, C>, Object> p) {
      return TraversableLike$class.partition(this, p);
   }

   public <K> Map<K, Iterable<Tuple2<K, C>>> groupBy(Function1<Tuple2<K, C>, K> f) {
      return TraversableLike$class.groupBy(this, f);
   }

   public <B, That> That scan(B z, Function2<B, B, B> op, CanBuildFrom<Iterable<Tuple2<K, C>>, B, That> cbf) {
      return (That)TraversableLike$class.scan(this, z, op, cbf);
   }

   public <B, That> That scanLeft(B z, Function2<B, Tuple2<K, C>, B> op, CanBuildFrom<Iterable<Tuple2<K, C>>, B, That> bf) {
      return (That)TraversableLike$class.scanLeft(this, z, op, bf);
   }

   public <B, That> That scanRight(B z, Function2<Tuple2<K, C>, B, B> op, CanBuildFrom<Iterable<Tuple2<K, C>>, B, That> bf) {
      return (That)TraversableLike$class.scanRight(this, z, op, bf);
   }

   public Option<Tuple2<K, C>> headOption() {
      return TraversableLike$class.headOption(this);
   }

   public Object tail() {
      return TraversableLike$class.tail(this);
   }

   public Object last() {
      return TraversableLike$class.last(this);
   }

   public Option<Tuple2<K, C>> lastOption() {
      return TraversableLike$class.lastOption(this);
   }

   public Object init() {
      return TraversableLike$class.init(this);
   }

   public Object sliceWithKnownDelta(int from, int until, int delta) {
      return TraversableLike$class.sliceWithKnownDelta(this, from, until, delta);
   }

   public Object sliceWithKnownBound(int from, int until) {
      return TraversableLike$class.sliceWithKnownBound(this, from, until);
   }

   public Object dropWhile(Function1 p) {
      return TraversableLike$class.dropWhile(this, p);
   }

   public Tuple2<Iterable<Tuple2<K, C>>, Iterable<Tuple2<K, C>>> span(Function1<Tuple2<K, C>, Object> p) {
      return TraversableLike$class.span(this, p);
   }

   public Tuple2<Iterable<Tuple2<K, C>>, Iterable<Tuple2<K, C>>> splitAt(int n) {
      return TraversableLike$class.splitAt(this, n);
   }

   public Iterator<Iterable<Tuple2<K, C>>> tails() {
      return TraversableLike$class.tails(this);
   }

   public Iterator<Iterable<Tuple2<K, C>>> inits() {
      return TraversableLike$class.inits(this);
   }

   public Traversable<Tuple2<K, C>> toTraversable() {
      return TraversableLike$class.toTraversable(this);
   }

   public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<K, C>, Col> cbf) {
      return (Col)TraversableLike$class.to(this, cbf);
   }

   public String stringPrefix() {
      return TraversableLike$class.stringPrefix(this);
   }

   public FilterMonadic<Tuple2<K, C>, Iterable<Tuple2<K, C>>> withFilter(Function1<Tuple2<K, C>, Object> p) {
      return TraversableLike$class.withFilter(this, p);
   }

   public Parallel par() {
      return Parallelizable$class.par(this);
   }

   public List<Tuple2<K, C>> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public int size() {
      return TraversableOnce$class.size(this);
   }

   public boolean nonEmpty() {
      return TraversableOnce$class.nonEmpty(this);
   }

   public int count(Function1<Tuple2<K, C>, Object> p) {
      return TraversableOnce$class.count(this, p);
   }

   public <B> Option<B> collectFirst(PartialFunction<Tuple2<K, C>, B> pf) {
      return TraversableOnce$class.collectFirst(this, pf);
   }

   public <B> B $div$colon(B z, Function2<B, Tuple2<K, C>, B> op) {
      return (B)TraversableOnce$class.$div$colon(this, z, op);
   }

   public <B> B $colon$bslash(B z, Function2<Tuple2<K, C>, B, B> op) {
      return (B)TraversableOnce$class.$colon$bslash(this, z, op);
   }

   public <B> B foldLeft(B z, Function2<B, Tuple2<K, C>, B> op) {
      return (B)TraversableOnce$class.foldLeft(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, Tuple2<K, C>, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<K, C>, B> op) {
      return TraversableOnce$class.reduceLeftOption(this, op);
   }

   public <B> Option<B> reduceRightOption(Function2<Tuple2<K, C>, B, B> op) {
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

   public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<K, C>, B> seqop, Function2<B, B, B> combop) {
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

   public List<Tuple2<K, C>> toList() {
      return TraversableOnce$class.toList(this);
   }

   public Seq<Tuple2<K, C>> toSeq() {
      return TraversableOnce$class.toSeq(this);
   }

   public IndexedSeq<Tuple2<K, C>> toIndexedSeq() {
      return TraversableOnce$class.toIndexedSeq(this);
   }

   public <B> Buffer<B> toBuffer() {
      return TraversableOnce$class.toBuffer(this);
   }

   public <B> Set<B> toSet() {
      return TraversableOnce$class.toSet(this);
   }

   public Vector<Tuple2<K, C>> toVector() {
      return TraversableOnce$class.toVector(this);
   }

   public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<K, C>, Tuple2<T, U>> ev) {
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

   // $FF: synthetic method
   public long protected$getUsed(ExternalAppendOnlyMap x$1) {
      return x$1.getUsed();
   }

   public SizeTrackingAppendOnlyMap<K, C> org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap() {
      return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap;
   }

   private void org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap_$eq(SizeTrackingAppendOnlyMap<K, C> x$1) {
      this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap = x$1;
   }

   public ArrayBuffer<ExternalAppendOnlyMap<K, V, C>.DiskMapIterator> org$apache$spark$util$collection$ExternalAppendOnlyMap$$spilledMaps() {
      return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$spilledMaps;
   }

   private SparkConf sparkConf() {
      return this.sparkConf;
   }

   private DiskBlockManager diskBlockManager() {
      return this.diskBlockManager;
   }

   public long org$apache$spark$util$collection$ExternalAppendOnlyMap$$serializerBatchSize() {
      return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$serializerBatchSize;
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

   private int fileBufferSize() {
      return this.fileBufferSize;
   }

   private ShuffleWriteMetrics writeMetrics() {
      return this.writeMetrics;
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

   public HashComparator<K> org$apache$spark$util$collection$ExternalAppendOnlyMap$$keyComparator() {
      return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$keyComparator;
   }

   public SerializerInstance org$apache$spark$util$collection$ExternalAppendOnlyMap$$ser() {
      return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$ser;
   }

   private ExternalAppendOnlyMap<K, V, C>.SpillableIterator readingIterator() {
      return this.readingIterator;
   }

   private void readingIterator_$eq(ExternalAppendOnlyMap<K, V, C>.SpillableIterator x$1) {
      this.readingIterator = x$1;
   }

   public int numSpills() {
      return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$spilledMaps().size();
   }

   public void insert(K key, V value) {
      this.insertAll(package$.MODULE$.Iterator().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2(key, value)})));
   }

   public void insertAll(Iterator<Product2<K, V>> entries) {
      if (this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap() == null) {
         throw new IllegalStateException("Cannot insert new elements into a map after calling iterator");
      } else {
         ObjectRef curEntry = ObjectRef.create((Object)null);
         Function2 update = new scala.Serializable(this, curEntry) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExternalAppendOnlyMap $outer;
            private final ObjectRef curEntry$1;

            public final C apply(boolean hadVal, C oldVal) {
               return (C)(hadVal ? this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$$mergeValue.apply(oldVal, ((Product2)this.curEntry$1.elem)._2()) : this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$$createCombiner.apply(((Product2)this.curEntry$1.elem)._2()));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.curEntry$1 = curEntry$1;
               }
            }
         };

         while(entries.hasNext()) {
            curEntry.elem = (T)((Product2)entries.next());
            long estimatedSize = this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap().estimateSize();
            if (estimatedSize > this._peakMemoryUsedBytes()) {
               this._peakMemoryUsedBytes_$eq(estimatedSize);
            }

            if (this.maybeSpill(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap(), estimatedSize)) {
               this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap_$eq(new SizeTrackingAppendOnlyMap());
            }

            this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap().changeValue(((Product2)curEntry.elem)._1(), update);
            this.addElementsRead();
         }

      }
   }

   public void insertAll(Iterable<Product2<K, V>> entries) {
      this.insertAll(entries.iterator());
   }

   public void spill(SizeTracker collection) {
      Iterator inMemoryIterator = this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap().destructiveSortedIterator(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$keyComparator());
      DiskMapIterator diskMapIterator = this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$spillMemoryIteratorToDisk(inMemoryIterator);
      this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$spilledMaps().$plus$eq(diskMapIterator);
   }

   public boolean forceSpill() {
      boolean var10000;
      if (this.readingIterator() == null) {
         if (this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap().size() > 0) {
            this.spill((SizeTracker)this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap());
            this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap_$eq(new SizeTrackingAppendOnlyMap());
            var10000 = true;
         } else {
            var10000 = false;
         }
      } else {
         boolean isSpilled = this.readingIterator().spill();
         if (isSpilled) {
            this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap_$eq((SizeTrackingAppendOnlyMap)null);
         }

         var10000 = isSpilled;
      }

      return var10000;
   }

   public ExternalAppendOnlyMap<K, V, C>.DiskMapIterator org$apache$spark$util$collection$ExternalAppendOnlyMap$$spillMemoryIteratorToDisk(Iterator<Tuple2<K, C>> inMemoryIterator) {
      Tuple2 var3 = this.diskBlockManager().createTempLocalBlock();
      if (var3 == null) {
         throw new MatchError(var3);
      } else {
         TempLocalBlockId blockId = (TempLocalBlockId)var3._1();
         File file = (File)var3._2();
         Tuple2 var6 = new Tuple2(blockId, file);
         TempLocalBlockId blockId = (TempLocalBlockId)var6._1();
         File file = (File)var6._2();
         DiskBlockObjectWriter writer = this.blockManager.getDiskWriter(blockId, file, this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$ser(), this.fileBufferSize(), this.writeMetrics());
         IntRef objectsWritten = IntRef.create(0);
         ArrayBuffer batchSizes = new ArrayBuffer();
         boolean success = false;

         try {
            while(inMemoryIterator.hasNext()) {
               Tuple2 kv = (Tuple2)inMemoryIterator.next();
               writer.write(kv._1(), kv._2());
               ++objectsWritten.elem;
               if ((long)objectsWritten.elem == this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$serializerBatchSize()) {
                  this.flush$1(writer, objectsWritten, batchSizes);
               }
            }

            if (objectsWritten.elem > 0) {
               this.flush$1(writer, objectsWritten, batchSizes);
               writer.close();
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               writer.revertPartialWritesAndClose();
            }

            success = true;
         } finally {
            if (!success) {
               writer.revertPartialWritesAndClose();
               if (file.exists() && !file.delete()) {
                  this.logWarning(new scala.Serializable(this, file) {
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

         return new DiskMapIterator(file, blockId, batchSizes);
      }
   }

   public Iterator<Tuple2<K, C>> destructiveIterator(Iterator<Tuple2<K, C>> inMemoryIterator) {
      this.readingIterator_$eq(new SpillableIterator(inMemoryIterator));
      return this.readingIterator();
   }

   public Iterator<Tuple2<K, C>> iterator() {
      if (this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap() == null) {
         throw new IllegalStateException("ExternalAppendOnlyMap.iterator is destructive and should only be called once.");
      } else {
         return (Iterator<Tuple2<K, C>>)(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$spilledMaps().isEmpty() ? CompletionIterator$.MODULE$.apply(this.destructiveIterator(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap().iterator()), new scala.Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExternalAppendOnlyMap $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$$freeCurrentMap();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }) : new ExternalIterator());
      }
   }

   public void org$apache$spark$util$collection$ExternalAppendOnlyMap$$freeCurrentMap() {
      if (this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap() != null) {
         this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap_$eq((SizeTrackingAppendOnlyMap)null);
         this.releaseMemory();
      }

   }

   public int org$apache$spark$util$collection$ExternalAppendOnlyMap$$hashKey(Tuple2<K, C> kc) {
      return ExternalAppendOnlyMap$.MODULE$.org$apache$spark$util$collection$ExternalAppendOnlyMap$$hash(kc._1());
   }

   public String toString() {
      return (new StringBuilder()).append((Object)this.getClass().getName()).append((Object)"@").append((Object)Integer.toHexString(this.hashCode())).toString();
   }

   private final void flush$1(DiskBlockObjectWriter writer$1, IntRef objectsWritten$1, ArrayBuffer batchSizes$1) {
      FileSegment segment = writer$1.commitAndGet();
      batchSizes$1.$plus$eq(BoxesRunTime.boxToLong(segment.length()));
      this._diskBytesSpilled_$eq(this._diskBytesSpilled() + segment.length());
      objectsWritten$1.elem = 0;
   }

   public ExternalAppendOnlyMap(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners, Serializer serializer, BlockManager blockManager, TaskContext context, SerializerManager serializerManager) {
      super(context.taskMemoryManager());
      this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$createCombiner = createCombiner;
      this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$mergeValue = mergeValue;
      this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$mergeCombiners = mergeCombiners;
      this.blockManager = blockManager;
      this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$context = context;
      this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$serializerManager = serializerManager;
      TraversableOnce$class.$init$(this);
      Parallelizable$class.$init$(this);
      TraversableLike$class.$init$(this);
      GenericTraversableTemplate$class.$init$(this);
      GenTraversable$class.$init$(this);
      Traversable$class.$init$(this);
      GenIterable$class.$init$(this);
      IterableLike$class.$init$(this);
      Iterable$class.$init$(this);
      if (context == null) {
         throw new IllegalStateException("Spillable collections should not be instantiated outside of tasks");
      } else {
         this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap = new SizeTrackingAppendOnlyMap<K, C>();
         this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$spilledMaps = new ArrayBuffer<ExternalAppendOnlyMap<K, V, C>.DiskMapIterator>();
         this.sparkConf = SparkEnv$.MODULE$.get().conf();
         this.diskBlockManager = blockManager.diskBlockManager();
         this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$serializerBatchSize = this.sparkConf().getLong("spark.shuffle.spill.batchSize", 10000L);
         this._diskBytesSpilled = 0L;
         this.fileBufferSize = (int)this.sparkConf().getSizeAsKb("spark.shuffle.file.buffer", "32k") * 1024;
         this.writeMetrics = new ShuffleWriteMetrics();
         this._peakMemoryUsedBytes = 0L;
         this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$keyComparator = new HashComparator<K>();
         this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$ser = serializer.newInstance();
         this.readingIterator = null;
      }
   }

   public ExternalAppendOnlyMap(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners, Serializer serializer, BlockManager blockManager) {
      this(createCombiner, mergeValue, mergeCombiners, serializer, blockManager, TaskContext$.MODULE$.get(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$7());
   }

   public class ExternalIterator implements Iterator<Tuple2<K, C>> {
      private final PriorityQueue<ExternalAppendOnlyMap<K, V, C>.ExternalIterator$StreamBuffer> org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap;
      private final CompletionIterator<Tuple2<K, C>, Iterator<Tuple2<K, C>>> sortedMap;
      private final Seq<BufferedIterator<Tuple2<K, C>>> inputStreams;
      // $FF: synthetic field
      public final ExternalAppendOnlyMap $outer;

      public Iterator<Tuple2<K, C>> seq() {
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

      public Iterator<Tuple2<K, C>> take(int n) {
         return Iterator$class.take(this, n);
      }

      public Iterator<Tuple2<K, C>> drop(int n) {
         return Iterator$class.drop(this, n);
      }

      public Iterator<Tuple2<K, C>> slice(int from, int until) {
         return Iterator$class.slice(this, from, until);
      }

      public <B> Iterator<B> map(Function1<Tuple2<K, C>, B> f) {
         return Iterator$class.map(this, f);
      }

      public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
         return Iterator$class.$plus$plus(this, that);
      }

      public <B> Iterator<B> flatMap(Function1<Tuple2<K, C>, GenTraversableOnce<B>> f) {
         return Iterator$class.flatMap(this, f);
      }

      public Iterator<Tuple2<K, C>> filter(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.filter(this, p);
      }

      public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<K, C>, B, Object> p) {
         return Iterator$class.corresponds(this, that, p);
      }

      public Iterator<Tuple2<K, C>> withFilter(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.withFilter(this, p);
      }

      public Iterator<Tuple2<K, C>> filterNot(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.filterNot(this, p);
      }

      public <B> Iterator<B> collect(PartialFunction<Tuple2<K, C>, B> pf) {
         return Iterator$class.collect(this, pf);
      }

      public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<K, C>, B> op) {
         return Iterator$class.scanLeft(this, z, op);
      }

      public <B> Iterator<B> scanRight(B z, Function2<Tuple2<K, C>, B, B> op) {
         return Iterator$class.scanRight(this, z, op);
      }

      public Iterator<Tuple2<K, C>> takeWhile(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.takeWhile(this, p);
      }

      public Tuple2<Iterator<Tuple2<K, C>>, Iterator<Tuple2<K, C>>> partition(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.partition(this, p);
      }

      public Tuple2<Iterator<Tuple2<K, C>>, Iterator<Tuple2<K, C>>> span(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.span(this, p);
      }

      public Iterator<Tuple2<K, C>> dropWhile(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.dropWhile(this, p);
      }

      public <B> Iterator<Tuple2<Tuple2<K, C>, B>> zip(Iterator<B> that) {
         return Iterator$class.zip(this, that);
      }

      public <A1> Iterator<A1> padTo(int len, A1 elem) {
         return Iterator$class.padTo(this, len, elem);
      }

      public Iterator<Tuple2<Tuple2<K, C>, Object>> zipWithIndex() {
         return Iterator$class.zipWithIndex(this);
      }

      public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
         return Iterator$class.zipAll(this, that, thisElem, thatElem);
      }

      public <U> void foreach(Function1<Tuple2<K, C>, U> f) {
         Iterator$class.foreach(this, f);
      }

      public boolean forall(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.forall(this, p);
      }

      public boolean exists(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.exists(this, p);
      }

      public boolean contains(Object elem) {
         return Iterator$class.contains(this, elem);
      }

      public Option<Tuple2<K, C>> find(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.find(this, p);
      }

      public int indexWhere(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.indexWhere(this, p);
      }

      public <B> int indexOf(B elem) {
         return Iterator$class.indexOf(this, elem);
      }

      public BufferedIterator<Tuple2<K, C>> buffered() {
         return Iterator$class.buffered(this);
      }

      public <B> Iterator<Tuple2<K, C>>.GroupedIterator<B> grouped(int size) {
         return Iterator$class.grouped(this, size);
      }

      public <B> Iterator<Tuple2<K, C>>.GroupedIterator<B> sliding(int size, int step) {
         return Iterator$class.sliding(this, size, step);
      }

      public int length() {
         return Iterator$class.length(this);
      }

      public Tuple2<Iterator<Tuple2<K, C>>, Iterator<Tuple2<K, C>>> duplicate() {
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

      public Traversable<Tuple2<K, C>> toTraversable() {
         return Iterator$class.toTraversable(this);
      }

      public Iterator<Tuple2<K, C>> toIterator() {
         return Iterator$class.toIterator(this);
      }

      public Stream<Tuple2<K, C>> toStream() {
         return Iterator$class.toStream(this);
      }

      public String toString() {
         return Iterator$class.toString(this);
      }

      public <B> int sliding$default$2() {
         return Iterator$class.sliding$default$2(this);
      }

      public List<Tuple2<K, C>> reversed() {
         return TraversableOnce$class.reversed(this);
      }

      public int size() {
         return TraversableOnce$class.size(this);
      }

      public boolean nonEmpty() {
         return TraversableOnce$class.nonEmpty(this);
      }

      public int count(Function1<Tuple2<K, C>, Object> p) {
         return TraversableOnce$class.count(this, p);
      }

      public <B> Option<B> collectFirst(PartialFunction<Tuple2<K, C>, B> pf) {
         return TraversableOnce$class.collectFirst(this, pf);
      }

      public <B> B $div$colon(B z, Function2<B, Tuple2<K, C>, B> op) {
         return (B)TraversableOnce$class.$div$colon(this, z, op);
      }

      public <B> B $colon$bslash(B z, Function2<Tuple2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.$colon$bslash(this, z, op);
      }

      public <B> B foldLeft(B z, Function2<B, Tuple2<K, C>, B> op) {
         return (B)TraversableOnce$class.foldLeft(this, z, op);
      }

      public <B> B foldRight(B z, Function2<Tuple2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.foldRight(this, z, op);
      }

      public <B> B reduceLeft(Function2<B, Tuple2<K, C>, B> op) {
         return (B)TraversableOnce$class.reduceLeft(this, op);
      }

      public <B> B reduceRight(Function2<Tuple2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.reduceRight(this, op);
      }

      public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<K, C>, B> op) {
         return TraversableOnce$class.reduceLeftOption(this, op);
      }

      public <B> Option<B> reduceRightOption(Function2<Tuple2<K, C>, B, B> op) {
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

      public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<K, C>, B> seqop, Function2<B, B, B> combop) {
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

      public List<Tuple2<K, C>> toList() {
         return TraversableOnce$class.toList(this);
      }

      public Iterable<Tuple2<K, C>> toIterable() {
         return TraversableOnce$class.toIterable(this);
      }

      public Seq<Tuple2<K, C>> toSeq() {
         return TraversableOnce$class.toSeq(this);
      }

      public IndexedSeq<Tuple2<K, C>> toIndexedSeq() {
         return TraversableOnce$class.toIndexedSeq(this);
      }

      public <B> Buffer<B> toBuffer() {
         return TraversableOnce$class.toBuffer(this);
      }

      public <B> Set<B> toSet() {
         return TraversableOnce$class.toSet(this);
      }

      public Vector<Tuple2<K, C>> toVector() {
         return TraversableOnce$class.toVector(this);
      }

      public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<K, C>, Col> cbf) {
         return (Col)TraversableOnce$class.to(this, cbf);
      }

      public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<K, C>, Tuple2<T, U>> ev) {
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

      public PriorityQueue<ExternalAppendOnlyMap<K, V, C>.ExternalIterator$StreamBuffer> org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap() {
         return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap;
      }

      private CompletionIterator<Tuple2<K, C>, Iterator<Tuple2<K, C>>> sortedMap() {
         return this.sortedMap;
      }

      private Seq<BufferedIterator<Tuple2<K, C>>> inputStreams() {
         return this.inputStreams;
      }

      public void org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$readNextHashCode(BufferedIterator<Tuple2<K, C>> it, ArrayBuffer<Tuple2<K, C>> buf) {
         if (it.hasNext()) {
            Tuple2 kc = (Tuple2)it.next();
            buf.$plus$eq(kc);
            int minHash = this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$hashKey(kc);

            while(it.hasNext() && ((Tuple2)it.head())._1().hashCode() == minHash) {
               kc = (Tuple2)it.next();
               buf.$plus$eq(kc);
            }
         }

      }

      private C mergeIfKeyExists(K key, C baseCombiner, ExternalAppendOnlyMap<K, V, C>.ExternalIterator$StreamBuffer buffer) {
         for(int i = 0; i < buffer.pairs().length(); ++i) {
            Tuple2 pair = (Tuple2)buffer.pairs().apply(i);
            if (BoxesRunTime.equals(pair._1(), key)) {
               this.removeFromBuffer(buffer.pairs(), i);
               return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$mergeCombiners.apply(baseCombiner, pair._2());
            }
         }

         return (C)baseCombiner;
      }

      private <T> T removeFromBuffer(ArrayBuffer<T> buffer, int index) {
         Object elem = buffer.apply(index);
         buffer.update(index, buffer.apply(buffer.size() - 1));
         buffer.reduceToSize(buffer.size() - 1);
         return (T)elem;
      }

      public boolean hasNext() {
         return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap().nonEmpty();
      }

      public Tuple2<K, C> next() {
         if (this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap().isEmpty()) {
            throw new NoSuchElementException();
         } else {
            StreamBuffer minBuffer = (StreamBuffer)this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap().dequeue();
            ArrayBuffer minPairs = minBuffer.pairs();
            int minHash = minBuffer.minKeyHash();
            Tuple2 minPair = (Tuple2)this.removeFromBuffer(minPairs, 0);
            Object minKey = minPair._1();
            Object minCombiner = minPair._2();
            Predef$.MODULE$.assert(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$hashKey(minPair) == minHash);
            ArrayBuffer mergedBuffers = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new StreamBuffer[]{minBuffer}));

            while(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap().nonEmpty() && ((StreamBuffer)this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap().head()).minKeyHash() == minHash) {
               StreamBuffer newBuffer = (StreamBuffer)this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap().dequeue();
               minCombiner = this.mergeIfKeyExists(minKey, minCombiner, newBuffer);
               mergedBuffers.$plus$eq(newBuffer);
            }

            mergedBuffers.foreach(new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExternalIterator $outer;

               public final void apply(ExternalAppendOnlyMap<K, V, C>.ExternalIterator$StreamBuffer buffer) {
                  if (buffer.isEmpty()) {
                     this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$readNextHashCode(buffer.iterator(), buffer.pairs());
                  }

                  if (!buffer.isEmpty()) {
                     this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap().enqueue(Predef$.MODULE$.wrapRefArray(new StreamBuffer[]{buffer}));
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
            return new Tuple2<K, C>(minKey, minCombiner);
         }
      }

      // $FF: synthetic method
      public ExternalAppendOnlyMap org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$$outer() {
         return this.$outer;
      }

      public ExternalIterator() {
         if (ExternalAppendOnlyMap.this == null) {
            throw null;
         } else {
            this.$outer = ExternalAppendOnlyMap.this;
            super();
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
            this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap = new PriorityQueue<ExternalAppendOnlyMap<K, V, C>.ExternalIterator$StreamBuffer>(Ordering$.MODULE$.ordered(Predef$.MODULE$.$conforms()));
            this.sortedMap = CompletionIterator$.MODULE$.<Tuple2<K, C>, Iterator<Tuple2<K, C>>>apply(ExternalAppendOnlyMap.this.destructiveIterator(ExternalAppendOnlyMap.this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$currentMap().destructiveSortedIterator(ExternalAppendOnlyMap.this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$keyComparator())), new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExternalIterator $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$freeCurrentMap();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.inputStreams = (Seq)((TraversableLike)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new CompletionIterator[]{this.sortedMap()}))).$plus$plus(ExternalAppendOnlyMap.this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$spilledMaps(), Seq$.MODULE$.canBuildFrom())).map(new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final BufferedIterator<Tuple2<K, C>> apply(Iterator<Tuple2<K, C>> it) {
                  return it.buffered();
               }
            }, Seq$.MODULE$.canBuildFrom());
            this.inputStreams().foreach(new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExternalIterator $outer;

               public final void apply(BufferedIterator<Tuple2<K, C>> it) {
                  ArrayBuffer kcPairs = new ArrayBuffer();
                  this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$readNextHashCode(it, kcPairs);
                  if (kcPairs.length() > 0) {
                     this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$mergeHeap().enqueue(Predef$.MODULE$.wrapRefArray(new StreamBuffer[]{this.$outer.new StreamBuffer(it, kcPairs)}));
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
      }

      public class StreamBuffer implements Comparable<ExternalAppendOnlyMap<K, V, C>.ExternalIterator$StreamBuffer> {
         private final BufferedIterator<Tuple2<K, C>> iterator;
         private final ArrayBuffer<Tuple2<K, C>> pairs;
         // $FF: synthetic field
         public final ExternalIterator $outer;

         public BufferedIterator<Tuple2<K, C>> iterator() {
            return this.iterator;
         }

         public ArrayBuffer<Tuple2<K, C>> pairs() {
            return this.pairs;
         }

         public boolean isEmpty() {
            return this.pairs().length() == 0;
         }

         public int minKeyHash() {
            Predef$.MODULE$.assert(this.pairs().length() > 0);
            return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$StreamBuffer$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$hashKey((Tuple2)this.pairs().head());
         }

         public int compareTo(ExternalAppendOnlyMap<K, V, C>.ExternalIterator$StreamBuffer other) {
            return other.minKeyHash() < this.minKeyHash() ? -1 : (other.minKeyHash() == this.minKeyHash() ? 0 : 1);
         }

         // $FF: synthetic method
         public ExternalIterator org$apache$spark$util$collection$ExternalAppendOnlyMap$ExternalIterator$StreamBuffer$$$outer() {
            return this.$outer;
         }

         public StreamBuffer(ExternalAppendOnlyMap<K, V, C>.ExternalIterator iterator, BufferedIterator<Tuple2<K, C>> pairs) {
            this.iterator = iterator;
            this.pairs = pairs;
            if (ExternalIterator.this == null) {
               throw null;
            } else {
               this.$outer = ExternalIterator.this;
               super();
            }
         }
      }
   }

   public class DiskMapIterator implements Iterator<Tuple2<K, C>> {
      public final File org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$file;
      private final BlockId blockId;
      private final ArrayBuffer<Object> org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets;
      private int batchIndex;
      private FileInputStream fileStream;
      private DeserializationStream deserializeStream;
      private Tuple2<K, C> nextItem;
      private int objectsRead;
      // $FF: synthetic field
      public final ExternalAppendOnlyMap $outer;

      public Iterator<Tuple2<K, C>> seq() {
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

      public Iterator<Tuple2<K, C>> take(int n) {
         return Iterator$class.take(this, n);
      }

      public Iterator<Tuple2<K, C>> drop(int n) {
         return Iterator$class.drop(this, n);
      }

      public Iterator<Tuple2<K, C>> slice(int from, int until) {
         return Iterator$class.slice(this, from, until);
      }

      public <B> Iterator<B> map(Function1<Tuple2<K, C>, B> f) {
         return Iterator$class.map(this, f);
      }

      public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
         return Iterator$class.$plus$plus(this, that);
      }

      public <B> Iterator<B> flatMap(Function1<Tuple2<K, C>, GenTraversableOnce<B>> f) {
         return Iterator$class.flatMap(this, f);
      }

      public Iterator<Tuple2<K, C>> filter(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.filter(this, p);
      }

      public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<K, C>, B, Object> p) {
         return Iterator$class.corresponds(this, that, p);
      }

      public Iterator<Tuple2<K, C>> withFilter(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.withFilter(this, p);
      }

      public Iterator<Tuple2<K, C>> filterNot(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.filterNot(this, p);
      }

      public <B> Iterator<B> collect(PartialFunction<Tuple2<K, C>, B> pf) {
         return Iterator$class.collect(this, pf);
      }

      public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<K, C>, B> op) {
         return Iterator$class.scanLeft(this, z, op);
      }

      public <B> Iterator<B> scanRight(B z, Function2<Tuple2<K, C>, B, B> op) {
         return Iterator$class.scanRight(this, z, op);
      }

      public Iterator<Tuple2<K, C>> takeWhile(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.takeWhile(this, p);
      }

      public Tuple2<Iterator<Tuple2<K, C>>, Iterator<Tuple2<K, C>>> partition(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.partition(this, p);
      }

      public Tuple2<Iterator<Tuple2<K, C>>, Iterator<Tuple2<K, C>>> span(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.span(this, p);
      }

      public Iterator<Tuple2<K, C>> dropWhile(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.dropWhile(this, p);
      }

      public <B> Iterator<Tuple2<Tuple2<K, C>, B>> zip(Iterator<B> that) {
         return Iterator$class.zip(this, that);
      }

      public <A1> Iterator<A1> padTo(int len, A1 elem) {
         return Iterator$class.padTo(this, len, elem);
      }

      public Iterator<Tuple2<Tuple2<K, C>, Object>> zipWithIndex() {
         return Iterator$class.zipWithIndex(this);
      }

      public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
         return Iterator$class.zipAll(this, that, thisElem, thatElem);
      }

      public <U> void foreach(Function1<Tuple2<K, C>, U> f) {
         Iterator$class.foreach(this, f);
      }

      public boolean forall(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.forall(this, p);
      }

      public boolean exists(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.exists(this, p);
      }

      public boolean contains(Object elem) {
         return Iterator$class.contains(this, elem);
      }

      public Option<Tuple2<K, C>> find(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.find(this, p);
      }

      public int indexWhere(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.indexWhere(this, p);
      }

      public <B> int indexOf(B elem) {
         return Iterator$class.indexOf(this, elem);
      }

      public BufferedIterator<Tuple2<K, C>> buffered() {
         return Iterator$class.buffered(this);
      }

      public <B> Iterator<Tuple2<K, C>>.GroupedIterator<B> grouped(int size) {
         return Iterator$class.grouped(this, size);
      }

      public <B> Iterator<Tuple2<K, C>>.GroupedIterator<B> sliding(int size, int step) {
         return Iterator$class.sliding(this, size, step);
      }

      public int length() {
         return Iterator$class.length(this);
      }

      public Tuple2<Iterator<Tuple2<K, C>>, Iterator<Tuple2<K, C>>> duplicate() {
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

      public Traversable<Tuple2<K, C>> toTraversable() {
         return Iterator$class.toTraversable(this);
      }

      public Iterator<Tuple2<K, C>> toIterator() {
         return Iterator$class.toIterator(this);
      }

      public Stream<Tuple2<K, C>> toStream() {
         return Iterator$class.toStream(this);
      }

      public String toString() {
         return Iterator$class.toString(this);
      }

      public <B> int sliding$default$2() {
         return Iterator$class.sliding$default$2(this);
      }

      public List<Tuple2<K, C>> reversed() {
         return TraversableOnce$class.reversed(this);
      }

      public int size() {
         return TraversableOnce$class.size(this);
      }

      public boolean nonEmpty() {
         return TraversableOnce$class.nonEmpty(this);
      }

      public int count(Function1<Tuple2<K, C>, Object> p) {
         return TraversableOnce$class.count(this, p);
      }

      public <B> Option<B> collectFirst(PartialFunction<Tuple2<K, C>, B> pf) {
         return TraversableOnce$class.collectFirst(this, pf);
      }

      public <B> B $div$colon(B z, Function2<B, Tuple2<K, C>, B> op) {
         return (B)TraversableOnce$class.$div$colon(this, z, op);
      }

      public <B> B $colon$bslash(B z, Function2<Tuple2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.$colon$bslash(this, z, op);
      }

      public <B> B foldLeft(B z, Function2<B, Tuple2<K, C>, B> op) {
         return (B)TraversableOnce$class.foldLeft(this, z, op);
      }

      public <B> B foldRight(B z, Function2<Tuple2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.foldRight(this, z, op);
      }

      public <B> B reduceLeft(Function2<B, Tuple2<K, C>, B> op) {
         return (B)TraversableOnce$class.reduceLeft(this, op);
      }

      public <B> B reduceRight(Function2<Tuple2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.reduceRight(this, op);
      }

      public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<K, C>, B> op) {
         return TraversableOnce$class.reduceLeftOption(this, op);
      }

      public <B> Option<B> reduceRightOption(Function2<Tuple2<K, C>, B, B> op) {
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

      public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<K, C>, B> seqop, Function2<B, B, B> combop) {
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

      public List<Tuple2<K, C>> toList() {
         return TraversableOnce$class.toList(this);
      }

      public Iterable<Tuple2<K, C>> toIterable() {
         return TraversableOnce$class.toIterable(this);
      }

      public Seq<Tuple2<K, C>> toSeq() {
         return TraversableOnce$class.toSeq(this);
      }

      public IndexedSeq<Tuple2<K, C>> toIndexedSeq() {
         return TraversableOnce$class.toIndexedSeq(this);
      }

      public <B> Buffer<B> toBuffer() {
         return TraversableOnce$class.toBuffer(this);
      }

      public <B> Set<B> toSet() {
         return TraversableOnce$class.toSet(this);
      }

      public Vector<Tuple2<K, C>> toVector() {
         return TraversableOnce$class.toVector(this);
      }

      public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<K, C>, Col> cbf) {
         return (Col)TraversableOnce$class.to(this, cbf);
      }

      public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<K, C>, Tuple2<T, U>> ev) {
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

      public ArrayBuffer<Object> org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets() {
         return this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets;
      }

      private int batchIndex() {
         return this.batchIndex;
      }

      private void batchIndex_$eq(int x$1) {
         this.batchIndex = x$1;
      }

      private FileInputStream fileStream() {
         return this.fileStream;
      }

      private void fileStream_$eq(FileInputStream x$1) {
         this.fileStream = x$1;
      }

      private DeserializationStream deserializeStream() {
         return this.deserializeStream;
      }

      private void deserializeStream_$eq(DeserializationStream x$1) {
         this.deserializeStream = x$1;
      }

      private Tuple2<K, C> nextItem() {
         return this.nextItem;
      }

      private void nextItem_$eq(Tuple2<K, C> x$1) {
         this.nextItem = x$1;
      }

      private int objectsRead() {
         return this.objectsRead;
      }

      private void objectsRead_$eq(int x$1) {
         this.objectsRead = x$1;
      }

      private DeserializationStream nextBatchStream() {
         DeserializationStream var10000;
         if (this.batchIndex() < this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets().length() - 1) {
            if (this.deserializeStream() != null) {
               this.deserializeStream().close();
               this.fileStream().close();
               this.deserializeStream_$eq((DeserializationStream)null);
               this.fileStream_$eq((FileInputStream)null);
            }

            long start = BoxesRunTime.unboxToLong(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets().apply(this.batchIndex()));
            this.fileStream_$eq(new FileInputStream(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$file));
            this.fileStream().getChannel().position(start);
            this.batchIndex_$eq(this.batchIndex() + 1);
            long end = BoxesRunTime.unboxToLong(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets().apply(this.batchIndex()));
            Predef$.MODULE$.assert(end >= start, new scala.Serializable(this, start, end) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DiskMapIterator $outer;
               private final long start$1;
               private final long end$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"start = ").append((Object)BoxesRunTime.boxToLong(this.start$1)).append((Object)", end = ").append((Object)BoxesRunTime.boxToLong(this.end$1)).append((Object)", batchOffsets = ").append((Object)this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets().mkString("[", ", ", "]")).toString();
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
            InputStream wrappedStream = this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$serializerManager.wrapStream(this.blockId, (InputStream)bufferedStream);
            var10000 = this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$ser().deserializeStream(wrappedStream);
         } else {
            this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$cleanup();
            var10000 = null;
         }

         return var10000;
      }

      private Tuple2<K, C> readNextItem() {
         Tuple2 var10000;
         try {
            Object k = this.deserializeStream().readKey(ClassTag$.MODULE$.Nothing());
            Object c = this.deserializeStream().readValue(ClassTag$.MODULE$.Nothing());
            Tuple2 item = new Tuple2(k, c);
            this.objectsRead_$eq(this.objectsRead() + 1);
            if ((long)this.objectsRead() == this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$serializerBatchSize()) {
               this.objectsRead_$eq(0);
               this.deserializeStream_$eq(this.nextBatchStream());
            }

            var10000 = item;
         } catch (EOFException var5) {
            this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$cleanup();
            var10000 = null;
         }

         return var10000;
      }

      public boolean hasNext() {
         if (this.nextItem() == null) {
            if (this.deserializeStream() == null) {
               this.deserializeStream_$eq(this.nextBatchStream());
               if (this.deserializeStream() == null) {
                  return false;
               }
            }

            this.nextItem_$eq(this.readNextItem());
         }

         return this.nextItem() != null;
      }

      public Tuple2<K, C> next() {
         if (this.hasNext()) {
            Tuple2 item = this.nextItem();
            this.nextItem_$eq((Tuple2)null);
            return item;
         } else {
            throw new NoSuchElementException();
         }
      }

      public void org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$cleanup() {
         this.batchIndex_$eq(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets().length());
         if (this.deserializeStream() != null) {
            this.deserializeStream().close();
            this.deserializeStream_$eq((DeserializationStream)null);
         }

         if (this.fileStream() != null) {
            this.fileStream().close();
            this.fileStream_$eq((FileInputStream)null);
         }

         if (this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$file.exists() && !this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$file.delete()) {
            this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$$outer().logWarning(new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DiskMapIterator $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$file}));
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
      public ExternalAppendOnlyMap org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$$outer() {
         return this.$outer;
      }

      public DiskMapIterator(ExternalAppendOnlyMap<K, V, C> file, File blockId, BlockId batchSizes) {
         this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$file = file;
         this.blockId = blockId;
         if (ExternalAppendOnlyMap.this == null) {
            throw null;
         } else {
            this.$outer = ExternalAppendOnlyMap.this;
            super();
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
            this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets = (ArrayBuffer)batchSizes.scanLeft(BoxesRunTime.boxToLong(0L), new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(long x$2, long x$3) {
                  return this.apply$mcJJJ$sp(x$2, x$3);
               }

               public long apply$mcJJJ$sp(long x$2, long x$3) {
                  return x$2 + x$3;
               }
            }, ArrayBuffer$.MODULE$.canBuildFrom());
            Predef$.MODULE$.assert(file.length() == BoxesRunTime.unboxToLong(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets().last()), new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DiskMapIterator $outer;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"File length is not equal to the last batch offset:\n").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"    file length = ", "\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$file.length())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"    last batch offset = ", "\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets().last()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"    all batch offsets = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$batchOffsets().mkString(",")}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.batchIndex = 0;
            this.fileStream = null;
            this.deserializeStream = null;
            this.nextItem = null;
            this.objectsRead = 0;
            ExternalAppendOnlyMap.this.org$apache$spark$util$collection$ExternalAppendOnlyMap$$context.addTaskCompletionListener((Function1)(new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DiskMapIterator $outer;

               public final void apply(TaskContext context) {
                  this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$DiskMapIterator$$cleanup();
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
      }
   }

   public class SpillableIterator implements Iterator<Tuple2<K, C>> {
      private Iterator<Tuple2<K, C>> upstream;
      private final Object SPILL_LOCK;
      private Iterator<Tuple2<K, C>> nextUpstream;
      private Tuple2<K, C> cur;
      private boolean hasSpilled;
      // $FF: synthetic field
      public final ExternalAppendOnlyMap $outer;

      public Iterator<Tuple2<K, C>> seq() {
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

      public Iterator<Tuple2<K, C>> take(int n) {
         return Iterator$class.take(this, n);
      }

      public Iterator<Tuple2<K, C>> drop(int n) {
         return Iterator$class.drop(this, n);
      }

      public Iterator<Tuple2<K, C>> slice(int from, int until) {
         return Iterator$class.slice(this, from, until);
      }

      public <B> Iterator<B> map(Function1<Tuple2<K, C>, B> f) {
         return Iterator$class.map(this, f);
      }

      public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
         return Iterator$class.$plus$plus(this, that);
      }

      public <B> Iterator<B> flatMap(Function1<Tuple2<K, C>, GenTraversableOnce<B>> f) {
         return Iterator$class.flatMap(this, f);
      }

      public Iterator<Tuple2<K, C>> filter(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.filter(this, p);
      }

      public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<K, C>, B, Object> p) {
         return Iterator$class.corresponds(this, that, p);
      }

      public Iterator<Tuple2<K, C>> withFilter(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.withFilter(this, p);
      }

      public Iterator<Tuple2<K, C>> filterNot(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.filterNot(this, p);
      }

      public <B> Iterator<B> collect(PartialFunction<Tuple2<K, C>, B> pf) {
         return Iterator$class.collect(this, pf);
      }

      public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<K, C>, B> op) {
         return Iterator$class.scanLeft(this, z, op);
      }

      public <B> Iterator<B> scanRight(B z, Function2<Tuple2<K, C>, B, B> op) {
         return Iterator$class.scanRight(this, z, op);
      }

      public Iterator<Tuple2<K, C>> takeWhile(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.takeWhile(this, p);
      }

      public Tuple2<Iterator<Tuple2<K, C>>, Iterator<Tuple2<K, C>>> partition(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.partition(this, p);
      }

      public Tuple2<Iterator<Tuple2<K, C>>, Iterator<Tuple2<K, C>>> span(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.span(this, p);
      }

      public Iterator<Tuple2<K, C>> dropWhile(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.dropWhile(this, p);
      }

      public <B> Iterator<Tuple2<Tuple2<K, C>, B>> zip(Iterator<B> that) {
         return Iterator$class.zip(this, that);
      }

      public <A1> Iterator<A1> padTo(int len, A1 elem) {
         return Iterator$class.padTo(this, len, elem);
      }

      public Iterator<Tuple2<Tuple2<K, C>, Object>> zipWithIndex() {
         return Iterator$class.zipWithIndex(this);
      }

      public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
         return Iterator$class.zipAll(this, that, thisElem, thatElem);
      }

      public <U> void foreach(Function1<Tuple2<K, C>, U> f) {
         Iterator$class.foreach(this, f);
      }

      public boolean forall(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.forall(this, p);
      }

      public boolean exists(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.exists(this, p);
      }

      public boolean contains(Object elem) {
         return Iterator$class.contains(this, elem);
      }

      public Option<Tuple2<K, C>> find(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.find(this, p);
      }

      public int indexWhere(Function1<Tuple2<K, C>, Object> p) {
         return Iterator$class.indexWhere(this, p);
      }

      public <B> int indexOf(B elem) {
         return Iterator$class.indexOf(this, elem);
      }

      public BufferedIterator<Tuple2<K, C>> buffered() {
         return Iterator$class.buffered(this);
      }

      public <B> Iterator<Tuple2<K, C>>.GroupedIterator<B> grouped(int size) {
         return Iterator$class.grouped(this, size);
      }

      public <B> Iterator<Tuple2<K, C>>.GroupedIterator<B> sliding(int size, int step) {
         return Iterator$class.sliding(this, size, step);
      }

      public int length() {
         return Iterator$class.length(this);
      }

      public Tuple2<Iterator<Tuple2<K, C>>, Iterator<Tuple2<K, C>>> duplicate() {
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

      public Traversable<Tuple2<K, C>> toTraversable() {
         return Iterator$class.toTraversable(this);
      }

      public Iterator<Tuple2<K, C>> toIterator() {
         return Iterator$class.toIterator(this);
      }

      public Stream<Tuple2<K, C>> toStream() {
         return Iterator$class.toStream(this);
      }

      public String toString() {
         return Iterator$class.toString(this);
      }

      public <B> int sliding$default$2() {
         return Iterator$class.sliding$default$2(this);
      }

      public List<Tuple2<K, C>> reversed() {
         return TraversableOnce$class.reversed(this);
      }

      public int size() {
         return TraversableOnce$class.size(this);
      }

      public boolean nonEmpty() {
         return TraversableOnce$class.nonEmpty(this);
      }

      public int count(Function1<Tuple2<K, C>, Object> p) {
         return TraversableOnce$class.count(this, p);
      }

      public <B> Option<B> collectFirst(PartialFunction<Tuple2<K, C>, B> pf) {
         return TraversableOnce$class.collectFirst(this, pf);
      }

      public <B> B $div$colon(B z, Function2<B, Tuple2<K, C>, B> op) {
         return (B)TraversableOnce$class.$div$colon(this, z, op);
      }

      public <B> B $colon$bslash(B z, Function2<Tuple2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.$colon$bslash(this, z, op);
      }

      public <B> B foldLeft(B z, Function2<B, Tuple2<K, C>, B> op) {
         return (B)TraversableOnce$class.foldLeft(this, z, op);
      }

      public <B> B foldRight(B z, Function2<Tuple2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.foldRight(this, z, op);
      }

      public <B> B reduceLeft(Function2<B, Tuple2<K, C>, B> op) {
         return (B)TraversableOnce$class.reduceLeft(this, op);
      }

      public <B> B reduceRight(Function2<Tuple2<K, C>, B, B> op) {
         return (B)TraversableOnce$class.reduceRight(this, op);
      }

      public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<K, C>, B> op) {
         return TraversableOnce$class.reduceLeftOption(this, op);
      }

      public <B> Option<B> reduceRightOption(Function2<Tuple2<K, C>, B, B> op) {
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

      public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<K, C>, B> seqop, Function2<B, B, B> combop) {
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

      public List<Tuple2<K, C>> toList() {
         return TraversableOnce$class.toList(this);
      }

      public Iterable<Tuple2<K, C>> toIterable() {
         return TraversableOnce$class.toIterable(this);
      }

      public Seq<Tuple2<K, C>> toSeq() {
         return TraversableOnce$class.toSeq(this);
      }

      public IndexedSeq<Tuple2<K, C>> toIndexedSeq() {
         return TraversableOnce$class.toIndexedSeq(this);
      }

      public <B> Buffer<B> toBuffer() {
         return TraversableOnce$class.toBuffer(this);
      }

      public <B> Set<B> toSet() {
         return TraversableOnce$class.toSet(this);
      }

      public Vector<Tuple2<K, C>> toVector() {
         return TraversableOnce$class.toVector(this);
      }

      public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<K, C>, Col> cbf) {
         return (Col)TraversableOnce$class.to(this, cbf);
      }

      public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<K, C>, Tuple2<T, U>> ev) {
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

      public Iterator<Tuple2<K, C>> upstream() {
         return this.upstream;
      }

      public void upstream_$eq(Iterator<Tuple2<K, C>> x$1) {
         this.upstream = x$1;
      }

      private Object SPILL_LOCK() {
         return this.SPILL_LOCK;
      }

      private Iterator<Tuple2<K, C>> nextUpstream() {
         return this.nextUpstream;
      }

      private void nextUpstream_$eq(Iterator<Tuple2<K, C>> x$1) {
         this.nextUpstream = x$1;
      }

      private Tuple2<K, C> cur() {
         return this.cur;
      }

      private void cur_$eq(Tuple2<K, C> x$1) {
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
               this.org$apache$spark$util$collection$ExternalAppendOnlyMap$SpillableIterator$$$outer().logInfo(new scala.Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final SpillableIterator $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " force spilling in-memory map to disk and "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$SpillableIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$context.taskAttemptId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"it will release ", " memory"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.util.Utils$.MODULE$.bytesToString(this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$SpillableIterator$$$outer().protected$getUsed(this.$outer.org$apache$spark$util$collection$ExternalAppendOnlyMap$SpillableIterator$$$outer()))}))).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               this.nextUpstream_$eq(this.org$apache$spark$util$collection$ExternalAppendOnlyMap$SpillableIterator$$$outer().org$apache$spark$util$collection$ExternalAppendOnlyMap$$spillMemoryIteratorToDisk(this.upstream()));
               this.hasSpilled_$eq(true);
               var10000 = BoxesRunTime.boxToBoolean(true);
            }

            var2 = var10000;
         }

         return BoxesRunTime.unboxToBoolean(var2);
      }

      public Tuple2<K, C> readNext() {
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

      public Tuple2<K, C> next() {
         Tuple2 r = this.cur();
         this.cur_$eq(this.readNext());
         return r;
      }

      // $FF: synthetic method
      public ExternalAppendOnlyMap org$apache$spark$util$collection$ExternalAppendOnlyMap$SpillableIterator$$$outer() {
         return this.$outer;
      }

      public SpillableIterator(ExternalAppendOnlyMap<K, V, C> upstream) {
         this.upstream = upstream;
         if (ExternalAppendOnlyMap.this == null) {
            throw null;
         } else {
            this.$outer = ExternalAppendOnlyMap.this;
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

   public static class HashComparator<K> implements Comparator<K> {
      public int compare(K key1, K key2) {
         int hash1 = ExternalAppendOnlyMap$.MODULE$.org$apache$spark$util$collection$ExternalAppendOnlyMap$$hash(key1);
         int hash2 = ExternalAppendOnlyMap$.MODULE$.org$apache$spark$util$collection$ExternalAppendOnlyMap$$hash(key2);
         return hash1 < hash2 ? -1 : (hash1 == hash2 ? 0 : 1);
      }
   }
}
