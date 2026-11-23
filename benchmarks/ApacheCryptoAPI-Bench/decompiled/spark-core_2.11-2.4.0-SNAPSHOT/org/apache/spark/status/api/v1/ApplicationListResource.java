package org.apache.spark.status.api.v1;

import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@Produces({"application/json"})
@ScalaSignature(
   bytes = "\u0006\u0001\u0005}a!B\u0001\u0003\u0001\tq!aF!qa2L7-\u0019;j_:d\u0015n\u001d;SKN|WO]2f\u0015\t\u0019A!\u0001\u0002wc)\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0019\u0019H/\u0019;vg*\u0011\u0011BC\u0001\u0006gB\f'o\u001b\u0006\u0003\u00171\ta!\u00199bG\",'\"A\u0007\u0002\u0007=\u0014xmE\u0002\u0001\u001fU\u0001\"\u0001E\n\u000e\u0003EQ\u0011AE\u0001\u0006g\u000e\fG.Y\u0005\u0003)E\u0011a!\u00118z%\u00164\u0007C\u0001\f\u0018\u001b\u0005\u0011\u0011B\u0001\r\u0003\u0005E\t\u0005/\u001b*fcV,7\u000f^\"p]R,\u0007\u0010\u001e\u0005\u00065\u0001!\t\u0001H\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\tQ\u0004\u0005\u0002\u0017\u0001!)q\u0004\u0001C\u0001A\u00059\u0011\r\u001d9MSN$HcB\u00111\u0013Zk&m\u001a\t\u0004E)jcBA\u0012)\u001d\t!s%D\u0001&\u0015\t13$\u0001\u0004=e>|GOP\u0005\u0002%%\u0011\u0011&E\u0001\ba\u0006\u001c7.Y4f\u0013\tYCF\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0015\tI\u0013\u0003\u0005\u0002\u0017]%\u0011qF\u0001\u0002\u0010\u0003B\u0004H.[2bi&|g.\u00138g_\")qA\ba\u0001cA\u0019!gN\u001d\u000e\u0003MR!\u0001N\u001b\u0002\tU$\u0018\u000e\u001c\u0006\u0002m\u0005!!.\u0019<b\u0013\tA4G\u0001\u0003MSN$\bC\u0001\f;\u0013\tY$AA\tBaBd\u0017nY1uS>t7\u000b^1ukNDC\u0001M\u001fH\u0011B\u0011a(R\u0007\u0002\u007f)\u0011\u0001)Q\u0001\u0003eNT!AQ\"\u0002\u0005]\u001c(\"\u0001#\u0002\u000b)\fg/\u0019=\n\u0005\u0019{$AC)vKJL\b+\u0019:b[\u0006)a/\u00197vK\u0006\nq\u0001C\u0003K=\u0001\u00071*A\u0004nS:$\u0015\r^3\u0011\u0005Ya\u0015BA'\u0003\u0005=\u0019\u0016.\u001c9mK\u0012\u000bG/\u001a)be\u0006l\u0007\u0006B%>\u000f>\u000b\u0013A\u0013\u0015\u0005\u0013F;E\u000b\u0005\u0002?%&\u00111k\u0010\u0002\r\t\u00164\u0017-\u001e7u-\u0006dW/Z\u0011\u0002+\u0006Q!\u0007M\u00191[A\nT\u0006M\u0019\t\u000b]s\u0002\u0019A&\u0002\u000f5\f\u0007\u0010R1uK\"\"a+P$ZC\u00059\u0006\u0006\u0002,R\u000fn\u000b\u0013\u0001X\u0001\u000bgA\u0002\u0004'\f\u00192[A\n\u0004\"\u00020\u001f\u0001\u0004Y\u0015AC7j]\u0016sG\rR1uK\"\"Q,P$aC\u0005q\u0006\u0006B/R\u000fRCQa\u0019\u0010A\u0002-\u000b!\"\\1y\u000b:$G)\u0019;fQ\u0011\u0011WhR3\"\u0003\rDCAY)H7\")\u0001N\ba\u0001S\u0006)A.[7jiB\u0011!.\\\u0007\u0002W*\u0011A.N\u0001\u0005Y\u0006tw-\u0003\u0002oW\n9\u0011J\u001c;fO\u0016\u0014\b\u0006B4>\u000fB\f\u0013\u0001\u001b\u0015\u0003=I\u0004\"AP:\n\u0005Q|$aA$F)\")a\u000f\u0001C\u0005o\u0006\u0001\u0012n]!ui\u0016l\u0007\u000f^%o%\u0006tw-\u001a\u000b\rqn\f\t!!\u0002\u0002\n\u0005-\u0011Q\u0002\t\u0003!eL!A_\t\u0003\u000f\t{w\u000e\\3b]\")A0\u001ea\u0001{\u00069\u0011\r\u001e;f[B$\bC\u0001\f\u007f\u0013\ty(A\u0001\fBaBd\u0017nY1uS>t\u0017\t\u001e;f[B$\u0018J\u001c4p\u0011\u0019\t\u0019!\u001ea\u0001\u0017\u0006aQ.\u001b8Ti\u0006\u0014H\u000fR1uK\"1\u0011qA;A\u0002-\u000bA\"\\1y'R\f'\u000f\u001e#bi\u0016DQAX;A\u0002-CQaY;A\u0002-Ca!a\u0004v\u0001\u0004A\u0018AC1osJ+hN\\5oO\"2\u0001!a\u0005H\u00033\u00012APA\u000b\u0013\r\t9b\u0010\u0002\t!J|G-^2fg2\u0012\u00111D\u0011\u0003\u0003;\t\u0001#\u00199qY&\u001c\u0017\r^5p]>R7o\u001c8"
)
public class ApplicationListResource implements ApiRequestContext {
   @Context
   private ServletContext servletContext;
   @Context
   private HttpServletRequest httpRequest;

