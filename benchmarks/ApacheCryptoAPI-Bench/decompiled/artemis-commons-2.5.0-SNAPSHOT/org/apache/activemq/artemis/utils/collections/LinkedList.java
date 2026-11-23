package org.apache.activemq.artemis.utils.collections;

public interface LinkedList<E> {
   void addHead(E var1);

   void addTail(E var1);

   E poll();

   LinkedListIterator<E> iterator();

   void clear();

   int size();
}
