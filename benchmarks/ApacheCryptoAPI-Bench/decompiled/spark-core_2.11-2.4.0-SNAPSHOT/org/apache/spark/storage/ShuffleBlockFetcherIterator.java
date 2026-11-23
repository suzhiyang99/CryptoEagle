package org.apache.spark.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.concurrent.GuardedBy;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.executor.TempShuffleReadMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.buffer.FileSegmentManagedBuffer;
import org.apache.spark.network.buffer.ManagedBuffer;
import org.apache.spark.network.shuffle.BlockFetchingListener;
import org.apache.spark.network.shuffle.ShuffleClient;
import org.apache.spark.network.shuffle.TempFileManager;
import org.apache.spark.shuffle.FetchFailedException;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.io.ChunkedByteBufferOutputStream;
import org.slf4j.Logger;
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
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple5;
import scala.collection.BufferedIterator;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Traversable;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.StringOps;
import scala.collection.immutable.Vector;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.HashSet$;
import scala.collection.mutable.Queue;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.math.package$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction2;
import scala.runtime.AbstractFunction3;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.LongRef;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0011-d!B\u0001\u0003\u0005\u0011Q!aG*ik\u001a4G.\u001a\"m_\u000e\\g)\u001a;dQ\u0016\u0014\u0018\n^3sCR|'O\u0003\u0002\u0004\t\u000591\u000f^8sC\u001e,'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u000b\u0001Y\u0011#L\u001b\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\r\u00112D\b\b\u0003'eq!\u0001\u0006\r\u000e\u0003UQ!AF\f\u0002\rq\u0012xn\u001c;?\u0007\u0001I\u0011AD\u0005\u000355\tq\u0001]1dW\u0006<W-\u0003\u0002\u001d;\tA\u0011\n^3sCR|'O\u0003\u0002\u001b\u001bA!AbH\u0011&\u0013\t\u0001SB\u0001\u0004UkBdWM\r\t\u0003E\rj\u0011AA\u0005\u0003I\t\u0011qA\u00117pG.LE\r\u0005\u0002'W5\tqE\u0003\u0002)S\u0005\u0011\u0011n\u001c\u0006\u0002U\u0005!!.\u0019<b\u0013\tasEA\u0006J]B,Ho\u0015;sK\u0006l\u0007C\u0001\u00184\u001b\u0005y#B\u0001\u00192\u0003\u001d\u0019\b.\u001e4gY\u0016T!A\r\u0003\u0002\u000f9,Go^8sW&\u0011Ag\f\u0002\u0010)\u0016l\u0007OR5mK6\u000bg.Y4feB\u0011a'O\u0007\u0002o)\u0011\u0001\bB\u0001\tS:$XM\u001d8bY&\u0011!h\u000e\u0002\b\u0019><w-\u001b8h\u0011!a\u0004A!A!\u0002\u0013i\u0014aB2p]R,\u0007\u0010\u001e\t\u0003}}j\u0011\u0001B\u0005\u0003\u0001\u0012\u00111\u0002V1tW\u000e{g\u000e^3yi\"A!\t\u0001B\u0001B\u0003%1)A\u0007tQV4g\r\\3DY&,g\u000e\u001e\t\u0003]\u0011K!!R\u0018\u0003\u001bMCWO\u001a4mK\u000ec\u0017.\u001a8u\u0011!9\u0005A!A!\u0002\u0013A\u0015\u0001\u00042m_\u000e\\W*\u00198bO\u0016\u0014\bC\u0001\u0012J\u0013\tQ%A\u0001\u0007CY>\u001c7.T1oC\u001e,'\u000f\u0003\u0005M\u0001\t\u0005\t\u0015!\u0003N\u0003=\u0011Gn\\2lg\nK\u0018\t\u001a3sKN\u001c\bc\u0001\nO!&\u0011q*\b\u0002\u0004'\u0016\f\b\u0003\u0002\u0007 #R\u0003\"A\t*\n\u0005M\u0013!A\u0004\"m_\u000e\\W*\u00198bO\u0016\u0014\u0018\n\u001a\t\u0004%9+\u0006\u0003\u0002\u0007 CY\u0003\"\u0001D,\n\u0005ak!\u0001\u0002'p]\u001eD\u0001B\u0017\u0001\u0003\u0002\u0003\u0006IaW\u0001\u000egR\u0014X-Y7Xe\u0006\u0004\b/\u001a:\u0011\u000b1a\u0016%J\u0013\n\u0005uk!!\u0003$v]\u000e$\u0018n\u001c83\u0011!y\u0006A!A!\u0002\u00131\u0016\u0001E7bq\nKH/Z:J]\u001ac\u0017n\u001a5u\u0011!\t\u0007A!A!\u0002\u0013\u0011\u0017aD7bqJ+\u0017o]%o\r2Lw\r\u001b;\u0011\u00051\u0019\u0017B\u00013\u000e\u0005\rIe\u000e\u001e\u0005\tM\u0002\u0011\t\u0011)A\u0005E\u0006YR.\u0019=CY>\u001c7n]%o\r2Lw\r\u001b;QKJ\fE\r\u001a:fgND\u0001\u0002\u001b\u0001\u0003\u0002\u0003\u0006IAV\u0001\u0017[\u0006D(+Z9TSj,7\u000b[;gM2,Gk\\'f[\"A!\u000e\u0001B\u0001B\u0003%1.A\u0007eKR,7\r^\"peJ,\b\u000f\u001e\t\u0003\u00191L!!\\\u0007\u0003\u000f\t{w\u000e\\3b]\")q\u000e\u0001C\u0001a\u00061A(\u001b8jiz\"2\"\u001d:tiV4x\u000f_={wB\u0011!\u0005\u0001\u0005\u0006y9\u0004\r!\u0010\u0005\u0006\u0005:\u0004\ra\u0011\u0005\u0006\u000f:\u0004\r\u0001\u0013\u0005\u0006\u0019:\u0004\r!\u0014\u0005\u00065:\u0004\ra\u0017\u0005\u0006?:\u0004\rA\u0016\u0005\u0006C:\u0004\rA\u0019\u0005\u0006M:\u0004\rA\u0019\u0005\u0006Q:\u0004\rA\u0016\u0005\u0006U:\u0004\ra\u001b\u0005\u0007{\u0002\u0001\u000b\u0015\u00022\u0002!9,XN\u00117pG.\u001cHk\u001c$fi\u000eD\u0007BB@\u0001A\u0003&!-\u0001\nok6\u0014En\\2lgB\u0013xnY3tg\u0016$\u0007bBA\u0002\u0001\u0001\u0006IAV\u0001\ngR\f'\u000f\u001e+j[\u0016D\u0001\"a\u0002\u0001A\u0003%\u0011\u0011B\u0001\fY>\u001c\u0017\r\u001c\"m_\u000e\\7\u000fE\u0003\u0002\f\u0005U\u0011%\u0004\u0002\u0002\u000e)!\u0011qBA\t\u0003\u001diW\u000f^1cY\u0016T1!a\u0005\u000e\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0003/\tiAA\u0006BeJ\f\u0017PQ;gM\u0016\u0014\b\u0002CA\u000e\u0001\u0001\u0006I!!\b\u0002\u0019I,Wn\u001c;f\u00052|7m[:\u0011\u000b\u0005-\u0011qD\u0011\n\t\u0005\u0005\u0012Q\u0002\u0002\b\u0011\u0006\u001c\bnU3u\u0011!\t)\u0003\u0001Q\u0001\n\u0005\u001d\u0012a\u0002:fgVdGo\u001d\t\u0007\u0003S\t\u0019$a\u000e\u000e\u0005\u0005-\"\u0002BA\u0017\u0003_\t!bY8oGV\u0014(/\u001a8u\u0015\r\t\t$K\u0001\u0005kRLG.\u0003\u0003\u00026\u0005-\"a\u0005'j].,GM\u00117pG.LgnZ)vKV,\u0007\u0003BA\u001d\u0005\u0007r1AIA\u001e\u000f!\tiD\u0001E\u0001\u0005\u0005}\u0012aG*ik\u001a4G.\u001a\"m_\u000e\\g)\u001a;dQ\u0016\u0014\u0018\n^3sCR|'\u000fE\u0002#\u0003\u00032q!\u0001\u0002\t\u0002\t\t\u0019eE\u0002\u0002B-Aqa\\A!\t\u0003\t9\u0005\u0006\u0002\u0002@\u00199\u00111JA!\u0001\u00065#\u0001\u0004$fi\u000eD'+Z9vKN$8cBA%\u0017\u0005=\u0013Q\u000b\t\u0004\u0019\u0005E\u0013bAA*\u001b\t9\u0001K]8ek\u000e$\bc\u0001\u0007\u0002X%\u0019\u0011\u0011L\u0007\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0017\u0005u\u0013\u0011\nBK\u0002\u0013\u0005\u0011qL\u0001\bC\u0012$'/Z:t+\u0005\t\u0006BCA2\u0003\u0013\u0012\t\u0012)A\u0005#\u0006A\u0011\r\u001a3sKN\u001c\b\u0005C\u0006\u0002h\u0005%#Q3A\u0005\u0002\u0005%\u0014A\u00022m_\u000e\\7/F\u0001U\u0011)\ti'!\u0013\u0003\u0012\u0003\u0006I\u0001V\u0001\bE2|7m[:!\u0011\u001dy\u0017\u0011\nC\u0001\u0003c\"b!a\u001d\u0002x\u0005e\u0004\u0003BA;\u0003\u0013j!!!\u0011\t\u000f\u0005u\u0013q\u000ea\u0001#\"9\u0011qMA8\u0001\u0004!\u0006BCA?\u0003\u0013\u0012\r\u0011\"\u0001\u0002\u0000\u0005!1/\u001b>f+\u00051\u0006\u0002CAB\u0003\u0013\u0002\u000b\u0011\u0002,\u0002\u000bML'0\u001a\u0011\t\u0015\u0005\u001d\u0015\u0011JA\u0001\n\u0003\tI)\u0001\u0003d_BLHCBA:\u0003\u0017\u000bi\tC\u0005\u0002^\u0005\u0015\u0005\u0013!a\u0001#\"I\u0011qMAC!\u0003\u0005\r\u0001\u0016\u0005\u000b\u0003#\u000bI%%A\u0005\u0002\u0005M\u0015AD2paf$C-\u001a4bk2$H%M\u000b\u0003\u0003+S3!UALW\t\tI\n\u0005\u0003\u0002\u001c\u0006\u0015VBAAO\u0015\u0011\ty*!)\u0002\u0013Ut7\r[3dW\u0016$'bAAR\u001b\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005\u001d\u0016Q\u0014\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007BCAV\u0003\u0013\n\n\u0011\"\u0001\u0002.\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012TCAAXU\r!\u0016q\u0013\u0005\u000b\u0003g\u000bI%!A\u0005B\u0005U\u0016!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070\u0006\u0002\u00028B!\u0011\u0011XA`\u001b\t\tYLC\u0002\u0002>&\nA\u0001\\1oO&!\u0011\u0011YA^\u0005\u0019\u0019FO]5oO\"Q\u0011QYA%\u0003\u0003%\t!a2\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003\tD!\"a3\u0002J\u0005\u0005I\u0011AAg\u00039\u0001(o\u001c3vGR,E.Z7f]R$B!a4\u0002VB\u0019A\"!5\n\u0007\u0005MWBA\u0002B]fD\u0011\"a6\u0002J\u0006\u0005\t\u0019\u00012\u0002\u0007a$\u0013\u0007\u0003\u0006\u0002\\\u0006%\u0013\u0011!C!\u0003;\fq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0003?\u0004b!!9\u0002d\u0006=WBAA\t\u0013\ra\u0012\u0011\u0003\u0005\u000b\u0003O\fI%!A\u0005\u0002\u0005%\u0018\u0001C2b]\u0016\u000bX/\u00197\u0015\u0007-\fY\u000f\u0003\u0006\u0002X\u0006\u0015\u0018\u0011!a\u0001\u0003\u001fD!\"a<\u0002J\u0005\u0005I\u0011IAy\u0003!A\u0017m\u001d5D_\u0012,G#\u00012\t\u0015\u0005U\u0018\u0011JA\u0001\n\u0003\n90\u0001\u0005u_N#(/\u001b8h)\t\t9\f\u0003\u0006\u0002|\u0006%\u0013\u0011!C!\u0003{\fa!Z9vC2\u001cHcA6\u0002\u0000\"Q\u0011q[A}\u0003\u0003\u0005\r!a4\b\u0015\t\r\u0011\u0011IA\u0001\u0012\u0003\u0011)!\u0001\u0007GKR\u001c\u0007NU3rk\u0016\u001cH\u000f\u0005\u0003\u0002v\t\u001daACA&\u0003\u0003\n\t\u0011#\u0001\u0003\nM1!q\u0001B\u0006\u0003+\u0002\u0002B!\u0004\u0003\u0014E#\u00161O\u0007\u0003\u0005\u001fQ1A!\u0005\u000e\u0003\u001d\u0011XO\u001c;j[\u0016LAA!\u0006\u0003\u0010\t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\u001c\u001a\t\u000f=\u00149\u0001\"\u0001\u0003\u001aQ\u0011!Q\u0001\u0005\u000b\u0003k\u00149!!A\u0005F\u0005]\bB\u0003B\u0010\u0005\u000f\t\t\u0011\"!\u0003\"\u0005)\u0011\r\u001d9msR1\u00111\u000fB\u0012\u0005KAq!!\u0018\u0003\u001e\u0001\u0007\u0011\u000bC\u0004\u0002h\tu\u0001\u0019\u0001+\t\u0015\t%\"qAA\u0001\n\u0003\u0013Y#A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\t5\"1\u0007\t\u0005\u0019\t=\u0002+C\u0002\u000325\u0011aa\u00149uS>t\u0007B\u0003B\u001b\u0005O\t\t\u00111\u0001\u0002t\u0005\u0019\u0001\u0010\n\u0019\t\u0015\te\"qAA\u0001\n\u0013\u0011Y$A\u0006sK\u0006$'+Z:pYZ,GC\u0001B\u001f!\u0011\tILa\u0010\n\t\t\u0005\u00131\u0018\u0002\u0007\u001f\nTWm\u0019;\u0007\u0017\t\u0015\u0013\u0011\tI\u0001$C\u0011!q\t\u0002\f\r\u0016$8\r\u001b*fgVdGoE\u0002\u0003D-A!Ba\u0013\u0003D\t\u0007i\u0011\u0001B'\u0003\u001d\u0011Gn\\2l\u0013\u0012,\u0012!\t\u0005\u000b\u0003;\u0012\u0019E1A\u0007\u0002\u0005}\u0013F\u0002B\"\u0005'\u00129L\u0002\u0005\u0003V\u0005\u0005\u0003I\u0001B,\u0005I1\u0015-\u001b7ve\u00164U\r^2i%\u0016\u001cX\u000f\u001c;\u0014\u0013\tM3B!\u0017\u0002P\u0005U\u0003\u0003BA;\u0005\u0007B1Ba\u0013\u0003T\tU\r\u0011\"\u0001\u0003N!Q!q\fB*\u0005#\u0005\u000b\u0011B\u0011\u0002\u0011\tdwnY6JI\u0002B1\"!\u0018\u0003T\tU\r\u0011\"\u0001\u0002`!Q\u00111\rB*\u0005#\u0005\u000b\u0011B)\t\u0017\t\u001d$1\u000bBK\u0002\u0013\u0005!\u0011N\u0001\u0002KV\u0011!1\u000e\t\u0004%\t5\u0014b\u0001B8;\tIA\u000b\u001b:po\u0006\u0014G.\u001a\u0005\f\u0005g\u0012\u0019F!E!\u0002\u0013\u0011Y'\u0001\u0002fA!9qNa\u0015\u0005\u0002\t]D\u0003\u0003B=\u0005w\u0012iHa \u0011\t\u0005U$1\u000b\u0005\b\u0005\u0017\u0012)\b1\u0001\"\u0011\u001d\tiF!\u001eA\u0002EC\u0001Ba\u001a\u0003v\u0001\u0007!1\u000e\u0005\u000b\u0003\u000f\u0013\u0019&!A\u0005\u0002\t\rE\u0003\u0003B=\u0005\u000b\u00139I!#\t\u0013\t-#\u0011\u0011I\u0001\u0002\u0004\t\u0003\"CA/\u0005\u0003\u0003\n\u00111\u0001R\u0011)\u00119G!!\u0011\u0002\u0003\u0007!1\u000e\u0005\u000b\u0003#\u0013\u0019&%A\u0005\u0002\t5UC\u0001BHU\r\t\u0013q\u0013\u0005\u000b\u0003W\u0013\u0019&%A\u0005\u0002\u0005M\u0005B\u0003BK\u0005'\n\n\u0011\"\u0001\u0003\u0018\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u001aTC\u0001BMU\u0011\u0011Y'a&\t\u0015\u0005M&1KA\u0001\n\u0003\n)\f\u0003\u0006\u0002F\nM\u0013\u0011!C\u0001\u0003\u000fD!\"a3\u0003T\u0005\u0005I\u0011\u0001BQ)\u0011\tyMa)\t\u0013\u0005]'qTA\u0001\u0002\u0004\u0011\u0007BCAn\u0005'\n\t\u0011\"\u0011\u0002^\"Q\u0011q\u001dB*\u0003\u0003%\tA!+\u0015\u0007-\u0014Y\u000b\u0003\u0006\u0002X\n\u001d\u0016\u0011!a\u0001\u0003\u001fD!\"a<\u0003T\u0005\u0005I\u0011IAy\u0011)\t)Pa\u0015\u0002\u0002\u0013\u0005\u0013q\u001f\u0005\u000b\u0003w\u0014\u0019&!A\u0005B\tMFcA6\u00036\"Q\u0011q\u001bBY\u0003\u0003\u0005\r!a4\u0007\u0011\te\u0016\u0011\t!\u0003\u0005w\u0013!cU;dG\u0016\u001c8OR3uG\"\u0014Vm];miNI!qW\u0006\u0003Z\u0005=\u0013Q\u000b\u0005\f\u0005\u0017\u00129L!f\u0001\n\u0003\u0011i\u0005\u0003\u0006\u0003`\t]&\u0011#Q\u0001\n\u0005B1\"!\u0018\u00038\nU\r\u0011\"\u0001\u0002`!Q\u00111\rB\\\u0005#\u0005\u000b\u0011B)\t\u0017\u0005u$q\u0017BK\u0002\u0013\u0005\u0011q\u0010\u0005\u000b\u0003\u0007\u00139L!E!\u0002\u00131\u0006b\u0003Bf\u0005o\u0013)\u001a!C\u0001\u0005\u001b\f1AY;g+\t\u0011y\r\u0005\u0003\u0003R\n]WB\u0001Bj\u0015\r\u0011).M\u0001\u0007EV4g-\u001a:\n\t\te'1\u001b\u0002\u000e\u001b\u0006t\u0017mZ3e\u0005V4g-\u001a:\t\u0017\tu'q\u0017B\tB\u0003%!qZ\u0001\u0005EV4\u0007\u0005C\u0006\u0003b\n]&Q3A\u0005\u0002\t\r\u0018\u0001E5t\u001d\u0016$xo\u001c:l%\u0016\fHi\u001c8f+\u0005Y\u0007B\u0003Bt\u0005o\u0013\t\u0012)A\u0005W\u0006\t\u0012n\u001d(fi^|'o\u001b*fc\u0012{g.\u001a\u0011\t\u000f=\u00149\f\"\u0001\u0003lRa!Q\u001eBx\u0005c\u0014\u0019P!>\u0003xB!\u0011Q\u000fB\\\u0011\u001d\u0011YE!;A\u0002\u0005Bq!!\u0018\u0003j\u0002\u0007\u0011\u000bC\u0004\u0002~\t%\b\u0019\u0001,\t\u0011\t-'\u0011\u001ea\u0001\u0005\u001fDqA!9\u0003j\u0002\u00071\u000e\u0003\u0006\u0002\b\n]\u0016\u0011!C\u0001\u0005w$BB!<\u0003~\n}8\u0011AB\u0002\u0007\u000bA\u0011Ba\u0013\u0003zB\u0005\t\u0019A\u0011\t\u0013\u0005u#\u0011 I\u0001\u0002\u0004\t\u0006\"CA?\u0005s\u0004\n\u00111\u0001W\u0011)\u0011YM!?\u0011\u0002\u0003\u0007!q\u001a\u0005\n\u0005C\u0014I\u0010%AA\u0002-D!\"!%\u00038F\u0005I\u0011\u0001BG\u0011)\tYKa.\u0012\u0002\u0013\u0005\u00111\u0013\u0005\u000b\u0005+\u00139,%A\u0005\u0002\r5QCAB\bU\r1\u0016q\u0013\u0005\u000b\u0007'\u00119,%A\u0005\u0002\rU\u0011AD2paf$C-\u001a4bk2$H\u0005N\u000b\u0003\u0007/QCAa4\u0002\u0018\"Q11\u0004B\\#\u0003%\ta!\b\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%kU\u00111q\u0004\u0016\u0004W\u0006]\u0005BCAZ\u0005o\u000b\t\u0011\"\u0011\u00026\"Q\u0011Q\u0019B\\\u0003\u0003%\t!a2\t\u0015\u0005-'qWA\u0001\n\u0003\u00199\u0003\u0006\u0003\u0002P\u000e%\u0002\"CAl\u0007K\t\t\u00111\u0001c\u0011)\tYNa.\u0002\u0002\u0013\u0005\u0013Q\u001c\u0005\u000b\u0003O\u00149,!A\u0005\u0002\r=BcA6\u00042!Q\u0011q[B\u0017\u0003\u0003\u0005\r!a4\t\u0015\u0005=(qWA\u0001\n\u0003\n\t\u0010\u0003\u0006\u0002v\n]\u0016\u0011!C!\u0003oD!\"a?\u00038\u0006\u0005I\u0011IB\u001d)\rY71\b\u0005\u000b\u0003/\u001c9$!AA\u0002\u0005=waCB \u0003\u0003\n\t\u0011#\u0001\u0003\u0007\u0003\n!cU;dG\u0016\u001c8OR3uG\"\u0014Vm];miB!\u0011QOB\"\r-\u0011I,!\u0011\u0002\u0002#\u0005!a!\u0012\u0014\r\r\r3qIA+!1\u0011ia!\u0013\"#Z\u0013ym\u001bBw\u0013\u0011\u0019YEa\u0004\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>tW\u0007C\u0004p\u0007\u0007\"\taa\u0014\u0015\u0005\r\u0005\u0003BCA{\u0007\u0007\n\t\u0011\"\u0012\u0002x\"Q!qDB\"\u0003\u0003%\ti!\u0016\u0015\u0019\t58qKB-\u00077\u001aifa\u0018\t\u000f\t-31\u000ba\u0001C!9\u0011QLB*\u0001\u0004\t\u0006bBA?\u0007'\u0002\rA\u0016\u0005\t\u0005\u0017\u001c\u0019\u00061\u0001\u0003P\"9!\u0011]B*\u0001\u0004Y\u0007B\u0003B\u0015\u0007\u0007\n\t\u0011\"!\u0004dQ!1QMB7!\u0015a!qFB4!%a1\u0011N\u0011R-\n=7.C\u0002\u0004l5\u0011a\u0001V;qY\u0016,\u0004B\u0003B\u001b\u0007C\n\t\u00111\u0001\u0003n\"Q!\u0011HB\"\u0003\u0003%IAa\u000f\b\u0017\rM\u0014\u0011IA\u0001\u0012\u0003\u00111QO\u0001\u0013\r\u0006LG.\u001e:f\r\u0016$8\r\u001b*fgVdG\u000f\u0005\u0003\u0002v\r]da\u0003B+\u0003\u0003\n\t\u0011#\u0001\u0003\u0007s\u001abaa\u001e\u0004|\u0005U\u0003C\u0003B\u0007\u0007{\n\u0013Ka\u001b\u0003z%!1q\u0010B\b\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|gn\r\u0005\b_\u000e]D\u0011ABB)\t\u0019)\b\u0003\u0006\u0002v\u000e]\u0014\u0011!C#\u0003oD!Ba\b\u0004x\u0005\u0005I\u0011QBE)!\u0011Iha#\u0004\u000e\u000e=\u0005b\u0002B&\u0007\u000f\u0003\r!\t\u0005\b\u0003;\u001a9\t1\u0001R\u0011!\u00119ga\"A\u0002\t-\u0004B\u0003B\u0015\u0007o\n\t\u0011\"!\u0004\u0014R!1QSBO!\u0015a!qFBL!\u001da1\u0011T\u0011R\u0005WJ1aa'\u000e\u0005\u0019!V\u000f\u001d7fg!Q!QGBI\u0003\u0003\u0005\rA!\u001f\t\u0015\te2qOA\u0001\n\u0013\u0011Y\u0004\u0003\u0005\u0004$\u0002\u0001\u000b\u0015BBS\u00035\u0019WO\u001d:f]R\u0014Vm];miB!\u0011\u0011\bB\\Q\u0011\u0019\tk!+\u0011\u00071\u0019Y+C\u0002\u0004.6\u0011\u0001B^8mCRLG.\u001a\u0005\t\u0007c\u0003\u0001\u0015!\u0003\u00044\u0006ia-\u001a;dQJ+\u0017/^3tiN\u0004b!a\u0003\u00046\u000ee\u0016\u0002BB\\\u0003\u001b\u0011Q!U;fk\u0016\u0004B!!\u000f\u0002J!A1Q\u0018\u0001!\u0002\u0013\u0019y,A\u000beK\u001a,'O]3e\r\u0016$8\r\u001b*fcV,7\u000f^:\u0011\u000f\u0005-1\u0011Y)\u00044&!11YA\u0007\u0005\u001dA\u0015m\u001d5NCBDqaa2\u0001A\u0003&a+A\u0007csR,7/\u00138GY&<\u0007\u000e\u001e\u0005\b\u0007\u0017\u0004\u0001\u0015)\u0003c\u00031\u0011X-]:J]\u001ac\u0017n\u001a5u\u0011!\u0019y\r\u0001Q\u0001\n\rE\u0017a\u00078v[\ncwnY6t\u0013:4E.[4iiB+'/\u00113ee\u0016\u001c8\u000f\u0005\u0004\u0002\f\r\u0005\u0017K\u0019\u0005\t\u0007+\u0004\u0001\u0015!\u0003\u0002\u001e\u0005y1m\u001c:skB$X\r\u001a\"m_\u000e\\7\u000f\u0003\u0005\u0004Z\u0002\u0001\u000b\u0011BBn\u00039\u0019\b.\u001e4gY\u0016lU\r\u001e:jGN\u0004Ba!8\u0004d6\u00111q\u001c\u0006\u0004\u0007C$\u0011\u0001C3yK\u000e,Ho\u001c:\n\t\r\u00158q\u001c\u0002\u0017)\u0016l\u0007o\u00155vM\u001adWMU3bI6+GO]5dg\"91\u0011\u001e\u0001!B\u0013Y\u0017\u0001C5t5>l'-[3)\u0011\r\u001d8Q^B\u007f\u0007\u007f\u0004Baa<\u0004z6\u00111\u0011\u001f\u0006\u0005\u0003[\u0019\u0019P\u0003\u0003\u0002$\u000eU(BAB|\u0003\u0015Q\u0017M^1y\u0013\u0011\u0019Yp!=\u0003\u0013\u001d+\u0018M\u001d3fI\nK\u0018!\u0002<bYV,\u0017E\u0001C\u0001\u0003\u0011!\b.[:\t\u0011\u0011\u0015\u0001\u0001)A\u0005\t\u000f\tqb\u001d5vM\u001adWMR5mKN\u001cV\r\u001e\t\u0007\u0003\u0017\ty\u0002\"\u0003\u0011\u0007\u0019\"Y!C\u0002\u0005\u000e\u001d\u0012AAR5mK\"BA1ABw\u0007{\u001cy\u0010\u0003\u0005\u0005\u0014\u0001!\tA\u0001C\u000b\u0003i\u0011X\r\\3bg\u0016\u001cUO\u001d:f]R\u0014Vm];mi\n+hMZ3s)\t!9\u0002E\u0002\r\t3I1\u0001b\u0007\u000e\u0005\u0011)f.\u001b;\t\u000f\u0011}\u0001\u0001\"\u0011\u0005\"\u0005q1M]3bi\u0016$V-\u001c9GS2,GC\u0001C\u0005\u0011\u001d!)\u0003\u0001C!\tO\tqC]3hSN$XM\u001d+f[B4\u0015\u000e\\3U_\u000ecW-\u00198\u0015\u0007-$I\u0003\u0003\u0005\u0005,\u0011\r\u0002\u0019\u0001C\u0005\u0003\u00111\u0017\u000e\\3\t\u0011\u0011=\u0002\u0001)C\u0005\t+\tqa\u00197fC:,\b\u000f\u0003\u0005\u00054\u0001\u0001K\u0011\u0002C\u001b\u0003-\u0019XM\u001c3SKF,Xm\u001d;\u0015\t\u0011]Aq\u0007\u0005\t\ts!\t\u00041\u0001\u0004:\u0006\u0019!/Z9\t\u0011\u0011u\u0002\u0001)C\u0005\t\u007f\tac\u001d9mSRdunY1m%\u0016lw\u000e^3CY>\u001c7n\u001d\u000b\u0003\t\u0003\u0002b!a\u0003\u0002\u0016\re\u0006\u0002\u0003C#\u0001\u0001&I\u0001\"\u0006\u0002!\u0019,Go\u00195M_\u000e\fGN\u00117pG.\u001c\b\u0002\u0003C%\u0001\u0001&I\u0001\"\u0006\u0002\u0015%t\u0017\u000e^5bY&TX\rC\u0004\u0005N\u0001!\tEa9\u0002\u000f!\f7OT3yi\"9A\u0011\u000b\u0001\u0005B\u0011M\u0013\u0001\u00028fqR$\u0012A\b\u0005\b\t/\u0002A\u0011\u0002C\u000b\u0003E1W\r^2i+B$v.T1y\u0005f$Xm\u001d\u0005\b\t7\u0002A\u0011\u0002C/\u0003e!\bN]8x\r\u0016$8\r\u001b$bS2,G-\u0012=dKB$\u0018n\u001c8\u0015\u0011\u0011}CQ\rC4\tS\u00022\u0001\u0004C1\u0013\r!\u0019'\u0004\u0002\b\u001d>$\b.\u001b8h\u0011\u001d\u0011Y\u0005\"\u0017A\u0002\u0005Bq!!\u0018\u0005Z\u0001\u0007\u0011\u000b\u0003\u0005\u0003h\u0011e\u0003\u0019\u0001B6\u0001"
)
public final class ShuffleBlockFetcherIterator implements Iterator<Tuple2<BlockId, InputStream>>, TempFileManager, Logging {
   private final TaskContext context;
   private final ShuffleClient shuffleClient;
   public final BlockManager org$apache$spark$storage$ShuffleBlockFetcherIterator$$blockManager;
   private final Seq<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> blocksByAddress;
   private final Function2<BlockId, InputStream, InputStream> streamWrapper;
   public final long org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBytesInFlight;
   private final int maxReqsInFlight;
   public final int org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBlocksInFlightPerAddress;
   private final long maxReqSizeShuffleToMem;
   private final boolean detectCorrupt;
   public int org$apache$spark$storage$ShuffleBlockFetcherIterator$$numBlocksToFetch;
   private int numBlocksProcessed;
   public final long org$apache$spark$storage$ShuffleBlockFetcherIterator$$startTime;
   public final ArrayBuffer<BlockId> org$apache$spark$storage$ShuffleBlockFetcherIterator$$localBlocks;
   public final HashSet<BlockId> org$apache$spark$storage$ShuffleBlockFetcherIterator$$remoteBlocks;
   public final LinkedBlockingQueue<FetchResult> org$apache$spark$storage$ShuffleBlockFetcherIterator$$results;
   private volatile SuccessFetchResult currentResult;
   private final Queue<FetchRequest> fetchRequests;
   public final HashMap<BlockManagerId, Queue<FetchRequest>> org$apache$spark$storage$ShuffleBlockFetcherIterator$$deferredFetchRequests;
   public long org$apache$spark$storage$ShuffleBlockFetcherIterator$$bytesInFlight;
   public int org$apache$spark$storage$ShuffleBlockFetcherIterator$$reqsInFlight;
   private final HashMap<BlockManagerId, Object> numBlocksInFlightPerAddress;
   private final HashSet<BlockId> corruptedBlocks;
   private final TempShuffleReadMetrics shuffleMetrics;
   @GuardedBy("this")
   public boolean org$apache$spark$storage$ShuffleBlockFetcherIterator$$isZombie;
   @GuardedBy("this")
   private final HashSet<File> shuffleFilesSet;
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

