package org.apache.spark.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Properties;
import javax.crypto.spec.SecretKeySpec;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.config.package$;
import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005muAB\u0001\u0003\u0011\u0003!!\"A\tDef\u0004Ho\\*ue\u0016\fW.\u0016;jYNT!a\u0001\u0003\u0002\u0011M,7-\u001e:jifT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'o\u001a\t\u0003\u00171i\u0011A\u0001\u0004\u0007\u001b\tA\t\u0001\u0002\b\u0003#\r\u0013\u0018\u0010\u001d;p'R\u0014X-Y7Vi&d7oE\u0002\r\u001fU\u0001\"\u0001E\n\u000e\u0003EQ\u0011AE\u0001\u0006g\u000e\fG.Y\u0005\u0003)E\u0011a!\u00118z%\u00164\u0007C\u0001\f\u001a\u001b\u00059\"B\u0001\r\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u000e\u0018\u0005\u001daunZ4j]\u001eDQ\u0001\b\u0007\u0005\u0002y\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0015!9\u0001\u0005\u0004b\u0001\n\u0003\t\u0013AE%W?2+ej\u0012+I?&suLQ-U\u000bN+\u0012A\t\t\u0003!\rJ!\u0001J\t\u0003\u0007%sG\u000f\u0003\u0004'\u0019\u0001\u0006IAI\u0001\u0014\u0013Z{F*\u0012(H)\"{\u0016JT0C3R+5\u000b\t\u0005\bQ1\u0011\r\u0011\"\u0001*\u0003%\u001a\u0006+\u0011*L?&{u,\u0012(D%f\u0003F+S(O?\u000e{U*T(O'~\u001buJ\u0014$J\u000f~\u0003&+\u0012$J1V\t!\u0006\u0005\u0002,a5\tAF\u0003\u0002.]\u0005!A.\u00198h\u0015\u0005y\u0013\u0001\u00026bm\u0006L!!\r\u0017\u0003\rM#(/\u001b8h\u0011\u0019\u0019D\u0002)A\u0005U\u0005Q3\u000bU!S\u0017~KujX#O\u0007JK\u0006\u000bV%P\u001d~\u001bu*T'P\u001dN{6i\u0014(G\u0013\u001e{\u0006KU#G\u0013b\u0003\u0003\"B\u001b\r\t\u00031\u0014\u0001G2sK\u0006$Xm\u0011:zaR|w*\u001e;qkR\u001cFO]3b[R!q'P F!\tA4(D\u0001:\u0015\tQd&\u0001\u0002j_&\u0011A(\u000f\u0002\r\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\u0006}Q\u0002\raN\u0001\u0003_NDQ\u0001\u0011\u001bA\u0002\u0005\u000b\u0011b\u001d9be.\u001cuN\u001c4\u0011\u0005\t\u001bU\"\u0001\u0003\n\u0005\u0011#!!C*qCJ\\7i\u001c8g\u0011\u00151E\u00071\u0001H\u0003\rYW-\u001f\t\u0004!!S\u0015BA%\u0012\u0005\u0015\t%O]1z!\t\u00012*\u0003\u0002M#\t!!)\u001f;f\u0011\u0015qE\u0002\"\u0001P\u0003U\u0019'/Z1uK^\u0013\u0018\u000e^1cY\u0016\u001c\u0005.\u00198oK2$B\u0001\u0015-[7B\u0011\u0011KV\u0007\u0002%*\u00111\u000bV\u0001\tG\"\fgN\\3mg*\u0011QKL\u0001\u0004]&|\u0017BA,S\u0005M9&/\u001b;bE2,')\u001f;f\u0007\"\fgN\\3m\u0011\u0015IV\n1\u0001Q\u0003\u001d\u0019\u0007.\u00198oK2DQ\u0001Q'A\u0002\u0005CQAR'A\u0002\u001dCQ!\u0018\u0007\u0005\u0002y\u000bqc\u0019:fCR,7I]=qi>Le\u000e];u'R\u0014X-Y7\u0015\t}\u0013G-\u001a\t\u0003q\u0001L!!Y\u001d\u0003\u0017%s\u0007/\u001e;TiJ,\u0017-\u001c\u0005\u0006Gr\u0003\raX\u0001\u0003SNDQ\u0001\u0011/A\u0002\u0005CQA\u0012/A\u0002\u001dCQa\u001a\u0007\u0005\u0002!\fQc\u0019:fCR,'+Z1eC\ndWm\u00115b]:,G\u000e\u0006\u0003jY6t\u0007CA)k\u0013\tY'KA\nSK\u0006$\u0017M\u00197f\u0005f$Xm\u00115b]:,G\u000eC\u0003ZM\u0002\u0007\u0011\u000eC\u0003AM\u0002\u0007\u0011\tC\u0003GM\u0002\u0007q\tC\u0003q\u0019\u0011\u0005\u0011/\u0001\u0007u_\u000e\u0013\u0018\u0010\u001d;p\u0007>tg\r\u0006\u0002sqB\u00111O^\u0007\u0002i*\u0011QOL\u0001\u0005kRLG.\u0003\u0002xi\nQ\u0001K]8qKJ$\u0018.Z:\t\u000be|\u0007\u0019A!\u0002\t\r|gN\u001a\u0005\u0006w2!\t\u0001`\u0001\nGJ,\u0017\r^3LKf$\"aR?\t\u000beT\b\u0019A!\t\u000f}d\u0001\u0015\"\u0003\u0002\u0002\u0005Q2M]3bi\u0016Le.\u001b;jC2L'0\u0019;j_:4Vm\u0019;peR\u0019q)a\u0001\t\r\u0005\u0015a\u00101\u0001s\u0003)\u0001(o\u001c9feRLWm\u001d\u0004\u0007\u0003\u0013aA!a\u0003\u0003'\r\u0013\u0018\u0010\u001d;p\u0011\u0016d\u0007/\u001a:DQ\u0006tg.\u001a7\u0014\u000b\u0005\u001d\u0011Q\u0002)\u0011\u0007-\ny!C\u0002\u0002\u00121\u0012aa\u00142kK\u000e$\bBCA\u000b\u0003\u000f\u0011\t\u0011)A\u0005!\u0006!1/\u001b8l\u0011\u001da\u0012q\u0001C\u0001\u00033!B!a\u0007\u0002 A!\u0011QDA\u0004\u001b\u0005a\u0001bBA\u000b\u0003/\u0001\r\u0001\u0015\u0005\t\u0003G\t9\u0001\"\u0011\u0002&\u0005)qO]5uKR\u0019!%a\n\t\u0011\u0005%\u0012\u0011\u0005a\u0001\u0003W\t1a\u001d:d!\u0011\ti#a\f\u000e\u0003QK1!!\rU\u0005)\u0011\u0015\u0010^3Ck\u001a4WM\u001d\u0005\t\u0003k\t9\u0001\"\u0011\u00028\u00051\u0011n](qK:$\"!!\u000f\u0011\u0007A\tY$C\u0002\u0002>E\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005\u0002B\u0005\u001dA\u0011IA\"\u0003\u0015\u0019Gn\\:f)\t\t)\u0005E\u0002\u0011\u0003\u000fJ1!!\u0013\u0012\u0005\u0011)f.\u001b;\u0007\r\u00055C\u0002BA(\u00051\u0019%/\u001f9u_B\u000b'/Y7t'\r\tYe\u0004\u0005\n\r\u0006-#\u0011!Q\u0001\n\u001dC\u0011\u0002QA&\u0005\u0003\u0005\u000b\u0011B!\t\u000fq\tY\u0005\"\u0001\u0002XQ1\u0011\u0011LA.\u0003;\u0002B!!\b\u0002L!1a)!\u0016A\u0002\u001dCa\u0001QA+\u0001\u0004\t\u0005BCA1\u0003\u0017\u0012\r\u0011\"\u0001\u0002d\u000591.Z=Ta\u0016\u001cWCAA3!\u0011\t9'!\u001e\u000e\u0005\u0005%$\u0002BA6\u0003[\nAa\u001d9fG*!\u0011qNA9\u0003\u0019\u0019'/\u001f9u_*\u0011\u00111O\u0001\u0006U\u00064\u0018\r_\u0005\u0005\u0003o\nIGA\u0007TK\u000e\u0014X\r^&fsN\u0003Xm\u0019\u0005\n\u0003w\nY\u0005)A\u0005\u0003K\n\u0001b[3z'B,7\r\t\u0005\u000b\u0003\u007f\nYE1A\u0005\u0002\u0005\u0005\u0015A\u0004;sC:\u001chm\u001c:nCRLwN\\\u000b\u0003\u0003\u0007\u0003B!!\"\u0002\f:\u0019\u0001#a\"\n\u0007\u0005%\u0015#\u0001\u0004Qe\u0016$WMZ\u0005\u0004c\u00055%bAAE#!I\u0011\u0011SA&A\u0003%\u00111Q\u0001\u0010iJ\fgn\u001d4pe6\fG/[8oA!I\u00110a\u0013C\u0002\u0013\u0005\u0011QS\u000b\u0002e\"A\u0011\u0011TA&A\u0003%!/A\u0003d_:4\u0007\u0005"
)
public final class CryptoStreamUtils {
   public static boolean initializeLogIfNecessary$default$2() {
      return CryptoStreamUtils$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return CryptoStreamUtils$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      CryptoStreamUtils$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return CryptoStreamUtils$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      CryptoStreamUtils$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      CryptoStreamUtils$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      CryptoStreamUtils$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      CryptoStreamUtils$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      CryptoStreamUtils$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      CryptoStreamUtils$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      CryptoStreamUtils$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      CryptoStreamUtils$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      CryptoStreamUtils$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      CryptoStreamUtils$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return CryptoStreamUtils$.MODULE$.log();
   }

