package org.apache.manifoldcf.core.threadcontext;

import java.util.Hashtable;
import org.apache.manifoldcf.core.interfaces.IThreadContext;

public class ThreadContext implements IThreadContext {
   public static final String _rcsid = "@(#)$Id: ThreadContext.java 988245 2010-08-23 18:39:35Z kwright $";
   protected Hashtable hashtable = new Hashtable();

   public void save(Object key, Object object) {
      if (object == null) {
         this.hashtable.remove(key);
      } else {
         this.hashtable.put(key, object);
      }

   }

   public Object get(Object key) {
      return this.hashtable.get(key);
   }
}
