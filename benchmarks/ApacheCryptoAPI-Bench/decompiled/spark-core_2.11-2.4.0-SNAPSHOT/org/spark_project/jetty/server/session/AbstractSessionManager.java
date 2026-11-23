package org.spark_project.jetty.server.session;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import org.spark_project.jetty.http.HttpCookie;
import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.server.SessionIdManager;
import org.spark_project.jetty.server.SessionManager;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.statistic.CounterStatistic;
import org.spark_project.jetty.util.statistic.SampleStatistic;

@ManagedObject("Abstract Session Manager")
public abstract class AbstractSessionManager extends ContainerLifeCycle implements SessionManager {
   static final Logger __log;
   public Set<SessionTrackingMode> __defaultSessionTrackingModes;
   public static final int __distantFuture = 628992000;
   public static final BigDecimal MAX_INACTIVE_MINUTES;
   static final HttpSessionContext __nullSessionContext;
   private boolean _usingCookies;
   protected int _dftMaxIdleSecs;
   protected SessionHandler _sessionHandler;
   protected boolean _httpOnly;
   protected SessionIdManager _sessionIdManager;
   protected boolean _secureCookies;
   protected boolean _secureRequestOnly;
   protected final List<HttpSessionAttributeListener> _sessionAttributeListeners;
   protected final List<HttpSessionListener> _sessionListeners;
   protected final List<HttpSessionIdListener> _sessionIdListeners;
   protected ClassLoader _loader;
   protected ContextHandler.Context _context;
   protected String _sessionCookie;
   protected String _sessionIdPathParameterName;
   protected String _sessionIdPathParameterNamePrefix;
   protected String _sessionDomain;
   protected String _sessionPath;
   protected int _maxCookieAge;
   protected int _refreshCookieAge;
   protected boolean _nodeIdInSessionId;
   protected boolean _checkingRemoteSessionIdEncoding;
   protected String _sessionComment;
   public Set<SessionTrackingMode> _sessionTrackingModes;
   private boolean _usingURLs;
   protected final CounterStatistic _sessionsStats;
   protected final SampleStatistic _sessionTimeStats;
   private SessionCookieConfig _cookieConfig;

   public AbstractSessionManager() {
      this.__defaultSessionTrackingModes = Collections.unmodifiableSet(new HashSet(Arrays.asList(SessionTrackingMode.COOKIE, SessionTrackingMode.URL)));
      this._usingCookies = true;
      this._dftMaxIdleSecs = -1;
      this._httpOnly = false;
      this._secureCookies = false;
      this._secureRequestOnly = true;
      this._sessionAttributeListeners = new CopyOnWriteArrayList();
      this._sessionListeners = new CopyOnWriteArrayList();
      this._sessionIdListeners = new CopyOnWriteArrayList();
      this._sessionCookie = "JSESSIONID";
      this._sessionIdPathParameterName = "jsessionid";
      this._sessionIdPathParameterNamePrefix = ";" + this._sessionIdPathParameterName + "=";
      this._maxCookieAge = -1;
      this._sessionsStats = new CounterStatistic();
      this._sessionTimeStats = new SampleStatistic();
      this._cookieConfig = new CookieConfig();
      this.setSessionTrackingModes(this.__defaultSessionTrackingModes);
   }

   public ContextHandler.Context getContext() {
      return this._context;
   }

   public ContextHandler getContextHandler() {
      return this._context.getContextHandler();
   }

   @ManagedAttribute("path of the session cookie, or null for default")
   public String getSessionPath() {
      return this._sessionPath;
   }

   @ManagedAttribute("if greater the zero, the time in seconds a session cookie will last for")
   public int getMaxCookieAge() {
      return this._maxCookieAge;
   }

   public HttpCookie access(HttpSession session, boolean secure) {
      long now = System.currentTimeMillis();
      AbstractSession s = ((SessionIf)session).getSession();
      if (s.access(now) && this.isUsingCookies() && (s.isIdChanged() || this.getSessionCookieConfig().getMaxAge() > 0 && this.getRefreshCookieAge() > 0 && (now - s.getCookieSetTime()) / 1000L > (long)this.getRefreshCookieAge())) {
         HttpCookie cookie = this.getSessionCookie(session, this._context == null ? "/" : this._context.getContextPath(), secure);
         s.cookieSet();
         s.setIdChanged(false);
         return cookie;
      } else {
         return null;
      }
   }

