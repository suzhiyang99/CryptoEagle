package org.apache.spark.status;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.apache.spark.ExceptionFailure;
import org.apache.spark.JobExecutionStatus;
import org.apache.spark.Resubmitted$;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext$;
import org.apache.spark.Success$;
import org.apache.spark.TaskCommitDenied;
import org.apache.spark.TaskEndReason;
import org.apache.spark.TaskFailedReason;
import org.apache.spark.TaskKilled;
import org.apache.spark.TaskState$;
import org.apache.spark.executor.ShuffleReadMetrics;
import org.apache.spark.executor.TaskMetrics$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.scheduler.AccumulableInfo;
import org.apache.spark.scheduler.JobFailed;
import org.apache.spark.scheduler.JobResult;
import org.apache.spark.scheduler.JobSucceeded$;
import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.scheduler.SparkListenerApplicationEnd;
import org.apache.spark.scheduler.SparkListenerApplicationStart;
import org.apache.spark.scheduler.SparkListenerBlockManagerAdded;
import org.apache.spark.scheduler.SparkListenerBlockManagerRemoved;
import org.apache.spark.scheduler.SparkListenerBlockUpdated;
import org.apache.spark.scheduler.SparkListenerEnvironmentUpdate;
import org.apache.spark.scheduler.SparkListenerEvent;
import org.apache.spark.scheduler.SparkListenerExecutorAdded;
import org.apache.spark.scheduler.SparkListenerExecutorBlacklisted;
import org.apache.spark.scheduler.SparkListenerExecutorBlacklistedForStage;
import org.apache.spark.scheduler.SparkListenerExecutorMetricsUpdate;
import org.apache.spark.scheduler.SparkListenerExecutorRemoved;
import org.apache.spark.scheduler.SparkListenerExecutorUnblacklisted;
import org.apache.spark.scheduler.SparkListenerJobEnd;
import org.apache.spark.scheduler.SparkListenerJobStart;
import org.apache.spark.scheduler.SparkListenerLogStart;
import org.apache.spark.scheduler.SparkListenerNodeBlacklisted;
import org.apache.spark.scheduler.SparkListenerNodeBlacklistedForStage;
import org.apache.spark.scheduler.SparkListenerNodeUnblacklisted;
import org.apache.spark.scheduler.SparkListenerStageCompleted;
import org.apache.spark.scheduler.SparkListenerStageSubmitted;
import org.apache.spark.scheduler.SparkListenerTaskEnd;
import org.apache.spark.scheduler.SparkListenerTaskGettingResult;
import org.apache.spark.scheduler.SparkListenerTaskStart;
import org.apache.spark.scheduler.SparkListenerUnpersistRDD;
import org.apache.spark.scheduler.StageInfo;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo$;
import org.apache.spark.status.api.v1.ApplicationEnvironmentInfo;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.status.api.v1.ApplicationInfo$;
import org.apache.spark.status.api.v1.RDDDataDistribution;
import org.apache.spark.status.api.v1.RuntimeInfo;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.status.api.v1.StageStatus;
import org.apache.spark.status.api.v1.TaskMetrics;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.RDDBlockId;
import org.apache.spark.storage.RDDInfo;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.storage.StreamBlockId;
import org.apache.spark.ui.SparkUI$;
import org.apache.spark.ui.scope.RDDOperationCluster;
import org.apache.spark.ui.scope.RDDOperationGraph;
import org.apache.spark.ui.scope.RDDOperationGraph$;
import org.apache.spark.util.kvstore.KVStoreIterator;
import org.apache.spark.util.kvstore.KVStoreView;
import org.slf4j.Logger;
import scala.Function0;
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
import scala.Tuple2$mcII$sp;
import scala.Tuple3;
import scala.Tuple4;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.Map;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.HashMap;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0011Md!B\u0001\u0003\u0001\u0011Q!!E!qaN#\u0018\r^;t\u0019&\u001cH/\u001a8fe*\u00111\u0001B\u0001\u0007gR\fG/^:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c2\u0001A\u0006\u0012!\taq\"D\u0001\u000e\u0015\tqA!A\u0005tG\",G-\u001e7fe&\u0011\u0001#\u0004\u0002\u000e'B\f'o\u001b'jgR,g.\u001a:\u0011\u0005I)R\"A\n\u000b\u0005Q!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005Y\u0019\"a\u0002'pO\u001eLgn\u001a\u0005\t1\u0001\u0011\t\u0011)A\u00055\u000591N^:u_J,7\u0001\u0001\t\u00037qi\u0011AA\u0005\u0003;\t\u0011A#\u00127f[\u0016tG\u000f\u0016:bG.LgnZ*u_J,\u0007\u0002C\u0010\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0011\u0002\t\r|gN\u001a\t\u0003C\tj\u0011\u0001B\u0005\u0003G\u0011\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011\u0015\u0002!\u0011!Q\u0001\n\u0019\nA\u0001\\5wKB\u0011qEK\u0007\u0002Q)\t\u0011&A\u0003tG\u0006d\u0017-\u0003\u0002,Q\t9!i\\8mK\u0006t\u0007\u0002C\u0017\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0018\u0002\u001d1\f7\u000f^+qI\u0006$X\rV5nKB\u0019qeL\u0019\n\u0005AB#AB(qi&|g\u000e\u0005\u0002(e%\u00111\u0007\u000b\u0002\u0005\u0019>tw\rC\u00036\u0001\u0011\u0005a'\u0001\u0004=S:LGO\u0010\u000b\u0006oaJ$h\u000f\t\u00037\u0001AQ\u0001\u0007\u001bA\u0002iAQa\b\u001bA\u0002\u0001BQ!\n\u001bA\u0002\u0019Bq!\f\u001b\u0011\u0002\u0003\u0007a\u0006C\u0004>\u0001\u0001\u0007I\u0011\u0002 \u0002\u0019M\u0004\u0018M]6WKJ\u001c\u0018n\u001c8\u0016\u0003}\u0002\"\u0001Q\"\u000f\u0005\u001d\n\u0015B\u0001\")\u0003\u0019\u0001&/\u001a3fM&\u0011A)\u0012\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\tC\u0003bB$\u0001\u0001\u0004%I\u0001S\u0001\u0011gB\f'o\u001b,feNLwN\\0%KF$\"!\u0013'\u0011\u0005\u001dR\u0015BA&)\u0005\u0011)f.\u001b;\t\u000f53\u0015\u0011!a\u0001\u007f\u0005\u0019\u0001\u0010J\u0019\t\r=\u0003\u0001\u0015)\u0003@\u00035\u0019\b/\u0019:l-\u0016\u00148/[8oA!9\u0011\u000b\u0001a\u0001\n\u0013\u0011\u0016aB1qa&sgm\\\u000b\u0002'B\u0011A+W\u0007\u0002+*\u0011akV\u0001\u0003mFR!\u0001\u0017\u0002\u0002\u0007\u0005\u0004\u0018.\u0003\u0002[+\ny\u0011\t\u001d9mS\u000e\fG/[8o\u0013:4w\u000eC\u0004]\u0001\u0001\u0007I\u0011B/\u0002\u0017\u0005\u0004\b/\u00138g_~#S-\u001d\u000b\u0003\u0013zCq!T.\u0002\u0002\u0003\u00071\u000b\u0003\u0004a\u0001\u0001\u0006KaU\u0001\tCB\u0004\u0018J\u001c4pA!9!\r\u0001a\u0001\n\u0013\u0019\u0017AC1qaN+X.\\1ssV\tA\r\u0005\u0002\u001cK&\u0011aM\u0001\u0002\u000b\u0003B\u00048+^7nCJL\bb\u00025\u0001\u0001\u0004%I![\u0001\u000fCB\u00048+^7nCJLx\fJ3r)\tI%\u000eC\u0004NO\u0006\u0005\t\u0019\u00013\t\r1\u0004\u0001\u0015)\u0003e\u0003-\t\u0007\u000f]*v[6\f'/\u001f\u0011\t\u000f9\u0004\u0001\u0019!C\u0005_\u0006a1m\u001c:fgB+'\u000fV1tWV\t\u0001\u000f\u0005\u0002(c&\u0011!\u000f\u000b\u0002\u0004\u0013:$\bb\u0002;\u0001\u0001\u0004%I!^\u0001\u0011G>\u0014Xm\u001d)feR\u000b7o[0%KF$\"!\u0013<\t\u000f5\u001b\u0018\u0011!a\u0001a\"1\u0001\u0010\u0001Q!\nA\fQbY8sKN\u0004VM\u001d+bg.\u0004\u0003b\u0002>\u0001\u0005\u0004%Ia_\u0001\u0013Y&4X-\u00169eCR,\u0007+\u001a:j_\u0012t5/F\u00012\u0011\u0019i\b\u0001)A\u0005c\u0005\u0019B.\u001b<f+B$\u0017\r^3QKJLw\u000e\u001a(tA!9q\u0010\u0001b\u0001\n\u0013y\u0017\u0001E7bqR\u000b7o[:QKJ\u001cF/Y4f\u0011\u001d\t\u0019\u0001\u0001Q\u0001\nA\f\u0011#\\1y)\u0006\u001c8n\u001d)feN#\u0018mZ3!\u0011!\t9\u0001\u0001b\u0001\n\u0013y\u0017!E7bq\u001e\u0013\u0018\r\u001d5S_>$hj\u001c3fg\"9\u00111\u0002\u0001!\u0002\u0013\u0001\u0018AE7bq\u001e\u0013\u0018\r\u001d5S_>$hj\u001c3fg\u0002B\u0011\"a\u0004\u0001\u0005\u0004%I!!\u0005\u0002\u00151Lg/Z*uC\u001e,7/\u0006\u0002\u0002\u0014AA\u0011QCA\u0012\u0003O\ti#\u0004\u0002\u0002\u0018)!\u0011\u0011DA\u000e\u0003)\u0019wN\\2veJ,g\u000e\u001e\u0006\u0005\u0003;\ty\"\u0001\u0003vi&d'BAA\u0011\u0003\u0011Q\u0017M^1\n\t\u0005\u0015\u0012q\u0003\u0002\u0012\u0007>t7-\u001e:sK:$\b*Y:i\u001b\u0006\u0004\b#B\u0014\u0002*A\u0004\u0018bAA\u0016Q\t1A+\u001e9mKJ\u00022aGA\u0018\u0013\r\t\tD\u0001\u0002\n\u0019&4Xm\u0015;bO\u0016D\u0001\"!\u000e\u0001A\u0003%\u00111C\u0001\fY&4Xm\u0015;bO\u0016\u001c\b\u0005C\u0005\u0002:\u0001\u0011\r\u0011\"\u0003\u0002<\u0005AA.\u001b<f\u0015>\u00147/\u0006\u0002\u0002>A9\u0011qHA%a\u00065SBAA!\u0015\u0011\t\u0019%!\u0012\u0002\u000f5,H/\u00192mK*\u0019\u0011q\t\u0015\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0002L\u0005\u0005#a\u0002%bg\"l\u0015\r\u001d\t\u00047\u0005=\u0013bAA)\u0005\t9A*\u001b<f\u0015>\u0014\u0007\u0002CA+\u0001\u0001\u0006I!!\u0010\u0002\u00131Lg/\u001a&pEN\u0004\u0003\"CA-\u0001\t\u0007I\u0011BA.\u00035a\u0017N^3Fq\u0016\u001cW\u000f^8sgV\u0011\u0011Q\f\t\b\u0003\u007f\tIePA0!\rY\u0012\u0011M\u0005\u0004\u0003G\u0012!\u0001\u0004'jm\u0016,\u00050Z2vi>\u0014\b\u0002CA4\u0001\u0001\u0006I!!\u0018\u0002\u001d1Lg/Z#yK\u000e,Ho\u001c:tA!I\u00111\u000e\u0001C\u0002\u0013%\u0011QN\u0001\nY&4X\rV1tWN,\"!a\u001c\u0011\u000f\u0005}\u0012\u0011J\u0019\u0002rA\u00191$a\u001d\n\u0007\u0005U$A\u0001\u0005MSZ,G+Y:l\u0011!\tI\b\u0001Q\u0001\n\u0005=\u0014A\u00037jm\u0016$\u0016m]6tA!I\u0011Q\u0010\u0001C\u0002\u0013%\u0011qP\u0001\tY&4XM\u0015#EgV\u0011\u0011\u0011\u0011\t\b\u0003\u007f\tI\u0005]AB!\rY\u0012QQ\u0005\u0004\u0003\u000f\u0013!a\u0002'jm\u0016\u0014F\t\u0012\u0005\t\u0003\u0017\u0003\u0001\u0015!\u0003\u0002\u0002\u0006IA.\u001b<f%\u0012#5\u000f\t\u0005\n\u0003\u001f\u0003!\u0019!C\u0005\u0003#\u000bQ\u0001]8pYN,\"!a%\u0011\u000f\u0005}\u0012\u0011J \u0002\u0016B\u00191$a&\n\u0007\u0005e%AA\u0007TG\",G-\u001e7feB{w\u000e\u001c\u0005\t\u0003;\u0003\u0001\u0015!\u0003\u0002\u0014\u00061\u0001o\\8mg\u0002B\u0001\"!)\u0001\u0001\u0004%Ia\\\u0001\u0014C\u000e$\u0018N^3Fq\u0016\u001cW\u000f^8s\u0007>,h\u000e\u001e\u0005\n\u0003K\u0003\u0001\u0019!C\u0005\u0003O\u000bq#Y2uSZ,W\t_3dkR|'oQ8v]R|F%Z9\u0015\u0007%\u000bI\u000b\u0003\u0005N\u0003G\u000b\t\u00111\u0001q\u0011\u001d\ti\u000b\u0001Q!\nA\fA#Y2uSZ,W\t_3dkR|'oQ8v]R\u0004\u0003\u0006BAV\u0003c\u00032aJAZ\u0013\r\t)\f\u000b\u0002\tm>d\u0017\r^5mK\"9\u0011\u0011\u0018\u0001\u0005B\u0005m\u0016\u0001D8o\u001fRDWM]#wK:$HcA%\u0002>\"A\u0011qXA\\\u0001\u0004\t\t-A\u0003fm\u0016tG\u000fE\u0002\r\u0003\u0007L1!!2\u000e\u0005I\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u00163XM\u001c;\t\u000f\u0005%\u0007\u0001\"\u0011\u0002L\u0006\u0011rN\\!qa2L7-\u0019;j_:\u001cF/\u0019:u)\rI\u0015Q\u001a\u0005\t\u0003\u007f\u000b9\r1\u0001\u0002PB\u0019A\"!5\n\u0007\u0005MWBA\u000fTa\u0006\u00148\u000eT5ti\u0016tWM]!qa2L7-\u0019;j_:\u001cF/\u0019:u\u0011\u001d\t9\u000e\u0001C!\u00033\f1c\u001c8F]ZL'o\u001c8nK:$X\u000b\u001d3bi\u0016$2!SAn\u0011!\ty,!6A\u0002\u0005u\u0007c\u0001\u0007\u0002`&\u0019\u0011\u0011]\u0007\u0003=M\u0003\u0018M]6MSN$XM\\3s\u000b:4\u0018N]8o[\u0016tG/\u00169eCR,\u0007bBAs\u0001\u0011\u0005\u0013q]\u0001\u0011_:\f\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8F]\u0012$2!SAu\u0011!\ty,a9A\u0002\u0005-\bc\u0001\u0007\u0002n&\u0019\u0011q^\u0007\u00037M\u0003\u0018M]6MSN$XM\\3s\u0003B\u0004H.[2bi&|g.\u00128e\u0011\u001d\t\u0019\u0010\u0001C!\u0003k\fqb\u001c8Fq\u0016\u001cW\u000f^8s\u0003\u0012$W\r\u001a\u000b\u0004\u0013\u0006]\b\u0002CA`\u0003c\u0004\r!!?\u0011\u00071\tY0C\u0002\u0002~6\u0011!d\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'/\u00113eK\u0012DqA!\u0001\u0001\t\u0003\u0012\u0019!A\tp]\u0016CXmY;u_J\u0014V-\\8wK\u0012$2!\u0013B\u0003\u0011!\ty,a@A\u0002\t\u001d\u0001c\u0001\u0007\u0003\n%\u0019!1B\u0007\u00039M\u0003\u0018M]6MSN$XM\\3s\u000bb,7-\u001e;peJ+Wn\u001c<fI\"9!q\u0002\u0001\u0005B\tE\u0011!F8o\u000bb,7-\u001e;pe\nc\u0017mY6mSN$X\r\u001a\u000b\u0004\u0013\nM\u0001\u0002CA`\u0005\u001b\u0001\rA!\u0006\u0011\u00071\u00119\"C\u0002\u0003\u001a5\u0011\u0001e\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'O\u00117bG.d\u0017n\u001d;fI\"9!Q\u0004\u0001\u0005B\t}\u0011!H8o\u000bb,7-\u001e;pe\nc\u0017mY6mSN$X\r\u001a$peN#\u0018mZ3\u0015\u0007%\u0013\t\u0003\u0003\u0005\u0002@\nm\u0001\u0019\u0001B\u0012!\ra!QE\u0005\u0004\u0005Oi!\u0001K*qCJ\\G*[:uK:,'/\u0012=fGV$xN\u001d\"mC\u000e\\G.[:uK\u00124uN]*uC\u001e,\u0007b\u0002B\u0016\u0001\u0011\u0005#QF\u0001\u001a_:tu\u000eZ3CY\u0006\u001c7\u000e\\5ti\u0016$gi\u001c:Ti\u0006<W\rF\u0002J\u0005_A\u0001\"a0\u0003*\u0001\u0007!\u0011\u0007\t\u0004\u0019\tM\u0012b\u0001B\u001b\u001b\t!3\u000b]1sW2K7\u000f^3oKJtu\u000eZ3CY\u0006\u001c7\u000e\\5ti\u0016$gi\u001c:Ti\u0006<W\rC\u0004\u0003:\u0001!\tEa\u000f\u0002/=tW\t_3dkR|'/\u00168cY\u0006\u001c7\u000e\\5ti\u0016$GcA%\u0003>!A\u0011q\u0018B\u001c\u0001\u0004\u0011y\u0004E\u0002\r\u0005\u0003J1Aa\u0011\u000e\u0005\t\u001a\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u0016CXmY;u_J,fN\u00197bG.d\u0017n\u001d;fI\"9!q\t\u0001\u0005B\t%\u0013!E8o\u001d>$WM\u00117bG.d\u0017n\u001d;fIR\u0019\u0011Ja\u0013\t\u0011\u0005}&Q\ta\u0001\u0005\u001b\u00022\u0001\u0004B(\u0013\r\u0011\t&\u0004\u0002\u001d'B\f'o\u001b'jgR,g.\u001a:O_\u0012,'\t\\1dW2L7\u000f^3e\u0011\u001d\u0011)\u0006\u0001C!\u0005/\n1c\u001c8O_\u0012,WK\u001c2mC\u000e\\G.[:uK\u0012$2!\u0013B-\u0011!\tyLa\u0015A\u0002\tm\u0003c\u0001\u0007\u0003^%\u0019!qL\u0007\u0003=M\u0003\u0018M]6MSN$XM\\3s\u001d>$W-\u00168cY\u0006\u001c7\u000e\\5ti\u0016$\u0007b\u0002B2\u0001\u0011%!QM\u0001\u0016kB$\u0017\r^3CY\u0006\u001c7\u000eT5tiN#\u0018\r^;t)\u0015I%q\rB6\u0011\u001d\u0011IG!\u0019A\u0002}\na!\u001a=fG&#\u0007b\u0002B7\u0005C\u0002\rAJ\u0001\fE2\f7m\u001b7jgR,G\rC\u0004\u0003r\u0001!IAa\u001d\u0002'U\u0004H-\u0019;f\u001d>$WM\u00117bG.d\u0015n\u001d;\u0015\u000b%\u0013)H!\u001f\t\u000f\t]$q\u000ea\u0001\u007f\u0005!\u0001n\\:u\u0011\u001d\u0011iGa\u001cA\u0002\u0019BqA! \u0001\t\u0003\u0012y(\u0001\u0006p]*{'m\u0015;beR$2!\u0013BA\u0011!\tyLa\u001fA\u0002\t\r\u0005c\u0001\u0007\u0003\u0006&\u0019!qQ\u0007\u0003+M\u0003\u0018M]6MSN$XM\\3s\u0015>\u00147\u000b^1si\"9!1\u0012\u0001\u0005\n\t5\u0015A\u00068foJ#Ei\u00149fe\u0006$\u0018n\u001c8DYV\u001cH/\u001a:\u0015\t\t=%Q\u0013\t\u00047\tE\u0015b\u0001BJ\u0005\tQ\"\u000b\u0012#Pa\u0016\u0014\u0018\r^5p]\u000ecWo\u001d;fe^\u0013\u0018\r\u001d9fe\"A!q\u0013BE\u0001\u0004\u0011I*A\u0004dYV\u001cH/\u001a:\u0011\t\tm%QU\u0007\u0003\u0005;SAAa(\u0003\"\u0006)1oY8qK*\u0019!1\u0015\u0003\u0002\u0005UL\u0017\u0002\u0002BT\u0005;\u00131C\u0015#E\u001fB,'/\u0019;j_:\u001cE.^:uKJDqAa+\u0001\t\u0003\u0012i+\u0001\u0005p]*{'-\u00128e)\rI%q\u0016\u0005\t\u0003\u007f\u0013I\u000b1\u0001\u00032B\u0019ABa-\n\u0007\tUVBA\nTa\u0006\u00148\u000eT5ti\u0016tWM\u001d&pE\u0016sG\rC\u0004\u0003:\u0002!\tEa/\u0002!=t7\u000b^1hKN+(-\\5ui\u0016$GcA%\u0003>\"A\u0011q\u0018B\\\u0001\u0004\u0011y\fE\u0002\r\u0005\u0003L1Aa1\u000e\u0005m\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8feN#\u0018mZ3Tk\nl\u0017\u000e\u001e;fI\"9!q\u0019\u0001\u0005B\t%\u0017aC8o)\u0006\u001c8n\u0015;beR$2!\u0013Bf\u0011!\tyL!2A\u0002\t5\u0007c\u0001\u0007\u0003P&\u0019!\u0011[\u0007\u0003-M\u0003\u0018M]6MSN$XM\\3s)\u0006\u001c8n\u0015;beRDqA!6\u0001\t\u0003\u00129.A\np]R\u000b7o[$fiRLgn\u001a*fgVdG\u000fF\u0002J\u00053D\u0001\"a0\u0003T\u0002\u0007!1\u001c\t\u0004\u0019\tu\u0017b\u0001Bp\u001b\tq2\u000b]1sW2K7\u000f^3oKJ$\u0016m]6HKR$\u0018N\\4SKN,H\u000e\u001e\u0005\b\u0005G\u0004A\u0011\tBs\u0003%yg\u000eV1tW\u0016sG\rF\u0002J\u0005OD\u0001\"a0\u0003b\u0002\u0007!\u0011\u001e\t\u0004\u0019\t-\u0018b\u0001Bw\u001b\t!2\u000b]1sW2K7\u000f^3oKJ$\u0016m]6F]\u0012DqA!=\u0001\t\u0003\u0012\u00190\u0001\tp]N#\u0018mZ3D_6\u0004H.\u001a;fIR\u0019\u0011J!>\t\u0011\u0005}&q\u001ea\u0001\u0005o\u00042\u0001\u0004B}\u0013\r\u0011Y0\u0004\u0002\u001c'B\f'o\u001b'jgR,g.\u001a:Ti\u0006<WmQ8na2,G/\u001a3\t\u000f\t}\b\u0001\"\u0011\u0004\u0002\u0005\u0019rN\u001c\"m_\u000e\\W*\u00198bO\u0016\u0014\u0018\t\u001a3fIR\u0019\u0011ja\u0001\t\u0011\u0005}&Q a\u0001\u0007\u000b\u00012\u0001DB\u0004\u0013\r\u0019I!\u0004\u0002\u001f'B\f'o\u001b'jgR,g.\u001a:CY>\u001c7.T1oC\u001e,'/\u00113eK\u0012Dqa!\u0004\u0001\t\u0003\u001ay!A\u000bp]\ncwnY6NC:\fw-\u001a:SK6|g/\u001a3\u0015\u0007%\u001b\t\u0002\u0003\u0005\u0002@\u000e-\u0001\u0019AB\n!\ra1QC\u0005\u0004\u0007/i!\u0001I*qCJ\\G*[:uK:,'O\u00117pG.l\u0015M\\1hKJ\u0014V-\\8wK\u0012Dqaa\u0007\u0001\t\u0003\u001ai\"\u0001\bp]Vs\u0007/\u001a:tSN$(\u000b\u0012#\u0015\u0007%\u001by\u0002\u0003\u0005\u0002@\u000ee\u0001\u0019AB\u0011!\ra11E\u0005\u0004\u0007Ki!!G*qCJ\\G*[:uK:,'/\u00168qKJ\u001c\u0018n\u001d;S\t\u0012Cqa!\u000b\u0001\t\u0003\u001aY#A\fp]\u0016CXmY;u_JlU\r\u001e:jGN,\u0006\u000fZ1uKR\u0019\u0011j!\f\t\u0011\u0005}6q\u0005a\u0001\u0007_\u00012\u0001DB\u0019\u0013\r\u0019\u0019$\u0004\u0002#'B\f'o\u001b'jgR,g.\u001a:Fq\u0016\u001cW\u000f^8s\u001b\u0016$(/[2t+B$\u0017\r^3\t\u000f\r]\u0002\u0001\"\u0011\u0004:\u0005qqN\u001c\"m_\u000e\\W\u000b\u001d3bi\u0016$GcA%\u0004<!A\u0011qXB\u001b\u0001\u0004\u0019i\u0004E\u0002\r\u0007\u007fI1a!\u0011\u000e\u0005e\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\ncwnY6Va\u0012\fG/\u001a3\t\u000f\r\u0015\u0003\u0001\"\u0003\u0004H\u0005)a\r\\;tQR\t\u0011\nC\u0004\u0004L\u0001!\ta!\u0014\u0002\u0019\u0005\u001cG/\u001b<f'R\fw-Z:\u0015\u0005\r=\u0003CBB)\u0007C\u001a9G\u0004\u0003\u0004T\ruc\u0002BB+\u00077j!aa\u0016\u000b\u0007\re\u0013$\u0001\u0004=e>|GOP\u0005\u0002S%\u00191q\f\u0015\u0002\u000fA\f7m[1hK&!11MB3\u0005\r\u0019V-\u001d\u0006\u0004\u0007?B\u0003c\u0001+\u0004j%\u001911N+\u0003\u0013M#\u0018mZ3ECR\f\u0007bBB8\u0001\u0011%1\u0011O\u0001\u000fkB$\u0017\r^3S\t\u0012\u0013En\\2l)\u0015I51OB;\u0011!\tyl!\u001cA\u0002\ru\u0002\u0002CB<\u0007[\u0002\ra!\u001f\u0002\u000b\tdwnY6\u0011\t\rm4\u0011Q\u0007\u0003\u0007{R1aa \u0005\u0003\u001d\u0019Ho\u001c:bO\u0016LAaa!\u0004~\tQ!\u000b\u0012#CY>\u001c7.\u00133\t\u000f\r\u001d\u0005\u0001\"\u0003\u0004\n\u0006\u0019r-\u001a;Pe\u000e\u0013X-\u0019;f\u000bb,7-\u001e;peR1\u0011qLBF\u0007\u001fCqa!$\u0004\u0006\u0002\u0007q(\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012Dqa!%\u0004\u0006\u0002\u0007\u0011'A\u0004bI\u0012$\u0016.\\3\t\u000f\rU\u0005\u0001\"\u0003\u0004\u0018\u0006\tR\u000f\u001d3bi\u0016\u001cFO]3b[\ncwnY6\u0015\u000b%\u001bIja'\t\u0011\u0005}61\u0013a\u0001\u0007{A\u0001b!(\u0004\u0014\u0002\u00071qT\u0001\u0007gR\u0014X-Y7\u0011\t\rm4\u0011U\u0005\u0005\u0007G\u001biHA\u0007TiJ,\u0017-\u001c\"m_\u000e\\\u0017\n\u001a\u0005\b\u0007O\u0003A\u0011BBU\u0003A9W\r^(s\u0007J,\u0017\r^3Ti\u0006<W\r\u0006\u0003\u0002.\r-\u0006\u0002CBW\u0007K\u0003\raa,\u0002\t%tgm\u001c\t\u0004\u0019\rE\u0016bABZ\u001b\tI1\u000b^1hK&sgm\u001c\u0005\b\u0007o\u0003A\u0011BB]\u0003IY\u0017\u000e\u001c7fIR\u000b7o[:Tk6l\u0017M]=\u0015\r\rm6\u0011YBf!\u0015\u00015QX q\u0013\r\u0019y,\u0012\u0002\u0004\u001b\u0006\u0004\b\u0002CBb\u0007k\u0003\ra!2\u0002\rI,\u0017m]8o!\r\t3qY\u0005\u0004\u0007\u0013$!!\u0004+bg.,e\u000e\u001a*fCN|g\u000e\u0003\u0005\u0004N\u000eU\u0006\u0019AB^\u0003)yG\u000eZ*v[6\f'/\u001f\u0005\b\u0007#\u0004A\u0011BBj\u0003\u0019)\b\u000fZ1uKR9\u0011j!6\u0004`\u000e\r\b\u0002CBl\u0007\u001f\u0004\ra!7\u0002\r\u0015tG/\u001b;z!\rY21\\\u0005\u0004\u0007;\u0014!A\u0003'jm\u0016,e\u000e^5us\"91\u0011]Bh\u0001\u0004\t\u0014a\u00018po\"I1Q]Bh!\u0003\u0005\rAJ\u0001\u0005Y\u0006\u001cH\u000fC\u0004\u0004j\u0002!Iaa;\u0002\u00175\f\u0017PY3Va\u0012\fG/\u001a\u000b\u0006\u0013\u000e58q\u001e\u0005\t\u0007/\u001c9\u000f1\u0001\u0004Z\"91\u0011]Bt\u0001\u0004\t\u0004bBBz\u0001\u0011%1Q_\u0001\u000bY&4X-\u00169eCR,G#B%\u0004x\u000ee\b\u0002CBl\u0007c\u0004\ra!7\t\u000f\r\u00058\u0011\u001fa\u0001c!91Q \u0001\u0005\n\r}\u0018\u0001E2mK\u0006tW\u000f]#yK\u000e,Ho\u001c:t)\rIE\u0011\u0001\u0005\b\t\u0007\u0019Y\u00101\u00012\u0003\u0015\u0019w.\u001e8u\u0011\u001d!9\u0001\u0001C\u0005\t\u0013\t1b\u00197fC:,\bOS8cgR\u0019\u0011\nb\u0003\t\u000f\u0011\rAQ\u0001a\u0001c!9Aq\u0002\u0001\u0005\n\u0011E\u0011!D2mK\u0006tW\u000f]*uC\u001e,7\u000fF\u0002J\t'Aq\u0001b\u0001\u0005\u000e\u0001\u0007\u0011\u0007C\u0004\u0005\u0018\u0001!I\u0001\"\u0007\u0002\u0019\rdW-\u00198vaR\u000b7o[:\u0015\u0007%#Y\u0002\u0003\u0005\u0005\u001e\u0011U\u0001\u0019AA\u0017\u0003\u0015\u0019H/Y4f\u0011\u001d!\t\u0003\u0001C\u0005\tG\tac\u00197fC:,\boQ1dQ\u0016$\u0017+^1oi&dWm\u001d\u000b\u0004\u0013\u0012\u0015\u0002\u0002\u0003C\u0014\t?\u0001\r\u0001\"\u000b\u0002\u0011M$\u0018mZ3LKf\u0004Ba\nC\u0016a&\u0019AQ\u0006\u0015\u0003\u000b\u0005\u0013(/Y=\t\u000f\u0011E\u0002\u0001\"\u0003\u00054\u000592-\u00197dk2\fG/\u001a(v[\n,'\u000fV8SK6|g/\u001a\u000b\u0006c\u0011UB\u0011\b\u0005\b\to!y\u00031\u00012\u0003!!\u0017\r^1TSj,\u0007b\u0002C\u001e\t_\u0001\r!M\u0001\re\u0016$\u0018-\u001b8fINK'0\u001a\u0005\n\t\u007f\u0001\u0011\u0013!C\u0005\t\u0003\n\u0001#\u001e9eCR,G\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005\u0011\r#f\u0001\u0014\u0005F-\u0012Aq\t\t\u0005\t\u0013\"\u0019&\u0004\u0002\u0005L)!AQ\nC(\u0003%)hn\u00195fG.,GMC\u0002\u0005R!\n!\"\u00198o_R\fG/[8o\u0013\u0011!)\u0006b\u0013\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cWm\u0002\u0006\u0005Z\t\t\t\u0011#\u0001\u0005\t7\n\u0011#\u00119q'R\fG/^:MSN$XM\\3s!\rYBQ\f\u0004\n\u0003\t\t\t\u0011#\u0001\u0005\t?\u001aB\u0001\"\u0018\u0005bA\u0019q\u0005b\u0019\n\u0007\u0011\u0015\u0004F\u0001\u0004B]f\u0014VM\u001a\u0005\bk\u0011uC\u0011\u0001C5)\t!Y\u0006\u0003\u0006\u0005n\u0011u\u0013\u0013!C\u0001\t_\n1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\"TC\u0001C9U\rqCQ\t"
)
public class AppStatusListener extends SparkListener implements Logging {
   public final ElementTrackingStore org$apache$spark$status$AppStatusListener$$kvstore;
   private final SparkConf conf;
   public final boolean org$apache$spark$status$AppStatusListener$$live;
   private final Option<Object> lastUpdateTime;
   private String sparkVersion;
   private ApplicationInfo appInfo;
   private AppSummary appSummary;
   private int org$apache$spark$status$AppStatusListener$$coresPerTask;
   private final long liveUpdatePeriodNs;
   private final int org$apache$spark$status$AppStatusListener$$maxTasksPerStage;
   private final int org$apache$spark$status$AppStatusListener$$maxGraphRootNodes;
   private final ConcurrentHashMap<Tuple2<Object, Object>, LiveStage> org$apache$spark$status$AppStatusListener$$liveStages;
   private final HashMap<Object, LiveJob> liveJobs;
   private final HashMap<String, LiveExecutor> org$apache$spark$status$AppStatusListener$$liveExecutors;
   private final HashMap<Object, LiveTask> org$apache$spark$status$AppStatusListener$$liveTasks;
   private final HashMap<Object, LiveRDD> org$apache$spark$status$AppStatusListener$$liveRDDs;
   private final HashMap<String, SchedulerPool> org$apache$spark$status$AppStatusListener$$pools;
   private volatile int org$apache$spark$status$AppStatusListener$$activeExecutorCount;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Option<Object> $lessinit$greater$default$4() {
      return AppStatusListener$.MODULE$.$lessinit$greater$default$4();
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

   private String sparkVersion() {
      return this.sparkVersion;
   }

   private void sparkVersion_$eq(String x$1) {
      this.sparkVersion = x$1;
   }

   private ApplicationInfo appInfo() {
      return this.appInfo;
   }

   private void appInfo_$eq(ApplicationInfo x$1) {
      this.appInfo = x$1;
   }

   private AppSummary appSummary() {
      return this.appSummary;
   }

   private void appSummary_$eq(AppSummary x$1) {
      this.appSummary = x$1;
   }

   public int org$apache$spark$status$AppStatusListener$$coresPerTask() {
      return this.org$apache$spark$status$AppStatusListener$$coresPerTask;
   }

   private void org$apache$spark$status$AppStatusListener$$coresPerTask_$eq(int x$1) {
      this.org$apache$spark$status$AppStatusListener$$coresPerTask = x$1;
   }

   private long liveUpdatePeriodNs() {
      return this.liveUpdatePeriodNs;
   }

   public int org$apache$spark$status$AppStatusListener$$maxTasksPerStage() {
      return this.org$apache$spark$status$AppStatusListener$$maxTasksPerStage;
   }

   public int org$apache$spark$status$AppStatusListener$$maxGraphRootNodes() {
      return this.org$apache$spark$status$AppStatusListener$$maxGraphRootNodes;
   }

   public ConcurrentHashMap<Tuple2<Object, Object>, LiveStage> org$apache$spark$status$AppStatusListener$$liveStages() {
      return this.org$apache$spark$status$AppStatusListener$$liveStages;
   }

   private HashMap<Object, LiveJob> liveJobs() {
      return this.liveJobs;
   }

   public HashMap<String, LiveExecutor> org$apache$spark$status$AppStatusListener$$liveExecutors() {
      return this.org$apache$spark$status$AppStatusListener$$liveExecutors;
   }

   public HashMap<Object, LiveTask> org$apache$spark$status$AppStatusListener$$liveTasks() {
      return this.org$apache$spark$status$AppStatusListener$$liveTasks;
   }

   public HashMap<Object, LiveRDD> org$apache$spark$status$AppStatusListener$$liveRDDs() {
      return this.org$apache$spark$status$AppStatusListener$$liveRDDs;
   }

   public HashMap<String, SchedulerPool> org$apache$spark$status$AppStatusListener$$pools() {
      return this.org$apache$spark$status$AppStatusListener$$pools;
   }

   public int org$apache$spark$status$AppStatusListener$$activeExecutorCount() {
      return this.org$apache$spark$status$AppStatusListener$$activeExecutorCount;
   }

   public void org$apache$spark$status$AppStatusListener$$activeExecutorCount_$eq(int x$1) {
      this.org$apache$spark$status$AppStatusListener$$activeExecutorCount = x$1;
   }

   public void onOtherEvent(SparkListenerEvent event) {
      if (event instanceof SparkListenerLogStart) {
         SparkListenerLogStart var3 = (SparkListenerLogStart)event;
         String version = var3.sparkVersion();
         this.sparkVersion_$eq(version);
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else {
         BoxedUnit var6 = BoxedUnit.UNIT;
      }

   }

   public void onApplicationStart(SparkListenerApplicationStart event) {
      Predef$.MODULE$.assert(event.appId().isDefined(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Application without IDs are not supported.";
         }
      });
      ApplicationAttemptInfo attempt = ApplicationAttemptInfo$.MODULE$.apply(event.appAttemptId(), new Date(event.time()), new Date(-1L), new Date(event.time()), -1L, event.sparkUser(), false, this.sparkVersion());
      this.appInfo_$eq(ApplicationInfo$.MODULE$.apply((String)((String)event.appId().get()), (String)event.appName(), None$.MODULE$, None$.MODULE$, None$.MODULE$, None$.MODULE$, (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new ApplicationAttemptInfo[]{attempt}))));
      this.org$apache$spark$status$AppStatusListener$$kvstore.write(new ApplicationInfoWrapper(this.appInfo()));
      this.org$apache$spark$status$AppStatusListener$$kvstore.write(this.appSummary());
      event.driverLogs().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final void apply(Map<String, String> logs) {
            Option driver = this.$outer.org$apache$spark$status$AppStatusListener$$liveExecutors().get(SparkContext$.MODULE$.DRIVER_IDENTIFIER()).orElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final Option<LiveExecutor> apply() {
                  return this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$liveExecutors().get(SparkContext$.MODULE$.LEGACY_DRIVER_IDENTIFIER());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            driver.foreach(new Serializable(this, logs) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final Map logs$1;

               public final void apply(LiveExecutor d) {
                  d.executorLogs_$eq(this.logs$1.toMap(Predef$.MODULE$.$conforms()));
                  this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update(d, System.nanoTime(), this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update$default$3());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.logs$1 = logs$1;
                  }
               }
            });
         }

