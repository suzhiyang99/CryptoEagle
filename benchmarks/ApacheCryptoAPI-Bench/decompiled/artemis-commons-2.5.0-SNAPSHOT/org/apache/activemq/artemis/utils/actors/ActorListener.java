package org.apache.activemq.artemis.utils.actors;

public interface ActorListener<T> {
   void onMessage(T var1);
}
