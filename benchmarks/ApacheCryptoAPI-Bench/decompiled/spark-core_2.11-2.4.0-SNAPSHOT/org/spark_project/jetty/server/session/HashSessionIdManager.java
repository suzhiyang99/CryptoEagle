package org.spark_project.jetty.server.session;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HashSessionIdManager extends AbstractSessionIdManager {
   private final Map<String, Set<WeakReference<HttpSession>>> _sessions = new HashMap();

   public HashSessionIdManager() {
   }

   public HashSessionIdManager(Random random) {
      super(random);
   }

   public Collection<String> getSessions() {
      return Collections.unmodifiableCollection(this._sessions.keySet());
   }

   public Collection<HttpSession> getSession(String id) {
      ArrayList<HttpSession> sessions = new ArrayList();
      Set<WeakReference<HttpSession>> refs = (Set)this._sessions.get(id);
      if (refs != null) {
         for(WeakReference<HttpSession> ref : refs) {
            HttpSession session = (HttpSession)ref.get();
            if (session != null) {
               sessions.add(session);
            }
         }
      }

      return sessions;
   }

   protected void doStart() throws Exception {
      super.doStart();
   }

   protected void doStop() throws Exception {
      this._sessions.clear();
      super.doStop();
   }

   public boolean idInUse(String id) {
      synchronized(this) {
         return this._sessions.containsKey(id);
      }
   }

   public void addSession(HttpSession session) {
      String id = this.getClusterId(session.getId());
      WeakReference<HttpSession> ref = new WeakReference(session);
      synchronized(this) {
         Set<WeakReference<HttpSession>> sessions = (Set)this._sessions.get(id);
         if (sessions == null) {
            sessions = new HashSet();
            this._sessions.put(id, sessions);
         }

         sessions.add(ref);
      }
   }

   public void removeSession(HttpSession session) {
      String id = this.getClusterId(session.getId());
      synchronized(this) {
         Collection<WeakReference<HttpSession>> sessions = (Collection)this._sessions.get(id);
         if (sessions != null) {
            Iterator<WeakReference<HttpSession>> iter = sessions.iterator();

            while(iter.hasNext()) {
               WeakReference<HttpSession> ref = (WeakReference)iter.next();
               HttpSession s = (HttpSession)ref.get();
               if (s == null) {
                  iter.remove();
               } else if (s == session) {
                  iter.remove();
                  break;
               }
            }

            if (sessions.isEmpty()) {
               this._sessions.remove(id);
            }
         }

      }
   }

   public void invalidateAll(String id) {
      Collection<WeakReference<HttpSession>> sessions;
      synchronized(this) {
         sessions = (Collection)this._sessions.remove(id);
      }

      if (sessions != null) {
         for(WeakReference<HttpSession> ref : sessions) {
            AbstractSession session = (AbstractSession)ref.get();
            if (session != null && session.isValid()) {
               session.invalidate();
            }
         }

         sessions.clear();
      }

   }

   public void renewSessionId(String oldClusterId, String oldNodeId, HttpServletRequest request) {
      String newClusterId = this.newSessionId((long)request.hashCode());
      synchronized(this) {
         Set<WeakReference<HttpSession>> sessions = (Set)this._sessions.remove(oldClusterId);
         if (sessions != null) {
            for(WeakReference<HttpSession> ref : sessions) {
               HttpSession s = (HttpSession)ref.get();
               if (s != null && s instanceof AbstractSession) {
                  AbstractSession abstractSession = (AbstractSession)s;
                  abstractSession.getSessionManager().renewSessionId(oldClusterId, oldNodeId, newClusterId, this.getNodeId(newClusterId, request));
               }
            }

            this._sessions.put(newClusterId, sessions);
         }

      }
   }
}
