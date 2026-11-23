package org.apache.spark.status.api.v1;

import java.util.List;
import java.util.NoSuchElementException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.apache.spark.ui.SparkUI;
import scala.Array$;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.StringOps;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.TraitSetter;

@Produces({"application/json"})
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ue!B\u0001\u0003\u0001\tq!AD*uC\u001e,7OU3t_V\u00148-\u001a\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c2\u0001A\b\u0016!\t\u00012#D\u0001\u0012\u0015\u0005\u0011\u0012!B:dC2\f\u0017B\u0001\u000b\u0012\u0005\u0019\te.\u001f*fMB\u0011acF\u0007\u0002\u0005%\u0011\u0001D\u0001\u0002\u0010\u0005\u0006\u001cX-\u00119q%\u0016\u001cx.\u001e:dK\")!\u0004\u0001C\u00019\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u001e!\t1\u0002\u0001C\u0003 \u0001\u0011\u0005\u0001%A\u0005ti\u0006<W\rT5tiR\u0011\u0011\u0005\r\t\u0004E)jcBA\u0012)\u001d\t!s%D\u0001&\u0015\t13$\u0001\u0004=e>|GOP\u0005\u0002%%\u0011\u0011&E\u0001\ba\u0006\u001c7.Y4f\u0013\tYCFA\u0002TKFT!!K\t\u0011\u0005Yq\u0013BA\u0018\u0003\u0005%\u0019F/Y4f\t\u0006$\u0018\rC\u00032=\u0001\u0007!'\u0001\u0005ti\u0006$Xo]3t!\r\u0019\u0004HO\u0007\u0002i)\u0011QGN\u0001\u0005kRLGNC\u00018\u0003\u0011Q\u0017M^1\n\u0005e\"$\u0001\u0002'jgR\u0004\"AF\u001e\n\u0005q\u0012!aC*uC\u001e,7\u000b^1ukNDC\u0001\r I\u0013B\u0011qHR\u0007\u0002\u0001*\u0011\u0011IQ\u0001\u0003eNT!a\u0011#\u0002\u0005]\u001c(\"A#\u0002\u000b)\fg/\u0019=\n\u0005\u001d\u0003%AC)vKJL\b+\u0019:b[\u0006)a/\u00197vK\u0006\nq\u0001\u000b\u0002\u001f\u0017B\u0011q\bT\u0005\u0003\u001b\u0002\u00131aR#U\u0011\u0015y\u0005\u0001\"\u0001Q\u0003%\u0019H/Y4f\t\u0006$\u0018\rF\u0002\"#nCQA\u0015(A\u0002M\u000bqa\u001d;bO\u0016LE\r\u0005\u0002\u0011)&\u0011Q+\u0005\u0002\u0004\u0013:$\b\u0006B)X\u0011j\u0003\"a\u0010-\n\u0005e\u0003%!\u0003)bi\"\u0004\u0016M]1nC\u0005\u0011\u0006\"\u0002/O\u0001\u0004i\u0016a\u00023fi\u0006LGn\u001d\t\u0003!yK!aX\t\u0003\u000f\t{w\u000e\\3b]\"\"1,\u0019%e!\ty$-\u0003\u0002d\u0001\naA)\u001a4bk2$h+\u00197vK\u0006\nQ-\u0001\u0003ueV,\u0007\u0006B.?\u0011\u001e\f\u0013\u0001\u0018\u0015\u0005\u001d&DE\u000e\u0005\u0002@U&\u00111\u000e\u0011\u0002\u0005!\u0006$\b.I\u0001n\u00039Y8\u000f^1hK&#'\b\t/eWuD#AT&\t\u000bA\u0004A\u0011A9\u0002\u001d=tW-\u0011;uK6\u0004H\u000fR1uCR!QF\u001d;y\u0011\u0015\u0011v\u000e1\u0001TQ\u0011\u0011x\u000b\u0013.\t\u000bU|\u0007\u0019A*\u0002\u001dM$\u0018mZ3BiR,W\u000e\u001d;JI\"\"Ao\u0016%xC\u0005)\b\"\u0002/p\u0001\u0004i\u0006\u0006\u0002=b\u0011\u0012DC\u0001\u001f IO\"\"q.\u001b%}C\u0005i\u0018\u0001J>ti\u0006<W-\u00133;Aq#7&`\u0018|gR\fw-Z!ui\u0016l\u0007\u000f^%eu\u0001bFmK?)\u0005=\\\u0005bBA\u0001\u0001\u0011\u0005\u00111A\u0001\fi\u0006\u001c8nU;n[\u0006\u0014\u0018\u0010\u0006\u0005\u0002\u0006\u0005-\u0011qBA\n!\r1\u0012qA\u0005\u0004\u0003\u0013\u0011!a\u0006+bg.lU\r\u001e:jG\u0012K7\u000f\u001e:jEV$\u0018n\u001c8t\u0011\u0015\u0011v\u00101\u0001TQ\u0015\tYa\u0016%[\u0011\u0015)x\u00101\u0001TQ\u0015\tya\u0016%x\u0011\u001d\t)b a\u0001\u0003/\ta\"];b]RLG.Z*ue&tw\r\u0005\u0003\u0002\u001a\u0005}ab\u0001\t\u0002\u001c%\u0019\u0011QD\t\u0002\rA\u0013X\rZ3g\u0013\u0011\t\t#a\t\u0003\rM#(/\u001b8h\u0015\r\ti\"\u0005\u0015\u0007\u0003'q\u0004*a\n\"\u0005\u0005%\u0012!C9vC:$\u0018\u000e\\3tQ\u0019\t\u0019\"\u0019%\u0002.\u0005\u0012\u0011qF\u0001\u0018a9\u0002T\u0007\f\u0019/eUb\u0003GL\u001b-a9:T\u0007\f\u0019/sUBSa`5I\u0003g\t#!!\u000e\u0002am\u001cH/Y4f\u0013\u0012T\u0004\u0005\u00183,{>Z8\u000f^1hK\u0006#H/Z7qi&#'\b\t/eWu|C/Y:l'VlW.\u0019:zQ\ty8\nC\u0004\u0002<\u0001!\t!!\u0010\u0002\u0011Q\f7o\u001b'jgR$B\"a\u0010\u0002H\u0005-\u0013qJA/\u0003W\u0002BA\t\u0016\u0002BA\u0019a#a\u0011\n\u0007\u0005\u0015#A\u0001\u0005UCN\\G)\u0019;b\u0011\u0019\u0011\u0016\u0011\ba\u0001'\"*\u0011qI,I5\"1Q/!\u000fA\u0002MCS!a\u0013X\u0011^Dq!!\u0015\u0002:\u0001\u00071+\u0001\u0004pM\u001a\u001cX\r\u001e\u0015\u0007\u0003\u001fr\u0004*!\u0016\"\u0005\u0005E\u0003FBA(C\"\u000bI&\t\u0002\u0002\\\u0005\t\u0001\u0007C\u0004\u0002`\u0005e\u0002\u0019A*\u0002\r1,gn\u001a;iQ\u0019\tiF\u0010%\u0002d\u0005\u0012\u0011q\f\u0015\u0007\u0003;\n\u0007*a\u001a\"\u0005\u0005%\u0014A\u0001\u001a1\u0011!\ti'!\u000fA\u0002\u0005=\u0014AB:peR\u0014\u0015\u0010E\u0002\u0017\u0003cJ1!a\u001d\u0003\u0005-!\u0016m]6T_J$\u0018N\\4)\r\u0005-d\bSA<C\t\ti\u0007\u000b\u0004\u0002l\u0005D\u00151P\u0011\u0003\u0003{\n!!\u0013#)\r\u0005e\u0012\u000eSAAC\t\t\u0019)A\u0017|gR\fw-Z%eu\u0001bFmK?0wN$\u0018mZ3BiR,W\u000e\u001d;JIj\u0002C\fZ\u0016~_Q\f7o\u001b'jgRD3!!\u000fLQ\u0019\u0001\u0011\u0011\u0012%\u0002\u0010B\u0019q(a#\n\u0007\u00055\u0005I\u0001\u0005Qe>$WoY3tY\t\t\t*\t\u0002\u0002\u0014\u0006\u0001\u0012\r\u001d9mS\u000e\fG/[8o_)\u001cxN\u001c"
)
public class StagesResource implements BaseAppResource {
   @PathParam("appId")
   private String appId;
   @PathParam("attemptId")
   private String attemptId;
   @Context
   private ServletContext servletContext;
   @Context
   private HttpServletRequest httpRequest;

