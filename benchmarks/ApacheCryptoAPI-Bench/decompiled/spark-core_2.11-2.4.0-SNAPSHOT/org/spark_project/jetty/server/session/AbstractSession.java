package org.spark_project.jetty.server.session;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import org.spark_project.jetty.server.SessionManager;
import org.spark_project.jetty.util.log.Logger;

public abstract class AbstractSession implements AbstractSessionManager.SessionIf {
   static final Logger LOG;
   public static final String SESSION_CREATED_SECURE = "org.spark_project.jetty.security.sessionCreatedSecure";
   private String _clusterId;
   private String _nodeId;
   private final AbstractSessionManager _manager;
   private boolean _idChanged;
   private final long _created;
   private long _cookieSet;
   private long _accessed;
   private long _lastAccessed;
   private boolean _invalid;
   private boolean _doInvalidate;
   private long _maxIdleMs;
   private boolean _newSession;
   private int _requests;

   protected AbstractSession(AbstractSessionManager abstractSessionManager, HttpServletRequest request) {
      this._manager = abstractSessionManager;
      this._newSession = true;
      this._created = System.currentTimeMillis();
      this._clusterId = this._manager._sessionIdManager.newSessionId(request, this._created);
      this._nodeId = this._manager._sessionIdManager.getNodeId(this._clusterId, request);
      this._accessed = this._created;
      this._lastAccessed = this._created;
      this._requests = 1;
      this._maxIdleMs = this._manager._dftMaxIdleSecs > 0 ? (long)this._manager._dftMaxIdleSecs * 1000L : -1L;
      if (LOG.isDebugEnabled()) {
         LOG.debug("New session & id " + this._nodeId + " " + this._clusterId);
      }

   }

   protected AbstractSession(AbstractSessionManager abstractSessionManager, long created, long accessed, String clusterId) {
      this._manager = abstractSessionManager;
      this._created = created;
      this._clusterId = clusterId;
      this._nodeId = this._manager._sessionIdManager.getNodeId(this._clusterId, (HttpServletRequest)null);
      this._accessed = accessed;
      this._lastAccessed = accessed;
      this._requests = 1;
      this._maxIdleMs = this._manager._dftMaxIdleSecs > 0 ? (long)this._manager._dftMaxIdleSecs * 1000L : -1L;
      if (LOG.isDebugEnabled()) {
         LOG.debug("Restored session " + this._nodeId + " " + this._clusterId);
      }

   }

   protected void checkValid() throws IllegalStateException {
      if (this._invalid) {
         throw new IllegalStateException("id=" + this._clusterId + " created=" + this._created + " accessed=" + this._accessed + " lastaccessed=" + this._lastAccessed + " maxInactiveMs=" + this._maxIdleMs);
      }
   }

   protected boolean checkExpiry(long time) {
      return this._maxIdleMs > 0L && this._lastAccessed > 0L && this._lastAccessed + this._maxIdleMs < time;
   }

   public AbstractSession getSession() {
      return this;
   }

   public long getAccessed() {
      synchronized(this) {
         return this._accessed;
      }
   }

   public abstract Map<String, Object> getAttributeMap();

   public abstract int getAttributes();

   public abstract Set<String> getNames();

   public long getCookieSetTime() {
      return this._cookieSet;
   }

   public void setCookieSetTime(long time) {
      this._cookieSet = time;
   }

   public long getCreationTime() throws IllegalStateException {
      this.checkValid();
      return this._created;
   }

   public String getId() throws IllegalStateException {
      return this._manager._nodeIdInSessionId ? this._nodeId : this._clusterId;
   }

   public String getNodeId() {
      return this._nodeId;
   }

   public String getClusterId() {
      return this._clusterId;
   }

   public long getLastAccessedTime() throws IllegalStateException {
      this.checkValid();
      return this._lastAccessed;
   }

   public void setLastAccessedTime(long time) {
      this._lastAccessed = time;
   }

   public int getMaxInactiveInterval() {
      return (int)(this._maxIdleMs / 1000L);
   }

   public ServletContext getServletContext() {
      return this._manager._context;
   }

   /** @deprecated */
   @Deprecated
   public HttpSessionContext getSessionContext() throws IllegalStateException {
      this.checkValid();
      return AbstractSessionManager.__nullSessionContext;
   }

   /** @deprecated */
   @Deprecated
   public Object getValue(String name) throws IllegalStateException {
      return this.getAttribute(name);
   }

   public void renewId(HttpServletRequest request) {
      this._manager._sessionIdManager.renewSessionId(this.getClusterId(), this.getNodeId(), request);
      this.setIdChanged(true);
   }

   public SessionManager getSessionManager() {
      return this._manager;
   }

   protected void setClusterId(String clusterId) {
      this._clusterId = clusterId;
   }

   protected void setNodeId(String nodeId) {
      this._nodeId = nodeId;
   }

   protected boolean access(long time) {
      synchronized(this) {
         if (this._invalid) {
            return false;
         } else {
            this._newSession = false;
            this._lastAccessed = this._accessed;
            this._accessed = time;
            if (this.checkExpiry(time)) {
               this.invalidate();
               return false;
            } else {
               ++this._requests;
               return true;
            }
         }
      }
   }

   protected void complete() {
      synchronized(this) {
         --this._requests;
         if (this._doInvalidate && this._requests <= 0) {
            this.doInvalidate();
         }

      }
   }

   protected void timeout() throws IllegalStateException {
      this._manager.removeSession(this, true);
      boolean do_invalidate = false;
      synchronized(this) {
         if (!this._invalid) {
            if (this._requests <= 0) {
               do_invalidate = true;
            } else {
               this._doInvalidate = true;
            }
         }
      }

      if (do_invalidate) {
         this.doInvalidate();
      }

   }

