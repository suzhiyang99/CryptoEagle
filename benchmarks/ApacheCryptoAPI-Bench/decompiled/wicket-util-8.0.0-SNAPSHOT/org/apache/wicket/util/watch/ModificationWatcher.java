package org.apache.wicket.util.watch;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.wicket.util.lang.Generics;
import org.apache.wicket.util.listener.ChangeListenerSet;
import org.apache.wicket.util.listener.IChangeListener;
import org.apache.wicket.util.thread.ICode;
import org.apache.wicket.util.thread.Task;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModificationWatcher implements IModificationWatcher {
   private static final Logger log = LoggerFactory.getLogger(ModificationWatcher.class);
   private final ConcurrentHashMap<IModifiable, Entry> modifiableToEntry = Generics.<IModifiable, Entry>newConcurrentHashMap();
   private Task task;

   public ModificationWatcher() {
   }

   public ModificationWatcher(Duration pollFrequency) {
      this.start(pollFrequency);
   }

   public final boolean add(IModifiable modifiable, IChangeListener<IModifiable> listener) {
      Entry entry = (Entry)this.modifiableToEntry.get(modifiable);
      if (entry == null) {
         Time lastModifiedTime = modifiable.lastModifiedTime();
         if (lastModifiedTime != null) {
            Entry newEntry = new Entry();
            newEntry.modifiable = modifiable;
            newEntry.lastModifiedTime = lastModifiedTime;
            newEntry.listeners.add(listener);
            this.modifiableToEntry.putIfAbsent(modifiable, newEntry);
         } else {
            log.info("Cannot track modifications to resource '{}'", modifiable);
         }

         return true;
      } else {
         return !entry.listeners.add(listener);
      }
   }

   public IModifiable remove(IModifiable modifiable) {
      Entry entry = (Entry)this.modifiableToEntry.remove(modifiable);
      return entry != null ? entry.modifiable : null;
   }

   public void start(Duration pollFrequency) {
      this.task = new Task("ModificationWatcher");
      this.task.run(pollFrequency, new ICode() {
         public void run(Logger log) {
            ModificationWatcher.this.checkModified();
         }
      });
   }

   protected void checkModified() {
      for(Entry entry : this.modifiableToEntry.values()) {
         Time modifiableLastModified = entry.modifiable.lastModifiedTime();
         if (modifiableLastModified != null && modifiableLastModified.after(entry.lastModifiedTime)) {
            entry.listeners.notifyListeners(entry.modifiable);
            entry.lastModifiedTime = modifiableLastModified;
         }
      }

   }

   public void destroy() {
      if (this.task != null) {
         this.task.interrupt();
      }

   }

   public final Set<IModifiable> getEntries() {
      return this.modifiableToEntry.keySet();
   }

   protected static final class Entry {
      public Time lastModifiedTime;
      public final ChangeListenerSet<IModifiable> listeners = new ChangeListenerSet<IModifiable>();
      public IModifiable modifiable;
   }
}