   public ServletContext servletContext() {
      return this.servletContext;
   }

   @TraitSetter
   public void servletContext_$eq(ServletContext x$1) {
      this.servletContext = x$1;
   }

   public HttpServletRequest httpRequest() {
      return this.httpRequest;
   }

   @TraitSetter
   public void httpRequest_$eq(HttpServletRequest x$1) {
      this.httpRequest = x$1;
   }

   public UIRoot uiRoot() {
      return ApiRequestContext$class.uiRoot(this);
   }

   @GET
   public Iterator<ApplicationInfo> appList(@QueryParam("status") List<ApplicationStatus> status, @DefaultValue("2010-01-01") @QueryParam("minDate") SimpleDateParam minDate, @DefaultValue("3000-01-01") @QueryParam("maxDate") SimpleDateParam maxDate, @DefaultValue("2010-01-01") @QueryParam("minEndDate") SimpleDateParam minEndDate, @DefaultValue("3000-01-01") @QueryParam("maxEndDate") SimpleDateParam maxEndDate, @QueryParam("limit") Integer limit) {
      int numApps = BoxesRunTime.unboxToInt(Option$.MODULE$.apply(limit).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Integer x$1) {
            return Predef$.MODULE$.Integer2int(x$1);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return Integer.MAX_VALUE;
         }
      }));
      boolean includeCompleted = status.isEmpty() || status.contains(ApplicationStatus.COMPLETED);
      boolean includeRunning = status.isEmpty() || status.contains(ApplicationStatus.RUNNING);
      return this.uiRoot().getApplicationInfoList().filter(new Serializable(this, minDate, maxDate, minEndDate, maxEndDate, includeCompleted, includeRunning) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ApplicationListResource $outer;
         public final SimpleDateParam minDate$1;
         public final SimpleDateParam maxDate$1;
         public final SimpleDateParam minEndDate$1;
         public final SimpleDateParam maxEndDate$1;
         private final boolean includeCompleted$1;
         private final boolean includeRunning$1;

         public final boolean apply(ApplicationInfo app) {
            boolean anyRunning = app.attempts().exists(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(ApplicationAttemptInfo x$2) {
                  return !x$2.completed();
               }
            });
            return (!anyRunning && this.includeCompleted$1 || anyRunning && this.includeRunning$1) && app.attempts().exists(new Serializable(this, anyRunning) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final boolean anyRunning$1;

               public final boolean apply(ApplicationAttemptInfo attempt) {
                  return this.$outer.org$apache$spark$status$api$v1$ApplicationListResource$$anonfun$$$outer().org$apache$spark$status$api$v1$ApplicationListResource$$isAttemptInRange(attempt, this.$outer.minDate$1, this.$outer.maxDate$1, this.$outer.minEndDate$1, this.$outer.maxEndDate$1, this.anyRunning$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.anyRunning$1 = anyRunning$1;
                  }
               }
            });
         }

         // $FF: synthetic method
         public ApplicationListResource org$apache$spark$status$api$v1$ApplicationListResource$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.minDate$1 = minDate$1;
               this.maxDate$1 = maxDate$1;
               this.minEndDate$1 = minEndDate$1;
               this.maxEndDate$1 = maxEndDate$1;
               this.includeCompleted$1 = includeCompleted$1;
               this.includeRunning$1 = includeRunning$1;
            }
         }
      }).take(numApps);
   }

   public boolean org$apache$spark$status$api$v1$ApplicationListResource$$isAttemptInRange(ApplicationAttemptInfo attempt, SimpleDateParam minStartDate, SimpleDateParam maxStartDate, SimpleDateParam minEndDate, SimpleDateParam maxEndDate, boolean anyRunning) {
      boolean startTimeOk = attempt.startTime().getTime() >= minStartDate.timestamp() && attempt.startTime().getTime() <= maxStartDate.timestamp();
      boolean endTimeOkForRunning = anyRunning && maxEndDate.timestamp() > System.currentTimeMillis();
      boolean endTimeOkForCompleted = !anyRunning && attempt.endTime().getTime() >= minEndDate.timestamp() && attempt.endTime().getTime() <= maxEndDate.timestamp();
      boolean endTimeOk = endTimeOkForRunning || endTimeOkForCompleted;
      return startTimeOk && endTimeOk;
   }

   public ApplicationListResource() {
      ApiRequestContext$class.$init$(this);
   }
}
