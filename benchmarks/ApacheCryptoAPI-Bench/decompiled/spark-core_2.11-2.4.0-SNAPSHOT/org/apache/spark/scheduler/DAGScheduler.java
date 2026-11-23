package org.apache.spark.scheduler;

import java.io.NotSerializableException;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.spark.Dependency;
import org.apache.spark.ExceptionFailure;
import org.apache.spark.ExecutorLostFailure;
import org.apache.spark.FetchFailed;
import org.apache.spark.MapOutputStatistics;
import org.apache.spark.MapOutputTrackerMaster;
import org.apache.spark.NarrowDependency;
import org.apache.spark.Partition;
import org.apache.spark.Resubmitted$;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkContext$;
import org.apache.spark.SparkDriverExecutionException;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.Success$;
import org.apache.spark.TaskCommitDenied;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskEndReason;
import org.apache.spark.TaskKilled;
import org.apache.spark.TaskResultLost$;
import org.apache.spark.UnknownReason$;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.executor.TaskMetrics$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.util.JavaUtils;
import org.apache.spark.partial.ApproximateActionListener;
import org.apache.spark.partial.ApproximateEvaluator;
import org.apache.spark.partial.PartialResult;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rpc.RpcTimeout;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.storage.BlockManagerMaster;
import org.apache.spark.storage.BlockManagerMessages;
import org.apache.spark.storage.RDDBlockId;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.storage.StorageLevel$;
import org.apache.spark.util.AccumulatorContext$;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.CallSite;
import org.apache.spark.util.Clock;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
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
import scala.Tuple4;
import scala.collection.GenTraversableOnce;
import scala.collection.IndexedSeq;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SetLike;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayStack;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.HashSet$;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.duration.Duration$;
import scala.math.Ordering;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BooleanRef;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.Nothing$;
import scala.runtime.Null$;
import scala.runtime.ObjectRef;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d\rf!B\u0001\u0003\u0001\u0011Q!\u0001\u0004#B\u000fN\u001b\u0007.\u001a3vY\u0016\u0014(BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\"\u0005\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I)R\"A\n\u000b\u0005Q!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005Y\u0019\"a\u0002'pO\u001eLgn\u001a\u0005\n1\u0001\u0011)\u0019!C\u0001\u0005e\t!a]2\u0016\u0003i\u0001\"a\u0007\u000f\u000e\u0003\u0011I!!\b\u0003\u0003\u0019M\u0003\u0018M]6D_:$X\r\u001f;\u0004\u0001!A\u0001\u0005\u0001B\u0001B\u0003%!$A\u0002tG\u0002B\u0011B\t\u0001\u0003\u0006\u0004%\tAA\u0012\u0002\u001bQ\f7o[*dQ\u0016$W\u000f\\3s+\u0005!\u0003CA\u0013'\u001b\u0005\u0011\u0011BA\u0014\u0003\u00055!\u0016m]6TG\",G-\u001e7fe\"A\u0011\u0006\u0001B\u0001B\u0003%A%\u0001\buCN\\7k\u00195fIVdWM\u001d\u0011\t\u0011-\u0002!\u0011!Q\u0001\n1\n1\u0002\\5ti\u0016tWM\u001d\"vgB\u0011Q%L\u0005\u0003]\t\u0011q\u0002T5wK2K7\u000f^3oKJ\u0014Uo\u001d\u0005\ta\u0001\u0011\t\u0011)A\u0005c\u0005\u0001R.\u00199PkR\u0004X\u000f\u001e+sC\u000e\\WM\u001d\t\u00037IJ!a\r\u0003\u0003-5\u000b\u0007oT;uaV$HK]1dW\u0016\u0014X*Y:uKJD\u0001\"\u000e\u0001\u0003\u0002\u0003\u0006IAN\u0001\u0013E2|7m['b]\u0006<WM]'bgR,'\u000f\u0005\u00028u5\t\u0001H\u0003\u0002:\t\u000591\u000f^8sC\u001e,\u0017BA\u001e9\u0005I\u0011En\\2l\u001b\u0006t\u0017mZ3s\u001b\u0006\u001cH/\u001a:\t\u0011u\u0002!\u0011!Q\u0001\ny\n1!\u001a8w!\tYr(\u0003\u0002A\t\tA1\u000b]1sW\u0016sg\u000f\u0003\u0005C\u0001\t\u0005\t\u0015!\u0003D\u0003\u0015\u0019Gn\\2l!\t!u)D\u0001F\u0015\t1E!\u0001\u0003vi&d\u0017B\u0001%F\u0005\u0015\u0019En\\2l\u0011\u0015Q\u0005\u0001\"\u0001L\u0003\u0019a\u0014N\\5u}QAA*\u0014(P!F\u00136\u000b\u0005\u0002&\u0001!)\u0001$\u0013a\u00015!)!%\u0013a\u0001I!)1&\u0013a\u0001Y!)\u0001'\u0013a\u0001c!)Q'\u0013a\u0001m!)Q(\u0013a\u0001}!9!)\u0013I\u0001\u0002\u0004\u0019\u0005\"\u0002&\u0001\t\u0003)Fc\u0001'W/\")\u0001\u0004\u0016a\u00015!)!\u0005\u0016a\u0001I!)!\n\u0001C\u00013R\u0011AJ\u0017\u0005\u00061a\u0003\rA\u0007\u0005\t9\u0002\u0011\r\u0011\"\u0001\u0005;\u0006iQ.\u001a;sS\u000e\u001c8k\\;sG\u0016,\u0012A\u0018\t\u0003K}K!\u0001\u0019\u0002\u0003%\u0011\u000buiU2iK\u0012,H.\u001a:T_V\u00148-\u001a\u0005\u0007E\u0002\u0001\u000b\u0011\u00020\u0002\u001d5,GO]5dgN{WO]2fA!AA\r\u0001b\u0001\n\u0003\u0011Q-A\u0005oKb$(j\u001c2JIV\ta\r\u0005\u0002h_6\t\u0001N\u0003\u0002jU\u00061\u0011\r^8nS\u000eT!a\u001b7\u0002\u0015\r|gnY;se\u0016tGO\u0003\u0002G[*\ta.\u0001\u0003kCZ\f\u0017B\u00019i\u00055\tEo\\7jG&sG/Z4fe\"1!\u000f\u0001Q\u0001\n\u0019\f!B\\3yi*{'-\u00133!\u0011\u0019!\b\u0001\"\u0001\u0003k\u0006aa.^7U_R\fGNS8cgV\ta\u000f\u0005\u0002\ro&\u0011\u00010\u0004\u0002\u0004\u0013:$\bb\u0002>\u0001\u0005\u0004%I!Z\u0001\f]\u0016DHo\u0015;bO\u0016LE\r\u0003\u0004}\u0001\u0001\u0006IAZ\u0001\r]\u0016DHo\u0015;bO\u0016LE\r\t\u0005\t}\u0002\u0011\r\u0011\"\u0001\u0003\u007f\u0006y!n\u001c2JIR{7\u000b^1hK&#7/\u0006\u0002\u0002\u0002A9\u00111AA\u0007m\u0006EQBAA\u0003\u0015\u0011\t9!!\u0003\u0002\u000f5,H/\u00192mK*\u0019\u00111B\u0007\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0002\u0010\u0005\u0015!a\u0002%bg\"l\u0015\r\u001d\t\u0006\u0003\u0007\t\u0019B^\u0005\u0005\u0003+\t)AA\u0004ICND7+\u001a;\t\u0011\u0005e\u0001\u0001)A\u0005\u0003\u0003\t\u0001C[8c\u0013\u0012$vn\u0015;bO\u0016LEm\u001d\u0011\t\u0015\u0005u\u0001A1A\u0005\u0002\t\ty\"\u0001\bti\u0006<W-\u00133U_N#\u0018mZ3\u0016\u0005\u0005\u0005\u0002cBA\u0002\u0003\u001b1\u00181\u0005\t\u0004K\u0005\u0015\u0012bAA\u0014\u0005\t)1\u000b^1hK\"A\u00111\u0006\u0001!\u0002\u0013\t\t#A\bti\u0006<W-\u00133U_N#\u0018mZ3!\u0011)\ty\u0003\u0001b\u0001\n\u0003\u0011\u0011\u0011G\u0001\u0014g\",hM\u001a7f\u0013\u0012$v.T1q'R\fw-Z\u000b\u0003\u0003g\u0001r!a\u0001\u0002\u000eY\f)\u0004E\u0002&\u0003oI1!!\u000f\u0003\u0005=\u0019\u0006.\u001e4gY\u0016l\u0015\r]*uC\u001e,\u0007\u0002CA\u001f\u0001\u0001\u0006I!a\r\u0002)MDWO\u001a4mK&#Gk\\'baN#\u0018mZ3!\u0011)\t\t\u0005\u0001b\u0001\n\u0003\u0011\u00111I\u0001\u0011U>\u0014\u0017\n\u001a+p\u0003\u000e$\u0018N^3K_\n,\"!!\u0012\u0011\u000f\u0005\r\u0011Q\u0002<\u0002HA\u0019Q%!\u0013\n\u0007\u0005-#AA\u0005BGRLg/\u001a&pE\"A\u0011q\n\u0001!\u0002\u0013\t)%A\tk_\nLE\rV8BGRLg/\u001a&pE\u0002B!\"a\u0015\u0001\u0005\u0004%\tAAA+\u000359\u0018-\u001b;j]\u001e\u001cF/Y4fgV\u0011\u0011q\u000b\t\u0007\u0003\u0007\t\u0019\"a\t\t\u0011\u0005m\u0003\u0001)A\u0005\u0003/\nab^1ji&twm\u0015;bO\u0016\u001c\b\u0005\u0003\u0006\u0002`\u0001\u0011\r\u0011\"\u0001\u0003\u0003+\nQB];o]&twm\u0015;bO\u0016\u001c\b\u0002CA2\u0001\u0001\u0006I!a\u0016\u0002\u001dI,hN\\5oON#\u0018mZ3tA!Q\u0011q\r\u0001C\u0002\u0013\u0005!!!\u0016\u0002\u0019\u0019\f\u0017\u000e\\3e'R\fw-Z:\t\u0011\u0005-\u0004\u0001)A\u0005\u0003/\nQBZ1jY\u0016$7\u000b^1hKN\u0004\u0003BCA8\u0001\t\u0007I\u0011\u0001\u0002\u0002r\u0005Q\u0011m\u0019;jm\u0016TuNY:\u0016\u0005\u0005M\u0004CBA\u0002\u0003'\t9\u0005\u0003\u0005\u0002x\u0001\u0001\u000b\u0011BA:\u0003-\t7\r^5wK*{'m\u001d\u0011\t\u0013\u0005m\u0004A1A\u0005\n\u0005u\u0014!C2bG\",Gj\\2t+\t\ty\bE\u0004\u0002\u0004\u00055a/!!\u0011\r\u0005\r\u00151SAM\u001d\u0011\t))a$\u000f\t\u0005\u001d\u0015QR\u0007\u0003\u0003\u0013S1!a#\u001f\u0003\u0019a$o\\8u}%\ta\"C\u0002\u0002\u00126\tq\u0001]1dW\u0006<W-\u0003\u0003\u0002\u0016\u0006]%AC%oI\u0016DX\rZ*fc*\u0019\u0011\u0011S\u0007\u0011\r\u0005\r\u00151TAP\u0013\u0011\ti*a&\u0003\u0007M+\u0017\u000fE\u0002&\u0003CK1!a)\u0003\u00051!\u0016m]6M_\u000e\fG/[8o\u0011!\t9\u000b\u0001Q\u0001\n\u0005}\u0014AC2bG\",Gj\\2tA!I\u00111\u0016\u0001C\u0002\u0013%\u0011QV\u0001\fM\u0006LG.\u001a3Fa>\u001c\u0007.\u0006\u0002\u00020BA\u00111AA\u0007\u0003c\u000by\f\u0005\u0003\u00024\u0006efb\u0001\u0007\u00026&\u0019\u0011qW\u0007\u0002\rA\u0013X\rZ3g\u0013\u0011\tY,!0\u0003\rM#(/\u001b8h\u0015\r\t9,\u0004\t\u0004\u0019\u0005\u0005\u0017bAAb\u001b\t!Aj\u001c8h\u0011!\t9\r\u0001Q\u0001\n\u0005=\u0016\u0001\u00044bS2,G-\u00129pG\"\u0004\u0003BCAf\u0001\t\u0007I\u0011\u0001\u0002\u0002N\u00069r.\u001e;qkR\u001cu.\\7ji\u000e{wN\u001d3j]\u0006$xN]\u000b\u0003\u0003\u001f\u00042!JAi\u0013\r\t\u0019N\u0001\u0002\u0018\u001fV$\b/\u001e;D_6l\u0017\u000e^\"p_J$\u0017N\\1u_JD\u0001\"a6\u0001A\u0003%\u0011qZ\u0001\u0019_V$\b/\u001e;D_6l\u0017\u000e^\"p_J$\u0017N\\1u_J\u0004\u0003\"CAn\u0001\t\u0007I\u0011BAo\u0003E\u0019Gn\\:ve\u0016\u001cVM]5bY&TXM]\u000b\u0003\u0003?\u0004B!!9\u0002h6\u0011\u00111\u001d\u0006\u0004\u0003K$\u0011AC:fe&\fG.\u001b>fe&!\u0011\u0011^Ar\u0005I\u0019VM]5bY&TXM]%ogR\fgnY3\t\u0011\u00055\b\u0001)A\u0005\u0003?\f!c\u00197pgV\u0014XmU3sS\u0006d\u0017N_3sA!I\u0011\u0011\u001f\u0001C\u0002\u0013%\u00111_\u0001\u001aI&\u001c\u0018\r\u001c7poN#\u0018mZ3SKR\u0014\u0018PR8s)\u0016\u001cH/\u0006\u0002\u0002vB\u0019A\"a>\n\u0007\u0005eXBA\u0004C_>dW-\u00198\t\u0011\u0005u\b\u0001)A\u0005\u0003k\f!\u0004Z5tC2dwn^*uC\u001e,'+\u001a;ss\u001a{'\u000fV3ti\u0002B!B!\u0001\u0001\u0005\u0004%\tAAAz\u0003\u0011*hNU3hSN$XM](viB,Ho\u00148I_N$xJ\u001c$fi\u000eDg)Y5mkJ,\u0007\u0002\u0003B\u0003\u0001\u0001\u0006I!!>\u0002KUt'+Z4jgR,'oT;uaV$xJ\u001c%pgR|eNR3uG\"4\u0015-\u001b7ve\u0016\u0004\u0003\"\u0003B\u0005\u0001\t\u0007I\u0011\u0001\u0002v\u0003mi\u0017\r_\"p]N,7-\u001e;jm\u0016\u001cF/Y4f\u0003R$X-\u001c9ug\"9!Q\u0002\u0001!\u0002\u00131\u0018\u0001H7bq\u000e{gn]3dkRLg/Z*uC\u001e,\u0017\t\u001e;f[B$8\u000f\t\u0005\n\u0005#\u0001!\u0019!C\u0005\u0005'\t\u0001#\\3tg\u0006<WmU2iK\u0012,H.\u001a:\u0016\u0005\tU\u0001\u0003\u0002B\f\u00053i\u0011A[\u0005\u0004\u00057Q'\u0001G*dQ\u0016$W\u000f\\3e\u000bb,7-\u001e;peN+'O^5dK\"A!q\u0004\u0001!\u0002\u0013\u0011)\"A\tnKN\u001c\u0018mZ3TG\",G-\u001e7fe\u0002B!Ba\t\u0001\u0005\u0004%\tA\u0001B\u0013\u0003A)g/\u001a8u!J|7-Z:t\u0019>|\u0007/\u0006\u0002\u0003(A\u0019QE!\u000b\n\u0007\t-\"A\u0001\u000fE\u0003\u001e\u001b6\r[3ek2,'/\u0012<f]R\u0004&o\\2fgNdun\u001c9\t\u0011\t=\u0002\u0001)A\u0005\u0005O\t\u0011#\u001a<f]R\u0004&o\\2fgNdun\u001c9!\u0011\u001d\u0011\u0019\u0004\u0001C\u0001\u0005k\t1\u0002^1tWN#\u0018M\u001d;fIR1!q\u0007B\u001f\u0005?\u00022\u0001\u0004B\u001d\u0013\r\u0011Y$\u0004\u0002\u0005+:LG\u000f\u0003\u0005\u0003@\tE\u0002\u0019\u0001B!\u0003\u0011!\u0018m]61\t\t\r#Q\n\t\u0006K\t\u0015#\u0011J\u0005\u0004\u0005\u000f\u0012!\u0001\u0002+bg.\u0004BAa\u0013\u0003N1\u0001A\u0001\u0004B(\u0005{\t\t\u0011!A\u0003\u0002\tE#aA0%cE!!1\u000bB-!\ra!QK\u0005\u0004\u0005/j!a\u0002(pi\"Lgn\u001a\t\u0004\u0019\tm\u0013b\u0001B/\u001b\t\u0019\u0011I\\=\t\u0011\t\u0005$\u0011\u0007a\u0001\u0005G\n\u0001\u0002^1tW&sgm\u001c\t\u0004K\t\u0015\u0014b\u0001B4\u0005\tAA+Y:l\u0013:4w\u000eC\u0004\u0003l\u0001!\tA!\u001c\u0002#Q\f7o[$fiRLgn\u001a*fgVdG\u000f\u0006\u0003\u00038\t=\u0004\u0002\u0003B1\u0005S\u0002\rAa\u0019\t\u000f\tM\u0004\u0001\"\u0001\u0003v\u0005IA/Y:l\u000b:$W\r\u001a\u000b\r\u0005o\u00119Ha!\u0003\u000e\nE%1\u0016\u0005\t\u0005\u007f\u0011\t\b1\u0001\u0003zA\"!1\u0010B@!\u0015)#Q\tB?!\u0011\u0011YEa \u0005\u0019\t\u0005%qOA\u0001\u0002\u0003\u0015\tA!\u0015\u0003\u0007}##\u0007\u0003\u0005\u0003\u0006\nE\u0004\u0019\u0001BD\u0003\u0019\u0011X-Y:p]B\u00191D!#\n\u0007\t-EAA\u0007UCN\\WI\u001c3SK\u0006\u001cxN\u001c\u0005\t\u0005\u001f\u0013\t\b1\u0001\u0003Z\u00051!/Z:vYRD\u0001Ba%\u0003r\u0001\u0007!QS\u0001\rC\u000e\u001cW/\\+qI\u0006$Xm\u001d\t\u0007\u0003\u0007\u000bYJa&1\r\te%\u0011\u0015BT!\u001d!%1\u0014BP\u0005KK1A!(F\u00055\t5mY;nk2\fGo\u001c:WeA!!1\nBQ\t1\u0011\u0019K!%\u0002\u0002\u0003\u0005)\u0011\u0001B)\u0005\ryFe\r\t\u0005\u0005\u0017\u00129\u000b\u0002\u0007\u0003*\nE\u0015\u0011!A\u0001\u0006\u0003\u0011\tFA\u0002`IQB\u0001B!\u0019\u0003r\u0001\u0007!1\r\u0005\b\u0005_\u0003A\u0011\u0001BY\u0003e)\u00070Z2vi>\u0014\b*Z1si\n,\u0017\r\u001e*fG\u0016Lg/\u001a3\u0015\u0011\u0005U(1\u0017B\\\u0005\u001bD\u0001B!.\u0003.\u0002\u0007\u0011\u0011W\u0001\u0007Kb,7-\u00133\t\u0011\tM%Q\u0016a\u0001\u0005s\u0003R\u0001\u0004B^\u0005\u007fK1A!0\u000e\u0005\u0015\t%O]1z!%a!\u0011YA`mZ\u0014)-C\u0002\u0003D6\u0011a\u0001V;qY\u0016$\u0004CBAB\u00037\u00139\rE\u0002&\u0005\u0013L1Aa3\u0003\u0005=\t5mY;nk2\f'\r\\3J]\u001a|\u0007\u0002\u0003Bh\u0005[\u0003\rA!5\u0002\u001d\tdwnY6NC:\fw-\u001a:JIB\u0019qGa5\n\u0007\tU\u0007H\u0001\bCY>\u001c7.T1oC\u001e,'/\u00133\t\u000f\te\u0007\u0001\"\u0001\u0003\\\u0006aQ\r_3dkR|'\u000fT8tiR1!q\u0007Bo\u0005?D\u0001B!.\u0003X\u0002\u0007\u0011\u0011\u0017\u0005\t\u0005\u000b\u00139\u000e1\u0001\u0003bB\u0019QEa9\n\u0007\t\u0015(A\u0001\nFq\u0016\u001cW\u000f^8s\u0019>\u001c8OU3bg>t\u0007b\u0002Bu\u0001\u0011\u0005!1^\u0001\u000eo>\u00148.\u001a:SK6|g/\u001a3\u0015\u0011\t]\"Q\u001eBy\u0005kD\u0001Ba<\u0003h\u0002\u0007\u0011\u0011W\u0001\to>\u00148.\u001a:JI\"A!1\u001fBt\u0001\u0004\t\t,\u0001\u0003i_N$\b\u0002\u0003B|\u0005O\u0004\r!!-\u0002\u000f5,7o]1hK\"9!1 \u0001\u0005\u0002\tu\u0018!D3yK\u000e,Ho\u001c:BI\u0012,G\r\u0006\u0004\u00038\t}8\u0011\u0001\u0005\t\u0005k\u0013I\u00101\u0001\u00022\"A!1\u001fB}\u0001\u0004\t\t\fC\u0004\u0004\u0006\u0001!\taa\u0002\u0002\u001bQ\f7o[*fi\u001a\u000b\u0017\u000e\\3e)!\u00119d!\u0003\u0004\u0014\rU\u0001\u0002CB\u0006\u0007\u0007\u0001\ra!\u0004\u0002\u000fQ\f7o[*fiB\u0019Qea\u0004\n\u0007\rE!AA\u0004UCN\\7+\u001a;\t\u0011\t\u001551\u0001a\u0001\u0003cC\u0001ba\u0006\u0004\u0004\u0001\u00071\u0011D\u0001\nKb\u001cW\r\u001d;j_:\u0004R\u0001DB\u000e\u0007?I1a!\b\u000e\u0005\u0019y\u0005\u000f^5p]B!\u00111QB\u0011\u0013\u0011\u0019\u0019#a&\u0003\u0013QC'o\\<bE2,\u0007bBB\u0014\u0001\u0011\u00051\u0011F\u0001\u0019gB,7-\u001e7bi&4X\rV1tWN+(-\\5ui\u0016$G\u0003\u0002B\u001c\u0007WA\u0001Ba\u0010\u0004&\u0001\u00071Q\u0006\u0019\u0005\u0007_\u0019\u0019\u0004E\u0003&\u0005\u000b\u001a\t\u0004\u0005\u0003\u0003L\rMB\u0001DB\u001b\u0007W\t\t\u0011!A\u0003\u0002\tE#aA0%k!A1\u0011\b\u0001\u0005\u0002\t\u0019Y$\u0001\u0007hKR\u001c\u0015m\u00195f\u0019>\u001c7\u000f\u0006\u0003\u0002\u0002\u000eu\u0002\u0002CB \u0007o\u0001\ra!\u0011\u0002\u0007I$G\r\r\u0003\u0004D\r=\u0003CBB#\u0007\u0013\u001ai%\u0004\u0002\u0004H)\u00191q\b\u0003\n\t\r-3q\t\u0002\u0004%\u0012#\u0005\u0003\u0002B&\u0007\u001f\"Ab!\u0015\u0004>\u0005\u0005\t\u0011!B\u0001\u0005#\u00121a\u0018\u00137\u0011\u001d\u0019)\u0006\u0001C\u0005\u0007/\nab\u00197fCJ\u001c\u0015m\u00195f\u0019>\u001c7\u000f\u0006\u0002\u00038!911\f\u0001\u0005\n\ru\u0013AG4fi>\u00138I]3bi\u0016\u001c\u0006.\u001e4gY\u0016l\u0015\r]*uC\u001e,GCBA\u001b\u0007?\u001ai\b\u0003\u0005\u0004b\re\u0003\u0019AB2\u0003)\u0019\b.\u001e4gY\u0016$U\r\u001d\u0019\t\u0007K\u001aiga\u001d\u0004zAI1da\u001a\u0004l\rE4qO\u0005\u0004\u0007S\"!!E*ik\u001a4G.\u001a#fa\u0016tG-\u001a8dsB!!1JB7\t1\u0019yga\u0018\u0002\u0002\u0003\u0005)\u0011\u0001B)\u0005\ryFe\u000e\t\u0005\u0005\u0017\u001a\u0019\b\u0002\u0007\u0004v\r}\u0013\u0011!A\u0001\u0006\u0003\u0011\tFA\u0002`Ia\u0002BAa\u0013\u0004z\u0011a11PB0\u0003\u0003\u0005\tQ!\u0001\u0003R\t\u0019q\fJ\u001d\t\u000f\r}4\u0011\fa\u0001m\u0006Qa-\u001b:ti*{'-\u00133\t\u000f\r\r\u0005\u0001\"\u0001\u0004\u0006\u0006)2M]3bi\u0016\u001c\u0006.\u001e4gY\u0016l\u0015\r]*uC\u001e,GCBA\u001b\u0007\u000f\u001by\n\u0003\u0005\u0004b\r\u0005\u0005\u0019ABEa!\u0019Yia$\u0004\u0016\u000em\u0005#C\u000e\u0004h\r551SBM!\u0011\u0011Yea$\u0005\u0019\rE5qQA\u0001\u0002\u0003\u0015\tA!\u0015\u0003\t}#\u0013\u0007\r\t\u0005\u0005\u0017\u001a)\n\u0002\u0007\u0004\u0018\u000e\u001d\u0015\u0011!A\u0001\u0006\u0003\u0011\tF\u0001\u0003`IE\n\u0004\u0003\u0002B&\u00077#Ab!(\u0004\b\u0006\u0005\t\u0011!B\u0001\u0005#\u0012Aa\u0018\u00132e!91\u0011UBA\u0001\u00041\u0018!\u00026pE&#\u0007bBBS\u0001\u0011%1qU\u0001\u0012GJ,\u0017\r^3SKN,H\u000e^*uC\u001e,G\u0003DBU\u0007_\u001bYl!9\u0004h\u000e%\bcA\u0013\u0004,&\u00191Q\u0016\u0002\u0003\u0017I+7/\u001e7u'R\fw-\u001a\u0005\t\u0007\u007f\u0019\u0019\u000b1\u0001\u00042B\"11WB\\!\u0019\u0019)e!\u0013\u00046B!!1JB\\\t1\u0019Ila,\u0002\u0002\u0003\u0005)\u0011\u0001B)\u0005\u0011yF%M\u001a\t\u0011\ru61\u0015a\u0001\u0007\u007f\u000bAAZ;oGB\"1\u0011YBo!%a11YBd\u0007\u001b\u001cY.C\u0002\u0004F6\u0011\u0011BR;oGRLwN\u001c\u001a\u0011\u0007m\u0019I-C\u0002\u0004L\u0012\u00111\u0002V1tW\u000e{g\u000e^3yiB\"1qZBl!\u0019\t\u0019i!5\u0004V&!11[AL\u0005!IE/\u001a:bi>\u0014\b\u0003\u0002B&\u0007/$Ab!7\u0004<\u0006\u0005\t\u0011!B\u0001\u0005#\u0012Aa\u0018\u00132iA!!1JBo\t1\u0019yna/\u0002\u0002\u0003\u0005)\u0011\u0001B)\u0005\u0011yF%M\u001b\t\u0011\r\r81\u0015a\u0001\u0007K\f!\u0002]1si&$\u0018n\u001c8t!\u0011a!1\u0018<\t\u000f\r\u000561\u0015a\u0001m\"A11^BR\u0001\u0004\u0019i/\u0001\u0005dC2d7+\u001b;f!\r!5q^\u0005\u0004\u0007c,%\u0001C\"bY2\u001c\u0016\u000e^3\t\u000f\rU\b\u0001\"\u0003\u0004x\u00069r-\u001a;Pe\u000e\u0013X-\u0019;f!\u0006\u0014XM\u001c;Ti\u0006<Wm\u001d\u000b\u0007\u0007s\u001cy\u0010b\u0003\u0011\r\u0005\r51`A\u0012\u0013\u0011\u0019i0a&\u0003\t1K7\u000f\u001e\u0005\t\u0007\u007f\u0019\u0019\u00101\u0001\u0005\u0002A\"A1\u0001C\u0004!\u0019\u0019)e!\u0013\u0005\u0006A!!1\nC\u0004\t1!Iaa@\u0002\u0002\u0003\u0005)\u0011\u0001B)\u0005\u0011yF%\r\u001c\t\u000f\r}41\u001fa\u0001m\"9Aq\u0002\u0001\u0005\n\u0011E\u0011!J4fi6K7o]5oO\u0006s7-Z:u_J\u001c\u0006.\u001e4gY\u0016$U\r]3oI\u0016t7-[3t)\u0011!\u0019\u0002b\f\u0011\r\u0005\rAQ\u0003C\r\u0013\u0011!9\"!\u0002\u0003\u0015\u0005\u0013(/Y=Ti\u0006\u001c7\u000e\r\u0005\u0005\u001c\u0011}AQ\u0005C\u0016!%Y2q\rC\u000f\tG!I\u0003\u0005\u0003\u0003L\u0011}A\u0001\u0004C\u0011\t\u001b\t\t\u0011!A\u0003\u0002\tE#\u0001B0%ca\u0002BAa\u0013\u0005&\u0011aAq\u0005C\u0007\u0003\u0003\u0005\tQ!\u0001\u0003R\t!q\fJ\u0019:!\u0011\u0011Y\u0005b\u000b\u0005\u0019\u00115BQBA\u0001\u0002\u0003\u0015\tA!\u0015\u0003\t}##\u0007\r\u0005\t\u0007\u007f!i\u00011\u0001\u00052A\"A1\u0007C\u001c!\u0019\u0019)e!\u0013\u00056A!!1\nC\u001c\t1!I\u0004b\f\u0002\u0002\u0003\u0005)\u0011\u0001B)\u0005\u0011yF%M\u001c\t\u0011\u0011u\u0002\u0001\"\u0001\u0003\t\u007f\tacZ3u'\",hM\u001a7f\t\u0016\u0004XM\u001c3f]\u000eLWm\u001d\u000b\u0005\t\u0003\"I\u0006\u0005\u0004\u0002\u0004\u0005MA1\t\u0019\t\t\u000b\"I\u0005b\u0014\u0005VAI1da\u001a\u0005H\u00115C1\u000b\t\u0005\u0005\u0017\"I\u0005\u0002\u0007\u0005L\u0011m\u0012\u0011!A\u0001\u0006\u0003\u0011\tF\u0001\u0003`II:\u0004\u0003\u0002B&\t\u001f\"A\u0002\"\u0015\u0005<\u0005\u0005\t\u0011!B\u0001\u0005#\u0012Aa\u0018\u00133qA!!1\nC+\t1!9\u0006b\u000f\u0002\u0002\u0003\u0005)\u0011\u0001B)\u0005\u0011yFEM\u001d\t\u0011\r}B1\ba\u0001\t7\u0002D\u0001\"\u0018\u0005bA11QIB%\t?\u0002BAa\u0013\u0005b\u0011aA1\rC-\u0003\u0003\u0005\tQ!\u0001\u0003R\t!q\f\n\u001a7\u0011\u001d!9\u0007\u0001C\u0005\tS\nacZ3u\u001b&\u001c8/\u001b8h!\u0006\u0014XM\u001c;Ti\u0006<Wm\u001d\u000b\u0005\u0007s$Y\u0007\u0003\u0005\u0005n\u0011\u0015\u0004\u0019AA\u0012\u0003\u0015\u0019H/Y4f\u0011\u001d!\t\b\u0001C\u0005\tg\na#\u001e9eCR,'j\u001c2JIN#\u0018mZ3JI6\u000b\u0007o\u001d\u000b\u0007\u0005o!)\bb\u001e\t\u000f\r\u0005Fq\u000ea\u0001m\"AAQ\u000eC8\u0001\u0004\t\u0019\u0003C\u0004\u0005|\u0001!I\u0001\" \u0002M\rdW-\u00198vaN#\u0018\r^3G_JTuNY!oI&sG-\u001a9f]\u0012,g\u000e^*uC\u001e,7\u000f\u0006\u0003\u00038\u0011}\u0004\u0002\u0003CA\ts\u0002\r!a\u0012\u0002\u0007)|'\rC\u0004\u0005\u0006\u0002!\t\u0001b\"\u0002\u0013M,(-\\5u\u0015>\u0014WC\u0002CE\t;#\u0019\n\u0006\b\u0005\f\u0012]E\u0011\u0015CT\tW#i\u000bb-\u0011\u000b\u0015\"i\t\"%\n\u0007\u0011=%AA\u0005K_\n<\u0016-\u001b;feB!!1\nCJ\t!!)\nb!C\u0002\tE#!A+\t\u0011\r}B1\u0011a\u0001\t3\u0003ba!\u0012\u0004J\u0011m\u0005\u0003\u0002B&\t;#\u0001\u0002b(\u0005\u0004\n\u0007!\u0011\u000b\u0002\u0002)\"A1Q\u0018CB\u0001\u0004!\u0019\u000bE\u0005\r\u0007\u0007\u001c9\r\"*\u0005\u0012B1\u00111QBi\t7C\u0001ba9\u0005\u0004\u0002\u0007A\u0011\u0016\t\u0006\u0003\u0007\u000bYJ\u001e\u0005\t\u0007W$\u0019\t1\u0001\u0004n\"AAq\u0016CB\u0001\u0004!\t,A\u0007sKN,H\u000e\u001e%b]\u0012dWM\u001d\t\t\u0019\r\rg\u000f\"%\u00038!AAQ\u0017CB\u0001\u0004!9,\u0001\u0006qe>\u0004XM\u001d;jKN\u0004B\u0001\"/\u0005<6\tA.C\u0002\u0005>2\u0014!\u0002\u0015:pa\u0016\u0014H/[3t\u0011\u001d!\t\r\u0001C\u0001\t\u0007\faA];o\u0015>\u0014WC\u0002Cc\t\u001b$9\u000e\u0006\b\u00038\u0011\u001dGq\u001aCm\t7$i\u000e\"9\t\u0011\r}Bq\u0018a\u0001\t\u0013\u0004ba!\u0012\u0004J\u0011-\u0007\u0003\u0002B&\t\u001b$\u0001\u0002b(\u0005@\n\u0007!\u0011\u000b\u0005\t\u0007{#y\f1\u0001\u0005RBIAba1\u0004H\u0012MGQ\u001b\t\u0007\u0003\u0007\u001b\t\u000eb3\u0011\t\t-Cq\u001b\u0003\t\t+#yL1\u0001\u0003R!A11\u001dC`\u0001\u0004!I\u000b\u0003\u0005\u0004l\u0012}\u0006\u0019ABw\u0011!!y\u000bb0A\u0002\u0011}\u0007\u0003\u0003\u0007\u0004DZ$)Na\u000e\t\u0011\u0011UFq\u0018a\u0001\toCq\u0001\":\u0001\t\u0003!9/A\tsk:\f\u0005\u000f\u001d:pq&l\u0017\r^3K_\n,\u0002\u0002\";\u0006\u0004\u00155A\u0011 \u000b\u000f\tW$i0\"\u0002\u0006\u0010\u0015eQ1DC\u0010!\u0019!i\u000fb=\u0005x6\u0011Aq\u001e\u0006\u0004\tc$\u0011a\u00029beRL\u0017\r\\\u0005\u0005\tk$yOA\u0007QCJ$\u0018.\u00197SKN,H\u000e\u001e\t\u0005\u0005\u0017\"I\u0010\u0002\u0005\u0005|\u0012\r(\u0019\u0001B)\u0005\u0005\u0011\u0006\u0002CB \tG\u0004\r\u0001b@\u0011\r\r\u00153\u0011JC\u0001!\u0011\u0011Y%b\u0001\u0005\u0011\u0011}E1\u001db\u0001\u0005#B\u0001b!0\u0005d\u0002\u0007Qq\u0001\t\n\u0019\r\r7qYC\u0005\u000b\u0017\u0001b!a!\u0004R\u0016\u0005\u0001\u0003\u0002B&\u000b\u001b!\u0001\u0002\"&\u0005d\n\u0007!\u0011\u000b\u0005\t\u000b#!\u0019\u000f1\u0001\u0006\u0014\u0005IQM^1mk\u0006$xN\u001d\t\t\t[,)\"b\u0003\u0005x&!Qq\u0003Cx\u0005Q\t\u0005\u000f\u001d:pq&l\u0017\r^3Fm\u0006dW/\u0019;pe\"A11\u001eCr\u0001\u0004\u0019i\u000f\u0003\u0005\u0006\u001e\u0011\r\b\u0019AA`\u0003\u001d!\u0018.\\3pkRD\u0001\u0002\".\u0005d\u0002\u0007Aq\u0017\u0005\b\u000bG\u0001A\u0011AC\u0013\u00039\u0019XOY7ji6\u000b\u0007o\u0015;bO\u0016,\u0002\"b\n\u0006:\u0015}RQ\t\u000b\u000b\u000bS)\t$\"\u0013\u0006T\u0015U\u0003#B\u0013\u0005\u000e\u0016-\u0002cA\u000e\u0006.%\u0019Qq\u0006\u0003\u0003'5\u000b\u0007oT;uaV$8\u000b^1uSN$\u0018nY:\t\u0011\u0015MR\u0011\u0005a\u0001\u000bk\t!\u0002Z3qK:$WM\\2z!%Y2qMC\u001c\u000b{)\u0019\u0005\u0005\u0003\u0003L\u0015eB\u0001CC\u001e\u000bC\u0011\rA!\u0015\u0003\u0003-\u0003BAa\u0013\u0006@\u0011AQ\u0011IC\u0011\u0005\u0004\u0011\tFA\u0001W!\u0011\u0011Y%\"\u0012\u0005\u0011\u0015\u001dS\u0011\u0005b\u0001\u0005#\u0012\u0011a\u0011\u0005\t\u000b\u0017*\t\u00031\u0001\u0006N\u0005A1-\u00197mE\u0006\u001c7\u000eE\u0004\r\u000b\u001f*YCa\u000e\n\u0007\u0015ESBA\u0005Gk:\u001cG/[8oc!A11^C\u0011\u0001\u0004\u0019i\u000f\u0003\u0005\u00056\u0016\u0005\u0002\u0019\u0001C\\\u0011\u001d)I\u0006\u0001C\u0001\u000b7\n\u0011bY1oG\u0016d'j\u001c2\u0015\r\t]RQLC0\u0011\u001d\u0019\t+b\u0016A\u0002YD\u0001B!\"\u0006X\u0001\u0007Q\u0011\r\t\u0006\u0019\rm\u0011\u0011\u0017\u0005\b\u000bK\u0002A\u0011AC4\u00039\u0019\u0017M\\2fY*{'m\u0012:pkB$BAa\u000e\u0006j!AQ1NC2\u0001\u0004\t\t,A\u0004he>,\b/\u00133\t\u000f\u0015=\u0004\u0001\"\u0001\u0004X\u0005i1-\u00198dK2\fE\u000e\u001c&pEND\u0001\"b\u001d\u0001\t\u0003\u00111qK\u0001\u0010I>\u001c\u0015M\\2fY\u0006cGNS8cg\"9Qq\u000f\u0001\u0005\u0002\u0015e\u0014aC2b]\u000e,Gn\u0015;bO\u0016$bAa\u000e\u0006|\u0015}\u0004bBC?\u000bk\u0002\rA^\u0001\bgR\fw-Z%e\u0011!\u0011))\"\u001eA\u0002\u0015\u0005\u0004bBCB\u0001\u0011\u0005QQQ\u0001\u0010W&dG\u000eV1tW\u0006#H/Z7qiRA\u0011Q_CD\u000b\u0017+y\t\u0003\u0005\u0006\n\u0016\u0005\u0005\u0019AA`\u0003\u0019!\u0018m]6JI\"AQQRCA\u0001\u0004\t)0A\bj]R,'O];qiRC'/Z1e\u0011!\u0011))\"!A\u0002\u0005E\u0006\u0002CCJ\u0001\u0011\u0005!aa\u0016\u0002)I,7/\u001e2nSR4\u0015-\u001b7fIN#\u0018mZ3t\u0011\u001d)9\n\u0001C\u0005\u000b3\u000b\u0001d];c[&$x+Y5uS:<7\t[5mIN#\u0018mZ3t)\u0011\u00119$b'\t\u0011\u0015uUQ\u0013a\u0001\u0003G\ta\u0001]1sK:$\bbBCQ\u0001\u0011%Q1U\u0001\u0012C\u000e$\u0018N^3K_\n4uN]*uC\u001e,G\u0003BCS\u000bO\u0003B\u0001DB\u000em\"AAQNCP\u0001\u0004\t\u0019\u0003\u0003\u0005\u0006,\u0002!\tAACW\u0003]A\u0017M\u001c3mK*{'m\u0012:pkB\u001c\u0015M\\2fY2,G\r\u0006\u0003\u00038\u0015=\u0006\u0002CC6\u000bS\u0003\r!!-\t\u0011\u0015M\u0006\u0001\"\u0001\u0003\u000bk\u000b\u0001\u0003[1oI2,')Z4j]\u00163XM\u001c;\u0015\r\t]RqWCb\u0011!\u0011y$\"-A\u0002\u0015e\u0006\u0007BC^\u000b\u007f\u0003R!\nB#\u000b{\u0003BAa\u0013\u0006@\u0012aQ\u0011YC\\\u0003\u0003\u0005\tQ!\u0001\u0003R\t!q\f\n\u001b3\u0011!\u0011\t'\"-A\u0002\t\r\u0004\u0002CCd\u0001\u0011\u0005!!\"3\u0002=!\fg\u000e\u001a7f'B,7-\u001e7bi&4X\rV1tWN+(-\\5ui\u0016$G\u0003\u0002B\u001c\u000b\u0017D\u0001Ba\u0010\u0006F\u0002\u0007QQ\u001a\u0019\u0005\u000b\u001f,\u0019\u000eE\u0003&\u0005\u000b*\t\u000e\u0005\u0003\u0003L\u0015MG\u0001DCk\u000b\u0017\f\t\u0011!A\u0003\u0002\tE#\u0001B0%iMB\u0001\"\"7\u0001\t\u0003\u0011Q1\\\u0001\u0014Q\u0006tG\r\\3UCN\\7+\u001a;GC&dW\r\u001a\u000b\t\u0005o)i.b8\u0006b\"A11BCl\u0001\u0004\u0019i\u0001\u0003\u0005\u0003\u0006\u0016]\u0007\u0019AAY\u0011!\u00199\"b6A\u0002\re\u0001\u0002CCs\u0001\u0011\u0005!aa\u0016\u00023\rdW-\u00198Va\u00063G/\u001a:TG\",G-\u001e7feN#x\u000e\u001d\u0005\t\u000bS\u0004A\u0011\u0001\u0002\u0006l\u0006\u0019\u0002.\u00198eY\u0016<U\r\u001e+bg.\u0014Vm];miR!!qGCw\u0011!\u0011\t'b:A\u0002\t\r\u0004\u0002CCy\u0001\u0011\u0005!!b=\u0002%!\fg\u000e\u001a7f\u0015>\u00147+\u001e2nSR$X\r\u001a\u000b\u0011\u0005o))0b>\u0007\u0006\u0019maQ\u0004D\u0010\rSAqa!)\u0006p\u0002\u0007a\u000f\u0003\u0005\u0006z\u0016=\b\u0019AC~\u0003!1\u0017N\\1m%\u0012#\u0005\u0007BC\u007f\r\u0003\u0001ba!\u0012\u0004J\u0015}\b\u0003\u0002B&\r\u0003!ABb\u0001\u0006x\u0006\u0005\t\u0011!B\u0001\u0005#\u0012Aa\u0018\u00135i!A1QXCx\u0001\u000419\u0001\r\u0003\u0007\n\u0019]\u0001#\u0003\u0007\u0004D\u000e\u001dg1\u0002D\u000ba\u00111iA\"\u0005\u0011\r\u0005\r5\u0011\u001bD\b!\u0011\u0011YE\"\u0005\u0005\u0019\u0019MaQAA\u0001\u0002\u0003\u0015\tA!\u0015\u0003\t}#C'\u000e\t\u0005\u0005\u001729\u0002\u0002\u0007\u0007\u001a\u0019\u0015\u0011\u0011!A\u0001\u0006\u0003\u0011\tF\u0001\u0003`IQ2\u0004\u0002CBr\u000b_\u0004\ra!:\t\u0011\r-Xq\u001ea\u0001\u0007[D\u0001B\"\t\u0006p\u0002\u0007a1E\u0001\tY&\u001cH/\u001a8feB\u0019QE\"\n\n\u0007\u0019\u001d\"AA\u0006K_\nd\u0015n\u001d;f]\u0016\u0014\b\u0002\u0003C[\u000b_\u0004\r\u0001b.\t\u0011\u00195\u0002\u0001\"\u0001\u0003\r_\tq\u0003[1oI2,W*\u00199Ti\u0006<WmU;c[&$H/\u001a3\u0015\u0019\t]b\u0011\u0007D\u001a\r\u00172iEb\u0014\t\u000f\r\u0005f1\u0006a\u0001m\"AQ1\u0007D\u0016\u0001\u00041)\u0004\r\u0005\u00078\u0019mb\u0011\tD$!%Y2q\rD\u001d\r\u007f1)\u0005\u0005\u0003\u0003L\u0019mB\u0001\u0004D\u001f\rg\t\t\u0011!A\u0003\u0002\tE#\u0001B0%i]\u0002BAa\u0013\u0007B\u0011aa1\tD\u001a\u0003\u0003\u0005\tQ!\u0001\u0003R\t!q\f\n\u001b9!\u0011\u0011YEb\u0012\u0005\u0019\u0019%c1GA\u0001\u0002\u0003\u0015\tA!\u0015\u0003\t}#C'\u000f\u0005\t\u0007W4Y\u00031\u0001\u0004n\"Aa\u0011\u0005D\u0016\u0001\u00041\u0019\u0003\u0003\u0005\u00056\u001a-\u0002\u0019\u0001C\\\u0011\u001d1\u0019\u0006\u0001C\u0005\r+\n1b];c[&$8\u000b^1hKR!!q\u0007D,\u0011!!iG\"\u0015A\u0002\u0005\r\u0002b\u0002D.\u0001\u0011%aQL\u0001\u0013gV\u0014W.\u001b;NSN\u001c\u0018N\\4UCN\\7\u000f\u0006\u0004\u00038\u0019}c\u0011\r\u0005\t\t[2I\u00061\u0001\u0002$!91\u0011\u0015D-\u0001\u00041\bb\u0002D3\u0001\u0011%aqM\u0001\u0013kB$\u0017\r^3BG\u000e,X.\u001e7bi>\u00148\u000f\u0006\u0003\u00038\u0019%\u0004\u0002\u0003D6\rG\u0002\rA\"\u001c\u0002\u000b\u00154XM\u001c;\u0011\u0007\u00152y'C\u0002\u0007r\t\u0011qbQ8na2,G/[8o\u000bZ,g\u000e\u001e\u0005\b\rk\u0002A\u0011\u0002D<\u0003-\u0001xn\u001d;UCN\\WI\u001c3\u0015\t\t]b\u0011\u0010\u0005\t\rW2\u0019\b1\u0001\u0007n!AaQ\u0010\u0001\u0005\u0002\t1y(\u0001\u000biC:$G.\u001a+bg.\u001cu.\u001c9mKRLwN\u001c\u000b\u0005\u0005o1\t\t\u0003\u0005\u0007l\u0019m\u0004\u0019\u0001D7\u0011!1)\t\u0001C\u0001\u0005\u0019\u001d\u0015A\u00055b]\u0012dW-\u0012=fGV$xN\u001d'pgR$bAa\u000e\u0007\n\u001a-\u0005\u0002\u0003B[\r\u0007\u0003\r!!-\t\u0011\u00195e1\u0011a\u0001\u0003k\f!b^8sW\u0016\u0014Hj\\:u\u0011\u001d1\t\n\u0001C\u0005\r'\u000b!E]3n_Z,W\t_3dkR|'/\u00118e+:\u0014XmZ5ti\u0016\u0014x*\u001e;qkR\u001cHC\u0003B\u001c\r+39Jb'\u0007 \"A!Q\u0017DH\u0001\u0004\t\t\f\u0003\u0005\u0007\u001a\u001a=\u0005\u0019AA{\u0003!1\u0017\u000e\\3M_N$\b\u0002\u0003DO\r\u001f\u0003\r!\"\u0019\u0002/!|7\u000f\u001e+p+:\u0014XmZ5ti\u0016\u0014x*\u001e;qkR\u001c\bB\u0003DQ\r\u001f\u0003\n\u00111\u0001\u0007$\u0006QQ.Y=cK\u0016\u0003xn\u00195\u0011\u000b1\u0019Y\"a0\t\u0011\u0019\u001d\u0006\u0001\"\u0001\u0003\rS\u000b1\u0003[1oI2,wk\u001c:lKJ\u0014V-\\8wK\u0012$\u0002Ba\u000e\u0007,\u001a5fq\u0016\u0005\t\u0005_4)\u000b1\u0001\u00022\"A!1\u001fDS\u0001\u0004\t\t\f\u0003\u0005\u0003x\u001a\u0015\u0006\u0019AAY\u0011!1\u0019\f\u0001C\u0001\u0005\u0019U\u0016a\u00055b]\u0012dW-\u0012=fGV$xN]!eI\u0016$GC\u0002B\u001c\ro3I\f\u0003\u0005\u00036\u001aE\u0006\u0019AAY\u0011!\u0011\u0019P\"-A\u0002\u0005E\u0006\u0002\u0003D_\u0001\u0011\u0005!Ab0\u0002/!\fg\u000e\u001a7f'R\fw-Z\"b]\u000e,G\u000e\\1uS>tGC\u0002B\u001c\r\u00034\u0019\rC\u0004\u0006~\u0019m\u0006\u0019\u0001<\t\u0011\t\u0015e1\u0018a\u0001\u000bCB\u0001Bb2\u0001\t\u0003\u0011a\u0011Z\u0001\u0016Q\u0006tG\r\\3K_\n\u001c\u0015M\\2fY2\fG/[8o)\u0019\u00119Db3\u0007N\"91\u0011\u0015Dc\u0001\u00041\b\u0002\u0003BC\r\u000b\u0004\r!\"\u0019\t\u000f\u0019E\u0007\u0001\"\u0003\u0007T\u0006\u0019R.\u0019:l'R\fw-Z!t\r&t\u0017n\u001d5fIR1!q\u0007Dk\r/D\u0001\u0002\"\u001c\u0007P\u0002\u0007\u00111\u0005\u0005\u000b\r34y\r%AA\u0002\u0015\u0005\u0014\u0001D3se>\u0014X*Z:tC\u001e,\u0007\u0002\u0003Do\u0001\u0011\u0005!Ab8\u0002\u0015\u0005\u0014wN\u001d;Ti\u0006<W\r\u0006\u0005\u00038\u0019\u0005hQ\u001dDt\u0011!1\u0019Ob7A\u0002\u0005\r\u0012a\u00034bS2,Gm\u0015;bO\u0016D\u0001B!\"\u0007\\\u0002\u0007\u0011\u0011\u0017\u0005\t\u0007/1Y\u000e1\u0001\u0004\u001a!9a1\u001e\u0001\u0005\n\u00195\u0018a\u00074bS2TuNY!oI&sG-\u001a9f]\u0012,g\u000e^*uC\u001e,7\u000f\u0006\u0005\u00038\u0019=h\u0011\u001fD{\u0011!!\tI\";A\u0002\u0005\u001d\u0003\u0002\u0003Dz\rS\u0004\r!!-\u0002\u001b\u0019\f\u0017\u000e\\;sKJ+\u0017m]8o\u0011)\u00199B\";\u0011\u0002\u0003\u00071\u0011\u0004\u0005\b\rs\u0004A\u0011\u0002D~\u00039\u0019H/Y4f\t\u0016\u0004XM\u001c3t\u001f:$b!!>\u0007~\u001a}\b\u0002\u0003C7\ro\u0004\r!a\t\t\u0011\u001d\u0005aq\u001fa\u0001\u0003G\ta\u0001^1sO\u0016$\b\u0002CD\u0003\u0001\u0011\u0005Aab\u0002\u0002!\u001d,G\u000f\u0015:fM\u0016\u0014(/\u001a3M_\u000e\u001cHCBAM\u000f\u00139)\u0002\u0003\u0005\u0004@\u001d\r\u0001\u0019AD\u0006a\u00119ia\"\u0005\u0011\r\r\u00153\u0011JD\b!\u0011\u0011Ye\"\u0005\u0005\u0019\u001dMq\u0011BA\u0001\u0002\u0003\u0015\tA!\u0015\u0003\t}#S\u0007\u000e\u0005\b\u000f/9\u0019\u00011\u0001w\u0003%\u0001\u0018M\u001d;ji&|g\u000eC\u0004\b\u001c\u0001!Ia\"\b\u00021\u001d,G\u000f\u0015:fM\u0016\u0014(/\u001a3M_\u000e\u001c\u0018J\u001c;fe:\fG\u000e\u0006\u0005\u0002\u001a\u001e}q1FD\u0017\u0011!\u0019yd\"\u0007A\u0002\u001d\u0005\u0002\u0007BD\u0012\u000fO\u0001ba!\u0012\u0004J\u001d\u0015\u0002\u0003\u0002B&\u000fO!Ab\"\u000b\b \u0005\u0005\t\u0011!B\u0001\u0005#\u0012Aa\u0018\u00136k!9qqCD\r\u0001\u00041\b\u0002CD\u0018\u000f3\u0001\ra\"\r\u0002\u000fYL7/\u001b;fIB1\u00111AA\n\u000fg\u0001b\u0001DD\u001b\u000fs1\u0018bAD\u001c\u001b\t1A+\u001e9mKJ\u0002Dab\u000f\b@A11QIB%\u000f{\u0001BAa\u0013\b@\u0011aq\u0011ID\u0017\u0003\u0003\u0005\tQ!\u0001\u0003R\t!q\fJ\u001b7\u0011\u001d9)\u0005\u0001C\u0001\u000f\u000f\n\u0011$\\1sW6\u000b\u0007o\u0015;bO\u0016TuNY!t\r&t\u0017n\u001d5fIR1!qGD%\u000f\u0017B\u0001\u0002\"!\bD\u0001\u0007\u0011q\t\u0005\t\u000f\u001b:\u0019\u00051\u0001\u0006,\u0005)1\u000f^1ug\"9q\u0011\u000b\u0001\u0005\u0002\r]\u0013\u0001B:u_BD\u0011b\"\u0016\u0001#\u0003%Iab\u0016\u0002;5\f'o[*uC\u001e,\u0017i\u001d$j]&\u001c\b.\u001a3%I\u00164\u0017-\u001e7uII*\"a\"\u0017+\t\u0015\u0005t1L\u0016\u0003\u000f;\u0002Bab\u0018\bj5\u0011q\u0011\r\u0006\u0005\u000fG:)'A\u0005v]\u000eDWmY6fI*\u0019qqM\u0007\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\bl\u001d\u0005$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"Iqq\u000e\u0001\u0012\u0002\u0013%q\u0011O\u0001-e\u0016lwN^3Fq\u0016\u001cW\u000f^8s\u0003:$WK\u001c:fO&\u001cH/\u001a:PkR\u0004X\u000f^:%I\u00164\u0017-\u001e7uIQ*\"ab\u001d+\t\u0019\rv1\f\u0005\n\u000fo\u0002\u0011\u0013!C\u0005\u000fs\nQEZ1jY*{'-\u00118e\u0013:$W\r]3oI\u0016tGo\u0015;bO\u0016\u001cH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005\u001dm$\u0006BB\r\u000f7:\u0001bb \u0003\u0011\u0003!q\u0011Q\u0001\r\t\u0006;5k\u00195fIVdWM\u001d\t\u0004K\u001d\reaB\u0001\u0003\u0011\u0003!qQQ\n\u0004\u000f\u0007[\u0001b\u0002&\b\u0004\u0012\u0005q\u0011\u0012\u000b\u0003\u000f\u0003C\u0011b\"$\b\u0004\n\u0007I\u0011A;\u0002!I+5+\u0016\"N\u0013R{F+S'F\u001fV#\u0006\u0002CDI\u000f\u0007\u0003\u000b\u0011\u0002<\u0002#I+5+\u0016\"N\u0013R{F+S'F\u001fV#\u0006\u0005C\u0005\b\u0016\u001e\r%\u0019!C\u0001k\u00061C)\u0012$B+2#v,T!Y?\u000e{ejU#D+RKe+R0T)\u0006;UiX!U)\u0016k\u0005\u000bV*\t\u0011\u001deu1\u0011Q\u0001\nY\fq\u0005R#G\u0003VcEkX'B1~\u001buJT*F\u0007V#\u0016JV#`'R\u000bu)R0B)R+U\n\u0015+TA!QqQTDB#\u0003%\tab(\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00138+\t9\tKK\u0002D\u000f7\u0002"
)
public class DAGScheduler implements Logging {
   private final SparkContext sc;
   private final TaskScheduler taskScheduler;
   public final LiveListenerBus org$apache$spark$scheduler$DAGScheduler$$listenerBus;
   public final MapOutputTrackerMaster org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker;
   private final BlockManagerMaster blockManagerMaster;
   private final SparkEnv env;
   public final Clock org$apache$spark$scheduler$DAGScheduler$$clock;
   private final DAGSchedulerSource metricsSource;
   private final AtomicInteger nextJobId;
   private final AtomicInteger nextStageId;
   private final HashMap<Object, HashSet<Object>> jobIdToStageIds;
   private final HashMap<Object, Stage> stageIdToStage;
   private final HashMap<Object, ShuffleMapStage> shuffleIdToMapStage;
   private final HashMap<Object, ActiveJob> jobIdToActiveJob;
   private final HashSet<Stage> waitingStages;
   private final HashSet<Stage> runningStages;
   private final HashSet<Stage> failedStages;
   private final HashSet<ActiveJob> activeJobs;
   private final HashMap<Object, IndexedSeq<Seq<TaskLocation>>> cacheLocs;
   private final HashMap<String, Object> failedEpoch;
   private final OutputCommitCoordinator outputCommitCoordinator;
   private final SerializerInstance closureSerializer;
   private final boolean disallowStageRetryForTest;
   private final boolean unRegisterOutputOnHostOnFetchFailure;
   private final int maxConsecutiveStageAttempts;
   private final ScheduledExecutorService messageScheduler;
   private final DAGSchedulerEventProcessLoop eventProcessLoop;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Clock $lessinit$greater$default$7() {
      return DAGScheduler$.MODULE$.$lessinit$greater$default$7();
   }

