package org.apache.spark.storage;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import scala.Function0;
import scala.collection.Map;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A<a!\u0001\u0002\t\u0002\u0011Q\u0011\u0001D*u_J\fw-Z+uS2\u001c(BA\u0002\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'o\u001a\t\u0003\u00171i\u0011A\u0001\u0004\u0007\u001b\tA\t\u0001\u0002\b\u0003\u0019M#xN]1hKV#\u0018\u000e\\:\u0014\u00071yQ\u0003\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012C\u0001\u0004B]f\u0014VM\u001a\t\u0003-ei\u0011a\u0006\u0006\u00031\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00035]\u0011q\u0001T8hO&tw\rC\u0003\u001d\u0019\u0011\u0005a$\u0001\u0004=S:LGOP\u0002\u0001)\u0005Q\u0001\"\u0002\u0011\r\t\u0003\t\u0013a\u00023jgB|7/\u001a\u000b\u0003E\u0015\u0002\"\u0001E\u0012\n\u0005\u0011\n\"\u0001B+oSRDQAJ\u0010A\u0002\u001d\naAY;gM\u0016\u0014\bC\u0001\u0015.\u001b\u0005I#B\u0001\u0016,\u0003\rq\u0017n\u001c\u0006\u0002Y\u0005!!.\u0019<b\u0013\tq\u0013F\u0001\u0006CsR,')\u001e4gKJDQ\u0001\r\u0007\u0005\nE\n\u0011c\u00197fC:$\u0015N]3di\n+hMZ3s)\t\u0011#\u0007C\u0003'_\u0001\u00071\u0007\u0005\u00025u5\tQG\u0003\u00027o\u0005\u00111\r\u001b\u0006\u0003UaR\u0011!O\u0001\u0004gVt\u0017BA\u001e6\u00051!\u0015N]3di\n+hMZ3s\u0011\u0015iD\u0002\"\u0001?\u00035)\b\u000fZ1uKJ#G-\u00138g_R\u0019!e\u0010)\t\u000b\u0001c\u0004\u0019A!\u0002\u0011I$G-\u00138g_N\u00042A\u0011&N\u001d\t\u0019\u0005J\u0004\u0002E\u000f6\tQI\u0003\u0002G;\u00051AH]8pizJ\u0011AE\u0005\u0003\u0013F\tq\u0001]1dW\u0006<W-\u0003\u0002L\u0019\n\u00191+Z9\u000b\u0005%\u000b\u0002CA\u0006O\u0013\ty%AA\u0004S\t\u0012KeNZ8\t\u000bEc\u0004\u0019\u0001*\u0002\u0011M$\u0018\r^;tKN\u00042A\u0011&T!\tYA+\u0003\u0002V\u0005\ti1\u000b^8sC\u001e,7\u000b^1ukNDQa\u0016\u0007\u0005\u0002a\u000bAcZ3u%\u0012$'\t\\8dW2{7-\u0019;j_:\u001cHcA-k_B!!,X0c\u001b\u0005Y&B\u0001/\u0012\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003=n\u00131!T1q!\tY\u0001-\u0003\u0002b\u0005\t9!\t\\8dW&#\u0007c\u0001\"KGB\u0011Am\u001a\b\u0003!\u0015L!AZ\t\u0002\rA\u0013X\rZ3g\u0013\tA\u0017N\u0001\u0004TiJLgn\u001a\u0006\u0003MFAQa\u001b,A\u00021\fQA\u001d3e\u0013\u0012\u0004\"\u0001E7\n\u00059\f\"aA%oi\")\u0011K\u0016a\u0001%\u0002"
)
public final class StorageUtils {
   public static boolean initializeLogIfNecessary$default$2() {
      return StorageUtils$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return StorageUtils$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      StorageUtils$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return StorageUtils$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      StorageUtils$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      StorageUtils$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      StorageUtils$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      StorageUtils$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      StorageUtils$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      StorageUtils$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      StorageUtils$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      StorageUtils$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      StorageUtils$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      StorageUtils$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return StorageUtils$.MODULE$.log();
   }

   public static String logName() {
      return StorageUtils$.MODULE$.logName();
   }

   public static Map<BlockId, Seq<String>> getRddBlockLocations(int var0, Seq<StorageStatus> var1) {
      return StorageUtils$.MODULE$.getRddBlockLocations(var0, var1);
   }

   public static void updateRddInfo(Seq<RDDInfo> var0, Seq<StorageStatus> var1) {
      StorageUtils$.MODULE$.updateRddInfo(var0, var1);
   }

   public static void dispose(ByteBuffer var0) {
      StorageUtils$.MODULE$.dispose(var0);
   }
}
