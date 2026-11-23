package org.apache.wicket.util.watch;

import java.util.Set;
import org.apache.wicket.util.listener.IChangeListener;
import org.apache.wicket.util.time.Duration;

public interface IModificationWatcher {
   boolean add(IModifiable var1, IChangeListener<IModifiable> var2);

   IModifiable remove(IModifiable var1);

   void start(Duration var1);

   void destroy();

   Set<IModifiable> getEntries();
}
