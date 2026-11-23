package org.apache.spark.network;

import java.nio.ByteBuffer;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.buffer.FileSegmentManagedBuffer;
import org.apache.spark.network.buffer.ManagedBuffer;
import org.apache.spark.network.buffer.NioManagedBuffer;
import org.apache.spark.network.shuffle.BlockFetchingListener;
import org.apache.spark.network.shuffle.ShuffleClient;
import org.apache.spark.network.shuffle.TempFileManager;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.ThreadUtils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.concurrent.Future;
import scala.concurrent.Promise;
import scala.concurrent.Promise$;
import scala.concurrent.duration.Duration$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005UcAB\u0001\u0003\u0003\u0003!!B\u0001\u000bCY>\u001c7\u000e\u0016:b]N4WM]*feZL7-\u001a\u0006\u0003\u0007\u0011\tqA\\3uo>\u00148N\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\u0011\u00011\"E\r\u0011\u00051yQ\"A\u0007\u000b\u00059\u0011\u0011aB:ik\u001a4G.Z\u0005\u0003!5\u0011Qb\u00155vM\u001adWm\u00117jK:$\bC\u0001\n\u0018\u001b\u0005\u0019\"B\u0001\u000b\u0016\u0003\tIwNC\u0001\u0017\u0003\u0011Q\u0017M^1\n\u0005a\u0019\"!C\"m_N,\u0017M\u00197f!\tQR$D\u0001\u001c\u0015\taB!\u0001\u0005j]R,'O\\1m\u0013\tq2DA\u0004M_\u001e<\u0017N\\4\t\u000b\u0001\u0002A\u0011\u0001\u0012\u0002\rqJg.\u001b;?\u0007\u0001!\u0012a\t\t\u0003I\u0001i\u0011A\u0001\u0005\u0006M\u00011\taJ\u0001\u0005S:LG\u000f\u0006\u0002)]A\u0011\u0011\u0006L\u0007\u0002U)\t1&A\u0003tG\u0006d\u0017-\u0003\u0002.U\t!QK\\5u\u0011\u0015yS\u00051\u00011\u0003A\u0011Gn\\2l\t\u0006$\u0018-T1oC\u001e,'\u000f\u0005\u0002%c%\u0011!G\u0001\u0002\u0011\u00052|7m\u001b#bi\u0006l\u0015M\\1hKJDQ\u0001\u000e\u0001\u0007\u0002U\nQa\u00197pg\u0016$\u0012\u0001\u000b\u0005\u0006o\u00011\t\u0001O\u0001\u0005a>\u0014H/F\u0001:!\tI#(\u0003\u0002<U\t\u0019\u0011J\u001c;\t\u000bu\u0002a\u0011\u0001 \u0002\u0011!|7\u000f\u001e(b[\u0016,\u0012a\u0010\t\u0003\u0001\u000es!!K!\n\u0005\tS\u0013A\u0002)sK\u0012,g-\u0003\u0002E\u000b\n11\u000b\u001e:j]\u001eT!A\u0011\u0016\t\u000b\u001d\u0003a\u0011\t%\u0002\u0017\u0019,Go\u00195CY>\u001c7n\u001d\u000b\bQ%[EJT*Y\u0011\u0015Qe\t1\u0001@\u0003\u0011Awn\u001d;\t\u000b]2\u0005\u0019A\u001d\t\u000b53\u0005\u0019A \u0002\r\u0015DXmY%e\u0011\u0015ye\t1\u0001Q\u0003!\u0011Gn\\2l\u0013\u0012\u001c\bcA\u0015R\u007f%\u0011!K\u000b\u0002\u0006\u0003J\u0014\u0018-\u001f\u0005\u0006)\u001a\u0003\r!V\u0001\tY&\u001cH/\u001a8feB\u0011ABV\u0005\u0003/6\u0011QC\u00117pG.4U\r^2iS:<G*[:uK:,'\u000fC\u0003Z\r\u0002\u0007!,A\buK6\u0004h)\u001b7f\u001b\u0006t\u0017mZ3s!\ta1,\u0003\u0002]\u001b\tyA+Z7q\r&dW-T1oC\u001e,'\u000fC\u0003_\u0001\u0019\u0005q,A\u0006va2|\u0017\r\u001a\"m_\u000e\\G\u0003\u00031gQ&T'O_@\u0011\u0007\u0005$\u0007&D\u0001c\u0015\t\u0019'&\u0001\u0006d_:\u001cWO\u001d:f]RL!!\u001a2\u0003\r\u0019+H/\u001e:f\u0011\u00159W\f1\u0001@\u0003!Awn\u001d;oC6,\u0007\"B\u001c^\u0001\u0004I\u0004\"B'^\u0001\u0004y\u0004\"B6^\u0001\u0004a\u0017a\u00022m_\u000e\\\u0017\n\u001a\t\u0003[Bl\u0011A\u001c\u0006\u0003_\u0012\tqa\u001d;pe\u0006<W-\u0003\u0002r]\n9!\t\\8dW&#\u0007\"B:^\u0001\u0004!\u0018!\u00032m_\u000e\\G)\u0019;b!\t)\b0D\u0001w\u0015\t9(!\u0001\u0004ck\u001a4WM]\u0005\u0003sZ\u0014Q\"T1oC\u001e,GMQ;gM\u0016\u0014\b\"B>^\u0001\u0004a\u0018!\u00027fm\u0016d\u0007CA7~\u0013\tqhN\u0001\u0007Ti>\u0014\u0018mZ3MKZ,G\u000eC\u0004\u0002\u0002u\u0003\r!a\u0001\u0002\u0011\rd\u0017m]:UC\u001e\u0004D!!\u0002\u0002\u0016A1\u0011qAA\u0007\u0003#i!!!\u0003\u000b\u0007\u0005-!&A\u0004sK\u001adWm\u0019;\n\t\u0005=\u0011\u0011\u0002\u0002\t\u00072\f7o\u001d+bOB!\u00111CA\u000b\u0019\u0001!1\"a\u0006\u0000\u0003\u0003\u0005\tQ!\u0001\u0002\u001a\t\u0019q\fJ\u0019\u0012\t\u0005m\u0011\u0011\u0005\t\u0004S\u0005u\u0011bAA\u0010U\t9aj\u001c;iS:<\u0007cA\u0015\u0002$%\u0019\u0011Q\u0005\u0016\u0003\u0007\u0005s\u0017\u0010C\u0004\u0002*\u0001!\t!a\u000b\u0002\u001d\u0019,Go\u00195CY>\u001c7nU=oGRYA/!\f\u00020\u0005E\u00121GA\u001b\u0011\u0019Q\u0015q\u0005a\u0001\u007f!1q'a\nA\u0002eBa!TA\u0014\u0001\u0004y\u0004BB6\u0002(\u0001\u0007q\b\u0003\u0004Z\u0003O\u0001\rA\u0017\u0005\b\u0003s\u0001A\u0011AA\u001e\u0003=)\b\u000f\\8bI\ncwnY6Ts:\u001cGc\u0004\u0015\u0002>\u0005}\u0012\u0011IA\"\u0003\u000b\n9%!\u0013\t\r\u001d\f9\u00041\u0001@\u0011\u00199\u0014q\u0007a\u0001s!1Q*a\u000eA\u0002}Baa[A\u001c\u0001\u0004a\u0007BB:\u00028\u0001\u0007A\u000f\u0003\u0004|\u0003o\u0001\r\u0001 \u0005\t\u0003\u0003\t9\u00041\u0001\u0002LA\"\u0011QJA)!\u0019\t9!!\u0004\u0002PA!\u00111CA)\t1\t\u0019&!\u0013\u0002\u0002\u0003\u0005)\u0011AA\r\u0005\ryFE\r"
)
public abstract class BlockTransferService extends ShuffleClient implements Logging {
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

