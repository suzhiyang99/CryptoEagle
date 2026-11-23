package org.apache.spark.deploy.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import scala.collection.mutable.StringBuilder;
import scala.io.Codec;
import scala.io.Codec$;
import scala.io.Source$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00153a!\u0001\u0002\u0002\u0002\ta!\u0001F*vE6LGOU3rk\u0016\u001cHoU3sm2,GO\u0003\u0002\u0004\t\u0005!!/Z:u\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001d=i\u0011AA\u0005\u0003!\t\u00111BU3tiN+'O\u001e7fi\")!\u0003\u0001C\u0001)\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u0016!\tq\u0001\u0001C\u0003\u0018\u0001\u0011E\u0003$\u0001\u0004e_B{7\u000f\u001e\u000b\u00043}Y\u0003C\u0001\u000e\u001e\u001b\u0005Y\"\"\u0001\u000f\u0002\u000bM\u001c\u0017\r\\1\n\u0005yY\"\u0001B+oSRDQ\u0001\t\fA\u0002\u0005\naB]3rk\u0016\u001cHoU3sm2,G\u000f\u0005\u0002#S5\t1E\u0003\u0002%K\u0005!\u0001\u000e\u001e;q\u0015\t1s%A\u0004tKJ4H.\u001a;\u000b\u0003!\nQA[1wCbL!AK\u0012\u0003%!#H\u000f]*feZdW\r\u001e*fcV,7\u000f\u001e\u0005\u0006YY\u0001\r!L\u0001\u0010e\u0016\u001c\bo\u001c8tKN+'O\u001e7fiB\u0011!EL\u0005\u0003_\r\u00121\u0003\u0013;uaN+'O\u001e7fiJ+7\u000f]8og\u0016DQ!\r\u0001\u0007\u0012I\nA\u0002[1oI2,7+\u001e2nSR$Ba\r\u001c@\tB\u0011a\u0002N\u0005\u0003k\t\u0011!dU;c[&$(+Z:u!J|Go\\2pYJ+7\u000f]8og\u0016DQa\u000e\u0019A\u0002a\n!C]3rk\u0016\u001cH/T3tg\u0006<WMS:p]B\u0011\u0011\b\u0010\b\u00035iJ!aO\u000e\u0002\rA\u0013X\rZ3g\u0013\tidH\u0001\u0004TiJLgn\u001a\u0006\u0003wmAQ\u0001\u0011\u0019A\u0002\u0005\u000baB]3rk\u0016\u001cH/T3tg\u0006<W\r\u0005\u0002\u000f\u0005&\u00111I\u0001\u0002\u001a'V\u0014W.\u001b;SKN$\bK]8u_\u000e|G.T3tg\u0006<W\rC\u0003-a\u0001\u0007Q\u0006"
)
public abstract class SubmitRequestServlet extends RestServlet {
   public void doPost(HttpServletRequest requestServlet, HttpServletResponse responseServlet) {
      Object var10000;
      try {
         String requestMessageJson = Source$.MODULE$.fromInputStream(requestServlet.getInputStream(), (Codec)Codec$.MODULE$.fallbackSystemCodec()).mkString();
         SubmitRestProtocolMessage requestMessage = SubmitRestProtocolMessage$.MODULE$.fromJson(requestMessageJson);
         requestMessage.validate();
         var10000 = this.handleSubmit(requestMessageJson, requestMessage, responseServlet);
      } catch (Throwable var11) {
         label51: {
            boolean var6;
            if (var11 instanceof JsonProcessingException) {
               var6 = true;
            } else if (var11 instanceof SubmitRestProtocolException) {
               var6 = true;
            } else {
               var6 = false;
            }

            if (!var6) {
               throw var11;
            }

            responseServlet.setStatus(400);
            ErrorResponse var7 = this.handleError((new StringBuilder()).append((Object)"Malformed request: ").append((Object)this.formatException(var11)).toString());
            var10000 = var7;
            break label51;
         }
      }

      SubmitRestProtocolResponse responseMessage = (SubmitRestProtocolResponse)var10000;
      this.sendResponse(responseMessage, responseServlet);
   }

   public abstract SubmitRestProtocolResponse handleSubmit(String var1, SubmitRestProtocolMessage var2, HttpServletResponse var3);
}