   public void invalidate() throws IllegalStateException {
      this.checkValid();
      this._manager.removeSession(this, true);
      this.doInvalidate();
   }

   protected void doInvalidate() throws IllegalStateException {
      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug("invalidate {}", this._clusterId);
         }

         if (this.isValid()) {
            this.clearAttributes();
         }
      } finally {
         synchronized(this) {
            this._invalid = true;
         }
      }

   }

   public abstract void clearAttributes();

   public boolean isIdChanged() {
      return this._idChanged;
   }

   public boolean isNew() throws IllegalStateException {
      this.checkValid();
      return this._newSession;
   }

   /** @deprecated */
   @Deprecated
   public void putValue(String name, Object value) throws IllegalStateException {
      this.changeAttribute(name, value);
   }

   public void removeAttribute(String name) {
      this.setAttribute(name, (Object)null);
   }

   /** @deprecated */
   @Deprecated
   public void removeValue(String name) throws IllegalStateException {
      this.removeAttribute(name);
   }

   public Enumeration<String> getAttributeNames() {
      synchronized(this) {
         this.checkValid();
         return this.doGetAttributeNames();
      }
   }

   /** @deprecated */
   @Deprecated
   public String[] getValueNames() throws IllegalStateException {
      synchronized(this) {
         this.checkValid();
         Enumeration<String> anames = this.doGetAttributeNames();
         if (anames == null) {
            return new String[0];
         } else {
            ArrayList<String> names = new ArrayList();

            while(anames.hasMoreElements()) {
               names.add(anames.nextElement());
            }

            return (String[])names.toArray(new String[names.size()]);
         }
      }
   }

   public abstract Object doPutOrRemove(String var1, Object var2);

   public abstract Object doGet(String var1);

   public abstract Enumeration<String> doGetAttributeNames();

   public Object getAttribute(String name) {
      synchronized(this) {
         this.checkValid();
         return this.doGet(name);
      }
   }

   public void setAttribute(String name, Object value) {
      this.changeAttribute(name, value);
   }

   /** @deprecated */
   @Deprecated
   protected boolean updateAttribute(String name, Object value) {
      Object old = null;
      synchronized(this) {
         this.checkValid();
         old = this.doPutOrRemove(name, value);
      }

      if (value != null && value.equals(old)) {
         return false;
      } else {
         if (old != null) {
            this.unbindValue(name, old);
         }

         if (value != null) {
            this.bindValue(name, value);
         }

         this._manager.doSessionAttributeListeners(this, name, old, value);
         return true;
      }
   }

   protected Object changeAttribute(String name, Object value) {
      Object old = null;
      synchronized(this) {
         this.checkValid();
         old = this.doPutOrRemove(name, value);
      }

      this.callSessionAttributeListeners(name, value, old);
      return old;
   }

   protected void callSessionAttributeListeners(String name, Object newValue, Object oldValue) {
      if (newValue == null || !newValue.equals(oldValue)) {
         if (oldValue != null) {
            this.unbindValue(name, oldValue);
         }

         if (newValue != null) {
            this.bindValue(name, newValue);
         }

         this._manager.doSessionAttributeListeners(this, name, oldValue, newValue);
      }

   }

   public void setIdChanged(boolean changed) {
      this._idChanged = changed;
   }

   public void setMaxInactiveInterval(int secs) {
      if (LOG.isDebugEnabled()) {
         if (secs <= 0) {
            LOG.debug("Session {} is now immortal (maxInactiveInterval={})", this._clusterId, secs);
         } else {
            LOG.debug("Session {} maxInactiveInterval={}", this._clusterId, secs);
         }
      }

      this._maxIdleMs = (long)secs * 1000L;
   }

   public String toString() {
      return this.getClass().getName() + ":" + this.getId() + "@" + this.hashCode();
   }

   public void bindValue(String name, Object value) {
      if (value != null && value instanceof HttpSessionBindingListener) {
         ((HttpSessionBindingListener)value).valueBound(new HttpSessionBindingEvent(this, name));
      }

   }

   public boolean isValid() {
      return !this._invalid;
   }

   protected void cookieSet() {
      synchronized(this) {
         this._cookieSet = this._accessed;
      }
   }

   public int getRequests() {
      synchronized(this) {
         return this._requests;
      }
   }

   public void setRequests(int requests) {
      synchronized(this) {
         this._requests = requests;
      }
   }

   public void unbindValue(String name, Object value) {
      if (value != null && value instanceof HttpSessionBindingListener) {
         ((HttpSessionBindingListener)value).valueUnbound(new HttpSessionBindingEvent(this, name));
      }

   }

   public void willPassivate() {
      synchronized(this) {
         HttpSessionEvent event = new HttpSessionEvent(this);

         for(Object value : this.getAttributeMap().values()) {
            if (value instanceof HttpSessionActivationListener) {
               HttpSessionActivationListener listener = (HttpSessionActivationListener)value;
               listener.sessionWillPassivate(event);
            }
         }

      }
   }

   public void didActivate() {
      synchronized(this) {
         HttpSessionEvent event = new HttpSessionEvent(this);

         for(Object value : this.getAttributeMap().values()) {
            if (value instanceof HttpSessionActivationListener) {
               HttpSessionActivationListener listener = (HttpSessionActivationListener)value;
               listener.sessionDidActivate(event);
            }
         }

      }
   }

   static {
      LOG = SessionHandler.LOG;
   }
}
