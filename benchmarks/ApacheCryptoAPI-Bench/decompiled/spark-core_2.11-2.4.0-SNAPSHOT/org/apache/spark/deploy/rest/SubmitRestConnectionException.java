package org.apache.spark.deploy.rest;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I2Q!\u0001\u0002\u0001\t1\u0011QdU;c[&$(+Z:u\u0007>tg.Z2uS>tW\t_2faRLwN\u001c\u0006\u0003\u0007\u0011\tAA]3ti*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\"\u0001A\u0007\u0011\u00059yQ\"\u0001\u0002\n\u0005A\u0011!aG*vE6LGOU3tiB\u0013x\u000e^8d_2,\u0005pY3qi&|g\u000e\u0003\u0005\u0013\u0001\t\u0005\t\u0015!\u0003\u0015\u0003\u001diWm]:bO\u0016\u001c\u0001\u0001\u0005\u0002\u001679\u0011a#G\u0007\u0002/)\t\u0001$A\u0003tG\u0006d\u0017-\u0003\u0002\u001b/\u00051\u0001K]3eK\u001aL!\u0001H\u000f\u0003\rM#(/\u001b8h\u0015\tQr\u0003\u0003\u0005 \u0001\t\u0005\t\u0015!\u0003!\u0003\u0015\u0019\u0017-^:f!\t\t\u0013F\u0004\u0002#O9\u00111EJ\u0007\u0002I)\u0011QeE\u0001\u0007yI|w\u000e\u001e \n\u0003aI!\u0001K\f\u0002\u000fA\f7m[1hK&\u0011!f\u000b\u0002\n)\"\u0014xn^1cY\u0016T!\u0001K\f\t\u000b5\u0002A\u0011\u0001\u0018\u0002\rqJg.\u001b;?)\ry\u0003'\r\t\u0003\u001d\u0001AQA\u0005\u0017A\u0002QAQa\b\u0017A\u0002\u0001\u0002"
)
public class SubmitRestConnectionException extends SubmitRestProtocolException {
   public SubmitRestConnectionException(String message, Throwable cause) {
      super(message, cause);
   }
}