   public void addEventListener(EventListener listener) {
      if (listener instanceof HttpSessionAttributeListener) {
         this._sessionAttributeListeners.add((HttpSessionAttributeListener)listener);
      }

      if (listener instanceof HttpSessionListener) {
         this._sessionListeners.add((HttpSessionListener)listener);
      }

      if (listener instanceof HttpSessionIdListener) {
         this._sessionIdListeners.add((HttpSessionIdListener)listener);
      }

      this.addBean(listener, false);
   }

   public void clearEventListeners() {
      for(EventListener e : this.getBeans(EventListener.class)) {
         this.removeBean(e);
      }

      this._sessionAttributeListeners.clear();
      this._sessionListeners.clear();
      this._sessionIdListeners.clear();
   }

   public void complete(HttpSession session) {
      AbstractSession s = ((SessionIf)session).getSession();
      s.complete();
   }

   public void doStart() throws Exception {
      this._context = ContextHandler.getCurrentContext();
      this._loader = Thread.currentThread().getContextClassLoader();
      Server server = this.getSessionHandler().getServer();
      synchronized(server) {
         if (this._sessionIdManager == null) {
            this._sessionIdManager = server.getSessionIdManager();
            if (this._sessionIdManager == null) {
               ClassLoader serverLoader = server.getClass().getClassLoader();

               try {
                  Thread.currentThread().setContextClassLoader(serverLoader);
                  this._sessionIdManager = new HashSessionIdManager();
                  server.setSessionIdManager(this._sessionIdManager);
                  server.manage(this._sessionIdManager);
                  this._sessionIdManager.start();
               } finally {
                  Thread.currentThread().setContextClassLoader(this._loader);
               }
            }

            this.addBean(this._sessionIdManager, false);
         }
      }

      if (this._context != null) {
         String tmp = this._context.getInitParameter("org.spark_project.jetty.servlet.SessionCookie");
         if (tmp != null) {
            this._sessionCookie = tmp;
         }

         tmp = this._context.getInitParameter("org.spark_project.jetty.servlet.SessionIdPathParameterName");
         if (tmp != null) {
            this.setSessionIdPathParameterName(tmp);
         }

         if (this._maxCookieAge == -1) {
            tmp = this._context.getInitParameter("org.spark_project.jetty.servlet.MaxAge");
            if (tmp != null) {
               this._maxCookieAge = Integer.parseInt(tmp.trim());
            }
         }

         if (this._sessionDomain == null) {
            this._sessionDomain = this._context.getInitParameter("org.spark_project.jetty.servlet.SessionDomain");
         }

         if (this._sessionPath == null) {
            this._sessionPath = this._context.getInitParameter("org.spark_project.jetty.servlet.SessionPath");
         }

         tmp = this._context.getInitParameter("org.spark_project.jetty.servlet.CheckingRemoteSessionIdEncoding");
         if (tmp != null) {
            this._checkingRemoteSessionIdEncoding = Boolean.parseBoolean(tmp);
         }
      }

      super.doStart();
   }

   public void doStop() throws Exception {
      super.doStop();
      this.shutdownSessions();
      this._loader = null;
   }

   @ManagedAttribute("true if cookies use the http only flag")
   public boolean getHttpOnly() {
      return this._httpOnly;
   }

   public HttpSession getHttpSession(String nodeId) {
      String cluster_id = this.getSessionIdManager().getClusterId(nodeId);
      AbstractSession session = this.getSession(cluster_id);
      if (session != null && !session.getNodeId().equals(nodeId)) {
         session.setIdChanged(true);
      }

      return session;
   }

   @ManagedAttribute("Session ID Manager")
   public SessionIdManager getSessionIdManager() {
      return this._sessionIdManager;
   }

   @ManagedAttribute("defailt maximum time a session may be idle for (in s)")
   public int getMaxInactiveInterval() {
      return this._dftMaxIdleSecs;
   }