   public String appId() {
      return this.appId;
   }

   @TraitSetter
   public void appId_$eq(String x$1) {
      this.appId = x$1;
   }

   public String attemptId() {
      return this.attemptId;
   }

   @TraitSetter
   public void attemptId_$eq(String x$1) {
      this.attemptId = x$1;
   }

   public <T> T withUI(Function1<SparkUI, T> fn) {
      return (T)BaseAppResource$class.withUI(this, fn);
   }

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
   public Seq<StageData> stageList(@QueryParam("status") List<StageStatus> statuses) {
      return (Seq)this.withUI(new Serializable(this, statuses) {
         public static final long serialVersionUID = 0L;
         private final List statuses$1;

         public final Seq<StageData> apply(SparkUI x$1) {
            return x$1.store().stageList(this.statuses$1);
         }

         public {
            this.statuses$1 = statuses$1;
         }
      });
   }

   @GET
   @Path("{stageId: \\d+}")
   public Seq<StageData> stageData(@PathParam("stageId") int stageId, @QueryParam("details") @DefaultValue("true") boolean details) {
      return (Seq)this.withUI(new Serializable(this, stageId, details) {
         public static final long serialVersionUID = 0L;
         private final int stageId$1;
         private final boolean details$1;

         public final Seq<StageData> apply(SparkUI ui) {
            Seq ret = ui.store().stageData(this.stageId$1, this.details$1);
            if (ret.nonEmpty()) {
               return ret;
            } else {
               throw new NotFoundException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"unknown stage: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stageId$1)})));
            }
         }

         public {
            this.stageId$1 = stageId$1;
            this.details$1 = details$1;
         }
      });
   }

