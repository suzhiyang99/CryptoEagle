package org.apache.spark.deploy.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import scala.Option;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001}2a!\u0001\u0002\u0002\u0002\ta!\u0001F*uCR,8OU3rk\u0016\u001cHoU3sm2,GO\u0003\u0002\u0004\t\u0005!!/Z:u\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001d=i\u0011AA\u0005\u0003!\t\u00111BU3tiN+'O\u001e7fi\")!\u0003\u0001C\u0001)\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u0016!\tq\u0001\u0001C\u0003\u0018\u0001\u0011E\u0003$A\u0003e_\u001e+G\u000fF\u0002\u001a?-\u0002\"AG\u000f\u000e\u0003mQ\u0011\u0001H\u0001\u0006g\u000e\fG.Y\u0005\u0003=m\u0011A!\u00168ji\")\u0001E\u0006a\u0001C\u00059!/Z9vKN$\bC\u0001\u0012*\u001b\u0005\u0019#B\u0001\u0013&\u0003\u0011AG\u000f\u001e9\u000b\u0005\u0019:\u0013aB:feZdW\r\u001e\u0006\u0002Q\u0005)!.\u0019<bq&\u0011!f\t\u0002\u0013\u0011R$\boU3sm2,GOU3rk\u0016\u001cH\u000fC\u0003--\u0001\u0007Q&\u0001\u0005sKN\u0004xN\\:f!\t\u0011c&\u0003\u00020G\t\u0019\u0002\n\u001e;q'\u0016\u0014h\u000f\\3u%\u0016\u001c\bo\u001c8tK\")\u0011\u0007\u0001D\te\u0005a\u0001.\u00198eY\u0016\u001cF/\u0019;vgR\u00111G\u000e\t\u0003\u001dQJ!!\u000e\u0002\u00031M+(-\\5tg&|gn\u0015;biV\u001c(+Z:q_:\u001cX\rC\u00038a\u0001\u0007\u0001(\u0001\u0007tk\nl\u0017n]:j_:LE\r\u0005\u0002:y9\u0011!DO\u0005\u0003wm\ta\u0001\u0015:fI\u00164\u0017BA\u001f?\u0005\u0019\u0019FO]5oO*\u00111h\u0007"
)
public abstract class StatusRequestServlet extends RestServlet {
   public void doGet(HttpServletRequest request, HttpServletResponse response) {
      Option submissionId = this.parseSubmissionId(request.getPathInfo());
      SubmitRestProtocolResponse responseMessage = (SubmitRestProtocolResponse)submissionId.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StatusRequestServlet $outer;

         public final SubmissionStatusResponse apply(String submissionId) {
            return this.$outer.handleStatus(submissionId);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).getOrElse(new Serializable(this, response) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StatusRequestServlet $outer;
         private final HttpServletResponse response$2;

         public final ErrorResponse apply() {
            this.response$2.setStatus(400);
            return this.$outer.handleError("Submission ID is missing in status request.");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.response$2 = response$2;
            }
         }
      });
      this.sendResponse(responseMessage, response);
   }

   public abstract SubmissionStatusResponse handleStatus(String var1);
}
