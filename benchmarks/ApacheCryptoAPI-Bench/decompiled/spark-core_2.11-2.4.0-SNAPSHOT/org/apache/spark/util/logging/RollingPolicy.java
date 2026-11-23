package org.apache.spark.util.logging;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001Q2\u0001\"\u0001\u0002\u0011\u0002G\u0005a\u0001\u0004\u0002\u000e%>dG.\u001b8h!>d\u0017nY=\u000b\u0005\r!\u0011a\u00027pO\u001eLgn\u001a\u0006\u0003\u000b\u0019\tA!\u001e;jY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001aDQ\u0001\u0006\u0001\u0007\u0002Y\tab\u001d5pk2$'k\u001c7m_Z,'o\u0001\u0001\u0015\u0005]Q\u0002C\u0001\b\u0019\u0013\tIrBA\u0004C_>dW-\u00198\t\u000bm\u0019\u0002\u0019\u0001\u000f\u0002!\tLH/Z:U_\n+wK]5ui\u0016t\u0007C\u0001\b\u001e\u0013\tqrB\u0001\u0003M_:<\u0007\"\u0002\u0011\u0001\r\u0003\t\u0013A\u0003:pY2,Gm\u0014<feR\t!\u0005\u0005\u0002\u000fG%\u0011Ae\u0004\u0002\u0005+:LG\u000fC\u0003'\u0001\u0019\u0005q%\u0001\u0007csR,7o\u0016:jiR,g\u000e\u0006\u0002#Q!)\u0011&\na\u00019\u0005)!-\u001f;fg\")1\u0006\u0001D\u0001Y\u0005ar-\u001a8fe\u0006$XMU8mY\u0016$wJ^3s\r&dWmU;gM&DH#A\u0017\u0011\u00059\ndB\u0001\b0\u0013\t\u0001t\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003eM\u0012aa\u0015;sS:<'B\u0001\u0019\u0010\u0001"
)
public interface RollingPolicy {
   boolean shouldRollover(long var1);

   void rolledOver();

   void bytesWritten(long var1);

   String generateRolledOverFileSuffix();
}