   @ManagedAttribute("maximum number of simultaneous sessions")
   public int getSessionsMax() {
      return (int)this._sessionsStats.getMax();
   }

   @ManagedAttribute("total number of sessions")
   public int getSessionsTotal() {
      return (int)this._sessionsStats.getTotal();
   }

   @ManagedAttribute("time before a session cookie is re-set (in s)")
   public int getRefreshCookieAge() {
      return this._refreshCookieAge;
   }

   @ManagedAttribute("if true, secure cookie flag is set on session cookies")
   public boolean getSecureCookies() {
      return this._secureCookies;
   }

   public boolean isSecureRequestOnly() {
      return this._secureRequestOnly;
   }

   public void setSecureRequestOnly(boolean secureRequestOnly) {
      this._secureRequestOnly = secureRequestOnly;
   }

   @ManagedAttribute("the set session cookie")
   public String getSessionCookie() {
      return this._sessionCookie;
   }

   public HttpCookie getSessionCookie(HttpSession session, String contextPath, boolean requestIsSecure) {
      if (this.isUsingCookies()) {
         String sessionPath = this._cookieConfig.getPath() == null ? contextPath : this._cookieConfig.getPath();
         sessionPath = sessionPath != null && sessionPath.length() != 0 ? sessionPath : "/";
         String id = this.getNodeId(session);
         HttpCookie cookie = null;
         if (this._sessionComment == null) {
            cookie = new HttpCookie(this._cookieConfig.getName(), id, this._cookieConfig.getDomain(), sessionPath, (long)this._cookieConfig.getMaxAge(), this._cookieConfig.isHttpOnly(), this._cookieConfig.isSecure() || this.isSecureRequestOnly() && requestIsSecure);
         } else {
            cookie = new HttpCookie(this._cookieConfig.getName(), id, this._cookieConfig.getDomain(), sessionPath, (long)this._cookieConfig.getMaxAge(), this._cookieConfig.isHttpOnly(), this._cookieConfig.isSecure() || this.isSecureRequestOnly() && requestIsSecure, this._sessionComment, 1);
         }

         return cookie;
      } else {
         return null;
      }
   }

   @ManagedAttribute("domain of the session cookie, or null for the default")
   public String getSessionDomain() {
      return this._sessionDomain;
   }

   public SessionHandler getSessionHandler() {
      return this._sessionHandler;
   }

   @ManagedAttribute("number of currently active sessions")
   public int getSessions() {
      return (int)this._sessionsStats.getCurrent();
   }

   @ManagedAttribute("name of use for URL session tracking")
   public String getSessionIdPathParameterName() {
      return this._sessionIdPathParameterName;
   }

   public String getSessionIdPathParameterNamePrefix() {
      return this._sessionIdPathParameterNamePrefix;
   }

   public boolean isUsingCookies() {
      return this._usingCookies;
   }

   public boolean isValid(HttpSession session) {
      AbstractSession s = ((SessionIf)session).getSession();
      return s.isValid();
   }

   public String getClusterId(HttpSession session) {
      AbstractSession s = ((SessionIf)session).getSession();
      return s.getClusterId();
   }

   public String getNodeId(HttpSession session) {
      AbstractSession s = ((SessionIf)session).getSession();
      return s.getNodeId();
   }

   public HttpSession newHttpSession(HttpServletRequest request) {
      AbstractSession session = this.newSession(request);
      session.setMaxInactiveInterval(this._dftMaxIdleSecs);
      if (request.isSecure()) {
         session.setAttribute("org.spark_project.jetty.security.sessionCreatedSecure", Boolean.TRUE);
      }

      this.addSession(session, true);
      return session;
   }

   public void removeEventListener(EventListener listener) {
      if (listener instanceof HttpSessionAttributeListener) {
         this._sessionAttributeListeners.remove(listener);
      }

      if (listener instanceof HttpSessionListener) {
         this._sessionListeners.remove(listener);
      }

      if (listener instanceof HttpSessionIdListener) {
         this._sessionIdListeners.remove(listener);
      }

      this.removeBean(listener);
   }

   @ManagedOperation(
      value = "reset statistics",
      impact = "ACTION"
   )
   public void statsReset() {
      this._sessionsStats.reset((long)this.getSessions());
      this._sessionTimeStats.reset();
   }