   public static int DEFAULT_MAX_CONSECUTIVE_STAGE_ATTEMPTS() {
      return DAGScheduler$.MODULE$.DEFAULT_MAX_CONSECUTIVE_STAGE_ATTEMPTS();
   }

   public static int RESUBMIT_TIMEOUT() {
      return DAGScheduler$.MODULE$.RESUBMIT_TIMEOUT();
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

   public SparkContext sc() {
      return this.sc;
   }

   public TaskScheduler taskScheduler() {
      return this.taskScheduler;
   }

   public DAGSchedulerSource metricsSource() {
      return this.metricsSource;
   }

   public AtomicInteger nextJobId() {
      return this.nextJobId;
   }

   public int numTotalJobs() {
      return this.nextJobId().get();
   }

   private AtomicInteger nextStageId() {
      return this.nextStageId;
   }

   public HashMap<Object, HashSet<Object>> jobIdToStageIds() {
      return this.jobIdToStageIds;
   }

   public HashMap<Object, Stage> stageIdToStage() {
      return this.stageIdToStage;
   }

   public HashMap<Object, ShuffleMapStage> shuffleIdToMapStage() {
      return this.shuffleIdToMapStage;
   }

   public HashMap<Object, ActiveJob> jobIdToActiveJob() {
      return this.jobIdToActiveJob;
   }

   public HashSet<Stage> waitingStages() {
      return this.waitingStages;
   }

   public HashSet<Stage> runningStages() {
      return this.runningStages;
   }

   public HashSet<Stage> failedStages() {
      return this.failedStages;
   }

   public HashSet<ActiveJob> activeJobs() {
      return this.activeJobs;
   }

   private HashMap<Object, IndexedSeq<Seq<TaskLocation>>> cacheLocs() {
      return this.cacheLocs;
   }

   private HashMap<String, Object> failedEpoch() {
      return this.failedEpoch;
   }

   public OutputCommitCoordinator outputCommitCoordinator() {
      return this.outputCommitCoordinator;
   }

   private SerializerInstance closureSerializer() {
      return this.closureSerializer;
   }

   private boolean disallowStageRetryForTest() {
      return this.disallowStageRetryForTest;
   }

   public boolean unRegisterOutputOnHostOnFetchFailure() {
      return this.unRegisterOutputOnHostOnFetchFailure;
   }

   public int maxConsecutiveStageAttempts() {
      return this.maxConsecutiveStageAttempts;
   }

   private ScheduledExecutorService messageScheduler() {
      return this.messageScheduler;
   }

   public DAGSchedulerEventProcessLoop eventProcessLoop() {
      return this.eventProcessLoop;
   }

   public void taskStarted(Task<?> task, TaskInfo taskInfo) {
      this.eventProcessLoop().post(new BeginEvent(task, taskInfo));
   }

   public void taskGettingResult(TaskInfo taskInfo) {
      this.eventProcessLoop().post(new GettingResultEvent(taskInfo));
   }

   public void taskEnded(Task<?> task, TaskEndReason reason, Object result, Seq<AccumulatorV2<?, ?>> accumUpdates, TaskInfo taskInfo) {
      this.eventProcessLoop().post(new CompletionEvent(task, reason, result, accumUpdates, taskInfo));
   }

   public boolean executorHeartbeatReceived(String execId, Tuple4<Object, Object, Object, Seq<AccumulableInfo>>[] accumUpdates, BlockManagerId blockManagerId) {
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerExecutorMetricsUpdate(execId, Predef$.MODULE$.wrapRefArray(accumUpdates)));
      return BoxesRunTime.unboxToBoolean(this.blockManagerMaster.driverEndpoint().askSync(new BlockManagerMessages.BlockManagerHeartbeat(blockManagerId), new RpcTimeout((new scala.concurrent.duration.package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(600))).seconds(), "BlockManagerHeartbeat"), ClassTag$.MODULE$.Boolean()));
   }

   public void executorLost(String execId, ExecutorLossReason reason) {
      this.eventProcessLoop().post(new ExecutorLost(execId, reason));
   }

   public void workerRemoved(String workerId, String host, String message) {
      this.eventProcessLoop().post(new WorkerRemoved(workerId, host, message));
   }

   public void executorAdded(String execId, String host) {
      this.eventProcessLoop().post(new ExecutorAdded(execId, host));
   }

   public void taskSetFailed(TaskSet taskSet, String reason, Option<Throwable> exception) {
      this.eventProcessLoop().post(new TaskSetFailed(taskSet, reason, exception));
   }

   public void speculativeTaskSubmitted(Task<?> task) {
      this.eventProcessLoop().post(new SpeculativeTaskSubmitted(task));
   }

   public IndexedSeq<Seq<TaskLocation>> getCacheLocs(RDD<?> rdd) {
      Object var3;
      synchronized(this.cacheLocs()) {
         if (!this.cacheLocs().contains(BoxesRunTime.boxToInteger(rdd.id()))) {
            IndexedSeq var8;
            label26: {
               label25: {
                  StorageLevel var10000 = rdd.getStorageLevel();
                  StorageLevel var5 = StorageLevel$.MODULE$.NONE();
                  if (var10000 == null) {
                     if (var5 == null) {
                        break label25;
                     }
                  } else if (var10000.equals(var5)) {
                     break label25;
                  }

                  BlockId[] blockIds = (BlockId[])((TraversableOnce)Predef$.MODULE$.refArrayOps(rdd.partitions()).indices().map(new Serializable(this, rdd) {
                     public static final long serialVersionUID = 0L;
                     private final RDD rdd$1;

                     public final RDDBlockId apply(int index) {
                        return new RDDBlockId(this.rdd$1.id(), index);
                     }

                     public {
                        this.rdd$1 = rdd$1;
                     }
                  }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(BlockId.class));
                  var8 = (IndexedSeq)this.blockManagerMaster.getLocations(blockIds).map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final Seq<TaskLocation> apply(Seq<BlockManagerId> bms) {
                        return (Seq)bms.map(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final TaskLocation apply(BlockManagerId bm) {
                              return TaskLocation$.MODULE$.apply(bm.host(), bm.executorId());
                           }
                        }, Seq$.MODULE$.canBuildFrom());
                     }
                  }, scala.collection.IndexedSeq$.MODULE$.canBuildFrom());
                  break label26;
               }

               var8 = (IndexedSeq)scala.package$.MODULE$.IndexedSeq().fill(rdd.partitions().length, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Nil$ apply() {
                     return Nil$.MODULE$;
                  }
               });
            }

            IndexedSeq locs = var8;
            this.cacheLocs().update(BoxesRunTime.boxToInteger(rdd.id()), locs);
         }

         var3 = this.cacheLocs().apply(BoxesRunTime.boxToInteger(rdd.id()));
      }

      return (IndexedSeq)var3;
   }

   private void clearCacheLocs() {
      synchronized(this.cacheLocs()) {
         this.cacheLocs().clear();
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public ShuffleMapStage org$apache$spark$scheduler$DAGScheduler$$getOrCreateShuffleMapStage(ShuffleDependency<?, ?, ?> shuffleDep, int firstJobId) {
      Option var3 = this.shuffleIdToMapStage().get(BoxesRunTime.boxToInteger(shuffleDep.shuffleId()));
      ShuffleMapStage var6;
      if (var3 instanceof Some) {
         Some var4 = (Some)var3;
         ShuffleMapStage stage = (ShuffleMapStage)var4.x();
         var6 = stage;
      } else {
         if (!None$.MODULE$.equals(var3)) {
            throw new MatchError(var3);
         }

         this.getMissingAncestorShuffleDependencies(shuffleDep.rdd()).foreach(new Serializable(this, firstJobId) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DAGScheduler $outer;
            private final int firstJobId$1;

            public final Object apply(ShuffleDependency<?, ?, ?> dep) {
               return this.$outer.shuffleIdToMapStage().contains(BoxesRunTime.boxToInteger(dep.shuffleId())) ? BoxedUnit.UNIT : this.$outer.createShuffleMapStage(dep, this.firstJobId$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.firstJobId$1 = firstJobId$1;
               }
            }
         });
         var6 = this.createShuffleMapStage(shuffleDep, firstJobId);
      }

      return var6;
   }

   public ShuffleMapStage createShuffleMapStage(ShuffleDependency<?, ?, ?> shuffleDep, int jobId) {
      RDD rdd = shuffleDep.rdd();
      int numTasks = rdd.partitions().length;
      List parents = this.getOrCreateParentStages(rdd, jobId);
      int id = this.nextStageId().getAndIncrement();
      ShuffleMapStage stage = new ShuffleMapStage(id, rdd, numTasks, parents, jobId, rdd.creationSite(), shuffleDep, this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker);
      this.stageIdToStage().update(BoxesRunTime.boxToInteger(id), stage);
      this.shuffleIdToMapStage().update(BoxesRunTime.boxToInteger(shuffleDep.shuffleId()), stage);
      this.updateJobIdStageIdMaps(jobId, stage);
      if (!this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.containsShuffle(shuffleDep.shuffleId())) {
         this.logInfo(new Serializable(this, rdd) {
            public static final long serialVersionUID = 0L;
            private final RDD rdd$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Registering RDD ").append((Object)BoxesRunTime.boxToInteger(this.rdd$2.id())).append((Object)" (").append((Object)this.rdd$2.getCreationSite()).append((Object)")").toString();
            }

            public {
               this.rdd$2 = rdd$2;
            }
         });
         this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.registerShuffle(shuffleDep.shuffleId(), rdd.partitions().length);
      }

      return stage;
   }

   private ResultStage createResultStage(RDD<?> rdd, Function2<TaskContext, Iterator<?>, ?> func, int[] partitions, int jobId, CallSite callSite) {
      List parents = this.getOrCreateParentStages(rdd, jobId);
      int id = this.nextStageId().getAndIncrement();
      ResultStage stage = new ResultStage(id, rdd, func, partitions, parents, jobId, callSite);
      this.stageIdToStage().update(BoxesRunTime.boxToInteger(id), stage);
      this.updateJobIdStageIdMaps(jobId, stage);
      return stage;
   }

   private List<Stage> getOrCreateParentStages(RDD<?> rdd, int firstJobId) {
      return ((TraversableOnce)this.getShuffleDependencies(rdd).map(new Serializable(this, firstJobId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;
         private final int firstJobId$2;

         public final ShuffleMapStage apply(ShuffleDependency<?, ?, ?> shuffleDep) {
            return this.$outer.org$apache$spark$scheduler$DAGScheduler$$getOrCreateShuffleMapStage(shuffleDep, this.firstJobId$2);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.firstJobId$2 = firstJobId$2;
            }
         }
      }, HashSet$.MODULE$.canBuildFrom())).toList();
   }

   private ArrayStack<ShuffleDependency<?, ?, ?>> getMissingAncestorShuffleDependencies(RDD<?> rdd) {
      ArrayStack ancestors = new ArrayStack();
      HashSet visited = new HashSet();
      ArrayStack waitingForVisit = new ArrayStack();
      waitingForVisit.push(rdd);

      while(waitingForVisit.nonEmpty()) {
         RDD toVisit = (RDD)waitingForVisit.pop();
         if (!visited.apply(toVisit)) {
            visited.$plus$eq(toVisit);
            this.getShuffleDependencies(toVisit).foreach(new Serializable(this, ancestors, waitingForVisit) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DAGScheduler $outer;
               private final ArrayStack ancestors$1;
               private final ArrayStack waitingForVisit$1;

               public final void apply(ShuffleDependency<?, ?, ?> shuffleDep) {
                  if (!this.$outer.shuffleIdToMapStage().contains(BoxesRunTime.boxToInteger(shuffleDep.shuffleId()))) {
                     this.ancestors$1.push(shuffleDep);
                     this.waitingForVisit$1.push(shuffleDep.rdd());
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.ancestors$1 = ancestors$1;
                     this.waitingForVisit$1 = waitingForVisit$1;
                  }
               }
            });
         }
      }

      return ancestors;
   }

   public HashSet<ShuffleDependency<?, ?, ?>> getShuffleDependencies(RDD<?> rdd) {
      HashSet parents = new HashSet();
      HashSet visited = new HashSet();
      ArrayStack waitingForVisit = new ArrayStack();
      waitingForVisit.push(rdd);

      while(waitingForVisit.nonEmpty()) {
         RDD toVisit = (RDD)waitingForVisit.pop();
         if (!visited.apply(toVisit)) {
            visited.$plus$eq(toVisit);
            toVisit.dependencies().foreach(new Serializable(this, parents, waitingForVisit) {
               public static final long serialVersionUID = 0L;
               private final HashSet parents$1;
               private final ArrayStack waitingForVisit$2;

               public final Object apply(Dependency<?> x0$1) {
                  Object var4;
                  if (x0$1 instanceof ShuffleDependency) {
                     ShuffleDependency var3 = (ShuffleDependency)x0$1;
                     var4 = this.parents$1.$plus$eq(var3);
                  } else {
                     this.waitingForVisit$2.push(x0$1.rdd());
                     var4 = BoxedUnit.UNIT;
                  }

                  return var4;
               }

               public {
                  this.parents$1 = parents$1;
                  this.waitingForVisit$2 = waitingForVisit$2;
               }
            });
         }
      }

      return parents;
   }

   public List<Stage> org$apache$spark$scheduler$DAGScheduler$$getMissingParentStages(Stage stage) {
      HashSet missing = new HashSet();
      HashSet visited = new HashSet();
      ArrayStack waitingForVisit = new ArrayStack();
      waitingForVisit.push(stage.rdd());

      while(waitingForVisit.nonEmpty()) {
         this.visit$1((RDD)waitingForVisit.pop(), stage, missing, visited, waitingForVisit);
      }

      return missing.toList();
   }

   private void updateJobIdStageIdMaps(int jobId, Stage stage) {
      this.updateJobIdStageIdMapsList$1(List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Stage[]{stage})), jobId);
   }

   private void cleanupStateForJobAndIndependentStages(ActiveJob job) {
      Option registeredStages = this.jobIdToStageIds().get(BoxesRunTime.boxToInteger(job.jobId()));
      if (!registeredStages.isEmpty() && !((SetLike)registeredStages.get()).isEmpty()) {
         this.stageIdToStage().filterKeys(new Serializable(this, registeredStages) {
            public static final long serialVersionUID = 0L;
            private final Option registeredStages$1;

            public final boolean apply(int stageId) {
               return this.apply$mcZI$sp(stageId);
            }

            public boolean apply$mcZI$sp(int stageId) {
               return ((HashSet)this.registeredStages$1.get()).contains(BoxesRunTime.boxToInteger(stageId));
            }

            public {
               this.registeredStages$1 = registeredStages$1;
            }
         }).foreach(new Serializable(this, job) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DAGScheduler $outer;
            public final ActiveJob job$1;

            public final void apply(Tuple2<Object, Stage> x0$2) {
               if (x0$2 != null) {
                  int stageId = x0$2._1$mcI$sp();
                  Stage stage = (Stage)x0$2._2();
                  HashSet jobSet = stage.jobIds();
                  if (jobSet.contains(BoxesRunTime.boxToInteger(this.job$1.jobId()))) {
                     jobSet.$minus$eq(BoxesRunTime.boxToInteger(this.job$1.jobId()));
                     if (jobSet.isEmpty()) {
                        this.removeStage$1(stageId);
                        BoxedUnit var10000 = BoxedUnit.UNIT;
                     } else {
                        BoxedUnit var7 = BoxedUnit.UNIT;
                     }
                  } else {
                     this.$outer.logError(new Serializable(this, stageId) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final int stageId$1;

                        public final String apply() {
                           return (new StringOps(Predef$.MODULE$.augmentString("Job %d not registered for stage %d even though that stage was registered for the job"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.job$1.jobId()), BoxesRunTime.boxToInteger(this.stageId$1)}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.stageId$1 = stageId$1;
                           }
                        }
                     });
                     BoxedUnit var8 = BoxedUnit.UNIT;
                  }

               } else {
                  throw new MatchError(x0$2);
               }
            }

            // $FF: synthetic method
            public DAGScheduler org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer() {
               return this.$outer;
            }

            private final void removeStage$1(int stageId) {
               this.$outer.stageIdToStage().get(BoxesRunTime.boxToInteger(stageId)).foreach(new Serializable(this, stageId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  public final int stageId$2;

                  public final Object apply(Stage stage) {
                     if (this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().runningStages().contains(stage)) {
                        this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().logDebug(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringOps(Predef$.MODULE$.augmentString("Removing running stage %d"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.stageId$2)}));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                        this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().runningStages().$minus$eq(stage);
                     } else {
                        BoxedUnit var10000 = BoxedUnit.UNIT;
                     }

                     this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().shuffleIdToMapStage().find(new Serializable(this, stage) {
                        public static final long serialVersionUID = 0L;
                        private final Stage stage$7;

                        public final boolean apply(Tuple2<Object, ShuffleMapStage> x$2) {
                           boolean var3;
                           label23: {
                              Object var10000 = x$2._2();
                              Stage var2 = this.stage$7;
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
                           this.stage$7 = stage$7;
                        }
                     }).withFilter(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final boolean apply(Tuple2<Object, ShuffleMapStage> check$ifrefutable$1) {
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
                        private final <undefinedtype> $outer;

                        public final Option<ShuffleMapStage> apply(Tuple2<Object, ShuffleMapStage> x$3) {
                           if (x$3 != null) {
                              int k = x$3._1$mcI$sp();
                              Option var4 = this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$anonfun$$$outer().org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().shuffleIdToMapStage().remove(BoxesRunTime.boxToInteger(k));
                              return var4;
                           } else {
                              throw new MatchError(x$3);
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
                     if (this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().waitingStages().contains(stage)) {
                        this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().logDebug(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringOps(Predef$.MODULE$.augmentString("Removing stage %d from waiting set."))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.stageId$2)}));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                        this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().waitingStages().$minus$eq(stage);
                     } else {
                        BoxedUnit var2 = BoxedUnit.UNIT;
                     }

                     Object var3;
                     if (this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().failedStages().contains(stage)) {
                        this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().logDebug(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringOps(Predef$.MODULE$.augmentString("Removing stage %d from failed set."))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.stageId$2)}));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                        var3 = this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().failedStages().$minus$eq(stage);
                     } else {
                        var3 = BoxedUnit.UNIT;
                     }

                     return var3;
                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$scheduler$DAGScheduler$$anonfun$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.stageId$2 = stageId$2;
                     }
                  }
               });
               this.$outer.stageIdToStage().$minus$eq(BoxesRunTime.boxToInteger(stageId));
               this.$outer.logDebug(new Serializable(this, stageId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int stageId$2;

                  public final String apply() {
                     return (new StringOps(Predef$.MODULE$.augmentString("After removal of stage %d, remaining stages = %d"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stageId$2), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().stageIdToStage().size())}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.stageId$2 = stageId$2;
                     }
                  }
               });
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.job$1 = job$1;
               }
            }
         });
      } else {
         this.logError(new Serializable(this, job) {
            public static final long serialVersionUID = 0L;
            private final ActiveJob job$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"No stages registered for job ").append((Object)BoxesRunTime.boxToInteger(this.job$1.jobId())).toString();
            }

            public {
               this.job$1 = job$1;
            }
         });
      }

      this.jobIdToStageIds().$minus$eq(BoxesRunTime.boxToInteger(job.jobId()));
      this.jobIdToActiveJob().$minus$eq(BoxesRunTime.boxToInteger(job.jobId()));
      this.activeJobs().$minus$eq(job);
      Stage var3 = job.finalStage();
      if (var3 instanceof ResultStage) {
         ResultStage var4 = (ResultStage)var3;
         var4.removeActiveJob();
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else {
         if (!(var3 instanceof ShuffleMapStage)) {
            throw new MatchError(var3);
         }

         ShuffleMapStage var6 = (ShuffleMapStage)var3;
         var6.removeActiveJob(job);
         BoxedUnit var7 = BoxedUnit.UNIT;
      }

   }

   public <T, U> JobWaiter<U> submitJob(RDD<T> rdd, Function2<TaskContext, Iterator<T>, U> func, Seq<Object> partitions, CallSite callSite, Function2<Object, U, BoxedUnit> resultHandler, Properties properties) {
      int maxPartitions = rdd.partitions().length;
      partitions.find(new Serializable(this, maxPartitions) {
         public static final long serialVersionUID = 0L;
         private final int maxPartitions$1;

         public final boolean apply(int p) {
            return this.apply$mcZI$sp(p);
         }

         public boolean apply$mcZI$sp(int p) {
            return p >= this.maxPartitions$1 || p < 0;
         }

         public {
            this.maxPartitions$1 = maxPartitions$1;
         }
      }).foreach(new Serializable(this, maxPartitions) {
         public static final long serialVersionUID = 0L;
         private final int maxPartitions$1;

         public final Nothing$ apply(int p) {
            throw new IllegalArgumentException((new StringBuilder()).append((Object)"Attempting to access a non-existent partition: ").append((Object)BoxesRunTime.boxToInteger(p)).append((Object)". ").append((Object)"Total number of partitions: ").append((Object)BoxesRunTime.boxToInteger(this.maxPartitions$1)).toString());
         }

         public {
            this.maxPartitions$1 = maxPartitions$1;
         }
      });
      int jobId = this.nextJobId().getAndIncrement();
      if (partitions.size() == 0) {
         return new JobWaiter<U>(this, jobId, 0, resultHandler);
      } else {
         Predef$.MODULE$.assert(partitions.size() > 0);
         JobWaiter waiter = new JobWaiter(this, jobId, partitions.size(), resultHandler);
         this.eventProcessLoop().post(new JobSubmitted(jobId, rdd, func, (int[])partitions.toArray(ClassTag$.MODULE$.Int()), callSite, waiter, (Properties)SerializationUtils.clone(properties)));
         return waiter;
      }
   }

   public <T, U> void runJob(RDD<T> rdd, Function2<TaskContext, Iterator<T>, U> func, Seq<Object> partitions, CallSite callSite, Function2<Object, U, BoxedUnit> resultHandler, Properties properties) {
      long start = System.nanoTime();
      JobWaiter waiter = this.submitJob(rdd, func, partitions, callSite, resultHandler, properties);
      ThreadUtils$.MODULE$.awaitReady(waiter.completionFuture(), Duration$.MODULE$.Inf());
      Try var10 = (Try)waiter.completionFuture().value().get();
      if (var10 instanceof Success) {
         this.logInfo(new Serializable(this, callSite, start, waiter) {
            public static final long serialVersionUID = 0L;
            private final CallSite callSite$1;
            private final long start$1;
            private final JobWaiter waiter$1;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("Job %d finished: %s, took %f s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.waiter$1.jobId()), this.callSite$1.shortForm(), BoxesRunTime.boxToDouble((double)(System.nanoTime() - this.start$1) / (double)1.0E9F)}));
            }

            public {
               this.callSite$1 = callSite$1;
               this.start$1 = start$1;
               this.waiter$1 = waiter$1;
            }
         });
         BoxedUnit var11 = BoxedUnit.UNIT;
      } else if (var10 instanceof Failure) {
         Failure var12 = (Failure)var10;
         Throwable exception = var12.exception();
         this.logInfo(new Serializable(this, callSite, start, waiter) {
            public static final long serialVersionUID = 0L;
            private final CallSite callSite$1;
            private final long start$1;
            private final JobWaiter waiter$1;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("Job %d failed: %s, took %f s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.waiter$1.jobId()), this.callSite$1.shortForm(), BoxesRunTime.boxToDouble((double)(System.nanoTime() - this.start$1) / (double)1.0E9F)}));
            }

            public {
               this.callSite$1 = callSite$1;
               this.start$1 = start$1;
               this.waiter$1 = waiter$1;
            }
         });
         StackTraceElement[] callerStackTrace = (StackTraceElement[])Predef$.MODULE$.refArrayOps((Object[])Thread.currentThread().getStackTrace()).tail();
         exception.setStackTrace((StackTraceElement[])Predef$.MODULE$.refArrayOps((Object[])exception.getStackTrace()).$plus$plus(Predef$.MODULE$.refArrayOps((Object[])callerStackTrace), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(StackTraceElement.class))));
         throw exception;
      } else {
         throw new MatchError(var10);
      }
   }

   public <T, U, R> PartialResult<R> runApproximateJob(RDD<T> rdd, Function2<TaskContext, Iterator<T>, U> func, ApproximateEvaluator<U, R> evaluator, CallSite callSite, long timeout, Properties properties) {
      ApproximateActionListener listener = new ApproximateActionListener(rdd, func, evaluator, timeout);
      int[] partitions = (int[])RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), rdd.partitions().length).toArray(ClassTag$.MODULE$.Int());
      int jobId = this.nextJobId().getAndIncrement();
      this.eventProcessLoop().post(new JobSubmitted(jobId, rdd, func, partitions, callSite, listener, (Properties)SerializationUtils.clone(properties)));
      return listener.awaitResult();
   }

   public <K, V, C> JobWaiter<MapOutputStatistics> submitMapStage(ShuffleDependency<K, V, C> dependency, Function1<MapOutputStatistics, BoxedUnit> callback, CallSite callSite, Properties properties) {
      RDD rdd = dependency.rdd();
      int jobId = this.nextJobId().getAndIncrement();
      if (rdd.partitions().length == 0) {
         throw new SparkException("Can't run submitMapStage on RDD with 0 partitions");
      } else {
         JobWaiter waiter = new JobWaiter(this, jobId, 1, new Serializable(this, callback) {
            public static final long serialVersionUID = 0L;
            private final Function1 callback$1;

            public final void apply(int i, MapOutputStatistics r) {
               this.callback$1.apply(r);
            }

            public {
               this.callback$1 = callback$1;
            }
         });
         this.eventProcessLoop().post(new MapStageSubmitted(jobId, dependency, callSite, waiter, (Properties)SerializationUtils.clone(properties)));
         return waiter;
      }
   }

   public void cancelJob(int jobId, Option<String> reason) {
      this.logInfo(new Serializable(this, jobId) {
         public static final long serialVersionUID = 0L;
         private final int jobId$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Asked to cancel job ").append((Object)BoxesRunTime.boxToInteger(this.jobId$1)).toString();
         }

         public {
            this.jobId$1 = jobId$1;
         }
      });
      this.eventProcessLoop().post(new JobCancelled(jobId, reason));
   }

   public void cancelJobGroup(String groupId) {
      this.logInfo(new Serializable(this, groupId) {
         public static final long serialVersionUID = 0L;
         private final String groupId$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Asked to cancel job group ").append((Object)this.groupId$1).toString();
         }

         public {
            this.groupId$1 = groupId$1;
         }
      });
      this.eventProcessLoop().post(new JobGroupCancelled(groupId));
   }

   public void cancelAllJobs() {
      this.eventProcessLoop().post(AllJobsCancelled$.MODULE$);
   }

   public void doCancelAllJobs() {
      ((HashSet)this.runningStages().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Stage x$4) {
            return x$4.firstJobId();
         }
      }, HashSet$.MODULE$.canBuildFrom())).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final void apply(int x$5) {
            this.apply$mcVI$sp(x$5);
         }

         public void apply$mcVI$sp(int x$5) {
            this.$outer.handleJobCancellation(x$5, Option$.MODULE$.apply("as part of cancellation of all jobs"));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.activeJobs().clear();
      this.jobIdToActiveJob().clear();
   }

   public void cancelStage(int stageId, Option<String> reason) {
      this.eventProcessLoop().post(new StageCancelled(stageId, reason));
   }

   public boolean killTaskAttempt(long taskId, boolean interruptThread, String reason) {
      return this.taskScheduler().killTaskAttempt(taskId, interruptThread, reason);
   }

   public void resubmitFailedStages() {
      if (this.failedStages().size() > 0) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Resubmitting failed stages";
            }
         });
         this.clearCacheLocs();
         Stage[] failedStagesCopy = (Stage[])this.failedStages().toArray(ClassTag$.MODULE$.apply(Stage.class));
         this.failedStages().clear();
         Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(failedStagesCopy).sortBy(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(Stage x$6) {
               return x$6.firstJobId();
            }
         }, Ordering.Int$.MODULE$)).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DAGScheduler $outer;

            public final void apply(Stage stage) {
               this.$outer.org$apache$spark$scheduler$DAGScheduler$$submitStage(stage);
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

   private void submitWaitingChildStages(Stage parent) {
      this.logTrace(new Serializable(this, parent) {
         public static final long serialVersionUID = 0L;
         private final Stage parent$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Checking if any dependencies of ", " are now runnable"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.parent$1}));
         }

         public {
            this.parent$1 = parent$1;
         }
      });
      this.logTrace(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"running: ").append((Object)this.$outer.runningStages()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.logTrace(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"waiting: ").append((Object)this.$outer.waitingStages()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.logTrace(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"failed: ").append((Object)this.$outer.failedStages()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Stage[] childStages = (Stage[])((TraversableOnce)this.waitingStages().filter(new Serializable(this, parent) {
         public static final long serialVersionUID = 0L;
         private final Stage parent$1;

         public final boolean apply(Stage x$7) {
            return x$7.parents().contains(this.parent$1);
         }

         public {
            this.parent$1 = parent$1;
         }
      })).toArray(ClassTag$.MODULE$.apply(Stage.class));
      this.waitingStages().$minus$minus$eq(Predef$.MODULE$.refArrayOps(childStages));
      Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(childStages).sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Stage x$8) {
            return x$8.firstJobId();
         }
      }, Ordering.Int$.MODULE$)).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final void apply(Stage stage) {
            this.$outer.org$apache$spark$scheduler$DAGScheduler$$submitStage(stage);
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

   private Option<Object> activeJobForStage(Stage stage) {
      int[] jobsThatUseStage = (int[])Predef$.MODULE$.intArrayOps((int[])stage.jobIds().toArray(ClassTag$.MODULE$.Int())).sorted(Ordering.Int$.MODULE$);
      return Predef$.MODULE$.intArrayOps(jobsThatUseStage).find(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final boolean apply(int key) {
            return this.apply$mcZI$sp(key);
         }

         public boolean apply$mcZI$sp(int key) {
            return this.$outer.jobIdToActiveJob().contains(BoxesRunTime.boxToInteger(key));
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

   public void handleJobGroupCancelled(String groupId) {
      HashSet activeInGroup = (HashSet)this.activeJobs().filter(new Serializable(this, groupId) {
         public static final long serialVersionUID = 0L;
         public final String groupId$2;

         public final boolean apply(ActiveJob activeJob) {
            return Option$.MODULE$.apply(activeJob.properties()).exists(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final boolean apply(Properties x$9) {
                  boolean var3;
                  label23: {
                     String var10000 = x$9.getProperty(SparkContext$.MODULE$.SPARK_JOB_GROUP_ID());
                     String var2 = this.$outer.groupId$2;
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
         }

         public {
            this.groupId$2 = groupId$2;
         }
      });
      HashSet jobIds = (HashSet)activeInGroup.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(ActiveJob x$10) {
            return x$10.jobId();
         }
      }, HashSet$.MODULE$.canBuildFrom());
      jobIds.foreach(new Serializable(this, groupId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;
         private final String groupId$2;

         public final void apply(int x$11) {
            this.apply$mcVI$sp(x$11);
         }

         public void apply$mcVI$sp(int x$11) {
            this.$outer.handleJobCancellation(x$11, Option$.MODULE$.apply((new StringOps(Predef$.MODULE$.augmentString("part of cancelled job group %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.groupId$2}))));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.groupId$2 = groupId$2;
            }
         }
      });
   }

   public void handleBeginEvent(Task<?> task, TaskInfo taskInfo) {
      int stageAttemptId = BoxesRunTime.unboxToInt(this.stageIdToStage().get(BoxesRunTime.boxToInteger(task.stageId())).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Stage x$12) {
            return x$12.latestInfo().attemptNumber();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return -1;
         }
      }));
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerTaskStart(task.stageId(), stageAttemptId, taskInfo));
   }

   public void handleSpeculativeTaskSubmitted(Task<?> task) {
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerSpeculativeTaskSubmitted(task.stageId()));
   }

   public void handleTaskSetFailed(TaskSet taskSet, String reason, Option<Throwable> exception) {
      this.stageIdToStage().get(BoxesRunTime.boxToInteger(taskSet.stageId())).foreach(new Serializable(this, reason, exception) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;
         private final String reason$1;
         private final Option exception$1;

         public final void apply(Stage x$13) {
            this.$outer.abortStage(x$13, this.reason$1, this.exception$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.reason$1 = reason$1;
               this.exception$1 = exception$1;
            }
         }
      });
   }

   public void cleanUpAfterSchedulerStop() {
      this.activeJobs().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final void apply(ActiveJob job) {
            SparkException error = new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Job ", " cancelled because SparkContext was shut down"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(job.jobId())})));
            job.listener().jobFailed(error);
            String stageFailedMessage = "Stage cancelled because SparkContext was shut down";
            Predef$.MODULE$.refArrayOps(this.$outer.runningStages().toArray(ClassTag$.MODULE$.apply(Stage.class))).foreach(new Serializable(this, stageFailedMessage) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final String stageFailedMessage$1;

               public final void apply(Stage stage) {
                  this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().org$apache$spark$scheduler$DAGScheduler$$markStageAsFinished(stage, new Some(this.stageFailedMessage$1));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.stageFailedMessage$1 = stageFailedMessage$1;
                  }
               }
            });
            this.$outer.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerJobEnd(job.jobId(), this.$outer.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis(), new JobFailed(error)));
         }

         // $FF: synthetic method
         public DAGScheduler org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer() {
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
   }

   public void handleGetTaskResult(TaskInfo taskInfo) {
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerTaskGettingResult(taskInfo));
   }

   public void handleJobSubmitted(int jobId, RDD<?> finalRDD, Function2<TaskContext, Iterator<?>, ?> func, int[] partitions, CallSite callSite, JobListener listener, Properties properties) {
      ObjectRef finalStage = ObjectRef.create((Object)null);

      try {
         finalStage.elem = (T)this.createResultStage(finalRDD, func, partitions, jobId, callSite);
      } catch (Exception var15) {
         this.logWarning(new Serializable(this, jobId) {
            public static final long serialVersionUID = 0L;
            private final int jobId$4;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Creating new stage failed due to exception - job: ").append((Object)BoxesRunTime.boxToInteger(this.jobId$4)).toString();
            }

            public {
               this.jobId$4 = jobId$4;
            }
         }, var15);
         listener.jobFailed(var15);
         return;
      }

      ActiveJob job = new ActiveJob(jobId, (ResultStage)finalStage.elem, callSite, listener, properties);
      this.clearCacheLocs();
      this.logInfo(new Serializable(this, partitions, callSite, job) {
         public static final long serialVersionUID = 0L;
         private final int[] partitions$1;
         private final CallSite callSite$2;
         private final ActiveJob job$2;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Got job %s (%s) with %d output partitions"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.job$2.jobId()), this.callSite$2.shortForm(), BoxesRunTime.boxToInteger(this.partitions$1.length)}));
         }

         public {
            this.partitions$1 = partitions$1;
            this.callSite$2 = callSite$2;
            this.job$2 = job$2;
         }
      });
      this.logInfo(new Serializable(this, finalStage) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef finalStage$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Final stage: ").append((Object)((ResultStage)this.finalStage$1.elem)).append((Object)" (").append((Object)((ResultStage)this.finalStage$1.elem).name()).append((Object)")").toString();
         }

         public {
            this.finalStage$1 = finalStage$1;
         }
      });
      this.logInfo(new Serializable(this, finalStage) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef finalStage$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Parents of final stage: ").append((Object)((ResultStage)this.finalStage$1.elem).parents()).toString();
         }

         public {
            this.finalStage$1 = finalStage$1;
         }
      });
      this.logInfo(new Serializable(this, finalStage) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;
         private final ObjectRef finalStage$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Missing parents: ").append((Object)this.$outer.org$apache$spark$scheduler$DAGScheduler$$getMissingParentStages((ResultStage)this.finalStage$1.elem)).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.finalStage$1 = finalStage$1;
            }
         }
      });
      long jobSubmissionTime = this.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis();
      this.jobIdToActiveJob().update(BoxesRunTime.boxToInteger(jobId), job);
      this.activeJobs().$plus$eq(job);
      ((ResultStage)finalStage.elem).setActiveJob(job);
      int[] stageIds = (int[])((TraversableOnce)this.jobIdToStageIds().apply(BoxesRunTime.boxToInteger(jobId))).toArray(ClassTag$.MODULE$.Int());
      StageInfo[] stageInfos = (StageInfo[])Predef$.MODULE$.intArrayOps(stageIds).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final Iterable<StageInfo> apply(int id) {
            return Option$.MODULE$.<StageInfo>option2Iterable(this.$outer.stageIdToStage().get(BoxesRunTime.boxToInteger(id)).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final StageInfo apply(Stage x$14) {
                  return x$14.latestInfo();
               }
            }));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(StageInfo.class)));
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerJobStart(job.jobId(), jobSubmissionTime, Predef$.MODULE$.wrapRefArray(stageInfos), properties));
      this.org$apache$spark$scheduler$DAGScheduler$$submitStage((ResultStage)finalStage.elem);
   }

   public void handleMapStageSubmitted(int jobId, ShuffleDependency<?, ?, ?> dependency, CallSite callSite, JobListener listener, Properties properties) {
      ObjectRef finalStage = ObjectRef.create((Object)null);

      try {
         finalStage.elem = (T)this.org$apache$spark$scheduler$DAGScheduler$$getOrCreateShuffleMapStage(dependency, jobId);
      } catch (Exception var13) {
         this.logWarning(new Serializable(this, jobId) {
            public static final long serialVersionUID = 0L;
            private final int jobId$5;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Creating new stage failed due to exception - job: ").append((Object)BoxesRunTime.boxToInteger(this.jobId$5)).toString();
            }

            public {
               this.jobId$5 = jobId$5;
            }
         }, var13);
         listener.jobFailed(var13);
         return;
      }

      ActiveJob job = new ActiveJob(jobId, (ShuffleMapStage)finalStage.elem, callSite, listener, properties);
      this.clearCacheLocs();
      this.logInfo(new Serializable(this, jobId, dependency, callSite) {
         public static final long serialVersionUID = 0L;
         private final int jobId$5;
         private final ShuffleDependency dependency$1;
         private final CallSite callSite$3;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Got map stage job %s (%s) with %d output partitions"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.jobId$5), this.callSite$3.shortForm(), BoxesRunTime.boxToInteger(this.dependency$1.rdd().partitions().length)}));
         }

         public {
            this.jobId$5 = jobId$5;
            this.dependency$1 = dependency$1;
            this.callSite$3 = callSite$3;
         }
      });
      this.logInfo(new Serializable(this, finalStage) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef finalStage$2;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Final stage: ").append((Object)((ShuffleMapStage)this.finalStage$2.elem)).append((Object)" (").append((Object)((ShuffleMapStage)this.finalStage$2.elem).name()).append((Object)")").toString();
         }

         public {
            this.finalStage$2 = finalStage$2;
         }
      });
      this.logInfo(new Serializable(this, finalStage) {
         public static final long serialVersionUID = 0L;
         private final ObjectRef finalStage$2;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Parents of final stage: ").append((Object)((ShuffleMapStage)this.finalStage$2.elem).parents()).toString();
         }

         public {
            this.finalStage$2 = finalStage$2;
         }
      });
      this.logInfo(new Serializable(this, finalStage) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;
         private final ObjectRef finalStage$2;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Missing parents: ").append((Object)this.$outer.org$apache$spark$scheduler$DAGScheduler$$getMissingParentStages((ShuffleMapStage)this.finalStage$2.elem)).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.finalStage$2 = finalStage$2;
            }
         }
      });
      long jobSubmissionTime = this.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis();
      this.jobIdToActiveJob().update(BoxesRunTime.boxToInteger(jobId), job);
      this.activeJobs().$plus$eq(job);
      ((ShuffleMapStage)finalStage.elem).addActiveJob(job);
      int[] stageIds = (int[])((TraversableOnce)this.jobIdToStageIds().apply(BoxesRunTime.boxToInteger(jobId))).toArray(ClassTag$.MODULE$.Int());
      StageInfo[] stageInfos = (StageInfo[])Predef$.MODULE$.intArrayOps(stageIds).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final Iterable<StageInfo> apply(int id) {
            return Option$.MODULE$.<StageInfo>option2Iterable(this.$outer.stageIdToStage().get(BoxesRunTime.boxToInteger(id)).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final StageInfo apply(Stage x$15) {
                  return x$15.latestInfo();
               }
            }));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(StageInfo.class)));
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerJobStart(job.jobId(), jobSubmissionTime, Predef$.MODULE$.wrapRefArray(stageInfos), properties));
      this.org$apache$spark$scheduler$DAGScheduler$$submitStage((ShuffleMapStage)finalStage.elem);
      if (((ShuffleMapStage)finalStage.elem).isAvailable()) {
         this.markMapStageJobAsFinished(job, this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.getStatistics(dependency));
      }

   }

   public void org$apache$spark$scheduler$DAGScheduler$$submitStage(Stage stage) {
      Option jobId = this.activeJobForStage(stage);
      if (jobId.isDefined()) {
         this.logDebug(new Serializable(this, stage) {
            public static final long serialVersionUID = 0L;
            private final Stage stage$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)"submitStage(").append((Object)this.stage$2).append((Object)")").toString();
            }

            public {
               this.stage$2 = stage$2;
            }
         });
         if (!this.waitingStages().apply(stage) && !this.runningStages().apply(stage) && !this.failedStages().apply(stage)) {
            List missing = (List)this.org$apache$spark$scheduler$DAGScheduler$$getMissingParentStages(stage).sortBy(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(Stage x$16) {
                  return x$16.id();
               }
            }, Ordering.Int$.MODULE$);
            this.logDebug(new Serializable(this, missing) {
               public static final long serialVersionUID = 0L;
               private final List missing$2;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"missing: ").append((Object)this.missing$2).toString();
               }

               public {
                  this.missing$2 = missing$2;
               }
            });
            if (missing.isEmpty()) {
               this.logInfo(new Serializable(this, stage) {
                  public static final long serialVersionUID = 0L;
                  private final Stage stage$2;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Submitting ").append((Object)this.stage$2).append((Object)" (").append((Object)this.stage$2.rdd()).append((Object)"), which has no missing parents").toString();
                  }

                  public {
                     this.stage$2 = stage$2;
                  }
               });
               this.submitMissingTasks(stage, BoxesRunTime.unboxToInt(jobId.get()));
            } else {
               missing.foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final DAGScheduler $outer;

                  public final void apply(Stage parent) {
                     this.$outer.org$apache$spark$scheduler$DAGScheduler$$submitStage(parent);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               this.waitingStages().$plus$eq(stage);
            }
         }
      } else {
         this.abortStage(stage, (new StringBuilder()).append((Object)"No active job for stage ").append((Object)BoxesRunTime.boxToInteger(stage.id())).toString(), None$.MODULE$);
      }

   }

   private void submitMissingTasks(Stage stage, int jobId) {
      this.logDebug(new Serializable(this, stage) {
         public static final long serialVersionUID = 0L;
         private final Stage stage$4;

         public final String apply() {
            return (new StringBuilder()).append((Object)"submitMissingTasks(").append((Object)this.stage$4).append((Object)")").toString();
         }

         public {
            this.stage$4 = stage$4;
         }
      });
      Seq partitionsToCompute = stage.findMissingPartitions();
      Properties properties = ((ActiveJob)this.jobIdToActiveJob().apply(BoxesRunTime.boxToInteger(jobId))).properties();
      this.runningStages().$plus$eq(stage);
      if (stage instanceof ShuffleMapStage) {
         ShuffleMapStage var6 = (ShuffleMapStage)stage;
         this.outputCommitCoordinator().stageStart(var6.id(), var6.numPartitions() - 1);
         BoxedUnit var7 = BoxedUnit.UNIT;
      } else {
         if (!(stage instanceof ResultStage)) {
            throw new MatchError(stage);
         }

         ResultStage var8 = (ResultStage)stage;
         this.outputCommitCoordinator().stageStart(var8.id(), var8.rdd().partitions().length - 1);
         BoxedUnit var58 = BoxedUnit.UNIT;
      }

      Map var10000;
      try {
         Map var16;
         if (stage instanceof ShuffleMapStage) {
            var16 = ((TraversableOnce)partitionsToCompute.map(new Serializable(this, stage) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DAGScheduler $outer;
               private final Stage stage$4;

               public final Tuple2<Object, Seq<TaskLocation>> apply(int id) {
                  return new Tuple2<Object, Seq<TaskLocation>>(BoxesRunTime.boxToInteger(id), this.$outer.getPreferredLocs(this.stage$4.rdd(), id));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.stage$4 = stage$4;
                  }
               }
            }, Seq$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
         } else {
            if (!(stage instanceof ResultStage)) {
               throw new MatchError(stage);
            }

            ResultStage var17 = (ResultStage)stage;
            var16 = ((TraversableOnce)partitionsToCompute.map(new Serializable(this, stage, var17) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DAGScheduler $outer;
               private final Stage stage$4;
               private final ResultStage x3$1;

               public final Tuple2<Object, Seq<TaskLocation>> apply(int id) {
                  int p = this.x3$1.partitions()[id];
                  return new Tuple2<Object, Seq<TaskLocation>>(BoxesRunTime.boxToInteger(id), this.$outer.getPreferredLocs(this.stage$4.rdd(), p));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.stage$4 = stage$4;
                     this.x3$1 = x3$1;
                  }
               }
            }, Seq$.MODULE$.canBuildFrom())).toMap(Predef$.MODULE$.$conforms());
         }

         var10000 = var16;
      } catch (Throwable var55) {
         Option var12 = NonFatal$.MODULE$.unapply(var55);
         if (var12.isEmpty()) {
            throw var55;
         }

         Throwable e = (Throwable)var12.get();
         stage.makeNewStageAttempt(partitionsToCompute.size(), stage.makeNewStageAttempt$default$2());
         this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerStageSubmitted(stage.latestInfo(), properties));
         this.abortStage(stage, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task creation failed: ", "\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{e, Utils$.MODULE$.exceptionString(e)})), new Some(e));
         this.runningStages().$minus$eq(stage);
         return;
      }

      scala.collection.Map taskIdToLocations = var10000;
      stage.makeNewStageAttempt(partitionsToCompute.size(), taskIdToLocations.values().toSeq());
      if (partitionsToCompute.nonEmpty()) {
         stage.latestInfo().submissionTime_$eq(new Some(BoxesRunTime.boxToLong(this.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis())));
      }

      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerStageSubmitted(stage.latestInfo(), properties));
      ObjectRef taskBinary = ObjectRef.create((Object)null);

      try {
         byte[] taskBinaryBytes;
         if (stage instanceof ShuffleMapStage) {
            ShuffleMapStage var27 = (ShuffleMapStage)stage;
            taskBinaryBytes = JavaUtils.bufferToArray(this.closureSerializer().serialize(new Tuple2(var27.rdd(), var27.shuffleDep()), ClassTag$.MODULE$.AnyRef()));
         } else {
            if (!(stage instanceof ResultStage)) {
               throw new MatchError(stage);
            }

            ResultStage var29 = (ResultStage)stage;
            taskBinaryBytes = JavaUtils.bufferToArray(this.closureSerializer().serialize(new Tuple2(var29.rdd(), var29.func()), ClassTag$.MODULE$.AnyRef()));
         }

         taskBinary.elem = (T)this.sc().broadcast(taskBinaryBytes, ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
      } catch (Throwable var56) {
         if (var56 instanceof NotSerializableException) {
            NotSerializableException var21 = (NotSerializableException)var56;
            this.abortStage(stage, (new StringBuilder()).append((Object)"Task not serializable: ").append((Object)var21.toString()).toString(), new Some(var21));
            this.runningStages().$minus$eq(stage);
            return;
         }

         Option var23 = NonFatal$.MODULE$.unapply(var56);
         if (var23.isEmpty()) {
            throw var56;
         }

         Throwable e = (Throwable)var23.get();
         this.abortStage(stage, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task serialization failed: ", "\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{e, Utils$.MODULE$.exceptionString(e)})), new Some(e));
         this.runningStages().$minus$eq(stage);
         return;
      }

      try {
         byte[] serializedTaskMetrics = this.closureSerializer().serialize(stage.latestInfo().taskMetrics(), ClassTag$.MODULE$.apply(TaskMetrics.class)).array();
         Seq var39;
         if (stage instanceof ShuffleMapStage) {
            ShuffleMapStage var38 = (ShuffleMapStage)stage;
            var38.pendingPartitions().clear();
            var39 = (Seq)partitionsToCompute.map(new Serializable(this, jobId, properties, taskIdToLocations, taskBinary, serializedTaskMetrics, var38) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DAGScheduler $outer;
               private final int jobId$6;
               private final Properties properties$1;
               private final scala.collection.Map taskIdToLocations$1;
               private final ObjectRef taskBinary$1;
               private final byte[] serializedTaskMetrics$1;
               private final ShuffleMapStage x2$1;

               public final ShuffleMapTask apply(int id) {
                  Seq locs = (Seq)this.taskIdToLocations$1.apply(BoxesRunTime.boxToInteger(id));
                  Partition part = this.x2$1.rdd().partitions()[id];
                  this.x2$1.pendingPartitions().$plus$eq(BoxesRunTime.boxToInteger(id));
                  return new ShuffleMapTask(this.x2$1.id(), this.x2$1.latestInfo().attemptNumber(), (Broadcast)this.taskBinary$1.elem, part, locs, this.properties$1, this.serializedTaskMetrics$1, Option$.MODULE$.apply(BoxesRunTime.boxToInteger(this.jobId$6)), Option$.MODULE$.apply(this.$outer.sc().applicationId()), this.$outer.sc().applicationAttemptId());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.jobId$6 = jobId$6;
                     this.properties$1 = properties$1;
                     this.taskIdToLocations$1 = taskIdToLocations$1;
                     this.taskBinary$1 = taskBinary$1;
                     this.serializedTaskMetrics$1 = serializedTaskMetrics$1;
                     this.x2$1 = x2$1;
                  }
               }
            }, Seq$.MODULE$.canBuildFrom());
         } else {
            if (!(stage instanceof ResultStage)) {
               throw new MatchError(stage);
            }

            ResultStage var40 = (ResultStage)stage;
            var39 = (Seq)partitionsToCompute.map(new Serializable(this, jobId, properties, taskIdToLocations, taskBinary, serializedTaskMetrics, var40) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DAGScheduler $outer;
               private final int jobId$6;
               private final Properties properties$1;
               private final scala.collection.Map taskIdToLocations$1;
               private final ObjectRef taskBinary$1;
               private final byte[] serializedTaskMetrics$1;
               private final ResultStage x3$2;

               public final ResultTask<Nothing$, Nothing$> apply(int id) {
                  int p = this.x3$2.partitions()[id];
                  Partition part = this.x3$2.rdd().partitions()[p];
                  Seq locs = (Seq)this.taskIdToLocations$1.apply(BoxesRunTime.boxToInteger(id));
                  return new ResultTask<Nothing$, Nothing$>(this.x3$2.id(), this.x3$2.latestInfo().attemptNumber(), (Broadcast)this.taskBinary$1.elem, part, locs, id, this.properties$1, this.serializedTaskMetrics$1, Option$.MODULE$.apply(BoxesRunTime.boxToInteger(this.jobId$6)), Option$.MODULE$.apply(this.$outer.sc().applicationId()), this.$outer.sc().applicationAttemptId());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.jobId$6 = jobId$6;
                     this.properties$1 = properties$1;
                     this.taskIdToLocations$1 = taskIdToLocations$1;
                     this.taskBinary$1 = taskBinary$1;
                     this.serializedTaskMetrics$1 = serializedTaskMetrics$1;
                     this.x3$2 = x3$2;
                  }
               }
            }, Seq$.MODULE$.canBuildFrom());
         }

         var59 = var39;
      } catch (Throwable var57) {
         Option var33 = NonFatal$.MODULE$.unapply(var57);
         if (var33.isEmpty()) {
            throw var57;
         }

         Throwable e = (Throwable)var33.get();
         this.abortStage(stage, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task creation failed: ", "\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{e, Utils$.MODULE$.exceptionString(e)})), new Some(e));
         this.runningStages().$minus$eq(stage);
         return;
      }

      Seq tasks = var59;
      if (tasks.size() > 0) {
         this.logInfo(new Serializable(this, stage, tasks) {
            public static final long serialVersionUID = 0L;
            private final Stage stage$4;
            private final Seq tasks$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Submitting ", " missing tasks from ", " (", ") (first 15 "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.tasks$1.size()), this.stage$4, this.stage$4.rdd()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"tasks are for partitions ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{((TraversableLike)this.tasks$1.take(15)).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final int apply(Task<?> x$17) {
                     return x$17.partitionId();
                  }
               }, Seq$.MODULE$.canBuildFrom())}))).toString();
            }

            public {
               this.stage$4 = stage$4;
               this.tasks$1 = tasks$1;
            }
         });
         this.taskScheduler().submitTasks(new TaskSet((Task[])tasks.toArray(ClassTag$.MODULE$.apply(Task.class)), stage.id(), stage.latestInfo().attemptNumber(), jobId, properties));
      } else {
         this.org$apache$spark$scheduler$DAGScheduler$$markStageAsFinished(stage, None$.MODULE$);
         String debugString;
         if (stage instanceof ShuffleMapStage) {
            ShuffleMapStage var43 = (ShuffleMapStage)stage;
            debugString = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Stage ", " is actually done; "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{var43}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(available: ", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToBoolean(var43.isAvailable())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"available outputs: ", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(var43.numAvailableOutputs())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"partitions: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(var43.numPartitions())}))).toString();
         } else {
            if (!(stage instanceof ResultStage)) {
               throw new MatchError(stage);
            }

            ResultStage var45 = (ResultStage)stage;
            debugString = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Stage ", " is actually done; (partitions: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{var45, BoxesRunTime.boxToInteger(var45.numPartitions())}));
         }

         this.logDebug(new Serializable(this, debugString) {
            public static final long serialVersionUID = 0L;
            private final String debugString$1;

            public final String apply() {
               return this.debugString$1;
            }

            public {
               this.debugString$1 = debugString$1;
            }
         });
         this.submitWaitingChildStages(stage);
      }

   }

   private void updateAccumulators(CompletionEvent event) {
      Task task = event.task();
      Stage stage = (Stage)this.stageIdToStage().apply(BoxesRunTime.boxToInteger(task.stageId()));

      try {
         event.accumUpdates().foreach(new Serializable(this, event, stage) {
            public static final long serialVersionUID = 0L;
            private final CompletionEvent event$1;
            private final Stage stage$5;

            public final void apply(AccumulatorV2<?, ?> updates) {
               long id = updates.id();
               Option var5 = AccumulatorContext$.MODULE$.get(id);
               if (var5 instanceof Some) {
                  Some var6 = (Some)var5;
                  AccumulatorV2 accum = (AccumulatorV2)var6.x();
                  accum.merge(updates);
                  if (accum.name().isDefined() && !updates.isZero()) {
                     this.stage$5.latestInfo().accumulables().update(BoxesRunTime.boxToLong(id), accum.toInfo(None$.MODULE$, new Some(accum.value())));
                     TaskInfo var10000 = this.event$1.taskInfo();
                     AccumulableInfo var9 = accum.toInfo(new Some(updates.value()), new Some(accum.value()));
                     var10000.setAccumulables((Seq)this.event$1.taskInfo().accumulables().$plus$colon(var9, Seq$.MODULE$.canBuildFrom()));
                  }

               } else if (None$.MODULE$.equals(var5)) {
                  throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"attempted to access non-existent accumulator ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(id)})));
               } else {
                  throw new MatchError(var5);
               }
            }

            public {
               this.event$1 = event$1;
               this.stage$5 = stage$5;
            }
         });
      } catch (Throwable var10) {
         Option var6 = NonFatal$.MODULE$.unapply(var10);
         if (var6.isEmpty()) {
            throw var10;
         }

         Throwable e = (Throwable)var6.get();
         this.logError(new Serializable(this, task) {
            public static final long serialVersionUID = 0L;
            private final Task task$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to update accumulators for task ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.task$1.partitionId())}));
            }

            public {
               this.task$1 = task$1;
            }
         }, e);
         BoxedUnit var8 = BoxedUnit.UNIT;
         return;
      }

   }

   private void postTaskEnd(CompletionEvent event) {
      TaskMetrics var10000;
      if (event.accumUpdates().nonEmpty()) {
         try {
            var10000 = TaskMetrics$.MODULE$.fromAccumulators(event.accumUpdates());
         } catch (Throwable var11) {
            label34: {
               Option var5 = NonFatal$.MODULE$.unapply(var11);
               if (var5.isEmpty()) {
                  throw var11;
               }

               Throwable e = (Throwable)var5.get();
               long taskId = event.taskInfo().taskId();
               this.logError(new Serializable(this, taskId) {
                  public static final long serialVersionUID = 0L;
                  private final long taskId$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error when attempting to reconstruct metrics for task ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskId$1)}));
                  }

                  public {
                     this.taskId$1 = taskId$1;
                  }
               }, e);
               Object var7 = null;
               var10000 = (TaskMetrics)var7;
               break label34;
            }
         }
      } else {
         var10000 = null;
      }

      TaskMetrics taskMetrics = var10000;
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerTaskEnd(event.task().stageId(), event.task().stageAttemptId(), Utils$.MODULE$.getFormattedClassName(event.task()), event.reason(), event.taskInfo(), taskMetrics));
   }

   public void handleTaskCompletion(CompletionEvent event) {
      Task task = event.task();
      String taskId = event.taskInfo().id();
      int stageId = task.stageId();
      String taskType = Utils$.MODULE$.getFormattedClassName(task);
      this.outputCommitCoordinator().taskCompleted(stageId, task.partitionId(), event.taskInfo().attemptNumber(), event.reason());
      if (this.stageIdToStage().contains(BoxesRunTime.boxToInteger(task.stageId()))) {
         Stage stage = (Stage)this.stageIdToStage().apply(BoxesRunTime.boxToInteger(task.stageId()));
         TaskEndReason var7 = event.reason();
         if (Success$.MODULE$.equals(var7)) {
            if (task instanceof ResultTask) {
               ResultTask var10 = (ResultTask)task;
               ResultStage resultStage = (ResultStage)stage;
               Option var13 = resultStage.activeJob();
               if (var13 instanceof Some) {
                  Some var14 = (Some)var13;
                  ActiveJob job = (ActiveJob)var14.x();
                  if (job.finished()[var10.outputId()]) {
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     this.updateAccumulators(event);
                     BoxedUnit var58 = BoxedUnit.UNIT;
                  }
               } else {
                  if (!None$.MODULE$.equals(var13)) {
                     throw new MatchError(var13);
                  }

                  BoxedUnit var16 = BoxedUnit.UNIT;
               }

               BoxedUnit var11 = BoxedUnit.UNIT;
            } else {
               this.updateAccumulators(event);
               BoxedUnit var51 = BoxedUnit.UNIT;
            }

            BoxedUnit var8 = BoxedUnit.UNIT;
         } else if (var7 instanceof ExceptionFailure) {
            this.updateAccumulators(event);
            BoxedUnit var49 = BoxedUnit.UNIT;
         } else {
            BoxedUnit var50 = BoxedUnit.UNIT;
         }

         this.postTaskEnd(event);
         TaskEndReason var17 = event.reason();
         if (!Success$.MODULE$.equals(var17)) {
            if (Resubmitted$.MODULE$.equals(var17)) {
               this.logInfo(new Serializable(this, task) {
                  public static final long serialVersionUID = 0L;
                  private final Task task$2;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Resubmitted ").append((Object)this.task$2).append((Object)", so marking it as still running").toString();
                  }

                  public {
                     this.task$2 = task$2;
                  }
               });
               if (stage instanceof ShuffleMapStage) {
                  ShuffleMapStage var34 = (ShuffleMapStage)stage;
                  var34.pendingPartitions().$plus$eq(BoxesRunTime.boxToInteger(task.partitionId()));
                  BoxedUnit var35 = BoxedUnit.UNIT;
               } else {
                  Predef$.MODULE$.assert(false, new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "TaskSetManagers should only send Resubmitted task statuses for tasks in ShuffleMapStages.";
                     }
                  });
                  BoxedUnit var57 = BoxedUnit.UNIT;
               }

               BoxedUnit var52 = BoxedUnit.UNIT;
            } else if (var17 instanceof FetchFailed) {
               FetchFailed var36 = (FetchFailed)var17;
               BlockManagerId bmAddress = var36.bmAddress();
               int shuffleId = var36.shuffleId();
               int mapId = var36.mapId();
               String failureMessage = var36.message();
               Stage failedStage = (Stage)this.stageIdToStage().apply(BoxesRunTime.boxToInteger(task.stageId()));
               ShuffleMapStage mapStage = (ShuffleMapStage)this.shuffleIdToMapStage().apply(BoxesRunTime.boxToInteger(shuffleId));
               if (failedStage.latestInfo().attemptNumber() != task.stageAttemptId()) {
                  this.logInfo(new Serializable(this, task, failedStage) {
                     public static final long serialVersionUID = 0L;
                     private final Task task$2;
                     private final Stage failedStage$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ignoring fetch failure from ", " as it's from ", " attempt"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.task$2, this.failedStage$2}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" ", " and there is a more recent attempt for that stage "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.task$2.stageAttemptId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(attempt ", ") running"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.failedStage$2.latestInfo().attemptNumber())}))).toString();
                     }

                     public {
                        this.task$2 = task$2;
                        this.failedStage$2 = failedStage$2;
                     }
                  });
                  BoxedUnit var71 = BoxedUnit.UNIT;
               } else {
                  if (this.runningStages().contains(failedStage)) {
                     this.logInfo(new Serializable(this, failedStage, mapStage) {
                        public static final long serialVersionUID = 0L;
                        private final Stage failedStage$2;
                        private final ShuffleMapStage mapStage$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Marking ", " (", ") as failed "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.failedStage$2, this.failedStage$2.name()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"due to a fetch failure from ", " (", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.mapStage$1, this.mapStage$1.name()}))).toString();
                        }

                        public {
                           this.failedStage$2 = failedStage$2;
                           this.mapStage$1 = mapStage$1;
                        }
                     });
                     this.org$apache$spark$scheduler$DAGScheduler$$markStageAsFinished(failedStage, new Some(failureMessage));
                  } else {
                     this.logDebug(new Serializable(this, task, failedStage) {
                        public static final long serialVersionUID = 0L;
                        private final Task task$2;
                        private final Stage failedStage$2;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Received fetch failure from ", ", but its from ", " which is no "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.task$2, this.failedStage$2}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"longer running"})))).s(Nil$.MODULE$)).toString();
                        }

                        public {
                           this.task$2 = task$2;
                           this.failedStage$2 = failedStage$2;
                        }
                     });
                  }

                  failedStage.fetchFailedAttemptIds().add(BoxesRunTime.boxToInteger(task.stageAttemptId()));
                  boolean shouldAbortStage = failedStage.fetchFailedAttemptIds().size() >= this.maxConsecutiveStageAttempts() || this.disallowStageRetryForTest();
                  if (shouldAbortStage) {
                     String abortMessage = this.disallowStageRetryForTest() ? "Fetch failure will not retry stage due to testing config" : (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " (", ")\n                 |has failed the maximum allowable number of\n                 |times: ", ".\n                 |Most recent failure reason: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{failedStage, failedStage.name(), BoxesRunTime.boxToInteger(this.maxConsecutiveStageAttempts()), failureMessage}))))).stripMargin().replaceAll("\n", " ");
                     this.abortStage(failedStage, abortMessage, None$.MODULE$);
                     BoxedUnit var67 = BoxedUnit.UNIT;
                  } else {
                     boolean noResubmitEnqueued = !this.failedStages().contains(failedStage);
                     this.failedStages().$plus$eq(failedStage);
                     this.failedStages().$plus$eq(mapStage);
                     if (noResubmitEnqueued) {
                        this.logInfo(new Serializable(this, failedStage, mapStage) {
                           public static final long serialVersionUID = 0L;
                           private final Stage failedStage$2;
                           private final ShuffleMapStage mapStage$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Resubmitting ", " (", ") and "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.mapStage$1, this.mapStage$1.name()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " (", ") due to fetch failure"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.failedStage$2, this.failedStage$2.name()}))).toString();
                           }

                           public {
                              this.failedStage$2 = failedStage$2;
                              this.mapStage$1 = mapStage$1;
                           }
                        });
                        this.messageScheduler().schedule(new Runnable(this) {
                           // $FF: synthetic field
                           private final DAGScheduler $outer;

                           public void run() {
                              this.$outer.eventProcessLoop().post(ResubmitFailedStages$.MODULE$);
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        }, (long)DAGScheduler$.MODULE$.RESUBMIT_TIMEOUT(), TimeUnit.MILLISECONDS);
                     } else {
                        BoxedUnit var68 = BoxedUnit.UNIT;
                     }
                  }

                  if (mapId != -1) {
                     this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.unregisterMapOutput(shuffleId, mapId, bmAddress);
                  }

                  if (bmAddress == null) {
                     BoxedUnit var70 = BoxedUnit.UNIT;
                  } else {
                     Option hostToUnregisterOutputs = (Option)(this.env.blockManager().externalShuffleServiceEnabled() && this.unRegisterOutputOnHostOnFetchFailure() ? new Some(bmAddress.host()) : None$.MODULE$);
                     this.removeExecutorAndUnregisterOutputs(bmAddress.executorId(), true, hostToUnregisterOutputs, new Some(BoxesRunTime.boxToLong(task.epoch())));
                     BoxedUnit var69 = BoxedUnit.UNIT;
                  }
               }
            } else if (var17 instanceof TaskCommitDenied) {
               BoxedUnit var53 = BoxedUnit.UNIT;
            } else if (var17 instanceof ExceptionFailure) {
               BoxedUnit var54 = BoxedUnit.UNIT;
            } else if (TaskResultLost$.MODULE$.equals(var17)) {
               BoxedUnit var55 = BoxedUnit.UNIT;
            } else {
               boolean var47;
               if (var17 instanceof ExecutorLostFailure) {
                  var47 = true;
               } else if (var17 instanceof TaskKilled) {
                  var47 = true;
               } else if (UnknownReason$.MODULE$.equals(var17)) {
                  var47 = true;
               } else {
                  var47 = false;
               }

               if (!var47) {
                  throw new MatchError(var17);
               }

               BoxedUnit var56 = BoxedUnit.UNIT;
            }
         } else {
            if (task instanceof ResultTask) {
               ResultTask var20 = (ResultTask)task;
               ResultStage resultStage = (ResultStage)stage;
               Option var23 = resultStage.activeJob();
               if (var23 instanceof Some) {
                  Some var24 = (Some)var23;
                  ActiveJob job = (ActiveJob)var24.x();
                  if (job.finished()[var20.outputId()]) {
                     BoxedUnit var59 = BoxedUnit.UNIT;
                  } else {
                     job.finished()[var20.outputId()] = true;
                     job.numFinished_$eq(job.numFinished() + 1);
                     if (job.numFinished() == job.numPartitions()) {
                        this.org$apache$spark$scheduler$DAGScheduler$$markStageAsFinished(resultStage, this.markStageAsFinished$default$2());
                        this.cleanupStateForJobAndIndependentStages(job);
                        this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerJobEnd(job.jobId(), this.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis(), JobSucceeded$.MODULE$));
                     }

                     try {
                        job.listener().taskSucceeded(var20.outputId(), event.result());
                        BoxedUnit var61 = BoxedUnit.UNIT;
                     } catch (Exception var48) {
                        job.listener().jobFailed(new SparkDriverExecutionException(var48));
                        BoxedUnit var60 = BoxedUnit.UNIT;
                     }
                  }
               } else {
                  if (!None$.MODULE$.equals(var23)) {
                     throw new MatchError(var23);
                  }

                  this.logInfo(new Serializable(this, var20) {
                     public static final long serialVersionUID = 0L;
                     private final ResultTask x2$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Ignoring result from ").append((Object)this.x2$2).append((Object)" because its job has finished").toString();
                     }

                     public {
                        this.x2$2 = x2$2;
                     }
                  });
                  BoxedUnit var26 = BoxedUnit.UNIT;
               }

               BoxedUnit var21 = BoxedUnit.UNIT;
            } else {
               if (!(task instanceof ShuffleMapTask)) {
                  throw new MatchError(task);
               }

               ShuffleMapTask var28 = (ShuffleMapTask)task;
               ShuffleMapStage shuffleStage = (ShuffleMapStage)stage;
               MapStatus status = (MapStatus)event.result();
               String execId = status.location().executorId();
               this.logDebug(new Serializable(this, execId) {
                  public static final long serialVersionUID = 0L;
                  private final String execId$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"ShuffleMapTask finished on ").append((Object)this.execId$1).toString();
                  }

                  public {
                     this.execId$1 = execId$1;
                  }
               });
               if (((Stage)this.stageIdToStage().apply(BoxesRunTime.boxToInteger(task.stageId()))).latestInfo().attemptNumber() == task.stageAttemptId()) {
                  shuffleStage.pendingPartitions().$minus$eq(BoxesRunTime.boxToInteger(task.partitionId()));
               } else {
                  BoxedUnit var62 = BoxedUnit.UNIT;
               }

               if (this.failedEpoch().contains(execId) && var28.epoch() <= BoxesRunTime.unboxToLong(this.failedEpoch().apply(execId))) {
                  this.logInfo(new Serializable(this, execId, var28) {
                     public static final long serialVersionUID = 0L;
                     private final String execId$1;
                     private final ShuffleMapTask x3$3;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ignoring possibly bogus ", " completion from executor ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x3$3, this.execId$1}));
                     }

                     public {
                        this.execId$1 = execId$1;
                        this.x3$3 = x3$3;
                     }
                  });
                  BoxedUnit var63 = BoxedUnit.UNIT;
               } else {
                  this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.registerMapOutput(shuffleStage.shuffleDep().shuffleId(), var28.partitionId(), status);
                  shuffleStage.pendingPartitions().$minus$eq(BoxesRunTime.boxToInteger(task.partitionId()));
               }

               if (this.runningStages().contains(shuffleStage) && shuffleStage.pendingPartitions().isEmpty()) {
                  this.org$apache$spark$scheduler$DAGScheduler$$markStageAsFinished(shuffleStage, this.markStageAsFinished$default$2());
                  this.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "looking for newly runnable stages";
                     }
                  });
                  this.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final DAGScheduler $outer;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"running: ").append((Object)this.$outer.runningStages()).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  this.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final DAGScheduler $outer;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"waiting: ").append((Object)this.$outer.waitingStages()).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  this.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final DAGScheduler $outer;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"failed: ").append((Object)this.$outer.failedStages()).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.incrementEpoch();
                  this.clearCacheLocs();
                  if (shuffleStage.isAvailable()) {
                     if (shuffleStage.mapStageJobs().nonEmpty()) {
                        MapOutputStatistics stats = this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.getStatistics(shuffleStage.shuffleDep());
                        shuffleStage.mapStageJobs().foreach(new Serializable(this, stats) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final DAGScheduler $outer;
                           private final MapOutputStatistics stats$1;

                           public final void apply(ActiveJob job) {
                              this.$outer.markMapStageJobAsFinished(job, this.stats$1);
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.stats$1 = stats$1;
                              }
                           }
                        });
                     }

                     this.submitWaitingChildStages(shuffleStage);
                     BoxedUnit var65 = BoxedUnit.UNIT;
                  } else {
                     this.logInfo(new Serializable(this, shuffleStage) {
                        public static final long serialVersionUID = 0L;
                        private final ShuffleMapStage shuffleStage$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"Resubmitting ").append((Object)this.shuffleStage$1).append((Object)" (").append((Object)this.shuffleStage$1.name()).append((Object)") because some of its tasks had failed: ").append((Object)this.shuffleStage$1.findMissingPartitions().mkString(", ")).toString();
                        }

                        public {
                           this.shuffleStage$1 = shuffleStage$1;
                        }
                     });
                     this.org$apache$spark$scheduler$DAGScheduler$$submitStage(shuffleStage);
                     BoxedUnit var66 = BoxedUnit.UNIT;
                  }
               } else {
                  BoxedUnit var64 = BoxedUnit.UNIT;
               }
            }

            BoxedUnit var18 = BoxedUnit.UNIT;
         }

      } else {
         this.postTaskEnd(event);
      }
   }

   public void handleExecutorLost(String execId, boolean workerLost) {
      boolean fileLost = workerLost || !this.env.blockManager().externalShuffleServiceEnabled();
      this.removeExecutorAndUnregisterOutputs(execId, fileLost, None$.MODULE$, None$.MODULE$);
   }

   private void removeExecutorAndUnregisterOutputs(String execId, boolean fileLost, Option<String> hostToUnregisterOutputs, Option<Object> maybeEpoch) {
      long currentEpoch = BoxesRunTime.unboxToLong(maybeEpoch.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.$outer.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.getEpoch();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      if (!this.failedEpoch().contains(execId) || BoxesRunTime.unboxToLong(this.failedEpoch().apply(execId)) < currentEpoch) {
         this.failedEpoch().update(execId, BoxesRunTime.boxToLong(currentEpoch));
         this.logInfo(new Serializable(this, execId, currentEpoch) {
            public static final long serialVersionUID = 0L;
            private final String execId$2;
            private final long currentEpoch$1;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("Executor lost: %s (epoch %d)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.execId$2, BoxesRunTime.boxToLong(this.currentEpoch$1)}));
            }

            public {
               this.execId$2 = execId$2;
               this.currentEpoch$1 = currentEpoch$1;
            }
         });
         this.blockManagerMaster.removeExecutor(execId);
         if (fileLost) {
            if (hostToUnregisterOutputs instanceof Some) {
               Some var8 = (Some)hostToUnregisterOutputs;
               String host = (String)var8.x();
               this.logInfo(new Serializable(this, currentEpoch, host) {
                  public static final long serialVersionUID = 0L;
                  private final long currentEpoch$1;
                  private final String host$1;

                  public final String apply() {
                     return (new StringOps(Predef$.MODULE$.augmentString("Shuffle files lost for host: %s (epoch %d)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.host$1, BoxesRunTime.boxToLong(this.currentEpoch$1)}));
                  }

                  public {
                     this.currentEpoch$1 = currentEpoch$1;
                     this.host$1 = host$1;
                  }
               });
               this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.removeOutputsOnHost(host);
               BoxedUnit var10 = BoxedUnit.UNIT;
            } else {
               if (!None$.MODULE$.equals(hostToUnregisterOutputs)) {
                  throw new MatchError(hostToUnregisterOutputs);
               }

               this.logInfo(new Serializable(this, execId, currentEpoch) {
                  public static final long serialVersionUID = 0L;
                  private final String execId$2;
                  private final long currentEpoch$1;

                  public final String apply() {
                     return (new StringOps(Predef$.MODULE$.augmentString("Shuffle files lost for executor: %s (epoch %d)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.execId$2, BoxesRunTime.boxToLong(this.currentEpoch$1)}));
                  }

                  public {
                     this.execId$2 = execId$2;
                     this.currentEpoch$1 = currentEpoch$1;
                  }
               });
               this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.removeOutputsOnExecutor(execId);
               BoxedUnit var11 = BoxedUnit.UNIT;
            }

            this.clearCacheLocs();
         } else {
            this.logDebug(new Serializable(this, execId, currentEpoch) {
               public static final long serialVersionUID = 0L;
               private final String execId$2;
               private final long currentEpoch$1;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Additional executor lost message for %s (epoch %d)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.execId$2, BoxesRunTime.boxToLong(this.currentEpoch$1)}));
               }

               public {
                  this.execId$2 = execId$2;
                  this.currentEpoch$1 = currentEpoch$1;
               }
            });
         }
      }

   }

   private Option<Object> removeExecutorAndUnregisterOutputs$default$4() {
      return None$.MODULE$;
   }

   public void handleWorkerRemoved(String workerId, String host, String message) {
      this.logInfo(new Serializable(this, workerId, host) {
         public static final long serialVersionUID = 0L;
         private final String workerId$1;
         private final String host$2;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Shuffle files lost for worker %s on host %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.workerId$1, this.host$2}));
         }

         public {
            this.workerId$1 = workerId$1;
            this.host$2 = host$2;
         }
      });
      this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker.removeOutputsOnHost(host);
      this.clearCacheLocs();
   }

   public void handleExecutorAdded(String execId, String host) {
      if (this.failedEpoch().contains(execId)) {
         this.logInfo(new Serializable(this, host) {
            public static final long serialVersionUID = 0L;
            private final String host$3;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Host added was in lost list earlier: ").append((Object)this.host$3).toString();
            }

            public {
               this.host$3 = host$3;
            }
         });
         this.failedEpoch().$minus$eq(execId);
      }

   }

   public void handleStageCancellation(int stageId, Option<String> reason) {
      Option var3 = this.stageIdToStage().get(BoxesRunTime.boxToInteger(stageId));
      if (var3 instanceof Some) {
         Some var4 = (Some)var3;
         Stage stage = (Stage)var4.x();
         int[] jobsThatUseStage = (int[])stage.jobIds().toArray(ClassTag$.MODULE$.Int());
         Predef$.MODULE$.intArrayOps(jobsThatUseStage).foreach(new Serializable(this, stageId, reason) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DAGScheduler $outer;
            private final int stageId$3;
            private final Option reason$3;

            public final void apply(int jobId) {
               this.apply$mcVI$sp(jobId);
            }

            public void apply$mcVI$sp(int jobId) {
               Option var3 = this.reason$3;
               String reasonStr;
               if (var3 instanceof Some) {
                  Some var4 = (Some)var3;
                  String originalReason = (String)var4.x();
                  reasonStr = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"because ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{originalReason}));
               } else {
                  if (!None$.MODULE$.equals(var3)) {
                     throw new MatchError(var3);
                  }

                  reasonStr = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"because Stage ", " was cancelled"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stageId$3)}));
               }

               this.$outer.handleJobCancellation(jobId, Option$.MODULE$.apply(reasonStr));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.stageId$3 = stageId$3;
                  this.reason$3 = reason$3;
               }
            }
         });
         BoxedUnit var6 = BoxedUnit.UNIT;
      } else {
         if (!None$.MODULE$.equals(var3)) {
            throw new MatchError(var3);
         }

         this.logInfo(new Serializable(this, stageId) {
            public static final long serialVersionUID = 0L;
            private final int stageId$3;

            public final String apply() {
               return (new StringBuilder()).append((Object)"No active jobs to kill for Stage ").append((Object)BoxesRunTime.boxToInteger(this.stageId$3)).toString();
            }

            public {
               this.stageId$3 = stageId$3;
            }
         });
         BoxedUnit var8 = BoxedUnit.UNIT;
      }

   }

   public void handleJobCancellation(int jobId, Option<String> reason) {
      if (this.jobIdToStageIds().contains(BoxesRunTime.boxToInteger(jobId))) {
         this.org$apache$spark$scheduler$DAGScheduler$$failJobAndIndependentStages((ActiveJob)this.jobIdToActiveJob().apply(BoxesRunTime.boxToInteger(jobId)), (new StringOps(Predef$.MODULE$.augmentString("Job %d cancelled %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(jobId), reason.getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "";
            }
         })})), this.failJobAndIndependentStages$default$3());
      } else {
         this.logDebug(new Serializable(this, jobId) {
            public static final long serialVersionUID = 0L;
            private final int jobId$3;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Trying to cancel unregistered job ").append((Object)BoxesRunTime.boxToInteger(this.jobId$3)).toString();
            }

            public {
               this.jobId$3 = jobId$3;
            }
         });
      }

   }

   public void org$apache$spark$scheduler$DAGScheduler$$markStageAsFinished(Stage stage, Option<String> errorMessage) {
      Option var4 = stage.latestInfo().submissionTime();
      String serviceTime;
      if (var4 instanceof Some) {
         Some var5 = (Some)var4;
         long t = BoxesRunTime.unboxToLong(var5.x());
         serviceTime = (new StringOps(Predef$.MODULE$.augmentString("%.03f"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble((double)(this.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis() - t) / (double)1000.0F)}));
      } else {
         serviceTime = "Unknown";
      }

      if (errorMessage.isEmpty()) {
         this.logInfo(new Serializable(this, stage, serviceTime) {
            public static final long serialVersionUID = 0L;
            private final Stage stage$3;
            private final String serviceTime$1;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("%s (%s) finished in %s s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.stage$3, this.stage$3.name(), this.serviceTime$1}));
            }

            public {
               this.stage$3 = stage$3;
               this.serviceTime$1 = serviceTime$1;
            }
         });
         stage.latestInfo().completionTime_$eq(new Some(BoxesRunTime.boxToLong(this.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis())));
         stage.clearFailures();
      } else {
         stage.latestInfo().stageFailed((String)errorMessage.get());
         this.logInfo(new Serializable(this, stage, errorMessage, serviceTime) {
            public static final long serialVersionUID = 0L;
            private final Stage stage$3;
            private final Option errorMessage$1;
            private final String serviceTime$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " (", ") failed in ", " s due to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.stage$3, this.stage$3.name(), this.serviceTime$1, this.errorMessage$1.get()}));
            }

            public {
               this.stage$3 = stage$3;
               this.errorMessage$1 = errorMessage$1;
               this.serviceTime$1 = serviceTime$1;
            }
         });
      }

      this.outputCommitCoordinator().stageEnd(stage.id());
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerStageCompleted(stage.latestInfo()));
      this.runningStages().$minus$eq(stage);
   }

   private Option<String> markStageAsFinished$default$2() {
      return None$.MODULE$;
   }

   public void abortStage(Stage failedStage, String reason, Option<Throwable> exception) {
      if (this.stageIdToStage().contains(BoxesRunTime.boxToInteger(failedStage.id()))) {
         Seq dependentJobs = ((SetLike)this.activeJobs().filter(new Serializable(this, failedStage) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DAGScheduler $outer;
            private final Stage failedStage$1;

            public final boolean apply(ActiveJob job) {
               return this.$outer.org$apache$spark$scheduler$DAGScheduler$$stageDependsOn(job.finalStage(), this.failedStage$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.failedStage$1 = failedStage$1;
               }
            }
         })).toSeq();
         failedStage.latestInfo().completionTime_$eq(new Some(BoxesRunTime.boxToLong(this.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis())));
         dependentJobs.foreach(new Serializable(this, reason, exception) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DAGScheduler $outer;
            private final String reason$2;
            private final Option exception$2;

            public final void apply(ActiveJob job) {
               this.$outer.org$apache$spark$scheduler$DAGScheduler$$failJobAndIndependentStages(job, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Job aborted due to stage failure: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.reason$2})), this.exception$2);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.reason$2 = reason$2;
                  this.exception$2 = exception$2;
               }
            }
         });
         if (dependentJobs.isEmpty()) {
            this.logInfo(new Serializable(this, failedStage) {
               public static final long serialVersionUID = 0L;
               private final Stage failedStage$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Ignoring failure of ").append((Object)this.failedStage$1).append((Object)" because all jobs depending on it are done").toString();
               }

               public {
                  this.failedStage$1 = failedStage$1;
               }
            });
         }

      }
   }

   public void org$apache$spark$scheduler$DAGScheduler$$failJobAndIndependentStages(ActiveJob job, String failureReason, Option<Throwable> exception) {
      SparkException error = new SparkException(failureReason, (Throwable)exception.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Null$ apply() {
            return null;
         }
      }));
      BooleanRef ableToCancelStages = BooleanRef.create(true);
      boolean shouldInterruptThread = job.properties() == null ? false : (new StringOps(Predef$.MODULE$.augmentString(job.properties().getProperty(SparkContext$.MODULE$.SPARK_JOB_INTERRUPT_ON_CANCEL(), "false")))).toBoolean();
      HashSet stages = (HashSet)this.jobIdToStageIds().apply(BoxesRunTime.boxToInteger(job.jobId()));
      if (stages.isEmpty()) {
         this.logError(new Serializable(this, job) {
            public static final long serialVersionUID = 0L;
            private final ActiveJob job$3;

            public final String apply() {
               return (new StringBuilder()).append((Object)"No stages registered for job ").append((Object)BoxesRunTime.boxToInteger(this.job$3.jobId())).toString();
            }

            public {
               this.job$3 = job$3;
            }
         });
      }

      stages.foreach(new Serializable(this, job, failureReason, ableToCancelStages, shouldInterruptThread) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DAGScheduler $outer;
         public final ActiveJob job$3;
         private final String failureReason$1;
         private final BooleanRef ableToCancelStages$1;
         private final boolean shouldInterruptThread$1;

         public final void apply(int stageId) {
            this.apply$mcVI$sp(stageId);
         }

         public void apply$mcVI$sp(int stageId) {
            Option jobsForStage = this.$outer.stageIdToStage().get(BoxesRunTime.boxToInteger(stageId)).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final HashSet<Object> apply(Stage x$19) {
                  return x$19.jobIds();
               }
            });
            if (!jobsForStage.isEmpty() && ((HashSet)jobsForStage.get()).contains(BoxesRunTime.boxToInteger(this.job$3.jobId()))) {
               if (((HashSet)jobsForStage.get()).size() == 1) {
                  if (this.$outer.stageIdToStage().contains(BoxesRunTime.boxToInteger(stageId))) {
                     Stage stage = (Stage)this.$outer.stageIdToStage().apply(BoxesRunTime.boxToInteger(stageId));
                     if (this.$outer.runningStages().contains(stage)) {
                        try {
                           this.$outer.taskScheduler().cancelTasks(stageId, this.shouldInterruptThread$1);
                           this.$outer.org$apache$spark$scheduler$DAGScheduler$$markStageAsFinished(stage, new Some(this.failureReason$1));
                        } catch (UnsupportedOperationException var5) {
                           this.$outer.logInfo(new Serializable(this, stageId) {
                              public static final long serialVersionUID = 0L;
                              private final int stageId$4;

                              public final String apply() {
                                 return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Could not cancel tasks for stage ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stageId$4)}));
                              }

                              public {
                                 this.stageId$4 = stageId$4;
                              }
                           }, var5);
                           this.ableToCancelStages$1.elem = false;
                        }
                     }
                  } else {
                     this.$outer.logError(new Serializable(this, stageId) {
                        public static final long serialVersionUID = 0L;
                        private final int stageId$4;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Missing Stage for stage with id ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stageId$4)}));
                        }

                        public {
                           this.stageId$4 = stageId$4;
                        }
                     });
                  }
               }
            } else {
               this.$outer.logError(new Serializable(this, stageId) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int stageId$4;

                  public final String apply() {
                     return (new StringOps(Predef$.MODULE$.augmentString("Job %d not registered for stage %d even though that stage was registered for the job"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.job$3.jobId()), BoxesRunTime.boxToInteger(this.stageId$4)}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.stageId$4 = stageId$4;
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
               this.job$3 = job$3;
               this.failureReason$1 = failureReason$1;
               this.ableToCancelStages$1 = ableToCancelStages$1;
               this.shouldInterruptThread$1 = shouldInterruptThread$1;
            }
         }
      });
      if (ableToCancelStages.elem) {
         this.cleanupStateForJobAndIndependentStages(job);
         job.listener().jobFailed(error);
         this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerJobEnd(job.jobId(), this.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis(), new JobFailed(error)));
      }

   }

   private Option<Throwable> failJobAndIndependentStages$default$3() {
      return None$.MODULE$;
   }

   public boolean org$apache$spark$scheduler$DAGScheduler$$stageDependsOn(Stage stage, Stage target) {
      if (stage == null) {
         if (target == null) {
            return true;
         }
      } else if (stage.equals(target)) {
         return true;
      }

      HashSet visitedRdds = new HashSet();
      ArrayStack waitingForVisit = new ArrayStack();
      waitingForVisit.push(stage.rdd());

      while(waitingForVisit.nonEmpty()) {
         this.visit$2((RDD)waitingForVisit.pop(), stage, visitedRdds, waitingForVisit);
      }

      return visitedRdds.contains(target.rdd());
   }

   public Seq<TaskLocation> getPreferredLocs(RDD<?> rdd, int partition) {
      return this.org$apache$spark$scheduler$DAGScheduler$$getPreferredLocsInternal(rdd, partition, new HashSet());
   }

   public Seq<TaskLocation> org$apache$spark$scheduler$DAGScheduler$$getPreferredLocsInternal(RDD<?> rdd, int partition, HashSet<Tuple2<RDD<?>, Object>> visited) {
      Object var4 = new Object();

      Object var10000;
      try {
         if (!visited.add(new Tuple2(rdd, BoxesRunTime.boxToInteger(partition)))) {
            return Nil$.MODULE$;
         }

         Seq cached = (Seq)this.getCacheLocs(rdd).apply(partition);
         if (cached.nonEmpty()) {
            return cached;
         }

         List rddPrefs = rdd.preferredLocations(rdd.partitions()[partition]).toList();
         if (rddPrefs.nonEmpty()) {
            return (Seq)rddPrefs.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final TaskLocation apply(String x$20) {
                  return TaskLocation$.MODULE$.apply(x$20);
               }
            }, List$.MODULE$.canBuildFrom());
         }

         rdd.dependencies().foreach(new Serializable(this, partition, visited, var4) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DAGScheduler $outer;
            private final int partition$1;
            public final HashSet visited$2;
            public final Object nonLocalReturnKey1$1;

            public final void apply(Dependency<?> x0$3) {
               if (x0$3 instanceof NarrowDependency) {
                  NarrowDependency var3 = (NarrowDependency)x0$3;
                  var3.getParents(this.partition$1).foreach(new Serializable(this, var3) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final NarrowDependency x2$3;

                     public final void apply(int inPart) {
                        this.apply$mcVI$sp(inPart);
                     }

                     public void apply$mcVI$sp(int inPart) {
                        Seq locs = this.$outer.org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer().org$apache$spark$scheduler$DAGScheduler$$getPreferredLocsInternal(this.x2$3.rdd(), inPart, this.$outer.visited$2);
                        Nil$ var3 = Nil$.MODULE$;
                        if (locs == null) {
                           if (var3 == null) {
                              return;
                           }
                        } else if (locs.equals(var3)) {
                           return;
                        }

                        throw new NonLocalReturnControl(this.$outer.nonLocalReturnKey1$1, locs);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.x2$3 = x2$3;
                        }
                     }
                  });
                  BoxedUnit var4 = BoxedUnit.UNIT;
               } else {
                  BoxedUnit var5 = BoxedUnit.UNIT;
               }

            }

            // $FF: synthetic method
            public DAGScheduler org$apache$spark$scheduler$DAGScheduler$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.partition$1 = partition$1;
                  this.visited$2 = visited$2;
                  this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
               }
            }
         });
         var10000 = Nil$.MODULE$;
      } catch (NonLocalReturnControl var8) {
         if (var8.key() != var4) {
            throw var8;
         }

         var10000 = (Seq)var8.value();
      }

      return (Seq<TaskLocation>)var10000;
   }

   public void markMapStageJobAsFinished(ActiveJob job, MapOutputStatistics stats) {
      job.finished()[0] = true;
      job.numFinished_$eq(job.numFinished() + 1);
      job.listener().taskSucceeded(0, stats);
      this.cleanupStateForJobAndIndependentStages(job);
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus.post(new SparkListenerJobEnd(job.jobId(), this.org$apache$spark$scheduler$DAGScheduler$$clock.getTimeMillis(), JobSucceeded$.MODULE$));
   }

   public void stop() {
      this.messageScheduler().shutdownNow();
      this.eventProcessLoop().stop();
      this.taskScheduler().stop();
   }

   private final void visit$1(RDD rdd, Stage stage$1, HashSet missing$1, HashSet visited$1, ArrayStack waitingForVisit$3) {
      if (!visited$1.apply(rdd)) {
         visited$1.$plus$eq(rdd);
         boolean rddHasUncachedPartitions = this.getCacheLocs(rdd).contains(Nil$.MODULE$);
         if (rddHasUncachedPartitions) {
            rdd.dependencies().foreach(new Serializable(this, stage$1, missing$1, waitingForVisit$3) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DAGScheduler $outer;
               private final Stage stage$1;
               private final HashSet missing$1;
               private final ArrayStack waitingForVisit$3;

               public final Object apply(Dependency<?> dep) {
                  Object var4;
                  if (dep instanceof ShuffleDependency) {
                     ShuffleDependency var3 = (ShuffleDependency)dep;
                     ShuffleMapStage mapStage = this.$outer.org$apache$spark$scheduler$DAGScheduler$$getOrCreateShuffleMapStage(var3, this.stage$1.firstJobId());
                     var4 = mapStage.isAvailable() ? BoxedUnit.UNIT : this.missing$1.$plus$eq(mapStage);
                  } else {
                     if (!(dep instanceof NarrowDependency)) {
                        throw new MatchError(dep);
                     }

                     NarrowDependency var6 = (NarrowDependency)dep;
                     this.waitingForVisit$3.push(var6.rdd());
                     var4 = BoxedUnit.UNIT;
                  }

                  return var4;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.stage$1 = stage$1;
                     this.missing$1 = missing$1;
                     this.waitingForVisit$3 = waitingForVisit$3;
                  }
               }
            });
         }
      }

   }

   private final void updateJobIdStageIdMapsList$1(List stages, int jobId$2) {
      while(stages.nonEmpty()) {
         Stage s = (Stage)stages.head();
         s.jobIds().$plus$eq(BoxesRunTime.boxToInteger(jobId$2));
         ((HashSet)this.jobIdToStageIds().getOrElseUpdate(BoxesRunTime.boxToInteger(jobId$2), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final HashSet<Object> apply() {
               return new HashSet<Object>();
            }
         })).$plus$eq(BoxesRunTime.boxToInteger(s.id()));
         List parentsWithoutThisJobId = (List)s.parents().filter(new Serializable(this, jobId$2) {
            public static final long serialVersionUID = 0L;
            private final int jobId$2;

            public final boolean apply(Stage x$1) {
               return !x$1.jobIds().contains(BoxesRunTime.boxToInteger(this.jobId$2));
            }

            public {
               this.jobId$2 = jobId$2;
            }
         });
         stages = (List)parentsWithoutThisJobId.$plus$plus((GenTraversableOnce)stages.tail(), List$.MODULE$.canBuildFrom());
      }

      BoxedUnit var10000 = BoxedUnit.UNIT;
   }

   private final void visit$2(RDD rdd, Stage stage$6, HashSet visitedRdds$1, ArrayStack waitingForVisit$4) {
      if (!visitedRdds$1.apply(rdd)) {
         visitedRdds$1.$plus$eq(rdd);
         rdd.dependencies().foreach(new Serializable(this, stage$6, waitingForVisit$4) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DAGScheduler $outer;
            private final Stage stage$6;
            private final ArrayStack waitingForVisit$4;

            public final void apply(Dependency<?> dep) {
               if (dep instanceof ShuffleDependency) {
                  ShuffleDependency var3 = (ShuffleDependency)dep;
                  ShuffleMapStage mapStage = this.$outer.org$apache$spark$scheduler$DAGScheduler$$getOrCreateShuffleMapStage(var3, this.stage$6.firstJobId());
                  if (mapStage.isAvailable()) {
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     this.waitingForVisit$4.push(mapStage.rdd());
                     BoxedUnit var7 = BoxedUnit.UNIT;
                  }
               } else {
                  if (!(dep instanceof NarrowDependency)) {
                     throw new MatchError(dep);
                  }

                  NarrowDependency var6 = (NarrowDependency)dep;
                  this.waitingForVisit$4.push(var6.rdd());
                  BoxedUnit var4 = BoxedUnit.UNIT;
               }

            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.stage$6 = stage$6;
                  this.waitingForVisit$4 = waitingForVisit$4;
               }
            }
         });
      }

   }

   public DAGScheduler(SparkContext sc, TaskScheduler taskScheduler, LiveListenerBus listenerBus, MapOutputTrackerMaster mapOutputTracker, BlockManagerMaster blockManagerMaster, SparkEnv env, Clock clock) {
      this.sc = sc;
      this.taskScheduler = taskScheduler;
      this.org$apache$spark$scheduler$DAGScheduler$$listenerBus = listenerBus;
      this.org$apache$spark$scheduler$DAGScheduler$$mapOutputTracker = mapOutputTracker;
      this.blockManagerMaster = blockManagerMaster;
      this.env = env;
      this.org$apache$spark$scheduler$DAGScheduler$$clock = clock;
      Logging$class.$init$(this);
      this.metricsSource = new DAGSchedulerSource(this);
      this.nextJobId = new AtomicInteger(0);
      this.nextStageId = new AtomicInteger(0);
      this.jobIdToStageIds = new HashMap<Object, HashSet<Object>>();
      this.stageIdToStage = new HashMap<Object, Stage>();
      this.shuffleIdToMapStage = new HashMap<Object, ShuffleMapStage>();
      this.jobIdToActiveJob = new HashMap<Object, ActiveJob>();
      this.waitingStages = new HashSet<Stage>();
      this.runningStages = new HashSet<Stage>();
      this.failedStages = new HashSet<Stage>();
      this.activeJobs = new HashSet<ActiveJob>();
      this.cacheLocs = new HashMap<Object, IndexedSeq<Seq<TaskLocation>>>();
      this.failedEpoch = new HashMap<String, Object>();
      this.outputCommitCoordinator = env.outputCommitCoordinator();
      this.closureSerializer = SparkEnv$.MODULE$.get().closureSerializer().newInstance();
      this.disallowStageRetryForTest = sc.getConf().getBoolean("spark.test.noStageRetry", false);
      this.unRegisterOutputOnHostOnFetchFailure = BoxesRunTime.unboxToBoolean(sc.getConf().get(org.apache.spark.internal.config.package$.MODULE$.UNREGISTER_OUTPUT_ON_HOST_ON_FETCH_FAILURE()));
      this.maxConsecutiveStageAttempts = sc.getConf().getInt("spark.stage.maxConsecutiveAttempts", DAGScheduler$.MODULE$.DEFAULT_MAX_CONSECUTIVE_STAGE_ATTEMPTS());
      this.messageScheduler = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("dag-scheduler-message");
      this.eventProcessLoop = new DAGSchedulerEventProcessLoop(this);
      taskScheduler.setDAGScheduler(this);
      this.eventProcessLoop().start();
   }

   public DAGScheduler(SparkContext sc, TaskScheduler taskScheduler) {
      this(sc, taskScheduler, sc.listenerBus(), (MapOutputTrackerMaster)sc.env().mapOutputTracker(), sc.env().blockManager().master(), sc.env(), DAGScheduler$.MODULE$.$lessinit$greater$default$7());
   }

   public DAGScheduler(SparkContext sc) {
      this(sc, sc.taskScheduler());
   }
}
