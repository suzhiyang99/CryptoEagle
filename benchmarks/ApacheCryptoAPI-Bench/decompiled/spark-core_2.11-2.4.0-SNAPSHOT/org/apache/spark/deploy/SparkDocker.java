package org.apache.spark.deploy;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u00011;Q!\u0001\u0002\t\n-\t1b\u00159be.$unY6fe*\u00111\u0001B\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001\u0001\u0005\u0002\r\u001b5\t!AB\u0003\u000f\u0005!%qBA\u0006Ta\u0006\u00148\u000eR8dW\u0016\u00148CA\u0007\u0011!\t\tB#D\u0001\u0013\u0015\u0005\u0019\u0012!B:dC2\f\u0017BA\u000b\u0013\u0005\u0019\te.\u001f*fM\")q#\u0004C\u00011\u00051A(\u001b8jiz\"\u0012a\u0003\u0005\u000655!\taG\u0001\fgR\f'\u000f^'bgR,'\u000f\u0006\u0002\u001d?A\u0011A\"H\u0005\u0003=\t\u0011a\u0002V3ti6\u000b7\u000f^3s\u0013:4w\u000eC\u0003!3\u0001\u0007\u0011%\u0001\u0005n_VtG\u000fR5s!\t\u0011SE\u0004\u0002\u0012G%\u0011AEE\u0001\u0007!J,G-\u001a4\n\u0005\u0019:#AB*ue&twM\u0003\u0002%%!)\u0011&\u0004C\u0001U\u0005Y1\u000f^1si^{'o[3s)\rYcf\f\t\u0003\u00191J!!\f\u0002\u0003\u001dQ+7\u000f^,pe.,'/\u00138g_\")\u0001\u0005\u000ba\u0001C!)\u0001\u0007\u000ba\u0001C\u00059Q.Y:uKJ\u001c\b\"\u0002\u001a\u000e\t\u0013\u0019\u0014!C:uCJ$hj\u001c3f)\t!$\tE\u0003\u0012k\u0005:$(\u0003\u00027%\t1A+\u001e9mKN\u0002\"\u0001\u0004\u001d\n\u0005e\u0012!\u0001\u0003#pG.,'/\u00133\u0011\u0005m\u0002U\"\u0001\u001f\u000b\u0005ur\u0014AA5p\u0015\u0005y\u0014\u0001\u00026bm\u0006L!!\u0011\u001f\u0003\t\u0019KG.\u001a\u0005\u0006\u0007F\u0002\r\u0001R\u0001\nI>\u001c7.\u001a:D[\u0012\u0004\"!\u0012&\u000e\u0003\u0019S!a\u0012%\u0002\u000fA\u0014xnY3tg*\u0011\u0011JE\u0001\u0004gf\u001c\u0018BA&G\u00059\u0001&o\\2fgN\u0014U/\u001b7eKJ\u0004"
)
public final class SparkDocker {
   public static TestWorkerInfo startWorker(String var0, String var1) {
      return SparkDocker$.MODULE$.startWorker(var0, var1);
   }

   public static TestMasterInfo startMaster(String var0) {
      return SparkDocker$.MODULE$.startMaster(var0);
   }
}