   public Iterator<Tuple2<BlockId, InputStream>> seq() {
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

   public Iterator<Tuple2<BlockId, InputStream>> take(int n) {
      return Iterator$class.take(this, n);
   }

   public Iterator<Tuple2<BlockId, InputStream>> drop(int n) {
      return Iterator$class.drop(this, n);
   }

   public Iterator<Tuple2<BlockId, InputStream>> slice(int from, int until) {
      return Iterator$class.slice(this, from, until);
   }

   public <B> Iterator<B> map(Function1<Tuple2<BlockId, InputStream>, B> f) {
      return Iterator$class.map(this, f);
   }

   public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
      return Iterator$class.$plus$plus(this, that);
   }

   public <B> Iterator<B> flatMap(Function1<Tuple2<BlockId, InputStream>, GenTraversableOnce<B>> f) {
      return Iterator$class.flatMap(this, f);
   }

   public Iterator<Tuple2<BlockId, InputStream>> filter(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.filter(this, p);
   }

   public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<BlockId, InputStream>, B, Object> p) {
      return Iterator$class.corresponds(this, that, p);
   }

   public Iterator<Tuple2<BlockId, InputStream>> withFilter(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.withFilter(this, p);
   }

   public Iterator<Tuple2<BlockId, InputStream>> filterNot(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.filterNot(this, p);
   }

   public <B> Iterator<B> collect(PartialFunction<Tuple2<BlockId, InputStream>, B> pf) {
      return Iterator$class.collect(this, pf);
   }

   public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<BlockId, InputStream>, B> op) {
      return Iterator$class.scanLeft(this, z, op);
   }

   public <B> Iterator<B> scanRight(B z, Function2<Tuple2<BlockId, InputStream>, B, B> op) {
      return Iterator$class.scanRight(this, z, op);
   }

   public Iterator<Tuple2<BlockId, InputStream>> takeWhile(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.takeWhile(this, p);
   }

   public Tuple2<Iterator<Tuple2<BlockId, InputStream>>, Iterator<Tuple2<BlockId, InputStream>>> partition(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.partition(this, p);
   }

   public Tuple2<Iterator<Tuple2<BlockId, InputStream>>, Iterator<Tuple2<BlockId, InputStream>>> span(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.span(this, p);
   }

   public Iterator<Tuple2<BlockId, InputStream>> dropWhile(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.dropWhile(this, p);
   }

   public <B> Iterator<Tuple2<Tuple2<BlockId, InputStream>, B>> zip(Iterator<B> that) {
      return Iterator$class.zip(this, that);
   }

   public <A1> Iterator<A1> padTo(int len, A1 elem) {
      return Iterator$class.padTo(this, len, elem);
   }

   public Iterator<Tuple2<Tuple2<BlockId, InputStream>, Object>> zipWithIndex() {
      return Iterator$class.zipWithIndex(this);
   }

   public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
      return Iterator$class.zipAll(this, that, thisElem, thatElem);
   }

   public <U> void foreach(Function1<Tuple2<BlockId, InputStream>, U> f) {
      Iterator$class.foreach(this, f);
   }

   public boolean forall(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.forall(this, p);
   }

   public boolean exists(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.exists(this, p);
   }

   public boolean contains(Object elem) {
      return Iterator$class.contains(this, elem);
   }

   public Option<Tuple2<BlockId, InputStream>> find(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.find(this, p);
   }

   public int indexWhere(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return Iterator$class.indexWhere(this, p);
   }

   public <B> int indexOf(B elem) {
      return Iterator$class.indexOf(this, elem);
   }

   public BufferedIterator<Tuple2<BlockId, InputStream>> buffered() {
      return Iterator$class.buffered(this);
   }

   public <B> Iterator<Tuple2<BlockId, InputStream>>.GroupedIterator<B> grouped(int size) {
      return Iterator$class.grouped(this, size);
   }

   public <B> Iterator<Tuple2<BlockId, InputStream>>.GroupedIterator<B> sliding(int size, int step) {
      return Iterator$class.sliding(this, size, step);
   }

   public int length() {
      return Iterator$class.length(this);
   }

   public Tuple2<Iterator<Tuple2<BlockId, InputStream>>, Iterator<Tuple2<BlockId, InputStream>>> duplicate() {
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

   public Traversable<Tuple2<BlockId, InputStream>> toTraversable() {
      return Iterator$class.toTraversable(this);
   }

   public Iterator<Tuple2<BlockId, InputStream>> toIterator() {
      return Iterator$class.toIterator(this);
   }

   public Stream<Tuple2<BlockId, InputStream>> toStream() {
      return Iterator$class.toStream(this);
   }

   public String toString() {
      return Iterator$class.toString(this);
   }

   public <B> int sliding$default$2() {
      return Iterator$class.sliding$default$2(this);
   }

   public List<Tuple2<BlockId, InputStream>> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public int size() {
      return TraversableOnce$class.size(this);
   }

   public boolean nonEmpty() {
      return TraversableOnce$class.nonEmpty(this);
   }

   public int count(Function1<Tuple2<BlockId, InputStream>, Object> p) {
      return TraversableOnce$class.count(this, p);
   }

   public <B> Option<B> collectFirst(PartialFunction<Tuple2<BlockId, InputStream>, B> pf) {
      return TraversableOnce$class.collectFirst(this, pf);
   }

   public <B> B $div$colon(B z, Function2<B, Tuple2<BlockId, InputStream>, B> op) {
      return (B)TraversableOnce$class.$div$colon(this, z, op);
   }

   public <B> B $colon$bslash(B z, Function2<Tuple2<BlockId, InputStream>, B, B> op) {
      return (B)TraversableOnce$class.$colon$bslash(this, z, op);
   }

   public <B> B foldLeft(B z, Function2<B, Tuple2<BlockId, InputStream>, B> op) {
      return (B)TraversableOnce$class.foldLeft(this, z, op);
   }

   public <B> B foldRight(B z, Function2<Tuple2<BlockId, InputStream>, B, B> op) {
      return (B)TraversableOnce$class.foldRight(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, Tuple2<BlockId, InputStream>, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> B reduceRight(Function2<Tuple2<BlockId, InputStream>, B, B> op) {
      return (B)TraversableOnce$class.reduceRight(this, op);
   }

   public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<BlockId, InputStream>, B> op) {
      return TraversableOnce$class.reduceLeftOption(this, op);
   }

   public <B> Option<B> reduceRightOption(Function2<Tuple2<BlockId, InputStream>, B, B> op) {
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

   public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<BlockId, InputStream>, B> seqop, Function2<B, B, B> combop) {
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

   public List<Tuple2<BlockId, InputStream>> toList() {
      return TraversableOnce$class.toList(this);
   }

   public Iterable<Tuple2<BlockId, InputStream>> toIterable() {
      return TraversableOnce$class.toIterable(this);
   }

   public Seq<Tuple2<BlockId, InputStream>> toSeq() {
      return TraversableOnce$class.toSeq(this);
   }

   public IndexedSeq<Tuple2<BlockId, InputStream>> toIndexedSeq() {
      return TraversableOnce$class.toIndexedSeq(this);
   }

   public <B> Buffer<B> toBuffer() {
      return TraversableOnce$class.toBuffer(this);
   }

   public <B> Set<B> toSet() {
      return TraversableOnce$class.toSet(this);
   }

   public Vector<Tuple2<BlockId, InputStream>> toVector() {
      return TraversableOnce$class.toVector(this);
   }

   public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<BlockId, InputStream>, Col> cbf) {
      return (Col)TraversableOnce$class.to(this, cbf);
   }

   public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<BlockId, InputStream>, Tuple2<T, U>> ev) {
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

   public void releaseCurrentResultBuffer() {
      if (this.currentResult == null) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      } else {
         this.currentResult.buf().release();
      }

      this.currentResult = null;
   }

   public File createTempFile() {
      return (File)this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$blockManager.diskBlockManager().createTempLocalBlock()._2();
   }

   public synchronized boolean registerTempFileToClean(File file) {
      boolean var10000;
      if (this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$isZombie) {
         var10000 = false;
      } else {
         this.shuffleFilesSet.$plus$eq(file);
         var10000 = true;
      }

      return var10000;
   }

   public void org$apache$spark$storage$ShuffleBlockFetcherIterator$$cleanup() {
      synchronized(this) {
         this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$isZombie = true;
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this.releaseCurrentResultBuffer();

      for(FetchResult result : this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$results) {
         if (result instanceof SuccessFetchResult) {
            ManagedBuffer buf;
            label43: {
               SuccessFetchResult var5 = (SuccessFetchResult)result;
               BlockManagerId address = var5.address();
               buf = var5.buf();
               BlockManagerId var9 = this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$blockManager.blockManagerId();
               if (address == null) {
                  if (var9 == null) {
                     break label43;
                  }
               } else if (address.equals(var9)) {
                  break label43;
               }

               this.shuffleMetrics.incRemoteBytesRead(buf.size());
               if (buf instanceof FileSegmentManagedBuffer) {
                  this.shuffleMetrics.incRemoteBytesReadToDisk(buf.size());
               }

               this.shuffleMetrics.incRemoteBlocksFetched(1L);
            }

            ManagedBuffer var8 = buf.release();
         } else {
            BoxedUnit var11 = BoxedUnit.UNIT;
         }
      }

      this.shuffleFilesSet.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleBlockFetcherIterator $outer;

         public final void apply(File file) {
            if (!file.delete()) {
               this.$outer.logWarning(new Serializable(this, file) {
                  public static final long serialVersionUID = 0L;
                  private final File file$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Failed to cleanup shuffle fetch temp file ").append((Object)this.file$1.getAbsolutePath()).toString();
                  }

                  public {
                     this.file$1 = file$1;
                  }
               });
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

   private void sendRequest(FetchRequest req) {
      this.logDebug(new Serializable(this, req) {
         public static final long serialVersionUID = 0L;
         private final FetchRequest req$1;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Sending request for %d blocks (%s) from %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.req$1.blocks().size()), Utils$.MODULE$.bytesToString(this.req$1.size()), this.req$1.address().hostPort()}));
         }

         public {
            this.req$1 = req$1;
         }
      });
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$bytesInFlight += req.size();
      ++this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$reqsInFlight;
      Map sizeMap = ((TraversableOnce)req.blocks().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<String, Object> apply(Tuple2<BlockId, Object> x0$1) {
            if (x0$1 != null) {
               BlockId blockId = (BlockId)x0$1._1();
               long size = x0$1._2$mcJ$sp();
               Tuple2 var6 = new Tuple2(blockId.toString(), BoxesRunTime.boxToLong(size));
               return var6;
            } else {
               throw new MatchError(x0$1);
            }
         }
      }, Seq$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
      HashSet remainingBlocks = (HashSet)(new HashSet()).$plus$plus$eq(sizeMap.keys());
      Seq blockIds = (Seq)req.blocks().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<BlockId, Object> x$1) {
            return ((BlockId)x$1._1()).toString();
         }
      }, Seq$.MODULE$.canBuildFrom());
      BlockManagerId address = req.address();
      BlockFetchingListener blockFetchingListener = new BlockFetchingListener(this, req, sizeMap, remainingBlocks, address) {
         // $FF: synthetic field
         private final ShuffleBlockFetcherIterator $outer;
         public final FetchRequest req$1;
         private final Map sizeMap$1;
         public final HashSet remainingBlocks$1;
         private final BlockManagerId address$1;

         public void onBlockFetchSuccess(String blockId, ManagedBuffer buf) {
            synchronized(this.$outer) {
               if (this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$isZombie) {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               } else {
                  buf.retain();
                  this.remainingBlocks$1.$minus$eq(blockId);
                  this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$results.put(new SuccessFetchResult(BlockId$.MODULE$.apply(blockId), this.address$1, BoxesRunTime.unboxToLong(this.sizeMap$1.apply(blockId)), buf, this.remainingBlocks$1.isEmpty()));
                  this.$outer.logDebug(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"remainingBlocks: ").append((Object)this.$outer.remainingBlocks$1).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  BoxedUnit var5 = BoxedUnit.UNIT;
               }
            }

            this.$outer.logTrace(new Serializable(this, blockId) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final String blockId$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Got remote block ").append((Object)this.blockId$1).append((Object)" after ").append((Object)Utils$.MODULE$.getUsedTimeMs(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$anon$$$outer().org$apache$spark$storage$ShuffleBlockFetcherIterator$$startTime)).toString();
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

         public void onBlockFetchFailure(String blockId, Throwable e) {
            this.$outer.logError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to get block(s) from ", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.req$1.address().host(), BoxesRunTime.boxToInteger(this.$outer.req$1.address().port())}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, e);
            this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$results.put(new FailureFetchResult(BlockId$.MODULE$.apply(blockId), this.address$1, e));
         }

         // $FF: synthetic method
         public ShuffleBlockFetcherIterator org$apache$spark$storage$ShuffleBlockFetcherIterator$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.req$1 = req$1;
               this.sizeMap$1 = sizeMap$1;
               this.remainingBlocks$1 = remainingBlocks$1;
               this.address$1 = address$1;
            }
         }
      };
      if (req.size() > this.maxReqSizeShuffleToMem) {
         this.shuffleClient.fetchBlocks(address.host(), address.port(), address.executorId(), (String[])blockIds.toArray(ClassTag$.MODULE$.apply(String.class)), blockFetchingListener, this);
      } else {
         this.shuffleClient.fetchBlocks(address.host(), address.port(), address.executorId(), (String[])blockIds.toArray(ClassTag$.MODULE$.apply(String.class)), blockFetchingListener, (TempFileManager)null);
      }

   }

   private ArrayBuffer<FetchRequest> splitLocalRemoteBlocks() {
      long targetRequestSize = package$.MODULE$.max(this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBytesInFlight / 5L, 1L);
      this.logDebug(new Serializable(this, targetRequestSize) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleBlockFetcherIterator $outer;
         private final long targetRequestSize$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"maxBytesInFlight: ").append((Object)BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBytesInFlight)).append((Object)", targetRequestSize: ").append((Object)BoxesRunTime.boxToLong(this.targetRequestSize$1)).append((Object)", maxBlocksInFlightPerAddress: ").append((Object)BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBlocksInFlightPerAddress)).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.targetRequestSize$1 = targetRequestSize$1;
            }
         }
      });
      ArrayBuffer remoteRequests = new ArrayBuffer();
      IntRef totalBlocks = IntRef.create(0);
      this.blocksByAddress.withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(this, targetRequestSize, remoteRequests, totalBlocks) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleBlockFetcherIterator $outer;
         private final long targetRequestSize$1;
         private final ArrayBuffer remoteRequests$1;
         private final IntRef totalBlocks$1;

         public final Object apply(Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>> x$5) {
            if (x$5 == null) {
               throw new MatchError(x$5);
            } else {
               Object var19;
               label49: {
                  Seq blockInfos;
                  label53: {
                     BlockManagerId address = (BlockManagerId)x$5._1();
                     blockInfos = (Seq)x$5._2();
                     this.totalBlocks$1.elem += blockInfos.size();
                     String var10000 = address.executorId();
                     String var6 = this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$blockManager.blockManagerId().executorId();
                     if (var10000 == null) {
                        if (var6 == null) {
                           break label53;
                        }
                     } else if (var10000.equals(var6)) {
                        break label53;
                     }

                     Iterator iterator = blockInfos.iterator();
                     LongRef curRequestSize = LongRef.create(0L);
                     ObjectRef curBlocks = ObjectRef.create(new ArrayBuffer());

                     while(iterator.hasNext()) {
                        Tuple2 var11 = (Tuple2)iterator.next();
                        if (var11 == null) {
                           throw new MatchError(var11);
                        }

                        BlockId blockId = (BlockId)var11._1();
                        long size = var11._2$mcJ$sp();
                        Tuple2 var15 = new Tuple2(blockId, BoxesRunTime.boxToLong(size));
                        BlockId blockId = (BlockId)var15._1();
                        long size = var15._2$mcJ$sp();
                        if (size > 0L) {
                           ((ArrayBuffer)curBlocks.elem).$plus$eq(new Tuple2(blockId, BoxesRunTime.boxToLong(size)));
                           this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$remoteBlocks.$plus$eq(blockId);
                           ++this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$numBlocksToFetch;
                           curRequestSize.elem += size;
                        } else if (size < 0L) {
                           throw new BlockException(blockId, (new StringBuilder()).append((Object)"Negative block size ").append((Object)BoxesRunTime.boxToLong(size)).toString());
                        }

                        if (curRequestSize.elem >= this.targetRequestSize$1 || ((ArrayBuffer)curBlocks.elem).size() >= this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBlocksInFlightPerAddress) {
                           this.remoteRequests$1.$plus$eq(new FetchRequest(address, (ArrayBuffer)curBlocks.elem));
                           this.$outer.logDebug(new Serializable(this, address, curRequestSize, curBlocks) {
                              public static final long serialVersionUID = 0L;
                              private final BlockManagerId address$2;
                              private final LongRef curRequestSize$1;
                              private final ObjectRef curBlocks$1;

                              public final String apply() {
                                 return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Creating fetch request of ", " at ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.curRequestSize$1.elem), this.address$2}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"with ", " blocks"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(((ArrayBuffer)this.curBlocks$1.elem).size())}))).toString();
                              }

                              public {
                                 this.address$2 = address$2;
                                 this.curRequestSize$1 = curRequestSize$1;
                                 this.curBlocks$1 = curBlocks$1;
                              }
                           });
                           curBlocks.elem = (T)(new ArrayBuffer());
                           curRequestSize.elem = 0L;
                        }
                     }

                     var19 = ((ArrayBuffer)curBlocks.elem).nonEmpty() ? this.remoteRequests$1.$plus$eq(new FetchRequest(address, (ArrayBuffer)curBlocks.elem)) : BoxedUnit.UNIT;
                     break label49;
                  }

                  this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$localBlocks.$plus$plus$eq((TraversableOnce)((TraversableLike)blockInfos.filter(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final boolean apply(Tuple2<BlockId, Object> x$2) {
                        return x$2._2$mcJ$sp() != 0L;
                     }
                  })).map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final BlockId apply(Tuple2<BlockId, Object> x$3) {
                        return (BlockId)x$3._1();
                     }
                  }, Seq$.MODULE$.canBuildFrom()));
                  this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$numBlocksToFetch += this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$localBlocks.size();
                  var19 = BoxedUnit.UNIT;
               }

               Object var5 = var19;
               return var5;
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.targetRequestSize$1 = targetRequestSize$1;
               this.remoteRequests$1 = remoteRequests$1;
               this.totalBlocks$1 = totalBlocks$1;
            }
         }
      });
      this.logInfo(new Serializable(this, totalBlocks) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleBlockFetcherIterator $outer;
         private final IntRef totalBlocks$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Getting ", " non-empty blocks out of ", " blocks"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$numBlocksToFetch), BoxesRunTime.boxToInteger(this.totalBlocks$1.elem)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.totalBlocks$1 = totalBlocks$1;
            }
         }
      });
      return remoteRequests;
   }

   private void fetchLocalBlocks() {
      Iterator iter = this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$localBlocks.iterator();

      while(iter.hasNext()) {
         BlockId blockId = (BlockId)iter.next();

         try {
            ManagedBuffer buf = this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$blockManager.getBlockData(blockId);
            this.shuffleMetrics.incLocalBlocksFetched(1L);
            this.shuffleMetrics.incLocalBytesRead(buf.size());
            buf.retain();
            this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$results.put(new SuccessFetchResult(blockId, this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$blockManager.blockManagerId(), 0L, buf, false));
         } catch (Exception var5) {
            this.logError(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error occurred while fetching local blocks"})))).s(Nil$.MODULE$);
               }
            }, var5);
            this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$results.put(new FailureFetchResult(blockId, this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$blockManager.blockManagerId(), var5));
            return;
         }
      }

   }

   private void initialize() {
      this.context.addTaskCompletionListener((Function1)(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleBlockFetcherIterator $outer;

         public final void apply(TaskContext x$6) {
            this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$cleanup();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      ArrayBuffer remoteRequests = this.splitLocalRemoteBlocks();
      this.fetchRequests.$plus$plus$eq(Utils$.MODULE$.randomize(remoteRequests, ClassTag$.MODULE$.apply(FetchRequest.class)));
      Predef$.MODULE$.assert(0 == this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$reqsInFlight == (0L == this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$bytesInFlight), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleBlockFetcherIterator $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"expected reqsInFlight = 0 but found reqsInFlight = ").append((Object)BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$reqsInFlight)).append((Object)", expected bytesInFlight = 0 but found bytesInFlight = ").append((Object)BoxesRunTime.boxToLong(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$bytesInFlight)).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.fetchUpToMaxBytes();
      int numFetches = remoteRequests.size() - this.fetchRequests.size();
      this.logInfo(new Serializable(this, numFetches) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleBlockFetcherIterator $outer;
         private final int numFetches$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Started ").append((Object)BoxesRunTime.boxToInteger(this.numFetches$1)).append((Object)" remote fetches in").append((Object)Utils$.MODULE$.getUsedTimeMs(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$startTime)).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.numFetches$1 = numFetches$1;
            }
         }
      });
      this.fetchLocalBlocks();
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleBlockFetcherIterator $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Got local blocks in ").append((Object)Utils$.MODULE$.getUsedTimeMs(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$startTime)).toString();
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

   public boolean hasNext() {
      return this.numBlocksProcessed < this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$numBlocksToFetch;
   }

   public Tuple2<BlockId, InputStream> next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         ++this.numBlocksProcessed;
         FetchResult result = null;

         InputStream input;
         for(input = null; result == null; this.fetchUpToMaxBytes()) {
            long startFetchWait = System.currentTimeMillis();
            result = (FetchResult)this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$results.take();
            long stopFetchWait = System.currentTimeMillis();
            this.shuffleMetrics.incFetchWaitTime(stopFetchWait - startFetchWait);
            if (!(result instanceof SuccessFetchResult)) {
               if (result instanceof FailureFetchResult) {
                  FailureFetchResult var23 = (FailureFetchResult)result;
                  BlockId blockId = var23.blockId();
                  BlockManagerId address = var23.address();
                  Throwable e = var23.e();
                  throw this.throwFetchFailedException(blockId, address, e);
               }

               throw new MatchError(result);
            }

            BlockId blockId;
            BlockManagerId address;
            long size;
            ManagedBuffer buf;
            boolean isNetworkReqDone;
            label160: {
               SuccessFetchResult var8 = (SuccessFetchResult)result;
               blockId = var8.blockId();
               address = var8.address();
               size = var8.size();
               buf = var8.buf();
               isNetworkReqDone = var8.isNetworkReqDone();
               BlockManagerId var16 = this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$blockManager.blockManagerId();
               if (address == null) {
                  if (var16 == null) {
                     break label160;
                  }
               } else if (address.equals(var16)) {
                  break label160;
               }

               this.numBlocksInFlightPerAddress.update(address, BoxesRunTime.boxToInteger(BoxesRunTime.unboxToInt(this.numBlocksInFlightPerAddress.apply(address)) - 1));
               this.shuffleMetrics.incRemoteBytesRead(buf.size());
               if (buf instanceof FileSegmentManagedBuffer) {
                  this.shuffleMetrics.incRemoteBytesReadToDisk(buf.size());
               }

               this.shuffleMetrics.incRemoteBlocksFetched(1L);
            }

            this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$bytesInFlight -= size;
            if (isNetworkReqDone) {
               --this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$reqsInFlight;
               this.logDebug(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final ShuffleBlockFetcherIterator $outer;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Number of requests in flight ").append((Object)BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$reqsInFlight)).toString();
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

            InputStream var10000;
            try {
               var10000 = buf.createInputStream();
            } catch (IOException var30) {
               Predef$.MODULE$.assert(buf instanceof FileSegmentManagedBuffer);
               this.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Failed to create input stream from local block";
                  }
               }, var30);
               buf.release();
               throw this.throwFetchFailedException(blockId, address, var30);
            }

            InputStream in = var10000;
            input = this.streamWrapper.apply(blockId, in);
            if (this.detectCorrupt && input != in && size < this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBytesInFlight / 3L) {
               InputStream originalInput = input;
               ChunkedByteBufferOutputStream out = new ChunkedByteBufferOutputStream(65536, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final ByteBuffer apply(int x$1) {
                     return ByteBuffer.allocate(x$1);
                  }
               });

               try {
                  Utils$.MODULE$.copyStream(input, out, Utils$.MODULE$.copyStream$default$3(), Utils$.MODULE$.copyStream$default$4());
                  out.close();
                  input = out.toChunkedByteBuffer().toInputStream(true);
                  BoxedUnit var35 = BoxedUnit.UNIT;
               } catch (IOException var31) {
                  buf.release();
                  if (buf instanceof FileSegmentManagedBuffer || this.corruptedBlocks.contains(blockId)) {
                     throw this.throwFetchFailedException(blockId, address, var31);
                  }

                  this.logWarning(new Serializable(this, blockId, address) {
                     public static final long serialVersionUID = 0L;
                     private final BlockId blockId$2;
                     private final BlockManagerId address$3;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"got an corrupted block ", " from ", ", fetch again"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.blockId$2, this.address$3}));
                     }

                     public {
                        this.blockId$2 = blockId$2;
                        this.address$3 = address$3;
                     }
                  }, var31);
                  this.corruptedBlocks.$plus$eq(blockId);
                  this.fetchRequests.$plus$eq(new FetchRequest(address, Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2(blockId, BoxesRunTime.boxToLong(size))})));
                  result = null;
                  BoxedUnit var34 = BoxedUnit.UNIT;
               } finally {
                  originalInput.close();
                  in.close();
               }
            } else {
               BoxedUnit var33 = BoxedUnit.UNIT;
            }
         }

         this.currentResult = (SuccessFetchResult)result;
         return new Tuple2<BlockId, InputStream>(this.currentResult.blockId(), new BufferReleasingInputStream(input, this));
      }
   }

   private void fetchUpToMaxBytes() {
      if (this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$deferredFetchRequests.nonEmpty()) {
         this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$deferredFetchRequests.withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Tuple2<BlockManagerId, Queue<FetchRequest>> check$ifrefutable$2) {
               boolean var3;
               if (check$ifrefutable$2 != null) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }
         }).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ShuffleBlockFetcherIterator $outer;

            public final void apply(Tuple2<BlockManagerId, Queue<FetchRequest>> x$7) {
               if (x$7 == null) {
                  throw new MatchError(x$7);
               } else {
                  BlockManagerId remoteAddress = (BlockManagerId)x$7._1();
                  Queue defReqQueue = (Queue)x$7._2();

                  while(this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$isRemoteBlockFetchable$1(defReqQueue) && !this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$isRemoteAddressMaxedOut$1(remoteAddress, (FetchRequest)defReqQueue.front())) {
                     FetchRequest request = (FetchRequest)defReqQueue.dequeue();
                     this.$outer.logDebug(new Serializable(this, remoteAddress, request) {
                        public static final long serialVersionUID = 0L;
                        private final BlockManagerId remoteAddress$1;
                        private final FetchRequest request$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Processing deferred fetch request for ", " with "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.remoteAddress$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " blocks"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.request$1.blocks().length())}))).toString();
                        }

                        public {
                           this.remoteAddress$1 = remoteAddress$1;
                           this.request$1 = request$1;
                        }
                     });
                     this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$send$1(remoteAddress, request);
                     if (defReqQueue.isEmpty()) {
                        this.$outer.org$apache$spark$storage$ShuffleBlockFetcherIterator$$deferredFetchRequests.$minus$eq(remoteAddress);
                     } else {
                        BoxedUnit var10000 = BoxedUnit.UNIT;
                     }
                  }

                  BoxedUnit var5 = BoxedUnit.UNIT;
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

      while(this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$isRemoteBlockFetchable$1(this.fetchRequests)) {
         FetchRequest request = this.fetchRequests.dequeue();
         BlockManagerId remoteAddress = request.address();
         if (this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$isRemoteAddressMaxedOut$1(remoteAddress, request)) {
            this.logDebug(new Serializable(this, request, remoteAddress) {
               public static final long serialVersionUID = 0L;
               private final FetchRequest request$2;
               private final BlockManagerId remoteAddress$2;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Deferring fetch request for ", " with ", " blocks"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.remoteAddress$2, BoxesRunTime.boxToInteger(this.request$2.blocks().size())}));
               }

               public {
                  this.request$2 = request$2;
                  this.remoteAddress$2 = remoteAddress$2;
               }
            });
            Queue defReqQueue = (Queue)this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$deferredFetchRequests.getOrElse(remoteAddress, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Queue<FetchRequest> apply() {
                  return new Queue<FetchRequest>();
               }
            });
            defReqQueue.enqueue(Predef$.MODULE$.wrapRefArray(new FetchRequest[]{request}));
            this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$deferredFetchRequests.update(remoteAddress, defReqQueue);
         } else {
            this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$send$1(remoteAddress, request);
         }
      }

   }

   private Nothing$ throwFetchFailedException(BlockId blockId, BlockManagerId address, Throwable e) {
      if (blockId instanceof ShuffleBlockId) {
         ShuffleBlockId var5 = (ShuffleBlockId)blockId;
         int shufId = var5.shuffleId();
         int mapId = var5.mapId();
         int reduceId = var5.reduceId();
         throw new FetchFailedException(address, shufId, mapId, reduceId, e);
      } else {
         throw new SparkException((new StringBuilder()).append((Object)"Failed to get block ").append((Object)blockId).append((Object)", which is not a shuffle block").toString(), e);
      }
   }

   public final void org$apache$spark$storage$ShuffleBlockFetcherIterator$$send$1(BlockManagerId remoteAddress, FetchRequest request) {
      this.sendRequest(request);
      this.numBlocksInFlightPerAddress.update(remoteAddress, BoxesRunTime.boxToInteger(BoxesRunTime.unboxToInt(this.numBlocksInFlightPerAddress.getOrElse(remoteAddress, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      })) + request.blocks().size()));
   }

   public final boolean org$apache$spark$storage$ShuffleBlockFetcherIterator$$isRemoteBlockFetchable$1(Queue fetchReqQueue) {
      return fetchReqQueue.nonEmpty() && (this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$bytesInFlight == 0L || this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$reqsInFlight + 1 <= this.maxReqsInFlight && this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$bytesInFlight + ((FetchRequest)fetchReqQueue.front()).size() <= this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBytesInFlight);
   }

   public final boolean org$apache$spark$storage$ShuffleBlockFetcherIterator$$isRemoteAddressMaxedOut$1(BlockManagerId remoteAddress, FetchRequest request) {
      return BoxesRunTime.unboxToInt(this.numBlocksInFlightPerAddress.getOrElse(remoteAddress, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      })) + request.blocks().size() > this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBlocksInFlightPerAddress;
   }

   public ShuffleBlockFetcherIterator(TaskContext context, ShuffleClient shuffleClient, BlockManager blockManager, Seq<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> blocksByAddress, Function2<BlockId, InputStream, InputStream> streamWrapper, long maxBytesInFlight, int maxReqsInFlight, int maxBlocksInFlightPerAddress, long maxReqSizeShuffleToMem, boolean detectCorrupt) {
      this.context = context;
      this.shuffleClient = shuffleClient;
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$blockManager = blockManager;
      this.blocksByAddress = blocksByAddress;
      this.streamWrapper = streamWrapper;
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBytesInFlight = maxBytesInFlight;
      this.maxReqsInFlight = maxReqsInFlight;
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$maxBlocksInFlightPerAddress = maxBlocksInFlightPerAddress;
      this.maxReqSizeShuffleToMem = maxReqSizeShuffleToMem;
      this.detectCorrupt = detectCorrupt;
      TraversableOnce$class.$init$(this);
      Iterator$class.$init$(this);
      Logging$class.$init$(this);
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$numBlocksToFetch = 0;
      this.numBlocksProcessed = 0;
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$startTime = System.currentTimeMillis();
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$localBlocks = new ArrayBuffer<BlockId>();
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$remoteBlocks = new HashSet<BlockId>();
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$results = new LinkedBlockingQueue();
      this.currentResult = null;
      this.fetchRequests = new Queue<FetchRequest>();
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$deferredFetchRequests = new HashMap<BlockManagerId, Queue<FetchRequest>>();
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$bytesInFlight = 0L;
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$reqsInFlight = 0;
      this.numBlocksInFlightPerAddress = new HashMap<BlockManagerId, Object>();
      this.corruptedBlocks = (HashSet)HashSet$.MODULE$.apply(Nil$.MODULE$);
      this.shuffleMetrics = context.taskMetrics().createTempShuffleReadMetrics();
      this.org$apache$spark$storage$ShuffleBlockFetcherIterator$$isZombie = false;
      this.shuffleFilesSet = (HashSet)HashSet$.MODULE$.apply(Nil$.MODULE$);
      this.initialize();
   }

   public static class FetchRequest implements Product, Serializable {
      private final BlockManagerId address;
      private final Seq<Tuple2<BlockId, Object>> blocks;
      private final long size;

      public BlockManagerId address() {
         return this.address;
      }

      public Seq<Tuple2<BlockId, Object>> blocks() {
         return this.blocks;
      }

      public long size() {
         return this.size;
      }

      public FetchRequest copy(BlockManagerId address, Seq<Tuple2<BlockId, Object>> blocks) {
         return new FetchRequest(address, blocks);
      }

      public BlockManagerId copy$default$1() {
         return this.address();
      }

      public Seq<Tuple2<BlockId, Object>> copy$default$2() {
         return this.blocks();
      }

      public String productPrefix() {
         return "FetchRequest";
      }

      public int productArity() {
         return 2;
      }

      public Object productElement(int x$1) {
         Object var10000;
         switch (x$1) {
            case 0:
               var10000 = this.address();
               break;
            case 1:
               var10000 = this.blocks();
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
         return x$1 instanceof FetchRequest;
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
            label62: {
               boolean var3;
               if (x$1 instanceof FetchRequest) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label44: {
                     label53: {
                        FetchRequest var4 = (FetchRequest)x$1;
                        BlockManagerId var10000 = this.address();
                        BlockManagerId var5 = var4.address();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label53;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label53;
                        }

                        Seq var7 = this.blocks();
                        Seq var6 = var4.blocks();
                        if (var7 == null) {
                           if (var6 != null) {
                              break label53;
                           }
                        } else if (!var7.equals(var6)) {
                           break label53;
                        }

                        if (var4.canEqual(this)) {
                           var9 = true;
                           break label44;
                        }
                     }

                     var9 = false;
                  }

                  if (var9) {
                     break label62;
                  }
               }

               var9 = false;
               return var9;
            }
         }

         var9 = true;
         return var9;
      }

      public FetchRequest(BlockManagerId address, Seq<Tuple2<BlockId, Object>> blocks) {
         this.address = address;
         this.blocks = blocks;
         Product$class.$init$(this);
         this.size = BoxesRunTime.unboxToLong(((TraversableOnce)blocks.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(Tuple2<BlockId, Object> x$8) {
               return x$8._2$mcJ$sp();
            }
         }, Seq$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
      }
   }

   public static class FetchRequest$ extends AbstractFunction2<BlockManagerId, Seq<Tuple2<BlockId, Object>>, FetchRequest> implements Serializable {
      public static final FetchRequest$ MODULE$;

      static {
         new FetchRequest$();
      }

      public final String toString() {
         return "FetchRequest";
      }

      public FetchRequest apply(BlockManagerId address, Seq<Tuple2<BlockId, Object>> blocks) {
         return new FetchRequest(address, blocks);
      }

      public Option<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> unapply(FetchRequest x$0) {
         return (Option<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.address(), x$0.blocks())));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public FetchRequest$() {
         MODULE$ = this;
      }
   }

   public static class SuccessFetchResult$ extends AbstractFunction5<BlockId, BlockManagerId, Object, ManagedBuffer, Object, SuccessFetchResult> implements Serializable {
      public static final SuccessFetchResult$ MODULE$;

      static {
         new SuccessFetchResult$();
      }

      public final String toString() {
         return "SuccessFetchResult";
      }

      public SuccessFetchResult apply(BlockId blockId, BlockManagerId address, long size, ManagedBuffer buf, boolean isNetworkReqDone) {
         return new SuccessFetchResult(blockId, address, size, buf, isNetworkReqDone);
      }

      public Option<Tuple5<BlockId, BlockManagerId, Object, ManagedBuffer, Object>> unapply(SuccessFetchResult x$0) {
         return (Option<Tuple5<BlockId, BlockManagerId, Object, ManagedBuffer, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(x$0.blockId(), x$0.address(), BoxesRunTime.boxToLong(x$0.size()), x$0.buf(), BoxesRunTime.boxToBoolean(x$0.isNetworkReqDone()))));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public SuccessFetchResult$() {
         MODULE$ = this;
      }
   }

   public static class SuccessFetchResult implements FetchResult, Product, Serializable {
      private final BlockId blockId;
      private final BlockManagerId address;
      private final long size;
      private final ManagedBuffer buf;
      private final boolean isNetworkReqDone;

      public BlockId blockId() {
         return this.blockId;
      }

      public BlockManagerId address() {
         return this.address;
      }

      public long size() {
         return this.size;
      }

      public ManagedBuffer buf() {
         return this.buf;
      }

      public boolean isNetworkReqDone() {
         return this.isNetworkReqDone;
      }

      public SuccessFetchResult copy(BlockId blockId, BlockManagerId address, long size, ManagedBuffer buf, boolean isNetworkReqDone) {
         return new SuccessFetchResult(blockId, address, size, buf, isNetworkReqDone);
      }

      public BlockId copy$default$1() {
         return this.blockId();
      }

      public BlockManagerId copy$default$2() {
         return this.address();
      }

      public long copy$default$3() {
         return this.size();
      }

      public ManagedBuffer copy$default$4() {
         return this.buf();
      }

      public boolean copy$default$5() {
         return this.isNetworkReqDone();
      }

      public String productPrefix() {
         return "SuccessFetchResult";
      }

      public int productArity() {
         return 5;
      }

      public Object productElement(int x$1) {
         Object var10000;
         switch (x$1) {
            case 0:
               var10000 = this.blockId();
               break;
            case 1:
               var10000 = this.address();
               break;
            case 2:
               var10000 = BoxesRunTime.boxToLong(this.size());
               break;
            case 3:
               var10000 = this.buf();
               break;
            case 4:
               var10000 = BoxesRunTime.boxToBoolean(this.isNetworkReqDone());
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
         return x$1 instanceof SuccessFetchResult;
      }

      public int hashCode() {
         int var1 = -889275714;
         var1 = Statics.mix(var1, Statics.anyHash(this.blockId()));
         var1 = Statics.mix(var1, Statics.anyHash(this.address()));
         var1 = Statics.mix(var1, Statics.longHash(this.size()));
         var1 = Statics.mix(var1, Statics.anyHash(this.buf()));
         var1 = Statics.mix(var1, this.isNetworkReqDone() ? 1231 : 1237);
         return Statics.finalizeHash(var1, 5);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var11;
         if (this != x$1) {
            label75: {
               boolean var3;
               if (x$1 instanceof SuccessFetchResult) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label57: {
                     label66: {
                        SuccessFetchResult var4 = (SuccessFetchResult)x$1;
                        BlockId var10000 = this.blockId();
                        BlockId var5 = var4.blockId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label66;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label66;
                        }

                        BlockManagerId var8 = this.address();
                        BlockManagerId var6 = var4.address();
                        if (var8 == null) {
                           if (var6 != null) {
                              break label66;
                           }
                        } else if (!var8.equals(var6)) {
                           break label66;
                        }

                        if (this.size() == var4.size()) {
                           label45: {
                              ManagedBuffer var9 = this.buf();
                              ManagedBuffer var7 = var4.buf();
                              if (var9 == null) {
                                 if (var7 != null) {
                                    break label45;
                                 }
                              } else if (!var9.equals(var7)) {
                                 break label45;
                              }

                              if (this.isNetworkReqDone() == var4.isNetworkReqDone() && var4.canEqual(this)) {
                                 var11 = true;
                                 break label57;
                              }
                           }
                        }
                     }

                     var11 = false;
                  }

                  if (var11) {
                     break label75;
                  }
               }

               var11 = false;
               return var11;
            }
         }

         var11 = true;
         return var11;
      }

      public SuccessFetchResult(BlockId blockId, BlockManagerId address, long size, ManagedBuffer buf, boolean isNetworkReqDone) {
         this.blockId = blockId;
         this.address = address;
         this.size = size;
         this.buf = buf;
         this.isNetworkReqDone = isNetworkReqDone;
         Product$class.$init$(this);
         Predef$.MODULE$.require(buf != null);
         Predef$.MODULE$.require(size >= 0L);
      }
   }

   public static class FailureFetchResult$ extends AbstractFunction3<BlockId, BlockManagerId, Throwable, FailureFetchResult> implements Serializable {
      public static final FailureFetchResult$ MODULE$;

      static {
         new FailureFetchResult$();
      }

      public final String toString() {
         return "FailureFetchResult";
      }

      public FailureFetchResult apply(BlockId blockId, BlockManagerId address, Throwable e) {
         return new FailureFetchResult(blockId, address, e);
      }

      public Option<Tuple3<BlockId, BlockManagerId, Throwable>> unapply(FailureFetchResult x$0) {
         return (Option<Tuple3<BlockId, BlockManagerId, Throwable>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.blockId(), x$0.address(), x$0.e())));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public FailureFetchResult$() {
         MODULE$ = this;
      }
   }

   public static class FailureFetchResult implements FetchResult, Product, Serializable {
      private final BlockId blockId;
      private final BlockManagerId address;
      private final Throwable e;

      public BlockId blockId() {
         return this.blockId;
      }

      public BlockManagerId address() {
         return this.address;
      }

      public Throwable e() {
         return this.e;
      }

      public FailureFetchResult copy(BlockId blockId, BlockManagerId address, Throwable e) {
         return new FailureFetchResult(blockId, address, e);
      }

      public BlockId copy$default$1() {
         return this.blockId();
      }

      public BlockManagerId copy$default$2() {
         return this.address();
      }

      public Throwable copy$default$3() {
         return this.e();
      }

      public String productPrefix() {
         return "FailureFetchResult";
      }

      public int productArity() {
         return 3;
      }

      public Object productElement(int x$1) {
         Object var10000;
         switch (x$1) {
            case 0:
               var10000 = this.blockId();
               break;
            case 1:
               var10000 = this.address();
               break;
            case 2:
               var10000 = this.e();
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
         return x$1 instanceof FailureFetchResult;
      }

      public int hashCode() {
         return ScalaRunTime$.MODULE$._hashCode(this);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var11;
         if (this != x$1) {
            label71: {
               boolean var3;
               if (x$1 instanceof FailureFetchResult) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label53: {
                     label62: {
                        FailureFetchResult var4 = (FailureFetchResult)x$1;
                        BlockId var10000 = this.blockId();
                        BlockId var5 = var4.blockId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label62;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label62;
                        }

                        BlockManagerId var8 = this.address();
                        BlockManagerId var6 = var4.address();
                        if (var8 == null) {
                           if (var6 != null) {
                              break label62;
                           }
                        } else if (!var8.equals(var6)) {
                           break label62;
                        }

                        Throwable var9 = this.e();
                        Throwable var7 = var4.e();
                        if (var9 == null) {
                           if (var7 != null) {
                              break label62;
                           }
                        } else if (!var9.equals(var7)) {
                           break label62;
                        }

                        if (var4.canEqual(this)) {
                           var11 = true;
                           break label53;
                        }
                     }

                     var11 = false;
                  }

                  if (var11) {
                     break label71;
                  }
               }

               var11 = false;
               return var11;
            }
         }

         var11 = true;
         return var11;
      }

      public FailureFetchResult(BlockId blockId, BlockManagerId address, Throwable e) {
         this.blockId = blockId;
         this.address = address;
         this.e = e;
         Product$class.$init$(this);
      }
   }

   public interface FetchResult {
      BlockId blockId();

      BlockManagerId address();
   }
}
