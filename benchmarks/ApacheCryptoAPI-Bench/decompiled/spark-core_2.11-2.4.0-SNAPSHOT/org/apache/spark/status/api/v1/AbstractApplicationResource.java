package org.apache.spark.status.api.v1;

import java.io.OutputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.Status;
import org.apache.spark.JobExecutionStatus;
import org.apache.spark.ui.SparkUI;
import scala.Function1;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

@Produces({"application/json"})
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ee!B\u0001\u0003\u0001\tq!aG!cgR\u0014\u0018m\u0019;BaBd\u0017nY1uS>t'+Z:pkJ\u001cWM\u0003\u0002\u0004\t\u0005\u0011a/\r\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"\u0001\u0004ti\u0006$Xo\u001d\u0006\u0003\u0013)\tQa\u001d9be.T!a\u0003\u0007\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005i\u0011aA8sON\u0019\u0001aD\u000b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g!\t1r#D\u0001\u0003\u0013\tA\"AA\bCCN,\u0017\t\u001d9SKN|WO]2f\u0011\u0015Q\u0002\u0001\"\u0001\u001d\u0003\u0019a\u0014N\\5u}\r\u0001A#A\u000f\u0011\u0005Y\u0001\u0001\"B\u0010\u0001\t\u0003\u0001\u0013\u0001\u00036pENd\u0015n\u001d;\u0015\u0005\u0005\u0002\u0004c\u0001\u0012+[9\u00111\u0005\u000b\b\u0003I\u001dj\u0011!\n\u0006\u0003Mm\ta\u0001\u0010:p_Rt\u0014\"\u0001\n\n\u0005%\n\u0012a\u00029bG.\fw-Z\u0005\u0003W1\u00121aU3r\u0015\tI\u0013\u0003\u0005\u0002\u0017]%\u0011qF\u0001\u0002\b\u0015>\u0014G)\u0019;b\u0011\u0015\td\u00041\u00013\u0003!\u0019H/\u0019;vg\u0016\u001c\bcA\u001a9u5\tAG\u0003\u00026m\u0005!Q\u000f^5m\u0015\u00059\u0014\u0001\u00026bm\u0006L!!\u000f\u001b\u0003\t1K7\u000f\u001e\t\u0003wqj\u0011\u0001C\u0005\u0003{!\u0011!CS8c\u000bb,7-\u001e;j_:\u001cF/\u0019;vg\"\"\u0001gP%K!\t\u0001u)D\u0001B\u0015\t\u00115)\u0001\u0002sg*\u0011A)R\u0001\u0003oNT\u0011AR\u0001\u0006U\u00064\u0018\r_\u0005\u0003\u0011\u0006\u0013!\"U;fef\u0004\u0016M]1n\u0003\u00151\u0018\r\\;fC\u00059\u0001\u0006\u0002\u0010M\u0013>\u0003\"\u0001Q'\n\u00059\u000b%\u0001\u0002)bi\"\f\u0013\u0001U\u0001\u0005U>\u00147\u000f\u000b\u0002\u001f%B\u0011\u0001iU\u0005\u0003)\u0006\u00131aR#U\u0011\u00151\u0006\u0001\"\u0001X\u0003\u0019yg.\u001a&pER\u0011Q\u0006\u0017\u0005\u00063V\u0003\rAW\u0001\u0006U>\u0014\u0017\n\u001a\t\u0003!mK!\u0001X\t\u0003\u0007%sG\u000f\u000b\u0003Y=&\u000b\u0007C\u0001!`\u0013\t\u0001\u0017IA\u0005QCRD\u0007+\u0019:b[\u0006\n\u0011\f\u000b\u0003V\u0019&\u001b\u0017%\u00013\u0002#)|'m]\u0018|U>\u0014\u0017\n\u001a\u001e!9\u0012\\S\u0010\u000b\u0002V%\")q\r\u0001C\u0001Q\u0006aQ\r_3dkR|'\u000fT5tiR\t\u0011\u000eE\u0002#U)\u0004\"AF6\n\u00051\u0014!aD#yK\u000e,Ho\u001c:Tk6l\u0017M]=)\t\u0019d\u0015J\\\u0011\u0002_\u0006IQ\r_3dkR|'o\u001d\u0015\u0003MJCQA\u001d\u0001\u0005\u0002!\fq\"\u00197m\u000bb,7-\u001e;pe2K7\u000f\u001e\u0015\u0005c2KE/I\u0001v\u00031\tG\u000e\\3yK\u000e,Ho\u001c:tQ\t\t(\u000bC\u0003y\u0001\u0011\u0005\u00110\u0001\u0004ti\u0006<Wm\u001d\u000b\u0002uB!1P`A\u0002\u001d\t\u0001B0\u0003\u0002~#\u00051\u0001K]3eK\u001aL1a`A\u0001\u0005\u0015\u0019E.Y:t\u0015\ti\u0018\u0003E\u0002\u0017\u0003\u000bI1!a\u0002\u0003\u00059\u0019F/Y4fgJ+7o\\;sG\u0016DSa\u001e'J\u0003\u0017\t\u0013\u0001\u001f\u0005\b\u0003\u001f\u0001A\u0011AA\t\u0003\u001d\u0011H\r\u001a'jgR$\"!a\u0005\u0011\t\tR\u0013Q\u0003\t\u0004-\u0005]\u0011bAA\r\u0005\tq!\u000b\u0012#Ti>\u0014\u0018mZ3J]\u001a|\u0007FBA\u0007\u0019&\u000bi\"\t\u0002\u0002 \u0005Y1\u000f^8sC\u001e,wF\u001d3eQ\r\tiA\u0015\u0005\b\u0003K\u0001A\u0011AA\u0014\u0003\u001d\u0011H\r\u001a#bi\u0006$B!!\u0006\u0002*!9\u00111FA\u0012\u0001\u0004Q\u0016!\u0002:eI&#\u0007FBA\u0015=&\u000by#\t\u0002\u0002,!2\u00111\u0005'J\u0003g\t#!!\u000e\u00021M$xN]1hK>\u0012H\rZ\u0018|e\u0012$\u0017\n\u001a\u001e!9\u0012\\S\u0010K\u0002\u0002$ICq!a\u000f\u0001\t\u0003\ti$A\bf]ZL'o\u001c8nK:$\u0018J\u001c4p)\t\ty\u0004E\u0002\u0017\u0003\u0003J1!a\u0011\u0003\u0005i\t\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8F]ZL'o\u001c8nK:$\u0018J\u001c4pQ\u0019\tI\u0004T%\u0002H\u0005\u0012\u0011\u0011J\u0001\fK:4\u0018N]8o[\u0016tG\u000fK\u0002\u0002:ICq!a\u0014\u0001\t\u0003\t\t&\u0001\u0007hKR,e/\u001a8u\u0019><7\u000f\u0006\u0002\u0002TA!\u0011QKA.\u001b\t\t9FC\u0002\u0002Z\u0005\u000bAaY8sK&!\u0011QLA,\u0005!\u0011Vm\u001d9p]N,\u0007fBA'\u0003CJ\u0015q\r\t\u0004\u0001\u0006\r\u0014bAA3\u0003\nA\u0001K]8ek\u000e,7\u000f\f\u0002\u0002j\u0005\u0012\u00111N\u0001\u0019CB\u0004H.[2bi&|gnL8di\u0016$Xf\u001d;sK\u0006l\u0007FBA'\u0019&\u000by'\t\u0002\u0002r\u0005!An\\4tQ\r\tiE\u0015\u0005\b\u0003o\u0002A\u0011AA=\u0003I\t\u0007\u000f\u001d7jG\u0006$\u0018n\u001c8BiR,W\u000e\u001d;\u0015\u0005\u0005m\u0004\u0003B>\u007f\u0003{\u00022AFA@\u0013\r\t\tI\u0001\u0002\u001e\u001f:,\u0017\t\u001d9mS\u000e\fG/[8o\u0003R$X-\u001c9u%\u0016\u001cx.\u001e:dK\"2\u0011Q\u000f'J\u0003\u000b\u000b#!a\"\u0002\u0017m\fG\u000f^3naRLE- \u0015\u0007\u0001\u0005\u0005\u0014*a#-\u0005\u00055\u0015EAAH\u0003A\t\u0007\u000f\u001d7jG\u0006$\u0018n\u001c80UN|g\u000e"
)
public class AbstractApplicationResource implements BaseAppResource {
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
   @Path("jobs")
   public Seq<JobData> jobsList(@QueryParam("status") List<JobExecutionStatus> statuses) {
      return (Seq)this.withUI(new Serializable(this, statuses) {
         public static final long serialVersionUID = 0L;
         private final List statuses$1;

         public final Seq<JobData> apply(SparkUI x$1) {
            return x$1.store().jobsList(this.statuses$1);
         }

         public {
            this.statuses$1 = statuses$1;
         }
      });
   }

