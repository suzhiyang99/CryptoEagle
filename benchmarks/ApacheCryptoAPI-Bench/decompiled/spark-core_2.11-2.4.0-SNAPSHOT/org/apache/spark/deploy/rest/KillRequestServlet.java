package org.apache.spark.deploy.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import scala.Option;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001}2a!\u0001\u0002\u0002\u0002\ta!AE&jY2\u0014V-];fgR\u001cVM\u001d<mKRT!a\u0001\u0003\u0002\tI,7\u000f\u001e\u0006\u0003\u000b\u0019\ta\u0001Z3qY>L(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0010\u001b\u0005\u0011\u0011B\u0001\t\u0003\u0005-\u0011Vm\u001d;TKJ4H.\u001a;\t\u000bI\u0001A\u0011\u0001\u000b\u0002\rqJg.\u001b;?\u0007\u0001!\u0012!\u0006\t\u0003\u001d\u0001AQa\u0006\u0001\u0005Ra\ta\u0001Z8Q_N$HcA\r WA\u0011!$H\u0007\u00027)\tA$A\u0003tG\u0006d\u0017-\u0003\u0002\u001f7\t!QK\\5u\u0011\u0015\u0001c\u00031\u0001\"\u0003\u001d\u0011X-];fgR\u0004\"AI\u0015\u000e\u0003\rR!\u0001J\u0013\u0002\t!$H\u000f\u001d\u0006\u0003M\u001d\nqa]3sm2,GOC\u0001)\u0003\u0015Q\u0017M^1y\u0013\tQ3E\u0001\nIiR\u00048+\u001a:wY\u0016$(+Z9vKN$\b\"\u0002\u0017\u0017\u0001\u0004i\u0013\u0001\u0003:fgB|gn]3\u0011\u0005\tr\u0013BA\u0018$\u0005MAE\u000f\u001e9TKJ4H.\u001a;SKN\u0004xN\\:f\u0011\u0015\t\u0004A\"\u00053\u0003)A\u0017M\u001c3mK.KG\u000e\u001c\u000b\u0003gY\u0002\"A\u0004\u001b\n\u0005U\u0012!AF&jY2\u001cVOY7jgNLwN\u001c*fgB|gn]3\t\u000b]\u0002\u0004\u0019\u0001\u001d\u0002\u0019M,(-\\5tg&|g.\u00133\u0011\u0005ebdB\u0001\u000e;\u0013\tY4$\u0001\u0004Qe\u0016$WMZ\u0005\u0003{y\u0012aa\u0015;sS:<'BA\u001e\u001c\u0001"
)
public abstract class KillRequestServlet extends RestServlet {
   public void doPost(HttpServletRequest request, HttpServletResponse response) {
      Option submissionId = this.parseSubmissionId(request.getPathInfo());
      SubmitRestProtocolResponse responseMessage = (SubmitRestProtocolResponse)submissionId.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final KillRequestServlet $outer;

         public final KillSubmissionResponse apply(String submissionId) {
            return this.$outer.handleKill(submissionId);
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
         private final KillRequestServlet $outer;
         private final HttpServletResponse response$1;

         public final ErrorResponse apply() {
            this.response$1.setStatus(400);
            return this.$outer.handleError("Submission ID is missing in kill request.");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.response$1 = response$1;
            }
         }
      });
      this.sendResponse(responseMessage, response);
   }

   public abstract KillSubmissionResponse handleKill(String var1);
}
