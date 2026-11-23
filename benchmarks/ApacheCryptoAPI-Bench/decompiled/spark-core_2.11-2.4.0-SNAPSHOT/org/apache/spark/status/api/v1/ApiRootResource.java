package org.apache.spark.status.api.v1;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.apache.spark.package$;
import org.spark_project.jetty.servlet.ServletContextHandler;
import scala.reflect.ScalaSignature;

@Path("/v1")
@ScalaSignature(
   bytes = "\u0006\u0001\u001d4Q!\u0001\u0002\u0001\u00059\u0011q\"\u00119j%>|GOU3t_V\u00148-\u001a\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c2\u0001A\b\u0016!\t\u00012#D\u0001\u0012\u0015\u0005\u0011\u0012!B:dC2\f\u0017B\u0001\u000b\u0012\u0005\u0019\te.\u001f*fMB\u0011acF\u0007\u0002\u0005%\u0011\u0001D\u0001\u0002\u0012\u0003BL'+Z9vKN$8i\u001c8uKb$\b\"\u0002\u000e\u0001\t\u0003a\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003u\u0001\"A\u0006\u0001\t\u000b}\u0001A\u0011\u0001\u0011\u0002\u001f\u0005\u0004\b\u000f\\5dCRLwN\u001c'jgR$\u0012!\t\t\u0004E\u0015BcB\u0001\t$\u0013\t!\u0013#\u0001\u0004Qe\u0016$WMZ\u0005\u0003M\u001d\u0012Qa\u00117bgNT!\u0001J\t\u0011\u0005YI\u0013B\u0001\u0016\u0003\u0005]\t\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8MSN$(+Z:pkJ\u001cW\r\u000b\u0003\u001fYY:\u0004CA\u00175\u001b\u0005q#BA\u00181\u0003\t\u00118O\u0003\u00022e\u0005\u0011qo\u001d\u0006\u0002g\u0005)!.\u0019<bq&\u0011QG\f\u0002\u0005!\u0006$\b.A\u0003wC2,X-I\u00019\u00031\t\u0007\u000f\u001d7jG\u0006$\u0018n\u001c8t\u0011\u0015Q\u0004\u0001\"\u0001<\u0003-\t\u0007\u000f\u001d7jG\u0006$\u0018n\u001c8\u0015\u0003q\u00022AI\u0013>!\t1b(\u0003\u0002@\u0005\t1rJ\\3BaBd\u0017nY1uS>t'+Z:pkJ\u001cW\r\u000b\u0003:YY\n\u0015%\u0001\"\u0002)\u0005\u0004\b\u000f\\5dCRLwN\\:0w\u0006\u0004\b/\u00133~\u0011\u0015!\u0005\u0001\"\u0001F\u0003\u001d1XM]:j_:$\u0012A\u0012\t\u0003-\u001dK!\u0001\u0013\u0002\u0003\u0017Y+'o]5p]&sgm\u001c\u0015\u0005\u000722$*I\u0001EQ\u0011\u0001AF\u000e'\"\u00035\u000b1a\f<2\u000f\u0019y%\u0001#\u0001\t!\u0006y\u0011\t]5S_>$(+Z:pkJ\u001cW\r\u0005\u0002\u0017#\u001a1\u0011A\u0001E\u0001\u0011I\u001b\"!U\b\t\u000bi\tF\u0011\u0001+\u0015\u0003ACQAV)\u0005\u0002]\u000b\u0011cZ3u'\u0016\u0014h\u000f\\3u\u0011\u0006tG\r\\3s)\tA&\r\u0005\u0002ZA6\t!L\u0003\u0002\\9\u000691/\u001a:wY\u0016$(BA/_\u0003\u0015QW\r\u001e;z\u0015\tyF\"A\u0004fG2L\u0007o]3\n\u0005\u0005T&!F*feZdW\r^\"p]R,\u0007\u0010\u001e%b]\u0012dWM\u001d\u0005\u0006GV\u0003\r\u0001Z\u0001\u0007k&\u0014vn\u001c;\u0011\u0005Y)\u0017B\u00014\u0003\u0005\u0019)\u0016JU8pi\u0002"
)
public class ApiRootResource implements ApiRequestContext {
   @Context
   private ServletContext servletContext;
   @Context
   private HttpServletRequest httpRequest;

   public static ServletContextHandler getServletHandler(UIRoot var0) {
      return ApiRootResource$.MODULE$.getServletHandler(var0);
   }

   public ServletContext servletContext() {
      return this.servletContext;
   }

   public void servletContext_$eq(ServletContext x$1) {
      this.servletContext = x$1;
   }

   public HttpServletRequest httpRequest() {
      return this.httpRequest;
   }

   public void httpRequest_$eq(HttpServletRequest x$1) {
      this.httpRequest = x$1;
   }

   public UIRoot uiRoot() {
      return ApiRequestContext$class.uiRoot(this);
   }

   @Path("applications")
   public Class<ApplicationListResource> applicationList() {
      return ApplicationListResource.class;
   }

   @Path("applications/{appId}")
   public Class<OneApplicationResource> application() {
      return OneApplicationResource.class;
   }

   @Path("version")
   public VersionInfo version() {
      return new VersionInfo(package$.MODULE$.SPARK_VERSION());
   }

   public ApiRootResource() {
      ApiRequestContext$class.$init$(this);
   }
}
