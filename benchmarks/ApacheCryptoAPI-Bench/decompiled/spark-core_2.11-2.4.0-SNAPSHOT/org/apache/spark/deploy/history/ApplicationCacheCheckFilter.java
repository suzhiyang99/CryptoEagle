package org.apache.spark.deploy.history;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option$;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001y3Q!\u0001\u0002\u0001\u00051\u00111$\u00119qY&\u001c\u0017\r^5p]\u000e\u000b7\r[3DQ\u0016\u001c7NR5mi\u0016\u0014(BA\u0002\u0005\u0003\u001dA\u0017n\u001d;pefT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0003\u0002\u0001\u000e+u\u0001\"AD\n\u000e\u0003=Q!\u0001E\t\u0002\t1\fgn\u001a\u0006\u0002%\u0005!!.\u0019<b\u0013\t!rB\u0001\u0004PE*,7\r\u001e\t\u0003-mi\u0011a\u0006\u0006\u00031e\tqa]3sm2,GOC\u0001\u001b\u0003\u0015Q\u0017M^1y\u0013\tarC\u0001\u0004GS2$XM\u001d\t\u0003=\u0005j\u0011a\b\u0006\u0003A\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003E}\u0011q\u0001T8hO&tw\r\u0003\u0005%\u0001\t\u0005\t\u0015!\u0003'\u0003\rYW-_\u0002\u0001!\t9\u0003&D\u0001\u0003\u0013\tI#A\u0001\u0005DC\u000eDWmS3z\u0011!Y\u0003A!A!\u0002\u0013a\u0013\u0001\u00037pC\u0012,G-V%\u0011\u0005\u001dj\u0013B\u0001\u0018\u0003\u0005-au.\u00193fI\u0006\u0003\b/V%\t\u0011A\u0002!\u0011!Q\u0001\nE\nQaY1dQ\u0016\u0004\"a\n\u001a\n\u0005M\u0012!\u0001E!qa2L7-\u0019;j_:\u001c\u0015m\u00195f\u0011\u0015)\u0004\u0001\"\u00017\u0003\u0019a\u0014N\\5u}Q!q\u0007O\u001d;!\t9\u0003\u0001C\u0003%i\u0001\u0007a\u0005C\u0003,i\u0001\u0007A\u0006C\u00031i\u0001\u0007\u0011\u0007C\u0003=\u0001\u0011\u0005S(\u0001\u0005e_\u001aKG\u000e^3s)\u0011qD)\u0013(\u0011\u0005}\u0012U\"\u0001!\u000b\u0003\u0005\u000bQa]2bY\u0006L!a\u0011!\u0003\tUs\u0017\u000e\u001e\u0005\u0006\u000bn\u0002\rAR\u0001\be\u0016\fX/Z:u!\t1r)\u0003\u0002I/\tq1+\u001a:wY\u0016$(+Z9vKN$\b\"\u0002&<\u0001\u0004Y\u0015\u0001\u0003:fgB|gn]3\u0011\u0005Ya\u0015BA'\u0018\u0005=\u0019VM\u001d<mKR\u0014Vm\u001d9p]N,\u0007\"B(<\u0001\u0004\u0001\u0016!B2iC&t\u0007C\u0001\fR\u0013\t\u0011vCA\u0006GS2$XM]\"iC&t\u0007\"\u0002+\u0001\t\u0003*\u0016\u0001B5oSR$\"A\u0010,\t\u000b]\u001b\u0006\u0019\u0001-\u0002\r\r|gNZ5h!\t1\u0012,\u0003\u0002[/\taa)\u001b7uKJ\u001cuN\u001c4jO\")A\f\u0001C!;\u00069A-Z:ue>LH#\u0001 "
)
public class ApplicationCacheCheckFilter implements Filter, Logging {
   private final CacheKey key;
   private final LoadedAppUI loadedUI;
   private final ApplicationCache cache;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
      if (request instanceof HttpServletRequest) {
         HttpServletRequest httpRequest = (HttpServletRequest)request;
         HttpServletResponse httpResponse = (HttpServletResponse)response;
         String requestURI = httpRequest.getRequestURI();
         String operation = httpRequest.getMethod();
         this.loadedUI.lock().readLock().lock();
         if (this.loadedUI.valid()) {
            try {
               chain.doFilter(request, response);
            } finally {
               this.loadedUI.lock().readLock().unlock();
            }
         } else {
            this.loadedUI.lock().readLock().unlock();
            this.cache.invalidate(this.key);
            String queryStr = (String)Option$.MODULE$.apply(httpRequest.getQueryString()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String x$2) {
                  return (new StringBuilder()).append((Object)"?").append((Object)x$2).toString();
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "";
               }
            });
            String redirectUrl = httpResponse.encodeRedirectURL((new StringBuilder()).append((Object)requestURI).append((Object)queryStr).toString());
            httpResponse.sendRedirect(redirectUrl);
         }

      } else {
         throw new ServletException("This filter only works for HTTP/HTTPS");
      }
   }

   public void init(FilterConfig config) {
   }

   public void destroy() {
   }

   public ApplicationCacheCheckFilter(CacheKey key, LoadedAppUI loadedUI, ApplicationCache cache) {
      this.key = key;
      this.loadedUI = loadedUI;
      this.cache = cache;
      Logging$class.$init$(this);
   }
}