   public static String logName() {
      return CryptoStreamUtils$.MODULE$.logName();
   }

   public static byte[] createKey(SparkConf var0) {
      return CryptoStreamUtils$.MODULE$.createKey(var0);
   }

   public static Properties toCryptoConf(SparkConf var0) {
      return CryptoStreamUtils$.MODULE$.toCryptoConf(var0);
   }

   public static ReadableByteChannel createReadableChannel(ReadableByteChannel var0, SparkConf var1, byte[] var2) {
      return CryptoStreamUtils$.MODULE$.createReadableChannel(var0, var1, var2);
   }

   public static InputStream createCryptoInputStream(InputStream var0, SparkConf var1, byte[] var2) {
      return CryptoStreamUtils$.MODULE$.createCryptoInputStream(var0, var1, var2);
   }

   public static WritableByteChannel createWritableChannel(WritableByteChannel var0, SparkConf var1, byte[] var2) {
      return CryptoStreamUtils$.MODULE$.createWritableChannel(var0, var1, var2);
   }

   public static OutputStream createCryptoOutputStream(OutputStream var0, SparkConf var1, byte[] var2) {
      return CryptoStreamUtils$.MODULE$.createCryptoOutputStream(var0, var1, var2);
   }

   public static String SPARK_IO_ENCRYPTION_COMMONS_CONFIG_PREFIX() {
      return CryptoStreamUtils$.MODULE$.SPARK_IO_ENCRYPTION_COMMONS_CONFIG_PREFIX();
   }

