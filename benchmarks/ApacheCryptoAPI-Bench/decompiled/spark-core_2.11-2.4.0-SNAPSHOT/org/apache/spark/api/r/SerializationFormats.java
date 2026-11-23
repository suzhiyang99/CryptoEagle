package org.apache.spark.api.r;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A:a!\u0001\u0002\t\u0002\u0019a\u0011\u0001F*fe&\fG.\u001b>bi&|gNR8s[\u0006$8O\u0003\u0002\u0004\t\u0005\t!O\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u0004\"!\u0004\b\u000e\u0003\t1aa\u0004\u0002\t\u0002\u0019\u0001\"\u0001F*fe&\fG.\u001b>bi&|gNR8s[\u0006$8o\u0005\u0002\u000f#A\u0011!#F\u0007\u0002')\tA#A\u0003tG\u0006d\u0017-\u0003\u0002\u0017'\t1\u0011I\\=SK\u001aDQ\u0001\u0007\b\u0005\u0002i\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0019!9AD\u0004b\u0001\n\u0003i\u0012\u0001\u0002\"Z)\u0016+\u0012A\b\t\u0003?\u0011j\u0011\u0001\t\u0006\u0003C\t\nA\u0001\\1oO*\t1%\u0001\u0003kCZ\f\u0017BA\u0013!\u0005\u0019\u0019FO]5oO\"1qE\u0004Q\u0001\ny\tQAQ-U\u000b\u0002Bq!\u000b\bC\u0002\u0013\u0005Q$\u0001\u0004T)JKej\u0012\u0005\u0007W9\u0001\u000b\u0011\u0002\u0010\u0002\u000fM#&+\u0013(HA!9QF\u0004b\u0001\n\u0003i\u0012a\u0001*P/\"1qF\u0004Q\u0001\ny\tAAU(XA\u0001"
)
public final class SerializationFormats {
   public static String ROW() {
      return SerializationFormats$.MODULE$.ROW();
   }

   public static String STRING() {
      return SerializationFormats$.MODULE$.STRING();
   }

   public static String BYTE() {
      return SerializationFormats$.MODULE$.BYTE();
   }
}
