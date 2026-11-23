package org.apache.spark.internal.config;

import java.util.concurrent.TimeUnit;
import org.apache.spark.network.util.ByteUnit;
import scala.Function1;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.util.matching.Regex;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]r!B\u0001\u0003\u0011\u0013i\u0011!D\"p]\u001aLw\rS3ma\u0016\u00148O\u0003\u0002\u0004\t\u000511m\u001c8gS\u001eT!!\u0002\u0004\u0002\u0011%tG/\u001a:oC2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001!\tqq\"D\u0001\u0003\r\u0015\u0001\"\u0001#\u0003\u0012\u00055\u0019uN\u001c4jO\"+G\u000e]3sgN\u0011qB\u0005\t\u0003'Yi\u0011\u0001\u0006\u0006\u0002+\u0005)1oY1mC&\u0011q\u0003\u0006\u0002\u0007\u0003:L(+\u001a4\t\u000beyA\u0011\u0001\u000e\u0002\rqJg.\u001b;?)\u0005i\u0001\"\u0002\u000f\u0010\t\u0003i\u0012\u0001\u0003;p\u001dVl'-\u001a:\u0016\u0005y\tC#B\u0010+gaR\u0004C\u0001\u0011\"\u0019\u0001!QAI\u000eC\u0002\r\u0012\u0011\u0001V\t\u0003I\u001d\u0002\"aE\u0013\n\u0005\u0019\"\"a\u0002(pi\"Lgn\u001a\t\u0003'!J!!\u000b\u000b\u0003\u0007\u0005s\u0017\u0010C\u0003,7\u0001\u0007A&A\u0001t!\ti\u0003G\u0004\u0002\u0014]%\u0011q\u0006F\u0001\u0007!J,G-\u001a4\n\u0005E\u0012$AB*ue&twM\u0003\u00020)!)Ag\u0007a\u0001k\u0005I1m\u001c8wKJ$XM\u001d\t\u0005'Ybs$\u0003\u00028)\tIa)\u001e8di&|g.\r\u0005\u0006sm\u0001\r\u0001L\u0001\u0004W\u0016L\b\"B\u001e\u001c\u0001\u0004a\u0013AC2p]\u001aLw\rV=qK\")Qh\u0004C\u0001}\u0005IAo\u001c\"p_2,\u0017M\u001c\u000b\u0004\u007f\t\u001b\u0005CA\nA\u0013\t\tECA\u0004C_>dW-\u00198\t\u000b-b\u0004\u0019\u0001\u0017\t\u000beb\u0004\u0019\u0001\u0017\t\u000b\u0015{A\u0011\u0001$\u0002\u0017M$(/\u001b8h)>\u001cV-]\u000b\u0003\u000fV#2\u0001\u0013,Y!\rI\u0015\u000b\u0016\b\u0003\u0015>s!a\u0013(\u000e\u00031S!!\u0014\u0007\u0002\rq\u0012xn\u001c;?\u0013\u0005)\u0012B\u0001)\u0015\u0003\u001d\u0001\u0018mY6bO\u0016L!AU*\u0003\u0007M+\u0017O\u0003\u0002Q)A\u0011\u0001%\u0016\u0003\u0006E\u0011\u0013\ra\t\u0005\u0006/\u0012\u0003\r\u0001L\u0001\u0004gR\u0014\b\"\u0002\u001bE\u0001\u0004I\u0006\u0003B\n7YQCQaW\b\u0005\u0002q\u000b1b]3r)>\u001cFO]5oOV\u0011QL\u0019\u000b\u0004Yy\u001b\u0007\"B0[\u0001\u0004\u0001\u0017!\u0001<\u0011\u0007%\u000b\u0016\r\u0005\u0002!E\u0012)!E\u0017b\u0001G!)AM\u0017a\u0001K\u0006y1\u000f\u001e:j]\u001e\u001cuN\u001c<feR,'\u000f\u0005\u0003\u0014m\u0005d\u0003\"B4\u0010\t\u0003A\u0017A\u0004;j[\u00164%o\\7TiJLgn\u001a\u000b\u0004S2l\u0007CA\nk\u0013\tYGC\u0001\u0003M_:<\u0007\"B,g\u0001\u0004a\u0003\"\u00028g\u0001\u0004y\u0017\u0001B;oSR\u0004\"\u0001]<\u000e\u0003ET!A]:\u0002\u0015\r|gnY;se\u0016tGO\u0003\u0002uk\u0006!Q\u000f^5m\u0015\u00051\u0018\u0001\u00026bm\u0006L!\u0001_9\u0003\u0011QKW.Z+oSRDQA_\b\u0005\u0002m\fA\u0002^5nKR{7\u000b\u001e:j]\u001e$2\u0001\f?~\u0011\u0015y\u0016\u00101\u0001j\u0011\u0015q\u0017\u00101\u0001p\u0011\u0019yx\u0002\"\u0001\u0002\u0002\u0005q!-\u001f;f\rJ|Wn\u0015;sS:<G#B5\u0002\u0004\u0005\u0015\u0001\"B,\u007f\u0001\u0004a\u0003B\u00028\u007f\u0001\u0004\t9\u0001\u0005\u0003\u0002\n\u0005EQBAA\u0006\u0015\r!\u0018Q\u0002\u0006\u0004\u0003\u001f1\u0011a\u00028fi^|'o[\u0005\u0005\u0003'\tYA\u0001\u0005CsR,WK\\5u\u0011\u001d\t9b\u0004C\u0001\u00033\tABY=uKR{7\u000b\u001e:j]\u001e$R\u0001LA\u000e\u0003;AaaXA\u000b\u0001\u0004I\u0007b\u00028\u0002\u0016\u0001\u0007\u0011q\u0001\u0005\b\u0003CyA\u0011AA\u0012\u0003=\u0011XmZ3y\rJ|Wn\u0015;sS:<GCBA\u0013\u0003g\t)\u0004\u0005\u0003\u0002(\u0005=RBAA\u0015\u0015\u0011\tY#!\f\u0002\u00115\fGo\u00195j]\u001eT!\u0001\u001e\u000b\n\t\u0005E\u0012\u0011\u0006\u0002\u0006%\u0016<W\r\u001f\u0005\u0007/\u0006}\u0001\u0019\u0001\u0017\t\re\ny\u00021\u0001-\u0001"
)
public final class ConfigHelpers {
   public static Regex regexFromString(String var0, String var1) {
      return ConfigHelpers$.MODULE$.regexFromString(var0, var1);
   }

   public static String byteToString(long var0, ByteUnit var2) {
      return ConfigHelpers$.MODULE$.byteToString(var0, var2);
   }

   public static long byteFromString(String var0, ByteUnit var1) {
      return ConfigHelpers$.MODULE$.byteFromString(var0, var1);
   }

   public static String timeToString(long var0, TimeUnit var2) {
      return ConfigHelpers$.MODULE$.timeToString(var0, var2);
   }

   public static long timeFromString(String var0, TimeUnit var1) {
      return ConfigHelpers$.MODULE$.timeFromString(var0, var1);
   }

   public static <T> String seqToString(Seq<T> var0, Function1<T, String> var1) {
      return ConfigHelpers$.MODULE$.seqToString(var0, var1);
   }

   public static <T> Seq<T> stringToSeq(String var0, Function1<String, T> var1) {
      return ConfigHelpers$.MODULE$.<T>stringToSeq(var0, var1);
   }

   public static boolean toBoolean(String var0, String var1) {
      return ConfigHelpers$.MODULE$.toBoolean(var0, var1);
   }

   public static <T> T toNumber(String var0, Function1<String, T> var1, String var2, String var3) {
      return (T)ConfigHelpers$.MODULE$.toNumber(var0, var1, var2, var3);
   }
}
