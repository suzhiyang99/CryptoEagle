package org.spark_project.jetty.continuation;

import java.lang.reflect.Constructor;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;

public class ContinuationSupport {
   static final boolean __servlet3;
   static final Class<?> __waitingContinuation;
   static final Constructor<? extends Continuation> __newServlet3Continuation;

   public static Continuation getContinuation(ServletRequest request) {
      Continuation continuation = (Continuation)request.getAttribute("org.spark_project.jetty.continuation");
      if (continuation != null) {
         return continuation;
      } else {
         while(request instanceof ServletRequestWrapper) {
            request = ((ServletRequestWrapper)request).getRequest();
         }

         if (__servlet3) {
            try {
               continuation = (Continuation)__newServlet3Continuation.newInstance(request);
               request.setAttribute("org.spark_project.jetty.continuation", continuation);
               return continuation;
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } else {
            throw new IllegalStateException("!(Jetty || Servlet 3.0 || ContinuationFilter)");
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public static Continuation getContinuation(ServletRequest request, ServletResponse response) {
      return getContinuation(request);
   }

   static {
      boolean servlet3Support = false;
      Constructor<? extends Continuation> s3cc = null;

      try {
         boolean servlet3 = ServletRequest.class.getMethod("startAsync") != null;
         if (servlet3) {
            Class<? extends Continuation> s3c = ContinuationSupport.class.getClassLoader().loadClass("org.spark_project.jetty.continuation.Servlet3Continuation").asSubclass(Continuation.class);
            s3cc = s3c.getConstructor(ServletRequest.class);
            servlet3Support = true;
         }
      } catch (Exception var16) {
      } finally {
         __servlet3 = servlet3Support;
         __newServlet3Continuation = s3cc;
      }

      Class<?> waiting = null;

      try {
         waiting = ContinuationSupport.class.getClassLoader().loadClass("org.mortbay.util.ajax.WaitingContinuation");
      } catch (Exception var14) {
      } finally {
         __waitingContinuation = waiting;
      }

   }
}
