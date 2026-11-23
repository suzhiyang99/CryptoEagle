package org.apache.wicket.util.listener;

public final class ChangeListenerSet<T> extends ListenerCollection<IChangeListener<T>> {
   private static final long serialVersionUID = 1L;

   public void notifyListeners(final T t) {
      this.notify(new ListenerCollection.INotifier<IChangeListener<T>>() {
         public void notify(IChangeListener<T> listener) {
            listener.onChange(t);
         }
      });
   }
}