         // $FF: synthetic method
         public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
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

   public void onEnvironmentUpdate(SparkListenerEnvironmentUpdate event) {
      Map details = event.environmentDetails();
      scala.collection.immutable.Map jvmInfo = (scala.collection.immutable.Map)Predef$.MODULE$.Map().apply((Seq)details.apply("JVM Information"));
      RuntimeInfo runtime = new RuntimeInfo((String)jvmInfo.get("Java Version").orNull(Predef$.MODULE$.$conforms()), (String)jvmInfo.get("Java Home").orNull(Predef$.MODULE$.$conforms()), (String)jvmInfo.get("Scala Version").orNull(Predef$.MODULE$.$conforms()));
      ApplicationEnvironmentInfo envInfo = new ApplicationEnvironmentInfo(runtime, (Seq)details.getOrElse("Spark Properties", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nil$ apply() {
            return Nil$.MODULE$;
         }
      }), (Seq)details.getOrElse("System Properties", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nil$ apply() {
            return Nil$.MODULE$;
         }
      }), (Seq)details.getOrElse("Classpath Entries", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nil$ apply() {
            return Nil$.MODULE$;
         }
      }));
      this.org$apache$spark$status$AppStatusListener$$coresPerTask_$eq(BoxesRunTime.unboxToInt(envInfo.sparkProperties().toMap(Predef$.MODULE$.$conforms()).get("spark.task.cpus").map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$1) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toInt();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return this.$outer.org$apache$spark$status$AppStatusListener$$coresPerTask();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      })));
      this.org$apache$spark$status$AppStatusListener$$kvstore.write(new ApplicationEnvironmentInfoWrapper(envInfo));
   }

   public void onApplicationEnd(SparkListenerApplicationEnd event) {
      ApplicationAttemptInfo old = (ApplicationAttemptInfo)this.appInfo().attempts().head();
      ApplicationAttemptInfo attempt = ApplicationAttemptInfo$.MODULE$.apply(old.attemptId(), old.startTime(), new Date(event.time()), new Date(event.time()), event.time() - old.startTime().getTime(), old.sparkUser(), true, old.appSparkVersion());
      this.appInfo_$eq(ApplicationInfo$.MODULE$.apply((String)this.appInfo().id(), (String)this.appInfo().name(), None$.MODULE$, None$.MODULE$, None$.MODULE$, None$.MODULE$, (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new ApplicationAttemptInfo[]{attempt}))));
      this.org$apache$spark$status$AppStatusListener$$kvstore.write(new ApplicationInfoWrapper(this.appInfo()));
   }

   public void onExecutorAdded(SparkListenerExecutorAdded event) {
      LiveExecutor exec = this.getOrCreateExecutor(event.executorId(), event.time());
      exec.host_$eq(event.executorInfo().executorHost());
      exec.isActive_$eq(true);
      exec.totalCores_$eq(event.executorInfo().totalCores());
      exec.maxTasks_$eq(event.executorInfo().totalCores() / this.org$apache$spark$status$AppStatusListener$$coresPerTask());
      exec.executorLogs_$eq(event.executorInfo().logUrlMap());
      this.org$apache$spark$status$AppStatusListener$$liveUpdate(exec, System.nanoTime());
   }

   public void onExecutorRemoved(SparkListenerExecutorRemoved event) {
      this.org$apache$spark$status$AppStatusListener$$liveExecutors().remove(event.executorId()).foreach(new Serializable(this, event) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final SparkListenerExecutorRemoved event$1;

         public final void apply(LiveExecutor exec) {
            long now = System.nanoTime();
            this.$outer.org$apache$spark$status$AppStatusListener$$activeExecutorCount_$eq(package$.MODULE$.max(0, this.$outer.org$apache$spark$status$AppStatusListener$$activeExecutorCount() - 1));
            exec.isActive_$eq(false);
            exec.removeTime_$eq(new Date(this.event$1.time()));
            exec.removeReason_$eq(this.event$1.reason());
            this.$outer.org$apache$spark$status$AppStatusListener$$update(exec, now, true);
            this.$outer.org$apache$spark$status$AppStatusListener$$liveRDDs().values().foreach(new Serializable(this, now, exec) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final long now$1;
               private final LiveExecutor exec$2;

               public final void apply(LiveRDD rdd) {
                  if (rdd.removeDistribution(this.exec$2)) {
                     this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update(rdd, this.now$1, this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update$default$3());
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.now$1 = now$1;
                     this.exec$2 = exec$2;
                  }
               }
            });
         }

         // $FF: synthetic method
         public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.event$1 = event$1;
            }
         }
      });
   }

   public void onExecutorBlacklisted(SparkListenerExecutorBlacklisted event) {
      this.updateBlackListStatus(event.executorId(), true);
   }

   public void onExecutorBlacklistedForStage(SparkListenerExecutorBlacklistedForStage event) {
      Option$.MODULE$.apply(this.org$apache$spark$status$AppStatusListener$$liveStages().get(new Tuple2$mcII$sp(event.stageId(), event.stageAttemptId()))).foreach(new Serializable(this, event) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final SparkListenerExecutorBlacklistedForStage event$2;

         public final void apply(LiveStage stage) {
            long now = System.nanoTime();
            LiveExecutorStageSummary esummary = stage.executorSummary(this.event$2.executorId());
            esummary.isBlacklisted_$eq(true);
            this.$outer.org$apache$spark$status$AppStatusListener$$maybeUpdate(esummary, now);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.event$2 = event$2;
            }
         }
      });
   }

   public void onNodeBlacklistedForStage(SparkListenerNodeBlacklistedForStage event) {
      long now = System.nanoTime();
      Option$.MODULE$.apply(this.org$apache$spark$status$AppStatusListener$$liveStages().get(new Tuple2$mcII$sp(event.stageId(), event.stageAttemptId()))).foreach(new Serializable(this, event, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         public final SparkListenerNodeBlacklistedForStage event$3;
         public final long now$2;

         public final void apply(LiveStage stage) {
            this.$outer.org$apache$spark$status$AppStatusListener$$liveExecutors().values().foreach(new Serializable(this, stage) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final LiveStage stage$2;

               public final void apply(LiveExecutor exec) {
                  String var10000 = exec.hostname();
                  String var2 = this.$outer.event$3.hostId();
                  if (var10000 == null) {
                     if (var2 != null) {
                        return;
                     }
                  } else if (!var10000.equals(var2)) {
                     return;
                  }

                  LiveExecutorStageSummary esummary = this.stage$2.executorSummary(exec.executorId());
                  esummary.isBlacklisted_$eq(true);
                  this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$maybeUpdate(esummary, this.$outer.now$2);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.stage$2 = stage$2;
                  }
               }
            });
         }

         // $FF: synthetic method
         public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.event$3 = event$3;
               this.now$2 = now$2;
            }
         }
      });
   }

   public void onExecutorUnblacklisted(SparkListenerExecutorUnblacklisted event) {
      this.updateBlackListStatus(event.executorId(), false);
   }

   public void onNodeBlacklisted(SparkListenerNodeBlacklisted event) {
      this.updateNodeBlackList(event.hostId(), true);
   }

   public void onNodeUnblacklisted(SparkListenerNodeUnblacklisted event) {
      this.updateNodeBlackList(event.hostId(), false);
   }

   private void updateBlackListStatus(String execId, boolean blacklisted) {
      this.org$apache$spark$status$AppStatusListener$$liveExecutors().get(execId).foreach(new Serializable(this, blacklisted) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final boolean blacklisted$1;

         public final void apply(LiveExecutor exec) {
            exec.isBlacklisted_$eq(this.blacklisted$1);
            this.$outer.org$apache$spark$status$AppStatusListener$$liveUpdate(exec, System.nanoTime());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blacklisted$1 = blacklisted$1;
            }
         }
      });
   }

   private void updateNodeBlackList(String host, boolean blacklisted) {
      long now = System.nanoTime();
      this.org$apache$spark$status$AppStatusListener$$liveExecutors().values().foreach(new Serializable(this, host, blacklisted, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final String host$1;
         private final boolean blacklisted$2;
         private final long now$3;

         public final void apply(LiveExecutor exec) {
            String var10000 = exec.hostname();
            String var2 = this.host$1;
            if (var10000 == null) {
               if (var2 != null) {
                  return;
               }
            } else if (!var10000.equals(var2)) {
               return;
            }

            exec.isBlacklisted_$eq(this.blacklisted$2);
            this.$outer.org$apache$spark$status$AppStatusListener$$liveUpdate(exec, this.now$3);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.host$1 = host$1;
               this.blacklisted$2 = blacklisted$2;
               this.now$3 = now$3;
            }
         }
      });
   }

   public void onJobStart(SparkListenerJobStart event) {
      long now = System.nanoTime();
      Seq missingStages = (Seq)event.stageInfos().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(StageInfo x$2) {
            return x$2.completionTime().isEmpty();
         }
      });
      int numTasks = BoxesRunTime.unboxToInt(((TraversableOnce)missingStages.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(StageInfo x$3) {
            return x$3.numTasks();
         }
      }, Seq$.MODULE$.canBuildFrom())).sum(Numeric.IntIsIntegral$.MODULE$));
      Option lastStageInfo = ((TraversableLike)event.stageInfos().sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(StageInfo x$4) {
            return x$4.stageId();
         }
      }, Ordering.Int$.MODULE$)).lastOption();
      String lastStageName = (String)lastStageInfo.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(StageInfo x$5) {
            return x$5.name();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "(Unknown Stage Name)";
         }
      });
      Option jobGroup = Option$.MODULE$.apply(event.properties()).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply(Properties p) {
            return Option$.MODULE$.<String>apply(p.getProperty(SparkContext$.MODULE$.SPARK_JOB_GROUP_ID()));
         }
      });
      LiveJob job = new LiveJob(event.jobId(), lastStageName, (Option)(event.time() > 0L ? new Some(new Date(event.time())) : None$.MODULE$), event.stageIds(), jobGroup, numTasks);
      this.liveJobs().put(BoxesRunTime.boxToInteger(event.jobId()), job);
      this.org$apache$spark$status$AppStatusListener$$liveUpdate(job, now);
      event.stageInfos().foreach(new Serializable(this, event, now, job) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final SparkListenerJobStart event$4;
         private final long now$4;
         private final LiveJob job$1;

         public final void apply(StageInfo stageInfo) {
            LiveStage stage = this.$outer.org$apache$spark$status$AppStatusListener$$getOrCreateStage(stageInfo);
            stage.jobs_$eq((Seq)stage.jobs().$colon$plus(this.job$1, Seq$.MODULE$.canBuildFrom()));
            stage.jobIds_$eq((Set)stage.jobIds().$plus(BoxesRunTime.boxToInteger(this.event$4.jobId())));
            this.$outer.org$apache$spark$status$AppStatusListener$$liveUpdate(stage, this.now$4);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.event$4 = event$4;
               this.now$4 = now$4;
               this.job$1 = job$1;
            }
         }
      });
      event.stageInfos().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final void apply(StageInfo stage) {
            RDDOperationGraph graph = RDDOperationGraph$.MODULE$.makeOperationGraph(stage, this.$outer.org$apache$spark$status$AppStatusListener$$maxGraphRootNodes());
            RDDOperationGraphWrapper uigraph = new RDDOperationGraphWrapper(stage.stageId(), graph.edges(), graph.outgoingEdges(), graph.incomingEdges(), this.$outer.org$apache$spark$status$AppStatusListener$$newRDDOperationCluster(graph.rootCluster()));
            this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.write(uigraph);
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

   public RDDOperationClusterWrapper org$apache$spark$status$AppStatusListener$$newRDDOperationCluster(RDDOperationCluster cluster) {
      return new RDDOperationClusterWrapper(cluster.id(), cluster.name(), cluster.childNodes(), (Seq)cluster.childClusters().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final RDDOperationClusterWrapper apply(RDDOperationCluster cluster) {
            return this.$outer.org$apache$spark$status$AppStatusListener$$newRDDOperationCluster(cluster);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom()));
   }

   public void onJobEnd(SparkListenerJobEnd event) {
      this.liveJobs().remove(BoxesRunTime.boxToInteger(event.jobId())).foreach(new Serializable(this, event) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final SparkListenerJobEnd event$5;

         public final void apply(LiveJob job) {
            long now = System.nanoTime();
            Iterator it = this.$outer.org$apache$spark$status$AppStatusListener$$liveStages().entrySet().iterator();

            while(it.hasNext()) {
               java.util.Map.Entry e = (java.util.Map.Entry)it.next();
               if (job.stageIds().contains(BoxesRunTime.boxToInteger(((Tuple2)e.getKey())._1$mcI$sp()))) {
                  LiveStage stage = (LiveStage)e.getValue();
                  stage.status_$eq(StageStatus.SKIPPED);
                  job.skippedStages_$eq((Set)job.skippedStages().$plus(BoxesRunTime.boxToInteger(stage.info().stageId())));
                  job.skippedTasks_$eq(job.skippedTasks() + stage.info().numTasks());
                  it.remove();
                  this.$outer.org$apache$spark$status$AppStatusListener$$update(stage, now, this.$outer.org$apache$spark$status$AppStatusListener$$update$default$3());
               }
            }

            JobResult var7 = this.event$5.jobResult();
            JobExecutionStatus var8;
            if (JobSucceeded$.MODULE$.equals(var7)) {
               var8 = JobExecutionStatus.SUCCEEDED;
            } else {
               if (!(var7 instanceof JobFailed)) {
                  throw new MatchError(var7);
               }

               var8 = JobExecutionStatus.FAILED;
            }

            job.status_$eq(var8);
            job.completionTime_$eq((Option)(this.event$5.time() > 0L ? new Some(new Date(this.event$5.time())) : None$.MODULE$));
            this.$outer.org$apache$spark$status$AppStatusListener$$update(job, now, true);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.event$5 = event$5;
            }
         }
      });
      this.appSummary_$eq(new AppSummary(this.appSummary().numCompletedJobs() + 1, this.appSummary().numCompletedStages()));
      this.org$apache$spark$status$AppStatusListener$$kvstore.write(this.appSummary());
   }

   public void onStageSubmitted(SparkListenerStageSubmitted event) {
      long now = System.nanoTime();
      LiveStage stage = this.org$apache$spark$status$AppStatusListener$$getOrCreateStage(event.stageInfo());
      stage.status_$eq(StageStatus.ACTIVE);
      stage.schedulingPool_$eq((String)Option$.MODULE$.apply(event.properties()).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply(Properties p) {
            return Option$.MODULE$.<String>apply(p.getProperty("spark.scheduler.pool"));
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return SparkUI$.MODULE$.DEFAULT_POOL_NAME();
         }
      }));
      stage.jobs_$eq(((TraversableOnce)this.liveJobs().values().filter(new Serializable(this, event) {
         public static final long serialVersionUID = 0L;
         private final SparkListenerStageSubmitted event$6;

         public final boolean apply(LiveJob x$6) {
            return x$6.stageIds().contains(BoxesRunTime.boxToInteger(this.event$6.stageInfo().stageId()));
         }

         public {
            this.event$6 = event$6;
         }
      })).toSeq());
      stage.jobIds_$eq(((TraversableOnce)stage.jobs().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(LiveJob x$7) {
            return x$7.jobId();
         }
      }, Seq$.MODULE$.canBuildFrom())).toSet());
      stage.description_$eq(Option$.MODULE$.apply(event.properties()).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply(Properties p) {
            return Option$.MODULE$.<String>apply(p.getProperty(SparkContext$.MODULE$.SPARK_JOB_DESCRIPTION()));
         }
      }));
      stage.jobs().foreach(new Serializable(this, event, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final SparkListenerStageSubmitted event$6;
         private final long now$5;

         public final void apply(LiveJob job) {
            job.completedStages_$eq((Set)job.completedStages().$minus(BoxesRunTime.boxToInteger(this.event$6.stageInfo().stageId())));
            job.activeStages_$eq(job.activeStages() + 1);
            this.$outer.org$apache$spark$status$AppStatusListener$$liveUpdate(job, this.now$5);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.event$6 = event$6;
               this.now$5 = now$5;
            }
         }
      });
      SchedulerPool pool = (SchedulerPool)this.org$apache$spark$status$AppStatusListener$$pools().getOrElseUpdate(stage.schedulingPool(), new Serializable(this, stage) {
         public static final long serialVersionUID = 0L;
         private final LiveStage stage$1;

         public final SchedulerPool apply() {
            return new SchedulerPool(this.stage$1.schedulingPool());
         }

         public {
            this.stage$1 = stage$1;
         }
      });
      pool.stageIds_$eq((Set)pool.stageIds().$plus(BoxesRunTime.boxToInteger(event.stageInfo().stageId())));
      this.org$apache$spark$status$AppStatusListener$$update(pool, now, this.org$apache$spark$status$AppStatusListener$$update$default$3());
      event.stageInfo().rddInfos().foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final long now$5;

         public final void apply(RDDInfo info) {
            if (info.storageLevel().isValid()) {
               this.$outer.org$apache$spark$status$AppStatusListener$$liveUpdate((LiveEntity)this.$outer.org$apache$spark$status$AppStatusListener$$liveRDDs().getOrElseUpdate(BoxesRunTime.boxToInteger(info.id()), new Serializable(this, info) {
                  public static final long serialVersionUID = 0L;
                  private final RDDInfo info$1;

                  public final LiveRDD apply() {
                     return new LiveRDD(this.info$1);
                  }

                  public {
                     this.info$1 = info$1;
                  }
               }), this.now$5);
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$5 = now$5;
            }
         }
      });
      this.org$apache$spark$status$AppStatusListener$$liveUpdate(stage, now);
   }

   public void onTaskStart(SparkListenerTaskStart event) {
      long now = System.nanoTime();
      LiveTask task = new LiveTask(event.taskInfo(), event.stageId(), event.stageAttemptId(), this.lastUpdateTime);
      this.org$apache$spark$status$AppStatusListener$$liveTasks().put(BoxesRunTime.boxToLong(event.taskInfo().taskId()), task);
      this.org$apache$spark$status$AppStatusListener$$liveUpdate(task, now);
      Option$.MODULE$.apply(this.org$apache$spark$status$AppStatusListener$$liveStages().get(new Tuple2$mcII$sp(event.stageId(), event.stageAttemptId()))).foreach(new Serializable(this, event, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final SparkListenerTaskStart event$7;
         public final long now$6;

         public final void apply(LiveStage stage) {
            stage.activeTasks_$eq(stage.activeTasks() + 1);
            stage.firstLaunchTime_$eq(package$.MODULE$.min(stage.firstLaunchTime(), this.event$7.taskInfo().launchTime()));
            String locality = this.event$7.taskInfo().taskLocality().toString();
            long count = BoxesRunTime.unboxToLong(stage.localitySummary().getOrElse(locality, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return 0L;
               }
            })) + 1L;
            stage.localitySummary_$eq(stage.localitySummary().$plus$plus(Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(locality), BoxesRunTime.boxToLong(count))}))));
            this.$outer.org$apache$spark$status$AppStatusListener$$maybeUpdate(stage, this.now$6);
            stage.jobs().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(LiveJob job) {
                  job.activeTasks_$eq(job.activeTasks() + 1);
                  this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$maybeUpdate(job, this.$outer.now$6);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            if (stage.savedTasks().incrementAndGet() > this.$outer.org$apache$spark$status$AppStatusListener$$maxTasksPerStage() && !stage.cleaning()) {
               stage.cleaning_$eq(true);
               this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.doAsync(new Serializable(this, stage) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final LiveStage stage$3;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$cleanupTasks(this.stage$3);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.stage$3 = stage$3;
                     }
                  }
               });
            }

         }

         // $FF: synthetic method
         public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.event$7 = event$7;
               this.now$6 = now$6;
            }
         }
      });
      this.org$apache$spark$status$AppStatusListener$$liveExecutors().get(event.taskInfo().executorId()).foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final long now$6;

         public final void apply(LiveExecutor exec) {
            exec.activeTasks_$eq(exec.activeTasks() + 1);
            exec.totalTasks_$eq(exec.totalTasks() + 1);
            this.$outer.org$apache$spark$status$AppStatusListener$$maybeUpdate(exec, this.now$6);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$6 = now$6;
            }
         }
      });
   }

   public void onTaskGettingResult(SparkListenerTaskGettingResult event) {
      this.org$apache$spark$status$AppStatusListener$$liveTasks().get(BoxesRunTime.boxToLong(event.taskInfo().taskId())).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final void apply(LiveTask task) {
            this.$outer.org$apache$spark$status$AppStatusListener$$maybeUpdate(task, System.nanoTime());
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

   public void onTaskEnd(SparkListenerTaskEnd event) {
      if (event.taskInfo() != null) {
         long now = System.nanoTime();
         TaskMetrics metricsDelta = (TaskMetrics)this.org$apache$spark$status$AppStatusListener$$liveTasks().remove(BoxesRunTime.boxToLong(event.taskInfo().taskId())).map(new Serializable(this, event, now) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final AppStatusListener $outer;
            private final SparkListenerTaskEnd event$8;
            private final long now$7;

            public final TaskMetrics apply(LiveTask task) {
               task.info_$eq(this.event$8.taskInfo());
               TaskEndReason var3 = this.event$8.reason();
               Object errorMessage;
               if (Success$.MODULE$.equals(var3)) {
                  errorMessage = None$.MODULE$;
               } else if (var3 instanceof TaskKilled) {
                  TaskKilled var5 = (TaskKilled)var3;
                  errorMessage = new Some(var5.reason());
               } else if (var3 instanceof ExceptionFailure) {
                  ExceptionFailure var6 = (ExceptionFailure)var3;
                  errorMessage = new Some(var6.toErrorString());
               } else if (var3 instanceof TaskFailedReason) {
                  TaskFailedReason var7 = (TaskFailedReason)var3;
                  errorMessage = new Some(var7.toErrorString());
               } else {
                  this.$outer.logInfo(new Serializable(this, var3) {
                     public static final long serialVersionUID = 0L;
                     private final TaskEndReason x1$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unhandled task end reason: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.x1$1}));
                     }

                     public {
                        this.x1$1 = x1$1;
                     }
                  });
                  errorMessage = None$.MODULE$;
               }

               task.errorMessage_$eq((Option)errorMessage);
               TaskMetrics delta = task.updateMetrics(this.event$8.taskMetrics());
               this.$outer.org$apache$spark$status$AppStatusListener$$update(task, this.now$7, true);
               return delta;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.event$8 = event$8;
                  this.now$7 = now$7;
               }
            }
         }).orNull(Predef$.MODULE$.$conforms());
         TaskEndReason var7 = event.reason();
         Tuple3 var8;
         if (Success$.MODULE$.equals(var7)) {
            var8 = new Tuple3(BoxesRunTime.boxToInteger(1), BoxesRunTime.boxToInteger(0), BoxesRunTime.boxToInteger(0));
         } else if (var7 instanceof TaskKilled) {
            var8 = new Tuple3(BoxesRunTime.boxToInteger(0), BoxesRunTime.boxToInteger(0), BoxesRunTime.boxToInteger(1));
         } else if (var7 instanceof TaskCommitDenied) {
            var8 = new Tuple3(BoxesRunTime.boxToInteger(0), BoxesRunTime.boxToInteger(0), BoxesRunTime.boxToInteger(1));
         } else {
            var8 = new Tuple3(BoxesRunTime.boxToInteger(0), BoxesRunTime.boxToInteger(1), BoxesRunTime.boxToInteger(0));
         }

         if (var8 != null) {
            int completedDelta = BoxesRunTime.unboxToInt(var8._1());
            int failedDelta = BoxesRunTime.unboxToInt(var8._2());
            int killedDelta = BoxesRunTime.unboxToInt(var8._3());
            Tuple3 var12 = new Tuple3(BoxesRunTime.boxToInteger(completedDelta), BoxesRunTime.boxToInteger(failedDelta), BoxesRunTime.boxToInteger(killedDelta));
            int completedDelta = BoxesRunTime.unboxToInt(var12._1());
            int failedDelta = BoxesRunTime.unboxToInt(var12._2());
            int killedDelta = BoxesRunTime.unboxToInt(var12._3());
            Option$.MODULE$.apply(this.org$apache$spark$status$AppStatusListener$$liveStages().get(new Tuple2$mcII$sp(event.stageId(), event.stageAttemptId()))).foreach(new Serializable(this, event, now, metricsDelta, completedDelta, failedDelta, killedDelta) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final AppStatusListener $outer;
               public final SparkListenerTaskEnd event$8;
               public final long now$7;
               private final TaskMetrics metricsDelta$1;
               public final int completedDelta$1;
               public final int failedDelta$1;
               public final int killedDelta$1;

               public final void apply(LiveStage stage) {
                  if (this.metricsDelta$1 != null) {
                     stage.metrics_$eq(LiveEntityHelpers$.MODULE$.addMetrics(stage.metrics(), this.metricsDelta$1));
                  }

                  stage.activeTasks_$eq(stage.activeTasks() - 1);
                  stage.completedTasks_$eq(stage.completedTasks() + this.completedDelta$1);
                  if (this.completedDelta$1 > 0) {
                     stage.completedIndices().add$mcI$sp(this.event$8.taskInfo().index());
                  }

                  stage.failedTasks_$eq(stage.failedTasks() + this.failedDelta$1);
                  stage.killedTasks_$eq(stage.killedTasks() + this.killedDelta$1);
                  if (this.killedDelta$1 > 0) {
                     stage.killedSummary_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$killedTasksSummary(this.event$8.reason(), stage.killedSummary()));
                  }

                  this.$outer.org$apache$spark$status$AppStatusListener$$maybeUpdate(stage, this.now$7);
                  long taskIndex = (long)this.event$8.stageId() << 32 | (long)this.event$8.taskInfo().index();
                  stage.jobs().foreach(new Serializable(this, taskIndex) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final long taskIndex$1;

                     public final void apply(LiveJob job) {
                        job.activeTasks_$eq(job.activeTasks() - 1);
                        job.completedTasks_$eq(job.completedTasks() + this.$outer.completedDelta$1);
                        if (this.$outer.completedDelta$1 > 0) {
                           job.completedIndices().add$mcJ$sp(this.taskIndex$1);
                        }

                        job.failedTasks_$eq(job.failedTasks() + this.$outer.failedDelta$1);
                        job.killedTasks_$eq(job.killedTasks() + this.$outer.killedDelta$1);
                        if (this.$outer.killedDelta$1 > 0) {
                           job.killedSummary_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$killedTasksSummary(this.$outer.event$8.reason(), job.killedSummary()));
                        }

                        this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$maybeUpdate(job, this.$outer.now$7);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.taskIndex$1 = taskIndex$1;
                        }
                     }
                  });
                  LiveExecutorStageSummary esummary = stage.executorSummary(this.event$8.taskInfo().executorId());
                  esummary.taskTime_$eq(esummary.taskTime() + this.event$8.taskInfo().duration());
                  esummary.succeededTasks_$eq(esummary.succeededTasks() + this.completedDelta$1);
                  esummary.failedTasks_$eq(esummary.failedTasks() + this.failedDelta$1);
                  esummary.killedTasks_$eq(esummary.killedTasks() + this.killedDelta$1);
                  if (this.metricsDelta$1 != null) {
                     esummary.metrics_$eq(LiveEntityHelpers$.MODULE$.addMetrics(esummary.metrics(), this.metricsDelta$1));
                  }

                  this.$outer.org$apache$spark$status$AppStatusListener$$maybeUpdate(esummary, this.now$7);
                  if (!stage.cleaning() && stage.savedTasks().get() > this.$outer.org$apache$spark$status$AppStatusListener$$maxTasksPerStage()) {
                     stage.cleaning_$eq(true);
                     this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.doAsync(new Serializable(this, stage) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final LiveStage stage$4;

                        public final void apply() {
                           this.apply$mcV$sp();
                        }

                        public void apply$mcV$sp() {
                           this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$cleanupTasks(this.stage$4);
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.stage$4 = stage$4;
                           }
                        }
                     });
                  }

               }

               // $FF: synthetic method
               public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.event$8 = event$8;
                     this.now$7 = now$7;
                     this.metricsDelta$1 = metricsDelta$1;
                     this.completedDelta$1 = completedDelta$1;
                     this.failedDelta$1 = failedDelta$1;
                     this.killedDelta$1 = killedDelta$1;
                  }
               }
            });
            this.org$apache$spark$status$AppStatusListener$$liveExecutors().get(event.taskInfo().executorId()).foreach(new Serializable(this, event, now, completedDelta, failedDelta) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final AppStatusListener $outer;
               private final SparkListenerTaskEnd event$8;
               private final long now$7;
               private final int completedDelta$1;
               private final int failedDelta$1;

               public final void apply(LiveExecutor exec) {
                  label21: {
                     exec.activeTasks_$eq(exec.activeTasks() - 1);
                     exec.completedTasks_$eq(exec.completedTasks() + this.completedDelta$1);
                     exec.failedTasks_$eq(exec.failedTasks() + this.failedDelta$1);
                     exec.totalDuration_$eq(exec.totalDuration() + this.event$8.taskInfo().duration());
                     TaskEndReason var10000 = this.event$8.reason();
                     Resubmitted$ var2 = Resubmitted$.MODULE$;
                     if (var10000 == null) {
                        if (var2 == null) {
                           break label21;
                        }
                     } else if (var10000.equals(var2)) {
                        break label21;
                     }

                     if (this.event$8.taskMetrics() != null) {
                        ShuffleReadMetrics readMetrics = this.event$8.taskMetrics().shuffleReadMetrics();
                        exec.totalGcTime_$eq(exec.totalGcTime() + this.event$8.taskMetrics().jvmGCTime());
                        exec.totalInputBytes_$eq(exec.totalInputBytes() + this.event$8.taskMetrics().inputMetrics().bytesRead());
                        exec.totalShuffleRead_$eq(exec.totalShuffleRead() + readMetrics.localBytesRead() + readMetrics.remoteBytesRead());
                        exec.totalShuffleWrite_$eq(exec.totalShuffleWrite() + this.event$8.taskMetrics().shuffleWriteMetrics().bytesWritten());
                     }
                  }

                  if (exec.activeTasks() == 0) {
                     this.$outer.org$apache$spark$status$AppStatusListener$$liveUpdate(exec, this.now$7);
                  } else {
                     this.$outer.org$apache$spark$status$AppStatusListener$$maybeUpdate(exec, this.now$7);
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.event$8 = event$8;
                     this.now$7 = now$7;
                     this.completedDelta$1 = completedDelta$1;
                     this.failedDelta$1 = failedDelta$1;
                  }
               }
            });
         } else {
            throw new MatchError(var8);
         }
      }
   }

   public void onStageCompleted(SparkListenerStageCompleted event) {
      Option maybeStage = Option$.MODULE$.apply(this.org$apache$spark$status$AppStatusListener$$liveStages().remove(new Tuple2$mcII$sp(event.stageInfo().stageId(), event.stageInfo().attemptNumber())));
      maybeStage.foreach(new Serializable(this, event) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         public final SparkListenerStageCompleted event$9;

         public final void apply(LiveStage stage) {
            long now = System.nanoTime();
            stage.info_$eq(this.event$9.stageInfo());
            Option var4 = this.event$9.stageInfo().failureReason();
            StageStatus var5;
            if (var4 instanceof Some) {
               var5 = StageStatus.FAILED;
            } else if (this.event$9.stageInfo().submissionTime().isDefined()) {
               var5 = StageStatus.COMPLETE;
            } else {
               var5 = StageStatus.SKIPPED;
            }

            stage.status_$eq(var5);
            stage.jobs().foreach(new Serializable(this, now, stage) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final long now$8;
               private final LiveStage stage$5;

               public final void apply(LiveJob job) {
                  StageStatus var2 = this.stage$5.status();
                  if (StageStatus.COMPLETE.equals(var2)) {
                     job.completedStages_$eq((Set)job.completedStages().$plus(BoxesRunTime.boxToInteger(this.$outer.event$9.stageInfo().stageId())));
                     BoxedUnit var3 = BoxedUnit.UNIT;
                  } else if (StageStatus.SKIPPED.equals(var2)) {
                     job.skippedStages_$eq((Set)job.skippedStages().$plus(BoxesRunTime.boxToInteger(this.$outer.event$9.stageInfo().stageId())));
                     job.skippedTasks_$eq(job.skippedTasks() + this.$outer.event$9.stageInfo().numTasks());
                     BoxedUnit var4 = BoxedUnit.UNIT;
                  } else {
                     job.failedStages_$eq(job.failedStages() + 1);
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  }

                  job.activeStages_$eq(job.activeStages() - 1);
                  this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$liveUpdate(job, this.now$8);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.now$8 = now$8;
                     this.stage$5 = stage$5;
                  }
               }
            });
            this.$outer.org$apache$spark$status$AppStatusListener$$pools().get(stage.schedulingPool()).foreach(new Serializable(this, now) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final long now$8;

               public final void apply(SchedulerPool pool) {
                  pool.stageIds_$eq((Set)pool.stageIds().$minus(BoxesRunTime.boxToInteger(this.$outer.event$9.stageInfo().stageId())));
                  this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update(pool, this.now$8, this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update$default$3());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.now$8 = now$8;
                  }
               }
            });
            stage.executorSummaries().values().foreach(new Serializable(this, now) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final long now$8;

               public final void apply(LiveExecutorStageSummary x$9) {
                  this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update(x$9, this.now$8, this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update$default$3());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.now$8 = now$8;
                  }
               }
            });
            this.$outer.org$apache$spark$status$AppStatusListener$$update(stage, now, true);
         }

         // $FF: synthetic method
         public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.event$9 = event$9;
            }
         }
      });
      this.appSummary_$eq(new AppSummary(this.appSummary().numCompletedJobs(), this.appSummary().numCompletedStages() + 1));
      this.org$apache$spark$status$AppStatusListener$$kvstore.write(this.appSummary());
   }

   public void onBlockManagerAdded(SparkListenerBlockManagerAdded event) {
      LiveExecutor exec = this.getOrCreateExecutor(event.blockManagerId().executorId(), event.time());
      exec.hostPort_$eq(event.blockManagerId().hostPort());
      event.maxOnHeapMem().foreach(new Serializable(this, event, exec) {
         public static final long serialVersionUID = 0L;
         private final SparkListenerBlockManagerAdded event$10;
         private final LiveExecutor exec$1;

         public final void apply(long x$10) {
            this.apply$mcVJ$sp(x$10);
         }

         public void apply$mcVJ$sp(long x$10) {
            this.exec$1.totalOnHeap_$eq(BoxesRunTime.unboxToLong(this.event$10.maxOnHeapMem().get()));
            this.exec$1.totalOffHeap_$eq(BoxesRunTime.unboxToLong(this.event$10.maxOffHeapMem().get()));
         }

         public {
            this.event$10 = event$10;
            this.exec$1 = exec$1;
         }
      });
      exec.isActive_$eq(true);
      exec.maxMemory_$eq(event.maxMem());
      this.org$apache$spark$status$AppStatusListener$$liveUpdate(exec, System.nanoTime());
   }

   public void onBlockManagerRemoved(SparkListenerBlockManagerRemoved event) {
   }

   public void onUnpersistRDD(SparkListenerUnpersistRDD event) {
      this.org$apache$spark$status$AppStatusListener$$liveRDDs().remove(BoxesRunTime.boxToInteger(event.rddId()));
      this.org$apache$spark$status$AppStatusListener$$kvstore.delete(RDDStorageInfoWrapper.class, BoxesRunTime.boxToInteger(event.rddId()));
   }

   public void onExecutorMetricsUpdate(SparkListenerExecutorMetricsUpdate event) {
      long now = System.nanoTime();
      event.accumUpdates().foreach(new Serializable(this, event, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         public final SparkListenerExecutorMetricsUpdate event$11;
         public final long now$9;

         public final void apply(Tuple4<Object, Object, Object, Seq<AccumulableInfo>> x0$1) {
            if (x0$1 != null) {
               long taskId = BoxesRunTime.unboxToLong(x0$1._1());
               int sid = BoxesRunTime.unboxToInt(x0$1._2());
               int sAttempt = BoxesRunTime.unboxToInt(x0$1._3());
               Seq accumUpdates = (Seq)x0$1._4();
               this.$outer.org$apache$spark$status$AppStatusListener$$liveTasks().get(BoxesRunTime.boxToLong(taskId)).foreach(new Serializable(this, sid, sAttempt, accumUpdates) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int sid$1;
                  private final int sAttempt$1;
                  private final Seq accumUpdates$1;

                  public final void apply(LiveTask task) {
                     org.apache.spark.executor.TaskMetrics metrics = TaskMetrics$.MODULE$.fromAccumulatorInfos(this.accumUpdates$1);
                     TaskMetrics delta = task.updateMetrics(metrics);
                     this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$maybeUpdate(task, this.$outer.now$9);
                     Option$.MODULE$.apply(this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$liveStages().get(new Tuple2$mcII$sp(this.sid$1, this.sAttempt$1))).foreach(new Serializable(this, delta) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final TaskMetrics delta$1;

                        public final void apply(LiveStage stage) {
                           stage.metrics_$eq(LiveEntityHelpers$.MODULE$.addMetrics(stage.metrics(), this.delta$1));
                           this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$maybeUpdate(stage, this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer().now$9);
                           LiveExecutorStageSummary esummary = stage.executorSummary(this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer().event$11.execId());
                           esummary.metrics_$eq(LiveEntityHelpers$.MODULE$.addMetrics(esummary.metrics(), this.delta$1));
                           this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$maybeUpdate(esummary, this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer().now$9);
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.delta$1 = delta$1;
                           }
                        }
                     });
                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.sid$1 = sid$1;
                        this.sAttempt$1 = sAttempt$1;
                        this.accumUpdates$1 = accumUpdates$1;
                     }
                  }
               });
               BoxedUnit var8 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x0$1);
            }
         }

         // $FF: synthetic method
         public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.event$11 = event$11;
               this.now$9 = now$9;
            }
         }
      });
   }

   public void onBlockUpdated(SparkListenerBlockUpdated event) {
      BlockId var2 = event.blockUpdatedInfo().blockId();
      if (var2 instanceof RDDBlockId) {
         RDDBlockId var3 = (RDDBlockId)var2;
         this.updateRDDBlock(event, var3);
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else if (var2 instanceof StreamBlockId) {
         StreamBlockId var5 = (StreamBlockId)var2;
         this.updateStreamBlock(event, var5);
         BoxedUnit var6 = BoxedUnit.UNIT;
      } else {
         BoxedUnit var7 = BoxedUnit.UNIT;
      }

   }

   public void org$apache$spark$status$AppStatusListener$$flush() {
      long now = System.nanoTime();
      ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.org$apache$spark$status$AppStatusListener$$liveStages().values()).asScala()).foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         public final long now$10;

         public final void apply(LiveStage stage) {
            this.$outer.org$apache$spark$status$AppStatusListener$$update(stage, this.now$10, this.$outer.org$apache$spark$status$AppStatusListener$$update$default$3());
            stage.executorSummaries().values().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(LiveExecutorStageSummary x$11) {
                  this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update(x$11, this.$outer.now$10, this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update$default$3());
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
         public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$10 = now$10;
            }
         }
      });
      this.liveJobs().values().foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final long now$10;

         public final void apply(LiveJob x$12) {
            this.$outer.org$apache$spark$status$AppStatusListener$$update(x$12, this.now$10, this.$outer.org$apache$spark$status$AppStatusListener$$update$default$3());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$10 = now$10;
            }
         }
      });
      this.org$apache$spark$status$AppStatusListener$$liveExecutors().values().foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final long now$10;

         public final void apply(LiveExecutor x$13) {
            this.$outer.org$apache$spark$status$AppStatusListener$$update(x$13, this.now$10, this.$outer.org$apache$spark$status$AppStatusListener$$update$default$3());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$10 = now$10;
            }
         }
      });
      this.org$apache$spark$status$AppStatusListener$$liveTasks().values().foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final long now$10;

         public final void apply(LiveTask x$14) {
            this.$outer.org$apache$spark$status$AppStatusListener$$update(x$14, this.now$10, this.$outer.org$apache$spark$status$AppStatusListener$$update$default$3());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$10 = now$10;
            }
         }
      });
      this.org$apache$spark$status$AppStatusListener$$liveRDDs().values().foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final long now$10;

         public final void apply(LiveRDD x$15) {
            this.$outer.org$apache$spark$status$AppStatusListener$$update(x$15, this.now$10, this.$outer.org$apache$spark$status$AppStatusListener$$update$default$3());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$10 = now$10;
            }
         }
      });
      this.org$apache$spark$status$AppStatusListener$$pools().values().foreach(new Serializable(this, now) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final long now$10;

         public final void apply(SchedulerPool x$16) {
            this.$outer.org$apache$spark$status$AppStatusListener$$update(x$16, this.now$10, this.$outer.org$apache$spark$status$AppStatusListener$$update$default$3());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$10 = now$10;
            }
         }
      });
   }

   public Seq<StageData> activeStages() {
      return (Seq)((TraversableOnce)((TraversableLike)((TraversableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.org$apache$spark$status$AppStatusListener$$liveStages().values()).asScala()).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(LiveStage x$17) {
            return x$17.info().submissionTime().isDefined();
         }
      })).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final StageData apply(LiveStage x$18) {
            return x$18.toApi();
         }
      }, Iterable$.MODULE$.canBuildFrom())).toList().sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(StageData x$19) {
            return x$19.stageId();
         }
      }, Ordering.Int$.MODULE$);
   }

   private void updateRDDBlock(SparkListenerBlockUpdated event, RDDBlockId block) {
      long now = System.nanoTime();
      String executorId = event.blockUpdatedInfo().blockManagerId().executorId();
      StorageLevel storageLevel = event.blockUpdatedInfo().storageLevel();
      long diskDelta = event.blockUpdatedInfo().diskSize() * (long)(storageLevel.useDisk() ? 1 : -1);
      long memoryDelta = event.blockUpdatedInfo().memSize() * (long)(storageLevel.useMemory() ? 1 : -1);
      Option updatedStorageLevel = (Option)(storageLevel.isValid() ? new Some(storageLevel.description()) : None$.MODULE$);
      Option maybeExec = this.org$apache$spark$status$AppStatusListener$$liveExecutors().get(executorId);
      IntRef rddBlocksDelta = IntRef.create(0);
      maybeExec.foreach(new Serializable(this, storageLevel, diskDelta, memoryDelta) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final StorageLevel storageLevel$1;
         private final long diskDelta$1;
         private final long memoryDelta$1;

         public final void apply(LiveExecutor exec) {
            if (exec.hasMemoryInfo()) {
               if (this.storageLevel$1.useOffHeap()) {
                  exec.usedOffHeap_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$newValue$1(exec.usedOffHeap(), this.memoryDelta$1));
               } else {
                  exec.usedOnHeap_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$newValue$1(exec.usedOnHeap(), this.memoryDelta$1));
               }
            }

            exec.memoryUsed_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$newValue$1(exec.memoryUsed(), this.memoryDelta$1));
            exec.diskUsed_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$newValue$1(exec.diskUsed(), this.diskDelta$1));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.storageLevel$1 = storageLevel$1;
               this.diskDelta$1 = diskDelta$1;
               this.memoryDelta$1 = memoryDelta$1;
            }
         }
      });
      this.org$apache$spark$status$AppStatusListener$$liveRDDs().get(BoxesRunTime.boxToInteger(block.rddId())).foreach(new Serializable(this, block, now, executorId, storageLevel, diskDelta, memoryDelta, updatedStorageLevel, maybeExec, rddBlocksDelta) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         public final RDDBlockId block$1;
         public final long now$11;
         public final String executorId$2;
         public final StorageLevel storageLevel$1;
         public final long diskDelta$1;
         public final long memoryDelta$1;
         private final Option updatedStorageLevel$1;
         private final Option maybeExec$1;
         public final IntRef rddBlocksDelta$1;

         public final void apply(LiveRDD rdd) {
            if (this.updatedStorageLevel$1.isDefined()) {
               rdd.setStorageLevel((String)this.updatedStorageLevel$1.get());
            }

            LiveRDDPartition partition = rdd.partition(this.block$1.name());
            Seq var10000;
            if (this.updatedStorageLevel$1.isDefined()) {
               Seq current = partition.executors();
               if (current.contains(this.executorId$2)) {
                  var10000 = current;
               } else {
                  this.rddBlocksDelta$1.elem = 1;
                  var10000 = (Seq)current.$colon$plus(this.executorId$2, Seq$.MODULE$.canBuildFrom());
               }
            } else {
               this.rddBlocksDelta$1.elem = -1;
               var10000 = (Seq)partition.executors().filter(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final boolean apply(String x$20) {
                     boolean var10000;
                     label23: {
                        String var2 = this.$outer.executorId$2;
                        if (x$20 == null) {
                           if (var2 == null) {
                              break label23;
                           }
                        } else if (x$20.equals(var2)) {
                           break label23;
                        }

                        var10000 = true;
                        return var10000;
                     }

                     var10000 = false;
                     return var10000;
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

            Seq executors = var10000;
            if (executors.nonEmpty()) {
               partition.update(executors, rdd.storageLevel(), this.$outer.org$apache$spark$status$AppStatusListener$$newValue$1(partition.memoryUsed(), this.memoryDelta$1), this.$outer.org$apache$spark$status$AppStatusListener$$newValue$1(partition.diskUsed(), this.diskDelta$1));
            } else {
               rdd.removePartition(this.block$1.name());
            }

            this.maybeExec$1.foreach(new Serializable(this, rdd) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final LiveRDD rdd$1;

               public final void apply(LiveExecutor exec) {
                  if (exec.rddBlocks() + this.$outer.rddBlocksDelta$1.elem > 0) {
                     LiveRDDDistribution dist = this.rdd$1.distribution(exec);
                     dist.memoryUsed_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$newValue$1(dist.memoryUsed(), this.$outer.memoryDelta$1));
                     dist.diskUsed_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$newValue$1(dist.diskUsed(), this.$outer.diskDelta$1));
                     if (exec.hasMemoryInfo()) {
                        if (this.$outer.storageLevel$1.useOffHeap()) {
                           dist.offHeapUsed_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$newValue$1(dist.offHeapUsed(), this.$outer.memoryDelta$1));
                        } else {
                           dist.onHeapUsed_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$newValue$1(dist.onHeapUsed(), this.$outer.memoryDelta$1));
                        }
                     }

                     dist.lastUpdate_$eq((RDDDataDistribution)null);
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     BoxesRunTime.boxToBoolean(this.rdd$1.removeDistribution(exec));
                  }

                  this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$liveRDDs().values().foreach(new Serializable(this, exec) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final LiveExecutor exec$3;

                     public final void apply(LiveRDD otherRdd) {
                        if (otherRdd.info().id() != this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer().block$1.rddId()) {
                           otherRdd.distributionOpt(this.exec$3).foreach(new Serializable(this, otherRdd) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;
                              private final LiveRDD otherRdd$1;

                              public final void apply(LiveRDDDistribution dist) {
                                 dist.lastUpdate_$eq((RDDDataDistribution)null);
                                 this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update(this.otherRdd$1, this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer().now$11, this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$update$default$3());
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                    this.otherRdd$1 = otherRdd$1;
                                 }
                              }
                           });
                        }

                     }

                     // $FF: synthetic method
                     public <undefinedtype> org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$anonfun$$$outer() {
                        return this.$outer;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.exec$3 = exec$3;
                        }
                     }
                  });
               }

               // $FF: synthetic method
               public <undefinedtype> org$apache$spark$status$AppStatusListener$$anonfun$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.rdd$1 = rdd$1;
                  }
               }
            });
            rdd.memoryUsed_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$newValue$1(rdd.memoryUsed(), this.memoryDelta$1));
            rdd.diskUsed_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$newValue$1(rdd.diskUsed(), this.diskDelta$1));
            this.$outer.org$apache$spark$status$AppStatusListener$$update(rdd, this.now$11, this.$outer.org$apache$spark$status$AppStatusListener$$update$default$3());
         }

         // $FF: synthetic method
         public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.block$1 = block$1;
               this.now$11 = now$11;
               this.executorId$2 = executorId$2;
               this.storageLevel$1 = storageLevel$1;
               this.diskDelta$1 = diskDelta$1;
               this.memoryDelta$1 = memoryDelta$1;
               this.updatedStorageLevel$1 = updatedStorageLevel$1;
               this.maybeExec$1 = maybeExec$1;
               this.rddBlocksDelta$1 = rddBlocksDelta$1;
            }
         }
      });
      maybeExec.foreach(new Serializable(this, now, rddBlocksDelta) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final long now$11;
         private final IntRef rddBlocksDelta$1;

         public final void apply(LiveExecutor exec) {
            exec.rddBlocks_$eq(exec.rddBlocks() + this.rddBlocksDelta$1.elem);
            this.$outer.org$apache$spark$status$AppStatusListener$$maybeUpdate(exec, this.now$11);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.now$11 = now$11;
               this.rddBlocksDelta$1 = rddBlocksDelta$1;
            }
         }
      });
   }

   private LiveExecutor getOrCreateExecutor(String executorId, long addTime) {
      return (LiveExecutor)this.org$apache$spark$status$AppStatusListener$$liveExecutors().getOrElseUpdate(executorId, new Serializable(this, executorId, addTime) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;
         private final String executorId$1;
         private final long addTime$1;

         public final LiveExecutor apply() {
            this.$outer.org$apache$spark$status$AppStatusListener$$activeExecutorCount_$eq(this.$outer.org$apache$spark$status$AppStatusListener$$activeExecutorCount() + 1);
            return new LiveExecutor(this.executorId$1, this.addTime$1);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.executorId$1 = executorId$1;
               this.addTime$1 = addTime$1;
            }
         }
      });
   }

   private void updateStreamBlock(SparkListenerBlockUpdated event, StreamBlockId stream) {
      StorageLevel storageLevel = event.blockUpdatedInfo().storageLevel();
      if (storageLevel.isValid()) {
         StreamBlockData data = new StreamBlockData(stream.name(), event.blockUpdatedInfo().blockManagerId().executorId(), event.blockUpdatedInfo().blockManagerId().hostPort(), storageLevel.description(), storageLevel.useMemory(), storageLevel.useDisk(), storageLevel.deserialized(), event.blockUpdatedInfo().memSize(), event.blockUpdatedInfo().diskSize());
         this.org$apache$spark$status$AppStatusListener$$kvstore.write(data);
      } else {
         this.org$apache$spark$status$AppStatusListener$$kvstore.delete(StreamBlockData.class, (Object[])(new String[]{stream.name(), event.blockUpdatedInfo().blockManagerId().executorId()}));
      }

   }

   public LiveStage org$apache$spark$status$AppStatusListener$$getOrCreateStage(StageInfo info) {
      LiveStage stage = (LiveStage)this.org$apache$spark$status$AppStatusListener$$liveStages().computeIfAbsent(new Tuple2$mcII$sp(info.stageId(), info.attemptNumber()), new Function<Tuple2<Object, Object>, LiveStage>(this) {
         public LiveStage apply(Tuple2<Object, Object> key) {
            return new LiveStage();
         }
      });
      stage.info_$eq(info);
      return stage;
   }

   public scala.collection.immutable.Map<String, Object> org$apache$spark$status$AppStatusListener$$killedTasksSummary(TaskEndReason reason, scala.collection.immutable.Map<String, Object> oldSummary) {
      scala.collection.immutable.Map var5;
      if (reason instanceof TaskKilled) {
         TaskKilled var4 = (TaskKilled)reason;
         var5 = oldSummary.updated(var4.reason(), BoxesRunTime.boxToInteger(BoxesRunTime.unboxToInt(oldSummary.getOrElse(var4.reason(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return 0;
            }
         })) + 1));
      } else if (reason instanceof TaskCommitDenied) {
         TaskCommitDenied var6 = (TaskCommitDenied)reason;
         String reason = var6.toErrorString();
         var5 = oldSummary.updated(reason, BoxesRunTime.boxToInteger(BoxesRunTime.unboxToInt(oldSummary.getOrElse(reason, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply() {
               return this.apply$mcI$sp();
            }

            public int apply$mcI$sp() {
               return 0;
            }
         })) + 1));
      } else {
         var5 = oldSummary;
      }

      return var5;
   }

   public void org$apache$spark$status$AppStatusListener$$update(LiveEntity entity, long now, boolean last) {
      entity.write(this.org$apache$spark$status$AppStatusListener$$kvstore, now, last);
   }

   public boolean org$apache$spark$status$AppStatusListener$$update$default$3() {
      return false;
   }

   public void org$apache$spark$status$AppStatusListener$$maybeUpdate(LiveEntity entity, long now) {
      if (this.org$apache$spark$status$AppStatusListener$$live && this.liveUpdatePeriodNs() >= 0L && now - entity.lastWriteTime() > this.liveUpdatePeriodNs()) {
         this.org$apache$spark$status$AppStatusListener$$update(entity, now, this.org$apache$spark$status$AppStatusListener$$update$default$3());
      }

   }

   public void org$apache$spark$status$AppStatusListener$$liveUpdate(LiveEntity entity, long now) {
      if (this.org$apache$spark$status$AppStatusListener$$live) {
         this.org$apache$spark$status$AppStatusListener$$update(entity, now, this.org$apache$spark$status$AppStatusListener$$update$default$3());
      }

   }

   public void org$apache$spark$status$AppStatusListener$$cleanupExecutors(long count) {
      int threshold = BoxesRunTime.unboxToInt(this.conf.get(config$.MODULE$.MAX_RETAINED_DEAD_EXECUTORS()));
      long dead = count - (long)this.org$apache$spark$status$AppStatusListener$$activeExecutorCount();
      if (dead > (long)threshold) {
         long countToDelete = this.calculateNumberToRemove(dead, (long)threshold);
         Seq toDelete = ((TraversableOnce)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.org$apache$spark$status$AppStatusListener$$kvstore.view(ExecutorSummaryWrapper.class).index("active").max(countToDelete).first(BoxesRunTime.boxToBoolean(false)).last(BoxesRunTime.boxToBoolean(false))).asScala()).toSeq();
         toDelete.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final AppStatusListener $outer;

            public final void apply(ExecutorSummaryWrapper e) {
               this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.delete(e.getClass(), e.info().id());
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

   public void org$apache$spark$status$AppStatusListener$$cleanupJobs(long count) {
      long countToDelete = this.calculateNumberToRemove(count, (long)BoxesRunTime.unboxToInt(this.conf.get(config$.MODULE$.MAX_RETAINED_JOBS())));
      if (countToDelete > 0L) {
         Seq toDelete = KVUtils$.MODULE$.viewToSeq(this.org$apache$spark$status$AppStatusListener$$kvstore.view(JobDataWrapper.class), (int)countToDelete, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(JobDataWrapper j) {
               boolean var5;
               label32: {
                  JobExecutionStatus var10000 = j.info().status();
                  JobExecutionStatus var2 = JobExecutionStatus.RUNNING;
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label32;
                     }
                  } else if (var10000.equals(var2)) {
                     break label32;
                  }

                  var10000 = j.info().status();
                  JobExecutionStatus var3 = JobExecutionStatus.UNKNOWN;
                  if (var10000 == null) {
                     if (var3 == null) {
                        break label32;
                     }
                  } else if (var10000.equals(var3)) {
                     break label32;
                  }

                  var5 = true;
                  return var5;
               }

               var5 = false;
               return var5;
            }
         });
         toDelete.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final AppStatusListener $outer;

            public final void apply(JobDataWrapper j) {
               this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.delete(j.getClass(), BoxesRunTime.boxToInteger(j.info().jobId()));
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

   public void org$apache$spark$status$AppStatusListener$$cleanupStages(long count) {
      long countToDelete = this.calculateNumberToRemove(count, (long)BoxesRunTime.unboxToInt(this.conf.get(config$.MODULE$.MAX_RETAINED_STAGES())));
      if (countToDelete > 0L) {
         Seq stages = KVUtils$.MODULE$.viewToSeq(this.org$apache$spark$status$AppStatusListener$$kvstore.view(StageDataWrapper.class), (int)countToDelete, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(StageDataWrapper s) {
               boolean var5;
               label32: {
                  StageStatus var10000 = s.info().status();
                  StageStatus var2 = StageStatus.ACTIVE;
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label32;
                     }
                  } else if (var10000.equals(var2)) {
                     break label32;
                  }

                  var10000 = s.info().status();
                  StageStatus var3 = StageStatus.PENDING;
                  if (var10000 == null) {
                     if (var3 == null) {
                        break label32;
                     }
                  } else if (var10000.equals(var3)) {
                     break label32;
                  }

                  var5 = true;
                  return var5;
               }

               var5 = false;
               return var5;
            }
         });
         stages.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final AppStatusListener $outer;

            public final void apply(StageDataWrapper s) {
               int[] key = new int[]{s.info().stageId(), s.info().attemptId()};
               this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.delete(s.getClass(), key);
               Seq execSummaries = ((TraversableOnce)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.view(ExecutorStageSummaryWrapper.class).index("stage").first(key).last(key)).asScala()).toSeq();
               execSummaries.foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(ExecutorStageSummaryWrapper e) {
                     this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$kvstore.delete(e.getClass(), e.id());
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               Iterable tasks = (Iterable)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.view(TaskDataWrapper.class).index("stage").first(key).last(key)).asScala();
               tasks.foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(TaskDataWrapper t) {
                     this.$outer.org$apache$spark$status$AppStatusListener$$anonfun$$$outer().org$apache$spark$status$AppStatusListener$$kvstore.delete(t.getClass(), t.taskId());
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               KVStoreIterator remainingAttempts = this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.view(StageDataWrapper.class).index("stageId").first(BoxesRunTime.boxToInteger(s.info().stageId())).last(BoxesRunTime.boxToInteger(s.info().stageId())).closeableIterator();

               boolean var10000;
               try {
                  var10000 = ((scala.collection.Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(remainingAttempts).asScala()).exists(new Serializable(this, s) {
                     public static final long serialVersionUID = 0L;
                     private final StageDataWrapper s$1;

                     public final boolean apply(StageDataWrapper other) {
                        return other.info().attemptId() != this.s$1.info().attemptId();
                     }

                     public {
                        this.s$1 = s$1;
                     }
                  });
               } finally {
                  remainingAttempts.close();
               }

               boolean hasMoreAttempts = var10000;
               if (!hasMoreAttempts) {
                  this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.delete(RDDOperationGraphWrapper.class, BoxesRunTime.boxToInteger(s.info().stageId()));
               }

               this.$outer.org$apache$spark$status$AppStatusListener$$cleanupCachedQuantiles(key);
            }

            // $FF: synthetic method
            public AppStatusListener org$apache$spark$status$AppStatusListener$$anonfun$$$outer() {
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
   }

   public void org$apache$spark$status$AppStatusListener$$cleanupTasks(LiveStage stage) {
      int countToDelete = (int)this.calculateNumberToRemove((long)stage.savedTasks().get(), (long)this.org$apache$spark$status$AppStatusListener$$maxTasksPerStage());
      if (countToDelete > 0) {
         int[] stageKey = new int[]{stage.info().stageId(), stage.info().attemptNumber()};
         KVStoreView view = this.org$apache$spark$status$AppStatusListener$$kvstore.view(TaskDataWrapper.class).index("stage").first(stageKey).last(stageKey);
         Seq toDelete = KVUtils$.MODULE$.viewToSeq(view, countToDelete, new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final AppStatusListener $outer;

            public final boolean apply(TaskDataWrapper t) {
               boolean var3;
               label25: {
                  if (this.$outer.org$apache$spark$status$AppStatusListener$$live) {
                     String var10000 = t.status();
                     String var2 = TaskState$.MODULE$.RUNNING().toString();
                     if (var10000 == null) {
                        if (var2 == null) {
                           break label25;
                        }
                     } else if (var10000.equals(var2)) {
                        break label25;
                     }
                  }

                  var3 = true;
                  return var3;
               }

               var3 = false;
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
         toDelete.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final AppStatusListener $outer;

            public final void apply(TaskDataWrapper t) {
               this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.delete(t.getClass(), t.taskId());
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         stage.savedTasks().addAndGet(-toDelete.size());
         int remaining = countToDelete - toDelete.size();
         if (remaining > 0) {
            List runningTasksToDelete = ((TraversableOnce)JavaConverters$.MODULE$.asScalaIteratorConverter(view.max((long)remaining).iterator()).asScala()).toList();
            runningTasksToDelete.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final AppStatusListener $outer;

               public final void apply(TaskDataWrapper t) {
                  this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.delete(t.getClass(), t.taskId());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            BoxesRunTime.boxToInteger(stage.savedTasks().addAndGet(-remaining));
         } else {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         if (this.org$apache$spark$status$AppStatusListener$$live) {
            this.org$apache$spark$status$AppStatusListener$$cleanupCachedQuantiles(stageKey);
         }
      }

      stage.cleaning_$eq(false);
   }

   public void org$apache$spark$status$AppStatusListener$$cleanupCachedQuantiles(int[] stageKey) {
      List cachedQuantiles = ((TraversableOnce)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.org$apache$spark$status$AppStatusListener$$kvstore.view(CachedQuantile.class).index("stage").first(stageKey).last(stageKey)).asScala()).toList();
      cachedQuantiles.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final void apply(CachedQuantile q) {
            this.$outer.org$apache$spark$status$AppStatusListener$$kvstore.delete(q.getClass(), q.id());
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

   private long calculateNumberToRemove(long dataSize, long retainedSize) {
      return dataSize > retainedSize ? package$.MODULE$.max(retainedSize / 10L, dataSize - retainedSize) : 0L;
   }

   public final long org$apache$spark$status$AppStatusListener$$newValue$1(long old, long delta) {
      return package$.MODULE$.max(0L, old + delta);
   }

   public AppStatusListener(ElementTrackingStore kvstore, SparkConf conf, boolean live, Option<Object> lastUpdateTime) {
      this.org$apache$spark$status$AppStatusListener$$kvstore = kvstore;
      this.conf = conf;
      this.org$apache$spark$status$AppStatusListener$$live = live;
      this.lastUpdateTime = lastUpdateTime;
      Logging$class.$init$(this);
      this.sparkVersion = org.apache.spark.package$.MODULE$.SPARK_VERSION();
      this.appInfo = null;
      this.appSummary = new AppSummary(0, 0);
      this.org$apache$spark$status$AppStatusListener$$coresPerTask = 1;
      this.liveUpdatePeriodNs = live ? BoxesRunTime.unboxToLong(conf.get(config$.MODULE$.LIVE_ENTITY_UPDATE_PERIOD())) : -1L;
      this.org$apache$spark$status$AppStatusListener$$maxTasksPerStage = BoxesRunTime.unboxToInt(conf.get(config$.MODULE$.MAX_RETAINED_TASKS_PER_STAGE()));
      this.org$apache$spark$status$AppStatusListener$$maxGraphRootNodes = BoxesRunTime.unboxToInt(conf.get(config$.MODULE$.MAX_RETAINED_ROOT_NODES()));
      this.org$apache$spark$status$AppStatusListener$$liveStages = new ConcurrentHashMap();
      this.liveJobs = new HashMap<Object, LiveJob>();
      this.org$apache$spark$status$AppStatusListener$$liveExecutors = new HashMap<String, LiveExecutor>();
      this.org$apache$spark$status$AppStatusListener$$liveTasks = new HashMap<Object, LiveTask>();
      this.org$apache$spark$status$AppStatusListener$$liveRDDs = new HashMap<Object, LiveRDD>();
      this.org$apache$spark$status$AppStatusListener$$pools = new HashMap<String, SchedulerPool>();
      this.org$apache$spark$status$AppStatusListener$$activeExecutorCount = 0;
      kvstore.addTrigger(ExecutorSummaryWrapper.class, (long)BoxesRunTime.unboxToInt(conf.get(config$.MODULE$.MAX_RETAINED_DEAD_EXECUTORS())), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final void apply(long count) {
            this.apply$mcVJ$sp(count);
         }

         public void apply$mcVJ$sp(long count) {
            this.$outer.org$apache$spark$status$AppStatusListener$$cleanupExecutors(count);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      kvstore.addTrigger(JobDataWrapper.class, (long)BoxesRunTime.unboxToInt(conf.get(config$.MODULE$.MAX_RETAINED_JOBS())), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final void apply(long count) {
            this.apply$mcVJ$sp(count);
         }

         public void apply$mcVJ$sp(long count) {
            this.$outer.org$apache$spark$status$AppStatusListener$$cleanupJobs(count);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      kvstore.addTrigger(StageDataWrapper.class, (long)BoxesRunTime.unboxToInt(conf.get(config$.MODULE$.MAX_RETAINED_STAGES())), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final void apply(long count) {
            this.apply$mcVJ$sp(count);
         }

         public void apply$mcVJ$sp(long count) {
            this.$outer.org$apache$spark$status$AppStatusListener$$cleanupStages(count);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      kvstore.onFlush(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppStatusListener $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            if (!this.$outer.org$apache$spark$status$AppStatusListener$$live) {
               this.$outer.org$apache$spark$status$AppStatusListener$$flush();
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