   public static int IV_LENGTH_IN_BYTES() {
      return CryptoStreamUtils$.MODULE$.IV_LENGTH_IN_BYTES();
   }

   public static class CryptoHelperChannel implements WritableByteChannel {
      private final WritableByteChannel sink;

      public int write(ByteBuffer src) {
         int count = src.remaining();

         while(src.hasRemaining()) {
            this.sink.write(src);
         }

         return count;
      }

      public boolean isOpen() {
         return this.sink.isOpen();
      }

      public void close() {
         this.sink.close();
      }

      public CryptoHelperChannel(WritableByteChannel sink) {
         this.sink = sink;
      }
   }

   public static class CryptoParams {
      private final SecretKeySpec keySpec;
      private final String transformation;
      private final Properties conf;

      public SecretKeySpec keySpec() {
         return this.keySpec;
      }

      public String transformation() {
         return this.transformation;
      }

      public Properties conf() {
         return this.conf;
      }

      public CryptoParams(byte[] key, SparkConf sparkConf) {
         this.keySpec = new SecretKeySpec(key, "AES");
         this.transformation = (String)sparkConf.get(package$.MODULE$.IO_CRYPTO_CIPHER_TRANSFORMATION());
         this.conf = CryptoStreamUtils$.MODULE$.toCryptoConf(sparkConf);
      }
   }
}