   @GET
   @Path("jobs/{jobId: \\d+}")
   public JobData oneJob(@PathParam("jobId") int jobId) {
      return (JobData)this.withUI(new Serializable(this, jobId) {
         public static final long serialVersionUID = 0L;
         private final int jobId$1;

         public final JobData apply(SparkUI ui) {
            try {
               return ui.store().job(this.jobId$1);
            } catch (NoSuchElementException var2) {
               throw new NotFoundException((new StringBuilder()).append((Object)"unknown job: ").append((Object)BoxesRunTime.boxToInteger(this.jobId$1)).toString());
            }
         }

         public {
            this.jobId$1 = jobId$1;
         }
      });
   }

   @GET
   @Path("executors")
   public Seq<ExecutorSummary> executorList() {
      return (Seq)this.withUI(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<ExecutorSummary> apply(SparkUI x$2) {
            return x$2.store().executorList(true);
         }
      });
   }

   @GET
   @Path("allexecutors")
   public Seq<ExecutorSummary> allExecutorList() {
      return (Seq)this.withUI(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<ExecutorSummary> apply(SparkUI x$3) {
            return x$3.store().executorList(false);
         }
      });
   }

   @Path("stages")
   public Class<StagesResource> stages() {
      return StagesResource.class;
   }

   @GET
   @Path("storage/rdd")
   public Seq<RDDStorageInfo> rddList() {
      return (Seq)this.withUI(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<RDDStorageInfo> apply(SparkUI x$4) {
            return x$4.store().rddList(x$4.store().rddList$default$1());
         }
      });
   }

   @GET
   @Path("storage/rdd/{rddId: \\d+}")
   public RDDStorageInfo rddData(@PathParam("rddId") int rddId) {
      return (RDDStorageInfo)this.withUI(new Serializable(this, rddId) {
         public static final long serialVersionUID = 0L;
         private final int rddId$1;

         public final RDDStorageInfo apply(SparkUI ui) {
            try {
               return ui.store().rdd(this.rddId$1);
            } catch (NoSuchElementException var2) {
               throw new NotFoundException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"no rdd found w/ id ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.rddId$1)})));
            }
         }

         public {
            this.rddId$1 = rddId$1;
         }
      });
   }

   @GET
   @Path("environment")
   public ApplicationEnvironmentInfo environmentInfo() {
      return (ApplicationEnvironmentInfo)this.withUI(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ApplicationEnvironmentInfo apply(SparkUI x$5) {
            return x$5.store().environmentInfo();
         }
      });
   }

   @GET
   @Path("logs")
   @Produces({"application/octet-stream"})
   public Response getEventLogs() {
      try {
         this.withUI(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(SparkUI x$6) {
            }
         });
      } catch (Throwable var15) {
         label109: {
            if (!(var15 instanceof NotFoundException) || this.attemptId() != null) {
               throw var15;
            }

            this.attemptId_$eq("1");
            this.withUI(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(SparkUI x$7) {
               }
            });
            this.attemptId_$eq((String)null);
            BoxedUnit var3 = BoxedUnit.UNIT;
            BoxedUnit var10000 = BoxedUnit.UNIT;
            break label109;
         }
      }

      Response var16;
      try {
         String fileName = this.attemptId() == null ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"eventLogs-", ".zip"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId()})) : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"eventLogs-", "-", ".zip"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId(), this.attemptId()}));
         StreamingOutput stream = new StreamingOutput(this) {
            // $FF: synthetic field
            private final AbstractApplicationResource $outer;

            public void write(OutputStream output) {
               ZipOutputStream zipStream = new ZipOutputStream(output);

               try {
                  this.$outer.uiRoot().writeEventLogs(this.$outer.appId(), Option$.MODULE$.apply(this.$outer.attemptId()), zipStream);
               } finally {
                  zipStream.close();
               }

            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
         var16 = Response.ok(stream).header("Content-Disposition", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"attachment; filename=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{fileName}))).header("Content-Type", "application/octet-stream").build();
      } catch (Throwable var14) {
         Option var6 = NonFatal$.MODULE$.unapply(var14);
         if (var6.isEmpty()) {
            throw var14;
         }

         Response var7 = Response.serverError().entity((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Event logs are not available for app: ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId()}))).status(Status.SERVICE_UNAVAILABLE).build();
         var16 = var7;
         return var16;
      }

      return var16;
   }

   @Path("{attemptId}")
   public Class<OneApplicationAttemptResource> applicationAttempt() {
      if (this.attemptId() == null) {
         return OneApplicationAttemptResource.class;
      } else {
         throw new NotFoundException(this.httpRequest().getRequestURI());
      }
   }

   public AbstractApplicationResource() {
      ApiRequestContext$class.$init$(this);
      BaseAppResource$class.$init$(this);
   }
}
