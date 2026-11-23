package org.apache.spark.scheduler;

import java.nio.ByteBuffer;
import java.util.Properties;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.TaskContextImpl;
import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.memory.MemoryManager;
import org.apache.spark.memory.MemoryMode;
import org.apache.spark.memory.TaskMemoryManager;
import org.apache.spark.metrics.MetricsSystem;
import org.apache.spark.storage.memory.MemoryStore;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.CallerContext;
import org.apache.spark.util.Utils$;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.GenTraversableOnce;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Nil$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\t}fAB\u0001\u0003\u0003\u0003!!B\u0001\u0003UCN\\'BA\u0002\u0005\u0003%\u00198\r[3ek2,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h+\tYqmE\u0002\u0001\u0019I\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\u0007\u0014\u0013\t!bB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u0017\u0001\t\u0015\r\u0011\"\u0001\u0019\u0003\u001d\u0019H/Y4f\u0013\u0012\u001c\u0001!F\u0001\u001a!\ti!$\u0003\u0002\u001c\u001d\t\u0019\u0011J\u001c;\t\u0011u\u0001!\u0011!Q\u0001\ne\t\u0001b\u001d;bO\u0016LE\r\t\u0005\t?\u0001\u0011)\u0019!C\u00011\u0005q1\u000f^1hK\u0006#H/Z7qi&#\u0007\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u001fM$\u0018mZ3BiR,W\u000e\u001d;JI\u0002B\u0001b\t\u0001\u0003\u0006\u0004%\t\u0001G\u0001\fa\u0006\u0014H/\u001b;j_:LE\r\u0003\u0005&\u0001\t\u0005\t\u0015!\u0003\u001a\u00031\u0001\u0018M\u001d;ji&|g.\u00133!\u0011!9\u0003A!a\u0001\n\u0003A\u0013a\u00047pG\u0006d\u0007K]8qKJ$\u0018.Z:\u0016\u0003%\u0002\"AK\u0018\u000e\u0003-R!\u0001L\u0017\u0002\tU$\u0018\u000e\u001c\u0006\u0002]\u0005!!.\u0019<b\u0013\t\u00014F\u0001\u0006Qe>\u0004XM\u001d;jKND\u0001B\r\u0001\u0003\u0002\u0004%\taM\u0001\u0014Y>\u001c\u0017\r\u001c)s_B,'\u000f^5fg~#S-\u001d\u000b\u0003i]\u0002\"!D\u001b\n\u0005Yr!\u0001B+oSRDq\u0001O\u0019\u0002\u0002\u0003\u0007\u0011&A\u0002yIEB\u0001B\u000f\u0001\u0003\u0002\u0003\u0006K!K\u0001\u0011Y>\u001c\u0017\r\u001c)s_B,'\u000f^5fg\u0002B#!\u000f\u001f\u0011\u00055i\u0014B\u0001 \u000f\u0005%!(/\u00198tS\u0016tG\u000f\u0003\u0005A\u0001\t\u0005\t\u0015!\u0003B\u0003U\u0019XM]5bY&TX\r\u001a+bg.lU\r\u001e:jGN\u00042!\u0004\"E\u0013\t\u0019eBA\u0003BeJ\f\u0017\u0010\u0005\u0002\u000e\u000b&\u0011aI\u0004\u0002\u0005\u0005f$X\r\u0003\u0005I\u0001\t\u0015\r\u0011\"\u0001J\u0003\u0015QwNY%e+\u0005Q\u0005cA\u0007L3%\u0011AJ\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u00119\u0003!\u0011!Q\u0001\n)\u000baA[8c\u0013\u0012\u0004\u0003\u0002\u0003)\u0001\u0005\u000b\u0007I\u0011A)\u0002\u000b\u0005\u0004\b/\u00133\u0016\u0003I\u00032!D&T!\t!vK\u0004\u0002\u000e+&\u0011aKD\u0001\u0007!J,G-\u001a4\n\u0005aK&AB*ue&twM\u0003\u0002W\u001d!A1\f\u0001B\u0001B\u0003%!+\u0001\u0004baBLE\r\t\u0005\t;\u0002\u0011)\u0019!C\u0001#\u0006a\u0011\r\u001d9BiR,W\u000e\u001d;JI\"Aq\f\u0001B\u0001B\u0003%!+A\u0007baB\fE\u000f^3naRLE\r\t\u0005\u0006C\u0002!\tAY\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0013\r\u0004\u0018O]:ukZ<\bc\u00013\u0001K6\t!\u0001\u0005\u0002gO2\u0001A!\u00025\u0001\u0005\u0004I'!\u0001+\u0012\u0005)l\u0007CA\u0007l\u0013\tagBA\u0004O_RD\u0017N\\4\u0011\u00055q\u0017BA8\u000f\u0005\r\te.\u001f\u0005\u0006-\u0001\u0004\r!\u0007\u0005\u0006?\u0001\u0004\r!\u0007\u0005\u0006G\u0001\u0004\r!\u0007\u0005\bO\u0001\u0004\n\u00111\u0001*\u0011\u001d\u0001\u0005\r%AA\u0002\u0005Cq\u0001\u00131\u0011\u0002\u0003\u0007!\nC\u0004QAB\u0005\t\u0019\u0001*\t\u000fu\u0003\u0007\u0013!a\u0001%\"A\u0011\u0010\u0001EC\u0002\u0013\u0005!0A\u0004nKR\u0014\u0018nY:\u0016\u0003m\u0004\"\u0001`@\u000e\u0003uT!A \u0003\u0002\u0011\u0015DXmY;u_JL1!!\u0001~\u0005-!\u0016m]6NKR\u0014\u0018nY:\t\u0013\u0005\u0015\u0001\u0001#A!B\u0013Y\u0018\u0001C7fiJL7m\u001d\u0011)\u0007\u0005\rA\bC\u0004\u0002\f\u0001!)!!\u0004\u0002\u0007I,h\u000eF\u0004f\u0003\u001f\tI\"!\b\t\u0011\u0005E\u0011\u0011\u0002a\u0001\u0003'\tQ\u0002^1tW\u0006#H/Z7qi&#\u0007cA\u0007\u0002\u0016%\u0019\u0011q\u0003\b\u0003\t1{gn\u001a\u0005\b\u00037\tI\u00011\u0001\u001a\u00035\tG\u000f^3naRtU/\u001c2fe\"A\u0011qDA\u0005\u0001\u0004\t\t#A\u0007nKR\u0014\u0018nY:TsN$X-\u001c\t\u0005\u0003G\t9#\u0004\u0002\u0002&)\u0011\u0011\u0010B\u0005\u0005\u0003S\t)CA\u0007NKR\u0014\u0018nY:TsN$X-\u001c\u0005\f\u0003[\u0001\u0001\u0019!a\u0001\n\u0013\ty#A\tuCN\\W*Z7pefl\u0015M\\1hKJ,\"!!\r\u0011\t\u0005M\u0012\u0011H\u0007\u0003\u0003kQ1!a\u000e\u0005\u0003\u0019iW-\\8ss&!\u00111HA\u001b\u0005E!\u0016m]6NK6|'/_'b]\u0006<WM\u001d\u0005\f\u0003\u007f\u0001\u0001\u0019!a\u0001\n\u0013\t\t%A\u000buCN\\W*Z7pefl\u0015M\\1hKJ|F%Z9\u0015\u0007Q\n\u0019\u0005C\u00059\u0003{\t\t\u00111\u0001\u00022!A\u0011q\t\u0001!B\u0013\t\t$\u0001\nuCN\\W*Z7pefl\u0015M\\1hKJ\u0004\u0003bBA&\u0001\u0011\u0005\u0011QJ\u0001\u0015g\u0016$H+Y:l\u001b\u0016lwN]=NC:\fw-\u001a:\u0015\u0007Q\ny\u0005\u0003\u0005\u0002.\u0005%\u0003\u0019AA\u0019\u0011\u001d\t\u0019\u0006\u0001D\u0001\u0003+\nqA];o)\u0006\u001c8\u000eF\u0002f\u0003/B\u0001\"!\u0017\u0002R\u0001\u0007\u00111L\u0001\bG>tG/\u001a=u!\u0011\ti&a\u0018\u000e\u0003\u0011I1!!\u0019\u0005\u0005-!\u0016m]6D_:$X\r\u001f;\t\u000f\u0005\u0015\u0004\u0001\"\u0001\u0002h\u0005\u0011\u0002O]3gKJ\u0014X\r\u001a'pG\u0006$\u0018n\u001c8t+\t\tI\u0007\u0005\u0004\u0002l\u0005m\u0014\u0011\u0011\b\u0005\u0003[\n9H\u0004\u0003\u0002p\u0005UTBAA9\u0015\r\t\u0019hF\u0001\u0007yI|w\u000e\u001e \n\u0003=I1!!\u001f\u000f\u0003\u001d\u0001\u0018mY6bO\u0016LA!! \u0002\u0000\t\u00191+Z9\u000b\u0007\u0005ed\u0002E\u0002e\u0003\u0007K1!!\"\u0003\u00051!\u0016m]6M_\u000e\fG/[8o\u0011%\tI\t\u0001a\u0001\n\u0003\tY)A\u0003fa>\u001c\u0007.\u0006\u0002\u0002\u0014!I\u0011q\u0012\u0001A\u0002\u0013\u0005\u0011\u0011S\u0001\nKB|7\r[0%KF$2\u0001NAJ\u0011%A\u0014QRA\u0001\u0002\u0004\t\u0019\u0002\u0003\u0005\u0002\u0018\u0002\u0001\u000b\u0015BA\n\u0003\u0019)\u0007o\\2iA!Y\u0011\u0011\f\u0001A\u0002\u0003\u0007I\u0011AAN+\t\ti\n\u0005\u0003\u0002^\u0005}\u0015bAAQ\t\tyA+Y:l\u0007>tG/\u001a=u\u00136\u0004H\u000eC\u0006\u0002&\u0002\u0001\r\u00111A\u0005\u0002\u0005\u001d\u0016aC2p]R,\u0007\u0010^0%KF$2\u0001NAU\u0011%A\u00141UA\u0001\u0002\u0004\ti\n\u0003\u0005\u0002.\u0002\u0001\u000b\u0015BAO\u0003!\u0019wN\u001c;fqR\u0004\u0003fAAVy!Y\u00111\u0017\u0001A\u0002\u0003\u0007I\u0011BA[\u0003)!\u0018m]6UQJ,\u0017\rZ\u000b\u0003\u0003o\u0003B!!/\u0002@6\u0011\u00111\u0018\u0006\u0004\u0003{k\u0013\u0001\u00027b]\u001eLA!!1\u0002<\n1A\u000b\u001b:fC\u0012D1\"!2\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u0002H\u0006qA/Y:l)\"\u0014X-\u00193`I\u0015\fHc\u0001\u001b\u0002J\"I\u0001(a1\u0002\u0002\u0003\u0007\u0011q\u0017\u0005\t\u0003\u001b\u0004\u0001\u0015)\u0003\u00028\u0006YA/Y:l)\"\u0014X-\u00193!Q\r\tY\r\u0010\u0015\u0005\u0003\u0017\f\u0019\u000eE\u0002\u000e\u0003+L1!a6\u000f\u0005!1x\u000e\\1uS2,\u0007\"CAn\u0001\u0001\u0007I\u0011BAo\u0003=y&/Z1t_:LemS5mY\u0016$W#A*\t\u0013\u0005\u0005\b\u00011A\u0005\n\u0005\r\u0018aE0sK\u0006\u001cxN\\%g\u0017&dG.\u001a3`I\u0015\fHc\u0001\u001b\u0002f\"A\u0001(a8\u0002\u0002\u0003\u00071\u000bC\u0004\u0002j\u0002\u0001\u000b\u0015B*\u0002!}\u0013X-Y:p]&37*\u001b7mK\u0012\u0004\u0003fAAty!\"\u0011q]Aj\u0011%\t\t\u0010\u0001a\u0001\n#\tY)\u0001\r`Kb,7-\u001e;pe\u0012+7/\u001a:jC2L'0\u001a+j[\u0016D\u0011\"!>\u0001\u0001\u0004%\t\"a>\u00029}+\u00070Z2vi>\u0014H)Z:fe&\fG.\u001b>f)&lWm\u0018\u0013fcR\u0019A'!?\t\u0013a\n\u00190!AA\u0002\u0005M\u0001\u0002CA\u007f\u0001\u0001\u0006K!a\u0005\u00023}+\u00070Z2vi>\u0014H)Z:fe&\fG.\u001b>f)&lW\r\t\u0005\n\u0005\u0003\u0001\u0001\u0019!C\t\u0003\u0017\u000b1dX3yK\u000e,Ho\u001c:EKN,'/[1mSj,7\t];US6,\u0007\"\u0003B\u0003\u0001\u0001\u0007I\u0011\u0003B\u0004\u0003}yV\r_3dkR|'\u000fR3tKJL\u0017\r\\5{K\u000e\u0003X\u000fV5nK~#S-\u001d\u000b\u0004i\t%\u0001\"\u0003\u001d\u0003\u0004\u0005\u0005\t\u0019AA\n\u0011!\u0011i\u0001\u0001Q!\n\u0005M\u0011\u0001H0fq\u0016\u001cW\u000f^8s\t\u0016\u001cXM]5bY&TXm\u00119v)&lW\r\t\u0005\u0007\u0005#\u0001A\u0011A)\u0002\u001dI,\u0017m]8o\u0013\u001a\\\u0015\u000e\u001c7fI\"9!Q\u0003\u0001\u0005\u0002\u0005-\u0015aF3yK\u000e,Ho\u001c:EKN,'/[1mSj,G+[7f\u0011\u001d\u0011I\u0002\u0001C\u0001\u0003\u0017\u000b!$\u001a=fGV$xN\u001d#fg\u0016\u0014\u0018.\u00197ju\u0016\u001c\u0005/\u001e+j[\u0016DqA!\b\u0001\t\u0003\u0011y\"A\rd_2dWm\u0019;BG\u000e,X.\u001e7bi>\u0014X\u000b\u001d3bi\u0016\u001cH\u0003\u0002B\u0011\u0005w\u0001b!a\u001b\u0002|\t\r\u0002G\u0002B\u0013\u0005c\u00119\u0004\u0005\u0005\u0003(\t-\"q\u0006B\u001b\u001b\t\u0011IC\u0003\u0002-\t%!!Q\u0006B\u0015\u00055\t5mY;nk2\fGo\u001c:WeA\u0019aM!\r\u0005\u0017\tM\"1DA\u0001\u0002\u0003\u0015\t!\u001b\u0002\u0004?\u0012\n\u0004c\u00014\u00038\u0011Y!\u0011\bB\u000e\u0003\u0003\u0005\tQ!\u0001j\u0005\ryFE\r\u0005\u000b\u0005{\u0011Y\u0002%AA\u0002\t}\u0012A\u0003;bg.4\u0015-\u001b7fIB\u0019QB!\u0011\n\u0007\t\rcBA\u0004C_>dW-\u00198\t\u000f\t\u001d\u0003\u0001\"\u0001\u0003J\u0005!1.\u001b7m)\u0015!$1\nB(\u0011!\u0011iE!\u0012A\u0002\t}\u0012aD5oi\u0016\u0014(/\u001e9u)\"\u0014X-\u00193\t\u000f\tE#Q\ta\u0001'\u00061!/Z1t_:D\u0011B!\u0016\u0001#\u0003%\tAa\u0016\u0002G\r|G\u000e\\3di\u0006\u001b7-^7vY\u0006$xN]+qI\u0006$Xm\u001d\u0013eK\u001a\fW\u000f\u001c;%cU\u0011!\u0011\f\u0016\u0005\u0005\u007f\u0011Yf\u000b\u0002\u0003^A!!q\fB5\u001b\t\u0011\tG\u0003\u0003\u0003d\t\u0015\u0014!C;oG\",7m[3e\u0015\r\u00119GD\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002\u0002B6\u0005C\u0012\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u000f)\u0011yGAA\u0001\u0012\u0003!!\u0011O\u0001\u0005)\u0006\u001c8\u000eE\u0002e\u0005g2\u0011\"\u0001\u0002\u0002\u0002#\u0005AA!\u001e\u0014\t\tMDB\u0005\u0005\bC\nMD\u0011\u0001B=)\t\u0011\t\b\u0003\u0006\u0003~\tM\u0014\u0013!C\u0001\u0005\u007f\n1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\"T\u0003\u0002BA\u0005\u000b+\"Aa!+\u0007%\u0012Y\u0006\u0002\u0004i\u0005w\u0012\r!\u001b\u0005\u000b\u0005\u0013\u0013\u0019(%A\u0005\u0002\t-\u0015a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$S'\u0006\u0003\u0003\u000e\nEUC\u0001BHU\r\t%1\f\u0003\u0007Q\n\u001d%\u0019A5\t\u0015\tU%1OI\u0001\n\u0003\u00119*A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HEN\u000b\u0005\u00053\u0013i*\u0006\u0002\u0003\u001c*\u001a!Ja\u0017\u0005\r!\u0014\u0019J1\u0001j\u0011)\u0011\tKa\u001d\u0012\u0002\u0013\u0005!1U\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001c\u0016\t\t\u0015&\u0011V\u000b\u0003\u0005OS3A\u0015B.\t\u0019A'q\u0014b\u0001S\"Q!Q\u0016B:#\u0003%\tAa,\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00139+\u0011\u0011)K!-\u0005\r!\u0014YK1\u0001j\u0011)\u0011)La\u001d\u0002\u0002\u0013%!qW\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0003:B!\u0011\u0011\u0018B^\u0013\u0011\u0011i,a/\u0003\r=\u0013'.Z2u\u0001"
)
public abstract class Task<T> implements Serializable {
   private final int stageId;
   private final int stageAttemptId;
   private final int partitionId;
   private transient Properties localProperties;
   private final byte[] serializedTaskMetrics;
   private final Option<Object> jobId;
   private final Option<String> appId;
   private final Option<String> appAttemptId;
   private transient TaskMetrics metrics;
   private TaskMemoryManager taskMemoryManager;
   private long epoch;
   private transient TaskContextImpl context;
   private transient volatile Thread taskThread;
   private transient volatile String _reasonIfKilled;
   private long _executorDeserializeTime;
   private long _executorDeserializeCpuTime;
   private transient volatile boolean bitmap$trans$0;