   public void setHttpOnly(boolean httpOnly) {
      this._httpOnly = httpOnly;
   }

   public void setSessionIdManager(SessionIdManager metaManager) {
      this.updateBean(this._sessionIdManager, metaManager);
      this._sessionIdManager = metaManager;
   }

   public void setMaxInactiveInterval(int seconds) {
      this._dftMaxIdleSecs = seconds;
      if (__log.isDebugEnabled()) {
         if (this._dftMaxIdleSecs <= 0) {
            __log.debug("Sessions created by this manager are immortal (default maxInactiveInterval={})", (long)this._dftMaxIdleSecs);
         } else {
            __log.debug("SessionManager default maxInactiveInterval={}", (long)this._dftMaxIdleSecs);
         }
      }

   }

   public void setRefreshCookieAge(int ageInSeconds) {
      this._refreshCookieAge = ageInSeconds;
   }

   public void setSessionCookie(String cookieName) {
      this._sessionCookie = cookieName;
   }

   public void setSessionHandler(SessionHandler sessionHandler) {
      this._sessionHandler = sessionHandler;
   }

   public void setSessionIdPathParameterName(String param) {
      this._sessionIdPathParameterName = param != null && !"none".equals(param) ? param : null;
      this._sessionIdPathParameterNamePrefix = param != null && !"none".equals(param) ? ";" + this._sessionIdPathParameterName + "=" : null;
   }

   public void setUsingCookies(boolean usingCookies) {
      this._usingCookies = usingCookies;
   }

   protected abstract void addSession(AbstractSession var1);

   protected void addSession(AbstractSession session, boolean created) {
      synchronized(this._sessionIdManager) {
         this._sessionIdManager.addSession(session);
         this.addSession(session);
      }

      if (created) {
         this._sessionsStats.increment();
         if (this._sessionListeners != null) {
            HttpSessionEvent event = new HttpSessionEvent(session);

            for(HttpSessionListener listener : this._sessionListeners) {
               listener.sessionCreated(event);
            }
         }
      }

   }

   public abstract AbstractSession getSession(String var1);

   protected abstract void shutdownSessions() throws Exception;

   protected abstract AbstractSession newSession(HttpServletRequest var1);

   public boolean isNodeIdInSessionId() {
      return this._nodeIdInSessionId;
   }

   public void setNodeIdInSessionId(boolean nodeIdInSessionId) {
      this._nodeIdInSessionId = nodeIdInSessionId;
   }

   public void removeSession(HttpSession session, boolean invalidate) {
      AbstractSession s = ((SessionIf)session).getSession();
      this.removeSession(s, invalidate);
   }

   public boolean removeSession(AbstractSession session, boolean invalidate) {
      boolean removed = this.removeSession(session.getClusterId());
      if (removed) {
         this._sessionsStats.decrement();
         this._sessionTimeStats.set(Math.round((double)(System.currentTimeMillis() - session.getCreationTime()) / (double)1000.0F));
         this._sessionIdManager.removeSession(session);
         if (invalidate) {
            this._sessionIdManager.invalidateAll(session.getClusterId());
         }

         if (invalidate && this._sessionListeners != null) {
            HttpSessionEvent event = new HttpSessionEvent(session);

            for(int i = this._sessionListeners.size() - 1; i >= 0; --i) {
               ((HttpSessionListener)this._sessionListeners.get(i)).sessionDestroyed(event);
            }
         }
      }

      return removed;
   }

   protected abstract boolean removeSession(String var1);

   @ManagedAttribute("maximum amount of time sessions have remained active (in s)")
   public long getSessionTimeMax() {
      return this._sessionTimeStats.getMax();
   }

