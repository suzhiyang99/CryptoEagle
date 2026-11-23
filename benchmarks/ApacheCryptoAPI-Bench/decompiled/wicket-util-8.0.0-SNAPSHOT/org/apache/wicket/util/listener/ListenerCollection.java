package org.apache.wicket.util.listener;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.wicket.util.collections.ReverseListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ListenerCollection<T> implements Serializable, Iterable<T> {
   private static final long serialVersionUID = 1L;
   private static final Logger logger = LoggerFactory.getLogger(ListenerCollection.class);
   private final List<T> listeners = new CopyOnWriteArrayList();

   public boolean add(T listener) {
      if (listener == null && !this.isAllowingNulls()) {
         return false;
      } else if (!this.isAllowingDuplicates() && this.listeners.contains(listener)) {
         return false;
      } else {
         this.listeners.add(listener);
         return true;
      }
   }

   protected void notify(INotifier<T> notifier) {
      for(T listener : this.listeners) {
         notifier.notify(listener);
      }

   }

   protected void notifyIgnoringExceptions(INotifier<T> notifier) {
      for(T listener : this.listeners) {
         try {
            notifier.notify(listener);
         } catch (Exception e) {
            logger.error("Error invoking listener: " + listener, e);
         }
      }

   }

   protected void reversedNotifyIgnoringExceptions(final INotifier<T> notifier) {
      this.reversedNotify(new INotifier<T>() {
         public void notify(T listener) {
            try {
               notifier.notify(listener);
            } catch (Exception e) {
               ListenerCollection.logger.error("Error invoking listener: " + listener, e);
            }

         }
      });
   }

   protected void reversedNotify(INotifier<T> notifier) {
      Iterator<T> it = new ReverseListIterator<T>(this.listeners);

      while(it.hasNext()) {
         T listener = (T)it.next();
         notifier.notify(listener);
      }

   }

   public void remove(T listener) {
      this.listeners.remove(listener);
   }

   protected boolean isAllowingDuplicates() {
      return true;
   }

   protected boolean isAllowingNulls() {
      return false;
   }

   public Iterator<T> iterator() {
      return this.listeners.iterator();
   }

   protected interface INotifier<T> {
      void notify(T var1);
   }
}