   @GET
   @Path("{stageId: \\d+}/{stageAttemptId: \\d+}")
   public StageData oneAttemptData(@PathParam("stageId") int stageId, @PathParam("stageAttemptId") int stageAttemptId, @QueryParam("details") @DefaultValue("true") boolean details) {
      return (StageData)this.withUI(new Serializable(this, stageId, stageAttemptId, details) {
         public static final long serialVersionUID = 0L;
         private final int stageId$2;
         private final int stageAttemptId$1;
         private final boolean details$2;

         public final StageData apply(SparkUI ui) {
            try {
               return ui.store().stageAttempt(this.stageId$2, this.stageAttemptId$1, this.details$2);
            } catch (NoSuchElementException var5) {
               Seq all = ui.store().stageData(this.stageId$2, ui.store().stageData$default$2());
               String var10000;
               if (all.nonEmpty()) {
                  Seq ids = (Seq)all.map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final int apply(StageData x$2) {
                        return x$2.attemptId();
                     }
                  }, Seq$.MODULE$.canBuildFrom());
                  var10000 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"unknown attempt for stage ", ".  Found attempts: [", "]"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stageId$2), ids.mkString(",")}));
               } else {
                  var10000 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"unknown stage: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stageId$2)}));
               }

               String msg = var10000;
               throw new NotFoundException(msg);
            }
         }

         public {
            this.stageId$2 = stageId$2;
            this.stageAttemptId$1 = stageAttemptId$1;
            this.details$2 = details$2;
         }
      });
   }

   @GET
   @Path("{stageId: \\d+}/{stageAttemptId: \\d+}/taskSummary")
   public TaskMetricDistributions taskSummary(@PathParam("stageId") int stageId, @PathParam("stageAttemptId") int stageAttemptId, @DefaultValue("0.05,0.25,0.5,0.75,0.95") @QueryParam("quantiles") String quantileString) {
      return (TaskMetricDistributions)this.withUI(new Serializable(this, stageId, stageAttemptId, quantileString) {
         public static final long serialVersionUID = 0L;
         public final int stageId$3;
         public final int stageAttemptId$2;
         private final String quantileString$1;

         public final TaskMetricDistributions apply(SparkUI ui) {
            double[] quantiles = (double[])Predef$.MODULE$.refArrayOps((Object[])this.quantileString$1.split(",")).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(String s) {
                  try {
                     return (new StringOps(Predef$.MODULE$.augmentString(s))).toDouble();
                  } catch (NumberFormatException var3) {
                     throw new BadParameterException("quantiles", "double", s);
                  }
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Double()));
            return (TaskMetricDistributions)ui.store().taskSummary(this.stageId$3, this.stageAttemptId$2, quantiles).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final Nothing$ apply() {
                  throw new NotFoundException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No tasks reported metrics for ", " / ", " yet."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.stageId$3), BoxesRunTime.boxToInteger(this.$outer.stageAttemptId$2)})));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         public {
            this.stageId$3 = stageId$3;
            this.stageAttemptId$2 = stageAttemptId$2;
            this.quantileString$1 = quantileString$1;
         }
      });
   }

   @GET
   @Path("{stageId: \\d+}/{stageAttemptId: \\d+}/taskList")
   public Seq<TaskData> taskList(@PathParam("stageId") int stageId, @PathParam("stageAttemptId") int stageAttemptId, @DefaultValue("0") @QueryParam("offset") int offset, @DefaultValue("20") @QueryParam("length") int length, @DefaultValue("ID") @QueryParam("sortBy") TaskSorting sortBy) {
      return (Seq)this.withUI(new Serializable(this, stageId, stageAttemptId, offset, length, sortBy) {
         public static final long serialVersionUID = 0L;
         private final int stageId$4;
         private final int stageAttemptId$3;
         private final int offset$1;
         private final int length$1;
         private final TaskSorting sortBy$1;

         public final Seq<TaskData> apply(SparkUI x$3) {
            return x$3.store().taskList(this.stageId$4, this.stageAttemptId$3, this.offset$1, this.length$1, this.sortBy$1);
         }

         public {
            this.stageId$4 = stageId$4;
            this.stageAttemptId$3 = stageAttemptId$3;
            this.offset$1 = offset$1;
            this.length$1 = length$1;
            this.sortBy$1 = sortBy$1;
         }
      });
   }

   public StagesResource() {
      ApiRequestContext$class.$init$(this);
      BaseAppResource$class.$init$(this);
   }
}
