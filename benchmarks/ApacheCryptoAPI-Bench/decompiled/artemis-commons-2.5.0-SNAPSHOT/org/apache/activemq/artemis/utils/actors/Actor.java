package org.apache.activemq.artemis.utils.actors;

import java.util.concurrent.Executor;

public class Actor<T> extends ProcessorBase<T> {
   private final ActorListener<T> listener;

   public Actor(Executor parent, ActorListener<T> listener) {
      super(parent);
      this.listener = listener;
   }

   protected final void doTask(T task) {
      this.listener.onMessage(task);
   }

   public final void act(T message) {
      this.task(message);
   }
}
