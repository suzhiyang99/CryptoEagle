package org.apache.spark.util.random;

import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001=<a!\u0001\u0002\t\u0002\u0019a\u0011!D*b[Bd\u0017N\\4Vi&d7O\u0003\u0002\u0004\t\u00051!/\u00198e_6T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOB\u0011QBD\u0007\u0002\u0005\u00191qB\u0001E\u0001\rA\u0011QbU1na2LgnZ+uS2\u001c8C\u0001\b\u0012!\t\u0011R#D\u0001\u0014\u0015\u0005!\u0012!B:dC2\f\u0017B\u0001\f\u0014\u0005\u0019\te.\u001f*fM\")\u0001D\u0004C\u00015\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\r\u0011\u0015ab\u0002\"\u0001\u001e\u0003]\u0011Xm]3sm>L'oU1na2,\u0017I\u001c3D_VtG/\u0006\u0002\u001fQQ!q\u0004\u0010&P)\t\u0001C\u0007\u0005\u0003\u0013C\r\n\u0014B\u0001\u0012\u0014\u0005\u0019!V\u000f\u001d7feA\u0019!\u0003\n\u0014\n\u0005\u0015\u001a\"!B!se\u0006L\bCA\u0014)\u0019\u0001!Q!K\u000eC\u0002)\u0012\u0011\u0001V\t\u0003W9\u0002\"A\u0005\u0017\n\u00055\u001a\"a\u0002(pi\"Lgn\u001a\t\u0003%=J!\u0001M\n\u0003\u0007\u0005s\u0017\u0010\u0005\u0002\u0013e%\u00111g\u0005\u0002\u0005\u0019>tw\rC\u000467\u0005\u0005\t9\u0001\u001c\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007E\u00028u\u0019j\u0011\u0001\u000f\u0006\u0003sM\tqA]3gY\u0016\u001cG/\u0003\u0002<q\tA1\t\\1tgR\u000bw\rC\u0003>7\u0001\u0007a(A\u0003j]B,H\u000fE\u0002@\u000f\u001ar!\u0001Q#\u000f\u0005\u0005#U\"\u0001\"\u000b\u0005\rK\u0012A\u0002\u001fs_>$h(C\u0001\u0015\u0013\t15#A\u0004qC\u000e\\\u0017mZ3\n\u0005!K%\u0001C%uKJ\fGo\u001c:\u000b\u0005\u0019\u001b\u0002\"B&\u001c\u0001\u0004a\u0015!A6\u0011\u0005Ii\u0015B\u0001(\u0014\u0005\rIe\u000e\u001e\u0005\b!n\u0001\n\u00111\u00012\u0003\u0011\u0019X-\u001a3\t\u000bIsA\u0011A*\u00029\r|W\u000e];uK\u001a\u0013\u0018m\u0019;j_:4uN]*b[BdWmU5{KR!AkV-\\!\t\u0011R+\u0003\u0002W'\t1Ai\\;cY\u0016DQ\u0001W)A\u00021\u000bAc]1na2,7+\u001b>f\u0019><XM\u001d\"pk:$\u0007\"\u0002.R\u0001\u0004\t\u0014!\u0002;pi\u0006d\u0007\"\u0002/R\u0001\u0004i\u0016aD<ji\"\u0014V\r\u001d7bG\u0016lWM\u001c;\u0011\u0005Iq\u0016BA0\u0014\u0005\u001d\u0011un\u001c7fC:Dq!\u0019\b\u0012\u0002\u0013\u0005!-A\u0011sKN,'O^8jeN\u000bW\u000e\u001d7f\u0003:$7i\\;oi\u0012\"WMZ1vYR$3'\u0006\u0002d]V\tAM\u000b\u00022K.\na\r\u0005\u0002hY6\t\u0001N\u0003\u0002jU\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003WN\t!\"\u00198o_R\fG/[8o\u0013\ti\u0007NA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016$Q!\u000b1C\u0002)\u0002"
)
public final class SamplingUtils {
   public static <T> long reservoirSampleAndCount$default$3() {
      return SamplingUtils$.MODULE$.reservoirSampleAndCount$default$3();
   }

   public static double computeFractionForSampleSize(int var0, long var1, boolean var3) {
      return SamplingUtils$.MODULE$.computeFractionForSampleSize(var0, var1, var3);
   }

   public static <T> Tuple2<Object, Object> reservoirSampleAndCount(Iterator<T> var0, int var1, long var2, ClassTag<T> var4) {
      return SamplingUtils$.MODULE$.reservoirSampleAndCount(var0, var1, var2, var4);
   }
}