   public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
      return this.__defaultSessionTrackingModes;
   }

   public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
      return Collections.unmodifiableSet(this._sessionTrackingModes);
   }

   public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
      this._sessionTrackingModes = new HashSet(sessionTrackingModes);
      this._usingCookies = this._sessionTrackingModes.contains(SessionTrackingMode.COOKIE);
      this._usingURLs = this._sessionTrackingModes.contains(SessionTrackingMode.URL);
   }

   public boolean isUsingURLs() {
      return this._usingURLs;
   }

   public SessionCookieConfig getSessionCookieConfig() {
      return this._cookieConfig;
   }

   @ManagedAttribute("total time sessions have remained valid")
   public long getSessionTimeTotal() {
      return this._sessionTimeStats.getTotal();
   }

   @ManagedAttribute("mean time sessions remain valid (in s)")
   public double getSessionTimeMean() {
      return this._sessionTimeStats.getMean();
   }

   @ManagedAttribute("standard deviation a session remained valid (in s)")
   public double getSessionTimeStdDev() {
      return this._sessionTimeStats.getStdDev();
   }

   @ManagedAttribute("check remote session id encoding")
   public boolean isCheckingRemoteSessionIdEncoding() {
      return this._checkingRemoteSessionIdEncoding;
   }

   public void setCheckingRemoteSessionIdEncoding(boolean remote) {
      this._checkingRemoteSessionIdEncoding = remote;
   }

   public void renewSessionId(String oldClusterId, String oldNodeId, String newClusterId, String newNodeId) {
      if (!this._sessionIdListeners.isEmpty()) {
         AbstractSession session = this.getSession(newClusterId);
         HttpSessionEvent event = new HttpSessionEvent(session);

         for(HttpSessionIdListener l : this._sessionIdListeners) {
            l.sessionIdChanged(event, oldClusterId);
         }
      }

   }

   public void doSessionAttributeListeners(AbstractSession session, String name, Object old, Object value) {
      if (!this._sessionAttributeListeners.isEmpty()) {
         HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, name, old == null ? value : old);

         for(HttpSessionAttributeListener l : this._sessionAttributeListeners) {
            if (old == null) {
               l.attributeAdded(event);
            } else if (value == null) {
               l.attributeRemoved(event);
            } else {
               l.attributeReplaced(event);
            }
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public SessionIdManager getMetaManager() {
      throw new UnsupportedOperationException();
   }

   static {
      __log = SessionHandler.LOG;
      MAX_INACTIVE_MINUTES = new BigDecimal(35791394);
      __nullSessionContext = new HttpSessionContext() {
         public HttpSession getSession(String sessionId) {
            return null;
         }

         public Enumeration getIds() {
            return Collections.enumeration(Collections.EMPTY_LIST);
         }
      };
   }

   public final class CookieConfig implements SessionCookieConfig {
      public String getComment() {
         return AbstractSessionManager.this._sessionComment;
      }

      public String getDomain() {
         return AbstractSessionManager.this._sessionDomain;
      }

      public int getMaxAge() {
         return AbstractSessionManager.this._maxCookieAge;
      }

      public String getName() {
         return AbstractSessionManager.this._sessionCookie;
      }

      public String getPath() {
         return AbstractSessionManager.this._sessionPath;
      }

      public boolean isHttpOnly() {
         return AbstractSessionManager.this._httpOnly;
      }

      public boolean isSecure() {
         return AbstractSessionManager.this._secureCookies;
      }

      public void setComment(String comment) {
         if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
            throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
         } else {
            AbstractSessionManager.this._sessionComment = comment;
         }
      }

      public void setDomain(String domain) {
         if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
            throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
         } else {
            AbstractSessionManager.this._sessionDomain = domain;
         }
      }

      public void setHttpOnly(boolean httpOnly) {
         if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
            throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
         } else {
            AbstractSessionManager.this._httpOnly = httpOnly;
         }
      }

      public void setMaxAge(int maxAge) {
         if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
            throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
         } else {
            AbstractSessionManager.this._maxCookieAge = maxAge;
         }
      }

      public void setName(String name) {
         if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
            throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
         } else {
            AbstractSessionManager.this._sessionCookie = name;
         }
      }

      public void setPath(String path) {
         if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
            throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
         } else {
            AbstractSessionManager.this._sessionPath = path;
         }
      }

      public void setSecure(boolean secure) {
         if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
            throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
         } else {
            AbstractSessionManager.this._secureCookies = secure;
         }
      }
   }

   public interface SessionIf extends HttpSession {
      AbstractSession getSession();
   }
}
