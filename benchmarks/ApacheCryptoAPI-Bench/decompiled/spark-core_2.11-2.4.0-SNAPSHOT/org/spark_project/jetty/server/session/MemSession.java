package org.spark_project.jetty.server.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class MemSession extends AbstractSession {
   private final Map<String, Object> _attributes = new HashMap();

   protected MemSession(AbstractSessionManager abstractSessionManager, HttpServletRequest request) {
      super(abstractSessionManager, request);
   }

   public MemSession(AbstractSessionManager abstractSessionManager, long created, long accessed, String clusterId) {
      super(abstractSessionManager, created, accessed, clusterId);
   }

   public Map<String, Object> getAttributeMap() {
      return this._attributes;
   }

   public int getAttributes() {
      synchronized(this) {
         this.checkValid();
         return this._attributes.size();
      }
   }

   public Enumeration<String> doGetAttributeNames() {
      List<String> names = (List<String>)(this._attributes == null ? Collections.EMPTY_LIST : new ArrayList(this._attributes.keySet()));
      return Collections.enumeration(names);
   }

   public Set<String> getNames() {
      synchronized(this) {
         return new HashSet(this._attributes.keySet());
      }
   }

   public void clearAttributes() {
      label41:
      while(true) {
         if (this._attributes != null && this._attributes.size() > 0) {
            ArrayList<String> keys;
            synchronized(this) {
               keys = new ArrayList(this._attributes.keySet());
            }

            Iterator<String> iter = keys.iterator();

            while(true) {
               if (!iter.hasNext()) {
                  continue label41;
               }

               String key = (String)iter.next();
               Object value;
               synchronized(this) {
                  value = this.doPutOrRemove(key, (Object)null);
               }

               this.unbindValue(key, value);
               ((AbstractSessionManager)this.getSessionManager()).doSessionAttributeListeners(this, key, value, (Object)null);
            }
         }

         if (this._attributes != null) {
            this._attributes.clear();
         }

         return;
      }
   }

   public void addAttributes(Map<String, Object> map) {
      this._attributes.putAll(map);
   }

   public Object doPutOrRemove(String name, Object value) {
      return value == null ? this._attributes.remove(name) : this._attributes.put(name, value);
   }

   public Object doGet(String name) {
      return this._attributes.get(name);
   }
}