   public static <T> Option<String> $lessinit$greater$default$8() {
      return Task$.MODULE$.$lessinit$greater$default$8();
   }

   public static <T> Option<String> $lessinit$greater$default$7() {
      return Task$.MODULE$.$lessinit$greater$default$7();
   }

   public static <T> Option<Object> $lessinit$greater$default$6() {
      return Task$.MODULE$.$lessinit$greater$default$6();
   }

   public static <T> byte[] $lessinit$greater$default$5() {
      return Task$.MODULE$.$lessinit$greater$default$5();
   }

   public static <T> Properties $lessinit$greater$default$4() {
      return Task$.MODULE$.$lessinit$greater$default$4();
   }

   private TaskMetrics metrics$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$trans$0) {
            this.metrics = (TaskMetrics)SparkEnv$.MODULE$.get().closureSerializer().newInstance().deserialize(ByteBuffer.wrap(this.serializedTaskMetrics), ClassTag$.MODULE$.Nothing());
            this.bitmap$trans$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.metrics;
   }

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptId() {
      return this.stageAttemptId;
   }

   public int partitionId() {
      return this.partitionId;
   }

   public Properties localProperties() {
      return this.localProperties;
   }

   public void localProperties_$eq(Properties x$1) {
      this.localProperties = x$1;
   }

   public Option<Object> jobId() {
      return this.jobId;
   }

   public Option<String> appId() {
      return this.appId;
   }

   public Option<String> appAttemptId() {
      return this.appAttemptId;
   }

   public TaskMetrics metrics() {
      return this.bitmap$trans$0 ? this.metrics : this.metrics$lzycompute();
   }

   public final T run(long taskAttemptId, int attemptNumber, MetricsSystem metricsSystem) {
      SparkEnv$.MODULE$.get().blockManager().registerTask(taskAttemptId);
      this.context_$eq(new TaskContextImpl(this.stageId(), this.stageAttemptId(), this.partitionId(), taskAttemptId, attemptNumber, this.taskMemoryManager(), this.localProperties(), metricsSystem, this.metrics()));
      TaskContext$.MODULE$.setTaskContext(this.context());
      this.taskThread_$eq(Thread.currentThread());
      if (this._reasonIfKilled() != null) {
         this.kill(false, this._reasonIfKilled());
      }

      (new CallerContext("TASK", (Option)SparkEnv$.MODULE$.get().conf().get((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.APP_CALLER_CONTEXT()), this.appId(), this.appAttemptId(), this.jobId(), Option$.MODULE$.apply(BoxesRunTime.boxToInteger(this.stageId())), Option$.MODULE$.apply(BoxesRunTime.boxToInteger(this.stageAttemptId())), Option$.MODULE$.apply(BoxesRunTime.boxToLong(taskAttemptId)), Option$.MODULE$.apply(BoxesRunTime.boxToInteger(attemptNumber)))).setCurrentContext();
      boolean var73 = false;

      Object var10000;
      try {
         try {
            var73 = true;
            var10000 = this.runTask(this.context());
            var73 = false;
         } catch (Throwable var93) {
            Throwable var5 = var93;

            label515:
            try {
               this.context().markTaskFailed(var5);
            } catch (Throwable var92) {
               var93.addSuppressed(var92);
               break label515;
            }

            this.context().markTaskCompleted(new Some(var93));
            throw var93;
         }
      } finally {
         if (var73) {
            try {
               this.context().markTaskCompleted(None$.MODULE$);
            } finally {
               try {
                  Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final void apply() {
                        this.apply$mcV$sp();
                     }

                     public void apply$mcV$sp() {
                        MemoryStore qual$1 = SparkEnv$.MODULE$.get().blockManager().memoryStore();
                        MemoryMode x$1 = MemoryMode.ON_HEAP;
                        long x$2 = qual$1.releaseUnrollMemoryForThisTask$default$2();
                        qual$1.releaseUnrollMemoryForThisTask(x$1, x$2);
                        MemoryStore qual$2 = SparkEnv$.MODULE$.get().blockManager().memoryStore();
                        MemoryMode x$3 = MemoryMode.OFF_HEAP;
                        long x$4 = qual$2.releaseUnrollMemoryForThisTask$default$2();
                        qual$2.releaseUnrollMemoryForThisTask(x$3, x$4);
                        MemoryManager memoryManager = SparkEnv$.MODULE$.get().memoryManager();
                        synchronized(memoryManager) {
                           memoryManager.notifyAll();
                           BoxedUnit var10000 = BoxedUnit.UNIT;
                        }
                     }
                  });
               } finally {
                  TaskContext$.MODULE$.unset();
               }
            }
         }
      }

      Object var6 = var10000;

      try {
         this.context().markTaskCompleted(None$.MODULE$);
      } finally {
         try {
            Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  MemoryStore qual$1 = SparkEnv$.MODULE$.get().blockManager().memoryStore();
                  MemoryMode x$1 = MemoryMode.ON_HEAP;
                  long x$2 = qual$1.releaseUnrollMemoryForThisTask$default$2();
                  qual$1.releaseUnrollMemoryForThisTask(x$1, x$2);
                  MemoryStore qual$2 = SparkEnv$.MODULE$.get().blockManager().memoryStore();
                  MemoryMode x$3 = MemoryMode.OFF_HEAP;
                  long x$4 = qual$2.releaseUnrollMemoryForThisTask$default$2();
                  qual$2.releaseUnrollMemoryForThisTask(x$3, x$4);
                  MemoryManager memoryManager = SparkEnv$.MODULE$.get().memoryManager();
                  synchronized(memoryManager) {
                     memoryManager.notifyAll();
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  }
               }
            });
         } finally {
            TaskContext$.MODULE$.unset();
         }
      }

      return (T)var6;
   }

   private TaskMemoryManager taskMemoryManager() {
      return this.taskMemoryManager;
   }

   private void taskMemoryManager_$eq(TaskMemoryManager x$1) {
      this.taskMemoryManager = x$1;
   }

   public void setTaskMemoryManager(TaskMemoryManager taskMemoryManager) {
      this.taskMemoryManager_$eq(taskMemoryManager);
   }

   public abstract T runTask(TaskContext var1);

   public Seq<TaskLocation> preferredLocations() {
      return Nil$.MODULE$;
   }

   public long epoch() {
      return this.epoch;
   }

   public void epoch_$eq(long x$1) {
      this.epoch = x$1;
   }

   public TaskContextImpl context() {
      return this.context;
   }

   public void context_$eq(TaskContextImpl x$1) {
      this.context = x$1;
   }

   private Thread taskThread() {
      return this.taskThread;
   }

   private void taskThread_$eq(Thread x$1) {
      this.taskThread = x$1;
   }

   private String _reasonIfKilled() {
      return this._reasonIfKilled;
   }

   private void _reasonIfKilled_$eq(String x$1) {
      this._reasonIfKilled = x$1;
   }

   public long _executorDeserializeTime() {
      return this._executorDeserializeTime;
   }

   public void _executorDeserializeTime_$eq(long x$1) {
      this._executorDeserializeTime = x$1;
   }

   public long _executorDeserializeCpuTime() {
      return this._executorDeserializeCpuTime;
   }

   public void _executorDeserializeCpuTime_$eq(long x$1) {
      this._executorDeserializeCpuTime = x$1;
   }

   public Option<String> reasonIfKilled() {
      return Option$.MODULE$.<String>apply(this._reasonIfKilled());
   }

   public long executorDeserializeTime() {
      return this._executorDeserializeTime();
   }

   public long executorDeserializeCpuTime() {
      return this._executorDeserializeCpuTime();
   }

   public Seq<AccumulatorV2<?, ?>> collectAccumulatorUpdates(boolean taskFailed) {
      return this.context() == null ? (Seq)Seq$.MODULE$.empty() : (Seq)this.context().taskMetrics().nonZeroInternalAccums().$plus$plus((GenTraversableOnce)this.context().taskMetrics().externalAccums().filter(new Serializable(this, taskFailed) {
         public static final long serialVersionUID = 0L;
         private final boolean taskFailed$1;

         public final boolean apply(AccumulatorV2<?, ?> a) {
            return !this.taskFailed$1 || a.countFailedValues();
         }

         public {
            this.taskFailed$1 = taskFailed$1;
         }
      }), Seq$.MODULE$.canBuildFrom());
   }

   public boolean collectAccumulatorUpdates$default$1() {
      return false;
   }

   public void kill(boolean interruptThread, String reason) {
      Predef$.MODULE$.require(reason != null);
      this._reasonIfKilled_$eq(reason);
      if (this.context() != null) {
         this.context().markInterrupted(reason);
      }

      if (interruptThread && this.taskThread() != null) {
         this.taskThread().interrupt();
      }

   }

   public Task(int stageId, int stageAttemptId, int partitionId, Properties localProperties, byte[] serializedTaskMetrics, Option<Object> jobId, Option<String> appId, Option<String> appAttemptId) {
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      this.partitionId = partitionId;
      this.localProperties = localProperties;
      this.serializedTaskMetrics = serializedTaskMetrics;
      this.jobId = jobId;
      this.appId = appId;
      this.appAttemptId = appAttemptId;
      super();
      this.epoch = -1L;
      this._reasonIfKilled = null;
      this._executorDeserializeTime = 0L;
      this._executorDeserializeCpuTime = 0L;
   }
}
