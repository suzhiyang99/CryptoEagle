package org.spark_project.jetty.server.session;

import java.io.IOException;
import java.util.EnumSet;
import java.util.EventListener;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import org.spark_project.jetty.http.HttpCookie;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.SessionManager;
import org.spark_project.jetty.server.handler.ScopedHandler;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class SessionHandler extends ScopedHandler {
   static final Logger LOG = Log.getLogger("org.spark_project.jetty.server.session");
   public static final EnumSet<SessionTrackingMode> DEFAULT_TRACKING;
   public static final Class<? extends EventListener>[] SESSION_LISTENER_TYPES;
   private SessionManager _sessionManager;

   public SessionHandler() {
      this(new HashSessionManager());
   }

   public SessionHandler(SessionManager manager) {
      this.setSessionManager(manager);
   }

   public SessionManager getSessionManager() {
      return this._sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         if (sessionManager != null) {
            sessionManager.setSessionHandler(this);
         }

         this.updateBean(this._sessionManager, sessionManager);
         this._sessionManager = sessionManager;
      }
   }

   protected void doStart() throws Exception {
      if (this._sessionManager == null) {
         this.setSessionManager(new HashSessionManager());
      }

      super.doStart();
   }

   protected void doStop() throws Exception {
      super.doStop();
   }

   public void doScope(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      SessionManager old_session_manager = null;
      HttpSession old_session = null;
      HttpSession access = null;

      try {
         old_session_manager = baseRequest.getSessionManager();
         old_session = baseRequest.getSession(false);
         if (old_session_manager != this._sessionManager) {
            baseRequest.setSessionManager(this._sessionManager);
            baseRequest.setSession((HttpSession)null);
            this.checkRequestedSessionId(baseRequest, request);
         }

         HttpSession session = null;
         if (this._sessionManager != null) {
            session = baseRequest.getSession(false);
            if (session != null) {
               if (session != old_session && (request.getDispatcherType() == DispatcherType.ASYNC || request.getDispatcherType() == DispatcherType.REQUEST)) {
                  access = session;
                  HttpCookie cookie = this._sessionManager.access(session, request.isSecure());
                  if (cookie != null) {
                     baseRequest.getResponse().addCookie(cookie);
                  }
               }
            } else {
               session = baseRequest.recoverNewSession(this._sessionManager);
               if (session != null) {
                  baseRequest.setSession(session);
               }
            }
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("sessionManager=" + this._sessionManager);
            LOG.debug("session=" + session);
         }

         if (this._nextScope != null) {
            this._nextScope.doScope(target, baseRequest, request, response);
         } else if (this._outerScope != null) {
            this._outerScope.doHandle(target, baseRequest, request, response);
         } else {
            this.doHandle(target, baseRequest, request, response);
         }
      } finally {
         if (access != null) {
            this._sessionManager.complete(access);
         }

         HttpSession session = baseRequest.getSession(false);
         if (session != null && old_session == null && session != access && (request.getDispatcherType() == DispatcherType.ASYNC || request.getDispatcherType() == DispatcherType.REQUEST)) {
            this._sessionManager.complete(session);
         }

         if (old_session_manager != null && old_session_manager != this._sessionManager) {
            baseRequest.setSessionManager(old_session_manager);
            baseRequest.setSession(old_session);
         }

      }

   }

   public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      if (this.never()) {
         this.nextHandle(target, baseRequest, request, response);
      } else if (this._nextScope != null && this._nextScope == this._handler) {
         this._nextScope.doHandle(target, baseRequest, request, response);
      } else if (this._handler != null) {
         this._handler.handle(target, baseRequest, request, response);
      }

   }

   protected void checkRequestedSessionId(Request baseRequest, HttpServletRequest request) {
      String requested_session_id = request.getRequestedSessionId();
      SessionManager sessionManager = this.getSessionManager();
      if (requested_session_id != null && sessionManager != null) {
         HttpSession session = sessionManager.getHttpSession(requested_session_id);
         if (session != null && sessionManager.isValid(session)) {
            baseRequest.setSession(session);
         }

      } else if (DispatcherType.REQUEST.equals(baseRequest.getDispatcherType())) {
         boolean requested_session_id_from_cookie = false;
         HttpSession session = null;
         if (this._sessionManager.isUsingCookies()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
               String sessionCookie = sessionManager.getSessionCookieConfig().getName();

               for(int i = 0; i < cookies.length; ++i) {
                  if (sessionCookie.equalsIgnoreCase(cookies[i].getName())) {
                     requested_session_id = cookies[i].getValue();
                     requested_session_id_from_cookie = true;
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Got Session ID {} from cookie", requested_session_id);
                     }

                     if (requested_session_id != null) {
                        session = sessionManager.getHttpSession(requested_session_id);
                        if (session != null && sessionManager.isValid(session)) {
                           break;
                        }
                     } else {
                        LOG.warn("null session id from cookie");
                     }
                  }
               }
            }
         }

         if (requested_session_id == null || session == null) {
            String uri = request.getRequestURI();
            String prefix = sessionManager.getSessionIdPathParameterNamePrefix();
            if (prefix != null) {
               int s = uri.indexOf(prefix);
               if (s >= 0) {
                  s += prefix.length();

                  int i;
                  for(i = s; i < uri.length(); ++i) {
                     char c = uri.charAt(i);
                     if (c == ';' || c == '#' || c == '?' || c == '/') {
                        break;
                     }
                  }

                  requested_session_id = uri.substring(s, i);
                  requested_session_id_from_cookie = false;
                  session = sessionManager.getHttpSession(requested_session_id);
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Got Session ID {} from URL", requested_session_id);
                  }
               }
            }
         }

         baseRequest.setRequestedSessionId(requested_session_id);
         baseRequest.setRequestedSessionIdFromCookie(requested_session_id != null && requested_session_id_from_cookie);
         if (session != null && sessionManager.isValid(session)) {
            baseRequest.setSession(session);
         }

      }
   }

   public void addEventListener(EventListener listener) {
      if (this._sessionManager != null) {
         this._sessionManager.addEventListener(listener);
      }

   }

   public void removeEventListener(EventListener listener) {
      if (this._sessionManager != null) {
         this._sessionManager.removeEventListener(listener);
      }

   }

   public void clearEventListeners() {
      if (this._sessionManager != null) {
         this._sessionManager.clearEventListeners();
      }

   }

   static {
      DEFAULT_TRACKING = EnumSet.of(SessionTrackingMode.COOKIE, SessionTrackingMode.URL);
      SESSION_LISTENER_TYPES = new Class[]{HttpSessionAttributeListener.class, HttpSessionIdListener.class, HttpSessionListener.class};
   }
}
