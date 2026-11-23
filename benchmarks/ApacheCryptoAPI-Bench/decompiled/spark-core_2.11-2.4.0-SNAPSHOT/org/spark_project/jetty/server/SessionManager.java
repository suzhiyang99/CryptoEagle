package org.spark_project.jetty.server;

import java.util.EventListener;
import java.util.Set;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.spark_project.jetty.http.HttpCookie;
import org.spark_project.jetty.server.session.SessionHandler;
import org.spark_project.jetty.util.component.LifeCycle;

public interface SessionManager extends LifeCycle {
   String __SessionCookieProperty = "org.spark_project.jetty.servlet.SessionCookie";
   String __DefaultSessionCookie = "JSESSIONID";
   String __SessionIdPathParameterNameProperty = "org.spark_project.jetty.servlet.SessionIdPathParameterName";
   String __DefaultSessionIdPathParameterName = "jsessionid";
   String __CheckRemoteSessionEncoding = "org.spark_project.jetty.servlet.CheckingRemoteSessionIdEncoding";
   String __SessionDomainProperty = "org.spark_project.jetty.servlet.SessionDomain";
   String __DefaultSessionDomain = null;
   String __SessionPathProperty = "org.spark_project.jetty.servlet.SessionPath";
   String __MaxAgeProperty = "org.spark_project.jetty.servlet.MaxAge";

   HttpSession getHttpSession(String var1);

   HttpSession newHttpSession(HttpServletRequest var1);

   boolean getHttpOnly();

   int getMaxInactiveInterval();

   void setMaxInactiveInterval(int var1);

   void setSessionHandler(SessionHandler var1);

   void addEventListener(EventListener var1);

   void removeEventListener(EventListener var1);

   void clearEventListeners();

   HttpCookie getSessionCookie(HttpSession var1, String var2, boolean var3);

   SessionIdManager getSessionIdManager();

   /** @deprecated */
   @Deprecated
   SessionIdManager getMetaManager();

   void setSessionIdManager(SessionIdManager var1);

   boolean isValid(HttpSession var1);

   String getNodeId(HttpSession var1);

   String getClusterId(HttpSession var1);

   HttpCookie access(HttpSession var1, boolean var2);

   void complete(HttpSession var1);

   void setSessionIdPathParameterName(String var1);

   String getSessionIdPathParameterName();

   String getSessionIdPathParameterNamePrefix();

   boolean isUsingCookies();

   boolean isUsingURLs();

   Set<SessionTrackingMode> getDefaultSessionTrackingModes();

   Set<SessionTrackingMode> getEffectiveSessionTrackingModes();

   void setSessionTrackingModes(Set<SessionTrackingMode> var1);

   SessionCookieConfig getSessionCookieConfig();

   boolean isCheckingRemoteSessionIdEncoding();

   void setCheckingRemoteSessionIdEncoding(boolean var1);

   void renewSessionId(String var1, String var2, String var3, String var4);
}
