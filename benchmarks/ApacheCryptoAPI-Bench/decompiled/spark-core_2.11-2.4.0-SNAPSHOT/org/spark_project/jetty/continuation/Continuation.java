package org.spark_project.jetty.continuation;

import javax.servlet.ServletResponse;

public interface Continuation {
   String ATTRIBUTE = "org.spark_project.jetty.continuation";

   void setTimeout(long var1);

   void suspend();

   void suspend(ServletResponse var1);

   void resume();

   void complete();

   boolean isSuspended();

   boolean isResumed();

   boolean isExpired();

   boolean isInitial();

   boolean isResponseWrapped();

   ServletResponse getServletResponse();

   void addContinuationListener(ContinuationListener var1);

   void setAttribute(String var1, Object var2);

   Object getAttribute(String var1);

   void removeAttribute(String var1);

   void undispatch() throws ContinuationThrowable;
}