   public abstract void init(BlockDataManager var1);

   public abstract void close();

   public abstract int port();

   public abstract String hostName();

   public abstract void fetchBlocks(String var1, int var2, String var3, String[] var4, BlockFetchingListener var5, TempFileManager var6);

   public abstract Future<BoxedUnit> uploadBlock(String var1, int var2, String var3, BlockId var4, ManagedBuffer var5, StorageLevel var6, ClassTag<?> var7);

   public ManagedBuffer fetchBlockSync(String host, int port, String execId, String blockId, TempFileManager tempFileManager) {
      Promise result = Promise$.MODULE$.apply();
      this.fetchBlocks(host, port, execId, (String[])((Object[])(new String[]{blockId})), new BlockFetchingListener(this, result) {
         private final Promise result$1;

         public void onBlockFetchFailure(String blockId, Throwable exception) {
            this.result$1.failure(exception);
         }

         public void onBlockFetchSuccess(String blockId, ManagedBuffer data) {
            if (data instanceof FileSegmentManagedBuffer) {
               FileSegmentManagedBuffer var4 = (FileSegmentManagedBuffer)data;
               this.result$1.success(var4);
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               ByteBuffer ret = ByteBuffer.allocate((int)data.size());
               ret.put(data.nioByteBuffer());
               ret.flip();
               this.result$1.success(new NioManagedBuffer(ret));
               BoxedUnit var7 = BoxedUnit.UNIT;
            }

         }

         public {
            this.result$1 = result$1;
         }
      }, tempFileManager);
      return (ManagedBuffer)ThreadUtils$.MODULE$.awaitResult(result.future(), Duration$.MODULE$.Inf());
   }

   public void uploadBlockSync(String hostname, int port, String execId, BlockId blockId, ManagedBuffer blockData, StorageLevel level, ClassTag<?> classTag) {
      Future future = this.uploadBlock(hostname, port, execId, blockId, blockData, level, classTag);
      ThreadUtils$.MODULE$.awaitResult(future, Duration$.MODULE$.Inf());
   }

   public BlockTransferService() {
      Logging$class.$init$(this);
   }
}
