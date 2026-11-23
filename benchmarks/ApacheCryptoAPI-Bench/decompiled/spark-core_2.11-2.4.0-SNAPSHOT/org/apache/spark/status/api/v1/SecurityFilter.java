package org.apache.spark.status.api.v1;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@Provider
@ScalaSignature(
   bytes = "\u0006\u0001\u00053Q!\u0001\u0002\u0001\u00059\u0011abU3dkJLG/\u001f$jYR,'O\u0003\u0002\u0004\t\u0005\u0011a/\r\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"\u0001\u0004ti\u0006$Xo\u001d\u0006\u0003\u0013)\tQa\u001d9be.T!a\u0003\u0007\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005i\u0011aA8sON!\u0001aD\f$!\t\u0001R#D\u0001\u0012\u0015\t\u00112#\u0001\u0003mC:<'\"\u0001\u000b\u0002\t)\fg/Y\u0005\u0003-E\u0011aa\u00142kK\u000e$\bC\u0001\r\"\u001b\u0005I\"B\u0001\u000e\u001c\u0003%\u0019wN\u001c;bS:,'O\u0003\u0002\u001d;\u0005\u0011!o\u001d\u0006\u0003=}\t!a^:\u000b\u0003\u0001\nQA[1wCbL!AI\r\u0003-\r{g\u000e^1j]\u0016\u0014(+Z9vKN$h)\u001b7uKJ\u0004\"\u0001J\u0013\u000e\u0003\tI!A\n\u0002\u0003#\u0005\u0003\u0018NU3rk\u0016\u001cHoQ8oi\u0016DH\u000fC\u0003)\u0001\u0011\u0005!&\u0001\u0004=S:LGOP\u0002\u0001)\u0005Y\u0003C\u0001\u0013\u0001\u0011\u0015i\u0003\u0001\"\u0011/\u0003\u00191\u0017\u000e\u001c;feR\u0011q&\u000e\t\u0003aMj\u0011!\r\u0006\u0002e\u0005)1oY1mC&\u0011A'\r\u0002\u0005+:LG\u000fC\u00037Y\u0001\u0007q'A\u0002sKF\u0004\"\u0001\u0007\u001d\n\u0005eJ\"aF\"p]R\f\u0017N\\3s%\u0016\fX/Z:u\u0007>tG/\u001a=uQ\t\u00011\b\u0005\u0002=\u007f5\tQH\u0003\u0002?7\u0005\u0019Q\r\u001f;\n\u0005\u0001k$\u0001\u0003)s_ZLG-\u001a:"
)
public class SecurityFilter implements ContainerRequestFilter, ApiRequestContext {
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

   public void filter(ContainerRequestContext req) {
      String user = this.httpRequest().getRemoteUser();
      if (!this.uiRoot().securityManager().checkUIViewPermissions(user)) {
         req.abortWith(Response.status(Status.FORBIDDEN).entity((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"user \"", "\" is not authorized"})))).raw(Predef$.MODULE$.genericWrapArray(new Object[]{user}))).build());
      }

   }

   public SecurityFilter() {
      ApiRequestContext$class.$init$(this);
   }
}
